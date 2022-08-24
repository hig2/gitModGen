import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;


public class MyController implements Initializable{




    private void startShowThread(){
        //preInits


        Thread statusMassageWatcherThread = new Thread(()->{
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
                        setConnectButtonShowStatus();
                    }
                });
            }
        });
        statusMassageWatcherThread.start();
    }

    @FXML
    private Button refreshComPortsListButton;

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
            //comPortsList.setValue(serialPortsList.get(0));
        }else{
            connectLabelText.setDisable(true);
            comPortsList.setDisable(true);
            connectButton.setDisable(true);
            comPortsList.valueProperty().set(null);
        }

    }

    public void refreshComPortsListButtonOnAction(ActionEvent event) {
        System.out.println("обновляем список");
        showComPortsList();
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

    public void setConnectButtonShowStatus(){
        if(SerialPortConnect.isConnected()){
            connectButton.setText("Отключиться");
        }else{
            connectButton.setText("Подключиться");
        }
    }
}
