lexer grammar DRL;
@header {
	package org.drools.lang;
}

T69 : ';' ;
T70 : ':' ;
T71 : '.' ;
T72 : '.*' ;
T73 : '->' ;
T74 : '==' ;
T75 : '>' ;
T76 : '>=' ;
T77 : '<' ;
T78 : '<=' ;
T79 : '!=' ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1739
WS      :       (	' '
                |	'\t'
                |	'\f'
                |	EOL
                )
                { $channel=HIDDEN; }
        ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1747
fragment
EOL 	:	     
   		(       ( '\r\n' )=> '\r\n'  // Evil DOS
                |       '\r'    // Macintosh
                |       '\n'    // Unix (the right way)
                )
        ;  
        
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1755
INT	
	:	('-')?('0'..'9')+
		;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1759
FLOAT
	:	('-')?('0'..'9')+ '.' ('0'..'9')+
	;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1763
STRING
    :  ('"' ( EscapeSequence | ~('\\'|'"') )* '"')
     | ('\'' ( EscapeSequence | ~('\\'|'\'') )* '\'')
    ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1768
fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1771
fragment
EscapeSequence
    :   '\\' ('b'|'B'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\'|'.'|'o'|'x'|'a'|'e'|'c'|'d'|'D'|'s'|'S'|'w'|'W'|'p'|'A'|'G'|'Z'|'z'|'Q'|'E')
    |   UnicodeEscape
    |   OctalEscape
    ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1778
fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1785
fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1790
BOOL
	:	('true'|'false') 
	;	

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1794
PACKAGE	:	'package';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1796
IMPORT	:	'import';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1798
FUNCTION :	'function';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1800
GLOBAL	:	'global';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1802
RULE    :	'rule';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1804
QUERY	:	'query';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1806
TEMPLATE :	'template';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1808
ATTRIBUTES :	'attributes';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1810
DATE_EFFECTIVE 
	:	'date-effective';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1813
DATE_EXPIRES 
	:	'date-expires';	
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1816
ENABLED :	'enabled';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1818
SALIENCE 
	:	'salience';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1821
NO_LOOP :	'no-loop';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1823
AUTO_FOCUS 
	:	'auto-focus';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1826
ACTIVATION_GROUP 
	:	'activation-group';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1829
AGENDA_GROUP 
	:	'agenda-group';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1832
DIALECT 
	:	'dialect';	
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1835
RULEFLOW_GROUP 
	:	'ruleflow-group';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1838
DURATION 
	:	'duration';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1841
LOCK_ON_ACTIVE
	:	'lock-on-active';	
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1844
FROM	:	'from';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1846
ACCUMULATE 
	:	'accumulate';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1849
INIT	:	'init';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1851
ACTION	:	'action';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1853
RESULT	:	'result';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1855
COLLECT :	'collect';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1857
OR	:	'or';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1859
AND	:	'and';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1861
CONTAINS 
	:	'contains';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1864
EXCLUDES 
	:	'excludes';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1867
MEMBEROF
	:	'memberOf';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1870
MATCHES :	'matches';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1872
IN	:	'in';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1874
NULL	:	'null';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1876
EXISTS	:	'exists';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1878
NOT	:	'not';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1880
EVAL	:	'eval';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1882
FORALL	:	'forall';							

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1884
WHEN    :	'when'; 

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1886
THEN	:    	'then';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1888
END     :	'end';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1890
ID	
	:	('a'..'z'|'A'..'Z'|'_'|'$'|'\u00c0'..'\u00ff')('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff')* 
	;
		
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1894
LEFT_PAREN
        :	'('
        ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1898
RIGHT_PAREN
        :	')'
        ;
        
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1902
LEFT_SQUARE
        :	'['
        ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1906
RIGHT_SQUARE
        :	']'
        ;        

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1910
LEFT_CURLY
        :	'{'
        ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1914
RIGHT_CURLY
        :	'}'
        ;
        
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1918
COMMA	:	','
	;
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1921
DOUBLE_AMPER
	:	'&&'
	;
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1925
DOUBLE_PIPE
	:	'||'
	;				
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1929
SH_STYLE_SINGLE_LINE_COMMENT	
	:	'#' ( options{greedy=false;} : .)* EOL /* ('\r')? '\n'  */
                { $channel=HIDDEN; }
	;
        
        
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1935
C_STYLE_SINGLE_LINE_COMMENT	
	:	'//' ( options{greedy=false;} : .)* EOL // ('\r')? '\n' 
                { $channel=HIDDEN; }
	;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1940
MULTI_LINE_COMMENT
	:	'/*' (options{greedy=false;} : .)* '*/'
                { $channel=HIDDEN; }
	;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1945
MISC 	:
		'!' | '@' | '$' | '%' | '^' | '*' | '_' | '-' | '+'  | '?' | '=' | '/' | '\'' | '\\' | '|' | '&'
	;
