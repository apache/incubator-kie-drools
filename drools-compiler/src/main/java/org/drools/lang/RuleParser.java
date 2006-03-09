// $ANTLR 3.0ea7 /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g 2006-03-08 21:41:19

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "EOL", "ID", "INT", "STRING", "FLOAT", "MISC", "WS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "\'package\'", "\'.\'", "\';\'", "\'import\'", "\'expander\'", "\'rule\'", "\'when\'", "\':\'", "\'then\'", "\'end\'", "\'options\'", "\'salience\'", "\'no-loop\'", "\'>\'", "\'(\'", "\')\'", "\',\'", "\'==\'", "\'>=\'", "\'<\'", "\'<=\'", "\'!=\'", "\'or\'", "\'||\'", "\'and\'", "\'&&\'", "\'exists\'", "\'not\'", "\'eval\'", "\'use\'"
    };
    public static final int INT=6;
    public static final int WS=10;
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
    		
    		return reparseLhs( text );
    	}
    	
    	private PatternDescr reparseLhs(String text) throws RecognitionException {
    		CharStream charStream = new ANTLRStringStream( text );
    		RuleParserLexer lexer = new RuleParserLexer( charStream );
    		TokenStream tokenStream = new CommonTokenStream( lexer );
    		RuleParser parser = new RuleParser( tokenStream );
    		
    		return parser.lhs();
    	}




    // $ANTLR start opt_eol
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:49:1: opt_eol : ( EOL )* ;
    public void opt_eol() throws RecognitionException {   




        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:50:17: ( ( EOL )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:50:17: ( EOL )*
            {

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:50:17: ( EOL )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);
                if ( LA1_0==EOL ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:50:17: EOL
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

    }
    // $ANTLR end opt_eol



    // $ANTLR start compilation_unit
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:53:1: compilation_unit : prolog (r= rule )* ;
    public void compilation_unit() throws RecognitionException {   



        RuleDescr r = null;


        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:54:17: ( prolog (r= rule )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:54:17: prolog (r= rule )*
            {

            following.push(FOLLOW_prolog_in_compilation_unit53);
            prolog();
            following.pop();


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:55:17: (r= rule )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);
                if ( LA2_0==EOL||LA2_0==19 ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:55:18: r= rule
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

    }
    // $ANTLR end compilation_unit



    // $ANTLR start prolog
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:58:1: prolog : opt_eol (name= package_statement )? opt_eol (name= import_statement )* opt_eol ( use_expander )? opt_eol ;
    public void prolog() throws RecognitionException {   



        String name = null;



        		String packageName = "";
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:62:17: ( opt_eol (name= package_statement )? opt_eol (name= import_statement )* opt_eol ( use_expander )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:62:17: opt_eol (name= package_statement )? opt_eol (name= import_statement )* opt_eol ( use_expander )? opt_eol
            {

            following.push(FOLLOW_opt_eol_in_prolog83);
            opt_eol();
            following.pop();


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:63:17: (name= package_statement )?
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
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:63:19: name= package_statement
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
            		

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:68:17: (name= import_statement )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);
                if ( LA4_0==17 ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:68:19: name= import_statement
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


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:70:17: ( use_expander )?
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
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:70:17: use_expander
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

    }
    // $ANTLR end prolog



    // $ANTLR start package_statement
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:74:1: package_statement returns [String packageName] : 'package' opt_eol id= ID ( '.' id= ID )* ( ';' )? opt_eol ;
    public String package_statement() throws RecognitionException {   

        String packageName;
        Token id=null;


        		packageName = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:78:17: ( 'package' opt_eol id= ID ( '.' id= ID )* ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:78:17: 'package' opt_eol id= ID ( '.' id= ID )* ( ';' )? opt_eol
            {

            match(input,14,FOLLOW_14_in_package_statement151);

            following.push(FOLLOW_opt_eol_in_package_statement153);
            opt_eol();
            following.pop();


            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_package_statement157);

             packageName = id.getText(); 

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:78:73: ( '.' id= ID )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);
                if ( LA6_0==15 ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:78:75: '.' id= ID
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


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:78:127: ( ';' )?
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
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:78:127: ';'
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:81:1: import_statement returns [String importStatement] : 'import' opt_eol name= java_package_or_class ( ';' )? opt_eol ;
    public String import_statement() throws RecognitionException {   

        String importStatement;

        String name = null;



        		importStatement = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:85:17: ( 'import' opt_eol name= java_package_or_class ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:85:17: 'import' opt_eol name= java_package_or_class ( ';' )? opt_eol
            {

            match(input,17,FOLLOW_17_in_import_statement200);

            following.push(FOLLOW_opt_eol_in_import_statement202);
            opt_eol();
            following.pop();


            following.push(FOLLOW_java_package_or_class_in_import_statement206);
            name=java_package_or_class();
            following.pop();


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:85:61: ( ';' )?
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
                    new NoViableAltException("85:61: ( \';\' )?", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:85:61: ';'
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:88:1: use_expander : 'expander' (id= ID )? ( ';' )? opt_eol ;
    public void use_expander() throws RecognitionException {   


        Token id=null;


        		String name=null;
        		String config=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:93:17: ( 'expander' (id= ID )? ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:93:17: 'expander' (id= ID )? ( ';' )? opt_eol
            {

            match(input,18,FOLLOW_18_in_use_expander231);

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:93:28: (id= ID )?
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
                    new NoViableAltException("93:28: (id= ID )?", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:93:29: id= ID
                    {

                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_use_expander236);

                     name = id.getText(); 

                    }
                    break;

            }


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:93:62: ( ';' )?
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
                    new NoViableAltException("93:62: ( \';\' )?", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:93:62: ';'
                    {

                    match(input,16,FOLLOW_16_in_use_expander242);

                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_use_expander245);
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

    }
    // $ANTLR end use_expander



    // $ANTLR start rule
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:100:1: rule returns [RuleDescr rule] : opt_eol loc= 'rule' ruleName= word opt_eol (a= rule_options )? (loc= 'when' ( ':' )? opt_eol (l= lhs )* )? ( 'then' ( ':' )? (any= . )* )? EOL 'end' opt_eol ;
    public RuleDescr rule() throws RecognitionException {   

        RuleDescr rule;
        Token loc=null;
        Token any=null;
        String ruleName = null;

        List a = null;

        PatternDescr l = null;



        		rule = null;
        		String consequence = "";
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:106:17: ( opt_eol loc= 'rule' ruleName= word opt_eol (a= rule_options )? (loc= 'when' ( ':' )? opt_eol (l= lhs )* )? ( 'then' ( ':' )? (any= . )* )? EOL 'end' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:106:17: opt_eol loc= 'rule' ruleName= word opt_eol (a= rule_options )? (loc= 'when' ( ':' )? opt_eol (l= lhs )* )? ( 'then' ( ':' )? (any= . )* )? EOL 'end' opt_eol
            {

            following.push(FOLLOW_opt_eol_in_rule273);
            opt_eol();
            following.pop();


            loc=(Token)input.LT(1);
            match(input,19,FOLLOW_19_in_rule279);

            following.push(FOLLOW_word_in_rule283);
            ruleName=word();
            following.pop();


            following.push(FOLLOW_opt_eol_in_rule285);
            opt_eol();
            following.pop();


             
            			rule = new RuleDescr( ruleName, null ); 
            			rule.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:112:17: (a= rule_options )?
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
                    new NoViableAltException("112:17: (a= rule_options )?", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:112:25: a= rule_options
                    {

                    following.push(FOLLOW_rule_options_in_rule298);
                    a=rule_options();
                    following.pop();



                    				rule.setAttributes( a );
                    			

                    }
                    break;

            }


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:117:17: (loc= 'when' ( ':' )? opt_eol (l= lhs )* )?
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
                    new NoViableAltException("117:17: (loc= \'when\' ( \':\' )? opt_eol (l= lhs )* )?", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:117:25: loc= 'when' ( ':' )? opt_eol (l= lhs )*
                    {

                    loc=(Token)input.LT(1);
                    match(input,20,FOLLOW_20_in_rule316);

                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:117:36: ( ':' )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);
                    if ( LA12_0==21 ) {
                        alt12=1;
                    }
                    else if ( (LA12_0>=EOL && LA12_0<=ID)||LA12_0==22||LA12_0==28||(LA12_0>=40 && LA12_0<=42) ) {
                        alt12=2;
                    }
                    else {

                        NoViableAltException nvae =
                            new NoViableAltException("117:36: ( \':\' )?", 12, 0, input);

                        throw nvae;
                    }
                    switch (alt12) {
                        case 1 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:117:36: ':'
                            {

                            match(input,21,FOLLOW_21_in_rule318);

                            }
                            break;

                    }


                    following.push(FOLLOW_opt_eol_in_rule321);
                    opt_eol();
                    following.pop();


                     
                    				AndDescr lhs = new AndDescr(); rule.setLhs( lhs ); 
                    				lhs.setLocation( loc.getLine(), loc.getCharPositionInLine() );
                    			

                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:122:33: (l= lhs )*
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);
                        if ( LA13_0==ID||LA13_0==28||(LA13_0>=40 && LA13_0<=42) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:122:34: l= lhs
                    	    {

                    	    following.push(FOLLOW_lhs_in_rule335);
                    	    l=lhs();
                    	    following.pop();


                    	     lhs.addDescr( l ); 

                    	    }
                    	    break;

                    	default :
                    	    break loop13;
                        }
                    } while (true);


                    }
                    break;

            }


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:124:17: ( 'then' ( ':' )? (any= . )* )?
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
                    new NoViableAltException("124:17: ( \'then\' ( \':\' )? (any= . )* )?", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:124:25: 'then' ( ':' )? (any= . )*
                    {

                    match(input,22,FOLLOW_22_in_rule351);

                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:124:32: ( ':' )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);
                    if ( LA15_0==21 ) {
                        alt15=1;
                    }
                    else if ( (LA15_0>=EOL && LA15_0<=20)||(LA15_0>=22 && LA15_0<=43) ) {
                        alt15=2;
                    }
                    else {

                        NoViableAltException nvae =
                            new NoViableAltException("124:32: ( \':\' )?", 15, 0, input);

                        throw nvae;
                    }
                    switch (alt15) {
                        case 1 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:124:32: ':'
                            {

                            match(input,21,FOLLOW_21_in_rule353);

                            }
                            break;

                    }


                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:125:25: (any= . )*
                    loop16:
                    do {
                        int alt16=2;
                        alt16 = dfa16.predict(input);
                        switch (alt16) {
                    	case 1 :
                    	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:125:26: any= .
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


            match(input,EOL,FOLLOW_EOL_in_rule388);

            match(input,23,FOLLOW_23_in_rule390);

            following.push(FOLLOW_opt_eol_in_rule392);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:135:1: rule_options returns [List options] : 'options' ( ':' )? opt_eol (a= rule_option opt_eol )* ;
    public List rule_options() throws RecognitionException {   

        List options;

        AttributeDescr a = null;



        		options = new ArrayList();
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:139:17: ( 'options' ( ':' )? opt_eol (a= rule_option opt_eol )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:139:17: 'options' ( ':' )? opt_eol (a= rule_option opt_eol )*
            {

            match(input,24,FOLLOW_24_in_rule_options413);

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:139:27: ( ':' )?
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
                    new NoViableAltException("139:27: ( \':\' )?", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:139:27: ':'
                    {

                    match(input,21,FOLLOW_21_in_rule_options415);

                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_rule_options418);
            opt_eol();
            following.pop();


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:140:25: (a= rule_option opt_eol )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);
                if ( (LA19_0>=25 && LA19_0<=26) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:140:33: a= rule_option opt_eol
            	    {

            	    following.push(FOLLOW_rule_option_in_rule_options427);
            	    a=rule_option();
            	    following.pop();


            	    following.push(FOLLOW_opt_eol_in_rule_options429);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:147:1: rule_option returns [AttributeDescr d] : (a= salience | a= no_loop );
    public AttributeDescr rule_option() throws RecognitionException {   

        AttributeDescr d;

        AttributeDescr a = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:152:25: (a= salience | a= no_loop )
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
                    new NoViableAltException("147:1: rule_option returns [AttributeDescr d] : (a= salience | a= no_loop );", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:152:25: a= salience
                    {

                    following.push(FOLLOW_salience_in_rule_option468);
                    a=salience();
                    following.pop();


                     d = a; 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:153:25: a= no_loop
                    {

                    following.push(FOLLOW_no_loop_in_rule_option478);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:157:1: salience returns [AttributeDescr d ] : loc= 'salience' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr salience() throws RecognitionException {   

        AttributeDescr d;
        Token loc=null;
        Token i=null;


        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:162:17: (loc= 'salience' opt_eol i= INT ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:162:17: loc= 'salience' opt_eol i= INT ( ';' )? opt_eol
            {

            loc=(Token)input.LT(1);
            match(input,25,FOLLOW_25_in_salience511);

            following.push(FOLLOW_opt_eol_in_salience513);
            opt_eol();
            following.pop();


            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience517);

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:162:46: ( ';' )?
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
                    new NoViableAltException("162:46: ( \';\' )?", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:162:46: ';'
                    {

                    match(input,16,FOLLOW_16_in_salience519);

                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_salience522);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:169:1: no_loop returns [AttributeDescr d] : loc= 'no-loop' ( ';' )? opt_eol ;
    public AttributeDescr no_loop() throws RecognitionException {   

        AttributeDescr d;
        Token loc=null;


        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:174:17: (loc= 'no-loop' ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:174:17: loc= 'no-loop' ( ';' )? opt_eol
            {

            loc=(Token)input.LT(1);
            match(input,26,FOLLOW_26_in_no_loop552);

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:174:31: ( ';' )?
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
                    new NoViableAltException("174:31: ( \';\' )?", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:174:31: ';'
                    {

                    match(input,16,FOLLOW_16_in_no_loop554);

                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_no_loop557);
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



    // $ANTLR start root_lhs
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:181:1: root_lhs returns [PatternDescr d] : ({...}?e= expander_lhs | l= lhs );
    public PatternDescr root_lhs() throws RecognitionException {   

        PatternDescr d;

        PatternDescr e = null;

        PatternDescr l = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:185:17: ({...}?e= expander_lhs | l= lhs )
            int alt23=2;
            int LA23_0 = input.LA(1);
            if ( LA23_0==EOL||LA23_0==27 ) {
                alt23=1;
            }
            else if ( LA23_0==ID||LA23_0==28||(LA23_0>=40 && LA23_0<=42) ) {
                alt23=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("181:1: root_lhs returns [PatternDescr d] : ({...}?e= expander_lhs | l= lhs );", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:185:17: {...}?e= expander_lhs
                    {

                    if ( !( expander != null ) ) {
                        throw new FailedPredicateException(input, "root_lhs", " expander != null ");
                    }

                    following.push(FOLLOW_expander_lhs_in_root_lhs587);
                    e=expander_lhs();
                    following.pop();


                     d = e; 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:186:25: l= lhs
                    {

                    following.push(FOLLOW_lhs_in_root_lhs597);
                    l=lhs();
                    following.pop();


                     d = l; 

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
    // $ANTLR end root_lhs



    // $ANTLR start expander_lhs
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:189:1: expander_lhs returns [PatternDescr d] : ( '>' l= lhs | a= EOL );
    public PatternDescr expander_lhs() throws RecognitionException {   

        PatternDescr d;
        Token a=null;
        PatternDescr l = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:194:17: ( '>' l= lhs | a= EOL )
            int alt24=2;
            int LA24_0 = input.LA(1);
            if ( LA24_0==27 ) {
                alt24=1;
            }
            else if ( LA24_0==EOL ) {
                alt24=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("189:1: expander_lhs returns [PatternDescr d] : ( \'>\' l= lhs | a= EOL );", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:194:17: '>' l= lhs
                    {

                    match(input,27,FOLLOW_27_in_expander_lhs624);

                    following.push(FOLLOW_lhs_in_expander_lhs628);
                    l=lhs();
                    following.pop();


                     d = l; 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:195:19: a= EOL
                    {



                    match(input,EOL,FOLLOW_EOL_in_expander_lhs641);

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
    // $ANTLR end expander_lhs



    // $ANTLR start expander_text
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:197:1: expander_text returns [PatternDescr d] : a= EOL ;
    public PatternDescr expander_text() throws RecognitionException {   

        PatternDescr d;
        Token a=null;


        		d = null;
        		String text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:203:17: (a= EOL )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:203:17: a= EOL
            {




            			if ( text == null ) {
            				text = a.getText();
            			} else {
            				text = text + " " + a.getText();
            			}
            		

            match(input,EOL,FOLLOW_EOL_in_expander_text675);


            			d = runExpander( text );
            		

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
    // $ANTLR end expander_text



    // $ANTLR start lhs
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:217:1: lhs returns [PatternDescr d] : l= lhs_or ;
    public PatternDescr lhs() throws RecognitionException {   

        PatternDescr d;

        PatternDescr l = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:221:17: (l= lhs_or )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:221:17: l= lhs_or
            {

            following.push(FOLLOW_lhs_or_in_lhs703);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:225:1: lhs_column returns [ColumnDescr d] : (f= fact_binding | f= fact );
    public ColumnDescr lhs_column() throws RecognitionException {   

        ColumnDescr d;

        ColumnDescr f = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:229:17: (f= fact_binding | f= fact )
            int alt25=2;
            alt25 = dfa25.predict(input);
            switch (alt25) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:229:17: f= fact_binding
                    {

                    following.push(FOLLOW_fact_binding_in_lhs_column730);
                    f=fact_binding();
                    following.pop();


                     d = f; 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:230:17: f= fact
                    {

                    following.push(FOLLOW_fact_in_lhs_column739);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:233:1: fact_binding returns [ColumnDescr d] : id= ID opt_eol ':' opt_eol f= fact opt_eol ;
    public ColumnDescr fact_binding() throws RecognitionException {   

        ColumnDescr d;
        Token id=null;
        ColumnDescr f = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:238:17: (id= ID opt_eol ':' opt_eol f= fact opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:238:17: id= ID opt_eol ':' opt_eol f= fact opt_eol
            {

            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding771);

            following.push(FOLLOW_opt_eol_in_fact_binding773);
            opt_eol();
            following.pop();


            match(input,21,FOLLOW_21_in_fact_binding775);

            following.push(FOLLOW_opt_eol_in_fact_binding777);
            opt_eol();
            following.pop();


            following.push(FOLLOW_fact_in_fact_binding781);
            f=fact();
            following.pop();


             d=f; 

            following.push(FOLLOW_opt_eol_in_fact_binding785);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:245:1: fact returns [ColumnDescr d] : id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol ;
    public ColumnDescr fact() throws RecognitionException {   

        ColumnDescr d;
        Token id=null;
        List c = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:249:17: (id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:249:17: id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol
            {

            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact817);

             
             			d = new ColumnDescr( id.getText() ); 
             			d.setLocation( id.getLine(), id.getCharPositionInLine() );
             		

            following.push(FOLLOW_opt_eol_in_fact825);
            opt_eol();
            following.pop();


            match(input,28,FOLLOW_28_in_fact831);

            following.push(FOLLOW_opt_eol_in_fact833);
            opt_eol();
            following.pop();


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:254:29: (c= constraints )?
            int alt26=2;
            alt26 = dfa26.predict(input);
            switch (alt26) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:254:33: c= constraints
                    {

                    following.push(FOLLOW_constraints_in_fact839);
                    c=constraints();
                    following.pop();



                    		 			for ( Iterator cIter = c.iterator() ; cIter.hasNext() ; ) {
                     						d.addDescr( (PatternDescr) cIter.next() );
                     					}
                     				

                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_fact858);
            opt_eol();
            following.pop();


            match(input,29,FOLLOW_29_in_fact860);

            following.push(FOLLOW_opt_eol_in_fact862);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:265:1: constraints returns [List constraints] : opt_eol c= constraint ( opt_eol ',' opt_eol c= constraint )* opt_eol ;
    public List constraints() throws RecognitionException {   

        List constraints;

        PatternDescr c = null;



        		constraints = new ArrayList();
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:269:17: ( opt_eol c= constraint ( opt_eol ',' opt_eol c= constraint )* opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:269:17: opt_eol c= constraint ( opt_eol ',' opt_eol c= constraint )* opt_eol
            {

            following.push(FOLLOW_opt_eol_in_constraints888);
            opt_eol();
            following.pop();


            following.push(FOLLOW_constraint_in_constraints894);
            c=constraint();
            following.pop();


             constraints.add( c ); 

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:271:17: ( opt_eol ',' opt_eol c= constraint )*
            loop27:
            do {
                int alt27=2;
                alt27 = dfa27.predict(input);
                switch (alt27) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:271:19: opt_eol ',' opt_eol c= constraint
            	    {

            	    following.push(FOLLOW_opt_eol_in_constraints903);
            	    opt_eol();
            	    following.pop();


            	    match(input,30,FOLLOW_30_in_constraints905);

            	    following.push(FOLLOW_opt_eol_in_constraints907);
            	    opt_eol();
            	    following.pop();


            	    following.push(FOLLOW_constraint_in_constraints911);
            	    c=constraint();
            	    following.pop();


            	     constraints.add( c ); 

            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);


            following.push(FOLLOW_opt_eol_in_constraints920);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:275:1: constraint returns [PatternDescr d] : opt_eol f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!=') opt_eol (lc= literal_constraint | rvc= retval_constraint ) opt_eol ;
    public PatternDescr constraint() throws RecognitionException {   

        PatternDescr d;
        Token f=null;
        Token op=null;
        String lc = null;

        String rvc = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:279:17: ( opt_eol f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!=') opt_eol (lc= literal_constraint | rvc= retval_constraint ) opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:279:17: opt_eol f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!=') opt_eol (lc= literal_constraint | rvc= retval_constraint ) opt_eol
            {

            following.push(FOLLOW_opt_eol_in_constraint942);
            opt_eol();
            following.pop();


            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint948);

            following.push(FOLLOW_opt_eol_in_constraint950);
            opt_eol();
            following.pop();


            op=(Token)input.LT(1);
            if ( input.LA(1)==27||(input.LA(1)>=31 && input.LA(1)<=35) ) {
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint957);    throw mse;
            }


            following.push(FOLLOW_opt_eol_in_constraint1011);
            opt_eol();
            following.pop();


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:288:41: (lc= literal_constraint | rvc= retval_constraint )
            int alt28=2;
            switch ( input.LA(1) ) {
            case STRING:
                alt28=1;
                break;
            case INT:
                alt28=1;
                break;
            case FLOAT:
                alt28=1;
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
                alt28=2;
                break;
            default:

                NoViableAltException nvae =
                    new NoViableAltException("288:41: (lc= literal_constraint | rvc= retval_constraint )", 28, 0, input);

                throw nvae;
            }

            switch (alt28) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:288:49: lc= literal_constraint
                    {

                    following.push(FOLLOW_literal_constraint_in_constraint1029);
                    lc=literal_constraint();
                    following.pop();


                     
                    							d = new LiteralDescr( f.getText(), op.getText(), lc ); 
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    						

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:293:49: rvc= retval_constraint
                    {

                    following.push(FOLLOW_retval_constraint_in_constraint1049);
                    rvc=retval_constraint();
                    following.pop();


                     
                    							d = new ReturnValueDescr( f.getText(), op.getText(), rvc ); 
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    						

                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_constraint1070);
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
    // $ANTLR end constraint



    // $ANTLR start literal_constraint
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:302:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT ) ;
    public String literal_constraint() throws RecognitionException {   

        String text;
        Token t=null;


        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:306:17: ( (t= STRING | t= INT | t= FLOAT ) )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:306:17: (t= STRING | t= INT | t= FLOAT )
            {

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:306:17: (t= STRING | t= INT | t= FLOAT )
            int alt29=3;
            switch ( input.LA(1) ) {
            case STRING:
                alt29=1;
                break;
            case INT:
                alt29=2;
                break;
            case FLOAT:
                alt29=3;
                break;
            default:

                NoViableAltException nvae =
                    new NoViableAltException("306:17: (t= STRING | t= INT | t= FLOAT )", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:306:25: t= STRING
                    {

                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint1096);

                     text = t.getText(); text=text.substring( 1, text.length() - 1 ); 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:307:25: t= INT
                    {

                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint1106);

                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:308:25: t= FLOAT
                    {

                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint1119);

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:312:1: retval_constraint returns [String text] : c= chunk ;
    public String retval_constraint() throws RecognitionException {   

        String text;

        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:317:17: (c= chunk )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:317:17: c= chunk
            {

            following.push(FOLLOW_chunk_in_retval_constraint1153);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:320:1: chunk returns [String text] : ( (any= . ) | ( '(' c= chunk ')' ) )* ;
    public String chunk() throws RecognitionException {   

        String text;
        Token any=null;
        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:324:17: ( ( (any= . ) | ( '(' c= chunk ')' ) )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:324:17: ( (any= . ) | ( '(' c= chunk ')' ) )*
            {

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:324:17: ( (any= . ) | ( '(' c= chunk ')' ) )*
            loop30:
            do {
                int alt30=3;
                alt30 = dfa30.predict(input);
                switch (alt30) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:324:25: (any= . )
            	    {

            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:324:25: (any= . )
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:324:27: any= .
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
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:331:25: ( '(' c= chunk ')' )
            	    {

            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:331:25: ( '(' c= chunk ')' )
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:331:27: '(' c= chunk ')'
            	    {

            	    match(input,28,FOLLOW_28_in_chunk1194);

            	    following.push(FOLLOW_chunk_in_chunk1198);
            	    c=chunk();
            	    following.pop();


            	    match(input,29,FOLLOW_29_in_chunk1200);


            	    							if ( text == null ) {
            	    								text = "( " + c + " )";
            	    							} else {
            	    								text = text + " ( " + c + " )";
            	    							}
            	    						

            	    }


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

        return text;

    }
    // $ANTLR end chunk



    // $ANTLR start field_binding
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:342:1: field_binding : /* epsilon */ ;
    public void field_binding() throws RecognitionException {   




        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:344:9: ( /* epsilon */ )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:344:9: /* epsilon */
        {



        }



    }
    // $ANTLR end field_binding



    // $ANTLR start lhs_or
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:346:1: lhs_or returns [PatternDescr d] : left= lhs_and ( ('or'|'||')right= lhs_and )* ;
    public PatternDescr lhs_or() throws RecognitionException {   

        PatternDescr d;

        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:351:17: (left= lhs_and ( ('or'|'||')right= lhs_and )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:351:17: left= lhs_and ( ('or'|'||')right= lhs_and )*
            {

             OrDescr or = null; 

            following.push(FOLLOW_lhs_and_in_lhs_or1252);
            left=lhs_and();
            following.pop();


            d = left; 

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:353:17: ( ('or'|'||')right= lhs_and )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);
                if ( (LA31_0>=36 && LA31_0<=37) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:353:25: ('or'|'||')right= lhs_and
            	    {

            	    if ( (input.LA(1)>=36 && input.LA(1)<=37) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or1262);    throw mse;
            	    }


            	    following.push(FOLLOW_lhs_and_in_lhs_or1273);
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

        return d;

    }
    // $ANTLR end lhs_or



    // $ANTLR start lhs_and
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:367:1: lhs_and returns [PatternDescr d] : left= lhs_unary ( ('and'|'&&')right= lhs_unary )* ;
    public PatternDescr lhs_and() throws RecognitionException {   

        PatternDescr d;

        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:372:17: (left= lhs_unary ( ('and'|'&&')right= lhs_unary )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:372:17: left= lhs_unary ( ('and'|'&&')right= lhs_unary )*
            {

             AndDescr and = null; 

            following.push(FOLLOW_lhs_unary_in_lhs_and1313);
            left=lhs_unary();
            following.pop();


             d = left; 

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:374:17: ( ('and'|'&&')right= lhs_unary )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);
                if ( (LA32_0>=38 && LA32_0<=39) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:374:25: ('and'|'&&')right= lhs_unary
            	    {

            	    if ( (input.LA(1)>=38 && input.LA(1)<=39) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and1322);    throw mse;
            	    }


            	    following.push(FOLLOW_lhs_unary_in_lhs_and1333);
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
    // $ANTLR end lhs_and



    // $ANTLR start lhs_unary
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:388:1: lhs_unary returns [PatternDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) ;
    public PatternDescr lhs_unary() throws RecognitionException {   

        PatternDescr d;

        PatternDescr u = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:392:17: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:392:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            {

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:392:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            int alt33=5;
            switch ( input.LA(1) ) {
            case 40:
                alt33=1;
                break;
            case 41:
                alt33=2;
                break;
            case 42:
                alt33=3;
                break;
            case ID:
                alt33=4;
                break;
            case 28:
                alt33=5;
                break;
            default:

                NoViableAltException nvae =
                    new NoViableAltException("392:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | \'(\' u= lhs \')\' )", 33, 0, input);

                throw nvae;
            }

            switch (alt33) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:392:25: u= lhs_exist
                    {

                    following.push(FOLLOW_lhs_exist_in_lhs_unary1371);
                    u=lhs_exist();
                    following.pop();


                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:393:25: u= lhs_not
                    {

                    following.push(FOLLOW_lhs_not_in_lhs_unary1379);
                    u=lhs_not();
                    following.pop();


                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:394:25: u= lhs_eval
                    {

                    following.push(FOLLOW_lhs_eval_in_lhs_unary1387);
                    u=lhs_eval();
                    following.pop();


                    }
                    break;
                case 4 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:395:25: u= lhs_column
                    {

                    following.push(FOLLOW_lhs_column_in_lhs_unary1395);
                    u=lhs_column();
                    following.pop();


                    }
                    break;
                case 5 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:396:25: '(' u= lhs ')'
                    {

                    match(input,28,FOLLOW_28_in_lhs_unary1401);

                    following.push(FOLLOW_lhs_in_lhs_unary1405);
                    u=lhs();
                    following.pop();


                    match(input,29,FOLLOW_29_in_lhs_unary1407);

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:400:1: lhs_exist returns [PatternDescr d] : loc= 'exists' column= lhs_column ;
    public PatternDescr lhs_exist() throws RecognitionException {   

        PatternDescr d;
        Token loc=null;
        ColumnDescr column = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:404:17: (loc= 'exists' column= lhs_column )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:404:17: loc= 'exists' column= lhs_column
            {

            loc=(Token)input.LT(1);
            match(input,40,FOLLOW_40_in_lhs_exist1437);

            following.push(FOLLOW_lhs_column_in_lhs_exist1441);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:411:1: lhs_not returns [NotDescr d] : loc= 'not' column= lhs_column ;
    public NotDescr lhs_not() throws RecognitionException {   

        NotDescr d;
        Token loc=null;
        ColumnDescr column = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:415:17: (loc= 'not' column= lhs_column )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:415:17: loc= 'not' column= lhs_column
            {

            loc=(Token)input.LT(1);
            match(input,41,FOLLOW_41_in_lhs_not1471);

            following.push(FOLLOW_lhs_column_in_lhs_not1475);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:422:1: lhs_eval returns [PatternDescr d] : 'eval' ;
    public PatternDescr lhs_eval() throws RecognitionException {   

        PatternDescr d;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:426:17: ( 'eval' )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:426:17: 'eval'
            {

            match(input,42,FOLLOW_42_in_lhs_eval1501);

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



    // $ANTLR start java_package_or_class
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:429:1: java_package_or_class returns [String name] : id= ID ( '.' id= ID )* ;
    public String java_package_or_class() throws RecognitionException {   

        String name;
        Token id=null;


        		name = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:434:17: (id= ID ( '.' id= ID )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:434:17: id= ID ( '.' id= ID )*
            {

            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_java_package_or_class1530);

             name=id.getText(); 

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:434:46: ( '.' id= ID )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);
                if ( LA34_0==15 ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:434:48: '.' id= ID
            	    {

            	    match(input,15,FOLLOW_15_in_java_package_or_class1536);

            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_java_package_or_class1540);

            	     name = name + "." + id.getText(); 

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

        return name;

    }
    // $ANTLR end java_package_or_class



    // $ANTLR start word
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:438:1: word returns [String word] : (id= ID | 'import' | 'use' | 'rule' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING );
    public String word() throws RecognitionException {   

        String word;
        Token id=null;
        Token str=null;


        		word = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:442:17: (id= ID | 'import' | 'use' | 'rule' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING )
            int alt35=10;
            switch ( input.LA(1) ) {
            case ID:
                alt35=1;
                break;
            case 17:
                alt35=2;
                break;
            case 43:
                alt35=3;
                break;
            case 19:
                alt35=4;
                break;
            case 25:
                alt35=5;
                break;
            case 26:
                alt35=6;
                break;
            case 20:
                alt35=7;
                break;
            case 22:
                alt35=8;
                break;
            case 23:
                alt35=9;
                break;
            case STRING:
                alt35=10;
                break;
            default:

                NoViableAltException nvae =
                    new NoViableAltException("438:1: word returns [String word] : (id= ID | \'import\' | \'use\' | \'rule\' | \'salience\' | \'no-loop\' | \'when\' | \'then\' | \'end\' | str= STRING );", 35, 0, input);

                throw nvae;
            }

            switch (alt35) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:442:17: id= ID
                    {

                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_word1570);

                     word=id.getText(); 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:443:17: 'import'
                    {

                    match(input,17,FOLLOW_17_in_word1582);

                     word="import"; 

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:444:17: 'use'
                    {

                    match(input,43,FOLLOW_43_in_word1591);

                     word="use"; 

                    }
                    break;
                case 4 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:445:17: 'rule'
                    {

                    match(input,19,FOLLOW_19_in_word1603);

                     word="rule"; 

                    }
                    break;
                case 5 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:446:17: 'salience'
                    {

                    match(input,25,FOLLOW_25_in_word1614);

                     word="salience"; 

                    }
                    break;
                case 6 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:447:17: 'no-loop'
                    {

                    match(input,26,FOLLOW_26_in_word1622);

                     word="no-loop"; 

                    }
                    break;
                case 7 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:448:17: 'when'
                    {

                    match(input,20,FOLLOW_20_in_word1630);

                     word="when"; 

                    }
                    break;
                case 8 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:449:17: 'then'
                    {

                    match(input,22,FOLLOW_22_in_word1641);

                     word="then"; 

                    }
                    break;
                case 9 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:450:17: 'end'
                    {

                    match(input,23,FOLLOW_23_in_word1652);

                     word="end"; 

                    }
                    break;
                case 10 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:451:17: str= STRING
                    {

                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_word1666);

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


    protected DFA16 dfa16 = new DFA16();protected DFA25 dfa25 = new DFA25();protected DFA26 dfa26 = new DFA26();protected DFA27 dfa27 = new DFA27();protected DFA30 dfa30 = new DFA30();
    class DFA16 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s2 = new DFA.State() {{alt=1;}};
        DFA.State s7 = new DFA.State() {{alt=2;}};
        DFA.State s16 = new DFA.State() {{alt=1;}};
        DFA.State s17 = new DFA.State() {{alt=1;}};
        DFA.State s18 = new DFA.State() {{alt=1;}};
        DFA.State s19 = new DFA.State() {{alt=1;}};
        DFA.State s20 = new DFA.State() {{alt=1;}};
        DFA.State s21 = new DFA.State() {{alt=1;}};
        DFA.State s22 = new DFA.State() {{alt=1;}};
        DFA.State s23 = new DFA.State() {{alt=1;}};
        DFA.State s24 = new DFA.State() {{alt=1;}};
        DFA.State s25 = new DFA.State() {{alt=1;}};
        DFA.State s8 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
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
                    return s2;

                case ID:
                    return s16;

                case 17:
                    return s17;

                case 43:
                    return s18;

                case 19:
                    return s19;

                case 25:
                    return s20;

                case 26:
                    return s21;

                case 20:
                    return s22;

                case 22:
                    return s23;

                case 23:
                    return s24;

                case STRING:
                    return s25;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 8, input);

                    throw nvae;        }
            }
        };
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
                    return s2;

                case -1:
                    return s7;

                case EOL:
                    return s6;

                case 19:
                    return s8;

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
                if ( (LA16_1>=EOL && LA16_1<=22)||(LA16_1>=24 && LA16_1<=43) ) {return s2;}


                NoViableAltException nvae =
        	    new NoViableAltException("", 16, 1, input);

                throw nvae;
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_0 = input.LA(1);
                if ( LA16_0==EOL ) {return s1;}
                if ( (LA16_0>=ID && LA16_0<=43) ) {return s2;}


                NoViableAltException nvae =
        	    new NoViableAltException("", 16, 0, input);

                throw nvae;
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
                case 21:
                    return s4;

                case EOL:
                    return s2;

                case 28:
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

                case 28:
                    return s3;

                case 21:
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
        DFA.State s3 = new DFA.State() {{alt=2;}};
        DFA.State s2 = new DFA.State() {{alt=1;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 29:
                    return s3;

                case EOL:
                    return s1;

                case ID:
                    return s2;

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
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 29:
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

    }class DFA30 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s1 = new DFA.State() {{alt=1;}};
        DFA.State s22 = new DFA.State() {{alt=1;}};
        DFA.State s18 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA30_18 = input.LA(1);
                if ( (LA30_18>=ID && LA30_18<=43) ) {return s22;}
                if ( LA30_18==EOL ) {return s18;}


                NoViableAltException nvae =
        	    new NoViableAltException("", 30, 18, input);

                throw nvae;
            }
        };
        DFA.State s4 = new DFA.State() {{alt=1;}};
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
                case 21:
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
                    return s22;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 10, input);

                    throw nvae;        }
            }
        };
        DFA.State s6 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s6;

                case ID:
                    return s10;

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
                    return s4;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 6, input);

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
                    return s4;

                case ID:
                    return s10;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 2, input);

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
                    return s4;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 0, input);

                    throw nvae;        }
            }
        };

    }


    public static final BitSet FOLLOW_EOL_in_opt_eol40 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit53 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_rule_in_compilation_unit61 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_prolog83 = new BitSet(new long[]{16402L});
    public static final BitSet FOLLOW_package_statement_in_prolog91 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_prolog100 = new BitSet(new long[]{131090L});
    public static final BitSet FOLLOW_import_statement_in_prolog112 = new BitSet(new long[]{131090L});
    public static final BitSet FOLLOW_opt_eol_in_prolog121 = new BitSet(new long[]{262162L});
    public static final BitSet FOLLOW_use_expander_in_prolog125 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_prolog130 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_14_in_package_statement151 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_package_statement153 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_ID_in_package_statement157 = new BitSet(new long[]{98322L});
    public static final BitSet FOLLOW_15_in_package_statement163 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_ID_in_package_statement167 = new BitSet(new long[]{98322L});
    public static final BitSet FOLLOW_16_in_package_statement174 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_package_statement177 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_17_in_import_statement200 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_import_statement202 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_java_package_or_class_in_import_statement206 = new BitSet(new long[]{65554L});
    public static final BitSet FOLLOW_16_in_import_statement208 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_import_statement213 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_18_in_use_expander231 = new BitSet(new long[]{65586L});
    public static final BitSet FOLLOW_ID_in_use_expander236 = new BitSet(new long[]{65554L});
    public static final BitSet FOLLOW_16_in_use_expander242 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_use_expander245 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_opt_eol_in_rule273 = new BitSet(new long[]{524288L});
    public static final BitSet FOLLOW_19_in_rule279 = new BitSet(new long[]{8796207972512L});
    public static final BitSet FOLLOW_word_in_rule283 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_rule285 = new BitSet(new long[]{22020112L});
    public static final BitSet FOLLOW_rule_options_in_rule298 = new BitSet(new long[]{5242896L});
    public static final BitSet FOLLOW_20_in_rule316 = new BitSet(new long[]{2097170L});
    public static final BitSet FOLLOW_21_in_rule318 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_rule321 = new BitSet(new long[]{7696854024240L});
    public static final BitSet FOLLOW_lhs_in_rule335 = new BitSet(new long[]{7696854024240L});
    public static final BitSet FOLLOW_22_in_rule351 = new BitSet(new long[]{17592186044400L});
    public static final BitSet FOLLOW_21_in_rule353 = new BitSet(new long[]{17592186044400L});
    public static final BitSet FOLLOW_EOL_in_rule388 = new BitSet(new long[]{8388608L});
    public static final BitSet FOLLOW_23_in_rule390 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_rule392 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_24_in_rule_options413 = new BitSet(new long[]{2097170L});
    public static final BitSet FOLLOW_21_in_rule_options415 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_rule_options418 = new BitSet(new long[]{100663298L});
    public static final BitSet FOLLOW_rule_option_in_rule_options427 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_rule_options429 = new BitSet(new long[]{100663298L});
    public static final BitSet FOLLOW_salience_in_rule_option468 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_no_loop_in_rule_option478 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_25_in_salience511 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_salience513 = new BitSet(new long[]{64L});
    public static final BitSet FOLLOW_INT_in_salience517 = new BitSet(new long[]{65554L});
    public static final BitSet FOLLOW_16_in_salience519 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_salience522 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_26_in_no_loop552 = new BitSet(new long[]{65554L});
    public static final BitSet FOLLOW_16_in_no_loop554 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop557 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_expander_lhs_in_root_lhs587 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_lhs_in_root_lhs597 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_27_in_expander_lhs624 = new BitSet(new long[]{7696849829920L});
    public static final BitSet FOLLOW_lhs_in_expander_lhs628 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_EOL_in_expander_lhs641 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_EOL_in_expander_text675 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_lhs_or_in_lhs703 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column730 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_fact_in_lhs_column739 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_ID_in_fact_binding771 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding773 = new BitSet(new long[]{2097152L});
    public static final BitSet FOLLOW_21_in_fact_binding775 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding777 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_fact_in_fact_binding781 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding785 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_ID_in_fact817 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact825 = new BitSet(new long[]{268435456L});
    public static final BitSet FOLLOW_28_in_fact831 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact833 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_constraints_in_fact839 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact858 = new BitSet(new long[]{536870912L});
    public static final BitSet FOLLOW_29_in_fact860 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact862 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_opt_eol_in_constraints888 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_constraint_in_constraints894 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_constraints903 = new BitSet(new long[]{1073741824L});
    public static final BitSet FOLLOW_30_in_constraints905 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_constraints907 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_constraint_in_constraints911 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_constraints920 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_opt_eol_in_constraint942 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_ID_in_constraint948 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_constraint950 = new BitSet(new long[]{66706210816L});
    public static final BitSet FOLLOW_set_in_constraint957 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1011 = new BitSet(new long[]{17592186044402L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint1029 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint1049 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1070 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint1096 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_INT_in_literal_constraint1106 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint1119 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_chunk_in_retval_constraint1153 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_28_in_chunk1194 = new BitSet(new long[]{17592186044402L});
    public static final BitSet FOLLOW_chunk_in_chunk1198 = new BitSet(new long[]{536870912L});
    public static final BitSet FOLLOW_29_in_chunk1200 = new BitSet(new long[]{17592186044402L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1252 = new BitSet(new long[]{206158430210L});
    public static final BitSet FOLLOW_set_in_lhs_or1262 = new BitSet(new long[]{7696849829920L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1273 = new BitSet(new long[]{206158430210L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1313 = new BitSet(new long[]{824633720834L});
    public static final BitSet FOLLOW_set_in_lhs_and1322 = new BitSet(new long[]{7696849829920L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1333 = new BitSet(new long[]{824633720834L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary1371 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary1379 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary1387 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary1395 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_28_in_lhs_unary1401 = new BitSet(new long[]{7696849829920L});
    public static final BitSet FOLLOW_lhs_in_lhs_unary1405 = new BitSet(new long[]{536870912L});
    public static final BitSet FOLLOW_29_in_lhs_unary1407 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_40_in_lhs_exist1437 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist1441 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_41_in_lhs_not1471 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not1475 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_42_in_lhs_eval1501 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_ID_in_java_package_or_class1530 = new BitSet(new long[]{32770L});
    public static final BitSet FOLLOW_15_in_java_package_or_class1536 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_ID_in_java_package_or_class1540 = new BitSet(new long[]{32770L});
    public static final BitSet FOLLOW_ID_in_word1570 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_17_in_word1582 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_43_in_word1591 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_19_in_word1603 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_25_in_word1614 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_26_in_word1622 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_20_in_word1630 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_22_in_word1641 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_23_in_word1652 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_STRING_in_word1666 = new BitSet(new long[]{2L});

}