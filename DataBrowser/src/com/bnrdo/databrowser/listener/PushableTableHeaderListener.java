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
	private boolean isSortable;

	public PushableTableHeaderListener(JTableHeader header, PushableTableHeaderRenderer renderer) {
		this.header = header;
		this.renderer = renderer;
		isSortable = true;
	} 

	public void mousePressed(MouseEvent e) {
		Cursor cursor = header.getCursor();
		
		if(cursor.getType() == Cursor.E_RESIZE_CURSOR)
			return;
		
		isSortable = true;
		
		int col = header.columnAtPoint(e.getPoint());
		renderer.setPressedColumn(col);
		header.repaint();
	}

	public void mouseReleased(MouseEvent e) {
		renderer.setPressedColumn(-1); // clear
		header.repaint();
	}
	
	public void mouseDragged(MouseEvent arg0) {
		isSortable = false;
		
		renderer.setPressedColumn(-1);
		header.repaint();
	}
	
	public boolean isSortable(){
		return isSortable;
	}
}
