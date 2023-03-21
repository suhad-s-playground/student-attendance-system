package lk.ijse.dep10.sas;

import javafx.application.Application;
import javafx.stage.Stage;
import lk.ijse.dep10.sas.db.DBConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Set;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (DBConnection.getInstance().getConnection() !=
                        null && !DBConnection.getInstance().getConnection().isClosed()) {
                    System.out.println("Database is about to close ");
                    DBConnection.getInstance().getConnection().close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
//        if(!checkDatabases()) return;
//        if (checkDatabases()) {
//            System.out.println("hi");
//        }
        generateSchemaIfNotExist();

    }

    private void generateSchemaIfNotExist() {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SHOW TABLES ");

            ArrayList<String> tableNameList = new ArrayList<>();

            while (rst.next()) {
                tableNameList.add(rst.getString(1));
            }
            boolean tableExits = tableNameList.containsAll(Set.of("Attendance",
                    "User", "Student", "Picture"));//--> set ekak hadagnna puluwn set .of ethod eken

            if (!tableExits) {


                stm.execute(readDBScript());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String readDBScript() {

        InputStream is = getClass().getResourceAsStream("/schema.sql");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))){
            String line;
            StringBuilder dbScriptBuilder = new StringBuilder();

            while ((line = br.readLine()) != null) {
                dbScriptBuilder.append(line);
            }

            return dbScriptBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

//    private boolean checkDatabases() {
//        Connection connection = DBConnection.getInstance().getConnection();
//        int tableCount = 0;
//        try {
//            Statement stm = connection.createStatement();
//            ResultSet rst = stm.executeQuery("SHOW TABLES");
//
//            while (rst.next()) {
//                ++tableCount;
//            }
//            if (tableCount == 4) {
//                return true;
//            }
//
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return false;
//
//    }
}
