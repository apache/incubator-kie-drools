/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.template.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.drools.util.StringUtils.splitConstraints;

/**
 * This utility class exists to convert rule script snippets to actual code. The
 * snippets contain place holders for values to be substituted into. See the
 * test case for how it really works !
 * <p/>
 * Snippet template example: "something.getBlah($param)" $param is the "place
 * holder". This will get replaced with the "cellValue" that is passed in.
 * <p/>
 * 12-Oct-2005 change: moved from regex to using simple character based interpolation.
 * Regex was overkill and couldn't not quite get it right.
 */
public class SnippetBuilder {

    public enum SnippetType {
        SINGLE, PARAM, INDEXED, FORALL
    }

    public static final String PARAM_PREFIX = "$";
    public static final String PARAM_SUFFIX = "param";
    public static final String PARAM_STRING = PARAM_PREFIX + PARAM_SUFFIX;
    public static final String PARAM_FORALL_STRING = "forall";
    public static final Pattern PARAM_FORALL_PATTERN = Pattern
            .compile( PARAM_FORALL_STRING + "\\(([^{}]*)\\)\\{([^{}]+)\\}" );

    private final String _template;

    private final SnippetType type;

    private final boolean trim;

    /**
     * @param snippetTemplate The snippet including the "place holder" for a parameter. If
     * no "place holder" is present,
     */
    public SnippetBuilder( String snippetTemplate ) {
        this(snippetTemplate, true);
    }

    public SnippetBuilder( String snippetTemplate, boolean trim ) {
        if ( snippetTemplate == null ) {
            throw new RuntimeException( "Script template is null - check for missing script definition." );
        }
        this.trim = trim;
        this._template = snippetTemplate;
        this.type = getType( _template );
    }

    public static SnippetType getType( String template ) {
        Matcher forallMatcher = PARAM_FORALL_PATTERN.matcher( template );
        if ( forallMatcher.find() ) {
            return SnippetType.FORALL;
        } else if ( template.indexOf( PARAM_PREFIX + "1" ) != -1 ) {
            return SnippetType.INDEXED;
        } else if ( template.indexOf( PARAM_STRING ) != -1 ) {
            return SnippetType.PARAM;
        }
        return SnippetType.SINGLE;
    }

    /**
     * @param cellValue The value from the cell to populate the snippet with. If no
     * place holder exists, will just return the snippet.
     * @return The final snippet.
     */
    public String build( final String cellValue ) {
        switch ( type ) {
            case FORALL:
                return buildForAll( cellValue );
            case INDEXED:
                return buildMulti( cellValue );
            default:
                return buildSingle( cellValue );
        }
    }

    private String buildForAll( final String cellValue ) {
        final String[] cellVals = split( cellValue );
        Map<String, String> replacements = new HashMap<>();
        Matcher forallMatcher = PARAM_FORALL_PATTERN.matcher( _template );
        while ( forallMatcher.find() ) {
            replacements.put( forallMatcher.group(), "" );
            for ( int paramNumber = 0; paramNumber < cellVals.length; paramNumber++ ) {
                replacements.put( forallMatcher.group(), replacements
                        .get( forallMatcher.group() )
                        + ( paramNumber == 0 ? "" : " " + forallMatcher.group( 1 )
                        + " " )
                        + replace( forallMatcher.group( 2 ), PARAM_PREFIX,
                                   cellVals[ paramNumber ].trim(), 256 ) );
            }
        }
        String result = _template;
        for ( String key : replacements.keySet() ) {
            result = replace( result, key, replacements.get( key ), 256 );
        }
        return result.equals( "" ) ? _template : result;
    }

    private String buildMulti( final String cellValue ) {
        final String[] cellVals = split( cellValue );
        String result = this._template;

        //Replace in reverse order so $10 is replaced before $1 etc
        for ( int paramNumber = cellVals.length - 1; paramNumber >= 0; paramNumber-- ) {
            final String replace = PARAM_PREFIX + ( paramNumber + 1 );
            result = replace( result,
                              replace,
                              trim ? cellVals[ paramNumber ].trim() : cellVals[ paramNumber ],
                              256 );

        }
        return result;
    }

    private String[] split( String input ) {
        List<String> splitList = splitConstraints(input, false);
        return splitList.toArray(new String[splitList.size()]);
    }

    /**
     * @param cellValue
     * @return
     */
    private String buildSingle( final String cellValue ) {

        return replace( this._template,
                        PARAM_STRING,
                        cellValue,
                        256 );

    }

    /**
     * Simple replacer.
     * jakarta commons provided the inspiration for this.
     */
    private String replace( final String text,
                            final String repl,
                            final String with,
                            int max ) {
        if ( text == null || repl == null || repl.equals( "" ) || with == null || max == 0 ) {
            return text;
        }

        final StringBuilder buf = new StringBuilder( text.length() );
        int start = 0, end = 0;
        while ( ( end = text.indexOf( repl,
                                      start ) ) != -1 ) {
            buf.append( text.substring( start,
                                        end ) ).append( with.replace("\n", "\\n") );
            start = end + repl.length();

            if ( --max == 0 ) {
                break;
            }
        }
        buf.append( text.substring( start ) );
        return buf.toString();
    }

}
