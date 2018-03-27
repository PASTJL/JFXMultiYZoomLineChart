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

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.jlp.javafx.common.Project;
import org.jlp.javafx.datas.AggDataDated;
import org.junit.jupiter.api.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class CSVFileTest.
 */
public class CSVFileTest {
	static {
		Project.root = System.getProperty("root");
		System.out.println("root =" + Project.root);
		org.jlp.javafx.common.Project.loadDateProperties(Project.root);
		org.jlp.javafx.common.Project.loadLogfouineurProperties(Project.root);
		try {
			Project.dateBeginProject = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss").parse("2010/05/12:16:00:00");
			Project.dateEndProject = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss").parse("2010/05/12:19:00:00");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Test create CSV file.
	 */
	@Test
	public void testCreateCSVFile() {
		System.out
				.println("path = " + Project.root + File.separator + "datas" + File.separator + "correctCsvFile0.csv");
		CSVFile csvFile = new CSVFile(Project.root + File.separator + "datas" + File.separator + "correctCsvFile0.csv");
		assertTrue(true, "correct");
		csvFile = new CSVFile(Project.root + File.separator + "datas" + File.separator + "incorrectCsvFile1.csv");
		assertTrue(true, "incorrect file ");
	}

	/**
	 * Test handling CSV file.
	 */
	@Test
	public void testHandlingCSVFile() {

		CSVFile csvFile = new CSVFile(Project.root + File.separator + "datas" + File.separator + "ficDuree.csv");
		csvFile.isGZippedFile = false;
		try {
			Project.dateBeginProject = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss").parse("2010/05/12:16:00:00");
			Project.dateEndProject = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss").parse("2010/05/12:19:00:00");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LineFileReader lfr = new LineFileReader(csvFile);
		Dispatcher disp = new Dispatcher(true, csvFile);

		lfr.linesStream.forEach(disp);
		disp.executor.shutdown();
		while (!disp.executor.isTerminated()) {

		}
		System.out.println("List series names");
		for (Callable<Integer> task : disp.callables) {
			for (String key : ((TaskHandler) task).hmOfHmAggData.keySet()) {
				System.out.println("rank = " + ((TaskHandler) task).rank + " ; key= " + key);
			}
		}
		System.out.println("Test all tasks terminated");
	}

	/**
	 * Test handling CSV file.
	 */
	@Test
	public void testHandlingCSVGZIPFile() {

		/* Gzipped file */
		System.out.println("GZipped File");
		CSVFile csvFile = new CSVFile(Project.root + File.separator + "datas" + File.separator + "ficDuree.csv.gz");
		csvFile.isGZippedFile = true;
		LineFileReader lfr = new LineFileReader(csvFile);
		Dispatcher disp = new Dispatcher(true, csvFile);

		lfr.linesStream.forEach(disp);
		disp.executor.shutdown();
		while (!disp.executor.isTerminated()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException ie) {
			}
		}
		System.out.println("Testbis all tasks terminated");
	}

	/**
	 * Test handling CSV file with pivots.
	 */
	@Test
	public void testHandlingCSVFileWithPivots() {
		System.out.println("File With Pivots csvFile: " + Project.root + File.separator + "datas" + File.separator
				+ "fileWithPivots.csv");
		CSVFile csvFile = new CSVFile(Project.root + File.separator + "datas" + File.separator + "fileWithPivots.csv");

		csvFile.isGZippedFile = false;
		if (null == csvFile) {
			System.out.println("csvFile is null");
		}
		LineFileReader lfr = new LineFileReader(csvFile);
		Dispatcher disp = new Dispatcher(true, csvFile);

		lfr.linesStream.forEach(disp);
		disp.executor.shutdown();
		while (!disp.executor.isTerminated()) {

		}
		System.out.println("File With Pivots List series names");
		for (Callable<Integer> task : disp.callables) {
			for (String key : ((TaskHandler) task).hmOfHmAggData.keySet()) {
				System.out.println("rank = " + ((TaskHandler) task).rank + " ; key= " + key);
			}

		}
		for (Callable<Integer> task : disp.callables) {
			System.out.println("rank = " + ((TaskHandler) task).rank + " has treated   "
					+ ((TaskHandler) task).counterlocal + " records");

		}
		System.out.println("File With Pivots Test all tasks terminated");

	}

	/**
	 * Test handling CSV file with pivots agg data.
	 */
	@Test

	public void testHandlingCSVFileWithPivotsAggData() {

		Long deb = System.currentTimeMillis();
		CSVFile csvFile = new CSVFile(Project.root + File.separator + "datas" + File.separator + "fileWithPivots.csv");

		csvFile.isGZippedFile = false;
		if (null == csvFile) {
			System.out.println("csvFile is null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(csvFile.csvTitle.strFormatDate);
		long period = 1000;
		try {
			Project.dateBeginProject = sdf.parse("2014/12/05:16:00:00.000");

			Project.dateEndProject = sdf.parse("2014/12/05:18:00:00.000");
			period = (Project.dateEndProject.getTime() - Project.dateBeginProject.getTime()) / Project.nbPointsLC;
		} catch (ParseException pe) {

		}

		LineFileReader lfr = new LineFileReader(csvFile);
		Dispatcher disp = new Dispatcher(true, csvFile, period, 0);

		lfr.linesStream.forEach(disp);
		disp.executor.shutdown();
		while (!disp.executor.isTerminated()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException ie) {
			}
		}

		for (Callable<Integer> task : disp.callables) {
			System.out.println("rank = " + ((TaskHandler) task).rank + " hmOfHmAggData		 has "
					+ ((TaskHandler) task).hmOfHmAggData.size() + " series");
			for (Entry<String, Map<Long, AggDataDated>> entry : ((TaskHandler) task).hmOfHmAggData.entrySet()) {
				System.out.println("rank = " + ((TaskHandler) task).rank + " taille pour " + entry.getKey() + " => "
						+ entry.getValue().size());
			}

		}

		for (Callable<Integer> task : disp.callables) {
			System.out.println("rank = " + ((TaskHandler) task).rank + " hmOfHmAggData		 has "
					+ ((TaskHandler) task).hmOfHmAggData.size() + " series");
			for (Entry<String, Map<Long, AggDataDated>> entry : ((TaskHandler) task).hmOfHmAggData.entrySet()) {
				System.out.println("rank = " + ((TaskHandler) task).rank + " taille pour " + entry.getKey() + " => "
						+ entry.getValue().size());
			}

		}

		disp.aggAllValues();
		System.out.println(
				"File With Pivots Test with agg  all tasks terminated duration :" + (System.currentTimeMillis() - deb));
	}
}
