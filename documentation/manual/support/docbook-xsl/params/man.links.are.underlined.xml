<refentry xmlns="http://docbook.org/ns/docbook"
          xmlns:xlink="http://www.w3.org/1999/xlink"
          xmlns:xi="http://www.w3.org/2001/XInclude"
          xmlns:src="http://nwalsh.com/xmlns/litprog/fragment"
          xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
          version="5.0" xml:id="man.links.are.underlined">
<refmeta>
<refentrytitle>man.links.are.underlined</refentrytitle>
<refmiscinfo class="other" otherclass="datatype">boolean</refmiscinfo>
</refmeta>
<refnamediv>
<refname>man.links.are.underlined</refname>
<refpurpose>Underline links?</refpurpose>
</refnamediv>

<refsynopsisdiv>
<src:fragment xml:id="man.links.are.underlined.frag">
<xsl:param name="man.links.are.underlined">1</xsl:param>
</src:fragment>
</refsynopsisdiv>

<refsection><info><title>Description</title></info>

<para>If the value of <parameter>man.links.are.underlined</parameter>
is non-zero (the default), then the contents of links are rendered
with an underline.</para>

<para>If the value of <parameter>man.links.are.underlined</parameter>
is zero, links are displayed without any underlining.</para>

<note>
  <para>Currently, this parameter only affects output for
  <tag>ulink</tag>s.</para>
</note>

<para>If you set <parameter>man.links.are.numbered</parameter> and/or
<parameter>man.links.list.enabled</parameter> to zero (disabled), then
you should probably also set
<parameter>man.links.are.underlined</parameter> to zero. But if
<parameter>man.links.are.numbered</parameter> is non-zero (enabled),
you should probably set a non-zero value for
<parameter>man.links.are.underlined</parameter> also<footnote><para>If the main purpose of underlining of links in most output
formats it to indicate that the underlined text is
“clickable”, given that links rendered in man pages are
not “real” hyperlinks that users can click on, it might
seem like there is never a good reason to have link contents
underlined in man output.</para> <para>In fact, if you suppress the
display of inline link references (by setting
<parameter>man.links.are.numbered</parameter> to zero), there is no
good reason to have links underlined. However, if
<parameter>man.links.are.numbered</parameter> is non-zero, having
links underlined may (arguably) serve a purpose: It provides
“context” information about exactly what part of the text
is being “annotated” by the link. Depending on how you
mark up your content, that context information may or may not
have value.</para></footnote>.</para>
</refsection>
</refentry>
