<?xml version="1.0" encoding="UTF-8"?>

<!--
	File:        freemind2html_standalone.xsl
	Version:     1.0.0
	Description: A standalone variant of freemind2html.xsl that produces
	self-contained HTML files with no external dependencies.
	- CSS is inlined in a <style> block
	- JavaScript for folding is inlined in a <script> block
	- Icons are rendered as Unicode emojis instead of <img> references
	- No _files/ directory is created
	Based on: freemind2html.xsl by Markus Brueckner
	License: BSD license without advertising clause.
-->
<xsl:stylesheet version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                >
<xsl:output method="xml"
            version="1.0"
            encoding="UTF-8"
            doctype-public="-//W3C//DTD XHTML 1.1//EN"
            doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"
            omit-xml-declaration="no"
            />

<xsl:param name="folding_type">html_export_no_folding</xsl:param>
<xsl:param name="show_icons">true</xsl:param>
<xsl:param name="show_link_url">false</xsl:param>

<!-- === Emoji Mapping via XSLT choose === -->
<xsl:template name="icon-to-emoji">
	<xsl:param name="name"/>
	<xsl:choose>
		<!-- Status / decision -->
		<xsl:when test="$name='idea'">&#x1F4A1;</xsl:when>
		<xsl:when test="$name='help'">&#x2753;</xsl:when>
		<xsl:when test="$name='yes'">&#x2714;&#xFE0F;</xsl:when>
		<xsl:when test="$name='messagebox_warning'">&#x26A0;&#xFE0F;</xsl:when>
		<xsl:when test="$name='stop-sign'">&#x1F6D1;</xsl:when>
		<xsl:when test="$name='closed'">&#x1F512;</xsl:when>
		<xsl:when test="$name='info'">&#x2139;&#xFE0F;</xsl:when>
		<xsl:when test="$name='button_ok'">&#x2705;</xsl:when>
		<xsl:when test="$name='button_cancel'">&#x274C;</xsl:when>
		<!-- Numbers -->
		<xsl:when test="$name='full-1'">1&#xFE0F;&#x20E3;</xsl:when>
		<xsl:when test="$name='full-2'">2&#xFE0F;&#x20E3;</xsl:when>
		<xsl:when test="$name='full-3'">3&#xFE0F;&#x20E3;</xsl:when>
		<xsl:when test="$name='full-4'">4&#xFE0F;&#x20E3;</xsl:when>
		<xsl:when test="$name='full-5'">5&#xFE0F;&#x20E3;</xsl:when>
		<xsl:when test="$name='full-6'">6&#xFE0F;&#x20E3;</xsl:when>
		<xsl:when test="$name='full-7'">7&#xFE0F;&#x20E3;</xsl:when>
		<xsl:when test="$name='full-8'">8&#xFE0F;&#x20E3;</xsl:when>
		<xsl:when test="$name='full-9'">9&#xFE0F;&#x20E3;</xsl:when>
		<xsl:when test="$name='full-0'">0&#xFE0F;&#x20E3;</xsl:when>
		<!-- Traffic lights -->
		<xsl:when test="$name='stop'">&#x1F534;</xsl:when>
		<xsl:when test="$name='prepare'">&#x1F7E1;</xsl:when>
		<xsl:when test="$name='go'">&#x1F7E2;</xsl:when>
		<!-- Navigation -->
		<xsl:when test="$name='back'">&#x2B05;&#xFE0F;</xsl:when>
		<xsl:when test="$name='forward'">&#x27A1;&#xFE0F;</xsl:when>
		<xsl:when test="$name='up'">&#x2B06;&#xFE0F;</xsl:when>
		<xsl:when test="$name='down'">&#x2B07;&#xFE0F;</xsl:when>
		<!-- Objects -->
		<xsl:when test="$name='attach'">&#x1F4CE;</xsl:when>
		<xsl:when test="$name='ksmiletris'">&#x1F604;</xsl:when>
		<xsl:when test="$name='smiley-neutral'">&#x1F610;</xsl:when>
		<xsl:when test="$name='smiley-oh'">&#x1F62E;</xsl:when>
		<xsl:when test="$name='smiley-angry'">&#x1F620;</xsl:when>
		<xsl:when test="$name='smily_bad'">&#x1F61E;</xsl:when>
		<xsl:when test="$name='clanbomber'">&#x1F4A3;</xsl:when>
		<xsl:when test="$name='desktop_new'">&#x1F5A5;&#xFE0F;</xsl:when>
		<xsl:when test="$name='gohome'">&#x1F3E0;</xsl:when>
		<xsl:when test="$name='folder'">&#x1F4C1;</xsl:when>
		<xsl:when test="$name='korn'">&#x1F4E5;</xsl:when>
		<xsl:when test="$name='Mail'">&#x1F4E7;</xsl:when>
		<xsl:when test="$name='kmail'">&#x2709;&#xFE0F;</xsl:when>
		<xsl:when test="$name='list'">&#x1F4CB;</xsl:when>
		<xsl:when test="$name='edit'">&#x270F;&#xFE0F;</xsl:when>
		<xsl:when test="$name='kaddressbook'">&#x1F4D6;</xsl:when>
		<xsl:when test="$name='knotify'">&#x1F514;</xsl:when>
		<xsl:when test="$name='password'">&#x1F511;</xsl:when>
		<xsl:when test="$name='pencil'">&#x270F;&#xFE0F;</xsl:when>
		<xsl:when test="$name='wizard'">&#x1F9D9;</xsl:when>
		<xsl:when test="$name='xmag'">&#x1F50D;</xsl:when>
		<xsl:when test="$name='bell'">&#x1F514;</xsl:when>
		<xsl:when test="$name='bookmark'">&#x1F516;</xsl:when>
		<xsl:when test="$name='penguin'">&#x1F427;</xsl:when>
		<xsl:when test="$name='freemind_butterfly'">&#x1F98B;</xsl:when>
		<xsl:when test="$name='broken-line'">&#x26A1;</xsl:when>
		<xsl:when test="$name='licq'">&#x1F4AC;</xsl:when>
		<!-- Time -->
		<xsl:when test="$name='calendar'">&#x1F4C5;</xsl:when>
		<xsl:when test="$name='clock'">&#x23F0;</xsl:when>
		<xsl:when test="$name='clock2'">&#x1F550;</xsl:when>
		<xsl:when test="$name='hourglass'">&#x231B;</xsl:when>
		<xsl:when test="$name='launch'">&#x1F680;</xsl:when>
		<!-- Flags -->
		<xsl:when test="$name='flag-black'">&#x1F3F4;</xsl:when>
		<xsl:when test="$name='flag-blue'">&#x1F7E6;</xsl:when>
		<xsl:when test="$name='flag-green'">&#x1F7E9;</xsl:when>
		<xsl:when test="$name='flag-orange'">&#x1F7E7;</xsl:when>
		<xsl:when test="$name='flag-pink'">&#x1F3F3;&#xFE0F;</xsl:when>
		<xsl:when test="$name='flag'">&#x1F3F3;&#xFE0F;</xsl:when>
		<xsl:when test="$name='flag-yellow'">&#x1F7E8;</xsl:when>
		<!-- People -->
		<xsl:when test="$name='family'">&#x1F468;&#x200D;&#x1F469;&#x200D;&#x1F467;&#x200D;&#x1F466;</xsl:when>
		<xsl:when test="$name='female1'">&#x1F469;</xsl:when>
		<xsl:when test="$name='female2'">&#x1F469;</xsl:when>
		<xsl:when test="$name='male1'">&#x1F468;</xsl:when>
		<xsl:when test="$name='male2'">&#x1F468;</xsl:when>
		<xsl:when test="$name='fema'">&#x1F464;</xsl:when>
		<xsl:when test="$name='group'">&#x1F465;</xsl:when>
		<!-- Security -->
		<xsl:when test="$name='encrypted'">&#x1F512;</xsl:when>
		<xsl:when test="$name='decrypted'">&#x1F513;</xsl:when>
		<!-- Other -->
		<xsl:when test="$name='redo'">&#x21A9;&#xFE0F;</xsl:when>
		<xsl:when test="$name='star'">&#x2B50;</xsl:when>
		<!-- Fallback: show icon name in brackets -->
		<xsl:otherwise>[<xsl:value-of select="$name"/>]</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- ### THE ROOT TEMPLATE ### -->

<xsl:template match="/">
<html>
<xsl:comment>This file has been created with freemind2html_standalone.xsl - a standalone HTML export</xsl:comment>
<head>
	<meta charset="UTF-8"/>
	<title><xsl:call-template name="output-title" /></title>
	<meta name="generator" content="FreeMind CE - Standalone HTML Export" />
	<!-- Inlined CSS -->
	<style type="text/css">
body {
  background-color: #FFFFFF;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  line-height: 1.5;
  color: #333;
  max-width: 960px;
  margin: 0 auto;
  padding: 1em;
}
div.node {
  padding-bottom: 1ex;
  padding-left: 2em;
}
div.cloud {
  padding-bottom: 1ex;
  padding-left: 2em;
  background-color: #C0C0FF;
  border-width: 2px;
  border-style: solid;
  border-color: #A0A0FF;
  border-radius: 8px;
}
div.content {
  border-width: 1px;
  border-style: dashed;
  border-color: #C0C0C0;
}
span.toggle {
  display: inline-block;
  width: 1.2em;
  text-align: center;
  cursor: pointer;
  color: #666;
  font-weight: bold;
  margin-right: 0.3em;
  user-select: none;
}
span.toggle:hover {
  color: #000;
  background: #e0e0e0;
  border-radius: 3px;
}
span.leaf {
  display: inline-block;
  width: 1.2em;
  text-align: center;
  margin-right: 0.3em;
  color: #999;
}
span.icon {
  font-size: 1em;
  margin-right: 0.2em;
}
.nodecontent { display: inline; }
.note-and-attributes {
  margin-left: 10%;
  color: #0000FF;
}
table.attributes {
  border-collapse: collapse;
  empty-cells: show;
  border: thin black solid;
}
table.attributes td, th {
  border: thin black solid;
  padding: 2px 3px;
}
table.attributes th {
  text-align: center;
}
table.attributes caption {
  margin-top: 1em;
  font-style: italic;
  text-align: center;
}
div.nodecontent > p {
  margin-top: 0pt;
  margin-bottom: 0pt;
  display: inline;
}
p + p {
  margin-top: 0.5em !important;
  display: block !important;
}
a { color: #0066cc; }
a:visited { color: #551a8b; }
	</style>
	<!-- Inlined JavaScript for folding -->
	<script type="text/javascript">
	<xsl:comment>
	<![CDATA[
function toggle(id) {
  var div_el = document.getElementById(id);
  var toggle_el = document.getElementById('toggle_' + id);
  if (div_el.style.display !== 'none') {
    div_el.style.display = 'none';
    toggle_el.textContent = '+';
  } else {
    div_el.style.display = 'block';
    toggle_el.textContent = '\u2212';
  }
}
function getVisibleParents(id) {
  var el = document.getElementById(id);
  while (el) {
    if (el.style && el.style.display === 'none') {
      el.style.display = 'block';
    }
    el = el.parentNode;
  }
}
	]]>
	</xsl:comment>
	</script>
</head>

<body>
	<h1><xsl:call-template name="output-title" /></h1>
	<xsl:apply-templates />
</body>

</html>
</xsl:template>

<!-- the template to output for each node -->
<xsl:template match="node">
<div>
	<xsl:variable name="contentID">
		<xsl:value-of select="generate-id()"/>
	</xsl:variable>
	<xsl:choose>
	<xsl:when test="cloud">
		<xsl:choose>
		<xsl:when test="cloud/@COLOR">
			<xsl:attribute name="class">cloud</xsl:attribute>
			<xsl:attribute name="style">background-color:<xsl:value-of select="cloud/@COLOR" /></xsl:attribute>
		</xsl:when>
		<xsl:otherwise>
			<xsl:attribute name="class">cloud</xsl:attribute>
		</xsl:otherwise>
		</xsl:choose>
	</xsl:when>
	<xsl:otherwise>
		<xsl:attribute name="class">node</xsl:attribute>
	</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
	<xsl:when test="node">
		<!-- Toggle button (Unicode minus sign) instead of hide.png -->
		<xsl:element name="span">
			<xsl:attribute name="class">toggle</xsl:attribute>
			<xsl:attribute name="id">toggle_<xsl:value-of select="$contentID" /></xsl:attribute>
			<xsl:attribute name="onclick"><![CDATA[toggle("]]><xsl:value-of select="$contentID" /><![CDATA[")]]></xsl:attribute>
			<xsl:text>&#x2212;</xsl:text>
		</xsl:element>
	</xsl:when>
	<xsl:otherwise>
		<!-- Leaf indicator (bullet) instead of leaf.png -->
		<span class="leaf">&#x2022;</span>
	</xsl:otherwise>
	</xsl:choose>
	<xsl:call-template name="output-icons" />
	<xsl:if test="@ID">
		<a>
			<xsl:attribute name="id">FM<xsl:value-of select="@ID"/>FM</xsl:attribute>
		</a>
	</xsl:if>
	<xsl:call-template name="output-node" />
	<xsl:if test="child::arrowlink">
		<xsl:call-template name="output-arrowlinks" />
	</xsl:if>
	<xsl:if test="richcontent[@TYPE='NOTE'] or attribute">
		<div class="note-and-attributes">
			<xsl:call-template name="output-note" />
			<xsl:call-template name="output-attributes" />
		</div>
	</xsl:if>
	<xsl:if test="node">
		<div class="content">
			<xsl:attribute name="id"><xsl:value-of select="$contentID" /></xsl:attribute>
			<xsl:apply-templates select="node[@POSITION='left']"/>
			<xsl:apply-templates select="node[@POSITION='right']"/>
			<xsl:apply-templates select="node[not(@POSITION)]"/>
		</div>
	</xsl:if>
</div>
</xsl:template>

<!-- ### XHTML LIBRARY ### -->

<xsl:template match="font">
	<xsl:if test="string-length(@SIZE) > 0">font-size:<xsl:value-of select="round((number(@SIZE) div 12)*100)" />%;</xsl:if><xsl:if test="@BOLD='true'">font-weight:bold;</xsl:if><xsl:if test="@ITALIC='true'">font-style:italic;</xsl:if>
</xsl:template>

<xsl:template name="output-node">
	<xsl:element name="div">
		<xsl:attribute name="class">nodecontent</xsl:attribute>
		<xsl:if test="@COLOR or @BACKGROUND_COLOR or font">
			<xsl:attribute name="style">
				<xsl:if test="@COLOR">color:<xsl:value-of select="@COLOR" />;</xsl:if>
				<xsl:if test="@BACKGROUND_COLOR">background-color:<xsl:value-of select="@BACKGROUND_COLOR" />;</xsl:if>
				<xsl:apply-templates select="font" />
			</xsl:attribute>
		</xsl:if>
		<xsl:choose>
		<xsl:when test="@LINK">
			<xsl:call-template name="output-node-with-link" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="output-nodecontent" />
		</xsl:otherwise>
		</xsl:choose>
		<!-- OpenStreetMap link -->
		<xsl:if test='hook/@NAME="plugins/map/MapNodePositionHolder.properties"'>
			<xsl:element name="a">
				<xsl:attribute name="href"><!--
				-->http://www.openstreetmap.org/?lat=<!--
				--><xsl:value-of select="hook/Parameters/@XML_STORAGE_MAP_LAT"/><!--
				-->&amp;lon=<!--
				--><xsl:value-of select="hook/Parameters/@XML_STORAGE_MAP_LON"/><!--
				-->&amp;mlat=<!--
				--><xsl:value-of select="hook/Parameters/@XML_STORAGE_POS_LAT"/><!--
				-->&amp;mlon=<!--
				--><xsl:value-of select="hook/Parameters/@XML_STORAGE_POS_LON"/><!--
				-->&amp;zoom=<!--
				--><xsl:value-of select="hook/Parameters/@XML_STORAGE_ZOOM"/></xsl:attribute>
				<xsl:text> &#x1F5FA;&#xFE0F;</xsl:text>
			</xsl:element>
		</xsl:if>
	</xsl:element>
</xsl:template>

<xsl:template name="output-node-with-link">
	<xsl:choose>
	<xsl:when test="not($show_link_url='true')">
		<xsl:variable name="link">
			<xsl:choose>
			<xsl:when test="starts-with(@LINK, '#')">#FM<xsl:value-of select="substring(@LINK,2)" />FM</xsl:when>
			<xsl:otherwise><xsl:value-of select="@LINK" /></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="a">
			<xsl:attribute name="href">
				<xsl:value-of select="$link" />
			</xsl:attribute>
			<xsl:call-template name="output-nodecontent" />
		</xsl:element>
		<xsl:if test="not($show_icons='false')">
			<xsl:text> </xsl:text>
			<xsl:element name="a">
				<xsl:attribute name="href">
					<xsl:value-of select="$link"/>
				</xsl:attribute>
				<xsl:text>&#x1F517;</xsl:text>
			</xsl:element>
		</xsl:if>
	</xsl:when>
	<xsl:otherwise>
		<xsl:call-template name="output-nodecontent" />
	</xsl:otherwise>
	</xsl:choose>
	<xsl:if test="$show_link_url='true'">
		- [ <a><xsl:attribute name="href"><xsl:value-of select="@LINK" />
		</xsl:attribute><xsl:value-of select="@LINK"/></a> ]
	</xsl:if>
</xsl:template>

<xsl:template name="output-nodecontent">
		<xsl:choose>
		<xsl:when test="richcontent[@TYPE='NODE']">
			<xsl:apply-templates select="richcontent[@TYPE='NODE']/html/body" mode="richcontent" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="textnode" />
		</xsl:otherwise>
		</xsl:choose>
</xsl:template>

<xsl:template match="body" mode="richcontent">
	<xsl:copy-of select="*|text()"/>
</xsl:template>

<xsl:template name="textnode">
	<xsl:call-template name="format_text">
		<xsl:with-param name="nodetext">
			<xsl:value-of select="@TEXT" />
		</xsl:with-param>
	</xsl:call-template>
</xsl:template>

<xsl:template name="output-title">
	<xsl:choose>
	<xsl:when test="/map/node/@TEXT">
		<xsl:value-of select="/map/node/@TEXT" />
	</xsl:when>
	<xsl:when test="/map/node/richcontent[@TYPE='NODE']">
		<xsl:apply-templates select="/map/node/richcontent[@TYPE='NODE']/html/body" mode="strip-tags" />
	</xsl:when>
	<xsl:otherwise>
		<xsl:text>FreeMind2HTML Mindmap</xsl:text>
	</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="text()|@*" mode="strip-tags">
	<xsl:value-of select="string(.)"/>
</xsl:template>

<xsl:template name="format_text">
	<xsl:param name="nodetext" />
	<xsl:if test="string-length($nodetext) = 0">
		<xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;]]></xsl:text>
	</xsl:if>
	<xsl:if test="string-length(substring-after($nodetext,'&#xa;')) = 0">
		<xsl:value-of select="$nodetext" />
	</xsl:if>
	<xsl:if test="string-length(substring-after($nodetext,'&#xa;')) > 0">
		<xsl:value-of select="substring-before($nodetext,'&#xa;')" />
		<br />
		<xsl:call-template name="format_text">
			<xsl:with-param name="nodetext">
				<xsl:value-of select="substring-after($nodetext,'&#xa;')" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:if>
</xsl:template>

<xsl:template name="output-note">
	<xsl:if test="richcontent[@TYPE='NOTE']">
		<span class="note">
			<xsl:apply-templates select="richcontent[@TYPE='NOTE']/html/body" mode="richcontent" />
		</span>
	</xsl:if>
</xsl:template>

<xsl:template name="output-attributes">
	<xsl:if test="attribute">
		<table class="attributes" summary="Attributes Names and Values">
			<caption>Attributes</caption>
			<tr><th>Name</th><th>Value</th></tr>
			<xsl:for-each select="attribute">
				<tr>
				<td><xsl:value-of select="@NAME" /></td>
				<td><xsl:value-of select="@VALUE" /></td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:if>
</xsl:template>

<xsl:template name="output-arrowlinks">
	<xsl:for-each select="arrowlink">
		<xsl:text> </xsl:text>
		<a>
			<xsl:attribute name="onclick">getVisibleParents('FM<xsl:value-of select="@DESTINATION" />FM')</xsl:attribute>
			<xsl:attribute name="href">#FM<xsl:value-of select="@DESTINATION" />FM</xsl:attribute>
			<xsl:text>&#x2192;</xsl:text>
		</a>
	</xsl:for-each>
</xsl:template>

<!-- Icons rendered as emojis -->
<xsl:template name="output-icons">
	<xsl:if test="not($show_icons='false')">
		<xsl:for-each select="icon">
			<span class="icon">
				<xsl:attribute name="title"><xsl:value-of select="@BUILTIN" /></xsl:attribute>
				<xsl:call-template name="icon-to-emoji">
					<xsl:with-param name="name" select="@BUILTIN"/>
				</xsl:call-template>
			</span>
			<xsl:text> </xsl:text>
		</xsl:for-each>
	</xsl:if>
</xsl:template>

</xsl:stylesheet>
