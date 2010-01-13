package org.drools.verifier.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DrlRuleParser {
    private static final String             rulesExpr = "(^\\s*#.*?\\n)?(^\\s*rule.*?$)(.*?)(when)(.*?)(then)(.*?)(^\\s*end)";

    private static final Pattern            finder    = Pattern.compile( rulesExpr,
                                                                         Pattern.DOTALL | Pattern.MULTILINE );

    private final Collection<String>        header;
    private final Collection<String>        lhs;
    private final Collection<String>        rhs;

    private final String                    name;

    private final String                    description;

    private final Collection<String>        metadata;

    private final Map<String, List<String>> otherInformation;

    private DrlRuleParser(String ruleName,
                        String header,
                        String lhs,
                        String rhs,
                        String description,
                        List<String> metadata,
                        Map<String, List<String>> otherInformation) {
        this.name = ruleName;
        this.header = asList( header.split( "\n" ) );
        this.lhs = asList( lhs.split( "\n" ) );
        this.rhs = asList( rhs.split( "\n" ) );
        this.description = description;
        this.metadata = metadata;
        this.otherInformation = otherInformation;
    }

    private Collection<String> asList(String[] array) {
        Collection<String> list = new ArrayList<String>();

        for ( int i = 0; i < array.length; i++ ) {
            String string = array[i];
            if ( !string.trim().equals( "" ) ) {
                list.add( string );
            }
        }

        return list;
    }

    public static List<DrlRuleParser> findRulesDataFromDrl(String drl) {
        final Matcher m = finder.matcher( drl );
        final List<DrlRuleParser> list = new ArrayList<DrlRuleParser>();

        while ( m.find() ) {

            // System.out.println( "1 " + m.group( 1 ) );
            // System.out.println( "2 " + m.group( 2 ) );
            // System.out.println( "3 " + m.group( 3 ) );
            // System.out.println( "4 " + m.group( 4 ) );
            // System.out.println( "5 " + m.group( 5 ) );
            // System.out.println( "6 " + m.group( 6 ) );
            // System.out.println( "7 " + m.group( 7 ) );

            Comment comment = processComment( m.group( 1 ) );

            String ruleName = m.group( 2 );

            ruleName = trimRuleName( ruleName );

            list.add( new DrlRuleParser( ruleName,
                                       m.group( 3 ),
                                       m.group( 5 ),
                                       m.group( 7 ),
                                       comment.description,
                                       comment.metadata,
                                       new HashMap<String, List<String>>() ) );
        }

        return list;
    }

    private static String trimRuleName(String ruleName) {
        ruleName = ruleName.substring( ruleName.indexOf( "rule" ) + "rule".length() ).trim();

        if ( ruleName.indexOf( "\"" ) == 0 ) {
            ruleName = ruleName.substring( 1 );
        }

        if ( ruleName.lastIndexOf( "\"" ) == (ruleName.length() - 1) ) {
            ruleName = ruleName.substring( 0,
                                           ruleName.length() - 1 );
        }

        return ruleName;
    }

    static Comment processComment(String text) {
        Comment comment = new Comment();

        if ( text == null ) {
            comment.description = "";
            comment.metadata = new ArrayList<String>();

            return comment;
        }

        // Sometimes the first rule in a file gets the package imports and
        // comments included.
        // Checking for that and fixing the description if this did happen.

        StringBuilder description = new StringBuilder();
        String[] commentLines = text.split( "\n" );
        for ( int i = 0; i < commentLines.length; i++ ) {
            String line = commentLines[i].trim();

            if ( line.startsWith( "#" ) ) {
                while ( line.startsWith( "#" ) ) {
                    line = line.substring( 1 );
                }

                line = line.trim();

                if ( !line.startsWith( "@" ) ) {
                    description.append( line );
                    description.append( "\n" );
                }
            } else {
                description.delete( 0,
                                    description.length() );
            }

        }
        comment.description = description.toString();

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

    public Collection<String> getHeader() {
        return header;
    }

    public Collection<String> getLhs() {
        return lhs;
    }

    public Collection<String> getRhs() {
        return rhs;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Collection<String> getMetadata() {
        return metadata;
    }

    public Map<String, List<String>> getOtherInformation() {
        return otherInformation;
    }
}

class Comment {
    String       description;
    List<String> metadata;
}