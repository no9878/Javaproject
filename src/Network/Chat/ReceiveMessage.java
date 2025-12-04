package Network.Chat;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import static Ex.Mylog.log;
import static Network.Chat.SwitchFuction.exit;

public class ReceiveMessage implements Runnable {
    private final DataInputStream input;
    private Socket socket;
    private final SwitchFuction switchFuction;

    public ReceiveMessage(DataInputStream input, Socket socket, SwitchFuction switchFuction) {
        this.input = input;
        this.socket = socket;
        this.switchFuction = switchFuction;
    }




    @Override
    public void run() {


                while (true) {

                    try {

                            String receivedname = input.readUTF();
                            String message = input.readUTF();
                            if(receivedname.equals("overlap")) {
                                switchFuction.responsequeue.offer(message);
                                continue;
                            }
                            System.out.print("\r" + " ".repeat(100));
                            System.out.print("\r");
                            if (receivedname.equals("userlist")) {
                                System.out.println(message);
                                switchFuction.responsequeue.offer("dummy");
                            }
                           else {
                                log(receivedname + " -> ALL client: " + message);
                                if (switchFuction != null)
                                    switchFuction.reprintPROMPT();
                            }

                    } catch (IOException e) {
                        if(exit) {
                            log("ReceiveMessage스레드 종료");
                            return;
                        }
                        try {

                            System.in.close();
                            socket.close();

                        } catch (IOException ex) {

                        }
                        log("ReceiveMessage스레드 종료");
                        return;
                    }

        }


}}



