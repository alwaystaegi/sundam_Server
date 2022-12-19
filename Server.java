import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Server {
    ServerSocket ss = null;
    static ArrayList<Client> clients = new ArrayList<Client>();
    static Server server = new Server();
    static Client c;
    public static void main(String[] args) {
        Socket socket=null;
        Timer timer=new Timer();
        try {
            server.ss = new ServerSocket(55555);
            System.out.println("서버소켓이 정상적으로 생성되었습니다.....");

            while(true) {
                socket = server.ss.accept();

                Client c = new Client(socket);
                clients.add(c);
                c.start();

            }






        }catch(SocketException e) {
            System.out.println("소켓관련 예외가 발생했습니다...");
            System.out.println(e);
        }catch(IOException e) {
            System.out.println("입출력 관련 예외가 발생했습니다...");
        }
    }

}
