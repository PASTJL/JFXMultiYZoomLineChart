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
package org.jlp.javafx.richview;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jlp.javafx.common.Project;
import org.jlp.javafx.datas.AggDataDated;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.paint.Color;

// TODO: Auto-generated Javadoc
/**
 * The Class FillTableView.
 */
public class FillTableView {
	
	/** The list data. */
	ObservableList<RowTableModel> listData = FXCollections.observableArrayList();

	/**
	 * Instantiates a new fill table view.
	 */
	public FillTableView() {
		super();

	}

	/**
	 * Fill.
	 */
	public void fill() {
		CompositePanel.tableView.setEditable(true);
		CompositePanel.tableView.getItems().clear();
		listData.clear();

		for (Entry<String, Map<Long, AggDataDated>> entry : CompositePanel.hmOfHmDataDated.entrySet()) {
			addRow(entry);
		}
		CompositePanel.tableView.setItems(listData);
		CompositePanel.tableView.refresh();

	}

	/**
	 * Adds the row.
	 *
	 * @param entry the entry
	 */
	private void addRow(Entry<String, Map<Long, AggDataDated>> entry) {

		RowTableModel row = new RowTableModel();
		if (null == CompositePanel.chart.hmVisibleSeries.get(entry.getKey()))
			return;

		row.setShown(CompositePanel.chart.hmVisibleSeries.get(entry.getKey()).isVisible);

		row.setSelected(CompositePanel.chart.hmVisibleSeries.get(entry.getKey()).isSelected);
		Color color = CompositePanel.chart.chartColorMap.get(entry.getKey());
		row.setStrColor(String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255)));
		row.setNameSerie(entry.getKey());
		row.setSource(CompositePanel.hmOfUnitSource.get(entry.getKey())[1]);
		row.setUnit(CompositePanel.hmOfUnitSource.get(entry.getKey())[0]);
		row.setStrategy(CompositePanel.hmCSVPathAndStrategy
				.get(Integer.parseInt(entry.getKey().substring(entry.getKey().lastIndexOf("-") + 1)))[1]);
		CompositeResult cpRes = truncate(entry.getValue(), row.getStrategy(), row.getUnit());
		// row.setCountPts(truncMap.size());
		row.setCountPts(cpRes.countPts);
		row.setCountVal(cpRes.countVals);
		row.setSum(cpRes.sum);
		row.setSumTotal(cpRes.sumTotal);
		row.setAvg(cpRes.avg);
		row.setAvgPond(cpRes.avgPond);
		row.setMin(cpRes.min);
		row.setMax(cpRes.max);
		row.setPerCtl(cpRes.percentile);
		row.setMedian(cpRes.median);
		row.setStdv(cpRes.stdv);
		row.setIrslope(cpRes.strLrslope);
		listData.add(row);

	}

	/**
	 * Truncate.
	 *
	 * @param value the value
	 * @param strategie the strategie
	 * @param unit the unit
	 * @return the composite result
	 */
	private CompositeResult truncate(Map<Long, AggDataDated> value, String strategie, String unit) {
		CompositeResult retcR = new CompositeResult();
		double numerateurAvg = 0;
		double numerateurAvgPond = 0.0;
		ObservableList<Double> olst = FXCollections.observableArrayList();

		Long minX = Long.MAX_VALUE;
		Long maxX = Long.MIN_VALUE;
		double sumXForAvg = 0;
		for (Entry<Long, AggDataDated> entry : value.entrySet()) {
			if (entry.getKey() >= CompositePanel.begDateZoom.getTime()
					&& entry.getKey() <= CompositePanel.endDateZoom.getTime()) {
				sumXForAvg += entry.getKey();
				if (minX > entry.getKey())
					minX = entry.getKey();
				if (maxX < entry.getKey())
					maxX = entry.getKey();
				retcR.retHm.put(entry.getKey(), entry.getValue());
				retcR.countVals++;
				retcR.countPts += entry.getValue().count;
				if (retcR.max < entry.getValue().max)
					retcR.max = entry.getValue().max;
				if (retcR.min > entry.getValue().min)
					retcR.min = entry.getValue().min;
				retcR.sum += entry.getValue().sum;
				retcR.sumTotal += entry.getValue().sum * entry.getValue().count;
				try {
					if (!strategie.toLowerCase().equals("count")) {
						// System.out.println("FillTableView strategie=" + strategie);
						retcR.xp2 += Math.pow(
								AggDataDated.class.getField(strategie.toLowerCase()).getDouble(entry.getValue()), 2);
						numerateurAvg += AggDataDated.class.getField(strategie.toLowerCase())
								.getDouble(entry.getValue());
						numerateurAvgPond += AggDataDated.class.getField(strategie.toLowerCase())
								.getDouble(entry.getValue()) * entry.getValue().count;
						olst.add(AggDataDated.class.getField(strategie.toLowerCase()).getDouble(entry.getValue()));
					} else {
						retcR.xp2 += Math.pow(entry.getValue().count, 2);
						numerateurAvg += entry.getValue().count;
						numerateurAvgPond += entry.getValue().count * entry.getValue().count;
						olst.add((double) entry.getValue().count);
					}

				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		// compute Avg AvgPond ...
		if (retcR.countVals > 0) {

			retcR.avg = numerateurAvg / retcR.countVals;
			retcR.avgPond = numerateurAvgPond / retcR.countPts;
			double avgX2 = retcR.xp2 / retcR.countVals;
			retcR.stdv = Math.sqrt(avgX2 - Math.pow(retcR.avg, 2));

			SortedList<Double> sl = new SortedList<Double>(olst, new ComparatorDouble());
			int idx = (int) Math.round((((sl.size() - 1) * Project.percentile) / 100));
			double eX = sumXForAvg / retcR.countVals;

			sl = sl.sorted();

			retcR.percentile = (double) sl.get(idx);
			int idx2 = (int) Math.round((sl.size() - 1) / 2.0);

			retcR.median = (double) sl.get(idx2);
			Double sumX = 0d;
			Double sumY = 0d;
			for (Entry<Long, AggDataDated> entry : value.entrySet()) {
				try {
					if (!strategie.toLowerCase().equals("count")) {
						sumY += (entry.getKey() - eX)
								* (AggDataDated.class.getField(strategie.toLowerCase()).getDouble(entry.getValue())
										- retcR.avg);
					} else {
						sumY += (entry.getKey() - eX) * (entry.getValue().count - retcR.avg);
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sumX += Math.pow((entry.getKey() - eX), 2);
			}
			retcR.irslope = sumY / sumX;

			retcR.strLrslope = normalize(retcR.irslope, unit);

		}

		return retcR;
	}

	/**
	 * The Class ComparatorDouble.
	 */
	private class ComparatorDouble implements Comparator<Double> {

		/**
		 * Compare.
		 *
		 * @param arg0 the arg 0
		 * @param arg1 the arg 1
		 * @return the int
		 */
		@Override
		public int compare(Double arg0, Double arg1) {
			return arg0.compareTo(arg1);
		}

	}

	/**
	 * Normalize.
	 *
	 * @param irslope the irslope
	 * @param unit the unit
	 * @return the string
	 */
	private String normalize(double irslope, String unit) {

		String ret = "";
		Double minLrSlope = Double
				.parseDouble(Project.propsLogfouineur.getProperty("logFouineur.lrslope.valueMax", "5000"));
		Double[] multiplicateurs = { 1d, 1000d, 60d, 60d, 24d, 30d, 12d };
		String[] timeunits = { "ms", "s", "mn", "H", "d", "M", "Y" };

		int i = 0;
		while (Math.abs(irslope) < minLrSlope && i < 7) {
			irslope = irslope * multiplicateurs[i];
			i = i + 1;
		}
		if (Math.abs(irslope) < 1000) {
			ret = String.format("%6.2f", irslope);
		} else {
			ret = String.format("%4.2E", irslope);
		}
		if (i > 0) {
			return ret + " " + unit + "/" + timeunits[i - 1];
		} else
			return ret + " " + unit + "/" + timeunits[0];
	}

	/**
	 * The Class CompositeResult.
	 */
	class CompositeResult {
		
		/** The ret hm. */
		public Map<Long, AggDataDated> retHm = new HashMap<>();
		
		/** The count pts. */
		public int countPts = 0;
		
		/** The count vals. */
		public int countVals = 0;
		
		/** The max. */
		public double max = Double.MIN_VALUE;
		
		/** The min. */
		public double min = Double.MAX_VALUE;
		
		/** The sum. */
		public double sum = 0.0;
		
		/** The sum total. */
		public double sumTotal = 0.0;
		
		/** The avg. */
		public double avg = 0.0;
		
		/** The avg pond. */
		public double avgPond = 0.0;
		
		/** The irslope. */
		public double irslope = 0.0;
		
		/** The percentile. */
		public double percentile = 0.0;
		
		/** The median. */
		public double median = 0.0;
		
		/** The stdv. */
		public double stdv = 0.0;
		
		/** The xp 2. */
		public double xp2 = 0.0; // some des carres
		
		/** The str lrslope. */
		public String strLrslope = "";

	}
}
