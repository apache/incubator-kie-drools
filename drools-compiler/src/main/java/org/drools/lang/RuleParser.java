// $ANTLR 3.0ea8 /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g 2006-03-16 01:11:21

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "EOL", "ID", "INT", "STRING", "FLOAT", "MISC", "WS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "\'package\'", "\'.\'", "\';\'", "\'import\'", "\'expander\'", "\'rule\'", "\'when\'", "\':\'", "\'then\'", "\'end\'", "\'options\'", "\'salience\'", "\'no-loop\'", "\'>\'", "\'or\'", "\'(\'", "\')\'", "\',\'", "\'==\'", "\'>=\'", "\'<\'", "\'<=\'", "\'!=\'", "\'contains\'", "\'||\'", "\'and\'", "\'&&\'", "\'exists\'", "\'not\'", "\'eval\'", "\'use\'"
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
                    case 28:
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
                    case 29:
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
                    else if ( LA23_3==ID||LA23_3==29||(LA23_3>=41 && LA23_3<=43) ) {
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
                if ( LA24_0==ID||LA24_0==29||(LA24_0>=41 && LA24_0<=43) ) {
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:227:1: lhs_column returns [PatternDescr d] : (f= fact_binding | f= fact );
    public PatternDescr lhs_column() throws RecognitionException {   
        PatternDescr d;
        PatternDescr f = null;



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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:235:1: fact_binding returns [PatternDescr d] : id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )* ;
    public PatternDescr fact_binding() throws RecognitionException {   
        PatternDescr d;
        Token id=null;
        PatternDescr f = null;



        		d=null;
        		boolean multi=false;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:241:17: (id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:241:17: id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )*
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

            following.push(FOLLOW_fact_in_fact_binding821);
            f=fact();
            following.pop();

            following.push(FOLLOW_opt_eol_in_fact_binding823);
            opt_eol();
            following.pop();


             			((ColumnDescr)f).setIdentifier( id.getText() );
             			d = f;
             		
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:252:17: ( 'or' f= fact )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);
                if ( LA26_0==28 ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:252:25: 'or' f= fact
            	    {
            	    match(input,28,FOLLOW_28_in_fact_binding835); 
            	    	if ( ! multi ) {
            	     					PatternDescr first = d;
            	     					d = new OrDescr();
            	     					((OrDescr)d).addDescr( first );
            	     					multi=true;
            	     				}
            	     			
            	    following.push(FOLLOW_fact_in_fact_binding849);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:268:1: fact returns [PatternDescr d] : id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol ;
    public PatternDescr fact() throws RecognitionException {   
        PatternDescr d;
        Token id=null;
        List c = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:272:17: (id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:272:17: id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact889); 
             
             			d = new ColumnDescr( id.getText() ); 
             			d.setLocation( id.getLine(), id.getCharPositionInLine() );
             		
            following.push(FOLLOW_opt_eol_in_fact897);
            opt_eol();
            following.pop();

            match(input,29,FOLLOW_29_in_fact903); 
            following.push(FOLLOW_opt_eol_in_fact905);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:277:29: (c= constraints )?
            int alt27=2;
            alt27 = dfa27.predict(input); 
            switch (alt27) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:277:33: c= constraints
                    {
                    following.push(FOLLOW_constraints_in_fact911);
                    c=constraints();
                    following.pop();


                    		 			for ( Iterator cIter = c.iterator() ; cIter.hasNext() ; ) {
                     						((ColumnDescr)d).addDescr( (PatternDescr) cIter.next() );
                     					}
                     				

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_fact930);
            opt_eol();
            following.pop();

            match(input,30,FOLLOW_30_in_fact932); 
            following.push(FOLLOW_opt_eol_in_fact934);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:288:1: constraints returns [List constraints] : opt_eol constraint[constraints] ( opt_eol ',' opt_eol constraint[constraints] )* opt_eol ;
    public List constraints() throws RecognitionException {   
        List constraints;

        		constraints = new ArrayList();
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:292:17: ( opt_eol constraint[constraints] ( opt_eol ',' opt_eol constraint[constraints] )* opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:292:17: opt_eol constraint[constraints] ( opt_eol ',' opt_eol constraint[constraints] )* opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraints959);
            opt_eol();
            following.pop();

            following.push(FOLLOW_constraint_in_constraints963);
            constraint(constraints);
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:294:17: ( opt_eol ',' opt_eol constraint[constraints] )*
            loop28:
            do {
                int alt28=2;
                alt28 = dfa28.predict(input); 
                switch (alt28) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:294:19: opt_eol ',' opt_eol constraint[constraints]
            	    {
            	    following.push(FOLLOW_opt_eol_in_constraints970);
            	    opt_eol();
            	    following.pop();

            	    match(input,31,FOLLOW_31_in_constraints972); 
            	    following.push(FOLLOW_opt_eol_in_constraints974);
            	    opt_eol();
            	    following.pop();

            	    following.push(FOLLOW_constraint_in_constraints976);
            	    constraint(constraints);
            	    following.pop();


            	    }
            	    break;

            	default :
            	    break loop28;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_constraints983);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:298:1: constraint[List constraints] : opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains') opt_eol (lc= literal_constraint | rvc= retval_constraint ) opt_eol ;
    public void constraint(List constraints) throws RecognitionException {   
        Token fb=null;
        Token f=null;
        Token op=null;
        String lc = null;

        String rvc = null;



        		PatternDescr d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:302:17: ( opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains') opt_eol (lc= literal_constraint | rvc= retval_constraint ) opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:302:17: opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains') opt_eol (lc= literal_constraint | rvc= retval_constraint ) opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraint1002);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:303:17: (fb= ID opt_eol ':' opt_eol )?
            int alt29=2;
            alt29 = dfa29.predict(input); 
            switch (alt29) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:303:19: fb= ID opt_eol ':' opt_eol
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint1010); 
                    following.push(FOLLOW_opt_eol_in_constraint1012);
                    opt_eol();
                    following.pop();

                    match(input,21,FOLLOW_21_in_constraint1014); 
                    following.push(FOLLOW_opt_eol_in_constraint1016);
                    opt_eol();
                    following.pop();


                    }
                    break;

            }

            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint1026); 

            			if ( fb != null ) {
            				System.err.println( "fb: " + fb.getText() );
            				System.err.println( " f: " + f.getText() );
            				d = new FieldBindingDescr( f.getText(), fb.getText() );
            				System.err.println( "fbd: " + d );
            				
            				d.setLocation( f.getLine(), f.getCharPositionInLine() );
            				constraints.add( d );
            			} 
            		
            following.push(FOLLOW_opt_eol_in_constraint1036);
            opt_eol();
            following.pop();

            op=(Token)input.LT(1);
            if ( input.LA(1)==27||(input.LA(1)>=32 && input.LA(1)<=37) ) {
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint1043);    throw mse;
            }

            following.push(FOLLOW_opt_eol_in_constraint1106);
            opt_eol();
            following.pop();

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:325:41: (lc= literal_constraint | rvc= retval_constraint )
            int alt30=2;
            switch ( input.LA(1) ) {
            case STRING:
                alt30=1;
                break;
            case INT:
                alt30=1;
                break;
            case FLOAT:
                alt30=1;
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
                alt30=2;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("325:41: (lc= literal_constraint | rvc= retval_constraint )", 30, 0, input);

                throw nvae;
            }

            switch (alt30) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:325:49: lc= literal_constraint
                    {
                    following.push(FOLLOW_literal_constraint_in_constraint1124);
                    lc=literal_constraint();
                    following.pop();

                     
                    							d = new LiteralDescr( f.getText(), op.getText(), lc ); 
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    							constraints.add( d );
                    						

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:331:49: rvc= retval_constraint
                    {
                    following.push(FOLLOW_retval_constraint_in_constraint1144);
                    rvc=retval_constraint();
                    following.pop();

                     
                    							d = new ReturnValueDescr( f.getText(), op.getText(), rvc ); 
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    							constraints.add( d );
                    						

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_constraint1165);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:341:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT ) ;
    public String literal_constraint() throws RecognitionException {   
        String text;
        Token t=null;


        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:345:17: ( (t= STRING | t= INT | t= FLOAT ) )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:345:17: (t= STRING | t= INT | t= FLOAT )
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:345:17: (t= STRING | t= INT | t= FLOAT )
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
                    new NoViableAltException("345:17: (t= STRING | t= INT | t= FLOAT )", 31, 0, input);

                throw nvae;
            }

            switch (alt31) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:345:25: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint1191); 
                     text = t.getText(); text=text.substring( 1, text.length() - 1 ); 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:346:25: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint1201); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:347:25: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint1214); 
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:351:1: retval_constraint returns [String text] : c= chunk ;
    public String retval_constraint() throws RecognitionException {   
        String text;
        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:356:17: (c= chunk )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:356:17: c= chunk
            {
            following.push(FOLLOW_chunk_in_retval_constraint1248);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:359:1: chunk returns [String text] : ( (any= . ) | ( '(' c= chunk ')' ) )* ;
    public String chunk() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:363:17: ( ( (any= . ) | ( '(' c= chunk ')' ) )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:363:17: ( (any= . ) | ( '(' c= chunk ')' ) )*
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:363:17: ( (any= . ) | ( '(' c= chunk ')' ) )*
            loop32:
            do {
                int alt32=3;
                alt32 = dfa32.predict(input); 
                switch (alt32) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:363:25: (any= . )
            	    {
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:363:25: (any= . )
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:363:27: any= .
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
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:370:25: ( '(' c= chunk ')' )
            	    {
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:370:25: ( '(' c= chunk ')' )
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:370:27: '(' c= chunk ')'
            	    {
            	    match(input,29,FOLLOW_29_in_chunk1289); 
            	    following.push(FOLLOW_chunk_in_chunk1293);
            	    c=chunk();
            	    following.pop();

            	    match(input,30,FOLLOW_30_in_chunk1295); 

            	    							if ( text == null ) {
            	    								text = "( " + c + " )";
            	    							} else {
            	    								text = text + " ( " + c + " )";
            	    							}
            	    						

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:380:1: lhs_or returns [PatternDescr d] : left= lhs_and ( ('or'|'||')right= lhs_and )* ;
    public PatternDescr lhs_or() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:385:17: (left= lhs_and ( ('or'|'||')right= lhs_and )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:385:17: left= lhs_and ( ('or'|'||')right= lhs_and )*
            {
             OrDescr or = null; 
            following.push(FOLLOW_lhs_and_in_lhs_or1335);
            left=lhs_and();
            following.pop();

            d = left; 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:387:17: ( ('or'|'||')right= lhs_and )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);
                if ( LA33_0==28||LA33_0==38 ) {
                    int LA33_10 = input.LA(2);
                    if ( LA33_10==ID||LA33_10==29||(LA33_10>=41 && LA33_10<=43) ) {
                        alt33=1;
                    }


                }


                switch (alt33) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:387:25: ('or'|'||')right= lhs_and
            	    {
            	    if ( input.LA(1)==28||input.LA(1)==38 ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or1345);    throw mse;
            	    }

            	    following.push(FOLLOW_lhs_and_in_lhs_or1356);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:401:1: lhs_and returns [PatternDescr d] : left= lhs_unary ( ('and'|'&&')right= lhs_unary )* ;
    public PatternDescr lhs_and() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:406:17: (left= lhs_unary ( ('and'|'&&')right= lhs_unary )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:406:17: left= lhs_unary ( ('and'|'&&')right= lhs_unary )*
            {
             AndDescr and = null; 
            following.push(FOLLOW_lhs_unary_in_lhs_and1396);
            left=lhs_unary();
            following.pop();

             d = left; 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:408:17: ( ('and'|'&&')right= lhs_unary )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);
                if ( (LA34_0>=39 && LA34_0<=40) ) {
                    int LA34_11 = input.LA(2);
                    if ( LA34_11==ID||LA34_11==29||(LA34_11>=41 && LA34_11<=43) ) {
                        alt34=1;
                    }


                }


                switch (alt34) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:408:25: ('and'|'&&')right= lhs_unary
            	    {
            	    if ( (input.LA(1)>=39 && input.LA(1)<=40) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and1405);    throw mse;
            	    }

            	    following.push(FOLLOW_lhs_unary_in_lhs_and1416);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:422:1: lhs_unary returns [PatternDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) ;
    public PatternDescr lhs_unary() throws RecognitionException {   
        PatternDescr d;
        PatternDescr u = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:426:17: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:426:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:426:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            int alt35=5;
            switch ( input.LA(1) ) {
            case 41:
                alt35=1;
                break;
            case 42:
                alt35=2;
                break;
            case 43:
                alt35=3;
                break;
            case ID:
                alt35=4;
                break;
            case 29:
                alt35=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("426:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | \'(\' u= lhs \')\' )", 35, 0, input);

                throw nvae;
            }

            switch (alt35) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:426:25: u= lhs_exist
                    {
                    following.push(FOLLOW_lhs_exist_in_lhs_unary1454);
                    u=lhs_exist();
                    following.pop();


                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:427:25: u= lhs_not
                    {
                    following.push(FOLLOW_lhs_not_in_lhs_unary1462);
                    u=lhs_not();
                    following.pop();


                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:428:25: u= lhs_eval
                    {
                    following.push(FOLLOW_lhs_eval_in_lhs_unary1470);
                    u=lhs_eval();
                    following.pop();


                    }
                    break;
                case 4 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:429:25: u= lhs_column
                    {
                    following.push(FOLLOW_lhs_column_in_lhs_unary1478);
                    u=lhs_column();
                    following.pop();


                    }
                    break;
                case 5 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:430:25: '(' u= lhs ')'
                    {
                    match(input,29,FOLLOW_29_in_lhs_unary1484); 
                    following.push(FOLLOW_lhs_in_lhs_unary1488);
                    u=lhs();
                    following.pop();

                    match(input,30,FOLLOW_30_in_lhs_unary1490); 

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:434:1: lhs_exist returns [PatternDescr d] : loc= 'exists' column= lhs_column ;
    public PatternDescr lhs_exist() throws RecognitionException {   
        PatternDescr d;
        Token loc=null;
        PatternDescr column = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:438:17: (loc= 'exists' column= lhs_column )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:438:17: loc= 'exists' column= lhs_column
            {
            loc=(Token)input.LT(1);
            match(input,41,FOLLOW_41_in_lhs_exist1520); 
            following.push(FOLLOW_lhs_column_in_lhs_exist1524);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:445:1: lhs_not returns [NotDescr d] : loc= 'not' column= lhs_column ;
    public NotDescr lhs_not() throws RecognitionException {   
        NotDescr d;
        Token loc=null;
        PatternDescr column = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:449:17: (loc= 'not' column= lhs_column )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:449:17: loc= 'not' column= lhs_column
            {
            loc=(Token)input.LT(1);
            match(input,42,FOLLOW_42_in_lhs_not1554); 
            following.push(FOLLOW_lhs_column_in_lhs_not1558);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:456:1: lhs_eval returns [PatternDescr d] : 'eval' ;
    public PatternDescr lhs_eval() throws RecognitionException {   
        PatternDescr d;

        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:460:17: ( 'eval' )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:460:17: 'eval'
            {
            match(input,43,FOLLOW_43_in_lhs_eval1584); 
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:463:1: dotted_name returns [String name] : id= ID ( '.' id= ID )* ;
    public String dotted_name() throws RecognitionException {   
        String name;
        Token id=null;


        		name = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:468:17: (id= ID ( '.' id= ID )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:468:17: id= ID ( '.' id= ID )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name1613); 
             name=id.getText(); 
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:468:46: ( '.' id= ID )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);
                if ( LA36_0==15 ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:468:48: '.' id= ID
            	    {
            	    match(input,15,FOLLOW_15_in_dotted_name1619); 
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_dotted_name1623); 
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:472:1: word returns [String word] : (id= ID | 'import' | 'use' | 'rule' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING );
    public String word() throws RecognitionException {   
        String word;
        Token id=null;
        Token str=null;


        		word = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:476:17: (id= ID | 'import' | 'use' | 'rule' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING )
            int alt37=10;
            switch ( input.LA(1) ) {
            case ID:
                alt37=1;
                break;
            case 17:
                alt37=2;
                break;
            case 44:
                alt37=3;
                break;
            case 19:
                alt37=4;
                break;
            case 25:
                alt37=5;
                break;
            case 26:
                alt37=6;
                break;
            case 20:
                alt37=7;
                break;
            case 22:
                alt37=8;
                break;
            case 23:
                alt37=9;
                break;
            case STRING:
                alt37=10;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("472:1: word returns [String word] : (id= ID | \'import\' | \'use\' | \'rule\' | \'salience\' | \'no-loop\' | \'when\' | \'then\' | \'end\' | str= STRING );", 37, 0, input);

                throw nvae;
            }

            switch (alt37) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:476:17: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_word1653); 
                     word=id.getText(); 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:477:17: 'import'
                    {
                    match(input,17,FOLLOW_17_in_word1665); 
                     word="import"; 

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:478:17: 'use'
                    {
                    match(input,44,FOLLOW_44_in_word1674); 
                     word="use"; 

                    }
                    break;
                case 4 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:479:17: 'rule'
                    {
                    match(input,19,FOLLOW_19_in_word1686); 
                     word="rule"; 

                    }
                    break;
                case 5 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:480:17: 'salience'
                    {
                    match(input,25,FOLLOW_25_in_word1697); 
                     word="salience"; 

                    }
                    break;
                case 6 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:481:17: 'no-loop'
                    {
                    match(input,26,FOLLOW_26_in_word1705); 
                     word="no-loop"; 

                    }
                    break;
                case 7 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:482:17: 'when'
                    {
                    match(input,20,FOLLOW_20_in_word1713); 
                     word="when"; 

                    }
                    break;
                case 8 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:483:17: 'then'
                    {
                    match(input,22,FOLLOW_22_in_word1724); 
                     word="then"; 

                    }
                    break;
                case 9 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:484:17: 'end'
                    {
                    match(input,23,FOLLOW_23_in_word1735); 
                     word="end"; 

                    }
                    break;
                case 10 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:485:17: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_word1749); 
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


    protected DFA16 dfa16 = new DFA16();protected DFA25 dfa25 = new DFA25();protected DFA27 dfa27 = new DFA27();protected DFA28 dfa28 = new DFA28();protected DFA29 dfa29 = new DFA29();protected DFA32 dfa32 = new DFA32();
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
                case 29:
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

                case 29:
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

                case 30:
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
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 31:
                    return s3;

                case EOL:
                    return s1;

                case 30:
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

                case 30:
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
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s4 = new DFA.State() {{alt=2;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 21:
                    return s3;

                case EOL:
                    return s2;

                case 27:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                    return s4;

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

                case 21:
                    return s3;

                case 27:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
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

    }class DFA32 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s1 = new DFA.State() {{alt=1;}};
        DFA.State s4 = new DFA.State() {{alt=1;}};
        DFA.State s23 = new DFA.State() {{alt=1;}};
        DFA.State s44 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA32_44 = input.LA(1);
                if ( (LA32_44>=ID && LA32_44<=44) ) {return s23;}
                if ( LA32_44==EOL ) {return s44;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 32, 44, input);

                throw nvae;
            }
        };
        DFA.State s36 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA32_36 = input.LA(1);
                if ( LA32_36==EOL ) {return s44;}
                if ( (LA32_36>=ID && LA32_36<=44) ) {return s23;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 32, 36, input);

                throw nvae;
            }
        };
        DFA.State s32 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
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
                    return s23;

                case EOL:
                    return s32;

                case ID:
                    return s36;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 32, input);

                    throw nvae;        }
            }
        };
        DFA.State s19 = new DFA.State() {
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
                    return s23;

                case ID:
                    return s36;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 19, input);

                    throw nvae;        }
            }
        };
        DFA.State s18 = new DFA.State() {
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
                    return s23;

                case EOL:
                    return s18;

                case 21:
                    return s19;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 18, input);

                    throw nvae;        }
            }
        };
        DFA.State s10 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s18;

                case 21:
                    return s19;

                case 29:
                case 30:
                case 31:
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
                case 28:
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
                    return s23;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 10, input);

                    throw nvae;        }
            }
        };
        DFA.State s6 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
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

                case EOL:
                    return s6;

                case ID:
                    return s10;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 6, input);

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
                        new NoViableAltException("", 32, 2, input);

                    throw nvae;        }
            }
        };
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s1;

                case 31:
                    return s2;

                case 30:
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
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 0, input);

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
    public static final BitSet FOLLOW_27_in_expander_lhs_block659 = new BitSet(new long[]{0x00000E0020000020L});
    public static final BitSet FOLLOW_lhs_in_expander_lhs_block663 = new BitSet(new long[]{0x00001FFFFFFFFFF2L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block690 = new BitSet(new long[]{0x00000E0020000022L});
    public static final BitSet FOLLOW_lhs_or_in_lhs726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column753 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_column762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding794 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding809 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_fact_binding811 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding813 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_fact_in_fact_binding821 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding823 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_28_in_fact_binding835 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_fact_in_fact_binding849 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_ID_in_fact889 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact897 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_fact903 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact905 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_constraints_in_fact911 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact930 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_fact932 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_fact934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraints959 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_constraint_in_constraints963 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints970 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_constraints972 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints974 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_constraint_in_constraints976 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1002 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint1010 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1012 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_constraint1014 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1016 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint1026 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1036 = new BitSet(new long[]{0x0000003F08000000L});
    public static final BitSet FOLLOW_set_in_constraint1043 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1106 = new BitSet(new long[]{0x00001FFFFFFFFFF2L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint1124 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint1144 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint1191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint1201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint1214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_chunk_in_retval_constraint1248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_chunk1289 = new BitSet(new long[]{0x00001FFFFFFFFFF2L});
    public static final BitSet FOLLOW_chunk_in_chunk1293 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_chunk1295 = new BitSet(new long[]{0x00001FFFFFFFFFF2L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1335 = new BitSet(new long[]{0x0000004010000002L});
    public static final BitSet FOLLOW_set_in_lhs_or1345 = new BitSet(new long[]{0x00000E0020000020L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1356 = new BitSet(new long[]{0x0000004010000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1396 = new BitSet(new long[]{0x0000018000000002L});
    public static final BitSet FOLLOW_set_in_lhs_and1405 = new BitSet(new long[]{0x00000E0020000020L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1416 = new BitSet(new long[]{0x0000018000000002L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary1454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary1462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary1470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary1478 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_lhs_unary1484 = new BitSet(new long[]{0x00000E0020000020L});
    public static final BitSet FOLLOW_lhs_in_lhs_unary1488 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_lhs_unary1490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_lhs_exist1520 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist1524 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_lhs_not1554 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not1558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_lhs_eval1584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name1613 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_15_in_dotted_name1619 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_dotted_name1623 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ID_in_word1653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_word1665 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_word1674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_word1686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_word1697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_word1705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_word1713 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_word1724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_word1735 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_word1749 = new BitSet(new long[]{0x0000000000000002L});

}