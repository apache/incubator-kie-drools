<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:doc="http://nwalsh.com/xsl/documentation/1.0"
                exclude-result-prefixes="doc"
                version='1.0'>

<!-- ********************************************************************
     $Id: qandaset.xsl 6480 2007-01-07 23:35:19Z bobstayton $
     ********************************************************************

     This file is part of the XSL DocBook Stylesheet distribution.
     See ../README or http://nwalsh.com/docbook/xsl/ for copyright
     and other information.

     ******************************************************************** -->

<!-- ==================================================================== -->

<xsl:template match="qandaset">
  <xsl:variable name="title" select="(blockinfo/title|info/title|title)[1]"/>
  <xsl:variable name="preamble" select="*[local-name(.) != 'title'
                                          and local-name(.) != 'titleabbrev'
                                          and local-name(.) != 'qandadiv'
                                          and local-name(.) != 'qandaentry']"/>
  <xsl:variable name="toc">
    <xsl:call-template name="dbhtml-attribute">
      <xsl:with-param name="pis"
                      select="processing-instruction('dbhtml')"/>
      <xsl:with-param name="attribute" select="'toc'"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="toc.params">
    <xsl:call-template name="find.path.params">
      <xsl:with-param name="table" select="normalize-space($generate.toc)"/>
    </xsl:call-template>
  </xsl:variable>

  <div>
    <xsl:apply-templates select="." mode="class.attribute"/>
    <xsl:apply-templates select="$title"/>
    <xsl:if test="((contains($toc.params, 'toc') and $toc != '0') or $toc = '1')
                  and not(ancestor::answer and not($qanda.nested.in.toc=0))">
      <xsl:call-template name="process.qanda.toc"/>
    </xsl:if>
    <xsl:apply-templates select="$preamble"/>
    <xsl:call-template name="process.qandaset"/>
  </div>
</xsl:template>

<xsl:template match="qandaset/blockinfo/title|
                     qandaset/info/title|
                     qandaset/title">
  <xsl:variable name="qalevel">
    <xsl:call-template name="qanda.section.level"/>
  </xsl:variable>
  <xsl:element name="h{string(number($qalevel)+1)}">
    <xsl:attribute name="class">
      <xsl:value-of select="local-name(.)"/>
    </xsl:attribute>
    <xsl:call-template name="anchor">
      <xsl:with-param name="node" select=".."/>
      <xsl:with-param name="conditional" select="0"/>
    </xsl:call-template>
    <xsl:apply-templates/>
  </xsl:element>
</xsl:template>

<xsl:template match="qandaset/blockinfo|qandaset/info">
  <!-- what should this template really do? -->
  <xsl:apply-templates select="legalnotice" mode="titlepage.mode"/>
</xsl:template>

<xsl:template match="qandadiv">
  <xsl:variable name="preamble" select="*[local-name(.) != 'title'
                                          and local-name(.) != 'titleabbrev'
                                          and local-name(.) != 'qandadiv'
                                          and local-name(.) != 'qandaentry']"/>

  <xsl:if test="blockinfo/title|info/title|title">
    <tr class="qandadiv">
      <td align="left" valign="top" colspan="2">
        <xsl:apply-templates select="(blockinfo/title|info/title|title)[1]"/>
      </td>
    </tr>
  </xsl:if>

  <xsl:variable name="toc">
    <xsl:call-template name="dbhtml-attribute">
      <xsl:with-param name="pis"
                      select="processing-instruction('dbhtml')"/>
      <xsl:with-param name="attribute" select="'toc'"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="toc.params">
    <xsl:call-template name="find.path.params">
      <xsl:with-param name="table" select="normalize-space($generate.toc)"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:if test="(contains($toc.params, 'toc') and $toc != '0') or $toc = '1'">
    <tr class="toc">
      <td align="left" valign="top" colspan="2">
        <xsl:call-template name="process.qanda.toc"/>
      </td>
    </tr>
  </xsl:if>
  <xsl:if test="$preamble">
    <tr class="toc">
      <td align="left" valign="top" colspan="2">
        <xsl:apply-templates select="$preamble"/>
      </td>
    </tr>
  </xsl:if>
  <xsl:apply-templates select="qandadiv|qandaentry"/>
</xsl:template>

<xsl:template match="qandadiv/blockinfo/title|
                     qandadiv/info/title|
                     qandadiv/title">
  <xsl:variable name="qalevel">
    <xsl:call-template name="qandadiv.section.level"/>
  </xsl:variable>

  <xsl:element name="h{string(number($qalevel)+1)}">
    <xsl:attribute name="class">
      <xsl:value-of select="local-name(.)"/>
    </xsl:attribute>
    <xsl:call-template name="anchor">
      <xsl:with-param name="node" select=".."/>
      <xsl:with-param name="conditional" select="0"/>
    </xsl:call-template>
    <xsl:apply-templates select="parent::qandadiv" mode="label.markup"/>
    <xsl:if test="$qandadiv.autolabel != 0">
      <xsl:apply-templates select="." mode="intralabel.punctuation"/>
      <xsl:text> </xsl:text>
    </xsl:if>
    <xsl:apply-templates/>
  </xsl:element>
</xsl:template>

<xsl:template match="qandaentry">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="question">
  <xsl:variable name="deflabel">
    <xsl:choose>
      <xsl:when test="ancestor-or-self::*[@defaultlabel]">
        <xsl:value-of select="(ancestor-or-self::*[@defaultlabel])[last()]
                              /@defaultlabel"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$qanda.defaultlabel"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <tr>
    <xsl:apply-templates select="." mode="class.attribute"/>
    <td align="left" valign="top">
      <xsl:call-template name="anchor">
        <xsl:with-param name="node" select=".."/>
        <xsl:with-param name="conditional" select="0"/>
      </xsl:call-template>
      <xsl:call-template name="anchor">
        <xsl:with-param name="conditional" select="0"/>
      </xsl:call-template>

      <xsl:variable name="label.content">
        <xsl:apply-templates select="." mode="label.markup"/>
        <xsl:if test="$deflabel = 'number' and not(label)">
          <xsl:apply-templates select="." mode="intralabel.punctuation"/>
        </xsl:if>
      </xsl:variable>

      <xsl:if test="string-length($label.content) &gt; 0">
        <b>
          <xsl:copy-of select="$label.content"/>
        </b>
      </xsl:if>
    </td>
    <td align="left" valign="top">
      <xsl:choose>
        <xsl:when test="$deflabel = 'none' and not(label)">
          <b><xsl:apply-templates select="*[local-name(.) != 'label']"/></b>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="*[local-name(.) != 'label']"/>
        </xsl:otherwise>
      </xsl:choose>
    </td>
  </tr>
</xsl:template>

<xsl:template match="answer">
  <xsl:variable name="deflabel">
    <xsl:choose>
      <xsl:when test="ancestor-or-self::*[@defaultlabel]">
        <xsl:value-of select="(ancestor-or-self::*[@defaultlabel])[last()]
                              /@defaultlabel"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$qanda.defaultlabel"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <tr class="{local-name(.)}">
    <td align="left" valign="top">
      <xsl:call-template name="anchor"/>
      <xsl:variable name="answer.label">
        <xsl:apply-templates select="." mode="label.markup"/>
      </xsl:variable>
      <xsl:if test="string-length($answer.label) &gt; 0">
        <b>
          <xsl:copy-of select="$answer.label"/>
        </b>
      </xsl:if>
    </td>
    <td align="left" valign="top">
      <xsl:apply-templates select="*[local-name(.) != 'label'
        and local-name(.) != 'qandaentry']"/>
      <!-- * handle nested answer/qandaentry instances -->
      <!-- * (bug 1509043 from Daniel Leidert) -->
      <xsl:if test="descendant::question">
        <xsl:call-template name="process.qandaset"/>
      </xsl:if>
    </td>
  </tr>
</xsl:template>

<xsl:template match="label">
  <xsl:apply-templates/>
</xsl:template>

<!-- ==================================================================== -->

<xsl:template name="process.qanda.toc">
  <!-- * if user wants nested qandaset and qandaentry in main Qandaset TOC, -->
  <!-- * then don't also include the nested stuff in the sub TOCs -->
  <dl>
    <xsl:apply-templates select="qandadiv" mode="qandatoc.mode"/>
    <xsl:apply-templates select="qandaset|qandaentry" mode="qandatoc.mode"/>
  </dl>
</xsl:template>

<xsl:template match="qandadiv" mode="qandatoc.mode">
  <dt><xsl:apply-templates select="title" mode="qandatoc.mode"/></dt>
  <dd><xsl:call-template name="process.qanda.toc"/></dd>
</xsl:template>

<xsl:template match="qandadiv/blockinfo/title|
                     qandadiv/info/title|
                     qandadiv/title" mode="qandatoc.mode">
  <xsl:variable name="qalevel">
    <xsl:call-template name="qandadiv.section.level"/>
  </xsl:variable>
  <xsl:variable name="id">
    <xsl:call-template name="object.id">
      <xsl:with-param name="object" select="parent::*"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:apply-templates select="parent::qandadiv" mode="label.markup"/>
  <xsl:value-of select="$autotoc.label.separator"/>
  <xsl:text> </xsl:text>
  <a>
    <xsl:attribute name="href">
      <xsl:call-template name="href.target">
        <xsl:with-param name="object" select="parent::*"/>
      </xsl:call-template>
    </xsl:attribute>
    <xsl:apply-templates/>
  </a>
</xsl:template>

<xsl:template match="qandaset" mode="qandatoc.mode">
  <xsl:for-each select="qandaentry">
    <xsl:apply-templates select="." mode="qandatoc.mode"/>
  </xsl:for-each>
</xsl:template>

<xsl:template match="qandaentry" mode="qandatoc.mode">
  <xsl:apply-templates select="question" mode="qandatoc.mode"/>
</xsl:template>

<xsl:template match="question" mode="qandatoc.mode">
  <xsl:variable name="firstch">
    <xsl:apply-templates select="(*[local-name(.)!='label'])[1]"/>
  </xsl:variable>
  <xsl:variable name="deflabel">
    <xsl:choose>
      <xsl:when test="ancestor-or-self::*[@defaultlabel]">
        <xsl:value-of select="(ancestor-or-self::*[@defaultlabel])[last()]
                              /@defaultlabel"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$qanda.defaultlabel"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <dt>
    <xsl:apply-templates select="." mode="label.markup"/>
    <xsl:if test="$deflabel = 'number' and not(label)">
      <xsl:apply-templates select="." mode="intralabel.punctuation"/>
    </xsl:if>
    <xsl:text> </xsl:text>
    <a>
      <xsl:attribute name="href">
        <xsl:call-template name="href.target">
          <xsl:with-param name="object" select=".."/>
        </xsl:call-template>
      </xsl:attribute>
      <xsl:value-of select="$firstch"/>
    </a>
  </dt>
  <!-- * include nested qandaset/qandaentry in TOC if user wants it -->
  <xsl:if test="not($qanda.nested.in.toc = 0)">
    <xsl:apply-templates select="following-sibling::answer" mode="qandatoc.mode"/>
  </xsl:if>
</xsl:template>

<xsl:template match="answer" mode="qandatoc.mode">
  <xsl:if test="descendant::question">
    <dd>
      <xsl:call-template name="process.qanda.toc"/>
    </dd>
  </xsl:if>
</xsl:template>

<!-- ==================================================================== -->

<xsl:template name="process.qandaset">

  <xsl:variable name="label-width">
    <xsl:call-template name="dbhtml-attribute">
      <xsl:with-param name="pis"
        select="processing-instruction('dbhtml')"/>
      <xsl:with-param name="attribute" select="'label-width'"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="table-summary">
    <xsl:call-template name="dbhtml-attribute">
      <xsl:with-param name="pis"
        select="processing-instruction('dbhtml')"/>
      <xsl:with-param name="attribute" select="'table-summary'"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="cellpadding">
    <xsl:call-template name="dbhtml-attribute">
      <xsl:with-param name="pis"
        select="processing-instruction('dbhtml')"/>
      <xsl:with-param name="attribute" select="'cellpadding'"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="cellspacing">
    <xsl:call-template name="dbhtml-attribute">
      <xsl:with-param name="pis"
        select="processing-instruction('dbhtml')"/>
      <xsl:with-param name="attribute" select="'cellspacing'"/>
    </xsl:call-template>
  </xsl:variable>

  <table border="0" summary="Q and A Set">
    <xsl:if test="$table-summary != ''">
      <xsl:attribute name="summary">
        <xsl:value-of select="$table-summary"/>
      </xsl:attribute>
    </xsl:if>

    <xsl:if test="$cellpadding != ''">
      <xsl:attribute name="cellpadding">
        <xsl:value-of select="$cellpadding"/>
      </xsl:attribute>
    </xsl:if>

    <xsl:if test="$cellspacing != ''">
      <xsl:attribute name="cellspacing">
        <xsl:value-of select="$cellspacing"/>
      </xsl:attribute>
    </xsl:if>

    <col align="left">
      <xsl:attribute name="width">
        <xsl:choose>
          <xsl:when test="$label-width != ''">
            <xsl:value-of select="$label-width"/>
          </xsl:when>
          <xsl:otherwise>1%</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
    </col>
    <tbody>
      <xsl:apply-templates select="qandaentry|qandadiv"/>
    </tbody>
  </table>
</xsl:template>

<!-- ==================================================================== -->

<xsl:template match="*" mode="no.wrapper.mode">
  <xsl:apply-templates/>
</xsl:template>

<!-- ==================================================================== -->

</xsl:stylesheet>
