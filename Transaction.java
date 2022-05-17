import java.util.Date;
public class Transaction {
    Date date;
    Integer id;
    GroceryItem[] itemsPurchased;
    Double totalExpenditure;

    public Transaction(Date date, Integer id, GroceryItem[] itemsPurchased, Double totalExpenditure){
        this.date = date;
        this.id = id;
        this.itemsPurchased = itemsPurchased;
        this.totalExpenditure = totalExpenditure;
    }
}
