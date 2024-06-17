import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import java.text.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.*;


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

                count = reporter.runQuery("SELECT sum(amount) FROM MedicationInStock");
            }catch(Exception e) {
                System.out.println("can't send query");
            }
            int sum = 0;
            try{
                if(count != null)sum = count.getInt(1);
            }catch(SQLException e){System.out.println("can't sum-up amount");}

            ResultSet table = null;
            try{
                table = reporter.runQuery("SELECT nameOfMedication, SUM(amount) FROM MedicationInStock GROUP BY nameOfMedication,dosageForm, strength");
            }catch(Exception e){
                System.out.println("Can't runQuery In group to get meds ");
            }
            int counter = 0;
            try {
                while (true){
                    if(table != null){
                        if (!table.next()) break;
                        ++counter;
                    }
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
                        availableMeds = new LabelDialog(getTable("availableMedsLabel"));
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
                        medsInLast7Days = new LabelDialog(getTable("medsDispensed"));
                        medsInLast7Days.setVisible(true);
                    }
                }
            });

            totalCountLabel = new FrontLabel("<html>Count of total available <br> medications: " + sum + "</html>");
            totalCountLabel.setBounds(100, 300, 400, 150);

            typeCountLabel = new FrontLabel("<html>Count of medications by<br> their name and form and strength: " + counter + "</html>");
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

        JPanel newPrescriptionList;
        JPanel expiredMedList;
        ArrayList<Prescription> prescriptionsList = new ArrayList<>();
        JButton getNewPrescriptionsButton = new JButton("<html>Get new <br>Prescriptions</html>");
        JButton getListOfExpiredMeds = new JButton("<html>Get Newly <br>Expired Medications</html>");
        JScrollPane newPrescriptionListScroll;
        GridBagConstraints gridConstr = new GridBagConstraints();
        ArrayList<JLabel> prescriptionLabels = new ArrayList<>();
        public NotificationPanel() {
            JLabel headerLabel =new JLabel("Prescriptions");
            headerLabel.setFont(new Font("Serif",Font.BOLD,20));
            newPrescriptionList = new JPanel();
            GridBagLayout layout = new GridBagLayout();

            newPrescriptionList.setLayout(layout);
            newPrescriptionList.add(headerLabel);
            newPrescriptionList.setSize(400,500);

            newPrescriptionListScroll = new JScrollPane(newPrescriptionList);
            newPrescriptionListScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            newPrescriptionListScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            newPrescriptionListScroll.setBounds(40,20,400,500);

            expiredMedList = new JPanel();
            JScrollPane expiredListScroll = new JScrollPane(expiredMedList);
            expiredListScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            expiredListScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            expiredListScroll.setBounds(480,20,400,500);
            getNewPrescriptionsButton.setBounds(900,50, 150,40);
            getListOfExpiredMeds.setBounds(900,110, 150,40);

            getNewPrescriptionsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setPrescriptionsList();
                    addLabels(prescriptionsList);
                }
            });

            add(getNewPrescriptionsButton);
            add(getListOfExpiredMeds);
            add(newPrescriptionListScroll);
            add(expiredListScroll);
            setLayout(null);
        }

        public void setPrescriptionsList() {
            PrescriptionNotifier notifier;// = deserialize();
            //if(notifier == null)
            notifier = new PrescriptionNotifier("jdbc:sqlite:..DBMAtrial.db");

            System.out.println("going for  thread");

            ArrayList<Prescription> prescriptions = notifier.call();
            System.out.println("my prescriptions: \n" + prescriptions);
            if (!prescriptions.isEmpty()) {
                prescriptionsList.addAll(prescriptions);
            }
            serialize(notifier);
        }


        void addLabels(Prescription prescription){
            JLabel newLabel = new JLabel(prescription.toString());
            newLabel.setBackground(new Color(65, 142, 224));
            newLabel.setFont(new Font("Serif",Font.BOLD,17));
            newLabel.setOpaque(true);
            newLabel.setMinimumSize(new Dimension(300,30));
            newLabel.setPreferredSize(new Dimension(400,35));
            newLabel.setBorder(BorderFactory.createLineBorder(new Color(190,200,200),8));

            ++gridConstr.gridy;
//            gridConstr.fill = GridBagConstraints.HORIZONTAL;
//            gridConstr.anchor = GridBagConstraints.NORTH;

            prescriptionLabels.add(newLabel);
            newPrescriptionList.add(newLabel,gridConstr);
            revalidate();
        }

        void addLabels(ArrayList<Prescription> prescriptions){
            for(Prescription prescription : prescriptions){
                System.out.println("adding Labels .....");
                addLabels(prescription);
            }
        }
        public void addAll(ArrayList<JLabel> comps, Container container){
            if(!comps.isEmpty()){
                for(JComponent comp : comps){
                    container.add(comp);
                }
            }
        }
    }

    protected class ReportsPanel extends JPanel{
        JLabel countOfPrescribedMeds;
        JLabel countOfDispensedMeds;
        JLabel nearToExpireMeds;
        JLabel medsInInventory;
        static JButton downloadReportButton;
        LabelDialog prescriptionsDialog;
        LabelDialog dispenseDialog;
        LabelDialog expiredMedsDialog;
        LabelDialog medsInInventoryDialog;
        public ReportsPanel(){
            ResultSet countOfPrescriptions = reporter.runQuery("SELECT SUM(amount) FROM PrescriptionsRecords");
            int prescriptionsCount = 0;
            try{
                prescriptionsCount = countOfPrescriptions.getInt("SUM(amount)");
            }catch(SQLException ex){
                System.out.println("from prescription count "+ex.getMessage());
            }
            ResultSet countOfDispenses = reporter.runQuery("SELECT  SUM(amount) FROM PrescriptionsRecords WHERE isDispensed = 1");
            int dispenseCount = 0;
            try{
                dispenseCount = countOfDispenses.getInt("SUM(amount)");
            }catch(SQLException ex){
                System.out.println("from dispenses count " + ex.getMessage());
            }
            long date = System.currentTimeMillis();
            ResultSet nearExpire = reporter.runQuery("SELECT nameOfMedication, strength, dosageForm, expireDate, count(*) FROM MedicationInStock WHERE ((" + date + " -expireDate) <7776000000)");
            int nearToExpire=0;
            try{
                nearToExpire = nearExpire.getInt("count(*)");
            }catch (SQLException ex){
                System.out.println("from near to expire "+ ex.getMessage());
            }

            downloadReportButton = new JButton("Download Report(in .pdf)");
            downloadReportButton.setBounds(800,25,180,20);
            downloadReportButton.addActionListener(new MyActionListener());

            countOfPrescribedMeds = new FrontLabel("<html> Total medications <br>Prescribed: " + prescriptionsCount + "</html>");
            countOfPrescribedMeds.setBounds(50,100,250,200);
            countOfPrescribedMeds.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ResultSet prescriptions = reporter.runQuery("SELECT nameOfMedication, strength, dosageForm, count(*) FROM PrescriptionsRecords GROUP BY nameOfMedication, strength, dosageForm");
                    Object[][] prescriptionData = registerer.formTable(prescriptions);
                    Object[] title = {"No", "Name of medication", "Strength", "Dosage Form","Frequency"};
                    JTable prescriptionsTable = new JTable(prescriptionData,title);
                    if(e.getSource() == countOfPrescribedMeds){
                        JScrollPane prescriptionsScroll = new JScrollPane(prescriptionsTable);
                        prescriptionsDialog = new LabelDialog(prescriptionsScroll);
                        prescriptionsDialog.setVisible(true);
                    }
                }
            });

            countOfDispensedMeds = new FrontLabel("<html> Total medications<br> Dispensed:" + dispenseCount + "</html>");
            countOfDispensedMeds.setBounds(400,100,250,200);
            countOfDispensedMeds.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ResultSet dispenses = reporter.runQuery("SELECT nameOfMedication, strength, dosageForm, count(*)" +
                            " FROM DispenseRecords GROUP BY nameOfMedication, strength, dosageForm");
                    Object[][] dispenseData = registerer.formTable(dispenses);
                    Object[] title = {"No", "Name of medication", "Strength", "Dosage Form","Frequency"};
                    JTable prescriptionsTable = new JTable(dispenseData,title);
                    if(e.getSource() == countOfDispensedMeds){
                        JScrollPane dispenseScroll = new JScrollPane(prescriptionsTable);
                        dispenseDialog = new LabelDialog(dispenseScroll);
                        dispenseDialog.setVisible(true);
                    }
                }
            });

            nearToExpireMeds = new FrontLabel("<html> Medications Near ExpireDate: <br>" + nearToExpire + "</html>");
            nearToExpireMeds.setBounds(750,100,250,200);
            nearToExpireMeds.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ResultSet nearToExpire = reporter.runQuery("SELECT nameOfMedication, strength, dosageForm, count(*) " +
                            "FROM MedicationInStock GROUP BY nameOfMedication,strength, dosageForm, expireDate " +
                            "HAVING ((" + date + "-expireDate) < 7776000000)");
                    Object[][] medicationData = registerer.formTable(nearToExpire);
                    Object[] title = {"No", "Name of medication", "Strength", "Dosage Form","Frequency"};
                    JTable expireMedsTable = new JTable(medicationData,title);
                    if(e.getSource() == nearToExpireMeds){
                        JScrollPane nearToExpireMedsScroll = new JScrollPane(expireMedsTable);
                        expiredMedsDialog = new LabelDialog(nearToExpireMedsScroll);
                        expiredMedsDialog.setVisible(true);
                    }
                }
            });

            medsInInventory = new FrontLabel("<html>Count of Medications<br> In Inventory</html>");
            medsInInventory.setBounds(250,400,500,100);
            medsInInventory.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ResultSet inventoryMeds = reporter.runQuery("SELECT nameOfMedication, strength, dosageForm, count(*) " +
                            "FROM MedicationInStock GROUP BY nameOfMedication,strength, dosageForm, expireDate ");
                    Object[][] medicationData = registerer.formTable(inventoryMeds);
                    Object[] title = {"No", "Name of medication", "Strength", "Dosage Form","Frequency"};
                    JTable inventoryMedsTable = new JTable(medicationData,title);
                    if(e.getSource() == medsInInventory){
                        JScrollPane inventoryMedsScroll = new JScrollPane(inventoryMedsTable);
                        medsInInventoryDialog = new LabelDialog(inventoryMedsScroll);
                        medsInInventoryDialog.setVisible(true);
                    }
                }
            });

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
    private static void serialize(Object object){
        try(FileOutputStream fileOut = new FileOutputStream("notify.ser");
        ObjectOutputStream outObj = new ObjectOutputStream(fileOut)){
            outObj.writeObject(object);
        }
        catch(IOException IOe){
            System.out.println("from serialize method " + IOe.getMessage());
        }
    }

    private static PrescriptionNotifier deserialize(){
        PrescriptionNotifier notifier =  null;
        try(FileInputStream fileIn = new FileInputStream("notify.ser");
        ObjectInputStream inObj = new ObjectInputStream(fileIn))
        {
            notifier = (PrescriptionNotifier) inObj.readObject();
        }catch(IOException|ClassNotFoundException CNF){
            System.out.println("from deserialize Method" + CNF.getMessage());
        }
        return notifier;
    }

    private JScrollPane getTable(String requester){
        JTable table;
        JScrollPane scrollPane1;
        switch (requester) {
            case "inventoryPanel" -> {
                Object[][] tableData = reporter.showDispensed();
                if (tableData[0][0] != null) {
                    String[] tableHeader = {"No", "Name Of Medication", "Dosage Form", "Strength", "Frequency"};

                    table = new JTable(tableData, tableHeader);
                    table.getColumnModel().getColumn(0).setPreferredWidth(5);
                    table.setFillsViewportHeight(true);
                    table.setRowHeight(30);
                    scrollPane1 = new JScrollPane(table);

                } else {
                    JLabel label = new JLabel("No Record of Dispensed Medications");
                    label.setFont(new Font("Arial", Font.BOLD, 30));
                    label.setForeground(Color.WHITE);
                    label.setBackground(new Color(50, 25, 25, 200));
                    scrollPane1 = new JScrollPane(label);
                }
            }
            case "availableMedsLabel" -> {
                Object[][] tableData = new Registerer("jdbc:sqlite:..DBMAtrial.db").getMedsInCount();
                Object[] tableHeader = {"No", "Name Of Medication", "Dosage Form", "Strength(mg or mg/ml)", "Amount(count)"};
                if (tableData[0][0] != null) {
                    table = new JTable(tableData, tableHeader);
                    table.getColumnModel().getColumn(0).setPreferredWidth(5);
                    table.setFillsViewportHeight(true);
                    table.setRowHeight(30);
                    scrollPane1 = new JScrollPane(table);

                } else {
                    JLabel label = new JLabel("No Medications are available");
                    label.setFont(new Font("Arial", Font.BOLD, 30));
                    label.setForeground(Color.WHITE);
                    label.setBackground(new Color(50, 25, 25, 200));
                    scrollPane1 = new JScrollPane(label);
                }
            }
            case "medsInShortage" -> {
                Object[][] tableData;
                Object[] tableHeader = {"No", "Name of Medications", "Dosage Form", "Strength", "Amount"};
                if(registerer.getMedsInShortage() != null)
                    tableData = registerer.getMedsInShortage();
                else tableData = new Object[2][5];
                if (tableData != null) {
                    table = new JTable(tableData, tableHeader);
                    table.getColumnModel().getColumn(0).setWidth(5);
                    table.setFillsViewportHeight(true);
                    table.setRowHeight(30);
                    scrollPane1 = new JScrollPane(table);

                } else {
                    JLabel label = new JLabel("No Record of Dispensed Medications");
                    label.setFont(new Font("Arial", Font.BOLD, 30));
                    label.setForeground(Color.WHITE);
                    label.setBackground(new Color(50, 25, 25, 200));
                    scrollPane1 = new JScrollPane(label);
                }
            }
            case "medsDispensed" -> {
                Object[][] rowData = registerer.medsInLast7days();
                Object[] tableHeader = {"No", "Name of Medication", "Dosage Form", "Strength", "Amount"};
                System.out.println(Arrays.deepToString(rowData));
                if(rowData[0][0] != null){
                    table = new JTable(rowData, tableHeader);
                    scrollPane1 = new JScrollPane(table);
                }else{
                    JLabel messageLabel = new JLabel("No medications dispensed in the last 7 days");
                    scrollPane1 = new JScrollPane(messageLabel);
                }
            }
            case null, default -> scrollPane1 = new JScrollPane(new JLabel("No Data to display"));
        }
        return scrollPane1;
}


static class FrontLabel extends JLabel{
    public FrontLabel(String title){
        super(title);
        setFont(new Font("Serif",Font.BOLD,30));
        setHorizontalAlignment(SwingConstants.CENTER);
        setForeground(Color.WHITE.brighter());
        setBackground(new Color(236,188,200,140));
        setOpaque(true);
    }
}

static class NewMedPanel extends JPanel{
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
        dialogTable.setBounds(5,5,450,300);

        okButton.setBounds(190,310,70,20);
        okButton.addActionListener(new Main.MyActionListener());

        setSize(new Dimension(410,380));
        add(dialogTable);
        add(okButton);
        setBounds(100,20,500,350);
        setLayout(null);
    }
}}

class NotificationSlide {}