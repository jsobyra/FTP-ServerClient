package Server;

/**
 * Created by KUBA on 2016-08-02.
 */
public class ServerInvoker {

    public static void main(String[] args){

        ThreadPooledServer server = new ThreadPooledServer(9000);
        new Thread(server).start();

        try{
            Thread.sleep(60 * 60* 1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println("Stopping Server");
        server.stop();
    }
}
