import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;


public class Main extends JFrame {
    Reporter reporter = new Reporter("jdbc:sqlite:..DBMAtrial.db");
    Registerer registerer = new Registerer("jdbc:sqlite:..DBMAtrial.db");
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
    class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {

            if (event.getSource() == InventoryPanel.addNewButton) {
                JPanel containerPanel = new JPanel();
                NewMedPanel newMedPanel = new NewMedPanel();
                JScrollPane scrollPane2 = new JScrollPane(containerPanel);
                JButton doneButton = new JButton("Done");

                containerPanel.setPreferredSize(new Dimension(400, 300));
                containerPanel.add(newMedPanel);
                containerPanel.add(doneButton);
                containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));

                scrollPane2.setBounds(650, 200, 400, 300);
                scrollPane2.setVisible(true);
                doneButton.addActionListener(e -> {
                    Main.inventoryPanel.remove(containerPanel);//Beki check this if you can
                    System.out.println("doneButton clicked");//Main.inventoryPanel.remove(newMedPanel);
                });

                InventoryPanel invenPanel = (InventoryPanel) inventoryPanel;
                invenPanel.scrollPane.setBounds(100, 200, 500, 300);
                inventoryPanel.add(scrollPane2);
            } else if (event.getSource() == LabelDialog.okButton) {
                try{
                    Main.HomePanel.medsInShortage.dispose();
                }catch(Exception e){
                    System.out.println("No LabelDialog of medsInShortage "+ e.getMessage());
                }
                try{
                    HomePanel.availableMeds.dispose();
                }catch (Exception e){
                    System.out.println("No LabelDialog of availableMeds " + e.getMessage());
                }
                try{
                    HomePanel.medsInLast7Days.dispose();
                }catch (Exception e){
                    System.out.println("No LabelDialog of medsInLast7Days " + e.getMessage());
                }
            } else if (event.getSource() == ReportsPanel.downloadReportButton) {
                Reporter reporter = new Reporter("jdbc:sqlite:..DBMAtrial.db");
                reporter.pdfGenerator("DBMAtrialDispensed","Dispense");
                reporter.pdfGenerator("DBMAtrialPrescribed", "Prescribe");
            }
        }
    }


    protected class HomePanel extends JPanel{
        static JLabel availableMedsLabel,medsInShortageLabel, medsDispensedLabel, typeCountLabel, totalCountLabel;
        static JDialog availableMeds, medsInShortage, medsInLast7Days;

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

            ResultSet table = null;
            try{
                table = reporter.runQuery("SELECT nameOfMedication, SUM(amount) FROM MedicationInStock GROUP BY nameOfMedication,dosageForm, strength","");
            }catch(Exception e){
                System.out.println("Can't runQuery In group to get meds ");
            }
            int counter = 0;
            try {
                while (true){
                    assert table != null;
                    if (!table.next()) break;
                    ++counter;
                }
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }

            //create components to be added to HomePanel
            availableMedsLabel = new FrontLabel("Available Medications");
            availableMedsLabel.setBounds(50, 50, 300, 200);
            availableMedsLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getSource() == HomePanel.availableMedsLabel) {
                        JScrollPane scrollPane = getTable("availableMedsLabel");

                        availableMeds = new LabelDialog(scrollPane);
                        availableMeds.setVisible(true);
                    } ;
                }
            });

            medsInShortageLabel = new FrontLabel("<html>Medications In<br> shortage </html>");
            medsInShortageLabel.setBounds(400, 50, 300, 200);
            medsInShortageLabel.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e){
                    if (e.getSource() == HomePanel.medsInShortageLabel){
                        medsInShortage = new LabelDialog(getTable("medsInShortage"));
                        medsInShortage.setVisible(true);
                    }
                }
            });

            medsDispensedLabel = new FrontLabel("<html>Medications Dispensed <br> in Last Seven Days</html>");
            medsDispensedLabel.setBounds(750, 50, 300, 200);
            medsDispensedLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(e.getSource() == HomePanel.medsDispensedLabel){
                        medsInLast7Days = new LabelDialog(getTable("medsDispensedLabel"));
                        medsInLast7Days.setVisible(true);
                    }
                }
            });

            totalCountLabel = new FrontLabel("<html>Count of total available <br> medications: "+ sum+"</html>");
            totalCountLabel.setBounds(100, 300, 400, 150);

            typeCountLabel = new FrontLabel("<html>Count of medications by<br> their name and form and strength: "+ counter + "</html>");
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

    protected class InventoryPanel extends JPanel{
        JTable table;
        static JButton addNewButton;
        JScrollPane scrollPane;
        static JOptionPane notification;
        InventoryPanel(){

            addNewButton = new JButton("Add New medication");
            addNewButton.setBounds(700,50,170,40);
            addNewButton.addActionListener(new MyActionListener());

            scrollPane = getTable("inventoryPanel");
            scrollPane.setVisible(true);
            scrollPane.setBounds(100,200,800,300);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            add(scrollPane);
            add(addNewButton);
            setLayout(null);
        }
    }

    protected static class NotificationPanel extends JPanel{
        JSplitPane splitPane1;
        JSplitPane splitPane2;
        JList newPrescriptionList;
        JList expiredMedList;
        JList notDispensedList;
        public NotificationPanel(){

            PrescriptionNotifier prescriptionNotifier = new PrescriptionNotifier("jdbc:sqlite:..DBMAtrial.db");
             ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
             AtomicReference<ArrayList<String>> prescriptionsList = new AtomicReference<>();
             executorService.scheduleAtFixedRate(()->{
                 ArrayList<String> prescriptions = prescriptionNotifier.call();
                 prescriptionsList.set(prescriptions);
             },0,10, TimeUnit.SECONDS);



            expiredMedList = new JList<>();
            JScrollPane expiredListScroll = new JScrollPane(expiredMedList);
            expiredListScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            notDispensedList= new JList<>();
            JScrollPane notDispensedListScroll = new JScrollPane(notDispensedList);
            notDispensedListScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            newPrescriptionList = new JList<>();
            JScrollPane newPrescriptionListScroll = new JScrollPane(newPrescriptionList);
            newPrescriptionListScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,newPrescriptionListScroll,notDispensedListScroll);
            splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane1,expiredListScroll);

            splitPane2.setBounds(50,50,1050,400);
            add(splitPane2);
            setLayout(null);
        }
    }

    protected class ReportsPanel extends JPanel{
        JLabel countOfPrescribedMeds;
        JLabel countOfDispensedMeds;
        JLabel nearToExpireMeds;
        JLabel medsInInventory;
        static JButton downloadReportButton;

        public ReportsPanel(){
            downloadReportButton = new JButton("Download Report(in .pdf)");
            downloadReportButton.setBounds(800,25,180,20);
            downloadReportButton.addActionListener(new MyActionListener());

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

    public static void main(String[] args) {
        new Main();
    }
    private JScrollPane getTable(String requester){
        JTable table;
        JScrollPane scrollPane1;
        if(Objects.equals(requester,"inventoryPanel")){
            Object[][] tableData = reporter.showDispensed();
            if(tableData[0][0] != null){
                String[] tableHeader = {"No","Name Of Medication", "Dosage Form","Strength", "Frequency"};


                table = new JTable(tableData,tableHeader);
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
        } else if (Objects.equals(requester,"availableMedsLabel")) {
            Object[][] tableData = new Registerer("jdbc:sqlite:..DBMAtrial.db").getMedsInCount();
            Object[] tableHeader = {"No","Name Of Medication", "Dosage Form", "Strength(mg or mg/ml)", "Amount(count)"};
            if(tableData[0][0] != null){
                table = new JTable(tableData,tableHeader);
                table.getColumnModel().getColumn(0).setPreferredWidth(5);
                table.setFillsViewportHeight(true);
                table.setRowHeight(30);
                scrollPane1 = new JScrollPane(table);

            }else {
                JLabel label = new JLabel("No Medications are available");
                label.setFont(new Font("Arial",Font.BOLD,30));
                label.setForeground(Color.WHITE);
                label.setBackground(new Color(50,25,25,200));
                scrollPane1 = new JScrollPane(label);
            }
        } else if (Objects.equals(requester,"medsInShortage")) {
            Object[][] tableData = new Registerer("jdbc:sqlite:..DBMAtrial.db").getMedsInShortage();
            Object[] tableHeader = {"No","Name of Medications", "Dosage Form", "Strength", "Amount"};
            if(tableData[0][0] != null){


                table = new JTable(tableData,tableHeader);
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
        } else if (Objects.equals(requester,"medsDispensed")) {
            Object[][] rowData = registerer.medsInLast7days();
            Object[] tableHeader ={"No","Name of Medication","Dosage Form", "Strength", "Amount"};
            table = new JTable(rowData,tableHeader);
            scrollPane1 = new JScrollPane(table);
        }
        else scrollPane1 = new JScrollPane(new JLabel("No Data to display"));
        return scrollPane1;
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
    static JButton addToInventButton, doneButton;

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

        addToInventButton = new JButton("Add To Inventory");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        addToInventButton.addActionListener(e -> {
            Registerer registerer = new Registerer("jdbc:sqlite:..DBMAtrial.db");
            NewlyPurchasedDrug newMed = null;
            try{
                newMed= new NewlyPurchasedDrug(NewMedPanel.nameOfMedication.getText(),
                        Integer.parseInt(NewMedPanel.strength.getText()),NewMedPanel.dosageForm.getText(),
                        dateFormat.parse(NewMedPanel.date.getText()),Integer.parseInt(NewMedPanel.amount.getText()));
            }catch(ParseException exception){
                System.out.println(exception.getMessage());
            }catch (Exception ex){
                System.out.println(ex.getMessage());
                Main.InventoryPanel inventoryPanel= (Main.InventoryPanel) Main.inventoryPanel;
               Main.InventoryPanel.notification = new JOptionPane("NO medication Inserted,Please add some medication",JOptionPane.WARNING_MESSAGE,JOptionPane.OK_CANCEL_OPTION);
                Main.InventoryPanel.notification.setVisible(true);
                inventoryPanel.add(Main.InventoryPanel.notification);
            }
            if(newMed != null)registerer.register(newMed);
        });
        add(nameLabel);add(nameOfMedication);
        add(strengthLabel); add(strength);
        add(formLabel); add(dosageForm);
        add(dateLabel);add(date);
        add(amountLabel);add(amount);
        add(addToInventButton);

        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
    }
}
class LabelDialog extends JDialog{
    JScrollPane dialogTable;
    public static JButton okButton = new JButton("OK");
    LabelDialog(JScrollPane scrollPane){
        dialogTable = scrollPane;
        dialogTable.setBounds(5,5,400,300);

        okButton.setBounds(190,310,70,20);
        okButton.addActionListener(new Main.MyActionListener());

        setSize(new Dimension(410,380));
        add(dialogTable);
        add(okButton);

        setLayout(null);
    }
}}