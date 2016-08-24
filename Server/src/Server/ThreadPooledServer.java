package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by KUBA on 2016-08-02.
 */
public class ThreadPooledServer implements Runnable {

    protected int serverPort = 100;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;
    protected ExecutorService threadPool = Executors.newFixedThreadPool(10);


    public ThreadPooledServer(int port){
        this.serverPort = port;
    }

    @Override
    public void run(){
        synchronized (this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(!isStopped()){
            Socket clientSocket = null;
            try{
                clientSocket = this.serverSocket.accept();
            } catch (IOException e){
                if(isStopped()){
                    System.out.println("Server Stopped.");;
                    break;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }

            try {
                this.threadPool.execute(new WorkerRunnable(clientSocket));
            } catch (SocketException e){
                System.out.println("Problem with executing new tread!");
            }

        }
        this.threadPool.shutdown();
        System.out.println("Server Stopped");
    }

    private synchronized boolean isStopped(){
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try{
            this.serverSocket.close();
        } catch (IOException e){
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket(){
        try{
            this.serverSocket = new ServerSocket(this.serverPort);
            System.out.println(this.serverSocket.getInetAddress());
        } catch (IOException e){
            throw new RuntimeException("Cannot open port " + serverPort, e);
        }
    }
}















































































