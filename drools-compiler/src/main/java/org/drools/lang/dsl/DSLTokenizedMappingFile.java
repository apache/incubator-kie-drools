package org.drools.lang.dsl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

public class DSLTokenizedMappingFile extends DSLMappingFile {
	public DSLTokenizedMappingFile() {
        super();
    }
	
	public boolean parseAndLoad(final Reader dsl) throws IOException {
        String line = null;
        int linecounter = 0;
        final BufferedReader dslFileReader = new BufferedReader( dsl );
        this.mapping = new DefaultDSLMapping();
        this.errors = new LinkedList();
        //Note: Use a string builder for 1.5 targets
        StringBuffer sb = new StringBuffer();
        while ( (line = dslFileReader.readLine()) != null ) {
            linecounter++;
            String trimmedline = line.trim() + "\n"; //this can be more efficient, get rid of trim(), iterate-- over last chars only.
            try{
            	final DSLMappingEntry entry = buildEntry(trimmedline);
            	if(entry != null) this.mapping.addEntry(entry);//will be null if a comment
            }catch(Exception re){
            	final String error = "Error parsing mapping entry: " + line;
            	final DSLMappingParseException exception = 
            		new DSLMappingParseException( error, linecounter );
            	this.errors.add( exception );
            }
        }
        return this.errors.isEmpty();
    }
	
	private DSLMappingEntry buildEntry(String line) throws IOException, RecognitionException{
		StringReader sr = new StringReader(line);
		ANTLRReaderStream reader = new ANTLRReaderStream(sr);
		DSLMapWalker walker = buildEntryWalker(reader);
		DSLMappingEntry entry = walker.valid_entry();
		return entry;
	}
	
	private DSLMapWalker buildEntryWalker(CharStream stream) throws RecognitionException{
		DSLMapLexer lexer = new DSLMapLexer(stream);
		CommonTokenStream tokens = new CommonTokenStream();
		tokens.setTokenSource(lexer);
		DSLMapParser parser = new DSLMapParser(tokens);
		DSLMapParser.statement_return example = parser.statement();
		CommonTree tree = (CommonTree) example.getTree();
		//System.out.println(tree.toStringTree());
		
		CommonTreeNodeStream nodes = new CommonTreeNodeStream(tree);
		DSLMapWalker walker = new DSLMapWalker(nodes);
		return walker;
	}
}
