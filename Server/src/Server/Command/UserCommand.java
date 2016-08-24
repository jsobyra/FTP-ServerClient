package Server.Command;

/**
 * Created by KUBA on 2016-08-09.
 */
public class UserCommand extends Command{
    private String username;

    public UserCommand(String[] arguments, SessionManager sessionManager){
        super(sessionManager);
        if(arguments.length != 1){
            throw new IllegalArgumentException("501 Syntax error in parameters or arguments.");
        }
        if(!isFirstCommand()){
            throw new IllegalArgumentException("503 Bad sequence of commands");
        }
        username = arguments[0];
    }

    public String getUsername(){
        return username;
    }

    @Override
    public String execute(){
        return "331 Password required";
    }

    private boolean isFirstCommand(){
        return getSessionManager().getCommandHistory().size() == 0;
    }
}
