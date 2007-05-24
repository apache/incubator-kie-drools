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
    :   '\\' ('b'|'B'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\'|'.'|'o'|
              'x'|'a'|'e'|'c'|'d'|'D'|'s'|'S'|'w'|'W'|'p'|'A'|
              'G'|'Z'|'z'|'Q'|'E'|'*'|'['|']'|'('|')'|'$'|'^'|
              '{'|'}'|'?'|'+'|'-'|'&'|'|')
    |   UnicodeEscape
    |   OctalEscape
    ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1781
fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1788
fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1793
BOOL
	:	('true'|'false') 
	;	

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1797
PACKAGE	:	'package';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1799
IMPORT	:	'import';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1801
FUNCTION :	'function';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1803
GLOBAL	:	'global';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1805
RULE    :	'rule';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1807
QUERY	:	'query';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1809
TEMPLATE :	'template';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1811
ATTRIBUTES :	'attributes';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1813
DATE_EFFECTIVE 
	:	'date-effective';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1816
DATE_EXPIRES 
	:	'date-expires';	
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1819
ENABLED :	'enabled';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1821
SALIENCE 
	:	'salience';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1824
NO_LOOP :	'no-loop';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1826
AUTO_FOCUS 
	:	'auto-focus';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1829
ACTIVATION_GROUP 
	:	'activation-group';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1832
AGENDA_GROUP 
	:	'agenda-group';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1835
DIALECT 
	:	'dialect';	
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1838
RULEFLOW_GROUP 
	:	'ruleflow-group';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1841
DURATION 
	:	'duration';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1844
LOCK_ON_ACTIVE
	:	'lock-on-active';	
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1847
FROM	:	'from';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1849
ACCUMULATE 
	:	'accumulate';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1852
INIT	:	'init';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1854
ACTION	:	'action';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1856
RESULT	:	'result';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1858
COLLECT :	'collect';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1860
OR	:	'or';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1862
AND	:	'and';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1864
CONTAINS 
	:	'contains';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1867
EXCLUDES 
	:	'excludes';
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1870
MEMBEROF
	:	'memberOf';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1873
MATCHES :	'matches';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1875
IN	:	'in';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1877
NULL	:	'null';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1879
EXISTS	:	'exists';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1881
NOT	:	'not';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1883
EVAL	:	'eval';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1885
FORALL	:	'forall';							

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1887
WHEN    :	'when'; 

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1889
THEN	:    	'then';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1891
END     :	'end';

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1893
ID	
	:	('a'..'z'|'A'..'Z'|'_'|'$'|'\u00c0'..'\u00ff')('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff')* 
	;
		
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1897
LEFT_PAREN
        :	'('
        ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1901
RIGHT_PAREN
        :	')'
        ;
        
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1905
LEFT_SQUARE
        :	'['
        ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1909
RIGHT_SQUARE
        :	']'
        ;        

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1913
LEFT_CURLY
        :	'{'
        ;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1917
RIGHT_CURLY
        :	'}'
        ;
        
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1921
COMMA	:	','
	;
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1924
DOUBLE_AMPER
	:	'&&'
	;
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1928
DOUBLE_PIPE
	:	'||'
	;				
	
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1932
SH_STYLE_SINGLE_LINE_COMMENT	
	:	'#' ( options{greedy=false;} : .)* EOL /* ('\r')? '\n'  */
                { $channel=HIDDEN; }
	;
        
        
// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1938
C_STYLE_SINGLE_LINE_COMMENT	
	:	'//' ( options{greedy=false;} : .)* EOL // ('\r')? '\n' 
                { $channel=HIDDEN; }
	;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1943
MULTI_LINE_COMMENT
	:	'/*' (options{greedy=false;} : .)* '*/'
                { $channel=HIDDEN; }
	;

// $ANTLR src "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g" 1948
MISC 	:
		'!' | '@' | '$' | '%' | '^' | '*' | '_' | '-' | '+'  | '?' | '=' | '/' | '\'' | '\\' | '|' | '&'
	;
