package Model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.*;
import com.j256.ormlite.table.TableUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by KUBA on 2016-08-07.
 */
public class DatabaseHelper {

    private final static String DATABASE_URL = "jdbc:mysql://localhost:3306/demo";
    private final static String user = "root";
    private final static String password = "1234";
    private Dao<Group, Integer> groupDao;
    private Dao<User, Integer> userDao;
    private Dao<GroupUser, Integer> groupUserDao;
    JdbcConnectionSource connectionSource;

    public void initializeDatabase() throws SQLException{
        connectionSource = null;
        connectionSource = new JdbcConnectionSource(DATABASE_URL, user, password);
        userDao = DaoManager.createDao(connectionSource, User.class);
        groupDao = DaoManager.createDao(connectionSource, Group.class);
        groupUserDao = DaoManager.createDao(connectionSource, GroupUser.class);
        TableUtils.createTableIfNotExists(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, Group.class);
        TableUtils.createTableIfNotExists(connectionSource, GroupUser.class);
    }


    public void closeDatabase() throws SQLException, IOException{
        if (connectionSource != null) {
            connectionSource.close();
        }
    }

    public void addUser(String name, String password) throws SQLException {
        if(!existUser(name)){
            userDao.create(new User(name, password));
        }
    }

    public void addGroup(String groupName) throws SQLException{
        if(!existGroup(groupName)){
            groupDao.create(new Group(groupName));
        }
    }

    public void addGroupUser(String name, String groupname) throws SQLException{
        if(!existGroupUser(name, groupname)){

            QueryBuilder<User, Integer> qb = userDao.queryBuilder();
            Where where = qb.where();
            where.eq(User.NAME_COLNAME, name);
            PreparedQuery<User> preparedQuery = qb.prepare();
            List<User> users = userDao.query(preparedQuery);

            QueryBuilder<Group, Integer> qb1 = groupDao.queryBuilder();
            Where where1 = qb1.where();
            where1.eq(Group.GROUPNAME_COLNAME, groupname);
            PreparedQuery<Group> preparedQuery1 = qb1.prepare();
            List<Group> groups = groupDao.query(preparedQuery1);

            groupUserDao.create(new GroupUser(groups.get(0), users.get(0)));
        }
    }

    public void deleteUser(String name) throws SQLException{
        if(existUser(name)){
            DeleteBuilder<GroupUser, Integer> qb1 = groupUserDao.deleteBuilder();
            Where where1 = qb1.where();
            where1.eq(GroupUser.USER_ID_COLNAME, findUserID(name));
            PreparedDelete<GroupUser> preparedDelete1 = qb1.prepare();
            groupUserDao.delete(preparedDelete1);

            DeleteBuilder<User, Integer> qb = userDao.deleteBuilder();
            Where where = qb.where();
            where.eq(User.NAME_COLNAME, name);
            PreparedDelete<User> preparedDelete = qb.prepare();
            userDao.delete(preparedDelete);
        }
    }

    public void deleteGroup(String groupName) throws SQLException{
        if(existGroup(groupName)){

            DeleteBuilder<GroupUser, Integer> qb1 = groupUserDao.deleteBuilder();
            Where where1 = qb1.where();
            where1.eq(GroupUser.GROUP_ID_COLNAME, findGroupID(groupName));
            PreparedDelete<GroupUser> preparedDelete1 = qb1.prepare();
            groupUserDao.delete(preparedDelete1);

            DeleteBuilder<Group, Integer> qb = groupDao.deleteBuilder();
            Where where = qb.where();
            where.eq(Group.GROUPNAME_COLNAME, groupName);
            PreparedDelete<Group> preparedDelete = qb.prepare();
            groupDao.delete(preparedDelete);
        }
    }

    public void deleteGroupUser(String name, String groupname) throws SQLException{
        if(existGroupUser(name, groupname)){
            DeleteBuilder<GroupUser, Integer> qb = groupUserDao.deleteBuilder();
            Where where = qb.where();
            where.eq(GroupUser.GROUP_ID_COLNAME, findGroupID(groupname));
            where.and();
            where.eq(GroupUser.USER_ID_COLNAME, findUserID(name));
            PreparedDelete<GroupUser> preparedDelete = qb.prepare();
            groupUserDao.delete(preparedDelete);
        }
    }

    private boolean existUser(String name) throws SQLException{
        QueryBuilder<User, Integer> qb = userDao.queryBuilder();
        Where where = qb.where();
        where.eq(User.NAME_COLNAME, name);
        PreparedQuery<User> preparedQuery = qb.prepare();
        List<User> users = userDao.query(preparedQuery);

        if(users.size() == 0)
            return false;
        return true;
    }

    private boolean existGroupUser(String name, String groupname) throws SQLException{

        Integer userId = findUserID(name);
        if(userId == null)
            return false;

        Integer groupId = findGroupID(groupname);
        if(groupId == null)
            return false;

        QueryBuilder<GroupUser, Integer> groupuserQB = groupUserDao.queryBuilder();
        Where where = groupuserQB.where();
        where.eq(GroupUser.USER_ID_COLNAME, userId);
        where.and();
        where.eq(GroupUser.GROUP_ID_COLNAME, groupId);
        PreparedQuery<GroupUser> preparedQuery = groupuserQB.prepare();
        List<GroupUser> groupusers = groupUserDao.query(preparedQuery);

        if(groupusers.size() == 0)
            return false;
        return true;
    }

    private boolean existGroup(String groupName) throws SQLException{
        QueryBuilder<Group, Integer> qb = groupDao.queryBuilder();
        Where where = qb.where();
        where.eq(Group.GROUPNAME_COLNAME, groupName);
        PreparedQuery<Group> preparedQuery = qb.prepare();
        List<Group> groups = groupDao.query(preparedQuery);

        if(groups.size() == 0)
            return false;
        return true;
    }

    private Integer findUserID(String name) throws SQLException{
        QueryBuilder<User, Integer> userQB = userDao.queryBuilder();
        Where where = userQB.where();
        where.eq(User.NAME_COLNAME, name);
        PreparedQuery<User> preparedQuery = userQB.prepare();
        List<User> users = userDao.query(preparedQuery);

        if(users.size() == 0)
            return null;

        return users.get(0).getId();
    }

    private Integer findGroupID(String groupName) throws SQLException{
        QueryBuilder<Group, Integer> groupQB = groupDao.queryBuilder();
        Where where1 = groupQB.where();
        where1.eq(Group.GROUPNAME_COLNAME, groupName);
        PreparedQuery<Group> preparedQuery1 = groupQB.prepare();
        List<Group> groups = groupDao.query(preparedQuery1);

        if(groups.size() == 0)
            return null;

        return groups.get(0).getId();
    }
}








