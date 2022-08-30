import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;

public class ModemPostman {
    private static ModemPostman modemPostman ;
    public static final int MAX_POWER_VAlUE = 22;
    public static final int MIN_POWER_VAlUE = -3;
    public static final int MAX_FREQUENCY_VAlUE = 1000 * 1_000_000;
    public static final int MIN_FREQUENCY_VAlUE = 100 * 1_000_000;
    public static final String MESSAGE_SYMBOL = "a";
    public static final int MESSAGE_POST_DELAY = 5;
    public static final int MESSAGE_READ_DELAY = 100;

    private static int powerModem;
    private static int frequencyModem = 0;
    public static int statusModem;
    private static int rssiModem;
    private static int snModem;


    private ModemPostman(){

    }

    public static boolean initModem() throws IOException, InterruptedException {
        if(modemPostman == null){
            modemPostman = new ModemPostman();
            return true;
        }
        return false;
    }


    public static boolean parseBuffer(char[] message){
        System.out.println(String.valueOf(message));
        char[] keyArray =  "+Val,".toCharArray();

        char[] secondCharArray = new char[message.length];
        String resultString = "";
        boolean flagRec = false;

        //проверка на мнимальный пакет
        if(keyArray.length + 4 > message.length){
            System.err.println("Ошибка парсера: длина посылки ("+ message.length + " - символа) меньше минимальной");
            return false;
        }

        for(int i = 0; i < message.length; i++){
            //выполняем совподение на ключ
            try {
                if(message[i] == keyArray[0]){
                    for(int e = 0; e < keyArray.length; e++){
                        if(keyArray[e] != message[i + e]){
                            break;
                        }

                        if(e == (keyArray.length - 1)){
                            flagRec = true;
                            i = i + e;
                        }
                    }
                }
                //терминальное условие + получение строки для дальнейшего парсинга
                if (flagRec && message[i] == '#'){
                    char[] resultArray = new char[i - keyArray.length ];

                    for(int n = 0; n < resultArray.length; n ++){
                        resultArray[n] = secondCharArray[n + keyArray.length];
                    }

                    resultString = String.valueOf(resultArray);

                    break;
                }
            }catch (Exception e){
                System.err.println("Ошибка парсера: ошибка обработки");
                return false;
            }


            //начинаем писать
            if(flagRec){
                secondCharArray[i] = message[i];
            }

            if(i == message.length - 1){
                //ненайден терминальный символ
                return  false;
            }
        }

        String[] valuesMessage = resultString.split(",");
        String key = " ";
        int value = 0;

        if(valuesMessage.length == 2){
            key = valuesMessage[0];
            value = Integer.parseInt(valuesMessage[1]);

        }else{
            //ошибка полученного значения
            return false;
        }

        try{
        }catch (Exception e){
            System.err.println("Ошибка парсера: не верное значение");
        }

        //записываем
        switch (key){
            case "0" :
                statusModem = value;
            break;
            case "1" :
                frequencyModem = value / 1_000_000;
            break;
            case "2" :
                powerModem = value;
            break;
            case "3" :
                rssiModem = value;
            break;
            case "4" :
                snModem = value;
            break;
        }

        return  true;
    }

    public static int getPowerModem(){
        return powerModem;
    }

    public static int getFrequencyModem() {
        return frequencyModem;
    }

    public static int getStatusModem() {
        return statusModem;
    }

    public static int getRssiModem() {
        return rssiModem;
    }

    public static int getSnModem() {
        return snModem;
    }

    public static String createMessageRequestFrequency(){
        return "+Get,1#" ;
    }
    public static String createMessageRequestStatusModem(){
        return "+Get,0#" ;
    }

    public static String createMessageRequestPower(){
        return "+Get,2#";
    }

    public static String createMessageSetFrequency(int value){
        value = value * 1_000_000;

        if(value > MAX_FREQUENCY_VAlUE){
            value = MAX_FREQUENCY_VAlUE;
        }
        if(value < MIN_FREQUENCY_VAlUE){
            value = MIN_FREQUENCY_VAlUE;
        }
        String result = "+Set,1," + value +"#";
        return result;
    }

    public static String createMessageSetPower(int value){


        if(value > MAX_POWER_VAlUE){
            value = MAX_POWER_VAlUE;
        }
        if(value < MIN_POWER_VAlUE){
            value = MIN_POWER_VAlUE;
        }
        String result = "+Set,2," + value +"#";
        return result;
    }

    public static String createMessageStatusModem(int value){
        String result = "+Set,0," + value +"#";
        return result;
    }



    public static String dbmTomWtt(double dbm){
        double mWtt = (1 * (Math.pow(10, (dbm / 10))));

        return new DecimalFormat("#0.0").format(mWtt);
    }






    /*
    private static void getTask(long delay) throws IOException, InterruptedException {
        AtomicLong t = new AtomicLong(0);
        Thread thread = new Thread(() -> {
            while (modemPostman != null) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if ((System.currentTimeMillis() - t.get()) > delay) {
                    t.set(System.currentTimeMillis());

                    //System.out.println(globalBuffer);
                }
            }
        });
        thread.start();
    }
    */


}
