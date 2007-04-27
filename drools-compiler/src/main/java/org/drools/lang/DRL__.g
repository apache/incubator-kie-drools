lexer grammar DRL;
@header {
	package org.drools.lang;
}

T64 : ';' ;
T65 : ':' ;
T66 : ',' ;
T67 : '.' ;
T68 : '.*' ;
T69 : '||' ;
T70 : '&' ;
T71 : '|' ;
T72 : '->' ;
T73 : '==' ;
T74 : '>' ;
T75 : '>=' ;
T76 : '<' ;
T77 : '<=' ;
T78 : '!=' ;
T79 : '&&' ;

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1489
WS      :       (	' '
                |	'\t'
                |	'\f'
                |	EOL
                )
                { $channel=HIDDEN; }
        ;

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1497
fragment
EOL 	:	     
   		(       ( '\r\n' )=> '\r\n'  // Evil DOS
                |       '\r'    // Macintosh
                |       '\n'    // Unix (the right way)
                )
        ;  
        
// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1505
INT	
	:	('-')?('0'..'9')+
		;

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1509
FLOAT
	:	('-')?('0'..'9')+ '.' ('0'..'9')+
	;

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1513
STRING
    :  ('"' ( EscapeSequence | ~('\\'|'"') )* '"')
     | ('\'' ( EscapeSequence | ~('\\'|'\'') )* '\'')
    ;

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1518
fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1521
fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\'|'.')
    |   UnicodeEscape
    |   OctalEscape
    ;

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1528
fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1535
fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1540
BOOL
	:	('true'|'false') 
	;	

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1544
PACKAGE	:	'package';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1546
IMPORT	:	'import';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1548
FUNCTION :	'function';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1550
GLOBAL	:	'global';
	
// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1552
RULE    :	'rule';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1554
QUERY	:	'query';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1556
TEMPLATE :	'template';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1558
ATTRIBUTES :	'attributes';
	
// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1560
DATE_EFFECTIVE 
	:	'date-effective';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1563
DATE_EXPIRES 
	:	'date-expires';	
	
// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1566
ENABLED :	'enabled';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1568
SALIENCE 
	:	'salience';
	
// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1571
NO_LOOP :	'no-loop';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1573
AUTO_FOCUS 
	:	'auto-focus';
	
// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1576
ACTIVATION_GROUP 
	:	'activation-group';
	
// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1579
AGENDA_GROUP 
	:	'agenda-group';
	
// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1582
DIALECT 
	:	'dialect';	
	
// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1585
RULEFLOW_GROUP 
	:	'ruleflow-group';
	
// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1588
DURATION 
	:	'duration';
	
// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1591
LOCK_ON_ACTIVE
	:	'lock-on-active';	
	
// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1594
FROM	:	'from';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1596
ACCUMULATE 
	:	'accumulate';
	
// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1599
INIT	:	'init';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1601
ACTION	:	'action';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1603
RESULT	:	'result';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1605
COLLECT :	'collect';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1607
OR	:	'or';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1609
AND	:	'and';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1611
CONTAINS 
	:	'contains';
	
// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1614
EXCLUDES 
	:	'excludes';
	
// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1617
MATCHES :	'matches';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1619
NULL	:	'null';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1621
EXISTS	:	'exists';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1623
NOT	:	'not';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1625
EVAL	:	'eval';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1627
FORALL	:	'forall';							

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1629
WHEN    :	'when'; 

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1631
THEN	:    	'then';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1633
END     :	'end';

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1635
ID	
	:	('a'..'z'|'A'..'Z'|'_'|'$'|'\u00c0'..'\u00ff')('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff')* 
	;
		

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1640
SH_STYLE_SINGLE_LINE_COMMENT	
	:	'#' ( options{greedy=false;} : .)* EOL /* ('\r')? '\n'  */
                { $channel=HIDDEN; }
	;
        
        
// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1646
C_STYLE_SINGLE_LINE_COMMENT	
	:	'//' ( options{greedy=false;} : .)* EOL // ('\r')? '\n' 
                { $channel=HIDDEN; }
	;


// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1652
LEFT_PAREN
        :	'('
        ;

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1656
RIGHT_PAREN
        :	')'
        ;
        
// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1660
LEFT_SQUARE
        :	'['
        ;

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1664
RIGHT_SQUARE
        :	']'
        ;        

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1668
LEFT_CURLY
        :	'{'
        ;

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1672
RIGHT_CURLY
        :	'}'
        ;
        
// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1676
MULTI_LINE_COMMENT
	:	'/*' (options{greedy=false;} : .)* '*/'
                { $channel=HIDDEN; }
	;

// $ANTLR src "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1681
MISC 	:
		'!' | '@' | '$' | '%' | '^' | '&' | '*' | '_' | '-' | '+'  | '?' | '|' | ',' | '=' | '/' | '\'' | '\\'
	;
