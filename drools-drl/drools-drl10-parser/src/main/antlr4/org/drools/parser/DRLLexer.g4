lexer grammar DRLLexer;

import JavaLexer;

@members {
    public String normalizeString( String input ) {
        if( input != null && (input.length() == 2 || input.length() >= 4) ) {
            input = input.substring( 1, input.length() - 1 );
            input = input.replaceAll( "\'", "'" );
            input = input.replaceAll( "\"", "\\\"" );
            input = "\"" + input + "\"";
        }
        return input;
    }
}

/////////////////
// KEYWORDS
/////////////////

// These keywords are already declared in JavaLexer. They should not be overriden with different names, or else Vocabulary's literalName will be null.
// So no need to declare by DRLLexer
// PACKAGE : 'package';
// IMPORT : 'import';
// STATIC : 'static';
// EXTENDS : 'extends';
// SUPER : 'super';

// DRL keywords
DRL_UNIT : 'unit';
DRL_FUNCTION : 'function';
DRL_GLOBAL : 'global';
DRL_DECLARE : 'declare';
DRL_RULE : 'rule';
DRL_QUERY : 'query';
DRL_WHEN : 'when';
DRL_THEN : 'then' -> pushMode(RHS);

DRL_AND : 'and';
DRL_OR : 'or';

DRL_EXISTS : 'exists';
DRL_NOT : 'not';
DRL_IN : 'in';
DRL_FROM : 'from';
DRL_MATCHES : 'matches';
DRL_ACCUMULATE : 'accumulate' | 'acc';
DRL_INIT : 'init';
DRL_ACTION : 'action';
DRL_REVERSE : 'reverse';
DRL_RESULT : 'result';
DRL_ENTRY_POINT : 'entry-point';

DRL_SALIENCE : 'salience';
DRL_ENABLED : 'enabled';
DRL_NO_LOOP : 'no-loop';
DRL_AUTO_FOCUS : 'auto-focus';
DRL_LOCK_ON_ACTIVE : 'lock-on-active';
DRL_REFRACT : 'refract';
DRL_DIRECT : 'direct';
DRL_AGENDA_GROUP : 'agenda-group';
DRL_ACTIVATION_GROUP : 'activation-group';
DRL_RULEFLOW_GROUP : 'ruleflow-group';
DRL_DATE_EFFECTIVE : 'date-effective';
DRL_DATE_EXPIRES : 'date-expires';
DRL_DIALECT : 'dialect';
DRL_CALENDARS : 'calendars';
DRL_TIMER : 'timer';
DRL_DURATION : 'duration';

/////////////////
// LEXER
/////////////////

TIME_INTERVAL
    : (('0'..'9')+ 'd') (('0'..'9')+ 'h')?(('0'..'9')+ 'm')?(('0'..'9')+ 's')?(('0'..'9')+ 'ms'?)?
    | (('0'..'9')+ 'h') (('0'..'9')+ 'm')?(('0'..'9')+ 's')?(('0'..'9')+ 'ms'?)?
    | (('0'..'9')+ 'm') (('0'..'9')+ 's')?(('0'..'9')+ 'ms'?)?
    | (('0'..'9')+ 's') (('0'..'9')+ 'ms'?)?
    | (('0'..'9')+ 'ms')
    ;

DRL_STRING_LITERAL
    :  ('"' ( DrlEscapeSequence | ~('\\'|'"') )* '"')
    |  ('\'' ( DrlEscapeSequence | ~('\\'|'\'') )* '\'') { setText( normalizeString( getText() ) ); }
    ;

/////////////////
// SYMBOLS
/////////////////

HASH : '#';
UNIFY :	':=' ;
NULL_SAFE_DOT :	'!.' ;
QUESTION_DIV :	'?/' ;

MISC : '\'' | '\\' | '$' ;

/////////////////
// Fragment
/////////////////
fragment
DrlEscapeSequence
    :   '\\' ('b'|'B'|'t'|'n'|'f'|'r'|'"'|'\''|'\\'|'.'|'o'|
              'x'|'a'|'e'|'c'|'d'|'D'|'s'|'S'|'w'|'W'|'p'|'A'|
              'G'|'Z'|'z'|'Q'|'E'|'*'|'['|']'|'('|')'|'$'|'^'|
              '{'|'}'|'?'|'+'|'-'|'&'|'|')
    |   DrlUnicodeEscape
    |   DrlOctalEscape
    ;

fragment
DrlOctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
DrlUnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

mode RHS;
RHS_WS : [ \t\r\n\u000C]+ -> channel(HIDDEN);
DRL_END : 'end' [ \t]* ('\n' | '\r\n' | EOF) {setText("end");} -> popMode;
RHS_CHUNK : ~[ \t\r\n\u000C]+ ;
