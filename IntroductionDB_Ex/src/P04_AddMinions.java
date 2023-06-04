import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class P04_AddMinions extends  Connectivity{

    public P04_AddMinions(String username, String password) {
        super(username, password);
    }
    static int townId = 0;
    static int villainId = 0;
    static int minionId = 0;

    public static void main(String[] args) throws SQLException {

        Connection connection = getConnection();

        Scanner scanner = new Scanner(System.in);

        String minionInput = scanner.nextLine().split(": ")[1];
        String minionName = minionInput.split((" "))[0];

        int minionAge = Integer.parseInt(minionInput.split((" "))[1]);
        String minionTown = minionInput.split((" "))[2];
        String villainName = scanner.nextLine().split(": ")[1];

        //----------- check towns
        getInsertTown(connection, minionTown);

        //----------- check villains
        getInsertVillain(connection, villainName);

        //---------- insert minions to DB
        insertMinionsToDb(connection, minionName, minionAge);

        //---------- insert minion to villain
        connectionMinionToVillain(connection);

       // print result
        System.out.printf("Successfully added %s to be minion of %s\n", minionName, villainName);


    }
    private static void connectionMinionToVillain(Connection connection) throws SQLException {
        PreparedStatement prepMinionToVillainStmt = connection.prepareStatement(
                "insert into `minions_villains`(minion_id, villain_id) values(?,?)"
        );
        prepMinionToVillainStmt.setInt(1, minionId);
        prepMinionToVillainStmt.setInt(2, villainId);
        prepMinionToVillainStmt.executeUpdate();
    }

    private static void insertMinionsToDb(Connection connection, String minionName, int minionAge) throws SQLException {
        PreparedStatement insertMinion = connection.prepareStatement(
                "insert into minions (`name`, `age`, town_id) values (?, ?, ?)");
        insertMinion.setString(1, minionName);
        insertMinion.setInt(2, minionAge);
        insertMinion.setInt(3, townId);
        insertMinion.executeUpdate();
        PreparedStatement getMinionsId = connection.prepareStatement("select id from minions where name = ?");
        getMinionsId.setString(1, minionName);
        ResultSet result = getMinionsId.executeQuery();
        result.next();
        minionId = result.getInt("id");
    }

    private static void getInsertTown(Connection connection, String minionTown) throws SQLException {
        PreparedStatement prepStmtTown = connection.prepareStatement(
                "select id from towns where name = ?;");
        prepStmtTown.setString(1, minionTown);
        ResultSet resultTown = prepStmtTown.executeQuery();

        if (!resultTown.next()) {
            PreparedStatement insertTown = connection.prepareStatement("""
                    insert into towns (name) values (?);
                    """);
            insertTown.setString(1, minionTown);

            insertTown.executeUpdate();
            ResultSet townResult = prepStmtTown.executeQuery();
            townResult.next();
            townId = townResult.getInt("id");
            System.out.printf("Town %s was added to the database.\n", minionTown);
        }else {
            townId = resultTown.getInt("id");
        }
    }

    private static void getInsertVillain(Connection connection, String villainName) throws SQLException {
        PreparedStatement prepStmtVillain = connection.prepareStatement(
                "select id from villains where name = ?;");
        prepStmtVillain.setString(1, villainName);

        ResultSet resultVillainName = prepStmtVillain.executeQuery();

        if(!resultVillainName.next()){
            PreparedStatement insertVillain = connection.prepareStatement(
                    "insert into villains (name, evilness_factor ) values(?, ?);"
            );
            insertVillain.setString(1, villainName);
            insertVillain.setString(2, "evil");

           insertVillain.executeUpdate();
           ResultSet newVillainsSet = prepStmtVillain.executeQuery();
           newVillainsSet.next();
           villainId = newVillainsSet.getInt("id");
            System.out.printf("Villain %s was added to the database.\n", villainName);
        }else {
            villainId = resultVillainName.getInt("id");
        }

    }
}
