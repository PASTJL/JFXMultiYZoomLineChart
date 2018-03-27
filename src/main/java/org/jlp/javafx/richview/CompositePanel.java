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

import java.awt.Toolkit;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.jlp.javafx.SimpleLineChartsMultiYAxis;
import org.jlp.javafx.common.CSVFileAndStrategy;
import org.jlp.javafx.common.ColorsSet;
import org.jlp.javafx.common.NearEvent;
import org.jlp.javafx.common.Project;
import org.jlp.javafx.common.ReBuildEvent;
import org.jlp.javafx.common.SampleEvent;
import org.jlp.javafx.common.ZoomEvent;
import org.jlp.javafx.datas.AggDataDated;
import org.jlp.javafx.datas.csv.CSVFile;
import org.jlp.javafx.datas.csv.TaskPrepareSeries;
import org.jlp.javafx.ext.MyLongToDateConverter;
import org.jlp.javafx.ext.MyTypeAxis;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class CompositePanel.
 */
public class CompositePanel extends BorderPane {

	/** The nb selected. */
	public static int nbSelected = 0;

	/** The fc. */
	static FileChooser fc = new FileChooser();
	static {
		Project.root = System.getProperty("root");
		Project.loadDateProperties(System.getProperty("root"));
		Project.loadLogfouineurProperties(System.getProperty("root"));
		Project.workspace = System.getProperty("workspace");

		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("csv Files", "*.csv", "*.csv.gz"));
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
		String repInitial = Project.workspace;
		if (Project.project.length() > 0) {
			repInitial += File.separator + Project.project;
			if (Project.scenario.length() > 0) {
				repInitial += File.separator + Project.scenario;
				if (new File(repInitial + File.separator + "csv").exists()) {
					repInitial += File.separator + "csv";
				}
			}
		}
		fc.setInitialDirectory(new File(repInitial));
	}
	/** The list CSV suffixes. */
	public static List<String> listCSVSuffixes;
	/** The pat date. */
	public static Pattern patDate = Pattern.compile("\\d{4}/\\d\\d/\\d\\d:\\d\\d:\\d\\d:\\d\\d");

	/** The sdf proj. */
	public static SimpleDateFormat sdfProj = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss");

	/** The rot rank color. */
	private int rotRankColor = 0;
	/** The period. */
	public static Long period;
	/**
	 * The rank CSV. Itis added to the key of hmXYSeries and the name of XYSerie
	 * because the key must be bound with several files or a file can be loaded
	 * several time.
	 */
	public static int rankCSV = 0;

	/** The first size keys. */
	public static int firstSizeKeys = 0;

	/** The last size keys. */
	public static int lastSizeKeys = 0;

	/** The last rank CSV. */
	public static int lastRankCSV = -1;

	/** The hm CSV path and strategy. */
	/*
	 * the Key is the rang of the CSV file The first String is the Absolute path of
	 * CSV file, the second String is the strategy for the data : AVG, MIN, MAX ...
	 */
	public static Map<Integer, String[]> hmCSVPathAndStrategy = new HashMap<Integer, String[]>();
	/**
	 * The hm of hm data dated. Key /String is the name of the series + rank CSV,
	 * value hashmap of X/Y values
	 */
	public static Map<String, Map<Long, AggDataDated>> hmOfHmDataDated = new HashMap<String, Map<Long, AggDataDated>>();

	/**
	 * the hm key is the name series + Rank CSV, first String is the unit, second
	 * String is the csv file absolute path.
	 */
	public static Map<String, String[]> hmOfUnitSource = new HashMap<String, String[]>();

	/** The hm XY series, Key is the final name of the serie by adding a rang. */
	public static Map<String, XYChart.Series<Number, Number>> hmXYSeries = new HashMap<String, XYChart.Series<Number, Number>>();

	/** The beg date zoom. */
	public static Date begDateZoom = null;

	/** The end date zoom. */
	public static Date endDateZoom = null;

	/** The my event type. */
	public static EventType<Event> myEventType;
	static {
		myEventType = new EventType<>("ClearAll");
	}
	/** The chart. */
	public static SimpleLineChartsMultiYAxis chart;

	/** The table view. */
	public static TableView<RowTableModel> tableView = new TableView<RowTableModel>();;

	/**
	 * Gets the table view.
	 *
	 * @return the table view
	 */
	public TableView<RowTableModel> getTableView() {
		return tableView;
	}

	/**
	 * Sets the table view.
	 *
	 * @param tableView
	 *            the new table view
	 */
	public void setTableView(TableView<RowTableModel> tableView) {
		CompositePanel.tableView = tableView;
	}

	/** The menu box. */
	private HBox menuBox = new HBox();;

	/** The collapse. */
	private Button collapse = new Button("Collapse");

	/** To handle multithreading, by default true. */
	private RadioButton btThreads = new RadioButton("multThds?");

	/** The is multi threads. */
	public boolean isMultiThreads = true;

	/** To handle multithreading, if isMultiThreads is true. */
	public TextField nbThreads = new TextField("10.0");

	/** The lab threads. */
	private Label labThreads = new Label("|Coeff");

	/** The lab step. */
	private Label labStep = new Label("Step");

	/** The nb steps. */
	public TextField nbSteps = new TextField("600");

	/** The l strategy. */
	private Label lStrategy = new Label("Strategy");

	/** The cb strategy. */
	public ComboBox<String> cbStrategy;
	/** The units. */
	public ComboBox<String> units;

	/** The bt to left. */
	public static Button btToLeft = new Button("|<- Left");

	/** The bt to right. */
	public static Button btToRight = new Button("Right ->|");

	/** The bt clear all. */
	private Button btClearAll = new Button("Clear");
	/** The sp. */
	public SplitPane sp = null;

	/** The scrp. */
	public ScrollPane scrp = null;

	/** The bt resample. */
	private Button btResample = new Button("Re-Sample");

	/** The vb dates. */
	private VBox vbDates = new VBox();
	/** The l beg proj. */
	private Label lBegProj = new Label("Beg");

	/** The l end proj. */
	private Label lEndProj = new Label("End");

	/** The t beg project. */
	public TextField tBegProject = new TextField();

	/** The t end project. */
	public TextField tEndProject = new TextField();

	/** The l verbosity. */
	private Label lVerbosity = new Label("Verbosity");

	/** The cb verbosity. */
	public static ComboBox<String> cbVerbosity = new ComboBox<String>();

	/** The name listener. */
	public static ChangeListener<String> nameListener;
	/** The fltbv. */
	FillTableView fltbv = new FillTableView();
	/**
	 * The hm X of Translated Series. When a Serie is translated, the original is
	 * hided, and we add a translated series in the chart and the table view with a
	 * name beginning by T_ . If we translate a T_xxx series it keeps this name to
	 * avoid cumulating T_T_ prefixes. If we reset the chart the T_xxx are deleted
	 * and we re-shown all hided series
	 *
	 * Tthe key of the map is the name of the original serie
	 */
	public Map<String, Number> hmXTranslatedValueSeries = new HashMap<String, Number>();

	/** The hm column width. */
	public Map<String, Double> hmColumnWidth = new HashMap<String, Double>();

	/**
	 * Instantiates a new composite panel.
	 *
	 * @param chart
	 *            the chart
	 */
	public CompositePanel(SimpleLineChartsMultiYAxis chart) {

		super();

		addEventHandler(ReBuildEvent.REBUILD_EVENT, event -> {
			// System.out.println("Re-filling table view");
			fltbv.fill();
			// double nbpts = (((NumberAxis) chart.baseChart.getXAxis()).getUpperBound()
			// - ((NumberAxis) chart.baseChart.getXAxis()).getLowerBound()) / period;
			// // System.out.println("nbpts=" + nbpts);
			// if (nbpts > Integer
			// .parseInt(Project.propsLogfouineur.getProperty("logFouineur.sampling.nbPoints",
			// "600"))) {
			// SimpleLineChartsMultiYAxis.isPopupMuted = true;
			// } else {
			// SimpleLineChartsMultiYAxis.isPopupMuted = false;
			// }

		});

		if (null != nameListener) {
			cbVerbosity.getSelectionModel().selectedItemProperty().removeListener(nameListener);
			nameListener = null;
		}

		nameListener = (options, oldValue, newValue) -> {
			switch (newValue) {
			case "Normal":
				SimpleLineChartsMultiYAxis.isPopupMuted = false;
				SimpleLineChartsMultiYAxis.isPopupFullVisible = false;
				break;
			case "Verbose":
				SimpleLineChartsMultiYAxis.isPopupMuted = false;
				SimpleLineChartsMultiYAxis.isPopupFullVisible = true;
				break;
			case "Silent":
				SimpleLineChartsMultiYAxis.isPopupMuted = true;
				break;
			}
		};
		cbVerbosity.getItems().clear();
		cbVerbosity.getItems().addAll("Normal", "Verbose", "Silent");
		cbVerbosity.getSelectionModel().selectedItemProperty().addListener(nameListener);
		btThreads.setSelected(true);
		tBegProject.setPromptText("yyyy/MM/dd:HH:mm:ss");
		tEndProject.setPromptText("yyyy/MM/dd:HH:mm:ss");
		HBox hDateDeb = new HBox();
		hDateDeb.getChildren().addAll(lBegProj, tBegProject);
		HBox hDateFin = new HBox();
		hDateFin.getChildren().addAll(lEndProj, tEndProject);
		lBegProj.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
		lEndProj.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
		tBegProject.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
		tEndProject.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
		vbDates.getChildren().addAll(hDateDeb, hDateFin);
		isMultiThreads = true;
		CompositePanel.chart = chart;
		menuBox.setPrefHeight(30);
		constructTableColumns();

		StackPane stkp = new StackPane(tableView);

		sp = new SplitPane(chart, stkp);
		sp.setMaxWidth(Toolkit.getDefaultToolkit().getScreenSize().getWidth());
		stkp.setPrefWidth(tableView.getWidth());
		stkp.setMaxWidth(Toolkit.getDefaultToolkit().getScreenSize().getWidth());

		// tableView.setMaxWidth(scrp.getWidth());
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		sp.setOrientation(Orientation.VERTICAL);
		sp.setDividerPosition(0, 0.7);

		this.chart = chart;
		SimpleLineChartsMultiYAxis.isPopupMuted = false;
		SimpleLineChartsMultiYAxis.isPopupFullVisible = false;
		this.setTop(menuBox);
		this.setCenter(sp);
		chart.setMinHeight(0.0);
		HBox.setMargin(collapse, new Insets(5, 10, 5, 10));
		Tooltip.install(this.btThreads, new Tooltip("choosing between multi-threads or mono-threads"));
		HBox.setMargin(btThreads, new Insets(10, 0, 5, 10));
		HBox.setMargin(labThreads, new Insets(10, 0, 5, 0));

		HBox.setMargin(nbThreads, new Insets(5, 10, 5, 0));
		Tooltip.install(labThreads, new Tooltip("Coeff multiThreads, nb total= coeff * CPU cores"));
		Tooltip.install(nbThreads, new Tooltip("Coeff multiThreads, nb total= coeff * CPU cores"));
		labStep.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
		HBox.setMargin(labStep, new Insets(10, 0, 5, 10));
		nbSteps.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
		HBox.setMargin(nbSteps, new Insets(5, 0, 5, 0));

		nbSteps.setPrefWidth(80);
		Tooltip t = new Tooltip("Can be a number of points or a time unit");
		Tooltip.install(labStep, t);
		Tooltip.install(this.nbSteps, t);
		nbThreads.setPrefWidth(60);
		ObservableList<String> unitPeriod = FXCollections.observableArrayList("pts", "ms", "s", "mn", "h", "d");
		units = new ComboBox<String>(unitPeriod);

		units.setPrefWidth(80);
		units.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
		HBox.setMargin(units, new Insets(5, 0, 5, 0));

		collapse.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
		btThreads.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
		labThreads.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
		nbThreads.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
		units.setValue("pts");
		ObservableList<String> strategies = FXCollections.observableArrayList("AVG", "PERCTL", "MAX", "MIN", "COUNT",
				"MEDIAN", "SUM");
		cbStrategy = new ComboBox<String>(strategies);
		cbStrategy.setValue("AVG");
		lStrategy.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
		btResample.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
		HBox.setMargin(btResample, new Insets(5, 10, 5, 0));
		Tooltip.install(lStrategy, new Tooltip("Strategy to choise the value, in the aggregate data"));

		cbStrategy.setStyle("-fx-font-weight:bold;-fx-fond-size:10px;");
		HBox.setMargin(lStrategy, new Insets(10, 0, 5, 10));
		HBox.setMargin(cbStrategy, new Insets(5, 0, 0, 0));
		HBox.setMargin(btClearAll, new Insets(5, 10, 5, 10));
		btClearAll.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
		btToLeft.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
		btToRight.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
		HBox.setMargin(lVerbosity, new Insets(10, 0, 5, 10));
		HBox.setMargin(cbVerbosity, new Insets(5, 10, 5, 0));
		lVerbosity.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
		cbVerbosity.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
		HBox.setMargin(btToLeft, new Insets(5, 0, 5, 10));
		HBox.setMargin(btToRight, new Insets(5, 10, 5, 0));
		btToLeft.setDisable(true);
		btToRight.setDisable(true);
		menuBox.getChildren().addAll(collapse, vbDates, lVerbosity, cbVerbosity, btThreads, labThreads, nbThreads,
				labStep, nbSteps, units, btResample, lStrategy, cbStrategy, btClearAll, btToLeft, btToRight);
		listCSVSuffixes = Arrays.asList(Project.propsLogfouineur.getProperty("csvviewer.suffixes").split(";"));
		nbThreads.setText(Project.propsLogfouineur.getProperty("logFouineur.threads.coeff", "1.0"));

		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tableView.setStyle("-fx-selection-bar: RGB(180,180,255);-fx-selection-bar-non-focused: RGB(180,180,255);");

		/* event Handling */

		addEventHandler(SampleEvent.SAMPLE_EVENT, event -> {
			// System.out.println("yea! an SampleEvent event received" + ((SampleEvent)
			// event).debSample + "/"
			// + ((SampleEvent) event).finSample);
			this.tBegProject.setText(sdfProj.format(((SampleEvent) event).debSample));
			this.tEndProject.setText(sdfProj.format(((SampleEvent) event).finSample));

			/* do a clear first */
			chart.clear();
			rankCSV = 0;
			rotRankColor = 0;
			this.hmOfHmDataDated.clear();
			this.hmOfUnitSource.clear();
			this.hmXYSeries.clear();
			firstSizeKeys = 0;
			lastSizeKeys = 0;
			lastRankCSV = -1;
			chart.nbYAxis = 0;
			/* re_init XLabel */
			((NumberAxis) chart.getBaseChart().getXAxis()).setTickLabelFormatter(MyTypeAxis.DATECONVERTER.dateConverter(
					((NumberAxis) chart.getBaseChart().getXAxis()).upperBoundProperty().longValue()
							- ((NumberAxis) chart.getBaseChart().getXAxis()).lowerBoundProperty().longValue(),
					Locale.FRANCE));
			// ((NumberAxis) chart.getBaseChart().getXAxis())
			// .setTickUnit(MyTypeAxis.DATECONVERTER.getTickUnitDefaults()[MyTypeAxis.DATECONVERTER.idxUnit
			// - 1]);
			chart.timeConverter = MyTypeAxis.DATECONVERTER.myConverter;

			chart.setXLabel(((MyLongToDateConverter) chart.timeConverter).getTimeFormat());

			/* retrieve the files from hmCSVPathAndStrategy */
			List<CSVFileAndStrategy> listCSVFiles = new ArrayList<CSVFileAndStrategy>();
			for (Entry<Integer, String[]> entry : CompositePanel.hmCSVPathAndStrategy.entrySet()) {
				listCSVFiles.add(new CSVFileAndStrategy(entry.getValue()[0], entry.getValue()[1].trim().toLowerCase()));

			}
			hmCSVPathAndStrategy.clear();
			proceedCSVs(listCSVFiles);

			event.consume();
		});

		addEventHandler(ZoomEvent.ZOOM_EVENT, event -> {
			// System.out.println("yea! an Zoom event received" + ((ZoomEvent) event).deb +
			// "/" + ((ZoomEvent) event).fin);
			CompositePanel.begDateZoom = ((ZoomEvent) event).deb;
			CompositePanel.endDateZoom = ((ZoomEvent) event).fin;
			tableView.getItems().clear();
			tBegProject.setText(sdfProj.format(CompositePanel.begDateZoom));
			tEndProject.setText(sdfProj.format(CompositePanel.endDateZoom));
			this.fireEvent(new ReBuildEvent());
			event.consume();
		});

		addEventHandler(myEventType, event -> {
			// System.out.println("yea! a clear event received");
			rankCSV = 0;
			rotRankColor = 0;
			CompositePanel.hmOfHmDataDated.clear();
			CompositePanel.hmOfUnitSource.clear();
			CompositePanel.hmXYSeries.clear();
			firstSizeKeys = 0;
			lastSizeKeys = 0;
			lastRankCSV = -1;
			chart.nbYAxis = 0;
			this.tBegProject.setText("");
			this.tEndProject.setText("");
			/* re_init XLabel */
			((NumberAxis) chart.getBaseChart().getXAxis()).setTickLabelFormatter(MyTypeAxis.DATECONVERTER.dateConverter(
					((NumberAxis) chart.getBaseChart().getXAxis()).upperBoundProperty().longValue()
							- ((NumberAxis) chart.getBaseChart().getXAxis()).lowerBoundProperty().longValue(),
					Locale.FRANCE));
			// ((NumberAxis) chart.getBaseChart().getXAxis())
			// .setTickUnit(MyTypeAxis.DATECONVERTER.getTickUnitDefaults()[MyTypeAxis.DATECONVERTER.idxUnit
			// - 1]);
			chart.timeConverter = MyTypeAxis.DATECONVERTER.myConverter;

			chart.setXLabel(((MyLongToDateConverter) chart.timeConverter).getTimeFormat());
		});

		collapse.setOnAction((ActionEvent event) -> {

			if (collapse.getText().equals("Collapse")) {
				collapse.setText("Expand");
				sp.setDividerPosition(0, 0.0);
			} else {
				collapse.setText("Collapse");
				sp.setDividerPosition(0, 1.0);
			}
		});

		btThreads.setOnAction((ActionEvent event) -> {

			if (btThreads.isSelected()) {
				isMultiThreads = true;
				labThreads.setVisible(true);
				nbThreads.setVisible(true);
				// System.out.println("changing to multithreads true");
			} else {
				isMultiThreads = false;
				labThreads.setVisible(false);
				nbThreads.setVisible(false);
				// System.out.println("changing to multithreads false");
			}
		});

		btClearAll.setOnAction((ActionEvent event) -> {
			chart.clear();
			tableView.getItems().clear();

			fireEvent(new Event(myEventType));

		});
		btResample.setOnAction((ActionEvent event) -> {
			/* Just Fire an Event to be treated by the JFX Application */
			/* not very glorius because of strong dependency but easy ;-) */
			Project.dateBeginProject = begDateZoom;
			Project.dateEndProject = endDateZoom;
			tableView.getItems().clear();
			fireEvent(new SampleEvent(begDateZoom, endDateZoom));
		});

		btToLeft.setOnAction((ActionEvent event) -> {
			System.out.println("btLeft0");
			// retrieve all Roww Selected
			ObservableList<RowTableModel> list = tableView.getItems();
			// First loop to find min of series
			double minSerie = Double.MAX_VALUE;
			for (RowTableModel row : list) {
				// System.out.println("btLeft1: " + row.getNameSerie() + " " + row.isShown() + "
				// " + row.isSelected());
				if (row.isShown() && row.isSelected()) {
					if (chart.hmXSortedSeries.get(row.getNameSerie()).getData().get(0).getXValue()
							.doubleValue() <= minSerie) {
						minSerie = chart.hmXSortedSeries.get(row.getNameSerie()).getData().get(0).getXValue()
								.doubleValue();

					}

				}

			}
			// second loop do translation
			for (RowTableModel row : list) {
				if (row.isShown() && row.isSelected()) {
					Double transl = chart.hmXSortedSeries.get(row.getNameSerie()).getData().get(0).getXValue()
							.doubleValue() - minSerie;

					if (transl != 0) {

						CompositePanel.chart.doTranlation(row.getNameSerie(), -transl);
					}

				}
			}
		});
		btToRight.setOnAction((ActionEvent event) -> {
			// retrieve all Roww Selected
			ObservableList<RowTableModel> list = tableView.getItems();
			// First loop to find min of series
			double maxSerie = Double.MIN_VALUE;

			for (RowTableModel row : list) {
				if (row.isShown() && row.isSelected()) {
					Series<Number, Number> series = chart.hmXSortedSeries.get(row.getNameSerie());
					if (series.getData().get(series.getData().size() - 1).getXValue().doubleValue() >= maxSerie) {
						maxSerie = series.getData().get(series.getData().size() - 1).getXValue().doubleValue();
					}
				}
			}
			// second loop do translation
			for (RowTableModel row : list) {
				if (row.isShown() && row.isSelected()) {
					Series<Number, Number> series = chart.hmXSortedSeries.get(row.getNameSerie());
					Double transl = series.getData().get(series.getData().size() - 1).getXValue().doubleValue()
							- maxSerie;
					if (transl != 0) {
						CompositePanel.chart.doTranlation(row.getNameSerie(), -transl);
					}

				}
			}
		});
		addEventFilter(MouseEvent.DRAG_DETECTED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				startFullDrag();

			}
		});
		setOnDragOver(event -> {

			if (event.getGestureSource() != this && event.getDragboard().hasFiles()) {
				event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
				// System.out.println("Yes!!! Drag Over comes with a file"
				// + ((javafx.scene.input.DragEvent)
				// event).getDragboard().getFiles().get(0).getAbsolutePath());
			}
			event.consume();
		});
		setOnDragDropped(event -> {

			if (((javafx.scene.input.DragEvent) event).getDragboard().hasFiles()) {

				// System.out.println("Yes!!! Dropped comes with a file"
				// + ((javafx.scene.input.DragEvent)
				// event).getDragboard().getFiles().get(0).getAbsolutePath());
				List<CSVFileAndStrategy> list = new ArrayList<CSVFileAndStrategy>();
				for (File file : ((javafx.scene.input.DragEvent) event).getDragboard().getFiles()) {
					list.add(new CSVFileAndStrategy(file, cbStrategy.getValue().trim().toLowerCase()));
				}
				proceedCSVs(list);
			}
			event.setDropCompleted(true);
			this.fireEvent(new ReBuildEvent());
			event.consume();
		});
		addEventHandler(NearEvent.NEAR_EVENT, event -> {

			int idxRow = retrieveRowIndexByNameOfSerie(event.nameSerie);

			if (idxRow >= 0) {
				if (event.isNear) {
					CompositePanel.tableView.getSelectionModel().select(idxRow);
				} else {
					CompositePanel.tableView.getSelectionModel().clearSelection(idxRow);
				}

			}

		});

		tableView.skinProperty().addListener((a, b, newSkin) -> {
			// TableHeaderRow headerRow = ((TableViewSkinBase) newSkin).getTableHeaderRow();
			TableHeaderRow header = (TableHeaderRow) tableView.lookup("TableHeaderRow");
			if (null == header) {

			} else {

				header.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

					if (event.isControlDown()) {

						final Stage dialog = new Stage();
						dialog.initModality(Modality.APPLICATION_MODAL);
						dialog.initOwner(null);
						HBox dialogHbox = new HBox(20);
						VBox vb1 = new VBox(10);
						VBox vb2 = new VBox(10);
						VBox vb3 = new VBox(10);

						CheckBox cbUnit = new CheckBox("unit");
						cbUnit.setSelected(unit.isVisible());

						CheckBox cbTransl = new CheckBox("transl");
						cbTransl.setSelected(translation.isVisible());

						CheckBox cbSource = new CheckBox("source");
						cbSource.setSelected(source.isVisible());

						CheckBox cbAvg = new CheckBox("avg");
						cbAvg.setSelected(avg.isVisible());

						CheckBox cbAvgPond = new CheckBox("avgPond");
						cbAvgPond.setSelected(avgPond.isVisible());

						CheckBox cbMin = new CheckBox("min");
						cbMin.setSelected(min.isVisible());

						CheckBox cbMax = new CheckBox("max");
						cbMax.setSelected(max.isVisible());

						CheckBox cbMedian = new CheckBox("median");
						cbMedian.setSelected(median.isVisible());

						vb1.getChildren().addAll(cbUnit, cbTransl, cbSource, cbAvg, cbAvgPond, cbMin, cbMax, cbMedian);

						CheckBox cbPerctle = new CheckBox("perctle");
						cbPerctle.setSelected(perCile.isVisible());

						CheckBox cbStdv = new CheckBox("stdv");
						cbStdv.setSelected(stdv.isVisible());

						CheckBox cbLrSlope = new CheckBox("LrSlope");
						cbLrSlope.setSelected(irslope.isVisible());

						CheckBox cbPts = new CheckBox("pts");
						cbPts.setSelected(countPts.isVisible());

						CheckBox cbVals = new CheckBox("vals");
						cbVals.setSelected(countVal.isVisible());

						CheckBox cbSum = new CheckBox("sum");
						cbSum.setSelected(sum.isVisible());

						CheckBox cbSumTotal = new CheckBox("sumTotal");
						cbSumTotal.setSelected(sumTotal.isVisible());

						vb2.getChildren().addAll(cbPerctle, cbStdv, cbLrSlope, cbPts, cbVals, cbSum, cbSumTotal);

						Button btSave = new Button("Save And Quit");
						vb3.setAlignment(Pos.CENTER);
						vb3.getChildren().add(btSave);
						dialogHbox.getChildren().addAll(vb1, vb2, vb3);
						btSave.setOnMouseClicked(eventBis -> {
							// update Visibility of columns
							unit.setVisible(cbUnit.isSelected());
							translation.setVisible(cbTransl.isSelected());
							source.setVisible(cbSource.isSelected());
							avg.setVisible(cbAvg.isSelected());
							avgPond.setVisible(cbAvgPond.isSelected());
							min.setVisible(cbMin.isSelected());
							max.setVisible(cbMax.isSelected());
							median.setVisible(cbMedian.isSelected());
							perCile.setVisible(cbPerctle.isSelected());
							stdv.setVisible(cbStdv.isSelected());
							irslope.setVisible(cbLrSlope.isSelected());
							countPts.setVisible(cbPts.isSelected());
							countVal.setVisible(cbVals.isSelected());
							sum.setVisible(cbSum.isSelected());
							sumTotal.setVisible(cbSumTotal.isSelected());

							dialog.close();

						});
						Scene dialogScene = new Scene(dialogHbox, 350, 300);
						dialog.setScene(dialogScene);
						dialog.show();

					}

				});
			}
		});
		tableView.setOnMousePressed(event -> {

			if (event.isPrimaryButtonDown()) {
				// selection
				ObservableList<RowTableModel> lstrow = CompositePanel.tableView.getSelectionModel().getSelectedItems();
				for (RowTableModel row : lstrow) {
					if (null != row.getNameSerie() && row.isShown()) {
						CompositePanel.chart.setSelected(row.getNameSerie(), true);
					} else
						return;
				}

				ObservableList<RowTableModel> lst = CompositePanel.tableView.getItems();
				for (RowTableModel rowBis : lst) {
					if (!lstrow.contains(rowBis)) {
						if (rowBis.isSelected()) {

							CompositePanel.chart.setSelected(rowBis.getNameSerie(), true);
						} else {

							CompositePanel.chart.setSelected(rowBis.getNameSerie(), false);
						}

					}
				}
			}

			else if (event.isSecondaryButtonDown()) {

				Alert alert = new Alert(AlertType.CONFIRMATION);

				alert.setResizable(true);

				alert.setTitle("Deleting Series for selected row ?");

				alert.setHeaderText(" Confirm you want to delete the selected Series ");

				Optional<ButtonType> bool = alert.showAndWait();

				if (bool.get().getText().equals("OK")) {
					ObservableList<RowTableModel> lstrow = CompositePanel.tableView.getSelectionModel()
							.getSelectedItems();
					for (RowTableModel row : lstrow) {
						if (null != row.getNameSerie() && row.isShown()) {
							tableView.getItems().remove(row);
							CompositePanel.chart.delSerie(row.getNameSerie());
							hmOfHmDataDated.remove(row.getNameSerie());

						} else
							return;
					}
				}
				return;
			}

			// selection

		});

	}

	/**
	 * Retrieve row index by name of serie.
	 *
	 * @param nameSerie
	 *            the name serie
	 * @return the int
	 */
	private int retrieveRowIndexByNameOfSerie(String nameSerie) {
		int ret = -1;
		int i = 0;
		for (RowTableModel row : CompositePanel.tableView.getItems()) {
			if (row.getNameSerie().equals(nameSerie)) {
				return i;
			}
			i++;
		}

		return ret;
	}

	/** The shown check. */
	private static TableColumn<RowTableModel, Boolean> shownCheck = new TableColumn<>("Shown");

	/** The selected check. */
	private static TableColumn<RowTableModel, Boolean> selectedCheck = new TableColumn<>("Select");

	/** The str color. */
	private static TableColumn<RowTableModel, String> strColor = new TableColumn<>("Color"); // The Column Color

	// #FFFFFFFF; => RGBA

	/** The unit. */
	private static TableColumn<RowTableModel, String> unit = new TableColumn<>("Unit");

	/** The translation. */
	private static TableColumn<RowTableModel, Double> translation = new TableColumn<>("Translation");

	/** The source. */
	private static TableColumn<RowTableModel, String> source = new TableColumn<>("Source");

	/** The name serie. */
	private static TableColumn<RowTableModel, String> nameSerie = new TableColumn<>("Serie");

	/** The strategy. */
	private static TableColumn<RowTableModel, String> strategy = new TableColumn<>("Strategy");

	/** The avg. */
	private static TableColumn<RowTableModel, Double> avg = new TableColumn<>("Avg");

	/** The avg pond. */
	private static TableColumn<RowTableModel, Double> avgPond = new TableColumn<>("AvgPond");

	/** The min. */
	private static TableColumn<RowTableModel, Double> min = new TableColumn<>("Min");

	/** The max. */
	private static TableColumn<RowTableModel, Double> max = new TableColumn<>("Max");

	/** The median. */
	private static TableColumn<RowTableModel, Double> median = new TableColumn<>("Median");

	/** The per cile. */
	private static TableColumn<RowTableModel, Double> perCile = new TableColumn<>(
			"PerCile(" + Project.percentile + ")");

	/** The stdv. */
	private static TableColumn<RowTableModel, Double> stdv = new TableColumn<>(
			"Stdv"); /* Standard deviation ; ecart type */

	/** The irslope. */
	private static TableColumn<RowTableModel, String> irslope = new TableColumn<>("LrSlope"); /* linear regression */

	/** The count pts. */
	private static TableColumn<RowTableModel, Integer> countPts = new TableColumn<>("Pts");

	/** The count val. */
	// countPts.setMaxWidth(-10);
	private static TableColumn<RowTableModel, Integer> countVal = new TableColumn<>("Vals");
	// countVal.setMaxWidth(-10);

	/** The sum. */
	private static TableColumn<RowTableModel, Double> sum = new TableColumn<>(
			"Sum"); /* sometimes has a signification but often it means nothing */

	/** The sum total. */
	private static TableColumn<RowTableModel, Double> sumTotal = new TableColumn<>(
			"SumTotal"); /* including the nb of Vals per concatenation; same remak as just above */

	/**
	 * Construct table columns.
	 */
	public void constructTableColumns() {
		tableView.getColumns().clear();
		shownCheck.setMaxWidth(60);
		shownCheck.setMinWidth(60);
		translation.setVisible(false);
		selectedCheck.setMaxWidth(60);
		selectedCheck.setMinWidth(60);
		strColor.setMaxWidth(50);
		strColor.setMinWidth(50); // format
		countPts.setVisible(false);
		countVal.setVisible(false);
		sum.setVisible(false);
		sumTotal.setVisible(false);
		tableView.getColumns().add(shownCheck);
		tableView.getColumns().add(selectedCheck);
		tableView.getColumns().add(strColor);
		tableView.getColumns().add(unit);
		tableView.getColumns().add(translation);
		tableView.getColumns().add(source);
		tableView.getColumns().add(nameSerie);
		tableView.getColumns().add(strategy);
		tableView.getColumns().add(avg);
		tableView.getColumns().add(avgPond);
		tableView.getColumns().add(min);
		tableView.getColumns().add(max);
		tableView.getColumns().add(median);
		tableView.getColumns().add(perCile);
		;
		tableView.getColumns().add(stdv);

		tableView.getColumns().add(irslope);
		tableView.getColumns().add(countPts);
		tableView.getColumns().add(countVal);
		tableView.getColumns().add(sum);
		tableView.getColumns().add(sumTotal);
		shownCheck.setEditable(true);
		shownCheck.setCellValueFactory(new PropertyValueFactory<>("shown"));
		// shownCheck.setCellFactory(CheckBoxTableCell.forTableColumn(shownCheck));

		// shownCheck.setCellValueFactory(cellData ->
		// cellData.getValue().shownProperty());

		shownCheck.setCellFactory(p -> {
			CheckBox checkBox = new CheckBox();
			TableCell<RowTableModel, Boolean> cell = new TableCell<RowTableModel, Boolean>() {
				@Override
				public void updateItem(Boolean item, boolean empty) {
					if (empty) {
						setGraphic(null);
					} else {
						checkBox.setSelected(item);
						setGraphic(checkBox);

					}
				}
			};
			checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
				// ((RowTableModel) cell.getTableRow().getItem()).setShown(isSelected);
				if (null == ((RowTableModel) cell.getTableRow().getItem()))
					return;
				String nameOfSerie = ((RowTableModel) cell.getTableRow().getItem()).getNameSerie();
				if (isSelected) {
					// show serie if hidden
					// System.out.println("Before seraching showing :" + nameOfSerie);
					if (null != CompositePanel.chart.hmVisibleSeries.get(nameOfSerie)
							&& !CompositePanel.chart.hmVisibleSeries.get(nameOfSerie).isVisible) {
						// System.out.println("showing :" + nameOfSerie);
						CompositePanel.chart.reShow(nameOfSerie);
						((RowTableModel) cell.getTableRow().getItem()).setShown(true);
					}

				} else {
					// hide Serie if Visible
					if (null != CompositePanel.chart.hmVisibleSeries.get(nameOfSerie)
							&& CompositePanel.chart.hmVisibleSeries.get(nameOfSerie).isVisible) {
						// System.out.println("hidding :" + nameOfSerie);
						((RowTableModel) cell.getTableRow().getItem()).setShown(false);
						CompositePanel.chart.hideSerie(nameOfSerie);
					}
				}
				/* re_init XLabel */
				((NumberAxis) chart.getBaseChart().getXAxis()).setTickLabelFormatter(MyTypeAxis.DATECONVERTER
						.dateConverter(((NumberAxis) chart.getBaseChart().getXAxis()).upperBoundProperty().longValue()
								- ((NumberAxis) chart.getBaseChart().getXAxis()).lowerBoundProperty().longValue(),
								Locale.FRANCE));
				// ((NumberAxis) chart.getBaseChart().getXAxis())
				// .setTickUnit(MyTypeAxis.DATECONVERTER.getTickUnitDefaults()[MyTypeAxis.DATECONVERTER.idxUnit
				// - 1]);
				chart.timeConverter = MyTypeAxis.DATECONVERTER.myConverter;

				chart.setXLabel(((MyLongToDateConverter) chart.timeConverter).getTimeFormat());
			});
			cell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			cell.setAlignment(Pos.CENTER);
			return cell;
		});
		selectedCheck.setEditable(true);
		selectedCheck.setCellValueFactory(new PropertyValueFactory<>("selected"));
		// shownCheck.setCellFactory(CheckBoxTableCell.forTableColumn(shownCheck));

		// shownCheck.setCellValueFactory(cellData ->
		// cellData.getValue().shownProperty());

		selectedCheck.setCellFactory(p -> {
			CheckBox checkBoxBis = new CheckBox();
			TableCell<RowTableModel, Boolean> cell = new TableCell<RowTableModel, Boolean>() {
				@Override
				public void updateItem(Boolean item, boolean empty) {
					if (empty) {
						setGraphic(null);
					} else {
						checkBoxBis.setSelected(item);
						setGraphic(checkBoxBis);
					}
				}
			};
			checkBoxBis.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
				if (null != ((RowTableModel) cell.getTableRow().getItem())) {
					String nameOfSerie = ((RowTableModel) cell.getTableRow().getItem()).getNameSerie();
					if (null != CompositePanel.chart.hmVisibleSeries.get(nameOfSerie)) {
						Boolean isShown = ((RowTableModel) cell.getTableRow().getItem()).isShown();
						// System.out.println("isSelected=" + isSelected + " nameSerie = "
						// + ((RowTableModel) cell.getTableRow().getItem()).getNameSerie());
						if (isShown) {
							if (isSelected) {
								CompositePanel.chart.setSelected(nameOfSerie, true);
								((RowTableModel) cell.getTableRow().getItem()).setSelected(true);
								CompositePanel.nbSelected++;
								if (CompositePanel.nbSelected >= 2) {
									CompositePanel.btToLeft.setDisable(false);
									CompositePanel.btToRight.setDisable(false);
								}
							} else {
								CompositePanel.chart.setSelected(nameOfSerie, false);
								((RowTableModel) cell.getTableRow().getItem()).setSelected(false);
								CompositePanel.nbSelected--;
								if (CompositePanel.nbSelected < 2) {
									CompositePanel.btToLeft.setDisable(true);
									CompositePanel.btToRight.setDisable(true);
								}

							}
						}
					}
				}
			});
			cell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

			cell.setAlignment(Pos.CENTER);
			return cell;
		});
		// selectedCheck.setCellValueFactory(new PropertyValueFactory<RowTableModel,
		// SimpleBooleanProperty>("selected"));
		strColor.setCellValueFactory(new PropertyValueFactory<RowTableModel, String>("strColor"));
		strColor.setCellFactory(p -> {
			Rectangle rect = new Rectangle();
			rect.setWidth(30);
			rect.setHeight(15);
			TableCell<RowTableModel, String> cell = new TableCell<RowTableModel, String>() {
				@Override
				public void updateItem(String item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);
						setText(null);
					} else {
						// System.out.println("Color=" + item);
						if (item.length() == 7) {
							rect.setFill(Color.web(item, 1.0));
						}
						// rect.getB.setSelected(item);
						setGraphic(rect);

					}
				}
			};

			cell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

			cell.setAlignment(Pos.CENTER);
			return cell;
		});

		unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
		unit.setCellFactory(p -> {
			TableCell<RowTableModel, String> cell = new TableCell<RowTableModel, String>() {
				public void updateItem(String item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);
						setText(null);
					} else {
						setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
						setText(item);
					}
				}
			};
			cell.setAlignment(Pos.CENTER);
			return cell;
		});

		translation.setCellValueFactory(new PropertyValueFactory<>("translation"));
		translation.setCellFactory(p -> {
			Slider slider = new Slider(-1, 1, 0.0);
			slider.setShowTickMarks(true);
			slider.setShowTickLabels(true);
			slider.setMajorTickUnit(0.25f);
			slider.setBlockIncrement(0.1f);
			TableCell<RowTableModel, Double> cell = new TableCell<RowTableModel, Double>() {
				@Override
				public void updateItem(Double item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);
						slider.setValue(0.5d);
					} else {
						setGraphic(slider);
						setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
						slider.setValue(0.0);
					}

				}
			};
			slider.addEventFilter(MouseEvent.MOUSE_RELEASED, pbis -> {

				// Do translation here
				// System.out.println("name Series is " +
				// cell.getTableRow().getItem().getNameSerie());
				Double limits = ((NumberAxis) CompositePanel.chart.baseChart.getXAxis()).getUpperBound()
						- ((NumberAxis) CompositePanel.chart.baseChart.getXAxis()).getLowerBound();
				CompositePanel.chart.doTranlation(cell.getTableRow().getItem().getNameSerie(),
						Math.round(slider.getValue() * limits / 10));
				// re-locate at 0.0
				slider.setValue(0.0);
			});
			return cell;
		});
		source.setCellValueFactory(new PropertyValueFactory<RowTableModel, String>("source"));

		source.setCellFactory(p -> {
			Label lab = new Label();
			lab.setMinWidth(source.getWidth());
			lab.setPrefWidth(source.getWidth());
			lab.setMaxWidth(source.getWidth());
			lab.setWrapText(false);
			lab.setTextAlignment(TextAlignment.RIGHT);
			lab.setEllipsisString("");

			TableCell<RowTableModel, String> cell = new TableCell<RowTableModel, String>() {
				@Override
				public void updateItem(String item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);
						setText(null);
					} else {
						// System.out.println("Color=" + item);
						lab.setFont(javafx.scene.text.Font.font("Arial", FontWeight.BOLD, 10));
						lab.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
						lab.setText(item);

						lab.setTextAlignment(TextAlignment.RIGHT);
						lab.setAlignment(Pos.CENTER_RIGHT);
						// rect.getB.setSelected(item);
						setGraphic(lab);
						String pref2 = "...";
						int lg2 = item.length() - 1;
						while (lg2 > 0) {

							Text tmp = new Text(pref2 + item.substring(lg2));
							tmp.setFont(javafx.scene.text.Font.font("Arial", FontWeight.BOLD, 10));

							if (tmp.getLayoutBounds().getWidth() < source.getWidth()) {
								lg2--;
							} else {

								if (lg2 == 1) {
									lab.setText(item);
								} else {
									lab.setText(pref2 + item.substring(lg2 + 1));
								}
								break;
							}
						}
						source.widthProperty().addListener(q -> {

							lab.setMaxWidth(source.getWidth());
							lab.setMinWidth(source.getWidth());
							lab.setPrefWidth(source.getWidth());

							String pref = "...";
							int lg = item.length() - 1;
							while (lg > 0) {

								Text tmp = new Text(pref + item.substring(lg));
								tmp.setFont(javafx.scene.text.Font.font("Arial", FontWeight.BOLD, 10));

								if (tmp.getLayoutBounds().getWidth() < source.getWidth()) {
									lg--;
								} else {

									if (lg == 1) {
										lab.setText(item);
									} else {
										lab.setText(pref + item.substring(lg + 1));
									}
									break;
								}
							}
							// System.out.println("width column changed");

						});

					}
				}
			};

			cell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			cell.setPadding(new Insets(0));
			cell.setAlignment(Pos.CENTER);
			return cell;
		});

		nameSerie.setCellValueFactory(new PropertyValueFactory<RowTableModel, String>("nameSerie"));

		nameSerie.setCellFactory(p -> {
			Label lab = new Label();
			lab.setMinWidth(nameSerie.getWidth());
			lab.setPrefWidth(nameSerie.getWidth());
			lab.setMaxWidth(nameSerie.getWidth());
			lab.setWrapText(false);
			lab.setTextAlignment(TextAlignment.RIGHT);
			lab.setEllipsisString("");

			TableCell<RowTableModel, String> cell = new TableCell<RowTableModel, String>() {
				@Override
				public void updateItem(String item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);
						setText(null);
					} else {
						// System.out.println("Color=" + item);
						lab.setFont(javafx.scene.text.Font.font("Arial", FontWeight.BOLD, 10));

						lab.setText(item);
						lab.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
						lab.setTextAlignment(TextAlignment.RIGHT);
						lab.setAlignment(Pos.CENTER_RIGHT);
						// rect.getB.setSelected(item);
						setGraphic(lab);
						String pref2 = "...";
						int lg2 = item.length() - 1;
						while (lg2 > 0) {

							Text tmp = new Text(pref2 + item.substring(lg2));
							tmp.setFont(javafx.scene.text.Font.font("Arial", FontWeight.BOLD, 10));

							if (tmp.getLayoutBounds().getWidth() < nameSerie.getWidth()) {
								lg2--;
							} else {

								if (lg2 == 1) {
									lab.setText(item);
								} else {
									lab.setText(pref2 + item.substring(lg2 + 1));
								}
								break;
							}
						}
						nameSerie.widthProperty().addListener(q -> {

							lab.setMaxWidth(nameSerie.getWidth());
							lab.setMinWidth(nameSerie.getWidth());
							lab.setPrefWidth(nameSerie.getWidth());

							String pref = "...";
							int lg = item.length() - 1;
							while (lg > 0) {

								Text tmp = new Text(pref + item.substring(lg));
								tmp.setFont(javafx.scene.text.Font.font("Arial", FontWeight.BOLD, 10));

								if (tmp.getLayoutBounds().getWidth() < nameSerie.getWidth()) {
									lg--;
								} else {

									if (lg == 1) {
										lab.setText(item);
									} else {
										lab.setText(pref + item.substring(lg + 1));
									}
									break;
								}
							}
							// System.out.println("width column changed");

						});

					}
				}
			};

			cell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			cell.setPadding(new Insets(0));
			cell.setAlignment(Pos.CENTER);
			return cell;
		});

		strategy.setCellValueFactory(new PropertyValueFactory<>("strategy"));
		strategy.setCellFactory(p -> {
			TableCell<RowTableModel, String> cell = new TableCell<RowTableModel, String>() {
				public void updateItem(String item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);
						setText(null);
					} else {
						setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
						setText(item);
					}
				}
			};
			cell.setAlignment(Pos.CENTER);
			return cell;
		});

		avg.setCellValueFactory(new PropertyValueFactory<>("avg"));
		avg.setCellFactory(p -> {
			TableCell<RowTableModel, Double> cell = new TableCell<RowTableModel, Double>() {
				public void updateItem(Double item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);
						setText(null);
					} else {
						setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
						setText(formatNumber(item));
					}
				}
			};
			cell.setAlignment(Pos.CENTER);
			return cell;
		});
		avgPond.setCellValueFactory(new PropertyValueFactory<>("avgPond"));
		avgPond.setCellFactory(p -> {
			TableCell<RowTableModel, Double> cell = new TableCell<RowTableModel, Double>() {
				public void updateItem(Double item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);
						setText(null);
					} else {
						setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
						setText(formatNumber(item));
					}
				}
			};
			cell.setAlignment(Pos.CENTER);
			return cell;
		});
		min.setCellValueFactory(new PropertyValueFactory<>("min"));
		min.setCellFactory(p -> {
			TableCell<RowTableModel, Double> cell = new TableCell<RowTableModel, Double>() {
				public void updateItem(Double item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);
						setText(null);
					} else {
						setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
						setText(formatNumber(item));
					}
				}
			};
			cell.setAlignment(Pos.CENTER);
			return cell;
		});
		max.setCellValueFactory(new PropertyValueFactory<>("max"));
		max.setCellFactory(p -> {
			TableCell<RowTableModel, Double> cell = new TableCell<RowTableModel, Double>() {
				public void updateItem(Double item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);
						setText(null);
					} else {
						setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
						setText(formatNumber(item));
					}
				}
			};
			cell.setAlignment(Pos.CENTER);
			return cell;
		});
		median.setCellValueFactory(new PropertyValueFactory<>("median"));
		median.setCellFactory(p -> {
			TableCell<RowTableModel, Double> cell = new TableCell<RowTableModel, Double>() {
				public void updateItem(Double item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);
						setText(null);
					} else {
						setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
						setText(formatNumber(item));
					}
				}
			};
			cell.setAlignment(Pos.CENTER);
			return cell;
		});
		perCile.setCellValueFactory(new PropertyValueFactory<>("perCtl"));
		perCile.setCellFactory(p -> {
			TableCell<RowTableModel, Double> cell = new TableCell<RowTableModel, Double>() {
				public void updateItem(Double item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);
						setText(null);
					} else {
						setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
						setText(formatNumber(item));
					}
				}
			};
			cell.setAlignment(Pos.CENTER);
			return cell;
		});
		stdv.setCellValueFactory(new PropertyValueFactory<>("stdv"));
		stdv.setCellFactory(p -> {
			TableCell<RowTableModel, Double> cell = new TableCell<RowTableModel, Double>() {
				public void updateItem(Double item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);
						setText(null);
					} else {
						setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
						setText(formatNumber(item));
					}
				}
			};
			cell.setAlignment(Pos.CENTER);
			return cell;
		});

		irslope.setCellValueFactory(new PropertyValueFactory<>("irslope"));
		irslope.setCellFactory(p -> {
			TableCell<RowTableModel, String> cell = new TableCell<RowTableModel, String>() {
				public void updateItem(String item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);
						setText(null);
					} else {
						setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
						setText(item);
					}
				}
			};
			cell.setAlignment(Pos.CENTER);
			return cell;
		});
		countPts.setCellValueFactory(new PropertyValueFactory<>("countPts"));
		countPts.setCellFactory(p -> {
			TableCell<RowTableModel, Integer> cell = new TableCell<RowTableModel, Integer>() {
				public void updateItem(Integer item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);
						setText(null);
					} else {
						setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
						setText(formatNumber(item));
					}
				}
			};
			cell.setAlignment(Pos.CENTER);
			return cell;
		});
		countVal.setCellValueFactory(new PropertyValueFactory<>("countVal"));
		countVal.setCellFactory(p -> {
			TableCell<RowTableModel, Integer> cell = new TableCell<RowTableModel, Integer>() {
				public void updateItem(Integer item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);
						setText(null);
					} else {
						setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
						setText(formatNumber(item));
					}
				}
			};
			cell.setAlignment(Pos.CENTER);
			return cell;
		});
		sum.setCellValueFactory(new PropertyValueFactory<>("sum"));
		sum.setCellFactory(p -> {
			TableCell<RowTableModel, Double> cell = new TableCell<RowTableModel, Double>() {
				public void updateItem(Double item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);
						setText(null);
					} else {
						setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
						setText(formatNumber(item));
					}
				}
			};
			cell.setAlignment(Pos.CENTER);
			return cell;
		});
		sumTotal.setCellValueFactory(new PropertyValueFactory<>("sumTotal"));
		sumTotal.setCellFactory(p -> {
			TableCell<RowTableModel, Double> cell = new TableCell<RowTableModel, Double>() {
				public void updateItem(Double item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);
						setText(null);
					} else {
						setStyle("-fx-font-weight:bold;-fx-font-size:10px;");
						setText(formatNumber(item));
					}
				}
			};
			cell.setAlignment(Pos.CENTER);
			return cell;
		});

		tableView.setEditable(true);
		tableView.setStyle("-fx-font-weight:bold;-fx-font-size:10px;");

	}

	/**
	 * Format number.
	 *
	 * @param num
	 *            the num
	 * @return the string
	 */
	private static String formatNumber(Number num) {
		if (num instanceof Integer) {
			return ((Integer) num).toString();
		} else if (num instanceof Double) {

			if (Math.abs(num.doubleValue()) < 10000) {
				return String.format("%6.2f", num.doubleValue()).replace(",", ".");
			} else {
				return String.format("%4.2E", num.doubleValue()).replace(",", ".");
			}

		}

		return null;
	}

	/**
	 * Proceed CS vs.
	 *
	 * @param listCSVFiles
	 *            the list CSV files
	 */
	public void proceedCSVs(List<CSVFileAndStrategy> listCSVFiles) {
		Long beg = System.currentTimeMillis();
		boolean isOneFile = true;
		if (listCSVFiles.size() > 1) {
			isOneFile = false;
		}
		/* 2010/05/12:16:21:14 */
		/* testing if we can determine a period */
		if (units.getValue().equals("pts")) {
			/* calcul of period with begin and end of observation and nb of points */
			if (tBegProject.getText().trim().length() < 1 || tEndProject.getText().trim().length() < 1
					|| !patDate.matcher(tEndProject.getText().trim()).find()
					|| !patDate.matcher(tBegProject.getText().trim()).find()) {
				/* Alerte */
				Alert alert = new Alert(AlertType.ERROR);

				alert.setResizable(true);

				alert.setTitle("Dates Begin and End are not correct");

				alert.setHeaderText(
						" The period can't be computed. Please fill the dates or choose a unit and a value  of period");

				alert.showAndWait();
				return;
			}
			try {
				Long begInMillis = sdfProj.parse(tBegProject.getText().trim()).getTime();
				Long endInMillis = sdfProj.parse(tEndProject.getText().trim()).getTime();
				period = (endInMillis - begInMillis) / Long.parseLong(nbSteps.getText().trim());
				Project.dateBeginProject = sdfProj.parse(tBegProject.getText().trim());
				Project.dateEndProject = sdfProj.parse(tEndProject.getText().trim());
			} catch (ParseException e) {

				e.printStackTrace();
			}
		} else {
			/*
			 * calcul de la periode en fonction de l unit� et de la dur�e, on met a null les
			 * dates de debut et de fin de Projet
			 */
			if (tBegProject.getText().trim().length() < 1) {
				Project.dateBeginProject = null;
				Project.dateEndProject = null;
			}
			switch (units.getValue().trim()) {
			case "ms":
				period = Long.parseLong(nbSteps.getText().trim());
				break;

			case "s":
				period = Long.parseLong(nbSteps.getText().trim()) * 1000;
				break;

			case "mn":
				period = Long.parseLong(nbSteps.getText().trim()) * 1000 * 60;
				break;
			case "h":
				period = Long.parseLong(nbSteps.getText().trim()) * 1000 * 60 * 60;
				break;
			case "d":
				period = Long.parseLong(nbSteps.getText().trim()) * 1000 * 60 * 60 * 24;
				break;
			case "M":
				period = Long.parseLong(nbSteps.getText().trim()) * 1000 * 60 * 60 * 24 * 30;
				break;
			default:
				period = 1000L;
				break;
			}
		}
		if (isMultiThreads) {

			Project.propsLogfouineur.setProperty("logFouineur.threads.coeff",
					nbThreads.getText().trim().replace(",", "."));
		}

		for (CSVFileAndStrategy csvFileAndStrategy : listCSVFiles) {
			String suff = csvFileAndStrategy.file.getName().substring(csvFileAndStrategy.file.getName().indexOf("."));
			lastRankCSV = rankCSV;
			if (suff != null && suff.length() > 0 && listCSVSuffixes.contains(suff)) {

				// System.out.println("Ok the file : " +
				// csvFileAndStrategy.file.getAbsolutePath() + " can be treated");

				CSVFile csvFile = new CSVFile(csvFileAndStrategy.file.getAbsolutePath(), isMultiThreads);
				csvFile.proceedCsvFile(isMultiThreads, period, rankCSV);
				hmOfHmDataDated.putAll(csvFile.disp.hmOfHmDataDated);

				hmOfUnitSource.putAll(csvFile.disp.hmOfUnitSource);
				lastSizeKeys += csvFile.disp.hmOfUnitSource.size();
				hmCSVPathAndStrategy.put(rankCSV, new String[] { csvFile.path, csvFileAndStrategy.strategy });
				rankCSV++;

			} else {
				System.out.println("No ! the file : " + csvFileAndStrategy.file.getAbsolutePath()
						+ " has an incorrect suffixe and can't be treated");
			}

		}

		System.out.println("ProceedCSV in " + (System.currentTimeMillis() - beg) + " milliseconds");

		generateXYSeries(isOneFile);

		lastRankCSV = rankCSV - 1;
		System.out.println(
				"CSVChartViewerMain : all  Files  treated in " + (System.currentTimeMillis() - beg) + " milliseconds");

		CompositePanel.begDateZoom = new Date(((long) ((NumberAxis) chart.baseChart.getXAxis()).getLowerBound()));
		CompositePanel.endDateZoom = new Date(((long) ((NumberAxis) chart.baseChart.getXAxis()).getUpperBound()));
		tBegProject.setText(sdfProj.format(CompositePanel.begDateZoom));
		tEndProject.setText(sdfProj.format(CompositePanel.endDateZoom));
		this.fireEvent(new ReBuildEvent());
	}

	/**
	 * Generate XY series.
	 *
	 * @param isOneFile
	 *            the is one file
	 */
	private void generateXYSeries(boolean isOneFile) {
		Long beg = System.currentTimeMillis();
		// System.out.println("generateSeries pool size = " + hmOfHmDataDated.size());
		// ExecutorService executorBis =
		// Executors.newFixedThreadPool(hmOfHmDataDated.size());
		ExecutorService executorBis = Executors.newFixedThreadPool(lastSizeKeys - firstSizeKeys);
		Future<XYChart.Series<Number, Number>>[] tabSeries = (Future<XYChart.Series<Number, Number>>[]) new Future[lastSizeKeys
				- firstSizeKeys];
		TaskPrepareSeries[] tabTaskSer = new TaskPrepareSeries[lastSizeKeys - firstSizeKeys];
		// System.out.println("lastSizeKeys - firstSizeKeys =" + (lastSizeKeys -
		// firstSizeKeys));

		int i = 0;
		if (isOneFile) {
			for (Entry<String, Map<Long, AggDataDated>> entry : hmOfHmDataDated.entrySet()) {
				int rankCSVToTreat = Integer.parseInt(entry.getKey().substring(entry.getKey().lastIndexOf("-") + 1));
				// System.out.println("One File Entry name=" + entry.getKey());
				// System.out.println("One File Treating Entry name=" + entry.getKey() + " => "
				// + (entry.getKey().substring(entry.getKey().lastIndexOf("-") + 1)) + "
				// lastRankCSV="
				// + lastRankCSV);
				if (Integer.parseInt(entry.getKey().substring(entry.getKey().lastIndexOf("-") + 1)) >= lastRankCSV) {
					// System.out.println("One File Treating Entry name=" + entry.getKey() + " => "
					// + (entry.getKey().substring(entry.getKey().lastIndexOf("-") + 1)));
					tabTaskSer[i] = new TaskPrepareSeries(entry.getKey(), entry.getValue().entrySet(),
							hmCSVPathAndStrategy.get(rankCSVToTreat)[1]);
					tabSeries[i] = executorBis.submit(tabTaskSer[i]);
					i++;
				}

			}
		} else {
			for (Entry<String, Map<Long, AggDataDated>> entry : hmOfHmDataDated.entrySet()) {
				int rankCSVToTreat = Integer.parseInt(entry.getKey().substring(entry.getKey().lastIndexOf("-") + 1));
				// System.out.println("List File Entry name=" + entry.getKey());
				// System.out.println("List File Treating Entry name=" + entry.getKey() + " => "
				// + (entry.getKey().substring(entry.getKey().lastIndexOf("-") + 1)) + "
				// lastRankCSV="
				// + lastRankCSV);
				if (Integer.parseInt(entry.getKey().substring(entry.getKey().lastIndexOf("-") + 1)) <= lastRankCSV) {
					// System.out.println("List File Treating Entry name=" + entry.getKey() + " => "
					// + (entry.getKey().substring(entry.getKey().lastIndexOf("-") + 1)));
					tabTaskSer[i] = new TaskPrepareSeries(entry.getKey(), entry.getValue().entrySet(),
							hmCSVPathAndStrategy.get(rankCSVToTreat)[1]);
					tabSeries[i] = executorBis.submit(tabTaskSer[i]);
					i++;
				}

			}

		}
		executorBis.shutdown();
		while (!executorBis.isTerminated()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("create series in " + (System.currentTimeMillis() - beg) + " milliseconds");
		// System.out.println("lastSizeKeys - firstSizeKeys=" + (lastSizeKeys -
		// firstSizeKeys));
		Long debAddSerie = System.currentTimeMillis();
		for (i = 0; i < lastSizeKeys - firstSizeKeys; i++) {
			try {
				// Long deb2 = System.currentTimeMillis();

				CompositePanel.chart.addSeries(tabSeries[i].get(), ColorsSet.tabColors[rotRankColor],
						hmOfUnitSource.get(tabSeries[i].get().getName())[0]);
				hmXYSeries.put(tabSeries[i].get().getName(), tabSeries[i].get());
				// System.out.println(" adding series :" + tabSeries[i].get().getName() + "
				// takes "
				// + (System.currentTimeMillis() - deb2) + " millis");
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			rotRankColor = (rotRankColor + 1) % ColorsSet.tabColors.length;
		}
		System.out.println("adding series in " + (System.currentTimeMillis() - debAddSerie) + " milliseconds");
		firstSizeKeys = lastSizeKeys;

		/* if the number of points exceeds 600 pts deactivate the popup */
		// double nbpts = (((NumberAxis) chart.baseChart.getXAxis()).getUpperBound()
		// - ((NumberAxis) chart.baseChart.getXAxis()).getLowerBound()) / period;
		// // System.out.println("nbpts=" + nbpts);
		// if (nbpts >
		// Integer.parseInt(Project.propsLogfouineur.getProperty("logFouineur.sampling.nbPoints",
		// "600"))) {
		// SimpleLineChartsMultiYAxis.isPopupMuted = true;
		// } else {
		// SimpleLineChartsMultiYAxis.isPopupMuted = Project.isPopupMuted;
		// }

		System.out.println("generating series in " + (System.currentTimeMillis() - beg) + " milliseconds");
	}

}
