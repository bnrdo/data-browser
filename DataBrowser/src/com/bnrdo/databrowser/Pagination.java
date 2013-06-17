package com.bnrdo.databrowser;

import java.util.ArrayList;
import java.util.Arrays;
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
	
	public Pagination(Pagination p){
		totalPageCount = p.getTotalPagecount();
		maxExposableCount = p.getMaxExposableCount();
		currentPageNum = p.getCurrentPageNum();
		pageNumsExposed = p.getPageNumsExposed();
		pageNumsRaw = p.getPageNumsRaw();
		itemsPerPage = p.getItemsPerPage();
		paginationListeners = p.getPaginationListeners();
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
			int[] arr = ArrayUtils.subarray(pageNumsRaw, 0, maxExposableCount);
			setPageNumsExposed(arr);
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

	/* this should be used by the client for hooking in the pagination 
	 * event of the databrowser. This is not used inside the databrowser. The usual
	 * ActionListener for button is used instead for managing the cosmetic changes
	 * when navigating through buttons, changing the exposed rows, etc.
	 */
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
		if(pageNumsRaw.length == 0)
			return 1;
		
		return pageNumsRaw[0];
	}

	public int getLastRawPageNum() {
		if(pageNumsRaw.length == 0)
			return 1;
		
		return pageNumsRaw[pageNumsRaw.length - 1];
	}
	
	public Map<String, PaginationListener> getPaginationListeners(){
		return paginationListeners;
	}
	
	public int[] getPageNumsRaw(){
		return pageNumsRaw;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + currentPageNum;
		result = prime * result + itemsPerPage;
		result = prime * result + maxExposableCount;
		result = prime * result + Arrays.hashCode(pageNumsExposed);
		result = prime * result + Arrays.hashCode(pageNumsRaw);
		result = prime
				* result
				+ ((paginationListeners == null) ? 0 : paginationListeners
						.hashCode());
		result = prime * result + totalPageCount;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pagination other = (Pagination) obj;
		if (currentPageNum != other.currentPageNum)
			return false;
		if (itemsPerPage != other.itemsPerPage)
			return false;
		if (maxExposableCount != other.maxExposableCount)
			return false;
		if (!Arrays.equals(pageNumsExposed, other.pageNumsExposed))
			return false;
		if (!Arrays.equals(pageNumsRaw, other.pageNumsRaw))
			return false;
		if (paginationListeners == null) {
			if (other.paginationListeners != null)
				return false;
		} else if (!paginationListeners.equals(other.paginationListeners))
			return false;
		if (totalPageCount != other.totalPageCount)
			return false;
		return true;
	}

}
