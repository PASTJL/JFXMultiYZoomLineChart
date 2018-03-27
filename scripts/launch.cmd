set JAVA_HOME=C:\Program Files\Java\jdk-9.0.4
set root=C:\opt\workspace47\JFXMultiYZoomLineChart
set workspace=C:\opt\workspaceLP
"%JAVA_HOME%\bin\java" -Xms1024M -Xmx1024M -Droot=%root% -Dworkspace=%workspace%  --module-path %root%\libs -m org.jlp.javafx/org.jlp.javafx.example.CSVChartViewerMain
