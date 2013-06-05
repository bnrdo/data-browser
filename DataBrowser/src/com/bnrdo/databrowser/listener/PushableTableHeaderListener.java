package com.bnrdo.databrowser.listener;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;

import com.bnrdo.databrowser.AppStat;
import com.bnrdo.databrowser.Constants;
import com.bnrdo.databrowser.PushableTableHeaderRenderer;


public class PushableTableHeaderListener extends MouseAdapter{
	private JTableHeader header;
	private PushableTableHeaderRenderer renderer;

	public PushableTableHeaderListener(JTableHeader header, PushableTableHeaderRenderer renderer) {
		this.header = header;
		this.renderer = renderer;
	} 

	public void mousePressed(MouseEvent e) {
		Cursor cursor = header.getCursor();
		
		if(cursor.getType() == Cursor.E_RESIZE_CURSOR)
			return;
		
		AppStat.IS_SORTABLE = true;
		
		int col = header.columnAtPoint(e.getPoint());
		renderer.setPressedColumn(col);
		header.repaint();
	}

	public void mouseReleased(MouseEvent e) {
		int col = header.columnAtPoint(e.getPoint());
		renderer.setPressedColumn(-1); // clear
		header.repaint();
	}
	
	public void mouseDragged(MouseEvent arg0) {
		AppStat.IS_SORTABLE = false;
		
		renderer.setPressedColumn(-1);
		header.repaint();
	}
}
