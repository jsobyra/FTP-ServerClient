package Server.Command;


import Server.Database.DatabaseHelper;
import Server.Database.User;

import java.io.File;

/**
 * Created by KUBA on 2016-08-11.
 */
public class DeleCommand extends Command{
    private final File fileDelete;

    public DeleCommand(String[] arguments, SessionManager sessionManager){
        super(sessionManager);
        fileDelete = getSessionManager().resolveRelativePath(arguments);
        System.out.println("DeleCommand constructor fileDelete: " + fileDelete.getPath());
    }

    @Override
    public String execute(){
        if(!checkPermission()){
            return "450 Permission denied";
        }
        if(fileDelete.isDirectory() && fileDelete.listFiles() != null){
            return "450 Non empty directory";
        }

        if(!fileDelete.delete() || !DatabaseHelper.getInstance().deleteFile(fileDelete.getPath())){
            return "450 Unsuccessful delete";
        }
        return "250 DELE was successful";
    }


    private boolean checkPermission(){
        User user = getSessionManager().getLoggedUser();
        String path = fileDelete.getPath();

        return DatabaseHelper.getInstance().canUserWriteToFile(path, user);
    }
}
