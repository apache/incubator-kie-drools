package org.drools.natural.lexer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * For interpolating Perl style variables in a string. Eg: "this is a ${left}
 * variable string".
 * 
 * Groovy can do it, Ruby can do it with "#{left}" style, but I couldn't find
 * anything suitable for java 1.4. Feel free to adapt this to use a library
 * should one be available.
 * 
 * Additionally, a list of variable names must be extracted, which most
 * interpolation utilities could not do.
 * 
 * "A person has a problem doing something with strings. She decides to use
 * regex. Now she has two problems." -- Me after marathon week debugging
 * a 10k line perl script.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class StringInterpolator
{
    private String originalString;

    public StringInterpolator(String original)
    {
        this.originalString = original;
    }

    /**
     * 
     * @param str
     * @return A set containing the names of variables found.
     */
    public List extractVariableNames()
    {
        boolean inVariable = false;
        char[] chars = originalString.toCharArray( );

        List result = new ArrayList( );
        StringBuffer varName = new StringBuffer( );
        for ( int i = 0; i < chars.length; i++ )
        {
            switch ( chars[i] )
            {
            case '$' :
                if ( nextCharIsBracket( chars,
                                        i ) )
                {
                    inVariable = true;
                    i++;
                    varName = new StringBuffer( );
                }
                break;
            case '}' :
                if ( inVariable )
                {
                    inVariable = false;
                    String var = varName.toString( );
                    if (!result.contains(var)) {
                        result.add( varName.toString( ) );
                    }
                }

                break;
            default :
                if ( inVariable )
                {
                    varName.append( chars[i] );
                }
                break;
            }

        }
        return result;

    }

    boolean nextCharIsBracket(char[] chars,
                              int i)
    {
        if ( chars.length == i + 1 )
        {
            return false;
        }
        else
        {
            return chars[i + 1] == '{';
        }
    }

    /**
     * Interpolate the variables into the String.  
     * The values obviously go into the props parameter,
     * but you probably could have guessed that.
     */
    public String interpolate(Properties props)
    {
        Iterator itr = props.keySet( ).iterator( );
        String str = originalString;
        while ( itr.hasNext( ) )
        {
            String key = (String) itr.next( );
            str = replace( str,
                           "${" + key + "}",
                           props.getProperty( key ),
                           -1 );
        }
        return str;
    }

    private String replace(String text,
                                  String repl,
                                  String with,
                                  int max)
    {
        return StringUtils.replace(text, repl, with, max);
//        if ( text == null || repl == null || repl.equals( "" ) || with == null || max == 0 )
//        {
//            return text;
//        }
//
//        StringBuffer buf = new StringBuffer( text.length( ) );
//        int start = 0, end = 0;
//        while ( (end = text.indexOf( repl,
//                                     start )) != -1 )
//        {
//            buf.append( text.substring( start,
//                                        end ) ).append( with );
//            start = end + repl.length( );
//
//            if ( --max == 0 )
//            {
//                break;
//            }
//        }
//        buf.append( text.substring( start ) );
//        return buf.toString( );
    }

}
