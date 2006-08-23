/* This file was kindly provided by Sectra AB, Sweden to DocBook community */
package com.nwalsh.saxon;

import com.icl.saxon.charcode.PluggableCharacterSet;

/**
 *
 * $Id: Windows1252.java 4089 2004-12-06 04:24:20Z xmldoc $
 *
 * File:      Windows1252CharacterSet.java
 * Created:   May 26 2004
 * Author:    Pontus Haglund
 * Project:   Venus
 *
 * This class extends Saxon 6.5.x with the windows-1252 character set.
 *
 * It is particularly useful when  generating HTML Help for
 * Western European Languages.
 *
 * To use this class for generating HTML Help output with the
 * DocBook XSL stylesheets, complete the following steps;
 *              
 * 1. Make sure that the Saxon 6.5.x jar file and the jar file for
 *    the DocBook XSL Java extensions are in your CLASSPATH
 *
 * 2. Create a DocBook XSL customization layer -- a file named
 *    "mystylesheet.xsl" or whatever -- that, at a minimum,
 *    contains the following:
 * 
 *      <xsl:stylesheet
 *        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 *        version='1.0'>
 *        <xsl:import href="http://docbook.sourceforge.net/release/xsl/current/htmlhelp/htmlhelp.xsl"/>
 *        <xsl:output method="html" encoding="WINDOWS-1252" indent="no"/>
 *        <xsl:param name="htmlhelp.encoding" select="'WINDOWS-1252'"></xsl:param>
 *        <xsl:param name="chunker.output.encoding" select="'WINDOWS-1252'"></xsl:param>
 *        <xsl:param name="saxon.character.representation" select="'native'"></xsl:param>
 *      </xsl:stylesheet>
 *
 * 3. Invoke Saxon with the "encoding.windows-1252" Java system
 *    property set to "com.nwalsh.saxon.Windows1252"; for example:
 *
 *      java \
 *        -Dencoding.windows-1252=com.nwalsh.saxon.Windows1252 \
 *      com.icl.saxon.StyleSheet \
 *      mydoc.xml mystylesheet.xsl
 *
 *    Or, for a more complete "real world" case showing other
 *    options you'll typically want to use:
 *
 *      java \
 *        -Dencoding.windows-1252=com.nwalsh.saxon.Windows1252 \
 *        -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl \
 *        -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl \
 *        -Djavax.xml.transform.TransformerFactory=com.icl.saxon.TransformerFactoryImpl \
 *      com.icl.saxon.StyleSheet \
 *        -x org.apache.xml.resolver.tools.ResolvingXMLReader \
 *        -y org.apache.xml.resolver.tools.ResolvingXMLReader \
 *        -r org.apache.xml.resolver.tools.CatalogResolver \
 *      mydoc.xml mystylesheet.xsl
 *
 *   In both cases, the "mystylesheet.xsl" file should be a DocBook
 *   customization layer containing the parameters show in step 2.
 *
 */



public class Windows1252 implements PluggableCharacterSet {

    public final boolean inCharset(int c) {

    return  (c >= 0x00 && c <= 0x7F) ||
            (c >= 0xA0 && c <= 0xFF) ||
            (c == 0x20AC) ||
            (c == 0x201A) ||
            (c == 0x0192) ||
            (c == 0x201E) ||
            (c == 0x2026) ||
            (c == 0x2020) ||
            (c == 0x2021) ||
            (c == 0x02C6) ||
            (c == 0x2030) ||
            (c == 0x0160) ||
            (c == 0x2039) ||
            (c == 0x0152) ||
            (c == 0x017D) ||
            (c == 0x2018) ||
            (c == 0x2019) ||
            (c == 0x201C) ||
            (c == 0x201D) ||
            (c == 0x2022) ||
            (c == 0x2013) ||
            (c == 0x2014) ||
            (c == 0x02DC) ||
            (c == 0x2122) ||
            (c == 0x0161) ||
            (c == 0x203A) ||
            (c == 0x0153) ||
            (c == 0x017E) ||
            (c == 0x0178);


    }

    public String getEncodingName() {
        return "WINDOWS-1252";
    }

}
