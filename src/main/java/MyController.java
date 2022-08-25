import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;


public class MyController implements Initializable{
    private void startShowThread(){
        //preInits

        Thread showThread = new Thread(()->{
            while (true){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //tasks
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        showComPortsList();
                        showStatusConnectButton();
                        showGeneralWindow();
                        showPar();
                    }
                });
            }
        });
        showThread.start();
    }


    @FXML
    private Button connectButton;

    @FXML
    private Button applyButton;

    @FXML
    private Button startAndStopButton;



    @FXML
    private ComboBox<String> comPortsList;


    @FXML
    private Label textStatus;

    @FXML
    private Label connectLabelText;

    @FXML
    private Label powStatus;

    @FXML
    private Label powToWatsStatus;

    @FXML
    private Label freqLabel;

    @FXML
    private Label freqStatus;

    @FXML
    private Label frqPar;

    @FXML
    private Label powLabel;

    @FXML
    private Label freInputLabel;

    @FXML
    private Spinner freqInput;

    @FXML
    private Label powInputLabel;

    @FXML
    private Spinner powerInput;

    @FXML
    private Label powPar;

    @FXML
    private Label powToWatsPar;

    @FXML
    private Label frqParInput;

    @FXML
    private Label powParInput;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startShowThread();
    }

    private void showComPortsList (){
        if (SerialPortConnect.getSerialPortsList().size() > 0){
            connectLabelText.setDisable(false);
            comPortsList.setDisable(false);
            connectButton.setDisable(false);
            ObservableList<String> serialPortsList = FXCollections.observableArrayList(SerialPortConnect.getSerialPortsList());
            comPortsList.setItems(serialPortsList);
            //первый из списка выбираем
            if(comPortsList.getValue() == null){
                comPortsList.setValue(serialPortsList.get(0));
            }
        }else{
            connectLabelText.setDisable(true);
            comPortsList.setDisable(true);
            connectButton.setDisable(true);
            comPortsList.valueProperty().set(null);
        }

    }

    public void connectButtonOnAction(ActionEvent actionEvent) {
        if(SerialPortConnect.isConnected()){
            System.out.println("Отключение.");
            SerialPortConnect.close();
        }else{
            System.out.println("Подключение: " + comPortsList.getValue());
            try {
                SerialPortConnect.connecting(comPortsList.getValue());
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void showStatusConnectButton(){
        if(SerialPortConnect.isConnected()){
            connectButton.setText("Отключиться");
        }else{
            connectButton.setText("Подключиться");
        }
    }

    private void showGeneralWindow(){
        boolean flagDisableGeneralWindow = !SerialPortConnect.isConnected();

        freqLabel.setDisable(flagDisableGeneralWindow);
        freqStatus.setDisable(flagDisableGeneralWindow);
        powLabel.setDisable(flagDisableGeneralWindow);
        powStatus.setDisable(flagDisableGeneralWindow);
        freInputLabel.setDisable(flagDisableGeneralWindow);
        freqInput.setDisable(flagDisableGeneralWindow);
        powInputLabel.setDisable(flagDisableGeneralWindow);
        powerInput.setDisable(flagDisableGeneralWindow);
        applyButton.setDisable(flagDisableGeneralWindow);
        startAndStopButton.setDisable(flagDisableGeneralWindow);
        powParInput.setDisable(flagDisableGeneralWindow);
        frqParInput.setDisable(flagDisableGeneralWindow);

    }

    private void showPar(){
        // так же нужно делать проверку на обмен
        String freq;
        String pow ;
        if(SerialPortConnect.isConnected()){
            freq = ModemPostman.getFrequencyModem() + " МГц";
           pow = ModemPostman.getPowerModem() + " dBm" + "(" + ModemPostman.dbmTomWtt(ModemPostman.getPowerModem()) + "мВт)";
        }else{
            freq = "n/a";
            pow = "n/a dBm (n/a мВт)";
        }

        freqStatus.setText(freq);
        powStatus.setText(pow);

    }
}
