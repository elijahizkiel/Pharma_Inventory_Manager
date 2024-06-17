import java.util.*;

public class DisposedMed extends Medication{
    private final String reasonToDispose;
    private final String purchaseNumber;
    private final int amount;
    DisposedMed(String nameOfMedication, int strength, String dosageForm, int amount, Date expireDate, String purchaseNumber, String reasonToDispose){
        super(nameOfMedication, strength, dosageForm, expireDate);
        this.purchaseNumber = purchaseNumber;
        this.reasonToDispose = reasonToDispose;
        this.amount = amount;
    }

    public String getPurchaseNumber() {
        return purchaseNumber;
    }

    public int getAmount() {
        return amount;
    }

    public String getReasonToDispose() {
        return reasonToDispose;
    }
}
