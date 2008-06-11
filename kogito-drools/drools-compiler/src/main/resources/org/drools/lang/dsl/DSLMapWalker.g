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
	: ^(VT_DSL_GRAMMAR valid_entry*)
	{
		$mapping = $mapping_file::retval;
	}
	;

valid_entry returns [DSLMappingEntry mappingEntry]
	: ent=entry {$mappingEntry = ent; }
	| ^(VT_COMMENT lc=LINE_COMMENT) {$mappingEntry = null;}
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
		$entry::retval.setMappingValue($entry::valuebuffer.toString());
		$mappingEntry = $entry::retval;
		$mapping_file::retval.addEntry($mappingEntry);
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
	;
 
key_sentence
	: variable_definition
	| vtl=VT_LITERAL 
	{
		$entry::keybuffer.append($vtl.text);
	}
	| VT_SPACE
	{
		$entry::keybuffer.append("\\s+");
	}
	;		

value_section
@after{
	$entry::valuebuffer.append(" ");
}
	: ^(VT_ENTRY_VAL value_sentence+ )
	;
	
value_sentence 	
	: variable_reference
	| vtl=VT_LITERAL
	{
		$entry::valuebuffer.append($vtl.text.replaceAll("\\$", "\\\\\\$"));
	}
	| VT_SPACE
	{
		$entry::valuebuffer.append(" ");
	}
	;	

literal 
	: theliteral=VT_LITERAL 
	;	

variable_definition
	:   ^(VT_VAR_DEF varname=LITERAL pattern=VT_PATTERN? )
	{
		$entry::counter++;
		$entry::variables.put($varname.text, new Integer($entry::counter));
		$entry::keybuffer.append($pattern != null? "(" + $pattern.text + ")" : "(.*?)");
	}
	;


variable_reference 
	: ^(varref=VT_VAR_REF lit=LITERAL ) 
	{
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
