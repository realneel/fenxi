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
import java.io.*;
import java.awt.*;
import java.util.*;

/** This class serves two purposes.
 * 1. Maintain the preferred list of colors that is assigned to series.
 * 2. Map color name to hexadecimal value.
 *
 * At startup, parse the fenxi_colors file and create our list.
 */
public class ZChartColor {
    static final String fenxiColors = "/fenxi_colors";
    public static synchronized ZChartColor getInstance(){
        if (ref == null)
            ref = new ZChartColor();
        return ref;
    }
    public Object clone()
    throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    public ZChartColor() {
        colorNameList = new ArrayList<String>(200);
        colorPaintList = new ArrayList<Color>(200);
        InputStream is = ZChartColor.class.getResourceAsStream(fenxiColors);
        if(is != null) {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                LineNumberReader lnr = new LineNumberReader(isr);
                String buffer;
                do {
                    buffer = lnr.readLine();
                    if(buffer == null || buffer.length() == 0)
                        break;
                    //parse the line: plum #DDA0DD
                    String tokens[] = buffer.split("#");
                    if(tokens.length != 2)
                        throw new Exception("Unknown line in fenxi_colors: " + buffer);
                    if (tokens == null || tokens[0] == null || tokens[1] == null || tokens[1].length() != 6)
                        throw new Exception("Unknown format for color in fenxi_colors: " + buffer);
                    Color color = new Color(Integer.parseInt(tokens[1], 16));
                    setColor(tokens[0], color);
                }while(buffer != null);
            }catch (Exception e){
                System.err.println("********* Error reading FenXi Color file ********");
                e.printStackTrace();
            }
        }
    }
        
    public static String[] getColorsAsString(){
        String scolors[] = ZChartColor.getInstance().colorNameList.toArray(new String[0]);
        return scolors;
    }
    public static Paint[] getColorsAsPaint(){
        Paint p[] = ZChartColor.getInstance().colorPaintList.toArray(new Paint[0]);
        return p;
    }
    public static String getColorAtIndex(int index){
        return ZChartColor.getInstance().colorNameList.get(index);
    }
    public static Color getColor(String key) {
        //System.out.println("GET: "+key);
        if(ZChartColor.getInstance().colorNameList.contains(key.toUpperCase().trim())){
            return ZChartColor.getInstance().colorPaintList.get(ZChartColor.getInstance().colorNameList.indexOf(key.toUpperCase().trim()));
        } else {
            if (key.trim().length() == 6 && key.trim().matches("[0-9A-Fa-f]+")){ //maybe we got key = "ffffdd";
                Color col = new Color(Integer.parseInt(key, 16));
                ZChartColor.getInstance().setColor(key, col);
                return col;
            }
        }
        return null;
    }
    
    public void setColor(String key, Color val) {
        colorNameList.add(key.toUpperCase().trim());
        colorPaintList.add(val);
    }
    
    private ArrayList<String> colorNameList;
    private ArrayList<Color> colorPaintList;
    private static ZChartColor ref; //singleton ref
};
