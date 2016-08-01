/******************************************************
 * Common Development and Distribution License - HEADER
 ******************************************************
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * http://fenxi.dev.java.net/cddl.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * http://fenxi.dev.java.net/cddl.html.  If applicable,
 * add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your
 * own identifying information:
 * "Portions Copyright [yyyy] [name of copyright owner]"
 ******************************************************/

package org.fenxi.chart;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import org.fenxi.Logger;

/**
 *
 * @author neel
 */
public class Chart {

    public static final String CHART_TYPE = "Line";
    public static final String XAXIS = "Time";
    public static final String YAXIS = "yAxis";
    public static final String TITLE = "Title";
    public static final String CUSTOM_PROPERTY_SEP = "=";
    public static final int WIDTH = 700;
    public static final int HEIGHT = 400;
    public static final String BGCOLOR = "F8D583";//"C1DAD7"; //"ffe4c4";
    public static final String PLOT_BGCOLOR = "White";
    public static final boolean SHOWSHAPE = false;
    public static final boolean SHOWLEGEND = true;
    public static final String ORIENTATION = "Horizontal";
    public static final boolean FORCE_XAXIS_INT = false;
    public static final boolean FORCE_YAXIS_INT = false;
    public static final boolean FORCE_XAXIS_LOG = false;
    public static final boolean FORCE_YAXIS_LOG = false;
    public static final String BRANDING_STR = "Generated by FenXi";
    public static final int LINE_WIDTH = 1;
    protected static final int LINE_CHART = 0;
    protected static final int LINE_MULTI_AXIS = 1;
    protected static final int XYLOG_CHART = 2;
    protected static final int XYAREA_CHART = 3;
    protected static final int CSTACKAREA_CHART = 4;
    protected static final int PIE_CHART = 5;
    protected static final int SCATTER_PLOT = 6;
    protected static final int BAR_CHART = 7;
    protected static final int CBAR_CHART = 8;
    protected static final int CBAR3D_CHART = 9;
    protected static final int CSTACKBAR_CHART = 10;
    protected static final int CSTACKBAR3D_CHART = 11;
    protected static final int HISTOGRAM_CHART = 12;
    protected static final int BARLOG_CHART = 13;
    protected static final int TIMESERIES_SECOND = 14;
    protected static final int CAREA_CHART = 88; /* same as XYArea, notused*/
    protected static final int DEFAULT_CHART = 99;
    /** these names are used by URLGenerator to create links */
    public static final String[] chart_names = {"Line", "MultiAxisLine", "Log",
                                                "Area", "Stacked Area", "Pie",
                                                "Scatter", "Bar", "Bar2",
                                                "Bar3D", "Stacked Bar",
                                                "Stacked Bar 3D", "Histogram",
                                                "LogBar", "TimeS"};
    protected String chartType;
    protected String xAxis; /*title for x axis */
    protected String yAxis; /* title for y axis */
    protected String title; /* Title */
    protected int width = WIDTH;
    protected int height = HEIGHT;
    protected String backgroundColor = BGCOLOR;
    protected String plotBackgroundColor = PLOT_BGCOLOR;
    protected boolean legendVisible = SHOWLEGEND;
    protected double backgroundAlpha = 0;
    protected double foregroundAlpha = 1;
    protected boolean shapeVisible = SHOWSHAPE;
    protected boolean verticalChart = false;
    protected boolean xAxisInteger = FORCE_XAXIS_INT;
    protected boolean yAxisInteger = FORCE_YAXIS_INT;
    protected boolean xAxisLog = FORCE_XAXIS_LOG;
    protected boolean yAxisLog = FORCE_YAXIS_LOG;
    private int lineWidth = LINE_WIDTH;
    private ArrayList<String> customColorList;
    private ArrayList<Integer> customLineWidth;

    public Chart(String title, String chartType, String xAxis,
                 String yAxis, String customProperties) {
        this.title = title;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.chartType = chartType;
        if (customProperties != null && customProperties.length() > 0) {
            parseCustomProperties(customProperties);
        }
    }

    public BufferedImage draw() throws Exception {
        return null;
    }

    private void parseCustomProperties(String customProperties) {
        try {
            String[] cmds = customProperties.split(";");
            for (int i = 0; i < cmds.length; i++) {                
                String[] nv = cmds[i].split(CUSTOM_PROPERTY_SEP);
                if (nv.length != 2)
                    continue;                
                if (nv[0].equalsIgnoreCase("h")) {
                    setHeight(Integer.parseInt(nv[1]));
                } else if (nv[0].equalsIgnoreCase("w")) {
                    setWidth(Integer.parseInt(nv[1]));
                } else if (nv[0].equalsIgnoreCase("xaxis")) {                    
                    setXAxis(nv[1]);
                } else if (nv[0].equalsIgnoreCase("yaxis")) {
                    setYAxis(nv[1]);
                } else if (nv[0].equalsIgnoreCase("xaxistype") && nv[1].equalsIgnoreCase("log")) {
                    setXAxisLog(true);
                } else if (nv[0].equalsIgnoreCase("yaxistype") && nv[1].equalsIgnoreCase("log")) {
                    setYAxisLog(true);
                }                
            }            
        } catch (Exception e) {
            Logger.getInstance().log(e);
            System.err.println("Unable to parse property:" + customProperties);
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getPlotBackgroundColor() {
        return plotBackgroundColor;
    }

    public void setPlotBackgroundColor(String plotBackgroundColor) {
        this.plotBackgroundColor = plotBackgroundColor;
    }

    public boolean isLegendVisible() {
        return legendVisible;
    }

    public void setLegendVisible(boolean legendVisible) {
        this.legendVisible = legendVisible;
    }

    public boolean isShapeVisible() {
        return shapeVisible;
    }

    public void setShapeVisible(boolean shapeVisible) {
        this.shapeVisible = shapeVisible;
    }

    public boolean isXAxisInteger() {
        return xAxisInteger;
    }

    public void setXAxisInteger(boolean xAxisInteger) {
        this.xAxisInteger = xAxisInteger;
    }

    public boolean isYAxisInteger() {
        return yAxisInteger;
    }

    public void setYAxisInteger(boolean yAxisInteger) {
        this.yAxisInteger = yAxisInteger;
    }

    public boolean isXAxisLog() {
        return xAxisLog;
    }

    public void setXAxisLog(boolean xAxisLog) {
        this.xAxisLog = xAxisLog;
    }

    public boolean isYAxisLog() {
        return yAxisLog;
    }

    public void setYAxisLog(boolean yAxisLog) {
        this.yAxisLog = yAxisLog;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public String getXAxis() {
        return xAxis;
    }

    public void setXAxis(String xAxis) {
        this.xAxis = xAxis;
    }

    public String getYAxis() {
        return yAxis;
    }

    public void setYAxis(String yAxis) {
        this.yAxis = yAxis;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVerticalChart() {
        return verticalChart;
    }

    public void setVerticalChart(boolean verticalChart) {
        this.verticalChart = verticalChart;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public ArrayList<String> getCustomColorList() {
        return customColorList;
    }

    public ArrayList<Integer> getCustomLineWidth() {
        return customLineWidth;
    }
}