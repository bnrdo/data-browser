package com.bnrdo.databrowser;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.bnrdo.databrowser.exception.ModelException;

public class DataBrowserTest {

	private DataBrowser dbrowse;

	@Before
	public void setUp() {
		dbrowse = new DataBrowser();
	}

	@Test
	public void testPaginationValidation() {
		Pagination p = new Pagination();

		p.setMaxExposableCount(10);
		p.setTotalPageCount(10);
		p.setCurrentPageNum(11);
		try {
			//dbrowse.setPagination(p);
			fail("No exception thrown at first test");
		} catch (ModelException e) {
			assertTrue(e instanceof ModelException);
		}
		
		p.setMaxExposableCount(10);
		p.setTotalPageCount(10);
		p.setCurrentPageNum(0);
		try {
			//dbrowse.setPagination(p);
			fail("No exception thrown at second test");
		} catch (ModelException e) {
			assertTrue(e instanceof ModelException);
		}
	}
}
