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
 * FenXiProfile.java
 *
 * Created on April 2, 2007, 2:11 PM
 *
 */
package org.fenxi.cmd.process;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import org.fenxi.Logger;
import org.fenxi.db.DBHelper;

/**
 *
 * @author neel
 */
;

public class FenXiProfile {

    private String db_dir;
    final static String profile_property = "fenxi.profile";
    private Logger logger;
    private String profile;

    /**
     * Creates a new instance of FenXiProfile
     */
    public FenXiProfile(String db_dir) {
        this.db_dir = db_dir;
        logger = Logger.getInstance();
    }

    /** Profile files basically consist of sql statements. Look at
     * default_profile for examples
     */
    private boolean loadAndExecuteProfile(String profile_file) {
        String sql;
        try {
            Connection conn = DBHelper.newConnection(db_dir + "/" + DBHelper.DBNAME);
            BufferedReader in = new BufferedReader(new FileReader(profile_file));
            for (;;) {
                if ((sql = in.readLine()) == null) {
                    break;
                }
                if (sql.startsWith("--") || sql.length() == 0) {
                    continue;
                }
                try {
                    PreparedStatement loadStmt = conn.prepareStatement(sql);
                    loadStmt.execute();
                } catch (java.sql.SQLException sqe) {
                    logger.log("Error executing " + sql);
                    logger.log(sqe);
                /* Ignore errors */
                }
            }
            in.close();
        } catch (java.io.IOException e) {
            logger.log("error in processing profile : " + profile_file);
            logger.log(e);
            return (false);
        }
        return (true);
    }

    public boolean execute() {
        boolean error;
        String fenxiBaseDir = System.getProperty("fenxi.basedir");
        if (fenxiBaseDir == null) {
            System.err.println("fenxi.basedir not set");
            return (false);
        }
        String p = System.getProperty(profile_property);
        this.profile = (p == null) ? "default_profile" : p;
        logger.log("Using profile: " + this.profile);
        if (this.profile.matches("none_profile")) {
            return (true);
        }
        System.out.printf("Processing profile %-29s ...", this.profile);
        String profile_file = fenxiBaseDir + java.io.File.separator + "txt2db" + java.io.File.separator + this.profile;
        if ((error = loadAndExecuteProfile(profile_file))) {
            System.out.println("OK");
        } else {
            System.out.println("Err");
        }
        return (error);
    }
}
