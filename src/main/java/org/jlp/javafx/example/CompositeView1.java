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
package org.jlp.javafx.example;

import java.util.Locale;
import java.util.function.Function;

import org.jlp.javafx.SimpleLineChartsMultiYAxis;
import org.jlp.javafx.ZoomableLineChartsMultiYAxis;
import org.jlp.javafx.ext.MyLongToDateConverter;
import org.jlp.javafx.ext.MyTypeAxis;
import org.jlp.javafx.richview.CompositePanel;
import org.jlp.javafx.richview.RowTableModel;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class CompositeView1.
 */
public class CompositeView1 extends Application {

	/** The Constant X_DATA_COUNT. */
	public static final int X_DATA_COUNT = 3600;

	/**
	 * Start.
	 *
	 * @param primaryStage the primary stage
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) {
		ZoomableLineChartsMultiYAxis chart = new ZoomableLineChartsMultiYAxis(1.0, false);
		SimpleLineChartsMultiYAxis.isPopupMuted = false;
		SimpleLineChartsMultiYAxis.isPopupFullVisible = false;

		chart.addSeries(prepareSeries("Pivot2", (x) -> (double) 1.5 * x * Math.sqrt(x)), Color.GREEN, "Ko/s");
		chart.addSeries(prepareSeries("Pivot3", (x) -> (double) -4.5 * x), Color.BLUE, "count");
		chart.addSeries(prepareSeries("Pivot4", (x) -> ((double) (2)) * x, 0L), Color.RED, "Mo");
		chart.addSeries(prepareSeries("Pivot5", (x) -> ((double) (x + 100) * (x - 200)), 3600000L), Color.BLACK,
				"Km/s");
		CompositePanel cp = new CompositePanel(chart);
		primaryStage.setTitle("MultipleAxesLineChart");

		Scene scene = new Scene(cp, 1024, 700);
		primaryStage.setScene(scene);
		primaryStage.show();

		primaryStage.setTitle("MultipleAxesLineChart");
		/*
		 * Block that formats date using class MyTypeAxis and a StringConverter
		 * MyLongToDateConverter
		 */
		((NumberAxis) chart.getBaseChart().getXAxis()).setTickLabelFormatter(MyTypeAxis.DATECONVERTER.dateConverter(
				((NumberAxis) chart.getBaseChart().getXAxis()).upperBoundProperty().longValue()
						- ((NumberAxis) chart.getBaseChart().getXAxis()).lowerBoundProperty().longValue(),
				Locale.FRANCE));
		// ((NumberAxis) chart.getBaseChart().getXAxis())
		// .setTickUnit(MyTypeAxis.DATECONVERTER.getTickUnitDefaults()[MyTypeAxis.DATECONVERTER.idxUnit
		// - 1]);
		chart.timeConverter = MyTypeAxis.DATECONVERTER.myConverter;

		chart.setXLabel(((MyLongToDateConverter) chart.timeConverter).getTimeFormat());
		//
		ObservableList<RowTableModel> datas = FXCollections.observableArrayList();
		datas.add(new RowTableModel());
		cp.getTableView().getItems().addAll(datas);

	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Prepare series.
	 *
	 * @param name
	 *            the name of the series must be unique
	 * @param function
	 *            the function
	 * @return the XY chart. series
	 */
	private XYChart.Series<Number, Number> prepareSeries(String name, Function<Long, Double> function) {
		XYChart.Series<Number, Number> series = new XYChart.Series<>();
		series.setName(name);
		for (Long i = 0L; i <= X_DATA_COUNT; i += 100) {
			series.getData().add(new XYChart.Data<>(1000 * i, function.apply(i)));
		}
		return series;
	}

	/**
	 * Prepare series.
	 *
	 * @param name
	 *            the name of the series must be unique
	 * @param function
	 *            the function
	 * @param decalXAxis
	 *            the decal X axis
	 * @return the XY chart. series
	 */
	private XYChart.Series<Number, Number> prepareSeries(String name, Function<Long, Double> function,
			Long decalXAxis) {
		XYChart.Series<Number, Number> series = new XYChart.Series<>();
		series.setName(name);
		for (Long i = 0L; i < X_DATA_COUNT; i += 100) {
			series.getData().add(new XYChart.Data<>(decalXAxis + 1000 * i, function.apply(i)));
		}
		return series;
	}

	/**
	 * Prepare inverse series.
	 *
	 * @param name
	 *            the name of the series must be unique
	 * @param function
	 *            the function
	 * @param decalXAxis
	 *            the decal X axis
	 * @return the XY chart. series
	 */
	private XYChart.Series<Number, Number> prepareInverseSeries(String name, Function<Long, Double> function,
			Long decalXAxis) {
		XYChart.Series<Number, Number> series = new XYChart.Series<>();
		series.setName(name);
		for (Long i = (long) (X_DATA_COUNT - 1); i >= 0; i -= 100) {
			series.getData().add(new XYChart.Data<>(decalXAxis + i, function.apply(i)));
		}
		return series;
	}

}
