import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
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

    private class InventoryPanel extends JPanel{
        JTable table;
        JButton addNewButton;
        JScrollPane scrollPane;
        InventoryPanel(){
            table = new JTable(5,5);
//            tabl
            table.setBorder(border);

            scrollPane = new JScrollPane(table);addNewButton = new JButton("Add New medication");
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