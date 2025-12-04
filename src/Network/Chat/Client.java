package Network.Chat;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import static Ex.Mylog.log;

public class Client {
    private static final int PORT = 12345;

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        log("클라이언트 시작");

        try(

                Socket socket = new Socket("localhost",PORT);
                DataInputStream input = new DataInputStream(socket.getInputStream());
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                ){
        log("소켓연결: "+socket);

            SwitchFuction switchFuction = new SwitchFuction(input,output, socket);
            ReceiveMessage receiveMessage = new ReceiveMessage(input,socket,switchFuction);
            Thread thread1 = new Thread(receiveMessage);
            thread1.start();
            Thread thread = new Thread(switchFuction);
            thread.start();
            thread.join();
            log("Client 스레드 종료");

    }
catch (ConnectException e){
   log("연결실패");
}
}
}
