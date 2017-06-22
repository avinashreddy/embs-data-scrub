package crux.embs.schemagen;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

@Component
public class DataSourceLookup {

    @Value("${jdbcUrl}")
    private String url;

    @Value("${userName}")
    private String user;

    @Value("${password}")
    private String password;

    private DataSource dataSource;

    private DataSource create()  {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass("org.postgresql.Driver");
        } catch (PropertyVetoException e) {
            throw new IllegalStateException(e);
        }
        cpds.setJdbcUrl(url);
        cpds.setUser(user);
        cpds.setPassword(password);
        return cpds;
    }

    public synchronized DataSource getDataSource() {
        if(dataSource == null) {
            dataSource = create();
        }
        return dataSource;
    }
}
