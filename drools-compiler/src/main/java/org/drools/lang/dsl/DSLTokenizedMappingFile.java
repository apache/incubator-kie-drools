package org.drools.lang.dsl;

import java.io.IOException;
import java.io.Reader;
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
        this.errors = new LinkedList();
		try{
			this.mapping = buildFileMapping(dsl);		
		}catch(Exception e){
			final String error = "Error parsing mapping file: " + e.getMessage();
        	final DSLMappingParseException exception = 
        		new DSLMappingParseException( error, -1 );
        	this.errors.add( exception );
		}
		return this.errors.isEmpty();
    }
	
	private DSLMapping buildFileMapping(Reader dsl) throws IOException, RecognitionException{
		ANTLRReaderStream reader = new ANTLRReaderStream(dsl);
		DSLMapWalker walker = buildFileMappingWalker(reader);
		DSLMapping mapping = walker.mapping_file();
		return mapping;
	}
	
	private DSLMapWalker buildFileMappingWalker(CharStream stream) throws RecognitionException{
		DSLMapLexer lexer = new DSLMapLexer(stream);
		CommonTokenStream tokens = new CommonTokenStream();
		tokens.setTokenSource(lexer);
		DSLMapParser parser = new DSLMapParser(tokens);
		DSLMapParser.mapping_file_return example = parser.mapping_file();
		this.errors.addAll(parser.getErrorList());
		CommonTree tree = (CommonTree) example.getTree();
//		System.out.println(tree.toStringTree());
		
		CommonTreeNodeStream nodes = new CommonTreeNodeStream(tree);
		DSLMapWalker walker = new DSLMapWalker(nodes);
		return walker;
	}
}
