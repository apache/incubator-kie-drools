// $ANTLR 3.0ea8 C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g 2006-03-17 12:41:33

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "EOL", "ID", "INT", "STRING", "FLOAT", "MISC", "WS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "\'package\'", "\'.\'", "\';\'", "\'import\'", "\'expander\'", "\'query\'", "\'end\'", "\'rule\'", "\'when\'", "\':\'", "\'then\'", "\'options\'", "\'salience\'", "\'no-loop\'", "\'>\'", "\'or\'", "\'(\'", "\')\'", "\',\'", "\'==\'", "\'>=\'", "\'<\'", "\'<=\'", "\'!=\'", "\'contains\'", "\'matches\'", "\'||\'", "\'and\'", "\'&&\'", "\'exists\'", "\'not\'", "\'eval\'", "\'use\'"
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:49:1: opt_eol : ( EOL )* ;
    public void opt_eol() throws RecognitionException {   
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:50:17: ( ( EOL )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:50:17: ( EOL )*
            {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:50:17: ( EOL )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);
                if ( LA1_0==EOL ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:50:17: EOL
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:53:1: compilation_unit : prolog (r= rule | q= query )* ;
    public void compilation_unit() throws RecognitionException {   
        RuleDescr r = null;

        QueryDescr q = null;


        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:54:17: ( prolog (r= rule | q= query )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:54:17: prolog (r= rule | q= query )*
            {
            following.push(FOLLOW_prolog_in_compilation_unit53);
            prolog();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:55:17: (r= rule | q= query )*
            loop2:
            do {
                int alt2=3;
                alt2 = dfa2.predict(input); 
                switch (alt2) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:55:18: r= rule
            	    {
            	    following.push(FOLLOW_rule_in_compilation_unit61);
            	    r=rule();
            	    following.pop();

            	    this.packageDescr.addRule( r ); 

            	    }
            	    break;
            	case 2 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:55:62: q= query
            	    {
            	    following.push(FOLLOW_query_in_compilation_unit69);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:58:1: prolog : opt_eol (name= package_statement )? opt_eol (name= import_statement )* opt_eol ( use_expander )? opt_eol ;
    public void prolog() throws RecognitionException {   
        String name = null;


        
        		String packageName = "";
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:62:17: ( opt_eol (name= package_statement )? opt_eol (name= import_statement )* opt_eol ( use_expander )? opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:62:17: opt_eol (name= package_statement )? opt_eol (name= import_statement )* opt_eol ( use_expander )? opt_eol
            {
            following.push(FOLLOW_opt_eol_in_prolog91);
            opt_eol();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:63:17: (name= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( LA3_0==14 ) {
                alt3=1;
            }
            else if ( LA3_0==-1||LA3_0==EOL||(LA3_0>=17 && LA3_0<=19)||LA3_0==21 ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("63:17: (name= package_statement )?", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:63:19: name= package_statement
                    {
                    following.push(FOLLOW_package_statement_in_prolog99);
                    name=package_statement();
                    following.pop();

                     packageName = name; 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_prolog108);
            opt_eol();
            following.pop();

             
            			this.packageDescr = new PackageDescr( name ); 
            		
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:68:17: (name= import_statement )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);
                if ( LA4_0==17 ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:68:19: name= import_statement
            	    {
            	    following.push(FOLLOW_import_statement_in_prolog120);
            	    name=import_statement();
            	    following.pop();

            	     this.packageDescr.addImport( name ); 

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_prolog129);
            opt_eol();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:70:17: ( use_expander )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( LA5_0==18 ) {
                alt5=1;
            }
            else if ( LA5_0==-1||LA5_0==EOL||LA5_0==19||LA5_0==21 ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("70:17: ( use_expander )?", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:70:17: use_expander
                    {
                    following.push(FOLLOW_use_expander_in_prolog133);
                    use_expander();
                    following.pop();


                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_prolog138);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:74:1: package_statement returns [String packageName] : 'package' opt_eol id= ID ( '.' id= ID )* ( ';' )? opt_eol ;
    public String package_statement() throws RecognitionException {   
        String packageName;
        Token id=null;

        
        		packageName = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:78:17: ( 'package' opt_eol id= ID ( '.' id= ID )* ( ';' )? opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:78:17: 'package' opt_eol id= ID ( '.' id= ID )* ( ';' )? opt_eol
            {
            match(input,14,FOLLOW_14_in_package_statement159); 
            following.push(FOLLOW_opt_eol_in_package_statement161);
            opt_eol();
            following.pop();

            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_package_statement165); 
             packageName = id.getText(); 
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:78:73: ( '.' id= ID )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);
                if ( LA6_0==15 ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:78:75: '.' id= ID
            	    {
            	    match(input,15,FOLLOW_15_in_package_statement171); 
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_package_statement175); 
            	     packageName += "." + id.getText(); 

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:78:127: ( ';' )?
            int alt7=2;
            int LA7_0 = input.LA(1);
            if ( LA7_0==16 ) {
                alt7=1;
            }
            else if ( LA7_0==-1||LA7_0==EOL||(LA7_0>=17 && LA7_0<=19)||LA7_0==21 ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("78:127: ( \';\' )?", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:78:127: ';'
                    {
                    match(input,16,FOLLOW_16_in_package_statement182); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_package_statement185);
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
        return packageName;
    }
    // $ANTLR end package_statement


    // $ANTLR start import_statement
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:81:1: import_statement returns [String importStatement] : 'import' opt_eol name= dotted_name ( ';' )? opt_eol ;
    public String import_statement() throws RecognitionException {   
        String importStatement;
        String name = null;


        
        		importStatement = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:85:17: ( 'import' opt_eol name= dotted_name ( ';' )? opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:85:17: 'import' opt_eol name= dotted_name ( ';' )? opt_eol
            {
            match(input,17,FOLLOW_17_in_import_statement208); 
            following.push(FOLLOW_opt_eol_in_import_statement210);
            opt_eol();
            following.pop();

            following.push(FOLLOW_dotted_name_in_import_statement214);
            name=dotted_name();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:85:51: ( ';' )?
            int alt8=2;
            int LA8_0 = input.LA(1);
            if ( LA8_0==16 ) {
                alt8=1;
            }
            else if ( LA8_0==-1||LA8_0==EOL||(LA8_0>=17 && LA8_0<=19)||LA8_0==21 ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("85:51: ( \';\' )?", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:85:51: ';'
                    {
                    match(input,16,FOLLOW_16_in_import_statement216); 

                    }
                    break;

            }

             importStatement = name; 
            following.push(FOLLOW_opt_eol_in_import_statement221);
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
        return importStatement;
    }
    // $ANTLR end import_statement


    // $ANTLR start use_expander
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:88:1: use_expander : 'expander' (name= dotted_name )? ( ';' )? opt_eol ;
    public void use_expander() throws RecognitionException {   
        String name = null;


        
        		String config=null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:92:17: ( 'expander' (name= dotted_name )? ( ';' )? opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:92:17: 'expander' (name= dotted_name )? ( ';' )? opt_eol
            {
            match(input,18,FOLLOW_18_in_use_expander239); 
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:92:28: (name= dotted_name )?
            int alt9=2;
            int LA9_0 = input.LA(1);
            if ( LA9_0==ID ) {
                alt9=1;
            }
            else if ( LA9_0==-1||LA9_0==EOL||LA9_0==16||LA9_0==19||LA9_0==21 ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("92:28: (name= dotted_name )?", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:92:29: name= dotted_name
                    {
                    following.push(FOLLOW_dotted_name_in_use_expander244);
                    name=dotted_name();
                    following.pop();


                    }
                    break;

            }

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:92:48: ( ';' )?
            int alt10=2;
            int LA10_0 = input.LA(1);
            if ( LA10_0==16 ) {
                alt10=1;
            }
            else if ( LA10_0==-1||LA10_0==EOL||LA10_0==19||LA10_0==21 ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("92:48: ( \';\' )?", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:92:48: ';'
                    {
                    match(input,16,FOLLOW_16_in_use_expander248); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_use_expander251);
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
    // $ANTLR end use_expander


    // $ANTLR start query
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:99:1: query returns [QueryDescr query] : opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol ;
    public QueryDescr query() throws RecognitionException {   
        QueryDescr query;
        Token loc=null;
        String queryName = null;


        
        		query = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:104:17: ( opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:104:17: opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol
            {
            following.push(FOLLOW_opt_eol_in_query279);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,19,FOLLOW_19_in_query285); 
            following.push(FOLLOW_word_in_query289);
            queryName=word();
            following.pop();

            following.push(FOLLOW_opt_eol_in_query291);
            opt_eol();
            following.pop();

             
            			query = new QueryDescr( queryName, null ); 
            			query.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            			AndDescr lhs = new AndDescr(); query.setLhs( lhs ); 
            			lhs.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:112:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
            int alt11=2;
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
                alt11=1;
                break;
            case 20:
                int LA11_2 = input.LA(2);
                if (  expander != null  ) {
                    alt11=1;
                }
                else if ( true ) {
                    alt11=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("112:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 11, 2, input);

                    throw nvae;
                }
                break;
            case 43:
                int LA11_3 = input.LA(2);
                if (  expander != null  ) {
                    alt11=1;
                }
                else if ( true ) {
                    alt11=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("112:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 11, 3, input);

                    throw nvae;
                }
                break;
            case 44:
                int LA11_4 = input.LA(2);
                if (  expander != null  ) {
                    alt11=1;
                }
                else if ( true ) {
                    alt11=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("112:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 11, 4, input);

                    throw nvae;
                }
                break;
            case 45:
                int LA11_5 = input.LA(2);
                if (  expander != null  ) {
                    alt11=1;
                }
                else if ( true ) {
                    alt11=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("112:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 11, 5, input);

                    throw nvae;
                }
                break;
            case ID:
                int LA11_6 = input.LA(2);
                if (  expander != null  ) {
                    alt11=1;
                }
                else if ( true ) {
                    alt11=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("112:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 11, 6, input);

                    throw nvae;
                }
                break;
            case 30:
                int LA11_7 = input.LA(2);
                if (  expander != null  ) {
                    alt11=1;
                }
                else if ( true ) {
                    alt11=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("112:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 11, 7, input);

                    throw nvae;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("112:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:113:25: {...}? expander_lhs_block[lhs]
                    {
                    if ( !( expander != null ) ) {
                        throw new FailedPredicateException(input, "query", " expander != null ");
                    }
                    following.push(FOLLOW_expander_lhs_block_in_query307);
                    expander_lhs_block(lhs);
                    following.pop();


                    }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:114:27: normal_lhs_block[lhs]
                    {
                    following.push(FOLLOW_normal_lhs_block_in_query315);
                    normal_lhs_block(lhs);
                    following.pop();


                    }
                    break;

            }

            match(input,20,FOLLOW_20_in_query330); 
            following.push(FOLLOW_opt_eol_in_query332);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:120:1: rule returns [RuleDescr rule] : opt_eol loc= 'rule' ruleName= word opt_eol (a= rule_options )? (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* 'end' opt_eol ;
    public RuleDescr rule() throws RecognitionException {   
        RuleDescr rule;
        Token loc=null;
        Token any=null;
        String ruleName = null;

        List a = null;


        
        		rule = null;
        		String consequence = "";
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:126:17: ( opt_eol loc= 'rule' ruleName= word opt_eol (a= rule_options )? (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* 'end' opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:126:17: opt_eol loc= 'rule' ruleName= word opt_eol (a= rule_options )? (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* 'end' opt_eol
            {
            following.push(FOLLOW_opt_eol_in_rule355);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,21,FOLLOW_21_in_rule361); 
            following.push(FOLLOW_word_in_rule365);
            ruleName=word();
            following.pop();

            following.push(FOLLOW_opt_eol_in_rule367);
            opt_eol();
            following.pop();

             
            			rule = new RuleDescr( ruleName, null ); 
            			rule.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:132:17: (a= rule_options )?
            int alt12=2;
            int LA12_0 = input.LA(1);
            if ( LA12_0==25 ) {
                alt12=1;
            }
            else if ( LA12_0==22||LA12_0==24 ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("132:17: (a= rule_options )?", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:132:25: a= rule_options
                    {
                    following.push(FOLLOW_rule_options_in_rule380);
                    a=rule_options();
                    following.pop();

                    
                    				rule.setAttributes( a );
                    			

                    }
                    break;

            }

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:137:17: (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?
            int alt15=2;
            int LA15_0 = input.LA(1);
            if ( LA15_0==22 ) {
                alt15=1;
            }
            else if ( LA15_0==24 ) {
                alt15=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("137:17: (loc= \'when\' ( \':\' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:137:25: loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                    {
                    loc=(Token)input.LT(1);
                    match(input,22,FOLLOW_22_in_rule398); 
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:137:36: ( ':' )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);
                    if ( LA13_0==23 ) {
                        int LA13_1 = input.LA(2);
                        if ( LA13_1==EOL ) {
                            int LA13_11 = input.LA(3);
                            if ( !( expander != null ) ) {
                                alt13=1;
                            }
                            else if (  expander != null  ) {
                                alt13=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("137:36: ( \':\' )?", 13, 11, input);

                                throw nvae;
                            }
                        }
                        else if ( (LA13_1>=ID && LA13_1<=46) ) {
                            alt13=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("137:36: ( \':\' )?", 13, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA13_0>=EOL && LA13_0<=22)||(LA13_0>=24 && LA13_0<=46) ) {
                        alt13=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("137:36: ( \':\' )?", 13, 0, input);

                        throw nvae;
                    }
                    switch (alt13) {
                        case 1 :
                            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:137:36: ':'
                            {
                            match(input,23,FOLLOW_23_in_rule400); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_rule403);
                    opt_eol();
                    following.pop();

                     
                    				AndDescr lhs = new AndDescr(); rule.setLhs( lhs ); 
                    				lhs.setLocation( loc.getLine(), loc.getCharPositionInLine() );
                    			
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:142:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                    int alt14=2;
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
                        alt14=1;
                        break;
                    case 24:
                        int LA14_2 = input.LA(2);
                        if (  expander != null  ) {
                            alt14=1;
                        }
                        else if ( true ) {
                            alt14=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("142:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 14, 2, input);

                            throw nvae;
                        }
                        break;
                    case 43:
                        int LA14_3 = input.LA(2);
                        if (  expander != null  ) {
                            alt14=1;
                        }
                        else if ( true ) {
                            alt14=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("142:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 14, 3, input);

                            throw nvae;
                        }
                        break;
                    case 44:
                        int LA14_4 = input.LA(2);
                        if (  expander != null  ) {
                            alt14=1;
                        }
                        else if ( true ) {
                            alt14=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("142:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 14, 4, input);

                            throw nvae;
                        }
                        break;
                    case 45:
                        int LA14_5 = input.LA(2);
                        if (  expander != null  ) {
                            alt14=1;
                        }
                        else if ( true ) {
                            alt14=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("142:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 14, 5, input);

                            throw nvae;
                        }
                        break;
                    case ID:
                        int LA14_6 = input.LA(2);
                        if (  expander != null  ) {
                            alt14=1;
                        }
                        else if ( true ) {
                            alt14=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("142:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 14, 6, input);

                            throw nvae;
                        }
                        break;
                    case 30:
                        int LA14_7 = input.LA(2);
                        if (  expander != null  ) {
                            alt14=1;
                        }
                        else if ( true ) {
                            alt14=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("142:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 14, 7, input);

                            throw nvae;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("142:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 14, 0, input);

                        throw nvae;
                    }

                    switch (alt14) {
                        case 1 :
                            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:143:33: {...}? expander_lhs_block[lhs]
                            {
                            if ( !( expander != null ) ) {
                                throw new FailedPredicateException(input, "rule", " expander != null ");
                            }
                            following.push(FOLLOW_expander_lhs_block_in_rule421);
                            expander_lhs_block(lhs);
                            following.pop();


                            }
                            break;
                        case 2 :
                            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:144:35: normal_lhs_block[lhs]
                            {
                            following.push(FOLLOW_normal_lhs_block_in_rule430);
                            normal_lhs_block(lhs);
                            following.pop();


                            }
                            break;

                    }


                    }
                    break;

            }

             System.err.println( "finished LHS?" ); 
            match(input,24,FOLLOW_24_in_rule455); 
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:149:24: ( ':' )?
            int alt16=2;
            int LA16_0 = input.LA(1);
            if ( LA16_0==23 ) {
                alt16=1;
            }
            else if ( (LA16_0>=EOL && LA16_0<=22)||(LA16_0>=24 && LA16_0<=46) ) {
                alt16=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("149:24: ( \':\' )?", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:149:24: ':'
                    {
                    match(input,23,FOLLOW_23_in_rule457); 

                    }
                    break;

            }

             System.err.println( "matched THEN" ); 
            following.push(FOLLOW_opt_eol_in_rule462);
            opt_eol();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:150:17: ( options {greedy=false; } : any= . )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);
                if ( LA17_0==20 ) {
                    alt17=2;
                }
                else if ( (LA17_0>=EOL && LA17_0<=19)||(LA17_0>=21 && LA17_0<=46) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:150:44: any= .
            	    {
            	    any=(Token)input.LT(1);
            	    matchAny(input); 
            	    
            	    				consequence = consequence + " " + any.getText();
            	    			

            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);

             rule.setConsequence( consequence ); 
            match(input,20,FOLLOW_20_in_rule497); 
            following.push(FOLLOW_opt_eol_in_rule499);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:160:1: rule_options returns [List options] : 'options' ( ':' )? opt_eol (a= rule_option opt_eol )* ;
    public List rule_options() throws RecognitionException {   
        List options;
        AttributeDescr a = null;


        
        		options = new ArrayList();
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:164:17: ( 'options' ( ':' )? opt_eol (a= rule_option opt_eol )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:164:17: 'options' ( ':' )? opt_eol (a= rule_option opt_eol )*
            {
            match(input,25,FOLLOW_25_in_rule_options522); 
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:164:27: ( ':' )?
            int alt18=2;
            int LA18_0 = input.LA(1);
            if ( LA18_0==23 ) {
                alt18=1;
            }
            else if ( LA18_0==EOL||LA18_0==22||LA18_0==24||(LA18_0>=26 && LA18_0<=27) ) {
                alt18=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("164:27: ( \':\' )?", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:164:27: ':'
                    {
                    match(input,23,FOLLOW_23_in_rule_options524); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_rule_options527);
            opt_eol();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:165:25: (a= rule_option opt_eol )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);
                if ( (LA19_0>=26 && LA19_0<=27) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:165:33: a= rule_option opt_eol
            	    {
            	    following.push(FOLLOW_rule_option_in_rule_options536);
            	    a=rule_option();
            	    following.pop();

            	    following.push(FOLLOW_opt_eol_in_rule_options538);
            	    opt_eol();
            	    following.pop();

            	    
            	    					options.add( a );
            	    				

            	    }
            	    break;

            	default :
            	    break loop19;
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:172:1: rule_option returns [AttributeDescr d] : (a= salience | a= no_loop );
    public AttributeDescr rule_option() throws RecognitionException {   
        AttributeDescr d;
        AttributeDescr a = null;


        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:177:25: (a= salience | a= no_loop )
            int alt20=2;
            int LA20_0 = input.LA(1);
            if ( LA20_0==26 ) {
                alt20=1;
            }
            else if ( LA20_0==27 ) {
                alt20=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("172:1: rule_option returns [AttributeDescr d] : (a= salience | a= no_loop );", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:177:25: a= salience
                    {
                    following.push(FOLLOW_salience_in_rule_option577);
                    a=salience();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:178:25: a= no_loop
                    {
                    following.push(FOLLOW_no_loop_in_rule_option587);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:182:1: salience returns [AttributeDescr d ] : loc= 'salience' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr salience() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:187:17: (loc= 'salience' opt_eol i= INT ( ';' )? opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:187:17: loc= 'salience' opt_eol i= INT ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,26,FOLLOW_26_in_salience620); 
            following.push(FOLLOW_opt_eol_in_salience622);
            opt_eol();
            following.pop();

            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience626); 
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:187:46: ( ';' )?
            int alt21=2;
            int LA21_0 = input.LA(1);
            if ( LA21_0==16 ) {
                alt21=1;
            }
            else if ( LA21_0==EOL||LA21_0==22||LA21_0==24||(LA21_0>=26 && LA21_0<=27) ) {
                alt21=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("187:46: ( \';\' )?", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:187:46: ';'
                    {
                    match(input,16,FOLLOW_16_in_salience628); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_salience631);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:194:1: no_loop returns [AttributeDescr d] : loc= 'no-loop' ( ';' )? opt_eol ;
    public AttributeDescr no_loop() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;

        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:199:17: (loc= 'no-loop' ( ';' )? opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:199:17: loc= 'no-loop' ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,27,FOLLOW_27_in_no_loop661); 
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:199:31: ( ';' )?
            int alt22=2;
            int LA22_0 = input.LA(1);
            if ( LA22_0==16 ) {
                alt22=1;
            }
            else if ( LA22_0==EOL||LA22_0==22||LA22_0==24||(LA22_0>=26 && LA22_0<=27) ) {
                alt22=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("199:31: ( \';\' )?", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:199:31: ';'
                    {
                    match(input,16,FOLLOW_16_in_no_loop663); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_no_loop666);
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


    // $ANTLR start expander_lhs_block
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:206:1: expander_lhs_block[AndDescr descrs] : ( ( options {greedy=false; } : a= . EOL ) | '>' d= lhs )* ;
    public void expander_lhs_block(AndDescr descrs) throws RecognitionException {   
        Token a=null;
        PatternDescr d = null;


        
        		String text = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:211:17: ( ( ( options {greedy=false; } : a= . EOL ) | '>' d= lhs )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:211:17: ( ( options {greedy=false; } : a= . EOL ) | '>' d= lhs )*
            {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:211:17: ( ( options {greedy=false; } : a= . EOL ) | '>' d= lhs )*
            loop23:
            do {
                int alt23=3;
                switch ( input.LA(1) ) {
                case 20:
                    alt23=1;
                    break;
                case 24:
                    alt23=1;
                    break;
                case 28:
                    alt23=1;
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
                    alt23=1;
                    break;

                }

                switch (alt23) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:212:25: ( options {greedy=false; } : a= . EOL )
            	    {
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:212:25: ( options {greedy=false; } : a= . EOL )
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:212:58: a= . EOL
            	    {
            	    a=(Token)input.LT(1);
            	    matchAny(input); 
            	    
            	    					if ( text == null ) {
            	    						text = a.getText();
            	    					} else {
            	    						text = text + " " + a.getText();
            	    					}
            	    				
            	    match(input,EOL,FOLLOW_EOL_in_expander_lhs_block722); 
            	    
            	    					d = runExpander( text );
            	    					descrs.addDescr( d );
            	    					text = null;
            	    					d = null;
            	    				

            	    }


            	    }
            	    break;
            	case 2 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:228:27: '>' d= lhs
            	    {
            	    match(input,28,FOLLOW_28_in_expander_lhs_block741); 
            	    following.push(FOLLOW_lhs_in_expander_lhs_block745);
            	    d=lhs();
            	    following.pop();

            	     descrs.addDescr( d ); 

            	    }
            	    break;

            	default :
            	    break loop23;
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


    // $ANTLR start normal_lhs_block
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:233:1: normal_lhs_block[AndDescr descrs] : (d= lhs )* ;
    public void normal_lhs_block(AndDescr descrs) throws RecognitionException {   
        PatternDescr d = null;


        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:235:17: ( (d= lhs )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:235:17: (d= lhs )*
            {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:235:17: (d= lhs )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);
                if ( LA24_0==ID||LA24_0==30||(LA24_0>=43 && LA24_0<=45) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:235:25: d= lhs
            	    {
            	    following.push(FOLLOW_lhs_in_normal_lhs_block772);
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


    // $ANTLR start lhs
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:241:1: lhs returns [PatternDescr d] : l= lhs_or ;
    public PatternDescr lhs() throws RecognitionException {   
        PatternDescr d;
        PatternDescr l = null;


        
        		d=null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:245:17: (l= lhs_or )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:245:17: l= lhs_or
            {
            following.push(FOLLOW_lhs_or_in_lhs808);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:249:1: lhs_column returns [PatternDescr d] : (f= fact_binding | f= fact );
    public PatternDescr lhs_column() throws RecognitionException {   
        PatternDescr d;
        PatternDescr f = null;


        
        		d=null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:253:17: (f= fact_binding | f= fact )
            int alt25=2;
            alt25 = dfa25.predict(input); 
            switch (alt25) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:253:17: f= fact_binding
                    {
                    following.push(FOLLOW_fact_binding_in_lhs_column835);
                    f=fact_binding();
                    following.pop();

                     d = f; 

                    }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:254:17: f= fact
                    {
                    following.push(FOLLOW_fact_in_lhs_column844);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:257:1: fact_binding returns [PatternDescr d] : id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )* ;
    public PatternDescr fact_binding() throws RecognitionException {   
        PatternDescr d;
        Token id=null;
        PatternDescr f = null;


        
        		d=null;
        		boolean multi=false;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:263:17: (id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:263:17: id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding876); 
            
             			System.err.println( "fact_binding(" + id.getText() + ")" );
             		
            following.push(FOLLOW_opt_eol_in_fact_binding891);
            opt_eol();
            following.pop();

            match(input,23,FOLLOW_23_in_fact_binding893); 
            following.push(FOLLOW_opt_eol_in_fact_binding895);
            opt_eol();
            following.pop();

            following.push(FOLLOW_fact_in_fact_binding903);
            f=fact();
            following.pop();

            following.push(FOLLOW_opt_eol_in_fact_binding905);
            opt_eol();
            following.pop();

            
             			((ColumnDescr)f).setIdentifier( id.getText() );
             			d = f;
             		
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:274:17: ( 'or' f= fact )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);
                if ( LA26_0==29 ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:274:25: 'or' f= fact
            	    {
            	    match(input,29,FOLLOW_29_in_fact_binding917); 
            	    	if ( ! multi ) {
            	     					PatternDescr first = d;
            	     					d = new OrDescr();
            	     					((OrDescr)d).addDescr( first );
            	     					multi=true;
            	     				}
            	     			
            	    following.push(FOLLOW_fact_in_fact_binding931);
            	    f=fact();
            	    following.pop();

            	    
            	     				((ColumnDescr)f).setIdentifier( id.getText() );
            	     				((OrDescr)d).addDescr( f );
            	     			

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
        return d;
    }
    // $ANTLR end fact_binding


    // $ANTLR start fact
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:290:1: fact returns [PatternDescr d] : id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol ;
    public PatternDescr fact() throws RecognitionException {   
        PatternDescr d;
        Token id=null;
        List c = null;


        
        		d=null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:294:17: (id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:294:17: id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact971); 
             
             			d = new ColumnDescr( id.getText() ); 
             			d.setLocation( id.getLine(), id.getCharPositionInLine() );
             		
            following.push(FOLLOW_opt_eol_in_fact979);
            opt_eol();
            following.pop();

            match(input,30,FOLLOW_30_in_fact985); 
            following.push(FOLLOW_opt_eol_in_fact987);
            opt_eol();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:299:29: (c= constraints )?
            int alt27=2;
            alt27 = dfa27.predict(input); 
            switch (alt27) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:299:33: c= constraints
                    {
                    following.push(FOLLOW_constraints_in_fact993);
                    c=constraints();
                    following.pop();

                    
                    		 			for ( Iterator cIter = c.iterator() ; cIter.hasNext() ; ) {
                     						((ColumnDescr)d).addDescr( (PatternDescr) cIter.next() );
                     					}
                     				

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_fact1012);
            opt_eol();
            following.pop();

            match(input,31,FOLLOW_31_in_fact1014); 
            following.push(FOLLOW_opt_eol_in_fact1016);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:310:1: constraints returns [List constraints] : opt_eol constraint[constraints] ( opt_eol ',' opt_eol constraint[constraints] )* opt_eol ;
    public List constraints() throws RecognitionException {   
        List constraints;
        
        		constraints = new ArrayList();
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:314:17: ( opt_eol constraint[constraints] ( opt_eol ',' opt_eol constraint[constraints] )* opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:314:17: opt_eol constraint[constraints] ( opt_eol ',' opt_eol constraint[constraints] )* opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraints1041);
            opt_eol();
            following.pop();

            following.push(FOLLOW_constraint_in_constraints1045);
            constraint(constraints);
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:316:17: ( opt_eol ',' opt_eol constraint[constraints] )*
            loop28:
            do {
                int alt28=2;
                alt28 = dfa28.predict(input); 
                switch (alt28) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:316:19: opt_eol ',' opt_eol constraint[constraints]
            	    {
            	    following.push(FOLLOW_opt_eol_in_constraints1052);
            	    opt_eol();
            	    following.pop();

            	    match(input,32,FOLLOW_32_in_constraints1054); 
            	    following.push(FOLLOW_opt_eol_in_constraints1056);
            	    opt_eol();
            	    following.pop();

            	    following.push(FOLLOW_constraint_in_constraints1058);
            	    constraint(constraints);
            	    following.pop();


            	    }
            	    break;

            	default :
            	    break loop28;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_constraints1065);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:320:1: constraint[List constraints] : opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= literal_constraint | rvc= retval_constraint ) opt_eol ;
    public void constraint(List constraints) throws RecognitionException {   
        Token fb=null;
        Token f=null;
        Token op=null;
        Token bvc=null;
        String lc = null;

        String rvc = null;


        
        		PatternDescr d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:324:17: ( opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= literal_constraint | rvc= retval_constraint ) opt_eol )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:324:17: opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= literal_constraint | rvc= retval_constraint ) opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraint1084);
            opt_eol();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:325:17: (fb= ID opt_eol ':' opt_eol )?
            int alt29=2;
            alt29 = dfa29.predict(input); 
            switch (alt29) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:325:19: fb= ID opt_eol ':' opt_eol
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint1092); 
                    following.push(FOLLOW_opt_eol_in_constraint1094);
                    opt_eol();
                    following.pop();

                    match(input,23,FOLLOW_23_in_constraint1096); 
                    following.push(FOLLOW_opt_eol_in_constraint1098);
                    opt_eol();
                    following.pop();


                    }
                    break;

            }

            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint1108); 
            
            			if ( fb != null ) {
            				System.err.println( "fb: " + fb.getText() );
            				System.err.println( " f: " + f.getText() );
            				d = new FieldBindingDescr( f.getText(), fb.getText() );
            				System.err.println( "fbd: " + d );
            				
            				d.setLocation( f.getLine(), f.getCharPositionInLine() );
            				constraints.add( d );
            			} 
            		
            following.push(FOLLOW_opt_eol_in_constraint1118);
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
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint1125);    throw mse;
            }

            following.push(FOLLOW_opt_eol_in_constraint1197);
            opt_eol();
            following.pop();

            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:348:41: (bvc= ID | lc= literal_constraint | rvc= retval_constraint )
            int alt30=3;
            switch ( input.LA(1) ) {
            case ID:
                alt30=1;
                break;
            case INT:
            case STRING:
            case FLOAT:
                alt30=2;
                break;
            case 30:
                alt30=3;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("348:41: (bvc= ID | lc= literal_constraint | rvc= retval_constraint )", 30, 0, input);

                throw nvae;
            }

            switch (alt30) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:348:49: bvc= ID
                    {
                    bvc=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint1215); 
                    
                    							d = new BoundVariableDescr( f.getText(), op.getText(), bvc.getText() );
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    							constraints.add( d );
                    						

                    }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:355:49: lc= literal_constraint
                    {
                    following.push(FOLLOW_literal_constraint_in_constraint1240);
                    lc=literal_constraint();
                    following.pop();

                     
                    							d = new LiteralDescr( f.getText(), op.getText(), lc ); 
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    							constraints.add( d );
                    						

                    }
                    break;
                case 3 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:361:49: rvc= retval_constraint
                    {
                    following.push(FOLLOW_retval_constraint_in_constraint1260);
                    rvc=retval_constraint();
                    following.pop();

                     
                    							d = new ReturnValueDescr( f.getText(), op.getText(), rvc ); 
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    							constraints.add( d );
                    						

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_constraint1281);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:371:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT ) ;
    public String literal_constraint() throws RecognitionException {   
        String text;
        Token t=null;

        
        		text = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:375:17: ( (t= STRING | t= INT | t= FLOAT ) )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:375:17: (t= STRING | t= INT | t= FLOAT )
            {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:375:17: (t= STRING | t= INT | t= FLOAT )
            int alt31=3;
            switch ( input.LA(1) ) {
            case STRING:
                alt31=1;
                break;
            case INT:
                alt31=2;
                break;
            case FLOAT:
                alt31=3;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("375:17: (t= STRING | t= INT | t= FLOAT )", 31, 0, input);

                throw nvae;
            }

            switch (alt31) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:375:25: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint1308); 
                     text = t.getText(); text=text.substring( 1, text.length() - 1 ); 

                    }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:376:25: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint1318); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:377:25: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint1331); 
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:381:1: retval_constraint returns [String text] : '(' c= chunk ')' ;
    public String retval_constraint() throws RecognitionException {   
        String text;
        String c = null;


        
        		text = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:386:17: ( '(' c= chunk ')' )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:386:17: '(' c= chunk ')'
            {
            match(input,30,FOLLOW_30_in_retval_constraint1363); 
            following.push(FOLLOW_chunk_in_retval_constraint1367);
            c=chunk();
            following.pop();

            match(input,31,FOLLOW_31_in_retval_constraint1369); 
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:389:1: chunk returns [String text] : ( options {greedy=false; } : '(' c= chunk ')' | any= . )* ;
    public String chunk() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;


        
        		text = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:395:17: ( ( options {greedy=false; } : '(' c= chunk ')' | any= . )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:395:17: ( options {greedy=false; } : '(' c= chunk ')' | any= . )*
            {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:395:17: ( options {greedy=false; } : '(' c= chunk ')' | any= . )*
            loop32:
            do {
                int alt32=3;
                switch ( input.LA(1) ) {
                case 31:
                    alt32=3;
                    break;
                case 30:
                    alt32=1;
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
                    alt32=2;
                    break;

                }

                switch (alt32) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:396:25: '(' c= chunk ')'
            	    {
            	    match(input,30,FOLLOW_30_in_chunk1412); 
            	    following.push(FOLLOW_chunk_in_chunk1416);
            	    c=chunk();
            	    following.pop();

            	    match(input,31,FOLLOW_31_in_chunk1418); 
            	    
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
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:408:19: any= .
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
            	    break loop32;
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:420:1: lhs_or returns [PatternDescr d] : left= lhs_and ( ('or'|'||')right= lhs_and )* ;
    public PatternDescr lhs_or() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;


        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:425:17: (left= lhs_and ( ('or'|'||')right= lhs_and )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:425:17: left= lhs_and ( ('or'|'||')right= lhs_and )*
            {
             OrDescr or = null; 
            following.push(FOLLOW_lhs_and_in_lhs_or1475);
            left=lhs_and();
            following.pop();

            d = left; 
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:427:17: ( ('or'|'||')right= lhs_and )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);
                if ( LA33_0==29||LA33_0==40 ) {
                    int LA33_10 = input.LA(2);
                    if ( LA33_10==ID||LA33_10==30||(LA33_10>=43 && LA33_10<=45) ) {
                        alt33=1;
                    }


                }


                switch (alt33) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:427:25: ('or'|'||')right= lhs_and
            	    {
            	    if ( input.LA(1)==29||input.LA(1)==40 ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or1485);    throw mse;
            	    }

            	    following.push(FOLLOW_lhs_and_in_lhs_or1496);
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
    // $ANTLR end lhs_or


    // $ANTLR start lhs_and
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:441:1: lhs_and returns [PatternDescr d] : left= lhs_unary ( ('and'|'&&')right= lhs_unary )* ;
    public PatternDescr lhs_and() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;


        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:446:17: (left= lhs_unary ( ('and'|'&&')right= lhs_unary )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:446:17: left= lhs_unary ( ('and'|'&&')right= lhs_unary )*
            {
             AndDescr and = null; 
            following.push(FOLLOW_lhs_unary_in_lhs_and1536);
            left=lhs_unary();
            following.pop();

             d = left; 
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:448:17: ( ('and'|'&&')right= lhs_unary )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);
                if ( (LA34_0>=41 && LA34_0<=42) ) {
                    int LA34_11 = input.LA(2);
                    if ( LA34_11==ID||LA34_11==30||(LA34_11>=43 && LA34_11<=45) ) {
                        alt34=1;
                    }


                }


                switch (alt34) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:448:25: ('and'|'&&')right= lhs_unary
            	    {
            	    if ( (input.LA(1)>=41 && input.LA(1)<=42) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and1545);    throw mse;
            	    }

            	    following.push(FOLLOW_lhs_unary_in_lhs_and1556);
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
    // $ANTLR end lhs_and


    // $ANTLR start lhs_unary
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:462:1: lhs_unary returns [PatternDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) ;
    public PatternDescr lhs_unary() throws RecognitionException {   
        PatternDescr d;
        PatternDescr u = null;


        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:466:17: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:466:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:466:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            int alt35=5;
            switch ( input.LA(1) ) {
            case 43:
                alt35=1;
                break;
            case 44:
                alt35=2;
                break;
            case 45:
                alt35=3;
                break;
            case ID:
                alt35=4;
                break;
            case 30:
                alt35=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("466:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | \'(\' u= lhs \')\' )", 35, 0, input);

                throw nvae;
            }

            switch (alt35) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:466:25: u= lhs_exist
                    {
                    following.push(FOLLOW_lhs_exist_in_lhs_unary1594);
                    u=lhs_exist();
                    following.pop();


                    }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:467:25: u= lhs_not
                    {
                    following.push(FOLLOW_lhs_not_in_lhs_unary1602);
                    u=lhs_not();
                    following.pop();


                    }
                    break;
                case 3 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:468:25: u= lhs_eval
                    {
                    following.push(FOLLOW_lhs_eval_in_lhs_unary1610);
                    u=lhs_eval();
                    following.pop();


                    }
                    break;
                case 4 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:469:25: u= lhs_column
                    {
                    following.push(FOLLOW_lhs_column_in_lhs_unary1618);
                    u=lhs_column();
                    following.pop();


                    }
                    break;
                case 5 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:470:25: '(' u= lhs ')'
                    {
                    match(input,30,FOLLOW_30_in_lhs_unary1624); 
                    following.push(FOLLOW_lhs_in_lhs_unary1628);
                    u=lhs();
                    following.pop();

                    match(input,31,FOLLOW_31_in_lhs_unary1630); 

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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:474:1: lhs_exist returns [PatternDescr d] : loc= 'exists' column= lhs_column ;
    public PatternDescr lhs_exist() throws RecognitionException {   
        PatternDescr d;
        Token loc=null;
        PatternDescr column = null;


        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:478:17: (loc= 'exists' column= lhs_column )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:478:17: loc= 'exists' column= lhs_column
            {
            loc=(Token)input.LT(1);
            match(input,43,FOLLOW_43_in_lhs_exist1660); 
            following.push(FOLLOW_lhs_column_in_lhs_exist1664);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:485:1: lhs_not returns [NotDescr d] : loc= 'not' column= lhs_column ;
    public NotDescr lhs_not() throws RecognitionException {   
        NotDescr d;
        Token loc=null;
        PatternDescr column = null;


        
        		d = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:489:17: (loc= 'not' column= lhs_column )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:489:17: loc= 'not' column= lhs_column
            {
            loc=(Token)input.LT(1);
            match(input,44,FOLLOW_44_in_lhs_not1694); 
            following.push(FOLLOW_lhs_column_in_lhs_not1698);
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:496:1: lhs_eval returns [PatternDescr d] : 'eval' '(' c= chunk ')' ;
    public PatternDescr lhs_eval() throws RecognitionException {   
        PatternDescr d;
        String c = null;


        
        		d = null;
        		String text = "";
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:501:17: ( 'eval' '(' c= chunk ')' )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:501:17: 'eval' '(' c= chunk ')'
            {
            match(input,45,FOLLOW_45_in_lhs_eval1724); 
            match(input,30,FOLLOW_30_in_lhs_eval1726); 
            following.push(FOLLOW_chunk_in_lhs_eval1730);
            c=chunk();
            following.pop();

            match(input,31,FOLLOW_31_in_lhs_eval1732); 
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:505:1: dotted_name returns [String name] : id= ID ( '.' id= ID )* ;
    public String dotted_name() throws RecognitionException {   
        String name;
        Token id=null;

        
        		name = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:510:17: (id= ID ( '.' id= ID )* )
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:510:17: id= ID ( '.' id= ID )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name1764); 
             name=id.getText(); 
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:510:46: ( '.' id= ID )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);
                if ( LA36_0==15 ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:510:48: '.' id= ID
            	    {
            	    match(input,15,FOLLOW_15_in_dotted_name1770); 
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_dotted_name1774); 
            	     name = name + "." + id.getText(); 

            	    }
            	    break;

            	default :
            	    break loop36;
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
    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:514:1: word returns [String word] : (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING );
    public String word() throws RecognitionException {   
        String word;
        Token id=null;
        Token str=null;

        
        		word = null;
        	
        try {
            // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:518:17: (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING )
            int alt37=11;
            switch ( input.LA(1) ) {
            case ID:
                alt37=1;
                break;
            case 17:
                alt37=2;
                break;
            case 46:
                alt37=3;
                break;
            case 21:
                alt37=4;
                break;
            case 19:
                alt37=5;
                break;
            case 26:
                alt37=6;
                break;
            case 27:
                alt37=7;
                break;
            case 22:
                alt37=8;
                break;
            case 24:
                alt37=9;
                break;
            case 20:
                alt37=10;
                break;
            case STRING:
                alt37=11;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("514:1: word returns [String word] : (id= ID | \'import\' | \'use\' | \'rule\' | \'query\' | \'salience\' | \'no-loop\' | \'when\' | \'then\' | \'end\' | str= STRING );", 37, 0, input);

                throw nvae;
            }

            switch (alt37) {
                case 1 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:518:17: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_word1804); 
                     word=id.getText(); 

                    }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:519:17: 'import'
                    {
                    match(input,17,FOLLOW_17_in_word1816); 
                     word="import"; 

                    }
                    break;
                case 3 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:520:17: 'use'
                    {
                    match(input,46,FOLLOW_46_in_word1825); 
                     word="use"; 

                    }
                    break;
                case 4 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:521:17: 'rule'
                    {
                    match(input,21,FOLLOW_21_in_word1837); 
                     word="rule"; 

                    }
                    break;
                case 5 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:522:17: 'query'
                    {
                    match(input,19,FOLLOW_19_in_word1848); 
                     word="query"; 

                    }
                    break;
                case 6 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:523:17: 'salience'
                    {
                    match(input,26,FOLLOW_26_in_word1858); 
                     word="salience"; 

                    }
                    break;
                case 7 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:524:17: 'no-loop'
                    {
                    match(input,27,FOLLOW_27_in_word1866); 
                     word="no-loop"; 

                    }
                    break;
                case 8 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:525:17: 'when'
                    {
                    match(input,22,FOLLOW_22_in_word1874); 
                     word="when"; 

                    }
                    break;
                case 9 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:526:17: 'then'
                    {
                    match(input,24,FOLLOW_24_in_word1885); 
                     word="then"; 

                    }
                    break;
                case 10 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:527:17: 'end'
                    {
                    match(input,20,FOLLOW_20_in_word1896); 
                     word="end"; 

                    }
                    break;
                case 11 :
                    // C:\Projects\jboss-rules\drools-compiler\src\main\resources\org\drools\lang\drl.g:528:17: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_word1910); 
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


    protected DFA2 dfa2 = new DFA2();protected DFA25 dfa25 = new DFA25();protected DFA27 dfa27 = new DFA27();protected DFA28 dfa28 = new DFA28();protected DFA29 dfa29 = new DFA29();
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
                case 19:
                    return s4;

                case EOL:
                    return s2;

                case 21:
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

    }class DFA25 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s4 = new DFA.State() {{alt=1;}};
        DFA.State s3 = new DFA.State() {{alt=2;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 23:
                    return s4;

                case EOL:
                    return s2;

                case 30:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 25, 2, input);

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

                case 23:
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 25, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA25_0 = input.LA(1);
                if ( LA25_0==ID ) {return s1;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }
        };

    }class DFA27 extends DFA {
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

                case 31:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 27, 1, input);

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
                        new NoViableAltException("", 27, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA28 extends DFA {
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
                        new NoViableAltException("", 28, 1, input);

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
                        new NoViableAltException("", 28, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA29 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s4 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {{alt=1;}};
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
                    return s4;

                case EOL:
                    return s2;

                case 23:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 29, 2, input);

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

                case 28:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 29, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA29_0 = input.LA(1);
                if ( LA29_0==ID ) {return s1;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }
        };

    }


    public static final BitSet FOLLOW_EOL_in_opt_eol40 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit53 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_rule_in_compilation_unit61 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_query_in_compilation_unit69 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_prolog91 = new BitSet(new long[]{0x0000000000004012L});
    public static final BitSet FOLLOW_package_statement_in_prolog99 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_prolog108 = new BitSet(new long[]{0x0000000000020012L});
    public static final BitSet FOLLOW_import_statement_in_prolog120 = new BitSet(new long[]{0x0000000000020012L});
    public static final BitSet FOLLOW_opt_eol_in_prolog129 = new BitSet(new long[]{0x0000000000040012L});
    public static final BitSet FOLLOW_use_expander_in_prolog133 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_prolog138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_package_statement159 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_package_statement161 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_package_statement165 = new BitSet(new long[]{0x0000000000018012L});
    public static final BitSet FOLLOW_15_in_package_statement171 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_package_statement175 = new BitSet(new long[]{0x0000000000018012L});
    public static final BitSet FOLLOW_16_in_package_statement182 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_package_statement185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_import_statement208 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_import_statement210 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_dotted_name_in_import_statement214 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_import_statement216 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_import_statement221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_use_expander239 = new BitSet(new long[]{0x0000000000010032L});
    public static final BitSet FOLLOW_dotted_name_in_use_expander244 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_use_expander248 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_use_expander251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_query279 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_query285 = new BitSet(new long[]{0x000040000D7A00A0L});
    public static final BitSet FOLLOW_word_in_query289 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_query291 = new BitSet(new long[]{0x00007FFFFFFFFFF2L});
    public static final BitSet FOLLOW_expander_lhs_block_in_query307 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query315 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_query330 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_query332 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_rule355 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_rule361 = new BitSet(new long[]{0x000040000D7A00A0L});
    public static final BitSet FOLLOW_word_in_rule365 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule367 = new BitSet(new long[]{0x0000000003400000L});
    public static final BitSet FOLLOW_rule_options_in_rule380 = new BitSet(new long[]{0x0000000001400000L});
    public static final BitSet FOLLOW_22_in_rule398 = new BitSet(new long[]{0x0000000000800012L});
    public static final BitSet FOLLOW_23_in_rule400 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule403 = new BitSet(new long[]{0x00007FFFFFFFFFF2L});
    public static final BitSet FOLLOW_expander_lhs_block_in_rule421 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule430 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_rule455 = new BitSet(new long[]{0x0000000000800012L});
    public static final BitSet FOLLOW_23_in_rule457 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule462 = new BitSet(new long[]{0x00007FFFFFFFFFF0L});
    public static final BitSet FOLLOW_20_in_rule497 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_rule_options522 = new BitSet(new long[]{0x0000000000800012L});
    public static final BitSet FOLLOW_23_in_rule_options524 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule_options527 = new BitSet(new long[]{0x000000000C000002L});
    public static final BitSet FOLLOW_rule_option_in_rule_options536 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule_options538 = new BitSet(new long[]{0x000000000C000002L});
    public static final BitSet FOLLOW_salience_in_rule_option577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_option587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_salience620 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_salience622 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_salience626 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_salience628 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_salience631 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_no_loop661 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_no_loop663 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EOL_in_expander_lhs_block722 = new BitSet(new long[]{0x00007FFFFFFFFFF2L});
    public static final BitSet FOLLOW_28_in_expander_lhs_block741 = new BitSet(new long[]{0x0000380040000020L});
    public static final BitSet FOLLOW_lhs_in_expander_lhs_block745 = new BitSet(new long[]{0x00007FFFFFFFFFF2L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block772 = new BitSet(new long[]{0x0000380040000022L});
    public static final BitSet FOLLOW_lhs_or_in_lhs808 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_column844 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding876 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding891 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_fact_binding893 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding895 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_fact_in_fact_binding903 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding905 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_29_in_fact_binding917 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_fact_in_fact_binding931 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_ID_in_fact971 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact979 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_fact985 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact987 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_constraints_in_fact993 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1012 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_fact1014 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1016 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1041 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_constraint_in_constraints1045 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1052 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_constraints1054 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1056 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_constraint_in_constraints1058 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1065 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1084 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint1092 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1094 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_constraint1096 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1098 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint1108 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1118 = new BitSet(new long[]{0x000000FE10000000L});
    public static final BitSet FOLLOW_set_in_constraint1125 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1197 = new BitSet(new long[]{0x00000000400001E0L});
    public static final BitSet FOLLOW_ID_in_constraint1215 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint1240 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint1260 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint1308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint1318 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint1331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_retval_constraint1363 = new BitSet(new long[]{0x00007FFFFFFFFFF2L});
    public static final BitSet FOLLOW_chunk_in_retval_constraint1367 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_retval_constraint1369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_chunk1412 = new BitSet(new long[]{0x00007FFFFFFFFFF2L});
    public static final BitSet FOLLOW_chunk_in_chunk1416 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_chunk1418 = new BitSet(new long[]{0x00007FFFFFFFFFF2L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1475 = new BitSet(new long[]{0x0000010020000002L});
    public static final BitSet FOLLOW_set_in_lhs_or1485 = new BitSet(new long[]{0x0000380040000020L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1496 = new BitSet(new long[]{0x0000010020000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1536 = new BitSet(new long[]{0x0000060000000002L});
    public static final BitSet FOLLOW_set_in_lhs_and1545 = new BitSet(new long[]{0x0000380040000020L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1556 = new BitSet(new long[]{0x0000060000000002L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary1594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary1602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary1610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary1618 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_lhs_unary1624 = new BitSet(new long[]{0x0000380040000020L});
    public static final BitSet FOLLOW_lhs_in_lhs_unary1628 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_lhs_unary1630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_lhs_exist1660 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist1664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_lhs_not1694 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not1698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_lhs_eval1724 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_lhs_eval1726 = new BitSet(new long[]{0x00007FFFFFFFFFF2L});
    public static final BitSet FOLLOW_chunk_in_lhs_eval1730 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_lhs_eval1732 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name1764 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_15_in_dotted_name1770 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_dotted_name1774 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ID_in_word1804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_word1816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_word1825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_word1837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_word1848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_word1858 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_word1866 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_word1874 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_word1885 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_word1896 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_word1910 = new BitSet(new long[]{0x0000000000000002L});

}