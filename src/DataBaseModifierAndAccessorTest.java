import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class DataBaseModifierAndAccessorTest {
    public static void main(String[] args) {
        Prescriber DBMA = new Prescriber("jdbc:sqlite:..DBMAtrial.db");
        Dispenser DBMA2 = new Dispenser("jdbc:sqlite:..DBMAtrial.db");

        ArrayList<Prescription> prescriptions = getPrescriptionArrayList();

        DBMA.prescribe(prescriptions);
        DBMA2.dispense(prescriptions);

        System.out.println("******************************\n Prescribed Medications\n*********************");

        ResultSet resultSet1 = DBMA.getInfoFromTable();
        System.out.printf("%20s   %20s  %8s  %15s   %10s %n","PrescriptionNumber", "Name Of Medication", "Strength", "DosageForm", "Amount");
        try{while(resultSet1.next()){
             System.out.printf("%20s   %20s  %8d  %15s   %10d %n", resultSet1.getString(1),
                     resultSet1.getString(2),resultSet1.getInt(3),
                     resultSet1.getString(4),resultSet1.getInt(5));
            }
        }catch (SQLException e){
            System.out.println("cant get info from PrescriptionRecords " + e.getMessage() );
        }

        System.out.println("*********************************\n Dispensed Medications \n******************");

        ResultSet resultSet2 = DBMA2.getInfoFromTable();
        System.out.printf("%20s   %20s  %8s  %15s   %n","PrescriptionNumber", "Name Of Medication", "Strength", "DosageForm");
        try{while(resultSet2.next()){
            System.out.printf("%20s   %20s  %8d  %15s  %n", resultSet2.getString(1),
                    resultSet2.getString(2),resultSet2.getInt(3),
                    resultSet2.getString(4));
        }
        }catch (SQLException e){
            System.out.println("cant get info from PrescriptionRecords " + e.getMessage() );
        }

        Reporter reporter = new Reporter("jdbc:sqlite:..DBMAtrial.db");

        reporter.pdfGenerator("DBMAPrescribed","Prescribe");
        reporter.pdfGenerator("DBMADispensed","Dispense");


        ResultSet dispenseCount = reporter.showDispensed();
       try{
           while(dispenseCount.next()){
                   System.out.printf("%s   %-15s %-15d %d %n",dispenseCount.getString(1),dispenseCount.getString(2),dispenseCount.getInt(3),dispenseCount.getInt(4));

           }
       }catch(SQLException e){
           System.out.println("can't get showDispensed " + e.getMessage());
       }

    }

    private static @NotNull ArrayList<Prescription> getPrescriptionArrayList() {
        Prescription p1 = new Prescription("paracetamol", 500,"Suspension", "BID", 2,7);
        Prescription p2 = new Prescription("paracetamol", 250,"Suspension", "BID", 2,7);
        Prescription p3 = new Prescription("paracetamol", 500,"Tablet", "TID", 3,7);
        Prescription p4 = new Prescription("paracetamol", 500,"Suspension", "BID", 2,7);

        ArrayList<Prescription> prescriptions = new ArrayList<>();
        prescriptions.add(p1);
        prescriptions.add(p2);
        prescriptions.add(p3);
        prescriptions.add(p4);
        return prescriptions;
    }
}
