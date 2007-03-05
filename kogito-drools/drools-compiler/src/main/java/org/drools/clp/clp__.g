lexer grammar CLP;
@header {
	package org.drools.clp;
}

T27 : ';' ;
T28 : 'defrule' ;
T29 : '<-' ;
T30 : '&' ;
T31 : '|' ;
T32 : '~' ;
T33 : ':' ;
T34 : '=' ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 392
VAR 	: '?'ID	
        ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 395
ID	
	:	('a'..'z'|'A'..'Z'|'_'|'$'|'\u00c0'..'\u00ff')('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff')* 
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 399
NULL	:	'null';

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 401
WS      :       (	' '
                |	'\t'
                |	'\f'
                |	EOL
                )
                { $channel=HIDDEN; }
        ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 409
fragment
EOL 	:	     
   		(       ( '\r\n' )=> '\r\n'  // Evil DOS
                |       '\r'    // Macintosh
                |       '\n'    // Unix (the right way)
                )
        ;  
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 417
INT	
	:	('-')?('0'..'9')+
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 421
FLOAT
	:	('-')?('0'..'9')+ '.' ('0'..'9')+
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 425
STRING
    :  ('"' ( EscapeSequence | ~('\\'|'"') )* '"')
     | ('\'' ( EscapeSequence | ~('\\'|'\'') )* '\'')
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 430
fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 433
fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 440
fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 447
fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 452
BOOL
	:	('true'|'false') 
	;
	
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 456
SH_STYLE_SINGLE_LINE_COMMENT	
	:	'#' ( options{greedy=false;} : .)* EOL /* ('\r')? '\n'  */
                { $channel=HIDDEN; }
	;
        
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 462
C_STYLE_SINGLE_LINE_COMMENT	
	:	'//' ( options{greedy=false;} : .)* EOL // ('\r')? '\n' 
                { $channel=HIDDEN; }
	;


// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 468
LEFT_PAREN
	:	'('
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 472
RIGHT_PAREN
	:	')'
	;
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 476
LEFT_SQUARE
	:	'['
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 480
RIGHT_SQUARE
	:	']'
	;        

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 484
LEFT_CURLY
	:	'{'
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 488
RIGHT_CURLY
	:	'}'
	;
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 492
MULTI_LINE_COMMENT
	:	'/*' (options{greedy=false;} : .)* '*/'
                { $channel=HIDDEN; }
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 497
MISC 	:
		'!' | '@' | '$' | '%' | '^' | '&' | '*' | '_' | '-' | '+'  | '?' | '|' | ',' | '=' | '/' | '\'' | '\\'
	;		
