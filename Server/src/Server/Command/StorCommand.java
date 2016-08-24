package Server.Command;

import Server.Database.DatabaseHelper;
import Server.Database.User;
import Server.PassiveTask;

import java.io.*;

/**
 * Created by KUBA on 2016-08-13.
 */
public class StorCommand extends Command{
    private final File fileRead;

    public StorCommand(String[] arguments, SessionManager sessionManager){
        super(sessionManager);
        fileRead = getSessionManager().resolveRelativePath(arguments);
    }

    @Override
    public String execute(){
        try{
            OutputStream outputStream = new FileOutputStream(fileRead);
            PassiveTask readTask = new PassiveTask("read");
            readTask.setOutputStream(outputStream);
            getSessionManager().getBlockingQueue().put(readTask);
        } catch (InterruptedException e){
            System.out.println("Error while putting STOR task to queue");
        } catch (FileNotFoundException e){
            System.out.println("Error storing file");
            return "450 Error storing file";
        }

        if (saveToDatabase()) {
            return "150 FILE " + fileRead.getName();
        } else {
            return "450 file already exists";
        }
    }


    private boolean saveToDatabase(){
        User user = getSessionManager().getLoggedUser();
        String path = fileRead.getPath();
        return DatabaseHelper.getInstance().insertNewFileIfNotExists(path, user);
    }
}
