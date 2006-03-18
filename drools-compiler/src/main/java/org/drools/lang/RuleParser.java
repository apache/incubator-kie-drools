// $ANTLR 3.0ea8 /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g 2006-03-17 22:43:18

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "EOL", "ID", "INT", "STRING", "FLOAT", "MISC", "WS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "\'package\'", "\';\'", "\'import\'", "\'expander\'", "\'global\'", "\'query\'", "\'end\'", "\'rule\'", "\'when\'", "\':\'", "\'then\'", "\'options\'", "\'salience\'", "\'no-loop\'", "\'>\'", "\'or\'", "\'(\'", "\')\'", "\',\'", "\'==\'", "\'>=\'", "\'<\'", "\'<=\'", "\'!=\'", "\'contains\'", "\'matches\'", "\'||\'", "\'and\'", "\'&&\'", "\'exists\'", "\'not\'", "\'eval\'", "\'.\'", "\'use\'"
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
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:60:25: q= query
            	    {
            	    following.push(FOLLOW_query_in_compilation_unit82);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:67:1: prolog : opt_eol (name= package_statement )? ( import_statement )* ( expander )? ( global )* opt_eol ;
    public void prolog() throws RecognitionException {   
        String name = null;



        		String packageName = "";
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:71:17: ( opt_eol (name= package_statement )? ( import_statement )* ( expander )? ( global )* opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:71:17: opt_eol (name= package_statement )? ( import_statement )* ( expander )? ( global )* opt_eol
            {
            following.push(FOLLOW_opt_eol_in_prolog111);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:72:17: (name= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( LA3_0==14 ) {
                alt3=1;
            }
            else if ( LA3_0==-1||LA3_0==EOL||(LA3_0>=16 && LA3_0<=19)||LA3_0==21 ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("72:17: (name= package_statement )?", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:72:19: name= package_statement
                    {
                    following.push(FOLLOW_package_statement_in_prolog119);
                    name=package_statement();
                    following.pop();

                     packageName = name; 

                    }
                    break;

            }

             
            			this.packageDescr = new PackageDescr( name ); 
            		
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:76:17: ( import_statement )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);
                if ( LA4_0==16 ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:76:17: import_statement
            	    {
            	    following.push(FOLLOW_import_statement_in_prolog132);
            	    import_statement();
            	    following.pop();


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:77:17: ( expander )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( LA5_0==17 ) {
                alt5=1;
            }
            else if ( LA5_0==-1||LA5_0==EOL||(LA5_0>=18 && LA5_0<=19)||LA5_0==21 ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("77:17: ( expander )?", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:77:17: expander
                    {
                    following.push(FOLLOW_expander_in_prolog137);
                    expander();
                    following.pop();


                    }
                    break;

            }

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:78:17: ( global )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);
                if ( LA6_0==18 ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:78:17: global
            	    {
            	    following.push(FOLLOW_global_in_prolog143);
            	    global();
            	    following.pop();


            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_prolog148);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:82:1: package_statement returns [String packageName] : 'package' opt_eol name= dotted_name ( ';' )? opt_eol ;
    public String package_statement() throws RecognitionException {   
        String packageName;
        String name = null;



        		packageName = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:87:17: ( 'package' opt_eol name= dotted_name ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:87:17: 'package' opt_eol name= dotted_name ( ';' )? opt_eol
            {
            match(input,14,FOLLOW_14_in_package_statement172); 
            following.push(FOLLOW_opt_eol_in_package_statement174);
            opt_eol();
            following.pop();

            following.push(FOLLOW_dotted_name_in_package_statement178);
            name=dotted_name();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:87:52: ( ';' )?
            int alt7=2;
            int LA7_0 = input.LA(1);
            if ( LA7_0==15 ) {
                alt7=1;
            }
            else if ( LA7_0==-1||LA7_0==EOL||(LA7_0>=16 && LA7_0<=19)||LA7_0==21 ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("87:52: ( \';\' )?", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:87:52: ';'
                    {
                    match(input,15,FOLLOW_15_in_package_statement180); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_package_statement183);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:93:1: import_statement : 'import' opt_eol name= dotted_name ( ';' )? opt_eol ;
    public void import_statement() throws RecognitionException {   
        String name = null;


        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:94:17: ( 'import' opt_eol name= dotted_name ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:94:17: 'import' opt_eol name= dotted_name ( ';' )? opt_eol
            {
            match(input,16,FOLLOW_16_in_import_statement199); 
            following.push(FOLLOW_opt_eol_in_import_statement201);
            opt_eol();
            following.pop();

            following.push(FOLLOW_dotted_name_in_import_statement205);
            name=dotted_name();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:94:51: ( ';' )?
            int alt8=2;
            int LA8_0 = input.LA(1);
            if ( LA8_0==15 ) {
                alt8=1;
            }
            else if ( LA8_0==-1||LA8_0==EOL||(LA8_0>=16 && LA8_0<=19)||LA8_0==21 ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("94:51: ( \';\' )?", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:94:51: ';'
                    {
                    match(input,15,FOLLOW_15_in_import_statement207); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_import_statement210);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:100:1: expander : 'expander' (name= dotted_name )? ( ';' )? opt_eol ;
    public void expander() throws RecognitionException {   
        String name = null;



        		String config=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:104:17: ( 'expander' (name= dotted_name )? ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:104:17: 'expander' (name= dotted_name )? ( ';' )? opt_eol
            {
            match(input,17,FOLLOW_17_in_expander232); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:104:28: (name= dotted_name )?
            int alt9=2;
            int LA9_0 = input.LA(1);
            if ( LA9_0==ID ) {
                alt9=1;
            }
            else if ( LA9_0==-1||LA9_0==EOL||LA9_0==15||(LA9_0>=18 && LA9_0<=19)||LA9_0==21 ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("104:28: (name= dotted_name )?", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:104:29: name= dotted_name
                    {
                    following.push(FOLLOW_dotted_name_in_expander237);
                    name=dotted_name();
                    following.pop();


                    }
                    break;

            }

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:104:48: ( ';' )?
            int alt10=2;
            int LA10_0 = input.LA(1);
            if ( LA10_0==15 ) {
                alt10=1;
            }
            else if ( LA10_0==-1||LA10_0==EOL||(LA10_0>=18 && LA10_0<=19)||LA10_0==21 ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("104:48: ( \';\' )?", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:104:48: ';'
                    {
                    match(input,15,FOLLOW_15_in_expander241); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_expander244);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:110:1: global : 'global' type= dotted_name id= ID ( ';' )? opt_eol ;
    public void global() throws RecognitionException {   
        Token id=null;
        String type = null;



        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:114:17: ( 'global' type= dotted_name id= ID ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:114:17: 'global' type= dotted_name id= ID ( ';' )? opt_eol
            {
            match(input,18,FOLLOW_18_in_global268); 
            following.push(FOLLOW_dotted_name_in_global272);
            type=dotted_name();
            following.pop();

            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_global276); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:114:49: ( ';' )?
            int alt11=2;
            int LA11_0 = input.LA(1);
            if ( LA11_0==15 ) {
                alt11=1;
            }
            else if ( LA11_0==-1||LA11_0==EOL||(LA11_0>=18 && LA11_0<=19)||LA11_0==21 ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("114:49: ( \';\' )?", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:114:49: ';'
                    {
                    match(input,15,FOLLOW_15_in_global278); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_global281);
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


    // $ANTLR start query
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:121:1: query returns [QueryDescr query] : opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol ;
    public QueryDescr query() throws RecognitionException {   
        QueryDescr query;
        Token loc=null;
        String queryName = null;



        		query = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:126:17: ( opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:126:17: opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol
            {
            following.push(FOLLOW_opt_eol_in_query309);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,19,FOLLOW_19_in_query315); 
            following.push(FOLLOW_word_in_query319);
            queryName=word();
            following.pop();

            following.push(FOLLOW_opt_eol_in_query321);
            opt_eol();
            following.pop();

             
            			query = new QueryDescr( queryName, null ); 
            			query.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            			AndDescr lhs = new AndDescr(); query.setLhs( lhs ); 
            			lhs.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:134:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
            int alt12=2;
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
            case 46:
            case 47:
                alt12=1;
                break;
            case 30:
                int LA12_2 = input.LA(2);
                if (  expander != null  ) {
                    alt12=1;
                }
                else if ( true ) {
                    alt12=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("134:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 12, 2, input);

                    throw nvae;
                }
                break;
            case 20:
                int LA12_4 = input.LA(2);
                if (  expander != null  ) {
                    alt12=1;
                }
                else if ( true ) {
                    alt12=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("134:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 12, 4, input);

                    throw nvae;
                }
                break;
            case 43:
                int LA12_5 = input.LA(2);
                if (  expander != null  ) {
                    alt12=1;
                }
                else if ( true ) {
                    alt12=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("134:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 12, 5, input);

                    throw nvae;
                }
                break;
            case 44:
                int LA12_6 = input.LA(2);
                if (  expander != null  ) {
                    alt12=1;
                }
                else if ( true ) {
                    alt12=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("134:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 12, 6, input);

                    throw nvae;
                }
                break;
            case 45:
                int LA12_7 = input.LA(2);
                if (  expander != null  ) {
                    alt12=1;
                }
                else if ( true ) {
                    alt12=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("134:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 12, 7, input);

                    throw nvae;
                }
                break;
            case ID:
                int LA12_8 = input.LA(2);
                if (  expander != null  ) {
                    alt12=1;
                }
                else if ( true ) {
                    alt12=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("134:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 12, 8, input);

                    throw nvae;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("134:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:135:25: {...}? expander_lhs_block[lhs]
                    {
                    if ( !( expander != null ) ) {
                        throw new FailedPredicateException(input, "query", " expander != null ");
                    }
                    following.push(FOLLOW_expander_lhs_block_in_query337);
                    expander_lhs_block(lhs);
                    following.pop();


                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:136:27: normal_lhs_block[lhs]
                    {
                    following.push(FOLLOW_normal_lhs_block_in_query345);
                    normal_lhs_block(lhs);
                    following.pop();


                    }
                    break;

            }

            match(input,20,FOLLOW_20_in_query360); 
            following.push(FOLLOW_opt_eol_in_query362);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:142:1: rule returns [RuleDescr rule] : opt_eol loc= 'rule' ruleName= word opt_eol (a= rule_options )? (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* 'end' opt_eol ;
    public RuleDescr rule() throws RecognitionException {   
        RuleDescr rule;
        Token loc=null;
        Token any=null;
        String ruleName = null;

        List a = null;



        		rule = null;
        		String consequence = "";
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:148:17: ( opt_eol loc= 'rule' ruleName= word opt_eol (a= rule_options )? (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* 'end' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:148:17: opt_eol loc= 'rule' ruleName= word opt_eol (a= rule_options )? (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* 'end' opt_eol
            {
            following.push(FOLLOW_opt_eol_in_rule385);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,21,FOLLOW_21_in_rule391); 
            following.push(FOLLOW_word_in_rule395);
            ruleName=word();
            following.pop();

            following.push(FOLLOW_opt_eol_in_rule397);
            opt_eol();
            following.pop();

             
            			rule = new RuleDescr( ruleName, null ); 
            			rule.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:154:17: (a= rule_options )?
            int alt13=2;
            int LA13_0 = input.LA(1);
            if ( LA13_0==25 ) {
                alt13=1;
            }
            else if ( LA13_0==22||LA13_0==24 ) {
                alt13=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("154:17: (a= rule_options )?", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:154:25: a= rule_options
                    {
                    following.push(FOLLOW_rule_options_in_rule410);
                    a=rule_options();
                    following.pop();


                    				rule.setAttributes( a );
                    			

                    }
                    break;

            }

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:159:17: (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?
            int alt16=2;
            int LA16_0 = input.LA(1);
            if ( LA16_0==22 ) {
                alt16=1;
            }
            else if ( LA16_0==24 ) {
                alt16=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("159:17: (loc= \'when\' ( \':\' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:159:25: loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                    {
                    loc=(Token)input.LT(1);
                    match(input,22,FOLLOW_22_in_rule428); 
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:159:36: ( ':' )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);
                    if ( LA14_0==23 ) {
                        int LA14_1 = input.LA(2);
                        if ( !( expander != null ) ) {
                            alt14=1;
                        }
                        else if (  expander != null  ) {
                            alt14=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("159:36: ( \':\' )?", 14, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA14_0>=EOL && LA14_0<=22)||(LA14_0>=24 && LA14_0<=47) ) {
                        alt14=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("159:36: ( \':\' )?", 14, 0, input);

                        throw nvae;
                    }
                    switch (alt14) {
                        case 1 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:159:36: ':'
                            {
                            match(input,23,FOLLOW_23_in_rule430); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_rule433);
                    opt_eol();
                    following.pop();

                     
                    				AndDescr lhs = new AndDescr(); rule.setLhs( lhs ); 
                    				lhs.setLocation( loc.getLine(), loc.getCharPositionInLine() );
                    			
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:164:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                    int alt15=2;
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
                    case 20:
                    case 21:
                    case 22:
                    case 23:
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
                    case 46:
                    case 47:
                        alt15=1;
                        break;
                    case 30:
                        int LA15_2 = input.LA(2);
                        if (  expander != null  ) {
                            alt15=1;
                        }
                        else if ( true ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("164:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 15, 2, input);

                            throw nvae;
                        }
                        break;
                    case 24:
                        int LA15_4 = input.LA(2);
                        if (  expander != null  ) {
                            alt15=1;
                        }
                        else if ( true ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("164:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 15, 4, input);

                            throw nvae;
                        }
                        break;
                    case 43:
                        int LA15_5 = input.LA(2);
                        if (  expander != null  ) {
                            alt15=1;
                        }
                        else if ( true ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("164:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 15, 5, input);

                            throw nvae;
                        }
                        break;
                    case 44:
                        int LA15_6 = input.LA(2);
                        if (  expander != null  ) {
                            alt15=1;
                        }
                        else if ( true ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("164:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 15, 6, input);

                            throw nvae;
                        }
                        break;
                    case 45:
                        int LA15_7 = input.LA(2);
                        if (  expander != null  ) {
                            alt15=1;
                        }
                        else if ( true ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("164:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 15, 7, input);

                            throw nvae;
                        }
                        break;
                    case ID:
                        int LA15_8 = input.LA(2);
                        if (  expander != null  ) {
                            alt15=1;
                        }
                        else if ( true ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("164:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 15, 8, input);

                            throw nvae;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("164:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 15, 0, input);

                        throw nvae;
                    }

                    switch (alt15) {
                        case 1 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:165:33: {...}? expander_lhs_block[lhs]
                            {
                            if ( !( expander != null ) ) {
                                throw new FailedPredicateException(input, "rule", " expander != null ");
                            }
                            following.push(FOLLOW_expander_lhs_block_in_rule451);
                            expander_lhs_block(lhs);
                            following.pop();


                            }
                            break;
                        case 2 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:166:35: normal_lhs_block[lhs]
                            {
                            following.push(FOLLOW_normal_lhs_block_in_rule460);
                            normal_lhs_block(lhs);
                            following.pop();


                            }
                            break;

                    }


                    }
                    break;

            }

             System.err.println( "finished LHS?" ); 
            match(input,24,FOLLOW_24_in_rule485); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:171:24: ( ':' )?
            int alt17=2;
            int LA17_0 = input.LA(1);
            if ( LA17_0==23 ) {
                alt17=1;
            }
            else if ( (LA17_0>=EOL && LA17_0<=22)||(LA17_0>=24 && LA17_0<=47) ) {
                alt17=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("171:24: ( \':\' )?", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:171:24: ':'
                    {
                    match(input,23,FOLLOW_23_in_rule487); 

                    }
                    break;

            }

             System.err.println( "matched THEN" ); 
            following.push(FOLLOW_opt_eol_in_rule492);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:172:17: ( options {greedy=false; } : any= . )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);
                if ( LA18_0==20 ) {
                    alt18=2;
                }
                else if ( (LA18_0>=EOL && LA18_0<=19)||(LA18_0>=21 && LA18_0<=47) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:172:44: any= .
            	    {
            	    any=(Token)input.LT(1);
            	    matchAny(input); 

            	    				consequence = consequence + " " + any.getText();
            	    			

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

             rule.setConsequence( consequence ); 
            match(input,20,FOLLOW_20_in_rule527); 
            following.push(FOLLOW_opt_eol_in_rule529);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:182:1: rule_options returns [List options] : 'options' ( ':' )? opt_eol (a= rule_option opt_eol )* ;
    public List rule_options() throws RecognitionException {   
        List options;
        AttributeDescr a = null;



        		options = new ArrayList();
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:186:17: ( 'options' ( ':' )? opt_eol (a= rule_option opt_eol )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:186:17: 'options' ( ':' )? opt_eol (a= rule_option opt_eol )*
            {
            match(input,25,FOLLOW_25_in_rule_options552); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:186:27: ( ':' )?
            int alt19=2;
            int LA19_0 = input.LA(1);
            if ( LA19_0==23 ) {
                alt19=1;
            }
            else if ( LA19_0==EOL||LA19_0==22||LA19_0==24||(LA19_0>=26 && LA19_0<=27) ) {
                alt19=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("186:27: ( \':\' )?", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:186:27: ':'
                    {
                    match(input,23,FOLLOW_23_in_rule_options554); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_rule_options557);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:187:25: (a= rule_option opt_eol )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);
                if ( (LA20_0>=26 && LA20_0<=27) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:187:33: a= rule_option opt_eol
            	    {
            	    following.push(FOLLOW_rule_option_in_rule_options566);
            	    a=rule_option();
            	    following.pop();

            	    following.push(FOLLOW_opt_eol_in_rule_options568);
            	    opt_eol();
            	    following.pop();


            	    					options.add( a );
            	    				

            	    }
            	    break;

            	default :
            	    break loop20;
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:194:1: rule_option returns [AttributeDescr d] : (a= salience | a= no_loop );
    public AttributeDescr rule_option() throws RecognitionException {   
        AttributeDescr d;
        AttributeDescr a = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:199:25: (a= salience | a= no_loop )
            int alt21=2;
            int LA21_0 = input.LA(1);
            if ( LA21_0==26 ) {
                alt21=1;
            }
            else if ( LA21_0==27 ) {
                alt21=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("194:1: rule_option returns [AttributeDescr d] : (a= salience | a= no_loop );", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:199:25: a= salience
                    {
                    following.push(FOLLOW_salience_in_rule_option607);
                    a=salience();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:200:25: a= no_loop
                    {
                    following.push(FOLLOW_no_loop_in_rule_option617);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:204:1: salience returns [AttributeDescr d ] : loc= 'salience' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr salience() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token i=null;


        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:209:17: (loc= 'salience' opt_eol i= INT ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:209:17: loc= 'salience' opt_eol i= INT ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,26,FOLLOW_26_in_salience650); 
            following.push(FOLLOW_opt_eol_in_salience652);
            opt_eol();
            following.pop();

            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience656); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:209:46: ( ';' )?
            int alt22=2;
            int LA22_0 = input.LA(1);
            if ( LA22_0==15 ) {
                alt22=1;
            }
            else if ( LA22_0==EOL||LA22_0==22||LA22_0==24||(LA22_0>=26 && LA22_0<=27) ) {
                alt22=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("209:46: ( \';\' )?", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:209:46: ';'
                    {
                    match(input,15,FOLLOW_15_in_salience658); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_salience661);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:216:1: no_loop returns [AttributeDescr d] : loc= 'no-loop' ( ';' )? opt_eol ;
    public AttributeDescr no_loop() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;


        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:221:17: (loc= 'no-loop' ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:221:17: loc= 'no-loop' ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,27,FOLLOW_27_in_no_loop691); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:221:31: ( ';' )?
            int alt23=2;
            int LA23_0 = input.LA(1);
            if ( LA23_0==15 ) {
                alt23=1;
            }
            else if ( LA23_0==EOL||LA23_0==22||LA23_0==24||(LA23_0>=26 && LA23_0<=27) ) {
                alt23=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("221:31: ( \';\' )?", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:221:31: ';'
                    {
                    match(input,15,FOLLOW_15_in_no_loop693); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_no_loop696);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:229:1: normal_lhs_block[AndDescr descrs] : (d= lhs )* ;
    public void normal_lhs_block(AndDescr descrs) throws RecognitionException {   
        PatternDescr d = null;


        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:231:17: ( (d= lhs )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:231:17: (d= lhs )*
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:231:17: (d= lhs )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);
                if ( LA24_0==ID||LA24_0==30||(LA24_0>=43 && LA24_0<=45) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:231:25: d= lhs
            	    {
            	    following.push(FOLLOW_lhs_in_normal_lhs_block720);
            	    d=lhs();
            	    following.pop();

            	     descrs.addDescr( d ); 

            	    }
            	    break;

            	default :
            	    break loop24;
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:236:1: expander_lhs_block[AndDescr descrs] : ( options {greedy=false; } : '>' d= lhs | ( options {greedy=false; } : text= chunk EOL ) )* ;
    public void expander_lhs_block(AndDescr descrs) throws RecognitionException {   
        PatternDescr d = null;

        String text = null;


        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:239:17: ( ( options {greedy=false; } : '>' d= lhs | ( options {greedy=false; } : text= chunk EOL ) )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:239:17: ( options {greedy=false; } : '>' d= lhs | ( options {greedy=false; } : text= chunk EOL ) )*
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:239:17: ( options {greedy=false; } : '>' d= lhs | ( options {greedy=false; } : text= chunk EOL ) )*
            loop25:
            do {
                int alt25=3;
                switch ( input.LA(1) ) {
                case 20:
                    alt25=3;
                    break;
                case 24:
                    alt25=3;
                    break;
                case 28:
                    alt25=1;
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
                case 21:
                case 22:
                case 23:
                case 25:
                case 26:
                case 27:
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
                    alt25=2;
                    break;

                }

                switch (alt25) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:240:25: '>' d= lhs
            	    {
            	    match(input,28,FOLLOW_28_in_expander_lhs_block760); 
            	    following.push(FOLLOW_lhs_in_expander_lhs_block764);
            	    d=lhs();
            	    following.pop();

            	     descrs.addDescr( d ); 

            	    }
            	    break;
            	case 2 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:242:25: ( options {greedy=false; } : text= chunk EOL )
            	    {
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:242:25: ( options {greedy=false; } : text= chunk EOL )
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:243:33: text= chunk EOL
            	    {
            	    following.push(FOLLOW_chunk_in_expander_lhs_block795);
            	    text=chunk();
            	    following.pop();

            	    match(input,EOL,FOLLOW_EOL_in_expander_lhs_block797); 

            	    					d = runExpander( text );
            	    					descrs.addDescr( d );
            	    					text = null;
            	    					d = null;
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    break loop25;
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:257:1: lhs returns [PatternDescr d] : l= lhs_or ;
    public PatternDescr lhs() throws RecognitionException {   
        PatternDescr d;
        PatternDescr l = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:261:17: (l= lhs_or )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:261:17: l= lhs_or
            {
            following.push(FOLLOW_lhs_or_in_lhs843);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:265:1: lhs_column returns [PatternDescr d] : (f= fact_binding | f= fact );
    public PatternDescr lhs_column() throws RecognitionException {   
        PatternDescr d;
        PatternDescr f = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:269:17: (f= fact_binding | f= fact )
            int alt26=2;
            alt26 = dfa26.predict(input); 
            switch (alt26) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:269:17: f= fact_binding
                    {
                    following.push(FOLLOW_fact_binding_in_lhs_column870);
                    f=fact_binding();
                    following.pop();

                     d = f; 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:270:17: f= fact
                    {
                    following.push(FOLLOW_fact_in_lhs_column879);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:273:1: fact_binding returns [PatternDescr d] : id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )* ;
    public PatternDescr fact_binding() throws RecognitionException {   
        PatternDescr d;
        Token id=null;
        PatternDescr f = null;



        		d=null;
        		boolean multi=false;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:279:17: (id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:279:17: id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding911); 

             			System.err.println( "fact_binding(" + id.getText() + ")" );
             		
            following.push(FOLLOW_opt_eol_in_fact_binding926);
            opt_eol();
            following.pop();

            match(input,23,FOLLOW_23_in_fact_binding928); 
            following.push(FOLLOW_opt_eol_in_fact_binding930);
            opt_eol();
            following.pop();

            following.push(FOLLOW_fact_in_fact_binding938);
            f=fact();
            following.pop();

            following.push(FOLLOW_opt_eol_in_fact_binding940);
            opt_eol();
            following.pop();


             			((ColumnDescr)f).setIdentifier( id.getText() );
             			d = f;
             		
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:290:17: ( 'or' f= fact )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);
                if ( LA27_0==29 ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:290:25: 'or' f= fact
            	    {
            	    match(input,29,FOLLOW_29_in_fact_binding952); 
            	    	if ( ! multi ) {
            	     					PatternDescr first = d;
            	     					d = new OrDescr();
            	     					((OrDescr)d).addDescr( first );
            	     					multi=true;
            	     				}
            	     			
            	    following.push(FOLLOW_fact_in_fact_binding966);
            	    f=fact();
            	    following.pop();


            	     				((ColumnDescr)f).setIdentifier( id.getText() );
            	     				((OrDescr)d).addDescr( f );
            	     			

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
        return d;
    }
    // $ANTLR end fact_binding


    // $ANTLR start fact
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:306:1: fact returns [PatternDescr d] : id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol ;
    public PatternDescr fact() throws RecognitionException {   
        PatternDescr d;
        Token id=null;
        List c = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:310:17: (id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:310:17: id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact1006); 
             
             			d = new ColumnDescr( id.getText() ); 
             			d.setLocation( id.getLine(), id.getCharPositionInLine() );
             		
            following.push(FOLLOW_opt_eol_in_fact1014);
            opt_eol();
            following.pop();

            match(input,30,FOLLOW_30_in_fact1020); 
            following.push(FOLLOW_opt_eol_in_fact1022);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:315:29: (c= constraints )?
            int alt28=2;
            alt28 = dfa28.predict(input); 
            switch (alt28) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:315:33: c= constraints
                    {
                    following.push(FOLLOW_constraints_in_fact1028);
                    c=constraints();
                    following.pop();


                    		 			for ( Iterator cIter = c.iterator() ; cIter.hasNext() ; ) {
                     						((ColumnDescr)d).addDescr( (PatternDescr) cIter.next() );
                     					}
                     				

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_fact1047);
            opt_eol();
            following.pop();

            match(input,31,FOLLOW_31_in_fact1049); 
            following.push(FOLLOW_opt_eol_in_fact1051);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:326:1: constraints returns [List constraints] : opt_eol constraint[constraints] ( opt_eol ',' opt_eol constraint[constraints] )* opt_eol ;
    public List constraints() throws RecognitionException {   
        List constraints;

        		constraints = new ArrayList();
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:330:17: ( opt_eol constraint[constraints] ( opt_eol ',' opt_eol constraint[constraints] )* opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:330:17: opt_eol constraint[constraints] ( opt_eol ',' opt_eol constraint[constraints] )* opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraints1076);
            opt_eol();
            following.pop();

            following.push(FOLLOW_constraint_in_constraints1080);
            constraint(constraints);
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:332:17: ( opt_eol ',' opt_eol constraint[constraints] )*
            loop29:
            do {
                int alt29=2;
                alt29 = dfa29.predict(input); 
                switch (alt29) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:332:19: opt_eol ',' opt_eol constraint[constraints]
            	    {
            	    following.push(FOLLOW_opt_eol_in_constraints1087);
            	    opt_eol();
            	    following.pop();

            	    match(input,32,FOLLOW_32_in_constraints1089); 
            	    following.push(FOLLOW_opt_eol_in_constraints1091);
            	    opt_eol();
            	    following.pop();

            	    following.push(FOLLOW_constraint_in_constraints1093);
            	    constraint(constraints);
            	    following.pop();


            	    }
            	    break;

            	default :
            	    break loop29;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_constraints1100);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:336:1: constraint[List constraints] : opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= literal_constraint | rvc= retval_constraint ) opt_eol ;
    public void constraint(List constraints) throws RecognitionException {   
        Token fb=null;
        Token f=null;
        Token op=null;
        Token bvc=null;
        String lc = null;

        String rvc = null;



        		PatternDescr d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:340:17: ( opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= literal_constraint | rvc= retval_constraint ) opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:340:17: opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= literal_constraint | rvc= retval_constraint ) opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraint1119);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:341:17: (fb= ID opt_eol ':' opt_eol )?
            int alt30=2;
            alt30 = dfa30.predict(input); 
            switch (alt30) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:341:19: fb= ID opt_eol ':' opt_eol
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint1127); 
                    following.push(FOLLOW_opt_eol_in_constraint1129);
                    opt_eol();
                    following.pop();

                    match(input,23,FOLLOW_23_in_constraint1131); 
                    following.push(FOLLOW_opt_eol_in_constraint1133);
                    opt_eol();
                    following.pop();


                    }
                    break;

            }

            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint1143); 

            			if ( fb != null ) {
            				System.err.println( "fb: " + fb.getText() );
            				System.err.println( " f: " + f.getText() );
            				d = new FieldBindingDescr( f.getText(), fb.getText() );
            				System.err.println( "fbd: " + d );
            				
            				d.setLocation( f.getLine(), f.getCharPositionInLine() );
            				constraints.add( d );
            			} 
            		
            following.push(FOLLOW_opt_eol_in_constraint1153);
            opt_eol();
            following.pop();

            op=(Token)input.LT(1);
            if ( input.LA(1)==28||(input.LA(1)>=33 && input.LA(1)<=39) ) {
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint1160);    throw mse;
            }

            following.push(FOLLOW_opt_eol_in_constraint1232);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:364:41: (bvc= ID | lc= literal_constraint | rvc= retval_constraint )
            int alt31=3;
            switch ( input.LA(1) ) {
            case ID:
                alt31=1;
                break;
            case INT:
            case STRING:
            case FLOAT:
                alt31=2;
                break;
            case 30:
                alt31=3;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("364:41: (bvc= ID | lc= literal_constraint | rvc= retval_constraint )", 31, 0, input);

                throw nvae;
            }

            switch (alt31) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:364:49: bvc= ID
                    {
                    bvc=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint1250); 

                    							d = new BoundVariableDescr( f.getText(), op.getText(), bvc.getText() );
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    							constraints.add( d );
                    						

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:371:49: lc= literal_constraint
                    {
                    following.push(FOLLOW_literal_constraint_in_constraint1275);
                    lc=literal_constraint();
                    following.pop();

                     
                    							d = new LiteralDescr( f.getText(), op.getText(), lc ); 
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    							constraints.add( d );
                    						

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:377:49: rvc= retval_constraint
                    {
                    following.push(FOLLOW_retval_constraint_in_constraint1295);
                    rvc=retval_constraint();
                    following.pop();

                     
                    							d = new ReturnValueDescr( f.getText(), op.getText(), rvc ); 
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    							constraints.add( d );
                    						

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_constraint1316);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:387:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT ) ;
    public String literal_constraint() throws RecognitionException {   
        String text;
        Token t=null;


        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:391:17: ( (t= STRING | t= INT | t= FLOAT ) )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:391:17: (t= STRING | t= INT | t= FLOAT )
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:391:17: (t= STRING | t= INT | t= FLOAT )
            int alt32=3;
            switch ( input.LA(1) ) {
            case STRING:
                alt32=1;
                break;
            case INT:
                alt32=2;
                break;
            case FLOAT:
                alt32=3;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("391:17: (t= STRING | t= INT | t= FLOAT )", 32, 0, input);

                throw nvae;
            }

            switch (alt32) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:391:25: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint1343); 
                     text = t.getText(); text=text.substring( 1, text.length() - 1 ); 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:392:25: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint1353); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:393:25: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint1366); 
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:397:1: retval_constraint returns [String text] : '(' c= chunk ')' ;
    public String retval_constraint() throws RecognitionException {   
        String text;
        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:402:17: ( '(' c= chunk ')' )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:402:17: '(' c= chunk ')'
            {
            match(input,30,FOLLOW_30_in_retval_constraint1398); 
            following.push(FOLLOW_chunk_in_retval_constraint1402);
            c=chunk();
            following.pop();

            match(input,31,FOLLOW_31_in_retval_constraint1404); 
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


    // $ANTLR start chunk
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:405:1: chunk returns [String text] : ( options {greedy=false; } : '(' c= chunk ')' | any= . )* ;
    public String chunk() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:411:17: ( ( options {greedy=false; } : '(' c= chunk ')' | any= . )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:411:17: ( options {greedy=false; } : '(' c= chunk ')' | any= . )*
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:411:17: ( options {greedy=false; } : '(' c= chunk ')' | any= . )*
            loop33:
            do {
                int alt33=3;
                switch ( input.LA(1) ) {
                case EOL:
                    alt33=3;
                    break;
                case 31:
                    alt33=3;
                    break;
                case 30:
                    alt33=1;
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
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
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
                    alt33=2;
                    break;

                }

                switch (alt33) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:412:25: '(' c= chunk ')'
            	    {
            	    match(input,30,FOLLOW_30_in_chunk1447); 
            	    following.push(FOLLOW_chunk_in_chunk1451);
            	    c=chunk();
            	    following.pop();

            	    match(input,31,FOLLOW_31_in_chunk1453); 

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
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:424:19: any= .
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
        return text;
    }
    // $ANTLR end chunk


    // $ANTLR start lhs_or
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:436:1: lhs_or returns [PatternDescr d] : left= lhs_and ( ('or'|'||')right= lhs_and )* ;
    public PatternDescr lhs_or() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:441:17: (left= lhs_and ( ('or'|'||')right= lhs_and )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:441:17: left= lhs_and ( ('or'|'||')right= lhs_and )*
            {
             OrDescr or = null; 
            following.push(FOLLOW_lhs_and_in_lhs_or1510);
            left=lhs_and();
            following.pop();

            d = left; 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:443:17: ( ('or'|'||')right= lhs_and )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);
                if ( LA34_0==29||LA34_0==40 ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:443:25: ('or'|'||')right= lhs_and
            	    {
            	    if ( input.LA(1)==29||input.LA(1)==40 ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or1520);    throw mse;
            	    }

            	    following.push(FOLLOW_lhs_and_in_lhs_or1531);
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
        return d;
    }
    // $ANTLR end lhs_or


    // $ANTLR start lhs_and
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:457:1: lhs_and returns [PatternDescr d] : left= lhs_unary ( ('and'|'&&')right= lhs_unary )* ;
    public PatternDescr lhs_and() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:462:17: (left= lhs_unary ( ('and'|'&&')right= lhs_unary )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:462:17: left= lhs_unary ( ('and'|'&&')right= lhs_unary )*
            {
             AndDescr and = null; 
            following.push(FOLLOW_lhs_unary_in_lhs_and1571);
            left=lhs_unary();
            following.pop();

             d = left; 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:464:17: ( ('and'|'&&')right= lhs_unary )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);
                if ( (LA35_0>=41 && LA35_0<=42) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:464:25: ('and'|'&&')right= lhs_unary
            	    {
            	    if ( (input.LA(1)>=41 && input.LA(1)<=42) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and1580);    throw mse;
            	    }

            	    following.push(FOLLOW_lhs_unary_in_lhs_and1591);
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
        return d;
    }
    // $ANTLR end lhs_and


    // $ANTLR start lhs_unary
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:478:1: lhs_unary returns [PatternDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) ;
    public PatternDescr lhs_unary() throws RecognitionException {   
        PatternDescr d;
        PatternDescr u = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:482:17: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:482:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:482:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            int alt36=5;
            switch ( input.LA(1) ) {
            case 43:
                alt36=1;
                break;
            case 44:
                alt36=2;
                break;
            case 45:
                alt36=3;
                break;
            case ID:
                alt36=4;
                break;
            case 30:
                alt36=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("482:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | \'(\' u= lhs \')\' )", 36, 0, input);

                throw nvae;
            }

            switch (alt36) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:482:25: u= lhs_exist
                    {
                    following.push(FOLLOW_lhs_exist_in_lhs_unary1629);
                    u=lhs_exist();
                    following.pop();


                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:483:25: u= lhs_not
                    {
                    following.push(FOLLOW_lhs_not_in_lhs_unary1637);
                    u=lhs_not();
                    following.pop();


                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:484:25: u= lhs_eval
                    {
                    following.push(FOLLOW_lhs_eval_in_lhs_unary1645);
                    u=lhs_eval();
                    following.pop();


                    }
                    break;
                case 4 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:485:25: u= lhs_column
                    {
                    following.push(FOLLOW_lhs_column_in_lhs_unary1653);
                    u=lhs_column();
                    following.pop();


                    }
                    break;
                case 5 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:486:25: '(' u= lhs ')'
                    {
                    match(input,30,FOLLOW_30_in_lhs_unary1659); 
                    following.push(FOLLOW_lhs_in_lhs_unary1663);
                    u=lhs();
                    following.pop();

                    match(input,31,FOLLOW_31_in_lhs_unary1665); 

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:490:1: lhs_exist returns [PatternDescr d] : loc= 'exists' column= lhs_column ;
    public PatternDescr lhs_exist() throws RecognitionException {   
        PatternDescr d;
        Token loc=null;
        PatternDescr column = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:494:17: (loc= 'exists' column= lhs_column )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:494:17: loc= 'exists' column= lhs_column
            {
            loc=(Token)input.LT(1);
            match(input,43,FOLLOW_43_in_lhs_exist1695); 
            following.push(FOLLOW_lhs_column_in_lhs_exist1699);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:501:1: lhs_not returns [NotDescr d] : loc= 'not' column= lhs_column ;
    public NotDescr lhs_not() throws RecognitionException {   
        NotDescr d;
        Token loc=null;
        PatternDescr column = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:505:17: (loc= 'not' column= lhs_column )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:505:17: loc= 'not' column= lhs_column
            {
            loc=(Token)input.LT(1);
            match(input,44,FOLLOW_44_in_lhs_not1729); 
            following.push(FOLLOW_lhs_column_in_lhs_not1733);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:512:1: lhs_eval returns [PatternDescr d] : 'eval' '(' c= chunk ')' ;
    public PatternDescr lhs_eval() throws RecognitionException {   
        PatternDescr d;
        String c = null;



        		d = null;
        		String text = "";
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:517:17: ( 'eval' '(' c= chunk ')' )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:517:17: 'eval' '(' c= chunk ')'
            {
            match(input,45,FOLLOW_45_in_lhs_eval1759); 
            match(input,30,FOLLOW_30_in_lhs_eval1761); 
            following.push(FOLLOW_chunk_in_lhs_eval1765);
            c=chunk();
            following.pop();

            match(input,31,FOLLOW_31_in_lhs_eval1767); 
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:521:1: dotted_name returns [String name] : id= ID ( '.' id= ID )* ;
    public String dotted_name() throws RecognitionException {   
        String name;
        Token id=null;


        		name = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:526:17: (id= ID ( '.' id= ID )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:526:17: id= ID ( '.' id= ID )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name1799); 
             name=id.getText(); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:526:46: ( '.' id= ID )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);
                if ( LA37_0==46 ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:526:48: '.' id= ID
            	    {
            	    match(input,46,FOLLOW_46_in_dotted_name1805); 
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_dotted_name1809); 
            	     name = name + "." + id.getText(); 

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
        return name;
    }
    // $ANTLR end dotted_name


    // $ANTLR start word
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:530:1: word returns [String word] : (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING );
    public String word() throws RecognitionException {   
        String word;
        Token id=null;
        Token str=null;


        		word = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:534:17: (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING )
            int alt38=11;
            switch ( input.LA(1) ) {
            case ID:
                alt38=1;
                break;
            case 16:
                alt38=2;
                break;
            case 47:
                alt38=3;
                break;
            case 21:
                alt38=4;
                break;
            case 19:
                alt38=5;
                break;
            case 26:
                alt38=6;
                break;
            case 27:
                alt38=7;
                break;
            case 22:
                alt38=8;
                break;
            case 24:
                alt38=9;
                break;
            case 20:
                alt38=10;
                break;
            case STRING:
                alt38=11;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("530:1: word returns [String word] : (id= ID | \'import\' | \'use\' | \'rule\' | \'query\' | \'salience\' | \'no-loop\' | \'when\' | \'then\' | \'end\' | str= STRING );", 38, 0, input);

                throw nvae;
            }

            switch (alt38) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:534:17: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_word1839); 
                     word=id.getText(); 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:535:17: 'import'
                    {
                    match(input,16,FOLLOW_16_in_word1851); 
                     word="import"; 

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:536:17: 'use'
                    {
                    match(input,47,FOLLOW_47_in_word1860); 
                     word="use"; 

                    }
                    break;
                case 4 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:537:17: 'rule'
                    {
                    match(input,21,FOLLOW_21_in_word1872); 
                     word="rule"; 

                    }
                    break;
                case 5 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:538:17: 'query'
                    {
                    match(input,19,FOLLOW_19_in_word1883); 
                     word="query"; 

                    }
                    break;
                case 6 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:539:17: 'salience'
                    {
                    match(input,26,FOLLOW_26_in_word1893); 
                     word="salience"; 

                    }
                    break;
                case 7 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:540:17: 'no-loop'
                    {
                    match(input,27,FOLLOW_27_in_word1901); 
                     word="no-loop"; 

                    }
                    break;
                case 8 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:541:17: 'when'
                    {
                    match(input,22,FOLLOW_22_in_word1909); 
                     word="when"; 

                    }
                    break;
                case 9 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:542:17: 'then'
                    {
                    match(input,24,FOLLOW_24_in_word1920); 
                     word="then"; 

                    }
                    break;
                case 10 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:543:17: 'end'
                    {
                    match(input,20,FOLLOW_20_in_word1931); 
                     word="end"; 

                    }
                    break;
                case 11 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:544:17: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_word1945); 
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


    protected DFA2 dfa2 = new DFA2();protected DFA26 dfa26 = new DFA26();protected DFA28 dfa28 = new DFA28();protected DFA29 dfa29 = new DFA29();protected DFA30 dfa30 = new DFA30();
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
                case 21:
                    return s3;

                case EOL:
                    return s2;

                case 19:
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

                case 21:
                    return s3;

                case 19:
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 2, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA26 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s4 = new DFA.State() {{alt=2;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 23:
                    return s3;

                case EOL:
                    return s2;

                case 30:
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 2, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s2;

                case 23:
                    return s3;

                case 30:
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA26_0 = input.LA(1);
                if ( LA26_0==ID ) {return s1;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 26, 0, input);

                throw nvae;
            }
        };

    }class DFA28 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s3 = new DFA.State() {{alt=2;}};
        DFA.State s2 = new DFA.State() {{alt=1;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 31:
                    return s3;

                case EOL:
                    return s1;

                case ID:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 28, 1, input);

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

                case 31:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 28, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA29 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 31:
                    return s2;

                case EOL:
                    return s1;

                case 32:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 29, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s1;

                case 31:
                    return s2;

                case 32:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 29, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA30 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s3 = new DFA.State() {{alt=2;}};
        DFA.State s4 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 28:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                    return s3;

                case EOL:
                    return s2;

                case 23:
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 2, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s2;

                case 28:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                    return s3;

                case 23:
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA30_0 = input.LA(1);
                if ( LA30_0==ID ) {return s1;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 30, 0, input);

                throw nvae;
            }
        };

    }


    public static final BitSet FOLLOW_EOL_in_opt_eol40 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_compilation_unit53 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit57 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_rule_in_compilation_unit66 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_query_in_compilation_unit82 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_prolog111 = new BitSet(new long[]{0x0000000000074012L});
    public static final BitSet FOLLOW_package_statement_in_prolog119 = new BitSet(new long[]{0x0000000000070012L});
    public static final BitSet FOLLOW_import_statement_in_prolog132 = new BitSet(new long[]{0x0000000000070012L});
    public static final BitSet FOLLOW_expander_in_prolog137 = new BitSet(new long[]{0x0000000000040012L});
    public static final BitSet FOLLOW_global_in_prolog143 = new BitSet(new long[]{0x0000000000040012L});
    public static final BitSet FOLLOW_opt_eol_in_prolog148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_package_statement172 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_package_statement174 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement178 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_package_statement180 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_package_statement183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_import_statement199 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_import_statement201 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_dotted_name_in_import_statement205 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_import_statement207 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_import_statement210 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_expander232 = new BitSet(new long[]{0x0000000000008032L});
    public static final BitSet FOLLOW_dotted_name_in_expander237 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_expander241 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_expander244 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_global268 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_dotted_name_in_global272 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_global276 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_global278 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_global281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_query309 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_query315 = new BitSet(new long[]{0x000080000D7900A0L});
    public static final BitSet FOLLOW_word_in_query319 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_query321 = new BitSet(new long[]{0x0000FFFFFFFFFFF2L});
    public static final BitSet FOLLOW_expander_lhs_block_in_query337 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query345 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_query360 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_query362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_rule385 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_rule391 = new BitSet(new long[]{0x000080000D7900A0L});
    public static final BitSet FOLLOW_word_in_rule395 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule397 = new BitSet(new long[]{0x0000000003400000L});
    public static final BitSet FOLLOW_rule_options_in_rule410 = new BitSet(new long[]{0x0000000001400000L});
    public static final BitSet FOLLOW_22_in_rule428 = new BitSet(new long[]{0x0000000000800012L});
    public static final BitSet FOLLOW_23_in_rule430 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule433 = new BitSet(new long[]{0x0000FFFFFFFFFFF2L});
    public static final BitSet FOLLOW_expander_lhs_block_in_rule451 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule460 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_rule485 = new BitSet(new long[]{0x0000000000800012L});
    public static final BitSet FOLLOW_23_in_rule487 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule492 = new BitSet(new long[]{0x0000FFFFFFFFFFF0L});
    public static final BitSet FOLLOW_20_in_rule527 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_rule_options552 = new BitSet(new long[]{0x0000000000800012L});
    public static final BitSet FOLLOW_23_in_rule_options554 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule_options557 = new BitSet(new long[]{0x000000000C000002L});
    public static final BitSet FOLLOW_rule_option_in_rule_options566 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule_options568 = new BitSet(new long[]{0x000000000C000002L});
    public static final BitSet FOLLOW_salience_in_rule_option607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_option617 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_salience650 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_salience652 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_salience656 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_salience658 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_salience661 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_no_loop691 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_no_loop693 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block720 = new BitSet(new long[]{0x0000380040000022L});
    public static final BitSet FOLLOW_28_in_expander_lhs_block760 = new BitSet(new long[]{0x0000380040000020L});
    public static final BitSet FOLLOW_lhs_in_expander_lhs_block764 = new BitSet(new long[]{0x0000FFFFFFFFFFF2L});
    public static final BitSet FOLLOW_chunk_in_expander_lhs_block795 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_EOL_in_expander_lhs_block797 = new BitSet(new long[]{0x0000FFFFFFFFFFF2L});
    public static final BitSet FOLLOW_lhs_or_in_lhs843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column870 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_column879 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding911 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding926 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_fact_binding928 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding930 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_fact_in_fact_binding938 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding940 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_29_in_fact_binding952 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_fact_in_fact_binding966 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_ID_in_fact1006 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1014 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_fact1020 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1022 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_constraints_in_fact1028 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1047 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_fact1049 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1076 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_constraint_in_constraints1080 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1087 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_constraints1089 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1091 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_constraint_in_constraints1093 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1119 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint1127 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1129 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_constraint1131 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1133 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint1143 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1153 = new BitSet(new long[]{0x000000FE10000000L});
    public static final BitSet FOLLOW_set_in_constraint1160 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1232 = new BitSet(new long[]{0x00000000400001E0L});
    public static final BitSet FOLLOW_ID_in_constraint1250 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint1275 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint1295 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint1343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint1353 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint1366 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_retval_constraint1398 = new BitSet(new long[]{0x0000FFFFFFFFFFF2L});
    public static final BitSet FOLLOW_chunk_in_retval_constraint1402 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_retval_constraint1404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_chunk1447 = new BitSet(new long[]{0x0000FFFFFFFFFFF2L});
    public static final BitSet FOLLOW_chunk_in_chunk1451 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_chunk1453 = new BitSet(new long[]{0x0000FFFFFFFFFFF2L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1510 = new BitSet(new long[]{0x0000010020000002L});
    public static final BitSet FOLLOW_set_in_lhs_or1520 = new BitSet(new long[]{0x0000380040000020L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1531 = new BitSet(new long[]{0x0000010020000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1571 = new BitSet(new long[]{0x0000060000000002L});
    public static final BitSet FOLLOW_set_in_lhs_and1580 = new BitSet(new long[]{0x0000380040000020L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1591 = new BitSet(new long[]{0x0000060000000002L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary1629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary1637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary1645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary1653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_lhs_unary1659 = new BitSet(new long[]{0x0000380040000020L});
    public static final BitSet FOLLOW_lhs_in_lhs_unary1663 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_lhs_unary1665 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_lhs_exist1695 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist1699 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_lhs_not1729 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not1733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_lhs_eval1759 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_lhs_eval1761 = new BitSet(new long[]{0x0000FFFFFFFFFFF2L});
    public static final BitSet FOLLOW_chunk_in_lhs_eval1765 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_lhs_eval1767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name1799 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_46_in_dotted_name1805 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_dotted_name1809 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_ID_in_word1839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_word1851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_word1860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_word1872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_word1883 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_word1893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_word1901 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_word1909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_word1920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_word1931 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_word1945 = new BitSet(new long[]{0x0000000000000002L});

}