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
package org.jlp.javafx.ext;

import java.util.Locale;

import javafx.util.StringConverter;

// TODO: Auto-generated Javadoc
/**
 * The Enum MyTypeAxis.
 *
 * @author jlp
 * 
 *         Using a Singleton with an enum structure. the XAxis with date must be
 *         shared with all LineChart and Chart of StackPane DateAxis to treat
 *         XAxis with date and time. Instead of extending the virtual class
 *         ValueAxis, I use an instance of NumberAxis in D. In java the Time is
 *         a Long, dating with the number of milliseconds from 01/01/1970. Using
 *         setTickLabelFormatter​(StringConverter value) to format time in
 *         millis to more comprehensible Human date with StringConverter. Format
 *         type date yyyy-MM-dd , Format time : HH-mm-ss.SSS ( for the most
 *         precise)
 */
public enum MyTypeAxis {

	/** The dateconverter. */
	DATECONVERTER, /** The othertypeaxis. */
 OTHERTYPEAXIS; /** The idx unit. */
 // Methods must be add to treat the new Converter;
	public int idxUnit = -1;
	
	/** The my converter. */
	public StringConverter<Number> myConverter = null;

	// public void setValue(NumberAxis value) {
	//
	// this.xAxis = value;
	//
	// }
	/**
	 * We use these for auto ranging to pick a user friendly tick unit. (must be
	 * increasingly bigger)
	 */
	private final double[] TICK_UNIT_DEFAULTS = { 1000, // 1s
			60000, // 1 mn
			3600000, // 1 Hour
			1440000, // 4 heures
			21600000, // 6 heures
			43200000, // 12 heures
			86400000, // 1 day
			172800000, // 2 days
			259200000, // 3 days
			345600000, // 4 days
			432000000, // 5 days
			518400000, // 6 days
			604800000, // 7 days
			691200000, // 8 days
			777600000, // 9 days
			864000000, // 10 days
			216000000E1, // 15 days
			388800000E1, // 20 days
			604800000E1, // 25 days
			872640000E1, // 31 days ~ 1 month
			1226880000E1, // 41 days
			1667520000E1, // 51 days
			2203200000E1, // 62 days ~ 2 months
			2868480000E1, // 77 days
			3672000000E1, // 93 days ~ 3 months
			4605120000E1, // 108 days
			5676480000E1, // 124 days ~ 4 months
			6877440000E1, // 139 days
			8216640000E1, // 155 days ~ 5 months
			9685440000E1, // 170 days
			1129248000E2, // 186 days ~ 6 months
			1445472000E2 // 366 days ~ 1 year
	};

	/**
	 * Gets the tick unit defaults.
	 *
	 * @return the tick unit defaults
	 */
	public final double[] getTickUnitDefaults() {
		return TICK_UNIT_DEFAULTS;
	}

	/**  These are matching date formatter strings. */
	private final String[] TICK_UNIT_FORMATTER_DEFAULTS = {

			"HH-mm-ss.SSS", // 1s
			"HH-mm-ss.SSS", // 1 mn
			"HH-mm-ss", // 1 Hour
			"HH-mm-ss", // 4 heures
			"HH-mm-ss", // 6 heures
			"HH-mm-ss", // 12 heures
			"yyyy-MM-dd HH'h'", // 1 day
			"yyyy-MM-dd HH'h'", // 2 das
			"yyyy-MM-dd HH'h'", // 3 days
			"yyyy-MM-dd HH'h'", // 4 days
			"yyyy-MM-dd HH'h'", // 5 days
			"yyyy-MM-dd", // 6 days
			"yyyy-MM-dd", // 7 days
			"yyyy-MM-dd", // 8 days
			"yyyy-MM-dd", // 9 days
			"yyyy-MM-dd", // 10 days
			"yyyy-MM-dd", // 15 days
			"yyyy-MM-dd", // 20 days
			"yyyy-MM-dd", // 25 days
			"yyyy-MM", // 31 days ~ 1 month
			"yyyy-MM", // 41 days
			"yyyy-MM", // 51 days
			"yyyy-MM", // 62 days ~ 2 months
			"yyyy-MM", // 77 days
			"yyyy-MM", // 93 days ~ 3 months
			"yyyy-MM", // 108 days
			"yyyy-MM", // 124 days ~ 4 months
			"yyyy-MM", // 139 days
			"yyyy-MM", // 155 days ~ 5 months
			"yyyy-MM", // 170 days
			"yyyy-MM", // 186 days ~ 6 months
			"yyyy" // 366 days ~ 1 year
	};

	/**
	 * Date converter.
	 *
	 * @param interval the interval
	 * @param loc the loc
	 * @return the string converter
	 */
	public StringConverter<Number> dateConverter(Long interval, Locale loc) {

		int idx = 0;
		double cbInt = (double) interval;
		for (double v : TICK_UNIT_DEFAULTS) {
			System.out.println("v=" + (double) v + " ; interval=" + interval);
			if ((double) v >= cbInt) {

				myConverter = new MyLongToDateConverter(TICK_UNIT_FORMATTER_DEFAULTS[idx], loc);
				idxUnit = idx;
				break;
			}
			idx++;
		}
		System.out.println("TICK_UNIT_FORMATTER_DEFAULTS[" + idx + "] =" + TICK_UNIT_FORMATTER_DEFAULTS[idx]);
		return myConverter;
	}

	/**
	 * Best lower bound.
	 *
	 * @param minorTime the minor time
	 * @param formatTime the format time
	 * @return the long
	 */
	public long bestLowerBound(long minorTime, String formatTime) {
		long retLong = minorTime;
		switch (formatTime) {
		case "HH-mm-ss.SSS":
			retLong = minorTime - 2 * (minorTime % 60000l);
			break;
		case "HH-mm-ss":
			retLong = minorTime - 2 * (minorTime % 60000l);
			break;

		case "yyyy-MM-dd\nHH'h'":
			retLong = minorTime - 2 * (minorTime % 3600000l);
			break;
		case "yyyy-MM-dd":
			retLong = minorTime - 2 * (minorTime % 86400000l);
			break;

		case "yyyy-MM":
			retLong = minorTime - 2 * (minorTime % 2592000000l);
			break;
		case "yyyy":
			retLong = minorTime - 2 * (minorTime % 31622400000l);
			break;

		default:

			break;

		}
		return retLong;

	}

	/**
	 * Best upper bound.
	 *
	 * @param majorTime the major time
	 * @param formatTime the format time
	 * @return the long
	 */
	public long bestUpperBound(long majorTime, String formatTime) {

		long retLong = majorTime;
		switch (formatTime) {
		case "HH-mm-ss.SSS":
			retLong = (majorTime - (majorTime % 60000l)) + 2 * 60000l;
			break;
		case "HH-mm-ss":
			retLong = (majorTime - (majorTime % 60000l)) + 2 * 60000l;
			break;

		case "yyyy-MM-dd\nHH'h'":
			retLong = (majorTime - (majorTime % 3600000l)) + 2 * 3600000l;
			break;
		case "yyyy-MM-dd":
			retLong = (majorTime - (majorTime % 86400000l)) + 2 * 86400000l;
			break;

		case "yyyy-MM":
			retLong = (majorTime - (majorTime % 2592000000l)) + 2 * 2592000000l;
			break;
		case "yyyy":
			retLong = (majorTime - (majorTime % 31622400000l)) + 2 * 31622400000l;
			break;

		default:

			break;

		}
		return retLong;

	}

}
