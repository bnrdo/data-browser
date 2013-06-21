package com.bnrdo.databrowser.mvc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;

import com.bnrdo.databrowser.listener.Disablelable;

public class DataBrowserView {
    private JPanel pnlMain;
    private JPanel pnlPage;
    private JPanel pnlSearch;
    private JPanel pnlTable;
    
    private JTextField txtSearch;
    private JComboBox cboSearch;
    private JTable tblData;
    private JButton btnSearch;
    private JButton btnSettings;

    private PageButton [] pageBtns;
    private PageButton btnFirst;
    private PageButton btnPrev;
    private PageButton btnNext;
    private PageButton btnLast;
    
    private JLabel lblSort;
    private JLabel lblFilter;
    private JLabel lblRowCount;
    private JLabel lblStatus;

    public DataBrowserView() {
        initUI();
    }

    private void initUI() {
        pnlMain = new JPanel(new BorderLayout());
        pnlPage = createPagePanel();
        pnlSearch = createSearchPanel();
        pnlTable = createTablePanel();

        JPanel pnlSearchAndPageHolder = new JPanel();
        pnlSearchAndPageHolder.setLayout(new BoxLayout(pnlSearchAndPageHolder,
                BoxLayout.LINE_AXIS));

        pnlSearchAndPageHolder.add(pnlSearch);
        pnlSearchAndPageHolder.add(pnlPage);

        pnlMain.add(pnlTable, BorderLayout.CENTER);
        pnlMain.add(pnlSearchAndPageHolder, BorderLayout.PAGE_START);
        pnlMain.add(createStatPanel(), BorderLayout.PAGE_END);
    }

    private JPanel createStatPanel(){
    	JPanel retVal = new JPanel();
    	retVal.setLayout(new BoxLayout(retVal, BoxLayout.LINE_AXIS));
    	retVal.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    	
    	Font statFont = new Font("Arial", Font.PLAIN, 11);
    	
    	lblStatus = new JLabel();
    	lblRowCount = new JLabel();
    	lblSort = new JLabel();
    	lblFilter = new JLabel();

    	lblStatus.setFont(statFont);
    	lblRowCount.setFont(statFont);
    	lblSort.setFont(statFont);
    	lblFilter.setFont(statFont);
    	
    	lblStatus.setVisible(false);
    	lblRowCount.setVisible(false);
    	lblSort.setVisible(false);
    	lblFilter.setVisible(false);
    	
    	//lblRowCount.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
    	lblSort.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));
    	lblFilter.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 7));
    	
    	retVal.add(lblSort);
    	retVal.add(lblFilter);
    	retVal.add(lblRowCount);
    	
    	retVal.add(Box.createHorizontalGlue());
    	retVal.add(lblStatus);
    	
    	lblStatus.setVisible(false);
    	
    	return retVal;
    }
    
    @SuppressWarnings("serial")
    private JPanel createTablePanel() {
        JPanel retVal = new JPanel(new BorderLayout()) {
            @Override public Dimension getPreferredSize() {
                return new Dimension(700, 300);
            }
        };

        tblData = new JTable();
        //tblData.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblData.setRowSelectionAllowed(true);
        
        JScrollPane pane = new JScrollPane(tblData);
        retVal.add(pane, BorderLayout.CENTER);
        return retVal;
    }
    
    @SuppressWarnings("serial")
	private JPanel createPagePanel(){
    	return new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(350, 20);
            }
        };
    }
    
    public void createPageButtons(int[] pageNums) {
    	if (pnlPage.getComponentCount() > 0) {
    		pnlPage.remove(0);
    	}
    	
    	JPanel pageHandle = new JPanel();
    	
    	pageHandle.setLayout(new BoxLayout(pageHandle, BoxLayout.LINE_AXIS));
    	pageHandle.add(Box.createHorizontalGlue());
    	
    	if (pageNums.length > 1) {
            btnFirst = new PageButton("First");
            btnPrev = new PageButton("Prev");
            btnFirst.setFocusable(false);
            btnPrev.setFocusable(false);
            btnFirst.setVisible(false);
            btnPrev.setVisible(false);
            pageHandle.add(btnFirst);
            pageHandle.add(btnPrev);
        }

        pageBtns = new PageButton [pageNums.length];
        int indxCtr = 1;
        for (int i : pageNums) {
            PageButton btn = new PageButton(i);
            btn.setFocusable(false);
            pageBtns[indxCtr - 1] = btn;
            indxCtr++;
            pageHandle.add(btn);
        }

        if (pageNums.length > 1) {
            btnNext = new PageButton("Next");
            btnLast = new PageButton("Last");
            btnNext.setFocusable(false);
            btnLast.setFocusable(false);
            pageHandle.add(btnNext);
            pageHandle.add(btnLast);
        }
    	
    	pnlPage.add(pageHandle);
    	pnlPage.repaint();
    }

    @SuppressWarnings("serial")
	private JPanel createSearchPanel() {
        JPanel retVal = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(350, 30);
            }
        };

        retVal.setLayout(new BoxLayout(retVal, BoxLayout.LINE_AXIS));

        JLabel lblSearch = new JLabel("Search Products");

        txtSearch = new JTextField() {
            @Override public Dimension getMaximumSize() {
                return new Dimension(100, 20);
            }
        };
        cboSearch = new JComboBox() {
            @Override public Dimension getMaximumSize() {
                return new Dimension(getPreferredSize().width, 20);
            }
        };
        btnSearch = new JButton(){
        	@Override public Dimension getMaximumSize() {
                return new Dimension(getPreferredSize().width, 20);
            }
        };
        btnSettings = new JButton("Settings"){
        	@Override public Dimension getMaximumSize() {
                return new Dimension(getPreferredSize().width, 20);
            }
        };

        retVal.add(Box.createRigidArea(new Dimension(10, 10)));
        retVal.add(lblSearch);
        retVal.add(Box.createRigidArea(new Dimension(5, 10)));
        retVal.add(txtSearch);
        retVal.add(Box.createRigidArea(new Dimension(5, 10)));
        retVal.add(cboSearch);
        retVal.add(Box.createRigidArea(new Dimension(5, 10)));
        retVal.add(btnSearch);
        //retVal.add(Box.createRigidArea(new Dimension(5, 10)));
        //retVal.add(btnSettings);
        
        return retVal;
    }
    
    public JTextField getTxtSearch() {
        return txtSearch;
    }

    public JPanel getUI() {
        return pnlMain;
    }

    public JTable getDataTable() {
        return tblData;
    }

    public JComboBox getCboSearch() {
        return cboSearch;
    }
    
    public JButton getBtnSearch(){
    	return btnSearch;
    }

    public PageButton [] getPageBtns() {
        return pageBtns;
    }

    public PageButton getBtnFirst() {
        return btnFirst;
    }

    public PageButton getBtnPrev() {
        return btnPrev;
    }

    public PageButton getBtnNext() {
        return btnNext;
    }

    public PageButton getBtnLast() {
        return btnLast;
    }

	public JLabel getLblSort() {
		return lblSort;
	}
	
	public JLabel getLblFilter() {
		return lblFilter;
	}
	
	public JLabel getLblRowCount(){
		return lblRowCount;
	}
	
	public void showStatus(String status){
		if(!lblStatus.getText().trim().equals("")) return;
		
		lblStatus.setText(status);
		lblStatus.setVisible(true);
	}
	
	public void hideStatus(){
		lblStatus.setText("");
		lblStatus.setVisible(false);
	}
	
	public void disableSearch(){
		btnSearch.setEnabled(false);
		cboSearch.setEnabled(false);
		txtSearch.setEnabled(false);
	}
	
	public void enableSearch(){
		btnSearch.setEnabled(true);
		cboSearch.setEnabled(true);
		txtSearch.setEnabled(true);
	}

	public void showTableLoader(){
    	txtSearch.setEnabled(false);
    	btnSearch.setEnabled(false);
    	cboSearch.setEnabled(false);
    	
    	JTableHeader header = tblData.getTableHeader();
    	header.setReorderingAllowed(false);
    	header.setResizingAllowed(false);
    	
    	//disable the disable-lable listeners
		for(MouseListener ms : header.getMouseListeners()){
			if(ms instanceof Disablelable){
				((Disablelable) ms).setEnabled(false);
			}
		}
    	
    	tblData.clearSelection();
    	tblData.setEnabled(false);
    	tblData.setForeground(Color.LIGHT_GRAY);
    	
    	for(PageButton btns : pageBtns){
    		btns.setEnabled(false);
    	}
    	
    	if(btnFirst != null) btnFirst.setEnabled(false);
    	if(btnPrev != null) btnPrev.setEnabled(false);
    	if(btnNext != null) btnNext.setEnabled(false);
    	if(btnLast != null) btnLast.setEnabled(false);
    }

    public void hideTableLoader(){
    	txtSearch.setEnabled(true);
    	btnSearch.setEnabled(true);
    	cboSearch.setEnabled(true);
    	
    	JTableHeader header = tblData.getTableHeader();
    	header.setReorderingAllowed(true);
    	header.setResizingAllowed(true);
    	
    	//enable the disable-lable listeners
		for(MouseListener ms : header.getMouseListeners()){
			if(ms instanceof Disablelable){
				((Disablelable) ms).setEnabled(true);
			}
		}
    			
    	tblData.setEnabled(true);
    	tblData.setForeground(Color.BLACK);
    	
    	for(PageButton btns : pageBtns){
    		btns.setEnabled(true);
    	}
    	
    	if(btnFirst != null) btnFirst.setEnabled(true);
    	if(btnPrev != null) btnPrev.setEnabled(true);
    	if(btnNext != null) btnNext.setEnabled(true);
    	if(btnLast != null) btnLast.setEnabled(true);
    }
    
    @SuppressWarnings("serial")
	public class PageButton extends JButton {
        private boolean selected;

        public PageButton() {
            this("");
        }

        public PageButton(int i) {
            this(Integer.toString(i));
        }

        public PageButton(String text) {
            super(text);
            buildUI();
        }

        private void buildUI() {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
            setFont(new Font("Calibri", 0, 12));
        }

        @Override
        public void setSelected(boolean isSel) {
            super.setSelected(isSel);

            if (isSel) {
                setForeground(new Color(99, 0, 156));
                setFont(new Font("Calibri", 1, 12));
            } else {
                setForeground(Color.BLACK);
                setFont(new Font("Calibri", 0, 12));
            }

            selected = true;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }
    }
}