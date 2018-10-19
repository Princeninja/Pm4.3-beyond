package usrlib;

import java.awt.BasicStroke;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.zkoss.zkex.zul.impl.JFreeChartEngine;
import org.zkoss.zul.Chart;

public class LineChartEngine extends JFreeChartEngine {

	public LineChartEngine(){
	}

	public static Integer strokeWidth = 2;
	public static boolean showLine = true;
	public static boolean lineShape = true;

	public boolean prepareJFreeChart(JFreeChart jfchart, Chart chart){

		LineAndShapeRenderer renderer = (LineAndShapeRenderer) ((CategoryPlot) jfchart.getPlot()).getRenderer();
		renderer.setSeriesStroke(0, new BasicStroke(strokeWidth));
		renderer.setSeriesStroke(1, new BasicStroke(strokeWidth));
		renderer.setSeriesStroke(2, new BasicStroke(strokeWidth));
		renderer.setSeriesLinesVisible(0, chart.isThreeD());
		renderer.setSeriesLinesVisible(1, showLine);
		renderer.setSeriesLinesVisible(2, showLine);
		renderer.setSeriesShapesVisible(0, lineShape);
		renderer.setSeriesShapesVisible(1, lineShape);
		renderer.setSeriesShapesVisible(2, lineShape);

		return false;
	}

	public static Integer getStroke(){
		return strokeWidth;
	}

	public static void setStroke(Integer stroke){
		strokeWidth = stroke;
	}

	public static boolean isShowLine(){
		return showLine;
	}

	public static void setShowLine(boolean showLine){
		LineChartEngine.showLine = showLine;
	}

	public static boolean isLineShape() {
		return lineShape;
	}

	public static void setLineShape(boolean showShape){
		lineShape = showShape;
	}
} 

/**/
