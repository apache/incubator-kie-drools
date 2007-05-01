<refentry xmlns="http://docbook.org/ns/docbook"
          xmlns:xlink="http://www.w3.org/1999/xlink"
          xmlns:xi="http://www.w3.org/2001/XInclude"
          xmlns:src="http://nwalsh.com/xmlns/litprog/fragment"
          xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
          version="5.0" xml:id="generate.legalnotice.link">
<refmeta>
<refentrytitle>generate.legalnotice.link</refentrytitle>
<refmiscinfo class="other" otherclass="datatype">boolean</refmiscinfo>
</refmeta>
<refnamediv>
<refname>generate.legalnotice.link</refname>
<refpurpose>Write legalnotice to separate chunk and generate link?</refpurpose>
</refnamediv>

<refsynopsisdiv>
<src:fragment xml:id="generate.legalnotice.link.frag"><xsl:param name="generate.legalnotice.link" select="0"/></src:fragment>
</refsynopsisdiv>

<refsection><info><title>Description</title></info>

<para>If the value of <parameter>generate.legalnotice.link</parameter>
is non-zero, the stylesheet:

<itemizedlist>
  <listitem>
    <para>writes the contents of <tag>legalnotice</tag> to a separate
    HTML file</para>
  </listitem>
  <listitem>
    <para>inserts a hyperlink to the <tag>legalnotice</tag> file</para>
  </listitem>
  <listitem>
    <para>adds (in the HTML <literal>head</literal>) either a single
    <literal>link</literal> or element or multiple
    <literal>link</literal> elements (depending on the value of the
    <parameter>html.head.legalnotice.link.multiple</parameter>
    parameter), with the value or values derived from the
    <parameter>html.head.legalnotice.link.types</parameter>
    parameter</para>
  </listitem>
  </itemizedlist>

  Otherwise, if <parameter>generate.legalnotice.link</parameter> is
  zero, <tag>legalnotice</tag> contents are rendered on the title
  page.</para>

</refsection>
</refentry>
