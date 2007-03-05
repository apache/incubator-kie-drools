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

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 391
VAR 	: '?'ID	
        ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 394
ID	
	:	('a'..'z'|'A'..'Z'|'_'|'$'|'\u00c0'..'\u00ff')('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff')* 
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 398
NULL	:	'null';

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 400
WS      :       (	' '
                |	'\t'
                |	'\f'
                |	EOL
                )
                { $channel=HIDDEN; }
        ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 408
fragment
EOL 	:	     
   		(       ( '\r\n' )=> '\r\n'  // Evil DOS
                |       '\r'    // Macintosh
                |       '\n'    // Unix (the right way)
                )
        ;  
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 416
INT	
	:	('-')?('0'..'9')+
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 420
FLOAT
	:	('-')?('0'..'9')+ '.' ('0'..'9')+
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 424
STRING
    :  ('"' ( EscapeSequence | ~('\\'|'"') )* '"')
     | ('\'' ( EscapeSequence | ~('\\'|'\'') )* '\'')
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 429
fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 432
fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 439
fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 446
fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 451
BOOL
	:	('true'|'false') 
	;
	
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 455
SH_STYLE_SINGLE_LINE_COMMENT	
	:	'#' ( options{greedy=false;} : .)* EOL /* ('\r')? '\n'  */
                { $channel=HIDDEN; }
	;
        
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 461
C_STYLE_SINGLE_LINE_COMMENT	
	:	'//' ( options{greedy=false;} : .)* EOL // ('\r')? '\n' 
                { $channel=HIDDEN; }
	;


// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 467
LEFT_PAREN
	:	'('
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 471
RIGHT_PAREN
	:	')'
	;
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 475
LEFT_SQUARE
	:	'['
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 479
RIGHT_SQUARE
	:	']'
	;        

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 483
LEFT_CURLY
	:	'{'
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 487
RIGHT_CURLY
	:	'}'
	;
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 491
MULTI_LINE_COMMENT
	:	'/*' (options{greedy=false;} : .)* '*/'
                { $channel=HIDDEN; }
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 496
MISC 	:
		'!' | '@' | '$' | '%' | '^' | '&' | '*' | '_' | '-' | '+'  | '?' | '|' | ',' | '=' | '/' | '\'' | '\\'
	;		
