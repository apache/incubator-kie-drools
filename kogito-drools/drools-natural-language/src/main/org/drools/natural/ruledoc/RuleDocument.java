package org.drools.natural.ruledoc;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.drools.natural.ruledoc.html.HTMLDocParser;

/**
 * This is the class that does it all for rule documents (HTML based).
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class RuleDocument
{

    public RuleDocument() {        
    }
    
    List buildRuleListFromDocument(URL document, Properties dictionary) {
        HTMLDocParser parser = new HTMLDocParser();
        RuleDocumentListener listener = new RuleDocumentListener();
        parser.parseDocument(document, listener);
        
        return listener.getRules();
        
    }
    
    
}
