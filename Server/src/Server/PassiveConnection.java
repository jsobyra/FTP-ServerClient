package Server;

import Server.Command.SessionManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by KUBA on 2016-08-11.
 */
public class PassiveConnection implements Runnable{

    private final SessionManager sessionManager;
    private final ServerSocket serverSocket;
    private final int serverPort;
    private Socket clientSocket;
    private volatile boolean isAborted = false;

    public PassiveConnection(SessionManager sessionManager, ServerSocket serverSocket, int serverPort){
        this.sessionManager = sessionManager;
        this.serverSocket = serverSocket;
        this.serverPort = serverPort;
    }

    @Override
    public void run(){
        try{
            clientSocket = serverSocket.accept();
            System.out.println("Connection accepted on passive connection portL " + serverPort);
            PassiveTask passiveTask = sessionManager.getBlockingQueue().take();
            executeTask(passiveTask);
        } catch(IOException e){
            System.out.println("Error when entering in passive mode");
        } catch (InterruptedException e){
            System.out.println("Error during taking task from queue");
        } finally {

            try{
                if(clientSocket != null)
                    clientSocket.close();
                if(serverSocket != null)
                    serverSocket.close();
            }catch (IOException e){
                System.out.println("Error when closing sockets in passive mode");
            }
        }
    }

    private void executeTask(PassiveTask passiveTask){
        try{
            Thread.sleep(500);
            switch (passiveTask.getType()){
                case "write" :
                    System.out.println("Sending data to client...");
                    copy(passiveTask.getInputStream(), clientSocket.getOutputStream());
                    break;
                case "read" :
                    System.out.println("Receiving data from client");
                    copy(clientSocket.getInputStream(), passiveTask.getOutputStream());
                    break;
                case "quit" :
                    break;
            }

            closeStreams(passiveTask);
            signalEndOfOperation();
        } catch (IOException e){
            System.out.println("Error sending/receiving data in passive mode");
        } catch (InterruptedException e){
            System.out.println("Error while waiting with Thread.sleep()");
        }
    }

    private void closeStreams(PassiveTask passiveTask) throws IOException{
        if(passiveTask.getInputStream() != null)
            passiveTask.getInputStream().close();
        if(passiveTask.getOutputStream() != null)
            passiveTask.getOutputStream().close();
    }

    private void signalEndOfOperation(){
        try{
            OutputStream outputStream = sessionManager.getCurrentThread().clientSocket.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            PrintWriter out = new PrintWriter(outputStreamWriter, true);
            out.println("226 Transfer complete");
        } catch (IOException e){
            System.out.println("Error writing to command port from passive connection");
        }
    }

    private void copy(InputStream inputStream, OutputStream outputStream) throws IOException{
        byte[] buffer = new byte[1024];
        int length;
        while(!isAborted() && (length = inputStream.read(buffer)) >= 0){
            outputStream.write(buffer, 0, length);
        }
    }

    private synchronized boolean isAborted(){
        return this.isAborted;
    }

    public synchronized void abort(){
        this.isAborted = true;
    }
}
































































