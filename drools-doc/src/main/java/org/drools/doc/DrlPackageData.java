package org.drools.doc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DrlPackageData {
    private static final String            packageExpr  = "(^\\s*#.*?\\n)?(^\\s*package.*?$)(.*?)";
    private static final Pattern           finder       = Pattern.compile( packageExpr,
                                                                           Pattern.DOTALL | Pattern.MULTILINE );

    private static final Pattern           globalFinder = Pattern.compile( "(^\\s*global.*?$)",
                                                                           Pattern.DOTALL | Pattern.MULTILINE );

    final String                           packageName;
    final List<DrlRuleData>                rules;
    final String                           description;

    public final List<String>              metadata;

    public final Map<String, List<String>> otherInformation;
    public final List<String>              globals;

    public DrlPackageData(String packageName,
                          String description,
                          List<DrlRuleData> rules,
                          List<String> globals,
                          List<String> metadata,
                          Map<String, List<String>> otherInformation) {
        this.packageName = packageName;
        this.description = description;
        this.rules = rules;
        this.globals = globals;
        this.metadata = metadata;
        this.otherInformation = otherInformation;
    }

    public static DrlPackageData findPackageDataFromDrl(String drl) {

        final Matcher m = finder.matcher( drl );
        m.find();

        String packageNameRow = m.group( 2 );

        int indexOfPackage = packageNameRow.indexOf( "package" );
        String packageName = packageNameRow.substring( indexOfPackage + "package".length() ).trim();

        Comment comment = DrlRuleData.processComment( m.group( 1 ) );

        return new DrlPackageData( packageName,
                                   comment.description,
                                   DrlRuleData.findRulesDataFromDrl( drl ),
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
}
