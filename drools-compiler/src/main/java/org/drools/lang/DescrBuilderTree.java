// $ANTLR 3.2 Sep 23, 2009 14:05:07 src/main/resources/org/drools/lang/DescrBuilderTree.g 2010-10-25 14:22:35

	package org.drools.lang;

	import java.util.HashMap;
	import java.util.Map;
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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_COMPILATION_UNIT", "VT_FUNCTION_IMPORT", "VT_FACT", "VT_CONSTRAINTS", "VT_LABEL", "VT_QUERY_ID", "VT_TYPE_DECLARE_ID", "VT_RULE_ID", "VT_ENTRYPOINT_ID", "VT_RULE_ATTRIBUTES", "VT_PKG_ATTRIBUTES", "VT_RHS_CHUNK", "VT_CURLY_CHUNK", "VT_SQUARE_CHUNK", "VT_PAREN_CHUNK", "VT_BEHAVIOR", "VT_AND_IMPLICIT", "VT_AND_PREFIX", "VT_OR_PREFIX", "VT_AND_INFIX", "VT_OR_INFIX", "VT_ACCUMULATE_INIT_CLAUSE", "VT_ACCUMULATE_ID_CLAUSE", "VT_FROM_SOURCE", "VT_EXPRESSION_CHAIN", "VT_PATTERN", "VT_FACT_BINDING", "VT_FACT_OR", "VT_BIND_FIELD", "VT_FIELD", "VT_ACCESSOR_PATH", "VT_ACCESSOR_ELEMENT", "VT_DATA_TYPE", "VT_PATTERN_TYPE", "VT_PACKAGE_ID", "VT_IMPORT_ID", "VT_GLOBAL_ID", "VT_FUNCTION_ID", "VT_PARAM_LIST", "VK_DATE_EFFECTIVE", "VK_DATE_EXPIRES", "VK_LOCK_ON_ACTIVE", "VK_NO_LOOP", "VK_AUTO_FOCUS", "VK_ACTIVATION_GROUP", "VK_AGENDA_GROUP", "VK_RULEFLOW_GROUP", "VK_TIMER", "VK_CALENDARS", "VK_DIALECT", "VK_SALIENCE", "VK_ENABLED", "VK_ATTRIBUTES", "VK_RULE", "VK_EXTEND", "VK_IMPORT", "VK_PACKAGE", "VK_QUERY", "VK_DECLARE", "VK_FUNCTION", "VK_GLOBAL", "VK_EVAL", "VK_ENTRY_POINT", "VK_NOT", "VK_IN", "VK_OR", "VK_AND", "VK_EXISTS", "VK_FORALL", "VK_ACTION", "VK_REVERSE", "VK_RESULT", "VK_OPERATOR", "VK_END", "VK_INIT", "VK_INSTANCEOF", "VK_EXTENDS", "VK_SUPER", "VK_PRIMITIVE_TYPE", "VK_THIS", "VK_VOID", "VK_CLASS", "VK_NEW", "SIGNED_DECIMAL", "SIGNED_HEX", "SIGNED_FLOAT", "SEMICOLON", "ID", "DOT", "DOT_STAR", "STRING", "AT", "COLON", "EQUALS_ASSIGN", "WHEN", "COMMA", "BOOL", "LEFT_PAREN", "RIGHT_PAREN", "FROM", "OVER", "ACCUMULATE", "COLLECT", "DOUBLE_PIPE", "DOUBLE_AMPER", "ARROW", "EQUALS", "GREATER", "GREATER_EQUALS", "LESS", "LESS_EQUALS", "NOT_EQUALS", "NULL", "PLUS", "MINUS", "DECIMAL", "HEX", "FLOAT", "LEFT_SQUARE", "RIGHT_SQUARE", "THEN", "LEFT_CURLY", "RIGHT_CURLY", "QUESTION", "PIPE", "XOR", "AMPER", "SHIFT_LEFT", "SHIFT_RIGHT_UNSIG", "SHIFT_RIGHT", "STAR", "DIV", "MOD", "INCR", "DECR", "TILDE", "NEGATION", "PLUS_ASSIGN", "MINUS_ASSIGN", "MULT_ASSIGN", "DIV_ASSIGN", "AND_ASSIGN", "OR_ASSIGN", "XOR_ASSIGN", "MOD_ASSIGN", "EOL", "WS", "Exponent", "FloatTypeSuffix", "HexDigit", "IntegerTypeSuffix", "EscapeSequence", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "IdentifierStart", "IdentifierPart", "MISC"
    };
    public static final int COMMA=99;
    public static final int VT_PATTERN_TYPE=37;
    public static final int VT_ACCUMULATE_ID_CLAUSE=26;
    public static final int MINUS=118;
    public static final int VK_DIALECT=53;
    public static final int VK_FUNCTION=63;
    public static final int HexDigit=153;
    public static final int VK_ATTRIBUTES=56;
    public static final int XOR_ASSIGN=147;
    public static final int OR_ASSIGN=146;
    public static final int VT_EXPRESSION_CHAIN=28;
    public static final int MISC=163;
    public static final int VT_AND_PREFIX=21;
    public static final int VK_QUERY=61;
    public static final int VK_INSTANCEOF=79;
    public static final int THEN=124;
    public static final int VK_AUTO_FOCUS=47;
    public static final int TILDE=139;
    public static final int AND_ASSIGN=145;
    public static final int PIPE=128;
    public static final int DOT=92;
    public static final int VK_IMPORT=59;
    public static final int NOT_EQUALS=115;
    public static final int MULT_ASSIGN=143;
    public static final int VT_PACKAGE_ID=38;
    public static final int LEFT_SQUARE=122;
    public static final int VK_TIMER=51;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=158;
    public static final int VT_DATA_TYPE=36;
    public static final int PLUS=117;
    public static final int VT_FACT=6;
    public static final int LEFT_CURLY=125;
    public static final int MINUS_ASSIGN=142;
    public static final int FloatTypeSuffix=152;
    public static final int AT=95;
    public static final int VK_VOID=84;
    public static final int LEFT_PAREN=101;
    public static final int DOUBLE_AMPER=108;
    public static final int IdentifierPart=162;
    public static final int VT_QUERY_ID=9;
    public static final int IntegerTypeSuffix=154;
    public static final int NEGATION=140;
    public static final int VT_ACCESSOR_PATH=34;
    public static final int VT_LABEL=8;
    public static final int WHEN=98;
    public static final int MOD_ASSIGN=148;
    public static final int VT_ENTRYPOINT_ID=12;
    public static final int WS=150;
    public static final int VT_FIELD=33;
    public static final int VK_SALIENCE=54;
    public static final int OVER=104;
    public static final int STRING=94;
    public static final int VK_AND=70;
    public static final int LESS_EQUALS=114;
    public static final int VT_ACCESSOR_ELEMENT=35;
    public static final int VT_ACCUMULATE_INIT_CLAUSE=25;
    public static final int VK_GLOBAL=64;
    public static final int VK_REVERSE=74;
    public static final int VT_BEHAVIOR=19;
    public static final int VT_SQUARE_CHUNK=17;
    public static final int VK_FORALL=72;
    public static final int VT_COMPILATION_UNIT=4;
    public static final int VT_PAREN_CHUNK=18;
    public static final int COLLECT=106;
    public static final int VK_ENABLED=55;
    public static final int EQUALS=110;
    public static final int VK_RESULT=75;
    public static final int UnicodeEscape=156;
    public static final int DIV_ASSIGN=144;
    public static final int VK_PACKAGE=60;
    public static final int VT_RULE_ID=11;
    public static final int SIGNED_FLOAT=89;
    public static final int VK_NO_LOOP=46;
    public static final int IdentifierStart=161;
    public static final int SEMICOLON=90;
    public static final int EQUALS_ASSIGN=97;
    public static final int VT_AND_IMPLICIT=20;
    public static final int VK_THIS=83;
    public static final int XOR=129;
    public static final int NULL=116;
    public static final int COLON=96;
    public static final int AMPER=130;
    public static final int MULTI_LINE_COMMENT=160;
    public static final int DIV=135;
    public static final int VT_RULE_ATTRIBUTES=13;
    public static final int HEX=120;
    public static final int RIGHT_SQUARE=123;
    public static final int SHIFT_LEFT=131;
    public static final int VK_AGENDA_GROUP=49;
    public static final int VT_FACT_OR=31;
    public static final int INCR=137;
    public static final int VK_NOT=67;
    public static final int VK_DATE_EXPIRES=44;
    public static final int DECR=138;
    public static final int ARROW=109;
    public static final int FLOAT=121;
    public static final int VK_EXTEND=58;
    public static final int MOD=136;
    public static final int PLUS_ASSIGN=141;
    public static final int VT_CURLY_CHUNK=16;
    public static final int VK_NEW=86;
    public static final int DECIMAL=119;
    public static final int QUESTION=127;
    public static final int VT_OR_PREFIX=22;
    public static final int VK_END=77;
    public static final int DOUBLE_PIPE=107;
    public static final int LESS=113;
    public static final int VT_TYPE_DECLARE_ID=10;
    public static final int VT_PATTERN=29;
    public static final int VK_DATE_EFFECTIVE=43;
    public static final int VK_EXISTS=71;
    public static final int EscapeSequence=155;
    public static final int VT_BIND_FIELD=32;
    public static final int VK_RULE=57;
    public static final int VK_EVAL=65;
    public static final int GREATER=111;
    public static final int VT_FACT_BINDING=30;
    public static final int VT_PKG_ATTRIBUTES=14;
    public static final int ID=91;
    public static final int FROM=103;
    public static final int VK_PRIMITIVE_TYPE=82;
    public static final int RIGHT_CURLY=126;
    public static final int VK_OPERATOR=76;
    public static final int Exponent=151;
    public static final int VK_ENTRY_POINT=66;
    public static final int VT_AND_INFIX=23;
    public static final int VT_PARAM_LIST=42;
    public static final int BOOL=100;
    public static final int VT_FROM_SOURCE=27;
    public static final int VK_LOCK_ON_ACTIVE=45;
    public static final int VT_FUNCTION_IMPORT=5;
    public static final int VK_SUPER=81;
    public static final int VK_IN=68;
    public static final int VT_RHS_CHUNK=15;
    public static final int VT_OR_INFIX=24;
    public static final int VK_CLASS=85;
    public static final int DOT_STAR=93;
    public static final int VK_OR=69;
    public static final int VT_GLOBAL_ID=40;
    public static final int ACCUMULATE=105;
    public static final int GREATER_EQUALS=112;
    public static final int VK_RULEFLOW_GROUP=50;
    public static final int SIGNED_DECIMAL=87;
    public static final int VT_FUNCTION_ID=41;
    public static final int SHIFT_RIGHT=133;
    public static final int EOF=-1;
    public static final int VT_CONSTRAINTS=7;
    public static final int VT_IMPORT_ID=39;
    public static final int EOL=149;
    public static final int VK_INIT=78;
    public static final int VK_ACTIVATION_GROUP=48;
    public static final int VK_EXTENDS=80;
    public static final int OctalEscape=157;
    public static final int VK_ACTION=73;
    public static final int SIGNED_HEX=88;
    public static final int STAR=134;
    public static final int RIGHT_PAREN=102;
    public static final int VK_CALENDARS=52;
    public static final int SHIFT_RIGHT_UNSIG=132;
    public static final int VK_DECLARE=62;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=159;

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:48:1: compilation_unit : ^( VT_COMPILATION_UNIT package_statement ( statement )* ) ;
    public final void compilation_unit() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:49:2: ( ^( VT_COMPILATION_UNIT package_statement ( statement )* ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:49:4: ^( VT_COMPILATION_UNIT package_statement ( statement )* )
            {
            match(input,VT_COMPILATION_UNIT,FOLLOW_VT_COMPILATION_UNIT_in_compilation_unit49); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                pushFollow(FOLLOW_package_statement_in_compilation_unit51);
                package_statement();

                state._fsp--;

                // src/main/resources/org/drools/lang/DescrBuilderTree.g:49:44: ( statement )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==VT_FUNCTION_IMPORT||(LA1_0>=VK_DATE_EFFECTIVE && LA1_0<=VK_ENABLED)||LA1_0==VK_RULE||LA1_0==VK_IMPORT||(LA1_0>=VK_QUERY && LA1_0<=VK_GLOBAL)) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:49:44: statement
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:52:1: package_statement returns [String packageName] : ( ^( VK_PACKAGE packageId= package_id ) | );
    public final String package_statement() throws RecognitionException {
        String packageName = null;

        List packageId = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:53:2: ( ^( VK_PACKAGE packageId= package_id ) | )
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
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:53:4: ^( VK_PACKAGE packageId= package_id )
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
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:57:2: 
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:61:1: package_id returns [List idList] : ^( VT_PACKAGE_ID (tempList+= ID )+ ) ;
    public final List package_id() throws RecognitionException {
        List idList = null;

        DroolsTree tempList=null;
        List list_tempList=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:62:2: ( ^( VT_PACKAGE_ID (tempList+= ID )+ ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:62:4: ^( VT_PACKAGE_ID (tempList+= ID )+ )
            {
            match(input,VT_PACKAGE_ID,FOLLOW_VT_PACKAGE_ID_in_package_id102); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:62:28: (tempList+= ID )+
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
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:62:28: tempList+= ID
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:66:1: statement : (a= rule_attribute | fi= function_import_statement | is= import_statement | gl= global | fn= function | rl= rule | qr= query | td= type_declaration );
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
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:67:2: (a= rule_attribute | fi= function_import_statement | is= import_statement | gl= global | fn= function | rl= rule | qr= query | td= type_declaration )
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
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:67:4: a= rule_attribute
                    {
                    pushFollow(FOLLOW_rule_attribute_in_statement124);
                    a=rule_attribute();

                    state._fsp--;

                    	this.packageDescr.addAttribute(a);	

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:69:4: fi= function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement134);
                    fi=function_import_statement();

                    state._fsp--;

                    	this.packageDescr.addFunctionImport(fi);	

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:71:4: is= import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement144);
                    is=import_statement();

                    state._fsp--;

                    	this.packageDescr.addImport(is);	

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:73:4: gl= global
                    {
                    pushFollow(FOLLOW_global_in_statement155);
                    gl=global();

                    state._fsp--;

                    	this.packageDescr.addGlobal((gl!=null?gl.globalDescr:null));	

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:75:4: fn= function
                    {
                    pushFollow(FOLLOW_function_in_statement165);
                    fn=function();

                    state._fsp--;

                    	this.packageDescr.addFunction((fn!=null?fn.functionDescr:null));	

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:77:4: rl= rule
                    {
                    pushFollow(FOLLOW_rule_in_statement175);
                    rl=rule();

                    state._fsp--;

                    	this.packageDescr.addRule((rl!=null?rl.ruleDescr:null));	

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:79:4: qr= query
                    {
                    pushFollow(FOLLOW_query_in_statement185);
                    qr=query();

                    state._fsp--;

                    	this.packageDescr.addRule((qr!=null?qr.queryDescr:null));	

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:81:4: td= type_declaration
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:85:1: import_statement returns [ImportDescr importDescr] : ^(importStart= VK_IMPORT importId= import_name ) ;
    public final ImportDescr import_statement() throws RecognitionException {
        ImportDescr importDescr = null;

        DroolsTree importStart=null;
        DescrBuilderTree.import_name_return importId = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:86:2: ( ^(importStart= VK_IMPORT importId= import_name ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:86:4: ^(importStart= VK_IMPORT importId= import_name )
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:90:1: function_import_statement returns [FunctionImportDescr functionImportDescr] : ^(importStart= VT_FUNCTION_IMPORT VK_FUNCTION importId= import_name ) ;
    public final FunctionImportDescr function_import_statement() throws RecognitionException {
        FunctionImportDescr functionImportDescr = null;

        DroolsTree importStart=null;
        DescrBuilderTree.import_name_return importId = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:91:2: ( ^(importStart= VT_FUNCTION_IMPORT VK_FUNCTION importId= import_name ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:91:4: ^(importStart= VT_FUNCTION_IMPORT VK_FUNCTION importId= import_name )
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:95:1: import_name returns [List idList, DroolsTree dotStar] : ^( VT_IMPORT_ID (tempList+= ID )+ (tempDotStar= DOT_STAR )? ) ;
    public final DescrBuilderTree.import_name_return import_name() throws RecognitionException {
        DescrBuilderTree.import_name_return retval = new DescrBuilderTree.import_name_return();
        retval.start = input.LT(1);

        DroolsTree tempDotStar=null;
        DroolsTree tempList=null;
        List list_tempList=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:96:2: ( ^( VT_IMPORT_ID (tempList+= ID )+ (tempDotStar= DOT_STAR )? ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:96:4: ^( VT_IMPORT_ID (tempList+= ID )+ (tempDotStar= DOT_STAR )? )
            {
            match(input,VT_IMPORT_ID,FOLLOW_VT_IMPORT_ID_in_import_name267); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:96:27: (tempList+= ID )+
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
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:96:27: tempList+= ID
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

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:96:44: (tempDotStar= DOT_STAR )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==DOT_STAR) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:96:44: tempDotStar= DOT_STAR
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:101:1: global returns [GlobalDescr globalDescr] : ^(start= VK_GLOBAL dt= data_type globalId= VT_GLOBAL_ID ) ;
    public final DescrBuilderTree.global_return global() throws RecognitionException {
        DescrBuilderTree.global_return retval = new DescrBuilderTree.global_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree globalId=null;
        BaseDescr dt = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:102:2: ( ^(start= VK_GLOBAL dt= data_type globalId= VT_GLOBAL_ID ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:102:4: ^(start= VK_GLOBAL dt= data_type globalId= VT_GLOBAL_ID )
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:106:1: function returns [FunctionDescr functionDescr] : ^(start= VK_FUNCTION (dt= data_type )? functionId= VT_FUNCTION_ID params= parameters content= VT_CURLY_CHUNK ) ;
    public final DescrBuilderTree.function_return function() throws RecognitionException {
        DescrBuilderTree.function_return retval = new DescrBuilderTree.function_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree functionId=null;
        DroolsTree content=null;
        BaseDescr dt = null;

        List params = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:107:2: ( ^(start= VK_FUNCTION (dt= data_type )? functionId= VT_FUNCTION_ID params= parameters content= VT_CURLY_CHUNK ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:107:4: ^(start= VK_FUNCTION (dt= data_type )? functionId= VT_FUNCTION_ID params= parameters content= VT_CURLY_CHUNK )
            {
            start=(DroolsTree)match(input,VK_FUNCTION,FOLLOW_VK_FUNCTION_in_function329); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:107:26: (dt= data_type )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==VT_DATA_TYPE) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:107:26: dt= data_type
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:111:1: query returns [QueryDescr queryDescr] : ^(start= VK_QUERY id= VT_QUERY_ID (params= parameters )? lb= lhs_block end= VK_END ) ;
    public final DescrBuilderTree.query_return query() throws RecognitionException {
        DescrBuilderTree.query_return retval = new DescrBuilderTree.query_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree id=null;
        DroolsTree end=null;
        List params = null;

        AndDescr lb = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:112:2: ( ^(start= VK_QUERY id= VT_QUERY_ID (params= parameters )? lb= lhs_block end= VK_END ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:112:4: ^(start= VK_QUERY id= VT_QUERY_ID (params= parameters )? lb= lhs_block end= VK_END )
            {
            start=(DroolsTree)match(input,VK_QUERY,FOLLOW_VK_QUERY_in_query368); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,VT_QUERY_ID,FOLLOW_VT_QUERY_ID_in_query372); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:112:42: (params= parameters )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==VT_PARAM_LIST) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:112:42: params= parameters
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:116:1: rule returns [RuleDescr ruleDescr] : ^(start= VK_RULE id= VT_RULE_ID ( ^( VK_EXTEND parent_id= VT_RULE_ID ) )? (dm= decl_metadata )* (ra= rule_attributes )? (wn= when_part )? content= VT_RHS_CHUNK ) ;
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
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:118:2: ( ^(start= VK_RULE id= VT_RULE_ID ( ^( VK_EXTEND parent_id= VT_RULE_ID ) )? (dm= decl_metadata )* (ra= rule_attributes )? (wn= when_part )? content= VT_RHS_CHUNK ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:118:4: ^(start= VK_RULE id= VT_RULE_ID ( ^( VK_EXTEND parent_id= VT_RULE_ID ) )? (dm= decl_metadata )* (ra= rule_attributes )? (wn= when_part )? content= VT_RHS_CHUNK )
            {
            start=(DroolsTree)match(input,VK_RULE,FOLLOW_VK_RULE_in_rule412); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,VT_RULE_ID,FOLLOW_VT_RULE_ID_in_rule416); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:118:35: ( ^( VK_EXTEND parent_id= VT_RULE_ID ) )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==VK_EXTEND) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:118:36: ^( VK_EXTEND parent_id= VT_RULE_ID )
                    {
                    match(input,VK_EXTEND,FOLLOW_VK_EXTEND_in_rule421); 

                    match(input, Token.DOWN, null); 
                    parent_id=(DroolsTree)match(input,VT_RULE_ID,FOLLOW_VT_RULE_ID_in_rule425); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:119:3: (dm= decl_metadata )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==AT) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:119:4: dm= decl_metadata
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

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:120:6: (ra= rule_attributes )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==VT_RULE_ATTRIBUTES) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:120:6: ra= rule_attributes
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule446);
                    ra=rule_attributes();

                    state._fsp--;


                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:121:6: (wn= when_part )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==WHEN) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:121:6: wn= when_part
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:125:1: when_part returns [AndDescr andDescr] : WHEN lh= lhs_block ;
    public final AndDescr when_part() throws RecognitionException {
        AndDescr andDescr = null;

        AndDescr lh = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:126:2: ( WHEN lh= lhs_block )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:126:4: WHEN lh= lhs_block
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:130:1: rule_attributes returns [List attrList] : ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? (rl= rule_attribute )+ ) ;
    public final List rule_attributes() throws RecognitionException {
        List attrList = null;

        AttributeDescr rl = null;



        	attrList = new LinkedList<AttributeDescr>();

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:133:3: ( ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? (rl= rule_attribute )+ ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:133:5: ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? (rl= rule_attribute )+ )
            {
            match(input,VT_RULE_ATTRIBUTES,FOLLOW_VT_RULE_ATTRIBUTES_in_rule_attributes505); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:133:26: ( VK_ATTRIBUTES )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==VK_ATTRIBUTES) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:133:26: VK_ATTRIBUTES
                    {
                    match(input,VK_ATTRIBUTES,FOLLOW_VK_ATTRIBUTES_in_rule_attributes507); 

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:133:41: (rl= rule_attribute )+
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
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:133:42: rl= rule_attribute
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:136:1: parameters returns [List paramList] : ^( VT_PARAM_LIST (p= param_definition )* ) ;
    public final List parameters() throws RecognitionException {
        List paramList = null;

        Map p = null;



        	paramList = new LinkedList<Map<BaseDescr, BaseDescr>>();

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:139:3: ( ^( VT_PARAM_LIST (p= param_definition )* ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:139:5: ^( VT_PARAM_LIST (p= param_definition )* )
            {
            match(input,VT_PARAM_LIST,FOLLOW_VT_PARAM_LIST_in_parameters537); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // src/main/resources/org/drools/lang/DescrBuilderTree.g:139:21: (p= param_definition )*
                loop15:
                do {
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==VT_DATA_TYPE||LA15_0==ID) ) {
                        alt15=1;
                    }


                    switch (alt15) {
                	case 1 :
                	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:139:22: p= param_definition
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:142:1: param_definition returns [Map param] : (dt= data_type )? a= argument ;
    public final Map param_definition() throws RecognitionException {
        Map param = null;

        BaseDescr dt = null;

        BaseDescr a = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:143:2: ( (dt= data_type )? a= argument )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:143:4: (dt= data_type )? a= argument
            {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:143:6: (dt= data_type )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==VT_DATA_TYPE) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:143:6: dt= data_type
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:148:1: argument returns [BaseDescr arg] : id= ID ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ;
    public final BaseDescr argument() throws RecognitionException {
        BaseDescr arg = null;

        DroolsTree id=null;
        DroolsTree rightList=null;
        List list_rightList=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:149:2: (id= ID ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:149:4: id= ID ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            {
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_argument589); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:149:10: ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==LEFT_SQUARE) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:149:11: LEFT_SQUARE rightList+= RIGHT_SQUARE
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:153:1: type_declaration returns [TypeDeclarationDescr declaration] : ^( VK_DECLARE id= VT_TYPE_DECLARE_ID (dm= decl_metadata )* (df= decl_field )* VK_END ) ;
    public final TypeDeclarationDescr type_declaration() throws RecognitionException {
        TypeDeclarationDescr declaration = null;

        DroolsTree id=null;
        Map dm = null;

        TypeFieldDescr df = null;


        	List<Map> declMetadaList = new LinkedList<Map>();
        		List<TypeFieldDescr> declFieldList = new LinkedList<TypeFieldDescr>(); 
        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:156:2: ( ^( VK_DECLARE id= VT_TYPE_DECLARE_ID (dm= decl_metadata )* (df= decl_field )* VK_END ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:156:4: ^( VK_DECLARE id= VT_TYPE_DECLARE_ID (dm= decl_metadata )* (df= decl_field )* VK_END )
            {
            match(input,VK_DECLARE,FOLLOW_VK_DECLARE_in_type_declaration622); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,VT_TYPE_DECLARE_ID,FOLLOW_VT_TYPE_DECLARE_ID_in_type_declaration626); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:157:4: (dm= decl_metadata )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==AT) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:157:5: dm= decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_type_declaration635);
            	    dm=decl_metadata();

            	    state._fsp--;

            	    declMetadaList.add(dm);	

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:158:4: (df= decl_field )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==ID) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:158:5: df= decl_field
            	    {
            	    pushFollow(FOLLOW_decl_field_in_type_declaration648);
            	    df=decl_field();

            	    state._fsp--;

            	    declFieldList.add(df);	

            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);

            match(input,VK_END,FOLLOW_VK_END_in_type_declaration654); 

            match(input, Token.UP, null); 
            	declaration = factory.createTypeDeclr(id, declMetadaList, declFieldList);	

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:162:1: decl_metadata returns [Map attData] : ^( AT att= ID (pc= VT_PAREN_CHUNK )? ) ;
    public final Map decl_metadata() throws RecognitionException {
        Map attData = null;

        DroolsTree att=null;
        DroolsTree pc=null;

        attData = new HashMap();
        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:164:2: ( ^( AT att= ID (pc= VT_PAREN_CHUNK )? ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:164:4: ^( AT att= ID (pc= VT_PAREN_CHUNK )? )
            {
            match(input,AT,FOLLOW_AT_in_decl_metadata679); 

            match(input, Token.DOWN, null); 
            att=(DroolsTree)match(input,ID,FOLLOW_ID_in_decl_metadata683); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:164:18: (pc= VT_PAREN_CHUNK )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==VT_PAREN_CHUNK) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:164:18: pc= VT_PAREN_CHUNK
                    {
                    pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_decl_metadata687); 

                    }
                    break;

            }


            match(input, Token.UP, null); 
            	attData.put(att, pc);	

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
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_decl_field715); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:170:16: (init= decl_field_initialization )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==EQUALS_ASSIGN) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:170:16: init= decl_field_initialization
                    {
                    pushFollow(FOLLOW_decl_field_initialization_in_decl_field719);
                    init=decl_field_initialization();

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_data_type_in_decl_field724);
            dt=data_type();

            state._fsp--;

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:170:57: (dm= decl_metadata )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==AT) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:170:58: dm= decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_decl_field729);
            	    dm=decl_metadata();

            	    state._fsp--;

            	    declMetadaList.add(dm);

            	    }
            	    break;

            	default :
            	    break loop22;
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
            match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_decl_field_initialization756); 

            match(input, Token.DOWN, null); 
            pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_decl_field_initialization760); 

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
            int alt29=13;
            switch ( input.LA(1) ) {
            case VK_SALIENCE:
                {
                alt29=1;
                }
                break;
            case VK_NO_LOOP:
                {
                alt29=2;
                }
                break;
            case VK_AGENDA_GROUP:
                {
                alt29=3;
                }
                break;
            case VK_TIMER:
                {
                alt29=4;
                }
                break;
            case VK_ACTIVATION_GROUP:
                {
                alt29=5;
                }
                break;
            case VK_AUTO_FOCUS:
                {
                alt29=6;
                }
                break;
            case VK_DATE_EFFECTIVE:
                {
                alt29=7;
                }
                break;
            case VK_DATE_EXPIRES:
                {
                alt29=8;
                }
                break;
            case VK_ENABLED:
                {
                alt29=9;
                }
                break;
            case VK_RULEFLOW_GROUP:
                {
                alt29=10;
                }
                break;
            case VK_LOCK_ON_ACTIVE:
                {
                alt29=11;
                }
                break;
            case VK_DIALECT:
                {
                alt29=12;
                }
                break;
            case VK_CALENDARS:
                {
                alt29=13;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:180:5: ^(attrName= VK_SALIENCE (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) )
                    {
                    attrName=(DroolsTree)match(input,VK_SALIENCE,FOLLOW_VK_SALIENCE_in_rule_attribute783); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:180:28: (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK )
                    int alt23=2;
                    int LA23_0 = input.LA(1);

                    if ( (LA23_0==SIGNED_DECIMAL) ) {
                        alt23=1;
                    }
                    else if ( (LA23_0==VT_PAREN_CHUNK) ) {
                        alt23=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 23, 0, input);

                        throw nvae;
                    }
                    switch (alt23) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:180:29: value= SIGNED_DECIMAL
                            {
                            value=(DroolsTree)match(input,SIGNED_DECIMAL,FOLLOW_SIGNED_DECIMAL_in_rule_attribute788); 

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:180:50: value= VT_PAREN_CHUNK
                            {
                            value=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_rule_attribute792); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:181:4: ^(attrName= VK_NO_LOOP (value= BOOL )? )
                    {
                    attrName=(DroolsTree)match(input,VK_NO_LOOP,FOLLOW_VK_NO_LOOP_in_rule_attribute803); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // src/main/resources/org/drools/lang/DescrBuilderTree.g:181:31: (value= BOOL )?
                        int alt24=2;
                        int LA24_0 = input.LA(1);

                        if ( (LA24_0==BOOL) ) {
                            alt24=1;
                        }
                        switch (alt24) {
                            case 1 :
                                // src/main/resources/org/drools/lang/DescrBuilderTree.g:181:31: value= BOOL
                                {
                                value=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_rule_attribute807); 

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
                    attrName=(DroolsTree)match(input,VK_AGENDA_GROUP,FOLLOW_VK_AGENDA_GROUP_in_rule_attribute819); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute823); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:183:4: ^(attrName= VK_TIMER (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK ) )
                    {
                    attrName=(DroolsTree)match(input,VK_TIMER,FOLLOW_VK_TIMER_in_rule_attribute834); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:183:24: (value= SIGNED_DECIMAL | value= VT_PAREN_CHUNK )
                    int alt25=2;
                    int LA25_0 = input.LA(1);

                    if ( (LA25_0==SIGNED_DECIMAL) ) {
                        alt25=1;
                    }
                    else if ( (LA25_0==VT_PAREN_CHUNK) ) {
                        alt25=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 25, 0, input);

                        throw nvae;
                    }
                    switch (alt25) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:183:25: value= SIGNED_DECIMAL
                            {
                            value=(DroolsTree)match(input,SIGNED_DECIMAL,FOLLOW_SIGNED_DECIMAL_in_rule_attribute839); 

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:183:46: value= VT_PAREN_CHUNK
                            {
                            value=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_rule_attribute843); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:184:4: ^(attrName= VK_ACTIVATION_GROUP value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_ACTIVATION_GROUP,FOLLOW_VK_ACTIVATION_GROUP_in_rule_attribute856); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute860); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:185:4: ^(attrName= VK_AUTO_FOCUS (value= BOOL )? )
                    {
                    attrName=(DroolsTree)match(input,VK_AUTO_FOCUS,FOLLOW_VK_AUTO_FOCUS_in_rule_attribute870); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // src/main/resources/org/drools/lang/DescrBuilderTree.g:185:34: (value= BOOL )?
                        int alt26=2;
                        int LA26_0 = input.LA(1);

                        if ( (LA26_0==BOOL) ) {
                            alt26=1;
                        }
                        switch (alt26) {
                            case 1 :
                                // src/main/resources/org/drools/lang/DescrBuilderTree.g:185:34: value= BOOL
                                {
                                value=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_rule_attribute874); 

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
                    attrName=(DroolsTree)match(input,VK_DATE_EFFECTIVE,FOLLOW_VK_DATE_EFFECTIVE_in_rule_attribute885); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute889); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:187:4: ^(attrName= VK_DATE_EXPIRES value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_DATE_EXPIRES,FOLLOW_VK_DATE_EXPIRES_in_rule_attribute899); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute903); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:188:4: ^(attrName= VK_ENABLED (value= BOOL | value= VT_PAREN_CHUNK ) )
                    {
                    attrName=(DroolsTree)match(input,VK_ENABLED,FOLLOW_VK_ENABLED_in_rule_attribute913); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:188:26: (value= BOOL | value= VT_PAREN_CHUNK )
                    int alt27=2;
                    int LA27_0 = input.LA(1);

                    if ( (LA27_0==BOOL) ) {
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
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:188:27: value= BOOL
                            {
                            value=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_rule_attribute918); 

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:188:38: value= VT_PAREN_CHUNK
                            {
                            value=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_rule_attribute922); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 10 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:189:4: ^(attrName= VK_RULEFLOW_GROUP value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_RULEFLOW_GROUP,FOLLOW_VK_RULEFLOW_GROUP_in_rule_attribute933); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute937); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 11 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:190:4: ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? )
                    {
                    attrName=(DroolsTree)match(input,VK_LOCK_ON_ACTIVE,FOLLOW_VK_LOCK_ON_ACTIVE_in_rule_attribute947); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // src/main/resources/org/drools/lang/DescrBuilderTree.g:190:38: (value= BOOL )?
                        int alt28=2;
                        int LA28_0 = input.LA(1);

                        if ( (LA28_0==BOOL) ) {
                            alt28=1;
                        }
                        switch (alt28) {
                            case 1 :
                                // src/main/resources/org/drools/lang/DescrBuilderTree.g:190:38: value= BOOL
                                {
                                value=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_rule_attribute951); 

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
                    attrName=(DroolsTree)match(input,VK_DIALECT,FOLLOW_VK_DIALECT_in_rule_attribute961); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute965); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 13 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:192:4: ^(attrName= VK_CALENDARS value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_CALENDARS,FOLLOW_VK_CALENDARS_in_rule_attribute974); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute978); 

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
            match(input,VT_AND_IMPLICIT,FOLLOW_VT_AND_IMPLICIT_in_lhs_block1003); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // src/main/resources/org/drools/lang/DescrBuilderTree.g:199:23: (dt= lhs )*
                loop30:
                do {
                    int alt30=2;
                    int LA30_0 = input.LA(1);

                    if ( ((LA30_0>=VT_AND_PREFIX && LA30_0<=VT_OR_INFIX)||LA30_0==VT_PATTERN||LA30_0==VK_EVAL||LA30_0==VK_NOT||(LA30_0>=VK_EXISTS && LA30_0<=VK_FORALL)||LA30_0==FROM) ) {
                        alt30=1;
                    }


                    switch (alt30) {
                	case 1 :
                	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:199:24: dt= lhs
                	    {
                	    pushFollow(FOLLOW_lhs_in_lhs_block1008);
                	    dt=lhs();

                	    state._fsp--;

                	    andDescr.addDescr((dt!=null?dt.baseDescr:null));

                	    }
                	    break;

                	default :
                	    break loop30;
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:202:1: lhs returns [BaseDescr baseDescr] : ( ^(start= VT_OR_PREFIX (dt= lhs )+ ) | ^(start= VT_OR_INFIX dt1= lhs dt2= lhs ) | ^(start= VT_AND_PREFIX (dt= lhs )+ ) | ^(start= VT_AND_INFIX dt1= lhs dt2= lhs ) | ^(start= VK_EXISTS dt= lhs ) | ^(start= VK_NOT dt= lhs ) | ^(start= VK_EVAL pc= VT_PAREN_CHUNK ) | ^(start= VK_FORALL (dt= lhs )+ ) | ^( FROM pn= lhs_pattern fe= from_elements ) | pn= lhs_pattern );
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
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:205:3: ( ^(start= VT_OR_PREFIX (dt= lhs )+ ) | ^(start= VT_OR_INFIX dt1= lhs dt2= lhs ) | ^(start= VT_AND_PREFIX (dt= lhs )+ ) | ^(start= VT_AND_INFIX dt1= lhs dt2= lhs ) | ^(start= VK_EXISTS dt= lhs ) | ^(start= VK_NOT dt= lhs ) | ^(start= VK_EVAL pc= VT_PAREN_CHUNK ) | ^(start= VK_FORALL (dt= lhs )+ ) | ^( FROM pn= lhs_pattern fe= from_elements ) | pn= lhs_pattern )
            int alt34=10;
            switch ( input.LA(1) ) {
            case VT_OR_PREFIX:
                {
                alt34=1;
                }
                break;
            case VT_OR_INFIX:
                {
                alt34=2;
                }
                break;
            case VT_AND_PREFIX:
                {
                alt34=3;
                }
                break;
            case VT_AND_INFIX:
                {
                alt34=4;
                }
                break;
            case VK_EXISTS:
                {
                alt34=5;
                }
                break;
            case VK_NOT:
                {
                alt34=6;
                }
                break;
            case VK_EVAL:
                {
                alt34=7;
                }
                break;
            case VK_FORALL:
                {
                alt34=8;
                }
                break;
            case FROM:
                {
                alt34=9;
                }
                break;
            case VT_PATTERN:
                {
                alt34=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;
            }

            switch (alt34) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:205:5: ^(start= VT_OR_PREFIX (dt= lhs )+ )
                    {
                    start=(DroolsTree)match(input,VT_OR_PREFIX,FOLLOW_VT_OR_PREFIX_in_lhs1034); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:205:26: (dt= lhs )+
                    int cnt31=0;
                    loop31:
                    do {
                        int alt31=2;
                        int LA31_0 = input.LA(1);

                        if ( ((LA31_0>=VT_AND_PREFIX && LA31_0<=VT_OR_INFIX)||LA31_0==VT_PATTERN||LA31_0==VK_EVAL||LA31_0==VK_NOT||(LA31_0>=VK_EXISTS && LA31_0<=VK_FORALL)||LA31_0==FROM) ) {
                            alt31=1;
                        }


                        switch (alt31) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:205:27: dt= lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs1039);
                    	    dt=lhs();

                    	    state._fsp--;

                    	    	lhsList.add((dt!=null?dt.baseDescr:null));	

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt31 >= 1 ) break loop31;
                                EarlyExitException eee =
                                    new EarlyExitException(31, input);
                                throw eee;
                        }
                        cnt31++;
                    } while (true);


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createOr(start, lhsList);	

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:207:4: ^(start= VT_OR_INFIX dt1= lhs dt2= lhs )
                    {
                    start=(DroolsTree)match(input,VT_OR_INFIX,FOLLOW_VT_OR_INFIX_in_lhs1055); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs1059);
                    dt1=lhs();

                    state._fsp--;

                    pushFollow(FOLLOW_lhs_in_lhs1063);
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
                    start=(DroolsTree)match(input,VT_AND_PREFIX,FOLLOW_VT_AND_PREFIX_in_lhs1075); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:211:26: (dt= lhs )+
                    int cnt32=0;
                    loop32:
                    do {
                        int alt32=2;
                        int LA32_0 = input.LA(1);

                        if ( ((LA32_0>=VT_AND_PREFIX && LA32_0<=VT_OR_INFIX)||LA32_0==VT_PATTERN||LA32_0==VK_EVAL||LA32_0==VK_NOT||(LA32_0>=VK_EXISTS && LA32_0<=VK_FORALL)||LA32_0==FROM) ) {
                            alt32=1;
                        }


                        switch (alt32) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:211:27: dt= lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs1080);
                    	    dt=lhs();

                    	    state._fsp--;

                    	    	lhsList.add((dt!=null?dt.baseDescr:null));	

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt32 >= 1 ) break loop32;
                                EarlyExitException eee =
                                    new EarlyExitException(32, input);
                                throw eee;
                        }
                        cnt32++;
                    } while (true);


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createAnd(start, lhsList);	

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:213:4: ^(start= VT_AND_INFIX dt1= lhs dt2= lhs )
                    {
                    start=(DroolsTree)match(input,VT_AND_INFIX,FOLLOW_VT_AND_INFIX_in_lhs1096); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs1100);
                    dt1=lhs();

                    state._fsp--;

                    pushFollow(FOLLOW_lhs_in_lhs1104);
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
                    start=(DroolsTree)match(input,VK_EXISTS,FOLLOW_VK_EXISTS_in_lhs1116); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs1120);
                    dt=lhs();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createExists(start, (dt!=null?dt.baseDescr:null));	

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:219:4: ^(start= VK_NOT dt= lhs )
                    {
                    start=(DroolsTree)match(input,VK_NOT,FOLLOW_VK_NOT_in_lhs1132); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs1136);
                    dt=lhs();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createNot(start, (dt!=null?dt.baseDescr:null));	

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:221:4: ^(start= VK_EVAL pc= VT_PAREN_CHUNK )
                    {
                    start=(DroolsTree)match(input,VK_EVAL,FOLLOW_VK_EVAL_in_lhs1148); 

                    match(input, Token.DOWN, null); 
                    pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_lhs1152); 

                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createEval(start, pc);	

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:223:4: ^(start= VK_FORALL (dt= lhs )+ )
                    {
                    start=(DroolsTree)match(input,VK_FORALL,FOLLOW_VK_FORALL_in_lhs1164); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:223:22: (dt= lhs )+
                    int cnt33=0;
                    loop33:
                    do {
                        int alt33=2;
                        int LA33_0 = input.LA(1);

                        if ( ((LA33_0>=VT_AND_PREFIX && LA33_0<=VT_OR_INFIX)||LA33_0==VT_PATTERN||LA33_0==VK_EVAL||LA33_0==VK_NOT||(LA33_0>=VK_EXISTS && LA33_0<=VK_FORALL)||LA33_0==FROM) ) {
                            alt33=1;
                        }


                        switch (alt33) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:223:23: dt= lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs1169);
                    	    dt=lhs();

                    	    state._fsp--;

                    	    	lhsList.add((dt!=null?dt.baseDescr:null));	

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
                    	retval.baseDescr = factory.createForAll(start, lhsList);	

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:225:4: ^( FROM pn= lhs_pattern fe= from_elements )
                    {
                    match(input,FROM,FOLLOW_FROM_in_lhs1183); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_pattern_in_lhs1187);
                    pn=lhs_pattern();

                    state._fsp--;

                    pushFollow(FOLLOW_from_elements_in_lhs1191);
                    fe=from_elements();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.setupFrom(pn, (fe!=null?fe.patternSourceDescr:null));	

                    }
                    break;
                case 10 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:227:4: pn= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs1202);
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:231:1: from_elements returns [PatternSourceDescr patternSourceDescr] : ( ^(start= ACCUMULATE dt= lhs ret= accumulate_parts[$patternSourceDescr] ) | ^(start= COLLECT dt= lhs ) | ^(start= VK_ENTRY_POINT entryId= VT_ENTRYPOINT_ID ) | fs= from_source_clause );
    public final DescrBuilderTree.from_elements_return from_elements() throws RecognitionException {
        DescrBuilderTree.from_elements_return retval = new DescrBuilderTree.from_elements_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree entryId=null;
        DescrBuilderTree.lhs_return dt = null;

        AccumulateDescr ret = null;

        DescrBuilderTree.from_source_clause_return fs = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:232:2: ( ^(start= ACCUMULATE dt= lhs ret= accumulate_parts[$patternSourceDescr] ) | ^(start= COLLECT dt= lhs ) | ^(start= VK_ENTRY_POINT entryId= VT_ENTRYPOINT_ID ) | fs= from_source_clause )
            int alt35=4;
            switch ( input.LA(1) ) {
            case ACCUMULATE:
                {
                alt35=1;
                }
                break;
            case COLLECT:
                {
                alt35=2;
                }
                break;
            case VK_ENTRY_POINT:
                {
                alt35=3;
                }
                break;
            case VT_FROM_SOURCE:
                {
                alt35=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;
            }

            switch (alt35) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:232:4: ^(start= ACCUMULATE dt= lhs ret= accumulate_parts[$patternSourceDescr] )
                    {
                    start=(DroolsTree)match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_from_elements1223); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_from_elements1227);
                    dt=lhs();

                    state._fsp--;

                    	retval.patternSourceDescr = factory.createAccumulate(start, (dt!=null?dt.baseDescr:null));	
                    pushFollow(FOLLOW_accumulate_parts_in_from_elements1237);
                    ret=accumulate_parts(retval.patternSourceDescr);

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.patternSourceDescr = ret;	

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:236:4: ^(start= COLLECT dt= lhs )
                    {
                    start=(DroolsTree)match(input,COLLECT,FOLLOW_COLLECT_in_from_elements1250); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_from_elements1254);
                    dt=lhs();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.patternSourceDescr = factory.createCollect(start, (dt!=null?dt.baseDescr:null));	

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:238:4: ^(start= VK_ENTRY_POINT entryId= VT_ENTRYPOINT_ID )
                    {
                    start=(DroolsTree)match(input,VK_ENTRY_POINT,FOLLOW_VK_ENTRY_POINT_in_from_elements1266); 

                    match(input, Token.DOWN, null); 
                    entryId=(DroolsTree)match(input,VT_ENTRYPOINT_ID,FOLLOW_VT_ENTRYPOINT_ID_in_from_elements1270); 

                    match(input, Token.UP, null); 
                    	retval.patternSourceDescr = factory.createEntryPoint(start, entryId);	

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:240:4: fs= from_source_clause
                    {
                    pushFollow(FOLLOW_from_source_clause_in_from_elements1281);
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:244:1: accumulate_parts[PatternSourceDescr patternSourceDescr] returns [AccumulateDescr accumulateDescr] : (ac1= accumulate_init_clause[$patternSourceDescr] | ac2= accumulate_id_clause[$patternSourceDescr] );
    public final AccumulateDescr accumulate_parts(PatternSourceDescr patternSourceDescr) throws RecognitionException {
        AccumulateDescr accumulateDescr = null;

        DescrBuilderTree.accumulate_init_clause_return ac1 = null;

        AccumulateDescr ac2 = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:245:2: (ac1= accumulate_init_clause[$patternSourceDescr] | ac2= accumulate_id_clause[$patternSourceDescr] )
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==VT_ACCUMULATE_INIT_CLAUSE) ) {
                alt36=1;
            }
            else if ( (LA36_0==VT_ACCUMULATE_ID_CLAUSE) ) {
                alt36=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 36, 0, input);

                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:245:4: ac1= accumulate_init_clause[$patternSourceDescr]
                    {
                    pushFollow(FOLLOW_accumulate_init_clause_in_accumulate_parts1302);
                    ac1=accumulate_init_clause(patternSourceDescr);

                    state._fsp--;

                    	accumulateDescr = (ac1!=null?ac1.accumulateDescr:null);	

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:247:4: ac2= accumulate_id_clause[$patternSourceDescr]
                    {
                    pushFollow(FOLLOW_accumulate_id_clause_in_accumulate_parts1313);
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:251:1: accumulate_init_clause[PatternSourceDescr accumulateParam] returns [AccumulateDescr accumulateDescr] : ^( VT_ACCUMULATE_INIT_CLAUSE ^(start= VK_INIT pc1= VT_PAREN_CHUNK ) ^( VK_ACTION pc2= VT_PAREN_CHUNK ) (rev= accumulate_init_reverse_clause )? ^( VK_RESULT pc3= VT_PAREN_CHUNK ) ) ;
    public final DescrBuilderTree.accumulate_init_clause_return accumulate_init_clause(PatternSourceDescr accumulateParam) throws RecognitionException {
        DescrBuilderTree.accumulate_init_clause_return retval = new DescrBuilderTree.accumulate_init_clause_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree pc1=null;
        DroolsTree pc2=null;
        DroolsTree pc3=null;
        DescrBuilderTree.accumulate_init_reverse_clause_return rev = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:252:2: ( ^( VT_ACCUMULATE_INIT_CLAUSE ^(start= VK_INIT pc1= VT_PAREN_CHUNK ) ^( VK_ACTION pc2= VT_PAREN_CHUNK ) (rev= accumulate_init_reverse_clause )? ^( VK_RESULT pc3= VT_PAREN_CHUNK ) ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:252:4: ^( VT_ACCUMULATE_INIT_CLAUSE ^(start= VK_INIT pc1= VT_PAREN_CHUNK ) ^( VK_ACTION pc2= VT_PAREN_CHUNK ) (rev= accumulate_init_reverse_clause )? ^( VK_RESULT pc3= VT_PAREN_CHUNK ) )
            {
            match(input,VT_ACCUMULATE_INIT_CLAUSE,FOLLOW_VT_ACCUMULATE_INIT_CLAUSE_in_accumulate_init_clause1336); 

            match(input, Token.DOWN, null); 
            start=(DroolsTree)match(input,VK_INIT,FOLLOW_VK_INIT_in_accumulate_init_clause1345); 

            match(input, Token.DOWN, null); 
            pc1=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1349); 

            match(input, Token.UP, null); 
            match(input,VK_ACTION,FOLLOW_VK_ACTION_in_accumulate_init_clause1357); 

            match(input, Token.DOWN, null); 
            pc2=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1361); 

            match(input, Token.UP, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:255:7: (rev= accumulate_init_reverse_clause )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==VK_REVERSE) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:255:7: rev= accumulate_init_reverse_clause
                    {
                    pushFollow(FOLLOW_accumulate_init_reverse_clause_in_accumulate_init_clause1370);
                    rev=accumulate_init_reverse_clause();

                    state._fsp--;


                    }
                    break;

            }

            match(input,VK_RESULT,FOLLOW_VK_RESULT_in_accumulate_init_clause1377); 

            match(input, Token.DOWN, null); 
            pc3=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1381); 

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:264:1: accumulate_init_reverse_clause returns [DroolsTree vkReverse, DroolsTree vkReverseChunk] : ^(vk= VK_REVERSE pc= VT_PAREN_CHUNK ) ;
    public final DescrBuilderTree.accumulate_init_reverse_clause_return accumulate_init_reverse_clause() throws RecognitionException {
        DescrBuilderTree.accumulate_init_reverse_clause_return retval = new DescrBuilderTree.accumulate_init_reverse_clause_return();
        retval.start = input.LT(1);

        DroolsTree vk=null;
        DroolsTree pc=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:265:2: ( ^(vk= VK_REVERSE pc= VT_PAREN_CHUNK ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:265:4: ^(vk= VK_REVERSE pc= VT_PAREN_CHUNK )
            {
            vk=(DroolsTree)match(input,VK_REVERSE,FOLLOW_VK_REVERSE_in_accumulate_init_reverse_clause1404); 

            match(input, Token.DOWN, null); 
            pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_reverse_clause1408); 

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:270:1: accumulate_id_clause[PatternSourceDescr accumulateParam] returns [AccumulateDescr accumulateDescr] : ^( VT_ACCUMULATE_ID_CLAUSE id= ID pc= VT_PAREN_CHUNK ) ;
    public final AccumulateDescr accumulate_id_clause(PatternSourceDescr accumulateParam) throws RecognitionException {
        AccumulateDescr accumulateDescr = null;

        DroolsTree id=null;
        DroolsTree pc=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:271:2: ( ^( VT_ACCUMULATE_ID_CLAUSE id= ID pc= VT_PAREN_CHUNK ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:271:4: ^( VT_ACCUMULATE_ID_CLAUSE id= ID pc= VT_PAREN_CHUNK )
            {
            match(input,VT_ACCUMULATE_ID_CLAUSE,FOLLOW_VT_ACCUMULATE_ID_CLAUSE_in_accumulate_id_clause1430); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_accumulate_id_clause1434); 
            pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_id_clause1438); 

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:275:1: from_source_clause returns [FromDescr fromDescr, AccessorDescr retAccessorDescr] : ^(fs= VT_FROM_SOURCE ) ;
    public final DescrBuilderTree.from_source_clause_return from_source_clause() throws RecognitionException {
        from_source_clause_stack.push(new from_source_clause_scope());
        DescrBuilderTree.from_source_clause_return retval = new DescrBuilderTree.from_source_clause_return();
        retval.start = input.LT(1);

        DroolsTree fs=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:278:3: ( ^(fs= VT_FROM_SOURCE ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:278:5: ^(fs= VT_FROM_SOURCE )
            {
            fs=(DroolsTree)match(input,VT_FROM_SOURCE,FOLLOW_VT_FROM_SOURCE_in_from_source_clause1462); 

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:298:1: expression_chain : ^(start= VT_EXPRESSION_CHAIN id= ID (sc= VT_SQUARE_CHUNK )? (pc= VT_PAREN_CHUNK )? ( expression_chain )? ) ;
    public final DescrBuilderTree.expression_chain_return expression_chain() throws RecognitionException {
        DescrBuilderTree.expression_chain_return retval = new DescrBuilderTree.expression_chain_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree id=null;
        DroolsTree sc=null;
        DroolsTree pc=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:299:2: ( ^(start= VT_EXPRESSION_CHAIN id= ID (sc= VT_SQUARE_CHUNK )? (pc= VT_PAREN_CHUNK )? ( expression_chain )? ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:299:4: ^(start= VT_EXPRESSION_CHAIN id= ID (sc= VT_SQUARE_CHUNK )? (pc= VT_PAREN_CHUNK )? ( expression_chain )? )
            {
            start=(DroolsTree)match(input,VT_EXPRESSION_CHAIN,FOLLOW_VT_EXPRESSION_CHAIN_in_expression_chain1492); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_expression_chain1496); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:299:40: (sc= VT_SQUARE_CHUNK )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==VT_SQUARE_CHUNK) ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:299:40: sc= VT_SQUARE_CHUNK
                    {
                    sc=(DroolsTree)match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_expression_chain1500); 

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:299:60: (pc= VT_PAREN_CHUNK )?
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==VT_PAREN_CHUNK) ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:299:60: pc= VT_PAREN_CHUNK
                    {
                    pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_expression_chain1505); 

                    }
                    break;

            }

            	DeclarativeInvokerDescr declarativeInvokerResult = factory.createExpressionChain(start, id, sc, pc);	
            		((from_source_clause_scope)from_source_clause_stack.peek()).accessorDescr.addInvoker(declarativeInvokerResult);	
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:302:3: ( expression_chain )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==VT_EXPRESSION_CHAIN) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:302:3: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain1513);
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:305:1: lhs_pattern returns [BaseDescr baseDescr] : ^( VT_PATTERN fe= fact_expression ) (oc= over_clause )? ;
    public final BaseDescr lhs_pattern() throws RecognitionException {
        BaseDescr baseDescr = null;

        DescrBuilderTree.fact_expression_return fe = null;

        List oc = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:306:2: ( ^( VT_PATTERN fe= fact_expression ) (oc= over_clause )? )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:306:4: ^( VT_PATTERN fe= fact_expression ) (oc= over_clause )?
            {
            match(input,VT_PATTERN,FOLLOW_VT_PATTERN_in_lhs_pattern1531); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_fact_expression_in_lhs_pattern1535);
            fe=fact_expression();

            state._fsp--;


            match(input, Token.UP, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:306:39: (oc= over_clause )?
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==OVER) ) {
                alt41=1;
            }
            switch (alt41) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:306:39: oc= over_clause
                    {
                    pushFollow(FOLLOW_over_clause_in_lhs_pattern1540);
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:310:1: over_clause returns [List behaviorList] : ^( OVER (oe= over_element )+ ) ;
    public final List over_clause() throws RecognitionException {
        List behaviorList = null;

        BehaviorDescr oe = null;


        behaviorList = new LinkedList();
        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:312:2: ( ^( OVER (oe= over_element )+ ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:312:4: ^( OVER (oe= over_element )+ )
            {
            match(input,OVER,FOLLOW_OVER_in_over_clause1565); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:312:11: (oe= over_element )+
            int cnt42=0;
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);

                if ( (LA42_0==VT_BEHAVIOR) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:312:12: oe= over_element
            	    {
            	    pushFollow(FOLLOW_over_element_in_over_clause1570);
            	    oe=over_element();

            	    state._fsp--;

            	    behaviorList.add(oe);

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:315:1: over_element returns [BehaviorDescr behavior] : ^( VT_BEHAVIOR ID id2= ID pc= VT_PAREN_CHUNK ) ;
    public final BehaviorDescr over_element() throws RecognitionException {
        BehaviorDescr behavior = null;

        DroolsTree id2=null;
        DroolsTree pc=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:316:2: ( ^( VT_BEHAVIOR ID id2= ID pc= VT_PAREN_CHUNK ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:316:4: ^( VT_BEHAVIOR ID id2= ID pc= VT_PAREN_CHUNK )
            {
            match(input,VT_BEHAVIOR,FOLLOW_VT_BEHAVIOR_in_over_element1591); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_over_element1593); 
            id2=(DroolsTree)match(input,ID,FOLLOW_ID_in_over_element1597); 
            pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_over_element1601); 

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:320:1: fact_expression returns [BaseDescr descr] : ( ^( VT_FACT pt= pattern_type (fe= fact_expression )* ) | ^( VT_FACT_BINDING label= VT_LABEL fact= fact_expression ) | ^(start= VT_FACT_OR left= fact_expression right= fact_expression ) | ^( VT_FIELD field= field_element (fe= fact_expression )? ) | ^( VT_BIND_FIELD label= VT_LABEL fe= fact_expression ) | ^( VK_EVAL pc= VT_PAREN_CHUNK ) | ^(op= EQUALS fe= fact_expression ) | ^(op= NOT_EQUALS fe= fact_expression ) | ^(op= GREATER fe= fact_expression ) | ^(op= GREATER_EQUALS fe= fact_expression ) | ^(op= LESS fe= fact_expression ) | ^(op= LESS_EQUALS fe= fact_expression ) | ^(op= VK_OPERATOR (not= VK_NOT )? (param= VT_SQUARE_CHUNK )? fe= fact_expression ) | ^( VK_IN (not= VK_NOT )? (fe= fact_expression )+ ) | ^( DOUBLE_PIPE left= fact_expression right= fact_expression ) | ^( DOUBLE_AMPER left= fact_expression right= fact_expression ) | ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) | s= STRING | ( PLUS | m= MINUS )? ( (i= DECIMAL | i= SIGNED_DECIMAL ) | (h= HEX | h= SIGNED_HEX ) | (f= FLOAT | f= SIGNED_FLOAT ) ) | b= BOOL | n= NULL | pc= VT_PAREN_CHUNK );
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
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:323:3: ( ^( VT_FACT pt= pattern_type (fe= fact_expression )* ) | ^( VT_FACT_BINDING label= VT_LABEL fact= fact_expression ) | ^(start= VT_FACT_OR left= fact_expression right= fact_expression ) | ^( VT_FIELD field= field_element (fe= fact_expression )? ) | ^( VT_BIND_FIELD label= VT_LABEL fe= fact_expression ) | ^( VK_EVAL pc= VT_PAREN_CHUNK ) | ^(op= EQUALS fe= fact_expression ) | ^(op= NOT_EQUALS fe= fact_expression ) | ^(op= GREATER fe= fact_expression ) | ^(op= GREATER_EQUALS fe= fact_expression ) | ^(op= LESS fe= fact_expression ) | ^(op= LESS_EQUALS fe= fact_expression ) | ^(op= VK_OPERATOR (not= VK_NOT )? (param= VT_SQUARE_CHUNK )? fe= fact_expression ) | ^( VK_IN (not= VK_NOT )? (fe= fact_expression )+ ) | ^( DOUBLE_PIPE left= fact_expression right= fact_expression ) | ^( DOUBLE_AMPER left= fact_expression right= fact_expression ) | ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) | s= STRING | ( PLUS | m= MINUS )? ( (i= DECIMAL | i= SIGNED_DECIMAL ) | (h= HEX | h= SIGNED_HEX ) | (f= FLOAT | f= SIGNED_FLOAT ) ) | b= BOOL | n= NULL | pc= VT_PAREN_CHUNK )
            int alt55=22;
            switch ( input.LA(1) ) {
            case VT_FACT:
                {
                alt55=1;
                }
                break;
            case VT_FACT_BINDING:
                {
                alt55=2;
                }
                break;
            case VT_FACT_OR:
                {
                alt55=3;
                }
                break;
            case VT_FIELD:
                {
                alt55=4;
                }
                break;
            case VT_BIND_FIELD:
                {
                alt55=5;
                }
                break;
            case VK_EVAL:
                {
                alt55=6;
                }
                break;
            case EQUALS:
                {
                alt55=7;
                }
                break;
            case NOT_EQUALS:
                {
                alt55=8;
                }
                break;
            case GREATER:
                {
                alt55=9;
                }
                break;
            case GREATER_EQUALS:
                {
                alt55=10;
                }
                break;
            case LESS:
                {
                alt55=11;
                }
                break;
            case LESS_EQUALS:
                {
                alt55=12;
                }
                break;
            case VK_OPERATOR:
                {
                alt55=13;
                }
                break;
            case VK_IN:
                {
                alt55=14;
                }
                break;
            case DOUBLE_PIPE:
                {
                alt55=15;
                }
                break;
            case DOUBLE_AMPER:
                {
                alt55=16;
                }
                break;
            case VT_ACCESSOR_PATH:
                {
                alt55=17;
                }
                break;
            case STRING:
                {
                alt55=18;
                }
                break;
            case SIGNED_DECIMAL:
            case SIGNED_HEX:
            case SIGNED_FLOAT:
            case PLUS:
            case MINUS:
            case DECIMAL:
            case HEX:
            case FLOAT:
                {
                alt55=19;
                }
                break;
            case BOOL:
                {
                alt55=20;
                }
                break;
            case NULL:
                {
                alt55=21;
                }
                break;
            case VT_PAREN_CHUNK:
                {
                alt55=22;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }

            switch (alt55) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:323:5: ^( VT_FACT pt= pattern_type (fe= fact_expression )* )
                    {
                    match(input,VT_FACT,FOLLOW_VT_FACT_in_fact_expression1624); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_pattern_type_in_fact_expression1628);
                    pt=pattern_type();

                    state._fsp--;

                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:323:31: (fe= fact_expression )*
                    loop43:
                    do {
                        int alt43=2;
                        int LA43_0 = input.LA(1);

                        if ( (LA43_0==VT_FACT||LA43_0==VT_PAREN_CHUNK||(LA43_0>=VT_FACT_BINDING && LA43_0<=VT_ACCESSOR_PATH)||LA43_0==VK_EVAL||LA43_0==VK_IN||LA43_0==VK_OPERATOR||(LA43_0>=SIGNED_DECIMAL && LA43_0<=SIGNED_FLOAT)||LA43_0==STRING||LA43_0==BOOL||(LA43_0>=DOUBLE_PIPE && LA43_0<=DOUBLE_AMPER)||(LA43_0>=EQUALS && LA43_0<=FLOAT)) ) {
                            alt43=1;
                        }


                        switch (alt43) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:323:32: fe= fact_expression
                    	    {
                    	    pushFollow(FOLLOW_fact_expression_in_fact_expression1633);
                    	    fe=fact_expression();

                    	    state._fsp--;

                    	    exprList.add((fe!=null?fe.descr:null));

                    	    }
                    	    break;

                    	default :
                    	    break loop43;
                        }
                    } while (true);


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createPattern(pt, exprList);	

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:325:4: ^( VT_FACT_BINDING label= VT_LABEL fact= fact_expression )
                    {
                    match(input,VT_FACT_BINDING,FOLLOW_VT_FACT_BINDING_in_fact_expression1647); 

                    match(input, Token.DOWN, null); 
                    label=(DroolsTree)match(input,VT_LABEL,FOLLOW_VT_LABEL_in_fact_expression1651); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1655);
                    fact=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupPatternBiding(label, (fact!=null?fact.descr:null));	

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:327:4: ^(start= VT_FACT_OR left= fact_expression right= fact_expression )
                    {
                    start=(DroolsTree)match(input,VT_FACT_OR,FOLLOW_VT_FACT_OR_in_fact_expression1667); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1671);
                    left=fact_expression();

                    state._fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1675);
                    right=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createFactOr(start, (left!=null?left.descr:null), (right!=null?right.descr:null));	

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:330:4: ^( VT_FIELD field= field_element (fe= fact_expression )? )
                    {
                    match(input,VT_FIELD,FOLLOW_VT_FIELD_in_fact_expression1686); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_field_element_in_fact_expression1690);
                    field=field_element();

                    state._fsp--;

                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:330:37: (fe= fact_expression )?
                    int alt44=2;
                    int LA44_0 = input.LA(1);

                    if ( (LA44_0==VT_FACT||LA44_0==VT_PAREN_CHUNK||(LA44_0>=VT_FACT_BINDING && LA44_0<=VT_ACCESSOR_PATH)||LA44_0==VK_EVAL||LA44_0==VK_IN||LA44_0==VK_OPERATOR||(LA44_0>=SIGNED_DECIMAL && LA44_0<=SIGNED_FLOAT)||LA44_0==STRING||LA44_0==BOOL||(LA44_0>=DOUBLE_PIPE && LA44_0<=DOUBLE_AMPER)||(LA44_0>=EQUALS && LA44_0<=FLOAT)) ) {
                        alt44=1;
                    }
                    switch (alt44) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:330:37: fe= fact_expression
                            {
                            pushFollow(FOLLOW_fact_expression_in_fact_expression1694);
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
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:336:4: ^( VT_BIND_FIELD label= VT_LABEL fe= fact_expression )
                    {
                    match(input,VT_BIND_FIELD,FOLLOW_VT_BIND_FIELD_in_fact_expression1705); 

                    match(input, Token.DOWN, null); 
                    label=(DroolsTree)match(input,VT_LABEL,FOLLOW_VT_LABEL_in_fact_expression1709); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1713);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createFieldBinding(label, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:339:4: ^( VK_EVAL pc= VT_PAREN_CHUNK )
                    {
                    match(input,VK_EVAL,FOLLOW_VK_EVAL_in_fact_expression1724); 

                    match(input, Token.DOWN, null); 
                    pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_fact_expression1728); 

                    match(input, Token.UP, null); 
                    	retval.descr = factory.createPredicate(pc);	

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:342:4: ^(op= EQUALS fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,EQUALS,FOLLOW_EQUALS_in_fact_expression1741); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1745);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:344:4: ^(op= NOT_EQUALS fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_fact_expression1757); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1761);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:346:4: ^(op= GREATER fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,GREATER,FOLLOW_GREATER_in_fact_expression1773); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1777);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 10 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:348:4: ^(op= GREATER_EQUALS fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,GREATER_EQUALS,FOLLOW_GREATER_EQUALS_in_fact_expression1789); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1793);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 11 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:350:4: ^(op= LESS fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,LESS,FOLLOW_LESS_in_fact_expression1805); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1809);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 12 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:352:4: ^(op= LESS_EQUALS fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,LESS_EQUALS,FOLLOW_LESS_EQUALS_in_fact_expression1821); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1825);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 13 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:354:4: ^(op= VK_OPERATOR (not= VK_NOT )? (param= VT_SQUARE_CHUNK )? fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,VK_OPERATOR,FOLLOW_VK_OPERATOR_in_fact_expression1837); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:354:24: (not= VK_NOT )?
                    int alt45=2;
                    int LA45_0 = input.LA(1);

                    if ( (LA45_0==VK_NOT) ) {
                        alt45=1;
                    }
                    switch (alt45) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:354:24: not= VK_NOT
                            {
                            not=(DroolsTree)match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1841); 

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:354:38: (param= VT_SQUARE_CHUNK )?
                    int alt46=2;
                    int LA46_0 = input.LA(1);

                    if ( (LA46_0==VT_SQUARE_CHUNK) ) {
                        alt46=1;
                    }
                    switch (alt46) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:354:38: param= VT_SQUARE_CHUNK
                            {
                            param=(DroolsTree)match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_fact_expression1846); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1851);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, not, (fe!=null?fe.descr:null), param);	

                    }
                    break;
                case 14 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:357:4: ^( VK_IN (not= VK_NOT )? (fe= fact_expression )+ )
                    {
                    match(input,VK_IN,FOLLOW_VK_IN_in_fact_expression1862); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:357:15: (not= VK_NOT )?
                    int alt47=2;
                    int LA47_0 = input.LA(1);

                    if ( (LA47_0==VK_NOT) ) {
                        alt47=1;
                    }
                    switch (alt47) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:357:15: not= VK_NOT
                            {
                            not=(DroolsTree)match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1866); 

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:357:24: (fe= fact_expression )+
                    int cnt48=0;
                    loop48:
                    do {
                        int alt48=2;
                        int LA48_0 = input.LA(1);

                        if ( (LA48_0==VT_FACT||LA48_0==VT_PAREN_CHUNK||(LA48_0>=VT_FACT_BINDING && LA48_0<=VT_ACCESSOR_PATH)||LA48_0==VK_EVAL||LA48_0==VK_IN||LA48_0==VK_OPERATOR||(LA48_0>=SIGNED_DECIMAL && LA48_0<=SIGNED_FLOAT)||LA48_0==STRING||LA48_0==BOOL||(LA48_0>=DOUBLE_PIPE && LA48_0<=DOUBLE_AMPER)||(LA48_0>=EQUALS && LA48_0<=FLOAT)) ) {
                            alt48=1;
                        }


                        switch (alt48) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:357:25: fe= fact_expression
                    	    {
                    	    pushFollow(FOLLOW_fact_expression_in_fact_expression1872);
                    	    fe=fact_expression();

                    	    state._fsp--;

                    	    exprList.add((fe!=null?fe.descr:null));

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
                    	retval.descr = factory.createRestrictionConnective(not, exprList);	

                    }
                    break;
                case 15 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:360:4: ^( DOUBLE_PIPE left= fact_expression right= fact_expression )
                    {
                    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_fact_expression1887); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1891);
                    left=fact_expression();

                    state._fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1895);
                    right=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createOrRestrictionConnective((left!=null?left.descr:null), (right!=null?right.descr:null));	

                    }
                    break;
                case 16 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:362:4: ^( DOUBLE_AMPER left= fact_expression right= fact_expression )
                    {
                    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_fact_expression1905); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1909);
                    left=fact_expression();

                    state._fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1913);
                    right=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createAndRestrictionConnective((left!=null?left.descr:null), (right!=null?right.descr:null));	

                    }
                    break;
                case 17 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:365:4: ^( VT_ACCESSOR_PATH (ae= accessor_element )+ )
                    {
                    match(input,VT_ACCESSOR_PATH,FOLLOW_VT_ACCESSOR_PATH_in_fact_expression1924); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:365:23: (ae= accessor_element )+
                    int cnt49=0;
                    loop49:
                    do {
                        int alt49=2;
                        int LA49_0 = input.LA(1);

                        if ( (LA49_0==VT_ACCESSOR_ELEMENT) ) {
                            alt49=1;
                        }


                        switch (alt49) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:365:24: ae= accessor_element
                    	    {
                    	    pushFollow(FOLLOW_accessor_element_in_fact_expression1929);
                    	    ae=accessor_element();

                    	    state._fsp--;

                    	    exprList.add(ae);

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
                    	retval.descr = factory.createAccessorPath(exprList);	

                    }
                    break;
                case 18 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:367:4: s= STRING
                    {
                    s=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_fact_expression1944); 
                    	retval.descr = factory.createStringLiteralRestriction(s);	

                    }
                    break;
                case 19 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:369:4: ( PLUS | m= MINUS )? ( (i= DECIMAL | i= SIGNED_DECIMAL ) | (h= HEX | h= SIGNED_HEX ) | (f= FLOAT | f= SIGNED_FLOAT ) )
                    {
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:369:4: ( PLUS | m= MINUS )?
                    int alt50=3;
                    int LA50_0 = input.LA(1);

                    if ( (LA50_0==PLUS) ) {
                        alt50=1;
                    }
                    else if ( (LA50_0==MINUS) ) {
                        alt50=2;
                    }
                    switch (alt50) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:369:5: PLUS
                            {
                            match(input,PLUS,FOLLOW_PLUS_in_fact_expression1953); 

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:369:10: m= MINUS
                            {
                            m=(DroolsTree)match(input,MINUS,FOLLOW_MINUS_in_fact_expression1957); 

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:370:10: ( (i= DECIMAL | i= SIGNED_DECIMAL ) | (h= HEX | h= SIGNED_HEX ) | (f= FLOAT | f= SIGNED_FLOAT ) )
                    int alt54=3;
                    switch ( input.LA(1) ) {
                    case SIGNED_DECIMAL:
                    case DECIMAL:
                        {
                        alt54=1;
                        }
                        break;
                    case SIGNED_HEX:
                    case HEX:
                        {
                        alt54=2;
                        }
                        break;
                    case SIGNED_FLOAT:
                    case FLOAT:
                        {
                        alt54=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 54, 0, input);

                        throw nvae;
                    }

                    switch (alt54) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:370:12: (i= DECIMAL | i= SIGNED_DECIMAL )
                            {
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:370:12: (i= DECIMAL | i= SIGNED_DECIMAL )
                            int alt51=2;
                            int LA51_0 = input.LA(1);

                            if ( (LA51_0==DECIMAL) ) {
                                alt51=1;
                            }
                            else if ( (LA51_0==SIGNED_DECIMAL) ) {
                                alt51=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 51, 0, input);

                                throw nvae;
                            }
                            switch (alt51) {
                                case 1 :
                                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:370:13: i= DECIMAL
                                    {
                                    i=(DroolsTree)match(input,DECIMAL,FOLLOW_DECIMAL_in_fact_expression1976); 

                                    }
                                    break;
                                case 2 :
                                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:370:23: i= SIGNED_DECIMAL
                                    {
                                    i=(DroolsTree)match(input,SIGNED_DECIMAL,FOLLOW_SIGNED_DECIMAL_in_fact_expression1980); 

                                    }
                                    break;

                            }

                             retval.descr = factory.createIntLiteralRestriction(i, m != null); 	

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:371:5: (h= HEX | h= SIGNED_HEX )
                            {
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:371:5: (h= HEX | h= SIGNED_HEX )
                            int alt52=2;
                            int LA52_0 = input.LA(1);

                            if ( (LA52_0==HEX) ) {
                                alt52=1;
                            }
                            else if ( (LA52_0==SIGNED_HEX) ) {
                                alt52=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 52, 0, input);

                                throw nvae;
                            }
                            switch (alt52) {
                                case 1 :
                                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:371:6: h= HEX
                                    {
                                    h=(DroolsTree)match(input,HEX,FOLLOW_HEX_in_fact_expression1992); 

                                    }
                                    break;
                                case 2 :
                                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:371:12: h= SIGNED_HEX
                                    {
                                    h=(DroolsTree)match(input,SIGNED_HEX,FOLLOW_SIGNED_HEX_in_fact_expression1996); 

                                    }
                                    break;

                            }

                             retval.descr = factory.createIntLiteralRestriction(h, m != null); 	

                            }
                            break;
                        case 3 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:372:5: (f= FLOAT | f= SIGNED_FLOAT )
                            {
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:372:5: (f= FLOAT | f= SIGNED_FLOAT )
                            int alt53=2;
                            int LA53_0 = input.LA(1);

                            if ( (LA53_0==FLOAT) ) {
                                alt53=1;
                            }
                            else if ( (LA53_0==SIGNED_FLOAT) ) {
                                alt53=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 53, 0, input);

                                throw nvae;
                            }
                            switch (alt53) {
                                case 1 :
                                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:372:6: f= FLOAT
                                    {
                                    f=(DroolsTree)match(input,FLOAT,FOLLOW_FLOAT_in_fact_expression2010); 

                                    }
                                    break;
                                case 2 :
                                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:372:14: f= SIGNED_FLOAT
                                    {
                                    f=(DroolsTree)match(input,SIGNED_FLOAT,FOLLOW_SIGNED_FLOAT_in_fact_expression2014); 

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
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:374:4: b= BOOL
                    {
                    b=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_fact_expression2030); 
                    	retval.descr = factory.createBoolLiteralRestriction(b);	

                    }
                    break;
                case 21 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:376:4: n= NULL
                    {
                    n=(DroolsTree)match(input,NULL,FOLLOW_NULL_in_fact_expression2040); 
                    	retval.descr = factory.createNullLiteralRestriction(n);	

                    }
                    break;
                case 22 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:378:4: pc= VT_PAREN_CHUNK
                    {
                    pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_fact_expression2050); 
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:382:1: field_element returns [FieldConstraintDescr element] : ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) ;
    public final FieldConstraintDescr field_element() throws RecognitionException {
        FieldConstraintDescr element = null;

        BaseDescr ae = null;



        	List<BaseDescr> aeList = new LinkedList<BaseDescr>();

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:385:3: ( ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:385:5: ^( VT_ACCESSOR_PATH (ae= accessor_element )+ )
            {
            match(input,VT_ACCESSOR_PATH,FOLLOW_VT_ACCESSOR_PATH_in_field_element2072); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:385:24: (ae= accessor_element )+
            int cnt56=0;
            loop56:
            do {
                int alt56=2;
                int LA56_0 = input.LA(1);

                if ( (LA56_0==VT_ACCESSOR_ELEMENT) ) {
                    alt56=1;
                }


                switch (alt56) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:385:25: ae= accessor_element
            	    {
            	    pushFollow(FOLLOW_accessor_element_in_field_element2077);
            	    ae=accessor_element();

            	    state._fsp--;

            	    aeList.add(ae);

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:389:1: accessor_element returns [BaseDescr element] : ^( VT_ACCESSOR_ELEMENT id= ID (sc+= VT_SQUARE_CHUNK )* ) ;
    public final BaseDescr accessor_element() throws RecognitionException {
        BaseDescr element = null;

        DroolsTree id=null;
        DroolsTree sc=null;
        List list_sc=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:390:2: ( ^( VT_ACCESSOR_ELEMENT id= ID (sc+= VT_SQUARE_CHUNK )* ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:390:4: ^( VT_ACCESSOR_ELEMENT id= ID (sc+= VT_SQUARE_CHUNK )* )
            {
            match(input,VT_ACCESSOR_ELEMENT,FOLLOW_VT_ACCESSOR_ELEMENT_in_accessor_element2101); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_accessor_element2105); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:390:34: (sc+= VT_SQUARE_CHUNK )*
            loop57:
            do {
                int alt57=2;
                int LA57_0 = input.LA(1);

                if ( (LA57_0==VT_SQUARE_CHUNK) ) {
                    alt57=1;
                }


                switch (alt57) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:390:34: sc+= VT_SQUARE_CHUNK
            	    {
            	    sc=(DroolsTree)match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_accessor_element2109); 
            	    if (list_sc==null) list_sc=new ArrayList();
            	    list_sc.add(sc);


            	    }
            	    break;

            	default :
            	    break loop57;
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:394:1: pattern_type returns [BaseDescr dataType] : ^( VT_PATTERN_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) ;
    public final BaseDescr pattern_type() throws RecognitionException {
        BaseDescr dataType = null;

        DroolsTree idList=null;
        DroolsTree rightList=null;
        List list_idList=null;
        List list_rightList=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:395:2: ( ^( VT_PATTERN_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:395:4: ^( VT_PATTERN_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* )
            {
            match(input,VT_PATTERN_TYPE,FOLLOW_VT_PATTERN_TYPE_in_pattern_type2130); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:395:28: (idList+= ID )+
            int cnt58=0;
            loop58:
            do {
                int alt58=2;
                int LA58_0 = input.LA(1);

                if ( (LA58_0==ID) ) {
                    alt58=1;
                }


                switch (alt58) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:395:28: idList+= ID
            	    {
            	    idList=(DroolsTree)match(input,ID,FOLLOW_ID_in_pattern_type2134); 
            	    if (list_idList==null) list_idList=new ArrayList();
            	    list_idList.add(idList);


            	    }
            	    break;

            	default :
            	    if ( cnt58 >= 1 ) break loop58;
                        EarlyExitException eee =
                            new EarlyExitException(58, input);
                        throw eee;
                }
                cnt58++;
            } while (true);

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:395:34: ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( (LA59_0==LEFT_SQUARE) ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:395:35: LEFT_SQUARE rightList+= RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_pattern_type2138); 
            	    rightList=(DroolsTree)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_pattern_type2142); 
            	    if (list_rightList==null) list_rightList=new ArrayList();
            	    list_rightList.add(rightList);


            	    }
            	    break;

            	default :
            	    break loop59;
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:399:1: data_type returns [BaseDescr dataType] : ^( VT_DATA_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) ;
    public final BaseDescr data_type() throws RecognitionException {
        BaseDescr dataType = null;

        DroolsTree idList=null;
        DroolsTree rightList=null;
        List list_idList=null;
        List list_rightList=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:400:2: ( ^( VT_DATA_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:400:4: ^( VT_DATA_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* )
            {
            match(input,VT_DATA_TYPE,FOLLOW_VT_DATA_TYPE_in_data_type2164); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:400:25: (idList+= ID )+
            int cnt60=0;
            loop60:
            do {
                int alt60=2;
                int LA60_0 = input.LA(1);

                if ( (LA60_0==ID) ) {
                    alt60=1;
                }


                switch (alt60) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:400:25: idList+= ID
            	    {
            	    idList=(DroolsTree)match(input,ID,FOLLOW_ID_in_data_type2168); 
            	    if (list_idList==null) list_idList=new ArrayList();
            	    list_idList.add(idList);


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

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:400:31: ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            loop61:
            do {
                int alt61=2;
                int LA61_0 = input.LA(1);

                if ( (LA61_0==LEFT_SQUARE) ) {
                    alt61=1;
                }


                switch (alt61) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:400:32: LEFT_SQUARE rightList+= RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_data_type2172); 
            	    rightList=(DroolsTree)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_data_type2176); 
            	    if (list_rightList==null) list_rightList=new ArrayList();
            	    list_rightList.add(rightList);


            	    }
            	    break;

            	default :
            	    break loop61;
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
    public static final BitSet FOLLOW_package_statement_in_compilation_unit51 = new BitSet(new long[]{0xEAFFF80000000028L,0x0000000000000001L});
    public static final BitSet FOLLOW_statement_in_compilation_unit53 = new BitSet(new long[]{0xEAFFF80000000028L,0x0000000000000001L});
    public static final BitSet FOLLOW_VK_PACKAGE_in_package_statement71 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_package_id_in_package_statement75 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PACKAGE_ID_in_package_id102 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_package_id106 = new BitSet(new long[]{0x0000000000000008L,0x0000000008000000L});
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
    public static final BitSet FOLLOW_VK_FUNCTION_in_function_import_statement244 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement248 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_IMPORT_ID_in_import_name267 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_import_name271 = new BitSet(new long[]{0x0000000000000008L,0x0000000028000000L});
    public static final BitSet FOLLOW_DOT_STAR_in_import_name276 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_GLOBAL_in_global299 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_global303 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_VT_GLOBAL_ID_in_global307 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_FUNCTION_in_function329 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_function333 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_VT_FUNCTION_ID_in_function338 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_parameters_in_function342 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_VT_CURLY_CHUNK_in_function346 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_QUERY_in_query368 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_QUERY_ID_in_query372 = new BitSet(new long[]{0x0000040000100000L});
    public static final BitSet FOLLOW_parameters_in_query376 = new BitSet(new long[]{0x0000040000100000L});
    public static final BitSet FOLLOW_lhs_block_in_query381 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_VK_END_in_query385 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_RULE_in_rule412 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_RULE_ID_in_rule416 = new BitSet(new long[]{0x040000000000A000L,0x0000000480000000L});
    public static final BitSet FOLLOW_VK_EXTEND_in_rule421 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_RULE_ID_in_rule425 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_decl_metadata_in_rule435 = new BitSet(new long[]{0x000000000000A000L,0x0000000480000000L});
    public static final BitSet FOLLOW_rule_attributes_in_rule446 = new BitSet(new long[]{0x0000000000008000L,0x0000000400000000L});
    public static final BitSet FOLLOW_when_part_in_rule455 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_VT_RHS_CHUNK_in_rule460 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHEN_in_when_part479 = new BitSet(new long[]{0x0000040000100000L});
    public static final BitSet FOLLOW_lhs_block_in_when_part483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_RULE_ATTRIBUTES_in_rule_attributes505 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_ATTRIBUTES_in_rule_attributes507 = new BitSet(new long[]{0x00FFF80000000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes513 = new BitSet(new long[]{0x00FFF80000000008L});
    public static final BitSet FOLLOW_VT_PARAM_LIST_in_parameters537 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_param_definition_in_parameters542 = new BitSet(new long[]{0x0000001000000008L,0x0000000008000000L});
    public static final BitSet FOLLOW_data_type_in_param_definition564 = new BitSet(new long[]{0x0000001000000008L,0x0000000008000000L});
    public static final BitSet FOLLOW_argument_in_param_definition569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_argument589 = new BitSet(new long[]{0x0000000000000002L,0x0400000000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument592 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument596 = new BitSet(new long[]{0x0000000000000002L,0x0400000000000000L});
    public static final BitSet FOLLOW_VK_DECLARE_in_type_declaration622 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_TYPE_DECLARE_ID_in_type_declaration626 = new BitSet(new long[]{0x0000000000000000L,0x0000000088002000L});
    public static final BitSet FOLLOW_decl_metadata_in_type_declaration635 = new BitSet(new long[]{0x0000000000000000L,0x0000000088002000L});
    public static final BitSet FOLLOW_decl_field_in_type_declaration648 = new BitSet(new long[]{0x0000000000000000L,0x0000000008002000L});
    public static final BitSet FOLLOW_VK_END_in_type_declaration654 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AT_in_decl_metadata679 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_decl_metadata683 = new BitSet(new long[]{0x0000000000040008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_decl_metadata687 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_decl_field715 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_decl_field_initialization_in_decl_field719 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_data_type_in_decl_field724 = new BitSet(new long[]{0x0000000000000008L,0x0000000080000000L});
    public static final BitSet FOLLOW_decl_metadata_in_decl_field729 = new BitSet(new long[]{0x0000000000000008L,0x0000000080000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_decl_field_initialization756 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_decl_field_initialization760 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_SALIENCE_in_rule_attribute783 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SIGNED_DECIMAL_in_rule_attribute788 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_rule_attribute792 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_NO_LOOP_in_rule_attribute803 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute807 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_AGENDA_GROUP_in_rule_attribute819 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute823 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_TIMER_in_rule_attribute834 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SIGNED_DECIMAL_in_rule_attribute839 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_rule_attribute843 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ACTIVATION_GROUP_in_rule_attribute856 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute860 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_AUTO_FOCUS_in_rule_attribute870 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute874 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DATE_EFFECTIVE_in_rule_attribute885 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute889 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DATE_EXPIRES_in_rule_attribute899 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute903 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ENABLED_in_rule_attribute913 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute918 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_rule_attribute922 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_RULEFLOW_GROUP_in_rule_attribute933 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute937 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_LOCK_ON_ACTIVE_in_rule_attribute947 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute951 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DIALECT_in_rule_attribute961 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute965 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_CALENDARS_in_rule_attribute974 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute978 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_AND_IMPLICIT_in_lhs_block1003 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs_block1008 = new BitSet(new long[]{0x0000000021E00008L,0x000000800000018AL});
    public static final BitSet FOLLOW_VT_OR_PREFIX_in_lhs1034 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1039 = new BitSet(new long[]{0x0000000021E00008L,0x000000800000018AL});
    public static final BitSet FOLLOW_VT_OR_INFIX_in_lhs1055 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1059 = new BitSet(new long[]{0x0000000021E00008L,0x000000800000018AL});
    public static final BitSet FOLLOW_lhs_in_lhs1063 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_AND_PREFIX_in_lhs1075 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1080 = new BitSet(new long[]{0x0000000021E00008L,0x000000800000018AL});
    public static final BitSet FOLLOW_VT_AND_INFIX_in_lhs1096 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1100 = new BitSet(new long[]{0x0000000021E00008L,0x000000800000018AL});
    public static final BitSet FOLLOW_lhs_in_lhs1104 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EXISTS_in_lhs1116 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1120 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_NOT_in_lhs1132 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1136 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EVAL_in_lhs1148 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_lhs1152 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_FORALL_in_lhs1164 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1169 = new BitSet(new long[]{0x0000000021E00008L,0x000000800000018AL});
    public static final BitSet FOLLOW_FROM_in_lhs1183 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs1187 = new BitSet(new long[]{0x0000000008000000L,0x0000060000000004L});
    public static final BitSet FOLLOW_from_elements_in_lhs1191 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs1202 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_from_elements1223 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_from_elements1227 = new BitSet(new long[]{0x0000000006000000L});
    public static final BitSet FOLLOW_accumulate_parts_in_from_elements1237 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_COLLECT_in_from_elements1250 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_from_elements1254 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ENTRY_POINT_in_from_elements1266 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_ENTRYPOINT_ID_in_from_elements1270 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_from_source_clause_in_from_elements1281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accumulate_init_clause_in_accumulate_parts1302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accumulate_id_clause_in_accumulate_parts1313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_ACCUMULATE_INIT_CLAUSE_in_accumulate_init_clause1336 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_INIT_in_accumulate_init_clause1345 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1349 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ACTION_in_accumulate_init_clause1357 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1361 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_accumulate_init_reverse_clause_in_accumulate_init_clause1370 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_VK_RESULT_in_accumulate_init_clause1377 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1381 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_REVERSE_in_accumulate_init_reverse_clause1404 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_reverse_clause1408 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ACCUMULATE_ID_CLAUSE_in_accumulate_id_clause1430 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_accumulate_id_clause1434 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_id_clause1438 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FROM_SOURCE_in_from_source_clause1462 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_EXPRESSION_CHAIN_in_expression_chain1492 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_expression_chain1496 = new BitSet(new long[]{0x0000000010060008L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_expression_chain1500 = new BitSet(new long[]{0x0000000010040008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_expression_chain1505 = new BitSet(new long[]{0x0000000010000008L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain1513 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PATTERN_in_lhs_pattern1531 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_lhs_pattern1535 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_over_clause_in_lhs_pattern1540 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OVER_in_over_clause1565 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_over_element_in_over_clause1570 = new BitSet(new long[]{0x0000000000080008L});
    public static final BitSet FOLLOW_VT_BEHAVIOR_in_over_element1591 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_over_element1593 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_over_element1597 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_over_element1601 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FACT_in_fact_expression1624 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_pattern_type_in_fact_expression1628 = new BitSet(new long[]{0x00000007C0040048L,0x03FFD81043801012L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1633 = new BitSet(new long[]{0x00000007C0040048L,0x03FFD81043801012L});
    public static final BitSet FOLLOW_VT_FACT_BINDING_in_fact_expression1647 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_LABEL_in_fact_expression1651 = new BitSet(new long[]{0x00000007C0040040L,0x03FFD81043801012L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1655 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FACT_OR_in_fact_expression1667 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1671 = new BitSet(new long[]{0x00000007C0040040L,0x03FFD81043801012L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1675 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FIELD_in_fact_expression1686 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_field_element_in_fact_expression1690 = new BitSet(new long[]{0x00000007C0040048L,0x03FFD81043801012L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1694 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_BIND_FIELD_in_fact_expression1705 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_LABEL_in_fact_expression1709 = new BitSet(new long[]{0x00000007C0040040L,0x03FFD81043801012L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1713 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EVAL_in_fact_expression1724 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_fact_expression1728 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EQUALS_in_fact_expression1741 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1745 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_EQUALS_in_fact_expression1757 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1761 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GREATER_in_fact_expression1773 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1777 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GREATER_EQUALS_in_fact_expression1789 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1793 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LESS_in_fact_expression1805 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1809 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LESS_EQUALS_in_fact_expression1821 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1825 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_OPERATOR_in_fact_expression1837 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1841 = new BitSet(new long[]{0x00000007C0060040L,0x03FFD81043801012L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_fact_expression1846 = new BitSet(new long[]{0x00000007C0040040L,0x03FFD81043801012L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1851 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_IN_in_fact_expression1862 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1866 = new BitSet(new long[]{0x00000007C0040040L,0x03FFD81043801012L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1872 = new BitSet(new long[]{0x00000007C0040048L,0x03FFD81043801012L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_fact_expression1887 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1891 = new BitSet(new long[]{0x00000007C0040040L,0x03FFD81043801012L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1895 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_fact_expression1905 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1909 = new BitSet(new long[]{0x00000007C0040040L,0x03FFD81043801012L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1913 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ACCESSOR_PATH_in_fact_expression1924 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_accessor_element_in_fact_expression1929 = new BitSet(new long[]{0x0000000800000008L});
    public static final BitSet FOLLOW_STRING_in_fact_expression1944 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_fact_expression1953 = new BitSet(new long[]{0x0000000000000000L,0x0380000003800000L});
    public static final BitSet FOLLOW_MINUS_in_fact_expression1957 = new BitSet(new long[]{0x0000000000000000L,0x0380000003800000L});
    public static final BitSet FOLLOW_DECIMAL_in_fact_expression1976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIGNED_DECIMAL_in_fact_expression1980 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HEX_in_fact_expression1992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIGNED_HEX_in_fact_expression1996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_fact_expression2010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIGNED_FLOAT_in_fact_expression2014 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_fact_expression2030 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_fact_expression2040 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_fact_expression2050 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_ACCESSOR_PATH_in_field_element2072 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_accessor_element_in_field_element2077 = new BitSet(new long[]{0x0000000800000008L});
    public static final BitSet FOLLOW_VT_ACCESSOR_ELEMENT_in_accessor_element2101 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_accessor_element2105 = new BitSet(new long[]{0x0000000000020008L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_accessor_element2109 = new BitSet(new long[]{0x0000000000020008L});
    public static final BitSet FOLLOW_VT_PATTERN_TYPE_in_pattern_type2130 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_pattern_type2134 = new BitSet(new long[]{0x0000000000000008L,0x0400000008000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_pattern_type2138 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_pattern_type2142 = new BitSet(new long[]{0x0000000000000008L,0x0400000000000000L});
    public static final BitSet FOLLOW_VT_DATA_TYPE_in_data_type2164 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_data_type2168 = new BitSet(new long[]{0x0000000000000008L,0x0400000008000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_data_type2172 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_data_type2176 = new BitSet(new long[]{0x0000000000000008L,0x0400000000000000L});

}