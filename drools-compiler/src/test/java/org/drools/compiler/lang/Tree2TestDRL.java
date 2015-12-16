/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

// $ANTLR 3.3 Nov 30, 2010 12:46:29 src/main/resources/org/drools/lang/Tree2TestDRL.g 2011-01-18 19:45:14

    package org.drools.compiler.lang;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class Tree2TestDRL extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_COMPILATION_UNIT", "VT_FUNCTION_IMPORT", "VT_FACT", "VT_CONSTRAINTS", "VT_LABEL", "VT_QUERY_ID", "VT_TYPE_DECLARE_ID", "VT_TYPE_NAME", "VT_RULE_ID", "VT_ENTRYPOINT_ID", "VT_RULE_ATTRIBUTES", "VT_PKG_ATTRIBUTES", "VT_RHS_CHUNK", "VT_CURLY_CHUNK", "VT_SQUARE_CHUNK", "VT_PAREN_CHUNK", "VT_BEHAVIOR", "VT_AND_IMPLICIT", "VT_AND_PREFIX", "VT_OR_PREFIX", "VT_AND_INFIX", "VT_OR_INFIX", "VT_ACCUMULATE_INIT_CLAUSE", "VT_ACCUMULATE_ID_CLAUSE", "VT_FROM_SOURCE", "VT_EXPRESSION_CHAIN", "VT_FOR_CE", "VT_FOR_FUNCTIONS", "VT_PATTERN", "VT_FACT_BINDING", "VT_FACT_OR", "VT_BIND_FIELD", "VT_FIELD", "VT_ACCESSOR_PATH", "VT_ACCESSOR_ELEMENT", "VT_DATA_TYPE", "VT_PATTERN_TYPE", "VT_PACKAGE_ID", "VT_IMPORT_ID", "VT_GLOBAL_ID", "VT_FUNCTION_ID", "VT_PARAM_LIST", "VT_ARGUMENTS", "VT_EXPRESSION", "VK_DATE_EFFECTIVE", "VK_DATE_EXPIRES", "VK_LOCK_ON_ACTIVE", "VK_NO_LOOP", "VK_AUTO_FOCUS", "VK_ACTIVATION_GROUP", "VK_AGENDA_GROUP", "VK_RULEFLOW_GROUP", "VK_TIMER", "VK_CALENDARS", "VK_DIALECT", "VK_SALIENCE", "VK_ENABLED", "VK_ATTRIBUTES", "VK_RULE", "VK_EXTEND", "VK_IMPLEMENTS", "VK_IMPORT", "VK_PACKAGE", "VK_QUERY", "VK_DECLARE", "VK_FUNCTION", "VK_GLOBAL", "VK_EVAL", "VK_ENTRY_POINT", "VK_NOT", "VK_IN", "VK_OR", "VK_AND", "VK_EXISTS", "VK_FORALL", "VK_FOR", "VK_ACTION", "VK_REVERSE", "VK_RESULT", "VK_OPERATOR", "VK_END", "VK_INIT", "VK_INSTANCEOF", "VK_EXTENDS", "VK_SUPER", "VK_PRIMITIVE_TYPE", "VK_THIS", "VK_VOID", "VK_CLASS", "VK_NEW", "VK_FINAL", "VK_IF", "VK_ELSE", "VK_WHILE", "VK_DO", "VK_CASE", "VK_DEFAULT", "VK_TRY", "VK_CATCH", "VK_FINALLY", "VK_SWITCH", "VK_SYNCHRONIZED", "VK_RETURN", "VK_THROW", "VK_BREAK", "VK_CONTINUE", "VK_ASSERT", "VK_MODIFY", "VK_STATIC", "VK_PUBLIC", "VK_PROTECTED", "VK_PRIVATE", "VK_ABSTRACT", "VK_NATIVE", "VK_TRANSIENT", "VK_VOLATILE", "VK_STRICTFP", "VK_THROWS", "VK_INTERFACE", "VK_ENUM", "SIGNED_DECIMAL", "SIGNED_HEX", "SIGNED_FLOAT", "VT_PROP_KEY", "VT_PROP_VALUE", "EOL", "WS", "Exponent", "FloatTypeSuffix", "FLOAT", "HexDigit", "IntegerTypeSuffix", "HEX", "DECIMAL", "EscapeSequence", "STRING", "TimePeriod", "UnicodeEscape", "OctalEscape", "BOOL", "ACCUMULATE", "COLLECT", "FROM", "NULL", "OVER", "THEN", "WHEN", "AT", "PLUS_ASSIGN", "MINUS_ASSIGN", "MULT_ASSIGN", "DIV_ASSIGN", "AND_ASSIGN", "OR_ASSIGN", "XOR_ASSIGN", "MOD_ASSIGN", "DECR", "INCR", "ARROW", "SEMICOLON", "COLON", "EQUALS", "NOT_EQUALS", "GREATER_EQUALS", "LESS_EQUALS", "GREATER", "LESS", "EQUALS_ASSIGN", "LEFT_PAREN", "RIGHT_PAREN", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "COMMA", "DOT", "DOUBLE_AMPER", "DOUBLE_PIPE", "QUESTION", "NEGATION", "TILDE", "PIPE", "AMPER", "XOR", "MOD", "STAR", "MINUS", "PLUS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "IdentifierStart", "IdentifierPart", "ID", "DIV", "MISC", "DOT_STAR", "VK_TEMPLATE", "VT_TEMPLATE_ID", "VT_SLOT", "VT_SLOT_ID", "INT", "VK_DURATION", "EQUAL", "GREATER_EQUAL", "LESS_EQUAL", "NOT_EQUAL"
    };
    public static final int EOF=-1;
    public static final int VT_COMPILATION_UNIT=4;
    public static final int VT_FUNCTION_IMPORT=5;
    public static final int VT_FACT=6;
    public static final int VT_CONSTRAINTS=7;
    public static final int VT_LABEL=8;
    public static final int VT_QUERY_ID=9;
    public static final int VT_TYPE_DECLARE_ID=10;
    public static final int VT_TYPE_NAME=11;
    public static final int VT_RULE_ID=12;
    public static final int VT_ENTRYPOINT_ID=13;
    public static final int VT_RULE_ATTRIBUTES=14;
    public static final int VT_PKG_ATTRIBUTES=15;
    public static final int VT_RHS_CHUNK=16;
    public static final int VT_CURLY_CHUNK=17;
    public static final int VT_SQUARE_CHUNK=18;
    public static final int VT_PAREN_CHUNK=19;
    public static final int VT_BEHAVIOR=20;
    public static final int VT_AND_IMPLICIT=21;
    public static final int VT_AND_PREFIX=22;
    public static final int VT_OR_PREFIX=23;
    public static final int VT_AND_INFIX=24;
    public static final int VT_OR_INFIX=25;
    public static final int VT_ACCUMULATE_INIT_CLAUSE=26;
    public static final int VT_ACCUMULATE_ID_CLAUSE=27;
    public static final int VT_FROM_SOURCE=28;
    public static final int VT_EXPRESSION_CHAIN=29;
    public static final int VT_FOR_CE=30;
    public static final int VT_FOR_FUNCTIONS=31;
    public static final int VT_PATTERN=32;
    public static final int VT_FACT_BINDING=33;
    public static final int VT_FACT_OR=34;
    public static final int VT_BIND_FIELD=35;
    public static final int VT_FIELD=36;
    public static final int VT_ACCESSOR_PATH=37;
    public static final int VT_ACCESSOR_ELEMENT=38;
    public static final int VT_DATA_TYPE=39;
    public static final int VT_PATTERN_TYPE=40;
    public static final int VT_PACKAGE_ID=41;
    public static final int VT_IMPORT_ID=42;
    public static final int VT_GLOBAL_ID=43;
    public static final int VT_FUNCTION_ID=44;
    public static final int VT_PARAM_LIST=45;
    public static final int VT_ARGUMENTS=46;
    public static final int VT_EXPRESSION=47;
    public static final int VK_DATE_EFFECTIVE=48;
    public static final int VK_DATE_EXPIRES=49;
    public static final int VK_LOCK_ON_ACTIVE=50;
    public static final int VK_NO_LOOP=51;
    public static final int VK_AUTO_FOCUS=52;
    public static final int VK_ACTIVATION_GROUP=53;
    public static final int VK_AGENDA_GROUP=54;
    public static final int VK_RULEFLOW_GROUP=55;
    public static final int VK_TIMER=56;
    public static final int VK_CALENDARS=57;
    public static final int VK_DIALECT=58;
    public static final int VK_SALIENCE=59;
    public static final int VK_ENABLED=60;
    public static final int VK_ATTRIBUTES=61;
    public static final int VK_RULE=62;
    public static final int VK_EXTEND=63;
    public static final int VK_IMPLEMENTS=64;
    public static final int VK_IMPORT=65;
    public static final int VK_PACKAGE=66;
    public static final int VK_QUERY=67;
    public static final int VK_DECLARE=68;
    public static final int VK_FUNCTION=69;
    public static final int VK_GLOBAL=70;
    public static final int VK_EVAL=71;
    public static final int VK_ENTRY_POINT=72;
    public static final int VK_NOT=73;
    public static final int VK_IN=74;
    public static final int VK_OR=75;
    public static final int VK_AND=76;
    public static final int VK_EXISTS=77;
    public static final int VK_FORALL=78;
    public static final int VK_FOR=79;
    public static final int VK_ACTION=80;
    public static final int VK_REVERSE=81;
    public static final int VK_RESULT=82;
    public static final int VK_OPERATOR=83;
    public static final int VK_END=84;
    public static final int VK_INIT=85;
    public static final int VK_INSTANCEOF=86;
    public static final int VK_EXTENDS=87;
    public static final int VK_SUPER=88;
    public static final int VK_PRIMITIVE_TYPE=89;
    public static final int VK_THIS=90;
    public static final int VK_VOID=91;
    public static final int VK_CLASS=92;
    public static final int VK_NEW=93;
    public static final int VK_FINAL=94;
    public static final int VK_IF=95;
    public static final int VK_ELSE=96;
    public static final int VK_WHILE=97;
    public static final int VK_DO=98;
    public static final int VK_CASE=99;
    public static final int VK_DEFAULT=100;
    public static final int VK_TRY=101;
    public static final int VK_CATCH=102;
    public static final int VK_FINALLY=103;
    public static final int VK_SWITCH=104;
    public static final int VK_SYNCHRONIZED=105;
    public static final int VK_RETURN=106;
    public static final int VK_THROW=107;
    public static final int VK_BREAK=108;
    public static final int VK_CONTINUE=109;
    public static final int VK_ASSERT=110;
    public static final int VK_MODIFY=111;
    public static final int VK_STATIC=112;
    public static final int VK_PUBLIC=113;
    public static final int VK_PROTECTED=114;
    public static final int VK_PRIVATE=115;
    public static final int VK_ABSTRACT=116;
    public static final int VK_NATIVE=117;
    public static final int VK_TRANSIENT=118;
    public static final int VK_VOLATILE=119;
    public static final int VK_STRICTFP=120;
    public static final int VK_THROWS=121;
    public static final int VK_INTERFACE=122;
    public static final int VK_ENUM=123;
    public static final int SIGNED_DECIMAL=124;
    public static final int SIGNED_HEX=125;
    public static final int SIGNED_FLOAT=126;
    public static final int VT_PROP_KEY=127;
    public static final int VT_PROP_VALUE=128;
    public static final int EOL=129;
    public static final int WS=130;
    public static final int Exponent=131;
    public static final int FloatTypeSuffix=132;
    public static final int FLOAT=133;
    public static final int HexDigit=134;
    public static final int IntegerTypeSuffix=135;
    public static final int HEX=136;
    public static final int DECIMAL=137;
    public static final int EscapeSequence=138;
    public static final int STRING=139;
    public static final int TimePeriod=140;
    public static final int UnicodeEscape=141;
    public static final int OctalEscape=142;
    public static final int BOOL=143;
    public static final int ACCUMULATE=144;
    public static final int COLLECT=145;
    public static final int FROM=146;
    public static final int NULL=147;
    public static final int OVER=148;
    public static final int THEN=149;
    public static final int WHEN=150;
    public static final int AT=151;
    public static final int PLUS_ASSIGN=152;
    public static final int MINUS_ASSIGN=153;
    public static final int MULT_ASSIGN=154;
    public static final int DIV_ASSIGN=155;
    public static final int AND_ASSIGN=156;
    public static final int OR_ASSIGN=157;
    public static final int XOR_ASSIGN=158;
    public static final int MOD_ASSIGN=159;
    public static final int DECR=160;
    public static final int INCR=161;
    public static final int ARROW=162;
    public static final int SEMICOLON=163;
    public static final int COLON=164;
    public static final int EQUALS=165;
    public static final int NOT_EQUALS=166;
    public static final int GREATER_EQUALS=167;
    public static final int LESS_EQUALS=168;
    public static final int GREATER=169;
    public static final int LESS=170;
    public static final int EQUALS_ASSIGN=171;
    public static final int LEFT_PAREN=172;
    public static final int RIGHT_PAREN=173;
    public static final int LEFT_SQUARE=174;
    public static final int RIGHT_SQUARE=175;
    public static final int LEFT_CURLY=176;
    public static final int RIGHT_CURLY=177;
    public static final int COMMA=178;
    public static final int DOT=179;
    public static final int DOUBLE_AMPER=180;
    public static final int DOUBLE_PIPE=181;
    public static final int QUESTION=182;
    public static final int NEGATION=183;
    public static final int TILDE=184;
    public static final int PIPE=185;
    public static final int AMPER=186;
    public static final int XOR=187;
    public static final int MOD=188;
    public static final int STAR=189;
    public static final int MINUS=190;
    public static final int PLUS=191;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=192;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=193;
    public static final int MULTI_LINE_COMMENT=194;
    public static final int IdentifierStart=195;
    public static final int IdentifierPart=196;
    public static final int ID=197;
    public static final int DIV=198;
    public static final int MISC=199;
    public static final int DOT_STAR=200;
    public static final int VK_TEMPLATE=201;
    public static final int VT_TEMPLATE_ID=202;
    public static final int VT_SLOT=203;
    public static final int VT_SLOT_ID=204;
    public static final int INT=205;
    public static final int VK_DURATION=206;
    public static final int EQUAL=207;
    public static final int GREATER_EQUAL=208;
    public static final int LESS_EQUAL=209;
    public static final int NOT_EQUAL=210;

    // delegates
    // delegators


        public Tree2TestDRL(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public Tree2TestDRL(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return Tree2TestDRL.tokenNames; }
    public String getGrammarFileName() { return "src/main/resources/org/drools/lang/Tree2TestDRL.g"; }



    // $ANTLR start "compilation_unit"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:13:1: compilation_unit : ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) ;
    public final void compilation_unit() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:14:2: ( ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:14:4: ^( VT_COMPILATION_UNIT ( package_statement )? ( statement )* )
            {
            match(input,VT_COMPILATION_UNIT,FOLLOW_VT_COMPILATION_UNIT_in_compilation_unit43);

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null);
                // src/main/resources/org/drools/lang/Tree2TestDRL.g:14:26: ( package_statement )?
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==VK_PACKAGE) ) {
                    alt1=1;
                }
                switch (alt1) {
                    case 1 :
                        // src/main/resources/org/drools/lang/Tree2TestDRL.g:14:26: package_statement
                        {
                        pushFollow(FOLLOW_package_statement_in_compilation_unit45);
                        package_statement();

                        state._fsp--;


                        }
                        break;

                }

                // src/main/resources/org/drools/lang/Tree2TestDRL.g:14:45: ( statement )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==VT_FUNCTION_IMPORT||(LA2_0>=VK_DATE_EFFECTIVE && LA2_0<=VK_RULEFLOW_GROUP)||(LA2_0>=VK_DIALECT && LA2_0<=VK_ENABLED)||LA2_0==VK_RULE||LA2_0==VK_IMPORT||(LA2_0>=VK_QUERY && LA2_0<=VK_GLOBAL)||LA2_0==VK_TEMPLATE||LA2_0==VK_DURATION) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                    case 1 :
                        // src/main/resources/org/drools/lang/Tree2TestDRL.g:14:45: statement
                        {
                        pushFollow(FOLLOW_statement_in_compilation_unit48);
                        statement();

                        state._fsp--;


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
    // $ANTLR end "compilation_unit"


    // $ANTLR start "package_statement"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:17:1: package_statement : ^( VK_PACKAGE package_id ) ;
    public final void package_statement() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:18:2: ( ^( VK_PACKAGE package_id ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:18:4: ^( VK_PACKAGE package_id )
            {
            match(input,VK_PACKAGE,FOLLOW_VK_PACKAGE_in_package_statement63);

            match(input, Token.DOWN, null);
            pushFollow(FOLLOW_package_id_in_package_statement65);
            package_id();

            state._fsp--;


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
    // $ANTLR end "package_statement"


    // $ANTLR start "package_id"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:21:1: package_id : ^( VT_PACKAGE_ID ( ID )+ ) ;
    public final void package_id() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:22:2: ( ^( VT_PACKAGE_ID ( ID )+ ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:22:4: ^( VT_PACKAGE_ID ( ID )+ )
            {
            match(input,VT_PACKAGE_ID,FOLLOW_VT_PACKAGE_ID_in_package_id78);

            match(input, Token.DOWN, null);
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:22:20: ( ID )+
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
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:22:20: ID
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
    // $ANTLR end "package_id"


    // $ANTLR start "statement"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:25:1: statement : ( rule_attribute | function_import_statement | import_statement | global | function | template | rule | query | type_declaration );
    public final void statement() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:26:2: ( rule_attribute | function_import_statement | import_statement | global | function | template | rule | query | type_declaration )
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
            case VK_DIALECT:
            case VK_SALIENCE:
            case VK_ENABLED:
            case VK_DURATION:
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
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:26:4: rule_attribute
                    {
                    pushFollow(FOLLOW_rule_attribute_in_statement93);
                    rule_attribute();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:27:4: function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement98);
                    function_import_statement();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:28:4: import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement104);
                    import_statement();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:29:4: global
                    {
                    pushFollow(FOLLOW_global_in_statement110);
                    global();

                    state._fsp--;


                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:30:4: function
                    {
                    pushFollow(FOLLOW_function_in_statement116);
                    function();

                    state._fsp--;


                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:31:4: template
                    {
                    pushFollow(FOLLOW_template_in_statement121);
                    template();

                    state._fsp--;


                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:32:4: rule
                    {
                    pushFollow(FOLLOW_rule_in_statement126);
                    rule();

                    state._fsp--;


                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:33:4: query
                    {
                    pushFollow(FOLLOW_query_in_statement131);
                    query();

                    state._fsp--;


                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:34:4: type_declaration
                    {
                    pushFollow(FOLLOW_type_declaration_in_statement136);
                    type_declaration();

                    state._fsp--;


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
    // $ANTLR end "statement"


    // $ANTLR start "import_statement"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:37:1: import_statement : ^( VK_IMPORT import_name ) ;
    public final void import_statement() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:38:2: ( ^( VK_IMPORT import_name ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:38:4: ^( VK_IMPORT import_name )
            {
            match(input,VK_IMPORT,FOLLOW_VK_IMPORT_in_import_statement148);

            match(input, Token.DOWN, null);
            pushFollow(FOLLOW_import_name_in_import_statement150);
            import_name();

            state._fsp--;


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
    // $ANTLR end "import_statement"


    // $ANTLR start "function_import_statement"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:41:1: function_import_statement : ^( VT_FUNCTION_IMPORT VK_FUNCTION import_name ) ;
    public final void function_import_statement() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:42:2: ( ^( VT_FUNCTION_IMPORT VK_FUNCTION import_name ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:42:4: ^( VT_FUNCTION_IMPORT VK_FUNCTION import_name )
            {
            match(input,VT_FUNCTION_IMPORT,FOLLOW_VT_FUNCTION_IMPORT_in_function_import_statement163);

            match(input, Token.DOWN, null);
            match(input,VK_FUNCTION,FOLLOW_VK_FUNCTION_in_function_import_statement165);
            pushFollow(FOLLOW_import_name_in_function_import_statement167);
            import_name();

            state._fsp--;


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
    // $ANTLR end "function_import_statement"


    // $ANTLR start "import_name"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:45:1: import_name : ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) ;
    public final void import_name() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:46:2: ( ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:46:4: ^( VT_IMPORT_ID ( ID )+ ( DOT_STAR )? )
            {
            match(input,VT_IMPORT_ID,FOLLOW_VT_IMPORT_ID_in_import_name180);

            match(input, Token.DOWN, null);
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:46:19: ( ID )+
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
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:46:19: ID
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

            // src/main/resources/org/drools/lang/Tree2TestDRL.g:46:23: ( DOT_STAR )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==DOT_STAR) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:46:23: DOT_STAR
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
    // $ANTLR end "import_name"


    // $ANTLR start "global"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:49:1: global : ^( VK_GLOBAL data_type VT_GLOBAL_ID ) ;
    public final void global() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:50:2: ( ^( VK_GLOBAL data_type VT_GLOBAL_ID ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:50:4: ^( VK_GLOBAL data_type VT_GLOBAL_ID )
            {
            match(input,VK_GLOBAL,FOLLOW_VK_GLOBAL_in_global199);

            match(input, Token.DOWN, null);
            pushFollow(FOLLOW_data_type_in_global201);
            data_type();

            state._fsp--;

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
    // $ANTLR end "global"


    // $ANTLR start "function"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:53:1: function : ^( VK_FUNCTION ( data_type )? VT_FUNCTION_ID parameters curly_chunk ) ;
    public final void function() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:54:2: ( ^( VK_FUNCTION ( data_type )? VT_FUNCTION_ID parameters curly_chunk ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:54:4: ^( VK_FUNCTION ( data_type )? VT_FUNCTION_ID parameters curly_chunk )
            {
            match(input,VK_FUNCTION,FOLLOW_VK_FUNCTION_in_function216);

            match(input, Token.DOWN, null);
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:54:18: ( data_type )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==VT_DATA_TYPE) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:54:18: data_type
                    {
                    pushFollow(FOLLOW_data_type_in_function218);
                    data_type();

                    state._fsp--;


                    }
                    break;

            }

            match(input,VT_FUNCTION_ID,FOLLOW_VT_FUNCTION_ID_in_function221);
            pushFollow(FOLLOW_parameters_in_function223);
            parameters();

            state._fsp--;

            pushFollow(FOLLOW_curly_chunk_in_function225);
            curly_chunk();

            state._fsp--;


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
    // $ANTLR end "function"


    // $ANTLR start "query"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:57:1: query : ^( VK_QUERY VT_QUERY_ID ( parameters )? lhs_block VK_END ) ;
    public final void query() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:58:2: ( ^( VK_QUERY VT_QUERY_ID ( parameters )? lhs_block VK_END ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:58:4: ^( VK_QUERY VT_QUERY_ID ( parameters )? lhs_block VK_END )
            {
            match(input,VK_QUERY,FOLLOW_VK_QUERY_in_query238);

            match(input, Token.DOWN, null);
            match(input,VT_QUERY_ID,FOLLOW_VT_QUERY_ID_in_query240);
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:58:27: ( parameters )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==VT_PARAM_LIST) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:58:27: parameters
                    {
                    pushFollow(FOLLOW_parameters_in_query242);
                    parameters();

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_lhs_block_in_query245);
            lhs_block();

            state._fsp--;

            match(input,VK_END,FOLLOW_VK_END_in_query247);

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
    // $ANTLR end "query"


    // $ANTLR start "parameters"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:61:1: parameters : ^( VT_PARAM_LIST ( param_definition )* ) ;
    public final void parameters() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:62:2: ( ^( VT_PARAM_LIST ( param_definition )* ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:62:4: ^( VT_PARAM_LIST ( param_definition )* )
            {
            match(input,VT_PARAM_LIST,FOLLOW_VT_PARAM_LIST_in_parameters260);

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null);
                // src/main/resources/org/drools/lang/Tree2TestDRL.g:62:20: ( param_definition )*
                loop9:
                do {
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==VT_DATA_TYPE||LA9_0==ID) ) {
                        alt9=1;
                    }


                    switch (alt9) {
                    case 1 :
                        // src/main/resources/org/drools/lang/Tree2TestDRL.g:62:20: param_definition
                        {
                        pushFollow(FOLLOW_param_definition_in_parameters262);
                        param_definition();

                        state._fsp--;


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
    // $ANTLR end "parameters"


    // $ANTLR start "param_definition"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:65:1: param_definition : ( data_type )? argument ;
    public final void param_definition() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:66:2: ( ( data_type )? argument )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:66:4: ( data_type )? argument
            {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:66:4: ( data_type )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==VT_DATA_TYPE) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:66:4: data_type
                    {
                    pushFollow(FOLLOW_data_type_in_param_definition275);
                    data_type();

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_argument_in_param_definition278);
            argument();

            state._fsp--;


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
    // $ANTLR end "param_definition"


    // $ANTLR start "argument"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:69:1: argument : ID ( dimension_definition )* ;
    public final void argument() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:70:2: ( ID ( dimension_definition )* )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:70:4: ID ( dimension_definition )*
            {
            match(input,ID,FOLLOW_ID_in_argument289);
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:70:7: ( dimension_definition )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==LEFT_SQUARE) ) {
                    alt11=1;
                }


                switch (alt11) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:70:7: dimension_definition
                    {
                    pushFollow(FOLLOW_dimension_definition_in_argument291);
                    dimension_definition();

                    state._fsp--;


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
    // $ANTLR end "argument"


    // $ANTLR start "type_declaration"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:73:1: type_declaration : ^( VK_DECLARE VT_TYPE_DECLARE_ID ( decl_metadata )* ( decl_field )* VK_END ) ;
    public final void type_declaration() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:74:2: ( ^( VK_DECLARE VT_TYPE_DECLARE_ID ( decl_metadata )* ( decl_field )* VK_END ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:74:4: ^( VK_DECLARE VT_TYPE_DECLARE_ID ( decl_metadata )* ( decl_field )* VK_END )
            {
            match(input,VK_DECLARE,FOLLOW_VK_DECLARE_in_type_declaration304);

            match(input, Token.DOWN, null);
            match(input,VT_TYPE_DECLARE_ID,FOLLOW_VT_TYPE_DECLARE_ID_in_type_declaration306);
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:74:36: ( decl_metadata )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==AT) ) {
                    alt12=1;
                }


                switch (alt12) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:74:36: decl_metadata
                    {
                    pushFollow(FOLLOW_decl_metadata_in_type_declaration308);
                    decl_metadata();

                    state._fsp--;


                    }
                    break;

                default :
                    break loop12;
                }
            } while (true);

            // src/main/resources/org/drools/lang/Tree2TestDRL.g:74:51: ( decl_field )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==ID) ) {
                    alt13=1;
                }


                switch (alt13) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:74:51: decl_field
                    {
                    pushFollow(FOLLOW_decl_field_in_type_declaration311);
                    decl_field();

                    state._fsp--;


                    }
                    break;

                default :
                    break loop13;
                }
            } while (true);

            match(input,VK_END,FOLLOW_VK_END_in_type_declaration314);

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
    // $ANTLR end "type_declaration"


    // $ANTLR start "decl_metadata"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:77:1: decl_metadata : ^( AT ID ( VT_PAREN_CHUNK )? ) ;
    public final void decl_metadata() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:78:2: ( ^( AT ID ( VT_PAREN_CHUNK )? ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:78:4: ^( AT ID ( VT_PAREN_CHUNK )? )
            {
            match(input,AT,FOLLOW_AT_in_decl_metadata327);

            match(input, Token.DOWN, null);
            match(input,ID,FOLLOW_ID_in_decl_metadata329);
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:78:12: ( VT_PAREN_CHUNK )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==VT_PAREN_CHUNK) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:78:12: VT_PAREN_CHUNK
                    {
                    match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_decl_metadata331);

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
    // $ANTLR end "decl_metadata"


    // $ANTLR start "decl_field"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:81:1: decl_field : ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* ) ;
    public final void decl_field() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:82:2: ( ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:82:4: ^( ID ( decl_field_initialization )? data_type ( decl_metadata )* )
            {
            match(input,ID,FOLLOW_ID_in_decl_field345);

            match(input, Token.DOWN, null);
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:82:9: ( decl_field_initialization )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==EQUALS) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:82:9: decl_field_initialization
                    {
                    pushFollow(FOLLOW_decl_field_initialization_in_decl_field347);
                    decl_field_initialization();

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_data_type_in_decl_field350);
            data_type();

            state._fsp--;

            // src/main/resources/org/drools/lang/Tree2TestDRL.g:82:46: ( decl_metadata )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==AT) ) {
                    alt16=1;
                }


                switch (alt16) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:82:46: decl_metadata
                    {
                    pushFollow(FOLLOW_decl_metadata_in_decl_field352);
                    decl_metadata();

                    state._fsp--;


                    }
                    break;

                default :
                    break loop16;
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
    // $ANTLR end "decl_field"


    // $ANTLR start "decl_field_initialization"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:85:1: decl_field_initialization : ^( EQUALS VT_PAREN_CHUNK ) ;
    public final void decl_field_initialization() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:86:2: ( ^( EQUALS VT_PAREN_CHUNK ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:86:4: ^( EQUALS VT_PAREN_CHUNK )
            {
            match(input,EQUALS,FOLLOW_EQUALS_in_decl_field_initialization366);

            match(input, Token.DOWN, null);
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_decl_field_initialization368);

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
    // $ANTLR end "decl_field_initialization"


    // $ANTLR start "template"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:89:1: template : ^( VK_TEMPLATE VT_TEMPLATE_ID ( template_slot )+ VK_END ) ;
    public final void template() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:90:2: ( ^( VK_TEMPLATE VT_TEMPLATE_ID ( template_slot )+ VK_END ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:90:4: ^( VK_TEMPLATE VT_TEMPLATE_ID ( template_slot )+ VK_END )
            {
            match(input,VK_TEMPLATE,FOLLOW_VK_TEMPLATE_in_template381);

            match(input, Token.DOWN, null);
            match(input,VT_TEMPLATE_ID,FOLLOW_VT_TEMPLATE_ID_in_template383);
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:90:33: ( template_slot )+
            int cnt17=0;
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==VT_SLOT) ) {
                    alt17=1;
                }


                switch (alt17) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:90:33: template_slot
                    {
                    pushFollow(FOLLOW_template_slot_in_template385);
                    template_slot();

                    state._fsp--;


                    }
                    break;

                default :
                    if ( cnt17 >= 1 ) break loop17;
                        EarlyExitException eee =
                            new EarlyExitException(17, input);
                        throw eee;
                }
                cnt17++;
            } while (true);

            match(input,VK_END,FOLLOW_VK_END_in_template388);

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
    // $ANTLR end "template"


    // $ANTLR start "template_slot"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:93:1: template_slot : ^( VT_SLOT data_type VT_SLOT_ID ) ;
    public final void template_slot() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:94:2: ( ^( VT_SLOT data_type VT_SLOT_ID ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:94:4: ^( VT_SLOT data_type VT_SLOT_ID )
            {
            match(input,VT_SLOT,FOLLOW_VT_SLOT_in_template_slot401);

            match(input, Token.DOWN, null);
            pushFollow(FOLLOW_data_type_in_template_slot403);
            data_type();

            state._fsp--;

            match(input,VT_SLOT_ID,FOLLOW_VT_SLOT_ID_in_template_slot405);

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
    // $ANTLR end "template_slot"


    // $ANTLR start "rule"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:97:1: rule : ^( VK_RULE VT_RULE_ID ( rule_attributes )? ( when_part )? VT_RHS_CHUNK ) ;
    public final void rule() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:98:2: ( ^( VK_RULE VT_RULE_ID ( rule_attributes )? ( when_part )? VT_RHS_CHUNK ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:98:4: ^( VK_RULE VT_RULE_ID ( rule_attributes )? ( when_part )? VT_RHS_CHUNK )
            {
            match(input,VK_RULE,FOLLOW_VK_RULE_in_rule418);

            match(input, Token.DOWN, null);
            match(input,VT_RULE_ID,FOLLOW_VT_RULE_ID_in_rule420);
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:98:25: ( rule_attributes )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==VT_RULE_ATTRIBUTES) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:98:25: rule_attributes
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule422);
                    rule_attributes();

                    state._fsp--;


                    }
                    break;

            }

            // src/main/resources/org/drools/lang/Tree2TestDRL.g:98:42: ( when_part )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==WHEN) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:98:42: when_part
                    {
                    pushFollow(FOLLOW_when_part_in_rule425);
                    when_part();

                    state._fsp--;


                    }
                    break;

            }

            match(input,VT_RHS_CHUNK,FOLLOW_VT_RHS_CHUNK_in_rule428);

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
    // $ANTLR end "rule"


    // $ANTLR start "when_part"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:101:1: when_part : WHEN lhs_block ;
    public final void when_part() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:102:2: ( WHEN lhs_block )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:102:4: WHEN lhs_block
            {
            match(input,WHEN,FOLLOW_WHEN_in_when_part440);
            pushFollow(FOLLOW_lhs_block_in_when_part442);
            lhs_block();

            state._fsp--;


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
    // $ANTLR end "when_part"


    // $ANTLR start "rule_attributes"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:105:1: rule_attributes : ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? ( rule_attribute )+ ) ;
    public final void rule_attributes() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:106:2: ( ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? ( rule_attribute )+ ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:106:4: ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? ( rule_attribute )+ )
            {
            match(input,VT_RULE_ATTRIBUTES,FOLLOW_VT_RULE_ATTRIBUTES_in_rule_attributes454);

            match(input, Token.DOWN, null);
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:106:25: ( VK_ATTRIBUTES )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==VK_ATTRIBUTES) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:106:25: VK_ATTRIBUTES
                    {
                    match(input,VK_ATTRIBUTES,FOLLOW_VK_ATTRIBUTES_in_rule_attributes456);

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/Tree2TestDRL.g:106:40: ( rule_attribute )+
            int cnt21=0;
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( ((LA21_0>=VK_DATE_EFFECTIVE && LA21_0<=VK_RULEFLOW_GROUP)||(LA21_0>=VK_DIALECT && LA21_0<=VK_ENABLED)||LA21_0==VK_DURATION) ) {
                    alt21=1;
                }


                switch (alt21) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:106:40: rule_attribute
                    {
                    pushFollow(FOLLOW_rule_attribute_in_rule_attributes459);
                    rule_attribute();

                    state._fsp--;


                    }
                    break;

                default :
                    if ( cnt21 >= 1 ) break loop21;
                        EarlyExitException eee =
                            new EarlyExitException(21, input);
                        throw eee;
                }
                cnt21++;
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
    // $ANTLR end "rule_attributes"


    // $ANTLR start "rule_attribute"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:109:1: rule_attribute : ( ^( VK_SALIENCE ( INT | VT_PAREN_CHUNK ) ) | ^( VK_NO_LOOP ( BOOL )? ) | ^( VK_AGENDA_GROUP STRING ) | ^( VK_DURATION INT ) | ^( VK_ACTIVATION_GROUP STRING ) | ^( VK_AUTO_FOCUS ( BOOL )? ) | ^( VK_DATE_EFFECTIVE STRING ) | ^( VK_DATE_EXPIRES STRING ) | ^( VK_ENABLED BOOL ) | ^( VK_RULEFLOW_GROUP STRING ) | ^( VK_LOCK_ON_ACTIVE ( BOOL )? ) | ^( VK_DIALECT STRING ) );
    public final void rule_attribute() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:110:2: ( ^( VK_SALIENCE ( INT | VT_PAREN_CHUNK ) ) | ^( VK_NO_LOOP ( BOOL )? ) | ^( VK_AGENDA_GROUP STRING ) | ^( VK_DURATION INT ) | ^( VK_ACTIVATION_GROUP STRING ) | ^( VK_AUTO_FOCUS ( BOOL )? ) | ^( VK_DATE_EFFECTIVE STRING ) | ^( VK_DATE_EXPIRES STRING ) | ^( VK_ENABLED BOOL ) | ^( VK_RULEFLOW_GROUP STRING ) | ^( VK_LOCK_ON_ACTIVE ( BOOL )? ) | ^( VK_DIALECT STRING ) )
            int alt25=12;
            switch ( input.LA(1) ) {
            case VK_SALIENCE:
                {
                alt25=1;
                }
                break;
            case VK_NO_LOOP:
                {
                alt25=2;
                }
                break;
            case VK_AGENDA_GROUP:
                {
                alt25=3;
                }
                break;
            case VK_DURATION:
                {
                alt25=4;
                }
                break;
            case VK_ACTIVATION_GROUP:
                {
                alt25=5;
                }
                break;
            case VK_AUTO_FOCUS:
                {
                alt25=6;
                }
                break;
            case VK_DATE_EFFECTIVE:
                {
                alt25=7;
                }
                break;
            case VK_DATE_EXPIRES:
                {
                alt25=8;
                }
                break;
            case VK_ENABLED:
                {
                alt25=9;
                }
                break;
            case VK_RULEFLOW_GROUP:
                {
                alt25=10;
                }
                break;
            case VK_LOCK_ON_ACTIVE:
                {
                alt25=11;
                }
                break;
            case VK_DIALECT:
                {
                alt25=12;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }

            switch (alt25) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:110:4: ^( VK_SALIENCE ( INT | VT_PAREN_CHUNK ) )
                    {
                    match(input,VK_SALIENCE,FOLLOW_VK_SALIENCE_in_rule_attribute473);

                    match(input, Token.DOWN, null);
                    if ( input.LA(1)==VT_PAREN_CHUNK||input.LA(1)==INT ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    match(input, Token.UP, null);

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:111:4: ^( VK_NO_LOOP ( BOOL )? )
                    {
                    match(input,VK_NO_LOOP,FOLLOW_VK_NO_LOOP_in_rule_attribute487);

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null);
                        // src/main/resources/org/drools/lang/Tree2TestDRL.g:111:17: ( BOOL )?
                        int alt22=2;
                        int LA22_0 = input.LA(1);

                        if ( (LA22_0==BOOL) ) {
                            alt22=1;
                        }
                        switch (alt22) {
                            case 1 :
                                // src/main/resources/org/drools/lang/Tree2TestDRL.g:111:17: BOOL
                                {
                                match(input,BOOL,FOLLOW_BOOL_in_rule_attribute489);

                                }
                                break;

                        }


                        match(input, Token.UP, null);
                    }

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:112:4: ^( VK_AGENDA_GROUP STRING )
                    {
                    match(input,VK_AGENDA_GROUP,FOLLOW_VK_AGENDA_GROUP_in_rule_attribute499);

                    match(input, Token.DOWN, null);
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute501);

                    match(input, Token.UP, null);

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:113:4: ^( VK_DURATION INT )
                    {
                    match(input,VK_DURATION,FOLLOW_VK_DURATION_in_rule_attribute510);

                    match(input, Token.DOWN, null);
                    match(input,INT,FOLLOW_INT_in_rule_attribute512);

                    match(input, Token.UP, null);

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:114:4: ^( VK_ACTIVATION_GROUP STRING )
                    {
                    match(input,VK_ACTIVATION_GROUP,FOLLOW_VK_ACTIVATION_GROUP_in_rule_attribute522);

                    match(input, Token.DOWN, null);
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute524);

                    match(input, Token.UP, null);

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:115:4: ^( VK_AUTO_FOCUS ( BOOL )? )
                    {
                    match(input,VK_AUTO_FOCUS,FOLLOW_VK_AUTO_FOCUS_in_rule_attribute532);

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null);
                        // src/main/resources/org/drools/lang/Tree2TestDRL.g:115:20: ( BOOL )?
                        int alt23=2;
                        int LA23_0 = input.LA(1);

                        if ( (LA23_0==BOOL) ) {
                            alt23=1;
                        }
                        switch (alt23) {
                            case 1 :
                                // src/main/resources/org/drools/lang/Tree2TestDRL.g:115:20: BOOL
                                {
                                match(input,BOOL,FOLLOW_BOOL_in_rule_attribute534);

                                }
                                break;

                        }


                        match(input, Token.UP, null);
                    }

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:116:4: ^( VK_DATE_EFFECTIVE STRING )
                    {
                    match(input,VK_DATE_EFFECTIVE,FOLLOW_VK_DATE_EFFECTIVE_in_rule_attribute543);

                    match(input, Token.DOWN, null);
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute545);

                    match(input, Token.UP, null);

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:117:4: ^( VK_DATE_EXPIRES STRING )
                    {
                    match(input,VK_DATE_EXPIRES,FOLLOW_VK_DATE_EXPIRES_in_rule_attribute553);

                    match(input, Token.DOWN, null);
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute555);

                    match(input, Token.UP, null);

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:118:4: ^( VK_ENABLED BOOL )
                    {
                    match(input,VK_ENABLED,FOLLOW_VK_ENABLED_in_rule_attribute563);

                    match(input, Token.DOWN, null);
                    match(input,BOOL,FOLLOW_BOOL_in_rule_attribute565);

                    match(input, Token.UP, null);

                    }
                    break;
                case 10 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:119:4: ^( VK_RULEFLOW_GROUP STRING )
                    {
                    match(input,VK_RULEFLOW_GROUP,FOLLOW_VK_RULEFLOW_GROUP_in_rule_attribute573);

                    match(input, Token.DOWN, null);
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute575);

                    match(input, Token.UP, null);

                    }
                    break;
                case 11 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:120:4: ^( VK_LOCK_ON_ACTIVE ( BOOL )? )
                    {
                    match(input,VK_LOCK_ON_ACTIVE,FOLLOW_VK_LOCK_ON_ACTIVE_in_rule_attribute583);

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null);
                        // src/main/resources/org/drools/lang/Tree2TestDRL.g:120:24: ( BOOL )?
                        int alt24=2;
                        int LA24_0 = input.LA(1);

                        if ( (LA24_0==BOOL) ) {
                            alt24=1;
                        }
                        switch (alt24) {
                            case 1 :
                                // src/main/resources/org/drools/lang/Tree2TestDRL.g:120:24: BOOL
                                {
                                match(input,BOOL,FOLLOW_BOOL_in_rule_attribute585);

                                }
                                break;

                        }


                        match(input, Token.UP, null);
                    }

                    }
                    break;
                case 12 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:121:4: ^( VK_DIALECT STRING )
                    {
                    match(input,VK_DIALECT,FOLLOW_VK_DIALECT_in_rule_attribute593);

                    match(input, Token.DOWN, null);
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute595);

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
    // $ANTLR end "rule_attribute"


    // $ANTLR start "lhs_block"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:124:1: lhs_block : ^( VT_AND_IMPLICIT ( lhs )* ) ;
    public final void lhs_block() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:125:2: ( ^( VT_AND_IMPLICIT ( lhs )* ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:125:4: ^( VT_AND_IMPLICIT ( lhs )* )
            {
            match(input,VT_AND_IMPLICIT,FOLLOW_VT_AND_IMPLICIT_in_lhs_block609);

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null);
                // src/main/resources/org/drools/lang/Tree2TestDRL.g:125:22: ( lhs )*
                loop26:
                do {
                    int alt26=2;
                    int LA26_0 = input.LA(1);

                    if ( ((LA26_0>=VT_AND_PREFIX && LA26_0<=VT_OR_INFIX)||LA26_0==VT_PATTERN||LA26_0==VK_EVAL||LA26_0==VK_NOT||(LA26_0>=VK_EXISTS && LA26_0<=VK_FORALL)||LA26_0==FROM) ) {
                        alt26=1;
                    }


                    switch (alt26) {
                    case 1 :
                        // src/main/resources/org/drools/lang/Tree2TestDRL.g:125:22: lhs
                        {
                        pushFollow(FOLLOW_lhs_in_lhs_block611);
                        lhs();

                        state._fsp--;


                        }
                        break;

                    default :
                        break loop26;
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
    // $ANTLR end "lhs_block"


    // $ANTLR start "lhs"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:128:1: lhs : ( ^( VT_OR_PREFIX ( lhs )+ ) | ^( VT_OR_INFIX lhs lhs ) | ^( VT_AND_PREFIX ( lhs )+ ) | ^( VT_AND_INFIX lhs lhs ) | ^( VK_EXISTS lhs ) | ^( VK_NOT lhs ) | ^( VK_EVAL VT_PAREN_CHUNK ) | ^( VK_FORALL ( lhs )+ ) | ^( FROM lhs_pattern from_elements ) | lhs_pattern );
    public final void lhs() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:128:5: ( ^( VT_OR_PREFIX ( lhs )+ ) | ^( VT_OR_INFIX lhs lhs ) | ^( VT_AND_PREFIX ( lhs )+ ) | ^( VT_AND_INFIX lhs lhs ) | ^( VK_EXISTS lhs ) | ^( VK_NOT lhs ) | ^( VK_EVAL VT_PAREN_CHUNK ) | ^( VK_FORALL ( lhs )+ ) | ^( FROM lhs_pattern from_elements ) | lhs_pattern )
            int alt30=10;
            switch ( input.LA(1) ) {
            case VT_OR_PREFIX:
                {
                alt30=1;
                }
                break;
            case VT_OR_INFIX:
                {
                alt30=2;
                }
                break;
            case VT_AND_PREFIX:
                {
                alt30=3;
                }
                break;
            case VT_AND_INFIX:
                {
                alt30=4;
                }
                break;
            case VK_EXISTS:
                {
                alt30=5;
                }
                break;
            case VK_NOT:
                {
                alt30=6;
                }
                break;
            case VK_EVAL:
                {
                alt30=7;
                }
                break;
            case VK_FORALL:
                {
                alt30=8;
                }
                break;
            case FROM:
                {
                alt30=9;
                }
                break;
            case VT_PATTERN:
                {
                alt30=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;
            }

            switch (alt30) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:128:7: ^( VT_OR_PREFIX ( lhs )+ )
                    {
                    match(input,VT_OR_PREFIX,FOLLOW_VT_OR_PREFIX_in_lhs624);

                    match(input, Token.DOWN, null);
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:128:22: ( lhs )+
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
                            // src/main/resources/org/drools/lang/Tree2TestDRL.g:128:22: lhs
                            {
                            pushFollow(FOLLOW_lhs_in_lhs626);
                            lhs();

                            state._fsp--;


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
                case 2 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:129:4: ^( VT_OR_INFIX lhs lhs )
                    {
                    match(input,VT_OR_INFIX,FOLLOW_VT_OR_INFIX_in_lhs634);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_lhs_in_lhs636);
                    lhs();

                    state._fsp--;

                    pushFollow(FOLLOW_lhs_in_lhs638);
                    lhs();

                    state._fsp--;


                    match(input, Token.UP, null);

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:130:4: ^( VT_AND_PREFIX ( lhs )+ )
                    {
                    match(input,VT_AND_PREFIX,FOLLOW_VT_AND_PREFIX_in_lhs645);

                    match(input, Token.DOWN, null);
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:130:20: ( lhs )+
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
                            // src/main/resources/org/drools/lang/Tree2TestDRL.g:130:20: lhs
                            {
                            pushFollow(FOLLOW_lhs_in_lhs647);
                            lhs();

                            state._fsp--;


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
                case 4 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:131:4: ^( VT_AND_INFIX lhs lhs )
                    {
                    match(input,VT_AND_INFIX,FOLLOW_VT_AND_INFIX_in_lhs655);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_lhs_in_lhs657);
                    lhs();

                    state._fsp--;

                    pushFollow(FOLLOW_lhs_in_lhs659);
                    lhs();

                    state._fsp--;


                    match(input, Token.UP, null);

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:132:4: ^( VK_EXISTS lhs )
                    {
                    match(input,VK_EXISTS,FOLLOW_VK_EXISTS_in_lhs666);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_lhs_in_lhs668);
                    lhs();

                    state._fsp--;


                    match(input, Token.UP, null);

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:133:4: ^( VK_NOT lhs )
                    {
                    match(input,VK_NOT,FOLLOW_VK_NOT_in_lhs675);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_lhs_in_lhs677);
                    lhs();

                    state._fsp--;


                    match(input, Token.UP, null);

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:134:4: ^( VK_EVAL VT_PAREN_CHUNK )
                    {
                    match(input,VK_EVAL,FOLLOW_VK_EVAL_in_lhs684);

                    match(input, Token.DOWN, null);
                    match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_lhs686);

                    match(input, Token.UP, null);

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:135:4: ^( VK_FORALL ( lhs )+ )
                    {
                    match(input,VK_FORALL,FOLLOW_VK_FORALL_in_lhs693);

                    match(input, Token.DOWN, null);
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:135:16: ( lhs )+
                    int cnt29=0;
                    loop29:
                    do {
                        int alt29=2;
                        int LA29_0 = input.LA(1);

                        if ( ((LA29_0>=VT_AND_PREFIX && LA29_0<=VT_OR_INFIX)||LA29_0==VT_PATTERN||LA29_0==VK_EVAL||LA29_0==VK_NOT||(LA29_0>=VK_EXISTS && LA29_0<=VK_FORALL)||LA29_0==FROM) ) {
                            alt29=1;
                        }


                        switch (alt29) {
                        case 1 :
                            // src/main/resources/org/drools/lang/Tree2TestDRL.g:135:16: lhs
                            {
                            pushFollow(FOLLOW_lhs_in_lhs695);
                            lhs();

                            state._fsp--;


                            }
                            break;

                        default :
                            if ( cnt29 >= 1 ) break loop29;
                                EarlyExitException eee =
                                    new EarlyExitException(29, input);
                                throw eee;
                        }
                        cnt29++;
                    } while (true);


                    match(input, Token.UP, null);

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:136:4: ^( FROM lhs_pattern from_elements )
                    {
                    match(input,FROM,FOLLOW_FROM_in_lhs703);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_lhs_pattern_in_lhs705);
                    lhs_pattern();

                    state._fsp--;

                    pushFollow(FOLLOW_from_elements_in_lhs707);
                    from_elements();

                    state._fsp--;


                    match(input, Token.UP, null);

                    }
                    break;
                case 10 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:137:4: lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs713);
                    lhs_pattern();

                    state._fsp--;


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
    // $ANTLR end "lhs"


    // $ANTLR start "from_elements"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:140:1: from_elements : ( ^( ACCUMULATE lhs ( accumulate_init_clause | accumulate_id_clause ) ) | ^( COLLECT lhs ) | ^( VK_ENTRY_POINT VT_ENTRYPOINT_ID ) | ^( VT_FROM_SOURCE ID ( VT_PAREN_CHUNK )? ( expression_chain )? ) );
    public final void from_elements() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:141:2: ( ^( ACCUMULATE lhs ( accumulate_init_clause | accumulate_id_clause ) ) | ^( COLLECT lhs ) | ^( VK_ENTRY_POINT VT_ENTRYPOINT_ID ) | ^( VT_FROM_SOURCE ID ( VT_PAREN_CHUNK )? ( expression_chain )? ) )
            int alt34=4;
            switch ( input.LA(1) ) {
            case ACCUMULATE:
                {
                alt34=1;
                }
                break;
            case COLLECT:
                {
                alt34=2;
                }
                break;
            case VK_ENTRY_POINT:
                {
                alt34=3;
                }
                break;
            case VT_FROM_SOURCE:
                {
                alt34=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;
            }

            switch (alt34) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:141:4: ^( ACCUMULATE lhs ( accumulate_init_clause | accumulate_id_clause ) )
                    {
                    match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_from_elements725);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_lhs_in_from_elements727);
                    lhs();

                    state._fsp--;

                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:141:21: ( accumulate_init_clause | accumulate_id_clause )
                    int alt31=2;
                    int LA31_0 = input.LA(1);

                    if ( (LA31_0==VT_ACCUMULATE_INIT_CLAUSE) ) {
                        alt31=1;
                    }
                    else if ( (LA31_0==VT_ACCUMULATE_ID_CLAUSE) ) {
                        alt31=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 31, 0, input);

                        throw nvae;
                    }
                    switch (alt31) {
                        case 1 :
                            // src/main/resources/org/drools/lang/Tree2TestDRL.g:141:22: accumulate_init_clause
                            {
                            pushFollow(FOLLOW_accumulate_init_clause_in_from_elements730);
                            accumulate_init_clause();

                            state._fsp--;


                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/Tree2TestDRL.g:141:45: accumulate_id_clause
                            {
                            pushFollow(FOLLOW_accumulate_id_clause_in_from_elements732);
                            accumulate_id_clause();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null);

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:142:4: ^( COLLECT lhs )
                    {
                    match(input,COLLECT,FOLLOW_COLLECT_in_from_elements740);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_lhs_in_from_elements742);
                    lhs();

                    state._fsp--;


                    match(input, Token.UP, null);

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:143:4: ^( VK_ENTRY_POINT VT_ENTRYPOINT_ID )
                    {
                    match(input,VK_ENTRY_POINT,FOLLOW_VK_ENTRY_POINT_in_from_elements749);

                    match(input, Token.DOWN, null);
                    match(input,VT_ENTRYPOINT_ID,FOLLOW_VT_ENTRYPOINT_ID_in_from_elements751);

                    match(input, Token.UP, null);

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:144:4: ^( VT_FROM_SOURCE ID ( VT_PAREN_CHUNK )? ( expression_chain )? )
                    {
                    match(input,VT_FROM_SOURCE,FOLLOW_VT_FROM_SOURCE_in_from_elements758);

                    match(input, Token.DOWN, null);
                    match(input,ID,FOLLOW_ID_in_from_elements760);
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:144:24: ( VT_PAREN_CHUNK )?
                    int alt32=2;
                    int LA32_0 = input.LA(1);

                    if ( (LA32_0==VT_PAREN_CHUNK) ) {
                        alt32=1;
                    }
                    switch (alt32) {
                        case 1 :
                            // src/main/resources/org/drools/lang/Tree2TestDRL.g:144:24: VT_PAREN_CHUNK
                            {
                            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_from_elements762);

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:144:40: ( expression_chain )?
                    int alt33=2;
                    int LA33_0 = input.LA(1);

                    if ( (LA33_0==VT_EXPRESSION_CHAIN) ) {
                        alt33=1;
                    }
                    switch (alt33) {
                        case 1 :
                            // src/main/resources/org/drools/lang/Tree2TestDRL.g:144:40: expression_chain
                            {
                            pushFollow(FOLLOW_expression_chain_in_from_elements765);
                            expression_chain();

                            state._fsp--;


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
    // $ANTLR end "from_elements"


    // $ANTLR start "accumulate_init_clause"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:147:1: accumulate_init_clause : ^( VT_ACCUMULATE_INIT_CLAUSE ^( VK_INIT VT_PAREN_CHUNK ) ^( VK_ACTION VT_PAREN_CHUNK ) ( accumulate_init_reverse_clause )? ^( VK_RESULT VT_PAREN_CHUNK ) ) ;
    public final void accumulate_init_clause() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:148:2: ( ^( VT_ACCUMULATE_INIT_CLAUSE ^( VK_INIT VT_PAREN_CHUNK ) ^( VK_ACTION VT_PAREN_CHUNK ) ( accumulate_init_reverse_clause )? ^( VK_RESULT VT_PAREN_CHUNK ) ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:148:4: ^( VT_ACCUMULATE_INIT_CLAUSE ^( VK_INIT VT_PAREN_CHUNK ) ^( VK_ACTION VT_PAREN_CHUNK ) ( accumulate_init_reverse_clause )? ^( VK_RESULT VT_PAREN_CHUNK ) )
            {
            match(input,VT_ACCUMULATE_INIT_CLAUSE,FOLLOW_VT_ACCUMULATE_INIT_CLAUSE_in_accumulate_init_clause779);

            match(input, Token.DOWN, null);
            match(input,VK_INIT,FOLLOW_VK_INIT_in_accumulate_init_clause786);

            match(input, Token.DOWN, null);
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause788);

            match(input, Token.UP, null);
            match(input,VK_ACTION,FOLLOW_VK_ACTION_in_accumulate_init_clause796);

            match(input, Token.DOWN, null);
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause798);

            match(input, Token.UP, null);
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:151:4: ( accumulate_init_reverse_clause )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==VK_REVERSE) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:151:4: accumulate_init_reverse_clause
                    {
                    pushFollow(FOLLOW_accumulate_init_reverse_clause_in_accumulate_init_clause805);
                    accumulate_init_reverse_clause();

                    state._fsp--;


                    }
                    break;

            }

            match(input,VK_RESULT,FOLLOW_VK_RESULT_in_accumulate_init_clause812);

            match(input, Token.DOWN, null);
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause814);

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
    // $ANTLR end "accumulate_init_clause"


    // $ANTLR start "accumulate_init_reverse_clause"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:155:1: accumulate_init_reverse_clause : ^( VK_REVERSE VT_PAREN_CHUNK ) ;
    public final void accumulate_init_reverse_clause() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:156:2: ( ^( VK_REVERSE VT_PAREN_CHUNK ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:156:4: ^( VK_REVERSE VT_PAREN_CHUNK )
            {
            match(input,VK_REVERSE,FOLLOW_VK_REVERSE_in_accumulate_init_reverse_clause828);

            match(input, Token.DOWN, null);
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_reverse_clause830);

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
    // $ANTLR end "accumulate_init_reverse_clause"


    // $ANTLR start "accumulate_id_clause"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:160:1: accumulate_id_clause : ^( VT_ACCUMULATE_ID_CLAUSE ID VT_PAREN_CHUNK ) ;
    public final void accumulate_id_clause() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:161:2: ( ^( VT_ACCUMULATE_ID_CLAUSE ID VT_PAREN_CHUNK ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:161:4: ^( VT_ACCUMULATE_ID_CLAUSE ID VT_PAREN_CHUNK )
            {
            match(input,VT_ACCUMULATE_ID_CLAUSE,FOLLOW_VT_ACCUMULATE_ID_CLAUSE_in_accumulate_id_clause844);

            match(input, Token.DOWN, null);
            match(input,ID,FOLLOW_ID_in_accumulate_id_clause846);
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_id_clause848);

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
    // $ANTLR end "accumulate_id_clause"


    // $ANTLR start "lhs_pattern"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:164:1: lhs_pattern : ^( VT_PATTERN fact_expression ) ( over_clause )? ;
    public final void lhs_pattern() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:165:2: ( ^( VT_PATTERN fact_expression ) ( over_clause )? )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:165:4: ^( VT_PATTERN fact_expression ) ( over_clause )?
            {
            match(input,VT_PATTERN,FOLLOW_VT_PATTERN_in_lhs_pattern861);

            match(input, Token.DOWN, null);
            pushFollow(FOLLOW_fact_expression_in_lhs_pattern863);
            fact_expression();

            state._fsp--;


            match(input, Token.UP, null);
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:165:34: ( over_clause )?
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==OVER) ) {
                alt36=1;
            }
            switch (alt36) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:165:34: over_clause
                    {
                    pushFollow(FOLLOW_over_clause_in_lhs_pattern866);
                    over_clause();

                    state._fsp--;


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
    // $ANTLR end "lhs_pattern"


    // $ANTLR start "over_clause"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:168:1: over_clause : ^( OVER ( over_element )+ ) ;
    public final void over_clause() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:169:2: ( ^( OVER ( over_element )+ ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:169:4: ^( OVER ( over_element )+ )
            {
            match(input,OVER,FOLLOW_OVER_in_over_clause879);

            match(input, Token.DOWN, null);
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:169:11: ( over_element )+
            int cnt37=0;
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( (LA37_0==VT_BEHAVIOR) ) {
                    alt37=1;
                }


                switch (alt37) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:169:11: over_element
                    {
                    pushFollow(FOLLOW_over_element_in_over_clause881);
                    over_element();

                    state._fsp--;


                    }
                    break;

                default :
                    if ( cnt37 >= 1 ) break loop37;
                        EarlyExitException eee =
                            new EarlyExitException(37, input);
                        throw eee;
                }
                cnt37++;
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
    // $ANTLR end "over_clause"


    // $ANTLR start "over_element"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:172:1: over_element : ^( VT_BEHAVIOR ID ID VT_PAREN_CHUNK ) ;
    public final void over_element() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:173:2: ( ^( VT_BEHAVIOR ID ID VT_PAREN_CHUNK ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:173:4: ^( VT_BEHAVIOR ID ID VT_PAREN_CHUNK )
            {
            match(input,VT_BEHAVIOR,FOLLOW_VT_BEHAVIOR_in_over_element895);

            match(input, Token.DOWN, null);
            match(input,ID,FOLLOW_ID_in_over_element897);
            match(input,ID,FOLLOW_ID_in_over_element899);
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_over_element901);

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
    // $ANTLR end "over_element"


    // $ANTLR start "fact_expression"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:176:1: fact_expression : ( ^( DOUBLE_PIPE fact_expression fact_expression ) | ^( DOUBLE_AMPER fact_expression fact_expression ) | ^( VT_FACT_BINDING VT_LABEL fact_expression ) | ^( VT_FACT pattern_type ( fact_expression )* ) | ^( VT_FACT_OR fact_expression fact_expression ) | ^( VK_EVAL VT_PAREN_CHUNK ) | ^( VK_IN ( VK_NOT )? ( fact_expression )+ ) | ^( EQUAL fact_expression ) | ^( GREATER fact_expression ) | ^( GREATER_EQUAL fact_expression ) | ^( LESS fact_expression ) | ^( LESS_EQUAL fact_expression ) | ^( NOT_EQUAL fact_expression ) | ^( VK_OPERATOR ( VK_NOT )? ( VT_SQUARE_CHUNK )? fact_expression ) | ^( ID ( VK_NOT )? ( VT_SQUARE_CHUNK )? fact_expression ) | ^( VT_BIND_FIELD VT_LABEL fact_expression ) | ^( VT_FIELD fact_expression ( fact_expression )? ) | ^( VT_ACCESSOR_PATH ( accessor_element )+ ) | STRING | INT | FLOAT | BOOL | NULL | VT_PAREN_CHUNK );
    public final void fact_expression() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:177:2: ( ^( DOUBLE_PIPE fact_expression fact_expression ) | ^( DOUBLE_AMPER fact_expression fact_expression ) | ^( VT_FACT_BINDING VT_LABEL fact_expression ) | ^( VT_FACT pattern_type ( fact_expression )* ) | ^( VT_FACT_OR fact_expression fact_expression ) | ^( VK_EVAL VT_PAREN_CHUNK ) | ^( VK_IN ( VK_NOT )? ( fact_expression )+ ) | ^( EQUAL fact_expression ) | ^( GREATER fact_expression ) | ^( GREATER_EQUAL fact_expression ) | ^( LESS fact_expression ) | ^( LESS_EQUAL fact_expression ) | ^( NOT_EQUAL fact_expression ) | ^( VK_OPERATOR ( VK_NOT )? ( VT_SQUARE_CHUNK )? fact_expression ) | ^( ID ( VK_NOT )? ( VT_SQUARE_CHUNK )? fact_expression ) | ^( VT_BIND_FIELD VT_LABEL fact_expression ) | ^( VT_FIELD fact_expression ( fact_expression )? ) | ^( VT_ACCESSOR_PATH ( accessor_element )+ ) | STRING | INT | FLOAT | BOOL | NULL | VT_PAREN_CHUNK )
            int alt47=24;
            switch ( input.LA(1) ) {
            case DOUBLE_PIPE:
                {
                alt47=1;
                }
                break;
            case DOUBLE_AMPER:
                {
                alt47=2;
                }
                break;
            case VT_FACT_BINDING:
                {
                alt47=3;
                }
                break;
            case VT_FACT:
                {
                alt47=4;
                }
                break;
            case VT_FACT_OR:
                {
                alt47=5;
                }
                break;
            case VK_EVAL:
                {
                alt47=6;
                }
                break;
            case VK_IN:
                {
                alt47=7;
                }
                break;
            case EQUAL:
                {
                alt47=8;
                }
                break;
            case GREATER:
                {
                alt47=9;
                }
                break;
            case GREATER_EQUAL:
                {
                alt47=10;
                }
                break;
            case LESS:
                {
                alt47=11;
                }
                break;
            case LESS_EQUAL:
                {
                alt47=12;
                }
                break;
            case NOT_EQUAL:
                {
                alt47=13;
                }
                break;
            case VK_OPERATOR:
                {
                alt47=14;
                }
                break;
            case ID:
                {
                alt47=15;
                }
                break;
            case VT_BIND_FIELD:
                {
                alt47=16;
                }
                break;
            case VT_FIELD:
                {
                alt47=17;
                }
                break;
            case VT_ACCESSOR_PATH:
                {
                alt47=18;
                }
                break;
            case STRING:
                {
                alt47=19;
                }
                break;
            case INT:
                {
                alt47=20;
                }
                break;
            case FLOAT:
                {
                alt47=21;
                }
                break;
            case BOOL:
                {
                alt47=22;
                }
                break;
            case NULL:
                {
                alt47=23;
                }
                break;
            case VT_PAREN_CHUNK:
                {
                alt47=24;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 47, 0, input);

                throw nvae;
            }

            switch (alt47) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:177:4: ^( DOUBLE_PIPE fact_expression fact_expression )
                    {
                    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_fact_expression914);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fact_expression_in_fact_expression916);
                    fact_expression();

                    state._fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression918);
                    fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null);

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:178:4: ^( DOUBLE_AMPER fact_expression fact_expression )
                    {
                    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_fact_expression925);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fact_expression_in_fact_expression927);
                    fact_expression();

                    state._fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression929);
                    fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null);

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:179:4: ^( VT_FACT_BINDING VT_LABEL fact_expression )
                    {
                    match(input,VT_FACT_BINDING,FOLLOW_VT_FACT_BINDING_in_fact_expression936);

                    match(input, Token.DOWN, null);
                    match(input,VT_LABEL,FOLLOW_VT_LABEL_in_fact_expression938);
                    pushFollow(FOLLOW_fact_expression_in_fact_expression940);
                    fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null);

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:180:4: ^( VT_FACT pattern_type ( fact_expression )* )
                    {
                    match(input,VT_FACT,FOLLOW_VT_FACT_in_fact_expression947);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_pattern_type_in_fact_expression949);
                    pattern_type();

                    state._fsp--;

                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:180:27: ( fact_expression )*
                    loop38:
                    do {
                        int alt38=2;
                        int LA38_0 = input.LA(1);

                        if ( (LA38_0==VT_FACT||LA38_0==VT_PAREN_CHUNK||(LA38_0>=VT_FACT_BINDING && LA38_0<=VT_ACCESSOR_PATH)||LA38_0==VK_EVAL||LA38_0==VK_IN||LA38_0==VK_OPERATOR||LA38_0==FLOAT||LA38_0==STRING||LA38_0==BOOL||LA38_0==NULL||(LA38_0>=GREATER && LA38_0<=LESS)||(LA38_0>=DOUBLE_AMPER && LA38_0<=DOUBLE_PIPE)||LA38_0==ID||LA38_0==INT||(LA38_0>=EQUAL && LA38_0<=NOT_EQUAL)) ) {
                            alt38=1;
                        }


                        switch (alt38) {
                        case 1 :
                            // src/main/resources/org/drools/lang/Tree2TestDRL.g:180:27: fact_expression
                            {
                            pushFollow(FOLLOW_fact_expression_in_fact_expression951);
                            fact_expression();

                            state._fsp--;


                            }
                            break;

                        default :
                            break loop38;
                        }
                    } while (true);


                    match(input, Token.UP, null);

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:181:4: ^( VT_FACT_OR fact_expression fact_expression )
                    {
                    match(input,VT_FACT_OR,FOLLOW_VT_FACT_OR_in_fact_expression959);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fact_expression_in_fact_expression961);
                    fact_expression();

                    state._fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression963);
                    fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null);

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:182:4: ^( VK_EVAL VT_PAREN_CHUNK )
                    {
                    match(input,VK_EVAL,FOLLOW_VK_EVAL_in_fact_expression970);

                    match(input, Token.DOWN, null);
                    match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_fact_expression972);

                    match(input, Token.UP, null);

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:183:4: ^( VK_IN ( VK_NOT )? ( fact_expression )+ )
                    {
                    match(input,VK_IN,FOLLOW_VK_IN_in_fact_expression979);

                    match(input, Token.DOWN, null);
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:183:12: ( VK_NOT )?
                    int alt39=2;
                    int LA39_0 = input.LA(1);

                    if ( (LA39_0==VK_NOT) ) {
                        alt39=1;
                    }
                    switch (alt39) {
                        case 1 :
                            // src/main/resources/org/drools/lang/Tree2TestDRL.g:183:12: VK_NOT
                            {
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression981);

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:183:20: ( fact_expression )+
                    int cnt40=0;
                    loop40:
                    do {
                        int alt40=2;
                        int LA40_0 = input.LA(1);

                        if ( (LA40_0==VT_FACT||LA40_0==VT_PAREN_CHUNK||(LA40_0>=VT_FACT_BINDING && LA40_0<=VT_ACCESSOR_PATH)||LA40_0==VK_EVAL||LA40_0==VK_IN||LA40_0==VK_OPERATOR||LA40_0==FLOAT||LA40_0==STRING||LA40_0==BOOL||LA40_0==NULL||(LA40_0>=GREATER && LA40_0<=LESS)||(LA40_0>=DOUBLE_AMPER && LA40_0<=DOUBLE_PIPE)||LA40_0==ID||LA40_0==INT||(LA40_0>=EQUAL && LA40_0<=NOT_EQUAL)) ) {
                            alt40=1;
                        }


                        switch (alt40) {
                        case 1 :
                            // src/main/resources/org/drools/lang/Tree2TestDRL.g:183:20: fact_expression
                            {
                            pushFollow(FOLLOW_fact_expression_in_fact_expression984);
                            fact_expression();

                            state._fsp--;


                            }
                            break;

                        default :
                            if ( cnt40 >= 1 ) break loop40;
                                EarlyExitException eee =
                                    new EarlyExitException(40, input);
                                throw eee;
                        }
                        cnt40++;
                    } while (true);


                    match(input, Token.UP, null);

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:184:4: ^( EQUAL fact_expression )
                    {
                    match(input,EQUAL,FOLLOW_EQUAL_in_fact_expression992);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fact_expression_in_fact_expression994);
                    fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null);

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:185:4: ^( GREATER fact_expression )
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_fact_expression1001);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1003);
                    fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null);

                    }
                    break;
                case 10 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:186:4: ^( GREATER_EQUAL fact_expression )
                    {
                    match(input,GREATER_EQUAL,FOLLOW_GREATER_EQUAL_in_fact_expression1010);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1012);
                    fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null);

                    }
                    break;
                case 11 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:187:4: ^( LESS fact_expression )
                    {
                    match(input,LESS,FOLLOW_LESS_in_fact_expression1019);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1021);
                    fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null);

                    }
                    break;
                case 12 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:188:4: ^( LESS_EQUAL fact_expression )
                    {
                    match(input,LESS_EQUAL,FOLLOW_LESS_EQUAL_in_fact_expression1028);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1030);
                    fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null);

                    }
                    break;
                case 13 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:189:4: ^( NOT_EQUAL fact_expression )
                    {
                    match(input,NOT_EQUAL,FOLLOW_NOT_EQUAL_in_fact_expression1037);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1039);
                    fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null);

                    }
                    break;
                case 14 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:190:4: ^( VK_OPERATOR ( VK_NOT )? ( VT_SQUARE_CHUNK )? fact_expression )
                    {
                    match(input,VK_OPERATOR,FOLLOW_VK_OPERATOR_in_fact_expression1046);

                    match(input, Token.DOWN, null);
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:190:18: ( VK_NOT )?
                    int alt41=2;
                    int LA41_0 = input.LA(1);

                    if ( (LA41_0==VK_NOT) ) {
                        alt41=1;
                    }
                    switch (alt41) {
                        case 1 :
                            // src/main/resources/org/drools/lang/Tree2TestDRL.g:190:18: VK_NOT
                            {
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1048);

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:190:26: ( VT_SQUARE_CHUNK )?
                    int alt42=2;
                    int LA42_0 = input.LA(1);

                    if ( (LA42_0==VT_SQUARE_CHUNK) ) {
                        alt42=1;
                    }
                    switch (alt42) {
                        case 1 :
                            // src/main/resources/org/drools/lang/Tree2TestDRL.g:190:26: VT_SQUARE_CHUNK
                            {
                            match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_fact_expression1051);

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1054);
                    fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null);

                    }
                    break;
                case 15 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:191:4: ^( ID ( VK_NOT )? ( VT_SQUARE_CHUNK )? fact_expression )
                    {
                    match(input,ID,FOLLOW_ID_in_fact_expression1061);

                    match(input, Token.DOWN, null);
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:191:9: ( VK_NOT )?
                    int alt43=2;
                    int LA43_0 = input.LA(1);

                    if ( (LA43_0==VK_NOT) ) {
                        alt43=1;
                    }
                    switch (alt43) {
                        case 1 :
                            // src/main/resources/org/drools/lang/Tree2TestDRL.g:191:9: VK_NOT
                            {
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1063);

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:191:17: ( VT_SQUARE_CHUNK )?
                    int alt44=2;
                    int LA44_0 = input.LA(1);

                    if ( (LA44_0==VT_SQUARE_CHUNK) ) {
                        alt44=1;
                    }
                    switch (alt44) {
                        case 1 :
                            // src/main/resources/org/drools/lang/Tree2TestDRL.g:191:17: VT_SQUARE_CHUNK
                            {
                            match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_fact_expression1066);

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1069);
                    fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null);

                    }
                    break;
                case 16 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:192:4: ^( VT_BIND_FIELD VT_LABEL fact_expression )
                    {
                    match(input,VT_BIND_FIELD,FOLLOW_VT_BIND_FIELD_in_fact_expression1076);

                    match(input, Token.DOWN, null);
                    match(input,VT_LABEL,FOLLOW_VT_LABEL_in_fact_expression1078);
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1080);
                    fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null);

                    }
                    break;
                case 17 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:193:4: ^( VT_FIELD fact_expression ( fact_expression )? )
                    {
                    match(input,VT_FIELD,FOLLOW_VT_FIELD_in_fact_expression1087);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1089);
                    fact_expression();

                    state._fsp--;

                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:193:31: ( fact_expression )?
                    int alt45=2;
                    int LA45_0 = input.LA(1);

                    if ( (LA45_0==VT_FACT||LA45_0==VT_PAREN_CHUNK||(LA45_0>=VT_FACT_BINDING && LA45_0<=VT_ACCESSOR_PATH)||LA45_0==VK_EVAL||LA45_0==VK_IN||LA45_0==VK_OPERATOR||LA45_0==FLOAT||LA45_0==STRING||LA45_0==BOOL||LA45_0==NULL||(LA45_0>=GREATER && LA45_0<=LESS)||(LA45_0>=DOUBLE_AMPER && LA45_0<=DOUBLE_PIPE)||LA45_0==ID||LA45_0==INT||(LA45_0>=EQUAL && LA45_0<=NOT_EQUAL)) ) {
                        alt45=1;
                    }
                    switch (alt45) {
                        case 1 :
                            // src/main/resources/org/drools/lang/Tree2TestDRL.g:193:31: fact_expression
                            {
                            pushFollow(FOLLOW_fact_expression_in_fact_expression1091);
                            fact_expression();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null);

                    }
                    break;
                case 18 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:194:4: ^( VT_ACCESSOR_PATH ( accessor_element )+ )
                    {
                    match(input,VT_ACCESSOR_PATH,FOLLOW_VT_ACCESSOR_PATH_in_fact_expression1099);

                    match(input, Token.DOWN, null);
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:194:23: ( accessor_element )+
                    int cnt46=0;
                    loop46:
                    do {
                        int alt46=2;
                        int LA46_0 = input.LA(1);

                        if ( (LA46_0==VT_ACCESSOR_ELEMENT) ) {
                            alt46=1;
                        }


                        switch (alt46) {
                        case 1 :
                            // src/main/resources/org/drools/lang/Tree2TestDRL.g:194:23: accessor_element
                            {
                            pushFollow(FOLLOW_accessor_element_in_fact_expression1101);
                            accessor_element();

                            state._fsp--;


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


                    match(input, Token.UP, null);

                    }
                    break;
                case 19 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:195:4: STRING
                    {
                    match(input,STRING,FOLLOW_STRING_in_fact_expression1108);

                    }
                    break;
                case 20 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:196:4: INT
                    {
                    match(input,INT,FOLLOW_INT_in_fact_expression1113);

                    }
                    break;
                case 21 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:197:4: FLOAT
                    {
                    match(input,FLOAT,FOLLOW_FLOAT_in_fact_expression1118);

                    }
                    break;
                case 22 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:198:4: BOOL
                    {
                    match(input,BOOL,FOLLOW_BOOL_in_fact_expression1123);

                    }
                    break;
                case 23 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:199:4: NULL
                    {
                    match(input,NULL,FOLLOW_NULL_in_fact_expression1128);

                    }
                    break;
                case 24 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:200:4: VT_PAREN_CHUNK
                    {
                    match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_fact_expression1133);

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
    // $ANTLR end "fact_expression"


    // $ANTLR start "pattern_type"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:203:1: pattern_type : ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) ;
    public final void pattern_type() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:204:2: ( ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:204:4: ^( VT_PATTERN_TYPE ( ID )+ ( dimension_definition )* )
            {
            match(input,VT_PATTERN_TYPE,FOLLOW_VT_PATTERN_TYPE_in_pattern_type1145);

            match(input, Token.DOWN, null);
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:204:22: ( ID )+
            int cnt48=0;
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( (LA48_0==ID) ) {
                    alt48=1;
                }


                switch (alt48) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:204:22: ID
                    {
                    match(input,ID,FOLLOW_ID_in_pattern_type1147);

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

            // src/main/resources/org/drools/lang/Tree2TestDRL.g:204:26: ( dimension_definition )*
            loop49:
            do {
                int alt49=2;
                int LA49_0 = input.LA(1);

                if ( (LA49_0==LEFT_SQUARE) ) {
                    alt49=1;
                }


                switch (alt49) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:204:26: dimension_definition
                    {
                    pushFollow(FOLLOW_dimension_definition_in_pattern_type1150);
                    dimension_definition();

                    state._fsp--;


                    }
                    break;

                default :
                    break loop49;
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
    // $ANTLR end "pattern_type"


    // $ANTLR start "data_type"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:207:1: data_type : ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) ;
    public final void data_type() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:208:2: ( ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:208:4: ^( VT_DATA_TYPE ( ID )+ ( dimension_definition )* )
            {
            match(input,VT_DATA_TYPE,FOLLOW_VT_DATA_TYPE_in_data_type1164);

            match(input, Token.DOWN, null);
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:208:19: ( ID )+
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
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:208:19: ID
                    {
                    match(input,ID,FOLLOW_ID_in_data_type1166);

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

            // src/main/resources/org/drools/lang/Tree2TestDRL.g:208:23: ( dimension_definition )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);

                if ( (LA51_0==LEFT_SQUARE) ) {
                    alt51=1;
                }


                switch (alt51) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:208:23: dimension_definition
                    {
                    pushFollow(FOLLOW_dimension_definition_in_data_type1169);
                    dimension_definition();

                    state._fsp--;


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
    // $ANTLR end "data_type"


    // $ANTLR start "dimension_definition"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:211:1: dimension_definition : LEFT_SQUARE RIGHT_SQUARE ;
    public final void dimension_definition() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:212:2: ( LEFT_SQUARE RIGHT_SQUARE )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:212:4: LEFT_SQUARE RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dimension_definition1182);
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dimension_definition1184);

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
    // $ANTLR end "dimension_definition"


    // $ANTLR start "accessor_element"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:215:1: accessor_element : ^( VT_ACCESSOR_ELEMENT ID ( VT_SQUARE_CHUNK )* ) ;
    public final void accessor_element() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:216:2: ( ^( VT_ACCESSOR_ELEMENT ID ( VT_SQUARE_CHUNK )* ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:216:4: ^( VT_ACCESSOR_ELEMENT ID ( VT_SQUARE_CHUNK )* )
            {
            match(input,VT_ACCESSOR_ELEMENT,FOLLOW_VT_ACCESSOR_ELEMENT_in_accessor_element1196);

            match(input, Token.DOWN, null);
            match(input,ID,FOLLOW_ID_in_accessor_element1198);
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:216:29: ( VT_SQUARE_CHUNK )*
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);

                if ( (LA52_0==VT_SQUARE_CHUNK) ) {
                    alt52=1;
                }


                switch (alt52) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:216:29: VT_SQUARE_CHUNK
                    {
                    match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_accessor_element1200);

                    }
                    break;

                default :
                    break loop52;
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
    // $ANTLR end "accessor_element"


    // $ANTLR start "expression_chain"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:219:1: expression_chain : ^( VT_EXPRESSION_CHAIN ID ( VT_SQUARE_CHUNK )? ( VT_PAREN_CHUNK )? ( expression_chain )? ) ;
    public final void expression_chain() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:220:2: ( ^( VT_EXPRESSION_CHAIN ID ( VT_SQUARE_CHUNK )? ( VT_PAREN_CHUNK )? ( expression_chain )? ) )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:220:4: ^( VT_EXPRESSION_CHAIN ID ( VT_SQUARE_CHUNK )? ( VT_PAREN_CHUNK )? ( expression_chain )? )
            {
            match(input,VT_EXPRESSION_CHAIN,FOLLOW_VT_EXPRESSION_CHAIN_in_expression_chain1214);

            match(input, Token.DOWN, null);
            match(input,ID,FOLLOW_ID_in_expression_chain1216);
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:220:29: ( VT_SQUARE_CHUNK )?
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==VT_SQUARE_CHUNK) ) {
                alt53=1;
            }
            switch (alt53) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:220:29: VT_SQUARE_CHUNK
                    {
                    match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_expression_chain1218);

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/Tree2TestDRL.g:220:46: ( VT_PAREN_CHUNK )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==VT_PAREN_CHUNK) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:220:46: VT_PAREN_CHUNK
                    {
                    match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_expression_chain1221);

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/Tree2TestDRL.g:220:62: ( expression_chain )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==VT_EXPRESSION_CHAIN) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // src/main/resources/org/drools/lang/Tree2TestDRL.g:220:62: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain1224);
                    expression_chain();

                    state._fsp--;


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
    // $ANTLR end "expression_chain"


    // $ANTLR start "curly_chunk"
    // src/main/resources/org/drools/lang/Tree2TestDRL.g:223:1: curly_chunk : VT_CURLY_CHUNK ;
    public final void curly_chunk() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:224:2: ( VT_CURLY_CHUNK )
            // src/main/resources/org/drools/lang/Tree2TestDRL.g:224:4: VT_CURLY_CHUNK
            {
            match(input,VT_CURLY_CHUNK,FOLLOW_VT_CURLY_CHUNK_in_curly_chunk1238);

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
    // $ANTLR end "curly_chunk"

    // Delegated rules


 

    public static final BitSet FOLLOW_VT_COMPILATION_UNIT_in_compilation_unit43 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_package_statement_in_compilation_unit45 = new BitSet(new long[]{0x5CFF000000000028L,0x000000000000007AL,0x0000000000000000L,0x0000000000004200L});
    public static final BitSet FOLLOW_statement_in_compilation_unit48 = new BitSet(new long[]{0x5CFF000000000028L,0x000000000000007AL,0x0000000000000000L,0x0000000000004200L});
    public static final BitSet FOLLOW_VK_PACKAGE_in_package_statement63 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_package_id_in_package_statement65 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PACKAGE_ID_in_package_id78 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_package_id80 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
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
    public static final BitSet FOLLOW_VK_FUNCTION_in_function_import_statement165 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement167 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_IMPORT_ID_in_import_name180 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_import_name182 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000120L});
    public static final BitSet FOLLOW_DOT_STAR_in_import_name185 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_GLOBAL_in_global199 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_global201 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_VT_GLOBAL_ID_in_global203 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_FUNCTION_in_function216 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_function218 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_VT_FUNCTION_ID_in_function221 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_parameters_in_function223 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_curly_chunk_in_function225 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_QUERY_in_query238 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_QUERY_ID_in_query240 = new BitSet(new long[]{0x0000200000200000L});
    public static final BitSet FOLLOW_parameters_in_query242 = new BitSet(new long[]{0x0000200000200000L});
    public static final BitSet FOLLOW_lhs_block_in_query245 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_VK_END_in_query247 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PARAM_LIST_in_parameters260 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_param_definition_in_parameters262 = new BitSet(new long[]{0x0000008000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_data_type_in_param_definition275 = new BitSet(new long[]{0x0000008000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_argument_in_param_definition278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_argument289 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000400000000000L});
    public static final BitSet FOLLOW_dimension_definition_in_argument291 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000400000000000L});
    public static final BitSet FOLLOW_VK_DECLARE_in_type_declaration304 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_TYPE_DECLARE_ID_in_type_declaration306 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L,0x0000000000800000L,0x0000000000000020L});
    public static final BitSet FOLLOW_decl_metadata_in_type_declaration308 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L,0x0000000000800000L,0x0000000000000020L});
    public static final BitSet FOLLOW_decl_field_in_type_declaration311 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_VK_END_in_type_declaration314 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AT_in_decl_metadata327 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_decl_metadata329 = new BitSet(new long[]{0x0000000000080008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_decl_metadata331 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_decl_field345 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_decl_field_initialization_in_decl_field347 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_data_type_in_decl_field350 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_decl_metadata_in_decl_field352 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_EQUALS_in_decl_field_initialization366 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_decl_field_initialization368 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_TEMPLATE_in_template381 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_TEMPLATE_ID_in_template383 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_template_slot_in_template385 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L,0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_VK_END_in_template388 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_SLOT_in_template_slot401 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_template_slot403 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_VT_SLOT_ID_in_template_slot405 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_RULE_in_rule418 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_RULE_ID_in_rule420 = new BitSet(new long[]{0x0000000000014000L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_rule_attributes_in_rule422 = new BitSet(new long[]{0x0000000000010000L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_when_part_in_rule425 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_VT_RHS_CHUNK_in_rule428 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHEN_in_when_part440 = new BitSet(new long[]{0x0000200000200000L});
    public static final BitSet FOLLOW_lhs_block_in_when_part442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_RULE_ATTRIBUTES_in_rule_attributes454 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_ATTRIBUTES_in_rule_attributes456 = new BitSet(new long[]{0x1CFF000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes459 = new BitSet(new long[]{0x1CFF000000000008L,0x0000000000000000L,0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_VK_SALIENCE_in_rule_attribute473 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_rule_attribute475 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_NO_LOOP_in_rule_attribute487 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute489 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_AGENDA_GROUP_in_rule_attribute499 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute501 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DURATION_in_rule_attribute510 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INT_in_rule_attribute512 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ACTIVATION_GROUP_in_rule_attribute522 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute524 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_AUTO_FOCUS_in_rule_attribute532 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute534 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DATE_EFFECTIVE_in_rule_attribute543 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute545 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DATE_EXPIRES_in_rule_attribute553 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute555 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ENABLED_in_rule_attribute563 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute565 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_RULEFLOW_GROUP_in_rule_attribute573 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute575 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_LOCK_ON_ACTIVE_in_rule_attribute583 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute585 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DIALECT_in_rule_attribute593 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute595 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_AND_IMPLICIT_in_lhs_block609 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs_block611 = new BitSet(new long[]{0x0000000103C00008L,0x0000000000006280L,0x0000000000040000L});
    public static final BitSet FOLLOW_VT_OR_PREFIX_in_lhs624 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs626 = new BitSet(new long[]{0x0000000103C00008L,0x0000000000006280L,0x0000000000040000L});
    public static final BitSet FOLLOW_VT_OR_INFIX_in_lhs634 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs636 = new BitSet(new long[]{0x0000000103C00008L,0x0000000000006280L,0x0000000000040000L});
    public static final BitSet FOLLOW_lhs_in_lhs638 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_AND_PREFIX_in_lhs645 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs647 = new BitSet(new long[]{0x0000000103C00008L,0x0000000000006280L,0x0000000000040000L});
    public static final BitSet FOLLOW_VT_AND_INFIX_in_lhs655 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs657 = new BitSet(new long[]{0x0000000103C00008L,0x0000000000006280L,0x0000000000040000L});
    public static final BitSet FOLLOW_lhs_in_lhs659 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EXISTS_in_lhs666 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs668 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_NOT_in_lhs675 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs677 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EVAL_in_lhs684 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_lhs686 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_FORALL_in_lhs693 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs695 = new BitSet(new long[]{0x0000000103C00008L,0x0000000000006280L,0x0000000000040000L});
    public static final BitSet FOLLOW_FROM_in_lhs703 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs705 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000100L,0x0000000000030000L});
    public static final BitSet FOLLOW_from_elements_in_lhs707 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs713 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_from_elements725 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_from_elements727 = new BitSet(new long[]{0x000000000C000000L});
    public static final BitSet FOLLOW_accumulate_init_clause_in_from_elements730 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_accumulate_id_clause_in_from_elements732 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_COLLECT_in_from_elements740 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_from_elements742 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ENTRY_POINT_in_from_elements749 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_ENTRYPOINT_ID_in_from_elements751 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FROM_SOURCE_in_from_elements758 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_from_elements760 = new BitSet(new long[]{0x0000000020080008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_from_elements762 = new BitSet(new long[]{0x0000000020000008L});
    public static final BitSet FOLLOW_expression_chain_in_from_elements765 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ACCUMULATE_INIT_CLAUSE_in_accumulate_init_clause779 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_INIT_in_accumulate_init_clause786 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause788 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ACTION_in_accumulate_init_clause796 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause798 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_accumulate_init_reverse_clause_in_accumulate_init_clause805 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_VK_RESULT_in_accumulate_init_clause812 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause814 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_REVERSE_in_accumulate_init_reverse_clause828 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_reverse_clause830 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ACCUMULATE_ID_CLAUSE_in_accumulate_id_clause844 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_accumulate_id_clause846 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_id_clause848 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PATTERN_in_lhs_pattern861 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_lhs_pattern863 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_over_clause_in_lhs_pattern866 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OVER_in_over_clause879 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_over_element_in_over_clause881 = new BitSet(new long[]{0x0000000000100008L});
    public static final BitSet FOLLOW_VT_BEHAVIOR_in_over_element895 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_over_element897 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_over_element899 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_over_element901 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_fact_expression914 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression916 = new BitSet(new long[]{0x0000003E00080040L,0x0000000000080480L,0x0030060000088820L,0x000000000007A020L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression918 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_fact_expression925 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression927 = new BitSet(new long[]{0x0000003E00080040L,0x0000000000080480L,0x0030060000088820L,0x000000000007A020L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression929 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FACT_BINDING_in_fact_expression936 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_LABEL_in_fact_expression938 = new BitSet(new long[]{0x0000003E00080040L,0x0000000000080480L,0x0030060000088820L,0x000000000007A020L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression940 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FACT_in_fact_expression947 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_pattern_type_in_fact_expression949 = new BitSet(new long[]{0x0000003E00080048L,0x0000000000080480L,0x0030060000088820L,0x000000000007A020L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression951 = new BitSet(new long[]{0x0000003E00080048L,0x0000000000080480L,0x0030060000088820L,0x000000000007A020L});
    public static final BitSet FOLLOW_VT_FACT_OR_in_fact_expression959 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression961 = new BitSet(new long[]{0x0000003E00080040L,0x0000000000080480L,0x0030060000088820L,0x000000000007A020L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression963 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EVAL_in_fact_expression970 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_fact_expression972 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_IN_in_fact_expression979 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression981 = new BitSet(new long[]{0x0000003E00080040L,0x0000000000080480L,0x0030060000088820L,0x000000000007A020L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression984 = new BitSet(new long[]{0x0000003E00080048L,0x0000000000080480L,0x0030060000088820L,0x000000000007A020L});
    public static final BitSet FOLLOW_EQUAL_in_fact_expression992 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression994 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GREATER_in_fact_expression1001 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1003 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GREATER_EQUAL_in_fact_expression1010 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1012 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LESS_in_fact_expression1019 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1021 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LESS_EQUAL_in_fact_expression1028 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1030 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_EQUAL_in_fact_expression1037 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1039 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_OPERATOR_in_fact_expression1046 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1048 = new BitSet(new long[]{0x0000003E000C0040L,0x0000000000080480L,0x0030060000088820L,0x000000000007A020L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_fact_expression1051 = new BitSet(new long[]{0x0000003E00080040L,0x0000000000080480L,0x0030060000088820L,0x000000000007A020L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1054 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_fact_expression1061 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1063 = new BitSet(new long[]{0x0000003E000C0040L,0x0000000000080480L,0x0030060000088820L,0x000000000007A020L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_fact_expression1066 = new BitSet(new long[]{0x0000003E00080040L,0x0000000000080480L,0x0030060000088820L,0x000000000007A020L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1069 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_BIND_FIELD_in_fact_expression1076 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_LABEL_in_fact_expression1078 = new BitSet(new long[]{0x0000003E00080040L,0x0000000000080480L,0x0030060000088820L,0x000000000007A020L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1080 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FIELD_in_fact_expression1087 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1089 = new BitSet(new long[]{0x0000003E00080048L,0x0000000000080480L,0x0030060000088820L,0x000000000007A020L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1091 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ACCESSOR_PATH_in_fact_expression1099 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_accessor_element_in_fact_expression1101 = new BitSet(new long[]{0x0000004000000008L});
    public static final BitSet FOLLOW_STRING_in_fact_expression1108 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_fact_expression1113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_fact_expression1118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_fact_expression1123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_fact_expression1128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_fact_expression1133 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_PATTERN_TYPE_in_pattern_type1145 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_pattern_type1147 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000400000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_dimension_definition_in_pattern_type1150 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000400000000000L});
    public static final BitSet FOLLOW_VT_DATA_TYPE_in_data_type1164 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_data_type1166 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000400000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_dimension_definition_in_data_type1169 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000400000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dimension_definition1182 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dimension_definition1184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_ACCESSOR_ELEMENT_in_accessor_element1196 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_accessor_element1198 = new BitSet(new long[]{0x0000000000040008L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_accessor_element1200 = new BitSet(new long[]{0x0000000000040008L});
    public static final BitSet FOLLOW_VT_EXPRESSION_CHAIN_in_expression_chain1214 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_expression_chain1216 = new BitSet(new long[]{0x00000000200C0008L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_expression_chain1218 = new BitSet(new long[]{0x0000000020080008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_expression_chain1221 = new BitSet(new long[]{0x0000000020000008L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain1224 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_CURLY_CHUNK_in_curly_chunk1238 = new BitSet(new long[]{0x0000000000000002L});

}
