import org.jetbrains.annotations.NotNull;

import java.sql.*;

public interface DataBaseModifierAndAccessor {


         void connect();
        void createTable();

        void insertCommand(@NotNull Medication medication);

        ResultSet getInfoFromTable();

}
