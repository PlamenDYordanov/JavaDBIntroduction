import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class P08_IncreaseMinionAge extends Connectivity{
    public P08_IncreaseMinionAge(String username, String password) {
        super(username, password);
    }

    public static void main(String[] args) throws SQLException {
        Connection connection = getConnection();

        Scanner scanner = new Scanner(System.in);
        List<Integer> integerList = Arrays
                .stream(scanner.nextLine().split("\\s+"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        for (int i = 0; i < integerList.size(); i++) {
            int currentId = integerList.get(i);

            PreparedStatement minionQuery = connection.prepareStatement("""
              select * from minions where id = ?
                """);
            minionQuery.setInt(1, currentId);
            ResultSet resultSet = minionQuery.executeQuery();
            if (!resultSet.next()) {
                continue;
            }

            PreparedStatement updateQueryAge = connection.prepareStatement("""
                update minions
                set age = age - 1
                where id in (?);
                """);
            updateQueryAge.setInt(1, currentId);
            updateQueryAge.executeUpdate();

            PreparedStatement updateQueryName = connection.prepareStatement("""
                update minions
                set name = LOWER(name)
                where id in (?);
                """);
            updateQueryName.setInt(1, currentId);
            updateQueryName.executeUpdate();
        }

        PreparedStatement query = connection.prepareStatement("""
                select name, age from minions
                """);
        ResultSet output = query.executeQuery();
        while (output.next()) {
            String name = output.getString("name");
            int age = output.getInt("age");
            System.out.printf("%s %d\n", name, age);

        }
    }
}
