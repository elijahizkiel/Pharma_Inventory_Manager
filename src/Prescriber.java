import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;

public class Prescriber implements DataBaseModifierAndAccessor {
    String location;
    Connection connection = null;

    public Prescriber() {
        location = "jdbc:sqlite:InventoryManager.db";

    }

    public Prescriber(String location) {
        this.location = location;
    }

    @Override
    public void connect() {
        try {
            if (connection == null) {
                connection = DriverManager.getConnection(this.location);
            }
        } catch (SQLException e) {
            System.out.println("can't connect to Prescriber");
        }
    }

    @Override
    public void createTable() {
        try {
            System.out.print("Reached Here hello check test 1111111111");
            System.out.println("Connection found" + connection);
            this.connect();
            Statement tableCreator = connection.createStatement();
            System.out.print("Reached Here hello check test 1111111111");
            tableCreator.execute("CREATE TABLE IF NOT EXISTS PrescriptionsRecords(prescriptionNumber " +
                    "TEXT, dateAndTime BIGINT, nameOfMedication TEXT, dosageForm TEXT," +
                    " strength INTEGER, dose TEXT,amount INTEGER, isDispensed BOOLEAN)");
        } catch (SQLException e) {
            System.out.println("can't create prescription table " + e.getMessage());
        }
    }

    public void prescribe(Prescription prescription) {
        this.connect();
        this.createTable();
        insertCommand(prescription);
        System.out.println("Prescription is completed");
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("can't close connection" + e.getMessage());
        }
    }

    public void prescribe(@NotNull ArrayList<Prescription> items) {
        this.connect();
        this.createTable();
        for (Prescription prescription : items) {
            insertCommand(prescription);
        }
        System.out.println("Prescription is completed");
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("can't close connection" + e.getMessage());
        }
    }

    public boolean isInStock(Prescription prescription) {
        boolean isInInventory = false;
        this.connect();
        ResultSet result= null;
        PreparedStatement isExistQuery = null;
        try {
            isExistQuery = connection.prepareStatement("SELECT nameOfMedication, strength, dosageForm FROM MedicationInStock " +
                    "WHERE nameOfMedication = ? AND strength = ? AND dosageForm = ?");
            isExistQuery.setString(1, prescription.getNameOfMedication());
            isExistQuery.setInt(2, prescription.getStrength());
            isExistQuery.setString(3, prescription.getDosageForm());
            result = isExistQuery.executeQuery();
            if (result.next()) {
                isInInventory = true;
                System.out.println("Medication is found");
                result.close();
            } else System.out.println("No medication Found");

        } catch (SQLException e) {
            System.out.println("can't create statement hello " + e.getMessage());
        }
        System.out.println("successfully checked if medication is in inventory");
        return isInInventory;
    }

    @Override
    public ResultSet getInfoFromTable() {
        ResultSet result = null;
        try {
            this.connect();
            Statement selectTableQuery = connection.createStatement();

            result = selectTableQuery.executeQuery("SELECT prescriptionNumber, nameOfMedication, strength, dosageForm, amount FROM PrescriptionsRecords");
        } catch (SQLException e) {
            System.out.println("can't get info from Prescription table " + e.getMessage());
        }finally {
            if ( connection != null){
                try {
                    connection.close();
                }
                catch(SQLException e) {
                    System.out.println("can't close connection" + e.getMessage());
                }
            }
        }
        return result;
    }

    @Override
    public void insertCommand(@NotNull Medication medication) {
        Prescription prescription = (Prescription) medication;
        PreparedStatement command;
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());
        try {
            String querySet = "insert into PrescriptionsRecords values (?,?,?,?,?,?,?,?)";
            command = connection.prepareStatement(querySet);
            command.setTimestamp(2, nowTime);
            System.out.println("dateAndTime is set");
            command.setString(1, prescription.getPrescriptionNumber());
            System.out.println("prescriptionNumbers is set");
            command.setString(3, prescription.getNameOfMedication());
            System.out.println("nameOfMedication is set");
            command.setString(4, prescription.getDosageForm());
            System.out.println("dosageForm is set");
            command.setInt(5, prescription.getStrength());
            System.out.println("strength is set");
            command.setString(6, prescription.getDose());
            System.out.println("dose is set");
            command.setInt(7, prescription.getAmount());
            System.out.println("Amount is set");
            command.setBoolean(8, prescription.isDispensed());
            System.out.println("Dispense status is set");
            command.executeUpdate();
            System.out.println("Data inserted");
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }

    }


}
