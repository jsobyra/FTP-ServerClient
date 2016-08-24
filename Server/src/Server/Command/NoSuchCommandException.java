package Server.Command;

/**
 * Created by KUBA on 2016-08-09.
 */
public class NoSuchCommandException extends IllegalArgumentException{

    public NoSuchCommandException(){
        super("500 Command does not exist");
    }
}
