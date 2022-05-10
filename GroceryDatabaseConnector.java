import java.sql.*;

public class GroceryDatabaseConnector {
    Connection connection;
    private static String driver = "com.mysql.cj.jdbc.Driver";
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
}
