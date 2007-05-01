<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="1.0">

<!-- ********************************************************************
     $Id: htmltbl.xsl 6676 2007-03-08 07:04:57Z bobstayton $
     ********************************************************************

     This file is part of the XSL DocBook Stylesheet distribution.
     See ../README or http://nwalsh.com/docbook/xsl/ for copyright
     and other information.

     ******************************************************************** -->

<!-- ==================================================================== -->

<xsl:attribute-set name="th.style">
  <xsl:attribute name="font-weight">bold</xsl:attribute>
</xsl:attribute-set>

<!-- Outputs an fo:table only, not the caption -->
<xsl:template match="table|informaltable" mode="htmlTable">

  <xsl:variable name="numcols">
    <xsl:call-template name="widest-html-row">
      <xsl:with-param name="rows" select=".//tr"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="prop-columns"
                select=".//col[contains(@width, '%')] |
                        .//colgroup[contains(@width, '%')]"/>

  <xsl:variable name="table.width">
    <xsl:call-template name="table.width"/>
  </xsl:variable>

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
        <xsl:when test="$table.width">
          <xsl:value-of select="$table.width"/>
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
        <fo:table-body start-indent="0pt" end-indent="0pt">
          <xsl:apply-templates select="tr" mode="htmlTable"/>
        </fo:table-body>
      </xsl:otherwise>
    </xsl:choose>
  </fo:table>

</xsl:template>

<xsl:template match="caption" mode="htmlTable">
  <!-- Handled by formal.object.heading -->
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
            <xsl:number from="table|informaltable" level="any" format="1"/>
          </xsl:attribute>
          <xsl:if test="@width">
            <xsl:attribute name="column-width">
              <xsl:choose>
                <xsl:when test="$fop.extensions != 0 and 
                                contains(@width, '%')">
                  <xsl:value-of select="concat('proportional-column-width(',
                                               substring-before(@width, '%'),
                                               ')')"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="@width"/>
                </xsl:otherwise>
              </xsl:choose>
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
                 border-bottom-color="black"
                 start-indent="0pt"
                 end-indent="0pt">
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
      <xsl:with-param name="rowsep.inherit" select="0"/>
      <xsl:with-param name="colsep.inherit" select="0"/>
    </xsl:call-template>
    <fo:block>
      <xsl:call-template name="table.cell.block.properties"/>
      <xsl:apply-templates/>
    </fo:block>
  </fo:table-cell>
</xsl:template>

<xsl:template match="tfoot" mode="htmlTable">
  <fo:table-footer start-indent="0pt"
                   end-indent="0pt">
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
      <xsl:with-param name="rowsep.inherit" select="0"/>
      <xsl:with-param name="colsep.inherit" select="0"/>
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
                   start-indent="0pt"
                   end-indent="0pt"
                   font-weight="bold">
    <xsl:apply-templates mode="htmlTable"/>
  </fo:table-header>
</xsl:template>

<xsl:template match="tr" mode="htmlTable">
  <fo:table-row>
    <xsl:call-template name="table.row.properties"/>
    <xsl:apply-templates mode="htmlTable"/>
  </fo:table-row>
</xsl:template>

</xsl:stylesheet>
