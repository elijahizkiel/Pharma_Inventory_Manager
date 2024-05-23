import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PrescriberGUI {
    static ImageIcon logo = new ImageIcon("C:\\Users\\Y.S\\IdeaProjects\\PharmacyInventoryManager\\src\\logo_yekatit.png");
    private static JFrame homeFrame ;
    private static JPanel bigPanel;
    private static JButton addPanelButton;
    private static JButton prescribeButton;
    private static ButtonClickListener buttonClickListener = new ButtonClickListener();
    private static ArrayList<Panel> numOfPanels = new ArrayList<>();
    static DataBaseModifierAndAccessor mod = new DataBaseModifierAndAccessor("jdbc:sqlite:..INVENTORY_DB.db");
    Border border = BorderFactory.createLineBorder( new Color(120,120,120),1,true);

    private static void addPanel(){
        Panel newPanel = new Panel();
//        bigPanel.setLayout(new BoxLayout(bigPanel, BoxLayout.Y_AXIS));
        bigPanel.add(newPanel);
        numOfPanels.add(newPanel);
////        GridLayout layout = (GridLayout) bigPanel.getLayout();
////        int x = layout.getColumns();
////        layout.setColumns(++x);
//        bigPanel.setLayout(layout);
        bigPanel.revalidate();
        homeFrame.revalidate();
        homeFrame.pack();
     }

    private static void prescribe(){
        ArrayList<Prescription> prescriptions = new ArrayList<>();
        for(Panel panel: numOfPanels){
            Prescription prescription = new Prescription(panel.nameOfMedication.getText(),Integer.parseInt(panel.strength.getText()),
                    panel.dosageForm.getText(),panel.dose.getText(),
                    Integer.parseInt(panel.frequency.getText()), Integer.parseInt(panel.duration.getText()));
            prescriptions.add(prescription);
        }
        mod.prescribe(prescriptions);

    }
     static class Panel extends JPanel{
        JTextField nameOfMedication = new JTextField(20);
         JTextField strength = new JTextField(20);
         JTextField dosageForm = new JTextField(20);
         JTextField dose = new JTextField(10);
         JTextField frequency = new JTextField(10);
         JTextField duration = new JTextField(10);

         JLabel nameLabel;
         JLabel strengthLabel;
         JLabel formLabel;
         JLabel doseLabel;
         JLabel frequencyLabel;
         JLabel durationLabel;
         Panel(){
             nameLabel = new JLabel("Name of Medication");
             strengthLabel = new JLabel("Strength of Medication");
             formLabel = new JLabel("Dosage Form");
             doseLabel = new JLabel("Dose");
             frequencyLabel = new JLabel("Frequency per Duration");
             durationLabel = new JLabel("Duration");

             //adding components
             this.add(nameLabel);this.add(nameOfMedication);
             this.add(strengthLabel);this.add(strength);
             this.add(formLabel);this.add(dosageForm);
             this.add(doseLabel);this.add(dose);
             this.add(frequencyLabel);this.add(frequency);
             this.add(durationLabel);this.add(duration);

             //setting layout
             this.setLayout(new FlowLayout());
//             this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
//             //create Group layout
//             GroupLayout  labels = new GroupLayout(this);
//
//             GroupLayout.SequentialGroup hNameGroup = labels.createSequentialGroup().addComponent(nameLabel).addComponent(nameOfMedication);
//             GroupLayout.SequentialGroup hStrengthGroup = labels.createSequentialGroup().addComponent(strengthLabel).addComponent(strength);
//             GroupLayout.SequentialGroup hDosageFormGroup = labels.createSequentialGroup().addComponent(formLabel).addComponent(dosageForm);
//             GroupLayout.SequentialGroup hDoseGroup = labels.createSequentialGroup().addComponent(doseLabel).addComponent(dose);
//             GroupLayout.SequentialGroup hFrequencyGroup = labels.createSequentialGroup().addComponent(frequencyLabel).addComponent(frequency);
//             GroupLayout.SequentialGroup hDurationGroup = labels.createSequentialGroup().addComponent(durationLabel).addComponent(duration);
//
//             //create horizontal groups
//             GroupLayout.ParallelGroup hGroup = labels.createParallelGroup(GroupLayout.Alignment.LEADING);
//             hGroup.addGroup(hNameGroup).addGroup(hStrengthGroup).addGroup(hDosageFormGroup).addGroup(hDoseGroup).addGroup(hFrequencyGroup).addGroup(hDurationGroup);
//             labels.setHorizontalGroup(hGroup);
//
//             //create vertical group
//             GroupLayout.ParallelGroup vGroup = labels.createParallelGroup(GroupLayout.Alignment.LEADING);
//             vGroup.addGroup(hNameGroup).addGroup(hStrengthGroup).addGroup(hDosageFormGroup).addGroup(hDoseGroup).addGroup(hFrequencyGroup).addGroup(hDurationGroup);
//             labels.setVerticalGroup(vGroup);
        }
    }
    private static class ButtonClickListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            if(e.getSource() == prescribeButton) prescribe();
            else if (e.getSource() == addPanelButton) {
                 addPanel();
            }
        }
    }

    public static void main(String[] args) {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        homeFrame = new JFrame("Prescriber Window");
        JScrollPane scrollablePane;
        bigPanel = new JPanel() ;
        Panel firstPanel = new Panel();
        addPanelButton = new JButton("Add Medication");
        prescribeButton = new JButton("Prescribe");
        JLabel label = new JLabel(logo);
        numOfPanels.add(firstPanel);

        label.setText("Welcome! Prescribe Medication for your Patient!");
        label.setSize(50,34);
        addPanelButton.addActionListener(buttonClickListener);
        addPanelButton.setMaximumSize(new Dimension(50,20));
        prescribeButton.addActionListener(buttonClickListener);

        firstPanel.add(addPanelButton);
        bigPanel.add(firstPanel);
        bigPanel.setLayout(new GridLayout(0,1));
        bigPanel.setBounds(20,30,400,300);

//        scrollablePane = new JScrollPane(bigPanel);
        homeFrame.add(label);
        homeFrame.add(bigPanel);
//        homeFrame.getContentPane().add(scrollablePane);//
        homeFrame.add(prescribeButton);
        homeFrame.setIconImage(logo.getImage());
        homeFrame.setLayout(new FlowLayout());
        homeFrame.setSize(screenSize);
        homeFrame.setLocationRelativeTo(null);
//        homeFrame.setResizable(true);
//        homeFrame.pack();

        homeFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        homeFrame.setVisible(true);

    }
}