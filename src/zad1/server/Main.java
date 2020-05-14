package zad1.server;

import zad1.utils.NotSupportedLanguageException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, NotSupportedLanguageException {

        MainServer dictionaryServer = new MainServer();
        dictionaryServer.start();
    }

}
