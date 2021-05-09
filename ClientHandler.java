import error.ClientConnectionError;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler extends Thread {
    DataInputStream bufferIn;
    DataOutputStream bufferOut;
    Socket socket;
    ArrayList<String[]> array;
    ArrayList<String> fastArray1;
    ArrayList<String> fastArray2;
    ArrayList<String> fastArray3;
    ArrayList<String> fastArray4;
    Boolean fast;
    String[] regex = new String[1];

    public ClientHandler(ArrayList<String[]> array,
                         ArrayList<String> fastArray1,ArrayList<String> fastArray2,ArrayList<String> fastArray3,
                         ArrayList<String> fastArray4, Boolean fast){
        this.array = array;
        this.fastArray1 = fastArray1;
        this.fastArray2 = fastArray2;
        this.fastArray3 = fastArray3;
        this.fastArray4 = fastArray4;
        this.fast = fast;

    }

    public void handle(Socket socket, DataInputStream bufferIn, DataOutputStream bufferOut) {
        this.socket = socket;
        this.bufferOut = bufferOut;
        this.bufferIn = bufferIn;
        long start = System.currentTimeMillis();
        try {
            waitForRegex();
            if (fast) fastResponsesMatches(); else responseMatches();
        } catch (UTFDataFormatException e) {
            try {
                System.out.println("Request too big");
                bufferOut.writeUTF("request may be too big \n\n");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            long stop = System.currentTimeMillis();
            long ellpased = stop - start;
            System.out.println("reqTime: "+ellpased);
            close();
        }
    }
    
    public void close(){
        try {
            this.bufferIn.close();
            this.bufferOut.close();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void responseMatches() throws IOException {
        String matches = "";
        Boolean all = false;
        int index = 0;
        System.out.println(regex[1]);
        if (regex[0].matches("(.*)[0-4](.*)")) {
            index = Integer.parseInt(regex[0]);
            System.out.println(index+ "matches, slow");
        }else{
            all = true;
            System.out.println("all matches, slow");
        }
        matches = arraySearch(index, matches, all, array);
        matches += "\n";
        bufferOut.writeUTF(matches);
    }

    public void fastResponsesMatches() throws IOException, UTFDataFormatException {
        String matches = "";
        System.out.println(regex[1]);
        int index = 0;
        if (regex[0].matches("(.*)[0-4](.*)")) {
            index = Integer.parseInt(regex[0]);
            System.out.println(index + " matches, fast");
            matches = fastArraySearch(chooseRightArray(index), matches);
            //matches = arraySearch(index, matches, false, array);

        } else { //all types
            System.out.println("all matches, fast");
            matches = fastArraySearch(fastArray1, matches);
            matches = fastArraySearch(fastArray2, matches);
            matches = fastArraySearch(fastArray3, matches);
            matches = fastArraySearch(fastArray4, matches);
        }
        matches += "\n";
        bufferOut.writeUTF(matches);
    }

    public ArrayList<String> chooseRightArray(int index){
        ArrayList<String> selectedArray = new ArrayList<String>();
        switch(index){
            case (1):
                return fastArray1;
            case (2):
                return fastArray2;
            case (3):
                return fastArray3;
            case (4):
                return fastArray4;
        }
        return selectedArray;
    }


    public String arraySearch(int index, String matches, boolean all, ArrayList<String[]> array) {
        for (String[] row : array) {
            int category = Integer.parseInt(row[0]);
            if (index == category || all) {
                if (row[1].matches(regex[1])) {
                    matches = matches + row[1] + "\n";
                }
            }
        }
        return matches;
    }

    public String fastArraySearch(ArrayList<String> selectedArray, String matches){
        for (String row : selectedArray) {
            if (row.matches(regex[1])) {
                matches = matches + row + "\n";
            }

        }
        return matches;
    }



            
    public void waitForRegex() throws IOException {
        regex = (bufferIn.readUTF()).split(";", 2);

    }
}