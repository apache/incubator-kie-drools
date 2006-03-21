<?xml version="1.0"?>

<!DOCTYPE xsl:stylesheet [
    <!ENTITY db_xsl_path        "../../support/docbook-xsl/">
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0"
                xmlns="http://www.w3.org/TR/xhtml1/transitional"
                exclude-result-prefixes="#default">
                
    <xsl:import href="&db_xsl_path;/html/docbook.xsl"/>

    <xsl:param name="img.src.path">../shared/images/</xsl:param>
    <xsl:param name="keep.relative.image.uris">0</xsl:param>
    
    <xsl:param name="html.stylesheet">../shared/css/html.css</xsl:param>
    
</xsl:stylesheet>
