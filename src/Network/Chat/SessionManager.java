package Network.Chat;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static Ex.Mylog.log;

public class SessionManager {
    List<Session> sessions = new ArrayList<>();

    public synchronized void add(Session session){
        sessions.add(session);
    }
    public synchronized void remove(Session session){
        sessions.remove(session);
    }
    public synchronized void closeAll(){
        for(Session session : sessions){
            session.close();
        }
        sessions.clear();
    }
    public void sendAll(String message, Session excludesession,String name) throws IOException, InterruptedException {
        for(Session session : sessions){
            if(session!=excludesession) {

                if(name!=null){
                    session.output.writeUTF(name);
                  log("All client <- " +name+": " + message);

                }
                else {
                    session.output.writeUTF("client");
                    log("All client <- client :" + message);
                }
                session.output.writeUTF(message);
                session.output.flush();
            }
        }
    }

}
