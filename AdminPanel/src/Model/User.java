package Model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by KUBA on 2016-08-06.
 */
public class User {

    public final static String ID_COLNAME = "_id";
    public final static String NAME_COLNAME = "_name";

    @DatabaseField(generatedId = true, columnName = ID_COLNAME)
    Integer id;

    @DatabaseField(columnName = NAME_COLNAME)
    String name;

    @DatabaseField
    String password;

    public User(){
    }

    public User(String name, String password){
        this.name = name;
        this.password = password;
    }

    public Integer getId(){
        return id;
    }

    public void  setId(Integer id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }
}



















































