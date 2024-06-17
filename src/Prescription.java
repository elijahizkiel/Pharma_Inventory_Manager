import java.util.UUID;
import java.time.LocalDateTime;

/*
* stores prescriptions
* it stores  the prescription attributes in the instance variables */
public class Prescription extends Medication {

    private final String dose;
    private final int amount;
    private int frequency;
    private int duration;
    private final String prescriptionNumber;
    private boolean isDispensed = false;
    Prescriber prescriber = new Prescriber("jdbc:sqlite:..DBMAtrial.db");


    public Prescription(String nameOfMedication, int strength,String dosageForm,String dose,int frequency, int duration ){
        super(nameOfMedication,strength,dosageForm);
        this.dose = dose;
        this.duration = duration;
        this.frequency = frequency;
        this.amount = frequency * duration;
        LocalDateTime dateTime = LocalDateTime.now();
        UUID id = UUID.randomUUID();
        this.prescriptionNumber = id.toString() + "DT" + dateTime;
    }
    public Prescription(String prescriptionNumber, String nameOfMedication, int strength,String dosageForm,String dose,int amount ){
        super(nameOfMedication,strength,dosageForm);
        this.dose = dose;
        this.amount =amount;
        this.prescriptionNumber = prescriptionNumber;
    }
    public String getNameOfMedication() {
        return super.getNameOfMedication();
    }

    public int getStrength() {
        return super.getStrength();
    }

    public String getDosageForm() {
        return super.getDosageForm();
    }

    //returns dose prescribed
    public String getDose() {
        return dose;
    }
    public int getAmount() {
        return amount;
    }

    boolean verifyPrescription(){
        return prescriber.isInStock(this);
    }

    public String getPrescriptionNumber() {
        return prescriptionNumber;
    }

    public void setDispensed(boolean dispensed) {
        isDispensed = dispensed;
    }

    public boolean isDispensed() {
        return isDispensed;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return super.getNameOfMedication() +" "+ super.getStrength()+" " + super.getDosageForm()
                + " " + dose +" "+ " " + duration+ "days";
    }

}