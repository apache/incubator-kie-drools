<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:doc="http://nwalsh.com/xsl/documentation/1.0"
                xmlns:date="http://exslt.org/dates-and-times"
                exclude-result-prefixes="doc date"
                version='1.0'>

<!-- ********************************************************************
     $Id: refentry.xsl,v 1.4 2005/07/08 08:41:01 xmldoc Exp $
     ********************************************************************

     This file is part of the XSL DocBook Stylesheet distribution.
     See ../README or http://docbook.sf.net/release/xsl/current/ for
     copyright and other information.

     ******************************************************************** -->

<!-- ==================================================================== -->

<doc:reference xmlns="">
  <referenceinfo>
    <releaseinfo role="meta">
      $Id: refentry.xsl,v 1.4 2005/07/08 08:41:01 xmldoc Exp $
    </releaseinfo>
    <author><orgname>The DocBook Project</orgname></author>
    <copyright><year>2005</year>
    <holder>The DocBook Project</holder>
    </copyright>
  </referenceinfo>
  <title>Refentry Metadata-Gathering Template Reference</title>

  <partintro>
    <section><title>Introduction</title>

    <para>This is technical reference documentation for the "refentry
    metadata gathering" templates in the DocBook XSL Stylesheets.</para>

    <para>This is not intended to be user documentation. It is provided
    for developers writing customization layers for the
    stylesheets.</para>

    <note>
      <para>Currently, only the manpages stylesheets make use of these
      templates. They are, however, potentially useful elsewhere.</para>
    </note>

    </section>
  </partintro>

</doc:reference>

<!-- ==================================================================== -->

<doc:template name="get.refentry.metadata" xmlns="">
  <refpurpose>Gathers metadata from a refentry and its parent</refpurpose>

  <refdescription>
    <para>Reference documentation for particular commands, functions,
    etc., is sometimes viewed in isolation from its greater "context". For
    example, users view Unix man pages as, well, individual pages, not as
    part of a "book" of some kind. Therefore, it is sometimes necessary to
    embed "context" information in output for each <tag>refentry</tag>.</para>

    <para>However, one problem is that mark up that context information in
    different ways. Often (usually), it is not actually part fo the
    content of the <tag>refentry</tag> itself, but instead part of its
    parent element's content. And even then, DocBook provides a variety of
    elements that users might potentially use to mark up the same kind of
    information. One user might use the <tag>productnumber</tag> element
    to mark up version information about a particular product, while
    another might use the <tag>releaseinfo</tag> element.</para>

    <para>Taking all that in mind, the
    <function>get.refentry.info</function> function tries to gather data
    from a <tag>refentry</tag> element and its parent element in an
    intelligent and user-configurable way.</para>

    <note>
      <para>The <function>get.refentry.info</function> is actually just
      sort of a "driver" function; it calls other function that do that
      actual data collection, the returns the data as a set.</para>
    </note>

    <para>The manpages stylesheets are an application of these APIs.</para>
  </refdescription>

  <refparameter>
    <variablelist>
      <varlistentry>
        <term>refname</term>
        <listitem>
          <para>The first <tag>refname</tag> in the refentry</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>info</term>
        <listitem>
          <para>An info node (from a <tag>refentry</tag> element)</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>parentinfo</term>
        <listitem>
          <para>An info node (from a parent of a <tag>refentry</tag>
          element)</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>prefs</term>
        <listitem>
          <para>A node containing users preferences (from global
          stylesheet parameters)</para>
        </listitem>
      </varlistentry>
    </variablelist>
  </refparameter>

  <refreturn>
    <para>Returns a node set with the following elements. The
    descriptions are verbatim from the <literal>man(7)</literal> man
    page.
    <variablelist>
      <varlistentry>
        <term>title</term>
        <listitem>
          <para>the title of the man page (e.g., <literal>MAN</literal>)</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>section</term>
        <listitem>
          <para>the section number the man page should be placed in (e.g.,
          <literal>7</literal>)</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>date</term>
        <listitem>
          <para>the date of the last revision</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>source</term>
        <listitem>
          <para>the source of the command</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>manual</term>
        <listitem>
          <para>the title of the manual (e.g., <citetitle>Linux
          Programmer's Manual</citetitle>)</para>
        </listitem>
      </varlistentry>
    </variablelist>
    </para>
  </refreturn>
</doc:template>

<xsl:template name="get.refentry.metadata">
  <xsl:param name="refname"/>
  <xsl:param name="info"/>
  <xsl:param name="parentinfo"/>
  <xsl:param name="prefs"/>
  <title>
    <xsl:call-template name="get.refentry.title">
      <xsl:with-param name="refname" select="$refname"/>
    </xsl:call-template>
  </title>
  <section>
    <xsl:call-template name="get.refentry.section"/>
  </section>
  <date>
    <xsl:call-template name="get.refentry.date">
      <xsl:with-param name="info" select="$info"/>
      <xsl:with-param name="parentinfo" select="$parentinfo"/>
      <xsl:with-param name="prefs" select="$prefs/DatePrefs"/>
    </xsl:call-template>
  </date>
  <source>
    <xsl:call-template name="get.refentry.source">
      <xsl:with-param name="info" select="$info"/>
      <xsl:with-param name="parentinfo" select="$parentinfo"/>
      <xsl:with-param name="prefs" select="$prefs/SourcePrefs"/>
    </xsl:call-template>
  </source>
  <manual>
    <xsl:call-template name="get.refentry.manual">
      <xsl:with-param name="info" select="$info"/>
      <xsl:with-param name="parentinfo" select="$parentinfo"/>
      <xsl:with-param name="prefs" select="$prefs/ManualPrefs"/>
    </xsl:call-template>
  </manual>
</xsl:template>

<!-- ====================================================================== -->

<doc:template name="get.refentry.title" xmlns="">
  <refpurpose>Gets title metadata for a refentry</refpurpose>

  <refdescription>
    <para>The <literal>man(7)</literal> man page describes this as "the
    title of the man page (e.g., <literal>MAN</literal>). This differs
    from <tag>refname</tag> in that, if the <tag>refentry</tag> has a
    <tag>refentrytitle</tag>, we use that as the <tag>title</tag>;
    otherwise, we just use first <tag>refname</tag> in the first
    <tag>refnamediv</tag> in the source.</para>
  </refdescription>

  <refparameter>
    <variablelist>
      <varlistentry>
        <term>refname</term>
        <listitem>
          <para>The first <tag>refname</tag> in the refentry</para>
        </listitem>
      </varlistentry>
    </variablelist>
  </refparameter>

  <refreturn>
  <para>Returns a <tag>title</tag> node.</para></refreturn>
</doc:template>

<xsl:template name="get.refentry.title">
  <xsl:param name="refname"/>
  <xsl:choose>
    <xsl:when test="refmeta/refentrytitle">
      <xsl:copy>
        <xsl:apply-templates select="refmeta/refentrytitle/node()"/>
      </xsl:copy>
    </xsl:when>
    <xsl:otherwise>
      <xsl:copy-of select="$refname"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ==================================================================== -->

<doc:template name="get.refentry.section" xmlns="">
  <refpurpose>Gets section metadata for a refentry</refpurpose>

  <refdescription>
    <para>The <literal>man(7)</literal> man page describes this as "the
    section number the man page should be placed in (e.g.,
    <literal>7</literal>)". If we do not find a <tag>manvolnum</tag>
    specified in the source, and we find that the <tag>refentry</tag> is
    for a function, we use the section number <literal>3</literal>
    ["Library calls (functions within program libraries)"]; otherwise, we
    default to using <literal>1</literal> ["Executable programs or shell
    commands"].</para>
  </refdescription>

  <refparameter><para>[none]</para></refparameter>

  <refreturn>
  <para>Returns a <tag>section</tag> node.</para></refreturn>
</doc:template>
<xsl:template name="get.refentry.section">
  <xsl:choose>
    <xsl:when test="refmeta/manvolnum">
      <xsl:value-of select="refmeta/manvolnum"/>
    </xsl:when>
    <xsl:when test=".//funcsynopsis">3</xsl:when>
    <xsl:otherwise>1</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ==================================================================== -->

<doc:template name="get.refentry.date" xmlns="">
  <refpurpose>Gets date metadata for a refentry</refpurpose>

  <refdescription>
    <para>The <literal>man(7)</literal> man page describes this as "the
    date of the last revision". If we cannot find a date in the source, we
    generate one.</para>
  </refdescription>

  <refparameter>
    <variablelist>
      <varlistentry>
        <term>info</term>
        <listitem>
          <para>An info node (from a <tag>refentry</tag> element)</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>parentinfo</term>
        <listitem>
          <para>An info node (from a parent of a <tag>refentry</tag> element)</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>prefs</term>
        <listitem>
          <para>A node containing users preferences (from global stylesheet parameters)</para>
        </listitem>
      </varlistentry>
    </variablelist>
  </refparameter>

  <refreturn><para>Returns a <tag>date</tag> node.</para></refreturn>
</doc:template>

<xsl:template name="get.refentry.date">
  <xsl:param name="info"/>
  <xsl:param name="parentinfo"/>
  <xsl:param name="prefs"/>
  <xsl:variable name="Date">
    <xsl:choose>
      <!-- * if profiling is enabled for date, and the date -->
      <!-- * profile is non-empty, use it -->
      <xsl:when test="$prefs/@profileEnabled != '0' and
                      $prefs/@profile != ''">
        <xsl:call-template name="evaluate.info.profile">
          <xsl:with-param name="profile" select="$prefs/@profile"/>
          <xsl:with-param name="info" select="$info"/>
          <xsl:with-param name="parentinfo" select="$parentinfo"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <!-- * either profiling is not enabled for date, or the-->
        <!-- * date profile is empty, so we need to look for date -->
        <!-- * first in *info, then in parentinfo -->
        <xsl:choose>
          <!-- * look for date or pubdate in *info -->
          <xsl:when test="$info/date
                          |$info/pubdate">
            <xsl:copy>
              <xsl:apply-templates
                  select="($info/date
                          |$info/pubdate)[1]/node()"/>
            </xsl:copy>
          </xsl:when>
          <!-- * look for date or pubdate in parentinfo -->
          <xsl:otherwise>
            <xsl:copy>
              <xsl:apply-templates
                  select="($parentinfo/date
                          |$parentinfo/pubdate)[1]/node()"/>
            </xsl:copy>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:choose>
    <xsl:when test="$Date != ''">
      <xsl:value-of select="$Date"/>
    </xsl:when>
    <!-- * We couldn't find a date, so we generate a date. -->
    <!-- * And we make it an appropriately localized date. -->
    <xsl:otherwise>
      <xsl:call-template name="datetime.format">
        <xsl:with-param name="date">
          <xsl:choose>
            <xsl:when test="function-available('date:date-time')">
              <xsl:value-of select="date:date-time()"/>
            </xsl:when>
            <xsl:when test="function-available('date:dateTime')">
              <!-- Xalan quirk -->
              <xsl:value-of select="date:dateTime()"/>
            </xsl:when>
          </xsl:choose>
        </xsl:with-param>
        <xsl:with-param name="format">
          <xsl:call-template name="gentext.template">
            <xsl:with-param name="context" select="'datetime'"/>
            <xsl:with-param name="name" select="'format'"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ==================================================================== -->

<doc:template name="get.refentry.source" xmlns="">
  <refpurpose>Gets source metadata for a refentry</refpurpose>

  <refdescription>
    <para>The <literal>man(7)</literal> man page describes this as "the
    source of the command", and provides the following examples:
    <itemizedlist>
      <listitem>
        <para>For binaries, use something like: GNU, NET-2, SLS
        Distribution, MCC Distribution.</para>
      </listitem>
      <listitem>
        <para>For system calls, use the version of the kernel that you are
        currently looking at: Linux 0.99.11.</para>
      </listitem>
      <listitem>
        <para>For library calls, use the source of the function: GNU, BSD
        4.3, Linux DLL 4.4.1.</para>
      </listitem>
    </itemizedlist>
    </para>

    <para>In practice, there are many pages that simply have a version
    number in the "source" field. So, it looks like what we have is a
    two-part field,
    <replaceable>Name</replaceable>&#160;<replaceable>Version</replaceable>,
    where:
    <variablelist>
      <varlistentry>
        <term>Name</term>
        <listitem>
          <para>product name (e.g., BSD) or org. name (e.g., GNU)</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>Version</term>
        <listitem>
          <para>version name</para>
        </listitem>
      </varlistentry>
    </variablelist>
    Each part is optional. If the <replaceable>Name</replaceable> is a
    product name, then the <replaceable>Version</replaceable> is probably
    the version of the product. Or there may be no
    <replaceable>Name</replaceable>, in which case, if there is a
    <replaceable>Version</replaceable>, it is probably the version of the
    item itself, not the product it is part of. Or, if the
    <replaceable>Name</replaceable> is an organization name, then there
    probably will be no <replaceable>Version</replaceable>.
    </para>
  </refdescription>

  <refparameter>
    <variablelist>
      <varlistentry>
        <term>info</term>
        <listitem>
          <para>An info node (from a <tag>refentry</tag> element)</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>parentinfo</term>
        <listitem>
          <para>An info node (from a parent of a <tag>refentry</tag> element)</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>prefs</term>
        <listitem>
          <para>A node containing users preferences (from global
          stylesheet parameters)</para>
        </listitem>
      </varlistentry>
    </variablelist>
  </refparameter>

  <refreturn><para>Returns a <tag>source</tag> node.</para></refreturn>
</doc:template>

<xsl:template name="get.refentry.source">
  <xsl:param name="info"/>
  <xsl:param name="parentinfo"/>
  <xsl:param name="prefs"/>
  <xsl:variable name="Name">
    <xsl:if test="$prefs/Name/@suppress = '0'">
      <xsl:call-template name="get.refentry.source.name">
        <xsl:with-param name="info" select="$info"/>
        <xsl:with-param name="parentinfo" select="$parentinfo"/>
        <xsl:with-param name="prefs" select="$prefs/Name"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:variable>
  <xsl:variable name="Version">
    <xsl:if test="$prefs/Version/@suppress = '0'">
      <xsl:call-template name="get.refentry.version">
        <xsl:with-param name="info" select="$info"/>
        <xsl:with-param name="parentinfo" select="$parentinfo"/>
        <xsl:with-param name="prefs" select="$prefs/Version"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:variable>
  <xsl:choose>
    <!-- * if we have a Name and/or Version, use either or both -->
    <!-- * of those, in the form "Name Version" or just "Name" -->
    <!-- * or just "Version" -->
    <xsl:when test="$Name != '' or $Version != ''">
      <xsl:choose>
        <xsl:when test="$Name != '' and $Version != ''">
          <xsl:copy-of select="$Name"/>
          <xsl:text> </xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:copy-of select="$Name"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:copy-of select="$Version"/>
    </xsl:when>
    <!-- * if no Name and no Version, use fallback (if any) -->
    <xsl:when test="$prefs/@fallback != ''">
      <xsl:call-template name="evaluate.info.profile">
        <xsl:with-param name="profile" select="$prefs/@fallback"/>
        <xsl:with-param name="info" select="$info"/>
        <xsl:with-param name="parentinfo" select="$parentinfo"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <!-- * found nothing, so leave <source> empty -->
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ==================================================================== -->

<doc:template name="get.refentry.source.name" xmlns="">
  <refpurpose>Gets source-name metadata for a refentry</refpurpose>

  <refdescription>
    <para>A "source name" is one part of a (potentially) two-part
    <replaceable>Name</replaceable>&#160;<replaceable>Version</replaceable>
    source field. For more details, see the documentation for the
    <function>get.refentry.source</function> template.</para>
  </refdescription>

  <refparameter>
    <variablelist>
      <varlistentry>
        <term>info</term>
        <listitem>
          <para>An info node (from a <tag>refentry</tag> element)</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>parentinfo</term>
        <listitem>
          <para>An info node (from a parent of a <tag>refentry</tag> element)</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>prefs</term>
        <listitem>
          <para>A node containing users preferences (from global
          stylesheet parameters)</para>
        </listitem>
      </varlistentry>
    </variablelist>
  </refparameter>

  <refreturn><para>Depending on what output method is used for the
  current stylesheet, either returns a text node or possibly an element
  node, containing "source name" data.</para></refreturn>
</doc:template>

<xsl:template name="get.refentry.source.name">
  <xsl:param name="info"/>
  <xsl:param name="parentinfo"/>
  <xsl:param name="prefs"/>
  <xsl:choose>
    <!-- * if profiling is enabled for source.name, and the -->
    <!-- * source.name profile is non-empty, use it -->
    <xsl:when test="$prefs/@profileEnabled != '0' and
                    $prefs/@profile != ''">
      <xsl:message>using source.name profile</xsl:message>
      <xsl:call-template name="evaluate.info.profile">
        <xsl:with-param name="profile" select="$prefs/@profile"/>
        <xsl:with-param name="info" select="$info"/>
        <xsl:with-param name="parentinfo" select="$parentinfo"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <!-- * either profiling for source.name is not enabled, or-->
      <!-- * the source.name profile is empty; so we need to look -->
      <!-- * for a name to use -->
      <xsl:choose>
        <xsl:when test="refmeta/refmiscinfo[@class = 'source']">
          <xsl:apply-templates 
              select="refmeta/refmiscinfo[@class = 'source'][1]/node()"/>
        </xsl:when>
        <!-- * no <refmisc class="source"/> found, so we need to -->
        <!-- * check *info and parentinfo -->
        <xsl:when test="$info/productname">
          <xsl:apply-templates select="$info/productname/node()"/>
        </xsl:when>
        <xsl:when test="$info/orgname">
          <xsl:apply-templates select="$info/orgname/node()"/>
        </xsl:when>
        <xsl:when test="$info/corpname">
          <xsl:apply-templates select="$info/corpname/node()"/>
        </xsl:when>
        <xsl:when test="$info/corpcredit">
          <xsl:apply-templates select="$info/corpcredit/node()"/>
        </xsl:when>
        <xsl:when test="$info/corpauthor">
          <xsl:apply-templates select="$info/corpauthor/node()"/>
        </xsl:when>
        <xsl:when test="$info/author/orgname">
          <xsl:apply-templates select="$info/author/orgname/node()"/>
        </xsl:when>
        <xsl:when test="$info/author/publishername">
          <xsl:apply-templates select="$info/author/publishername/node()"/>
        </xsl:when>
        <!-- * then check parentinfo -->
        <xsl:when test="$parentinfo/productname">
          <xsl:apply-templates select="$parentinfo/productname/node()"/>
        </xsl:when>
        <xsl:when test="$parentinfo/orgname">
          <xsl:apply-templates select="$parentinfo/orgname/node()"/>
        </xsl:when>
        <xsl:when test="$parentinfo/corpname">
          <xsl:apply-templates select="$parentinfo/corpname/node()"/>
        </xsl:when>
        <xsl:when test="$parentinfo/corpcredit">
          <xsl:apply-templates select="$parentinfo/corpcredit/node()"/>
        </xsl:when>
        <xsl:when test="$parentinfo/corpauthor">
          <xsl:apply-templates select="$parentinfo/corpauthor/node()"/>
        </xsl:when>
        <xsl:when test="$parentinfo/author/orgname">
          <xsl:apply-templates select="$parentinfo/author/orgname/node()"/>
        </xsl:when>
        <xsl:when test="$parentinfo/author/publishername">
          <xsl:apply-templates select="$parentinfo/author/publishername/node()"/>
        </xsl:when>
        <xsl:otherwise>
          <!-- * found nothing, so return nothing -->
        </xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ==================================================================== -->

<doc:template name="get.refentry.version" xmlns="">
  <refpurpose>Gets version metadata for a refentry</refpurpose>

  <refdescription>
    <para>A "version" is one part of a (potentially) two-part
    <replaceable>Name</replaceable>&#160;<replaceable>Version</replaceable>
    source field. For more details, see the documentation for the
    <function>get.refentry.source</function> template.</para>
  </refdescription>

  <refparameter>
    <variablelist>
      <varlistentry>
        <term>info</term>
        <listitem>
          <para>An info node (from a <tag>refentry</tag> element)</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>parentinfo</term>
        <listitem>
          <para>An info node (from a parent of a <tag>refentry</tag> element)</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>prefs</term>
        <listitem>
          <para>A node containing users preferences (from global
          stylesheet parameters)</para>
        </listitem>
      </varlistentry>
    </variablelist>
  </refparameter>

  <refreturn><para>Depending on what output method is used for the
  current stylesheet, either returns a text node or possibly an element
  node, containing "version" data.</para></refreturn>
</doc:template>

<xsl:template name="get.refentry.version">
  <xsl:param name="info"/>
  <xsl:param name="parentinfo"/>
  <xsl:param name="prefs"/>
  <xsl:choose>
    <!-- * if profiling is enabled for version, and the -->
    <!-- * version profile is non-empty, use it -->
    <xsl:when test="$prefs/@profileEnabled != '0' and
                    $prefs/@profile != ''">
      <xsl:message>using version profile</xsl:message>
      <xsl:call-template name="evaluate.info.profile">
        <xsl:with-param name="profile" select="$prefs/@profile"/>
        <xsl:with-param name="info" select="$info"/>
        <xsl:with-param name="parentinfo" select="$parentinfo"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <!-- * either profiling for source.name is not enabled, or-->
      <!-- * the source.name profile is empty; so we need to look -->
      <!-- * for a name to use -->
      <xsl:choose>
        <xsl:when test="refmeta/refmiscinfo[@class = 'version']">
          <xsl:apply-templates 
              select="refmeta/refmiscinfo[@class = 'version'][1]/node()"/>
        </xsl:when>
        <!-- * no <refmisc class="version"/> found, so we need to -->
        <!-- * check *info and parentinfo -->
        <xsl:when test="$info/productnumber">
          <xsl:apply-templates select="$info/productnumber/node()"/>
        </xsl:when>
        <xsl:when test="$info/edition">
          <xsl:apply-templates select="$info/edition/node()"/>
        </xsl:when>
        <xsl:when test="$info/releaseinfo">
          <xsl:apply-templates select="$info/releaseinfo/node()"/>
        </xsl:when>
        <!-- * then check parentinfo -->
        <xsl:when test="$parentinfo/productnumber">
          <xsl:apply-templates select="$parentinfo/productnumber/node()"/>
        </xsl:when>
        <xsl:when test="$parentinfo/edition">
          <xsl:apply-templates select="$parentinfo/edition/node()"/>
        </xsl:when>
        <xsl:when test="$parentinfo/releaseinfo">
          <xsl:apply-templates select="$parentinfo/releaseinfo/node()"/>
        </xsl:when>
        <xsl:otherwise>
          <!-- *found nothing, so return nothing -->
        </xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ==================================================================== -->

<doc:template name="get.refentry.manual" xmlns="">
  <refpurpose>Gets source metadata for a refentry</refpurpose>

  <refdescription>
    <para>The <literal>man(7)</literal> man page describes this as "the
    title of the manual (e.g., <citetitle>Linux Programmer's
    Manual</citetitle>)". Here are some examples from existing man pages:
    <itemizedlist>
      <listitem>
        <para><citetitle>dpkg utilities</citetitle>
        (<command>dpkg-name</command>)</para>
      </listitem>
      <listitem>
        <para><citetitle>User Contributed Perl Documentation</citetitle>
        (<command>GET</command>)</para>
      </listitem>
      <listitem>
        <para><citetitle>GNU Development Tools</citetitle>
        (<command>ld</command>)</para>
      </listitem>
      <listitem>
        <para><citetitle>Emperor Norton Utilities</citetitle>
        (<command>ddate</command>)</para>
      </listitem>
      <listitem>
        <para><citetitle>Debian GNU/Linux manual</citetitle>
        (<command>faked</command>)</para>
      </listitem>
      <listitem>
        <para><citetitle>GIMP Manual Pages</citetitle>
        (<command>gimp</command>)</para>
      </listitem>
      <listitem>
        <para><citetitle>KDOC Documentation System</citetitle>
        (<command>qt2kdoc</command>)</para>
      </listitem>
    </itemizedlist>
    </para>
  </refdescription>

  <refparameter>
    <variablelist>
      <varlistentry>
        <term>info</term>
        <listitem>
          <para>An info node (from a <tag>refentry</tag> element)</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>parentinfo</term>
        <listitem>
          <para>An info node (from a parent of a <tag>refentry</tag> element)</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>prefs</term>
        <listitem>
          <para>A node containing users preferences (from global
          stylesheet parameters)</para>
        </listitem>
      </varlistentry>
    </variablelist>
  </refparameter>

  <refreturn><para>Returns a <tag>manual</tag> node.</para></refreturn>
</doc:template>
<xsl:template name="get.refentry.manual">
  <xsl:param name="info"/>
  <xsl:param name="parentinfo"/>
  <xsl:param name="prefs"/>
  <xsl:variable name="Manual">
    <xsl:choose>
      <!-- * if profiling is enabled for manual, and the manual -->
      <!-- * profile is non-empty, use it -->
      <xsl:when test="$prefs/@profileEnabled != '0' and
                      $prefs/@profile != ''">
        <xsl:message>using manual profile</xsl:message>
        <xsl:call-template name="evaluate.info.profile">
          <xsl:with-param name="profile" select="$prefs/@profile"/>
          <xsl:with-param name="info" select="$info"/>
          <xsl:with-param name="parentinfo" select="$parentinfo"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <!-- * either profiling for source.name is not enabled, or-->
        <!-- * the source.name profile is empty; so we need to look -->
        <!-- * for a name to use -->
        <xsl:choose>
          <xsl:when test="refmeta/refmiscinfo[@class = 'manual']">
            <xsl:apply-templates 
                select="refmeta/refmiscinfo[@class = 'manual'][1]/node()"/>
          </xsl:when>
          <!-- * no <refmisc class="manual"/> found, so we need to -->
          <!-- * check title in parentinfo and parent title -->
          <xsl:when test="$parentinfo/title">
            <xsl:apply-templates select="$parentinfo/title/node()"/>
          </xsl:when>
          <xsl:when test="../title">
            <xsl:apply-templates select="../title/node()"/>
          </xsl:when>
          <xsl:otherwise>
            <!-- * found nothing, so return nothing -->
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:choose>
    <xsl:when test="$Manual != ''">
      <xsl:copy-of select="$Manual"/>
    </xsl:when>
    <!-- * if no Manual, use contents of specified -->
    <!-- * Fallback (if any) -->
    <xsl:when test="$prefs/@fallback != ''">
      <xsl:call-template name="evaluate.info.profile">
        <xsl:with-param name="profile" select="$prefs/@fallback"/>
        <xsl:with-param name="info" select="$info"/>
        <xsl:with-param name="parentinfo" select="$parentinfo"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <!-- * found nothing, so leave it empty -->
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ====================================================================== -->

<doc:template name="get.refentry.metadata.prefs" xmlns="">
  <refpurpose>Gets user preferences for refentry metadata gathering</refpurpose>

  <refdescription>
    <para>The DocBook XSL stylesheets include several user-configurable
    global stylesheet parameters for controlling <tag>refentry</tag>
    metadata gathering. Those parameters are not read directly by the
    other <tag>refentry</tag> metadata-gathering functions. Instead, they
    are read only by the <function>get.refentry.metadata.prefs</function>
    function, which assembles them into a structure that is then passed to
    the other <tag>refentry</tag> metadata-gathering functions.</para>

    <para>So the, <function>get.refentry.metadata.prefs</function>
    function is the only interface to collecting stylesheet parameters for
    controlling <tag>refentry</tag> metadata gathering.</para>
  </refdescription>

  <refparameter>
    <para>There are no local parameters for this function; however, it
    does rely on a number of global parameters.</para>
  </refparameter>

  <refreturn><para>Returns a <tag>manual</tag> node.</para></refreturn>
</doc:template>

<xsl:template name="get.refentry.metadata.prefs">
  <DatePrefs>
    <xsl:attribute name="profile">
      <xsl:value-of select="$refentry.date.profile"/>
    </xsl:attribute>
    <xsl:attribute name="profileEnabled">
      <xsl:value-of select="$refentry.date.profile.enabled"/>
    </xsl:attribute>
  </DatePrefs>
  <SourcePrefs>
    <xsl:attribute name="fallback">
      <xsl:value-of select="$refentry.source.fallback.profile"/>
    </xsl:attribute>
    <Name>
      <xsl:attribute name="profile">
        <xsl:value-of select="$refentry.source.name.profile"/>
      </xsl:attribute>
      <xsl:attribute name="profileEnabled">
        <xsl:value-of select="$refentry.source.name.profile.enabled"/>
      </xsl:attribute>
      <xsl:attribute name="suppress">
        <xsl:value-of select="$refentry.source.name.suppress"/>
      </xsl:attribute>
    </Name>
    <Version>
      <xsl:attribute name="profile">
        <xsl:value-of select="$refentry.version.profile"/>
      </xsl:attribute>
      <xsl:attribute name="profileEnabled">
        <xsl:value-of select="$refentry.version.profile.enabled"/>
      </xsl:attribute>
      <xsl:attribute name="suppress">
        <xsl:value-of select="$refentry.version.suppress"/>
      </xsl:attribute>
    </Version>
  </SourcePrefs>
  <ManualPrefs>
    <xsl:attribute name="fallback">
      <xsl:value-of select="$refentry.manual.fallback.profile"/>
    </xsl:attribute>
    <xsl:attribute name="profile">
      <xsl:value-of select="$refentry.manual.profile"/>
    </xsl:attribute>
    <xsl:attribute name="profileEnabled">
      <xsl:value-of select="$refentry.manual.profile.enabled"/>
    </xsl:attribute>
  </ManualPrefs>
</xsl:template>

</xsl:stylesheet>
