package Client.ServerCommunication;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by KUBA on 2016-08-15.
 */
public class SessionManager {
    private static final SessionManager instance = new SessionManager();
    private final SimpleStringProperty localPath = new SimpleStringProperty();
    private final SimpleStringProperty remotePath = new SimpleStringProperty();

    private SessionManager(){
    }

    public static SessionManager getInstance(){
        return instance;
    }

    public String getRemotePath(){
        return remotePath.get();
    }

    public void setRemotePath(String path){
        remotePath.set(path);
    }

    public StringProperty remotePathProperty(){
        return remotePath;
    }

    public String getLocalPath(){
        return localPath.get();
    }

    public void setLocalPath(String path){
        localPath.set(path);
    }

    public StringProperty localPathProperty(){
        return localPath;
    }
}

















































