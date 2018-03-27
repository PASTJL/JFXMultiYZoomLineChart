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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.jlp.javafx.datas.AggDataDated;

import javafx.collections.FXCollections;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

// TODO: Auto-generated Javadoc
/**
 * The Class TaskAgg.
 */
public class TaskPrepareSeries implements Callable<XYChart.Series<Number, Number>> {

	/** The name serie. */
	public String nameSerie;

	/** The entry set. */
	Set<Entry<Long, AggDataDated>> entrySet;

	/** The field. */
	String field;

	/**
	 * Instantiates a new task prepare series.
	 *
	 * @param name the name
	 * @param entrySet the entry set
	 */
	public TaskPrepareSeries(String name, Set<Entry<Long, AggDataDated>> entrySet) {
		this(name, entrySet, "avg");
	}

	/**
	 * Instantiates a new task prepare series.
	 *
	 * @param name the name
	 * @param entrySet the entry set
	 * @param field the field
	 */
	public TaskPrepareSeries(String name, Set<Entry<Long, AggDataDated>> entrySet, String field) {
		this.entrySet = entrySet;
		this.nameSerie = name;
		this.field = field;

	}

	/**
	 * Call.
	 *
	 * @return the XY chart. series
	 * @throws Exception the exception
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public XYChart.Series<Number, Number> call() throws Exception {
		XYChart.Series<Number, Number> series = new XYChart.Series<>();
		series.setName(nameSerie);
		// ObservableList<Data<Number, Number>>
		List<Data<Number, Number>> list = new ArrayList<Data<Number, Number>>();
		Field whatField = AggDataDated.class.getField(field);
		for (Entry<Long, AggDataDated> entryBis : entrySet) {
			// AggDataDated.class.getField(field).getDouble(entryBis.getValue());
			// series.getData().add(new Data<Number, Number>(entryBis.getKey(),
			// whatField.getDouble(entryBis.getValue())));
			list.add(new Data<Number, Number>(entryBis.getKey(), whatField.getDouble(entryBis.getValue())));
		}
		series.setData(FXCollections.observableArrayList(list));
		return series;
	}

}
