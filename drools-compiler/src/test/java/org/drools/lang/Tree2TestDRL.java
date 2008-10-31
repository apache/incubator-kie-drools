// $ANTLR 3.0.1 /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g 2008-08-26 16:10:09

	package org.drools.lang;


import org.antlr.runtime.BitSet;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeParser;

public class Tree2TestDRL extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_COMPILATION_UNIT", "VT_FUNCTION_IMPORT", "VT_FACT", "VT_CONSTRAINTS", "VT_LABEL", "VT_QUERY_ID", "VT_TEMPLATE_ID", "VT_TYPE_DECLARE_ID", "VT_RULE_ID", "VT_ENTRYPOINT_ID", "VT_SLOT_ID", "VT_SLOT", "VT_RULE_ATTRIBUTES", "VT_RHS_CHUNK", "VT_CURLY_CHUNK", "VT_SQUARE_CHUNK", "VT_PAREN_CHUNK", "VT_BEHAVIOR", "VT_AND_IMPLICIT", "VT_AND_PREFIX", "VT_OR_PREFIX", "VT_AND_INFIX", "VT_OR_INFIX", "VT_ACCUMULATE_INIT_CLAUSE", "VT_ACCUMULATE_ID_CLAUSE", "VT_FROM_SOURCE", "VT_EXPRESSION_CHAIN", "VT_PATTERN", "VT_FACT_BINDING", "VT_FACT_OR", "VT_BIND_FIELD", "VT_FIELD", "VT_ACCESSOR_PATH", "VT_ACCESSOR_ELEMENT", "VT_DATA_TYPE", "VT_PATTERN_TYPE", "VT_PACKAGE_ID", "VT_IMPORT_ID", "VT_GLOBAL_ID", "VT_FUNCTION_ID", "VT_PARAM_LIST", "VK_DATE_EFFECTIVE", "VK_DATE_EXPIRES", "VK_LOCK_ON_ACTIVE", "VK_NO_LOOP", "VK_AUTO_FOCUS", "VK_ACTIVATION_GROUP", "VK_AGENDA_GROUP", "VK_RULEFLOW_GROUP", "VK_DURATION", "VK_DIALECT", "VK_SALIENCE", "VK_ENABLED", "VK_ATTRIBUTES", "VK_RULE", "VK_IMPORT", "VK_PACKAGE", "VK_TEMPLATE", "VK_QUERY", "VK_DECLARE", "VK_FUNCTION", "VK_GLOBAL", "VK_EVAL", "VK_CONTAINS", "VK_MATCHES", "VK_EXCLUDES", "VK_SOUNDSLIKE", "VK_MEMBEROF", "VK_ENTRY_POINT", "VK_NOT", "VK_IN", "VK_OR", "VK_AND", "VK_EXISTS", "VK_FORALL", "VK_ACTION", "VK_REVERSE", "VK_RESULT", "SEMICOLON", "ID", "DOT", "DOT_STAR", "END", "STRING", "LEFT_PAREN", "COMMA", "RIGHT_PAREN", "AT", "COLON", "EQUALS", "WHEN", "BOOL", "INT", "DOUBLE_PIPE", "DOUBLE_AMPER", "FROM", "OVER", "ACCUMULATE", "INIT", "COLLECT", "ARROW", "EQUAL", "GREATER", "GREATER_EQUAL", "LESS", "LESS_EQUAL", "NOT_EQUAL", "GRAVE_ACCENT", "FLOAT", "NULL", "LEFT_SQUARE", "RIGHT_SQUARE", "THEN", "LEFT_CURLY", "RIGHT_CURLY", "MISC", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT"
    };
    public static final int COMMA=89;
    public static final int VT_PATTERN_TYPE=39;
    public static final int VT_ACCUMULATE_ID_CLAUSE=28;
    public static final int VK_DIALECT=54;
    public static final int VK_FUNCTION=64;
    public static final int END=86;
    public static final int HexDigit=123;
    public static final int VK_ATTRIBUTES=57;
    public static final int VT_EXPRESSION_CHAIN=30;
    public static final int MISC=119;
    public static final int VT_AND_PREFIX=23;
    public static final int VK_QUERY=62;
    public static final int THEN=116;
    public static final int VK_AUTO_FOCUS=49;
    public static final int DOT=84;
    public static final int VK_IMPORT=59;
    public static final int VT_SLOT=15;
    public static final int VT_PACKAGE_ID=40;
    public static final int LEFT_SQUARE=114;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=126;
    public static final int VT_DATA_TYPE=38;
    public static final int VK_MATCHES=68;
    public static final int VT_FACT=6;
    public static final int LEFT_CURLY=117;
    public static final int AT=91;
    public static final int LEFT_PAREN=88;
    public static final int DOUBLE_AMPER=98;
    public static final int VT_QUERY_ID=9;
    public static final int VT_ACCESSOR_PATH=36;
    public static final int VT_LABEL=8;
    public static final int WHEN=94;
    public static final int VT_ENTRYPOINT_ID=13;
    public static final int VK_SOUNDSLIKE=70;
    public static final int VK_SALIENCE=55;
    public static final int VT_FIELD=35;
    public static final int WS=121;
    public static final int OVER=100;
    public static final int STRING=87;
    public static final int VK_AND=76;
    public static final int VT_ACCESSOR_ELEMENT=37;
    public static final int VT_ACCUMULATE_INIT_CLAUSE=27;
    public static final int VK_GLOBAL=65;
    public static final int VK_REVERSE=80;
    public static final int VT_BEHAVIOR=21;
    public static final int GRAVE_ACCENT=111;
    public static final int VK_DURATION=53;
    public static final int VT_SQUARE_CHUNK=19;
    public static final int VK_FORALL=78;
    public static final int VT_PAREN_CHUNK=20;
    public static final int VT_COMPILATION_UNIT=4;
    public static final int COLLECT=103;
    public static final int VK_ENABLED=56;
    public static final int EQUALS=93;
    public static final int VK_RESULT=81;
    public static final int UnicodeEscape=124;
    public static final int VK_PACKAGE=60;
    public static final int VT_RULE_ID=12;
    public static final int EQUAL=105;
    public static final int VK_NO_LOOP=48;
    public static final int SEMICOLON=82;
    public static final int VK_TEMPLATE=61;
    public static final int VT_AND_IMPLICIT=22;
    public static final int NULL=113;
    public static final int COLON=92;
    public static final int MULTI_LINE_COMMENT=128;
    public static final int VT_RULE_ATTRIBUTES=16;
    public static final int RIGHT_SQUARE=115;
    public static final int VK_AGENDA_GROUP=51;
    public static final int VT_FACT_OR=33;
    public static final int VK_NOT=73;
    public static final int VK_DATE_EXPIRES=46;
    public static final int ARROW=104;
    public static final int FLOAT=112;
    public static final int INIT=102;
    public static final int VT_SLOT_ID=14;
    public static final int VT_CURLY_CHUNK=18;
    public static final int VT_OR_PREFIX=24;
    public static final int DOUBLE_PIPE=97;
    public static final int LESS=108;
    public static final int VT_TYPE_DECLARE_ID=11;
    public static final int VT_PATTERN=31;
    public static final int VK_DATE_EFFECTIVE=45;
    public static final int EscapeSequence=122;
    public static final int VK_EXISTS=77;
    public static final int INT=96;
    public static final int VT_BIND_FIELD=34;
    public static final int VK_RULE=58;
    public static final int VK_EVAL=66;
    public static final int GREATER=106;
    public static final int VT_FACT_BINDING=32;
    public static final int ID=83;
    public static final int FROM=99;
    public static final int NOT_EQUAL=110;
    public static final int RIGHT_CURLY=118;
    public static final int VK_ENTRY_POINT=72;
    public static final int VT_PARAM_LIST=44;
    public static final int VT_AND_INFIX=25;
    public static final int BOOL=95;
    public static final int VT_FROM_SOURCE=29;
    public static final int VK_CONTAINS=67;
    public static final int VK_LOCK_ON_ACTIVE=47;
    public static final int VT_FUNCTION_IMPORT=5;
    public static final int VK_IN=74;
    public static final int VT_RHS_CHUNK=17;
    public static final int VK_MEMBEROF=71;
    public static final int GREATER_EQUAL=107;
    public static final int VT_OR_INFIX=26;
    public static final int DOT_STAR=85;
    public static final int VK_OR=75;
    public static final int VT_GLOBAL_ID=42;
    public static final int LESS_EQUAL=109;
    public static final int ACCUMULATE=101;
    public static final int VK_RULEFLOW_GROUP=52;
    public static final int VT_FUNCTION_ID=43;
    public static final int EOF=-1;
    public static final int VT_CONSTRAINTS=7;
    public static final int VT_IMPORT_ID=41;
    public static final int EOL=120;
    public static final int VK_ACTIVATION_GROUP=50;
    public static final int OctalEscape=125;
    public static final int VK_ACTION=79;
    public static final int VK_EXCLUDES=69;
    public static final int RIGHT_PAREN=90;
    public static final int VT_TEMPLATE_ID=10;
    public static final int VK_DECLARE=63;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=127;

        public Tree2TestDRL(TreeNodeStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "/Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g"; }



    // $ANTLR start compilation_unit
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:13:1: compilation_unit : ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) ;
    public final void compilation_unit() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:14:2: ( ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:14:4: ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* )
            {
            match(input,VT_COMPILATION_UNIT,FOLLOW_VT_COMPILATION_UNIT_in_compilation_unit43); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:14:26: ( package_statement )?
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==VK_PACKAGE) ) {
                    alt1=1;
                }
                switch (alt1) {
                    case 1 :
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:14:26: package_statement
                        {
                        pushFollow(FOLLOW_package_statement_in_compilation_unit45);
                        package_statement();
                        _fsp--;


                        }
                        break;

                }

                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:14:45: ( statement )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==VT_FUNCTION_IMPORT||(LA2_0>=VK_DATE_EFFECTIVE && LA2_0<=VK_ENABLED)||(LA2_0>=VK_RULE && LA2_0<=VK_IMPORT)||(LA2_0>=VK_TEMPLATE && LA2_0<=VK_GLOBAL)) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:14:45: statement
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:17:1: package_statement : ^( VK_PACKAGE package_id ) ;
    public final void package_statement() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:18:2: ( ^( VK_PACKAGE package_id ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:18:4: ^( VK_PACKAGE package_id )
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:21:1: package_id : ^( VT_PACKAGE_ID ( ID )+ ) ;
    public final void package_id() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:22:2: ( ^( VT_PACKAGE_ID ( ID )+ ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:22:4: ^( VT_PACKAGE_ID ( ID )+ )
            {
            match(input,VT_PACKAGE_ID,FOLLOW_VT_PACKAGE_ID_in_package_id78); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:22:20: ( ID )+
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
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:22:20: ID
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:25:1: statement : ( rule_attribute | function_import_statement | import_statement | global | function | template | rule | query | type_declaration );
    public final void statement() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:26:2: ( rule_attribute | function_import_statement | import_statement | global | function | template | rule | query | type_declaration )
            int alt4=9;
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
            case VK_DECLARE:
                {
                alt4=9;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("25:1: statement : ( rule_attribute | function_import_statement | import_statement | global | function | template | rule | query | type_declaration );", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:26:4: rule_attribute
                    {
                    pushFollow(FOLLOW_rule_attribute_in_statement93);
                    rule_attribute();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:27:4: function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement98);
                    function_import_statement();
                    _fsp--;


                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:28:4: import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement104);
                    import_statement();
                    _fsp--;


                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:29:4: global
                    {
                    pushFollow(FOLLOW_global_in_statement110);
                    global();
                    _fsp--;


                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:30:4: function
                    {
                    pushFollow(FOLLOW_function_in_statement116);
                    function();
                    _fsp--;


                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:31:4: template
                    {
                    pushFollow(FOLLOW_template_in_statement121);
                    template();
                    _fsp--;


                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:32:4: rule
                    {
                    pushFollow(FOLLOW_rule_in_statement126);
                    rule();
                    _fsp--;


                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:33:4: query
                    {
                    pushFollow(FOLLOW_query_in_statement131);
                    query();
                    _fsp--;


                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:34:4: type_declaration
                    {
                    pushFollow(FOLLOW_type_declaration_in_statement136);
                    type_declaration();
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:37:1: import_statement : ^( VK_IMPORT import_name ) ;
    public final void import_statement() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:38:2: ( ^( VK_IMPORT import_name ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:38:4: ^( VK_IMPORT import_name )
            {
            match(input,VK_IMPORT,FOLLOW_VK_IMPORT_in_import_statement148); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_import_name_in_import_statement150);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:41:1: function_import_statement : ^( VT_FUNCTION_IMPORT VK_FUNCTION import_name ) ;
    public final void function_import_statement() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:42:2: ( ^( VT_FUNCTION_IMPORT VK_FUNCTION import_name ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:42:4: ^( VT_FUNCTION_IMPORT VK_FUNCTION import_name )
            {
            match(input,VT_FUNCTION_IMPORT,FOLLOW_VT_FUNCTION_IMPORT_in_function_import_statement163); 

            match(input, Token.DOWN, null); 
            match(input,VK_FUNCTION,FOLLOW_VK_FUNCTION_in_function_import_statement165); 
            pushFollow(FOLLOW_import_name_in_function_import_statement167);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:45:1: import_name : ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) ;
    public final void import_name() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:46:2: ( ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:46:4: ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? )
            {
            match(input,VT_IMPORT_ID,FOLLOW_VT_IMPORT_ID_in_import_name180); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:46:19: ( ID )+
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
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:46:19: ID
            	    {
            	    match(input,ID,FOLLOW_ID_in_import_name182); 

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

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:46:23: ( DOT_STAR )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==DOT_STAR) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:46:23: DOT_STAR
                    {
                    match(input,DOT_STAR,FOLLOW_DOT_STAR_in_import_name185); 

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:49:1: global : ^( VK_GLOBAL data_type VT_GLOBAL_ID ) ;
    public final void global() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:50:2: ( ^( VK_GLOBAL data_type VT_GLOBAL_ID ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:50:4: ^( VK_GLOBAL data_type VT_GLOBAL_ID )
            {
            match(input,VK_GLOBAL,FOLLOW_VK_GLOBAL_in_global199); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_data_type_in_global201);
            data_type();
            _fsp--;

            match(input,VT_GLOBAL_ID,FOLLOW_VT_GLOBAL_ID_in_global203); 

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:53:1: function : ^( VK_FUNCTION ( data_type )? VT_FUNCTION_ID parameters curly_chunk ) ;
    public final void function() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:54:2: ( ^( VK_FUNCTION ( data_type )? VT_FUNCTION_ID parameters curly_chunk ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:54:4: ^( VK_FUNCTION ( data_type )? VT_FUNCTION_ID parameters curly_chunk )
            {
            match(input,VK_FUNCTION,FOLLOW_VK_FUNCTION_in_function216); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:54:18: ( data_type )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==VT_DATA_TYPE) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:54:18: data_type
                    {
                    pushFollow(FOLLOW_data_type_in_function218);
                    data_type();
                    _fsp--;


                    }
                    break;

            }

            match(input,VT_FUNCTION_ID,FOLLOW_VT_FUNCTION_ID_in_function221); 
            pushFollow(FOLLOW_parameters_in_function223);
            parameters();
            _fsp--;

            pushFollow(FOLLOW_curly_chunk_in_function225);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:57:1: query : ^( VK_QUERY VT_QUERY_ID ( parameters )? lhs_block END ) ;
    public final void query() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:58:2: ( ^( VK_QUERY VT_QUERY_ID ( parameters )? lhs_block END ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:58:4: ^( VK_QUERY VT_QUERY_ID ( parameters )? lhs_block END )
            {
            match(input,VK_QUERY,FOLLOW_VK_QUERY_in_query238); 

            match(input, Token.DOWN, null); 
            match(input,VT_QUERY_ID,FOLLOW_VT_QUERY_ID_in_query240); 
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:58:27: ( parameters )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==VT_PARAM_LIST) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:58:27: parameters
                    {
                    pushFollow(FOLLOW_parameters_in_query242);
                    parameters();
                    _fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_lhs_block_in_query245);
            lhs_block();
            _fsp--;

            match(input,END,FOLLOW_END_in_query247); 

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:61:1: parameters : ^( VT_PARAM_LIST ( param_definition )* ) ;
    public final void parameters() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:62:2: ( ^( VT_PARAM_LIST ( param_definition )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:62:4: ^( VT_PARAM_LIST ( param_definition )* )
            {
            match(input,VT_PARAM_LIST,FOLLOW_VT_PARAM_LIST_in_parameters260); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:62:20: ( param_definition )*
                loop9:
                do {
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==VT_DATA_TYPE||LA9_0==ID) ) {
                        alt9=1;
                    }


                    switch (alt9) {
                	case 1 :
                	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:62:20: param_definition
                	    {
                	    pushFollow(FOLLOW_param_definition_in_parameters262);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:65:1: param_definition : ( data_type )? argument ;
    public final void param_definition() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:66:2: ( ( data_type )? argument )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:66:4: ( data_type )? argument
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:66:4: ( data_type )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==VT_DATA_TYPE) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:66:4: data_type
                    {
                    pushFollow(FOLLOW_data_type_in_param_definition275);
                    data_type();
                    _fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_argument_in_param_definition278);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:69:1: argument : ID ( dimension_definition )* ;
    public final void argument() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:70:2: ( ID ( dimension_definition )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:70:4: ID ( dimension_definition )*
            {
            match(input,ID,FOLLOW_ID_in_argument289); 
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:70:7: ( dimension_definition )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==LEFT_SQUARE) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:70:7: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_argument291);
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


    // $ANTLR start type_declaration
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:73:1: type_declaration : ^( VK_DECLARE VT_TYPE_DECLARE_ID ( decl_metadata )* ( decl_field )* END ) ;
    public final void type_declaration() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:74:2: ( ^( VK_DECLARE VT_TYPE_DECLARE_ID ( decl_metadata )* ( decl_field )* END ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:74:4: ^( VK_DECLARE VT_TYPE_DECLARE_ID ( decl_metadata )* ( decl_field )* END )
            {
            match(input,VK_DECLARE,FOLLOW_VK_DECLARE_in_type_declaration304); 

            match(input, Token.DOWN, null); 
            match(input,VT_TYPE_DECLARE_ID,FOLLOW_VT_TYPE_DECLARE_ID_in_type_declaration306); 
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:74:36: ( decl_metadata )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==AT) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:74:36: decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_type_declaration308);
            	    decl_metadata();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:74:51: ( decl_field )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==ID) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:74:51: decl_field
            	    {
            	    pushFollow(FOLLOW_decl_field_in_type_declaration311);
            	    decl_field();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);

            match(input,END,FOLLOW_END_in_type_declaration314); 

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
    // $ANTLR end type_declaration


    // $ANTLR start decl_metadata
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:77:1: decl_metadata : ^( AT ID VT_PAREN_CHUNK ) ;
    public final void decl_metadata() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:78:2: ( ^( AT ID VT_PAREN_CHUNK ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:78:4: ^( AT ID VT_PAREN_CHUNK )
            {
            match(input,AT,FOLLOW_AT_in_decl_metadata327); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_decl_metadata329); 
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_decl_metadata331); 

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
    // $ANTLR end decl_metadata


    // $ANTLR start decl_field
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:81:1: decl_field : ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* ) ;
    public final void decl_field() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:82:2: ( ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:82:4: ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* )
            {
            match(input,ID,FOLLOW_ID_in_decl_field344); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:82:9: ( decl_field_initialization )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==EQUALS) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:82:9: decl_field_initialization
                    {
                    pushFollow(FOLLOW_decl_field_initialization_in_decl_field346);
                    decl_field_initialization();
                    _fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_data_type_in_decl_field349);
            data_type();
            _fsp--;

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:82:46: ( decl_metadata )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==AT) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:82:46: decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_decl_field351);
            	    decl_metadata();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop15;
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
    // $ANTLR end decl_field


    // $ANTLR start decl_field_initialization
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:85:1: decl_field_initialization : ^( EQUALS VT_PAREN_CHUNK ) ;
    public final void decl_field_initialization() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:86:2: ( ^( EQUALS VT_PAREN_CHUNK ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:86:4: ^( EQUALS VT_PAREN_CHUNK )
            {
            match(input,EQUALS,FOLLOW_EQUALS_in_decl_field_initialization365); 

            match(input, Token.DOWN, null); 
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_decl_field_initialization367); 

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
    // $ANTLR end decl_field_initialization


    // $ANTLR start template
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:89:1: template : ^( VK_TEMPLATE VT_TEMPLATE_ID ( template_slot )+ END ) ;
    public final void template() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:90:2: ( ^( VK_TEMPLATE VT_TEMPLATE_ID ( template_slot )+ END ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:90:4: ^( VK_TEMPLATE VT_TEMPLATE_ID ( template_slot )+ END )
            {
            match(input,VK_TEMPLATE,FOLLOW_VK_TEMPLATE_in_template380); 

            match(input, Token.DOWN, null); 
            match(input,VT_TEMPLATE_ID,FOLLOW_VT_TEMPLATE_ID_in_template382); 
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:90:33: ( template_slot )+
            int cnt16=0;
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==VT_SLOT) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:90:33: template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template384);
            	    template_slot();
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

            match(input,END,FOLLOW_END_in_template387); 

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:93:1: template_slot : ^( VT_SLOT data_type VT_SLOT_ID ) ;
    public final void template_slot() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:94:2: ( ^( VT_SLOT data_type VT_SLOT_ID ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:94:4: ^( VT_SLOT data_type VT_SLOT_ID )
            {
            match(input,VT_SLOT,FOLLOW_VT_SLOT_in_template_slot400); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_data_type_in_template_slot402);
            data_type();
            _fsp--;

            match(input,VT_SLOT_ID,FOLLOW_VT_SLOT_ID_in_template_slot404); 

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:97:1: rule : ^( VK_RULE VT_RULE_ID ( rule_attributes )? ( when_part )? VT_RHS_CHUNK ) ;
    public final void rule() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:98:2: ( ^( VK_RULE VT_RULE_ID ( rule_attributes )? ( when_part )? VT_RHS_CHUNK ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:98:4: ^( VK_RULE VT_RULE_ID ( rule_attributes )? ( when_part )? VT_RHS_CHUNK )
            {
            match(input,VK_RULE,FOLLOW_VK_RULE_in_rule417); 

            match(input, Token.DOWN, null); 
            match(input,VT_RULE_ID,FOLLOW_VT_RULE_ID_in_rule419); 
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:98:25: ( rule_attributes )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==VT_RULE_ATTRIBUTES) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:98:25: rule_attributes
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule421);
                    rule_attributes();
                    _fsp--;


                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:98:42: ( when_part )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==WHEN) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:98:42: when_part
                    {
                    pushFollow(FOLLOW_when_part_in_rule424);
                    when_part();
                    _fsp--;


                    }
                    break;

            }

            match(input,VT_RHS_CHUNK,FOLLOW_VT_RHS_CHUNK_in_rule427); 

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:101:1: when_part : WHEN lhs_block ;
    public final void when_part() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:102:2: ( WHEN lhs_block )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:102:4: WHEN lhs_block
            {
            match(input,WHEN,FOLLOW_WHEN_in_when_part439); 
            pushFollow(FOLLOW_lhs_block_in_when_part441);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:105:1: rule_attributes : ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? ( rule_attribute )+ ) ;
    public final void rule_attributes() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:106:2: ( ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? ( rule_attribute )+ ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:106:4: ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? ( rule_attribute )+ )
            {
            match(input,VT_RULE_ATTRIBUTES,FOLLOW_VT_RULE_ATTRIBUTES_in_rule_attributes453); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:106:25: ( VK_ATTRIBUTES )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==VK_ATTRIBUTES) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:106:25: VK_ATTRIBUTES
                    {
                    match(input,VK_ATTRIBUTES,FOLLOW_VK_ATTRIBUTES_in_rule_attributes455); 

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:106:40: ( rule_attribute )+
            int cnt20=0;
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( ((LA20_0>=VK_DATE_EFFECTIVE && LA20_0<=VK_ENABLED)) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:106:40: rule_attribute
            	    {
            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes458);
            	    rule_attribute();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt20 >= 1 ) break loop20;
                        EarlyExitException eee =
                            new EarlyExitException(20, input);
                        throw eee;
                }
                cnt20++;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:109:1: rule_attribute : ( ^( VK_SALIENCE ( INT | VT_PAREN_CHUNK ) ) | ^( VK_NO_LOOP ( BOOL )? ) | ^( VK_AGENDA_GROUP STRING ) | ^( VK_DURATION INT ) | ^( VK_ACTIVATION_GROUP STRING ) | ^( VK_AUTO_FOCUS ( BOOL )? ) | ^( VK_DATE_EFFECTIVE STRING ) | ^( VK_DATE_EXPIRES STRING ) | ^( VK_ENABLED BOOL ) | ^( VK_RULEFLOW_GROUP STRING ) | ^( VK_LOCK_ON_ACTIVE ( BOOL )? ) | ^( VK_DIALECT STRING ) );
    public final void rule_attribute() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:110:2: ( ^( VK_SALIENCE ( INT | VT_PAREN_CHUNK ) ) | ^( VK_NO_LOOP ( BOOL )? ) | ^( VK_AGENDA_GROUP STRING ) | ^( VK_DURATION INT ) | ^( VK_ACTIVATION_GROUP STRING ) | ^( VK_AUTO_FOCUS ( BOOL )? ) | ^( VK_DATE_EFFECTIVE STRING ) | ^( VK_DATE_EXPIRES STRING ) | ^( VK_ENABLED BOOL ) | ^( VK_RULEFLOW_GROUP STRING ) | ^( VK_LOCK_ON_ACTIVE ( BOOL )? ) | ^( VK_DIALECT STRING ) )
            int alt24=12;
            switch ( input.LA(1) ) {
            case VK_SALIENCE:
                {
                alt24=1;
                }
                break;
            case VK_NO_LOOP:
                {
                alt24=2;
                }
                break;
            case VK_AGENDA_GROUP:
                {
                alt24=3;
                }
                break;
            case VK_DURATION:
                {
                alt24=4;
                }
                break;
            case VK_ACTIVATION_GROUP:
                {
                alt24=5;
                }
                break;
            case VK_AUTO_FOCUS:
                {
                alt24=6;
                }
                break;
            case VK_DATE_EFFECTIVE:
                {
                alt24=7;
                }
                break;
            case VK_DATE_EXPIRES:
                {
                alt24=8;
                }
                break;
            case VK_ENABLED:
                {
                alt24=9;
                }
                break;
            case VK_RULEFLOW_GROUP:
                {
                alt24=10;
                }
                break;
            case VK_LOCK_ON_ACTIVE:
                {
                alt24=11;
                }
                break;
            case VK_DIALECT:
                {
                alt24=12;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("109:1: rule_attribute : ( ^( VK_SALIENCE ( INT | VT_PAREN_CHUNK ) ) | ^( VK_NO_LOOP ( BOOL )? ) | ^( VK_AGENDA_GROUP STRING ) | ^( VK_DURATION INT ) | ^( VK_ACTIVATION_GROUP STRING ) | ^( VK_AUTO_FOCUS ( BOOL )? ) | ^( VK_DATE_EFFECTIVE STRING ) | ^( VK_DATE_EXPIRES STRING ) | ^( VK_ENABLED BOOL ) | ^( VK_RULEFLOW_GROUP STRING ) | ^( VK_LOCK_ON_ACTIVE ( BOOL )? ) | ^( VK_DIALECT STRING ) );", 24, 0, input);

                throw nvae;
            }

            switch (alt24) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:110:4: ^( VK_SALIENCE ( INT | VT_PAREN_CHUNK ) )
                    {
                    match(input,VK_SALIENCE,FOLLOW_VK_SALIENCE_in_rule_attribute472); 

                    match(input, Token.DOWN, null); 
                    if ( input.LA(1)==VT_PAREN_CHUNK||input.LA(1)==INT ) {
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_rule_attribute474);    throw mse;
                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:111:4: ^( VK_NO_LOOP ( BOOL )? )
                    {
                    match(input,VK_NO_LOOP,FOLLOW_VK_NO_LOOP_in_rule_attribute486); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:111:17: ( BOOL )?
                        int alt21=2;
                        int LA21_0 = input.LA(1);

                        if ( (LA21_0==BOOL) ) {
                            alt21=1;
                        }
                        switch (alt21) {
                            case 1 :
                                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:111:17: BOOL
                                {
                                match(input,BOOL,FOLLOW_BOOL_in_rule_attribute488); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:112:4: ^( VK_AGENDA_GROUP STRING )
                    {
                    match(input,VK_AGENDA_GROUP,FOLLOW_VK_AGENDA_GROUP_in_rule_attribute498); 

                    match(input, Token.DOWN, null); 
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute500); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:113:4: ^( VK_DURATION INT )
                    {
                    match(input,VK_DURATION,FOLLOW_VK_DURATION_in_rule_attribute509); 

                    match(input, Token.DOWN, null); 
                    match(input,INT,FOLLOW_INT_in_rule_attribute511); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:114:4: ^( VK_ACTIVATION_GROUP STRING )
                    {
                    match(input,VK_ACTIVATION_GROUP,FOLLOW_VK_ACTIVATION_GROUP_in_rule_attribute521); 

                    match(input, Token.DOWN, null); 
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute523); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:115:4: ^( VK_AUTO_FOCUS ( BOOL )? )
                    {
                    match(input,VK_AUTO_FOCUS,FOLLOW_VK_AUTO_FOCUS_in_rule_attribute531); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:115:20: ( BOOL )?
                        int alt22=2;
                        int LA22_0 = input.LA(1);

                        if ( (LA22_0==BOOL) ) {
                            alt22=1;
                        }
                        switch (alt22) {
                            case 1 :
                                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:115:20: BOOL
                                {
                                match(input,BOOL,FOLLOW_BOOL_in_rule_attribute533); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:116:4: ^( VK_DATE_EFFECTIVE STRING )
                    {
                    match(input,VK_DATE_EFFECTIVE,FOLLOW_VK_DATE_EFFECTIVE_in_rule_attribute542); 

                    match(input, Token.DOWN, null); 
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute544); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:117:4: ^( VK_DATE_EXPIRES STRING )
                    {
                    match(input,VK_DATE_EXPIRES,FOLLOW_VK_DATE_EXPIRES_in_rule_attribute552); 

                    match(input, Token.DOWN, null); 
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute554); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:118:4: ^( VK_ENABLED BOOL )
                    {
                    match(input,VK_ENABLED,FOLLOW_VK_ENABLED_in_rule_attribute562); 

                    match(input, Token.DOWN, null); 
                    match(input,BOOL,FOLLOW_BOOL_in_rule_attribute564); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 10 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:119:4: ^( VK_RULEFLOW_GROUP STRING )
                    {
                    match(input,VK_RULEFLOW_GROUP,FOLLOW_VK_RULEFLOW_GROUP_in_rule_attribute572); 

                    match(input, Token.DOWN, null); 
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute574); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 11 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:120:4: ^( VK_LOCK_ON_ACTIVE ( BOOL )? )
                    {
                    match(input,VK_LOCK_ON_ACTIVE,FOLLOW_VK_LOCK_ON_ACTIVE_in_rule_attribute582); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:120:24: ( BOOL )?
                        int alt23=2;
                        int LA23_0 = input.LA(1);

                        if ( (LA23_0==BOOL) ) {
                            alt23=1;
                        }
                        switch (alt23) {
                            case 1 :
                                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:120:24: BOOL
                                {
                                match(input,BOOL,FOLLOW_BOOL_in_rule_attribute584); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 12 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:121:4: ^( VK_DIALECT STRING )
                    {
                    match(input,VK_DIALECT,FOLLOW_VK_DIALECT_in_rule_attribute592); 

                    match(input, Token.DOWN, null); 
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute594); 

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:124:1: lhs_block : ^( VT_AND_IMPLICIT ( lhs )* ) ;
    public final void lhs_block() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:125:2: ( ^( VT_AND_IMPLICIT ( lhs )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:125:4: ^( VT_AND_IMPLICIT ( lhs )* )
            {
            match(input,VT_AND_IMPLICIT,FOLLOW_VT_AND_IMPLICIT_in_lhs_block608); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:125:22: ( lhs )*
                loop25:
                do {
                    int alt25=2;
                    int LA25_0 = input.LA(1);

                    if ( ((LA25_0>=VT_AND_PREFIX && LA25_0<=VT_OR_INFIX)||LA25_0==VT_PATTERN||LA25_0==VK_EVAL||LA25_0==VK_NOT||(LA25_0>=VK_EXISTS && LA25_0<=VK_FORALL)||LA25_0==FROM) ) {
                        alt25=1;
                    }


                    switch (alt25) {
                	case 1 :
                	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:125:22: lhs
                	    {
                	    pushFollow(FOLLOW_lhs_in_lhs_block610);
                	    lhs();
                	    _fsp--;


                	    }
                	    break;

                	default :
                	    break loop25;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:128:1: lhs : ( ^( VT_OR_PREFIX ( lhs )+ ) | ^( VT_OR_INFIX lhs lhs ) | ^( VT_AND_PREFIX ( lhs )+ ) | ^( VT_AND_INFIX lhs lhs ) | ^( VK_EXISTS lhs ) | ^( VK_NOT lhs ) | ^( VK_EVAL VT_PAREN_CHUNK ) | ^( VK_FORALL ( lhs )+ ) | ^( FROM lhs_pattern from_elements ) | lhs_pattern );
    public final void lhs() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:128:5: ( ^( VT_OR_PREFIX ( lhs )+ ) | ^( VT_OR_INFIX lhs lhs ) | ^( VT_AND_PREFIX ( lhs )+ ) | ^( VT_AND_INFIX lhs lhs ) | ^( VK_EXISTS lhs ) | ^( VK_NOT lhs ) | ^( VK_EVAL VT_PAREN_CHUNK ) | ^( VK_FORALL ( lhs )+ ) | ^( FROM lhs_pattern from_elements ) | lhs_pattern )
            int alt29=10;
            switch ( input.LA(1) ) {
            case VT_OR_PREFIX:
                {
                alt29=1;
                }
                break;
            case VT_OR_INFIX:
                {
                alt29=2;
                }
                break;
            case VT_AND_PREFIX:
                {
                alt29=3;
                }
                break;
            case VT_AND_INFIX:
                {
                alt29=4;
                }
                break;
            case VK_EXISTS:
                {
                alt29=5;
                }
                break;
            case VK_NOT:
                {
                alt29=6;
                }
                break;
            case VK_EVAL:
                {
                alt29=7;
                }
                break;
            case VK_FORALL:
                {
                alt29=8;
                }
                break;
            case FROM:
                {
                alt29=9;
                }
                break;
            case VT_PATTERN:
                {
                alt29=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("128:1: lhs : ( ^( VT_OR_PREFIX ( lhs )+ ) | ^( VT_OR_INFIX lhs lhs ) | ^( VT_AND_PREFIX ( lhs )+ ) | ^( VT_AND_INFIX lhs lhs ) | ^( VK_EXISTS lhs ) | ^( VK_NOT lhs ) | ^( VK_EVAL VT_PAREN_CHUNK ) | ^( VK_FORALL ( lhs )+ ) | ^( FROM lhs_pattern from_elements ) | lhs_pattern );", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:128:7: ^( VT_OR_PREFIX ( lhs )+ )
                    {
                    match(input,VT_OR_PREFIX,FOLLOW_VT_OR_PREFIX_in_lhs623); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:128:22: ( lhs )+
                    int cnt26=0;
                    loop26:
                    do {
                        int alt26=2;
                        int LA26_0 = input.LA(1);

                        if ( ((LA26_0>=VT_AND_PREFIX && LA26_0<=VT_OR_INFIX)||LA26_0==VT_PATTERN||LA26_0==VK_EVAL||LA26_0==VK_NOT||(LA26_0>=VK_EXISTS && LA26_0<=VK_FORALL)||LA26_0==FROM) ) {
                            alt26=1;
                        }


                        switch (alt26) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:128:22: lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs625);
                    	    lhs();
                    	    _fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt26 >= 1 ) break loop26;
                                EarlyExitException eee =
                                    new EarlyExitException(26, input);
                                throw eee;
                        }
                        cnt26++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:129:4: ^( VT_OR_INFIX lhs lhs )
                    {
                    match(input,VT_OR_INFIX,FOLLOW_VT_OR_INFIX_in_lhs633); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs635);
                    lhs();
                    _fsp--;

                    pushFollow(FOLLOW_lhs_in_lhs637);
                    lhs();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:130:4: ^( VT_AND_PREFIX ( lhs )+ )
                    {
                    match(input,VT_AND_PREFIX,FOLLOW_VT_AND_PREFIX_in_lhs644); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:130:20: ( lhs )+
                    int cnt27=0;
                    loop27:
                    do {
                        int alt27=2;
                        int LA27_0 = input.LA(1);

                        if ( ((LA27_0>=VT_AND_PREFIX && LA27_0<=VT_OR_INFIX)||LA27_0==VT_PATTERN||LA27_0==VK_EVAL||LA27_0==VK_NOT||(LA27_0>=VK_EXISTS && LA27_0<=VK_FORALL)||LA27_0==FROM) ) {
                            alt27=1;
                        }


                        switch (alt27) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:130:20: lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs646);
                    	    lhs();
                    	    _fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt27 >= 1 ) break loop27;
                                EarlyExitException eee =
                                    new EarlyExitException(27, input);
                                throw eee;
                        }
                        cnt27++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:131:4: ^( VT_AND_INFIX lhs lhs )
                    {
                    match(input,VT_AND_INFIX,FOLLOW_VT_AND_INFIX_in_lhs654); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs656);
                    lhs();
                    _fsp--;

                    pushFollow(FOLLOW_lhs_in_lhs658);
                    lhs();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:132:4: ^( VK_EXISTS lhs )
                    {
                    match(input,VK_EXISTS,FOLLOW_VK_EXISTS_in_lhs665); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs667);
                    lhs();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:133:4: ^( VK_NOT lhs )
                    {
                    match(input,VK_NOT,FOLLOW_VK_NOT_in_lhs674); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs676);
                    lhs();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:134:4: ^( VK_EVAL VT_PAREN_CHUNK )
                    {
                    match(input,VK_EVAL,FOLLOW_VK_EVAL_in_lhs683); 

                    match(input, Token.DOWN, null); 
                    match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_lhs685); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:135:4: ^( VK_FORALL ( lhs )+ )
                    {
                    match(input,VK_FORALL,FOLLOW_VK_FORALL_in_lhs692); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:135:16: ( lhs )+
                    int cnt28=0;
                    loop28:
                    do {
                        int alt28=2;
                        int LA28_0 = input.LA(1);

                        if ( ((LA28_0>=VT_AND_PREFIX && LA28_0<=VT_OR_INFIX)||LA28_0==VT_PATTERN||LA28_0==VK_EVAL||LA28_0==VK_NOT||(LA28_0>=VK_EXISTS && LA28_0<=VK_FORALL)||LA28_0==FROM) ) {
                            alt28=1;
                        }


                        switch (alt28) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:135:16: lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs694);
                    	    lhs();
                    	    _fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt28 >= 1 ) break loop28;
                                EarlyExitException eee =
                                    new EarlyExitException(28, input);
                                throw eee;
                        }
                        cnt28++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:136:4: ^( FROM lhs_pattern from_elements )
                    {
                    match(input,FROM,FOLLOW_FROM_in_lhs702); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_pattern_in_lhs704);
                    lhs_pattern();
                    _fsp--;

                    pushFollow(FOLLOW_from_elements_in_lhs706);
                    from_elements();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 10 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:137:4: lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs712);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:140:1: from_elements : ( ^( ACCUMULATE lhs ( accumulate_init_clause | accumulate_id_clause ) ) | ^( COLLECT lhs ) | ^( VK_ENTRY_POINT VT_ENTRYPOINT_ID ) | ^( VT_FROM_SOURCE ID ( VT_PAREN_CHUNK )? ( expression_chain )? ) );
    public final void from_elements() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:141:2: ( ^( ACCUMULATE lhs ( accumulate_init_clause | accumulate_id_clause ) ) | ^( COLLECT lhs ) | ^( VK_ENTRY_POINT VT_ENTRYPOINT_ID ) | ^( VT_FROM_SOURCE ID ( VT_PAREN_CHUNK )? ( expression_chain )? ) )
            int alt33=4;
            switch ( input.LA(1) ) {
            case ACCUMULATE:
                {
                alt33=1;
                }
                break;
            case COLLECT:
                {
                alt33=2;
                }
                break;
            case VK_ENTRY_POINT:
                {
                alt33=3;
                }
                break;
            case VT_FROM_SOURCE:
                {
                alt33=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("140:1: from_elements : ( ^( ACCUMULATE lhs ( accumulate_init_clause | accumulate_id_clause ) ) | ^( COLLECT lhs ) | ^( VK_ENTRY_POINT VT_ENTRYPOINT_ID ) | ^( VT_FROM_SOURCE ID ( VT_PAREN_CHUNK )? ( expression_chain )? ) );", 33, 0, input);

                throw nvae;
            }

            switch (alt33) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:141:4: ^( ACCUMULATE lhs ( accumulate_init_clause | accumulate_id_clause ) )
                    {
                    match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_from_elements724); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_from_elements726);
                    lhs();
                    _fsp--;

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:141:21: ( accumulate_init_clause | accumulate_id_clause )
                    int alt30=2;
                    int LA30_0 = input.LA(1);

                    if ( (LA30_0==VT_ACCUMULATE_INIT_CLAUSE) ) {
                        alt30=1;
                    }
                    else if ( (LA30_0==VT_ACCUMULATE_ID_CLAUSE) ) {
                        alt30=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("141:21: ( accumulate_init_clause | accumulate_id_clause )", 30, 0, input);

                        throw nvae;
                    }
                    switch (alt30) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:141:22: accumulate_init_clause
                            {
                            pushFollow(FOLLOW_accumulate_init_clause_in_from_elements729);
                            accumulate_init_clause();
                            _fsp--;


                            }
                            break;
                        case 2 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:141:45: accumulate_id_clause
                            {
                            pushFollow(FOLLOW_accumulate_id_clause_in_from_elements731);
                            accumulate_id_clause();
                            _fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:142:4: ^( COLLECT lhs )
                    {
                    match(input,COLLECT,FOLLOW_COLLECT_in_from_elements739); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_from_elements741);
                    lhs();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:143:4: ^( VK_ENTRY_POINT VT_ENTRYPOINT_ID )
                    {
                    match(input,VK_ENTRY_POINT,FOLLOW_VK_ENTRY_POINT_in_from_elements748); 

                    match(input, Token.DOWN, null); 
                    match(input,VT_ENTRYPOINT_ID,FOLLOW_VT_ENTRYPOINT_ID_in_from_elements750); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:144:4: ^( VT_FROM_SOURCE ID ( VT_PAREN_CHUNK )? ( expression_chain )? )
                    {
                    match(input,VT_FROM_SOURCE,FOLLOW_VT_FROM_SOURCE_in_from_elements757); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_from_elements759); 
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:144:24: ( VT_PAREN_CHUNK )?
                    int alt31=2;
                    int LA31_0 = input.LA(1);

                    if ( (LA31_0==VT_PAREN_CHUNK) ) {
                        alt31=1;
                    }
                    switch (alt31) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:144:24: VT_PAREN_CHUNK
                            {
                            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_from_elements761); 

                            }
                            break;

                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:144:40: ( expression_chain )?
                    int alt32=2;
                    int LA32_0 = input.LA(1);

                    if ( (LA32_0==VT_EXPRESSION_CHAIN) ) {
                        alt32=1;
                    }
                    switch (alt32) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:144:40: expression_chain
                            {
                            pushFollow(FOLLOW_expression_chain_in_from_elements764);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:147:1: accumulate_init_clause : ^( VT_ACCUMULATE_INIT_CLAUSE ^( INIT VT_PAREN_CHUNK ) ^( VK_ACTION VT_PAREN_CHUNK ) ( accumulate_init_reverse_clause )? ^( VK_RESULT VT_PAREN_CHUNK ) ) ;
    public final void accumulate_init_clause() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:148:2: ( ^( VT_ACCUMULATE_INIT_CLAUSE ^( INIT VT_PAREN_CHUNK ) ^( VK_ACTION VT_PAREN_CHUNK ) ( accumulate_init_reverse_clause )? ^( VK_RESULT VT_PAREN_CHUNK ) ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:148:4: ^( VT_ACCUMULATE_INIT_CLAUSE ^( INIT VT_PAREN_CHUNK ) ^( VK_ACTION VT_PAREN_CHUNK ) ( accumulate_init_reverse_clause )? ^( VK_RESULT VT_PAREN_CHUNK ) )
            {
            match(input,VT_ACCUMULATE_INIT_CLAUSE,FOLLOW_VT_ACCUMULATE_INIT_CLAUSE_in_accumulate_init_clause778); 

            match(input, Token.DOWN, null); 
            match(input,INIT,FOLLOW_INIT_in_accumulate_init_clause785); 

            match(input, Token.DOWN, null); 
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause787); 

            match(input, Token.UP, null); 
            match(input,VK_ACTION,FOLLOW_VK_ACTION_in_accumulate_init_clause795); 

            match(input, Token.DOWN, null); 
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause797); 

            match(input, Token.UP, null); 
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:151:4: ( accumulate_init_reverse_clause )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==VK_REVERSE) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:151:4: accumulate_init_reverse_clause
                    {
                    pushFollow(FOLLOW_accumulate_init_reverse_clause_in_accumulate_init_clause804);
                    accumulate_init_reverse_clause();
                    _fsp--;


                    }
                    break;

            }

            match(input,VK_RESULT,FOLLOW_VK_RESULT_in_accumulate_init_clause811); 

            match(input, Token.DOWN, null); 
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause813); 

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:155:1: accumulate_init_reverse_clause : ^( VK_REVERSE VT_PAREN_CHUNK ) ;
    public final void accumulate_init_reverse_clause() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:156:2: ( ^( VK_REVERSE VT_PAREN_CHUNK ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:156:4: ^( VK_REVERSE VT_PAREN_CHUNK )
            {
            match(input,VK_REVERSE,FOLLOW_VK_REVERSE_in_accumulate_init_reverse_clause827); 

            match(input, Token.DOWN, null); 
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_reverse_clause829); 

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:160:1: accumulate_id_clause : ^( VT_ACCUMULATE_ID_CLAUSE ID VT_PAREN_CHUNK ) ;
    public final void accumulate_id_clause() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:161:2: ( ^( VT_ACCUMULATE_ID_CLAUSE ID VT_PAREN_CHUNK ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:161:4: ^( VT_ACCUMULATE_ID_CLAUSE ID VT_PAREN_CHUNK )
            {
            match(input,VT_ACCUMULATE_ID_CLAUSE,FOLLOW_VT_ACCUMULATE_ID_CLAUSE_in_accumulate_id_clause843); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_accumulate_id_clause845); 
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_id_clause847); 

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:164:1: lhs_pattern : ^( VT_PATTERN fact_expression ) ( over_clause )? ;
    public final void lhs_pattern() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:165:2: ( ^( VT_PATTERN fact_expression ) ( over_clause )? )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:165:4: ^( VT_PATTERN fact_expression ) ( over_clause )?
            {
            match(input,VT_PATTERN,FOLLOW_VT_PATTERN_in_lhs_pattern860); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_fact_expression_in_lhs_pattern862);
            fact_expression();
            _fsp--;


            match(input, Token.UP, null); 
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:165:34: ( over_clause )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==OVER) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:165:34: over_clause
                    {
                    pushFollow(FOLLOW_over_clause_in_lhs_pattern865);
                    over_clause();
                    _fsp--;


                    }
                    break;

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
    // $ANTLR end lhs_pattern


    // $ANTLR start over_clause
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:168:1: over_clause : ^( OVER ( over_element )+ ) ;
    public final void over_clause() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:169:2: ( ^( OVER ( over_element )+ ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:169:4: ^( OVER ( over_element )+ )
            {
            match(input,OVER,FOLLOW_OVER_in_over_clause878); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:169:11: ( over_element )+
            int cnt36=0;
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( (LA36_0==VT_BEHAVIOR) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:169:11: over_element
            	    {
            	    pushFollow(FOLLOW_over_element_in_over_clause880);
            	    over_element();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt36 >= 1 ) break loop36;
                        EarlyExitException eee =
                            new EarlyExitException(36, input);
                        throw eee;
                }
                cnt36++;
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
    // $ANTLR end over_clause


    // $ANTLR start over_element
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:172:1: over_element : ^( VT_BEHAVIOR ID ID VT_PAREN_CHUNK ) ;
    public final void over_element() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:173:2: ( ^( VT_BEHAVIOR ID ID VT_PAREN_CHUNK ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:173:4: ^( VT_BEHAVIOR ID ID VT_PAREN_CHUNK )
            {
            match(input,VT_BEHAVIOR,FOLLOW_VT_BEHAVIOR_in_over_element894); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_over_element896); 
            match(input,ID,FOLLOW_ID_in_over_element898); 
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_over_element900); 

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
    // $ANTLR end over_element


    // $ANTLR start fact_expression
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:176:1: fact_expression : ( ^( DOUBLE_PIPE fact_expression fact_expression ) | ^( DOUBLE_AMPER fact_expression fact_expression ) | ^( VT_FACT_BINDING VT_LABEL fact_expression ) | ^( VT_FACT pattern_type ( fact_expression )* ) | ^( VT_FACT_OR fact_expression fact_expression ) | ^( VK_EVAL VT_PAREN_CHUNK ) | ^( VK_IN ( VK_NOT )? ( fact_expression )+ ) | ^( EQUAL fact_expression ) | ^( GREATER fact_expression ) | ^( GREATER_EQUAL fact_expression ) | ^( LESS fact_expression ) | ^( LESS_EQUAL fact_expression ) | ^( NOT_EQUAL fact_expression ) | ^( VK_CONTAINS ( VK_NOT )? fact_expression ) | ^( VK_EXCLUDES ( VK_NOT )? fact_expression ) | ^( VK_MATCHES ( VK_NOT )? fact_expression ) | ^( VK_SOUNDSLIKE ( VK_NOT )? fact_expression ) | ^( VK_MEMBEROF ( VK_NOT )? fact_expression ) | ^( ID ( VK_NOT )? ( VT_SQUARE_CHUNK )? fact_expression ) | ^( VT_BIND_FIELD VT_LABEL fact_expression ) | ^( VT_FIELD fact_expression ( fact_expression )? ) | ^( VT_ACCESSOR_PATH ( accessor_element )+ ) | STRING | INT | FLOAT | BOOL | NULL | VT_PAREN_CHUNK );
    public final void fact_expression() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:177:2: ( ^( DOUBLE_PIPE fact_expression fact_expression ) | ^( DOUBLE_AMPER fact_expression fact_expression ) | ^( VT_FACT_BINDING VT_LABEL fact_expression ) | ^( VT_FACT pattern_type ( fact_expression )* ) | ^( VT_FACT_OR fact_expression fact_expression ) | ^( VK_EVAL VT_PAREN_CHUNK ) | ^( VK_IN ( VK_NOT )? ( fact_expression )+ ) | ^( EQUAL fact_expression ) | ^( GREATER fact_expression ) | ^( GREATER_EQUAL fact_expression ) | ^( LESS fact_expression ) | ^( LESS_EQUAL fact_expression ) | ^( NOT_EQUAL fact_expression ) | ^( VK_CONTAINS ( VK_NOT )? fact_expression ) | ^( VK_EXCLUDES ( VK_NOT )? fact_expression ) | ^( VK_MATCHES ( VK_NOT )? fact_expression ) | ^( VK_SOUNDSLIKE ( VK_NOT )? fact_expression ) | ^( VK_MEMBEROF ( VK_NOT )? fact_expression ) | ^( ID ( VK_NOT )? ( VT_SQUARE_CHUNK )? fact_expression ) | ^( VT_BIND_FIELD VT_LABEL fact_expression ) | ^( VT_FIELD fact_expression ( fact_expression )? ) | ^( VT_ACCESSOR_PATH ( accessor_element )+ ) | STRING | INT | FLOAT | BOOL | NULL | VT_PAREN_CHUNK )
            int alt49=28;
            switch ( input.LA(1) ) {
            case DOUBLE_PIPE:
                {
                alt49=1;
                }
                break;
            case DOUBLE_AMPER:
                {
                alt49=2;
                }
                break;
            case VT_FACT_BINDING:
                {
                alt49=3;
                }
                break;
            case VT_FACT:
                {
                alt49=4;
                }
                break;
            case VT_FACT_OR:
                {
                alt49=5;
                }
                break;
            case VK_EVAL:
                {
                alt49=6;
                }
                break;
            case VK_IN:
                {
                alt49=7;
                }
                break;
            case EQUAL:
                {
                alt49=8;
                }
                break;
            case GREATER:
                {
                alt49=9;
                }
                break;
            case GREATER_EQUAL:
                {
                alt49=10;
                }
                break;
            case LESS:
                {
                alt49=11;
                }
                break;
            case LESS_EQUAL:
                {
                alt49=12;
                }
                break;
            case NOT_EQUAL:
                {
                alt49=13;
                }
                break;
            case VK_CONTAINS:
                {
                alt49=14;
                }
                break;
            case VK_EXCLUDES:
                {
                alt49=15;
                }
                break;
            case VK_MATCHES:
                {
                alt49=16;
                }
                break;
            case VK_SOUNDSLIKE:
                {
                alt49=17;
                }
                break;
            case VK_MEMBEROF:
                {
                alt49=18;
                }
                break;
            case ID:
                {
                alt49=19;
                }
                break;
            case VT_BIND_FIELD:
                {
                alt49=20;
                }
                break;
            case VT_FIELD:
                {
                alt49=21;
                }
                break;
            case VT_ACCESSOR_PATH:
                {
                alt49=22;
                }
                break;
            case STRING:
                {
                alt49=23;
                }
                break;
            case INT:
                {
                alt49=24;
                }
                break;
            case FLOAT:
                {
                alt49=25;
                }
                break;
            case BOOL:
                {
                alt49=26;
                }
                break;
            case NULL:
                {
                alt49=27;
                }
                break;
            case VT_PAREN_CHUNK:
                {
                alt49=28;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("176:1: fact_expression : ( ^( DOUBLE_PIPE fact_expression fact_expression ) | ^( DOUBLE_AMPER fact_expression fact_expression ) | ^( VT_FACT_BINDING VT_LABEL fact_expression ) | ^( VT_FACT pattern_type ( fact_expression )* ) | ^( VT_FACT_OR fact_expression fact_expression ) | ^( VK_EVAL VT_PAREN_CHUNK ) | ^( VK_IN ( VK_NOT )? ( fact_expression )+ ) | ^( EQUAL fact_expression ) | ^( GREATER fact_expression ) | ^( GREATER_EQUAL fact_expression ) | ^( LESS fact_expression ) | ^( LESS_EQUAL fact_expression ) | ^( NOT_EQUAL fact_expression ) | ^( VK_CONTAINS ( VK_NOT )? fact_expression ) | ^( VK_EXCLUDES ( VK_NOT )? fact_expression ) | ^( VK_MATCHES ( VK_NOT )? fact_expression ) | ^( VK_SOUNDSLIKE ( VK_NOT )? fact_expression ) | ^( VK_MEMBEROF ( VK_NOT )? fact_expression ) | ^( ID ( VK_NOT )? ( VT_SQUARE_CHUNK )? fact_expression ) | ^( VT_BIND_FIELD VT_LABEL fact_expression ) | ^( VT_FIELD fact_expression ( fact_expression )? ) | ^( VT_ACCESSOR_PATH ( accessor_element )+ ) | STRING | INT | FLOAT | BOOL | NULL | VT_PAREN_CHUNK );", 49, 0, input);

                throw nvae;
            }

            switch (alt49) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:177:4: ^( DOUBLE_PIPE fact_expression fact_expression )
                    {
                    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_fact_expression913); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression915);
                    fact_expression();
                    _fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression917);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:178:4: ^( DOUBLE_AMPER fact_expression fact_expression )
                    {
                    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_fact_expression924); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression926);
                    fact_expression();
                    _fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression928);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:179:4: ^( VT_FACT_BINDING VT_LABEL fact_expression )
                    {
                    match(input,VT_FACT_BINDING,FOLLOW_VT_FACT_BINDING_in_fact_expression935); 

                    match(input, Token.DOWN, null); 
                    match(input,VT_LABEL,FOLLOW_VT_LABEL_in_fact_expression937); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression939);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:180:4: ^( VT_FACT pattern_type ( fact_expression )* )
                    {
                    match(input,VT_FACT,FOLLOW_VT_FACT_in_fact_expression946); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_pattern_type_in_fact_expression948);
                    pattern_type();
                    _fsp--;

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:180:27: ( fact_expression )*
                    loop37:
                    do {
                        int alt37=2;
                        int LA37_0 = input.LA(1);

                        if ( (LA37_0==VT_FACT||LA37_0==VT_PAREN_CHUNK||(LA37_0>=VT_FACT_BINDING && LA37_0<=VT_ACCESSOR_PATH)||(LA37_0>=VK_EVAL && LA37_0<=VK_MEMBEROF)||LA37_0==VK_IN||LA37_0==ID||LA37_0==STRING||(LA37_0>=BOOL && LA37_0<=DOUBLE_AMPER)||(LA37_0>=EQUAL && LA37_0<=NOT_EQUAL)||(LA37_0>=FLOAT && LA37_0<=NULL)) ) {
                            alt37=1;
                        }


                        switch (alt37) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:180:27: fact_expression
                    	    {
                    	    pushFollow(FOLLOW_fact_expression_in_fact_expression950);
                    	    fact_expression();
                    	    _fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop37;
                        }
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:181:4: ^( VT_FACT_OR fact_expression fact_expression )
                    {
                    match(input,VT_FACT_OR,FOLLOW_VT_FACT_OR_in_fact_expression958); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression960);
                    fact_expression();
                    _fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression962);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:182:4: ^( VK_EVAL VT_PAREN_CHUNK )
                    {
                    match(input,VK_EVAL,FOLLOW_VK_EVAL_in_fact_expression969); 

                    match(input, Token.DOWN, null); 
                    match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_fact_expression971); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:183:4: ^( VK_IN ( VK_NOT )? ( fact_expression )+ )
                    {
                    match(input,VK_IN,FOLLOW_VK_IN_in_fact_expression978); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:183:12: ( VK_NOT )?
                    int alt38=2;
                    int LA38_0 = input.LA(1);

                    if ( (LA38_0==VK_NOT) ) {
                        alt38=1;
                    }
                    switch (alt38) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:183:12: VK_NOT
                            {
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression980); 

                            }
                            break;

                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:183:20: ( fact_expression )+
                    int cnt39=0;
                    loop39:
                    do {
                        int alt39=2;
                        int LA39_0 = input.LA(1);

                        if ( (LA39_0==VT_FACT||LA39_0==VT_PAREN_CHUNK||(LA39_0>=VT_FACT_BINDING && LA39_0<=VT_ACCESSOR_PATH)||(LA39_0>=VK_EVAL && LA39_0<=VK_MEMBEROF)||LA39_0==VK_IN||LA39_0==ID||LA39_0==STRING||(LA39_0>=BOOL && LA39_0<=DOUBLE_AMPER)||(LA39_0>=EQUAL && LA39_0<=NOT_EQUAL)||(LA39_0>=FLOAT && LA39_0<=NULL)) ) {
                            alt39=1;
                        }


                        switch (alt39) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:183:20: fact_expression
                    	    {
                    	    pushFollow(FOLLOW_fact_expression_in_fact_expression983);
                    	    fact_expression();
                    	    _fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt39 >= 1 ) break loop39;
                                EarlyExitException eee =
                                    new EarlyExitException(39, input);
                                throw eee;
                        }
                        cnt39++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:184:4: ^( EQUAL fact_expression )
                    {
                    match(input,EQUAL,FOLLOW_EQUAL_in_fact_expression991); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression993);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:185:4: ^( GREATER fact_expression )
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_fact_expression1000); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1002);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 10 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:186:4: ^( GREATER_EQUAL fact_expression )
                    {
                    match(input,GREATER_EQUAL,FOLLOW_GREATER_EQUAL_in_fact_expression1009); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1011);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 11 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:187:4: ^( LESS fact_expression )
                    {
                    match(input,LESS,FOLLOW_LESS_in_fact_expression1018); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1020);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 12 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:188:4: ^( LESS_EQUAL fact_expression )
                    {
                    match(input,LESS_EQUAL,FOLLOW_LESS_EQUAL_in_fact_expression1027); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1029);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 13 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:189:4: ^( NOT_EQUAL fact_expression )
                    {
                    match(input,NOT_EQUAL,FOLLOW_NOT_EQUAL_in_fact_expression1036); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1038);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 14 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:190:4: ^( VK_CONTAINS ( VK_NOT )? fact_expression )
                    {
                    match(input,VK_CONTAINS,FOLLOW_VK_CONTAINS_in_fact_expression1045); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:190:18: ( VK_NOT )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);

                    if ( (LA40_0==VK_NOT) ) {
                        alt40=1;
                    }
                    switch (alt40) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:190:18: VK_NOT
                            {
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1047); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1050);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 15 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:191:4: ^( VK_EXCLUDES ( VK_NOT )? fact_expression )
                    {
                    match(input,VK_EXCLUDES,FOLLOW_VK_EXCLUDES_in_fact_expression1057); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:191:18: ( VK_NOT )?
                    int alt41=2;
                    int LA41_0 = input.LA(1);

                    if ( (LA41_0==VK_NOT) ) {
                        alt41=1;
                    }
                    switch (alt41) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:191:18: VK_NOT
                            {
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1059); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1062);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 16 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:192:4: ^( VK_MATCHES ( VK_NOT )? fact_expression )
                    {
                    match(input,VK_MATCHES,FOLLOW_VK_MATCHES_in_fact_expression1069); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:192:17: ( VK_NOT )?
                    int alt42=2;
                    int LA42_0 = input.LA(1);

                    if ( (LA42_0==VK_NOT) ) {
                        alt42=1;
                    }
                    switch (alt42) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:192:17: VK_NOT
                            {
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1071); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1074);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 17 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:193:4: ^( VK_SOUNDSLIKE ( VK_NOT )? fact_expression )
                    {
                    match(input,VK_SOUNDSLIKE,FOLLOW_VK_SOUNDSLIKE_in_fact_expression1081); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:193:20: ( VK_NOT )?
                    int alt43=2;
                    int LA43_0 = input.LA(1);

                    if ( (LA43_0==VK_NOT) ) {
                        alt43=1;
                    }
                    switch (alt43) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:193:20: VK_NOT
                            {
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1083); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1086);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 18 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:194:4: ^( VK_MEMBEROF ( VK_NOT )? fact_expression )
                    {
                    match(input,VK_MEMBEROF,FOLLOW_VK_MEMBEROF_in_fact_expression1093); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:194:18: ( VK_NOT )?
                    int alt44=2;
                    int LA44_0 = input.LA(1);

                    if ( (LA44_0==VK_NOT) ) {
                        alt44=1;
                    }
                    switch (alt44) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:194:18: VK_NOT
                            {
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1095); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1098);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 19 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:195:4: ^( ID ( VK_NOT )? ( VT_SQUARE_CHUNK )? fact_expression )
                    {
                    match(input,ID,FOLLOW_ID_in_fact_expression1105); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:195:9: ( VK_NOT )?
                    int alt45=2;
                    int LA45_0 = input.LA(1);

                    if ( (LA45_0==VK_NOT) ) {
                        alt45=1;
                    }
                    switch (alt45) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:195:9: VK_NOT
                            {
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1107); 

                            }
                            break;

                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:195:17: ( VT_SQUARE_CHUNK )?
                    int alt46=2;
                    int LA46_0 = input.LA(1);

                    if ( (LA46_0==VT_SQUARE_CHUNK) ) {
                        alt46=1;
                    }
                    switch (alt46) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:195:17: VT_SQUARE_CHUNK
                            {
                            match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_fact_expression1110); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1113);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 20 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:196:4: ^( VT_BIND_FIELD VT_LABEL fact_expression )
                    {
                    match(input,VT_BIND_FIELD,FOLLOW_VT_BIND_FIELD_in_fact_expression1120); 

                    match(input, Token.DOWN, null); 
                    match(input,VT_LABEL,FOLLOW_VT_LABEL_in_fact_expression1122); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1124);
                    fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 21 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:197:4: ^( VT_FIELD fact_expression ( fact_expression )? )
                    {
                    match(input,VT_FIELD,FOLLOW_VT_FIELD_in_fact_expression1131); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1133);
                    fact_expression();
                    _fsp--;

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:197:31: ( fact_expression )?
                    int alt47=2;
                    int LA47_0 = input.LA(1);

                    if ( (LA47_0==VT_FACT||LA47_0==VT_PAREN_CHUNK||(LA47_0>=VT_FACT_BINDING && LA47_0<=VT_ACCESSOR_PATH)||(LA47_0>=VK_EVAL && LA47_0<=VK_MEMBEROF)||LA47_0==VK_IN||LA47_0==ID||LA47_0==STRING||(LA47_0>=BOOL && LA47_0<=DOUBLE_AMPER)||(LA47_0>=EQUAL && LA47_0<=NOT_EQUAL)||(LA47_0>=FLOAT && LA47_0<=NULL)) ) {
                        alt47=1;
                    }
                    switch (alt47) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:197:31: fact_expression
                            {
                            pushFollow(FOLLOW_fact_expression_in_fact_expression1135);
                            fact_expression();
                            _fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 22 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:198:4: ^( VT_ACCESSOR_PATH ( accessor_element )+ )
                    {
                    match(input,VT_ACCESSOR_PATH,FOLLOW_VT_ACCESSOR_PATH_in_fact_expression1143); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:198:23: ( accessor_element )+
                    int cnt48=0;
                    loop48:
                    do {
                        int alt48=2;
                        int LA48_0 = input.LA(1);

                        if ( (LA48_0==VT_ACCESSOR_ELEMENT) ) {
                            alt48=1;
                        }


                        switch (alt48) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:198:23: accessor_element
                    	    {
                    	    pushFollow(FOLLOW_accessor_element_in_fact_expression1145);
                    	    accessor_element();
                    	    _fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt48 >= 1 ) break loop48;
                                EarlyExitException eee =
                                    new EarlyExitException(48, input);
                                throw eee;
                        }
                        cnt48++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 23 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:199:4: STRING
                    {
                    match(input,STRING,FOLLOW_STRING_in_fact_expression1152); 

                    }
                    break;
                case 24 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:200:4: INT
                    {
                    match(input,INT,FOLLOW_INT_in_fact_expression1157); 

                    }
                    break;
                case 25 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:201:4: FLOAT
                    {
                    match(input,FLOAT,FOLLOW_FLOAT_in_fact_expression1162); 

                    }
                    break;
                case 26 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:202:4: BOOL
                    {
                    match(input,BOOL,FOLLOW_BOOL_in_fact_expression1167); 

                    }
                    break;
                case 27 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:203:4: NULL
                    {
                    match(input,NULL,FOLLOW_NULL_in_fact_expression1172); 

                    }
                    break;
                case 28 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:204:4: VT_PAREN_CHUNK
                    {
                    match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_fact_expression1177); 

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:207:1: pattern_type : ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) ;
    public final void pattern_type() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:208:2: ( ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:208:4: ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* )
            {
            match(input,VT_PATTERN_TYPE,FOLLOW_VT_PATTERN_TYPE_in_pattern_type1189); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:208:22: ( ID )+
            int cnt50=0;
            loop50:
            do {
                int alt50=2;
                int LA50_0 = input.LA(1);

                if ( (LA50_0==ID) ) {
                    alt50=1;
                }


                switch (alt50) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:208:22: ID
            	    {
            	    match(input,ID,FOLLOW_ID_in_pattern_type1191); 

            	    }
            	    break;

            	default :
            	    if ( cnt50 >= 1 ) break loop50;
                        EarlyExitException eee =
                            new EarlyExitException(50, input);
                        throw eee;
                }
                cnt50++;
            } while (true);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:208:26: ( dimension_definition )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);

                if ( (LA51_0==LEFT_SQUARE) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:208:26: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_pattern_type1194);
            	    dimension_definition();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop51;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:211:1: data_type : ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) ;
    public final void data_type() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:212:2: ( ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:212:4: ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* )
            {
            match(input,VT_DATA_TYPE,FOLLOW_VT_DATA_TYPE_in_data_type1208); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:212:19: ( ID )+
            int cnt52=0;
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);

                if ( (LA52_0==ID) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:212:19: ID
            	    {
            	    match(input,ID,FOLLOW_ID_in_data_type1210); 

            	    }
            	    break;

            	default :
            	    if ( cnt52 >= 1 ) break loop52;
                        EarlyExitException eee =
                            new EarlyExitException(52, input);
                        throw eee;
                }
                cnt52++;
            } while (true);

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:212:23: ( dimension_definition )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);

                if ( (LA53_0==LEFT_SQUARE) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:212:23: dimension_definition
            	    {
            	    pushFollow(FOLLOW_dimension_definition_in_data_type1213);
            	    dimension_definition();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop53;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:215:1: dimension_definition : LEFT_SQUARE RIGHT_SQUARE ;
    public final void dimension_definition() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:216:2: ( LEFT_SQUARE RIGHT_SQUARE )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:216:4: LEFT_SQUARE RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dimension_definition1226); 
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dimension_definition1228); 

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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:219:1: accessor_element : ^( VT_ACCESSOR_ELEMENT ID ( VT_SQUARE_CHUNK )* ) ;
    public final void accessor_element() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:220:2: ( ^( VT_ACCESSOR_ELEMENT ID ( VT_SQUARE_CHUNK )* ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:220:4: ^( VT_ACCESSOR_ELEMENT ID ( VT_SQUARE_CHUNK )* )
            {
            match(input,VT_ACCESSOR_ELEMENT,FOLLOW_VT_ACCESSOR_ELEMENT_in_accessor_element1240); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_accessor_element1242); 
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:220:29: ( VT_SQUARE_CHUNK )*
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);

                if ( (LA54_0==VT_SQUARE_CHUNK) ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:220:29: VT_SQUARE_CHUNK
            	    {
            	    match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_accessor_element1244); 

            	    }
            	    break;

            	default :
            	    break loop54;
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:223:1: expression_chain : ^( VT_EXPRESSION_CHAIN ID ( VT_SQUARE_CHUNK )? ( VT_PAREN_CHUNK )? ( expression_chain )? ) ;
    public final void expression_chain() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:224:2: ( ^( VT_EXPRESSION_CHAIN ID ( VT_SQUARE_CHUNK )? ( VT_PAREN_CHUNK )? ( expression_chain )? ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:224:4: ^( VT_EXPRESSION_CHAIN ID ( VT_SQUARE_CHUNK )? ( VT_PAREN_CHUNK )? ( expression_chain )? )
            {
            match(input,VT_EXPRESSION_CHAIN,FOLLOW_VT_EXPRESSION_CHAIN_in_expression_chain1258); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_expression_chain1260); 
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:224:29: ( VT_SQUARE_CHUNK )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==VT_SQUARE_CHUNK) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:224:29: VT_SQUARE_CHUNK
                    {
                    match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_expression_chain1262); 

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:224:46: ( VT_PAREN_CHUNK )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==VT_PAREN_CHUNK) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:224:46: VT_PAREN_CHUNK
                    {
                    match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_expression_chain1265); 

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:224:62: ( expression_chain )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==VT_EXPRESSION_CHAIN) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:224:62: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain1268);
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
    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:227:1: curly_chunk : VT_CURLY_CHUNK ;
    public final void curly_chunk() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:228:2: ( VT_CURLY_CHUNK )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/Tree2TestDRL.g:228:4: VT_CURLY_CHUNK
            {
            match(input,VT_CURLY_CHUNK,FOLLOW_VT_CURLY_CHUNK_in_curly_chunk1282); 

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
    public static final BitSet FOLLOW_package_statement_in_compilation_unit45 = new BitSet(new long[]{0xEDFFE00000000028L,0x0000000000000003L});
    public static final BitSet FOLLOW_statement_in_compilation_unit48 = new BitSet(new long[]{0xEDFFE00000000028L,0x0000000000000003L});
    public static final BitSet FOLLOW_VK_PACKAGE_in_package_statement63 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_package_id_in_package_statement65 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PACKAGE_ID_in_package_id78 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_package_id80 = new BitSet(new long[]{0x0000000000000008L,0x0000000000080000L});
    public static final BitSet FOLLOW_rule_attribute_in_statement93 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement98 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement116 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_declaration_in_statement136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VK_IMPORT_in_import_statement148 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_import_name_in_import_statement150 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FUNCTION_IMPORT_in_function_import_statement163 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_FUNCTION_in_function_import_statement165 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement167 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_IMPORT_ID_in_import_name180 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_import_name182 = new BitSet(new long[]{0x0000000000000008L,0x0000000000280000L});
    public static final BitSet FOLLOW_DOT_STAR_in_import_name185 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_GLOBAL_in_global199 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_global201 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_VT_GLOBAL_ID_in_global203 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_FUNCTION_in_function216 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_function218 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_VT_FUNCTION_ID_in_function221 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_parameters_in_function223 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_curly_chunk_in_function225 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_QUERY_in_query238 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_QUERY_ID_in_query240 = new BitSet(new long[]{0x0000100000400000L});
    public static final BitSet FOLLOW_parameters_in_query242 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_lhs_block_in_query245 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_END_in_query247 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PARAM_LIST_in_parameters260 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_param_definition_in_parameters262 = new BitSet(new long[]{0x0000004000000008L,0x0000000000080000L});
    public static final BitSet FOLLOW_data_type_in_param_definition275 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_argument_in_param_definition278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_argument289 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_dimension_definition_in_argument291 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_VK_DECLARE_in_type_declaration304 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_TYPE_DECLARE_ID_in_type_declaration306 = new BitSet(new long[]{0x0000000000000000L,0x0000000008480000L});
    public static final BitSet FOLLOW_decl_metadata_in_type_declaration308 = new BitSet(new long[]{0x0000000000000000L,0x0000000008480000L});
    public static final BitSet FOLLOW_decl_field_in_type_declaration311 = new BitSet(new long[]{0x0000000000000000L,0x0000000000480000L});
    public static final BitSet FOLLOW_END_in_type_declaration314 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AT_in_decl_metadata327 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_decl_metadata329 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_decl_metadata331 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_decl_field344 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_decl_field_initialization_in_decl_field346 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_data_type_in_decl_field349 = new BitSet(new long[]{0x0000000000000008L,0x0000000008000000L});
    public static final BitSet FOLLOW_decl_metadata_in_decl_field351 = new BitSet(new long[]{0x0000000000000008L,0x0000000008000000L});
    public static final BitSet FOLLOW_EQUALS_in_decl_field_initialization365 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_decl_field_initialization367 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_TEMPLATE_in_template380 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_TEMPLATE_ID_in_template382 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_template_slot_in_template384 = new BitSet(new long[]{0x0000000000008000L,0x0000000000400000L});
    public static final BitSet FOLLOW_END_in_template387 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_SLOT_in_template_slot400 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_template_slot402 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_VT_SLOT_ID_in_template_slot404 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_RULE_in_rule417 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_RULE_ID_in_rule419 = new BitSet(new long[]{0x0000000000030000L,0x0000000040000000L});
    public static final BitSet FOLLOW_rule_attributes_in_rule421 = new BitSet(new long[]{0x0000000000020000L,0x0000000040000000L});
    public static final BitSet FOLLOW_when_part_in_rule424 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_VT_RHS_CHUNK_in_rule427 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHEN_in_when_part439 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_lhs_block_in_when_part441 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_RULE_ATTRIBUTES_in_rule_attributes453 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_ATTRIBUTES_in_rule_attributes455 = new BitSet(new long[]{0x01FFE00000000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes458 = new BitSet(new long[]{0x01FFE00000000008L});
    public static final BitSet FOLLOW_VK_SALIENCE_in_rule_attribute472 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_rule_attribute474 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_NO_LOOP_in_rule_attribute486 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute488 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_AGENDA_GROUP_in_rule_attribute498 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute500 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DURATION_in_rule_attribute509 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INT_in_rule_attribute511 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ACTIVATION_GROUP_in_rule_attribute521 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute523 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_AUTO_FOCUS_in_rule_attribute531 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute533 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DATE_EFFECTIVE_in_rule_attribute542 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute544 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DATE_EXPIRES_in_rule_attribute552 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute554 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ENABLED_in_rule_attribute562 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute564 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_RULEFLOW_GROUP_in_rule_attribute572 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute574 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_LOCK_ON_ACTIVE_in_rule_attribute582 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute584 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DIALECT_in_rule_attribute592 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute594 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_AND_IMPLICIT_in_lhs_block608 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs_block610 = new BitSet(new long[]{0x0000000087800008L,0x0000000800006204L});
    public static final BitSet FOLLOW_VT_OR_PREFIX_in_lhs623 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs625 = new BitSet(new long[]{0x0000000087800008L,0x0000000800006204L});
    public static final BitSet FOLLOW_VT_OR_INFIX_in_lhs633 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs635 = new BitSet(new long[]{0x0000000087800000L,0x0000000800006204L});
    public static final BitSet FOLLOW_lhs_in_lhs637 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_AND_PREFIX_in_lhs644 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs646 = new BitSet(new long[]{0x0000000087800008L,0x0000000800006204L});
    public static final BitSet FOLLOW_VT_AND_INFIX_in_lhs654 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs656 = new BitSet(new long[]{0x0000000087800000L,0x0000000800006204L});
    public static final BitSet FOLLOW_lhs_in_lhs658 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EXISTS_in_lhs665 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs667 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_NOT_in_lhs674 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs676 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EVAL_in_lhs683 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_lhs685 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_FORALL_in_lhs692 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs694 = new BitSet(new long[]{0x0000000087800008L,0x0000000800006204L});
    public static final BitSet FOLLOW_FROM_in_lhs702 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs704 = new BitSet(new long[]{0x0000000020000000L,0x000000A000000100L});
    public static final BitSet FOLLOW_from_elements_in_lhs706 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_from_elements724 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_from_elements726 = new BitSet(new long[]{0x0000000018000000L});
    public static final BitSet FOLLOW_accumulate_init_clause_in_from_elements729 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_accumulate_id_clause_in_from_elements731 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_COLLECT_in_from_elements739 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_from_elements741 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ENTRY_POINT_in_from_elements748 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_ENTRYPOINT_ID_in_from_elements750 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FROM_SOURCE_in_from_elements757 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_from_elements759 = new BitSet(new long[]{0x0000000040100008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_from_elements761 = new BitSet(new long[]{0x0000000040000008L});
    public static final BitSet FOLLOW_expression_chain_in_from_elements764 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ACCUMULATE_INIT_CLAUSE_in_accumulate_init_clause778 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INIT_in_accumulate_init_clause785 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause787 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ACTION_in_accumulate_init_clause795 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause797 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_accumulate_init_reverse_clause_in_accumulate_init_clause804 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_VK_RESULT_in_accumulate_init_clause811 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause813 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_REVERSE_in_accumulate_init_reverse_clause827 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_reverse_clause829 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ACCUMULATE_ID_CLAUSE_in_accumulate_id_clause843 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_accumulate_id_clause845 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_id_clause847 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PATTERN_in_lhs_pattern860 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_lhs_pattern862 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_over_clause_in_lhs_pattern865 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OVER_in_over_clause878 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_over_element_in_over_clause880 = new BitSet(new long[]{0x0000000000200008L});
    public static final BitSet FOLLOW_VT_BEHAVIOR_in_over_element894 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_over_element896 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_over_element898 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_over_element900 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_fact_expression913 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression915 = new BitSet(new long[]{0x0000001F00100040L,0x00037E07808804FCL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression917 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_fact_expression924 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression926 = new BitSet(new long[]{0x0000001F00100040L,0x00037E07808804FCL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression928 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FACT_BINDING_in_fact_expression935 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_LABEL_in_fact_expression937 = new BitSet(new long[]{0x0000001F00100040L,0x00037E07808804FCL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression939 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FACT_in_fact_expression946 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_pattern_type_in_fact_expression948 = new BitSet(new long[]{0x0000001F00100048L,0x00037E07808804FCL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression950 = new BitSet(new long[]{0x0000001F00100048L,0x00037E07808804FCL});
    public static final BitSet FOLLOW_VT_FACT_OR_in_fact_expression958 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression960 = new BitSet(new long[]{0x0000001F00100040L,0x00037E07808804FCL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression962 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EVAL_in_fact_expression969 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_fact_expression971 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_IN_in_fact_expression978 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression980 = new BitSet(new long[]{0x0000001F00100040L,0x00037E07808804FCL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression983 = new BitSet(new long[]{0x0000001F00100048L,0x00037E07808804FCL});
    public static final BitSet FOLLOW_EQUAL_in_fact_expression991 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression993 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GREATER_in_fact_expression1000 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1002 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GREATER_EQUAL_in_fact_expression1009 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1011 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LESS_in_fact_expression1018 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1020 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LESS_EQUAL_in_fact_expression1027 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1029 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_EQUAL_in_fact_expression1036 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1038 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_CONTAINS_in_fact_expression1045 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1047 = new BitSet(new long[]{0x0000001F00100040L,0x00037E07808804FCL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1050 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EXCLUDES_in_fact_expression1057 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1059 = new BitSet(new long[]{0x0000001F00100040L,0x00037E07808804FCL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1062 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_MATCHES_in_fact_expression1069 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1071 = new BitSet(new long[]{0x0000001F00100040L,0x00037E07808804FCL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1074 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_SOUNDSLIKE_in_fact_expression1081 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1083 = new BitSet(new long[]{0x0000001F00100040L,0x00037E07808804FCL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1086 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_MEMBEROF_in_fact_expression1093 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1095 = new BitSet(new long[]{0x0000001F00100040L,0x00037E07808804FCL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1098 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_fact_expression1105 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1107 = new BitSet(new long[]{0x0000001F00180040L,0x00037E07808804FCL});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_fact_expression1110 = new BitSet(new long[]{0x0000001F00100040L,0x00037E07808804FCL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1113 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_BIND_FIELD_in_fact_expression1120 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_LABEL_in_fact_expression1122 = new BitSet(new long[]{0x0000001F00100040L,0x00037E07808804FCL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1124 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FIELD_in_fact_expression1131 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1133 = new BitSet(new long[]{0x0000001F00100048L,0x00037E07808804FCL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1135 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ACCESSOR_PATH_in_fact_expression1143 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_accessor_element_in_fact_expression1145 = new BitSet(new long[]{0x0000002000000008L});
    public static final BitSet FOLLOW_STRING_in_fact_expression1152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_fact_expression1157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_fact_expression1162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_fact_expression1167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_fact_expression1172 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_fact_expression1177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_PATTERN_TYPE_in_pattern_type1189 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_pattern_type1191 = new BitSet(new long[]{0x0000000000000008L,0x0004000000080000L});
    public static final BitSet FOLLOW_dimension_definition_in_pattern_type1194 = new BitSet(new long[]{0x0000000000000008L,0x0004000000000000L});
    public static final BitSet FOLLOW_VT_DATA_TYPE_in_data_type1208 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_data_type1210 = new BitSet(new long[]{0x0000000000000008L,0x0004000000080000L});
    public static final BitSet FOLLOW_dimension_definition_in_data_type1213 = new BitSet(new long[]{0x0000000000000008L,0x0004000000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dimension_definition1226 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dimension_definition1228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_ACCESSOR_ELEMENT_in_accessor_element1240 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_accessor_element1242 = new BitSet(new long[]{0x0000000000080008L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_accessor_element1244 = new BitSet(new long[]{0x0000000000080008L});
    public static final BitSet FOLLOW_VT_EXPRESSION_CHAIN_in_expression_chain1258 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_expression_chain1260 = new BitSet(new long[]{0x0000000040180008L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_expression_chain1262 = new BitSet(new long[]{0x0000000040100008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_expression_chain1265 = new BitSet(new long[]{0x0000000040000008L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain1268 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_CURLY_CHUNK_in_curly_chunk1282 = new BitSet(new long[]{0x0000000000000002L});

}