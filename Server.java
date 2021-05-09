import error.ClientConnectionError;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    static final String DB_DATA = "dbdata.txt";
    static ArrayList<String[]> array = new ArrayList<String[]>();
    static ArrayList<String> fastArray1 = new ArrayList<String>();
    static ArrayList<String> fastArray2 = new ArrayList<String>();
    static ArrayList<String> fastArray3 = new ArrayList<String>();
    static ArrayList<String> fastArray4 = new ArrayList<String>();
    static Boolean fast;
    public static void main(String[] args) {
        int isFast = Integer.parseInt(args[0]);
        if (isFast == 1) {
            fast = true;
        }else{
            fast = false;
        }
        if (fast) {
            fastArray1 = initFastArray(1) ;
            fastArray2 = initFastArray(2) ;
            fastArray3 = initFastArray(3) ;
            fastArray4 = initFastArray(4) ;

        } else initArray();
        listen(5056);

    }

    static void listen(int portNumber){
        System.out.println("Listening....");
        ClientHandler clientThread = new ClientHandler(array, fastArray1, fastArray2, fastArray3, fastArray4, fast);
        clientThread.start();
        try{
            ServerSocket serverSocket = new ServerSocket(portNumber);
            while(true) {
                Socket clientSocket = serverSocket.accept();
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                clientThread.handle(clientSocket, dis, dos);
            }
        } catch (Exception e){
            e.fillInStackTrace();
        }
    }
    static void initArray() {
        try {
            File myObj = new File("src\\"+DB_DATA);
            Scanner myReader = new Scanner(myObj);
            int count = 0;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] dataSplitted = splitIndexData(data);
                array.add(dataSplitted);
                count++;
            }
            System.out.println(count);
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    static ArrayList<String> initFastArray(int number) {
        ArrayList<String> fastArray = new ArrayList<String>();

        try {
            File myObj = new File("src\\"+DB_DATA);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] dataSplitted = splitIndexData(data);
                int category = Integer.parseInt(dataSplitted[0]);
                if (category == number) {
                    fastArray.add(dataSplitted[1]);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return fastArray;

    }

    static String[] splitIndexData(String data){
       String[] arrOfStr = data.split("@@@", 2);
       return arrOfStr;
   }
}