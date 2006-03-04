// $ANTLR 3.0ea7 /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g 2006-03-04 17:06:04

	package org.drools.lang;
	import java.util.List;
	import java.util.ArrayList;
	import org.drools.lang.descr.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class RuleParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "EOL", "ID", "INT", "STRING", "FLOAT", "WS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "\'package\'", "\'.\'", "\';\'", "\'import\'", "\'use\'", "\'expander\'", "\'rule\'", "\'when\'", "\':\'", "\'then\'", "\'end\'", "\'options\'", "\',\'", "\'salience\'", "\'no-loop\'", "\'(\'", "\')\'", "\'==\'", "\'>\'", "\'>=\'", "\'<\'", "\'<=\'", "\'!=\'", "\'or\'", "\'||\'", "\'and\'", "\'&&\'", "\'exists\'", "\'not\'", "\'eval\'"
    };
    public static final int INT=6;
    public static final int WS=9;
    public static final int EOL=4;
    public static final int STRING=7;
    public static final int FLOAT=8;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=10;
    public static final int MULTI_LINE_COMMENT=12;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=11;
    public static final int ID=5;
        public RuleParser(TokenStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }


    	private String    packageName = "";
    	private List      rules       = new ArrayList();
    	private List      imports     = new ArrayList();
    	
    	public String getPackageName() { return packageName; }
    	public List getImports() { return imports; }
    	public List getRules() { return rules; }




    // $ANTLR start opt_eol
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:24:1: opt_eol : ( EOL )* ;
    public void opt_eol() throws RecognitionException {   




        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:25:17: ( ( EOL )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:25:17: ( EOL )*
            {

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:25:17: ( EOL )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);
                if ( LA1_0==EOL ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:25:17: EOL
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:28:1: compilation_unit : prolog (r= rule )* ;
    public void compilation_unit() throws RecognitionException {   



        RuleDescr r = null;


        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:29:17: ( prolog (r= rule )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:29:17: prolog (r= rule )*
            {

            following.push(FOLLOW_prolog_in_compilation_unit53);
            prolog();
            following.pop();


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:30:17: (r= rule )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);
                if ( LA2_0==EOL||LA2_0==19 ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:30:18: r= rule
            	    {

            	    following.push(FOLLOW_rule_in_compilation_unit61);
            	    r=rule();
            	    following.pop();


            	    this.rules.add( r ); 

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:33:1: prolog : opt_eol (name= package_statement )? opt_eol (name= import_statement )* opt_eol ( use_expander )? opt_eol ;
    public void prolog() throws RecognitionException {   



        String name = null;


        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:34:17: ( opt_eol (name= package_statement )? opt_eol (name= import_statement )* opt_eol ( use_expander )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:34:17: opt_eol (name= package_statement )? opt_eol (name= import_statement )* opt_eol ( use_expander )? opt_eol
            {

            following.push(FOLLOW_opt_eol_in_prolog77);
            opt_eol();
            following.pop();


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:35:17: (name= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( LA3_0==13 ) {
                alt3=1;
            }
            else if ( LA3_0==-1||LA3_0==EOL||(LA3_0>=16 && LA3_0<=17)||LA3_0==19 ) {
                alt3=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("35:17: (name= package_statement )?", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:35:19: name= package_statement
                    {

                    following.push(FOLLOW_package_statement_in_prolog85);
                    name=package_statement();
                    following.pop();


                     this.packageName = name; 

                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_prolog94);
            opt_eol();
            following.pop();


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:37:17: (name= import_statement )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);
                if ( LA4_0==16 ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:37:19: name= import_statement
            	    {

            	    following.push(FOLLOW_import_statement_in_prolog102);
            	    name=import_statement();
            	    following.pop();


            	     this.imports.add( name ); 

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            following.push(FOLLOW_opt_eol_in_prolog111);
            opt_eol();
            following.pop();


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:39:17: ( use_expander )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( LA5_0==17 ) {
                alt5=1;
            }
            else if ( LA5_0==-1||LA5_0==EOL||LA5_0==19 ) {
                alt5=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("39:17: ( use_expander )?", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:39:17: use_expander
                    {

                    following.push(FOLLOW_use_expander_in_prolog115);
                    use_expander();
                    following.pop();


                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_prolog120);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:43:1: package_statement returns [String packageName] : 'package' opt_eol id= ID ( '.' id= ID )* ( ';' )? opt_eol ;
    public String package_statement() throws RecognitionException {   

        String packageName;
        Token id=null;


        		packageName = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:47:17: ( 'package' opt_eol id= ID ( '.' id= ID )* ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:47:17: 'package' opt_eol id= ID ( '.' id= ID )* ( ';' )? opt_eol
            {

            match(input,13,FOLLOW_13_in_package_statement141);

            following.push(FOLLOW_opt_eol_in_package_statement143);
            opt_eol();
            following.pop();


            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_package_statement147);

             packageName = id.getText(); 

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:47:73: ( '.' id= ID )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);
                if ( LA6_0==14 ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:47:75: '.' id= ID
            	    {

            	    match(input,14,FOLLOW_14_in_package_statement153);

            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_package_statement157);

            	     packageName += "." + id.getText(); 

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:47:127: ( ';' )?
            int alt7=2;
            int LA7_0 = input.LA(1);
            if ( LA7_0==15 ) {
                alt7=1;
            }
            else if ( LA7_0==-1||LA7_0==EOL||(LA7_0>=16 && LA7_0<=17)||LA7_0==19 ) {
                alt7=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("47:127: ( \';\' )?", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:47:127: ';'
                    {

                    match(input,15,FOLLOW_15_in_package_statement164);

                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_package_statement167);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:50:1: import_statement returns [String importStatement] : 'import' opt_eol name= java_package_or_class ( ';' )? opt_eol ;
    public String import_statement() throws RecognitionException {   

        String importStatement;

        String name = null;



        		importStatement = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:54:17: ( 'import' opt_eol name= java_package_or_class ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:54:17: 'import' opt_eol name= java_package_or_class ( ';' )? opt_eol
            {

            match(input,16,FOLLOW_16_in_import_statement190);

            following.push(FOLLOW_opt_eol_in_import_statement192);
            opt_eol();
            following.pop();


            following.push(FOLLOW_java_package_or_class_in_import_statement196);
            name=java_package_or_class();
            following.pop();


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:54:61: ( ';' )?
            int alt8=2;
            int LA8_0 = input.LA(1);
            if ( LA8_0==15 ) {
                alt8=1;
            }
            else if ( LA8_0==-1||LA8_0==EOL||(LA8_0>=16 && LA8_0<=17)||LA8_0==19 ) {
                alt8=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("54:61: ( \';\' )?", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:54:61: ';'
                    {

                    match(input,15,FOLLOW_15_in_import_statement198);

                    }
                    break;

            }


             importStatement = name; 

            following.push(FOLLOW_opt_eol_in_import_statement203);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:57:1: use_expander : 'use' 'expander' ID ( ';' )? opt_eol ;
    public void use_expander() throws RecognitionException {   




        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:58:17: ( 'use' 'expander' ID ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:58:17: 'use' 'expander' ID ( ';' )? opt_eol
            {

            match(input,17,FOLLOW_17_in_use_expander215);

            match(input,18,FOLLOW_18_in_use_expander217);

            match(input,ID,FOLLOW_ID_in_use_expander219);

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:58:37: ( ';' )?
            int alt9=2;
            int LA9_0 = input.LA(1);
            if ( LA9_0==15 ) {
                alt9=1;
            }
            else if ( LA9_0==-1||LA9_0==EOL||LA9_0==19 ) {
                alt9=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("58:37: ( \';\' )?", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:58:37: ';'
                    {

                    match(input,15,FOLLOW_15_in_use_expander221);

                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_use_expander224);
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
    // $ANTLR end use_expander



    // $ANTLR start rule
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:62:1: rule returns [RuleDescr rule] : opt_eol 'rule' ruleName= word opt_eol ( rule_options )? ( 'when' ( ':' )? opt_eol (l= lhs )* )? ( 'then' ( ':' )? (any= . )* )? EOL 'end' opt_eol ;
    public RuleDescr rule() throws RecognitionException {   

        RuleDescr rule;
        Token any=null;
        String ruleName = null;

        PatternDescr l = null;



        		rule = null;
        		String consequence = "";
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:68:17: ( opt_eol 'rule' ruleName= word opt_eol ( rule_options )? ( 'when' ( ':' )? opt_eol (l= lhs )* )? ( 'then' ( ':' )? (any= . )* )? EOL 'end' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:68:17: opt_eol 'rule' ruleName= word opt_eol ( rule_options )? ( 'when' ( ':' )? opt_eol (l= lhs )* )? ( 'then' ( ':' )? (any= . )* )? EOL 'end' opt_eol
            {

            following.push(FOLLOW_opt_eol_in_rule248);
            opt_eol();
            following.pop();


            match(input,19,FOLLOW_19_in_rule252);

            following.push(FOLLOW_word_in_rule256);
            ruleName=word();
            following.pop();


            following.push(FOLLOW_opt_eol_in_rule258);
            opt_eol();
            following.pop();


             rule = new RuleDescr( ruleName, null ); 

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:71:17: ( rule_options )?
            int alt10=2;
            int LA10_0 = input.LA(1);
            if ( LA10_0==24 ) {
                alt10=1;
            }
            else if ( LA10_0==EOL||LA10_0==20||LA10_0==22 ) {
                alt10=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("71:17: ( rule_options )?", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:71:17: rule_options
                    {

                    following.push(FOLLOW_rule_options_in_rule267);
                    rule_options();
                    following.pop();


                    }
                    break;

            }


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:72:17: ( 'when' ( ':' )? opt_eol (l= lhs )* )?
            int alt13=2;
            int LA13_0 = input.LA(1);
            if ( LA13_0==20 ) {
                alt13=1;
            }
            else if ( LA13_0==EOL||LA13_0==22 ) {
                alt13=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("72:17: ( \'when\' ( \':\' )? opt_eol (l= lhs )* )?", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:72:25: 'when' ( ':' )? opt_eol (l= lhs )*
                    {

                    match(input,20,FOLLOW_20_in_rule274);

                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:72:32: ( ':' )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);
                    if ( LA11_0==21 ) {
                        alt11=1;
                    }
                    else if ( (LA11_0>=EOL && LA11_0<=ID)||LA11_0==22||LA11_0==28||(LA11_0>=40 && LA11_0<=42) ) {
                        alt11=2;
                    }
                    else {

                        NoViableAltException nvae =
                            new NoViableAltException("72:32: ( \':\' )?", 11, 0, input);

                        throw nvae;
                    }
                    switch (alt11) {
                        case 1 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:72:32: ':'
                            {

                            match(input,21,FOLLOW_21_in_rule276);

                            }
                            break;

                    }


                    following.push(FOLLOW_opt_eol_in_rule279);
                    opt_eol();
                    following.pop();


                     AndDescr lhs = new AndDescr(); rule.setLhs( lhs ); 

                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:74:33: (l= lhs )*
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);
                        if ( LA12_0==ID||LA12_0==28||(LA12_0>=40 && LA12_0<=42) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:74:34: l= lhs
                    	    {

                    	    following.push(FOLLOW_lhs_in_rule293);
                    	    l=lhs();
                    	    following.pop();


                    	     lhs.addDescr( l ); 

                    	    }
                    	    break;

                    	default :
                    	    break loop12;
                        }
                    } while (true);


                    }
                    break;

            }


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:76:17: ( 'then' ( ':' )? (any= . )* )?
            int alt16=2;
            int LA16_0 = input.LA(1);
            if ( LA16_0==22 ) {
                alt16=1;
            }
            else if ( LA16_0==EOL ) {
                alt16=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("76:17: ( \'then\' ( \':\' )? (any= . )* )?", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:76:25: 'then' ( ':' )? (any= . )*
                    {

                    match(input,22,FOLLOW_22_in_rule309);

                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:76:32: ( ':' )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);
                    if ( LA14_0==21 ) {
                        alt14=1;
                    }
                    else if ( (LA14_0>=EOL && LA14_0<=20)||(LA14_0>=22 && LA14_0<=42) ) {
                        alt14=2;
                    }
                    else {

                        NoViableAltException nvae =
                            new NoViableAltException("76:32: ( \':\' )?", 14, 0, input);

                        throw nvae;
                    }
                    switch (alt14) {
                        case 1 :
                            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:76:32: ':'
                            {

                            match(input,21,FOLLOW_21_in_rule311);

                            }
                            break;

                    }


                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:77:25: (any= . )*
                    loop15:
                    do {
                        int alt15=2;
                        alt15 = dfa15.predict(input);
                        switch (alt15) {
                    	case 1 :
                    	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:77:26: any= .
                    	    {

                    	    any=(Token)input.LT(1);
                    	    matchAny(input);


                    	    					consequence = consequence + " " + any.getText();
                    	    				

                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);


                     rule.setConsequence( consequence ); 

                    }
                    break;

            }


            match(input,EOL,FOLLOW_EOL_in_rule346);

            match(input,23,FOLLOW_23_in_rule348);

            following.push(FOLLOW_opt_eol_in_rule350);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:87:1: rule_options : 'options' ( ':' )? opt_eol ( salience | no_loop ) opt_eol ( ( ',' )? opt_eol ( salience | no_loop ) )* opt_eol ;
    public void rule_options() throws RecognitionException {   




        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:88:17: ( 'options' ( ':' )? opt_eol ( salience | no_loop ) opt_eol ( ( ',' )? opt_eol ( salience | no_loop ) )* opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:88:17: 'options' ( ':' )? opt_eol ( salience | no_loop ) opt_eol ( ( ',' )? opt_eol ( salience | no_loop ) )* opt_eol
            {

            match(input,24,FOLLOW_24_in_rule_options361);

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:88:27: ( ':' )?
            int alt17=2;
            int LA17_0 = input.LA(1);
            if ( LA17_0==21 ) {
                alt17=1;
            }
            else if ( LA17_0==EOL||(LA17_0>=26 && LA17_0<=27) ) {
                alt17=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("88:27: ( \':\' )?", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:88:27: ':'
                    {

                    match(input,21,FOLLOW_21_in_rule_options363);

                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_rule_options366);
            opt_eol();
            following.pop();


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:89:25: ( salience | no_loop )
            int alt18=2;
            int LA18_0 = input.LA(1);
            if ( LA18_0==26 ) {
                alt18=1;
            }
            else if ( LA18_0==27 ) {
                alt18=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("89:25: ( salience | no_loop )", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:89:27: salience
                    {

                    following.push(FOLLOW_salience_in_rule_options373);
                    salience();
                    following.pop();


                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:89:38: no_loop
                    {

                    following.push(FOLLOW_no_loop_in_rule_options377);
                    no_loop();
                    following.pop();


                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_rule_options381);
            opt_eol();
            following.pop();


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:89:56: ( ( ',' )? opt_eol ( salience | no_loop ) )*
            loop21:
            do {
                int alt21=2;
                alt21 = dfa21.predict(input);
                switch (alt21) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:89:58: ( ',' )? opt_eol ( salience | no_loop )
            	    {

            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:89:58: ( ',' )?
            	    int alt19=2;
            	    int LA19_0 = input.LA(1);
            	    if ( LA19_0==25 ) {
            	        alt19=1;
            	    }
            	    else if ( LA19_0==EOL||(LA19_0>=26 && LA19_0<=27) ) {
            	        alt19=2;
            	    }
            	    else {

            	        NoViableAltException nvae =
            	            new NoViableAltException("89:58: ( \',\' )?", 19, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt19) {
            	        case 1 :
            	            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:89:58: ','
            	            {

            	            match(input,25,FOLLOW_25_in_rule_options385);

            	            }
            	            break;

            	    }


            	    following.push(FOLLOW_opt_eol_in_rule_options388);
            	    opt_eol();
            	    following.pop();


            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:89:71: ( salience | no_loop )
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
            	            new NoViableAltException("89:71: ( salience | no_loop )", 20, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt20) {
            	        case 1 :
            	            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:89:73: salience
            	            {

            	            following.push(FOLLOW_salience_in_rule_options392);
            	            salience();
            	            following.pop();


            	            }
            	            break;
            	        case 2 :
            	            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:89:84: no_loop
            	            {

            	            following.push(FOLLOW_no_loop_in_rule_options396);
            	            no_loop();
            	            following.pop();


            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);


            following.push(FOLLOW_opt_eol_in_rule_options403);
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
    // $ANTLR end rule_options



    // $ANTLR start salience
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:92:1: salience : 'salience' INT ( ';' )? opt_eol ;
    public void salience() throws RecognitionException {   




        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:94:17: ( 'salience' INT ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:94:17: 'salience' INT ( ';' )? opt_eol
            {

            match(input,26,FOLLOW_26_in_salience418);

            match(input,INT,FOLLOW_INT_in_salience420);

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:94:32: ( ';' )?
            int alt22=2;
            int LA22_0 = input.LA(1);
            if ( LA22_0==15 ) {
                alt22=1;
            }
            else if ( LA22_0==EOL||LA22_0==20||LA22_0==22||(LA22_0>=25 && LA22_0<=27) ) {
                alt22=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("94:32: ( \';\' )?", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:94:32: ';'
                    {

                    match(input,15,FOLLOW_15_in_salience422);

                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_salience425);
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
    // $ANTLR end salience



    // $ANTLR start no_loop
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:97:1: no_loop : 'no-loop' ( ';' )? opt_eol ;
    public void no_loop() throws RecognitionException {   




        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:99:17: ( 'no-loop' ( ';' )? opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:99:17: 'no-loop' ( ';' )? opt_eol
            {

            match(input,27,FOLLOW_27_in_no_loop439);

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:99:27: ( ';' )?
            int alt23=2;
            int LA23_0 = input.LA(1);
            if ( LA23_0==15 ) {
                alt23=1;
            }
            else if ( LA23_0==EOL||LA23_0==20||LA23_0==22||(LA23_0>=25 && LA23_0<=27) ) {
                alt23=2;
            }
            else {

                NoViableAltException nvae =
                    new NoViableAltException("99:27: ( \';\' )?", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:99:27: ';'
                    {

                    match(input,15,FOLLOW_15_in_no_loop441);

                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_no_loop444);
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
    // $ANTLR end no_loop



    // $ANTLR start lhs
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:103:1: lhs returns [PatternDescr d] : l= lhs_or ;
    public PatternDescr lhs() throws RecognitionException {   

        PatternDescr d;

        PatternDescr l = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:107:17: (l= lhs_or )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:107:17: l= lhs_or
            {

            following.push(FOLLOW_lhs_or_in_lhs470);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:111:1: lhs_column returns [ColumnDescr d] : ( fact_binding | fact );
    public ColumnDescr lhs_column() throws RecognitionException {   

        ColumnDescr d;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:115:17: ( fact_binding | fact )
            int alt24=2;
            alt24 = dfa24.predict(input);
            switch (alt24) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:115:17: fact_binding
                    {

                    following.push(FOLLOW_fact_binding_in_lhs_column495);
                    fact_binding();
                    following.pop();


                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:116:17: fact
                    {

                    following.push(FOLLOW_fact_in_lhs_column500);
                    fact();
                    following.pop();


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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:119:1: fact_binding returns [PatternDescr d] : ID opt_eol ':' opt_eol f= fact opt_eol ;
    public PatternDescr fact_binding() throws RecognitionException {   

        PatternDescr d;

        PatternDescr f = null;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:124:17: ( ID opt_eol ':' opt_eol f= fact opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:124:17: ID opt_eol ':' opt_eol f= fact opt_eol
            {

            match(input,ID,FOLLOW_ID_in_fact_binding527);

            following.push(FOLLOW_opt_eol_in_fact_binding529);
            opt_eol();
            following.pop();


            match(input,21,FOLLOW_21_in_fact_binding531);

            following.push(FOLLOW_opt_eol_in_fact_binding533);
            opt_eol();
            following.pop();


            following.push(FOLLOW_fact_in_fact_binding537);
            f=fact();
            following.pop();


             d=f; 

            following.push(FOLLOW_opt_eol_in_fact_binding541);
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
    // $ANTLR end fact_binding



    // $ANTLR start fact
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:127:1: fact returns [PatternDescr d] : ID opt_eol '(' opt_eol ( constraints )? opt_eol ')' opt_eol ;
    public PatternDescr fact() throws RecognitionException {   

        PatternDescr d;



        		d=null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:131:17: ( ID opt_eol '(' opt_eol ( constraints )? opt_eol ')' opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:131:17: ID opt_eol '(' opt_eol ( constraints )? opt_eol ')' opt_eol
            {

            match(input,ID,FOLLOW_ID_in_fact566);

            following.push(FOLLOW_opt_eol_in_fact568);
            opt_eol();
            following.pop();


            match(input,28,FOLLOW_28_in_fact570);

            following.push(FOLLOW_opt_eol_in_fact572);
            opt_eol();
            following.pop();


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:131:40: ( constraints )?
            int alt25=2;
            alt25 = dfa25.predict(input);
            switch (alt25) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:131:40: constraints
                    {

                    following.push(FOLLOW_constraints_in_fact574);
                    constraints();
                    following.pop();


                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_fact577);
            opt_eol();
            following.pop();


            match(input,29,FOLLOW_29_in_fact579);

            following.push(FOLLOW_opt_eol_in_fact581);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:135:1: constraints returns [List constraints] : opt_eol c= constraint ( opt_eol ',' opt_eol c= constraint )* opt_eol ;
    public List constraints() throws RecognitionException {   

        List constraints;

        PatternDescr c = null;



        		constraints = new ArrayList();
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:139:17: ( opt_eol c= constraint ( opt_eol ',' opt_eol c= constraint )* opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:139:17: opt_eol c= constraint ( opt_eol ',' opt_eol c= constraint )* opt_eol
            {

            following.push(FOLLOW_opt_eol_in_constraints607);
            opt_eol();
            following.pop();


            following.push(FOLLOW_constraint_in_constraints613);
            c=constraint();
            following.pop();


             constraints.add( c ); 

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:141:17: ( opt_eol ',' opt_eol c= constraint )*
            loop26:
            do {
                int alt26=2;
                alt26 = dfa26.predict(input);
                switch (alt26) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:141:19: opt_eol ',' opt_eol c= constraint
            	    {

            	    following.push(FOLLOW_opt_eol_in_constraints622);
            	    opt_eol();
            	    following.pop();


            	    match(input,25,FOLLOW_25_in_constraints624);

            	    following.push(FOLLOW_opt_eol_in_constraints626);
            	    opt_eol();
            	    following.pop();


            	    following.push(FOLLOW_constraint_in_constraints630);
            	    c=constraint();
            	    following.pop();


            	     constraints.add( c ); 

            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);


            following.push(FOLLOW_opt_eol_in_constraints639);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:145:1: constraint returns [PatternDescr d] : opt_eol f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!=') opt_eol (lc= literal_constraint | rvc= retval_constraint ) opt_eol ;
    public PatternDescr constraint() throws RecognitionException {   

        PatternDescr d;
        Token f=null;
        Token op=null;
        String lc = null;

        String rvc = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:149:17: ( opt_eol f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!=') opt_eol (lc= literal_constraint | rvc= retval_constraint ) opt_eol )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:149:17: opt_eol f= ID opt_eol op= ('=='|'>'|'>='|'<'|'<='|'!=') opt_eol (lc= literal_constraint | rvc= retval_constraint ) opt_eol
            {

            following.push(FOLLOW_opt_eol_in_constraint661);
            opt_eol();
            following.pop();


            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint667);

            following.push(FOLLOW_opt_eol_in_constraint669);
            opt_eol();
            following.pop();


            op=(Token)input.LT(1);
            if ( (input.LA(1)>=30 && input.LA(1)<=35) ) {
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint675);    throw mse;
            }


            following.push(FOLLOW_opt_eol_in_constraint717);
            opt_eol();
            following.pop();


            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:156:41: (lc= literal_constraint | rvc= retval_constraint )
            int alt27=2;
            switch ( input.LA(1) ) {
            case STRING:
                alt27=1;
                break;
            case INT:
                alt27=1;
                break;
            case FLOAT:
                alt27=1;
                break;
            case EOL:
            case ID:
            case WS:
            case SH_STYLE_SINGLE_LINE_COMMENT:
            case C_STYLE_SINGLE_LINE_COMMENT:
            case MULTI_LINE_COMMENT:
            case 13:
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
                alt27=2;
                break;
            default:

                NoViableAltException nvae =
                    new NoViableAltException("156:41: (lc= literal_constraint | rvc= retval_constraint )", 27, 0, input);

                throw nvae;
            }

            switch (alt27) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:156:49: lc= literal_constraint
                    {

                    following.push(FOLLOW_literal_constraint_in_constraint723);
                    lc=literal_constraint();
                    following.pop();


                     d = new LiteralDescr( f.getText(), null, lc ); 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:157:49: rvc= retval_constraint
                    {

                    following.push(FOLLOW_retval_constraint_in_constraint736);
                    rvc=retval_constraint();
                    following.pop();


                     d = new ReturnValueDescr( f.getText(), null, rvc ); 

                    }
                    break;

            }


            following.push(FOLLOW_opt_eol_in_constraint744);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:161:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT ) ;
    public String literal_constraint() throws RecognitionException {   

        String text;
        Token t=null;


        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:165:17: ( (t= STRING | t= INT | t= FLOAT ) )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:165:17: (t= STRING | t= INT | t= FLOAT )
            {

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:165:17: (t= STRING | t= INT | t= FLOAT )
            int alt28=3;
            switch ( input.LA(1) ) {
            case STRING:
                alt28=1;
                break;
            case INT:
                alt28=2;
                break;
            case FLOAT:
                alt28=3;
                break;
            default:

                NoViableAltException nvae =
                    new NoViableAltException("165:17: (t= STRING | t= INT | t= FLOAT )", 28, 0, input);

                throw nvae;
            }

            switch (alt28) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:165:25: t= STRING
                    {

                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint770);

                     text = t.getText(); text=text.substring( 1, text.length() - 1 ); 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:166:25: t= INT
                    {

                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint780);

                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:167:25: t= FLOAT
                    {

                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint793);

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:171:1: retval_constraint returns [String text] : c= chunk ;
    public String retval_constraint() throws RecognitionException {   

        String text;

        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:176:17: (c= chunk )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:176:17: c= chunk
            {

            following.push(FOLLOW_chunk_in_retval_constraint827);
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:179:1: chunk returns [String text] : ( (any= . ) | ( '(' c= chunk ')' ) )* ;
    public String chunk() throws RecognitionException {   

        String text;
        Token any=null;
        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:183:17: ( ( (any= . ) | ( '(' c= chunk ')' ) )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:183:17: ( (any= . ) | ( '(' c= chunk ')' ) )*
            {

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:183:17: ( (any= . ) | ( '(' c= chunk ')' ) )*
            loop29:
            do {
                int alt29=3;
                alt29 = dfa29.predict(input);
                switch (alt29) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:183:25: (any= . )
            	    {

            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:183:25: (any= . )
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:183:27: any= .
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
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:190:25: ( '(' c= chunk ')' )
            	    {

            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:190:25: ( '(' c= chunk ')' )
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:190:27: '(' c= chunk ')'
            	    {

            	    match(input,28,FOLLOW_28_in_chunk868);

            	    following.push(FOLLOW_chunk_in_chunk872);
            	    c=chunk();
            	    following.pop();


            	    match(input,29,FOLLOW_29_in_chunk874);


            	    							if ( text == null ) {
            	    								text = "( " + c + " )";
            	    							} else {
            	    								text = text + " ( " + c + " )";
            	    							}
            	    						

            	    }


            	    }
            	    break;

            	default :
            	    break loop29;
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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:201:1: field_binding : /* epsilon */ ;
    public void field_binding() throws RecognitionException {   




        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:203:9: ( /* epsilon */ )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:203:9: /* epsilon */
        {



        }



    }
    // $ANTLR end field_binding



    // $ANTLR start lhs_or
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:205:1: lhs_or returns [PatternDescr d] : left= lhs_and ( ('or'|'||')right= lhs_and )* ;
    public PatternDescr lhs_or() throws RecognitionException {   

        PatternDescr d;

        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:210:17: (left= lhs_and ( ('or'|'||')right= lhs_and )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:210:17: left= lhs_and ( ('or'|'||')right= lhs_and )*
            {

             OrDescr or = null; 

            following.push(FOLLOW_lhs_and_in_lhs_or926);
            left=lhs_and();
            following.pop();


            d = left; 

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:212:17: ( ('or'|'||')right= lhs_and )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);
                if ( (LA30_0>=36 && LA30_0<=37) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:212:25: ('or'|'||')right= lhs_and
            	    {

            	    if ( (input.LA(1)>=36 && input.LA(1)<=37) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or936);    throw mse;
            	    }


            	    following.push(FOLLOW_lhs_and_in_lhs_or947);
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

        return d;

    }
    // $ANTLR end lhs_or



    // $ANTLR start lhs_and
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:226:1: lhs_and returns [PatternDescr d] : left= lhs_unary ( ('and'|'&&')right= lhs_unary )* ;
    public PatternDescr lhs_and() throws RecognitionException {   

        PatternDescr d;

        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:231:17: (left= lhs_unary ( ('and'|'&&')right= lhs_unary )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:231:17: left= lhs_unary ( ('and'|'&&')right= lhs_unary )*
            {

             AndDescr and = null; 

            following.push(FOLLOW_lhs_unary_in_lhs_and987);
            left=lhs_unary();
            following.pop();


             d = left; 

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:233:17: ( ('and'|'&&')right= lhs_unary )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);
                if ( (LA31_0>=38 && LA31_0<=39) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:233:25: ('and'|'&&')right= lhs_unary
            	    {

            	    if ( (input.LA(1)>=38 && input.LA(1)<=39) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and996);    throw mse;
            	    }


            	    following.push(FOLLOW_lhs_unary_in_lhs_and1007);
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
    // $ANTLR end lhs_and



    // $ANTLR start lhs_unary
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:247:1: lhs_unary returns [PatternDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) ;
    public PatternDescr lhs_unary() throws RecognitionException {   

        PatternDescr d;

        PatternDescr u = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:251:17: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:251:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            {

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:251:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            int alt32=5;
            switch ( input.LA(1) ) {
            case 40:
                alt32=1;
                break;
            case 41:
                alt32=2;
                break;
            case 42:
                alt32=3;
                break;
            case ID:
                alt32=4;
                break;
            case 28:
                alt32=5;
                break;
            default:

                NoViableAltException nvae =
                    new NoViableAltException("251:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | \'(\' u= lhs \')\' )", 32, 0, input);

                throw nvae;
            }

            switch (alt32) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:251:25: u= lhs_exist
                    {

                    following.push(FOLLOW_lhs_exist_in_lhs_unary1045);
                    u=lhs_exist();
                    following.pop();


                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:252:25: u= lhs_not
                    {

                    following.push(FOLLOW_lhs_not_in_lhs_unary1053);
                    u=lhs_not();
                    following.pop();


                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:253:25: u= lhs_eval
                    {

                    following.push(FOLLOW_lhs_eval_in_lhs_unary1061);
                    u=lhs_eval();
                    following.pop();


                    }
                    break;
                case 4 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:254:25: u= lhs_column
                    {

                    following.push(FOLLOW_lhs_column_in_lhs_unary1069);
                    u=lhs_column();
                    following.pop();


                    }
                    break;
                case 5 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:255:25: '(' u= lhs ')'
                    {

                    match(input,28,FOLLOW_28_in_lhs_unary1075);

                    following.push(FOLLOW_lhs_in_lhs_unary1079);
                    u=lhs();
                    following.pop();


                    match(input,29,FOLLOW_29_in_lhs_unary1081);

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:259:1: lhs_exist returns [PatternDescr d] : 'exists' column= lhs_column ;
    public PatternDescr lhs_exist() throws RecognitionException {   

        PatternDescr d;

        ColumnDescr column = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:263:17: ( 'exists' column= lhs_column )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:263:17: 'exists' column= lhs_column
            {

            match(input,40,FOLLOW_40_in_lhs_exist1109);

            following.push(FOLLOW_lhs_column_in_lhs_exist1113);
            column=lhs_column();
            following.pop();


             d = new ExistsDescr( column ); 

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:266:1: lhs_not returns [NotDescr d] : 'not' column= lhs_column ;
    public NotDescr lhs_not() throws RecognitionException {   

        NotDescr d;

        ColumnDescr column = null;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:270:17: ( 'not' column= lhs_column )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:270:17: 'not' column= lhs_column
            {

            match(input,41,FOLLOW_41_in_lhs_not1138);

            following.push(FOLLOW_lhs_column_in_lhs_not1142);
            column=lhs_column();
            following.pop();


             d = new NotDescr( column ); 

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:273:1: lhs_eval returns [PatternDescr d] : 'eval' ;
    public PatternDescr lhs_eval() throws RecognitionException {   

        PatternDescr d;



        		d = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:277:17: ( 'eval' )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:277:17: 'eval'
            {

            match(input,42,FOLLOW_42_in_lhs_eval1165);

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
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:280:1: java_package_or_class returns [String name] : id= ID ( '.' id= ID )* ;
    public String java_package_or_class() throws RecognitionException {   

        String name;
        Token id=null;


        		name = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:285:17: (id= ID ( '.' id= ID )* )
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:285:17: id= ID ( '.' id= ID )*
            {

            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_java_package_or_class1194);

             name=id.getText(); 

            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:285:46: ( '.' id= ID )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);
                if ( LA33_0==14 ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:285:48: '.' id= ID
            	    {

            	    match(input,14,FOLLOW_14_in_java_package_or_class1200);

            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_java_package_or_class1204);

            	     name = name + "." + id.getText(); 

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

        return name;

    }
    // $ANTLR end java_package_or_class



    // $ANTLR start word
    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:289:1: word returns [String word] : (id= ID | 'import' | 'use' | 'rule' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING );
    public String word() throws RecognitionException {   

        String word;
        Token id=null;
        Token str=null;


        		word = null;
        	
        try {
            // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:293:17: (id= ID | 'import' | 'use' | 'rule' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING )
            int alt34=10;
            switch ( input.LA(1) ) {
            case ID:
                alt34=1;
                break;
            case 16:
                alt34=2;
                break;
            case 17:
                alt34=3;
                break;
            case 19:
                alt34=4;
                break;
            case 26:
                alt34=5;
                break;
            case 27:
                alt34=6;
                break;
            case 20:
                alt34=7;
                break;
            case 22:
                alt34=8;
                break;
            case 23:
                alt34=9;
                break;
            case STRING:
                alt34=10;
                break;
            default:

                NoViableAltException nvae =
                    new NoViableAltException("289:1: word returns [String word] : (id= ID | \'import\' | \'use\' | \'rule\' | \'salience\' | \'no-loop\' | \'when\' | \'then\' | \'end\' | str= STRING );", 34, 0, input);

                throw nvae;
            }

            switch (alt34) {
                case 1 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:293:17: id= ID
                    {

                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_word1234);

                     word=id.getText(); 

                    }
                    break;
                case 2 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:294:17: 'import'
                    {

                    match(input,16,FOLLOW_16_in_word1246);

                     word="import"; 

                    }
                    break;
                case 3 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:295:17: 'use'
                    {

                    match(input,17,FOLLOW_17_in_word1255);

                     word="use"; 

                    }
                    break;
                case 4 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:296:17: 'rule'
                    {

                    match(input,19,FOLLOW_19_in_word1267);

                     word="rule"; 

                    }
                    break;
                case 5 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:297:17: 'salience'
                    {

                    match(input,26,FOLLOW_26_in_word1278);

                     word="salience"; 

                    }
                    break;
                case 6 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:298:17: 'no-loop'
                    {

                    match(input,27,FOLLOW_27_in_word1286);

                     word="no-loop"; 

                    }
                    break;
                case 7 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:299:17: 'when'
                    {

                    match(input,20,FOLLOW_20_in_word1294);

                     word="when"; 

                    }
                    break;
                case 8 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:300:17: 'then'
                    {

                    match(input,22,FOLLOW_22_in_word1305);

                     word="then"; 

                    }
                    break;
                case 9 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:301:17: 'end'
                    {

                    match(input,23,FOLLOW_23_in_word1316);

                     word="end"; 

                    }
                    break;
                case 10 :
                    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/drl.g:302:17: str= STRING
                    {

                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_word1330);

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


    protected DFA15 dfa15 = new DFA15();protected DFA21 dfa21 = new DFA21();protected DFA24 dfa24 = new DFA24();protected DFA25 dfa25 = new DFA25();protected DFA26 dfa26 = new DFA26();protected DFA29 dfa29 = new DFA29();
    class DFA15 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s2 = new DFA.State() {{alt=1;}};
        DFA.State s8 = new DFA.State() {{alt=2;}};
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
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 13:
                case 14:
                case 15:
                case 18:
                case 21:
                case 24:
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

                case 16:
                    return s17;

                case 17:
                    return s18;

                case 19:
                    return s19;

                case 26:
                    return s20;

                case 27:
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
                        new NoViableAltException("", 15, 7, input);

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
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 13:
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
                    return s2;

                case -1:
                    return s8;

                case EOL:
                    return s6;

                case 19:
                    return s7;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 6, input);

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
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 13:
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
                    return s2;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 3, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA15_1 = input.LA(1);
                if ( LA15_1==23 ) {return s3;}
                if ( (LA15_1>=EOL && LA15_1<=22)||(LA15_1>=24 && LA15_1<=42) ) {return s2;}


                NoViableAltException nvae =
        	    new NoViableAltException("", 15, 1, input);

                throw nvae;
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA15_0 = input.LA(1);
                if ( LA15_0==EOL ) {return s1;}
                if ( (LA15_0>=ID && LA15_0<=42) ) {return s2;}


                NoViableAltException nvae =
        	    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
        };

    }class DFA21 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s4 = new DFA.State() {{alt=1;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 20:
                case 22:
                case 23:
                    return s2;

                case EOL:
                    return s1;

                case 26:
                case 27:
                    return s4;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 21, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s1;

                case 20:
                case 22:
                    return s2;

                case 25:
                case 26:
                case 27:
                    return s4;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 21, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA24 extends DFA {
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

                case 28:
                    return s4;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 24, 2, input);

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
                        new NoViableAltException("", 24, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA24_0 = input.LA(1);
                if ( LA24_0==ID ) {return s1;}


                NoViableAltException nvae =
        	    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }
        };

    }class DFA25 extends DFA {
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
                        new NoViableAltException("", 25, 1, input);

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
                        new NoViableAltException("", 25, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA26 extends DFA {
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

                case 25:
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

                case 29:
                    return s2;

                case 25:
                    return s3;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA29 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s1 = new DFA.State() {{alt=1;}};
        DFA.State s4 = new DFA.State() {{alt=1;}};
        DFA.State s22 = new DFA.State() {{alt=1;}};
        DFA.State s18 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA29_18 = input.LA(1);
                if ( LA29_18==EOL ) {return s18;}
                if ( (LA29_18>=ID && LA29_18<=42) ) {return s22;}


                NoViableAltException nvae =
        	    new NoViableAltException("", 29, 18, input);

                throw nvae;
            }
        };
        DFA.State s10 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s18;

                case 25:
                case 28:
                case 29:
                    return s4;

                case ID:
                case INT:
                case STRING:
                case FLOAT:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 13:
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
                case 26:
                case 27:
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
                    return s22;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 29, 10, input);

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
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 13:
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
                    return s4;

                case ID:
                    return s10;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 29, 6, input);

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
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 13:
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
                    return s4;

                case ID:
                    return s10;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 29, 2, input);

                    throw nvae;        }
            }
        };
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                    return s1;

                case 25:
                    return s2;

                case 29:
                    return s3;

                case ID:
                case INT:
                case STRING:
                case FLOAT:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 13:
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
                case 41:
                case 42:
                    return s4;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 29, 0, input);

                    throw nvae;        }
            }
        };

    }


    public static final BitSet FOLLOW_EOL_in_opt_eol40 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit53 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_rule_in_compilation_unit61 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_prolog77 = new BitSet(new long[]{8210L});
    public static final BitSet FOLLOW_package_statement_in_prolog85 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_prolog94 = new BitSet(new long[]{65554L});
    public static final BitSet FOLLOW_import_statement_in_prolog102 = new BitSet(new long[]{65554L});
    public static final BitSet FOLLOW_opt_eol_in_prolog111 = new BitSet(new long[]{131090L});
    public static final BitSet FOLLOW_use_expander_in_prolog115 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_prolog120 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_13_in_package_statement141 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_package_statement143 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_ID_in_package_statement147 = new BitSet(new long[]{49170L});
    public static final BitSet FOLLOW_14_in_package_statement153 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_ID_in_package_statement157 = new BitSet(new long[]{49170L});
    public static final BitSet FOLLOW_15_in_package_statement164 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_package_statement167 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_16_in_import_statement190 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_import_statement192 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_java_package_or_class_in_import_statement196 = new BitSet(new long[]{32786L});
    public static final BitSet FOLLOW_15_in_import_statement198 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_import_statement203 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_17_in_use_expander215 = new BitSet(new long[]{262144L});
    public static final BitSet FOLLOW_18_in_use_expander217 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_ID_in_use_expander219 = new BitSet(new long[]{32786L});
    public static final BitSet FOLLOW_15_in_use_expander221 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_use_expander224 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_opt_eol_in_rule248 = new BitSet(new long[]{524288L});
    public static final BitSet FOLLOW_19_in_rule252 = new BitSet(new long[]{215679136L});
    public static final BitSet FOLLOW_word_in_rule256 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_rule258 = new BitSet(new long[]{22020112L});
    public static final BitSet FOLLOW_rule_options_in_rule267 = new BitSet(new long[]{5242896L});
    public static final BitSet FOLLOW_20_in_rule274 = new BitSet(new long[]{2097170L});
    public static final BitSet FOLLOW_21_in_rule276 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_rule279 = new BitSet(new long[]{7696854024240L});
    public static final BitSet FOLLOW_lhs_in_rule293 = new BitSet(new long[]{7696854024240L});
    public static final BitSet FOLLOW_22_in_rule309 = new BitSet(new long[]{8796093022192L});
    public static final BitSet FOLLOW_21_in_rule311 = new BitSet(new long[]{8796093022192L});
    public static final BitSet FOLLOW_EOL_in_rule346 = new BitSet(new long[]{8388608L});
    public static final BitSet FOLLOW_23_in_rule348 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_rule350 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_24_in_rule_options361 = new BitSet(new long[]{2097170L});
    public static final BitSet FOLLOW_21_in_rule_options363 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_rule_options366 = new BitSet(new long[]{201326592L});
    public static final BitSet FOLLOW_salience_in_rule_options373 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_no_loop_in_rule_options377 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_rule_options381 = new BitSet(new long[]{33554450L});
    public static final BitSet FOLLOW_25_in_rule_options385 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_rule_options388 = new BitSet(new long[]{201326592L});
    public static final BitSet FOLLOW_salience_in_rule_options392 = new BitSet(new long[]{33554450L});
    public static final BitSet FOLLOW_no_loop_in_rule_options396 = new BitSet(new long[]{33554450L});
    public static final BitSet FOLLOW_opt_eol_in_rule_options403 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_26_in_salience418 = new BitSet(new long[]{64L});
    public static final BitSet FOLLOW_INT_in_salience420 = new BitSet(new long[]{32786L});
    public static final BitSet FOLLOW_15_in_salience422 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_salience425 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_27_in_no_loop439 = new BitSet(new long[]{32786L});
    public static final BitSet FOLLOW_15_in_no_loop441 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop444 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_lhs_or_in_lhs470 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column495 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_fact_in_lhs_column500 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_ID_in_fact_binding527 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding529 = new BitSet(new long[]{2097152L});
    public static final BitSet FOLLOW_21_in_fact_binding531 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding533 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_fact_in_fact_binding537 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding541 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_ID_in_fact566 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact568 = new BitSet(new long[]{268435456L});
    public static final BitSet FOLLOW_28_in_fact570 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact572 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_constraints_in_fact574 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact577 = new BitSet(new long[]{536870912L});
    public static final BitSet FOLLOW_29_in_fact579 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_fact581 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_opt_eol_in_constraints607 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_constraint_in_constraints613 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_constraints622 = new BitSet(new long[]{33554432L});
    public static final BitSet FOLLOW_25_in_constraints624 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_constraints626 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_constraint_in_constraints630 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_constraints639 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_opt_eol_in_constraint661 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_ID_in_constraint667 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_constraint669 = new BitSet(new long[]{67645734912L});
    public static final BitSet FOLLOW_set_in_constraint675 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_constraint717 = new BitSet(new long[]{8796093022194L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint723 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint736 = new BitSet(new long[]{18L});
    public static final BitSet FOLLOW_opt_eol_in_constraint744 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint770 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_INT_in_literal_constraint780 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint793 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_chunk_in_retval_constraint827 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_28_in_chunk868 = new BitSet(new long[]{8796093022194L});
    public static final BitSet FOLLOW_chunk_in_chunk872 = new BitSet(new long[]{536870912L});
    public static final BitSet FOLLOW_29_in_chunk874 = new BitSet(new long[]{8796093022194L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or926 = new BitSet(new long[]{206158430210L});
    public static final BitSet FOLLOW_set_in_lhs_or936 = new BitSet(new long[]{7696849829920L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or947 = new BitSet(new long[]{206158430210L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and987 = new BitSet(new long[]{824633720834L});
    public static final BitSet FOLLOW_set_in_lhs_and996 = new BitSet(new long[]{7696849829920L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1007 = new BitSet(new long[]{824633720834L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary1045 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary1053 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary1061 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary1069 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_28_in_lhs_unary1075 = new BitSet(new long[]{7696849829920L});
    public static final BitSet FOLLOW_lhs_in_lhs_unary1079 = new BitSet(new long[]{536870912L});
    public static final BitSet FOLLOW_29_in_lhs_unary1081 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_40_in_lhs_exist1109 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist1113 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_41_in_lhs_not1138 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not1142 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_42_in_lhs_eval1165 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_ID_in_java_package_or_class1194 = new BitSet(new long[]{16386L});
    public static final BitSet FOLLOW_14_in_java_package_or_class1200 = new BitSet(new long[]{32L});
    public static final BitSet FOLLOW_ID_in_java_package_or_class1204 = new BitSet(new long[]{16386L});
    public static final BitSet FOLLOW_ID_in_word1234 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_16_in_word1246 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_17_in_word1255 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_19_in_word1267 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_26_in_word1278 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_27_in_word1286 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_20_in_word1294 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_22_in_word1305 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_23_in_word1316 = new BitSet(new long[]{2L});
    public static final BitSet FOLLOW_STRING_in_word1330 = new BitSet(new long[]{2L});

}