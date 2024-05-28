
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    PrescribePanel prescribePanel = new PrescribePanel();
    JScrollPane prescriptionPane = new JScrollPane(containerPanel);

    public PrescriberGUI(){
        JLabel label =new JLabel("Welcome to Yekatit 12 Pharmacy Prescription Center!");
        label.setIcon(logoImage);
        imagePanel.add(label);
        imagePanel.setBorder(border);
        imagePanel.setBounds(40,0,1100,250);

        imagePanel.setBackground(Color.WHITE);

        containerPanel.add(prescribingPanel);
        containerPanel.setLayout(new GridLayout(0,1));

        prescriptionPane.setBounds(100,270,900,200);
        prescriptionPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        prescriptionPane.setWheelScrollingEnabled(true);

        prescribeButton.setBounds(1100,350,100,30);
        addMedicationButton.setBounds(1100,400,140,30);

        addMedicationButton.addActionListener(new ButtonClickListener());

        this.setTitle("Prescriber Home");
        this.setSize(1300,900);
        this.add(imagePanel);
        this.getContentPane().add(prescriptionPane);
        this.add(addMedicationButton);
        this.add(prescribeButton);
        this.setBackground(Color.cyan);
        this.setLayout(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void addMedication(){
        PrescribePanel panel = new PrescribePanel();
        containerPanel.add(panel);
        containerPanel.revalidate();
    }
    public static void main(String[] args) {
        new PrescriberGUI();
    }

    private class  ButtonClickListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e){
            if (e.getSource() == addMedicationButton){
                addMedication();
            } else if (e.getSource() == prescribeButton) {

            }
        }
    }
}
class PrescribePanel extends JPanel {
    JTextField nameOfMedication = new JTextField(),strength =new JTextField();
    JTextField dosageForm = new JTextField(),frequency = new JTextField();
    JTextField duration =new JTextField(),dose = new JTextField();

    public PrescribePanel(){
        nameOfMedication.setSize(70,40);
        strength.setSize(70,40);
        dosageForm.setSize(70,40);
        frequency.setSize(70,40);
        duration.setSize(70,40);
        dose.setSize(70,40);
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