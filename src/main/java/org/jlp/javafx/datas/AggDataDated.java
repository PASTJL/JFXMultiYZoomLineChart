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
package org.jlp.javafx.datas;

import org.jlp.javafx.common.Project;
import org.jlp.javafx.common.StrategyPeriod;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

// TODO: Auto-generated Javadoc
/**
 * The Class AggDataDated.
 */
public class AggDataDated {

	/** The x begin period. */
	public long xBeginPeriod; // in millis since 01/01/1970

	/** The period. */
	public long period; // in millis

	/** The count. */
	public int count;

	/** The avg. */
	public double avg;

	/** The percentile. */
	public double perctl;

	/** The min. */
	public double min;

	/** The max. */
	public double max;

	/** The median. */
	public double median;

	/** The sum. */
	public double sum;

	/** The x period. */
	public long xPeriod;

	/** The list values. */
	protected final ObservableList<Double> listValues = FXCollections.observableArrayList();

	/** The lsorted values. */
	private SortedList<Double> lsortedValues = new SortedList<Double>(listValues);

	/**
	 * Instantiates a new agg data dated.
	 *
	 * @param id
	 *            the id
	 * @param period
	 *            the period
	 */
	public AggDataDated(long id, long period) {
		xBeginPeriod = id;
		this.period = period;
		init(this);
	}

	/**
	 * Inits the.
	 *
	 * @param aggDataDated
	 *            the agg data dated
	 */
	private void init(AggDataDated aggDataDated) {
		count = 0;
		avg = 0;
		perctl = 0;
		min = Double.MAX_VALUE;
		max = Double.MIN_VALUE;
		median = 0;
		sum = 0;

	}

	/**
	 * Adds the value.
	 *
	 * @param value
	 *            the value
	 * @return true, if successful
	 */
	public boolean addValue(double value) {
		count++;
		sum += value;

		return listValues.add(value);
	}

	/**
	 * Close.
	 *
	 * @return the agg data dated
	 */
	public AggDataDated close() {
		return close(StrategyPeriod.BEGIN);
	}

	/**
	 * Close.
	 *
	 * @param sp
	 *            the sp
	 * @return the agg data dated
	 */
	public AggDataDated close(StrategyPeriod sp) {
		// sort the list
		lsortedValues = lsortedValues.sorted();
		// System.out.println("count=" + count);
		if (count > 0) {
			// Average
			avg = (double) (sum / count);
			if (count > 1) {
				int idx = (int) (double) ((count / 2.0) + 0.5);
				median = lsortedValues.get(idx - 1);
				idx = (int) ((double) ((count * Project.percentile) / 100));
				perctl = lsortedValues.get(idx - 1);

			} else {
				// only one value
				median = sum;
			}
			min = lsortedValues.get(0);
			max = lsortedValues.get(count - 1);
			switch (sp) {
			case BEGIN:
				xPeriod = this.xBeginPeriod;
				break;
			case END:
				xPeriod = this.xBeginPeriod + period;
				break;
			case MIDDLE:
				xPeriod = this.xBeginPeriod + period / 2;
				break;
			default:
				xPeriod = this.xBeginPeriod;
				break;

			}
			// System.out.println("sum=" + sum);
			// System.out.println("min=" + min);
			// System.out.println("max=" +max);
			// System.out.println("avg=" + avg);
			// System.out.println("median=" + median);
			// System.out.println("percentile=" + percentile);

		}
		return this;
	}

	/**
	 * Merge.
	 *
	 * @param other
	 *            the other
	 * @return the agg data dated
	 */
	public AggDataDated merge(AggDataDated other) {
		if (this.xBeginPeriod == other.xBeginPeriod) {
			// merge is possible
			this.count += other.count;
			this.sum += other.sum;
			this.max = Math.max(this.max, other.max);
			this.min = Math.min(this.min, other.min);
			this.listValues.addAll(other.listValues);

		} else {
			// TODO Exception
		}
		return this;
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		AggDataDated agg = new AggDataDated(10000000, 10000);
		agg.addValue(20.0d);
		agg.addValue(-1.0d);
		agg.addValue(31.0d);
		agg.addValue(100.0d);
		agg.addValue(50.0d);
		agg.close(StrategyPeriod.BEGIN);
	}
}
