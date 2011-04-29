package org.drools.lang;

import org.drools.base.evaluators.Operator;

/**
 * Simple holder class identifying all the DRL soft keywords. This is used by
 * DRLParser.
 */
public class DroolsSoftKeywords {
    public static final String DATE         = "date";
    public static final String EFFECTIVE    = "effective";
    public static final String EXPIRES      = "expires";
    public static final String LOCK         = "lock";
    public static final String ON           = "on";
    public static final String ACTIVE       = "active";
    public static final String NO           = "no";
    public static final String LOOP         = "loop";
    public static final String AUTO         = "auto";
    public static final String FOCUS        = "focus";
    public static final String ACTIVATION   = "activation";
    public static final String GROUP        = "group";
    public static final String AGENDA       = "agenda";
    public static final String RULEFLOW     = "ruleflow";
    public static final String DURATION     = "duration";
    public static final String TIMER        = "timer";
    public static final String CALENDARS    = "calendars";
    public static final String PACKAGE      = "package";
    public static final String IMPORT       = "import";
    public static final String DIALECT      = "dialect";
    public static final String SALIENCE     = "salience";
    public static final String ENABLED      = "enabled";
    public static final String ATTRIBUTES   = "attributes";
    public static final String RULE         = "rule";
    public static final String EXTEND       = "extends";
    public static final String TEMPLATE     = "template";
    public static final String WHEN         = "when";
    public static final String THEN         = "then";
    public static final String QUERY        = "query";
    public static final String DECLARE      = "declare";
    public static final String FUNCTION     = "function";
    public static final String GLOBAL       = "global";
    public static final String CONTAINS     = "contains";
    public static final String MATCHES      = "matches";
    public static final String EVAL         = "eval";
    public static final String EXCLUDES     = "excludes";
    public static final String SOUNDSLIKE   = "soundslike";
    public static final String MEMBEROF     = "memberof";
    public static final String NOT          = "not";
    public static final String IN           = "in";
    public static final String OR           = "or";
    public static final String AND          = "and";
    public static final String EXISTS       = "exists";
    public static final String FORALL       = "forall";
    public static final String OVER         = "over";
    public static final String FROM         = "from";
    public static final String ENTRY        = "entry";
    public static final String POINT        = "point";
    public static final String ACCUMULATE   = "accumulate";
    public static final String ACC          = "acc"; 
    public static final String COLLECT      = "collect";
    public static final String ACTION       = "action";
    public static final String REVERSE      = "reverse";
    public static final String RESULT       = "result";
    public static final String END          = "end";
    public static final String INIT         = "init";
    public static final String INSTANCEOF   = "instanceof";
    public static final String EXTENDS      = "extends";
    public static final String SUPER        = "super";
    public static final String BOOLEAN      = "boolean";
    public static final String CHAR         = "char";
    public static final String BYTE         = "byte";
    public static final String SHORT        = "short";
    public static final String INT          = "int";
    public static final String LONG         = "long";
    public static final String FLOAT        = "float";
    public static final String DOUBLE       = "double";
    public static final String THIS         = "this";
    public static final String VOID         = "void";
    public static final String CLASS        = "class";
    public static final String NEW          = "new";

    public static final String CASE         = "case";
    public static final String FINAL        = "final";
    public static final String IF           = "if";
    public static final String ELSE         = "else";
    public static final String FOR          = "for";
    public static final String WHILE        = "while";
    public static final String DO           = "do";
    public static final String DEFAULT      = "default";
    public static final String TRY          = "try";
    public static final String CATCH        = "catch";
    public static final String FINALLY      = "finally";
    public static final String SWITCH       = "switch";
    public static final String SYNCHRONIZED = "synchronized";
    public static final String RETURN       = "return";
    public static final String THROW        = "throw";
    public static final String BREAK        = "break";
    public static final String CONTINUE     = "continue";
    public static final String ASSERT       = "assert";
    public static final String MODIFY       = "modify";
    public static final String STATIC       = "static";

    public static final String PUBLIC       = "public";
    public static final String PROTECTED    = "protected";
    public static final String PRIVATE      = "private";
    public static final String ABSTRACT     = "abstract";
    public static final String NATIVE       = "native";
    public static final String TRANSIENT    = "transient";
    public static final String VOLATILE     = "volatile";
    public static final String STRICTFP     = "strictfp";
    public static final String THROWS       = "throws";
    public static final String INTERFACE    = "interface";
    public static final String ENUM         = "enum";
    public static final String IMPLEMENTS   = "implements";

    public static boolean isOperator( final String operator,
                                      final boolean negated ) {
        return Operator.determineOperator( operator,
                                           negated ) != null;
    }

}
