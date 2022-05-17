import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.text.DecimalFormat;

public class Main {  
    private static JFrame login = new JFrame();
    private static JFrame POS = new JFrame();
    private static JFrame myTransactions = new JFrame();

    private static Double totalCost = 0d;

    private static JTable cartTable;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    private static GroceryDatabaseConnector groceryDatabaseConnector = new GroceryDatabaseConnector();

    private static String loginName = "";

    private static StockTableModel stockTableModel = new StockTableModel();
    private static TransactionTableModel transactionModel = new TransactionTableModel(stockTableModel);

    public static void main(String[] args) { 
//        groceryDatabaseConnector.getGroceryItems();
//        groceryDatabaseConnector.getTransactions();
//        groceryDatabaseConnector.getItemsFromSpecificTransaction(1);
//        groceryDatabaseConnector.getClerkTransactions("tli30");
        makeLogin();
        login.setTitle("Login Point");
        makePOS();
        POS.setTitle("Point of Service 3000");
        makeMyTransactions();
        myTransactions.setTitle("Transactions Menu");
    }

    private static void setLogin(String name){
        loginName = name;
    }

    private static void makeLogin(){
        JLabel title = new JLabel("A Real P.O.S.");
        title.setBounds(13,12,175,40);
        login.add(title);

        JTextField userName = new JTextField("");
        userName.setBounds(50,85,100,20);
        login.add(userName);   

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(50,138,100,20);
        login.add(passwordField);   
        
        JButton submit = new JButton("Login");
        submit.setBounds(65,176,70,16);
        login.add(submit);

        userName.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent e) {
                if(userName.getText().trim().equals(""))
                    userName.setText("username");
                //do nothing
            }
            public void focusGained(FocusEvent e) {
                if(userName.getText().trim().equals("username"))
                    userName.setText("");
                //do nothing
            }
        });
//        passwordField.addFocusListener(new FocusListener() {
//            public void focusLost(FocusEvent e) {
//                if(passwordField.getText().trim().equals(""))
//                    passwordField.setText("password");
//                //do nothing
//            }
//            public void focusGained(FocusEvent e) {
//                if(passwordField.getText().trim().equals("password"))
//                    passwordField.setText("");
//                //do nothing
//            }
//        });
        submit.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){
                POS.setLocationRelativeTo(null);
                POS.setVisible(true);
                System.out.println(userName.getText());
                System.out.println(passwordField.getPassword());
                setLogin(userName.getText());
                login.setVisible(false);
            }  
        });
        
        login.setSize(200,300);
        login.setLayout(null);//using no layout managers
        login.setLocationRelativeTo(null);
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
                System.out.println("deleting current row: " + cartTable.getSelectedRow());
                System.out.println(cartTable.getSize());
                if(cartTable.getSelectedRow() > -1){
                    cartTableModel.removeRow(cartTable.getSelectedRow());
                }
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

        seeYourTransactions.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                myTransactions.setLocationRelativeTo(null);
                myTransactions.setVisible(true);
                POS.setVisible(false);
            }
        });

        JButton seeAllTransactions = new JButton("See all transactions");
        seeAllTransactions.setBounds(358,88,207,32);
        POS.add(seeAllTransactions);

        seeAllTransactions.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                myTransactions.setLocationRelativeTo(null);
                myTransactions.setVisible(true);
                POS.setVisible(false);
            }
        });

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
        seeYourTransactions.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ArrayList<ArrayList<String>> transactions = groceryDatabaseConnector.getClerkTransactions(loginName);
                Object[] data = new Object[4];
                transactionModel.clear();
                for (ArrayList<String> listed: transactions) {
                    data[0] = listed.get(0);
                    data[1] = listed.get(1);
                    data[2] = listed.get(2);
                    data[3] = listed.get(3);
                    transactionModel.addRow(data);
                }
                myTransactions.repaint();
            }
        });
        seeAllTransactions.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ArrayList<ArrayList<String>> transactions = groceryDatabaseConnector.getAllClerkTransactions();
                Object[] data = new Object[4];
                transactionModel.clear();
                for (ArrayList<String> listed: transactions) {
                    data[0] = listed.get(0);
                    data[1] = listed.get(1);
                    data[2] = listed.get(2);
                    data[3] = listed.get(3);
                    transactionModel.addRow(data);
                }
                myTransactions.repaint();
            }
        });
    }


    private static void makeMyTransactions(){
        JTable transactionCartTable = new JTable(transactionModel);
        JScrollPane scrollableCartList = new JScrollPane(transactionCartTable);
        scrollableCartList.setBounds(16,16, 570, 512);
        myTransactions.add(scrollableCartList);

        int buttonWidth = 257;
        int uiWidth = 620;
        int xCenter = (uiWidth - buttonWidth) / 2;
        JButton returnToPOS = new JButton("Return to POS");
        returnToPOS.setBounds(xCenter,540,buttonWidth,50);
        myTransactions.add(returnToPOS);

        returnToPOS.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.out.println("Returning to POS");
                myTransactions.setVisible(false);
                POS.setLocationRelativeTo(null);
                POS.setVisible(true);
            }
        });

        myTransactions.setSize(uiWidth,640);
        myTransactions.setLayout(null);
        myTransactions.setVisible(false);

    }
}  