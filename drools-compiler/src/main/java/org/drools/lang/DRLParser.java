// $ANTLR 3.0b7 /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g 2007-04-27 11:20:35

	package org.drools.lang;
	import java.util.List;
	import java.util.ArrayList;
	import java.util.Iterator;
	import java.util.HashMap;	
	import java.util.StringTokenizer;
	import org.drools.lang.descr.*;
	import org.drools.compiler.SwitchingCommonTokenStream;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class DRLParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ATTRIBUTES", "PACKAGE", "IMPORT", "FUNCTION", "GLOBAL", "QUERY", "END", "TEMPLATE", "RULE", "WHEN", "DATE_EFFECTIVE", "STRING", "DATE_EXPIRES", "ENABLED", "BOOL", "SALIENCE", "INT", "NO_LOOP", "AUTO_FOCUS", "ACTIVATION_GROUP", "RULEFLOW_GROUP", "AGENDA_GROUP", "DURATION", "DIALECT", "LOCK_ON_ACTIVE", "ACCUMULATE", "INIT", "ACTION", "RESULT", "COLLECT", "ID", "OR", "LEFT_PAREN", "RIGHT_PAREN", "CONTAINS", "MATCHES", "EXCLUDES", "FLOAT", "NULL", "LEFT_CURLY", "RIGHT_CURLY", "LEFT_SQUARE", "RIGHT_SQUARE", "AND", "FROM", "EXISTS", "NOT", "EVAL", "FORALL", "THEN", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "MISC", "';'", "':'", "','", "'.'", "'.*'", "'||'", "'&'", "'|'", "'->'", "'=='", "'>'", "'>='", "'<'", "'<='", "'!='", "'&&'"
    };
    public static final int EXISTS=49;
    public static final int AUTO_FOCUS=22;
    public static final int END=10;
    public static final int HexDigit=57;
    public static final int FORALL=52;
    public static final int TEMPLATE=11;
    public static final int MISC=63;
    public static final int FLOAT=41;
    public static final int QUERY=9;
    public static final int THEN=53;
    public static final int RULE=12;
    public static final int INIT=30;
    public static final int IMPORT=6;
    public static final int DATE_EFFECTIVE=14;
    public static final int PACKAGE=5;
    public static final int OR=35;
    public static final int AND=47;
    public static final int FUNCTION=7;
    public static final int GLOBAL=8;
    public static final int EscapeSequence=56;
    public static final int DIALECT=27;
    public static final int INT=20;
    public static final int LOCK_ON_ACTIVE=28;
    public static final int DATE_EXPIRES=16;
    public static final int LEFT_SQUARE=45;
    public static final int CONTAINS=38;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=60;
    public static final int ATTRIBUTES=4;
    public static final int RESULT=32;
    public static final int LEFT_CURLY=43;
    public static final int FROM=48;
    public static final int ID=34;
    public static final int ACTIVATION_GROUP=23;
    public static final int LEFT_PAREN=36;
    public static final int RIGHT_CURLY=44;
    public static final int BOOL=18;
    public static final int EXCLUDES=40;
    public static final int WHEN=13;
    public static final int RULEFLOW_GROUP=24;
    public static final int WS=55;
    public static final int STRING=15;
    public static final int ACTION=31;
    public static final int COLLECT=33;
    public static final int NO_LOOP=21;
    public static final int ACCUMULATE=29;
    public static final int UnicodeEscape=58;
    public static final int DURATION=26;
    public static final int EVAL=51;
    public static final int MATCHES=39;
    public static final int EOF=-1;
    public static final int EOL=54;
    public static final int AGENDA_GROUP=25;
    public static final int NULL=42;
    public static final int OctalEscape=59;
    public static final int SALIENCE=19;
    public static final int MULTI_LINE_COMMENT=62;
    public static final int NOT=50;
    public static final int RIGHT_PAREN=37;
    public static final int ENABLED=17;
    public static final int RIGHT_SQUARE=46;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=61;

        public DRLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[197+1];
         }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g"; }


    	private PackageDescr packageDescr;
    	private List errors = new ArrayList();
    	private String source = "unknown";
    	private int lineOffset = 0;
    	private DescrFactory factory = new DescrFactory();
    	private boolean parserDebug = false;
    	
    	// THE FOLLOWING LINE IS A DUMMY ATTRIBUTE TO WORK AROUND AN ANTLR BUG
    	private BaseDescr from = null;
    	
    	public void setParserDebug(boolean parserDebug) {
    		this.parserDebug = parserDebug;
    	}
    	
    	public void debug(String message) {
    		if ( parserDebug ) 
    			System.err.println( "drl parser: " + message );
    	}
    	
    	public void setSource(String source) {
    		this.source = source;
    	}
    	public DescrFactory getFactory() {
    		return factory;
    	}	

    	public String getSource() {
    		return this.source;
    	}
    	
    	public PackageDescr getPackageDescr() {
    		return packageDescr;
    	}
    	
    	private int offset(int line) {
    		return line + lineOffset;
    	}
    	
    	/**
    	 * This will set the offset to record when reparsing. Normally is zero of course 
    	 */
    	public void setLineOffset(int i) {
    	 	this.lineOffset = i;
    	}
    	
    	private String getString(Token token) {
    		String orig = token.getText();
    		return orig.substring( 1, orig.length() -1 );
    	}
    	
    	public void reportError(RecognitionException ex) {
    	        // if we've already reported an error and have not matched a token
                    // yet successfully, don't report any errors.
                    if ( errorRecovery ) {
                            return;
                    }
                    errorRecovery = true;

    		ex.line = offset(ex.line); //add the offset if there is one
    		errors.add( ex ); 
    	}
         	
         	/** return the raw RecognitionException errors */
         	public List getErrors() {
         		return errors;
         	}
         	
         	/** Return a list of pretty strings summarising the errors */
         	public List getErrorMessages() {
         		List messages = new ArrayList();
     		for ( Iterator errorIter = errors.iterator() ; errorIter.hasNext() ; ) {
         	     		messages.add( createErrorMessage( (RecognitionException) errorIter.next() ) );
         	     	}
         	     	return messages;
         	}
         	
         	/** return true if any parser errors were accumulated */
         	public boolean hasErrors() {
      		return ! errors.isEmpty();
         	}
         	
         	/** This will take a RecognitionException, and create a sensible error message out of it */
         	public String createErrorMessage(RecognitionException e)
            {
    		StringBuffer message = new StringBuffer();		
                    message.append( source + ":"+e.line+":"+e.charPositionInLine+" ");
                    if ( e instanceof MismatchedTokenException ) {
                            MismatchedTokenException mte = (MismatchedTokenException)e;
                            message.append("mismatched token: "+
                                                               e.token+
                                                               "; expecting type "+
                                                               tokenNames[mte.expecting]);
                    }
                    else if ( e instanceof MismatchedTreeNodeException ) {
                            MismatchedTreeNodeException mtne = (MismatchedTreeNodeException)e;
                            message.append("mismatched tree node: "+
                                                               mtne.toString() +
                                                               "; expecting type "+
                                                               tokenNames[mtne.expecting]);
                    }
                    else if ( e instanceof NoViableAltException ) {
                            NoViableAltException nvae = (NoViableAltException)e;
    			message.append( "Unexpected token '" + e.token.getText() + "'" );
                            /*
                            message.append("decision=<<"+nvae.grammarDecisionDescription+">>"+
                                                               " state "+nvae.stateNumber+
                                                               " (decision="+nvae.decisionNumber+
                                                               ") no viable alt; token="+
                                                               e.token);
                                                               */
                    }
                    else if ( e instanceof EarlyExitException ) {
                            EarlyExitException eee = (EarlyExitException)e;
                            message.append("required (...)+ loop (decision="+
                                                               eee.decisionNumber+
                                                               ") did not match anything; token="+
                                                               e.token);
                    }
                    else if ( e instanceof MismatchedSetException ) {
                            MismatchedSetException mse = (MismatchedSetException)e;
                            message.append("mismatched token '"+
                                                               e.token+
                                                               "' expecting set "+mse.expecting);
                    }
                    else if ( e instanceof MismatchedNotSetException ) {
                            MismatchedNotSetException mse = (MismatchedNotSetException)e;
                            message.append("mismatched token '"+
                                                               e.token+
                                                               "' expecting set "+mse.expecting);
                    }
                    else if ( e instanceof FailedPredicateException ) {
                            FailedPredicateException fpe = (FailedPredicateException)e;
                            message.append("rule "+fpe.ruleName+" failed predicate: {"+
                                                               fpe.predicateText+"}?");
                    } else if (e instanceof GeneralParseException) {
    			message.append(" " + e.getMessage());
    		}
                   	return message.toString();
            }   
            
            void checkTrailingSemicolon(String text, int line) {
            	if (text.trim().endsWith( ";" ) ) {
            		this.errors.add( new GeneralParseException( "Trailing semi-colon not allowed", offset(line) ) );
            	}
            }
          



    // $ANTLR start opt_semicolon
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:168:1: opt_semicolon : ( ';' )? ;
    public final void opt_semicolon() throws RecognitionException {
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:169:4: ( ( ';' )? )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:169:4: ( ';' )?
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:169:4: ( ';' )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==64) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: ';'
                    {
                    match(input,64,FOLLOW_64_in_opt_semicolon46); if (failed) return ;

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
        return ;
    }
    // $ANTLR end opt_semicolon


    // $ANTLR start compilation_unit
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:172:1: compilation_unit : prolog ( statement )+ ;
    public final void compilation_unit() throws RecognitionException {
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:173:4: ( prolog ( statement )+ )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:173:4: prolog ( statement )+
            {
            pushFollow(FOLLOW_prolog_in_compilation_unit58);
            prolog();
            _fsp--;
            if (failed) return ;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:174:3: ( statement )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>=IMPORT && LA2_0<=QUERY)||(LA2_0>=TEMPLATE && LA2_0<=RULE)) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:174:5: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_compilation_unit65);
            	    statement();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:177:1: prolog : (n= package_statement )? ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )* ;
    public final void prolog() throws RecognitionException {
        String n = null;

        AttributeDescr a = null;



        		String packageName = "";
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:181:4: ( (n= package_statement )? ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:181:4: (n= package_statement )? ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )*
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:181:4: (n= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==PACKAGE) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:181:6: n= package_statement
                    {
                    pushFollow(FOLLOW_package_statement_in_prolog90);
                    n=package_statement();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       packageName = n; 
                    }

                    }
                    break;

            }

            if ( backtracking==0 ) {
               
              			this.packageDescr = factory.createPackage( packageName ); 
              		
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:185:4: ( ATTRIBUTES ':' )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==ATTRIBUTES) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:185:5: ATTRIBUTES ':'
                    {
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_prolog105); if (failed) return ;
                    match(input,65,FOLLOW_65_in_prolog107); if (failed) return ;

                    }
                    break;

            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:186:4: ( ( ',' )? a= rule_attribute )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==DATE_EFFECTIVE||(LA6_0>=DATE_EXPIRES && LA6_0<=ENABLED)||LA6_0==SALIENCE||(LA6_0>=NO_LOOP && LA6_0<=LOCK_ON_ACTIVE)||LA6_0==66) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:186:6: ( ',' )? a= rule_attribute
            	    {
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:186:6: ( ',' )?
            	    int alt5=2;
            	    int LA5_0 = input.LA(1);

            	    if ( (LA5_0==66) ) {
            	        alt5=1;
            	    }
            	    switch (alt5) {
            	        case 1 :
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: ','
            	            {
            	            match(input,66,FOLLOW_66_in_prolog116); if (failed) return ;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_rule_attribute_in_prolog121);
            	    a=rule_attribute();
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {

            	      					this.packageDescr.addAttribute( a );
            	      				
            	    }

            	    }
            	    break;

            	default :
            	    break loop6;
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
    // $ANTLR end prolog


    // $ANTLR start statement
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:193:1: statement : ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query ) ;
    public final void statement() throws RecognitionException {
        FactTemplateDescr t = null;

        RuleDescr r = null;

        QueryDescr q = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:195:2: ( ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:195:2: ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:195:2: ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query )
            int alt7=7;
            switch ( input.LA(1) ) {
            case IMPORT:
                {
                int LA7_1 = input.LA(2);

                if ( (synpred7()) ) {
                    alt7=1;
                }
                else if ( (synpred8()) ) {
                    alt7=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("195:2: ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query )", 7, 1, input);

                    throw nvae;
                }
                }
                break;
            case GLOBAL:
                {
                alt7=3;
                }
                break;
            case FUNCTION:
                {
                alt7=4;
                }
                break;
            case TEMPLATE:
                {
                alt7=5;
                }
                break;
            case RULE:
                {
                alt7=6;
                }
                break;
            case QUERY:
                {
                alt7=7;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("195:2: ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query )", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:195:4: function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement150);
                    function_import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:196:4: import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement156);
                    import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:197:4: global
                    {
                    pushFollow(FOLLOW_global_in_statement162);
                    global();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:198:4: function
                    {
                    pushFollow(FOLLOW_function_in_statement168);
                    function();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:199:10: t= template
                    {
                    pushFollow(FOLLOW_template_in_statement182);
                    t=template();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      this.packageDescr.addFactTemplate( t ); 
                    }

                    }
                    break;
                case 6 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:200:4: r= rule
                    {
                    pushFollow(FOLLOW_rule_in_statement191);
                    r=rule();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       if( r != null ) this.packageDescr.addRule( r ); 
                    }

                    }
                    break;
                case 7 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:201:4: q= query
                    {
                    pushFollow(FOLLOW_query_in_statement203);
                    q=query();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       if( q != null ) this.packageDescr.addRule( q ); 
                    }

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
        return ;
    }
    // $ANTLR end statement


    // $ANTLR start package_statement
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:205:1: package_statement returns [String packageName] : PACKAGE n= dotted_name[null] opt_semicolon ;
    public final String package_statement() throws RecognitionException {
        String packageName = null;

        String n = null;



        		packageName = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:210:3: ( PACKAGE n= dotted_name[null] opt_semicolon )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:210:3: PACKAGE n= dotted_name[null] opt_semicolon
            {
            match(input,PACKAGE,FOLLOW_PACKAGE_in_package_statement232); if (failed) return packageName;
            pushFollow(FOLLOW_dotted_name_in_package_statement236);
            n=dotted_name(null);
            _fsp--;
            if (failed) return packageName;
            pushFollow(FOLLOW_opt_semicolon_in_package_statement239);
            opt_semicolon();
            _fsp--;
            if (failed) return packageName;
            if ( backtracking==0 ) {

              			packageName = n;
              		
            }

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:217:1: import_statement : imp= IMPORT import_name[importDecl] opt_semicolon ;
    public final void import_statement() throws RecognitionException {
        Token imp=null;


                	ImportDescr importDecl = null;
                
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:221:4: (imp= IMPORT import_name[importDecl] opt_semicolon )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:221:4: imp= IMPORT import_name[importDecl] opt_semicolon
            {
            imp=(Token)input.LT(1);
            match(input,IMPORT,FOLLOW_IMPORT_in_import_statement271); if (failed) return ;
            if ( backtracking==0 ) {

              	            importDecl = factory.createImport( );
              	            importDecl.setStartCharacter( ((CommonToken)imp).getStartIndex() );
              		    if (packageDescr != null) {
              			packageDescr.addImport( importDecl );
              		    }
              	        
            }
            pushFollow(FOLLOW_import_name_in_import_statement294);
            import_name(importDecl);
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_import_statement297);
            opt_semicolon();
            _fsp--;
            if (failed) return ;

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


    // $ANTLR start function_import_statement
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:232:1: function_import_statement : imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon ;
    public final void function_import_statement() throws RecognitionException {
        Token imp=null;


                	FunctionImportDescr importDecl = null;
                
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:236:4: (imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:236:4: imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon
            {
            imp=(Token)input.LT(1);
            match(input,IMPORT,FOLLOW_IMPORT_in_function_import_statement323); if (failed) return ;
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function_import_statement325); if (failed) return ;
            if ( backtracking==0 ) {

              	            importDecl = factory.createFunctionImport();
              	            importDecl.setStartCharacter( ((CommonToken)imp).getStartIndex() );
              		    if (packageDescr != null) {
              			packageDescr.addFunctionImport( importDecl );
              		    }
              	        
            }
            pushFollow(FOLLOW_import_name_in_function_import_statement348);
            import_name(importDecl);
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_function_import_statement351);
            opt_semicolon();
            _fsp--;
            if (failed) return ;

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
    // $ANTLR end function_import_statement


    // $ANTLR start import_name
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:248:1: import_name[ImportDescr importDecl] returns [String name] : id= identifier ( '.' id= identifier )* (star= '.*' )? ;
    public final String import_name(ImportDescr importDecl) throws RecognitionException {
        String name = null;

        Token star=null;
        Token id = null;



        		name = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:253:3: (id= identifier ( '.' id= identifier )* (star= '.*' )? )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:253:3: id= identifier ( '.' id= identifier )* (star= '.*' )?
            {
            pushFollow(FOLLOW_identifier_in_import_name379);
            id=identifier();
            _fsp--;
            if (failed) return name;
            if ( backtracking==0 ) {
               
              		    name=id.getText(); 
              		    importDecl.setTarget( name );
              		    importDecl.setEndCharacter( ((CommonToken)id).getStopIndex() );
              		
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:259:3: ( '.' id= identifier )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==67) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:259:5: '.' id= identifier
            	    {
            	    match(input,67,FOLLOW_67_in_import_name391); if (failed) return name;
            	    pushFollow(FOLLOW_identifier_in_import_name395);
            	    id=identifier();
            	    _fsp--;
            	    if (failed) return name;
            	    if ( backtracking==0 ) {
            	       
            	      		        name = name + "." + id.getText(); 
            	      			importDecl.setTarget( name );
            	      		        importDecl.setEndCharacter( ((CommonToken)id).getStopIndex() );
            	      		    
            	    }

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:266:3: (star= '.*' )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==68) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:266:5: star= '.*'
                    {
                    star=(Token)input.LT(1);
                    match(input,68,FOLLOW_68_in_import_name419); if (failed) return name;
                    if ( backtracking==0 ) {
                       
                      		        name = name + star.getText(); 
                      			importDecl.setTarget( name );
                      		        importDecl.setEndCharacter( ((CommonToken)star).getStopIndex() );
                      		    
                    }

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
        return name;
    }
    // $ANTLR end import_name


    // $ANTLR start global
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:276:1: global : loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon ;
    public final void global() throws RecognitionException {
        Token loc=null;
        String type = null;

        Token id = null;



        	    GlobalDescr global = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:281:3: (loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:281:3: loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,GLOBAL,FOLLOW_GLOBAL_in_global455); if (failed) return ;
            if ( backtracking==0 ) {

              		    global = factory.createGlobal();
              	            global.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		    packageDescr.addGlobal( global );
              		
            }
            pushFollow(FOLLOW_dotted_name_in_global466);
            type=dotted_name(null);
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              		    global.setType( type );
              		
            }
            pushFollow(FOLLOW_identifier_in_global478);
            id=identifier();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_global480);
            opt_semicolon();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              		    global.setIdentifier( id.getText() );
              		    global.setEndCharacter( ((CommonToken)id).getStopIndex() );
              		
            }

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:299:1: function : loc= FUNCTION (retType= dotted_name[null] )? n= identifier '(' ( (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f] ;
    public final void function() throws RecognitionException {
        Token loc=null;
        String retType = null;

        Token n = null;

        String paramType = null;

        String paramName = null;

        String body = null;



        		FunctionDescr f = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:304:3: (loc= FUNCTION (retType= dotted_name[null] )? n= identifier '(' ( (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f] )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:304:3: loc= FUNCTION (retType= dotted_name[null] )? n= identifier '(' ( (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f]
            {
            loc=(Token)input.LT(1);
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function507); if (failed) return ;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:304:16: (retType= dotted_name[null] )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==ID) ) {
                int LA10_1 = input.LA(2);

                if ( ((LA10_1>=ATTRIBUTES && LA10_1<=WHEN)||LA10_1==ENABLED||LA10_1==SALIENCE||LA10_1==DURATION||(LA10_1>=ACCUMULATE && LA10_1<=OR)||(LA10_1>=CONTAINS && LA10_1<=EXCLUDES)||LA10_1==NULL||LA10_1==LEFT_SQUARE||(LA10_1>=AND && LA10_1<=THEN)||LA10_1==67) ) {
                    alt10=1;
                }
            }
            switch (alt10) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:304:17: retType= dotted_name[null]
                    {
                    pushFollow(FOLLOW_dotted_name_in_function512);
                    retType=dotted_name(null);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_identifier_in_function519);
            n=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              			//System.err.println( "function :: " + n.getText() );
              			f = factory.createFunction( n.getText(), retType );
              			f.setLocation(offset(loc.getLine()), loc.getCharPositionInLine());
              	        	f.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			packageDescr.addFunction( f );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_function528); if (failed) return ;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:313:4: ( (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )* )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( ((LA14_0>=ATTRIBUTES && LA14_0<=WHEN)||LA14_0==ENABLED||LA14_0==SALIENCE||LA14_0==DURATION||(LA14_0>=ACCUMULATE && LA14_0<=OR)||(LA14_0>=CONTAINS && LA14_0<=EXCLUDES)||LA14_0==NULL||(LA14_0>=AND && LA14_0<=THEN)) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:313:6: (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )*
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:313:6: (paramType= dotted_name[null] )?
                    int alt11=2;
                    alt11 = dfa11.predict(input);
                    switch (alt11) {
                        case 1 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:313:7: paramType= dotted_name[null]
                            {
                            pushFollow(FOLLOW_dotted_name_in_function538);
                            paramType=dotted_name(null);
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_argument_in_function545);
                    paramName=argument();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      					f.addParameter( paramType, paramName );
                      				
                    }
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:317:5: ( ',' (paramType= dotted_name[null] )? paramName= argument )*
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( (LA13_0==66) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:317:7: ',' (paramType= dotted_name[null] )? paramName= argument
                    	    {
                    	    match(input,66,FOLLOW_66_in_function559); if (failed) return ;
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:317:11: (paramType= dotted_name[null] )?
                    	    int alt12=2;
                    	    alt12 = dfa12.predict(input);
                    	    switch (alt12) {
                    	        case 1 :
                    	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:317:12: paramType= dotted_name[null]
                    	            {
                    	            pushFollow(FOLLOW_dotted_name_in_function564);
                    	            paramType=dotted_name(null);
                    	            _fsp--;
                    	            if (failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_argument_in_function571);
                    	    paramName=argument();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    if ( backtracking==0 ) {

                    	      						f.addParameter( paramType, paramName );
                    	      					
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop13;
                        }
                    } while (true);


                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_function595); if (failed) return ;
            pushFollow(FOLLOW_curly_chunk_in_function601);
            body=curly_chunk(f);
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              			//strip out '{','}'
              			f.setText( body.substring( 1, body.length()-1 ) );
              		
            }

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:333:1: query returns [QueryDescr query] : loc= QUERY queryName= name ( normal_lhs_block[lhs] ) loc= END ;
    public final QueryDescr query() throws RecognitionException {
        QueryDescr query = null;

        Token loc=null;
        String queryName = null;



        		query = null;
        		AndDescr lhs = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:339:3: (loc= QUERY queryName= name ( normal_lhs_block[lhs] ) loc= END )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:339:3: loc= QUERY queryName= name ( normal_lhs_block[lhs] ) loc= END
            {
            loc=(Token)input.LT(1);
            match(input,QUERY,FOLLOW_QUERY_in_query633); if (failed) return query;
            pushFollow(FOLLOW_name_in_query637);
            queryName=name();
            _fsp--;
            if (failed) return query;
            if ( backtracking==0 ) {
               
              			query = factory.createQuery( queryName ); 
              			query.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			query.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			lhs = new AndDescr(); query.setLhs( lhs ); 
              			lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:347:3: ( normal_lhs_block[lhs] )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:348:4: normal_lhs_block[lhs]
            {
            pushFollow(FOLLOW_normal_lhs_block_in_query650);
            normal_lhs_block(lhs);
            _fsp--;
            if (failed) return query;

            }

            loc=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_query667); if (failed) return query;
            if ( backtracking==0 ) {

              			query.setEndCharacter( ((CommonToken)loc).getStopIndex() );
              		
            }

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


    // $ANTLR start template
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:358:1: template returns [FactTemplateDescr template] : loc= TEMPLATE templateName= identifier opt_semicolon (slot= template_slot )+ loc= END opt_semicolon ;
    public final FactTemplateDescr template() throws RecognitionException {
        FactTemplateDescr template = null;

        Token loc=null;
        Token templateName = null;

        FieldTemplateDescr slot = null;



        		template = null;		
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:363:3: (loc= TEMPLATE templateName= identifier opt_semicolon (slot= template_slot )+ loc= END opt_semicolon )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:363:3: loc= TEMPLATE templateName= identifier opt_semicolon (slot= template_slot )+ loc= END opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,TEMPLATE,FOLLOW_TEMPLATE_in_template697); if (failed) return template;
            pushFollow(FOLLOW_identifier_in_template701);
            templateName=identifier();
            _fsp--;
            if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template703);
            opt_semicolon();
            _fsp--;
            if (failed) return template;
            if ( backtracking==0 ) {

              			template = new FactTemplateDescr(templateName.getText());
              			template.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );			
              			template.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:369:3: (slot= template_slot )+
            int cnt15=0;
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==ID) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:370:4: slot= template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template718);
            	    slot=template_slot();
            	    _fsp--;
            	    if (failed) return template;
            	    if ( backtracking==0 ) {

            	      				template.addFieldTemplate(slot);
            	      			
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt15 >= 1 ) break loop15;
            	    if (backtracking>0) {failed=true; return template;}
                        EarlyExitException eee =
                            new EarlyExitException(15, input);
                        throw eee;
                }
                cnt15++;
            } while (true);

            loc=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_template735); if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template737);
            opt_semicolon();
            _fsp--;
            if (failed) return template;
            if ( backtracking==0 ) {

              			template.setEndCharacter( ((CommonToken)loc).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return template;
    }
    // $ANTLR end template


    // $ANTLR start template_slot
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:381:1: template_slot returns [FieldTemplateDescr field] : fieldType= dotted_name[field] n= identifier opt_semicolon ;
    public final FieldTemplateDescr template_slot() throws RecognitionException {
        FieldTemplateDescr field = null;

        String fieldType = null;

        Token n = null;



        		field = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:386:11: (fieldType= dotted_name[field] n= identifier opt_semicolon )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:386:11: fieldType= dotted_name[field] n= identifier opt_semicolon
            {
            if ( backtracking==0 ) {

              			field = factory.createFieldTemplate();
              	         
            }
            pushFollow(FOLLOW_dotted_name_in_template_slot783);
            fieldType=dotted_name(field);
            _fsp--;
            if (failed) return field;
            if ( backtracking==0 ) {

              		        field.setClassType( fieldType );
              		 
            }
            pushFollow(FOLLOW_identifier_in_template_slot801);
            n=identifier();
            _fsp--;
            if (failed) return field;
            pushFollow(FOLLOW_opt_semicolon_in_template_slot803);
            opt_semicolon();
            _fsp--;
            if (failed) return field;
            if ( backtracking==0 ) {

              		        field.setName( n.getText() );
              			field.setLocation( offset(n.getLine()), n.getCharPositionInLine() );
              			field.setEndCharacter( ((CommonToken)n).getStopIndex() );
              		 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return field;
    }
    // $ANTLR end template_slot


    // $ANTLR start rule
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:402:1: rule returns [RuleDescr rule] : loc= RULE ruleName= name rule_attributes[rule] (loc= WHEN ( ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule] ;
    public final RuleDescr rule() throws RecognitionException {
        RuleDescr rule = null;

        Token loc=null;
        String ruleName = null;



        		rule = null;
        		String consequence = "";
        		AndDescr lhs = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:409:3: (loc= RULE ruleName= name rule_attributes[rule] (loc= WHEN ( ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule] )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:409:3: loc= RULE ruleName= name rule_attributes[rule] (loc= WHEN ( ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule]
            {
            loc=(Token)input.LT(1);
            match(input,RULE,FOLLOW_RULE_in_rule836); if (failed) return rule;
            pushFollow(FOLLOW_name_in_rule840);
            ruleName=name();
            _fsp--;
            if (failed) return rule;
            if ( backtracking==0 ) {
               
              			debug( "start rule: " + ruleName );
              			rule = new RuleDescr( ruleName, null ); 
              			rule.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			rule.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            pushFollow(FOLLOW_rule_attributes_in_rule849);
            rule_attributes(rule);
            _fsp--;
            if (failed) return rule;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:417:3: (loc= WHEN ( ':' )? ( normal_lhs_block[lhs] ) )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==WHEN) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:417:5: loc= WHEN ( ':' )? ( normal_lhs_block[lhs] )
                    {
                    loc=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_rule858); if (failed) return rule;
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:417:14: ( ':' )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==65) ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: ':'
                            {
                            match(input,65,FOLLOW_65_in_rule860); if (failed) return rule;

                            }
                            break;

                    }

                    if ( backtracking==0 ) {
                       
                      				lhs = new AndDescr(); rule.setLhs( lhs ); 
                      				lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      				lhs.setStartCharacter( ((CommonToken)loc).getStartIndex() );
                      			
                    }
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:423:4: ( normal_lhs_block[lhs] )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:425:5: normal_lhs_block[lhs]
                    {
                    pushFollow(FOLLOW_normal_lhs_block_in_rule878);
                    normal_lhs_block(lhs);
                    _fsp--;
                    if (failed) return rule;

                    }


                    }
                    break;

            }

            pushFollow(FOLLOW_rhs_chunk_in_rule899);
            rhs_chunk(rule);
            _fsp--;
            if (failed) return rule;

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:434:1: rule_attributes[RuleDescr rule] : ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )* ;
    public final void rule_attributes(RuleDescr rule) throws RecognitionException {
        AttributeDescr a = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:436:4: ( ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:436:4: ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )*
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:436:4: ( ATTRIBUTES ':' )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==ATTRIBUTES) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:436:5: ATTRIBUTES ':'
                    {
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_rule_attributes920); if (failed) return ;
                    match(input,65,FOLLOW_65_in_rule_attributes922); if (failed) return ;

                    }
                    break;

            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:437:4: ( ( ',' )? a= rule_attribute )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==DATE_EFFECTIVE||(LA20_0>=DATE_EXPIRES && LA20_0<=ENABLED)||LA20_0==SALIENCE||(LA20_0>=NO_LOOP && LA20_0<=LOCK_ON_ACTIVE)||LA20_0==66) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:437:6: ( ',' )? a= rule_attribute
            	    {
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:437:6: ( ',' )?
            	    int alt19=2;
            	    int LA19_0 = input.LA(1);

            	    if ( (LA19_0==66) ) {
            	        alt19=1;
            	    }
            	    switch (alt19) {
            	        case 1 :
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: ','
            	            {
            	            match(input,66,FOLLOW_66_in_rule_attributes931); if (failed) return ;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes936);
            	    a=rule_attribute();
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {

            	      					rule.addAttribute( a );
            	      				
            	    }

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
        return ;
    }
    // $ANTLR end rule_attributes


    // $ANTLR start rule_attribute
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:446:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect );
    public final AttributeDescr rule_attribute() throws RecognitionException {
        AttributeDescr d = null;

        AttributeDescr a = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:451:4: (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect )
            int alt21=12;
            switch ( input.LA(1) ) {
            case SALIENCE:
                {
                alt21=1;
                }
                break;
            case NO_LOOP:
                {
                alt21=2;
                }
                break;
            case AGENDA_GROUP:
                {
                alt21=3;
                }
                break;
            case DURATION:
                {
                alt21=4;
                }
                break;
            case ACTIVATION_GROUP:
                {
                alt21=5;
                }
                break;
            case AUTO_FOCUS:
                {
                alt21=6;
                }
                break;
            case DATE_EFFECTIVE:
                {
                alt21=7;
                }
                break;
            case DATE_EXPIRES:
                {
                alt21=8;
                }
                break;
            case ENABLED:
                {
                alt21=9;
                }
                break;
            case RULEFLOW_GROUP:
                {
                alt21=10;
                }
                break;
            case LOCK_ON_ACTIVE:
                {
                alt21=11;
                }
                break;
            case DIALECT:
                {
                alt21=12;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("446:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect );", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:451:4: a= salience
                    {
                    pushFollow(FOLLOW_salience_in_rule_attribute977);
                    a=salience();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:5: a= no_loop
                    {
                    pushFollow(FOLLOW_no_loop_in_rule_attribute987);
                    a=no_loop();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:453:5: a= agenda_group
                    {
                    pushFollow(FOLLOW_agenda_group_in_rule_attribute998);
                    a=agenda_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:454:5: a= duration
                    {
                    pushFollow(FOLLOW_duration_in_rule_attribute1011);
                    a=duration();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:455:5: a= activation_group
                    {
                    pushFollow(FOLLOW_activation_group_in_rule_attribute1025);
                    a=activation_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 6 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:456:5: a= auto_focus
                    {
                    pushFollow(FOLLOW_auto_focus_in_rule_attribute1036);
                    a=auto_focus();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 7 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:457:5: a= date_effective
                    {
                    pushFollow(FOLLOW_date_effective_in_rule_attribute1047);
                    a=date_effective();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d = a; 
                    }

                    }
                    break;
                case 8 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:458:5: a= date_expires
                    {
                    pushFollow(FOLLOW_date_expires_in_rule_attribute1057);
                    a=date_expires();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d = a; 
                    }

                    }
                    break;
                case 9 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:459:5: a= enabled
                    {
                    pushFollow(FOLLOW_enabled_in_rule_attribute1067);
                    a=enabled();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d=a;
                    }

                    }
                    break;
                case 10 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:460:5: a= ruleflow_group
                    {
                    pushFollow(FOLLOW_ruleflow_group_in_rule_attribute1077);
                    a=ruleflow_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 11 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:461:5: a= lock_on_active
                    {
                    pushFollow(FOLLOW_lock_on_active_in_rule_attribute1087);
                    a=lock_on_active();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 12 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:462:5: a= dialect
                    {
                    pushFollow(FOLLOW_dialect_in_rule_attribute1096);
                    a=dialect();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d = a; 
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
    // $ANTLR end rule_attribute


    // $ANTLR start date_effective
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:466:1: date_effective returns [AttributeDescr d] : loc= DATE_EFFECTIVE val= STRING ;
    public final AttributeDescr date_effective() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token val=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:471:3: (loc= DATE_EFFECTIVE val= STRING )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:471:3: loc= DATE_EFFECTIVE val= STRING
            {
            loc=(Token)input.LT(1);
            match(input,DATE_EFFECTIVE,FOLLOW_DATE_EFFECTIVE_in_date_effective1128); if (failed) return d;
            val=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_effective1132); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new AttributeDescr( "date-effective", getString( val ) );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			d.setEndCharacter( ((CommonToken)val).getStopIndex() );
              		
            }

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
    // $ANTLR end date_effective


    // $ANTLR start date_expires
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:481:1: date_expires returns [AttributeDescr d] : loc= DATE_EXPIRES val= STRING ;
    public final AttributeDescr date_expires() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token val=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:486:3: (loc= DATE_EXPIRES val= STRING )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:486:3: loc= DATE_EXPIRES val= STRING
            {
            loc=(Token)input.LT(1);
            match(input,DATE_EXPIRES,FOLLOW_DATE_EXPIRES_in_date_expires1165); if (failed) return d;
            val=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_expires1169); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new AttributeDescr( "date-expires", getString( val ) );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			d.setEndCharacter( ((CommonToken)val).getStopIndex() );
              		
            }

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
    // $ANTLR end date_expires


    // $ANTLR start enabled
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:497:1: enabled returns [AttributeDescr d] : loc= ENABLED t= BOOL ;
    public final AttributeDescr enabled() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:502:4: (loc= ENABLED t= BOOL )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:502:4: loc= ENABLED t= BOOL
            {
            loc=(Token)input.LT(1);
            match(input,ENABLED,FOLLOW_ENABLED_in_enabled1204); if (failed) return d;
            t=(Token)input.LT(1);
            match(input,BOOL,FOLLOW_BOOL_in_enabled1208); if (failed) return d;
            if ( backtracking==0 ) {

              				d = new AttributeDescr( "enabled", t.getText() );
              				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              				d.setEndCharacter( ((CommonToken)t).getStopIndex() );
              			
            }

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
    // $ANTLR end enabled


    // $ANTLR start salience
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:515:1: salience returns [AttributeDescr d ] : loc= SALIENCE i= INT ;
    public final AttributeDescr salience() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:520:3: (loc= SALIENCE i= INT )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:520:3: loc= SALIENCE i= INT
            {
            loc=(Token)input.LT(1);
            match(input,SALIENCE,FOLLOW_SALIENCE_in_salience1253); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience1257); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new AttributeDescr( "salience", i.getText() );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			d.setEndCharacter( ((CommonToken)i).getStopIndex() );
              		
            }

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:529:1: no_loop returns [AttributeDescr d] : ( (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );
    public final AttributeDescr no_loop() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:534:3: ( (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==NO_LOOP) ) {
                int LA22_1 = input.LA(2);

                if ( (LA22_1==BOOL) ) {
                    alt22=2;
                }
                else if ( (LA22_1==EOF||(LA22_1>=IMPORT && LA22_1<=QUERY)||(LA22_1>=TEMPLATE && LA22_1<=DATE_EFFECTIVE)||(LA22_1>=DATE_EXPIRES && LA22_1<=ENABLED)||LA22_1==SALIENCE||(LA22_1>=NO_LOOP && LA22_1<=LOCK_ON_ACTIVE)||LA22_1==THEN||LA22_1==66) ) {
                    alt22=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("529:1: no_loop returns [AttributeDescr d] : ( (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );", 22, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("529:1: no_loop returns [AttributeDescr d] : ( (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:534:3: (loc= NO_LOOP )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:534:3: (loc= NO_LOOP )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:535:4: loc= NO_LOOP
                    {
                    loc=(Token)input.LT(1);
                    match(input,NO_LOOP,FOLLOW_NO_LOOP_in_no_loop1295); if (failed) return d;
                    if ( backtracking==0 ) {

                      				d = new AttributeDescr( "no-loop", "true" );
                      				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
                      				d.setEndCharacter( ((CommonToken)loc).getStopIndex() );
                      			
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:544:3: (loc= NO_LOOP t= BOOL )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:544:3: (loc= NO_LOOP t= BOOL )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:545:4: loc= NO_LOOP t= BOOL
                    {
                    loc=(Token)input.LT(1);
                    match(input,NO_LOOP,FOLLOW_NO_LOOP_in_no_loop1323); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1327); if (failed) return d;
                    if ( backtracking==0 ) {

                      				d = new AttributeDescr( "no-loop", t.getText() );
                      				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
                      				d.setEndCharacter( ((CommonToken)t).getStopIndex() );
                      			
                    }

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:557:1: auto_focus returns [AttributeDescr d] : ( (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );
    public final AttributeDescr auto_focus() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:562:3: ( (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) )
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==AUTO_FOCUS) ) {
                int LA23_1 = input.LA(2);

                if ( (LA23_1==BOOL) ) {
                    alt23=2;
                }
                else if ( (LA23_1==EOF||(LA23_1>=IMPORT && LA23_1<=QUERY)||(LA23_1>=TEMPLATE && LA23_1<=DATE_EFFECTIVE)||(LA23_1>=DATE_EXPIRES && LA23_1<=ENABLED)||LA23_1==SALIENCE||(LA23_1>=NO_LOOP && LA23_1<=LOCK_ON_ACTIVE)||LA23_1==THEN||LA23_1==66) ) {
                    alt23=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("557:1: auto_focus returns [AttributeDescr d] : ( (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );", 23, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("557:1: auto_focus returns [AttributeDescr d] : ( (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:562:3: (loc= AUTO_FOCUS )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:562:3: (loc= AUTO_FOCUS )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:563:4: loc= AUTO_FOCUS
                    {
                    loc=(Token)input.LT(1);
                    match(input,AUTO_FOCUS,FOLLOW_AUTO_FOCUS_in_auto_focus1376); if (failed) return d;
                    if ( backtracking==0 ) {

                      				d = new AttributeDescr( "auto-focus", "true" );
                      				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
                      				d.setEndCharacter( ((CommonToken)loc).getStopIndex() );
                      			
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:572:3: (loc= AUTO_FOCUS t= BOOL )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:572:3: (loc= AUTO_FOCUS t= BOOL )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:573:4: loc= AUTO_FOCUS t= BOOL
                    {
                    loc=(Token)input.LT(1);
                    match(input,AUTO_FOCUS,FOLLOW_AUTO_FOCUS_in_auto_focus1404); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1408); if (failed) return d;
                    if ( backtracking==0 ) {

                      				d = new AttributeDescr( "auto-focus", t.getText() );
                      				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
                      				d.setEndCharacter( ((CommonToken)t).getStopIndex() );
                      			
                    }

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


    // $ANTLR start activation_group
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:585:1: activation_group returns [AttributeDescr d] : loc= ACTIVATION_GROUP n= STRING ;
    public final AttributeDescr activation_group() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token n=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:590:3: (loc= ACTIVATION_GROUP n= STRING )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:590:3: loc= ACTIVATION_GROUP n= STRING
            {
            loc=(Token)input.LT(1);
            match(input,ACTIVATION_GROUP,FOLLOW_ACTIVATION_GROUP_in_activation_group1453); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1457); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new AttributeDescr( "activation-group", getString( n ) );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			d.setEndCharacter( ((CommonToken)n).getStopIndex() );
              		
            }

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
    // $ANTLR end activation_group


    // $ANTLR start ruleflow_group
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:599:1: ruleflow_group returns [AttributeDescr d] : loc= RULEFLOW_GROUP n= STRING ;
    public final AttributeDescr ruleflow_group() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token n=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:604:3: (loc= RULEFLOW_GROUP n= STRING )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:604:3: loc= RULEFLOW_GROUP n= STRING
            {
            loc=(Token)input.LT(1);
            match(input,RULEFLOW_GROUP,FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1489); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_ruleflow_group1493); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new AttributeDescr( "ruleflow-group", getString( n ) );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			d.setEndCharacter( ((CommonToken)n).getStopIndex() );
              		
            }

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
    // $ANTLR end ruleflow_group


    // $ANTLR start agenda_group
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:613:1: agenda_group returns [AttributeDescr d] : loc= AGENDA_GROUP n= STRING ;
    public final AttributeDescr agenda_group() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token n=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:618:3: (loc= AGENDA_GROUP n= STRING )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:618:3: loc= AGENDA_GROUP n= STRING
            {
            loc=(Token)input.LT(1);
            match(input,AGENDA_GROUP,FOLLOW_AGENDA_GROUP_in_agenda_group1525); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1529); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new AttributeDescr( "agenda-group", getString( n ) );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			d.setEndCharacter( ((CommonToken)n).getStopIndex() );
              		
            }

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:628:1: duration returns [AttributeDescr d] : loc= DURATION i= INT ;
    public final AttributeDescr duration() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:633:3: (loc= DURATION i= INT )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:633:3: loc= DURATION i= INT
            {
            loc=(Token)input.LT(1);
            match(input,DURATION,FOLLOW_DURATION_in_duration1563); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1567); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new AttributeDescr( "duration", i.getText() );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			d.setEndCharacter( ((CommonToken)i).getStopIndex() );
              		
            }

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


    // $ANTLR start dialect
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:642:1: dialect returns [AttributeDescr d] : loc= DIALECT n= STRING ;
    public final AttributeDescr dialect() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token n=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:647:3: (loc= DIALECT n= STRING )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:647:3: loc= DIALECT n= STRING
            {
            loc=(Token)input.LT(1);
            match(input,DIALECT,FOLLOW_DIALECT_in_dialect1599); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_dialect1603); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new AttributeDescr( "dialect", getString( n ) );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			d.setEndCharacter( ((CommonToken)n).getStopIndex() );
              		
            }

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
    // $ANTLR end dialect


    // $ANTLR start lock_on_active
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:661:1: lock_on_active returns [AttributeDescr d] : ( (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) );
    public final AttributeDescr lock_on_active() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:666:3: ( (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) )
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==LOCK_ON_ACTIVE) ) {
                int LA24_1 = input.LA(2);

                if ( (LA24_1==BOOL) ) {
                    alt24=2;
                }
                else if ( (LA24_1==EOF||(LA24_1>=IMPORT && LA24_1<=QUERY)||(LA24_1>=TEMPLATE && LA24_1<=DATE_EFFECTIVE)||(LA24_1>=DATE_EXPIRES && LA24_1<=ENABLED)||LA24_1==SALIENCE||(LA24_1>=NO_LOOP && LA24_1<=LOCK_ON_ACTIVE)||LA24_1==THEN||LA24_1==66) ) {
                    alt24=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("661:1: lock_on_active returns [AttributeDescr d] : ( (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) );", 24, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("661:1: lock_on_active returns [AttributeDescr d] : ( (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) );", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:666:3: (loc= LOCK_ON_ACTIVE )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:666:3: (loc= LOCK_ON_ACTIVE )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:667:4: loc= LOCK_ON_ACTIVE
                    {
                    loc=(Token)input.LT(1);
                    match(input,LOCK_ON_ACTIVE,FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1652); if (failed) return d;
                    if ( backtracking==0 ) {

                      				d = new AttributeDescr( "lock-on-active", "true" );
                      				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
                      				d.setEndCharacter( ((CommonToken)loc).getStopIndex() );
                      			
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:676:3: (loc= LOCK_ON_ACTIVE t= BOOL )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:676:3: (loc= LOCK_ON_ACTIVE t= BOOL )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:677:4: loc= LOCK_ON_ACTIVE t= BOOL
                    {
                    loc=(Token)input.LT(1);
                    match(input,LOCK_ON_ACTIVE,FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1680); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_lock_on_active1684); if (failed) return d;
                    if ( backtracking==0 ) {

                      				d = new AttributeDescr( "lock-on-active", t.getText() );
                      				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
                      				d.setEndCharacter( ((CommonToken)t).getStopIndex() );
                      			
                    }

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
    // $ANTLR end lock_on_active


    // $ANTLR start normal_lhs_block
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:689:1: normal_lhs_block[AndDescr descr] : (d= lhs[descr] )* ;
    public final void normal_lhs_block(AndDescr descr) throws RecognitionException {
        BaseDescr d = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:691:3: ( (d= lhs[descr] )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:691:3: (d= lhs[descr] )*
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:691:3: (d= lhs[descr] )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==ID||LA25_0==LEFT_PAREN||(LA25_0>=EXISTS && LA25_0<=FORALL)) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:691:5: d= lhs[descr]
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1722);
            	    d=lhs(descr);
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       if(d != null) descr.addDescr( d ); 
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
    // $ANTLR end normal_lhs_block


    // $ANTLR start lhs
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:697:1: lhs[ConditionalElementDescr ce] returns [BaseDescr d] : l= lhs_or ;
    public final BaseDescr lhs(ConditionalElementDescr ce) throws RecognitionException {
        BaseDescr d = null;

        BaseDescr l = null;



        		d=null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:701:4: (l= lhs_or )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:701:4: l= lhs_or
            {
            pushFollow(FOLLOW_lhs_or_in_lhs1759);
            l=lhs_or();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               d = l; 
            }

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


    // $ANTLR start lhs_pattern
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:705:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact );
    public final BaseDescr lhs_pattern() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr f = null;



        		d=null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:709:4: (f= fact_binding | f= fact )
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==ID) ) {
                int LA26_1 = input.LA(2);

                if ( (LA26_1==65) ) {
                    alt26=1;
                }
                else if ( (LA26_1==LEFT_PAREN||LA26_1==LEFT_SQUARE||LA26_1==67) ) {
                    alt26=2;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("705:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact );", 26, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("705:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact );", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:709:4: f= fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_pattern1787);
                    f=fact_binding();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = f; 
                    }

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:710:4: f= fact
                    {
                    pushFollow(FOLLOW_fact_in_lhs_pattern1796);
                    f=fact();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = f; 
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
    // $ANTLR end lhs_pattern


    // $ANTLR start from_statement
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:713:1: from_statement returns [FromDescr d] : ds= from_source[d] ;
    public final FromDescr from_statement() throws RecognitionException {
        FromDescr d = null;

        DeclarativeInvokerDescr ds = null;



        		d=factory.createFrom();
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:718:2: (ds= from_source[d] )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:718:2: ds= from_source[d]
            {
            pushFollow(FOLLOW_from_source_in_from_statement1823);
            ds=from_source(d);
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {

              			d.setDataSource(ds);
              		
              		
            }

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
    // $ANTLR end from_statement


    // $ANTLR start from_source
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:728:1: from_source[FromDescr from] returns [DeclarativeInvokerDescr ds] : ident= identifier (args= paren_chunk[from] )? ( expression_chain[from, ad] )? ;
    public final DeclarativeInvokerDescr from_source(FromDescr from) throws RecognitionException {
        DeclarativeInvokerDescr ds = null;

        Token ident = null;

        String args = null;



        		ds = null;
        		AccessorDescr ad = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:734:3: (ident= identifier (args= paren_chunk[from] )? ( expression_chain[from, ad] )? )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:734:3: ident= identifier (args= paren_chunk[from] )? ( expression_chain[from, ad] )?
            {
            pushFollow(FOLLOW_identifier_in_from_source1865);
            ident=identifier();
            _fsp--;
            if (failed) return ds;
            if ( backtracking==0 ) {

              			ad = new AccessorDescr(ident.getText());	
              			ad.setLocation( offset(ident.getLine()), ident.getCharPositionInLine() );
              			ad.setStartCharacter( ((CommonToken)ident).getStartIndex() );
              			ad.setEndCharacter( ((CommonToken)ident).getStopIndex() );
              			ds = ad;
              		
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:742:3: (args= paren_chunk[from] )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==LEFT_PAREN) ) {
                int LA27_1 = input.LA(2);

                if ( (synpred42()) ) {
                    alt27=1;
                }
            }
            switch (alt27) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:742:4: args= paren_chunk[from]
                    {
                    pushFollow(FOLLOW_paren_chunk_in_from_source1876);
                    args=paren_chunk(from);
                    _fsp--;
                    if (failed) return ds;
                    if ( backtracking==0 ) {

                      			if( args != null ) {
                      				ad.setVariableName( null );
                      				FunctionCallDescr fc = new FunctionCallDescr(ident.getText());
                      				fc.setLocation( offset(ident.getLine()), ident.getCharPositionInLine() );			
                      				fc.setArguments(args);
                      				fc.setStartCharacter( ((CommonToken)ident).getStartIndex() );
                      				fc.setEndCharacter( ((CommonToken)ident).getStopIndex() );
                      				ad.addInvoker(fc);
                      			}
                      		
                    }

                    }
                    break;

            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:755:3: ( expression_chain[from, ad] )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==67) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: expression_chain[from, ad]
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source1890);
                    expression_chain(from,  ad);
                    _fsp--;
                    if (failed) return ds;

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
        return ds;
    }
    // $ANTLR end from_source


    // $ANTLR start expression_chain
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:758:1: expression_chain[FromDescr from, AccessorDescr as] : ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )? ) ;
    public final void expression_chain(FromDescr from, AccessorDescr as) throws RecognitionException {
        Token field = null;

        String sqarg = null;

        String paarg = null;



          		FieldAccessDescr fa = null;
        	    	MethodAccessDescr ma = null;	
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:764:2: ( ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )? ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:764:2: ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )? )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:764:2: ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )? )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:764:4: '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )?
            {
            match(input,67,FOLLOW_67_in_expression_chain1915); if (failed) return ;
            pushFollow(FOLLOW_identifier_in_expression_chain1919);
            field=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              	        fa = new FieldAccessDescr(field.getText());	
              		fa.setLocation( offset(field.getLine()), field.getCharPositionInLine() );
              		fa.setStartCharacter( ((CommonToken)field).getStartIndex() );
              		fa.setEndCharacter( ((CommonToken)field).getStopIndex() );
              	    
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:771:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )?
            int alt29=3;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==LEFT_SQUARE) && (synpred44())) {
                alt29=1;
            }
            else if ( (LA29_0==LEFT_PAREN) ) {
                int LA29_2 = input.LA(2);

                if ( (synpred45()) ) {
                    alt29=2;
                }
            }
            switch (alt29) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:772:6: ( LEFT_SQUARE )=>sqarg= square_chunk[from]
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain1950);
                    sqarg=square_chunk(from);
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      	          fa.setArgument( sqarg );	
                      	      
                    }

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:777:6: ( LEFT_PAREN )=>paarg= paren_chunk[from]
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain1984);
                    paarg=paren_chunk(from);
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      	    	  ma = new MethodAccessDescr( field.getText(), paarg );	
                      		  ma.setLocation( offset(field.getLine()), field.getCharPositionInLine() );
                      		  ma.setStartCharacter( ((CommonToken)field).getStartIndex() );
                      		
                    }

                    }
                    break;

            }

            if ( backtracking==0 ) {

              	      if( ma != null ) {
              	          as.addInvoker( ma );
              	      } else {
              	          as.addInvoker( fa );
              	      }
              	  
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:791:4: ( expression_chain[from, as] )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==67) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: expression_chain[from, as]
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain2005);
                    expression_chain(from,  as);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }


            }


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
    // $ANTLR end expression_chain


    // $ANTLR start accumulate_statement
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:795:1: accumulate_statement returns [AccumulateDescr d] : loc= ACCUMULATE '(' pattern= lhs_pattern ',' INIT text= paren_chunk[null] ',' ACTION text= paren_chunk[null] ',' RESULT text= paren_chunk[null] loc= ')' ;
    public final AccumulateDescr accumulate_statement() throws RecognitionException {
        AccumulateDescr d = null;

        Token loc=null;
        BaseDescr pattern = null;

        String text = null;



        		d = factory.createAccumulate();
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:800:10: (loc= ACCUMULATE '(' pattern= lhs_pattern ',' INIT text= paren_chunk[null] ',' ACTION text= paren_chunk[null] ',' RESULT text= paren_chunk[null] loc= ')' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:800:10: loc= ACCUMULATE '(' pattern= lhs_pattern ',' INIT text= paren_chunk[null] ',' ACTION text= paren_chunk[null] ',' RESULT text= paren_chunk[null] loc= ')'
            {
            loc=(Token)input.LT(1);
            match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_accumulate_statement2046); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement2056); if (failed) return d;
            pushFollow(FOLLOW_lhs_pattern_in_accumulate_statement2060);
            pattern=lhs_pattern();
            _fsp--;
            if (failed) return d;
            match(input,66,FOLLOW_66_in_accumulate_statement2062); if (failed) return d;
            if ( backtracking==0 ) {

              		        d.setSourcePattern( (PatternDescr)pattern );
              		
            }
            match(input,INIT,FOLLOW_INIT_in_accumulate_statement2071); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2075);
            text=paren_chunk(null);
            _fsp--;
            if (failed) return d;
            match(input,66,FOLLOW_66_in_accumulate_statement2078); if (failed) return d;
            if ( backtracking==0 ) {

              		        d.setInitCode( text.substring(1, text.length()-1) );
              		
            }
            match(input,ACTION,FOLLOW_ACTION_in_accumulate_statement2087); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2091);
            text=paren_chunk(null);
            _fsp--;
            if (failed) return d;
            match(input,66,FOLLOW_66_in_accumulate_statement2094); if (failed) return d;
            if ( backtracking==0 ) {

              		        d.setActionCode( text.substring(1, text.length()-1) );
              		
            }
            match(input,RESULT,FOLLOW_RESULT_in_accumulate_statement2103); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2107);
            text=paren_chunk(null);
            _fsp--;
            if (failed) return d;
            loc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement2112); if (failed) return d;
            if ( backtracking==0 ) {

              		        d.setResultCode( text.substring(1, text.length()-1) );
              			d.setEndCharacter( ((CommonToken)loc).getStopIndex() );
              		
            }

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
    // $ANTLR end accumulate_statement


    // $ANTLR start collect_statement
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:824:1: collect_statement returns [CollectDescr d] : loc= COLLECT '(' pattern= lhs_pattern loc= ')' ;
    public final CollectDescr collect_statement() throws RecognitionException {
        CollectDescr d = null;

        Token loc=null;
        BaseDescr pattern = null;



        		d = factory.createCollect();
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:829:10: (loc= COLLECT '(' pattern= lhs_pattern loc= ')' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:829:10: loc= COLLECT '(' pattern= lhs_pattern loc= ')'
            {
            loc=(Token)input.LT(1);
            match(input,COLLECT,FOLLOW_COLLECT_in_collect_statement2155); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement2165); if (failed) return d;
            pushFollow(FOLLOW_lhs_pattern_in_collect_statement2169);
            pattern=lhs_pattern();
            _fsp--;
            if (failed) return d;
            loc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement2173); if (failed) return d;
            if ( backtracking==0 ) {

              		        d.setSourcePattern( (PatternDescr)pattern );
              			d.setEndCharacter( ((CommonToken)loc).getStopIndex() );
              		
            }

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
    // $ANTLR end collect_statement


    // $ANTLR start fact_binding
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:841:1: fact_binding returns [BaseDescr d] : id= ID ':' fe= fact_expression[id.getText()] ;
    public final BaseDescr fact_binding() throws RecognitionException {
        BaseDescr d = null;

        Token id=null;
        BaseDescr fe = null;



        		d=null;
        		boolean multi=false;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:847:4: (id= ID ':' fe= fact_expression[id.getText()] )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:847:4: id= ID ':' fe= fact_expression[id.getText()]
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding2207); if (failed) return d;
            match(input,65,FOLLOW_65_in_fact_binding2209); if (failed) return d;
            if ( backtracking==0 ) {

               		        // handling incomplete parsing
               		        d = new PatternDescr( );
               		        ((PatternDescr) d).setIdentifier( id.getText() );
               		
            }
            pushFollow(FOLLOW_fact_expression_in_fact_binding2222);
            fe=fact_expression(id.getText());
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {

               		        // override previously instantiated pattern
               			d=fe;
               			if( d != null ) {
                 			    d.setStartCharacter( ((CommonToken)id).getStartIndex() );
                 			}
               		
            }

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


    // $ANTLR start fact_expression
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:863:2: fact_expression[String id] returns [BaseDescr pd] : ( '(' fe= fact_expression[id] ')' | f= fact ( ( OR | '||' ) f= fact )* );
    public final BaseDescr fact_expression(String id) throws RecognitionException {
        BaseDescr pd = null;

        BaseDescr fe = null;

        BaseDescr f = null;



         		pd = null;
         		boolean multi = false;
         	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:868:5: ( '(' fe= fact_expression[id] ')' | f= fact ( ( OR | '||' ) f= fact )* )
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==LEFT_PAREN) ) {
                alt32=1;
            }
            else if ( (LA32_0==ID) ) {
                alt32=2;
            }
            else {
                if (backtracking>0) {failed=true; return pd;}
                NoViableAltException nvae =
                    new NoViableAltException("863:2: fact_expression[String id] returns [BaseDescr pd] : ( '(' fe= fact_expression[id] ')' | f= fact ( ( OR | '||' ) f= fact )* );", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:868:5: '(' fe= fact_expression[id] ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_expression2254); if (failed) return pd;
                    pushFollow(FOLLOW_fact_expression_in_fact_expression2258);
                    fe=fact_expression(id);
                    _fsp--;
                    if (failed) return pd;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_expression2261); if (failed) return pd;
                    if ( backtracking==0 ) {
                       pd=fe; 
                    }

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:869:6: f= fact ( ( OR | '||' ) f= fact )*
                    {
                    pushFollow(FOLLOW_fact_in_fact_expression2272);
                    f=fact();
                    _fsp--;
                    if (failed) return pd;
                    if ( backtracking==0 ) {

                       			((PatternDescr)f).setIdentifier( id );
                       			pd = f;
                       		
                    }
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:874:4: ( ( OR | '||' ) f= fact )*
                    loop31:
                    do {
                        int alt31=2;
                        int LA31_0 = input.LA(1);

                        if ( (LA31_0==OR||LA31_0==69) ) {
                            int LA31_6 = input.LA(2);

                            if ( (synpred49()) ) {
                                alt31=1;
                            }


                        }


                        switch (alt31) {
                    	case 1 :
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:874:6: ( OR | '||' ) f= fact
                    	    {
                    	    if ( input.LA(1)==OR||input.LA(1)==69 ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return pd;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fact_expression2284);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {
                    	      	if ( ! multi ) {
                    	       					BaseDescr first = pd;
                    	       					pd = new OrDescr();
                    	       					((OrDescr)pd).addDescr( first );
                    	       					multi=true;
                    	       				}
                    	       			
                    	    }
                    	    pushFollow(FOLLOW_fact_in_fact_expression2302);
                    	    f=fact();
                    	    _fsp--;
                    	    if (failed) return pd;
                    	    if ( backtracking==0 ) {

                    	       				((PatternDescr)f).setIdentifier( id );
                    	       				((OrDescr)pd).addDescr( f );
                    	       			
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop31;
                        }
                    } while (true);


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
        return pd;
    }
    // $ANTLR end fact_expression


    // $ANTLR start fact
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:890:1: fact returns [BaseDescr d] : id= dotted_name[d] loc= LEFT_PAREN ( constraints[pattern] )? endLoc= RIGHT_PAREN ;
    public final BaseDescr fact() throws RecognitionException {
        BaseDescr d = null;

        Token loc=null;
        Token endLoc=null;
        String id = null;



        		d=null;
        		PatternDescr pattern = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:896:11: (id= dotted_name[d] loc= LEFT_PAREN ( constraints[pattern] )? endLoc= RIGHT_PAREN )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:896:11: id= dotted_name[d] loc= LEFT_PAREN ( constraints[pattern] )? endLoc= RIGHT_PAREN
            {
            if ( backtracking==0 ) {

               			pattern = new PatternDescr( );
               			d = pattern; 
               	        
            }
            pushFollow(FOLLOW_dotted_name_in_fact2363);
            id=dotted_name(d);
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
               		        pattern.setObjectType( id );
               		        pattern.setEndCharacter( -1 );
               		
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact2377); if (failed) return d;
            if ( backtracking==0 ) {

               				pattern.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
               			        pattern.setLeftParentCharacter( ((CommonToken)loc).getStartIndex() );
               			
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:909:4: ( constraints[pattern] )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( ((LA33_0>=ATTRIBUTES && LA33_0<=WHEN)||LA33_0==ENABLED||LA33_0==SALIENCE||LA33_0==DURATION||(LA33_0>=ACCUMULATE && LA33_0<=LEFT_PAREN)||(LA33_0>=CONTAINS && LA33_0<=EXCLUDES)||LA33_0==NULL||(LA33_0>=AND && LA33_0<=THEN)) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:909:6: constraints[pattern]
                    {
                    pushFollow(FOLLOW_constraints_in_fact2387);
                    constraints(pattern);
                    _fsp--;
                    if (failed) return d;

                    }
                    break;

            }

            endLoc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact2400); if (failed) return d;
            if ( backtracking==0 ) {

              		        if( endLoc.getType() == RIGHT_PAREN ) {
              				pattern.setEndLocation( offset(endLoc.getLine()), endLoc.getCharPositionInLine() );	
              				pattern.setEndCharacter( ((CommonToken)endLoc).getStopIndex() );
               			        pattern.setRightParentCharacter( ((CommonToken)endLoc).getStartIndex() );
              			}
               		
            }

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:921:1: constraints[PatternDescr pattern] : ( constraint[pattern] | predicate[pattern] ) ( ',' ( constraint[pattern] | predicate[pattern] ) )* ;
    public final void constraints(PatternDescr pattern) throws RecognitionException {
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:922:4: ( ( constraint[pattern] | predicate[pattern] ) ( ',' ( constraint[pattern] | predicate[pattern] ) )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:922:4: ( constraint[pattern] | predicate[pattern] ) ( ',' ( constraint[pattern] | predicate[pattern] ) )*
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:922:4: ( constraint[pattern] | predicate[pattern] )
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( ((LA34_0>=ATTRIBUTES && LA34_0<=WHEN)||LA34_0==ENABLED||LA34_0==SALIENCE||LA34_0==DURATION||(LA34_0>=ACCUMULATE && LA34_0<=OR)||(LA34_0>=CONTAINS && LA34_0<=EXCLUDES)||LA34_0==NULL||(LA34_0>=AND && LA34_0<=THEN)) ) {
                alt34=1;
            }
            else if ( (LA34_0==LEFT_PAREN) ) {
                alt34=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("922:4: ( constraint[pattern] | predicate[pattern] )", 34, 0, input);

                throw nvae;
            }
            switch (alt34) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:922:5: constraint[pattern]
                    {
                    pushFollow(FOLLOW_constraint_in_constraints2421);
                    constraint(pattern);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:922:25: predicate[pattern]
                    {
                    pushFollow(FOLLOW_predicate_in_constraints2424);
                    predicate(pattern);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:923:3: ( ',' ( constraint[pattern] | predicate[pattern] ) )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( (LA36_0==66) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:923:5: ',' ( constraint[pattern] | predicate[pattern] )
            	    {
            	    match(input,66,FOLLOW_66_in_constraints2432); if (failed) return ;
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:923:9: ( constraint[pattern] | predicate[pattern] )
            	    int alt35=2;
            	    int LA35_0 = input.LA(1);

            	    if ( ((LA35_0>=ATTRIBUTES && LA35_0<=WHEN)||LA35_0==ENABLED||LA35_0==SALIENCE||LA35_0==DURATION||(LA35_0>=ACCUMULATE && LA35_0<=OR)||(LA35_0>=CONTAINS && LA35_0<=EXCLUDES)||LA35_0==NULL||(LA35_0>=AND && LA35_0<=THEN)) ) {
            	        alt35=1;
            	    }
            	    else if ( (LA35_0==LEFT_PAREN) ) {
            	        alt35=2;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("923:9: ( constraint[pattern] | predicate[pattern] )", 35, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt35) {
            	        case 1 :
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:923:10: constraint[pattern]
            	            {
            	            pushFollow(FOLLOW_constraint_in_constraints2435);
            	            constraint(pattern);
            	            _fsp--;
            	            if (failed) return ;

            	            }
            	            break;
            	        case 2 :
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:923:30: predicate[pattern]
            	            {
            	            pushFollow(FOLLOW_predicate_in_constraints2438);
            	            predicate(pattern);
            	            _fsp--;
            	            if (failed) return ;

            	            }
            	            break;

            	    }


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
        return ;
    }
    // $ANTLR end constraints


    // $ANTLR start constraint
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:926:1: constraint[PatternDescr pattern] : (fb= ID ':' )? f= identifier ( (rd= constraint_expression (con= ( '&' | '|' ) rd= constraint_expression )* ) | '->' predicate[pattern] )? ;
    public final void constraint(PatternDescr pattern) throws RecognitionException {
        Token fb=null;
        Token con=null;
        Token f = null;

        RestrictionDescr rd = null;



        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:932:3: ( (fb= ID ':' )? f= identifier ( (rd= constraint_expression (con= ( '&' | '|' ) rd= constraint_expression )* ) | '->' predicate[pattern] )? )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:932:3: (fb= ID ':' )? f= identifier ( (rd= constraint_expression (con= ( '&' | '|' ) rd= constraint_expression )* ) | '->' predicate[pattern] )?
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:932:3: (fb= ID ':' )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==ID) ) {
                int LA37_1 = input.LA(2);

                if ( (LA37_1==65) ) {
                    alt37=1;
                }
            }
            switch (alt37) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:932:5: fb= ID ':'
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint2467); if (failed) return ;
                    match(input,65,FOLLOW_65_in_constraint2469); if (failed) return ;
                    if ( backtracking==0 ) {
                       
                      			fbd = new FieldBindingDescr();
                      			fbd.setIdentifier( fb.getText() );
                      			fbd.setLocation( offset(fb.getLine()), fb.getCharPositionInLine() );
                      			fbd.setStartCharacter( ((CommonToken)fb).getStartIndex() );
                      			pattern.addDescr( fbd );

                      		    
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_identifier_in_constraint2490);
            f=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              		    if( f != null ) {
              		    
              			if ( fbd != null ) {
              			    fbd.setFieldName( f.getText() );
               			    fbd.setEndCharacter( ((CommonToken)f).getStopIndex() );
              			} 
              			fc = new FieldConstraintDescr(f.getText());
              			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
              			fc.setStartCharacter( ((CommonToken)f).getStartIndex() );
              			
              			// it must be a field constraint, as it is not a binding
              			if( fb == null ) {
              			    pattern.addDescr( fc );
              			}
              		    }
              		
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:960:3: ( (rd= constraint_expression (con= ( '&' | '|' ) rd= constraint_expression )* ) | '->' predicate[pattern] )?
            int alt39=3;
            int LA39_0 = input.LA(1);

            if ( ((LA39_0>=CONTAINS && LA39_0<=EXCLUDES)||(LA39_0>=73 && LA39_0<=78)) ) {
                alt39=1;
            }
            else if ( (LA39_0==72) ) {
                alt39=2;
            }
            switch (alt39) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:961:4: (rd= constraint_expression (con= ( '&' | '|' ) rd= constraint_expression )* )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:961:4: (rd= constraint_expression (con= ( '&' | '|' ) rd= constraint_expression )* )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:961:6: rd= constraint_expression (con= ( '&' | '|' ) rd= constraint_expression )*
                    {
                    pushFollow(FOLLOW_constraint_expression_in_constraint2508);
                    rd=constraint_expression();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      					fc.addRestriction(rd);
                      					// we must add now as we didn't before
                      					if( fb != null) {
                      					    pattern.addDescr( fc );
                      					}
                      				
                    }
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:969:5: (con= ( '&' | '|' ) rd= constraint_expression )*
                    loop38:
                    do {
                        int alt38=2;
                        int LA38_0 = input.LA(1);

                        if ( ((LA38_0>=70 && LA38_0<=71)) ) {
                            alt38=1;
                        }


                        switch (alt38) {
                    	case 1 :
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:970:6: con= ( '&' | '|' ) rd= constraint_expression
                    	    {
                    	    con=(Token)input.LT(1);
                    	    if ( (input.LA(1)>=70 && input.LA(1)<=71) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return ;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint2529);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {

                    	      						if (con.getText().equals("&") ) {								
                    	      							fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND));	
                    	      						} else {
                    	      							fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR));	
                    	      						}							
                    	      					
                    	    }
                    	    pushFollow(FOLLOW_constraint_expression_in_constraint2549);
                    	    rd=constraint_expression();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    if ( backtracking==0 ) {

                    	      					        if( rd != null ) {
                    	      							fc.addRestriction(rd);
                    	      					        }
                    	      					
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop38;
                        }
                    } while (true);


                    }


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:987:4: '->' predicate[pattern]
                    {
                    match(input,72,FOLLOW_72_in_constraint2577); if (failed) return ;
                    pushFollow(FOLLOW_predicate_in_constraint2579);
                    predicate(pattern);
                    _fsp--;
                    if (failed) return ;

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
        return ;
    }
    // $ANTLR end constraint


    // $ANTLR start constraint_expression
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:991:1: constraint_expression returns [RestrictionDescr rd] : op= ( '==' | '>' | '>=' | '<' | '<=' | '!=' | CONTAINS | MATCHES | EXCLUDES ) (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) ;
    public final RestrictionDescr constraint_expression() throws RecognitionException {
        RestrictionDescr rd = null;

        Token op=null;
        Token bvc=null;
        String lc = null;

        String rvc = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:993:3: (op= ( '==' | '>' | '>=' | '<' | '<=' | '!=' | CONTAINS | MATCHES | EXCLUDES ) (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:993:3: op= ( '==' | '>' | '>=' | '<' | '<=' | '!=' | CONTAINS | MATCHES | EXCLUDES ) (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
            {
            op=(Token)input.LT(1);
            if ( (input.LA(1)>=CONTAINS && input.LA(1)<=EXCLUDES)||(input.LA(1)>=73 && input.LA(1)<=78) ) {
                input.consume();
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return rd;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint_expression2614);    throw mse;
            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1003:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
            int alt40=4;
            switch ( input.LA(1) ) {
            case ID:
                {
                int LA40_1 = input.LA(2);

                if ( (LA40_1==67) ) {
                    alt40=2;
                }
                else if ( (LA40_1==EOF||LA40_1==RIGHT_PAREN||LA40_1==66||(LA40_1>=70 && LA40_1<=71)) ) {
                    alt40=1;
                }
                else {
                    if (backtracking>0) {failed=true; return rd;}
                    NoViableAltException nvae =
                        new NoViableAltException("1003:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 40, 1, input);

                    throw nvae;
                }
                }
                break;
            case STRING:
            case BOOL:
            case INT:
            case FLOAT:
            case NULL:
                {
                alt40=3;
                }
                break;
            case LEFT_PAREN:
                {
                alt40=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return rd;}
                NoViableAltException nvae =
                    new NoViableAltException("1003:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 40, 0, input);

                throw nvae;
            }

            switch (alt40) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1003:5: bvc= ID
                    {
                    bvc=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint_expression2683); if (failed) return rd;
                    if ( backtracking==0 ) {

                      				rd = new VariableRestrictionDescr(op.getText(), bvc.getText());
                      			
                    }

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1008:4: lc= enum_constraint
                    {
                    pushFollow(FOLLOW_enum_constraint_in_constraint_expression2699);
                    lc=enum_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd  = new LiteralRestrictionDescr(op.getText(), lc, true);
                      			
                    }

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1013:4: lc= literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_constraint_expression2722);
                    lc=literal_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd  = new LiteralRestrictionDescr(op.getText(), lc);
                      			
                    }

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1017:5: rvc= retval_constraint
                    {
                    pushFollow(FOLLOW_retval_constraint_in_constraint_expression2736);
                    rvc=retval_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd = new ReturnValueRestrictionDescr(op.getText(), rvc);							
                      			
                    }

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
        return rd;
    }
    // $ANTLR end constraint_expression


    // $ANTLR start literal_constraint
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1024:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public final String literal_constraint() throws RecognitionException {
        String text = null;

        Token t=null;


        		text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1028:4: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1028:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1028:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            int alt41=5;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt41=1;
                }
                break;
            case INT:
                {
                alt41=2;
                }
                break;
            case FLOAT:
                {
                alt41=3;
                }
                break;
            case BOOL:
                {
                alt41=4;
                }
                break;
            case NULL:
                {
                alt41=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return text;}
                NoViableAltException nvae =
                    new NoViableAltException("1028:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )", 41, 0, input);

                throw nvae;
            }

            switch (alt41) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1028:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint2775); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = getString( t ); 
                    }

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1029:5: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint2786); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1030:5: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint2799); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1031:5: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint2810); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1032:5: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal_constraint2822); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = null; 
                    }

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


    // $ANTLR start enum_constraint
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1036:1: enum_constraint returns [String text] : id= ID ( '.' ident= identifier )+ ;
    public final String enum_constraint() throws RecognitionException {
        String text = null;

        Token id=null;
        Token ident = null;



        		text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1041:3: (id= ID ( '.' ident= identifier )+ )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1041:3: id= ID ( '.' ident= identifier )+
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint2857); if (failed) return text;
            if ( backtracking==0 ) {
               text=id.getText(); 
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1041:32: ( '.' ident= identifier )+
            int cnt42=0;
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);

                if ( (LA42_0==67) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1041:34: '.' ident= identifier
            	    {
            	    match(input,67,FOLLOW_67_in_enum_constraint2863); if (failed) return text;
            	    pushFollow(FOLLOW_identifier_in_enum_constraint2867);
            	    ident=identifier();
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {
            	       text += "." + ident.getText(); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt42 >= 1 ) break loop42;
            	    if (backtracking>0) {failed=true; return text;}
                        EarlyExitException eee =
                            new EarlyExitException(42, input);
                        throw eee;
                }
                cnt42++;
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
    // $ANTLR end enum_constraint


    // $ANTLR start predicate
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1045:1: predicate[PatternDescr pattern] : text= paren_chunk[d] ;
    public final void predicate(PatternDescr pattern) throws RecognitionException {
        String text = null;



        		PredicateDescr d = null;
                
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1050:3: (text= paren_chunk[d] )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1050:3: text= paren_chunk[d]
            {
            if ( backtracking==0 ) {

              			d = new PredicateDescr( );
              		
            }
            pushFollow(FOLLOW_paren_chunk_in_predicate2909);
            text=paren_chunk(d);
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              		        if( text != null ) {
              			        String body = text.substring(1, text.length()-1);
              			        d.setContent( body );
              				pattern.addDescr( d );
              		        }
              		
            }

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1063:1: paren_chunk[BaseDescr descr] returns [String text] : loc= LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )* loc= RIGHT_PAREN ;
    public final String paren_chunk(BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc=null;
        String chunk = null;



                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1069:10: (loc= LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )* loc= RIGHT_PAREN )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1069:10: loc= LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )* loc= RIGHT_PAREN
            {
            if ( backtracking==0 ) {

              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk2958); if (failed) return text;
            if ( backtracking==0 ) {

              		    buf.append( loc.getText());
               
              		
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1079:3: (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )*
            loop43:
            do {
                int alt43=3;
                int LA43_0 = input.LA(1);

                if ( ((LA43_0>=ATTRIBUTES && LA43_0<=OR)||(LA43_0>=CONTAINS && LA43_0<=79)) ) {
                    alt43=1;
                }
                else if ( (LA43_0==LEFT_PAREN) ) {
                    alt43=2;
                }


                switch (alt43) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1080:4: ~ ( LEFT_PAREN | RIGHT_PAREN )
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=OR)||(input.LA(1)>=CONTAINS && input.LA(1)<=79) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_paren_chunk2974);    throw mse;
            	    }

            	    if ( backtracking==0 ) {

            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1085:4: chunk= paren_chunk[null]
            	    {
            	    pushFollow(FOLLOW_paren_chunk_in_paren_chunk2998);
            	    chunk=paren_chunk(null);
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {

            	      			    buf.append( chunk );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop43;
                }
            } while (true);

            if ( backtracking==0 ) {

              		    if( channel != null ) {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, channel.intValue());
              		    } else {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, Token.HIDDEN_CHANNEL);
              		    }
              		
            }
            loc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk3035); if (failed) return text;
            if ( backtracking==0 ) {

                                  buf.append( loc.getText() );
              		    text = buf.toString();
              		    if( descr != null ) {
              		        descr.setEndCharacter( ((CommonToken)loc).getStopIndex() );
              		    }
                              
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
    // $ANTLR end paren_chunk


    // $ANTLR start curly_chunk
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1107:1: curly_chunk[BaseDescr descr] returns [String text] : loc= LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )* loc= RIGHT_CURLY ;
    public final String curly_chunk(BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc=null;
        String chunk = null;



                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1113:3: (loc= LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )* loc= RIGHT_CURLY )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1113:3: loc= LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )* loc= RIGHT_CURLY
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk3086); if (failed) return text;
            if ( backtracking==0 ) {

              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              		    
              		    buf.append( loc.getText() );
              		
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1121:3: (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )*
            loop44:
            do {
                int alt44=3;
                int LA44_0 = input.LA(1);

                if ( ((LA44_0>=ATTRIBUTES && LA44_0<=NULL)||(LA44_0>=LEFT_SQUARE && LA44_0<=79)) ) {
                    alt44=1;
                }
                else if ( (LA44_0==LEFT_CURLY) ) {
                    alt44=2;
                }


                switch (alt44) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1122:4: ~ ( LEFT_CURLY | RIGHT_CURLY )
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=NULL)||(input.LA(1)>=LEFT_SQUARE && input.LA(1)<=79) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_curly_chunk3102);    throw mse;
            	    }

            	    if ( backtracking==0 ) {

            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1127:4: chunk= curly_chunk[descr]
            	    {
            	    pushFollow(FOLLOW_curly_chunk_in_curly_chunk3126);
            	    chunk=curly_chunk(descr);
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {

            	      			    buf.append( chunk );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop44;
                }
            } while (true);

            if ( backtracking==0 ) {

              		    if( channel != null ) {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, channel.intValue());
              		    } else {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, Token.HIDDEN_CHANNEL);
              		    }
              		
            }
            loc=(Token)input.LT(1);
            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk3163); if (failed) return text;
            if ( backtracking==0 ) {

                                  buf.append( loc.getText() );
              		    text = buf.toString();
              		    if( descr != null ) {
              		        descr.setEndCharacter( ((CommonToken)loc).getStopIndex() );
              		    }
                              
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
    // $ANTLR end curly_chunk


    // $ANTLR start square_chunk
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1149:1: square_chunk[BaseDescr descr] returns [String text] : loc= LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )* loc= RIGHT_SQUARE ;
    public final String square_chunk(BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc=null;
        String chunk = null;



                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1155:10: (loc= LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )* loc= RIGHT_SQUARE )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1155:10: loc= LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )* loc= RIGHT_SQUARE
            {
            if ( backtracking==0 ) {

              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk3226); if (failed) return text;
            if ( backtracking==0 ) {

              		    buf.append( loc.getText());
               
              		
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1165:3: (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )*
            loop45:
            do {
                int alt45=3;
                int LA45_0 = input.LA(1);

                if ( ((LA45_0>=ATTRIBUTES && LA45_0<=RIGHT_CURLY)||(LA45_0>=AND && LA45_0<=79)) ) {
                    alt45=1;
                }
                else if ( (LA45_0==LEFT_SQUARE) ) {
                    alt45=2;
                }


                switch (alt45) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1166:4: ~ ( LEFT_SQUARE | RIGHT_SQUARE )
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=RIGHT_CURLY)||(input.LA(1)>=AND && input.LA(1)<=79) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_square_chunk3242);    throw mse;
            	    }

            	    if ( backtracking==0 ) {

            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1171:4: chunk= square_chunk[null]
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_square_chunk3266);
            	    chunk=square_chunk(null);
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {

            	      			    buf.append( chunk );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop45;
                }
            } while (true);

            if ( backtracking==0 ) {

              		    if( channel != null ) {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, channel.intValue());
              		    } else {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, Token.HIDDEN_CHANNEL);
              		    }
              		
            }
            loc=(Token)input.LT(1);
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk3303); if (failed) return text;
            if ( backtracking==0 ) {

                                  buf.append( loc.getText() );
              		    text = buf.toString();
              		    if( descr != null ) {
              		        descr.setEndCharacter( ((CommonToken)loc).getStopIndex() );
              		    }
                              
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
    // $ANTLR end square_chunk


    // $ANTLR start retval_constraint
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1193:1: retval_constraint returns [String text] : c= paren_chunk[null] ;
    public final String retval_constraint() throws RecognitionException {
        String text = null;

        String c = null;



        		text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1198:3: (c= paren_chunk[null] )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1198:3: c= paren_chunk[null]
            {
            pushFollow(FOLLOW_paren_chunk_in_retval_constraint3348);
            c=paren_chunk(null);
            _fsp--;
            if (failed) return text;
            if ( backtracking==0 ) {
               text = c.substring(1, c.length()-1); 
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
    // $ANTLR end retval_constraint


    // $ANTLR start lhs_or
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1201:1: lhs_or returns [BaseDescr d] : left= lhs_and ( ( OR | '||' ) right= lhs_and )* ;
    public final BaseDescr lhs_or() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d = null;
        		OrDescr or = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1207:3: (left= lhs_and ( ( OR | '||' ) right= lhs_and )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1207:3: left= lhs_and ( ( OR | '||' ) right= lhs_and )*
            {
            pushFollow(FOLLOW_lhs_and_in_lhs_or3376);
            left=lhs_and();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
              d = left; 
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1208:3: ( ( OR | '||' ) right= lhs_and )*
            loop46:
            do {
                int alt46=2;
                int LA46_0 = input.LA(1);

                if ( (LA46_0==OR||LA46_0==69) ) {
                    alt46=1;
                }


                switch (alt46) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1208:5: ( OR | '||' ) right= lhs_and
            	    {
            	    if ( input.LA(1)==OR||input.LA(1)==69 ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return d;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or3384);    throw mse;
            	    }

            	    pushFollow(FOLLOW_lhs_and_in_lhs_or3395);
            	    right=lhs_and();
            	    _fsp--;
            	    if (failed) return d;
            	    if ( backtracking==0 ) {

            	      				if ( or == null ) {
            	      					or = new OrDescr();
            	      					or.addDescr( left );
            	      					d = or;
            	      				}
            	      				
            	      				or.addDescr( right );
            	      			
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
        return d;
    }
    // $ANTLR end lhs_or


    // $ANTLR start lhs_and
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1222:1: lhs_and returns [BaseDescr d] : left= lhs_unary ( ( AND | '&&' ) right= lhs_unary )* ;
    public final BaseDescr lhs_and() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d = null;
        		AndDescr and = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1228:3: (left= lhs_unary ( ( AND | '&&' ) right= lhs_unary )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1228:3: left= lhs_unary ( ( AND | '&&' ) right= lhs_unary )*
            {
            pushFollow(FOLLOW_lhs_unary_in_lhs_and3431);
            left=lhs_unary();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               d = left; 
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1229:3: ( ( AND | '&&' ) right= lhs_unary )*
            loop47:
            do {
                int alt47=2;
                int LA47_0 = input.LA(1);

                if ( (LA47_0==AND||LA47_0==79) ) {
                    alt47=1;
                }


                switch (alt47) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1229:5: ( AND | '&&' ) right= lhs_unary
            	    {
            	    if ( input.LA(1)==AND||input.LA(1)==79 ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return d;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and3439);    throw mse;
            	    }

            	    pushFollow(FOLLOW_lhs_unary_in_lhs_and3450);
            	    right=lhs_unary();
            	    _fsp--;
            	    if (failed) return d;
            	    if ( backtracking==0 ) {

            	      				if ( and == null ) {
            	      					and = new AndDescr();
            	      					and.addDescr( left );
            	      					d = and;
            	      				}
            	      				
            	      				and.addDescr( right );
            	      			
            	    }

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
    // $ANTLR end lhs_and


    // $ANTLR start lhs_unary
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1243:1: lhs_unary returns [BaseDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon ;
    public final BaseDescr lhs_unary() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr u = null;

        AccumulateDescr ac = null;

        CollectDescr cs = null;

        FromDescr fm = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1247:4: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1247:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1247:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' )
            int alt50=6;
            switch ( input.LA(1) ) {
            case EXISTS:
                {
                alt50=1;
                }
                break;
            case NOT:
                {
                alt50=2;
                }
                break;
            case EVAL:
                {
                alt50=3;
                }
                break;
            case ID:
                {
                alt50=4;
                }
                break;
            case FORALL:
                {
                alt50=5;
                }
                break;
            case LEFT_PAREN:
                {
                alt50=6;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1247:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' )", 50, 0, input);

                throw nvae;
            }

            switch (alt50) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1247:6: u= lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary3487);
                    u=lhs_exist();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1248:5: u= lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary3495);
                    u=lhs_not();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1249:5: u= lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary3503);
                    u=lhs_eval();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1250:5: u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )?
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_unary3511);
                    u=lhs_pattern();
                    _fsp--;
                    if (failed) return d;
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1250:19: ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )?
                    int alt49=2;
                    int LA49_0 = input.LA(1);

                    if ( (LA49_0==FROM) ) {
                        alt49=1;
                    }
                    switch (alt49) {
                        case 1 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1251:13: FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )
                            {
                            match(input,FROM,FOLLOW_FROM_in_lhs_unary3527); if (failed) return d;
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1251:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )
                            int alt48=3;
                            int LA48_0 = input.LA(1);

                            if ( (LA48_0==ACCUMULATE) && ((synpred94()||synpred91()))) {
                                int LA48_1 = input.LA(2);

                                if ( (synpred91()) ) {
                                    alt48=1;
                                }
                                else if ( (synpred94()) ) {
                                    alt48=3;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return d;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("1251:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )", 48, 1, input);

                                    throw nvae;
                                }
                            }
                            else if ( (LA48_0==COLLECT) && ((synpred92()||synpred94()))) {
                                int LA48_2 = input.LA(2);

                                if ( (synpred92()) ) {
                                    alt48=2;
                                }
                                else if ( (synpred94()) ) {
                                    alt48=3;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return d;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("1251:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )", 48, 2, input);

                                    throw nvae;
                                }
                            }
                            else if ( ((LA48_0>=ATTRIBUTES && LA48_0<=WHEN)||LA48_0==ENABLED||LA48_0==SALIENCE||LA48_0==DURATION||(LA48_0>=INIT && LA48_0<=RESULT)||(LA48_0>=ID && LA48_0<=OR)||(LA48_0>=CONTAINS && LA48_0<=EXCLUDES)||LA48_0==NULL||(LA48_0>=AND && LA48_0<=THEN)) && (synpred94())) {
                                alt48=3;
                            }
                            else {
                                if (backtracking>0) {failed=true; return d;}
                                NoViableAltException nvae =
                                    new NoViableAltException("1251:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )", 48, 0, input);

                                throw nvae;
                            }
                            switch (alt48) {
                                case 1 :
                                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1252:14: ( ACCUMULATE )=> (ac= accumulate_statement )
                                    {
                                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1252:32: (ac= accumulate_statement )
                                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1252:33: ac= accumulate_statement
                                    {
                                    pushFollow(FOLLOW_accumulate_statement_in_lhs_unary3555);
                                    ac=accumulate_statement();
                                    _fsp--;
                                    if (failed) return d;
                                    if ( backtracking==0 ) {
                                      ac.setResultPattern((PatternDescr) u); u=ac;
                                    }

                                    }


                                    }
                                    break;
                                case 2 :
                                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1253:14: ( COLLECT )=> (cs= collect_statement )
                                    {
                                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1253:29: (cs= collect_statement )
                                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1253:30: cs= collect_statement
                                    {
                                    pushFollow(FOLLOW_collect_statement_in_lhs_unary3584);
                                    cs=collect_statement();
                                    _fsp--;
                                    if (failed) return d;
                                    if ( backtracking==0 ) {
                                      cs.setResultPattern((PatternDescr) u); u=cs;
                                    }

                                    }


                                    }
                                    break;
                                case 3 :
                                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1254:14: (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement )
                                    {
                                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1254:43: (fm= from_statement )
                                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1254:44: fm= from_statement
                                    {
                                    pushFollow(FOLLOW_from_statement_in_lhs_unary3619);
                                    fm=from_statement();
                                    _fsp--;
                                    if (failed) return d;
                                    if ( backtracking==0 ) {
                                      fm.setPattern((PatternDescr) u); u=fm;
                                    }

                                    }


                                    }
                                    break;

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1257:5: u= lhs_forall
                    {
                    pushFollow(FOLLOW_lhs_forall_in_lhs_unary3658);
                    u=lhs_forall();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 6 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1258:5: '(' u= lhs_or ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary3666); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_unary3670);
                    u=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary3672); if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               d = u; 
            }
            pushFollow(FOLLOW_opt_semicolon_in_lhs_unary3682);
            opt_semicolon();
            _fsp--;
            if (failed) return d;

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1263:1: lhs_exist returns [BaseDescr d] : loc= EXISTS ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern ) ;
    public final BaseDescr lhs_exist() throws RecognitionException {
        BaseDescr d = null;

        Token loc=null;
        Token end=null;
        BaseDescr pattern = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1267:4: (loc= EXISTS ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1267:4: loc= EXISTS ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )
            {
            loc=(Token)input.LT(1);
            match(input,EXISTS,FOLLOW_EXISTS_in_lhs_exist3706); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new ExistsDescr( ); 
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1273:10: ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==LEFT_PAREN) ) {
                alt51=1;
            }
            else if ( (LA51_0==ID) ) {
                alt51=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1273:10: ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )", 51, 0, input);

                throw nvae;
            }
            switch (alt51) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1273:12: ( '(' pattern= lhs_or end= ')' )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1273:12: ( '(' pattern= lhs_or end= ')' )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1273:14: '(' pattern= lhs_or end= ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist3726); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist3730);
                    pattern=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( pattern != null ) ((ExistsDescr)d).addDescr( pattern ); 
                    }
                    end=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist3762); if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( end != null ) d.setEndCharacter( ((CommonToken)end).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1278:12: pattern= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_exist3812);
                    pattern=lhs_pattern();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {

                      	                	if ( pattern != null ) {
                      	                		((ExistsDescr)d).addDescr( pattern );
                      	                		d.setEndCharacter( pattern.getEndCharacter() );
                      	                	}
                      	                
                    }

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
        return d;
    }
    // $ANTLR end lhs_exist


    // $ANTLR start lhs_not
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1288:1: lhs_not returns [NotDescr d] : loc= NOT ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern ) ;
    public final NotDescr lhs_not() throws RecognitionException {
        NotDescr d = null;

        Token loc=null;
        Token end=null;
        BaseDescr pattern = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1292:4: (loc= NOT ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1292:4: loc= NOT ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )
            {
            loc=(Token)input.LT(1);
            match(input,NOT,FOLLOW_NOT_in_lhs_not3866); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new NotDescr( ); 
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1298:3: ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==LEFT_PAREN) ) {
                alt52=1;
            }
            else if ( (LA52_0==ID) ) {
                alt52=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1298:3: ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )", 52, 0, input);

                throw nvae;
            }
            switch (alt52) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1298:5: ( '(' pattern= lhs_or end= ')' )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1298:5: ( '(' pattern= lhs_or end= ')' )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1298:7: '(' pattern= lhs_or end= ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not3879); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_not3883);
                    pattern=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( pattern != null ) d.addDescr( pattern ); 
                    }
                    end=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not3916); if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( end != null ) d.setEndCharacter( ((CommonToken)end).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1304:3: pattern= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_not3953);
                    pattern=lhs_pattern();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {

                      	                	if ( pattern != null ) {
                      	                		d.addDescr( pattern );
                      	                		d.setEndCharacter( pattern.getEndCharacter() );
                      	                	}
                      	                
                    }

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
        return d;
    }
    // $ANTLR end lhs_not


    // $ANTLR start lhs_eval
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1314:1: lhs_eval returns [BaseDescr d] : loc= EVAL c= paren_chunk[d] ;
    public final BaseDescr lhs_eval() throws RecognitionException {
        BaseDescr d = null;

        Token loc=null;
        String c = null;



        		d = new EvalDescr( );
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1319:3: (loc= EVAL c= paren_chunk[d] )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1319:3: loc= EVAL c= paren_chunk[d]
            {
            loc=(Token)input.LT(1);
            match(input,EVAL,FOLLOW_EVAL_in_lhs_eval4001); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval4005);
            c=paren_chunk(d);
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
              			if ( loc != null ) d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		        if( c != null ) {
              		            String body = c.substring(1, c.length()-1);
              			    checkTrailingSemicolon( body, offset(loc.getLine()) );
              			    ((EvalDescr) d).setContent( body );
              			}
              		
            }

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


    // $ANTLR start lhs_forall
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1330:1: lhs_forall returns [ForallDescr d] : loc= FORALL '(' base= lhs_pattern ( ( ',' )? pattern= lhs_pattern )+ end= ')' ;
    public final ForallDescr lhs_forall() throws RecognitionException {
        ForallDescr d = null;

        Token loc=null;
        Token end=null;
        BaseDescr base = null;

        BaseDescr pattern = null;



        		d = factory.createForall();
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1334:4: (loc= FORALL '(' base= lhs_pattern ( ( ',' )? pattern= lhs_pattern )+ end= ')' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1334:4: loc= FORALL '(' base= lhs_pattern ( ( ',' )? pattern= lhs_pattern )+ end= ')'
            {
            loc=(Token)input.LT(1);
            match(input,FORALL,FOLLOW_FORALL_in_lhs_forall4034); if (failed) return d;
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_forall4036); if (failed) return d;
            pushFollow(FOLLOW_lhs_pattern_in_lhs_forall4040);
            base=lhs_pattern();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {

              			if ( loc != null ) d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		        // adding the base pattern
              		        d.addDescr( base );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1341:3: ( ( ',' )? pattern= lhs_pattern )+
            int cnt54=0;
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);

                if ( (LA54_0==ID||LA54_0==66) ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1341:5: ( ',' )? pattern= lhs_pattern
            	    {
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1341:5: ( ',' )?
            	    int alt53=2;
            	    int LA53_0 = input.LA(1);

            	    if ( (LA53_0==66) ) {
            	        alt53=1;
            	    }
            	    switch (alt53) {
            	        case 1 :
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1341:6: ','
            	            {
            	            match(input,66,FOLLOW_66_in_lhs_forall4054); if (failed) return d;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_lhs_pattern_in_lhs_forall4060);
            	    pattern=lhs_pattern();
            	    _fsp--;
            	    if (failed) return d;
            	    if ( backtracking==0 ) {

            	      		        // adding additional patterns
            	      			d.addDescr( pattern );
            	      		
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt54 >= 1 ) break loop54;
            	    if (backtracking>0) {failed=true; return d;}
                        EarlyExitException eee =
                            new EarlyExitException(54, input);
                        throw eee;
                }
                cnt54++;
            } while (true);

            end=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_forall4075); if (failed) return d;
            if ( backtracking==0 ) {

              		        if ( end != null ) d.setEndCharacter( ((CommonToken)end).getStopIndex() );
              		
            }

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
    // $ANTLR end lhs_forall


    // $ANTLR start dotted_name
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1353:1: dotted_name[BaseDescr descr] returns [String name] : id= ID ( '.' ident= identifier )* ( '[' loc= ']' )* ;
    public final String dotted_name(BaseDescr descr) throws RecognitionException {
        String name = null;

        Token id=null;
        Token loc=null;
        Token ident = null;



        		name = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1358:3: (id= ID ( '.' ident= identifier )* ( '[' loc= ']' )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1358:3: id= ID ( '.' ident= identifier )* ( '[' loc= ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name4106); if (failed) return name;
            if ( backtracking==0 ) {
               
              		    name=id.getText(); 
              		    if( descr != null ) {
              			descr.setStartCharacter( ((CommonToken)id).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)id).getStopIndex() );
              		    }
              		
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1366:3: ( '.' ident= identifier )*
            loop55:
            do {
                int alt55=2;
                int LA55_0 = input.LA(1);

                if ( (LA55_0==67) ) {
                    alt55=1;
                }


                switch (alt55) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1366:5: '.' ident= identifier
            	    {
            	    match(input,67,FOLLOW_67_in_dotted_name4118); if (failed) return name;
            	    pushFollow(FOLLOW_identifier_in_dotted_name4122);
            	    ident=identifier();
            	    _fsp--;
            	    if (failed) return name;
            	    if ( backtracking==0 ) {
            	       
            	      		        name = name + "." + ident.getText(); 
            	          		        if( descr != null ) {
            	      			    descr.setEndCharacter( ((CommonToken)ident).getStopIndex() );
            	      		        }
            	      		    
            	    }

            	    }
            	    break;

            	default :
            	    break loop55;
                }
            } while (true);

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1374:3: ( '[' loc= ']' )*
            loop56:
            do {
                int alt56=2;
                int LA56_0 = input.LA(1);

                if ( (LA56_0==LEFT_SQUARE) ) {
                    alt56=1;
                }


                switch (alt56) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1374:5: '[' loc= ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dotted_name4144); if (failed) return name;
            	    loc=(Token)input.LT(1);
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dotted_name4148); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       
            	      		        name = name + "[]";
            	          		        if( descr != null ) {
            	      			    descr.setEndCharacter( ((CommonToken)loc).getStopIndex() );
            	      		        }
            	      		    
            	    }

            	    }
            	    break;

            	default :
            	    break loop56;
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


    // $ANTLR start argument
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1384:1: argument returns [String name] : id= identifier ( '[' ']' )* ;
    public final String argument() throws RecognitionException {
        String name = null;

        Token id = null;



        		name = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1389:3: (id= identifier ( '[' ']' )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1389:3: id= identifier ( '[' ']' )*
            {
            pushFollow(FOLLOW_identifier_in_argument4187);
            id=identifier();
            _fsp--;
            if (failed) return name;
            if ( backtracking==0 ) {
               name=id.getText(); 
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1389:40: ( '[' ']' )*
            loop57:
            do {
                int alt57=2;
                int LA57_0 = input.LA(1);

                if ( (LA57_0==LEFT_SQUARE) ) {
                    alt57=1;
                }


                switch (alt57) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1389:42: '[' ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_argument4193); if (failed) return name;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_argument4195); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       name = name + "[]";
            	    }

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
    // $ANTLR end argument


    // $ANTLR start rhs_chunk
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1392:1: rhs_chunk[RuleDescr rule] : start= THEN (~ END )* loc= END ;
    public final void rhs_chunk(RuleDescr rule) throws RecognitionException {
        Token start=null;
        Token loc=null;


                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1398:10: (start= THEN (~ END )* loc= END )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1398:10: start= THEN (~ END )* loc= END
            {
            if ( backtracking==0 ) {

              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            start=(Token)input.LT(1);
            match(input,THEN,FOLLOW_THEN_in_rhs_chunk4239); if (failed) return ;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1404:3: (~ END )*
            loop58:
            do {
                int alt58=2;
                int LA58_0 = input.LA(1);

                if ( ((LA58_0>=ATTRIBUTES && LA58_0<=QUERY)||(LA58_0>=TEMPLATE && LA58_0<=79)) ) {
                    alt58=1;
                }


                switch (alt58) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1405:6: ~ END
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=QUERY)||(input.LA(1)>=TEMPLATE && input.LA(1)<=79) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_rhs_chunk4251);    throw mse;
            	    }

            	    if ( backtracking==0 ) {

            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop58;
                }
            } while (true);

            if ( backtracking==0 ) {

              		    if( channel != null ) {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, channel.intValue());
              		    } else {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, Token.HIDDEN_CHANNEL);
              		    }
              		
            }
            loc=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_rhs_chunk4288); if (failed) return ;
            if ( backtracking==0 ) {

                                  // ignoring first line in the consequence
                                  int index = 0;
                                  while( (index < buf.length() ) && Character.isWhitespace( buf.charAt( index ) ) &&
                                         (buf.charAt( index ) != 10 ) && (buf.charAt( index ) != 13 ))
                                             index++;
                                  if( (index < buf.length() ) && ( buf.charAt( index ) == '\r' ) )
                                      index++;
                                  if( (index < buf.length() ) && ( buf.charAt( index ) == '\n' ) )
                                      index++;
                                  
              		    rule.setConsequence( buf.substring( index ) );
                   		    rule.setConsequenceLocation(offset(start.getLine()), start.getCharPositionInLine());
               		    rule.setEndCharacter( ((CommonToken)loc).getStopIndex() );
                              
            }

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
    // $ANTLR end rhs_chunk


    // $ANTLR start name
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1435:1: name returns [String s] : (tok= ID | str= STRING ) ;
    public final String name() throws RecognitionException {
        String s = null;

        Token tok=null;
        Token str=null;

        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1437:2: ( (tok= ID | str= STRING ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1437:2: (tok= ID | str= STRING )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1437:2: (tok= ID | str= STRING )
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==ID) ) {
                alt59=1;
            }
            else if ( (LA59_0==STRING) ) {
                alt59=2;
            }
            else {
                if (backtracking>0) {failed=true; return s;}
                NoViableAltException nvae =
                    new NoViableAltException("1437:2: (tok= ID | str= STRING )", 59, 0, input);

                throw nvae;
            }
            switch (alt59) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1438:6: tok= ID
                    {
                    tok=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_name4332); if (failed) return s;
                    if ( backtracking==0 ) {

                      	        s = tok.getText();
                      	    
                    }

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1443:6: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_name4351); if (failed) return s;
                    if ( backtracking==0 ) {

                      	       s = getString( str );
                      	    
                    }

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
        return s;
    }
    // $ANTLR end name


    // $ANTLR start identifier
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1450:1: identifier returns [Token tok] : (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END ) ;
    public final Token identifier() throws RecognitionException {
        Token tok = null;

        Token t=null;

        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1452:2: ( (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1452:2: (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1452:2: (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END )
            int alt60=31;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt60=1;
                }
                break;
            case PACKAGE:
                {
                alt60=2;
                }
                break;
            case FUNCTION:
                {
                alt60=3;
                }
                break;
            case GLOBAL:
                {
                alt60=4;
                }
                break;
            case IMPORT:
                {
                alt60=5;
                }
                break;
            case RULE:
                {
                alt60=6;
                }
                break;
            case QUERY:
                {
                alt60=7;
                }
                break;
            case TEMPLATE:
                {
                alt60=8;
                }
                break;
            case ATTRIBUTES:
                {
                alt60=9;
                }
                break;
            case ENABLED:
                {
                alt60=10;
                }
                break;
            case SALIENCE:
                {
                alt60=11;
                }
                break;
            case DURATION:
                {
                alt60=12;
                }
                break;
            case FROM:
                {
                alt60=13;
                }
                break;
            case ACCUMULATE:
                {
                alt60=14;
                }
                break;
            case INIT:
                {
                alt60=15;
                }
                break;
            case ACTION:
                {
                alt60=16;
                }
                break;
            case RESULT:
                {
                alt60=17;
                }
                break;
            case COLLECT:
                {
                alt60=18;
                }
                break;
            case OR:
                {
                alt60=19;
                }
                break;
            case AND:
                {
                alt60=20;
                }
                break;
            case CONTAINS:
                {
                alt60=21;
                }
                break;
            case EXCLUDES:
                {
                alt60=22;
                }
                break;
            case MATCHES:
                {
                alt60=23;
                }
                break;
            case NULL:
                {
                alt60=24;
                }
                break;
            case EXISTS:
                {
                alt60=25;
                }
                break;
            case NOT:
                {
                alt60=26;
                }
                break;
            case EVAL:
                {
                alt60=27;
                }
                break;
            case FORALL:
                {
                alt60=28;
                }
                break;
            case WHEN:
                {
                alt60=29;
                }
                break;
            case THEN:
                {
                alt60=30;
                }
                break;
            case END:
                {
                alt60=31;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return tok;}
                NoViableAltException nvae =
                    new NoViableAltException("1452:2: (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END )", 60, 0, input);

                throw nvae;
            }

            switch (alt60) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1452:10: t= ID
                    {
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_identifier4389); if (failed) return tok;

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1453:4: t= PACKAGE
                    {
                    t=(Token)input.LT(1);
                    match(input,PACKAGE,FOLLOW_PACKAGE_in_identifier4402); if (failed) return tok;

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1454:4: t= FUNCTION
                    {
                    t=(Token)input.LT(1);
                    match(input,FUNCTION,FOLLOW_FUNCTION_in_identifier4409); if (failed) return tok;

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1455:4: t= GLOBAL
                    {
                    t=(Token)input.LT(1);
                    match(input,GLOBAL,FOLLOW_GLOBAL_in_identifier4416); if (failed) return tok;

                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1456:4: t= IMPORT
                    {
                    t=(Token)input.LT(1);
                    match(input,IMPORT,FOLLOW_IMPORT_in_identifier4423); if (failed) return tok;

                    }
                    break;
                case 6 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1457:4: t= RULE
                    {
                    t=(Token)input.LT(1);
                    match(input,RULE,FOLLOW_RULE_in_identifier4432); if (failed) return tok;

                    }
                    break;
                case 7 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1458:4: t= QUERY
                    {
                    t=(Token)input.LT(1);
                    match(input,QUERY,FOLLOW_QUERY_in_identifier4439); if (failed) return tok;

                    }
                    break;
                case 8 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1459:17: t= TEMPLATE
                    {
                    t=(Token)input.LT(1);
                    match(input,TEMPLATE,FOLLOW_TEMPLATE_in_identifier4460); if (failed) return tok;

                    }
                    break;
                case 9 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1460:17: t= ATTRIBUTES
                    {
                    t=(Token)input.LT(1);
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_identifier4488); if (failed) return tok;

                    }
                    break;
                case 10 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1461:17: t= ENABLED
                    {
                    t=(Token)input.LT(1);
                    match(input,ENABLED,FOLLOW_ENABLED_in_identifier4514); if (failed) return tok;

                    }
                    break;
                case 11 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1462:17: t= SALIENCE
                    {
                    t=(Token)input.LT(1);
                    match(input,SALIENCE,FOLLOW_SALIENCE_in_identifier4543); if (failed) return tok;

                    }
                    break;
                case 12 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1463:17: t= DURATION
                    {
                    t=(Token)input.LT(1);
                    match(input,DURATION,FOLLOW_DURATION_in_identifier4565); if (failed) return tok;

                    }
                    break;
                case 13 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1464:17: t= FROM
                    {
                    t=(Token)input.LT(1);
                    match(input,FROM,FOLLOW_FROM_in_identifier4587); if (failed) return tok;

                    }
                    break;
                case 14 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1465:17: t= ACCUMULATE
                    {
                    t=(Token)input.LT(1);
                    match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_identifier4616); if (failed) return tok;

                    }
                    break;
                case 15 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1466:17: t= INIT
                    {
                    t=(Token)input.LT(1);
                    match(input,INIT,FOLLOW_INIT_in_identifier4638); if (failed) return tok;

                    }
                    break;
                case 16 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1467:17: t= ACTION
                    {
                    t=(Token)input.LT(1);
                    match(input,ACTION,FOLLOW_ACTION_in_identifier4667); if (failed) return tok;

                    }
                    break;
                case 17 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1468:17: t= RESULT
                    {
                    t=(Token)input.LT(1);
                    match(input,RESULT,FOLLOW_RESULT_in_identifier4696); if (failed) return tok;

                    }
                    break;
                case 18 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1469:17: t= COLLECT
                    {
                    t=(Token)input.LT(1);
                    match(input,COLLECT,FOLLOW_COLLECT_in_identifier4725); if (failed) return tok;

                    }
                    break;
                case 19 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1470:17: t= OR
                    {
                    t=(Token)input.LT(1);
                    match(input,OR,FOLLOW_OR_in_identifier4754); if (failed) return tok;

                    }
                    break;
                case 20 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1471:17: t= AND
                    {
                    t=(Token)input.LT(1);
                    match(input,AND,FOLLOW_AND_in_identifier4783); if (failed) return tok;

                    }
                    break;
                case 21 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1472:17: t= CONTAINS
                    {
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_identifier4812); if (failed) return tok;

                    }
                    break;
                case 22 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1473:17: t= EXCLUDES
                    {
                    t=(Token)input.LT(1);
                    match(input,EXCLUDES,FOLLOW_EXCLUDES_in_identifier4834); if (failed) return tok;

                    }
                    break;
                case 23 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1474:17: t= MATCHES
                    {
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_identifier4856); if (failed) return tok;

                    }
                    break;
                case 24 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1475:17: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_identifier4885); if (failed) return tok;

                    }
                    break;
                case 25 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1476:17: t= EXISTS
                    {
                    t=(Token)input.LT(1);
                    match(input,EXISTS,FOLLOW_EXISTS_in_identifier4914); if (failed) return tok;

                    }
                    break;
                case 26 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1477:17: t= NOT
                    {
                    t=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_identifier4943); if (failed) return tok;

                    }
                    break;
                case 27 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1478:17: t= EVAL
                    {
                    t=(Token)input.LT(1);
                    match(input,EVAL,FOLLOW_EVAL_in_identifier4972); if (failed) return tok;

                    }
                    break;
                case 28 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1479:17: t= FORALL
                    {
                    t=(Token)input.LT(1);
                    match(input,FORALL,FOLLOW_FORALL_in_identifier5001); if (failed) return tok;

                    }
                    break;
                case 29 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1480:17: t= WHEN
                    {
                    t=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_identifier5039); if (failed) return tok;

                    }
                    break;
                case 30 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1481:17: t= THEN
                    {
                    t=(Token)input.LT(1);
                    match(input,THEN,FOLLOW_THEN_in_identifier5071); if (failed) return tok;

                    }
                    break;
                case 31 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1482:17: t= END
                    {
                    t=(Token)input.LT(1);
                    match(input,END,FOLLOW_END_in_identifier5100); if (failed) return tok;

                    }
                    break;

            }

            if ( backtracking==0 ) {

              	    tok = t;
              	
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return tok;
    }
    // $ANTLR end identifier

    // $ANTLR start synpred7
    public final void synpred7_fragment() throws RecognitionException {   
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:195:4: ( function_import_statement )
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:195:4: function_import_statement
        {
        pushFollow(FOLLOW_function_import_statement_in_synpred7150);
        function_import_statement();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred7

    // $ANTLR start synpred8
    public final void synpred8_fragment() throws RecognitionException {   
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:196:4: ( import_statement )
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:196:4: import_statement
        {
        pushFollow(FOLLOW_import_statement_in_synpred8156);
        import_statement();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred8

    // $ANTLR start synpred42
    public final void synpred42_fragment() throws RecognitionException {   
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:742:4: ( paren_chunk[from] )
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:742:4: paren_chunk[from]
        {
        pushFollow(FOLLOW_paren_chunk_in_synpred421876);
        paren_chunk(from);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred42

    // $ANTLR start synpred44
    public final void synpred44_fragment() throws RecognitionException {   
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:772:6: ( LEFT_SQUARE )
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:772:8: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred441942); if (failed) return ;

        }
    }
    // $ANTLR end synpred44

    // $ANTLR start synpred45
    public final void synpred45_fragment() throws RecognitionException {   
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:777:6: ( LEFT_PAREN )
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:777:8: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred451976); if (failed) return ;

        }
    }
    // $ANTLR end synpred45

    // $ANTLR start synpred49
    public final void synpred49_fragment() throws RecognitionException {   
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:874:6: ( ( OR | '||' ) fact )
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:874:6: ( OR | '||' ) fact
        {
        if ( input.LA(1)==OR||input.LA(1)==69 ) {
            input.consume();
            errorRecovery=false;failed=false;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            MismatchedSetException mse =
                new MismatchedSetException(null,input);
            recoverFromMismatchedSet(input,mse,FOLLOW_set_in_synpred492284);    throw mse;
        }

        pushFollow(FOLLOW_fact_in_synpred492302);
        fact();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred49

    // $ANTLR start synpred91
    public final void synpred91_fragment() throws RecognitionException {   
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1252:14: ( ACCUMULATE )
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1252:16: ACCUMULATE
        {
        match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_synpred913546); if (failed) return ;

        }
    }
    // $ANTLR end synpred91

    // $ANTLR start synpred92
    public final void synpred92_fragment() throws RecognitionException {   
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1253:14: ( COLLECT )
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1253:16: COLLECT
        {
        match(input,COLLECT,FOLLOW_COLLECT_in_synpred923575); if (failed) return ;

        }
    }
    // $ANTLR end synpred92

    // $ANTLR start synpred94
    public final void synpred94_fragment() throws RecognitionException {   
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1254:14: (~ ( ACCUMULATE | COLLECT ) )
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1254:16: ~ ( ACCUMULATE | COLLECT )
        {
        if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=LOCK_ON_ACTIVE)||(input.LA(1)>=INIT && input.LA(1)<=RESULT)||(input.LA(1)>=ID && input.LA(1)<=79) ) {
            input.consume();
            errorRecovery=false;failed=false;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            MismatchedSetException mse =
                new MismatchedSetException(null,input);
            recoverFromMismatchedSet(input,mse,FOLLOW_set_in_synpred943605);    throw mse;
        }


        }
    }
    // $ANTLR end synpred94

    public final boolean synpred44() {
        backtracking++;
        int start = input.mark();
        try {
            synpred44_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred92() {
        backtracking++;
        int start = input.mark();
        try {
            synpred92_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred49() {
        backtracking++;
        int start = input.mark();
        try {
            synpred49_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred7() {
        backtracking++;
        int start = input.mark();
        try {
            synpred7_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred94() {
        backtracking++;
        int start = input.mark();
        try {
            synpred94_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred45() {
        backtracking++;
        int start = input.mark();
        try {
            synpred45_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred91() {
        backtracking++;
        int start = input.mark();
        try {
            synpred91_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred8() {
        backtracking++;
        int start = input.mark();
        try {
            synpred8_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred42() {
        backtracking++;
        int start = input.mark();
        try {
            synpred42_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }


    protected DFA11 dfa11 = new DFA11(this);
    protected DFA12 dfa12 = new DFA12(this);
    static final String DFA11_eotS =
        "\6\uffff";
    static final String DFA11_eofS =
        "\6\uffff";
    static final String DFA11_minS =
        "\2\4\1\uffff\1\56\1\uffff\1\4";
    static final String DFA11_maxS =
        "\1\65\1\103\1\uffff\1\56\1\uffff\1\102";
    static final String DFA11_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\1\uffff";
    static final String DFA11_specialS =
        "\6\uffff}>";
    static final String[] DFA11_transitionS = {
            "\12\2\3\uffff\1\2\1\uffff\1\2\6\uffff\1\2\2\uffff\5\2\1\1\1"+
            "\2\2\uffff\3\2\1\uffff\1\2\4\uffff\7\2",
            "\12\4\3\uffff\1\4\1\uffff\1\4\6\uffff\1\4\2\uffff\7\4\1\uffff"+
            "\1\2\3\4\1\uffff\1\4\2\uffff\1\3\1\uffff\7\4\14\uffff\1\2\1"+
            "\4",
            "",
            "\1\5",
            "",
            "\12\4\3\uffff\1\4\1\uffff\1\4\6\uffff\1\4\2\uffff\7\4\1\uffff"+
            "\1\2\3\4\1\uffff\1\4\2\uffff\1\3\1\uffff\7\4\14\uffff\1\2"
    };

    static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
    static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
    static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
    static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
    static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
    static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
    static final short[][] DFA11_transition;

    static {
        int numStates = DFA11_transitionS.length;
        DFA11_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
        }
    }

    class DFA11 extends DFA {

        public DFA11(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 11;
            this.eot = DFA11_eot;
            this.eof = DFA11_eof;
            this.min = DFA11_min;
            this.max = DFA11_max;
            this.accept = DFA11_accept;
            this.special = DFA11_special;
            this.transition = DFA11_transition;
        }
        public String getDescription() {
            return "313:6: (paramType= dotted_name[null] )?";
        }
    }
    static final String DFA12_eotS =
        "\6\uffff";
    static final String DFA12_eofS =
        "\6\uffff";
    static final String DFA12_minS =
        "\2\4\2\uffff\1\56\1\4";
    static final String DFA12_maxS =
        "\1\65\1\103\2\uffff\1\56\1\102";
    static final String DFA12_acceptS =
        "\2\uffff\1\2\1\1\2\uffff";
    static final String DFA12_specialS =
        "\6\uffff}>";
    static final String[] DFA12_transitionS = {
            "\12\2\3\uffff\1\2\1\uffff\1\2\6\uffff\1\2\2\uffff\5\2\1\1\1"+
            "\2\2\uffff\3\2\1\uffff\1\2\4\uffff\7\2",
            "\12\3\3\uffff\1\3\1\uffff\1\3\6\uffff\1\3\2\uffff\7\3\1\uffff"+
            "\1\2\3\3\1\uffff\1\3\2\uffff\1\4\1\uffff\7\3\14\uffff\1\2\1"+
            "\3",
            "",
            "",
            "\1\5",
            "\12\3\3\uffff\1\3\1\uffff\1\3\6\uffff\1\3\2\uffff\7\3\1\uffff"+
            "\1\2\3\3\1\uffff\1\3\2\uffff\1\4\1\uffff\7\3\14\uffff\1\2"
    };

    static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
    static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
    static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
    static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
    static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
    static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
    static final short[][] DFA12_transition;

    static {
        int numStates = DFA12_transitionS.length;
        DFA12_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
        }
    }

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = DFA12_eot;
            this.eof = DFA12_eof;
            this.min = DFA12_min;
            this.max = DFA12_max;
            this.accept = DFA12_accept;
            this.special = DFA12_special;
            this.transition = DFA12_transition;
        }
        public String getDescription() {
            return "317:11: (paramType= dotted_name[null] )?";
        }
    }
 

    public static final BitSet FOLLOW_64_in_opt_semicolon46 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit58 = new BitSet(new long[]{0x0000000000001BC0L});
    public static final BitSet FOLLOW_statement_in_compilation_unit65 = new BitSet(new long[]{0x0000000000001BC2L});
    public static final BitSet FOLLOW_package_statement_in_prolog90 = new BitSet(new long[]{0x000000001FEB4012L,0x0000000000000004L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_prolog105 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_prolog107 = new BitSet(new long[]{0x000000001FEB4002L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_prolog116 = new BitSet(new long[]{0x000000001FEB4000L});
    public static final BitSet FOLLOW_rule_attribute_in_prolog121 = new BitSet(new long[]{0x000000001FEB4002L,0x0000000000000004L});
    public static final BitSet FOLLOW_function_import_statement_in_statement150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_package_statement232 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement236 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_opt_semicolon_in_package_statement239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_statement271 = new BitSet(new long[]{0x003F85CFE40A3FF0L});
    public static final BitSet FOLLOW_import_name_in_import_statement294 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_opt_semicolon_in_import_statement297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_function_import_statement323 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FUNCTION_in_function_import_statement325 = new BitSet(new long[]{0x003F85CFE40A3FF0L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement348 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_opt_semicolon_in_function_import_statement351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_import_name379 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000018L});
    public static final BitSet FOLLOW_67_in_import_name391 = new BitSet(new long[]{0x003F85CFE40A3FF0L});
    public static final BitSet FOLLOW_identifier_in_import_name395 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000018L});
    public static final BitSet FOLLOW_68_in_import_name419 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GLOBAL_in_global455 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_dotted_name_in_global466 = new BitSet(new long[]{0x003F85CFE40A3FF0L});
    public static final BitSet FOLLOW_identifier_in_global478 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_opt_semicolon_in_global480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function507 = new BitSet(new long[]{0x003F85CFE40A3FF0L});
    public static final BitSet FOLLOW_dotted_name_in_function512 = new BitSet(new long[]{0x003F85CFE40A3FF0L});
    public static final BitSet FOLLOW_identifier_in_function519 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function528 = new BitSet(new long[]{0x003F85EFE40A3FF0L});
    public static final BitSet FOLLOW_dotted_name_in_function538 = new BitSet(new long[]{0x003F85CFE40A3FF0L});
    public static final BitSet FOLLOW_argument_in_function545 = new BitSet(new long[]{0x0000002000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_function559 = new BitSet(new long[]{0x003F85CFE40A3FF0L});
    public static final BitSet FOLLOW_dotted_name_in_function564 = new BitSet(new long[]{0x003F85CFE40A3FF0L});
    public static final BitSet FOLLOW_argument_in_function571 = new BitSet(new long[]{0x0000002000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function595 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_curly_chunk_in_function601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_query633 = new BitSet(new long[]{0x0000000400008000L});
    public static final BitSet FOLLOW_name_in_query637 = new BitSet(new long[]{0x001E001400000400L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query650 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_END_in_query667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_template697 = new BitSet(new long[]{0x003F85CFE40A3FF0L});
    public static final BitSet FOLLOW_identifier_in_template701 = new BitSet(new long[]{0x0000000400000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_opt_semicolon_in_template703 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_template_slot_in_template718 = new BitSet(new long[]{0x0000000400000400L});
    public static final BitSet FOLLOW_END_in_template735 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_opt_semicolon_in_template737 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_template_slot783 = new BitSet(new long[]{0x003F85CFE40A3FF0L});
    public static final BitSet FOLLOW_identifier_in_template_slot801 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_opt_semicolon_in_template_slot803 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule836 = new BitSet(new long[]{0x0000000400008000L});
    public static final BitSet FOLLOW_name_in_rule840 = new BitSet(new long[]{0x002000001FEB6010L,0x0000000000000004L});
    public static final BitSet FOLLOW_rule_attributes_in_rule849 = new BitSet(new long[]{0x0020000000002000L});
    public static final BitSet FOLLOW_WHEN_in_rule858 = new BitSet(new long[]{0x003E001400000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_rule860 = new BitSet(new long[]{0x003E001400000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule878 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_rhs_chunk_in_rule899 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_rule_attributes920 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_rule_attributes922 = new BitSet(new long[]{0x000000001FEB4002L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_rule_attributes931 = new BitSet(new long[]{0x000000001FEB4000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes936 = new BitSet(new long[]{0x000000001FEB4002L,0x0000000000000004L});
    public static final BitSet FOLLOW_salience_in_rule_attribute977 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute1011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute1025 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute1047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute1057 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute1067 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_in_rule_attribute1077 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_in_rule_attribute1087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_in_rule_attribute1096 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EFFECTIVE_in_date_effective1128 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_date_effective1132 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EXPIRES_in_date_expires1165 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_date_expires1169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENABLED_in_enabled1204 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_BOOL_in_enabled1208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_salience1253 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_INT_in_salience1257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NO_LOOP_in_no_loop1295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NO_LOOP_in_no_loop1323 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AUTO_FOCUS_in_auto_focus1376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AUTO_FOCUS_in_auto_focus1404 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTIVATION_GROUP_in_activation_group1453 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_activation_group1457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1489 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_ruleflow_group1493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AGENDA_GROUP_in_agenda_group1525 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DURATION_in_duration1563 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_INT_in_duration1567 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIALECT_in_dialect1599 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_dialect1603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1652 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1680 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_BOOL_in_lock_on_active1684 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1722 = new BitSet(new long[]{0x001E001400000002L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_pattern1787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_pattern1796 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_source_in_from_statement1823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_from_source1865 = new BitSet(new long[]{0x0000001000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source1876 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_chain_in_from_source1890 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_expression_chain1915 = new BitSet(new long[]{0x003F85CFE40A3FF0L});
    public static final BitSet FOLLOW_identifier_in_expression_chain1919 = new BitSet(new long[]{0x0000201000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain1950 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain1984 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain2005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_accumulate_statement2046 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement2056 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_lhs_pattern_in_accumulate_statement2060 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_accumulate_statement2062 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_INIT_in_accumulate_statement2071 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2075 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_accumulate_statement2078 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_ACTION_in_accumulate_statement2087 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2091 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_accumulate_statement2094 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_RESULT_in_accumulate_statement2103 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2107 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement2112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_collect_statement2155 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement2165 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_lhs_pattern_in_collect_statement2169 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement2173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding2207 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_fact_binding2209 = new BitSet(new long[]{0x0000001400000000L});
    public static final BitSet FOLLOW_fact_expression_in_fact_binding2222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_expression2254 = new BitSet(new long[]{0x0000001400000000L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2258 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_expression2261 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_expression2272 = new BitSet(new long[]{0x0000000800000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_set_in_fact_expression2284 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_fact_in_fact_expression2302 = new BitSet(new long[]{0x0000000800000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_dotted_name_in_fact2363 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact2377 = new BitSet(new long[]{0x003F85FFE40A3FF0L});
    public static final BitSet FOLLOW_constraints_in_fact2387 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact2400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints2421 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_constraints2424 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_constraints2432 = new BitSet(new long[]{0x003F85DFE40A3FF0L});
    public static final BitSet FOLLOW_constraint_in_constraints2435 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_predicate_in_constraints2438 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_constraint2467 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_constraint2469 = new BitSet(new long[]{0x003F85CFE40A3FF0L});
    public static final BitSet FOLLOW_identifier_in_constraint2490 = new BitSet(new long[]{0x000001C000000002L,0x0000000000007F00L});
    public static final BitSet FOLLOW_constraint_expression_in_constraint2508 = new BitSet(new long[]{0x0000000000000002L,0x00000000000000C0L});
    public static final BitSet FOLLOW_set_in_constraint2529 = new BitSet(new long[]{0x000001C000000000L,0x0000000000007E00L});
    public static final BitSet FOLLOW_constraint_expression_in_constraint2549 = new BitSet(new long[]{0x0000000000000002L,0x00000000000000C0L});
    public static final BitSet FOLLOW_72_in_constraint2577 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_predicate_in_constraint2579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_constraint_expression2614 = new BitSet(new long[]{0x0000061400148000L});
    public static final BitSet FOLLOW_ID_in_constraint_expression2683 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enum_constraint_in_constraint_expression2699 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint_expression2722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint_expression2736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint2775 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint2786 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint2799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint2810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal_constraint2822 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enum_constraint2857 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_enum_constraint2863 = new BitSet(new long[]{0x003F85CFE40A3FF0L});
    public static final BitSet FOLLOW_identifier_in_enum_constraint2867 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate2909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk2958 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_set_in_paren_chunk2974 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk2998 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk3035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk3086 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_set_in_curly_chunk3102 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk3126 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk3163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk3226 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_set_in_square_chunk3242 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_square_chunk_in_square_chunk3266 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk3303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint3348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3376 = new BitSet(new long[]{0x0000000800000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_set_in_lhs_or3384 = new BitSet(new long[]{0x001E001400000000L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3395 = new BitSet(new long[]{0x0000000800000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3431 = new BitSet(new long[]{0x0000800000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_set_in_lhs_and3439 = new BitSet(new long[]{0x001E001400000000L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3450 = new BitSet(new long[]{0x0000800000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary3487 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary3495 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary3503 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_unary3511 = new BitSet(new long[]{0x0001000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_FROM_in_lhs_unary3527 = new BitSet(new long[]{0x003F85CFE40A3FF0L});
    public static final BitSet FOLLOW_accumulate_statement_in_lhs_unary3555 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_collect_statement_in_lhs_unary3584 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_from_statement_in_lhs_unary3619 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary3658 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary3666 = new BitSet(new long[]{0x001E001400000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_unary3670 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary3672 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_opt_semicolon_in_lhs_unary3682 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_lhs_exist3706 = new BitSet(new long[]{0x0000001400000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist3726 = new BitSet(new long[]{0x001E001400000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist3730 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist3762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_exist3812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_lhs_not3866 = new BitSet(new long[]{0x0000001400000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not3879 = new BitSet(new long[]{0x001E001400000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not3883 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not3916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_not3953 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_lhs_eval4001 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval4005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_lhs_forall4034 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall4036 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall4040 = new BitSet(new long[]{0x0000000400000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_lhs_forall4054 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall4060 = new BitSet(new long[]{0x0000002400000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall4075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name4106 = new BitSet(new long[]{0x0000200000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_dotted_name4118 = new BitSet(new long[]{0x003F85CFE40A3FF0L});
    public static final BitSet FOLLOW_identifier_in_dotted_name4122 = new BitSet(new long[]{0x0000200000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dotted_name4144 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dotted_name4148 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_identifier_in_argument4187 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument4193 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument4195 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk4239 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_set_in_rhs_chunk4251 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_END_in_rhs_chunk4288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_name4332 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_name4351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_identifier4389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_identifier4402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_identifier4409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GLOBAL_in_identifier4416 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_identifier4423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_identifier4432 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_identifier4439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_identifier4460 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_identifier4488 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENABLED_in_identifier4514 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_identifier4543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DURATION_in_identifier4565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_identifier4587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_identifier4616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INIT_in_identifier4638 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_identifier4667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RESULT_in_identifier4696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_identifier4725 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_identifier4754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_in_identifier4783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONTAINS_in_identifier4812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXCLUDES_in_identifier4834 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MATCHES_in_identifier4856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_identifier4885 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_identifier4914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_identifier4943 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_identifier4972 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_identifier5001 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_identifier5039 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THEN_in_identifier5071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_END_in_identifier5100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_synpred7150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_synpred8156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_synpred421876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred441942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred451976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred492284 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_fact_in_synpred492302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_synpred913546 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_synpred923575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred943605 = new BitSet(new long[]{0x0000000000000002L});

}