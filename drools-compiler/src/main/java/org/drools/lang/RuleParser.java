// $ANTLR 3.0ea8 C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g 2006-03-28 14:38:45

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "EOL", "ID", "INT", "BOOL", "STRING", "FLOAT", "MISC", "WS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "\'package\'", "\';\'", "\'import\'", "\'expander\'", "\'global\'", "\'function\'", "\'(\'", "\',\'", "\')\'", "\'{\'", "\'}\'", "\'query\'", "\'end\'", "\'rule\'", "\'when\'", "\':\'", "\'then\'", "\'attributes\'", "\'salience\'", "\'no-loop\'", "\'auto-focus\'", "\'xor-group\'", "\'agenda-group\'", "\'duration\'", "\'or\'", "\'==\'", "\'>\'", "\'>=\'", "\'<\'", "\'<=\'", "\'!=\'", "\'contains\'", "\'matches\'", "\'->\'", "\'||\'", "\'and\'", "\'&&\'", "\'exists\'", "\'not\'", "\'eval\'", "\'.\'", "\'use\'"
    };
    public static final int BOOL=7;
    public static final int INT=6;
    public static final int WS=11;
    public static final int EOF=-1;
    public static final int MISC=10;
    public static final int STRING=8;
    public static final int EOL=4;
    public static final int FLOAT=9;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=12;
    public static final int MULTI_LINE_COMMENT=14;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=13;
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
    	
    	private void runWhenExpander(String text, AndDescr descrs) throws RecognitionException {
    		String expanded = text.trim();
    		if (expanded.startsWith(">")) {
    			expanded = expanded.substring(1);  //escape !!
    		} else {
    			expanded = expander.expand( "when", text );			
    		}
    		reparseLhs( expanded, descrs );
    	}
    
    	private void reparseLhs(String text, AndDescr descrs) throws RecognitionException {
    		CharStream charStream = new ANTLRStringStream( text );
    		RuleParserLexer lexer = new RuleParserLexer( charStream );
    		TokenStream tokenStream = new CommonTokenStream( lexer );
    		RuleParser parser = new RuleParser( tokenStream );
    		
    		parser.normal_lhs_block(descrs);
    	}
    	
    	private String runThenExpander(String text) {
    		//System.err.println( "expand THEN [" + text + "]" );
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
    					expanded.append( expander.expand( "then", line ) );
    					expanded.append( eol );
    				}
    			}
    		}
    		
    		return expanded.toString();
    	}
    	
    
    	
    	private String getString(Token token) {
    		String orig = token.getText();
    		return orig.substring( 1, orig.length() -1 );
    	}



    // $ANTLR start opt_eol
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:86:1: opt_eol : ( EOL )* ;
    public void opt_eol() throws RecognitionException {   
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:87:17: ( ( EOL )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:87:17: ( EOL )*
            {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:87:17: ( EOL )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);
                if ( LA1_0==EOL ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:87:17: EOL
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:90:1: compilation_unit : opt_eol prolog (r= rule | q= query )* ;
    public void compilation_unit() throws RecognitionException {   
        RuleDescr r = null;

        QueryDescr q = null;


        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:91:17: ( opt_eol prolog (r= rule | q= query )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:91:17: opt_eol prolog (r= rule | q= query )*
            {
            following.push(FOLLOW_opt_eol_in_compilation_unit53);
            opt_eol();
            following.pop();

            following.push(FOLLOW_prolog_in_compilation_unit57);
            prolog();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:93:17: (r= rule | q= query )*
            loop2:
            do {
                int alt2=3;
                alt2 = dfa2.predict(input); 
                switch (alt2) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:93:25: r= rule
            	    {
            	    following.push(FOLLOW_rule_in_compilation_unit66);
            	    r=rule();
            	    following.pop();

            	    this.packageDescr.addRule( r ); 

            	    }
            	    break;
            	case 2 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:94:25: q= query
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:98:1: prolog : opt_eol (name= package_statement )? ( import_statement )* ( expander )? ( global )* ( function )* opt_eol ;
    public void prolog() throws RecognitionException {   
        String name = null;


        
        		String packageName = "";
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:102:17: ( opt_eol (name= package_statement )? ( import_statement )* ( expander )? ( global )* ( function )* opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:102:17: opt_eol (name= package_statement )? ( import_statement )* ( expander )? ( global )* ( function )* opt_eol
            {
            following.push(FOLLOW_opt_eol_in_prolog104);
            opt_eol();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:103:17: (name= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( LA3_0==15 ) {
                alt3=1;
            }
            else if ( LA3_0==-1||LA3_0==EOL||(LA3_0>=17 && LA3_0<=20)||LA3_0==26||LA3_0==28 ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("103:17: (name= package_statement )?", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:103:19: name= package_statement
                    {
                    following.push(FOLLOW_package_statement_in_prolog112);
                    name=package_statement();
                    following.pop();

                     packageName = name; 

                    }
                    break;

            }

             
            			this.packageDescr = new PackageDescr( name ); 
            		
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:107:17: ( import_statement )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);
                if ( LA4_0==17 ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:107:17: import_statement
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

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:108:17: ( expander )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( LA5_0==18 ) {
                alt5=1;
            }
            else if ( LA5_0==-1||LA5_0==EOL||(LA5_0>=19 && LA5_0<=20)||LA5_0==26||LA5_0==28 ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("108:17: ( expander )?", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:108:17: expander
                    {
                    following.push(FOLLOW_expander_in_prolog130);
                    expander();
                    following.pop();


                    }
                    break;

            }

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:109:17: ( global )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);
                if ( LA6_0==19 ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:109:17: global
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

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:110:17: ( function )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);
                if ( LA7_0==20 ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:110:17: function
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:114:1: package_statement returns [String packageName] : 'package' opt_eol name= dotted_name ( ';' )? opt_eol ;
    public String package_statement() throws RecognitionException {   
        String packageName;
        String name = null;


        
        		packageName = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:119:17: ( 'package' opt_eol name= dotted_name ( ';' )? opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:119:17: 'package' opt_eol name= dotted_name ( ';' )? opt_eol
            {
            match(input,15,FOLLOW_15_in_package_statement170); 
            following.push(FOLLOW_opt_eol_in_package_statement172);
            opt_eol();
            following.pop();

            following.push(FOLLOW_dotted_name_in_package_statement176);
            name=dotted_name();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:119:52: ( ';' )?
            int alt8=2;
            int LA8_0 = input.LA(1);
            if ( LA8_0==16 ) {
                alt8=1;
            }
            else if ( LA8_0==-1||LA8_0==EOL||(LA8_0>=17 && LA8_0<=20)||LA8_0==26||LA8_0==28 ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("119:52: ( \';\' )?", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:119:52: ';'
                    {
                    match(input,16,FOLLOW_16_in_package_statement178); 

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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:125:1: import_statement : 'import' opt_eol name= dotted_name ( ';' )? opt_eol ;
    public void import_statement() throws RecognitionException {   
        String name = null;


        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:126:17: ( 'import' opt_eol name= dotted_name ( ';' )? opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:126:17: 'import' opt_eol name= dotted_name ( ';' )? opt_eol
            {
            match(input,17,FOLLOW_17_in_import_statement197); 
            following.push(FOLLOW_opt_eol_in_import_statement199);
            opt_eol();
            following.pop();

            following.push(FOLLOW_dotted_name_in_import_statement203);
            name=dotted_name();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:126:51: ( ';' )?
            int alt9=2;
            int LA9_0 = input.LA(1);
            if ( LA9_0==16 ) {
                alt9=1;
            }
            else if ( LA9_0==-1||LA9_0==EOL||(LA9_0>=17 && LA9_0<=20)||LA9_0==26||LA9_0==28 ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("126:51: ( \';\' )?", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:126:51: ';'
                    {
                    match(input,16,FOLLOW_16_in_import_statement205); 

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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:132:1: expander : 'expander' (name= dotted_name )? ( ';' )? opt_eol ;
    public void expander() throws RecognitionException {   
        String name = null;


        
        		String config=null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:136:17: ( 'expander' (name= dotted_name )? ( ';' )? opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:136:17: 'expander' (name= dotted_name )? ( ';' )? opt_eol
            {
            match(input,18,FOLLOW_18_in_expander230); 
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:136:28: (name= dotted_name )?
            int alt10=2;
            int LA10_0 = input.LA(1);
            if ( LA10_0==ID ) {
                alt10=1;
            }
            else if ( LA10_0==-1||LA10_0==EOL||LA10_0==16||(LA10_0>=19 && LA10_0<=20)||LA10_0==26||LA10_0==28 ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("136:28: (name= dotted_name )?", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:136:29: name= dotted_name
                    {
                    following.push(FOLLOW_dotted_name_in_expander235);
                    name=dotted_name();
                    following.pop();


                    }
                    break;

            }

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:136:48: ( ';' )?
            int alt11=2;
            int LA11_0 = input.LA(1);
            if ( LA11_0==16 ) {
                alt11=1;
            }
            else if ( LA11_0==-1||LA11_0==EOL||(LA11_0>=19 && LA11_0<=20)||LA11_0==26||LA11_0==28 ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("136:48: ( \';\' )?", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:136:48: ';'
                    {
                    match(input,16,FOLLOW_16_in_expander239); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_expander242);
            opt_eol();
            following.pop();

            
            			if (expanderResolver == null) 
            				throw new IllegalArgumentException("Unable to use expander. Make sure a expander or dsl config is being passed to the parser. [ExpanderResolver was not set].");
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:144:1: global : 'global' type= dotted_name id= ID ( ';' )? opt_eol ;
    public void global() throws RecognitionException {   
        Token id=null;
        String type = null;


        
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:148:17: ( 'global' type= dotted_name id= ID ( ';' )? opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:148:17: 'global' type= dotted_name id= ID ( ';' )? opt_eol
            {
            match(input,19,FOLLOW_19_in_global266); 
            following.push(FOLLOW_dotted_name_in_global270);
            type=dotted_name();
            following.pop();

            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_global274); 
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:148:49: ( ';' )?
            int alt12=2;
            int LA12_0 = input.LA(1);
            if ( LA12_0==16 ) {
                alt12=1;
            }
            else if ( LA12_0==-1||LA12_0==EOL||(LA12_0>=19 && LA12_0<=20)||LA12_0==26||LA12_0==28 ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("148:49: ( \';\' )?", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:148:49: ';'
                    {
                    match(input,16,FOLLOW_16_in_global276); 

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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:154:1: function : 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol ;
    public void function() throws RecognitionException {   
        Token name=null;
        Token paramName=null;
        String retType = null;

        String paramType = null;

        String body = null;


        
        		FunctionDescr f = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:159:17: ( 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:159:17: 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol
            {
            match(input,20,FOLLOW_20_in_function303); 
            following.push(FOLLOW_opt_eol_in_function305);
            opt_eol();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:159:36: (retType= dotted_name )?
            int alt13=2;
            alt13 = dfa13.predict(input); 
            switch (alt13) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:159:37: retType= dotted_name
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

            
            			//System.err.println( "function :: " + name.getText() );
            			f = new FunctionDescr( name.getText(), retType );
            		
            match(input,21,FOLLOW_21_in_function329); 
            following.push(FOLLOW_opt_eol_in_function331);
            opt_eol();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:165:25: ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )?
            int alt17=2;
            int LA17_0 = input.LA(1);
            if ( (LA17_0>=EOL && LA17_0<=ID) ) {
                alt17=1;
            }
            else if ( LA17_0==23 ) {
                alt17=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("165:25: ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( \',\' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )?", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:165:33: (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )*
                    {
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:165:33: (paramType= dotted_name )?
                    int alt14=2;
                    alt14 = dfa14.predict(input); 
                    switch (alt14) {
                        case 1 :
                            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:165:34: paramType= dotted_name
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
                    				
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:169:33: ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )*
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);
                        if ( LA16_0==22 ) {
                            alt16=1;
                        }


                        switch (alt16) {
                    	case 1 :
                    	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:169:41: ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol
                    	    {
                    	    match(input,22,FOLLOW_22_in_function365); 
                    	    following.push(FOLLOW_opt_eol_in_function367);
                    	    opt_eol();
                    	    following.pop();

                    	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:169:53: (paramType= dotted_name )?
                    	    int alt15=2;
                    	    alt15 = dfa15.predict(input); 
                    	    switch (alt15) {
                    	        case 1 :
                    	            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:169:54: paramType= dotted_name
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

            match(input,23,FOLLOW_23_in_function407); 
            following.push(FOLLOW_opt_eol_in_function411);
            opt_eol();
            following.pop();

            match(input,24,FOLLOW_24_in_function415); 
            following.push(FOLLOW_curly_chunk_in_function422);
            body=curly_chunk();
            following.pop();

            
            				f.setText( body );
            			
            match(input,25,FOLLOW_25_in_function431); 
            
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:190:1: query returns [QueryDescr query] : opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol ;
    public QueryDescr query() throws RecognitionException {   
        QueryDescr query;
        Token loc=null;
        String queryName = null;


        
        		query = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:195:17: ( opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:195:17: opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol
            {
            following.push(FOLLOW_opt_eol_in_query463);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,26,FOLLOW_26_in_query469); 
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
            		
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:203:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
            int alt18=2;
            switch ( input.LA(1) ) {
            case 21:
                int LA18_1 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("203:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 1, input);

                    throw nvae;
                }
                break;
            case EOL:
            case INT:
            case BOOL:
            case STRING:
            case FLOAT:
            case MISC:
            case WS:
            case SH_STYLE_SINGLE_LINE_COMMENT:
            case C_STYLE_SINGLE_LINE_COMMENT:
            case MULTI_LINE_COMMENT:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
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
            case 55:
            case 56:
                alt18=1;
                break;
            case 27:
                int LA18_3 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("203:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 3, input);

                    throw nvae;
                }
                break;
            case 52:
                int LA18_4 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("203:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 4, input);

                    throw nvae;
                }
                break;
            case 53:
                int LA18_5 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("203:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 5, input);

                    throw nvae;
                }
                break;
            case 54:
                int LA18_6 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("203:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 6, input);

                    throw nvae;
                }
                break;
            case ID:
                int LA18_7 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("203:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 7, input);

                    throw nvae;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("203:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:204:25: {...}? expander_lhs_block[lhs]
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
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:205:27: normal_lhs_block[lhs]
                    {
                    following.push(FOLLOW_normal_lhs_block_in_query499);
                    normal_lhs_block(lhs);
                    following.pop();


                    }
                    break;

            }

            match(input,27,FOLLOW_27_in_query514); 
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:211:1: rule returns [RuleDescr rule] : opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? opt_eol (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* 'end' opt_eol ;
    public RuleDescr rule() throws RecognitionException {   
        RuleDescr rule;
        Token loc=null;
        Token any=null;
        String ruleName = null;


        
        		rule = null;
        		String consequence = "";
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:217:17: ( opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? opt_eol (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* 'end' opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:217:17: opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? opt_eol (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* 'end' opt_eol
            {
            following.push(FOLLOW_opt_eol_in_rule539);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,28,FOLLOW_28_in_rule545); 
            following.push(FOLLOW_word_in_rule549);
            ruleName=word();
            following.pop();

            following.push(FOLLOW_opt_eol_in_rule551);
            opt_eol();
            following.pop();

             
            			rule = new RuleDescr( ruleName, null ); 
            			rule.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:223:17: ( rule_attributes[rule] )?
            int alt19=2;
            switch ( input.LA(1) ) {
            case 30:
            case 32:
                alt19=1;
                break;
            case EOL:
            case 22:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
                alt19=1;
                break;
            case 29:
                alt19=1;
                break;
            case 31:
                alt19=1;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("223:17: ( rule_attributes[rule] )?", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:223:25: rule_attributes[rule]
                    {
                    following.push(FOLLOW_rule_attributes_in_rule562);
                    rule_attributes(rule);
                    following.pop();


                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_rule572);
            opt_eol();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:226:17: (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?
            int alt22=2;
            int LA22_0 = input.LA(1);
            if ( LA22_0==29 ) {
                alt22=1;
            }
            else if ( LA22_0==31 ) {
                alt22=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("226:17: (loc= \'when\' ( \':\' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:226:25: loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                    {
                    loc=(Token)input.LT(1);
                    match(input,29,FOLLOW_29_in_rule580); 
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:226:36: ( ':' )?
                    int alt20=2;
                    int LA20_0 = input.LA(1);
                    if ( LA20_0==30 ) {
                        int LA20_1 = input.LA(2);
                        if ( !( expander != null ) ) {
                            alt20=1;
                        }
                        else if (  expander != null  ) {
                            alt20=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("226:36: ( \':\' )?", 20, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA20_0>=EOL && LA20_0<=29)||(LA20_0>=31 && LA20_0<=56) ) {
                        alt20=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("226:36: ( \':\' )?", 20, 0, input);

                        throw nvae;
                    }
                    switch (alt20) {
                        case 1 :
                            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:226:36: ':'
                            {
                            match(input,30,FOLLOW_30_in_rule582); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_rule585);
                    opt_eol();
                    following.pop();

                     
                    				AndDescr lhs = new AndDescr(); rule.setLhs( lhs ); 
                    				lhs.setLocation( loc.getLine(), loc.getCharPositionInLine() );
                    			
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:231:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                    int alt21=2;
                    switch ( input.LA(1) ) {
                    case 21:
                        int LA21_1 = input.LA(2);
                        if (  expander != null  ) {
                            alt21=1;
                        }
                        else if ( true ) {
                            alt21=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("231:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 1, input);

                            throw nvae;
                        }
                        break;
                    case EOL:
                    case INT:
                    case BOOL:
                    case STRING:
                    case FLOAT:
                    case MISC:
                    case WS:
                    case SH_STYLE_SINGLE_LINE_COMMENT:
                    case C_STYLE_SINGLE_LINE_COMMENT:
                    case MULTI_LINE_COMMENT:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 30:
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
                    case 55:
                    case 56:
                        alt21=1;
                        break;
                    case 31:
                        int LA21_3 = input.LA(2);
                        if (  expander != null  ) {
                            alt21=1;
                        }
                        else if ( true ) {
                            alt21=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("231:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 3, input);

                            throw nvae;
                        }
                        break;
                    case 52:
                        int LA21_4 = input.LA(2);
                        if (  expander != null  ) {
                            alt21=1;
                        }
                        else if ( true ) {
                            alt21=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("231:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 4, input);

                            throw nvae;
                        }
                        break;
                    case 53:
                        int LA21_5 = input.LA(2);
                        if (  expander != null  ) {
                            alt21=1;
                        }
                        else if ( true ) {
                            alt21=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("231:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 5, input);

                            throw nvae;
                        }
                        break;
                    case 54:
                        int LA21_6 = input.LA(2);
                        if (  expander != null  ) {
                            alt21=1;
                        }
                        else if ( true ) {
                            alt21=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("231:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 6, input);

                            throw nvae;
                        }
                        break;
                    case ID:
                        int LA21_7 = input.LA(2);
                        if (  expander != null  ) {
                            alt21=1;
                        }
                        else if ( true ) {
                            alt21=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("231:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 7, input);

                            throw nvae;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("231:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 21, 0, input);

                        throw nvae;
                    }

                    switch (alt21) {
                        case 1 :
                            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:232:33: {...}? expander_lhs_block[lhs]
                            {
                            if ( !( expander != null ) ) {
                                throw new FailedPredicateException(input, "rule", " expander != null ");
                            }
                            following.push(FOLLOW_expander_lhs_block_in_rule603);
                            expander_lhs_block(lhs);
                            following.pop();


                            }
                            break;
                        case 2 :
                            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:233:35: normal_lhs_block[lhs]
                            {
                            following.push(FOLLOW_normal_lhs_block_in_rule612);
                            normal_lhs_block(lhs);
                            following.pop();


                            }
                            break;

                    }


                    }
                    break;

            }

            match(input,31,FOLLOW_31_in_rule633); 
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:237:24: ( ':' )?
            int alt23=2;
            int LA23_0 = input.LA(1);
            if ( LA23_0==30 ) {
                alt23=1;
            }
            else if ( (LA23_0>=EOL && LA23_0<=29)||(LA23_0>=31 && LA23_0<=56) ) {
                alt23=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("237:24: ( \':\' )?", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:237:24: ':'
                    {
                    match(input,30,FOLLOW_30_in_rule635); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_rule639);
            opt_eol();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:238:17: ( options {greedy=false; } : any= . )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);
                if ( LA24_0==27 ) {
                    alt24=2;
                }
                else if ( (LA24_0>=EOL && LA24_0<=26)||(LA24_0>=28 && LA24_0<=56) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:238:44: any= .
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
            		
            match(input,27,FOLLOW_27_in_rule674); 
            following.push(FOLLOW_opt_eol_in_rule676);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:255:1: rule_attributes[RuleDescr rule] : ( 'attributes' )? ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )* ;
    public void rule_attributes(RuleDescr rule) throws RecognitionException {   
        AttributeDescr a = null;


        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:257:25: ( ( 'attributes' )? ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:257:25: ( 'attributes' )? ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )*
            {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:257:25: ( 'attributes' )?
            int alt25=2;
            int LA25_0 = input.LA(1);
            if ( LA25_0==32 ) {
                alt25=1;
            }
            else if ( LA25_0==EOL||LA25_0==22||(LA25_0>=29 && LA25_0<=31)||(LA25_0>=33 && LA25_0<=38) ) {
                alt25=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("257:25: ( \'attributes\' )?", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:257:25: 'attributes'
                    {
                    match(input,32,FOLLOW_32_in_rule_attributes694); 

                    }
                    break;

            }

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:257:39: ( ':' )?
            int alt26=2;
            int LA26_0 = input.LA(1);
            if ( LA26_0==30 ) {
                alt26=1;
            }
            else if ( LA26_0==EOL||LA26_0==22||LA26_0==29||LA26_0==31||(LA26_0>=33 && LA26_0<=38) ) {
                alt26=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("257:39: ( \':\' )?", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:257:39: ':'
                    {
                    match(input,30,FOLLOW_30_in_rule_attributes697); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_rule_attributes700);
            opt_eol();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:258:25: ( ( ',' )? a= rule_attribute opt_eol )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);
                if ( LA28_0==22||(LA28_0>=33 && LA28_0<=38) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:258:33: ( ',' )? a= rule_attribute opt_eol
            	    {
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:258:33: ( ',' )?
            	    int alt27=2;
            	    int LA27_0 = input.LA(1);
            	    if ( LA27_0==22 ) {
            	        alt27=1;
            	    }
            	    else if ( (LA27_0>=33 && LA27_0<=38) ) {
            	        alt27=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("258:33: ( \',\' )?", 27, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt27) {
            	        case 1 :
            	            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:258:33: ','
            	            {
            	            match(input,22,FOLLOW_22_in_rule_attributes707); 

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
            	    break loop28;
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:265:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= xor_group | a= auto_focus );
    public AttributeDescr rule_attribute() throws RecognitionException {   
        AttributeDescr d;
        AttributeDescr a = null;


        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:270:25: (a= salience | a= no_loop | a= agenda_group | a= duration | a= xor_group | a= auto_focus )
            int alt29=6;
            switch ( input.LA(1) ) {
            case 33:
                alt29=1;
                break;
            case 34:
                alt29=2;
                break;
            case 37:
                alt29=3;
                break;
            case 38:
                alt29=4;
                break;
            case 36:
                alt29=5;
                break;
            case 35:
                alt29=6;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("265:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= xor_group | a= auto_focus );", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:270:25: a= salience
                    {
                    following.push(FOLLOW_salience_in_rule_attribute753);
                    a=salience();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:271:25: a= no_loop
                    {
                    following.push(FOLLOW_no_loop_in_rule_attribute763);
                    a=no_loop();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 3 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:272:25: a= agenda_group
                    {
                    following.push(FOLLOW_agenda_group_in_rule_attribute774);
                    a=agenda_group();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 4 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:273:25: a= duration
                    {
                    following.push(FOLLOW_duration_in_rule_attribute787);
                    a=duration();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 5 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:274:25: a= xor_group
                    {
                    following.push(FOLLOW_xor_group_in_rule_attribute801);
                    a=xor_group();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 6 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:275:25: a= auto_focus
                    {
                    following.push(FOLLOW_auto_focus_in_rule_attribute812);
                    a=auto_focus();
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:279:1: salience returns [AttributeDescr d ] : loc= 'salience' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr salience() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:284:17: (loc= 'salience' opt_eol i= INT ( ';' )? opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:284:17: loc= 'salience' opt_eol i= INT ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,33,FOLLOW_33_in_salience845); 
            following.push(FOLLOW_opt_eol_in_salience847);
            opt_eol();
            following.pop();

            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience851); 
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:284:46: ( ';' )?
            int alt30=2;
            int LA30_0 = input.LA(1);
            if ( LA30_0==16 ) {
                alt30=1;
            }
            else if ( LA30_0==EOL||LA30_0==22||LA30_0==29||LA30_0==31||(LA30_0>=33 && LA30_0<=38) ) {
                alt30=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("284:46: ( \';\' )?", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:284:46: ';'
                    {
                    match(input,16,FOLLOW_16_in_salience853); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_salience856);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:291:1: no_loop returns [AttributeDescr d] : ( (loc= 'no-loop' opt_eol ( ';' )? opt_eol ) | (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol ) );
    public AttributeDescr no_loop() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:296:17: ( (loc= 'no-loop' opt_eol ( ';' )? opt_eol ) | (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol ) )
            int alt33=2;
            int LA33_0 = input.LA(1);
            if ( LA33_0==34 ) {
                int LA33_1 = input.LA(2);
                if ( LA33_1==BOOL ) {
                    alt33=2;
                }
                else if ( LA33_1==EOL||LA33_1==16||LA33_1==22||LA33_1==29||LA33_1==31||(LA33_1>=33 && LA33_1<=38) ) {
                    alt33=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("291:1: no_loop returns [AttributeDescr d] : ( (loc= \'no-loop\' opt_eol ( \';\' )? opt_eol ) | (loc= \'no-loop\' t= BOOL opt_eol ( \';\' )? opt_eol ) );", 33, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("291:1: no_loop returns [AttributeDescr d] : ( (loc= \'no-loop\' opt_eol ( \';\' )? opt_eol ) | (loc= \'no-loop\' t= BOOL opt_eol ( \';\' )? opt_eol ) );", 33, 0, input);

                throw nvae;
            }
            switch (alt33) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:296:17: (loc= 'no-loop' opt_eol ( ';' )? opt_eol )
                    {
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:296:17: (loc= 'no-loop' opt_eol ( ';' )? opt_eol )
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:297:25: loc= 'no-loop' opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,34,FOLLOW_34_in_no_loop891); 
                    following.push(FOLLOW_opt_eol_in_no_loop893);
                    opt_eol();
                    following.pop();

                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:297:47: ( ';' )?
                    int alt31=2;
                    int LA31_0 = input.LA(1);
                    if ( LA31_0==16 ) {
                        alt31=1;
                    }
                    else if ( LA31_0==EOL||LA31_0==22||LA31_0==29||LA31_0==31||(LA31_0>=33 && LA31_0<=38) ) {
                        alt31=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("297:47: ( \';\' )?", 31, 0, input);

                        throw nvae;
                    }
                    switch (alt31) {
                        case 1 :
                            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:297:47: ';'
                            {
                            match(input,16,FOLLOW_16_in_no_loop895); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_no_loop898);
                    opt_eol();
                    following.pop();

                    
                    				d = new AttributeDescr( "no-loop", "true" );
                    				d.setLocation( loc.getLine(), loc.getCharPositionInLine() );
                    			

                    }


                    }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:304:17: (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol )
                    {
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:304:17: (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol )
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:305:25: loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,34,FOLLOW_34_in_no_loop923); 
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop927); 
                    following.push(FOLLOW_opt_eol_in_no_loop929);
                    opt_eol();
                    following.pop();

                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:305:54: ( ';' )?
                    int alt32=2;
                    int LA32_0 = input.LA(1);
                    if ( LA32_0==16 ) {
                        alt32=1;
                    }
                    else if ( LA32_0==EOL||LA32_0==22||LA32_0==29||LA32_0==31||(LA32_0>=33 && LA32_0<=38) ) {
                        alt32=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("305:54: ( \';\' )?", 32, 0, input);

                        throw nvae;
                    }
                    switch (alt32) {
                        case 1 :
                            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:305:54: ';'
                            {
                            match(input,16,FOLLOW_16_in_no_loop931); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_no_loop934);
                    opt_eol();
                    following.pop();

                    
                    				d = new AttributeDescr( "no-loop", t.getText() );
                    				d.setLocation( loc.getLine(), loc.getCharPositionInLine() );
                    			

                    }


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
    // $ANTLR end no_loop


    // $ANTLR start auto_focus
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:315:1: auto_focus returns [AttributeDescr d] : ( (loc= 'auto-focus' opt_eol ( ';' )? opt_eol ) | (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol ) );
    public AttributeDescr auto_focus() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:320:17: ( (loc= 'auto-focus' opt_eol ( ';' )? opt_eol ) | (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol ) )
            int alt36=2;
            int LA36_0 = input.LA(1);
            if ( LA36_0==35 ) {
                int LA36_1 = input.LA(2);
                if ( LA36_1==BOOL ) {
                    alt36=2;
                }
                else if ( LA36_1==EOL||LA36_1==16||LA36_1==22||LA36_1==29||LA36_1==31||(LA36_1>=33 && LA36_1<=38) ) {
                    alt36=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("315:1: auto_focus returns [AttributeDescr d] : ( (loc= \'auto-focus\' opt_eol ( \';\' )? opt_eol ) | (loc= \'auto-focus\' t= BOOL opt_eol ( \';\' )? opt_eol ) );", 36, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("315:1: auto_focus returns [AttributeDescr d] : ( (loc= \'auto-focus\' opt_eol ( \';\' )? opt_eol ) | (loc= \'auto-focus\' t= BOOL opt_eol ( \';\' )? opt_eol ) );", 36, 0, input);

                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:320:17: (loc= 'auto-focus' opt_eol ( ';' )? opt_eol )
                    {
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:320:17: (loc= 'auto-focus' opt_eol ( ';' )? opt_eol )
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:321:25: loc= 'auto-focus' opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,35,FOLLOW_35_in_auto_focus980); 
                    following.push(FOLLOW_opt_eol_in_auto_focus982);
                    opt_eol();
                    following.pop();

                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:321:50: ( ';' )?
                    int alt34=2;
                    int LA34_0 = input.LA(1);
                    if ( LA34_0==16 ) {
                        alt34=1;
                    }
                    else if ( LA34_0==EOL||LA34_0==22||LA34_0==29||LA34_0==31||(LA34_0>=33 && LA34_0<=38) ) {
                        alt34=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("321:50: ( \';\' )?", 34, 0, input);

                        throw nvae;
                    }
                    switch (alt34) {
                        case 1 :
                            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:321:50: ';'
                            {
                            match(input,16,FOLLOW_16_in_auto_focus984); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_auto_focus987);
                    opt_eol();
                    following.pop();

                    
                    				d = new AttributeDescr( "auto-focus", "true" );
                    				d.setLocation( loc.getLine(), loc.getCharPositionInLine() );
                    			

                    }


                    }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:328:17: (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol )
                    {
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:328:17: (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol )
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:329:25: loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,35,FOLLOW_35_in_auto_focus1012); 
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1016); 
                    following.push(FOLLOW_opt_eol_in_auto_focus1018);
                    opt_eol();
                    following.pop();

                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:329:57: ( ';' )?
                    int alt35=2;
                    int LA35_0 = input.LA(1);
                    if ( LA35_0==16 ) {
                        alt35=1;
                    }
                    else if ( LA35_0==EOL||LA35_0==22||LA35_0==29||LA35_0==31||(LA35_0>=33 && LA35_0<=38) ) {
                        alt35=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("329:57: ( \';\' )?", 35, 0, input);

                        throw nvae;
                    }
                    switch (alt35) {
                        case 1 :
                            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:329:57: ';'
                            {
                            match(input,16,FOLLOW_16_in_auto_focus1020); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_auto_focus1023);
                    opt_eol();
                    following.pop();

                    
                    				d = new AttributeDescr( "auto-focus", t.getText() );
                    				d.setLocation( loc.getLine(), loc.getCharPositionInLine() );
                    			

                    }


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
    // $ANTLR end auto_focus


    // $ANTLR start xor_group
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:339:1: xor_group returns [AttributeDescr d] : loc= 'xor-group' opt_eol name= STRING ( ';' )? opt_eol ;
    public AttributeDescr xor_group() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token name=null;

        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:344:17: (loc= 'xor-group' opt_eol name= STRING ( ';' )? opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:344:17: loc= 'xor-group' opt_eol name= STRING ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,36,FOLLOW_36_in_xor_group1065); 
            following.push(FOLLOW_opt_eol_in_xor_group1067);
            opt_eol();
            following.pop();

            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_xor_group1071); 
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:344:53: ( ';' )?
            int alt37=2;
            int LA37_0 = input.LA(1);
            if ( LA37_0==16 ) {
                alt37=1;
            }
            else if ( LA37_0==EOL||LA37_0==22||LA37_0==29||LA37_0==31||(LA37_0>=33 && LA37_0<=38) ) {
                alt37=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("344:53: ( \';\' )?", 37, 0, input);

                throw nvae;
            }
            switch (alt37) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:344:53: ';'
                    {
                    match(input,16,FOLLOW_16_in_xor_group1073); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_xor_group1076);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:351:1: agenda_group returns [AttributeDescr d] : loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol ;
    public AttributeDescr agenda_group() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token name=null;

        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:356:17: (loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:356:17: loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,37,FOLLOW_37_in_agenda_group1105); 
            following.push(FOLLOW_opt_eol_in_agenda_group1107);
            opt_eol();
            following.pop();

            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1111); 
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:356:56: ( ';' )?
            int alt38=2;
            int LA38_0 = input.LA(1);
            if ( LA38_0==16 ) {
                alt38=1;
            }
            else if ( LA38_0==EOL||LA38_0==22||LA38_0==29||LA38_0==31||(LA38_0>=33 && LA38_0<=38) ) {
                alt38=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("356:56: ( \';\' )?", 38, 0, input);

                throw nvae;
            }
            switch (alt38) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:356:56: ';'
                    {
                    match(input,16,FOLLOW_16_in_agenda_group1113); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_agenda_group1116);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:364:1: duration returns [AttributeDescr d] : loc= 'duration' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr duration() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:369:17: (loc= 'duration' opt_eol i= INT ( ';' )? opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:369:17: loc= 'duration' opt_eol i= INT ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,38,FOLLOW_38_in_duration1148); 
            following.push(FOLLOW_opt_eol_in_duration1150);
            opt_eol();
            following.pop();

            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1154); 
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:369:46: ( ';' )?
            int alt39=2;
            int LA39_0 = input.LA(1);
            if ( LA39_0==16 ) {
                alt39=1;
            }
            else if ( LA39_0==EOL||LA39_0==22||LA39_0==29||LA39_0==31||(LA39_0>=33 && LA39_0<=38) ) {
                alt39=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("369:46: ( \';\' )?", 39, 0, input);

                throw nvae;
            }
            switch (alt39) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:369:46: ';'
                    {
                    match(input,16,FOLLOW_16_in_duration1156); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_duration1159);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:377:1: normal_lhs_block[AndDescr descrs] : (d= lhs )* ;
    public void normal_lhs_block(AndDescr descrs) throws RecognitionException {   
        PatternDescr d = null;


        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:379:17: ( (d= lhs )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:379:17: (d= lhs )*
            {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:379:17: (d= lhs )*
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);
                if ( LA40_0==ID||LA40_0==21||(LA40_0>=52 && LA40_0<=54) ) {
                    alt40=1;
                }


                switch (alt40) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:379:25: d= lhs
            	    {
            	    following.push(FOLLOW_lhs_in_normal_lhs_block1185);
            	    d=lhs();
            	    following.pop();

            	     descrs.addDescr( d ); 

            	    }
            	    break;

            	default :
            	    break loop40;
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:387:1: expander_lhs_block[AndDescr descrs] : ( options {greedy=false; } : text= paren_chunk EOL )* ;
    public void expander_lhs_block(AndDescr descrs) throws RecognitionException {   
        String text = null;


        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:390:17: ( ( options {greedy=false; } : text= paren_chunk EOL )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:390:17: ( options {greedy=false; } : text= paren_chunk EOL )*
            {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:390:17: ( options {greedy=false; } : text= paren_chunk EOL )*
            loop41:
            do {
                int alt41=2;
                switch ( input.LA(1) ) {
                case 27:
                    alt41=2;
                    break;
                case 31:
                    alt41=2;
                    break;
                case EOL:
                case ID:
                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
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
                case 26:
                case 28:
                case 29:
                case 30:
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
                case 55:
                case 56:
                    alt41=1;
                    break;

                }

                switch (alt41) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:391:25: text= paren_chunk EOL
            	    {
            	    following.push(FOLLOW_paren_chunk_in_expander_lhs_block1231);
            	    text=paren_chunk();
            	    following.pop();

            	    match(input,EOL,FOLLOW_EOL_in_expander_lhs_block1233); 
            	    
            	    				//only expand non null
            	    				if (text != null) {
            	    					runWhenExpander( text, descrs);
            	    					text = null;
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
        return ;
    }
    // $ANTLR end expander_lhs_block


    // $ANTLR start lhs
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:406:1: lhs returns [PatternDescr d] : l= lhs_or ;
    public PatternDescr lhs() throws RecognitionException {   
        PatternDescr d;
        PatternDescr l = null;


        
        		d=null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:410:17: (l= lhs_or )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:410:17: l= lhs_or
            {
            following.push(FOLLOW_lhs_or_in_lhs1277);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:414:1: lhs_column returns [PatternDescr d] : (f= fact_binding | f= fact );
    public PatternDescr lhs_column() throws RecognitionException {   
        PatternDescr d;
        PatternDescr f = null;


        
        		d=null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:418:17: (f= fact_binding | f= fact )
            int alt42=2;
            alt42 = dfa42.predict(input); 
            switch (alt42) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:418:17: f= fact_binding
                    {
                    following.push(FOLLOW_fact_binding_in_lhs_column1304);
                    f=fact_binding();
                    following.pop();

                     d = f; 

                    }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:419:17: f= fact
                    {
                    following.push(FOLLOW_fact_in_lhs_column1313);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:422:1: fact_binding returns [PatternDescr d] : id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )* ;
    public PatternDescr fact_binding() throws RecognitionException {   
        PatternDescr d;
        Token id=null;
        PatternDescr f = null;


        
        		d=null;
        		boolean multi=false;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:428:17: (id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:428:17: id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding1345); 
            following.push(FOLLOW_opt_eol_in_fact_binding1355);
            opt_eol();
            following.pop();

            match(input,30,FOLLOW_30_in_fact_binding1357); 
            following.push(FOLLOW_opt_eol_in_fact_binding1359);
            opt_eol();
            following.pop();

            following.push(FOLLOW_fact_in_fact_binding1367);
            f=fact();
            following.pop();

            following.push(FOLLOW_opt_eol_in_fact_binding1369);
            opt_eol();
            following.pop();

            
             			((ColumnDescr)f).setIdentifier( id.getText() );
             			d = f;
             		
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:436:17: ( 'or' f= fact )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);
                if ( LA43_0==39 ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:436:25: 'or' f= fact
            	    {
            	    match(input,39,FOLLOW_39_in_fact_binding1381); 
            	    	if ( ! multi ) {
            	     					PatternDescr first = d;
            	     					d = new OrDescr();
            	     					((OrDescr)d).addDescr( first );
            	     					multi=true;
            	     				}
            	     			
            	    following.push(FOLLOW_fact_in_fact_binding1395);
            	    f=fact();
            	    following.pop();

            	    
            	     				((ColumnDescr)f).setIdentifier( id.getText() );
            	     				((OrDescr)d).addDescr( f );
            	     			

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
    // $ANTLR end fact_binding


    // $ANTLR start fact
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:452:1: fact returns [PatternDescr d] : id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol ;
    public PatternDescr fact() throws RecognitionException {   
        PatternDescr d;
        Token id=null;
        List c = null;


        
        		d=null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:456:17: (id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:456:17: id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact1435); 
             
             			d = new ColumnDescr( id.getText() ); 
             			d.setLocation( id.getLine(), id.getCharPositionInLine() );
             		
            following.push(FOLLOW_opt_eol_in_fact1443);
            opt_eol();
            following.pop();

            match(input,21,FOLLOW_21_in_fact1449); 
            following.push(FOLLOW_opt_eol_in_fact1451);
            opt_eol();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:461:29: (c= constraints )?
            int alt44=2;
            alt44 = dfa44.predict(input); 
            switch (alt44) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:461:33: c= constraints
                    {
                    following.push(FOLLOW_constraints_in_fact1457);
                    c=constraints();
                    following.pop();

                    
                    		 			for ( Iterator cIter = c.iterator() ; cIter.hasNext() ; ) {
                     						((ColumnDescr)d).addDescr( (PatternDescr) cIter.next() );
                     					}
                     				

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_fact1476);
            opt_eol();
            following.pop();

            match(input,23,FOLLOW_23_in_fact1478); 
            following.push(FOLLOW_opt_eol_in_fact1480);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:472:1: constraints returns [List constraints] : opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol ;
    public List constraints() throws RecognitionException {   
        List constraints;
        
        		constraints = new ArrayList();
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:476:17: ( opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:476:17: opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraints1505);
            opt_eol();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:477:17: ( constraint[constraints] | predicate[constraints] )
            int alt45=2;
            int LA45_0 = input.LA(1);
            if ( LA45_0==EOL ) {
                alt45=1;
            }
            else if ( LA45_0==ID ) {
                int LA45_2 = input.LA(2);
                if ( LA45_2==30 ) {
                    int LA45_3 = input.LA(3);
                    if ( LA45_3==ID ) {
                        int LA45_8 = input.LA(4);
                        if ( LA45_8==48 ) {
                            alt45=2;
                        }
                        else if ( LA45_8==EOL||(LA45_8>=22 && LA45_8<=23)||(LA45_8>=40 && LA45_8<=47) ) {
                            alt45=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("477:17: ( constraint[constraints] | predicate[constraints] )", 45, 8, input);

                            throw nvae;
                        }
                    }
                    else if ( LA45_3==EOL ) {
                        alt45=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("477:17: ( constraint[constraints] | predicate[constraints] )", 45, 3, input);

                        throw nvae;
                    }
                }
                else if ( LA45_2==EOL||(LA45_2>=22 && LA45_2<=23)||(LA45_2>=40 && LA45_2<=47) ) {
                    alt45=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("477:17: ( constraint[constraints] | predicate[constraints] )", 45, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("477:17: ( constraint[constraints] | predicate[constraints] )", 45, 0, input);

                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:477:18: constraint[constraints]
                    {
                    following.push(FOLLOW_constraint_in_constraints1510);
                    constraint(constraints);
                    following.pop();


                    }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:477:42: predicate[constraints]
                    {
                    following.push(FOLLOW_predicate_in_constraints1513);
                    predicate(constraints);
                    following.pop();


                    }
                    break;

            }

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:478:17: ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )*
            loop47:
            do {
                int alt47=2;
                alt47 = dfa47.predict(input); 
                switch (alt47) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:478:19: opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] )
            	    {
            	    following.push(FOLLOW_opt_eol_in_constraints1521);
            	    opt_eol();
            	    following.pop();

            	    match(input,22,FOLLOW_22_in_constraints1523); 
            	    following.push(FOLLOW_opt_eol_in_constraints1525);
            	    opt_eol();
            	    following.pop();

            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:478:39: ( constraint[constraints] | predicate[constraints] )
            	    int alt46=2;
            	    int LA46_0 = input.LA(1);
            	    if ( LA46_0==EOL ) {
            	        alt46=1;
            	    }
            	    else if ( LA46_0==ID ) {
            	        int LA46_2 = input.LA(2);
            	        if ( LA46_2==30 ) {
            	            int LA46_3 = input.LA(3);
            	            if ( LA46_3==ID ) {
            	                int LA46_8 = input.LA(4);
            	                if ( LA46_8==48 ) {
            	                    alt46=2;
            	                }
            	                else if ( LA46_8==EOL||(LA46_8>=22 && LA46_8<=23)||(LA46_8>=40 && LA46_8<=47) ) {
            	                    alt46=1;
            	                }
            	                else {
            	                    NoViableAltException nvae =
            	                        new NoViableAltException("478:39: ( constraint[constraints] | predicate[constraints] )", 46, 8, input);

            	                    throw nvae;
            	                }
            	            }
            	            else if ( LA46_3==EOL ) {
            	                alt46=1;
            	            }
            	            else {
            	                NoViableAltException nvae =
            	                    new NoViableAltException("478:39: ( constraint[constraints] | predicate[constraints] )", 46, 3, input);

            	                throw nvae;
            	            }
            	        }
            	        else if ( LA46_2==EOL||(LA46_2>=22 && LA46_2<=23)||(LA46_2>=40 && LA46_2<=47) ) {
            	            alt46=1;
            	        }
            	        else {
            	            NoViableAltException nvae =
            	                new NoViableAltException("478:39: ( constraint[constraints] | predicate[constraints] )", 46, 2, input);

            	            throw nvae;
            	        }
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("478:39: ( constraint[constraints] | predicate[constraints] )", 46, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt46) {
            	        case 1 :
            	            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:478:40: constraint[constraints]
            	            {
            	            following.push(FOLLOW_constraint_in_constraints1528);
            	            constraint(constraints);
            	            following.pop();


            	            }
            	            break;
            	        case 2 :
            	            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:478:64: predicate[constraints]
            	            {
            	            following.push(FOLLOW_predicate_in_constraints1531);
            	            predicate(constraints);
            	            following.pop();


            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop47;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_constraints1539);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:482:1: constraint[List constraints] : opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol (op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= literal_constraint | rvc= retval_constraint ) )? opt_eol ;
    public void constraint(List constraints) throws RecognitionException {   
        Token fb=null;
        Token f=null;
        Token op=null;
        Token bvc=null;
        String lc = null;

        String rvc = null;


        
        		PatternDescr d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:486:17: ( opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol (op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= literal_constraint | rvc= retval_constraint ) )? opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:486:17: opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol (op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= literal_constraint | rvc= retval_constraint ) )? opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraint1558);
            opt_eol();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:487:17: (fb= ID opt_eol ':' opt_eol )?
            int alt48=2;
            alt48 = dfa48.predict(input); 
            switch (alt48) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:487:19: fb= ID opt_eol ':' opt_eol
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint1566); 
                    following.push(FOLLOW_opt_eol_in_constraint1568);
                    opt_eol();
                    following.pop();

                    match(input,30,FOLLOW_30_in_constraint1570); 
                    following.push(FOLLOW_opt_eol_in_constraint1572);
                    opt_eol();
                    following.pop();


                    }
                    break;

            }

            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint1582); 
            
            			if ( fb != null ) {
            				//System.err.println( "fb: " + fb.getText() );
            				//System.err.println( " f: " + f.getText() );
            				d = new FieldBindingDescr( f.getText(), fb.getText() );
            				//System.err.println( "fbd: " + d );
            				
            				d.setLocation( f.getLine(), f.getCharPositionInLine() );
            				constraints.add( d );
            			} 
            		
            following.push(FOLLOW_opt_eol_in_constraint1592);
            opt_eol();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:500:33: (op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= literal_constraint | rvc= retval_constraint ) )?
            int alt50=2;
            int LA50_0 = input.LA(1);
            if ( (LA50_0>=40 && LA50_0<=47) ) {
                alt50=1;
            }
            else if ( LA50_0==EOL||(LA50_0>=22 && LA50_0<=23) ) {
                alt50=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("500:33: (op= (\'==\'|\'>\'|\'>=\'|\'<\'|\'<=\'|\'!=\'|\'contains\'|\'matches\') opt_eol (bvc= ID | lc= literal_constraint | rvc= retval_constraint ) )?", 50, 0, input);

                throw nvae;
            }
            switch (alt50) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:500:41: op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= literal_constraint | rvc= retval_constraint )
                    {
                    op=(Token)input.LT(1);
                    if ( (input.LA(1)>=40 && input.LA(1)<=47) ) {
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint1600);    throw mse;
                    }

                    following.push(FOLLOW_opt_eol_in_constraint1672);
                    opt_eol();
                    following.pop();

                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:510:41: (bvc= ID | lc= literal_constraint | rvc= retval_constraint )
                    int alt49=3;
                    switch ( input.LA(1) ) {
                    case ID:
                        alt49=1;
                        break;
                    case INT:
                    case BOOL:
                    case STRING:
                    case FLOAT:
                        alt49=2;
                        break;
                    case 21:
                        alt49=3;
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("510:41: (bvc= ID | lc= literal_constraint | rvc= retval_constraint )", 49, 0, input);

                        throw nvae;
                    }

                    switch (alt49) {
                        case 1 :
                            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:510:49: bvc= ID
                            {
                            bvc=(Token)input.LT(1);
                            match(input,ID,FOLLOW_ID_in_constraint1690); 
                            
                            							d = new BoundVariableDescr( f.getText(), op.getText(), bvc.getText() );
                            							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                            							constraints.add( d );
                            						

                            }
                            break;
                        case 2 :
                            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:517:49: lc= literal_constraint
                            {
                            following.push(FOLLOW_literal_constraint_in_constraint1715);
                            lc=literal_constraint();
                            following.pop();

                             
                            							d = new LiteralDescr( f.getText(), op.getText(), lc ); 
                            							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                            							constraints.add( d );
                            						

                            }
                            break;
                        case 3 :
                            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:523:49: rvc= retval_constraint
                            {
                            following.push(FOLLOW_retval_constraint_in_constraint1735);
                            rvc=retval_constraint();
                            following.pop();

                             
                            							d = new ReturnValueDescr( f.getText(), op.getText(), rvc ); 
                            							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                            							constraints.add( d );
                            						

                            }
                            break;

                    }


                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_constraint1768);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:534:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT | t= BOOL ) ;
    public String literal_constraint() throws RecognitionException {   
        String text;
        Token t=null;

        
        		text = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:538:17: ( (t= STRING | t= INT | t= FLOAT | t= BOOL ) )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:538:17: (t= STRING | t= INT | t= FLOAT | t= BOOL )
            {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:538:17: (t= STRING | t= INT | t= FLOAT | t= BOOL )
            int alt51=4;
            switch ( input.LA(1) ) {
            case STRING:
                alt51=1;
                break;
            case INT:
                alt51=2;
                break;
            case FLOAT:
                alt51=3;
                break;
            case BOOL:
                alt51=4;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("538:17: (t= STRING | t= INT | t= FLOAT | t= BOOL )", 51, 0, input);

                throw nvae;
            }

            switch (alt51) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:538:25: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint1795); 
                     text = getString( t ); 

                    }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:539:25: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint1806); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:540:25: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint1819); 
                     text = t.getText(); 

                    }
                    break;
                case 4 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:541:25: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint1830); 
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:545:1: retval_constraint returns [String text] : '(' c= paren_chunk ')' ;
    public String retval_constraint() throws RecognitionException {   
        String text;
        String c = null;


        
        		text = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:550:17: ( '(' c= paren_chunk ')' )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:550:17: '(' c= paren_chunk ')'
            {
            match(input,21,FOLLOW_21_in_retval_constraint1863); 
            following.push(FOLLOW_paren_chunk_in_retval_constraint1867);
            c=paren_chunk();
            following.pop();

            match(input,23,FOLLOW_23_in_retval_constraint1869); 
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:553:1: predicate[List constraints] : decl= ID ':' field= ID '->' '(' text= paren_chunk ')' ;
    public void predicate(List constraints) throws RecognitionException {   
        Token decl=null;
        Token field=null;
        String text = null;


        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:555:17: (decl= ID ':' field= ID '->' '(' text= paren_chunk ')' )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:555:17: decl= ID ':' field= ID '->' '(' text= paren_chunk ')'
            {
            decl=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate1887); 
            match(input,30,FOLLOW_30_in_predicate1889); 
            field=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate1893); 
            match(input,48,FOLLOW_48_in_predicate1895); 
            match(input,21,FOLLOW_21_in_predicate1897); 
            following.push(FOLLOW_paren_chunk_in_predicate1901);
            text=paren_chunk();
            following.pop();

            match(input,23,FOLLOW_23_in_predicate1903); 
            
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:562:1: paren_chunk returns [String text] : ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )* ;
    public String paren_chunk() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;


        
        		text = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:568:17: ( ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:568:17: ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )*
            {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:568:17: ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )*
            loop52:
            do {
                int alt52=3;
                switch ( input.LA(1) ) {
                case EOL:
                    alt52=3;
                    break;
                case 23:
                    alt52=3;
                    break;
                case 21:
                    alt52=1;
                    break;
                case ID:
                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 22:
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
                case 55:
                case 56:
                    alt52=2;
                    break;

                }

                switch (alt52) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:569:25: '(' c= paren_chunk ')'
            	    {
            	    match(input,21,FOLLOW_21_in_paren_chunk1948); 
            	    following.push(FOLLOW_paren_chunk_in_paren_chunk1952);
            	    c=paren_chunk();
            	    following.pop();

            	    match(input,23,FOLLOW_23_in_paren_chunk1954); 
            	    
            	    				//System.err.println( "chunk [" + c + "]" );
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
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:581:19: any= .
            	    {
            	    any=(Token)input.LT(1);
            	    matchAny(input); 
            	    
            	    				//System.err.println( "any [" + any.getText() + "]" );
            	    				if ( text == null ) {
            	    					text = any.getText();
            	    				} else {
            	    					text = text + " " + any.getText(); 
            	    				} 
            	    			

            	    }
            	    break;

            	default :
            	    break loop52;
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:593:1: curly_chunk returns [String text] : ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )* ;
    public String curly_chunk() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;


        
        		text = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:599:17: ( ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:599:17: ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )*
            {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:599:17: ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )*
            loop53:
            do {
                int alt53=3;
                switch ( input.LA(1) ) {
                case 25:
                    alt53=3;
                    break;
                case 24:
                    alt53=1;
                    break;
                case EOL:
                case ID:
                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
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
                case 55:
                case 56:
                    alt53=2;
                    break;

                }

                switch (alt53) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:600:25: '{' c= curly_chunk '}'
            	    {
            	    match(input,24,FOLLOW_24_in_curly_chunk2022); 
            	    following.push(FOLLOW_curly_chunk_in_curly_chunk2026);
            	    c=curly_chunk();
            	    following.pop();

            	    match(input,25,FOLLOW_25_in_curly_chunk2028); 
            	    
            	    				//System.err.println( "chunk [" + c + "]" );
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
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:612:19: any= .
            	    {
            	    any=(Token)input.LT(1);
            	    matchAny(input); 
            	    
            	    				//System.err.println( "any [" + any.getText() + "]" );
            	    				if ( text == null ) {
            	    					text = any.getText();
            	    				} else {
            	    					text = text + " " + any.getText(); 
            	    				} 
            	    			

            	    }
            	    break;

            	default :
            	    break loop53;
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:624:1: lhs_or returns [PatternDescr d] : left= lhs_and ( ('or'|'||')right= lhs_and )* ;
    public PatternDescr lhs_or() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;


        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:629:17: (left= lhs_and ( ('or'|'||')right= lhs_and )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:629:17: left= lhs_and ( ('or'|'||')right= lhs_and )*
            {
             OrDescr or = null; 
            following.push(FOLLOW_lhs_and_in_lhs_or2086);
            left=lhs_and();
            following.pop();

            d = left; 
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:631:17: ( ('or'|'||')right= lhs_and )*
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);
                if ( LA54_0==39||LA54_0==49 ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:631:25: ('or'|'||')right= lhs_and
            	    {
            	    if ( input.LA(1)==39||input.LA(1)==49 ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or2096);    throw mse;
            	    }

            	    following.push(FOLLOW_lhs_and_in_lhs_or2107);
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
            	    break loop54;
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:645:1: lhs_and returns [PatternDescr d] : left= lhs_unary ( ('and'|'&&')right= lhs_unary )* ;
    public PatternDescr lhs_and() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;


        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:650:17: (left= lhs_unary ( ('and'|'&&')right= lhs_unary )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:650:17: left= lhs_unary ( ('and'|'&&')right= lhs_unary )*
            {
             AndDescr and = null; 
            following.push(FOLLOW_lhs_unary_in_lhs_and2147);
            left=lhs_unary();
            following.pop();

             d = left; 
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:652:17: ( ('and'|'&&')right= lhs_unary )*
            loop55:
            do {
                int alt55=2;
                int LA55_0 = input.LA(1);
                if ( (LA55_0>=50 && LA55_0<=51) ) {
                    alt55=1;
                }


                switch (alt55) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:652:25: ('and'|'&&')right= lhs_unary
            	    {
            	    if ( (input.LA(1)>=50 && input.LA(1)<=51) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and2156);    throw mse;
            	    }

            	    following.push(FOLLOW_lhs_unary_in_lhs_and2167);
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
            	    break loop55;
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:666:1: lhs_unary returns [PatternDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) ;
    public PatternDescr lhs_unary() throws RecognitionException {   
        PatternDescr d;
        PatternDescr u = null;


        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:670:17: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:670:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:670:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            int alt56=5;
            switch ( input.LA(1) ) {
            case 52:
                alt56=1;
                break;
            case 53:
                alt56=2;
                break;
            case 54:
                alt56=3;
                break;
            case ID:
                alt56=4;
                break;
            case 21:
                alt56=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("670:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | \'(\' u= lhs \')\' )", 56, 0, input);

                throw nvae;
            }

            switch (alt56) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:670:25: u= lhs_exist
                    {
                    following.push(FOLLOW_lhs_exist_in_lhs_unary2205);
                    u=lhs_exist();
                    following.pop();


                    }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:671:25: u= lhs_not
                    {
                    following.push(FOLLOW_lhs_not_in_lhs_unary2213);
                    u=lhs_not();
                    following.pop();


                    }
                    break;
                case 3 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:672:25: u= lhs_eval
                    {
                    following.push(FOLLOW_lhs_eval_in_lhs_unary2221);
                    u=lhs_eval();
                    following.pop();


                    }
                    break;
                case 4 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:673:25: u= lhs_column
                    {
                    following.push(FOLLOW_lhs_column_in_lhs_unary2229);
                    u=lhs_column();
                    following.pop();


                    }
                    break;
                case 5 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:674:25: '(' u= lhs ')'
                    {
                    match(input,21,FOLLOW_21_in_lhs_unary2235); 
                    following.push(FOLLOW_lhs_in_lhs_unary2239);
                    u=lhs();
                    following.pop();

                    match(input,23,FOLLOW_23_in_lhs_unary2241); 

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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:678:1: lhs_exist returns [PatternDescr d] : loc= 'exists' column= lhs_column ;
    public PatternDescr lhs_exist() throws RecognitionException {   
        PatternDescr d;
        Token loc=null;
        PatternDescr column = null;


        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:682:17: (loc= 'exists' column= lhs_column )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:682:17: loc= 'exists' column= lhs_column
            {
            loc=(Token)input.LT(1);
            match(input,52,FOLLOW_52_in_lhs_exist2271); 
            following.push(FOLLOW_lhs_column_in_lhs_exist2275);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:689:1: lhs_not returns [NotDescr d] : loc= 'not' column= lhs_column ;
    public NotDescr lhs_not() throws RecognitionException {   
        NotDescr d;
        Token loc=null;
        PatternDescr column = null;


        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:693:17: (loc= 'not' column= lhs_column )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:693:17: loc= 'not' column= lhs_column
            {
            loc=(Token)input.LT(1);
            match(input,53,FOLLOW_53_in_lhs_not2305); 
            following.push(FOLLOW_lhs_column_in_lhs_not2309);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:700:1: lhs_eval returns [PatternDescr d] : 'eval' '(' c= paren_chunk ')' ;
    public PatternDescr lhs_eval() throws RecognitionException {   
        PatternDescr d;
        String c = null;


        
        		d = null;
        		String text = "";
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:705:17: ( 'eval' '(' c= paren_chunk ')' )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:705:17: 'eval' '(' c= paren_chunk ')'
            {
            match(input,54,FOLLOW_54_in_lhs_eval2335); 
            match(input,21,FOLLOW_21_in_lhs_eval2337); 
            following.push(FOLLOW_paren_chunk_in_lhs_eval2341);
            c=paren_chunk();
            following.pop();

            match(input,23,FOLLOW_23_in_lhs_eval2343); 
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:709:1: dotted_name returns [String name] : id= ID ( '.' id= ID )* ;
    public String dotted_name() throws RecognitionException {   
        String name;
        Token id=null;

        
        		name = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:714:17: (id= ID ( '.' id= ID )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:714:17: id= ID ( '.' id= ID )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name2375); 
             name=id.getText(); 
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:714:46: ( '.' id= ID )*
            loop57:
            do {
                int alt57=2;
                int LA57_0 = input.LA(1);
                if ( LA57_0==55 ) {
                    alt57=1;
                }


                switch (alt57) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:714:48: '.' id= ID
            	    {
            	    match(input,55,FOLLOW_55_in_dotted_name2381); 
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_dotted_name2385); 
            	     name = name + "." + id.getText(); 

            	    }
            	    break;

            	default :
            	    break loop57;
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:718:1: word returns [String word] : (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING );
    public String word() throws RecognitionException {   
        String word;
        Token id=null;
        Token str=null;

        
        		word = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:722:17: (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING )
            int alt58=11;
            switch ( input.LA(1) ) {
            case ID:
                alt58=1;
                break;
            case 17:
                alt58=2;
                break;
            case 56:
                alt58=3;
                break;
            case 28:
                alt58=4;
                break;
            case 26:
                alt58=5;
                break;
            case 33:
                alt58=6;
                break;
            case 34:
                alt58=7;
                break;
            case 29:
                alt58=8;
                break;
            case 31:
                alt58=9;
                break;
            case 27:
                alt58=10;
                break;
            case STRING:
                alt58=11;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("718:1: word returns [String word] : (id= ID | \'import\' | \'use\' | \'rule\' | \'query\' | \'salience\' | \'no-loop\' | \'when\' | \'then\' | \'end\' | str= STRING );", 58, 0, input);

                throw nvae;
            }

            switch (alt58) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:722:17: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_word2415); 
                     word=id.getText(); 

                    }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:723:17: 'import'
                    {
                    match(input,17,FOLLOW_17_in_word2427); 
                     word="import"; 

                    }
                    break;
                case 3 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:724:17: 'use'
                    {
                    match(input,56,FOLLOW_56_in_word2436); 
                     word="use"; 

                    }
                    break;
                case 4 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:725:17: 'rule'
                    {
                    match(input,28,FOLLOW_28_in_word2448); 
                     word="rule"; 

                    }
                    break;
                case 5 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:726:17: 'query'
                    {
                    match(input,26,FOLLOW_26_in_word2459); 
                     word="query"; 

                    }
                    break;
                case 6 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:727:17: 'salience'
                    {
                    match(input,33,FOLLOW_33_in_word2469); 
                     word="salience"; 

                    }
                    break;
                case 7 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:728:17: 'no-loop'
                    {
                    match(input,34,FOLLOW_34_in_word2477); 
                     word="no-loop"; 

                    }
                    break;
                case 8 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:729:17: 'when'
                    {
                    match(input,29,FOLLOW_29_in_word2485); 
                     word="when"; 

                    }
                    break;
                case 9 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:730:17: 'then'
                    {
                    match(input,31,FOLLOW_31_in_word2496); 
                     word="then"; 

                    }
                    break;
                case 10 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:731:17: 'end'
                    {
                    match(input,27,FOLLOW_27_in_word2507); 
                     word="end"; 

                    }
                    break;
                case 11 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:732:17: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_word2521); 
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


    protected DFA2 dfa2 = new DFA2();protected DFA13 dfa13 = new DFA13();protected DFA14 dfa14 = new DFA14();protected DFA15 dfa15 = new DFA15();protected DFA42 dfa42 = new DFA42();protected DFA44 dfa44 = new DFA44();protected DFA47 dfa47 = new DFA47();protected DFA48 dfa48 = new DFA48();
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
                case 26:
                    return s4;

                case EOL:
                    return s2;

                case 28:
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

                case 28:
                    return s3;

                case 26:
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
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s5 = new DFA.State() {{alt=1;}};
        DFA.State s3 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 21:
                    return s2;

                case EOL:
                    return s3;

                case ID:
                    return s5;

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

                case 21:
                    return s2;

                case ID:
                case 55:
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

                case 22:
                case 23:
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
                case 55:
                    return s3;

                case EOL:
                    return s4;

                case 22:
                case 23:
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
        DFA.State s6 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s6;

                case EOL:
                    return s3;

                case 22:
                case 23:
                    return s2;

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

                case 22:
                case 23:
                    return s2;

                case ID:
                case 55:
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

    }class DFA42 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s4 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 21:
                    return s4;

                case EOL:
                    return s2;

                case 30:
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

                case 30:
                    return s3;

                case 21:
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

    }class DFA44 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s3 = new DFA.State() {{alt=2;}};
        DFA.State s2 = new DFA.State() {{alt=1;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 23:
                    return s3;

                case EOL:
                    return s1;

                case ID:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 44, 1, input);

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

                case 23:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 44, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA47 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 22:
                    return s3;

                case EOL:
                    return s1;

                case 23:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s1;

                case 23:
                    return s2;

                case 22:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA48 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s4 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 22:
                case 23:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                    return s4;

                case EOL:
                    return s2;

                case 30:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 2, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s2;

                case 30:
                    return s3;

                case 22:
                case 23:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA48_0 = input.LA(1);
                if ( LA48_0==ID ) {return s1;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 48, 0, input);

                throw nvae;
            }
        };

    }


    public static final BitSet FOLLOW_EOL_in_opt_eol40 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_compilation_unit53 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit57 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_rule_in_compilation_unit66 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_query_in_compilation_unit79 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_prolog104 = new BitSet(new long[]{0x00000000001E8012L});
    public static final BitSet FOLLOW_package_statement_in_prolog112 = new BitSet(new long[]{0x00000000001E0012L});
    public static final BitSet FOLLOW_import_statement_in_prolog125 = new BitSet(new long[]{0x00000000001E0012L});
    public static final BitSet FOLLOW_expander_in_prolog130 = new BitSet(new long[]{0x0000000000180012L});
    public static final BitSet FOLLOW_global_in_prolog136 = new BitSet(new long[]{0x0000000000180012L});
    public static final BitSet FOLLOW_function_in_prolog141 = new BitSet(new long[]{0x0000000000100012L});
    public static final BitSet FOLLOW_opt_eol_in_prolog146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_package_statement170 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_package_statement172 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement176 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_package_statement178 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_package_statement181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_import_statement197 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_import_statement199 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_dotted_name_in_import_statement203 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_import_statement205 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_import_statement208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_expander230 = new BitSet(new long[]{0x0000000000010032L});
    public static final BitSet FOLLOW_dotted_name_in_expander235 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_expander239 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_expander242 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_global266 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_dotted_name_in_global270 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_global274 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_global276 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_global279 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_function303 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function305 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_dotted_name_in_function310 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function314 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_function318 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function320 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_function329 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function331 = new BitSet(new long[]{0x0000000000800032L});
    public static final BitSet FOLLOW_dotted_name_in_function341 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function345 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_function349 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function351 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_22_in_function365 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function367 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_dotted_name_in_function372 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function376 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_function380 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function382 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_23_in_function407 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function411 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_function415 = new BitSet(new long[]{0x01FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_curly_chunk_in_function422 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_function431 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_function439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_query463 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_query469 = new BitSet(new long[]{0x01000006BC020120L});
    public static final BitSet FOLLOW_word_in_query473 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_query475 = new BitSet(new long[]{0x01FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_expander_lhs_block_in_query491 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query499 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_query514 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_query516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_rule539 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_rule545 = new BitSet(new long[]{0x01000006BC020120L});
    public static final BitSet FOLLOW_word_in_rule549 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule551 = new BitSet(new long[]{0x0000000140000012L});
    public static final BitSet FOLLOW_rule_attributes_in_rule562 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule572 = new BitSet(new long[]{0x00000000A0000000L});
    public static final BitSet FOLLOW_29_in_rule580 = new BitSet(new long[]{0x0000000040000012L});
    public static final BitSet FOLLOW_30_in_rule582 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule585 = new BitSet(new long[]{0x01FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_expander_lhs_block_in_rule603 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule612 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_rule633 = new BitSet(new long[]{0x0000000040000012L});
    public static final BitSet FOLLOW_30_in_rule635 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule639 = new BitSet(new long[]{0x01FFFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_27_in_rule674 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_rule_attributes694 = new BitSet(new long[]{0x0000000040000012L});
    public static final BitSet FOLLOW_30_in_rule_attributes697 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule_attributes700 = new BitSet(new long[]{0x0000007E00400002L});
    public static final BitSet FOLLOW_22_in_rule_attributes707 = new BitSet(new long[]{0x0000007E00000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes712 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule_attributes714 = new BitSet(new long[]{0x0000007E00400002L});
    public static final BitSet FOLLOW_salience_in_rule_attribute753 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute763 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_xor_group_in_rule_attribute801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_salience845 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_salience847 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_salience851 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_salience853 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_salience856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_no_loop891 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop893 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_no_loop895 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_no_loop923 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_BOOL_in_no_loop927 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop929 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_no_loop931 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_auto_focus980 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_auto_focus982 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_auto_focus984 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_auto_focus987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_auto_focus1012 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1016 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_auto_focus1018 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_auto_focus1020 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_auto_focus1023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_xor_group1065 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_xor_group1067 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_STRING_in_xor_group1071 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_xor_group1073 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_xor_group1076 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_agenda_group1105 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_agenda_group1107 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1111 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_agenda_group1113 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_agenda_group1116 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_duration1148 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_duration1150 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_duration1154 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_duration1156 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_duration1159 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1185 = new BitSet(new long[]{0x0070000000200022L});
    public static final BitSet FOLLOW_paren_chunk_in_expander_lhs_block1231 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_EOL_in_expander_lhs_block1233 = new BitSet(new long[]{0x01FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column1304 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_column1313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding1345 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding1355 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_fact_binding1357 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding1359 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_fact_in_fact_binding1367 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding1369 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_39_in_fact_binding1381 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_fact_in_fact_binding1395 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_ID_in_fact1435 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1443 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_fact1449 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1451 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_constraints_in_fact1457 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1476 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_fact1478 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1505 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_constraint_in_constraints1510 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_predicate_in_constraints1513 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1521 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_constraints1523 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1525 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_constraint_in_constraints1528 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_predicate_in_constraints1531 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1558 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint1566 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1568 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_constraint1570 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1572 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint1582 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1592 = new BitSet(new long[]{0x0000FF0000000012L});
    public static final BitSet FOLLOW_set_in_constraint1600 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1672 = new BitSet(new long[]{0x00000000002003E0L});
    public static final BitSet FOLLOW_ID_in_constraint1690 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint1715 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint1735 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint1795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint1806 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint1819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint1830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_retval_constraint1863 = new BitSet(new long[]{0x01FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint1867 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_retval_constraint1869 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_predicate1887 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_predicate1889 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_predicate1893 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_48_in_predicate1895 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_predicate1897 = new BitSet(new long[]{0x01FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate1901 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_predicate1903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_paren_chunk1948 = new BitSet(new long[]{0x01FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk1952 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_paren_chunk1954 = new BitSet(new long[]{0x01FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_24_in_curly_chunk2022 = new BitSet(new long[]{0x01FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk2026 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_curly_chunk2028 = new BitSet(new long[]{0x01FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2086 = new BitSet(new long[]{0x0002008000000002L});
    public static final BitSet FOLLOW_set_in_lhs_or2096 = new BitSet(new long[]{0x0070000000200020L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2107 = new BitSet(new long[]{0x0002008000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2147 = new BitSet(new long[]{0x000C000000000002L});
    public static final BitSet FOLLOW_set_in_lhs_and2156 = new BitSet(new long[]{0x0070000000200020L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2167 = new BitSet(new long[]{0x000C000000000002L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary2205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary2213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary2221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary2229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_lhs_unary2235 = new BitSet(new long[]{0x0070000000200020L});
    public static final BitSet FOLLOW_lhs_in_lhs_unary2239 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_lhs_unary2241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_lhs_exist2271 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist2275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_lhs_not2305 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not2309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_lhs_eval2335 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_lhs_eval2337 = new BitSet(new long[]{0x01FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval2341 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_lhs_eval2343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name2375 = new BitSet(new long[]{0x0080000000000002L});
    public static final BitSet FOLLOW_55_in_dotted_name2381 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_dotted_name2385 = new BitSet(new long[]{0x0080000000000002L});
    public static final BitSet FOLLOW_ID_in_word2415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_word2427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_56_in_word2436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_word2448 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_word2459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_word2469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_word2477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_word2485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_word2496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_word2507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_word2521 = new BitSet(new long[]{0x0000000000000002L});

}