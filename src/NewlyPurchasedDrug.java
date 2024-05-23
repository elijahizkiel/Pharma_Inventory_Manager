/*
* This class is to add purchased medications into database
* it allows to register what medications are purchased
* how many are they,
* dosage form, strength,  */

import java.util.Date;

public class NewlyPurchasedDrug extends Medication{

    public NewlyPurchasedDrug(String nameOfMedication, int strength, String dosageForm, Date expireDate){
        super( nameOfMedication, strength, dosageForm, expireDate);

    }

}
