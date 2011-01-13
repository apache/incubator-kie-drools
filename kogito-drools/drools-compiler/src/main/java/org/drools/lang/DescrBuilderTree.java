// $ANTLR 3.3 Nov 30, 2010 12:46:29 src/main/resources/org/drools/lang/DescrBuilderTree.g 2011-01-12 16:44:31

	package org.drools.lang;

	import java.util.HashMap;
	import java.util.Map;
	import java.util.Hashtable;
	import java.util.LinkedList;
	import org.drools.lang.descr.*;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class DescrBuilderTree extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_COMPILATION_UNIT", "VT_FUNCTION_IMPORT", "VT_FACT", "VT_CONSTRAINTS", "VT_LABEL", "VT_QUERY_ID", "VT_TYPE_DECLARE_ID", "VT_TYPE_NAME", "VT_RULE_ID", "VT_ENTRYPOINT_ID", "VT_RULE_ATTRIBUTES", "VT_PKG_ATTRIBUTES", "VT_RHS_CHUNK", "VT_CURLY_CHUNK", "VT_SQUARE_CHUNK", "VT_PAREN_CHUNK", "VT_BEHAVIOR", "VT_AND_IMPLICIT", "VT_AND_PREFIX", "VT_OR_PREFIX", "VT_AND_INFIX", "VT_OR_INFIX", "VT_ACCUMULATE_INIT_CLAUSE", "VT_ACCUMULATE_ID_CLAUSE", "VT_FROM_SOURCE", "VT_EXPRESSION_CHAIN", "VT_FOR_CE", "VT_FOR_FUNCTIONS", "VT_PATTERN", "VT_FACT_BINDING", "VT_FACT_OR", "VT_BIND_FIELD", "VT_FIELD", "VT_ACCESSOR_PATH", "VT_ACCESSOR_ELEMENT", "VT_DATA_TYPE", "VT_PATTERN_TYPE", "VT_PACKAGE_ID", "VT_IMPORT_ID", "VT_GLOBAL_ID", "VT_FUNCTION_ID", "VT_PARAM_LIST", "VT_ARGUMENTS", "VT_EXPRESSION", "VK_DATE_EFFECTIVE", "VK_DATE_EXPIRES", "VK_LOCK_ON_ACTIVE", "VK_NO_LOOP", "VK_AUTO_FOCUS", "VK_ACTIVATION_GROUP", "VK_AGENDA_GROUP", "VK_RULEFLOW_GROUP", "VK_TIMER", "VK_CALENDARS", "VK_DIALECT", "VK_SALIENCE", "VK_ENABLED", "VK_ATTRIBUTES", "VK_RULE", "VK_EXTEND", "VK_IMPLEMENTS", "VK_IMPORT", "VK_PACKAGE", "VK_QUERY", "VK_DECLARE", "VK_FUNCTION", "VK_GLOBAL", "VK_EVAL", "VK_ENTRY_POINT", "VK_NOT", "VK_IN", "VK_OR", "VK_AND", "VK_EXISTS", "VK_FORALL", "VK_FOR", "VK_ACTION", "VK_REVERSE", "VK_RESULT", "VK_OPERATOR", "VK_END", "VK_INIT", "VK_INSTANCEOF", "VK_EXTENDS", "VK_SUPER", "VK_PRIMITIVE_TYPE", "VK_THIS", "VK_VOID", "VK_CLASS", "VK_NEW", "VK_FINAL", "VK_IF", "VK_ELSE", "VK_WHILE", "VK_DO", "VK_CASE", "VK_DEFAULT", "VK_TRY", "VK_CATCH", "VK_FINALLY", "VK_SWITCH", "VK_SYNCHRONIZED", "VK_RETURN", "VK_THROW", "VK_BREAK", "VK_CONTINUE", "VK_ASSERT", "VK_MODIFY", "VK_STATIC", "VK_PUBLIC", "VK_PROTECTED", "VK_PRIVATE", "VK_ABSTRACT", "VK_NATIVE", "VK_TRANSIENT", "VK_VOLATILE", "VK_STRICTFP", "VK_THROWS", "VK_INTERFACE", "VK_ENUM", "SIGNED_DECIMAL", "SIGNED_HEX", "SIGNED_FLOAT", "VT_PROP_KEY", "VT_PROP_VALUE", "SEMICOLON", "ID", "DOT", "DOT_STAR", "STRING", "AT", "COLON", "EQUALS_ASSIGN", "WHEN", "COMMA", "BOOL", "LEFT_PAREN", "RIGHT_PAREN", "FROM", "OVER", "TimePeriod", "DECIMAL", "ACCUMULATE", "COLLECT", "DOUBLE_PIPE", "DOUBLE_AMPER", "ARROW", "EQUALS", "GREATER", "GREATER_EQUALS", "LESS", "LESS_EQUALS", "NOT_EQUALS", "LEFT_SQUARE", "RIGHT_SQUARE", "NULL", "PLUS", "MINUS", "HEX", "FLOAT", "THEN", "LEFT_CURLY", "RIGHT_CURLY", "QUESTION", "PIPE", "XOR", "AMPER", "SHIFT_LEFT", "SHIFT_RIGHT_UNSIG", "SHIFT_RIGHT", "STAR", "DIV", "MOD", "INCR", "DECR", "TILDE", "NEGATION", "PLUS_ASSIGN", "MINUS_ASSIGN", "MULT_ASSIGN", "DIV_ASSIGN", "AND_ASSIGN", "OR_ASSIGN", "XOR_ASSIGN", "MOD_ASSIGN", "EOL", "WS", "Exponent", "FloatTypeSuffix", "HexDigit", "IntegerTypeSuffix", "EscapeSequence", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "IdentifierStart", "IdentifierPart", "MISC"
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
    public static final int SEMICOLON=129;
    public static final int ID=130;
    public static final int DOT=131;
    public static final int DOT_STAR=132;
    public static final int STRING=133;
    public static final int AT=134;
    public static final int COLON=135;
    public static final int EQUALS_ASSIGN=136;
    public static final int WHEN=137;
    public static final int COMMA=138;
    public static final int BOOL=139;
    public static final int LEFT_PAREN=140;
    public static final int RIGHT_PAREN=141;
    public static final int FROM=142;
    public static final int OVER=143;
    public static final int TimePeriod=144;
    public static final int DECIMAL=145;
    public static final int ACCUMULATE=146;
    public static final int COLLECT=147;
    public static final int DOUBLE_PIPE=148;
    public static final int DOUBLE_AMPER=149;
    public static final int ARROW=150;
    public static final int EQUALS=151;
    public static final int GREATER=152;
    public static final int GREATER_EQUALS=153;
    public static final int LESS=154;
    public static final int LESS_EQUALS=155;
    public static final int NOT_EQUALS=156;
    public static final int LEFT_SQUARE=157;
    public static final int RIGHT_SQUARE=158;
    public static final int NULL=159;
    public static final int PLUS=160;
    public static final int MINUS=161;
    public static final int HEX=162;
    public static final int FLOAT=163;
    public static final int THEN=164;
    public static final int LEFT_CURLY=165;
    public static final int RIGHT_CURLY=166;
    public static final int QUESTION=167;
    public static final int PIPE=168;
    public static final int XOR=169;
    public static final int AMPER=170;
    public static final int SHIFT_LEFT=171;
    public static final int SHIFT_RIGHT_UNSIG=172;
    public static final int SHIFT_RIGHT=173;
    public static final int STAR=174;
    public static final int DIV=175;
    public static final int MOD=176;
    public static final int INCR=177;
    public static final int DECR=178;
    public static final int TILDE=179;
    public static final int NEGATION=180;
    public static final int PLUS_ASSIGN=181;
    public static final int MINUS_ASSIGN=182;
    public static final int MULT_ASSIGN=183;
    public static final int DIV_ASSIGN=184;
    public static final int AND_ASSIGN=185;
    public static final int OR_ASSIGN=186;
    public static final int XOR_ASSIGN=187;
    public static final int MOD_ASSIGN=188;
    public static final int EOL=189;
    public static final int WS=190;
    public static final int Exponent=191;
    public static final int FloatTypeSuffix=192;
    public static final int HexDigit=193;
    public static final int IntegerTypeSuffix=194;
    public static final int EscapeSequence=195;
    public static final int UnicodeEscape=196;
    public static final int OctalEscape=197;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=198;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=199;
    public static final int MULTI_LINE_COMMENT=200;
    public static final int IdentifierStart=201;
    public static final int IdentifierPart=202;
    public static final int MISC=203;

    // delegates
    // delegators


        public DescrBuilderTree(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public DescrBuilderTree(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return DescrBuilderTree.tokenNames; }
    public String getGrammarFileName() { return "src/main/resources/org/drools/lang/DescrBuilderTree.g"; }


    	DescrFactory factory = new DescrFactory();
    	PackageDescr packageDescr = null;
    	
    	public PackageDescr getPackageDescr() {
    		return packageDescr;
    	}



    // $ANTLR start "compilation_unit"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:28:1: compilation_unit : ^( VT_COMPILATION_UNIT package_statement ( statement )* ) ;
    public final void compilation_unit() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:29:2: ( ^( VT_COMPILATION_UNIT package_statement ( statement )* ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:29:4: ^( VT_COMPILATION_UNIT package_statement ( statement )* )
            {
            match(input,VT_COMPILATION_UNIT,FOLLOW_VT_COMPILATION_UNIT_in_compilation_unit49); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                pushFollow(FOLLOW_package_statement_in_compilation_unit51);
                package_statement();

                state._fsp--;

                // src/main/resources/org/drools/lang/DescrBuilderTree.g:29:44: ( statement )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==VT_FUNCTION_IMPORT||(LA1_0>=VK_DATE_EFFECTIVE && LA1_0<=VK_ENABLED)||LA1_0==VK_RULE||LA1_0==VK_IMPORT||(LA1_0>=VK_QUERY && LA1_0<=VK_GLOBAL)) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:29:44: statement
                	    {
                	    pushFollow(FOLLOW_statement_in_compilation_unit53);
                	    statement();

                	    state._fsp--;


                	    }
                	    break;

                	default :
                	    break loop1;
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:32:1: package_statement returns [String packageName] : ( ^( VK_PACKAGE packageId= package_id ) | );
    public final String package_statement() throws RecognitionException {
        String packageName = null;

        List packageId = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:33:2: ( ^( VK_PACKAGE packageId= package_id ) | )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==VK_PACKAGE) ) {
                alt2=1;
            }
            else if ( (LA2_0==UP||LA2_0==VT_FUNCTION_IMPORT||(LA2_0>=VK_DATE_EFFECTIVE && LA2_0<=VK_ENABLED)||LA2_0==VK_RULE||LA2_0==VK_IMPORT||(LA2_0>=VK_QUERY && LA2_0<=VK_GLOBAL)) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:33:4: ^( VK_PACKAGE packageId= package_id )
                    {
                    match(input,VK_PACKAGE,FOLLOW_VK_PACKAGE_in_package_statement71); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_package_id_in_package_statement75);
                    packageId=package_id();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	this.packageDescr = factory.createPackage(packageId);	
                    		packageName = packageDescr.getName();	

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:37:2: 
                    {
                    	this.packageDescr = factory.createPackage(null);	
                    		packageName = "";	

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
        return packageName;
    }
    // $ANTLR end "package_statement"


    // $ANTLR start "package_id"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:41:1: package_id returns [List idList] : ^( VT_PACKAGE_ID (tempList+= ID )+ ) ;
    public final List package_id() throws RecognitionException {
        List idList = null;

        DroolsTree tempList=null;
        List list_tempList=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:42:2: ( ^( VT_PACKAGE_ID (tempList+= ID )+ ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:42:4: ^( VT_PACKAGE_ID (tempList+= ID )+ )
            {
            match(input,VT_PACKAGE_ID,FOLLOW_VT_PACKAGE_ID_in_package_id102); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:42:28: (tempList+= ID )+
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
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:42:28: tempList+= ID
            	    {
            	    tempList=(DroolsTree)match(input,ID,FOLLOW_ID_in_package_id106); 
            	    if (list_tempList==null) list_tempList=new ArrayList();
            	    list_tempList.add(tempList);


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
            	idList = list_tempList;	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return idList;
    }
    // $ANTLR end "package_id"


    // $ANTLR start "statement"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:46:1: statement : (a= rule_attribute | fi= function_import_statement | is= import_statement | gl= global | fn= function | rl= rule | qr= query | td= type_declaration );
    public final void statement() throws RecognitionException {
        AttributeDescr a = null;

        FunctionImportDescr fi = null;

        ImportDescr is = null;

        DescrBuilderTree.global_return gl = null;

        DescrBuilderTree.function_return fn = null;

        DescrBuilderTree.rule_return rl = null;

        DescrBuilderTree.query_return qr = null;

        TypeDeclarationDescr td = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:47:2: (a= rule_attribute | fi= function_import_statement | is= import_statement | gl= global | fn= function | rl= rule | qr= query | td= type_declaration )
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
            case VK_TIMER:
            case VK_CALENDARS:
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
            case VK_RULE:
                {
                alt4=6;
                }
                break;
            case VK_QUERY:
                {
                alt4=7;
                }
                break;
            case VK_DECLARE:
                {
                alt4=8;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:47:4: a= rule_attribute
                    {
                    pushFollow(FOLLOW_rule_attribute_in_statement124);
                    a=rule_attribute();

                    state._fsp--;

                    	this.packageDescr.addAttribute(a);	

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:49:4: fi= function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement134);
                    fi=function_import_statement();

                    state._fsp--;

                    	this.packageDescr.addFunctionImport(fi);	

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:51:4: is= import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement144);
                    is=import_statement();

                    state._fsp--;

                    	this.packageDescr.addImport(is);	

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:53:4: gl= global
                    {
                    pushFollow(FOLLOW_global_in_statement155);
                    gl=global();

                    state._fsp--;

                    	this.packageDescr.addGlobal((gl!=null?gl.globalDescr:null));	

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:55:4: fn= function
                    {
                    pushFollow(FOLLOW_function_in_statement165);
                    fn=function();

                    state._fsp--;

                    	this.packageDescr.addFunction((fn!=null?fn.functionDescr:null));	

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:57:4: rl= rule
                    {
                    pushFollow(FOLLOW_rule_in_statement175);
                    rl=rule();

                    state._fsp--;

                    	this.packageDescr.addRule((rl!=null?rl.ruleDescr:null));	

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:59:4: qr= query
                    {
                    pushFollow(FOLLOW_query_in_statement185);
                    qr=query();

                    state._fsp--;

                    	this.packageDescr.addRule((qr!=null?qr.queryDescr:null));	

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:61:4: td= type_declaration
                    {
                    pushFollow(FOLLOW_type_declaration_in_statement195);
                    td=type_declaration();

                    state._fsp--;

                    	this.packageDescr.addTypeDeclaration(td);	

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:65:1: import_statement returns [ImportDescr importDescr] : ^(importStart= VK_IMPORT importId= import_name ) ;
    public final ImportDescr import_statement() throws RecognitionException {
        ImportDescr importDescr = null;

        DroolsTree importStart=null;
        DescrBuilderTree.import_name_return importId = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:66:2: ( ^(importStart= VK_IMPORT importId= import_name ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:66:4: ^(importStart= VK_IMPORT importId= import_name )
            {
            importStart=(DroolsTree)match(input,VK_IMPORT,FOLLOW_VK_IMPORT_in_import_statement216); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_import_name_in_import_statement220);
            importId=import_name();

            state._fsp--;


            match(input, Token.UP, null); 
            	importDescr = factory.createImport(importStart, (importId!=null?importId.idList:null), (importId!=null?importId.dotStar:null));	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return importDescr;
    }
    // $ANTLR end "import_statement"


    // $ANTLR start "function_import_statement"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:70:1: function_import_statement returns [FunctionImportDescr functionImportDescr] : ^(importStart= VT_FUNCTION_IMPORT VK_FUNCTION importId= import_name ) ;
    public final FunctionImportDescr function_import_statement() throws RecognitionException {
        FunctionImportDescr functionImportDescr = null;

        DroolsTree importStart=null;
        DescrBuilderTree.import_name_return importId = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:71:2: ( ^(importStart= VT_FUNCTION_IMPORT VK_FUNCTION importId= import_name ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:71:4: ^(importStart= VT_FUNCTION_IMPORT VK_FUNCTION importId= import_name )
            {
            importStart=(DroolsTree)match(input,VT_FUNCTION_IMPORT,FOLLOW_VT_FUNCTION_IMPORT_in_function_import_statement242); 

            match(input, Token.DOWN, null); 
            match(input,VK_FUNCTION,FOLLOW_VK_FUNCTION_in_function_import_statement244); 
            pushFollow(FOLLOW_import_name_in_function_import_statement248);
            importId=import_name();

            state._fsp--;


            match(input, Token.UP, null); 
            	functionImportDescr = factory.createFunctionImport(importStart, (importId!=null?importId.idList:null), (importId!=null?importId.dotStar:null));	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return functionImportDescr;
    }
    // $ANTLR end "function_import_statement"

    public static class import_name_return extends TreeRuleReturnScope {
        public List idList;
        public DroolsTree dotStar;
    };

    // $ANTLR start "import_name"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:75:1: import_name returns [List idList, DroolsTree dotStar] : ^( VT_IMPORT_ID (tempList+= ID )+ (tempDotStar= DOT_STAR )? ) ;
    public final DescrBuilderTree.import_name_return import_name() throws RecognitionException {
        DescrBuilderTree.import_name_return retval = new DescrBuilderTree.import_name_return();
        retval.start = input.LT(1);

        DroolsTree tempDotStar=null;
        DroolsTree tempList=null;
        List list_tempList=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:76:2: ( ^( VT_IMPORT_ID (tempList+= ID )+ (tempDotStar= DOT_STAR )? ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:76:4: ^( VT_IMPORT_ID (tempList+= ID )+ (tempDotStar= DOT_STAR )? )
            {
            match(input,VT_IMPORT_ID,FOLLOW_VT_IMPORT_ID_in_import_name267); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:76:27: (tempList+= ID )+
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
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:76:27: tempList+= ID
            	    {
            	    tempList=(DroolsTree)match(input,ID,FOLLOW_ID_in_import_name271); 
            	    if (list_tempList==null) list_tempList=new ArrayList();
            	    list_tempList.add(tempList);


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

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:76:44: (tempDotStar= DOT_STAR )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==DOT_STAR) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:76:44: tempDotStar= DOT_STAR
                    {
                    tempDotStar=(DroolsTree)match(input,DOT_STAR,FOLLOW_DOT_STAR_in_import_name276); 

                    }
                    break;

            }


            match(input, Token.UP, null); 
            	retval.idList = list_tempList;
            		retval.dotStar = tempDotStar;	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "import_name"

    public static class global_return extends TreeRuleReturnScope {
        public GlobalDescr globalDescr;
    };

    // $ANTLR start "global"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:81:1: global returns [GlobalDescr globalDescr] : ^(start= VK_GLOBAL dt= data_type globalId= VT_GLOBAL_ID ) ;
    public final DescrBuilderTree.global_return global() throws RecognitionException {
        DescrBuilderTree.global_return retval = new DescrBuilderTree.global_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree globalId=null;
        BaseDescr dt = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:82:2: ( ^(start= VK_GLOBAL dt= data_type globalId= VT_GLOBAL_ID ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:82:4: ^(start= VK_GLOBAL dt= data_type globalId= VT_GLOBAL_ID )
            {
            start=(DroolsTree)match(input,VK_GLOBAL,FOLLOW_VK_GLOBAL_in_global299); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_data_type_in_global303);
            dt=data_type();

            state._fsp--;

            globalId=(DroolsTree)match(input,VT_GLOBAL_ID,FOLLOW_VT_GLOBAL_ID_in_global307); 

            match(input, Token.UP, null); 
            	retval.globalDescr = factory.createGlobal(start,dt, globalId);	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "global"

    public static class function_return extends TreeRuleReturnScope {
        public FunctionDescr functionDescr;
    };

    // $ANTLR start "function"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:86:1: function returns [FunctionDescr functionDescr] : ^(start= VK_FUNCTION (dt= data_type )? functionId= VT_FUNCTION_ID params= parameters content= VT_CURLY_CHUNK ) ;
    public final DescrBuilderTree.function_return function() throws RecognitionException {
        DescrBuilderTree.function_return retval = new DescrBuilderTree.function_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree functionId=null;
        DroolsTree content=null;
        BaseDescr dt = null;

        List params = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:87:2: ( ^(start= VK_FUNCTION (dt= data_type )? functionId= VT_FUNCTION_ID params= parameters content= VT_CURLY_CHUNK ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:87:4: ^(start= VK_FUNCTION (dt= data_type )? functionId= VT_FUNCTION_ID params= parameters content= VT_CURLY_CHUNK )
            {
            start=(DroolsTree)match(input,VK_FUNCTION,FOLLOW_VK_FUNCTION_in_function329); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:87:26: (dt= data_type )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==VT_DATA_TYPE) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:87:26: dt= data_type
                    {
                    pushFollow(FOLLOW_data_type_in_function333);
                    dt=data_type();

                    state._fsp--;


                    }
                    break;

            }

            functionId=(DroolsTree)match(input,VT_FUNCTION_ID,FOLLOW_VT_FUNCTION_ID_in_function338); 
            pushFollow(FOLLOW_parameters_in_function342);
            params=parameters();

            state._fsp--;

            content=(DroolsTree)match(input,VT_CURLY_CHUNK,FOLLOW_VT_CURLY_CHUNK_in_function346); 

            match(input, Token.UP, null); 
            	retval.functionDescr = factory.createFunction(start, dt, functionId, params, content);	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "function"

    public static class query_return extends TreeRuleReturnScope {
        public QueryDescr queryDescr;
    };

    // $ANTLR start "query"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:91:1: query returns [QueryDescr queryDescr] : ^(start= VK_QUERY id= VT_QUERY_ID (params= parameters )? lb= lhs_block end= VK_END ) ;
    public final DescrBuilderTree.query_return query() throws RecognitionException {
        DescrBuilderTree.query_return retval = new DescrBuilderTree.query_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree id=null;
        DroolsTree end=null;
        List params = null;

        AndDescr lb = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:92:2: ( ^(start= VK_QUERY id= VT_QUERY_ID (params= parameters )? lb= lhs_block end= VK_END ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:92:4: ^(start= VK_QUERY id= VT_QUERY_ID (params= parameters )? lb= lhs_block end= VK_END )
            {
            start=(DroolsTree)match(input,VK_QUERY,FOLLOW_VK_QUERY_in_query368); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,VT_QUERY_ID,FOLLOW_VT_QUERY_ID_in_query372); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:92:42: (params= parameters )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==VT_PARAM_LIST) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:92:42: params= parameters
                    {
                    pushFollow(FOLLOW_parameters_in_query376);
                    params=parameters();

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_lhs_block_in_query381);
            lb=lhs_block();

            state._fsp--;

            end=(DroolsTree)match(input,VK_END,FOLLOW_VK_END_in_query385); 

            match(input, Token.UP, null); 
            	retval.queryDescr = factory.createQuery(start, id, params, lb, end);	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "query"

    public static class rule_return extends TreeRuleReturnScope {
        public RuleDescr ruleDescr;
    };

    // $ANTLR start "rule"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:96:1: rule returns [RuleDescr ruleDescr] : ^(start= VK_RULE id= VT_RULE_ID ( ^( VK_EXTEND parent_id= VT_RULE_ID ) )? (dm= decl_metadata )* (ra= rule_attributes )? (wn= when_part )? content= VT_RHS_CHUNK ) ;
    public final DescrBuilderTree.rule_return rule() throws RecognitionException {
        DescrBuilderTree.rule_return retval = new DescrBuilderTree.rule_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree id=null;
        DroolsTree parent_id=null;
        DroolsTree content=null;
        Map dm = null;

        List ra = null;

        AndDescr wn = null;


        	List<Map> declMetadaList = new LinkedList<Map>();
        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:98:2: ( ^(start= VK_RULE id= VT_RULE_ID ( ^( VK_EXTEND parent_id= VT_RULE_ID ) )? (dm= decl_metadata )* (ra= rule_attributes )? (wn= when_part )? content= VT_RHS_CHUNK ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:98:4: ^(start= VK_RULE id= VT_RULE_ID ( ^( VK_EXTEND parent_id= VT_RULE_ID ) )? (dm= decl_metadata )* (ra= rule_attributes )? (wn= when_part )? content= VT_RHS_CHUNK )
            {
            start=(DroolsTree)match(input,VK_RULE,FOLLOW_VK_RULE_in_rule412); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,VT_RULE_ID,FOLLOW_VT_RULE_ID_in_rule416); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:98:35: ( ^( VK_EXTEND parent_id= VT_RULE_ID ) )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==VK_EXTEND) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:98:36: ^( VK_EXTEND parent_id= VT_RULE_ID )
                    {
                    match(input,VK_EXTEND,FOLLOW_VK_EXTEND_in_rule421); 

                    match(input, Token.DOWN, null); 
                    parent_id=(DroolsTree)match(input,VT_RULE_ID,FOLLOW_VT_RULE_ID_in_rule425); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:99:3: (dm= decl_metadata )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==AT) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:99:4: dm= decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_rule435);
            	    dm=decl_metadata();

            	    state._fsp--;

            	    declMetadaList.add(dm);	

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:100:6: (ra= rule_attributes )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==VT_RULE_ATTRIBUTES) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:100:6: ra= rule_attributes
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule446);
                    ra=rule_attributes();

                    state._fsp--;


                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:101:6: (wn= when_part )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==WHEN) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:101:6: wn= when_part
                    {
                    pushFollow(FOLLOW_when_part_in_rule455);
                    wn=when_part();

                    state._fsp--;


                    }
                    break;

            }

            content=(DroolsTree)match(input,VT_RHS_CHUNK,FOLLOW_VT_RHS_CHUNK_in_rule460); 

            match(input, Token.UP, null); 
            	retval.ruleDescr = factory.createRule(start, id, parent_id, ra, wn, content, declMetadaList);	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rule"


    // $ANTLR start "when_part"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:105:1: when_part returns [AndDescr andDescr] : WHEN lh= lhs_block ;
    public final AndDescr when_part() throws RecognitionException {
        AndDescr andDescr = null;

        AndDescr lh = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:106:2: ( WHEN lh= lhs_block )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:106:4: WHEN lh= lhs_block
            {
            match(input,WHEN,FOLLOW_WHEN_in_when_part479); 
            pushFollow(FOLLOW_lhs_block_in_when_part483);
            lh=lhs_block();

            state._fsp--;

            	andDescr = lh;	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return andDescr;
    }
    // $ANTLR end "when_part"


    // $ANTLR start "rule_attributes"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:110:1: rule_attributes returns [List attrList] : ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? (rl= rule_attribute )+ ) ;
    public final List rule_attributes() throws RecognitionException {
        List attrList = null;

        AttributeDescr rl = null;



        	attrList = new LinkedList<AttributeDescr>();

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:113:3: ( ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? (rl= rule_attribute )+ ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:113:5: ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? (rl= rule_attribute )+ )
            {
            match(input,VT_RULE_ATTRIBUTES,FOLLOW_VT_RULE_ATTRIBUTES_in_rule_attributes505); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:113:26: ( VK_ATTRIBUTES )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==VK_ATTRIBUTES) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:113:26: VK_ATTRIBUTES
                    {
                    match(input,VK_ATTRIBUTES,FOLLOW_VK_ATTRIBUTES_in_rule_attributes507); 

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:113:41: (rl= rule_attribute )+
            int cnt14=0;
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( ((LA14_0>=VK_DATE_EFFECTIVE && LA14_0<=VK_ENABLED)) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:113:42: rl= rule_attribute
            	    {
            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes513);
            	    rl=rule_attribute();

            	    state._fsp--;

            	    attrList.add(rl);

            	    }
            	    break;

            	default :
            	    if ( cnt14 >= 1 ) break loop14;
                        EarlyExitException eee =
                            new EarlyExitException(14, input);
                        throw eee;
                }
                cnt14++;
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
        return attrList;
    }
    // $ANTLR end "rule_attributes"


    // $ANTLR start "parameters"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:116:1: parameters returns [List paramList] : ^( VT_PARAM_LIST (p= param_definition )* ) ;
    public final List parameters() throws RecognitionException {
        List paramList = null;

        Map p = null;



        	paramList = new LinkedList<Map<BaseDescr, BaseDescr>>();

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:119:3: ( ^( VT_PARAM_LIST (p= param_definition )* ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:119:5: ^( VT_PARAM_LIST (p= param_definition )* )
            {
            match(input,VT_PARAM_LIST,FOLLOW_VT_PARAM_LIST_in_parameters537); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // src/main/resources/org/drools/lang/DescrBuilderTree.g:119:21: (p= param_definition )*
                loop15:
                do {
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==VT_DATA_TYPE||LA15_0==ID) ) {
                        alt15=1;
                    }


                    switch (alt15) {
                	case 1 :
                	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:119:22: p= param_definition
                	    {
                	    pushFollow(FOLLOW_param_definition_in_parameters542);
                	    p=param_definition();

                	    state._fsp--;

                	    paramList.add(p);

                	    }
                	    break;

                	default :
                	    break loop15;
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
        return paramList;
    }
    // $ANTLR end "parameters"


    // $ANTLR start "param_definition"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:122:1: param_definition returns [Map param] : (dt= data_type )? a= argument ;
    public final Map param_definition() throws RecognitionException {
        Map param = null;

        BaseDescr dt = null;

        BaseDescr a = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:123:2: ( (dt= data_type )? a= argument )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:123:4: (dt= data_type )? a= argument
            {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:123:6: (dt= data_type )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==VT_DATA_TYPE) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:123:6: dt= data_type
                    {
                    pushFollow(FOLLOW_data_type_in_param_definition564);
                    dt=data_type();

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_argument_in_param_definition569);
            a=argument();

            state._fsp--;

            	param = new HashMap<BaseDescr, BaseDescr>();
            		param.put(a, dt);	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return param;
    }
    // $ANTLR end "param_definition"


    // $ANTLR start "argument"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:128:1: argument returns [BaseDescr arg] : id= ID ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ;
    public final BaseDescr argument() throws RecognitionException {
        BaseDescr arg = null;

        DroolsTree id=null;
        DroolsTree rightList=null;
        List list_rightList=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:129:2: (id= ID ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:129:4: id= ID ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            {
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_argument589); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:129:10: ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==LEFT_SQUARE) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:129:11: LEFT_SQUARE rightList+= RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_argument592); 
            	    rightList=(DroolsTree)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_argument596); 
            	    if (list_rightList==null) list_rightList=new ArrayList();
            	    list_rightList.add(rightList);


            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);

            	arg = factory.createArgument(id, list_rightList);	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return arg;
    }
    // $ANTLR end "argument"


    // $ANTLR start "type_declaration"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:133:1: type_declaration returns [TypeDeclarationDescr declaration] : ^( VK_DECLARE id= VT_TYPE_DECLARE_ID ( ^( VK_EXTENDS ext= VT_TYPE_NAME ) )? ( ^( VK_IMPLEMENTS (intf= VT_TYPE_NAME )+ ) )? (dm= decl_metadata )* (df= decl_field )* VK_END ) ;
    public final TypeDeclarationDescr type_declaration() throws RecognitionException {
        TypeDeclarationDescr declaration = null;

        DroolsTree id=null;
        DroolsTree ext=null;
        DroolsTree intf=null;
        Map dm = null;

        TypeFieldDescr df = null;


        	List<Map> declMetadaList = new LinkedList<Map>();
        		List<TypeFieldDescr> declFieldList = new LinkedList<TypeFieldDescr>(); 
        		List<String> interfaces = new LinkedList<String>(); 
        		
        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:138:2: ( ^( VK_DECLARE id= VT_TYPE_DECLARE_ID ( ^( VK_EXTENDS ext= VT_TYPE_NAME ) )? ( ^( VK_IMPLEMENTS (intf= VT_TYPE_NAME )+ ) )? (dm= decl_metadata )* (df= decl_field )* VK_END ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:138:4: ^( VK_DECLARE id= VT_TYPE_DECLARE_ID ( ^( VK_EXTENDS ext= VT_TYPE_NAME ) )? ( ^( VK_IMPLEMENTS (intf= VT_TYPE_NAME )+ ) )? (dm= decl_metadata )* (df= decl_field )* VK_END )
            {
            match(input,VK_DECLARE,FOLLOW_VK_DECLARE_in_type_declaration623); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,VT_TYPE_DECLARE_ID,FOLLOW_VT_TYPE_DECLARE_ID_in_type_declaration627); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:139:4: ( ^( VK_EXTENDS ext= VT_TYPE_NAME ) )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==VK_EXTENDS) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:139:10: ^( VK_EXTENDS ext= VT_TYPE_NAME )
                    {
                    match(input,VK_EXTENDS,FOLLOW_VK_EXTENDS_in_type_declaration640); 

                    match(input, Token.DOWN, null); 
                    ext=(DroolsTree)match(input,VT_TYPE_NAME,FOLLOW_VT_TYPE_NAME_in_type_declaration644); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:140:4: ( ^( VK_IMPLEMENTS (intf= VT_TYPE_NAME )+ ) )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==VK_IMPLEMENTS) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:140:10: ^( VK_IMPLEMENTS (intf= VT_TYPE_NAME )+ )
                    {
                    match(input,VK_IMPLEMENTS,FOLLOW_VK_IMPLEMENTS_in_type_declaration663); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:140:26: (intf= VT_TYPE_NAME )+
                    int cnt19=0;
                    loop19:
                    do {
                        int alt19=2;
                        int LA19_0 = input.LA(1);

                        if ( (LA19_0==VT_TYPE_NAME) ) {
                            alt19=1;
                        }


                        switch (alt19) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:140:27: intf= VT_TYPE_NAME
                    	    {
                    	    intf=(DroolsTree)match(input,VT_TYPE_NAME,FOLLOW_VT_TYPE_NAME_in_type_declaration668); 
                    	    interfaces.add((intf!=null?intf.getText():null));

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt19 >= 1 ) break loop19;
                                EarlyExitException eee =
                                    new EarlyExitException(19, input);
                                throw eee;
                        }
                        cnt19++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:141:4: (dm= decl_metadata )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==AT) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:141:5: dm= decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_type_declaration688);
            	    dm=decl_metadata();

            	    state._fsp--;

            	    declMetadaList.add(dm);	

            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:142:4: (df= decl_field )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==ID) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:142:5: df= decl_field
            	    {
            	    pushFollow(FOLLOW_decl_field_in_type_declaration701);
            	    df=decl_field();

            	    state._fsp--;

            	    declFieldList.add(df);	

            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);

            match(input,VK_END,FOLLOW_VK_END_in_type_declaration715); 

            match(input, Token.UP, null); 
            	declaration = factory.createTypeDeclr(id, (ext!=null?ext.getText():null), interfaces, declMetadaList, declFieldList);	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return declaration;
    }
    // $ANTLR end "type_declaration"


    // $ANTLR start "decl_metadata"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:154:1: decl_metadata returns [Map attData] : ^( AT att= VT_TYPE_NAME (p= decl_metadata_properties )? ) ;
    public final Map decl_metadata() throws RecognitionException {
        Map attData = null;

        DroolsTree att=null;
        Hashtable p = null;


        attData = new HashMap();
        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:156:2: ( ^( AT att= VT_TYPE_NAME (p= decl_metadata_properties )? ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:156:4: ^( AT att= VT_TYPE_NAME (p= decl_metadata_properties )? )
            {
            match(input,AT,FOLLOW_AT_in_decl_metadata747); 

            match(input, Token.DOWN, null); 
            att=(DroolsTree)match(input,VT_TYPE_NAME,FOLLOW_VT_TYPE_NAME_in_decl_metadata751); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:156:26: (p= decl_metadata_properties )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==VT_PROP_KEY) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:156:27: p= decl_metadata_properties
                    {
                    pushFollow(FOLLOW_decl_metadata_properties_in_decl_metadata756);
                    p=decl_metadata_properties();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 
            	attData.put((att!=null?att.getText():null), p);	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return attData;
    }
    // $ANTLR end "decl_metadata"


    // $ANTLR start "decl_metadata_properties"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:160:1: decl_metadata_properties returns [Hashtable props] : ( ^(key= VT_PROP_KEY (val= VT_PROP_VALUE )? ) )+ ;
    public final Hashtable decl_metadata_properties() throws RecognitionException {
        Hashtable props = null;

        DroolsTree key=null;
        DroolsTree val=null;

        props = new Hashtable();
        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:162:2: ( ( ^(key= VT_PROP_KEY (val= VT_PROP_VALUE )? ) )+ )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:162:4: ( ^(key= VT_PROP_KEY (val= VT_PROP_VALUE )? ) )+
            {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:162:4: ( ^(key= VT_PROP_KEY (val= VT_PROP_VALUE )? ) )+
            int cnt25=0;
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==VT_PROP_KEY) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:163:4: ^(key= VT_PROP_KEY (val= VT_PROP_VALUE )? )
            	    {
            	    key=(DroolsTree)match(input,VT_PROP_KEY,FOLLOW_VT_PROP_KEY_in_decl_metadata_properties791); 

            	    if ( input.LA(1)==Token.DOWN ) {
            	        match(input, Token.DOWN, null); 
            	        // src/main/resources/org/drools/lang/DescrBuilderTree.g:163:22: (val= VT_PROP_VALUE )?
            	        int alt24=2;
            	        int LA24_0 = input.LA(1);

            	        if ( (LA24_0==VT_PROP_VALUE) ) {
            	            alt24=1;
            	        }
            	        switch (alt24) {
            	            case 1 :
            	                // src/main/resources/org/drools/lang/DescrBuilderTree.g:163:23: val= VT_PROP_VALUE
            	                {
            	                val=(DroolsTree)match(input,VT_PROP_VALUE,FOLLOW_VT_PROP_VALUE_in_decl_metadata_properties796); 

            	                }
            	                break;

            	        }


            	        match(input, Token.UP, null); 
            	    }
            	     props.put((key!=null?key.getText():null),val == null ? (key!=null?key.getText():null) : (val!=null?val.getText():null) ); 

            	    }
            	    break;

            	default :
            	    if ( cnt25 >= 1 ) break loop25;
                        EarlyExitException eee =
                            new EarlyExitException(25, input);
                        throw eee;
                }
                cnt25++;
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return props;
    }
    // $ANTLR end "decl_metadata_properties"


    // $ANTLR start "decl_field"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:168:1: decl_field returns [TypeFieldDescr fieldDescr] : ^(id= ID (init= decl_field_initialization )? dt= data_type (dm= decl_metadata )* ) ;
    public final TypeFieldDescr decl_field() throws RecognitionException {
        TypeFieldDescr fieldDescr = null;

        DroolsTree id=null;
        String init = null;

        BaseDescr dt = null;

        Map dm = null;


        List<Map> declMetadaList = new LinkedList<Map>(); 
        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:170:2: ( ^(id= ID (init= decl_field_initialization )? dt= data_type (dm= decl_metadata )* ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:170:4: ^(id= ID (init= decl_field_initialization )? dt= data_type (dm= decl_metadata )* )
            {
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_decl_field833); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:170:16: (init= decl_field_initialization )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==EQUALS_ASSIGN) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:170:16: init= decl_field_initialization
                    {
                    pushFollow(FOLLOW_decl_field_initialization_in_decl_field837);
                    init=decl_field_initialization();

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_data_type_in_decl_field842);
            dt=data_type();

            state._fsp--;

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:170:57: (dm= decl_metadata )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==AT) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:170:58: dm= decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_decl_field847);
            	    dm=decl_metadata();

            	    state._fsp--;

            	    declMetadaList.add(dm);

            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);


            match(input, Token.UP, null); 
            	fieldDescr = factory.createTypeField(id, init, dt, declMetadaList);	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return fieldDescr;
    }
    // $ANTLR end "decl_field"


    // $ANTLR start "decl_field_initialization"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:174:1: decl_field_initialization returns [String expr] : ^( EQUALS_ASSIGN pc= VT_PAREN_CHUNK ) ;
    public final String decl_field_initialization() throws RecognitionException {
        String expr = null;

        DroolsTree pc=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:175:2: ( ^( EQUALS_ASSIGN pc= VT_PAREN_CHUNK ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:175:4: ^( EQUALS_ASSIGN pc= VT_PAREN_CHUNK )
            {
            match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_decl_field_initialization874); 

            match(input, Token.DOWN, null); 
            pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_decl_field_initialization878); 

            match(input, Token.UP, null); 
            	expr = (pc!=null?pc.getText():null).substring(1, (pc!=null?pc.getText():null).length() -1 ).trim();	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return expr;
    }
    // $ANTLR end "decl_field_initialization"


    // $ANTLR start "rule_attribute"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:179:1: rule_attribute returns [AttributeDescr attributeDescr] : ( ^(attrName= VK_SALIENCE (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_NO_LOOP (value= BOOL )? ) | ^(attrName= VK_AGENDA_GROUP value= STRING ) | ^(attrName= VK_TIMER (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_ACTIVATION_GROUP value= STRING ) | ^(attrName= VK_AUTO_FOCUS (value= BOOL )? ) | ^(attrName= VK_DATE_EFFECTIVE value= STRING ) | ^(attrName= VK_DATE_EXPIRES value= STRING ) | ^(attrName= VK_ENABLED (value= BOOL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_RULEFLOW_GROUP value= STRING ) | ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? ) | ^(attrName= VK_DIALECT value= STRING ) | ^(attrName= VK_CALENDARS value= STRING ) ) ;
    public final AttributeDescr rule_attribute() throws RecognitionException {
        AttributeDescr attributeDescr = null;

        DroolsTree attrName=null;
        DroolsTree value=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:180:2: ( ( ^(attrName= VK_SALIENCE (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_NO_LOOP (value= BOOL )? ) | ^(attrName= VK_AGENDA_GROUP value= STRING ) | ^(attrName= VK_TIMER (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_ACTIVATION_GROUP value= STRING ) | ^(attrName= VK_AUTO_FOCUS (value= BOOL )? ) | ^(attrName= VK_DATE_EFFECTIVE value= STRING ) | ^(attrName= VK_DATE_EXPIRES value= STRING ) | ^(attrName= VK_ENABLED (value= BOOL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_RULEFLOW_GROUP value= STRING ) | ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? ) | ^(attrName= VK_DIALECT value= STRING ) | ^(attrName= VK_CALENDARS value= STRING ) ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:180:4: ( ^(attrName= VK_SALIENCE (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_NO_LOOP (value= BOOL )? ) | ^(attrName= VK_AGENDA_GROUP value= STRING ) | ^(attrName= VK_TIMER (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_ACTIVATION_GROUP value= STRING ) | ^(attrName= VK_AUTO_FOCUS (value= BOOL )? ) | ^(attrName= VK_DATE_EFFECTIVE value= STRING ) | ^(attrName= VK_DATE_EXPIRES value= STRING ) | ^(attrName= VK_ENABLED (value= BOOL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_RULEFLOW_GROUP value= STRING ) | ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? ) | ^(attrName= VK_DIALECT value= STRING ) | ^(attrName= VK_CALENDARS value= STRING ) )
            {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:180:4: ( ^(attrName= VK_SALIENCE (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_NO_LOOP (value= BOOL )? ) | ^(attrName= VK_AGENDA_GROUP value= STRING ) | ^(attrName= VK_TIMER (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_ACTIVATION_GROUP value= STRING ) | ^(attrName= VK_AUTO_FOCUS (value= BOOL )? ) | ^(attrName= VK_DATE_EFFECTIVE value= STRING ) | ^(attrName= VK_DATE_EXPIRES value= STRING ) | ^(attrName= VK_ENABLED (value= BOOL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_RULEFLOW_GROUP value= STRING ) | ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? ) | ^(attrName= VK_DIALECT value= STRING ) | ^(attrName= VK_CALENDARS value= STRING ) )
            int alt34=13;
            switch ( input.LA(1) ) {
            case VK_SALIENCE:
                {
                alt34=1;
                }
                break;
            case VK_NO_LOOP:
                {
                alt34=2;
                }
                break;
            case VK_AGENDA_GROUP:
                {
                alt34=3;
                }
                break;
            case VK_TIMER:
                {
                alt34=4;
                }
                break;
            case VK_ACTIVATION_GROUP:
                {
                alt34=5;
                }
                break;
            case VK_AUTO_FOCUS:
                {
                alt34=6;
                }
                break;
            case VK_DATE_EFFECTIVE:
                {
                alt34=7;
                }
                break;
            case VK_DATE_EXPIRES:
                {
                alt34=8;
                }
                break;
            case VK_ENABLED:
                {
                alt34=9;
                }
                break;
            case VK_RULEFLOW_GROUP:
                {
                alt34=10;
                }
                break;
            case VK_LOCK_ON_ACTIVE:
                {
                alt34=11;
                }
                break;
            case VK_DIALECT:
                {
                alt34=12;
                }
                break;
            case VK_CALENDARS:
                {
                alt34=13;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;
            }

            switch (alt34) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:180:5: ^(attrName= VK_SALIENCE (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) )
                    {
                    attrName=(DroolsTree)match(input,VK_SALIENCE,FOLLOW_VK_SALIENCE_in_rule_attribute901); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:180:28: (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK )
                    int alt28=2;
                    int LA28_0 = input.LA(1);

                    if ( (LA28_0==SIGNED_DECIMAL) ) {
                        alt28=1;
                    }
                    else if ( (LA28_0==VT_PAREN_CHUNK) ) {
                        alt28=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 28, 0, input);

                        throw nvae;
                    }
                    switch (alt28) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:180:29: value= SIGNED_DECIMAL
                            {
                            value=(DroolsTree)match(input,SIGNED_DECIMAL,FOLLOW_SIGNED_DECIMAL_in_rule_attribute906); 

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:180:50: value= VT_PAREN_CHUNK
                            {
                            value=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_rule_attribute910); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:181:4: ^(attrName= VK_NO_LOOP (value= BOOL )? )
                    {
                    attrName=(DroolsTree)match(input,VK_NO_LOOP,FOLLOW_VK_NO_LOOP_in_rule_attribute921); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // src/main/resources/org/drools/lang/DescrBuilderTree.g:181:31: (value= BOOL )?
                        int alt29=2;
                        int LA29_0 = input.LA(1);

                        if ( (LA29_0==BOOL) ) {
                            alt29=1;
                        }
                        switch (alt29) {
                            case 1 :
                                // src/main/resources/org/drools/lang/DescrBuilderTree.g:181:31: value= BOOL
                                {
                                value=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_rule_attribute925); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:182:4: ^(attrName= VK_AGENDA_GROUP value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_AGENDA_GROUP,FOLLOW_VK_AGENDA_GROUP_in_rule_attribute937); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute941); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:183:4: ^(attrName= VK_TIMER (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) )
                    {
                    attrName=(DroolsTree)match(input,VK_TIMER,FOLLOW_VK_TIMER_in_rule_attribute952); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:183:24: (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK )
                    int alt30=2;
                    int LA30_0 = input.LA(1);

                    if ( (LA30_0==SIGNED_DECIMAL) ) {
                        alt30=1;
                    }
                    else if ( (LA30_0==VT_PAREN_CHUNK) ) {
                        alt30=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 30, 0, input);

                        throw nvae;
                    }
                    switch (alt30) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:183:25: value= SIGNED_DECIMAL
                            {
                            value=(DroolsTree)match(input,SIGNED_DECIMAL,FOLLOW_SIGNED_DECIMAL_in_rule_attribute957); 

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:183:46: value= VT_PAREN_CHUNK
                            {
                            value=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_rule_attribute961); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:184:4: ^(attrName= VK_ACTIVATION_GROUP value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_ACTIVATION_GROUP,FOLLOW_VK_ACTIVATION_GROUP_in_rule_attribute974); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute978); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:185:4: ^(attrName= VK_AUTO_FOCUS (value= BOOL )? )
                    {
                    attrName=(DroolsTree)match(input,VK_AUTO_FOCUS,FOLLOW_VK_AUTO_FOCUS_in_rule_attribute988); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // src/main/resources/org/drools/lang/DescrBuilderTree.g:185:34: (value= BOOL )?
                        int alt31=2;
                        int LA31_0 = input.LA(1);

                        if ( (LA31_0==BOOL) ) {
                            alt31=1;
                        }
                        switch (alt31) {
                            case 1 :
                                // src/main/resources/org/drools/lang/DescrBuilderTree.g:185:34: value= BOOL
                                {
                                value=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_rule_attribute992); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:186:4: ^(attrName= VK_DATE_EFFECTIVE value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_DATE_EFFECTIVE,FOLLOW_VK_DATE_EFFECTIVE_in_rule_attribute1003); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute1007); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:187:4: ^(attrName= VK_DATE_EXPIRES value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_DATE_EXPIRES,FOLLOW_VK_DATE_EXPIRES_in_rule_attribute1017); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute1021); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:188:4: ^(attrName= VK_ENABLED (value= BOOL | value= VT_PAREN_CHUNK ) )
                    {
                    attrName=(DroolsTree)match(input,VK_ENABLED,FOLLOW_VK_ENABLED_in_rule_attribute1031); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:188:26: (value= BOOL | value= VT_PAREN_CHUNK )
                    int alt32=2;
                    int LA32_0 = input.LA(1);

                    if ( (LA32_0==BOOL) ) {
                        alt32=1;
                    }
                    else if ( (LA32_0==VT_PAREN_CHUNK) ) {
                        alt32=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 32, 0, input);

                        throw nvae;
                    }
                    switch (alt32) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:188:27: value= BOOL
                            {
                            value=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_rule_attribute1036); 

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:188:38: value= VT_PAREN_CHUNK
                            {
                            value=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_rule_attribute1040); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 10 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:189:4: ^(attrName= VK_RULEFLOW_GROUP value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_RULEFLOW_GROUP,FOLLOW_VK_RULEFLOW_GROUP_in_rule_attribute1051); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute1055); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 11 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:190:4: ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? )
                    {
                    attrName=(DroolsTree)match(input,VK_LOCK_ON_ACTIVE,FOLLOW_VK_LOCK_ON_ACTIVE_in_rule_attribute1065); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // src/main/resources/org/drools/lang/DescrBuilderTree.g:190:38: (value= BOOL )?
                        int alt33=2;
                        int LA33_0 = input.LA(1);

                        if ( (LA33_0==BOOL) ) {
                            alt33=1;
                        }
                        switch (alt33) {
                            case 1 :
                                // src/main/resources/org/drools/lang/DescrBuilderTree.g:190:38: value= BOOL
                                {
                                value=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_rule_attribute1069); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 12 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:191:4: ^(attrName= VK_DIALECT value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_DIALECT,FOLLOW_VK_DIALECT_in_rule_attribute1079); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute1083); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 13 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:192:4: ^(attrName= VK_CALENDARS value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_CALENDARS,FOLLOW_VK_CALENDARS_in_rule_attribute1092); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute1096); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }

            	attributeDescr = factory.createAttribute(attrName, value);	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return attributeDescr;
    }
    // $ANTLR end "rule_attribute"


    // $ANTLR start "lhs_block"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:196:1: lhs_block returns [AndDescr andDescr] : ^( VT_AND_IMPLICIT (dt= lhs )* ) ;
    public final AndDescr lhs_block() throws RecognitionException {
        AndDescr andDescr = null;

        DescrBuilderTree.lhs_return dt = null;



        	andDescr = new AndDescr();

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:199:3: ( ^( VT_AND_IMPLICIT (dt= lhs )* ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:199:5: ^( VT_AND_IMPLICIT (dt= lhs )* )
            {
            match(input,VT_AND_IMPLICIT,FOLLOW_VT_AND_IMPLICIT_in_lhs_block1121); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // src/main/resources/org/drools/lang/DescrBuilderTree.g:199:23: (dt= lhs )*
                loop35:
                do {
                    int alt35=2;
                    int LA35_0 = input.LA(1);

                    if ( ((LA35_0>=VT_AND_PREFIX && LA35_0<=VT_OR_INFIX)||LA35_0==VT_FOR_CE||LA35_0==VT_PATTERN||LA35_0==VK_EVAL||LA35_0==VK_NOT||(LA35_0>=VK_EXISTS && LA35_0<=VK_FORALL)||LA35_0==FROM) ) {
                        alt35=1;
                    }


                    switch (alt35) {
                	case 1 :
                	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:199:24: dt= lhs
                	    {
                	    pushFollow(FOLLOW_lhs_in_lhs_block1126);
                	    dt=lhs();

                	    state._fsp--;

                	    andDescr.addDescr((dt!=null?dt.baseDescr:null));

                	    }
                	    break;

                	default :
                	    break loop35;
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
        return andDescr;
    }
    // $ANTLR end "lhs_block"

    public static class lhs_return extends TreeRuleReturnScope {
        public BaseDescr baseDescr;
    };

    // $ANTLR start "lhs"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:202:1: lhs returns [BaseDescr baseDescr] : ( ^(start= VT_OR_PREFIX (dt= lhs )+ ) | ^(start= VT_OR_INFIX dt1= lhs dt2= lhs ) | ^(start= VT_AND_PREFIX (dt= lhs )+ ) | ^(start= VT_AND_INFIX dt1= lhs dt2= lhs ) | ^(start= VK_EXISTS dt= lhs ) | ^(start= VK_NOT dt= lhs ) | ^(start= VK_EVAL pc= VT_PAREN_CHUNK ) | ^(start= VK_FORALL (dt= lhs )+ ) | ^(start= VT_FOR_CE dt= lhs for_functions ( fact_expression )? ) | ^( FROM pn= lhs_pattern fe= from_elements ) | pn= lhs_pattern );
    public final DescrBuilderTree.lhs_return lhs() throws RecognitionException {
        DescrBuilderTree.lhs_return retval = new DescrBuilderTree.lhs_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree pc=null;
        DescrBuilderTree.lhs_return dt = null;

        DescrBuilderTree.lhs_return dt1 = null;

        DescrBuilderTree.lhs_return dt2 = null;

        BaseDescr pn = null;

        DescrBuilderTree.from_elements_return fe = null;



        	List<BaseDescr> lhsList = new LinkedList<BaseDescr>();

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:205:3: ( ^(start= VT_OR_PREFIX (dt= lhs )+ ) | ^(start= VT_OR_INFIX dt1= lhs dt2= lhs ) | ^(start= VT_AND_PREFIX (dt= lhs )+ ) | ^(start= VT_AND_INFIX dt1= lhs dt2= lhs ) | ^(start= VK_EXISTS dt= lhs ) | ^(start= VK_NOT dt= lhs ) | ^(start= VK_EVAL pc= VT_PAREN_CHUNK ) | ^(start= VK_FORALL (dt= lhs )+ ) | ^(start= VT_FOR_CE dt= lhs for_functions ( fact_expression )? ) | ^( FROM pn= lhs_pattern fe= from_elements ) | pn= lhs_pattern )
            int alt40=11;
            switch ( input.LA(1) ) {
            case VT_OR_PREFIX:
                {
                alt40=1;
                }
                break;
            case VT_OR_INFIX:
                {
                alt40=2;
                }
                break;
            case VT_AND_PREFIX:
                {
                alt40=3;
                }
                break;
            case VT_AND_INFIX:
                {
                alt40=4;
                }
                break;
            case VK_EXISTS:
                {
                alt40=5;
                }
                break;
            case VK_NOT:
                {
                alt40=6;
                }
                break;
            case VK_EVAL:
                {
                alt40=7;
                }
                break;
            case VK_FORALL:
                {
                alt40=8;
                }
                break;
            case VT_FOR_CE:
                {
                alt40=9;
                }
                break;
            case FROM:
                {
                alt40=10;
                }
                break;
            case VT_PATTERN:
                {
                alt40=11;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 40, 0, input);

                throw nvae;
            }

            switch (alt40) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:205:5: ^(start= VT_OR_PREFIX (dt= lhs )+ )
                    {
                    start=(DroolsTree)match(input,VT_OR_PREFIX,FOLLOW_VT_OR_PREFIX_in_lhs1152); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:205:26: (dt= lhs )+
                    int cnt36=0;
                    loop36:
                    do {
                        int alt36=2;
                        int LA36_0 = input.LA(1);

                        if ( ((LA36_0>=VT_AND_PREFIX && LA36_0<=VT_OR_INFIX)||LA36_0==VT_FOR_CE||LA36_0==VT_PATTERN||LA36_0==VK_EVAL||LA36_0==VK_NOT||(LA36_0>=VK_EXISTS && LA36_0<=VK_FORALL)||LA36_0==FROM) ) {
                            alt36=1;
                        }


                        switch (alt36) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:205:27: dt= lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs1157);
                    	    dt=lhs();

                    	    state._fsp--;

                    	    	lhsList.add((dt!=null?dt.baseDescr:null));	

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
                    	retval.baseDescr = factory.createOr(start, lhsList);	

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:207:4: ^(start= VT_OR_INFIX dt1= lhs dt2= lhs )
                    {
                    start=(DroolsTree)match(input,VT_OR_INFIX,FOLLOW_VT_OR_INFIX_in_lhs1173); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs1177);
                    dt1=lhs();

                    state._fsp--;

                    pushFollow(FOLLOW_lhs_in_lhs1181);
                    dt2=lhs();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	lhsList.add((dt1!=null?dt1.baseDescr:null));
                    		lhsList.add((dt2!=null?dt2.baseDescr:null));
                    		retval.baseDescr = factory.createOr(start, lhsList);	

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:211:4: ^(start= VT_AND_PREFIX (dt= lhs )+ )
                    {
                    start=(DroolsTree)match(input,VT_AND_PREFIX,FOLLOW_VT_AND_PREFIX_in_lhs1193); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:211:26: (dt= lhs )+
                    int cnt37=0;
                    loop37:
                    do {
                        int alt37=2;
                        int LA37_0 = input.LA(1);

                        if ( ((LA37_0>=VT_AND_PREFIX && LA37_0<=VT_OR_INFIX)||LA37_0==VT_FOR_CE||LA37_0==VT_PATTERN||LA37_0==VK_EVAL||LA37_0==VK_NOT||(LA37_0>=VK_EXISTS && LA37_0<=VK_FORALL)||LA37_0==FROM) ) {
                            alt37=1;
                        }


                        switch (alt37) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:211:27: dt= lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs1198);
                    	    dt=lhs();

                    	    state._fsp--;

                    	    	lhsList.add((dt!=null?dt.baseDescr:null));	

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
                    	retval.baseDescr = factory.createAnd(start, lhsList);	

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:213:4: ^(start= VT_AND_INFIX dt1= lhs dt2= lhs )
                    {
                    start=(DroolsTree)match(input,VT_AND_INFIX,FOLLOW_VT_AND_INFIX_in_lhs1214); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs1218);
                    dt1=lhs();

                    state._fsp--;

                    pushFollow(FOLLOW_lhs_in_lhs1222);
                    dt2=lhs();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	lhsList.add((dt1!=null?dt1.baseDescr:null));
                    		lhsList.add((dt2!=null?dt2.baseDescr:null));
                    		retval.baseDescr = factory.createAnd(start, lhsList);	

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:217:4: ^(start= VK_EXISTS dt= lhs )
                    {
                    start=(DroolsTree)match(input,VK_EXISTS,FOLLOW_VK_EXISTS_in_lhs1234); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs1238);
                    dt=lhs();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createExists(start, (dt!=null?dt.baseDescr:null));	

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:219:4: ^(start= VK_NOT dt= lhs )
                    {
                    start=(DroolsTree)match(input,VK_NOT,FOLLOW_VK_NOT_in_lhs1250); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs1254);
                    dt=lhs();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createNot(start, (dt!=null?dt.baseDescr:null));	

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:221:4: ^(start= VK_EVAL pc= VT_PAREN_CHUNK )
                    {
                    start=(DroolsTree)match(input,VK_EVAL,FOLLOW_VK_EVAL_in_lhs1266); 

                    match(input, Token.DOWN, null); 
                    pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_lhs1270); 

                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createEval(start, pc);	

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:223:4: ^(start= VK_FORALL (dt= lhs )+ )
                    {
                    start=(DroolsTree)match(input,VK_FORALL,FOLLOW_VK_FORALL_in_lhs1282); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:223:22: (dt= lhs )+
                    int cnt38=0;
                    loop38:
                    do {
                        int alt38=2;
                        int LA38_0 = input.LA(1);

                        if ( ((LA38_0>=VT_AND_PREFIX && LA38_0<=VT_OR_INFIX)||LA38_0==VT_FOR_CE||LA38_0==VT_PATTERN||LA38_0==VK_EVAL||LA38_0==VK_NOT||(LA38_0>=VK_EXISTS && LA38_0<=VK_FORALL)||LA38_0==FROM) ) {
                            alt38=1;
                        }


                        switch (alt38) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:223:23: dt= lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs1287);
                    	    dt=lhs();

                    	    state._fsp--;

                    	    	lhsList.add((dt!=null?dt.baseDescr:null));	

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt38 >= 1 ) break loop38;
                                EarlyExitException eee =
                                    new EarlyExitException(38, input);
                                throw eee;
                        }
                        cnt38++;
                    } while (true);


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createForAll(start, lhsList);	

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:225:4: ^(start= VT_FOR_CE dt= lhs for_functions ( fact_expression )? )
                    {
                    start=(DroolsTree)match(input,VT_FOR_CE,FOLLOW_VT_FOR_CE_in_lhs1303); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs1307);
                    dt=lhs();

                    state._fsp--;

                    pushFollow(FOLLOW_for_functions_in_lhs1309);
                    for_functions();

                    state._fsp--;

                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:225:43: ( fact_expression )?
                    int alt39=2;
                    int LA39_0 = input.LA(1);

                    if ( (LA39_0==VT_FACT||LA39_0==VT_PAREN_CHUNK||(LA39_0>=VT_FACT_BINDING && LA39_0<=VT_ACCESSOR_PATH)||LA39_0==VK_EVAL||LA39_0==VK_IN||LA39_0==VK_OPERATOR||(LA39_0>=SIGNED_DECIMAL && LA39_0<=SIGNED_FLOAT)||LA39_0==STRING||LA39_0==BOOL||LA39_0==DECIMAL||(LA39_0>=DOUBLE_PIPE && LA39_0<=DOUBLE_AMPER)||(LA39_0>=EQUALS && LA39_0<=NOT_EQUALS)||(LA39_0>=NULL && LA39_0<=FLOAT)) ) {
                        alt39=1;
                    }
                    switch (alt39) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:225:43: fact_expression
                            {
                            pushFollow(FOLLOW_fact_expression_in_lhs1311);
                            fact_expression();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 10 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:227:4: ^( FROM pn= lhs_pattern fe= from_elements )
                    {
                    match(input,FROM,FOLLOW_FROM_in_lhs1322); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_pattern_in_lhs1326);
                    pn=lhs_pattern();

                    state._fsp--;

                    pushFollow(FOLLOW_from_elements_in_lhs1330);
                    fe=from_elements();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.setupFrom(pn, (fe!=null?fe.patternSourceDescr:null));	

                    }
                    break;
                case 11 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:229:4: pn= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs1341);
                    pn=lhs_pattern();

                    state._fsp--;

                    	retval.baseDescr = pn;	

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
        return retval;
    }
    // $ANTLR end "lhs"

    public static class from_elements_return extends TreeRuleReturnScope {
        public PatternSourceDescr patternSourceDescr;
    };

    // $ANTLR start "from_elements"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:233:1: from_elements returns [PatternSourceDescr patternSourceDescr] : ( ^(start= ACCUMULATE dt= lhs ret= accumulate_parts[$patternSourceDescr] ) | ^(start= COLLECT dt= lhs ) | ^(start= VK_ENTRY_POINT entryId= VT_ENTRYPOINT_ID ) | fs= from_source_clause );
    public final DescrBuilderTree.from_elements_return from_elements() throws RecognitionException {
        DescrBuilderTree.from_elements_return retval = new DescrBuilderTree.from_elements_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree entryId=null;
        DescrBuilderTree.lhs_return dt = null;

        AccumulateDescr ret = null;

        DescrBuilderTree.from_source_clause_return fs = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:234:2: ( ^(start= ACCUMULATE dt= lhs ret= accumulate_parts[$patternSourceDescr] ) | ^(start= COLLECT dt= lhs ) | ^(start= VK_ENTRY_POINT entryId= VT_ENTRYPOINT_ID ) | fs= from_source_clause )
            int alt41=4;
            switch ( input.LA(1) ) {
            case ACCUMULATE:
                {
                alt41=1;
                }
                break;
            case COLLECT:
                {
                alt41=2;
                }
                break;
            case VK_ENTRY_POINT:
                {
                alt41=3;
                }
                break;
            case VT_FROM_SOURCE:
                {
                alt41=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;
            }

            switch (alt41) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:234:4: ^(start= ACCUMULATE dt= lhs ret= accumulate_parts[$patternSourceDescr] )
                    {
                    start=(DroolsTree)match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_from_elements1363); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_from_elements1367);
                    dt=lhs();

                    state._fsp--;

                    	retval.patternSourceDescr = factory.createAccumulate(start, (dt!=null?dt.baseDescr:null));	
                    pushFollow(FOLLOW_accumulate_parts_in_from_elements1377);
                    ret=accumulate_parts(retval.patternSourceDescr);

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.patternSourceDescr = ret;	

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:238:4: ^(start= COLLECT dt= lhs )
                    {
                    start=(DroolsTree)match(input,COLLECT,FOLLOW_COLLECT_in_from_elements1390); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_from_elements1394);
                    dt=lhs();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.patternSourceDescr = factory.createCollect(start, (dt!=null?dt.baseDescr:null));	

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:240:4: ^(start= VK_ENTRY_POINT entryId= VT_ENTRYPOINT_ID )
                    {
                    start=(DroolsTree)match(input,VK_ENTRY_POINT,FOLLOW_VK_ENTRY_POINT_in_from_elements1406); 

                    match(input, Token.DOWN, null); 
                    entryId=(DroolsTree)match(input,VT_ENTRYPOINT_ID,FOLLOW_VT_ENTRYPOINT_ID_in_from_elements1410); 

                    match(input, Token.UP, null); 
                    	retval.patternSourceDescr = factory.createEntryPoint(start, entryId);	

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:242:4: fs= from_source_clause
                    {
                    pushFollow(FOLLOW_from_source_clause_in_from_elements1421);
                    fs=from_source_clause();

                    state._fsp--;

                    	retval.patternSourceDescr = (fs!=null?fs.fromDescr:null);	

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
        return retval;
    }
    // $ANTLR end "from_elements"


    // $ANTLR start "accumulate_parts"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:246:1: accumulate_parts[PatternSourceDescr patternSourceDescr] returns [AccumulateDescr accumulateDescr] : (ac1= accumulate_init_clause[$patternSourceDescr] | ac2= accumulate_id_clause[$patternSourceDescr] );
    public final AccumulateDescr accumulate_parts(PatternSourceDescr patternSourceDescr) throws RecognitionException {
        AccumulateDescr accumulateDescr = null;

        DescrBuilderTree.accumulate_init_clause_return ac1 = null;

        AccumulateDescr ac2 = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:247:2: (ac1= accumulate_init_clause[$patternSourceDescr] | ac2= accumulate_id_clause[$patternSourceDescr] )
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==VT_ACCUMULATE_INIT_CLAUSE) ) {
                alt42=1;
            }
            else if ( (LA42_0==VT_ACCUMULATE_ID_CLAUSE) ) {
                alt42=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:247:4: ac1= accumulate_init_clause[$patternSourceDescr]
                    {
                    pushFollow(FOLLOW_accumulate_init_clause_in_accumulate_parts1442);
                    ac1=accumulate_init_clause(patternSourceDescr);

                    state._fsp--;

                    	accumulateDescr = (ac1!=null?ac1.accumulateDescr:null);	

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:249:4: ac2= accumulate_id_clause[$patternSourceDescr]
                    {
                    pushFollow(FOLLOW_accumulate_id_clause_in_accumulate_parts1453);
                    ac2=accumulate_id_clause(patternSourceDescr);

                    state._fsp--;

                    	accumulateDescr = ac2;	

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
        return accumulateDescr;
    }
    // $ANTLR end "accumulate_parts"

    public static class accumulate_init_clause_return extends TreeRuleReturnScope {
        public AccumulateDescr accumulateDescr;
    };

    // $ANTLR start "accumulate_init_clause"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:253:1: accumulate_init_clause[PatternSourceDescr accumulateParam] returns [AccumulateDescr accumulateDescr] : ^( VT_ACCUMULATE_INIT_CLAUSE ^(start= VK_INIT pc1= VT_PAREN_CHUNK ) ^( VK_ACTION pc2= VT_PAREN_CHUNK ) (rev= accumulate_init_reverse_clause )? ^( VK_RESULT pc3= VT_PAREN_CHUNK ) ) ;
    public final DescrBuilderTree.accumulate_init_clause_return accumulate_init_clause(PatternSourceDescr accumulateParam) throws RecognitionException {
        DescrBuilderTree.accumulate_init_clause_return retval = new DescrBuilderTree.accumulate_init_clause_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree pc1=null;
        DroolsTree pc2=null;
        DroolsTree pc3=null;
        DescrBuilderTree.accumulate_init_reverse_clause_return rev = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:254:2: ( ^( VT_ACCUMULATE_INIT_CLAUSE ^(start= VK_INIT pc1= VT_PAREN_CHUNK ) ^( VK_ACTION pc2= VT_PAREN_CHUNK ) (rev= accumulate_init_reverse_clause )? ^( VK_RESULT pc3= VT_PAREN_CHUNK ) ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:254:4: ^( VT_ACCUMULATE_INIT_CLAUSE ^(start= VK_INIT pc1= VT_PAREN_CHUNK ) ^( VK_ACTION pc2= VT_PAREN_CHUNK ) (rev= accumulate_init_reverse_clause )? ^( VK_RESULT pc3= VT_PAREN_CHUNK ) )
            {
            match(input,VT_ACCUMULATE_INIT_CLAUSE,FOLLOW_VT_ACCUMULATE_INIT_CLAUSE_in_accumulate_init_clause1476); 

            match(input, Token.DOWN, null); 
            start=(DroolsTree)match(input,VK_INIT,FOLLOW_VK_INIT_in_accumulate_init_clause1485); 

            match(input, Token.DOWN, null); 
            pc1=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1489); 

            match(input, Token.UP, null); 
            match(input,VK_ACTION,FOLLOW_VK_ACTION_in_accumulate_init_clause1497); 

            match(input, Token.DOWN, null); 
            pc2=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1501); 

            match(input, Token.UP, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:257:7: (rev= accumulate_init_reverse_clause )?
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==VK_REVERSE) ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:257:7: rev= accumulate_init_reverse_clause
                    {
                    pushFollow(FOLLOW_accumulate_init_reverse_clause_in_accumulate_init_clause1510);
                    rev=accumulate_init_reverse_clause();

                    state._fsp--;


                    }
                    break;

            }

            match(input,VK_RESULT,FOLLOW_VK_RESULT_in_accumulate_init_clause1517); 

            match(input, Token.DOWN, null); 
            pc3=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1521); 

            match(input, Token.UP, null); 

            match(input, Token.UP, null); 
            	if (null == rev){
            			retval.accumulateDescr = factory.setupAccumulateInit(accumulateParam, start, pc1, pc2, pc3, null);
            		} else {
            			retval.accumulateDescr = factory.setupAccumulateInit(accumulateParam, start, pc1, pc2, pc3, (rev!=null?rev.vkReverseChunk:null));
            		}	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "accumulate_init_clause"

    public static class accumulate_init_reverse_clause_return extends TreeRuleReturnScope {
        public DroolsTree vkReverse;
        public DroolsTree vkReverseChunk;
    };

    // $ANTLR start "accumulate_init_reverse_clause"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:266:1: accumulate_init_reverse_clause returns [DroolsTree vkReverse, DroolsTree vkReverseChunk] : ^(vk= VK_REVERSE pc= VT_PAREN_CHUNK ) ;
    public final DescrBuilderTree.accumulate_init_reverse_clause_return accumulate_init_reverse_clause() throws RecognitionException {
        DescrBuilderTree.accumulate_init_reverse_clause_return retval = new DescrBuilderTree.accumulate_init_reverse_clause_return();
        retval.start = input.LT(1);

        DroolsTree vk=null;
        DroolsTree pc=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:267:2: ( ^(vk= VK_REVERSE pc= VT_PAREN_CHUNK ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:267:4: ^(vk= VK_REVERSE pc= VT_PAREN_CHUNK )
            {
            vk=(DroolsTree)match(input,VK_REVERSE,FOLLOW_VK_REVERSE_in_accumulate_init_reverse_clause1544); 

            match(input, Token.DOWN, null); 
            pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_reverse_clause1548); 

            match(input, Token.UP, null); 
            	retval.vkReverse = vk;
            		retval.vkReverseChunk = pc;	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "accumulate_init_reverse_clause"


    // $ANTLR start "accumulate_id_clause"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:272:1: accumulate_id_clause[PatternSourceDescr accumulateParam] returns [AccumulateDescr accumulateDescr] : ^( VT_ACCUMULATE_ID_CLAUSE id= ID pc= VT_PAREN_CHUNK ) ;
    public final AccumulateDescr accumulate_id_clause(PatternSourceDescr accumulateParam) throws RecognitionException {
        AccumulateDescr accumulateDescr = null;

        DroolsTree id=null;
        DroolsTree pc=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:273:2: ( ^( VT_ACCUMULATE_ID_CLAUSE id= ID pc= VT_PAREN_CHUNK ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:273:4: ^( VT_ACCUMULATE_ID_CLAUSE id= ID pc= VT_PAREN_CHUNK )
            {
            match(input,VT_ACCUMULATE_ID_CLAUSE,FOLLOW_VT_ACCUMULATE_ID_CLAUSE_in_accumulate_id_clause1570); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_accumulate_id_clause1574); 
            pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_id_clause1578); 

            match(input, Token.UP, null); 
            	accumulateDescr = factory.setupAccumulateId(accumulateParam, id, pc);	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return accumulateDescr;
    }
    // $ANTLR end "accumulate_id_clause"

    protected static class from_source_clause_scope {
        AccessorDescr accessorDescr;
    }
    protected Stack from_source_clause_stack = new Stack();

    public static class from_source_clause_return extends TreeRuleReturnScope {
        public FromDescr fromDescr;
        public AccessorDescr retAccessorDescr;
    };

    // $ANTLR start "from_source_clause"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:277:1: from_source_clause returns [FromDescr fromDescr, AccessorDescr retAccessorDescr] : ^(fs= VT_FROM_SOURCE ) ;
    public final DescrBuilderTree.from_source_clause_return from_source_clause() throws RecognitionException {
        from_source_clause_stack.push(new from_source_clause_scope());
        DescrBuilderTree.from_source_clause_return retval = new DescrBuilderTree.from_source_clause_return();
        retval.start = input.LT(1);

        DroolsTree fs=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:280:3: ( ^(fs= VT_FROM_SOURCE ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:280:5: ^(fs= VT_FROM_SOURCE )
            {
            fs=(DroolsTree)match(input,VT_FROM_SOURCE,FOLLOW_VT_FROM_SOURCE_in_from_source_clause1602); 

              ((from_source_clause_scope)from_source_clause_stack.peek()).accessorDescr = factory.createAccessor((fs!=null?fs.getText():null));	
            			   retval.retAccessorDescr = ((from_source_clause_scope)from_source_clause_stack.peek()).accessorDescr;	
            			

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                match(input, Token.UP, null); 
            }
            	retval.fromDescr = factory.createFromSource(factory.setupAccessorOffset(((from_source_clause_scope)from_source_clause_stack.peek()).accessorDescr)); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            from_source_clause_stack.pop();
        }
        return retval;
    }
    // $ANTLR end "from_source_clause"

    public static class expression_chain_return extends TreeRuleReturnScope {
    };

    // $ANTLR start "expression_chain"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:300:1: expression_chain : ^(start= VT_EXPRESSION_CHAIN id= ID (sc= VT_SQUARE_CHUNK )? (pc= VT_PAREN_CHUNK )? ( expression_chain )? ) ;
    public final DescrBuilderTree.expression_chain_return expression_chain() throws RecognitionException {
        DescrBuilderTree.expression_chain_return retval = new DescrBuilderTree.expression_chain_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree id=null;
        DroolsTree sc=null;
        DroolsTree pc=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:301:2: ( ^(start= VT_EXPRESSION_CHAIN id= ID (sc= VT_SQUARE_CHUNK )? (pc= VT_PAREN_CHUNK )? ( expression_chain )? ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:301:4: ^(start= VT_EXPRESSION_CHAIN id= ID (sc= VT_SQUARE_CHUNK )? (pc= VT_PAREN_CHUNK )? ( expression_chain )? )
            {
            start=(DroolsTree)match(input,VT_EXPRESSION_CHAIN,FOLLOW_VT_EXPRESSION_CHAIN_in_expression_chain1632); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_expression_chain1636); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:301:40: (sc= VT_SQUARE_CHUNK )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==VT_SQUARE_CHUNK) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:301:40: sc= VT_SQUARE_CHUNK
                    {
                    sc=(DroolsTree)match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_expression_chain1640); 

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:301:60: (pc= VT_PAREN_CHUNK )?
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==VT_PAREN_CHUNK) ) {
                alt45=1;
            }
            switch (alt45) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:301:60: pc= VT_PAREN_CHUNK
                    {
                    pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_expression_chain1645); 

                    }
                    break;

            }

            	DeclarativeInvokerDescr declarativeInvokerResult = factory.createExpressionChain(start, id, sc, pc);	
            		((from_source_clause_scope)from_source_clause_stack.peek()).accessorDescr.addInvoker(declarativeInvokerResult);	
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:304:3: ( expression_chain )?
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==VT_EXPRESSION_CHAIN) ) {
                alt46=1;
            }
            switch (alt46) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:304:3: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain1653);
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
        return retval;
    }
    // $ANTLR end "expression_chain"


    // $ANTLR start "lhs_pattern"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:307:1: lhs_pattern returns [BaseDescr baseDescr] : ^( VT_PATTERN fe= fact_expression ) (oc= over_clause )? ;
    public final BaseDescr lhs_pattern() throws RecognitionException {
        BaseDescr baseDescr = null;

        DescrBuilderTree.fact_expression_return fe = null;

        List oc = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:308:2: ( ^( VT_PATTERN fe= fact_expression ) (oc= over_clause )? )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:308:4: ^( VT_PATTERN fe= fact_expression ) (oc= over_clause )?
            {
            match(input,VT_PATTERN,FOLLOW_VT_PATTERN_in_lhs_pattern1671); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_fact_expression_in_lhs_pattern1675);
            fe=fact_expression();

            state._fsp--;


            match(input, Token.UP, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:308:39: (oc= over_clause )?
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==OVER) ) {
                alt47=1;
            }
            switch (alt47) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:308:39: oc= over_clause
                    {
                    pushFollow(FOLLOW_over_clause_in_lhs_pattern1680);
                    oc=over_clause();

                    state._fsp--;


                    }
                    break;

            }

            	baseDescr = factory.setupBehavior((fe!=null?fe.descr:null), oc);	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return baseDescr;
    }
    // $ANTLR end "lhs_pattern"


    // $ANTLR start "over_clause"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:312:1: over_clause returns [List behaviorList] : ^( OVER (oe= over_element )+ ) ;
    public final List over_clause() throws RecognitionException {
        List behaviorList = null;

        BehaviorDescr oe = null;


        behaviorList = new LinkedList();
        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:314:2: ( ^( OVER (oe= over_element )+ ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:314:4: ^( OVER (oe= over_element )+ )
            {
            match(input,OVER,FOLLOW_OVER_in_over_clause1705); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:314:11: (oe= over_element )+
            int cnt48=0;
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( (LA48_0==VT_BEHAVIOR) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:314:12: oe= over_element
            	    {
            	    pushFollow(FOLLOW_over_element_in_over_clause1710);
            	    oe=over_element();

            	    state._fsp--;

            	    behaviorList.add(oe);

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

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return behaviorList;
    }
    // $ANTLR end "over_clause"


    // $ANTLR start "over_element"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:317:1: over_element returns [BehaviorDescr behavior] : ^( VT_BEHAVIOR ID id2= ID pc= VT_PAREN_CHUNK ) ;
    public final BehaviorDescr over_element() throws RecognitionException {
        BehaviorDescr behavior = null;

        DroolsTree id2=null;
        DroolsTree pc=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:318:2: ( ^( VT_BEHAVIOR ID id2= ID pc= VT_PAREN_CHUNK ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:318:4: ^( VT_BEHAVIOR ID id2= ID pc= VT_PAREN_CHUNK )
            {
            match(input,VT_BEHAVIOR,FOLLOW_VT_BEHAVIOR_in_over_element1731); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_over_element1733); 
            id2=(DroolsTree)match(input,ID,FOLLOW_ID_in_over_element1737); 
            pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_over_element1741); 

            match(input, Token.UP, null); 
            	behavior = factory.createBehavior(id2,pc);	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return behavior;
    }
    // $ANTLR end "over_element"


    // $ANTLR start "for_functions"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:322:1: for_functions returns [List<ForFunctionDescr> list] : ^( VT_FOR_FUNCTIONS (ff= for_function )+ ) ;
    public final List<ForFunctionDescr> for_functions() throws RecognitionException {
        List<ForFunctionDescr> list = null;

        ForFunctionDescr ff = null;


         List<ForFunctionDescr> fors = new ArrayList<ForFunctionDescr>(); 
        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:325:2: ( ^( VT_FOR_FUNCTIONS (ff= for_function )+ ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:325:4: ^( VT_FOR_FUNCTIONS (ff= for_function )+ )
            {
            match(input,VT_FOR_FUNCTIONS,FOLLOW_VT_FOR_FUNCTIONS_in_for_functions1770); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:325:23: (ff= for_function )+
            int cnt49=0;
            loop49:
            do {
                int alt49=2;
                int LA49_0 = input.LA(1);

                if ( (LA49_0==ID) ) {
                    alt49=1;
                }


                switch (alt49) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:325:24: ff= for_function
            	    {
            	    pushFollow(FOLLOW_for_function_in_for_functions1775);
            	    ff=for_function();

            	    state._fsp--;

            	     fors.add( ff ); 

            	    }
            	    break;

            	default :
            	    if ( cnt49 >= 1 ) break loop49;
                        EarlyExitException eee =
                            new EarlyExitException(49, input);
                        throw eee;
                }
                cnt49++;
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
        return list;
    }
    // $ANTLR end "for_functions"


    // $ANTLR start "for_function"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:328:1: for_function returns [ForFunctionDescr func] : ^( ID VT_LABEL arguments ) ;
    public final ForFunctionDescr for_function() throws RecognitionException {
        ForFunctionDescr func = null;

        DroolsTree ID1=null;
        DroolsTree VT_LABEL2=null;
        List<String> arguments3 = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:329:2: ( ^( ID VT_LABEL arguments ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:329:5: ^( ID VT_LABEL arguments )
            {
            ID1=(DroolsTree)match(input,ID,FOLLOW_ID_in_for_function1800); 

            match(input, Token.DOWN, null); 
            VT_LABEL2=(DroolsTree)match(input,VT_LABEL,FOLLOW_VT_LABEL_in_for_function1802); 
            pushFollow(FOLLOW_arguments_in_for_function1804);
            arguments3=arguments();

            state._fsp--;


            match(input, Token.UP, null); 
              func = factory.createForFunction( ID1, VT_LABEL2, arguments3 );   

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return func;
    }
    // $ANTLR end "for_function"


    // $ANTLR start "arguments"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:333:1: arguments returns [List<String> args] : ^( VT_ARGUMENTS (param= VT_EXPRESSION )* ) ;
    public final List<String> arguments() throws RecognitionException {
        List<String> args = null;

        DroolsTree param=null;

         List<String> params = new ArrayList<String>(); 
        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:336:2: ( ^( VT_ARGUMENTS (param= VT_EXPRESSION )* ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:336:4: ^( VT_ARGUMENTS (param= VT_EXPRESSION )* )
            {
            match(input,VT_ARGUMENTS,FOLLOW_VT_ARGUMENTS_in_arguments1833); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // src/main/resources/org/drools/lang/DescrBuilderTree.g:336:19: (param= VT_EXPRESSION )*
                loop50:
                do {
                    int alt50=2;
                    int LA50_0 = input.LA(1);

                    if ( (LA50_0==VT_EXPRESSION) ) {
                        alt50=1;
                    }


                    switch (alt50) {
                	case 1 :
                	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:336:20: param= VT_EXPRESSION
                	    {
                	    param=(DroolsTree)match(input,VT_EXPRESSION,FOLLOW_VT_EXPRESSION_in_arguments1838); 
                	     params.add((param!=null?param.getText():null)); 

                	    }
                	    break;

                	default :
                	    break loop50;
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
        return args;
    }
    // $ANTLR end "arguments"

    public static class fact_expression_return extends TreeRuleReturnScope {
        public BaseDescr descr;
    };

    // $ANTLR start "fact_expression"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:339:1: fact_expression returns [BaseDescr descr] : ( ^( VT_FACT pt= pattern_type (fe= fact_expression )* ) | ^( VT_FACT_BINDING label= VT_LABEL fact= fact_expression ) | ^(start= VT_FACT_OR left= fact_expression right= fact_expression ) | ^( VT_FIELD field= field_element (fe= fact_expression )? ) | ^( VT_BIND_FIELD label= VT_LABEL fe= fact_expression ) | ^( VK_EVAL pc= VT_PAREN_CHUNK ) | ^(op= EQUALS fe= fact_expression ) | ^(op= NOT_EQUALS fe= fact_expression ) | ^(op= GREATER fe= fact_expression ) | ^(op= GREATER_EQUALS fe= fact_expression ) | ^(op= LESS fe= fact_expression ) | ^(op= LESS_EQUALS fe= fact_expression ) | ^(op= VK_OPERATOR (not= VK_NOT )? (param= VT_SQUARE_CHUNK )? fe= fact_expression ) | ^( VK_IN (not= VK_NOT )? (fe= fact_expression )+ ) | ^( DOUBLE_PIPE left= fact_expression right= fact_expression ) | ^( DOUBLE_AMPER left= fact_expression right= fact_expression ) | ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) | s= STRING | ( PLUS | m= MINUS )? ( (i= DECIMAL | i= SIGNED_DECIMAL ) | (h= HEX | h= SIGNED_HEX ) | (f= FLOAT | f= SIGNED_FLOAT ) ) | b= BOOL | n= NULL | pc= VT_PAREN_CHUNK );
    public final DescrBuilderTree.fact_expression_return fact_expression() throws RecognitionException {
        DescrBuilderTree.fact_expression_return retval = new DescrBuilderTree.fact_expression_return();
        retval.start = input.LT(1);

        DroolsTree label=null;
        DroolsTree start=null;
        DroolsTree pc=null;
        DroolsTree op=null;
        DroolsTree not=null;
        DroolsTree param=null;
        DroolsTree s=null;
        DroolsTree m=null;
        DroolsTree i=null;
        DroolsTree h=null;
        DroolsTree f=null;
        DroolsTree b=null;
        DroolsTree n=null;
        BaseDescr pt = null;

        DescrBuilderTree.fact_expression_return fe = null;

        DescrBuilderTree.fact_expression_return fact = null;

        DescrBuilderTree.fact_expression_return left = null;

        DescrBuilderTree.fact_expression_return right = null;

        FieldConstraintDescr field = null;

        BaseDescr ae = null;



        	List<BaseDescr> exprList = new LinkedList<BaseDescr>();

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:342:3: ( ^( VT_FACT pt= pattern_type (fe= fact_expression )* ) | ^( VT_FACT_BINDING label= VT_LABEL fact= fact_expression ) | ^(start= VT_FACT_OR left= fact_expression right= fact_expression ) | ^( VT_FIELD field= field_element (fe= fact_expression )? ) | ^( VT_BIND_FIELD label= VT_LABEL fe= fact_expression ) | ^( VK_EVAL pc= VT_PAREN_CHUNK ) | ^(op= EQUALS fe= fact_expression ) | ^(op= NOT_EQUALS fe= fact_expression ) | ^(op= GREATER fe= fact_expression ) | ^(op= GREATER_EQUALS fe= fact_expression ) | ^(op= LESS fe= fact_expression ) | ^(op= LESS_EQUALS fe= fact_expression ) | ^(op= VK_OPERATOR (not= VK_NOT )? (param= VT_SQUARE_CHUNK )? fe= fact_expression ) | ^( VK_IN (not= VK_NOT )? (fe= fact_expression )+ ) | ^( DOUBLE_PIPE left= fact_expression right= fact_expression ) | ^( DOUBLE_AMPER left= fact_expression right= fact_expression ) | ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) | s= STRING | ( PLUS | m= MINUS )? ( (i= DECIMAL | i= SIGNED_DECIMAL ) | (h= HEX | h= SIGNED_HEX ) | (f= FLOAT | f= SIGNED_FLOAT ) ) | b= BOOL | n= NULL | pc= VT_PAREN_CHUNK )
            int alt63=22;
            switch ( input.LA(1) ) {
            case VT_FACT:
                {
                alt63=1;
                }
                break;
            case VT_FACT_BINDING:
                {
                alt63=2;
                }
                break;
            case VT_FACT_OR:
                {
                alt63=3;
                }
                break;
            case VT_FIELD:
                {
                alt63=4;
                }
                break;
            case VT_BIND_FIELD:
                {
                alt63=5;
                }
                break;
            case VK_EVAL:
                {
                alt63=6;
                }
                break;
            case EQUALS:
                {
                alt63=7;
                }
                break;
            case NOT_EQUALS:
                {
                alt63=8;
                }
                break;
            case GREATER:
                {
                alt63=9;
                }
                break;
            case GREATER_EQUALS:
                {
                alt63=10;
                }
                break;
            case LESS:
                {
                alt63=11;
                }
                break;
            case LESS_EQUALS:
                {
                alt63=12;
                }
                break;
            case VK_OPERATOR:
                {
                alt63=13;
                }
                break;
            case VK_IN:
                {
                alt63=14;
                }
                break;
            case DOUBLE_PIPE:
                {
                alt63=15;
                }
                break;
            case DOUBLE_AMPER:
                {
                alt63=16;
                }
                break;
            case VT_ACCESSOR_PATH:
                {
                alt63=17;
                }
                break;
            case STRING:
                {
                alt63=18;
                }
                break;
            case SIGNED_DECIMAL:
            case SIGNED_HEX:
            case SIGNED_FLOAT:
            case DECIMAL:
            case PLUS:
            case MINUS:
            case HEX:
            case FLOAT:
                {
                alt63=19;
                }
                break;
            case BOOL:
                {
                alt63=20;
                }
                break;
            case NULL:
                {
                alt63=21;
                }
                break;
            case VT_PAREN_CHUNK:
                {
                alt63=22;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 63, 0, input);

                throw nvae;
            }

            switch (alt63) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:342:5: ^( VT_FACT pt= pattern_type (fe= fact_expression )* )
                    {
                    match(input,VT_FACT,FOLLOW_VT_FACT_in_fact_expression1863); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_pattern_type_in_fact_expression1867);
                    pt=pattern_type();

                    state._fsp--;

                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:342:31: (fe= fact_expression )*
                    loop51:
                    do {
                        int alt51=2;
                        int LA51_0 = input.LA(1);

                        if ( (LA51_0==VT_FACT||LA51_0==VT_PAREN_CHUNK||(LA51_0>=VT_FACT_BINDING && LA51_0<=VT_ACCESSOR_PATH)||LA51_0==VK_EVAL||LA51_0==VK_IN||LA51_0==VK_OPERATOR||(LA51_0>=SIGNED_DECIMAL && LA51_0<=SIGNED_FLOAT)||LA51_0==STRING||LA51_0==BOOL||LA51_0==DECIMAL||(LA51_0>=DOUBLE_PIPE && LA51_0<=DOUBLE_AMPER)||(LA51_0>=EQUALS && LA51_0<=NOT_EQUALS)||(LA51_0>=NULL && LA51_0<=FLOAT)) ) {
                            alt51=1;
                        }


                        switch (alt51) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:342:32: fe= fact_expression
                    	    {
                    	    pushFollow(FOLLOW_fact_expression_in_fact_expression1872);
                    	    fe=fact_expression();

                    	    state._fsp--;

                    	    exprList.add((fe!=null?fe.descr:null));

                    	    }
                    	    break;

                    	default :
                    	    break loop51;
                        }
                    } while (true);


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createPattern(pt, exprList);	

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:344:4: ^( VT_FACT_BINDING label= VT_LABEL fact= fact_expression )
                    {
                    match(input,VT_FACT_BINDING,FOLLOW_VT_FACT_BINDING_in_fact_expression1886); 

                    match(input, Token.DOWN, null); 
                    label=(DroolsTree)match(input,VT_LABEL,FOLLOW_VT_LABEL_in_fact_expression1890); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1894);
                    fact=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupPatternBiding(label, (fact!=null?fact.descr:null));	

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:346:4: ^(start= VT_FACT_OR left= fact_expression right= fact_expression )
                    {
                    start=(DroolsTree)match(input,VT_FACT_OR,FOLLOW_VT_FACT_OR_in_fact_expression1906); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1910);
                    left=fact_expression();

                    state._fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1914);
                    right=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createFactOr(start, (left!=null?left.descr:null), (right!=null?right.descr:null));	

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:349:4: ^( VT_FIELD field= field_element (fe= fact_expression )? )
                    {
                    match(input,VT_FIELD,FOLLOW_VT_FIELD_in_fact_expression1925); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_field_element_in_fact_expression1929);
                    field=field_element();

                    state._fsp--;

                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:349:37: (fe= fact_expression )?
                    int alt52=2;
                    int LA52_0 = input.LA(1);

                    if ( (LA52_0==VT_FACT||LA52_0==VT_PAREN_CHUNK||(LA52_0>=VT_FACT_BINDING && LA52_0<=VT_ACCESSOR_PATH)||LA52_0==VK_EVAL||LA52_0==VK_IN||LA52_0==VK_OPERATOR||(LA52_0>=SIGNED_DECIMAL && LA52_0<=SIGNED_FLOAT)||LA52_0==STRING||LA52_0==BOOL||LA52_0==DECIMAL||(LA52_0>=DOUBLE_PIPE && LA52_0<=DOUBLE_AMPER)||(LA52_0>=EQUALS && LA52_0<=NOT_EQUALS)||(LA52_0>=NULL && LA52_0<=FLOAT)) ) {
                        alt52=1;
                    }
                    switch (alt52) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:349:37: fe= fact_expression
                            {
                            pushFollow(FOLLOW_fact_expression_in_fact_expression1933);
                            fe=fact_expression();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                    	if (null != fe){
                    			retval.descr = factory.setupFieldConstraint(field, (fe!=null?fe.descr:null));
                    		} else {
                    			retval.descr = factory.setupFieldConstraint(field, null);
                    		}	

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:355:4: ^( VT_BIND_FIELD label= VT_LABEL fe= fact_expression )
                    {
                    match(input,VT_BIND_FIELD,FOLLOW_VT_BIND_FIELD_in_fact_expression1944); 

                    match(input, Token.DOWN, null); 
                    label=(DroolsTree)match(input,VT_LABEL,FOLLOW_VT_LABEL_in_fact_expression1948); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1952);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createFieldBinding(label, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:358:4: ^( VK_EVAL pc= VT_PAREN_CHUNK )
                    {
                    match(input,VK_EVAL,FOLLOW_VK_EVAL_in_fact_expression1963); 

                    match(input, Token.DOWN, null); 
                    pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_fact_expression1967); 

                    match(input, Token.UP, null); 
                    	retval.descr = factory.createPredicate(pc);	

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:361:4: ^(op= EQUALS fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,EQUALS,FOLLOW_EQUALS_in_fact_expression1980); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1984);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:363:4: ^(op= NOT_EQUALS fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_fact_expression1996); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression2000);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:365:4: ^(op= GREATER fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,GREATER,FOLLOW_GREATER_in_fact_expression2012); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression2016);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 10 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:367:4: ^(op= GREATER_EQUALS fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,GREATER_EQUALS,FOLLOW_GREATER_EQUALS_in_fact_expression2028); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression2032);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 11 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:369:4: ^(op= LESS fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,LESS,FOLLOW_LESS_in_fact_expression2044); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression2048);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 12 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:371:4: ^(op= LESS_EQUALS fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,LESS_EQUALS,FOLLOW_LESS_EQUALS_in_fact_expression2060); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression2064);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 13 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:373:4: ^(op= VK_OPERATOR (not= VK_NOT )? (param= VT_SQUARE_CHUNK )? fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,VK_OPERATOR,FOLLOW_VK_OPERATOR_in_fact_expression2076); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:373:24: (not= VK_NOT )?
                    int alt53=2;
                    int LA53_0 = input.LA(1);

                    if ( (LA53_0==VK_NOT) ) {
                        alt53=1;
                    }
                    switch (alt53) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:373:24: not= VK_NOT
                            {
                            not=(DroolsTree)match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression2080); 

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:373:38: (param= VT_SQUARE_CHUNK )?
                    int alt54=2;
                    int LA54_0 = input.LA(1);

                    if ( (LA54_0==VT_SQUARE_CHUNK) ) {
                        alt54=1;
                    }
                    switch (alt54) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:373:38: param= VT_SQUARE_CHUNK
                            {
                            param=(DroolsTree)match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_fact_expression2085); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression2090);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, not, (fe!=null?fe.descr:null), param);	

                    }
                    break;
                case 14 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:376:4: ^( VK_IN (not= VK_NOT )? (fe= fact_expression )+ )
                    {
                    match(input,VK_IN,FOLLOW_VK_IN_in_fact_expression2101); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:376:15: (not= VK_NOT )?
                    int alt55=2;
                    int LA55_0 = input.LA(1);

                    if ( (LA55_0==VK_NOT) ) {
                        alt55=1;
                    }
                    switch (alt55) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:376:15: not= VK_NOT
                            {
                            not=(DroolsTree)match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression2105); 

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:376:24: (fe= fact_expression )+
                    int cnt56=0;
                    loop56:
                    do {
                        int alt56=2;
                        int LA56_0 = input.LA(1);

                        if ( (LA56_0==VT_FACT||LA56_0==VT_PAREN_CHUNK||(LA56_0>=VT_FACT_BINDING && LA56_0<=VT_ACCESSOR_PATH)||LA56_0==VK_EVAL||LA56_0==VK_IN||LA56_0==VK_OPERATOR||(LA56_0>=SIGNED_DECIMAL && LA56_0<=SIGNED_FLOAT)||LA56_0==STRING||LA56_0==BOOL||LA56_0==DECIMAL||(LA56_0>=DOUBLE_PIPE && LA56_0<=DOUBLE_AMPER)||(LA56_0>=EQUALS && LA56_0<=NOT_EQUALS)||(LA56_0>=NULL && LA56_0<=FLOAT)) ) {
                            alt56=1;
                        }


                        switch (alt56) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:376:25: fe= fact_expression
                    	    {
                    	    pushFollow(FOLLOW_fact_expression_in_fact_expression2111);
                    	    fe=fact_expression();

                    	    state._fsp--;

                    	    exprList.add((fe!=null?fe.descr:null));

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt56 >= 1 ) break loop56;
                                EarlyExitException eee =
                                    new EarlyExitException(56, input);
                                throw eee;
                        }
                        cnt56++;
                    } while (true);


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createRestrictionConnective(not, exprList);	

                    }
                    break;
                case 15 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:379:4: ^( DOUBLE_PIPE left= fact_expression right= fact_expression )
                    {
                    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_fact_expression2126); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression2130);
                    left=fact_expression();

                    state._fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression2134);
                    right=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createOrRestrictionConnective((left!=null?left.descr:null), (right!=null?right.descr:null));	

                    }
                    break;
                case 16 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:381:4: ^( DOUBLE_AMPER left= fact_expression right= fact_expression )
                    {
                    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_fact_expression2144); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression2148);
                    left=fact_expression();

                    state._fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression2152);
                    right=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createAndRestrictionConnective((left!=null?left.descr:null), (right!=null?right.descr:null));	

                    }
                    break;
                case 17 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:384:4: ^( VT_ACCESSOR_PATH (ae= accessor_element )+ )
                    {
                    match(input,VT_ACCESSOR_PATH,FOLLOW_VT_ACCESSOR_PATH_in_fact_expression2163); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:384:23: (ae= accessor_element )+
                    int cnt57=0;
                    loop57:
                    do {
                        int alt57=2;
                        int LA57_0 = input.LA(1);

                        if ( (LA57_0==VT_ACCESSOR_ELEMENT) ) {
                            alt57=1;
                        }


                        switch (alt57) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:384:24: ae= accessor_element
                    	    {
                    	    pushFollow(FOLLOW_accessor_element_in_fact_expression2168);
                    	    ae=accessor_element();

                    	    state._fsp--;

                    	    exprList.add(ae);

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt57 >= 1 ) break loop57;
                                EarlyExitException eee =
                                    new EarlyExitException(57, input);
                                throw eee;
                        }
                        cnt57++;
                    } while (true);


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createAccessorPath(exprList);	

                    }
                    break;
                case 18 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:386:4: s= STRING
                    {
                    s=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_fact_expression2183); 
                    	retval.descr = factory.createStringLiteralRestriction(s);	

                    }
                    break;
                case 19 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:388:4: ( PLUS | m= MINUS )? ( (i= DECIMAL | i= SIGNED_DECIMAL ) | (h= HEX | h= SIGNED_HEX ) | (f= FLOAT | f= SIGNED_FLOAT ) )
                    {
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:388:4: ( PLUS | m= MINUS )?
                    int alt58=3;
                    int LA58_0 = input.LA(1);

                    if ( (LA58_0==PLUS) ) {
                        alt58=1;
                    }
                    else if ( (LA58_0==MINUS) ) {
                        alt58=2;
                    }
                    switch (alt58) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:388:5: PLUS
                            {
                            match(input,PLUS,FOLLOW_PLUS_in_fact_expression2192); 

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:388:10: m= MINUS
                            {
                            m=(DroolsTree)match(input,MINUS,FOLLOW_MINUS_in_fact_expression2196); 

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:389:10: ( (i= DECIMAL | i= SIGNED_DECIMAL ) | (h= HEX | h= SIGNED_HEX ) | (f= FLOAT | f= SIGNED_FLOAT ) )
                    int alt62=3;
                    switch ( input.LA(1) ) {
                    case SIGNED_DECIMAL:
                    case DECIMAL:
                        {
                        alt62=1;
                        }
                        break;
                    case SIGNED_HEX:
                    case HEX:
                        {
                        alt62=2;
                        }
                        break;
                    case SIGNED_FLOAT:
                    case FLOAT:
                        {
                        alt62=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 62, 0, input);

                        throw nvae;
                    }

                    switch (alt62) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:389:12: (i= DECIMAL | i= SIGNED_DECIMAL )
                            {
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:389:12: (i= DECIMAL | i= SIGNED_DECIMAL )
                            int alt59=2;
                            int LA59_0 = input.LA(1);

                            if ( (LA59_0==DECIMAL) ) {
                                alt59=1;
                            }
                            else if ( (LA59_0==SIGNED_DECIMAL) ) {
                                alt59=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 59, 0, input);

                                throw nvae;
                            }
                            switch (alt59) {
                                case 1 :
                                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:389:13: i= DECIMAL
                                    {
                                    i=(DroolsTree)match(input,DECIMAL,FOLLOW_DECIMAL_in_fact_expression2215); 

                                    }
                                    break;
                                case 2 :
                                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:389:23: i= SIGNED_DECIMAL
                                    {
                                    i=(DroolsTree)match(input,SIGNED_DECIMAL,FOLLOW_SIGNED_DECIMAL_in_fact_expression2219); 

                                    }
                                    break;

                            }

                             retval.descr = factory.createIntLiteralRestriction(i, m != null); 	

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:390:5: (h= HEX | h= SIGNED_HEX )
                            {
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:390:5: (h= HEX | h= SIGNED_HEX )
                            int alt60=2;
                            int LA60_0 = input.LA(1);

                            if ( (LA60_0==HEX) ) {
                                alt60=1;
                            }
                            else if ( (LA60_0==SIGNED_HEX) ) {
                                alt60=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 60, 0, input);

                                throw nvae;
                            }
                            switch (alt60) {
                                case 1 :
                                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:390:6: h= HEX
                                    {
                                    h=(DroolsTree)match(input,HEX,FOLLOW_HEX_in_fact_expression2231); 

                                    }
                                    break;
                                case 2 :
                                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:390:12: h= SIGNED_HEX
                                    {
                                    h=(DroolsTree)match(input,SIGNED_HEX,FOLLOW_SIGNED_HEX_in_fact_expression2235); 

                                    }
                                    break;

                            }

                             retval.descr = factory.createIntLiteralRestriction(h, m != null); 	

                            }
                            break;
                        case 3 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:391:5: (f= FLOAT | f= SIGNED_FLOAT )
                            {
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:391:5: (f= FLOAT | f= SIGNED_FLOAT )
                            int alt61=2;
                            int LA61_0 = input.LA(1);

                            if ( (LA61_0==FLOAT) ) {
                                alt61=1;
                            }
                            else if ( (LA61_0==SIGNED_FLOAT) ) {
                                alt61=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 61, 0, input);

                                throw nvae;
                            }
                            switch (alt61) {
                                case 1 :
                                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:391:6: f= FLOAT
                                    {
                                    f=(DroolsTree)match(input,FLOAT,FOLLOW_FLOAT_in_fact_expression2249); 

                                    }
                                    break;
                                case 2 :
                                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:391:14: f= SIGNED_FLOAT
                                    {
                                    f=(DroolsTree)match(input,SIGNED_FLOAT,FOLLOW_SIGNED_FLOAT_in_fact_expression2253); 

                                    }
                                    break;

                            }

                             retval.descr = factory.createFloatLiteralRestriction(f, m != null);	

                            }
                            break;

                    }


                    }
                    break;
                case 20 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:393:4: b= BOOL
                    {
                    b=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_fact_expression2269); 
                    	retval.descr = factory.createBoolLiteralRestriction(b);	

                    }
                    break;
                case 21 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:395:4: n= NULL
                    {
                    n=(DroolsTree)match(input,NULL,FOLLOW_NULL_in_fact_expression2279); 
                    	retval.descr = factory.createNullLiteralRestriction(n);	

                    }
                    break;
                case 22 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:397:4: pc= VT_PAREN_CHUNK
                    {
                    pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_fact_expression2289); 
                    	retval.descr = factory.createReturnValue(pc);	

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
        return retval;
    }
    // $ANTLR end "fact_expression"


    // $ANTLR start "field_element"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:401:1: field_element returns [FieldConstraintDescr element] : ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) ;
    public final FieldConstraintDescr field_element() throws RecognitionException {
        FieldConstraintDescr element = null;

        BaseDescr ae = null;



        	List<BaseDescr> aeList = new LinkedList<BaseDescr>();

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:404:3: ( ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:404:5: ^( VT_ACCESSOR_PATH (ae= accessor_element )+ )
            {
            match(input,VT_ACCESSOR_PATH,FOLLOW_VT_ACCESSOR_PATH_in_field_element2311); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:404:24: (ae= accessor_element )+
            int cnt64=0;
            loop64:
            do {
                int alt64=2;
                int LA64_0 = input.LA(1);

                if ( (LA64_0==VT_ACCESSOR_ELEMENT) ) {
                    alt64=1;
                }


                switch (alt64) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:404:25: ae= accessor_element
            	    {
            	    pushFollow(FOLLOW_accessor_element_in_field_element2316);
            	    ae=accessor_element();

            	    state._fsp--;

            	    aeList.add(ae);

            	    }
            	    break;

            	default :
            	    if ( cnt64 >= 1 ) break loop64;
                        EarlyExitException eee =
                            new EarlyExitException(64, input);
                        throw eee;
                }
                cnt64++;
            } while (true);


            match(input, Token.UP, null); 
            	element = factory.createFieldConstraint(aeList);	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return element;
    }
    // $ANTLR end "field_element"


    // $ANTLR start "accessor_element"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:408:1: accessor_element returns [BaseDescr element] : ^( VT_ACCESSOR_ELEMENT id= ID (sc+= VT_SQUARE_CHUNK )* ) ;
    public final BaseDescr accessor_element() throws RecognitionException {
        BaseDescr element = null;

        DroolsTree id=null;
        DroolsTree sc=null;
        List list_sc=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:409:2: ( ^( VT_ACCESSOR_ELEMENT id= ID (sc+= VT_SQUARE_CHUNK )* ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:409:4: ^( VT_ACCESSOR_ELEMENT id= ID (sc+= VT_SQUARE_CHUNK )* )
            {
            match(input,VT_ACCESSOR_ELEMENT,FOLLOW_VT_ACCESSOR_ELEMENT_in_accessor_element2340); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_accessor_element2344); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:409:34: (sc+= VT_SQUARE_CHUNK )*
            loop65:
            do {
                int alt65=2;
                int LA65_0 = input.LA(1);

                if ( (LA65_0==VT_SQUARE_CHUNK) ) {
                    alt65=1;
                }


                switch (alt65) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:409:34: sc+= VT_SQUARE_CHUNK
            	    {
            	    sc=(DroolsTree)match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_accessor_element2348); 
            	    if (list_sc==null) list_sc=new ArrayList();
            	    list_sc.add(sc);


            	    }
            	    break;

            	default :
            	    break loop65;
                }
            } while (true);


            match(input, Token.UP, null); 
            	element = factory.createAccessorElement(id, list_sc);	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return element;
    }
    // $ANTLR end "accessor_element"


    // $ANTLR start "pattern_type"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:413:1: pattern_type returns [BaseDescr dataType] : ^( VT_PATTERN_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) ;
    public final BaseDescr pattern_type() throws RecognitionException {
        BaseDescr dataType = null;

        DroolsTree idList=null;
        DroolsTree rightList=null;
        List list_idList=null;
        List list_rightList=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:414:2: ( ^( VT_PATTERN_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:414:4: ^( VT_PATTERN_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* )
            {
            match(input,VT_PATTERN_TYPE,FOLLOW_VT_PATTERN_TYPE_in_pattern_type2369); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:414:28: (idList+= ID )+
            int cnt66=0;
            loop66:
            do {
                int alt66=2;
                int LA66_0 = input.LA(1);

                if ( (LA66_0==ID) ) {
                    alt66=1;
                }


                switch (alt66) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:414:28: idList+= ID
            	    {
            	    idList=(DroolsTree)match(input,ID,FOLLOW_ID_in_pattern_type2373); 
            	    if (list_idList==null) list_idList=new ArrayList();
            	    list_idList.add(idList);


            	    }
            	    break;

            	default :
            	    if ( cnt66 >= 1 ) break loop66;
                        EarlyExitException eee =
                            new EarlyExitException(66, input);
                        throw eee;
                }
                cnt66++;
            } while (true);

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:414:34: ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            loop67:
            do {
                int alt67=2;
                int LA67_0 = input.LA(1);

                if ( (LA67_0==LEFT_SQUARE) ) {
                    alt67=1;
                }


                switch (alt67) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:414:35: LEFT_SQUARE rightList+= RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_pattern_type2377); 
            	    rightList=(DroolsTree)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_pattern_type2381); 
            	    if (list_rightList==null) list_rightList=new ArrayList();
            	    list_rightList.add(rightList);


            	    }
            	    break;

            	default :
            	    break loop67;
                }
            } while (true);


            match(input, Token.UP, null); 
            	dataType = factory.createDataType(list_idList, list_rightList);	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return dataType;
    }
    // $ANTLR end "pattern_type"


    // $ANTLR start "data_type"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:418:1: data_type returns [BaseDescr dataType] : ^( VT_DATA_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) ;
    public final BaseDescr data_type() throws RecognitionException {
        BaseDescr dataType = null;

        DroolsTree idList=null;
        DroolsTree rightList=null;
        List list_idList=null;
        List list_rightList=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:419:2: ( ^( VT_DATA_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:419:4: ^( VT_DATA_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* )
            {
            match(input,VT_DATA_TYPE,FOLLOW_VT_DATA_TYPE_in_data_type2403); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:419:25: (idList+= ID )+
            int cnt68=0;
            loop68:
            do {
                int alt68=2;
                int LA68_0 = input.LA(1);

                if ( (LA68_0==ID) ) {
                    alt68=1;
                }


                switch (alt68) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:419:25: idList+= ID
            	    {
            	    idList=(DroolsTree)match(input,ID,FOLLOW_ID_in_data_type2407); 
            	    if (list_idList==null) list_idList=new ArrayList();
            	    list_idList.add(idList);


            	    }
            	    break;

            	default :
            	    if ( cnt68 >= 1 ) break loop68;
                        EarlyExitException eee =
                            new EarlyExitException(68, input);
                        throw eee;
                }
                cnt68++;
            } while (true);

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:419:31: ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            loop69:
            do {
                int alt69=2;
                int LA69_0 = input.LA(1);

                if ( (LA69_0==LEFT_SQUARE) ) {
                    alt69=1;
                }


                switch (alt69) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:419:32: LEFT_SQUARE rightList+= RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_data_type2411); 
            	    rightList=(DroolsTree)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_data_type2415); 
            	    if (list_rightList==null) list_rightList=new ArrayList();
            	    list_rightList.add(rightList);


            	    }
            	    break;

            	default :
            	    break loop69;
                }
            } while (true);


            match(input, Token.UP, null); 
            	dataType = factory.createDataType(list_idList, list_rightList);	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return dataType;
    }
    // $ANTLR end "data_type"

    // Delegated rules


 

    public static final BitSet FOLLOW_VT_COMPILATION_UNIT_in_compilation_unit49 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_package_statement_in_compilation_unit51 = new BitSet(new long[]{0x5FFF000000000028L,0x000000000000007AL});
    public static final BitSet FOLLOW_statement_in_compilation_unit53 = new BitSet(new long[]{0x5FFF000000000028L,0x000000000000007AL});
    public static final BitSet FOLLOW_VK_PACKAGE_in_package_statement71 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_package_id_in_package_statement75 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PACKAGE_ID_in_package_id102 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_package_id106 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_rule_attribute_in_statement124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_declaration_in_statement195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VK_IMPORT_in_import_statement216 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_import_name_in_import_statement220 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FUNCTION_IMPORT_in_function_import_statement242 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_FUNCTION_in_function_import_statement244 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement248 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_IMPORT_ID_in_import_name267 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_import_name271 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000014L});
    public static final BitSet FOLLOW_DOT_STAR_in_import_name276 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_GLOBAL_in_global299 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_global303 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_VT_GLOBAL_ID_in_global307 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_FUNCTION_in_function329 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_function333 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_VT_FUNCTION_ID_in_function338 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_parameters_in_function342 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_VT_CURLY_CHUNK_in_function346 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_QUERY_in_query368 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_QUERY_ID_in_query372 = new BitSet(new long[]{0x0000200000200000L});
    public static final BitSet FOLLOW_parameters_in_query376 = new BitSet(new long[]{0x0000200000200000L});
    public static final BitSet FOLLOW_lhs_block_in_query381 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_VK_END_in_query385 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_RULE_in_rule412 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_RULE_ID_in_rule416 = new BitSet(new long[]{0x8000000000014000L,0x0000000000000000L,0x0000000000000240L});
    public static final BitSet FOLLOW_VK_EXTEND_in_rule421 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_RULE_ID_in_rule425 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_decl_metadata_in_rule435 = new BitSet(new long[]{0x0000000000014000L,0x0000000000000000L,0x0000000000000240L});
    public static final BitSet FOLLOW_rule_attributes_in_rule446 = new BitSet(new long[]{0x0000000000010000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_when_part_in_rule455 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_VT_RHS_CHUNK_in_rule460 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHEN_in_when_part479 = new BitSet(new long[]{0x0000200000200000L});
    public static final BitSet FOLLOW_lhs_block_in_when_part483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_RULE_ATTRIBUTES_in_rule_attributes505 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_ATTRIBUTES_in_rule_attributes507 = new BitSet(new long[]{0x1FFF000000000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes513 = new BitSet(new long[]{0x1FFF000000000008L});
    public static final BitSet FOLLOW_VT_PARAM_LIST_in_parameters537 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_param_definition_in_parameters542 = new BitSet(new long[]{0x0000008000000008L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_param_definition564 = new BitSet(new long[]{0x0000008000000008L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_argument_in_param_definition569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_argument589 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument592 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument596 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_VK_DECLARE_in_type_declaration623 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_TYPE_DECLARE_ID_in_type_declaration627 = new BitSet(new long[]{0x0000000000000000L,0x0000000000900001L,0x0000000000000044L});
    public static final BitSet FOLLOW_VK_EXTENDS_in_type_declaration640 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_TYPE_NAME_in_type_declaration644 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_IMPLEMENTS_in_type_declaration663 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_TYPE_NAME_in_type_declaration668 = new BitSet(new long[]{0x0000000000000808L});
    public static final BitSet FOLLOW_decl_metadata_in_type_declaration688 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L,0x0000000000000044L});
    public static final BitSet FOLLOW_decl_field_in_type_declaration701 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L,0x0000000000000004L});
    public static final BitSet FOLLOW_VK_END_in_type_declaration715 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AT_in_decl_metadata747 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_TYPE_NAME_in_decl_metadata751 = new BitSet(new long[]{0x0000000000000008L,0x8000000000000000L});
    public static final BitSet FOLLOW_decl_metadata_properties_in_decl_metadata756 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PROP_KEY_in_decl_metadata_properties791 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PROP_VALUE_in_decl_metadata_properties796 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_decl_field833 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_decl_field_initialization_in_decl_field837 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_data_type_in_decl_field842 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_decl_metadata_in_decl_field847 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_decl_field_initialization874 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_decl_field_initialization878 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_SALIENCE_in_rule_attribute901 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SIGNED_DECIMAL_in_rule_attribute906 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_rule_attribute910 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_NO_LOOP_in_rule_attribute921 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute925 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_AGENDA_GROUP_in_rule_attribute937 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute941 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_TIMER_in_rule_attribute952 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SIGNED_DECIMAL_in_rule_attribute957 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_rule_attribute961 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ACTIVATION_GROUP_in_rule_attribute974 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute978 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_AUTO_FOCUS_in_rule_attribute988 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute992 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DATE_EFFECTIVE_in_rule_attribute1003 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute1007 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DATE_EXPIRES_in_rule_attribute1017 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute1021 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ENABLED_in_rule_attribute1031 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute1036 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_rule_attribute1040 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_RULEFLOW_GROUP_in_rule_attribute1051 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute1055 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_LOCK_ON_ACTIVE_in_rule_attribute1065 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute1069 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DIALECT_in_rule_attribute1079 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute1083 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_CALENDARS_in_rule_attribute1092 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute1096 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_AND_IMPLICIT_in_lhs_block1121 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs_block1126 = new BitSet(new long[]{0x0000000143C00008L,0x0000000000006280L,0x0000000000004000L});
    public static final BitSet FOLLOW_VT_OR_PREFIX_in_lhs1152 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1157 = new BitSet(new long[]{0x0000000143C00008L,0x0000000000006280L,0x0000000000004000L});
    public static final BitSet FOLLOW_VT_OR_INFIX_in_lhs1173 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1177 = new BitSet(new long[]{0x0000000143C00008L,0x0000000000006280L,0x0000000000004000L});
    public static final BitSet FOLLOW_lhs_in_lhs1181 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_AND_PREFIX_in_lhs1193 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1198 = new BitSet(new long[]{0x0000000143C00008L,0x0000000000006280L,0x0000000000004000L});
    public static final BitSet FOLLOW_VT_AND_INFIX_in_lhs1214 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1218 = new BitSet(new long[]{0x0000000143C00008L,0x0000000000006280L,0x0000000000004000L});
    public static final BitSet FOLLOW_lhs_in_lhs1222 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EXISTS_in_lhs1234 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1238 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_NOT_in_lhs1250 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1254 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EVAL_in_lhs1266 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_lhs1270 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_FORALL_in_lhs1282 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1287 = new BitSet(new long[]{0x0000000143C00008L,0x0000000000006280L,0x0000000000004000L});
    public static final BitSet FOLLOW_VT_FOR_CE_in_lhs1303 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1307 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_for_functions_in_lhs1309 = new BitSet(new long[]{0x0000003E00080048L,0x7000000000080480L,0x0000000F9FB20820L});
    public static final BitSet FOLLOW_fact_expression_in_lhs1311 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FROM_in_lhs1322 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs1326 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000100L,0x00000000000C0000L});
    public static final BitSet FOLLOW_from_elements_in_lhs1330 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs1341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_from_elements1363 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_from_elements1367 = new BitSet(new long[]{0x000000000C000000L});
    public static final BitSet FOLLOW_accumulate_parts_in_from_elements1377 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_COLLECT_in_from_elements1390 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_from_elements1394 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ENTRY_POINT_in_from_elements1406 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_ENTRYPOINT_ID_in_from_elements1410 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_from_source_clause_in_from_elements1421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accumulate_init_clause_in_accumulate_parts1442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accumulate_id_clause_in_accumulate_parts1453 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_ACCUMULATE_INIT_CLAUSE_in_accumulate_init_clause1476 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_INIT_in_accumulate_init_clause1485 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1489 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ACTION_in_accumulate_init_clause1497 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1501 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_accumulate_init_reverse_clause_in_accumulate_init_clause1510 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_VK_RESULT_in_accumulate_init_clause1517 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1521 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_REVERSE_in_accumulate_init_reverse_clause1544 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_reverse_clause1548 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ACCUMULATE_ID_CLAUSE_in_accumulate_id_clause1570 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_accumulate_id_clause1574 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_id_clause1578 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FROM_SOURCE_in_from_source_clause1602 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_EXPRESSION_CHAIN_in_expression_chain1632 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_expression_chain1636 = new BitSet(new long[]{0x00000000200C0008L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_expression_chain1640 = new BitSet(new long[]{0x0000000020080008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_expression_chain1645 = new BitSet(new long[]{0x0000000020000008L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain1653 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PATTERN_in_lhs_pattern1671 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_lhs_pattern1675 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_over_clause_in_lhs_pattern1680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OVER_in_over_clause1705 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_over_element_in_over_clause1710 = new BitSet(new long[]{0x0000000000100008L});
    public static final BitSet FOLLOW_VT_BEHAVIOR_in_over_element1731 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_over_element1733 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_over_element1737 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_over_element1741 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FOR_FUNCTIONS_in_for_functions1770 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_for_function_in_for_functions1775 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_for_function1800 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_LABEL_in_for_function1802 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_arguments_in_for_function1804 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ARGUMENTS_in_arguments1833 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_EXPRESSION_in_arguments1838 = new BitSet(new long[]{0x0000800000000008L});
    public static final BitSet FOLLOW_VT_FACT_in_fact_expression1863 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_pattern_type_in_fact_expression1867 = new BitSet(new long[]{0x0000003E00080048L,0x7000000000080480L,0x0000000F9FB20820L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1872 = new BitSet(new long[]{0x0000003E00080048L,0x7000000000080480L,0x0000000F9FB20820L});
    public static final BitSet FOLLOW_VT_FACT_BINDING_in_fact_expression1886 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_LABEL_in_fact_expression1890 = new BitSet(new long[]{0x0000003E00080040L,0x7000000000080480L,0x0000000F9FB20820L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1894 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FACT_OR_in_fact_expression1906 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1910 = new BitSet(new long[]{0x0000003E00080040L,0x7000000000080480L,0x0000000F9FB20820L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1914 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FIELD_in_fact_expression1925 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_field_element_in_fact_expression1929 = new BitSet(new long[]{0x0000003E00080048L,0x7000000000080480L,0x0000000F9FB20820L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1933 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_BIND_FIELD_in_fact_expression1944 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_LABEL_in_fact_expression1948 = new BitSet(new long[]{0x0000003E00080040L,0x7000000000080480L,0x0000000F9FB20820L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1952 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EVAL_in_fact_expression1963 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_fact_expression1967 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EQUALS_in_fact_expression1980 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1984 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_EQUALS_in_fact_expression1996 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2000 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GREATER_in_fact_expression2012 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2016 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GREATER_EQUALS_in_fact_expression2028 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2032 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LESS_in_fact_expression2044 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2048 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LESS_EQUALS_in_fact_expression2060 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2064 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_OPERATOR_in_fact_expression2076 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression2080 = new BitSet(new long[]{0x0000003E000C0040L,0x7000000000080480L,0x0000000F9FB20820L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_fact_expression2085 = new BitSet(new long[]{0x0000003E00080040L,0x7000000000080480L,0x0000000F9FB20820L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2090 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_IN_in_fact_expression2101 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression2105 = new BitSet(new long[]{0x0000003E00080040L,0x7000000000080480L,0x0000000F9FB20820L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2111 = new BitSet(new long[]{0x0000003E00080048L,0x7000000000080480L,0x0000000F9FB20820L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_fact_expression2126 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2130 = new BitSet(new long[]{0x0000003E00080040L,0x7000000000080480L,0x0000000F9FB20820L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2134 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_fact_expression2144 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2148 = new BitSet(new long[]{0x0000003E00080040L,0x7000000000080480L,0x0000000F9FB20820L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2152 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ACCESSOR_PATH_in_fact_expression2163 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_accessor_element_in_fact_expression2168 = new BitSet(new long[]{0x0000004000000008L});
    public static final BitSet FOLLOW_STRING_in_fact_expression2183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_fact_expression2192 = new BitSet(new long[]{0x0000000000000000L,0x7000000000000000L,0x0000000C00020000L});
    public static final BitSet FOLLOW_MINUS_in_fact_expression2196 = new BitSet(new long[]{0x0000000000000000L,0x7000000000000000L,0x0000000C00020000L});
    public static final BitSet FOLLOW_DECIMAL_in_fact_expression2215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIGNED_DECIMAL_in_fact_expression2219 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HEX_in_fact_expression2231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIGNED_HEX_in_fact_expression2235 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_fact_expression2249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIGNED_FLOAT_in_fact_expression2253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_fact_expression2269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_fact_expression2279 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_fact_expression2289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_ACCESSOR_PATH_in_field_element2311 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_accessor_element_in_field_element2316 = new BitSet(new long[]{0x0000004000000008L});
    public static final BitSet FOLLOW_VT_ACCESSOR_ELEMENT_in_accessor_element2340 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_accessor_element2344 = new BitSet(new long[]{0x0000000000040008L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_accessor_element2348 = new BitSet(new long[]{0x0000000000040008L});
    public static final BitSet FOLLOW_VT_PATTERN_TYPE_in_pattern_type2369 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_pattern_type2373 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000020000004L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_pattern_type2377 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_pattern_type2381 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_VT_DATA_TYPE_in_data_type2403 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_data_type2407 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000020000004L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_data_type2411 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_data_type2415 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000020000000L});

}