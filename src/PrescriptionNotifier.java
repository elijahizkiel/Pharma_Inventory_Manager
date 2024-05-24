/*
*
* Sends notification if a prescription object is added to PrescriptionRecords table  */

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

    private long getLastRowPointer(){
        long dateAndTime = 0;
        this.connect();
        this.sortDataInTable();
        try{
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM PrescriptionsRecords");
            if(result.isFirst()){
                dateAndTime = result.getLong("dateAndTime");
            }
        }catch(SQLException e){
            System.out.println("can't get dateAndTime for item in PrescriptionsRecords " + e.getMessage());
        }
        return dateAndTime;
    }

    private void sortDataInTable(){
        try{
            Statement statement = connection.createStatement();
            statement.execute("SELECT dateAndTime FROM PrescriptionsRecords ORDER BY dateAndTime DSC");
        }catch (SQLException e){
            System.out.println("can't sort data in table " + e.getMessage());
        }
    }

    public String call(){

        return "";
    };
}
