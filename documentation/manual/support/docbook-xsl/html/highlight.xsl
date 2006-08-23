<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xslthl="http://xslthl.sf.net"
                exclude-result-prefixes="xslthl"
                version='1.0'>

<!-- ********************************************************************
     $Id: inline.xsl 5953 2006-05-08 04:23:10Z bobstayton $
     ********************************************************************

     This file is part of the XSL DocBook Stylesheet distribution.
     See ../README or http://nwalsh.com/docbook/xsl/ for copyright
     and other information.

     ******************************************************************** -->

<xsl:template match='xslthl:keyword'>
  <b class="hl-keyword"><xsl:apply-templates/></b>
</xsl:template>

<xsl:template match='xslthl:string'>
  <b class="hl-string"><i><font color='red'><xsl:apply-templates/></font></i></b>
</xsl:template>

<xsl:template match='xslthl:comment'>
  <i class="hl-comment"><font color='silver'><xsl:apply-templates/></font></i>
</xsl:template>

<xsl:template match='xslthl:tag'>
  <b class="hl-tag"><font color='blue'><xsl:apply-templates/></font></b>
</xsl:template>

<xsl:template match='xslthl:attribute'>
  <span class="hl-attribute"><font color='blue'><xsl:apply-templates/></font></span>
</xsl:template>

<xsl:template match='xslthl:value'>
  <span class="hl-value"><font color='blue'><xsl:apply-templates/></font></span>
</xsl:template>

<xsl:template match='xslthl:html'>
  <b><i><font color='red'><xsl:apply-templates/></font></i></b>
</xsl:template>

<xsl:template match='xslthl:xslt'>
  <b><font color='blue'><xsl:apply-templates/></font></b>
</xsl:template>

<xsl:template match='xslthl:section'>
  <b><xsl:apply-templates/></b>
</xsl:template>


</xsl:stylesheet>

