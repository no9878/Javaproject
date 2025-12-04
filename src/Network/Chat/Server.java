package Network.Chat;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import static Ex.Mylog.log;

public class Server {
    private static final int PORT = 12345;
    public static void main(String[] args) throws IOException {
        log("서버 시작");
        SessionManager sessionManager = new SessionManager();
        ServerSocket serverSocket = new ServerSocket(PORT);
        log("서버 소켓 시작 - 리스닝 포트: "+PORT);
        shutdownhook shutdownhook = new shutdownhook(serverSocket,sessionManager);
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownhook,"shutdown"));

        try {
            while (true) {
                Socket socket = serverSocket.accept();
                log("소켓 연결: " + socket);
                Thread thread = new Thread(new Session(socket, sessionManager));
                thread.start();
            }
        } catch (IOException e){
            log("서버 소켓 종료: "+e);
        }
    }
    static class shutdownhook implements Runnable{
        private final ServerSocket serverSocket;
        private final SessionManager sessionManager;
        public shutdownhook(ServerSocket serverSocket,SessionManager sessionManager){
            this.serverSocket = serverSocket;
            this.sessionManager = sessionManager;
        }

        @Override
        public void run() {
            log("shutdownhook 실행");
            try {
                sessionManager.closeAll();
                serverSocket.close();

                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("e = "+e);
            }

        }


    }
}
