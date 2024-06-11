import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.Timer;

public class Registerer implements DataBaseModifierAndAccessor{
    Connection connection = null;
    String location;
    public Registerer(){
        location = "jdbc:sqlite:.InventoryManager.db";
    }

    public Registerer(String location){
        this.location = location;
    }

    public void connect(){
        try{
            connection = DriverManager.getConnection(location);
        }catch(SQLException e){
            System.out.println("failed to connect Database" + e.getMessage());
        }
    }
    @Override
    public void createTable(){
        try {
            Statement tableCreator = connection.createStatement();
            tableCreator.execute("CREATE TABLE IF NOT EXISTS MedicationInStock(purchaseNumber  TEXT, " +
                    "nameOfMedication TEXT,strength INTEGER, dosageForm TEXT,amount INTEGER, expireDate BIGINT)");
        }catch(SQLException e){
            System.out.println("can't create register table" + e.getMessage());
        }
    }

    @Override
    public void insertCommand(@NotNull Medication medication1) {
        PreparedStatement command;
        NewlyPurchasedDrug medication = (NewlyPurchasedDrug) medication1;
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());
        try {
            String querySet = "insert into MedicationInStock values (?,?,?,?,?,?)";
            command = connection.prepareStatement(querySet);
            command.setString(1,nowTime.toString());
            System.out.println("purchase number is set");
            command.setString(2,medication.getNameOfMedication());
            System.out.println("nameOfMedication is set");
            command.setString(4,medication.getDosageForm());
            System.out.println("dosageForm is set");
            command.setInt(3,medication.getStrength());
            System.out.println("strength is set");
            command.setInt(5,medication.getAmount());
            System.out.println("Amount is set");
            command.setTimestamp(6,new Timestamp( medication.getExpireDate().getTime()));
            System.out.println("Expire date  is set");
            command.executeUpdate();
            System.out.println("Data inserted");
        } catch(SQLException exception) {
            System.out.println(exception.getMessage());
        }

    }

    public void register(@NotNull ArrayList<Medication> medications){
        for (Medication medication : medications) {
            register(medication);
        }
    }

    public void register(@NotNull Medication medication){
        this.connect();
        this.createTable();
        insertCommand(medication);
    }
    @Override
    public  ResultSet getInfoFromTable() {
        ResultSet resultSet = null;
        try{
            this.connect();
            Statement queryStatement = connection.createStatement();
            resultSet = queryStatement.executeQuery("SELECT nameOfMedication, strength, dosageForm FROM MedicationInStock");
        }catch(SQLException e){
            System.out.println("can't get info from MedicationInStock table " + e.getMessage());
        }
        return resultSet;
    }

    public @NotNull Object[][] getMedsInCount(){
        ResultSet medsInCount = null;
        this.connect();
        try{
            Statement run = connection.createStatement();
            medsInCount = run.executeQuery("SELECT nameOfMedication,dosageForm,strength,sum(amount)" +
                    " FROM MedicationInStock GROUP BY nameOfMedication, dosageForm, strength ORDER By sum(amount) DESC;");
        }catch (SQLException e){
            System.out.println("can't get medsInCount table " + e.getMessage());
        }
        assert medsInCount != null;
        return formTable( medsInCount);
    }

    private Object[][] formTable(@NotNull ResultSet tableData){
        ArrayList<Object[]> tableData2 = new ArrayList<>();
        int rowNumb = 1;
        try{
            while(tableData.next()) {
                Object[] rowData = {rowNumb++,tableData.getString("nameOfMedication"), tableData.getString("dosageForm"),
                        tableData.getInt("strength"), (tableData.getInt(4))};
                tableData2.add(rowData);
            }
        }catch(SQLException e){
            System.out.println("can't form medsInCount table " + e.getMessage());
        }
        Object[][] table = new Object[tableData2.size()][];
        for(int i = 0; i < tableData2.size(); ++i){
            table[i] = tableData2.get(i);
        }
        return table;
    }

    public Object[][] medsInLast7days(){
        long now = System.currentTimeMillis();
        ResultSet medsInLast7Days = null;
        this.connect();
        try{
            Statement query = connection.createStatement();
            medsInLast7Days = query.executeQuery("SELECT nameOfMedication, dosageForm, strength, count(*) FROM DispenseRecords Group By nameOfMedication, dosageForm, strength HAVING dateAndTime >= ("+now+"-640800000)");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        if(medsInLast7Days!= null){
            return formTable(medsInLast7Days);
        }
        else {
            return new Object[5][4];
        }
    }

    public Object[][] getMedsInShortage(){
        this.connect();
        ResultSet medsInShortage = null;
        try{
            Statement query = connection.createStatement();
            medsInShortage = query.executeQuery("SELECT nameOfMedication, dosageForm, strength, sum(amount)" +
                    " FROM MedicationInStock GROUP BY nameOfMedication,dosageForm, strength HAVING sum(amount) <= 50;");
        }catch (SQLException e){
            System.out.println("can't get medsInShortage " + e.getMessage());
        }
        if(medsInShortage!=null)return formTable(medsInShortage);
        else return new Object[5][4];
    }
}
