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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;

import org.jlp.javafx.common.Project;
import org.jlp.javafx.datas.AggDataDated;

// TODO: Auto-generated Javadoc
/**
 * The Class CSVFile.
 */
public class CSVFile {

	/** The rank series. */
	public static int rankSeries = 0;

	/** The path. */
	public String path = ".";

	/** The line title. */
	public String lineTitle = "";

	/** The line data. */
	public String lineData = "";

	/** The csv title. */
	public CSVLineTitle csvTitle = null;

	/** The is multi thread. */
	public boolean isMultiThread = true;

	/** The is G zipped file. */
	public boolean isGZippedFile = false;

	/** The disp. */
	public Dispatcher disp;

	/**
	 * Instantiates a new CSV file.
	 *
	 * @param path
	 *            the path
	 */
	public CSVFile(String path) {
		this(path, true);
	}

	/**
	 * Instantiates a new CSV file.
	 *
	 * @param path
	 *            the path
	 * @param multiThread
	 *            the multi thread
	 */
	public CSVFile(String path, boolean multiThread) {
		this.path = path;
		if (!controExtensions(path))
			try {
				throw new Exception("This file :" + path + "  has an incorrect extension");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		this.isMultiThread = multiThread;
		if (path.endsWith(".gz")) {
			isGZippedFile = true;
		} else
			isGZippedFile = false;
		try {
			fillTitleAndData();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Contro extensions.
	 *
	 * @param path
	 *            the path
	 * @return true, if successful
	 */
	private boolean controExtensions(String path) {
		// TODO Auto-generated method stub
		String[] suffixes = Project.propsLogfouineur.getProperty("csvviewer.suffixes").split(";");
		for (String suff : suffixes) {
			if (path.toLowerCase().endsWith(suff)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Fill title and data.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void fillTitleAndData() throws Exception {
		String line = "";
		if (isGZippedFile) {

			try (InputStream fis = new FileInputStream(path);

					BufferedInputStream br = new BufferedInputStream(fis, 65535);
					GZIPInputStream gzipIs = new GZIPInputStream(br);) {
				boolean boucle = true;
				boolean firstLineRead = false;
				BufferedReader reader = new BufferedReader(new InputStreamReader(gzipIs));
				while ((line = reader.readLine().trim()) != null && boucle) {

					if ((line.toLowerCase().trim().contains("date") || line.toLowerCase().trim().contains("time"))
							&& !firstLineRead) {
						lineTitle = line;

						firstLineRead = true;
					} else if (line.length() > 0) {
						/* it is a data Row */
						lineData = line;
						if (lineTitle.length() > 0) {
							boucle = false;
						} else {
							System.out.println("The File :" + path + " has an incorrect title line : " + lineTitle
									+ "or no title line at ALL");
							throw new Exception("The File :" + path + " has an incorrect title line : " + lineTitle
									+ " or no title line at ALL");
						}
					}

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			InputStream fis = null;
			InputStreamReader isr = null;
			BufferedReader br = null;
			try {
				fis = new FileInputStream(path);
				isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
				br = new BufferedReader(isr);
				boolean boucle = true;
				boolean firstLineRead = false;
				while ((line = br.readLine().trim()) != null && boucle) {

					if ((line.toLowerCase().contains("date") || line.toLowerCase().contains("time"))
							&& !firstLineRead) {
						lineTitle = line;
						firstLineRead = true;
					} else if (line.length() > 0) {
						/* it is a data Row */
						lineData = line;
						if (lineTitle.length() > 0) {
							boucle = false;
						} else {
							System.out.println("The File :" + path + " has an incorrect title line : " + lineTitle
									+ "or no title line at ALL");
							throw new Exception("The File :" + path + " has an incorrect title line : " + lineTitle
									+ " or no title line at ALL");
						}
					}

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				br.close();
			}
		}
		if (lineTitle.length() > 10 && lineData.length() > 10) {
			/* File seems to be correct */
			csvTitle = new CSVLineTitle(lineTitle);
			System.out.println("File : " + path + " is correct for charting");
			if (!csvTitle.validTitleWithRowData(lineData)) {
				throw new Exception("The File :" + path + " has an incorrect datas in  :" + lineData);
			}
		} else {
			throw new Exception(
					"The File :" + path + " cannot be charted with title : " + lineTitle + " and data as :" + lineData);
		}
	}

	/**
	 * Proceed csv file.
	 *
	 * @param multiThread            the multi thread
	 * @param period            the period
	 * @param rankCSV the rank CSV
	 */
	public void proceedCsvFile(Boolean multiThread, Long period, int rankCSV) {

		LineFileReader lfr = new LineFileReader(this);
		disp = new Dispatcher(multiThread, this, period, rankCSV);

		lfr.linesStream.forEach(disp);
		disp.executor.shutdown();
		while (!disp.executor.isTerminated()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException ie) {
			}
		}

		disp.aggAllValues();
		/* Construction of series */
		// TODO

	}

	/**
	 * Mainbis.
	 *
	 * @param args the args
	 */
	public static void mainbis(String[] args) {

		Long deb = System.currentTimeMillis();
		Project.root = System.getProperty("root");
		System.out.println("root =" + Project.root);
		org.jlp.javafx.common.Project.loadDateProperties(Project.root);
		org.jlp.javafx.common.Project.loadLogfouineurProperties(Project.root);
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
			System.out.println("rank = " + ((TaskHandler) task).rank + " hmOfHmAggData	 has "
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

	/**
	 * The mainbis method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		long deb = System.currentTimeMillis();

		Project.root = System.getProperty("root");
		System.out.println("root =" + Project.root);
		org.jlp.javafx.common.Project.loadDateProperties(Project.root);
		org.jlp.javafx.common.Project.loadLogfouineurProperties(Project.root);

		CSVFile csvFile = new CSVFile(Project.root + File.separator + "datas" + File.separator + "fileWithPivots.csv");
		SimpleDateFormat sdf = new SimpleDateFormat(csvFile.csvTitle.strFormatDate);
		long period = 1000;

		try {
			Project.dateBeginProject = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss").parse("2014/12/05:16:00:00");
			Project.dateEndProject = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss").parse("2014/12/05:19:00:00");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		period = (Project.dateEndProject.getTime() - Project.dateBeginProject.getTime()) / Project.nbPointsLC;

		csvFile.proceedCsvFile(true, period, 0);
		System.out
				.println("File With Pivots Test all tasks terminated duration :" + (System.currentTimeMillis() - deb));
		for (Entry<String, Map<Long, AggDataDated>> entry : csvFile.disp.hmOfHmDataDated.entrySet()) {
			System.out.println(entry.getKey() + " has " + entry.getValue().size() + " records");
		}
	}
}
