<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exsl="http://exslt.org/common"
                xmlns:cf="http://docbook.sourceforge.net/xmlns/chunkfast/1.0"
		version="1.0"
                exclude-result-prefixes="cf exsl">

<!-- ********************************************************************
     $Id: chunkfast.xsl,v 1.4 2005/08/04 07:03:47 bobstayton Exp $
     ********************************************************************

     This file is part of the XSL DocBook Stylesheet distribution.
     See ../README or http://nwalsh.com/docbook/xsl/ for copyright
     and other information.

     ******************************************************************** -->

<!-- ==================================================================== -->

<xsl:import href="chunk.xsl"/>
<xsl:param name="chunk.fast" select="1"/>

<xsl:variable name="chunks" select="exsl:node-set($chunk.hierarchy)//cf:div"/>

<!-- ==================================================================== -->

<xsl:template name="process-chunk-element">
  <xsl:choose>
    <xsl:when test="$chunk.fast != 0 and function-available('exsl:node-set')">
      <xsl:variable name="genid" select="generate-id()"/>

      <xsl:variable name="div" select="$chunks[@id=$genid]"/>

      <xsl:variable name="prevdiv"
                    select="($div/preceding-sibling::cf:div|$div/preceding::cf:div|$div/parent::cf:div)[last()]"/>
      <xsl:variable name="prev" select="key('genid', $prevdiv/@id)"/>

      <xsl:variable name="nextdiv"
                    select="($div/following-sibling::cf:div|$div/following::cf:div|$div/cf:div)[1]"/>
      <xsl:variable name="next" select="key('genid', $nextdiv/@id)"/>

      <xsl:choose>
        <xsl:when test="$onechunk != 0 and parent::*">
          <xsl:apply-imports/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="process-chunk">
            <xsl:with-param name="prev" select="$prev"/>
            <xsl:with-param name="next" select="$next"/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
        <xsl:when test="$onechunk != 0 and not(parent::*)">
          <xsl:call-template name="chunk-all-sections"/>
        </xsl:when>
        <xsl:when test="$onechunk != 0">
          <xsl:apply-imports/>
        </xsl:when>
        <xsl:when test="$chunk.first.sections = 0">
          <xsl:call-template name="chunk-first-section-with-parent"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="chunk-all-sections"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
