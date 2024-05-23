import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

import org.jetbrains.annotations.*;

public class DataBaseModifierAndAccessor {
    Connection connect = null;
    String location;


    DataBaseModifierAndAccessor(String url){
        location = url;
    }

    protected void connect(){
        try{
            connect = DriverManager.getConnection(location);
            System.out.println("database connected");
        }catch(SQLException e){
           System.out.println("An error occurred "+e.getMessage());
        }
    }

    private void createTable(String action){
        if (Objects.equals(action, "Prescribe")){
        try {
            Statement tableCreator = connect.createStatement();
            tableCreator.execute("CREATE TABLE IF NOT EXISTS PrescriptionsRecords(prescriptionNumber TEXT PRIMARY KEY ,dateAndTime BIGINT, nameOfMedication TEXT, dosageForm TEXT, strength INTEGER, dose TEXT,amount INTEGER, isDispensed BOOLEAN)");
        } catch(SQLException e){
            System.out.println("can't create table " + e.getMessage());
        }
        } else if (Objects.equals(action,"Dispense")){
            try {
                Statement tableCreator = connect.createStatement();
                tableCreator.execute("CREATE TABLE IF NOT EXISTS DispenseRecords(dateAndTime BIGINT,prescriptionNumber TEXT, nameOfMedication TEXT, dosageForm TEXT, strength INTEGER, dose TEXT)");//dispensingNumber TEXT, is to be added.
            } catch(SQLException e){
                System.out.println("can't create table " + e.getMessage());
            }
        }else if (Objects.equals(action,"registerPurchase")){
           try {
               Statement tableCreator = connect.createStatement();
               tableCreator.execute("CREATE TABLE IF NOT EXISTS MedicationInStock(purchaseNumber  TEXT, medicationName TEXT )");
           }catch(SQLException e){
               System.out.println("can't create register table" + e.getMessage());
           }
        }
    }

    private void insertCommand(@NotNull Prescription prescription, String destinationTable) {
        PreparedStatement command;
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());
        if (Objects.equals(destinationTable,"Prescribe")){
            try {
                String querySet = "insert into PrescriptionsRecords values (?,?,?,?,?,?,?,?)";
                command = connect.prepareStatement(querySet);
                command.setString(1,prescription.getPrescriptionNumber());
                System.out.println("prescriptionNumbers is set");
                command.setTimestamp(2,nowTime);
                System.out.println("dateAndTime is set");
                command.setString(3,prescription.getNameOfMedication());
                System.out.println("nameOfMedication is set");
                command.setString(4,prescription.getDosageForm());
                System.out.println("dosageForm is set");
                command.setInt(5,prescription.getStrength());
                System.out.println("strength is set");
                command.setString(6,prescription.getDose());
                System.out.println("dose is set");
                command.setInt(7,prescription.getAmount());
                command.setBoolean(8,false);
                command.executeUpdate();
                System.out.println("Data inserted");
            } catch(SQLException exception) {
                System.out.println(exception.getMessage());
            }
        } else if (Objects.equals(destinationTable,"Dispense")) {
            try {
                String querySet = "insert into DispenseRecords values (?,?,?,?,?,?)";
                command = connect.prepareStatement(querySet);
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

    private boolean tableExists(String tableName){
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
    }

    public void prescribe(@NotNull ArrayList items){

            this.connect();
            this.createTable("Prescribe");
            for (Object prescript: items){
                Prescription prescription = (Prescription) prescript;
                insertCommand(prescription, "Prescribe");}
        System.out.println("Prescription is completed");
           try{connect.close();}catch(SQLException e){
               System.out.println("can't close connection" + e.getMessage());
           };
    }

    public void dispense(@NotNull ArrayList prescriptions) {
        this.connect();
        this.createTable("Dispense");
        if (!(prescriptions.isEmpty())) {
            for (Object prescript : prescriptions) {
                Prescription prescription = (Prescription) prescript;
                insertCommand(prescription, "Dispense");

            }
        }
    }

    public boolean isInInventory(String nameOfMedication,int strength, String dosageForm){
        boolean isInInventory = false;
        PreparedStatement isExistQuery;
        if(tableExists("AvailableMedications")) {
            try {
                isExistQuery = connect.prepareStatement("SELECT nameOfMedication, strength, dosageForm  FROM store WHERE nameOfMedication = ?, strength = ?, dosageForm = ?");
                isExistQuery.setString(1, nameOfMedication);
                isExistQuery.setInt(2,strength);
                isExistQuery.setString(3,dosageForm);
                ResultSet result = isExistQuery.executeQuery();
                if(!result.isLast()) isInInventory = true;
            }catch(SQLException e){
                System.out.println("can't create statement " + e.getMessage());
            }
        }
        System.out.println("successfully checked if medication is in inventory");
        return isInInventory;
    }

    public ResultSet getInfoFromTable(String nameOfTable){
        ResultSet result = null;
        if(Objects.equals(nameOfTable, "PrescriptionsRecords")){
            try {
                this.connect();
                PreparedStatement selectTableQuery = connect.prepareStatement("SELECT prescriptionNumber, nameOfMedication, strength, dosageForm, amount FROM PrescriptionsRecords");
//            selectTableQuery.setString(1,nameOfTable);
                result = selectTableQuery.executeQuery();
            }catch (SQLException e){
                System.out.println("can't get info from Prescription table " + e.getMessage());
            }
        } else if (Objects.equals(nameOfTable, "DispenseRecords")) {
            try {
                PreparedStatement selectTableQuery = connect.prepareStatement("SELECT prescriptionNumber, nameOfMedication, strength, dosageForm FROM DispenseRecords ");
//            selectTableQuery.setString(1,nameOfTable);
                result = selectTableQuery.executeQuery();
            }catch (SQLException e){
                System.out.println("can't get info from DispenseRecords table " + e.getMessage());
            }
        }
        return result;
    }
}
