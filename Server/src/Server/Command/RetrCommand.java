package Server.Command;

import Server.Database.DatabaseHelper;
import Server.Database.User;
import Server.PassiveTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by KUBA on 2016-08-13.
 */
public class RetrCommand extends Command {
    private final File fileSend;

    public RetrCommand(String[] arguments, SessionManager sessionManager){
        super(sessionManager);
        fileSend = getSessionManager().resolveRelativePath(arguments);
    }

    @Override
    public String execute(){
        if(!checkPermission())
            return "450 Permission denied";

        try{
            InputStream inputStream = new FileInputStream(fileSend);
            PassiveTask writeTask = new PassiveTask("write");
            writeTask.setInputStream(inputStream);
            getSessionManager().getBlockingQueue().put(writeTask);
        } catch (InterruptedException e){
            System.out.println("Error while putting RETR task to queue");
        } catch (FileNotFoundException e){
            System.out.println("File not found");
            return "450 File not found";
        }

        return "150 Opening binary mode data connection for " + fileSend.getName();
    }

    private boolean checkPermission(){
        User user = getSessionManager().getLoggedUser();
        String path = fileSend.getPath();
        return DatabaseHelper.getInstance().canUserWriteToFile(path, user);
    }
}
