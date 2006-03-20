<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="1.0">

<!-- ********************************************************************
     $Id: htmltbl.xsl,v 1.7 2005/04/07 21:24:52 bobstayton Exp $
     ********************************************************************

     This file is part of the XSL DocBook Stylesheet distribution.
     See ../README or http://nwalsh.com/docbook/xsl/ for copyright
     and other information.

     ******************************************************************** -->

<!-- ==================================================================== -->

<xsl:attribute-set name="th.style">
  <xsl:attribute name="font-weight">bold</xsl:attribute>
</xsl:attribute-set>

<xsl:template match="table|informaltable" mode="htmlTable">
  <xsl:if test="tgroup/tbody/row
                |tgroup/thead/row
                |tgroup/tfoot/row">
    <xsl:message terminate="yes">Broken table: row descendent of HTML table.</xsl:message>
  </xsl:if>

  <xsl:variable name="id">
    <xsl:call-template name="object.id"/>
  </xsl:variable>

  <xsl:variable name="numcols">
    <xsl:call-template name="widest-html-row">
      <xsl:with-param name="rows" select=".//tr"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="footnotes">
    <xsl:if test=".//footnote">
      <fo:block font-family="{$body.fontset}"
                font-size="{$footnote.font.size}"
                keep-with-previous.within-column="always">
        <xsl:apply-templates select=".//footnote" mode="table.footnote.mode"/>
      </fo:block>
    </xsl:if>
  </xsl:variable>

  <xsl:choose>
    <xsl:when test="caption">
      <fo:table-and-caption id="{$id}" 
                            xsl:use-attribute-sets="table.properties">
        <xsl:apply-templates select="caption" mode="htmlTable"/>
        <fo:table xsl:use-attribute-sets="table.table.properties">
          <xsl:choose>
            <xsl:when test="$fop.extensions != 0 or
                            $passivetex.extensions != 0">
              <xsl:attribute name="table-layout">fixed</xsl:attribute>
            </xsl:when>
          </xsl:choose>
          <xsl:attribute name="width">
            <xsl:choose>
              <xsl:when test="@width">
                <xsl:value-of select="@width"/>
              </xsl:when>
              <xsl:otherwise>100%</xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
          <xsl:call-template name="make-html-table-columns">
            <xsl:with-param name="count" select="$numcols"/>
          </xsl:call-template>
          <xsl:apply-templates select="thead" mode="htmlTable"/>
          <xsl:apply-templates select="tfoot" mode="htmlTable"/>
          <xsl:choose>
            <xsl:when test="tbody">
              <xsl:apply-templates select="tbody" mode="htmlTable"/>
            </xsl:when>
            <xsl:otherwise>
              <fo:table-body>
                <xsl:apply-templates select="tr" mode="htmlTable"/>
              </fo:table-body>
            </xsl:otherwise>
          </xsl:choose>
        </fo:table>
      </fo:table-and-caption>
      <xsl:copy-of select="$footnotes"/>
    </xsl:when>
    <xsl:otherwise>
      <fo:block id="{$id}"
                xsl:use-attribute-sets="informaltable.properties">
        <fo:table table-layout="fixed"
                  xsl:use-attribute-sets="table.table.properties">
          <xsl:attribute name="width">
            <xsl:choose>
              <xsl:when test="@width">
                <xsl:value-of select="@width"/>
              </xsl:when>
              <xsl:otherwise>100%</xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
          <xsl:call-template name="make-html-table-columns">
            <xsl:with-param name="count" select="$numcols"/>
          </xsl:call-template>
          <xsl:apply-templates mode="htmlTable"/>
        </fo:table>
      </fo:block>
      <xsl:copy-of select="$footnotes"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="caption" mode="htmlTable">
  <fo:table-caption>
    <fo:block>
      <xsl:apply-templates select=".." mode="object.title.markup">
        <xsl:with-param name="allow-anchors" select="1"/>
      </xsl:apply-templates>
    </fo:block>
  </fo:table-caption>
</xsl:template>

<xsl:template name="widest-html-row">
  <xsl:param name="rows" select="''"/>
  <xsl:param name="count" select="0"/>
  <xsl:choose>
    <xsl:when test="count($rows) = 0">
      <xsl:value-of select="$count"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
        <xsl:when test="$count &gt; count($rows[1]/*)">
          <xsl:call-template name="widest-html-row">
            <xsl:with-param name="rows" select="$rows[position() &gt; 1]"/>
            <xsl:with-param name="count" select="$count"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="widest-html-row">
            <xsl:with-param name="rows" select="$rows[position() &gt; 1]"/>
            <xsl:with-param name="count" select="count($rows[1]/*)"/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="make-html-table-columns">
  <xsl:param name="count" select="0"/>
  <xsl:param name="number" select="1"/>

  <xsl:choose>
    <xsl:when test="col|colgroup/col">
      <xsl:for-each select="col|colgroup/col">
        <fo:table-column>
          <xsl:attribute name="column-number">
            <xsl:number from="table" level="any" format="1"/>
          </xsl:attribute>
          <xsl:if test="@width">
            <xsl:attribute name="column-width">
              <xsl:value-of select="@width"/>
            </xsl:attribute>
          </xsl:if>
        </fo:table-column>
      </xsl:for-each>
    </xsl:when>
    <xsl:when test="$fop.extensions != 0">
      <xsl:if test="$number &lt;= $count">
        <fo:table-column column-number="{$number}"
                         column-width="{6.5 div $count}in"/>
        <xsl:call-template name="make-html-table-columns">
          <xsl:with-param name="count" select="$count"/>
          <xsl:with-param name="number" select="$number + 1"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<xsl:template match="tbody" mode="htmlTable">
  <fo:table-body border-bottom-width="0.25pt"
                 border-bottom-style="solid"
                 border-bottom-color="black">
    <xsl:apply-templates mode="htmlTable"/>
  </fo:table-body>
</xsl:template>

<xsl:template match="td" mode="htmlTable">
  <xsl:variable name="bgcolor">
    <xsl:call-template name="dbfo-attribute">
      <xsl:with-param name="pis" select="processing-instruction('dbfo')"/>
      <xsl:with-param name="attribute" select="'bgcolor'"/>
    </xsl:call-template>
  </xsl:variable>
  <fo:table-cell xsl:use-attribute-sets="table.cell.padding">
    <xsl:call-template name="table.cell.properties">
      <xsl:with-param name="bgcolor.pi" select="$bgcolor"/>
    </xsl:call-template>
    <fo:block>
      <xsl:call-template name="table.cell.block.properties"/>
      <xsl:apply-templates/>
    </fo:block>
  </fo:table-cell>
</xsl:template>

<xsl:template match="tfoot" mode="htmlTable">
  <fo:table-footer>
    <xsl:apply-templates mode="htmlTable"/>
  </fo:table-footer>
</xsl:template>

<xsl:template match="th" mode="htmlTable">
  <xsl:variable name="bgcolor">
    <xsl:call-template name="dbfo-attribute">
      <xsl:with-param name="pis" select="processing-instruction('dbfo')"/>
      <xsl:with-param name="attribute" select="'bgcolor'"/>
    </xsl:call-template>
  </xsl:variable>

  <fo:table-cell xsl:use-attribute-sets="th.style table.cell.padding">
    <xsl:call-template name="table.cell.properties">
      <xsl:with-param name="bgcolor.pi" select="$bgcolor"/>
    </xsl:call-template>
    <fo:block>
      <xsl:call-template name="table.cell.block.properties"/>
      <xsl:apply-templates/>
    </fo:block>
  </fo:table-cell>
</xsl:template>

<xsl:template match="thead" mode="htmlTable">
  <fo:table-header border-bottom-width="0.25pt"
                   border-bottom-style="solid"
                   border-bottom-color="black"
                   font-weight="bold">
    <xsl:apply-templates mode="htmlTable"/>
  </fo:table-header>
</xsl:template>

<xsl:template match="tr" mode="htmlTable">
  <xsl:variable name="bgcolor">
    <xsl:call-template name="dbfo-attribute">
      <xsl:with-param name="pis" select="processing-instruction('dbfo')"/>
      <xsl:with-param name="attribute" select="'bgcolor'"/>
    </xsl:call-template>
  </xsl:variable>

  <fo:table-row>
    <xsl:if test="$bgcolor != ''">
      <xsl:attribute name="background-color">
        <xsl:value-of select="$bgcolor"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:apply-templates mode="htmlTable"/>
  </fo:table-row>
</xsl:template>

</xsl:stylesheet>
