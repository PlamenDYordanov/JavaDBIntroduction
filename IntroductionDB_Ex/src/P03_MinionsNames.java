import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class P03_MinionsNames extends Connectivity {
    public P03_MinionsNames(String username, String password) {
        super(username, password);
    }

    //    Write a program that prints on the console all MINIONS NAMES and their AGE for given villain id.
//    For the output, use the formats given in the examples.
    public static void main(String[] args) throws SQLException {
        Connection connection = getConnection();

        Scanner scanner = new Scanner(System.in);
        int villainId = Integer.parseInt(scanner.nextLine());
        PreparedStatement villainQuery = connection.prepareStatement(
                "select name from villains where id = ?"
        );

        villainQuery.setInt(1,villainId);

        ResultSet villainExistResult = villainQuery.executeQuery();

        if(!villainExistResult.next()) {
            System.out.printf("No villain with ID %d exists in the database.", villainId);
            return;
        }

        PreparedStatement query = connection.prepareStatement("""
                select distinct m.`name`, m.`age` from `villains` as v
                left join minions_villains as mv on mv.villain_id = v.id
                join minions as m on mv.minion_id = m.id
                where v.id = ?;
                """);
        query.setInt(1,villainId);

        ResultSet result = query.executeQuery();


        String villainName = villainExistResult.getString("name");
        System.out.printf("Villain: %s\n",villainName);
        for (int i = 1; result.next(); i++) {
            String minionsName = result.getString("name");
            int minionsAge = result.getInt("age");
            System.out.printf("%d. %s %d\n", i, minionsName, minionsAge);
        }



    }
}
