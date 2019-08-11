package org.cd.pa.blog;

import org.apache.commons.lang3.StringUtils;
import org.cd.dao.AbstractBaseDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-11 18:22
 **/
public class CnblogDao extends AbstractBaseDao<Cnblog.PageVo> {

    @Override
    public Long insert(Connection conn, Cnblog.PageVo pageVo) {
        Long id = 0L;
        if(check(conn, pageVo.getHash()))
            return id;
        String column = "hash,`name`,`url`,`desc`,`create_time`";
        String values = "?,?,?,?,?";
        String sql = "insert into blog (" + column + ") values(" +values+")";
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, pageVo.getHash());
            ps.setString(2, pageVo.getName());
            ps.setString(3, pageVo.getUrl());
            ps.setString(4, pageVo.getDesc());
            ps.setString(5, pageVo.getCreateTime());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                id = rs.getLong(1);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public boolean check(Connection conn, Object id) {
        String sql = "select count(*) as count from blog where hash = '"+ id + "'";
        try {
            if(executeQuery(sql)){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
