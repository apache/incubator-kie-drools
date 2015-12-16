/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.commons.backend.packages;

/**
 * Utility methods to parse a Package Name
 */
public final class PackageNameParser {

    private static final String KEYWORD = "package ";

    private PackageNameParser() {
    }

    public static String parsePackageName( final String content ) {
        String packageName = "";

        if ( content == null || content.trim().equals( "" ) ) {
            return packageName;
        } else {
            final String[] lines = content.split( "\\n" );

            for ( int i = 0; i < lines.length; i++ ) {
                String line = lines[ i ].trim();
                if ( !( line.equals( "" ) || line.startsWith( "#" ) ) ) {
                    if ( line.startsWith( KEYWORD ) ) {
                        line = line.substring( KEYWORD.length() ).trim();
                        if ( line.endsWith( ";" ) ) {
                            line = line.substring( 0, line.length() - 1 );
                        }
                        return line;
                    }
                }
            }

            return packageName;
        }

    }

}
