package org.drools.decisiontable.model;
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
public class SnippetBuilder
{

    private static final String PARAM_PREFIX       = "$";

    private static final String PARAM              = PARAM_PREFIX + "param";    


    private String              _template;

    /**
     * @param snippetTemplate
     *            The snippet including the "place holder" for a parameter. If
     *            no "place holder" is present,
     */
    public SnippetBuilder(String snippetTemplate)
    {
        _template = snippetTemplate;
    }

    /**
     * @param cellValue
     *            The value from the cell to populate the snippet with. If no
     *            place holder exists, will just return the snippet.
     * @return The final snippet.
     */
    public String build(String cellValue)
    {
        if ( _template == null )
        {
            throw new RuntimeException( "Script template is null - check for missing script definition." );
        }
        
        if ( _template.indexOf( PARAM_PREFIX + "1" ) > 0 )
        {
            return buildMulti( cellValue );
        }
        else
        {
            return buildSingle( cellValue );
        }
    }



    private String buildMulti(String cellValue)
    {
        String[] cellVals = cellValue.split( "," );
        String result = _template;

        for ( int paramNumber = 0; paramNumber < cellVals.length; paramNumber++ )
        {
            String replace = PARAM_PREFIX + (paramNumber + 1);
            result = replace(result, replace, cellVals[paramNumber].trim( ), 256 );


        }
        return result;
    }

    /**
     * @param cellValue
     * @return
     */
    private String buildSingle(String cellValue)
    {

        return replace(_template, PARAM, cellValue, 256);

    }

    /**
     * Simple replacer. 
     * jakarta commons provided the inspiration for this.
     */
    private String replace(String text,
                                  String repl,
                                  String with,
                                  int max)
    {
        if ( text == null || repl == null || repl.equals( "" ) || with == null || max == 0 )
        {
            return text;
        }

        StringBuffer buf = new StringBuffer( text.length( ) );
        int start = 0, end = 0;
        while ( (end = text.indexOf( repl,
                                     start )) != -1 )
        {
            buf.append( text.substring( start,
                                        end ) ).append( with );
            start = end + repl.length( );

            if ( --max == 0 )
            {
                break;
            }
        }
        buf.append( text.substring( start ) );
        return buf.toString( );
    }

}