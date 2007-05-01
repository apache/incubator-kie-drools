<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:doc="http://nwalsh.com/xsl/documentation/1.0"
                xmlns:date="http://exslt.org/dates-and-times"
                exclude-result-prefixes="doc date"
                version='1.0'>

<!-- ********************************************************************
     $Id: refentry.xsl 6566 2007-01-30 05:16:56Z xmldoc $
     ********************************************************************

     This file is part of the XSL DocBook Stylesheet distribution.
     See ../README or http://docbook.sf.net/release/xsl/current/ for
     copyright and other information.

     ******************************************************************** -->

<!-- ==================================================================== -->

<doc:reference xmlns="">
  <referenceinfo>
    <releaseinfo role="meta">
      $Id: refentry.xsl 6566 2007-01-30 05:16:56Z xmldoc $
    </releaseinfo>
    <corpauthor>The DocBook Project</corpauthor>
    <copyright>
      <year>2005-2007</year>
      <holder>The DocBook Project</holder>
    </copyright>
  </referenceinfo>
  <title>Refentry Metadata-Gathering Template Reference</title>

  <partintro id="partintro">
    <title>Introduction</title>

    <para>This is technical reference documentation for the "refentry
    metadata gathering" templates in the DocBook XSL Stylesheets.</para>

    <para>This is not intended to be user documentation. It is provided
    for developers writing customization layers for the
    stylesheets.</para>

    <note>
      <para>Currently, only the manpages stylesheets make use of these
      templates. They are, however, potentially useful elsewhere.</para>
    </note>

  </partintro>

</doc:reference>

<!-- ==================================================================== -->

<doc:template name="get.refentry.metadata" xmlns="">
  <refpurpose>Gathers metadata from a refentry and its ancestors</refpurpose>

  <refdescription>
    <para>Reference documentation for particular commands, functions,
    etc., is sometimes viewed in isolation from its greater "context". For
    example, users view Unix man pages as, well, individual pages, not as
    part of a "book" of some kind. Therefore, it is sometimes necessary to
    embed "context" information in output for each <sgmltag>refentry</sgmltag>.</para>

    <para>However, one problem is that different users mark up that
    context information in different ways. Often (usually), the
    context information is not actually part of the content of the
    <sgmltag>refentry</sgmltag> itself, but instead part of the content of a
    parent or ancestor element to the the <sgmltag>refentry</sgmltag>. And
    even then, DocBook provides a variety of elements that users might
    potentially use to mark up the same kind of information. One user
    might use the <sgmltag>productnumber</sgmltag> element to mark up version
    information about a particular product, while another might use
    the <sgmltag>releaseinfo</sgmltag> element.</para>

    <para>Taking all that in mind, the
    <function>get.refentry.metadata</function> function tries to gather
    metadata from a <sgmltag>refentry</sgmltag> element and its ancestor
    elements in an intelligent and user-configurable way. The basic
    mechanism used in the XPath expressions throughout this stylesheet
    is to select the relevant metadata from the *info element that is
    closest to the actual <sgmltag>refentry</sgmltag>&#160;– either on the
    <sgmltag>refentry</sgmltag> itself, or on its nearest ancestor.</para>

    <note>
      <para>The <function>get.refentry.metadata</function> function is
      actually just sort of a "driver" function; it calls other
      functions that do the actual data collection, then returns the
      data as a set.</para>
    </note>

  </refdescription>

  <refparameter>
    <variablelist>
      <varlistentry>
        <term>refname</term>
        <listitem>
          <para>The first <sgmltag>refname</sgmltag> in the refentry</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>info</term>
        <listitem>
          <para>A set of info nodes (from a <sgmltag>refentry</sgmltag>
          element and its ancestors)</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>prefs</term>
        <listitem>
          <para>A node containing user preferences (from global
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
  <xsl:param name="prefs"/>
  <title>
    <xsl:call-template name="get.refentry.title">
      <xsl:with-param name="refname" select="$refname"/>
    </xsl:call-template>
  </title>
  <section>
    <xsl:call-template name="get.refentry.section">
      <xsl:with-param name="refname" select="$refname"/>
    </xsl:call-template>
  </section>
  <date>
    <xsl:call-template name="get.refentry.date">
      <xsl:with-param name="info" select="$info"/>
      <xsl:with-param name="refname" select="$refname"/>
      <xsl:with-param name="prefs" select="$prefs/DatePrefs"/>
    </xsl:call-template>
  </date>
  <source>
    <xsl:call-template name="get.refentry.source">
      <xsl:with-param name="info" select="$info"/>
      <xsl:with-param name="refname" select="$refname"/>
      <xsl:with-param name="prefs" select="$prefs/SourcePrefs"/>
    </xsl:call-template>
  </source>
  <manual>
    <xsl:call-template name="get.refentry.manual">
      <xsl:with-param name="info" select="$info"/>
      <xsl:with-param name="refname" select="$refname"/>
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
    from <sgmltag>refname</sgmltag> in that, if the <sgmltag>refentry</sgmltag> has a
    <sgmltag>refentrytitle</sgmltag>, we use that as the <sgmltag>title</sgmltag>;
    otherwise, we just use first <sgmltag>refname</sgmltag> in the first
    <sgmltag>refnamediv</sgmltag> in the source.</para>
  </refdescription>

  <refparameter>
    <variablelist>
      <varlistentry>
        <term>refname</term>
        <listitem>
          <para>The first <sgmltag>refname</sgmltag> in the refentry</para>
        </listitem>
      </varlistentry>
    </variablelist>
  </refparameter>

  <refreturn>
  <para>Returns a <sgmltag>title</sgmltag> node.</para></refreturn>
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
    <literal>7</literal>)". If we do not find a <sgmltag>manvolnum</sgmltag>
    specified in the source, and we find that the <sgmltag>refentry</sgmltag> is
    for a function, we use the section number <literal>3</literal>
    ["Library calls (functions within program libraries)"]; otherwise, we
    default to using <literal>1</literal> ["Executable programs or shell
    commands"].</para>
  </refdescription>

  <refparameter>
    <variablelist>
      <varlistentry>
        <term>refname</term>
        <listitem>
          <para>The first <sgmltag>refname</sgmltag> in the refentry</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>quiet</term>
        <listitem>
          <para>If non-zero, no "missing" message is emitted</para>
        </listitem>
      </varlistentry>
    </variablelist>
  </refparameter>

  <refreturn>
  <para>Returns a string representing a section number.</para></refreturn>
</doc:template>
<xsl:template name="get.refentry.section">
  <xsl:param name="refname"/>
  <xsl:param name="quiet" select="0"/>
  <xsl:choose>
    <xsl:when test="refmeta/manvolnum">
      <xsl:value-of select="refmeta/manvolnum"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:if test="$quiet = 0">
        <xsl:if test="$refentry.meta.get.quietly = 0">
          <xsl:call-template name="log.message">
            <xsl:with-param name="level">Note</xsl:with-param>
            <xsl:with-param name="source" select="$refname"/>
            <xsl:with-param
                name="message"
                >meta manvol : No manvolnum</xsl:with-param>
          </xsl:call-template>
        </xsl:if>
      </xsl:if>
      <xsl:choose>
        <xsl:when test=".//funcsynopsis">
          <xsl:if test="$quiet = 0">
            <xsl:if test="$refentry.meta.get.quietly = 0">
              <xsl:call-template name="log.message">
                <xsl:with-param name="level">Note</xsl:with-param>
                <xsl:with-param name="source" select="$refname"/>
                <xsl:with-param
                    name="message"
                    >meta manvol : Setting man section to 3</xsl:with-param>
              </xsl:call-template>
            </xsl:if>
          </xsl:if>
          <xsl:text>3</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>1</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
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
        <term>refname</term>
        <listitem>
          <para>The first <sgmltag>refname</sgmltag> in the refentry</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>info</term>
        <listitem>
          <para>A set of info nodes (from a <sgmltag>refentry</sgmltag>
          element and its ancestors)</para>
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

  <refreturn><para>Returns a <sgmltag>date</sgmltag> node.</para></refreturn>
</doc:template>

<xsl:template name="get.refentry.date">
  <xsl:param name="refname"/>
  <xsl:param name="info"/>
  <xsl:param name="prefs"/>
  <xsl:variable name="Date">
    <xsl:choose>
      <!-- * if profiling is enabled for date, and the date -->
      <!-- * profile is non-empty, use it -->
      <xsl:when test="not($prefs/@profileEnabled = 0) and
                      not($prefs/@profile = '')">
        <xsl:call-template name="evaluate.info.profile">
          <xsl:with-param name="profile" select="$prefs/@profile"/>
          <xsl:with-param name="info" select="$info"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <!-- * either profiling is not enabled for date, or the-->
        <!-- * date profile is empty, so we need to look for date -->
        <!-- * in *info -->
        <xsl:choose>
          <!-- * look for date or pubdate in *info -->
          <xsl:when test="$info/date/node()
                          |$info/pubdate/node()">
            <xsl:apply-templates
                select="(($info[date])[last()]/date)[1]|
                        (($info[pubdate])[last()]/pubdate)[1]"/>
          </xsl:when>
          <xsl:otherwise>
            <!-- * found no Date or Pubdate -->
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:choose>
    <xsl:when test="not($Date = '')">
      <xsl:value-of select="$Date"/>
    </xsl:when>
    <!-- * We couldn't find a date, so we generate a date. -->
    <!-- * And we make it an appropriately localized date. -->
    <xsl:otherwise>
      <xsl:if test="$refentry.meta.get.quietly = 0">
        <xsl:call-template name="log.message">
          <xsl:with-param name="level">Note</xsl:with-param>
          <xsl:with-param name="source" select="$refname"/>
          <xsl:with-param
              name="message"
              >meta date   : No date. Using generated date</xsl:with-param>
        </xsl:call-template>
      </xsl:if>
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

    <para>The <literal>solbook(5)</literal> man page describes
    something very much like what <literal>man(7)</literal> calls
    "source", except that <literal>solbook(5)</literal> names it
    "software" and describes it like this:
    <blockquote>
      <para>This is the name of the software product that the topic
      discussed on the reference page belongs to. For example UNIX
      commands are part of the <literal>SunOS x.x</literal>
      release.</para>
    </blockquote>
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
        <term>refname</term>
        <listitem>
          <para>The first <sgmltag>refname</sgmltag> in the refentry</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>info</term>
        <listitem>
          <para>A set of info nodes (from a <sgmltag>refentry</sgmltag>
          element and its ancestors)</para>
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

  <refreturn><para>Returns a <sgmltag>source</sgmltag> node.</para></refreturn>
</doc:template>

<xsl:template name="get.refentry.source">
  <xsl:param name="refname"/>
  <xsl:param name="info"/>
  <xsl:param name="prefs"/>
  <xsl:variable name="Name">
    <xsl:if test="$prefs/Name/@suppress = 0">
      <xsl:call-template name="get.refentry.source.name">
        <xsl:with-param name="info" select="$info"/>
        <xsl:with-param name="refname" select="$refname"/>
        <xsl:with-param name="prefs" select="$prefs/Name"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:variable>
  <xsl:variable name="Version">
    <xsl:if test="$prefs/Version/@suppress = 0">
      <xsl:call-template name="get.refentry.version">
        <xsl:with-param name="info" select="$info"/>
        <xsl:with-param name="refname" select="$refname"/>
        <xsl:with-param name="prefs" select="$prefs/Version"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:variable>
  <xsl:choose>
    <!-- * if we have a Name and/or Version, use either or both -->
    <!-- * of those, in the form "Name Version" or just "Name" -->
    <!-- * or just "Version" -->
    <xsl:when test="not($Name = '') or not($Version = '')">
      <xsl:choose>
        <xsl:when test="not($Name = '') and not($Version = '')">
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
    <xsl:when test="not($prefs/@fallback = '')">
      <xsl:variable name="source.fallback">
        <xsl:call-template name="evaluate.info.profile">
          <xsl:with-param name="profile" select="$prefs/@fallback"/>
          <xsl:with-param name="info" select="$info"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="not($source.fallback = '')">
          <xsl:value-of select="$source.fallback"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="$refentry.meta.get.quietly = 0">
            <xsl:call-template name="log.message">
              <xsl:with-param name="level">Warn</xsl:with-param>
              <xsl:with-param name="source" select="$refname"/>
              <xsl:with-param
                  name="message"
                  >meta source : No valid fallback. Leaving empty</xsl:with-param>
            </xsl:call-template>
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <xsl:if test="$refentry.meta.get.quietly = 0">
        <xsl:call-template name="log.message">
          <xsl:with-param name="level">Warn</xsl:with-param>
          <xsl:with-param name="source" select="$refname"/>
          <xsl:with-param
              name="message"
              >meta source : No fallback specified; leaving empty.</xsl:with-param>
        </xsl:call-template>
      </xsl:if>
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
        <term>refname</term>
        <listitem>
          <para>The first <sgmltag>refname</sgmltag> in the refentry</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>info</term>
        <listitem>
          <para>A set of info nodes (from a <sgmltag>refentry</sgmltag>
          element and its ancestors)</para>
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
  <xsl:param name="refname"/>
  <xsl:param name="info"/>
  <xsl:param name="prefs"/>
  <xsl:choose>
    <!-- * if profiling is enabled for source.name, and the -->
    <!-- * source.name profile is non-empty, use it -->
    <xsl:when test="not($prefs/@profileEnabled = 0) and
                    not($prefs/@profile = '')">
      <xsl:call-template name="evaluate.info.profile">
        <xsl:with-param name="profile" select="$prefs/@profile"/>
        <xsl:with-param name="info" select="$info"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <!-- * either profiling for source.name is not enabled, or-->
      <!-- * the source.name profile is empty; so we need to look -->
      <!-- * for a name to use -->
      <xsl:choose>
        <xsl:when test="refmeta/refmiscinfo[@class = 'source' or @class = 'software']">
          <xsl:apply-templates 
              select="refmeta/refmiscinfo[@class = 'source' or @class='software'][1]/node()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:choose>
            <xsl:when test="$info/productname">
              <xsl:call-template name="set.refentry.metadata">
                <xsl:with-param name="refname" select="$refname"/>
                <xsl:with-param
                    name="info"
                    select="($info[productname])[last()]"/>
                <xsl:with-param
                    name="contents"
                    select="(($info[productname])[last()]/productname)[1]"/>
                <xsl:with-param name="context">source</xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:when test="$info/corpname">
              <xsl:call-template name="set.refentry.metadata">
                <xsl:with-param name="refname" select="$refname"/>
                <xsl:with-param
                    name="info"
                    select="($info[corpname])[last()]"/>
                <xsl:with-param
                    name="contents"
                    select="(($info[corpname])[last()]/corpname)[1]"/>
                <xsl:with-param name="context">source</xsl:with-param>
                <xsl:with-param name="preferred">productname</xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:when test="$info/corpcredit">
              <xsl:call-template name="set.refentry.metadata">
                <xsl:with-param name="refname" select="$refname"/>
                <xsl:with-param
                    name="info"
                    select="($info[corpcredit])[last()]"/>
                <xsl:with-param
                    name="contents"
                    select="(($info[corpcredit])[last()]/corpcredit)[1]"/>
                <xsl:with-param name="context">source</xsl:with-param>
                <xsl:with-param name="preferred">productname</xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:when test="$info/corpauthor">
              <xsl:call-template name="set.refentry.metadata">
                <xsl:with-param name="refname" select="$refname"/>
                <xsl:with-param
                    name="info"
                    select="($info[corpauthor])[last()]"/>
                <xsl:with-param
                    name="contents"
                    select="(($info[corpauthor])[last()]/corpauthor)[1]"/>
                <xsl:with-param name="context">source</xsl:with-param>
                <xsl:with-param name="preferred">productname</xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:when test="$info//orgname">
              <xsl:call-template name="set.refentry.metadata">
                <xsl:with-param name="refname" select="$refname"/>
                <xsl:with-param
                    name="info"
                    select="($info[//orgname])[last()]"/>
                <xsl:with-param
                    name="contents"
                    select="(($info[//orgname])[last()]//orgname)[1]"/>
                <xsl:with-param name="context">source</xsl:with-param>
                <xsl:with-param name="preferred">productname</xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:when test="$info//publishername">
              <xsl:call-template name="set.refentry.metadata">
                <xsl:with-param name="refname" select="$refname"/>
                <xsl:with-param
                    name="info"
                    select="($info[//publishername])[last()]"/>
                <xsl:with-param
                    name="contents"
                    select="(($info[//publishername])[last()]//publishername)[1]"/>
                <xsl:with-param name="context">source</xsl:with-param>
                <xsl:with-param name="preferred">productname</xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:if test="$refentry.meta.get.quietly = 0">
                <xsl:call-template name="log.message">
                  <xsl:with-param name="level">Note</xsl:with-param>
                  <xsl:with-param name="source" select="$refname"/>
                  <xsl:with-param
                      name="message"
                      >meta source : No productname or alternative</xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="log.message">
                  <xsl:with-param name="level">Note</xsl:with-param>
                  <xsl:with-param name="source" select="$refname"/>
                  <xsl:with-param
                      name="message"
                      >meta source : No refmiscinfo@class=source</xsl:with-param>
                </xsl:call-template>
              </xsl:if>
            </xsl:otherwise>
          </xsl:choose>
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
        <term>refname</term>
        <listitem>
          <para>The first <sgmltag>refname</sgmltag> in the refentry</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>info</term>
        <listitem>
          <para>A set of info nodes (from a <sgmltag>refentry</sgmltag>
          element and its ancestors)</para>
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
  <xsl:param name="refname"/>
  <xsl:param name="info"/>
  <xsl:param name="prefs"/>
  <xsl:choose>
    <!-- * if profiling is enabled for version, and the -->
    <!-- * version profile is non-empty, use it -->
    <xsl:when test="not($prefs/@profileEnabled = 0) and
                    not($prefs/@profile = '')">
      <xsl:call-template name="evaluate.info.profile">
        <xsl:with-param name="profile" select="$prefs/@profile"/>
        <xsl:with-param name="info" select="$info"/>
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
        <xsl:otherwise>
          <xsl:choose>
            <xsl:when test="$info/productnumber">
              <xsl:call-template name="set.refentry.metadata">
                <xsl:with-param name="refname" select="$refname"/>
                <xsl:with-param
                    name="info"
                    select="($info[productnumber])[last()]"/>
                <xsl:with-param
                    name="contents"
                    select="(($info[productnumber])[last()]/productnumber)[1]"/>
                <xsl:with-param name="context">version</xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:when test="$info/edition">
              <xsl:call-template name="set.refentry.metadata">
                <xsl:with-param name="refname" select="$refname"/>
                <xsl:with-param
                    name="info"
                    select="($info[edition])[last()]"/>
                <xsl:with-param
                    name="contents"
                    select="(($info[edition])[last()]/edition)[1]"/>
                <xsl:with-param name="context">version</xsl:with-param>
                <xsl:with-param name="preferred">productnumber</xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:when test="$info/releaseinfo">
              <xsl:call-template name="set.refentry.metadata">
                <xsl:with-param name="refname" select="$refname"/>
                <xsl:with-param
                    name="info"
                    select="($info[releaseinfo])[last()]"/>
                <xsl:with-param
                    name="contents"
                    select="(($info[releaseinfo])[last()]/releaseinfo)[1]"/>
                <xsl:with-param name="context">version</xsl:with-param>
                <xsl:with-param name="preferred">productnumber</xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:if test="$refentry.meta.get.quietly = 0">
                <xsl:call-template name="log.message">
                  <xsl:with-param name="level">Note</xsl:with-param>
                  <xsl:with-param name="source" select="$refname"/>
                  <xsl:with-param
                      name="message"
                      >meta version: No productnumber or alternative</xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="log.message">
                  <xsl:with-param name="level">Note</xsl:with-param>
                  <xsl:with-param name="source" select="$refname"/>
                  <xsl:with-param
                      name="message"
                      >meta version: No refmiscinfo@class=version</xsl:with-param>
                </xsl:call-template>
              </xsl:if>
            </xsl:otherwise>
          </xsl:choose>
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

    <para>The <literal>solbook(5)</literal> man page describes
    something very much like what <literal>man(7)</literal> calls
    "manual", except that <literal>solbook(5)</literal> names it
    "sectdesc" and describes it like this:
    <blockquote>
      <para>This is the section title of the reference page; for
      example <literal>User Commands</literal>.</para>
    </blockquote>
    </para>

  </refdescription>

  <refparameter>
    <variablelist>
      <varlistentry>
        <term>refname</term>
        <listitem>
          <para>The first <sgmltag>refname</sgmltag> in the refentry</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>info</term>
        <listitem>
          <para>A set of info nodes (from a <sgmltag>refentry</sgmltag>
          element and its ancestors)</para>
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

  <refreturn><para>Returns a <sgmltag>manual</sgmltag> node.</para></refreturn>
</doc:template>
<xsl:template name="get.refentry.manual">
  <xsl:param name="refname"/>
  <xsl:param name="info"/>
  <xsl:param name="prefs"/>
  <xsl:variable name="Manual">
    <xsl:choose>
      <!-- * if profiling is enabled for manual, and the manual -->
      <!-- * profile is non-empty, use it -->
      <xsl:when test="not($prefs/@profileEnabled = 0) and
                      not($prefs/@profile = '')">
        <xsl:call-template name="evaluate.info.profile">
          <xsl:with-param name="profile" select="$prefs/@profile"/>
          <xsl:with-param name="info" select="$info"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="refmeta/refmiscinfo[@class = 'manual' or @class = 'sectdesc']">
            <xsl:apply-templates 
                select="refmeta/refmiscinfo[@class = 'manual' or @class = 'sectdesc'][1]/node()"/>
          </xsl:when>
          <xsl:otherwise>
            <!-- * only in the case of choosing appropriate -->
            <!-- * "manual" content do we select the furthest -->
            <!-- * (first) matching element instead of the -->
            <!-- * closest (last) matching one -->
            <xsl:choose>
              <xsl:when test="ancestor::*/title">
                <xsl:call-template name="set.refentry.metadata">
                  <xsl:with-param name="refname" select="$refname"/>
                  <xsl:with-param
                      name="info"
                      select="(ancestor::*[title])[1]"/>
                  <xsl:with-param
                      name="contents"
                      select="(ancestor::*[title])[1]/title"/>
                  <xsl:with-param name="context">manual</xsl:with-param>
                </xsl:call-template>
              </xsl:when>
              <xsl:when test="$info/title">
                <xsl:call-template name="set.refentry.metadata">
                  <xsl:with-param name="refname" select="$refname"/>
                  <xsl:with-param
                      name="info"
                      select="($info[title])[1]"/>
                  <xsl:with-param
                      name="contents"
                      select="(($info[title])[1]/title)[1]"/>
                  <xsl:with-param name="context">manual</xsl:with-param>
                </xsl:call-template>
              </xsl:when>
              <xsl:otherwise>
                <xsl:if test="$refentry.meta.get.quietly = 0">
                  <xsl:call-template name="log.message">
                    <xsl:with-param name="level">Note</xsl:with-param>
                    <xsl:with-param name="source" select="$refname"/>
                    <xsl:with-param
                        name="message"
                        >meta manual : No ancestor with title</xsl:with-param>
                  </xsl:call-template>
                  <xsl:call-template name="log.message">
                    <xsl:with-param name="level">Note</xsl:with-param>
                    <xsl:with-param name="source" select="$refname"/>
                    <xsl:with-param
                        name="message"
                        >meta manual : No refmiscinfo@class=manual</xsl:with-param>
                  </xsl:call-template>
                </xsl:if>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:choose>
    <xsl:when test="not($Manual = '')">
      <xsl:copy-of select="$Manual"/>
    </xsl:when>
    <!-- * if no Manual, use contents of specified Fallback (if any) -->
    <xsl:when test="not($prefs/@fallback = '')">
      <xsl:variable name="manual.fallback">
        <xsl:call-template name="evaluate.info.profile">
          <xsl:with-param name="profile" select="$prefs/@fallback"/>
          <xsl:with-param name="info" select="$info"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="not($manual.fallback = '')">
          <xsl:value-of select="$manual.fallback"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="$refentry.meta.get.quietly = 0">
            <xsl:call-template name="log.message">
              <xsl:with-param name="level">Warn</xsl:with-param>
              <xsl:with-param name="source" select="$refname"/>
              <xsl:with-param
                  name="message"
                  >meta manual : No valid fallback. Leaving empty</xsl:with-param>
            </xsl:call-template>
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>

    </xsl:when>
    <xsl:otherwise>
      <xsl:if test="$refentry.meta.get.quietly = 0">
        <xsl:call-template name="log.message">
          <xsl:with-param name="level">Warn</xsl:with-param>
          <xsl:with-param name="source" select="$refname"/>
          <xsl:with-param
              name="message"
              >meta manual : No fallback specified; leaving empty.</xsl:with-param>
        </xsl:call-template>
      </xsl:if>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ====================================================================== -->

<doc:template name="get.refentry.metadata.prefs" xmlns="">
  <refpurpose>Gets user preferences for refentry metadata gathering</refpurpose>

  <refdescription>
    <para>The DocBook XSL stylesheets include several user-configurable
    global stylesheet parameters for controlling <sgmltag>refentry</sgmltag>
    metadata gathering. Those parameters are not read directly by the
    other <sgmltag>refentry</sgmltag> metadata-gathering functions. Instead, they
    are read only by the <function>get.refentry.metadata.prefs</function>
    function, which assembles them into a structure that is then passed to
    the other <sgmltag>refentry</sgmltag> metadata-gathering functions.</para>

    <para>So the, <function>get.refentry.metadata.prefs</function>
    function is the only interface to collecting stylesheet parameters for
    controlling <sgmltag>refentry</sgmltag> metadata gathering.</para>
  </refdescription>

  <refparameter>
    <para>There are no local parameters for this function; however, it
    does rely on a number of global parameters.</para>
  </refparameter>

  <refreturn><para>Returns a <sgmltag>manual</sgmltag> node.</para></refreturn>
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

<!-- ====================================================================== -->

<doc:template name="set.refentry.metadata" xmlns="">
  <refpurpose>Sets content of a refentry metadata item</refpurpose>

  <refdescription>
    <para>The <function>set.refentry.metadata</function> function is
    called each time a suitable source element is found for a certain
    metadata field.</para>
  </refdescription>

  <refparameter>
    <variablelist>
      <varlistentry>
        <term>refname</term>
        <listitem>
          <para>The first <sgmltag>refname</sgmltag> in the refentry</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>info</term>
        <listitem>
          <para>A single *info node that contains the selected source element.</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>contents</term>
        <listitem>
          <para>A node containing the selected source element.</para>
        </listitem>
      </varlistentry>
      <varlistentry>
        <term>context</term>
        <listitem>
          <para>A string describing the metadata context in which the
          <function>set.refentry.metadata</function> function was
          called: either "date", "source", "version", or "manual".</para>
        </listitem>
      </varlistentry>
    </variablelist>
  </refparameter>
  <refreturn>
  <para>Returns formatted contents of a selected source element.</para></refreturn>
</doc:template>

<xsl:template name="set.refentry.metadata">
  <xsl:param name="refname"/>
  <xsl:param name="info"/>
  <xsl:param name="contents"/>
  <xsl:param name="context"/>
  <xsl:param name="preferred"/>
  <xsl:if test="not($preferred = '')">
    <xsl:if test="$refentry.meta.get.quietly = 0">
      <xsl:call-template name="log.message">
        <xsl:with-param name="level">Note</xsl:with-param>
        <xsl:with-param name="source" select="$refname"/>
        <xsl:with-param
            name="message"
            >meta <xsl:value-of
            select="$context"/><xsl:call-template
            name="copy-string">
        <xsl:with-param name="string" select="'&#x20;'"/>
        <xsl:with-param
            name="count"
            select="7 - string-length($context)"/>
        </xsl:call-template>: No <xsl:value-of select="$preferred"
        /></xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="log.message">
        <xsl:with-param name="level">Note</xsl:with-param>
        <xsl:with-param name="source" select="$refname"/>
        <xsl:with-param
            name="message"
            >meta <xsl:value-of
            select="$context"/><xsl:call-template
            name="copy-string">
        <xsl:with-param name="string" select="'&#x20;'"/>
        <xsl:with-param
            name="count"
            select="7 - string-length($context)"/>
        </xsl:call-template>: No refmiscinfo@class=<xsl:value-of
        select="$context"/></xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="log.message">
        <xsl:with-param name="level">Note</xsl:with-param>
        <xsl:with-param name="source" select="$refname"/>
        <xsl:with-param
            name="message"
            >meta <xsl:value-of
            select="$context"/><xsl:call-template
            name="copy-string">
        <xsl:with-param name="string" select="'&#x20;'"/>
        <xsl:with-param
            name="count"
            select="7 - string-length($context)"/>
        </xsl:call-template>: Using <xsl:value-of
        select="local-name($contents)"/></xsl:with-param>
      </xsl:call-template>
    </xsl:if>
  </xsl:if>
  <xsl:value-of select="$contents"/>
</xsl:template>

</xsl:stylesheet>
