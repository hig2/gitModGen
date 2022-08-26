import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

public class ModemPostman {
    private static ModemPostman modemPostman ;
    public static final int MAX_POWER_VAlUE = 22;
    public static final int MIN_POWER_VAlUE = -3;
    public static final int MAX_FREQUENCY_VAlUE = 1000;
    public static final int MIN_FREQUENCY_VAlUE = 100;
    public static final String MESSAGE_SYMBOL = "a";
    public static final int MESSAGE_POST_DELAY = 5;
    public static final int MESSAGE_READ_DELAY = 100;

    private static int powerModem = 0;
    private static int frequencyModem = 0;
    private static int statusModem = 0;
    private static int rssiModem = 0;
    private static int snModem = 0;


    private ModemPostman(){

    }

    public static boolean initModem() throws IOException, InterruptedException {
        if(modemPostman == null){
            modemPostman = new ModemPostman();
            return true;
        }
        return false;
    }


    public static boolean parseBuffer(String message){
        String[] valuesMessage = message.split(",");
        System.out.println(message);
        String value = valuesMessage[2].substring(0, valuesMessage[2].length() - 1).trim();
        String lastSymbol = valuesMessage[2].substring(valuesMessage[2].length() - 1);
        int result = 0;
        try{
            result = Integer.parseInt(value);
        }catch (Exception e){
            System.out.println("ошибка парсера");
        }


        for (String e : valuesMessage) {
            if (e.matches("\\+Val") && lastSymbol.equals("#")) {
                //записываем
                switch (valuesMessage[1]){
                    case "0" :
                        statusModem = result;
                    break;
                    case "1" :
                        frequencyModem = result / 1_000_000;
                    break;
                    case "2" :
                        powerModem = result;
                    break;
                    case "3" :
                        rssiModem = result;
                    break;
                    case "4" :
                        snModem = result;
                    break;
                }

                return  true;
            }
        }
        return false;
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

    public static String createMessageRequestPower(){
        return "+Get,2#";
    }


    private void setModemPower(int value){
        if(value > MAX_POWER_VAlUE){
            value = MAX_POWER_VAlUE;
        }
        if(value < MIN_POWER_VAlUE){
            value = MIN_POWER_VAlUE;
        }


    }


    public static String dbmTomWtt(double dbm){
        double mWtt = (1 * (Math.pow(10, (dbm / 10))));

        return new DecimalFormat("#0").format(mWtt);
    }


    public static void clear(){
        modemPostman = null;
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
