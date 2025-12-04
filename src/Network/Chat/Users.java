package Network.Chat;

import java.util.ArrayList;
import java.util.List;

public class Users {
    private volatile static List<String> users = new ArrayList<>();


    public synchronized static List<String> getUsers() {
        return users;
    }

    public synchronized static void addusers(String name){
        users.add(name);
    }
    public synchronized static void removeusers(String name){
        users.remove(name);
    }



    @Override
    public String toString() {
        return "Users{" +
                "users=" + users +
                '}';
    }
}
