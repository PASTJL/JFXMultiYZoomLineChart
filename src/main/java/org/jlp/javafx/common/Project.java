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
package org.jlp.javafx.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * A list of static parameters used by the current Project
 * </p>
 * <p>
 * Can be configured in files ./config/logFouineur.properties and
 * logFouineurDates.properties
 * </p>
 * 
 */
public class Project {

	/** The name project. */
	public static String nameProject;

	/** The root of the binary installation. */
	public static String root = ".";

	/** The workspace of the csv files. */
	public static String workspace = ".";

	/** The project. */
	public static String project = "";

	/** The scenario. */
	public static String scenario = "";

	/** The date begin project. */
	public static Date dateBeginProject;

	/** The date end project. */
	public static Date dateEndProject;

	/** The percentile. */
	public static double percentile = 90.0;

	/** The is popup muted. */
	public static boolean isPopupMuted = false;

	/**
	 * The nb points in series of LineChart (It needs an aggregate operation from
	 * the csv files).
	 */
	public static int nbPointsLC = 600;

	/** The multi thread. */
	public static double multiThread = 1.0;

	/** The props date format. */
	public static Properties propsDateFormat = new Properties();

	/** The props logfouineur. */
	public static Properties propsLogfouineur = new Properties();

	/**
	 * Load date properties.
	 *
	 * @param rootBin
	 *            the root bin
	 */
	public static void loadDateProperties(String rootBin) {
		try (InputStream in = new FileInputStream(
				rootBin + File.separator + "config" + File.separator + "logFouineurDates.properties");) {
			propsDateFormat.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static {
		// nbPointsLC =
		// Integer.valueOf(propsLogfouineur.getProperty("logFouineur.sampling.nbPoints",
		// "600"));
		// multiThread =
		// Integer.valueOf(propsLogfouineur.getProperty("logFouineur.threads.coeff",
		// "1"));
	}

	/**
	 * Load logfouineur properties.
	 *
	 * @param rootBin
	 *            the root bin
	 */
	public static void loadLogfouineurProperties(String rootBin) {
		try (InputStream in = new FileInputStream(
				rootBin + File.separator + "config" + File.separator + "logFouineur.properties");) {
			propsLogfouineur.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Retrieve date format.
	 *
	 * @param strToParse
	 *            the str to parse
	 * @param rootBin
	 *            the root bin
	 * @return the string
	 */
	public static String retrieveDateFormat(String strToParse, String rootBin) {

		loadDateProperties(rootBin);

		// println("tabDateTimeRegexp=" + tabDateTimeRegexp)
		String regexLongestr = "";
		String formatDate = "";

		Enumeration<Object> kkeys = propsDateFormat.keys();
		String kkeyLongest = "";
		while (kkeys.hasMoreElements()) {
			String myKey = (String) kkeys.nextElement();

			if (!myKey.contains("format.")) {
				// System.out.println("key examined ="+ myKey);
				String regex = propsDateFormat.getProperty(myKey, "");
				Pattern pat = Pattern.compile("^" + regex);
				Matcher matcher = pat.matcher(strToParse);
				if (matcher.find()) {
					if (myKey.length() > kkeyLongest.length()) {
						kkeyLongest = myKey;
					}
				}

			}

		}

		return propsDateFormat.getProperty("format." + kkeyLongest, "");

	}

}
