package Network.Chat;

import Ex.Mylog;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static Ex.Mylog.log;


public class SwitchFuction implements Runnable {
    Scanner scanner = new Scanner(System.in);
    private final DataInputStream input;
    private final DataOutputStream output;
    private final Socket socket;
    private String name = null;
    private String PROMPT = "명령어를 입력하세요: ";
    protected static boolean exit = false;
    public SwitchFuction(DataInputStream input, DataOutputStream output, Socket socket) {
        this.input = input;
        this.output = output;
        this.socket = socket;
    }
public void reprintPROMPT(){
    System.out.print(PROMPT);
}

    protected BlockingQueue<String> responsequeue = new LinkedBlockingQueue<>();


    @Override
    public void run() {

        try {

            while (true) {
                System.out.print(PROMPT);
                String order = scanner.nextLine();
                String menu = order;
                if (name != null) {
                    Mylog.log(name + " -> server: " + order);
                } else {
                    Mylog.log("client -> server: " + order);
                }

                output.writeUTF(order);

                Function function = Function.searchcommand(menu);
                try {
                    switch (function) {
                        case JOIN:
                            if (name != null) {
                                System.out.println("이미 이름이 존재합니다. 변경하려면 /change 호출바람.");
                                break;
                            }
                            PROMPT = "이름을 입력하세요.(형식: {이름}): ";
                            existcheck();
                            System.out.println("이름을 생성하였습니다.");
                            break;
                        case CHANGE:
                            PROMPT = "변경할 이름을 입력하세요.(형식: {이름}): ";
                            existcheck();
                            System.out.println("이름이 변경되었습니다.");
                            break;
                        case MESSAGE:
                            String input2;
                            while (true) {
                                System.out.print("내용을 입력하세요: ");
                                input2 = scanner.nextLine();
                                if (!formcheck(input2)) {
                                    System.out.println("다시 입력해 주세요.(형식: {내용})");
                                    continue;
                                }
                                break;
                            }
                            output.writeUTF(input2);
                            if (name != null)
                                log(name + " -> server: " + input2);
                            else
                                log("client -> server: " + input2);

                            break;
                        case USERS:
                            responsequeue.take();
                            break;

                        case EXIT:
                            exit = true;
                            log("SwitchFunction스레드 종료");
                            return;

                    }
                } catch (Exception e) {
                    if (order.isEmpty())
                        continue;
                    else {
                        System.out.println("잘못된 입력입니다.");
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            log(e);
            log("SwitchFunction스레드 종료");
        }


    }



    private void existcheck() throws IOException, InterruptedException {
        while(true) {
            System.out.print(PROMPT);
            String input = scanner.nextLine();
            if (!formcheck(input)) {
                System.out.println("다시 입력해주세요.(형식: {이름})");
                continue;
            }
            if (name != null) {
                Mylog.log(name + " -> server: " + input);
            } else {
                Mylog.log("client -> server: " + input);
            }
            output.writeUTF(input);
                    if (responsequeue.take().equals("refused")) {
                        System.out.println("중복된 이름입니다.");

                    } else {
                        name = getString(input);
                        PROMPT = "명령어를 입력하세요: ";
                        break;

                    }
            }
        }


    private String getString(String input) {
        int length = input.length();
        return input.substring(1, length - 1);
    }
    private boolean formcheck(String input){
        int length = input.length();
        if(input.isEmpty()){
            return false;
        }
        if( ((input.charAt(0)) == '{')  && ((input.charAt(length-1))=='}')){
            return true;
        }
            return false;
    }
}
