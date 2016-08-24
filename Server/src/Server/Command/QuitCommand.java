package Server.Command;

/**
 * Created by KUBA on 2016-08-10.
 */
public class QuitCommand extends Command{

    public QuitCommand(String[] arguments, SessionManager sessionManager) {
        super(sessionManager);
        if (arguments.length != 0) {
            throw new IllegalArgumentException("501 Syntax error in parameters or arguments.");
        }
    }

    @Override
    public String execute(){
        getSessionManager().getCurrentThread().setRunning(false);
        return "221 Bye";
    }
}
