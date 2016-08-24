package Server.Database;

import com.j256.ormlite.field.DatabaseField;


/**
 * Created by KUBA on 2016-08-06.
 */
public class Group {
    public final static String ID_COLNAME = "_id";
    public final static String GROUPNAME_COLNAME = "_groupname";

    @DatabaseField(generatedId = true, columnName = ID_COLNAME)
    Integer id;

    @DatabaseField(columnName = GROUPNAME_COLNAME)
    String groupName;


    public Group(){
    }

    public Group(String groupName){
        this.groupName = groupName;
    }

    public Integer getId(){
        return id;
    }

    public void  setId(Integer id){
        this.id = id;
    }

    public String getGroupName(){
        return groupName;
    }

    public void setGroupName(String groupName){
        this.groupName = groupName;
    }
}
