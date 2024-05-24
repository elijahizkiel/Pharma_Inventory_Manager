import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;

public class Dispenser implements DataBaseModifierAndAccessor{
    Connection connection = null;
    String location = "jdbc:sqlite:.InventoryManager.db";
    Dispenser(){}
    Dispenser(String location){
        this.location = location;
    }
    @Override
    public void connect() {
        try{
            connection = DriverManager.getConnection(location);
        }catch (SQLException e){
            System.out.println("can't connect to database from reporter " + e.getMessage());
        }
    }

    @Override
    public void createTable(){
        try {
            Statement tableCreator = connection.createStatement();
            tableCreator.execute("CREATE TABLE IF NOT EXISTS DispenseRecords(dateAndTime BIGINT,prescriptionNumber TEXT, nameOfMedication TEXT, dosageForm TEXT, strength INTEGER, dose TEXT)");//dispensingNumber TEXT, is to be added.
        } catch(SQLException e){
            System.out.println("can't create table " + e.getMessage());
        }
    }

    @Override
    public void insertCommand(@NotNull Medication medication) {
        Prescription  prescription = (Prescription) medication;
        PreparedStatement command;
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());
        try {
            String querySet = "insert into DispenseRecords values (?,?,?,?,?,?)";
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


    public void dispense(@NotNull ArrayList prescriptions){
        this.connect();
        this.createTable();
        if (!(prescriptions.isEmpty())) {
            for (Object prescript : prescriptions) {
                Prescription prescription = (Prescription) prescript;
                insertCommand(prescription);
            }
        }
    }

    @Override
    public ResultSet getInfoFromTable(){
        ResultSet result = null;
        try {
            PreparedStatement selectTableQuery = connection.prepareStatement("SELECT prescriptionNumber, nameOfMedication, strength, dosageForm FROM DispenseRecords ");
//            selectTableQuery.setString(1,nameOfTable);
            result = selectTableQuery.executeQuery();
        }catch (SQLException e){
            System.out.println("can't get info from DispenseRecords table " + e.getMessage());
        }
        return result;
    }
}
