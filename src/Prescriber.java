import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;

public class Prescriber implements DataBaseModifierAndAccessor{
    String location = "jdbc:sqlite:InventoryManager.db";
    Connection connection = null;

    public Prescriber(){}
    public Prescriber(String location){
         this.location = location;
     }

    @Override
    public void connect() {
        try{
            connection = DriverManager.getConnection(location);
        }catch(SQLException e){
            System.out.println("can't connect to Prescriber");
        }
    }

    @Override
    public void createTable(){
        try {
            Statement tableCreator = connection.createStatement();
            tableCreator.execute("CREATE TABLE IF NOT EXISTS PrescriptionsRecords(prescriptionNumber TEXT PRIMARY KEY ,dateAndTime BIGINT, nameOfMedication TEXT, dosageForm TEXT, strength INTEGER, dose TEXT,amount INTEGER, isDispensed BOOLEAN)");
        } catch(SQLException e){
            System.out.println("can't create prescription table " + e.getMessage());
        }
    }

    public void prescribe(@NotNull ArrayList items){
        this.connect();
        this.createTable();
        for (Object prescript: items){
            Prescription prescription = (Prescription) prescript;
            insertCommand(prescription);}
        System.out.println("Prescription is completed");
        try{connection.close();}catch(SQLException e){
            System.out.println("can't close connection" + e.getMessage());
        }
    }

    public boolean isInStock(String nameOfMedication,int strength, String dosageForm ){
        boolean isInInventory = false;
        PreparedStatement isExistQuery;
        try {
                isExistQuery = connection.prepareStatement("SELECT nameOfMedication, strength, dosageForm  FROM MedicationInStock WHERE nameOfMedication = ?, strength = ?, dosageForm = ?");
                isExistQuery.setString(1, nameOfMedication);
                isExistQuery.setInt(2,strength);
                isExistQuery.setString(3,dosageForm);
                ResultSet result = isExistQuery.executeQuery();
                if(!result.isLast()) isInInventory = true;
            }catch(SQLException e){
                System.out.println("can't create statement " + e.getMessage());
            }

        System.out.println("successfully checked if medication is in inventory");
        return isInInventory;
    }

    @Override
    public ResultSet getInfoFromTable() {
        ResultSet result = null;
        try {
            this.connect();
            PreparedStatement selectTableQuery = connection.prepareStatement("SELECT prescriptionNumber, nameOfMedication, strength, dosageForm, amount FROM PrescriptionsRecords");

            result = selectTableQuery.executeQuery();
        } catch (SQLException e) {
            System.out.println("can't get info from Prescription table " + e.getMessage());
        }
        return result;
    }

    @Override
    public void insertCommand(@NotNull Medication medication){
        Prescription  prescription = (Prescription) medication;
        PreparedStatement command;
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());
        try {
            String querySet = "insert into PrescriptionsRecords values (?,?,?,?,?,?)";
            command = connection.prepareStatement(querySet);
            command.setString(2,prescription.getPrescriptionNumber());
            //System.out.println("dispenseNumber is set ");
            System.out.println("prescriptionNumbers is set");
            command.setTimestamp(1,nowTime);
            System.out.println("dateAndTime is set");
            command.setString(3,prescription.getNameOfMedication());
            System.out.println("nameOfMedication is set");
            command.setString(4,prescription.getDosageForm());
            System.out.println("dosageForm is set");
            command.setInt(5,prescription.getStrength());
            System.out.println("strength is set");
            command.setString(6,prescription.getDose());
            System.out.println("dose is set");
            command.executeUpdate();
            System.out.println("Data inserted");
        } catch(SQLException exception) {
            System.out.println(exception.getMessage());
        }

    }
}
