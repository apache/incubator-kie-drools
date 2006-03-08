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



import java.text.StringCharacterIterator;

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale </a>
 * 
 * The LayerSupertype for this model/parse tree.
 */
public abstract class DRLElement
{

    private String _comment;

    public void setComment(String comment)
    {
        _comment = comment;
    }

    String getComment()
    {
        return _comment;
    }

    /**
     * This escapes plain snippets into an xml/drl safe format.
     * 
     * @param snippet
     * @return An escaped DRL safe string.
     */
    static String escapeSnippet(String snippet)
    {
        if ( snippet != null )
        {
            final StringBuffer result = new StringBuffer( );

            final StringCharacterIterator iterator = new StringCharacterIterator( snippet );
            char character = iterator.current( );
            while ( character != StringCharacterIterator.DONE )
            {
                if ( character == '<' )
                {
                    result.append( "&lt;" );
                }
                else if ( character == '>' )
                {
                    result.append( "&gt;" );
                } // What else really needs to be escaped? SAX parsers are
                // inconsistent here...
                /*
                 * else if (character == '\"') { result.append("&quot;"); } else
                 * if (character == '\'') { result.append("&#039;"); } else if
                 * (character == '\\') { result.append("&#092;"); }
                 */
                else if ( character == '&' )
                {
                    result.append( "&amp;" );
                }
                else
                {
                    // the char is not a special one
                    // add it to the result as is
                    result.append( character );
                }
                character = iterator.next( );
            }
            return result.toString( );
        }
        return null;
    }

}

