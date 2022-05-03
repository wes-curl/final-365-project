import javax.swing.table.DefaultTableModel;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import javax.swing.event.TableModelEvent;
import java.util.Objects;

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
