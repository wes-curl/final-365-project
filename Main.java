import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.awt.Component;
import java.awt.event.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.text.DecimalFormat;

public class Main {  
    private static JFrame login = new JFrame();
    private static JFrame POS = new JFrame();
    private static int POSwidth = 1280;
    private static int POSheight = 720;
    private static JFrame myTransactions = new JFrame();

    private static Double totalCost = 0d;

    private static JTable cartTable;

    private static final DecimalFormat df = new DecimalFormat("0.00");
    public static JLabel welcome = new JLabel("Welcome ", SwingConstants.CENTER);

    private static GroceryDatabaseConnector groceryDatabaseConnector = new GroceryDatabaseConnector();

    private static Clerk clerk = null;

    private static StockTableModel stockTableModel = new StockTableModel(null, groceryDatabaseConnector);
    private static GroceryCartTableModel cartTableModel  = new GroceryCartTableModel(stockTableModel);
    private static TransactionTableModel transactionModel = new TransactionTableModel(stockTableModel);
    private static List<GroceryItem> allItems;

    public static void main(String[] args) { 
        stockTableModel.GCTM = cartTableModel;
        allItems = groceryDatabaseConnector.getGroceryItems();
        makeLogin();
        login.setTitle("Login Point");
        makePOS();
        POS.setTitle("Point of Service 3000");
        makeMyTransactions();
        myTransactions.setTitle("Transactions Menu");
    }

    private static void makeLogin(){
        JLabel title = new JLabel("Point of Service Login");
        title.setHorizontalAlignment(SwingConstants.CENTER);
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
        submit.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){
                POS.setLocationRelativeTo(null);
                System.out.println(userName.getText());
                System.out.println(passwordField.getPassword());
                if(userName.getText().length() > 0 && passwordField.getPassword().length > 0){
                    clerk = groceryDatabaseConnector.isValidLogin(userName.getText(), String.valueOf(passwordField.getPassword()));
                    if(clerk != null){
                        login.setVisible(false);
                        POS.setVisible(true);
                        welcome.setText("Welcome " + clerk.getName());
                    }
                }
            }  
        });
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setSize(200,300);
        login.setLayout(null);//using no layout managers
        login.setLocationRelativeTo(null);
        login.setVisible(true);//making the frame visible  
    }

    private static void makePOS(){
        final int tww = 257;
        final int twh = 41;
        final int chartSizeWidth = POSwidth/8*5 - 300;
        final int chartSizeHeight = POSheight/4*3 - POSheight/15;
        final int insideSize = POSheight/5*3;

        POS.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        POS.setMaximumSize(new DimensionUIResource(1080, 720));
        POS.setSize(POSwidth, POSheight);
        POS.setMinimumSize(new DimensionUIResource(480, 320));


        cartTableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
               totalCost = cartTableModel.getTotal();
               total.setText("$"+df.format(totalCost));
               POS.repaint();
            }
          });

        cartTable = new JTable(cartTableModel);
        JScrollPane scrollableCartList = new JScrollPane(cartTable);
        scrollableCartList.setBounds(16,16, POSwidth/8*5 - 320, insideSize);
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

        Object[] data = new Object[4];
        for (GroceryItem I: allItems) {
            data[0] = I.name;
            data[1] = I.id;
            data[2] = I.cost;
            data[3] = I.stock;
            stockTableModel.addRow(data);
        }

        ColorUIResource red = new ColorUIResource(250, 100, 100);
        ColorUIResource itemCell = new ColorUIResource(183, 250, 238);
        ColorUIResource itemCellOpposite = new ColorUIResource(240, 166, 55);

        JTable itemTable =  new JTable(stockTableModel);
        final TableCellRenderer boldFont = new BoldCellRender();
        itemTable.setBackground(itemCell);
        itemTable.getColumnModel().getColumn(0).setCellRenderer(boldFont);
        JScrollPane scrollableItemTable = new JScrollPane(itemTable);
        scrollableItemTable.setBounds(POSwidth/8*5 - 150 + 106, 16, chartSizeWidth - 20, insideSize);
        POS.add(scrollableItemTable);

        JPanel listOfItemsAvailable = new JPanel();
        listOfItemsAvailable.setBackground(itemCellOpposite);
        listOfItemsAvailable.setBounds(POSwidth/8*5 - 50, 10, chartSizeWidth, chartSizeHeight);
        
        POS.add(listOfItemsAvailable);

        JPanel listOfItemsInCart = new JPanel();
        listOfItemsInCart.setBackground(red);
        listOfItemsInCart.setBounds(10,10, chartSizeWidth, chartSizeHeight);
        POS.add(listOfItemsInCart);

        JButton completeTransaction = new JButton("Complete Transaction");
        completeTransaction.setBounds(POSwidth/8*5 - 300, 10 + POSheight/4*3 - POSheight/15,257,50);
        POS.add(completeTransaction);

        completeTransaction.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){
                groceryDatabaseConnector.submitTransaction(cartTableModel.getDataVector(), clerk.getLogin());
                stockTableModel.submitTransaction(cartTableModel.getDataVector());
                cartTableModel.clear();
            }  
        });
        
        welcome.setBounds((POSwidth- tww - 16)/2, POSheight/15,tww,twh);
        POS.add(welcome);

        JButton seeYourTransactions = new JButton("See your transactions");
        seeYourTransactions.setBounds((POSwidth - tww + 30)/2, POSheight/15 + twh, tww-50, twh);
        POS.add(seeYourTransactions);

        seeYourTransactions.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                myTransactions.setLocationRelativeTo(null);
                myTransactions.setVisible(true);
                POS.setVisible(false);
            }
        });

        JButton seeAllTransactions = new JButton("See all transactions");
        seeAllTransactions.setBounds((POSwidth - tww + 30)/2, POSheight/15 + twh * 2, tww-50, twh);
        POS.add(seeAllTransactions);

        seeAllTransactions.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                myTransactions.setLocationRelativeTo(null);
                myTransactions.setVisible(true);
                POS.setVisible(false);
            }
        });

        JButton newItem = new JButton("Add a new item");
        newItem.setBounds(POSwidth/4*3 - POSwidth/18, POSheight/9*8 - POSheight/7,207,28);
        POS.add(newItem);

        JTextField newItemName = new JTextField("[item name]");
        newItemName.setBounds(POSwidth/4*3 - POSwidth/18, POSheight/9*8 - POSheight/19 - POSheight/7,207,28);
        POS.add(newItemName);

        newItem.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                System.out.print(newItemName.getText());
                GroceryItem GI = groceryDatabaseConnector.createNewItem(newItemName.getText());
                stockTableModel.addItem(GI);
            }  
        });

        // JPanel availableData = new JPanel();
        // availableData.setBackground(new ColorUIResource(100, 100, 100));
        // availableData.setBounds(333,10,257,184);
        // POS.add(availableData);

        POS.setSize(POSwidth,POSheight);
        POS.setLayout(null);
        POS.setVisible(false);        
        
        addItem.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                if(itemNumber.getText().length() > 0 && itemNumber.getText().length() < 10){
                    Integer idNumber = Integer.parseInt(itemNumber.getText());
                    System.out.println("adding: " + itemNumber.getText());
                    if(stockTableModel.getByID(idNumber) != null){
                        GroceryItem GCI = stockTableModel.getByID(idNumber);
                        Object[] row = {GCI.id, GCI.name, 1, GCI.cost};
                        itemNumber.setText("");
                        if(cartTableModel.getByID(idNumber) == null){
                            cartTableModel.addRow(row);
                        } else {
                            cartTableModel.updateRow(row);
                        }
                    }
                }
            }  
        });
        seeYourTransactions.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ArrayList<ArrayList<String>> transactions = groceryDatabaseConnector.getClerkTransactions(clerk.getLogin());
                Object[] data = new Object[4];
                transactionModel.clear();
                for (ArrayList<String> listed: transactions) {
                    data[0] = listed.get(0);
                    data[1] = Double.valueOf(Integer.parseInt(listed.get(1)))/100;
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
                    data[1] = Double.valueOf(Integer.parseInt(listed.get(1)))/100;
                    data[2] = listed.get(2);
                    data[3] = listed.get(3);
                    transactionModel.addRow(data);
                }
                myTransactions.repaint();
            }
        });
    }


    private static void makeMyTransactions(){
        myTransactions.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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