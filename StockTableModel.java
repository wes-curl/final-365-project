import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;

import java.util.Enumeration;
import java.util.Objects;
import java.util.Vector;

public class StockTableModel  extends DefaultTableModel{
    public static final String[] columnNames = {"Item name", "Item ID", "Item cost", "# in Stock"};


    public StockTableModel() {
        super(columnNames, 0);
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
        if(e.getFirstRow() >= 0 && e.getColumn() == 3){
            Object quantity = super.getValueAt(e.getFirstRow(), e.getColumn());
            if(Objects.isNull(quantity) || (Integer)quantity < 0){
                super.setValueAt(0, e.getFirstRow(), e.getColumn());
            }
        }
        super.fireTableChanged(e);
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
