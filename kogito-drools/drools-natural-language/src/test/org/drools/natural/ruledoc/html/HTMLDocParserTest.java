package org.drools.natural.ruledoc.html;

import java.net.URL;
import java.util.Properties;

import junit.framework.TestCase;

import org.drools.natural.ruledoc.RuleDocumentListener;

public class HTMLDocParserTest extends TestCase
{
    public void testDocParsing() throws Exception {
        
        MockRuleDocumentListenter listener = new MockRuleDocumentListenter();
        URL url = this.getClass().getResource("simplest.html");
        HTMLDocParser parser = new HTMLDocParser();
        parser.parseDocument(url.openConnection(), listener);
      
        assertTrue(listener.buf.toString().indexOf("A line") > 0);
        assertFalse(listener.comment.toString().indexOf("comment") > 0);
        
        assertEquals("comment", listener.comment.toString());
        
        assertTrue(listener.table.toString().indexOf("left1|right1") > 0);
    }
    
    static class MockRuleDocumentListenter extends RuleDocumentListener {

        public StringBuffer comment = new StringBuffer();
        public StringBuffer buf = new StringBuffer();
        private boolean inComment = false;
        public StringBuffer table = new StringBuffer();
        private boolean inTable = false;
        
        public void handleText(String text) 
        {
            if (inComment) {
                comment.append(text);
            } else if (inTable) {
                table.append(text);
            }           
            else {
                buf.append(text);
                
            }
        }

        public void startTable()
        {
            inTable = true;
        }

        public void startColumn()
        {
            this.table.append("|");
        }

        public void startRow()
        {
        }

        public void endTable()
        {
            this.table.append("||");
            inTable = false;
        }

        public void endColumn()
        {
        }

        public void endRow()
        {
        }

        public void startComment()
        {
            inComment  = true;
        }

        public void endComment()
        {
            inComment = false;
        }
        
    }
}
