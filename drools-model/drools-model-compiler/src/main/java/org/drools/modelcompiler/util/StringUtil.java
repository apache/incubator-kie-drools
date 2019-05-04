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

package org.drools.modelcompiler.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtil {

    public static String toId(String id) {
        String fixStart = id;
        if ( !Character.isJavaIdentifierStart(fixStart.charAt(0)) ) {
            fixStart = "_" + fixStart;
        }
        fixStart = fixStart.replaceAll("_", "__");
        StringBuilder result = new StringBuilder();
        char[] cs = fixStart.toCharArray();
        for ( char c : cs ) {
            if ( Character.isJavaIdentifierPart(c) ) {
                result.append(c);
            } else {
                result.append("_" + Integer.valueOf(c));
            }
        }
        return result.toString();
    }

    public static String fileNameToClass(String fileName) {
        return fileName.substring( 0, fileName.length() - ".class".length() ).replace( '/', '.' );
    }

    public static String md5Hash(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            return bytesToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException( e );
        }
    }

    private static final char[] HEX_ARRAY = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
