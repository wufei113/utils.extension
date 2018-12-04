package priv.wufei.utils.database;

import priv.wufei.utils.basis.PropertiesUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;

/**
 * H2数据库数据源
 *
 * @author WuFei
 */
public final class H2DatabaseSource implements DataSource {

    /**
     * 创建单例对象
     */
    private static volatile H2DatabaseSource h2DatabaseSource;
    /**
     * 驱动类
     */
    private static String driverClassName;
    /**
     * url
     */
    private static String url;
    /**
     * 用户
     */
    private static String username;
    /**
     * 密码
     */
    private static String password;
    /**
     * 核心池大小
     */
    private static int corePoolSize;
    /**
     * 连接池
     */
    private static LinkedList<Connection> pool = new LinkedList<>();

    /*初始化数据*/
    static {
        Properties prop = null;

        try {
            prop = PropertiesUtils.loadProperties(H2DatabaseSource.class, "/priv/wufei/utils/database/h2-config.properties");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert prop != null;

        driverClassName = PropertiesUtils.getString(prop, "driverClassName");
        url = PropertiesUtils.getString(prop, "url");
        username = PropertiesUtils.getString(prop, "username");
        password = PropertiesUtils.getString(prop, "password");
        corePoolSize = PropertiesUtils.getInt(prop, "corePoolSize");
    }

    /**
     * 单例模式
     *
     * @throws Exception Exception
     */
    private H2DatabaseSource() throws Exception {
        //加载驱动
        Class.forName(driverClassName);
        //初始化数据池
        for (int i = 0; i < corePoolSize; i++) {

            createConnection();
        }
    }

    /**
     * 获得{@link H2DatabaseSource}单例对象
     *
     * @return {@link H2DatabaseSource}
     * @throws Exception Exception
     */
    public static H2DatabaseSource getInstance() throws Exception {

        if (h2DatabaseSource == null) {

            synchronized (H2DatabaseSource.class) {

                if (h2DatabaseSource == null) {

                    h2DatabaseSource = new H2DatabaseSource();
                }
            }
        }
        return h2DatabaseSource;
    }

    /**
     * 创建连接
     *
     * @throws SQLException SQLException
     */
    private void createConnection() throws SQLException {

        //获取连接
        Connection conn = DriverManager.getConnection(url, username, password);
        //进行装饰
        ConnectionWrapper wrapper = new ConnectionWrapper(conn, pool, this);

        pool.add(wrapper);
    }

    /**
     * 获取连接
     *
     * @return {@link Connection}
     * @throws SQLException SQLException
     */
    @Override
    public Connection getConnection() throws SQLException {

        Connection conn;

        if (pool == null || pool.size() <= 0) {

            createConnection();
        }
        conn = pool.removeFirst();

        return conn;
    }

    /**
     * 关闭连接池
     *
     * @throws SQLException SQLException
     */
    @Override
    public void close() throws SQLException {

        int size = pool.size();

        for (int i = 0; i < size; i++) {

            Connection conn = pool.removeFirst();

            ConnectionWrapper wrapper = (ConnectionWrapper) conn;

            if (!wrapper.isClosed()) {

                wrapper.closeConnection();
            }
        }
    }

    /**
     * 获取核心池大小
     *
     * @return int
     */
    @Override
    public int getCorePoolSize() {

        return corePoolSize;
    }

}
