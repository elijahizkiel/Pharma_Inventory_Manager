import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//Gives information or reports information about medications in stock and how much is purchased
// how many is dispensed and how many is disposed  due to expire date
//generates pdf report
//Generates analytical data on how many medications are dispensed and how many are disposed

public class Reporter extends DataBaseModifierAndAccessor {

    public Reporter(String location){
        super(location);

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
            ResultSet resultSet = super.getInfoFromTable(nameOfTable);
           try{ while( resultSet.next()){
                for(int i = 0; i < 5; ++i ){
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
        ResultSet countOfMedications = null;
        try {
            super.connect();
            Statement queryStatement = this.connect.createStatement();
            countOfMedications = queryStatement.executeQuery("SELECT nameOfMedication,dosageForm,strength, COUNT(*) FROM DispenseRecords GROUP BY nameOfMedication, dosageForm, strength");
        }catch (SQLException e){
            System.out.println("can't get query statement result"+ e.getMessage());
        }

        return countOfMedications;
    }


    //reports how many prescriptions are done in given time interval


    //

}
