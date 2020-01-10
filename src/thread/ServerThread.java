package thread;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ServerThread extends Thread{
    private Socket socket = null;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run(){
        try{
            InetAddress inetAddress = socket.getInetAddress();
            System.out.println(inetAddress.getHostAddress()+"호스트 주소");

            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            printWriter  = new PrintWriter(new OutputStreamWriter(outputStream));
            bufferedReader  = new BufferedReader(new InputStreamReader(inputStream));

            String line = null;
            while((line = bufferedReader.readLine())!=null){
                printWriter.println(line);
                printWriter.flush();
            }

        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    public void close(){
        try{
            printWriter.close();
            bufferedReader.close();
            socket.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
