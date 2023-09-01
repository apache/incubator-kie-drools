package org.drools.drl.ast.util;

import static java.lang.Character.isWhitespace;

public class AstUtil {
    public static boolean isEmpty(final CharSequence str) {
        if ( str == null || str.length() == 0 ) {
            return true;
        }

        for ( int i = 0, length = str.length(); i < length; i++ ){
            if ( !isWhitespace(str.charAt( i )) )  {
                return false;
            }
        }

        return true;
    }
}
