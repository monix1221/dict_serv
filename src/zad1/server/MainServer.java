package zad1.server;

import zad1.utils.CommonUtils;
import zad1.utils.DictUtils;
import zad1.utils.NotSupportedLanguageException;
import zad1.utils.NullMessageException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static zad1.utils.CommonUtils.MAIN_DICTIONARY_SERVER_PORT;
import static zad1.utils.CommonUtils.log;

public class MainServer {

    private static final String CLIENT_LOCALHOST = CommonUtils.CLIENT_LOCALHOST;

    private final ServerSocket serverSocket;

    private final Map<String, Integer> dictionaryServerPortsByLangs = new HashMap<>();

    public MainServer() throws IOException {
        serverSocket = new ServerSocket(MAIN_DICTIONARY_SERVER_PORT);
    }

    void start() throws IOException, NotSupportedLanguageException {
        runMainServer();

        List<String> supportedLanguages = DictUtils.listSupportedLanguages();
        List<DictionaryServer> dictionaryServers = createDictionaryServers(supportedLanguages);

        runDictionaryServers(dictionaryServers);
    }

    private List<DictionaryServer> createDictionaryServers(List<String> supportedLanguages)
            throws IOException, NotSupportedLanguageException {

        List<DictionaryServer> dictionaryServers = new LinkedList<>();

        for (String supportedLang : supportedLanguages) {
            Map<String, String> translations = DictUtils.getTranslationsForGivenLang(supportedLang);
            DictionaryServer dictionaryServer = new DictionaryServer(supportedLang, translations);
            dictionaryServers.add(dictionaryServer);
        }

        return dictionaryServers;
    }

    private void runDictionaryServers(List<DictionaryServer> dictionaryServers) {
        for (DictionaryServer dictServer : dictionaryServers) {
            dictServer.runServer();
            dictionaryServerPortsByLangs.put(dictServer.getLang(), dictServer.getDictServerPort());
        }
    }

    private void runMainServer() {
        //start MainDictionaryServer
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    log("Main Dictionary Server successfully started");
                    try (Socket socket = serverSocket.accept()) {

                        String messageFromGuiClient = receiveMessageFromGuiClient(socket);

                        log("Received message from GuiClient:" + messageFromGuiClient);

                        sendMessageToDictionaryServer(messageFromGuiClient);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void sendMessageToDictionaryServer(String messageFromGuiClient) throws NullMessageException {
        if (messageFromGuiClient == null) {
            throw new NullMessageException("Message from client is null!");
        }

        String language = messageFromGuiClient.split(",")[0];

        String wordToTranslate = messageFromGuiClient.split(",")[1];

        int portOnWhichClientWaits = Integer.parseInt(messageFromGuiClient.split(",")[2]);

        try (Socket dictSocket = new Socket(CLIENT_LOCALHOST, dictionaryServerPortsByLangs.get(language));
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(dictSocket.getOutputStream())) {

            objectOutputStream.writeObject(String.format("%s,%d", wordToTranslate, portOnWhichClientWaits));
            objectOutputStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String receiveMessageFromGuiClient(Socket socket) {
        log(getClientInfo(socket));

        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {

            return (String) objectInputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String getClientInfo(final Socket clientSocket) {
        String clientIP = clientSocket.getInetAddress().toString();
        int clientPort = clientSocket.getPort();
        return "Client IP: " + clientIP + ", client port: " + clientPort;
    }

}
