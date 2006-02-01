package org.drools.lang.dsl.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Experimental... seeing how far I can take dynamically generated regex... 
 * its a bit of a nightmare to escape everything though...
 * 
 * @author Michael
 *
 */
public class RegexTemplate {

   
    private Pattern templatePattern;
    private List holes;
    private String template;
    
    public static void main(String[] args) {
        
        RegexTemplate regTemplate = new RegexTemplate("the date between {before} and {after}");
        regTemplate.compile();
        
        String out = regTemplate.populate("the date between date1 and date2", "dateBetween({before},{after})");
        
        perfRegex( regTemplate,
                   out );
        
        regTemplate = new RegexTemplate("date of '{date}'");
        regTemplate.compile();
        
        
        
        System.out.println(regTemplate.populate("date of 'today' and date of 'tomorrow'", "dateOf({date})"));
        
        
        perfTemplate();
        
        
        
    }


    private static void perfRegex(RegexTemplate regTemplate,
                                  String out) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            regTemplate.populate("the date between date1 and date2", "dateBetween({before},{after})");
        }
        System.out.println("time for regex " + (System.currentTimeMillis() - start));
        System.out.println(out);
    }


    private static void perfTemplate() {
        long start;
        TemplateFactory factory = new TemplateFactory();
        Template template = factory.getTemplate("the date between {before} and {after}");
        
        start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            template.expandAll("the date between date1 and date2", "dateBetween({before},{after})" );
        }
        System.out.println("time for non " + (System.currentTimeMillis() - start));
    }
  
    
    List lex() {
        ChunkLexer lex = new ChunkLexer();
        List chunks = lex.lex(template);
        return chunks;
      
    }
    

    public String populate(String source, String targetTemplate) {
        Matcher matcher = templatePattern.matcher(source);
        if (!matcher.matches()) return source;
        
        String result = targetTemplate;
        if (matcher.groupCount() != holes.size()) {
            throw new IllegalArgumentException("Unable to match up holes in template with source.");
        }
        
        for (int i = 0; i < matcher.groupCount(); i++ ) {
            String val = matcher.group(i + 1);
            String hole = (String) holes.get(i);
            result = replace(result, hole, val.trim());// result.replace(hole, val);
        }
        return result;
    }
    
    public void compile() {
        List chunks = lex();
        
        StringBuffer regex = new StringBuffer();
        List holes = new ArrayList();
        for ( Iterator iter = chunks.iterator(); iter.hasNext(); ) {
            String chunk = (String) iter.next();
            if (chunk.startsWith("{")) {
                holes.add(chunk);
                regex.append("\\b(.*)\\b");
            } else {
                regex.append(replace(chunk, " ", "\\s"));//chunk.replace(" ", "\\s"));
            }
        }
        this.holes = holes;
        this.templatePattern = Pattern.compile("\\s*" + regex.toString() + "\\s*");
    }
    
    public RegexTemplate(String grammarTemplate) {
        this.template = grammarTemplate;
    }
    
    private String replace(String str, String find, String replace) {
        return StringUtils.replace(str, find, replace);
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
