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
 * DBHelper.java
 *
 * Created on July 20, 2006, 12:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.fenxi.db;
import java.sql.*;
import java.util.*;
import org.fenxi.Logger;
/**
 *
 * @author neel
 */
public class DBHelper {
    public static final String DBNAME = "/xanaDB";
    public static final int FENXI_TRENDLINE_TYPE = 99801;
    private static Logger logger = Logger.getInstance();
    public static Connection newConnection(String dbname) {
        /* the default framework is embedded*/
        String driver = "org.apache.derby.jdbc.EmbeddedDriver";
        String protocol = "jdbc:derby:";
        Connection conn = null;
        
        // Load Derby driver
        try {
            Class.forName(driver).newInstance();
            logger.log("Loaded the Derby driver class");
            //conn = DriverManager.getConnection(protocol + dbname + ";create=true");
            conn = DriverManager.getConnection(protocol + dbname);
            logger.log("Connected to database " + protocol+dbname);
        } catch (Exception e) {
            logger.log("Error loading Derby Driver for " + dbname);
            logger.log(e);
        }
        return (conn);
    }
    public static void shutDatabase() {
        try {
            DriverManager.getConnection("jdbc:derby:cs;shutdown=true");
        } catch (Exception e) {
            logger.log("Derby DB shutdown");
        }
    }
    public static void executeSimpleQuery(Connection conn, String sql)
    throws java.sql.SQLException {
        Statement stmt = conn.createStatement();
        executeQuery(stmt, sql);
    }
    public static ResultSet executeQuery(Statement stmt, String sql)
    throws java.sql.SQLException {
        //System.out.println("Executing: " + sql);
        
        // Implement the limit clause not implemented by Derby
        int limInd = sql.lastIndexOf("limit");
        if (limInd != -1) {
            String limClause = sql.substring(limInd);
            sql = sql.replaceFirst("limit.*$", "");
            limClause = limClause.replaceFirst("limit\\s+", "");
            int maxRows = Integer.parseInt(limClause);
            stmt.setMaxRows(maxRows);
        }
        ResultSet rset = stmt.executeQuery(sql);
        
        return (rset);
    }
    /* Export the table to a file */
    public static void exportTable(String dbname, String table, String file)
    throws Exception{
        Connection conn = DBHelper.newConnection(dbname);
        PreparedStatement exportStmt = null;
        exportStmt = conn.prepareStatement("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE"+
                "(?,?,?,?,?,?)");
        exportStmt.setString(1, null);
        exportStmt.setString(4, "|");
        exportStmt.setString(5, null);
        exportStmt.setString(6,null);
        exportStmt.setString(2, table);
        exportStmt.setString(3, file);
        
        exportStmt.execute();
        
        exportStmt.close();
        conn.close();
    }
    /* For a given table name, return its create string */
    /*
    public static String constructCreateTable(String dbname, String table, DerbyUtil du)
    throws Exception{
        List<String> l = du.getColumnsForTable(dbname, table);
        String sql = "("; //"create table " + table + " (";
        for (String col : l){
            String type = du.getColumnType(table, col);
            sql += " " + col + " " + type + ",";
        }
        sql = sql.substring(0, sql.length() - 1); // get rid of last ,
        sql += ")";
        
        return (sql);
    }
     * */
    public static String loadTableString(String table, String file) {
        if (file.equalsIgnoreCase("c/xanadb")) {
            try {
                throw new Exception("A");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        logger.log("Loading " + table + " with contents from " + file);
        String sql = "CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (null, ";
        sql = sql + "'" + table.toUpperCase() + "'" +",'" + file + "','|',null,null, 0)";
        
        return (sql);
    }
    public static String getNVPair(Connection conn, String name) {
        String value = "Not found";
        String sql = "select metavalue from Metatable where Metaname = '"
                + name + "'";
        
        try {
            ResultSet rset = executeQuery(conn.createStatement(), sql);
            while (rset.next()) {
                value = rset.getString(1);
            }
        }catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
        return value;
    }
    /** returns true of sql type (java.sql.Types) is Numeric */
    public static boolean isNumeric(int type) {
        switch (type) {
            case java.sql.Types.INTEGER:
            case java.sql.Types.FLOAT:
            case java.sql.Types.DOUBLE:
            case java.sql.Types.DECIMAL:
            case java.sql.Types.BIGINT:
            case java.sql.Types.REAL:
                return (true);
            default:
                return (false);
        }
    }
    public boolean isGraphic(int type) {
        if (type == FENXI_TRENDLINE_TYPE)
            return (true);
        return (false);
    }
    public static ArrayList<String> getSynonyms(Connection conn) throws Exception {
        ArrayList<String> synonyms = new ArrayList<String>();
        String sql = "select tablename from SYS.SYSTABLES where tabletype = 'A'";
        Statement stmt = conn.createStatement();
        ResultSet rset = DBHelper.executeQuery(stmt, sql);
        while (rset.next()) {
            synonyms.add(rset.getString(1));
        }
        return (synonyms);
    }
}

