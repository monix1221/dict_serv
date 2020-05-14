package zad1.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static zad1.utils.CommonUtils.CLIENT_LOCALHOST;
import static zad1.utils.CommonUtils.CONNECTION_TIMEOUT;
import static zad1.utils.CommonUtils.MAIN_DICTIONARY_SERVER_PORT;
import static zad1.utils.CommonUtils.log;

public class DictClient {

    private final int clientWaitingPort;
    private final String lang;
    private final String wordToTranslate;

    private ServerSocket serverSocket;

    private boolean isTranslationNotYetReceived;

    private final MainGuiController mainGuiController;

    public DictClient(String lang, String wordToTranslate, int port, MainGuiController mainGuiController) {
        this.lang = lang;
        this.wordToTranslate = wordToTranslate;
        this.clientWaitingPort = port;
        this.mainGuiController = mainGuiController;
    }

    public void translate() throws IOException {
        // establish server socket on port given by user
        this.serverSocket = new ServerSocket(clientWaitingPort);
        this.serverSocket.setSoTimeout(CONNECTION_TIMEOUT);
        isTranslationNotYetReceived = true;

        //start listening for translations on given port
        runDictionaryClient();

        //send message to main dictionary server
        try {
            Socket clientSocket = new Socket(CLIENT_LOCALHOST, MAIN_DICTIONARY_SERVER_PORT);
            clientSocket.setSoTimeout(CONNECTION_TIMEOUT);

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            objectOutputStream.writeObject(String.format("%s,%s,%d", this.lang, this.wordToTranslate, this.clientWaitingPort));
            objectOutputStream.flush();

            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runDictionaryClient() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (isTranslationNotYetReceived) {
                    try (Socket socket = serverSocket.accept();
                         ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {

                        String receivedTranslationFromDictServer = (String) objectInputStream.readObject();

                        log("Translation received: " + receivedTranslationFromDictServer);

                        passTranslationToGuiController(receivedTranslationFromDictServer);

                    } catch (IOException e) {
                        //e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    closePort();
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void passTranslationToGuiController(String message) {
        mainGuiController.setTranslation(message);
    }

    private void closePort() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isTranslationNotYetReceived = false;
    }

}
