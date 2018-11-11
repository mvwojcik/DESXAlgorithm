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
    @FXML
    private Button button;

    private DESX desx;
    private File file;
    private String path;
    private int size;

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
        this.desx.start(this.saveAsBytesArray(),size,true);
        try {
            this.saveFile(desx);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void saveFile(DESX zmienna) throws IOException {
        byte[]temp=zmienna.getBytes();
      //  File fileOutput = new File("C:\\Users\\Mateusz\\Desktop\\Studia\\2.studia\\Kryptografia\\output.txt");
        FileOutputStream stream = new FileOutputStream("C:\\Users\\Mateusz\\Desktop\\Studia\\2.studia\\Kryptografia\\output.txt");
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
        this.desx.start(this.saveAsBytesArray(),size,false);

        try {
            this.saveFile(desx);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void openFileChooser() {
        FileChooser fc = new FileChooser();  //Inicjalizacja fc
        fc.setInitialDirectory(new File("C:\\Users\\Mateusz\\Desktop\\Studia\\2.studia\\Kryptografia"));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT", "*.txt"), new FileChooser.ExtensionFilter("PNG ", "*.png"), new FileChooser.ExtensionFilter("BMP", "*.bmp"));
        File selectedFile = fc.showOpenDialog(null); //przypisz wybrany w fc plik do selectedFile
        fileIsNull(selectedFile);
    }

    private void fileIsNull(File selectedFile) {
        if (selectedFile != null) {
            listview.getItems().add(selectedFile.getAbsolutePath()); //dodaje wybrany plik do listyitemów
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
            this.size = (fileContent.length / 8) + 1;

            return fileContent;
        } else {
            System.out.println("ERROR! U HAVE TO CHOOSE FILE");
            return null;
        }
    }


}
