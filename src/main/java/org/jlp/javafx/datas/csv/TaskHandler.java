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

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.jlp.javafx.common.Project;
import org.jlp.javafx.datas.AggDataDated;

// TODO: Auto-generated Javadoc
/**
 * The Class TaskHandler.
 */
public class TaskHandler implements Callable<Integer> {

	/**
	 * <p>
	 * The key of enclosing Map is the name of the serie, the enclosed Map contains
	 * the values
	 * </p>
	 * <p>
	 * the name of the series is construct as :
	 * </p>
	 * <p>
	 * - &lt;the name of the pivot|"gen"&gt;-&lt;the name of the value&gt;
	 * </p>
	 * .
	 */
	public Map<String, Map<Long, AggDataDated>> hmOfHmAggData = new HashMap<String, Map<Long, AggDataDated>>();

	/** The rank CSV. */
	public int rankCSV = 0;
	/**
	 * the hm key is the name series, first String is the unit, second String is the
	 * csv file absolute path.
	 */
	public Map<String, String[]> hmOfUnitSource = new HashMap<String, String[]>();

	/** The sdf. */
	public SimpleDateFormat sdf;

	/** The counterlocal. */
	public int counterlocal = 0;
	/** The rank. */
	int rank;

	/** the period in millis. */
	public long period;

	/** The record. */
	public String record;

	/** The is with pivots. */
	private boolean isWithPivots = false;

	/** The csv file. */
	private CSVFile csvFile;

	/**
	 * Instantiates a new task handler.
	 *
	 * @param rank
	 *            the rank
	 * @param csvFile
	 *            the csv file
	 */
	public TaskHandler(int rank, CSVFile csvFile) {
		this(rank, csvFile, 1000, 0);
	}

	/**
	 * Instantiates a new task handler.
	 *
	 * @param rank            the rank
	 * @param csvFile            the csv file
	 * @param period            the period
	 * @param rankCSV the rank CSV
	 */
	public TaskHandler(int rank, CSVFile csvFile, long period, int rankCSV) {
		super();
		this.period = period;
		this.csvFile = csvFile;
		this.rankCSV = rankCSV;
		counterlocal = 0;
		// tester les formats timeInMillis et autre
		// TODO

		if (!csvFile.csvTitle.strFormatDate.toLowerCase().contains("timein")) {
			sdf = new SimpleDateFormat(csvFile.csvTitle.strFormatDate);
		} else
			sdf = null;
		if (csvFile.csvTitle.pivotsColumn < 0) {
			isWithPivots = false;
			/* Construct the prefix name of the series */
			for (Entry<Integer, String[]> entry : csvFile.csvTitle.hmValuesColums.entrySet()) {
				String nameSeries = "gen-" + entry.getValue()[0] + "-" + rankCSV;
				;

				if (!hmOfHmAggData.containsKey(nameSeries)) {
					hmOfHmAggData.put(nameSeries, new HashMap<Long, AggDataDated>());
					hmOfUnitSource.put(nameSeries, new String[] { entry.getValue()[1], csvFile.path });
				} else {
					// duplicate name of a value
					try {
						throw new Exception("duplicate name of a value :" + entry.getValue()[0]);
					} catch (Exception e) {
						// TODO Auto-generated catch block

					}
				}
			}
		} else {
			isWithPivots = true;
		}
		this.rank = rank;
		/* Construct the prefix name of the series searching the pivots if any */

	}

	/**
	 * Call.
	 *
	 * @return the integer
	 * @throws Exception the exception
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Integer call() throws Exception {
		// TODO Auto-generated method stub
		// System.out.println("CounterLocal=" + counterlocal + " ;Treating record :" +
		// record);
		if (!isWithPivots) {
			String key = "";
			String[] tab = record.split(csvFile.csvTitle.separator, -1);
			for (Entry<Integer, String[]> entry : csvFile.csvTitle.hmValuesColums.entrySet()) {
				if (tab[entry.getKey()].trim().length() > 0) {
					key = "gen" + "-" + entry.getValue()[0] + "-" + rankCSV;
					Long dateInMillis = 0L;
					// System.out.println("key=" + key + " sdfFormat = " +
					// csvFile.csvTitle.strFormatDate);
					if (null != sdf) {
						dateInMillis = sdf.parse(tab[csvFile.csvTitle.datesColumn].trim()).getTime();
					} else {
						dateInMillis = getTime(tab[csvFile.csvTitle.datesColumn], csvFile.csvTitle.strFormatDate);
					}
					/*
					 * valid if the date is between Project.dateBeginProject and
					 * Project.dateRndProject
					 */
					if ((null != Project.dateBeginProject) && (null != Project.dateEndProject)
							&& !(dateInMillis >= Project.dateBeginProject.getTime()
									&& dateInMillis <= Project.dateEndProject.getTime())) {
						counterlocal++;
						Dispatcher.isActive[rank] = false;
						return rank;
					}
					Long keyForHmAggData = (dateInMillis / period) * period;
					// System.out.println("keyForHmAggData=" + keyForHmAggData);
					HashMap<Long, AggDataDated> hmAggDataDated = (HashMap<Long, AggDataDated>) hmOfHmAggData.get(key);
					/* on test que la data existe */
					// If the value is a string with a length 0 skip the value

					if (hmAggDataDated.containsKey(keyForHmAggData)) {
						// // merging
						// hmAggDataDated.put(key, value)
						AggDataDated aggDataToMerge = new AggDataDated(keyForHmAggData, period);
						aggDataToMerge.addValue(Double.parseDouble(tab[entry.getKey()].trim().replace(",", ".")));
						AggDataDated aggDataExist = hmAggDataDated.get(keyForHmAggData);
						aggDataExist.merge(aggDataToMerge);
						hmAggDataDated.put(keyForHmAggData, aggDataExist);
						hmOfHmAggData.put(key, hmAggDataDated);

					} else {
						// creation
						AggDataDated aggDataToAdd = new AggDataDated(keyForHmAggData, period);
						aggDataToAdd.addValue(Double.parseDouble(tab[entry.getKey()].trim().replace(",", ".")));
						hmAggDataDated.put(keyForHmAggData, aggDataToAdd);
						hmOfHmAggData.put(key, hmAggDataDated);
					}
				}
			}

		} else {
			// Control if the serie exist if not creation

			String[] tab = record.split(csvFile.csvTitle.separator, -1);

			for (Entry<Integer, String[]> entry : csvFile.csvTitle.hmValuesColums.entrySet()) {
				String nameSeries = tab[csvFile.csvTitle.pivotsColumn] + "-" + entry.getValue()[0] + "-" + rankCSV;
				if (!hmOfHmAggData.containsKey(nameSeries)) {
					hmOfHmAggData.put(nameSeries, new HashMap<Long, AggDataDated>());
					hmOfUnitSource.put(nameSeries, new String[] { entry.getValue()[1], csvFile.path });
				}

			}

			/*
			 * from record extracting the pivot and the value to get the keys of the series
			 */
			/* get the name of the pivot */
			String pivot = record.split(csvFile.csvTitle.separator)[csvFile.csvTitle.pivotsColumn];
			/* the values from the title value and concat with pivot */
			String key = "";

			for (Entry<Integer, String[]> entry : csvFile.csvTitle.hmValuesColums.entrySet()) {
				// skip the String with length =0

				if (null != tab[entry.getKey()] && tab[entry.getKey()].trim().length() > 0) {
					key = pivot + "-" + entry.getValue()[0] + "-" + rankCSV;
					Long dateInMillis = 0L;
					// System.out.println("key=" + key);

					if (null != sdf) {
						dateInMillis = sdf.parse(tab[csvFile.csvTitle.datesColumn].trim()).getTime();
					} else {
						dateInMillis = getTime(tab[csvFile.csvTitle.datesColumn], csvFile.csvTitle.strFormatDate);
					}
					/*
					 * valid if the date is between Project.dateBeginProject and
					 * Project.dateRndProject
					 */

					if ((null != Project.dateBeginProject) && (null != Project.dateEndProject)
							&& !(dateInMillis >= Project.dateBeginProject.getTime()
									&& dateInMillis <= Project.dateEndProject.getTime())) {
						counterlocal++;
						Dispatcher.isActive[rank] = false;
						return rank;
					}

					Long keyForHmAggData = (dateInMillis / period) * period;
					Map<Long, AggDataDated> hmAggDataDated = hmOfHmAggData.get(key);
					/* on test que la data existe */

					if (hmAggDataDated.containsKey(keyForHmAggData)) {
						// merging
						// hmAggDataDated.put(key, value)
						AggDataDated aggDataToMerge = new AggDataDated(keyForHmAggData, period);
						aggDataToMerge.addValue(Double.parseDouble(tab[entry.getKey()].trim().replace(",", ".")));
						AggDataDated aggDataExist = hmAggDataDated.get(keyForHmAggData);
						aggDataExist.merge(aggDataToMerge);
						hmAggDataDated.put(keyForHmAggData, aggDataExist);
						hmOfHmAggData.put(key, hmAggDataDated);

					} else {
						// creation
						AggDataDated aggDataToAdd = new AggDataDated(keyForHmAggData, period);

						aggDataToAdd.addValue(Double.parseDouble(tab[entry.getKey()].trim().replace(",", ".")));

						hmAggDataDated.put(keyForHmAggData, aggDataToAdd);

						hmOfHmAggData.put(key, hmAggDataDated);

					}

				}

			}

		}
		// System.out.println("rank = " + rank + " ; " + record);
		// System.out.println("counterlocal= " + counterlocal);
		counterlocal++;

		Dispatcher.isActive[rank] = false;
		return rank;
	}

	/**
	 * Gets the time.
	 *
	 * @param strDouble
	 *            the str double
	 * @param strFormatDate
	 *            the str format date
	 * @return the time
	 */
	private Long getTime(String strDouble, String strFormatDate) {

		// timeInMillis=1\\d{12}
		// timeInSecond=1\\d{9}
		// timeInSecondDotMillis=1\\d{9}\\.\\d+
		// timeInSecondCommaMillis=1\\d{9}\\,\\d+

		double recup = Double.parseDouble(strDouble.trim().replace(",", "."));
		long retLong = 0L;
		switch (strFormatDate.trim()) {
		case "timeInMillis":
			retLong = (long) recup;
			break;

		case "timeInSecond":
			retLong = (long) (recup * 1000);
			break;
		case "timeInSecondCommaMillis":
		case "timeInSecondDotMillis":
			retLong = (long) (recup * 1000);
			break;
		default:
			retLong = (long) recup;
			break;
		}

		return retLong;
	}

}
