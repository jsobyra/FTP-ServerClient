package Server.Command;

import java.io.File;

/**
 * Created by KUBA on 2016-08-11.
 */
public class RmdCommand extends Command{
    private final File toDeleteDirectory;

    public RmdCommand(String[] arguments, SessionManager sessionManager){
        super(sessionManager);
        toDeleteDirectory = getSessionManager().resolveRelativePath(arguments);
    }

    @Override
    public String execute(){
        if(toDeleteDirectory.delete())
            return "250 RMD was successful";
        else
            return "450 non empty directory";
    }
}
