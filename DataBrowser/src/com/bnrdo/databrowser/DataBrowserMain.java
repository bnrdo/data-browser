package com.bnrdo.databrowser;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.bnrdo.databrowser.DataBrowser;
import com.bnrdo.databrowser.Pagination;

public class DataBrowserMain {
    public static void main(String [] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @SuppressWarnings( { "unchecked", "serial" })
            public void run() {
                try {
                    UIManager
                            .setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                } catch (InstantiationException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (UnsupportedLookAndFeelException e1) {
                    e1.printStackTrace();
                }
                DataBrowser dbrowse = new DataBrowser();
                Pagination p = new Pagination();
                p.setMaxExposableCount(10);
                p
                        .setPageNumsExposed(new int [] { 1, 2, 3, 4, 5, 6, 7,
                                8, 9, 10 });
                p.setTotalPageCount(100);
                p.setCurrentPageNum(1);
                dbrowse.setPagination(p);

                JFrame frame = new JFrame("Demo");
                frame.getContentPane().setLayout(new BorderLayout());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                frame.getContentPane().add(dbrowse, BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
