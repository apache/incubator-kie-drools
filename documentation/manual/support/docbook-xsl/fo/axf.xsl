<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:axf="http://www.antennahouse.com/names/XSL/Extensions"
                version='1.0'>

<!-- ********************************************************************
     $Id: axf.xsl,v 1.2 2005/11/20 19:31:14 bobstayton Exp $
     ******************************************************************** -->

<xsl:template name="axf-document-information">

    <xsl:variable name="authors" select="(//author|//editor|
                                          //corpauthor|//authorgroup)[1]"/>
    <xsl:if test="$authors">
      <xsl:variable name="author">
        <xsl:choose>
          <xsl:when test="$authors[self::authorgroup]">
            <xsl:call-template name="person.name.list">
              <xsl:with-param name="person.list" 
                 select="$authors/*[self::author|self::corpauthor|
                               self::othercredit|self::editor]"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="$authors[self::corpauthor]">
            <xsl:value-of select="$authors"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="person.name">
              <xsl:with-param name="node" select="$authors"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <xsl:element name="axf:document-info">
        <xsl:attribute name="name">author</xsl:attribute>
        <xsl:attribute name="value">
          <xsl:value-of select="normalize-space($author)"/>
        </xsl:attribute>
      </xsl:element>
    </xsl:if>

    <xsl:variable name="title">
      <xsl:apply-templates select="/*[1]" mode="label.markup"/>
      <xsl:apply-templates select="/*[1]" mode="title.markup"/>
    </xsl:variable>

    <axf:document-info>
      <xsl:attribute name="title">
        <xsl:value-of select="normalize-space($title)"/>
      </xsl:attribute>
    </axf:document-info>

    <xsl:if test="//keyword">
      <xsl:element name="axf:document-info">
        <xsl:attribute name="name">keywords</xsl:attribute>
        <xsl:attribute name="value">
          <xsl:for-each select="//keyword">
            <xsl:value-of select="normalize-space(.)"/>
            <xsl:if test="position() != last()">
              <xsl:text>, </xsl:text>
            </xsl:if>
          </xsl:for-each>
        </xsl:attribute>
      </xsl:element>
    </xsl:if>

    <xsl:if test="//subjectterm">
      <xsl:element name="axf:document-info">
        <xsl:attribute name="name">subject</xsl:attribute>
        <xsl:attribute name="value">
          <xsl:for-each select="//subjectterm">
            <xsl:value-of select="normalize-space(.)"/>
            <xsl:if test="position() != last()">
              <xsl:text>, </xsl:text>
            </xsl:if>
          </xsl:for-each>
        </xsl:attribute>
      </xsl:element>
    </xsl:if>

</xsl:template>

</xsl:stylesheet>
