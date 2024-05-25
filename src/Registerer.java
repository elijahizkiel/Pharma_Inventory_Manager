import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;

public class Registerer implements DataBaseModifierAndAccessor{
    Connection connection = null;
    String location = "jdbc:sqlite:.InventoryManager.db";
    public void connect(){
        try{
            connection = DriverManager.getConnection(location);
        }catch(SQLException e){
            System.out.println("failed to connect Database" + e.getMessage());
        }
    }
    @Override
    public void createTable(){
        try {
            Statement tableCreator = connection.createStatement();
            tableCreator.execute("CREATE TABLE IF NOT EXISTS MedicationInStock(purchaseNumber  TEXT, nameOfMedication TEXT,strength INTEGER, dosageForm TEXT,amount INTEGER, expireDate BIGINT)");
        }catch(SQLException e){
            System.out.println("can't create register table" + e.getMessage());
        }
    }

    @Override
    public void insertCommand(@NotNull Medication medication) {
        PreparedStatement command;
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());
        try {
            String querySet = "insert into PrescriptionsRecords values (?,?,?,?,?,?)";
            command = connection.prepareStatement(querySet);
            System.out.println("prescriptionNumbers is set");
            command.setString(1,nowTime.toString());
            System.out.println("dateAndTime is set");
            command.setString(2,medication.getNameOfMedication());
            System.out.println("nameOfMedication is set");
            command.setString(4,medication.getDosageForm());
            System.out.println("dosageForm is set");
            command.setInt(3,medication.getStrength());
            System.out.println("strength is set");
            command.setTimestamp(6,(Timestamp) medication.getExpireDate());
            System.out.println("dose is set");
            command.executeUpdate();
            System.out.println("Data inserted");
        } catch(SQLException exception) {
            System.out.println(exception.getMessage());
        }

    }

    public void register(@NotNull ArrayList<Medication> medications){
        for (Medication medication : medications) {
            insertCommand(medication);
        }
    }
    public void register(@NotNull Medication medication){
        insertCommand(medication);
    }
    @Override
    public  ResultSet getInfoFromTable() {
        ResultSet resultSet = null;
        try{
            this.connect();
            Statement queryStatement = connection.createStatement();
            resultSet = queryStatement.executeQuery("SELECT nameOfMedication, strength, dosageForm FROM MedicationInStock");
        }catch(SQLException e){
            System.out.println("can't get info from MedicationInStock table " + e.getMessage());
        }
        return resultSet;
    }


}
