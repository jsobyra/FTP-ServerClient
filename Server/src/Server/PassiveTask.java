package Server;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by KUBA on 2016-08-11.
 */
public class PassiveTask {
    private final String type;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    public PassiveTask(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }

    public InputStream getInputStream(){
        return inputStream;
    }

    public OutputStream getOutputStream(){
        return outputStream;
    }

    public void setInputStream(InputStream inputStream){
        this.inputStream = inputStream;
    }

    public void setOutputStream(OutputStream outputStream){
        this.outputStream = outputStream;
    }
}
