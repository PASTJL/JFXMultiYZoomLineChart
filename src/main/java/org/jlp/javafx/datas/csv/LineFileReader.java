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
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

// TODO: Auto-generated Javadoc
/**
 * The Class LineFileReader.
 */
public class LineFileReader {

	/** The csv file. */
	/* for reading flat text file or gz compressed file */
	public CSVFile csvFile;
	/** The lines stream. */
	public Stream<String> linesStream = null;

	/** The file is. */
	InputStream fileIs = null;

	/** The buffered is. */
	BufferedInputStream bufferedIs = null;

	/** The gzip is. */
	GZIPInputStream gzipIs = null;

	/**
	 * Instantiates a new line file reader.
	 *
	 * @param csvFile
	 *            the csv file
	 */
	public LineFileReader(CSVFile csvFile) {
		this.csvFile = csvFile;
		Path pathForStream = Paths.get(csvFile.path);

		lines(pathForStream, csvFile.isGZippedFile);

		// System.out.println("Constructor linesStream Size = " + linesStream.count());

		// TODO Auto-generated constructor stub
	}

	/**
	 * Lines.
	 *
	 * @param path
	 *            the path
	 * @param isGZipped
	 *            the is G zipped
	 * 
	 */
	public void lines(Path path, boolean isGZipped) {

		InputStream fileIs = null;
		BufferedInputStream bufferedIs = null;

		if (isGZipped) {

			try

			{
				fileIs = Files.newInputStream(path);
				bufferedIs = new BufferedInputStream(fileIs, 2 * 1024 * 1024);
				gzipIs = new GZIPInputStream(bufferedIs);

			} catch (IOException e) {
				closeSafely(gzipIs);
				closeSafely(bufferedIs);
				closeSafely(fileIs);
				throw new UncheckedIOException(e);
			}
			System.out.println("Zipped and No  Exception");
			BufferedReader reader = new BufferedReader(new InputStreamReader(gzipIs));
			linesStream = reader.lines().onClose(() -> closeSafely(reader));
		} else {

			try

			{
				fileIs = Files.newInputStream(path);
				bufferedIs = new BufferedInputStream(fileIs, 2 * 1024 * 1024);

			} catch (IOException e) {
				System.out.println(" Aie ! No Zipped and  Exception");

			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(bufferedIs));
			System.out.println("No Zipped and No  Exception");
			linesStream = reader.lines().onClose(() -> closeSafely(reader));
			// System.out.println("nbLines = "+stream.count());
			// System.out.println("linesStream Size = "+ linesStream.count());
		}
		// TODO Auto-generated constructor stub
	}

	/**
	 * Close safely.
	 *
	 * @param closeable
	 *            the closeable
	 */
	private static void closeSafely(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// Ignore
			}
		}
	}
}
