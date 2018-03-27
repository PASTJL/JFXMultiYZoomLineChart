JAVA_HOME=/opt/jdk-9.0.4
root=/opt/workspace47/JFXMultiYZoomLineChart
workspace=/opt/workspaceLP
$JAVA_HOME/bin/java -Xms1024M -Xmx1024M -Droot=$root -Dworkspace=$workspace  --module-path $root/libs -m org.jlp.javafx/org.jlp.javafx.example.CSVChartViewerMain
