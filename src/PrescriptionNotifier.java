/*
*
* Sends notification if a prescription object is added to PrescriptionRecords table  */

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Callable;
import java.sql.*;


public class PrescriptionNotifier implements Callable {
    Connection connection = null;
    String location;

    PrescriptionNotifier(){
        location = "jdbc:sqlite:.InventoryManager.db";
    }
    PrescriptionNotifier(String location){
        this.location = location;
    }
    private void connect(){
        try{
            connection = DriverManager.getConnection(location);
        }catch (SQLException e){
            System.out.println("PrescriptionNotifier can't connect to Database " + e.getMessage());
        }
    }

    private ResultSet getTable(){
        ResultSet result = null;
        try{
            Statement statement = connection.createStatement();
             result = statement.executeQuery("SELECT * FROM PrescriptionsRecords");

        }catch(SQLException e){
            System.out.println("can't get PrescriptionsRecords table " + e.getMessage());
        }
        return result;
    }

    private ResultSet getTable(long time){
        ResultSet result = null;
        try{
            Statement statement = connection.createStatement();
            result = statement.executeQuery("SELECT * FROM PrescriptionsRecords WHERE dateAndTime >= " + time + ";");

        }catch(SQLException e){
            System.out.println("can't get PrescriptionsRecords table " + e.getMessage());
        }
        return result;
    }

    private long getLastRowPointer(){
        long dateAndTime = 0;
        this.connect();
        this.sortDataInTable();
        ResultSet result = this.getTable();
        try{
            if(result.isFirst()){
                dateAndTime = result.getLong("dateAndTime");}
        }catch (SQLException e){
            System.out.println("cant get dateAndTime from PrescriptionsRecords " + e.getMessage());
        }
        return dateAndTime;
    }

    @NotNull
    private Map getPrescriptions(){
        Map<String, Medication> prescriptions = new HashMap<>();
        ResultSet newPrescriptions = this.getTable();
        try{
            while(newPrescriptions.next()){
                Prescription prescription = new Prescription(newPrescriptions.getString("prescriptionNumber"),
                        newPrescriptions.getString("nameOfMedication"),newPrescriptions.getInt("strength"),
                        newPrescriptions.getString("dosageForm"),newPrescriptions.getString("dose"),
                        newPrescriptions.getInt("amount"));
                prescriptions.put(prescription.getPrescriptionNumber(), prescription);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return prescriptions;
    }

    private void sortDataInTable(){
        try{
            Statement statement = connection.createStatement();
            statement.execute("SELECT dateAndTime FROM PrescriptionsRecords ORDER BY dateAndTime DSC");
        }catch (SQLException e){
            System.out.println("can't sort data in table " + e.getMessage());
        }
    }

    public ArrayList call(){
        ArrayList<String> prescriptionStrings = new ArrayList<>();
        Map<String,Prescription> prescriptions=(HashMap)this.getPrescriptions();
        for (Map.Entry<String,Prescription> prescription:prescriptions.entrySet()){
            Prescription prescription1 = prescription.getValue();
            prescriptionStrings.add(prescription1.toString());
        }
        return prescriptionStrings;
    };
}
