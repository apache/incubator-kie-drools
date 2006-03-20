<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:doc="http://nwalsh.com/xsl/documentation/1.0"
                xmlns:date="http://exslt.org/dates-and-times"
                xmlns:exsl="http://exslt.org/common"
                exclude-result-prefixes="doc date exsl"
                extension-element-prefixes="date exsl"
                version='1.0'>

<!-- ********************************************************************
     $Id: pi.xsl,v 1.10 2005/09/09 03:58:58 xmldoc Exp $
     ********************************************************************

     This file is part of the XSL DocBook Stylesheet distribution.
     See ../README or http://nwalsh.com/docbook/xsl/ for copyright
     and other information.

     This file contains general templates for processing processing
     instructions common to both the HTML and FO versions of the
     DocBook stylesheets.
     ******************************************************************** -->

<!-- Process PIs also on title pages -->
<xsl:template match="processing-instruction()" mode="titlepage.mode">
  <xsl:apply-templates select="."/>
</xsl:template>

<xsl:template match="processing-instruction('dbtimestamp')">
  <xsl:variable name="format">
    <xsl:variable name="pi-format">
      <xsl:call-template name="pi-attribute">
        <xsl:with-param name="pis" select="."/>
        <xsl:with-param name="attribute">format</xsl:with-param>
      </xsl:call-template>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$pi-format != ''">
        <xsl:value-of select="$pi-format"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="gentext.template">
          <xsl:with-param name="context" select="'datetime'"/>
          <xsl:with-param name="name" select="'format'"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>  

  <xsl:variable name="padding">
    <xsl:variable name="pi-padding">
      <xsl:call-template name="pi-attribute">
        <xsl:with-param name="pis" select="."/>
        <xsl:with-param name="attribute">padding</xsl:with-param>
      </xsl:call-template>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$pi-padding != ''">
        <xsl:value-of select="$pi-padding"/>
      </xsl:when>
      <xsl:otherwise>1</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="date">
    <xsl:choose>
      <xsl:when test="function-available('date:date-time')">
        <xsl:value-of select="date:date-time()"/>
      </xsl:when>
      <xsl:when test="function-available('date:dateTime')">
        <!-- Xalan quirk -->
        <xsl:value-of select="date:dateTime()"/>
      </xsl:when>
    </xsl:choose>
  </xsl:variable>

  <xsl:choose>
    <xsl:when test="function-available('date:date-time') or
                    function-available('date:dateTime')">
      <xsl:call-template name="datetime.format">
        <xsl:with-param name="date" select="$date"/>
        <xsl:with-param name="format" select="$format"/>
        <xsl:with-param name="padding" select="$padding"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message>
        Timestamp processing requires XSLT processor with EXSLT date support.
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>

</xsl:template>

<xsl:template name="datetime.format">
  <xsl:param name="date"/>
  <xsl:param name="format"/>
  <xsl:param name="padding" select="1"/>
  
  <xsl:if test="$format != ''">
    <!-- replace any whitespace in the format string with a non-breaking space -->
    <xsl:variable name="format-nbsp"
		  select="translate($format,
			  '&#x20;&#x9;&#xd;&#xa;',
			  '&#xa0;&#xa0;&#xa0;&#xa0;')"/>
    <xsl:variable name="tokenized-format-string">
      <xsl:call-template name="str.tokenize.keep.delimiters">
	<xsl:with-param name="string" select="$format-nbsp"/>
	<xsl:with-param name="delimiters" select="'&#xa0;,./-()[]:'"/>
      </xsl:call-template>
    </xsl:variable>

  <xsl:choose>
    <xsl:when test="function-available('exsl:node-set')">
      <!-- We must preserve context node in order to get valid language -->
      <xsl:variable name="context" select="."/>
      <xsl:for-each select="exsl:node-set($tokenized-format-string)/node()">
        <xsl:variable name="token">
          <xsl:value-of select="."/>
        </xsl:variable>
        <!-- Restore context node -->
        <xsl:for-each select="$context">
          <xsl:choose>
            <xsl:when test="$token = 'a'">
              <xsl:call-template name="gentext.template">
                <xsl:with-param name="context" select="'datetime-abbrev'"/>
                <xsl:with-param name="name" select="date:day-abbreviation($date)"/>
              </xsl:call-template>
            </xsl:when>
            <xsl:when test="$token = 'A'">
              <xsl:call-template name="gentext.template">
                <xsl:with-param name="context" select="'datetime-full'"/>
                <xsl:with-param name="name" select="date:day-name($date)"/>
              </xsl:call-template>
            </xsl:when>
            <xsl:when test="$token = 'b'">
              <xsl:call-template name="gentext.template">
                <xsl:with-param name="context" select="'datetime-abbrev'"/>
                <xsl:with-param name="name" select="date:month-abbreviation($date)"/>
              </xsl:call-template>
            </xsl:when>
            <xsl:when test="$token = 'c'">
              <xsl:value-of select="date:date($date)"/>
              <xsl:text> </xsl:text>
              <xsl:value-of select="date:time($date)"/>
            </xsl:when>
            <xsl:when test="$token = 'B'">
              <xsl:call-template name="gentext.template">
                <xsl:with-param name="context" select="'datetime-full'"/>
                <xsl:with-param name="name" select="date:month-name($date)"/>
              </xsl:call-template>
            </xsl:when>
            <xsl:when test="$token = 'd'">
              <xsl:if test="$padding = 1 and
              string-length(date:day-in-month($date)) = 1">0</xsl:if>
              <xsl:value-of select="date:day-in-month($date)"/>
            </xsl:when>
            <xsl:when test="$token = 'H'">
              <xsl:if test="$padding = 1 and string-length(date:hour-in-day($date)) = 1">0</xsl:if>
              <xsl:value-of select="date:hour-in-day($date)"/>
            </xsl:when>
            <xsl:when test="$token = 'j'">
              <xsl:value-of select="date:day-in-year($date)"/>
            </xsl:when>
            <xsl:when test="$token = 'm'">
              <xsl:if test="$padding = 1 and string-length(date:month-in-year($date)) = 1">0</xsl:if>
              <xsl:value-of select="date:month-in-year($date)"/>
            </xsl:when>
            <xsl:when test="$token = 'M'">
              <xsl:if test="string-length(date:minute-in-hour($date)) = 1">0</xsl:if>
              <xsl:value-of select="date:minute-in-hour($date)"/>
            </xsl:when>
            <xsl:when test="$token = 'S'">
              <xsl:if test="string-length(date:second-in-minute($date)) = 1">0</xsl:if>
              <xsl:value-of select="date:second-in-minute($date)"/>
            </xsl:when>
            <xsl:when test="$token = 'U'">
              <xsl:value-of select="date:week-in-year($date)"/>
            </xsl:when>
            <xsl:when test="$token = 'w'">
              <xsl:value-of select="date:day-in-week($date)"/>
            </xsl:when>
            <xsl:when test="$token = 'x'">
              <xsl:value-of select="date:date($date)"/>
            </xsl:when>
            <xsl:when test="$token = 'X'">
              <xsl:value-of select="date:time($date)"/>
            </xsl:when>
            <xsl:when test="$token = 'Y'">
              <xsl:value-of select="date:year($date)"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$token"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message>
        Timestamp processing requires an XSLT processor with support
        for the EXSLT node-set() function.
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
  </xsl:if>

</xsl:template>

</xsl:stylesheet>
