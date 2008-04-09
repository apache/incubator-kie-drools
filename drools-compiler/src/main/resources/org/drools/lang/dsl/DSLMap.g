grammar DSLMap;

options { output = AST; backtrack = true; }

tokens {
       // imaginary tokens
       VT_DSL_GRAMMAR;
       VT_COMMENT;
       VT_ENTRY;
       
       VT_SCOPE;
       VT_CONDITION;
       VT_CONSEQUENCE;
       VT_KEYWORD;
       VT_ANY;
       
       VT_META;
       VT_ENTRY_KEY;
       VT_ENTRY_VAL;
       VT_VAR_DEF;
       VT_VAR_REF;
       VT_LITERAL;
       VT_PATTERN;
}

@parser::header {
	package org.drools.lang.dsl;
}

@lexer::header {
	package org.drools.lang.dsl;
}

@parser::members {
	private boolean validateLT(int LTNumber, String text){
		if (null == input) return false;
		if (null == input.LT(LTNumber)) return false;
		if (null == input.LT(LTNumber).getText()) return false;
		
		String text2Validate = input.LT(LTNumber).getText();
		if (text2Validate.startsWith("[") && text2Validate.endsWith("]")){
			text2Validate = text2Validate.substring(1, text2Validate.length() - 1); 
		}

		return text2Validate.equalsIgnoreCase(text);
	}

	private boolean validateIdentifierKey(String text){
		return validateLT(1, text);
	}
}

// PARSER RULES
mapping_file
	: statement* 
	-> ^(VT_DSL_GRAMMAR statement*)
	;

statement
	: entry
	| comment
	| EOL!	
	;

comment	: LINE_COMMENT
	-> ^(VT_COMMENT[$LINE_COMMENT, "COMMENT"] LINE_COMMENT )
	;	

entry 	: scope_section meta_section key_section EQUALS value_section (EOL|EOF)
	-> ^(VT_ENTRY scope_section meta_section key_section value_section)
	;

scope_section 
	: LEFT_SQUARE
	    ( value1=condition_key
	    | value2=consequence_key
	    | value3=keyword_key
	    | value4=any_key         
	    )
	  RIGHT_SQUARE
	-> ^(VT_SCOPE[$LEFT_SQUARE, "SCOPE SECTION"] $value1? $value2? $value3? $value4? )
	;
	
meta_section
	: LEFT_SQUARE LITERAL? RIGHT_SQUARE
	-> ^(VT_META[$LEFT_SQUARE, "META SECTION"] LITERAL?)
	;

key_section
	: key_sentence+
	-> ^(VT_ENTRY_KEY key_sentence+ )
	;
 
key_sentence
@init {
        String text = "";
}	
	: variable_definition
	| cb=key_chunk { text = $cb.text; }
	-> ^(VT_LITERAL[$cb.start, text] )
	;		

key_chunk
	: literal+
	;		
	
value_section
	: value_sentence+
	-> ^(VT_ENTRY_VAL value_sentence+ )
	;
	
value_sentence 
@init {
        String text = "";
}	
	: variable_reference
	| vc=value_chunk { text = $vc.text; }
	-> ^(VT_LITERAL[$vc.start, text] )
	;	
	
value_chunk
	: (literal|EQUALS)+
	;	
	
literal 
	: ( LITERAL | LEFT_SQUARE | RIGHT_SQUARE | COLLOM )
	;	
	
variable_definition
@init {
        String text = "";
}	
	: LEFT_CURLY name=LITERAL ( COLLOM pat=pattern {text = $pat.text;} )? RIGHT_CURLY
	-> ^(VT_VAR_DEF $name VT_PATTERN[$pat.start, text] )
	;
	
pattern 
        : ( LITERAL
          | LEFT_CURLY LITERAL RIGHT_CURLY
          )+
	;	
	
variable_reference 
	: LEFT_CURLY name=LITERAL RIGHT_CURLY
	-> ^(VT_VAR_REF $name )
	;	

condition_key
	:	{validateIdentifierKey("condition")||validateIdentifierKey("when")}?  value=LITERAL
	-> VT_CONDITION[$value]
	;

consequence_key 
	:	{validateIdentifierKey("consequence")||validateIdentifierKey("then")}?  value=LITERAL
	-> VT_CONSEQUENCE[$value]
	;

keyword_key 
	:	{validateIdentifierKey("keyword")}?  value=LITERAL
	-> VT_KEYWORD[$value]
	;

any_key 
	:	{validateIdentifierKey("*")}?  value=LITERAL
	-> VT_ANY[$value]
	;


// LEXER RULES
	
WS      :       (	' '
                |	'\t'
                |	'\f'
                )+
                { $channel=HIDDEN; }
        ;

EOL 	:	     
   		(       ( '\r\n' )=> '\r\n'  // Evil DOS
                |       '\r'    // Macintosh
                |       '\n'    // Unix (the right way)
                )
        ;  
        
fragment
EscapeSequence
    :   '\\' ('b'|'B'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\'|'.'|'o'|
              'x'|'a'|'e'|'c'|'d'|'D'|'s'|'S'|'w'|'W'|'p'|'A'|
              'G'|'Z'|'z'|'Q'|'E'|'*'|'['|']'|'('|')'|'$'|'^'|
              '{'|'}'|'?'|'+'|'-'|'&'|'|'|'='|'u'|'0'|'#')
    ;

LEFT_SQUARE
        :	'['
        ;

RIGHT_SQUARE
        :	']'
        ;        

LEFT_CURLY
        :	'{'
        ;

RIGHT_CURLY
        :	'}'
        ;
        
EQUALS	:	'='
	;
	
POUND   :	'#'
	;

COLLOM	:	':'
	;
	
LINE_COMMENT	
	:	POUND ( options{greedy=false;} : .)* EOL /* ('\r')? '\n'  */
	;

LITERAL	
	:	('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff'|MISC|EscapeSequence)+
	;

fragment		
MISC 	:
		'!' | '@' | '$' | '%' | '^' | '*' | '-' | '+'  | '?' | '/' | '\'' | '"' | '|' | '&' | '(' | ')' | ';'
	;


