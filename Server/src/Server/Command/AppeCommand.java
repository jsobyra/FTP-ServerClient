package Server.Command;

import Server.Database.DatabaseHelper;
import Server.Database.User;
import Server.PassiveTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by KUBA on 2016-08-18.
 */
public class AppeCommand extends Command{
    private final File fileRead;

    public AppeCommand(String[] arguments, SessionManager sessionManager){
        super(sessionManager);
        fileRead = getSessionManager().resolveRelativePath(arguments);
    }

    @Override
    public String execute(){
        if(!checkPermission()){
            System.out.println("Permission denied!");
            return "450 Permission denied";
        }

        try{
            OutputStream outputStream = new FileOutputStream(fileRead);
            PassiveTask readTask = new PassiveTask("read");
            readTask.setOutputStream(outputStream);
            getSessionManager().getBlockingQueue().put(readTask);
        } catch (InterruptedException e){
            System.out.println("Error while putting APPE task to queue");
        } catch (FileNotFoundException e){
            System.out.println("Error appending file");
            return "450 Error appending file";
        }

        return "150 Opening binary mode data connection for " + fileRead.getName();

    }

    private boolean checkPermission(){
        User user = getSessionManager().getLoggedUser();
        String path = fileRead.getPath();

        return DatabaseHelper.getInstance().canUserWriteToFile(path, user);
    }
}
