import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.jetbrains.annotations.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

//Gives information or reports information about medications in stock and how much is purchased
// how many is dispensed and how many is disposed  due to expire date
//generates pdf report
//Generates analytical data on how many medications are dispensed and how many are disposed

public class Reporter implements DataBaseModifierAndAccessor {

    Connection connection = null;
    String location;
    Reporter(){
        location ="jdbc:sqlite:.InventoryManager.db";
    }
    Reporter(String location){
        this.location = location;
    }
    //uses iText to create pdf report
    //creates pdf with table to represent the DB table

     void pdfGenerator(String name, String nameOfTable){
        this.connect();
        Document document = new Document();
        String fileName = name +".pdf";
        try{
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            PdfPTable table = new PdfPTable(5);
            ResultSet resultSet = this.getInfoFromTable(nameOfTable);

           try{
               int count = resultSet.getMetaData().getColumnCount();
               while( resultSet.next()){
                for(int i = 0; i < count; ++i ){
                    table.addCell(new PdfPCell(new Paragraph(resultSet.getString(i + 1))));
                }
                document.add(table);
               System.out.println("data successfully added to the pdf");
               }
           }catch(SQLException e){
               System.out.println("can't access data from table " + e.getMessage() );
           }
        }catch (DocumentException | FileNotFoundException e){
            System.out.println(e.getMessage());
        }
         document.close();
    }

    //reports how many are disposed due to expire date



    // reports how many are dispensed
    Object[][] showDispensed (){
        /*
        * returns paired set of prescribed items and count  */
        this.connect();
        Dispenser dispenser = new Dispenser(location);
        return formTable(dispenser.showTopDispensed());
    }

    private Object[][] formTable(@NotNull ResultSet tableData){
        ArrayList<Object[]> tableData2 = new ArrayList<>();
        int rowNumb = 1;
        try{
            while(tableData.next()) {
                Object[] rowData = {rowNumb,tableData.getString("nameOfMedication"), tableData.getString("dosageForm"),
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


    @Override
    public void connect() {
         try{
             connection = DriverManager.getConnection(location);
         } catch (SQLException e){
             System.out.println("can't connect from reporter " + e.getMessage());
         }
    }

    @Override
    public void createTable(){}

    @Override
    public void insertCommand(@NotNull Medication medication) {}

    @Override
    public ResultSet getInfoFromTable() {
        return null;
    }

    public ResultSet getInfoFromTable(String nameOfTable){
         ResultSet result = null;
         if(Objects.equals(nameOfTable,"Prescribe")){
             Prescriber prescriber = new Prescriber(location);
             result = prescriber.getInfoFromTable();
         } else if (Objects.equals(nameOfTable, "Dispense")) {
             Dispenser dispenser = new Dispenser(location);
             result = dispenser.getInfoFromTable();
         } else if (Objects.equals(nameOfTable, "MedicationInStock")) {
             Registerer registerer = new Registerer();
             result = registerer.getInfoFromTable();
         }
        return result;
    }

    public ResultSet runQuery(String query){
        ResultSet resultSet = null;
        this.connect();
        try{
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

        }catch(SQLException e){
            System.out.println("can't run the query" + e.getMessage());
        }
        return resultSet;
    }
}
