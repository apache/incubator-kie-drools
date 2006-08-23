package com.nwalsh.saxon;

import java.util.Stack;
import org.xml.sax.*;
import org.w3c.dom.*;
import javax.xml.transform.TransformerException;
import com.icl.saxon.output.*;
import com.icl.saxon.om.*;
import com.icl.saxon.Controller;
import com.icl.saxon.tree.AttributeCollection;

/**
 * <p>Saxon extension to unwrap links in a result tree fragment.</p>
 *
 * <p>$Id: UnwrapLinksEmitter.java 1731 2002-06-26 11:03:05Z nwalsh $</p>
 *
 * <p>Copyright (C) 2000, 2002 Norman Walsh.</p>
 *
 * <p>This class provides the guts of a
 * <a href="http://saxon.sf.net/">Saxon 6.*</a>
 * implementation of a link unwrapper.</p>
 *
 * <p>The general design is this: the stylesheets construct a result tree
 * fragment for some environment. Then the result tree fragment
 * is "replayed" through the UnwrapLinksEmitter; the UnwrapLinksEmitter
 * builds a
 * new result tree fragment from this event stream with top-level links unwrapped.
 * That RTF is returned. Note that only a <i>single</i> level of unwrapping
 * is performed. This is clearly a crude implementation.
 * </p>
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
 * @version $Id: UnwrapLinksEmitter.java 1731 2002-06-26 11:03:05Z nwalsh $
 *
 */
public class UnwrapLinksEmitter extends CopyEmitter {
  /** A stack for the preserving information about open elements. */
  protected Stack elementStack = null;
  protected Stack saveStack = null;

  /** The FO namespace name. */
  protected static String foURI = "http://www.w3.org/1999/XSL/Format";

  /** The XHTML namespace name. */
  protected static String xhURI = "http://www.w3.org/1999/xhtml";

  /** Is the stylesheet currently running an FO stylesheet? */
  protected boolean foStylesheet = false;

  /** Are we currently in a link? How deep? */
  protected int linkDepth = 0;
  protected int skipDepth = 0;

  protected int htmlAFingerprint  = 0;
  protected int xhtmlAFingerprint = 0;
  protected boolean inSkip = false;
  protected boolean tryAgain = false;


  /** <p>Constructor for the UnwrapLinksEmitter.</p>
   *
   * @param namePool The name pool to use for constructing elements and attributes.
   * @param foStylesheet Is this an FO stylesheet?
   */
  public UnwrapLinksEmitter(Controller controller,
			    NamePool namePool,
			    boolean foStylesheet) {
    super(controller,namePool);
    elementStack = new Stack();
    this.foStylesheet = foStylesheet;

    htmlAFingerprint  = namePool.getFingerprint("", "a");
    xhtmlAFingerprint = namePool.getFingerprint(xhURI, "a");
  }

  /** Process start element events. */
  public void startElement(int nameCode,
			   org.xml.sax.Attributes attributes,
			   int[] namespaces,
			   int nscount)
    throws TransformerException {

    int thisFingerprint = namePool.getFingerprint(nameCode);
    boolean isLink = (thisFingerprint == htmlAFingerprint
		      || thisFingerprint == xhtmlAFingerprint);

    if (isLink) {
      linkDepth++;
      tryAgain = tryAgain || inSkip;
    }

    if (isLink && linkDepth > 1 && !inSkip) {
      inSkip = true;

      // Close all the open elements
      saveStack = new Stack();
      Stack tempStack = new Stack();
      while (!elementStack.empty()) {
	StartElementInfo elem = (StartElementInfo) elementStack.pop();
	rtfEmitter.endElement(elem.getNameCode());
	saveStack.push(elem);
	tempStack.push(elem);
      }

      while (!tempStack.empty()) {
	StartElementInfo elem = (StartElementInfo) tempStack.pop();
	elementStack.push(elem);
      }
    }

    if (inSkip) {
      skipDepth++;
    } else {
    }

    rtfEmitter.startElement(nameCode,attributes,namespaces,nscount);

    StartElementInfo sei = new StartElementInfo(nameCode, attributes,
						namespaces, nscount);
    elementStack.push(sei);
  }

  /** Process end element events. */
  public void endElement(int nameCode) throws TransformerException {
    int thisFingerprint   = namePool.getFingerprint(nameCode);
    boolean isLink = (thisFingerprint == htmlAFingerprint
		      || thisFingerprint == xhtmlAFingerprint);

    rtfEmitter.endElement(nameCode);
    elementStack.pop();

    if (isLink) {
      linkDepth--;
    }

    if (inSkip) {
      skipDepth--;
      inSkip = (skipDepth > 0);
      if (!inSkip) {
	// Reopen all the ones we closed before...
	while (!saveStack.empty()) {
	  StartElementInfo elem = (StartElementInfo) saveStack.pop();

	  AttributeCollection attr = (AttributeCollection)elem.getAttributes();
	  AttributeCollection newAttr = new AttributeCollection(namePool);

	  for (int acount = 0; acount < attr.getLength(); acount++) {
	    String localName = attr.getLocalName(acount);
	    String type = attr.getType(acount);
	    String value = attr.getValue(acount);
	    String uri = attr.getURI(acount);
	    String prefix = "";

	    if (localName.indexOf(':') > 0) {
	      prefix = localName.substring(0, localName.indexOf(':'));
	      localName = localName.substring(localName.indexOf(':')+1);
	    }

	    if (uri.equals("")
		&& ((foStylesheet
		     && localName.equals("id"))
		    || (!foStylesheet
			&& (localName.equals("id")
			    || localName.equals("name"))))) {
	      // skip this attribute
	    } else {
	      newAttr.addAttribute(prefix, uri, localName, type, value);
	    }
	  }

	  rtfEmitter.startElement(elem.getNameCode(),
				  newAttr,
				  elem.getNamespaces(),
				  elem.getNSCount());
	}
      }
    }
  }

  public boolean tryAgain()
    throws TransformerException {
    return tryAgain;
  }

  /**
   * <p>A private class for maintaining the information required to call
   * the startElement method.</p>
   *
   * <p>In order to close and reopen elements, information about those
   * elements has to be maintained. This class is just the little record
   * that we push on the stack to keep track of that info.</p>
   */
  private class StartElementInfo {
    private int _nameCode;
    org.xml.sax.Attributes _attributes;
    int[] _namespaces;
    int _nscount;

    public StartElementInfo(int nameCode,
			    org.xml.sax.Attributes attributes,
			    int[] namespaces,
			    int nscount) {
      _nameCode = nameCode;
      _attributes = attributes;
      _namespaces = namespaces;
      _nscount = nscount;
    }

    public int getNameCode() {
      return _nameCode;
    }

    public org.xml.sax.Attributes getAttributes() {
      return _attributes;
    }

    public int[] getNamespaces() {
      return _namespaces;
    }

    public int getNSCount() {
      return _nscount;
    }
  }
}
