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
package org.jlp.javafx.datas.csv;

import static org.junit.jupiter.api.Assertions.*;

import org.jlp.javafx.common.Project;
import org.junit.jupiter.api.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class CSVLineValuesTest.
 */
public class CSVLineValuesTest {
	static {
		Project.root = System.getProperty("root");
		System.out.println("root =" + Project.root);
		org.jlp.javafx.common.Project.loadDateProperties(Project.root);
		org.jlp.javafx.common.Project.loadLogfouineurProperties(Project.root);
	}

	/** The csv LT. */
	CSVLineTitle csvLT = new CSVLineTitle("date;pivots;value1(ms);value2(octets);value3(KBytes/s);", ";");

	/** The row data 2. */
	String rowData2 = "2014/09/12:20:10:04.450;pivot1;1000.0;450000;-34.2E+14;";

	/** The row data 3. */
	String rowData3 = "100000000;pivot1;1000.0;450000;-34.2E+14;";

	/**
	 * Test.
	 */
	@Test
	public void test() {
		csvLT.validTitleWithRowData(rowData2);
		CSVLineValues csvLV1 = new CSVLineValues(rowData2, csvLT);
		System.out.println("date => 2014/09/12:20:10:04.450 timeInMillis ->" + csvLV1.dateInMillis);

		assertTrue(csvLV1.namePivot.equals("pivot1"), "rowData2 csvLV1.pivot -> " + csvLV1.namePivot);
		csvLT.validTitleWithRowData(rowData3);

		CSVLineValues csvLV2 = new CSVLineValues(rowData3, csvLT);
		System.out.println("date => 100000000 timeInMillis ->" + csvLV2.dateInMillis);
		assertTrue(csvLV2.dateInMillis == 100000000, "rowData3 csvLV2.dateInMillis-> " + csvLV2.dateInMillis);
	}

}
