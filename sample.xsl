<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<html>
  <head>
	<title>Title of Automation Test</title>
	<style type="text/css">
		html{background:#ccc;padding 10px;}
		body{width:900px;margin:0 auto;background:white;height:80%;text-align:center; padding:10px;}
		table{width:100%;text-align:center; }
		td,th{border:#888 1px solid;}
		tr{background:#fff;}
		tr:hover{background:#d3dcef;}
		.pass, .fail, .warn {color:white;font:bold;}
		.pass {background:#00b500;}
		.fail {background:#e70301;}
		.warn {background:#dfbe01;}		
	</style>
  </head>
  <body>
	<h1>My JMeter test header</h1>
	  <table>
		<thead>
			<tr>
				<th style="width:70px">Status</th>
				<th style="width:100px">Response Time</th>
				<th style="width:200px">Name of job</th>
				<th style="width:100px">Response Code</th>				
				<th style="width:400px">Request URL</th>
			</tr>
		</thead>
		<xsl:for-each select="/testResults/httpSample">
			<tr>
				<xsl:choose>
					<xsl:when test="@s='true'"><td class="pass">PASS</td></xsl:when>
					<xsl:otherwise><td class="fail">FAIL</td></xsl:otherwise>
				</xsl:choose>				
				<td><xsl:value-of select="@t"/></td>
				<td><xsl:value-of select="@lb"/></td>
				<td><xsl:value-of select="@rc"/></td>
				<td><xsl:value-of select="java.net.URL"/></td>
			</tr>
		</xsl:for-each>		
	  </table>	 
  </body>
</html>
</xsl:template>
</xsl:stylesheet>