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
            }
            public void focusGained(FocusEvent e) {
                if(userName.getText().trim().equals("username"))
                    userName.setText("");
            }
        });
        submit.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){
                POS.setLocationRelativeTo(null);
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
        final int segmentHeight = POSheight/8;
        final int segmentWidth = POSwidth/5;
        final int chartSizeWidth = POSwidth/8*5 - 300;
        final int chartSizeHeight = POSheight/4*3 - POSheight/15;
        final int gutterSize = segmentHeight / 2;

        POS.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton addItem = new JButton("Add:");
        addItem.setBounds(0,6*segmentHeight + gutterSize,2*segmentWidth/3,gutterSize);
        POS.add(addItem);

        JLabel total = new JLabel("Total: 0.00");
        total.setBounds(0,7*segmentHeight,2*segmentWidth,gutterSize);
        POS.add(total);

        JTextField itemNumber = new JTextField();
        itemNumber.setBounds(2*segmentWidth/3,6*segmentHeight + gutterSize,2*segmentWidth/3,gutterSize);
        POS.add(itemNumber);

        JButton deleteItem = new JButton("Delete");
        deleteItem.setBounds(4*segmentWidth/3+segmentWidth,6*segmentHeight + gutterSize,2*segmentWidth/3,gutterSize);
        POS.add(deleteItem);

        itemNumber.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if ( ((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE)) {
                     e.consume(); 
                }
            }
        });
        POS.setMaximumSize(new DimensionUIResource(POSwidth+256, POSheight+256));
        POS.setSize(POSwidth+13,POSheight+36);
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
        scrollableCartList.setBounds(0,0, 2*segmentWidth, 6*segmentHeight);
        POS.add(scrollableCartList);

        deleteItem.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
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
        //ColorUIResource itemCellOpposite = new ColorUIResource(240, 166, 55);

        JTable itemTable =  new JTable(stockTableModel);
        final TableCellRenderer boldFont = new BoldCellRender();
        itemTable.setBackground(itemCell);
        itemTable.getColumnModel().getColumn(0).setCellRenderer(boldFont);
        JScrollPane scrollableItemTable = new JScrollPane(itemTable);
        scrollableItemTable.setBounds(segmentWidth*3, 0, segmentWidth*2, segmentHeight*6);
        POS.add(scrollableItemTable);

        //JPanel listOfItemsAvailable = new JPanel();
        //listOfItemsAvailable.setBackground(itemCellOpposite);
        //listOfItemsAvailable.setBounds(POSwidth/8*5 - 50, 10, chartSizeWidth, chartSizeHeight);
        
        //POS.add(listOfItemsAvailable);

        JPanel listOfItemsInCart = new JPanel();
        listOfItemsInCart.setBackground(red);
        listOfItemsInCart.setBounds(10,10, chartSizeWidth, chartSizeHeight);
        POS.add(listOfItemsInCart);

        JButton completeTransaction = new JButton("complete Transaction");
        completeTransaction.setBounds(segmentWidth*2, segmentHeight*6 + gutterSize,segmentWidth,segmentHeight);
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
        seeYourTransactions.setBounds(segmentWidth*2, segmentHeight + gutterSize, segmentWidth, gutterSize);
        POS.add(seeYourTransactions);

        seeYourTransactions.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                myTransactions.setLocationRelativeTo(null);
                myTransactions.setVisible(true);
                POS.setVisible(false);
            }
        });

        JButton seeAllTransactions = new JButton("See all transactions");
        seeAllTransactions.setBounds(segmentWidth*2, segmentHeight*2 + gutterSize, segmentWidth, gutterSize);
        POS.add(seeAllTransactions);

        seeAllTransactions.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                myTransactions.setLocationRelativeTo(null);
                myTransactions.setVisible(true);
                POS.setVisible(false);
            }
        });

        JButton newItem = new JButton("Add a new item");
        newItem.setBounds(segmentWidth*3 + segmentWidth/2, segmentHeight*7,segmentWidth,gutterSize);
        POS.add(newItem);

        JTextField newItemName = new JTextField("[item name]");
        newItemName.setBounds(segmentWidth*3, segmentHeight*6 + gutterSize,segmentWidth*2,gutterSize);
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
        POS.setLayout(null);
        POS.setVisible(false);        
        
        addItem.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                if(itemNumber.getText().length() > 0 && itemNumber.getText().length() < 10){
                    Integer idNumber = Integer.parseInt(itemNumber.getText());
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