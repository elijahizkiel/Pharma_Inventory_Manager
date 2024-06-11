import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class BackEndTest {

    public static void main(String[] args) {
        Prescriber DBMA = new Prescriber("jdbc:sqlite:..DBMAtrial.db");
        Dispenser DBMA2 = new Dispenser("jdbc:sqlite:..DBMAtrial.db");
        Registerer registerer = new Registerer("jdbc:sqlite:..DBMAtrial.db");
        ArrayList<Medication> medications = new ArrayList<>();
        medications.add(new NewlyPurchasedDrug("Diclofenac",500, "Suspension",new Date(2025, Calendar.MARCH,24),200));
        medications.add(new NewlyPurchasedDrug("Paracetamol",500, "Suspension",new Date(2025,Calendar.FEBRUARY,24),200));
        medications.add(new NewlyPurchasedDrug("Diclofenac",250, "Suspension",new Date(2025,Calendar.FEBRUARY,24),200));
        medications.add(new NewlyPurchasedDrug("Paracetamol",250, "Suspension",new Date(2025,Calendar.FEBRUARY,24),200));
        medications.add(new NewlyPurchasedDrug("Diclofenac",500, "Tablet",new Date(2025,Calendar.FEBRUARY,24),200));
        medications.add(new NewlyPurchasedDrug("Paracetamol",500, "Tablet",new Date(2025,Calendar.FEBRUARY,24),200));
        medications.add(new NewlyPurchasedDrug("Diclofenac",500, "Suspension",new Date(2023,Calendar.FEBRUARY,24),200));
        medications.add(new NewlyPurchasedDrug("Diclofenac",500, "Tablet",new Date(2024,Calendar.FEBRUARY,24),200));
        medications.add(new NewlyPurchasedDrug("Paracetamol",250, "Tablet",new Date(2025,Calendar.FEBRUARY,24),200));
        medications.add(new NewlyPurchasedDrug("Paracetamol",250, "Tablet",new Date(2025,Calendar.FEBRUARY,24),200));


        registerer.register(medications);

        ArrayList<Prescription> prescriptions = getPrescriptionArrayList();
        int[] truthValues = new int[5];
        int i =0;
        for(Prescription prescription: prescriptions){
            if(prescription.verifyPrescription()) truthValues[i] = 1;
            else truthValues[i] = 0;
            System.out.println("verification values is: "+prescription.verifyPrescription());
        }
        for(int j: truthValues){
            System.out.println(j);
        }
        for(Prescription prescription: prescriptions) {
            if(prescription.verifyPrescription())DBMA.prescribe(prescription);
            else System.out.println(prescription + " is not in the inventory");
        }

        DBMA2.dispense(prescriptions);

        System.out.println("******************************\n Prescribed Medications\n*********************");

        ResultSet resultSet1 = DBMA.getInfoFromTable();
        System.out.printf("%20s   %20s  %8s  %15s   %10s %n","PrescriptionNumber", "Name Of Medication", "Strength", "DosageForm", "Amount");
        try{
            if(resultSet1 != null){
                while(resultSet1.next()){
                    System.out.printf("%20s   %20s  %8d  %15s   %10d %n", resultSet1.getString(1),
                            resultSet1.getString(2),resultSet1.getInt(3),
                            resultSet1.getString(4),resultSet1.getInt(5));
                }
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


        Object[][] dispenseCount = reporter.showDispensed();

           while(dispenseCount[0][0]!=null){
               for(int j = 0; i<dispenseCount.length;++i){
                   System.out.printf("%s   %-15s %-15d %d %n",dispenseCount[1],dispenseCount[2],dispenseCount[3],dispenseCount[4]);
           }}


    }

    private static @NotNull ArrayList<Prescription> getPrescriptionArrayList() {
        Prescription p1 = new Prescription("Paracetamol", 500,"Suspension", "BID", 2,7);
        Prescription p2 = new Prescription("Paracetamol", 250,"Suspension", "BID", 2,7);
        Prescription p3 = new Prescription("Paracetamol", 500,"Tablet", "TID", 3,7);
        Prescription p4 = new Prescription("Paracetamol", 500,"Suspension", "BID", 2,7);
        Prescription p5 = new Prescription("Paracetamol", 350,"Suspension", "BID", 2,7);

        ArrayList<Prescription> prescriptions = new ArrayList<>();
        prescriptions.add(p1);
        prescriptions.add(p2);
        prescriptions.add(p3);
        prescriptions.add(p4);
        prescriptions.add(p5);

        return prescriptions;
    }
}
