package Server.Command;

import Server.Database.DatabaseHelper;
import Server.Database.User;


import java.io.File;
import java.util.Arrays;


//there may be problem with relative and absolute paths

/**
 * Created by KUBA on 2016-08-10.
 */
public class ChmodCommand extends Command {
    private final File file;
    private final String newPermission;

    public ChmodCommand(String[] arguments, SessionManager sessionManager){
        super(sessionManager);
        String[] filename = Arrays.copyOfRange(arguments, 0, arguments.length - 1);
        newPermission = arguments[arguments.length - 1];
        file = getSessionManager().resolveRelativePath(filename);

        if(newPermission.length() != 2 || !newPermission.matches("[0-3]2")){
            throw new IllegalArgumentException("501 Syntax error in parameters or arguments.");
        }
    }

    @Override
    public String execute(){
        if(!checkPermission()){
            return "450 Permission denied";
        }
        DatabaseHelper.getInstance().changePermissions(file.getAbsolutePath(), preparePermission(newPermission));

        return "Permissions changed";
    }

    private boolean checkPermission(){
        User user = getSessionManager().getLoggedUser();
        String path = file.getAbsolutePath();
        return DatabaseHelper.getInstance().canUserWriteToFile(path, user);
    }

    private Permissions preparePermission(String newPermission){
        int ownerCode = Integer.parseInt(newPermission.substring(0, 1));
        int groupCode = Integer.parseInt(newPermission.substring(1, 2));
        Permissions permissions = new Permissions();

        if(ownerCode == 2 || ownerCode == 3)
            permissions.userWrite = true;
        if(ownerCode == 1 || ownerCode == 3)
            permissions.userRead = true;
        if(groupCode == 2 || groupCode == 3)
            permissions.groupWrite = true;
        if(groupCode == 1 || groupCode == 3)
            permissions.groupRead = true;

        return  permissions;
    }

    public static class Permissions{
        public boolean userRead = false;
        public boolean userWrite = false;
        public boolean groupRead = false;
        public boolean groupWrite = false;
    }
}
