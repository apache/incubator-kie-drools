/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package org.drools.compiler.runtime.pipeline.impl.castor;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.lang.model.SourceVersion;

/**
 * Originally copied from com.sun.xml.bind.api.impl.NameConverter.Standard (parts, not the full class)
 *
 * Included in order to make sure that the target namespace URI -> java package name translation
 * for XSD-generated classes remained the same
 * when we moved from XJC/The com.sun.* based XSD-based code generation to Castor codegen.
 */
public class XmlnsNameConverter {

    /**
     * Computes a Java package name from a namespace URI,
     * as specified in the spec.
     *
     * @return
     *      null if it fails to derive a package name.
     */
    public static String toPackageName( String nsUri ) {
        // remove scheme and :, if present
        // spec only requires us to remove 'http' and 'urn'...
        int idx = nsUri.indexOf(':');
        String scheme = "";
        if(idx>=0) {
            scheme = nsUri.substring(0,idx);
            if( scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("urn") )
                nsUri = nsUri.substring(idx+1);
        }

        // tokenize string
        ArrayList<String> tokens = tokenize( nsUri, "/: " );
        if( tokens.size() == 0 ) {
            return null;
        }

        // remove trailing file type, if necessary
        if( tokens.size() > 1 ) {
            // for uri's like "www.foo.com" and "foo.com", there is no trailing
            // file, so there's no need to look at the last '.' and substring
            // otherwise, we loose the "com" (which would be wrong)
            String lastToken = tokens.get( tokens.size()-1 );
            idx = lastToken.lastIndexOf( '.' );
            if( idx > 0 ) {
                lastToken = lastToken.substring( 0, idx );
                tokens.set( tokens.size()-1, lastToken );
            }
        }

        // tokenize domain name and reverse.  Also remove :port if it exists
        String domain = tokens.get( 0 );
        idx = domain.indexOf(':');
        if( idx >= 0) domain = domain.substring(0, idx);
        ArrayList<String> r = reverse( tokenize( domain, scheme.equals("urn")?".-":"." ) );
        if( r.get( r.size()-1 ).equalsIgnoreCase( "www" ) ) {
            // remove leading www
            r.remove( r.size()-1 );
        }

        // replace the domain name with tokenized items
        tokens.addAll( 1, r );
        tokens.remove( 0 );

        // iterate through the tokens and apply xml->java name algorithm
        for( int i = 0; i < tokens.size(); i++ ) {

            // get the token and remove illegal chars
            String token = tokens.get( i );
            token = removeIllegalIdentifierChars( token );

            // this will check for reserved keywords
            if (SourceVersion.isKeyword(token.toLowerCase())) {
                token = '_' + token;
            }

            tokens.set( i, token.toLowerCase() );
        }

        // concat all the pieces and return it
        return combine( tokens, '.' );
    }

    private static String removeIllegalIdentifierChars(String token) {
        StringBuilder newToken = new StringBuilder(token.length() + 1); // max expected length
        for( int i = 0; i < token.length(); i++ ) {
            char c = token.charAt( i );
            if (i == 0 && !Character.isJavaIdentifierStart(c)) { // c can't be used as FIRST char
                newToken.append('_');
            }
            if (!Character.isJavaIdentifierPart(c)) { // c can't be used
                newToken.append('_');
            } else {
                newToken.append(c); // c is valid
            }
        }
        return newToken.toString();
    }


    private static ArrayList<String> tokenize( String str, String sep ) {
        StringTokenizer tokens = new StringTokenizer(str,sep);
        ArrayList<String> r = new ArrayList<String>();

        while(tokens.hasMoreTokens())
            r.add( tokens.nextToken() );

        return r;
    }


    private static <T> ArrayList<T> reverse( List<T> a ) {
        ArrayList<T> r = new ArrayList<T>();

        for( int i=a.size()-1; i>=0; i-- )
            r.add( a.get(i) );

        return r;
    }

    private static String combine( List r, char sep ) {
        StringBuilder buf = new StringBuilder(r.get(0).toString());

        for( int i=1; i<r.size(); i++ ) {
            buf.append(sep);
            buf.append(r.get(i));
        }

        return buf.toString();
    }
}
