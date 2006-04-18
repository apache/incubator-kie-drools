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
    
    boolean isCommented() {
    	return (_comment != null && !("".equals(_comment)));
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