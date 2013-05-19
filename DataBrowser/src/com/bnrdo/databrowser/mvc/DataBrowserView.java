package com.bnrdo.databrowser.mvc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

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

public class DataBrowserView {
    private JPanel pnlMain;
    private JPanel pnlPage;
    private JPanel pnlSearch;
    private JPanel pnlTable;

    private JTextField txtSearch;
    private JComboBox cboSearch;
    private JTable tblData;
    
    private JButton testButton;

    private PageButton [] pageBtns;
    private PageButton btnFirst;
    private PageButton btnPrev;
    private PageButton btnNext;
    private PageButton btnLast;

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
        pnlMain.add(createTestButton(), BorderLayout.PAGE_END);
    }

    private JPanel createTestButton(){
    	JPanel retVal = new JPanel(new BorderLayout());
    	testButton = new JButton("Test Common");
    	retVal.add(testButton, BorderLayout.CENTER);
    	return retVal;
    }
    public JButton getTestButton(){
    	return testButton;
    }
    
    @SuppressWarnings("serial")
    private JPanel createTablePanel() {
        JPanel retVal = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(500, 500);
            }
        };

        tblData = new JTable();
        JScrollPane pane = new JScrollPane(tblData);
        retVal.add(pane);
        return retVal;
    }

    @SuppressWarnings("serial")
    private JPanel createPagePanel() {
        JPanel retVal = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(250, 30);
            }
        };

        return retVal;
    }

    public JPanel createPageButtons(int [] numsExposed) {
        JPanel retVal = new JPanel();
        retVal.setLayout(new BoxLayout(retVal, BoxLayout.LINE_AXIS));

        retVal.add(Box.createHorizontalGlue());

        if (numsExposed.length > 1) {
            btnFirst = new PageButton("First");
            btnPrev = new PageButton("Prev");
            btnFirst.setFocusable(false);
            btnPrev.setFocusable(false);
            btnFirst.setVisible(false);
            btnPrev.setVisible(false);
            retVal.add(btnFirst);
            retVal.add(btnPrev);
        }

        pageBtns = new PageButton [numsExposed.length];
        int indxCtr = 1;
        for (int i : numsExposed) {
            PageButton btn = new PageButton(i);
            btn.setFocusable(false);
            pageBtns[indxCtr - 1] = btn;
            indxCtr++;
            retVal.add(btn);
        }

        if (numsExposed.length > 1) {
            btnNext = new PageButton("Next");
            btnLast = new PageButton("Last");
            btnNext.setFocusable(false);
            btnLast.setFocusable(false);
            retVal.add(btnNext);
            retVal.add(btnLast);
        }

        return retVal;
    }

    @SuppressWarnings("serial")
	private JPanel createSearchPanel() {
        JPanel retVal = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(250, 30);
            }
        };

        retVal.setLayout(new BoxLayout(retVal, BoxLayout.LINE_AXIS));

        JLabel lblSearch = new JLabel("Search Products");

        txtSearch = new JTextField() {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(100, 20);
            }
        };
        cboSearch = new JComboBox() {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(getPreferredSize().width, 20);
            }
        };

        retVal.add(Box.createRigidArea(new Dimension(10, 10)));
        retVal.add(lblSearch);
        retVal.add(Box.createRigidArea(new Dimension(5, 10)));
        retVal.add(txtSearch);
        retVal.add(Box.createRigidArea(new Dimension(5, 10)));
        retVal.add(cboSearch);

        return retVal;
    }

    public void refreshPageNumbers(int [] numsExposed) {
        if (pnlPage.getComponentCount() > 0) {
            pnlPage.remove(0);
        }
        pnlPage.add(createPageButtons(numsExposed));
        pnlPage.revalidate();
        pnlPage.repaint();
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

    @SuppressWarnings("serial")
	class PageButton extends JButton {
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
