package Server.Command;

import Server.Database.DatabaseHelper;

import java.util.Arrays;

/**
 * Created by KUBA on 2016-08-11.
 */
public class CwdCommand extends Command{
    private final String updatedDirectory;

    public CwdCommand(String[] arguments, SessionManager sessionManager){
        super(sessionManager);
        updatedDirectory = String.join(" ", arguments);
    }

    @Override
    public String execute(){
        getSessionManager().setCurrentDirectory(updatedDirectory);

        return "250 CWD was successful";
    }
}
