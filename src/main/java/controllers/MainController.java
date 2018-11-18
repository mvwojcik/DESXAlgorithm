package controllers;

import algorithm.DESX;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;

public class MainController {
    @FXML
    private ListView listview;

    private DESX desx;
    private File file;
    private String path;
    private int size;
    private String extension;

    @FXML
    public void initialize() {

    }

    @FXML
    void buttonAE() {
        openFileChooser();
    }

    @FXML
    public void encrypt() {
        desx = new DESX();
        this.path = getItem();
        this.file = new File(path);
        this.desx.start(this.saveAsBytesArray(), size, true);
        try {
            this.saveFile(desx, "encrypted."+extension);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void saveFile(DESX zmienna, String nazwa) throws IOException {
        byte[] temp = zmienna.getBytes();
        FileOutputStream stream = new FileOutputStream(new File((System.getProperty("user.dir"))+"\\test\\"+nazwa));
        try {
            stream.write(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stream.close();
        }
    }


    @FXML
    public void decrypt() {
        desx = new DESX();
        this.path = getItem();
        this.file = new File(path);
        this.desx.start(this.saveAsBytesArray(), size, false);

        try {
            this.saveFile(desx, "decrypted."+extension);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void openFileChooser() {
        FileChooser fc = new FileChooser();  //Inicjalizacja fc
        fc.setInitialDirectory(new File((System.getProperty("user.dir"))+"\\test"));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("txt", "*.txt"), new FileChooser.ExtensionFilter("jpg", "*.jpg"));

        File selectedFile = fc.showOpenDialog(null); //przypisz wybrany w fc plik do selectedFile
        fileIsNull(selectedFile,fc);
    }

    private void fileIsNull(File selectedFile,FileChooser fc) {
        if (selectedFile != null) {
            listview.getItems().add(selectedFile.getAbsolutePath()); //dodaje wybrany plik do listyitemów
            this.extension = fc.getSelectedExtensionFilter().getDescription();

        } else {
            System.out.println("file is not valid");
        }
    }

    private String getItem() {
        String absolutePath = (String) listview.getSelectionModel().getSelectedItem();
        if (absolutePath == null) {
            absolutePath = (String) listview.getItems().get(0);
        }
        System.out.println(absolutePath);
        return absolutePath;
    }

    public byte[] saveAsBytesArray() {
        if (file != null) {
            byte[] fileContent = null;
            try {
                fileContent = Files.readAllBytes(file.toPath());

            } catch (IOException e) {
                e.printStackTrace();
            }
            this.size = (fileContent.length % 8 == 0) ? (fileContent.length / 8) : ((fileContent.length / 8) + 1);

            return fileContent;
        } else {
            System.out.println("ERROR! U HAVE TO CHOOSE FILE");
            return null;
        }
    }


}
