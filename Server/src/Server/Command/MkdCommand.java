package Server.Command;

import java.io.File;

/**
 * Created by KUBA on 2016-08-11.
 */
public class MkdCommand extends Command{
    private final File createdNewDirectory;

    public MkdCommand(String[] arguments, SessionManager sessionManager){
        super(sessionManager);
        createdNewDirectory = getSessionManager().resolveRelativePath(arguments);
    }

    @Override
    public String execute(){
        if(createdNewDirectory.mkdirs())
            return "257 Pathname was created";
        else
            return "450 directory already exists";
    }
}
