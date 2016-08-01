grammar Ambiguous;

expr:   functionInvocation
    |   logical_or
    ;

logical_or
    :   logical_and
    |   logical_or or_key logical_and
    ;

logical_and
    :   primary
    |   logical_and and_key primary
    ;

primary
    :   INT
    |   ID
    |   '(' expr ')'
    ;

functionInvocation
    :   identifier '(' expr ')'
    ;

identifier
    :   ID {true}? (ID)*
    ;

and_key
    :   {_input.LT(1).getText().equals("and")}? ID
    ;

or_key
    :   {_input.LT(1).getText().equals("or")}? ID
    ;

ID  :   [a-zA-Z]+ ;      // match identifiers
INT :   [0-9]+ ;         // match integers
WS  :   [ \t]+ -> skip ; // toss out whitespace

