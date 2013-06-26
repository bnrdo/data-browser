package com.bnrdo.databrowser.mvc;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;

import com.bnrdo.databrowser.LineNumberTableRowHeader;
import com.bnrdo.databrowser.listener.Disablelable;
import com.bnrdo.pagination.Pagination;

public class DataBrowserView {
    private JLayeredPane pnlMain;
    private JPanel pnlPage;
    private JPanel pnlSearch;
    private JPanel pnlTable;
    private JPanel pnlSettings;
    
    private JTextField txtSearch;
    private JComboBox cboSearch;
    private JTable tblData;
    private JButton btnSearch;
    private JButton btnSettings;
    
    private Pagination pagination;
    
    private JLabel lblSort;
    private JLabel lblFilter;
    private JLabel lblRowCount;
    private JLabel lblStatus;

    public DataBrowserView() {
        initUI();
    }

    private void initUI() {
        pnlMain = new JLayeredPane();
        pnlPage = createPagePanel();
        pnlSearch = createSearchPanel();
        pnlTable = createTablePanel();
        pnlSettings = createSettingsPanel();
        
        JPanel pnlSearchAndPageHolder = new JPanel();
        JPanel pnlMainHolder = new JPanel(new BorderLayout());
        pnlSearchAndPageHolder.setLayout(new BoxLayout(pnlSearchAndPageHolder,
                BoxLayout.LINE_AXIS));

        pnlSearchAndPageHolder.add(pnlSearch);
        pnlSearchAndPageHolder.add(pnlPage);

        pnlMainHolder.add(pnlTable, BorderLayout.CENTER);
        pnlMainHolder.add(pnlSearchAndPageHolder, BorderLayout.PAGE_START);
        pnlMainHolder.add(createStatPanel(), BorderLayout.PAGE_END);
        
        pnlMain.setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        
        pnlMain.add(pnlMainHolder, c);
        pnlMain.add(pnlSettings, c);
        
        pnlMain.setLayer(pnlMainHolder, JLayeredPane.DEFAULT_LAYER);
        pnlMain.setLayer(pnlSettings, JLayeredPane.DRAG_LAYER);
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
        LineNumberTableRowHeader tableLineNumber = new LineNumberTableRowHeader(pane, tblData);
        tableLineNumber.setBackground(Color.LIGHT_GRAY);
        pane.setRowHeaderView(tableLineNumber);
        retVal.add(pane, BorderLayout.CENTER);
        
        return retVal;
    }
    
    private JPanel createSettingsPanel(){
    	JPanel retVal = new JPanel(new BorderLayout()) {
            @Override public void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.BLACK);

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 5 * 0.1f));
                g2d.fillRect(0, 0, pnlMain.getWidth(), pnlMain.getHeight());
            }
        };
        
        retVal.setOpaque(false);
        retVal.setVisible(false);
        
        //just block the mouse clicks
        retVal.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) {e.consume();}
			@Override public void mouseClicked(MouseEvent e) {e.consume();}
		});
        
        return retVal;
    }
    
    @SuppressWarnings("serial")
	private JPanel createPagePanel(){
    	JPanel retVal = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(350, 20);
            }
        };
        pagination = new Pagination();
        retVal.add(pagination, BorderLayout.CENTER);
        
        return retVal;
    }
    
    public Pagination getPagination(){
    	return pagination;
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
        btnSettings = new JButton("*"){
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
        retVal.add(Box.createRigidArea(new Dimension(5, 10)));
        retVal.add(btnSettings);
        
        return retVal;
    }
    
    public JTextField getTxtSearch() {
        return txtSearch;
    }

    public JComponent getUI() {
        return pnlMain;
    }

    public JTable getDataTable() {
        return tblData;
    }

    public JComboBox getCboSearch() {
        return cboSearch;
    }
    
    public JButton getBtnSettings(){
    	return btnSettings;
    }
    
    public JButton getBtnSearch(){
    	return btnSearch;
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
	
	public JPanel getPnlSettings(){
		return pnlSettings;
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
		btnSettings.setEnabled(false);
		cboSearch.setEnabled(false);
		txtSearch.setEnabled(false);
	}
	
	public void enableSearch(){
		btnSearch.setEnabled(true);
		btnSettings.setEnabled(true);
		cboSearch.setEnabled(true);
		txtSearch.setEnabled(true);
	}

	public void showTableLoader(){
    	txtSearch.setEnabled(false);
    	btnSearch.setEnabled(false);
    	btnSettings.setEnabled(false);
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
    }

    public void hideTableLoader(){
    	txtSearch.setEnabled(true);
    	btnSearch.setEnabled(true);
    	btnSettings.setEnabled(true);
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
    	
    	/*for(PageButton btns : pageBtns){
    		btns.setEnabled(true);
    	}
    	
    	if(btnFirst != null) btnFirst.setEnabled(true);
    	if(btnPrev != null) btnPrev.setEnabled(true);
    	if(btnNext != null) btnNext.setEnabled(true);
    	if(btnLast != null) btnLast.setEnabled(true);*/
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
    
    /*class WorkPane extends JLayeredPane {
        private final BackingPane backingPane;

        public WorkPane() {

            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.BOTH;
            //add(createLabel("Center", Color.BLUE), gbc);

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 0;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.VERTICAL;
            //add(createLabel("Left", Color.RED), gbc);
            gbc.gridx = 2;
            //add(createLabel("Right", Color.GREEN), gbc);

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.gridheight = GridBagConstraints.REMAINDER;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.BOTH;

            backingPane = new BackingPane();
            backingPane.add(new SettingsPane());
            backingPane.setVisible(false);
            add(backingPane, gbc);

            setLayer(backingPane, DEFAULT_LAYER + 1);

        }*/
}