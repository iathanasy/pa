package org.cd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-05-28 23:55
 **/
public abstract class AbstractBaseDao<T> implements BaseDao<T> {


    public Long insert(T t) {
        return insert(ConnectionManager.getConnection(), t);
    }

    public boolean check(Object id) {
        return check(ConnectionManager.getConnection(), id);
    }

    public boolean check(Connection conn, Object id){
        return false;
    };

    public boolean executeQuery(String sql) throws SQLException {
        return executeQuery(ConnectionManager.getConnection(), sql);
    }

    public boolean executeQuery(Connection conn, String sql) throws SQLException {
        int num = 0;
        PreparedStatement ps;
        ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            num = rs.getInt("count");
        }
        rs.close();
        ps.close();
        if(num == 0){
            return false;
        }else{
            return true;
        }
    }
}
