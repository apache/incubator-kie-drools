package org.drools.lang.dsl.template;

/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Experimental... seeing how far I can take dynamically generated regex... 
 * its a bit of a nightmare to escape everything though...
 * 
 * @author Michael
 *
 */
public class RegexTemplate {

    private Pattern templatePattern;
    private List    holes;
    private String  template;

    public static void main(final String[] args) {

        RegexTemplate regTemplate = new RegexTemplate( "the date between {before} and {after}" );
        regTemplate.compile();

        final String out = regTemplate.populate( "the date between date1 and date2",
                                           "dateBetween({before},{after})" );

        perfRegex( regTemplate,
                   out );

        regTemplate = new RegexTemplate( "date of '{date}'" );
        regTemplate.compile();

        System.out.println( regTemplate.populate( "date of 'today' and date of 'tomorrow'",
                                                  "dateOf({date})" ) );

        perfTemplate();

    }

    private static void perfRegex(final RegexTemplate regTemplate,
                                  final String out) {
        final long start = System.currentTimeMillis();
        for ( int i = 0; i < 100000; i++ ) {
            regTemplate.populate( "the date between date1 and date2",
                                  "dateBetween({before},{after})" );
        }
        System.out.println( "time for regex " + (System.currentTimeMillis() - start) );
        System.out.println( out );
    }

    private static void perfTemplate() {
        long start;
        final TemplateFactory factory = new TemplateFactory();
        final Template template = factory.getTemplate( "the date between {before} and {after}" );

        start = System.currentTimeMillis();
        for ( int i = 0; i < 100000; i++ ) {
            template.expandAll( "the date between date1 and date2",
                                "dateBetween({before},{after})" );
        }
        System.out.println( "time for non " + (System.currentTimeMillis() - start) );
    }

    List lex() {
        final ChunkLexer lex = new ChunkLexer();
        final List chunks = lex.lex( this.template );
        return chunks;

    }

    public String populate(final String source,
                           final String targetTemplate) {
        final Matcher matcher = this.templatePattern.matcher( source );
        if ( !matcher.matches() ) {
            return source;
        }

        String result = targetTemplate;
        if ( matcher.groupCount() != this.holes.size() ) {
            throw new IllegalArgumentException( "Unable to match up holes in template with source." );
        }

        for ( int i = 0; i < matcher.groupCount(); i++ ) {
            final String val = matcher.group( i + 1 );
            final String hole = (String) this.holes.get( i );
            result = replace( result,
                              hole,
                              val.trim() );// result.replace(hole, val);
        }
        return result;
    }

    public void compile() {
        final List chunks = lex();

        final StringBuffer regex = new StringBuffer();
        final List holes = new ArrayList();
        for ( final Iterator iter = chunks.iterator(); iter.hasNext(); ) {
            final String chunk = (String) iter.next();
            if ( chunk.startsWith( "{" ) ) {
                holes.add( chunk );
                regex.append( "\\b(.*)\\b" );
            } else {
                regex.append( replace( chunk,
                                       " ",
                                       "\\s" ) );//chunk.replace(" ", "\\s"));
            }
        }
        this.holes = holes;
        this.templatePattern = Pattern.compile( "\\s*" + regex.toString() + "\\s*" );
    }

    public RegexTemplate(final String grammarTemplate) {
        this.template = grammarTemplate;
    }

    private String replace(final String str,
                           final String find,
                           final String replace) {
//        return StringUtils.replace( str,
//                                    find,
//                                    replace );
        return null;
    }

    /**
     * Lex out chunks. 
     * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
     */
    static class ChunkLexer {

        private final List         chunks = new ArrayList();

        private StringBuffer buffer = new StringBuffer();

        public List lex(final String grammarTemplate) {

            final char[] chars = grammarTemplate.toCharArray();

            for ( int i = 0; i < chars.length; i++ ) {
                switch ( chars[i] ) {
                    case '{' :
                        startHole();
                        break;
                    case '}' :
                        endHole();
                        break;
                    default :
                        this.buffer.append( chars[i] );
                        break;
                }
            }
            final String buf = this.buffer.toString();
            if ( !buf.equals( "" ) ) {
                addChunk( buf );
            }
            return this.chunks;

        }

        private boolean addChunk(final String buf) {
            return this.chunks.add( buf.trim() );
        }

        private void endHole() {
            final String buf = this.buffer.toString();
            this.chunks.add( "{" + buf + "}" );
            this.buffer = new StringBuffer();
        }

        private void startHole() {
            final String buf = this.buffer.toString();
            if ( !buf.equals( "" ) ) {
                addChunk( buf );
            }
            this.buffer = new StringBuffer();
        }

    }
}