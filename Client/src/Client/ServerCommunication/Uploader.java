package Client.ServerCommunication;

import org.apache.commons.io.IOUtils;



import java.io.*;
import java.net.Socket;

/**
 * Created by KUBA on 2016-08-14.
 */
public class Uploader implements Runnable{
    private final String passiveHost;
    private final int passivePort;
    private final PrintWriter outCommand;
    private final BufferedReader inCommand;
    private final File localFile;


    public Uploader(String passiveHost, int passivePort, PrintWriter outCommand, BufferedReader inCommand, File localFile){
        this.passiveHost = passiveHost;
        this.passivePort = passivePort;
        this.outCommand = outCommand;
        this.inCommand = inCommand;
        this.localFile = localFile;
    }

    @Override
    public void run(){
        try(Socket passive = new Socket(passiveHost, passivePort)){
            synchronized (inCommand){
                ServerConnection.getInstance().sendCommand("STOR " + localFile.getName());
                ServerConnection.getInstance().readAnswer();
            }

            try(OutputStream fileUploadStream = passive.getOutputStream()){
                InputStream sourceStream = new FileInputStream(localFile);
                IOUtils.copy(sourceStream, fileUploadStream);
            }

            synchronized (inCommand){
                String answer = inCommand.readLine();
                System.out.println("Server: " + answer);
            }
        } catch (IOException e){
            System.out.println("Error uploading file");
        }
    }
}






























































































