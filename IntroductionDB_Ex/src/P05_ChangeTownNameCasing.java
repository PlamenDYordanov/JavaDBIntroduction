import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class P05_ChangeTownNameCasing extends Connectivity {
    public P05_ChangeTownNameCasing(String username, String password) {
        super(username, password);
    }

    public static void main(String[] args) throws SQLException {
        Connection connection = getConnection();

        PreparedStatement queryCount = connection.prepareStatement(
                """
                        select count(*) as count_towns from towns where country = ?;
                        """
        );
        queryCount.setString(1, "Italy");
        ResultSet resultSet = queryCount.executeQuery();
        resultSet.next();
        int countOfTowns = resultSet.getInt("count_towns");
        if (countOfTowns == 0) {
            System.out.println("No town names were affected.");
            return;
        }
        System.out.printf("%d town names were affected.\n",countOfTowns);


        PreparedStatement queryTowns = connection.prepareStatement(
                """
                   select * from towns where country = ?;
                        """
        );
        queryTowns.setString(1, "Bulgaria");
        ResultSet resultSetOfTowns = queryTowns.executeQuery();
        List<String> townNames = new ArrayList<>();
        while (resultSetOfTowns.next()){
            String currentTownName = resultSetOfTowns.getString("name");
            townNames.add(currentTownName);
        }
        System.out.println("[" + String.join(", ", townNames)+ "]");
    }

}
