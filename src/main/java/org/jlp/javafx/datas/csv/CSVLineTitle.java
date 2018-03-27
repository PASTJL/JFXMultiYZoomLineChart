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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jlp.javafx.common.Project;

// TODO: Auto-generated Javadoc
/**
 * 
 * <p>
 * A CSVLineTitle must be as below.
 * </p>
 * <p>
 * If your CSV file has no title, you must add a similar line at the top of the
 * file
 * </p>
 * <p>
 * A CSVLineTitle :
 * </p>
 * <p>
 * <b>date;pivots;value1(ms);value2(octets);value3(KBytes/s);</b>
 * </p>
 * <p>
 * Each value must have its unit between parenthesis. the valids units must be
 * declared in file ./config/logfouineurs.properties
 * </p>
 *
 * @author jlp
 *
 */
public class CSVLineTitle {

	/** The dates column. */
	public int datesColumn = 0;

	/** The pivots column. */
	public int pivotsColumn = 1;

	/** The separator. */
	public String separator = ";";

	/**
	 * The hm values colums
	 * <p>
	 * the array contains the name of the column and the unit of the value.
	 */
	public Map<Integer, String[]> hmValuesColums = new HashMap<Integer, String[]>();

	/** a default format date that could be valided by parsing a line of data. */
	public String strFormatDate = null;

	/**
	 * Retrieve column by name.
	 *
	 * @param title
	 *            the title
	 * @param model
	 *            the model
	 * @return the int
	 */
	public int retrieveColumnByName(String title, String model) {
		int ret = -1;
		int i = 0;
		for (String titleColumn : title.split(separator)) {
			if (titleColumn.trim().toLowerCase().startsWith(model)) {
				return i;
			}
			i++;

		}
		return ret;
	}

	/**
	 * Retrieve best separator.
	 *
	 * @param title
	 *            the title
	 * @return the string
	 */
	private static String retrieveBestSeparator(String title) {
		/* try to find the better separator loading logfouineur.properties */
		/*
		 * csvFile.separatorOfSeparators=~ csvFile.separators=;~\t+~\|~ ~
		 */
		String csvLineSeparatorOfSeparators = Project.propsLogfouineur.getProperty("csvLine.separatorOfSeparators",
				"~");
		String[] csvLineSeparators = Project.propsLogfouineur.getProperty("csvLine.separators", ";~\\t+~\\|~ ~")
				.split(csvLineSeparatorOfSeparators);
		int nb = 0;
		String tmpSeparator = "";
		for (String sep : csvLineSeparators) {
			int nbTmp = (title.length() - title.replace(sep, "").length()) / sep.length();
			if (nbTmp > nb) {
				tmpSeparator = sep;
				nb = nbTmp;
			}
		}
		return tmpSeparator;
	}

	/**
	 * Instantiates a new CSV line title.
	 *
	 * @param title
	 *            the title
	 */
	public CSVLineTitle(String title) {

		this(title, retrieveBestSeparator(title));

	}

	/**
	 * Instantiates a new CSV line title.
	 *
	 * @param title
	 *            the title
	 * @param separator
	 *            the separator
	 *
	 *
	 */
	public CSVLineTitle(String title, String separator) {
		this.separator = separator;
		datesColumn = retrieveColumnByName(title.toLowerCase(), "date");
		if (datesColumn == -1) {
			// try with time
			datesColumn = retrieveColumnByName(title.toLowerCase(), "time");
		}
		if (datesColumn == -1) {
			System.out.println("The Title :" + title + " has an incorrect title line or no title line at ALL");

		}
		pivotsColumn = retrieveColumnByName(title, "pivot");
		String[] tab = title.split(separator);

		for (int i = 0; i < tab.length; i++) {
			if (i != datesColumn && i != pivotsColumn) {
				// System.out.println("Parsing column tab["+i+"] = |"+ tab[i].trim()+"|" );
				if (tab[i].trim().matches("[\\w+\\s]+\\([^\\)]+\\)")) {
					// Correct column

					hmValuesColums.put(i,
							new String[] { tab[i].split("\\(")[0], tab[i].split("\\(")[1].split("\\)")[0] });
				} else {
					// incorrect column . Create a unit "units"
					hmValuesColums.put(i, new String[] { tab[i].trim(), "units" });
					System.out.println("The column tab[" + i + "] = |" + tab[i]
							+ "|   is not correctly libelled a default unit is created");
				}
			}
		}

	}

	/**
	 * Valid title with row data.
	 *
	 * @param rowData
	 *            the row data
	 * @return true, if successful
	 */
	public boolean validTitleWithRowData(String rowData) {
		String[] tab = rowData.split(";");
		boolean ret = true;
		for (int i = 0; i < tab.length; i++) {
			if (i == datesColumn) {
				// retrouver le modele
				// System.out.println("Try to retrieve model for : |"+tab[i]+"|"+ " ; root="+
				// Project.root);
				strFormatDate = Project.retrieveDateFormat(tab[i], Project.root);
			} else if (i == pivotsColumn) {
				// on verifie must contains letters
				String strPattern = "^[0-9@_\\-\\)\\(\\.]*[a-zA-Z]{2,}.*";
				Pattern pat = Pattern.compile(strPattern);
				Matcher matcher = pat.matcher(tab[i]);
				if (!matcher.find()) {
					// on supprime la colonne
					ret = false;
					System.out.println("la colonne Pivots :" + tab[i] + " is not a Pivot");
					pivotsColumn = -1;

				}

			} else {
				// Column Value
				if (tab[i].equals("")) {
					System.out.println("Colonne : " + i + " Value vide on la garde :" + tab[i] + " is an empty Value");
				} else {
					String strPatternLong = "^(\\+|\\-)?\\d+$";
					String strPatternDoubleFloat = "^(\\+|\\-)?\\d+\\.\\d+$";
					String strPatternScientific = "^(\\+|\\-)?\\d+\\.\\d+E(\\+|\\-)?\\d+$";
					Pattern pat = Pattern.compile(
							"(" + strPatternLong + ")|(" + strPatternDoubleFloat + ")|(" + strPatternScientific + ")");
					Matcher matcher = pat.matcher(tab[i].trim());
					if (!matcher.find()) {
						// on supprime la colonne
						ret = false;
						System.out.println("Suppression colonne Value :" + tab[i] + " is not a Value");
						hmValuesColums.remove(i);
					}
				}
			}
		}
		return ret;
	}
}
