import java.sql.*;
import java.util.Properties;
import java.util.Scanner;


public class P02_VillainsNames extends Connectivity {


    public P02_VillainsNames(String username, String password) throws SQLException {
        super(username, password);
    }

    public static void main(String[] args) throws SQLException {
        Connection connection = getConnection();

        Scanner scanner = new Scanner(System.in);
        int minionsCount = Integer.parseInt(scanner.nextLine());

        PreparedStatement query = connection.prepareStatement("""
                select name, count(distinct mv.minion_id) as count_minions from villains as v
                join minions_villains mv on v.id = mv.villain_id
                group by v.id
                having  count_minions > ?
                order by count_minions desc;
                """);

        query.setInt(1, minionsCount);

        ResultSet result = query.executeQuery();

        while (result.next()) {
            String nameOfVillains = result.getString("name");
            int countOfMinions = result.getInt("count_minions");
            System.out.printf("%s %d", nameOfVillains, countOfMinions);
            System.out.println();
        }
    }
}




