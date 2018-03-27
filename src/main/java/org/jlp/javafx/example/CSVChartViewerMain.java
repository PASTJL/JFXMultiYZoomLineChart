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

import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jlp.javafx.SimpleLineChartsMultiYAxis;
import org.jlp.javafx.ZoomableLineChartsMultiYAxis;
import org.jlp.javafx.common.CSVFileAndStrategy;
import org.jlp.javafx.common.Project;
import org.jlp.javafx.ext.MyLongToDateConverter;
import org.jlp.javafx.ext.MyTypeAxis;
import org.jlp.javafx.richview.CompositePanel;
import org.jlp.javafx.richview.RowTableModel;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class CSVChartViewerMain.
 */
public class CSVChartViewerMain extends Application {

	/** The cp. */
	CompositePanel cp;

	/** The screen. */
	private VBox screen = new VBox();

	/** The menu gen. */
	private HBox menuGen = new HBox();

	/** The menu bar. */
	private MenuBar menuBar = new MenuBar();

	/** The menu 1. */
	private Menu menu1 = new Menu("File");

	/** The m open csv files. */
	private MenuItem mOpenCsvFiles = new MenuItem("Open Csv Files");

	/** The m quit. */
	private MenuItem mQuit = new MenuItem("Quit");

	/** The l date format. */
	private Label lDateFormat = new Label("Date Format: yyyy/MM/dd:HH:mm:ss");

	/** The chart. */
	public SimpleLineChartsMultiYAxis chart = new ZoomableLineChartsMultiYAxis(1.0, true);;

	/**
	 * Start.
	 *
	 * @param primaryStage
	 *            the primary stage
	 * @throws Exception
	 *             the exception
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		FileChooser fc = new FileChooser();

		Project.root = System.getProperty("root");
		Project.loadDateProperties(System.getProperty("root"));
		Project.loadLogfouineurProperties(System.getProperty("root"));
		Project.workspace = System.getProperty("workspace");

		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("csv Files", "*.csv", "*.csv.gz"));
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
		fc.setInitialDirectory(new File(Project.workspace));

		SimpleLineChartsMultiYAxis.isPopupMuted = false;
		SimpleLineChartsMultiYAxis.isPopupFullVisible = false;

		cp = new CompositePanel(chart);
		primaryStage.setTitle("MultipleAxesLineChart");
		menu1.getItems().addAll(mOpenCsvFiles, mQuit);
		menuBar.getMenus().addAll(menu1);

		lDateFormat.setStyle("-fx-font-weight:bold;-fx-font-size:12px;");
		menu1.setStyle("-fx-font-weight:bold;-fx-font-size:12px;");

		mOpenCsvFiles.setStyle("-fx-font-weight:bold;-fx-font-size:12px;");

		mQuit.setStyle("-fx-font-weight:bold;-fx-font-size:12px;");

		HBox.setMargin(menuBar, new Insets(0, 0, 0, 10));

		HBox.setMargin(lDateFormat, new Insets(7, 0, 0, 0));
		menuGen.getChildren().addAll(menuBar, lDateFormat);
		screen.getChildren().addAll(menuGen, cp);
		cp.setMaxWidth(Toolkit.getDefaultToolkit().getScreenSize().getWidth());
		chart.setMaxWidth(Toolkit.getDefaultToolkit().getScreenSize().getWidth());
		Scene scene = new Scene(screen, Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 3 / 4,
				Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 3 / 4);
		primaryStage.setMaxWidth(Toolkit.getDefaultToolkit().getScreenSize().getWidth());
		primaryStage.setScene(scene);
		primaryStage.show();
		CompositePanel.cbVerbosity.getSelectionModel().select("Normal");
		SimpleLineChartsMultiYAxis.isPopupMuted = false;
		SimpleLineChartsMultiYAxis.isPopupFullVisible = false;

		mQuit.setOnAction(event -> {
			System.exit(0);
		});
		primaryStage.setTitle("MultipleAxesLineChart");
		mOpenCsvFiles.setOnAction(event -> {
			List<File> selectedFiles = fc.showOpenMultipleDialog(primaryStage);
			List<CSVFileAndStrategy> list = new ArrayList<CSVFileAndStrategy>();

			if (selectedFiles != null) {
				for (File file : selectedFiles) {
					list.add(new CSVFileAndStrategy(file, cp.cbStrategy.getValue().trim().toLowerCase()));
				}
				cp.proceedCSVs(list);
			}
		});
		/*
		 * Block that formats date using class MyTypeAxis and a StringConverter
		 * MyLongToDateConverter
		 */
		((NumberAxis) CompositePanel.chart.getBaseChart().getXAxis())
				.setTickLabelFormatter(MyTypeAxis.DATECONVERTER.dateConverter(
						((NumberAxis) CompositePanel.chart.getBaseChart().getXAxis()).upperBoundProperty().longValue()
								- ((NumberAxis) CompositePanel.chart.getBaseChart().getXAxis()).lowerBoundProperty()
										.longValue(),
						Locale.FRANCE));
		// ((NumberAxis) chart.getBaseChart().getXAxis())
		// .setTickUnit(MyTypeAxis.DATECONVERTER.getTickUnitDefaults()[MyTypeAxis.DATECONVERTER.idxUnit
		// - 1]);
		CompositePanel.chart.timeConverter = MyTypeAxis.DATECONVERTER.myConverter;

		CompositePanel.chart.setXLabel(((MyLongToDateConverter) cp.chart.timeConverter).getTimeFormat());
		//
		ObservableList<RowTableModel> datas = FXCollections.observableArrayList();
		datas.add(new RowTableModel());
		cp.getTableView().getItems().addAll(datas);

		scene.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				// System.out.println("Change width => " + scene.getWidth());
				cp.setMaxWidth(scene.getWidth());
				cp.setPrefWidth(scene.getWidth());
				cp.sp.setMaxWidth(scene.getWidth());
				cp.sp.setPrefWidth(scene.getWidth());
				CompositePanel.chart.setMaxWidth(scene.getWidth());
				CompositePanel.chart.setMinWidth(scene.getWidth());
				cp.getTableView().setMinWidth(scene.getWidth());
				cp.getTableView().setPrefWidth(scene.getWidth());
				cp.getTableView().setMaxWidth(scene.getWidth());
				Double div = cp.sp.getDividerPositions()[0];
				cp.getTableView().setPrefHeight(primaryStage.getHeight() * div);
				cp.getTableView().setMaxHeight(primaryStage.getHeight() * div);
				CompositePanel.tableView.setEditable(true);
			}
		});

	}

}
