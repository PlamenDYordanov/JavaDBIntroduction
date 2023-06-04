import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class P09_InsertAgeStoredProcedure extends Connectivity{
    public P09_InsertAgeStoredProcedure(String username, String password) {
        super(username, password);
    }

    public static void main(String[] args) throws SQLException {
        Connection connection = getConnection();

        Scanner scanner = new Scanner(System.in);
        int minionId = Integer.parseInt(scanner.nextLine());

        PreparedStatement increaseAgeQuery = connection.prepareStatement("""
            call usp_get_older(?);
            
""");
        increaseAgeQuery.setInt(1, minionId);
        increaseAgeQuery.executeUpdate();

        PreparedStatement increasedMinionAge = connection.prepareStatement("""
            select name, age from minions where id = ?
            
""");
        increasedMinionAge.setInt(1, minionId);
        ResultSet resultSet = increasedMinionAge.executeQuery();
        resultSet.next();
        System.out.printf("%s / %d\n",resultSet.getString("name"), resultSet.getInt("age"));

    }
}
