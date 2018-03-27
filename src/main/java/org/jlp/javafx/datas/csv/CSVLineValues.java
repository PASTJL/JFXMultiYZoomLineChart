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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

// TODO: Auto-generated Javadoc
/**
 * The Class CSVLineValues.
 */
public class CSVLineValues {
	
	/** The values. */
	public double[] values;
	
	/** The name pivot. */
	public String namePivot;
	
	/** The date in millis. */
	public Long dateInMillis;
	
	/** The correct time in millis. */
	public Long correctTimeInMillis = 0L;

	/**
	 * Instantiates a new CSV line values.
	 *
	 * @param line the line
	 * @param csvTitle the csv title
	 */
	public CSVLineValues(String line, CSVLineTitle csvTitle) {
		String[] tab = line.split(csvTitle.separator);
		values = new double[tab.length];
		/* TODO Handling different Locale */
		if ( csvTitle.strFormatDate.length()> 1 && !csvTitle.strFormatDate.trim().startsWith("timeIn")) {
			try {
				dateInMillis = correctTimeInMillis + (new SimpleDateFormat(csvTitle.strFormatDate, Locale.FRENCH)
						.parse(tab[csvTitle.datesColumn].trim())).getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			switch (csvTitle.strFormatDate.trim()) {
			case "timeInMillis":
				dateInMillis = correctTimeInMillis + Long.parseLong(tab[csvTitle.datesColumn].trim());
				break;
			case "timeInSecond":
				dateInMillis = correctTimeInMillis + Long.parseLong(tab[csvTitle.datesColumn].trim()) * 1000;
				break;
			case "timeInSecondDotMillis":
				dateInMillis = correctTimeInMillis
						+ ((Double) (Double.parseDouble(tab[csvTitle.datesColumn].trim()) * 1000)).longValue();
				break;
			case "timeInSecondCommaMillis":
				dateInMillis = correctTimeInMillis
						+ ((Double) (Double.parseDouble(tab[csvTitle.datesColumn].trim().replace(",", ".")) * 1000))
								.longValue();
				break;
			default:
				/* null case if it could run !!! */
				dateInMillis = correctTimeInMillis + Long.parseLong(tab[csvTitle.datesColumn].trim());
				break;

			}
			dateInMillis = correctTimeInMillis + Long.parseLong(tab[csvTitle.datesColumn].trim());
		}

		if (csvTitle.pivotsColumn >= 0) {
			namePivot = tab[csvTitle.pivotsColumn].trim();
		} else {
			namePivot = "All";
		}
		for (Integer key : csvTitle.hmValuesColums.keySet()) {
			values[key] = Double.parseDouble(tab[key].trim());
		}
	}

}
