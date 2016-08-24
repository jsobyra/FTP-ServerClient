package Client.ServerCommunication;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.Socket;

/**
 * Created by KUBA on 2016-08-15.
 */
public class Downloader implements Runnable{
    private final String passiveHost;
    private final int passivePort;
    private final PrintWriter outCommand;
    private final BufferedReader inCommand;
    private final File remoteFile;
    private final String localPath;


    public Downloader(String passiveHost, int passivePort, PrintWriter outCommand, BufferedReader inCommand, File remoteFile, String localPath){
        this.passiveHost = passiveHost;
        this.passivePort = passivePort;
        this.outCommand = outCommand;
        this.inCommand = inCommand;
        this.remoteFile = remoteFile;
        this.localPath = localPath;

    }

    @Override
    public void run(){
        try(Socket passive = new Socket(passiveHost, passivePort)){
            synchronized (inCommand){
                ServerConnection.getInstance().sendCommand("RETR " + remoteFile.getName());
                ServerConnection.getInstance().readAnswer();
            }

            try(InputStream fileFromServerStream = passive.getInputStream()){
                File targetFile = new File(localPath +"/" + remoteFile.getName());
                FileUtils.copyInputStreamToFile(fileFromServerStream, targetFile);
            }

            synchronized (inCommand){
                String answer = inCommand.readLine();
                System.out.println("Server: " + answer);
            }
        } catch (IOException e){
            System.out.println("Error downloading file");
        }
    }

    private void copy(InputStream inputStream, OutputStream outputStream) throws IOException{
        byte[] buffer = new byte[1024];
        int length;
        while((length = inputStream.read(buffer)) >= 0){
            outputStream.write(buffer, 0, length);
        }
    }
}
