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
 * InitDB.java
 *
 * Created on March 30, 2007, 5:18 PM
 *
 * Create and load the database
 */

package org.fenxi.cmd.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import org.fenxi.Logger;
import org.fenxi.db.DBHelper;

/**
 *
 * @author neel
 */
public class InitDB {
    private String dir;
    
    final static String[] sql = {
        //"drop table EXPORT_VIEW",
        "create table EXPORT_VIEW (tablename varchar(128), sectiondesc varchar(128), displaytype varchar(128), dims smallint, viewquery long varchar)",
        "create table Metatable (METANAME varchar(128), METAVALUE varchar(128), SOURCE varchar(128))",
    };
    /** Creates a new instance of InitDB
     * @param dir Directory where the database will be created
     */
    public InitDB(String dir) {
        this.dir = dir;
    }
    /** Execute
     * @return true/false Success or Failure?
     */
    public boolean execute(String name) {
        
        Connection conn = DBHelper.newConnection(dir + "/" + DBHelper.DBNAME + ";create=true");
        for (int i = 0; i < sql.length; i++) {
            try {
                PreparedStatement loadStmt = conn.prepareStatement(sql[i]);
                loadStmt.execute();
                loadStmt.close();
            } catch (java.sql.SQLException e) {
                Logger.getInstance().log("Error executing " + sql[i]);
                Logger.getInstance().log(e.toString());
                return (false);
            }
        }
        try {
            String sql1 = "insert into metatable values ('name', '" + name + "', 'initsql')";
            PreparedStatement loadStmt = conn.prepareStatement(sql1);
            loadStmt.execute();
            loadStmt.close();
            conn.close();
        } catch(Exception e) {
            Logger.getInstance().log("Error Executing initial set of queries");
            Logger.getInstance().log(e);
        }
        
        return (true);
    }
    
}
