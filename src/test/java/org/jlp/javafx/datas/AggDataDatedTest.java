/*
 * Copyright 2017 Jean-Louis Pasturel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
*/
package org.jlp.javafx.datas;

import static org.junit.jupiter.api.Assertions.*;

import org.jlp.javafx.common.StrategyPeriod;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class AggDataDatedTest.
 */
public class AggDataDatedTest {

	/** The agg. */
	AggDataDated agg = new AggDataDated(10000000, 10000);

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@BeforeAll
	public void setUp() throws Exception {

	}

	/**
	 * Test agg data dated.
	 */
	@Test
	public void testAggDataDated() {
		assertTrue(agg.period == 10000, "VeryGood");
	}

	/**
	 * Test add value.
	 */
	@Test
	public void testAddValue() {
		// assertTrue("listValue vide",agg.listValues.size()==0);

		assertTrue(agg.addValue(20.0), "listValue 1 element");
		assertTrue(agg.addValue(-1.0), "listValue 1 element");
		assertTrue(agg.addValue(31.0), "listValue 1 element");
	}

	/**
	 * Test close.
	 */
	@Test
	public void testClose() {
		agg.close(StrategyPeriod.BEGIN);
		// assertTrue("Min OK",agg.min == -1.0d);
		// assertTrue("Max OK",agg.max == 31.0d);
		// assertTrue("Avg OK",agg.avg == (double) 50.0/3.0);
	}

	/**
	 * Test close strategy period.
	 */
	@Test
	public void testCloseStrategyPeriod() {
		assertTrue(true, "Not yet implemented");
	}

}
