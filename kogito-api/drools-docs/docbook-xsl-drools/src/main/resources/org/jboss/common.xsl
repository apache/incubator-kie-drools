<?xml version='1.0'?>

<!--
    Copyright 2008 JBoss, a division of Red Hat
    License: GPL
    Author: Pete Muir
    Author: Mark Newton (mark.newton@jboss.org)
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:d="http://docbook.org/ns/docbook"
                exclude-result-prefixes="d">


  <!-- We need to add this as it's needed later for a check -->
  <xsl:param name="confidential" select="0"/>
  
  <!--  Enable extensions (needed for callouts) -->
  <xsl:param name="use.extensions">1</xsl:param>
 
  <!-- For backwards compatibility we want to use callouts specified using programlistingco elements -->
  <xsl:param name="callouts.extension">1</xsl:param>
 
  <!-- Use graphical callouts as they look nicer with highlighed code. -->
  <xsl:param name="callout.graphics">1</xsl:param>
  <xsl:param name="callout.graphics.number.limit">15</xsl:param>
  <xsl:param name="callout.graphics.extension">.png</xsl:param>
   
  <xsl:param name="callout.graphics.path">
    <xsl:if test="$img.src.path != ''">
      <xsl:value-of select="$img.src.path"/>
    </xsl:if>
    <xsl:text>images/community/docbook/callouts/</xsl:text>
  </xsl:param>
      
  <!-- Admonitions -->
  <xsl:param name="admon.graphics" select="1"/>
  <xsl:param name="admon.graphics.path">
    <xsl:if test="$img.src.path != ''">
      <xsl:value-of select="$img.src.path"/>
    </xsl:if>
    <xsl:text>images/community/docbook/</xsl:text>
  </xsl:param>
  
  <!-- And disable these extensions -->
  <xsl:param name="tablecolumns.extension">0</xsl:param>
 
  <!-- TOC -->
  <xsl:param name="section.autolabel" select="1"/>

  <!-- Include the chapter no -->
  <xsl:param name="section.label.includes.component.label" select="1" />

  <xsl:param name="use.simplified.author.group" select="1"/>

   <xsl:template name="person.name.list">
      <xsl:param name="person.list" select="author|corpauthor|othercredit|editor" />
      <xsl:param name="person.count" select="count($person.list)" />
      <xsl:param name="person.type" select="'author'" />      
      <xsl:param name="count" select="1" />

      <xsl:choose>
      <xsl:when test="$use.simplified.author.group = 1">
      <xsl:choose>
         <!-- If there are no names in the list then don't do anything -->
         <xsl:when test="$count &gt; $person.count"></xsl:when>
         <xsl:otherwise>
         
            <!-- Depending on the type of people in the list print out different prefixes -->
            <xsl:choose>
             <xsl:when test="$count = 1 and $person.type = 'author' or $person.type = 'corpauthor'">
               <xsl:call-template name="gentext.by" />
               <xsl:call-template name="gentext.space" />
             </xsl:when>
             <xsl:when test="$count = 1 and $person.type = 'editor'">
               <xsl:call-template name="gentext.editors" />
               <xsl:call-template name="gentext.space" />
             </xsl:when>
             <xsl:when test="$count = 1 and $person.type = 'othercredit'">
               <xsl:call-template name="gentext.others" />
               <xsl:call-template name="gentext.space" />
             </xsl:when>
            </xsl:choose>
                        
            <!-- Output each person's name -->
            <xsl:call-template name="person.name">
               <xsl:with-param name="node"
                  select="$person.list[position()=$count]" />
            </xsl:call-template>

            <xsl:choose>
               <!-- Put parathenses around short affiliation descriptions -->
               <xsl:when
                  test="$person.list[position()=$count]/affiliation/shortaffil">
                  <xsl:call-template name="gentext.space" />
                  <xsl:text>(</xsl:text>
                  <xsl:value-of
                     select="$person.list[position()=$count]/affiliation/shortaffil" />
                  <xsl:text>)</xsl:text>
               </xsl:when>
            </xsl:choose>

            <xsl:choose>
               <!-- If only two names are present then insert 'and' between them -->
               <xsl:when test="$person.count = 2 and $count = 1">
                  <xsl:call-template name="gentext.template">
                     <xsl:with-param name="context" select="'authorgroup'" />
                     <xsl:with-param name="name" select="'sep2'" />
                  </xsl:call-template>
               </xsl:when>
               <!-- If we get to the last name insert 'and' before it -->
               <xsl:when
                  test="$person.count &gt; 2 and $count+1 = $person.count">
                  <xsl:call-template name="gentext.template">
                     <xsl:with-param name="context" select="'authorgroup'" />
                     <xsl:with-param name="name" select="'seplast'" />
                  </xsl:call-template>
               </xsl:when>
               <!-- If we are in the middle of a list insert a comma between names -->
               <xsl:when test="$count &lt; $person.count">
                  <xsl:call-template name="gentext.template">
                     <xsl:with-param name="context" select="'authorgroup'" />
                     <xsl:with-param name="name" select="'sep'" />
                  </xsl:call-template>
               </xsl:when>
            </xsl:choose>

            <!-- Recursively call the template to process all the names in the list -->
            <xsl:call-template name="person.name.list">
               <xsl:with-param name="person.list" select="$person.list" />
               <xsl:with-param name="person.count" select="$person.count" />
               <xsl:with-param name="count" select="$count+1" />
            </xsl:call-template>
         </xsl:otherwise>
      </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
         <xsl:apply-imports/>
      </xsl:otherwise>
      </xsl:choose>
   </xsl:template>

   <xsl:template name="gentext.editors">
      <xsl:text>edited by</xsl:text>
   </xsl:template>

   <xsl:template name="gentext.others">
      <xsl:text>and thanks to</xsl:text>
   </xsl:template>

<!-- Modify the default navigation wording -->
<xsl:param name="local.l10n.xml" select="document('')" />
<l:i18n xmlns:l="http://docbook.sourceforge.net/xmlns/l10n/1.0">
 <l:l10n language="en">
  <l:gentext key="nav-home" text="Front page"/>
 </l:l10n>
</l:i18n>

<l:i18n xmlns:l="http://docbook.sourceforge.net/xmlns/l10n/1.0">
 <l:l10n language="en">
  <l:gentext key="nav-up" text="Top of page"/>
 </l:l10n>
</l:i18n>

<!--
Copied from fo/params.xsl
-->
<xsl:param name="l10n.gentext.default.language" select="'en'"/>

<!-- This sets the filename based on the ID -->
<xsl:param name="use.id.as.filename" select="'1'"/>

<xsl:template match="command">
  <xsl:call-template name="inline.monoseq"/>
</xsl:template>

<xsl:template match="application">
  <xsl:call-template name="inline.boldseq"/>
</xsl:template>

<xsl:template match="guibutton">
  <xsl:call-template name="inline.boldseq"/>
</xsl:template>

<xsl:template match="guiicon">
  <xsl:call-template name="inline.boldseq"/>
</xsl:template>

<xsl:template match="guilabel">
  <xsl:call-template name="inline.boldseq"/>
</xsl:template>

<xsl:template match="guimenu">
  <xsl:call-template name="inline.boldseq"/>
</xsl:template>

<xsl:template match="guimenuitem">
  <xsl:call-template name="inline.boldseq"/>
</xsl:template>

<xsl:template match="guisubmenu">
  <xsl:call-template name="inline.boldseq"/>
</xsl:template>

<xsl:template match="filename">
  <xsl:call-template name="inline.monoseq"/>
</xsl:template>

<!--
WL: add <kw> for keywords: bold, monospaced
-->
<xsl:template match="d:kw">
  <xsl:call-template name="inline.boldmonoseq"/>
</xsl:template>

</xsl:stylesheet>
