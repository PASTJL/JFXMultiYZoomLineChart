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
import java.util.Locale;
import java.util.function.Function;

import org.jlp.javafx.SimpleLineChartsMultiYAxis;
import org.jlp.javafx.ext.MyLongToDateConverter;
import org.jlp.javafx.ext.MyTypeAxis;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class SimpleLineChartsMultiYAxisMainXDate.
 */
public class SimpleLineChartsMultiYAxisMainXDate3 extends Application {

	/** The Constant X_DATA_COUNT. */
	public static final int X_DATA_COUNT = 3600;

	/** The Constant NB_MAJOR_TICK_MAK. */
	private static final int NB_MAJOR_TICK_MAK = 10;

	/**
	 * Start.
	 *
	 * @param primaryStage the primary stage
	 * @throws Exception the exception
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		// The baseChart must be empty !
		SimpleLineChartsMultiYAxis chart = new SimpleLineChartsMultiYAxis(1.0);
		URL stylesheet = SimpleLineChartsMultiYAxisMainXDate3.class.getResource("/org/jlp/javafx/style.css");
		if (null == stylesheet) {
			System.out.println("Null Pointer to style.css");
			System.out.println("jarLocation" + Scene.class.getProtectionDomain().getCodeSource().getLocation());
		} else {
			System.out.println("Yes !! style.css found");
			chart.getStylesheets().add(stylesheet.toString());
		}

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
		chart.addSeries(prepareSeries("Pivot2", (x) -> (double) -1.5d * x * x), Color.CYAN, "Mo/s");
		chart.addSeries(prepareSeries("Pivot2Bis", (x) -> (double) -1.5d * x * Math.sqrt(x)), Color.RED, "toDestroy");
		chart.addSeries(prepareSeries("Pivot3", (x) -> (double) -1.5 * x), Color.BLACK, "Units");
		chart.addSeries(prepareSeries("Pivot4", (x) -> ((double) 2 * x), 0L), Color.GREEN, "Octets");
		chart.addSeries(prepareInverseSeries("Pivot5", (x) -> ((double) (x + 100) * (x - 200)), 0L), Color.BLUE,
				"toDestroy");

		primaryStage.setTitle("MultipleAxesLineChart");

		Scene scene = new Scene(chart, 1024, 600);
		/*
		 * Block that formats date using class MyTypeAxis and a StringConverter
		 * MyLongToDateConverter
		 */
		((NumberAxis) chart.baseChart.getXAxis())
				.setTickLabelFormatter(MyTypeAxis.DATECONVERTER.dateConverter(
						((NumberAxis) chart.baseChart.getXAxis()).upperBoundProperty().longValue()
								- ((NumberAxis) chart.baseChart.getXAxis()).lowerBoundProperty().longValue(),
						Locale.FRANCE));

		chart.timeConverter = MyTypeAxis.DATECONVERTER.myConverter;

		chart.setXLabel(((MyLongToDateConverter) chart.timeConverter).getTimeFormat());

		primaryStage.setScene(scene);
		primaryStage.show();
		System.out.println("apres lancement, trying to select  Pivot5");
		Alert alert = new Alert(AlertType.INFORMATION);

		alert.setResizable(true);

		alert.setTitle("Information Dialog");

		alert.setHeaderText(null);
		alert.setContentText("trying to select Pivot5 et Pivot2Bis et Pivot2 : ");
		alert.showAndWait();
		chart.setSelected("Pivot5", true);
		chart.setSelected("Pivot2Bis", true);
		chart.setSelected("Pivot2", true);
		// alert.setHeaderText(null);
		// alert.setContentText("trying to deselect Pivot5: ");
		// alert.showAndWait();
		// chart.setSelected("Pivot5", false);
		// // System.out.println("apres lancement, trying to hide Pivot5");
		// Alert alert = new Alert(AlertType.INFORMATION);
		// //
		// // alert.setResizable(true);
		// //
		// // alert.setTitle("Information Dialog");
		// //
		// // alert.setHeaderText(null);
		// // alert.setContentText("trying to hide Pivot5: ");
		// //
		// // alert.showAndWait();
		// // chart.hideSerie("Pivot5");
		// // System.out.println("after hiding Pivot5");
		// // System.out.println("before, hiding Pivot2 and baseChart");
		// alert = new Alert(AlertType.INFORMATION);
		//
		// alert.setResizable(true);
		//
		// alert.setTitle("Information Dialog");
		// String strSer = "Pivot3";
		// alert.setHeaderText(null);
		// alert.setContentText("trying to hide " + strSer);
		//
		// alert.showAndWait();
		// chart.hideSerie(strSer);
		// System.out.println("after hiding " + strSer);
		// /* reshow the Xaxis Label */
		// chart.setXLabel(((MyLongToDateConverter)
		// chart.timeConverter).getTimeFormat());
		// // System.out.println("reshowing Pivot5 in 3 seconds");
		// //
		// // Platform.runLater(new Runnable() {
		// //
		// // @Override
		// // public void run() {
		// // try {
		// // Thread.sleep(3000);
		// // chart.reShow("Pivot5");
		// // } catch (InterruptedException e) {
		// // // TODO Auto-generated catch block
		// // e.printStackTrace();
		// // }
		// //
		// // }
		// //
		// // });
		// alert.setContentText("trying to reshow" + strSer);
		// System.out.println("reshowing " + strSer + " in 3 seconds");
		// alert.showAndWait();
		//
		// chart.reShow(strSer);
		// System.out.println("after reshowing " + strSer);
		// chart.setXLabel(((MyLongToDateConverter)
		// chart.timeConverter).getTimeFormat());
		//
		// alert.setResizable(true);
		//
		// alert.setTitle("Information Dialog");
		//
		// alert.setHeaderText(null);
		// alert.setContentText("ClearAll");
		//
		// alert.showAndWait();
		// chart.clear();
		// alert.setContentText("reconstructing Series");
		//
		// alert.showAndWait();
		// chart.addSeries(prepareSeries("Pivot2", (x) -> (double) -1.5d * x * x),
		// Color.BLACK, "Mo/s");
		// chart.addSeries(prepareSeries("Pivot2Bis", (x) -> (double) -1.5d * x *
		// Math.sqrt(x)), Color.RED, "toDestroy");
		// chart.addSeries(prepareSeries("Pivot3", (x) -> (double) -1.5 * x),
		// Color.BLUE, "Units");
		// chart.addSeries(prepareSeries("Pivot4", (x) -> ((double) 2 * x), 0L),
		// Color.GREEN, "Octets");
		// chart.addSeries(prepareInverseSeries("Pivot5", (x) -> ((double) (x + 100) *
		// (x - 200)), 0L), Color.RED,
		// "toDestroy");
		//
		// // To Align all LineCharts
		// chart.setXLabel(((MyLongToDateConverter)
		// chart.timeConverter).getTimeFormat());
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
		for (Long i = 0L; i <= X_DATA_COUNT; i += 100) {
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
			series.getData().add(new XYChart.Data<>(decalXAxis + 1000 * i, function.apply(i)));
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
