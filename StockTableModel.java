import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Vector;

public class StockTableModel  extends DefaultTableModel{
    public static final String[] columnNames = {"Item name", "Item ID", "Item cost", "# in Stock"};
    public GroceryCartTableModel GCTM;
    public GroceryDatabaseConnector GDC;

    public StockTableModel(GroceryCartTableModel GCTM, GroceryDatabaseConnector GDC) {
        super(columnNames, 0);
        this.GCTM = GCTM;
        this.GDC = GDC;
    }

    public String getColumnName(int col) {
        return columnNames[col].toString();
    }

    @Override 
    public boolean isCellEditable(int row, int column)
    {
        return column > 1;
    }

    @Override
    public void fireTableChanged(TableModelEvent e) {
        // ensure data integrity
        if(e.getFirstRow() >= 0 && e.getColumn() == 3){
            Object quantity = super.getValueAt(e.getFirstRow(), e.getColumn());
            if(Objects.isNull(quantity) || (Integer)quantity < 0){
                super.setValueAt(0, e.getFirstRow(), e.getColumn());
            }
            // update cart quantity
            GCTM.updateQuantity((Integer)super.getValueAt(e.getFirstRow(), 1), (Integer)quantity);
            GDC.updateQuantity((Integer)super.getValueAt(e.getFirstRow(), 1), (Integer)quantity);
        }
        if(e.getColumn() == 2){
            Object cost = super.getValueAt(e.getFirstRow(), e.getColumn());
            if(Objects.isNull(cost) || (Double)cost < 0){
                super.setValueAt(0, e.getFirstRow(), e.getColumn());
            }
            // update cart cost
            GCTM.updateCost((Integer)super.getValueAt(e.getFirstRow(), 1), (Double)cost);
            GDC.updateCost((Integer)super.getValueAt(e.getFirstRow(), 1), (Double)cost);
        }
        

        super.fireTableChanged(e);
    }

    public void updateQuantity(Integer id, Integer quantity){
        for(int i = 0; i < super.getRowCount(); i++){
            if(super.getValueAt(i, 1) == id){
                if(quantity < (Integer)super.getValueAt(i, 3)){
                    super.setValueAt(quantity, i, 3);
                }
            }
        }
    }

    public void submitTransaction(Vector<Vector> allRows){
        try{
            for(Vector v : allRows)
            {
                int oldQTY = -1;
                Vector<Vector> vectors = this.dataVector;
                int prid = (int)v.get(0);
                for(Vector v2 : vectors)
                {
                    if ((int)v.get(0) == prid)
                    {
                        oldQTY = (int)v.get(2);
                    }
                }
                int qty  = (int)(v.get(2));
                if(oldQTY >= qty)
                {
                    this.updateQuantity(prid, oldQTY - qty);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GroceryItem getByID(Integer ID){
        Enumeration<Vector> elements = super.dataVector.elements();
        while(elements.hasMoreElements()){
            Vector v = elements.nextElement();
            if(v.get(1) == ID){
                return new GroceryItem((String)v.get(0), ID, (Double)v.get(2), (Integer)v.get(3));
            }
        }
        return null;
    }

    //add a row given an item
    public void addItem(GroceryItem GI){
        this.addRow(new Object[]{GI.name,GI.id,GI.cost,GI.stock});
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex){
            case 0:
                return String.class;
            case 1:
                return Integer.class;
            case 2:
                return Double.class;
            case 3:
                return Integer.class;
        }
        return Double.class;
    }
}
