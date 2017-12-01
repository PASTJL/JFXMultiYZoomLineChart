#!/bin/bash
export JAVA_HOME=/opt/jdk-9.0.1"
SET JAVA_TOOL_OPTIONS=
$JAVA_HOME/bin/java" -Xms1024M -Xmx1024M  -javaagent:./scenicview.jar --module-path ./build/libs;./scenicview.jar   -m org.jlp.javafx/org.jlp.javafx.example.SimpleLineChartsMultiYAxisMain