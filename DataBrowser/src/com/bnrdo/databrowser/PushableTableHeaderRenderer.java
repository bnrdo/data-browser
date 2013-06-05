package com.bnrdo.databrowser;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class PushableTableHeaderRenderer extends JButton implements TableCellRenderer {
	int pushedColumn;

	public PushableTableHeaderRenderer() {
		pushedColumn = -1;
		setMargin(new Insets(0, 0, 0, 0));
	}

	public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		setText((value == null) ? "" : value.toString());
		boolean isPressed = (column == pushedColumn);
		getModel().setPressed(isPressed);
		getModel().setArmed(isPressed);
		return this;
	}

	public void setPressedColumn(int col) {
		pushedColumn = col;
	}
}
