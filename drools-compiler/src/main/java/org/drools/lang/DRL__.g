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

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1564
WS      :       (	' '
                |	'\t'
                |	'\f'
                |	EOL
                )
                { $channel=HIDDEN; }
        ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1572
fragment
EOL 	:	     
   		(       ( '\r\n' )=> '\r\n'  // Evil DOS
                |       '\r'    // Macintosh
                |       '\n'    // Unix (the right way)
                )
        ;  
        
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1580
INT	
	:	('-')?('0'..'9')+
		;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1584
FLOAT
	:	('-')?('0'..'9')+ '.' ('0'..'9')+
	;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1588
STRING
    :  ('"' ( EscapeSequence | ~('\\'|'"') )* '"')
     | ('\'' ( EscapeSequence | ~('\\'|'\'') )* '\'')
    ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1593
fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1596
fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\'|'.')
    |   UnicodeEscape
    |   OctalEscape
    ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1603
fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1610
fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1615
BOOL
	:	('true'|'false') 
	;	

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1619
PACKAGE	:	'package';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1621
IMPORT	:	'import';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1623
FUNCTION :	'function';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1625
GLOBAL	:	'global';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1627
RULE    :	'rule';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1629
QUERY	:	'query';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1631
TEMPLATE :	'template';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1633
ATTRIBUTES :	'attributes';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1635
DATE_EFFECTIVE 
	:	'date-effective';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1638
DATE_EXPIRES 
	:	'date-expires';	
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1641
ENABLED :	'enabled';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1643
SALIENCE 
	:	'salience';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1646
NO_LOOP :	'no-loop';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1648
AUTO_FOCUS 
	:	'auto-focus';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1651
ACTIVATION_GROUP 
	:	'activation-group';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1654
AGENDA_GROUP 
	:	'agenda-group';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1657
DIALECT 
	:	'dialect';	
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1660
RULEFLOW_GROUP 
	:	'ruleflow-group';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1663
DURATION 
	:	'duration';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1666
LOCK_ON_ACTIVE
	:	'lock-on-active';	
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1669
FROM	:	'from';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1671
ACCUMULATE 
	:	'accumulate';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1674
INIT	:	'init';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1676
ACTION	:	'action';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1678
RESULT	:	'result';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1680
COLLECT :	'collect';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1682
OR	:	'or';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1684
AND	:	'and';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1686
CONTAINS 
	:	'contains';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1689
EXCLUDES 
	:	'excludes';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1692
MEMBEROF
	:	'memberOf';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1695
MATCHES :	'matches';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1697
IN	:	'in';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1699
NULL	:	'null';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1701
EXISTS	:	'exists';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1703
NOT	:	'not';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1705
EVAL	:	'eval';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1707
FORALL	:	'forall';							

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1709
WHEN    :	'when'; 

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1711
THEN	:    	'then';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1713
END     :	'end';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1715
ID	
	:	('a'..'z'|'A'..'Z'|'_'|'$'|'\u00c0'..'\u00ff')('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff')* 
	;
		

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1720
SH_STYLE_SINGLE_LINE_COMMENT	
	:	'#' ( options{greedy=false;} : .)* EOL /* ('\r')? '\n'  */
                { $channel=HIDDEN; }
	;
        
        
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1726
C_STYLE_SINGLE_LINE_COMMENT	
	:	'//' ( options{greedy=false;} : .)* EOL // ('\r')? '\n' 
                { $channel=HIDDEN; }
	;


// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1732
LEFT_PAREN
        :	'('
        ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1736
RIGHT_PAREN
        :	')'
        ;
        
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1740
LEFT_SQUARE
        :	'['
        ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1744
RIGHT_SQUARE
        :	']'
        ;        

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1748
LEFT_CURLY
        :	'{'
        ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1752
RIGHT_CURLY
        :	'}'
        ;
        
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1756
MULTI_LINE_COMMENT
	:	'/*' (options{greedy=false;} : .)* '*/'
                { $channel=HIDDEN; }
	;
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1761
COMMA	:	','
	;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1764
MISC 	:
		'!' | '@' | '$' | '%' | '^' | '&' | '*' | '_' | '-' | '+'  | '?' | '|' | '=' | '/' | '\'' | '\\'
	;
