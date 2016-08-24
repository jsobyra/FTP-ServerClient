package Server.Command;

import Server.PassiveConnection;

/**
 * Created by KUBA on 2016-08-13.
 */
public class AborCommand extends Command{

    public AborCommand(String[] arguments, SessionManager sessionManager){
        super(sessionManager);
        if(arguments.length != 0)
            throw new IllegalArgumentException("501 Syntax error in parameters or arguments.");
    }

    @Override
    public String execute(){
        PassiveConnection passiveConnection = getSessionManager().getPassiveConnection();
        if(passiveConnection != null)
            passiveConnection.abort();

        return "Executed succesfully";
    }
}
