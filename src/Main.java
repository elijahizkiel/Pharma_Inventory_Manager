import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

public class Main extends JFrame {
    Reporter reporter = new Reporter("jdbc:sqlite:..DBMAtrial.db");
    JTabbedPane mainPane;
    JPanel homePanel,inventoryPanel, reportsPanel, notificationPanel;

    public Main(){
        mainPane = new JTabbedPane(SwingConstants.TOP);

        homePanel = new HomePanel();
        inventoryPanel = new InventoryPanel();
        reportsPanel = new ReportsPanel();
//        notificationPanel =


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

        public HomePanel() {
            ResultSet count = null;
            try {
                count = reporter.runQuery("SELECT sum(amount) FROM PrescriptionsRecords");
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
            JLabel availableMedsLabel = new FrontLabel("Available Medications");
            availableMedsLabel.setBounds(50, 50, 300, 200);

            JLabel medsInShortageLabel = new FrontLabel("<html>Medications In<br> shortage </html>");
            medsInShortageLabel.setBounds(400, 50, 300, 200);

            JLabel medsDispensedLabel = new FrontLabel("<html>Medications Dispensed <br> in Last Seven Days</html>");
            medsDispensedLabel.setBounds(750, 50, 300, 200);

            JLabel totalCountLabel = new FrontLabel("<html>Count of total available <br> medications: "+ sum+"</html>");
            totalCountLabel.setBounds(100, 300, 400, 150);

            JLabel typeCountLabel = new FrontLabel("<html>Count of medications by<br> their name: "+ sum+"</html>");
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

    }

    private class ReportsPanel extends JPanel{
        JLabel countOfPrescribedMeds;
        JLabel countOfDispensedMeds;
        JLabel nearToExpireMeds;
        JLabel medsInInventory;
        JComboBox downloadReportButton;

        public ReportsPanel(){
            downloadReportButton = new JComboBox();
            downloadReportButton.setBounds(700,25,50,20);

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
            setLayout(null);
        }
    }

    class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == InventoryPanel.addNewButton) {
                JPanel containerPanel = new JPanel();
                NewMedPanel newMedPanel = new NewMedPanel();
                JScrollPane scrollPane2 = new JScrollPane(containerPanel);
                JButton addToInvenButton = new JButton("Add To Inventory");

                containerPanel.setPreferredSize(new Dimension(400,300));
                containerPanel.add(newMedPanel);
                containerPanel.add(addToInvenButton);
                containerPanel.setLayout(new BoxLayout(containerPanel,BoxLayout.Y_AXIS));

                scrollPane2.setBounds(650,200,400,300);
                scrollPane2.setVisible(true);

                InventoryPanel invenPanel = (InventoryPanel)inventoryPanel;
                invenPanel.scrollPane1.setBounds(100,200,500,300);
                inventoryPanel.add(scrollPane2);
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
    JTextField nameOfMedication,strength,dosageForm, date,amount;
    JLabel nameLabel,strengthLabel, formLabel, dateLabel,amountLabel;
    public NewMedPanel(){
        nameOfMedication = new JTextField();
        strength = new JTextField();
        dosageForm = new JTextField();
        date = new JTextField();
        amount = new JTextField();

        nameLabel = new JLabel("Name Of medication");
        strengthLabel = new JLabel("Strength");
        formLabel = new JLabel("Dosage Form");
        dateLabel = new JLabel("Date");
        amountLabel = new JLabel("Amount");

        add(nameLabel);add(nameOfMedication);
        add(strengthLabel); add(strength);
        add(formLabel); add(dosageForm);
        add(dateLabel);add(date);
        add(amountLabel);add(amount);

        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
    }
}
