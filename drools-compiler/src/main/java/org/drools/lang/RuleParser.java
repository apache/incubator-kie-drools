// $ANTLR 3.0ea7 /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g 2006-03-09 00:28:52

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "EOL", "ID", "INT", "STRING", "FLOAT", "MISC", "WS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "\'package\'", "\'.\'", "\';\'", "\'import\'", "\'expander\'", "\'rule\'", "\'when\'", "\':\'", "\'>\'", "\'then\'", "\'end\'", "\'options\'", "\'salience\'", "\'no-loop\'", "\'(\'", "\')\'", "\',\'", "\'==\'", "\'>=\'", "\'<\'", "\'<=\'", "\'!=\'", "\'or\'", "\'||\'", "\'and\'", "\'&&\'", "\'exists\'", "\'not\'", "\'eval\'", "\'use\'"
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:81:1: import_statement returns [String importStatement] : 'import' opt_eol name= dotted_name ( ';' )? opt_eol ;
    public String import_statement() throws RecognitionException {   

        String importStatement;

        String name = null;



        		importStatement = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:85:17: ( 'import' opt_eol name= dotted_name ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:85:17: 'import' opt_eol name= dotted_name ( ';' )? opt_eol
            {

            match(input,17,FOLLOW_17_in_import_statement200);

            following.push(FOLLOW_opt_eol_in_import_statement202);
            opt_eol();
            following.pop();


            following.push(FOLLOW_dotted_name_in_import_statement206);
            name=dotted_name();
            following.pop();


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:85:51: ( ';' )?
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
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:85:51: ';'
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:88:1: use_expander : 'expander' (name= dotted_name )? ( ';' )? opt_eol ;
    public void use_expander() throws RecognitionException {   



        String name = null;



        		String config=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:92:17: ( 'expander' (name= dotted_name )? ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:92:17: 'expander' (name= dotted_name )? ( ';' )? opt_eol
            {

            match(input,18,FOLLOW_18_in_use_expander231);

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:92:28: (name= dotted_name )?
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
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:92:29: name= dotted_name
                    {

                    following.push(FOLLOW_dotted_name_in_use_expander236);
                    name=dotted_name();
                    following.pop();


                    }
                    break;

            }


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:92:48: ( ';' )?
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
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:92:48: ';'
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

    }
    // $ANTLR end use_expander



    // $ANTLR start rule
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:99:1: rule returns [RuleDescr rule] : opt_eol loc= 'rule' ruleName= word opt_eol (a= rule_options )? (loc= 'when' ( ':' )? opt_eol ({...}? ( '>' l= lhs | ( options {greedy=false; } : any= . )+ EOL ) | l= lhs )* )? ( 'then' ( ':' )? (any= . )* )? EOL 'end' opt_eol ;
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
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:105:17: ( opt_eol loc= 'rule' ruleName= word opt_eol (a= rule_options )? (loc= 'when' ( ':' )? opt_eol ({...}? ( '>' l= lhs | ( options {greedy=false; } : any= . )+ EOL ) | l= lhs )* )? ( 'then' ( ':' )? (any= . )* )? EOL 'end' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:105:17: opt_eol loc= 'rule' ruleName= word opt_eol (a= rule_options )? (loc= 'when' ( ':' )? opt_eol ({...}? ( '>' l= lhs | ( options {greedy=false; } : any= . )+ EOL ) | l= lhs )* )? ( 'then' ( ':' )? (any= . )* )? EOL 'end' opt_eol
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
            		

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:111:17: (a= rule_options )?
            int alt11=2;
            int LA11_0 = input.LA(1);
            if ( LA11_0==25 ) {
                alt11=1;
            }
            else if ( LA11_0==EOL||LA11_0==20||LA11_0==23 ) {
                alt11=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("111:17: (a= rule_options )?", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:111:25: a= rule_options
                    {

                    following.push(FOLLOW_rule_options_in_rule296);
                    a=rule_options();
                    following.pop();



                    				rule.setAttributes( a );
                    			

                    }
                    break;

            }


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:116:17: (loc= 'when' ( ':' )? opt_eol ({...}? ( '>' l= lhs | ( options {greedy=false; } : any= . )+ EOL ) | l= lhs )* )?
            int alt16=2;
            int LA16_0 = input.LA(1);
            if ( LA16_0==20 ) {
                alt16=1;
            }
            else if ( LA16_0==EOL||LA16_0==23 ) {
                alt16=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("116:17: (loc= \'when\' ( \':\' )? opt_eol ({...}? ( \'>\' l= lhs | ( options {greedy=false; } : any= . )+ EOL ) | l= lhs )* )?", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:116:25: loc= 'when' ( ':' )? opt_eol ({...}? ( '>' l= lhs | ( options {greedy=false; } : any= . )+ EOL ) | l= lhs )*
                    {

                    loc=(Token)input.LT(1);
                    match(input,20,FOLLOW_20_in_rule314);

                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:116:36: ( ':' )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);
                    if ( LA12_0==21 ) {
                        int LA12_1 = input.LA(2);
                        if ( !( expander != null ) ) {
                            alt12=1;
                        }
                        else if (  expander != null  ) {
                            alt12=2;
                        }
                        else {

                            NoViableAltException nvae =
                                new NoViableAltException("116:36: ( \':\' )?", 12, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA12_0>=EOL && LA12_0<=20)||(LA12_0>=22 && LA12_0<=43) ) {
                        alt12=2;
                    }
                    else {

                        NoViableAltException nvae =
                            new NoViableAltException("116:36: ( \':\' )?", 12, 0, input);

                        throw nvae;
                    }
                    switch (alt12) {
                        case 1 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:116:36: ':'
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
                    			

                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:121:25: ({...}? ( '>' l= lhs | ( options {greedy=false; } : any= . )+ EOL ) | l= lhs )*
                    loop15:
                    do {
                        int alt15=3;
                        switch ( input.LA(1) ) {
                        case 23:
                            int LA15_1 = input.LA(2);
                            if (  expander != null  ) {
                                alt15=1;
                            }


                            break;
                        case EOL:
                            int LA15_2 = input.LA(2);
                            if (  expander != null  ) {
                                alt15=1;
                            }


                            break;
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
                        case 43:
                            alt15=1;
                            break;
                        case 40:
                            int LA15_4 = input.LA(2);
                            if (  expander != null  ) {
                                alt15=1;
                            }
                            else if ( true ) {
                                alt15=2;
                            }


                            break;
                        case 41:
                            int LA15_5 = input.LA(2);
                            if (  expander != null  ) {
                                alt15=1;
                            }
                            else if ( true ) {
                                alt15=2;
                            }


                            break;
                        case 42:
                            int LA15_6 = input.LA(2);
                            if (  expander != null  ) {
                                alt15=1;
                            }
                            else if ( true ) {
                                alt15=2;
                            }


                            break;
                        case ID:
                            int LA15_7 = input.LA(2);
                            if (  expander != null  ) {
                                alt15=1;
                            }
                            else if ( true ) {
                                alt15=2;
                            }


                            break;
                        case 28:
                            int LA15_8 = input.LA(2);
                            if (  expander != null  ) {
                                alt15=1;
                            }
                            else if ( true ) {
                                alt15=2;
                            }


                            break;

                        }

                        switch (alt15) {
                    	case 1 :
                    	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:122:41: {...}? ( '>' l= lhs | ( options {greedy=false; } : any= . )+ EOL )
                    	    {

                    	    if ( !( expander != null ) ) {
                    	        throw new FailedPredicateException(input, "rule", " expander != null ");
                    	    }

                    	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:123:41: ( '>' l= lhs | ( options {greedy=false; } : any= . )+ EOL )
                    	    int alt14=2;
                    	    int LA14_0 = input.LA(1);
                    	    if ( LA14_0==22 ) {
                    	        alt14=1;
                    	    }
                    	    else if ( (LA14_0>=EOL && LA14_0<=21)||(LA14_0>=23 && LA14_0<=43) ) {
                    	        alt14=2;
                    	    }
                    	    else {

                    	        NoViableAltException nvae =
                    	            new NoViableAltException("123:41: ( \'>\' l= lhs | ( options {greedy=false; } : any= . )+ EOL )", 14, 0, input);

                    	        throw nvae;
                    	    }
                    	    switch (alt14) {
                    	        case 1 :
                    	            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:124:49: '>' l= lhs
                    	            {

                    	            match(input,22,FOLLOW_22_in_rule351);

                    	            following.push(FOLLOW_lhs_in_rule355);
                    	            l=lhs();
                    	            following.pop();


                    	             lhs.addDescr( l ); 

                    	            }
                    	            break;
                    	        case 2 :
                    	            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:126:49: ( options {greedy=false; } : any= . )+ EOL
                    	            {


                    	            							String text = null;
                    	            						

                    	            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:129:49: ( options {greedy=false; } : any= . )+
                    	            int cnt13=0;
                    	            loop13:
                    	            do {
                    	                int alt13=2;
                    	                int LA13_0 = input.LA(1);
                    	                if ( LA13_0==EOL ) {
                    	                    alt13=2;
                    	                }
                    	                else if ( (LA13_0>=ID && LA13_0<=43) ) {
                    	                    alt13=1;
                    	                }


                    	                switch (alt13) {
                    	            	case 1 :
                    	            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:129:81: any= .
                    	            	    {

                    	            	    any=(Token)input.LT(1);
                    	            	    matchAny(input);


                    	            	    								System.err.println( "[[" + any.getText() + "]]" );
                    	            	    								if ( text == null ) {
                    	            	    									text = any.getText();
                    	            	    								} else {
                    	            	    									text = text + " " + any.getText();
                    	            	    								}
                    	            	    							

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


                    	            match(input,EOL,FOLLOW_EOL_in_rule418);

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;
                    	case 2 :
                    	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:140:41: l= lhs
                    	    {

                    	    following.push(FOLLOW_lhs_in_rule435);
                    	    l=lhs();
                    	    following.pop();


                    	     lhs.addDescr( l ); 

                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);


                    }
                    break;

            }


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:151:17: ( 'then' ( ':' )? (any= . )* )?
            int alt19=2;
            int LA19_0 = input.LA(1);
            if ( LA19_0==23 ) {
                alt19=1;
            }
            else if ( LA19_0==EOL ) {
                alt19=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("151:17: ( \'then\' ( \':\' )? (any= . )* )?", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:151:25: 'then' ( ':' )? (any= . )*
                    {

                    match(input,23,FOLLOW_23_in_rule459);

                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:151:32: ( ':' )?
                    int alt17=2;
                    int LA17_0 = input.LA(1);
                    if ( LA17_0==21 ) {
                        alt17=1;
                    }
                    else if ( (LA17_0>=EOL && LA17_0<=20)||(LA17_0>=22 && LA17_0<=43) ) {
                        alt17=2;
                    }
                    else {

                        NoViableAltException nvae =
                            new NoViableAltException("151:32: ( \':\' )?", 17, 0, input);

                        throw nvae;
                    }
                    switch (alt17) {
                        case 1 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:151:32: ':'
                            {

                            match(input,21,FOLLOW_21_in_rule461);

                            }
                            break;

                    }


                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:152:25: (any= . )*
                    loop18:
                    do {
                        int alt18=2;
                        alt18 = dfa18.predict(input);
                        switch (alt18) {
                    	case 1 :
                    	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:152:26: any= .
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

                    }
                    break;

            }


            match(input,EOL,FOLLOW_EOL_in_rule496);

            match(input,24,FOLLOW_24_in_rule498);

            following.push(FOLLOW_opt_eol_in_rule500);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:162:1: rule_options returns [List options] : 'options' ( ':' )? opt_eol (a= rule_option opt_eol )* ;
    public List rule_options() throws RecognitionException {   

        List options;

        AttributeDescr a = null;



        		options = new ArrayList();
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:166:17: ( 'options' ( ':' )? opt_eol (a= rule_option opt_eol )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:166:17: 'options' ( ':' )? opt_eol (a= rule_option opt_eol )*
            {

            match(input,25,FOLLOW_25_in_rule_options521);

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:166:27: ( ':' )?
            int alt20=2;
            int LA20_0 = input.LA(1);
            if ( LA20_0==21 ) {
                alt20=1;
            }
            else if ( LA20_0==EOL||LA20_0==20||LA20_0==23||(LA20_0>=26 && LA20_0<=27) ) {
                alt20=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("166:27: ( \':\' )?", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:166:27: ':'
                    {

                    match(input,21,FOLLOW_21_in_rule_options523);

                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_rule_options526);
            opt_eol();
            following.pop();


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:167:25: (a= rule_option opt_eol )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);
                if ( (LA21_0>=26 && LA21_0<=27) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:167:33: a= rule_option opt_eol
            	    {

            	    following.push(FOLLOW_rule_option_in_rule_options535);
            	    a=rule_option();
            	    following.pop();


            	    following.push(FOLLOW_opt_eol_in_rule_options537);
            	    opt_eol();
            	    following.pop();



            	    					options.add( a );
            	    				

            	    }
            	    break;

            	default :
            	    break loop21;
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:174:1: rule_option returns [AttributeDescr d] : (a= salience | a= no_loop );
    public AttributeDescr rule_option() throws RecognitionException {   

        AttributeDescr d;

        AttributeDescr a = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:179:25: (a= salience | a= no_loop )
            int alt22=2;
            int LA22_0 = input.LA(1);
            if ( LA22_0==26 ) {
                alt22=1;
            }
            else if ( LA22_0==27 ) {
                alt22=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("174:1: rule_option returns [AttributeDescr d] : (a= salience | a= no_loop );", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:179:25: a= salience
                    {

                    following.push(FOLLOW_salience_in_rule_option576);
                    a=salience();
                    following.pop();


                     d = a; 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:180:25: a= no_loop
                    {

                    following.push(FOLLOW_no_loop_in_rule_option586);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:184:1: salience returns [AttributeDescr d ] : loc= 'salience' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr salience() throws RecognitionException {   

        AttributeDescr d;
        Token loc=null;
        Token i=null;


        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:189:17: (loc= 'salience' opt_eol i= INT ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:189:17: loc= 'salience' opt_eol i= INT ( ';' )? opt_eol
            {

            loc=(Token)input.LT(1);
            match(input,26,FOLLOW_26_in_salience619);

            following.push(FOLLOW_opt_eol_in_salience621);
            opt_eol();
            following.pop();


            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience625);

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:189:46: ( ';' )?
            int alt23=2;
            int LA23_0 = input.LA(1);
            if ( LA23_0==16 ) {
                alt23=1;
            }
            else if ( LA23_0==EOL||LA23_0==20||LA23_0==23||(LA23_0>=26 && LA23_0<=27) ) {
                alt23=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("189:46: ( \';\' )?", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:189:46: ';'
                    {

                    match(input,16,FOLLOW_16_in_salience627);

                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_salience630);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:196:1: no_loop returns [AttributeDescr d] : loc= 'no-loop' ( ';' )? opt_eol ;
    public AttributeDescr no_loop() throws RecognitionException {   

        AttributeDescr d;
        Token loc=null;


        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:201:17: (loc= 'no-loop' ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:201:17: loc= 'no-loop' ( ';' )? opt_eol
            {

            loc=(Token)input.LT(1);
            match(input,27,FOLLOW_27_in_no_loop660);

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:201:31: ( ';' )?
            int alt24=2;
            int LA24_0 = input.LA(1);
            if ( LA24_0==16 ) {
                alt24=1;
            }
            else if ( LA24_0==EOL||LA24_0==20||LA24_0==23||(LA24_0>=26 && LA24_0<=27) ) {
                alt24=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("201:31: ( \';\' )?", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:201:31: ';'
                    {

                    match(input,16,FOLLOW_16_in_no_loop662);

                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_no_loop665);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:208:1: root_lhs returns [PatternDescr d] : ({...}?e= expander_lhs | l= lhs );
    public PatternDescr root_lhs() throws RecognitionException {   

        PatternDescr d;

        PatternDescr e = null;

        PatternDescr l = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:212:17: ({...}?e= expander_lhs | l= lhs )
            int alt25=2;
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
            case 43:
                alt25=1;
                break;
            case 40:
                int LA25_2 = input.LA(2);
                if (  expander != null  ) {
                    alt25=1;
                }
                else if ( true ) {
                    alt25=2;
                }
                else {

                    NoViableAltException nvae =
                        new NoViableAltException("208:1: root_lhs returns [PatternDescr d] : ({...}?e= expander_lhs | l= lhs );", 25, 2, input);

                    throw nvae;
                }
                break;
            case 41:
                int LA25_3 = input.LA(2);
                if (  expander != null  ) {
                    alt25=1;
                }
                else if ( true ) {
                    alt25=2;
                }
                else {

                    NoViableAltException nvae =
                        new NoViableAltException("208:1: root_lhs returns [PatternDescr d] : ({...}?e= expander_lhs | l= lhs );", 25, 3, input);

                    throw nvae;
                }
                break;
            case 42:
                int LA25_4 = input.LA(2);
                if (  expander != null  ) {
                    alt25=1;
                }
                else if ( true ) {
                    alt25=2;
                }
                else {

                    NoViableAltException nvae =
                        new NoViableAltException("208:1: root_lhs returns [PatternDescr d] : ({...}?e= expander_lhs | l= lhs );", 25, 4, input);

                    throw nvae;
                }
                break;
            case ID:
                int LA25_5 = input.LA(2);
                if (  expander != null  ) {
                    alt25=1;
                }
                else if ( true ) {
                    alt25=2;
                }
                else {

                    NoViableAltException nvae =
                        new NoViableAltException("208:1: root_lhs returns [PatternDescr d] : ({...}?e= expander_lhs | l= lhs );", 25, 5, input);

                    throw nvae;
                }
                break;
            case 28:
                int LA25_6 = input.LA(2);
                if (  expander != null  ) {
                    alt25=1;
                }
                else if ( true ) {
                    alt25=2;
                }
                else {

                    NoViableAltException nvae =
                        new NoViableAltException("208:1: root_lhs returns [PatternDescr d] : ({...}?e= expander_lhs | l= lhs );", 25, 6, input);

                    throw nvae;
                }
                break;
            default:

                NoViableAltException nvae =
                    new NoViableAltException("208:1: root_lhs returns [PatternDescr d] : ({...}?e= expander_lhs | l= lhs );", 25, 0, input);

                throw nvae;
            }

            switch (alt25) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:212:17: {...}?e= expander_lhs
                    {

                    if ( !( expander != null ) ) {
                        throw new FailedPredicateException(input, "root_lhs", " expander != null ");
                    }

                    following.push(FOLLOW_expander_lhs_in_root_lhs695);
                    e=expander_lhs();
                    following.pop();


                     d = e; 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:213:25: l= lhs
                    {

                    following.push(FOLLOW_lhs_in_root_lhs705);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:216:1: expander_lhs returns [PatternDescr d] : ( '>' l= lhs | e= expander_text );
    public PatternDescr expander_lhs() throws RecognitionException {   

        PatternDescr d;

        PatternDescr l = null;

        PatternDescr e = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:221:17: ( '>' l= lhs | e= expander_text )
            int alt26=2;
            int LA26_0 = input.LA(1);
            if ( LA26_0==22 ) {
                alt26=1;
            }
            else if ( (LA26_0>=EOL && LA26_0<=21)||(LA26_0>=23 && LA26_0<=43) ) {
                alt26=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("216:1: expander_lhs returns [PatternDescr d] : ( \'>\' l= lhs | e= expander_text );", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:221:17: '>' l= lhs
                    {

                    match(input,22,FOLLOW_22_in_expander_lhs732);

                    following.push(FOLLOW_lhs_in_expander_lhs736);
                    l=lhs();
                    following.pop();


                     d = l; 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:222:19: e= expander_text
                    {

                    following.push(FOLLOW_expander_text_in_expander_lhs746);
                    e=expander_text();
                    following.pop();


                     d = e; 

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:224:1: expander_text returns [PatternDescr d] : ( options {greedy=false; } : a= . )+ EOL ;
    public PatternDescr expander_text() throws RecognitionException {   

        PatternDescr d;
        Token a=null;


        		d = null;
        		String text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:230:17: ( ( options {greedy=false; } : a= . )+ EOL )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:230:17: ( options {greedy=false; } : a= . )+ EOL
            {

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:230:17: ( options {greedy=false; } : a= . )+
            int cnt27=0;
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);
                if ( LA27_0==EOL ) {
                    int LA27_1 = input.LA(2);
                    if ( (LA27_1>=EOL && LA27_1<=43) ) {
                        alt27=1;
                    }
                    else if ( LA27_1==-1 ) {
                        alt27=2;
                    }


                }
                else if ( (LA27_0>=ID && LA27_0<=43) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:230:50: a= .
            	    {

            	    a=(Token)input.LT(1);
            	    matchAny(input);


            	    				System.err.println( "[" + a.getText() + "]" );
            	    				if ( text == null ) {
            	    					text = a.getText();
            	    				} else {
            	    					text = text + " " + a.getText();
            	    				}
            	    			

            	    }
            	    break;

            	default :
            	    if ( cnt27 >= 1 ) break loop27;
                        EarlyExitException eee =
                            new EarlyExitException(27, input);
                        throw eee;
                }
                cnt27++;
            } while (true);


            match(input,EOL,FOLLOW_EOL_in_expander_text797);


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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:246:1: lhs returns [PatternDescr d] : l= lhs_or ;
    public PatternDescr lhs() throws RecognitionException {   

        PatternDescr d;

        PatternDescr l = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:250:17: (l= lhs_or )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:250:17: l= lhs_or
            {

            following.push(FOLLOW_lhs_or_in_lhs825);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:254:1: lhs_column returns [ColumnDescr d] : (f= fact_binding | f= fact );
    public ColumnDescr lhs_column() throws RecognitionException {   

        ColumnDescr d;

        ColumnDescr f = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:258:17: (f= fact_binding | f= fact )
            int alt28=2;
            alt28 = dfa28.predict(input);
            switch (alt28) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:258:17: f= fact_binding
                    {

                    following.push(FOLLOW_fact_binding_in_lhs_column852);
                    f=fact_binding();
                    following.pop();


                     d = f; 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:259:17: f= fact
                    {

                    following.push(FOLLOW_fact_in_lhs_column861);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:262:1: fact_binding returns [ColumnDescr d] : id= ID opt_eol ':' opt_eol f= fact opt_eol ;
    public ColumnDescr fact_binding() throws RecognitionException {   

        ColumnDescr d;
        Token id=null;
        ColumnDescr f = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:267:17: (id= ID opt_eol ':' opt_eol f= fact opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:267:17: id= ID opt_eol ':' opt_eol f= fact opt_eol
            {

            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding893);

            following.push(FOLLOW_opt_eol_in_fact_binding895);
            opt_eol();
            following.pop();


            match(input,21,FOLLOW_21_in_fact_binding897);

            following.push(FOLLOW_opt_eol_in_fact_binding899);
            opt_eol();
            following.pop();


            following.push(FOLLOW_fact_in_fact_binding903);
            f=fact();
            following.pop();


             d=f; 

            following.push(FOLLOW_opt_eol_in_fact_binding907);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:274:1: fact returns [ColumnDescr d] : id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol ;
    public ColumnDescr fact() throws RecognitionException {   

        ColumnDescr d;
        Token id=null;
        List c = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:278:17: (id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:278:17: id= ID opt_eol '(' opt_eol (c= constraints )? opt_eol ')' opt_eol
            {

            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact939);

             
             			d = new ColumnDescr( id.getText() ); 
             			d.setLocation( id.getLine(), id.getCharPositionInLine() );
             		

            following.push(FOLLOW_opt_eol_in_fact947);
            opt_eol();
            following.pop();


            match(input,28,FOLLOW_28_in_fact953);

            following.push(FOLLOW_opt_eol_in_fact955);
            opt_eol();
            following.pop();


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:283:29: (c= constraints )?
            int alt29=2;
            alt29 = dfa29.predict(input);
            switch (alt29) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:283:33: c= constraints
                    {

                    following.push(FOLLOW_constraints_in_fact961);
                    c=constraints();
                    following.pop();



                    		 			for ( Iterator cIter = c.iterator() ; cIter.hasNext() ; ) {
                     						d.addDescr( (PatternDescr) cIter.next() );
                     					}
                     				

                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_fact980);
            opt_eol();
            following.pop();


            match(input,29,FOLLOW_29_in_fact982);

            following.push(FOLLOW_opt_eol_in_fact984);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:294:1: constraints returns [List constraints] : opt_eol c= constraint ( opt_eol ',' opt_eol c= constraint )* opt_eol ;
    public List constraints() throws RecognitionException {   

        List constraints;

        PatternDescr c = null;



        		constraints = new ArrayList();
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:298:17: ( opt_eol c= constraint ( opt_eol ',' opt_eol c= constraint )* opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:298:17: opt_eol c= constraint ( opt_eol ',' opt_eol c= constraint )* opt_eol
            {

            following.push(FOLLOW_opt_eol_in_constraints1010);
            opt_eol();
            following.pop();


            following.push(FOLLOW_constraint_in_constraints1016);
            c=constraint();
            following.pop();


             constraints.add( c ); 

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:300:17: ( opt_eol ',' opt_eol c= constraint )*
            loop30:
            do {
                int alt30=2;
                alt30 = dfa30.predict(input);
                switch (alt30) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:300:19: opt_eol ',' opt_eol c= constraint
            	    {

            	    following.push(FOLLOW_opt_eol_in_constraints1024);
            	    opt_eol();
            	    following.pop();


            	    match(input,30,FOLLOW_30_in_constraints1026);

            	    following.push(FOLLOW_opt_eol_in_constraints1028);
            	    opt_eol();
            	    following.pop();


            	    following.push(FOLLOW_constraint_in_constraints1032);
            	    c=constraint();
            	    following.pop();


            	     constraints.add( c ); 

            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);


            following.push(FOLLOW_opt_eol_in_constraints1041);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:304:1: constraint returns [PatternDescr d] : opt_eol f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!=') opt_eol (lc= literal_constraint | rvc= retval_constraint ) opt_eol ;
    public PatternDescr constraint() throws RecognitionException {   

        PatternDescr d;
        Token f=null;
        Token op=null;
        String lc = null;

        String rvc = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:308:17: ( opt_eol f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!=') opt_eol (lc= literal_constraint | rvc= retval_constraint ) opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:308:17: opt_eol f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!=') opt_eol (lc= literal_constraint | rvc= retval_constraint ) opt_eol
            {

            following.push(FOLLOW_opt_eol_in_constraint1063);
            opt_eol();
            following.pop();


            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint1069);

            following.push(FOLLOW_opt_eol_in_constraint1071);
            opt_eol();
            following.pop();


            op=(Token)input.LT(1);
            if ( input.LA(1)==22||(input.LA(1)>=31 && input.LA(1)<=35) ) {
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint1078);    throw mse;
            }


            following.push(FOLLOW_opt_eol_in_constraint1132);
            opt_eol();
            following.pop();


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:317:41: (lc= literal_constraint | rvc= retval_constraint )
            int alt31=2;
            switch ( input.LA(1) ) {
            case STRING:
                alt31=1;
                break;
            case INT:
                alt31=1;
                break;
            case FLOAT:
                alt31=1;
                break;
            case -1:
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
                alt31=2;
                break;
            default:

                NoViableAltException nvae =
                    new NoViableAltException("317:41: (lc= literal_constraint | rvc= retval_constraint )", 31, 0, input);

                throw nvae;
            }

            switch (alt31) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:317:49: lc= literal_constraint
                    {

                    following.push(FOLLOW_literal_constraint_in_constraint1150);
                    lc=literal_constraint();
                    following.pop();


                     
                    							d = new LiteralDescr( f.getText(), op.getText(), lc ); 
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    						

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:322:49: rvc= retval_constraint
                    {

                    following.push(FOLLOW_retval_constraint_in_constraint1170);
                    rvc=retval_constraint();
                    following.pop();


                     
                    							d = new ReturnValueDescr( f.getText(), op.getText(), rvc ); 
                    							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                    						

                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_constraint1191);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:331:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT ) ;
    public String literal_constraint() throws RecognitionException {   

        String text;
        Token t=null;


        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:335:17: ( (t= STRING | t= INT | t= FLOAT ) )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:335:17: (t= STRING | t= INT | t= FLOAT )
            {

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:335:17: (t= STRING | t= INT | t= FLOAT )
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
                    new NoViableAltException("335:17: (t= STRING | t= INT | t= FLOAT )", 32, 0, input);

                throw nvae;
            }

            switch (alt32) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:335:25: t= STRING
                    {

                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint1217);

                     text = t.getText(); text=text.substring( 1, text.length() - 1 ); 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:336:25: t= INT
                    {

                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint1227);

                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:337:25: t= FLOAT
                    {

                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint1240);

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:341:1: retval_constraint returns [String text] : c= chunk ;
    public String retval_constraint() throws RecognitionException {   

        String text;

        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:346:17: (c= chunk )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:346:17: c= chunk
            {

            following.push(FOLLOW_chunk_in_retval_constraint1274);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:349:1: chunk returns [String text] : ( (any= . ) | ( '(' c= chunk ')' ) )* ;
    public String chunk() throws RecognitionException {   

        String text;
        Token any=null;
        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:353:17: ( ( (any= . ) | ( '(' c= chunk ')' ) )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:353:17: ( (any= . ) | ( '(' c= chunk ')' ) )*
            {

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:353:17: ( (any= . ) | ( '(' c= chunk ')' ) )*
            loop33:
            do {
                int alt33=3;
                alt33 = dfa33.predict(input);
                switch (alt33) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:353:25: (any= . )
            	    {

            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:353:25: (any= . )
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:353:27: any= .
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
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:360:25: ( '(' c= chunk ')' )
            	    {

            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:360:25: ( '(' c= chunk ')' )
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:360:27: '(' c= chunk ')'
            	    {

            	    match(input,28,FOLLOW_28_in_chunk1315);

            	    following.push(FOLLOW_chunk_in_chunk1319);
            	    c=chunk();
            	    following.pop();


            	    match(input,29,FOLLOW_29_in_chunk1321);


            	    							if ( text == null ) {
            	    								text = "( " + c + " )";
            	    							} else {
            	    								text = text + " ( " + c + " )";
            	    							}
            	    						

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



    // $ANTLR start field_binding
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:371:1: field_binding returns [PatternDescr d] : f= ID ':' c= constraint ;
    public PatternDescr field_binding() throws RecognitionException {   

        PatternDescr d;
        Token f=null;
        PatternDescr c = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:376:17: (f= ID ':' c= constraint )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:376:17: f= ID ':' c= constraint
            {

            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_field_binding1359);

            match(input,21,FOLLOW_21_in_field_binding1361);

            following.push(FOLLOW_constraint_in_field_binding1365);
            c=constraint();
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
    // $ANTLR end field_binding



    // $ANTLR start lhs_or
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:379:1: lhs_or returns [PatternDescr d] : left= lhs_and ( ('or'|'||')right= lhs_and )* ;
    public PatternDescr lhs_or() throws RecognitionException {   

        PatternDescr d;

        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:384:17: (left= lhs_and ( ('or'|'||')right= lhs_and )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:384:17: left= lhs_and ( ('or'|'||')right= lhs_and )*
            {

             OrDescr or = null; 

            following.push(FOLLOW_lhs_and_in_lhs_or1395);
            left=lhs_and();
            following.pop();


            d = left; 

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:386:17: ( ('or'|'||')right= lhs_and )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);
                if ( (LA34_0>=36 && LA34_0<=37) ) {
                    int LA34_11 = input.LA(2);
                    if ( !( expander != null ) ) {
                        alt34=1;
                    }


                }


                switch (alt34) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:386:25: ('or'|'||')right= lhs_and
            	    {

            	    if ( (input.LA(1)>=36 && input.LA(1)<=37) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or1405);    throw mse;
            	    }


            	    following.push(FOLLOW_lhs_and_in_lhs_or1416);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:400:1: lhs_and returns [PatternDescr d] : left= lhs_unary ( ('and'|'&&')right= lhs_unary )* ;
    public PatternDescr lhs_and() throws RecognitionException {   

        PatternDescr d;

        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:405:17: (left= lhs_unary ( ('and'|'&&')right= lhs_unary )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:405:17: left= lhs_unary ( ('and'|'&&')right= lhs_unary )*
            {

             AndDescr and = null; 

            following.push(FOLLOW_lhs_unary_in_lhs_and1456);
            left=lhs_unary();
            following.pop();


             d = left; 

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:407:17: ( ('and'|'&&')right= lhs_unary )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);
                if ( (LA35_0>=38 && LA35_0<=39) ) {
                    int LA35_12 = input.LA(2);
                    if ( !( expander != null ) ) {
                        alt35=1;
                    }


                }


                switch (alt35) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:407:25: ('and'|'&&')right= lhs_unary
            	    {

            	    if ( (input.LA(1)>=38 && input.LA(1)<=39) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and1465);    throw mse;
            	    }


            	    following.push(FOLLOW_lhs_unary_in_lhs_and1476);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:421:1: lhs_unary returns [PatternDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) ;
    public PatternDescr lhs_unary() throws RecognitionException {   

        PatternDescr d;

        PatternDescr u = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:425:17: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:425:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            {

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:425:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            int alt36=5;
            switch ( input.LA(1) ) {
            case 40:
                alt36=1;
                break;
            case 41:
                alt36=2;
                break;
            case 42:
                alt36=3;
                break;
            case ID:
                alt36=4;
                break;
            case 28:
                alt36=5;
                break;
            default:

                NoViableAltException nvae =
                    new NoViableAltException("425:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | \'(\' u= lhs \')\' )", 36, 0, input);

                throw nvae;
            }

            switch (alt36) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:425:25: u= lhs_exist
                    {

                    following.push(FOLLOW_lhs_exist_in_lhs_unary1514);
                    u=lhs_exist();
                    following.pop();


                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:426:25: u= lhs_not
                    {

                    following.push(FOLLOW_lhs_not_in_lhs_unary1522);
                    u=lhs_not();
                    following.pop();


                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:427:25: u= lhs_eval
                    {

                    following.push(FOLLOW_lhs_eval_in_lhs_unary1530);
                    u=lhs_eval();
                    following.pop();


                    }
                    break;
                case 4 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:428:25: u= lhs_column
                    {

                    following.push(FOLLOW_lhs_column_in_lhs_unary1538);
                    u=lhs_column();
                    following.pop();


                    }
                    break;
                case 5 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:429:25: '(' u= lhs ')'
                    {

                    match(input,28,FOLLOW_28_in_lhs_unary1544);

                    following.push(FOLLOW_lhs_in_lhs_unary1548);
                    u=lhs();
                    following.pop();


                    match(input,29,FOLLOW_29_in_lhs_unary1550);

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:433:1: lhs_exist returns [PatternDescr d] : loc= 'exists' column= lhs_column ;
    public PatternDescr lhs_exist() throws RecognitionException {   

        PatternDescr d;
        Token loc=null;
        ColumnDescr column = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:437:17: (loc= 'exists' column= lhs_column )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:437:17: loc= 'exists' column= lhs_column
            {

            loc=(Token)input.LT(1);
            match(input,40,FOLLOW_40_in_lhs_exist1580);

            following.push(FOLLOW_lhs_column_in_lhs_exist1584);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:444:1: lhs_not returns [NotDescr d] : loc= 'not' column= lhs_column ;
    public NotDescr lhs_not() throws RecognitionException {   

        NotDescr d;
        Token loc=null;
        ColumnDescr column = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:448:17: (loc= 'not' column= lhs_column )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:448:17: loc= 'not' column= lhs_column
            {

            loc=(Token)input.LT(1);
            match(input,41,FOLLOW_41_in_lhs_not1614);

            following.push(FOLLOW_lhs_column_in_lhs_not1618);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:455:1: lhs_eval returns [PatternDescr d] : 'eval' ;
    public PatternDescr lhs_eval() throws RecognitionException {   

        PatternDescr d;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:459:17: ( 'eval' )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:459:17: 'eval'
            {

            match(input,42,FOLLOW_42_in_lhs_eval1644);

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:462:1: dotted_name returns [String name] : id= ID ( '.' id= ID )* ;
    public String dotted_name() throws RecognitionException {   

        String name;
        Token id=null;


        		name = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:467:17: (id= ID ( '.' id= ID )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:467:17: id= ID ( '.' id= ID )*
            {

            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name1673);

             name=id.getText(); 

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:467:46: ( '.' id= ID )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);
                if ( LA37_0==15 ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:467:48: '.' id= ID
            	    {

            	    match(input,15,FOLLOW_15_in_dotted_name1679);

            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_dotted_name1683);

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:471:1: word returns [String word] : (id= ID | 'import' | 'use' | 'rule' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING );
    public String word() throws RecognitionException {   

        String word;
        Token id=null;
        Token str=null;


        		word = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:475:17: (id= ID | 'import' | 'use' | 'rule' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING )
            int alt38=10;
            switch ( input.LA(1) ) {
            case ID:
                alt38=1;
                break;
            case 17:
                alt38=2;
                break;
            case 43:
                alt38=3;
                break;
            case 19:
                alt38=4;
                break;
            case 26:
                alt38=5;
                break;
            case 27:
                alt38=6;
                break;
            case 20:
                alt38=7;
                break;
            case 23:
                alt38=8;
                break;
            case 24:
                alt38=9;
                break;
            case STRING:
                alt38=10;
                break;
            default:

                NoViableAltException nvae =
                    new NoViableAltException("471:1: word returns [String word] : (id= ID | \'import\' | \'use\' | \'rule\' | \'salience\' | \'no-loop\' | \'when\' | \'then\' | \'end\' | str= STRING );", 38, 0, input);

                throw nvae;
            }

            switch (alt38) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:475:17: id= ID
                    {

                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_word1713);

                     word=id.getText(); 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:476:17: 'import'
                    {

                    match(input,17,FOLLOW_17_in_word1725);

                     word="import"; 

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:477:17: 'use'
                    {

                    match(input,43,FOLLOW_43_in_word1734);

                     word="use"; 

                    }
                    break;
                case 4 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:478:17: 'rule'
                    {

                    match(input,19,FOLLOW_19_in_word1746);

                     word="rule"; 

                    }
                    break;
                case 5 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:479:17: 'salience'
                    {

                    match(input,26,FOLLOW_26_in_word1757);

                     word="salience"; 

                    }
                    break;
                case 6 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:480:17: 'no-loop'
                    {

                    match(input,27,FOLLOW_27_in_word1765);

                     word="no-loop"; 

                    }
                    break;
                case 7 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:481:17: 'when'
                    {

                    match(input,20,FOLLOW_20_in_word1773);

                     word="when"; 

                    }
                    break;
                case 8 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:482:17: 'then'
                    {

                    match(input,23,FOLLOW_23_in_word1784);

                     word="then"; 

                    }
                    break;
                case 9 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:483:17: 'end'
                    {

                    match(input,24,FOLLOW_24_in_word1795);

                     word="end"; 

                    }
                    break;
                case 10 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:484:17: str= STRING
                    {

                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_word1809);

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


    protected DFA18 dfa18 = new DFA18();protected DFA28 dfa28 = new DFA28();protected DFA29 dfa29 = new DFA29();protected DFA30 dfa30 = new DFA30();protected DFA33 dfa33 = new DFA33();
    class DFA18 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s2 = new DFA.State() {{alt=1;}};
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
        DFA.State s7 = new DFA.State() {
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
                case 22:
                case 25:
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

                case 26:
                    return s20;

                case 27:
                    return s21;

                case 20:
                    return s22;

                case 23:
                    return s23;

                case 24:
                    return s24;

                case STRING:
                    return s25;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 18, 7, input);

                    throw nvae;        }
            }
        };
        DFA.State s8 = new DFA.State() {{alt=2;}};
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

                case 19:
                    return s7;

                case EOL:
                    return s6;

                case -1:
                    return s8;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 18, 6, input);

                    throw nvae;        }
            }
        };
        DFA.State s3 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s6;

                case 19:
                    return s7;

                case -1:
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
                        new NoViableAltException("", 18, 3, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA18_1 = input.LA(1);
                if ( LA18_1==24 ) {return s3;}
                if ( (LA18_1>=EOL && LA18_1<=23)||(LA18_1>=25 && LA18_1<=43) ) {return s2;}


                NoViableAltException nvae =
        	    new NoViableAltException("", 18, 1, input);

                throw nvae;
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA18_0 = input.LA(1);
                if ( LA18_0==EOL ) {return s1;}
                if ( (LA18_0>=ID && LA18_0<=43) ) {return s2;}


                NoViableAltException nvae =
        	    new NoViableAltException("", 18, 0, input);

                throw nvae;
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
                case 28:
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

                case 28:
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

    }class DFA29 extends DFA {
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
                        new NoViableAltException("", 29, 1, input);

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
                        new NoViableAltException("", 29, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA30 extends DFA {
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
                        new NoViableAltException("", 30, 1, input);

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
                        new NoViableAltException("", 30, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA33 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s1 = new DFA.State() {{alt=1;}};
        DFA.State s5 = new DFA.State() {{alt=1;}};
        DFA.State s26 = new DFA.State() {{alt=1;}};
        DFA.State s21 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA33_21 = input.LA(1);
                if ( LA33_21==EOL ) {return s21;}
                if ( LA33_21==-1||(LA33_21>=ID && LA33_21<=43) ) {return s26;}


                NoViableAltException nvae =
        	    new NoViableAltException("", 33, 21, input);

                throw nvae;
            }
        };
        DFA.State s12 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s21;

                case -1:
                case 28:
                case 29:
                case 30:
                    return s5;

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
                    return s26;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 33, 12, input);

                    throw nvae;        }
            }
        };
        DFA.State s7 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s7;

                case -1:
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
                    return s5;

                case ID:
                    return s12;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 33, 7, input);

                    throw nvae;        }
            }
        };
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s7;

                case -1:
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
                    return s5;

                case ID:
                    return s12;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 33, 2, input);

                    throw nvae;        }
            }
        };
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s4 = new DFA.State() {{alt=3;}};
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s1;

                case 30:
                    return s2;

                case 29:
                    return s3;

                case -1:
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
                    return s5;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 33, 0, input);

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
    public static final BitSet FOLLOW_dotted_name_in_import_statement206 = new BitSet(new long[]{65554L});
    public static final BitSet FOLLOW_16_in_import_statement208 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_import_statement213 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_18_in_use_expander231 = new BitSet(new long[]{65586L});
    public static final BitSet FOLLOW_dotted_name_in_use_expander236 = new BitSet(new long[]{65554L});
    public static final BitSet FOLLOW_16_in_use_expander240 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_use_expander243 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_opt_eol_in_rule271 = new BitSet(new long[]{524288L});
    public static final BitSet FOLLOW_19_in_rule277 = new BitSet(new long[]{8796321218720L});
    public static final BitSet FOLLOW_word_in_rule281 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_rule283 = new BitSet(new long[]{42991632L});
    public static final BitSet FOLLOW_rule_options_in_rule296 = new BitSet(new long[]{9437200L});
    public static final BitSet FOLLOW_20_in_rule314 = new BitSet(new long[]{2097170L});
    public static final BitSet FOLLOW_21_in_rule316 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_rule319 = new BitSet(new long[]{17592186044400L});
    public static final BitSet FOLLOW_22_in_rule351 = new BitSet(new long[]{7696849829920L});
    public static final BitSet FOLLOW_lhs_in_rule355 = new BitSet(new long[]{17592186044400L});
    public static final BitSet FOLLOW_EOL_in_rule418 = new BitSet(new long[]{17592186044400L});
    public static final BitSet FOLLOW_lhs_in_rule435 = new BitSet(new long[]{17592186044400L});
    public static final BitSet FOLLOW_23_in_rule459 = new BitSet(new long[]{17592186044400L});
    public static final BitSet FOLLOW_21_in_rule461 = new BitSet(new long[]{17592186044400L});
    public static final BitSet FOLLOW_EOL_in_rule496 = new BitSet(new long[]{16777216L});
    public static final BitSet FOLLOW_24_in_rule498 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_rule500 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_25_in_rule_options521 = new BitSet(new long[]{2097170L});
    public static final BitSet FOLLOW_21_in_rule_options523 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_rule_options526 = new BitSet(new long[]{201326594L});
    public static final BitSet FOLLOW_rule_option_in_rule_options535 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_rule_options537 = new BitSet(new long[]{201326594L});
    public static final BitSet FOLLOW_salience_in_rule_option576 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_no_loop_in_rule_option586 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_26_in_salience619 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_salience621 = new BitSet(new long[]{64L});
    public static final BitSet FOLLOW_INT_in_salience625 = new BitSet(new long[]{65554L});
    public static final BitSet FOLLOW_16_in_salience627 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_salience630 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_27_in_no_loop660 = new BitSet(new long[]{65554L});
    public static final BitSet FOLLOW_16_in_no_loop662 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop665 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_expander_lhs_in_root_lhs695 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_lhs_in_root_lhs705 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_22_in_expander_lhs732 = new BitSet(new long[]{7696849829920L});
    public static final BitSet FOLLOW_lhs_in_expander_lhs736 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_expander_text_in_expander_lhs746 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_EOL_in_expander_text797 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_lhs_or_in_lhs825 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column852 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_fact_in_lhs_column861 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_ID_in_fact_binding893 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding895 = new BitSet(new long[]{2097152L});
    public static final BitSet FOLLOW_21_in_fact_binding897 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding899 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_fact_in_fact_binding903 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding907 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_ID_in_fact939 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact947 = new BitSet(new long[]{268435456L});
    public static final BitSet FOLLOW_28_in_fact953 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact955 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_constraints_in_fact961 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact980 = new BitSet(new long[]{536870912L});
    public static final BitSet FOLLOW_29_in_fact982 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact984 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1010 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_constraint_in_constraints1016 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1024 = new BitSet(new long[]{1073741824L});
    public static final BitSet FOLLOW_30_in_constraints1026 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1028 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_constraint_in_constraints1032 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1041 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1063 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_ID_in_constraint1069 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1071 = new BitSet(new long[]{66576187392L});
    public static final BitSet FOLLOW_set_in_constraint1078 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1132 = new BitSet(new long[]{17592186044402L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint1150 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint1170 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1191 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint1217 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_INT_in_literal_constraint1227 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint1240 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_chunk_in_retval_constraint1274 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_28_in_chunk1315 = new BitSet(new long[]{17592186044402L});
    public static final BitSet FOLLOW_chunk_in_chunk1319 = new BitSet(new long[]{536870912L});
    public static final BitSet FOLLOW_29_in_chunk1321 = new BitSet(new long[]{17592186044402L});
    public static final BitSet FOLLOW_ID_in_field_binding1359 = new BitSet(new long[]{2097152L});
    public static final BitSet FOLLOW_21_in_field_binding1361 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_constraint_in_field_binding1365 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1395 = new BitSet(new long[]{206158430210L});
    public static final BitSet FOLLOW_set_in_lhs_or1405 = new BitSet(new long[]{7696849829920L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1416 = new BitSet(new long[]{206158430210L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1456 = new BitSet(new long[]{824633720834L});
    public static final BitSet FOLLOW_set_in_lhs_and1465 = new BitSet(new long[]{7696849829920L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1476 = new BitSet(new long[]{824633720834L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary1514 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary1522 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary1530 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary1538 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_28_in_lhs_unary1544 = new BitSet(new long[]{7696849829920L});
    public static final BitSet FOLLOW_lhs_in_lhs_unary1548 = new BitSet(new long[]{536870912L});
    public static final BitSet FOLLOW_29_in_lhs_unary1550 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_40_in_lhs_exist1580 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist1584 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_41_in_lhs_not1614 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not1618 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_42_in_lhs_eval1644 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_ID_in_dotted_name1673 = new BitSet(new long[]{32770L});
    public static final BitSet FOLLOW_15_in_dotted_name1679 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_ID_in_dotted_name1683 = new BitSet(new long[]{32770L});
    public static final BitSet FOLLOW_ID_in_word1713 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_17_in_word1725 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_43_in_word1734 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_19_in_word1746 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_26_in_word1757 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_27_in_word1765 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_20_in_word1773 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_23_in_word1784 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_24_in_word1795 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_STRING_in_word1809 = new BitSet(new long[]{2L});

}