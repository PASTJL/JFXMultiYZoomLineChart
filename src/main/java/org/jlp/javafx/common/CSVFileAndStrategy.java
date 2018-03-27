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

// TODO: Auto-generated Javadoc
/**
 * The Class CSVFileAndStrategy.
 */
public class CSVFileAndStrategy {
	
	/** The file. */
	public File file;
	
	/** The strategy. */
	public String strategy;

	/**
	 * Instantiates a new CSV file and strategy.
	 *
	 * @param file the file
	 * @param strategy the strategy
	 */
	public CSVFileAndStrategy(File file, String strategy) {
		super();
		this.file = file;
		this.strategy = strategy.toLowerCase();
	}

	/**
	 * Instantiates a new CSV file and strategy.
	 *
	 * @param strFile the str file
	 * @param strategy the strategy
	 */
	public CSVFileAndStrategy(String strFile, String strategy) {
		super();
		this.file = new File(strFile);
		this.strategy = strategy.toLowerCase();
	}

}
