lexer grammar JavaParserLexer;
@members {
	public static final CommonToken IGNORE_TOKEN = new CommonToken(null,0,99,0,0);
}
@header {
	package org.drools.semantics.java.parser;
}

T68 : 'void' ;
T69 : 'boolean' ;
T70 : 'byte' ;
T71 : 'char' ;
T72 : 'short' ;
T73 : 'int' ;
T74 : 'float' ;
T75 : 'long' ;
T76 : 'double' ;
T77 : 'private' ;
T78 : 'public' ;
T79 : 'protected' ;
T80 : 'static' ;
T81 : 'transient' ;
T82 : 'final' ;
T83 : 'abstract' ;
T84 : 'native' ;
T85 : 'threadsafe' ;
T86 : 'synchronized' ;
T87 : 'volatile' ;
T88 : 'strictfp' ;
T89 : 'class' ;
T90 : 'extends' ;
T91 : 'interface' ;
T92 : 'implements' ;
T93 : 'this' ;
T94 : 'super' ;
T95 : 'throws' ;
T96 : 'if' ;
T97 : 'else' ;
T98 : 'for' ;
T99 : 'while' ;
T100 : 'do' ;
T101 : 'break' ;
T102 : 'continue' ;
T103 : 'return' ;
T104 : 'switch' ;
T105 : 'throw' ;
T106 : 'case' ;
T107 : 'default' ;
T108 : 'try' ;
T109 : 'finally' ;
T110 : 'catch' ;
T111 : 'instanceof' ;
T112 : 'true' ;
T113 : 'false' ;
T114 : 'null' ;
T115 : 'new' ;

#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 914
QUESTION		:	'?'		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 917
LPAREN			:	'('		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 920
RPAREN			:	')'		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 923
LBRACK			:	'['		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 926
RBRACK			:	']'		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 929
LCURLY			:	'{'		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 932
RCURLY			:	'}'		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 935
COLON			:	':'		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 938
COMMA			:	','		;

#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 940
DOT				:	'.'		;

#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 942
ASSIGN			:	'='		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 945
EQUAL			:	'=='	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 948
LNOT			:	'!'		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 951
BNOT			:	'~'		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 954
NOT_EQUAL		:	'!='	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 957
DIV				:	'/'		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 960
DIV_ASSIGN		:	'/='	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 963
PLUS			:	'+'		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 966
PLUS_ASSIGN		:	'+='	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 969
INC				:	'++'	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 972
MINUS			:	'-'		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 975
MINUS_ASSIGN	:	'-='	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 978
DEC				:	'--'	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 981
STAR			:	'*'		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 984
STAR_ASSIGN		:	'*='	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 987
MOD				:	'%'		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 990
MOD_ASSIGN		:	'%='	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 993
SR				:	'>>'	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 996
SR_ASSIGN		:	'>>='	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 999
BSR				:	'>>>'	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1002
BSR_ASSIGN		:	'>>>='	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1005
GE				:	'>='	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1008
GT				:	'>'		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1011
SL				:	'<<'	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1014
SL_ASSIGN		:	'<<='	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1017
LE				:	'<='	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1020
LT				:	'<'		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1023
BXOR			:	'^'		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1026
BXOR_ASSIGN		:	'^='	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1029
BOR				:	'|'		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1032
BOR_ASSIGN		:	'|='	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1035
LOR				:	'||'	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1038
BAND			:	'&'		;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1041
BAND_ASSIGN		:	'&='	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1044
LAND			:	'&&'	;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1047
SEMI			:	';'		;


// Whitespace -- ignored


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1053
WS	:	(	' '
		|	'\t'
		|	'\f'
			// handle newlines
		|	(	'\r\n'  // Evil DOS
			|	'\r'    // Macintosh
			|	'\n'    // Unix (the right way)
			)
		)+
		{ channel=99; /*token = JavaParser.IGNORE_TOKEN;*/ }
	;

// Single-line comments


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1068
SL_COMMENT
	:	'//' (options {greedy=false;} : .)* ('\r')? '\n'
		{channel=99; /*token = JavaParser.IGNORE_TOKEN;*/}
	;

// multiple-line comments


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1076
ML_COMMENT
	:	'/*'
		( options {greedy=false;} : . )*
		'*/'
		{channel=99;/*token = JavaParser.IGNORE_TOKEN;*/}
	;

#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1083
IDENT
	:	('a'..'z'|'A'..'Z'|'_'|'$') ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$')*
	;

// From the java language spec

#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1089
NUM_INT
    : DECIMAL_LITERAL 
    | HEX_LITERAL
    | OCTAL_LITERAL
    ;

#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1095
fragment
DECIMAL_LITERAL: '1'..'9' ('0'..'9')* ('l'|'L')? ;

#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1098
fragment
HEX_LITERAL: '0' ('x'|'X') ('0'..'9'|'a'..'f'|'A'..'F')+ ('l'|'L')? ;

#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1101
fragment
OCTAL_LITERAL: '0' ('0'..'7')* ('l'|'L')? ;

#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1104
NUM_FLOAT
    :     DIGITS '.' (DIGITS)? (EXPONENT_PART)? (FLOAT_TYPE_SUFFIX)?
    | '.' DIGITS (EXPONENT_PART)? (FLOAT_TYPE_SUFFIX)?
    |     DIGITS EXPONENT_PART FLOAT_TYPE_SUFFIX
    |     DIGITS EXPONENT_PART
    |     DIGITS FLOAT_TYPE_SUFFIX
    ;


#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1113
fragment
DIGITS : ('0'..'9')+ ;

/*
fragment
EXPONENT_PART: ('e'|'E') ('+'|'-')? DIGITS ;
*/

#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1121
fragment
EXPONENT_PART: ('e'|'E') ('+'|'-')? DIGITS ;

#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1124
fragment
FLOAT_TYPE_SUFFIX :   ('f'|'F'|'d'|'D') ;

#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1127
CHAR_LITERAL
    :
      '\''
      ( ~('\''|'\\')
      | ESCAPE_SEQUENCE
      )
      '\''
    ;

#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1136
STRING_LITERAL
    :
      '\"'
      ( ~('\"'|'\\')
      | ESCAPE_SEQUENCE
      )*
      '\"'
        ;

#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1145
fragment
ESCAPE_SEQUENCE
    :	'\\' 'b'
    |   '\\' 't'
    |   '\\' 'n'
    |   '\\' 'f'
    |   '\\' 'r'
    |   '\\' '\"'
    |   '\\' '\''
    |   '\\' '\\'
    |	'\\' '0'..'3' OCTAL_DIGIT OCTAL_DIGIT
    |   '\\' OCTAL_DIGIT OCTAL_DIGIT
    |   '\\' OCTAL_DIGIT
	|	UNICODE_CHAR
	;

#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1161
fragment
UNICODE_CHAR
	:	'\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
	;

#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1166
fragment
HEX_DIGIT
	:	'0'..'9'|'a'..'f'|'A'..'F'
	;

#src "/Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/java.g" 1171
fragment
OCTAL_DIGIT
	:	'0'..'7'
	;
