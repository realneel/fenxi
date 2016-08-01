Source code for the Fenxi Project. Details about fenxi are available at
http://fenxi.dev.java.net. Please email questions and comments to
dev_AT_fenxi_DOT_DEV_JAVA_NET. Bugs/RFEs can filed at the fenxi website.
Fenxi uses the CDDL opensource license, and is developed by members of the
Performance Applications Engineering group at Sun Microsystems

Using fenxi
============
To use the static version of fenxi, you can use
$FENXI_HOME/scripts/fenxi process <RAWDIR> <HTMLDIR> <RUNDESC> or
$FENXI_HOME/scripts/fenxi compare <DIR1> <DIR2> ... <OUTDIR>
More examples can be found at the fenxi website

To use the dynamic version of fenxi, just deploy the fenxi.war to a
web container and visit something like http://foobar/fenxi/


Compiling Fenxi
===============
rake build

Testing Fenxi
==============
To run unit tests (you need jruby for this)
rake test

To run blackbox tests
rake testrun (any ruby will do)

To debug the dynamic part of fenxi, you can cd to src/rails and run
script/server

Requirements
============
To use fenxi, the only requirement is that you have Java 5 or higher installed.

To develop or build fenxi, you need
1. Java 5+
2. JRuby with Rails 2.2.2, rake, and warbler installed. This is usually done
   by running
   gem install rails warbler
  (be sure to use the gem command provided by jruby and not the system one)

if you are on swan, you can prepend this to your PATH
/net/paedata.sfbay/paedata1/neel/jruby-1.16/bin
