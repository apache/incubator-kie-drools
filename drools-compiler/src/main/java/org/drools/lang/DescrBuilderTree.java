// $ANTLR 3.1.1 src/main/resources/org/drools/lang/DescrBuilderTree.g 2009-12-07 17:31:02

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_COMPILATION_UNIT", "VT_FUNCTION_IMPORT", "VT_FACT", "VT_CONSTRAINTS", "VT_LABEL", "VT_QUERY_ID", "VT_TEMPLATE_ID", "VT_TYPE_DECLARE_ID", "VT_RULE_ID", "VT_ENTRYPOINT_ID", "VT_SLOT_ID", "VT_SLOT", "VT_RULE_ATTRIBUTES", "VT_RHS_CHUNK", "VT_CURLY_CHUNK", "VT_SQUARE_CHUNK", "VT_PAREN_CHUNK", "VT_BEHAVIOR", "VT_AND_IMPLICIT", "VT_AND_PREFIX", "VT_OR_PREFIX", "VT_AND_INFIX", "VT_OR_INFIX", "VT_ACCUMULATE_INIT_CLAUSE", "VT_ACCUMULATE_ID_CLAUSE", "VT_FROM_SOURCE", "VT_EXPRESSION_CHAIN", "VT_PATTERN", "VT_FACT_BINDING", "VT_FACT_OR", "VT_BIND_FIELD", "VT_FIELD", "VT_ACCESSOR_PATH", "VT_ACCESSOR_ELEMENT", "VT_DATA_TYPE", "VT_PATTERN_TYPE", "VT_PACKAGE_ID", "VT_IMPORT_ID", "VT_GLOBAL_ID", "VT_FUNCTION_ID", "VT_PARAM_LIST", "VK_DATE_EFFECTIVE", "VK_DATE_EXPIRES", "VK_LOCK_ON_ACTIVE", "VK_NO_LOOP", "VK_AUTO_FOCUS", "VK_ACTIVATION_GROUP", "VK_AGENDA_GROUP", "VK_RULEFLOW_GROUP", "VK_TIMER", "VK_CALENDARS", "VK_DIALECT", "VK_SALIENCE", "VK_ENABLED", "VK_ATTRIBUTES", "VK_RULE", "VK_EXTEND", "VK_IMPORT", "VK_PACKAGE", "VK_TEMPLATE", "VK_QUERY", "VK_DECLARE", "VK_FUNCTION", "VK_GLOBAL", "VK_EVAL", "VK_ENTRY_POINT", "VK_NOT", "VK_IN", "VK_OR", "VK_AND", "VK_EXISTS", "VK_FORALL", "VK_ACTION", "VK_REVERSE", "VK_RESULT", "VK_OPERATOR", "VK_END", "VK_INIT", "SEMICOLON", "ID", "DOT", "DOT_STAR", "STRING", "LEFT_PAREN", "COMMA", "RIGHT_PAREN", "AT", "COLON", "EQUALS", "WHEN", "BOOL", "INT", "DOUBLE_PIPE", "DOUBLE_AMPER", "FROM", "OVER", "ACCUMULATE", "COLLECT", "ARROW", "EQUAL", "GREATER", "GREATER_EQUAL", "LESS", "LESS_EQUAL", "NOT_EQUAL", "FLOAT", "NULL", "LEFT_SQUARE", "RIGHT_SQUARE", "THEN", "LEFT_CURLY", "RIGHT_CURLY", "MISC", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "IdentifierStart", "IdentifierPart"
    };
    public static final int COMMA=88;
    public static final int VT_PATTERN_TYPE=39;
    public static final int VT_ACCUMULATE_ID_CLAUSE=28;
    public static final int VK_DIALECT=55;
    public static final int VK_FUNCTION=66;
    public static final int HexDigit=120;
    public static final int VK_ATTRIBUTES=58;
    public static final int VT_EXPRESSION_CHAIN=30;
    public static final int MISC=116;
    public static final int VT_AND_PREFIX=23;
    public static final int VK_QUERY=64;
    public static final int THEN=113;
    public static final int VK_AUTO_FOCUS=49;
    public static final int DOT=84;
    public static final int VK_IMPORT=61;
    public static final int VT_SLOT=15;
    public static final int VT_PACKAGE_ID=40;
    public static final int LEFT_SQUARE=111;
    public static final int VK_TIMER=53;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=123;
    public static final int VT_DATA_TYPE=38;
    public static final int VT_FACT=6;
    public static final int LEFT_CURLY=114;
    public static final int AT=90;
    public static final int LEFT_PAREN=87;
    public static final int DOUBLE_AMPER=97;
    public static final int IdentifierPart=127;
    public static final int VT_QUERY_ID=9;
    public static final int VT_ACCESSOR_PATH=36;
    public static final int VT_LABEL=8;
    public static final int WHEN=93;
    public static final int VT_ENTRYPOINT_ID=13;
    public static final int VK_SALIENCE=56;
    public static final int VT_FIELD=35;
    public static final int WS=118;
    public static final int OVER=99;
    public static final int STRING=86;
    public static final int VK_AND=73;
    public static final int VT_ACCESSOR_ELEMENT=37;
    public static final int VT_ACCUMULATE_INIT_CLAUSE=27;
    public static final int VK_GLOBAL=67;
    public static final int VK_REVERSE=77;
    public static final int VT_BEHAVIOR=21;
    public static final int VT_SQUARE_CHUNK=19;
    public static final int VK_FORALL=75;
    public static final int VT_PAREN_CHUNK=20;
    public static final int VT_COMPILATION_UNIT=4;
    public static final int COLLECT=101;
    public static final int VK_ENABLED=57;
    public static final int EQUALS=92;
    public static final int VK_RESULT=78;
    public static final int UnicodeEscape=121;
    public static final int VK_PACKAGE=62;
    public static final int VT_RULE_ID=12;
    public static final int EQUAL=103;
    public static final int VK_NO_LOOP=48;
    public static final int IdentifierStart=126;
    public static final int SEMICOLON=82;
    public static final int VK_TEMPLATE=63;
    public static final int VT_AND_IMPLICIT=22;
    public static final int NULL=110;
    public static final int COLON=91;
    public static final int MULTI_LINE_COMMENT=125;
    public static final int VT_RULE_ATTRIBUTES=16;
    public static final int RIGHT_SQUARE=112;
    public static final int VK_AGENDA_GROUP=51;
    public static final int VT_FACT_OR=33;
    public static final int VK_NOT=70;
    public static final int VK_DATE_EXPIRES=46;
    public static final int ARROW=102;
    public static final int FLOAT=109;
    public static final int VK_EXTEND=60;
    public static final int VT_SLOT_ID=14;
    public static final int VT_CURLY_CHUNK=18;
    public static final int VT_OR_PREFIX=24;
    public static final int DOUBLE_PIPE=96;
    public static final int VK_END=80;
    public static final int LESS=106;
    public static final int VT_TYPE_DECLARE_ID=11;
    public static final int VT_PATTERN=31;
    public static final int VK_DATE_EFFECTIVE=45;
    public static final int EscapeSequence=119;
    public static final int VK_EXISTS=74;
    public static final int INT=95;
    public static final int VT_BIND_FIELD=34;
    public static final int VK_RULE=59;
    public static final int VK_EVAL=68;
    public static final int GREATER=104;
    public static final int VT_FACT_BINDING=32;
    public static final int ID=83;
    public static final int FROM=98;
    public static final int NOT_EQUAL=108;
    public static final int RIGHT_CURLY=115;
    public static final int VK_OPERATOR=79;
    public static final int VK_ENTRY_POINT=69;
    public static final int VT_PARAM_LIST=44;
    public static final int VT_AND_INFIX=25;
    public static final int BOOL=94;
    public static final int VT_FROM_SOURCE=29;
    public static final int VK_LOCK_ON_ACTIVE=47;
    public static final int VT_FUNCTION_IMPORT=5;
    public static final int VK_IN=71;
    public static final int VT_RHS_CHUNK=17;
    public static final int GREATER_EQUAL=105;
    public static final int VT_OR_INFIX=26;
    public static final int DOT_STAR=85;
    public static final int VK_OR=72;
    public static final int VT_GLOBAL_ID=42;
    public static final int LESS_EQUAL=107;
    public static final int ACCUMULATE=100;
    public static final int VK_RULEFLOW_GROUP=52;
    public static final int VT_FUNCTION_ID=43;
    public static final int EOF=-1;
    public static final int VT_CONSTRAINTS=7;
    public static final int VT_IMPORT_ID=41;
    public static final int EOL=117;
    public static final int VK_INIT=81;
    public static final int VK_ACTIVATION_GROUP=50;
    public static final int OctalEscape=122;
    public static final int VK_ACTION=76;
    public static final int RIGHT_PAREN=89;
    public static final int VK_CALENDARS=54;
    public static final int VT_TEMPLATE_ID=10;
    public static final int VK_DECLARE=65;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=124;

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

                    if ( (LA1_0==VT_FUNCTION_IMPORT||(LA1_0>=VK_DATE_EFFECTIVE && LA1_0<=VK_ENABLED)||LA1_0==VK_RULE||LA1_0==VK_IMPORT||(LA1_0>=VK_TEMPLATE && LA1_0<=VK_GLOBAL)) ) {
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
            else if ( (LA2_0==UP||LA2_0==VT_FUNCTION_IMPORT||(LA2_0>=VK_DATE_EFFECTIVE && LA2_0<=VK_ENABLED)||LA2_0==VK_RULE||LA2_0==VK_IMPORT||(LA2_0>=VK_TEMPLATE && LA2_0<=VK_GLOBAL)) ) {
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:66:1: statement : (a= rule_attribute | fi= function_import_statement | is= import_statement | gl= global | fn= function | tp= template | rl= rule | qr= query | td= type_declaration );
    public final void statement() throws RecognitionException {
        AttributeDescr a = null;

        FunctionImportDescr fi = null;

        ImportDescr is = null;

        DescrBuilderTree.global_return gl = null;

        DescrBuilderTree.function_return fn = null;

        DescrBuilderTree.template_return tp = null;

        DescrBuilderTree.rule_return rl = null;

        DescrBuilderTree.query_return qr = null;

        TypeDeclarationDescr td = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:67:2: (a= rule_attribute | fi= function_import_statement | is= import_statement | gl= global | fn= function | tp= template | rl= rule | qr= query | td= type_declaration )
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
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:77:4: tp= template
                    {
                    pushFollow(FOLLOW_template_in_statement175);
                    tp=template();

                    state._fsp--;

                    	this.packageDescr.addFactTemplate((tp!=null?tp.factTemplateDescr:null));	

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:79:4: rl= rule
                    {
                    pushFollow(FOLLOW_rule_in_statement185);
                    rl=rule();

                    state._fsp--;

                    	this.packageDescr.addRule((rl!=null?rl.ruleDescr:null));	

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:81:4: qr= query
                    {
                    pushFollow(FOLLOW_query_in_statement195);
                    qr=query();

                    state._fsp--;

                    	this.packageDescr.addRule((qr!=null?qr.queryDescr:null));	

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:83:4: td= type_declaration
                    {
                    pushFollow(FOLLOW_type_declaration_in_statement205);
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:87:1: import_statement returns [ImportDescr importDescr] : ^(importStart= VK_IMPORT importId= import_name ) ;
    public final ImportDescr import_statement() throws RecognitionException {
        ImportDescr importDescr = null;

        DroolsTree importStart=null;
        DescrBuilderTree.import_name_return importId = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:88:2: ( ^(importStart= VK_IMPORT importId= import_name ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:88:4: ^(importStart= VK_IMPORT importId= import_name )
            {
            importStart=(DroolsTree)match(input,VK_IMPORT,FOLLOW_VK_IMPORT_in_import_statement226); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_import_name_in_import_statement230);
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:92:1: function_import_statement returns [FunctionImportDescr functionImportDescr] : ^(importStart= VT_FUNCTION_IMPORT VK_FUNCTION importId= import_name ) ;
    public final FunctionImportDescr function_import_statement() throws RecognitionException {
        FunctionImportDescr functionImportDescr = null;

        DroolsTree importStart=null;
        DescrBuilderTree.import_name_return importId = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:93:2: ( ^(importStart= VT_FUNCTION_IMPORT VK_FUNCTION importId= import_name ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:93:4: ^(importStart= VT_FUNCTION_IMPORT VK_FUNCTION importId= import_name )
            {
            importStart=(DroolsTree)match(input,VT_FUNCTION_IMPORT,FOLLOW_VT_FUNCTION_IMPORT_in_function_import_statement252); 

            match(input, Token.DOWN, null); 
            match(input,VK_FUNCTION,FOLLOW_VK_FUNCTION_in_function_import_statement254); 
            pushFollow(FOLLOW_import_name_in_function_import_statement258);
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:97:1: import_name returns [List idList, DroolsTree dotStar] : ^( VT_IMPORT_ID (tempList+= ID )+ (tempDotStar= DOT_STAR )? ) ;
    public final DescrBuilderTree.import_name_return import_name() throws RecognitionException {
        DescrBuilderTree.import_name_return retval = new DescrBuilderTree.import_name_return();
        retval.start = input.LT(1);

        DroolsTree tempDotStar=null;
        DroolsTree tempList=null;
        List list_tempList=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:98:2: ( ^( VT_IMPORT_ID (tempList+= ID )+ (tempDotStar= DOT_STAR )? ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:98:4: ^( VT_IMPORT_ID (tempList+= ID )+ (tempDotStar= DOT_STAR )? )
            {
            match(input,VT_IMPORT_ID,FOLLOW_VT_IMPORT_ID_in_import_name277); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:98:27: (tempList+= ID )+
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
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:98:27: tempList+= ID
            	    {
            	    tempList=(DroolsTree)match(input,ID,FOLLOW_ID_in_import_name281); 
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

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:98:44: (tempDotStar= DOT_STAR )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==DOT_STAR) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:98:44: tempDotStar= DOT_STAR
                    {
                    tempDotStar=(DroolsTree)match(input,DOT_STAR,FOLLOW_DOT_STAR_in_import_name286); 

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:103:1: global returns [GlobalDescr globalDescr] : ^(start= VK_GLOBAL dt= data_type globalId= VT_GLOBAL_ID ) ;
    public final DescrBuilderTree.global_return global() throws RecognitionException {
        DescrBuilderTree.global_return retval = new DescrBuilderTree.global_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree globalId=null;
        BaseDescr dt = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:104:2: ( ^(start= VK_GLOBAL dt= data_type globalId= VT_GLOBAL_ID ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:104:4: ^(start= VK_GLOBAL dt= data_type globalId= VT_GLOBAL_ID )
            {
            start=(DroolsTree)match(input,VK_GLOBAL,FOLLOW_VK_GLOBAL_in_global309); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_data_type_in_global313);
            dt=data_type();

            state._fsp--;

            globalId=(DroolsTree)match(input,VT_GLOBAL_ID,FOLLOW_VT_GLOBAL_ID_in_global317); 

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:108:1: function returns [FunctionDescr functionDescr] : ^(start= VK_FUNCTION (dt= data_type )? functionId= VT_FUNCTION_ID params= parameters content= VT_CURLY_CHUNK ) ;
    public final DescrBuilderTree.function_return function() throws RecognitionException {
        DescrBuilderTree.function_return retval = new DescrBuilderTree.function_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree functionId=null;
        DroolsTree content=null;
        BaseDescr dt = null;

        List params = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:109:2: ( ^(start= VK_FUNCTION (dt= data_type )? functionId= VT_FUNCTION_ID params= parameters content= VT_CURLY_CHUNK ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:109:4: ^(start= VK_FUNCTION (dt= data_type )? functionId= VT_FUNCTION_ID params= parameters content= VT_CURLY_CHUNK )
            {
            start=(DroolsTree)match(input,VK_FUNCTION,FOLLOW_VK_FUNCTION_in_function339); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:109:26: (dt= data_type )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==VT_DATA_TYPE) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:109:26: dt= data_type
                    {
                    pushFollow(FOLLOW_data_type_in_function343);
                    dt=data_type();

                    state._fsp--;


                    }
                    break;

            }

            functionId=(DroolsTree)match(input,VT_FUNCTION_ID,FOLLOW_VT_FUNCTION_ID_in_function348); 
            pushFollow(FOLLOW_parameters_in_function352);
            params=parameters();

            state._fsp--;

            content=(DroolsTree)match(input,VT_CURLY_CHUNK,FOLLOW_VT_CURLY_CHUNK_in_function356); 

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

    public static class template_return extends TreeRuleReturnScope {
        public FactTemplateDescr factTemplateDescr;
    };

    // $ANTLR start "template"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:113:1: template returns [FactTemplateDescr factTemplateDescr] : ^(start= VK_TEMPLATE id= VT_TEMPLATE_ID (ts= template_slot )+ end= VK_END ) ;
    public final DescrBuilderTree.template_return template() throws RecognitionException {
        DescrBuilderTree.template_return retval = new DescrBuilderTree.template_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree id=null;
        DroolsTree end=null;
        FieldTemplateDescr ts = null;



        	List slotList = new LinkedList<FieldTemplateDescr>();

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:116:3: ( ^(start= VK_TEMPLATE id= VT_TEMPLATE_ID (ts= template_slot )+ end= VK_END ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:116:5: ^(start= VK_TEMPLATE id= VT_TEMPLATE_ID (ts= template_slot )+ end= VK_END )
            {
            start=(DroolsTree)match(input,VK_TEMPLATE,FOLLOW_VK_TEMPLATE_in_template381); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,VT_TEMPLATE_ID,FOLLOW_VT_TEMPLATE_ID_in_template385); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:117:4: (ts= template_slot )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==VT_SLOT) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:117:6: ts= template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template394);
            	    ts=template_slot();

            	    state._fsp--;

            	    slotList.add(ts);

            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);

            end=(DroolsTree)match(input,VK_END,FOLLOW_VK_END_in_template402); 

            match(input, Token.UP, null); 
            	retval.factTemplateDescr = factory.createFactTemplate(start, id, slotList, end);	

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
    // $ANTLR end "template"


    // $ANTLR start "template_slot"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:121:1: template_slot returns [FieldTemplateDescr fieldTemplateDescr] : ^( VT_SLOT dt= data_type id= VT_SLOT_ID ) ;
    public final FieldTemplateDescr template_slot() throws RecognitionException {
        FieldTemplateDescr fieldTemplateDescr = null;

        DroolsTree id=null;
        BaseDescr dt = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:122:2: ( ^( VT_SLOT dt= data_type id= VT_SLOT_ID ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:122:4: ^( VT_SLOT dt= data_type id= VT_SLOT_ID )
            {
            match(input,VT_SLOT,FOLLOW_VT_SLOT_in_template_slot422); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_data_type_in_template_slot426);
            dt=data_type();

            state._fsp--;

            id=(DroolsTree)match(input,VT_SLOT_ID,FOLLOW_VT_SLOT_ID_in_template_slot430); 

            match(input, Token.UP, null); 
            	fieldTemplateDescr = factory.createFieldTemplate(dt, id);	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return fieldTemplateDescr;
    }
    // $ANTLR end "template_slot"

    public static class query_return extends TreeRuleReturnScope {
        public QueryDescr queryDescr;
    };

    // $ANTLR start "query"
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:126:1: query returns [QueryDescr queryDescr] : ^(start= VK_QUERY id= VT_QUERY_ID (params= parameters )? lb= lhs_block end= VK_END ) ;
    public final DescrBuilderTree.query_return query() throws RecognitionException {
        DescrBuilderTree.query_return retval = new DescrBuilderTree.query_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree id=null;
        DroolsTree end=null;
        List params = null;

        AndDescr lb = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:127:2: ( ^(start= VK_QUERY id= VT_QUERY_ID (params= parameters )? lb= lhs_block end= VK_END ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:127:4: ^(start= VK_QUERY id= VT_QUERY_ID (params= parameters )? lb= lhs_block end= VK_END )
            {
            start=(DroolsTree)match(input,VK_QUERY,FOLLOW_VK_QUERY_in_query452); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,VT_QUERY_ID,FOLLOW_VT_QUERY_ID_in_query456); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:127:42: (params= parameters )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==VT_PARAM_LIST) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:127:42: params= parameters
                    {
                    pushFollow(FOLLOW_parameters_in_query460);
                    params=parameters();

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_lhs_block_in_query465);
            lb=lhs_block();

            state._fsp--;

            end=(DroolsTree)match(input,VK_END,FOLLOW_VK_END_in_query469); 

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:131:1: rule returns [RuleDescr ruleDescr] : ^(start= VK_RULE id= VT_RULE_ID ( ^( VK_EXTEND parent_id= VT_RULE_ID ) )? (dm= decl_metadata )* (ra= rule_attributes )? (wn= when_part )? content= VT_RHS_CHUNK ) ;
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
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:133:2: ( ^(start= VK_RULE id= VT_RULE_ID ( ^( VK_EXTEND parent_id= VT_RULE_ID ) )? (dm= decl_metadata )* (ra= rule_attributes )? (wn= when_part )? content= VT_RHS_CHUNK ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:133:4: ^(start= VK_RULE id= VT_RULE_ID ( ^( VK_EXTEND parent_id= VT_RULE_ID ) )? (dm= decl_metadata )* (ra= rule_attributes )? (wn= when_part )? content= VT_RHS_CHUNK )
            {
            start=(DroolsTree)match(input,VK_RULE,FOLLOW_VK_RULE_in_rule496); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,VT_RULE_ID,FOLLOW_VT_RULE_ID_in_rule500); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:133:35: ( ^( VK_EXTEND parent_id= VT_RULE_ID ) )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==VK_EXTEND) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:133:36: ^( VK_EXTEND parent_id= VT_RULE_ID )
                    {
                    match(input,VK_EXTEND,FOLLOW_VK_EXTEND_in_rule505); 

                    match(input, Token.DOWN, null); 
                    parent_id=(DroolsTree)match(input,VT_RULE_ID,FOLLOW_VT_RULE_ID_in_rule509); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:134:3: (dm= decl_metadata )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==AT) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:134:4: dm= decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_rule519);
            	    dm=decl_metadata();

            	    state._fsp--;

            	    declMetadaList.add(dm);	

            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:135:6: (ra= rule_attributes )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==VT_RULE_ATTRIBUTES) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:135:6: ra= rule_attributes
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule530);
                    ra=rule_attributes();

                    state._fsp--;


                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:136:6: (wn= when_part )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==WHEN) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:136:6: wn= when_part
                    {
                    pushFollow(FOLLOW_when_part_in_rule539);
                    wn=when_part();

                    state._fsp--;


                    }
                    break;

            }

            content=(DroolsTree)match(input,VT_RHS_CHUNK,FOLLOW_VT_RHS_CHUNK_in_rule544); 

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:140:1: when_part returns [AndDescr andDescr] : WHEN lh= lhs_block ;
    public final AndDescr when_part() throws RecognitionException {
        AndDescr andDescr = null;

        AndDescr lh = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:141:2: ( WHEN lh= lhs_block )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:141:4: WHEN lh= lhs_block
            {
            match(input,WHEN,FOLLOW_WHEN_in_when_part563); 
            pushFollow(FOLLOW_lhs_block_in_when_part567);
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:145:1: rule_attributes returns [List attrList] : ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? (rl= rule_attribute )+ ) ;
    public final List rule_attributes() throws RecognitionException {
        List attrList = null;

        AttributeDescr rl = null;



        	attrList = new LinkedList<AttributeDescr>();

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:148:3: ( ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? (rl= rule_attribute )+ ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:148:5: ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? (rl= rule_attribute )+ )
            {
            match(input,VT_RULE_ATTRIBUTES,FOLLOW_VT_RULE_ATTRIBUTES_in_rule_attributes589); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:148:26: ( VK_ATTRIBUTES )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==VK_ATTRIBUTES) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:148:26: VK_ATTRIBUTES
                    {
                    match(input,VK_ATTRIBUTES,FOLLOW_VK_ATTRIBUTES_in_rule_attributes591); 

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:148:41: (rl= rule_attribute )+
            int cnt15=0;
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( ((LA15_0>=VK_DATE_EFFECTIVE && LA15_0<=VK_ENABLED)) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:148:42: rl= rule_attribute
            	    {
            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes597);
            	    rl=rule_attribute();

            	    state._fsp--;

            	    attrList.add(rl);

            	    }
            	    break;

            	default :
            	    if ( cnt15 >= 1 ) break loop15;
                        EarlyExitException eee =
                            new EarlyExitException(15, input);
                        throw eee;
                }
                cnt15++;
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:151:1: parameters returns [List paramList] : ^( VT_PARAM_LIST (p= param_definition )* ) ;
    public final List parameters() throws RecognitionException {
        List paramList = null;

        Map p = null;



        	paramList = new LinkedList<Map<BaseDescr, BaseDescr>>();

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:154:3: ( ^( VT_PARAM_LIST (p= param_definition )* ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:154:5: ^( VT_PARAM_LIST (p= param_definition )* )
            {
            match(input,VT_PARAM_LIST,FOLLOW_VT_PARAM_LIST_in_parameters621); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // src/main/resources/org/drools/lang/DescrBuilderTree.g:154:21: (p= param_definition )*
                loop16:
                do {
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==VT_DATA_TYPE||LA16_0==ID) ) {
                        alt16=1;
                    }


                    switch (alt16) {
                	case 1 :
                	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:154:22: p= param_definition
                	    {
                	    pushFollow(FOLLOW_param_definition_in_parameters626);
                	    p=param_definition();

                	    state._fsp--;

                	    paramList.add(p);

                	    }
                	    break;

                	default :
                	    break loop16;
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:157:1: param_definition returns [Map param] : (dt= data_type )? a= argument ;
    public final Map param_definition() throws RecognitionException {
        Map param = null;

        BaseDescr dt = null;

        BaseDescr a = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:158:2: ( (dt= data_type )? a= argument )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:158:4: (dt= data_type )? a= argument
            {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:158:6: (dt= data_type )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==VT_DATA_TYPE) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:158:6: dt= data_type
                    {
                    pushFollow(FOLLOW_data_type_in_param_definition648);
                    dt=data_type();

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_argument_in_param_definition653);
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:163:1: argument returns [BaseDescr arg] : id= ID ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ;
    public final BaseDescr argument() throws RecognitionException {
        BaseDescr arg = null;

        DroolsTree id=null;
        DroolsTree rightList=null;
        List list_rightList=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:164:2: (id= ID ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:164:4: id= ID ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            {
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_argument673); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:164:10: ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==LEFT_SQUARE) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:164:11: LEFT_SQUARE rightList+= RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_argument676); 
            	    rightList=(DroolsTree)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_argument680); 
            	    if (list_rightList==null) list_rightList=new ArrayList();
            	    list_rightList.add(rightList);


            	    }
            	    break;

            	default :
            	    break loop18;
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:168:1: type_declaration returns [TypeDeclarationDescr declaration] : ^( VK_DECLARE id= VT_TYPE_DECLARE_ID (dm= decl_metadata )* (df= decl_field )* VK_END ) ;
    public final TypeDeclarationDescr type_declaration() throws RecognitionException {
        TypeDeclarationDescr declaration = null;

        DroolsTree id=null;
        Map dm = null;

        TypeFieldDescr df = null;


        	List<Map> declMetadaList = new LinkedList<Map>();
        		List<TypeFieldDescr> declFieldList = new LinkedList<TypeFieldDescr>(); 
        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:171:2: ( ^( VK_DECLARE id= VT_TYPE_DECLARE_ID (dm= decl_metadata )* (df= decl_field )* VK_END ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:171:4: ^( VK_DECLARE id= VT_TYPE_DECLARE_ID (dm= decl_metadata )* (df= decl_field )* VK_END )
            {
            match(input,VK_DECLARE,FOLLOW_VK_DECLARE_in_type_declaration706); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,VT_TYPE_DECLARE_ID,FOLLOW_VT_TYPE_DECLARE_ID_in_type_declaration710); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:172:4: (dm= decl_metadata )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==AT) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:172:5: dm= decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_type_declaration719);
            	    dm=decl_metadata();

            	    state._fsp--;

            	    declMetadaList.add(dm);	

            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:173:4: (df= decl_field )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==ID) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:173:5: df= decl_field
            	    {
            	    pushFollow(FOLLOW_decl_field_in_type_declaration732);
            	    df=decl_field();

            	    state._fsp--;

            	    declFieldList.add(df);	

            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);

            match(input,VK_END,FOLLOW_VK_END_in_type_declaration738); 

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:177:1: decl_metadata returns [Map attData] : ^( AT att= ID (pc= VT_PAREN_CHUNK )? ) ;
    public final Map decl_metadata() throws RecognitionException {
        Map attData = null;

        DroolsTree att=null;
        DroolsTree pc=null;

        attData = new HashMap();
        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:179:2: ( ^( AT att= ID (pc= VT_PAREN_CHUNK )? ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:179:4: ^( AT att= ID (pc= VT_PAREN_CHUNK )? )
            {
            match(input,AT,FOLLOW_AT_in_decl_metadata763); 

            match(input, Token.DOWN, null); 
            att=(DroolsTree)match(input,ID,FOLLOW_ID_in_decl_metadata767); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:179:18: (pc= VT_PAREN_CHUNK )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==VT_PAREN_CHUNK) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:179:18: pc= VT_PAREN_CHUNK
                    {
                    pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_decl_metadata771); 

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:183:1: decl_field returns [TypeFieldDescr fieldDescr] : ^(id= ID (init= decl_field_initialization )? dt= data_type (dm= decl_metadata )* ) ;
    public final TypeFieldDescr decl_field() throws RecognitionException {
        TypeFieldDescr fieldDescr = null;

        DroolsTree id=null;
        String init = null;

        BaseDescr dt = null;

        Map dm = null;


        List<Map> declMetadaList = new LinkedList<Map>(); 
        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:185:2: ( ^(id= ID (init= decl_field_initialization )? dt= data_type (dm= decl_metadata )* ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:185:4: ^(id= ID (init= decl_field_initialization )? dt= data_type (dm= decl_metadata )* )
            {
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_decl_field799); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:185:16: (init= decl_field_initialization )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==EQUALS) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:185:16: init= decl_field_initialization
                    {
                    pushFollow(FOLLOW_decl_field_initialization_in_decl_field803);
                    init=decl_field_initialization();

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_data_type_in_decl_field808);
            dt=data_type();

            state._fsp--;

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:185:57: (dm= decl_metadata )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==AT) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:185:58: dm= decl_metadata
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_decl_field813);
            	    dm=decl_metadata();

            	    state._fsp--;

            	    declMetadaList.add(dm);

            	    }
            	    break;

            	default :
            	    break loop23;
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:189:1: decl_field_initialization returns [String expr] : ^( EQUALS pc= VT_PAREN_CHUNK ) ;
    public final String decl_field_initialization() throws RecognitionException {
        String expr = null;

        DroolsTree pc=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:190:2: ( ^( EQUALS pc= VT_PAREN_CHUNK ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:190:4: ^( EQUALS pc= VT_PAREN_CHUNK )
            {
            match(input,EQUALS,FOLLOW_EQUALS_in_decl_field_initialization840); 

            match(input, Token.DOWN, null); 
            pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_decl_field_initialization844); 

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:194:1: rule_attribute returns [AttributeDescr attributeDescr] : ( ^(attrName= VK_SALIENCE (value= INT | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_NO_LOOP (value= BOOL )? ) | ^(attrName= VK_AGENDA_GROUP value= STRING ) | ^(attrName= VK_TIMER (value= INT | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_ACTIVATION_GROUP value= STRING ) | ^(attrName= VK_AUTO_FOCUS (value= BOOL )? ) | ^(attrName= VK_DATE_EFFECTIVE value= STRING ) | ^(attrName= VK_DATE_EXPIRES value= STRING ) | ^(attrName= VK_ENABLED (value= BOOL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_RULEFLOW_GROUP value= STRING ) | ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? ) | ^(attrName= VK_DIALECT value= STRING ) | ^(attrName= VK_CALENDARS value= STRING ) ) ;
    public final AttributeDescr rule_attribute() throws RecognitionException {
        AttributeDescr attributeDescr = null;

        DroolsTree attrName=null;
        DroolsTree value=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:195:2: ( ( ^(attrName= VK_SALIENCE (value= INT | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_NO_LOOP (value= BOOL )? ) | ^(attrName= VK_AGENDA_GROUP value= STRING ) | ^(attrName= VK_TIMER (value= INT | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_ACTIVATION_GROUP value= STRING ) | ^(attrName= VK_AUTO_FOCUS (value= BOOL )? ) | ^(attrName= VK_DATE_EFFECTIVE value= STRING ) | ^(attrName= VK_DATE_EXPIRES value= STRING ) | ^(attrName= VK_ENABLED (value= BOOL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_RULEFLOW_GROUP value= STRING ) | ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? ) | ^(attrName= VK_DIALECT value= STRING ) | ^(attrName= VK_CALENDARS value= STRING ) ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:195:4: ( ^(attrName= VK_SALIENCE (value= INT | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_NO_LOOP (value= BOOL )? ) | ^(attrName= VK_AGENDA_GROUP value= STRING ) | ^(attrName= VK_TIMER (value= INT | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_ACTIVATION_GROUP value= STRING ) | ^(attrName= VK_AUTO_FOCUS (value= BOOL )? ) | ^(attrName= VK_DATE_EFFECTIVE value= STRING ) | ^(attrName= VK_DATE_EXPIRES value= STRING ) | ^(attrName= VK_ENABLED (value= BOOL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_RULEFLOW_GROUP value= STRING ) | ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? ) | ^(attrName= VK_DIALECT value= STRING ) | ^(attrName= VK_CALENDARS value= STRING ) )
            {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:195:4: ( ^(attrName= VK_SALIENCE (value= INT | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_NO_LOOP (value= BOOL )? ) | ^(attrName= VK_AGENDA_GROUP value= STRING ) | ^(attrName= VK_TIMER (value= INT | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_ACTIVATION_GROUP value= STRING ) | ^(attrName= VK_AUTO_FOCUS (value= BOOL )? ) | ^(attrName= VK_DATE_EFFECTIVE value= STRING ) | ^(attrName= VK_DATE_EXPIRES value= STRING ) | ^(attrName= VK_ENABLED (value= BOOL | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_RULEFLOW_GROUP value= STRING ) | ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? ) | ^(attrName= VK_DIALECT value= STRING ) | ^(attrName= VK_CALENDARS value= STRING ) )
            int alt30=13;
            switch ( input.LA(1) ) {
            case VK_SALIENCE:
                {
                alt30=1;
                }
                break;
            case VK_NO_LOOP:
                {
                alt30=2;
                }
                break;
            case VK_AGENDA_GROUP:
                {
                alt30=3;
                }
                break;
            case VK_TIMER:
                {
                alt30=4;
                }
                break;
            case VK_ACTIVATION_GROUP:
                {
                alt30=5;
                }
                break;
            case VK_AUTO_FOCUS:
                {
                alt30=6;
                }
                break;
            case VK_DATE_EFFECTIVE:
                {
                alt30=7;
                }
                break;
            case VK_DATE_EXPIRES:
                {
                alt30=8;
                }
                break;
            case VK_ENABLED:
                {
                alt30=9;
                }
                break;
            case VK_RULEFLOW_GROUP:
                {
                alt30=10;
                }
                break;
            case VK_LOCK_ON_ACTIVE:
                {
                alt30=11;
                }
                break;
            case VK_DIALECT:
                {
                alt30=12;
                }
                break;
            case VK_CALENDARS:
                {
                alt30=13;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;
            }

            switch (alt30) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:195:5: ^(attrName= VK_SALIENCE (value= INT | value= VT_PAREN_CHUNK ) )
                    {
                    attrName=(DroolsTree)match(input,VK_SALIENCE,FOLLOW_VK_SALIENCE_in_rule_attribute867); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:195:28: (value= INT | value= VT_PAREN_CHUNK )
                    int alt24=2;
                    int LA24_0 = input.LA(1);

                    if ( (LA24_0==INT) ) {
                        alt24=1;
                    }
                    else if ( (LA24_0==VT_PAREN_CHUNK) ) {
                        alt24=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 24, 0, input);

                        throw nvae;
                    }
                    switch (alt24) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:195:29: value= INT
                            {
                            value=(DroolsTree)match(input,INT,FOLLOW_INT_in_rule_attribute872); 

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:195:39: value= VT_PAREN_CHUNK
                            {
                            value=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_rule_attribute876); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:196:4: ^(attrName= VK_NO_LOOP (value= BOOL )? )
                    {
                    attrName=(DroolsTree)match(input,VK_NO_LOOP,FOLLOW_VK_NO_LOOP_in_rule_attribute887); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // src/main/resources/org/drools/lang/DescrBuilderTree.g:196:31: (value= BOOL )?
                        int alt25=2;
                        int LA25_0 = input.LA(1);

                        if ( (LA25_0==BOOL) ) {
                            alt25=1;
                        }
                        switch (alt25) {
                            case 1 :
                                // src/main/resources/org/drools/lang/DescrBuilderTree.g:196:31: value= BOOL
                                {
                                value=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_rule_attribute891); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:197:4: ^(attrName= VK_AGENDA_GROUP value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_AGENDA_GROUP,FOLLOW_VK_AGENDA_GROUP_in_rule_attribute903); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute907); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:198:4: ^(attrName= VK_TIMER (value= INT | value= VT_PAREN_CHUNK ) )
                    {
                    attrName=(DroolsTree)match(input,VK_TIMER,FOLLOW_VK_TIMER_in_rule_attribute918); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:198:24: (value= INT | value= VT_PAREN_CHUNK )
                    int alt26=2;
                    int LA26_0 = input.LA(1);

                    if ( (LA26_0==INT) ) {
                        alt26=1;
                    }
                    else if ( (LA26_0==VT_PAREN_CHUNK) ) {
                        alt26=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 26, 0, input);

                        throw nvae;
                    }
                    switch (alt26) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:198:25: value= INT
                            {
                            value=(DroolsTree)match(input,INT,FOLLOW_INT_in_rule_attribute923); 

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:198:35: value= VT_PAREN_CHUNK
                            {
                            value=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_rule_attribute927); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:199:4: ^(attrName= VK_ACTIVATION_GROUP value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_ACTIVATION_GROUP,FOLLOW_VK_ACTIVATION_GROUP_in_rule_attribute940); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute944); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:200:4: ^(attrName= VK_AUTO_FOCUS (value= BOOL )? )
                    {
                    attrName=(DroolsTree)match(input,VK_AUTO_FOCUS,FOLLOW_VK_AUTO_FOCUS_in_rule_attribute954); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // src/main/resources/org/drools/lang/DescrBuilderTree.g:200:34: (value= BOOL )?
                        int alt27=2;
                        int LA27_0 = input.LA(1);

                        if ( (LA27_0==BOOL) ) {
                            alt27=1;
                        }
                        switch (alt27) {
                            case 1 :
                                // src/main/resources/org/drools/lang/DescrBuilderTree.g:200:34: value= BOOL
                                {
                                value=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_rule_attribute958); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:201:4: ^(attrName= VK_DATE_EFFECTIVE value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_DATE_EFFECTIVE,FOLLOW_VK_DATE_EFFECTIVE_in_rule_attribute969); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute973); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:202:4: ^(attrName= VK_DATE_EXPIRES value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_DATE_EXPIRES,FOLLOW_VK_DATE_EXPIRES_in_rule_attribute983); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute987); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:203:4: ^(attrName= VK_ENABLED (value= BOOL | value= VT_PAREN_CHUNK ) )
                    {
                    attrName=(DroolsTree)match(input,VK_ENABLED,FOLLOW_VK_ENABLED_in_rule_attribute997); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:203:26: (value= BOOL | value= VT_PAREN_CHUNK )
                    int alt28=2;
                    int LA28_0 = input.LA(1);

                    if ( (LA28_0==BOOL) ) {
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
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:203:27: value= BOOL
                            {
                            value=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_rule_attribute1002); 

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:203:38: value= VT_PAREN_CHUNK
                            {
                            value=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_rule_attribute1006); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 10 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:204:4: ^(attrName= VK_RULEFLOW_GROUP value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_RULEFLOW_GROUP,FOLLOW_VK_RULEFLOW_GROUP_in_rule_attribute1017); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute1021); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 11 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:205:4: ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? )
                    {
                    attrName=(DroolsTree)match(input,VK_LOCK_ON_ACTIVE,FOLLOW_VK_LOCK_ON_ACTIVE_in_rule_attribute1031); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // src/main/resources/org/drools/lang/DescrBuilderTree.g:205:38: (value= BOOL )?
                        int alt29=2;
                        int LA29_0 = input.LA(1);

                        if ( (LA29_0==BOOL) ) {
                            alt29=1;
                        }
                        switch (alt29) {
                            case 1 :
                                // src/main/resources/org/drools/lang/DescrBuilderTree.g:205:38: value= BOOL
                                {
                                value=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_rule_attribute1035); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 12 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:206:4: ^(attrName= VK_DIALECT value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_DIALECT,FOLLOW_VK_DIALECT_in_rule_attribute1045); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute1049); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 13 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:207:4: ^(attrName= VK_CALENDARS value= STRING )
                    {
                    attrName=(DroolsTree)match(input,VK_CALENDARS,FOLLOW_VK_CALENDARS_in_rule_attribute1058); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_rule_attribute1062); 

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:211:1: lhs_block returns [AndDescr andDescr] : ^( VT_AND_IMPLICIT (dt= lhs )* ) ;
    public final AndDescr lhs_block() throws RecognitionException {
        AndDescr andDescr = null;

        DescrBuilderTree.lhs_return dt = null;



        	andDescr = new AndDescr();

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:214:3: ( ^( VT_AND_IMPLICIT (dt= lhs )* ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:214:5: ^( VT_AND_IMPLICIT (dt= lhs )* )
            {
            match(input,VT_AND_IMPLICIT,FOLLOW_VT_AND_IMPLICIT_in_lhs_block1087); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // src/main/resources/org/drools/lang/DescrBuilderTree.g:214:23: (dt= lhs )*
                loop31:
                do {
                    int alt31=2;
                    int LA31_0 = input.LA(1);

                    if ( ((LA31_0>=VT_AND_PREFIX && LA31_0<=VT_OR_INFIX)||LA31_0==VT_PATTERN||LA31_0==VK_EVAL||LA31_0==VK_NOT||(LA31_0>=VK_EXISTS && LA31_0<=VK_FORALL)||LA31_0==FROM) ) {
                        alt31=1;
                    }


                    switch (alt31) {
                	case 1 :
                	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:214:24: dt= lhs
                	    {
                	    pushFollow(FOLLOW_lhs_in_lhs_block1092);
                	    dt=lhs();

                	    state._fsp--;

                	    andDescr.addDescr((dt!=null?dt.baseDescr:null));

                	    }
                	    break;

                	default :
                	    break loop31;
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:217:1: lhs returns [BaseDescr baseDescr] : ( ^(start= VT_OR_PREFIX (dt= lhs )+ ) | ^(start= VT_OR_INFIX dt1= lhs dt2= lhs ) | ^(start= VT_AND_PREFIX (dt= lhs )+ ) | ^(start= VT_AND_INFIX dt1= lhs dt2= lhs ) | ^(start= VK_EXISTS dt= lhs ) | ^(start= VK_NOT dt= lhs ) | ^(start= VK_EVAL pc= VT_PAREN_CHUNK ) | ^(start= VK_FORALL (dt= lhs )+ ) | ^( FROM pn= lhs_pattern fe= from_elements ) | pn= lhs_pattern );
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
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:220:3: ( ^(start= VT_OR_PREFIX (dt= lhs )+ ) | ^(start= VT_OR_INFIX dt1= lhs dt2= lhs ) | ^(start= VT_AND_PREFIX (dt= lhs )+ ) | ^(start= VT_AND_INFIX dt1= lhs dt2= lhs ) | ^(start= VK_EXISTS dt= lhs ) | ^(start= VK_NOT dt= lhs ) | ^(start= VK_EVAL pc= VT_PAREN_CHUNK ) | ^(start= VK_FORALL (dt= lhs )+ ) | ^( FROM pn= lhs_pattern fe= from_elements ) | pn= lhs_pattern )
            int alt35=10;
            switch ( input.LA(1) ) {
            case VT_OR_PREFIX:
                {
                alt35=1;
                }
                break;
            case VT_OR_INFIX:
                {
                alt35=2;
                }
                break;
            case VT_AND_PREFIX:
                {
                alt35=3;
                }
                break;
            case VT_AND_INFIX:
                {
                alt35=4;
                }
                break;
            case VK_EXISTS:
                {
                alt35=5;
                }
                break;
            case VK_NOT:
                {
                alt35=6;
                }
                break;
            case VK_EVAL:
                {
                alt35=7;
                }
                break;
            case VK_FORALL:
                {
                alt35=8;
                }
                break;
            case FROM:
                {
                alt35=9;
                }
                break;
            case VT_PATTERN:
                {
                alt35=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;
            }

            switch (alt35) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:220:5: ^(start= VT_OR_PREFIX (dt= lhs )+ )
                    {
                    start=(DroolsTree)match(input,VT_OR_PREFIX,FOLLOW_VT_OR_PREFIX_in_lhs1118); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:220:26: (dt= lhs )+
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
                    	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:220:27: dt= lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs1123);
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
                    	retval.baseDescr = factory.createOr(start, lhsList);	

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:222:4: ^(start= VT_OR_INFIX dt1= lhs dt2= lhs )
                    {
                    start=(DroolsTree)match(input,VT_OR_INFIX,FOLLOW_VT_OR_INFIX_in_lhs1139); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs1143);
                    dt1=lhs();

                    state._fsp--;

                    pushFollow(FOLLOW_lhs_in_lhs1147);
                    dt2=lhs();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	lhsList.add((dt1!=null?dt1.baseDescr:null));
                    		lhsList.add((dt2!=null?dt2.baseDescr:null));
                    		retval.baseDescr = factory.createOr(start, lhsList);	

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:226:4: ^(start= VT_AND_PREFIX (dt= lhs )+ )
                    {
                    start=(DroolsTree)match(input,VT_AND_PREFIX,FOLLOW_VT_AND_PREFIX_in_lhs1159); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:226:26: (dt= lhs )+
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
                    	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:226:27: dt= lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs1164);
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
                    	retval.baseDescr = factory.createAnd(start, lhsList);	

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:228:4: ^(start= VT_AND_INFIX dt1= lhs dt2= lhs )
                    {
                    start=(DroolsTree)match(input,VT_AND_INFIX,FOLLOW_VT_AND_INFIX_in_lhs1180); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs1184);
                    dt1=lhs();

                    state._fsp--;

                    pushFollow(FOLLOW_lhs_in_lhs1188);
                    dt2=lhs();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	lhsList.add((dt1!=null?dt1.baseDescr:null));
                    		lhsList.add((dt2!=null?dt2.baseDescr:null));
                    		retval.baseDescr = factory.createAnd(start, lhsList);	

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:232:4: ^(start= VK_EXISTS dt= lhs )
                    {
                    start=(DroolsTree)match(input,VK_EXISTS,FOLLOW_VK_EXISTS_in_lhs1200); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs1204);
                    dt=lhs();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createExists(start, (dt!=null?dt.baseDescr:null));	

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:234:4: ^(start= VK_NOT dt= lhs )
                    {
                    start=(DroolsTree)match(input,VK_NOT,FOLLOW_VK_NOT_in_lhs1216); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs1220);
                    dt=lhs();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createNot(start, (dt!=null?dt.baseDescr:null));	

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:236:4: ^(start= VK_EVAL pc= VT_PAREN_CHUNK )
                    {
                    start=(DroolsTree)match(input,VK_EVAL,FOLLOW_VK_EVAL_in_lhs1232); 

                    match(input, Token.DOWN, null); 
                    pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_lhs1236); 

                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createEval(start, pc);	

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:238:4: ^(start= VK_FORALL (dt= lhs )+ )
                    {
                    start=(DroolsTree)match(input,VK_FORALL,FOLLOW_VK_FORALL_in_lhs1248); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:238:22: (dt= lhs )+
                    int cnt34=0;
                    loop34:
                    do {
                        int alt34=2;
                        int LA34_0 = input.LA(1);

                        if ( ((LA34_0>=VT_AND_PREFIX && LA34_0<=VT_OR_INFIX)||LA34_0==VT_PATTERN||LA34_0==VK_EVAL||LA34_0==VK_NOT||(LA34_0>=VK_EXISTS && LA34_0<=VK_FORALL)||LA34_0==FROM) ) {
                            alt34=1;
                        }


                        switch (alt34) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:238:23: dt= lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs1253);
                    	    dt=lhs();

                    	    state._fsp--;

                    	    	lhsList.add((dt!=null?dt.baseDescr:null));	

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt34 >= 1 ) break loop34;
                                EarlyExitException eee =
                                    new EarlyExitException(34, input);
                                throw eee;
                        }
                        cnt34++;
                    } while (true);


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createForAll(start, lhsList);	

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:240:4: ^( FROM pn= lhs_pattern fe= from_elements )
                    {
                    match(input,FROM,FOLLOW_FROM_in_lhs1267); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_pattern_in_lhs1271);
                    pn=lhs_pattern();

                    state._fsp--;

                    pushFollow(FOLLOW_from_elements_in_lhs1275);
                    fe=from_elements();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.setupFrom(pn, (fe!=null?fe.patternSourceDescr:null));	

                    }
                    break;
                case 10 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:242:4: pn= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs1286);
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:246:1: from_elements returns [PatternSourceDescr patternSourceDescr] : ( ^(start= ACCUMULATE dt= lhs ret= accumulate_parts[$patternSourceDescr] ) | ^(start= COLLECT dt= lhs ) | ^(start= VK_ENTRY_POINT entryId= VT_ENTRYPOINT_ID ) | fs= from_source_clause );
    public final DescrBuilderTree.from_elements_return from_elements() throws RecognitionException {
        DescrBuilderTree.from_elements_return retval = new DescrBuilderTree.from_elements_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree entryId=null;
        DescrBuilderTree.lhs_return dt = null;

        AccumulateDescr ret = null;

        DescrBuilderTree.from_source_clause_return fs = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:247:2: ( ^(start= ACCUMULATE dt= lhs ret= accumulate_parts[$patternSourceDescr] ) | ^(start= COLLECT dt= lhs ) | ^(start= VK_ENTRY_POINT entryId= VT_ENTRYPOINT_ID ) | fs= from_source_clause )
            int alt36=4;
            switch ( input.LA(1) ) {
            case ACCUMULATE:
                {
                alt36=1;
                }
                break;
            case COLLECT:
                {
                alt36=2;
                }
                break;
            case VK_ENTRY_POINT:
                {
                alt36=3;
                }
                break;
            case VT_FROM_SOURCE:
                {
                alt36=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 36, 0, input);

                throw nvae;
            }

            switch (alt36) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:247:4: ^(start= ACCUMULATE dt= lhs ret= accumulate_parts[$patternSourceDescr] )
                    {
                    start=(DroolsTree)match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_from_elements1307); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_from_elements1311);
                    dt=lhs();

                    state._fsp--;

                    	retval.patternSourceDescr = factory.createAccumulate(start, (dt!=null?dt.baseDescr:null));	
                    pushFollow(FOLLOW_accumulate_parts_in_from_elements1321);
                    ret=accumulate_parts(retval.patternSourceDescr);

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.patternSourceDescr = ret;	

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:251:4: ^(start= COLLECT dt= lhs )
                    {
                    start=(DroolsTree)match(input,COLLECT,FOLLOW_COLLECT_in_from_elements1334); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_from_elements1338);
                    dt=lhs();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.patternSourceDescr = factory.createCollect(start, (dt!=null?dt.baseDescr:null));	

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:253:4: ^(start= VK_ENTRY_POINT entryId= VT_ENTRYPOINT_ID )
                    {
                    start=(DroolsTree)match(input,VK_ENTRY_POINT,FOLLOW_VK_ENTRY_POINT_in_from_elements1350); 

                    match(input, Token.DOWN, null); 
                    entryId=(DroolsTree)match(input,VT_ENTRYPOINT_ID,FOLLOW_VT_ENTRYPOINT_ID_in_from_elements1354); 

                    match(input, Token.UP, null); 
                    	retval.patternSourceDescr = factory.createEntryPoint(start, entryId);	

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:255:4: fs= from_source_clause
                    {
                    pushFollow(FOLLOW_from_source_clause_in_from_elements1365);
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:259:1: accumulate_parts[PatternSourceDescr patternSourceDescr] returns [AccumulateDescr accumulateDescr] : (ac1= accumulate_init_clause[$patternSourceDescr] | ac2= accumulate_id_clause[$patternSourceDescr] );
    public final AccumulateDescr accumulate_parts(PatternSourceDescr patternSourceDescr) throws RecognitionException {
        AccumulateDescr accumulateDescr = null;

        DescrBuilderTree.accumulate_init_clause_return ac1 = null;

        AccumulateDescr ac2 = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:260:2: (ac1= accumulate_init_clause[$patternSourceDescr] | ac2= accumulate_id_clause[$patternSourceDescr] )
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==VT_ACCUMULATE_INIT_CLAUSE) ) {
                alt37=1;
            }
            else if ( (LA37_0==VT_ACCUMULATE_ID_CLAUSE) ) {
                alt37=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, input);

                throw nvae;
            }
            switch (alt37) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:260:4: ac1= accumulate_init_clause[$patternSourceDescr]
                    {
                    pushFollow(FOLLOW_accumulate_init_clause_in_accumulate_parts1386);
                    ac1=accumulate_init_clause(patternSourceDescr);

                    state._fsp--;

                    	accumulateDescr = (ac1!=null?ac1.accumulateDescr:null);	

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:262:4: ac2= accumulate_id_clause[$patternSourceDescr]
                    {
                    pushFollow(FOLLOW_accumulate_id_clause_in_accumulate_parts1397);
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:266:1: accumulate_init_clause[PatternSourceDescr accumulateParam] returns [AccumulateDescr accumulateDescr] : ^( VT_ACCUMULATE_INIT_CLAUSE ^(start= VK_INIT pc1= VT_PAREN_CHUNK ) ^( VK_ACTION pc2= VT_PAREN_CHUNK ) (rev= accumulate_init_reverse_clause )? ^( VK_RESULT pc3= VT_PAREN_CHUNK ) ) ;
    public final DescrBuilderTree.accumulate_init_clause_return accumulate_init_clause(PatternSourceDescr accumulateParam) throws RecognitionException {
        DescrBuilderTree.accumulate_init_clause_return retval = new DescrBuilderTree.accumulate_init_clause_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree pc1=null;
        DroolsTree pc2=null;
        DroolsTree pc3=null;
        DescrBuilderTree.accumulate_init_reverse_clause_return rev = null;


        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:267:2: ( ^( VT_ACCUMULATE_INIT_CLAUSE ^(start= VK_INIT pc1= VT_PAREN_CHUNK ) ^( VK_ACTION pc2= VT_PAREN_CHUNK ) (rev= accumulate_init_reverse_clause )? ^( VK_RESULT pc3= VT_PAREN_CHUNK ) ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:267:4: ^( VT_ACCUMULATE_INIT_CLAUSE ^(start= VK_INIT pc1= VT_PAREN_CHUNK ) ^( VK_ACTION pc2= VT_PAREN_CHUNK ) (rev= accumulate_init_reverse_clause )? ^( VK_RESULT pc3= VT_PAREN_CHUNK ) )
            {
            match(input,VT_ACCUMULATE_INIT_CLAUSE,FOLLOW_VT_ACCUMULATE_INIT_CLAUSE_in_accumulate_init_clause1420); 

            match(input, Token.DOWN, null); 
            start=(DroolsTree)match(input,VK_INIT,FOLLOW_VK_INIT_in_accumulate_init_clause1429); 

            match(input, Token.DOWN, null); 
            pc1=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1433); 

            match(input, Token.UP, null); 
            match(input,VK_ACTION,FOLLOW_VK_ACTION_in_accumulate_init_clause1441); 

            match(input, Token.DOWN, null); 
            pc2=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1445); 

            match(input, Token.UP, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:270:7: (rev= accumulate_init_reverse_clause )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==VK_REVERSE) ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:270:7: rev= accumulate_init_reverse_clause
                    {
                    pushFollow(FOLLOW_accumulate_init_reverse_clause_in_accumulate_init_clause1454);
                    rev=accumulate_init_reverse_clause();

                    state._fsp--;


                    }
                    break;

            }

            match(input,VK_RESULT,FOLLOW_VK_RESULT_in_accumulate_init_clause1461); 

            match(input, Token.DOWN, null); 
            pc3=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1465); 

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:279:1: accumulate_init_reverse_clause returns [DroolsTree vkReverse, DroolsTree vkReverseChunk] : ^(vk= VK_REVERSE pc= VT_PAREN_CHUNK ) ;
    public final DescrBuilderTree.accumulate_init_reverse_clause_return accumulate_init_reverse_clause() throws RecognitionException {
        DescrBuilderTree.accumulate_init_reverse_clause_return retval = new DescrBuilderTree.accumulate_init_reverse_clause_return();
        retval.start = input.LT(1);

        DroolsTree vk=null;
        DroolsTree pc=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:280:2: ( ^(vk= VK_REVERSE pc= VT_PAREN_CHUNK ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:280:4: ^(vk= VK_REVERSE pc= VT_PAREN_CHUNK )
            {
            vk=(DroolsTree)match(input,VK_REVERSE,FOLLOW_VK_REVERSE_in_accumulate_init_reverse_clause1488); 

            match(input, Token.DOWN, null); 
            pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_reverse_clause1492); 

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:285:1: accumulate_id_clause[PatternSourceDescr accumulateParam] returns [AccumulateDescr accumulateDescr] : ^( VT_ACCUMULATE_ID_CLAUSE id= ID pc= VT_PAREN_CHUNK ) ;
    public final AccumulateDescr accumulate_id_clause(PatternSourceDescr accumulateParam) throws RecognitionException {
        AccumulateDescr accumulateDescr = null;

        DroolsTree id=null;
        DroolsTree pc=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:286:2: ( ^( VT_ACCUMULATE_ID_CLAUSE id= ID pc= VT_PAREN_CHUNK ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:286:4: ^( VT_ACCUMULATE_ID_CLAUSE id= ID pc= VT_PAREN_CHUNK )
            {
            match(input,VT_ACCUMULATE_ID_CLAUSE,FOLLOW_VT_ACCUMULATE_ID_CLAUSE_in_accumulate_id_clause1514); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_accumulate_id_clause1518); 
            pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_id_clause1522); 

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:290:1: from_source_clause returns [FromDescr fromDescr, AccessorDescr retAccessorDescr] : ^( VT_FROM_SOURCE id= ID (pc= VT_PAREN_CHUNK )? ( expression_chain )? ) ;
    public final DescrBuilderTree.from_source_clause_return from_source_clause() throws RecognitionException {
        from_source_clause_stack.push(new from_source_clause_scope());
        DescrBuilderTree.from_source_clause_return retval = new DescrBuilderTree.from_source_clause_return();
        retval.start = input.LT(1);

        DroolsTree id=null;
        DroolsTree pc=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:293:3: ( ^( VT_FROM_SOURCE id= ID (pc= VT_PAREN_CHUNK )? ( expression_chain )? ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:293:5: ^( VT_FROM_SOURCE id= ID (pc= VT_PAREN_CHUNK )? ( expression_chain )? )
            {
            match(input,VT_FROM_SOURCE,FOLLOW_VT_FROM_SOURCE_in_from_source_clause1544); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_from_source_clause1548); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:293:30: (pc= VT_PAREN_CHUNK )?
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==VT_PAREN_CHUNK) ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:293:30: pc= VT_PAREN_CHUNK
                    {
                    pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_from_source_clause1552); 

                    }
                    break;

            }

            	((from_source_clause_scope)from_source_clause_stack.peek()).accessorDescr = factory.createAccessor(id, pc);	
            		retval.retAccessorDescr = ((from_source_clause_scope)from_source_clause_stack.peek()).accessorDescr;	
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:296:3: ( expression_chain )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==VT_EXPRESSION_CHAIN) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:296:3: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source_clause1561);
                    expression_chain();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 
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
            start=(DroolsTree)match(input,VT_EXPRESSION_CHAIN,FOLLOW_VT_EXPRESSION_CHAIN_in_expression_chain1580); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_expression_chain1584); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:301:40: (sc= VT_SQUARE_CHUNK )?
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==VT_SQUARE_CHUNK) ) {
                alt41=1;
            }
            switch (alt41) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:301:40: sc= VT_SQUARE_CHUNK
                    {
                    sc=(DroolsTree)match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_expression_chain1588); 

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:301:60: (pc= VT_PAREN_CHUNK )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==VT_PAREN_CHUNK) ) {
                alt42=1;
            }
            switch (alt42) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:301:60: pc= VT_PAREN_CHUNK
                    {
                    pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_expression_chain1593); 

                    }
                    break;

            }

            	DeclarativeInvokerDescr declarativeInvokerResult = factory.createExpressionChain(start, id, sc, pc);	
            		((from_source_clause_scope)from_source_clause_stack.peek()).accessorDescr.addInvoker(declarativeInvokerResult);	
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:304:3: ( expression_chain )?
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==VT_EXPRESSION_CHAIN) ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:304:3: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain1601);
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
            match(input,VT_PATTERN,FOLLOW_VT_PATTERN_in_lhs_pattern1619); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_fact_expression_in_lhs_pattern1623);
            fe=fact_expression();

            state._fsp--;


            match(input, Token.UP, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:308:39: (oc= over_clause )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==OVER) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:308:39: oc= over_clause
                    {
                    pushFollow(FOLLOW_over_clause_in_lhs_pattern1628);
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
            match(input,OVER,FOLLOW_OVER_in_over_clause1653); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:314:11: (oe= over_element )+
            int cnt45=0;
            loop45:
            do {
                int alt45=2;
                int LA45_0 = input.LA(1);

                if ( (LA45_0==VT_BEHAVIOR) ) {
                    alt45=1;
                }


                switch (alt45) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:314:12: oe= over_element
            	    {
            	    pushFollow(FOLLOW_over_element_in_over_clause1658);
            	    oe=over_element();

            	    state._fsp--;

            	    behaviorList.add(oe);

            	    }
            	    break;

            	default :
            	    if ( cnt45 >= 1 ) break loop45;
                        EarlyExitException eee =
                            new EarlyExitException(45, input);
                        throw eee;
                }
                cnt45++;
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
            match(input,VT_BEHAVIOR,FOLLOW_VT_BEHAVIOR_in_over_element1679); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_over_element1681); 
            id2=(DroolsTree)match(input,ID,FOLLOW_ID_in_over_element1685); 
            pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_over_element1689); 

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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:322:1: fact_expression returns [BaseDescr descr] : ( ^( VT_FACT pt= pattern_type (fe= fact_expression )* ) | ^( VT_FACT_BINDING label= VT_LABEL fact= fact_expression ) | ^(start= VT_FACT_OR left= fact_expression right= fact_expression ) | ^( VT_FIELD field= field_element (fe= fact_expression )? ) | ^( VT_BIND_FIELD label= VT_LABEL fe= fact_expression ) | ^( VK_EVAL pc= VT_PAREN_CHUNK ) | ^(op= EQUAL fe= fact_expression ) | ^(op= NOT_EQUAL fe= fact_expression ) | ^(op= GREATER fe= fact_expression ) | ^(op= GREATER_EQUAL fe= fact_expression ) | ^(op= LESS fe= fact_expression ) | ^(op= LESS_EQUAL fe= fact_expression ) | ^(op= VK_OPERATOR (not= VK_NOT )? (param= VT_SQUARE_CHUNK )? fe= fact_expression ) | ^( VK_IN (not= VK_NOT )? (fe= fact_expression )+ ) | ^( DOUBLE_PIPE left= fact_expression right= fact_expression ) | ^( DOUBLE_AMPER left= fact_expression right= fact_expression ) | ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) | s= STRING | i= INT | f= FLOAT | b= BOOL | n= NULL | pc= VT_PAREN_CHUNK );
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
        DroolsTree i=null;
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
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:325:3: ( ^( VT_FACT pt= pattern_type (fe= fact_expression )* ) | ^( VT_FACT_BINDING label= VT_LABEL fact= fact_expression ) | ^(start= VT_FACT_OR left= fact_expression right= fact_expression ) | ^( VT_FIELD field= field_element (fe= fact_expression )? ) | ^( VT_BIND_FIELD label= VT_LABEL fe= fact_expression ) | ^( VK_EVAL pc= VT_PAREN_CHUNK ) | ^(op= EQUAL fe= fact_expression ) | ^(op= NOT_EQUAL fe= fact_expression ) | ^(op= GREATER fe= fact_expression ) | ^(op= GREATER_EQUAL fe= fact_expression ) | ^(op= LESS fe= fact_expression ) | ^(op= LESS_EQUAL fe= fact_expression ) | ^(op= VK_OPERATOR (not= VK_NOT )? (param= VT_SQUARE_CHUNK )? fe= fact_expression ) | ^( VK_IN (not= VK_NOT )? (fe= fact_expression )+ ) | ^( DOUBLE_PIPE left= fact_expression right= fact_expression ) | ^( DOUBLE_AMPER left= fact_expression right= fact_expression ) | ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) | s= STRING | i= INT | f= FLOAT | b= BOOL | n= NULL | pc= VT_PAREN_CHUNK )
            int alt53=23;
            switch ( input.LA(1) ) {
            case VT_FACT:
                {
                alt53=1;
                }
                break;
            case VT_FACT_BINDING:
                {
                alt53=2;
                }
                break;
            case VT_FACT_OR:
                {
                alt53=3;
                }
                break;
            case VT_FIELD:
                {
                alt53=4;
                }
                break;
            case VT_BIND_FIELD:
                {
                alt53=5;
                }
                break;
            case VK_EVAL:
                {
                alt53=6;
                }
                break;
            case EQUAL:
                {
                alt53=7;
                }
                break;
            case NOT_EQUAL:
                {
                alt53=8;
                }
                break;
            case GREATER:
                {
                alt53=9;
                }
                break;
            case GREATER_EQUAL:
                {
                alt53=10;
                }
                break;
            case LESS:
                {
                alt53=11;
                }
                break;
            case LESS_EQUAL:
                {
                alt53=12;
                }
                break;
            case VK_OPERATOR:
                {
                alt53=13;
                }
                break;
            case VK_IN:
                {
                alt53=14;
                }
                break;
            case DOUBLE_PIPE:
                {
                alt53=15;
                }
                break;
            case DOUBLE_AMPER:
                {
                alt53=16;
                }
                break;
            case VT_ACCESSOR_PATH:
                {
                alt53=17;
                }
                break;
            case STRING:
                {
                alt53=18;
                }
                break;
            case INT:
                {
                alt53=19;
                }
                break;
            case FLOAT:
                {
                alt53=20;
                }
                break;
            case BOOL:
                {
                alt53=21;
                }
                break;
            case NULL:
                {
                alt53=22;
                }
                break;
            case VT_PAREN_CHUNK:
                {
                alt53=23;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 53, 0, input);

                throw nvae;
            }

            switch (alt53) {
                case 1 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:325:5: ^( VT_FACT pt= pattern_type (fe= fact_expression )* )
                    {
                    match(input,VT_FACT,FOLLOW_VT_FACT_in_fact_expression1712); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_pattern_type_in_fact_expression1716);
                    pt=pattern_type();

                    state._fsp--;

                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:325:31: (fe= fact_expression )*
                    loop46:
                    do {
                        int alt46=2;
                        int LA46_0 = input.LA(1);

                        if ( (LA46_0==VT_FACT||LA46_0==VT_PAREN_CHUNK||(LA46_0>=VT_FACT_BINDING && LA46_0<=VT_ACCESSOR_PATH)||LA46_0==VK_EVAL||LA46_0==VK_IN||LA46_0==VK_OPERATOR||LA46_0==STRING||(LA46_0>=BOOL && LA46_0<=DOUBLE_AMPER)||(LA46_0>=EQUAL && LA46_0<=NULL)) ) {
                            alt46=1;
                        }


                        switch (alt46) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:325:32: fe= fact_expression
                    	    {
                    	    pushFollow(FOLLOW_fact_expression_in_fact_expression1721);
                    	    fe=fact_expression();

                    	    state._fsp--;

                    	    exprList.add((fe!=null?fe.descr:null));

                    	    }
                    	    break;

                    	default :
                    	    break loop46;
                        }
                    } while (true);


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createPattern(pt, exprList);	

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:327:4: ^( VT_FACT_BINDING label= VT_LABEL fact= fact_expression )
                    {
                    match(input,VT_FACT_BINDING,FOLLOW_VT_FACT_BINDING_in_fact_expression1735); 

                    match(input, Token.DOWN, null); 
                    label=(DroolsTree)match(input,VT_LABEL,FOLLOW_VT_LABEL_in_fact_expression1739); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1743);
                    fact=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupPatternBiding(label, (fact!=null?fact.descr:null));	

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:329:4: ^(start= VT_FACT_OR left= fact_expression right= fact_expression )
                    {
                    start=(DroolsTree)match(input,VT_FACT_OR,FOLLOW_VT_FACT_OR_in_fact_expression1755); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1759);
                    left=fact_expression();

                    state._fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1763);
                    right=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createFactOr(start, (left!=null?left.descr:null), (right!=null?right.descr:null));	

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:332:4: ^( VT_FIELD field= field_element (fe= fact_expression )? )
                    {
                    match(input,VT_FIELD,FOLLOW_VT_FIELD_in_fact_expression1774); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_field_element_in_fact_expression1778);
                    field=field_element();

                    state._fsp--;

                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:332:37: (fe= fact_expression )?
                    int alt47=2;
                    int LA47_0 = input.LA(1);

                    if ( (LA47_0==VT_FACT||LA47_0==VT_PAREN_CHUNK||(LA47_0>=VT_FACT_BINDING && LA47_0<=VT_ACCESSOR_PATH)||LA47_0==VK_EVAL||LA47_0==VK_IN||LA47_0==VK_OPERATOR||LA47_0==STRING||(LA47_0>=BOOL && LA47_0<=DOUBLE_AMPER)||(LA47_0>=EQUAL && LA47_0<=NULL)) ) {
                        alt47=1;
                    }
                    switch (alt47) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:332:37: fe= fact_expression
                            {
                            pushFollow(FOLLOW_fact_expression_in_fact_expression1782);
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
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:338:4: ^( VT_BIND_FIELD label= VT_LABEL fe= fact_expression )
                    {
                    match(input,VT_BIND_FIELD,FOLLOW_VT_BIND_FIELD_in_fact_expression1793); 

                    match(input, Token.DOWN, null); 
                    label=(DroolsTree)match(input,VT_LABEL,FOLLOW_VT_LABEL_in_fact_expression1797); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1801);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createFieldBinding(label, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:341:4: ^( VK_EVAL pc= VT_PAREN_CHUNK )
                    {
                    match(input,VK_EVAL,FOLLOW_VK_EVAL_in_fact_expression1812); 

                    match(input, Token.DOWN, null); 
                    pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_fact_expression1816); 

                    match(input, Token.UP, null); 
                    	retval.descr = factory.createPredicate(pc);	

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:344:4: ^(op= EQUAL fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,EQUAL,FOLLOW_EQUAL_in_fact_expression1829); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1833);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:346:4: ^(op= NOT_EQUAL fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,NOT_EQUAL,FOLLOW_NOT_EQUAL_in_fact_expression1845); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1849);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:348:4: ^(op= GREATER fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,GREATER,FOLLOW_GREATER_in_fact_expression1861); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1865);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 10 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:350:4: ^(op= GREATER_EQUAL fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,GREATER_EQUAL,FOLLOW_GREATER_EQUAL_in_fact_expression1877); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1881);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 11 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:352:4: ^(op= LESS fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,LESS,FOLLOW_LESS_in_fact_expression1893); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1897);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 12 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:354:4: ^(op= LESS_EQUAL fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,LESS_EQUAL,FOLLOW_LESS_EQUAL_in_fact_expression1909); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1913);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, (fe!=null?fe.descr:null));	

                    }
                    break;
                case 13 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:356:4: ^(op= VK_OPERATOR (not= VK_NOT )? (param= VT_SQUARE_CHUNK )? fe= fact_expression )
                    {
                    op=(DroolsTree)match(input,VK_OPERATOR,FOLLOW_VK_OPERATOR_in_fact_expression1925); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:356:24: (not= VK_NOT )?
                    int alt48=2;
                    int LA48_0 = input.LA(1);

                    if ( (LA48_0==VK_NOT) ) {
                        alt48=1;
                    }
                    switch (alt48) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:356:24: not= VK_NOT
                            {
                            not=(DroolsTree)match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1929); 

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:356:38: (param= VT_SQUARE_CHUNK )?
                    int alt49=2;
                    int LA49_0 = input.LA(1);

                    if ( (LA49_0==VT_SQUARE_CHUNK) ) {
                        alt49=1;
                    }
                    switch (alt49) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:356:38: param= VT_SQUARE_CHUNK
                            {
                            param=(DroolsTree)match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_fact_expression1934); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1939);
                    fe=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, not, (fe!=null?fe.descr:null), param);	

                    }
                    break;
                case 14 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:359:4: ^( VK_IN (not= VK_NOT )? (fe= fact_expression )+ )
                    {
                    match(input,VK_IN,FOLLOW_VK_IN_in_fact_expression1950); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:359:15: (not= VK_NOT )?
                    int alt50=2;
                    int LA50_0 = input.LA(1);

                    if ( (LA50_0==VK_NOT) ) {
                        alt50=1;
                    }
                    switch (alt50) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DescrBuilderTree.g:359:15: not= VK_NOT
                            {
                            not=(DroolsTree)match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1954); 

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:359:24: (fe= fact_expression )+
                    int cnt51=0;
                    loop51:
                    do {
                        int alt51=2;
                        int LA51_0 = input.LA(1);

                        if ( (LA51_0==VT_FACT||LA51_0==VT_PAREN_CHUNK||(LA51_0>=VT_FACT_BINDING && LA51_0<=VT_ACCESSOR_PATH)||LA51_0==VK_EVAL||LA51_0==VK_IN||LA51_0==VK_OPERATOR||LA51_0==STRING||(LA51_0>=BOOL && LA51_0<=DOUBLE_AMPER)||(LA51_0>=EQUAL && LA51_0<=NULL)) ) {
                            alt51=1;
                        }


                        switch (alt51) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:359:25: fe= fact_expression
                    	    {
                    	    pushFollow(FOLLOW_fact_expression_in_fact_expression1960);
                    	    fe=fact_expression();

                    	    state._fsp--;

                    	    exprList.add((fe!=null?fe.descr:null));

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt51 >= 1 ) break loop51;
                                EarlyExitException eee =
                                    new EarlyExitException(51, input);
                                throw eee;
                        }
                        cnt51++;
                    } while (true);


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createRestrictionConnective(not, exprList);	

                    }
                    break;
                case 15 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:362:4: ^( DOUBLE_PIPE left= fact_expression right= fact_expression )
                    {
                    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_fact_expression1975); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1979);
                    left=fact_expression();

                    state._fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1983);
                    right=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createOrRestrictionConnective((left!=null?left.descr:null), (right!=null?right.descr:null));	

                    }
                    break;
                case 16 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:364:4: ^( DOUBLE_AMPER left= fact_expression right= fact_expression )
                    {
                    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_fact_expression1993); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1997);
                    left=fact_expression();

                    state._fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression2001);
                    right=fact_expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createAndRestrictionConnective((left!=null?left.descr:null), (right!=null?right.descr:null));	

                    }
                    break;
                case 17 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:367:4: ^( VT_ACCESSOR_PATH (ae= accessor_element )+ )
                    {
                    match(input,VT_ACCESSOR_PATH,FOLLOW_VT_ACCESSOR_PATH_in_fact_expression2012); 

                    match(input, Token.DOWN, null); 
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:367:23: (ae= accessor_element )+
                    int cnt52=0;
                    loop52:
                    do {
                        int alt52=2;
                        int LA52_0 = input.LA(1);

                        if ( (LA52_0==VT_ACCESSOR_ELEMENT) ) {
                            alt52=1;
                        }


                        switch (alt52) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:367:24: ae= accessor_element
                    	    {
                    	    pushFollow(FOLLOW_accessor_element_in_fact_expression2017);
                    	    ae=accessor_element();

                    	    state._fsp--;

                    	    exprList.add(ae);

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
                    	retval.descr = factory.createAccessorPath(exprList);	

                    }
                    break;
                case 18 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:369:4: s= STRING
                    {
                    s=(DroolsTree)match(input,STRING,FOLLOW_STRING_in_fact_expression2032); 
                    	retval.descr = factory.createStringLiteralRestriction(s);	

                    }
                    break;
                case 19 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:371:4: i= INT
                    {
                    i=(DroolsTree)match(input,INT,FOLLOW_INT_in_fact_expression2042); 
                    	retval.descr = factory.createIntLiteralRestriction(i);	

                    }
                    break;
                case 20 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:373:4: f= FLOAT
                    {
                    f=(DroolsTree)match(input,FLOAT,FOLLOW_FLOAT_in_fact_expression2052); 
                    	retval.descr = factory.createFloatLiteralRestriction(f);	

                    }
                    break;
                case 21 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:375:4: b= BOOL
                    {
                    b=(DroolsTree)match(input,BOOL,FOLLOW_BOOL_in_fact_expression2062); 
                    	retval.descr = factory.createBoolLiteralRestriction(b);	

                    }
                    break;
                case 22 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:377:4: n= NULL
                    {
                    n=(DroolsTree)match(input,NULL,FOLLOW_NULL_in_fact_expression2072); 
                    	retval.descr = factory.createNullLiteralRestriction(n);	

                    }
                    break;
                case 23 :
                    // src/main/resources/org/drools/lang/DescrBuilderTree.g:379:4: pc= VT_PAREN_CHUNK
                    {
                    pc=(DroolsTree)match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_fact_expression2082); 
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:383:1: field_element returns [FieldConstraintDescr element] : ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) ;
    public final FieldConstraintDescr field_element() throws RecognitionException {
        FieldConstraintDescr element = null;

        BaseDescr ae = null;



        	List<BaseDescr> aeList = new LinkedList<BaseDescr>();

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:386:3: ( ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:386:5: ^( VT_ACCESSOR_PATH (ae= accessor_element )+ )
            {
            match(input,VT_ACCESSOR_PATH,FOLLOW_VT_ACCESSOR_PATH_in_field_element2104); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:386:24: (ae= accessor_element )+
            int cnt54=0;
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);

                if ( (LA54_0==VT_ACCESSOR_ELEMENT) ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:386:25: ae= accessor_element
            	    {
            	    pushFollow(FOLLOW_accessor_element_in_field_element2109);
            	    ae=accessor_element();

            	    state._fsp--;

            	    aeList.add(ae);

            	    }
            	    break;

            	default :
            	    if ( cnt54 >= 1 ) break loop54;
                        EarlyExitException eee =
                            new EarlyExitException(54, input);
                        throw eee;
                }
                cnt54++;
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:390:1: accessor_element returns [BaseDescr element] : ^( VT_ACCESSOR_ELEMENT id= ID (sc+= VT_SQUARE_CHUNK )* ) ;
    public final BaseDescr accessor_element() throws RecognitionException {
        BaseDescr element = null;

        DroolsTree id=null;
        DroolsTree sc=null;
        List list_sc=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:391:2: ( ^( VT_ACCESSOR_ELEMENT id= ID (sc+= VT_SQUARE_CHUNK )* ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:391:4: ^( VT_ACCESSOR_ELEMENT id= ID (sc+= VT_SQUARE_CHUNK )* )
            {
            match(input,VT_ACCESSOR_ELEMENT,FOLLOW_VT_ACCESSOR_ELEMENT_in_accessor_element2133); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)match(input,ID,FOLLOW_ID_in_accessor_element2137); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:391:34: (sc+= VT_SQUARE_CHUNK )*
            loop55:
            do {
                int alt55=2;
                int LA55_0 = input.LA(1);

                if ( (LA55_0==VT_SQUARE_CHUNK) ) {
                    alt55=1;
                }


                switch (alt55) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:391:34: sc+= VT_SQUARE_CHUNK
            	    {
            	    sc=(DroolsTree)match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_accessor_element2141); 
            	    if (list_sc==null) list_sc=new ArrayList();
            	    list_sc.add(sc);


            	    }
            	    break;

            	default :
            	    break loop55;
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:395:1: pattern_type returns [BaseDescr dataType] : ^( VT_PATTERN_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) ;
    public final BaseDescr pattern_type() throws RecognitionException {
        BaseDescr dataType = null;

        DroolsTree idList=null;
        DroolsTree rightList=null;
        List list_idList=null;
        List list_rightList=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:396:2: ( ^( VT_PATTERN_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:396:4: ^( VT_PATTERN_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* )
            {
            match(input,VT_PATTERN_TYPE,FOLLOW_VT_PATTERN_TYPE_in_pattern_type2162); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:396:28: (idList+= ID )+
            int cnt56=0;
            loop56:
            do {
                int alt56=2;
                int LA56_0 = input.LA(1);

                if ( (LA56_0==ID) ) {
                    alt56=1;
                }


                switch (alt56) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:396:28: idList+= ID
            	    {
            	    idList=(DroolsTree)match(input,ID,FOLLOW_ID_in_pattern_type2166); 
            	    if (list_idList==null) list_idList=new ArrayList();
            	    list_idList.add(idList);


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

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:396:34: ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            loop57:
            do {
                int alt57=2;
                int LA57_0 = input.LA(1);

                if ( (LA57_0==LEFT_SQUARE) ) {
                    alt57=1;
                }


                switch (alt57) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:396:35: LEFT_SQUARE rightList+= RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_pattern_type2170); 
            	    rightList=(DroolsTree)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_pattern_type2174); 
            	    if (list_rightList==null) list_rightList=new ArrayList();
            	    list_rightList.add(rightList);


            	    }
            	    break;

            	default :
            	    break loop57;
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
    // src/main/resources/org/drools/lang/DescrBuilderTree.g:400:1: data_type returns [BaseDescr dataType] : ^( VT_DATA_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) ;
    public final BaseDescr data_type() throws RecognitionException {
        BaseDescr dataType = null;

        DroolsTree idList=null;
        DroolsTree rightList=null;
        List list_idList=null;
        List list_rightList=null;

        try {
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:401:2: ( ^( VT_DATA_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) )
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:401:4: ^( VT_DATA_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* )
            {
            match(input,VT_DATA_TYPE,FOLLOW_VT_DATA_TYPE_in_data_type2196); 

            match(input, Token.DOWN, null); 
            // src/main/resources/org/drools/lang/DescrBuilderTree.g:401:25: (idList+= ID )+
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
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:401:25: idList+= ID
            	    {
            	    idList=(DroolsTree)match(input,ID,FOLLOW_ID_in_data_type2200); 
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

            // src/main/resources/org/drools/lang/DescrBuilderTree.g:401:31: ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( (LA59_0==LEFT_SQUARE) ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DescrBuilderTree.g:401:32: LEFT_SQUARE rightList+= RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_data_type2204); 
            	    rightList=(DroolsTree)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_data_type2208); 
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
    // $ANTLR end "data_type"

    // Delegated rules


 

    public static final BitSet FOLLOW_VT_COMPILATION_UNIT_in_compilation_unit49 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_package_statement_in_compilation_unit51 = new BitSet(new long[]{0xABFFE00000000028L,0x000000000000000FL});
    public static final BitSet FOLLOW_statement_in_compilation_unit53 = new BitSet(new long[]{0xABFFE00000000028L,0x000000000000000FL});
    public static final BitSet FOLLOW_VK_PACKAGE_in_package_statement71 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_package_id_in_package_statement75 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PACKAGE_ID_in_package_id102 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_package_id106 = new BitSet(new long[]{0x0000000000000008L,0x0000000000080000L});
    public static final BitSet FOLLOW_rule_attribute_in_statement124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_declaration_in_statement205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VK_IMPORT_in_import_statement226 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_import_name_in_import_statement230 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FUNCTION_IMPORT_in_function_import_statement252 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_FUNCTION_in_function_import_statement254 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement258 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_IMPORT_ID_in_import_name277 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_import_name281 = new BitSet(new long[]{0x0000000000000008L,0x0000000000280000L});
    public static final BitSet FOLLOW_DOT_STAR_in_import_name286 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_GLOBAL_in_global309 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_global313 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_VT_GLOBAL_ID_in_global317 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_FUNCTION_in_function339 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_function343 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_VT_FUNCTION_ID_in_function348 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_parameters_in_function352 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_VT_CURLY_CHUNK_in_function356 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_TEMPLATE_in_template381 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_TEMPLATE_ID_in_template385 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_template_slot_in_template394 = new BitSet(new long[]{0x0000000000008000L,0x0000000000010000L});
    public static final BitSet FOLLOW_VK_END_in_template402 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_SLOT_in_template_slot422 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_template_slot426 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_VT_SLOT_ID_in_template_slot430 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_QUERY_in_query452 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_QUERY_ID_in_query456 = new BitSet(new long[]{0x0000100000400000L});
    public static final BitSet FOLLOW_parameters_in_query460 = new BitSet(new long[]{0x0000100000400000L});
    public static final BitSet FOLLOW_lhs_block_in_query465 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_VK_END_in_query469 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_RULE_in_rule496 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_RULE_ID_in_rule500 = new BitSet(new long[]{0x1000000000030000L,0x0000000024000000L});
    public static final BitSet FOLLOW_VK_EXTEND_in_rule505 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_RULE_ID_in_rule509 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_decl_metadata_in_rule519 = new BitSet(new long[]{0x0000000000030000L,0x0000000024000000L});
    public static final BitSet FOLLOW_rule_attributes_in_rule530 = new BitSet(new long[]{0x0000000000020000L,0x0000000020000000L});
    public static final BitSet FOLLOW_when_part_in_rule539 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_VT_RHS_CHUNK_in_rule544 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHEN_in_when_part563 = new BitSet(new long[]{0x0000100000400000L});
    public static final BitSet FOLLOW_lhs_block_in_when_part567 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_RULE_ATTRIBUTES_in_rule_attributes589 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_ATTRIBUTES_in_rule_attributes591 = new BitSet(new long[]{0x03FFE00000000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes597 = new BitSet(new long[]{0x03FFE00000000008L});
    public static final BitSet FOLLOW_VT_PARAM_LIST_in_parameters621 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_param_definition_in_parameters626 = new BitSet(new long[]{0x0000004000000008L,0x0000000000080000L});
    public static final BitSet FOLLOW_data_type_in_param_definition648 = new BitSet(new long[]{0x0000004000000008L,0x0000000000080000L});
    public static final BitSet FOLLOW_argument_in_param_definition653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_argument673 = new BitSet(new long[]{0x0000000000000002L,0x0000800000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument676 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument680 = new BitSet(new long[]{0x0000000000000002L,0x0000800000000000L});
    public static final BitSet FOLLOW_VK_DECLARE_in_type_declaration706 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_TYPE_DECLARE_ID_in_type_declaration710 = new BitSet(new long[]{0x0000000000000000L,0x0000000004090000L});
    public static final BitSet FOLLOW_decl_metadata_in_type_declaration719 = new BitSet(new long[]{0x0000000000000000L,0x0000000004090000L});
    public static final BitSet FOLLOW_decl_field_in_type_declaration732 = new BitSet(new long[]{0x0000000000000000L,0x0000000000090000L});
    public static final BitSet FOLLOW_VK_END_in_type_declaration738 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AT_in_decl_metadata763 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_decl_metadata767 = new BitSet(new long[]{0x0000000000100008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_decl_metadata771 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_decl_field799 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_decl_field_initialization_in_decl_field803 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_data_type_in_decl_field808 = new BitSet(new long[]{0x0000000000000008L,0x0000000004000000L});
    public static final BitSet FOLLOW_decl_metadata_in_decl_field813 = new BitSet(new long[]{0x0000000000000008L,0x0000000004000000L});
    public static final BitSet FOLLOW_EQUALS_in_decl_field_initialization840 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_decl_field_initialization844 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_SALIENCE_in_rule_attribute867 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INT_in_rule_attribute872 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_rule_attribute876 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_NO_LOOP_in_rule_attribute887 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute891 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_AGENDA_GROUP_in_rule_attribute903 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute907 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_TIMER_in_rule_attribute918 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INT_in_rule_attribute923 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_rule_attribute927 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ACTIVATION_GROUP_in_rule_attribute940 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute944 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_AUTO_FOCUS_in_rule_attribute954 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute958 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DATE_EFFECTIVE_in_rule_attribute969 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute973 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DATE_EXPIRES_in_rule_attribute983 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute987 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ENABLED_in_rule_attribute997 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute1002 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_rule_attribute1006 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_RULEFLOW_GROUP_in_rule_attribute1017 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute1021 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_LOCK_ON_ACTIVE_in_rule_attribute1031 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute1035 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DIALECT_in_rule_attribute1045 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute1049 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_CALENDARS_in_rule_attribute1058 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute1062 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_AND_IMPLICIT_in_lhs_block1087 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs_block1092 = new BitSet(new long[]{0x0000000087800008L,0x0000000400000C50L});
    public static final BitSet FOLLOW_VT_OR_PREFIX_in_lhs1118 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1123 = new BitSet(new long[]{0x0000000087800008L,0x0000000400000C50L});
    public static final BitSet FOLLOW_VT_OR_INFIX_in_lhs1139 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1143 = new BitSet(new long[]{0x0000000087800008L,0x0000000400000C50L});
    public static final BitSet FOLLOW_lhs_in_lhs1147 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_AND_PREFIX_in_lhs1159 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1164 = new BitSet(new long[]{0x0000000087800008L,0x0000000400000C50L});
    public static final BitSet FOLLOW_VT_AND_INFIX_in_lhs1180 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1184 = new BitSet(new long[]{0x0000000087800008L,0x0000000400000C50L});
    public static final BitSet FOLLOW_lhs_in_lhs1188 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EXISTS_in_lhs1200 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1204 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_NOT_in_lhs1216 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1220 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EVAL_in_lhs1232 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_lhs1236 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_FORALL_in_lhs1248 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1253 = new BitSet(new long[]{0x0000000087800008L,0x0000000400000C50L});
    public static final BitSet FOLLOW_FROM_in_lhs1267 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs1271 = new BitSet(new long[]{0x0000000020000000L,0x0000003000000020L});
    public static final BitSet FOLLOW_from_elements_in_lhs1275 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs1286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_from_elements1307 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_from_elements1311 = new BitSet(new long[]{0x0000000018000000L});
    public static final BitSet FOLLOW_accumulate_parts_in_from_elements1321 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_COLLECT_in_from_elements1334 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_from_elements1338 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ENTRY_POINT_in_from_elements1350 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_ENTRYPOINT_ID_in_from_elements1354 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_from_source_clause_in_from_elements1365 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accumulate_init_clause_in_accumulate_parts1386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accumulate_id_clause_in_accumulate_parts1397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_ACCUMULATE_INIT_CLAUSE_in_accumulate_init_clause1420 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_INIT_in_accumulate_init_clause1429 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1433 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ACTION_in_accumulate_init_clause1441 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1445 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_accumulate_init_reverse_clause_in_accumulate_init_clause1454 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_VK_RESULT_in_accumulate_init_clause1461 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1465 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_REVERSE_in_accumulate_init_reverse_clause1488 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_reverse_clause1492 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ACCUMULATE_ID_CLAUSE_in_accumulate_id_clause1514 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_accumulate_id_clause1518 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_id_clause1522 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FROM_SOURCE_in_from_source_clause1544 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_from_source_clause1548 = new BitSet(new long[]{0x0000000040100008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_from_source_clause1552 = new BitSet(new long[]{0x0000000040000008L});
    public static final BitSet FOLLOW_expression_chain_in_from_source_clause1561 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_EXPRESSION_CHAIN_in_expression_chain1580 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_expression_chain1584 = new BitSet(new long[]{0x0000000040180008L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_expression_chain1588 = new BitSet(new long[]{0x0000000040100008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_expression_chain1593 = new BitSet(new long[]{0x0000000040000008L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain1601 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PATTERN_in_lhs_pattern1619 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_lhs_pattern1623 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_over_clause_in_lhs_pattern1628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OVER_in_over_clause1653 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_over_element_in_over_clause1658 = new BitSet(new long[]{0x0000000000200008L});
    public static final BitSet FOLLOW_VT_BEHAVIOR_in_over_element1679 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_over_element1681 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_over_element1685 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_over_element1689 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FACT_in_fact_expression1712 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_pattern_type_in_fact_expression1716 = new BitSet(new long[]{0x0000001F00100048L,0x00007F83C0408090L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1721 = new BitSet(new long[]{0x0000001F00100048L,0x00007F83C0408090L});
    public static final BitSet FOLLOW_VT_FACT_BINDING_in_fact_expression1735 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_LABEL_in_fact_expression1739 = new BitSet(new long[]{0x0000001F00100040L,0x00007F83C0408090L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1743 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FACT_OR_in_fact_expression1755 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1759 = new BitSet(new long[]{0x0000001F00100040L,0x00007F83C0408090L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1763 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FIELD_in_fact_expression1774 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_field_element_in_fact_expression1778 = new BitSet(new long[]{0x0000001F00100048L,0x00007F83C0408090L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1782 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_BIND_FIELD_in_fact_expression1793 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_LABEL_in_fact_expression1797 = new BitSet(new long[]{0x0000001F00100040L,0x00007F83C0408090L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1801 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EVAL_in_fact_expression1812 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_fact_expression1816 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EQUAL_in_fact_expression1829 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1833 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_EQUAL_in_fact_expression1845 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1849 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GREATER_in_fact_expression1861 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1865 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GREATER_EQUAL_in_fact_expression1877 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1881 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LESS_in_fact_expression1893 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1897 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LESS_EQUAL_in_fact_expression1909 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1913 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_OPERATOR_in_fact_expression1925 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1929 = new BitSet(new long[]{0x0000001F00180040L,0x00007F83C0408090L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_fact_expression1934 = new BitSet(new long[]{0x0000001F00100040L,0x00007F83C0408090L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1939 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_IN_in_fact_expression1950 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1954 = new BitSet(new long[]{0x0000001F00100040L,0x00007F83C0408090L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1960 = new BitSet(new long[]{0x0000001F00100048L,0x00007F83C0408090L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_fact_expression1975 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1979 = new BitSet(new long[]{0x0000001F00100040L,0x00007F83C0408090L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1983 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_fact_expression1993 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1997 = new BitSet(new long[]{0x0000001F00100040L,0x00007F83C0408090L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2001 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ACCESSOR_PATH_in_fact_expression2012 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_accessor_element_in_fact_expression2017 = new BitSet(new long[]{0x0000002000000008L});
    public static final BitSet FOLLOW_STRING_in_fact_expression2032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_fact_expression2042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_fact_expression2052 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_fact_expression2062 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_fact_expression2072 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_fact_expression2082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_ACCESSOR_PATH_in_field_element2104 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_accessor_element_in_field_element2109 = new BitSet(new long[]{0x0000002000000008L});
    public static final BitSet FOLLOW_VT_ACCESSOR_ELEMENT_in_accessor_element2133 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_accessor_element2137 = new BitSet(new long[]{0x0000000000080008L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_accessor_element2141 = new BitSet(new long[]{0x0000000000080008L});
    public static final BitSet FOLLOW_VT_PATTERN_TYPE_in_pattern_type2162 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_pattern_type2166 = new BitSet(new long[]{0x0000000000000008L,0x0000800000080000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_pattern_type2170 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_pattern_type2174 = new BitSet(new long[]{0x0000000000000008L,0x0000800000000000L});
    public static final BitSet FOLLOW_VT_DATA_TYPE_in_data_type2196 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_data_type2200 = new BitSet(new long[]{0x0000000000000008L,0x0000800000080000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_data_type2204 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_data_type2208 = new BitSet(new long[]{0x0000000000000008L,0x0000800000000000L});

}