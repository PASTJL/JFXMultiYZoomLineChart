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

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.jlp.javafx.common.Project;
import org.jlp.javafx.datas.AggDataDated;

// TODO: Auto-generated Javadoc
/**
 * The Class Dispatcher.
 */
public class Dispatcher implements Consumer<String> {

	/**
	 * The hm of hm data dated.The key is the name of the series, the values is the
	 * hasHMap key =&gt; Time in Millis, value =&gt; Y Value
	 * 
	 * 
	 */

	public int rankCSV;

	/** The hm of hm data dated. */
	public Map<String, Map<Long, AggDataDated>> hmOfHmDataDated = new HashMap<String, Map<Long, AggDataDated>>();

	/**
	 * the hm key is the name series, first String is the unit, second String is the
	 * csv file absolute path.
	 */
	public Map<String, String[]> hmOfUnitSource = new HashMap<String, String[]>();

	/** The counter. */
	public int counter = 0;

	/** period in millis. */
	public long period = 1000;

	/** The nb procs. */
	final public int nbProcs = Runtime.getRuntime().availableProcessors();
	// final public int nbProcs = 8;

	/** The callables. */
	public Callable<Integer>[] callables;

	/** The is active. */
	static public Boolean isActive[];

	/** The executor. */
	public ExecutorService executor;

	/** The csv file. */
	CSVFile csvFile;

	/**
	 * Instantiates a new dispatcher.
	 *
	 * @param isMultiThreads
	 *            the is multi threads
	 * @param csvFile
	 *            the csv file
	 */
	public Dispatcher(Boolean isMultiThreads, CSVFile csvFile) {
		this(isMultiThreads, csvFile, 1000, 0);
	}

	/**
	 * Instantiates a new dispatcher.
	 *
	 * @param isMultiThreads
	 *            the is multi threads
	 * @param csvFile
	 *            the csv file
	 * @param period
	 *            the period
	 * @param rankCSV
	 *            the rank CSV
	 */
	public Dispatcher(Boolean isMultiThreads, CSVFile csvFile, long period, int rankCSV) {
		counter = 0;
		this.period = period;
		this.csvFile = csvFile;
		this.rankCSV = rankCSV;
		Project.multiThread = Double
				.parseDouble(Project.propsLogfouineur.getProperty("logFouineur.threads.coeff", "1.0"));
		int nbTask = Math.max(1, (int) (Project.multiThread * nbProcs));
		if (isMultiThreads) {
			callables = new TaskHandler[nbTask];
			isActive = new Boolean[nbTask];
		} else {
			callables = new TaskHandler[1];
			isActive = new Boolean[1];

		}
		for (int i = 0; i < callables.length; i++) {
			callables[i] = new TaskHandler(i, csvFile, period, rankCSV);

			isActive[i] = false;
		}
		executor = Executors.newFixedThreadPool(callables.length);
	}

	/**
	 * Accept.
	 *
	 * @param record
	 *            the record
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.function.Consumer#accept(java.lang.Object)
	 */
	@Override
	public void accept(String record) {
		// int nbRunning = 0;
		// for (boolean bool : isActive) {
		// if (bool)
		// nbRunning++;
		// }
		// System.out.println(nbRunning + " Task");

		// TODO Auto-generated method stub
		if (counter == 0 || record.length() < 10) {
			System.out.println("Dispatcher skipped record=" + record);
		} else {
			int currentRank = counter % callables.length;
			while (isActive[currentRank]) {

				currentRank = (currentRank + 1) % callables.length;
			}
			isActive[currentRank] = true;
			((TaskHandler) callables[currentRank]).record = record;

			try {
				executor.submit(callables[currentRank]);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		counter++;

	}

	/**
	 * Agg all values.
	 */
	public void aggAllValues() {
		// create Set<String> all all nameseries
		Set<String> nameSeries = new HashSet<String>();
		for (Callable<Integer> task : this.callables) {
			for (String key : ((TaskHandler) task).hmOfHmAggData.keySet()) {

				nameSeries.add(key);
				if (!hmOfUnitSource.containsKey(key)) {
					hmOfUnitSource.put(key, ((TaskHandler) task).hmOfUnitSource.get(key));
				}
			}

		}

		// To construct the TaskAgg we pass an array of HMap<Long, AggDataDated> that
		// share the same nameserie
		executor = Executors.newFixedThreadPool(nameSeries.size());
		Callable<Integer>[] tabTaskAgg = new TaskAgg[nameSeries.size()];
		int i = 0;
		for (String name : nameSeries) {
			int lengKeySeries = 0;
			List<Map<Long, AggDataDated>> listHmsDatas = new ArrayList<Map<Long, AggDataDated>>(0);
			for (Callable<Integer> task : callables) {

				if (((TaskHandler) task).hmOfHmAggData.containsKey(name)) {
					listHmsDatas.add(((TaskHandler) task).hmOfHmAggData.get(name));
					lengKeySeries++;
				}

			}
			tabTaskAgg[i] = new TaskAgg(name, listHmsDatas);

			executor.submit(tabTaskAgg[i]);
			i++;
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException ie) {
			}
		}
		// construction de
		for (Callable<Integer> task : tabTaskAgg) {
			hmOfHmDataDated.put(((TaskAgg) task).nameSerie, ((TaskAgg) task).hmDatasClosed);
		}
		// help garbage
		tabTaskAgg = null;
		callables = null;
		System.out.println("yes i finish aggregate all the task values");

	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		long deb = System.currentTimeMillis();
		Project.root = System.getProperty("root");

		org.jlp.javafx.common.Project.loadDateProperties(Project.root);
		org.jlp.javafx.common.Project.loadLogfouineurProperties(Project.root);

		CSVFile csvFile = new CSVFile(Project.root + File.separator + "datas" + File.separator + "ficDuree.csv");

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

		disp.aggAllValues();
		System.out
				.println("File With Pivots Test all tasks terminated duration :" + (System.currentTimeMillis() - deb));
		for (Entry<String, Map<Long, AggDataDated>> entry : disp.hmOfHmDataDated.entrySet()) {
			System.out.println(entry.getKey() + " has " + entry.getValue().size() + " records");
		}
	}

}
