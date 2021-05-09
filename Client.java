import java.io.*;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Client {
     static String filename;
     static String ipAdress;
     static ArrayList<String> requests = new ArrayList<String>();
    public static void main(String[] args)   {
        ipAdress = args[0];
        Integer alpha = Integer.parseInt(args[1]);
        filename = args[2];
        try {
            parseRequests();
            sendAtPoisson(generatePoisson(alpha));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    static private void  sendAtPoisson(ArrayList<Integer> occurs) throws IOException, InterruptedException {
        InetAddress ip = InetAddress.getByName(ipAdress);
        int counter = 0;
        int poissonArrival = 0;
        while (counter < requests.size() && poissonArrival < occurs.size()) {
            int occurrences = occurs.get(poissonArrival);
            System.out.println(occurrences);
            for (int occur=0; occur < occurrences; occur++){
                if (counter < requests.size()) {
                    RequestHandler clientThread = new RequestHandler(requests.get(counter), counter, ipAdress);
                    clientThread.start();
                    clientThread.makeRequest();
                    counter++;
                }
            }
            poissonArrival++;
            if (counter < requests.size()) {
                Thread.sleep(10000);
            }
        }
    }

    private static ArrayList<Integer> generatePoisson(Integer mean){
        System.out.println(mean);
        ArrayList<Integer> distrib = new ArrayList<Integer>();
        Random random = new Random();
        for (int c=0 ; c < requests.size() ; c++){
            double limit = Math.exp(-mean), prod = random.nextDouble();
            int n = 0;
            for (n = 0; prod >= limit; n++)
                prod *= random.nextDouble();
            distrib.add(n);
        }
        return distrib;
    };




    private static void parseRequests() throws FileNotFoundException {
        File myObj = new File("src\\"+filename);
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            System.out.println(data);
            requests.add(data);
        }
    };


}
