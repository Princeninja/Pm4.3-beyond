package usrlib;

import org.zkoss.zkex.zul.impl.JFreeChartEngine;
import java.awt.BasicStroke;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;

import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.zkoss.zul.Chart;

public class MyChartEngine extends JFreeChartEngine {

	
		public MyChartEngine() {
			numSeries = 0;
		}
		
		public static int numSeries = 0;
		public static Integer strokeWidth[] = new Integer[9];
		public static boolean showLine[] = new boolean[9];
		public static boolean lineShape[] = new boolean[9];
		
		public boolean prepareJFreeChart(JFreeChart jfchart, Chart chart) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) ((XYPlot) jfchart.getPlot()).getRenderer();
		
			for ( int i = 0; i < numSeries; ++i ){				
				renderer.setSeriesStroke( i, new BasicStroke(strokeWidth[i]));
				renderer.setSeriesLinesVisible( i, showLine[i] );
				renderer.setSeriesShapesVisible( i, lineShape[i] );
			}
			return false;
		}
		
		public int addSeries( int width, boolean line, boolean shape ){
			strokeWidth[numSeries] = width;
			showLine[numSeries] = line;
			lineShape[numSeries] = shape;
			return ++numSeries - 1;
		}
		
/*		public static Integer getStroke() {
			return strokeWidth;
		}
		
		public static void setStroke(Integer stroke) {
			strokeWidth = stroke;
		}
		
		public static boolean isShowLine() {
			return showLine;
		}
		
		public static void setShowLine(boolean showLine) {
			MyChartEngine.showLine = showLine;
		}
		
		public static boolean isLineShape() {
			return lineShape;
		}
		
		public static void setLineShape(boolean showShape) {
			lineShape = showShape;
		} 
*/
	}

