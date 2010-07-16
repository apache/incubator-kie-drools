/**
 * Copyright 2010 JBoss Inc
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

package org.drools.verifier.misc;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DrlPackageParser {
    private static final String             packageExpr  = "(^\\s*#.*?\\n)?(^\\s*package.*?$)(.*?)";
    private static final Pattern            finder       = Pattern.compile( packageExpr,
                                                                            Pattern.DOTALL | Pattern.MULTILINE );

    private static final Pattern            globalFinder = Pattern.compile( "(^\\s*global.*?$)",
                                                                            Pattern.DOTALL | Pattern.MULTILINE );

    private final String                    name;
    private final List<DrlRuleParser>       rules;
    private final String                    description;

    private final List<String>              metadata;

    private final Map<String, List<String>> otherInformation;
    private final List<String>              globals;

    public DrlPackageParser(String packageName,
                            String description,
                            List<DrlRuleParser> rules,
                            List<String> globals,
                            List<String> metadata,
                            Map<String, List<String>> otherInformation) {
        this.name = packageName;
        this.description = description;
        this.rules = rules;
        this.globals = globals;
        this.metadata = metadata;
        this.otherInformation = otherInformation;
    }

    public static DrlPackageParser findPackageDataFromDrl(String drl) throws ParseException {

        // Remove block comments
        int start = drl.indexOf( "/*" );
        while ( start > 0 ) {
            if ( start >= 0 ) {
                drl = drl.replace( drl.substring( start,
                                                  drl.indexOf( "*/",
                                                               start ) ),
                                   "" );

                start = drl.indexOf( "/*" );
            }
        }

        final Matcher m = finder.matcher( drl );
        m.find();

        String packageNameRow;
        try {
            packageNameRow = m.group( 2 );
        } catch ( IllegalStateException e ) {
            throw new ParseException( "DRL string contained no package data.",
                                      0 );
        }

        int indexOfPackage = packageNameRow.indexOf( "package" );
        String packageName = packageNameRow.substring( indexOfPackage + "package".length() ).trim();

        Comment comment = DrlRuleParser.processComment( m.group( 1 ) );

        return new DrlPackageParser( packageName,
                                     comment.description,
                                     DrlRuleParser.findRulesDataFromDrl( drl ),
                                     findGlobals( drl ),
                                     comment.metadata,
                                     new HashMap<String, List<String>>() );
    }

    public static List<String> findGlobals(String drl) {
        List<String> globals = new ArrayList<String>();
        Matcher gm = globalFinder.matcher( drl );

        while ( gm.find() ) {
            String row = gm.group();
            globals.add( row.substring( row.indexOf( "global" ) + "global".length() ).trim() );
        }

        return globals;
    }

    static Comment processComment(String text) {
        Comment comment = new Comment();

        if ( text == null ) {
            comment.description = "";
            comment.metadata = new ArrayList<String>();

            return comment;
        }

        comment.description = text.replaceAll( "#",
                                               "" ).trim();
        comment.metadata = findMetaData( text );

        return comment;
    }

    private static List<String> findMetaData(String text) {
        List<String> list = new ArrayList<String>();

        while ( text.contains( "@" ) ) {
            int start = text.indexOf( '@' );
            text = text.substring( start + 1 );
            int end = text.indexOf( "\n" );
            list.add( text.substring( 0,
                                      end ) );
            text = text.substring( end );
        }

        return list;
    }

    public String getName() {
        return name;
    }

    public List<DrlRuleParser> getRules() {
        return rules;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getMetadata() {
        return metadata;
    }

    public Map<String, List<String>> getOtherInformation() {
        return otherInformation;
    }

    public List<String> getGlobals() {
        return globals;
    }
}
