<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:param name="regex"/>
	<xsl:param name="replacement"/>

	<xsl:template match="/">
		<xsl:variable name="input" select="."/>
		<xsl:value-of select="replace($input, $regex, $replacement)"/>
	</xsl:template>
</xsl:stylesheet>