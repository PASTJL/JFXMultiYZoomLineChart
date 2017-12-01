set JAVA_HOME="C:\Program Files\Java\jdk-9.0.1"
SET JAVA_TOOL_OPTIONS=
"C:\Program Files\Java\jdk-9.0.1\bin\java" -Xms1024M -Xmx1024M  -javaagent:./scenicview.jar --module-path ./build/libs;./scenicview.jar   -m org.jlp.javafx/org.jlp.javafx.example.SimpleLineChartsMultiYAxisMain