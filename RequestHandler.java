import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class RequestHandler  extends Thread {
    String request;
    Integer counter;
    String ipAdress;

    public RequestHandler(String request, Integer counter, String ipAdress) {
        this.request = request;
        this.counter = counter;
        this.ipAdress = ipAdress;
    }
    public void makeRequest(){
        try {
            InetAddress ip = InetAddress.getByName(ipAdress);
            // establish the connection with server port 5056
            Socket s = new Socket(ip, 5056);
            // obtaining input and out streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            //Send request
            dos.writeUTF(request);

            String answer = dis.readUTF();
            FileWriter myWriter = new FileWriter("ans_"+counter+".txt");
            myWriter.write(request+"\n\n");
            myWriter.write(answer) ;
            myWriter.close();

            //die
        }catch(IOException e){
            e.getStackTrace();
        }
    }
}
