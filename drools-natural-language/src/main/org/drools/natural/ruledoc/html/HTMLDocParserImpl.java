package org.drools.natural.ruledoc.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.parser.ParserDelegator;


import org.drools.natural.ruledoc.RuleDocumentListener;

/**
 * This implementation of a doc parser uses the javax.swing package.
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class HTMLDocParserImpl extends HTMLEditorKit.ParserCallback implements HTMLDocParser
{

    private RuleDocumentListener listener;

    public void handleEndTag(Tag tag,
                             int arg1)
    {
        System.out.println("Breaksflow: " + tag.breaksFlow());
        
        if ( tag == Tag.I )
        {
            listener.endComment();
        }
        else if ( tag == Tag.TABLE )
        {
            listener.endTable();
        }
        else if ( tag == Tag.TH )
        {
            listener.endRow();
        }
        else if ( tag == Tag.TR )
        {
            listener.endRow();
        }
        else if ( tag == Tag.TD )
        {
            listener.endColumn();
        } else if (tag == Tag.BR) {
            System.out.println("BREAK");
        } else if (tag == Tag.P) {
            System.out.println("PARA");
        }
            
    }

    public void handleStartTag(Tag tag,
                               MutableAttributeSet arg1,
                               int arg2)
    {

        // System.out.println("Start TAG: " + name);
        if ( tag == Tag.I )
        {
            listener.startComment();
        }
        else if ( tag == Tag.TABLE )
        {
            listener.startTable();
        }
        else if ( tag == Tag.TH )
        {
            listener.startRow();            
        }
        else if ( tag == Tag.TR )
        {
            listener.startRow();
        }
        else if ( tag == Tag.TD )
        {
            listener.startColumn();
        }

    }

    public void handleText(char[] chars,
                           int arg1)
    {
        String s = new String(chars);
        
        listener.handleText(unescapeSmartQuotes(unescapeEntities(s)));
    }



    public void parseDocument(InputStream input,
                              RuleDocumentListener listener) throws IOException
    {
        this.listener = listener;
        ParserDelegator del = new ParserDelegator();
        Reader reader = new InputStreamReader(input);
        del.parse(reader, this, true);
    }
    
    private static String unescapeSmartQuotes(String s) {
        s = s.replace('\u201c', '"');
        s = s.replace('\u201d', '"');
        s = s.replace('\u2018', '\'');
        s = s.replace('\u2019', '\'');
        return s;
    }

    private static String unescapeEntities(String s) {
        s = s.replaceAll("&lt;", "<");
        s = s.replaceAll("&gt;", ">");
        s = s.replaceAll("&nbsp;", " ");
        s = s.replaceAll("&quot;", "\"");
        s = s.replaceAll("&amp;", "&");
        return s;
    }    

}
