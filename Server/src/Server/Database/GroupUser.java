package Server.Database;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by KUBA on 2016-08-06.
 */
//assosiations

public class GroupUser {
    public static final String USER_ID_COLNAME = "user_id";
    public static final String GROUP_ID_COLNAME = "group_id";

    @DatabaseField(generatedId = true)
    Integer id;

    @DatabaseField(foreign = true, columnName = USER_ID_COLNAME)
    User user;

    @DatabaseField(foreign = true, columnName = GROUP_ID_COLNAME)
    Group group;

    public GroupUser(){
    }

    public GroupUser(Group group, User user){
        this.group = group;
        this.user = user;
    }
}
