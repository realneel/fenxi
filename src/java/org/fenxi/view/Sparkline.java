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

/*
 * Sparkline.java
 *
 * Created on January 3, 2007, 3:09 PM
 *
 * $Id: Sparkline.java,v 1.1 2007-12-06 19:47:38 realneel Exp $
 * $Author: realneel $
 *
 * A basic sparkline implementation.
 */

package org.fenxi.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 *
 * @author neel
 */
public class Sparkline {
    private static final int WIDTH = 98;
    private static final int HEIGHT = 24;
    private static final Color DEFAULT_COLOR = new Color(0x5382A1);
    private static final int MARGIN_X = 2;
    private static final int MARGIN_Y = 2;
    private static final float line_width = (float)1.5;
    public Color color;
    private int width;
    private int height;
    private float[] numbers;
    
    /** Creates a new instance of Sparkline */
    public Sparkline(float[] num, int width, int height, Color color) {
        this.height = height;
        this.width = width;
        this.color = color;
        this.numbers = num;
    }
    public Sparkline(float[] num, int width, int height) {
        this(num, width, height, DEFAULT_COLOR);
    }
    public Sparkline(float[] num) {
        this(num, WIDTH, HEIGHT, DEFAULT_COLOR);
    }
    public BufferedImage draw() {
        if (numbers.length < 2)
            return null;
        
        float[] newnumbers;
        int step = 1;
        BufferedImage img = new BufferedImage(width + MARGIN_X,
                height + MARGIN_Y, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setPaint(color);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(new BasicStroke(line_width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        float max_y = numbers[0];
        float min_y = numbers[0];
        for (float y : numbers) {
            max_y = (y > max_y) ? y : max_y;
            min_y = (y < min_y) ? y : min_y;
        }
        float range_y = max_y - min_y;                
        if (range_y == 0)
            range_y = 1.0f;        
        
        float scale_x = ((float)(numbers.length - 1))/width;                
        /* If scale_x > 1, then we have more datapoints than what we can draw */                
        if (scale_x > 1) {
            step = Math.round(scale_x);
        }
        for (int i = 0; i < numbers.length - step; i += step) {
            float x1 = i/scale_x + MARGIN_X;
            float x2 = (i + step)/scale_x + MARGIN_X;
             /* Now scale the y values */
            float y1 = (numbers[i] - min_y)/range_y * height;            
            float y2 = (numbers[i + step] - min_y)/range_y * height;
            //System.err.println("Plotting " + x1+","+(height - y1)+" "+ x2+","+(height - y2));
            g.draw(new Line2D.Float(x1, height - y1, x2, height - y2));
        }
        return (img);
    }
    public static void main(String[] args) {
        if (args.length != 1 ){
            System.err.println("usage: sparkline length");
            System.exit(1);
        }
        int count = Integer.parseInt(args[0]);
        float[] num = new float[count];
        Random generator = new Random(System.currentTimeMillis());
        for (int i = 0; i < count; i++) {
            //num[i] = generator.nextFloat();
            num[i] = (i % 2 == 0) ? 10 : -10*i;
            //num[i] = (i % 2 == 0) ? 10 : -10*i;
            System.err.println("num[" + i + "] = " + num[i]);
        }
        
        long wa_start = System.currentTimeMillis();
        Sparkline s = new Sparkline(num);
        BufferedImage bi = s.draw();
        double time = (System.currentTimeMillis() - wa_start)/1000.0;
        System.out.println("Mine = " + time + "(s)");
        
        final File file = new File("a.png");
        try {
            ImageIO.write(bi, "png", file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        /*
        Number[] numbers = new Number[count];
        for (int i = 0; i < count; i++) {
            numbers[i] = new Float(num[i]);
        }
        wa_start = System.currentTimeMillis();
        BufferedImage img = com.representqueens.spark.LineGraph.createGraph(numbers);
        time = (System.currentTimeMillis() - wa_start)/1000.0;
        System.out.println("Other = " + time + "(s)");
        final File file1 = new File("b.png");
        try {
            ImageIO.write(img, "png", file1);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
         **/
    }
}
