import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public interface DataBaseModifierAndAccessor {


         void connect();
        void createTable();

        void insertCommand(@NotNull Medication medication);

        /**boolean tableExists(String tableName){
            boolean doesExist = false;
            try {
                this.connect();
                DatabaseMetaData databaseMetaData = connect.getMetaData();
                ResultSet resultSet = databaseMetaData.getTables(null,null,tableName,null);
                if(resultSet.next())doesExist = true;
            }catch (SQLException e){
                System.out.println("unable to get metaData" + e.getMessage());
            }
            return doesExist;
        }*/



        ResultSet getInfoFromTable();

}
