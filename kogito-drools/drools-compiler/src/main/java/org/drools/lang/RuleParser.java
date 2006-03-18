// $ANTLR 3.0ea8 /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g 2006-03-18 01:36:58

	package org.drools.lang;
	import java.util.List;
	import java.util.ArrayList;
	import java.util.Iterator;
	import java.util.StringTokenizer;
	import org.drools.lang.descr.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class RuleParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "EOL", "ID", "INT", "STRING", "FLOAT", "MISC", "WS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "\'package\'", "\';\'", "\'import\'", "\'expander\'", "\'global\'", "\'function\'", "\'(\'", "\',\'", "\')\'", "\'{\'", "\'}\'", "\'query\'", "\'end\'", "\'rule\'", "\'when\'", "\':\'", "\'then\'", "\'attributes\'", "\'salience\'", "\'no-loop\'", "\'xor-group\'", "\'agenda-group\'", "\'duration\'", "\'>\'", "\'or\'", "\'==\'", "\'>=\'", "\'<\'", "\'<=\'", "\'!=\'", "\'contains\'", "\'matches\'", "\'->\'", "\'||\'", "\'and\'", "\'&&\'", "\'exists\'", "\'not\'", "\'eval\'", "\'.\'", "\'use\'"
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
    	
    	private PatternDescr runWhenExpander(String text) throws RecognitionException {
    		String expanded = expander.expand( "when", text, this );
    		return reparseLhs( expanded );
    	}
    	
    	private String runThenExpander(String text) {
    		System.err.println( "expand THEN [" + text + "]" );
    		StringTokenizer lines = new StringTokenizer( text, "\n\r" );

    		StringBuffer expanded = new StringBuffer();
    		
    		String eol = System.getProperty( "line.separator" );
    				
    		while ( lines.hasMoreTokens() ) {
    			String line = lines.nextToken();
    			line = line.trim();
    			if ( line.length() > 0 ) {
    				if ( line.startsWith( ">" ) ) {
    					expanded.append( line.substring( 1 ) );
    					expanded.append( eol );
    				} else {
    					expanded.append( expander.expand( "then", text, this ) );
    					expanded.append( eol );
    				}
    			}
    		}
    		
    		return expanded.toString();
    	}
    	
    	private PatternDescr reparseLhs(String text) throws RecognitionException {
    		CharStream charStream = new ANTLRStringStream( text );
    		RuleParserLexer lexer = new RuleParserLexer( charStream );
    		TokenStream tokenStream = new CommonTokenStream( lexer );
    		RuleParser parser = new RuleParser( tokenStream );
    		
    		return parser.lhs();
    	}
    	
    	private String getString(Token token) {
    		String orig = token.getText();
    		return orig.substring( 1, orig.length() -1 );
    	}



    // $ANTLR start opt_eol
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:79:1: opt_eol : ( EOL )* ;
    public void opt_eol() throws RecognitionException {   
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:80:17: ( ( EOL )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:80:17: ( EOL )*
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:80:17: ( EOL )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);
                if ( LA1_0==EOL ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:80:17: EOL
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:83:1: compilation_unit : opt_eol prolog (r= rule | q= query )* ;
    public void compilation_unit() throws RecognitionException {   
        RuleDescr r = null;

        QueryDescr q = null;


        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:84:17: ( opt_eol prolog (r= rule | q= query )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:84:17: opt_eol prolog (r= rule | q= query )*
            {
            following.push(FOLLOW_opt_eol_in_compilation_unit53);
            opt_eol();
            following.pop();

            following.push(FOLLOW_prolog_in_compilation_unit57);
            prolog();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:86:17: (r= rule | q= query )*
            loop2:
            do {
                int alt2=3;
                alt2 = dfa2.predict(input); 
                switch (alt2) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:86:25: r= rule
            	    {
            	    following.push(FOLLOW_rule_in_compilation_unit66);
            	    r=rule();
            	    following.pop();

            	    this.packageDescr.addRule( r ); 

            	    }
            	    break;
            	case 2 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:87:25: q= query
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:91:1: prolog : opt_eol (name= package_statement )? ( import_statement )* ( expander )? ( global )* ( function )* opt_eol ;
    public void prolog() throws RecognitionException {   
        String name = null;



        		String packageName = "";
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:95:17: ( opt_eol (name= package_statement )? ( import_statement )* ( expander )? ( global )* ( function )* opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:95:17: opt_eol (name= package_statement )? ( import_statement )* ( expander )? ( global )* ( function )* opt_eol
            {
            following.push(FOLLOW_opt_eol_in_prolog104);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:96:17: (name= package_statement )?
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
                    new NoViableAltException("96:17: (name= package_statement )?", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:96:19: name= package_statement
                    {
                    following.push(FOLLOW_package_statement_in_prolog112);
                    name=package_statement();
                    following.pop();

                     packageName = name; 

                    }
                    break;

            }

             
            			this.packageDescr = new PackageDescr( name ); 
            		
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:100:17: ( import_statement )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);
                if ( LA4_0==16 ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:100:17: import_statement
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

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:101:17: ( expander )?
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
                    new NoViableAltException("101:17: ( expander )?", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:101:17: expander
                    {
                    following.push(FOLLOW_expander_in_prolog130);
                    expander();
                    following.pop();


                    }
                    break;

            }

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:102:17: ( global )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);
                if ( LA6_0==18 ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:102:17: global
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

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:103:17: ( function )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);
                if ( LA7_0==19 ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:103:17: function
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:107:1: package_statement returns [String packageName] : 'package' opt_eol name= dotted_name ( ';' )? opt_eol ;
    public String package_statement() throws RecognitionException {   
        String packageName;
        String name = null;



        		packageName = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:112:17: ( 'package' opt_eol name= dotted_name ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:112:17: 'package' opt_eol name= dotted_name ( ';' )? opt_eol
            {
            match(input,14,FOLLOW_14_in_package_statement170); 
            following.push(FOLLOW_opt_eol_in_package_statement172);
            opt_eol();
            following.pop();

            following.push(FOLLOW_dotted_name_in_package_statement176);
            name=dotted_name();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:112:52: ( ';' )?
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
                    new NoViableAltException("112:52: ( \';\' )?", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:112:52: ';'
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:118:1: import_statement : 'import' opt_eol name= dotted_name ( ';' )? opt_eol ;
    public void import_statement() throws RecognitionException {   
        String name = null;


        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:119:17: ( 'import' opt_eol name= dotted_name ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:119:17: 'import' opt_eol name= dotted_name ( ';' )? opt_eol
            {
            match(input,16,FOLLOW_16_in_import_statement197); 
            following.push(FOLLOW_opt_eol_in_import_statement199);
            opt_eol();
            following.pop();

            following.push(FOLLOW_dotted_name_in_import_statement203);
            name=dotted_name();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:119:51: ( ';' )?
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
                    new NoViableAltException("119:51: ( \';\' )?", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:119:51: ';'
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:125:1: expander : 'expander' (name= dotted_name )? ( ';' )? opt_eol ;
    public void expander() throws RecognitionException {   
        String name = null;



        		String config=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:129:17: ( 'expander' (name= dotted_name )? ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:129:17: 'expander' (name= dotted_name )? ( ';' )? opt_eol
            {
            match(input,17,FOLLOW_17_in_expander230); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:129:28: (name= dotted_name )?
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
                    new NoViableAltException("129:28: (name= dotted_name )?", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:129:29: name= dotted_name
                    {
                    following.push(FOLLOW_dotted_name_in_expander235);
                    name=dotted_name();
                    following.pop();


                    }
                    break;

            }

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:129:48: ( ';' )?
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
                    new NoViableAltException("129:48: ( \';\' )?", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:129:48: ';'
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:135:1: global : 'global' type= dotted_name id= ID ( ';' )? opt_eol ;
    public void global() throws RecognitionException {   
        Token id=null;
        String type = null;



        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:139:17: ( 'global' type= dotted_name id= ID ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:139:17: 'global' type= dotted_name id= ID ( ';' )? opt_eol
            {
            match(input,18,FOLLOW_18_in_global266); 
            following.push(FOLLOW_dotted_name_in_global270);
            type=dotted_name();
            following.pop();

            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_global274); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:139:49: ( ';' )?
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
                    new NoViableAltException("139:49: ( \';\' )?", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:139:49: ';'
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:145:1: function : 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol ;
    public void function() throws RecognitionException {   
        Token name=null;
        Token paramName=null;
        String retType = null;

        String paramType = null;

        String body = null;



        		FunctionDescr f = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:150:17: ( 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:150:17: 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol
            {
            match(input,19,FOLLOW_19_in_function303); 
            following.push(FOLLOW_opt_eol_in_function305);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:150:36: (retType= dotted_name )?
            int alt13=2;
            alt13 = dfa13.predict(input); 
            switch (alt13) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:150:37: retType= dotted_name
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

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:156:25: ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )?
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
                    new NoViableAltException("156:25: ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( \',\' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )?", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:156:33: (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )*
                    {
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:156:33: (paramType= dotted_name )?
                    int alt14=2;
                    alt14 = dfa14.predict(input); 
                    switch (alt14) {
                        case 1 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:156:34: paramType= dotted_name
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
                    				
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:160:33: ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )*
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);
                        if ( LA16_0==21 ) {
                            alt16=1;
                        }


                        switch (alt16) {
                    	case 1 :
                    	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:160:41: ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol
                    	    {
                    	    match(input,21,FOLLOW_21_in_function365); 
                    	    following.push(FOLLOW_opt_eol_in_function367);
                    	    opt_eol();
                    	    following.pop();

                    	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:160:53: (paramType= dotted_name )?
                    	    int alt15=2;
                    	    alt15 = dfa15.predict(input); 
                    	    switch (alt15) {
                    	        case 1 :
                    	            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:160:54: paramType= dotted_name
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:181:1: query returns [QueryDescr query] : opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol ;
    public QueryDescr query() throws RecognitionException {   
        QueryDescr query;
        Token loc=null;
        String queryName = null;



        		query = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:186:17: ( opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:186:17: opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol
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
            		
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:194:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
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
            case 47:
            case 48:
            case 49:
            case 53:
            case 54:
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
                        new NoViableAltException("194:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 2, input);

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
                        new NoViableAltException("194:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 4, input);

                    throw nvae;
                }
                break;
            case 50:
                int LA18_5 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("194:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 5, input);

                    throw nvae;
                }
                break;
            case 51:
                int LA18_6 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("194:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 6, input);

                    throw nvae;
                }
                break;
            case 52:
                int LA18_7 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("194:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 7, input);

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
                        new NoViableAltException("194:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 8, input);

                    throw nvae;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("194:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:195:25: {...}? expander_lhs_block[lhs]
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
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:196:27: normal_lhs_block[lhs]
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:202:1: rule returns [RuleDescr rule] : opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* 'end' opt_eol ;
    public RuleDescr rule() throws RecognitionException {   
        RuleDescr rule;
        Token loc=null;
        Token any=null;
        String ruleName = null;



        		rule = null;
        		String consequence = "";
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:208:17: ( opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* 'end' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:208:17: opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* 'end' opt_eol
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
            		
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:214:17: ( rule_attributes[rule] )?
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
                    new NoViableAltException("214:17: ( rule_attributes[rule] )?", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:214:25: rule_attributes[rule]
                    {
                    following.push(FOLLOW_rule_attributes_in_rule562);
                    rule_attributes(rule);
                    following.pop();


                    }
                    break;

            }

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:216:17: (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?
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
                    new NoViableAltException("216:17: (loc= \'when\' ( \':\' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:216:25: loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                    {
                    loc=(Token)input.LT(1);
                    match(input,28,FOLLOW_28_in_rule576); 
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:216:36: ( ':' )?
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
                                new NoViableAltException("216:36: ( \':\' )?", 20, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA20_0>=EOL && LA20_0<=28)||(LA20_0>=30 && LA20_0<=54) ) {
                        alt20=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("216:36: ( \':\' )?", 20, 0, input);

                        throw nvae;
                    }
                    switch (alt20) {
                        case 1 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:216:36: ':'
                            {
                            match(input,29,FOLLOW_29_in_rule578); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_rule581);
                    opt_eol();
                    following.pop();

                     
                    				AndDescr lhs = new AndDescr(); rule.setLhs( lhs ); 
                    				lhs.setLocation( loc.getLine(), loc.getCharPositionInLine() );
                    			
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:221:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
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
                    case 47:
                    case 48:
                    case 49:
                    case 53:
                    case 54:
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
                                new NoViableAltException("221:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 2, input);

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
                                new NoViableAltException("221:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 4, input);

                            throw nvae;
                        }
                        break;
                    case 50:
                        int LA21_5 = input.LA(2);
                        if (  expander != null  ) {
                            alt21=1;
                        }
                        else if ( true ) {
                            alt21=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("221:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 5, input);

                            throw nvae;
                        }
                        break;
                    case 51:
                        int LA21_6 = input.LA(2);
                        if (  expander != null  ) {
                            alt21=1;
                        }
                        else if ( true ) {
                            alt21=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("221:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 6, input);

                            throw nvae;
                        }
                        break;
                    case 52:
                        int LA21_7 = input.LA(2);
                        if (  expander != null  ) {
                            alt21=1;
                        }
                        else if ( true ) {
                            alt21=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("221:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 7, input);

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
                                new NoViableAltException("221:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 8, input);

                            throw nvae;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("221:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 0, input);

                        throw nvae;
                    }

                    switch (alt21) {
                        case 1 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:222:33: {...}? expander_lhs_block[lhs]
                            {
                            if ( !( expander != null ) ) {
                                throw new FailedPredicateException(input, "rule", " expander != null ");
                            }
                            following.push(FOLLOW_expander_lhs_block_in_rule599);
                            expander_lhs_block(lhs);
                            following.pop();


                            }
                            break;
                        case 2 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:223:35: normal_lhs_block[lhs]
                            {
                            following.push(FOLLOW_normal_lhs_block_in_rule608);
                            normal_lhs_block(lhs);
                            following.pop();


                            }
                            break;

                    }


                    }
                    break;

            }

             System.err.println( "finished LHS?" ); 
            match(input,30,FOLLOW_30_in_rule633); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:228:24: ( ':' )?
            int alt23=2;
            int LA23_0 = input.LA(1);
            if ( LA23_0==29 ) {
                alt23=1;
            }
            else if ( (LA23_0>=EOL && LA23_0<=28)||(LA23_0>=30 && LA23_0<=54) ) {
                alt23=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("228:24: ( \':\' )?", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:228:24: ':'
                    {
                    match(input,29,FOLLOW_29_in_rule635); 

                    }
                    break;

            }

             System.err.println( "matched THEN" ); 
            following.push(FOLLOW_opt_eol_in_rule640);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:229:17: ( options {greedy=false; } : any= . )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);
                if ( LA24_0==26 ) {
                    alt24=2;
                }
                else if ( (LA24_0>=EOL && LA24_0<=25)||(LA24_0>=27 && LA24_0<=54) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:229:44: any= .
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


            			if ( expander != null ) {
            				String expanded = runThenExpander( consequence );
            				rule.setConsequence( expanded );
            			} else { 
            				rule.setConsequence( consequence ); 
            			}
            		
            match(input,26,FOLLOW_26_in_rule675); 
            following.push(FOLLOW_opt_eol_in_rule677);
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


    // $ANTLR start rule_attributes
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:246:1: rule_attributes[RuleDescr rule] : 'attributes' ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )* ;
    public void rule_attributes(RuleDescr rule) throws RecognitionException {   
        AttributeDescr a = null;


        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:248:25: ( 'attributes' ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:248:25: 'attributes' ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )*
            {
            match(input,31,FOLLOW_31_in_rule_attributes695); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:248:38: ( ':' )?
            int alt25=2;
            int LA25_0 = input.LA(1);
            if ( LA25_0==29 ) {
                alt25=1;
            }
            else if ( LA25_0==EOL||LA25_0==21||LA25_0==28||LA25_0==30||(LA25_0>=32 && LA25_0<=36) ) {
                alt25=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("248:38: ( \':\' )?", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:248:38: ':'
                    {
                    match(input,29,FOLLOW_29_in_rule_attributes697); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_rule_attributes700);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:249:25: ( ( ',' )? a= rule_attribute opt_eol )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);
                if ( LA27_0==21||(LA27_0>=32 && LA27_0<=36) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:249:33: ( ',' )? a= rule_attribute opt_eol
            	    {
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:249:33: ( ',' )?
            	    int alt26=2;
            	    int LA26_0 = input.LA(1);
            	    if ( LA26_0==21 ) {
            	        alt26=1;
            	    }
            	    else if ( (LA26_0>=32 && LA26_0<=36) ) {
            	        alt26=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("249:33: ( \',\' )?", 26, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt26) {
            	        case 1 :
            	            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:249:33: ','
            	            {
            	            match(input,21,FOLLOW_21_in_rule_attributes707); 

            	            }
            	            break;

            	    }

            	    following.push(FOLLOW_rule_attribute_in_rule_attributes712);
            	    a=rule_attribute();
            	    following.pop();

            	    following.push(FOLLOW_opt_eol_in_rule_attributes714);
            	    opt_eol();
            	    following.pop();


            	    					rule.addAttribute( a );
            	    				

            	    }
            	    break;

            	default :
            	    break loop27;
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
    // $ANTLR end rule_attributes


    // $ANTLR start rule_attribute
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:256:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= xor_group );
    public AttributeDescr rule_attribute() throws RecognitionException {   
        AttributeDescr d;
        AttributeDescr a = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:261:25: (a= salience | a= no_loop | a= agenda_group | a= duration | a= xor_group )
            int alt28=5;
            switch ( input.LA(1) ) {
            case 32:
                alt28=1;
                break;
            case 33:
                alt28=2;
                break;
            case 35:
                alt28=3;
                break;
            case 36:
                alt28=4;
                break;
            case 34:
                alt28=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("256:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= xor_group );", 28, 0, input);

                throw nvae;
            }

            switch (alt28) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:261:25: a= salience
                    {
                    following.push(FOLLOW_salience_in_rule_attribute753);
                    a=salience();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:262:25: a= no_loop
                    {
                    following.push(FOLLOW_no_loop_in_rule_attribute763);
                    a=no_loop();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:263:25: a= agenda_group
                    {
                    following.push(FOLLOW_agenda_group_in_rule_attribute774);
                    a=agenda_group();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 4 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:264:25: a= duration
                    {
                    following.push(FOLLOW_duration_in_rule_attribute787);
                    a=duration();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 5 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:265:25: a= xor_group
                    {
                    following.push(FOLLOW_xor_group_in_rule_attribute801);
                    a=xor_group();
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
    // $ANTLR end rule_attribute


    // $ANTLR start salience
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:269:1: salience returns [AttributeDescr d ] : loc= 'salience' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr salience() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token i=null;


        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:274:17: (loc= 'salience' opt_eol i= INT ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:274:17: loc= 'salience' opt_eol i= INT ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,32,FOLLOW_32_in_salience834); 
            following.push(FOLLOW_opt_eol_in_salience836);
            opt_eol();
            following.pop();

            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience840); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:274:46: ( ';' )?
            int alt29=2;
            int LA29_0 = input.LA(1);
            if ( LA29_0==15 ) {
                alt29=1;
            }
            else if ( LA29_0==EOL||LA29_0==21||LA29_0==28||LA29_0==30||(LA29_0>=32 && LA29_0<=36) ) {
                alt29=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("274:46: ( \';\' )?", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:274:46: ';'
                    {
                    match(input,15,FOLLOW_15_in_salience842); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_salience845);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:281:1: no_loop returns [AttributeDescr d] : loc= 'no-loop' opt_eol ( ';' )? opt_eol ;
    public AttributeDescr no_loop() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;


        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:286:17: (loc= 'no-loop' opt_eol ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:286:17: loc= 'no-loop' opt_eol ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,33,FOLLOW_33_in_no_loop875); 
            following.push(FOLLOW_opt_eol_in_no_loop877);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:286:39: ( ';' )?
            int alt30=2;
            int LA30_0 = input.LA(1);
            if ( LA30_0==15 ) {
                alt30=1;
            }
            else if ( LA30_0==EOL||LA30_0==21||LA30_0==28||LA30_0==30||(LA30_0>=32 && LA30_0<=36) ) {
                alt30=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("286:39: ( \';\' )?", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:286:39: ';'
                    {
                    match(input,15,FOLLOW_15_in_no_loop879); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_no_loop882);
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


    // $ANTLR start xor_group
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:293:1: xor_group returns [AttributeDescr d] : loc= 'xor-group' opt_eol name= STRING ( ';' )? opt_eol ;
    public AttributeDescr xor_group() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token name=null;


        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:298:17: (loc= 'xor-group' opt_eol name= STRING ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:298:17: loc= 'xor-group' opt_eol name= STRING ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,34,FOLLOW_34_in_xor_group912); 
            following.push(FOLLOW_opt_eol_in_xor_group914);
            opt_eol();
            following.pop();

            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_xor_group918); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:298:53: ( ';' )?
            int alt31=2;
            int LA31_0 = input.LA(1);
            if ( LA31_0==15 ) {
                alt31=1;
            }
            else if ( LA31_0==EOL||LA31_0==21||LA31_0==28||LA31_0==30||(LA31_0>=32 && LA31_0<=36) ) {
                alt31=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("298:53: ( \';\' )?", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:298:53: ';'
                    {
                    match(input,15,FOLLOW_15_in_xor_group920); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_xor_group923);
            opt_eol();
            following.pop();


            			d = new AttributeDescr( "xor-group", getString( name ) );
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
    // $ANTLR end xor_group


    // $ANTLR start agenda_group
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:305:1: agenda_group returns [AttributeDescr d] : loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol ;
    public AttributeDescr agenda_group() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token name=null;


        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:310:17: (loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:310:17: loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,35,FOLLOW_35_in_agenda_group952); 
            following.push(FOLLOW_opt_eol_in_agenda_group954);
            opt_eol();
            following.pop();

            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group958); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:310:56: ( ';' )?
            int alt32=2;
            int LA32_0 = input.LA(1);
            if ( LA32_0==15 ) {
                alt32=1;
            }
            else if ( LA32_0==EOL||LA32_0==21||LA32_0==28||LA32_0==30||(LA32_0>=32 && LA32_0<=36) ) {
                alt32=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("310:56: ( \';\' )?", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:310:56: ';'
                    {
                    match(input,15,FOLLOW_15_in_agenda_group960); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_agenda_group963);
            opt_eol();
            following.pop();


            			d = new AttributeDescr( "agenda-group", getString( name ) );
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
    // $ANTLR end agenda_group


    // $ANTLR start duration
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:318:1: duration returns [AttributeDescr d] : loc= 'duration' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr duration() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token i=null;


        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:323:17: (loc= 'duration' opt_eol i= INT ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:323:17: loc= 'duration' opt_eol i= INT ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,36,FOLLOW_36_in_duration995); 
            following.push(FOLLOW_opt_eol_in_duration997);
            opt_eol();
            following.pop();

            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1001); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:323:46: ( ';' )?
            int alt33=2;
            int LA33_0 = input.LA(1);
            if ( LA33_0==15 ) {
                alt33=1;
            }
            else if ( LA33_0==EOL||LA33_0==21||LA33_0==28||LA33_0==30||(LA33_0>=32 && LA33_0<=36) ) {
                alt33=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("323:46: ( \';\' )?", 33, 0, input);

                throw nvae;
            }
            switch (alt33) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:323:46: ';'
                    {
                    match(input,15,FOLLOW_15_in_duration1003); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_duration1006);
            opt_eol();
            following.pop();


            			d = new AttributeDescr( "duration", i.getText() );
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
    // $ANTLR end duration


    // $ANTLR start normal_lhs_block
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:331:1: normal_lhs_block[AndDescr descrs] : (d= lhs )* ;
    public void normal_lhs_block(AndDescr descrs) throws RecognitionException {   
        PatternDescr d = null;


        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:333:17: ( (d= lhs )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:333:17: (d= lhs )*
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:333:17: (d= lhs )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);
                if ( LA34_0==ID||LA34_0==20||(LA34_0>=50 && LA34_0<=52) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:333:25: d= lhs
            	    {
            	    following.push(FOLLOW_lhs_in_normal_lhs_block1032);
            	    d=lhs();
            	    following.pop();

            	     descrs.addDescr( d ); 

            	    }
            	    break;

            	default :
            	    break loop34;
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:341:1: expander_lhs_block[AndDescr descrs] : ( options {greedy=false; } : '>' d= lhs | ( options {greedy=false; } : text= paren_chunk EOL ) )* ;
    public void expander_lhs_block(AndDescr descrs) throws RecognitionException {   
        PatternDescr d = null;

        String text = null;


        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:344:17: ( ( options {greedy=false; } : '>' d= lhs | ( options {greedy=false; } : text= paren_chunk EOL ) )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:344:17: ( options {greedy=false; } : '>' d= lhs | ( options {greedy=false; } : text= paren_chunk EOL ) )*
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:344:17: ( options {greedy=false; } : '>' d= lhs | ( options {greedy=false; } : text= paren_chunk EOL ) )*
            loop35:
            do {
                int alt35=3;
                switch ( input.LA(1) ) {
                case 26:
                    alt35=3;
                    break;
                case 30:
                    alt35=3;
                    break;
                case 37:
                    alt35=1;
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
                case 34:
                case 35:
                case 36:
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
                case 52:
                case 53:
                case 54:
                    alt35=2;
                    break;

                }

                switch (alt35) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:345:25: '>' d= lhs
            	    {
            	    match(input,37,FOLLOW_37_in_expander_lhs_block1076); 
            	    following.push(FOLLOW_lhs_in_expander_lhs_block1080);
            	    d=lhs();
            	    following.pop();

            	     descrs.addDescr( d ); 

            	    }
            	    break;
            	case 2 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:347:25: ( options {greedy=false; } : text= paren_chunk EOL )
            	    {
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:347:25: ( options {greedy=false; } : text= paren_chunk EOL )
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:348:33: text= paren_chunk EOL
            	    {
            	    following.push(FOLLOW_paren_chunk_in_expander_lhs_block1111);
            	    text=paren_chunk();
            	    following.pop();

            	    match(input,EOL,FOLLOW_EOL_in_expander_lhs_block1113); 

            	    					d = runWhenExpander( text );
            	    					descrs.addDescr( d );
            	    					text = null;
            	    					d = null;
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    break loop35;
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:362:1: lhs returns [PatternDescr d] : l= lhs_or ;
    public PatternDescr lhs() throws RecognitionException {   
        PatternDescr d;
        PatternDescr l = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:366:17: (l= lhs_or )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:366:17: l= lhs_or
            {
            following.push(FOLLOW_lhs_or_in_lhs1159);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:370:1: lhs_column returns [PatternDescr d] : (f= fact_binding | f= fact );
    public PatternDescr lhs_column() throws RecognitionException {   
        PatternDescr d;
        PatternDescr f = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:374:17: (f= fact_binding | f= fact )
            int alt36=2;
            alt36 = dfa36.predict(input); 
            switch (alt36) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:374:17: f= fact_binding
                    {
                    following.push(FOLLOW_fact_binding_in_lhs_column1186);
                    f=fact_binding();
                    following.pop();

                     d = f; 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:375:17: f= fact
                    {
                    following.push(FOLLOW_fact_in_lhs_column1195);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:378:1: fact_binding returns [PatternDescr d] : id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )* ;
    public PatternDescr fact_binding() throws RecognitionException {   
        PatternDescr d;
        Token id=null;
        PatternDescr f = null;



        		d=null;
        		boolean multi=false;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:384:17: (id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:384:17: id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding1227); 

             			System.err.println( "fact_binding(" + id.getText() + ")" );
             		
            following.push(FOLLOW_opt_eol_in_fact_binding1242);
            opt_eol();
            following.pop();

            match(input,29,FOLLOW_29_in_fact_binding1244); 
            following.push(FOLLOW_opt_eol_in_fact_binding1246);
            opt_eol();
            following.pop();

            following.push(FOLLOW_fact_in_fact_binding1254);
            f=fact();
            following.pop();

            following.push(FOLLOW_opt_eol_in_fact_binding1256);
            opt_eol();
            following.pop();


             			((ColumnDescr)f).setIdentifier( id.getText() );
             			d = f;
             		
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:395:17: ( 'or' f= fact )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);
                if ( LA37_0==38 ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:395:25: 'or' f= fact
            	    {
            	    match(input,38,FOLLOW_38_in_fact_binding1268); 
            	    	if ( ! multi ) {
            	     					PatternDescr first = d;
            	     					d = new OrDescr();
            	     					((OrDescr)d).addDescr( first );
            	     					multi=true;
            	     				}
            	     			
            	    following.push(FOLLOW_fact_in_fact_binding1282);
            	    f=fact();
            	    following.pop();


            	     				((ColumnDescr)f).setIdentifier( id.getText() );
            	     				((OrDescr)d).addDescr( f );
            	     			

            	    }
            	    break;

            	default :
            	    break loop37;
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:411:1: fact returns [PatternDescr d] : id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol ;
    public PatternDescr fact() throws RecognitionException {   
        PatternDescr d;
        Token id=null;
        List c = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:415:17: (id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:415:17: id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact1322); 
             
             			d = new ColumnDescr( id.getText() ); 
             			d.setLocation( id.getLine(), id.getCharPositionInLine() );
             		
            following.push(FOLLOW_opt_eol_in_fact1330);
            opt_eol();
            following.pop();

            match(input,20,FOLLOW_20_in_fact1336); 
            following.push(FOLLOW_opt_eol_in_fact1338);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:420:29: (c= constraints )?
            int alt38=2;
            alt38 = dfa38.predict(input); 
            switch (alt38) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:420:33: c= constraints
                    {
                    following.push(FOLLOW_constraints_in_fact1344);
                    c=constraints();
                    following.pop();


                    		 			for ( Iterator cIter = c.iterator() ; cIter.hasNext() ; ) {
                     						((ColumnDescr)d).addDescr( (PatternDescr) cIter.next() );
                     					}
                     				

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_fact1363);
            opt_eol();
            following.pop();

            match(input,22,FOLLOW_22_in_fact1365); 
            following.push(FOLLOW_opt_eol_in_fact1367);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:431:1: constraints returns [List constraints] : opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol ;
    public List constraints() throws RecognitionException {   
        List constraints;

        		constraints = new ArrayList();
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:435:17: ( opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:435:17: opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraints1392);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:436:17: ( constraint[constraints] | predicate[constraints] )
            int alt39=2;
            int LA39_0 = input.LA(1);
            if ( LA39_0==EOL ) {
                alt39=1;
            }
            else if ( LA39_0==ID ) {
                int LA39_2 = input.LA(2);
                if ( LA39_2==29 ) {
                    int LA39_3 = input.LA(3);
                    if ( LA39_3==ID ) {
                        int LA39_6 = input.LA(4);
                        if ( LA39_6==46 ) {
                            alt39=2;
                        }
                        else if ( LA39_6==EOL||LA39_6==37||(LA39_6>=39 && LA39_6<=45) ) {
                            alt39=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("436:17: ( constraint[constraints] | predicate[constraints] )", 39, 6, input);

                            throw nvae;
                        }
                    }
                    else if ( LA39_3==EOL ) {
                        alt39=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("436:17: ( constraint[constraints] | predicate[constraints] )", 39, 3, input);

                        throw nvae;
                    }
                }
                else if ( LA39_2==EOL||LA39_2==37||(LA39_2>=39 && LA39_2<=45) ) {
                    alt39=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("436:17: ( constraint[constraints] | predicate[constraints] )", 39, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("436:17: ( constraint[constraints] | predicate[constraints] )", 39, 0, input);

                throw nvae;
            }
            switch (alt39) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:436:18: constraint[constraints]
                    {
                    following.push(FOLLOW_constraint_in_constraints1397);
                    constraint(constraints);
                    following.pop();


                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:436:42: predicate[constraints]
                    {
                    following.push(FOLLOW_predicate_in_constraints1400);
                    predicate(constraints);
                    following.pop();


                    }
                    break;

            }

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:437:17: ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )*
            loop41:
            do {
                int alt41=2;
                alt41 = dfa41.predict(input); 
                switch (alt41) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:437:19: opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] )
            	    {
            	    following.push(FOLLOW_opt_eol_in_constraints1408);
            	    opt_eol();
            	    following.pop();

            	    match(input,21,FOLLOW_21_in_constraints1410); 
            	    following.push(FOLLOW_opt_eol_in_constraints1412);
            	    opt_eol();
            	    following.pop();

            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:437:39: ( constraint[constraints] | predicate[constraints] )
            	    int alt40=2;
            	    int LA40_0 = input.LA(1);
            	    if ( LA40_0==EOL ) {
            	        alt40=1;
            	    }
            	    else if ( LA40_0==ID ) {
            	        int LA40_2 = input.LA(2);
            	        if ( LA40_2==29 ) {
            	            int LA40_3 = input.LA(3);
            	            if ( LA40_3==ID ) {
            	                int LA40_6 = input.LA(4);
            	                if ( LA40_6==46 ) {
            	                    alt40=2;
            	                }
            	                else if ( LA40_6==EOL||LA40_6==37||(LA40_6>=39 && LA40_6<=45) ) {
            	                    alt40=1;
            	                }
            	                else {
            	                    NoViableAltException nvae =
            	                        new NoViableAltException("437:39: ( constraint[constraints] | predicate[constraints] )", 40, 6, input);

            	                    throw nvae;
            	                }
            	            }
            	            else if ( LA40_3==EOL ) {
            	                alt40=1;
            	            }
            	            else {
            	                NoViableAltException nvae =
            	                    new NoViableAltException("437:39: ( constraint[constraints] | predicate[constraints] )", 40, 3, input);

            	                throw nvae;
            	            }
            	        }
            	        else if ( LA40_2==EOL||LA40_2==37||(LA40_2>=39 && LA40_2<=45) ) {
            	            alt40=1;
            	        }
            	        else {
            	            NoViableAltException nvae =
            	                new NoViableAltException("437:39: ( constraint[constraints] | predicate[constraints] )", 40, 2, input);

            	            throw nvae;
            	        }
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("437:39: ( constraint[constraints] | predicate[constraints] )", 40, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt40) {
            	        case 1 :
            	            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:437:40: constraint[constraints]
            	            {
            	            following.push(FOLLOW_constraint_in_constraints1415);
            	            constraint(constraints);
            	            following.pop();


            	            }
            	            break;
            	        case 2 :
            	            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:437:64: predicate[constraints]
            	            {
            	            following.push(FOLLOW_predicate_in_constraints1418);
            	            predicate(constraints);
            	            following.pop();


            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop41;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_constraints1426);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:441:1: constraint[List constraints] : opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= literal_constraint | rvc= retval_constraint ) opt_eol ;
    public void constraint(List constraints) throws RecognitionException {   
        Token fb=null;
        Token f=null;
        Token op=null;
        Token bvc=null;
        String lc = null;

        String rvc = null;



        		PatternDescr d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:445:17: ( opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= literal_constraint | rvc= retval_constraint ) opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:445:17: opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= literal_constraint | rvc= retval_constraint ) opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraint1445);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:446:17: (fb= ID opt_eol ':' opt_eol )?
            int alt42=2;
            alt42 = dfa42.predict(input); 
            switch (alt42) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:446:19: fb= ID opt_eol ':' opt_eol
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint1453); 
                    following.push(FOLLOW_opt_eol_in_constraint1455);
                    opt_eol();
                    following.pop();

                    match(input,29,FOLLOW_29_in_constraint1457); 
                    following.push(FOLLOW_opt_eol_in_constraint1459);
                    opt_eol();
                    following.pop();


                    }
                    break;

            }

            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint1469); 

            			if ( fb != null ) {
            				System.err.println( "fb: " + fb.getText() );
            				System.err.println( " f: " + f.getText() );
            				d = new FieldBindingDescr( f.getText(), fb.getText() );
            				System.err.println( "fbd: " + d );
            				
            				d.setLocation( f.getLine(), f.getCharPositionInLine() );
            				constraints.add( d );
            			} 
            		
            following.push(FOLLOW_opt_eol_in_constraint1479);
            opt_eol();
            following.pop();

            op=(Token)input.LT(1);
            if ( input.LA(1)==37||(input.LA(1)>=39 && input.LA(1)<=45) ) {
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint1486);    throw mse;
            }

            following.push(FOLLOW_opt_eol_in_constraint1558);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:469:41: (bvc= ID | lc= literal_constraint | rvc= retval_constraint )
            int alt43=3;
            switch ( input.LA(1) ) {
            case ID:
                alt43=1;
                break;
            case INT:
            case STRING:
            case FLOAT:
                alt43=2;
                break;
            case 20:
                alt43=3;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("469:41: (bvc= ID | lc= literal_constraint | rvc= retval_constraint )", 43, 0, input);

                throw nvae;
            }

            switch (alt43) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:469:49: bvc= ID
                    {
                    bvc=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint1576); 

                    							d = new BoundVariableDescr( f.getText(), op.getText(), bvc.getText() );
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    							constraints.add( d );
                    						

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:476:49: lc= literal_constraint
                    {
                    following.push(FOLLOW_literal_constraint_in_constraint1601);
                    lc=literal_constraint();
                    following.pop();

                     
                    							d = new LiteralDescr( f.getText(), op.getText(), lc ); 
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    							constraints.add( d );
                    						

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:482:49: rvc= retval_constraint
                    {
                    following.push(FOLLOW_retval_constraint_in_constraint1621);
                    rvc=retval_constraint();
                    following.pop();

                     
                    							d = new ReturnValueDescr( f.getText(), op.getText(), rvc ); 
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    							constraints.add( d );
                    						

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_constraint1642);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:492:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT ) ;
    public String literal_constraint() throws RecognitionException {   
        String text;
        Token t=null;


        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:496:17: ( (t= STRING | t= INT | t= FLOAT ) )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:496:17: (t= STRING | t= INT | t= FLOAT )
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:496:17: (t= STRING | t= INT | t= FLOAT )
            int alt44=3;
            switch ( input.LA(1) ) {
            case STRING:
                alt44=1;
                break;
            case INT:
                alt44=2;
                break;
            case FLOAT:
                alt44=3;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("496:17: (t= STRING | t= INT | t= FLOAT )", 44, 0, input);

                throw nvae;
            }

            switch (alt44) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:496:25: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint1669); 
                     text = getString( t ); 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:497:25: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint1680); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:498:25: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint1693); 
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:502:1: retval_constraint returns [String text] : '(' c= paren_chunk ')' ;
    public String retval_constraint() throws RecognitionException {   
        String text;
        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:507:17: ( '(' c= paren_chunk ')' )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:507:17: '(' c= paren_chunk ')'
            {
            match(input,20,FOLLOW_20_in_retval_constraint1725); 
            following.push(FOLLOW_paren_chunk_in_retval_constraint1729);
            c=paren_chunk();
            following.pop();

            match(input,22,FOLLOW_22_in_retval_constraint1731); 
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:510:1: predicate[List constraints] : decl= ID ':' field= ID '->' '(' text= paren_chunk ')' ;
    public void predicate(List constraints) throws RecognitionException {   
        Token decl=null;
        Token field=null;
        String text = null;


        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:512:17: (decl= ID ':' field= ID '->' '(' text= paren_chunk ')' )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:512:17: decl= ID ':' field= ID '->' '(' text= paren_chunk ')'
            {
            decl=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate1749); 
            match(input,29,FOLLOW_29_in_predicate1751); 
            field=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate1755); 
            match(input,46,FOLLOW_46_in_predicate1757); 
            match(input,20,FOLLOW_20_in_predicate1759); 
            following.push(FOLLOW_paren_chunk_in_predicate1763);
            text=paren_chunk();
            following.pop();

            match(input,22,FOLLOW_22_in_predicate1765); 

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:519:1: paren_chunk returns [String text] : ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )* ;
    public String paren_chunk() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:525:17: ( ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:525:17: ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )*
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:525:17: ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )*
            loop45:
            do {
                int alt45=3;
                switch ( input.LA(1) ) {
                case EOL:
                    alt45=3;
                    break;
                case 22:
                    alt45=3;
                    break;
                case 20:
                    alt45=1;
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
                case 52:
                case 53:
                case 54:
                    alt45=2;
                    break;

                }

                switch (alt45) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:526:25: '(' c= paren_chunk ')'
            	    {
            	    match(input,20,FOLLOW_20_in_paren_chunk1810); 
            	    following.push(FOLLOW_paren_chunk_in_paren_chunk1814);
            	    c=paren_chunk();
            	    following.pop();

            	    match(input,22,FOLLOW_22_in_paren_chunk1816); 

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
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:538:19: any= .
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
            	    break loop45;
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:550:1: curly_chunk returns [String text] : ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )* ;
    public String curly_chunk() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:556:17: ( ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:556:17: ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )*
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:556:17: ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )*
            loop46:
            do {
                int alt46=3;
                switch ( input.LA(1) ) {
                case 24:
                    alt46=3;
                    break;
                case 23:
                    alt46=1;
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
                case 52:
                case 53:
                case 54:
                    alt46=2;
                    break;

                }

                switch (alt46) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:557:25: '{' c= curly_chunk '}'
            	    {
            	    match(input,23,FOLLOW_23_in_curly_chunk1884); 
            	    following.push(FOLLOW_curly_chunk_in_curly_chunk1888);
            	    c=curly_chunk();
            	    following.pop();

            	    match(input,24,FOLLOW_24_in_curly_chunk1890); 

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
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:569:19: any= .
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
        return text;
    }
    // $ANTLR end curly_chunk


    // $ANTLR start lhs_or
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:581:1: lhs_or returns [PatternDescr d] : left= lhs_and ( ('or'|'||')right= lhs_and )* ;
    public PatternDescr lhs_or() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:586:17: (left= lhs_and ( ('or'|'||')right= lhs_and )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:586:17: left= lhs_and ( ('or'|'||')right= lhs_and )*
            {
             OrDescr or = null; 
            following.push(FOLLOW_lhs_and_in_lhs_or1948);
            left=lhs_and();
            following.pop();

            d = left; 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:588:17: ( ('or'|'||')right= lhs_and )*
            loop47:
            do {
                int alt47=2;
                int LA47_0 = input.LA(1);
                if ( LA47_0==38||LA47_0==47 ) {
                    alt47=1;
                }


                switch (alt47) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:588:25: ('or'|'||')right= lhs_and
            	    {
            	    if ( input.LA(1)==38||input.LA(1)==47 ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or1958);    throw mse;
            	    }

            	    following.push(FOLLOW_lhs_and_in_lhs_or1969);
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
            	    break loop47;
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:602:1: lhs_and returns [PatternDescr d] : left= lhs_unary ( ('and'|'&&')right= lhs_unary )* ;
    public PatternDescr lhs_and() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:607:17: (left= lhs_unary ( ('and'|'&&')right= lhs_unary )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:607:17: left= lhs_unary ( ('and'|'&&')right= lhs_unary )*
            {
             AndDescr and = null; 
            following.push(FOLLOW_lhs_unary_in_lhs_and2009);
            left=lhs_unary();
            following.pop();

             d = left; 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:609:17: ( ('and'|'&&')right= lhs_unary )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);
                if ( (LA48_0>=48 && LA48_0<=49) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:609:25: ('and'|'&&')right= lhs_unary
            	    {
            	    if ( (input.LA(1)>=48 && input.LA(1)<=49) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and2018);    throw mse;
            	    }

            	    following.push(FOLLOW_lhs_unary_in_lhs_and2029);
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
            	    break loop48;
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:623:1: lhs_unary returns [PatternDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) ;
    public PatternDescr lhs_unary() throws RecognitionException {   
        PatternDescr d;
        PatternDescr u = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:627:17: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:627:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:627:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            int alt49=5;
            switch ( input.LA(1) ) {
            case 50:
                alt49=1;
                break;
            case 51:
                alt49=2;
                break;
            case 52:
                alt49=3;
                break;
            case ID:
                alt49=4;
                break;
            case 20:
                alt49=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("627:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | \'(\' u= lhs \')\' )", 49, 0, input);

                throw nvae;
            }

            switch (alt49) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:627:25: u= lhs_exist
                    {
                    following.push(FOLLOW_lhs_exist_in_lhs_unary2067);
                    u=lhs_exist();
                    following.pop();


                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:628:25: u= lhs_not
                    {
                    following.push(FOLLOW_lhs_not_in_lhs_unary2075);
                    u=lhs_not();
                    following.pop();


                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:629:25: u= lhs_eval
                    {
                    following.push(FOLLOW_lhs_eval_in_lhs_unary2083);
                    u=lhs_eval();
                    following.pop();


                    }
                    break;
                case 4 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:630:25: u= lhs_column
                    {
                    following.push(FOLLOW_lhs_column_in_lhs_unary2091);
                    u=lhs_column();
                    following.pop();


                    }
                    break;
                case 5 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:631:25: '(' u= lhs ')'
                    {
                    match(input,20,FOLLOW_20_in_lhs_unary2097); 
                    following.push(FOLLOW_lhs_in_lhs_unary2101);
                    u=lhs();
                    following.pop();

                    match(input,22,FOLLOW_22_in_lhs_unary2103); 

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:635:1: lhs_exist returns [PatternDescr d] : loc= 'exists' column= lhs_column ;
    public PatternDescr lhs_exist() throws RecognitionException {   
        PatternDescr d;
        Token loc=null;
        PatternDescr column = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:639:17: (loc= 'exists' column= lhs_column )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:639:17: loc= 'exists' column= lhs_column
            {
            loc=(Token)input.LT(1);
            match(input,50,FOLLOW_50_in_lhs_exist2133); 
            following.push(FOLLOW_lhs_column_in_lhs_exist2137);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:646:1: lhs_not returns [NotDescr d] : loc= 'not' column= lhs_column ;
    public NotDescr lhs_not() throws RecognitionException {   
        NotDescr d;
        Token loc=null;
        PatternDescr column = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:650:17: (loc= 'not' column= lhs_column )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:650:17: loc= 'not' column= lhs_column
            {
            loc=(Token)input.LT(1);
            match(input,51,FOLLOW_51_in_lhs_not2167); 
            following.push(FOLLOW_lhs_column_in_lhs_not2171);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:657:1: lhs_eval returns [PatternDescr d] : 'eval' '(' c= paren_chunk ')' ;
    public PatternDescr lhs_eval() throws RecognitionException {   
        PatternDescr d;
        String c = null;



        		d = null;
        		String text = "";
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:662:17: ( 'eval' '(' c= paren_chunk ')' )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:662:17: 'eval' '(' c= paren_chunk ')'
            {
            match(input,52,FOLLOW_52_in_lhs_eval2197); 
            match(input,20,FOLLOW_20_in_lhs_eval2199); 
            following.push(FOLLOW_paren_chunk_in_lhs_eval2203);
            c=paren_chunk();
            following.pop();

            match(input,22,FOLLOW_22_in_lhs_eval2205); 
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:666:1: dotted_name returns [String name] : id= ID ( '.' id= ID )* ;
    public String dotted_name() throws RecognitionException {   
        String name;
        Token id=null;


        		name = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:671:17: (id= ID ( '.' id= ID )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:671:17: id= ID ( '.' id= ID )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name2237); 
             name=id.getText(); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:671:46: ( '.' id= ID )*
            loop50:
            do {
                int alt50=2;
                int LA50_0 = input.LA(1);
                if ( LA50_0==53 ) {
                    alt50=1;
                }


                switch (alt50) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:671:48: '.' id= ID
            	    {
            	    match(input,53,FOLLOW_53_in_dotted_name2243); 
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_dotted_name2247); 
            	     name = name + "." + id.getText(); 

            	    }
            	    break;

            	default :
            	    break loop50;
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:675:1: word returns [String word] : (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING );
    public String word() throws RecognitionException {   
        String word;
        Token id=null;
        Token str=null;


        		word = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:679:17: (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING )
            int alt51=11;
            switch ( input.LA(1) ) {
            case ID:
                alt51=1;
                break;
            case 16:
                alt51=2;
                break;
            case 54:
                alt51=3;
                break;
            case 27:
                alt51=4;
                break;
            case 25:
                alt51=5;
                break;
            case 32:
                alt51=6;
                break;
            case 33:
                alt51=7;
                break;
            case 28:
                alt51=8;
                break;
            case 30:
                alt51=9;
                break;
            case 26:
                alt51=10;
                break;
            case STRING:
                alt51=11;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("675:1: word returns [String word] : (id= ID | \'import\' | \'use\' | \'rule\' | \'query\' | \'salience\' | \'no-loop\' | \'when\' | \'then\' | \'end\' | str= STRING );", 51, 0, input);

                throw nvae;
            }

            switch (alt51) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:679:17: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_word2277); 
                     word=id.getText(); 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:680:17: 'import'
                    {
                    match(input,16,FOLLOW_16_in_word2289); 
                     word="import"; 

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:681:17: 'use'
                    {
                    match(input,54,FOLLOW_54_in_word2298); 
                     word="use"; 

                    }
                    break;
                case 4 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:682:17: 'rule'
                    {
                    match(input,27,FOLLOW_27_in_word2310); 
                     word="rule"; 

                    }
                    break;
                case 5 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:683:17: 'query'
                    {
                    match(input,25,FOLLOW_25_in_word2321); 
                     word="query"; 

                    }
                    break;
                case 6 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:684:17: 'salience'
                    {
                    match(input,32,FOLLOW_32_in_word2331); 
                     word="salience"; 

                    }
                    break;
                case 7 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:685:17: 'no-loop'
                    {
                    match(input,33,FOLLOW_33_in_word2339); 
                     word="no-loop"; 

                    }
                    break;
                case 8 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:686:17: 'when'
                    {
                    match(input,28,FOLLOW_28_in_word2347); 
                     word="when"; 

                    }
                    break;
                case 9 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:687:17: 'then'
                    {
                    match(input,30,FOLLOW_30_in_word2358); 
                     word="then"; 

                    }
                    break;
                case 10 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:688:17: 'end'
                    {
                    match(input,26,FOLLOW_26_in_word2369); 
                     word="end"; 

                    }
                    break;
                case 11 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:689:17: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_word2383); 
                     word=getString(str);

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


    protected DFA2 dfa2 = new DFA2();protected DFA13 dfa13 = new DFA13();protected DFA14 dfa14 = new DFA14();protected DFA15 dfa15 = new DFA15();protected DFA36 dfa36 = new DFA36();protected DFA38 dfa38 = new DFA38();protected DFA41 dfa41 = new DFA41();protected DFA42 dfa42 = new DFA42();
    class DFA2 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s1 = new DFA.State() {{alt=3;}};
        DFA.State s4 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 25:
                    return s4;

                case EOL:
                    return s2;

                case 27:
                    return s3;

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
                case 53:
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
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s4 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s3;

                case EOL:
                    return s4;

                case 21:
                case 22:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 4, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                case 53:
                    return s3;

                case EOL:
                    return s4;

                case 21:
                case 22:
                    return s2;

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
                case 53:
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

    }class DFA36 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s4 = new DFA.State() {{alt=2;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 29:
                    return s3;

                case EOL:
                    return s2;

                case 20:
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 36, 2, input);

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
                        new NoViableAltException("", 36, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA36_0 = input.LA(1);
                if ( LA36_0==ID ) {return s1;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 36, 0, input);

                throw nvae;
            }
        };

    }class DFA38 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s2 = new DFA.State() {{alt=1;}};
        DFA.State s3 = new DFA.State() {{alt=2;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s2;

                case EOL:
                    return s1;

                case 22:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 1, input);

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
                        new NoViableAltException("", 38, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA41 extends DFA {
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
                        new NoViableAltException("", 41, 1, input);

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
                        new NoViableAltException("", 41, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA42 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s4 = new DFA.State() {{alt=1;}};
        DFA.State s3 = new DFA.State() {{alt=2;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 29:
                    return s4;

                case EOL:
                    return s2;

                case 37:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 42, 2, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s2;

                case 37:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                    return s3;

                case 29:
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 42, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA42_0 = input.LA(1);
                if ( LA42_0==ID ) {return s1;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 42, 0, input);

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
    public static final BitSet FOLLOW_23_in_function415 = new BitSet(new long[]{0x007FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_curly_chunk_in_function422 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_function431 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_query463 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_query469 = new BitSet(new long[]{0x004000035E0100A0L});
    public static final BitSet FOLLOW_word_in_query473 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_query475 = new BitSet(new long[]{0x007FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_expander_lhs_block_in_query491 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query499 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_query514 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_query516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_rule539 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_rule545 = new BitSet(new long[]{0x004000035E0100A0L});
    public static final BitSet FOLLOW_word_in_rule549 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule551 = new BitSet(new long[]{0x00000000D0000000L});
    public static final BitSet FOLLOW_rule_attributes_in_rule562 = new BitSet(new long[]{0x0000000050000000L});
    public static final BitSet FOLLOW_28_in_rule576 = new BitSet(new long[]{0x0000000020000012L});
    public static final BitSet FOLLOW_29_in_rule578 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule581 = new BitSet(new long[]{0x007FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_expander_lhs_block_in_rule599 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule608 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_rule633 = new BitSet(new long[]{0x0000000020000012L});
    public static final BitSet FOLLOW_29_in_rule635 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule640 = new BitSet(new long[]{0x007FFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_26_in_rule675 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule677 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_rule_attributes695 = new BitSet(new long[]{0x0000000020000012L});
    public static final BitSet FOLLOW_29_in_rule_attributes697 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule_attributes700 = new BitSet(new long[]{0x0000001F00200002L});
    public static final BitSet FOLLOW_21_in_rule_attributes707 = new BitSet(new long[]{0x0000001F00000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes712 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule_attributes714 = new BitSet(new long[]{0x0000001F00200002L});
    public static final BitSet FOLLOW_salience_in_rule_attribute753 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute763 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_xor_group_in_rule_attribute801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_salience834 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_salience836 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_salience840 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_salience842 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_salience845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_no_loop875 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop877 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_no_loop879 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_xor_group912 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_xor_group914 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_STRING_in_xor_group918 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_xor_group920 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_xor_group923 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_agenda_group952 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_agenda_group954 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_STRING_in_agenda_group958 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_agenda_group960 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_agenda_group963 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_duration995 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_duration997 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_duration1001 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_duration1003 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_duration1006 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1032 = new BitSet(new long[]{0x001C000000100022L});
    public static final BitSet FOLLOW_37_in_expander_lhs_block1076 = new BitSet(new long[]{0x001C000000100020L});
    public static final BitSet FOLLOW_lhs_in_expander_lhs_block1080 = new BitSet(new long[]{0x007FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_paren_chunk_in_expander_lhs_block1111 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_EOL_in_expander_lhs_block1113 = new BitSet(new long[]{0x007FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1159 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column1186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_column1195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding1227 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding1242 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_fact_binding1244 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding1246 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_fact_in_fact_binding1254 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding1256 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_38_in_fact_binding1268 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_fact_in_fact_binding1282 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_ID_in_fact1322 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1330 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_fact1336 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1338 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_constraints_in_fact1344 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1363 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_fact1365 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1392 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_constraint_in_constraints1397 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_predicate_in_constraints1400 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1408 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_constraints1410 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1412 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_constraint_in_constraints1415 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_predicate_in_constraints1418 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1445 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint1453 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1455 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_constraint1457 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1459 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint1469 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1479 = new BitSet(new long[]{0x00003FA000000000L});
    public static final BitSet FOLLOW_set_in_constraint1486 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1558 = new BitSet(new long[]{0x00000000001001E0L});
    public static final BitSet FOLLOW_ID_in_constraint1576 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint1601 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint1621 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint1669 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint1680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint1693 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_retval_constraint1725 = new BitSet(new long[]{0x007FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint1729 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_retval_constraint1731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_predicate1749 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_predicate1751 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_predicate1755 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_46_in_predicate1757 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_predicate1759 = new BitSet(new long[]{0x007FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate1763 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_predicate1765 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_paren_chunk1810 = new BitSet(new long[]{0x007FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk1814 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_paren_chunk1816 = new BitSet(new long[]{0x007FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_23_in_curly_chunk1884 = new BitSet(new long[]{0x007FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk1888 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_curly_chunk1890 = new BitSet(new long[]{0x007FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1948 = new BitSet(new long[]{0x0000804000000002L});
    public static final BitSet FOLLOW_set_in_lhs_or1958 = new BitSet(new long[]{0x001C000000100020L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1969 = new BitSet(new long[]{0x0000804000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2009 = new BitSet(new long[]{0x0003000000000002L});
    public static final BitSet FOLLOW_set_in_lhs_and2018 = new BitSet(new long[]{0x001C000000100020L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2029 = new BitSet(new long[]{0x0003000000000002L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary2067 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary2075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary2083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary2091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_lhs_unary2097 = new BitSet(new long[]{0x001C000000100020L});
    public static final BitSet FOLLOW_lhs_in_lhs_unary2101 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_lhs_unary2103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_lhs_exist2133 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist2137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_lhs_not2167 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not2171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_lhs_eval2197 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_lhs_eval2199 = new BitSet(new long[]{0x007FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval2203 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_lhs_eval2205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name2237 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_53_in_dotted_name2243 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_dotted_name2247 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_ID_in_word2277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_word2289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_word2298 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_word2310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_word2321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_word2331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_word2339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_word2347 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_word2358 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_word2369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_word2383 = new BitSet(new long[]{0x0000000000000002L});

}