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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javafx.util.StringConverter;

// TODO: Auto-generated Javadoc
/**
 * The Class MyLongToDateConverter.
 */
public class MyLongToDateConverter extends StringConverter<Number> {
	
	/** The time format. */
	private String timeFormat = "yyyy-MM-dd";
	
	/**
	 * Gets the time format.
	 *
	 * @return the time format
	 */
	public String getTimeFormat() {
		return timeFormat;
	}

	/** The locale. */
	private Locale locale=Locale.FRANCE;
	
	/** The sdf. */
	private SimpleDateFormat sdf;
	
	/** The cal. */
	private Calendar cal;
	
	/**
	 * From string.
	 *
	 * @param date the date
	 * @return the number
	 */
	/* (non-Javadoc)
	 * @see javafx.util.StringConverter#fromString(java.lang.String)
	 */
	@Override
	public Number fromString(String date) {
		// TODO Auto-generated method stub
		try {
			cal.setTime(sdf.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  cal.getTimeInMillis();
	}

	/**
	 * Instantiates a new my long to date converter.
	 *
	 * @param timeFormat the time format
	 * @param loc the loc
	 */
	public MyLongToDateConverter(String timeFormat,Locale loc) {
		super();
		this.timeFormat = timeFormat;
		this.locale=loc;
		
		sdf=new SimpleDateFormat(timeFormat,locale);
		 cal=Calendar.getInstance();
	}

	/**
	 * To string.
	 *
	 * @param time the time
	 * @return the string
	 */
	/* (non-Javadoc)
	 * @see javafx.util.StringConverter#toString(java.lang.Object)
	 */
	@Override
	public String toString(Number time) {
		
		
		cal.setTimeInMillis(time.longValue());
		
		return sdf.format(cal.getTime());
	}

}
