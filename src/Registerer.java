import org.jetbrains.annotations.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Registerer implements DataBaseModifierAndAccessor {
    Connection connection = null;
    String location;

    public Registerer() {
        location = "jdbc:sqlite:.InventoryManager.db";
    }

    public Registerer(String location) {
        this.location = location;
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection(location);
        } catch (SQLException e) {
            System.out.println("failed to connect Database" + e.getMessage());
        }
    }

    @Override
    public void createTable() {
        try {
            Statement tableCreator = connection.createStatement();
            tableCreator.execute("CREATE TABLE IF NOT EXISTS MedicationInStock(purchaseNumber  TEXT, " +
                    "nameOfMedication TEXT,strength INTEGER, dosageForm TEXT,amount INTEGER, expireDate BIGINT)");
        } catch (SQLException e) {
            System.out.println("can't create register table" + e.getMessage());
        }
    }

    @Override
    public void insertCommand(@NotNull Medication medication1) {
        PreparedStatement command;
        NewlyPurchasedDrug medication = (NewlyPurchasedDrug) medication1;
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());
        try {
            String querySet = "INSERT INTO MedicationInStock values (?,?,?,?,?,?)";
            command = connection.prepareStatement(querySet);
            command.setString(1, nowTime.toString());
            System.out.println("purchase number is set");
            command.setString(2, medication.getNameOfMedication());
            System.out.println("nameOfMedication is set");
            command.setString(4, medication.getDosageForm());
            System.out.println("dosageForm is set");
            command.setInt(3, medication.getStrength());
            System.out.println("strength is set");
            command.setInt(5, medication.getAmount());
            System.out.println("Amount is set");
            command.setTimestamp(6, new Timestamp(medication.getExpireDate().getTime()));
            System.out.println("Expire date  is set");
            command.executeUpdate();
            System.out.println("Data inserted");
        } catch (SQLException exception) {
            System.out.println("from registerer can't add data to table " + exception.getMessage());
        }

    }

    public void register(@NotNull ArrayList<Medication> medications) {
        for (Medication medication : medications) {
            register(medication);
        }
    }

    public void register(@NotNull Medication medication) {
        this.connect();
        this.createTable();
        insertCommand(medication);
    }

    @Override
    public ResultSet getInfoFromTable() {
        ResultSet resultSet = null;
        try {
            this.connect();
            Statement queryStatement = connection.createStatement();
            resultSet = queryStatement.executeQuery("SELECT nameOfMedication, strength, dosageForm FROM MedicationInStock");
        } catch (SQLException e) {
            System.out.println("can't get info from MedicationInStock table " + e.getMessage());
        }
        return resultSet;
    }

    public @NotNull Object[][] getMedsInCount() {
        ResultSet medsInCount = null;
        this.connect();
        try {
            Statement run = connection.createStatement();
            medsInCount = run.executeQuery("SELECT nameOfMedication,dosageForm,strength,sum(amount)" +
                    " FROM MedicationInStock GROUP BY nameOfMedication, dosageForm, strength ORDER By sum(amount) DESC;");
        } catch (SQLException e) {
            System.out.println("can't get medsInCount table " + e.getMessage());
        }
        assert medsInCount != null;
        return formTable(medsInCount);
    }

    Object[][] formTable(@NotNull ResultSet tableData) {
        ArrayList<Object[]> tableData2 = new ArrayList<>();
        int rowNumb = 1;

        try {
            // Check if the result set is empty before processing
            if (!tableData.next()) {
                System.out.println("The table is empty.");
                return new Object[5][4]; // Return default empty table
            }

            // Process the first row (since tableData.next() was already called)
            do {
                Object[] rowData = {
                        rowNumb++,
                        tableData.getString("nameOfMedication"),
                        tableData.getString("dosageForm"),
                        tableData.getInt("strength"),
                        tableData.getInt(4)  // Assuming column 4 is an int
                };
                tableData2.add(rowData);
                System.out.println("rowData: " + Arrays.toString(rowData));
            } while (tableData.next());

        } catch (SQLException e) {
            System.out.println("can't form medsInCount table " + e.getMessage());
        }

        System.out.println("size of tableData2 array = " + tableData2.size());
        Object[][] table = new Object[tableData2.size()][];
        for (int i = 0; i < tableData2.size(); ++i) {
            System.out.println(Arrays.toString(tableData2.get(i)));
            table[i] = tableData2.get(i);
        }
        System.out.println("table\n" + Arrays.deepToString(table));

        // Return the table if it's not empty, otherwise return the default table
        return table.length > 0 ? table : new Object[5][4];
    }

    public Object[][] medsInLast7days() {
        long now = System.currentTimeMillis();
        ResultSet medsInLast7Days = null;
        this.connect();
        try {
            Statement query = connection.createStatement();
            medsInLast7Days = query.executeQuery("SELECT nameOfMedication, dosageForm, strength, count(*) FROM DispenseRecords Group By nameOfMedication, dosageForm, strength HAVING dateAndTime >= (" + now + "-640800000)");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(medsInLast7Days);

        if(medsInLast7Days!= null)return formTable(medsInLast7Days);
        else return new Object[4][5];
    }

    public Object[][] getMedsInShortage() {
        this.connect();
        ResultSet medsInShortage = null;
        try {
            Statement query = connection.createStatement();
            medsInShortage = query.executeQuery("SELECT nameOfMedication, dosageForm, strength, sum(amount)" +
                    " FROM MedicationInStock GROUP BY nameOfMedication,dosageForm, strength HAVING sum(amount) <= 50;");
        } catch (SQLException e) {
            System.out.println("can't get medsInShortage " + e.getMessage());
        }
        if (medsInShortage != null) return formTable(medsInShortage);
        else return new Object[5][4];
    }
}
