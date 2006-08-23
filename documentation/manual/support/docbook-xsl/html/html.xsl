<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='1.0'>

<!-- ********************************************************************
     $Id: html.xsl 6118 2006-07-28 21:20:20Z kosek $
     ********************************************************************

     This file is part of the XSL DocBook Stylesheet distribution.
     See ../README or http://nwalsh.com/docbook/xsl/ for copyright
     and other information.

     ******************************************************************** -->

<!-- The generate.html.title template is currently used for generating HTML -->
<!-- "title" attributes for some inline elements only, but not for any -->
<!-- block elements. It is called in eleven places in the inline.xsl -->
<!-- file. But it's called by all the inline.* templates (e.g., -->
<!-- inline.boldseq), which in turn are called by other (element) -->
<!-- templates, so it results, currently, in supporting generation of the -->
<!-- HTML "title" attribute for a total of about 92 elements. -->
<xsl:template name="generate.html.title">
  <xsl:if test="alt">
    <xsl:attribute name="title">
      <xsl:value-of select="normalize-space(alt)"/>
    </xsl:attribute>
  </xsl:if>
</xsl:template>

<xsl:template name="dir">
  <xsl:param name="inherit" select="0"/>

  <xsl:variable name="dir">
    <xsl:choose>
      <xsl:when test="@dir">
	<xsl:value-of select="@dir"/>
      </xsl:when>
      <xsl:when test="$inherit != 0">
	<xsl:value-of select="ancestor::*/@dir[1]"/>
      </xsl:when>
    </xsl:choose>
  </xsl:variable>

  <xsl:if test="$dir != ''">
    <xsl:attribute name="dir">
      <xsl:value-of select="$dir"/>
    </xsl:attribute>
  </xsl:if>
</xsl:template>

<xsl:template name="anchor">
  <xsl:param name="node" select="."/>
  <xsl:param name="conditional" select="1"/>
  <xsl:variable name="id">
    <xsl:call-template name="object.id">
      <xsl:with-param name="object" select="$node"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:if test="$conditional = 0 or $node/@id or $node/@xml:id">
    <a name="{$id}"/>
  </xsl:if>
</xsl:template>

<xsl:template name="href.target.uri">
  <xsl:param name="context" select="."/>
  <xsl:param name="object" select="."/>
  <xsl:text>#</xsl:text>
  <xsl:call-template name="object.id">
    <xsl:with-param name="object" select="$object"/>
  </xsl:call-template>
</xsl:template>

<xsl:template name="href.target">
  <xsl:param name="context" select="."/>
  <xsl:param name="object" select="."/>
  <xsl:text>#</xsl:text>
  <xsl:call-template name="object.id">
    <xsl:with-param name="object" select="$object"/>
  </xsl:call-template>
</xsl:template>

<xsl:template name="href.target.with.base.dir">
  <xsl:param name="context" select="."/>
  <xsl:param name="object" select="."/>
  <xsl:if test="$manifest.in.base.dir = 0">
    <xsl:value-of select="$base.dir"/>
  </xsl:if>
  <xsl:call-template name="href.target">
    <xsl:with-param name="context" select="$context"/>
    <xsl:with-param name="object" select="$object"/>
  </xsl:call-template>
</xsl:template>

<xsl:template name="dingbat">
  <xsl:param name="dingbat">bullet</xsl:param>
  <xsl:call-template name="dingbat.characters">
    <xsl:with-param name="dingbat" select="$dingbat"/>
  </xsl:call-template>
</xsl:template>

<xsl:template name="dingbat.characters">
  <!-- now that I'm using the real serializer, all that dingbat malarky -->
  <!-- isn't necessary anymore... -->
  <xsl:param name="dingbat">bullet</xsl:param>
  <xsl:choose>
    <xsl:when test="$dingbat='bullet'">&#x2022;</xsl:when>
    <xsl:when test="$dingbat='copyright'">&#x00A9;</xsl:when>
    <xsl:when test="$dingbat='trademark'">&#x2122;</xsl:when>
    <xsl:when test="$dingbat='trade'">&#x2122;</xsl:when>
    <xsl:when test="$dingbat='registered'">&#x00AE;</xsl:when>
    <xsl:when test="$dingbat='service'">(SM)</xsl:when>
    <xsl:when test="$dingbat='nbsp'">&#x00A0;</xsl:when>
    <xsl:when test="$dingbat='ldquo'">&#x201C;</xsl:when>
    <xsl:when test="$dingbat='rdquo'">&#x201D;</xsl:when>
    <xsl:when test="$dingbat='lsquo'">&#x2018;</xsl:when>
    <xsl:when test="$dingbat='rsquo'">&#x2019;</xsl:when>
    <xsl:when test="$dingbat='em-dash'">&#x2014;</xsl:when>
    <xsl:when test="$dingbat='mdash'">&#x2014;</xsl:when>
    <xsl:when test="$dingbat='en-dash'">&#x2013;</xsl:when>
    <xsl:when test="$dingbat='ndash'">&#x2013;</xsl:when>
    <xsl:otherwise>
      <xsl:text>&#x2022;</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="id.warning">
  <xsl:if test="$id.warnings != 0 and not(@id) and not(@xml:id) and parent::*">
    <xsl:variable name="title">
      <xsl:choose>
	<xsl:when test="title">
	  <xsl:value-of select="title[1]"/>
	</xsl:when>
	<xsl:when test="substring(local-name(*[1]),
			          string-length(local-name(*[1])-3) = 'info')
			and *[1]/title">
	  <xsl:value-of select="*[1]/title[1]"/>
	</xsl:when>
	<xsl:when test="refmeta/refentrytitle">
	  <xsl:value-of select="refmeta/refentrytitle"/>
	</xsl:when>
	<xsl:when test="refnamediv/refname">
	  <xsl:value-of select="refnamediv/refname[1]"/>
	</xsl:when>
      </xsl:choose>
    </xsl:variable>

    <xsl:message>
      <xsl:text>ID recommended on </xsl:text>
      <xsl:value-of select="local-name(.)"/>
      <xsl:if test="$title != ''">
	<xsl:text>: </xsl:text>
	<xsl:choose>
	  <xsl:when test="string-length($title) &gt; 40">
	    <xsl:value-of select="substring($title,1,40)"/>
	    <xsl:text>...</xsl:text>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:value-of select="$title"/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:if>
    </xsl:message>
  </xsl:if>
</xsl:template>

</xsl:stylesheet>

