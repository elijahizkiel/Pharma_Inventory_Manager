/*
* This class is to add purchased medications into database
* it allows to register what medications are purchased
* how many are they,
* dosage form, strength,  */

import java.util.Date;

public class NewlyPurchasedDrug extends Medication{
    int amount = 0;

    public NewlyPurchasedDrug(String nameOfMedication, int strength, String dosageForm, Date expireDate,int amount){
        super( nameOfMedication, strength, dosageForm, expireDate);
        this.amount = amount;
    }

    @Override
    public int getStrength() {
        return super.getStrength();
    }

    @Override
    public String getNameOfMedication() {
        return super.getNameOfMedication();
    }

    @Override
    public String getDosageForm() {
        return super.getDosageForm();
    }

    public int getAmount() {
        return amount;
    }


}

