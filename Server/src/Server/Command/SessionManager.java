package Server.Command;

import Server.Database.User;
import Server.PassiveConnection;
import Server.PassiveTask;
import Server.WorkerRunnable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by KUBA on 2016-08-09.
 */
public class SessionManager {
    private WorkerRunnable currentThread;
    private List<Command> commandHistory = new ArrayList<>();
    private User loggedUser = null;
    private Path currentDirectory = Paths.get("FTP-Server");
    private PassiveConnection passiveConnection = null;
    private BlockingQueue<PassiveTask> blockingQueue = new ArrayBlockingQueue<>(1);


    public List<Command> getCommandHistory(){
        return commandHistory;
    }

    public BlockingQueue<PassiveTask> getBlockingQueue(){
        return blockingQueue;
    }

    public void setLoggedUser(User user){
        loggedUser = user;
    }

    public void setPassiveConnection(PassiveConnection passiveConnection){
        this.passiveConnection = passiveConnection;
    }

    public void setCurrentThread(WorkerRunnable workerRunnable){
        this.currentThread = workerRunnable;
    }

    public WorkerRunnable getCurrentThread(){
        return currentThread;
    }

    public PassiveConnection getPassiveConnection(){
        return passiveConnection;
    }

    public String getAbsolutePath(){
        return currentDirectory.toAbsolutePath().toString();
    }

    public String getPath(){
        if(currentDirectory.getNameCount() < 2)
            return "/";
        else{
            String subPath = currentDirectory.subpath(1, currentDirectory.getNameCount()).toString();
            return "/" + subPath + "   " + currentDirectory.getFileName();
        }
    }

    public User getLoggedUser(){
        return loggedUser;
    }

    public void setCurrentDirectory(String directory){
        currentDirectory = Paths.get("FTP-Server" + directory);
    }

    public Path getCurrentDirectory(){
        return currentDirectory;
    }

    public File resolveRelativePath(String[] args){
        String relativePath = String.join(" ", args);
        System.out.println("Inside resolvePath: " + relativePath);
        System.out.println(currentDirectory.toString());
        return Paths.get(currentDirectory.toString(), relativePath).toFile();
    }
}
