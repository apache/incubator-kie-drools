// $ANTLR 3.2 Sep 23, 2009 12:02:23 /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g 2010-11-10 01:08:26

	package org.drools.lang;

	import java.util.HashMap;
	import java.util.Map;
	import java.util.Hashtable;
	import java.util.LinkedList;
	import org.drools.lang.descr.AccessorDescr;
	import org.drools.lang.descr.AccumulateDescr;
	import org.drools.lang.descr.AndDescr;
	import org.drools.lang.descr.AttributeDescr;
	import org.drools.lang.descr.BaseDescr;
	import org.drools.lang.descr.BehaviorDescr;
	import org.drools.lang.descr.DeclarativeInvokerDescr;
	import org.drools.lang.descr.DescrFactory;
	import org.drools.lang.descr.FactTemplateDescr;
	import org.drools.lang.descr.FieldConstraintDescr;
	import org.drools.lang.descr.FieldTemplateDescr;
	import org.drools.lang.descr.FromDescr;
	import org.drools.lang.descr.FunctionDescr;
	import org.drools.lang.descr.FunctionImportDescr;
	import org.drools.lang.descr.GlobalDescr;
	import org.drools.lang.descr.ImportDescr;
	import org.drools.lang.descr.PackageDescr;
	import org.drools.lang.descr.PatternSourceDescr;
	import org.drools.lang.descr.QueryDescr;
	import org.drools.lang.descr.RuleDescr;
	import org.drools.lang.descr.TypeDeclarationDescr;
	import org.drools.lang.descr.TypeFieldDescr;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class DescrBuilderTree extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_COMPILATION_UNIT", "VT_FUNCTION_IMPORT", "VT_FACT", "VT_CONSTRAINTS", "VT_LABEL", "VT_QUERY_ID", "VT_TYPE_DECLARE_ID", "VT_TYPE_NAME", "VT_RULE_ID", "VT_ENTRYPOINT_ID", "VT_RULE_ATTRIBUTES", "VT_PKG_ATTRIBUTES", "VT_RHS_CHUNK", "VT_CURLY_CHUNK", "VT_SQUARE_CHUNK", "VT_PAREN_CHUNK", "VT_BEHAVIOR", "VT_AND_IMPLICIT", "VT_AND_PREFIX", "VT_OR_PREFIX", "VT_AND_INFIX", "VT_OR_INFIX", "VT_ACCUMULATE_INIT_CLAUSE", "VT_ACCUMULATE_ID_CLAUSE", "VT_FROM_SOURCE", "VT_EXPRESSION_CHAIN", "VT_PATTERN", "VT_FACT_BINDING", "VT_FACT_OR", "VT_BIND_FIELD", "VT_FIELD", "VT_ACCESSOR_PATH", "VT_ACCESSOR_ELEMENT", "VT_DATA_TYPE", "VT_PATTERN_TYPE", "VT_PACKAGE_ID", "VT_IMPORT_ID", "VT_GLOBAL_ID", "VT_FUNCTION_ID", "VT_PARAM_LIST", "VK_DATE_EFFECTIVE", "VK_DATE_EXPIRES", "VK_LOCK_ON_ACTIVE", "VK_NO_LOOP", "VK_AUTO_FOCUS", "VK_ACTIVATION_GROUP", "VK_AGENDA_GROUP", "VK_RULEFLOW_GROUP", "VK_TIMER", "VK_CALENDARS", "VK_DIALECT", "VK_SALIENCE", "VK_ENABLED", "VK_ATTRIBUTES", "VK_RULE", "VK_EXTEND", "VK_IMPLEMENTS", "VK_IMPORT", "VK_PACKAGE", "VK_QUERY", "VK_DECLARE", "VK_FUNCTION", "VK_GLOBAL", "VK_EVAL", "VK_ENTRY_POINT", "VK_NOT", "VK_IN", "VK_OR", "VK_AND", "VK_EXISTS", "VK_FORALL", "VK_ACTION", "VK_REVERSE", "VK_RESULT", "VK_OPERATOR", "VK_END", "VK_INIT", "VK_INSTANCEOF", "VK_EXTENDS", "VK_SUPER", "VK_PRIMITIVE_TYPE", "VK_THIS", "VK_VOID", "VK_CLASS", "VK_NEW", "VK_FINAL", "VK_IF", "VK_ELSE", "VK_FOR", "VK_WHILE", "VK_DO", "VK_CASE", "VK_DEFAULT", "VK_TRY", "VK_CATCH", "VK_FINALLY", "VK_SWITCH", "VK_SYNCHRONIZED", "VK_RETURN", "VK_THROW", "VK_BREAK", "VK_CONTINUE", "VK_ASSERT", "VK_MODIFY", "VK_STATIC", "VK_PUBLIC", "VK_PROTECTED", "VK_PRIVATE", "VK_ABSTRACT", "VK_NATIVE", "VK_TRANSIENT", "VK_VOLATILE", "VK_STRICTFP", "VK_THROWS", "VK_INTERFACE", "VK_ENUM", "SIGNED_DECIMAL", "SIGNED_HEX", "SIGNED_FLOAT", "VT_PROP_KEY", "VT_PROP_VALUE", "SEMICOLON", "ID", "DOT", "DOT_STAR", "STRING", "COLON", "EQUALS_ASSIGN", "WHEN", "COMMA", "BOOL", "LEFT_PAREN", "RIGHT_PAREN", "FROM", "OVER", "TimePeriod", "DECIMAL", "ACCUMULATE", "COLLECT", "DOUBLE_PIPE", "DOUBLE_AMPER", "ARROW", "EQUALS", "GREATER", "GREATER_EQUALS", "LESS", "LESS_EQUALS", "NOT_EQUALS", "LEFT_SQUARE", "RIGHT_SQUARE", "NULL", "PLUS", "MINUS", "HEX", "FLOAT", "THEN", "LEFT_CURLY", "RIGHT_CURLY", "QUESTION", "PIPE", "XOR", "AMPER", "SHIFT_LEFT", "SHIFT_RIGHT_UNSIG", "SHIFT_RIGHT", "STAR", "DIV", "MOD", "INCR", "DECR", "TILDE", "NEGATION", "PLUS_ASSIGN", "MINUS_ASSIGN", "MULT_ASSIGN", "DIV_ASSIGN", "AND_ASSIGN", "OR_ASSIGN", "XOR_ASSIGN", "MOD_ASSIGN", "AT", "EOL", "WS", "Exponent", "FloatTypeSuffix", "HexDigit", "IntegerTypeSuffix", "EscapeSequence", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "IdentifierStart", "IdentifierPart", "MISC"
    };
    public static final int VT_ACCESSOR_ELEMENT=36;
    public static final int VK_CASE=95;
    public static final int ACCUMULATE=141;
    public static final int VK_TRY=97;
    public static final int VT_DATA_TYPE=37;
    public static final int STAR=169;
    public static final int MOD=171;
    public static final int VK_OPERATOR=78;
    public static final int VK_FUNCTION=65;
    public static final int VK_GLOBAL=66;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=194;
    public static final int VK_SALIENCE=55;
    public static final int EOF=-1;
    public static final int EOL=185;
    public static final int VK_MODIFY=107;
    public static final int VK_CLASS=87;
    public static final int VK_ACTION=75;
    public static final int TimePeriod=139;
    public static final int VK_INTERFACE=118;
    public static final int SIGNED_FLOAT=122;
    public static final int VK_FINAL=89;
    public static final int VK_OR=71;
    public static final int IntegerTypeSuffix=190;
    public static final int VK_NEW=88;
    public static final int MINUS_ASSIGN=177;
    public static final int BOOL=134;
    public static final int SEMICOLON=125;
    public static final int VT_FUNCTION_ID=42;
    public static final int SHIFT_RIGHT_UNSIG=167;
    public static final int VK_RULEFLOW_GROUP=51;
    public static final int VK_RESULT=77;
    public static final int VT_LABEL=8;
    public static final int WS=186;
    public static final int VK_NO_LOOP=47;
    public static final int VK_STRICTFP=116;
    public static final int VT_FACT=6;
    public static final int LEFT_CURLY=160;
    public static final int VT_TYPE_NAME=11;
    public static final int LEFT_PAREN=135;
    public static final int VK_ASSERT=106;
    public static final int VT_RULE_ID=12;
    public static final int VK_ACTIVATION_GROUP=49;
    public static final int VK_DEFAULT=96;
    public static final int VK_VOID=86;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=195;
    public static final int VK_EXTEND=59;
    public static final int VT_FACT_OR=32;
    public static final int FLOAT=158;
    public static final int VT_ACCUMULATE_ID_CLAUSE=27;
    public static final int NOT_EQUALS=151;
    public static final int HexDigit=189;
    public static final int VK_EVAL=67;
    public static final int AT=184;
    public static final int DOUBLE_PIPE=143;
    public static final int RIGHT_PAREN=136;
    public static final int THEN=159;
    public static final int VT_COMPILATION_UNIT=4;
    public static final int VT_EXPRESSION_CHAIN=29;
    public static final int GREATER_EQUALS=148;
    public static final int XOR_ASSIGN=182;
    public static final int VK_ABSTRACT=112;
    public static final int PIPE=163;
    public static final int PLUS=155;
    public static final int VK_DIALECT=54;
    public static final int VT_GLOBAL_ID=41;
    public static final int VK_NOT=69;
    public static final int VT_FROM_SOURCE=28;
    public static final int VK_ENUM=119;
    public static final int VT_ENTRYPOINT_ID=13;
    public static final int VK_DECLARE=64;
    public static final int VK_INSTANCEOF=81;
    public static final int VT_TYPE_DECLARE_ID=10;
    public static final int VK_REVERSE=76;
    public static final int VK_LOCK_ON_ACTIVE=46;
    public static final int VK_PUBLIC=109;
    public static final int SIGNED_HEX=121;
    public static final int VK_THIS=85;
    public static final int VT_RULE_ATTRIBUTES=14;
    public static final int VK_SYNCHRONIZED=101;
    public static final int MINUS=156;
    public static final int VT_ACCESSOR_PATH=35;
    public static final int MULTI_LINE_COMMENT=196;
    public static final int COLON=130;
    public static final int OR_ASSIGN=181;
    public static final int VT_AND_IMPLICIT=21;
    public static final int VK_THROW=103;
    public static final int NEGATION=175;
    public static final int DECIMAL=140;
    public static final int VK_ATTRIBUTES=57;
    public static final int WHEN=132;
    public static final int PLUS_ASSIGN=176;
    public static final int VT_PARAM_LIST=43;
    public static final int ARROW=145;
    public static final int VT_BIND_FIELD=33;
    public static final int DIV=170;
    public static final int VK_AGENDA_GROUP=50;
    public static final int VT_CONSTRAINTS=7;
    public static final int OctalEscape=193;
    public static final int VK_NATIVE=113;
    public static final int STRING=129;
    public static final int VK_THROWS=117;
    public static final int FloatTypeSuffix=188;
    public static final int EQUALS_ASSIGN=131;
    public static final int VK_VOLATILE=115;
    public static final int DOT_STAR=128;
    public static final int MOD_ASSIGN=183;
    public static final int VK_AND=72;
    public static final int VT_PROP_VALUE=124;
    public static final int EQUALS=146;
    public static final int VK_AUTO_FOCUS=48;
    public static final int VK_CONTINUE=105;
    public static final int DIV_ASSIGN=179;
    public static final int VT_PATTERN_TYPE=38;
    public static final int VT_FUNCTION_IMPORT=5;
    public static final int VT_OR_INFIX=25;
    public static final int DOUBLE_AMPER=144;
    public static final int GREATER=147;
    public static final int VK_IMPORT=61;
    public static final int INCR=172;
    public static final int VK_RULE=58;
    public static final int LESS=149;
    public static final int VT_AND_PREFIX=22;
    public static final int VK_SWITCH=100;
    public static final int VK_BREAK=104;
    public static final int NULL=154;
    public static final int VK_RETURN=102;
    public static final int IdentifierStart=197;
    public static final int VK_PRIMITIVE_TYPE=84;
    public static final int VK_QUERY=63;
    public static final int VT_RHS_CHUNK=16;
    public static final int VT_FACT_BINDING=31;
    public static final int VK_ENTRY_POINT=68;
    public static final int SHIFT_LEFT=166;
    public static final int VT_PACKAGE_ID=39;
    public static final int VK_IMPLEMENTS=60;
    public static final int SHIFT_RIGHT=168;
    public static final int VK_CALENDARS=53;
    public static final int VK_IF=90;
    public static final int VK_STATIC=108;
    public static final int VK_CATCH=98;
    public static final int VK_IN=70;
    public static final int VT_PATTERN=30;
    public static final int SIGNED_DECIMAL=120;
    public static final int VT_IMPORT_ID=40;
    public static final int MISC=199;
    public static final int VK_PRIVATE=111;
    public static final int FROM=137;
    public static final int COLLECT=142;
    public static final int EscapeSequence=191;
    public static final int VK_ENABLED=56;
    public static final int VK_WHILE=93;
    public static final int VK_END=79;
    public static final int VK_PACKAGE=62;
    public static final int OVER=138;
    public static final int RIGHT_SQUARE=153;
    public static final int RIGHT_CURLY=161;
    public static final int VK_ELSE=91;
    public static final int VK_FOR=92;
    public static final int VK_PROTECTED=110;
    public static final int VT_PROP_KEY=123;
    public static final int MULT_ASSIGN=178;
    public static final int VT_FIELD=34;
    public static final int VK_FINALLY=99;
    public static final int Exponent=187;
    public static final int VK_SUPER=83;
    public static final int VK_EXISTS=73;
    public static final int VK_DATE_EXPIRES=45;
    public static final int ID=126;
    public static final int AND_ASSIGN=180;
    public static final int VK_TIMER=52;
    public static final int VT_PKG_ATTRIBUTES=15;
    public static final int VT_OR_PREFIX=23;
    public static final int LESS_EQUALS=150;
    public static final int VK_TRANSIENT=114;
    public static final int COMMA=133;
    public static final int HEX=157;
    public static final int AMPER=165;
    public static final int TILDE=174;
    public static final int VT_BEHAVIOR=20;
    public static final int VT_QUERY_ID=9;
    public static final int DOT=127;
    public static final int IdentifierPart=198;
    public static final int VT_PAREN_CHUNK=19;
    public static final int XOR=164;
    public static final int VK_EXTENDS=82;
    public static final int VT_AND_INFIX=24;
    public static final int VK_FORALL=74;
    public static final int VK_DO=94;
    public static final int VT_ACCUMULATE_INIT_CLAUSE=26;
    public static final int VT_CURLY_CHUNK=17;
    public static final int QUESTION=162;
    public static final int UnicodeEscape=192;
    public static final int VT_SQUARE_CHUNK=18;
    public static final int DECR=173;
    public static final int VK_DATE_EFFECTIVE=44;
    public static final int LEFT_SQUARE=152;
    public static final int VK_INIT=80;

    // delegates
    // delegators


        public DescrBuilderTree(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public DescrBuilderTree(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return DescrBuilderTree.tokenNames; }
    public String getGrammarFileName() { return "/home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g"; }


    	DescrFactory factory = new DescrFactory();
    	PackageDescr packageDescr = null;
    	
    	public PackageDescr getPackageDescr() {
    		return packageDescr;
    	}



    // $ANTLR start "compilation_unit"
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:49:1: compilation_unit : ^( VT_COMPILATION_UNIT package_statement ( statement )* ) ;
    public final void compilation_unit() throws RecognitionException {
        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:50:2: ( ^( VT_COMPILATION_UNIT package_statement ( statement )* ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:50:4: ^( VT_COMPILATION_UNIT package_statement ( statement )* )
            {
            match(input,VT_COMPILATION_UNIT,FOLLOW_VT_COMPILATION_UNIT_in_compilation_unit49); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                pushFollow(FOLLOW_package_statement_in_compilation_unit51);
                package_statement();

                state._fsp--;

                // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:50:44: ( statement )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==VT_FUNCTION_IMPORT||(LA1_0>=VK_DATE_EFFECTIVE && LA1_0<=VK_ENABLED)||LA1_0==VK_RULE||LA1_0==VK_IMPORT||(LA1_0>=VK_QUERY && LA1_0<=VK_GLOBAL)) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:50:44: statement
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:53:1: package_statement returns [String packageName] : ( ^( VK_PACKAGE packageId= package_id ) | );
    public final String package_statement() throws RecognitionException {
        String packageName = null;

        List packageId = null;


        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:54:2: ( ^( VK_PACKAGE packageId= package_id ) | )
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
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:54:4: ^( VK_PACKAGE packageId= package_id )
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
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:58:2: 
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:62:1: package_id returns [List idList] : ^( VT_PACKAGE_ID (tempList+= ID )+ ) ;
    public final List package_id() throws RecognitionException {
        List idList = null;

        DroolsTree tempList=null;
        List list_tempList=null;

        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:63:2: ( ^( VT_PACKAGE_ID (tempList+= ID )+ ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:63:4: ^( VT_PACKAGE_ID (tempList+= ID )+ )
            {
            match(input,VT_PACKAGE_ID,FOLLOW_VT_PACKAGE_ID_in_package_id102); 

            match(input, Token.DOWN, null); 
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:63:28: (tempList+= ID )+
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
            	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:63:28: tempList+= ID
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:67:1: statement : (a= rule_attribute | fi= function_import_statement | is= import_statement | gl= global | fn= function | rl= rule | qr= query | td= type_declaration );
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
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:68:2: (a= rule_attribute | fi= function_import_statement | is= import_statement | gl= global | fn= function | rl= rule | qr= query | td= type_declaration )
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
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:68:4: a= rule_attribute
                    {
                    pushFollow(FOLLOW_rule_attribute_in_statement124);
                    a=rule_attribute();

                    state._fsp--;

                    	this.packageDescr.addAttribute(a);	

                    }
                    break;
                case 2 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:70:4: fi= function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement134);
                    fi=function_import_statement();

                    state._fsp--;

                    	this.packageDescr.addFunctionImport(fi);	

                    }
                    break;
                case 3 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:72:4: is= import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement144);
                    is=import_statement();

                    state._fsp--;

                    	this.packageDescr.addImport(is);	

                    }
                    break;
                case 4 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:74:4: gl= global
                    {
                    pushFollow(FOLLOW_global_in_statement155);
                    gl=global();

                    state._fsp--;

                    	this.packageDescr.addGlobal((gl!=null?gl.globalDescr:null));	

                    }
                    break;
                case 5 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:76:4: fn= function
                    {
                    pushFollow(FOLLOW_function_in_statement165);
                    fn=function();

                    state._fsp--;

                    	this.packageDescr.addFunction((fn!=null?fn.functionDescr:null));	

                    }
                    break;
                case 6 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:78:4: rl= rule
                    {
                    pushFollow(FOLLOW_rule_in_statement175);
                    rl=rule();

                    state._fsp--;

                    	this.packageDescr.addRule((rl!=null?rl.ruleDescr:null));	

                    }
                    break;
                case 7 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:80:4: qr= query
                    {
                    pushFollow(FOLLOW_query_in_statement185);
                    qr=query();

                    state._fsp--;

                    	this.packageDescr.addRule((qr!=null?qr.queryDescr:null));	

                    }
                    break;
                case 8 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:82:4: td= type_declaration
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:86:1: import_statement returns [ImportDescr importDescr] : ^(importStart= VK_IMPORT importId= import_name ) ;
    public final ImportDescr import_statement() throws RecognitionException {
        ImportDescr importDescr = null;

        DroolsTree importStart=null;
        DescrBuilderTree.import_name_return importId = null;


        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:87:2: ( ^(importStart= VK_IMPORT importId= import_name ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:87:4: ^(importStart= VK_IMPORT importId= import_name )
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:91:1: function_import_statement returns [FunctionImportDescr functionImportDescr] : ^(importStart= VT_FUNCTION_IMPORT VK_FUNCTION importId= import_name ) ;
    public final FunctionImportDescr function_import_statement() throws RecognitionException {
        FunctionImportDescr functionImportDescr = null;

        DroolsTree importStart=null;
        DescrBuilderTree.import_name_return importId = null;


        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:92:2: ( ^(importStart= VT_FUNCTION_IMPORT VK_FUNCTION importId= import_name ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:92:4: ^(importStart= VT_FUNCTION_IMPORT VK_FUNCTION importId= import_name )
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:96:1: import_name returns [List idList, DroolsTree dotStar] : ^( VT_IMPORT_ID (tempList+= ID )+ (tempDotStar= DOT_STAR )? ) ;
    public final DescrBuilderTree.import_name_return import_name() throws RecognitionException {
        DescrBuilderTree.import_name_return retval = new DescrBuilderTree.import_name_return();
        retval.start = input.LT(1);

        DroolsTree tempDotStar=null;
        DroolsTree tempList=null;
        List list_tempList=null;

        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:97:2: ( ^( VT_IMPORT_ID (tempList+= ID )+ (tempDotStar= DOT_STAR )? ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:97:4: ^( VT_IMPORT_ID (tempList+= ID )+ (tempDotStar= DOT_STAR )? )
            {
            match(input,VT_IMPORT_ID,FOLLOW_VT_IMPORT_ID_in_import_name267); 

            match(input, Token.DOWN, null); 
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:97:27: (tempList+= ID )+
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
            	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:97:27: tempList+= ID
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

            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:97:44: (tempDotStar= DOT_STAR )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==DOT_STAR) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:97:44: tempDotStar= DOT_STAR
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:102:1: global returns [GlobalDescr globalDescr] : ^(start= VK_GLOBAL dt= data_type globalId= VT_GLOBAL_ID ) ;
    public final DescrBuilderTree.global_return global() throws RecognitionException {
        DescrBuilderTree.global_return retval = new DescrBuilderTree.global_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree globalId=null;
        BaseDescr dt = null;


        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:103:2: ( ^(start= VK_GLOBAL dt= data_type globalId= VT_GLOBAL_ID ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:103:4: ^(start= VK_GLOBAL dt= data_type globalId= VT_GLOBAL_ID )
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:107:1: function returns [FunctionDescr functionDescr] : ^(start= VK_FUNCTION (dt= data_type )? functionId= VT_FUNCTION_ID params= parameters content= VT_CURLY_CHUNK ) ;
    public final DescrBuilderTree.function_return function() throws RecognitionException {
        DescrBuilderTree.function_return retval = new DescrBuilderTree.function_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree functionId=null;
        DroolsTree content=null;
        BaseDescr dt = null;

        List params = null;


        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:108:2: ( ^(start= VK_FUNCTION (dt= data_type )? functionId= VT_FUNCTION_ID params= parameters content= VT_CURLY_CHUNK ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:108:4: ^(start= VK_FUNCTION (dt= data_type )? functionId= VT_FUNCTION_ID params= parameters content= VT_CURLY_CHUNK )
            {
            start=(DroolsTree)match(input,VK_FUNCTION,FOLLOW_VK_FUNCTION_in_function329); 

            match(input, Token.DOWN, null); 
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:108:26: (dt= data_type )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==VT_DATA_TYPE) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:108:26: dt= data_type
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:112:1: query returns [QueryDescr queryDescr] : ^(start= VK_QUERY id= VT_QUERY_ID (params= parameters )? lb= lhs_block end= VK_END ) ;
    public final DescrBuilderTree.query_return query() throws RecognitionException {
        DescrBuilderTree.query_return retval = new DescrBuilderTree.query_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree id=null;
        DroolsTree end=null;
        List params = null;

        AndDescr lb = null;


        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:113:2: ( ^(start= VK_QUERY id= VT_QUERY_ID (params= parameters )? lb= lhs_block end= VK_END ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:113:4: ^(start= VK_QUERY id= VT_QUERY_ID (params= parameters )? lb= lhs_block end= VK_END )
            {
            start=(DroolsTree)match(input,VK_QUERY,FOLLOW_VK_QUERY_in_query368); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,VT_QUERY_ID,FOLLOW_VT_QUERY_ID_in_query372); 
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:113:42: (params= parameters )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==VT_PARAM_LIST) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:113:42: params= parameters
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:117:1: rule returns [RuleDescr ruleDescr] : ^(start= VK_RULE id= VT_RULE_ID ( ^( VK_EXTEND parent_id= VT_RULE_ID ) )? (dm= decl_metadata )* (ra= rule_attributes )? (wn= when_part )? content= VT_RHS_CHUNK ) ;
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
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:119:2: ( ^(start= VK_RULE id= VT_RULE_ID ( ^( VK_EXTEND parent_id= VT_RULE_ID ) )? (dm= decl_metadata )* (ra= rule_attributes )? (wn= when_part )? content= VT_RHS_CHUNK ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:119:4: ^(start= VK_RULE id= VT_RULE_ID ( ^( VK_EXTEND parent_id= VT_RULE_ID ) )? (dm= decl_metadata )* (ra= rule_attributes )? (wn= when_part )? content= VT_RHS_CHUNK )
            {
            start=(DroolsTree)match(input,VK_RULE,FOLLOW_VK_RULE_in_rule412); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,VT_RULE_ID,FOLLOW_VT_RULE_ID_in_rule416); 
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:119:35: ( ^( VK_EXTEND parent_id= VT_RULE_ID ) )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==VK_EXTEND) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:119:36: ^( VK_EXTEND parent_id= VT_RULE_ID )
                    {
                    match(input,VK_EXTEND,FOLLOW_VK_EXTEND_in_rule421); 

                    match(input, Token.DOWN, null); 
                    parent_id=(DroolsTree)match(input,VT_RULE_ID,FOLLOW_VT_RULE_ID_in_rule425); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }

            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:120:3: (dm= decl_metadata )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==AT) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:120:4: dm= decl_metadata
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

            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:121:6: (ra= rule_attributes )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==VT_RULE_ATTRIBUTES) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:121:6: ra= rule_attributes
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule446);
                    ra=rule_attributes();

                    state._fsp--;


                    }
                    break;

            }

            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:122:6: (wn= when_part )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==WHEN) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:122:6: wn= when_part
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:126:1: when_part returns [AndDescr andDescr] : WHEN lh= lhs_block ;
    public final AndDescr when_part() throws RecognitionException {
        AndDescr andDescr = null;

        AndDescr lh = null;


        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:127:2: ( WHEN lh= lhs_block )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:127:4: WHEN lh= lhs_block
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:131:1: rule_attributes returns [List attrList] : ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? (rl= rule_attribute )+ ) ;
    public final List rule_attributes() throws RecognitionException {
        List attrList = null;

        AttributeDescr rl = null;



        	attrList = new LinkedList<AttributeDescr>();

        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:134:3: ( ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? (rl= rule_attribute )+ ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:134:5: ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? (rl= rule_attribute )+ )
            {
            match(input,VT_RULE_ATTRIBUTES,FOLLOW_VT_RULE_ATTRIBUTES_in_rule_attributes505); 

            match(input, Token.DOWN, null); 
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:134:26: ( VK_ATTRIBUTES )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==VK_ATTRIBUTES) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:134:26: VK_ATTRIBUTES
                    {
                    match(input,VK_ATTRIBUTES,FOLLOW_VK_ATTRIBUTES_in_rule_attributes507); 

                    }
                    break;

            }

            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:134:41: (rl= rule_attribute )+
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
            	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:134:42: rl= rule_attribute
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:137:1: parameters returns [List paramList] : ^( VT_PARAM_LIST (p= param_definition )* ) ;
    public final List parameters() throws RecognitionException {
        List paramList = null;

        Map p = null;



        	paramList = new LinkedList<Map<BaseDescr, BaseDescr>>();

        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:140:3: ( ^( VT_PARAM_LIST (p= param_definition )* ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:140:5: ^( VT_PARAM_LIST (p= param_definition )* )
            {
            match(input,VT_PARAM_LIST,FOLLOW_VT_PARAM_LIST_in_parameters537); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:140:21: (p= param_definition )*
                loop15:
                do {
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==VT_DATA_TYPE||LA15_0==ID) ) {
                        alt15=1;
                    }


                    switch (alt15) {
                	case 1 :
                	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:140:22: p= param_definition
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:143:1: param_definition returns [Map param] : (dt= data_type )? a= argument ;
    public final Map param_definition() throws RecognitionException {
        Map param = null;

        BaseDescr dt = null;

        BaseDescr a = null;


        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:144:2: ( (dt= data_type )? a= argument )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:144:4: (dt= data_type )? a= argument
            {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:144:6: (dt= data_type )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==VT_DATA_TYPE) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:144:6: dt= data_type
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:149:1: argument returns [BaseDescr arg] : id= ID ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ;
    public final BaseDescr argument() throws RecognitionException {
        BaseDescr arg = null;

        DroolsTree id=null;
        DroolsTree rightList=null;
        List list_rightList=null;

        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:150:2: (id= ID ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:150:4: id= ID ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            {
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_argument589); 
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:150:10: ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==LEFT_SQUARE) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:150:11: LEFT_SQUARE rightList+= RIGHT_SQUARE
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:154:1: type_declaration returns [TypeDeclarationDescr declaration] : ^( VK_DECLARE id= VT_TYPE_DECLARE_ID ( ^( VK_EXTENDS ext= VT_TYPE_NAME ) )? ( ^( VK_IMPLEMENTS (intf= VT_TYPE_NAME )+ ) )? (dm= decl_metadata )* (df= decl_field )* VK_END ) ;
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
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:159:2: ( ^( VK_DECLARE id= VT_TYPE_DECLARE_ID ( ^( VK_EXTENDS ext= VT_TYPE_NAME ) )? ( ^( VK_IMPLEMENTS (intf= VT_TYPE_NAME )+ ) )? (dm= decl_metadata )* (df= decl_field )* VK_END ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:159:4: ^( VK_DECLARE id= VT_TYPE_DECLARE_ID ( ^( VK_EXTENDS ext= VT_TYPE_NAME ) )? ( ^( VK_IMPLEMENTS (intf= VT_TYPE_NAME )+ ) )? (dm= decl_metadata )* (df= decl_field )* VK_END )
            {
            match(input,VK_DECLARE,FOLLOW_VK_DECLARE_in_type_declaration623); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,VT_TYPE_DECLARE_ID,FOLLOW_VT_TYPE_DECLARE_ID_in_type_declaration627); 
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:160:4: ( ^( VK_EXTENDS ext= VT_TYPE_NAME ) )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==VK_EXTENDS) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:160:10: ^( VK_EXTENDS ext= VT_TYPE_NAME )
                    {
                    match(input,VK_EXTENDS,FOLLOW_VK_EXTENDS_in_type_declaration640); 

                    match(input, Token.DOWN, null); 
                    ext=(DroolsTree)match(input,VT_TYPE_NAME,FOLLOW_VT_TYPE_NAME_in_type_declaration644); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }

            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:161:4: ( ^( VK_IMPLEMENTS (intf= VT_TYPE_NAME )+ ) )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==VK_IMPLEMENTS) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:161:10: ^( VK_IMPLEMENTS (intf= VT_TYPE_NAME )+ )
                    {
                    match(input,VK_IMPLEMENTS,FOLLOW_VK_IMPLEMENTS_in_type_declaration663); 

                    match(input, Token.DOWN, null); 
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:161:26: (intf= VT_TYPE_NAME )+
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
                    	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:161:27: intf= VT_TYPE_NAME
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

            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:162:4: (dm= decl_metadata )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==AT) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:162:5: dm= decl_metadata
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

            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:163:4: (df= decl_field )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==ID) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:163:5: df= decl_field
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:175:1: decl_metadata returns [Map attData] : ^( AT att= VT_TYPE_NAME (p= decl_metadata_properties )? ) ;
    public final Map decl_metadata() throws RecognitionException {
        Map attData = null;

        DroolsTree att=null;
        Hashtable p = null;


        attData = new HashMap();
        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:177:2: ( ^( AT att= VT_TYPE_NAME (p= decl_metadata_properties )? ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:177:4: ^( AT att= VT_TYPE_NAME (p= decl_metadata_properties )? )
            {
            match(input,AT,FOLLOW_AT_in_decl_metadata747); 

            match(input, Token.DOWN, null); 
            att=(DroolsTree)match(input,VT_TYPE_NAME,FOLLOW_VT_TYPE_NAME_in_decl_metadata751); 
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:177:26: (p= decl_metadata_properties )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==VT_PROP_KEY) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:177:27: p= decl_metadata_properties
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:181:1: decl_metadata_properties returns [Hashtable props] : ^(key= VT_PROP_KEY (val= VT_PROP_VALUE )? ) ;
    public final Hashtable decl_metadata_properties() throws RecognitionException {
        Hashtable props = null;

        DroolsTree key=null;
        DroolsTree val=null;

        props = new Hashtable();
        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:183:2: ( ^(key= VT_PROP_KEY (val= VT_PROP_VALUE )? ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:183:4: ^(key= VT_PROP_KEY (val= VT_PROP_VALUE )? )
            {
            key=(DroolsTree)match(input,VT_PROP_KEY,FOLLOW_VT_PROP_KEY_in_decl_metadata_properties786); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:183:22: (val= VT_PROP_VALUE )?
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==VT_PROP_VALUE) ) {
                    alt24=1;
                }
                switch (alt24) {
                    case 1 :
                        // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:183:23: val= VT_PROP_VALUE
                        {
                        val=(DroolsTree)match(input,VT_PROP_VALUE,FOLLOW_VT_PROP_VALUE_in_decl_metadata_properties791); 

                        }
                        break;

                }


                match(input, Token.UP, null); 
            }
             props.put((key!=null?key.getText():null),val == null ? (key!=null?key.getText():null) : (val!=null?val.getText():null) ); 

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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:187:1: decl_field returns [TypeFieldDescr fieldDescr] : ^(id= ID (init= decl_field_initialization )? dt= data_type (dm= decl_metadata )* ) ;
    public final TypeFieldDescr decl_field() throws RecognitionException {
        TypeFieldDescr fieldDescr = null;

        DroolsTree id=null;
        String init = null;

        BaseDescr dt = null;

        Map dm = null;


        List<Map> declMetadaList = new LinkedList<Map>(); 
        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:189:2: ( ^(id= ID (init= decl_field_initialization )? dt= data_type (dm= decl_metadata )* ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:189:4: ^(id= ID (init= decl_field_initialization )? dt= data_type (dm= decl_metadata )* )
            {
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_decl_field821); 

            match(input, Token.DOWN, null); 
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:189:16: (init= decl_field_initialization )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==EQUALS_ASSIGN) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:189:16: init= decl_field_initialization
                    {
                    pushFollow(FOLLOW_decl_field_initialization_in_decl_field825);
                    init=decl_field_initialization();

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_data_type_in_decl_field830);
            dt=data_type();

            state._fsp--;

            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:189:57: (dm= decl_metadata )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==AT) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:189:58: dm= decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_decl_field835);
            	    dm=decl_metadata();

            	    state._fsp--;

            	    declMetadaList.add(dm);

            	    }
            	    break;

            	default :
            	    break loop26;
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:193:1: decl_field_initialization returns [String expr] : ^( EQUALS_ASSIGN pc= VT_PAREN_CHUNK ) ;
    public final String decl_field_initialization() throws RecognitionException {
        String expr = null;

        DroolsTree pc=null;

        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:194:2: ( ^( EQUALS_ASSIGN pc= VT_PAREN_CHUNK ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:194:4: ^( EQUALS_ASSIGN pc= VT_PAREN_CHUNK )
            {
            match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_decl_field_initialization862); 

            match(input, Token.DOWN, null); 
            pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_decl_field_initialization866); 

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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:198:1: rule_attribute returns [AttributeDescr attributeDescr] : ( ^(attrName= VK_SALIENCE (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_NO_LOOP (value= BOOL )? ) | ^(attrName= VK_AGENDA_GROUP value= STRING ) | ^(attrName= VK_TIMER (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_ACTIVATION_GROUP value= STRING ) | ^(attrName= VK_AUTO_FOCUS (value= BOOL )? ) | ^(attrName= VK_DATE_EFFECTIVE value= STRING ) | ^(attrName= VK_DATE_EXPIRES value= STRING ) | ^(attrName= VK_ENABLED (value= BOOL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_RULEFLOW_GROUP value= STRING ) | ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? ) | ^(attrName= VK_DIALECT value= STRING ) | ^(attrName= VK_CALENDARS value= STRING ) ) ;
    public final AttributeDescr rule_attribute() throws RecognitionException {
        AttributeDescr attributeDescr = null;

        DroolsTree attrName=null;
        DroolsTree value=null;

        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:199:2: ( ( ^(attrName= VK_SALIENCE (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_NO_LOOP (value= BOOL )? ) | ^(attrName= VK_AGENDA_GROUP value= STRING ) | ^(attrName= VK_TIMER (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_ACTIVATION_GROUP value= STRING ) | ^(attrName= VK_AUTO_FOCUS (value= BOOL )? ) | ^(attrName= VK_DATE_EFFECTIVE value= STRING ) | ^(attrName= VK_DATE_EXPIRES value= STRING ) | ^(attrName= VK_ENABLED (value= BOOL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_RULEFLOW_GROUP value= STRING ) | ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? ) | ^(attrName= VK_DIALECT value= STRING ) | ^(attrName= VK_CALENDARS value= STRING ) ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:199:4: ( ^(attrName= VK_SALIENCE (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_NO_LOOP (value= BOOL )? ) | ^(attrName= VK_AGENDA_GROUP value= STRING ) | ^(attrName= VK_TIMER (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_ACTIVATION_GROUP value= STRING ) | ^(attrName= VK_AUTO_FOCUS (value= BOOL )? ) | ^(attrName= VK_DATE_EFFECTIVE value= STRING ) | ^(attrName= VK_DATE_EXPIRES value= STRING ) | ^(attrName= VK_ENABLED (value= BOOL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_RULEFLOW_GROUP value= STRING ) | ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? ) | ^(attrName= VK_DIALECT value= STRING ) | ^(attrName= VK_CALENDARS value= STRING ) )
            {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:199:4: ( ^(attrName= VK_SALIENCE (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_NO_LOOP (value= BOOL )? ) | ^(attrName= VK_AGENDA_GROUP value= STRING ) | ^(attrName= VK_TIMER (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_ACTIVATION_GROUP value= STRING ) | ^(attrName= VK_AUTO_FOCUS (value= BOOL )? ) | ^(attrName= VK_DATE_EFFECTIVE value= STRING ) | ^(attrName= VK_DATE_EXPIRES value= STRING ) | ^(attrName= VK_ENABLED (value= BOOL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_RULEFLOW_GROUP value= STRING ) | ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? ) | ^(attrName= VK_DIALECT value= STRING ) | ^(attrName= VK_CALENDARS value= STRING ) )
            int alt33=13;
            switch ( input.LA(1) ) {
            case VK_SALIENCE:
                {
                alt33=1;
                }
                break;
            case VK_NO_LOOP:
                {
                alt33=2;
                }
                break;
            case VK_AGENDA_GROUP:
                {
                alt33=3;
                }
                break;
            case VK_TIMER:
                {
                alt33=4;
                }
                break;
            case VK_ACTIVATION_GROUP:
                {
                alt33=5;
                }
                break;
            case VK_AUTO_FOCUS:
                {
                alt33=6;
                }
                break;
            case VK_DATE_EFFECTIVE:
                {
                alt33=7;
                }
                break;
            case VK_DATE_EXPIRES:
                {
                alt33=8;
                }
                break;
            case VK_ENABLED:
                {
                alt33=9;
                }
                break;
            case VK_RULEFLOW_GROUP:
                {
                alt33=10;
                }
                break;
            case VK_LOCK_ON_ACTIVE:
                {
                alt33=11;
                }
                break;
            case VK_DIALECT:
                {
                alt33=12;
                }
                break;
            case VK_CALENDARS:
                {
                alt33=13;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;
            }

            switch (alt33) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:199:5: ^(attrName= VK_SALIENCE (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) )
                    {
                    attrName=(DroolsTree)match(input,VK_SALIENCE,FOLLOW_VK_SALIENCE_in_rule_attribute889); 

                    match(input, Token.DOWN, null); 
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:199:28: (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK )
                    int alt27=2;
                    int LA27_0 = input.LA(1);

                    if ( (LA27_0==SIGNED_DECIMAL) ) {
                        alt27=1;
                    }
                    else if ( (LA27_0==VT_PAREN_CHUNK) ) {
                        alt27=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 27, 0, input);

                        throw nvae;
                    }
                    switch (alt27) {
                        case 1 :
                            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:199:29: value= SIGNED_DECIMAL
                            {
                            value=(DroolsTree)match(input,SIGNED_DECIMAL,FOLLOW_SIGNED_DECIMAL_in_rule_attribute894); 

                            }
                            break;
                        case 2 :
                            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:199:50: value= VT_PAREN_CHUNK
                            {
                            value=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_rule_attribute898); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:200:4: ^(attrName= VK_NO_LOOP (value= BOOL )? )
                    {
                    attrName=(DroolsTree)match(input,VK_NO_LOOP,FOLLOW_VK_NO_LOOP_in_rule_attribute909); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:200:31: (value= BOOL )?
                        int alt28=2;
                        int LA28_0 = input.LA(1);

                        if ( (LA28_0==BOOL) ) {
                            alt28=1;
                        }
                        switch (alt28) {
                            case 1 :
                                // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:200:31: value= BOOL
                                {
                                value=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_rule_attribute913); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 3 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:201:4: ^(attrName= VK_AGENDA_GROUP value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_AGENDA_GROUP,FOLLOW_VK_AGENDA_GROUP_in_rule_attribute925); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute929); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:202:4: ^(attrName= VK_TIMER (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) )
                    {
                    attrName=(DroolsTree)match(input,VK_TIMER,FOLLOW_VK_TIMER_in_rule_attribute940); 

                    match(input, Token.DOWN, null); 
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:202:24: (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK )
                    int alt29=2;
                    int LA29_0 = input.LA(1);

                    if ( (LA29_0==SIGNED_DECIMAL) ) {
                        alt29=1;
                    }
                    else if ( (LA29_0==VT_PAREN_CHUNK) ) {
                        alt29=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 29, 0, input);

                        throw nvae;
                    }
                    switch (alt29) {
                        case 1 :
                            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:202:25: value= SIGNED_DECIMAL
                            {
                            value=(DroolsTree)match(input,SIGNED_DECIMAL,FOLLOW_SIGNED_DECIMAL_in_rule_attribute945); 

                            }
                            break;
                        case 2 :
                            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:202:46: value= VT_PAREN_CHUNK
                            {
                            value=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_rule_attribute949); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:203:4: ^(attrName= VK_ACTIVATION_GROUP value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_ACTIVATION_GROUP,FOLLOW_VK_ACTIVATION_GROUP_in_rule_attribute962); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute966); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:204:4: ^(attrName= VK_AUTO_FOCUS (value= BOOL )? )
                    {
                    attrName=(DroolsTree)match(input,VK_AUTO_FOCUS,FOLLOW_VK_AUTO_FOCUS_in_rule_attribute976); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:204:34: (value= BOOL )?
                        int alt30=2;
                        int LA30_0 = input.LA(1);

                        if ( (LA30_0==BOOL) ) {
                            alt30=1;
                        }
                        switch (alt30) {
                            case 1 :
                                // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:204:34: value= BOOL
                                {
                                value=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_rule_attribute980); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 7 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:205:4: ^(attrName= VK_DATE_EFFECTIVE value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_DATE_EFFECTIVE,FOLLOW_VK_DATE_EFFECTIVE_in_rule_attribute991); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute995); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:206:4: ^(attrName= VK_DATE_EXPIRES value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_DATE_EXPIRES,FOLLOW_VK_DATE_EXPIRES_in_rule_attribute1005); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute1009); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 9 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:207:4: ^(attrName= VK_ENABLED (value= BOOL | value= VT_PAREN_CHUNK ) )
                    {
                    attrName=(DroolsTree)match(input,VK_ENABLED,FOLLOW_VK_ENABLED_in_rule_attribute1019); 

                    match(input, Token.DOWN, null); 
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:207:26: (value= BOOL | value= VT_PAREN_CHUNK )
                    int alt31=2;
                    int LA31_0 = input.LA(1);

                    if ( (LA31_0==BOOL) ) {
                        alt31=1;
                    }
                    else if ( (LA31_0==VT_PAREN_CHUNK) ) {
                        alt31=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 31, 0, input);

                        throw nvae;
                    }
                    switch (alt31) {
                        case 1 :
                            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:207:27: value= BOOL
                            {
                            value=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_rule_attribute1024); 

                            }
                            break;
                        case 2 :
                            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:207:38: value= VT_PAREN_CHUNK
                            {
                            value=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_rule_attribute1028); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 10 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:208:4: ^(attrName= VK_RULEFLOW_GROUP value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_RULEFLOW_GROUP,FOLLOW_VK_RULEFLOW_GROUP_in_rule_attribute1039); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute1043); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 11 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:209:4: ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? )
                    {
                    attrName=(DroolsTree)match(input,VK_LOCK_ON_ACTIVE,FOLLOW_VK_LOCK_ON_ACTIVE_in_rule_attribute1053); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:209:38: (value= BOOL )?
                        int alt32=2;
                        int LA32_0 = input.LA(1);

                        if ( (LA32_0==BOOL) ) {
                            alt32=1;
                        }
                        switch (alt32) {
                            case 1 :
                                // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:209:38: value= BOOL
                                {
                                value=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_rule_attribute1057); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 12 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:210:4: ^(attrName= VK_DIALECT value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_DIALECT,FOLLOW_VK_DIALECT_in_rule_attribute1067); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute1071); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 13 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:211:4: ^(attrName= VK_CALENDARS value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_CALENDARS,FOLLOW_VK_CALENDARS_in_rule_attribute1080); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute1084); 

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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:215:1: lhs_block returns [AndDescr andDescr] : ^( VT_AND_IMPLICIT (dt= lhs )* ) ;
    public final AndDescr lhs_block() throws RecognitionException {
        AndDescr andDescr = null;

        DescrBuilderTree.lhs_return dt = null;



        	andDescr = new AndDescr();

        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:218:3: ( ^( VT_AND_IMPLICIT (dt= lhs )* ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:218:5: ^( VT_AND_IMPLICIT (dt= lhs )* )
            {
            match(input,VT_AND_IMPLICIT,FOLLOW_VT_AND_IMPLICIT_in_lhs_block1109); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:218:23: (dt= lhs )*
                loop34:
                do {
                    int alt34=2;
                    int LA34_0 = input.LA(1);

                    if ( ((LA34_0>=VT_AND_PREFIX && LA34_0<=VT_OR_INFIX)||LA34_0==VT_PATTERN||LA34_0==VK_EVAL||LA34_0==VK_NOT||(LA34_0>=VK_EXISTS && LA34_0<=VK_FORALL)||LA34_0==FROM) ) {
                        alt34=1;
                    }


                    switch (alt34) {
                	case 1 :
                	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:218:24: dt= lhs
                	    {
                	    pushFollow(FOLLOW_lhs_in_lhs_block1114);
                	    dt=lhs();

                	    state._fsp--;

                	    andDescr.addDescr((dt!=null?dt.baseDescr:null));

                	    }
                	    break;

                	default :
                	    break loop34;
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:221:1: lhs returns [BaseDescr baseDescr] : ( ^(start= VT_OR_PREFIX (dt= lhs )+ ) | ^(start= VT_OR_INFIX dt1= lhs dt2= lhs ) | ^(start= VT_AND_PREFIX (dt= lhs )+ ) | ^(start= VT_AND_INFIX dt1= lhs dt2= lhs ) | ^(start= VK_EXISTS dt= lhs ) | ^(start= VK_NOT dt= lhs ) | ^(start= VK_EVAL pc= VT_PAREN_CHUNK ) | ^(start= VK_FORALL (dt= lhs )+ ) | ^( FROM pn= lhs_pattern fe= from_elements ) | pn= lhs_pattern );
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
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:224:3: ( ^(start= VT_OR_PREFIX (dt= lhs )+ ) | ^(start= VT_OR_INFIX dt1= lhs dt2= lhs ) | ^(start= VT_AND_PREFIX (dt= lhs )+ ) | ^(start= VT_AND_INFIX dt1= lhs dt2= lhs ) | ^(start= VK_EXISTS dt= lhs ) | ^(start= VK_NOT dt= lhs ) | ^(start= VK_EVAL pc= VT_PAREN_CHUNK ) | ^(start= VK_FORALL (dt= lhs )+ ) | ^( FROM pn= lhs_pattern fe= from_elements ) | pn= lhs_pattern )
            int alt38=10;
            switch ( input.LA(1) ) {
            case VT_OR_PREFIX:
                {
                alt38=1;
                }
                break;
            case VT_OR_INFIX:
                {
                alt38=2;
                }
                break;
            case VT_AND_PREFIX:
                {
                alt38=3;
                }
                break;
            case VT_AND_INFIX:
                {
                alt38=4;
                }
                break;
            case VK_EXISTS:
                {
                alt38=5;
                }
                break;
            case VK_NOT:
                {
                alt38=6;
                }
                break;
            case VK_EVAL:
                {
                alt38=7;
                }
                break;
            case VK_FORALL:
                {
                alt38=8;
                }
                break;
            case FROM:
                {
                alt38=9;
                }
                break;
            case VT_PATTERN:
                {
                alt38=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                throw nvae;
            }

            switch (alt38) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:224:5: ^(start= VT_OR_PREFIX (dt= lhs )+ )
                    {
                    start=(DroolsTree)match(input,VT_OR_PREFIX,FOLLOW_VT_OR_PREFIX_in_lhs1140); 

                    match(input, Token.DOWN, null); 
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:224:26: (dt= lhs )+
                    int cnt35=0;
                    loop35:
                    do {
                        int alt35=2;
                        int LA35_0 = input.LA(1);

                        if ( ((LA35_0>=VT_AND_PREFIX && LA35_0<=VT_OR_INFIX)||LA35_0==VT_PATTERN||LA35_0==VK_EVAL||LA35_0==VK_NOT||(LA35_0>=VK_EXISTS && LA35_0<=VK_FORALL)||LA35_0==FROM) ) {
                            alt35=1;
                        }


                        switch (alt35) {
                    	case 1 :
                    	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:224:27: dt= lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs1145);
                    	    dt=lhs();

                    	    state._fsp--;

                    	    	lhsList.add((dt!=null?dt.baseDescr:null));	

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt35 >= 1 ) break loop35;
                                EarlyExitException eee =
                                    new EarlyExitException(35, input);
                                throw eee;
                        }
                        cnt35++;
                    } while (true);


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createOr(start, lhsList);	

                    }
                    break;
                case 2 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:226:4: ^(start= VT_OR_INFIX dt1= lhs dt2= lhs )
                    {
                    start=(DroolsTree)match(input,VT_OR_INFIX,FOLLOW_VT_OR_INFIX_in_lhs1161); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs1165);
                    dt1=lhs();

                    state._fsp--;

                    pushFollow(FOLLOW_lhs_in_lhs1169);
                    dt2=lhs();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	lhsList.add((dt1!=null?dt1.baseDescr:null));
                    		lhsList.add((dt2!=null?dt2.baseDescr:null));
                    		retval.baseDescr = factory.createOr(start, lhsList);	

                    }
                    break;
                case 3 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:230:4: ^(start= VT_AND_PREFIX (dt= lhs )+ )
                    {
                    start=(DroolsTree)match(input,VT_AND_PREFIX,FOLLOW_VT_AND_PREFIX_in_lhs1181); 

                    match(input, Token.DOWN, null); 
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:230:26: (dt= lhs )+
                    int cnt36=0;
                    loop36:
                    do {
                        int alt36=2;
                        int LA36_0 = input.LA(1);

                        if ( ((LA36_0>=VT_AND_PREFIX && LA36_0<=VT_OR_INFIX)||LA36_0==VT_PATTERN||LA36_0==VK_EVAL||LA36_0==VK_NOT||(LA36_0>=VK_EXISTS && LA36_0<=VK_FORALL)||LA36_0==FROM) ) {
                            alt36=1;
                        }


                        switch (alt36) {
                    	case 1 :
                    	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:230:27: dt= lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs1186);
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
                    	retval.baseDescr = factory.createAnd(start, lhsList);	

                    }
                    break;
                case 4 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:232:4: ^(start= VT_AND_INFIX dt1= lhs dt2= lhs )
                    {
                    start=(DroolsTree)match(input,VT_AND_INFIX,FOLLOW_VT_AND_INFIX_in_lhs1202); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs1206);
                    dt1=lhs();

                    state._fsp--;

                    pushFollow(FOLLOW_lhs_in_lhs1210);
                    dt2=lhs();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	lhsList.add((dt1!=null?dt1.baseDescr:null));
                    		lhsList.add((dt2!=null?dt2.baseDescr:null));
                    		retval.baseDescr = factory.createAnd(start, lhsList);	

                    }
                    break;
                case 5 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:236:4: ^(start= VK_EXISTS dt= lhs )
                    {
                    start=(DroolsTree)match(input,VK_EXISTS,FOLLOW_VK_EXISTS_in_lhs1222); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs1226);
                    dt=lhs();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createExists(start, (dt!=null?dt.baseDescr:null));	

                    }
                    break;
                case 6 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:238:4: ^(start= VK_NOT dt= lhs )
                    {
                    start=(DroolsTree)match(input,VK_NOT,FOLLOW_VK_NOT_in_lhs1238); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs1242);
                    dt=lhs();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createNot(start, (dt!=null?dt.baseDescr:null));	

                    }
                    break;
                case 7 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:240:4: ^(start= VK_EVAL pc= VT_PAREN_CHUNK )
                    {
                    start=(DroolsTree)match(input,VK_EVAL,FOLLOW_VK_EVAL_in_lhs1254); 

                    match(input, Token.DOWN, null); 
                    pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_lhs1258); 

                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createEval(start, pc);	

                    }
                    break;
                case 8 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:242:4: ^(start= VK_FORALL (dt= lhs )+ )
                    {
                    start=(DroolsTree)match(input,VK_FORALL,FOLLOW_VK_FORALL_in_lhs1270); 

                    match(input, Token.DOWN, null); 
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:242:22: (dt= lhs )+
                    int cnt37=0;
                    loop37:
                    do {
                        int alt37=2;
                        int LA37_0 = input.LA(1);

                        if ( ((LA37_0>=VT_AND_PREFIX && LA37_0<=VT_OR_INFIX)||LA37_0==VT_PATTERN||LA37_0==VK_EVAL||LA37_0==VK_NOT||(LA37_0>=VK_EXISTS && LA37_0<=VK_FORALL)||LA37_0==FROM) ) {
                            alt37=1;
                        }


                        switch (alt37) {
                    	case 1 :
                    	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:242:23: dt= lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs1275);
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
                    	retval.baseDescr = factory.createForAll(start, lhsList);	

                    }
                    break;
                case 9 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:244:4: ^( FROM pn= lhs_pattern fe= from_elements )
                    {
                    match(input,FROM,FOLLOW_FROM_in_lhs1289); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_pattern_in_lhs1293);
                    pn=lhs_pattern();

                    state._fsp--;

                    pushFollow(FOLLOW_from_elements_in_lhs1297);
                    fe=from_elements();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.setupFrom(pn, (fe!=null?fe.patternSourceDescr:null));	

                    }
                    break;
                case 10 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:246:4: pn= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs1308);
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:250:1: from_elements returns [PatternSourceDescr patternSourceDescr] : ( ^(start= ACCUMULATE dt= lhs ret= accumulate_parts[$patternSourceDescr] ) | ^(start= COLLECT dt= lhs ) | ^(start= VK_ENTRY_POINT entryId= VT_ENTRYPOINT_ID ) | fs= from_source_clause );
    public final DescrBuilderTree.from_elements_return from_elements() throws RecognitionException {
        DescrBuilderTree.from_elements_return retval = new DescrBuilderTree.from_elements_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree entryId=null;
        DescrBuilderTree.lhs_return dt = null;

        AccumulateDescr ret = null;

        DescrBuilderTree.from_source_clause_return fs = null;


        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:251:2: ( ^(start= ACCUMULATE dt= lhs ret= accumulate_parts[$patternSourceDescr] ) | ^(start= COLLECT dt= lhs ) | ^(start= VK_ENTRY_POINT entryId= VT_ENTRYPOINT_ID ) | fs= from_source_clause )
            int alt39=4;
            switch ( input.LA(1) ) {
            case ACCUMULATE:
                {
                alt39=1;
                }
                break;
            case COLLECT:
                {
                alt39=2;
                }
                break;
            case VK_ENTRY_POINT:
                {
                alt39=3;
                }
                break;
            case VT_FROM_SOURCE:
                {
                alt39=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;
            }

            switch (alt39) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:251:4: ^(start= ACCUMULATE dt= lhs ret= accumulate_parts[$patternSourceDescr] )
                    {
                    start=(DroolsTree)match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_from_elements1329); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_from_elements1333);
                    dt=lhs();

                    state._fsp--;

                    	retval.patternSourceDescr = factory.createAccumulate(start, (dt!=null?dt.baseDescr:null));	
                    pushFollow(FOLLOW_accumulate_parts_in_from_elements1343);
                    ret=accumulate_parts(retval.patternSourceDescr);

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.patternSourceDescr = ret;	

                    }
                    break;
                case 2 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:255:4: ^(start= COLLECT dt= lhs )
                    {
                    start=(DroolsTree)match(input,COLLECT,FOLLOW_COLLECT_in_from_elements1356); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_from_elements1360);
                    dt=lhs();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.patternSourceDescr = factory.createCollect(start, (dt!=null?dt.baseDescr:null));	

                    }
                    break;
                case 3 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:257:4: ^(start= VK_ENTRY_POINT entryId= VT_ENTRYPOINT_ID )
                    {
                    start=(DroolsTree)match(input,VK_ENTRY_POINT,FOLLOW_VK_ENTRY_POINT_in_from_elements1372); 

                    match(input, Token.DOWN, null); 
                    entryId=(DroolsTree)match(input,VT_ENTRYPOINT_ID,FOLLOW_VT_ENTRYPOINT_ID_in_from_elements1376); 

                    match(input, Token.UP, null); 
                    	retval.patternSourceDescr = factory.createEntryPoint(start, entryId);	

                    }
                    break;
                case 4 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:259:4: fs= from_source_clause
                    {
                    pushFollow(FOLLOW_from_source_clause_in_from_elements1387);
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:263:1: accumulate_parts[PatternSourceDescr patternSourceDescr] returns [AccumulateDescr accumulateDescr] : (ac1= accumulate_init_clause[$patternSourceDescr] | ac2= accumulate_id_clause[$patternSourceDescr] );
    public final AccumulateDescr accumulate_parts(PatternSourceDescr patternSourceDescr) throws RecognitionException {
        AccumulateDescr accumulateDescr = null;

        DescrBuilderTree.accumulate_init_clause_return ac1 = null;

        AccumulateDescr ac2 = null;


        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:264:2: (ac1= accumulate_init_clause[$patternSourceDescr] | ac2= accumulate_id_clause[$patternSourceDescr] )
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==VT_ACCUMULATE_INIT_CLAUSE) ) {
                alt40=1;
            }
            else if ( (LA40_0==VT_ACCUMULATE_ID_CLAUSE) ) {
                alt40=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 40, 0, input);

                throw nvae;
            }
            switch (alt40) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:264:4: ac1= accumulate_init_clause[$patternSourceDescr]
                    {
                    pushFollow(FOLLOW_accumulate_init_clause_in_accumulate_parts1408);
                    ac1=accumulate_init_clause(patternSourceDescr);

                    state._fsp--;

                    	accumulateDescr = (ac1!=null?ac1.accumulateDescr:null);	

                    }
                    break;
                case 2 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:266:4: ac2= accumulate_id_clause[$patternSourceDescr]
                    {
                    pushFollow(FOLLOW_accumulate_id_clause_in_accumulate_parts1419);
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:270:1: accumulate_init_clause[PatternSourceDescr accumulateParam] returns [AccumulateDescr accumulateDescr] : ^( VT_ACCUMULATE_INIT_CLAUSE ^(start= VK_INIT pc1= VT_PAREN_CHUNK ) ^( VK_ACTION pc2= VT_PAREN_CHUNK ) (rev= accumulate_init_reverse_clause )? ^( VK_RESULT pc3= VT_PAREN_CHUNK ) ) ;
    public final DescrBuilderTree.accumulate_init_clause_return accumulate_init_clause(PatternSourceDescr accumulateParam) throws RecognitionException {
        DescrBuilderTree.accumulate_init_clause_return retval = new DescrBuilderTree.accumulate_init_clause_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree pc1=null;
        DroolsTree pc2=null;
        DroolsTree pc3=null;
        DescrBuilderTree.accumulate_init_reverse_clause_return rev = null;


        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:271:2: ( ^( VT_ACCUMULATE_INIT_CLAUSE ^(start= VK_INIT pc1= VT_PAREN_CHUNK ) ^( VK_ACTION pc2= VT_PAREN_CHUNK ) (rev= accumulate_init_reverse_clause )? ^( VK_RESULT pc3= VT_PAREN_CHUNK ) ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:271:4: ^( VT_ACCUMULATE_INIT_CLAUSE ^(start= VK_INIT pc1= VT_PAREN_CHUNK ) ^( VK_ACTION pc2= VT_PAREN_CHUNK ) (rev= accumulate_init_reverse_clause )? ^( VK_RESULT pc3= VT_PAREN_CHUNK ) )
            {
            match(input,VT_ACCUMULATE_INIT_CLAUSE,FOLLOW_VT_ACCUMULATE_INIT_CLAUSE_in_accumulate_init_clause1442); 

            match(input, Token.DOWN, null); 
            start=(DroolsTree)match(input,VK_INIT,FOLLOW_VK_INIT_in_accumulate_init_clause1451); 

            match(input, Token.DOWN, null); 
            pc1=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1455); 

            match(input, Token.UP, null); 
            match(input,VK_ACTION,FOLLOW_VK_ACTION_in_accumulate_init_clause1463); 

            match(input, Token.DOWN, null); 
            pc2=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1467); 

            match(input, Token.UP, null); 
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:274:7: (rev= accumulate_init_reverse_clause )?
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==VK_REVERSE) ) {
                alt41=1;
            }
            switch (alt41) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:274:7: rev= accumulate_init_reverse_clause
                    {
                    pushFollow(FOLLOW_accumulate_init_reverse_clause_in_accumulate_init_clause1476);
                    rev=accumulate_init_reverse_clause();

                    state._fsp--;


                    }
                    break;

            }

            match(input,VK_RESULT,FOLLOW_VK_RESULT_in_accumulate_init_clause1483); 

            match(input, Token.DOWN, null); 
            pc3=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1487); 

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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:283:1: accumulate_init_reverse_clause returns [DroolsTree vkReverse, DroolsTree vkReverseChunk] : ^(vk= VK_REVERSE pc= VT_PAREN_CHUNK ) ;
    public final DescrBuilderTree.accumulate_init_reverse_clause_return accumulate_init_reverse_clause() throws RecognitionException {
        DescrBuilderTree.accumulate_init_reverse_clause_return retval = new DescrBuilderTree.accumulate_init_reverse_clause_return();
        retval.start = input.LT(1);

        DroolsTree vk=null;
        DroolsTree pc=null;

        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:284:2: ( ^(vk= VK_REVERSE pc= VT_PAREN_CHUNK ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:284:4: ^(vk= VK_REVERSE pc= VT_PAREN_CHUNK )
            {
            vk=(DroolsTree)match(input,VK_REVERSE,FOLLOW_VK_REVERSE_in_accumulate_init_reverse_clause1510); 

            match(input, Token.DOWN, null); 
            pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_reverse_clause1514); 

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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:289:1: accumulate_id_clause[PatternSourceDescr accumulateParam] returns [AccumulateDescr accumulateDescr] : ^( VT_ACCUMULATE_ID_CLAUSE id= ID pc= VT_PAREN_CHUNK ) ;
    public final AccumulateDescr accumulate_id_clause(PatternSourceDescr accumulateParam) throws RecognitionException {
        AccumulateDescr accumulateDescr = null;

        DroolsTree id=null;
        DroolsTree pc=null;

        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:290:2: ( ^( VT_ACCUMULATE_ID_CLAUSE id= ID pc= VT_PAREN_CHUNK ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:290:4: ^( VT_ACCUMULATE_ID_CLAUSE id= ID pc= VT_PAREN_CHUNK )
            {
            match(input,VT_ACCUMULATE_ID_CLAUSE,FOLLOW_VT_ACCUMULATE_ID_CLAUSE_in_accumulate_id_clause1536); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_accumulate_id_clause1540); 
            pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_id_clause1544); 

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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:294:1: from_source_clause returns [FromDescr fromDescr, AccessorDescr retAccessorDescr] : ^(fs= VT_FROM_SOURCE ) ;
    public final DescrBuilderTree.from_source_clause_return from_source_clause() throws RecognitionException {
        from_source_clause_stack.push(new from_source_clause_scope());
        DescrBuilderTree.from_source_clause_return retval = new DescrBuilderTree.from_source_clause_return();
        retval.start = input.LT(1);

        DroolsTree fs=null;

        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:297:3: ( ^(fs= VT_FROM_SOURCE ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:297:5: ^(fs= VT_FROM_SOURCE )
            {
            fs=(DroolsTree)match(input,VT_FROM_SOURCE,FOLLOW_VT_FROM_SOURCE_in_from_source_clause1568); 

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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:317:1: expression_chain : ^(start= VT_EXPRESSION_CHAIN id= ID (sc= VT_SQUARE_CHUNK )? (pc= VT_PAREN_CHUNK )? ( expression_chain )? ) ;
    public final DescrBuilderTree.expression_chain_return expression_chain() throws RecognitionException {
        DescrBuilderTree.expression_chain_return retval = new DescrBuilderTree.expression_chain_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree id=null;
        DroolsTree sc=null;
        DroolsTree pc=null;

        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:318:2: ( ^(start= VT_EXPRESSION_CHAIN id= ID (sc= VT_SQUARE_CHUNK )? (pc= VT_PAREN_CHUNK )? ( expression_chain )? ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:318:4: ^(start= VT_EXPRESSION_CHAIN id= ID (sc= VT_SQUARE_CHUNK )? (pc= VT_PAREN_CHUNK )? ( expression_chain )? )
            {
            start=(DroolsTree)match(input,VT_EXPRESSION_CHAIN,FOLLOW_VT_EXPRESSION_CHAIN_in_expression_chain1598); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_expression_chain1602); 
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:318:40: (sc= VT_SQUARE_CHUNK )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==VT_SQUARE_CHUNK) ) {
                alt42=1;
            }
            switch (alt42) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:318:40: sc= VT_SQUARE_CHUNK
                    {
                    sc=(DroolsTree)match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_expression_chain1606); 

                    }
                    break;

            }

            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:318:60: (pc= VT_PAREN_CHUNK )?
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==VT_PAREN_CHUNK) ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:318:60: pc= VT_PAREN_CHUNK
                    {
                    pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_expression_chain1611); 

                    }
                    break;

            }

            	DeclarativeInvokerDescr declarativeInvokerResult = factory.createExpressionChain(start, id, sc, pc);	
            		((from_source_clause_scope)from_source_clause_stack.peek()).accessorDescr.addInvoker(declarativeInvokerResult);	
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:321:3: ( expression_chain )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==VT_EXPRESSION_CHAIN) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:321:3: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain1619);
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:324:1: lhs_pattern returns [BaseDescr baseDescr] : ^( VT_PATTERN fe= fact_expression ) (oc= over_clause )? ;
    public final BaseDescr lhs_pattern() throws RecognitionException {
        BaseDescr baseDescr = null;

        DescrBuilderTree.fact_expression_return fe = null;

        List oc = null;


        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:325:2: ( ^( VT_PATTERN fe= fact_expression ) (oc= over_clause )? )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:325:4: ^( VT_PATTERN fe= fact_expression ) (oc= over_clause )?
            {
            match(input,VT_PATTERN,FOLLOW_VT_PATTERN_in_lhs_pattern1637); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_fact_expression_in_lhs_pattern1641);
            fe=fact_expression();

            state._fsp--;


            match(input, Token.UP, null); 
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:325:39: (oc= over_clause )?
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==OVER) ) {
                alt45=1;
            }
            switch (alt45) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:325:39: oc= over_clause
                    {
                    pushFollow(FOLLOW_over_clause_in_lhs_pattern1646);
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:329:1: over_clause returns [List behaviorList] : ^( OVER (oe= over_element )+ ) ;
    public final List over_clause() throws RecognitionException {
        List behaviorList = null;

        BehaviorDescr oe = null;


        behaviorList = new LinkedList();
        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:331:2: ( ^( OVER (oe= over_element )+ ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:331:4: ^( OVER (oe= over_element )+ )
            {
            match(input,OVER,FOLLOW_OVER_in_over_clause1671); 

            match(input, Token.DOWN, null); 
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:331:11: (oe= over_element )+
            int cnt46=0;
            loop46:
            do {
                int alt46=2;
                int LA46_0 = input.LA(1);

                if ( (LA46_0==VT_BEHAVIOR) ) {
                    alt46=1;
                }


                switch (alt46) {
            	case 1 :
            	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:331:12: oe= over_element
            	    {
            	    pushFollow(FOLLOW_over_element_in_over_clause1676);
            	    oe=over_element();

            	    state._fsp--;

            	    behaviorList.add(oe);

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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:334:1: over_element returns [BehaviorDescr behavior] : ^( VT_BEHAVIOR ID id2= ID pc= VT_PAREN_CHUNK ) ;
    public final BehaviorDescr over_element() throws RecognitionException {
        BehaviorDescr behavior = null;

        DroolsTree id2=null;
        DroolsTree pc=null;

        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:335:2: ( ^( VT_BEHAVIOR ID id2= ID pc= VT_PAREN_CHUNK ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:335:4: ^( VT_BEHAVIOR ID id2= ID pc= VT_PAREN_CHUNK )
            {
            match(input,VT_BEHAVIOR,FOLLOW_VT_BEHAVIOR_in_over_element1697); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_over_element1699); 
            id2=(DroolsTree)match(input,ID,FOLLOW_ID_in_over_element1703); 
            pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_over_element1707); 

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

    public static class fact_expression_return extends TreeRuleReturnScope {
        public BaseDescr descr;
    };

    // $ANTLR start "fact_expression"
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:339:1: fact_expression returns [BaseDescr descr] : ( ^( VT_FACT pt= pattern_type (fe= fact_expression )* ) | ^( VT_FACT_BINDING label= VT_LABEL fact= fact_expression ) | ^(start= VT_FACT_OR left= fact_expression right= fact_expression ) | ^( VT_FIELD field= field_element (fe= fact_expression )? ) | ^( VT_BIND_FIELD label= VT_LABEL fe= fact_expression ) | ^( VK_EVAL pc= VT_PAREN_CHUNK ) | ^(op= EQUALS fe= fact_expression ) | ^(op= NOT_EQUALS fe= fact_expression ) | ^(op= GREATER fe= fact_expression ) | ^(op= GREATER_EQUALS fe= fact_expression ) | ^(op= LESS fe= fact_expression ) | ^(op= LESS_EQUALS fe= fact_expression ) | ^(op= VK_OPERATOR (not= VK_NOT )? (param= VT_SQUARE_CHUNK )? fe= fact_expression ) | ^( VK_IN (not= VK_NOT )? (fe= fact_expression )+ ) | ^( DOUBLE_PIPE left= fact_expression right= fact_expression ) | ^( DOUBLE_AMPER left= fact_expression right= fact_expression ) | ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) | s= STRING | ( PLUS | m= MINUS )? ( (i= DECIMAL | i= SIGNED_DECIMAL ) | (h= HEX | h= SIGNED_HEX ) | (f= FLOAT | f= SIGNED_FLOAT ) ) | b= BOOL | n= NULL | pc= VT_PAREN_CHUNK );
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
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:342:3: ( ^( VT_FACT pt= pattern_type (fe= fact_expression )* ) | ^( VT_FACT_BINDING label= VT_LABEL fact= fact_expression ) | ^(start= VT_FACT_OR left= fact_expression right= fact_expression ) | ^( VT_FIELD field= field_element (fe= fact_expression )? ) | ^( VT_BIND_FIELD label= VT_LABEL fe= fact_expression ) | ^( VK_EVAL pc= VT_PAREN_CHUNK ) | ^(op= EQUALS fe= fact_expression ) | ^(op= NOT_EQUALS fe= fact_expression ) | ^(op= GREATER fe= fact_expression ) | ^(op= GREATER_EQUALS fe= fact_expression ) | ^(op= LESS fe= fact_expression ) | ^(op= LESS_EQUALS fe= fact_expression ) | ^(op= VK_OPERATOR (not= VK_NOT )? (param= VT_SQUARE_CHUNK )? fe= fact_expression ) | ^( VK_IN (not= VK_NOT )? (fe= fact_expression )+ ) | ^( DOUBLE_PIPE left= fact_expression right= fact_expression ) | ^( DOUBLE_AMPER left= fact_expression right= fact_expression ) | ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) | s= STRING | ( PLUS | m= MINUS )? ( (i= DECIMAL | i= SIGNED_DECIMAL ) | (h= HEX | h= SIGNED_HEX ) | (f= FLOAT | f= SIGNED_FLOAT ) ) | b= BOOL | n= NULL | pc= VT_PAREN_CHUNK )
            int alt59=22;
            switch ( input.LA(1) ) {
            case VT_FACT:
                {
                alt59=1;
                }
                break;
            case VT_FACT_BINDING:
                {
                alt59=2;
                }
                break;
            case VT_FACT_OR:
                {
                alt59=3;
                }
                break;
            case VT_FIELD:
                {
                alt59=4;
                }
                break;
            case VT_BIND_FIELD:
                {
                alt59=5;
                }
                break;
            case VK_EVAL:
                {
                alt59=6;
                }
                break;
            case EQUALS:
                {
                alt59=7;
                }
                break;
            case NOT_EQUALS:
                {
                alt59=8;
                }
                break;
            case GREATER:
                {
                alt59=9;
                }
                break;
            case GREATER_EQUALS:
                {
                alt59=10;
                }
                break;
            case LESS:
                {
                alt59=11;
                }
                break;
            case LESS_EQUALS:
                {
                alt59=12;
                }
                break;
            case VK_OPERATOR:
                {
                alt59=13;
                }
                break;
            case VK_IN:
                {
                alt59=14;
                }
                break;
            case DOUBLE_PIPE:
                {
                alt59=15;
                }
                break;
            case DOUBLE_AMPER:
                {
                alt59=16;
                }
                break;
            case VT_ACCESSOR_PATH:
                {
                alt59=17;
                }
                break;
            case STRING:
                {
                alt59=18;
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
                alt59=19;
                }
                break;
            case BOOL:
                {
                alt59=20;
                }
                break;
            case NULL:
                {
                alt59=21;
                }
                break;
            case VT_PAREN_CHUNK:
                {
                alt59=22;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 59, 0, input);

                throw nvae;
            }

            switch (alt59) {
                case 1 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:342:5: ^( VT_FACT pt= pattern_type (fe= fact_expression )* )
                    {
                    match(input,VT_FACT,FOLLOW_VT_FACT_in_fact_expression1730); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_pattern_type_in_fact_expression1734);
                    pt=pattern_type();

                    state._fsp--;

                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:342:31: (fe= fact_expression )*
                    loop47:
                    do {
                        int alt47=2;
                        int LA47_0 = input.LA(1);

                        if ( (LA47_0==VT_FACT||LA47_0==VT_PAREN_CHUNK||(LA47_0>=VT_FACT_BINDING && LA47_0<=VT_ACCESSOR_PATH)||LA47_0==VK_EVAL||LA47_0==VK_IN||LA47_0==VK_OPERATOR||(LA47_0>=SIGNED_DECIMAL && LA47_0<=SIGNED_FLOAT)||LA47_0==STRING||LA47_0==BOOL||LA47_0==DECIMAL||(LA47_0>=DOUBLE_PIPE && LA47_0<=DOUBLE_AMPER)||(LA47_0>=EQUALS && LA47_0<=NOT_EQUALS)||(LA47_0>=NULL && LA47_0<=FLOAT)) ) {
                            alt47=1;
                        }


                        switch (alt47) {
                    	case 1 :
                    	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:342:32: fe= fact_expression
                    	    {
                    	    pushFollow(FOLLOW_fact_expression_in_fact_expression1739);
                    	    fe=fact_expression();

                    	    state._fsp--;

                    	    exprList.add((fe!=null?fe.descr:null));

                    	    }
                    	    break;

                    	default :
                    	    break loop47;
                        }
                    } while (true);


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createPattern(pt, exprList);	

                    }
                    break;
                case 2 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:344:4: ^( VT_FACT_BINDING label= VT_LABEL fact= fact_expression )
                    {
                    match(input,VT_FACT_BINDING,FOLLOW_VT_FACT_BINDING_in_fact_expression1753); 

                    match(input, Token.DOWN, null); 
                    label=(DroolsTree)match(input,VT_LABEL,FOLLOW_VT_LABEL_in_fact_expression1757); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1761);
                    fact=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupPatternBiding(label, (fact!=null?fact.descr:null));	

                    }
                    break;
                case 3 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:346:4: ^(start= VT_FACT_OR left= fact_expression right= fact_expression )
                    {
                    start=(DroolsTree)match(input,VT_FACT_OR,FOLLOW_VT_FACT_OR_in_fact_expression1773); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1777);
                    left=fact_expression();

                    state._fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1781);
                    right=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createFactOr(start, (left!=null?left.descr:null), (right!=null?right.descr:null));	

                    }
                    break;
                case 4 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:349:4: ^( VT_FIELD field= field_element (fe= fact_expression )? )
                    {
                    match(input,VT_FIELD,FOLLOW_VT_FIELD_in_fact_expression1792); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_field_element_in_fact_expression1796);
                    field=field_element();

                    state._fsp--;

                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:349:37: (fe= fact_expression )?
                    int alt48=2;
                    int LA48_0 = input.LA(1);

                    if ( (LA48_0==VT_FACT||LA48_0==VT_PAREN_CHUNK||(LA48_0>=VT_FACT_BINDING && LA48_0<=VT_ACCESSOR_PATH)||LA48_0==VK_EVAL||LA48_0==VK_IN||LA48_0==VK_OPERATOR||(LA48_0>=SIGNED_DECIMAL && LA48_0<=SIGNED_FLOAT)||LA48_0==STRING||LA48_0==BOOL||LA48_0==DECIMAL||(LA48_0>=DOUBLE_PIPE && LA48_0<=DOUBLE_AMPER)||(LA48_0>=EQUALS && LA48_0<=NOT_EQUALS)||(LA48_0>=NULL && LA48_0<=FLOAT)) ) {
                        alt48=1;
                    }
                    switch (alt48) {
                        case 1 :
                            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:349:37: fe= fact_expression
                            {
                            pushFollow(FOLLOW_fact_expression_in_fact_expression1800);
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
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:355:4: ^( VT_BIND_FIELD label= VT_LABEL fe= fact_expression )
                    {
                    match(input,VT_BIND_FIELD,FOLLOW_VT_BIND_FIELD_in_fact_expression1811); 

                    match(input, Token.DOWN, null); 
                    label=(DroolsTree)match(input,VT_LABEL,FOLLOW_VT_LABEL_in_fact_expression1815); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1819);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createFieldBinding(label, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 6 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:358:4: ^( VK_EVAL pc= VT_PAREN_CHUNK )
                    {
                    match(input,VK_EVAL,FOLLOW_VK_EVAL_in_fact_expression1830); 

                    match(input, Token.DOWN, null); 
                    pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_fact_expression1834); 

                    match(input, Token.UP, null); 
                    	retval.descr = factory.createPredicate(pc);	

                    }
                    break;
                case 7 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:361:4: ^(op= EQUALS fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,EQUALS,FOLLOW_EQUALS_in_fact_expression1847); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1851);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 8 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:363:4: ^(op= NOT_EQUALS fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_fact_expression1863); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1867);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 9 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:365:4: ^(op= GREATER fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,GREATER,FOLLOW_GREATER_in_fact_expression1879); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1883);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 10 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:367:4: ^(op= GREATER_EQUALS fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,GREATER_EQUALS,FOLLOW_GREATER_EQUALS_in_fact_expression1895); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1899);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 11 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:369:4: ^(op= LESS fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,LESS,FOLLOW_LESS_in_fact_expression1911); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1915);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 12 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:371:4: ^(op= LESS_EQUALS fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,LESS_EQUALS,FOLLOW_LESS_EQUALS_in_fact_expression1927); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1931);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 13 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:373:4: ^(op= VK_OPERATOR (not= VK_NOT )? (param= VT_SQUARE_CHUNK )? fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,VK_OPERATOR,FOLLOW_VK_OPERATOR_in_fact_expression1943); 

                    match(input, Token.DOWN, null); 
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:373:24: (not= VK_NOT )?
                    int alt49=2;
                    int LA49_0 = input.LA(1);

                    if ( (LA49_0==VK_NOT) ) {
                        alt49=1;
                    }
                    switch (alt49) {
                        case 1 :
                            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:373:24: not= VK_NOT
                            {
                            not=(DroolsTree)match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1947); 

                            }
                            break;

                    }

                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:373:38: (param= VT_SQUARE_CHUNK )?
                    int alt50=2;
                    int LA50_0 = input.LA(1);

                    if ( (LA50_0==VT_SQUARE_CHUNK) ) {
                        alt50=1;
                    }
                    switch (alt50) {
                        case 1 :
                            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:373:38: param= VT_SQUARE_CHUNK
                            {
                            param=(DroolsTree)match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_fact_expression1952); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1957);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, not, (fe!=null?fe.descr:null), param);	

                    }
                    break;
                case 14 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:376:4: ^( VK_IN (not= VK_NOT )? (fe= fact_expression )+ )
                    {
                    match(input,VK_IN,FOLLOW_VK_IN_in_fact_expression1968); 

                    match(input, Token.DOWN, null); 
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:376:15: (not= VK_NOT )?
                    int alt51=2;
                    int LA51_0 = input.LA(1);

                    if ( (LA51_0==VK_NOT) ) {
                        alt51=1;
                    }
                    switch (alt51) {
                        case 1 :
                            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:376:15: not= VK_NOT
                            {
                            not=(DroolsTree)match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1972); 

                            }
                            break;

                    }

                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:376:24: (fe= fact_expression )+
                    int cnt52=0;
                    loop52:
                    do {
                        int alt52=2;
                        int LA52_0 = input.LA(1);

                        if ( (LA52_0==VT_FACT||LA52_0==VT_PAREN_CHUNK||(LA52_0>=VT_FACT_BINDING && LA52_0<=VT_ACCESSOR_PATH)||LA52_0==VK_EVAL||LA52_0==VK_IN||LA52_0==VK_OPERATOR||(LA52_0>=SIGNED_DECIMAL && LA52_0<=SIGNED_FLOAT)||LA52_0==STRING||LA52_0==BOOL||LA52_0==DECIMAL||(LA52_0>=DOUBLE_PIPE && LA52_0<=DOUBLE_AMPER)||(LA52_0>=EQUALS && LA52_0<=NOT_EQUALS)||(LA52_0>=NULL && LA52_0<=FLOAT)) ) {
                            alt52=1;
                        }


                        switch (alt52) {
                    	case 1 :
                    	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:376:25: fe= fact_expression
                    	    {
                    	    pushFollow(FOLLOW_fact_expression_in_fact_expression1978);
                    	    fe=fact_expression();

                    	    state._fsp--;

                    	    exprList.add((fe!=null?fe.descr:null));

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


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createRestrictionConnective(not, exprList);	

                    }
                    break;
                case 15 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:379:4: ^( DOUBLE_PIPE left= fact_expression right= fact_expression )
                    {
                    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_fact_expression1993); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1997);
                    left=fact_expression();

                    state._fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression2001);
                    right=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createOrRestrictionConnective((left!=null?left.descr:null), (right!=null?right.descr:null));	

                    }
                    break;
                case 16 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:381:4: ^( DOUBLE_AMPER left= fact_expression right= fact_expression )
                    {
                    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_fact_expression2011); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression2015);
                    left=fact_expression();

                    state._fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression2019);
                    right=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createAndRestrictionConnective((left!=null?left.descr:null), (right!=null?right.descr:null));	

                    }
                    break;
                case 17 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:384:4: ^( VT_ACCESSOR_PATH (ae= accessor_element )+ )
                    {
                    match(input,VT_ACCESSOR_PATH,FOLLOW_VT_ACCESSOR_PATH_in_fact_expression2030); 

                    match(input, Token.DOWN, null); 
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:384:23: (ae= accessor_element )+
                    int cnt53=0;
                    loop53:
                    do {
                        int alt53=2;
                        int LA53_0 = input.LA(1);

                        if ( (LA53_0==VT_ACCESSOR_ELEMENT) ) {
                            alt53=1;
                        }


                        switch (alt53) {
                    	case 1 :
                    	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:384:24: ae= accessor_element
                    	    {
                    	    pushFollow(FOLLOW_accessor_element_in_fact_expression2035);
                    	    ae=accessor_element();

                    	    state._fsp--;

                    	    exprList.add(ae);

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt53 >= 1 ) break loop53;
                                EarlyExitException eee =
                                    new EarlyExitException(53, input);
                                throw eee;
                        }
                        cnt53++;
                    } while (true);


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createAccessorPath(exprList);	

                    }
                    break;
                case 18 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:386:4: s= STRING
                    {
                    s=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_fact_expression2050); 
                    	retval.descr = factory.createStringLiteralRestriction(s);	

                    }
                    break;
                case 19 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:388:4: ( PLUS | m= MINUS )? ( (i= DECIMAL | i= SIGNED_DECIMAL ) | (h= HEX | h= SIGNED_HEX ) | (f= FLOAT | f= SIGNED_FLOAT ) )
                    {
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:388:4: ( PLUS | m= MINUS )?
                    int alt54=3;
                    int LA54_0 = input.LA(1);

                    if ( (LA54_0==PLUS) ) {
                        alt54=1;
                    }
                    else if ( (LA54_0==MINUS) ) {
                        alt54=2;
                    }
                    switch (alt54) {
                        case 1 :
                            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:388:5: PLUS
                            {
                            match(input,PLUS,FOLLOW_PLUS_in_fact_expression2059); 

                            }
                            break;
                        case 2 :
                            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:388:10: m= MINUS
                            {
                            m=(DroolsTree)match(input,MINUS,FOLLOW_MINUS_in_fact_expression2063); 

                            }
                            break;

                    }

                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:389:10: ( (i= DECIMAL | i= SIGNED_DECIMAL ) | (h= HEX | h= SIGNED_HEX ) | (f= FLOAT | f= SIGNED_FLOAT ) )
                    int alt58=3;
                    switch ( input.LA(1) ) {
                    case SIGNED_DECIMAL:
                    case DECIMAL:
                        {
                        alt58=1;
                        }
                        break;
                    case SIGNED_HEX:
                    case HEX:
                        {
                        alt58=2;
                        }
                        break;
                    case SIGNED_FLOAT:
                    case FLOAT:
                        {
                        alt58=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 58, 0, input);

                        throw nvae;
                    }

                    switch (alt58) {
                        case 1 :
                            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:389:12: (i= DECIMAL | i= SIGNED_DECIMAL )
                            {
                            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:389:12: (i= DECIMAL | i= SIGNED_DECIMAL )
                            int alt55=2;
                            int LA55_0 = input.LA(1);

                            if ( (LA55_0==DECIMAL) ) {
                                alt55=1;
                            }
                            else if ( (LA55_0==SIGNED_DECIMAL) ) {
                                alt55=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 55, 0, input);

                                throw nvae;
                            }
                            switch (alt55) {
                                case 1 :
                                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:389:13: i= DECIMAL
                                    {
                                    i=(DroolsTree)match(input,DECIMAL,FOLLOW_DECIMAL_in_fact_expression2082); 

                                    }
                                    break;
                                case 2 :
                                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:389:23: i= SIGNED_DECIMAL
                                    {
                                    i=(DroolsTree)match(input,SIGNED_DECIMAL,FOLLOW_SIGNED_DECIMAL_in_fact_expression2086); 

                                    }
                                    break;

                            }

                             retval.descr = factory.createIntLiteralRestriction(i, m != null); 	

                            }
                            break;
                        case 2 :
                            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:390:5: (h= HEX | h= SIGNED_HEX )
                            {
                            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:390:5: (h= HEX | h= SIGNED_HEX )
                            int alt56=2;
                            int LA56_0 = input.LA(1);

                            if ( (LA56_0==HEX) ) {
                                alt56=1;
                            }
                            else if ( (LA56_0==SIGNED_HEX) ) {
                                alt56=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 56, 0, input);

                                throw nvae;
                            }
                            switch (alt56) {
                                case 1 :
                                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:390:6: h= HEX
                                    {
                                    h=(DroolsTree)match(input,HEX,FOLLOW_HEX_in_fact_expression2098); 

                                    }
                                    break;
                                case 2 :
                                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:390:12: h= SIGNED_HEX
                                    {
                                    h=(DroolsTree)match(input,SIGNED_HEX,FOLLOW_SIGNED_HEX_in_fact_expression2102); 

                                    }
                                    break;

                            }

                             retval.descr = factory.createIntLiteralRestriction(h, m != null); 	

                            }
                            break;
                        case 3 :
                            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:391:5: (f= FLOAT | f= SIGNED_FLOAT )
                            {
                            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:391:5: (f= FLOAT | f= SIGNED_FLOAT )
                            int alt57=2;
                            int LA57_0 = input.LA(1);

                            if ( (LA57_0==FLOAT) ) {
                                alt57=1;
                            }
                            else if ( (LA57_0==SIGNED_FLOAT) ) {
                                alt57=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 57, 0, input);

                                throw nvae;
                            }
                            switch (alt57) {
                                case 1 :
                                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:391:6: f= FLOAT
                                    {
                                    f=(DroolsTree)match(input,FLOAT,FOLLOW_FLOAT_in_fact_expression2116); 

                                    }
                                    break;
                                case 2 :
                                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:391:14: f= SIGNED_FLOAT
                                    {
                                    f=(DroolsTree)match(input,SIGNED_FLOAT,FOLLOW_SIGNED_FLOAT_in_fact_expression2120); 

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
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:393:4: b= BOOL
                    {
                    b=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_fact_expression2136); 
                    	retval.descr = factory.createBoolLiteralRestriction(b);	

                    }
                    break;
                case 21 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:395:4: n= NULL
                    {
                    n=(DroolsTree)match(input,NULL,FOLLOW_NULL_in_fact_expression2146); 
                    	retval.descr = factory.createNullLiteralRestriction(n);	

                    }
                    break;
                case 22 :
                    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:397:4: pc= VT_PAREN_CHUNK
                    {
                    pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_fact_expression2156); 
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:401:1: field_element returns [FieldConstraintDescr element] : ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) ;
    public final FieldConstraintDescr field_element() throws RecognitionException {
        FieldConstraintDescr element = null;

        BaseDescr ae = null;



        	List<BaseDescr> aeList = new LinkedList<BaseDescr>();

        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:404:3: ( ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:404:5: ^( VT_ACCESSOR_PATH (ae= accessor_element )+ )
            {
            match(input,VT_ACCESSOR_PATH,FOLLOW_VT_ACCESSOR_PATH_in_field_element2178); 

            match(input, Token.DOWN, null); 
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:404:24: (ae= accessor_element )+
            int cnt60=0;
            loop60:
            do {
                int alt60=2;
                int LA60_0 = input.LA(1);

                if ( (LA60_0==VT_ACCESSOR_ELEMENT) ) {
                    alt60=1;
                }


                switch (alt60) {
            	case 1 :
            	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:404:25: ae= accessor_element
            	    {
            	    pushFollow(FOLLOW_accessor_element_in_field_element2183);
            	    ae=accessor_element();

            	    state._fsp--;

            	    aeList.add(ae);

            	    }
            	    break;

            	default :
            	    if ( cnt60 >= 1 ) break loop60;
                        EarlyExitException eee =
                            new EarlyExitException(60, input);
                        throw eee;
                }
                cnt60++;
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:408:1: accessor_element returns [BaseDescr element] : ^( VT_ACCESSOR_ELEMENT id= ID (sc+= VT_SQUARE_CHUNK )* ) ;
    public final BaseDescr accessor_element() throws RecognitionException {
        BaseDescr element = null;

        DroolsTree id=null;
        DroolsTree sc=null;
        List list_sc=null;

        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:409:2: ( ^( VT_ACCESSOR_ELEMENT id= ID (sc+= VT_SQUARE_CHUNK )* ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:409:4: ^( VT_ACCESSOR_ELEMENT id= ID (sc+= VT_SQUARE_CHUNK )* )
            {
            match(input,VT_ACCESSOR_ELEMENT,FOLLOW_VT_ACCESSOR_ELEMENT_in_accessor_element2207); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_accessor_element2211); 
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:409:34: (sc+= VT_SQUARE_CHUNK )*
            loop61:
            do {
                int alt61=2;
                int LA61_0 = input.LA(1);

                if ( (LA61_0==VT_SQUARE_CHUNK) ) {
                    alt61=1;
                }


                switch (alt61) {
            	case 1 :
            	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:409:34: sc+= VT_SQUARE_CHUNK
            	    {
            	    sc=(DroolsTree)match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_accessor_element2215); 
            	    if (list_sc==null) list_sc=new ArrayList();
            	    list_sc.add(sc);


            	    }
            	    break;

            	default :
            	    break loop61;
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:413:1: pattern_type returns [BaseDescr dataType] : ^( VT_PATTERN_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) ;
    public final BaseDescr pattern_type() throws RecognitionException {
        BaseDescr dataType = null;

        DroolsTree idList=null;
        DroolsTree rightList=null;
        List list_idList=null;
        List list_rightList=null;

        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:414:2: ( ^( VT_PATTERN_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:414:4: ^( VT_PATTERN_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* )
            {
            match(input,VT_PATTERN_TYPE,FOLLOW_VT_PATTERN_TYPE_in_pattern_type2236); 

            match(input, Token.DOWN, null); 
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:414:28: (idList+= ID )+
            int cnt62=0;
            loop62:
            do {
                int alt62=2;
                int LA62_0 = input.LA(1);

                if ( (LA62_0==ID) ) {
                    alt62=1;
                }


                switch (alt62) {
            	case 1 :
            	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:414:28: idList+= ID
            	    {
            	    idList=(DroolsTree)match(input,ID,FOLLOW_ID_in_pattern_type2240); 
            	    if (list_idList==null) list_idList=new ArrayList();
            	    list_idList.add(idList);


            	    }
            	    break;

            	default :
            	    if ( cnt62 >= 1 ) break loop62;
                        EarlyExitException eee =
                            new EarlyExitException(62, input);
                        throw eee;
                }
                cnt62++;
            } while (true);

            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:414:34: ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            loop63:
            do {
                int alt63=2;
                int LA63_0 = input.LA(1);

                if ( (LA63_0==LEFT_SQUARE) ) {
                    alt63=1;
                }


                switch (alt63) {
            	case 1 :
            	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:414:35: LEFT_SQUARE rightList+= RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_pattern_type2244); 
            	    rightList=(DroolsTree)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_pattern_type2248); 
            	    if (list_rightList==null) list_rightList=new ArrayList();
            	    list_rightList.add(rightList);


            	    }
            	    break;

            	default :
            	    break loop63;
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
    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:418:1: data_type returns [BaseDescr dataType] : ^( VT_DATA_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) ;
    public final BaseDescr data_type() throws RecognitionException {
        BaseDescr dataType = null;

        DroolsTree idList=null;
        DroolsTree rightList=null;
        List list_idList=null;
        List list_rightList=null;

        try {
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:419:2: ( ^( VT_DATA_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) )
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:419:4: ^( VT_DATA_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* )
            {
            match(input,VT_DATA_TYPE,FOLLOW_VT_DATA_TYPE_in_data_type2270); 

            match(input, Token.DOWN, null); 
            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:419:25: (idList+= ID )+
            int cnt64=0;
            loop64:
            do {
                int alt64=2;
                int LA64_0 = input.LA(1);

                if ( (LA64_0==ID) ) {
                    alt64=1;
                }


                switch (alt64) {
            	case 1 :
            	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:419:25: idList+= ID
            	    {
            	    idList=(DroolsTree)match(input,ID,FOLLOW_ID_in_data_type2274); 
            	    if (list_idList==null) list_idList=new ArrayList();
            	    list_idList.add(idList);


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

            // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:419:31: ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            loop65:
            do {
                int alt65=2;
                int LA65_0 = input.LA(1);

                if ( (LA65_0==LEFT_SQUARE) ) {
                    alt65=1;
                }


                switch (alt65) {
            	case 1 :
            	    // /home/davide/Projects/Eclipse/drools-trunk/trunk/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:419:32: LEFT_SQUARE rightList+= RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_data_type2278); 
            	    rightList=(DroolsTree)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_data_type2282); 
            	    if (list_rightList==null) list_rightList=new ArrayList();
            	    list_rightList.add(rightList);


            	    }
            	    break;

            	default :
            	    break loop65;
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
    public static final BitSet FOLLOW_package_statement_in_compilation_unit51 = new BitSet(new long[]{0xA5FFF00000000028L,0x0000000000000007L});
    public static final BitSet FOLLOW_statement_in_compilation_unit53 = new BitSet(new long[]{0xA5FFF00000000028L,0x0000000000000007L});
    public static final BitSet FOLLOW_VK_PACKAGE_in_package_statement71 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_package_id_in_package_statement75 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PACKAGE_ID_in_package_id102 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_package_id106 = new BitSet(new long[]{0x0000000000000008L,0x4000000000000000L});
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
    public static final BitSet FOLLOW_VK_FUNCTION_in_function_import_statement244 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement248 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_IMPORT_ID_in_import_name267 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_import_name271 = new BitSet(new long[]{0x0000000000000008L,0x4000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_DOT_STAR_in_import_name276 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_GLOBAL_in_global299 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_global303 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_VT_GLOBAL_ID_in_global307 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_FUNCTION_in_function329 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_function333 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_VT_FUNCTION_ID_in_function338 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_parameters_in_function342 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_VT_CURLY_CHUNK_in_function346 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_QUERY_in_query368 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_QUERY_ID_in_query372 = new BitSet(new long[]{0x0000080000200000L});
    public static final BitSet FOLLOW_parameters_in_query376 = new BitSet(new long[]{0x0000080000200000L});
    public static final BitSet FOLLOW_lhs_block_in_query381 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_VK_END_in_query385 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_RULE_in_rule412 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_RULE_ID_in_rule416 = new BitSet(new long[]{0x0800000000014000L,0x0000000000000000L,0x0100000000000010L});
    public static final BitSet FOLLOW_VK_EXTEND_in_rule421 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_RULE_ID_in_rule425 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_decl_metadata_in_rule435 = new BitSet(new long[]{0x0000000000014000L,0x0000000000000000L,0x0100000000000010L});
    public static final BitSet FOLLOW_rule_attributes_in_rule446 = new BitSet(new long[]{0x0000000000010000L,0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_when_part_in_rule455 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_VT_RHS_CHUNK_in_rule460 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHEN_in_when_part479 = new BitSet(new long[]{0x0000080000200000L});
    public static final BitSet FOLLOW_lhs_block_in_when_part483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_RULE_ATTRIBUTES_in_rule_attributes505 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_ATTRIBUTES_in_rule_attributes507 = new BitSet(new long[]{0x01FFF00000000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes513 = new BitSet(new long[]{0x01FFF00000000008L});
    public static final BitSet FOLLOW_VT_PARAM_LIST_in_parameters537 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_param_definition_in_parameters542 = new BitSet(new long[]{0x0000002000000008L,0x4000000000000000L});
    public static final BitSet FOLLOW_data_type_in_param_definition564 = new BitSet(new long[]{0x0000002000000008L,0x4000000000000000L});
    public static final BitSet FOLLOW_argument_in_param_definition569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_argument589 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument592 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument596 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_VK_DECLARE_in_type_declaration623 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_TYPE_DECLARE_ID_in_type_declaration627 = new BitSet(new long[]{0x1000000000000000L,0x4000000000048000L,0x0100000000000000L});
    public static final BitSet FOLLOW_VK_EXTENDS_in_type_declaration640 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_TYPE_NAME_in_type_declaration644 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_IMPLEMENTS_in_type_declaration663 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_TYPE_NAME_in_type_declaration668 = new BitSet(new long[]{0x0000000000000808L});
    public static final BitSet FOLLOW_decl_metadata_in_type_declaration688 = new BitSet(new long[]{0x0000000000000000L,0x4000000000008000L,0x0100000000000000L});
    public static final BitSet FOLLOW_decl_field_in_type_declaration701 = new BitSet(new long[]{0x0000000000000000L,0x4000000000008000L});
    public static final BitSet FOLLOW_VK_END_in_type_declaration715 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AT_in_decl_metadata747 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_TYPE_NAME_in_decl_metadata751 = new BitSet(new long[]{0x0000000000000008L,0x0800000000000000L});
    public static final BitSet FOLLOW_decl_metadata_properties_in_decl_metadata756 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PROP_KEY_in_decl_metadata_properties786 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PROP_VALUE_in_decl_metadata_properties791 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_decl_field821 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_decl_field_initialization_in_decl_field825 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_data_type_in_decl_field830 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_decl_metadata_in_decl_field835 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_decl_field_initialization862 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_decl_field_initialization866 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_SALIENCE_in_rule_attribute889 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SIGNED_DECIMAL_in_rule_attribute894 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_rule_attribute898 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_NO_LOOP_in_rule_attribute909 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute913 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_AGENDA_GROUP_in_rule_attribute925 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute929 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_TIMER_in_rule_attribute940 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SIGNED_DECIMAL_in_rule_attribute945 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_rule_attribute949 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ACTIVATION_GROUP_in_rule_attribute962 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute966 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_AUTO_FOCUS_in_rule_attribute976 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute980 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DATE_EFFECTIVE_in_rule_attribute991 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute995 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DATE_EXPIRES_in_rule_attribute1005 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute1009 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ENABLED_in_rule_attribute1019 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute1024 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_rule_attribute1028 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_RULEFLOW_GROUP_in_rule_attribute1039 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute1043 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_LOCK_ON_ACTIVE_in_rule_attribute1053 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute1057 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DIALECT_in_rule_attribute1067 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute1071 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_CALENDARS_in_rule_attribute1080 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute1084 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_AND_IMPLICIT_in_lhs_block1109 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs_block1114 = new BitSet(new long[]{0x0000000043C00008L,0x0000000000000628L,0x0000000000000200L});
    public static final BitSet FOLLOW_VT_OR_PREFIX_in_lhs1140 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1145 = new BitSet(new long[]{0x0000000043C00008L,0x0000000000000628L,0x0000000000000200L});
    public static final BitSet FOLLOW_VT_OR_INFIX_in_lhs1161 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1165 = new BitSet(new long[]{0x0000000043C00008L,0x0000000000000628L,0x0000000000000200L});
    public static final BitSet FOLLOW_lhs_in_lhs1169 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_AND_PREFIX_in_lhs1181 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1186 = new BitSet(new long[]{0x0000000043C00008L,0x0000000000000628L,0x0000000000000200L});
    public static final BitSet FOLLOW_VT_AND_INFIX_in_lhs1202 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1206 = new BitSet(new long[]{0x0000000043C00008L,0x0000000000000628L,0x0000000000000200L});
    public static final BitSet FOLLOW_lhs_in_lhs1210 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EXISTS_in_lhs1222 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1226 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_NOT_in_lhs1238 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1242 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EVAL_in_lhs1254 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_lhs1258 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_FORALL_in_lhs1270 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1275 = new BitSet(new long[]{0x0000000043C00008L,0x0000000000000628L,0x0000000000000200L});
    public static final BitSet FOLLOW_FROM_in_lhs1289 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs1293 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000010L,0x0000000000006000L});
    public static final BitSet FOLLOW_from_elements_in_lhs1297 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs1308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_from_elements1329 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_from_elements1333 = new BitSet(new long[]{0x000000000C000000L});
    public static final BitSet FOLLOW_accumulate_parts_in_from_elements1343 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_COLLECT_in_from_elements1356 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_from_elements1360 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ENTRY_POINT_in_from_elements1372 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_ENTRYPOINT_ID_in_from_elements1376 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_from_source_clause_in_from_elements1387 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accumulate_init_clause_in_accumulate_parts1408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accumulate_id_clause_in_accumulate_parts1419 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_ACCUMULATE_INIT_CLAUSE_in_accumulate_init_clause1442 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_INIT_in_accumulate_init_clause1451 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1455 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ACTION_in_accumulate_init_clause1463 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1467 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_accumulate_init_reverse_clause_in_accumulate_init_clause1476 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_VK_RESULT_in_accumulate_init_clause1483 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1487 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_REVERSE_in_accumulate_init_reverse_clause1510 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_reverse_clause1514 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ACCUMULATE_ID_CLAUSE_in_accumulate_id_clause1536 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_accumulate_id_clause1540 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_id_clause1544 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FROM_SOURCE_in_from_source_clause1568 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_EXPRESSION_CHAIN_in_expression_chain1598 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_expression_chain1602 = new BitSet(new long[]{0x00000000200C0008L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_expression_chain1606 = new BitSet(new long[]{0x0000000020080008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_expression_chain1611 = new BitSet(new long[]{0x0000000020000008L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain1619 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PATTERN_in_lhs_pattern1637 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_lhs_pattern1641 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_over_clause_in_lhs_pattern1646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OVER_in_over_clause1671 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_over_element_in_over_clause1676 = new BitSet(new long[]{0x0000000000100008L});
    public static final BitSet FOLLOW_VT_BEHAVIOR_in_over_element1697 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_over_element1699 = new BitSet(new long[]{0x0000000000000000L,0x4000000000000000L});
    public static final BitSet FOLLOW_ID_in_over_element1703 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_over_element1707 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FACT_in_fact_expression1730 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_pattern_type_in_fact_expression1734 = new BitSet(new long[]{0x0000000F80080048L,0x0700000000004048L,0x000000007CFD9042L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1739 = new BitSet(new long[]{0x0000000F80080048L,0x0700000000004048L,0x000000007CFD9042L});
    public static final BitSet FOLLOW_VT_FACT_BINDING_in_fact_expression1753 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_LABEL_in_fact_expression1757 = new BitSet(new long[]{0x0000000F80080040L,0x0700000000004048L,0x000000007CFD9042L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1761 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FACT_OR_in_fact_expression1773 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1777 = new BitSet(new long[]{0x0000000F80080040L,0x0700000000004048L,0x000000007CFD9042L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1781 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FIELD_in_fact_expression1792 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_field_element_in_fact_expression1796 = new BitSet(new long[]{0x0000000F80080048L,0x0700000000004048L,0x000000007CFD9042L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1800 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_BIND_FIELD_in_fact_expression1811 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_LABEL_in_fact_expression1815 = new BitSet(new long[]{0x0000000F80080040L,0x0700000000004048L,0x000000007CFD9042L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1819 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EVAL_in_fact_expression1830 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_fact_expression1834 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EQUALS_in_fact_expression1847 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1851 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_EQUALS_in_fact_expression1863 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1867 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GREATER_in_fact_expression1879 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1883 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GREATER_EQUALS_in_fact_expression1895 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1899 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LESS_in_fact_expression1911 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1915 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LESS_EQUALS_in_fact_expression1927 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1931 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_OPERATOR_in_fact_expression1943 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1947 = new BitSet(new long[]{0x0000000F800C0040L,0x0700000000004048L,0x000000007CFD9042L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_fact_expression1952 = new BitSet(new long[]{0x0000000F80080040L,0x0700000000004048L,0x000000007CFD9042L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1957 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_IN_in_fact_expression1968 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1972 = new BitSet(new long[]{0x0000000F80080040L,0x0700000000004048L,0x000000007CFD9042L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1978 = new BitSet(new long[]{0x0000000F80080048L,0x0700000000004048L,0x000000007CFD9042L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_fact_expression1993 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1997 = new BitSet(new long[]{0x0000000F80080040L,0x0700000000004048L,0x000000007CFD9042L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2001 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_fact_expression2011 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2015 = new BitSet(new long[]{0x0000000F80080040L,0x0700000000004048L,0x000000007CFD9042L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2019 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ACCESSOR_PATH_in_fact_expression2030 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_accessor_element_in_fact_expression2035 = new BitSet(new long[]{0x0000001000000008L});
    public static final BitSet FOLLOW_STRING_in_fact_expression2050 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_fact_expression2059 = new BitSet(new long[]{0x0000000000000000L,0x0700000000000000L,0x0000000060001000L});
    public static final BitSet FOLLOW_MINUS_in_fact_expression2063 = new BitSet(new long[]{0x0000000000000000L,0x0700000000000000L,0x0000000060001000L});
    public static final BitSet FOLLOW_DECIMAL_in_fact_expression2082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIGNED_DECIMAL_in_fact_expression2086 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HEX_in_fact_expression2098 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIGNED_HEX_in_fact_expression2102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_fact_expression2116 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIGNED_FLOAT_in_fact_expression2120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_fact_expression2136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_fact_expression2146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_fact_expression2156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_ACCESSOR_PATH_in_field_element2178 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_accessor_element_in_field_element2183 = new BitSet(new long[]{0x0000001000000008L});
    public static final BitSet FOLLOW_VT_ACCESSOR_ELEMENT_in_accessor_element2207 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_accessor_element2211 = new BitSet(new long[]{0x0000000000040008L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_accessor_element2215 = new BitSet(new long[]{0x0000000000040008L});
    public static final BitSet FOLLOW_VT_PATTERN_TYPE_in_pattern_type2236 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_pattern_type2240 = new BitSet(new long[]{0x0000000000000008L,0x4000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_pattern_type2244 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_pattern_type2248 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_VT_DATA_TYPE_in_data_type2270 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_data_type2274 = new BitSet(new long[]{0x0000000000000008L,0x4000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_data_type2278 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_data_type2282 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000000L,0x0000000001000000L});

}