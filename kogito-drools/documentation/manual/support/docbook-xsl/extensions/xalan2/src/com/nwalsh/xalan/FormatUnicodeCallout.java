package com.nwalsh.xalan;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.SAXException;
import org.w3c.dom.*;
import org.apache.xml.utils.DOMBuilder;
import com.nwalsh.xalan.Callout;
import org.apache.xml.utils.AttList;

/**
 * <p>Utility class for the Verbatim extension (ignore this).</p>
 *
 * <p>$Id: FormatUnicodeCallout.java 3251 2003-12-17 01:01:34Z nwalsh $</p>
 *
 * <p>Copyright (C) 2000, 2001 Norman Walsh.</p>
 *
 * <p><b>Change Log:</b></p>
 * <dl>
 * <dt>1.0</dt>
 * <dd><p>Initial release.</p></dd>
 * </dl>
 *
 * @author Norman Walsh
 * <a href="mailto:ndw@nwalsh.com">ndw@nwalsh.com</a>
 *
 * @see Verbatim
 *
 * @version $Id: FormatUnicodeCallout.java 3251 2003-12-17 01:01:34Z nwalsh $
 **/

public class FormatUnicodeCallout extends FormatCallout {
  int unicodeMax = 0;
  int unicodeStart = 0;
  String unicodeFont = "";

  public FormatUnicodeCallout(String font, int start, int max, boolean fo) {
    unicodeFont = font;
    unicodeMax = max;
    unicodeStart = start;
    stylesheetFO = fo;
  }

  public void formatCallout(DOMBuilder rtf,
			    Callout callout) {
    Element area = callout.getArea();
    int num = callout.getCallout();
    String label = areaLabel(area);

    try {
      if (label == null && num <= unicodeMax) {
	AttributesImpl inAttr = new AttributesImpl();
	String ns = "";
	String prefix = "";
	String inName = "";

	if (!unicodeFont.equals("")) {
	  if (stylesheetFO) {
	    ns = foURI;
	    prefix = "fo:";
	    inName = "inline";
	    inAttr.addAttribute("", "", "font-family", "CDATA", unicodeFont);
	  } else {
	    inName = "font";
	    inAttr.addAttribute("", "", "face", "CDATA", unicodeFont);
	  }
	}

	char chars[] = new char[1];
	chars[0] = (char) (unicodeStart + num - 1);

	startSpan(rtf);
	if (!unicodeFont.equals("")) {
	  rtf.startElement(ns, inName, prefix+inName, inAttr);
	}
	rtf.characters(chars, 0, 1);
	if (!unicodeFont.equals("")) {
	  rtf.endElement(ns, inName, prefix+inName);
	}
	endSpan(rtf);
      } else {
	formatTextCallout(rtf, callout);
      }
    } catch (SAXException e) {
      System.out.println("SAX Exception in unicode formatCallout");
    }
  }
}
