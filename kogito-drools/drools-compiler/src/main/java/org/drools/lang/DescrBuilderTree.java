// $ANTLR 3.0.1 /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g 2008-06-05 13:52:43

	package org.drools.lang;

	import java.util.HashMap;
	import java.util.Map;
	import java.util.LinkedList;
	import org.drools.lang.descr.DescrFactory;
	import org.drools.lang.descr.BaseDescr;
	import org.drools.lang.descr.PackageDescr;
	import org.drools.lang.descr.AttributeDescr;
	import org.drools.lang.descr.ImportDescr;
	import org.drools.lang.descr.FunctionImportDescr;
	import org.drools.lang.descr.GlobalDescr;
	import org.drools.lang.descr.FunctionDescr;
	import org.drools.lang.descr.FactTemplateDescr;
	import org.drools.lang.descr.FieldTemplateDescr;
	import org.drools.lang.descr.AndDescr;
	import org.drools.lang.descr.QueryDescr;
	import org.drools.lang.descr.RuleDescr;
	import org.drools.lang.descr.PatternSourceDescr;
	import org.drools.lang.descr.AccumulateDescr;
	import org.drools.lang.descr.AccessorDescr;
	import org.drools.lang.descr.DeclarativeInvokerDescr;
	import org.drools.lang.descr.FromDescr;
	import org.drools.lang.descr.FieldConstraintDescr;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class DescrBuilderTree extends TreeParser {
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

        public DescrBuilderTree(TreeNodeStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "/Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g"; }


    	DescrFactory factory = new DescrFactory();
    	PackageDescr packageDescr = null;
    	
    	public PackageDescr getPackageDescr() {
    		return packageDescr;
    	}



    // $ANTLR start compilation_unit
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:45:1: compilation_unit : ^( VT_COMPILATION_UNIT package_statement ( statement )* ) ;
    public final void compilation_unit() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:46:2: ( ^( VT_COMPILATION_UNIT package_statement ( statement )* ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:46:4: ^( VT_COMPILATION_UNIT package_statement ( statement )* )
            {
            match(input,VT_COMPILATION_UNIT,FOLLOW_VT_COMPILATION_UNIT_in_compilation_unit49); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                pushFollow(FOLLOW_package_statement_in_compilation_unit51);
                package_statement();
                _fsp--;

                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:46:44: ( statement )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==VT_FUNCTION_IMPORT||(LA1_0>=VK_DATE_EFFECTIVE && LA1_0<=VK_ENABLED)||(LA1_0>=VK_RULE && LA1_0<=VK_IMPORT)||(LA1_0>=VK_TEMPLATE && LA1_0<=VK_QUERY)||(LA1_0>=VK_FUNCTION && LA1_0<=VK_GLOBAL)) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:46:44: statement
                	    {
                	    pushFollow(FOLLOW_statement_in_compilation_unit53);
                	    statement();
                	    _fsp--;


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
    // $ANTLR end compilation_unit


    // $ANTLR start package_statement
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:49:1: package_statement returns [String packageName] : ( ^( VK_PACKAGE packageId= package_id ) | );
    public final String package_statement() throws RecognitionException {
        String packageName = null;

        List packageId = null;


        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:50:2: ( ^( VK_PACKAGE packageId= package_id ) | )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==VK_PACKAGE) ) {
                alt2=1;
            }
            else if ( (LA2_0==UP||LA2_0==VT_FUNCTION_IMPORT||(LA2_0>=VK_DATE_EFFECTIVE && LA2_0<=VK_ENABLED)||(LA2_0>=VK_RULE && LA2_0<=VK_IMPORT)||(LA2_0>=VK_TEMPLATE && LA2_0<=VK_QUERY)||(LA2_0>=VK_FUNCTION && LA2_0<=VK_GLOBAL)) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("49:1: package_statement returns [String packageName] : ( ^( VK_PACKAGE packageId= package_id ) | );", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:50:4: ^( VK_PACKAGE packageId= package_id )
                    {
                    match(input,VK_PACKAGE,FOLLOW_VK_PACKAGE_in_package_statement71); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_package_id_in_package_statement75);
                    packageId=package_id();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	this.packageDescr = factory.createPackage(packageId);	
                    		packageName = packageDescr.getName();	

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:54:2: 
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
    // $ANTLR end package_statement


    // $ANTLR start package_id
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:58:1: package_id returns [List idList] : ^( VT_PACKAGE_ID (tempList+= ID )+ ) ;
    public final List package_id() throws RecognitionException {
        List idList = null;

        DroolsTree tempList=null;
        List list_tempList=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:59:2: ( ^( VT_PACKAGE_ID (tempList+= ID )+ ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:59:4: ^( VT_PACKAGE_ID (tempList+= ID )+ )
            {
            match(input,VT_PACKAGE_ID,FOLLOW_VT_PACKAGE_ID_in_package_id102); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:59:28: (tempList+= ID )+
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
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:59:28: tempList+= ID
            	    {
            	    tempList=(DroolsTree)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_package_id106); 
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
    // $ANTLR end package_id


    // $ANTLR start statement
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:63:1: statement : (a= rule_attribute | fi= function_import_statement | is= import_statement | gl= global | fn= function | tp= template | rl= rule | qr= query );
    public final void statement() throws RecognitionException {
        AttributeDescr a = null;

        FunctionImportDescr fi = null;

        ImportDescr is = null;

        global_return gl = null;

        function_return fn = null;

        template_return tp = null;

        rule_return rl = null;

        query_return qr = null;


        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:64:2: (a= rule_attribute | fi= function_import_statement | is= import_statement | gl= global | fn= function | tp= template | rl= rule | qr= query )
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
                    new NoViableAltException("63:1: statement : (a= rule_attribute | fi= function_import_statement | is= import_statement | gl= global | fn= function | tp= template | rl= rule | qr= query );", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:64:4: a= rule_attribute
                    {
                    pushFollow(FOLLOW_rule_attribute_in_statement124);
                    a=rule_attribute();
                    _fsp--;

                    	this.packageDescr.addAttribute(a);	

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:66:4: fi= function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement134);
                    fi=function_import_statement();
                    _fsp--;

                    	this.packageDescr.addFunctionImport(fi);	

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:68:4: is= import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement144);
                    is=import_statement();
                    _fsp--;

                    	this.packageDescr.addImport(is);	

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:70:4: gl= global
                    {
                    pushFollow(FOLLOW_global_in_statement155);
                    gl=global();
                    _fsp--;

                    	this.packageDescr.addGlobal(gl.globalDescr);	

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:72:4: fn= function
                    {
                    pushFollow(FOLLOW_function_in_statement165);
                    fn=function();
                    _fsp--;

                    	this.packageDescr.addFunction(fn.functionDescr);	

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:74:4: tp= template
                    {
                    pushFollow(FOLLOW_template_in_statement175);
                    tp=template();
                    _fsp--;

                    	this.packageDescr.addFactTemplate(tp.factTemplateDescr);	

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:76:4: rl= rule
                    {
                    pushFollow(FOLLOW_rule_in_statement185);
                    rl=rule();
                    _fsp--;

                    	this.packageDescr.addRule(rl.ruleDescr);	

                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:78:4: qr= query
                    {
                    pushFollow(FOLLOW_query_in_statement195);
                    qr=query();
                    _fsp--;

                    	this.packageDescr.addRule(qr.queryDescr);	

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
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:82:1: import_statement returns [ImportDescr importDescr] : ^(importStart= VK_IMPORT importId= import_name ) ;
    public final ImportDescr import_statement() throws RecognitionException {
        ImportDescr importDescr = null;

        DroolsTree importStart=null;
        import_name_return importId = null;


        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:83:2: ( ^(importStart= VK_IMPORT importId= import_name ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:83:4: ^(importStart= VK_IMPORT importId= import_name )
            {
            importStart=(DroolsTree)input.LT(1);
            match(input,VK_IMPORT,FOLLOW_VK_IMPORT_in_import_statement216); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_import_name_in_import_statement220);
            importId=import_name();
            _fsp--;


            match(input, Token.UP, null); 
            	importDescr = factory.createImport(importStart, importId.idList, importId.dotStar);	

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
    // $ANTLR end import_statement


    // $ANTLR start function_import_statement
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:87:1: function_import_statement returns [FunctionImportDescr functionImportDescr] : ^(importStart= VT_FUNCTION_IMPORT VK_FUNCTION importId= import_name ) ;
    public final FunctionImportDescr function_import_statement() throws RecognitionException {
        FunctionImportDescr functionImportDescr = null;

        DroolsTree importStart=null;
        import_name_return importId = null;


        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:88:2: ( ^(importStart= VT_FUNCTION_IMPORT VK_FUNCTION importId= import_name ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:88:4: ^(importStart= VT_FUNCTION_IMPORT VK_FUNCTION importId= import_name )
            {
            importStart=(DroolsTree)input.LT(1);
            match(input,VT_FUNCTION_IMPORT,FOLLOW_VT_FUNCTION_IMPORT_in_function_import_statement242); 

            match(input, Token.DOWN, null); 
            match(input,VK_FUNCTION,FOLLOW_VK_FUNCTION_in_function_import_statement244); 
            pushFollow(FOLLOW_import_name_in_function_import_statement248);
            importId=import_name();
            _fsp--;


            match(input, Token.UP, null); 
            	functionImportDescr = factory.createFunctionImport(importStart, importId.idList, importId.dotStar);	

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
    // $ANTLR end function_import_statement

    public static class import_name_return extends TreeRuleReturnScope {
        public List idList;
        public DroolsTree dotStar;
    };

    // $ANTLR start import_name
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:92:1: import_name returns [List idList, DroolsTree dotStar] : ^( VT_IMPORT_ID (tempList+= ID )+ (tempDotStar= DOT_STAR )? ) ;
    public final import_name_return import_name() throws RecognitionException {
        import_name_return retval = new import_name_return();
        retval.start = input.LT(1);

        DroolsTree tempDotStar=null;
        DroolsTree tempList=null;
        List list_tempList=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:93:2: ( ^( VT_IMPORT_ID (tempList+= ID )+ (tempDotStar= DOT_STAR )? ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:93:4: ^( VT_IMPORT_ID (tempList+= ID )+ (tempDotStar= DOT_STAR )? )
            {
            match(input,VT_IMPORT_ID,FOLLOW_VT_IMPORT_ID_in_import_name267); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:93:27: (tempList+= ID )+
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
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:93:27: tempList+= ID
            	    {
            	    tempList=(DroolsTree)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_import_name271); 
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

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:93:44: (tempDotStar= DOT_STAR )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==DOT_STAR) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:93:44: tempDotStar= DOT_STAR
                    {
                    tempDotStar=(DroolsTree)input.LT(1);
                    match(input,DOT_STAR,FOLLOW_DOT_STAR_in_import_name276); 

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
    // $ANTLR end import_name

    public static class global_return extends TreeRuleReturnScope {
        public GlobalDescr globalDescr;
    };

    // $ANTLR start global
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:98:1: global returns [GlobalDescr globalDescr] : ^(start= VK_GLOBAL dt= data_type globalId= VT_GLOBAL_ID ) ;
    public final global_return global() throws RecognitionException {
        global_return retval = new global_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree globalId=null;
        BaseDescr dt = null;


        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:99:2: ( ^(start= VK_GLOBAL dt= data_type globalId= VT_GLOBAL_ID ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:99:4: ^(start= VK_GLOBAL dt= data_type globalId= VT_GLOBAL_ID )
            {
            start=(DroolsTree)input.LT(1);
            match(input,VK_GLOBAL,FOLLOW_VK_GLOBAL_in_global299); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_data_type_in_global303);
            dt=data_type();
            _fsp--;

            globalId=(DroolsTree)input.LT(1);
            match(input,VT_GLOBAL_ID,FOLLOW_VT_GLOBAL_ID_in_global307); 

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
    // $ANTLR end global

    public static class function_return extends TreeRuleReturnScope {
        public FunctionDescr functionDescr;
    };

    // $ANTLR start function
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:103:1: function returns [FunctionDescr functionDescr] : ^(start= VK_FUNCTION (dt= data_type )? functionId= VT_FUNCTION_ID params= parameters content= VT_CURLY_CHUNK ) ;
    public final function_return function() throws RecognitionException {
        function_return retval = new function_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree functionId=null;
        DroolsTree content=null;
        BaseDescr dt = null;

        List params = null;


        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:104:2: ( ^(start= VK_FUNCTION (dt= data_type )? functionId= VT_FUNCTION_ID params= parameters content= VT_CURLY_CHUNK ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:104:4: ^(start= VK_FUNCTION (dt= data_type )? functionId= VT_FUNCTION_ID params= parameters content= VT_CURLY_CHUNK )
            {
            start=(DroolsTree)input.LT(1);
            match(input,VK_FUNCTION,FOLLOW_VK_FUNCTION_in_function329); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:104:26: (dt= data_type )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==VT_DATA_TYPE) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:104:26: dt= data_type
                    {
                    pushFollow(FOLLOW_data_type_in_function333);
                    dt=data_type();
                    _fsp--;


                    }
                    break;

            }

            functionId=(DroolsTree)input.LT(1);
            match(input,VT_FUNCTION_ID,FOLLOW_VT_FUNCTION_ID_in_function338); 
            pushFollow(FOLLOW_parameters_in_function342);
            params=parameters();
            _fsp--;

            content=(DroolsTree)input.LT(1);
            match(input,VT_CURLY_CHUNK,FOLLOW_VT_CURLY_CHUNK_in_function346); 

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
    // $ANTLR end function

    public static class template_return extends TreeRuleReturnScope {
        public FactTemplateDescr factTemplateDescr;
    };

    // $ANTLR start template
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:108:1: template returns [FactTemplateDescr factTemplateDescr] : ^(start= VK_TEMPLATE id= VT_TEMPLATE_ID (ts= template_slot )+ end= END ) ;
    public final template_return template() throws RecognitionException {
        template_return retval = new template_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree id=null;
        DroolsTree end=null;
        FieldTemplateDescr ts = null;



        	List slotList = new LinkedList<FieldTemplateDescr>();

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:111:3: ( ^(start= VK_TEMPLATE id= VT_TEMPLATE_ID (ts= template_slot )+ end= END ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:111:5: ^(start= VK_TEMPLATE id= VT_TEMPLATE_ID (ts= template_slot )+ end= END )
            {
            start=(DroolsTree)input.LT(1);
            match(input,VK_TEMPLATE,FOLLOW_VK_TEMPLATE_in_template371); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)input.LT(1);
            match(input,VT_TEMPLATE_ID,FOLLOW_VT_TEMPLATE_ID_in_template375); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:112:4: (ts= template_slot )+
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
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:112:6: ts= template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template384);
            	    ts=template_slot();
            	    _fsp--;

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

            end=(DroolsTree)input.LT(1);
            match(input,END,FOLLOW_END_in_template392); 

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
    // $ANTLR end template


    // $ANTLR start template_slot
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:116:1: template_slot returns [FieldTemplateDescr fieldTemplateDescr] : ^( VT_SLOT dt= data_type id= VT_SLOT_ID ) ;
    public final FieldTemplateDescr template_slot() throws RecognitionException {
        FieldTemplateDescr fieldTemplateDescr = null;

        DroolsTree id=null;
        BaseDescr dt = null;


        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:117:2: ( ^( VT_SLOT dt= data_type id= VT_SLOT_ID ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:117:4: ^( VT_SLOT dt= data_type id= VT_SLOT_ID )
            {
            match(input,VT_SLOT,FOLLOW_VT_SLOT_in_template_slot412); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_data_type_in_template_slot416);
            dt=data_type();
            _fsp--;

            id=(DroolsTree)input.LT(1);
            match(input,VT_SLOT_ID,FOLLOW_VT_SLOT_ID_in_template_slot420); 

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
    // $ANTLR end template_slot

    public static class query_return extends TreeRuleReturnScope {
        public QueryDescr queryDescr;
    };

    // $ANTLR start query
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:121:1: query returns [QueryDescr queryDescr] : ^(start= VK_QUERY id= VT_QUERY_ID (params= parameters )? lb= lhs_block end= END ) ;
    public final query_return query() throws RecognitionException {
        query_return retval = new query_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree id=null;
        DroolsTree end=null;
        List params = null;

        AndDescr lb = null;


        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:122:2: ( ^(start= VK_QUERY id= VT_QUERY_ID (params= parameters )? lb= lhs_block end= END ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:122:4: ^(start= VK_QUERY id= VT_QUERY_ID (params= parameters )? lb= lhs_block end= END )
            {
            start=(DroolsTree)input.LT(1);
            match(input,VK_QUERY,FOLLOW_VK_QUERY_in_query442); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)input.LT(1);
            match(input,VT_QUERY_ID,FOLLOW_VT_QUERY_ID_in_query446); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:122:42: (params= parameters )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==VT_PARAM_LIST) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:122:42: params= parameters
                    {
                    pushFollow(FOLLOW_parameters_in_query450);
                    params=parameters();
                    _fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_lhs_block_in_query455);
            lb=lhs_block();
            _fsp--;

            end=(DroolsTree)input.LT(1);
            match(input,END,FOLLOW_END_in_query459); 

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
    // $ANTLR end query

    public static class rule_return extends TreeRuleReturnScope {
        public RuleDescr ruleDescr;
    };

    // $ANTLR start rule
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:126:1: rule returns [RuleDescr ruleDescr] : ^(start= VK_RULE id= VT_RULE_ID (ra= rule_attributes )? (wn= when_part )? content= VT_RHS_CHUNK ) ;
    public final rule_return rule() throws RecognitionException {
        rule_return retval = new rule_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree id=null;
        DroolsTree content=null;
        List ra = null;

        AndDescr wn = null;


        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:127:2: ( ^(start= VK_RULE id= VT_RULE_ID (ra= rule_attributes )? (wn= when_part )? content= VT_RHS_CHUNK ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:127:4: ^(start= VK_RULE id= VT_RULE_ID (ra= rule_attributes )? (wn= when_part )? content= VT_RHS_CHUNK )
            {
            start=(DroolsTree)input.LT(1);
            match(input,VK_RULE,FOLLOW_VK_RULE_in_rule481); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)input.LT(1);
            match(input,VT_RULE_ID,FOLLOW_VT_RULE_ID_in_rule485); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:127:36: (ra= rule_attributes )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==VT_RULE_ATTRIBUTES) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:127:36: ra= rule_attributes
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule489);
                    ra=rule_attributes();
                    _fsp--;


                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:127:56: (wn= when_part )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==VK_WHEN) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:127:56: wn= when_part
                    {
                    pushFollow(FOLLOW_when_part_in_rule494);
                    wn=when_part();
                    _fsp--;


                    }
                    break;

            }

            content=(DroolsTree)input.LT(1);
            match(input,VT_RHS_CHUNK,FOLLOW_VT_RHS_CHUNK_in_rule499); 

            match(input, Token.UP, null); 
            	retval.ruleDescr = factory.createRule(start, id, ra, wn, content);	

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
    // $ANTLR end rule


    // $ANTLR start when_part
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:131:1: when_part returns [AndDescr andDescr] : VK_WHEN lh= lhs_block ;
    public final AndDescr when_part() throws RecognitionException {
        AndDescr andDescr = null;

        AndDescr lh = null;


        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:132:2: ( VK_WHEN lh= lhs_block )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:132:4: VK_WHEN lh= lhs_block
            {
            match(input,VK_WHEN,FOLLOW_VK_WHEN_in_when_part518); 
            pushFollow(FOLLOW_lhs_block_in_when_part522);
            lh=lhs_block();
            _fsp--;

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
    // $ANTLR end when_part


    // $ANTLR start rule_attributes
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:136:1: rule_attributes returns [List attrList] : ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? (rl= rule_attribute )+ ) ;
    public final List rule_attributes() throws RecognitionException {
        List attrList = null;

        AttributeDescr rl = null;



        	attrList = new LinkedList<AttributeDescr>();

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:139:3: ( ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? (rl= rule_attribute )+ ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:139:5: ^( VT_RULE_ATTRIBUTES ( VK_ATTRIBUTES )? (rl= rule_attribute )+ )
            {
            match(input,VT_RULE_ATTRIBUTES,FOLLOW_VT_RULE_ATTRIBUTES_in_rule_attributes544); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:139:26: ( VK_ATTRIBUTES )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==VK_ATTRIBUTES) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:139:26: VK_ATTRIBUTES
                    {
                    match(input,VK_ATTRIBUTES,FOLLOW_VK_ATTRIBUTES_in_rule_attributes546); 

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:139:41: (rl= rule_attribute )+
            int cnt13=0;
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( ((LA13_0>=VK_DATE_EFFECTIVE && LA13_0<=VK_ENABLED)) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:139:42: rl= rule_attribute
            	    {
            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes552);
            	    rl=rule_attribute();
            	    _fsp--;

            	    attrList.add(rl);

            	    }
            	    break;

            	default :
            	    if ( cnt13 >= 1 ) break loop13;
                        EarlyExitException eee =
                            new EarlyExitException(13, input);
                        throw eee;
                }
                cnt13++;
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
    // $ANTLR end rule_attributes


    // $ANTLR start parameters
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:142:1: parameters returns [List paramList] : ^( VT_PARAM_LIST (p= param_definition )* ) ;
    public final List parameters() throws RecognitionException {
        List paramList = null;

        Map p = null;



        	paramList = new LinkedList<Map<BaseDescr, BaseDescr>>();

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:145:3: ( ^( VT_PARAM_LIST (p= param_definition )* ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:145:5: ^( VT_PARAM_LIST (p= param_definition )* )
            {
            match(input,VT_PARAM_LIST,FOLLOW_VT_PARAM_LIST_in_parameters576); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:145:21: (p= param_definition )*
                loop14:
                do {
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==VT_DATA_TYPE||LA14_0==ID) ) {
                        alt14=1;
                    }


                    switch (alt14) {
                	case 1 :
                	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:145:22: p= param_definition
                	    {
                	    pushFollow(FOLLOW_param_definition_in_parameters581);
                	    p=param_definition();
                	    _fsp--;

                	    paramList.add(p);

                	    }
                	    break;

                	default :
                	    break loop14;
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
    // $ANTLR end parameters


    // $ANTLR start param_definition
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:148:1: param_definition returns [Map param] : (dt= data_type )? a= argument ;
    public final Map param_definition() throws RecognitionException {
        Map param = null;

        BaseDescr dt = null;

        BaseDescr a = null;


        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:149:2: ( (dt= data_type )? a= argument )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:149:4: (dt= data_type )? a= argument
            {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:149:6: (dt= data_type )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==VT_DATA_TYPE) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:149:6: dt= data_type
                    {
                    pushFollow(FOLLOW_data_type_in_param_definition603);
                    dt=data_type();
                    _fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_argument_in_param_definition608);
            a=argument();
            _fsp--;

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
    // $ANTLR end param_definition


    // $ANTLR start argument
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:154:1: argument returns [BaseDescr arg] : id= ID ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ;
    public final BaseDescr argument() throws RecognitionException {
        BaseDescr arg = null;

        DroolsTree id=null;
        DroolsTree rightList=null;
        List list_rightList=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:155:2: (id= ID ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:155:4: id= ID ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            {
            id=(DroolsTree)input.LT(1);
            match(input,ID,FOLLOW_ID_in_argument628); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:155:10: ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==LEFT_SQUARE) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:155:11: LEFT_SQUARE rightList+= RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_argument631); 
            	    rightList=(DroolsTree)input.LT(1);
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_argument635); 
            	    if (list_rightList==null) list_rightList=new ArrayList();
            	    list_rightList.add(rightList);


            	    }
            	    break;

            	default :
            	    break loop16;
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
    // $ANTLR end argument


    // $ANTLR start rule_attribute
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:159:1: rule_attribute returns [AttributeDescr attributeDescr] : ( ^(attrName= VK_SALIENCE (value= INT | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_NO_LOOP (value= BOOL )? ) | ^(attrName= VK_AGENDA_GROUP value= STRING ) | ^(attrName= VK_DURATION value= INT ) | ^(attrName= VK_ACTIVATION_GROUP value= STRING ) | ^(attrName= VK_AUTO_FOCUS (value= BOOL )? ) | ^(attrName= VK_DATE_EFFECTIVE value= STRING ) | ^(attrName= VK_DATE_EXPIRES value= STRING ) | ^(attrName= VK_ENABLED value= BOOL ) | ^(attrName= VK_RULEFLOW_GROUP value= STRING ) | ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? ) | ^(attrName= VK_DIALECT value= STRING ) ) ;
    public final AttributeDescr rule_attribute() throws RecognitionException {
        AttributeDescr attributeDescr = null;

        DroolsTree attrName=null;
        DroolsTree value=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:160:2: ( ( ^(attrName= VK_SALIENCE (value= INT | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_NO_LOOP (value= BOOL )? ) | ^(attrName= VK_AGENDA_GROUP value= STRING ) | ^(attrName= VK_DURATION value= INT ) | ^(attrName= VK_ACTIVATION_GROUP value= STRING ) | ^(attrName= VK_AUTO_FOCUS (value= BOOL )? ) | ^(attrName= VK_DATE_EFFECTIVE value= STRING ) | ^(attrName= VK_DATE_EXPIRES value= STRING ) | ^(attrName= VK_ENABLED value= BOOL ) | ^(attrName= VK_RULEFLOW_GROUP value= STRING ) | ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? ) | ^(attrName= VK_DIALECT value= STRING ) ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:160:4: ( ^(attrName= VK_SALIENCE (value= INT | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_NO_LOOP (value= BOOL )? ) | ^(attrName= VK_AGENDA_GROUP value= STRING ) | ^(attrName= VK_DURATION value= INT ) | ^(attrName= VK_ACTIVATION_GROUP value= STRING ) | ^(attrName= VK_AUTO_FOCUS (value= BOOL )? ) | ^(attrName= VK_DATE_EFFECTIVE value= STRING ) | ^(attrName= VK_DATE_EXPIRES value= STRING ) | ^(attrName= VK_ENABLED value= BOOL ) | ^(attrName= VK_RULEFLOW_GROUP value= STRING ) | ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? ) | ^(attrName= VK_DIALECT value= STRING ) )
            {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:160:4: ( ^(attrName= VK_SALIENCE (value= INT | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_NO_LOOP (value= BOOL )? ) | ^(attrName= VK_AGENDA_GROUP value= STRING ) | ^(attrName= VK_DURATION value= INT ) | ^(attrName= VK_ACTIVATION_GROUP value= STRING ) | ^(attrName= VK_AUTO_FOCUS (value= BOOL )? ) | ^(attrName= VK_DATE_EFFECTIVE value= STRING ) | ^(attrName= VK_DATE_EXPIRES value= STRING ) | ^(attrName= VK_ENABLED value= BOOL ) | ^(attrName= VK_RULEFLOW_GROUP value= STRING ) | ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? ) | ^(attrName= VK_DIALECT value= STRING ) )
            int alt21=12;
            switch ( input.LA(1) ) {
            case VK_SALIENCE:
                {
                alt21=1;
                }
                break;
            case VK_NO_LOOP:
                {
                alt21=2;
                }
                break;
            case VK_AGENDA_GROUP:
                {
                alt21=3;
                }
                break;
            case VK_DURATION:
                {
                alt21=4;
                }
                break;
            case VK_ACTIVATION_GROUP:
                {
                alt21=5;
                }
                break;
            case VK_AUTO_FOCUS:
                {
                alt21=6;
                }
                break;
            case VK_DATE_EFFECTIVE:
                {
                alt21=7;
                }
                break;
            case VK_DATE_EXPIRES:
                {
                alt21=8;
                }
                break;
            case VK_ENABLED:
                {
                alt21=9;
                }
                break;
            case VK_RULEFLOW_GROUP:
                {
                alt21=10;
                }
                break;
            case VK_LOCK_ON_ACTIVE:
                {
                alt21=11;
                }
                break;
            case VK_DIALECT:
                {
                alt21=12;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("160:4: ( ^(attrName= VK_SALIENCE (value= INT | value= VT_PAREN_CHUNK ) ) | ^(attrName= VK_NO_LOOP (value= BOOL )? ) | ^(attrName= VK_AGENDA_GROUP value= STRING ) | ^(attrName= VK_DURATION value= INT ) | ^(attrName= VK_ACTIVATION_GROUP value= STRING ) | ^(attrName= VK_AUTO_FOCUS (value= BOOL )? ) | ^(attrName= VK_DATE_EFFECTIVE value= STRING ) | ^(attrName= VK_DATE_EXPIRES value= STRING ) | ^(attrName= VK_ENABLED value= BOOL ) | ^(attrName= VK_RULEFLOW_GROUP value= STRING ) | ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? ) | ^(attrName= VK_DIALECT value= STRING ) )", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:160:5: ^(attrName= VK_SALIENCE (value= INT | value= VT_PAREN_CHUNK ) )
                    {
                    attrName=(DroolsTree)input.LT(1);
                    match(input,VK_SALIENCE,FOLLOW_VK_SALIENCE_in_rule_attribute659); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:160:28: (value= INT | value= VT_PAREN_CHUNK )
                    int alt17=2;
                    int LA17_0 = input.LA(1);

                    if ( (LA17_0==INT) ) {
                        alt17=1;
                    }
                    else if ( (LA17_0==VT_PAREN_CHUNK) ) {
                        alt17=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("160:28: (value= INT | value= VT_PAREN_CHUNK )", 17, 0, input);

                        throw nvae;
                    }
                    switch (alt17) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:160:29: value= INT
                            {
                            value=(DroolsTree)input.LT(1);
                            match(input,INT,FOLLOW_INT_in_rule_attribute664); 

                            }
                            break;
                        case 2 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:160:39: value= VT_PAREN_CHUNK
                            {
                            value=(DroolsTree)input.LT(1);
                            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_rule_attribute668); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:161:4: ^(attrName= VK_NO_LOOP (value= BOOL )? )
                    {
                    attrName=(DroolsTree)input.LT(1);
                    match(input,VK_NO_LOOP,FOLLOW_VK_NO_LOOP_in_rule_attribute679); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:161:31: (value= BOOL )?
                        int alt18=2;
                        int LA18_0 = input.LA(1);

                        if ( (LA18_0==BOOL) ) {
                            alt18=1;
                        }
                        switch (alt18) {
                            case 1 :
                                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:161:31: value= BOOL
                                {
                                value=(DroolsTree)input.LT(1);
                                match(input,BOOL,FOLLOW_BOOL_in_rule_attribute683); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:162:4: ^(attrName= VK_AGENDA_GROUP value= STRING )
                    {
                    attrName=(DroolsTree)input.LT(1);
                    match(input,VK_AGENDA_GROUP,FOLLOW_VK_AGENDA_GROUP_in_rule_attribute695); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute699); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:163:4: ^(attrName= VK_DURATION value= INT )
                    {
                    attrName=(DroolsTree)input.LT(1);
                    match(input,VK_DURATION,FOLLOW_VK_DURATION_in_rule_attribute710); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_rule_attribute714); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:164:4: ^(attrName= VK_ACTIVATION_GROUP value= STRING )
                    {
                    attrName=(DroolsTree)input.LT(1);
                    match(input,VK_ACTIVATION_GROUP,FOLLOW_VK_ACTIVATION_GROUP_in_rule_attribute726); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute730); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:165:4: ^(attrName= VK_AUTO_FOCUS (value= BOOL )? )
                    {
                    attrName=(DroolsTree)input.LT(1);
                    match(input,VK_AUTO_FOCUS,FOLLOW_VK_AUTO_FOCUS_in_rule_attribute740); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:165:34: (value= BOOL )?
                        int alt19=2;
                        int LA19_0 = input.LA(1);

                        if ( (LA19_0==BOOL) ) {
                            alt19=1;
                        }
                        switch (alt19) {
                            case 1 :
                                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:165:34: value= BOOL
                                {
                                value=(DroolsTree)input.LT(1);
                                match(input,BOOL,FOLLOW_BOOL_in_rule_attribute744); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:166:4: ^(attrName= VK_DATE_EFFECTIVE value= STRING )
                    {
                    attrName=(DroolsTree)input.LT(1);
                    match(input,VK_DATE_EFFECTIVE,FOLLOW_VK_DATE_EFFECTIVE_in_rule_attribute755); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute759); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:167:4: ^(attrName= VK_DATE_EXPIRES value= STRING )
                    {
                    attrName=(DroolsTree)input.LT(1);
                    match(input,VK_DATE_EXPIRES,FOLLOW_VK_DATE_EXPIRES_in_rule_attribute769); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute773); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:168:4: ^(attrName= VK_ENABLED value= BOOL )
                    {
                    attrName=(DroolsTree)input.LT(1);
                    match(input,VK_ENABLED,FOLLOW_VK_ENABLED_in_rule_attribute783); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_rule_attribute787); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 10 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:169:4: ^(attrName= VK_RULEFLOW_GROUP value= STRING )
                    {
                    attrName=(DroolsTree)input.LT(1);
                    match(input,VK_RULEFLOW_GROUP,FOLLOW_VK_RULEFLOW_GROUP_in_rule_attribute797); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute801); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 11 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:170:4: ^(attrName= VK_LOCK_ON_ACTIVE (value= BOOL )? )
                    {
                    attrName=(DroolsTree)input.LT(1);
                    match(input,VK_LOCK_ON_ACTIVE,FOLLOW_VK_LOCK_ON_ACTIVE_in_rule_attribute811); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:170:38: (value= BOOL )?
                        int alt20=2;
                        int LA20_0 = input.LA(1);

                        if ( (LA20_0==BOOL) ) {
                            alt20=1;
                        }
                        switch (alt20) {
                            case 1 :
                                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:170:38: value= BOOL
                                {
                                value=(DroolsTree)input.LT(1);
                                match(input,BOOL,FOLLOW_BOOL_in_rule_attribute815); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    }
                    break;
                case 12 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:171:4: ^(attrName= VK_DIALECT value= STRING )
                    {
                    attrName=(DroolsTree)input.LT(1);
                    match(input,VK_DIALECT,FOLLOW_VK_DIALECT_in_rule_attribute825); 

                    match(input, Token.DOWN, null); 
                    value=(DroolsTree)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_rule_attribute829); 

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
    // $ANTLR end rule_attribute


    // $ANTLR start lhs_block
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:175:1: lhs_block returns [AndDescr andDescr] : ^( VT_AND_IMPLICIT (dt= lhs )* ) ;
    public final AndDescr lhs_block() throws RecognitionException {
        AndDescr andDescr = null;

        lhs_return dt = null;



        	andDescr = new AndDescr();

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:178:3: ( ^( VT_AND_IMPLICIT (dt= lhs )* ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:178:5: ^( VT_AND_IMPLICIT (dt= lhs )* )
            {
            match(input,VT_AND_IMPLICIT,FOLLOW_VT_AND_IMPLICIT_in_lhs_block854); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:178:23: (dt= lhs )*
                loop22:
                do {
                    int alt22=2;
                    int LA22_0 = input.LA(1);

                    if ( ((LA22_0>=VT_AND_PREFIX && LA22_0<=VT_OR_INFIX)||LA22_0==VT_PATTERN||LA22_0==VK_EVAL||LA22_0==VK_NOT||(LA22_0>=VK_EXISTS && LA22_0<=VK_FROM)) ) {
                        alt22=1;
                    }


                    switch (alt22) {
                	case 1 :
                	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:178:24: dt= lhs
                	    {
                	    pushFollow(FOLLOW_lhs_in_lhs_block859);
                	    dt=lhs();
                	    _fsp--;

                	    andDescr.addDescr(dt.baseDescr);

                	    }
                	    break;

                	default :
                	    break loop22;
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
    // $ANTLR end lhs_block

    public static class lhs_return extends TreeRuleReturnScope {
        public BaseDescr baseDescr;
    };

    // $ANTLR start lhs
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:181:1: lhs returns [BaseDescr baseDescr] : ( ^(start= VT_OR_PREFIX (dt= lhs )+ ) | ^(start= VT_OR_INFIX dt1= lhs dt2= lhs ) | ^(start= VT_AND_PREFIX (dt= lhs )+ ) | ^(start= VT_AND_INFIX dt1= lhs dt2= lhs ) | ^(start= VK_EXISTS dt= lhs ) | ^(start= VK_NOT dt= lhs ) | ^(start= VK_EVAL pc= VT_PAREN_CHUNK ) | ^(start= VK_FORALL (dt= lhs )+ ) | ^( VK_FROM pn= lhs_pattern fe= from_elements ) | pn= lhs_pattern );
    public final lhs_return lhs() throws RecognitionException {
        lhs_return retval = new lhs_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree pc=null;
        lhs_return dt = null;

        lhs_return dt1 = null;

        lhs_return dt2 = null;

        BaseDescr pn = null;

        from_elements_return fe = null;



        	List<BaseDescr> lhsList = new LinkedList<BaseDescr>();

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:184:3: ( ^(start= VT_OR_PREFIX (dt= lhs )+ ) | ^(start= VT_OR_INFIX dt1= lhs dt2= lhs ) | ^(start= VT_AND_PREFIX (dt= lhs )+ ) | ^(start= VT_AND_INFIX dt1= lhs dt2= lhs ) | ^(start= VK_EXISTS dt= lhs ) | ^(start= VK_NOT dt= lhs ) | ^(start= VK_EVAL pc= VT_PAREN_CHUNK ) | ^(start= VK_FORALL (dt= lhs )+ ) | ^( VK_FROM pn= lhs_pattern fe= from_elements ) | pn= lhs_pattern )
            int alt26=10;
            switch ( input.LA(1) ) {
            case VT_OR_PREFIX:
                {
                alt26=1;
                }
                break;
            case VT_OR_INFIX:
                {
                alt26=2;
                }
                break;
            case VT_AND_PREFIX:
                {
                alt26=3;
                }
                break;
            case VT_AND_INFIX:
                {
                alt26=4;
                }
                break;
            case VK_EXISTS:
                {
                alt26=5;
                }
                break;
            case VK_NOT:
                {
                alt26=6;
                }
                break;
            case VK_EVAL:
                {
                alt26=7;
                }
                break;
            case VK_FORALL:
                {
                alt26=8;
                }
                break;
            case VK_FROM:
                {
                alt26=9;
                }
                break;
            case VT_PATTERN:
                {
                alt26=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("181:1: lhs returns [BaseDescr baseDescr] : ( ^(start= VT_OR_PREFIX (dt= lhs )+ ) | ^(start= VT_OR_INFIX dt1= lhs dt2= lhs ) | ^(start= VT_AND_PREFIX (dt= lhs )+ ) | ^(start= VT_AND_INFIX dt1= lhs dt2= lhs ) | ^(start= VK_EXISTS dt= lhs ) | ^(start= VK_NOT dt= lhs ) | ^(start= VK_EVAL pc= VT_PAREN_CHUNK ) | ^(start= VK_FORALL (dt= lhs )+ ) | ^( VK_FROM pn= lhs_pattern fe= from_elements ) | pn= lhs_pattern );", 26, 0, input);

                throw nvae;
            }

            switch (alt26) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:184:5: ^(start= VT_OR_PREFIX (dt= lhs )+ )
                    {
                    start=(DroolsTree)input.LT(1);
                    match(input,VT_OR_PREFIX,FOLLOW_VT_OR_PREFIX_in_lhs885); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:184:26: (dt= lhs )+
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
                    	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:184:27: dt= lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs890);
                    	    dt=lhs();
                    	    _fsp--;

                    	    	lhsList.add(dt.baseDescr);	

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
                    	retval.baseDescr = factory.createOr(start, lhsList);	

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:186:4: ^(start= VT_OR_INFIX dt1= lhs dt2= lhs )
                    {
                    start=(DroolsTree)input.LT(1);
                    match(input,VT_OR_INFIX,FOLLOW_VT_OR_INFIX_in_lhs906); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs910);
                    dt1=lhs();
                    _fsp--;

                    pushFollow(FOLLOW_lhs_in_lhs914);
                    dt2=lhs();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	lhsList.add(dt1.baseDescr);
                    		lhsList.add(dt2.baseDescr);
                    		retval.baseDescr = factory.createOr(start, lhsList);	

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:190:4: ^(start= VT_AND_PREFIX (dt= lhs )+ )
                    {
                    start=(DroolsTree)input.LT(1);
                    match(input,VT_AND_PREFIX,FOLLOW_VT_AND_PREFIX_in_lhs926); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:190:26: (dt= lhs )+
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
                    	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:190:27: dt= lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs931);
                    	    dt=lhs();
                    	    _fsp--;

                    	    	lhsList.add(dt.baseDescr);	

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
                    	retval.baseDescr = factory.createAnd(start, lhsList);	

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:192:4: ^(start= VT_AND_INFIX dt1= lhs dt2= lhs )
                    {
                    start=(DroolsTree)input.LT(1);
                    match(input,VT_AND_INFIX,FOLLOW_VT_AND_INFIX_in_lhs947); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs951);
                    dt1=lhs();
                    _fsp--;

                    pushFollow(FOLLOW_lhs_in_lhs955);
                    dt2=lhs();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	lhsList.add(dt1.baseDescr);
                    		lhsList.add(dt2.baseDescr);
                    		retval.baseDescr = factory.createAnd(start, lhsList);	

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:196:4: ^(start= VK_EXISTS dt= lhs )
                    {
                    start=(DroolsTree)input.LT(1);
                    match(input,VK_EXISTS,FOLLOW_VK_EXISTS_in_lhs967); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs971);
                    dt=lhs();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createExists(start, dt.baseDescr);	

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:198:4: ^(start= VK_NOT dt= lhs )
                    {
                    start=(DroolsTree)input.LT(1);
                    match(input,VK_NOT,FOLLOW_VK_NOT_in_lhs983); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_lhs987);
                    dt=lhs();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createNot(start, dt.baseDescr);	

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:200:4: ^(start= VK_EVAL pc= VT_PAREN_CHUNK )
                    {
                    start=(DroolsTree)input.LT(1);
                    match(input,VK_EVAL,FOLLOW_VK_EVAL_in_lhs999); 

                    match(input, Token.DOWN, null); 
                    pc=(DroolsTree)input.LT(1);
                    match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_lhs1003); 

                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createEval(start, pc);	

                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:202:4: ^(start= VK_FORALL (dt= lhs )+ )
                    {
                    start=(DroolsTree)input.LT(1);
                    match(input,VK_FORALL,FOLLOW_VK_FORALL_in_lhs1015); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:202:22: (dt= lhs )+
                    int cnt25=0;
                    loop25:
                    do {
                        int alt25=2;
                        int LA25_0 = input.LA(1);

                        if ( ((LA25_0>=VT_AND_PREFIX && LA25_0<=VT_OR_INFIX)||LA25_0==VT_PATTERN||LA25_0==VK_EVAL||LA25_0==VK_NOT||(LA25_0>=VK_EXISTS && LA25_0<=VK_FROM)) ) {
                            alt25=1;
                        }


                        switch (alt25) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:202:23: dt= lhs
                    	    {
                    	    pushFollow(FOLLOW_lhs_in_lhs1020);
                    	    dt=lhs();
                    	    _fsp--;

                    	    	lhsList.add(dt.baseDescr);	

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


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.createForAll(start, lhsList);	

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:204:4: ^( VK_FROM pn= lhs_pattern fe= from_elements )
                    {
                    match(input,VK_FROM,FOLLOW_VK_FROM_in_lhs1034); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_pattern_in_lhs1038);
                    pn=lhs_pattern();
                    _fsp--;

                    pushFollow(FOLLOW_from_elements_in_lhs1042);
                    fe=from_elements();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.baseDescr = factory.setupFrom(pn, fe.patternSourceDescr);	

                    }
                    break;
                case 10 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:206:4: pn= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs1053);
                    pn=lhs_pattern();
                    _fsp--;

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
    // $ANTLR end lhs

    public static class from_elements_return extends TreeRuleReturnScope {
        public PatternSourceDescr patternSourceDescr;
    };

    // $ANTLR start from_elements
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:210:1: from_elements returns [PatternSourceDescr patternSourceDescr] : ( ^(start= VK_ACCUMULATE dt= lhs ret= accumulate_parts[$patternSourceDescr] ) | ^(start= VK_COLLECT dt= lhs ) | ^(start= VK_ENTRY_POINT entryId= VT_ENTRYPOINT_ID ) | fs= from_source_clause );
    public final from_elements_return from_elements() throws RecognitionException {
        from_elements_return retval = new from_elements_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree entryId=null;
        lhs_return dt = null;

        AccumulateDescr ret = null;

        from_source_clause_return fs = null;


        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:211:2: ( ^(start= VK_ACCUMULATE dt= lhs ret= accumulate_parts[$patternSourceDescr] ) | ^(start= VK_COLLECT dt= lhs ) | ^(start= VK_ENTRY_POINT entryId= VT_ENTRYPOINT_ID ) | fs= from_source_clause )
            int alt27=4;
            switch ( input.LA(1) ) {
            case VK_ACCUMULATE:
                {
                alt27=1;
                }
                break;
            case VK_COLLECT:
                {
                alt27=2;
                }
                break;
            case VK_ENTRY_POINT:
                {
                alt27=3;
                }
                break;
            case VT_FROM_SOURCE:
                {
                alt27=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("210:1: from_elements returns [PatternSourceDescr patternSourceDescr] : ( ^(start= VK_ACCUMULATE dt= lhs ret= accumulate_parts[$patternSourceDescr] ) | ^(start= VK_COLLECT dt= lhs ) | ^(start= VK_ENTRY_POINT entryId= VT_ENTRYPOINT_ID ) | fs= from_source_clause );", 27, 0, input);

                throw nvae;
            }

            switch (alt27) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:211:4: ^(start= VK_ACCUMULATE dt= lhs ret= accumulate_parts[$patternSourceDescr] )
                    {
                    start=(DroolsTree)input.LT(1);
                    match(input,VK_ACCUMULATE,FOLLOW_VK_ACCUMULATE_in_from_elements1074); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_from_elements1078);
                    dt=lhs();
                    _fsp--;

                    	retval.patternSourceDescr = factory.createAccumulate(start, dt.baseDescr);	
                    pushFollow(FOLLOW_accumulate_parts_in_from_elements1088);
                    ret=accumulate_parts(retval.patternSourceDescr);
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.patternSourceDescr = ret;	

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:215:4: ^(start= VK_COLLECT dt= lhs )
                    {
                    start=(DroolsTree)input.LT(1);
                    match(input,VK_COLLECT,FOLLOW_VK_COLLECT_in_from_elements1101); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_lhs_in_from_elements1105);
                    dt=lhs();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.patternSourceDescr = factory.createCollect(start, dt.baseDescr);	

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:217:4: ^(start= VK_ENTRY_POINT entryId= VT_ENTRYPOINT_ID )
                    {
                    start=(DroolsTree)input.LT(1);
                    match(input,VK_ENTRY_POINT,FOLLOW_VK_ENTRY_POINT_in_from_elements1117); 

                    match(input, Token.DOWN, null); 
                    entryId=(DroolsTree)input.LT(1);
                    match(input,VT_ENTRYPOINT_ID,FOLLOW_VT_ENTRYPOINT_ID_in_from_elements1121); 

                    match(input, Token.UP, null); 
                    	retval.patternSourceDescr = factory.createEntryPoint(start, entryId);	

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:219:4: fs= from_source_clause
                    {
                    pushFollow(FOLLOW_from_source_clause_in_from_elements1132);
                    fs=from_source_clause();
                    _fsp--;

                    	retval.patternSourceDescr = fs.fromDescr;	

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
    // $ANTLR end from_elements


    // $ANTLR start accumulate_parts
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:223:1: accumulate_parts[PatternSourceDescr patternSourceDescr] returns [AccumulateDescr accumulateDescr] : (ac1= accumulate_init_clause[$patternSourceDescr] | ac2= accumulate_id_clause[$patternSourceDescr] );
    public final AccumulateDescr accumulate_parts(PatternSourceDescr patternSourceDescr) throws RecognitionException {
        AccumulateDescr accumulateDescr = null;

        accumulate_init_clause_return ac1 = null;

        AccumulateDescr ac2 = null;


        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:224:2: (ac1= accumulate_init_clause[$patternSourceDescr] | ac2= accumulate_id_clause[$patternSourceDescr] )
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==VT_ACCUMULATE_INIT_CLAUSE) ) {
                alt28=1;
            }
            else if ( (LA28_0==VT_ACCUMULATE_ID_CLAUSE) ) {
                alt28=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("223:1: accumulate_parts[PatternSourceDescr patternSourceDescr] returns [AccumulateDescr accumulateDescr] : (ac1= accumulate_init_clause[$patternSourceDescr] | ac2= accumulate_id_clause[$patternSourceDescr] );", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:224:4: ac1= accumulate_init_clause[$patternSourceDescr]
                    {
                    pushFollow(FOLLOW_accumulate_init_clause_in_accumulate_parts1153);
                    ac1=accumulate_init_clause(patternSourceDescr);
                    _fsp--;

                    	accumulateDescr = ac1.accumulateDescr;	

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:226:4: ac2= accumulate_id_clause[$patternSourceDescr]
                    {
                    pushFollow(FOLLOW_accumulate_id_clause_in_accumulate_parts1164);
                    ac2=accumulate_id_clause(patternSourceDescr);
                    _fsp--;

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
    // $ANTLR end accumulate_parts

    public static class accumulate_init_clause_return extends TreeRuleReturnScope {
        public AccumulateDescr accumulateDescr;
    };

    // $ANTLR start accumulate_init_clause
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:230:1: accumulate_init_clause[PatternSourceDescr accumulateParam] returns [AccumulateDescr accumulateDescr] : ^( VT_ACCUMULATE_INIT_CLAUSE ^(start= VK_INIT pc1= VT_PAREN_CHUNK ) ^( VK_ACTION pc2= VT_PAREN_CHUNK ) (rev= accumulate_init_reverse_clause )? ^( VK_RESULT pc3= VT_PAREN_CHUNK ) ) ;
    public final accumulate_init_clause_return accumulate_init_clause(PatternSourceDescr accumulateParam) throws RecognitionException {
        accumulate_init_clause_return retval = new accumulate_init_clause_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree pc1=null;
        DroolsTree pc2=null;
        DroolsTree pc3=null;
        accumulate_init_reverse_clause_return rev = null;


        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:231:2: ( ^( VT_ACCUMULATE_INIT_CLAUSE ^(start= VK_INIT pc1= VT_PAREN_CHUNK ) ^( VK_ACTION pc2= VT_PAREN_CHUNK ) (rev= accumulate_init_reverse_clause )? ^( VK_RESULT pc3= VT_PAREN_CHUNK ) ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:231:4: ^( VT_ACCUMULATE_INIT_CLAUSE ^(start= VK_INIT pc1= VT_PAREN_CHUNK ) ^( VK_ACTION pc2= VT_PAREN_CHUNK ) (rev= accumulate_init_reverse_clause )? ^( VK_RESULT pc3= VT_PAREN_CHUNK ) )
            {
            match(input,VT_ACCUMULATE_INIT_CLAUSE,FOLLOW_VT_ACCUMULATE_INIT_CLAUSE_in_accumulate_init_clause1187); 

            match(input, Token.DOWN, null); 
            start=(DroolsTree)input.LT(1);
            match(input,VK_INIT,FOLLOW_VK_INIT_in_accumulate_init_clause1196); 

            match(input, Token.DOWN, null); 
            pc1=(DroolsTree)input.LT(1);
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1200); 

            match(input, Token.UP, null); 
            match(input,VK_ACTION,FOLLOW_VK_ACTION_in_accumulate_init_clause1208); 

            match(input, Token.DOWN, null); 
            pc2=(DroolsTree)input.LT(1);
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1212); 

            match(input, Token.UP, null); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:234:7: (rev= accumulate_init_reverse_clause )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==VK_REVERSE) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:234:7: rev= accumulate_init_reverse_clause
                    {
                    pushFollow(FOLLOW_accumulate_init_reverse_clause_in_accumulate_init_clause1221);
                    rev=accumulate_init_reverse_clause();
                    _fsp--;


                    }
                    break;

            }

            match(input,VK_RESULT,FOLLOW_VK_RESULT_in_accumulate_init_clause1228); 

            match(input, Token.DOWN, null); 
            pc3=(DroolsTree)input.LT(1);
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1232); 

            match(input, Token.UP, null); 

            match(input, Token.UP, null); 
            	if (null == rev){
            			retval.accumulateDescr = factory.setupAccumulateInit(accumulateParam, start, pc1, pc2, pc3, null);
            		} else {
            			retval.accumulateDescr = factory.setupAccumulateInit(accumulateParam, start, pc1, pc2, pc3, rev.vkReverseChunk);
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
    // $ANTLR end accumulate_init_clause

    public static class accumulate_init_reverse_clause_return extends TreeRuleReturnScope {
        public DroolsTree vkReverse;
        public DroolsTree vkReverseChunk;
    };

    // $ANTLR start accumulate_init_reverse_clause
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:243:1: accumulate_init_reverse_clause returns [DroolsTree vkReverse, DroolsTree vkReverseChunk] : ^(vk= VK_REVERSE pc= VT_PAREN_CHUNK ) ;
    public final accumulate_init_reverse_clause_return accumulate_init_reverse_clause() throws RecognitionException {
        accumulate_init_reverse_clause_return retval = new accumulate_init_reverse_clause_return();
        retval.start = input.LT(1);

        DroolsTree vk=null;
        DroolsTree pc=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:244:2: ( ^(vk= VK_REVERSE pc= VT_PAREN_CHUNK ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:244:4: ^(vk= VK_REVERSE pc= VT_PAREN_CHUNK )
            {
            vk=(DroolsTree)input.LT(1);
            match(input,VK_REVERSE,FOLLOW_VK_REVERSE_in_accumulate_init_reverse_clause1255); 

            match(input, Token.DOWN, null); 
            pc=(DroolsTree)input.LT(1);
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_reverse_clause1259); 

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
    // $ANTLR end accumulate_init_reverse_clause


    // $ANTLR start accumulate_id_clause
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:249:1: accumulate_id_clause[PatternSourceDescr accumulateParam] returns [AccumulateDescr accumulateDescr] : ^( VT_ACCUMULATE_ID_CLAUSE id= ID pc= VT_PAREN_CHUNK ) ;
    public final AccumulateDescr accumulate_id_clause(PatternSourceDescr accumulateParam) throws RecognitionException {
        AccumulateDescr accumulateDescr = null;

        DroolsTree id=null;
        DroolsTree pc=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:250:2: ( ^( VT_ACCUMULATE_ID_CLAUSE id= ID pc= VT_PAREN_CHUNK ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:250:4: ^( VT_ACCUMULATE_ID_CLAUSE id= ID pc= VT_PAREN_CHUNK )
            {
            match(input,VT_ACCUMULATE_ID_CLAUSE,FOLLOW_VT_ACCUMULATE_ID_CLAUSE_in_accumulate_id_clause1281); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)input.LT(1);
            match(input,ID,FOLLOW_ID_in_accumulate_id_clause1285); 
            pc=(DroolsTree)input.LT(1);
            match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_accumulate_id_clause1289); 

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
    // $ANTLR end accumulate_id_clause

    protected static class from_source_clause_scope {
        AccessorDescr accessorDescr;
    }
    protected Stack from_source_clause_stack = new Stack();

    public static class from_source_clause_return extends TreeRuleReturnScope {
        public FromDescr fromDescr;
        public AccessorDescr retAccessorDescr;
    };

    // $ANTLR start from_source_clause
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:254:1: from_source_clause returns [FromDescr fromDescr, AccessorDescr retAccessorDescr] : ^( VT_FROM_SOURCE id= ID (pc= VT_PAREN_CHUNK )? ( expression_chain )? ) ;
    public final from_source_clause_return from_source_clause() throws RecognitionException {
        from_source_clause_stack.push(new from_source_clause_scope());
        from_source_clause_return retval = new from_source_clause_return();
        retval.start = input.LT(1);

        DroolsTree id=null;
        DroolsTree pc=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:257:3: ( ^( VT_FROM_SOURCE id= ID (pc= VT_PAREN_CHUNK )? ( expression_chain )? ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:257:5: ^( VT_FROM_SOURCE id= ID (pc= VT_PAREN_CHUNK )? ( expression_chain )? )
            {
            match(input,VT_FROM_SOURCE,FOLLOW_VT_FROM_SOURCE_in_from_source_clause1311); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)input.LT(1);
            match(input,ID,FOLLOW_ID_in_from_source_clause1315); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:257:30: (pc= VT_PAREN_CHUNK )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==VT_PAREN_CHUNK) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:257:30: pc= VT_PAREN_CHUNK
                    {
                    pc=(DroolsTree)input.LT(1);
                    match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_from_source_clause1319); 

                    }
                    break;

            }

            	((from_source_clause_scope)from_source_clause_stack.peek()).accessorDescr = factory.createAccessor(id, pc);	
            		retval.retAccessorDescr = ((from_source_clause_scope)from_source_clause_stack.peek()).accessorDescr;	
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:260:3: ( expression_chain )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==VT_EXPRESSION_CHAIN) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:260:3: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source_clause1328);
                    expression_chain();
                    _fsp--;


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
    // $ANTLR end from_source_clause

    public static class expression_chain_return extends TreeRuleReturnScope {
    };

    // $ANTLR start expression_chain
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:264:1: expression_chain : ^(start= VT_EXPRESSION_CHAIN id= ID (sc= VT_SQUARE_CHUNK )? (pc= VT_PAREN_CHUNK )? ( expression_chain )? ) ;
    public final expression_chain_return expression_chain() throws RecognitionException {
        expression_chain_return retval = new expression_chain_return();
        retval.start = input.LT(1);

        DroolsTree start=null;
        DroolsTree id=null;
        DroolsTree sc=null;
        DroolsTree pc=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:265:2: ( ^(start= VT_EXPRESSION_CHAIN id= ID (sc= VT_SQUARE_CHUNK )? (pc= VT_PAREN_CHUNK )? ( expression_chain )? ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:265:4: ^(start= VT_EXPRESSION_CHAIN id= ID (sc= VT_SQUARE_CHUNK )? (pc= VT_PAREN_CHUNK )? ( expression_chain )? )
            {
            start=(DroolsTree)input.LT(1);
            match(input,VT_EXPRESSION_CHAIN,FOLLOW_VT_EXPRESSION_CHAIN_in_expression_chain1347); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)input.LT(1);
            match(input,ID,FOLLOW_ID_in_expression_chain1351); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:265:40: (sc= VT_SQUARE_CHUNK )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==VT_SQUARE_CHUNK) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:265:40: sc= VT_SQUARE_CHUNK
                    {
                    sc=(DroolsTree)input.LT(1);
                    match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_expression_chain1355); 

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:265:60: (pc= VT_PAREN_CHUNK )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==VT_PAREN_CHUNK) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:265:60: pc= VT_PAREN_CHUNK
                    {
                    pc=(DroolsTree)input.LT(1);
                    match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_expression_chain1360); 

                    }
                    break;

            }

            	DeclarativeInvokerDescr declarativeInvokerResult = factory.createExpressionChain(start, id, sc, pc);	
            		((from_source_clause_scope)from_source_clause_stack.peek()).accessorDescr.addInvoker(declarativeInvokerResult);	
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:268:3: ( expression_chain )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==VT_EXPRESSION_CHAIN) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:268:3: expression_chain
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain1368);
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
        return retval;
    }
    // $ANTLR end expression_chain


    // $ANTLR start lhs_pattern
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:271:1: lhs_pattern returns [BaseDescr baseDescr] : ^( VT_PATTERN fe= fact_expression ) ;
    public final BaseDescr lhs_pattern() throws RecognitionException {
        BaseDescr baseDescr = null;

        fact_expression_return fe = null;


        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:272:2: ( ^( VT_PATTERN fe= fact_expression ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:272:4: ^( VT_PATTERN fe= fact_expression )
            {
            match(input,VT_PATTERN,FOLLOW_VT_PATTERN_in_lhs_pattern1386); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_fact_expression_in_lhs_pattern1390);
            fe=fact_expression();
            _fsp--;


            match(input, Token.UP, null); 
            	baseDescr = fe.descr;	

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
    // $ANTLR end lhs_pattern

    public static class fact_expression_return extends TreeRuleReturnScope {
        public BaseDescr descr;
    };

    // $ANTLR start fact_expression
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:276:1: fact_expression returns [BaseDescr descr] : ( ^( VT_FACT pt= pattern_type (fe= fact_expression )* ) | ^( VT_FACT_BINDING label= VT_LABEL fact= fact_expression ) | ^(start= VT_FACT_OR left= fact_expression right= fact_expression ) | ^( VT_FIELD field= field_element (fe= fact_expression )? ) | ^( VT_BIND_FIELD label= VT_LABEL fe= fact_expression ) | ^( VK_EVAL pc= VT_PAREN_CHUNK ) | ^(op= EQUAL fe= fact_expression ) | ^(op= NOT_EQUAL fe= fact_expression ) | ^(op= GREATER fe= fact_expression ) | ^(op= GREATER_EQUAL fe= fact_expression ) | ^(op= LESS fe= fact_expression ) | ^(op= LESS_EQUAL fe= fact_expression ) | ^(op= VK_CONTAINS (not= VK_NOT )? fe= fact_expression ) | ^(op= VK_EXCLUDES (not= VK_NOT )? fe= fact_expression ) | ^(op= VK_MATCHES (not= VK_NOT )? fe= fact_expression ) | ^(op= VK_SOUNDSLIKE (not= VK_NOT )? fe= fact_expression ) | ^(op= VK_MEMBEROF (not= VK_NOT )? fe= fact_expression ) | ^(op= ID (not= VK_NOT )? (param= VT_SQUARE_CHUNK )? fe= fact_expression ) | ^( VK_IN (not= VK_NOT )? (fe= fact_expression )+ ) | ^( DOUBLE_PIPE left= fact_expression right= fact_expression ) | ^( DOUBLE_AMPER left= fact_expression right= fact_expression ) | ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) | s= STRING | i= INT | f= FLOAT | b= BOOL | n= NULL | pc= VT_PAREN_CHUNK );
    public final fact_expression_return fact_expression() throws RecognitionException {
        fact_expression_return retval = new fact_expression_return();
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

        fact_expression_return fe = null;

        fact_expression_return fact = null;

        fact_expression_return left = null;

        fact_expression_return right = null;

        FieldConstraintDescr field = null;

        BaseDescr ae = null;



        	List<BaseDescr> exprList = new LinkedList<BaseDescr>();

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:279:3: ( ^( VT_FACT pt= pattern_type (fe= fact_expression )* ) | ^( VT_FACT_BINDING label= VT_LABEL fact= fact_expression ) | ^(start= VT_FACT_OR left= fact_expression right= fact_expression ) | ^( VT_FIELD field= field_element (fe= fact_expression )? ) | ^( VT_BIND_FIELD label= VT_LABEL fe= fact_expression ) | ^( VK_EVAL pc= VT_PAREN_CHUNK ) | ^(op= EQUAL fe= fact_expression ) | ^(op= NOT_EQUAL fe= fact_expression ) | ^(op= GREATER fe= fact_expression ) | ^(op= GREATER_EQUAL fe= fact_expression ) | ^(op= LESS fe= fact_expression ) | ^(op= LESS_EQUAL fe= fact_expression ) | ^(op= VK_CONTAINS (not= VK_NOT )? fe= fact_expression ) | ^(op= VK_EXCLUDES (not= VK_NOT )? fe= fact_expression ) | ^(op= VK_MATCHES (not= VK_NOT )? fe= fact_expression ) | ^(op= VK_SOUNDSLIKE (not= VK_NOT )? fe= fact_expression ) | ^(op= VK_MEMBEROF (not= VK_NOT )? fe= fact_expression ) | ^(op= ID (not= VK_NOT )? (param= VT_SQUARE_CHUNK )? fe= fact_expression ) | ^( VK_IN (not= VK_NOT )? (fe= fact_expression )+ ) | ^( DOUBLE_PIPE left= fact_expression right= fact_expression ) | ^( DOUBLE_AMPER left= fact_expression right= fact_expression ) | ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) | s= STRING | i= INT | f= FLOAT | b= BOOL | n= NULL | pc= VT_PAREN_CHUNK )
            int alt47=28;
            switch ( input.LA(1) ) {
            case VT_FACT:
                {
                alt47=1;
                }
                break;
            case VT_FACT_BINDING:
                {
                alt47=2;
                }
                break;
            case VT_FACT_OR:
                {
                alt47=3;
                }
                break;
            case VT_FIELD:
                {
                alt47=4;
                }
                break;
            case VT_BIND_FIELD:
                {
                alt47=5;
                }
                break;
            case VK_EVAL:
                {
                alt47=6;
                }
                break;
            case EQUAL:
                {
                alt47=7;
                }
                break;
            case NOT_EQUAL:
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
            case VK_CONTAINS:
                {
                alt47=13;
                }
                break;
            case VK_EXCLUDES:
                {
                alt47=14;
                }
                break;
            case VK_MATCHES:
                {
                alt47=15;
                }
                break;
            case VK_SOUNDSLIKE:
                {
                alt47=16;
                }
                break;
            case VK_MEMBEROF:
                {
                alt47=17;
                }
                break;
            case ID:
                {
                alt47=18;
                }
                break;
            case VK_IN:
                {
                alt47=19;
                }
                break;
            case DOUBLE_PIPE:
                {
                alt47=20;
                }
                break;
            case DOUBLE_AMPER:
                {
                alt47=21;
                }
                break;
            case VT_ACCESSOR_PATH:
                {
                alt47=22;
                }
                break;
            case STRING:
                {
                alt47=23;
                }
                break;
            case INT:
                {
                alt47=24;
                }
                break;
            case FLOAT:
                {
                alt47=25;
                }
                break;
            case BOOL:
                {
                alt47=26;
                }
                break;
            case NULL:
                {
                alt47=27;
                }
                break;
            case VT_PAREN_CHUNK:
                {
                alt47=28;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("276:1: fact_expression returns [BaseDescr descr] : ( ^( VT_FACT pt= pattern_type (fe= fact_expression )* ) | ^( VT_FACT_BINDING label= VT_LABEL fact= fact_expression ) | ^(start= VT_FACT_OR left= fact_expression right= fact_expression ) | ^( VT_FIELD field= field_element (fe= fact_expression )? ) | ^( VT_BIND_FIELD label= VT_LABEL fe= fact_expression ) | ^( VK_EVAL pc= VT_PAREN_CHUNK ) | ^(op= EQUAL fe= fact_expression ) | ^(op= NOT_EQUAL fe= fact_expression ) | ^(op= GREATER fe= fact_expression ) | ^(op= GREATER_EQUAL fe= fact_expression ) | ^(op= LESS fe= fact_expression ) | ^(op= LESS_EQUAL fe= fact_expression ) | ^(op= VK_CONTAINS (not= VK_NOT )? fe= fact_expression ) | ^(op= VK_EXCLUDES (not= VK_NOT )? fe= fact_expression ) | ^(op= VK_MATCHES (not= VK_NOT )? fe= fact_expression ) | ^(op= VK_SOUNDSLIKE (not= VK_NOT )? fe= fact_expression ) | ^(op= VK_MEMBEROF (not= VK_NOT )? fe= fact_expression ) | ^(op= ID (not= VK_NOT )? (param= VT_SQUARE_CHUNK )? fe= fact_expression ) | ^( VK_IN (not= VK_NOT )? (fe= fact_expression )+ ) | ^( DOUBLE_PIPE left= fact_expression right= fact_expression ) | ^( DOUBLE_AMPER left= fact_expression right= fact_expression ) | ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) | s= STRING | i= INT | f= FLOAT | b= BOOL | n= NULL | pc= VT_PAREN_CHUNK );", 47, 0, input);

                throw nvae;
            }

            switch (alt47) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:279:5: ^( VT_FACT pt= pattern_type (fe= fact_expression )* )
                    {
                    match(input,VT_FACT,FOLLOW_VT_FACT_in_fact_expression1413); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_pattern_type_in_fact_expression1417);
                    pt=pattern_type();
                    _fsp--;

                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:279:31: (fe= fact_expression )*
                    loop35:
                    do {
                        int alt35=2;
                        int LA35_0 = input.LA(1);

                        if ( (LA35_0==VT_FACT||LA35_0==VT_PAREN_CHUNK||(LA35_0>=VT_FACT_BINDING && LA35_0<=VT_ACCESSOR_PATH)||(LA35_0>=VK_EVAL && LA35_0<=VK_MEMBEROF)||LA35_0==VK_IN||LA35_0==ID||LA35_0==STRING||(LA35_0>=BOOL && LA35_0<=DOUBLE_AMPER)||(LA35_0>=EQUAL && LA35_0<=NOT_EQUAL)||(LA35_0>=FLOAT && LA35_0<=NULL)) ) {
                            alt35=1;
                        }


                        switch (alt35) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:279:32: fe= fact_expression
                    	    {
                    	    pushFollow(FOLLOW_fact_expression_in_fact_expression1422);
                    	    fe=fact_expression();
                    	    _fsp--;

                    	    exprList.add(fe.descr);

                    	    }
                    	    break;

                    	default :
                    	    break loop35;
                        }
                    } while (true);


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createPattern(pt, exprList);	

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:281:4: ^( VT_FACT_BINDING label= VT_LABEL fact= fact_expression )
                    {
                    match(input,VT_FACT_BINDING,FOLLOW_VT_FACT_BINDING_in_fact_expression1436); 

                    match(input, Token.DOWN, null); 
                    label=(DroolsTree)input.LT(1);
                    match(input,VT_LABEL,FOLLOW_VT_LABEL_in_fact_expression1440); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1444);
                    fact=fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupPatternBiding(label, fact.descr);	

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:283:4: ^(start= VT_FACT_OR left= fact_expression right= fact_expression )
                    {
                    start=(DroolsTree)input.LT(1);
                    match(input,VT_FACT_OR,FOLLOW_VT_FACT_OR_in_fact_expression1456); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1460);
                    left=fact_expression();
                    _fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1464);
                    right=fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createFactOr(start, left.descr, right.descr);	

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:286:4: ^( VT_FIELD field= field_element (fe= fact_expression )? )
                    {
                    match(input,VT_FIELD,FOLLOW_VT_FIELD_in_fact_expression1475); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_field_element_in_fact_expression1479);
                    field=field_element();
                    _fsp--;

                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:286:37: (fe= fact_expression )?
                    int alt36=2;
                    int LA36_0 = input.LA(1);

                    if ( (LA36_0==VT_FACT||LA36_0==VT_PAREN_CHUNK||(LA36_0>=VT_FACT_BINDING && LA36_0<=VT_ACCESSOR_PATH)||(LA36_0>=VK_EVAL && LA36_0<=VK_MEMBEROF)||LA36_0==VK_IN||LA36_0==ID||LA36_0==STRING||(LA36_0>=BOOL && LA36_0<=DOUBLE_AMPER)||(LA36_0>=EQUAL && LA36_0<=NOT_EQUAL)||(LA36_0>=FLOAT && LA36_0<=NULL)) ) {
                        alt36=1;
                    }
                    switch (alt36) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:286:37: fe= fact_expression
                            {
                            pushFollow(FOLLOW_fact_expression_in_fact_expression1483);
                            fe=fact_expression();
                            _fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                    	if (null != fe){
                    			retval.descr = factory.setupFieldConstraint(field, fe.descr);
                    		} else {
                    			retval.descr = factory.setupFieldConstraint(field, null);
                    		}	

                    }
                    break;
                case 5 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:292:4: ^( VT_BIND_FIELD label= VT_LABEL fe= fact_expression )
                    {
                    match(input,VT_BIND_FIELD,FOLLOW_VT_BIND_FIELD_in_fact_expression1494); 

                    match(input, Token.DOWN, null); 
                    label=(DroolsTree)input.LT(1);
                    match(input,VT_LABEL,FOLLOW_VT_LABEL_in_fact_expression1498); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1502);
                    fe=fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createFieldBinding(label, fe.descr);	

                    }
                    break;
                case 6 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:295:4: ^( VK_EVAL pc= VT_PAREN_CHUNK )
                    {
                    match(input,VK_EVAL,FOLLOW_VK_EVAL_in_fact_expression1513); 

                    match(input, Token.DOWN, null); 
                    pc=(DroolsTree)input.LT(1);
                    match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_fact_expression1517); 

                    match(input, Token.UP, null); 
                    	retval.descr = factory.createPredicate(pc);	

                    }
                    break;
                case 7 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:298:4: ^(op= EQUAL fe= fact_expression )
                    {
                    op=(DroolsTree)input.LT(1);
                    match(input,EQUAL,FOLLOW_EQUAL_in_fact_expression1530); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1534);
                    fe=fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, fe.descr);	

                    }
                    break;
                case 8 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:300:4: ^(op= NOT_EQUAL fe= fact_expression )
                    {
                    op=(DroolsTree)input.LT(1);
                    match(input,NOT_EQUAL,FOLLOW_NOT_EQUAL_in_fact_expression1546); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1550);
                    fe=fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, fe.descr);	

                    }
                    break;
                case 9 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:302:4: ^(op= GREATER fe= fact_expression )
                    {
                    op=(DroolsTree)input.LT(1);
                    match(input,GREATER,FOLLOW_GREATER_in_fact_expression1562); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1566);
                    fe=fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, fe.descr);	

                    }
                    break;
                case 10 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:304:4: ^(op= GREATER_EQUAL fe= fact_expression )
                    {
                    op=(DroolsTree)input.LT(1);
                    match(input,GREATER_EQUAL,FOLLOW_GREATER_EQUAL_in_fact_expression1578); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1582);
                    fe=fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, fe.descr);	

                    }
                    break;
                case 11 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:306:4: ^(op= LESS fe= fact_expression )
                    {
                    op=(DroolsTree)input.LT(1);
                    match(input,LESS,FOLLOW_LESS_in_fact_expression1594); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1598);
                    fe=fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, fe.descr);	

                    }
                    break;
                case 12 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:308:4: ^(op= LESS_EQUAL fe= fact_expression )
                    {
                    op=(DroolsTree)input.LT(1);
                    match(input,LESS_EQUAL,FOLLOW_LESS_EQUAL_in_fact_expression1610); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1614);
                    fe=fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, null, fe.descr);	

                    }
                    break;
                case 13 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:310:4: ^(op= VK_CONTAINS (not= VK_NOT )? fe= fact_expression )
                    {
                    op=(DroolsTree)input.LT(1);
                    match(input,VK_CONTAINS,FOLLOW_VK_CONTAINS_in_fact_expression1626); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:310:24: (not= VK_NOT )?
                    int alt37=2;
                    int LA37_0 = input.LA(1);

                    if ( (LA37_0==VK_NOT) ) {
                        alt37=1;
                    }
                    switch (alt37) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:310:24: not= VK_NOT
                            {
                            not=(DroolsTree)input.LT(1);
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1630); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1635);
                    fe=fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, not, fe.descr);	

                    }
                    break;
                case 14 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:312:4: ^(op= VK_EXCLUDES (not= VK_NOT )? fe= fact_expression )
                    {
                    op=(DroolsTree)input.LT(1);
                    match(input,VK_EXCLUDES,FOLLOW_VK_EXCLUDES_in_fact_expression1647); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:312:24: (not= VK_NOT )?
                    int alt38=2;
                    int LA38_0 = input.LA(1);

                    if ( (LA38_0==VK_NOT) ) {
                        alt38=1;
                    }
                    switch (alt38) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:312:24: not= VK_NOT
                            {
                            not=(DroolsTree)input.LT(1);
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1651); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1656);
                    fe=fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, not, fe.descr);	

                    }
                    break;
                case 15 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:314:4: ^(op= VK_MATCHES (not= VK_NOT )? fe= fact_expression )
                    {
                    op=(DroolsTree)input.LT(1);
                    match(input,VK_MATCHES,FOLLOW_VK_MATCHES_in_fact_expression1668); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:314:23: (not= VK_NOT )?
                    int alt39=2;
                    int LA39_0 = input.LA(1);

                    if ( (LA39_0==VK_NOT) ) {
                        alt39=1;
                    }
                    switch (alt39) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:314:23: not= VK_NOT
                            {
                            not=(DroolsTree)input.LT(1);
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1672); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1677);
                    fe=fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, not, fe.descr);	

                    }
                    break;
                case 16 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:316:4: ^(op= VK_SOUNDSLIKE (not= VK_NOT )? fe= fact_expression )
                    {
                    op=(DroolsTree)input.LT(1);
                    match(input,VK_SOUNDSLIKE,FOLLOW_VK_SOUNDSLIKE_in_fact_expression1689); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:316:26: (not= VK_NOT )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);

                    if ( (LA40_0==VK_NOT) ) {
                        alt40=1;
                    }
                    switch (alt40) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:316:26: not= VK_NOT
                            {
                            not=(DroolsTree)input.LT(1);
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1693); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1698);
                    fe=fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, not, fe.descr);	

                    }
                    break;
                case 17 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:318:4: ^(op= VK_MEMBEROF (not= VK_NOT )? fe= fact_expression )
                    {
                    op=(DroolsTree)input.LT(1);
                    match(input,VK_MEMBEROF,FOLLOW_VK_MEMBEROF_in_fact_expression1710); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:318:24: (not= VK_NOT )?
                    int alt41=2;
                    int LA41_0 = input.LA(1);

                    if ( (LA41_0==VK_NOT) ) {
                        alt41=1;
                    }
                    switch (alt41) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:318:24: not= VK_NOT
                            {
                            not=(DroolsTree)input.LT(1);
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1714); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1719);
                    fe=fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, not, fe.descr);	

                    }
                    break;
                case 18 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:320:4: ^(op= ID (not= VK_NOT )? (param= VT_SQUARE_CHUNK )? fe= fact_expression )
                    {
                    op=(DroolsTree)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_fact_expression1731); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:320:15: (not= VK_NOT )?
                    int alt42=2;
                    int LA42_0 = input.LA(1);

                    if ( (LA42_0==VK_NOT) ) {
                        alt42=1;
                    }
                    switch (alt42) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:320:15: not= VK_NOT
                            {
                            not=(DroolsTree)input.LT(1);
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1735); 

                            }
                            break;

                    }

                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:320:29: (param= VT_SQUARE_CHUNK )?
                    int alt43=2;
                    int LA43_0 = input.LA(1);

                    if ( (LA43_0==VT_SQUARE_CHUNK) ) {
                        alt43=1;
                    }
                    switch (alt43) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:320:29: param= VT_SQUARE_CHUNK
                            {
                            param=(DroolsTree)input.LT(1);
                            match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_fact_expression1740); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1745);
                    fe=fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.setupRestriction(op, not, fe.descr, param);	

                    }
                    break;
                case 19 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:323:4: ^( VK_IN (not= VK_NOT )? (fe= fact_expression )+ )
                    {
                    match(input,VK_IN,FOLLOW_VK_IN_in_fact_expression1756); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:323:15: (not= VK_NOT )?
                    int alt44=2;
                    int LA44_0 = input.LA(1);

                    if ( (LA44_0==VK_NOT) ) {
                        alt44=1;
                    }
                    switch (alt44) {
                        case 1 :
                            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:323:15: not= VK_NOT
                            {
                            not=(DroolsTree)input.LT(1);
                            match(input,VK_NOT,FOLLOW_VK_NOT_in_fact_expression1760); 

                            }
                            break;

                    }

                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:323:24: (fe= fact_expression )+
                    int cnt45=0;
                    loop45:
                    do {
                        int alt45=2;
                        int LA45_0 = input.LA(1);

                        if ( (LA45_0==VT_FACT||LA45_0==VT_PAREN_CHUNK||(LA45_0>=VT_FACT_BINDING && LA45_0<=VT_ACCESSOR_PATH)||(LA45_0>=VK_EVAL && LA45_0<=VK_MEMBEROF)||LA45_0==VK_IN||LA45_0==ID||LA45_0==STRING||(LA45_0>=BOOL && LA45_0<=DOUBLE_AMPER)||(LA45_0>=EQUAL && LA45_0<=NOT_EQUAL)||(LA45_0>=FLOAT && LA45_0<=NULL)) ) {
                            alt45=1;
                        }


                        switch (alt45) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:323:25: fe= fact_expression
                    	    {
                    	    pushFollow(FOLLOW_fact_expression_in_fact_expression1766);
                    	    fe=fact_expression();
                    	    _fsp--;

                    	    exprList.add(fe.descr);

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
                    	retval.descr = factory.createRestrictionConnective(not, exprList);	

                    }
                    break;
                case 20 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:326:4: ^( DOUBLE_PIPE left= fact_expression right= fact_expression )
                    {
                    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_fact_expression1781); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1785);
                    left=fact_expression();
                    _fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1789);
                    right=fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createOrRestrictionConnective(left.descr, right.descr);	

                    }
                    break;
                case 21 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:328:4: ^( DOUBLE_AMPER left= fact_expression right= fact_expression )
                    {
                    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_fact_expression1799); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fact_expression_in_fact_expression1803);
                    left=fact_expression();
                    _fsp--;

                    pushFollow(FOLLOW_fact_expression_in_fact_expression1807);
                    right=fact_expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    	retval.descr = factory.createAndRestrictionConnective(left.descr, right.descr);	

                    }
                    break;
                case 22 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:331:4: ^( VT_ACCESSOR_PATH (ae= accessor_element )+ )
                    {
                    match(input,VT_ACCESSOR_PATH,FOLLOW_VT_ACCESSOR_PATH_in_fact_expression1818); 

                    match(input, Token.DOWN, null); 
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:331:23: (ae= accessor_element )+
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
                    	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:331:24: ae= accessor_element
                    	    {
                    	    pushFollow(FOLLOW_accessor_element_in_fact_expression1823);
                    	    ae=accessor_element();
                    	    _fsp--;

                    	    exprList.add(ae);

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
                    	retval.descr = factory.createAccessorPath(exprList);	

                    }
                    break;
                case 23 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:333:4: s= STRING
                    {
                    s=(DroolsTree)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_fact_expression1838); 
                    	retval.descr = factory.createStringLiteralRestriction(s);	

                    }
                    break;
                case 24 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:335:4: i= INT
                    {
                    i=(DroolsTree)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_fact_expression1848); 
                    	retval.descr = factory.createIntLiteralRestriction(i);	

                    }
                    break;
                case 25 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:337:4: f= FLOAT
                    {
                    f=(DroolsTree)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_fact_expression1858); 
                    	retval.descr = factory.createFloatLiteralRestriction(f);	

                    }
                    break;
                case 26 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:339:4: b= BOOL
                    {
                    b=(DroolsTree)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_fact_expression1868); 
                    	retval.descr = factory.createBoolLiteralRestriction(b);	

                    }
                    break;
                case 27 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:341:4: n= NULL
                    {
                    n=(DroolsTree)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_fact_expression1878); 
                    	retval.descr = factory.createNullLiteralRestriction(n);	

                    }
                    break;
                case 28 :
                    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:343:4: pc= VT_PAREN_CHUNK
                    {
                    pc=(DroolsTree)input.LT(1);
                    match(input,VT_PAREN_CHUNK,FOLLOW_VT_PAREN_CHUNK_in_fact_expression1888); 
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
    // $ANTLR end fact_expression


    // $ANTLR start field_element
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:347:1: field_element returns [FieldConstraintDescr element] : ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) ;
    public final FieldConstraintDescr field_element() throws RecognitionException {
        FieldConstraintDescr element = null;

        BaseDescr ae = null;



        	List<BaseDescr> aeList = new LinkedList<BaseDescr>();

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:350:3: ( ^( VT_ACCESSOR_PATH (ae= accessor_element )+ ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:350:5: ^( VT_ACCESSOR_PATH (ae= accessor_element )+ )
            {
            match(input,VT_ACCESSOR_PATH,FOLLOW_VT_ACCESSOR_PATH_in_field_element1910); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:350:24: (ae= accessor_element )+
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
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:350:25: ae= accessor_element
            	    {
            	    pushFollow(FOLLOW_accessor_element_in_field_element1915);
            	    ae=accessor_element();
            	    _fsp--;

            	    aeList.add(ae);

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
    // $ANTLR end field_element


    // $ANTLR start accessor_element
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:354:1: accessor_element returns [BaseDescr element] : ^( VT_ACCESSOR_ELEMENT id= ID (sc+= VT_SQUARE_CHUNK )* ) ;
    public final BaseDescr accessor_element() throws RecognitionException {
        BaseDescr element = null;

        DroolsTree id=null;
        DroolsTree sc=null;
        List list_sc=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:355:2: ( ^( VT_ACCESSOR_ELEMENT id= ID (sc+= VT_SQUARE_CHUNK )* ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:355:4: ^( VT_ACCESSOR_ELEMENT id= ID (sc+= VT_SQUARE_CHUNK )* )
            {
            match(input,VT_ACCESSOR_ELEMENT,FOLLOW_VT_ACCESSOR_ELEMENT_in_accessor_element1939); 

            match(input, Token.DOWN, null); 
            id=(DroolsTree)input.LT(1);
            match(input,ID,FOLLOW_ID_in_accessor_element1943); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:355:34: (sc+= VT_SQUARE_CHUNK )*
            loop49:
            do {
                int alt49=2;
                int LA49_0 = input.LA(1);

                if ( (LA49_0==VT_SQUARE_CHUNK) ) {
                    alt49=1;
                }


                switch (alt49) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:355:34: sc+= VT_SQUARE_CHUNK
            	    {
            	    sc=(DroolsTree)input.LT(1);
            	    match(input,VT_SQUARE_CHUNK,FOLLOW_VT_SQUARE_CHUNK_in_accessor_element1947); 
            	    if (list_sc==null) list_sc=new ArrayList();
            	    list_sc.add(sc);


            	    }
            	    break;

            	default :
            	    break loop49;
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
    // $ANTLR end accessor_element


    // $ANTLR start pattern_type
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:359:1: pattern_type returns [BaseDescr dataType] : ^( VT_PATTERN_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) ;
    public final BaseDescr pattern_type() throws RecognitionException {
        BaseDescr dataType = null;

        DroolsTree idList=null;
        DroolsTree rightList=null;
        List list_idList=null;
        List list_rightList=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:360:2: ( ^( VT_PATTERN_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:360:4: ^( VT_PATTERN_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* )
            {
            match(input,VT_PATTERN_TYPE,FOLLOW_VT_PATTERN_TYPE_in_pattern_type1968); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:360:28: (idList+= ID )+
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
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:360:28: idList+= ID
            	    {
            	    idList=(DroolsTree)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_pattern_type1972); 
            	    if (list_idList==null) list_idList=new ArrayList();
            	    list_idList.add(idList);


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

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:360:34: ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);

                if ( (LA51_0==LEFT_SQUARE) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:360:35: LEFT_SQUARE rightList+= RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_pattern_type1976); 
            	    rightList=(DroolsTree)input.LT(1);
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_pattern_type1980); 
            	    if (list_rightList==null) list_rightList=new ArrayList();
            	    list_rightList.add(rightList);


            	    }
            	    break;

            	default :
            	    break loop51;
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
    // $ANTLR end pattern_type


    // $ANTLR start data_type
    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:364:1: data_type returns [BaseDescr dataType] : ^( VT_DATA_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) ;
    public final BaseDescr data_type() throws RecognitionException {
        BaseDescr dataType = null;

        DroolsTree idList=null;
        DroolsTree rightList=null;
        List list_idList=null;
        List list_rightList=null;

        try {
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:365:2: ( ^( VT_DATA_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* ) )
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:365:4: ^( VT_DATA_TYPE (idList+= ID )+ ( LEFT_SQUARE rightList+= RIGHT_SQUARE )* )
            {
            match(input,VT_DATA_TYPE,FOLLOW_VT_DATA_TYPE_in_data_type2002); 

            match(input, Token.DOWN, null); 
            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:365:25: (idList+= ID )+
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
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:365:25: idList+= ID
            	    {
            	    idList=(DroolsTree)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_data_type2006); 
            	    if (list_idList==null) list_idList=new ArrayList();
            	    list_idList.add(idList);


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

            // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:365:31: ( LEFT_SQUARE rightList+= RIGHT_SQUARE )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);

                if ( (LA53_0==LEFT_SQUARE) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools/drools-compiler/src/main/resources/org/drools/lang/DescrBuilderTree.g:365:32: LEFT_SQUARE rightList+= RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_data_type2010); 
            	    rightList=(DroolsTree)input.LT(1);
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_data_type2014); 
            	    if (list_rightList==null) list_rightList=new ArrayList();
            	    list_rightList.add(rightList);


            	    }
            	    break;

            	default :
            	    break loop53;
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
    // $ANTLR end data_type


 

    public static final BitSet FOLLOW_VT_COMPILATION_UNIT_in_compilation_unit49 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_package_statement_in_compilation_unit51 = new BitSet(new long[]{0xB67FF80000000028L,0x0000000000000001L});
    public static final BitSet FOLLOW_statement_in_compilation_unit53 = new BitSet(new long[]{0xB67FF80000000028L,0x0000000000000001L});
    public static final BitSet FOLLOW_VK_PACKAGE_in_package_statement71 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_package_id_in_package_statement75 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PACKAGE_ID_in_package_id102 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_package_id106 = new BitSet(new long[]{0x0000000000000008L,0x0000000000400000L});
    public static final BitSet FOLLOW_rule_attribute_in_statement124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VK_IMPORT_in_import_statement216 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_import_name_in_import_statement220 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FUNCTION_IMPORT_in_function_import_statement242 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_FUNCTION_in_function_import_statement244 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement248 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_IMPORT_ID_in_import_name267 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_import_name271 = new BitSet(new long[]{0x0000000000000008L,0x0000000001400000L});
    public static final BitSet FOLLOW_DOT_STAR_in_import_name276 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_GLOBAL_in_global299 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_global303 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_VT_GLOBAL_ID_in_global307 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_FUNCTION_in_function329 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_function333 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_VT_FUNCTION_ID_in_function338 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_parameters_in_function342 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_VT_CURLY_CHUNK_in_function346 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_TEMPLATE_in_template371 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_TEMPLATE_ID_in_template375 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_template_slot_in_template384 = new BitSet(new long[]{0x0000000000004000L,0x0000000002000000L});
    public static final BitSet FOLLOW_END_in_template392 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_SLOT_in_template_slot412 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_data_type_in_template_slot416 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_VT_SLOT_ID_in_template_slot420 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_QUERY_in_query442 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_QUERY_ID_in_query446 = new BitSet(new long[]{0x0000040000100000L});
    public static final BitSet FOLLOW_parameters_in_query450 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_lhs_block_in_query455 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_END_in_query459 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_RULE_in_rule481 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_RULE_ID_in_rule485 = new BitSet(new long[]{0x0100000000018000L});
    public static final BitSet FOLLOW_rule_attributes_in_rule489 = new BitSet(new long[]{0x0100000000010000L});
    public static final BitSet FOLLOW_when_part_in_rule494 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_VT_RHS_CHUNK_in_rule499 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_WHEN_in_when_part518 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_lhs_block_in_when_part522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_RULE_ATTRIBUTES_in_rule_attributes544 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_ATTRIBUTES_in_rule_attributes546 = new BitSet(new long[]{0x007FF80000000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes552 = new BitSet(new long[]{0x007FF80000000008L});
    public static final BitSet FOLLOW_VT_PARAM_LIST_in_parameters576 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_param_definition_in_parameters581 = new BitSet(new long[]{0x0000001000000008L,0x0000000000400000L});
    public static final BitSet FOLLOW_data_type_in_param_definition603 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_argument_in_param_definition608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_argument628 = new BitSet(new long[]{0x0000000000000002L,0x0000200000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument631 = new BitSet(new long[]{0x0000000000000000L,0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument635 = new BitSet(new long[]{0x0000000000000002L,0x0000200000000000L});
    public static final BitSet FOLLOW_VK_SALIENCE_in_rule_attribute659 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INT_in_rule_attribute664 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_rule_attribute668 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_NO_LOOP_in_rule_attribute679 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute683 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_AGENDA_GROUP_in_rule_attribute695 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute699 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DURATION_in_rule_attribute710 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INT_in_rule_attribute714 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ACTIVATION_GROUP_in_rule_attribute726 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute730 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_AUTO_FOCUS_in_rule_attribute740 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute744 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DATE_EFFECTIVE_in_rule_attribute755 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute759 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DATE_EXPIRES_in_rule_attribute769 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute773 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ENABLED_in_rule_attribute783 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute787 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_RULEFLOW_GROUP_in_rule_attribute797 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute801 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_LOCK_ON_ACTIVE_in_rule_attribute811 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_in_rule_attribute815 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_DIALECT_in_rule_attribute825 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_in_rule_attribute829 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_AND_IMPLICIT_in_lhs_block854 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs_block859 = new BitSet(new long[]{0x0000000021E00008L,0x0000000000007102L});
    public static final BitSet FOLLOW_VT_OR_PREFIX_in_lhs885 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs890 = new BitSet(new long[]{0x0000000021E00008L,0x0000000000007102L});
    public static final BitSet FOLLOW_VT_OR_INFIX_in_lhs906 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs910 = new BitSet(new long[]{0x0000000021E00000L,0x0000000000007102L});
    public static final BitSet FOLLOW_lhs_in_lhs914 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_AND_PREFIX_in_lhs926 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs931 = new BitSet(new long[]{0x0000000021E00008L,0x0000000000007102L});
    public static final BitSet FOLLOW_VT_AND_INFIX_in_lhs947 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs951 = new BitSet(new long[]{0x0000000021E00000L,0x0000000000007102L});
    public static final BitSet FOLLOW_lhs_in_lhs955 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EXISTS_in_lhs967 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs971 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_NOT_in_lhs983 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs987 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EVAL_in_lhs999 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_lhs1003 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_FORALL_in_lhs1015 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_lhs1020 = new BitSet(new long[]{0x0000000021E00008L,0x0000000000007102L});
    public static final BitSet FOLLOW_VK_FROM_in_lhs1034 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs1038 = new BitSet(new long[]{0x0000000008000000L,0x0000000000108080L});
    public static final BitSet FOLLOW_from_elements_in_lhs1042 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs1053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VK_ACCUMULATE_in_from_elements1074 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_from_elements1078 = new BitSet(new long[]{0x0000000006000000L});
    public static final BitSet FOLLOW_accumulate_parts_in_from_elements1088 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_COLLECT_in_from_elements1101 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_lhs_in_from_elements1105 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ENTRY_POINT_in_from_elements1117 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_ENTRYPOINT_ID_in_from_elements1121 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_from_source_clause_in_from_elements1132 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accumulate_init_clause_in_accumulate_parts1153 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accumulate_id_clause_in_accumulate_parts1164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_ACCUMULATE_INIT_CLAUSE_in_accumulate_init_clause1187 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_INIT_in_accumulate_init_clause1196 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1200 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_ACTION_in_accumulate_init_clause1208 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1212 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_accumulate_init_reverse_clause_in_accumulate_init_clause1221 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_VK_RESULT_in_accumulate_init_clause1228 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_clause1232 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_REVERSE_in_accumulate_init_reverse_clause1255 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_init_reverse_clause1259 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ACCUMULATE_ID_CLAUSE_in_accumulate_id_clause1281 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_accumulate_id_clause1285 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_accumulate_id_clause1289 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FROM_SOURCE_in_from_source_clause1311 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_from_source_clause1315 = new BitSet(new long[]{0x0000000010080008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_from_source_clause1319 = new BitSet(new long[]{0x0000000010000008L});
    public static final BitSet FOLLOW_expression_chain_in_from_source_clause1328 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_EXPRESSION_CHAIN_in_expression_chain1347 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_expression_chain1351 = new BitSet(new long[]{0x00000000100C0008L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_expression_chain1355 = new BitSet(new long[]{0x0000000010080008L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_expression_chain1360 = new BitSet(new long[]{0x0000000010000008L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain1368 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_PATTERN_in_lhs_pattern1386 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_lhs_pattern1390 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FACT_in_fact_expression1413 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_pattern_type_in_fact_expression1417 = new BitSet(new long[]{0x00000007C0080048L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1422 = new BitSet(new long[]{0x00000007C0080048L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_VT_FACT_BINDING_in_fact_expression1436 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_LABEL_in_fact_expression1440 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1444 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FACT_OR_in_fact_expression1456 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1460 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1464 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_FIELD_in_fact_expression1475 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_field_element_in_fact_expression1479 = new BitSet(new long[]{0x00000007C0080048L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1483 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_BIND_FIELD_in_fact_expression1494 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_LABEL_in_fact_expression1498 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1502 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EVAL_in_fact_expression1513 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_fact_expression1517 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EQUAL_in_fact_expression1530 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1534 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_EQUAL_in_fact_expression1546 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1550 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GREATER_in_fact_expression1562 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1566 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GREATER_EQUAL_in_fact_expression1578 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1582 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LESS_in_fact_expression1594 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1598 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LESS_EQUAL_in_fact_expression1610 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1614 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_CONTAINS_in_fact_expression1626 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1630 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1635 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_EXCLUDES_in_fact_expression1647 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1651 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1656 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_MATCHES_in_fact_expression1668 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1672 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1677 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_SOUNDSLIKE_in_fact_expression1689 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1693 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1698 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_MEMBEROF_in_fact_expression1710 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1714 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1719 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_fact_expression1731 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1735 = new BitSet(new long[]{0x00000007C00C0040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_fact_expression1740 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1745 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VK_IN_in_fact_expression1756 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VK_NOT_in_fact_expression1760 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1766 = new BitSet(new long[]{0x00000007C0080048L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_fact_expression1781 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1785 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1789 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_fact_expression1799 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1803 = new BitSet(new long[]{0x00000007C0080040L,0x00001BF78440027EL});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1807 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VT_ACCESSOR_PATH_in_fact_expression1818 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_accessor_element_in_fact_expression1823 = new BitSet(new long[]{0x0000000800000008L});
    public static final BitSet FOLLOW_STRING_in_fact_expression1838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_fact_expression1848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_fact_expression1858 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_fact_expression1868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_fact_expression1878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_PAREN_CHUNK_in_fact_expression1888 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VT_ACCESSOR_PATH_in_field_element1910 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_accessor_element_in_field_element1915 = new BitSet(new long[]{0x0000000800000008L});
    public static final BitSet FOLLOW_VT_ACCESSOR_ELEMENT_in_accessor_element1939 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_accessor_element1943 = new BitSet(new long[]{0x0000000000040008L});
    public static final BitSet FOLLOW_VT_SQUARE_CHUNK_in_accessor_element1947 = new BitSet(new long[]{0x0000000000040008L});
    public static final BitSet FOLLOW_VT_PATTERN_TYPE_in_pattern_type1968 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_pattern_type1972 = new BitSet(new long[]{0x0000000000000008L,0x0000200000400000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_pattern_type1976 = new BitSet(new long[]{0x0000000000000000L,0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_pattern_type1980 = new BitSet(new long[]{0x0000000000000008L,0x0000200000000000L});
    public static final BitSet FOLLOW_VT_DATA_TYPE_in_data_type2002 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_data_type2006 = new BitSet(new long[]{0x0000000000000008L,0x0000200000400000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_data_type2010 = new BitSet(new long[]{0x0000000000000000L,0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_data_type2014 = new BitSet(new long[]{0x0000000000000008L,0x0000200000000000L});

}