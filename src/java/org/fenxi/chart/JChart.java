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

import org.jfree.chart.axis.*;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.*;
import org.jfree.ui.*;
import org.jfree.data.DefaultPieDataset;
import org.jfree.chart.plot.*;
import org.jfree.chart.*;
import org.jfree.chart.renderer.*;
import org.jfree.chart.labels.StandardPieItemLabelGenerator;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.text.*;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author neel
 */
public class JChart extends Chart {

    private static final float FG_ALPHA = 0.8f;
    private static final float BG_ALPHA = 1.0f;
    private final String domainGridlinePaint = "888888";
    private final String rangeGridlinePaint = "888888";
    private JFreeChart jchart;
    private int ctype;
    private XYSeriesCollection xysc;
    private HistogramDataset histogramDataset;
    private CategoryDataset categoryDataset;
    private TimeSeriesCollection timeSeriesDataset;

    public JChart(XYSeriesCollection data, String title,
                  String chartType, String xAxis, String yAxis, String customizations) {
        super(title, chartType, xAxis, yAxis, customizations);
        xysc = data;
        if (xysc.getSeriesCount() < 5) {
            this.setLineWidth(2);
        }
    }

    public BufferedImage draw() throws Exception {
        super.draw();
        if (create_chart()) {
            if (customize_chart()) {
                return jchart.createBufferedImage(width, height,
                                                  BufferedImage.TYPE_INT_ARGB,
                                                  null);
            }
        }
        throw new Exception("Could not draw chart");
    }

    private boolean create_chart() {

        if (xysc == null) {
            return false;
        }
        if (xysc.getSeriesCount() > 24) {
            this.legendVisible = false;
        }
        ctype = chartTypeAsInt(this.getChartType());
        switch (ctype) {
            case BARLOG_CHART:
            case BAR_CHART:
                jchart = ChartFactory.createXYBarChart(getTitle(), getXAxis(),
                                                       false, getYAxis(), xysc,
                                                       PlotOrientation.VERTICAL,
                                                       legendVisible, true,
                                                       false);
                break;
            case SCATTER_PLOT:
                jchart = ChartFactory.createScatterPlot(getTitle(), getXAxis(),
                                                        getYAxis(), xysc,
                                                        PlotOrientation.VERTICAL,
                                                        legendVisible, true,
                                                        false);
                break;
            case LINE_MULTI_AXIS:
                XYSeriesCollection xysc1 = new XYSeriesCollection(xysc.getSeries(0));
                jchart = ChartFactory.createXYLineChart(getTitle(), getXAxis(),
                                                        getYAxis(), xysc1,
                                                        PlotOrientation.VERTICAL,
                                                        legendVisible, true,
                                                        false);
                break;
            case DEFAULT_CHART:
            case XYLOG_CHART:
            case LINE_CHART:
                jchart = ChartFactory.createXYLineChart(getTitle(), getXAxis(),
                                                        getYAxis(), xysc,
                                                        PlotOrientation.VERTICAL,
                                                        legendVisible, true,
                                                        false);
                break;
            case XYAREA_CHART:
                jchart = ChartFactory.createXYAreaChart(getTitle(), getXAxis(),
                                                        getYAxis(), xysc,
                                                        PlotOrientation.VERTICAL,
                                                        legendVisible, true,
                                                        false);
                break;
            case PIE_CHART:
                jchart = ChartFactory.createPieChart3D(getTitle(),
                                                       convertDatasetToDefaultPieDataset(xysc),
                                                       legendVisible, true,
                                                       false);
                break;
            case CAREA_CHART:
                jchart = ChartFactory.createAreaChart(getTitle(), getXAxis(),
                                                      getYAxis(),
                                                      getCategoryDataset(),
                                                      PlotOrientation.VERTICAL,
                                                      legendVisible, true, false);
                break;
            case CBAR3D_CHART:
                jchart = ChartFactory.createBarChart3D(getTitle(), getXAxis(),
                                                       getYAxis(),
                                                       getCategoryDataset(),
                                                       PlotOrientation.VERTICAL,
                                                       legendVisible, true,
                                                       false);
                break;
            case CBAR_CHART:
                jchart = ChartFactory.createBarChart(getTitle(), getXAxis(),
                                                     getYAxis(),
                                                     getCategoryDataset(),
                                                     PlotOrientation.VERTICAL,
                                                     legendVisible, true, false);
                break;
            case CSTACKAREA_CHART:
                jchart = ChartFactory.createStackedAreaChart(getTitle(),
                                                             getXAxis(),
                                                             getYAxis(),
                                                             getCategoryDataset(),
                                                             PlotOrientation.VERTICAL,
                                                             legendVisible, true,
                                                             false);
                break;
            case CSTACKBAR3D_CHART:
                jchart = ChartFactory.createStackedBarChart3D(getTitle(),
                                                              getXAxis(),
                                                              getYAxis(),
                                                              getCategoryDataset(),
                                                              PlotOrientation.VERTICAL,
                                                              legendVisible,
                                                              true, false);
                break;
            case CSTACKBAR_CHART:
                jchart = ChartFactory.createStackedBarChart(getTitle(),
                                                            getXAxis(),
                                                            getYAxis(),
                                                            getCategoryDataset(),
                                                            PlotOrientation.VERTICAL,
                                                            legendVisible, true,
                                                            false);
                break;
            case HISTOGRAM_CHART:
                jchart = ChartFactory.createHistogram(getTitle(), getXAxis(),
                                                      getYAxis(),
                                                      getHistogramDataset(),
                                                      PlotOrientation.VERTICAL,
                                                      legendVisible, true, false);
                break;
            case TIMESERIES_SECOND:
                jchart = ChartFactory.createTimeSeriesChart(getTitle(),
                                                            getXAxis(),
                                                            getYAxis(),
                                                            getTimeSeriesDataset(),
                                                            legendVisible, true,
                                                            false);
                break;
            default:
                System.err.println("Chart type not found:" + getChartType());
                return false;
        }
        return true;
    }

    private boolean customize_chart() {
        generic_customize();
        if (jchart.getPlot() instanceof XYPlot) {
            if (ctype != HISTOGRAM_CHART) {
                customize_XYChart();
            }
        } else if (jchart.getPlot() instanceof CategoryPlot) {
            customize_CategoryChart();
        }
        if (ctype == LINE_MULTI_AXIS) {
            customize_MultipleAxisPlot();
        } else if (ctype == TIMESERIES_SECOND) {
            customize_TimeSeriesSecondChart();
        } else if (ctype == PIE_CHART) {
            customize_PieChart();
        } else if (ctype == XYLOG_CHART || ctype == BARLOG_CHART) {
            LogarithmicAxis rangeAxis = new LogarithmicAxis(getYAxis());
            rangeAxis.setStrictValuesFlag(false);
            jchart.getXYPlot().setRangeAxis(rangeAxis);
        }
        return true;
    }

    private void generic_customize() {
        Paint p = new GradientPaint(0, 0, new Color(0xfefefe), 300, 300,
                                    ZChartColor.getColor(getBackgroundColor()));
        jchart.setBackgroundPaint(p);

        /* Do FenXi branding */
        TextTitle fenxi = new TextTitle(BRANDING_STR,
                                        new Font("SansSerif",
                                                 Font.PLAIN, 8));
        fenxi.setPosition(RectangleEdge.BOTTOM);
        fenxi.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        jchart.addSubtitle(fenxi);
        jchart.setAntiAlias(true);
        StandardLegend legend = (StandardLegend) jchart.getLegend();
        //if there a re alot of legends, dont show them
        //else if there a re alot of legends, decrease font size
        if (xysc.getSeriesCount() > 24) {
            jchart.setLegend(null);
        } else if (xysc.getSeriesCount() > 8) {
            legend.setItemFont(new Font("Serif", Font.PLAIN, 8));
        } else if (xysc.getSeriesCount() > 4) {
            legend.setItemFont(new Font("Serif", Font.PLAIN, 12));
        } else {
            legend.setItemFont(new Font("Serif", Font.PLAIN, 14));
        }
        if (xysc.getSeries(0).getItemCount() < 16) {
            setShapeVisible(true);
        }
        if (!isLegendVisible()) {
            jchart.setLegend(null);
        }
        jchart.getPlot().setForegroundAlpha(FG_ALPHA);
        jchart.getPlot().setBackgroundAlpha(BG_ALPHA);
    }

    private void customize_XYChart() {
        XYPlot plot = jchart.getXYPlot();
        StandardLegend legend = (StandardLegend) jchart.getLegend();
        plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(ZChartColor.getColor(domainGridlinePaint));
        plot.setRangeGridlinePaint(ZChartColor.getColor(rangeGridlinePaint));
        plot.setBackgroundPaint(ZChartColor.getColor(this.getPlotBackgroundColor()));
        if (isVerticalChart()) {
            plot.setOrientation(PlotOrientation.VERTICAL);
        }
        if (isShapeVisible()) {
            if (legend != null)
                legend.setDisplaySeriesShapes(true);
            if (ctype == LINE_CHART || ctype == XYLOG_CHART || ctype == DEFAULT_CHART) {
                StandardXYItemRenderer renderer = (StandardXYItemRenderer) plot.getRenderer();
                renderer.setPlotShapes(true);
            }
        }
        // change the auto tick unit selection to integer units only...
        if (isXAxisLog()) {
            LogarithmicAxis axis = new LogarithmicAxis(getXAxis());
            axis.setStrictValuesFlag(false);
            plot.setDomainAxis(axis);
        }
        if (isYAxisLog()) {
            LogarithmicAxis axis = new LogarithmicAxis(getYAxis());
            axis.setStrictValuesFlag(false);
            plot.setRangeAxis(axis);
        }

        NumberAxis y_axis = (NumberAxis) plot.getRangeAxis();
        y_axis.setAutoTickUnitSelection(true);
        y_axis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        y_axis.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
        if (ctype == TIMESERIES_SECOND) {
            return;
        }
        NumberAxis x_axis = (NumberAxis) plot.getDomainAxis();
        x_axis.setAutoTickUnitSelection(true);
        x_axis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));

        x_axis.setLabelFont(new Font("SansSerif", Font.BOLD, 12));

        //xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        if (isXAxisInteger()) {
            x_axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        }
        if (isYAxisInteger()) {
            y_axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        }

        Paint[] p = org.fenxi.chart.ZChartColor.getColorsAsPaint();
        int paintIndex = 0;
        XYItemRenderer r = plot.getRenderer(); // XYItemRenderer();
        for (int i = 0; i < xysc.getSeriesCount() && i < p.length; i++) {
            if (p[paintIndex].equals(this.getPlotBackgroundColor())) {
                paintIndex++; /* Skip background color Issue#28 */              
            }
            if (paintIndex >= p.length)
                paintIndex = 0;
            r.setSeriesPaint(i, p[paintIndex]);
            r.setSeriesStroke(i, new BasicStroke(getLineWidth()));
            paintIndex++;
        }
        plot.setRenderer(r);
    }

    private void customize_MultipleAxisPlot() {
        /* we need to setup multiple axis. We have to create seperate
         * datasets (1 dataset for each xyseris) and then add them to
         * the plot. We first setup the initial dataset, then create
         * secondary axis for other data
         */
        XYPlot plot = jchart.getXYPlot();
        XYSeriesCollection xysc1;
        Paint[] p = org.fenxi.chart.ZChartColor.getColorsAsPaint();
        plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        for (int i = 0; i < xysc.getSeriesCount() && i < p.length; i++) {
            xysc1 = new XYSeriesCollection(xysc.getSeries(i));
            final NumberAxis axis = new NumberAxis(xysc.getSeries(i).getName());
            axis.setAutoRangeIncludesZero(false);
            axis.setLabelPaint(p[i]);
            axis.setTickLabelPaint(p[i]);
            axis.setAutoRangeMinimumSize(1.0);
            plot.setRangeAxis(i, axis);
            plot.setDataset(i, xysc1);
            plot.mapDatasetToRangeAxis(i, i);
            StandardXYItemRenderer renderer2 = new StandardXYItemRenderer();
            if (isShapeVisible()) {
                renderer2.setPlotShapes(true);
            }
            plot.setRenderer(i, renderer2);
            renderer2.setSeriesPaint(0, p[i]);
            renderer2.setSeriesStroke(i,
                                      new BasicStroke(getLineWidth()));
            if (i != 0) {
                plot.setRangeAxisLocation(i,
                                          AxisLocation.BOTTOM_OR_RIGHT);
            } else {
                plot.setRangeAxisLocation(i,
                                          AxisLocation.BOTTOM_OR_LEFT);
            }
        }
    }

    private void customize_TimeSeriesSecondChart() {
        final XYPlot plot = jchart.getXYPlot();
        final DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MM-dd HH:mm:ss"));
    }

    private void customize_PieChart() {
        final PiePlot pplot = (PiePlot) jchart.getPlot();
        pplot.setLabelGenerator(new StandardPieItemLabelGenerator("{0} = {2}",
                                                                  NumberFormat.getNumberInstance(),
                                                                  NumberFormat.getPercentInstance()));
    }

    private void customize_CategoryChart() {
        final CategoryPlot cplot = (CategoryPlot) jchart.getPlot();
        if (isVerticalChart()) {
            cplot.setOrientation(PlotOrientation.VERTICAL);
        }
        CategoryAxis catAxis = cplot.getDomainAxis();
        catAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        catAxis.setCategoryMargin(0.10);
        /* If we have a lot of coloums, the ticks on the
         * X axis overwrite each other, making it look very
         * bad. We only display the ticks, if there are less than
         * 10 datapoints
         */
        if (cplot.getDataset().getColumnCount() > 10) {
            catAxis.setVisible(false);
        }
    }

    public CategoryDataset getCategoryDataset() {
        if (categoryDataset == null) {
            categoryDataset = convertDatasetToCategoryDataset(xysc);
        }
        return categoryDataset;
    }

    public HistogramDataset getHistogramDataset() {
        if (histogramDataset == null) {
            histogramDataset = convertDatasetToHistogramDataset(xysc, 100);
        }
        return histogramDataset;
    }

    public TimeSeriesCollection getTimeSeriesDataset() {
        if (timeSeriesDataset == null) {
            timeSeriesDataset = convertDatasetToTimeSeriesCollection(xysc,
                                                                     new Date());
        }
        return timeSeriesDataset;
    }

    public static synchronized CategoryDataset convertDatasetToCategoryDataset(XYSeriesCollection xysc) {
        final DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int r = 0; r < xysc.getSeriesCount(); r++) {
            XYSeries xys = xysc.getSeries(r);
            final String rowKey = xys.getName();
            for (int c = 0; c < xys.getItemCount(); c++) {
                String columnKey = " ";
                columnKey = "" + xys.getXValue(c);
                result.addValue(new Double(xys.getYValue(c).doubleValue()),
                                rowKey, columnKey);
            }
        }
        return result;
    }

    public static synchronized HistogramDataset convertDatasetToHistogramDataset(XYSeriesCollection xysc,
                                                                                 int bins) {
        final HistogramDataset hd = new HistogramDataset();
        for (int r = 0; r < xysc.getSeriesCount(); r++) {
            XYSeries xys = xysc.getSeries(r);
            double[] val = new double[xys.getItemCount()];
            for (int i = 0; i < xys.getItemCount(); i++) {
                val[i] = xys.getYValue(i).doubleValue();
            }
            hd.addSeries("Freq for" + xys.getName(), val, bins);
        }
        return hd;
    }

    public static synchronized DefaultPieDataset convertDatasetToDefaultPieDataset(XYSeriesCollection xysc) {
        DefaultPieDataset result = new DefaultPieDataset();
        java.util.List list = xysc.getSeries();
        for (int i = 0; i < list.size(); i++) {
            XYSeries xys = (XYSeries) list.get(i);
            double total = 0;
            for (int j = 0; j < xys.getItemCount(); j++) {
                total += xys.getYValue(j).doubleValue();
            }
            result.setValue(xys.getName(), total);
        }
        return result;
    }

    public static synchronized TimeSeriesCollection convertDatasetToTimeSeriesCollection(XYSeriesCollection xysc,
                                                                                         Date start) {
        Calendar cal = Calendar.getInstance();
        TimeSeriesCollection tsc = new TimeSeriesCollection();
        Date trialTime = start;

        for (int r = 0; r < xysc.getSeriesCount(); r++) {
            XYSeries xys = xysc.getSeries(r);
            final TimeSeries ts = new TimeSeries(xys.getName(),
                                                 Second.class);
            for (int i = 0; i < xys.getItemCount(); i++) {
                double y = xys.getYValue(i).doubleValue();
                int x = xys.getXValue(i).intValue();
                cal.setTime(trialTime);
                cal.add(Calendar.SECOND, x);
                ts.addOrUpdate(new Second(cal.getTime()), y);
            }
            tsc.addSeries(ts);
        }
        return tsc;
    }

    public static int chartTypeAsInt(String chartName) {
        for (int i = 0; i < chart_names.length; i++) {
            if (chart_names[i].equalsIgnoreCase(chartName)) {
                return i;
            }
        }
        return -1;
    }
}
