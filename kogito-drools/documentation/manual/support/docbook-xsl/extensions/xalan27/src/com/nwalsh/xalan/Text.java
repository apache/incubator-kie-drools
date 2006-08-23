// Text - Xalan extension element for inserting text

package com.nwalsh.xalan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.MalformedURLException;

import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;

import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPath;
import org.apache.xpath.NodeSet;
import org.apache.xalan.extensions.XSLProcessorContext;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.ElemExtensionCall;
import org.apache.xalan.templates.OutputProperties;
import org.apache.xalan.res.XSLTErrorResources;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.Source;

/**
 * <p>Xalan extension element for inserting text
 *
 * <p>$Id: Text.java 5972 2006-05-15 11:14:03Z nwalsh $</p>
 *
 * <p>Copyright (C) 2001 Norman Walsh.</p>
 *
 * <p>This class provides a
 * <a href="http://xml.apache.org/xalan-j/">Xalan</a>
 * extension element for inserting text into a result tree.</p>
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
 * @version $Id: Text.java 5972 2006-05-15 11:14:03Z nwalsh $
 *
 */
public class Text {
  /**
   * <p>Constructor for Text</p>
   *
   * <p>Does nothing.</p>
   */
  public Text() {
  }

  public String insertfile(XSLProcessorContext context,
			     ElemExtensionCall elem)
    throws MalformedURLException,
           FileNotFoundException,
           IOException,
	   TransformerException {
    String href = getFilename(context, elem);
    String encoding = getEncoding(context, elem);

    String baseURI = context.getTransformer().getBaseURLOfSource();
    URIResolver resolver = context.getTransformer().getURIResolver();

    if (resolver != null) {
      Source source = resolver.resolve(href, baseURI);
      href = source.getSystemId();
    }

    URL baseURL = null;
    URL fileURL = null;

    try {
      baseURL = new URL(baseURI);
    } catch (MalformedURLException e1) {
      try {
	baseURL = new URL("file:" + baseURI);
      } catch (MalformedURLException e2) {
	System.out.println("Cannot find base URI for " + baseURI);
	baseURL = null;
      }
    }

    String text = "";

    try {
      try {
        fileURL = new URL(baseURL, href);
      } catch (MalformedURLException e1) {
        try {
          fileURL = new URL(baseURL, "file:" + href);
        } catch (MalformedURLException e2) {
          System.out.println("Cannot open " + href);
          return "";
        }
      }

      InputStreamReader isr = null;
      if (encoding.equals("") == true)
        isr = new InputStreamReader(fileURL.openStream());
      else
        isr = new InputStreamReader(fileURL.openStream(), encoding);

      BufferedReader is = new BufferedReader(isr);

      final int BUFFER_SIZE = 4096;
      char chars[] = new char[BUFFER_SIZE];
      char nchars[] = new char[BUFFER_SIZE];
      int len = 0;
      int i = 0;
      int carry = -1;

      while ((len = is.read(chars)) > 0) {
        // various new lines are normalized to LF to prevent blank lines
	// between lines

        int nlen = 0;
        for (i=0; i<len; i++) {
          // is current char CR?
          if (chars[i] == '\r') {
            if (i < (len - 1)) {
              // skip it if next char is LF
              if (chars[i+1] == '\n') continue;
              // single CR -> LF to normalize MAC line endings
              nchars[nlen] = '\n';
              nlen++;
              continue;
            } else {
              // if CR is last char of buffer we must look ahead
              carry = is.read();
              nchars[nlen] = '\n';
              nlen++;
              if (carry == '\n') {
                carry = -1;
              }
              break;
            }
          }
          nchars[nlen] = chars[i];
          nlen++;
        }

	text += String.valueOf(nchars, 0, nlen);

        // handle look aheaded character
        if (carry != -1) text += String.valueOf((char)carry);
        carry = -1;
      }
      is.close();
    } catch (Exception e) {
      System.out.println("Cannot read " + href);
    }

    return text;
  }

  private String getFilename(XSLProcessorContext context, ElemExtensionCall elem)
    throws java.net.MalformedURLException,
	   java.io.FileNotFoundException,
	   java.io.IOException,
	   javax.xml.transform.TransformerException {

    String fileName;

    fileName = ((ElemExtensionCall)elem).getAttribute ("href",
						       context.getContextNode(),
						       context.getTransformer());

    if ("".equals(fileName)) {
      context.getTransformer().getMsgMgr().error(elem,
						 "No 'href' on text, or not a filename");
    }

    return fileName;
  }

  private String getEncoding(XSLProcessorContext context, ElemExtensionCall elem)
    throws java.net.MalformedURLException,
	   java.io.FileNotFoundException,
	   java.io.IOException,
	   javax.xml.transform.TransformerException {

    String encoding;

    encoding = ((ElemExtensionCall)elem).getAttribute ("encoding",
						       context.getContextNode(),
						       context.getTransformer());

    return encoding;
  }
}
