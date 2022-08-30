import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class SerialPortConnect {
    private final static int BAUD_RATE_DEFAULT = 57600;
    private static SerialPort serialPort;
    private static  ArrayList<String> serialPortsList = new ArrayList<>(0);
    private static Queue<String> taskMessageQueue = new LinkedList<>();
    private static HashSet<String> defaultMessageList = new HashSet<String>();
    private static String openPortName = "";
    private int openPortBaudRate;
    private static SerialPortConnect connect;
    private final byte[] globalBuffer = new byte[256];
    static SerialPort[] serialPorts;
    private static boolean exchangeFlag = false;


    private SerialPortConnect(String openPortName) {
        serialPort = SerialPort.getCommPort(openPortName);
        serialPort.openPort();
        serialPort.setBaudRate(BAUD_RATE_DEFAULT);
        System.out.println("Порт открыт:  " + openPortName);
        this.openPortName = openPortName;
        //disconnectWatcher();

    }

    public static boolean connecting(String openPortName) throws Exception {
        if(connect == null){
            if(getSerialPortsList().stream().anyMatch(e -> e.equals(openPortName))){
                connect = new SerialPortConnect(openPortName);
                System.out.println("Подключен");
                try {
                    setDefaultPermanentMessageList();
                    readTask(20);
                    writeTask(20);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
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
        openPortName = "";
        exchangeFlag = false;
        connect = null;
    }

    private static void readTask(long delay) throws IOException, InterruptedException {
        AtomicLong delayTimer = new AtomicLong(0);
        final AtomicLong exchangeTimer = new AtomicLong(0);
        Thread thread = new Thread(() -> {
            while (connect != null) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if ((System.currentTimeMillis() - delayTimer.get()) > delay) {
                    delayTimer.set(System.currentTimeMillis());

                    //чтение и проверка на обмен
                    if(readSerial()){
                        exchangeTimer.set(System.currentTimeMillis());
                        exchangeFlag = true;
                    }else if((System.currentTimeMillis() - exchangeTimer.get()) > 1000){
                        exchangeFlag = false;
                    }
                }
            }
        });
        thread.start();
    }

    private static void writeTask(long delay) throws IOException, InterruptedException {
        AtomicLong t = new AtomicLong(0);
        Thread thread = new Thread(() -> {
            setDefaultTask(defaultMessageList);
            while (connect != null) {
                if(ModemPostman.getStatusModem() == 0){
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if ((System.currentTimeMillis() - t.get()) > delay) {
                        t.set(System.currentTimeMillis());
                        //get
                        String taskMessage = taskMessageQueue.remove();
                        if(defaultMessageList.contains(taskMessage)){
                            taskMessageQueue.offer(taskMessage);
                        }
                        writeSerial(taskMessage);
                    }
                }else if(ModemPostman.getStatusModem() == 1){
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    writeSerial("333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333");
                }
            }
        });
        thread.start();
    }


    public static void writeSerial(String message){
        byte[] bytesMassage = message.getBytes();
        serialPort.writeBytes(bytesMassage, bytesMassage.length);
    }

    public static boolean isExchangeFlag(){
        return exchangeFlag;
    }


    private static boolean readSerial(){
        AtomicLong t = new AtomicLong(0);
        while (serialPort.bytesAvailable() > 0) {
            byte[] readBuffer = new byte[serialPort.bytesAvailable()];
            int numRead = serialPort.readBytes(readBuffer, readBuffer.length);
            if(numRead <= 0){
                return false;
            }
            char charArray[] = new char[numRead];

            for(int i = 0; numRead > i; i++){
                charArray[i] = (char)readBuffer[i];
            }

            //String message = new String(charArray);

            //отслеживаем обмен и записываем данные в ModemPostman
            return  ModemPostman.parseBuffer(charArray);

        }
        return false;
    }

    private static void setDefaultTask(HashSet taskList){
        for(Object e : taskList){
            taskMessageQueue.offer((String) e);
        }
    }

    public static void setQueueMessage(String message){
        System.out.println(message);
        taskMessageQueue.add(message);
    }

    private static void setDefaultPermanentMessageList(){
        defaultMessageList.add(ModemPostman.createMessageRequestFrequency());
        defaultMessageList.add(ModemPostman.createMessageRequestPower());
        defaultMessageList.add(ModemPostman.createMessageRequestStatusModem());
    }

}
