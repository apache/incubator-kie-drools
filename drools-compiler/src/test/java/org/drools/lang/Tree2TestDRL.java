// $ANTLR 3.0.1 /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g 2008-06-05 13:52:46

	package org.drools.lang;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class Tree2TestDRL extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_COMPILATION_UNIT", "VT_FUNCTION_IMPORT", "VT_FACT", "VT_CONSTRAINTS", "VT_LABEL", "VT_QUERY_ID", "VT_TEMPLATE_ID", "VT_RULE_ID", "VT_ENTRYPOINT_ID", "VT_SLOT_ID", "VT_SLOT", "VT_RULE_ATTRIBUTES", "VT_RHS_CHUNK", "VT_CURLY_CHUNK", "VT_SQUARE_CHUNK", "VT_PAREN_CHUNK", "VT_AND_IMPLICIT", "VT_AND_PREFIX", "VT_OR_PREFIX", "VT_AND_INFIX", "VT_OR_INFIX", "VT_ACCUMULATE_INIT_CLAUSE", "VT_ACCUMULATE_ID_CLAUSE", "VT_FROM_SOURCE", "VT_EXPRESSION_CHAIN", "VT_PATTERN", "VT_FACT_BINDING", "VT_FACT_OR", "VT_BIND_FIELD", "VT_FIELD", "VT_ACCESSOR_PATH", "VT_ACCESSOR_ELEMENT", "VT_DATA_TYPE", "VT_PATTERN_TYPE", "VT_PACKAGE_ID", "VT_IMPORT_ID", "VT_GLOBAL_ID", "VT_FUNCTION_ID", "VT_PARAM_LIST", "VK_DATE_EFFECTIVE", "VK_DATE_EXPIRES", "VK_LOCK_ON_ACTIVE", "VK_NO_LOOP", "VK_AUTO_FOCUS", "VK_ACTIVATION_GROUP", "VK_AGENDA_GROUP", "VK_RULEFLOW_GROUP", "VK_DURATION", "VK_DIALECT", "VK_SALIENCE", "VK_ENABLED", "VK_ATTRIBUTES", "VK_WHEN", "VK_RULE", "VK_IMPORT", "VK_PACKAGE", "VK_TEMPLATE", "VK_QUERY", "VK_DECLARE", "VK_FUNCTION", "VK_GLOBAL", "VK_EVAL", "VK_CONTAINS", "VK_MATCHES", "VK_EXCLUDES", "VK_SOUNDSLIKE", "VK_MEMBEROF", "VK_ENTRY_POINT", "VK_NOT", "VK_IN", "VK_OR", "VK_AND", "VK_EXISTS", "VK_FORALL", "VK_FROM", "VK_ACCUMULATE", "VK_INIT", "VK_ACTION", "VK_REVERSE", "VK_RESULT", "VK_COLLECT", "SEMICOLON", "ID", "DOT", "DOT_STAR", "END", "STRING", "LEFT_PAREN", "COMMA", "RIGHT_PAREN", "COLON", "BOOL", "INT", "DOUBLE_PIPE", "DOUBLE_AMPER", "ARROW", "EQUAL", "GREATER", "GREATER_EQUAL", "LESS", "LESS_EQUAL", "NOT_EQUAL", "GRAVE_ACCENT", "FLOAT", "NULL", "LEFT_SQUARE", "RIGHT_SQUARE", "THEN", "LEFT_CURLY", "RIGHT_CURLY", "MISC", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT"
    };
    public static final int COMMA=92;
    public static final int VT_PATTERN_TYPE=37;
    public static final int VT_ACCUMULATE_ID_CLAUSE=26;
    public static final int VK_DIALECT=52;
    public static final int VK_FUNCTION=63;
    public static final int END=89;
    public static final int HexDigit=118;
    public static final int VK_ATTRIBUTES=55;
    public static final int VT_EXPRESSION_CHAIN=28;
    public static final int VK_ACCUMULATE=79;
    public static final int MISC=114;
    public static final int VT_AND_PREFIX=21;
    public static final int VK_QUERY=61;
    public static final int THEN=111;
    public static final int VK_AUTO_FOCUS=47;
    public static final int DOT=87;
    public static final int VK_IMPORT=58;
    public static final int VT_SLOT=14;
    public static final int VT_PACKAGE_ID=38;
    public static final int LEFT_SQUARE=109;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=121;
    public static final int VT_DATA_TYPE=36;
    public static final int VK_MATCHES=67;
    public static final int VT_FACT=6;
    public static final int LEFT_CURLY=112;
    public static final int LEFT_PAREN=91;
    public static final int DOUBLE_AMPER=98;
    public static final int VT_QUERY_ID=9;
    public static final int VT_ACCESSOR_PATH=34;
    public static final int VT_LABEL=8;
    public static final int VT_ENTRYPOINT_ID=12;
    public static final int VK_SOUNDSLIKE=69;
    public static final int VK_SALIENCE=53;
    public static final int VT_FIELD=33;
    public static final int WS=116;
    public static final int STRING=90;
    public static final int VK_AND=75;
    public static final int VT_ACCESSOR_ELEMENT=35;
    public static final int VK_GLOBAL=64;
    public static final int VT_ACCUMULATE_INIT_CLAUSE=25;
    public static final int VK_REVERSE=82;
    public static final int GRAVE_ACCENT=106;
    public static final int VK_DURATION=51;
    public static final int VT_SQUARE_CHUNK=18;
    public static final int VK_FORALL=77;
    public static final int VT_COMPILATION_UNIT=4;
    public static final int VT_PAREN_CHUNK=19;
    public static final int VK_ENABLED=54;
    public static final int VK_RESULT=83;
    public static final int UnicodeEscape=119;
    public static final int VK_PACKAGE=59;
    public static final int VT_RULE_ID=11;
    public static final int EQUAL=100;
    public static final int VK_NO_LOOP=46;
    public static final int SEMICOLON=85;
    public static final int VK_TEMPLATE=60;
    public static final int VT_AND_IMPLICIT=20;
    public static final int NULL=108;
    public static final int COLON=94;
    public static final int MULTI_LINE_COMMENT=123;
    public static final int VT_RULE_ATTRIBUTES=15;
    public static final int RIGHT_SQUARE=110;
    public static final int VK_AGENDA_GROUP=49;
    public static final int VT_FACT_OR=31;
    public static final int VK_NOT=72;
    public static final int VK_DATE_EXPIRES=44;
    public static final int ARROW=99;
    public static final int FLOAT=107;
    public static final int VT_SLOT_ID=13;
    public static final int VT_CURLY_CHUNK=17;
    public static final int VT_OR_PREFIX=22;
    public static final int DOUBLE_PIPE=97;
    public static final int LESS=103;
    public static final int VT_PATTERN=29;
    public static final int VK_DATE_EFFECTIVE=43;
    public static final int EscapeSequence=117;
    public static final int VK_EXISTS=76;
    public static final int INT=96;
    public static final int VT_BIND_FIELD=32;
    public static final int VK_RULE=57;
    public static final int VK_EVAL=65;
    public static final int VK_COLLECT=84;
    public static final int GREATER=101;
    public static final int VT_FACT_BINDING=30;
    public static final int ID=86;
    public static final int NOT_EQUAL=105;
    public static final int RIGHT_CURLY=113;
    public static final int VK_ENTRY_POINT=71;
    public static final int VT_AND_INFIX=23;
    public static final int VT_PARAM_LIST=42;
    public static final int BOOL=95;
    public static final int VT_FROM_SOURCE=27;
    public static final int VK_CONTAINS=66;
    public static final int VK_LOCK_ON_ACTIVE=45;
    public static final int VT_FUNCTION_IMPORT=5;
    public static final int VK_IN=73;
    public static final int VT_RHS_CHUNK=16;
    public static final int VK_MEMBEROF=70;
    public static final int GREATER_EQUAL=102;
    public static final int VT_OR_INFIX=24;
    public static final int DOT_STAR=88;
    public static final int VK_OR=74;
    public static final int VT_GLOBAL_ID=40;
    public static final int LESS_EQUAL=104;
    public static final int VK_WHEN=56;
    public static final int VK_RULEFLOW_GROUP=50;
    public static final int VT_FUNCTION_ID=41;
    public static final int EOF=-1;
    public static final int VT_CONSTRAINTS=7;
    public static final int VT_IMPORT_ID=39;
    public static final int EOL=115;
    public static final int VK_INIT=80;
    public static final int VK_ACTIVATION_GROUP=48;
    public static final int OctalEscape=120;
    public static final int VK_ACTION=81;
    public static final int VK_FROM=78;
    public static final int VK_EXCLUDES=68;
    public static final int RIGHT_PAREN=93;
    public static final int VT_TEMPLATE_ID=10;
    public static final int VK_DECLARE=62;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=122;

        public Tree2TestDRL(TreeNodeStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "/Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g"; }



    // $ANTLR start compilation_unit
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:13:1: compilation_unit : ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) ;
    public final void compilation_unit() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:14:2: ( ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:14:4: ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* )
            {
            match(input,VT_COMPILATION_UNIT,FOLLOW_VT_COMPILATION_UNIT_in_compilation_unit43); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:14:26: ( package_statement )?
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==VK_PACKAGE) ) {
                    alt1=1;
                }
                switch (alt1) {
                    case 1 :
                        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:14:26: package_statement
                        {
                        pushFollow(FOLLOW_package_statement_in_compilation_unit45);
                        package_statement();
                        _fsp--;


                        }
                        break;

                }

                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:14:45: ( statement )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==VT_FUNCTION_IMPORT||(LA2_0>=VK_DATE_EFFECTIVE && LA2_0<=VK_ENABLED)||(LA2_0>=VK_RULE && LA2_0<=VK_IMPORT)||(LA2_0>=VK_TEMPLATE && LA2_0<=VK_QUERY)||(LA2_0>=VK_FUNCTION && LA2_0<=VK_GLOBAL)) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:14:45: statement
                	    {
                	    pushFollow(FOLLOW_statement_in_compilation_unit48);
                	    statement();
                	    _fsp--;


                	    }
                	    break;

                	default :
                	    break loop2;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end compilation_unit


    // $ANTLR start package_statement
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:17:1: package_statement : ^( VK_PACKAGE package_id ) ;
    public final void package_statement() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:18:2: ( ^( VK_PACKAGE package_id ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:18:4: ^( VK_PACKAGE package_id )
            {
            match(input,VK_PACKAGE,FOLLOW_VK_PACKAGE_in_package_statement63); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_package_id_in_package_statement65);
            package_id();
            _fsp--;


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end package_statement


    // $ANTLR start package_id
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:21:1: package_id : ^( VT_PACKAGE_ID ( ID )+ ) ;
    public final void package_id() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:22:2: ( ^( VT_PACKAGE_ID ( ID )+ ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:22:4: ^( VT_PACKAGE_ID ( ID )+ )
            {
            match(input,VT_PACKAGE_ID,FOLLOW_VT_PACKAGE_ID_in_package_id78); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:22:20: ( ID )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==ID) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:22:20: ID
            	    {
            	    match(input,ID,FOLLOW_ID_in_package_id80); 

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end package_id


    // $ANTLR start statement
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:25:1: statement : ( rule_attribute | function_import_statement | import_statement | global | function | template | rule | query );
    public final void statement() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:26:2: ( rule_attribute | function_import_statement | import_statement | global | function | template | rule | query )
            int alt4=8;
            switch ( input.LA(1) ) {
            case VK_DATE_EFFECTIVE:
            case VK_DATE_EXPIRES:
            case VK_LOCK_ON_ACTIVE:
            case VK_NO_LOOP:
            case VK_AUTO_FOCUS:
            case VK_ACTIVATION_GROUP:
            case VK_AGENDA_GROUP:
            case VK_RULEFLOW_GROUP:
            case VK_DURATION:
            case VK_DIALECT:
            case VK_SALIENCE:
            case VK_ENABLED:
                {
                alt4=1;
                }
                break;
            case VT_FUNCTION_IMPORT:
                {
                alt4=2;
                }
                break;
            case VK_IMPORT:
                {
                alt4=3;
                }
                break;
            case VK_GLOBAL:
                {
                alt4=4;
                }
                break;
            case VK_FUNCTION:
                {
                alt4=5;
                }
                break;
            case VK_TEMPLATE:
                {
                alt4=6;
                }
                break;
            case VK_RULE:
                {
                alt4=7;
                }
                break;
            case VK_QUERY:
                {
                alt4=8;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("25:1: statement : ( rule_attribute | function_import_statement | import_statement | global | function | template | rule | query );", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:26:4: rule_attribute
                    {
                    pushFollow(FOLLOW_rule_attribute_in_statement93);
                    rule_attribute();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:27:4: function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement98);
                    function_import_statement();
                    _fsp--;


                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:28:4: import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement104);
                    import_statement();
                    _fsp--;


                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:29:4: global
                    {
                    pushFollow(FOLLOW_global_in_statement110);
                    global();
                    _fsp--;


                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:30:4: function
                    {
                    pushFollow(FOLLOW_function_in_statement116);
                    function();
                    _fsp--;


                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:31:4: template
                    {
                    pushFollow(FOLLOW_template_in_statement121);
                    template();
                    _fsp--;


                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:32:4: rule
                    {
                    pushFollow(FOLLOW_rule_in_statement126);
                    rule();
                    _fsp--;


                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:33:4: query
                    {
                    pushFollow(FOLLOW_query_in_statement131);
                    query();
                    _fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end statement


    // $ANTLR start import_statement
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:36:1: import_statement : ^( VK_IMPORT import_name ) ;
    public final void import_statement() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:37:2: ( ^( VK_IMPORT import_name ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:37:4: ^( VK_IMPORT import_name )
            {
            match(input,VK_IMPORT,FOLLOW_VK_IMPORT_in_import_statement143); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_import_name_in_import_statement145);
            import_name();
            _fsp--;


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end import_statement


    // $ANTLR start function_import_statement
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:40:1: function_import_statement : ^( VT_FUNCTION_IMPORT VK_FUNCTION import_name ) ;
    public final void function_import_statement() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:41:2: ( ^( VT_FUNCTION_IMPORT VK_FUNCTION import_name ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:41:4: ^( VT_FUNCTION_IMPORT VK_FUNCTION import_name )
            {
            match(input,VT_FUNCTION_IMPORT,FOLLOW_VT_FUNCTION_IMPORT_in_function_import_statement158); 

            match(input, Token.DOWN, null); 
            match(input,VK_FUNCTION,FOLLOW_VK_FUNCTION_in_function_import_statement160); 
            pushFollow(FOLLOW_import_name_in_function_import_statement162);
            import_name();
            _fsp--;


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end function_import_statement


    // $ANTLR start import_name
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:44:1: import_name : ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) ;
    public final void import_name() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:45:2: ( ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:45:4: ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? )
            {
            match(input,VT_IMPORT_ID,FOLLOW_VT_IMPORT_ID_in_import_name175); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:45:19: ( ID )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==ID) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:45:19: ID
            	    {
            	    match(input,ID,FOLLOW_ID_in_import_name177); 

            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
            } while (true);

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:45:23: ( DOT_STAR )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==DOT_STAR) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:45:23: DOT_STAR
                    {
                    match(input,DOT_STAR,FOLLOW_DOT_STAR_in_import_name180); 

                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end import_name


    // $ANTLR start global
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:48:1: global : ^( VK_GLOBAL data_type VT_GLOBAL_ID ) ;
    public final void global() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:49:2: ( ^( VK_GLOBAL data_type VT_GLOBAL_ID ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:49:4: ^( VK_GLOBAL data_type VT_GLOBAL_ID )
            {
            match(input,VK_GLOBAL,FOLLOW_VK_GLOBAL_in_global194); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_data_type_in_global196);
            data_type();
            _fsp--;

            match(input,VT_GLOBAL_ID,FOLLOW_VT_GLOBAL_ID_in_global198); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end global


    // $ANTLR start function
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:52:1: function : ^( VK_FUNCTION ( data_type )? VT_FUNCTION_ID parameters curly_chunk ) ;
    public final void function() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:53:2: ( ^( VK_FUNCTION ( data_type )? VT_FUNCTION_ID parameters curly_chunk ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:53:4: ^( VK_FUNCTION ( data_type )? VT_FUNCTION_ID parameters curly_chunk )
            {
            match(input,VK_FUNCTION,FOLLOW_VK_FUNCTION_in_function211); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:53:18: ( data_type )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==VT_DATA_TYPE) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:53:18: data_type
                    {
                    pushFollow(FOLLOW_data_type_in_function213);
                    data_type();
                    _fsp--;


                    }
                    break;

            }

            match(input,VT_FUNCTION_ID,FOLLOW_VT_FUNCTION_ID_in_function216); 
            pushFollow(FOLLOW_parameters_in_function218);
            parameters();
            _fsp--;

            pushFollow(FOLLOW_curly_chunk_in_function220);
            curly_chunk();
            _fsp--;


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end function


    // $ANTLR start query
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:56:1: query : ^( VK_QUERY VT_QUERY_ID ( parameters )? lhs_block END ) ;
    public final void query() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:57:2: ( ^( VK_QUERY VT_QUERY_ID ( parameters )? lhs_block END ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:57:4: ^( VK_QUERY VT_QUERY_ID ( parameters )? lhs_block END )
            {
            match(input,VK_QUERY,FOLLOW_VK_QUERY_in_query233); 

            match(input, Token.DOWN, null); 
            match(input,VT_QUERY_ID,FOLLOW_VT_QUERY_ID_in_query235); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:57:27: ( parameters )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==VT_PARAM_LIST) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:57:27: parameters
                    {
                    pushFollow(FOLLOW_parameters_in_query237);
                    parameters();
                    _fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_lhs_block_in_query240);
            lhs_block();
            _fsp--;

            match(input,END,FOLLOW_END_in_query242); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end query


    // $ANTLR start parameters
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:60:1: parameters : ^( VT_PARAM_LIST ( param_definition )* ) ;
    public final void parameters() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:61:2: ( ^( VT_PARAM_LIST ( param_definition )* ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:61:4: ^( VT_PARAM_LIST ( param_definition )* )
            {
            match(input,VT_PARAM_LIST,FOLLOW_VT_PARAM_LIST_in_parameters255); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:61:20: ( param_definition )*
                loop9:
                do {
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==VT_DATA_TYPE||LA9_0==ID) ) {
                        alt9=1;
                    }


                    switch (alt9) {
                	case 1 :
                	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:61:20: param_definition
                	    {
                	    pushFollow(FOLLOW_param_definition_in_parameters257);
                	    param_definition();
                	    _fsp--;


                	    }
                	    break;

                	default :
                	    break loop9;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end parameters


    // $ANTLR start param_definition
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:64:1: param_definition : ( data_type )? argument ;
    public final void param_definition() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:65:2: ( ( data_type )? argument )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:65:4: ( data_type )? argument
            {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:65:4: ( data_type )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==VT_DATA_TYPE) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:65:4: data_type
                    {
                    pushFollow(FOLLOW_data_type_in_param_definition270);
                    data_type();
                    _fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_argument_in_param_definition273);
            argument();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end param_definition


    // $ANTLR start argument
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:68:1: argument : ID ( dimension_definition )* ;
    public final void argument() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:69:2: ( ID ( dimension_definition )* )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:69:4: ID ( dimension_definition )*
            {
            match(input,ID,FOLLOW_ID_in_argument284); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:69:7: ( dimension_definition )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==LEFT_SQUARE) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:69:7: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_argument286);
            	    dimension_definition();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end argument


    // $ANTLR start template
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:73:1: template : ^( VK_TEMPLATE VT_TEMPLATE_ID ( template_slot )+ END ) ;
    public final void template() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:74:2: ( ^( VK_TEMPLATE VT_TEMPLATE_ID ( template_slot )+ END ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:74:4: ^( VK_TEMPLATE VT_TEMPLATE_ID ( template_slot )+ END )
            {
            match(input,VK_TEMPLATE,FOLLOW_VK_TEMPLATE_in_template300); 

            match(input, Token.DOWN, null); 
            match(input,VT_TEMPLATE_ID,FOLLOW_VT_TEMPLATE_ID_in_template302); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:74:33: ( template_slot )+
            int cnt12=0;
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==VT_SLOT) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:74:33: template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template304);
            	    template_slot();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt12 >= 1 ) break loop12;
                        EarlyExitException eee =
                            new EarlyExitException(12, input);
                        throw eee;
                }
                cnt12++;
            } while (true);

            match(input,END,FOLLOW_END_in_template307); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end template


    // $ANTLR start template_slot
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:77:1: template_slot : ^( VT_SLOT data_type VT_SLOT_ID ) ;
    public final void template_slot() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:78:2: ( ^( VT_SLOT data_type VT_SLOT_ID ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:78:4: ^( VT_SLOT data_type VT_SLOT_ID )
            {
            match(input,VT_SLOT,FOLLOW_VT_SLOT_in_template_slot320); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_data_type_in_template_slot322);
            data_type();
            _fsp--;

            match(input,VT_SLOT_ID,FOLLOW_VT_SLOT_ID_in_template_slot324); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end template_slot


    // $ANTLR start rule
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:81:1: rule : ^( VK_RULE VT_RULE_ID ( rule_attributes )? ( when_part )? VT_RHS_CHUNK ) ;
    public final void rule() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:82:2: ( ^( VK_RULE VT_RULE_ID ( rule_attributes )? ( when_part )? VT_RHS_CHUNK ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:82:4: ^( VK_RULE VT_RULE_ID ( rule_attributes )? ( when_part )? VT_RHS_CHUNK )
            {
            match(input,VK_RULE,FOLLOW_VK_RULE_in_rule337); 

            match(input, Token.DOWN, null); 
            match(input,VT_RULE_ID,FOLLOW_VT_RULE_ID_in_rule339); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:82:25: ( rule_attributes )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==VT_RULE_ATTRIBUTES) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:82:25: rule_attributes
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule341);
                    rule_attributes();
                    _fsp--;


                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:82:42: ( when_part )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==VK_WHEN) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:82:42: when_part
                    {
                    pushFollow(FOLLOW_when_part_in_rule344);
                    when_part();
                    _fsp--;


                    }
                    break;

            }

            match(input,VT_RHS_CHUNK,FOLLOW_VT_RHS_CHUNK_in_rule347); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end rule


    // $ANTLR start when_part
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:85:1: when_part : VK_WHEN lhs_block ;
    public final void when_part() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:86:2: ( VK_WHEN lhs_block )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:86:4: VK_WHEN lhs_block
            {
            match(input,VK_WHEN,FOLLOW_VK_WHEN_in_when_part359); 
            pushFollow(FOLLOW_lhs_block_in_when_part361);
            lhs_block();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end when_part


    // $ANTLR start rule_attributes
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:89:1: rule_attributes : ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? ( rule_attribute )+ ) ;
    public final void rule_attributes() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:90:2: ( ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? ( rule_attribute )+ ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:90:4: ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? ( rule_attribute )+ )
            {
            match(input,VT_RULE_ATTRIBUTES,FOLLOW_VT_RULE_ATTRIBUTES_in_rule_attributes373); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:90:25: ( VK_ATTRIBUTES )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==VK_ATTRIBUTES) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:90:25: VK_ATTRIBUTES
                    {
                    match(input,VK_ATTRIBUTES,FOLLOW_VK_ATTRIBUTES_in_rule_attributes375); 

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:90:40: ( rule_attribute )+
            int cnt16=0;
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( ((LA16_0>=VK_DATE_EFFECTIVE && LA16_0<=VK_ENABLED)) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:90:40: rule_attribute
            	    {
            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes378);
            	    rule_attribute();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt16 >= 1 ) break loop16;
                        EarlyExitException eee =
                            new EarlyExitException(16, input);
                        throw eee;
                }
                cnt16++;
            } while (true);


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end rule_attributes


    // $ANTLR start rule_attribute
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:93:1: rule_attribute : ( ^( VK_SALIENCE ( INT | VT_PAREN_CHUNK ) ) | ^( VK_NO_LOOP ( BOOL )? ) | ^( VK_AGENDA_GROUP STRING ) | ^( VK_DURATION INT ) | ^( VK_ACTIVATION_GROUP STRING ) | ^( VK_AUTO_FOCUS ( BOOL )? ) | ^( VK_DATE_EFFECTIVE STRING ) | ^( VK_DATE_EXPIRES STRING ) | ^( VK_ENABLED BOOL ) | ^( VK_RULEFLOW_GROUP STRING ) | ^( VK_LOCK_ON_ACTIVE ( BOOL )? ) | ^( VK_DIALECT STRING ) );
    public final void rule_attribute() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:94:2: ( ^( VK_SALIENCE ( INT | VT_PAREN_CHUNK ) ) | ^( VK_NO_LOOP ( BOOL )? ) | ^( VK_AGENDA_GROUP STRING ) | ^( VK_DURATION INT ) | ^( VK_ACTIVATION_GROUP STRING ) | ^( VK_AUTO_FOCUS ( BOOL )? ) | ^( VK_DATE_EFFECTIVE STRING ) | ^( VK_DATE_EXPIRES STRING ) | ^( VK_ENABLED BOOL ) | ^( VK_RULEFLOW_GROUP STRING ) | ^( VK_LOCK_ON_ACTIVE ( BOOL )? ) | ^( VK_DIALECT STRING ) )
            int alt20=12;
            switch ( input.LA(1) ) {
            case VK_SALIENCE:
                {
                alt20=1;
                }
                break;
            case VK_NO_LOOP:
                {
                alt20=2;
                }
                break;
            case VK_AGENDA_GROUP:
                {
                alt20=3;
                }
                break;
            case VK_DURATION:
                {
                alt20=4;
                }
                break;
            case VK_ACTIVATION_GROUP:
                {
                alt20=5;
                }
                break;
            case VK_AUTO_FOCUS:
                {
                alt20=6;
                }
                break;
            case VK_DATE_EFFECTIVE:
                {
                alt20=7;
                }
                break;
            case VK_DATE_EXPIRES:
                {
                alt20=8;
                }
                break;
            case VK_ENABLED:
                {
                alt20=9;
                }
                break;
            case VK_RULEFLOW_GROUP:
                {
                alt20=10;
                }
                break;
            case VK_LOCK_ON_ACTIVE:
                {
                alt20=11;
                }
                break;
            case VK_DIALECT:
                {
                alt20=12;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("93:1: rule_attribute : ( ^( VK_SALIENCE ( INT | VT_PAREN_CHUNK ) ) | ^( VK_NO_LOOP ( BOOL )? ) | ^( VK_AGENDA_GROUP STRING ) | ^( VK_DURATION INT ) | ^( VK_ACTIVATION_GROUP STRING ) | ^( VK_AUTO_FOCUS ( BOOL )? ) | ^( VK_DATE_EFFECTIVE STRING ) | ^( VK_DATE_EXPIRES STRING ) | ^( VK_ENABLED BOOL ) | ^( VK_RULEFLOW_GROUP STRING ) | ^( VK_LOCK_ON_ACTIVE ( BOOL )? ) | ^( VK_DIALECT STRING ) );", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:94:4: ^( VK_SALIENCE ( INT | VT_PAREN_CHUNK ) )
                    {
                    match(input,VK_SALIENCE,FOLLOW_VK_SALIENCE_in_rule_attribute392); 

                    match(input, Token.DOWN, null); 
                    if ( input.LA(1)==VT_PAREN_CHUNK||input.LA(1)==INT ) {
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_rule_attribute394);    throw mse;
                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:95:4: ^( VK_NO_LOOP ( BOOL )? )
                    {
                    match(input,VK_NO_LOOP,FOLLOW_VK_NO_LOOP_in_rule_attribute406); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:95:17: ( BOOL )?
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( (LA17_0==BOOL) ) {
                            alt17=1;
                        }
                        switch (alt17) {
                            case 1 :
                                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:95:17: BOOL
                                {
                                match(input,BOOL,FOLLOW_BOOL_in_rule_attribute408); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:96:4: ^( VK_AGENDA_GROUP STRING )
                    {
                    match(input,VK_AGENDA_GROUP,FOLLOW_VK_AGENDA_GROUP_in_rule_attribute418); 

                    match(input, Token.DOWN, null); 
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute420); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:97:4: ^( VK_DURATION INT )
                    {
                    match(input,VK_DURATION,FOLLOW_VK_DURATION_in_rule_attribute429); 

                    match(input, Token.DOWN, null); 
                    match(input,INT,FOLLOW_INT_in_rule_attribute431); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:98:4: ^( VK_ACTIVATION_GROUP STRING )
                    {
                    match(input,VK_ACTIVATION_GROUP,FOLLOW_VK_ACTIVATION_GROUP_in_rule_attribute441); 

                    match(input, Token.DOWN, null); 
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute443); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:99:4: ^( VK_AUTO_FOCUS ( BOOL )? )
                    {
                    match(input,VK_AUTO_FOCUS,FOLLOW_VK_AUTO_FOCUS_in_rule_attribute451); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:99:20: ( BOOL )?
                        int alt18=2;
                        int LA18_0 = input.LA(1);

                        if ( (LA18_0==BOOL) ) {
                            alt18=1;
                        }
                        switch (alt18) {
                            case 1 :
                                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:99:20: BOOL
                                {
                                match(input,BOOL,FOLLOW_BOOL_in_rule_attribute453); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:100:4: ^( VK_DATE_EFFECTIVE STRING )
                    {
                    match(input,VK_DATE_EFFECTIVE,FOLLOW_VK_DATE_EFFECTIVE_in_rule_attribute462); 

                    match(input, Token.DOWN, null); 
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute464); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:101:4: ^( VK_DATE_EXPIRES STRING )
                    {
                    match(input,VK_DATE_EXPIRES,FOLLOW_VK_DATE_EXPIRES_in_rule_attribute472); 

                    match(input, Token.DOWN, null); 
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute474); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:102:4: ^( VK_ENABLED BOOL )
                    {
                    match(input,VK_ENABLED,FOLLOW_VK_ENABLED_in_rule_attribute482); 

                    match(input, Token.DOWN, null); 
                    match(input,BOOL,FOLLOW_BOOL_in_rule_attribute484); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 10 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:103:4: ^( VK_RULEFLOW_GROUP STRING )
                    {
                    match(input,VK_RULEFLOW_GROUP,FOLLOW_VK_RULEFLOW_GROUP_in_rule_attribute492); 

                    match(input, Token.DOWN, null); 
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute494); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 11 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:104:4: ^( VK_LOCK_ON_ACTIVE ( BOOL )? )
                    {
                    match(input,VK_LOCK_ON_ACTIVE,FOLLOW_VK_LOCK_ON_ACTIVE_in_rule_attribute502); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:104:24: ( BOOL )?
                        int alt19=2;
                        int LA19_0 = input.LA(1);

                        if ( (LA19_0==BOOL) ) {
                            alt19=1;
                        }
                        switch (alt19) {
                            case 1 :
                                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:104:24: BOOL
                                {
                                match(input,BOOL,FOLLOW_BOOL_in_rule_attribute504); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 12 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:105:4: ^( VK_DIALECT STRING )
                    {
                    match(input,VK_DIALECT,FOLLOW_VK_DIALECT_in_rule_attribute512); 

                    match(input, Token.DOWN, null); 
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute514); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end rule_attribute


    // $ANTLR start lhs_block
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:108:1: lhs_block : ^( VT_AND_IMPLICIT ( lhs )* ) ;
    public final void lhs_block() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:109:2: ( ^( VT_AND_IMPLICIT ( lhs )* ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:109:4: ^( VT_AND_IMPLICIT ( lhs )* )
            {
            match(input,VT_AND_IMPLICIT,FOLLOW_VT_AND_IMPLICIT_in_lhs_block528); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:109:22: ( lhs )*
                loop21:
                do {
                    int alt21=2;
                    int LA21_0 = input.LA(1);

                    if ( ((LA21_0>=VT_AND_PREFIX && LA21_0<=VT_OR_INFIX)||LA21_0==VT_PATTERN||LA21_0==VK_EVAL||LA21_0==VK_NOT||(LA21_0>=VK_EXISTS && LA21_0<=VK_FROM)) ) {
                        alt21=1;
                    }


                    switch (alt21) {
                	case 1 :
                	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:109:22: lhs
                	    {
                	    pushFollow(FOLLOW_lhs_in_lhs_block530);
                	    lhs();
                	    _fsp--;


                	    }
                	    break;

                	default :
                	    break loop21;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end lhs_block


    // $ANTLR start lhs
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:112:1: lhs : ( ^( VT_OR_PREFIX ( lhs )+ ) | ^( VT_OR_INFIX lhs lhs ) | ^( VT_AND_PREFIX ( lhs )+ ) | ^( VT_AND_INFIX lhs lhs ) | ^( VK_EXISTS lhs ) | ^( VK_NOT lhs ) | ^( VK_EVAL VT_PAREN_CHUNK ) | ^( VK_FORALL ( lhs )+ ) | ^( VK_FROM lhs_pattern from_elements ) | lhs_pattern );
    public final void lhs() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:112:5: ( ^( VT_OR_PREFIX ( lhs )+ ) | ^( VT_OR_INFIX lhs lhs ) | ^( VT_AND_PREFIX ( lhs )+ ) | ^( VT_AND_INFIX lhs lhs ) | ^( VK_EXISTS lhs ) | ^( VK_NOT lhs ) | ^( VK_EVAL VT_PAREN_CHUNK ) | ^( VK_FORALL ( lhs )+ ) | ^( VK_FROM lhs_pattern from_elements ) | lhs_pattern )
            int alt25=10;
            switch ( input.LA(1) ) {
            case VT_OR_PREFIX:
                {
                alt25=1;
                }
                break;
            case VT_OR_INFIX:
                {
                alt25=2;
                }
                break;
            case VT_AND_PREFIX:
                {
                alt25=3;
                }
                break;
            case VT_AND_INFIX:
                {
                alt25=4;
                }
                break;
            case VK_EXISTS:
                {
                alt25=5;
                }
                break;
            case VK_NOT:
                {
                alt25=6;
                }
                break;
            case VK_EVAL:
                {
                alt25=7;
                }
                break;
            case VK_FORALL:
                {
                alt25=8;
                }
                break;
            case VK_FROM:
                {
                alt25=9;
                }
                break;
            case VT_PATTERN:
                {
                alt25=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("112:1: lhs : ( ^( VT_OR_PREFIX ( lhs )+ ) | ^( VT_OR_INFIX lhs lhs ) | ^( VT_AND_PREFIX ( lhs )+ ) | ^( VT_AND_INFIX lhs lhs ) | ^( VK_EXISTS lhs ) | ^( VK_NOT lhs ) | ^( VK_EVAL VT_PAREN_CHUNK ) | ^( VK_FORALL ( lhs )+ ) | ^( VK_FROM lhs_pattern from_elements ) | lhs_pattern );", 25, 0, input);

                throw nvae;
            }

            switch (alt25) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:112:7: ^( VT_OR_PREFIX ( lhs )+ )
                    {
                    match(input,VT_OR_PREFIX,FOLLOW_VT_OR_PREFIX_in_lhs543); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:112:22: ( lhs )+
                    int cnt22=0;
                    loop22:
                    do {
                        int alt22=2;
                        int LA22_0 = input.LA(1);

                        if ( ((LA22_0>=VT_AND_PREFIX && LA22_0<=VT_OR_INFIX)||LA22_0==VT_PATTERN||LA22_0==VK_EVAL||LA22_0==VK_NOT||(LA22_0>=VK_EXISTS && LA22_0<=VK_FROM)) ) {
                            alt22=1;
                        }


                        switch (alt22) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:112:22: lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs545);
                    	    lhs();
                    	    _fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt22 >= 1 ) break loop22;
                                EarlyExitException eee =
                                    new EarlyExitException(22, input);
                                throw eee;
                        }
                        cnt22++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:113:4: ^( VT_OR_INFIX lhs lhs )
                    {
                    match(input,VT_OR_INFIX,FOLLOW_VT_OR_INFIX_in_lhs553); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs555);
                    lhs();
                    _fsp--;

                    pushFollow(FOLLOW_lhs_in_lhs557);
                    lhs();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:114:4: ^( VT_AND_PREFIX ( lhs )+ )
                    {
                    match(input,VT_AND_PREFIX,FOLLOW_VT_AND_PREFIX_in_lhs564); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:114:20: ( lhs )+
                    int cnt23=0;
                    loop23:
                    do {
                        int alt23=2;
                        int LA23_0 = input.LA(1);

                        if ( ((LA23_0>=VT_AND_PREFIX && LA23_0<=VT_OR_INFIX)||LA23_0==VT_PATTERN||LA23_0==VK_EVAL||LA23_0==VK_NOT||(LA23_0>=VK_EXISTS && LA23_0<=VK_FROM)) ) {
                            alt23=1;
                        }


                        switch (alt23) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:114:20: lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs566);
                    	    lhs();
                    	    _fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt23 >= 1 ) break loop23;
                                EarlyExitException eee =
                                    new EarlyExitException(23, input);
                                throw eee;
                        }
                        cnt23++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:115:4: ^( VT_AND_INFIX lhs lhs )
                    {
                    match(input,VT_AND_INFIX,FOLLOW_VT_AND_INFIX_in_lhs574); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs576);
                    lhs();
                    _fsp--;

                    pushFollow(FOLLOW_lhs_in_lhs578);
                    lhs();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:116:4: ^( VK_EXISTS lhs )
                    {
                    match(input,VK_EXISTS,FOLLOW_VK_EXISTS_in_lhs585); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs587);
                    lhs();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:117:4: ^( VK_NOT lhs )
                    {
                    match(input,VK_NOT,FOLLOW_VK_NOT_in_lhs594); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs596);
                    lhs();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:118:4: ^( VK_EVAL VT_PAREN_CHUNK )
                    {
                    match(input,VK_EVAL,FOLLOW_VK_EVAL_in_lhs603); 

                    match(input, Token.DOWN, null); 
                    match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_lhs605); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:119:4: ^( VK_FORALL ( lhs )+ )
                    {
                    match(input,VK_FORALL,FOLLOW_VK_FORALL_in_lhs612); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:119:16: ( lhs )+
                    int cnt24=0;
                    loop24:
                    do {
                        int alt24=2;
                        int LA24_0 = input.LA(1);

                        if ( ((LA24_0>=VT_AND_PREFIX && LA24_0<=VT_OR_INFIX)||LA24_0==VT_PATTERN||LA24_0==VK_EVAL||LA24_0==VK_NOT||(LA24_0>=VK_EXISTS && LA24_0<=VK_FROM)) ) {
                            alt24=1;
                        }


                        switch (alt24) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:119:16: lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs614);
                    	    lhs();
                    	    _fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt24 >= 1 ) break loop24;
                                EarlyExitException eee =
                                    new EarlyExitException(24, input);
                                throw eee;
                        }
                        cnt24++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:120:4: ^( VK_FROM lhs_pattern from_elements )
                    {
                    match(input,VK_FROM,FOLLOW_VK_FROM_in_lhs622); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_pattern_in_lhs624);
                    lhs_pattern();
                    _fsp--;

                    pushFollow(FOLLOW_from_elements_in_lhs626);
                    from_elements();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 10 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:121:4: lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs632);
                    lhs_pattern();
                    _fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end lhs


    // $ANTLR start from_elements
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:124:1: from_elements : ( ^( VK_ACCUMULATE lhs ( accumulate_init_clause | accumulate_id_clause ) ) | ^( VK_COLLECT lhs ) | ^( VK_ENTRY_POINT VT_ENTRYPOINT_ID ) | ^( VT_FROM_SOURCE ID ( VT_PAREN_CHUNK )? ( expression_chain )? ) );
    public final void from_elements() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:125:2: ( ^( VK_ACCUMULATE lhs ( accumulate_init_clause | accumulate_id_clause ) ) | ^( VK_COLLECT lhs ) | ^( VK_ENTRY_POINT VT_ENTRYPOINT_ID ) | ^( VT_FROM_SOURCE ID ( VT_PAREN_CHUNK )? ( expression_chain )? ) )
            int alt29=4;
            switch ( input.LA(1) ) {
            case VK_ACCUMULATE:
                {
                alt29=1;
                }
                break;
            case VK_COLLECT:
                {
                alt29=2;
                }
                break;
            case VK_ENTRY_POINT:
                {
                alt29=3;
                }
                break;
            case VT_FROM_SOURCE:
                {
                alt29=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("124:1: from_elements : ( ^( VK_ACCUMULATE lhs ( accumulate_init_clause | accumulate_id_clause ) ) | ^( VK_COLLECT lhs ) | ^( VK_ENTRY_POINT VT_ENTRYPOINT_ID ) | ^( VT_FROM_SOURCE ID ( VT_PAREN_CHUNK )? ( expression_chain )? ) );", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:125:4: ^( VK_ACCUMULATE lhs ( accumulate_init_clause | accumulate_id_clause ) )
                    {
                    match(input,VK_ACCUMULATE,FOLLOW_VK_ACCUMULATE_in_from_elements644); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_from_elements646);
                    lhs();
                    _fsp--;

                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:125:24: ( accumulate_init_clause | accumulate_id_clause )
                    int alt26=2;
                    int LA26_0 = input.LA(1);

                    if ( (LA26_0==VT_ACCUMULATE_INIT_CLAUSE) ) {
                        alt26=1;
                    }
                    else if ( (LA26_0==VT_ACCUMULATE_ID_CLAUSE) ) {
                        alt26=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("125:24: ( accumulate_init_clause | accumulate_id_clause )", 26, 0, input);

                        throw nvae;
                    }
                    switch (alt26) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:125:25: accumulate_init_clause
                            {
                            pushFollow(FOLLOW_accumulate_init_clause_in_from_elements649);
                            accumulate_init_clause();
                            _fsp--;


                            }
                            break;
                        case 2 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:125:48: accumulate_id_clause
                            {
                            pushFollow(FOLLOW_accumulate_id_clause_in_from_elements651);
                            accumulate_id_clause();
                            _fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:126:4: ^( VK_COLLECT lhs )
                    {
                    match(input,VK_COLLECT,FOLLOW_VK_COLLECT_in_from_elements659); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_from_elements661);
                    lhs();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:127:4: ^( VK_ENTRY_POINT VT_ENTRYPOINT_ID )
                    {
                    match(input,VK_ENTRY_POINT,FOLLOW_VK_ENTRY_POINT_in_from_elements668); 

                    match(input, Token.DOWN, null); 
                    match(input,VT_ENTRYPOINT_ID,FOLLOW_VT_ENTRYPOINT_ID_in_from_elements670); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:128:4: ^( VT_FROM_SOURCE ID ( VT_PAREN_CHUNK )? ( expression_chain )? )
                    {
                    match(input,VT_FROM_SOURCE,FOLLOW_VT_FROM_SOURCE_in_from_elements677); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_from_elements679); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:128:24: ( VT_PAREN_CHUNK )?
                    int alt27=2;
                    int LA27_0 = input.LA(1);

                    if ( (LA27_0==VT_PAREN_CHUNK) ) {
                        alt27=1;
                    }
                    switch (alt27) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:128:24: VT_PAREN_CHUNK
                            {
                            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_from_elements681); 

                            }
                            break;

                    }

                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:128:40: ( expression_chain )?
                    int alt28=2;
                    int LA28_0 = input.LA(1);

                    if ( (LA28_0==VT_EXPRESSION_CHAIN) ) {
                        alt28=1;
                    }
                    switch (alt28) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:128:40: expression_chain
                            {
                            pushFollow(FOLLOW_expression_chain_in_from_elements684);
                            expression_chain();
                            _fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end from_elements


    // $ANTLR start accumulate_init_clause
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:131:1: accumulate_init_clause : ^( VT_ACCUMULATE_INIT_CLAUSE ^( VK_INIT VT_PAREN_CHUNK ) ^( VK_ACTION VT_PAREN_CHUNK ) ( accumulate_init_reverse_clause )? ^( VK_RESULT VT_PAREN_CHUNK ) ) ;
    public final void accumulate_init_clause() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:132:2: ( ^( VT_ACCUMULATE_INIT_CLAUSE ^( VK_INIT VT_PAREN_CHUNK ) ^( VK_ACTION VT_PAREN_CHUNK ) ( accumulate_init_reverse_clause )? ^( VK_RESULT VT_PAREN_CHUNK ) ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:132:4: ^( VT_ACCUMULATE_INIT_CLAUSE ^( VK_INIT VT_PAREN_CHUNK ) ^( VK_ACTION VT_PAREN_CHUNK ) ( accumulate_init_reverse_clause )? ^( VK_RESULT VT_PAREN_CHUNK ) )
            {
            match(input,VT_ACCUMULATE_INIT_CLAUSE,FOLLOW_VT_ACCUMULATE_INIT_CLAUSE_in_accumulate_init_clause698); 

            match(input, Token.DOWN, null); 
            match(input,VK_INIT,FOLLOW_VK_INIT_in_accumulate_init_clause705); 

            match(input, Token.DOWN, null); 
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause707); 

            match(input, Token.UP, null); 
            match(input,VK_ACTION,FOLLOW_VK_ACTION_in_accumulate_init_clause715); 

            match(input, Token.DOWN, null); 
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause717); 

            match(input, Token.UP, null); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:135:4: ( accumulate_init_reverse_clause )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==VK_REVERSE) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:135:4: accumulate_init_reverse_clause
                    {
                    pushFollow(FOLLOW_accumulate_init_reverse_clause_in_accumulate_init_clause724);
                    accumulate_init_reverse_clause();
                    _fsp--;


                    }
                    break;

            }

            match(input,VK_RESULT,FOLLOW_VK_RESULT_in_accumulate_init_clause731); 

            match(input, Token.DOWN, null); 
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause733); 

            match(input, Token.UP, null); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end accumulate_init_clause


    // $ANTLR start accumulate_init_reverse_clause
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:139:1: accumulate_init_reverse_clause : ^( VK_REVERSE VT_PAREN_CHUNK ) ;
    public final void accumulate_init_reverse_clause() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:140:2: ( ^( VK_REVERSE VT_PAREN_CHUNK ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:140:4: ^( VK_REVERSE VT_PAREN_CHUNK )
            {
            match(input,VK_REVERSE,FOLLOW_VK_REVERSE_in_accumulate_init_reverse_clause747); 

            match(input, Token.DOWN, null); 
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_reverse_clause749); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end accumulate_init_reverse_clause


    // $ANTLR start accumulate_id_clause
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:144:1: accumulate_id_clause : ^( VT_ACCUMULATE_ID_CLAUSE ID VT_PAREN_CHUNK ) ;
    public final void accumulate_id_clause() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:145:2: ( ^( VT_ACCUMULATE_ID_CLAUSE ID VT_PAREN_CHUNK ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:145:4: ^( VT_ACCUMULATE_ID_CLAUSE ID VT_PAREN_CHUNK )
            {
            match(input,VT_ACCUMULATE_ID_CLAUSE,FOLLOW_VT_ACCUMULATE_ID_CLAUSE_in_accumulate_id_clause763); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_accumulate_id_clause765); 
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_id_clause767); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end accumulate_id_clause


    // $ANTLR start lhs_pattern
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:148:1: lhs_pattern : ^( VT_PATTERN fact_expression ) ;
    public final void lhs_pattern() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:149:2: ( ^( VT_PATTERN fact_expression ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:149:4: ^( VT_PATTERN fact_expression )
            {
            match(input,VT_PATTERN,FOLLOW_VT_PATTERN_in_lhs_pattern780); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_fact_expression_in_lhs_pattern782);
            fact_expression();
            _fsp--;


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end lhs_pattern


    // $ANTLR start fact_expression
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:152:1: fact_expression : ( ^( DOUBLE_PIPE fact_expression fact_expression ) | ^( DOUBLE_AMPER fact_expression fact_expression ) | ^( VT_FACT_BINDING VT_LABEL fact_expression ) | ^( VT_FACT pattern_type ( fact_expression )* ) | ^( VT_FACT_OR fact_expression fact_expression ) | ^( VK_EVAL VT_PAREN_CHUNK ) | ^( VK_IN ( VK_NOT )? ( fact_expression )+ ) | ^( EQUAL fact_expression ) | ^( GREATER fact_expression ) | ^( GREATER_EQUAL fact_expression ) | ^( LESS fact_expression ) | ^( LESS_EQUAL fact_expression ) | ^( NOT_EQUAL fact_expression ) | ^( VK_CONTAINS ( VK_NOT )? fact_expression ) | ^( VK_EXCLUDES ( VK_NOT )? fact_expression ) | ^( VK_MATCHES ( VK_NOT )? fact_expression ) | ^( VK_SOUNDSLIKE ( VK_NOT )? fact_expression ) | ^( VK_MEMBEROF ( VK_NOT )? fact_expression ) | ^( ID ( VK_NOT )? ( VT_SQUARE_CHUNK )? fact_expression ) | ^( VT_BIND_FIELD VT_LABEL fact_expression ) | ^( VT_FIELD fact_expression ( fact_expression )? ) | ^( VT_ACCESSOR_PATH ( accessor_element )+ ) | STRING | INT | FLOAT | BOOL | NULL | VT_PAREN_CHUNK );
    public final void fact_expression() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:153:2: ( ^( DOUBLE_PIPE fact_expression fact_expression ) | ^( DOUBLE_AMPER fact_expression fact_expression ) | ^( VT_FACT_BINDING VT_LABEL fact_expression ) | ^( VT_FACT pattern_type ( fact_expression )* ) | ^( VT_FACT_OR fact_expression fact_expression ) | ^( VK_EVAL VT_PAREN_CHUNK ) | ^( VK_IN ( VK_NOT )? ( fact_expression )+ ) | ^( EQUAL fact_expression ) | ^( GREATER fact_expression ) | ^( GREATER_EQUAL fact_expression ) | ^( LESS fact_expression ) | ^( LESS_EQUAL fact_expression ) | ^( NOT_EQUAL fact_expression ) | ^( VK_CONTAINS ( VK_NOT )? fact_expression ) | ^( VK_EXCLUDES ( VK_NOT )? fact_expression ) | ^( VK_MATCHES ( VK_NOT )? fact_expression ) | ^( VK_SOUNDSLIKE ( VK_NOT )? fact_expression ) | ^( VK_MEMBEROF ( VK_NOT )? fact_expression ) | ^( ID ( VK_NOT )? ( VT_SQUARE_CHUNK )? fact_expression ) | ^( VT_BIND_FIELD VT_LABEL fact_expression ) | ^( VT_FIELD fact_expression ( fact_expression )? ) | ^( VT_ACCESSOR_PATH ( accessor_element )+ ) | STRING | INT | FLOAT | BOOL | NULL | VT_PAREN_CHUNK )
            int alt43=28;
            switch ( input.LA(1) ) {
            case DOUBLE_PIPE:
                {
                alt43=1;
                }
                break;
            case DOUBLE_AMPER:
                {
                alt43=2;
                }
                break;
            case VT_FACT_BINDING:
                {
                alt43=3;
                }
                break;
            case VT_FACT:
                {
                alt43=4;
                }
                break;
            case VT_FACT_OR:
                {
                alt43=5;
                }
                break;
            case VK_EVAL:
                {
                alt43=6;
                }
                break;
            case VK_IN:
                {
                alt43=7;
                }
                break;
            case EQUAL:
                {
                alt43=8;
                }
                break;
            case GREATER:
                {
                alt43=9;
                }
                break;
            case GREATER_EQUAL:
                {
                alt43=10;
                }
                break;
            case LESS:
                {
                alt43=11;
                }
                break;
            case LESS_EQUAL:
                {
                alt43=12;
                }
                break;
            case NOT_EQUAL:
                {
                alt43=13;
                }
                break;
            case VK_CONTAINS:
                {
                alt43=14;
                }
                break;
            case VK_EXCLUDES:
                {
                alt43=15;
                }
                break;
            case VK_MATCHES:
                {
                alt43=16;
                }
                break;
            case VK_SOUNDSLIKE:
                {
                alt43=17;
                }
                break;
            case VK_MEMBEROF:
                {
                alt43=18;
                }
                break;
            case ID:
                {
                alt43=19;
                }
                break;
            case VT_BIND_FIELD:
                {
                alt43=20;
                }
                break;
            case VT_FIELD:
                {
                alt43=21;
                }
                break;
            case VT_ACCESSOR_PATH:
                {
                alt43=22;
                }
                break;
            case STRING:
                {
                alt43=23;
                }
                break;
            case INT:
                {
                alt43=24;
                }
                break;
            case FLOAT:
                {
                alt43=25;
                }
                break;
            case BOOL:
                {
                alt43=26;
                }
                break;
            case NULL:
                {
                alt43=27;
                }
                break;
            case VT_PAREN_CHUNK:
                {
                alt43=28;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("152:1: fact_expression : ( ^( DOUBLE_PIPE fact_expression fact_expression ) | ^( DOUBLE_AMPER fact_expression fact_expression ) | ^( VT_FACT_BINDING VT_LABEL fact_expression ) | ^( VT_FACT pattern_type ( fact_expression )* ) | ^( VT_FACT_OR fact_expression fact_expression ) | ^( VK_EVAL VT_PAREN_CHUNK ) | ^( VK_IN ( VK_NOT )? ( fact_expression )+ ) | ^( EQUAL fact_expression ) | ^( GREATER fact_expression ) | ^( GREATER_EQUAL fact_expression ) | ^( LESS fact_expression ) | ^( LESS_EQUAL fact_expression ) | ^( NOT_EQUAL fact_expression ) | ^( VK_CONTAINS ( VK_NOT )? fact_expression ) | ^( VK_EXCLUDES ( VK_NOT )? fact_expression ) | ^( VK_MATCHES ( VK_NOT )? fact_expression ) | ^( VK_SOUNDSLIKE ( VK_NOT )? fact_expression ) | ^( VK_MEMBEROF ( VK_NOT )? fact_expression ) | ^( ID ( VK_NOT )? ( VT_SQUARE_CHUNK )? fact_expression ) | ^( VT_BIND_FIELD VT_LABEL fact_expression ) | ^( VT_FIELD fact_expression ( fact_expression )? ) | ^( VT_ACCESSOR_PATH ( accessor_element )+ ) | STRING | INT | FLOAT | BOOL | NULL | VT_PAREN_CHUNK );", 43, 0, input);

                throw nvae;
            }

            switch (alt43) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:153:4: ^( DOUBLE_PIPE fact_expression fact_expression )
                    {
                    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_fact_expression795); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression797);
                    fact_expression();
                    _fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression799);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:154:4: ^( DOUBLE_AMPER fact_expression fact_expression )
                    {
                    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_fact_expression806); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression808);
                    fact_expression();
                    _fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression810);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:155:4: ^( VT_FACT_BINDING VT_LABEL fact_expression )
                    {
                    match(input,VT_FACT_BINDING,FOLLOW_VT_FACT_BINDING_in_fact_expression817); 

                    match(input, Token.DOWN, null); 
                    match(input,VT_LABEL,FOLLOW_VT_LABEL_in_fact_expression819); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression821);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:156:4: ^( VT_FACT pattern_type ( fact_expression )* )
                    {
                    match(input,VT_FACT,FOLLOW_VT_FACT_in_fact_expression828); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_pattern_type_in_fact_expression830);
                    pattern_type();
                    _fsp--;

                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:156:27: ( fact_expression )*
                    loop31:
                    do {
                        int alt31=2;
                        int LA31_0 = input.LA(1);

                        if ( (LA31_0==VT_FACT||LA31_0==VT_PAREN_CHUNK||(LA31_0>=VT_FACT_BINDING && LA31_0<=VT_ACCESSOR_PATH)||(LA31_0>=VK_EVAL && LA31_0<=VK_MEMBEROF)||LA31_0==VK_IN||LA31_0==ID||LA31_0==STRING||(LA31_0>=BOOL && LA31_0<=DOUBLE_AMPER)||(LA31_0>=EQUAL && LA31_0<=NOT_EQUAL)||(LA31_0>=FLOAT && LA31_0<=NULL)) ) {
                            alt31=1;
                        }


                        switch (alt31) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:156:27: fact_expression
                    	    {
                    	    pushFollow(FOLLOW_fact_expression_in_fact_expression832);
                    	    fact_expression();
                    	    _fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop31;
                        }
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:157:4: ^( VT_FACT_OR fact_expression fact_expression )
                    {
                    match(input,VT_FACT_OR,FOLLOW_VT_FACT_OR_in_fact_expression840); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression842);
                    fact_expression();
                    _fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression844);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:158:4: ^( VK_EVAL VT_PAREN_CHUNK )
                    {
                    match(input,VK_EVAL,FOLLOW_VK_EVAL_in_fact_expression851); 

                    match(input, Token.DOWN, null); 
                    match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_fact_expression853); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:159:4: ^( VK_IN ( VK_NOT )? ( fact_expression )+ )
                    {
                    match(input,VK_IN,FOLLOW_VK_IN_in_fact_expression860); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:159:12: ( VK_NOT )?
                    int alt32=2;
                    int LA32_0 = input.LA(1);

                    if ( (LA32_0==VK_NOT) ) {
                        alt32=1;
                    }
                    switch (alt32) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:159:12: VK_NOT
                            {
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression862); 

                            }
                            break;

                    }

                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:159:20: ( fact_expression )+
                    int cnt33=0;
                    loop33:
                    do {
                        int alt33=2;
                        int LA33_0 = input.LA(1);

                        if ( (LA33_0==VT_FACT||LA33_0==VT_PAREN_CHUNK||(LA33_0>=VT_FACT_BINDING && LA33_0<=VT_ACCESSOR_PATH)||(LA33_0>=VK_EVAL && LA33_0<=VK_MEMBEROF)||LA33_0==VK_IN||LA33_0==ID||LA33_0==STRING||(LA33_0>=BOOL && LA33_0<=DOUBLE_AMPER)||(LA33_0>=EQUAL && LA33_0<=NOT_EQUAL)||(LA33_0>=FLOAT && LA33_0<=NULL)) ) {
                            alt33=1;
                        }


                        switch (alt33) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:159:20: fact_expression
                    	    {
                    	    pushFollow(FOLLOW_fact_expression_in_fact_expression865);
                    	    fact_expression();
                    	    _fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt33 >= 1 ) break loop33;
                                EarlyExitException eee =
                                    new EarlyExitException(33, input);
                                throw eee;
                        }
                        cnt33++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:160:4: ^( EQUAL fact_expression )
                    {
                    match(input,EQUAL,FOLLOW_EQUAL_in_fact_expression873); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression875);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:161:4: ^( GREATER fact_expression )
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_fact_expression882); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression884);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 10 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:162:4: ^( GREATER_EQUAL fact_expression )
                    {
                    match(input,GREATER_EQUAL,FOLLOW_GREATER_EQUAL_in_fact_expression891); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression893);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 11 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:163:4: ^( LESS fact_expression )
                    {
                    match(input,LESS,FOLLOW_LESS_in_fact_expression900); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression902);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 12 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:164:4: ^( LESS_EQUAL fact_expression )
                    {
                    match(input,LESS_EQUAL,FOLLOW_LESS_EQUAL_in_fact_expression909); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression911);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 13 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:165:4: ^( NOT_EQUAL fact_expression )
                    {
                    match(input,NOT_EQUAL,FOLLOW_NOT_EQUAL_in_fact_expression918); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression920);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 14 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:166:4: ^( VK_CONTAINS ( VK_NOT )? fact_expression )
                    {
                    match(input,VK_CONTAINS,FOLLOW_VK_CONTAINS_in_fact_expression927); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:166:18: ( VK_NOT )?
                    int alt34=2;
                    int LA34_0 = input.LA(1);

                    if ( (LA34_0==VK_NOT) ) {
                        alt34=1;
                    }
                    switch (alt34) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:166:18: VK_NOT
                            {
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression929); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression932);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 15 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:167:4: ^( VK_EXCLUDES ( VK_NOT )? fact_expression )
                    {
                    match(input,VK_EXCLUDES,FOLLOW_VK_EXCLUDES_in_fact_expression939); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:167:18: ( VK_NOT )?
                    int alt35=2;
                    int LA35_0 = input.LA(1);

                    if ( (LA35_0==VK_NOT) ) {
                        alt35=1;
                    }
                    switch (alt35) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:167:18: VK_NOT
                            {
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression941); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression944);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 16 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:168:4: ^( VK_MATCHES ( VK_NOT )? fact_expression )
                    {
                    match(input,VK_MATCHES,FOLLOW_VK_MATCHES_in_fact_expression951); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:168:17: ( VK_NOT )?
                    int alt36=2;
                    int LA36_0 = input.LA(1);

                    if ( (LA36_0==VK_NOT) ) {
                        alt36=1;
                    }
                    switch (alt36) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:168:17: VK_NOT
                            {
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression953); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression956);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 17 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:169:4: ^( VK_SOUNDSLIKE ( VK_NOT )? fact_expression )
                    {
                    match(input,VK_SOUNDSLIKE,FOLLOW_VK_SOUNDSLIKE_in_fact_expression963); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:169:20: ( VK_NOT )?
                    int alt37=2;
                    int LA37_0 = input.LA(1);

                    if ( (LA37_0==VK_NOT) ) {
                        alt37=1;
                    }
                    switch (alt37) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:169:20: VK_NOT
                            {
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression965); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression968);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 18 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:170:4: ^( VK_MEMBEROF ( VK_NOT )? fact_expression )
                    {
                    match(input,VK_MEMBEROF,FOLLOW_VK_MEMBEROF_in_fact_expression975); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:170:18: ( VK_NOT )?
                    int alt38=2;
                    int LA38_0 = input.LA(1);

                    if ( (LA38_0==VK_NOT) ) {
                        alt38=1;
                    }
                    switch (alt38) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:170:18: VK_NOT
                            {
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression977); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression980);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 19 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:171:4: ^( ID ( VK_NOT )? ( VT_SQUARE_CHUNK )? fact_expression )
                    {
                    match(input,ID,FOLLOW_ID_in_fact_expression987); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:171:9: ( VK_NOT )?
                    int alt39=2;
                    int LA39_0 = input.LA(1);

                    if ( (LA39_0==VK_NOT) ) {
                        alt39=1;
                    }
                    switch (alt39) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:171:9: VK_NOT
                            {
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression989); 

                            }
                            break;

                    }

                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:171:17: ( VT_SQUARE_CHUNK )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);

                    if ( (LA40_0==VT_SQUARE_CHUNK) ) {
                        alt40=1;
                    }
                    switch (alt40) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:171:17: VT_SQUARE_CHUNK
                            {
                            match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_fact_expression992); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression995);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 20 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:172:4: ^( VT_BIND_FIELD VT_LABEL fact_expression )
                    {
                    match(input,VT_BIND_FIELD,FOLLOW_VT_BIND_FIELD_in_fact_expression1002); 

                    match(input, Token.DOWN, null); 
                    match(input,VT_LABEL,FOLLOW_VT_LABEL_in_fact_expression1004); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1006);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 21 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:173:4: ^( VT_FIELD fact_expression ( fact_expression )? )
                    {
                    match(input,VT_FIELD,FOLLOW_VT_FIELD_in_fact_expression1013); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1015);
                    fact_expression();
                    _fsp--;

                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:173:31: ( fact_expression )?
                    int alt41=2;
                    int LA41_0 = input.LA(1);

                    if ( (LA41_0==VT_FACT||LA41_0==VT_PAREN_CHUNK||(LA41_0>=VT_FACT_BINDING && LA41_0<=VT_ACCESSOR_PATH)||(LA41_0>=VK_EVAL && LA41_0<=VK_MEMBEROF)||LA41_0==VK_IN||LA41_0==ID||LA41_0==STRING||(LA41_0>=BOOL && LA41_0<=DOUBLE_AMPER)||(LA41_0>=EQUAL && LA41_0<=NOT_EQUAL)||(LA41_0>=FLOAT && LA41_0<=NULL)) ) {
                        alt41=1;
                    }
                    switch (alt41) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:173:31: fact_expression
                            {
                            pushFollow(FOLLOW_fact_expression_in_fact_expression1017);
                            fact_expression();
                            _fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 22 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:174:4: ^( VT_ACCESSOR_PATH ( accessor_element )+ )
                    {
                    match(input,VT_ACCESSOR_PATH,FOLLOW_VT_ACCESSOR_PATH_in_fact_expression1025); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:174:23: ( accessor_element )+
                    int cnt42=0;
                    loop42:
                    do {
                        int alt42=2;
                        int LA42_0 = input.LA(1);

                        if ( (LA42_0==VT_ACCESSOR_ELEMENT) ) {
                            alt42=1;
                        }


                        switch (alt42) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:174:23: accessor_element
                    	    {
                    	    pushFollow(FOLLOW_accessor_element_in_fact_expression1027);
                    	    accessor_element();
                    	    _fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt42 >= 1 ) break loop42;
                                EarlyExitException eee =
                                    new EarlyExitException(42, input);
                                throw eee;
                        }
                        cnt42++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 23 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:175:4: STRING
                    {
                    match(input,STRING,FOLLOW_STRING_in_fact_expression1034); 

                    }
                    break;
                case 24 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:176:4: INT
                    {
                    match(input,INT,FOLLOW_INT_in_fact_expression1039); 

                    }
                    break;
                case 25 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:177:4: FLOAT
                    {
                    match(input,FLOAT,FOLLOW_FLOAT_in_fact_expression1044); 

                    }
                    break;
                case 26 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:178:4: BOOL
                    {
                    match(input,BOOL,FOLLOW_BOOL_in_fact_expression1049); 

                    }
                    break;
                case 27 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:179:4: NULL
                    {
                    match(input,NULL,FOLLOW_NULL_in_fact_expression1054); 

                    }
                    break;
                case 28 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:180:4: VT_PAREN_CHUNK
                    {
                    match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_fact_expression1059); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end fact_expression


    // $ANTLR start pattern_type
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:183:1: pattern_type : ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) ;
    public final void pattern_type() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:184:2: ( ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:184:4: ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* )
            {
            match(input,VT_PATTERN_TYPE,FOLLOW_VT_PATTERN_TYPE_in_pattern_type1071); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:184:22: ( ID )+
            int cnt44=0;
            loop44:
            do {
                int alt44=2;
                int LA44_0 = input.LA(1);

                if ( (LA44_0==ID) ) {
                    alt44=1;
                }


                switch (alt44) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:184:22: ID
            	    {
            	    match(input,ID,FOLLOW_ID_in_pattern_type1073); 

            	    }
            	    break;

            	default :
            	    if ( cnt44 >= 1 ) break loop44;
                        EarlyExitException eee =
                            new EarlyExitException(44, input);
                        throw eee;
                }
                cnt44++;
            } while (true);

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:184:26: ( dimension_definition )*
            loop45:
            do {
                int alt45=2;
                int LA45_0 = input.LA(1);

                if ( (LA45_0==LEFT_SQUARE) ) {
                    alt45=1;
                }


                switch (alt45) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:184:26: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_pattern_type1076);
            	    dimension_definition();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop45;
                }
            } while (true);


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end pattern_type


    // $ANTLR start data_type
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:187:1: data_type : ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) ;
    public final void data_type() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:188:2: ( ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:188:4: ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* )
            {
            match(input,VT_DATA_TYPE,FOLLOW_VT_DATA_TYPE_in_data_type1090); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:188:19: ( ID )+
            int cnt46=0;
            loop46:
            do {
                int alt46=2;
                int LA46_0 = input.LA(1);

                if ( (LA46_0==ID) ) {
                    alt46=1;
                }


                switch (alt46) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:188:19: ID
            	    {
            	    match(input,ID,FOLLOW_ID_in_data_type1092); 

            	    }
            	    break;

            	default :
            	    if ( cnt46 >= 1 ) break loop46;
                        EarlyExitException eee =
                            new EarlyExitException(46, input);
                        throw eee;
                }
                cnt46++;
            } while (true);

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:188:23: ( dimension_definition )*
            loop47:
            do {
                int alt47=2;
                int LA47_0 = input.LA(1);

                if ( (LA47_0==LEFT_SQUARE) ) {
                    alt47=1;
                }


                switch (alt47) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:188:23: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_data_type1095);
            	    dimension_definition();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop47;
                }
            } while (true);


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end data_type


    // $ANTLR start dimension_definition
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:191:1: dimension_definition : LEFT_SQUARE RIGHT_SQUARE ;
    public final void dimension_definition() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:192:2: ( LEFT_SQUARE RIGHT_SQUARE )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:192:4: LEFT_SQUARE RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dimension_definition1108); 
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dimension_definition1110); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end dimension_definition


    // $ANTLR start accessor_element
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:195:1: accessor_element : ^( VT_ACCESSOR_ELEMENT ID ( VT_SQUARE_CHUNK )* ) ;
    public final void accessor_element() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:196:2: ( ^( VT_ACCESSOR_ELEMENT ID ( VT_SQUARE_CHUNK )* ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:196:4: ^( VT_ACCESSOR_ELEMENT ID ( VT_SQUARE_CHUNK )* )
            {
            match(input,VT_ACCESSOR_ELEMENT,FOLLOW_VT_ACCESSOR_ELEMENT_in_accessor_element1122); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_accessor_element1124); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:196:29: ( VT_SQUARE_CHUNK )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( (LA48_0==VT_SQUARE_CHUNK) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:196:29: VT_SQUARE_CHUNK
            	    {
            	    match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_accessor_element1126); 

            	    }
            	    break;

            	default :
            	    break loop48;
                }
            } while (true);


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end accessor_element


    // $ANTLR start expression_chain
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:199:1: expression_chain : ^( VT_EXPRESSION_CHAIN ID ( VT_SQUARE_CHUNK )? ( VT_PAREN_CHUNK )? ( expression_chain )? ) ;
    public final void expression_chain() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:200:2: ( ^( VT_EXPRESSION_CHAIN ID ( VT_SQUARE_CHUNK )? ( VT_PAREN_CHUNK )? ( expression_chain )? ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:200:4: ^( VT_EXPRESSION_CHAIN ID ( VT_SQUARE_CHUNK )? ( VT_PAREN_CHUNK )? ( expression_chain )? )
            {
            match(input,VT_EXPRESSION_CHAIN,FOLLOW_VT_EXPRESSION_CHAIN_in_expression_chain1140); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_expression_chain1142); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:200:29: ( VT_SQUARE_CHUNK )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==VT_SQUARE_CHUNK) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:200:29: VT_SQUARE_CHUNK
                    {
                    match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_expression_chain1144); 

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:200:46: ( VT_PAREN_CHUNK )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==VT_PAREN_CHUNK) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:200:46: VT_PAREN_CHUNK
                    {
                    match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_expression_chain1147); 

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:200:62: ( expression_chain )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==VT_EXPRESSION_CHAIN) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:200:62: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain1150);
                    expression_chain();
                    _fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end expression_chain


    // $ANTLR start curly_chunk
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:203:1: curly_chunk : VT_CURLY_CHUNK ;
    public final void curly_chunk() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:204:2: ( VT_CURLY_CHUNK )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:204:4: VT_CURLY_CHUNK
            {
            match(input,VT_CURLY_CHUNK,FOLLOW_VT_CURLY_CHUNK_in_curly_chunk1164); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end curly_chunk


 

    public static final BitSet FOLLOW_VT_COMPILATION_UNIT_in_compilation_unit43 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_package_statement_in_compilation_unit45 = new BitSet(new long[]{0xB67FF80000000028L,0x0000000000000001L});
    public static final BitSet FOLLOW_statement_in_compilation_unit48 = new BitSet(new long[]{0xB67FF80000000028L,0x0000000000000001L});
    public static final BitSet FOLLOW_VK_PACKAGE_in_package_statement63 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_package_id_in_package_statement65 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PACKAGE_ID_in_package_id78 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_package_id80 = new BitSet(new long[]{0x0000000000000008L,0x0000000000400000L});
    public static final BitSet FOLLOW_rule_attribute_in_statement93 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement98 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement116 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VK_IMPORT_in_import_statement143 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_import_name_in_import_statement145 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FUNCTION_IMPORT_in_function_import_statement158 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_FUNCTION_in_function_import_statement160 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement162 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_IMPORT_ID_in_import_name175 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_import_name177 = new BitSet(new long[]{0x0000000000000008L,0x0000000001400000L});
    public static final BitSet FOLLOW_DOT_STAR_in_import_name180 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_GLOBAL_in_global194 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_global196 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_VT_GLOBAL_ID_in_global198 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_FUNCTION_in_function211 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_function213 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_VT_FUNCTION_ID_in_function216 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_parameters_in_function218 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_curly_chunk_in_function220 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_QUERY_in_query233 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_QUERY_ID_in_query235 = new BitSet(new long[]{0x0000040000100000L});
    public static final BitSet FOLLOW_parameters_in_query237 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_lhs_block_in_query240 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_END_in_query242 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PARAM_LIST_in_parameters255 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_param_definition_in_parameters257 = new BitSet(new long[]{0x0000001000000008L,0x0000000000400000L});
    public static final BitSet FOLLOW_data_type_in_param_definition270 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_argument_in_param_definition273 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_argument284 = new BitSet(new long[]{0x0000000000000002L,0x0000200000000000L});
    public static final BitSet FOLLOW_dimension_definition_in_argument286 = new BitSet(new long[]{0x0000000000000002L,0x0000200000000000L});
    public static final BitSet FOLLOW_VK_TEMPLATE_in_template300 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_TEMPLATE_ID_in_template302 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_template_slot_in_template304 = new BitSet(new long[]{0x0000000000004000L,0x0000000002000000L});
    public static final BitSet FOLLOW_END_in_template307 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_SLOT_in_template_slot320 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_template_slot322 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_VT_SLOT_ID_in_template_slot324 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_RULE_in_rule337 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_RULE_ID_in_rule339 = new BitSet(new long[]{0x0100000000018000L});
    public static final BitSet FOLLOW_rule_attributes_in_rule341 = new BitSet(new long[]{0x0100000000010000L});
    public static final BitSet FOLLOW_when_part_in_rule344 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_VT_RHS_CHUNK_in_rule347 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_WHEN_in_when_part359 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_lhs_block_in_when_part361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_RULE_ATTRIBUTES_in_rule_attributes373 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_ATTRIBUTES_in_rule_attributes375 = new BitSet(new long[]{0x007FF80000000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes378 = new BitSet(new long[]{0x007FF80000000008L});
    public static final BitSet FOLLOW_VK_SALIENCE_in_rule_attribute392 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_rule_attribute394 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_NO_LOOP_in_rule_attribute406 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute408 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_AGENDA_GROUP_in_rule_attribute418 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute420 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DURATION_in_rule_attribute429 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INT_in_rule_attribute431 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ACTIVATION_GROUP_in_rule_attribute441 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute443 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_AUTO_FOCUS_in_rule_attribute451 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute453 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DATE_EFFECTIVE_in_rule_attribute462 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute464 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DATE_EXPIRES_in_rule_attribute472 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute474 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ENABLED_in_rule_attribute482 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute484 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_RULEFLOW_GROUP_in_rule_attribute492 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute494 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_LOCK_ON_ACTIVE_in_rule_attribute502 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute504 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DIALECT_in_rule_attribute512 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute514 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_AND_IMPLICIT_in_lhs_block528 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs_block530 = new BitSet(new long[]{0x0000000021E00008L,0x0000000000007102L});
    public static final BitSet FOLLOW_VT_OR_PREFIX_in_lhs543 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs545 = new BitSet(new long[]{0x0000000021E00008L,0x0000000000007102L});
    public static final BitSet FOLLOW_VT_OR_INFIX_in_lhs553 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs555 = new BitSet(new long[]{0x0000000021E00000L,0x0000000000007102L});
    public static final BitSet FOLLOW_lhs_in_lhs557 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_AND_PREFIX_in_lhs564 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs566 = new BitSet(new long[]{0x0000000021E00008L,0x0000000000007102L});
    public static final BitSet FOLLOW_VT_AND_INFIX_in_lhs574 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs576 = new BitSet(new long[]{0x0000000021E00000L,0x0000000000007102L});
    public static final BitSet FOLLOW_lhs_in_lhs578 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EXISTS_in_lhs585 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs587 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_NOT_in_lhs594 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs596 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EVAL_in_lhs603 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_lhs605 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_FORALL_in_lhs612 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs614 = new BitSet(new long[]{0x0000000021E00008L,0x0000000000007102L});
    public static final BitSet FOLLOW_VK_FROM_in_lhs622 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs624 = new BitSet(new long[]{0x0000000008000000L,0x0000000000108080L});
    public static final BitSet FOLLOW_from_elements_in_lhs626 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VK_ACCUMULATE_in_from_elements644 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_from_elements646 = new BitSet(new long[]{0x0000000006000000L});
    public static final BitSet FOLLOW_accumulate_init_clause_in_from_elements649 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_accumulate_id_clause_in_from_elements651 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_COLLECT_in_from_elements659 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_from_elements661 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ENTRY_POINT_in_from_elements668 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_ENTRYPOINT_ID_in_from_elements670 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FROM_SOURCE_in_from_elements677 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_from_elements679 = new BitSet(new long[]{0x0000000010080008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_from_elements681 = new BitSet(new long[]{0x0000000010000008L});
    public static final BitSet FOLLOW_expression_chain_in_from_elements684 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ACCUMULATE_INIT_CLAUSE_in_accumulate_init_clause698 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_INIT_in_accumulate_init_clause705 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause707 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ACTION_in_accumulate_init_clause715 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause717 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_accumulate_init_reverse_clause_in_accumulate_init_clause724 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_VK_RESULT_in_accumulate_init_clause731 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause733 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_REVERSE_in_accumulate_init_reverse_clause747 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_reverse_clause749 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ACCUMULATE_ID_CLAUSE_in_accumulate_id_clause763 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_accumulate_id_clause765 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_id_clause767 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PATTERN_in_lhs_pattern780 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_lhs_pattern782 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_fact_expression795 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression797 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression799 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_fact_expression806 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression808 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression810 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FACT_BINDING_in_fact_expression817 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_LABEL_in_fact_expression819 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression821 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FACT_in_fact_expression828 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_pattern_type_in_fact_expression830 = new BitSet(new long[]{0x00000007C0080048L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression832 = new BitSet(new long[]{0x00000007C0080048L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_VT_FACT_OR_in_fact_expression840 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression842 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression844 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EVAL_in_fact_expression851 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_fact_expression853 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_IN_in_fact_expression860 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression862 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression865 = new BitSet(new long[]{0x00000007C0080048L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_EQUAL_in_fact_expression873 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression875 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GREATER_in_fact_expression882 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression884 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GREATER_EQUAL_in_fact_expression891 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression893 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LESS_in_fact_expression900 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression902 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LESS_EQUAL_in_fact_expression909 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression911 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_EQUAL_in_fact_expression918 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression920 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_CONTAINS_in_fact_expression927 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression929 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression932 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EXCLUDES_in_fact_expression939 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression941 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression944 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_MATCHES_in_fact_expression951 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression953 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression956 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_SOUNDSLIKE_in_fact_expression963 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression965 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression968 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_MEMBEROF_in_fact_expression975 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression977 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression980 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_fact_expression987 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression989 = new BitSet(new long[]{0x00000007C00C0040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_fact_expression992 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression995 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_BIND_FIELD_in_fact_expression1002 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_LABEL_in_fact_expression1004 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1006 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FIELD_in_fact_expression1013 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1015 = new BitSet(new long[]{0x00000007C0080048L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1017 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ACCESSOR_PATH_in_fact_expression1025 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_accessor_element_in_fact_expression1027 = new BitSet(new long[]{0x0000000800000008L});
    public static final BitSet FOLLOW_STRING_in_fact_expression1034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_fact_expression1039 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_fact_expression1044 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_fact_expression1049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_fact_expression1054 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_fact_expression1059 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_PATTERN_TYPE_in_pattern_type1071 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_pattern_type1073 = new BitSet(new long[]{0x0000000000000008L,0x0000200000400000L});
    public static final BitSet FOLLOW_dimension_definition_in_pattern_type1076 = new BitSet(new long[]{0x0000000000000008L,0x0000200000000000L});
    public static final BitSet FOLLOW_VT_DATA_TYPE_in_data_type1090 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_data_type1092 = new BitSet(new long[]{0x0000000000000008L,0x0000200000400000L});
    public static final BitSet FOLLOW_dimension_definition_in_data_type1095 = new BitSet(new long[]{0x0000000000000008L,0x0000200000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dimension_definition1108 = new BitSet(new long[]{0x0000000000000000L,0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dimension_definition1110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_ACCESSOR_ELEMENT_in_accessor_element1122 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_accessor_element1124 = new BitSet(new long[]{0x0000000000040008L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_accessor_element1126 = new BitSet(new long[]{0x0000000000040008L});
    public static final BitSet FOLLOW_VT_EXPRESSION_CHAIN_in_expression_chain1140 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_expression_chain1142 = new BitSet(new long[]{0x00000000100C0008L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_expression_chain1144 = new BitSet(new long[]{0x0000000010080008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_expression_chain1147 = new BitSet(new long[]{0x0000000010000008L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain1150 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_CURLY_CHUNK_in_curly_chunk1164 = new BitSet(new long[]{0x0000000000000002L});

}