package org.drools.decisiontable.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale </a>
 * 
 * This utility class exists to convert rule script snippets to actual code. The
 * snippets contain place holders for values to be substituted into. See the
 * test case for how it really works !
 * 
 * Snippet template example: "something.getBlah($param)" $param is the "place
 * holder". This will get replaced with the "cellValue" that is passed in.
 * 
 * 12-Oct-2005 change: moved from regex to using simple character based interpolation.
 * Regex was overkill and couldn't not quite get it right.
 */
public class SnippetBuilder {

    private static final String PARAM_PREFIX = "$";

    private static final String PARAM        = SnippetBuilder.PARAM_PREFIX + "param";

    private final String        _template;
    
    private final boolean       single;
    
    private final Pattern       delimiter;

    /**
     * @param snippetTemplate
     *            The snippet including the "place holder" for a parameter. If
     *            no "place holder" is present,
     */
    public SnippetBuilder(final String snippetTemplate) {
        if ( snippetTemplate == null ) {
            throw new RuntimeException( "Script template is null - check for missing script definition." );
        }
        this._template = snippetTemplate;
        this.single = this._template.indexOf( SnippetBuilder.PARAM_PREFIX + "1" ) < 0;
        this.delimiter = Pattern.compile( "(.*?[^\\\\])(,|\\z)" );
    }

    /**
     * @param cellValue
     *            The value from the cell to populate the snippet with. If no
     *            place holder exists, will just return the snippet.
     * @return The final snippet.
     */
    public String build(final String cellValue) {
        if ( single ) {
            return buildSingle( cellValue );
        } else {
            return buildMulti( cellValue );
        }
    }

    private String buildMulti(final String cellValue) {
        final String[] cellVals = split( cellValue );
        String result = this._template;

        for ( int paramNumber = 0; paramNumber < cellVals.length; paramNumber++ ) {
            final String replace = SnippetBuilder.PARAM_PREFIX + (paramNumber + 1);
            result = replace( result,
                              replace,
                              cellVals[paramNumber].trim(),
                              256 );

        }
        return result;
    }
    
    private String[] split( String input ) {
        Matcher m = delimiter.matcher( input );
        List result = new ArrayList();
        while( m.find() ) {
            result.add( m.group( 1 ).replaceAll( "\\\\,", "," ) );
        }
        return (String[]) result.toArray( new String[result.size()] );
        
    }

    /**
     * @param cellValue
     * @return
     */
    private String buildSingle(final String cellValue) {

        return replace( this._template,
                        SnippetBuilder.PARAM,
                        cellValue,
                        256 );

    }

    /**
     * Simple replacer. 
     * jakarta commons provided the inspiration for this.
     */
    private String replace(final String text,
                           final String repl,
                           final String with,
                           int max) {
        if ( text == null || repl == null || repl.equals( "" ) || with == null || max == 0 ) {
            return text;
        }

        final StringBuffer buf = new StringBuffer( text.length() );
        int start = 0, end = 0;
        while ( (end = text.indexOf( repl,
                                     start )) != -1 ) {
            buf.append( text.substring( start,
                                        end ) ).append( with );
            start = end + repl.length();

            if ( --max == 0 ) {
                break;
            }
        }
        buf.append( text.substring( start ) );
        return buf.toString();
    }

}