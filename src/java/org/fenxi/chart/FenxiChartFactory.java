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
 *****************************************************
 */

package org.fenxi.chart;

import java.io.ByteArrayOutputStream;
import org.jfree.data.XYSeriesCollection;

/**
 *
 * @author neel
 */
public class FenxiChartFactory {
	public static Chart newChart(XYSeriesCollection data,
                                 String hdr, String disp, String x, String y,
				String cust) {
        return new JChart(data, hdr, disp, x, y, cust);
                          
    }
    public ByteArrayOutputStream newChartAsOutputStream(XYSeriesCollection data,
                                 String hdr, String disp, String x, String y,
				String cust) throws Exception {
        Chart chart = FenxiChartFactory.newChart(data, hdr,disp,x,y,cust);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(25 * 1024);
        javax.imageio.ImageIO.write(chart.draw(), "png", baos);
        return baos;
    }
}
