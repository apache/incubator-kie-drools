<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0"
                xmlns="http://www.w3.org/TR/xhtml1/transitional"
                exclude-result-prefixes="#default"
                xmlns:svg="http://www.w3.org/2000/svg" >

		<!-- identity transformation -->
		<xsl:template match="@* | node()">
		        <xsl:copy>
		                <xsl:apply-templates select="@* | node()"/>
		        </xsl:copy>
		</xsl:template>
		
		<!-- special element need special template - adding attributes -->
		<xsl:template match="svg:marker">
		        <xsl:copy>
		                <xsl:attribute name="overflow">visible</xsl:attribute>
		                <xsl:apply-templates select="@* | node()"/>
		        </xsl:copy>      
		</xsl:template>

</xsl:stylesheet>