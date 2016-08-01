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
 * LoadData.java
 *
 * Created on March 30, 2007, 5:38 PM
 *
 * Load data files
 */

package org.fenxi.cmd.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import org.fenxi.Logger;
import org.fenxi.db.DBHelper;

/**
 *
 * @author neel
 */
public class LoadData {
    private String db_dir;
    private String data_dir;
    private ArrayList<String> sqlList;
    private Logger logger;
    
    /** Creates a new instance of CreateTables
     * @param db_dir Directory where the database resides
     * @param data_dir Directory where the data files reside
     */
    public LoadData(String db_dir, String data_dir) {
        this.db_dir = db_dir;
        this.data_dir = data_dir;
        this.sqlList = new ArrayList<String>();
        this.logger = Logger.getInstance();
    }
    /** Read the contents of the create file and add it to our list */
    private boolean loadAndExecuteFromFile(Connection conn, String file) {
        String sql;
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(file));
            for (;;) {
                sql = in.readLine();
                if (sql == null)
                    break;
                try {
                    PreparedStatement loadStmt = conn.prepareStatement(sql);
                    loadStmt.execute();
                    loadStmt.close();
                } catch (java.sql.SQLException e) {
                    logger.log("Error executing " + sql);
                    logger.log(e);
                    return (false);
                }
                logger.log("Successfully executed: " + sql);
            }
            in.close();
        }catch (Exception e) {
            logger.log("Error Loading from " + file);
            logger.log(e.toString());
            return (false);
        }
        return (true);
    }
    /** This is not a scalable way of doing this. Ideally, we would want a
     * properties file that specifies this mapping, but in the interest of
     * time ..
     */
    private String getSynonym(String file) {
        String table = file.toLowerCase();               
        if (table.startsWith("iostat"))
            return "IOSTAT";        
        else if (table.startsWith("vmstat"))
            return "VMSTAT";
        else if (table.startsWith("mpstat_a"))
            return "MPSTAT_A";
        else if (table.startsWith("mpstat"))
            return "MPSTAT";
        else if (table.startsWith("statit"))
            return "STATIT";
        else if (table.startsWith("netsum"))
            return "NETSUM";
        else if (table.startsWith("nicstat"))
            return "NICSTAT";
        else if (table.matches(".*statspack.*top5events"))            
            return "STATSPACK_TOP5EVENTS";        
        else
            return (null);
    }
    /** Load all the datafiles with names begining with prefix
     * @param conn The database connection
     * @param prefix Tablename prefix
     * @param files Array of filenames
     */
    private boolean loadDataFiles(Connection conn, String prefix, String[] files) {
        int errors, processed;
        errors = processed = 0;
        for (int i  = 0; i < files.length; i++) {
            if (files[i].endsWith(".data") && files[i].startsWith(prefix)){
                processed++;
                String tablename = files[i].substring(0, files[i].indexOf("."));
                String sql = DBHelper.loadTableString(tablename, data_dir + "/" + files[i]);
                try {
                    PreparedStatement loadStmt = conn.prepareStatement(sql);
                    loadStmt.execute();
                    loadStmt.close();
                } catch (java.sql.SQLException e) {
                    logger.log("Error executing " + sql);
                    logger.log(e);
                    errors++;
                }
                try {
                    String synonym = getSynonym(tablename);
                    if (synonym != null) {
                        sql = "create synonym " + synonym + " for " + tablename;
                        PreparedStatement loadStmt = conn.prepareStatement(sql);
                        loadStmt.execute();
                    }
                }catch (Exception e) {
                    /* This is ok to ignore as synonym might already exist */
                    logger.log("Ok to ignore exception below");
                    logger.log(e);
                }
                logger.log("Successfully executed: " + sql);
            }
        }
        if (processed == 0) {
            logger.log("Could not files matching files :" + prefix +"*.data");
            return (false);
        }
        /* Only return an error if number of processed files == errors */
        if (errors == processed) {
            logger.log("" + errors + " errors processing " + processed + "files");
            return (false);
        }
        return (true);
    }
    private boolean loadViewFile(Connection conn, String viewfile) {
        String sql = DBHelper.loadTableString("EXPORT_VIEW", viewfile);
        try {
            PreparedStatement loadStmt = conn.prepareStatement(sql);
            loadStmt.execute();
            loadStmt.close();
        } catch (java.sql.SQLException e) {
            logger.log("Error executing " + sql);
            logger.log(e);
            return (false);
        }
        logger.log("Successfully executed: " + sql);
        return (true);
    }
    
    /** Execute
     * @return true/false Success or Failure?
     */
    public boolean execute() {
        boolean loaded = false;
        Connection conn = DBHelper.newConnection(db_dir + "/" + DBHelper.DBNAME);
        File f = new File(this.data_dir);
        if (f.isDirectory() == false){
            logger.log(data_dir + " is not a directory");
            f.mkdir();
        }
        logger.log("Loading create statements from " + this.data_dir);
        String files[] = f.list();
        for (int i  = 0; i < files.length; i++) {
            if (files[i].endsWith(".create")){
                loaded = false;
                String tablename_prefix = files[i].replaceAll(".create", "");
                System.out.printf("Loading %-40s ...", tablename_prefix);
                if (loadAndExecuteFromFile(conn, data_dir + "/" + files[i])) {
                    System.out.print("+..");
                    /*  OK to load .view file */
                    String viewFile = files[i].replaceAll(".create", ".view");
                    if (loadViewFile(conn, data_dir + "/" + viewFile)) {
                        /* OK to load data files */
                        System.out.print("+..");
                        
                        if (loadDataFiles(conn, tablename_prefix, files)) {
                            System.out.print("+..");
                            loaded = true;
                        } else {
                            System.out.print("D..");
                        }
                    } else {
                        System.out.print("V..");
                    }
                } else
                    System.out.print("C..");
                if (loaded)
                    System.out.println("OK");
                else
                    System.out.println("Err");
            }            
        }
        try { 
            conn.close();
        } catch(Exception e) {
            Logger.getInstance().log("Error closing connection");
            Logger.getInstance().log(e);
        }
        return (true);
    }
}
