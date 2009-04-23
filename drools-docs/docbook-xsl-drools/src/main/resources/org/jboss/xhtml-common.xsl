<?xml version='1.0'?>

<!--
    Copyright 2007 Red Hat, Inc.
    License: GPL
    Author: Jeff Fearn <jfearn@redhat.com>
    Author: Tammy Fox <tfox@redhat.com>
    Author: Andy Fitzsimon <afitzsim@redhat.com>
    Author: Mark Newton <mark.newton@jboss.org>
    Author: Pete Muir    
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				version="1.0"
                xmlns:rf="java:org.jboss.highlight.XhtmlRendererFactory"
                exclude-result-prefixes="#default">

   <xsl:import href="common.xsl" />
  
   <!-- Do not ignore image scaling in html version -->
   <xsl:param name="ignore.image.scaling" select="0"/>
   <xsl:param name="graphicsize.extension" select="0"></xsl:param>

   <!-- WL: failed experiments - postponed
   <xsl:param name="use.extensions" select="1"></xsl:param>
   <xsl:param name="graphicsize.use.img.src.path" select="1"></xsl:param>
   <xsl:param name="keep.relative.image.uris" select="1"></xsl:param>
   -->

  <xsl:param name="generate.legalnotice.link" select="1"/>
  <xsl:param name="generate.revhistory.link" select="0"/>
  
  <!-- This is needed to generate the correct xhtml-strict DOCTYPE on the front page.
      We can't use indentation as the algorithm inserts linebreaks into the markup
      created for callouts. This means that callouts appear on different lines than
      the code they are supposed to refer to. -->
  <xsl:output method="xml"
              encoding="UTF-8"
              indent="no"
              doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
              doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
              standalone="no"/>

  <xsl:param name="siteHref" select="'http://www.jboss.org'"/>
  <xsl:param name="docHref" select="'http://docs.jboss.org/'"/>
  <xsl:param name="siteLinkText" select="'JBoss.org'"/>
  <xsl:param name="docLinkText" select="'Community Documentation'"/>

   
   <!-- Placement of titles -->
   <xsl:param name="formal.title.placement">
      figure after example before equation before table before procedure before
   </xsl:param>
   
   <!-- Callouts -->
   <!-- Place callout marks at this column in annotated areas. The algorithm using this number doesn't
        know about highlighted code with extra span elements so we need to pad each line at the start
        with an XML comment and a line break. The callout marks must then be placed immediately afterwards.
        This ensures that the callouts appear on the same line as the code it relates to and we can position
        them using CSS so that they all appear in a column on the right. -->
   <xsl:param name="callout.defaultcolumn">15</xsl:param>
   <xsl:param name="callout.icon.size">17px</xsl:param>
      
  <!-- Admonitions -->
  <xsl:param name="admon.style" select="''"/>

  <!-- Set chunk.section.depth to 0 to just chunk chapters. -->
  <xsl:param name="chunk.section.depth" select="0"/>
  <xsl:param name="chunk.first.sections" select="1"/>
  <xsl:param name="chunk.toc" select="''"/>
  <xsl:param name="chunker.output.doctype-public" select="'-//W3C//DTD XHTML 1.0 Strict//EN'"/>
  <xsl:param name="chunker.output.doctype-system" select="'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd'"/>
  <xsl:param name="chunker.output.encoding" select="'UTF-8'"/>

  <!-- We must turn off indenting as the algorithm inserts linebreaks into the callout markup that is added by the
      code highlighting routine. This causes the callouts to appear on different lines from the code they relate to. -->
  <xsl:param name="chunker.output.indent" select="'no'"/>

  <xsl:param name="html.stylesheet" select="'css/jbossorg.css'"/>
  <xsl:param name="html.stylesheet.type" select="'text/css'"/>
  <xsl:param name="html.cleanup" select="1"/>
  <xsl:param name="html.ext" select="'.html'"/>


  <xsl:template match="authorgroup" mode="titlepage.mode">
   <xsl:choose>
   <xsl:when test="$use.simplified.author.group = 1">
      <div class="authorgroup">
         <div class="authors">
            <xsl:call-template name="person.name.list">
               <xsl:with-param name="person.list" select="./author|./corpauthor" />
               <xsl:with-param name="person.type" select="'author'"/>
            </xsl:call-template>
         </div>

         <div class="editors">
            <xsl:call-template name="person.name.list">
               <xsl:with-param name="person.list" select="./editor" />
               <xsl:with-param name="person.type" select="'editor'"/>
            </xsl:call-template>
         </div>

         <div class="others">
            <xsl:call-template name="person.name.list">
               <xsl:with-param name="person.list" select="./othercredit" />
               <xsl:with-param name="person.type" select="'othercredit'"/>
            </xsl:call-template>
         </div>
      </div>
      </xsl:when>
      <xsl:otherwise>
         <xsl:apply-imports/>
      </xsl:otherwise>
      </xsl:choose>
      
   </xsl:template>     

<!-- 
From: fo/callout.xsl
Version: 1.73.2
Reason: This includes the callout.icon.size attribute for SVGs
        (We can probably get rid of this if we upgrade to DocBook Stylesheets 1.73.2)
 -->
 <xsl:template name="callout-bug">
  <xsl:param name="conum" select="1"/>

  <xsl:choose>
    <xsl:when test="$callout.graphics != 0                     and $conum &lt;= $callout.graphics.number.limit">
      <img src="{$callout.graphics.path}{$conum}{$callout.graphics.extension}" alt="{$conum}" border="0" height="{$callout.icon.size}" width="{$callout.icon.size}"/>
    </xsl:when>
    <xsl:when test="$callout.unicode != 0                     and $conum &lt;= $callout.unicode.number.limit">
      <xsl:choose>
        <xsl:when test="$callout.unicode.start.character = 10102">
          <xsl:choose>
            <xsl:when test="$conum = 1">&#10102;</xsl:when>
            <xsl:when test="$conum = 2">&#10103;</xsl:when>
            <xsl:when test="$conum = 3">&#10104;</xsl:when>
            <xsl:when test="$conum = 4">&#10105;</xsl:when>
            <xsl:when test="$conum = 5">&#10106;</xsl:when>
            <xsl:when test="$conum = 6">&#10107;</xsl:when>
            <xsl:when test="$conum = 7">&#10108;</xsl:when>
            <xsl:when test="$conum = 8">&#10109;</xsl:when>
            <xsl:when test="$conum = 9">&#10110;</xsl:when>
            <xsl:when test="$conum = 10">&#10111;</xsl:when>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <xsl:message>
            <xsl:text>Don't know how to generate Unicode callouts </xsl:text>
            <xsl:text>when $callout.unicode.start.character is </xsl:text>
            <xsl:value-of select="$callout.unicode.start.character"/>
          </xsl:message>
          <xsl:text>(</xsl:text>
          <xsl:value-of select="$conum"/>
          <xsl:text>)</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>(</xsl:text>
      <xsl:value-of select="$conum"/>
      <xsl:text>)</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- TOC -->
<xsl:param name="generate.toc">
set toc
book toc
article toc
chapter toc
qandadiv toc
qandaset toc
sect1 nop
sect2 nop
sect3 nop
sect4 nop
sect5 nop
section toc
part toc
</xsl:param>

<xsl:param name="suppress.navigation" select="0"/>
<xsl:param name="suppress.header.navigation" select="0"/>
<xsl:param name="suppress.footer.navigation" select="0"/>

<xsl:param name="header.rule" select="0"/>
<xsl:param name="footer.rule" select="0"/>
<xsl:param name="css.decoration" select="0"/>
<xsl:param name="ulink.target"/>
<xsl:param name="table.cell.border.style"/>

<!-- BUGBUG TODO 

	There is a bug where inserting elements in to the body level
	of xhtml will add xmlns="" to the tag. This is invalid xhtml.
	To overcome this I added:
		xmlns="http://www.w3.org/1999/xhtml"
	to the outer most tag. This gets stripped by the parser, resulting
	in valid xhtml ... go figure.
  
    This sounds like the system used by the stylesheets to process
    DocBook 5 docs by stripping out the XML namespace before processing
    the node set as normal: http://lists.oasis-open.org/archives/docbook-apps/200701/msg00184.html
-->

<!--
From: xhtml/admon.xsl
Reason: remove tables
Version: 1.72.0
-->
<xsl:template name="graphical.admonition">
	<xsl:variable name="admon.type">
		<xsl:choose>
			<xsl:when test="local-name(.)='note'">Note</xsl:when>
			<xsl:when test="local-name(.)='warning'">Warning</xsl:when>
			<xsl:when test="local-name(.)='caution'">Caution</xsl:when>
			<xsl:when test="local-name(.)='tip'">Tip</xsl:when>
			<xsl:when test="local-name(.)='important'">Important</xsl:when>
			<xsl:otherwise>Note</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:variable name="alt">
		<xsl:call-template name="gentext">
			<xsl:with-param name="key" select="$admon.type"/>
		</xsl:call-template>
	</xsl:variable>

	<div xmlns="http://www.w3.org/1999/xhtml">
	 	 <xsl:apply-templates select="." mode="class.attribute"/>
		<xsl:if test="$admon.style != ''">
			<xsl:attribute name="style">
				<xsl:value-of select="$admon.style"/>
			</xsl:attribute>
		</xsl:if>

		<xsl:call-template name="anchor"/>
			<xsl:if test="$admon.textlabel != 0 or title">
				<h2>
					<xsl:apply-templates select="." mode="object.title.markup"/>
				</h2>
			</xsl:if>
		<xsl:apply-templates/>
	</div>
</xsl:template>

<!--
From: xhtml/lists.xsl
Reason: Remove invalid type attribute from ol
Version: 1.72.0
-->
<xsl:template match="substeps">
	<xsl:variable name="numeration">
		<xsl:call-template name="procedure.step.numeration"/>
	</xsl:variable>
	<xsl:call-template name="anchor"/>
	<ol xmlns="http://www.w3.org/1999/xhtml" class="{$numeration}">
		<xsl:apply-templates/>
	</ol>
</xsl:template>

<!--
From: xhtml/lists.xsl
Reason: Remove invalid type, start & compact attributes from ol
Version: 1.72.0
-->
<xsl:template match="orderedlist">
	<div xmlns="http://www.w3.org/1999/xhtml">
		<xsl:apply-templates select="." mode="class.attribute"/>
		<xsl:call-template name="anchor"/>
		<xsl:if test="title">
			<xsl:call-template name="formal.object.heading"/>
		</xsl:if>
<!-- Preserve order of PIs and comments -->
		<xsl:apply-templates select="*[not(self::listitem or self::title or self::titleabbrev)]	|comment()[not(preceding-sibling::listitem)]	|processing-instruction()[not(preceding-sibling::listitem)]"/>
		<ol>
			<xsl:apply-templates select="listitem |comment()[preceding-sibling::listitem] |processing-instruction()[preceding-sibling::listitem]"/>
		</ol>
	</div>
</xsl:template>

<!--
From: xhtml/lists.xsl
Reason: Remove invalid type, start & compact attributes from ol
Version: 1.72.0
-->
<xsl:template match="procedure">
	<xsl:variable name="param.placement" select="substring-after(normalize-space($formal.title.placement), concat(local-name(.), ' '))"/>

	<xsl:variable name="placement">
		<xsl:choose>
			<xsl:when test="contains($param.placement, ' ')">
				<xsl:value-of select="substring-before($param.placement, ' ')"/>
			</xsl:when>
			<xsl:when test="$param.placement = ''">before</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$param.placement"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

<!-- Preserve order of PIs and comments -->
	<xsl:variable name="preamble" select="*[not(self::step or self::title or self::titleabbrev)] |comment()[not(preceding-sibling::step)]	|processing-instruction()[not(preceding-sibling::step)]"/>
	<div xmlns="http://www.w3.org/1999/xhtml">
		<xsl:apply-templates select="." mode="class.attribute"/>
		<xsl:call-template name="anchor">
			<xsl:with-param name="conditional">
				<xsl:choose>
					<xsl:when test="title">0</xsl:when>
					<xsl:otherwise>1</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:call-template>
		<xsl:if test="title and $placement = 'before'">
			<xsl:call-template name="formal.object.heading"/>
		</xsl:if>
		<xsl:apply-templates select="$preamble"/>
		<xsl:choose>
			<xsl:when test="count(step) = 1">
				<ul>
					<xsl:apply-templates select="step |comment()[preceding-sibling::step] |processing-instruction()[preceding-sibling::step]"/>
				</ul>
			</xsl:when>
			<xsl:otherwise>
				<ol>
					<xsl:attribute name="class">
						<xsl:value-of select="substring($procedure.step.numeration.formats,1,1)"/>
					</xsl:attribute>
					<xsl:apply-templates select="step |comment()[preceding-sibling::step] |processing-instruction()[preceding-sibling::step]"/>
				</ol>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="title and $placement != 'before'">
			<xsl:call-template name="formal.object.heading"/>
		</xsl:if>
	</div>
</xsl:template>

<!--
From: xhtml/graphics.xsl
Reason:  Remove html markup (align)
Version: 1.72.0
-->
<xsl:template name="longdesc.link">
	<xsl:param name="longdesc.uri" select="''"/>

	<xsl:variable name="this.uri">
	<xsl:call-template name="make-relative-filename">
		<xsl:with-param name="base.dir" select="$base.dir"/>
			<xsl:with-param name="base.name">
				<xsl:call-template name="href.target.uri"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="href.to">
		<xsl:call-template name="trim.common.uri.paths">
			<xsl:with-param name="uriA" select="$longdesc.uri"/>
			<xsl:with-param name="uriB" select="$this.uri"/>
			<xsl:with-param name="return" select="'A'"/>
		</xsl:call-template>
	</xsl:variable>
	<div xmlns="http://www.w3.org/1999/xhtml" class="longdesc-link">
		<br/>
		<span class="longdesc-link">
			<xsl:text>[</xsl:text>
			<a href="{$href.to}">D</a>
			<xsl:text>]</xsl:text>
		</span>
	</div>
</xsl:template>

<!--
From: xhtml/docbook.xsl
Reason: Remove inline style for draft mode
Version: 1.72.0
-->
<xsl:template name="head.content">
	<xsl:param name="node" select="."/>
	<xsl:param name="title">
		<xsl:apply-templates select="$node" mode="object.title.markup.textonly"/>
	</xsl:param>

	<title xmlns="http://www.w3.org/1999/xhtml" >
		<xsl:copy-of select="$title"/>
	</title>

	<xsl:if test="$html.stylesheet != ''">
		<xsl:call-template name="output.html.stylesheets">
			<xsl:with-param name="stylesheets" select="normalize-space($html.stylesheet)"/>
		</xsl:call-template>
	</xsl:if>

	<xsl:if test="$link.mailto.url != ''">
		<link rev="made" href="{$link.mailto.url}"/>
	</xsl:if>

	<xsl:if test="$html.base != ''">
		<base href="{$html.base}"/>
	</xsl:if>

	<meta xmlns="http://www.w3.org/1999/xhtml" name="generator" content="DocBook {$DistroTitle} V{$VERSION}"/>

	<xsl:if test="$generate.meta.abstract != 0">
		<xsl:variable name="info" select="(articleinfo |bookinfo |prefaceinfo |chapterinfo |appendixinfo |sectioninfo |sect1info |sect2info |sect3info |sect4info |sect5info |referenceinfo |refentryinfo |partinfo |info |docinfo)[1]"/>
		<xsl:if test="$info and $info/abstract">
			<meta xmlns="http://www.w3.org/1999/xhtml" name="description">
				<xsl:attribute name="content">
					<xsl:for-each select="$info/abstract[1]/*">
						<xsl:value-of select="normalize-space(.)"/>
						<xsl:if test="position() &lt; last()">
							<xsl:text> </xsl:text>
						</xsl:if>
					</xsl:for-each>
				</xsl:attribute>
			</meta>
		</xsl:if>
	</xsl:if>

	<xsl:apply-templates select="." mode="head.keywords.content"/>
</xsl:template>

<!--
From: xhtml/docbook.xsl
Reason: Add css class for draft mode
Version: 1.72.0
-->
<xsl:template name="body.attributes">
	<xsl:if test="($draft.mode = 'yes' or ($draft.mode = 'maybe' and ancestor-or-self::*[@status][1]/@status = 'draft'))">
		<xsl:attribute name="class">
			<xsl:value-of select="ancestor-or-self::*[@status][1]/@status"/>
		</xsl:attribute>
	</xsl:if>
</xsl:template>

<!--
From: xhtml/docbook.xsl
Reason: Add confidential to footer
Version: 1.72.0
-->
<xsl:template name="user.footer.content">
	<xsl:param name="node" select="."/>
	<xsl:if test="$confidential = '1'">
		<h1 xmlns="http://www.w3.org/1999/xhtml" class="confidential">
			<xsl:text>Red Hat Confidential!</xsl:text>
		</h1>
	</xsl:if>
</xsl:template>

<!--
From: xhtml/block.xsl
Reason:  default class (otherwise) to formalpara
Version: 1.72.0
-->
<xsl:template match="formalpara">
	<xsl:call-template name="paragraph">
		<xsl:with-param name="class">
			<xsl:choose>
				<xsl:when test="@role and $para.propagates.style != 0">
					<xsl:value-of select="@role"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>formalpara</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:with-param>
		<xsl:with-param name="content">
			<xsl:call-template name="anchor"/>
			<xsl:apply-templates/>
		</xsl:with-param>
	</xsl:call-template>
</xsl:template>

<!--
From: xhtml/block.xsl
Reason:  h5 instead of <b>, remove default title end punctuation
Version: 1.72.0
-->
<xsl:template match="formalpara/title|formalpara/info/title">
	<xsl:variable name="titleStr">
			<xsl:apply-templates/>
	</xsl:variable>
	<h5 xmlns="http://www.w3.org/1999/xhtml" class="formalpara">
		<xsl:copy-of select="$titleStr"/>
	</h5>
</xsl:template>

<!--
From: xhtml/footnote.xsl
Reason: remove inline css from hr
Version: 1.72.0
-->
<xsl:template name="process.footnotes">
  <xsl:variable name="footnotes" select=".//footnote"/>
  <xsl:variable name="table.footnotes" select=".//tgroup//footnote"/>

  <!-- Only bother to do this if there's at least one non-table footnote -->
  <xsl:if test="count($footnotes)&gt;count($table.footnotes)">
    <div class="footnotes">
      <br/>
      <hr/>
      <xsl:apply-templates select="$footnotes" mode="process.footnote.mode"/>
    </div>
  </xsl:if>

  <xsl:if test="$annotation.support != 0 and //annotation">
    <div class="annotation-list">
      <div class="annotation-nocss">
  <p>The following annotations are from this essay. You are seeing
  them here because your browser doesn&#8217;t support the user-interface
  techniques used to make them appear as &#8216;popups&#8217; on modern browsers.</p>
      </div>

      <xsl:apply-templates select="//annotation" mode="annotation-popup"/>
    </div>
  </xsl:if>
</xsl:template>

  <xsl:template match="programlisting[@role='XML']|programlisting[@role='JAVA']|programlisting[@role='XHTML']|programlisting[@role='JSP']|programlisting[@role='CSS']">
    
    <xsl:variable name="role">
      <xsl:value-of select="s:toUpperCase(string(@role))" xmlns:s="java:java.lang.String"/>
    </xsl:variable>
    
    <xsl:variable name="factory" select="rf:instance()"/>
    <xsl:variable name="hiliter" select="rf:getRenderer($factory, string($role))"/>

    <pre class="{$role}">
    <xsl:choose>
      <xsl:when test="$hiliter">
            <xsl:for-each select="node()">
              <xsl:choose>
                <xsl:when test="self::text()">
                  <xsl:variable name="child.content" select="."/>
          
                  <xsl:value-of select="jhr:highlight($hiliter, $role, string($child.content), 'UTF-8', true())"
            xmlns:jhr="com.uwyn.jhighlight.renderer.Renderer" disable-output-escaping="yes"/>
          </xsl:when>
                <xsl:otherwise>
                  <!-- Support a single linkend in HTML -->
                  <xsl:variable name="targets" select="key('id', @linkends)"/>
                  <xsl:variable name="target" select="$targets[1]"/>
                  <xsl:choose>
                  <xsl:when test="$target">
                  <a>
                    <xsl:if test="@id or @xml:id">
                      <xsl:attribute name="id">
                        <xsl:value-of select="(@id|@xml:id)[1]"/>
                      </xsl:attribute>
                    </xsl:if>
                    <xsl:attribute name="href">
                      <xsl:call-template name="href.target">
                        <xsl:with-param name="object" select="$target"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:apply-templates select="." mode="callout-bug"/>
                  </a>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:call-template name="anchor"/>
                    <xsl:apply-templates select="." mode="callout-bug"/>
                  </xsl:otherwise>
                  </xsl:choose>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:for-each>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates/>
          </xsl:otherwise>
        </xsl:choose>
      </pre>
    
  </xsl:template>

</xsl:stylesheet>
