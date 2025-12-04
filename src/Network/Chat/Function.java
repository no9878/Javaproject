package Network.Chat;

public enum Function {
    JOIN("/join"),
    MESSAGE("/message"),
    CHANGE("/change"),
    USERS("/users"),
    EXIT("/exit")
;
    private final String command;

    Function(String command) {
        this.command = command;
    }

    public String getCommand(){
        return command;
    }
    public static Function searchcommand(String input){
        for (Function f : values()){
            if (f.getCommand().equals(input))
                return f;
        }
        return null;
    }
}
