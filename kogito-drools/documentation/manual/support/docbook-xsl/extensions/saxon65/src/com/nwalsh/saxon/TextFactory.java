// TextFactory - Saxon extension element factory

package com.nwalsh.saxon;

import com.icl.saxon.style.ExtensionElementFactory;
import org.xml.sax.SAXException;

/**
 * <p>Saxon extension element factory
 *
 * <p>$Id: TextFactory.java 5907 2006-04-27 08:26:47Z xmldoc $</p>
 *
 * <p>Copyright (C) 2000 Norman Walsh.</p>
 *
 * <p>This class provides a
 * <a href="http://saxon.sourceforge.net/">Saxon</a>
 * extension element factory for the Text extension element
 * family.</p>
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
 * @version $Id: TextFactory.java 5907 2006-04-27 08:26:47Z xmldoc $
 *
 * @see Text
 *
 */
public class TextFactory implements ExtensionElementFactory {
  /**
   * <p>Constructor for TextFactory</p>
   *
   * <p>Does nothing.</p>
   */
  public TextFactory() {
  }

  /**
   * <p>Return the class that implements a particular extension element.</p>
   *
   * @param localname The local name of the extension element.
   *
   * @return The class that handles that extension element.
   *
   * @exception SAXException("Unknown Text extension element")
   */
  public Class getExtensionClass(String localname) {
    if (localname.equals("insertfile")) {
      try {
	return Class.forName("com.nwalsh.saxon.Text");
      } catch (ClassNotFoundException e) {
	return null;
      }
    }
    return null;
  }
}





