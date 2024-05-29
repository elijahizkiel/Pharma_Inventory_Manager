
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;


public class PrescriberGUI extends JFrame {
    JPanel imagePanel = new JPanel();
    ImageIcon logoImage = new ImageIcon("C:\\Users\\Y.S\\IdeaProjects\\PharmacyInventoryManager\\src\\logo_yekatit.png");
    Image image = logoImage.getImage();
    Border border = BorderFactory.createLineBorder(Color.BLUE);

    JButton addMedicationButton = new JButton("Add Medication");
    JButton prescribeButton = new JButton("Prescribe");

    JPanel containerPanel = new JPanel();
    PrescribePanel prescribingPanel= new PrescribePanel();
    ArrayList<PrescribePanel> panels = new ArrayList<>();
    JScrollPane prescriptionPane = new JScrollPane(containerPanel);

    public PrescriberGUI(){
        JLabel label =new JLabel("Welcome to Yekatit 12 Pharmacy Prescription Center!");
        label.setIcon(logoImage);
        imagePanel.add(label);
        imagePanel.setBorder(border);
        imagePanel.setBounds(40,0,1100,250);
        imagePanel.setBackground(Color.WHITE);

        panels.add(prescribingPanel);

        containerPanel.add(prescribingPanel);
        containerPanel.setLayout(new GridLayout(0,1));

        prescriptionPane.setBounds(100,270,900,200);
        prescriptionPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        prescriptionPane.setWheelScrollingEnabled(true);

        prescribeButton.setBounds(1100,350,100,30);
        addMedicationButton.setBounds(1100,400,140,30);

        addMedicationButton.addActionListener(new ButtonClickListener());
        prescribeButton.addActionListener(new ButtonClickListener());

        this.setTitle("Prescriber Home");
        this.setSize(1300,900);
        this.add(imagePanel);
        this.getContentPane().add(prescriptionPane);
        this.add(addMedicationButton);
        this.add(prescribeButton);
        this.setBackground(Color.CYAN);
        this.setLayout(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void addMedication(){
        PrescribePanel panel = new PrescribePanel();
        panels.add(panel);
        containerPanel.add(panel);
        containerPanel.revalidate();
    }

    private ArrayList<Prescription> getPrescriptions(ArrayList<PrescribePanel> panels){
        ArrayList<Prescription> prescriptions = new ArrayList<>();
        for(PrescribePanel panel: panels){
            Prescription prescription = new Prescription(panel.nameOfMedication.getText(),Integer.parseInt(panel.strength.getText()),
                    panel.dosageForm.getText(),panel.dose.getText(),Integer.parseInt(panel.frequency.getText()),
                    Integer.parseInt(panel.duration.getText()));
            prescriptions.add(prescription);
        }
        return prescriptions;
    }

    private void prescribe(ArrayList<PrescribePanel> panels){
        ArrayList<Prescription> prescriptions = getPrescriptions(panels);
        Prescriber prescriber = new Prescriber("jdbc:sqlite:..DBMAtrial.db");
        prescriber.prescribe(prescriptions);
    }
    public static void main(String[] args) {
        new PrescriberGUI();
    }

    private class  ButtonClickListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e){
            if (e.getSource() == addMedicationButton) addMedication(); else if (e.getSource() == prescribeButton) prescribe(panels);
        }
    }
}
class PrescribePanel extends JPanel {
    JTextField nameOfMedication = new JTextField(),strength =new JTextField();
    JTextField dosageForm = new JTextField(),frequency = new JTextField();
    JTextField duration =new JTextField(),dose = new JTextField();

    public PrescribePanel(){
        Dimension dm = new Dimension(70,30);
        nameOfMedication.setSize(70,30);
        strength.setPreferredSize(dm);//Size(70,30);
        dosageForm.setSize(70,30);
        frequency.setSize(70,30);
        duration.setSize(70,30);
        dose.setSize(70,30);
        GridLayout gridLayout = new GridLayout(2,6,5,5);
        this.add(new JLabel("Name Of Medication"));
        this.add(new JLabel("Strength"));
        this.add(new JLabel("Dosage Form"));
        this.add(new JLabel("Frequency"));
        this.add(new JLabel("Duration"));
        this.add(new JLabel("Dose"));

        nameOfMedication.setName("bale");
        this.add(nameOfMedication);
        this.add(strength);
        this.add(dosageForm);
        this.add(frequency);
        this.add(duration);
        this.add(dose);
        this.setLayout(gridLayout);
    }
}