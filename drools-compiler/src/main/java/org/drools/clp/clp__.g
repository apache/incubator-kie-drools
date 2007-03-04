lexer grammar CLP;
@header {
	package org.drools.clp;
}

T27 : ';' ;
T28 : 'defrule' ;
T29 : '~' ;
T30 : '&' ;
T31 : '|' ;
T32 : '=' ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 368
VAR 	: '?'ID	
        ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 371
ID	
	:	('a'..'z'|'A'..'Z'|'_'|'$'|'\u00c0'..'\u00ff')('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff')* 
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 375
NULL	:	'null';

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 377
WS      :       (	' '
                |	'\t'
                |	'\f'
                |	EOL
                )
                { $channel=HIDDEN; }
        ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 385
fragment
EOL 	:	     
   		(       ( '\r\n' )=> '\r\n'  // Evil DOS
                |       '\r'    // Macintosh
                |       '\n'    // Unix (the right way)
                )
        ;  
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 393
INT	
	:	('-')?('0'..'9')+
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 397
FLOAT
	:	('-')?('0'..'9')+ '.' ('0'..'9')+
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 401
STRING
    :  ('"' ( EscapeSequence | ~('\\'|'"') )* '"')
     | ('\'' ( EscapeSequence | ~('\\'|'\'') )* '\'')
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 406
fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 409
fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 416
fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 423
fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 428
BOOL
	:	('true'|'false') 
	;
	
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 432
SH_STYLE_SINGLE_LINE_COMMENT	
	:	'#' ( options{greedy=false;} : .)* EOL /* ('\r')? '\n'  */
                { $channel=HIDDEN; }
	;
        
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 438
C_STYLE_SINGLE_LINE_COMMENT	
	:	'//' ( options{greedy=false;} : .)* EOL // ('\r')? '\n' 
                { $channel=HIDDEN; }
	;


// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 444
LEFT_PAREN
	:	'('
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 448
RIGHT_PAREN
	:	')'
	;
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 452
LEFT_SQUARE
	:	'['
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 456
RIGHT_SQUARE
	:	']'
	;        

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 460
LEFT_CURLY
	:	'{'
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 464
RIGHT_CURLY
	:	'}'
	;
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 468
MULTI_LINE_COMMENT
	:	'/*' (options{greedy=false;} : .)* '*/'
                { $channel=HIDDEN; }
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 473
MISC 	:
		'!' | '@' | '$' | '%' | '^' | '&' | '*' | '_' | '-' | '+'  | '?' | '|' | ',' | '=' | '/' | '\'' | '\\'
	;		
