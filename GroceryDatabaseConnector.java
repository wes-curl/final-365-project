import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

public class GroceryDatabaseConnector {
    Connection connection;
    private static String driver = "com.mysql.cj.jdbc.Driver";
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

    public LinkedList<GroceryItem> getGroceryItems(){
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM produce");
            LinkedList<GroceryItem> items = new  LinkedList<GroceryItem>();
            while(resultSet.next()){
                Integer id = Integer.parseInt(resultSet.getString(1));
                Double cost = Double.parseDouble(resultSet.getString(2)) / 100;
                String name = resultSet.getString(3);
                Integer stock = Integer.parseInt(resultSet.getString(4));
                items.add(new GroceryItem(name, id, cost, stock));
            }
            return items;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    public void submitTransaction(Vector<Vector> allRows, String clerkLogin){
        try{
            Statement statement = connection.createStatement();
            statement.execute("insert into transaction (transactionCLERK) values ('" + clerkLogin + "')");
            ResultSet resultSet = statement.executeQuery("select transactionID from transaction where transactionID >= all (select transactionID from transaction)");
            
            resultSet.next();
            int transactionID = resultSet.getInt(1);

            for(Vector v : allRows)
            {
                String buildastring = "insert into purchasedgoods (purchasedgoodsTRID, purchasedgoodsPRID, purchasedgoodsCOST, purchasedGoodsAMNT) values";
                // ID, name, quantity, cost
                int trid = transactionID;
                int prid = (int)v.get(0);
                int cost = (int)(100*((double)(v.get(3))));
                int qty  = (int)(v.get(2));
                buildastring = buildastring.concat(" (" + String.valueOf(trid) + ", " + String.valueOf(prid) + ", " + String.valueOf(cost) + ", " + String.valueOf(qty) + ")");
                statement.execute(buildastring);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public GroceryItem createNewItem(String name){
        try{
            Statement statement = connection.createStatement();
            // create a new produce item in the DB
            statement.execute("INSERT INTO produce (produceCOST, produceName, stock) VALUES (0, '"+name+"',0);");
            // get its ID
            ResultSet resultSet = statement.executeQuery("SELECT MAX(produceID) FROM produce");
            if(resultSet.next()){
                Integer id = Integer.parseInt(resultSet.getString(1));
                return new GroceryItem(name, id, 0.0, 0);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateCost(Integer id, Double cost){
        try{
            Statement statement = connection.createStatement();
            // edit the cost of an item in the stock table
            String costString = ((Integer)Math.round((float)(cost * 100))).toString();
            statement.execute("UPDATE produce SET produceCOST = "+costString+" WHERE produceID = "+id.toString()+";");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateQuantity(Integer id, Integer quantity){
        try{
            Statement statement = connection.createStatement();
            // edit the quanitity of an item in the stock table
            statement.execute("UPDATE produce SET stock = "+quantity.toString()+" WHERE produceID = "+id.toString()+";");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
