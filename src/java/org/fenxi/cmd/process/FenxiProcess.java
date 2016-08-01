package org.fenxi.cmd.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.fenxi.Logger;

/**
 * The main entry point for the process command.
 * The raw files are processed to prepare database 'loadable'
 * files TMP_DIR. We create a database, create the export_view table,
 * and load files from TMP_DIR. After loading, queries are executed to
 * generate the results, which are then converted to HTML via a xsl stylesheet
 * 
 * @author neel
 */
public class FenxiProcess {

    static final String TMP_DIR = "txt";
    private boolean have_native_ruby;
    private String dbDir;
    private String inputLocation;
    private String exptName;
    private String fenxiDir;
    private Properties parserProperty;
    private static Logger logger = org.fenxi.Logger.getInstance();
    static final String fenxiParseProp = "/fenxi_parse_properties";
    private static final String progress = "-\\|/";

    private FenxiProcess(String src, String dest, String name) {
        this.dbDir = dest;
        this.inputLocation = src;
        this.exptName = name;
    }

    private static void Usage() {
        System.err.println("Usage: FenxiProcess <input-dir/file> <html-dir> <expt_name>");
        System.exit(-1);
    }

    private void execute() {
        File input = new File(inputLocation);
        File[] inputFiles;
        if (input.isDirectory()) {
            inputFiles = input.listFiles();
        } else {
            inputFiles = new File[1];
            inputFiles[0] = input;
        }
        fenxiDir = System.getProperty("fenxi.basedir");
        if (fenxiDir == null || fenxiDir.length() == 0) {
            System.err.println("fenxi.basedir is not set");
            return;
        }
        if (System.getProperty("fenxi.have_native_ruby") != null) {
            have_native_ruby = true;
        } else {
            have_native_ruby = false;
        }
        try {
            File dstdir = new File(dbDir);
            dstdir.mkdir();
            long now = System.currentTimeMillis();
            InitDB init = new InitDB(dbDir);
            logger.log("Creating initial tables");
            System.out.printf("%-48s ", "Creating database ...");
            if (init.execute(exptName) == false) {
                System.err.println("\nError creating initial tables");
                return;
            }
            System.out.printf("%-3.2fs\n",
                              (System.currentTimeMillis() - now) / 1000.0);
            now = System.currentTimeMillis();
            System.out.printf("%-48s ", "Parsing raw files ...");
            logger.log("Processing files");
            process(inputFiles);
            System.out.printf("%-3.2fs\n",
                              (System.currentTimeMillis() - now) / 1000.0);

            logger.log("Loading files");
            LoadData ld = new LoadData(dbDir, TMP_DIR);
            if (ld.execute() == false) {
                System.err.println("Error loading data into database");
                return;
            }
            logger.log("Generating view data");
            //new DBViewerMT(dbDir, dbDir).execute();
            FenXiProfile xp = new FenXiProfile(dbDir);
            if (xp.execute() == false) {
                System.err.println("WARNING: Error executing fenxi profile");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void process(File[] files) {
        boolean doParallel = true;
        if (loadParserProperties() == false) {
            System.err.println("Error loading parser properties");
            return;
        }
        ArrayList<Thread> tlist = new ArrayList<Thread>();
        for (final File file : files) {
            logger.log("Processing " + file.toString());
            if (doParallel) {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        processfile(file);
                    }
                };
                t.start();
                tlist.add(t);
            } else {
                processfile(file);
            }
        }
        if (doParallel) {
            for (Thread t : tlist) {
                try {
                    t.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean processfile(File file) {
        String converter = null;
        String xhome = fenxiDir + java.io.File.separator + "txt2db" + java.io.File.separator;

        for (Enumeration e = parserProperty.propertyNames(); e.hasMoreElements();) {
            String parseProp = e.nextElement().toString();
            Pattern p = Pattern.compile(parseProp);
            Matcher m = p.matcher(file.getName());
            if (m.find()) {
                converter = xhome + parserProperty.getProperty(parseProp);
                if (converter.endsWith(".pl")) {
                    converter = "perl " + converter;
                } else if (converter.endsWith(".rb") && have_native_ruby) {
                    converter = "ruby " + converter;
                } else if (converter.endsWith(".rb")) {
                    String jrbhome = fenxiDir + java.io.File.separator + "WEB-INF" +
                            java.io.File.separator + "lib" + java.io.File.separator;
                    String[] jars = new File(jrbhome).list();
                    for (String jar : jars) {
                        if (jar.startsWith("jruby-complete-") && jar.endsWith(".jar")) {
                            jrbhome += jar + ' ';
                            break;
                        }
                    }

                    converter = "java -Xms64m -Xmx512m -jar " + jrbhome + converter;
                }
                break;
            }
            if (!e.hasMoreElements()) {
                return false;
            }
        }


        logger.log("PARSING " + file.toString());
        logger.log("Converter = " + converter);

        try {

            Runtime myRuntime = Runtime.getRuntime();
            String cmd = converter + " " + file.toString() + " " + TMP_DIR;
            logger.log(cmd);
            Process p = myRuntime.exec(cmd);
            InputStream is = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String str;
            while ((str = in.readLine()) != null) {
                logger.log(converter + " : " + str);
            }
            is = p.getErrorStream();
            in = new BufferedReader(new InputStreamReader(is));
            while ((str = in.readLine()) != null) {
                logger.log(converter + " : " + str);
            }

            if (p.waitFor() != 0) {
                System.err.println("Error in executing " + converter);
            }
        } catch (Exception e) {
            logger.log(e);
            return false;
        }
        return true;
    }

    private boolean loadParserProperties() {

        InputStream isParse = this.getClass().getResourceAsStream(fenxiParseProp);
        if (isParse != null) {
            parserProperty = new Properties();
            try {
                parserProperty.load(isParse);
            } catch (Exception e) {
                System.err.println("********* Error reading property file ********");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            Usage();
        }

        FenxiProcess me = new FenxiProcess(args[0], args[1], args[2]);
        me.execute();
    }
}
