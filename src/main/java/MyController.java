import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javafx.scene.text.Text;


public class MyController implements Initializable{
    private void showStatusMassage(){
        Thread statusMassageWatcherThread = new Thread(()->{
            while (true){
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //
                /*
                try {
                    if(NppuConnect.getNppuConnect() != null && NppuConnect.getNppuConnect().isConnected()){
                        switch (NppuConnect.getNppuConnect().getModemChanel()){
                            case 1:
                                m1Button.setStyle("-fx-background-color: #50C878");
                                m2Button.setStyle("-fx-background-color: #DDDDDD");
                                break;
                            case 2:
                                m1Button.setStyle("-fx-background-color: #DDDDDD");
                                m2Button.setStyle("-fx-background-color: #50C878");
                                break;
                        }
                    }else{
                        m1Button.setStyle("-fx-background-color: #DDDDDD");
                        m2Button.setStyle("-fx-background-color: #DDDDDD");
                    }
                }catch (Exception e){
                    // e.printStackTrace();
                }
                
                 */

            }
        });
        statusMassageWatcherThread.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showStatusMassage();
    }
}
