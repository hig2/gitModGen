import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicLong;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;


public class MyController implements Initializable{
    private long timerDelay = 0;
    private boolean firstStartFlag = false;

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

                        if(!firstStartFlag){
                            if(SerialPortConnect.isConnected() && SerialPortConnect.isExchangeFlag()){
                                timerDelay = System.currentTimeMillis();
                                firstStartFlag = true;
                            }else{
                                timerDelay = System.currentTimeMillis();
                            }
                        }

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
    private TextField freqInput;

    @FXML
    private Label powInputLabel;

    @FXML
    private Spinner<Integer> powerInput;

    @FXML
    private Label powPar;

    @FXML
    private Label powToWatsPar;

    @FXML
    private Label frqParInput;

    @FXML
    private Label powParInput;

    @FXML
    private Label powInputDbmToWats;

    @FXML
    private Label modeLabel;

    @FXML
    private Label rssiLabel;

    @FXML
    private Label rssiStatus;

    @FXML
    private Label statusLabel;

    @FXML
    private Label conLabel;



    @FXML
    private RadioButton genRadioButton;

    @FXML
    private RadioButton rssiRadioButton;

    @FXML
    private ToggleGroup modeGroup;




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
            //???????????? ???? ???????????? ????????????????
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
            System.out.println("????????????????????.");
            SerialPortConnect.close();
        }else{
            System.out.println("??????????????????????: " + comPortsList.getValue());
            try {
                SerialPortConnect.connecting(comPortsList.getValue());
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void applyButtonOnAction(ActionEvent actionEvent){
        SerialPortConnect.setQueueMessage(ModemPostman.createMessageSetFrequency(Integer.parseInt(freqInput.getText())));
        SerialPortConnect.setQueueMessage(ModemPostman.createMessageSetPower(powerInput.getValue()));
    }

    private void showStatusConnectButton(){
        if(SerialPortConnect.isConnected()){
            connectButton.setText("??????????????????????");
        }else{
            connectButton.setText("????????????????????????");
        }
    }

    public void startAndStopOnAction(ActionEvent actionEvent){

        if(ModemPostman.getStatusModem() == 0){
            SerialPortConnect.startModem();
        }else if(ModemPostman.getStatusModem() == 1){
            SerialPortConnect.stopModem();
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
        startAndStopButton.setDisable(flagDisableGeneralWindow);
        powParInput.setDisable(flagDisableGeneralWindow);
        frqParInput.setDisable(flagDisableGeneralWindow);
        powInputDbmToWats.setDisable(flagDisableGeneralWindow);
        rssiLabel.setDisable(flagDisableGeneralWindow);
        rssiStatus.setDisable(flagDisableGeneralWindow);
        statusLabel.setDisable(flagDisableGeneralWindow);
        conLabel.setDisable(flagDisableGeneralWindow);


        showPar();
        showInputInfo();
        showApplyButton();
        showStatusStartAndStopButton();
        showPowInputDbmToWats();
        showMode();
        getRadioButton();
    }

    private void showPar(){
        // ?????? ???? ?????????? ???????????? ???????????????? ???? ??????????
        String freq;
        String pow;
        String rssi;

        if(SerialPortConnect.isConnected() && (SerialPortConnect.isExchangeFlag() || ModemPostman.getStatusModem() == 1)){
           freq = ModemPostman.getFrequencyModem() + " ??????";
           pow = ModemPostman.getPowerModem() + " dBm" + "(" + ModemPostman.dbmTomWtt(ModemPostman.getPowerModem()) + "??????)";
           // ?????????? ?????????? ???????????????? ???? ?????????? ????????????
        }else{
            freq = "n/a";
            pow = "n/a dBm (n/a ??????)";
        }

        //?????????? ?? rssi ????????????
        if(ModemPostman.getStatusModem() == 1 && ModemPostman.getJobMode() == 1){
            rssi = String.valueOf(ModemPostman.getRssiModem()) + " dBm";
        }else{
            rssi = "n/a";
        }



        freqStatus.setText(freq);
        powStatus.setText(pow);
        rssiStatus.setText(rssi);
    }

    private void showInputInfo(){

        if((System.currentTimeMillis() - timerDelay) > 300){
            if(SerialPortConnect.isConnected() && SerialPortConnect.isExchangeFlag()){

                if(freqInput.getText().equals("") ){
                    freqInput.setText(String.valueOf(ModemPostman.getFrequencyModem()));
                }

                if(powerInput.getValue() == null ){
                    SpinnerValueFactory<Integer> valuePowFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(ModemPostman.MIN_POWER_VAlUE, ModemPostman.MAX_POWER_VAlUE, ModemPostman.getPowerModem());
                    powerInput.setValueFactory(valuePowFactory);
                }
            }
        }
    }
    private void showApplyButton(){
        //???????????????? ?????? ???????? ??????????????????????, ??????????, ?? ???? ???????????????? ????????????????
        if(SerialPortConnect.isConnected() && SerialPortConnect.isExchangeFlag() && ModemPostman.getStatusModem() == 0){
            applyButton.setDisable(false);
        }else{
            applyButton.setDisable(true);
        }
    }

    private void showStatusStartAndStopButton(){
        if(ModemPostman.getStatusModem() == 0){
            startAndStopButton.setText("????????????");
        }else {
            startAndStopButton.setText("????????????????????");
        }
    }

    private void showPowInputDbmToWats(){
        if(powerInput.getValue() != null){
            int value = powerInput.getValue();
            if(SerialPortConnect.isConnected()){
                powInputDbmToWats.setText("(" + ModemPostman.dbmTomWtt(value) + " ??????)");
            }else{
                powInputDbmToWats.setText("(n/a ??????)");
            }
        }
    }

    private void showMode(){
        boolean status = !(SerialPortConnect.isConnected() && SerialPortConnect.isExchangeFlag() && ModemPostman.getStatusModem() == 0);
        modeLabel.setDisable(status);
        genRadioButton.setDisable(status);
        rssiRadioButton.setDisable(status);
    }

    private void getRadioButton(){
        RadioButton selectedRadioButton = (RadioButton) modeGroup.getSelectedToggle();
        String valueMode = selectedRadioButton.getText();

        switch (valueMode){
            case "??????????????????" :
                ModemPostman.setJobMode(0);
                break;
            case "?????????????????? Rssi" :
                ModemPostman.setJobMode(1);
                break;
        }
    }
}

