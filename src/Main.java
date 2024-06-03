import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.text.*;
import java.util.ArrayList;
import java.util.concurrent.*;


public class Main extends JFrame {
    Reporter reporter = new Reporter("jdbc:sqlite:..DBMAtrial.db");
    JTabbedPane mainPane;
    JPanel homePanel;
    static JPanel inventoryPanel;
    JPanel reportsPanel;
    JPanel notificationPanel;

    public Main(){
        mainPane = new JTabbedPane(SwingConstants.TOP);

        homePanel = new HomePanel();
        inventoryPanel = new InventoryPanel();
        reportsPanel = new ReportsPanel();
        notificationPanel = new NotificationPanel();


        mainPane.addTab("Home", homePanel);
        mainPane.addTab("Inventory", inventoryPanel);
        mainPane.addTab("Reports", reportsPanel);
        mainPane.addTab("Notifications", notificationPanel);
        mainPane.setBackground(new Color(236,188,200,140).brighter());

        add(mainPane);
        setTitle("Pharmacy Inventory Manager");
        setSize(1400,900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private class HomePanel extends JPanel{
        static JLabel availableMedsLabel,medsInShortageLabel, medsDispensedLabel, typeCountLabel, totalCountLabel;
        public HomePanel() {
            ResultSet count = null;
            try {

                count = reporter.runQuery("SELECT sum(amount) FROM MedicationInStock", "prescriber");
            }catch(Exception e) {
                System.out.println("can't send query");
            }
            int sum = 0;
            try{
                if(count != null)sum = count.getInt(1);
            }catch(SQLException e){
                System.out.println("can't sum-up amount");
            }

            //create components to be added to HomePanel
            availableMedsLabel = new FrontLabel("Available Medications");
            availableMedsLabel.setBounds(50, 50, 300, 200);

            medsInShortageLabel = new FrontLabel("<html>Medications In<br> shortage </html>");
            medsInShortageLabel.setBounds(400, 50, 300, 200);

            medsDispensedLabel = new FrontLabel("<html>Medications Dispensed <br> in Last Seven Days</html>");
            medsDispensedLabel.setBounds(750, 50, 300, 200);

            totalCountLabel = new FrontLabel("<html>Count of total available <br> medications: "+ sum+"</html>");
            totalCountLabel.setBounds(100, 300, 400, 150);

            typeCountLabel = new FrontLabel("<html>Count of medications by<br> their name: "+ sum+"</html>");
            typeCountLabel.setBounds(550, 300, 400, 150);

            //setting home panel
            add(availableMedsLabel);
            add(medsInShortageLabel);
            add(medsDispensedLabel);
            add(totalCountLabel);
            add(typeCountLabel);
            setLayout(null);
        }
    }

    private class InventoryPanel extends JPanel{
        JTable table;
        static JButton addNewButton;
        JScrollPane scrollPane1;
        InventoryPanel(){
            ResultSet tableData = reporter.showDispensed();

            if(tableData != null){
                String[] tableHeader = {"No","Name Of Medication", "Dosage Form","Strength", "Frequency"};
                Object[][] rowModel = new Object[10][5];

                int rowCount =0;
                try{
                    while(tableData.next() && (rowCount <10)){
                        rowModel[rowCount][0] = rowCount + 1;
                        rowModel[rowCount][1] = tableData.getString("nameOfMedication");
                        rowModel[rowCount][2] = tableData.getString("dosageForm");
                        rowModel[rowCount][3] = tableData.getInt("strength");
                        rowModel[rowCount][4] = tableData.getInt(4);
                        ++rowCount;
                    }
                }catch (SQLException e){
                    System.out.println("can't create rowModel" + e.getMessage());
                }

                table = new JTable(rowModel,tableHeader);
                table.getColumnModel().getColumn(0).setPreferredWidth(5);
                table.setFillsViewportHeight(true);
                table.setRowHeight(30);
                scrollPane1 = new JScrollPane(table);
            }else {
                JLabel label = new JLabel("No Record of Dispensed Medications");
                label.setFont(new Font("Arial",Font.BOLD,30));
                label.setForeground(Color.WHITE);
                label.setBackground(new Color(50,25,25,200));
                scrollPane1 = new JScrollPane(label);
            }

            addNewButton = new JButton("Add New medication");
            addNewButton.setBounds(700,50,170,40);
            addNewButton.addActionListener(new MyActionListener());

            scrollPane1.setVisible(true);
            scrollPane1.setBounds(100,200,800,300);
            scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            add(scrollPane1);
            add(addNewButton);
            setLayout(null);
        }
    }

    private class NotificationPanel extends JPanel{
        JSplitPane splitPane1;
        JSplitPane splitPane2;
        JList newPrescriptionList;
        JList expiredMedList;
        JList notDispensedList;
        public NotificationPanel(){

            PrescriptionNotifier prescriptionNotifier = new PrescriptionNotifier("jdbc:sqlite:..DBMAtrial.db");
             ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//             AtomicReference<ArrayList<String>> prescriptionsList = new AtomicReference<>();
             executorService.scheduleAtFixedRate(()->{
                 ArrayList<String> prescriptions = prescriptionNotifier.call();
//                 prescriptionsList.set(prescriptions);
             },0,10, TimeUnit.SECONDS);



            expiredMedList = new JList<>();
            JScrollPane expiredListScroll = new JScrollPane(expiredMedList);
            expiredListScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            notDispensedList= new JList<>();
            JScrollPane notDispensedListScroll = new JScrollPane(notDispensedList);
            notDispensedListScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            //newPrescriptionList = new JList<>((ArrayList)prescriptionsList);
            JScrollPane newPrescriptionListScroll = new JScrollPane(newPrescriptionList);
            newPrescriptionListScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,newPrescriptionListScroll,notDispensedListScroll);
            splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane1,expiredListScroll);

            splitPane2.setBounds(50,50,1050,400);
            add(splitPane2);
            setLayout(null);
        }
    }

    private class ReportsPanel extends JPanel{
        JLabel countOfPrescribedMeds;
        JLabel countOfDispensedMeds;
        JLabel nearToExpireMeds;
        JLabel medsInInventory;
        JButton downloadReportButton;

        public ReportsPanel(){
            downloadReportButton = new JButton("Download Report(in .pdf)");
            downloadReportButton.setBounds(800,25,180,20);

            countOfPrescribedMeds = new FrontLabel("<html> Total medications <br>Prescribed</html>");
            countOfPrescribedMeds.setBounds(50,100,250,200);

            countOfDispensedMeds = new FrontLabel("<html> Total medications<br> Dispensed</html>");
            countOfDispensedMeds.setBounds(400,100,250,200);

            nearToExpireMeds = new FrontLabel("<html> Medications Near ExpireDate: <br>"+"</html>");
            nearToExpireMeds.setBounds(750,100,250,200);

            medsInInventory = new FrontLabel("<html>Count of Medications<br> In Inventory</html>");
            medsInInventory.setBounds(250,400,500,100);

            add(countOfPrescribedMeds);
            add(countOfDispensedMeds);
            add(nearToExpireMeds);
            add(medsInInventory);
            add(downloadReportButton);
            setLayout(null);
        }
    }

     static class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == InventoryPanel.addNewButton) {
                JPanel containerPanel = new JPanel();
                NewMedPanel newMedPanel = new NewMedPanel();
                JScrollPane scrollPane2 = new JScrollPane(containerPanel);
                JButton doneButton = new JButton("Done");

                containerPanel.setPreferredSize(new Dimension(400,300));
                containerPanel.add(newMedPanel);
                containerPanel.add(doneButton);
                containerPanel.setLayout(new BoxLayout(containerPanel,BoxLayout.Y_AXIS));

                scrollPane2.setBounds(650,200,400,300);
                scrollPane2.setVisible(true);
                doneButton.addActionListener(e ->{
                    Main.inventoryPanel.remove(containerPanel);//Beki check this if you can
                    System.out.println("doneButton clicked");//Main.inventoryPanel.remove(newMedPanel);
                     });

                InventoryPanel invenPanel = (InventoryPanel)inventoryPanel;
                invenPanel.scrollPane1.setBounds(100,200,500,300);
                inventoryPanel.add(scrollPane2);
            } else if (event.getSource() == HomePanel.availableMedsLabel){

            }
        }
    }

        public static void main(String[] args) {
        new Main();
    }
}

class FrontLabel extends JLabel{
    public FrontLabel(String title){
        super(title);
        setFont(new Font("Serif",Font.BOLD,30));
        setHorizontalAlignment(SwingConstants.CENTER);
        setForeground(Color.WHITE.brighter());
        setBackground(new Color(236,188,200,140));
        setOpaque(true);
    }
}
class NewMedPanel extends JPanel{
    static JTextField nameOfMedication,strength,dosageForm, date,amount;
    static JLabel nameLabel,strengthLabel, formLabel, dateLabel,amountLabel;
    static JButton addToInvenButton;
    public NewMedPanel(){
        nameOfMedication = new JTextField();
        strength = new JTextField();
        dosageForm = new JTextField();
        date = new JTextField();
        amount = new JTextField();

        nameLabel = new JLabel("Name Of medication");
        strengthLabel = new JLabel("Strength");
        formLabel = new JLabel("Dosage Form");
        dateLabel = new JLabel("Expire Date(use yyyy-MM-dd format)");
        amountLabel = new JLabel("Amount");

        addToInvenButton  = new JButton("Add To Inventory");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        addToInvenButton.addActionListener(e -> {
            Registerer registerer = new Registerer("jdbc:sqlite:..DBMAtrial.db");
            NewlyPurchasedDrug newMed = null;
                    try{
                        newMed= new NewlyPurchasedDrug(NewMedPanel.nameOfMedication.getText(),
                                Integer.parseInt(NewMedPanel.strength.getText()),NewMedPanel.dosageForm.getText(),
                                dateFormat.parse(NewMedPanel.date.getText()),Integer.parseInt(NewMedPanel.amount.getText()));
                    }catch(ParseException exception){
                        System.out.println(exception.getMessage());
                    }
            registerer.register(newMed);

        });
        add(nameLabel);add(nameOfMedication);
        add(strengthLabel); add(strength);
        add(formLabel); add(dosageForm);
        add(dateLabel);add(date);
        add(amountLabel);add(amount);
        add(addToInvenButton);

        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
    }
}
class LabelDialogues extends JDialog{
    JScrollPane dialogTable;
    JButton okButton;
    LabelDialogues(DefaultTableModel table2){
        JTable medicationsTable;
        JPanel tablePanel = new JPanel();
        dialogTable = new JScrollPane(tablePanel);

    }
}