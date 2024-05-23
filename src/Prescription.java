import java.util.UUID;
import java.time.LocalDateTime;

/*
* stores prescriptions
* it stores  the prescription attributes in the instance variables */
public class Prescription extends Medication {

    private final String dose;
    private final int amount;
    private final int frequency;
    private final int duration;
    private final String prescriptionNumber;
    DataBaseModifierAndAccessor dataBaseModifierAndAccessor = new DataBaseModifierAndAccessor("jdbc:sqlite:..INVENTORY_DB.db");


    public Prescription(String nameOfMedication, int strength,String dosageForm,String dose,int frequency, int duration ){
        super(nameOfMedication,strength,dosageForm);
        this.dose = dose;
        this.duration = duration;
        this.frequency = frequency;
        this.amount = frequency * duration;
        LocalDateTime dateTime = LocalDateTime.now();
        UUID id = UUID.randomUUID();
        this.prescriptionNumber = id.toString(); //+ "DT" + dateTime;
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
        return dataBaseModifierAndAccessor.isInInventory(this.getNameOfMedication(), this.getStrength(), this.getDosageForm());
    }

    public String getPrescriptionNumber() {
        return prescriptionNumber;
    }

    @Override
    public String toString() {
        return super.getNameOfMedication() + super.getStrength() + super.getDosageForm() + dose + frequency + duration;
    }

}
