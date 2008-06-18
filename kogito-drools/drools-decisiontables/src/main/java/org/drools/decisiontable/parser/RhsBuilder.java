package org.drools.decisiontable.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.template.model.SnippetBuilder;

/**
 * Builds up a consequence entry.
 * @author Michael Neale
 *
 */
public class RhsBuilder implements SourceBuilder {

    private Map<Integer, String> templates;
    private String  variable;
    private List<String> values;
    private boolean hasValues;

    /**
     * @param boundVariable Pass in a bound variable if there is one.
     * Any cells below then will be called as methods on it. 
     * Leaving it blank will make it work in "classic" mode.
     */
    public RhsBuilder(String boundVariable) {
        this.variable = boundVariable == null ? "" : boundVariable.trim();
        this.templates = new HashMap<Integer, String>();
        this.values = new ArrayList<String>();
    }

    public void addTemplate(int col,
                                       String content) {
        Integer key = new Integer( col );
        content = content.trim();
        if ( isBoundVar() ) {
            content = variable + "." + content + ";";
        }
        this.templates.put( key,
                            content );
    }

    private boolean isBoundVar() {
        return !("".equals( variable ));
    }

    public void addCellValue(int col,
                             String value) {
        hasValues = true;
        String template = (String) this.templates.get( new Integer( col ) );
        SnippetBuilder snip = new SnippetBuilder(template);
        
        this.values.add(snip.build( value ));

    }
    
    public void clearValues() {
        this.hasValues = false;
        this.values.clear();
    }
    
    public String getResult() {
        StringBuffer buf = new StringBuffer();
        for ( Iterator<String> iter = this.values.iterator(); iter.hasNext(); ) {            
            buf.append( iter.next() );
            if (iter.hasNext()) {
                buf.append( '\n' );
            }
        }
        return buf.toString();
    }

    public boolean hasValues() {
        
        return hasValues;
    }

}
