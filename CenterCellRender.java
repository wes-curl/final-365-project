import javax.swing.JLabel;
import javax.swing.JTable;
import java.awt.Font;
import java.awt.Component;
import javax.swing.table.DefaultTableCellRenderer;

public class CenterCellRender extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        this.setValue(table.getValueAt(row, column));
        this.setHorizontalAlignment( JLabel.CENTER );
        return this;
    }
    }
    