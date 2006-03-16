// $ANTLR 3.0ea8 /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g 2006-03-16 00:09:50

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "EOL", "ID", "INT", "STRING", "FLOAT", "MISC", "WS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "\'package\'", "\'.\'", "\';\'", "\'import\'", "\'expander\'", "\'rule\'", "\'when\'", "\':\'", "\'then\'", "\'end\'", "\'options\'", "\'salience\'", "\'no-loop\'", "\'>\'", "\'(\'", "\')\'", "\',\'", "\'==\'", "\'>=\'", "\'<\'", "\'<=\'", "\'!=\'", "\'contains\'", "\'or\'", "\'||\'", "\'and\'", "\'&&\'", "\'exists\'", "\'not\'", "\'eval\'", "\'use\'"
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:53:1: compilation_unit : prolog (r= rule )* ;
    public void compilation_unit() throws RecognitionException {   
        RuleDescr r = null;


        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:54:17: ( prolog (r= rule )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:54:17: prolog (r= rule )*
            {
            following.push(FOLLOW_prolog_in_compilation_unit53);
            prolog();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:55:17: (r= rule )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);
                if ( LA2_0==EOL||LA2_0==19 ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:55:18: r= rule
            	    {
            	    following.push(FOLLOW_rule_in_compilation_unit61);
            	    r=rule();
            	    following.pop();

            	    this.packageDescr.addRule( r ); 

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:58:1: prolog : opt_eol (name= package_statement )? opt_eol (name= import_statement )* opt_eol ( use_expander )? opt_eol ;
    public void prolog() throws RecognitionException {   
        String name = null;



        		String packageName = "";
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:62:17: ( opt_eol (name= package_statement )? opt_eol (name= import_statement )* opt_eol ( use_expander )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:62:17: opt_eol (name= package_statement )? opt_eol (name= import_statement )* opt_eol ( use_expander )? opt_eol
            {
            following.push(FOLLOW_opt_eol_in_prolog83);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:63:17: (name= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( LA3_0==14 ) {
                alt3=1;
            }
            else if ( LA3_0==-1||LA3_0==EOL||(LA3_0>=17 && LA3_0<=19) ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("63:17: (name= package_statement )?", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:63:19: name= package_statement
                    {
                    following.push(FOLLOW_package_statement_in_prolog91);
                    name=package_statement();
                    following.pop();

                     packageName = name; 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_prolog100);
            opt_eol();
            following.pop();

             
            			this.packageDescr = new PackageDescr( name ); 
            		
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:68:17: (name= import_statement )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);
                if ( LA4_0==17 ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:68:19: name= import_statement
            	    {
            	    following.push(FOLLOW_import_statement_in_prolog112);
            	    name=import_statement();
            	    following.pop();

            	     this.packageDescr.addImport( name ); 

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_prolog121);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:70:17: ( use_expander )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( LA5_0==18 ) {
                alt5=1;
            }
            else if ( LA5_0==-1||LA5_0==EOL||LA5_0==19 ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("70:17: ( use_expander )?", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:70:17: use_expander
                    {
                    following.push(FOLLOW_use_expander_in_prolog125);
                    use_expander();
                    following.pop();


                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_prolog130);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:74:1: package_statement returns [String packageName] : 'package' opt_eol id= ID ( '.' id= ID )* ( ';' )? opt_eol ;
    public String package_statement() throws RecognitionException {   
        String packageName;
        Token id=null;


        		packageName = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:78:17: ( 'package' opt_eol id= ID ( '.' id= ID )* ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:78:17: 'package' opt_eol id= ID ( '.' id= ID )* ( ';' )? opt_eol
            {
            match(input,14,FOLLOW_14_in_package_statement151); 
            following.push(FOLLOW_opt_eol_in_package_statement153);
            opt_eol();
            following.pop();

            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_package_statement157); 
             packageName = id.getText(); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:78:73: ( '.' id= ID )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);
                if ( LA6_0==15 ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:78:75: '.' id= ID
            	    {
            	    match(input,15,FOLLOW_15_in_package_statement163); 
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_package_statement167); 
            	     packageName += "." + id.getText(); 

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:78:127: ( ';' )?
            int alt7=2;
            int LA7_0 = input.LA(1);
            if ( LA7_0==16 ) {
                alt7=1;
            }
            else if ( LA7_0==-1||LA7_0==EOL||(LA7_0>=17 && LA7_0<=19) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("78:127: ( \';\' )?", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:78:127: ';'
                    {
                    match(input,16,FOLLOW_16_in_package_statement174); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_package_statement177);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:81:1: import_statement returns [String importStatement] : 'import' opt_eol name= dotted_name ( ';' )? opt_eol ;
    public String import_statement() throws RecognitionException {   
        String importStatement;
        String name = null;



        		importStatement = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:85:17: ( 'import' opt_eol name= dotted_name ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:85:17: 'import' opt_eol name= dotted_name ( ';' )? opt_eol
            {
            match(input,17,FOLLOW_17_in_import_statement200); 
            following.push(FOLLOW_opt_eol_in_import_statement202);
            opt_eol();
            following.pop();

            following.push(FOLLOW_dotted_name_in_import_statement206);
            name=dotted_name();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:85:51: ( ';' )?
            int alt8=2;
            int LA8_0 = input.LA(1);
            if ( LA8_0==16 ) {
                alt8=1;
            }
            else if ( LA8_0==-1||LA8_0==EOL||(LA8_0>=17 && LA8_0<=19) ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("85:51: ( \';\' )?", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:85:51: ';'
                    {
                    match(input,16,FOLLOW_16_in_import_statement208); 

                    }
                    break;

            }

             importStatement = name; 
            following.push(FOLLOW_opt_eol_in_import_statement213);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:88:1: use_expander : 'expander' (name= dotted_name )? ( ';' )? opt_eol ;
    public void use_expander() throws RecognitionException {   
        String name = null;



        		String config=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:92:17: ( 'expander' (name= dotted_name )? ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:92:17: 'expander' (name= dotted_name )? ( ';' )? opt_eol
            {
            match(input,18,FOLLOW_18_in_use_expander231); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:92:28: (name= dotted_name )?
            int alt9=2;
            int LA9_0 = input.LA(1);
            if ( LA9_0==ID ) {
                alt9=1;
            }
            else if ( LA9_0==-1||LA9_0==EOL||LA9_0==16||LA9_0==19 ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("92:28: (name= dotted_name )?", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:92:29: name= dotted_name
                    {
                    following.push(FOLLOW_dotted_name_in_use_expander236);
                    name=dotted_name();
                    following.pop();


                    }
                    break;

            }

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:92:48: ( ';' )?
            int alt10=2;
            int LA10_0 = input.LA(1);
            if ( LA10_0==16 ) {
                alt10=1;
            }
            else if ( LA10_0==-1||LA10_0==EOL||LA10_0==19 ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("92:48: ( \';\' )?", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:92:48: ';'
                    {
                    match(input,16,FOLLOW_16_in_use_expander240); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_use_expander243);
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


    // $ANTLR start rule
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:99:1: rule returns [RuleDescr rule] : opt_eol loc= 'rule' ruleName= word opt_eol (a= rule_options )? (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( 'then' ( ':' )? ( options {greedy=false; } : any= . )* )? EOL 'end' opt_eol ;
    public RuleDescr rule() throws RecognitionException {   
        RuleDescr rule;
        Token loc=null;
        Token any=null;
        String ruleName = null;

        List a = null;



        		rule = null;
        		String consequence = "";
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:105:17: ( opt_eol loc= 'rule' ruleName= word opt_eol (a= rule_options )? (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( 'then' ( ':' )? ( options {greedy=false; } : any= . )* )? EOL 'end' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:105:17: opt_eol loc= 'rule' ruleName= word opt_eol (a= rule_options )? (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( 'then' ( ':' )? ( options {greedy=false; } : any= . )* )? EOL 'end' opt_eol
            {
            following.push(FOLLOW_opt_eol_in_rule271);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,19,FOLLOW_19_in_rule277); 
            following.push(FOLLOW_word_in_rule281);
            ruleName=word();
            following.pop();

            following.push(FOLLOW_opt_eol_in_rule283);
            opt_eol();
            following.pop();

             
            			rule = new RuleDescr( ruleName, null ); 
            			rule.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:111:17: (a= rule_options )?
            int alt11=2;
            int LA11_0 = input.LA(1);
            if ( LA11_0==24 ) {
                alt11=1;
            }
            else if ( LA11_0==EOL||LA11_0==20||LA11_0==22 ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("111:17: (a= rule_options )?", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:111:25: a= rule_options
                    {
                    following.push(FOLLOW_rule_options_in_rule296);
                    a=rule_options();
                    following.pop();


                    				rule.setAttributes( a );
                    			

                    }
                    break;

            }

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:116:17: (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?
            int alt14=2;
            int LA14_0 = input.LA(1);
            if ( LA14_0==20 ) {
                alt14=1;
            }
            else if ( LA14_0==EOL||LA14_0==22 ) {
                alt14=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("116:17: (loc= \'when\' ( \':\' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:116:25: loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                    {
                    loc=(Token)input.LT(1);
                    match(input,20,FOLLOW_20_in_rule314); 
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:116:36: ( ':' )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);
                    if ( LA12_0==21 ) {
                        int LA12_1 = input.LA(2);
                        if ( LA12_1==EOL ) {
                            int LA12_11 = input.LA(3);
                            if ( !( expander != null ) ) {
                                alt12=1;
                            }
                            else if (  expander != null  ) {
                                alt12=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("116:36: ( \':\' )?", 12, 11, input);

                                throw nvae;
                            }
                        }
                        else if ( (LA12_1>=ID && LA12_1<=44) ) {
                            alt12=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("116:36: ( \':\' )?", 12, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA12_0>=EOL && LA12_0<=20)||(LA12_0>=22 && LA12_0<=44) ) {
                        alt12=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("116:36: ( \':\' )?", 12, 0, input);

                        throw nvae;
                    }
                    switch (alt12) {
                        case 1 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:116:36: ':'
                            {
                            match(input,21,FOLLOW_21_in_rule316); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_rule319);
                    opt_eol();
                    following.pop();

                     
                    				AndDescr lhs = new AndDescr(); rule.setLhs( lhs ); 
                    				lhs.setLocation( loc.getLine(), loc.getCharPositionInLine() );
                    			
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:121:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                    int alt13=2;
                    switch ( input.LA(1) ) {
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
                    case 23:
                    case 24:
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
                    case 44:
                        alt13=1;
                        break;
                    case 22:
                        int LA13_2 = input.LA(2);
                        if (  expander != null  ) {
                            alt13=1;
                        }
                        else if ( true ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("121:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 13, 2, input);

                            throw nvae;
                        }
                        break;
                    case EOL:
                        int LA13_3 = input.LA(2);
                        if (  expander != null  ) {
                            alt13=1;
                        }
                        else if ( true ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("121:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 13, 3, input);

                            throw nvae;
                        }
                        break;
                    case 41:
                        int LA13_4 = input.LA(2);
                        if (  expander != null  ) {
                            alt13=1;
                        }
                        else if ( true ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("121:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 13, 4, input);

                            throw nvae;
                        }
                        break;
                    case 42:
                        int LA13_5 = input.LA(2);
                        if (  expander != null  ) {
                            alt13=1;
                        }
                        else if ( true ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("121:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 13, 5, input);

                            throw nvae;
                        }
                        break;
                    case 43:
                        int LA13_6 = input.LA(2);
                        if (  expander != null  ) {
                            alt13=1;
                        }
                        else if ( true ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("121:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 13, 6, input);

                            throw nvae;
                        }
                        break;
                    case ID:
                        int LA13_7 = input.LA(2);
                        if (  expander != null  ) {
                            alt13=1;
                        }
                        else if ( true ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("121:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 13, 7, input);

                            throw nvae;
                        }
                        break;
                    case 28:
                        int LA13_8 = input.LA(2);
                        if (  expander != null  ) {
                            alt13=1;
                        }
                        else if ( true ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("121:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 13, 8, input);

                            throw nvae;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("121:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 13, 0, input);

                        throw nvae;
                    }

                    switch (alt13) {
                        case 1 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:122:33: {...}? expander_lhs_block[lhs]
                            {
                            if ( !( expander != null ) ) {
                                throw new FailedPredicateException(input, "rule", " expander != null ");
                            }
                            following.push(FOLLOW_expander_lhs_block_in_rule337);
                            expander_lhs_block(lhs);
                            following.pop();


                            }
                            break;
                        case 2 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:123:35: normal_lhs_block[lhs]
                            {
                            following.push(FOLLOW_normal_lhs_block_in_rule346);
                            normal_lhs_block(lhs);
                            following.pop();


                            }
                            break;

                    }


                    }
                    break;

            }

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:127:17: ( 'then' ( ':' )? ( options {greedy=false; } : any= . )* )?
            int alt17=2;
            int LA17_0 = input.LA(1);
            if ( LA17_0==22 ) {
                alt17=1;
            }
            else if ( LA17_0==EOL ) {
                alt17=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("127:17: ( \'then\' ( \':\' )? ( options {greedy=false; } : any= . )* )?", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:127:25: 'then' ( ':' )? ( options {greedy=false; } : any= . )*
                    {
                    match(input,22,FOLLOW_22_in_rule369); 
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:127:32: ( ':' )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);
                    if ( LA15_0==21 ) {
                        alt15=1;
                    }
                    else if ( (LA15_0>=EOL && LA15_0<=20)||(LA15_0>=22 && LA15_0<=44) ) {
                        alt15=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("127:32: ( \':\' )?", 15, 0, input);

                        throw nvae;
                    }
                    switch (alt15) {
                        case 1 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:127:32: ':'
                            {
                            match(input,21,FOLLOW_21_in_rule371); 

                            }
                            break;

                    }

                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:128:25: ( options {greedy=false; } : any= . )*
                    loop16:
                    do {
                        int alt16=2;
                        alt16 = dfa16.predict(input); 
                        switch (alt16) {
                    	case 1 :
                    	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:128:51: any= .
                    	    {
                    	    any=(Token)input.LT(1);
                    	    matchAny(input); 

                    	    					consequence = consequence + " " + any.getText();
                    	    				

                    	    }
                    	    break;

                    	default :
                    	    break loop16;
                        }
                    } while (true);

                     rule.setConsequence( consequence ); 

                    }
                    break;

            }

            match(input,EOL,FOLLOW_EOL_in_rule415); 
            match(input,23,FOLLOW_23_in_rule417); 
            following.push(FOLLOW_opt_eol_in_rule419);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:138:1: rule_options returns [List options] : 'options' ( ':' )? opt_eol (a= rule_option opt_eol )* ;
    public List rule_options() throws RecognitionException {   
        List options;
        AttributeDescr a = null;



        		options = new ArrayList();
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:142:17: ( 'options' ( ':' )? opt_eol (a= rule_option opt_eol )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:142:17: 'options' ( ':' )? opt_eol (a= rule_option opt_eol )*
            {
            match(input,24,FOLLOW_24_in_rule_options440); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:142:27: ( ':' )?
            int alt18=2;
            int LA18_0 = input.LA(1);
            if ( LA18_0==21 ) {
                alt18=1;
            }
            else if ( LA18_0==EOL||LA18_0==20||LA18_0==22||(LA18_0>=25 && LA18_0<=26) ) {
                alt18=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("142:27: ( \':\' )?", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:142:27: ':'
                    {
                    match(input,21,FOLLOW_21_in_rule_options442); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_rule_options445);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:143:25: (a= rule_option opt_eol )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);
                if ( (LA19_0>=25 && LA19_0<=26) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:143:33: a= rule_option opt_eol
            	    {
            	    following.push(FOLLOW_rule_option_in_rule_options454);
            	    a=rule_option();
            	    following.pop();

            	    following.push(FOLLOW_opt_eol_in_rule_options456);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:150:1: rule_option returns [AttributeDescr d] : (a= salience | a= no_loop );
    public AttributeDescr rule_option() throws RecognitionException {   
        AttributeDescr d;
        AttributeDescr a = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:155:25: (a= salience | a= no_loop )
            int alt20=2;
            int LA20_0 = input.LA(1);
            if ( LA20_0==25 ) {
                alt20=1;
            }
            else if ( LA20_0==26 ) {
                alt20=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("150:1: rule_option returns [AttributeDescr d] : (a= salience | a= no_loop );", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:155:25: a= salience
                    {
                    following.push(FOLLOW_salience_in_rule_option495);
                    a=salience();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:156:25: a= no_loop
                    {
                    following.push(FOLLOW_no_loop_in_rule_option505);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:160:1: salience returns [AttributeDescr d ] : loc= 'salience' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr salience() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token i=null;


        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:165:17: (loc= 'salience' opt_eol i= INT ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:165:17: loc= 'salience' opt_eol i= INT ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,25,FOLLOW_25_in_salience538); 
            following.push(FOLLOW_opt_eol_in_salience540);
            opt_eol();
            following.pop();

            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience544); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:165:46: ( ';' )?
            int alt21=2;
            int LA21_0 = input.LA(1);
            if ( LA21_0==16 ) {
                alt21=1;
            }
            else if ( LA21_0==EOL||LA21_0==20||LA21_0==22||(LA21_0>=25 && LA21_0<=26) ) {
                alt21=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("165:46: ( \';\' )?", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:165:46: ';'
                    {
                    match(input,16,FOLLOW_16_in_salience546); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_salience549);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:172:1: no_loop returns [AttributeDescr d] : loc= 'no-loop' ( ';' )? opt_eol ;
    public AttributeDescr no_loop() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;


        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:177:17: (loc= 'no-loop' ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:177:17: loc= 'no-loop' ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,26,FOLLOW_26_in_no_loop579); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:177:31: ( ';' )?
            int alt22=2;
            int LA22_0 = input.LA(1);
            if ( LA22_0==16 ) {
                alt22=1;
            }
            else if ( LA22_0==EOL||LA22_0==20||LA22_0==22||(LA22_0>=25 && LA22_0<=26) ) {
                alt22=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("177:31: ( \';\' )?", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:177:31: ';'
                    {
                    match(input,16,FOLLOW_16_in_no_loop581); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_no_loop584);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:184:1: expander_lhs_block[AndDescr descrs] : ( ( options {greedy=false; } : a= . EOL ) | '>' d= lhs )* ;
    public void expander_lhs_block(AndDescr descrs) throws RecognitionException {   
        Token a=null;
        PatternDescr d = null;



        		String text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:189:17: ( ( ( options {greedy=false; } : a= . EOL ) | '>' d= lhs )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:189:17: ( ( options {greedy=false; } : a= . EOL ) | '>' d= lhs )*
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:189:17: ( ( options {greedy=false; } : a= . EOL ) | '>' d= lhs )*
            loop23:
            do {
                int alt23=3;
                switch ( input.LA(1) ) {
                case 22:
                    int LA23_1 = input.LA(2);
                    if ( LA23_1==EOL ) {
                        alt23=1;
                    }


                    break;
                case EOL:
                    int LA23_2 = input.LA(2);
                    if ( LA23_2==EOL ) {
                        alt23=1;
                    }


                    break;
                case 27:
                    int LA23_3 = input.LA(2);
                    if ( LA23_3==EOL ) {
                        alt23=1;
                    }
                    else if ( LA23_3==ID||LA23_3==28||(LA23_3>=41 && LA23_3<=43) ) {
                        alt23=2;
                    }


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
                    alt23=1;
                    break;

                }

                switch (alt23) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:190:25: ( options {greedy=false; } : a= . EOL )
            	    {
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:190:25: ( options {greedy=false; } : a= . EOL )
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:190:58: a= . EOL
            	    {
            	    a=(Token)input.LT(1);
            	    matchAny(input); 

            	    					if ( text == null ) {
            	    						text = a.getText();
            	    					} else {
            	    						text = text + " " + a.getText();
            	    					}
            	    				
            	    match(input,EOL,FOLLOW_EOL_in_expander_lhs_block640); 

            	    					d = runExpander( text );
            	    					descrs.addDescr( d );
            	    					text = null;
            	    					d = null;
            	    				

            	    }


            	    }
            	    break;
            	case 2 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:206:27: '>' d= lhs
            	    {
            	    match(input,27,FOLLOW_27_in_expander_lhs_block659); 
            	    following.push(FOLLOW_lhs_in_expander_lhs_block663);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:211:1: normal_lhs_block[AndDescr descrs] : (d= lhs )* ;
    public void normal_lhs_block(AndDescr descrs) throws RecognitionException {   
        PatternDescr d = null;


        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:213:17: ( (d= lhs )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:213:17: (d= lhs )*
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:213:17: (d= lhs )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);
                if ( LA24_0==ID||LA24_0==28||(LA24_0>=41 && LA24_0<=43) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:213:25: d= lhs
            	    {
            	    following.push(FOLLOW_lhs_in_normal_lhs_block690);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:219:1: lhs returns [PatternDescr d] : l= lhs_or ;
    public PatternDescr lhs() throws RecognitionException {   
        PatternDescr d;
        PatternDescr l = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:223:17: (l= lhs_or )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:223:17: l= lhs_or
            {
            following.push(FOLLOW_lhs_or_in_lhs726);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:227:1: lhs_column returns [ColumnDescr d] : (f= fact_binding | f= fact );
    public ColumnDescr lhs_column() throws RecognitionException {   
        ColumnDescr d;
        ColumnDescr f = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:231:17: (f= fact_binding | f= fact )
            int alt25=2;
            alt25 = dfa25.predict(input); 
            switch (alt25) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:231:17: f= fact_binding
                    {
                    following.push(FOLLOW_fact_binding_in_lhs_column753);
                    f=fact_binding();
                    following.pop();

                     d = f; 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:232:17: f= fact
                    {
                    following.push(FOLLOW_fact_in_lhs_column762);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:235:1: fact_binding returns [ColumnDescr d] : id= ID opt_eol ':' opt_eol f= fact opt_eol ;
    public ColumnDescr fact_binding() throws RecognitionException {   
        ColumnDescr d;
        Token id=null;
        ColumnDescr f = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:240:17: (id= ID opt_eol ':' opt_eol f= fact opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:240:17: id= ID opt_eol ':' opt_eol f= fact opt_eol
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding794); 

             			System.err.println( "fact_binding(" + id.getText() + ")" );
             		
            following.push(FOLLOW_opt_eol_in_fact_binding809);
            opt_eol();
            following.pop();

            match(input,21,FOLLOW_21_in_fact_binding811); 
            following.push(FOLLOW_opt_eol_in_fact_binding813);
            opt_eol();
            following.pop();

            following.push(FOLLOW_fact_in_fact_binding817);
            f=fact();
            following.pop();

             d=f; 
            following.push(FOLLOW_opt_eol_in_fact_binding821);
            opt_eol();
            following.pop();


             			d=f;
             			d.setIdentifier( id.getText() );
             		

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:252:1: fact returns [ColumnDescr d] : id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol ;
    public ColumnDescr fact() throws RecognitionException {   
        ColumnDescr d;
        Token id=null;
        List c = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:256:17: (id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:256:17: id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact853); 
             
             			d = new ColumnDescr( id.getText() ); 
             			d.setLocation( id.getLine(), id.getCharPositionInLine() );
             		
            following.push(FOLLOW_opt_eol_in_fact861);
            opt_eol();
            following.pop();

            match(input,28,FOLLOW_28_in_fact867); 
            following.push(FOLLOW_opt_eol_in_fact869);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:261:29: (c= constraints )?
            int alt26=2;
            alt26 = dfa26.predict(input); 
            switch (alt26) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:261:33: c= constraints
                    {
                    following.push(FOLLOW_constraints_in_fact875);
                    c=constraints();
                    following.pop();


                    		 			for ( Iterator cIter = c.iterator() ; cIter.hasNext() ; ) {
                     						d.addDescr( (PatternDescr) cIter.next() );
                     					}
                     				

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_fact894);
            opt_eol();
            following.pop();

            match(input,29,FOLLOW_29_in_fact896); 
            following.push(FOLLOW_opt_eol_in_fact898);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:272:1: constraints returns [List constraints] : opt_eol constraint[constraints] ( opt_eol ',' opt_eol constraint[constraints] )* opt_eol ;
    public List constraints() throws RecognitionException {   
        List constraints;

        		constraints = new ArrayList();
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:276:17: ( opt_eol constraint[constraints] ( opt_eol ',' opt_eol constraint[constraints] )* opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:276:17: opt_eol constraint[constraints] ( opt_eol ',' opt_eol constraint[constraints] )* opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraints924);
            opt_eol();
            following.pop();

            following.push(FOLLOW_constraint_in_constraints928);
            constraint(constraints);
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:278:17: ( opt_eol ',' opt_eol constraint[constraints] )*
            loop27:
            do {
                int alt27=2;
                alt27 = dfa27.predict(input); 
                switch (alt27) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:278:19: opt_eol ',' opt_eol constraint[constraints]
            	    {
            	    following.push(FOLLOW_opt_eol_in_constraints935);
            	    opt_eol();
            	    following.pop();

            	    match(input,30,FOLLOW_30_in_constraints937); 
            	    following.push(FOLLOW_opt_eol_in_constraints939);
            	    opt_eol();
            	    following.pop();

            	    following.push(FOLLOW_constraint_in_constraints941);
            	    constraint(constraints);
            	    following.pop();


            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_constraints948);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:282:1: constraint[List constraints] : opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains') opt_eol (lc= literal_constraint | rvc= retval_constraint ) opt_eol ;
    public void constraint(List constraints) throws RecognitionException {   
        Token fb=null;
        Token f=null;
        Token op=null;
        String lc = null;

        String rvc = null;



        		PatternDescr d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:286:17: ( opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains') opt_eol (lc= literal_constraint | rvc= retval_constraint ) opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:286:17: opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains') opt_eol (lc= literal_constraint | rvc= retval_constraint ) opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraint967);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:287:17: (fb= ID opt_eol ':' opt_eol )?
            int alt28=2;
            alt28 = dfa28.predict(input); 
            switch (alt28) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:287:19: fb= ID opt_eol ':' opt_eol
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint975); 
                    following.push(FOLLOW_opt_eol_in_constraint977);
                    opt_eol();
                    following.pop();

                    match(input,21,FOLLOW_21_in_constraint979); 
                    following.push(FOLLOW_opt_eol_in_constraint981);
                    opt_eol();
                    following.pop();


                    }
                    break;

            }

            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint991); 

            			if ( fb != null ) {
            				System.err.println( "fb: " + fb.getText() );
            				System.err.println( " f: " + f.getText() );
            				d = new FieldBindingDescr( f.getText(), fb.getText() );
            				System.err.println( "fbd: " + d );
            				
            				d.setLocation( f.getLine(), f.getCharPositionInLine() );
            				constraints.add( d );
            			} 
            		
            following.push(FOLLOW_opt_eol_in_constraint1001);
            opt_eol();
            following.pop();

            op=(Token)input.LT(1);
            if ( input.LA(1)==27||(input.LA(1)>=31 && input.LA(1)<=36) ) {
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint1008);    throw mse;
            }

            following.push(FOLLOW_opt_eol_in_constraint1071);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:309:41: (lc= literal_constraint | rvc= retval_constraint )
            int alt29=2;
            switch ( input.LA(1) ) {
            case STRING:
                alt29=1;
                break;
            case INT:
                alt29=1;
                break;
            case FLOAT:
                alt29=1;
                break;
            case EOL:
            case ID:
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
                alt29=2;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("309:41: (lc= literal_constraint | rvc= retval_constraint )", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:309:49: lc= literal_constraint
                    {
                    following.push(FOLLOW_literal_constraint_in_constraint1089);
                    lc=literal_constraint();
                    following.pop();

                     
                    							d = new LiteralDescr( f.getText(), op.getText(), lc ); 
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    							constraints.add( d );
                    						

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:315:49: rvc= retval_constraint
                    {
                    following.push(FOLLOW_retval_constraint_in_constraint1109);
                    rvc=retval_constraint();
                    following.pop();

                     
                    							d = new ReturnValueDescr( f.getText(), op.getText(), rvc ); 
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    							constraints.add( d );
                    						

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_constraint1130);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:325:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT ) ;
    public String literal_constraint() throws RecognitionException {   
        String text;
        Token t=null;


        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:329:17: ( (t= STRING | t= INT | t= FLOAT ) )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:329:17: (t= STRING | t= INT | t= FLOAT )
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:329:17: (t= STRING | t= INT | t= FLOAT )
            int alt30=3;
            switch ( input.LA(1) ) {
            case STRING:
                alt30=1;
                break;
            case INT:
                alt30=2;
                break;
            case FLOAT:
                alt30=3;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("329:17: (t= STRING | t= INT | t= FLOAT )", 30, 0, input);

                throw nvae;
            }

            switch (alt30) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:329:25: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint1156); 
                     text = t.getText(); text=text.substring( 1, text.length() - 1 ); 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:330:25: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint1166); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:331:25: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint1179); 
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:335:1: retval_constraint returns [String text] : c= chunk ;
    public String retval_constraint() throws RecognitionException {   
        String text;
        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:340:17: (c= chunk )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:340:17: c= chunk
            {
            following.push(FOLLOW_chunk_in_retval_constraint1213);
            c=chunk();
            following.pop();

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:343:1: chunk returns [String text] : ( (any= . ) | ( '(' c= chunk ')' ) )* ;
    public String chunk() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:347:17: ( ( (any= . ) | ( '(' c= chunk ')' ) )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:347:17: ( (any= . ) | ( '(' c= chunk ')' ) )*
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:347:17: ( (any= . ) | ( '(' c= chunk ')' ) )*
            loop31:
            do {
                int alt31=3;
                alt31 = dfa31.predict(input); 
                switch (alt31) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:347:25: (any= . )
            	    {
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:347:25: (any= . )
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:347:27: any= .
            	    {
            	    any=(Token)input.LT(1);
            	    matchAny(input); 

            	    					if ( text == null ) {
            	    						text = any.getText();
            	    					} else {
            	    						text = text + " " + any.getText(); 
            	    					} 
            	    				

            	    }


            	    }
            	    break;
            	case 2 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:354:25: ( '(' c= chunk ')' )
            	    {
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:354:25: ( '(' c= chunk ')' )
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:354:27: '(' c= chunk ')'
            	    {
            	    match(input,28,FOLLOW_28_in_chunk1254); 
            	    following.push(FOLLOW_chunk_in_chunk1258);
            	    c=chunk();
            	    following.pop();

            	    match(input,29,FOLLOW_29_in_chunk1260); 

            	    							if ( text == null ) {
            	    								text = "( " + c + " )";
            	    							} else {
            	    								text = text + " ( " + c + " )";
            	    							}
            	    						

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
        return text;
    }
    // $ANTLR end chunk


    // $ANTLR start lhs_or
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:364:1: lhs_or returns [PatternDescr d] : left= lhs_and ( ('or'|'||')right= lhs_and )* ;
    public PatternDescr lhs_or() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:369:17: (left= lhs_and ( ('or'|'||')right= lhs_and )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:369:17: left= lhs_and ( ('or'|'||')right= lhs_and )*
            {
             OrDescr or = null; 
            following.push(FOLLOW_lhs_and_in_lhs_or1300);
            left=lhs_and();
            following.pop();

            d = left; 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:371:17: ( ('or'|'||')right= lhs_and )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);
                if ( (LA32_0>=37 && LA32_0<=38) ) {
                    int LA32_10 = input.LA(2);
                    if ( LA32_10==ID||LA32_10==28||(LA32_10>=41 && LA32_10<=43) ) {
                        alt32=1;
                    }


                }


                switch (alt32) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:371:25: ('or'|'||')right= lhs_and
            	    {
            	    if ( (input.LA(1)>=37 && input.LA(1)<=38) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or1310);    throw mse;
            	    }

            	    following.push(FOLLOW_lhs_and_in_lhs_or1321);
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
        return d;
    }
    // $ANTLR end lhs_or


    // $ANTLR start lhs_and
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:385:1: lhs_and returns [PatternDescr d] : left= lhs_unary ( ('and'|'&&')right= lhs_unary )* ;
    public PatternDescr lhs_and() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:390:17: (left= lhs_unary ( ('and'|'&&')right= lhs_unary )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:390:17: left= lhs_unary ( ('and'|'&&')right= lhs_unary )*
            {
             AndDescr and = null; 
            following.push(FOLLOW_lhs_unary_in_lhs_and1361);
            left=lhs_unary();
            following.pop();

             d = left; 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:392:17: ( ('and'|'&&')right= lhs_unary )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);
                if ( (LA33_0>=39 && LA33_0<=40) ) {
                    int LA33_11 = input.LA(2);
                    if ( LA33_11==ID||LA33_11==28||(LA33_11>=41 && LA33_11<=43) ) {
                        alt33=1;
                    }


                }


                switch (alt33) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:392:25: ('and'|'&&')right= lhs_unary
            	    {
            	    if ( (input.LA(1)>=39 && input.LA(1)<=40) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and1370);    throw mse;
            	    }

            	    following.push(FOLLOW_lhs_unary_in_lhs_and1381);
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
    // $ANTLR end lhs_and


    // $ANTLR start lhs_unary
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:406:1: lhs_unary returns [PatternDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) ;
    public PatternDescr lhs_unary() throws RecognitionException {   
        PatternDescr d;
        PatternDescr u = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:410:17: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:410:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:410:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            int alt34=5;
            switch ( input.LA(1) ) {
            case 41:
                alt34=1;
                break;
            case 42:
                alt34=2;
                break;
            case 43:
                alt34=3;
                break;
            case ID:
                alt34=4;
                break;
            case 28:
                alt34=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("410:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | \'(\' u= lhs \')\' )", 34, 0, input);

                throw nvae;
            }

            switch (alt34) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:410:25: u= lhs_exist
                    {
                    following.push(FOLLOW_lhs_exist_in_lhs_unary1419);
                    u=lhs_exist();
                    following.pop();


                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:411:25: u= lhs_not
                    {
                    following.push(FOLLOW_lhs_not_in_lhs_unary1427);
                    u=lhs_not();
                    following.pop();


                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:412:25: u= lhs_eval
                    {
                    following.push(FOLLOW_lhs_eval_in_lhs_unary1435);
                    u=lhs_eval();
                    following.pop();


                    }
                    break;
                case 4 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:413:25: u= lhs_column
                    {
                    following.push(FOLLOW_lhs_column_in_lhs_unary1443);
                    u=lhs_column();
                    following.pop();


                    }
                    break;
                case 5 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:414:25: '(' u= lhs ')'
                    {
                    match(input,28,FOLLOW_28_in_lhs_unary1449); 
                    following.push(FOLLOW_lhs_in_lhs_unary1453);
                    u=lhs();
                    following.pop();

                    match(input,29,FOLLOW_29_in_lhs_unary1455); 

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:418:1: lhs_exist returns [PatternDescr d] : loc= 'exists' column= lhs_column ;
    public PatternDescr lhs_exist() throws RecognitionException {   
        PatternDescr d;
        Token loc=null;
        ColumnDescr column = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:422:17: (loc= 'exists' column= lhs_column )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:422:17: loc= 'exists' column= lhs_column
            {
            loc=(Token)input.LT(1);
            match(input,41,FOLLOW_41_in_lhs_exist1485); 
            following.push(FOLLOW_lhs_column_in_lhs_exist1489);
            column=lhs_column();
            following.pop();

             
            			d = new ExistsDescr( column ); 
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:429:1: lhs_not returns [NotDescr d] : loc= 'not' column= lhs_column ;
    public NotDescr lhs_not() throws RecognitionException {   
        NotDescr d;
        Token loc=null;
        ColumnDescr column = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:433:17: (loc= 'not' column= lhs_column )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:433:17: loc= 'not' column= lhs_column
            {
            loc=(Token)input.LT(1);
            match(input,42,FOLLOW_42_in_lhs_not1519); 
            following.push(FOLLOW_lhs_column_in_lhs_not1523);
            column=lhs_column();
            following.pop();


            			d = new NotDescr( column ); 
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:440:1: lhs_eval returns [PatternDescr d] : 'eval' ;
    public PatternDescr lhs_eval() throws RecognitionException {   
        PatternDescr d;

        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:444:17: ( 'eval' )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:444:17: 'eval'
            {
            match(input,43,FOLLOW_43_in_lhs_eval1549); 
             d = new EvalDescr( "" ); 

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:447:1: dotted_name returns [String name] : id= ID ( '.' id= ID )* ;
    public String dotted_name() throws RecognitionException {   
        String name;
        Token id=null;


        		name = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:452:17: (id= ID ( '.' id= ID )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:452:17: id= ID ( '.' id= ID )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name1578); 
             name=id.getText(); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:452:46: ( '.' id= ID )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);
                if ( LA35_0==15 ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:452:48: '.' id= ID
            	    {
            	    match(input,15,FOLLOW_15_in_dotted_name1584); 
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_dotted_name1588); 
            	     name = name + "." + id.getText(); 

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
        return name;
    }
    // $ANTLR end dotted_name


    // $ANTLR start word
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:456:1: word returns [String word] : (id= ID | 'import' | 'use' | 'rule' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING );
    public String word() throws RecognitionException {   
        String word;
        Token id=null;
        Token str=null;


        		word = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:460:17: (id= ID | 'import' | 'use' | 'rule' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING )
            int alt36=10;
            switch ( input.LA(1) ) {
            case ID:
                alt36=1;
                break;
            case 17:
                alt36=2;
                break;
            case 44:
                alt36=3;
                break;
            case 19:
                alt36=4;
                break;
            case 25:
                alt36=5;
                break;
            case 26:
                alt36=6;
                break;
            case 20:
                alt36=7;
                break;
            case 22:
                alt36=8;
                break;
            case 23:
                alt36=9;
                break;
            case STRING:
                alt36=10;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("456:1: word returns [String word] : (id= ID | \'import\' | \'use\' | \'rule\' | \'salience\' | \'no-loop\' | \'when\' | \'then\' | \'end\' | str= STRING );", 36, 0, input);

                throw nvae;
            }

            switch (alt36) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:460:17: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_word1618); 
                     word=id.getText(); 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:461:17: 'import'
                    {
                    match(input,17,FOLLOW_17_in_word1630); 
                     word="import"; 

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:462:17: 'use'
                    {
                    match(input,44,FOLLOW_44_in_word1639); 
                     word="use"; 

                    }
                    break;
                case 4 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:463:17: 'rule'
                    {
                    match(input,19,FOLLOW_19_in_word1651); 
                     word="rule"; 

                    }
                    break;
                case 5 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:464:17: 'salience'
                    {
                    match(input,25,FOLLOW_25_in_word1662); 
                     word="salience"; 

                    }
                    break;
                case 6 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:465:17: 'no-loop'
                    {
                    match(input,26,FOLLOW_26_in_word1670); 
                     word="no-loop"; 

                    }
                    break;
                case 7 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:466:17: 'when'
                    {
                    match(input,20,FOLLOW_20_in_word1678); 
                     word="when"; 

                    }
                    break;
                case 8 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:467:17: 'then'
                    {
                    match(input,22,FOLLOW_22_in_word1689); 
                     word="then"; 

                    }
                    break;
                case 9 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:468:17: 'end'
                    {
                    match(input,23,FOLLOW_23_in_word1700); 
                     word="end"; 

                    }
                    break;
                case 10 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:469:17: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_word1714); 
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


    protected DFA16 dfa16 = new DFA16();protected DFA25 dfa25 = new DFA25();protected DFA26 dfa26 = new DFA26();protected DFA27 dfa27 = new DFA27();protected DFA28 dfa28 = new DFA28();protected DFA31 dfa31 = new DFA31();
    class DFA16 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s2 = new DFA.State() {{alt=1;}};
        DFA.State s15 = new DFA.State() {{alt=2;}};
        DFA.State s16 = new DFA.State() {{alt=2;}};
        DFA.State s17 = new DFA.State() {{alt=2;}};
        DFA.State s18 = new DFA.State() {{alt=2;}};
        DFA.State s19 = new DFA.State() {{alt=2;}};
        DFA.State s20 = new DFA.State() {{alt=2;}};
        DFA.State s21 = new DFA.State() {{alt=2;}};
        DFA.State s22 = new DFA.State() {{alt=2;}};
        DFA.State s23 = new DFA.State() {{alt=2;}};
        DFA.State s24 = new DFA.State() {{alt=2;}};
        DFA.State s8 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s15;

                case 17:
                    return s16;

                case 44:
                    return s17;

                case 19:
                    return s18;

                case 25:
                    return s19;

                case 26:
                    return s20;

                case 20:
                    return s21;

                case 22:
                    return s22;

                case 23:
                    return s23;

                case STRING:
                    return s24;

                case EOL:
                case INT:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 14:
                case 15:
                case 16:
                case 18:
                case 21:
                case 24:
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
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 8, input);

                    throw nvae;        }
            }
        };
        DFA.State s7 = new DFA.State() {{alt=2;}};
        DFA.State s6 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
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
                    return s2;

                case 19:
                    return s8;

                case EOL:
                    return s6;

                case -1:
                    return s7;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 6, input);

                    throw nvae;        }
            }
        };
        DFA.State s3 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s6;

                case -1:
                    return s7;

                case 19:
                    return s8;

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
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 3, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_1 = input.LA(1);
                if ( LA16_1==23 ) {return s3;}
                if ( (LA16_1>=EOL && LA16_1<=22)||(LA16_1>=24 && LA16_1<=44) ) {return s2;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 16, 1, input);

                throw nvae;
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_0 = input.LA(1);
                if ( LA16_0==EOL ) {return s1;}
                if ( (LA16_0>=ID && LA16_0<=44) ) {return s2;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }
        };

    }class DFA25 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s4 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 28:
                    return s4;

                case EOL:
                    return s2;

                case 21:
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

                case 21:
                    return s3;

                case 28:
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

    }class DFA26 extends DFA {
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

                case 29:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 1, input);

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

                case 29:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA27 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 30:
                    return s3;

                case EOL:
                    return s1;

                case 29:
                    return s2;

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

                case 29:
                    return s2;

                case 30:
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
        DFA.State s4 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 27:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                    return s4;

                case EOL:
                    return s2;

                case 21:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 28, 2, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s2;

                case 21:
                    return s3;

                case 27:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 28, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA28_0 = input.LA(1);
                if ( LA28_0==ID ) {return s1;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 28, 0, input);

                throw nvae;
            }
        };

    }class DFA31 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s1 = new DFA.State() {{alt=1;}};
        DFA.State s4 = new DFA.State() {{alt=1;}};
        DFA.State s22 = new DFA.State() {{alt=1;}};
        DFA.State s44 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA31_44 = input.LA(1);
                if ( LA31_44==EOL ) {return s44;}
                if ( (LA31_44>=ID && LA31_44<=44) ) {return s22;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 31, 44, input);

                throw nvae;
            }
        };
        DFA.State s36 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA31_36 = input.LA(1);
                if ( LA31_36==EOL ) {return s44;}
                if ( (LA31_36>=ID && LA31_36<=44) ) {return s22;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 31, 36, input);

                throw nvae;
            }
        };
        DFA.State s32 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s32;

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
                    return s22;

                case ID:
                    return s36;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 31, 32, input);

                    throw nvae;        }
            }
        };
        DFA.State s23 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s32;

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
                    return s22;

                case ID:
                    return s36;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 31, 23, input);

                    throw nvae;        }
            }
        };
        DFA.State s18 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s18;

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
                case 22:
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
                    return s22;

                case 21:
                    return s23;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 31, 18, input);

                    throw nvae;        }
            }
        };
        DFA.State s10 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s18;

                case 28:
                case 29:
                case 30:
                    return s4;

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
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
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
                    return s22;

                case 21:
                    return s23;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 31, 10, input);

                    throw nvae;        }
            }
        };
        DFA.State s6 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s6;

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
                    return s4;

                case ID:
                    return s10;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 31, 6, input);

                    throw nvae;        }
            }
        };
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s6;

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
                    return s4;

                case ID:
                    return s10;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 31, 2, input);

                    throw nvae;        }
            }
        };
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s1;

                case 30:
                    return s2;

                case 29:
                    return s3;

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
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 31, 0, input);

                    throw nvae;        }
            }
        };

    }


    public static final BitSet FOLLOW_EOL_in_opt_eol40 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit53 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_rule_in_compilation_unit61 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_prolog83 = new BitSet(new long[]{0x0000000000004012L});
    public static final BitSet FOLLOW_package_statement_in_prolog91 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_prolog100 = new BitSet(new long[]{0x0000000000020012L});
    public static final BitSet FOLLOW_import_statement_in_prolog112 = new BitSet(new long[]{0x0000000000020012L});
    public static final BitSet FOLLOW_opt_eol_in_prolog121 = new BitSet(new long[]{0x0000000000040012L});
    public static final BitSet FOLLOW_use_expander_in_prolog125 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_prolog130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_package_statement151 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_package_statement153 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_package_statement157 = new BitSet(new long[]{0x0000000000018012L});
    public static final BitSet FOLLOW_15_in_package_statement163 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_package_statement167 = new BitSet(new long[]{0x0000000000018012L});
    public static final BitSet FOLLOW_16_in_package_statement174 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_package_statement177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_import_statement200 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_import_statement202 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_dotted_name_in_import_statement206 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_import_statement208 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_import_statement213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_use_expander231 = new BitSet(new long[]{0x0000000000010032L});
    public static final BitSet FOLLOW_dotted_name_in_use_expander236 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_use_expander240 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_use_expander243 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_rule271 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_rule277 = new BitSet(new long[]{0x0000100006DA00A0L});
    public static final BitSet FOLLOW_word_in_rule281 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule283 = new BitSet(new long[]{0x0000000001500010L});
    public static final BitSet FOLLOW_rule_options_in_rule296 = new BitSet(new long[]{0x0000000000500010L});
    public static final BitSet FOLLOW_20_in_rule314 = new BitSet(new long[]{0x0000000000200012L});
    public static final BitSet FOLLOW_21_in_rule316 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule319 = new BitSet(new long[]{0x00001FFFFFFFFFF2L});
    public static final BitSet FOLLOW_expander_lhs_block_in_rule337 = new BitSet(new long[]{0x0000000000400010L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule346 = new BitSet(new long[]{0x0000000000400010L});
    public static final BitSet FOLLOW_22_in_rule369 = new BitSet(new long[]{0x00001FFFFFFFFFF0L});
    public static final BitSet FOLLOW_21_in_rule371 = new BitSet(new long[]{0x00001FFFFFFFFFF0L});
    public static final BitSet FOLLOW_EOL_in_rule415 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_rule417 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule419 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_rule_options440 = new BitSet(new long[]{0x0000000000200012L});
    public static final BitSet FOLLOW_21_in_rule_options442 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule_options445 = new BitSet(new long[]{0x0000000006000002L});
    public static final BitSet FOLLOW_rule_option_in_rule_options454 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_rule_options456 = new BitSet(new long[]{0x0000000006000002L});
    public static final BitSet FOLLOW_salience_in_rule_option495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_option505 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_salience538 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_salience540 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_salience544 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_salience546 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_salience549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_no_loop579 = new BitSet(new long[]{0x0000000000010012L});
    public static final BitSet FOLLOW_16_in_no_loop581 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EOL_in_expander_lhs_block640 = new BitSet(new long[]{0x00001FFFFFFFFFF2L});
    public static final BitSet FOLLOW_27_in_expander_lhs_block659 = new BitSet(new long[]{0x00000E0010000020L});
    public static final BitSet FOLLOW_lhs_in_expander_lhs_block663 = new BitSet(new long[]{0x00001FFFFFFFFFF2L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block690 = new BitSet(new long[]{0x00000E0010000022L});
    public static final BitSet FOLLOW_lhs_or_in_lhs726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column753 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_column762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding794 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding809 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_fact_binding811 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding813 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_fact_in_fact_binding817 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding821 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact853 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact861 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_fact867 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact869 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_constraints_in_fact875 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact894 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_fact896 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraints924 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_constraint_in_constraints928 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints935 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_constraints937 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints939 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_constraint_in_constraints941 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints948 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraint967 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint975 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint977 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_constraint979 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint981 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint991 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1001 = new BitSet(new long[]{0x0000001F88000000L});
    public static final BitSet FOLLOW_set_in_constraint1008 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1071 = new BitSet(new long[]{0x00001FFFFFFFFFF2L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint1089 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint1109 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint1156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint1166 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint1179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_chunk_in_retval_constraint1213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_chunk1254 = new BitSet(new long[]{0x00001FFFFFFFFFF2L});
    public static final BitSet FOLLOW_chunk_in_chunk1258 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_chunk1260 = new BitSet(new long[]{0x00001FFFFFFFFFF2L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1300 = new BitSet(new long[]{0x0000006000000002L});
    public static final BitSet FOLLOW_set_in_lhs_or1310 = new BitSet(new long[]{0x00000E0010000020L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1321 = new BitSet(new long[]{0x0000006000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1361 = new BitSet(new long[]{0x0000018000000002L});
    public static final BitSet FOLLOW_set_in_lhs_and1370 = new BitSet(new long[]{0x00000E0010000020L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1381 = new BitSet(new long[]{0x0000018000000002L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary1419 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary1427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary1435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary1443 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_lhs_unary1449 = new BitSet(new long[]{0x00000E0010000020L});
    public static final BitSet FOLLOW_lhs_in_lhs_unary1453 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_lhs_unary1455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_lhs_exist1485 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist1489 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_lhs_not1519 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not1523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_lhs_eval1549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name1578 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_15_in_dotted_name1584 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_dotted_name1588 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ID_in_word1618 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_word1630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_word1639 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_word1651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_word1662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_word1670 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_word1678 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_word1689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_word1700 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_word1714 = new BitSet(new long[]{0x0000000000000002L});

}