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

import java.awt.Toolkit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jlp.javafx.common.NearEvent;
import org.jlp.javafx.ext.MyLongToDateConverter;
import org.jlp.javafx.ext.MyYaxisDoubleFormatter;

import javafx.application.Platform;
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
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
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
	private boolean isClearing = false;
	/** The is re showing serie. */
	private boolean isReShowingSerie = false;
	/** The is rebuild base chart. */
	private boolean isRebuildBaseChart = false;
	/** The hm hmVisibleSeries series. */
	public Map<String, TupleBoolLC> hmVisibleSeries = new HashMap<String, TupleBoolLC>();

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

	/** The nb Y axis. */
	public int nbYAxis = 0;

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

	/**
	 * The chart color map. Key is the name of the series.
	 * <p>
	 * This name must be unique
	 * </p>
	 * .
	 */
	public final Map<String, Color> chartColorMap = new HashMap<String, Color>();

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
	private double strokeWidth = 1.0;

	/**
	 * The details window. Shows or not the values X/Y of series, bound with
	 * isPopupVisible
	 */
	private AnchorPane detailsWindow = null;;

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

			if (!isClearing)//
			{

				rebuildChart();
			}
		});

		detailsWindow = new AnchorPane();
		getChildren().add(detailsWindow);
		detailsWindow.getChildren().add(detailsPopup);

		bindMouseEvents(baseChart, this.strokeWidth);

		normalizeBaseChartBound();
		rebuildChart();
		this.setMaxWidth(Toolkit.getDefaultToolkit().getScreenSize().getWidth());

	}

	/**
	 * Sets the selected.
	 *
	 * @param nameSerie
	 *            the name serie
	 * @param selected
	 *            the selected
	 */
	public void setSelected(String nameSerie, Boolean selected) {
		TupleBoolLC tup = this.hmVisibleSeries.get(nameSerie);
		if (null == tup)
			return;
		tup.isSelected = selected;
		hmVisibleSeries.put(nameSerie, tup);
		// find NameSeries
		int idx = 0;
		for (Series<Number, Number> series : tup.lineChart.getData()) {
			if (series.getName().equals(nameSerie))
				break;
			idx++;
		}
		String oldStyle = "";

		if (idx < tup.lineChart.getData().size())
			oldStyle = tup.lineChart.getData().get(idx).getNode().getStyle();
		String newStyleString = "";
		if (selected) {
			newStyleString = newStyle(oldStyle, "-fx-stroke-width: 3px", "-fx-stroke-width:\\*\\d+(px|\\.?\\d+?)");
		} else {
			newStyleString = newStyle(oldStyle, "-fx-stroke-width: 1px", "-fx-stroke-width:\\*\\d+(px|\\.?\\d+?)");
		}

		tup.lineChart.getData().get(idx).getNode().setStyle(newStyleString);
		// styleChartLine(baseChart,
		// chartColorMap.getOrDefault(tup.lineChart.getData().get(idx).getName(),
		// Color.BLACK));

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
								+ toRGBCode(this.chartColorMap.getOrDefault(series.getName(), lineColor))
								+ "; -fx-padding: 2px;");
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
		baseChart.setAnimated(false);
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
			line.setStyle("-fx-stroke: " + toRGBCode(this.chartColorMap.getOrDefault(series.getName(), lineColor))
					+ "; -fx-stroke-width: " + strokeWidth + ";");
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

			line.setStyle("-fx-stroke: " + toRGBCode(this.chartColorMap.getOrDefault(series.getName(), lineColor))
					+ "; -fx-stroke-width: " + strokeWidth + ";");

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
	 * Rebuild chart.
	 *
	 * @param minX
	 *            the min X
	 * @param maxX
	 *            the max X
	 */
	protected final void rebuildChart(Number minX, Number maxX) {
		getChildren().clear();

		getChildren().add(resizeBaseChart(minX, maxX));

		for (TupleStrLC tuple : backgroundCharts) {

			getChildren().add(resizeBackgroundChart(tuple, minX, maxX));
		}

		getChildren().add(detailsWindow);

	}

	/**
	 * Resize baseChart.
	 *
	 * @param minX
	 *            the min X
	 * @param maxX
	 *            the max X
	 * @return the node
	 */
	private final Node resizeBaseChart(Number minX, Number maxX) {
		HBox hBox = new HBox(baseChart);
		hBox.setAlignment(Pos.CENTER_LEFT);
		hBox.prefHeightProperty().bind(heightProperty());
		hBox.prefWidthProperty().bind(widthProperty());

		((NumberAxis) baseChart.getXAxis()).setUpperBound(maxX.doubleValue());
		((NumberAxis) baseChart.getXAxis()).setLowerBound(minX.doubleValue());
		baseChart.minWidthProperty()
				.bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size()));
		baseChart.prefWidthProperty()
				.bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size()));
		baseChart.maxWidthProperty()
				.bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size()));
		// styleSymbol(baseChart,
		// chartColorMap.get(baseChart.getData().get(0).getName()));
		((NumberAxis) baseChart.getXAxis()).setAutoRanging(false);
		((NumberAxis) baseChart.getXAxis()).setTickUnit(maxX.doubleValue() - minX.doubleValue() / nbXTicks);
		((NumberAxis) baseChart.getYAxis()).setTickUnit((((NumberAxis) baseChart.getYAxis()).getUpperBound()
				- ((NumberAxis) baseChart.getYAxis()).getLowerBound()) / nbYTicks);
		// styleSymbol(lineChart,
		// chartColorMap.get(lineChart.getData().get(0).getName()));
		return baseChart;
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
		// styleSymbol(baseChart,
		// chartColorMap.get(baseChart.getData().get(0).getName()));
		((NumberAxis) baseChart.getXAxis()).setAutoRanging(false);
		((NumberAxis) baseChart.getXAxis()).setTickUnit((((NumberAxis) baseChart.getXAxis()).getUpperBound()
				- ((NumberAxis) baseChart.getXAxis()).getLowerBound()) / nbXTicks);
		((NumberAxis) baseChart.getYAxis()).setTickUnit((((NumberAxis) baseChart.getYAxis()).getUpperBound()
				- ((NumberAxis) baseChart.getYAxis()).getLowerBound()) / nbYTicks);
		// styleSymbol(lineChart,
		// chartColorMap.get(lineChart.getData().get(0).getName()));
		return lineChart;
	}

	/**
	 * Resize background chart.
	 *
	 * @param tuple
	 *            the tuple
	 * @param minX
	 *            the min X
	 * @param maxX
	 *            the max X
	 * @return the node
	 */
	private final Node resizeBackgroundChart(TupleStrLC tuple, Number minX, Number maxX) {
		HBox hBox = new HBox(tuple.lineChart);
		hBox.setAlignment(Pos.CENTER_LEFT);
		hBox.prefHeightProperty().bind(heightProperty());
		hBox.prefWidthProperty().bind(widthProperty());
		hBox.setMouseTransparent(true);
		tuple.lineChart.getYAxis().setSide(Side.RIGHT);
		tuple.lineChart.minWidthProperty()
				.bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size()));
		tuple.lineChart.prefWidthProperty()
				.bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size()));
		tuple.lineChart.maxWidthProperty()
				.bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size()));

		tuple.lineChart.translateXProperty().bind(baseChart.getYAxis().widthProperty());
		tuple.lineChart.getYAxis().setTranslateX((yAxisWidth + yAxisSeparation) * backgroundCharts.indexOf(tuple));

		// styleSymbol(tuple.lineChart,
		// chartColorMap.get(tuple.lineChart.getData().get(0).getName()));

		return hBox;
	}

	/**
	 * Resize background lineCharts.
	 *
	 * @param tuple
	 *            the tuple
	 * @return the node
	 */
	private final Node resizeBackgroundChart(TupleStrLC tuple) {
		tuple.lineChart.getYAxis().setSide(Side.RIGHT);
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

		// styleSymbol(tuple.lineChart,
		// chartColorMap.get(tuple.lineChart.getData().get(0).getName()));

		return hBox;
	}

	/** The x line. */
	final public Line xLine = new Line();

	/** The y line. */
	final public Line yLine = new Line();

	/**
	 * Unbind mouse events.
	 */
	public final void unbindMouseEvents() {
		setOnMouseMoved(null);
		final Node chartBackground = baseChart.lookup(".chart-plot-background");
		chartBackground.setOnMouseEntered(null);
		chartBackground.setOnMouseExited(null);
		chartBackground.setOnMouseMoved(null);
		xLine.setVisible(false);
		yLine.setVisible(false);
	}

	/**
	 * Bind mouse events.
	 */
	public final void bindMouseEvents() {
		bindMouseEvents(baseChart, strokeWidth);
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

		// final Line xLine = new Line();
		// final Line yLine = new Line();
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
			if (event.isPrimaryButtonDown()) {

			} else {
				/* Zooming or Drag/drop a file */
				chartBackground.getOnMouseMoved().handle(event);
				detailsPopup.setVisible(true);
				xLine.setVisible(true);
				yLine.setVisible(true);
				if (!detailsWindow.getChildren().contains(xLine)) {
					detailsWindow.getChildren().addAll(xLine, yLine);
				}

			}
			event.consume();
		});
		chartBackground.setOnMouseExited((event) -> {

			detailsPopup.setVisible(false);
			xLine.setVisible(false);
			yLine.setVisible(false);

			detailsWindow.getChildren().removeAll(xLine, yLine);

		});
		chartBackground.setOnMouseMoved(event -> {
			if (event.isPrimaryButtonDown() || event.isSecondaryButtonDown()) {

				event.consume();
			} else {
				Long deb = System.currentTimeMillis();
				double x = event.getX() + chartBackground.getLayoutX();
				double y = event.getY() + chartBackground.getLayoutY();
				// set hmBoldAxis to false
				for (Entry<LineChart<Number, Number>, Boolean> entry : hmBoldAxis.entrySet()) {
					hmBoldAxis.put(entry.getKey(), false);
				}

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
				// System.out.println("duration on Mouse Move = " + (System.currentTimeMillis()
				// - deb));
			}
			event.consume();
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
	 * @param nameSerie
	 *            the name serie
	 */
	public void reShow(String nameSerie) {
		// Verify that this nameSerie is hidden
		if (!this.hmVisibleSeries.containsKey(nameSerie))
			return;
		TupleBoolLC tupBoolLC = this.hmVisibleSeries.get(nameSerie);
		tupBoolLC.isVisible = true;
		this.hmVisibleSeries.put(nameSerie, tupBoolLC);
		this.isReShowingSerie = true;
		Color col = Color.BLACK;
		// if (!tupBoolLC.lineChart.getData().isEmpty()) {
		col = this.chartColorMap.getOrDefault(nameSerie, Color.BLACK);
		// }

		Series<Number, Number> serie = this.hmXSortedSeries.get(nameSerie);

		setXLabel("      ");
		String unit = tupBoolLC.lineChart.getYAxis().getLabel();

		serie.setName(nameSerie);

		// System.out
		// .println("Before adding series nb Series= " +
		// tupBoolLC.lineChart.getData().size() + " unit=" + unit);
		for (Series<Number, Number> serieBis : tupBoolLC.lineChart.getData()) {
			if (serieBis.getName().equals(nameSerie)) {
				tupBoolLC.lineChart.getData().remove(serieBis);
				break;
			}
		}

		LineChart<Number, Number> rtLC = addSeries(serie, col, unit);

		this.hmVisibleSeries.put(nameSerie, new TupleBoolLC(true, tupBoolLC.isSelected, rtLC, tupBoolLC.translation));
		// this.hmVisibleSeries.put(nameSerie, tupBoolLC);
		// System.out.println(
		// "After adding series nb Series= " +
		// this.hmVisibleSeries.get(nameSerie).lineChart.getData().size());
		rtLC.setAnimated(false);

		this.isReShowingSerie = false;
		rtLC.setCreateSymbols(true);

		if (baseChart.getXAxis().getLabel() != null)
			rtLC.getXAxis().setLabel("    ");

	}

	/**
	 * Rebound basechart X axis.
	 */
	private void reboundBasechartXAxis() {
		Double dblLowBoundX = Double.MAX_VALUE;
		Double dblMaxBoundX = Double.MIN_VALUE;
		TupleBoolLC tupBoolStr = null;

		for (String nameSeries : this.hmXSortedSeries.keySet()) {
			tupBoolStr = this.hmVisibleSeries.get(nameSeries);
			if (null != tupBoolStr && tupBoolStr.isVisible) {

				Series<Number, Number> serie = hmXSortedSeries.get(nameSeries);
				if (dblLowBoundX >= serie.getData().get(0).getXValue().doubleValue()) {
					dblLowBoundX = serie.getData().get(0).getXValue().doubleValue();
				}
				if (dblMaxBoundX <= serie.getData().get(serie.getData().size() - 1).getXValue().doubleValue()) {
					dblMaxBoundX = serie.getData().get(serie.getData().size() - 1).getXValue().doubleValue();
				}
			} else {

				if (null == tupBoolStr)
					System.out.println("tupBoolStr not retrieved");
			}
		}
		Double marge = (dblMaxBoundX - dblLowBoundX) / 20;
		((NumberAxis) baseChart.getXAxis())
				.setLowerBound(Math.min(((NumberAxis) baseChart.getXAxis()).getLowerBound(), dblLowBoundX - marge));
		((NumberAxis) baseChart.getXAxis())
				.setUpperBound(Math.max(((NumberAxis) baseChart.getXAxis()).getUpperBound(), dblMaxBoundX + marge));
	}

	/**
	 * Reset.
	 */
	public void reset() {
		for (Entry<String, TupleBoolLC> entry : this.hmVisibleSeries.entrySet()) {
			if (!entry.getValue().isVisible) {
				this.reShow(entry.getKey());
			}
		}
	}

	/**
	 * Clear.
	 */
	public void clear() {
		this.getChildren().clear();
		//
		hmVisibleSeries.clear();
		hmXSortedSeries.clear();
		hmYSortedSeries.clear();
		hmBoldAxis.clear();

		isClearing = true;
		//
		this.backgroundCharts.clear();
		this.chartColorMap.clear();

		// recreating baseChart
		NumberAxis xAxis = new NumberAxis();
		NumberAxis yAxis = new NumberAxis();
		this.baseChart = new LineChart<Number, Number>(xAxis, yAxis);
		((NumberAxis) baseChart.getYAxis()).setTickLabelFormatter(new MyYaxisDoubleFormatter());
		baseChart.setId("baseChart");

		setFixedAxisWidth(baseChart);
		setAlignment(Pos.CENTER_LEFT);

		detailsWindow.getChildren().clear();
		getChildren().add(detailsWindow);
		detailsWindow.getChildren().add(detailsPopup);

		bindMouseEvents(baseChart, this.strokeWidth);

		normalizeBaseChartBound();
		rebuildChart();
		isClearing = false;
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
			// System.out.println(
			// "Hiding :Series :" + nameSeries + " with idx =" + idx + "/" +
			// tupBoolLC.lineChart.getData().size());

			lc.getData().remove(idx);

			TupleBoolLC tupBoolLCBis = new TupleBoolLC(false, tupBoolLC.isSelected, lc, tupBoolLC.translation);

			this.hmVisibleSeries.put(nameSeries, tupBoolLCBis);

			/* If it is the only series, remove also the LineChart from the StackPane */
			if (lc.getData().isEmpty()) {
				if (lc != baseChart) {
					// System.out.println("removing linechart in backgroundCharts");
					int idx2 = retrieveLineChartInbackgroundCharts(lc);
					// System.out.println("removed linechart -> idx2=" + idx2 + " ; nameSerie=" +
					// nameSeries);
					if (idx2 >= 0) {
						TupleStrLC tup2 = this.backgroundCharts.remove(idx2);

						// System.out.println("removed linechart -> " +
						// tup2.lineChart.getYAxis().getLabel());
					}
				}

			}

		}
		reboundBasechartXAxis();
	}

	/**
	 * Del serie.
	 *
	 * @param nameSerie
	 *            the name serie
	 */
	public void delSerie(String nameSerie) {
		TupleBoolLC tupBoolLC = this.hmVisibleSeries.get(nameSerie);
		/* First hideSeries a lot of work is done */
		hideSerie(nameSerie);
		this.hmVisibleSeries.remove(nameSerie);
		this.hmXSortedSeries.remove(nameSerie);
		this.hmYSortedSeries.remove(nameSerie);
		this.chartColorMap.remove(nameSerie);

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
	 * @param series
	 *            the series
	 * @param lineColor
	 *            the line color
	 * @param unit
	 *            the unit
	 * @return the line chart
	 */
	public final LineChart<Number, Number> addSeries(XYChart.Series<Number, Number> series, Color lineColor,
			String unit) {

		// long deb1 = System.currentTimeMillis();
		// create chart
		// Search first if a LineChar with the same unit exists
		// try with the baseChart
		NumberAxis xAxis = new NumberAxis();
		NumberAxis yAxis = new NumberAxis();
		LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
		;
		// testing if there is a series in the baseChart :
		// Series<Number, Number> serie1 = new Series<Number, Number>();
		if (series.getData().isEmpty()) {
			System.out.println("the series " + series.getName() + " is empty");
			return lineChart;
		}

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
		if (!this.isReShowingSerie) {
			this.chartColorMap.put(series.getName(), lineColor);
		}
		// long deb2 = System.currentTimeMillis();
		// System.out.println("indx1 sorting series duration =" + (deb2 - deb1));
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
		// Long deb3 = System.currentTimeMillis();
		if (baseChart.getData().isEmpty()
				&& (null == baseChart.getYAxis().getLabel() || unit.equals(baseChart.getYAxis().getLabel()))) {
			// System.out.println("Init baseChart with serie :" + series.getName());

			lineChart = baseChart;
			lineChart.getYAxis().setLabel(unit);

			styleBaseChart(lineChart);
			styleBaseChartLine(lineColor);
			styleSymbol(lineChart, lineColor);
			// Modif 1 Première serie
			Double marge = (maxSeries.get() - minSeries.get()) / 20;
			minSeries.set(minSeries.get() - marge);
			maxSeries.set(maxSeries.get() + marge);
			((NumberAxis) baseChart.getXAxis()).lowerBoundProperty().bindBidirectional(minSeries);
			((NumberAxis) baseChart.getXAxis()).upperBoundProperty().bindBidirectional(maxSeries);
			// Long deb4 = System.currentTimeMillis();
			// System.out.println("indx2 basechart empty duaration =" + (deb4 - deb3));
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
				Double marge = (maxSeries.get() - minSeries.get()) / 20;
				minSeries.set(minSeries.get() - marge);
				maxSeries.set(maxSeries.get() + marge);
				((NumberAxis) baseChart.getXAxis()).lowerBoundProperty().bindBidirectional(minSeries);
				((NumberAxis) baseChart.getXAxis()).upperBoundProperty().bindBidirectional(maxSeries);
				styleBaseChart(lineChart);
				styleBaseChartLine(lineColor);
				styleSymbol(lineChart, lineColor);
				// Long deb5 = System.currentTimeMillis();
				// System.out.println("indx2 basechart existing duaration =" + (deb5 - deb3));
			} else {
				for (TupleStrLC tuple : this.backgroundCharts) {
					if (unit.equals(tuple.unit)) {
						lineChart = tuple.lineChart;
						break;
					}
				}
			}
			if (null == lineChart.getYAxis().getLabel()) {
				// Long deb6 = System.currentTimeMillis();

				yAxis.setAutoRanging(true);
				// style x-axis
				xAxis.setAutoRanging(false);
				xAxis.setVisible(false);
				xAxis.setOpacity(0.0); // somehow the upper setVisible does not work
				nbYAxis++;

				if (((NumberAxis) baseChart.getXAxis()).lowerBoundProperty().getValue().doubleValue() >= minSeries
						.doubleValue()) {

					((NumberAxis) baseChart.getXAxis()).lowerBoundProperty().bindBidirectional(minSeries);

				}
				if (((NumberAxis) baseChart.getXAxis()).upperBoundProperty().getValue().doubleValue() <= maxSeries
						.doubleValue()) {

					((NumberAxis) baseChart.getXAxis()).upperBoundProperty().bindBidirectional(maxSeries);

				}
				Double marge = (maxSeries.get() - minSeries.get()) / 20;
				minSeries.set(minSeries.get() - marge);
				maxSeries.set(maxSeries.get() + marge);
				// ((NumberAxis)
				// baseChart.getXAxis()).lowerBoundProperty().bindBidirectional(minSeries);
				// ((NumberAxis)
				// baseChart.getXAxis()).upperBoundProperty().bindBidirectional(maxSeries);

				((NumberAxis) baseChart.getXAxis()).setLowerBound(
						Math.min(((NumberAxis) baseChart.getXAxis()).getLowerBound() - marge, minSeries.get() - marge));
				((NumberAxis) baseChart.getXAxis()).setUpperBound(
						Math.max(((NumberAxis) baseChart.getXAxis()).getUpperBound() + marge, maxSeries.get() + marge));
				xAxis.lowerBoundProperty().bindBidirectional(((NumberAxis) baseChart.getXAxis()).lowerBoundProperty());
				xAxis.upperBoundProperty().bindBidirectional(((NumberAxis) baseChart.getXAxis()).upperBoundProperty());

				//

				// xAxis.tickUnitProperty().bind(((NumberAxis)
				// baseChart.getXAxis()).tickUnitProperty());
				// style y-axis
				yAxis.setSide(Side.RIGHT);
				yAxis.setLabel(unit);
				lineChart = new LineChart<Number, Number>(xAxis, yAxis);
				lineChart.setAnimated(false);
				lineChart.setLegendVisible(false);

				if (!SimpleLineChartsMultiYAxis.hmBoldAxis.containsKey(lineChart)) {
					hmBoldAxis.put(lineChart, false);
				}
				((NumberAxis) lineChart.getYAxis()).setTickLabelFormatter(new MyYaxisDoubleFormatter());
				((NumberAxis) lineChart.getXAxis()).setTickUnit((((NumberAxis) lineChart.getXAxis()).getUpperBound()
						- ((NumberAxis) lineChart.getXAxis()).getLowerBound()) / nbXTicks);
				((NumberAxis) lineChart.getYAxis()).setTickUnit((((NumberAxis) lineChart.getYAxis()).getUpperBound()
						- ((NumberAxis) lineChart.getYAxis()).getLowerBound()) / nbYTicks);

				((NumberAxis) baseChart.getXAxis()).setTickUnit(
						(xAxis.upperBoundProperty().doubleValue() - xAxis.lowerBoundProperty().doubleValue())
								/ nbXTicks);

				this.backgroundCharts.add(new TupleStrLC(unit, lineChart));
				// Long deb7 = System.currentTimeMillis();
				// System.out.println("indx3 other chart new duaration =" + (deb7 - deb6));
			}
		}
		// Long deb8 = System.currentTimeMillis();

		lineChart.getYAxis().setAutoRanging(false);

		if (((NumberAxis) lineChart.getYAxis()).lowerBoundProperty().getValue().doubleValue() >= minYSeries
				.doubleValue()) {

			// ((NumberAxis)
			// lineChart.getYAxis()).lowerBoundProperty().bindBidirectional(minYSeries);
			((NumberAxis) lineChart.getYAxis()).setLowerBound(minYSeries.doubleValue());

		}
		minYSeries.setValue(((NumberAxis) lineChart.getYAxis()).getLowerBound());
		if (((NumberAxis) lineChart.getYAxis()).upperBoundProperty().getValue().doubleValue() <= maxYSeries
				.doubleValue()) {

			((NumberAxis) lineChart.getYAxis()).setUpperBound(maxYSeries.doubleValue());
		}

		maxYSeries.setValue(((NumberAxis) lineChart.getYAxis()).getUpperBound());
		Double marge = (maxYSeries.get() - minYSeries.get()) / 20;
		minYSeries.set(minYSeries.get() - marge);
		maxYSeries.set(maxYSeries.get() + marge);
		((NumberAxis) lineChart.getYAxis()).lowerBoundProperty().bindBidirectional(minYSeries);
		((NumberAxis) lineChart.getYAxis()).upperBoundProperty().bindBidirectional(maxYSeries);

		((NumberAxis) lineChart.getYAxis()).setTickLabelFormatter(new MyYaxisDoubleFormatter());
		((NumberAxis) lineChart.getXAxis()).setTickUnit((((NumberAxis) lineChart.getXAxis()).getUpperBound()
				- ((NumberAxis) lineChart.getXAxis()).getLowerBound()) / nbXTicks);
		((NumberAxis) lineChart.getYAxis()).setTickUnit((((NumberAxis) lineChart.getYAxis()).getUpperBound()
				- ((NumberAxis) lineChart.getYAxis()).getLowerBound()) / nbYTicks);
		// Long deb9 = System.currentTimeMillis();
		// System.out.println("indx4 avant addSeries duaration =" + (deb9 - deb8));
		// lineChart.getData().add(series);
		// Long deb10 = System.currentTimeMillis();
		// System.out.println("indx5 apres addSeries duaration =" + (deb10 - deb9));
		lineChart.getYAxis().setAutoRanging(false);

		styleBackgroundChart(lineChart, lineColor);
		// styleSerieLine(lineChart, series, lineColor);
		setFixedAxisWidth(lineChart);
		// styleSymbol(lineChart, lineColor);

		hmVisibleSeries.put(series.getName(), new TupleBoolLC(true, false, lineChart, 0.0));
		// System.out.println("hmVisible -> " +
		// hmVisibleSeries.get(series.getName()).lineChart.getYAxis().getLabel());
		// Long deb11 = System.currentTimeMillis();
		// System.out.println("indx6 un pe avant fin addSeries duaration =" + (deb11 -
		// deb10));
		final LineChart<Number, Number> lc = lineChart;

		Platform.runLater(() -> {

			lc.getData().add(series);
			styleSerieLine(lc, series, lineColor);
			styleSymbol(lc, lineColor);
			if (lc == baseChart) {
				// System.out.println("rebounding XAxis");
				reboundBasechartXAxis();
				// rebuildChart();
			} else {
				if (null != baseChart.getXAxis().getLabel() && baseChart.getXAxis().getLabel().length() > 0) {
					lc.getXAxis().setLabel("                      ");
				}
			}

		});

		// Long deb12 = System.currentTimeMillis();
		// System.out.println("indx7 fin addSeries duaration =" + (deb12 - deb11));
		// return lineChart;
		return lc;
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
	 * New style.
	 *
	 * @param OldStyle
	 *            the old style
	 * @param addNewStyle
	 *            the add new style
	 * @param regextoReplace
	 *            the regexto replace
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
	 * Do a translation on XAxis.
	 *
	 * @param nameSeries
	 *            the name series
	 * @param translat
	 *            the translat
	 */
	public void doTranlation(String nameSeries, double translat) {
		/*
		 * hashMap to update public Map<String, TupleBoolLC> hmVisibleSeries = new
		 * HashMap<String, TupleBoolLC>();
		 * 
		 * 
		 * public Map<String, Series<Number, Number>> hmXSortedSeries = new
		 * HashMap<String, Series<Number, Number>>();
		 * 
		 * 
		 * public Map<String, Series<Number, Number>> hmYSortedSeries = new
		 * HashMap<String, Series<Number, Number>>();
		 */
		TupleBoolLC tup1 = hmVisibleSeries.get(nameSeries);

		tup1.translation = tup1.translation + translat;

		hmVisibleSeries.put(nameSeries, tup1);
		Series<Number, Number> serie = hmXSortedSeries.get(nameSeries);

		for (Data<Number, Number> data : serie.getData()) {
			data.setXValue(data.getXValue().doubleValue() + translat);

		}

		hmXSortedSeries.put(nameSeries, serie);

		this.hideSerie(nameSeries);

		this.reShow(nameSeries);
	}

	/**
	 * The Class TupleBoolLC.
	 */
	final public class TupleBoolLC {

		/** The isVisible. */
		public Boolean isVisible;

		/** The line chart. */
		public LineChart<Number, Number> lineChart;

		/** The is selected. */
		public Boolean isSelected;

		/** The translation. */
		public Double translation = 0.0d;

		/**
		 * Instantiates a new tuple TupleBoolLC.
		 *
		 * @param isVisible
		 *            the isVisible
		 * @param isSelected
		 *            the is selected
		 * @param lineChart
		 *            the line chart
		 * @param translation
		 *            the translation
		 */
		public TupleBoolLC(Boolean isVisible, Boolean isSelected, LineChart<Number, Number> lineChart,
				double translation) {
			super();
			this.isVisible = isVisible;
			this.lineChart = lineChart;
			this.isSelected = isSelected;
			this.translation = translation;
		}

	}

	/**
	 * The Class TupleStrLC.
	 */
	final public class TupleStrLC {

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
			if (event.getButton() == MouseButton.PRIMARY || event.isPrimaryButtonDown()) {
				event.consume();
				// System.out.println("primaryButton down");
				return; /* Draging mode */
			} else {

				boolean isNear = false;
				// System.out.println("boldLineNearCursor");
				// TODO Auto-generated method stub
				Long xValueLong = Math.round((double) baseChart.getXAxis().getValueForDisplay(event.getX()));

				int nbSeriesBaseChartPopup = baseChart.getData().size();
				for (int i = 0; i < nbSeriesBaseChartPopup; i++) {

					Number yValueForChart = getYValueForXBySeries(baseChart, xValueLong, i);
					if (null == yValueForChart) {
						continue;
					}
					Number yValueLower = (normalizeYValue(baseChart, event.getY() - 5));
					Number yValueUpper = (normalizeYValue(baseChart, event.getY() + 5));
					Label seriesName = new Label(
							baseChart.getData().get(i).getName() + " -> " + baseChart.getYAxis().getLabel());
					seriesName
							.setTextFill(chartColorMap.getOrDefault(baseChart.getData().get(i).getName(), Color.BLACK));
					Number yValueUnderMouse = ((double) baseChart.getYAxis().getValueForDisplay(event.getY()));
					if (isMouseNearLine(yValueForChart, yValueUnderMouse,
							Math.abs(yValueLower.doubleValue() - yValueUpper.doubleValue()), baseChart)) {
						isNear = true;

						this.fireEvent(new NearEvent(baseChart.getData().get(i).getName(), true));
						seriesName.setStyle("-fx-font-weight: bold");
						SimpleLineChartsMultiYAxis.hmBoldAxis.put(baseChart, true);
						String oldStyle = baseChart.getData().get(i).getNode().getStyle();

						String newStyleString = newStyle(oldStyle, "-fx-stroke-width: 3px",
								"-fx-stroke-width:\\s*\\d+(px|\\.?\\d+?)");
						// lc.lineChart.getData().get(i).getNode().setStyle(
						baseChart.getData().get(i).getNode().setStyle(newStyleString);

						// Color for the Axis border with a transparency 80 => 0.5

						baseChart.getYAxis()
								.setStyle("-fx-border-width: 0 10 0 0;-fx-border-color: "
										+ toRGBCodeTrans(chartColorMap
												.getOrDefault(baseChart.getData().get(i).getName(), Color.BLACK), "80")
										+ " ;");
						styleChartLine(baseChart,
								chartColorMap.getOrDefault(baseChart.getData().get(i).getName(), Color.BLACK));

					} else {
						if (!isNear) {
							String oldStyle = baseChart.getData().get(i).getNode().getStyle();
							this.fireEvent(new NearEvent(baseChart.getData().get(i).getName(), false));
							String newStyleString = "";

							if (!hmVisibleSeries.get(baseChart.getData().get(i).getName()).isSelected) {
								newStyleString = newStyle(oldStyle, "-fx-stroke-width: 1px",
										"-fx-stroke-width:\\*\\d+(px|\\.?\\d+?)");

								baseChart.getData().get(i).getNode().setStyle(newStyleString);
								styleChartLine(baseChart,
										chartColorMap.getOrDefault(baseChart.getData().get(i).getName(), Color.BLACK));
							} else {
								newStyleString = newStyle(oldStyle, "-fx-stroke-width: 3px",
										"-fx-stroke-width:\\*\\d+(px|\\.?\\d+?)");
								baseChart.getData().get(i).getNode().setStyle(newStyleString);
								styleChartLine(baseChart,
										chartColorMap.getOrDefault(baseChart.getData().get(i).getName(), Color.BLACK));
							}

							if (SimpleLineChartsMultiYAxis.hmBoldAxis.containsKey(baseChart)
									&& !SimpleLineChartsMultiYAxis.hmBoldAxis.get(baseChart)) {
								baseChart.getYAxis().setStyle("-fx-border-width: 0 1 0 1;");
							}

						}
					}

				}
				isNear = false;
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
						seriesName.setTextFill(
								chartColorMap.getOrDefault(lc.lineChart.getData().get(i).getName(), Color.BLACK));
						Number yValueUnderMouse = ((double) lc.lineChart.getYAxis().getValueForDisplay(event.getY()));
						if (isMouseNearLine(yValueForChart, yValueUnderMouse,
								Math.abs(yValueLower.doubleValue() - yValueUpper.doubleValue()), lc.lineChart)) {
							isNear = true;
							this.fireEvent(new NearEvent(lc.lineChart.getData().get(i).getName(), true));
							// System.out.println("yes near :" + lc.lineChart.getData().get(i).getName());
							seriesName.setStyle("-fx-font-weight: bold");
							SimpleLineChartsMultiYAxis.hmBoldAxis.put(lc.lineChart, true);
							String oldStyle = lc.lineChart.getData().get(i).getNode().getStyle();

							String newStyleString = newStyle(oldStyle, "-fx-stroke-width: 3px",
									"-fx-stroke-width:\\s*\\d+(px|\\.?\\d+?)");

							lc.lineChart.getData().get(i).getNode().setStyle(newStyleString);

							// Color for the Axis border with a transparency 80 => 0.5

							lc.lineChart.getYAxis()
									.setStyle("-fx-border-width: 0 0 0 10;-fx-border-color: "
											+ toRGBCodeTrans(chartColorMap.getOrDefault(
													lc.lineChart.getData().get(i).getName(), Color.BLACK), "80")
											+ " ;");

							styleChartLine(lc.lineChart,
									chartColorMap.getOrDefault(lc.lineChart.getData().get(i).getName(), Color.BLACK));

						} else {
							if (!isNear) {
								String oldStyle = lc.lineChart.getData().get(i).getNode().getStyle();
								this.fireEvent(new NearEvent(lc.lineChart.getData().get(i).getName(), false));
								String newStyleString = "";
								if (SimpleLineChartsMultiYAxis.hmBoldAxis.containsKey(lc.lineChart)
										&& !SimpleLineChartsMultiYAxis.hmBoldAxis.get(lc.lineChart)) {
									lc.lineChart.getYAxis().setStyle("-fx-border-width: 0 1 0 1;");
								}

								if (!hmVisibleSeries.get(lc.lineChart.getData().get(i).getName()).isSelected) {
									newStyleString = newStyle(oldStyle, "-fx-stroke-width: 1px",
											"-fx-stroke-width:\\*\\d+(px|\\.?\\d+?)");

									lc.lineChart.getData().get(i).getNode().setStyle(newStyleString);
									styleChartLine(lc.lineChart, chartColorMap
											.getOrDefault(lc.lineChart.getData().get(i).getName(), Color.BLACK));
								} else {
									newStyleString = newStyle(oldStyle, "-fx-stroke-width: 3px",
											"-fx-stroke-width:\\*\\d+(px|\\.?\\d+?)");
									lc.lineChart.getData().get(i).getNode().setStyle(newStyleString);
									styleChartLine(lc.lineChart, chartColorMap
											.getOrDefault(lc.lineChart.getData().get(i).getName(), Color.BLACK));
								}
							}
						}
					}
				}
			}
		}

		/** The global is near. */
		private boolean globalIsNear = false;

		/**
		 * Show chart description by series.
		 *
		 * @param event
		 *            the event
		 */
		public void showChartDescriptionBySeries(MouseEvent event) {

			if (isPopupMuted) {
				setVisible(false);

				event.consume();
				return;
			}
			if (!event.isPrimaryButtonDown()) {

				boldLineNearCursor(event);
			} else {

				event.consume();
				return;
			}
			getChildren().clear();
			Long xValueLong = Math.round((double) baseChart.getXAxis().getValueForDisplay(event.getX()));
			globalIsNear = false;
			int nbSeriesBaseChartPopup = baseChart.getData().size();
			for (int i = 0; i < nbSeriesBaseChartPopup; i++) {
				HBox baseChartPopupRow = buildPopupRowBySeries(event, xValueLong, baseChart, i);

				if (baseChartPopupRow != null) {
					getChildren().add(baseChartPopupRow);
				}
			}
			globalIsNear = false;
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
			seriesName
					.setTextFill(chartColorMap.getOrDefault(lineChart.getData().get(idxSeries).getName(), Color.BLACK));

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
				globalIsNear = true;
				this.fireEvent(new NearEvent(lineChart.getData().get(idxSeries).getName(), true));
				seriesName.setStyle("-fx-font-weight: bold");
				SimpleLineChartsMultiYAxis.hmBoldAxis.put(lineChart, true);
				String oldStyle = lineChart.getData().get(idxSeries).getNode().getStyle();

				String newStyleString = newStyle(oldStyle, "-fx-stroke-width: 3px",
						"-fx-stroke-width:\\s*\\d+(px|\\.?\\d+?)");

				lineChart.getData().get(idxSeries).getNode().setStyle(newStyleString);
				if (lineChart.equals(baseChart)) {
					// Color for the Axis border with a transparency 80 => 0.5

					lineChart.getYAxis().setStyle("-fx-border-width: 0 10 0 0;-fx-border-color: " + toRGBCodeTrans(
							chartColorMap.getOrDefault(lineChart.getData().get(idxSeries).getName(), Color.BLACK), "80")
							+ " ;");
				} else {
					lineChart.getYAxis().setStyle("-fx-border-width: 0 0 0 10;-fx-border-color:" + toRGBCodeTrans(
							chartColorMap.getOrDefault(lineChart.getData().get(idxSeries).getName(), Color.BLACK), "80")
							+ " ;");
				}
				styleChartLine(lineChart,
						chartColorMap.getOrDefault(lineChart.getData().get(idxSeries).getName(), Color.BLACK));

			} else {
				if (!globalIsNear) {
					String oldStyle = lineChart.getData().get(idxSeries).getNode().getStyle();
					this.fireEvent(new NearEvent(lineChart.getData().get(idxSeries).getName(), false));
					String newStyleString = "";
					//
					// lineChart.getData().get(idxSeries).getNode().setStyle(newStyleString);
					if (SimpleLineChartsMultiYAxis.hmBoldAxis.containsKey(lineChart)
							&& !SimpleLineChartsMultiYAxis.hmBoldAxis.get(lineChart)) {
						lineChart.getYAxis().setStyle("-fx-border-width: 0 1 0 1;");
					}

					if (!hmVisibleSeries.get(lineChart.getData().get(idxSeries).getName()).isSelected) {
						newStyleString = newStyle(oldStyle, "-fx-stroke-width: 1px",
								"-fx-stroke-width:\\*\\d+(px|\\.?\\d+?)");

						lineChart.getData().get(idxSeries).getNode().setStyle(newStyleString);
						styleChartLine(lineChart,
								chartColorMap.getOrDefault(lineChart.getData().get(idxSeries).getName(), Color.BLACK));
					} else {
						// System.out.println("Not Near and Selected for " +
						// lineChart.getData().get(idxSeries).getName());
						newStyleString = newStyle(oldStyle, "-fx-stroke-width: 3px",
								"-fx-stroke-width:\\*\\d+(px|\\.?\\d+?)");
						lineChart.getData().get(idxSeries).getNode().setStyle(newStyleString);
						styleChartLine(lineChart,
								chartColorMap.getOrDefault(lineChart.getData().get(idxSeries).getName(), Color.BLACK));
					}
				}
				if (!isPopupFullVisible)
					return null; // the line is not visible

			}
			HBox popupRow = null;
			if (null != timeConverter) {
				popupRow = new HBox(10, seriesName, new Label(
						"[" + timeConverter.toString(xValueLong) + ";" + dblFormatter.toString(yValueForChart) + "]"));
			} else {

				popupRow = new HBox(10, seriesName, new Label("[" + dblFormatter.toString(xValueLong.doubleValue())
						+ ";" + dblFormatter.toString(yValueForChart) + "]"));
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
