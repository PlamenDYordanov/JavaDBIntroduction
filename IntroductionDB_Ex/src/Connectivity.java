import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public abstract class Connectivity {

    private String username;
    private String password;

    public Connectivity(String username, String password){
        this.username = "root";
        this.password = "123456";
    }

    static Connection getConnection () throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "123456");

        Connection connection = DriverManager
                .getConnection("jdbc:mysql://localhost:3306/minions_db", properties);
    return connection;
    }
}
