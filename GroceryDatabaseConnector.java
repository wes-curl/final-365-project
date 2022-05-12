import java.sql.*;

public class GroceryDatabaseConnector {
    Connection connection;
    private static String driver = "com.mysql.cj.jdbc.Driver";
    private static String driverTONYFILE = "com.mysql.jdbc.Driver";
    private static String url = "ambari-node5.csc.calpoly.edu:3306";
    private static String user = "licurldb";
    private static String password = "securedbpw";

    public GroceryDatabaseConnector(){
        try{
            Class.forName(driver);
            connection = DriverManager.getConnection("jdbc:mysql://ambari-node5.csc.calpoly.edu:3306/" + user + "?" + "user=" + user + "&password=" + password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getGroceryItems(){
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("");
            while(resultSet.next()){
                String row = resultSet.getString(1);
                System.out.println(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTransactions(){
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from transaction");
            while(resultSet.next()){
                String row = resultSet.getString(1);
                System.out.println(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getItemsFromSpecificTransaction(int transaction){
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from purchasedgoods where purchasedgoodsTRID = " + String.valueOf(transaction));
            System.out.println(String.format("%5s | %5s | %8s | %5s", "TRID", "PRID", "COST", "QTY"));
            while(resultSet.next()){
                String trid = resultSet.getString(1);
                String prid = resultSet.getString(2);
                String cost = resultSet.getString(3);
                String qty = resultSet.getString(4);
                String row = String.format("%5s , %5s , %8s , %5s", trid, prid, cost, qty);
                System.out.println(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getClerkTransactions(String clerkLogin){
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from transaction where transactionCLERK = '" + clerkLogin + "'");
            System.out.println(String.format("%8s | %8s | %8s | %8s", "TRID", "TRTOT", "TRCLERK", "TRDATE"));
            while(resultSet.next()){
                String trid = resultSet.getString(1);
                String trtot = resultSet.getString(2);
                String trclerk = resultSet.getString(3);
                String trdate = resultSet.getString(4);
                String row = String.format("%8s , %8s , %8s , %8s", trid, trtot, trclerk, trdate);
                System.out.println(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
