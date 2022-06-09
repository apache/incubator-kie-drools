<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output indent="yes" method="xml" encoding="UTF-8"/>

  <!-- Copy element's local name (ignoring namespace) and apply templates on its content and attributes. -->
  <xsl:template match="*">
    <xsl:element name="{local-name()}">
      <xsl:apply-templates select="@* | node()"/>
    </xsl:element>
  </xsl:template>

  <!-- Copy attribute's local name (ignoring namespace) and value. -->
  <xsl:template match="@*">
    <xsl:attribute name="{local-name()}">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>

  <!-- Copy everything else. -->
  <xsl:template match="comment() | text() | processing-instruction()">
    <xsl:copy/>
  </xsl:template>

</xsl:stylesheet>
