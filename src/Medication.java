import java.util.Date;

public abstract class Medication {
    private final String nameOfMedication;
    private final int strength;
    private final String dosageForm;
    protected Date expireDate;

    public Medication(String nameOfMedication, int strength, String dosageForm, Date expireDate){
        this.nameOfMedication = nameOfMedication;
        this.strength = strength;
        this.dosageForm = dosageForm;
        this.expireDate = expireDate;
    }
    public Medication(String nameOfMedication,int strength, String dosageForm){
        this.nameOfMedication = nameOfMedication;
        this.strength = strength;
        this.dosageForm = dosageForm;
    }

    public String getNameOfMedication() {
        return nameOfMedication;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public int getStrength() {
        return strength;
    }

}
