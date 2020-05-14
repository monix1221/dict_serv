package zad1.server;

import zad1.utils.CommonUtils;
import zad1.utils.NullMessageException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import static zad1.utils.CommonUtils.log;

public class DictionaryServer {

    private static final String CLIENT_LOCALHOST = CommonUtils.CLIENT_LOCALHOST;

    private final String supportedLang;
    private final Map<String, String> translations;

    private ServerSocket dictServerSocket;

    public DictionaryServer(String supportedLang, Map<String, String> translations) throws IOException {
        this.supportedLang = supportedLang;
        this.translations = translations;

        this.dictServerSocket = new ServerSocket(0);
    }

    public void runServer() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try (Socket socket = dictServerSocket.accept()) {

                        String clientMessage = receiveMessageFromMainServer(socket);

                        log("Received message from main server:" + clientMessage);

                        sendMessageToGuiClient(clientMessage);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void sendMessageToGuiClient(String messageFromMainServer) throws NullMessageException {
        if (messageFromMainServer == null) {
            throw new NullMessageException("Message from main server is null!");
        }

        String wordToTranslate = messageFromMainServer.split(",")[0];
        int clientOpenPort = Integer.parseInt(messageFromMainServer.split(",")[1]);

        try (Socket clientSocket = new Socket(CLIENT_LOCALHOST, clientOpenPort);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream())) {

            String translation = translations.get(wordToTranslate);

            objectOutputStream.writeObject(translation);
            objectOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String receiveMessageFromMainServer(Socket socket) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {

            return (String) objectInputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    String getLang() {
        return this.supportedLang;
    }

    int getDictServerPort() {
        return this.dictServerSocket.getLocalPort();
    }

}
