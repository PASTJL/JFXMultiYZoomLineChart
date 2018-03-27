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

// TODO: Auto-generated Javadoc
/**
 * The Class RowTableModel.
 */
public class RowTableModel {

	/** The shown. */
	private Boolean shown;

	/**
	 * Gets the shown.
	 *
	 * @return the shown
	 */
	public Boolean isShown() {
		return shown;
	}

	/**
	 * Sets the shown.
	 *
	 * @param bshown the new shown
	 */
	public void setShown(Boolean bshown) {
		shown = bshown;
	}

	/** The selected. */
	private Boolean selected;

	/**
	 * Gets the selected.
	 *
	 * @return the selected
	 */
	public Boolean isSelected() {
		return selected;
	}

	/**
	 * Sets the selected.
	 *
	 * @param bselected the new selected
	 */
	public void setSelected(Boolean bselected) {
		this.selected = bselected;
	}

	/** The Median. */
	private Double median;

	/**
	 * Gets the median.
	 *
	 * @return the median
	 */
	public Double getMedian() {
		return median;
	}

	/**
	 * Sets the median.
	 *
	 * @param dmedian the new median
	 */
	public void setMedian(Double dmedian) {
		this.median = dmedian;
	}

	/** The Percentile. */
	private Double perCtl;

	/**
	 * Gets the per cile.
	 *
	 * @return the per cile
	 */
	public Double getPerCtl() {
		return perCtl;
	}

	/**
	 * Sets the percent cile.
	 *
	 * @param dperCtl the new per ctl
	 */
	public void setPerCtl(Double dperCtl) {
		this.perCtl = dperCtl;
	}

	/** The str color. */
	private String strColor; // The Column Color format #FFFFFFFF; => RGBA

	/**
	 * Gets the str color.
	 *
	 * @return the str color
	 */
	public String getStrColor() {
		return strColor;
	}

	/**
	 * Sets the str color.
	 *
	 * @param sstrColor the new str color
	 */
	public void setStrColor(String sstrColor) {
		this.strColor = sstrColor;
	}

	// private SimpleStringProperty strColorProperty() {
	// if (strColor == null) {
	// strColor = new SimpleStringProperty(this, "Color");
	// }
	// return strColor;
	//
	// }

	/** The strategy. */
	private String strategy;

	/**
	 * Gets the strategy.
	 *
	 * @return the strategy
	 */
	public String getStrategy() {
		return strategy;
	}

	/**
	 * Sets the strategy.
	 *
	 * @param sstrategy the new strategy
	 */
	public void setStrategy(String sstrategy) {
		this.strategy = sstrategy;
	}

	/** The unit. */
	private String unit;

	/**
	 * Gets the unit.
	 *
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * Sets the unit.
	 *
	 * @param sunit the new unit
	 */
	public void setUnit(String sunit) {
		this.unit = sunit;
	}

	/** The translation. */
	private Double translation;

	/**
	 * Gets the translation.
	 *
	 * @return the translation
	 */
	public Double getTranslation() {
		return translation;
	}

	/**
	 * Sets the translation.
	 *
	 * @param dtranslation the new translation
	 */
	public void setTranslation(Double dtranslation) {
		this.translation = dtranslation;
	}

	/** The source. */
	private String source;

	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Sets the source.
	 *
	 * @param ssource the new source
	 */
	public void setSource(String ssource) {
		this.source = ssource;
	}

	/** The name serie. */
	private String nameSerie;

	/**
	 * Gets the name serie.
	 *
	 * @return the name serie
	 */
	public String getNameSerie() {
		return nameSerie;
	}

	/**
	 * Sets the name serie.
	 *
	 * @param snameSerie the new name serie
	 */
	public void setNameSerie(String snameSerie) {
		this.nameSerie = snameSerie;
	}

	/** The avg. */
	private Double avg;

	/**
	 * Gets the avg.
	 *
	 * @return the avg
	 */
	public Double getAvg() {
		return avg;
	}

	/**
	 * Sets the avg.
	 *
	 * @param davg the new avg
	 */
	public void setAvg(Double davg) {
		this.avg = davg;
	}

	// private SimpleDoubleProperty avgProperty() {
	// if (avg == null) {
	// avg = new SimpleDoubleProperty(this, "avg", 0.0);
	// }
	// return avg;
	//
	// }

	/** The avg pond. */
	private Double avgPond;

	/**
	 * Gets the avg pond.
	 *
	 * @return the avg pond
	 */
	public Double getAvgPond() {
		return avgPond;
	}

	/**
	 * Sets the avg pond.
	 *
	 * @param davgPond the new avg pond
	 */
	public void setAvgPond(Double davgPond) {
		this.avgPond = davgPond;

	}

	/** The min. */
	private Double min;

	/**
	 * Gets the min.
	 *
	 * @return the min
	 */
	public Double getMin() {
		return min;
	}

	/**
	 * Sets the min.
	 *
	 * @param dmin the new min
	 */
	public void setMin(Double dmin) {
		this.min = dmin;
	}

	/** The max. */
	private Double max;

	/**
	 * Gets the max.
	 *
	 * @return the max
	 */
	public Double getMax() {
		return max;
	}

	/**
	 * Sets the max.
	 *
	 * @param dmax the new max
	 */
	public void setMax(Double dmax) {
		this.max = dmax;
		;
	}

	/** The stdv. */
	private Double stdv; /* Standard deviation ; ecart type */

	/**
	 * Gets the stdv.
	 *
	 * @return the stdv
	 */
	public Double getStdv() {
		return stdv;
	}

	/**
	 * Sets the stdv.
	 *
	 * @param dstdv the new stdv
	 */
	public void setStdv(Double dstdv) {
		this.stdv = dstdv;
	}

	/** The irslope. */
	private String irslope; /* linear regression */

	/**
	 * Gets the irslope.
	 *
	 * @return the irslope
	 */
	public String getIrslope() {
		return irslope;
	}

	/**
	 * Sets the irslope.
	 *
	 * @param dirslope the new irslope
	 */
	public void setIrslope(String dirslope) {
		this.irslope = dirslope;
		;
	}

	/** The count pts. */
	private Integer countPts;

	/**
	 * Gets the count pts.
	 *
	 * @return the count pts
	 */
	public Integer getCountPts() {
		return countPts;
	}

	/**
	 * Sets the count pts.
	 *
	 * @param icountPts the new count pts
	 */
	public void setCountPts(Integer icountPts) {
		this.countPts = icountPts;
		;
	}

	/** The count val. */
	private Integer countVal;

	/**
	 * Gets the count val.
	 *
	 * @return the count val
	 */
	public Integer getCountVal() {
		return countVal;
	}

	/**
	 * Sets the count val.
	 *
	 * @param icountVal the new count val
	 */
	public void setCountVal(Integer icountVal) {
		this.countVal = icountVal;
		;
	}

	/** The sum. */
	private Double sum; /* sometimes has a signification but often it means nothing */

	/**
	 * Gets the sum.
	 *
	 * @return the sum
	 */
	public Double getSum() {
		return sum;
	}

	/**
	 * Sets the sum.
	 *
	 * @param dsum the new sum
	 */
	public void setSum(Double dsum) {
		this.sum = dsum;
		;
	}

	/** The sum total. */
	private Double sumTotal; /* including the nb of Vals per concatenation; same remak as just above */

	/**
	 * Gets the sum total.
	 *
	 * @return the sum total
	 */
	public Double getSumTotal() {
		return this.sumTotal;
	}

	/**
	 * Sets the sum total.
	 *
	 * @param dsumTotal the new sum total
	 */
	public void setSumTotal(Double dsumTotal) {
		this.sumTotal = dsumTotal;
		;
	}

	/**
	 * Instantiates a new row table model.
	 */
	public RowTableModel() {

		this(true, false, "", "AVG", "unit", 0.0, "pathToFile", "nameOfSerie", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				"0.0 unit/ms", 0, 0, 0.0, 0.0);
	}

	/**
	 * Instantiates a new row table model.
	 *
	 * @param shown the shown
	 * @param selected the selected
	 * @param strColor the str color
	 * @param strategy the strategy
	 * @param unit the unit
	 * @param translation the translation
	 * @param source the source
	 * @param nameSerie the name serie
	 * @param avg the avg
	 * @param avgPond the avg pond
	 * @param min the min
	 * @param max the max
	 * @param median the median
	 * @param perCtl the per ctl
	 * @param stdv the stdv
	 * @param irslope the irslope
	 * @param countPts the count pts
	 * @param countVal the count val
	 * @param sum the sum
	 * @param sumTotal the sum total
	 */
	public RowTableModel(Boolean shown, Boolean selected, String strColor, String strategy, String unit,
			Double translation, String source, String nameSerie, Double avg, Double avgPond, Double min, Double max,
			Double median, Double perCtl, Double stdv, String irslope, Integer countPts, Integer countVal, Double sum,
			Double sumTotal) {

		this.shown = shown;
		this.selected = selected;
		this.strColor = strColor;
		this.strategy = strategy;
		this.unit = unit;
		this.translation = translation;
		this.source = source;
		this.nameSerie = nameSerie;
		this.avg = avg;
		this.avgPond = avgPond;
		this.min = min;
		this.max = max;
		this.median = median;
		this.perCtl = perCtl;
		this.stdv = stdv;
		this.irslope = irslope;
		this.countPts = countPts;
		this.countVal = countVal;
		this.sum = sum;
		this.sumTotal = sumTotal;
	}

}
