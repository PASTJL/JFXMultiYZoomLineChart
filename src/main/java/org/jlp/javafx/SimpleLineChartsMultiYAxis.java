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
package org.jlp.javafx;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jlp.javafx.ext.MyLongToDateConverter;
import org.jlp.javafx.ext.MyYaxisDoubleFormatter;

import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.StringConverter;

// TODO: Auto-generated Javadoc
/**
 * A simple set of LineCharts that share the same XAxis and with several YAxis,
 * the LineChart YAxis is located at the left Size the others at the right size.
 *
 * @author Jean-Louis PASTUREL 12/2017
 */

public class SimpleLineChartsMultiYAxis extends StackPane {

	/** The dbl formatter. */
	protected MyYaxisDoubleFormatter dblFormatter = new MyYaxisDoubleFormatter();
	
	/** The is re showing serie. */
	private boolean isReShowingSerie = false;
	/** The is rebuild base chart. */
	private boolean isRebuildBaseChart = false;
	/** The hm hmVisibleSeries series. */
	protected Map<String, TupleBoolLC> hmVisibleSeries = new HashMap<String, TupleBoolLC>();

	/** The hm X sorted series. */
	public Map<String, Series<Number, Number>> hmXSortedSeries = new HashMap<String, Series<Number, Number>>();

	/** The hm Y sorted series. */
	public Map<String, Series<Number, Number>> hmYSortedSeries = new HashMap<String, Series<Number, Number>>();

	/** The nb X ticks. */
	public static int nbXTicks = 40;

	/** The nb Y ticks. */
	public static int nbYTicks = 20;
	/** The base chart. */
	public LineChart<Number, Number> baseChart;

	/**
	 * Getter for the main BaseChart of the StackPane.
	 *
	 * @return the base chart
	 */
	public LineChart<Number, Number> getBaseChart() {
		return baseChart;
	}

	/** The details popup. */
	final DetailsPopup detailsPopup = new DetailsPopup();
	/** The is popup muted. */
	public static boolean isPopupMuted = true;
	/** The is popup visible. */
	public static boolean isPopupFullVisible = true;

	/**
	 * This fields timeConverter allows to format date on the shared XAxis formats
	 * date using enum MyTypeAxis and a StringConverter MyLongToDateConverter.
	 */
	public StringConverter<Number> timeConverter = null;
	/**
	 * 
	 * This fields ObservableList contains a list of tuples (String, LineChart), the
	 * String part of the tuple is the unit of YAxis. A lineChar can have more than
	 * 1 Series, if the unit are the same for the series
	 */
	protected final ObservableList<TupleStrLC> backgroundCharts = FXCollections.observableArrayList();

	/**
	 * Gets the background charts.
	 *
	 * @return the background charts
	 */
	private ObservableList<TupleStrLC> getBackgroundCharts() {
		return backgroundCharts;
	}

	/** The chart color map. */
	protected final Map<LineChart<Number, Number>, Color> chartColorMap = new HashMap<>();

	/**
	 * The Constant hmBoldAxis a hashMap with keys are LineChart and value a boolean
	 * to indicate wich YAxis must be colored when cross cursor is near a Serie.
	 */
	public static final Map<LineChart<Number, Number>, Boolean> hmBoldAxis = new HashMap<>();

	/** The y axis width. */
	protected final double yAxisWidth = 60;

	/** The y axis separation when Y Axis are in the right side. */
	private final double yAxisSeparation = 20;

	/** The stroke width of the lineChart . */
	private double strokeWidth = 0.2;

	/**
	 * The details window. Shows or not the values X/Y of series, bound with
	 * isPopupVisible
	 */
	private final AnchorPane detailsWindow;

	/**
	 * Instantiates a new simple line charts multi Y axis.
	 *
	 * @param strokeWidthpas
	 *            the stroke widthpas
	 */
	public SimpleLineChartsMultiYAxis(Double strokeWidthpas) {
		this(strokeWidthpas, true);
	}

	/**
	 * Instantiates a new simple line charts multi Y axis.
	 *
	 * @param strokeWidthpas
	 *            the stroke widthpas
	 * @param popup
	 *            the popup
	 */
	public SimpleLineChartsMultiYAxis(Double strokeWidthpas, boolean popup) {
		if (strokeWidthpas != null) {
			this.strokeWidth = strokeWidthpas;
		}
		
		isRebuildBaseChart = false;
		isReShowingSerie = false;
	  isPopupFullVisible = popup;
		NumberAxis xAxis = new NumberAxis();
		NumberAxis yAxis = new NumberAxis();
		this.baseChart = new LineChart<Number, Number>(xAxis, yAxis);
		((NumberAxis) baseChart.getYAxis()).setTickLabelFormatter(new MyYaxisDoubleFormatter());
		baseChart.setId("baseChart");

		setFixedAxisWidth(baseChart);
		setAlignment(Pos.CENTER_LEFT);
		backgroundCharts.clear();
		backgroundCharts.addListener((Observable observable) -> {
			// System.out.println("an event occurs in backgroundCharts");
			rebuildChart();
		});
		
		detailsWindow = new AnchorPane();
		getChildren().add(detailsWindow);
		detailsWindow.getChildren().add(detailsPopup);
		
		bindMouseEvents(baseChart, this.strokeWidth);

		normalizeBaseChartBound();
		rebuildChart();

	}

	/**
	 * Normalizes the baseChart with lowerBounds/upperBounds on X and Y Axis.
	 *
	 * return nothing
	 */
	private void normalizeBaseChartBound() {

		Double dblLowBoundX = Double.MAX_VALUE;
		Double dblMaxBoundX = Double.MIN_VALUE;
		Double dblLowBound = Double.MAX_VALUE;
		Double dblMaxBound = Double.MIN_VALUE;
		/* initialing this , there is no Data */

		if (baseChart.getData().isEmpty())
			return;
		for (Series<Number, Number> series : baseChart.getData()) {
			// series.getData().sort(Comparator.comparingDouble(d ->
			// d.getYValue().doubleValue()));
			if (dblLowBound > this.hmYSortedSeries.get(series.getName()).getData().get(0).getYValue().doubleValue()) {
				dblLowBound = this.hmYSortedSeries.get(series.getName()).getData().get(0).getYValue().doubleValue();
			}
			if (dblMaxBound < this.hmYSortedSeries.get(series.getName()).getData().get(series.getData().size() - 1)
					.getYValue().doubleValue()) {
				dblMaxBound = this.hmYSortedSeries.get(series.getName()).getData().get(series.getData().size() - 1)
						.getYValue().doubleValue();
			}
		}
		for (Series<Number, Number> series : baseChart.getData()) {
			// series.getData().sort(Comparator.comparingDouble(d ->
			// d.getXValue().doubleValue()));
			if (dblLowBoundX > this.hmXSortedSeries.get(series.getName()).getData().get(0).getXValue().doubleValue()) {
				dblLowBoundX = this.hmXSortedSeries.get(series.getName()).getData().get(0).getXValue().doubleValue();
			}
			if (dblMaxBoundX < this.hmXSortedSeries.get(series.getName()).getData().get(series.getData().size() - 1)
					.getXValue().doubleValue()) {
				dblMaxBoundX = this.hmXSortedSeries.get(series.getName()).getData().get(series.getData().size() - 1)
						.getXValue().doubleValue();
			}
		}

		((NumberAxis) baseChart.getYAxis()).setLowerBound(dblLowBound);
		((NumberAxis) baseChart.getYAxis()).setUpperBound(dblMaxBound);
		((NumberAxis) baseChart.getXAxis()).setLowerBound(dblLowBoundX);
		((NumberAxis) baseChart.getXAxis()).setUpperBound(dblMaxBoundX);

		((NumberAxis) baseChart.getXAxis()).setTickUnit((((NumberAxis) baseChart.getXAxis()).getUpperBound()
				- ((NumberAxis) baseChart.getXAxis()).getLowerBound()) / nbXTicks);
		((NumberAxis) baseChart.getYAxis()).setTickUnit((((NumberAxis) baseChart.getYAxis()).getUpperBound()
				- ((NumberAxis) baseChart.getYAxis()).getLowerBound()) / nbYTicks);

	}

	/**
	 * Style symbol.
	 *
	 * @param chart
	 *            the chart
	 * @param lineColor
	 *            the line color
	 */
	private final void styleSymbol(LineChart<Number, Number> chart, Color lineColor) {
		chart.setCreateSymbols(true);
		// TODO Auto-generated method stub
		ObservableList<Series<Number, Number>> list = chart.getData();

		for (XYChart.Series<Number, Number> series : list) {
			for (int index = 0; index < series.getData().size(); index++) {
				XYChart.Data<Number, Number> dataPoint = series.getData().get(index);
				Node lineSymbol = dataPoint.getNode().lookup(".chart-line-symbol");
				lineSymbol.setStyle(
						// lineSymbol.getStyle() +
						"\n -fx-background-insets: 0, 2;\n" + "   \n" + "     -fx-background-color: "
								+ toRGBCode(lineColor) + " #000000; -fx-padding: 3px;");
			}

		}

	}

	/**
	 * Style base chart.
	 *
	 * @param baseChart
	 *            the base chart
	 */
	private final void styleBaseChart(LineChart<Number, Number> baseChart) {
		baseChart.setCreateSymbols(true);
		baseChart.setLegendVisible(false);
		baseChart.getXAxis().setAutoRanging(false);
		baseChart.getXAxis().setAnimated(false);
		baseChart.getYAxis().setAnimated(false);

	}

	/**
	 * Style serie line.
	 *
	 * @param chart
	 *            the chart
	 * @param series
	 *            the series
	 * @param lineColor
	 *            the line color
	 */
	private final void styleSerieLine(LineChart<Number, Number> chart, XYChart.Series<Number, Number> series,
			Color lineColor) {

		Node line = series.getNode().lookup(".chart-series-line");
		if (null == line) {
			System.out.println(" line is null");
		} else
			line.setStyle("-fx-stroke: " + toRGBCode(lineColor) + "; -fx-stroke-width: " + strokeWidth + ";");
	}

	/**
	 * Style base chart line.
	 *
	 * @param lineColor
	 *            the line color
	 */
	private final void styleBaseChartLine(Color lineColor) {
		baseChart.getYAxis().lookup(".axis-label")
				.setStyle("-fx-text-fill: " + toRGBCode(lineColor) + "; -fx-font-weight: bold;");
		ObservableList<Series<Number, Number>> list = baseChart.getData();
		for (XYChart.Series<Number, Number> series : list) {
			Node line = series.getNode().lookup(".chart-series-line");

			line.setStyle("-fx-stroke: " + toRGBCode(lineColor) + "; -fx-stroke-width: " + strokeWidth + ";");

		}

	}

	/**
	 * Style chart line.
	 *
	 * @param chart
	 *            the chart
	 * @param lineColor
	 *            the line color
	 */
	private final void styleChartLine(LineChart<Number, Number> chart, Color lineColor) {
		chart.getYAxis().lookup(".axis-label")
				.setStyle("-fx-text-fill: " + toRGBCode(lineColor) + "; -fx-font-weight: bold;");

	}

	/**
	 * To RGB code.
	 *
	 * @param color
	 *            the color
	 * @return the string
	 */
	private final String toRGBCode(Color color) {
		return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));
	}

	/**
	 * To RGB code and alpha transparence.
	 *
	 * @param color
	 *            the color
	 * @param trans
	 *            the transparence
	 * @return the string for css fx style
	 */
	private final String toRGBCodeTrans(Color color, String trans) {
		return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255)) + trans;
	}

	/**
	 * Sets the fixed axis width.
	 *
	 * @param chart
	 *            the chart
	 */
	private final void setFixedAxisWidth(LineChart<Number, Number> chart) {
		chart.getYAxis().setPrefWidth(yAxisWidth);
		chart.getYAxis().setMaxWidth(yAxisWidth);
	}

	/**
	 * Rebuild basechart and all LineChart of the stackPane.
	 */
	protected final void rebuildChart() {
		getChildren().clear();

		getChildren().add(resizeBaseChart(baseChart));

		for (TupleStrLC tuple : backgroundCharts) {

			getChildren().add(resizeBackgroundChart(tuple));
		}

		getChildren().add(detailsWindow);
	}

	/**
	 * Resize baseChart.
	 *
	 * @param lineChart
	 *            the line chart
	 * @return the node
	 */
	private final Node resizeBaseChart(LineChart<Number, Number> lineChart) {
		HBox hBox = new HBox(baseChart);
		hBox.setAlignment(Pos.CENTER_LEFT);
		hBox.prefHeightProperty().bind(heightProperty());
		hBox.prefWidthProperty().bind(widthProperty());

		lineChart.minWidthProperty()
				.bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size()));
		lineChart.prefWidthProperty()
				.bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size()));
		lineChart.maxWidthProperty()
				.bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size()));
		styleSymbol(baseChart, chartColorMap.get(baseChart));
		((NumberAxis) baseChart.getXAxis()).setAutoRanging(false);
		((NumberAxis) baseChart.getXAxis()).setTickUnit((((NumberAxis) baseChart.getXAxis()).getUpperBound()
				- ((NumberAxis) baseChart.getXAxis()).getLowerBound()) / nbXTicks);
		((NumberAxis) baseChart.getYAxis()).setTickUnit((((NumberAxis) baseChart.getYAxis()).getUpperBound()
				- ((NumberAxis) baseChart.getYAxis()).getLowerBound()) / nbYTicks);
		styleSymbol(lineChart, chartColorMap.get(lineChart));
		return lineChart;
	}

	/**
	 * Resize background lineCharts.
	 *
	 * @param tuple
	 *            the tuple
	 * @return the node
	 */
	private final Node resizeBackgroundChart(TupleStrLC tuple) {
		HBox hBox = new HBox(tuple.lineChart);
		hBox.setAlignment(Pos.CENTER_LEFT);
		hBox.prefHeightProperty().bind(heightProperty());
		hBox.prefWidthProperty().bind(widthProperty());
		hBox.setMouseTransparent(true);

		tuple.lineChart.minWidthProperty()
				.bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size()));
		tuple.lineChart.prefWidthProperty()
				.bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size()));
		tuple.lineChart.maxWidthProperty()
				.bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size()));

		tuple.lineChart.translateXProperty().bind(baseChart.getYAxis().widthProperty());
		tuple.lineChart.getYAxis().setTranslateX((yAxisWidth + yAxisSeparation) * backgroundCharts.indexOf(tuple));

		styleSymbol(tuple.lineChart, chartColorMap.get(tuple.lineChart));

		return hBox;
	}

	/**
	 * Bind mouse events.
	 *
	 * @param baseChart
	 *            the base chart
	 * @param strokeWidth
	 *            the stroke width
	 */
	private final void bindMouseEvents(LineChart<Number, Number> baseChart, Double strokeWidth) {

		detailsWindow.prefHeightProperty().bind(heightProperty());
		detailsWindow.prefWidthProperty().bind(widthProperty());
		detailsWindow.setMouseTransparent(true);

		setOnMouseMoved(null);
		setMouseTransparent(false);

		final Axis<Number> xAxis = baseChart.getXAxis();
		final Axis<Number> yAxis = baseChart.getYAxis();

		final Line xLine = new Line();
		final Line yLine = new Line();
		yLine.setFill(Color.GRAY);
		xLine.setFill(Color.GRAY);
		yLine.setStrokeWidth(strokeWidth / 2);
		xLine.setStrokeWidth(strokeWidth / 2);
		xLine.setVisible(false);
		yLine.setVisible(false);

		final Node chartBackground = baseChart.lookup(".chart-plot-background");
		for (Node n : chartBackground.getParent().getChildrenUnmodifiable()) {
			if (n != chartBackground && n != xAxis && n != yAxis) {
				n.setMouseTransparent(true);
			}
		}
		chartBackground.setCursor(Cursor.CROSSHAIR);
		chartBackground.setOnMouseEntered((event) -> {
			chartBackground.getOnMouseMoved().handle(event);
			detailsPopup.setVisible(true);
			xLine.setVisible(true);
			yLine.setVisible(true);
			detailsWindow.getChildren().addAll(xLine, yLine);

		});
		chartBackground.setOnMouseExited((event) -> {
			detailsPopup.setVisible(false);
			xLine.setVisible(false);
			yLine.setVisible(false);
			detailsWindow.getChildren().removeAll(xLine, yLine);
		});
		chartBackground.setOnMouseMoved(event -> {

			
			// set hmBoldAxis to false
			for (Entry<LineChart<Number, Number>, Boolean> entry : hmBoldAxis.entrySet()) {
				hmBoldAxis.put(entry.getKey(), false);
			}
			double x = event.getX() + chartBackground.getLayoutX();
			double y = event.getY() + chartBackground.getLayoutY();

			xLine.setStartX(10);
			xLine.setEndX(detailsWindow.getWidth() - 10);
			xLine.setStartY(y + 5);
			xLine.setEndY(y + 5);

			yLine.setStartX(x + 5);
			yLine.setEndX(x + 5);
			yLine.setStartY(10);
			yLine.setEndY(detailsWindow.getHeight() - 10);
			detailsPopup.showChartDescriptionBySeries(event);
			if (y + detailsPopup.getHeight() + 10 < getHeight()) {
				AnchorPane.setTopAnchor(detailsPopup, y + 10);
			} else {
				AnchorPane.setTopAnchor(detailsPopup, y - 10 - detailsPopup.getHeight());
			}

			if (x + detailsPopup.getWidth() + 10 < getWidth()) {
				AnchorPane.setLeftAnchor(detailsPopup, x + 10);
			} else {
				AnchorPane.setLeftAnchor(detailsPopup, x - 10 - detailsPopup.getWidth());
			}
		});
	}

	/**
	 * Retrieve series by name.
	 *
	 * @param name
	 *            the name
	 * @param lc
	 *            the lc
	 * @return the int
	 */
	private int retrieveSeriesByName(String name, LineChart<Number, Number> lc) {

		int j = 0;
		for (XYChart.Series<Number, Number> series : lc.getData()) {
			if (series.getName().equals(name)) {
				return j;
			}
			j++;
		}

		return -1;
	}

	/**
	 * Re show.
	 *
	 * @param nameSerie the name serie
	 */
	public void reShow(String nameSerie) {
		// Verify that this nameSerie is hidden
		if (!this.hmVisibleSeries.containsKey(nameSerie))
			return;
		TupleBoolLC tupBoolLC = this.hmVisibleSeries.get(nameSerie);
		tupBoolLC.isVisible = true;
		this.hmVisibleSeries.put(nameSerie, tupBoolLC);
		this.isReShowingSerie = true;
		Color col = this.chartColorMap.get(tupBoolLC.lineChart);

		Series<Number, Number> serie = this.hmXSortedSeries.get(nameSerie);
		if (tupBoolLC.lineChart == baseChart) {
			tupBoolLC.lineChart.getYAxis().setSide(Side.LEFT);

		} else {
			tupBoolLC.lineChart.getYAxis().setSide(Side.RIGHT);
			tupBoolLC.lineChart.getYAxis().setTickLabelGap(0);
		}

		
		
		setXLabel("      ");
		int idx = retrieveLineChartInbackgroundCharts(tupBoolLC.lineChart);
		
		if (idx >= 0) {

			this.backgroundCharts.remove(idx);
		}
		

		tupBoolLC.lineChart.setAnimated(true);
		
		serie.setName(nameSerie);
		LineChart<Number,Number> retLC=this.addSeries(serie, col, tupBoolLC.lineChart.getYAxis().getLabel());
		this.isReShowingSerie = false;
		retLC.setCreateSymbols(true);
		
		retLC.setAnimated(true);
		if (baseChart.getXAxis().getLabel()!= null) retLC.getXAxis().setLabel("    ");

	}

	/**
	 * Hide serie.
	 *
	 * @param nameSeries
	 *            the name series
	 */
	public void hideSerie(String nameSeries) {
		TupleBoolLC tupBoolLC = this.hmVisibleSeries.get(nameSeries);
		if (null == tupBoolLC) {
			// System.out.println("no tubBoolLC in hmVisibleSeries for :" + nameSeries);
			return;
		}
		LineChart<Number, Number> lc = tupBoolLC.lineChart;
		int idx = retrieveSeriesByName(nameSeries, lc);

		if (idx >= 0) {

			tupBoolLC.lineChart.getData().remove(idx);
			tupBoolLC.isVisible = false;
			this.hmVisibleSeries.put(nameSeries, tupBoolLC);

			/* If it is the only series, remove also the LineChart from the StackPane */
			if (tupBoolLC.lineChart.getData().isEmpty()) {
				if (lc != baseChart) {
					// System.out.println("removing linechart in backgroundCharts");
					int idx2 = retrieveLineChartInbackgroundCharts(lc);

					TupleStrLC tup2 = this.backgroundCharts.remove(idx2);
					// System.out.println("removed linechart -> " +
					// tup2.lineChart.getYAxis().getLabel());

				} else {
					// the first Line Chart of this.backgroundCharts becomes the baseChart
					isRebuildBaseChart = true;
					NumberAxis xAxis = (NumberAxis) baseChart.getXAxis();

					baseChart = new LineChart<Number, Number>(xAxis, this.backgroundCharts.get(0).lineChart.getYAxis());
					// baseChart = tup1.lineChart;
					baseChart.getYAxis().setSide(Side.LEFT);
					for (Series<Number, Number> series : this.backgroundCharts.get(0).lineChart.getData()) {
						this.addSeries(series, chartColorMap.get(this.backgroundCharts.get(0).lineChart),
								baseChart.getYAxis().getLabel());
					}
					this.backgroundCharts.remove(0);
					isRebuildBaseChart = false;

					baseChart.setId("baseChart");
					bindMouseEvents(baseChart, this.strokeWidth);
				}

			}

		}

	}

	/**
	 * Retrieve line chart in background charts.
	 *
	 * @param lc
	 *            the lc
	 * @return the int
	 */
	private int retrieveLineChartInbackgroundCharts(LineChart<Number, Number> lc) {
		int j = 0;
		for (TupleStrLC tup : backgroundCharts) {
			if (tup.unit.equals(lc.getYAxis().getLabel())) {
				// if (tup.lineChart == lc) {
				return j;
			}
			j++;
		}

		return -1;
	}

	/**
	 * Adds the series.
	 *
	 * @param series            the series
	 * @param lineColor            the line color
	 * @param unit            the unit
	 * @return the line chart
	 */
	public final LineChart<Number,Number> addSeries(XYChart.Series<Number, Number> series, Color lineColor, String unit) {

		// create chart
		// Search first if a LineChar with the same unit exists
		// try with the baseChart
		NumberAxis xAxis = new NumberAxis();
		NumberAxis yAxis = new NumberAxis();
		LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
		;
		// testing if there is a seies in the baseChart :
		if (!this.hmXSortedSeries.containsKey(series.getName())) {
			Series<Number, Number> serie1 = new Series<Number, Number>();
			serie1.setName(series.getName());
			serie1.setData(series.getData().sorted(Comparator.comparingLong(d -> d.getXValue().longValue())));
			hmXSortedSeries.put(series.getName(), serie1);
			Series<Number, Number> serie2 = new Series<Number, Number>();
			serie2.setName(series.getName());
			serie2.setData(series.getData().sorted(Comparator.comparingDouble(d -> d.getYValue().doubleValue())));
			hmYSortedSeries.put(series.getName(), serie2);

		} else {
			if (!isRebuildBaseChart && !this.isReShowingSerie) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setResizable(true);

				alert.setTitle("Information Dialog");

				alert.setHeaderText(null);
				alert.setContentText("Duplicate Name for Series : " + series.getName() + "\nThis series is skipped.");

				alert.showAndWait();
				return null;
			}
		}

		DoubleProperty minSeries = new SimpleDoubleProperty();
		minSeries.setValue(hmXSortedSeries.get(series.getName()).getData().get(0).getXValue());
		DoubleProperty maxSeries = new SimpleDoubleProperty();
		maxSeries
				.setValue(hmXSortedSeries.get(series.getName()).getData().get(series.getData().size() - 1).getXValue());
		double min = minSeries.doubleValue();
		// minSeries.setValue(min - (maxSeries.doubleValue() - min) / 50);
		// sorting series by YValue (Number)

		DoubleProperty minYSeries = new SimpleDoubleProperty();
		minYSeries.setValue(hmYSortedSeries.get(series.getName()).getData().get(0).getYValue());
		DoubleProperty maxYSeries = new SimpleDoubleProperty();
		maxYSeries
				.setValue(hmYSortedSeries.get(series.getName()).getData().get(series.getData().size() - 1).getYValue());
		if (baseChart.getData().isEmpty()) {
			// System.out.println("Init baseChart with serie :" + series.getName());

			lineChart = baseChart;
			lineChart.getYAxis().setLabel(unit);
			chartColorMap.put(lineChart, lineColor);
			styleBaseChart(lineChart);
			styleBaseChartLine(lineColor);
			styleSymbol(lineChart, lineColor);

		} else {

			if (unit.equals(baseChart.getYAxis().getLabel())) {
				lineChart = baseChart;
				if (((NumberAxis) baseChart.getXAxis()).lowerBoundProperty().getValue().doubleValue() > minSeries
						.doubleValue()) {

					((NumberAxis) baseChart.getXAxis()).lowerBoundProperty().bindBidirectional(minSeries);

				}
				if (((NumberAxis) baseChart.getXAxis()).upperBoundProperty().getValue().doubleValue() < maxSeries
						.doubleValue()) {

					((NumberAxis) baseChart.getXAxis()).upperBoundProperty().bindBidirectional(maxSeries);

				}
				styleBaseChart(lineChart);
				styleBaseChartLine(lineColor);
				styleSymbol(lineChart, lineColor);

			} else {
				for (TupleStrLC tuple : this.backgroundCharts) {
					if (unit.equals(tuple.unit)) {
						lineChart = tuple.lineChart;
						break;
					}
				}
			}
			if (null == lineChart.getYAxis().getLabel()) {

				yAxis.setAutoRanging(true);
				// style x-axis
				xAxis.setAutoRanging(false);
				xAxis.setVisible(false);
				xAxis.setOpacity(0.0); // somehow the upper setVisible does not work

				if (((NumberAxis) baseChart.getXAxis()).lowerBoundProperty().getValue().doubleValue() >= minSeries
						.doubleValue()) {

					((NumberAxis) baseChart.getXAxis()).lowerBoundProperty().bindBidirectional(minSeries);

				}
				if (((NumberAxis) baseChart.getXAxis()).upperBoundProperty().getValue().doubleValue() <= maxSeries
						.doubleValue()) {

					((NumberAxis) baseChart.getXAxis()).upperBoundProperty().bindBidirectional(maxSeries);

				}

				xAxis.lowerBoundProperty().bindBidirectional(((NumberAxis) baseChart.getXAxis()).lowerBoundProperty());
				xAxis.upperBoundProperty().bindBidirectional(((NumberAxis) baseChart.getXAxis()).upperBoundProperty());
				//

				// xAxis.tickUnitProperty().bind(((NumberAxis)
				// baseChart.getXAxis()).tickUnitProperty());
				// style y-axis
				yAxis.setSide(Side.RIGHT);
				yAxis.setLabel(unit);
				lineChart = new LineChart<Number, Number>(xAxis, yAxis);
				lineChart.setAnimated(true);
				lineChart.setLegendVisible(false);

				if (!SimpleLineChartsMultiYAxis.hmBoldAxis.containsKey(lineChart)) {
					hmBoldAxis.put(lineChart, false);
				}
				((NumberAxis) lineChart.getYAxis()).setTickLabelFormatter(new MyYaxisDoubleFormatter());
				((NumberAxis) lineChart.getXAxis()).setTickUnit((((NumberAxis) lineChart.getXAxis()).getUpperBound()
						- ((NumberAxis) lineChart.getXAxis()).getLowerBound()) / nbXTicks);
				((NumberAxis) lineChart.getYAxis()).setTickUnit((((NumberAxis) lineChart.getYAxis()).getUpperBound()
						- ((NumberAxis) lineChart.getYAxis()).getLowerBound()) / nbYTicks);
				chartColorMap.put(lineChart, lineColor);

				((NumberAxis) baseChart.getXAxis()).setTickUnit(
						(xAxis.upperBoundProperty().doubleValue() - xAxis.lowerBoundProperty().doubleValue())
								/ nbXTicks);
				

				this.backgroundCharts.add(new TupleStrLC(unit, lineChart));

			}
		}
		lineChart.getYAxis().setAutoRanging(true);

		if (((NumberAxis) lineChart.getYAxis()).lowerBoundProperty().getValue().doubleValue() >= minYSeries
				.doubleValue()) {

			// ((NumberAxis)
			// lineChart.getYAxis()).lowerBoundProperty().bindBidirectional(minYSeries);
			((NumberAxis) lineChart.getYAxis()).setLowerBound(minYSeries.doubleValue());

		}
		if (((NumberAxis) lineChart.getYAxis()).upperBoundProperty().getValue().doubleValue() <= maxYSeries
				.doubleValue()) {

			((NumberAxis) lineChart.getYAxis()).setUpperBound(maxYSeries.doubleValue());
		}

		((NumberAxis) lineChart.getYAxis()).setTickLabelFormatter(new MyYaxisDoubleFormatter());
		((NumberAxis) lineChart.getXAxis()).setTickUnit((((NumberAxis) lineChart.getXAxis()).getUpperBound()
				- ((NumberAxis) lineChart.getXAxis()).getLowerBound()) / nbXTicks);
		((NumberAxis) lineChart.getYAxis()).setTickUnit((((NumberAxis) lineChart.getYAxis()).getUpperBound()
				- ((NumberAxis) lineChart.getYAxis()).getLowerBound()) / nbYTicks);
		lineChart.getData().add(series);
		lineChart.getYAxis().setAutoRanging(false);

		styleBackgroundChart(lineChart, lineColor);
		styleSerieLine(lineChart, series, lineColor);
		setFixedAxisWidth(lineChart);
		styleSymbol(lineChart, lineColor);

		hmVisibleSeries.put(series.getName(), new TupleBoolLC(true, lineChart));
		// System.out.println("hmVisible -> " +
		// hmVisibleSeries.get(series.getName()).lineChart.getYAxis().getLabel());
		return lineChart;
	}

	/**
	 * Sets the x label.
	 *
	 * @param label
	 *            the new x label
	 */
	public final void setXLabel(String label) {
		if (null == this.timeConverter) {
			baseChart.getXAxis().setLabel("Units");
			for (SimpleLineChartsMultiYAxis.TupleStrLC tpl : getBackgroundCharts()) {
				tpl.lineChart.getXAxis().setLabel("Units");
			}
		} else {
			label = ((MyLongToDateConverter) timeConverter).getTimeFormat();
			baseChart.getXAxis().setLabel(label);
			for (SimpleLineChartsMultiYAxis.TupleStrLC tpl : getBackgroundCharts()) {
				tpl.lineChart.getXAxis().setLabel(label);
			}
		}
	}

	/**
	 * Style background chart.
	 *
	 * @param lineChart
	 *            the line chart
	 * @param lineColor
	 *            the line color
	 */
	protected final void styleBackgroundChart(LineChart<Number, Number> lineChart, Color lineColor) {
		styleChartLine(lineChart, lineColor);

		Node contentBackground = lineChart.lookup(".chart-content").lookup(".chart-plot-background");
		contentBackground.setStyle("-fx-background-color: transparent;");

		lineChart.setVerticalZeroLineVisible(false);
		lineChart.setHorizontalZeroLineVisible(false);
		lineChart.setVerticalGridLinesVisible(false);
		lineChart.setHorizontalGridLinesVisible(false);
		lineChart.setCreateSymbols(true);
	}

	/**
	 * The Class TupleBoolLC.
	 */
	final class TupleBoolLC {

		/** The isVisible. */
		public Boolean isVisible;

		/** The line chart. */
		public LineChart<Number, Number> lineChart;

		/**
		 * Instantiates a new tuple TupleBoolLC.
		 *
		 * @param isVisible
		 *            the isVisible
		 * @param lineChart
		 *            the line chart
		 */
		public TupleBoolLC(Boolean isVisible, LineChart<Number, Number> lineChart) {
			super();
			this.isVisible = isVisible;
			this.lineChart = lineChart;
		}

	}

	/**
	 * The Class TupleStrLC.
	 */
	final class TupleStrLC {

		/** The unit. */
		public String unit;

		/** The line chart. */
		public LineChart<Number, Number> lineChart;

		/**
		 * Instantiates a new tuple str LC.
		 *
		 * @param unit
		 *            the unit
		 * @param lineChart
		 *            the line chart
		 */
		public TupleStrLC(String unit, LineChart<Number, Number> lineChart) {
			super();
			this.unit = unit;
			this.lineChart = lineChart;
		}

	}

	/**
	 * The Class DetailsPopup.
	 */
	private final class DetailsPopup extends VBox {

		/**
		 * Instantiates a new details popup.
		 */
		private DetailsPopup() {
			setStyle(
					"-fx-border-width: 1px; -fx-padding: 5 5 5 5px; -fx-border-color: gray; -fx-background-color: whitesmoke;");
			setVisible(false);
		}

		/**
		 * Bold line near cursor.
		 *
		 * @param event
		 *            the event
		 */
		public void boldLineNearCursor(MouseEvent event) {
			// TODO Auto-generated method stub
			Long xValueLong = Math.round((double) baseChart.getXAxis().getValueForDisplay(event.getX()));

			int nbSeriesBadeChartPopup = baseChart.getData().size();
			for (int i = 0; i < nbSeriesBadeChartPopup; i++) {

				Number yValueForChart = getYValueForXBySeries(baseChart, xValueLong, i);
				if (null == yValueForChart) {
					continue;
				}
				Number yValueLower = (normalizeYValue(baseChart, event.getY() - 5));
				Number yValueUpper = (normalizeYValue(baseChart, event.getY() + 5));
				Label seriesName = new Label(
						baseChart.getData().get(i).getName() + " -> " + baseChart.getYAxis().getLabel());
				seriesName.setTextFill(chartColorMap.get(baseChart));
				Number yValueUnderMouse = ((double) baseChart.getYAxis().getValueForDisplay(event.getY()));
				if (isMouseNearLine(yValueForChart, yValueUnderMouse,
						Math.abs(yValueLower.doubleValue() - yValueUpper.doubleValue()), baseChart)) {
					seriesName.setStyle("-fx-font-weight: bold");
					SimpleLineChartsMultiYAxis.hmBoldAxis.put(baseChart, true);
					String oldStyle = baseChart.getData().get(i).getNode().getStyle();

					String newStyleString = newStyle(oldStyle, "-fx-stroke-width: 3px",
							"-fx-stroke-width:\\s*\\d+(px|\\.?\\d+?)");
					// lc.lineChart.getData().get(i).getNode().setStyle(
					baseChart.getData().get(i).getNode().setStyle(newStyleString);

					// Color for the Axis border with a transparency 80 => 0.5

					baseChart.getYAxis().setStyle("-fx-border-width: 0 10 0 0;-fx-border-color: "
							+ toRGBCodeTrans(chartColorMap.get(baseChart), "80") + " ;");

				} else {
					String oldStyle = baseChart.getData().get(i).getNode().getStyle();

					String newStyleString = newStyle(oldStyle, "-fx-stroke-width: 1px",
							"-fx-stroke-width:\\*\\d+(px|\\.?\\d+?)");

					baseChart.getData().get(i).getNode().setStyle(newStyleString);

					if (SimpleLineChartsMultiYAxis.hmBoldAxis.containsKey(baseChart)
							&& !SimpleLineChartsMultiYAxis.hmBoldAxis.get(baseChart)) {
						baseChart.getYAxis().setStyle("-fx-border-width: 0 1 0 1;");
					}

				}

			}

			for (TupleStrLC lc : backgroundCharts) {
				int nbSeries = lc.lineChart.getData().size();
				for (int i = 0; i < nbSeries; i++) {
					Number yValueForChart = getYValueForXBySeries(lc.lineChart, xValueLong, i);
					if (null == yValueForChart) {
						continue;
					}
					Number yValueLower = (normalizeYValue(lc.lineChart, event.getY() - 5));
					Number yValueUpper = (normalizeYValue(lc.lineChart, event.getY() + 5));
					Label seriesName = new Label(
							lc.lineChart.getData().get(i).getName() + " -> " + lc.lineChart.getYAxis().getLabel());
					seriesName.setTextFill(chartColorMap.get(lc.lineChart));
					Number yValueUnderMouse = ((double) lc.lineChart.getYAxis().getValueForDisplay(event.getY()));
					if (isMouseNearLine(yValueForChart, yValueUnderMouse,
							Math.abs(yValueLower.doubleValue() - yValueUpper.doubleValue()), lc.lineChart)) {
						seriesName.setStyle("-fx-font-weight: bold");
						SimpleLineChartsMultiYAxis.hmBoldAxis.put(lc.lineChart, true);
						String oldStyle = lc.lineChart.getData().get(i).getNode().getStyle();

						String newStyleString = newStyle(oldStyle, "-fx-stroke-width: 3px",
								"-fx-stroke-width:\\s*\\d+(px|\\.?\\d+?)");
						
						lc.lineChart.getData().get(i).getNode().setStyle(newStyleString);

						// Color for the Axis border with a transparency 80 => 0.5

						lc.lineChart.getYAxis().setStyle("-fx-border-width: 0 0 0 10;-fx-border-color: "
								+ toRGBCodeTrans(chartColorMap.get(lc.lineChart), "80") + " ;");

					} else {
						String oldStyle = lc.lineChart.getData().get(i).getNode().getStyle();

						String newStyleString = newStyle(oldStyle, "-fx-stroke-width: 1px",
								"-fx-stroke-width:\\s*\\d+(px|\\.?\\d+?)");
						
						lc.lineChart.getData().get(i).getNode().setStyle(newStyleString);

						if (SimpleLineChartsMultiYAxis.hmBoldAxis.containsKey(lc.lineChart)
								&& !SimpleLineChartsMultiYAxis.hmBoldAxis.get(lc.lineChart)) {
							lc.lineChart.getYAxis().setStyle("-fx-border-width: 0 1 0 1;");
						}

					}
				}
			}
		}

		/**
		 * Show chart description by series.
		 *
		 * @param event
		 *            the event
		 */
		public void showChartDescriptionBySeries(MouseEvent event) {
			getChildren().clear();
			if (isPopupMuted) {
				setVisible(false);
				boldLineNearCursor(event);
				return;
			}

			Long xValueLong = Math.round((double) baseChart.getXAxis().getValueForDisplay(event.getX()));

			int nbSeriesBadeChartPopup = baseChart.getData().size();
			for (int i = 0; i < nbSeriesBadeChartPopup; i++) {
				HBox baseChartPopupRow = buildPopupRowBySeries(event, xValueLong, baseChart, i);

				if (baseChartPopupRow != null) {
					getChildren().add(baseChartPopupRow);
				}
			}

			for (TupleStrLC lc : backgroundCharts) {
				int nbSeries = lc.lineChart.getData().size();
				for (int i = 0; i < nbSeries; i++) {
					HBox popupRow = buildPopupRowBySeries(event, xValueLong, lc.lineChart, i);

					if (popupRow == null)
						continue;

					getChildren().add(popupRow);
				}
			}

		}

		/**
		 * New style.
		 *
		 * @param OldStyle the old style
		 * @param addNewStyle the add new style
		 * @param regextoReplace the regexto replace
		 * @return the string
		 */
		private String newStyle(String OldStyle, String addNewStyle, String regextoReplace) {
			StringBuilder strB = new StringBuilder();
			String[] arrayOldStyle = OldStyle.split(";");
			
			for (String frag : arrayOldStyle) {
				if (frag.trim().matches(regextoReplace)) {
					/* Skip the Value */
					
				} else {

					strB.append(frag).append(";");
				}

			}
		/* Added only Once at the end */
				strB.append(addNewStyle).append(";");

			return strB.toString();
		}

		/**
		 * Builds the popup row by series.
		 *
		 * @param event
		 *            the event
		 * @param xValueLong
		 *            the x value long
		 * @param lineChart
		 *            the line chart
		 * @param idxSeries
		 *            the idx series
		 * @return the h box
		 */
		private HBox buildPopupRowBySeries(MouseEvent event, Long xValueLong, LineChart<Number, Number> lineChart,
				int idxSeries) {
			Label seriesName = new Label(
					lineChart.getData().get(idxSeries).getName() + " -> " + lineChart.getYAxis().getLabel());
			seriesName.setTextFill(chartColorMap.get(lineChart));

			Number yValueForChart = getYValueForXBySeries(lineChart, xValueLong, idxSeries);
			if (yValueForChart == null) {
				return null;
			}
			Number yValueLower = (normalizeYValue(lineChart, event.getY() - 5));
			Number yValueUpper = (normalizeYValue(lineChart, event.getY() + 5));
			Number yValueUnderMouse = ((double) lineChart.getYAxis().getValueForDisplay(event.getY()));

			// make series name bold when mouse is near given chart's line

			if (isMouseNearLine(yValueForChart, yValueUnderMouse,
					Math.abs(yValueLower.doubleValue() - yValueUpper.doubleValue()), lineChart)) {
				seriesName.setStyle("-fx-font-weight: bold");
				SimpleLineChartsMultiYAxis.hmBoldAxis.put(lineChart, true);
				String oldStyle = lineChart.getData().get(idxSeries).getNode().getStyle();

				String newStyleString = newStyle(oldStyle, "-fx-stroke-width: 3px",
						"-fx-stroke-width:\\s*\\d+(px|\\.?\\d+?)");

				
				lineChart.getData().get(idxSeries).getNode().setStyle(newStyleString);
				if (lineChart.equals(baseChart)) {
					// Color for the Axis border with a transparency 80 => 0.5

					lineChart.getYAxis().setStyle("-fx-border-width: 0 10 0 0;-fx-border-color: "
							+ toRGBCodeTrans(chartColorMap.get(lineChart), "80") + " ;");
				} else {
					lineChart.getYAxis().setStyle("-fx-border-width: 0 0 0 10;-fx-border-color:"
							+ toRGBCodeTrans(chartColorMap.get(lineChart), "80") + " ;");
				}
			} else {
				String oldStyle = lineChart.getData().get(idxSeries).getNode().getStyle();

				String newStyleString = newStyle(oldStyle, "-fx-stroke-width: 1px",
						"-fx-stroke-width:\\s*\\d+(px|\\.?\\d+?)");

				lineChart.getData().get(idxSeries).getNode().setStyle(newStyleString);
				if (SimpleLineChartsMultiYAxis.hmBoldAxis.containsKey(lineChart)
						&& !SimpleLineChartsMultiYAxis.hmBoldAxis.get(lineChart)) {
					lineChart.getYAxis().setStyle("-fx-border-width: 0 1 0 1;");
				}
				if (!isPopupFullVisible)
					return null; // the line is not visible

			}
			HBox popupRow = null;
			if (null != timeConverter) {
				popupRow = new HBox(10, seriesName, new Label(
						"[" + timeConverter.toString(xValueLong) + ";" + dblFormatter.toString(yValueForChart) + "]"));
			} else {
				
				popupRow = new HBox(10, seriesName, new Label(
						"[" + dblFormatter.toString(xValueLong.doubleValue()) + ";" + dblFormatter.toString(yValueForChart) + "]"));
			}
			return popupRow;
		}

		/**
		 * Normalize Y value.
		 *
		 * @param lineChart
		 *            the line chart
		 * @param value
		 *            the value
		 * @return the double
		 */
		private double normalizeYValue(LineChart<Number, Number> lineChart, double value) {
			Double val = (Double) lineChart.getYAxis().getValueForDisplay(value);
			if (val == null) {
				return 0;
			} else {
				return val;
			}
		}

		/**
		 * Checks if is mouse near line.
		 *
		 * @param realYValue
		 *            the real Y value
		 * @param yValueUnderMouse
		 *            the y value under mouse
		 * @param tolerance
		 *            the tolerance
		 * @param linechart
		 *            the lineChart to inspect
		 * @return true, if is mouse near line
		 */
		private boolean isMouseNearLine(Number realYValue, Number yValueUnderMouse, Double tolerance,
				LineChart<Number, Number> linechart) {

			return (Math.abs(yValueUnderMouse.doubleValue() - realYValue.doubleValue()) < Math.abs(tolerance));
		}

		/**
		 * Gets the y value for X by series.
		 *
		 * @param chart
		 *            the chart
		 * @param xValue
		 *            the x value
		 * @param idxSerie
		 *            the idx serie
		 * @return the y value for X by series
		 */
		public Number getYValueForXBySeries(LineChart<Number, Number> chart, Number xValue, int idxSerie) {
			ObservableList<XYChart.Data<Number, Number>> dataList = ((ObservableList<XYChart.Data<Number, Number>>) ((XYChart.Series<Number, Number>) chart
					.getData().get(idxSerie)).getData());
			Series<Number, Number> serie = (Series<Number, Number>) chart.getData().get(idxSerie);

			int idx = 0;

			for (XYChart.Data<Number, Number> data : hmXSortedSeries.get(serie.getName()).getData()) { // Series must be
																										// sorted on
																										// XValues
				// linear interpolation
				if (data.getXValue().doubleValue() >= xValue.doubleValue()) {
					if (idx == 0) {
						return (Number) null;
					}
					XYChart.Data<Number, Number> dataPrecedent = dataList.get(idx - 1);

					Double realValue = dataPrecedent.getYValue().doubleValue()
							+ ((data.getYValue().doubleValue() - dataPrecedent.getYValue().doubleValue()))
									* (xValue.doubleValue() - dataPrecedent.getXValue().doubleValue())
									/ (data.getXValue().doubleValue() - dataPrecedent.getXValue().doubleValue());
					return (Number) realValue;
				}
				idx++;
			}
			return null;
		}

	}

}
