import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class Disposer implements DataBaseModifierAndAccessor{
    String location;
    Connection connection;
    Disposer(){
        location = "jdbc:sqlite:.Inventory.db";
    }
    Disposer(String location){
        this.location = location;
    }

    @Override
    public void connect() {
        try{
            if(connection == null) connection = DriverManager.getConnection(location);
        } catch(SQLException ex){
            System.out.println("from Disposer can't create connection" + ex.getMessage());
        }
    }

    @Override
    public void createTable() {
        try{
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXIST DisposedMedications(purchaseNumber TEXT, disposingNumber TEXT, " +
                    "nameOfMedication TEXT, strength INTEGER, dosageForm TEXT, expireDate TEXT, amount INTEGER, reasonForDisposal TEXT,)");
        }catch(SQLException exception){
            System.out.println("from Disposer can't create table " + exception.getMessage());
        }
    }

    @Override
    public void insertCommand(@NotNull Medication medication) {
        this.connect();
        DisposedMed med = (DisposedMed) medication;
        String disposingNumber = new Date(System.currentTimeMillis()).toString();
        try {
            PreparedStatement insertCommand = connection.prepareStatement("INSERT INTO DisposedMedications VALUES(?, ?, ?, ?, ?, ?, ?, ?");

            insertCommand.setString(1, med.getPurchaseNumber());
            insertCommand.setString(2, disposingNumber);
            insertCommand.setString(3, med.getNameOfMedication());
            insertCommand.setInt(4, med.getStrength());
            insertCommand.setString(5, med.getDosageForm());
            insertCommand.setTimestamp(6, (Timestamp) med.getExpireDate());
            insertCommand.setInt(7,med.getAmount());
            insertCommand.setString(8, med.getReasonToDispose());

        }catch (SQLException ex){
            System.out.println("from disposer can't insert data to table" + ex.getMessage());
        }
    }

    @Override
    public ResultSet getInfoFromTable() {
        return null;
    }

    private void removeFromInventory(Medication medication){
        this.connect();
        DisposedMed med = (DisposedMed) medication;
        try{
            Statement deleteStatement = connection.createStatement();
            deleteStatement.execute("DELETE FROM MedicationInStock WHERE nameOfMedication = " + med.getNameOfMedication()
                    + ", strength = " +med.getStrength() +", expireDate = " + med.getExpireDate() +
                    "purchaseNumber = " + med.getPurchaseNumber() +" ,dosageForm = " + med.getDosageForm());
        }catch(SQLException e){
            System.out.println("from Disposer can't delete medication " + e.getMessage());
        }
    }
    public void dispose(DisposedMed med){
        this.connect();
        this.insertCommand(med);
        removeFromInventory(med);
    }
}
