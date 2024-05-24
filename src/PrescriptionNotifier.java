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

    private ResultSet getMetaDataOfTable(){
        ResultSet tableMetaData = null;
        try{
            DatabaseMetaData dbMetaData = connection.getMetaData();
            tableMetaData = dbMetaData.getTables(null,null,"PrescriptionsRecords",null);

        }catch (SQLException e){
            System.out.println("can't getMetadata of prescription table " + e.getMessage());
        }
        return tableMetaData;
    }

    private void sortDataInTable(){
        try{
            Statement statement = connection.createStatement();
            statement.execute("SELECT dateAndTime FROM PrescriptionsRecords ORDER BY dateAndTime ");
        }catch (SQLException e){
            System.out.println("can't sort data in table " + e.getMessage());
        }
    }

    public String call(){

        return "";
    };
}
