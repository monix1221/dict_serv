package zad1.client;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import zad1.utils.DictUtils;

import java.io.IOException;

import static zad1.utils.CommonUtils.MAIN_DICTIONARY_SERVER_PORT;

public class MainGuiController {

    private static final String TRANSLATION_NOT_PROVIDED_YET = "-- translation not provided yet --";
    private static final String TRANSLATION_PROMPT_TEXT = "-- translation --";

    private static final String STYLE_FOR_ERROR_TRANSLATION_MSG = "-fx-text-fill: red";
    private static final String DEFAULT_STYLE = "-fx-text-fill: black";

    private static final String EMPTY_STR = "";

    private static final int MIN_PORT = 49152;
    private static final int MAX_PORT = 65535;

    @FXML
    private ComboBox<String> langChooserComboBox;

    @FXML
    private TextField wordToTranslateTextField;

    @FXML
    private TextField portTextField;

    @FXML
    private TextField resultTranslation;

    public void initialize() {
        langChooserComboBox.getItems().clear();
        langChooserComboBox.getItems().addAll(DictUtils.listSupportedLanguages());
        langChooserComboBox.setValue(DictUtils.listSupportedLanguages().get(0));
    }

    @FXML
    private void comboBoxOnActionEvent() {
        clearResultTranslation();
    }

    @FXML
    private void translate() throws IOException {
        clearResultTranslation();

        String wordToTranslate = wordToTranslateTextField.getText();
        String portStr = portTextField.getText();

        boolean isUserInputValid = isUserInputValid(wordToTranslate, portStr);

        if (isUserInputValid) {
            String lang = this.langChooserComboBox.getValue();
            DictClient client = new DictClient(lang, wordToTranslate, Integer.parseInt(portStr), this);
            client.translate();
        } else {
            showClientErrors(wordToTranslate, portStr);
        }

        System.out.println("Translate - word: " + wordToTranslate + " - on port: " + portStr);
    }

    void setTranslation(String translatedMsg) {
        clearResultTranslation();
        if (translatedMsg == null || translatedMsg.equals(EMPTY_STR)) {
            resultTranslation.setText(TRANSLATION_NOT_PROVIDED_YET);
            resultTranslation.setStyle(STYLE_FOR_ERROR_TRANSLATION_MSG);
        } else {
            resultTranslation.setText(translatedMsg);
        }
    }

    private boolean isUserInputValid(String wordToTranslate, String portStr) {
        return isWordToTranslateValid(wordToTranslate) && isClientPortValid(portStr);
    }

    private void showClientErrors(String word, String port) {
        if (!isWordToTranslateValid(word)) {
            setErrorMsgForResultTranslation("Word to translate must not be empty!");
            return;
        }

        if (!isClientPortValid(port)) {
            setErrorMsgForResultTranslation("Port must be in range " + MIN_PORT + "-" + MAX_PORT
                    + " and not " + MAIN_DICTIONARY_SERVER_PORT);
        }
    }

    private boolean isWordToTranslateValid(String word) {
        return !word.equals(EMPTY_STR);
    }

    private boolean isClientPortValid(String port) {
        int portToInt = -1;
        try {
            portToInt = Integer.parseInt(port);
        } catch (NumberFormatException exception) {
            return false;
        }

        return portToInt != MAIN_DICTIONARY_SERVER_PORT && portToInt >= MIN_PORT && portToInt <= MAX_PORT;
    }

    private void setErrorMsgForResultTranslation(String errorMessage) {
        this.resultTranslation.setStyle(STYLE_FOR_ERROR_TRANSLATION_MSG);
        this.resultTranslation.setText(errorMessage);
    }

    private void clearResultTranslation() {
        this.resultTranslation.setStyle(DEFAULT_STYLE);
        this.resultTranslation.clear();
        this.resultTranslation.setPromptText(TRANSLATION_PROMPT_TEXT);
    }

}
