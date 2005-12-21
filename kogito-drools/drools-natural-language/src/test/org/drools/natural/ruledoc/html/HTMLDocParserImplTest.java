package org.drools.natural.ruledoc.html;

import java.io.InputStream;

import org.drools.natural.ruledoc.RuleDocumentListener;

import junit.framework.TestCase;

public class HTMLDocParserImplTest extends TestCase
{
    public void testDocParsing() throws Exception {
        
        MockRuleDocumentListenter listener = new MockRuleDocumentListenter();
        InputStream stream = this.getClass().getResourceAsStream("simplest.html");
        HTMLDocParserImpl parser = new HTMLDocParserImpl();
        parser.parseDocument(stream, listener);
        assertTrue(listener.buf.toString().indexOf("line B") > 0);
        assertFalse(listener.comment.toString().indexOf("comment") > 0);
        System.out.println(listener.buf.toString());
        assertEquals("comment", listener.comment.toString());
    }
    
    static class MockRuleDocumentListenter implements RuleDocumentListener {

        public StringBuffer comment = new StringBuffer();
        public StringBuffer buf = new StringBuffer();
        private boolean inComment = false;
        
        public void handleText(String text) 
        {
            if (!inComment) {
                buf.append(text + "|");
            } else {
                comment.append(text);
            }
        }

        public void startTable()
        {
        }

        public void startColumn()
        {
        }

        public void startRow()
        {
        }

        public void endTable()
        {
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
