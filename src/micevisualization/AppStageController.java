package micevisualization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Scanner;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;

public class AppStageController {
    // Parker (3/2/17): access AppStage.java's stage variable during file IO
    @FXML private Stage stage;
    
    // Parker (3/2/17): access the GUI's left status label
    @FXML private Label leftStatus;
    
    // Parker (3/2/17): the fileChooser variable can be reused throughout the
    // system's event handlers, so we create a global within the controller.
    final FileChooser fileChooser = new FileChooser();
    
    // Parker (3/17/17)
    // variables for tracking if the current session is new / old and determining
    // if to prompt user to save data
    Boolean isFileLoaded = false;
    String currentFilePath = "";
    Boolean isSessionLoaded = false;
    String currentSessionPath = "";

    /* 
    Parker (3/2/17): 
    openFileAction is the event handler called when the user clicks on the 
    File -> Open menu option. Its purpose is to call the operating system's 
    file system viewer. This implementation supports Windows, Mac, and Unix
    operating systems (http://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm).
    */
    int timesConfigured = 0;
    @FXML protected void openFileAction(ActionEvent event) {
        // prevent the file chooser from being configured multiple times
        if (timesConfigured == 0) {
            configureFileChooser(fileChooser);
            timesConfigured++;
        }
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            openFile(file);
        }
    }
    
    /*
     Parker (3/2/17):
    exitApplication is called when the user exits the application, currently by
    selecting the File -> Exit menu option. Any cleanup code, if necessary, needs
    to go either here or in the AppStage.java file's stop() function.
    */
    @FXML
    public void exitApplication(ActionEvent event) {
       System.out.println("Platform is closing");
       Platform.exit();
    }
    
    /*
     Parker (3/2/17):
    configureFileChooser configures the file system viewer to have a specific title,
    initial directory, and accepted file types. This function is called using an
    instantiated FileChooser object as its parameter.
    */
    private static void configureFileChooser(
    final FileChooser fileChooser) {      
        fileChooser.setTitle("Select a data set file (CSV) or session file (JSON)");
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        );                 
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Files", "*.*"),
            new FileChooser.ExtensionFilter("CSV", "*.csv"),
            new FileChooser.ExtensionFilter("JSON", "*.json")
        );
    }
    
    /*
    Parker (3/2/17):
    openFile opens the File passed as a parameter. This class is called after
    the user picks a file from the FileChooser display in the openFileAction
    event handler.
    */
    private void openFile(File file) {
        try {
            leftStatus.setText("Opening " + file.getName() + " ...");
            /* Parker (3-15-17):
            * FileInputStream implementation below from 
            * http://www.baeldung.com/java-read-lines-large-file */
            FileInputStream inputStream = null;
            Scanner sc = null;
            // Parker (3-17-17): the try {} block below contains code that will
            // execute until the data set loading is finished. The finally {}
            // block contains code that happens after the data set has been loaded.
            try {
                inputStream = new FileInputStream(file.getPath());
                sc = new Scanner(inputStream, "UTF-8");
                long start = System.currentTimeMillis();
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    //System.out.println(line);
                }
                long end = System.currentTimeMillis();
                long elapsed = end - start;
                System.out.println("done reading file! It took " + elapsed + " milliseconds");
                // note that Scanner suppresses exceptions
                if (sc.ioException() != null) {
                    throw sc.ioException();
                }
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                    // Parker (3-17-17): Set currentFilePath and isFileLoaded:
                    currentFilePath = file.getPath();
                    isFileLoaded = true;
                    // Parker (3-17-17): If the current session is new, ask user
                    // if they would like to create a new one:
                    if (!isSessionLoaded) {
                        // Parker (3-17-17): Create a default name, consisting of the
                        // current date and time, for the potential new session:
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        String defaultSessionName = "Session " + dateFormat.format(date);
                        
                        // Parker (3-17-17): Prompt the user asking if they would like
                        // to save the currently loaded data set file within a new
                        // session:
                        TextInputDialog dialog = new TextInputDialog(defaultSessionName);
                        dialog.setTitle("Create new session?");
                        dialog.setHeaderText("Sessions save program settings and data, maintaining your current work.");
                        dialog.setContentText("Please enter a name for this session:");

                        // Traditional way to get the response value.
                        Optional<String> result = dialog.showAndWait();
                        if (result.isPresent()){
                            System.out.println("Session name: " + result.get());
                        }
                    }
                }
                if (sc != null) {
                    sc.close();
                }
            }
        } catch(Exception e) { 
            System.out.println(e); 
        }
    }
}