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

PACKAGE : 'package';
UNIT : 'unit';
IMPORT : 'import';
FUNCTION : 'function';
STATIC : 'static';
GLOBAL : 'global';
RULE : 'rule';
QUERY : 'query';
EXTENDS : 'extends';
SUPER : 'super';
WHEN : 'when';
THEN : 'then';
END : 'end';

KWD_AND : 'and';
KWD_OR : 'or';

EXISTS : 'exists';
NOT : 'not';
IN : 'in';
FROM : 'from';

SALIENCE : 'salience';
ENABLED : 'enabled';
NO_LOOP : 'no-loop';
AUTO_FOCUS : 'auto-focus';
LOCK_ON_ACTIVE : 'lock-on-active';
REFRACT : 'refract';
DIRECT : 'direct';
AGENDA_GROUP : 'agenda-group';
ACTIVATION_GROUP : 'activation-group';
RULEFLOW_GROUP : 'ruleflow-group';
DATE_EFFECTIVE : 'date-effective';
DATE_EXPIRES : 'date-expires';
DIALECT : 'dialect';
CALENDARS : 'calendars';
TIMER : 'timer';
DURATION : 'duration';

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


/////////////////
// SYMBOLS
/////////////////

HASH : '#';
UNIFY :	':=' ;
NULL_SAFE_DOT :	'!.' ;
QUESTION_DIV :	'?/' ;

MISC : '\'' | '\\' | '$' ;
