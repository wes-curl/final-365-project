import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.util.LinkedList;
import java.util.List;
import java.awt.event.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.text.DecimalFormat;

public class Main {  
    private static JFrame login = new JFrame();
    private static JFrame POS = new JFrame();
    private static JFrame personalTransactionList = new JFrame();
    private static JFrame globalTransactionList = new JFrame();
    private static JFrame itemManager = new JFrame();

    private static List<GroceryCartItem> cartItems;
    private static Double totalCost = 0d;

    private static JTable cartTable;

    private static final DecimalFormat df = new DecimalFormat("0.00");
    

    public static void main(String[] args) {  
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

    private static void makeTransactionList(Transaction transaction){

    }

    private static void makePOS(){
        JButton addItem = new JButton("Add:");
        addItem.setBounds(10,540,60,30);
        POS.add(addItem);
        JButton deleteItem = new JButton("Delete");
        deleteItem.setBounds(210,540,90,30);
        POS.add(deleteItem);
        

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

        cartItems = new LinkedList<GroceryCartItem>();
        GroceryCartTableModel cartTableModel = new GroceryCartTableModel();
        cartTableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
               totalCost = cartTableModel.getTotal();
               System.out.print(totalCost.toString() + " is the new total");
               total.setText("$"+df.format(totalCost));
               POS.repaint();
            }
          });

        cartTable = new JTable(cartTableModel);
        JScrollPane scrollableCartList = new JScrollPane(cartTable);
        scrollableCartList.setBounds(16,16, 278, 512);
        POS.add(scrollableCartList);

        List<GroceryItem> allItems = List.of(new GroceryItem("carrots", 12, 12.31, 15), new GroceryItem("potatoes", 123, 0.32, 6));
        String[] columnNames = {"Item name", "Item ID", "Item cost", "# in Stock"};

        Object[][] data = new String[allItems.size()][4];
        int i = 0;
        for (GroceryItem I: allItems) {
            data[i][0] = I.name;
            data[i][1] = I.id.toString();
            data[i][2] = I.cost.toString();
            data[i][3] = I.stock.toString();
            i++;
        }

        JTable itemTable =  new JTable(displayTable(data, columnNames));
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
                System.out.println("cartItems...");
                System.out.println(totalCost);
                cartItems = new LinkedList<GroceryCartItem>();
                cartTableModel.clear();
            }  
        });

        JLabel welcome = new JLabel("Welcome ---------");
        welcome.setBounds(333,10,257,41);
        POS.add(welcome);

        JButton seeAllTransactions = new JButton("See all transactions");
        seeAllTransactions.setBounds(358,99,207,32);
        POS.add(seeAllTransactions);

        JButton seeYourTransactions = new JButton("See your transactions");
        seeYourTransactions.setBounds(358,51,207,32);
        POS.add(seeYourTransactions);

        JButton logOut = new JButton("Manage Inventory");
        logOut.setBounds(358,146,207,32);
        POS.add(logOut);

        JPanel availableData = new JPanel();
        availableData.setBackground(new ColorUIResource(100, 100, 100));
        availableData.setBounds(333,10,257,184);
        POS.add(availableData);

        POS.setSize(620,640);
        POS.setLayout(null);
        POS.setVisible(false);        

        addItem.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                System.out.println(itemNumber.getText());
                if(itemNumber.getText().length() > 0 && itemNumber.getText().length() < 10){
                    cartItems.add(new GroceryCartItem(Integer.valueOf(itemNumber.getText()), "Nothing, yet"));
                    cartItems.add(new GroceryCartItem(-1, "test"));
                    itemNumber.setText("");
                    Object[] row = {-1,"test", 1, 1.23};
                    cartTableModel.addRow(row);
                }
            }  
        });
    }

    private static AbstractTableModel displayTable(Object[][] rowData, String[] columnNames){
        return new AbstractTableModel() {
            public String getColumnName(int col) {
                return columnNames[col].toString();
            }
            public int getRowCount() { return rowData.length; }
            public int getColumnCount() { return columnNames.length; }
            public Object getValueAt(int row, int col) {
                return rowData[row][col];
            }
            public boolean isCellEditable(int row, int col)
                { return false; }
            public void setValueAt(Object value, int row, int col) {
                rowData[row][col] = value;
                fireTableCellUpdated(row, col);
            }
        };
    }
}  