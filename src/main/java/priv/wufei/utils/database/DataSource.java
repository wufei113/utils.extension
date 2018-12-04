package priv.wufei.utils.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库数据源
 *
 * @author WuFei
 */
public interface DataSource extends AutoCloseable {

    /**
     * 得到一个连接
     *
     * @return {@link Connection}
     * @throws SQLException SQLException
     */
    Connection getConnection() throws SQLException;

    /**
     * 获得核心池大小
     *
     * @return int
     */
    int getCorePoolSize();
}
