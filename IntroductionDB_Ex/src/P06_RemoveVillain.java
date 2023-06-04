import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class P06_RemoveVillain extends Connectivity {
    static  int countOfMinions;
    static  String villainName;
    public P06_RemoveVillain(String username, String password) {
        super(username, password);
    }

    public static void main(String[] args) throws SQLException {

        Scanner scanner = new Scanner(System.in);
        int deleteVillainId = Integer.parseInt(scanner.nextLine());

        Connection connection = getConnection();


        PreparedStatement checkVillain = connection.prepareStatement(
                """
                        SELECT name from villains WHERE id = ?;
                        """
        );
        checkVillain.setInt(1, deleteVillainId);
        ResultSet villainSet = checkVillain.executeQuery();
        if(!villainSet.next()) {
            System.out.println("No such villain was found");
            return;
        }
        PreparedStatement queryCountAndVillainName = connection.prepareStatement(
                """
                        select count(mv.minion_id) as count_minions, v.name from minions_villains as mv
                        join villains v on mv.villain_id = v.id
                        where v.id = ?;
                        """
        );
        queryCountAndVillainName.setInt(1, deleteVillainId);
        ResultSet resultCountName = queryCountAndVillainName.executeQuery();

        resultCountName.next();
        countOfMinions = resultCountName.getInt("count_minions");
        villainName = resultCountName.getString("name");


        connection.setAutoCommit(false);
        try {
            PreparedStatement deleteMV = connection.prepareStatement(
                    """
                            delete mv from minions_villains as mv
                            where mv.villain_id = ?;
                            """
            );
            deleteMV.setInt(1, deleteVillainId);
            deleteMV.executeUpdate();

            PreparedStatement deleteV = connection.prepareStatement(
                    """
                            delete v from villains as v
                            where v.id = ?;
    
                            """
            );
            deleteV.setInt(1,deleteVillainId);
            deleteV.executeUpdate();

            connection.commit();
            System.out.printf("%s was deleted\n", villainName);
            System.out.printf("%d minions released\n", countOfMinions);
        }catch (SQLException e) {
        connection.rollback();
        }

    }
}
