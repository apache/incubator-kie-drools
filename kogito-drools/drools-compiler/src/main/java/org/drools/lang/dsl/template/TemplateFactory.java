package org.drools.lang.dsl.template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This takes a natural grammar template (the left hand side of a grammar mapping table)
 * and builds a TemplateContext for it.
 * 
 * Uses a built in lexer.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
class TemplateFactory {

    /**
     * This will lex the template string into chunks.
     * @param naturalTemplate From the grammar. eg "{0} likes cheese" is a template.
     * @return A template context ready to apply to a nl expression.
     */
    public Template getTemplate(String naturalTemplate) {
        
        Template template = new Template();
        
        List chunkList = lexChunks(naturalTemplate);
        for ( Iterator iter = chunkList.iterator(); iter.hasNext(); ) {
             template.addChunk((String) iter.next());
            
        }
        return template;
    }
    
    
    List lexChunks(String grammarTemplate) {
        ChunkLexer lexer = new ChunkLexer();
        return lexer.lex(grammarTemplate);
    }
    
    
    /**
     * Lex out chunks. 
     * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
     */
    static class ChunkLexer {
        
        private List chunks = new ArrayList();
        
        private StringBuffer buffer = new StringBuffer();
        
        public List lex(String grammarTemplate) {
            
            char[] chars = grammarTemplate.toCharArray();
            
            for ( int i = 0; i < chars.length; i++ ) {
                switch ( chars[i] ) {
                    case '{' :
                        startHole();
                        break;
                    case '}' :
                        endHole();
                        break;
                    default : 
                        buffer.append(chars[i]);
                        break;
                }
            }
            String buf = this.buffer.toString();
            if (!buf.equals("")) addChunk( buf );
            return this.chunks;
            
        }

        private boolean addChunk(String buf) {
            return this.chunks.add( buf.trim() );
        }

        private void endHole() {
            String buf = this.buffer.toString();
            chunks.add("{" + buf + "}");
            this.buffer = new StringBuffer();
        }

        private void startHole() {
            String buf = this.buffer.toString();
            if (!buf.equals("")) {
                addChunk( buf );
            }
            this.buffer = new StringBuffer();            
        }
        
        
    }
    
}
