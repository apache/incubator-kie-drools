package org.drools.lang.dsl;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.ParserError;

public class DSLTokenizedMappingFile extends DSLMappingFile {

    public DSLTokenizedMappingFile() {
        super();
    }

    private static String nl = System.getProperty( "line.separator" );
    private static Pattern commentPat = Pattern.compile( "^\\s*((#/?|//).*)?$" );
    private static Pattern entryPat   = Pattern.compile( "^\\s*\\[.+$" );

    // Original line lengths, for fixing error messages.
    private List<Integer> lineLengths;
    // Option keywords, i.e., anything on a #/ line.
    private Set<String> optionSet = new HashSet<String>();

    /**
     * Read a DSL file and convert it to a String. Comment lines are removed.
     * Split lines are joined, inserting a space for an EOL, but maintaining the
     * original number of lines by inserting EOLs. Options are recognized.
     * Keeps track of original line lengths for fixing parser error messages.
     * @param reader for the DSL file data
     * @return the transformed DSL file
     * @throws IOException
     */
    private String readFile( Reader reader ) throws IOException {
        lineLengths = new ArrayList<Integer>();
        lineLengths.add( null );
        LineNumberReader lnr = new LineNumberReader( reader );
        StringBuilder sb = new StringBuilder();
        int nlCount = 0;
        boolean inEntry = false;
        String line;

        while( (line = lnr.readLine()) != null ){
            lineLengths.add( line.length() );
            Matcher commentMat = commentPat.matcher( line );
            if( commentMat.matches() ){
                if( inEntry ){
                    nlCount++;
                } else {
                    sb.append( '\n' );
                }
                if( "#/".equals( commentMat.group( 2 ) ) ){
                    String[] options = commentMat.group( 1 ).substring( 2 ).trim().split( "\\s+" );
                    for( String option: options ){
                        optionSet.add( option );
                    }
                }
                continue;
            }
            if( entryPat.matcher( line ).matches() ){
                if( inEntry ){
                    for( int i = 0; i < nlCount; i++ ) sb.append( '\n' );
                }
                sb.append( line );
                nlCount = 1;
                inEntry = true;
                continue;
            }
            sb.append( ' ').append( line );
            nlCount++;
        }
        if( inEntry ) sb.append( '\n' );

        lnr.close();      
//        System.out.println( "====== DSL definition:" );
//        System.out.println( sb.toString() );

        return sb.toString();
    }

    @Override
    public boolean parseAndLoad(Reader dsl) throws IOException {
        List<ParserError> errors = new ArrayList<ParserError>();
        String text = readFile( dsl );
        dsl = new StringReader( text );

        try  {
            DSLMapping mapping = buildFileMapping(errors, dsl);
            mapping.setOptions( optionSet );
            setMapping( mapping );
            List<ParserError> moderr = new ArrayList<ParserError>();
            for( ParserError err: errors ){
                int row = err.getRow();
                int col = err.getCol();
                if( row > 0 ){
                    int len;
                    while( (len = lineLengths.get( row )) < col ){
                        col -= len + 1;
                        row++;
                    }
                }
                moderr.add( new ParserError( err.getMessage(), row, col ) );
            }
            errors = moderr;
        } catch(Exception e){
            final String msg = "Error parsing DSL mapping: " + e.getMessage();
            ParserError parserError = new ParserError( msg, -1, 0 );
            errors.add( parserError );
        }
        setErrors( errors );

        //        for( ParserError err: errors ){
        //            System.err.println( "[" + err.getRow() + "," + err.getCol() + "]: " + err.getMessage() );
        //        }

        return errors.isEmpty();
    }	

    private DSLMapping buildFileMapping(final List<ParserError> errors, final Reader dsl) throws IOException, RecognitionException{
        ANTLRReaderStream reader = new ANTLRReaderStream(dsl);
        DSLMapWalker walker = buildFileMappingWalker(errors, reader);
        DSLMapping mapping = walker.mapping_file();
        return mapping;
    }

    private DSLMapWalker buildFileMappingWalker(final List<ParserError> errors, CharStream stream) throws RecognitionException{
        DSLMapLexer lexer = new DSLMapLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream();
        tokens.setTokenSource(lexer);
        DSLMapParser parser = new DSLMapParser(tokens);
        DSLMapParser.mapping_file_return example = parser.mapping_file();
        CommonTree tree = (CommonTree) example.getTree();
        //        System.out.println(tree.toStringTree());

        CommonTreeNodeStream nodes = new CommonTreeNodeStream(tree);
        DSLMapWalker walker = new DSLMapWalker(nodes);

        errors.addAll( lexer.getErrors() );
        errors.addAll( parser.getErrors() );
        return walker;
    }
}
