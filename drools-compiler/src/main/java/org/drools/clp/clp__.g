lexer grammar CLP;
@header {
	package org.drools.clp;
}

T33 : ';' ;
T34 : '<-' ;
T35 : '&' ;
T36 : '|' ;
T37 : '~' ;
T38 : ':' ;
T39 : '=' ;
T40 : 'modify' ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 533
DEFRULE	:	'defrule';
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 534
OR 		:	'or';
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 535
AND 	:	'and';
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 536
NOT 	:	'not';
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 537
EXISTS 	:	'exists';
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 538
TEST 	:	'test';

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 540
VAR 	: '?'ID	
        ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 543
ID	
	:	('a'..'z'|'A'..'Z'|'_'|'$'|'\u00c0'..'\u00ff')('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff')* 
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 547
NULL	:	'null';

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 549
WS      :       (	' '
                |	'\t'
                |	'\f'
                |	EOL
                )
                { $channel=HIDDEN; }
        ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 557
fragment
EOL 	:	     
   		(       ( '\r\n' )=> '\r\n'  // Evil DOS
                |       '\r'    // Macintosh
                |       '\n'    // Unix (the right way)
                )
        ;  
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 565
INT	
	:	('-')?('0'..'9')+
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 569
FLOAT
	:	('-')?('0'..'9')+ '.' ('0'..'9')+
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 573
STRING
    :  ('"' ( EscapeSequence | ~('\\'|'"') )* '"')
     | ('\'' ( EscapeSequence | ~('\\'|'\'') )* '\'')
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 578
fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 581
fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 588
fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 595
fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 600
BOOL
	:	('true'|'false') 
	;
	
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 604
SH_STYLE_SINGLE_LINE_COMMENT	
	:	'#' ( options{greedy=false;} : .)* EOL /* ('\r')? '\n'  */
                { $channel=HIDDEN; }
	;
        
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 610
C_STYLE_SINGLE_LINE_COMMENT	
	:	'//' ( options{greedy=false;} : .)* EOL // ('\r')? '\n' 
                { $channel=HIDDEN; }
	;


// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 616
LEFT_PAREN
	:	'('
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 620
RIGHT_PAREN
	:	')'
	;
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 624
LEFT_SQUARE
	:	'['
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 628
RIGHT_SQUARE
	:	']'
	;        

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 632
LEFT_CURLY
	:	'{'
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 636
RIGHT_CURLY
	:	'}'
	;
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 640
MULTI_LINE_COMMENT
	:	'/*' (options{greedy=false;} : .)* '*/'
                { $channel=HIDDEN; }
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 645
MISC 	:
		'!' | '@' | '$' | '%' | '^' | '&' | '*' | '_' | '-' | '+'  | '?' | '|' | ',' | '=' | '/' | '\'' | '\\'
	;		
