package Server.Command;

/**
 * Created by KUBA on 2016-08-11.
 */
public class PwdCommand extends Command{
    public PwdCommand(String[] arguments, SessionManager sessionManager){
        super(sessionManager);
        if(arguments.length != 0){
            throw new IllegalArgumentException("501 Syntax error in parameters or arguments.");
        }
    }

    @Override
    public String execute(){
        return "257 \"" + getSessionManager().getPath() + "\"";
    }
}
