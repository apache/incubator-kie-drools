<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version='1.0'>

<!-- ********************************************************************
     $Id: pi.xsl,v 1.8 2005/03/08 08:36:29 bobstayton Exp $
     ********************************************************************

     This file is part of the XSL DocBook Stylesheet distribution.
     See ../README or http://nwalsh.com/docbook/xsl/ for copyright
     and other information.

     ******************************************************************** -->

<xsl:template match="processing-instruction()">
</xsl:template>

<!-- ==================================================================== -->

<xsl:template name="dbfo-attribute">
  <xsl:param name="pis" select="processing-instruction('dbfo')"/>
  <xsl:param name="attribute">filename</xsl:param>

  <xsl:call-template name="pi-attribute">
    <xsl:with-param name="pis" select="$pis"/>
    <xsl:with-param name="attribute" select="$attribute"/>
  </xsl:call-template>
</xsl:template>

<xsl:template name="dbfo-filename">
  <xsl:param name="pis" select="./processing-instruction('dbfo')"/>
  <xsl:call-template name="dbfo-attribute">
    <xsl:with-param name="pis" select="$pis"/>
    <xsl:with-param name="attribute">filename</xsl:with-param>
  </xsl:call-template>
</xsl:template>

<xsl:template name="dbfo-dir">
  <xsl:param name="pis" select="./processing-instruction('dbfo')"/>
  <xsl:call-template name="dbfo-attribute">
    <xsl:with-param name="pis" select="$pis"/>
    <xsl:with-param name="attribute">dir</xsl:with-param>
  </xsl:call-template>
</xsl:template>

<!-- ==================================================================== -->

<xsl:template name="process.cmdsynopsis.list">
  <xsl:param name="cmdsynopses"/><!-- empty node list by default -->
  <xsl:param name="count" select="1"/>

  <xsl:choose>
    <xsl:when test="$count>count($cmdsynopses)"></xsl:when>
    <xsl:otherwise>
      <xsl:variable name="cmdsyn" select="$cmdsynopses[$count]"/>

       <dt>
       <a>
         <xsl:attribute name="href">
           <xsl:call-template name="object.id">
             <xsl:with-param name="object" select="$cmdsyn"/>
           </xsl:call-template>
         </xsl:attribute>

         <xsl:choose>
           <xsl:when test="$cmdsyn/@xreflabel">
             <xsl:call-template name="xref.xreflabel">
               <xsl:with-param name="target" select="$cmdsyn"/>
             </xsl:call-template>
           </xsl:when>
           <xsl:otherwise>
             <xsl:apply-templates select="$cmdsyn" mode="xref-to">
               <xsl:with-param name="target" select="$cmdsyn"/>
             </xsl:apply-templates>
           </xsl:otherwise>
         </xsl:choose>
       </a>
       </dt>

        <xsl:call-template name="process.cmdsynopsis.list">
          <xsl:with-param name="cmdsynopses" select="$cmdsynopses"/>
          <xsl:with-param name="count" select="$count+1"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template match="processing-instruction('dbcmdlist')">
  <xsl:variable name="cmdsynopses" select="..//cmdsynopsis"/>

  <xsl:if test="count($cmdsynopses)&lt;1">
    <xsl:message><xsl:text>No cmdsynopsis elements matched dbcmdlist PI, perhaps it's nested too deep?</xsl:text>
    </xsl:message>
  </xsl:if>

  <dl>
    <xsl:call-template name="process.cmdsynopsis.list">
      <xsl:with-param name="cmdsynopses" select="$cmdsynopses"/>
    </xsl:call-template>
  </dl>
</xsl:template>

<!-- ==================================================================== -->

<xsl:template name="process.funcsynopsis.list">
  <xsl:param name="funcsynopses"/><!-- empty node list by default -->
  <xsl:param name="count" select="1"/>

  <xsl:choose>
    <xsl:when test="$count>count($funcsynopses)"></xsl:when>
    <xsl:otherwise>
      <xsl:variable name="cmdsyn" select="$funcsynopses[$count]"/>

       <dt>
       <a>
         <xsl:attribute name="href">
           <xsl:call-template name="object.id">
             <xsl:with-param name="object" select="$cmdsyn"/>
           </xsl:call-template>
         </xsl:attribute>

         <xsl:choose>
           <xsl:when test="$cmdsyn/@xreflabel">
             <xsl:call-template name="xref.xreflabel">
               <xsl:with-param name="target" select="$cmdsyn"/>
             </xsl:call-template>
           </xsl:when>
           <xsl:otherwise>
              <xsl:apply-templates select="$cmdsyn" mode="xref-to">
                <xsl:with-param name="target" select="$cmdsyn"/>
              </xsl:apply-templates>
           </xsl:otherwise>
         </xsl:choose>
       </a>
       </dt>

        <xsl:call-template name="process.funcsynopsis.list">
          <xsl:with-param name="funcsynopses" select="$funcsynopses"/>
          <xsl:with-param name="count" select="$count+1"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template match="processing-instruction('dbfunclist')">
  <xsl:variable name="funcsynopses" select="..//funcsynopsis"/>

  <xsl:if test="count($funcsynopses)&lt;1">
    <xsl:message><xsl:text>No funcsynopsis elements matched dbfunclist PI, perhaps it's nested too deep?</xsl:text>
    </xsl:message>
  </xsl:if>

  <dl>
    <xsl:call-template name="process.funcsynopsis.list">
      <xsl:with-param name="funcsynopses" select="$funcsynopses"/>
    </xsl:call-template>
  </dl>
</xsl:template>

<!-- ==================================================================== -->

<!-- "need" processing instruction, a kind of soft page break -->
<!-- A "need" is a request for space on a page.  If the requested space
     is not available, the page breaks and the content that follows
     the need request appears on the next page. If the requested
     space is available, then the request is ignored. -->

<xsl:template match="processing-instruction('dbfo-need')">

  <xsl:variable name="pi-height">
    <xsl:call-template name="dbfo-attribute">
      <xsl:with-param name="pis" select="."/>
      <xsl:with-param name="attribute" select="'height'"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="height">
    <xsl:choose>
      <xsl:when test="$pi-height != ''">
        <xsl:value-of select="$pi-height"/>
      </xsl:when>
      <xsl:otherwise>0pt</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="pi-before">
    <xsl:call-template name="dbfo-attribute">
      <xsl:with-param name="pis" select="."/>
      <xsl:with-param name="attribute" select="'space-before'"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="spacer">
    <fo:block-container width="100%" height="{$height}">
      <fo:block><fo:leader leader-length="0pt"/></fo:block>
    </fo:block-container>
  </xsl:variable>

  <xsl:choose>
    <xsl:when test="$fop.extensions != 0">
      <!-- Doesn't work in fop -->
    </xsl:when>
    <xsl:when test="$pi-before != '' and
                    not(following-sibling::listitem) and
                    not(following-sibling::step)">
      <fo:block space-after="0pt" space-before="{$pi-before}">
        <xsl:copy-of select="$spacer"/>
      </fo:block>
    </xsl:when>
    <xsl:when test="following-sibling::para">
      <fo:block space-after="0pt" 
                xsl:use-attribute-sets="normal.para.spacing">
        <xsl:copy-of select="$spacer"/>
      </fo:block>
    </xsl:when>
    <xsl:when test="following-sibling::table or
                    following-sibling::figure or
                    following-sibling::example or
                    following-sibling::equation">
      <fo:block space-after="0pt" 
                xsl:use-attribute-sets="formal.object.properties">
        <xsl:copy-of select="$spacer"/>
      </fo:block>
    </xsl:when>
    <xsl:when test="following-sibling::informaltable or
                    following-sibling::informalfigure or
                    following-sibling::informalexample or
                    following-sibling::informalequation">
      <fo:block space-after="0pt" 
                xsl:use-attribute-sets="informal.object.properties">
        <xsl:copy-of select="$spacer"/>
      </fo:block>
    </xsl:when>
    <xsl:when test="following-sibling::itemizedlist or
                    following-sibling::orderedlist or
                    following-sibling::variablelist or
                    following-sibling::simplelist">
      <fo:block space-after="0pt" 
                xsl:use-attribute-sets="informal.object.properties">
        <xsl:copy-of select="$spacer"/>
      </fo:block>
    </xsl:when>
    <xsl:when test="following-sibling::listitem or
                    following-sibling::step">
      <fo:list-item space-after="0pt" 
                xsl:use-attribute-sets="informal.object.properties">
        <fo:list-item-label/>
        <fo:list-item-body start-indent="0pt" end-indent="0pt">
          <xsl:copy-of select="$spacer"/>
        </fo:list-item-body>
      </fo:list-item>
    </xsl:when>
    <xsl:when test="following-sibling::sect1 or
                    following-sibling::sect2 or
                    following-sibling::sect3 or
                    following-sibling::sect4 or
                    following-sibling::sect5 or
                    following-sibling::section">
      <fo:block space-after="0pt" 
                xsl:use-attribute-sets="section.title.properties">
        <xsl:copy-of select="$spacer"/>
      </fo:block>
    </xsl:when>
    <xsl:otherwise>
      <fo:block space-after="0pt" space-before="0em">
        <xsl:copy-of select="$spacer"/>
      </fo:block>
    </xsl:otherwise>
  </xsl:choose>

  <xsl:choose>
    <xsl:when test="$fop.extensions != 0">
      <!-- Doesn't work in fop -->
    </xsl:when>
    <xsl:when test="following-sibling::listitem or
                    following-sibling::step">
      <fo:list-item space-before.precedence="force"
                space-before="-{$height}"
                space-after="0pt"
                space-after.precedence="force">
        <fo:list-item-label/>
        <fo:list-item-body start-indent="0pt" end-indent="0pt"/>
      </fo:list-item>
    </xsl:when>
    <xsl:otherwise>
      <fo:block space-before.precedence="force"
                space-before="-{$height}"
                space-after="0pt"
                space-after.precedence="force">
      </fo:block>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<!-- ==================================================================== -->

</xsl:stylesheet>
