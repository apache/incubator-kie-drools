<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:ng="http://docbook.org/docbook-ng"
		xmlns:db="http://docbook.org/ns/docbook"
                xmlns:exsl="http://exslt.org/common"
                exclude-result-prefixes="db ng exsl"
                version='1.0'>

<xsl:import href="docbook.xsl"/>

<xsl:output method="xml" encoding="utf-8" indent="no"/>

<xsl:template match="/">
  <xsl:choose>
    <xsl:when test="function-available('exsl:node-set')
		    and (*/self::ng:* or */self::db:*)">
      <!-- Hack! If someone hands us a DocBook V5.x or DocBook NG document,
	   toss the namespace and continue. Someday we'll reverse this logic
	   and add the namespace to documents that don't have one.
	   But not before the whole stylesheet has been converted to use
	   namespaces. i.e., don't hold your breath -->
      <xsl:message>Stripping NS from DocBook 5/NG document.</xsl:message>
      <xsl:apply-templates mode="stripNS"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
	<xsl:text>Cannot strip without exsl:node-set.</xsl:text>
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
