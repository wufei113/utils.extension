package priv.wufei.tools.database;

import java.sql.Connection;

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
     */
    Connection getConnection();

    /**
     * 获得核心池大小
     *
     * @return int
     */
    int getCorePoolSize();
}
