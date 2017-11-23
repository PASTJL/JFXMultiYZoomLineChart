<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
	<title></title>
	<meta name="generator" content="LibreOffice 5.4.1.2 (Linux)"/>
	<meta name="created" content="00:00:00"/>
	<meta name="changed" content="00:00:00"/>
</head>
<body lang="en-US" dir="ltr">
<p align="center" style="margin-top: 0.42cm; margin-bottom: 0.21cm; line-height: 100%; page-break-after: avoid">
<font face="Liberation Sans, sans-serif"><font size="6" style="font-size: 28pt"><b>JFXMultiZoomLineChartYAxis</b></font></font></p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-bottom: 0cm; line-height: 100%">At this time, with
JFX / SDK 9, there is no native LineChart with several Yaxis.</p>
<p style="margin-bottom: 0cm; line-height: 100%">For my needs I
want&nbsp;:</p>
<p style="margin-bottom: 0cm; line-height: 100%">- a XAxis shared by
all the LineCharts, it is a NumberAxis.</p>
<p style="margin-bottom: 0cm; line-height: 100%">- the Xaxis can be a
time/date Axis ( in fact long in millis seconds since 01/01/1970 that
can be formated to date/Time =&gt; TickFormater / StringConverter)</p>
<p style="margin-bottom: 0cm; line-height: 100%">- Several YAxis with
differents Units</p>
<p style="margin-bottom: 0cm; line-height: 100%">- the LineCharts
must be Zoomable.</p>
<p style="margin-bottom: 0cm; line-height: 100%">For not reinventing
the wheel, I search what already exist on Internet..</p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-bottom: 0cm; line-height: 100%">The aim of my work
is the class developed by <a href="https://gist.github.com/MaciejDobrowolski">MaciejDobrowolski</a>&nbsp;:</p>
<p style="margin-bottom: 0cm; line-height: 100%"><a href="https://gist.github.com/MaciejDobrowolski/9c99af00668986a0a303">https://gist.github.com/MaciejDobrowolski/9c99af00668986a0a303</a></p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-bottom: 0cm; line-height: 100%">For the automatic
choose of TickFormater formating of the date/time, I get the tips ( 2
arrays) used here : 
</p>
<p style="margin-bottom: 0cm; line-height: 100%"><a href="https://github.com/dukke/FXCharts/blob/master/DateAxis.java">https://github.com/dukke/FXCharts/blob/master/DateAxis.java</a></p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-bottom: 0cm; line-height: 100%">For Zooming, I start
from : 
</p>
<p style="margin-bottom: 0cm; line-height: 100%"><a href="https://gist.github.com/james-d/7252698">https://gist.github.com/james-d/7252698</a></p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-bottom: 0cm; line-height: 100%">I used JDK9 /
Eclipse Oxygen/Gradle. I mixed the 3 sources and with a little
personal work, I can obtain the result below :</p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-bottom: 0cm; line-height: 100%"><img src="readme_md_m6354ea9a.jpg" name="images1" align="bottom" width="643" height="396" border="0"/>

</p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-bottom: 0cm; line-height: 100%">I developped it with
SDK 9 Oracle, but perhaps it is possible to compil the source with
SDK8. JFX has not very changed between the two versions. 
</p>
<p style="margin-bottom: 0cm; line-height: 100%">The License is<a href="http://www.apache.org/licenses/LICENSE-2.0.html">
Apache 2.0.</a></p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-bottom: 0cm; line-height: 100%">There are some
examples in the package <font color="#000000"><font face="Monospace"><font size="2" style="font-size: 10pt"><span style="background: #e8f2fe">org.jlp.javafx.example</span></font></font></font></p>
<p style="margin-bottom: 0cm; line-height: 100%">You can take the jar
at the root and launch it for example as :</p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-bottom: 0cm; font-variant: normal; font-style: normal; line-height: 100%; background: #ccffff">
<font face="Courier New, monospace"><font size="2" style="font-size: 10pt"><b>/pathToJdk9Bin/java
-Xms1024M -Xmx1024M --module-path /pathToTheJarFile -m
org.jlp.javafx/org.jlp.javafx.example.ZoomableLineChartsMultiYAxisMainXDate</b></font></font></p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-bottom: 0cm; font-variant: normal; font-style: normal; line-height: 100%">
The archive jar, containing the module org.jlp.javafx is
<b>libs/JFXMultiYZoomLineChart.jar</b></p>
<p style="margin-bottom: 0cm; font-variant: normal; font-style: normal; font-weight: normal; line-height: 100%">
The command below becomes :</p>
<p style="margin-bottom: 0cm; font-variant: normal; font-style: normal; line-height: 100%; background: #ccffff">
<font face="Courier New, monospace"><font size="2" style="font-size: 10pt"><b>/pathToJdk9Bin/java
-Xms1024M -Xmx1024M --module-path /pathToParentOf_libs/libs -m
org.jlp.javafx/org.jlp.javafx.example.ZoomableLineChartsMultiYAxisMainXDate</b></font></font></p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-bottom: 0cm; line-height: 100%"><i>If you want to
test in early version of JDK, you have to modify the JDK Source and
Target in build.gradle and rename file <b>module-info.java </b>( to
module-info.java.no_use for example).</i></p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-bottom: 0cm; line-height: 100%">I will continue to
improve these classes. Next step =&gt; coupling a TableView with the
LineCharts synchronized with the zoom in and reset actions.</p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
</body>
</html>