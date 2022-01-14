grammar DRL;

// KEYWORDS

PACKAGE : 'package';
IMPORT : 'import';
RULE : 'rule';
WHEN : 'when';
THEN : 'then';
END : 'end';

// PARSER

compilationunit : packagedef? importdef* ruledef* ;

packagedef : PACKAGE FQNAME SEMICOLON? ;

importdef : IMPORT FQNAME (DOT STAR)? SEMICOLON? ;

ruledef : RULE IDENTIFIER WHEN lhs THEN rhs END ;

lhs : TEXT ;

rhs : TEXT ;

// LITERALS

fragment DIGIT : [0-9] ;
NUMBER         : DIGIT+ ([.,] DIGIT+)? ;

fragment LOWERCASE  : [a-z] ;
fragment UPPERCASE  : [A-Z] ;
LETTER : (LOWERCASE | UPPERCASE | '_' | '$') ;

IDENTIFIER : LETTER (LETTER | DIGIT)* ;
FQNAME : IDENTIFIER (DOT IDENTIFIER)* ;

// SEPARATORS

LPAREN : '(';
RPAREN : ')';
LBRACE : '{';
RBRACE : '}';
LBRACK : '[';
RBRACK : ']';
COMMA : ',';
ELIPSIS : '..';
DOT : '.';
COLON : ':';
SEMICOLON : ';';
STAR : '*';

// OPERATORS

EQUAL : '=';
GT : '>';
LT : '<';
LE : '<=';
GE : '>=';
NOTEQUAL : '!=';

WS : [ \t\r\n\u000C\u00A0]+ -> skip ;

fragment TEXT : .+ ;
