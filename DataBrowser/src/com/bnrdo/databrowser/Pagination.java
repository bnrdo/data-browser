package com.bnrdo.databrowser;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.SwingPropertyChangeSupport;

import org.apache.commons.lang3.ArrayUtils;

public class Pagination {

    public static final String FN_CURRENT_PAGE_NUMBER = "currentPageNum";
    public static final String FN_PAGE_NUMS_EXPOSED = "pageNumsExposed";
    public static final String FN_TOTAL_PAGE_COUNT = "totalPageCount";
    public static final String FN_MAX_EXPOSABLE_COUNT = "maxExposableCount";

    public static final String FIRST_PAGE = "firstPage";
    public static final String LAST_PAGE = "lastPage";
    public static final String PREV_PAGE = "prevPage";
    public static final String NEXT_PAGE = "nextPage";

    private int currentPageNum;
    private int [] pageNumsRaw;
    private int [] pageNumsExposed;
    private int totalPageCount;
    private int maxExposableCount;

    private SwingPropertyChangeSupport propChangeFirer;
    private List<PaginationListener> paginationListeners;

    public Pagination() {
        totalPageCount = 100;
        maxExposableCount = 10;
        currentPageNum = 0;
        pageNumsExposed = new int [] { 0 };
        propChangeFirer = new SwingPropertyChangeSupport(this);
        paginationListeners = new ArrayList<PaginationListener>();
    }

    public void setTotalPageCount(int num) {
        List<Integer> ints = new ArrayList<Integer>();
        for (int i = 1; i <= num; i++) {
            ints.add(i);
        }
        pageNumsRaw = ArrayUtils.toPrimitive(ints.toArray(new Integer [ints
                .size()]));
        setPageNumsExposed(ArrayUtils.subarray(pageNumsRaw, 0,
                maxExposableCount));
    }

    public void setMaxExposableCount(int num) {

    }

    public void addPaginationListener(PaginationListener p) {
        paginationListeners.add(p);
    }

    public int getCurrentPageNum() {
        return currentPageNum;
    }

    public void setCurrentPageNum(int num) {
        int oldVal = currentPageNum;

        if (isCenterable(num)) {
            centerCurrentPage(num);
        } else {
            int centerPosition = (pageNumsExposed.length / 2) + 1;
            if (num < centerPosition)
                scrollToFirst();
            else
                scrollToLast();
        }

        currentPageNum = num;
        propChangeFirer.firePropertyChange(FN_CURRENT_PAGE_NUMBER, oldVal, num);

        for (PaginationListener p : paginationListeners) {
            p.pageChanged(num);
        }
    }

    private boolean isCenterable(int num) {
        int exposeLen = pageNumsExposed.length;
        int centerPosition = (exposeLen / 2) + 1;
        if (num >= centerPosition
                && num <= (getLastRawPageNum() - (exposeLen - centerPosition)))
            return true;
        return false;
    }

    private void centerCurrentPage(int toCenter) {
        int toCenterOrigCopy = toCenter;
        int exposeLen = pageNumsExposed.length;
        int centerPosition = (exposeLen / 2) + 1;
        int [] newNums = new int [exposeLen];

        // right
        for (int a = centerPosition - 2; a >= 0; a--) {
            newNums[a] = --toCenter;
        }

        // left
        for (int b = centerPosition - 1; b < exposeLen; b++) {
            newNums[b] = toCenterOrigCopy++;
        }

        setPageNumsExposed(newNums);

        /*
         * nahinto ako sa pagcecenter ng pagination ...
         * 
         * what i want is totally detached pagination, behavior, gusto ko mag
         * reregister nalang ng listener then ung manner ng kung anong data i
         * didisplay based dun sa page na currently e slected, delegated un sa
         * client ng pagination ok
         */
    }

    public void setCurrentPageNum(String where) {
        int num = 0;

        if (where.equals(NEXT_PAGE)) {
            num = currentPageNum + 1;
        } else if (where.equals(PREV_PAGE)) {
            num = currentPageNum - 1;
        } else if (where.equals(FIRST_PAGE)) {
            num = pageNumsRaw[0];
            scrollToFirst();
        } else if (where.equals(LAST_PAGE)) {
            num = pageNumsRaw[pageNumsRaw.length - 1];
            scrollToLast();
        }

        setCurrentPageNum(num);
    }

    private void scrollToFirst() {
        setPageNumsExposed(ArrayUtils.subarray(pageNumsRaw, 0,
                maxExposableCount));
    }

    private void scrollToLast() {
        setPageNumsExposed(ArrayUtils.subarray(pageNumsRaw, totalPageCount
                - maxExposableCount, totalPageCount));
    }

    public int [] getPageNumsExposed() {
        return pageNumsExposed;
    }

    public void setPageNumsExposed(int [] num) {
        int [] oldVal = pageNumsExposed;
        pageNumsExposed = num;
        propChangeFirer.firePropertyChange(FN_PAGE_NUMS_EXPOSED, oldVal,
                pageNumsExposed);
    }

    public int getFirstRawPageNum() {
        return pageNumsRaw[0];
    }

    public int getLastRawPageNum() {
        return pageNumsRaw[pageNumsRaw.length - 1];
    }
    // public int getFirstPageNum
    /*
     * public void setMaxExposableNums(int max) { int oldVal = maxExposableNums;
     * maxExposableNums = max;
     * propChangeFirer.firePropertyChange(MAX_EXPOSABLE_NUMS, oldVal, max);
     * 
     * int [] newNums = new int [max];
     * 
     * for (int i = 0; i < max; i++) { if (i > pageNumsExposed.length) break;
     * 
     * newNums[i] = pageNumsExposed[i]; }
     * propChangeFirer.firePropertyChange(PAGE_NUMS_EXPOSED, pageNumsExposed,
     * newNums); }
     */
    // public int getMaxExposableNums() {
    // return maxExposableNums;
    // }
}
