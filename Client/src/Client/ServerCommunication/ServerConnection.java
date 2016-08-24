package Client.ServerCommunication;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by KUBA on 2016-08-14.
 */
public class ServerConnection {
    private static ServerConnection instance;
    private final PrintWriter outCommand;
    private final BufferedReader inCommand;

    public ServerConnection(String hostName, int port) throws IOException{
        Socket commandPort = new Socket(hostName, port);
        outCommand = new PrintWriter(new OutputStreamWriter(commandPort.getOutputStream()), true);
        inCommand = new BufferedReader(new InputStreamReader(commandPort.getInputStream()));

        TimerTask noopSender = new NoopSender(inCommand, outCommand);
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(noopSender, 20 * 1000, 10 * 1000);
        readAnswer();
        instance = this;
    }

    public static ServerConnection getInstance(){
        return instance;
    }

    public boolean login(String username, String password) throws IOException{
        sendCommand("USER " + username);
        if(readAnswer().startsWith("331")){
            sendCommand("PASS " + password);
            return readAnswer().startsWith("230");
        }
        return false;
    }

    public String listCurrentDirectory() throws IOException{
        Address address = connectToPassivePort();
        if(address == null){
            System.out.println("Cannot create passive connection!");
            return " ";
        }
        try (Socket passivePort = new Socket(address.getHostIP(), address.getPort())){
            synchronized (inCommand){
                sendCommand("LIST");
                readAnswer();
            }
            String response = IOUtils.toString(passivePort.getInputStream(), Charset.defaultCharset());
            readAnswer();
            return response;
        }

    }

    public void uploadFileToServer(File localFile) throws IOException{
        Address address = connectToPassivePort();
        if(address == null)
            return;
        Uploader uploader = new Uploader(address.getHostIP(), address.getPort(), outCommand, inCommand, localFile);
        Thread uploadThread = new Thread(uploader);
        uploadThread.start();
    }

    public File downloadRemoteFile(File file) throws IOException{
        Address address = connectToPassivePort();
        if(address == null){
            System.out.println("Cannot create passive connection!");
            return null;
        }
        String localPath = SessionManager.getInstance().getLocalPath();
        Downloader downloader = new Downloader(address.getHostIP(), address.getPort(), outCommand, inCommand, file, localPath);
        Thread downloadThread = new Thread(downloader);
        downloadThread.start();
        return new File(localPath + "/" + file.getName());

    }

    private Address connectToPassivePort() throws IOException{
        String answer;
        synchronized (inCommand){
            sendCommand("PASV");
            answer = readAnswer();
        }

        if(answer.startsWith("227"))
            return decodeAddress(answer);
        return null;
    }

    void sendCommand(String command){
        outCommand.println(command);
    }

    String readAnswer() throws IOException{
        String answer = inCommand.readLine();
        if(answer != null){
            String trimAnswer = answer.trim();
            return trimAnswer;
        }
        return " ";
    }

    private Address decodeAddress(String answer){
        String encodedAddress = answer.substring(answer.indexOf("(") + 1, answer.indexOf(")"));
        String[] numbers = encodedAddress.split(",");
        Address address = new Address();
        address.setHostIP(numbers[0]);
        for (int i = 1; i < 4; i++){
            address.setHostIP(address.getHostIP() + "." + numbers[i]);
        }
        address.setPort(256 * Integer.parseInt(numbers[4]) + Integer.parseInt(numbers[5]));

        return address;
    }

    public void changeRemoteDirectory(String directory) throws IOException{
        String answer;
        synchronized (inCommand){
            sendCommand("CWD " + directory);
            answer = readAnswer();
        }
        if(answer.startsWith("250")){
            SessionManager.getInstance().setRemotePath(directory);
        }
    }

    public boolean deleteRemoteFile(File remoteFile) throws IOException{
        System.out.println("Current: " + currentRemoteDirectory());
        changeRemoteDirectory(remoteFile.getParent());
        String answer;
        System.out.println("remote file: " + remoteFile.getName());
        synchronized (inCommand){
            sendCommand("DELE " + remoteFile.getName());
            answer = readAnswer();
        }
        return answer.startsWith("250");
    }

    private String currentRemoteDirectory() throws IOException{
        String answer;
        synchronized (inCommand){
            sendCommand("PWD");
            answer = readAnswer();
        }
        return answer;
    }

    public void makeNewRemoteDir(String newDirectory) throws IOException{
        synchronized (inCommand){
            sendCommand("MKD " + newDirectory);
            readAnswer();
        }
    }

    private static class Address{
        private String hostIP;
        private int port;

        public String getHostIP(){
            return hostIP;
        }

        public int getPort(){
            return port;
        }

        public void setHostIP(String hostIP){
            this.hostIP = hostIP;
        }

        public void setPort(int port){
            this.port = port;
        }
    }
}





















































