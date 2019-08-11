package org.cd.dao;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import org.cd.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DB Connection管理
 * @author cd
 * @date 2019年5月27日 下午5:44:59
 * @desc
 */
public class ConnectionManager {
	
	private static Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
	private static Connection conn;
	
	public static Connection getConnection(){
		//获取数据库连接
		try {
			if(conn == null || conn.isClosed()){
                conn = createConnection();
            }
            else{
                return conn;
            }
		} catch (SQLException e) {
			logger.error("SQLException",e);
		}
		return conn;
	}
	
	static {
		try {
			//Class.forName("org.gjt.mm.mysql.Driver") ;//加载驱动
			Class.forName(Config.driverClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public static void close(){
		if(conn != null){
			//logger.info("关闭连接中");
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error("SQLException",e);
			}
		}
	}
	
	public static Connection createConnection(){
		String user = Config.dbUsername;
		String password = Config.dbPassword;
		String dbName = Config.dbName;
		String url= Config.dbUrl;
		Connection con=null;
		try{
			con = DriverManager.getConnection(url,user,password);//建立mysql的连接
			logger.debug("success!");
		} catch(MySQLSyntaxErrorException e){
			logger.error("数据库不存在..请先手动创建创建数据库:" + dbName);
			e.printStackTrace();
		} catch(SQLException e2){
			logger.error("SQLException",e2);
		}
		return con;
	}

	public static void main(String [] args) throws Exception{
		getConnection();
		close();
	}
	
	/**
	 * logger.info("解析用户成功:" + u.toString());
            if(Config.dbEnable){
                Connection cn = getConnection();
                if (zhiHuDao1.insertUser(cn, u)){
                    parseUserCount.incrementAndGet();
                }
                for (int j = 0; j < u.getFollowees() / 20; j++){
                    if (zhiHuHttpClient.getDetailListPageThreadPool().getQueue().size() > 1000){
                        continue;
                    }
                    String nextUrl = String.format(USER_FOLLOWEES_URL, u.getUserToken(), j * 20);
                    if (zhiHuDao1.insertUrl(cn, Md5Util.Convert2Md5(nextUrl)) ||
                            zhiHuHttpClient.getDetailListPageThreadPool().getActiveCount() == 1){
                        //防止死锁
                        HttpGet request = new HttpGet(nextUrl);
//                        request.setHeader("authorization", "oauth " + ZhiHuHttpClient.getAuthorization());
                        zhiHuHttpClient.getDetailListPageThreadPool().execute(new DetailListPageTask(request, true));
                    }
                }
            }
            
            
    /**
     * Thread-数据库连接
     *
    private static Map<Thread, Connection> connectionMap = new ConcurrentHashMap<>();   
    public static Map<Thread, Connection> getConnectionMap() {
        return connectionMap;
    } 
     /**
     * 每个thread维护一个Connection
     * @return
     *
    private Connection getConnection(){
        Thread currentThread = Thread.currentThread();
        Connection cn = null;
        if (!connectionMap.containsKey(currentThread)){
            cn = ConnectionManager.createConnection();
            connectionMap.put(currentThread, cn);
        }  else {
            cn = connectionMap.get(currentThread);
        }
        return cn;
    }
	 */
}
