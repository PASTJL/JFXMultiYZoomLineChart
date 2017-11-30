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

import javafx.util.StringConverter;

// TODO: Auto-generated Javadoc
/**
 * The Class MyLongToDateConverter.
 */
public class MyYaxisDoubleFormatter extends StringConverter<Number> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.util.StringConverter#fromString(java.lang.String)
	 */
	@Override
	public Number fromString(String aNumber) {
		// TODO Auto-generated method stub
		// never use for this time
		return null;
	}

	/* (non-Javadoc)
	 * @see javafx.util.StringConverter#toString(java.lang.Object)
	 */
	@Override
	public String toString(Number aNumber) {

		double value = aNumber.doubleValue();
		if (Math.abs(value) < 1000000) {
			return String.format("%6.2f", aNumber);
		} else {
			return String.format("%4.2E", aNumber);
		}

	}

}
