<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
	<title></title>
	<meta name="generator" content="LibreOffice 5.4.5.1 (Linux)"/>
	<meta name="created" content="2017-11-20T11:24:41.257874917"/>
	<meta name="changed" content="2018-03-27T08:39:50.256248008"/>
	<style type="text/css">
		@page { margin: 2cm }
		p { margin-bottom: 0.25cm; line-height: 120% }
		h1 { margin-bottom: 0.21cm }
		h1.western { font-family: "Liberation Serif", serif }
		h1.cjk { font-family: "Noto Sans CJK SC Regular"; font-size: 24pt }
		h1.ctl { font-family: "Lohit Devanagari"; font-size: 24pt }
		a:link { so-language: zxx }
	</style>
</head>
<body lang="en-US" dir="ltr">
<p align="center" style="margin-top: 0.42cm; margin-bottom: 0.21cm; line-height: 100%; page-break-after: avoid">
<font face="Liberation Sans, sans-serif"><font size="6" style="font-size: 28pt"><b>JFXMultiZoomLineChartYAxis</b></font></font></p>
<ol>
	<li/>
<h1 class="western">LineChart multi Y-Axis</h1>
</ol>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-bottom: 0cm; line-height: 100%">At this time, with
JFX / SDK 9, there is no native LineChart with several YAxis.</p>
<p style="margin-bottom: 0cm; line-height: 100%">For my needs i
want&nbsp;:</p>
<p style="margin-bottom: 0cm; line-height: 100%">- a XAxis shared by
all the LineChart, it is a NumberAxis.</p>
<p style="margin-bottom: 0cm; line-height: 100%">- the Xaxis can be a
time/date Axis ( in fact long since 01/01/1970 that can be formated
to date/Time =&gt; TickFormater / StringConverter)</p>
<p style="margin-bottom: 0cm; line-height: 100%">- Several YAxis with
differents Units</p>
<p style="margin-bottom: 0cm; line-height: 100%">- the LineCharts
must be Zoomable.</p>
<p style="margin-bottom: 0cm; line-height: 100%">For not reinventing
the wheel, i search what exists on Internet..</p>
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
<p style="margin-bottom: 0cm; line-height: 100%">I mixed the 3
sources and with a little personal work, I can obtain the result
below :</p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-bottom: 0cm; line-height: 100%"><img src="readme_html_d8ce468dc07d2661.png" name="Image1" align="bottom" width="643" height="399" border="0"/>

</p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-bottom: 0cm; line-height: 100%">I developped it with
SDK 9 Oracle, but perhaps it is possible to compil the source with
SDK8. JFX has not very changed between the two versions. 
</p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-bottom: 0cm; line-height: 100%">There are some
examples in the package <font color="#000000"><font face="Monospace"><font size="2" style="font-size: 10pt"><span style="background: #e8f2fe">org.jlp.javafx.example</span></font></font></font></p>
<p style="margin-bottom: 0cm; line-height: 100%">You can take the jar
at the root and launch it for example as :</p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-bottom: 0cm; line-height: 100%"><i>/pathToJdk9Bin/-Xms1024M
-Xmx1024M --module-path /pathToTheJarFile -m
org.jlp.javafx/org.jlp.javafx.example.ZoomableLineChartsMultiYAxisMainXDate</i></p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<ol start="2">
	<li/>
<h1 class="western">Charting with CSV files</h1>
</ol>
<p>The class <b>org.jlp.javafx.example.CSVChartViewerMain</b> is the
main class to graph some csv files which must respect some
conditions:</p>
<p>The first line is the title, with the mandatory format below :</p>
<p lang="en-GB" style="margin-left: 0.5cm; font-weight: normal"><b>DateTime</b>;<b>Pivots</b>;Value1(hit/s);Value2(ms);</p>
<p lang="en-GB" style="margin-left: 0.5cm; font-weight: normal">The
first column is a <b>DateTime</b> ( The title must contains <b>Date</b>
or/And <b>Time</b> in the name). The format of the date must be
handled by regex and Java DateFormat described in file
<b>./config/logFouineurDates.properties. </b>If the format is not
recognized, it must be added into yjis file ( Be care to do a backup
of this file before modifying it, it is a bit tricky).</p>
<p lang="en-GB" style="margin-left: 0.5cm; font-weight: normal">The
second column <b>Pivots</b>, is optionnal, and is useful to sort the
values ( Exemple different kind of URL after parsing an http access
log file).</p>
<p lang="en-GB" style="margin-left: 0.5cm; font-weight: normal">The
others column are <b>Values,</b> between parenthesis, you indicate
the unit of the value. If absent a default “unit” is attributed.</p>
<p lang="en-GB" style="margin-left: 0.5cm">The semi-colon is the csv
field separator.</p>
<p lang="en-GB" style="margin-left: 0.5cm">The tool allows to have
several Y-Axis.</p>
<p lang="en-GB" style="margin-left: 0.5cm">Read the quick manual in
manual repository.</p>
<p lang="en-GB" style="margin-left: 0.5cm">A script to launch it on a
Linux desktop :</p>
<p lang="en-GB" style="margin-bottom: 0cm; line-height: 100%; background: #729fcf">
<font face="Monospace"><font size="2" style="font-size: 10pt">JAVA_HOME=/opt/</font></font><font color="#31363b"><font face="Monospace"><font size="2" style="font-size: 10pt"><u>jdk</u></font></font></font><font face="Monospace"><font size="2" style="font-size: 10pt">-9.0.4</font></font></p>
<p align="left" style="margin-bottom: 0cm; line-height: 100%; background: #729fcf">
<font face="Monospace"><font size="2" style="font-size: 10pt">root=/opt/workspace47/JFXMultiYZoomLineChart</font></font></p>
<p align="left" style="margin-bottom: 0cm; line-height: 100%; background: #729fcf">
<font face="Monospace"><font size="2" style="font-size: 10pt">workspace=/opt/workspaceLP</font></font></p>
<p align="left" style="margin-bottom: 0cm; line-height: 100%; background: #729fcf">
<font face="Monospace"><font size="2" style="font-size: 10pt">$JAVA_HOME/bin/java
-Xms1024M -Xmx1024M -</font></font><font color="#31363b"><font face="Monospace"><font size="2" style="font-size: 10pt"><u>Droot</u></font></font></font><font face="Monospace"><font size="2" style="font-size: 10pt">=$root
-</font></font><font color="#31363b"><font face="Monospace"><font size="2" style="font-size: 10pt"><u>Dworkspace</u></font></font></font><font face="Monospace"><font size="2" style="font-size: 10pt">=$workspace
--module-path $root/</font></font><font color="#31363b"><font face="Monospace"><font size="2" style="font-size: 10pt"><u>libs</u></font></font></font>
<font face="Monospace"><font size="2" style="font-size: 10pt">-m
org.jlp.javafx/org.jlp.javafx.example.CSVChartViewerMain</font></font></p>
<p style="margin-left: 0.5cm; margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p lang="en-GB" style="margin-left: 0.5cm">The same on a Windows
desktop :</p>
<p lang="en-GB" style="margin-bottom: 0cm; line-height: 100%; background: #729fcf">
<font face="Monospace"><font size="2" style="font-size: 10pt">set
JAVA_HOME=C:\Program Files\Java\</font></font><font color="#31363b"><font face="Monospace"><font size="2" style="font-size: 10pt"><u>jdk</u></font></font></font><font face="Monospace"><font size="2" style="font-size: 10pt">-9.0.4</font></font></p>
<p align="left" style="margin-bottom: 0cm; line-height: 100%; background: #729fcf">
<font face="Monospace"><font size="2" style="font-size: 10pt">set
root=C:\opt\workspace47\JFXMultiYZoomLineChart</font></font></p>
<p align="left" style="margin-bottom: 0cm; line-height: 100%; background: #729fcf">
<font face="Monospace"><font size="2" style="font-size: 10pt">set
workspace=C:\opt\workspaceLP</font></font></p>
<p align="left" style="margin-bottom: 0cm; line-height: 100%; background: #729fcf">
<font face="Monospace"><font size="2" style="font-size: 10pt">&quot;%JAVA_HOME%\bin\java&quot;
-Xms1024M -Xmx1024M -</font></font><font color="#31363b"><font face="Monospace"><font size="2" style="font-size: 10pt"><u>Droot</u></font></font></font><font face="Monospace"><font size="2" style="font-size: 10pt">=%root%
-</font></font><font color="#31363b"><font face="Monospace"><font size="2" style="font-size: 10pt"><u>Dworkspace</u></font></font></font><font face="Monospace"><font size="2" style="font-size: 10pt">=%workspace%
--module-path %root%\</font></font><font color="#31363b"><font face="Monospace"><font size="2" style="font-size: 10pt"><u>libs</u></font></font></font>
<font face="Monospace"><font size="2" style="font-size: 10pt">-m
org.jlp.javafx/org.jlp.javafx.example.CSVChartViewerMain</font></font></p>
<p style="margin-left: 0.5cm; margin-bottom: 0cm; line-height: 100%"><br/>

</p>
<p style="margin-left: 0.5cm"><br/>
<br/>

</p>
<p lang="en-GB" style="margin-left: 0.5cm; page-break-before: always">
Below a screen shot:</p>
<p style="margin-left: 0.5cm"><br/>
<br/>

</p>
<p style="margin-left: 0.5cm"><img src="readme_html_3600c0052a4b9116.jpg" name="Image2" align="bottom" width="624" height="351" border="0"/>
</p>
<p style="margin-left: 0.5cm"><br/>
<br/>

</p>
<p style="margin-left: 0.5cm">for the csv file :</p>
<p style="margin-left: 0.5cm; margin-bottom: 0cm; line-height: 100%">
<font face="DejaVu Sans Mono, monospace"><font size="1" style="font-size: 6pt">DateTime;POSTkrmx69es_durationbis_Avg(s);POSTkrmx69be_durationbis_Avg(s);POSTkrmx69ch_durationbis_Avg(s);POSTkrmx69pl_durationbis_Avg(s);global_durationbis_Avg(s);</font></font></p>
<p style="margin-left: 0.5cm; margin-bottom: 0cm; line-height: 100%"><font size="1" style="font-size: 6pt"><font face="DejaVu Sans Mono, monospace">2010/05/12:15:41:14;4.83045924137931;16.277920499999997;7.42722225;10.972653499999998;8.432598881355931;</font></font></p>
<p style="margin-left: 0.5cm; margin-bottom: 0cm; line-height: 100%"><font face="DejaVu Sans Mono, monospace"><font size="1" style="font-size: 6pt">2010/05/12:15:41:15;4.059669388888889;16.774655631578945;10.7794122;13.80661;9.79711650617284;</font></font></p>
<p style="margin-left: 0.5cm; margin-bottom: 0cm; line-height: 100%"><font face="DejaVu Sans Mono, monospace"><font size="1" style="font-size: 6pt">2010/05/12:15:41:16;1.9068376666666669;20.306890666666664;7.410049;;10.578747714285715;</font></font></p>
<p style="margin-left: 0.5cm; margin-bottom: 0cm; line-height: 100%"><font face="DejaVu Sans Mono, monospace"><font size="1" style="font-size: 6pt">2010/05/12:15:41:17;1.3363226666666665;13.781568666666665;4.5099730000000005;6.431179;6.267221892857142;</font></font></p>
<p style="margin-left: 0.5cm; margin-bottom: 0cm; line-height: 100%"><font face="DejaVu Sans Mono, monospace"><font size="1" style="font-size: 6pt">2010/05/12:15:41:18;0.1924879024390244;7.612964549999998;3.5619031999999997;4.734340499999999;2.756316058823529;</font></font></p>
<p style="margin-left: 0.5cm; margin-bottom: 0cm; line-height: 100%"><font face="DejaVu Sans Mono, monospace"><font size="1" style="font-size: 6pt">2010/05/12:15:41:19;7.145098851851851;6.7216338124999995;;;7.0483068428571425;</font></font></p>
</body>
</html>