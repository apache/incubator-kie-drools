package org.drools.decisiontable.model;

/*
 * Copyright 2005 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
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
