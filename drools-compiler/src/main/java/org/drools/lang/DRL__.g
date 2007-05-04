lexer grammar DRL;
@header {
	package org.drools.lang;
}

T67 : ';' ;
T68 : ':' ;
T69 : '.' ;
T70 : '.*' ;
T71 : '||' ;
T72 : '&' ;
T73 : '|' ;
T74 : '->' ;
T75 : '==' ;
T76 : '>' ;
T77 : '>=' ;
T78 : '<' ;
T79 : '<=' ;
T80 : '!=' ;
T81 : '&&' ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1562
WS      :       (	' '
                |	'\t'
                |	'\f'
                |	EOL
                )
                { $channel=HIDDEN; }
        ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1570
fragment
EOL 	:	     
   		(       ( '\r\n' )=> '\r\n'  // Evil DOS
                |       '\r'    // Macintosh
                |       '\n'    // Unix (the right way)
                )
        ;  
        
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1578
INT	
	:	('-')?('0'..'9')+
		;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1582
FLOAT
	:	('-')?('0'..'9')+ '.' ('0'..'9')+
	;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1586
STRING
    :  ('"' ( EscapeSequence | ~('\\'|'"') )* '"')
     | ('\'' ( EscapeSequence | ~('\\'|'\'') )* '\'')
    ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1591
fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1594
fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\'|'.')
    |   UnicodeEscape
    |   OctalEscape
    ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1601
fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1608
fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1613
BOOL
	:	('true'|'false') 
	;	

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1617
PACKAGE	:	'package';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1619
IMPORT	:	'import';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1621
FUNCTION :	'function';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1623
GLOBAL	:	'global';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1625
RULE    :	'rule';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1627
QUERY	:	'query';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1629
TEMPLATE :	'template';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1631
ATTRIBUTES :	'attributes';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1633
DATE_EFFECTIVE 
	:	'date-effective';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1636
DATE_EXPIRES 
	:	'date-expires';	
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1639
ENABLED :	'enabled';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1641
SALIENCE 
	:	'salience';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1644
NO_LOOP :	'no-loop';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1646
AUTO_FOCUS 
	:	'auto-focus';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1649
ACTIVATION_GROUP 
	:	'activation-group';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1652
AGENDA_GROUP 
	:	'agenda-group';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1655
DIALECT 
	:	'dialect';	
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1658
RULEFLOW_GROUP 
	:	'ruleflow-group';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1661
DURATION 
	:	'duration';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1664
LOCK_ON_ACTIVE
	:	'lock-on-active';	
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1667
FROM	:	'from';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1669
ACCUMULATE 
	:	'accumulate';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1672
INIT	:	'init';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1674
ACTION	:	'action';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1676
RESULT	:	'result';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1678
COLLECT :	'collect';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1680
OR	:	'or';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1682
AND	:	'and';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1684
CONTAINS 
	:	'contains';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1687
EXCLUDES 
	:	'excludes';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1690
MEMBEROF
	:	'memberOf';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1693
MATCHES :	'matches';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1695
IN	:	'in';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1697
NULL	:	'null';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1699
EXISTS	:	'exists';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1701
NOT	:	'not';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1703
EVAL	:	'eval';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1705
FORALL	:	'forall';							

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1707
WHEN    :	'when'; 

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1709
THEN	:    	'then';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1711
END     :	'end';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1713
ID	
	:	('a'..'z'|'A'..'Z'|'_'|'$'|'\u00c0'..'\u00ff')('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff')* 
	;
		

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1718
SH_STYLE_SINGLE_LINE_COMMENT	
	:	'#' ( options{greedy=false;} : .)* EOL /* ('\r')? '\n'  */
                { $channel=HIDDEN; }
	;
        
        
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1724
C_STYLE_SINGLE_LINE_COMMENT	
	:	'//' ( options{greedy=false;} : .)* EOL // ('\r')? '\n' 
                { $channel=HIDDEN; }
	;


// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1730
LEFT_PAREN
        :	'('
        ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1734
RIGHT_PAREN
        :	')'
        ;
        
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1738
LEFT_SQUARE
        :	'['
        ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1742
RIGHT_SQUARE
        :	']'
        ;        

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1746
LEFT_CURLY
        :	'{'
        ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1750
RIGHT_CURLY
        :	'}'
        ;
        
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1754
MULTI_LINE_COMMENT
	:	'/*' (options{greedy=false;} : .)* '*/'
                { $channel=HIDDEN; }
	;
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1759
COMMA	:	','
	;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1762
MISC 	:
		'!' | '@' | '$' | '%' | '^' | '&' | '*' | '_' | '-' | '+'  | '?' | '|' | '=' | '/' | '\'' | '\\'
	;
