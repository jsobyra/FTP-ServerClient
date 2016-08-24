package Server.Command;

/**
 * Created by KUBA on 2016-08-08.
 */

public abstract class Command {
    private final SessionManager sessionManager;

    public abstract String execute();

    public SessionManager getSessionManager(){
        return sessionManager;
    }

    public Command(SessionManager sessionManager){
        this.sessionManager = sessionManager;
    }
}
