// UnwrapLinks.java - Saxon extension for unwrapping nested links

package com.nwalsh.saxon;

import java.util.Stack;
import java.util.StringTokenizer;
import org.xml.sax.*;
import org.w3c.dom.*;
import javax.xml.transform.TransformerException;
import com.icl.saxon.Controller;
import com.icl.saxon.expr.*;
import com.icl.saxon.om.*;
import com.icl.saxon.pattern.*;
import com.icl.saxon.Context;
import com.icl.saxon.tree.*;
import com.icl.saxon.functions.Extensions;
import com.nwalsh.saxon.UnwrapLinksEmitter;

/**
 * <p>Saxon extension for unwrapping nested links</p>
 *
 * <p>$Id: UnwrapLinks.java 5907 2006-04-27 08:26:47Z xmldoc $</p>
 *
 * <p>Copyright (C) 2000, 2002 Norman Walsh.</p>
 *
 * <p>This class provides a
 * <a href="http://saxon.sf.net/">Saxon 6.*</a>
 * implementation of a link unwrapper.</p>
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
 * @version $Id: UnwrapLinks.java 5907 2006-04-27 08:26:47Z xmldoc $
 *
 */
public class UnwrapLinks {
  /** True if the stylesheet is producing formatting objects */
  private static boolean foStylesheet = false;

  /**
   * <p>Constructor for UnwrapLinks</p>
   *
   * <p>All of the methods are static, so the constructor does nothing.</p>
   */
  public UnwrapLinks() {
  }

  /**
   * <p>Find the string value of a stylesheet variable or parameter</p>
   *
   * <p>Returns the string value of <code>varName</code> in the current
   * <code>context</code>. Returns the empty string if the variable is
   * not defined.</p>
   *
   * @param context The current stylesheet context
   * @param varName The name of the variable (without the dollar sign)
   *
   * @return The string value of the variable
   */
  protected static String getVariable(Context context, String varName) {
    Value variable = null;
    String varString = null;

    try {
      variable = Extensions.evaluate(context, "$" + varName);
      varString = variable.asString();
      return varString;
    } catch (TransformerException te) {
      System.out.println("Undefined variable: " + varName);
      return "";
    } catch (IllegalArgumentException iae) {
      System.out.println("Undefined variable: " + varName);
      return "";
    }
  }

  /**
   * <p>Setup the parameters associated with unwrapping links</p>
   *
   * @param context The current stylesheet context
   *
   */
  private static void setupUnwrapLinks(Context context) {
    // Get the stylesheet type
    String varString = getVariable(context, "stylesheet.result.type");
    foStylesheet = (varString.equals("fo"));
  }

  /**
   * <p>Unwrap links</p>
   *
   * @param context The current stylesheet context.
   * @param rtf_ns The result tree fragment of the verbatim environment.
   *
   * @return The modified result tree fragment.
   */
  public static NodeSetValue unwrapLinks (Context context,
					  NodeSetValue rtf_ns) {

    FragmentValue rtf = (FragmentValue) rtf_ns;
    boolean tryAgain = true;

    setupUnwrapLinks(context);

    try {
      Controller controller = context.getController();
      NamePool namePool = controller.getNamePool();

      while (tryAgain) {
	UnwrapLinksEmitter ulEmitter = new UnwrapLinksEmitter(controller,
							      namePool,
							      foStylesheet);
	rtf.replay(ulEmitter);
	tryAgain = ulEmitter.tryAgain();
	rtf = (FragmentValue) ulEmitter.getResultTreeFragment();
      }

      return rtf;

    } catch (TransformerException e) {
      // This "can't" happen.
      System.out.println("Transformer Exception in unwrapLinks");
      return rtf;
    }
  }
}
