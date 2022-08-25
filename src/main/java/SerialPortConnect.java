import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

public class SerialPortConnect {
    private final static int BAUD_RATE_DEFAULT = 57600;
    private static SerialPort serialPort;
    private static  ArrayList<String> serialPortsList = new ArrayList<>();
    private String openPortName;
    private int openPortBaudRate;
    private static SerialPortConnect connect;
    private final byte[] globalBuffer = new byte[256];

    private SerialPortConnect(String openPortName) {
        serialPort = SerialPort.getCommPort(openPortName);
        serialPort.openPort();
        serialPort.setBaudRate(BAUD_RATE_DEFAULT);
        System.out.println("Порт открыт:  " + openPortName);
        //disconnectWatcher();

        try {
            readTask(100);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean connecting(String openPortName) throws Exception {
        if(connect == null){
            if(getSerialPortsList().stream().anyMatch(e -> e.equals(openPortName))){
                connect = new SerialPortConnect(openPortName);
                System.out.println("Подключен");
                return true;
            }else{
                throw new Exception("Порт не обнаружен");
            }
        }
        return false;
    }

    public static SerialPortConnect getConnect(){
        return connect;
    }


    public static boolean isConnected(){
        return SerialPortConnect.connect != null;
    }

    synchronized public static ArrayList<String> getSerialPortsList(){
        serialPortsList.clear();
        for(SerialPort e: SerialPort.getCommPorts()){
            if(e.toString().equals("User-Specified Port")){
                continue;
            }
            serialPortsList.add(e.getSystemPortName());
        }
        //если нет не одного порта производим дисконнект
        if(serialPortsList.size() == 0){
            close();
        }

        return serialPortsList;
    }

    public static void close(){
       if(serialPort != null && serialPort.isOpen()){
           serialPort.closePort();
       }
        connect = null;
    }

    private void readTask(long delay) throws IOException, InterruptedException {
        AtomicLong t = new AtomicLong(0);
        Thread thread = new Thread(() -> {
            while (connect != null) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if ((System.currentTimeMillis() - t.get()) > delay) {
                    t.set(System.currentTimeMillis());
                    readSerial();
                    //System.out.println(globalBuffer);
                }
            }
        });
        thread.start();
    }

    /*
    private void disconnectWatcher(){
        Thread thread = new Thread(()->{
            while (connect != null) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //если нет не одного порта производим дисконнект
            }
        });
        thread.start();
    }

     */

    private boolean readSerial(){
        while (serialPort.bytesAvailable() > 0) {
            byte[] readBuffer = new byte[serialPort.bytesAvailable()];
            int numRead = serialPort.readBytes(readBuffer, readBuffer.length);

            char charArray[] = new char[numRead];
            for(int i = 0; numRead > i; i++){
                charArray[i] = (char)readBuffer[i];
            }
            String message = new String(charArray);

            ModemPostman.parseBuffer(message);
            return true;
        }
        return false;
    }

}
