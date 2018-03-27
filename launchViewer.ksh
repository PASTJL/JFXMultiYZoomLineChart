#!/bin/bash
export JAVA_HOME=/opt/jdk-9.0.4
PROFILER="-XX:+UnlockCommercialFeatures -XX:+FlightRecorder -XX:StartFlightRecording=duration=60s,filename=myrecording.jfr "
$JAVA_HOME/bin/java -Droot=. -Dworkspace=/opt/workspaceLP -Xms1024M -Xmx1024M --module-path /opt/workspace47/JFXMultiYZoomLineChart/libs  -m org.jlp.javafx/org.jlp.javafx.example.CSVChartViewerMain
