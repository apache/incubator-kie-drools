lexer grammar JFDI;
@header {
	package org.codehaus.jfdi.parser;
}

T8 : ';' ;
T9 : '{' ;
T10 : '}' ;
T11 : 'for' ;
T12 : 'in' ;
T13 : '=' ;
T14 : '||' ;
T15 : '&&' ;
T16 : '+' ;
T17 : '-' ;
T18 : '*' ;
T19 : '/' ;
T20 : 'true' ;
T21 : 'false' ;
T22 : '(' ;
T23 : ')' ;
T24 : ',' ;
T25 : '[' ;
T26 : ']' ;
T27 : '.' ;
T28 : '=>' ;

// $ANTLR src "/Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g" 265
IDENT
	:	
		('a'..'z'|'A'..'Z'|'_'|'$')('a'..'z'|'A'..'Z'|'_'|'0'..'9')* 
	;
	
// $ANTLR src "/Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g" 270
INTEGER
	:
		(	('1'..'9')('0'..'9')*
		|	'0x' ('0'..'9'|'A'..'F'|'a'..'f')+
		|	'0' ('0'..'7')+
		)
	;
	
// $ANTLR src "/Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g" 278
STRING
	:
		( ('"' ~'"'+ '"') | ('\'' ~'\'' + '\'') )
	;
	
// $ANTLR src "/Users/bob/checkouts/jfdi/src/org/codehaus/jfdi/parser/JFDI.g" 283
FLOAT
	:
		('0'..'9')+'.'('0'..'9')+
	;

	
