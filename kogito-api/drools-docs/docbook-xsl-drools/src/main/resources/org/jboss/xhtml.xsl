<?xml version='1.0'?>

<!--
    Copyright 2008 JBoss, a division of Red Hat
    License: GPL
    Author: Jeff Fearn <jfearn@redhat.com>
    Author: Tammy Fox <tfox@redhat.com>
    Author: Andy Fitzsimon <afitzsim@redhat.com>
    Author: Mark Newton <mark.newton@jboss.org>
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  
  <xsl:import href="http://docbook.sourceforge.net/release/xsl-ns/1.74.0/xhtml/chunk.xsl"/>

  <xsl:include href="xhtml-common.xsl"/>

<!--
From: xhtml/chunk-common.xsl
Reason: remove tables, truncate link text
Version:
-->
<xsl:template name="header.navigation">
	<xsl:param name="prev" select="/foo"/>
	<xsl:param name="next" select="/foo"/>
	<xsl:param name="nav.context"/>
	<xsl:variable name="home" select="/*[1]"/>
	<xsl:variable name="up" select="parent::*"/>
	<xsl:variable name="row1" select="$navig.showtitles != 0"/>
	<xsl:variable name="row2" select="count($prev) &gt; 0 or (count($up) &gt; 0 and generate-id($up) != generate-id($home) and $navig.showtitles != 0) or count($next) &gt; 0"/>
	<xsl:if test="$suppress.navigation = '0' and $suppress.header.navigation = '0'">
		<xsl:if test="$row1 or $row2">
			<xsl:if test="$row1">
				<p xmlns="http://www.w3.org/1999/xhtml">
					<xsl:attribute name="id">
						<xsl:text>title</xsl:text>
					</xsl:attribute>
					<a>
						<xsl:attribute name="href">
							<xsl:value-of select="$siteHref" />
						</xsl:attribute>
						<xsl:attribute name="class">
							<xsl:text>site_href</xsl:text>
						</xsl:attribute>
						<strong>
						        <xsl:value-of select="$siteLinkText"/>	
						</strong>
					</a>
					<a>
						<xsl:attribute name="href">
							<xsl:value-of select="$docHref" />
						</xsl:attribute>
						<xsl:attribute name="class">
							<xsl:text>doc_href</xsl:text>
						</xsl:attribute>
						<strong>
						        <xsl:value-of select="$docLinkText"/>	
						</strong>
					</a>
				</p>
			</xsl:if>
			<xsl:if test="$row2">
				<ul class="docnav" xmlns="http://www.w3.org/1999/xhtml">
					<li class="previous">
						<xsl:if test="count($prev)&gt;0">
							<a accesskey="p">
								<xsl:attribute name="href">
									<xsl:call-template name="href.target">
										<xsl:with-param name="object" select="$prev"/>
									</xsl:call-template>
								</xsl:attribute>
								<strong>
									<xsl:call-template name="navig.content">
										<xsl:with-param name="direction" select="'prev'"/>
									</xsl:call-template>
								</strong>
							</a>
						</xsl:if>
					</li>
					<li class="next">
						<xsl:if test="count($next)&gt;0">
							<a accesskey="n">
								<xsl:attribute name="href">
									<xsl:call-template name="href.target">
										<xsl:with-param name="object" select="$next"/>
									</xsl:call-template>
								</xsl:attribute>
								<strong>
									<xsl:call-template name="navig.content">
										<xsl:with-param name="direction" select="'next'"/>
									</xsl:call-template>
								</strong>
							</a>
						</xsl:if>
					</li>
				</ul>
			</xsl:if>
		</xsl:if>
		<xsl:if test="$header.rule != 0">
			<hr/>
		</xsl:if>
	</xsl:if>
</xsl:template>

<!--
From: xhtml/chunk-common.xsl
Reason: remove tables, truncate link text
Version:
-->
<xsl:template name="footer.navigation">
	<xsl:param name="prev" select="/foo"/>
	<xsl:param name="next" select="/foo"/>
	<xsl:param name="nav.context"/>
	<xsl:param name="title-limit" select="'50'"/>
	<xsl:variable name="home" select="/*[1]"/>
	<xsl:variable name="up" select="parent::*"/>
	<xsl:variable name="row1" select="count($prev) &gt; 0 or count($up) &gt; 0 or count($next) &gt; 0"/>
	<xsl:variable name="row2" select="($prev and $navig.showtitles != 0) or (generate-id($home) != generate-id(.) or $nav.context = 'toc') or ($chunk.tocs.and.lots != 0 and $nav.context != 'toc') or ($next and $navig.showtitles != 0)"/>

	<xsl:if test="$suppress.navigation = '0' and $suppress.footer.navigation = '0'">
		<xsl:if test="$footer.rule != 0">
			<hr/>
		</xsl:if>
		<xsl:if test="$row1 or $row2">
			<ul class="docnav" xmlns="http://www.w3.org/1999/xhtml">
				<xsl:if test="$row1">
					<li class="previous">
						<xsl:if test="count($prev) &gt; 0">
							<a accesskey="p">
								<xsl:attribute name="href">
									<xsl:call-template name="href.target">
										<xsl:with-param name="object" select="$prev"/>
									</xsl:call-template>
								</xsl:attribute>
								<strong>
									<xsl:call-template name="navig.content">
										<xsl:with-param name="direction" select="'prev'"/>
									</xsl:call-template>
								</strong>
								<xsl:variable name="text">
									<xsl:apply-templates select="$prev" mode="object.title.markup"/>
								</xsl:variable>
								<xsl:choose>
									<xsl:when test="string-length($text) &gt; $title-limit">
										<xsl:value-of select="concat(substring($text, 0, $title-limit), '...')"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$text"/>
									</xsl:otherwise>
								</xsl:choose>
							</a>
						</xsl:if>
					</li>
					<xsl:if test="count($up) &gt; 0">
						<li class="up">
							<a accesskey="u">
								<xsl:attribute name="href">
									<xsl:text>#</xsl:text>
								</xsl:attribute>
								<strong>
									<xsl:call-template name="navig.content">
										<xsl:with-param name="direction" select="'up'"/>
									</xsl:call-template>
								</strong>
							</a>
						</li>
					</xsl:if>
					<xsl:if test="$home != . or $nav.context = 'toc'">
						<li class="home">
							<a accesskey="h">
								<xsl:attribute name="href">
									<xsl:call-template name="href.target">
										<xsl:with-param name="object" select="$home"/>
									</xsl:call-template>
								</xsl:attribute>
								<strong>
									<xsl:call-template name="navig.content">
										<xsl:with-param name="direction" select="'home'"/>
									</xsl:call-template>
								</strong>
							</a>
						</li>
					</xsl:if>
					<xsl:if test="count($next)&gt;0">
						<li class="next">
							<a accesskey="n">
								<xsl:attribute name="href">
									<xsl:call-template name="href.target">
										<xsl:with-param name="object" select="$next"/>
									</xsl:call-template>
								</xsl:attribute>
								<strong>
									<xsl:call-template name="navig.content">
										<xsl:with-param name="direction" select="'next'"/>
									</xsl:call-template>
								</strong>
								<xsl:variable name="text">
									<xsl:apply-templates select="$next" mode="object.title.markup"/>
								</xsl:variable>
								<xsl:choose>
									<xsl:when test="string-length($text) &gt; $title-limit">
										<xsl:value-of select="concat(substring($text, 0, $title-limit),'...')"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$text"/>
									</xsl:otherwise>
								</xsl:choose>
							</a>
						</li>
					</xsl:if>
				</xsl:if>
			</ul>
		</xsl:if>
	</xsl:if>
</xsl:template>

</xsl:stylesheet>
