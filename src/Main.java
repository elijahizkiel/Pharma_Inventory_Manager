import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class Main extends JFrame {
    JTabbedPane mainPane;
    JPanel homePanel,inventoryPanel, reportsPanel, notificationPanel;
    Font font = new Font("Arial",Font.ITALIC,20);
    Color color = new Color(236,188,200,70);
    Border border = BorderFactory.createLineBorder(Color.LIGHT_GRAY,25,true);

    public Main(){
        mainPane = new JTabbedPane(SwingConstants.TOP);

        homePanel = new HomePanel();
        inventoryPanel = new InventoryPanel();

        mainPane.addTab("Home", homePanel);
        mainPane.addTab("Inventory", inventoryPanel);
        mainPane.addTab("Reports", reportsPanel);
        mainPane.addTab("Notifications", notificationPanel);
        mainPane.setBackground(new Color(37,78,138,200));

        add(mainPane);
        setTitle("Pharmacy Inventory Manager");
        setSize(1400,900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
    private class HomePanel extends JPanel{

        JLabel availableMedsLabel = new JLabel("Available Medications");
        JLabel medsInShortageLabel = new JLabel("Medications In shortage");
        JLabel medsDispensedLabel = new JLabel("Medications Dispensed in Last week");
        JLabel totalCountLabel = new JLabel("Count of total available medications: ");
        JLabel typeCountLabel = new JLabel("Count of medications by their name: ");
        public HomePanel() {
            availableMedsLabel.setBounds(50, 50, 300, 200);
            availableMedsLabel.setBackground(Color.lightGray);
            availableMedsLabel.setForeground(Color.WHITE);
            availableMedsLabel.setOpaque(true);
            availableMedsLabel.setFont(font);
            availableMedsLabel.setBorder(border);

            medsInShortageLabel.setBounds(400, 50, 300, 200);
            medsInShortageLabel.setForeground(Color.WHITE);
            medsInShortageLabel.setBackground(Color.lightGray);
            medsInShortageLabel.setOpaque(true);
            medsInShortageLabel.setFont(font);
            medsInShortageLabel.setBorder(border);

            medsDispensedLabel.setBounds(750, 50, 300, 200);
            medsDispensedLabel.setForeground(Color.WHITE);
            medsDispensedLabel.setBackground(Color.lightGray);
            medsDispensedLabel.setOpaque(true);
            medsDispensedLabel.setFont(font);
            medsDispensedLabel.setBorder(border);

            totalCountLabel.setBounds(100, 300, 400, 150);
            totalCountLabel.setBackground(Color.lightGray);
            totalCountLabel.setOpaque(true);
            totalCountLabel.setFont(font);
            totalCountLabel.setBorder(border);
            totalCountLabel.setForeground(Color.WHITE);

            typeCountLabel.setBorder(border);
            typeCountLabel.setForeground(Color.WHITE);
            typeCountLabel.setBackground(Color.lightGray);
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

    private static class InventoryPanel extends JPanel{
        JTable table;
        JButton addNewButton;
        JScrollPane scrollPane;
        InventoryPanel(){
            Reporter reporter = new Reporter("jdbc:sqlite:..DBMAtrial.db");
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
                scrollPane = new JScrollPane(table);
            }else {
                JLabel label = new JLabel("No Record of Dispensed Medications");
                label.setFont(new Font("Arial",Font.BOLD,30));
                label.setForeground(Color.WHITE);
                label.setBackground(new Color(50,25,25,200));
                scrollPane = new JScrollPane(label);
            }

            addNewButton = new JButton("Add New medication");
            addNewButton.setBounds(700,50,170,40);

            scrollPane.setVisible(true);
            scrollPane.setBounds(100,200,800,300);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            setBackground(Color.lightGray);
            add(scrollPane);
            add(addNewButton);
            setLayout(null);
        }
    }
    public static void main(String[] args) {
        new Main();
    }
}