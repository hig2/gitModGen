import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

public class ModemPostman {
    private static ModemPostman modemPostman ;
    public static final int MAX_POWER_VAlUE = 22;
    public static final int MIN_POWER_VAlUE = -3;
    public static final int MAX_FREQUENCY_VAlUE = 150;
    public static final int MIN_FREQUENCY_VAlUE = 1000;
    public static final String MESSAGE_SYMBOL = "a";
    public static final int MESSAGE_POST_DELAY = 5;
    public static final int MESSAGE_READ_DELAY = 100;

    private static int powerModem;
    private static int frequencyModem;
    private static int statusModem ;
    private static int rssiModem ;
    private static int snModem ;


    private ModemPostman(){

    }

    public static boolean initModem() throws IOException, InterruptedException {
        if(modemPostman == null){
            modemPostman = new ModemPostman();
            return true;
        }
        return false;
    }


    public static void parseBuffer(String message){
        String[] valuesMessage = message.split(",");
        System.out.println(Integer.parseInt(valuesMessage[2].trim()));
        for (String word : valuesMessage) {
            if (word.matches("\\+Val")) {
                //записываем
                System.out.println(message);
                switch (valuesMessage[1]){
                    case "0" :
                        statusModem = Integer.parseInt(valuesMessage[2].trim());
                    break;
                    case "1" :
                        frequencyModem = Integer.parseInt(valuesMessage[2].trim()) / 1000;
                    break;
                    case "2" :
                        powerModem = Integer.parseInt(valuesMessage[2].trim());
                    break;
                    case "3" :
                        rssiModem = Integer.parseInt(valuesMessage[2].trim());
                    break;
                    case "4" :
                        snModem = Integer.parseInt(valuesMessage[2].trim());
                    break;
                }
            }
        }
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
