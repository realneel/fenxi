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
 * Logger.java
 *
 * Created on March 30, 2007, 5:14 PM
 *
 * FenXi Logger
 */

package org.fenxi;

import java.io.PrintWriter;

/**
 *
 * @author neel
 */
public class Logger {
    private PrintWriter pw;
    private String logfile;
    private static Logger instance;
    /** Creates a new instance of Logger */
    private Logger(String logfile) {
        this.logfile = logfile;
        try {
            this.pw = new PrintWriter(logfile);            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unable to open logfile - " + this.logfile);
            System.exit(1);
        }
    }
    /** Log a message
     * @param message message to be logged
     */
    public synchronized void log(String message) {
        if (message != null) {
            pw.println(message);
            pw.flush();
        }
    }
    public synchronized void log(Exception e) {
        e.printStackTrace(pw);
        pw.flush();
    }
    public static synchronized Logger getInstance() {
        if (Logger.instance == null)
            Logger.instance = new Logger("fenxi.log");
        return (Logger.instance);
    }    
}
