<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
	<META HTTP-EQUIV="CONTENT-TYPE" CONTENT="text/html; charset=windows-1252">
	
</HEAD>
<BODY LANG="en-US" DIR="LTR">
<P ALIGN=CENTER STYLE="margin-top: 0.42cm; margin-bottom: 0.21cm; line-height: 100%; page-break-after: avoid">
<FONT FACE="Liberation Sans, sans-serif"><FONT SIZE=6 STYLE="font-size: 28pt"><B>JFXMultiZoomLineChartYAxis</B></FONT></FONT></P>
<P STYLE="margin-bottom: 0cm; line-height: 100%">At this time, with
JFX / SDK 9, there is no native LineChart with several Yaxis.</P>
<P STYLE="margin-bottom: 0cm; line-height: 100%">For my needs I
want&nbsp;:</P>
<P STYLE="margin-bottom: 0cm; line-height: 100%">- a XAxis shared by
all the LineCharts, it is a NumberAxis.</P>
<P STYLE="margin-bottom: 0cm; line-height: 100%">- the Xaxis can be a
time/date Axis ( in fact long in millis seconds since 01/01/1970 that
can be formated to date/Time =&gt; TickFormater / StringConverter)</P>
<P STYLE="margin-bottom: 0cm; line-height: 100%">- Several YAxis with
differents Units</P>
<P STYLE="margin-bottom: 0cm; line-height: 100%">- the LineCharts
must be Zoomable.</P>
<P STYLE="margin-bottom: 0cm; line-height: 100%">For not reinventing
the wheel, I search what already exist on Internet..</P>
<P STYLE="margin-bottom: 0cm; line-height: 100%">The aim of my work
is the class developed by <A HREF="https://gist.github.com/MaciejDobrowolski">MaciejDobrowolski</A>&nbsp;:</P>
<P STYLE="margin-bottom: 0cm; line-height: 100%"><A HREF="https://gist.github.com/MaciejDobrowolski/9c99af00668986a0a303">https://gist.github.com/MaciejDobrowolski/9c99af00668986a0a303</A></P>
<P STYLE="margin-bottom: 0cm; line-height: 100%">For the automatic
choose of TickFormater formating of the date/time, I get the tips ( 2
arrays) used here : 
</P>
<P STYLE="margin-bottom: 0cm; line-height: 100%"><A HREF="https://github.com/dukke/FXCharts/blob/master/DateAxis.java">https://github.com/dukke/FXCharts/blob/master/DateAxis.java</A></P>
<P STYLE="margin-bottom: 0cm; line-height: 100%">For Zooming, I start
from : 
</P>
<P STYLE="margin-bottom: 0cm; line-height: 100%"><A HREF="https://gist.github.com/james-d/7252698">https://gist.github.com/james-d/7252698</A></P>
<P STYLE="margin-bottom: 0cm; line-height: 100%">I used JDK9 /
Eclipse Oxygen/Gradle. I mixed the 3 sources and with a little
personal work, I can obtain the result below :</P>
<P STYLE="margin-bottom: 0cm; line-height: 100%"><IMG SRC="readme_md_m6354ea9a.jpg" NAME="images1" ALIGN=BOTTOM WIDTH=643 HEIGHT=396 BORDER=0>
</P>
<P STYLE="margin-bottom: 0cm; line-height: 100%">I developped it with
SDK 9 Oracle, but perhaps it is possible to compil the source with
SDK8. JFX has not very changed between the two versions. 
</P>
<P STYLE="margin-bottom: 0cm; line-height: 100%">The License is<A HREF="http://www.apache.org/licenses/LICENSE-2.0.html">
Apache 2.0.</A></P>
<P STYLE="margin-bottom: 0cm; line-height: 100%">There are some
examples in the package <FONT COLOR="#000000"><FONT FACE="Monospace"><FONT SIZE=2><SPAN STYLE="background: #e8f2fe">org.jlp.javafx.example</SPAN></FONT></FONT></FONT></P>
<P STYLE="margin-bottom: 0cm; line-height: 100%">You can take the jar
in a folder and launch it for example as :</P>
<P STYLE="margin-bottom: 0cm; background: #ccffff; font-style: normal; line-height: 100%">
<FONT FACE="Courier New, monospace"><FONT SIZE=2><B>/pathToJdk9Bin/java
-Xms1024M -Xmx1024M --module-path /pathToTheJarFile -m
org.jlp.javafx/org.jlp.javafx.example.ZoomableLineChartsMultiYAxisMainXDate</B></FONT></FONT></P>
<P STYLE="margin-bottom: 0cm; font-style: normal; line-height: 100%">The
archive jar, containing the module is org.jlp.javafx is
<B>libs/JFXMultiYZoomLineChart.jar</B></P>
<P STYLE="margin-bottom: 0cm; font-style: normal; font-weight: normal; line-height: 100%">
The command below becomes :</P>
<P STYLE="margin-bottom: 0cm; background: #ccffff; font-style: normal; line-height: 100%">
<FONT FACE="Courier New, monospace"><FONT SIZE=2><B>/pathToJdk9Bin/java
-Xms1024M -Xmx1024M --module-path /pathToParentOf_libs/libs -m
org.jlp.javafx/org.jlp.javafx.example.ZoomableLineChartsMultiYAxisMainXDate</B></FONT></FONT></P>
<P STYLE="margin-bottom: 0cm; line-height: 100%"><I>If you want to
test in older versions of JDK, you have to modify the JDK Source and
Target in build.gradle and rename file <B>module-info.java </B>( to
module-info.java.no_use for example).</I></P>
<P STYLE="margin-bottom: 0cm; line-height: 100%">I will continue to
improve these classes. Next step =&gt; coupling a TableView with the
LineCharts synchronized with the zoom in and reset actions.</P>
</BODY>
</HTML>