/*
*
* Sends notification if a prescription object is added to PrescriptionRecords table  */

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.sql.*;


public class PrescriptionNotifier implements Callable<ArrayList<String>>, Serializable {
    transient Connection connection = null;
    String location = "jdbc:sqlite:.InventoryManager.db";
    long oldLastRowPointer = 259200000;
    transient long newLastRowPointer;
    transient Prescriber prescriber = new Prescriber(location);
    PrescriptionNotifier(){}
    PrescriptionNotifier(String location){
        this.location = location;
    }

    private void connect(){
        try{
            connection = DriverManager.getConnection(location);

            System.out.println("Connection" + connection);
        }catch (SQLException e){
            System.out.println("PrescriptionNotifier can't connect to Database " + e.getMessage());
        }
    }

    private ResultSet getTable(){
        this.connect();
        System.out.println("creating table");
        //prescriber.createTable();
        System.out.println("created table");
        ResultSet result = null;
        try{
            Statement statement = connection.createStatement();
             result = statement.executeQuery("SELECT * FROM PrescriptionsRecords");

            if(result != null) System.out.println("Result List"+ result);
            else System.out.println("Result is null");
        }catch(SQLException e){
            System.out.println("can't get PrescriptionsRecords table from Notifier " + e.getMessage());
        }
        return result;
    }

    private ResultSet getTable(long time){
        System.out.println("Diving into getTable(long)");
        ResultSet result = null;
        this.connect();
        prescriber.createTable();
        try{
            Statement statement = connection.createStatement();
            System.out.println(connection);
            result = statement.executeQuery("SELECT * FROM PrescriptionsRecords WHERE dateAndTime >= " + time + ";");
            System.out.println(result);
        }catch(SQLException e){
            System.out.println("can't get PrescriptionsRecords table " + e.getMessage());
        }
        return result;
    }

    private long getLastRowPointer(){
        long dateAndTime = 0;
        System.out.println("go itn to getting last row pointer");
        this.connect();
        System.out.println("trying to get connection");
        this.sortDataInTable();
        System.out.println("sorting out table");
        ResultSet result = this.getTable();
        System.out.println("got table without long");
        try{
            if(result != null){
                System.out.println("the result isn't null\n"+result);
                if(result.next()){
                    dateAndTime = result.getLong(2);}
                System.out.println("dateAndTime: " + dateAndTime);
            }else System.out.println("the result is null");
        }catch (SQLException e){
            System.out.println("can't get dateAndTime from PrescriptionsRecords " + e.getMessage());
        }
        return dateAndTime;
    }

    private @NotNull Map<String, Medication> getPrescriptions(){
        Map<String, Medication> prescriptions = new HashMap<>();
        ResultSet newPrescriptions = null;
        newLastRowPointer =this.getLastRowPointer();

        //prints for error checking
        if(oldLastRowPointer == newLastRowPointer ) System.out.println("Old and new last row pointers are equal");

        System.out.println("My last row pointer is: " + newLastRowPointer);

         if(oldLastRowPointer < newLastRowPointer) {
        newPrescriptions = this.getTable(oldLastRowPointer);
//         oldLastRowPointer = newLastRowPointer;
             System.out.println(newPrescriptions);}
//        ResultSet newPrescriptions = this.getTable();
        System.out.println(newPrescriptions);
        if (newPrescriptions != null) System.out.println("I got the table");
        try{
            if(newPrescriptions != null){
                while(newPrescriptions.next()){
                    Prescription prescription = new Prescription(newPrescriptions.getString("prescriptionNumber"),
                            newPrescriptions.getString("nameOfMedication"),newPrescriptions.getInt("strength"),
                            newPrescriptions.getString("dosageForm"),newPrescriptions.getString("dose"),
                            newPrescriptions.getInt("amount"));
                    prescriptions.put(prescription.getPrescriptionNumber(), prescription);
                }
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return prescriptions;
    }

    private void sortDataInTable(){
        try{
            Statement statement = connection.createStatement();
            statement.execute("SELECT dateAndTime FROM PrescriptionsRecords ORDER BY dateAndTime DESC");
        }catch (SQLException e){
            System.out.println("can't sort data in table " + e.getMessage());
        }
    }

    public ArrayList<String> call(){
        ArrayList<String> prescriptionStrings = new ArrayList<>();
        System.out.println("going for getting prescriptions");
        Map<String, Medication> prescriptions = this.getPrescriptions();
        System.out.println("returned from getPrescriptions");
        if(!prescriptions.isEmpty()) {
            for (Map.Entry<String,Medication> prescription:prescriptions.entrySet()){
                Prescription prescription1 =(Prescription) prescription.getValue();
                prescriptionStrings.add(prescription1.toString());
                if(prescription.getValue() != null)System.out.println(prescription1);
            }
        }
        return prescriptionStrings;
    }

}
