lexer grammar CLP;
@header {
	package org.drools.clp;
}

T37 : ';' ;
T38 : '<-' ;
T39 : ':' ;
T40 : '=' ;
T41 : 'modify' ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 545
DEFRULE	:	'defrule';
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 546
OR 	:	'or';
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 547
AND 	:	'and';
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 548
NOT 	:	'not';
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 549
EXISTS 	:	'exists';
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 550
TEST 	:	'test';

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 552
NULL	:	'null';

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 554
WS      :       (	' '
                |	'\t'
                |	'\f'
                |	EOL
                )
                { $channel=HIDDEN; }
        ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 562
fragment
EOL 	:	     
   		(       ( '\r\n' )=> '\r\n'  // Evil DOS
                |       '\r'    // Macintosh
                |       '\n'    // Unix (the right way)
                )
        ;  
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 570
INT	
	:	('-')?('0'..'9')+
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 574
FLOAT
	:	('-')?('0'..'9')+ '.' ('0'..'9')+
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 578
STRING
    :  ('"' ( EscapeSequence | ~('\\'|'"') )* '"')
     | ('\'' ( EscapeSequence | ~('\\'|'\'') )* '\'')
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 583
fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 586
fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 593
fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 600
fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 605
BOOL
	:	('true'|'false') 
	;
	
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 609
VAR 	: '?'('a'..'z'|'A'..'Z'|'_'|'$'|'\u00c0'..'\u00ff')('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff')* 
        ;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 612
ID	
	:	('a'..'z'|'A'..'Z'|'_'|'$'|'\u00c0'..'\u00ff')('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff')* 
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 616
SH_STYLE_SINGLE_LINE_COMMENT	
	:	'#' ( options{greedy=false;} : .)* EOL /* ('\r')? '\n'  */
                { $channel=HIDDEN; }
	;
        
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 622
C_STYLE_SINGLE_LINE_COMMENT	
	:	'//' ( options{greedy=false;} : .)* EOL // ('\r')? '\n' 
                { $channel=HIDDEN; }
	;


// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 628
LEFT_PAREN
	:	'('
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 632
RIGHT_PAREN
	:	')'
	;
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 636
LEFT_SQUARE
	:	'['
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 640
RIGHT_SQUARE
	:	']'
	;        

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 644
LEFT_CURLY
	:	'{'
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 648
RIGHT_CURLY
	:	'}'
	;
	
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 652
TILDE	:	'~'
	;	
	
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 655
AMPERSAND 
	:	'&'
	;
	
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 659
PIPE
	:	'|'
	;		
        
// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 663
MULTI_LINE_COMMENT
	:	'/*' (options{greedy=false;} : .)* '*/'
                { $channel=HIDDEN; }
	;

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 668
MISC 	:
		'!' | '@' | '$' | '%' | '^' | '*' | '_' | '-' | '+'  | '?' | ',' | '=' | '/' | '\'' | '\\' | 
		'<' | '>' | '<=' | '>=' 
	;		

// $ANTLR src "C:\dev\jbossrules\trunk\drools-compiler\src\main\resources\org\drools\clp\CLP.g" 673
SYMBOL
	:	(~(' '|'('|')'|'~'|'"'|'?'|'&'|'|') )+
	;

