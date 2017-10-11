package modules;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

/**
 * Created by RXLiuli on 2017/8/16.
 * modules.DaoExtension 数据库扩展连接类(可组合 / 继承)
 */
public abstract class DaoExtension<T> {
    /**
     * 获取一个 Connection 数据库连接实例
     *
     * @return 数据库连接实例(可能为 null)
     */
    public static Connection getConn() {
        Connection conn = null;
        try {
            Class.forName(driverClass);
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            logger.info(e);
        }
        return conn;
    }

    /**
     * 更新方法
     *
     * @param sql     预编译的 sql 语句
     * @param objects 填充参数
     * @return 更新影响的行数
     */
    public int update(String sql, Object... objects) {
        return update(thisConn, sql, objects);
    }

    /**
     * 更新方法
     *
     * @param conn    数据库连接实例
     * @param sql     预编译的 sql 语句
     * @param objects 填充参数
     * @return 更新影响的行数
     */
    public int update(Connection conn, String sql, Object... objects) {
        PreparedStatement ps = null;
        int num = -1;
        try {
            ps = conn.prepareStatement(sql);
            fillParse(ps, objects);
            num = ps.executeUpdate();
        } catch (SQLException e) {
            logger.info(e);
        } finally {
            closeAll(conn == thisConn ? null : conn, ps, null);
        }
        return num;
    }

    /**
     * 使用存储过程进行多条 sql 语句的增删改(可以正确返回受影响行数)
     *
     * @param map 包含多条要执行的 sql 语句以及与之对应的填充参数
     * @return 真实的受影响行数, 如果执行失败返回 -1
     */
    public int updateMatter(Map<String, Object[]> map) {
        return updateMatter(thisConn, map);
    }

    /**
     * 使用存储过程进行多条 sql 语句的增删改(可以正确返回受影响行数)
     *
     * @param conn 数据库连接
     * @param map  包含多条要执行的 sql 语句以及与之对应的填充参数
     * @return 真实的受影响行数, 如果执行失败返回 -1
     */
    public int updateMatter(Connection conn, Map<String, Object[]> map) {
        PreparedStatement ps = null;
        int num = 0;
        Set<String> set = map.keySet();
        try {
            conn.setAutoCommit(false);
            for (String sql : set) {
                ps = conn.prepareStatement(sql);
                Object[] objects = map.get(sql);
                if (objects != null) fillParse(ps, objects);
                num += ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            num = -1;
            try {
                conn.rollback();
            } catch (SQLException e1) {
                logger.info(e1);
            }
            logger.info(e);
        } finally {
            closeAll(conn == thisConn ? null : conn, ps, null);
        }
        return num;
    }

    /**
     * 查询方法
     *
     * @param sql     进行预编译的 sql 语句
     * @param objects 填充参数
     * @return 泛型集合
     */
    public List<T> query(String sql, Object... objects) {
        return query(thisConn, sql, objects);
    }

    /**
     * 查询方法
     *
     * @param conn    数据库连接实例
     * @param sql     进行预编译的 sql 语句
     * @param objects 填充参数
     * @return 泛型集合
     */
    public List<T> query(Connection conn, String sql, Object... objects) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<T> list = new ArrayList<>();
        try {
            ps = conn.prepareStatement(sql);
            fillParse(ps, objects);
            rs = ps.executeQuery();
            rsToList(rs, list);
        } catch (SQLException e) {
            logger.info(e);
            e.printStackTrace();
        } finally {
            closeAll(conn == thisConn ? null : conn, ps, rs);
        }
        return list;
    }

    /**
     * 查询单个值
     *
     * @param sql     进行预编译的 sql 语句
     * @param objects 填充参数
     * @return 查询到的单个值, 或者 null
     */
    public Object single(String sql, Object... objects) {
        return single(thisConn, sql, objects);
    }

    /**
     * 查询单个值
     *
     * @param conn    数据库连接实例
     * @param sql     进行预编译的 sql 语句
     * @param objects 填充参数
     * @return 查询到的单个值, 或者 null
     */
    public Object single(Connection conn, String sql, Object... objects) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Object obj = null;
        try {
            ps = conn.prepareStatement(sql);
            fillParse(ps, objects);
            rs = ps.executeQuery();
            if (rs.next()) obj = rs.getObject(1);
        } catch (SQLException e) {
            logger.info(e);
        } finally {
            closeAll(conn == thisConn ? null : conn, ps, rs);
        }
        return obj;
    }

    //region 辅助方法(基本不会用到这些内容)

    //记录日志的 log4j 的实例(可重写)
    private static Logger logger = Logger.getLogger(DaoExtension.class.getName());
    private static String driverClass;
    private static String url;
    private static String username;
    private static String password;

    /**
     * 初始化数据库连接字符串
     */
    static {
        connStrInit();
    }


    //默认的 Connection 数据库连接实例(必须在初始化连接参数之后才能使用!!!)
    private static Connection thisConn = getConn();

    /**
     * 从配置文件 db.properties 中读取数据库连接信息
     */
    private static void connStrInit() {
        Properties prop = new Properties();
        InputStream in = DaoExtension.class.getClassLoader().getResourceAsStream("db.properties");
        try {
            prop.load(in);
            driverClass = prop.getProperty("driverClass");
            url = prop.getProperty("url");
            username = prop.getProperty("username");
            password = prop.getProperty("password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 转换方法(将 ResultSet 结果集中的值转化为 List 集合)
     *
     * @param rs 将 ResultSet 结果集转换为 List<T>
     */
    private void rsToList(ResultSet rs, List<T> list) throws SQLException {
        while (rs.next()) list.add(rsToInstance(rs));
    }

    /**
     * 转换方法(转化一次实例)
     *
     * @param rs 要进行转化的的 ResultSet 结果集
     * @return 转换到的实例
     */
    public abstract T rsToInstance(ResultSet rs) throws SQLException;

    /**
     * 填充 PreparedStatement
     *
     * @param ps   要进行填充的 PreparedStatement 实例
     * @param objs 填充参数
     */
    private void fillParse(PreparedStatement ps, Object... objs) {
        for (int i = 0; i < objs.length; i++) {
            try {
                ps.setObject(i + 1, objs[i]);
            } catch (SQLException e) {
                logger.info(e);
            }
        }
    }

    /**
     * 释放数据库资源
     *
     * @param conn Connection 连接
     * @param stmt Statement 对象
     * @param rs   ResultSet 结果集
     */
    private void closeAll(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            logger.info(e);
        }
        if (stmt != null) try {
            stmt.close();
        } catch (SQLException e) {
            logger.info(e);
        }
        if (conn != null) try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //endregion

}
