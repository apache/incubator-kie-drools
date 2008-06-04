tree grammar DSLMapWalker;

options {
	tokenVocab=DSLMap;
	ASTLabelType=CommonTree;
}

@treeparser::header {
	package org.drools.lang.dsl;
	
	import java.util.Map;
	import java.util.HashMap;
	
}


mapping_file returns [DSLMapping mapping]
scope {
	DSLMapping retval;
}
@init {
	$mapping_file::retval = new DefaultDSLMapping() ;
}
	: ^(VT_DSL_GRAMMAR entry*)
	{
		//System.out.println("done parsing file");
		//System.out.println($mapping_file::retval.dumpFile());
		$mapping = $mapping_file::retval;
		//java.io.StringWriter sw = new java.io.StringWriter();
		//$mapping_file::retval.saveMapping(sw);
		//System.out.println(sw.toString());
	}
	;
	
mapping_entry 
	: ent=entry
	{
		$mapping_file::retval.addEntry(ent);
		//System.out.println("mapping size is now " + $mapping_file::retval.getEntries().size());
	}
	;
	
valid_entry returns [DSLMappingEntry mappingEntry]
	: ent=entry {$mappingEntry = ent;}
	| VT_COMMENT {$mappingEntry = null;}
	;


entry returns [DSLMappingEntry mappingEntry]
scope {
	Map variables;
	AntlrDSLMappingEntry retval;
	int counter;
	StringBuffer keybuffer;
	StringBuffer valuebuffer;
}
@init {
	$entry::retval = new AntlrDSLMappingEntry() ;
	$entry::variables = new HashMap();
	$entry::keybuffer = new StringBuffer();
	$entry::valuebuffer = new StringBuffer();
}
	: ^(VT_ENTRY scope_section meta_section? key_section {$entry::retval.variables = $entry::variables; $entry::retval.setMappingKey($entry::keybuffer.toString());}
		value_section)
	{
		//System.out.println("for this entry, metadata is " + $entry::retval.getMetaData().getMetaData());
		//System.out.println("variables are " + $entry::variables);
		
		//System.out.println("keybuffer: " + $entry::keybuffer);
		//System.out.println("valuebuffer: " + $entry::valuebuffer);
//		$mapping_file::retval.addEntry($entry::retval);
//		System.out.println("mapping size is now " + $mapping_file::retval.getEntries().size());
		//$entry::retval.variables = $entry::variables;
		//$entry::retval.setMappingKey($entry::keybuffer.toString());
		$entry::retval.setMappingValue($entry::valuebuffer.toString());
		//System.out.println("keypattern is " + $entry::retval.getKeyPattern());
		//System.out.println("valuepattern is " + $entry::retval.getValuePattern());
		$mappingEntry = $entry::retval;
	}
	;


scope_section 
	: ^(thescope=VT_SCOPE condition_key? consequence_key? keyword_key? any_key?)
	;


	
meta_section
	: ^(VT_META metalit=LITERAL?)
	{
		if ( $metalit == null || $metalit.text == null || $metalit.text.length() == 0 ) {
			$entry::retval.setMetaData(DSLMappingEntry.EMPTY_METADATA);
		} else {
        		$entry::retval.setMetaData(new DSLMappingEntry.DefaultDSLEntryMetaData( $metalit.text ));
	        }
	}
	;

key_section
	: ^(VT_ENTRY_KEY key_sentence+ )
	{
		//$entry::retval.setMappingKey($entry::keybuffer.toString());
	}
	;
 
key_sentence
	: variable_definition
	| vtl=VT_LITERAL 
	{
		//System.out.println("in key_sentence, literal is " + $vtl.text);
		$entry::keybuffer.append($vtl.text);
	}
	| VT_SPACE
	{
		$entry::keybuffer.append("\\s+");
	}
	;		
/*
key_chunk
	: literal+
	;		
*/	
value_section
	: ^(VT_ENTRY_VAL value_sentence+ )
	{
		//$entry::retval.setMappingValue($entry::valuebuffer.toString());
	}
	;
	
value_sentence 	
	: variable_reference
	| vtl=VT_LITERAL
	{
		//System.out.println("in value_sentence, literal is " + $vtl.text);
		$entry::valuebuffer.append($vtl.text);
	}
	| VT_SPACE
	{
		$entry::valuebuffer.append(" ");
	}
	;	
/*	
value_chunk
	: (literal|EQUALS)+
	;	
*/	
literal 
	: theliteral=VT_LITERAL {//System.out.println("theliteral is " + $theliteral.text);}
	;	


variable_definition

	:   ^(VT_VAR_DEF varname=LITERAL pattern=VT_PATTERN? )
	{
		//System.out.println("variable " + $varname.text + " defined with pattern " + $pattern);
		$entry::counter++;
		$entry::variables.put($varname.text, new Integer($entry::counter));
		$entry::keybuffer.append($pattern != null? "(" + $pattern.text + ")" : "(.*?)");
	}
	;


variable_reference 
	: ^(varref=VT_VAR_REF lit=LITERAL ) 
	{
		//System.out.println("varref is " + $varref.text + " and points to " + $lit.text);
		$entry::valuebuffer.append("$" + $entry::variables.get($lit.text));
	}
	;	

condition_key
	: VT_CONDITION
	{$entry::retval.setSection(DSLMappingEntry.CONDITION);}
	;

consequence_key 
	: VT_CONSEQUENCE
	{$entry::retval.setSection(DSLMappingEntry.CONSEQUENCE);}
	;

keyword_key 
	: VT_KEYWORD
	{$entry::retval.setSection(DSLMappingEntry.KEYWORD);}
	;

any_key 
	: VT_ANY
	{$entry::retval.setSection(DSLMappingEntry.ANY);}
	;
