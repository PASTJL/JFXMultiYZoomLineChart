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

import java.util.Date;

import org.jlp.javafx.SimpleLineChartsMultiYAxis.TupleStrLC;
import org.jlp.javafx.common.ZoomEvent;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

// TODO: Auto-generated Javadoc
/**
 * The Class ZoomableLineChartsMultiYAxis.
 */
public class ZoomableLineChartsMultiYAxis extends SimpleLineChartsMultiYAxis {

	/** The Constant myEventType. */
	public final static EventType<Event> myEventType;
	static {
		myEventType = new EventType<>("zoomEvent");
	}
	/** The is draging. */
	public boolean isDraging = false;

	// final public StackPane chartContainer = new StackPane();
	// final public HBox controls = new HBox(10);
	/** The deb dragging. */
	// final LineChart<Number, Number> chart = createChart();
	private Point2D debDragging = new Point2D(0, 0);

	/**
	 * Instantiates a new zoomable line charts multi Y axis.
	 *
	 * @param strokeWidthpas
	 *            the stroke widthpas
	 */
	public ZoomableLineChartsMultiYAxis(Double strokeWidthpas) {
		this(strokeWidthpas, true);
	}

	/**
	 * Instantiates a new zoomable line charts multi Y axis.
	 *
	 * @param strokeWidthpas
	 *            the stroke widthpas
	 * @param popup
	 *            the popup
	 */
	public ZoomableLineChartsMultiYAxis(Double strokeWidthpas, boolean popup) {
		super(strokeWidthpas, popup);
		// chartContainer.getChildren().add(chart);
		baseChart.getXAxis().setAutoRanging(false);
		baseChart.getYAxis().setAutoRanging(false);

		final Rectangle zoomRect = new Rectangle();
		zoomRect.setManaged(false);
		zoomRect.setFill(Color.GREEN.deriveColor(0, 1, 1, 0.4));
		zoomRect.setMouseTransparent(true);
		getChildren().add(zoomRect);
		setUpZooming(zoomRect, this);

		// setUpZooming(zoomRect, this);
	}

	/**
	 * Sets the up zooming.
	 *
	 * @param rect
	 *            the rect
	 * @param zoomingNode
	 *            the zooming node
	 */
	private void setUpZooming(final Rectangle rect, final Node zoomingNode) {
		final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();
		zoomingNode.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			debDragging = new Point2D(event.getX(), event.getY());
			mouseAnchor.set(new Point2D(event.getX(), event.getY()));
			event.consume();
			for (TupleStrLC tup : this.backgroundCharts) {
				tup.lineChart.setMouseTransparent(true);
			}
			baseChart.setMouseTransparent(true);
			this.unbindMouseEvents();
			rect.setWidth(0);
			rect.setHeight(0);
			rect.setVisible(true);
			setRectOnTop(rect);

		});
		zoomingNode.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {

			isDraging = true;
			double x = event.getX();
			double y = event.getY();
			event.consume();
			rect.setX(Math.abs(Math.min(x, mouseAnchor.get().getX())));
			rect.setY(Math.abs(Math.min(y, mouseAnchor.get().getY())));
			rect.setWidth(Math.abs(x - mouseAnchor.get().getX()));
			rect.setHeight(Math.abs(y - mouseAnchor.get().getY()));

			rect.setVisible(true);
			// System.out.println(
			// "rect :" + rect.getHeight() + ";" + rect.getWidth() + " ; " + rect.getX() +
			// ";" + rect.getY());

		});
		zoomingNode.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
			event.consume();
			if (isDraging) {

				// if (event.getX() > debDragging.getX() ) {
				if (Math.abs(rect.getHeight()) > 10) {
					/* Zoom action */
					doZoom(rect, baseChart);

				} else {
					/* Reset Action */
					doReset(baseChart);

				}
				rebuildChart();

				// rebuildChart(((NumberAxis) baseChart.getXAxis()).getLowerBound(),
				// ((NumberAxis) baseChart.getXAxis()).getUpperBound());

				setRectOnTop(rect);

			}
			baseChart.setMouseTransparent(false);
			for (TupleStrLC tup : this.backgroundCharts) {
				tup.lineChart.setMouseTransparent(false);
			}
			this.bindMouseEvents();
			isDraging = false;
		});
	}

	/**
	 * Sets the rect on top.
	 *
	 * @param rect
	 *            the new rect on top
	 */
	private void setRectOnTop(Rectangle rect) {

		getChildren().remove(rect);
		getChildren().add(rect);
	}

	/**
	 * Do reset.
	 *
	 * @param chart
	 *            the chart
	 */
	private void doReset(LineChart<Number, Number> chart) {
		// System.out.println("reseting " + chart.getYAxis().getLabel());
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
		double lowerX = Double.MAX_VALUE;
		double upperX = Double.MIN_VALUE;
		double lowerY = Double.MAX_VALUE;
		double upperY = Double.MIN_VALUE;

		for (Series<Number, Number> series : ((LineChart<Number, Number>) chart).getData()) {
			// series.getData().sort(Comparator.comparingDouble(d ->
			// d.getYValue().longValue()));
			// For every data reset the translation to zero
			String nameSeries = series.getName();
			TupleBoolLC tup1 = hmVisibleSeries.get(nameSeries);
			Double translation = tup1.translation;
			tup1.translation = 0.0;
			hmVisibleSeries.put(nameSeries, tup1);
			// System.out.println("doReset " + nameSeries + " translation => " +
			// translation);
			for (Data<Number, Number> data : series.getData()) {
				data.setXValue(data.getXValue().doubleValue() - translation);
			}

			if (lowerY > hmYSortedSeries.get(series.getName()).getData().get(0).getYValue().doubleValue()) {
				lowerY = hmYSortedSeries.get(series.getName()).getData().get(0).getYValue().doubleValue();
			}
			if (upperY < hmYSortedSeries.get(series.getName()).getData().get(series.getData().size() - 1).getYValue()
					.doubleValue()) {
				upperY = hmYSortedSeries.get(series.getName()).getData().get(series.getData().size() - 1).getYValue()
						.doubleValue();
			}
			((NumberAxis) chart.getYAxis()).setLowerBound(lowerY - (upperY - lowerY) / 20);
			((NumberAxis) chart.getYAxis()).setUpperBound(upperY + (upperY - lowerY) / 20);
			// series.getData().sort(Comparator.comparingLong(d ->
			// d.getXValue().longValue()));
			if (lowerX > hmXSortedSeries.get(series.getName()).getData().get(0).getXValue().longValue()) {
				lowerX = hmXSortedSeries.get(series.getName()).getData().get(0).getXValue().longValue();
			}
			if (upperX < hmXSortedSeries.get(series.getName()).getData().get(series.getData().size() - 1).getXValue()
					.longValue()) {
				upperX = hmXSortedSeries.get(series.getName()).getData().get(series.getData().size() - 1).getXValue()
						.longValue();
			}

		}
		// ((NumberAxis) chart.getXAxis()).setLowerBound(lowerX - (upperX - lowerX) /
		// 100);
		// ((NumberAxis) chart.getXAxis()).setUpperBound(upperX+ (upperX-lowerX)/20);

		((NumberAxis) chart.getXAxis()).setLowerBound(
				Math.min(((NumberAxis) chart.getXAxis()).getLowerBound(), lowerX - (upperX - lowerX) / 20));
		((NumberAxis) chart.getXAxis()).setUpperBound(
				Math.max(((NumberAxis) chart.getXAxis()).getUpperBound(), upperX + (upperX - lowerX) / 20));
		((NumberAxis) chart.getYAxis()).setLowerBound(
				Math.min(((NumberAxis) chart.getYAxis()).getLowerBound(), lowerY - (upperY - lowerY) / 20));
		((NumberAxis) chart.getYAxis()).setUpperBound(
				Math.max(((NumberAxis) chart.getYAxis()).getUpperBound(), upperY + (upperY - lowerY) / 20));
		((NumberAxis) chart.getYAxis()).setTickUnit(
				(((NumberAxis) chart.getYAxis()).getUpperBound() - ((NumberAxis) chart.getYAxis()).getLowerBound())
						/ nbYTicks);

		for (TupleStrLC tpl : backgroundCharts) {
			boundYAxis(tpl.lineChart, null, true);

		}
		((NumberAxis) chart.getXAxis()).setTickUnit(
				(((NumberAxis) chart.getXAxis()).getUpperBound() - ((NumberAxis) chart.getXAxis()).getLowerBound())
						/ nbXTicks);
		NumberAxis xAxis = (NumberAxis) chart.getXAxis();
		ZoomEvent ze = new ZoomEvent(new Date((long) xAxis.getLowerBound()), new Date((long) xAxis.getUpperBound()));
		this.fireEvent(ze);
	}

	/**
	 * Do zoom.
	 *
	 * @param zoomRect
	 *            the zoom rect
	 * @param chart
	 *            the chart
	 */
	private void doZoom(Rectangle zoomRect, LineChart<Number, Number> chart) {
		Point2D zoomTopLeft = new Point2D(zoomRect.getX(), zoomRect.getY());
		Point2D zoomBottomRight = new Point2D(zoomTopLeft.getX() + zoomRect.getWidth(),
				zoomTopLeft.getY() + zoomRect.getHeight());
		final NumberAxis yAxis = (NumberAxis) chart.getYAxis();
		final NumberAxis yAxisBaseChart = (NumberAxis) baseChart.getYAxis();
		yAxis.setAutoRanging(false);
		chart.getXAxis().setAutoRanging(false);

		Point2D yAxisInParent = yAxisBaseChart.localToScene(0, 0);

		final NumberAxis xAxis = (NumberAxis) chart.getXAxis();

		Point2D xAxisInParent = xAxis.localToScene(0, 0);

		double xOffset = zoomTopLeft.getX() - yAxisInParent.getX() - baseChart.getYAxis().getWidth();

		// 30 is an estimate value of Label Font height and padding
		double yOffset = zoomBottomRight.getY() - xAxisInParent.getY() + xAxis.getHeight() + 30;
		double xAxisScale = xAxis.getScale();
		double yAxisScale = yAxis.getScale();

		xAxis.setLowerBound(((Double) (xAxis.getLowerBound() + (xOffset / xAxisScale))));
		xAxis.setUpperBound(((Double) (xAxis.getLowerBound() + (Math.abs(zoomRect.getWidth()) / xAxisScale))));

		yAxis.setLowerBound(yAxis.getLowerBound() + yOffset / yAxisScale);
		yAxis.setUpperBound(yAxis.getLowerBound() + (Math.abs(zoomRect.getHeight() / yAxisScale))); // test
		yAxis.setTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()) / nbYTicks);
		xAxis.setTickUnit((xAxis.getUpperBound() - xAxis.getLowerBound()) / nbXTicks);
		// System.out.println(yAxis.getLowerBound() + " " + yAxis.getUpperBound());

		for (TupleStrLC tpl : backgroundCharts) {
			boundYAxis(tpl.lineChart, zoomRect, false);

		}
		// new Date(((long) ((NumberAxis) chart.baseChart.getXAxis()).getLowerBound()));
		zoomRect.setWidth(0);
		zoomRect.setHeight(0);
		ZoomEvent ze = new ZoomEvent(new Date((long) xAxis.getLowerBound()), new Date((long) xAxis.getUpperBound()));
		this.fireEvent(ze);
	}

	/**
	 * Bound Y axis.
	 *
	 * @param chart
	 *            the chart
	 * @param zoomRect
	 *            the zoom rect
	 * @param reset
	 *            the reset
	 */
	private void boundYAxis(LineChart<Number, Number> chart, Rectangle zoomRect, Boolean reset) {
		if (!reset) {
			Point2D zoomBottomRight = new Point2D(zoomRect.getX() + zoomRect.getWidth(),
					zoomRect.getY() + zoomRect.getHeight());
			final NumberAxis yAxis = (NumberAxis) chart.getYAxis();
			final NumberAxis xAxis = (NumberAxis) baseChart.getXAxis();
			yAxis.setAutoRanging(false);
			Point2D xAxisInParent = xAxis.localToScene(0, 0);
			double yOffset = zoomBottomRight.getY() - xAxisInParent.getY() + xAxis.getHeight() + 30;

			double yAxisScale = yAxis.getScale();
			yAxis.setLowerBound(yAxis.getLowerBound() + (yOffset / yAxisScale));
			yAxis.setUpperBound(yAxis.getLowerBound() + (Math.abs(zoomRect.getHeight() / yAxisScale))); // test
			yAxis.setTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()) / nbYTicks);
			// xAxis.setTickUnit((xAxis.getUpperBound() - xAxis.getLowerBound()) / nbTicks);
			// // Test
		} else {
			/* bounds with initial values */
			double lowerY = Double.MAX_VALUE;
			double upperY = Double.MIN_VALUE;
			double lowerX = Double.MAX_VALUE;
			double upperX = Double.MIN_VALUE;
			// double lowerX = ((NumberAxis) baseChart.getXAxis()).getLowerBound();
			// double upperX = ((NumberAxis) baseChart.getXAxis()).getUpperBound();
			((NumberAxis) chart.getYAxis()).setAutoRanging(false);
			for (Series<Number, Number> series : ((LineChart<Number, Number>) chart).getData()) {

				String nameSeries = series.getName();
				TupleBoolLC tup1 = hmVisibleSeries.get(nameSeries);
				Double translation = tup1.translation;
				tup1.translation = 0.0;
				hmVisibleSeries.put(nameSeries, tup1);
				// System.out.println(nameSeries + " translation => " + translation);
				// System.out.println("Reset First value for " + nameSeries + " before
				// Translation : "
				// + series.getData().get(0).getXValue());
				// System.out.println("Last value for " + nameSeries + " before Translation : "
				// + series.getData().get(series.getData().size() - 1).getXValue());
				for (Data<Number, Number> data : series.getData()) {
					data.setXValue(data.getXValue().doubleValue() - translation);
				}

				if (lowerY > hmYSortedSeries.get(series.getName()).getData().get(0).getYValue().doubleValue()) {
					lowerY = hmYSortedSeries.get(series.getName()).getData().get(0).getYValue().doubleValue();
				}
				if (upperY < hmYSortedSeries.get(series.getName()).getData().get(series.getData().size() - 1)
						.getYValue().doubleValue()) {
					upperY = hmYSortedSeries.get(series.getName()).getData().get(series.getData().size() - 1)
							.getYValue().doubleValue();
				}
				((NumberAxis) chart.getYAxis()).setLowerBound(lowerY);
				((NumberAxis) chart.getYAxis()).setUpperBound(upperY);
			}
			for (Series<Number, Number> series : ((LineChart<Number, Number>) chart).getData()) {

				// series.getData().sort(Comparator.comparingDouble(d ->
				// d.getXValue().longValue()));
				if (lowerX > hmXSortedSeries.get(series.getName()).getData().get(0).getXValue().doubleValue()) {
					lowerX = hmXSortedSeries.get(series.getName()).getData().get(0).getXValue().doubleValue();
				}
				if (upperX < hmXSortedSeries.get(series.getName()).getData().get(series.getData().size() - 1)
						.getXValue().doubleValue()) {
					upperX = hmXSortedSeries.get(series.getName()).getData().get(series.getData().size() - 1)
							.getXValue().doubleValue();
				}
				((NumberAxis) baseChart.getXAxis()).setLowerBound(
						Math.min(((NumberAxis) baseChart.getXAxis()).getLowerBound() - (upperX - lowerX) / 20,
								lowerX - (upperX - lowerX) / 20));
				((NumberAxis) baseChart.getXAxis()).setUpperBound(
						Math.max(((NumberAxis) baseChart.getXAxis()).getUpperBound() + (upperX - lowerX) / 20,
								upperX + (upperX - lowerX) / 20));
			}
			((NumberAxis) chart.getYAxis()).setLowerBound(
					Math.min(((NumberAxis) chart.getYAxis()).getLowerBound(), lowerY - (upperY - lowerY) / 20));
			((NumberAxis) chart.getYAxis()).setUpperBound(
					Math.max(((NumberAxis) chart.getYAxis()).getUpperBound(), upperY + (upperY - lowerY) / 20));

			((NumberAxis) chart.getYAxis()).setTickUnit(
					(((NumberAxis) chart.getYAxis()).getUpperBound() - ((NumberAxis) chart.getYAxis()).getLowerBound())
							/ nbYTicks);

		}

	}

}
