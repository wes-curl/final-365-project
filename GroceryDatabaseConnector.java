import java.sql.*;
import java.util.ArrayList;

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
        ArrayList<ArrayList<String>> infoList = new ArrayList<>();
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

    public ArrayList<ArrayList<String>> getClerkTransactions(String clerkLogin){
        ArrayList<ArrayList<String>> infoList = new ArrayList<>();
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
                ArrayList<String> infoRow = new ArrayList<>();
                infoRow.add(trid);
                infoRow.add(trtot);
                infoRow.add(trclerk);
                infoRow.add(trdate);
                infoList.add(infoRow);
                System.out.println(row);
            }
            String row = String.format("%8s , %8s , %8s , %8s", "null", "null", "null", "null");
            System.out.println(row);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infoList;
    }

    public ArrayList<ArrayList<String>> getAllClerkTransactions(){
        ArrayList<ArrayList<String>> infoList = new ArrayList<>();
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from transaction");
            System.out.println(String.format("%8s | %8s | %8s | %8s", "TRID", "TRTOT", "TRCLERK", "TRDATE"));
            while(resultSet.next()){
                String trid = resultSet.getString(1);
                String trtot = resultSet.getString(2);
                String trclerk = resultSet.getString(3);
                String trdate = resultSet.getString(4);
                String row = String.format("%8s , %8s , %8s , %8s", trid, trtot, trclerk, trdate);
                ArrayList<String> infoRow = new ArrayList<>();
                infoRow.add(trid);
                infoRow.add(trtot);
                infoRow.add(trclerk);
                infoRow.add(trdate);
                infoList.add(infoRow);
                System.out.println(row);
            }
            String row = String.format("%8s , %8s , %8s , %8s", "null", "null", "null", "null");
            System.out.println(row);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infoList;
    }

    public Clerk isValidLogin(String username, String password){
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from clerk where clerkLOGIN = '"+username+"' and clerkPW = '"+password+"'");
            System.out.println("select * from clerk where clerkLOGIN = '"+username+"' and clerkPW = '"+password+"'");
            if(resultSet.next()){
                String login = resultSet.getString(1);
                String name = resultSet.getString(2);
                String pw = resultSet.getString(3);
                return new Clerk(login, name, pw);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
