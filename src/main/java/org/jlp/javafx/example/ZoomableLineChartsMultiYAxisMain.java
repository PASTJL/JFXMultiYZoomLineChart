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

import java.net.URL;
import java.util.function.Function;

import org.jlp.javafx.SimpleLineChartsMultiYAxis;
import org.jlp.javafx.ZoomableLineChartsMultiYAxis;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class ZoomableLineChartsMultiYAxisMainXDate.
 */
public class ZoomableLineChartsMultiYAxisMain extends Application {

	/** The Constant X_DATA_COUNT. */
	public static final int X_DATA_COUNT = 3600;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		NumberAxis xAxis = new NumberAxis(0, X_DATA_COUNT, 200);
		NumberAxis yAxis = new NumberAxis();

		LineChart<Number, Number> baseChart = new LineChart<Number, Number>(xAxis, yAxis);

		URL stylesheet = ZoomableLineChartsMultiYAxisMain.class.getResource("/org/jlp/javafx/style.css");
		if (null == stylesheet) {
			System.out.println("Null Pointer to style.css");
			System.out.println("jarLocation" + Scene.class.getProtectionDomain().getCodeSource().getLocation());
		} else {
			System.out.println("Yes !! style.css found");
			baseChart.getStylesheets().add(stylesheet.toString());
		}
		// The baseChart must be empty !
		ZoomableLineChartsMultiYAxis chart = new ZoomableLineChartsMultiYAxis(baseChart, Color.RED, 1.0, false);

		/*
		 * To configure the verbosity of the popup Window information isPopupMuted =
		 * true; no information isPopupFullVisible is inoperative isPopupMuted =
		 * false;isPopupFullVisible=false; Only the information of the Series near the
		 * cross cursor are visible isPopupMuted = false;isPopupFullVisible=true; all
		 * series information are visible
		 */
		SimpleLineChartsMultiYAxis.isPopupMuted = false;
		SimpleLineChartsMultiYAxis.isPopupFullVisible = false;

		// important : Don't use the way chart.baseChart.getData().add(series), use
		// instead
		// chart.addSeries(Series, <Color>, <YUnit>), the baseChart is automatically
		// filled
		// important : the name of the series must be unique ( Pivot2 Pivot3 ...)
		chart.addSeries(prepareSeries("Pivot2", (x) -> (double) 1.5 * x * Math.sqrt(x)), Color.GREEN, "Ko/s");
		chart.addSeries(prepareSeries("Pivot3", (x) -> (double) -4.5 * x), Color.BLUE, "count");
		chart.addSeries(prepareSeries("Pivot3", (x) -> ((double) (2)) * x, 0L), Color.GREEN, "Mo");
		chart.addSeries(prepareSeries("Pivot5", (x) -> ((double) (x + 100) * (x - 200)), 3600000L), Color.BLACK,
				"Km/s");

		primaryStage.setTitle("MultipleAxesLineChart");

		Scene scene = new Scene(chart, 1024, 600);

		primaryStage.setScene(scene);
		primaryStage.show();
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
		for (Long i = 0L; i < X_DATA_COUNT; i += 100) {
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

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
