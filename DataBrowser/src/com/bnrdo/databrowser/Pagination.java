package com.bnrdo.databrowser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.SwingPropertyChangeSupport;

import org.apache.commons.lang3.ArrayUtils;

import com.bnrdo.databrowser.exception.ModelException;
import com.bnrdo.databrowser.listener.PaginationListener;

public class Pagination {

	public static final String FIRST_PAGE = "firstPage";
	public static final String LAST_PAGE = "lastPage";
	public static final String PREV_PAGE = "prevPage";
	public static final String NEXT_PAGE = "nextPage";
	public static final String ITEMS_PER_PAGE = "itemsPerPage";
	public static final String PAGINATE_CONTENT_ID = "paginateContentId";

	private int currentPageNum;
	private int[] pageNumsRaw;
	private int[] pageNumsExposed;
	private int totalPageCount;
	private int maxExposableCount;
	private int itemsPerPage;

	private Map<String, PaginationListener> paginationListeners;

	public Pagination() {
		totalPageCount = 0;
		maxExposableCount = 0;
		currentPageNum = 1;
		pageNumsExposed = new int[] { 0 };
		pageNumsRaw = new int[] { 1 };
		paginationListeners = new HashMap<String, PaginationListener>();
	}

	public void setTotalPageCount(int num) {
		totalPageCount = num;

		List<Integer> ints = new ArrayList<Integer>();
		for (int i = 1; i <= num; i++) {
			ints.add(i);
		}
		pageNumsRaw = ArrayUtils.toPrimitive(ints.toArray(new Integer[ints.size()]));
		
		//if the called when the datasource is newly set reset the page nums exposed to the initial one. else
		//preserve the old page nums exposed (works perfectly in sorting)
		if(getCurrentPageNum() == 1){
			setPageNumsExposed(ArrayUtils.subarray(pageNumsRaw, 0, maxExposableCount));
		}
	}

	public int getTotalPagecount() {
		return totalPageCount;
	}

	public void setMaxExposableCount(int num) {
		maxExposableCount = num;
	}

	public int getMaxExposableCount() {
		return maxExposableCount;
	}

	public void addPaginationListener(String id, PaginationListener p) {
		paginationListeners.put(id, p);
	}
	
	public int getItemsPerPage() {
		return itemsPerPage;
	}

	public void setItemsPerPage(int num) {
		itemsPerPage = num;
	}

	public void removePaginationListener(int index){
		paginationListeners.remove(index);
	}
	
	public void clearPaginationListeners(){
		paginationListeners.clear();
	}
	
	public void removePaginationListener(String id){
		paginationListeners.remove(id);
	}

	public int getCurrentPageNum() {
		return currentPageNum;
	}

	public void setCurrentPageNum(Integer num) {
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
		
		for(String s : paginationListeners.keySet()){
			paginationListeners.get(s).pageChanged(num);
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
		int[] newNums = new int[exposeLen];

		// right
		for (int a = centerPosition - 2; a >= 0; a--) {
			newNums[a] = --toCenter;
		}

		// left
		for (int b = centerPosition - 1; b < exposeLen; b++) {
			newNums[b] = toCenterOrigCopy++;
		}

		setPageNumsExposed(newNums);
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
		} else{
			throw new ModelException("Invalid page number.");
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

	public int[] getPageNumsExposed() {
		return pageNumsExposed;
	}

	public void setPageNumsExposed(int[] num) {
		pageNumsExposed = num;
	}

	public int getFirstRawPageNum() {
		return pageNumsRaw[0];
	}

	public int getLastRawPageNum() {
		return pageNumsRaw[pageNumsRaw.length - 1];
	}
}
