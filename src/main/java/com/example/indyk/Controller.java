package com.example.indyk;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Controller {
    @FXML
    private TextField keyField;
    @FXML
    private Label fullAlphabetLabel;
    @FXML
    private Label shiftAlphabetLabel;
    @FXML
    private Label openedFileLabel;
    @FXML
    private Label fileInfoLabel;
    @FXML
    private String getTextFromTextField() {return keyField.getText();}
    public Stage stage;
    public ExtensionFilter extxt = new ExtensionFilter("Text files", "*.txt");
    public static final String latinAlphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String cyrillicAlphabet = "абвгґдеєжзиіїйклмнопрстуфхцчшщьюяАБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ";
    public static final String numbers = "1234567890";
    public static final String nonAlphaNumeric = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
    public static final String fullAlphabet = latinAlphabet + cyrillicAlphabet + numbers + nonAlphaNumeric;
    public static File openedFile;
    public static int encryptionKey;

    public static void encryptFile(File inputFile, int offset) {
        String outputFileName = generateOutputFileName(inputFile.getName(), true);
        File outputFile = new File(inputFile.getParent(), outputFileName);

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String shiftedAlphabet = shiftAlphabet(fullAlphabet, offset);

            int currentChar;
            while ((currentChar = reader.read()) != -1) {
                char originalChar = (char) currentChar;

                int index = fullAlphabet.indexOf(originalChar);
                if (index != -1) {
                    char encryptedChar = shiftedAlphabet.charAt(index);
                    writer.write(encryptedChar);
                } else {
                    writer.write(originalChar);
                }
            }

            showInfoAlert("Encrypted file saved to:\n" + outputFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "An error occurred during encryption.");
        }
    }
    public static void decryptFile(File inputFile, int offset) {
        String outputFileName = generateOutputFileName(inputFile.getName(), false);
        File outputFile = new File(inputFile.getParent(), outputFileName);

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String shiftedAlphabet = shiftAlphabet(fullAlphabet, -offset);

            int currentChar;
            while ((currentChar = reader.read()) != -1) {
                char originalChar = (char) currentChar;

                int index = fullAlphabet.indexOf(originalChar);
                if (index != -1) {
                    char decryptedChar = shiftedAlphabet.charAt(index);
                    writer.write(decryptedChar);
                } else {
                    writer.write(originalChar);
                }
            }

            showInfoAlert("Decrypted file saved to:\n" + outputFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "An error occurred during decryption.");
        }
    }
    private static String shiftAlphabet(String fullAlphabet, int offset) {
        int length = fullAlphabet.length();
        offset = (offset % length + length) % length;
        return fullAlphabet.substring(offset) + fullAlphabet.substring(0, offset);
    }
    private static String generateOutputFileName(String inputFileName, Boolean selector) {
        int dotIndex = inputFileName.lastIndexOf('.');
        String nameWithoutExtension, extension;

        boolean isEncrypted = inputFileName.contains("_encrypted");

        if (dotIndex == -1) {
            nameWithoutExtension = inputFileName;
            extension = "";
        } else {
            nameWithoutExtension = inputFileName.substring(0, dotIndex);
            extension = inputFileName.substring(dotIndex);
        }

        if (selector) {
            if (isEncrypted) {
                nameWithoutExtension = nameWithoutExtension.replace("_encrypted", "");
            }
            return nameWithoutExtension + "_encrypted" + extension;
        } else {
            if (isEncrypted) {
                nameWithoutExtension = nameWithoutExtension.replace("_encrypted", "");
            }
            return nameWithoutExtension + "_decrypted" + extension;
        }
    }
    private static void showInfoAlert(String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Encryption completed");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void showWarningAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private static void showErrorAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public void onEncryptButtonClick(ActionEvent event) throws IOException {
        try {
            String keyText = getTextFromTextField();
            if (keyText.isEmpty()) {
                showWarningAlert("Key is empty", "Please enter an encryption key.");
                return;
            }
            encryptionKey = Integer.parseInt(keyText);
            if (openedFile != null) {
                encryptFile(openedFile, Integer.parseInt(keyText));
            } else {
                showWarningAlert("No file selected", "Please select a file to encrypt.");
            }

        } catch (NumberFormatException e) {
            showErrorAlert("Invalid key", "Please enter a valid integer for the encryption key.");
        }
    }
    public void onDecryptButtonClick(ActionEvent event) throws IOException {
        try {
            String keyText = getTextFromTextField();
            if (keyText.isEmpty()) {
                showWarningAlert("Key is empty", "Please enter a decryption key.");
                return;
            }
            encryptionKey = Integer.parseInt(keyText);
            if (openedFile != null) {
                decryptFile(openedFile, Integer.parseInt(keyText));
            } else {
                showWarningAlert("No file selected", "Please select a file to decrypt.");
            }

        } catch (NumberFormatException e) {
            showErrorAlert("Invalid key", "Please enter a valid integer for the decryption key.");
        }
    }
    public void onOpenFileButtonClick() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(extxt);
        fileChooser.setInitialDirectory(new File("H:/stud/4course/BtaZPiD/KR/txtfiles"));
        openedFile = fileChooser.showOpenDialog(stage);
        if (openedFile != null) {
            openedFileLabel.setText(openedFile.getAbsolutePath());

            long fileSize = openedFile.length();
            long charCount = Files.lines(openedFile.toPath(), StandardCharsets.UTF_8)
                    .flatMapToInt(CharSequence::chars)
                    .count();
            if (fileSize < 1024) {
                fileInfoLabel.setText(fileSize + " bytes | " + charCount + " symbols");
            } else if (fileSize < 1024 * 1024) {
                double sizeInKB = (double) fileSize / 1024;
                fileInfoLabel.setText(String.format("%.2f KB | %d symbols", sizeInKB, charCount));
            } else {
                double sizeInMB = (double) fileSize / (1024 * 1024);
                fileInfoLabel.setText(String.format("%.2f MB | %d symbols", sizeInMB, charCount));
            }
        }
    }
    public void KeyFieldTextChanged() throws IOException {
        encryptionKey = Integer.parseInt(keyField.getText());
        String fullAlphabet4symbols = fullAlphabet.substring(0, Math.min(fullAlphabet.length(), 4));
        String shiftAlphabet4symbols = shiftAlphabet(fullAlphabet, encryptionKey).substring(0, Math.min(shiftAlphabet(fullAlphabet, encryptionKey).length(), 4));
        fullAlphabetLabel.setText(fullAlphabet4symbols);
        shiftAlphabetLabel.setText(shiftAlphabet4symbols);
    }
    public void onPlusButtonClick(ActionEvent event) throws IOException{
        encryptionKey = Integer.parseInt(keyField.getText()) + 1;
        keyField.setText(String.valueOf(encryptionKey));
        String fullAlphabet4symbols = fullAlphabet.substring(0, Math.min(fullAlphabet.length(), 4));
        String shiftAlphabet4symbols = shiftAlphabet(fullAlphabet, encryptionKey).substring(0, Math.min(shiftAlphabet(fullAlphabet, encryptionKey).length(), 4));
        fullAlphabetLabel.setText(fullAlphabet4symbols);
        shiftAlphabetLabel.setText(shiftAlphabet4symbols);
    }
    public void onMinusButtonClick(ActionEvent event) throws IOException{
        encryptionKey = Integer.parseInt(keyField.getText()) - 1;
        keyField.setText(String.valueOf(encryptionKey));
        String fullAlphabet4symbols = fullAlphabet.substring(0, Math.min(fullAlphabet.length(), 4));
        String shiftAlphabet4symbols = shiftAlphabet(fullAlphabet, encryptionKey).substring(0, Math.min(shiftAlphabet(fullAlphabet, encryptionKey).length(), 4));
        fullAlphabetLabel.setText(fullAlphabet4symbols);
        shiftAlphabetLabel.setText(shiftAlphabet4symbols);
    }
}