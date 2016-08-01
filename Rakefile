# Fenxi build file. To build the war use rake build
require 'rake/testtask'

# What tasks do we need?
# Clean up dist                     ===> cleandist
# Clean up files created by testrun ===> cleantest
# clean up various othe files.      ===> cleanother
# clean everything                  ===> clean
# prepare to build                  ===> prepare_build
# Compile java files                ===> build_java
# copy ruby source to dist/ruby     ===> build_ruby
# copy libs to dist/WEB-INF/lib     ===> build_libs
# copy other files to dist          ===> build_other
# generate war                      ===> build_war
# fix permissions                   ===> fixpermissions
# test process                      ===> testrun
# test compare                      ===> testcompare
# test unit                         ===> testunit


BUILD_DIRS=%w|dist/WEB-INF/classes dist/WEB-INF/lib dist/html dist/scripts 
              dist/ruby/fenxi dist/ruby/test dist/txt2db|

task :default => ["build"]

BUILD_DIRS.each do |dir|
  directory dir
end

def build(name, srcfiles, pattern1, pattern2)
  srcfiles.each do |srcfile|
    next if File.directory?(srcfile)
    destfile = srcfile.gsub(pattern1, pattern2)
    file destfile => srcfile do
      cp srcfile, destfile
    end
  end
  task name => srcfiles.sub(pattern1, pattern2)
end

JARS=FileList['lib/*jar']
OTHER_FILES=FileList['src/ruby/**/*rb', 'src/html/*', 'src/txt2db/*', 'src/scripts/*']
SPECIAL_FILES=FileList['src/html/fenxi_*']

build("jars", JARS, /^lib/, "dist/WEB-INF/lib")
build("other_files", OTHER_FILES, /^src/, "dist")
build("sfiles", SPECIAL_FILES, "src/html", "dist/WEB-INF/classes")

desc "Clean up everything"
task :clean => [:cleandist, :cleantest, :cleanother] 

task :cleandist do
  rm_rf 'dist'
end

task :cleantest do
  rm_rf %w|testhtml test1html testcomparehtml test.html test1.html|
  rm FileList.new("**/fenxi.log")
  rm FileList.new("**/derby.log")
end

task :cleanother do  
  rm_f FileList.new(%w|fenxi.war fenxi.jar|)
end

task :prepare_build => BUILD_DIRS

desc "Build java"
task :build_java => :prepare_build do |t|
  f = FileList.new("src/java/**/*.java")
  target_dir = "dist/WEB-INF/classes"
  classpath = FileList.new('lib/**/*.jar').include(target_dir).join(":")
  sh "javac -Xlint -d #{target_dir} -cp #{classpath} #{f}"
end

task :build_libs => "jars"
task :build_other => ["other_files", "sfiles"]

desc "Build everything"
task :build => [:prepare_build, :build_libs, :build_other, :build_java, 
  :fix_permissions] do |t|
  puts "Done"
end

task :fix_permissions => :build_other do |t|
  sh "chmod -R a+rx dist/scripts dist/txt2db dist/ruby"
  sh "chmod -R a+r dist"
end


desc "Create war"
task :build_war => :build do |t|
  system("cd src/rails && jruby -S warble && mv fenxi.war ../..")
end

desc "Test"
Rake::TestTask.new do |t|
  t.libs << "src/ruby"
  t.test_files = FileList['src/ruby/test*.rb']
  t.verbose = true
end

desc "Test load"
task :testload => ["testhtml", "test1html"]
task :testcompare => ["testhtml", "test1html"] do |t|
  sh "time ./dist/scripts/fenxi compare test1html testhtml testcomparehtml"
end

task :testall => [:test, :testcompare]

["testhtml", "test1html"]. each do |t|
  file t => :build do
    sh "time ./dist/scripts/fenxi process test #{t} #{t}"
  end
end