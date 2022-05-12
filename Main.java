import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.util.List;
import java.awt.event.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.text.DecimalFormat;

public class Main {  
    private static JFrame login = new JFrame();
    private static JFrame POS = new JFrame();

    private static Double totalCost = 0d;

    private static JTable cartTable;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    private static GroceryDatabaseConnector groceryDatabaseConnector = new GroceryDatabaseConnector();

    public static void main(String[] args) { 
//        groceryDatabaseConnector.getGroceryItems();
//        groceryDatabaseConnector.getTransactions();
        groceryDatabaseConnector.getItemsFromSpecificTransaction(1);
        groceryDatabaseConnector.getClerkTransactions("tli30");
        makeLogin();
        makePOS(); 
    }  

    private static void makeLogin(){
        JLabel title = new JLabel("A Real P.O.S.");
        title.setBounds(13,12,175,40);
        login.add(title);

        JTextField userName = new JTextField("Username");
        userName.setBounds(50,85,100,20);
        login.add(userName);   

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(50,138,100,20);
        login.add(passwordField);   
        
        JButton submit = new JButton("Login");
        submit.setBounds(65,176,70,16);
        login.add(submit);

        submit.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                POS.setVisible(true);
                System.out.println(userName.getText());
                System.out.println(passwordField.getPassword());
                login.setVisible(false);
            }  
        });
        
        login.setSize(200,300);
        login.setLayout(null);//using no layout managers  
        login.setVisible(true);//making the frame visible  
    }

    private static void makePOS(){
        JButton addItem = new JButton("Add:");
        addItem.setBounds(10,540,60,30);
        POS.add(addItem);

        JLabel total = new JLabel("Total: 0.00");
        total.setBounds(10,565,257,41);
        POS.add(total);

        JTextField itemNumber = new JTextField();
        itemNumber.setBounds(80,540,120,30);
        POS.add(itemNumber);

        itemNumber.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if ( ((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE)) {
                     e.consume(); 
                }
            }
        });

        StockTableModel stockTableModel = new StockTableModel();

        GroceryCartTableModel cartTableModel = new GroceryCartTableModel(stockTableModel);
        cartTableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
               totalCost = cartTableModel.getTotal();
               total.setText("$"+df.format(totalCost));
               POS.repaint();
            }
          });

        cartTable = new JTable(cartTableModel);
        JScrollPane scrollableCartList = new JScrollPane(cartTable);
        scrollableCartList.setBounds(16,16, 278, 512);
        POS.add(scrollableCartList);

        JButton deleteItem = new JButton("Delete");
        deleteItem.setBounds(210,540,90,30);
        POS.add(deleteItem);

        deleteItem.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                cartTableModel.removeRow(cartTable.getSelectedRow());
            }  
        });

        List<GroceryItem> allItems = List.of(new GroceryItem("carrots", 12, 12.31, 15), new GroceryItem("potatoes", 123, 0.32, 6));

        Object[] data = new Object[4];
        for (GroceryItem I: allItems) {
            data[0] = I.name;
            data[1] = I.id;
            data[2] = I.cost;
            data[3] = I.stock;
            stockTableModel.addRow(data);
        }

        JTable itemTable =  new JTable(stockTableModel);
        JScrollPane scrollableItemTable = new JScrollPane(itemTable);
        scrollableItemTable.setBounds(339,206, 245, 322);
        POS.add(scrollableItemTable);

        JPanel listOfItemsAvailable = new JPanel();
        listOfItemsAvailable.setBackground(new ColorUIResource(100, 100, 100));
        listOfItemsAvailable.setBounds(333,200,257,334);
        
        POS.add(listOfItemsAvailable);

        JPanel listOfItemsInCart = new JPanel();
        listOfItemsInCart.setBackground(new ColorUIResource(100, 100, 100));
        listOfItemsInCart.setBounds(10,10,290,524);
        POS.add(listOfItemsInCart);

        JButton completeTransaction = new JButton("complete Transaction");
        completeTransaction.setBounds(333,540,257,50);
        POS.add(completeTransaction);

        completeTransaction.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                System.out.println("cart items in cartTableModel...");
                System.out.println(totalCost);
                cartTableModel.clear();
            }  
        });

        JLabel welcome = new JLabel("Welcome ---------", SwingConstants.CENTER);
        welcome.setBounds(333,10,257,41);
        POS.add(welcome);

        JButton seeYourTransactions = new JButton("See your transactions");
        seeYourTransactions.setBounds(358,51,207,32);
        POS.add(seeYourTransactions);

        JButton seeAllTransactions = new JButton("See all transactions");
        seeAllTransactions.setBounds(358,88,207,32);
        POS.add(seeAllTransactions);

        JTextField newItemName = new JTextField("Manage Inventory");
        newItemName.setBounds(358,125,207,28);
        POS.add(newItemName);

        JPanel availableData = new JPanel();
        availableData.setBackground(new ColorUIResource(100, 100, 100));
        availableData.setBounds(333,10,257,184);
        POS.add(availableData);

        POS.setSize(620,640);
        POS.setLayout(null);
        POS.setVisible(false);        
        
        addItem.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                if(itemNumber.getText().length() > 0 && itemNumber.getText().length() < 10){
                    Integer idNumber = Integer.parseInt(itemNumber.getText());
                    System.out.println("adding: " + itemNumber.getText());
                    if(stockTableModel.getByID(idNumber) != null && cartTableModel.getByID(idNumber) == null){
                        GroceryItem GCI = stockTableModel.getByID(idNumber);
                        Object[] row = {GCI.id, GCI.name, 1, GCI.cost};
                        itemNumber.setText("");
                        cartTableModel.addRow(row);
                    }
                }
            }  
        });
    }
}  