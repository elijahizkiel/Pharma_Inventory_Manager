import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.jetbrains.annotations.*;

import java.io.*;
import java.sql.*;
import java.util.Objects;

//Gives information or reports information about medications in stock and how much is purchased
// how many is dispensed and how many is disposed  due to expire date
//generates pdf report
//Generates analytical data on how many medications are dispensed and how many are disposed

public class Reporter implements DataBaseModifierAndAccessor {

    Connection connection = null;
    String location ="jdbc:sqlite:.InventoryManager.db";
    Reporter(){}
    Reporter(String location){
        this.location = location;
    }
    //uses iText to create pdf report
    //creates pdf with table to represent the DB table

     void pdfGenerator(String name, String nameOfTable){
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
    ResultSet showDispensed (){
        /*
        * returns paired set of prescribed items and count  */
        this.connect();
        ResultSet countOfMedications = null;
        try {
            Statement queryStatement = this.connection.createStatement();
            countOfMedications = queryStatement.executeQuery("SELECT nameOfMedication,dosageForm,strength, COUNT(*) FROM DispenseRecords GROUP BY nameOfMedication, dosageForm, strength ORDER BY COUNT(*)");
        }catch (SQLException e){
            System.out.println("can't get query statement result"+ e.getMessage());
        }

        return countOfMedications;
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


    //reports how many prescriptions are done in given time interval


    //

}
