/*
*
* Sends notification if a prescription object is added to PrescriptionRecords table  */

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.sql.*;


public class PrescriptionNotifier implements Callable<ArrayList<String>>, Serializable {
    Connection connection = null;
    String location;
    long oldLastRowPointer;
    transient long newLastRowPointer;
    Prescriber prescriber = new Prescriber(location);

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
        this.connect();
        prescriber.createTable();
        ResultSet result = null;
        try{
            Statement statement = connection.createStatement();
             result = statement.executeQuery("SELECT * FROM PrescriptionsRecords");

        }catch(SQLException e){
            System.out.println("can't get PrescriptionsRecords table from Notifier " + e.getMessage());
        }
        return result;
    }

    private ResultSet getTable(long time){
        ResultSet result = null;
        this.connect();
        prescriber.createTable();
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
        if(result != null) System.out.println("we got the data table");
        try{
            if(result.isFirst()){
                dateAndTime = result.getLong("dateAndTime");}
        }catch (SQLException e){
            System.out.println("cant get dateAndTime from PrescriptionsRecords " + e.getMessage());
        }
        return dateAndTime;
    }


    private @NotNull Map<String, Medication> getPrescriptions(){
        Map<String, Medication> prescriptions = new HashMap<>();
         ResultSet newPrescriptions = this.getTable();
        if (newPrescriptions != null) System.out.println("I got the table");
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

    public ArrayList<String> call(){
        ArrayList<String> prescriptionStrings = new ArrayList<>();
        Map<String, Medication> prescriptions = this.getPrescriptions();
        if(!prescriptions.isEmpty()) System.out.println("i got prescriptions");
        for (Map.Entry<String,Medication> prescription:prescriptions.entrySet()){
            Prescription prescription1 =(Prescription) prescription.getValue();
            prescriptionStrings.add(prescription1.toString());
            if(prescription.getValue() != null)System.out.println(prescription1.toString());
        }
        return prescriptionStrings;
    }
}
