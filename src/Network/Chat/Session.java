package Network.Chat;





import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import static Ex.Mylog.log;
import static Network.V4.SocketCloseutil.closeAll;

public class Session implements Runnable{
    private final Socket socket;
    private final DataInputStream input;
    final DataOutputStream output;
    private final SessionManager sessionManager;
    private boolean closed = false;
    private String name=null;

    public Session(Socket socket, SessionManager sessionManager) throws IOException {
        this.socket = socket;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
        this.sessionManager = sessionManager;
        this.sessionManager.add(this);
    }



    @Override
    public void run() {

        try {

            while(true) {
                String received = input.readUTF();
                if (name != null)
                    log(name + " -> server: " + received);
                else
                    log("client -> server: " + received);
                Function function = Function.searchcommand(received);
                try {
                    switch (function) {
                        case MESSAGE:
                            String message = getString();
                            sessionManager.sendAll(message, this, name);
                            break;
                        case JOIN:
                            if (name == null) {
                                existcheck();
                            }
                            break;
                        case USERS:
                            String users = Users.getUsers().toString();
                            output.writeUTF("userlist");
                            output.writeUTF(users);
                            output.flush();
                            break;
                        case CHANGE:
                            existcheck();
                            break;
                        case EXIT:
                            Thread.sleep(1000);
                            return;
                    }
                }catch (NullPointerException e) {
                    continue;
                }

            }
        } catch (IOException | InterruptedException e) {
        log(e);
        }
        finally {
            sessionManager.remove(this);
            close();
        }

    }

    private void existcheck() throws IOException, InterruptedException {
        while (true) {
                String input = getString();
                if (Users.getUsers().contains(input)) {


                    output.writeUTF("overlap");
                    output.writeUTF("refused");

                } else {


                    output.writeUTF("overlap");
                    output.writeUTF("accepted");

                    if(Users.getUsers().contains(name))
                        Users.removeusers(name);
                    Users.addusers(input);
                    name = input;
                    break;
                }
        }
    }

    private String getString() throws IOException {
        String received3 = input.readUTF();
        if(name!=null)
            log(name+" -> server: " + received3);
        else
         log("client -> server: " + received3);
        int length = received3.length();
        return received3.substring(1, length - 1);
    }

    public synchronized void close(){
        if(closed)
            return;
        closeAll(socket,input,output);
        closed = true;
        log("연결종료: "+socket);
        if(name!=null)
            Users.removeusers(name);
    }
}
