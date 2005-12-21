package org.drools.natural.ruledoc.html;

import java.io.IOException;
import java.io.InputStream;

import org.drools.natural.ruledoc.RuleDocumentListener;

/** 
 * The HTML document parser will treat italics as comments.
 * Must be tolerant of dodgy HTML, and handle quirky HTML like the stuff
 * that Microsoft word spits out.
 * 
 * Any html parsers must implement this.
 * My suggested ones are:
 *  1) javax.swing 
 *  2) HotSax
 *  3) that one from SiteMesh
 *  4) roll your own.
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public interface HTMLDocParser
{
    /**
     * @param input The input to the HTML.
     * @param listener The listener to raise document events against.
     * @throws IOException If there is something wrong with the stream.
     */
    void parseDocument(InputStream input, RuleDocumentListener listener) throws IOException;
}
