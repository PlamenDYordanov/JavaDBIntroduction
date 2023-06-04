import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;

public class P07_PrintAllMinionNames extends Connectivity{

    public P07_PrintAllMinionNames(String username, String password) {
        super(username, password);
    }

    public static void main(String[] args) throws SQLException {
        Connection connection = getConnection();

        PreparedStatement getAllMinions = connection.prepareStatement("""
                select name from minions as m;
                """);
        ResultSet resultSet = getAllMinions.executeQuery();
        ArrayDeque<String> nameOfMinions = new ArrayDeque<>();
        while (resultSet.next()) {
            nameOfMinions.add(resultSet.getString("name"));
        }
        for (int i = 0; i < nameOfMinions.size(); i++) {
            System.out.println(nameOfMinions.pop());
            System.out.println(nameOfMinions.poll());
        }
    }
}
