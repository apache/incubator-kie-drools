// $ANTLR 3.0ea8 /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g 2006-03-17 23:38:02

	package org.drools.lang;
	import java.util.List;
	import java.util.ArrayList;
	import java.util.Iterator;
	import org.drools.lang.descr.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class RuleParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "EOL", "ID", "INT", "STRING", "FLOAT", "MISC", "WS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "\'package\'", "\';\'", "\'import\'", "\'expander\'", "\'global\'", "\'function\'", "\'(\'", "\',\'", "\')\'", "\'{\'", "\'}\'", "\'query\'", "\'end\'", "\'rule\'", "\'when\'", "\':\'", "\'then\'", "\'options\'", "\'salience\'", "\'no-loop\'", "\'>\'", "\'or\'", "\'==\'", "\'>=\'", "\'<\'", "\'<=\'", "\'!=\'", "\'contains\'", "\'matches\'", "\'->\'", "\'||\'", "\'and\'", "\'&&\'", "\'exists\'", "\'not\'", "\'eval\'", "\'.\'", "\'use\'"
    };
    public static final int INT=6;
    public static final int WS=10;
    public static final int EOF=-1;
    public static final int MISC=9;
    public static final int EOL=4;
    public static final int STRING=7;
    public static final int FLOAT=8;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=11;
    public static final int MULTI_LINE_COMMENT=13;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=12;
    public static final int ID=5;
        public RuleParser(TokenStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }


    	private ExpanderResolver expanderResolver;
    	private Expander expander;

    	private PackageDescr packageDescr;
    	
    	public PackageDescr getPackageDescr() {
    		return packageDescr;
    	}
    	
    	public void setExpanderResolver(ExpanderResolver expanderResolver) {
    		this.expanderResolver = expanderResolver;
    	}
    	
    	public ExpanderResolver getExpanderResolver() {
    		return expanderResolver;
    	}
    	
    	private PatternDescr runExpander(String text) throws RecognitionException {
    		String expanded = expander.expand( text, this );
    		
    		return reparseLhs( expanded );
    	}
    	
    	private PatternDescr reparseLhs(String text) throws RecognitionException {
    		CharStream charStream = new ANTLRStringStream( text );
    		RuleParserLexer lexer = new RuleParserLexer( charStream );
    		TokenStream tokenStream = new CommonTokenStream( lexer );
    		RuleParser parser = new RuleParser( tokenStream );
    		
    		return parser.lhs();
    	}



    // $ANTLR start opt_eol
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:49:1: opt_eol : ( EOL )* ;
    public void opt_eol() throws RecognitionException {   
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:50:17: ( ( EOL )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:50:17: ( EOL )*
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:50:17: ( EOL )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);
                if ( LA1_0==EOL ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:50:17: EOL
            	    {
            	    match(input,EOL,FOLLOW_EOL_in_opt_eol40); 

            	    }
            	    break;

            	default :
            	    break loop1;
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
    // $ANTLR end opt_eol


    // $ANTLR start compilation_unit
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:53:1: compilation_unit : opt_eol prolog (r= rule | q= query )* ;
    public void compilation_unit() throws RecognitionException {   
        RuleDescr r = null;

        QueryDescr q = null;


        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:54:17: ( opt_eol prolog (r= rule | q= query )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:54:17: opt_eol prolog (r= rule | q= query )*
            {
            following.push(FOLLOW_opt_eol_in_compilation_unit53);
            opt_eol();
            following.pop();

            following.push(FOLLOW_prolog_in_compilation_unit57);
            prolog();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:56:17: (r= rule | q= query )*
            loop2:
            do {
                int alt2=3;
                alt2 = dfa2.predict(input); 
                switch (alt2) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:56:25: r= rule
            	    {
            	    following.push(FOLLOW_rule_in_compilation_unit66);
            	    r=rule();
            	    following.pop();

            	    this.packageDescr.addRule( r ); 

            	    }
            	    break;
            	case 2 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:57:25: q= query
            	    {
            	    following.push(FOLLOW_query_in_compilation_unit79);
            	    q=query();
            	    following.pop();

            	    this.packageDescr.addRule( q ); 

            	    }
            	    break;

            	default :
            	    break loop2;
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
    // $ANTLR end compilation_unit


    // $ANTLR start prolog
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:61:1: prolog : opt_eol (name= package_statement )? ( import_statement )* ( expander )? ( global )* ( function )* opt_eol ;
    public void prolog() throws RecognitionException {   
        String name = null;



        		String packageName = "";
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:65:17: ( opt_eol (name= package_statement )? ( import_statement )* ( expander )? ( global )* ( function )* opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:65:17: opt_eol (name= package_statement )? ( import_statement )* ( expander )? ( global )* ( function )* opt_eol
            {
            following.push(FOLLOW_opt_eol_in_prolog104);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:66:17: (name= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( LA3_0==14 ) {
                alt3=1;
            }
            else if ( LA3_0==-1||LA3_0==EOL||(LA3_0>=16 && LA3_0<=19)||LA3_0==25||LA3_0==27 ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("66:17: (name= package_statement )?", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:66:19: name= package_statement
                    {
                    following.push(FOLLOW_package_statement_in_prolog112);
                    name=package_statement();
                    following.pop();

                     packageName = name; 

                    }
                    break;

            }

             
            			this.packageDescr = new PackageDescr( name ); 
            		
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:70:17: ( import_statement )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);
                if ( LA4_0==16 ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:70:17: import_statement
            	    {
            	    following.push(FOLLOW_import_statement_in_prolog125);
            	    import_statement();
            	    following.pop();


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:71:17: ( expander )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( LA5_0==17 ) {
                alt5=1;
            }
            else if ( LA5_0==-1||LA5_0==EOL||(LA5_0>=18 && LA5_0<=19)||LA5_0==25||LA5_0==27 ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("71:17: ( expander )?", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:71:17: expander
                    {
                    following.push(FOLLOW_expander_in_prolog130);
                    expander();
                    following.pop();


                    }
                    break;

            }

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:72:17: ( global )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);
                if ( LA6_0==18 ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:72:17: global
            	    {
            	    following.push(FOLLOW_global_in_prolog136);
            	    global();
            	    following.pop();


            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:73:17: ( function )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);
                if ( LA7_0==19 ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:73:17: function
            	    {
            	    following.push(FOLLOW_function_in_prolog141);
            	    function();
            	    following.pop();


            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_prolog146);
            opt_eol();
            following.pop();


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
    // $ANTLR end prolog


    // $ANTLR start package_statement
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:77:1: package_statement returns [String packageName] : 'package' opt_eol name= dotted_name ( ';' )? opt_eol ;
    public String package_statement() throws RecognitionException {   
        String packageName;
        String name = null;



        		packageName = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:82:17: ( 'package' opt_eol name= dotted_name ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:82:17: 'package' opt_eol name= dotted_name ( ';' )? opt_eol
            {
            match(input,14,FOLLOW_14_in_package_statement170); 
            following.push(FOLLOW_opt_eol_in_package_statement172);
            opt_eol();
            following.pop();

            following.push(FOLLOW_dotted_name_in_package_statement176);
            name=dotted_name();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:82:52: ( ';' )?
            int alt8=2;
            int LA8_0 = input.LA(1);
            if ( LA8_0==15 ) {
                alt8=1;
            }
            else if ( LA8_0==-1||LA8_0==EOL||(LA8_0>=16 && LA8_0<=19)||LA8_0==25||LA8_0==27 ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("82:52: ( \';\' )?", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:82:52: ';'
                    {
                    match(input,15,FOLLOW_15_in_package_statement178); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_package_statement181);
            opt_eol();
            following.pop();


            			packageName = name;
            		

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


    // $ANTLR start import_statement
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:88:1: import_statement : 'import' opt_eol name= dotted_name ( ';' )? opt_eol ;
    public void import_statement() throws RecognitionException {   
        String name = null;


        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:89:17: ( 'import' opt_eol name= dotted_name ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:89:17: 'import' opt_eol name= dotted_name ( ';' )? opt_eol
            {
            match(input,16,FOLLOW_16_in_import_statement197); 
            following.push(FOLLOW_opt_eol_in_import_statement199);
            opt_eol();
            following.pop();

            following.push(FOLLOW_dotted_name_in_import_statement203);
            name=dotted_name();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:89:51: ( ';' )?
            int alt9=2;
            int LA9_0 = input.LA(1);
            if ( LA9_0==15 ) {
                alt9=1;
            }
            else if ( LA9_0==-1||LA9_0==EOL||(LA9_0>=16 && LA9_0<=19)||LA9_0==25||LA9_0==27 ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("89:51: ( \';\' )?", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:89:51: ';'
                    {
                    match(input,15,FOLLOW_15_in_import_statement205); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_import_statement208);
            opt_eol();
            following.pop();


            			packageDescr.addImport( name );
            		

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


    // $ANTLR start expander
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:95:1: expander : 'expander' (name= dotted_name )? ( ';' )? opt_eol ;
    public void expander() throws RecognitionException {   
        String name = null;



        		String config=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:99:17: ( 'expander' (name= dotted_name )? ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:99:17: 'expander' (name= dotted_name )? ( ';' )? opt_eol
            {
            match(input,17,FOLLOW_17_in_expander230); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:99:28: (name= dotted_name )?
            int alt10=2;
            int LA10_0 = input.LA(1);
            if ( LA10_0==ID ) {
                alt10=1;
            }
            else if ( LA10_0==-1||LA10_0==EOL||LA10_0==15||(LA10_0>=18 && LA10_0<=19)||LA10_0==25||LA10_0==27 ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("99:28: (name= dotted_name )?", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:99:29: name= dotted_name
                    {
                    following.push(FOLLOW_dotted_name_in_expander235);
                    name=dotted_name();
                    following.pop();


                    }
                    break;

            }

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:99:48: ( ';' )?
            int alt11=2;
            int LA11_0 = input.LA(1);
            if ( LA11_0==15 ) {
                alt11=1;
            }
            else if ( LA11_0==-1||LA11_0==EOL||(LA11_0>=18 && LA11_0<=19)||LA11_0==25||LA11_0==27 ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("99:48: ( \';\' )?", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:99:48: ';'
                    {
                    match(input,15,FOLLOW_15_in_expander239); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_expander242);
            opt_eol();
            following.pop();


            			expander = expanderResolver.get( name, config );
            		

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
    // $ANTLR end expander


    // $ANTLR start global
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:105:1: global : 'global' type= dotted_name id= ID ( ';' )? opt_eol ;
    public void global() throws RecognitionException {   
        Token id=null;
        String type = null;



        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:109:17: ( 'global' type= dotted_name id= ID ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:109:17: 'global' type= dotted_name id= ID ( ';' )? opt_eol
            {
            match(input,18,FOLLOW_18_in_global266); 
            following.push(FOLLOW_dotted_name_in_global270);
            type=dotted_name();
            following.pop();

            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_global274); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:109:49: ( ';' )?
            int alt12=2;
            int LA12_0 = input.LA(1);
            if ( LA12_0==15 ) {
                alt12=1;
            }
            else if ( LA12_0==-1||LA12_0==EOL||(LA12_0>=18 && LA12_0<=19)||LA12_0==25||LA12_0==27 ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("109:49: ( \';\' )?", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:109:49: ';'
                    {
                    match(input,15,FOLLOW_15_in_global276); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_global279);
            opt_eol();
            following.pop();


            			packageDescr.addGlobal( id.getText(), type );
            		

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:115:1: function : 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol ;
    public void function() throws RecognitionException {   
        Token name=null;
        Token paramName=null;
        String retType = null;

        String paramType = null;

        String body = null;



        		FunctionDescr f = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:120:17: ( 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:120:17: 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol
            {
            match(input,19,FOLLOW_19_in_function303); 
            following.push(FOLLOW_opt_eol_in_function305);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:120:36: (retType= dotted_name )?
            int alt13=2;
            alt13 = dfa13.predict(input); 
            switch (alt13) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:120:37: retType= dotted_name
                    {
                    following.push(FOLLOW_dotted_name_in_function310);
                    retType=dotted_name();
                    following.pop();


                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_function314);
            opt_eol();
            following.pop();

            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_function318); 
            following.push(FOLLOW_opt_eol_in_function320);
            opt_eol();
            following.pop();


            			System.err.println( "function :: " + name.getText() );
            			f = new FunctionDescr( name.getText(), retType );
            		
            match(input,20,FOLLOW_20_in_function329); 
            following.push(FOLLOW_opt_eol_in_function331);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:126:25: ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )?
            int alt17=2;
            int LA17_0 = input.LA(1);
            if ( (LA17_0>=EOL && LA17_0<=ID) ) {
                alt17=1;
            }
            else if ( LA17_0==22 ) {
                alt17=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("126:25: ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( \',\' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )?", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:126:33: (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )*
                    {
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:126:33: (paramType= dotted_name )?
                    int alt14=2;
                    alt14 = dfa14.predict(input); 
                    switch (alt14) {
                        case 1 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:126:34: paramType= dotted_name
                            {
                            following.push(FOLLOW_dotted_name_in_function341);
                            paramType=dotted_name();
                            following.pop();


                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_function345);
                    opt_eol();
                    following.pop();

                    paramName=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_function349); 
                    following.push(FOLLOW_opt_eol_in_function351);
                    opt_eol();
                    following.pop();


                    					f.addParameter( paramType, paramName.getText() );
                    				
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:130:33: ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )*
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);
                        if ( LA16_0==21 ) {
                            alt16=1;
                        }


                        switch (alt16) {
                    	case 1 :
                    	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:130:41: ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol
                    	    {
                    	    match(input,21,FOLLOW_21_in_function365); 
                    	    following.push(FOLLOW_opt_eol_in_function367);
                    	    opt_eol();
                    	    following.pop();

                    	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:130:53: (paramType= dotted_name )?
                    	    int alt15=2;
                    	    alt15 = dfa15.predict(input); 
                    	    switch (alt15) {
                    	        case 1 :
                    	            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:130:54: paramType= dotted_name
                    	            {
                    	            following.push(FOLLOW_dotted_name_in_function372);
                    	            paramType=dotted_name();
                    	            following.pop();


                    	            }
                    	            break;

                    	    }

                    	    following.push(FOLLOW_opt_eol_in_function376);
                    	    opt_eol();
                    	    following.pop();

                    	    paramName=(Token)input.LT(1);
                    	    match(input,ID,FOLLOW_ID_in_function380); 
                    	    following.push(FOLLOW_opt_eol_in_function382);
                    	    opt_eol();
                    	    following.pop();


                    	    						f.addParameter( paramType, paramName.getText() );
                    	    					

                    	    }
                    	    break;

                    	default :
                    	    break loop16;
                        }
                    } while (true);


                    }
                    break;

            }

            match(input,22,FOLLOW_22_in_function407); 
            following.push(FOLLOW_opt_eol_in_function411);
            opt_eol();
            following.pop();

            match(input,23,FOLLOW_23_in_function415); 
            following.push(FOLLOW_curly_chunk_in_function422);
            body=curly_chunk();
            following.pop();


            				f.setText( body );
            			
            match(input,24,FOLLOW_24_in_function431); 

            			packageDescr.addFunction( f );
            		
            following.push(FOLLOW_opt_eol_in_function439);
            opt_eol();
            following.pop();


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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:151:1: query returns [QueryDescr query] : opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol ;
    public QueryDescr query() throws RecognitionException {   
        QueryDescr query;
        Token loc=null;
        String queryName = null;



        		query = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:156:17: ( opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:156:17: opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol
            {
            following.push(FOLLOW_opt_eol_in_query463);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,25,FOLLOW_25_in_query469); 
            following.push(FOLLOW_word_in_query473);
            queryName=word();
            following.pop();

            following.push(FOLLOW_opt_eol_in_query475);
            opt_eol();
            following.pop();

             
            			query = new QueryDescr( queryName, null ); 
            			query.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            			AndDescr lhs = new AndDescr(); query.setLhs( lhs ); 
            			lhs.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:164:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
            int alt18=2;
            switch ( input.LA(1) ) {
            case EOL:
            case INT:
            case STRING:
            case FLOAT:
            case MISC:
            case WS:
            case SH_STYLE_SINGLE_LINE_COMMENT:
            case C_STYLE_SINGLE_LINE_COMMENT:
            case MULTI_LINE_COMMENT:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 50:
            case 51:
                alt18=1;
                break;
            case 20:
                int LA18_2 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("164:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 2, input);

                    throw nvae;
                }
                break;
            case 26:
                int LA18_4 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("164:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 4, input);

                    throw nvae;
                }
                break;
            case 47:
                int LA18_5 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("164:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 5, input);

                    throw nvae;
                }
                break;
            case 48:
                int LA18_6 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("164:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 6, input);

                    throw nvae;
                }
                break;
            case 49:
                int LA18_7 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("164:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 7, input);

                    throw nvae;
                }
                break;
            case ID:
                int LA18_8 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("164:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 8, input);

                    throw nvae;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("164:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:165:25: {...}? expander_lhs_block[lhs]
                    {
                    if ( !( expander != null ) ) {
                        throw new FailedPredicateException(input, "query", " expander != null ");
                    }
                    following.push(FOLLOW_expander_lhs_block_in_query491);
                    expander_lhs_block(lhs);
                    following.pop();


                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:166:27: normal_lhs_block[lhs]
                    {
                    following.push(FOLLOW_normal_lhs_block_in_query499);
                    normal_lhs_block(lhs);
                    following.pop();


                    }
                    break;

            }

            match(input,26,FOLLOW_26_in_query514); 
            following.push(FOLLOW_opt_eol_in_query516);
            opt_eol();
            following.pop();


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return query;
    }
    // $ANTLR end query


    // $ANTLR start rule
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:172:1: rule returns [RuleDescr rule] : opt_eol loc= 'rule' ruleName= word opt_eol (a= rule_options )? (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* 'end' opt_eol ;
    public RuleDescr rule() throws RecognitionException {   
        RuleDescr rule;
        Token loc=null;
        Token any=null;
        String ruleName = null;

        List a = null;



        		rule = null;
        		String consequence = "";
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:178:17: ( opt_eol loc= 'rule' ruleName= word opt_eol (a= rule_options )? (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* 'end' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:178:17: opt_eol loc= 'rule' ruleName= word opt_eol (a= rule_options )? (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* 'end' opt_eol
            {
            following.push(FOLLOW_opt_eol_in_rule539);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,27,FOLLOW_27_in_rule545); 
            following.push(FOLLOW_word_in_rule549);
            ruleName=word();
            following.pop();

            following.push(FOLLOW_opt_eol_in_rule551);
            opt_eol();
            following.pop();

             
            			rule = new RuleDescr( ruleName, null ); 
            			rule.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:184:17: (a= rule_options )?
            int alt19=2;
            int LA19_0 = input.LA(1);
            if ( LA19_0==31 ) {
                alt19=1;
            }
            else if ( LA19_0==28||LA19_0==30 ) {
                alt19=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("184:17: (a= rule_options )?", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:184:25: a= rule_options
                    {
                    following.push(FOLLOW_rule_options_in_rule564);
                    a=rule_options();
                    following.pop();


                    				rule.setAttributes( a );
                    			

                    }
                    break;

            }

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:189:17: (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?
            int alt22=2;
            int LA22_0 = input.LA(1);
            if ( LA22_0==28 ) {
                alt22=1;
            }
            else if ( LA22_0==30 ) {
                alt22=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("189:17: (loc= \'when\' ( \':\' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:189:25: loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                    {
                    loc=(Token)input.LT(1);
                    match(input,28,FOLLOW_28_in_rule582); 
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:189:36: ( ':' )?
                    int alt20=2;
                    int LA20_0 = input.LA(1);
                    if ( LA20_0==29 ) {
                        int LA20_1 = input.LA(2);
                        if ( !( expander != null ) ) {
                            alt20=1;
                        }
                        else if (  expander != null  ) {
                            alt20=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("189:36: ( \':\' )?", 20, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA20_0>=EOL && LA20_0<=28)||(LA20_0>=30 && LA20_0<=51) ) {
                        alt20=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("189:36: ( \':\' )?", 20, 0, input);

                        throw nvae;
                    }
                    switch (alt20) {
                        case 1 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:189:36: ':'
                            {
                            match(input,29,FOLLOW_29_in_rule584); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_rule587);
                    opt_eol();
                    following.pop();

                     
                    				AndDescr lhs = new AndDescr(); rule.setLhs( lhs ); 
                    				lhs.setLocation( loc.getLine(), loc.getCharPositionInLine() );
                    			
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:194:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                    int alt21=2;
                    switch ( input.LA(1) ) {
                    case EOL:
                    case INT:
                    case STRING:
                    case FLOAT:
                    case MISC:
                    case WS:
                    case SH_STYLE_SINGLE_LINE_COMMENT:
                    case C_STYLE_SINGLE_LINE_COMMENT:
                    case MULTI_LINE_COMMENT:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 21:
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    case 39:
                    case 40:
                    case 41:
                    case 42:
                    case 43:
                    case 44:
                    case 45:
                    case 46:
                    case 50:
                    case 51:
                        alt21=1;
                        break;
                    case 20:
                        int LA21_2 = input.LA(2);
                        if (  expander != null  ) {
                            alt21=1;
                        }
                        else if ( true ) {
                            alt21=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("194:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 2, input);

                            throw nvae;
                        }
                        break;
                    case 30:
                        int LA21_4 = input.LA(2);
                        if (  expander != null  ) {
                            alt21=1;
                        }
                        else if ( true ) {
                            alt21=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("194:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 4, input);

                            throw nvae;
                        }
                        break;
                    case 47:
                        int LA21_5 = input.LA(2);
                        if (  expander != null  ) {
                            alt21=1;
                        }
                        else if ( true ) {
                            alt21=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("194:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 5, input);

                            throw nvae;
                        }
                        break;
                    case 48:
                        int LA21_6 = input.LA(2);
                        if (  expander != null  ) {
                            alt21=1;
                        }
                        else if ( true ) {
                            alt21=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("194:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 6, input);

                            throw nvae;
                        }
                        break;
                    case 49:
                        int LA21_7 = input.LA(2);
                        if (  expander != null  ) {
                            alt21=1;
                        }
                        else if ( true ) {
                            alt21=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("194:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 7, input);

                            throw nvae;
                        }
                        break;
                    case ID:
                        int LA21_8 = input.LA(2);
                        if (  expander != null  ) {
                            alt21=1;
                        }
                        else if ( true ) {
                            alt21=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("194:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 8, input);

                            throw nvae;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("194:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 0, input);

                        throw nvae;
                    }

                    switch (alt21) {
                        case 1 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:195:33: {...}? expander_lhs_block[lhs]
                            {
                            if ( !( expander != null ) ) {
                                throw new FailedPredicateException(input, "rule", " expander != null ");
                            }
                            following.push(FOLLOW_expander_lhs_block_in_rule605);
                            expander_lhs_block(lhs);
                            following.pop();


                            }
                            break;
                        case 2 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:196:35: normal_lhs_block[lhs]
                            {
                            following.push(FOLLOW_normal_lhs_block_in_rule614);
                            normal_lhs_block(lhs);
                            following.pop();


                            }
                            break;

                    }


                    }
                    break;

            }

             System.err.println( "finished LHS?" ); 
            match(input,30,FOLLOW_30_in_rule639); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:201:24: ( ':' )?
            int alt23=2;
            int LA23_0 = input.LA(1);
            if ( LA23_0==29 ) {
                alt23=1;
            }
            else if ( (LA23_0>=EOL && LA23_0<=28)||(LA23_0>=30 && LA23_0<=51) ) {
                alt23=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("201:24: ( \':\' )?", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:201:24: ':'
                    {
                    match(input,29,FOLLOW_29_in_rule641); 

                    }
                    break;

            }

             System.err.println( "matched THEN" ); 
            following.push(FOLLOW_opt_eol_in_rule646);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:202:17: ( options {greedy=false; } : any= . )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);
                if ( LA24_0==26 ) {
                    alt24=2;
                }
                else if ( (LA24_0>=EOL && LA24_0<=25)||(LA24_0>=27 && LA24_0<=51) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:202:44: any= .
            	    {
            	    any=(Token)input.LT(1);
            	    matchAny(input); 

            	    				consequence = consequence + " " + any.getText();
            	    			

            	    }
            	    break;

            	default :
            	    break loop24;
                }
            } while (true);

             rule.setConsequence( consequence ); 
            match(input,26,FOLLOW_26_in_rule681); 
            following.push(FOLLOW_opt_eol_in_rule683);
            opt_eol();
            following.pop();


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return rule;
    }
    // $ANTLR end rule


    // $ANTLR start rule_options
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:212:1: rule_options returns [List options] : 'options' ( ':' )? opt_eol (a= rule_option opt_eol )* ;
    public List rule_options() throws RecognitionException {   
        List options;
        AttributeDescr a = null;



        		options = new ArrayList();
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:216:17: ( 'options' ( ':' )? opt_eol (a= rule_option opt_eol )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:216:17: 'options' ( ':' )? opt_eol (a= rule_option opt_eol )*
            {
            match(input,31,FOLLOW_31_in_rule_options706); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:216:27: ( ':' )?
            int alt25=2;
            int LA25_0 = input.LA(1);
            if ( LA25_0==29 ) {
                alt25=1;
            }
            else if ( LA25_0==EOL||LA25_0==28||LA25_0==30||(LA25_0>=32 && LA25_0<=33) ) {
                alt25=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("216:27: ( \':\' )?", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:216:27: ':'
                    {
                    match(input,29,FOLLOW_29_in_rule_options708); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_rule_options711);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:217:25: (a= rule_option opt_eol )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);
                if ( (LA26_0>=32 && LA26_0<=33) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:217:33: a= rule_option opt_eol
            	    {
            	    following.push(FOLLOW_rule_option_in_rule_options720);
            	    a=rule_option();
            	    following.pop();

            	    following.push(FOLLOW_opt_eol_in_rule_options722);
            	    opt_eol();
            	    following.pop();


            	    					options.add( a );
            	    				

            	    }
            	    break;

            	default :
            	    break loop26;
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
        return options;
    }
    // $ANTLR end rule_options


    // $ANTLR start rule_option
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:224:1: rule_option returns [AttributeDescr d] : (a= salience | a= no_loop );
    public AttributeDescr rule_option() throws RecognitionException {   
        AttributeDescr d;
        AttributeDescr a = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:229:25: (a= salience | a= no_loop )
            int alt27=2;
            int LA27_0 = input.LA(1);
            if ( LA27_0==32 ) {
                alt27=1;
            }
            else if ( LA27_0==33 ) {
                alt27=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("224:1: rule_option returns [AttributeDescr d] : (a= salience | a= no_loop );", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:229:25: a= salience
                    {
                    following.push(FOLLOW_salience_in_rule_option761);
                    a=salience();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:230:25: a= no_loop
                    {
                    following.push(FOLLOW_no_loop_in_rule_option771);
                    a=no_loop();
                    following.pop();

                     d = a; 

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
        return d;
    }
    // $ANTLR end rule_option


    // $ANTLR start salience
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:234:1: salience returns [AttributeDescr d ] : loc= 'salience' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr salience() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token i=null;


        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:239:17: (loc= 'salience' opt_eol i= INT ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:239:17: loc= 'salience' opt_eol i= INT ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,32,FOLLOW_32_in_salience804); 
            following.push(FOLLOW_opt_eol_in_salience806);
            opt_eol();
            following.pop();

            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience810); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:239:46: ( ';' )?
            int alt28=2;
            int LA28_0 = input.LA(1);
            if ( LA28_0==15 ) {
                alt28=1;
            }
            else if ( LA28_0==EOL||LA28_0==28||LA28_0==30||(LA28_0>=32 && LA28_0<=33) ) {
                alt28=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("239:46: ( \';\' )?", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:239:46: ';'
                    {
                    match(input,15,FOLLOW_15_in_salience812); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_salience815);
            opt_eol();
            following.pop();


            			d = new AttributeDescr( "salience", i.getText() );
            			d.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end salience


    // $ANTLR start no_loop
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:246:1: no_loop returns [AttributeDescr d] : loc= 'no-loop' ( ';' )? opt_eol ;
    public AttributeDescr no_loop() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;


        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:251:17: (loc= 'no-loop' ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:251:17: loc= 'no-loop' ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,33,FOLLOW_33_in_no_loop845); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:251:31: ( ';' )?
            int alt29=2;
            int LA29_0 = input.LA(1);
            if ( LA29_0==15 ) {
                alt29=1;
            }
            else if ( LA29_0==EOL||LA29_0==28||LA29_0==30||(LA29_0>=32 && LA29_0<=33) ) {
                alt29=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("251:31: ( \';\' )?", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:251:31: ';'
                    {
                    match(input,15,FOLLOW_15_in_no_loop847); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_no_loop850);
            opt_eol();
            following.pop();


            			d = new AttributeDescr( "no-loop", null );
            			d.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end no_loop


    // $ANTLR start normal_lhs_block
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:259:1: normal_lhs_block[AndDescr descrs] : (d= lhs )* ;
    public void normal_lhs_block(AndDescr descrs) throws RecognitionException {   
        PatternDescr d = null;


        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:261:17: ( (d= lhs )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:261:17: (d= lhs )*
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:261:17: (d= lhs )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);
                if ( LA30_0==ID||LA30_0==20||(LA30_0>=47 && LA30_0<=49) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:261:25: d= lhs
            	    {
            	    following.push(FOLLOW_lhs_in_normal_lhs_block874);
            	    d=lhs();
            	    following.pop();

            	     descrs.addDescr( d ); 

            	    }
            	    break;

            	default :
            	    break loop30;
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
    // $ANTLR end normal_lhs_block


    // $ANTLR start expander_lhs_block
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:266:1: expander_lhs_block[AndDescr descrs] : ( options {greedy=false; } : '>' d= lhs | ( options {greedy=false; } : text= paren_chunk EOL ) )* ;
    public void expander_lhs_block(AndDescr descrs) throws RecognitionException {   
        PatternDescr d = null;

        String text = null;


        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:269:17: ( ( options {greedy=false; } : '>' d= lhs | ( options {greedy=false; } : text= paren_chunk EOL ) )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:269:17: ( options {greedy=false; } : '>' d= lhs | ( options {greedy=false; } : text= paren_chunk EOL ) )*
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:269:17: ( options {greedy=false; } : '>' d= lhs | ( options {greedy=false; } : text= paren_chunk EOL ) )*
            loop31:
            do {
                int alt31=3;
                switch ( input.LA(1) ) {
                case 26:
                    alt31=3;
                    break;
                case 30:
                    alt31=3;
                    break;
                case 34:
                    alt31=1;
                    break;
                case EOL:
                case ID:
                case INT:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 27:
                case 28:
                case 29:
                case 31:
                case 32:
                case 33:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                    alt31=2;
                    break;

                }

                switch (alt31) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:270:25: '>' d= lhs
            	    {
            	    match(input,34,FOLLOW_34_in_expander_lhs_block914); 
            	    following.push(FOLLOW_lhs_in_expander_lhs_block918);
            	    d=lhs();
            	    following.pop();

            	     descrs.addDescr( d ); 

            	    }
            	    break;
            	case 2 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:272:25: ( options {greedy=false; } : text= paren_chunk EOL )
            	    {
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:272:25: ( options {greedy=false; } : text= paren_chunk EOL )
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:273:33: text= paren_chunk EOL
            	    {
            	    following.push(FOLLOW_paren_chunk_in_expander_lhs_block949);
            	    text=paren_chunk();
            	    following.pop();

            	    match(input,EOL,FOLLOW_EOL_in_expander_lhs_block951); 

            	    					d = runExpander( text );
            	    					descrs.addDescr( d );
            	    					text = null;
            	    					d = null;
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    break loop31;
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
    // $ANTLR end expander_lhs_block


    // $ANTLR start lhs
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:287:1: lhs returns [PatternDescr d] : l= lhs_or ;
    public PatternDescr lhs() throws RecognitionException {   
        PatternDescr d;
        PatternDescr l = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:291:17: (l= lhs_or )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:291:17: l= lhs_or
            {
            following.push(FOLLOW_lhs_or_in_lhs997);
            l=lhs_or();
            following.pop();

             d = l; 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end lhs


    // $ANTLR start lhs_column
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:295:1: lhs_column returns [PatternDescr d] : (f= fact_binding | f= fact );
    public PatternDescr lhs_column() throws RecognitionException {   
        PatternDescr d;
        PatternDescr f = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:299:17: (f= fact_binding | f= fact )
            int alt32=2;
            alt32 = dfa32.predict(input); 
            switch (alt32) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:299:17: f= fact_binding
                    {
                    following.push(FOLLOW_fact_binding_in_lhs_column1024);
                    f=fact_binding();
                    following.pop();

                     d = f; 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:300:17: f= fact
                    {
                    following.push(FOLLOW_fact_in_lhs_column1033);
                    f=fact();
                    following.pop();

                     d = f; 

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
        return d;
    }
    // $ANTLR end lhs_column


    // $ANTLR start fact_binding
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:303:1: fact_binding returns [PatternDescr d] : id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )* ;
    public PatternDescr fact_binding() throws RecognitionException {   
        PatternDescr d;
        Token id=null;
        PatternDescr f = null;



        		d=null;
        		boolean multi=false;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:309:17: (id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:309:17: id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding1065); 

             			System.err.println( "fact_binding(" + id.getText() + ")" );
             		
            following.push(FOLLOW_opt_eol_in_fact_binding1080);
            opt_eol();
            following.pop();

            match(input,29,FOLLOW_29_in_fact_binding1082); 
            following.push(FOLLOW_opt_eol_in_fact_binding1084);
            opt_eol();
            following.pop();

            following.push(FOLLOW_fact_in_fact_binding1092);
            f=fact();
            following.pop();

            following.push(FOLLOW_opt_eol_in_fact_binding1094);
            opt_eol();
            following.pop();


             			((ColumnDescr)f).setIdentifier( id.getText() );
             			d = f;
             		
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:320:17: ( 'or' f= fact )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);
                if ( LA33_0==35 ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:320:25: 'or' f= fact
            	    {
            	    match(input,35,FOLLOW_35_in_fact_binding1106); 
            	    	if ( ! multi ) {
            	     					PatternDescr first = d;
            	     					d = new OrDescr();
            	     					((OrDescr)d).addDescr( first );
            	     					multi=true;
            	     				}
            	     			
            	    following.push(FOLLOW_fact_in_fact_binding1120);
            	    f=fact();
            	    following.pop();


            	     				((ColumnDescr)f).setIdentifier( id.getText() );
            	     				((OrDescr)d).addDescr( f );
            	     			

            	    }
            	    break;

            	default :
            	    break loop33;
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
        return d;
    }
    // $ANTLR end fact_binding


    // $ANTLR start fact
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:336:1: fact returns [PatternDescr d] : id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol ;
    public PatternDescr fact() throws RecognitionException {   
        PatternDescr d;
        Token id=null;
        List c = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:340:17: (id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:340:17: id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact1160); 
             
             			d = new ColumnDescr( id.getText() ); 
             			d.setLocation( id.getLine(), id.getCharPositionInLine() );
             		
            following.push(FOLLOW_opt_eol_in_fact1168);
            opt_eol();
            following.pop();

            match(input,20,FOLLOW_20_in_fact1174); 
            following.push(FOLLOW_opt_eol_in_fact1176);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:345:29: (c= constraints )?
            int alt34=2;
            alt34 = dfa34.predict(input); 
            switch (alt34) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:345:33: c= constraints
                    {
                    following.push(FOLLOW_constraints_in_fact1182);
                    c=constraints();
                    following.pop();


                    		 			for ( Iterator cIter = c.iterator() ; cIter.hasNext() ; ) {
                     						((ColumnDescr)d).addDescr( (PatternDescr) cIter.next() );
                     					}
                     				

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_fact1201);
            opt_eol();
            following.pop();

            match(input,22,FOLLOW_22_in_fact1203); 
            following.push(FOLLOW_opt_eol_in_fact1205);
            opt_eol();
            following.pop();


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end fact


    // $ANTLR start constraints
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:356:1: constraints returns [List constraints] : opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol ;
    public List constraints() throws RecognitionException {   
        List constraints;

        		constraints = new ArrayList();
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:360:17: ( opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:360:17: opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraints1230);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:361:17: ( constraint[constraints] | predicate[constraints] )
            int alt35=2;
            int LA35_0 = input.LA(1);
            if ( LA35_0==EOL ) {
                alt35=1;
            }
            else if ( LA35_0==ID ) {
                int LA35_2 = input.LA(2);
                if ( LA35_2==29 ) {
                    int LA35_3 = input.LA(3);
                    if ( LA35_3==ID ) {
                        int LA35_6 = input.LA(4);
                        if ( LA35_6==43 ) {
                            alt35=2;
                        }
                        else if ( LA35_6==EOL||LA35_6==34||(LA35_6>=36 && LA35_6<=42) ) {
                            alt35=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("361:17: ( constraint[constraints] | predicate[constraints] )", 35, 6, input);

                            throw nvae;
                        }
                    }
                    else if ( LA35_3==EOL ) {
                        alt35=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("361:17: ( constraint[constraints] | predicate[constraints] )", 35, 3, input);

                        throw nvae;
                    }
                }
                else if ( LA35_2==EOL||LA35_2==34||(LA35_2>=36 && LA35_2<=42) ) {
                    alt35=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("361:17: ( constraint[constraints] | predicate[constraints] )", 35, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("361:17: ( constraint[constraints] | predicate[constraints] )", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:361:18: constraint[constraints]
                    {
                    following.push(FOLLOW_constraint_in_constraints1235);
                    constraint(constraints);
                    following.pop();


                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:361:42: predicate[constraints]
                    {
                    following.push(FOLLOW_predicate_in_constraints1238);
                    predicate(constraints);
                    following.pop();


                    }
                    break;

            }

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:362:17: ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )*
            loop37:
            do {
                int alt37=2;
                alt37 = dfa37.predict(input); 
                switch (alt37) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:362:19: opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] )
            	    {
            	    following.push(FOLLOW_opt_eol_in_constraints1246);
            	    opt_eol();
            	    following.pop();

            	    match(input,21,FOLLOW_21_in_constraints1248); 
            	    following.push(FOLLOW_opt_eol_in_constraints1250);
            	    opt_eol();
            	    following.pop();

            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:362:39: ( constraint[constraints] | predicate[constraints] )
            	    int alt36=2;
            	    int LA36_0 = input.LA(1);
            	    if ( LA36_0==EOL ) {
            	        alt36=1;
            	    }
            	    else if ( LA36_0==ID ) {
            	        int LA36_2 = input.LA(2);
            	        if ( LA36_2==29 ) {
            	            int LA36_3 = input.LA(3);
            	            if ( LA36_3==ID ) {
            	                int LA36_6 = input.LA(4);
            	                if ( LA36_6==43 ) {
            	                    alt36=2;
            	                }
            	                else if ( LA36_6==EOL||LA36_6==34||(LA36_6>=36 && LA36_6<=42) ) {
            	                    alt36=1;
            	                }
            	                else {
            	                    NoViableAltException nvae =
            	                        new NoViableAltException("362:39: ( constraint[constraints] | predicate[constraints] )", 36, 6, input);

            	                    throw nvae;
            	                }
            	            }
            	            else if ( LA36_3==EOL ) {
            	                alt36=1;
            	            }
            	            else {
            	                NoViableAltException nvae =
            	                    new NoViableAltException("362:39: ( constraint[constraints] | predicate[constraints] )", 36, 3, input);

            	                throw nvae;
            	            }
            	        }
            	        else if ( LA36_2==EOL||LA36_2==34||(LA36_2>=36 && LA36_2<=42) ) {
            	            alt36=1;
            	        }
            	        else {
            	            NoViableAltException nvae =
            	                new NoViableAltException("362:39: ( constraint[constraints] | predicate[constraints] )", 36, 2, input);

            	            throw nvae;
            	        }
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("362:39: ( constraint[constraints] | predicate[constraints] )", 36, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt36) {
            	        case 1 :
            	            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:362:40: constraint[constraints]
            	            {
            	            following.push(FOLLOW_constraint_in_constraints1253);
            	            constraint(constraints);
            	            following.pop();


            	            }
            	            break;
            	        case 2 :
            	            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:362:64: predicate[constraints]
            	            {
            	            following.push(FOLLOW_predicate_in_constraints1256);
            	            predicate(constraints);
            	            following.pop();


            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop37;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_constraints1264);
            opt_eol();
            following.pop();


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return constraints;
    }
    // $ANTLR end constraints


    // $ANTLR start constraint
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:366:1: constraint[List constraints] : opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= literal_constraint | rvc= retval_constraint ) opt_eol ;
    public void constraint(List constraints) throws RecognitionException {   
        Token fb=null;
        Token f=null;
        Token op=null;
        Token bvc=null;
        String lc = null;

        String rvc = null;



        		PatternDescr d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:370:17: ( opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= literal_constraint | rvc= retval_constraint ) opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:370:17: opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= literal_constraint | rvc= retval_constraint ) opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraint1283);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:371:17: (fb= ID opt_eol ':' opt_eol )?
            int alt38=2;
            alt38 = dfa38.predict(input); 
            switch (alt38) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:371:19: fb= ID opt_eol ':' opt_eol
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint1291); 
                    following.push(FOLLOW_opt_eol_in_constraint1293);
                    opt_eol();
                    following.pop();

                    match(input,29,FOLLOW_29_in_constraint1295); 
                    following.push(FOLLOW_opt_eol_in_constraint1297);
                    opt_eol();
                    following.pop();


                    }
                    break;

            }

            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint1307); 

            			if ( fb != null ) {
            				System.err.println( "fb: " + fb.getText() );
            				System.err.println( " f: " + f.getText() );
            				d = new FieldBindingDescr( f.getText(), fb.getText() );
            				System.err.println( "fbd: " + d );
            				
            				d.setLocation( f.getLine(), f.getCharPositionInLine() );
            				constraints.add( d );
            			} 
            		
            following.push(FOLLOW_opt_eol_in_constraint1317);
            opt_eol();
            following.pop();

            op=(Token)input.LT(1);
            if ( input.LA(1)==34||(input.LA(1)>=36 && input.LA(1)<=42) ) {
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint1324);    throw mse;
            }

            following.push(FOLLOW_opt_eol_in_constraint1396);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:394:41: (bvc= ID | lc= literal_constraint | rvc= retval_constraint )
            int alt39=3;
            switch ( input.LA(1) ) {
            case ID:
                alt39=1;
                break;
            case INT:
            case STRING:
            case FLOAT:
                alt39=2;
                break;
            case 20:
                alt39=3;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("394:41: (bvc= ID | lc= literal_constraint | rvc= retval_constraint )", 39, 0, input);

                throw nvae;
            }

            switch (alt39) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:394:49: bvc= ID
                    {
                    bvc=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint1414); 

                    							d = new BoundVariableDescr( f.getText(), op.getText(), bvc.getText() );
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    							constraints.add( d );
                    						

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:401:49: lc= literal_constraint
                    {
                    following.push(FOLLOW_literal_constraint_in_constraint1439);
                    lc=literal_constraint();
                    following.pop();

                     
                    							d = new LiteralDescr( f.getText(), op.getText(), lc ); 
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    							constraints.add( d );
                    						

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:407:49: rvc= retval_constraint
                    {
                    following.push(FOLLOW_retval_constraint_in_constraint1459);
                    rvc=retval_constraint();
                    following.pop();

                     
                    							d = new ReturnValueDescr( f.getText(), op.getText(), rvc ); 
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    							constraints.add( d );
                    						

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_constraint1480);
            opt_eol();
            following.pop();


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
    // $ANTLR end constraint


    // $ANTLR start literal_constraint
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:417:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT ) ;
    public String literal_constraint() throws RecognitionException {   
        String text;
        Token t=null;


        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:421:17: ( (t= STRING | t= INT | t= FLOAT ) )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:421:17: (t= STRING | t= INT | t= FLOAT )
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:421:17: (t= STRING | t= INT | t= FLOAT )
            int alt40=3;
            switch ( input.LA(1) ) {
            case STRING:
                alt40=1;
                break;
            case INT:
                alt40=2;
                break;
            case FLOAT:
                alt40=3;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("421:17: (t= STRING | t= INT | t= FLOAT )", 40, 0, input);

                throw nvae;
            }

            switch (alt40) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:421:25: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint1507); 
                     text = t.getText(); text=text.substring( 1, text.length() - 1 ); 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:422:25: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint1517); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:423:25: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint1530); 
                     text = t.getText(); 

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
        return text;
    }
    // $ANTLR end literal_constraint


    // $ANTLR start retval_constraint
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:427:1: retval_constraint returns [String text] : '(' c= paren_chunk ')' ;
    public String retval_constraint() throws RecognitionException {   
        String text;
        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:432:17: ( '(' c= paren_chunk ')' )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:432:17: '(' c= paren_chunk ')'
            {
            match(input,20,FOLLOW_20_in_retval_constraint1562); 
            following.push(FOLLOW_paren_chunk_in_retval_constraint1566);
            c=paren_chunk();
            following.pop();

            match(input,22,FOLLOW_22_in_retval_constraint1568); 
             text = c; 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return text;
    }
    // $ANTLR end retval_constraint


    // $ANTLR start predicate
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:435:1: predicate[List constraints] : decl= ID ':' field= ID '->' '(' text= paren_chunk ')' ;
    public void predicate(List constraints) throws RecognitionException {   
        Token decl=null;
        Token field=null;
        String text = null;


        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:437:17: (decl= ID ':' field= ID '->' '(' text= paren_chunk ')' )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:437:17: decl= ID ':' field= ID '->' '(' text= paren_chunk ')'
            {
            decl=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate1586); 
            match(input,29,FOLLOW_29_in_predicate1588); 
            field=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate1592); 
            match(input,43,FOLLOW_43_in_predicate1594); 
            match(input,20,FOLLOW_20_in_predicate1596); 
            following.push(FOLLOW_paren_chunk_in_predicate1600);
            text=paren_chunk();
            following.pop();

            match(input,22,FOLLOW_22_in_predicate1602); 

            			PredicateDescr d = new PredicateDescr(field.getText(), decl.getText(), text );
            			constraints.add( d );
            		

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
    // $ANTLR end predicate


    // $ANTLR start paren_chunk
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:444:1: paren_chunk returns [String text] : ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )* ;
    public String paren_chunk() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:450:17: ( ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:450:17: ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )*
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:450:17: ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )*
            loop41:
            do {
                int alt41=3;
                switch ( input.LA(1) ) {
                case EOL:
                    alt41=3;
                    break;
                case 22:
                    alt41=3;
                    break;
                case 20:
                    alt41=1;
                    break;
                case ID:
                case INT:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 21:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                    alt41=2;
                    break;

                }

                switch (alt41) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:451:25: '(' c= paren_chunk ')'
            	    {
            	    match(input,20,FOLLOW_20_in_paren_chunk1647); 
            	    following.push(FOLLOW_paren_chunk_in_paren_chunk1651);
            	    c=paren_chunk();
            	    following.pop();

            	    match(input,22,FOLLOW_22_in_paren_chunk1653); 

            	    				System.err.println( "chunk [" + c + "]" );
            	    				if ( c == null ) {
            	    					c = "";
            	    				}
            	    				if ( text == null ) {
            	    					text = "( " + c + " )";
            	    				} else {
            	    					text = text + " ( " + c + " )";
            	    				}
            	    			

            	    }
            	    break;
            	case 2 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:463:19: any= .
            	    {
            	    any=(Token)input.LT(1);
            	    matchAny(input); 

            	    				System.err.println( "any [" + any.getText() + "]" );
            	    				if ( text == null ) {
            	    					text = any.getText();
            	    				} else {
            	    					text = text + " " + any.getText(); 
            	    				} 
            	    			

            	    }
            	    break;

            	default :
            	    break loop41;
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
        return text;
    }
    // $ANTLR end paren_chunk


    // $ANTLR start curly_chunk
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:475:1: curly_chunk returns [String text] : ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )* ;
    public String curly_chunk() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:481:17: ( ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:481:17: ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )*
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:481:17: ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )*
            loop42:
            do {
                int alt42=3;
                switch ( input.LA(1) ) {
                case 24:
                    alt42=3;
                    break;
                case 23:
                    alt42=1;
                    break;
                case EOL:
                case ID:
                case INT:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                    alt42=2;
                    break;

                }

                switch (alt42) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:482:25: '{' c= curly_chunk '}'
            	    {
            	    match(input,23,FOLLOW_23_in_curly_chunk1721); 
            	    following.push(FOLLOW_curly_chunk_in_curly_chunk1725);
            	    c=curly_chunk();
            	    following.pop();

            	    match(input,24,FOLLOW_24_in_curly_chunk1727); 

            	    				System.err.println( "chunk [" + c + "]" );
            	    				if ( c == null ) {
            	    					c = "";
            	    				}
            	    				if ( text == null ) {
            	    					text = "{ " + c + " }";
            	    				} else {
            	    					text = text + " { " + c + " }";
            	    				}
            	    			

            	    }
            	    break;
            	case 2 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:494:19: any= .
            	    {
            	    any=(Token)input.LT(1);
            	    matchAny(input); 

            	    				System.err.println( "any [" + any.getText() + "]" );
            	    				if ( text == null ) {
            	    					text = any.getText();
            	    				} else {
            	    					text = text + " " + any.getText(); 
            	    				} 
            	    			

            	    }
            	    break;

            	default :
            	    break loop42;
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
        return text;
    }
    // $ANTLR end curly_chunk


    // $ANTLR start lhs_or
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:506:1: lhs_or returns [PatternDescr d] : left= lhs_and ( ('or'|'||')right= lhs_and )* ;
    public PatternDescr lhs_or() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:511:17: (left= lhs_and ( ('or'|'||')right= lhs_and )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:511:17: left= lhs_and ( ('or'|'||')right= lhs_and )*
            {
             OrDescr or = null; 
            following.push(FOLLOW_lhs_and_in_lhs_or1785);
            left=lhs_and();
            following.pop();

            d = left; 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:513:17: ( ('or'|'||')right= lhs_and )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);
                if ( LA43_0==35||LA43_0==44 ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:513:25: ('or'|'||')right= lhs_and
            	    {
            	    if ( input.LA(1)==35||input.LA(1)==44 ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or1795);    throw mse;
            	    }

            	    following.push(FOLLOW_lhs_and_in_lhs_or1806);
            	    right=lhs_and();
            	    following.pop();


            	    				if ( or == null ) {
            	    					or = new OrDescr();
            	    					or.addDescr( left );
            	    					d = or;
            	    				}
            	    				
            	    				or.addDescr( right );
            	    			

            	    }
            	    break;

            	default :
            	    break loop43;
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
        return d;
    }
    // $ANTLR end lhs_or


    // $ANTLR start lhs_and
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:527:1: lhs_and returns [PatternDescr d] : left= lhs_unary ( ('and'|'&&')right= lhs_unary )* ;
    public PatternDescr lhs_and() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:532:17: (left= lhs_unary ( ('and'|'&&')right= lhs_unary )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:532:17: left= lhs_unary ( ('and'|'&&')right= lhs_unary )*
            {
             AndDescr and = null; 
            following.push(FOLLOW_lhs_unary_in_lhs_and1846);
            left=lhs_unary();
            following.pop();

             d = left; 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:534:17: ( ('and'|'&&')right= lhs_unary )*
            loop44:
            do {
                int alt44=2;
                int LA44_0 = input.LA(1);
                if ( (LA44_0>=45 && LA44_0<=46) ) {
                    alt44=1;
                }


                switch (alt44) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:534:25: ('and'|'&&')right= lhs_unary
            	    {
            	    if ( (input.LA(1)>=45 && input.LA(1)<=46) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and1855);    throw mse;
            	    }

            	    following.push(FOLLOW_lhs_unary_in_lhs_and1866);
            	    right=lhs_unary();
            	    following.pop();


            	    				if ( and == null ) {
            	    					and = new AndDescr();
            	    					and.addDescr( left );
            	    					d = and;
            	    				}
            	    				
            	    				and.addDescr( right );
            	    			

            	    }
            	    break;

            	default :
            	    break loop44;
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
        return d;
    }
    // $ANTLR end lhs_and


    // $ANTLR start lhs_unary
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:548:1: lhs_unary returns [PatternDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) ;
    public PatternDescr lhs_unary() throws RecognitionException {   
        PatternDescr d;
        PatternDescr u = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:552:17: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:552:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:552:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            int alt45=5;
            switch ( input.LA(1) ) {
            case 47:
                alt45=1;
                break;
            case 48:
                alt45=2;
                break;
            case 49:
                alt45=3;
                break;
            case ID:
                alt45=4;
                break;
            case 20:
                alt45=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("552:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | \'(\' u= lhs \')\' )", 45, 0, input);

                throw nvae;
            }

            switch (alt45) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:552:25: u= lhs_exist
                    {
                    following.push(FOLLOW_lhs_exist_in_lhs_unary1904);
                    u=lhs_exist();
                    following.pop();


                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:553:25: u= lhs_not
                    {
                    following.push(FOLLOW_lhs_not_in_lhs_unary1912);
                    u=lhs_not();
                    following.pop();


                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:554:25: u= lhs_eval
                    {
                    following.push(FOLLOW_lhs_eval_in_lhs_unary1920);
                    u=lhs_eval();
                    following.pop();


                    }
                    break;
                case 4 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:555:25: u= lhs_column
                    {
                    following.push(FOLLOW_lhs_column_in_lhs_unary1928);
                    u=lhs_column();
                    following.pop();


                    }
                    break;
                case 5 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:556:25: '(' u= lhs ')'
                    {
                    match(input,20,FOLLOW_20_in_lhs_unary1934); 
                    following.push(FOLLOW_lhs_in_lhs_unary1938);
                    u=lhs();
                    following.pop();

                    match(input,22,FOLLOW_22_in_lhs_unary1940); 

                    }
                    break;

            }

             d = u; 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end lhs_unary


    // $ANTLR start lhs_exist
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:560:1: lhs_exist returns [PatternDescr d] : loc= 'exists' column= lhs_column ;
    public PatternDescr lhs_exist() throws RecognitionException {   
        PatternDescr d;
        Token loc=null;
        PatternDescr column = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:564:17: (loc= 'exists' column= lhs_column )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:564:17: loc= 'exists' column= lhs_column
            {
            loc=(Token)input.LT(1);
            match(input,47,FOLLOW_47_in_lhs_exist1970); 
            following.push(FOLLOW_lhs_column_in_lhs_exist1974);
            column=lhs_column();
            following.pop();

             
            			d = new ExistsDescr( (ColumnDescr) column ); 
            			d.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end lhs_exist


    // $ANTLR start lhs_not
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:571:1: lhs_not returns [NotDescr d] : loc= 'not' column= lhs_column ;
    public NotDescr lhs_not() throws RecognitionException {   
        NotDescr d;
        Token loc=null;
        PatternDescr column = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:575:17: (loc= 'not' column= lhs_column )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:575:17: loc= 'not' column= lhs_column
            {
            loc=(Token)input.LT(1);
            match(input,48,FOLLOW_48_in_lhs_not2004); 
            following.push(FOLLOW_lhs_column_in_lhs_not2008);
            column=lhs_column();
            following.pop();


            			d = new NotDescr( (ColumnDescr) column ); 
            			d.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end lhs_not


    // $ANTLR start lhs_eval
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:582:1: lhs_eval returns [PatternDescr d] : 'eval' '(' c= paren_chunk ')' ;
    public PatternDescr lhs_eval() throws RecognitionException {   
        PatternDescr d;
        String c = null;



        		d = null;
        		String text = "";
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:587:17: ( 'eval' '(' c= paren_chunk ')' )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:587:17: 'eval' '(' c= paren_chunk ')'
            {
            match(input,49,FOLLOW_49_in_lhs_eval2034); 
            match(input,20,FOLLOW_20_in_lhs_eval2036); 
            following.push(FOLLOW_paren_chunk_in_lhs_eval2040);
            c=paren_chunk();
            following.pop();

            match(input,22,FOLLOW_22_in_lhs_eval2042); 
             d = new EvalDescr( c ); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return d;
    }
    // $ANTLR end lhs_eval


    // $ANTLR start dotted_name
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:591:1: dotted_name returns [String name] : id= ID ( '.' id= ID )* ;
    public String dotted_name() throws RecognitionException {   
        String name;
        Token id=null;


        		name = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:596:17: (id= ID ( '.' id= ID )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:596:17: id= ID ( '.' id= ID )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name2074); 
             name=id.getText(); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:596:46: ( '.' id= ID )*
            loop46:
            do {
                int alt46=2;
                int LA46_0 = input.LA(1);
                if ( LA46_0==50 ) {
                    alt46=1;
                }


                switch (alt46) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:596:48: '.' id= ID
            	    {
            	    match(input,50,FOLLOW_50_in_dotted_name2080); 
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_dotted_name2084); 
            	     name = name + "." + id.getText(); 

            	    }
            	    break;

            	default :
            	    break loop46;
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
        return name;
    }
    // $ANTLR end dotted_name


    // $ANTLR start word
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:600:1: word returns [String word] : (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING );
    public String word() throws RecognitionException {   
        String word;
        Token id=null;
        Token str=null;


        		word = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:604:17: (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING )
            int alt47=11;
            switch ( input.LA(1) ) {
            case ID:
                alt47=1;
                break;
            case 16:
                alt47=2;
                break;
            case 51:
                alt47=3;
                break;
            case 27:
                alt47=4;
                break;
            case 25:
                alt47=5;
                break;
            case 32:
                alt47=6;
                break;
            case 33:
                alt47=7;
                break;
            case 28:
                alt47=8;
                break;
            case 30:
                alt47=9;
                break;
            case 26:
                alt47=10;
                break;
            case STRING:
                alt47=11;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("600:1: word returns [String word] : (id= ID | \'import\' | \'use\' | \'rule\' | \'query\' | \'salience\' | \'no-loop\' | \'when\' | \'then\' | \'end\' | str= STRING );", 47, 0, input);

                throw nvae;
            }

            switch (alt47) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:604:17: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_word2114); 
                     word=id.getText(); 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:605:17: 'import'
                    {
                    match(input,16,FOLLOW_16_in_word2126); 
                     word="import"; 

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:606:17: 'use'
                    {
                    match(input,51,FOLLOW_51_in_word2135); 
                     word="use"; 

                    }
                    break;
                case 4 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:607:17: 'rule'
                    {
                    match(input,27,FOLLOW_27_in_word2147); 
                     word="rule"; 

                    }
                    break;
                case 5 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:608:17: 'query'
                    {
                    match(input,25,FOLLOW_25_in_word2158); 
                     word="query"; 

                    }
                    break;
                case 6 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:609:17: 'salience'
                    {
                    match(input,32,FOLLOW_32_in_word2168); 
                     word="salience"; 

                    }
                    break;
                case 7 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:610:17: 'no-loop'
                    {
                    match(input,33,FOLLOW_33_in_word2176); 
                     word="no-loop"; 

                    }
                    break;
                case 8 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:611:17: 'when'
                    {
                    match(input,28,FOLLOW_28_in_word2184); 
                     word="when"; 

                    }
                    break;
                case 9 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:612:17: 'then'
                    {
                    match(input,30,FOLLOW_30_in_word2195); 
                     word="then"; 

                    }
                    break;
                case 10 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:613:17: 'end'
                    {
                    match(input,26,FOLLOW_26_in_word2206); 
                     word="end"; 

                    }
                    break;
                case 11 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:614:17: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_word2220); 
                     word=str.getText(); word=word.substring( 1, word.length()-1 ); 

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
        return word;
    }
    // $ANTLR end word


    protected DFA2 dfa2 = new DFA2();protected DFA13 dfa13 = new DFA13();protected DFA14 dfa14 = new DFA14();protected DFA15 dfa15 = new DFA15();protected DFA32 dfa32 = new DFA32();protected DFA34 dfa34 = new DFA34();protected DFA37 dfa37 = new DFA37();protected DFA38 dfa38 = new DFA38();
    class DFA2 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s1 = new DFA.State() {{alt=3;}};
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s4 = new DFA.State() {{alt=2;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 27:
                    return s3;

                case EOL:
                    return s2;

                case 25:
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 2, 2, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case -1:
                    return s1;

                case EOL:
                    return s2;

                case 27:
                    return s3;

                case 25:
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 2, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA13 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s5 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s5;

                case EOL:
                    return s3;

                case 20:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 3, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s3;

                case 20:
                    return s2;

                case ID:
                case 50:
                    return s5;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA13_0 = input.LA(1);
                if ( LA13_0==ID ) {return s1;}
                if ( LA13_0==EOL ) {return s2;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
        };

    }class DFA14 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s6 = new DFA.State() {{alt=1;}};
        DFA.State s3 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 21:
                case 22:
                    return s2;

                case EOL:
                    return s3;

                case ID:
                    return s6;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 3, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s3;

                case 21:
                case 22:
                    return s2;

                case ID:
                case 50:
                    return s6;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA14_0 = input.LA(1);
                if ( LA14_0==ID ) {return s1;}
                if ( LA14_0==EOL ) {return s2;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }
        };

    }class DFA15 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s6 = new DFA.State() {{alt=1;}};
        DFA.State s3 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 21:
                case 22:
                    return s2;

                case EOL:
                    return s3;

                case ID:
                    return s6;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 3, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s3;

                case 21:
                case 22:
                    return s2;

                case ID:
                case 50:
                    return s6;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA15_0 = input.LA(1);
                if ( LA15_0==ID ) {return s1;}
                if ( LA15_0==EOL ) {return s2;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
        };

    }class DFA32 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s4 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 20:
                    return s4;

                case EOL:
                    return s2;

                case 29:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 2, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s2;

                case 29:
                    return s3;

                case 20:
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA32_0 = input.LA(1);
                if ( LA32_0==ID ) {return s1;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }
        };

    }class DFA34 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s2 = new DFA.State() {{alt=1;}};
        DFA.State s3 = new DFA.State() {{alt=2;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s1;

                case ID:
                    return s2;

                case 22:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 34, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s1;

                case ID:
                    return s2;

                case 22:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 34, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA37 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 21:
                    return s3;

                case EOL:
                    return s1;

                case 22:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 37, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s1;

                case 22:
                    return s2;

                case 21:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 37, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA38 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s4 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 34:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                    return s4;

                case EOL:
                    return s2;

                case 29:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 2, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s2;

                case 29:
                    return s3;

                case 34:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA38_0 = input.LA(1);
                if ( LA38_0==ID ) {return s1;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 38, 0, input);

                throw nvae;
            }
        };

    }


    public static final BitSet FOLLOW_EOL_in_opt_eol40 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_compilation_unit53 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit57 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_rule_in_compilation_unit66 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_query_in_compilation_unit79 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_prolog104 = new BitSet(new long[]{0x00000000000F4012L});
    public static final BitSet FOLLOW_package_statement_in_prolog112 = new BitSet(new long[]{0x00000000000F0012L});
    public static final BitSet FOLLOW_import_statement_in_prolog125 = new BitSet(new long[]{0x00000000000F0012L});
    public static final BitSet FOLLOW_expander_in_prolog130 = new BitSet(new long[]{0x00000000000C0012L});
    public static final BitSet FOLLOW_global_in_prolog136 = new BitSet(new long[]{0x00000000000C0012L});
    public static final BitSet FOLLOW_function_in_prolog141 = new BitSet(new long[]{0x0000000000080012L});
    public static final BitSet FOLLOW_opt_eol_in_prolog146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_package_statement170 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_package_statement172 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement176 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_package_statement178 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_package_statement181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_import_statement197 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_import_statement199 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_dotted_name_in_import_statement203 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_import_statement205 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_import_statement208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_expander230 = new BitSet(new long[]{0x0000000000008032L});
    public static final BitSet FOLLOW_dotted_name_in_expander235 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_expander239 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_expander242 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_global266 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_dotted_name_in_global270 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_global274 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_global276 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_global279 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_function303 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function305 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_dotted_name_in_function310 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function314 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_function318 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function320 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_function329 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function331 = new BitSet(new long[]{0x0000000000400032L});
    public static final BitSet FOLLOW_dotted_name_in_function341 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function345 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_function349 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function351 = new BitSet(new long[]{0x0000000000600000L});
    public static final BitSet FOLLOW_21_in_function365 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function367 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_dotted_name_in_function372 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function376 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_function380 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function382 = new BitSet(new long[]{0x0000000000600000L});
    public static final BitSet FOLLOW_22_in_function407 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function411 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_function415 = new BitSet(new long[]{0x000FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_curly_chunk_in_function422 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_function431 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_query463 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_query469 = new BitSet(new long[]{0x000800035E0100A0L});
    public static final BitSet FOLLOW_word_in_query473 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_query475 = new BitSet(new long[]{0x000FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_expander_lhs_block_in_query491 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query499 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_query514 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_query516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_rule539 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_rule545 = new BitSet(new long[]{0x000800035E0100A0L});
    public static final BitSet FOLLOW_word_in_rule549 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule551 = new BitSet(new long[]{0x00000000D0000000L});
    public static final BitSet FOLLOW_rule_options_in_rule564 = new BitSet(new long[]{0x0000000050000000L});
    public static final BitSet FOLLOW_28_in_rule582 = new BitSet(new long[]{0x0000000020000012L});
    public static final BitSet FOLLOW_29_in_rule584 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule587 = new BitSet(new long[]{0x000FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_expander_lhs_block_in_rule605 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule614 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_rule639 = new BitSet(new long[]{0x0000000020000012L});
    public static final BitSet FOLLOW_29_in_rule641 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule646 = new BitSet(new long[]{0x000FFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_26_in_rule681 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule683 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_rule_options706 = new BitSet(new long[]{0x0000000020000012L});
    public static final BitSet FOLLOW_29_in_rule_options708 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule_options711 = new BitSet(new long[]{0x0000000300000002L});
    public static final BitSet FOLLOW_rule_option_in_rule_options720 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule_options722 = new BitSet(new long[]{0x0000000300000002L});
    public static final BitSet FOLLOW_salience_in_rule_option761 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_option771 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_salience804 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_salience806 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_salience810 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_salience812 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_salience815 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_no_loop845 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_no_loop847 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block874 = new BitSet(new long[]{0x0003800000100022L});
    public static final BitSet FOLLOW_34_in_expander_lhs_block914 = new BitSet(new long[]{0x0003800000100020L});
    public static final BitSet FOLLOW_lhs_in_expander_lhs_block918 = new BitSet(new long[]{0x000FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_paren_chunk_in_expander_lhs_block949 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_EOL_in_expander_lhs_block951 = new BitSet(new long[]{0x000FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_lhs_or_in_lhs997 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column1024 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_column1033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding1065 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding1080 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_fact_binding1082 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding1084 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_fact_in_fact_binding1092 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding1094 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_35_in_fact_binding1106 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_fact_in_fact_binding1120 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_ID_in_fact1160 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1168 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_fact1174 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1176 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_constraints_in_fact1182 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1201 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_fact1203 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1230 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_constraint_in_constraints1235 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_predicate_in_constraints1238 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1246 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_constraints1248 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1250 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_constraint_in_constraints1253 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_predicate_in_constraints1256 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1283 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint1291 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1293 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_constraint1295 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1297 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint1307 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1317 = new BitSet(new long[]{0x000007F400000000L});
    public static final BitSet FOLLOW_set_in_constraint1324 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1396 = new BitSet(new long[]{0x00000000001001E0L});
    public static final BitSet FOLLOW_ID_in_constraint1414 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint1439 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint1459 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint1507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint1517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint1530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_retval_constraint1562 = new BitSet(new long[]{0x000FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint1566 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_retval_constraint1568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_predicate1586 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_predicate1588 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_predicate1592 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_predicate1594 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_predicate1596 = new BitSet(new long[]{0x000FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate1600 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_predicate1602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_paren_chunk1647 = new BitSet(new long[]{0x000FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk1651 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_paren_chunk1653 = new BitSet(new long[]{0x000FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_23_in_curly_chunk1721 = new BitSet(new long[]{0x000FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk1725 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_curly_chunk1727 = new BitSet(new long[]{0x000FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1785 = new BitSet(new long[]{0x0000100800000002L});
    public static final BitSet FOLLOW_set_in_lhs_or1795 = new BitSet(new long[]{0x0003800000100020L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1806 = new BitSet(new long[]{0x0000100800000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1846 = new BitSet(new long[]{0x0000600000000002L});
    public static final BitSet FOLLOW_set_in_lhs_and1855 = new BitSet(new long[]{0x0003800000100020L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1866 = new BitSet(new long[]{0x0000600000000002L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary1904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary1912 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary1920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary1928 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_lhs_unary1934 = new BitSet(new long[]{0x0003800000100020L});
    public static final BitSet FOLLOW_lhs_in_lhs_unary1938 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_lhs_unary1940 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_lhs_exist1970 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist1974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_lhs_not2004 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not2008 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_lhs_eval2034 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_lhs_eval2036 = new BitSet(new long[]{0x000FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval2040 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_lhs_eval2042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name2074 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_50_in_dotted_name2080 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_dotted_name2084 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_ID_in_word2114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_word2126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_word2135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_word2147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_word2158 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_word2168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_word2176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_word2184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_word2195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_word2206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_word2220 = new BitSet(new long[]{0x0000000000000002L});

}