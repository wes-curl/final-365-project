import javax.swing.table.DefaultTableModel;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import javax.swing.event.TableModelEvent;
import java.util.Objects;

public class GroceryCartTableModel extends DefaultTableModel{
    public static final String[] columnNames = {"ID", "Name", "Quantity", "Unit Cost"};

    public GroceryCartTableModel() {
        super(columnNames, 0);
    }

    @Override
    public void fireTableChanged(TableModelEvent e) {
        if(e.getFirstRow() >= 0 && e.getColumn() >= 0){
            System.out.println("Now has: " + super.getValueAt(e.getFirstRow(), e.getColumn()));
            Object quantity = super.getValueAt(e.getFirstRow(), e.getColumn());
            if(Objects.isNull(quantity) || (Integer)quantity <= 0){
                super.removeRow(e.getFirstRow());
            }
        }
        super.fireTableChanged(e);
    }

    public Double getTotal(){
        Double sum = 0d;
        for(int i = 0; i < super.getRowCount(); i++){
            Double value = (Double)super.getValueAt(i, 3) * (Integer)super.getValueAt(i, 2);
            sum += value;
        }
        return sum;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (getRowCount() > 0 && getValueAt(0, columnIndex) != null) {
            return getValueAt(0, columnIndex).getClass();
        }
        return super.getColumnClass(columnIndex);
    }

    public void addRow(Object[] rowData) {
        Vector<Object> rowVector = new Vector<>();
        rowVector.add(rowData[0]);
        rowVector.add(rowData[1]);
        rowVector.add(rowData[2]);
        rowVector.add(rowData[3]);

        super.addRow(rowVector);
    }

    // Deletes the currently selected row
    public void deleteRow(Integer row){
        super.removeRow(row);
    }

    public void clear(){
        super.setRowCount(0);
    }

    @Override 
    public boolean isCellEditable(int row, int column)
    {
        return column == 2;
    }
}