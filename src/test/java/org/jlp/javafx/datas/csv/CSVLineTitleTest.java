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
 * The Class CSVLineTitleTest.
 */
public class CSVLineTitleTest {
	static {
		Project.root = System.getProperty("root");
		System.out.println("root =" + Project.root);
		org.jlp.javafx.common.Project.loadDateProperties(Project.root);
	}

	/** The csv LT. */
	CSVLineTitle csvLT = new CSVLineTitle("date;pivots;value1(ms);value2(octets);value3(KBytes/s);", ";");

	/** The row data 1. */
	String rowData1 = "2014/09/12;pivot1;1000.0;450000;34;";

	/** The row data 2. */
	String rowData2 = "2014/09/12:20:10:04.450;pivot1;1000.0;450000;-34.2E+14;";

	/** The row data 3. */
	String rowData3 = "2014/09/12:20:10:04.450;pivot1;;;-34.2E+14;";

	/** The row data 4. */
	String rowData4 = "2014/09/12:20:10:04.450;pivot1;oooo;;-34.2E+14;";

	/**
	 * Test constructor.
	 */
	@Test
	public void testConstructor() {
		CSVLineTitle csvLT2 = new CSVLineTitle("date;pivots;value1(ms);value2(octets);value3(KBytes/s);");
		assertTrue(csvLT2.separator.equals(";"), "Construct cvsLT2 trying to find separator -> " + csvLT2.separator);
	}

	/**
	 * Test retrieve column by name.
	 */
	@Test
	public void testRetrieveColumnByName() {
		assertTrue(true, "Not yet implemented");
	}

	/**
	 * Test CSV line title.
	 */
	@Test
	public void testCSVLineTitle() {
		assertTrue(true, "Not yet implemented");
	}

	/**
	 * Testvalid title with row data.
	 */
	@Test
	public void testvalidTitleWithRowData() {

		csvLT.validTitleWithRowData(rowData1);
		assertTrue(csvLT.strFormatDate.equals("yyyy/MM/dd"),
				"Controle rowdata1 : 2014/09/12 -> " + csvLT.strFormatDate);
		csvLT.validTitleWithRowData(rowData2);
		assertTrue(csvLT.strFormatDate.equals("yyyy/MM/dd:HH:mm:ss.SSS"),
				"Controle rowdata2 : 2014/09/12:20:10:04.450 -> ");
		boolean ret = csvLT.validTitleWithRowData(rowData2);
		assertTrue(ret, "Controle rowdata2 : 2014/09/12:20:10:04;pivot1;1000.0;450000;-34.2E+14;-> ");
	}

	/**
	 * Testvalid title with row data miss values.
	 */
	@Test
	public void testvalidTitleWithRowDataMissValues() {
		csvLT.validTitleWithRowData(rowData3);
		System.out.println(" rowdata3 -> " + rowData3 + " taille hmValuesColumns : " + csvLT.hmValuesColums.size());

		assertTrue(true, "Controle rowdata3 :  -> " + rowData3);

		csvLT.validTitleWithRowData(rowData4);
		System.out.println(" rowdata3 -> " + rowData4 + " taille hmValuesColumns : " + csvLT.hmValuesColums.size());

		assertTrue(csvLT.hmValuesColums.size() == 2, "Controle rowdata4 :  -> " + rowData4);
	}

	/**
	 * Test novalid title.
	 */
	@Test
	public void testNovalidTitle() {
		CSVLineTitle csvLT = new CSVLineTitle("value1(ms);value2(octets);value3(KBytes/s);", ";");
		assertTrue(csvLT.datesColumn == -1, "Line incorrect csvLT.datesColumn=" + csvLT.datesColumn);
	}
}
