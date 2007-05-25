// $ANTLR 3.0 /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g 2007-05-25 16:51:44

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ATTRIBUTES", "PACKAGE", "IMPORT", "FUNCTION", "GLOBAL", "QUERY", "END", "TEMPLATE", "RULE", "WHEN", "DATE_EFFECTIVE", "STRING", "DATE_EXPIRES", "ENABLED", "BOOL", "SALIENCE", "INT", "NO_LOOP", "AUTO_FOCUS", "ACTIVATION_GROUP", "RULEFLOW_GROUP", "AGENDA_GROUP", "DURATION", "DIALECT", "LOCK_ON_ACTIVE", "ACCUMULATE", "LEFT_PAREN", "COMMA", "INIT", "ACTION", "RESULT", "RIGHT_PAREN", "COLLECT", "ID", "OR", "DOUBLE_PIPE", "DOUBLE_AMPER", "EVAL", "CONTAINS", "NOT", "EXCLUDES", "MATCHES", "MEMBEROF", "IN", "FLOAT", "NULL", "LEFT_CURLY", "RIGHT_CURLY", "LEFT_SQUARE", "RIGHT_SQUARE", "AND", "FROM", "EXISTS", "FORALL", "THEN", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "MISC", "';'", "':'", "'.'", "'.*'", "'->'", "'=='", "'>'", "'>='", "'<'", "'<='", "'!='"
    };
    public static final int EXISTS=56;
    public static final int COMMA=31;
    public static final int AUTO_FOCUS=22;
    public static final int END=10;
    public static final int HexDigit=62;
    public static final int FORALL=57;
    public static final int TEMPLATE=11;
    public static final int MISC=68;
    public static final int FLOAT=48;
    public static final int QUERY=9;
    public static final int THEN=58;
    public static final int RULE=12;
    public static final int INIT=32;
    public static final int IMPORT=6;
    public static final int DATE_EFFECTIVE=14;
    public static final int PACKAGE=5;
    public static final int OR=38;
    public static final int DOUBLE_PIPE=39;
    public static final int AND=54;
    public static final int FUNCTION=7;
    public static final int GLOBAL=8;
    public static final int EscapeSequence=61;
    public static final int DIALECT=27;
    public static final int INT=20;
    public static final int LOCK_ON_ACTIVE=28;
    public static final int DATE_EXPIRES=16;
    public static final int LEFT_SQUARE=52;
    public static final int CONTAINS=42;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=65;
    public static final int ATTRIBUTES=4;
    public static final int LEFT_CURLY=50;
    public static final int RESULT=34;
    public static final int FROM=55;
    public static final int ID=37;
    public static final int ACTIVATION_GROUP=23;
    public static final int LEFT_PAREN=30;
    public static final int DOUBLE_AMPER=40;
    public static final int RIGHT_CURLY=51;
    public static final int EXCLUDES=44;
    public static final int BOOL=18;
    public static final int MEMBEROF=46;
    public static final int WHEN=13;
    public static final int RULEFLOW_GROUP=24;
    public static final int WS=60;
    public static final int STRING=15;
    public static final int ACTION=33;
    public static final int COLLECT=36;
    public static final int IN=47;
    public static final int NO_LOOP=21;
    public static final int ACCUMULATE=29;
    public static final int UnicodeEscape=63;
    public static final int DURATION=26;
    public static final int EVAL=41;
    public static final int MATCHES=45;
    public static final int EOF=-1;
    public static final int EOL=59;
    public static final int NULL=49;
    public static final int AGENDA_GROUP=25;
    public static final int OctalEscape=64;
    public static final int SALIENCE=19;
    public static final int MULTI_LINE_COMMENT=67;
    public static final int RIGHT_PAREN=35;
    public static final int NOT=43;
    public static final int ENABLED=17;
    public static final int RIGHT_SQUARE=53;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=66;

        public DRLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[222+1];
         }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g"; }


    	private PackageDescr packageDescr;
    	private List errors = new ArrayList();
    	private String source = "unknown";
    	private int lineOffset = 0;
    	private DescrFactory factory = new DescrFactory();
    	private boolean parserDebug = false;
    	private Location location = new Location( Location.LOCATION_UNKNOWN );
    	
    	// THE FOLLOWING LINES ARE DUMMY ATTRIBUTES TO WORK AROUND AN ANTLR BUG
    	private BaseDescr from = null;
    	private FieldConstraintDescr fc = null;
    	private RestrictionConnectiveDescr and = null;
    	private RestrictionConnectiveDescr or = null;
    	private ConditionalElementDescr base = null;
    	
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
            
            public Location getLocation() {
                    return this.location;
            }
          



    // $ANTLR start opt_semicolon
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:177:1: opt_semicolon : ( ';' )? ;
    public final void opt_semicolon() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:178:4: ( ( ';' )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:178:4: ( ';' )?
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:178:4: ( ';' )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==69) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: ';'
                    {
                    match(input,69,FOLLOW_69_in_opt_semicolon46); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:181:1: compilation_unit : prolog ( statement )+ ;
    public final void compilation_unit() throws RecognitionException {

        		// reset Location information
        		this.location = new Location( Location.LOCATION_UNKNOWN );
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:186:4: ( prolog ( statement )+ )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:186:4: prolog ( statement )+
            {
            pushFollow(FOLLOW_prolog_in_compilation_unit64);
            prolog();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:187:3: ( statement )+
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:187:5: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_compilation_unit71);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:190:1: prolog : (n= package_statement )? ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )* ;
    public final void prolog() throws RecognitionException {
        String n = null;

        AttributeDescr a = null;



        		String packageName = "";
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:194:4: ( (n= package_statement )? ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:194:4: (n= package_statement )? ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:194:4: (n= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==PACKAGE) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:194:6: n= package_statement
                    {
                    pushFollow(FOLLOW_package_statement_in_prolog96);
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:198:4: ( ATTRIBUTES ':' )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==ATTRIBUTES) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:198:5: ATTRIBUTES ':'
                    {
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_prolog111); if (failed) return ;
                    match(input,70,FOLLOW_70_in_prolog113); if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:199:4: ( ( ',' )? a= rule_attribute )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==DATE_EFFECTIVE||(LA6_0>=DATE_EXPIRES && LA6_0<=ENABLED)||LA6_0==SALIENCE||(LA6_0>=NO_LOOP && LA6_0<=LOCK_ON_ACTIVE)||LA6_0==COMMA) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:199:6: ( ',' )? a= rule_attribute
            	    {
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:199:6: ( ',' )?
            	    int alt5=2;
            	    int LA5_0 = input.LA(1);

            	    if ( (LA5_0==COMMA) ) {
            	        alt5=1;
            	    }
            	    switch (alt5) {
            	        case 1 :
            	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: ','
            	            {
            	            match(input,COMMA,FOLLOW_COMMA_in_prolog122); if (failed) return ;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_rule_attribute_in_prolog127);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:206:1: statement : ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query ) ;
    public final void statement() throws RecognitionException {
        FactTemplateDescr t = null;

        RuleDescr r = null;

        QueryDescr q = null;


        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:208:2: ( ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:208:2: ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:208:2: ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query )
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
                        new NoViableAltException("208:2: ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query )", 7, 1, input);

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
                    new NoViableAltException("208:2: ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query )", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:208:4: function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement156);
                    function_import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:209:4: import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement162);
                    import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:210:4: global
                    {
                    pushFollow(FOLLOW_global_in_statement168);
                    global();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:211:4: function
                    {
                    pushFollow(FOLLOW_function_in_statement174);
                    function();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:212:10: t= template
                    {
                    pushFollow(FOLLOW_template_in_statement188);
                    t=template();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      this.packageDescr.addFactTemplate( t ); 
                    }

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:213:4: r= rule
                    {
                    pushFollow(FOLLOW_rule_in_statement197);
                    r=rule();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       if( r != null ) this.packageDescr.addRule( r ); 
                    }

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:214:4: q= query
                    {
                    pushFollow(FOLLOW_query_in_statement209);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:218:1: package_statement returns [String packageName] : PACKAGE n= dotted_name[null] opt_semicolon ;
    public final String package_statement() throws RecognitionException {
        String packageName = null;

        String n = null;



        		packageName = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:223:3: ( PACKAGE n= dotted_name[null] opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:223:3: PACKAGE n= dotted_name[null] opt_semicolon
            {
            match(input,PACKAGE,FOLLOW_PACKAGE_in_package_statement238); if (failed) return packageName;
            pushFollow(FOLLOW_dotted_name_in_package_statement242);
            n=dotted_name(null);
            _fsp--;
            if (failed) return packageName;
            pushFollow(FOLLOW_opt_semicolon_in_package_statement245);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:230:1: import_statement : imp= IMPORT import_name[importDecl] opt_semicolon ;
    public final void import_statement() throws RecognitionException {
        Token imp=null;


                	ImportDescr importDecl = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:234:4: (imp= IMPORT import_name[importDecl] opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:234:4: imp= IMPORT import_name[importDecl] opt_semicolon
            {
            imp=(Token)input.LT(1);
            match(input,IMPORT,FOLLOW_IMPORT_in_import_statement277); if (failed) return ;
            if ( backtracking==0 ) {

              	            importDecl = factory.createImport( );
              	            importDecl.setStartCharacter( ((CommonToken)imp).getStartIndex() );
              		    if (packageDescr != null) {
              			packageDescr.addImport( importDecl );
              		    }
              	        
            }
            pushFollow(FOLLOW_import_name_in_import_statement300);
            import_name(importDecl);
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_import_statement303);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:245:1: function_import_statement : imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon ;
    public final void function_import_statement() throws RecognitionException {
        Token imp=null;


                	FunctionImportDescr importDecl = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:249:4: (imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:249:4: imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon
            {
            imp=(Token)input.LT(1);
            match(input,IMPORT,FOLLOW_IMPORT_in_function_import_statement329); if (failed) return ;
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function_import_statement331); if (failed) return ;
            if ( backtracking==0 ) {

              	            importDecl = factory.createFunctionImport();
              	            importDecl.setStartCharacter( ((CommonToken)imp).getStartIndex() );
              		    if (packageDescr != null) {
              			packageDescr.addFunctionImport( importDecl );
              		    }
              	        
            }
            pushFollow(FOLLOW_import_name_in_function_import_statement354);
            import_name(importDecl);
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_function_import_statement357);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:261:1: import_name[ImportDescr importDecl] returns [String name] : id= identifier ( '.' id= identifier )* (star= '.*' )? ;
    public final String import_name(ImportDescr importDecl) throws RecognitionException {
        String name = null;

        Token star=null;
        Token id = null;



        		name = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:266:3: (id= identifier ( '.' id= identifier )* (star= '.*' )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:266:3: id= identifier ( '.' id= identifier )* (star= '.*' )?
            {
            pushFollow(FOLLOW_identifier_in_import_name385);
            id=identifier();
            _fsp--;
            if (failed) return name;
            if ( backtracking==0 ) {
               
              		    name=id.getText(); 
              		    importDecl.setTarget( name );
              		    importDecl.setEndCharacter( ((CommonToken)id).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:272:3: ( '.' id= identifier )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==71) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:272:5: '.' id= identifier
            	    {
            	    match(input,71,FOLLOW_71_in_import_name397); if (failed) return name;
            	    pushFollow(FOLLOW_identifier_in_import_name401);
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

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:279:3: (star= '.*' )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==72) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:279:5: star= '.*'
                    {
                    star=(Token)input.LT(1);
                    match(input,72,FOLLOW_72_in_import_name425); if (failed) return name;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:289:1: global : loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon ;
    public final void global() throws RecognitionException {
        Token loc=null;
        String type = null;

        Token id = null;



        	    GlobalDescr global = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:294:3: (loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:294:3: loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,GLOBAL,FOLLOW_GLOBAL_in_global461); if (failed) return ;
            if ( backtracking==0 ) {

              		    global = factory.createGlobal();
              	            global.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		    packageDescr.addGlobal( global );
              		
            }
            pushFollow(FOLLOW_dotted_name_in_global472);
            type=dotted_name(null);
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              		    global.setType( type );
              		
            }
            pushFollow(FOLLOW_identifier_in_global484);
            id=identifier();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_global486);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:312:1: function : loc= FUNCTION (retType= dotted_name[null] )? n= identifier '(' ( (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f] ;
    public final void function() throws RecognitionException {
        Token loc=null;
        String retType = null;

        Token n = null;

        String paramType = null;

        String paramName = null;

        String body = null;



        		FunctionDescr f = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:317:3: (loc= FUNCTION (retType= dotted_name[null] )? n= identifier '(' ( (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:317:3: loc= FUNCTION (retType= dotted_name[null] )? n= identifier '(' ( (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f]
            {
            loc=(Token)input.LT(1);
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function513); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:317:16: (retType= dotted_name[null] )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==ID) ) {
                int LA10_1 = input.LA(2);

                if ( ((LA10_1>=ATTRIBUTES && LA10_1<=WHEN)||LA10_1==ENABLED||LA10_1==SALIENCE||LA10_1==DURATION||LA10_1==ACCUMULATE||(LA10_1>=INIT && LA10_1<=RESULT)||(LA10_1>=COLLECT && LA10_1<=OR)||(LA10_1>=EVAL && LA10_1<=IN)||LA10_1==NULL||LA10_1==LEFT_SQUARE||(LA10_1>=AND && LA10_1<=THEN)||LA10_1==71) ) {
                    alt10=1;
                }
            }
            switch (alt10) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:317:17: retType= dotted_name[null]
                    {
                    pushFollow(FOLLOW_dotted_name_in_function518);
                    retType=dotted_name(null);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_identifier_in_function525);
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
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_function534); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:326:4: ( (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )* )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( ((LA14_0>=ATTRIBUTES && LA14_0<=WHEN)||LA14_0==ENABLED||LA14_0==SALIENCE||LA14_0==DURATION||LA14_0==ACCUMULATE||(LA14_0>=INIT && LA14_0<=RESULT)||(LA14_0>=COLLECT && LA14_0<=OR)||(LA14_0>=EVAL && LA14_0<=IN)||LA14_0==NULL||(LA14_0>=AND && LA14_0<=THEN)) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:326:6: (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )*
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:326:6: (paramType= dotted_name[null] )?
                    int alt11=2;
                    alt11 = dfa11.predict(input);
                    switch (alt11) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:326:7: paramType= dotted_name[null]
                            {
                            pushFollow(FOLLOW_dotted_name_in_function544);
                            paramType=dotted_name(null);
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_argument_in_function551);
                    paramName=argument();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      					f.addParameter( paramType, paramName );
                      				
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:330:5: ( ',' (paramType= dotted_name[null] )? paramName= argument )*
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( (LA13_0==COMMA) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:330:7: ',' (paramType= dotted_name[null] )? paramName= argument
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_function565); if (failed) return ;
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:330:11: (paramType= dotted_name[null] )?
                    	    int alt12=2;
                    	    alt12 = dfa12.predict(input);
                    	    switch (alt12) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:330:12: paramType= dotted_name[null]
                    	            {
                    	            pushFollow(FOLLOW_dotted_name_in_function570);
                    	            paramType=dotted_name(null);
                    	            _fsp--;
                    	            if (failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_argument_in_function577);
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

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_function601); if (failed) return ;
            pushFollow(FOLLOW_curly_chunk_in_function607);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:346:1: query returns [QueryDescr query] : loc= QUERY queryName= name ( normal_lhs_block[lhs] ) loc= END ;
    public final QueryDescr query() throws RecognitionException {
        QueryDescr query = null;

        Token loc=null;
        String queryName = null;



        		query = null;
        		AndDescr lhs = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:352:3: (loc= QUERY queryName= name ( normal_lhs_block[lhs] ) loc= END )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:352:3: loc= QUERY queryName= name ( normal_lhs_block[lhs] ) loc= END
            {
            loc=(Token)input.LT(1);
            match(input,QUERY,FOLLOW_QUERY_in_query639); if (failed) return query;
            pushFollow(FOLLOW_name_in_query643);
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:360:3: ( normal_lhs_block[lhs] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:361:4: normal_lhs_block[lhs]
            {
            pushFollow(FOLLOW_normal_lhs_block_in_query656);
            normal_lhs_block(lhs);
            _fsp--;
            if (failed) return query;

            }

            loc=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_query673); if (failed) return query;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:371:1: template returns [FactTemplateDescr template] : loc= TEMPLATE templateName= identifier opt_semicolon (slot= template_slot )+ loc= END opt_semicolon ;
    public final FactTemplateDescr template() throws RecognitionException {
        FactTemplateDescr template = null;

        Token loc=null;
        Token templateName = null;

        FieldTemplateDescr slot = null;



        		template = null;		
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:376:3: (loc= TEMPLATE templateName= identifier opt_semicolon (slot= template_slot )+ loc= END opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:376:3: loc= TEMPLATE templateName= identifier opt_semicolon (slot= template_slot )+ loc= END opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,TEMPLATE,FOLLOW_TEMPLATE_in_template703); if (failed) return template;
            pushFollow(FOLLOW_identifier_in_template707);
            templateName=identifier();
            _fsp--;
            if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template709);
            opt_semicolon();
            _fsp--;
            if (failed) return template;
            if ( backtracking==0 ) {

              			template = new FactTemplateDescr(templateName.getText());
              			template.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );			
              			template.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:382:3: (slot= template_slot )+
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:383:4: slot= template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template724);
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
            match(input,END,FOLLOW_END_in_template741); if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template743);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:394:1: template_slot returns [FieldTemplateDescr field] : fieldType= dotted_name[field] n= identifier opt_semicolon ;
    public final FieldTemplateDescr template_slot() throws RecognitionException {
        FieldTemplateDescr field = null;

        String fieldType = null;

        Token n = null;



        		field = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:399:11: (fieldType= dotted_name[field] n= identifier opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:399:11: fieldType= dotted_name[field] n= identifier opt_semicolon
            {
            if ( backtracking==0 ) {

              			field = factory.createFieldTemplate();
              	         
            }
            pushFollow(FOLLOW_dotted_name_in_template_slot789);
            fieldType=dotted_name(field);
            _fsp--;
            if (failed) return field;
            if ( backtracking==0 ) {

              		        field.setClassType( fieldType );
              		 
            }
            pushFollow(FOLLOW_identifier_in_template_slot807);
            n=identifier();
            _fsp--;
            if (failed) return field;
            pushFollow(FOLLOW_opt_semicolon_in_template_slot809);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:415:1: rule returns [RuleDescr rule] : loc= RULE ruleName= name rule_attributes[rule] (loc= WHEN ( ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule] ;
    public final RuleDescr rule() throws RecognitionException {
        RuleDescr rule = null;

        Token loc=null;
        String ruleName = null;



        		rule = null;
        		String consequence = "";
        		AndDescr lhs = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:422:3: (loc= RULE ruleName= name rule_attributes[rule] (loc= WHEN ( ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:422:3: loc= RULE ruleName= name rule_attributes[rule] (loc= WHEN ( ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule]
            {
            loc=(Token)input.LT(1);
            match(input,RULE,FOLLOW_RULE_in_rule842); if (failed) return rule;
            pushFollow(FOLLOW_name_in_rule846);
            ruleName=name();
            _fsp--;
            if (failed) return rule;
            if ( backtracking==0 ) {
               
              			location.setType( Location.LOCATION_RULE_HEADER );
              			debug( "start rule: " + ruleName );
              			rule = new RuleDescr( ruleName, null ); 
              			rule.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			rule.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            pushFollow(FOLLOW_rule_attributes_in_rule855);
            rule_attributes(rule);
            _fsp--;
            if (failed) return rule;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:431:3: (loc= WHEN ( ':' )? ( normal_lhs_block[lhs] ) )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==WHEN) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:431:5: loc= WHEN ( ':' )? ( normal_lhs_block[lhs] )
                    {
                    loc=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_rule864); if (failed) return rule;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:431:14: ( ':' )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==70) ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: ':'
                            {
                            match(input,70,FOLLOW_70_in_rule866); if (failed) return rule;

                            }
                            break;

                    }

                    if ( backtracking==0 ) {
                       
                      				this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
                      				lhs = new AndDescr(); rule.setLhs( lhs ); 
                      				lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      				lhs.setStartCharacter( ((CommonToken)loc).getStartIndex() );
                      			
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:438:4: ( normal_lhs_block[lhs] )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:440:5: normal_lhs_block[lhs]
                    {
                    pushFollow(FOLLOW_normal_lhs_block_in_rule884);
                    normal_lhs_block(lhs);
                    _fsp--;
                    if (failed) return rule;

                    }


                    }
                    break;

            }

            pushFollow(FOLLOW_rhs_chunk_in_rule905);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:449:1: rule_attributes[RuleDescr rule] : ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )* ;
    public final void rule_attributes(RuleDescr rule) throws RecognitionException {
        AttributeDescr a = null;


        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:451:4: ( ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:451:4: ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:451:4: ( ATTRIBUTES ':' )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==ATTRIBUTES) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:451:5: ATTRIBUTES ':'
                    {
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_rule_attributes926); if (failed) return ;
                    match(input,70,FOLLOW_70_in_rule_attributes928); if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:4: ( ( ',' )? a= rule_attribute )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==DATE_EFFECTIVE||(LA20_0>=DATE_EXPIRES && LA20_0<=ENABLED)||LA20_0==SALIENCE||(LA20_0>=NO_LOOP && LA20_0<=LOCK_ON_ACTIVE)||LA20_0==COMMA) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:6: ( ',' )? a= rule_attribute
            	    {
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:6: ( ',' )?
            	    int alt19=2;
            	    int LA19_0 = input.LA(1);

            	    if ( (LA19_0==COMMA) ) {
            	        alt19=1;
            	    }
            	    switch (alt19) {
            	        case 1 :
            	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: ','
            	            {
            	            match(input,COMMA,FOLLOW_COMMA_in_rule_attributes937); if (failed) return ;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes942);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:461:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect );
    public final AttributeDescr rule_attribute() throws RecognitionException {
        AttributeDescr d = null;

        AttributeDescr a = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:466:4: (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect )
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
                    new NoViableAltException("461:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect );", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:466:4: a= salience
                    {
                    pushFollow(FOLLOW_salience_in_rule_attribute983);
                    a=salience();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:467:5: a= no_loop
                    {
                    pushFollow(FOLLOW_no_loop_in_rule_attribute993);
                    a=no_loop();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:468:5: a= agenda_group
                    {
                    pushFollow(FOLLOW_agenda_group_in_rule_attribute1004);
                    a=agenda_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:469:5: a= duration
                    {
                    pushFollow(FOLLOW_duration_in_rule_attribute1017);
                    a=duration();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:470:5: a= activation_group
                    {
                    pushFollow(FOLLOW_activation_group_in_rule_attribute1031);
                    a=activation_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:471:5: a= auto_focus
                    {
                    pushFollow(FOLLOW_auto_focus_in_rule_attribute1042);
                    a=auto_focus();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:472:5: a= date_effective
                    {
                    pushFollow(FOLLOW_date_effective_in_rule_attribute1053);
                    a=date_effective();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d = a; 
                    }

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:473:5: a= date_expires
                    {
                    pushFollow(FOLLOW_date_expires_in_rule_attribute1063);
                    a=date_expires();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d = a; 
                    }

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:474:5: a= enabled
                    {
                    pushFollow(FOLLOW_enabled_in_rule_attribute1073);
                    a=enabled();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d=a;
                    }

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:475:5: a= ruleflow_group
                    {
                    pushFollow(FOLLOW_ruleflow_group_in_rule_attribute1083);
                    a=ruleflow_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:476:5: a= lock_on_active
                    {
                    pushFollow(FOLLOW_lock_on_active_in_rule_attribute1093);
                    a=lock_on_active();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:477:5: a= dialect
                    {
                    pushFollow(FOLLOW_dialect_in_rule_attribute1102);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:481:1: date_effective returns [AttributeDescr d] : loc= DATE_EFFECTIVE val= STRING ;
    public final AttributeDescr date_effective() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token val=null;


        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:486:3: (loc= DATE_EFFECTIVE val= STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:486:3: loc= DATE_EFFECTIVE val= STRING
            {
            loc=(Token)input.LT(1);
            match(input,DATE_EFFECTIVE,FOLLOW_DATE_EFFECTIVE_in_date_effective1134); if (failed) return d;
            val=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_effective1138); if (failed) return d;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:496:1: date_expires returns [AttributeDescr d] : loc= DATE_EXPIRES val= STRING ;
    public final AttributeDescr date_expires() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token val=null;


        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:501:3: (loc= DATE_EXPIRES val= STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:501:3: loc= DATE_EXPIRES val= STRING
            {
            loc=(Token)input.LT(1);
            match(input,DATE_EXPIRES,FOLLOW_DATE_EXPIRES_in_date_expires1171); if (failed) return d;
            val=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_expires1175); if (failed) return d;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:512:1: enabled returns [AttributeDescr d] : loc= ENABLED t= BOOL ;
    public final AttributeDescr enabled() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;


        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:517:4: (loc= ENABLED t= BOOL )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:517:4: loc= ENABLED t= BOOL
            {
            loc=(Token)input.LT(1);
            match(input,ENABLED,FOLLOW_ENABLED_in_enabled1210); if (failed) return d;
            t=(Token)input.LT(1);
            match(input,BOOL,FOLLOW_BOOL_in_enabled1214); if (failed) return d;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:530:1: salience returns [AttributeDescr d ] : loc= SALIENCE (i= INT | txt= paren_chunk[d] ) ;
    public final AttributeDescr salience() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;
        String txt = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:535:3: (loc= SALIENCE (i= INT | txt= paren_chunk[d] ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:535:3: loc= SALIENCE (i= INT | txt= paren_chunk[d] )
            {
            loc=(Token)input.LT(1);
            match(input,SALIENCE,FOLLOW_SALIENCE_in_salience1259); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new AttributeDescr( "salience" );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:541:3: (i= INT | txt= paren_chunk[d] )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==INT) ) {
                alt22=1;
            }
            else if ( (LA22_0==LEFT_PAREN) ) {
                alt22=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("541:3: (i= INT | txt= paren_chunk[d] )", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:541:5: i= INT
                    {
                    i=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_salience1272); if (failed) return d;
                    if ( backtracking==0 ) {

                      			d.setValue( i.getText() );
                      			d.setEndCharacter( ((CommonToken)i).getStopIndex() );
                      		
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:546:5: txt= paren_chunk[d]
                    {
                    pushFollow(FOLLOW_paren_chunk_in_salience1287);
                    txt=paren_chunk(d);
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {

                      			d.setValue( txt );
                      		
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
    // $ANTLR end salience


    // $ANTLR start no_loop
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:553:1: no_loop returns [AttributeDescr d] : ( (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );
    public final AttributeDescr no_loop() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;


        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:558:3: ( (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) )
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==NO_LOOP) ) {
                int LA23_1 = input.LA(2);

                if ( (LA23_1==BOOL) ) {
                    alt23=2;
                }
                else if ( (LA23_1==EOF||(LA23_1>=IMPORT && LA23_1<=QUERY)||(LA23_1>=TEMPLATE && LA23_1<=DATE_EFFECTIVE)||(LA23_1>=DATE_EXPIRES && LA23_1<=ENABLED)||LA23_1==SALIENCE||(LA23_1>=NO_LOOP && LA23_1<=LOCK_ON_ACTIVE)||LA23_1==COMMA||LA23_1==THEN) ) {
                    alt23=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("553:1: no_loop returns [AttributeDescr d] : ( (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );", 23, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("553:1: no_loop returns [AttributeDescr d] : ( (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:558:3: (loc= NO_LOOP )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:558:3: (loc= NO_LOOP )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:559:4: loc= NO_LOOP
                    {
                    loc=(Token)input.LT(1);
                    match(input,NO_LOOP,FOLLOW_NO_LOOP_in_no_loop1327); if (failed) return d;
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:568:3: (loc= NO_LOOP t= BOOL )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:568:3: (loc= NO_LOOP t= BOOL )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:569:4: loc= NO_LOOP t= BOOL
                    {
                    loc=(Token)input.LT(1);
                    match(input,NO_LOOP,FOLLOW_NO_LOOP_in_no_loop1355); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1359); if (failed) return d;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:581:1: auto_focus returns [AttributeDescr d] : ( (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );
    public final AttributeDescr auto_focus() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;


        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:586:3: ( (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) )
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==AUTO_FOCUS) ) {
                int LA24_1 = input.LA(2);

                if ( (LA24_1==BOOL) ) {
                    alt24=2;
                }
                else if ( (LA24_1==EOF||(LA24_1>=IMPORT && LA24_1<=QUERY)||(LA24_1>=TEMPLATE && LA24_1<=DATE_EFFECTIVE)||(LA24_1>=DATE_EXPIRES && LA24_1<=ENABLED)||LA24_1==SALIENCE||(LA24_1>=NO_LOOP && LA24_1<=LOCK_ON_ACTIVE)||LA24_1==COMMA||LA24_1==THEN) ) {
                    alt24=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("581:1: auto_focus returns [AttributeDescr d] : ( (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );", 24, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("581:1: auto_focus returns [AttributeDescr d] : ( (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:586:3: (loc= AUTO_FOCUS )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:586:3: (loc= AUTO_FOCUS )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:587:4: loc= AUTO_FOCUS
                    {
                    loc=(Token)input.LT(1);
                    match(input,AUTO_FOCUS,FOLLOW_AUTO_FOCUS_in_auto_focus1408); if (failed) return d;
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:596:3: (loc= AUTO_FOCUS t= BOOL )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:596:3: (loc= AUTO_FOCUS t= BOOL )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:597:4: loc= AUTO_FOCUS t= BOOL
                    {
                    loc=(Token)input.LT(1);
                    match(input,AUTO_FOCUS,FOLLOW_AUTO_FOCUS_in_auto_focus1436); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1440); if (failed) return d;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:609:1: activation_group returns [AttributeDescr d] : loc= ACTIVATION_GROUP n= STRING ;
    public final AttributeDescr activation_group() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token n=null;


        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:614:3: (loc= ACTIVATION_GROUP n= STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:614:3: loc= ACTIVATION_GROUP n= STRING
            {
            loc=(Token)input.LT(1);
            match(input,ACTIVATION_GROUP,FOLLOW_ACTIVATION_GROUP_in_activation_group1485); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1489); if (failed) return d;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:623:1: ruleflow_group returns [AttributeDescr d] : loc= RULEFLOW_GROUP n= STRING ;
    public final AttributeDescr ruleflow_group() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token n=null;


        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:628:3: (loc= RULEFLOW_GROUP n= STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:628:3: loc= RULEFLOW_GROUP n= STRING
            {
            loc=(Token)input.LT(1);
            match(input,RULEFLOW_GROUP,FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1521); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_ruleflow_group1525); if (failed) return d;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:637:1: agenda_group returns [AttributeDescr d] : loc= AGENDA_GROUP n= STRING ;
    public final AttributeDescr agenda_group() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token n=null;


        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:642:3: (loc= AGENDA_GROUP n= STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:642:3: loc= AGENDA_GROUP n= STRING
            {
            loc=(Token)input.LT(1);
            match(input,AGENDA_GROUP,FOLLOW_AGENDA_GROUP_in_agenda_group1557); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1561); if (failed) return d;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:652:1: duration returns [AttributeDescr d] : loc= DURATION i= INT ;
    public final AttributeDescr duration() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;


        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:657:3: (loc= DURATION i= INT )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:657:3: loc= DURATION i= INT
            {
            loc=(Token)input.LT(1);
            match(input,DURATION,FOLLOW_DURATION_in_duration1595); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1599); if (failed) return d;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:666:1: dialect returns [AttributeDescr d] : loc= DIALECT n= STRING ;
    public final AttributeDescr dialect() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token n=null;


        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:671:3: (loc= DIALECT n= STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:671:3: loc= DIALECT n= STRING
            {
            loc=(Token)input.LT(1);
            match(input,DIALECT,FOLLOW_DIALECT_in_dialect1631); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_dialect1635); if (failed) return d;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:685:1: lock_on_active returns [AttributeDescr d] : ( (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) );
    public final AttributeDescr lock_on_active() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;


        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:690:3: ( (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==LOCK_ON_ACTIVE) ) {
                int LA25_1 = input.LA(2);

                if ( (LA25_1==BOOL) ) {
                    alt25=2;
                }
                else if ( (LA25_1==EOF||(LA25_1>=IMPORT && LA25_1<=QUERY)||(LA25_1>=TEMPLATE && LA25_1<=DATE_EFFECTIVE)||(LA25_1>=DATE_EXPIRES && LA25_1<=ENABLED)||LA25_1==SALIENCE||(LA25_1>=NO_LOOP && LA25_1<=LOCK_ON_ACTIVE)||LA25_1==COMMA||LA25_1==THEN) ) {
                    alt25=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("685:1: lock_on_active returns [AttributeDescr d] : ( (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) );", 25, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("685:1: lock_on_active returns [AttributeDescr d] : ( (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) );", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:690:3: (loc= LOCK_ON_ACTIVE )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:690:3: (loc= LOCK_ON_ACTIVE )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:691:4: loc= LOCK_ON_ACTIVE
                    {
                    loc=(Token)input.LT(1);
                    match(input,LOCK_ON_ACTIVE,FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1684); if (failed) return d;
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:700:3: (loc= LOCK_ON_ACTIVE t= BOOL )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:700:3: (loc= LOCK_ON_ACTIVE t= BOOL )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:701:4: loc= LOCK_ON_ACTIVE t= BOOL
                    {
                    loc=(Token)input.LT(1);
                    match(input,LOCK_ON_ACTIVE,FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1712); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_lock_on_active1716); if (failed) return d;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:713:1: normal_lhs_block[AndDescr descr] : (d= lhs[descr] )* ;
    public final void normal_lhs_block(AndDescr descr) throws RecognitionException {
        BaseDescr d = null;


        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:715:3: ( (d= lhs[descr] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:715:3: (d= lhs[descr] )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:715:3: (d= lhs[descr] )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==LEFT_PAREN||LA26_0==ID||LA26_0==EVAL||LA26_0==NOT||(LA26_0>=EXISTS && LA26_0<=FORALL)) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:715:5: d= lhs[descr]
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1754);
            	    d=lhs(descr);
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       if(d != null) descr.addDescr( d ); 
            	    }

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
        return ;
    }
    // $ANTLR end normal_lhs_block


    // $ANTLR start lhs
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:721:1: lhs[ConditionalElementDescr ce] returns [BaseDescr d] : l= lhs_or ;
    public final BaseDescr lhs(ConditionalElementDescr ce) throws RecognitionException {
        BaseDescr d = null;

        BaseDescr l = null;



        		d=null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:725:4: (l= lhs_or )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:725:4: l= lhs_or
            {
            pushFollow(FOLLOW_lhs_or_in_lhs1791);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:729:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact );
    public final BaseDescr lhs_pattern() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr f = null;



        		d=null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:733:4: (f= fact_binding | f= fact )
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==ID) ) {
                int LA27_1 = input.LA(2);

                if ( (LA27_1==70) ) {
                    alt27=1;
                }
                else if ( (LA27_1==LEFT_PAREN||LA27_1==LEFT_SQUARE||LA27_1==71) ) {
                    alt27=2;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("729:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact );", 27, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("729:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact );", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:733:4: f= fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_pattern1819);
                    f=fact_binding();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = f; 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:734:4: f= fact
                    {
                    pushFollow(FOLLOW_fact_in_lhs_pattern1828);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:737:1: from_statement returns [FromDescr d] : ds= from_source[d] ;
    public final FromDescr from_statement() throws RecognitionException {
        FromDescr d = null;

        DeclarativeInvokerDescr ds = null;



        		d=factory.createFrom();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:742:2: (ds= from_source[d] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:742:2: ds= from_source[d]
            {
            pushFollow(FOLLOW_from_source_in_from_statement1855);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:752:1: from_source[FromDescr from] returns [DeclarativeInvokerDescr ds] : ident= identifier (args= paren_chunk[from] )? ( expression_chain[from, ad] )? ;
    public final DeclarativeInvokerDescr from_source(FromDescr from) throws RecognitionException {
        DeclarativeInvokerDescr ds = null;

        Token ident = null;

        String args = null;



        		ds = null;
        		AccessorDescr ad = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:758:3: (ident= identifier (args= paren_chunk[from] )? ( expression_chain[from, ad] )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:758:3: ident= identifier (args= paren_chunk[from] )? ( expression_chain[from, ad] )?
            {
            pushFollow(FOLLOW_identifier_in_from_source1897);
            ident=identifier();
            _fsp--;
            if (failed) return ds;
            if ( backtracking==0 ) {

              			ad = new AccessorDescr(ident.getText());	
              			ad.setLocation( offset(ident.getLine()), ident.getCharPositionInLine() );
              			ad.setStartCharacter( ((CommonToken)ident).getStartIndex() );
              			ad.setEndCharacter( ((CommonToken)ident).getStopIndex() );
              			ds = ad;
              			location.setProperty(Location.LOCATION_FROM_CONTENT, ident.getText());
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:767:3: (args= paren_chunk[from] )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==LEFT_PAREN) ) {
                int LA28_1 = input.LA(2);

                if ( (synpred43()) ) {
                    alt28=1;
                }
            }
            switch (alt28) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:767:4: args= paren_chunk[from]
                    {
                    pushFollow(FOLLOW_paren_chunk_in_from_source1908);
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
                      				location.setProperty(Location.LOCATION_FROM_CONTENT, args);
                      			}
                      		
                    }

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:781:3: ( expression_chain[from, ad] )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==71) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: expression_chain[from, ad]
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source1922);
                    expression_chain(from,  ad);
                    _fsp--;
                    if (failed) return ds;

                    }
                    break;

            }

            if ( backtracking==0 ) {

              			if( ad != null ) {
              				location.setProperty(Location.LOCATION_FROM_CONTENT, ad.toString() );
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
        return ds;
    }
    // $ANTLR end from_source


    // $ANTLR start expression_chain
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:789:1: expression_chain[FromDescr from, AccessorDescr as] : ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )? ) ;
    public final void expression_chain(FromDescr from, AccessorDescr as) throws RecognitionException {
        Token field = null;

        String sqarg = null;

        String paarg = null;



          		FieldAccessDescr fa = null;
        	    	MethodAccessDescr ma = null;	
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:795:2: ( ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )? ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:795:2: ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )? )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:795:2: ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:795:4: '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )?
            {
            match(input,71,FOLLOW_71_in_expression_chain1951); if (failed) return ;
            pushFollow(FOLLOW_identifier_in_expression_chain1955);
            field=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              	        fa = new FieldAccessDescr(field.getText());	
              		fa.setLocation( offset(field.getLine()), field.getCharPositionInLine() );
              		fa.setStartCharacter( ((CommonToken)field).getStartIndex() );
              		fa.setEndCharacter( ((CommonToken)field).getStopIndex() );
              	    
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:802:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )?
            int alt30=3;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==LEFT_SQUARE) && (synpred45())) {
                alt30=1;
            }
            else if ( (LA30_0==LEFT_PAREN) ) {
                int LA30_2 = input.LA(2);

                if ( (synpred46()) ) {
                    alt30=2;
                }
            }
            switch (alt30) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:803:6: ( LEFT_SQUARE )=>sqarg= square_chunk[from]
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain1986);
                    sqarg=square_chunk(from);
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      	          fa.setArgument( sqarg );	
                      	      
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:808:6: ( LEFT_PAREN )=>paarg= paren_chunk[from]
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain2020);
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:822:4: ( expression_chain[from, as] )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==71) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: expression_chain[from, as]
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain2041);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:826:1: accumulate_statement returns [AccumulateDescr d] : loc= ACCUMULATE LEFT_PAREN pattern= lhs_pattern ( COMMA )? INIT text= paren_chunk[null] ( COMMA )? ACTION text= paren_chunk[null] ( COMMA )? RESULT text= paren_chunk[null] loc= RIGHT_PAREN ;
    public final AccumulateDescr accumulate_statement() throws RecognitionException {
        AccumulateDescr d = null;

        Token loc=null;
        BaseDescr pattern = null;

        String text = null;



        		d = factory.createAccumulate();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:831:10: (loc= ACCUMULATE LEFT_PAREN pattern= lhs_pattern ( COMMA )? INIT text= paren_chunk[null] ( COMMA )? ACTION text= paren_chunk[null] ( COMMA )? RESULT text= paren_chunk[null] loc= RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:831:10: loc= ACCUMULATE LEFT_PAREN pattern= lhs_pattern ( COMMA )? INIT text= paren_chunk[null] ( COMMA )? ACTION text= paren_chunk[null] ( COMMA )? RESULT text= paren_chunk[null] loc= RIGHT_PAREN
            {
            loc=(Token)input.LT(1);
            match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_accumulate_statement2082); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement2092); if (failed) return d;
            pushFollow(FOLLOW_lhs_pattern_in_accumulate_statement2096);
            pattern=lhs_pattern();
            _fsp--;
            if (failed) return d;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:837:34: ( COMMA )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==COMMA) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2098); if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {

              		        d.setSourcePattern( (PatternDescr)pattern );
              		
            }
            match(input,INIT,FOLLOW_INIT_in_accumulate_statement2108); if (failed) return d;
            if ( backtracking==0 ) {

              			location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE_INIT );
              		
            }
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2119);
            text=paren_chunk(null);
            _fsp--;
            if (failed) return d;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:845:26: ( COMMA )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==COMMA) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2122); if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {

              			if( text != null ) {
              			        d.setInitCode( text.substring(1, text.length()-1) );
              				location.setProperty(Location.LOCATION_PROPERTY_FROM_ACCUMULATE_INIT_CONTENT, d.getInitCode());
              				location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION );
              			}
              		
            }
            match(input,ACTION,FOLLOW_ACTION_in_accumulate_statement2131); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2135);
            text=paren_chunk(null);
            _fsp--;
            if (failed) return d;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:853:33: ( COMMA )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==COMMA) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2138); if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {

              			if( text != null ) {
              			        d.setActionCode( text.substring(1, text.length()-1) );
              	       			location.setProperty(Location.LOCATION_PROPERTY_FROM_ACCUMULATE_ACTION_CONTENT, d.getActionCode());
              				location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT );
              			}
              		
            }
            match(input,RESULT,FOLLOW_RESULT_in_accumulate_statement2147); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2151);
            text=paren_chunk(null);
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {

              			if( text != null ) {
              			        d.setResultCode( text.substring(1, text.length()-1) );
              				location.setProperty(Location.LOCATION_PROPERTY_FROM_ACCUMULATE_RESULT_CONTENT, d.getResultCode());
              			}
              		
            }
            loc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement2163); if (failed) return d;
            if ( backtracking==0 ) {

              			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:875:1: collect_statement returns [CollectDescr d] : loc= COLLECT LEFT_PAREN pattern= lhs_pattern loc= RIGHT_PAREN ;
    public final CollectDescr collect_statement() throws RecognitionException {
        CollectDescr d = null;

        Token loc=null;
        BaseDescr pattern = null;



        		d = factory.createCollect();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:880:10: (loc= COLLECT LEFT_PAREN pattern= lhs_pattern loc= RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:880:10: loc= COLLECT LEFT_PAREN pattern= lhs_pattern loc= RIGHT_PAREN
            {
            loc=(Token)input.LT(1);
            match(input,COLLECT,FOLLOW_COLLECT_in_collect_statement2206); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_FROM_COLLECT );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement2216); if (failed) return d;
            pushFollow(FOLLOW_lhs_pattern_in_collect_statement2220);
            pattern=lhs_pattern();
            _fsp--;
            if (failed) return d;
            loc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement2224); if (failed) return d;
            if ( backtracking==0 ) {

              		        d.setSourcePattern( (PatternDescr)pattern );
              			d.setEndCharacter( ((CommonToken)loc).getStopIndex() );
              			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
              		
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:894:1: fact_binding returns [BaseDescr d] : id= ID ':' fe= fact_expression[id.getText()] ;
    public final BaseDescr fact_binding() throws RecognitionException {
        BaseDescr d = null;

        Token id=null;
        BaseDescr fe = null;



        		d=null;
        		boolean multi=false;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:900:4: (id= ID ':' fe= fact_expression[id.getText()] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:900:4: id= ID ':' fe= fact_expression[id.getText()]
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding2258); if (failed) return d;
            match(input,70,FOLLOW_70_in_fact_binding2260); if (failed) return d;
            if ( backtracking==0 ) {

               		        // handling incomplete parsing
               		        d = new PatternDescr( );
               		        ((PatternDescr) d).setIdentifier( id.getText() );
               		
            }
            pushFollow(FOLLOW_fact_expression_in_fact_binding2273);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:916:2: fact_expression[String id] returns [BaseDescr pd] : ( LEFT_PAREN fe= fact_expression[id] RIGHT_PAREN | f= fact ( ( OR | DOUBLE_PIPE ) f= fact )* );
    public final BaseDescr fact_expression(String id) throws RecognitionException {
        BaseDescr pd = null;

        BaseDescr fe = null;

        BaseDescr f = null;



         		pd = null;
         		boolean multi = false;
         	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:921:5: ( LEFT_PAREN fe= fact_expression[id] RIGHT_PAREN | f= fact ( ( OR | DOUBLE_PIPE ) f= fact )* )
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==LEFT_PAREN) ) {
                alt36=1;
            }
            else if ( (LA36_0==ID) ) {
                alt36=2;
            }
            else {
                if (backtracking>0) {failed=true; return pd;}
                NoViableAltException nvae =
                    new NoViableAltException("916:2: fact_expression[String id] returns [BaseDescr pd] : ( LEFT_PAREN fe= fact_expression[id] RIGHT_PAREN | f= fact ( ( OR | DOUBLE_PIPE ) f= fact )* );", 36, 0, input);

                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:921:5: LEFT_PAREN fe= fact_expression[id] RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_expression2305); if (failed) return pd;
                    pushFollow(FOLLOW_fact_expression_in_fact_expression2309);
                    fe=fact_expression(id);
                    _fsp--;
                    if (failed) return pd;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_expression2312); if (failed) return pd;
                    if ( backtracking==0 ) {
                       pd=fe; 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:922:6: f= fact ( ( OR | DOUBLE_PIPE ) f= fact )*
                    {
                    pushFollow(FOLLOW_fact_in_fact_expression2323);
                    f=fact();
                    _fsp--;
                    if (failed) return pd;
                    if ( backtracking==0 ) {

                       			((PatternDescr)f).setIdentifier( id );
                       			pd = f;
                       		
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:927:4: ( ( OR | DOUBLE_PIPE ) f= fact )*
                    loop35:
                    do {
                        int alt35=2;
                        int LA35_0 = input.LA(1);

                        if ( ((LA35_0>=OR && LA35_0<=DOUBLE_PIPE)) ) {
                            int LA35_7 = input.LA(2);

                            if ( (synpred53()) ) {
                                alt35=1;
                            }


                        }


                        switch (alt35) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:927:6: ( OR | DOUBLE_PIPE ) f= fact
                    	    {
                    	    if ( (input.LA(1)>=OR && input.LA(1)<=DOUBLE_PIPE) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return pd;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fact_expression2335);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {
                    	      	if ( ! multi ) {
                    	       					BaseDescr first = pd;
                    	       					pd = new OrDescr();
                    	       					((OrDescr)pd).addDescr( first );
                    	       					multi=true;
                    	       				}
                    	       			
                    	    }
                    	    pushFollow(FOLLOW_fact_in_fact_expression2353);
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
                    	    break loop35;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:943:1: fact returns [BaseDescr d] : id= dotted_name[d] loc= LEFT_PAREN ( constraints[pattern] )? endLoc= RIGHT_PAREN ;
    public final BaseDescr fact() throws RecognitionException {
        BaseDescr d = null;

        Token loc=null;
        Token endLoc=null;
        String id = null;



        		d=null;
        		PatternDescr pattern = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:949:11: (id= dotted_name[d] loc= LEFT_PAREN ( constraints[pattern] )? endLoc= RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:949:11: id= dotted_name[d] loc= LEFT_PAREN ( constraints[pattern] )? endLoc= RIGHT_PAREN
            {
            if ( backtracking==0 ) {

               			pattern = new PatternDescr( );
               			d = pattern; 
               	        
            }
            pushFollow(FOLLOW_dotted_name_in_fact2414);
            id=dotted_name(d);
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
               		        pattern.setObjectType( id );
               		        pattern.setEndCharacter( -1 );
               		
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact2428); if (failed) return d;
            if ( backtracking==0 ) {

              		                location.setType( Location.LOCATION_LHS_INSIDE_CONDITION_START );
                          			location.setProperty( Location.LOCATION_PROPERTY_CLASS_NAME, id );
               				
               				pattern.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
               			        pattern.setLeftParentCharacter( ((CommonToken)loc).getStartIndex() );
               			
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:965:4: ( constraints[pattern] )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( ((LA37_0>=ATTRIBUTES && LA37_0<=WHEN)||LA37_0==ENABLED||LA37_0==SALIENCE||LA37_0==DURATION||(LA37_0>=ACCUMULATE && LA37_0<=LEFT_PAREN)||(LA37_0>=INIT && LA37_0<=RESULT)||(LA37_0>=COLLECT && LA37_0<=OR)||(LA37_0>=EVAL && LA37_0<=IN)||LA37_0==NULL||(LA37_0>=AND && LA37_0<=THEN)) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:965:6: constraints[pattern]
                    {
                    pushFollow(FOLLOW_constraints_in_fact2438);
                    constraints(pattern);
                    _fsp--;
                    if (failed) return d;

                    }
                    break;

            }

            endLoc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact2451); if (failed) return d;
            if ( backtracking==0 ) {

              		        if( endLoc.getType() == RIGHT_PAREN ) {
              				this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:978:1: constraints[PatternDescr pattern] : constraint[pattern] ( ',' constraint[pattern] )* ;
    public final void constraints(PatternDescr pattern) throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:979:4: ( constraint[pattern] ( ',' constraint[pattern] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:979:4: constraint[pattern] ( ',' constraint[pattern] )*
            {
            pushFollow(FOLLOW_constraint_in_constraints2471);
            constraint(pattern);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:980:3: ( ',' constraint[pattern] )*
            loop38:
            do {
                int alt38=2;
                int LA38_0 = input.LA(1);

                if ( (LA38_0==COMMA) ) {
                    alt38=1;
                }


                switch (alt38) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:980:5: ',' constraint[pattern]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_constraints2478); if (failed) return ;
            	    if ( backtracking==0 ) {
            	       location.setType( Location.LOCATION_LHS_INSIDE_CONDITION_START ); 
            	    }
            	    pushFollow(FOLLOW_constraint_in_constraints2487);
            	    constraint(pattern);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop38;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:985:1: constraint[PatternDescr pattern] : or_constr[top] ;
    public final void constraint(PatternDescr pattern) throws RecognitionException {

        		ConditionalElementDescr top = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:990:3: ( or_constr[top] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:990:3: or_constr[top]
            {
            if ( backtracking==0 ) {

              			top = pattern.getConstraint();
              		
            }
            pushFollow(FOLLOW_or_constr_in_constraint2520);
            or_constr(top);
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
    // $ANTLR end constraint


    // $ANTLR start or_constr
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:996:1: or_constr[ConditionalElementDescr base] : and_constr[or] (t= DOUBLE_PIPE and_constr[or] )* ;
    public final void or_constr(ConditionalElementDescr base) throws RecognitionException {
        Token t=null;


        		OrDescr or = new OrDescr();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1001:3: ( and_constr[or] (t= DOUBLE_PIPE and_constr[or] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1001:3: and_constr[or] (t= DOUBLE_PIPE and_constr[or] )*
            {
            pushFollow(FOLLOW_and_constr_in_or_constr2543);
            and_constr(or);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1002:3: (t= DOUBLE_PIPE and_constr[or] )*
            loop39:
            do {
                int alt39=2;
                int LA39_0 = input.LA(1);

                if ( (LA39_0==DOUBLE_PIPE) ) {
                    alt39=1;
                }


                switch (alt39) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1002:5: t= DOUBLE_PIPE and_constr[or]
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_constr2553); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_START);
            	      			
            	    }
            	    pushFollow(FOLLOW_and_constr_in_or_constr2563);
            	    and_constr(or);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop39;
                }
            } while (true);

            if ( backtracking==0 ) {

              		        if( or.getDescrs().size() == 1 ) {
              		                base.addOrMerge( (BaseDescr) or.getDescrs().get(0) );
              		        } else if ( or.getDescrs().size() > 1 ) {
              		        	base.addDescr( or );
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
    // $ANTLR end or_constr


    // $ANTLR start and_constr
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1017:1: and_constr[ConditionalElementDescr base] : unary_constr[and] (t= DOUBLE_AMPER unary_constr[and] )* ;
    public final void and_constr(ConditionalElementDescr base) throws RecognitionException {
        Token t=null;


        		AndDescr and = new AndDescr();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1022:3: ( unary_constr[and] (t= DOUBLE_AMPER unary_constr[and] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1022:3: unary_constr[and] (t= DOUBLE_AMPER unary_constr[and] )*
            {
            pushFollow(FOLLOW_unary_constr_in_and_constr2595);
            unary_constr(and);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1023:3: (t= DOUBLE_AMPER unary_constr[and] )*
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);

                if ( (LA40_0==DOUBLE_AMPER) ) {
                    alt40=1;
                }


                switch (alt40) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1023:5: t= DOUBLE_AMPER unary_constr[and]
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_constr2605); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_START);
            	      			
            	    }
            	    pushFollow(FOLLOW_unary_constr_in_and_constr2615);
            	    unary_constr(and);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop40;
                }
            } while (true);

            if ( backtracking==0 ) {

              		        if( and.getDescrs().size() == 1) {
              		                base.addOrMerge( (BaseDescr) and.getDescrs().get(0) );
              		        } else if( and.getDescrs().size() > 1) {
              		        	base.addDescr( and );
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
    // $ANTLR end and_constr


    // $ANTLR start unary_constr
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1038:1: unary_constr[ConditionalElementDescr base] : ( field_constraint[base] | LEFT_PAREN or_constr[base] RIGHT_PAREN | EVAL predicate[base] ) ;
    public final void unary_constr(ConditionalElementDescr base) throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1040:3: ( ( field_constraint[base] | LEFT_PAREN or_constr[base] RIGHT_PAREN | EVAL predicate[base] ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1040:3: ( field_constraint[base] | LEFT_PAREN or_constr[base] RIGHT_PAREN | EVAL predicate[base] )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1040:3: ( field_constraint[base] | LEFT_PAREN or_constr[base] RIGHT_PAREN | EVAL predicate[base] )
            int alt41=3;
            switch ( input.LA(1) ) {
            case ATTRIBUTES:
            case PACKAGE:
            case IMPORT:
            case FUNCTION:
            case GLOBAL:
            case QUERY:
            case END:
            case TEMPLATE:
            case RULE:
            case WHEN:
            case ENABLED:
            case SALIENCE:
            case DURATION:
            case ACCUMULATE:
            case INIT:
            case ACTION:
            case RESULT:
            case COLLECT:
            case ID:
            case OR:
            case CONTAINS:
            case NOT:
            case EXCLUDES:
            case MATCHES:
            case MEMBEROF:
            case IN:
            case NULL:
            case AND:
            case FROM:
            case EXISTS:
            case FORALL:
            case THEN:
                {
                alt41=1;
                }
                break;
            case EVAL:
                {
                int LA41_28 = input.LA(2);

                if ( (synpred58()) ) {
                    alt41=1;
                }
                else if ( (true) ) {
                    alt41=3;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1040:3: ( field_constraint[base] | LEFT_PAREN or_constr[base] RIGHT_PAREN | EVAL predicate[base] )", 41, 28, input);

                    throw nvae;
                }
                }
                break;
            case LEFT_PAREN:
                {
                alt41=2;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1040:3: ( field_constraint[base] | LEFT_PAREN or_constr[base] RIGHT_PAREN | EVAL predicate[base] )", 41, 0, input);

                throw nvae;
            }

            switch (alt41) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1040:5: field_constraint[base]
                    {
                    pushFollow(FOLLOW_field_constraint_in_unary_constr2643);
                    field_constraint(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1041:5: LEFT_PAREN or_constr[base] RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_unary_constr2651); if (failed) return ;
                    pushFollow(FOLLOW_or_constr_in_unary_constr2653);
                    or_constr(base);
                    _fsp--;
                    if (failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_unary_constr2656); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1042:5: EVAL predicate[base]
                    {
                    match(input,EVAL,FOLLOW_EVAL_in_unary_constr2662); if (failed) return ;
                    pushFollow(FOLLOW_predicate_in_unary_constr2664);
                    predicate(base);
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
    // $ANTLR end unary_constr


    // $ANTLR start field_constraint
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1046:1: field_constraint[ConditionalElementDescr base] : (fb= ID ':' )? f= identifier ( or_restr_connective[top] | '->' predicate[base] )? ;
    public final void field_constraint(ConditionalElementDescr base) throws RecognitionException {
        Token fb=null;
        Token f = null;



        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        		RestrictionConnectiveDescr top = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1053:3: ( (fb= ID ':' )? f= identifier ( or_restr_connective[top] | '->' predicate[base] )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1053:3: (fb= ID ':' )? f= identifier ( or_restr_connective[top] | '->' predicate[base] )?
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1053:3: (fb= ID ':' )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==ID) ) {
                int LA42_1 = input.LA(2);

                if ( (LA42_1==70) ) {
                    alt42=1;
                }
            }
            switch (alt42) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1053:5: fb= ID ':'
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_field_constraint2696); if (failed) return ;
                    match(input,70,FOLLOW_70_in_field_constraint2698); if (failed) return ;
                    if ( backtracking==0 ) {
                       
                      			fbd = new FieldBindingDescr();
                      			fbd.setIdentifier( fb.getText() );
                      			fbd.setLocation( offset(fb.getLine()), fb.getCharPositionInLine() );
                      			fbd.setStartCharacter( ((CommonToken)fb).getStartIndex() );
                      			base.addDescr( fbd );

                      		    
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_identifier_in_field_constraint2719);
            f=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              		    if( f != null ) {
              			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
              			location.setProperty(Location.LOCATION_PROPERTY_PROPERTY_NAME, f.getText());
              		    
              			if ( fbd != null ) {
              			    fbd.setFieldName( f.getText() );
               			    fbd.setEndCharacter( ((CommonToken)f).getStopIndex() );
              			} 
              			fc = new FieldConstraintDescr(f.getText());
              			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
              			fc.setStartCharacter( ((CommonToken)f).getStartIndex() );
              			top = fc.getRestriction();
              			
              			// it must be a field constraint, as it is not a binding
              			if( fb == null ) {
              			    base.addDescr( fc );
              			}
              		    }
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1084:3: ( or_restr_connective[top] | '->' predicate[base] )?
            int alt43=3;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==LEFT_PAREN||(LA43_0>=CONTAINS && LA43_0<=IN)||(LA43_0>=74 && LA43_0<=79)) ) {
                alt43=1;
            }
            else if ( (LA43_0==73) ) {
                alt43=2;
            }
            switch (alt43) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1085:4: or_restr_connective[top]
                    {
                    pushFollow(FOLLOW_or_restr_connective_in_field_constraint2733);
                    or_restr_connective(top);
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      				// we must add now as we didn't before
                      				if( fb != null) {
                      				    base.addDescr( fc );
                      				}
                      			
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1093:4: '->' predicate[base]
                    {
                    match(input,73,FOLLOW_73_in_field_constraint2748); if (failed) return ;
                    pushFollow(FOLLOW_predicate_in_field_constraint2750);
                    predicate(base);
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
    // $ANTLR end field_constraint


    // $ANTLR start or_restr_connective
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1098:1: or_restr_connective[ RestrictionConnectiveDescr base ] : and_restr_connective[or] (t= DOUBLE_PIPE and_restr_connective[or] )* ;
    public final void or_restr_connective(RestrictionConnectiveDescr base) throws RecognitionException {
        Token t=null;


        		RestrictionConnectiveDescr or = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR);
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1103:3: ( and_restr_connective[or] (t= DOUBLE_PIPE and_restr_connective[or] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1103:3: and_restr_connective[or] (t= DOUBLE_PIPE and_restr_connective[or] )*
            {
            pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective2779);
            and_restr_connective(or);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1104:3: (t= DOUBLE_PIPE and_restr_connective[or] )*
            loop44:
            do {
                int alt44=2;
                int LA44_0 = input.LA(1);

                if ( (LA44_0==DOUBLE_PIPE) ) {
                    int LA44_2 = input.LA(2);

                    if ( (synpred63()) ) {
                        alt44=1;
                    }


                }


                switch (alt44) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1104:5: t= DOUBLE_PIPE and_restr_connective[or]
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_restr_connective2789); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	      			
            	    }
            	    pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective2801);
            	    and_restr_connective(or);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop44;
                }
            } while (true);

            if ( backtracking==0 ) {

              		        if( or.getRestrictions().size() == 1 ) {
              		                base.addOrMerge( (RestrictionDescr) or.getRestrictions().get( 0 ) );
              		        } else if ( or.getRestrictions().size() > 1 ) {
              		        	base.addRestriction( or );
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
    // $ANTLR end or_restr_connective


    // $ANTLR start and_restr_connective
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1120:1: and_restr_connective[ RestrictionConnectiveDescr base ] : constraint_expression[and] (t= DOUBLE_AMPER constraint_expression[and] )* ;
    public final void and_restr_connective(RestrictionConnectiveDescr base) throws RecognitionException {
        Token t=null;


        		RestrictionConnectiveDescr and = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND);
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1125:3: ( constraint_expression[and] (t= DOUBLE_AMPER constraint_expression[and] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1125:3: constraint_expression[and] (t= DOUBLE_AMPER constraint_expression[and] )*
            {
            pushFollow(FOLLOW_constraint_expression_in_and_restr_connective2835);
            constraint_expression(and);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1126:3: (t= DOUBLE_AMPER constraint_expression[and] )*
            loop45:
            do {
                int alt45=2;
                int LA45_0 = input.LA(1);

                if ( (LA45_0==DOUBLE_AMPER) ) {
                    int LA45_2 = input.LA(2);

                    if ( (synpred64()) ) {
                        alt45=1;
                    }


                }


                switch (alt45) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1126:5: t= DOUBLE_AMPER constraint_expression[and]
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_restr_connective2845); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	      			
            	    }
            	    pushFollow(FOLLOW_constraint_expression_in_and_restr_connective2855);
            	    constraint_expression(and);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop45;
                }
            } while (true);

            if ( backtracking==0 ) {

              		        if( and.getRestrictions().size() == 1) {
              		                base.addOrMerge( (RestrictionDescr) and.getRestrictions().get( 0 ) );
              		        } else if ( and.getRestrictions().size() > 1 ) {
              		        	base.addRestriction( and );
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
    // $ANTLR end and_restr_connective


    // $ANTLR start constraint_expression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1141:1: constraint_expression[RestrictionConnectiveDescr base] : ( compound_operator[base] | simple_operator[base] | LEFT_PAREN or_restr_connective[base] RIGHT_PAREN ) ;
    public final void constraint_expression(RestrictionConnectiveDescr base) throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1143:3: ( ( compound_operator[base] | simple_operator[base] | LEFT_PAREN or_restr_connective[base] RIGHT_PAREN ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1143:3: ( compound_operator[base] | simple_operator[base] | LEFT_PAREN or_restr_connective[base] RIGHT_PAREN )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1143:3: ( compound_operator[base] | simple_operator[base] | LEFT_PAREN or_restr_connective[base] RIGHT_PAREN )
            int alt46=3;
            switch ( input.LA(1) ) {
            case IN:
                {
                alt46=1;
                }
                break;
            case NOT:
                {
                int LA46_2 = input.LA(2);

                if ( (LA46_2==CONTAINS||(LA46_2>=MATCHES && LA46_2<=MEMBEROF)) ) {
                    alt46=2;
                }
                else if ( (LA46_2==IN) ) {
                    alt46=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1143:3: ( compound_operator[base] | simple_operator[base] | LEFT_PAREN or_restr_connective[base] RIGHT_PAREN )", 46, 2, input);

                    throw nvae;
                }
                }
                break;
            case CONTAINS:
            case EXCLUDES:
            case MATCHES:
            case MEMBEROF:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
                {
                alt46=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt46=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1143:3: ( compound_operator[base] | simple_operator[base] | LEFT_PAREN or_restr_connective[base] RIGHT_PAREN )", 46, 0, input);

                throw nvae;
            }

            switch (alt46) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1143:5: compound_operator[base]
                    {
                    pushFollow(FOLLOW_compound_operator_in_constraint_expression2890);
                    compound_operator(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1144:5: simple_operator[base]
                    {
                    pushFollow(FOLLOW_simple_operator_in_constraint_expression2897);
                    simple_operator(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1145:5: LEFT_PAREN or_restr_connective[base] RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_constraint_expression2904); if (failed) return ;
                    if ( backtracking==0 ) {

                      			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
                      		
                    }
                    pushFollow(FOLLOW_or_restr_connective_in_constraint_expression2913);
                    or_restr_connective(base);
                    _fsp--;
                    if (failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_constraint_expression2919); if (failed) return ;

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
    // $ANTLR end constraint_expression


    // $ANTLR start simple_operator
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1154:1: simple_operator[RestrictionConnectiveDescr base] : (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF ) rd= expression_value[op] ;
    public final void simple_operator(RestrictionConnectiveDescr base) throws RecognitionException {
        Token t=null;
        Token n=null;
        RestrictionDescr rd = null;



        		String op = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1159:3: ( (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF ) rd= expression_value[op] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1159:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF ) rd= expression_value[op]
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1159:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF )
            int alt47=13;
            switch ( input.LA(1) ) {
            case 74:
                {
                alt47=1;
                }
                break;
            case 75:
                {
                alt47=2;
                }
                break;
            case 76:
                {
                alt47=3;
                }
                break;
            case 77:
                {
                alt47=4;
                }
                break;
            case 78:
                {
                alt47=5;
                }
                break;
            case 79:
                {
                alt47=6;
                }
                break;
            case CONTAINS:
                {
                alt47=7;
                }
                break;
            case NOT:
                {
                switch ( input.LA(2) ) {
                case MATCHES:
                    {
                    alt47=11;
                    }
                    break;
                case MEMBEROF:
                    {
                    alt47=13;
                    }
                    break;
                case CONTAINS:
                    {
                    alt47=8;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1159:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF )", 47, 8, input);

                    throw nvae;
                }

                }
                break;
            case EXCLUDES:
                {
                alt47=9;
                }
                break;
            case MATCHES:
                {
                alt47=10;
                }
                break;
            case MEMBEROF:
                {
                alt47=12;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1159:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF )", 47, 0, input);

                throw nvae;
            }

            switch (alt47) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1159:5: t= '=='
                    {
                    t=(Token)input.LT(1);
                    match(input,74,FOLLOW_74_in_simple_operator2950); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1160:5: t= '>'
                    {
                    t=(Token)input.LT(1);
                    match(input,75,FOLLOW_75_in_simple_operator2958); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1161:5: t= '>='
                    {
                    t=(Token)input.LT(1);
                    match(input,76,FOLLOW_76_in_simple_operator2966); if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1162:5: t= '<'
                    {
                    t=(Token)input.LT(1);
                    match(input,77,FOLLOW_77_in_simple_operator2974); if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1163:5: t= '<='
                    {
                    t=(Token)input.LT(1);
                    match(input,78,FOLLOW_78_in_simple_operator2982); if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1164:5: t= '!='
                    {
                    t=(Token)input.LT(1);
                    match(input,79,FOLLOW_79_in_simple_operator2990); if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1165:5: t= CONTAINS
                    {
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_simple_operator2998); if (failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1166:5: n= NOT t= CONTAINS
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator3006); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_simple_operator3010); if (failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1167:5: t= EXCLUDES
                    {
                    t=(Token)input.LT(1);
                    match(input,EXCLUDES,FOLLOW_EXCLUDES_in_simple_operator3018); if (failed) return ;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1168:5: t= MATCHES
                    {
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_simple_operator3026); if (failed) return ;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1169:5: n= NOT t= MATCHES
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator3034); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_simple_operator3038); if (failed) return ;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1170:5: t= MEMBEROF
                    {
                    t=(Token)input.LT(1);
                    match(input,MEMBEROF,FOLLOW_MEMBEROF_in_simple_operator3046); if (failed) return ;

                    }
                    break;
                case 13 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1171:5: n= NOT t= MEMBEROF
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator3054); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,MEMBEROF,FOLLOW_MEMBEROF_in_simple_operator3058); if (failed) return ;

                    }
                    break;

            }

            if ( backtracking==0 ) {

                		    location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);
                                  location.setProperty(Location.LOCATION_PROPERTY_OPERATOR, t.getText());
              		    if( n != null ) {
              		        op = "not "+t.getText();
              		    } else {
              		        op = t.getText();
              		    }
              		
            }
            pushFollow(FOLLOW_expression_value_in_simple_operator3072);
            rd=expression_value(op);
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              			    if( rd != null ) {
              				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_END);
              			        base.addRestriction( rd );
              			    } else if ( rd == null && op != null ) {
              			        base.addRestriction( new LiteralRestrictionDescr(op, null) );
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
    // $ANTLR end simple_operator


    // $ANTLR start compound_operator
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1193:1: compound_operator[RestrictionConnectiveDescr base] : ( IN | NOT IN ) LEFT_PAREN rd= expression_value[op] ( COMMA rd= expression_value[op] )* RIGHT_PAREN ;
    public final void compound_operator(RestrictionConnectiveDescr base) throws RecognitionException {
        RestrictionDescr rd = null;



        		String op = null;
        		RestrictionConnectiveDescr group = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1199:3: ( ( IN | NOT IN ) LEFT_PAREN rd= expression_value[op] ( COMMA rd= expression_value[op] )* RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1199:3: ( IN | NOT IN ) LEFT_PAREN rd= expression_value[op] ( COMMA rd= expression_value[op] )* RIGHT_PAREN
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1199:3: ( IN | NOT IN )
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==IN) ) {
                alt48=1;
            }
            else if ( (LA48_0==NOT) ) {
                alt48=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1199:3: ( IN | NOT IN )", 48, 0, input);

                throw nvae;
            }
            switch (alt48) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1199:5: IN
                    {
                    match(input,IN,FOLLOW_IN_in_compound_operator3101); if (failed) return ;
                    if ( backtracking==0 ) {

                      			  op = "==";
                      			  group = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR);
                      			  base.addRestriction( group );
                        		    	  location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);
                                          	  location.setProperty(Location.LOCATION_PROPERTY_OPERATOR, "in");
                      			
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1207:5: NOT IN
                    {
                    match(input,NOT,FOLLOW_NOT_in_compound_operator3113); if (failed) return ;
                    match(input,IN,FOLLOW_IN_in_compound_operator3115); if (failed) return ;
                    if ( backtracking==0 ) {

                      			  op = "!=";
                      			  group = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND);
                      			  base.addRestriction( group );
                        		    	  location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);
                                          	  location.setProperty(Location.LOCATION_PROPERTY_OPERATOR, "in");
                      			
                    }

                    }
                    break;

            }

            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_compound_operator3130); if (failed) return ;
            pushFollow(FOLLOW_expression_value_in_compound_operator3134);
            rd=expression_value(op);
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              			    if( rd != null ) {
              			        group.addRestriction( rd );
              			    }
              			
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1222:3: ( COMMA rd= expression_value[op] )*
            loop49:
            do {
                int alt49=2;
                int LA49_0 = input.LA(1);

                if ( (LA49_0==COMMA) ) {
                    alt49=1;
                }


                switch (alt49) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1222:5: COMMA rd= expression_value[op]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_compound_operator3146); if (failed) return ;
            	    pushFollow(FOLLOW_expression_value_in_compound_operator3150);
            	    rd=expression_value(op);
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {

            	      			    if( rd != null ) {
            	      		        	group.addRestriction( rd );
            	      			    }
            	      			
            	    }

            	    }
            	    break;

            	default :
            	    break loop49;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_compound_operator3166); if (failed) return ;
            if ( backtracking==0 ) {

              			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_END);
              		
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
    // $ANTLR end compound_operator


    // $ANTLR start expression_value
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1235:1: expression_value[String op] returns [RestrictionDescr rd] : (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) ;
    public final RestrictionDescr expression_value(String op) throws RecognitionException {
        RestrictionDescr rd = null;

        Token bvc=null;
        String lc = null;

        String rvc = null;


        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1237:3: ( (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1237:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1237:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
            int alt50=4;
            switch ( input.LA(1) ) {
            case ID:
                {
                int LA50_1 = input.LA(2);

                if ( (LA50_1==EOF||LA50_1==COMMA||LA50_1==RIGHT_PAREN||(LA50_1>=DOUBLE_PIPE && LA50_1<=DOUBLE_AMPER)) ) {
                    alt50=1;
                }
                else if ( (LA50_1==71) ) {
                    alt50=2;
                }
                else {
                    if (backtracking>0) {failed=true; return rd;}
                    NoViableAltException nvae =
                        new NoViableAltException("1237:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 50, 1, input);

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
                alt50=3;
                }
                break;
            case LEFT_PAREN:
                {
                alt50=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return rd;}
                NoViableAltException nvae =
                    new NoViableAltException("1237:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 50, 0, input);

                throw nvae;
            }

            switch (alt50) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1237:5: bvc= ID
                    {
                    bvc=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_expression_value3194); if (failed) return rd;
                    if ( backtracking==0 ) {

                      				rd = new VariableRestrictionDescr(op, bvc.getText());
                      			
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1241:5: lc= enum_constraint
                    {
                    pushFollow(FOLLOW_enum_constraint_in_expression_value3207);
                    lc=enum_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd  = new QualifiedIdentifierRestrictionDescr(op, lc);
                      			
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1245:5: lc= literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_expression_value3227);
                    lc=literal_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd  = new LiteralRestrictionDescr(op, lc);
                      			
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1249:5: rvc= retval_constraint
                    {
                    pushFollow(FOLLOW_retval_constraint_in_expression_value3241);
                    rvc=retval_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd = new ReturnValueRestrictionDescr(op, rvc);							
                      			
                    }

                    }
                    break;

            }

            if ( backtracking==0 ) {

              			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_END);
              		
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
    // $ANTLR end expression_value


    // $ANTLR start literal_constraint
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1259:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public final String literal_constraint() throws RecognitionException {
        String text = null;

        Token t=null;


        		text = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1263:4: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1263:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1263:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            int alt51=5;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt51=1;
                }
                break;
            case INT:
                {
                alt51=2;
                }
                break;
            case FLOAT:
                {
                alt51=3;
                }
                break;
            case BOOL:
                {
                alt51=4;
                }
                break;
            case NULL:
                {
                alt51=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return text;}
                NoViableAltException nvae =
                    new NoViableAltException("1263:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )", 51, 0, input);

                throw nvae;
            }

            switch (alt51) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1263:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint3284); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = getString( t ); 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1264:5: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint3295); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1265:5: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint3308); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1266:5: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint3319); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1267:5: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal_constraint3331); if (failed) return text;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1271:1: enum_constraint returns [String text] : id= ID ( '.' ident= identifier )+ ;
    public final String enum_constraint() throws RecognitionException {
        String text = null;

        Token id=null;
        Token ident = null;



        		text = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1276:3: (id= ID ( '.' ident= identifier )+ )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1276:3: id= ID ( '.' ident= identifier )+
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint3366); if (failed) return text;
            if ( backtracking==0 ) {
               text=id.getText(); 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1276:32: ( '.' ident= identifier )+
            int cnt52=0;
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);

                if ( (LA52_0==71) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1276:34: '.' ident= identifier
            	    {
            	    match(input,71,FOLLOW_71_in_enum_constraint3372); if (failed) return text;
            	    pushFollow(FOLLOW_identifier_in_enum_constraint3376);
            	    ident=identifier();
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {
            	       text += "." + ident.getText(); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt52 >= 1 ) break loop52;
            	    if (backtracking>0) {failed=true; return text;}
                        EarlyExitException eee =
                            new EarlyExitException(52, input);
                        throw eee;
                }
                cnt52++;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1280:1: predicate[ConditionalElementDescr base] : text= paren_chunk[d] ;
    public final void predicate(ConditionalElementDescr base) throws RecognitionException {
        String text = null;



        		PredicateDescr d = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1285:3: (text= paren_chunk[d] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1285:3: text= paren_chunk[d]
            {
            if ( backtracking==0 ) {

              			d = new PredicateDescr( );
              		
            }
            pushFollow(FOLLOW_paren_chunk_in_predicate3418);
            text=paren_chunk(d);
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              		        if( text != null ) {
              			        String body = text.substring(1, text.length()-1);
              			        d.setContent( body );
              				base.addDescr( d );
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1298:1: paren_chunk[BaseDescr descr] returns [String text] : loc= LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )* loc= RIGHT_PAREN ;
    public final String paren_chunk(BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc=null;
        String chunk = null;



                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1304:10: (loc= LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )* loc= RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1304:10: loc= LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )* loc= RIGHT_PAREN
            {
            if ( backtracking==0 ) {

              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk3467); if (failed) return text;
            if ( backtracking==0 ) {

              		    buf.append( loc.getText());
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1313:3: (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )*
            loop53:
            do {
                int alt53=3;
                int LA53_0 = input.LA(1);

                if ( ((LA53_0>=ATTRIBUTES && LA53_0<=ACCUMULATE)||(LA53_0>=COMMA && LA53_0<=RESULT)||(LA53_0>=COLLECT && LA53_0<=79)) ) {
                    alt53=1;
                }
                else if ( (LA53_0==LEFT_PAREN) ) {
                    alt53=2;
                }


                switch (alt53) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1314:4: ~ ( LEFT_PAREN | RIGHT_PAREN )
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=ACCUMULATE)||(input.LA(1)>=COMMA && input.LA(1)<=RESULT)||(input.LA(1)>=COLLECT && input.LA(1)<=79) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_paren_chunk3483);    throw mse;
            	    }

            	    if ( backtracking==0 ) {

            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1319:4: chunk= paren_chunk[null]
            	    {
            	    pushFollow(FOLLOW_paren_chunk_in_paren_chunk3507);
            	    chunk=paren_chunk(null);
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {

            	      			    buf.append( chunk );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop53;
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
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk3544); if (failed) return text;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1341:1: curly_chunk[BaseDescr descr] returns [String text] : loc= LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )* loc= RIGHT_CURLY ;
    public final String curly_chunk(BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc=null;
        String chunk = null;



                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1347:3: (loc= LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )* loc= RIGHT_CURLY )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1347:3: loc= LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )* loc= RIGHT_CURLY
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk3595); if (failed) return text;
            if ( backtracking==0 ) {

              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              		    
              		    buf.append( loc.getText() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1355:3: (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )*
            loop54:
            do {
                int alt54=3;
                int LA54_0 = input.LA(1);

                if ( ((LA54_0>=ATTRIBUTES && LA54_0<=NULL)||(LA54_0>=LEFT_SQUARE && LA54_0<=79)) ) {
                    alt54=1;
                }
                else if ( (LA54_0==LEFT_CURLY) ) {
                    alt54=2;
                }


                switch (alt54) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1356:4: ~ ( LEFT_CURLY | RIGHT_CURLY )
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=NULL)||(input.LA(1)>=LEFT_SQUARE && input.LA(1)<=79) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_curly_chunk3611);    throw mse;
            	    }

            	    if ( backtracking==0 ) {

            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1361:4: chunk= curly_chunk[descr]
            	    {
            	    pushFollow(FOLLOW_curly_chunk_in_curly_chunk3635);
            	    chunk=curly_chunk(descr);
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {

            	      			    buf.append( chunk );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop54;
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
            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk3672); if (failed) return text;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1383:1: square_chunk[BaseDescr descr] returns [String text] : loc= LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )* loc= RIGHT_SQUARE ;
    public final String square_chunk(BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc=null;
        String chunk = null;



                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1389:10: (loc= LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )* loc= RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1389:10: loc= LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )* loc= RIGHT_SQUARE
            {
            if ( backtracking==0 ) {

              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk3735); if (failed) return text;
            if ( backtracking==0 ) {

              		    buf.append( loc.getText());
               
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1399:3: (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )*
            loop55:
            do {
                int alt55=3;
                int LA55_0 = input.LA(1);

                if ( ((LA55_0>=ATTRIBUTES && LA55_0<=RIGHT_CURLY)||(LA55_0>=AND && LA55_0<=79)) ) {
                    alt55=1;
                }
                else if ( (LA55_0==LEFT_SQUARE) ) {
                    alt55=2;
                }


                switch (alt55) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1400:4: ~ ( LEFT_SQUARE | RIGHT_SQUARE )
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=RIGHT_CURLY)||(input.LA(1)>=AND && input.LA(1)<=79) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_square_chunk3751);    throw mse;
            	    }

            	    if ( backtracking==0 ) {

            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1405:4: chunk= square_chunk[null]
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_square_chunk3775);
            	    chunk=square_chunk(null);
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {

            	      			    buf.append( chunk );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop55;
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
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk3812); if (failed) return text;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1427:1: retval_constraint returns [String text] : c= paren_chunk[null] ;
    public final String retval_constraint() throws RecognitionException {
        String text = null;

        String c = null;



        		text = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1432:3: (c= paren_chunk[null] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1432:3: c= paren_chunk[null]
            {
            pushFollow(FOLLOW_paren_chunk_in_retval_constraint3857);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1435:1: lhs_or returns [BaseDescr d] : left= lhs_and ( ( OR | '||' ) right= lhs_and )* ;
    public final BaseDescr lhs_or() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d = null;
        		OrDescr or = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1441:3: (left= lhs_and ( ( OR | '||' ) right= lhs_and )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1441:3: left= lhs_and ( ( OR | '||' ) right= lhs_and )*
            {
            pushFollow(FOLLOW_lhs_and_in_lhs_or3885);
            left=lhs_and();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
              d = left; 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1442:3: ( ( OR | '||' ) right= lhs_and )*
            loop56:
            do {
                int alt56=2;
                int LA56_0 = input.LA(1);

                if ( ((LA56_0>=OR && LA56_0<=DOUBLE_PIPE)) ) {
                    alt56=1;
                }


                switch (alt56) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1442:5: ( OR | '||' ) right= lhs_and
            	    {
            	    if ( (input.LA(1)>=OR && input.LA(1)<=DOUBLE_PIPE) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return d;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or3893);    throw mse;
            	    }

            	    if ( backtracking==0 ) {

            	      				location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
            	      			
            	    }
            	    pushFollow(FOLLOW_lhs_and_in_lhs_or3909);
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
        return d;
    }
    // $ANTLR end lhs_or


    // $ANTLR start lhs_and
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1459:1: lhs_and returns [BaseDescr d] : left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* ;
    public final BaseDescr lhs_and() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d = null;
        		AndDescr and = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1465:3: (left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1465:3: left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )*
            {
            pushFollow(FOLLOW_lhs_unary_in_lhs_and3945);
            left=lhs_unary();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               d = left; 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1466:3: ( ( AND | DOUBLE_AMPER ) right= lhs_unary )*
            loop57:
            do {
                int alt57=2;
                int LA57_0 = input.LA(1);

                if ( (LA57_0==DOUBLE_AMPER||LA57_0==AND) ) {
                    alt57=1;
                }


                switch (alt57) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1466:5: ( AND | DOUBLE_AMPER ) right= lhs_unary
            	    {
            	    if ( input.LA(1)==DOUBLE_AMPER||input.LA(1)==AND ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return d;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and3953);    throw mse;
            	    }

            	    if ( backtracking==0 ) {

            	      				location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
            	      			
            	    }
            	    pushFollow(FOLLOW_lhs_unary_in_lhs_and3969);
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
        return d;
    }
    // $ANTLR end lhs_and


    // $ANTLR start lhs_unary
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1483:1: lhs_unary returns [BaseDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon ;
    public final BaseDescr lhs_unary() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr u = null;

        AccumulateDescr ac = null;

        CollectDescr cs = null;

        FromDescr fm = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1487:4: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1487:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1487:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' )
            int alt60=6;
            switch ( input.LA(1) ) {
            case EXISTS:
                {
                alt60=1;
                }
                break;
            case NOT:
                {
                alt60=2;
                }
                break;
            case EVAL:
                {
                alt60=3;
                }
                break;
            case ID:
                {
                alt60=4;
                }
                break;
            case FORALL:
                {
                alt60=5;
                }
                break;
            case LEFT_PAREN:
                {
                alt60=6;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1487:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' )", 60, 0, input);

                throw nvae;
            }

            switch (alt60) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1487:6: u= lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary4006);
                    u=lhs_exist();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1488:5: u= lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary4014);
                    u=lhs_not();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1489:5: u= lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary4022);
                    u=lhs_eval();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1490:5: u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )?
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_unary4030);
                    u=lhs_pattern();
                    _fsp--;
                    if (failed) return d;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1490:19: ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )?
                    int alt59=2;
                    int LA59_0 = input.LA(1);

                    if ( (LA59_0==FROM) ) {
                        alt59=1;
                    }
                    switch (alt59) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1491:13: FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )
                            {
                            match(input,FROM,FOLLOW_FROM_in_lhs_unary4046); if (failed) return d;
                            if ( backtracking==0 ) {

                              				location.setType(Location.LOCATION_LHS_FROM);
                              				location.setProperty(Location.LOCATION_FROM_CONTENT, "");
                              		          
                            }
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1496:13: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )
                            int alt58=3;
                            int LA58_0 = input.LA(1);

                            if ( (LA58_0==ACCUMULATE) ) {
                                int LA58_1 = input.LA(2);

                                if ( (synpred105()) ) {
                                    alt58=1;
                                }
                                else if ( (synpred108()) ) {
                                    alt58=3;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return d;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("1496:13: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )", 58, 1, input);

                                    throw nvae;
                                }
                            }
                            else if ( (LA58_0==COLLECT) ) {
                                int LA58_2 = input.LA(2);

                                if ( (synpred106()) ) {
                                    alt58=2;
                                }
                                else if ( (synpred108()) ) {
                                    alt58=3;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return d;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("1496:13: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )", 58, 2, input);

                                    throw nvae;
                                }
                            }
                            else if ( (LA58_0==ID) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==PACKAGE) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==FUNCTION) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==GLOBAL) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==IMPORT) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==RULE) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==QUERY) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==TEMPLATE) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==ATTRIBUTES) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==ENABLED) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==SALIENCE) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==DURATION) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==FROM) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==INIT) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==ACTION) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==RESULT) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==OR) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==AND) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==CONTAINS) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==EXCLUDES) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==MEMBEROF) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==MATCHES) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==NULL) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==EXISTS) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==NOT) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==EVAL) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==FORALL) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==WHEN) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==THEN) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==END) && (synpred108())) {
                                alt58=3;
                            }
                            else if ( (LA58_0==IN) && (synpred108())) {
                                alt58=3;
                            }
                            else {
                                if (backtracking>0) {failed=true; return d;}
                                NoViableAltException nvae =
                                    new NoViableAltException("1496:13: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )", 58, 0, input);

                                throw nvae;
                            }
                            switch (alt58) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1497:14: ( ACCUMULATE )=> (ac= accumulate_statement )
                                    {
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1497:32: (ac= accumulate_statement )
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1497:33: ac= accumulate_statement
                                    {
                                    pushFollow(FOLLOW_accumulate_statement_in_lhs_unary4101);
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
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1498:14: ( COLLECT )=> (cs= collect_statement )
                                    {
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1498:29: (cs= collect_statement )
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1498:30: cs= collect_statement
                                    {
                                    pushFollow(FOLLOW_collect_statement_in_lhs_unary4130);
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
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1499:14: (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement )
                                    {
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1499:43: (fm= from_statement )
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1499:44: fm= from_statement
                                    {
                                    pushFollow(FOLLOW_from_statement_in_lhs_unary4165);
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1502:5: u= lhs_forall
                    {
                    pushFollow(FOLLOW_lhs_forall_in_lhs_unary4204);
                    u=lhs_forall();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1503:5: '(' u= lhs_or ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary4212); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_unary4216);
                    u=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary4218); if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               d = u; 
            }
            pushFollow(FOLLOW_opt_semicolon_in_lhs_unary4228);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1508:1: lhs_exist returns [BaseDescr d] : loc= EXISTS ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern ) ;
    public final BaseDescr lhs_exist() throws RecognitionException {
        BaseDescr d = null;

        Token loc=null;
        Token end=null;
        BaseDescr pattern = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1512:4: (loc= EXISTS ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1512:4: loc= EXISTS ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )
            {
            loc=(Token)input.LT(1);
            match(input,EXISTS,FOLLOW_EXISTS_in_lhs_exist4252); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new ExistsDescr( ); 
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1519:10: ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==LEFT_PAREN) ) {
                alt61=1;
            }
            else if ( (LA61_0==ID) ) {
                alt61=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1519:10: ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )", 61, 0, input);

                throw nvae;
            }
            switch (alt61) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1519:12: ( '(' pattern= lhs_or end= ')' )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1519:12: ( '(' pattern= lhs_or end= ')' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1519:14: '(' pattern= lhs_or end= ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist4272); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist4276);
                    pattern=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( pattern != null ) ((ExistsDescr)d).addDescr( pattern ); 
                    }
                    end=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist4308); if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( end != null ) d.setEndCharacter( ((CommonToken)end).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1524:12: pattern= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_exist4358);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1534:1: lhs_not returns [NotDescr d] : loc= NOT ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern ) ;
    public final NotDescr lhs_not() throws RecognitionException {
        NotDescr d = null;

        Token loc=null;
        Token end=null;
        BaseDescr pattern = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1538:4: (loc= NOT ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1538:4: loc= NOT ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )
            {
            loc=(Token)input.LT(1);
            match(input,NOT,FOLLOW_NOT_in_lhs_not4412); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new NotDescr( ); 
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_NOT );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1545:3: ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==LEFT_PAREN) ) {
                alt62=1;
            }
            else if ( (LA62_0==ID) ) {
                alt62=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1545:3: ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )", 62, 0, input);

                throw nvae;
            }
            switch (alt62) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1545:5: ( '(' pattern= lhs_or end= ')' )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1545:5: ( '(' pattern= lhs_or end= ')' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1545:7: '(' pattern= lhs_or end= ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not4425); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_not4429);
                    pattern=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( pattern != null ) d.addDescr( pattern ); 
                    }
                    end=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not4462); if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( end != null ) d.setEndCharacter( ((CommonToken)end).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1551:3: pattern= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_not4499);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1561:1: lhs_eval returns [BaseDescr d] : loc= EVAL c= paren_chunk[d] ;
    public final BaseDescr lhs_eval() throws RecognitionException {
        BaseDescr d = null;

        Token loc=null;
        String c = null;



        		d = new EvalDescr( );
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1566:3: (loc= EVAL c= paren_chunk[d] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1566:3: loc= EVAL c= paren_chunk[d]
            {
            loc=(Token)input.LT(1);
            match(input,EVAL,FOLLOW_EVAL_in_lhs_eval4547); if (failed) return d;
            if ( backtracking==0 ) {

              			location.setType( Location.LOCATION_LHS_INSIDE_EVAL );
              		
            }
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval4558);
            c=paren_chunk(d);
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
              			if ( loc != null ) d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		        if( c != null ) {
              	  		    this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
              		            String body = c.length() > 1 ? c.substring(1, c.length()-1) : "";
              			    checkTrailingSemicolon( body, offset(loc.getLine()) );
              			    ((EvalDescr) d).setContent( body );
              			    location.setProperty(Location.LOCATION_EVAL_CONTENT, body);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1583:1: lhs_forall returns [ForallDescr d] : loc= FORALL '(' base= lhs_pattern ( ( ',' )? pattern= lhs_pattern )+ end= ')' ;
    public final ForallDescr lhs_forall() throws RecognitionException {
        ForallDescr d = null;

        Token loc=null;
        Token end=null;
        BaseDescr base = null;

        BaseDescr pattern = null;



        		d = factory.createForall();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1587:4: (loc= FORALL '(' base= lhs_pattern ( ( ',' )? pattern= lhs_pattern )+ end= ')' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1587:4: loc= FORALL '(' base= lhs_pattern ( ( ',' )? pattern= lhs_pattern )+ end= ')'
            {
            loc=(Token)input.LT(1);
            match(input,FORALL,FOLLOW_FORALL_in_lhs_forall4587); if (failed) return d;
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_forall4589); if (failed) return d;
            pushFollow(FOLLOW_lhs_pattern_in_lhs_forall4593);
            base=lhs_pattern();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {

              			if ( loc != null ) d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		        // adding the base pattern
              		        d.addDescr( base );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1594:3: ( ( ',' )? pattern= lhs_pattern )+
            int cnt64=0;
            loop64:
            do {
                int alt64=2;
                int LA64_0 = input.LA(1);

                if ( (LA64_0==COMMA||LA64_0==ID) ) {
                    alt64=1;
                }


                switch (alt64) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1594:5: ( ',' )? pattern= lhs_pattern
            	    {
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1594:5: ( ',' )?
            	    int alt63=2;
            	    int LA63_0 = input.LA(1);

            	    if ( (LA63_0==COMMA) ) {
            	        alt63=1;
            	    }
            	    switch (alt63) {
            	        case 1 :
            	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1594:6: ','
            	            {
            	            match(input,COMMA,FOLLOW_COMMA_in_lhs_forall4607); if (failed) return d;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_lhs_pattern_in_lhs_forall4613);
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
            	    if ( cnt64 >= 1 ) break loop64;
            	    if (backtracking>0) {failed=true; return d;}
                        EarlyExitException eee =
                            new EarlyExitException(64, input);
                        throw eee;
                }
                cnt64++;
            } while (true);

            end=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_forall4628); if (failed) return d;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1606:1: dotted_name[BaseDescr descr] returns [String name] : id= ID ( '.' ident= identifier )* ( '[' loc= ']' )* ;
    public final String dotted_name(BaseDescr descr) throws RecognitionException {
        String name = null;

        Token id=null;
        Token loc=null;
        Token ident = null;



        		name = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1611:3: (id= ID ( '.' ident= identifier )* ( '[' loc= ']' )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1611:3: id= ID ( '.' ident= identifier )* ( '[' loc= ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name4659); if (failed) return name;
            if ( backtracking==0 ) {
               
              		    name=id.getText(); 
              		    if( descr != null ) {
              			descr.setStartCharacter( ((CommonToken)id).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)id).getStopIndex() );
              		    }
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1619:3: ( '.' ident= identifier )*
            loop65:
            do {
                int alt65=2;
                int LA65_0 = input.LA(1);

                if ( (LA65_0==71) ) {
                    alt65=1;
                }


                switch (alt65) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1619:5: '.' ident= identifier
            	    {
            	    match(input,71,FOLLOW_71_in_dotted_name4671); if (failed) return name;
            	    pushFollow(FOLLOW_identifier_in_dotted_name4675);
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
            	    break loop65;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1627:3: ( '[' loc= ']' )*
            loop66:
            do {
                int alt66=2;
                int LA66_0 = input.LA(1);

                if ( (LA66_0==LEFT_SQUARE) ) {
                    alt66=1;
                }


                switch (alt66) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1627:5: '[' loc= ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dotted_name4697); if (failed) return name;
            	    loc=(Token)input.LT(1);
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dotted_name4701); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       
            	      		        name = name + "[]";
            	          		        if( descr != null ) {
            	      			    descr.setEndCharacter( ((CommonToken)loc).getStopIndex() );
            	      		        }
            	      		    
            	    }

            	    }
            	    break;

            	default :
            	    break loop66;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1637:1: argument returns [String name] : id= identifier ( '[' ']' )* ;
    public final String argument() throws RecognitionException {
        String name = null;

        Token id = null;



        		name = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1642:3: (id= identifier ( '[' ']' )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1642:3: id= identifier ( '[' ']' )*
            {
            pushFollow(FOLLOW_identifier_in_argument4740);
            id=identifier();
            _fsp--;
            if (failed) return name;
            if ( backtracking==0 ) {
               name=id.getText(); 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1642:40: ( '[' ']' )*
            loop67:
            do {
                int alt67=2;
                int LA67_0 = input.LA(1);

                if ( (LA67_0==LEFT_SQUARE) ) {
                    alt67=1;
                }


                switch (alt67) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1642:42: '[' ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_argument4746); if (failed) return name;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_argument4748); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       name = name + "[]";
            	    }

            	    }
            	    break;

            	default :
            	    break loop67;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1645:1: rhs_chunk[RuleDescr rule] : start= THEN (~ END )* loc= END ;
    public final void rhs_chunk(RuleDescr rule) throws RecognitionException {
        Token start=null;
        Token loc=null;


                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1651:10: (start= THEN (~ END )* loc= END )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1651:10: start= THEN (~ END )* loc= END
            {
            if ( backtracking==0 ) {

              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            start=(Token)input.LT(1);
            match(input,THEN,FOLLOW_THEN_in_rhs_chunk4792); if (failed) return ;
            if ( backtracking==0 ) {

              			location.setType( Location.LOCATION_RHS );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1660:3: (~ END )*
            loop68:
            do {
                int alt68=2;
                int LA68_0 = input.LA(1);

                if ( ((LA68_0>=ATTRIBUTES && LA68_0<=QUERY)||(LA68_0>=TEMPLATE && LA68_0<=79)) ) {
                    alt68=1;
                }


                switch (alt68) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1661:6: ~ END
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=QUERY)||(input.LA(1)>=TEMPLATE && input.LA(1)<=79) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_rhs_chunk4808);    throw mse;
            	    }

            	    if ( backtracking==0 ) {

            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop68;
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
            match(input,END,FOLLOW_END_in_rhs_chunk4845); if (failed) return ;
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
               		    location.setProperty( Location.LOCATION_RHS_CONTENT, rule.getConsequence() );
                              
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1692:1: name returns [String s] : (tok= ID | str= STRING ) ;
    public final String name() throws RecognitionException {
        String s = null;

        Token tok=null;
        Token str=null;

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1694:2: ( (tok= ID | str= STRING ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1694:2: (tok= ID | str= STRING )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1694:2: (tok= ID | str= STRING )
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==ID) ) {
                alt69=1;
            }
            else if ( (LA69_0==STRING) ) {
                alt69=2;
            }
            else {
                if (backtracking>0) {failed=true; return s;}
                NoViableAltException nvae =
                    new NoViableAltException("1694:2: (tok= ID | str= STRING )", 69, 0, input);

                throw nvae;
            }
            switch (alt69) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1695:6: tok= ID
                    {
                    tok=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_name4889); if (failed) return s;
                    if ( backtracking==0 ) {

                      	        s = tok.getText();
                      	    
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1700:6: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_name4908); if (failed) return s;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1707:1: identifier returns [Token tok] : (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MEMBEROF | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END | t= IN ) ;
    public final Token identifier() throws RecognitionException {
        Token tok = null;

        Token t=null;

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1709:2: ( (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MEMBEROF | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END | t= IN ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1709:2: (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MEMBEROF | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END | t= IN )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1709:2: (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MEMBEROF | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END | t= IN )
            int alt70=33;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt70=1;
                }
                break;
            case PACKAGE:
                {
                alt70=2;
                }
                break;
            case FUNCTION:
                {
                alt70=3;
                }
                break;
            case GLOBAL:
                {
                alt70=4;
                }
                break;
            case IMPORT:
                {
                alt70=5;
                }
                break;
            case RULE:
                {
                alt70=6;
                }
                break;
            case QUERY:
                {
                alt70=7;
                }
                break;
            case TEMPLATE:
                {
                alt70=8;
                }
                break;
            case ATTRIBUTES:
                {
                alt70=9;
                }
                break;
            case ENABLED:
                {
                alt70=10;
                }
                break;
            case SALIENCE:
                {
                alt70=11;
                }
                break;
            case DURATION:
                {
                alt70=12;
                }
                break;
            case FROM:
                {
                alt70=13;
                }
                break;
            case ACCUMULATE:
                {
                alt70=14;
                }
                break;
            case INIT:
                {
                alt70=15;
                }
                break;
            case ACTION:
                {
                alt70=16;
                }
                break;
            case RESULT:
                {
                alt70=17;
                }
                break;
            case COLLECT:
                {
                alt70=18;
                }
                break;
            case OR:
                {
                alt70=19;
                }
                break;
            case AND:
                {
                alt70=20;
                }
                break;
            case CONTAINS:
                {
                alt70=21;
                }
                break;
            case EXCLUDES:
                {
                alt70=22;
                }
                break;
            case MEMBEROF:
                {
                alt70=23;
                }
                break;
            case MATCHES:
                {
                alt70=24;
                }
                break;
            case NULL:
                {
                alt70=25;
                }
                break;
            case EXISTS:
                {
                alt70=26;
                }
                break;
            case NOT:
                {
                alt70=27;
                }
                break;
            case EVAL:
                {
                alt70=28;
                }
                break;
            case FORALL:
                {
                alt70=29;
                }
                break;
            case WHEN:
                {
                alt70=30;
                }
                break;
            case THEN:
                {
                alt70=31;
                }
                break;
            case END:
                {
                alt70=32;
                }
                break;
            case IN:
                {
                alt70=33;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return tok;}
                NoViableAltException nvae =
                    new NoViableAltException("1709:2: (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MEMBEROF | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END | t= IN )", 70, 0, input);

                throw nvae;
            }

            switch (alt70) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1709:10: t= ID
                    {
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_identifier4946); if (failed) return tok;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1710:4: t= PACKAGE
                    {
                    t=(Token)input.LT(1);
                    match(input,PACKAGE,FOLLOW_PACKAGE_in_identifier4959); if (failed) return tok;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1711:4: t= FUNCTION
                    {
                    t=(Token)input.LT(1);
                    match(input,FUNCTION,FOLLOW_FUNCTION_in_identifier4966); if (failed) return tok;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1712:4: t= GLOBAL
                    {
                    t=(Token)input.LT(1);
                    match(input,GLOBAL,FOLLOW_GLOBAL_in_identifier4973); if (failed) return tok;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1713:4: t= IMPORT
                    {
                    t=(Token)input.LT(1);
                    match(input,IMPORT,FOLLOW_IMPORT_in_identifier4980); if (failed) return tok;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1714:4: t= RULE
                    {
                    t=(Token)input.LT(1);
                    match(input,RULE,FOLLOW_RULE_in_identifier4989); if (failed) return tok;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1715:4: t= QUERY
                    {
                    t=(Token)input.LT(1);
                    match(input,QUERY,FOLLOW_QUERY_in_identifier4996); if (failed) return tok;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1716:17: t= TEMPLATE
                    {
                    t=(Token)input.LT(1);
                    match(input,TEMPLATE,FOLLOW_TEMPLATE_in_identifier5017); if (failed) return tok;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1717:17: t= ATTRIBUTES
                    {
                    t=(Token)input.LT(1);
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_identifier5045); if (failed) return tok;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1718:17: t= ENABLED
                    {
                    t=(Token)input.LT(1);
                    match(input,ENABLED,FOLLOW_ENABLED_in_identifier5071); if (failed) return tok;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1719:17: t= SALIENCE
                    {
                    t=(Token)input.LT(1);
                    match(input,SALIENCE,FOLLOW_SALIENCE_in_identifier5100); if (failed) return tok;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1720:17: t= DURATION
                    {
                    t=(Token)input.LT(1);
                    match(input,DURATION,FOLLOW_DURATION_in_identifier5122); if (failed) return tok;

                    }
                    break;
                case 13 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1721:17: t= FROM
                    {
                    t=(Token)input.LT(1);
                    match(input,FROM,FOLLOW_FROM_in_identifier5144); if (failed) return tok;

                    }
                    break;
                case 14 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1722:17: t= ACCUMULATE
                    {
                    t=(Token)input.LT(1);
                    match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_identifier5173); if (failed) return tok;

                    }
                    break;
                case 15 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1723:17: t= INIT
                    {
                    t=(Token)input.LT(1);
                    match(input,INIT,FOLLOW_INIT_in_identifier5195); if (failed) return tok;

                    }
                    break;
                case 16 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1724:17: t= ACTION
                    {
                    t=(Token)input.LT(1);
                    match(input,ACTION,FOLLOW_ACTION_in_identifier5224); if (failed) return tok;

                    }
                    break;
                case 17 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1725:17: t= RESULT
                    {
                    t=(Token)input.LT(1);
                    match(input,RESULT,FOLLOW_RESULT_in_identifier5253); if (failed) return tok;

                    }
                    break;
                case 18 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1726:17: t= COLLECT
                    {
                    t=(Token)input.LT(1);
                    match(input,COLLECT,FOLLOW_COLLECT_in_identifier5282); if (failed) return tok;

                    }
                    break;
                case 19 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1727:17: t= OR
                    {
                    t=(Token)input.LT(1);
                    match(input,OR,FOLLOW_OR_in_identifier5311); if (failed) return tok;

                    }
                    break;
                case 20 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1728:17: t= AND
                    {
                    t=(Token)input.LT(1);
                    match(input,AND,FOLLOW_AND_in_identifier5340); if (failed) return tok;

                    }
                    break;
                case 21 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1729:17: t= CONTAINS
                    {
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_identifier5369); if (failed) return tok;

                    }
                    break;
                case 22 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1730:17: t= EXCLUDES
                    {
                    t=(Token)input.LT(1);
                    match(input,EXCLUDES,FOLLOW_EXCLUDES_in_identifier5391); if (failed) return tok;

                    }
                    break;
                case 23 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1731:17: t= MEMBEROF
                    {
                    t=(Token)input.LT(1);
                    match(input,MEMBEROF,FOLLOW_MEMBEROF_in_identifier5413); if (failed) return tok;

                    }
                    break;
                case 24 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1732:17: t= MATCHES
                    {
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_identifier5433); if (failed) return tok;

                    }
                    break;
                case 25 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1733:17: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_identifier5462); if (failed) return tok;

                    }
                    break;
                case 26 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1734:17: t= EXISTS
                    {
                    t=(Token)input.LT(1);
                    match(input,EXISTS,FOLLOW_EXISTS_in_identifier5491); if (failed) return tok;

                    }
                    break;
                case 27 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1735:17: t= NOT
                    {
                    t=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_identifier5520); if (failed) return tok;

                    }
                    break;
                case 28 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1736:17: t= EVAL
                    {
                    t=(Token)input.LT(1);
                    match(input,EVAL,FOLLOW_EVAL_in_identifier5549); if (failed) return tok;

                    }
                    break;
                case 29 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1737:17: t= FORALL
                    {
                    t=(Token)input.LT(1);
                    match(input,FORALL,FOLLOW_FORALL_in_identifier5578); if (failed) return tok;

                    }
                    break;
                case 30 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1738:17: t= WHEN
                    {
                    t=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_identifier5616); if (failed) return tok;

                    }
                    break;
                case 31 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1739:17: t= THEN
                    {
                    t=(Token)input.LT(1);
                    match(input,THEN,FOLLOW_THEN_in_identifier5648); if (failed) return tok;

                    }
                    break;
                case 32 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1740:17: t= END
                    {
                    t=(Token)input.LT(1);
                    match(input,END,FOLLOW_END_in_identifier5677); if (failed) return tok;

                    }
                    break;
                case 33 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1741:11: t= IN
                    {
                    t=(Token)input.LT(1);
                    match(input,IN,FOLLOW_IN_in_identifier5696); if (failed) return tok;

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
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:208:4: ( function_import_statement )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:208:4: function_import_statement
        {
        pushFollow(FOLLOW_function_import_statement_in_synpred7156);
        function_import_statement();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred7

    // $ANTLR start synpred8
    public final void synpred8_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:209:4: ( import_statement )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:209:4: import_statement
        {
        pushFollow(FOLLOW_import_statement_in_synpred8162);
        import_statement();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred8

    // $ANTLR start synpred43
    public final void synpred43_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:767:4: ( paren_chunk[from] )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:767:4: paren_chunk[from]
        {
        pushFollow(FOLLOW_paren_chunk_in_synpred431908);
        paren_chunk(from);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred43

    // $ANTLR start synpred45
    public final void synpred45_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:803:6: ( LEFT_SQUARE )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:803:8: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred451978); if (failed) return ;

        }
    }
    // $ANTLR end synpred45

    // $ANTLR start synpred46
    public final void synpred46_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:808:6: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:808:8: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred462012); if (failed) return ;

        }
    }
    // $ANTLR end synpred46

    // $ANTLR start synpred53
    public final void synpred53_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:927:6: ( ( OR | DOUBLE_PIPE ) fact )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:927:6: ( OR | DOUBLE_PIPE ) fact
        {
        if ( (input.LA(1)>=OR && input.LA(1)<=DOUBLE_PIPE) ) {
            input.consume();
            errorRecovery=false;failed=false;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            MismatchedSetException mse =
                new MismatchedSetException(null,input);
            recoverFromMismatchedSet(input,mse,FOLLOW_set_in_synpred532335);    throw mse;
        }

        pushFollow(FOLLOW_fact_in_synpred532353);
        fact();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred53

    // $ANTLR start synpred58
    public final void synpred58_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1040:5: ( field_constraint[base] )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1040:5: field_constraint[base]
        {
        pushFollow(FOLLOW_field_constraint_in_synpred582643);
        field_constraint(base);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred58

    // $ANTLR start synpred63
    public final void synpred63_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1104:5: ( DOUBLE_PIPE and_restr_connective[or] )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1104:5: DOUBLE_PIPE and_restr_connective[or]
        {
        match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred632789); if (failed) return ;
        pushFollow(FOLLOW_and_restr_connective_in_synpred632801);
        and_restr_connective(or);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred63

    // $ANTLR start synpred64
    public final void synpred64_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1126:5: ( DOUBLE_AMPER constraint_expression[and] )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1126:5: DOUBLE_AMPER constraint_expression[and]
        {
        match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred642845); if (failed) return ;
        pushFollow(FOLLOW_constraint_expression_in_synpred642855);
        constraint_expression(and);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred64

    // $ANTLR start synpred105
    public final void synpred105_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1497:14: ( ACCUMULATE )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1497:16: ACCUMULATE
        {
        match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_synpred1054092); if (failed) return ;

        }
    }
    // $ANTLR end synpred105

    // $ANTLR start synpred106
    public final void synpred106_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1498:14: ( COLLECT )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1498:16: COLLECT
        {
        match(input,COLLECT,FOLLOW_COLLECT_in_synpred1064121); if (failed) return ;

        }
    }
    // $ANTLR end synpred106

    // $ANTLR start synpred108
    public final void synpred108_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1499:14: (~ ( ACCUMULATE | COLLECT ) )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1499:16: ~ ( ACCUMULATE | COLLECT )
        {
        if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=LOCK_ON_ACTIVE)||(input.LA(1)>=LEFT_PAREN && input.LA(1)<=RIGHT_PAREN)||(input.LA(1)>=ID && input.LA(1)<=79) ) {
            input.consume();
            errorRecovery=false;failed=false;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            MismatchedSetException mse =
                new MismatchedSetException(null,input);
            recoverFromMismatchedSet(input,mse,FOLLOW_set_in_synpred1084151);    throw mse;
        }


        }
    }
    // $ANTLR end synpred108

    public final boolean synpred106() {
        backtracking++;
        int start = input.mark();
        try {
            synpred106_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred53() {
        backtracking++;
        int start = input.mark();
        try {
            synpred53_fragment(); // can never throw exception
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
    public final boolean synpred63() {
        backtracking++;
        int start = input.mark();
        try {
            synpred63_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred58() {
        backtracking++;
        int start = input.mark();
        try {
            synpred58_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred43() {
        backtracking++;
        int start = input.mark();
        try {
            synpred43_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred108() {
        backtracking++;
        int start = input.mark();
        try {
            synpred108_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred105() {
        backtracking++;
        int start = input.mark();
        try {
            synpred105_fragment(); // can never throw exception
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
    public final boolean synpred64() {
        backtracking++;
        int start = input.mark();
        try {
            synpred64_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred46() {
        backtracking++;
        int start = input.mark();
        try {
            synpred46_fragment(); // can never throw exception
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
        "\2\4\2\uffff\1\65\1\4";
    static final String DFA11_maxS =
        "\1\72\1\107\2\uffff\1\65\1\72";
    static final String DFA11_acceptS =
        "\2\uffff\1\2\1\1\2\uffff";
    static final String DFA11_specialS =
        "\6\uffff}>";
    static final String[] DFA11_transitionS = {
            "\12\2\3\uffff\1\2\1\uffff\1\2\6\uffff\1\2\2\uffff\1\2\2\uffff"+
            "\3\2\1\uffff\1\2\1\1\1\2\2\uffff\7\2\1\uffff\1\2\4\uffff\5\2",
            "\12\3\3\uffff\1\3\1\uffff\1\3\6\uffff\1\3\2\uffff\1\3\1\uffff"+
            "\1\2\3\3\1\2\3\3\2\uffff\7\3\1\uffff\1\3\2\uffff\1\4\1\uffff"+
            "\5\3\14\uffff\1\3",
            "",
            "",
            "\1\5",
            "\12\3\3\uffff\1\3\1\uffff\1\3\6\uffff\1\3\2\uffff\1\3\1\uffff"+
            "\1\2\3\3\1\2\3\3\2\uffff\7\3\1\uffff\1\3\2\uffff\1\4\1\uffff"+
            "\5\3"
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
            return "326:6: (paramType= dotted_name[null] )?";
        }
    }
    static final String DFA12_eotS =
        "\6\uffff";
    static final String DFA12_eofS =
        "\6\uffff";
    static final String DFA12_minS =
        "\2\4\1\uffff\1\65\1\uffff\1\4";
    static final String DFA12_maxS =
        "\1\72\1\107\1\uffff\1\65\1\uffff\1\72";
    static final String DFA12_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\1\uffff";
    static final String DFA12_specialS =
        "\6\uffff}>";
    static final String[] DFA12_transitionS = {
            "\12\2\3\uffff\1\2\1\uffff\1\2\6\uffff\1\2\2\uffff\1\2\2\uffff"+
            "\3\2\1\uffff\1\2\1\1\1\2\2\uffff\7\2\1\uffff\1\2\4\uffff\5\2",
            "\12\4\3\uffff\1\4\1\uffff\1\4\6\uffff\1\4\2\uffff\1\4\1\uffff"+
            "\1\2\3\4\1\2\3\4\2\uffff\7\4\1\uffff\1\4\2\uffff\1\3\1\uffff"+
            "\5\4\14\uffff\1\4",
            "",
            "\1\5",
            "",
            "\12\4\3\uffff\1\4\1\uffff\1\4\6\uffff\1\4\2\uffff\1\4\1\uffff"+
            "\1\2\3\4\1\2\3\4\2\uffff\7\4\1\uffff\1\4\2\uffff\1\3\1\uffff"+
            "\5\4"
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
            return "330:11: (paramType= dotted_name[null] )?";
        }
    }
 

    public static final BitSet FOLLOW_69_in_opt_semicolon46 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit64 = new BitSet(new long[]{0x0000000000001BC0L});
    public static final BitSet FOLLOW_statement_in_compilation_unit71 = new BitSet(new long[]{0x0000000000001BC2L});
    public static final BitSet FOLLOW_package_statement_in_prolog96 = new BitSet(new long[]{0x000000009FEB4012L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_prolog111 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_prolog113 = new BitSet(new long[]{0x000000009FEB4002L});
    public static final BitSet FOLLOW_COMMA_in_prolog122 = new BitSet(new long[]{0x000000001FEB4000L});
    public static final BitSet FOLLOW_rule_attribute_in_prolog127 = new BitSet(new long[]{0x000000009FEB4002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_package_statement238 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement242 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_opt_semicolon_in_package_statement245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_statement277 = new BitSet(new long[]{0x07C2FE77240A3FF0L});
    public static final BitSet FOLLOW_import_name_in_import_statement300 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_opt_semicolon_in_import_statement303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_function_import_statement329 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FUNCTION_in_function_import_statement331 = new BitSet(new long[]{0x07C2FE77240A3FF0L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement354 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_opt_semicolon_in_function_import_statement357 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_import_name385 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000180L});
    public static final BitSet FOLLOW_71_in_import_name397 = new BitSet(new long[]{0x07C2FE77240A3FF0L});
    public static final BitSet FOLLOW_identifier_in_import_name401 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000180L});
    public static final BitSet FOLLOW_72_in_import_name425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GLOBAL_in_global461 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_dotted_name_in_global472 = new BitSet(new long[]{0x07C2FE77240A3FF0L});
    public static final BitSet FOLLOW_identifier_in_global484 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_opt_semicolon_in_global486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function513 = new BitSet(new long[]{0x07C2FE77240A3FF0L});
    public static final BitSet FOLLOW_dotted_name_in_function518 = new BitSet(new long[]{0x07C2FE77240A3FF0L});
    public static final BitSet FOLLOW_identifier_in_function525 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function534 = new BitSet(new long[]{0x07C2FE7F240A3FF0L});
    public static final BitSet FOLLOW_dotted_name_in_function544 = new BitSet(new long[]{0x07C2FE77240A3FF0L});
    public static final BitSet FOLLOW_argument_in_function551 = new BitSet(new long[]{0x0000000880000000L});
    public static final BitSet FOLLOW_COMMA_in_function565 = new BitSet(new long[]{0x07C2FE77240A3FF0L});
    public static final BitSet FOLLOW_dotted_name_in_function570 = new BitSet(new long[]{0x07C2FE77240A3FF0L});
    public static final BitSet FOLLOW_argument_in_function577 = new BitSet(new long[]{0x0000000880000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function601 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_curly_chunk_in_function607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_query639 = new BitSet(new long[]{0x0000002000008000L});
    public static final BitSet FOLLOW_name_in_query643 = new BitSet(new long[]{0x03000A2040000400L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query656 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_END_in_query673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_template703 = new BitSet(new long[]{0x07C2FE77240A3FF0L});
    public static final BitSet FOLLOW_identifier_in_template707 = new BitSet(new long[]{0x0000002000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_opt_semicolon_in_template709 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_template_slot_in_template724 = new BitSet(new long[]{0x0000002000000400L});
    public static final BitSet FOLLOW_END_in_template741 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_opt_semicolon_in_template743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_template_slot789 = new BitSet(new long[]{0x07C2FE77240A3FF0L});
    public static final BitSet FOLLOW_identifier_in_template_slot807 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_opt_semicolon_in_template_slot809 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule842 = new BitSet(new long[]{0x0000002000008000L});
    public static final BitSet FOLLOW_name_in_rule846 = new BitSet(new long[]{0x040000009FEB6010L});
    public static final BitSet FOLLOW_rule_attributes_in_rule855 = new BitSet(new long[]{0x0400000000002000L});
    public static final BitSet FOLLOW_WHEN_in_rule864 = new BitSet(new long[]{0x07000A2040000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_rule866 = new BitSet(new long[]{0x07000A2040000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule884 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_rhs_chunk_in_rule905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_rule_attributes926 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_rule_attributes928 = new BitSet(new long[]{0x000000009FEB4002L});
    public static final BitSet FOLLOW_COMMA_in_rule_attributes937 = new BitSet(new long[]{0x000000001FEB4000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes942 = new BitSet(new long[]{0x000000009FEB4002L});
    public static final BitSet FOLLOW_salience_in_rule_attribute983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute993 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute1004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute1017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute1031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute1053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute1063 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute1073 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_in_rule_attribute1083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_in_rule_attribute1093 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_in_rule_attribute1102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EFFECTIVE_in_date_effective1134 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_date_effective1138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EXPIRES_in_date_expires1171 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_date_expires1175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENABLED_in_enabled1210 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_BOOL_in_enabled1214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_salience1259 = new BitSet(new long[]{0x0000000040100000L});
    public static final BitSet FOLLOW_INT_in_salience1272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_salience1287 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NO_LOOP_in_no_loop1327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NO_LOOP_in_no_loop1355 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AUTO_FOCUS_in_auto_focus1408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AUTO_FOCUS_in_auto_focus1436 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1440 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTIVATION_GROUP_in_activation_group1485 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_activation_group1489 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1521 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_ruleflow_group1525 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AGENDA_GROUP_in_agenda_group1557 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DURATION_in_duration1595 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_INT_in_duration1599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIALECT_in_dialect1631 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_dialect1635 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1684 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1712 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_BOOL_in_lock_on_active1716 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1754 = new BitSet(new long[]{0x03000A2040000002L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_pattern1819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_pattern1828 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_source_in_from_statement1855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_from_source1897 = new BitSet(new long[]{0x0000000040000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source1908 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_expression_chain_in_from_source1922 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_expression_chain1951 = new BitSet(new long[]{0x07C2FE77240A3FF0L});
    public static final BitSet FOLLOW_identifier_in_expression_chain1955 = new BitSet(new long[]{0x0010000040000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain1986 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain2020 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain2041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_accumulate_statement2082 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement2092 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_lhs_pattern_in_accumulate_statement2096 = new BitSet(new long[]{0x0000000180000000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2098 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_INIT_in_accumulate_statement2108 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2119 = new BitSet(new long[]{0x0000000280000000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2122 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_ACTION_in_accumulate_statement2131 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2135 = new BitSet(new long[]{0x0000000480000000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2138 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_RESULT_in_accumulate_statement2147 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2151 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement2163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_collect_statement2206 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement2216 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_lhs_pattern_in_collect_statement2220 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement2224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding2258 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_fact_binding2260 = new BitSet(new long[]{0x0000002040000000L});
    public static final BitSet FOLLOW_fact_expression_in_fact_binding2273 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_expression2305 = new BitSet(new long[]{0x0000002040000000L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2309 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_expression2312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_expression2323 = new BitSet(new long[]{0x000000C000000002L});
    public static final BitSet FOLLOW_set_in_fact_expression2335 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_fact_in_fact_expression2353 = new BitSet(new long[]{0x000000C000000002L});
    public static final BitSet FOLLOW_dotted_name_in_fact2414 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact2428 = new BitSet(new long[]{0x07C2FE7F640A3FF0L});
    public static final BitSet FOLLOW_constraints_in_fact2438 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact2451 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints2471 = new BitSet(new long[]{0x0000000080000002L});
    public static final BitSet FOLLOW_COMMA_in_constraints2478 = new BitSet(new long[]{0x07C2FE77640A3FF0L});
    public static final BitSet FOLLOW_constraint_in_constraints2487 = new BitSet(new long[]{0x0000000080000002L});
    public static final BitSet FOLLOW_or_constr_in_constraint2520 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_constr_in_or_constr2543 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_constr2553 = new BitSet(new long[]{0x07C2FE77640A3FF0L});
    public static final BitSet FOLLOW_and_constr_in_or_constr2563 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr2595 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_constr2605 = new BitSet(new long[]{0x07C2FE77640A3FF0L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr2615 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_field_constraint_in_unary_constr2643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_unary_constr2651 = new BitSet(new long[]{0x07C2FE77640A3FF0L});
    public static final BitSet FOLLOW_or_constr_in_unary_constr2653 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_unary_constr2656 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_unary_constr2662 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_predicate_in_unary_constr2664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_field_constraint2696 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_field_constraint2698 = new BitSet(new long[]{0x07C2FE77240A3FF0L});
    public static final BitSet FOLLOW_identifier_in_field_constraint2719 = new BitSet(new long[]{0x0000FC0040000002L,0x000000000000FE00L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint2733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_field_constraint2748 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_predicate_in_field_constraint2750 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective2779 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_restr_connective2789 = new BitSet(new long[]{0x0000FC0040000000L,0x000000000000FC00L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective2801 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective2835 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_restr_connective2845 = new BitSet(new long[]{0x0000FC0040000000L,0x000000000000FC00L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective2855 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_compound_operator_in_constraint_expression2890 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_operator_in_constraint_expression2897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_constraint_expression2904 = new BitSet(new long[]{0x0000FC0040000000L,0x000000000000FC00L});
    public static final BitSet FOLLOW_or_restr_connective_in_constraint_expression2913 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_constraint_expression2919 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_simple_operator2950 = new BitSet(new long[]{0x0003002040148000L});
    public static final BitSet FOLLOW_75_in_simple_operator2958 = new BitSet(new long[]{0x0003002040148000L});
    public static final BitSet FOLLOW_76_in_simple_operator2966 = new BitSet(new long[]{0x0003002040148000L});
    public static final BitSet FOLLOW_77_in_simple_operator2974 = new BitSet(new long[]{0x0003002040148000L});
    public static final BitSet FOLLOW_78_in_simple_operator2982 = new BitSet(new long[]{0x0003002040148000L});
    public static final BitSet FOLLOW_79_in_simple_operator2990 = new BitSet(new long[]{0x0003002040148000L});
    public static final BitSet FOLLOW_CONTAINS_in_simple_operator2998 = new BitSet(new long[]{0x0003002040148000L});
    public static final BitSet FOLLOW_NOT_in_simple_operator3006 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_CONTAINS_in_simple_operator3010 = new BitSet(new long[]{0x0003002040148000L});
    public static final BitSet FOLLOW_EXCLUDES_in_simple_operator3018 = new BitSet(new long[]{0x0003002040148000L});
    public static final BitSet FOLLOW_MATCHES_in_simple_operator3026 = new BitSet(new long[]{0x0003002040148000L});
    public static final BitSet FOLLOW_NOT_in_simple_operator3034 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_MATCHES_in_simple_operator3038 = new BitSet(new long[]{0x0003002040148000L});
    public static final BitSet FOLLOW_MEMBEROF_in_simple_operator3046 = new BitSet(new long[]{0x0003002040148000L});
    public static final BitSet FOLLOW_NOT_in_simple_operator3054 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_MEMBEROF_in_simple_operator3058 = new BitSet(new long[]{0x0003002040148000L});
    public static final BitSet FOLLOW_expression_value_in_simple_operator3072 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_compound_operator3101 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_NOT_in_compound_operator3113 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_IN_in_compound_operator3115 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_compound_operator3130 = new BitSet(new long[]{0x0003002040148000L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator3134 = new BitSet(new long[]{0x0000000880000000L});
    public static final BitSet FOLLOW_COMMA_in_compound_operator3146 = new BitSet(new long[]{0x0003002040148000L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator3150 = new BitSet(new long[]{0x0000000880000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_compound_operator3166 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_expression_value3194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enum_constraint_in_expression_value3207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_expression_value3227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_retval_constraint_in_expression_value3241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint3284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint3295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint3308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint3319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal_constraint3331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enum_constraint3366 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_71_in_enum_constraint3372 = new BitSet(new long[]{0x07C2FE77240A3FF0L});
    public static final BitSet FOLLOW_identifier_in_enum_constraint3376 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate3418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk3467 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_set_in_paren_chunk3483 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk3507 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk3544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk3595 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_set_in_curly_chunk3611 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk3635 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk3672 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk3735 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_set_in_square_chunk3751 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_square_chunk_in_square_chunk3775 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk3812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint3857 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3885 = new BitSet(new long[]{0x000000C000000002L});
    public static final BitSet FOLLOW_set_in_lhs_or3893 = new BitSet(new long[]{0x03000A2040000000L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3909 = new BitSet(new long[]{0x000000C000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3945 = new BitSet(new long[]{0x0040010000000002L});
    public static final BitSet FOLLOW_set_in_lhs_and3953 = new BitSet(new long[]{0x03000A2040000000L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3969 = new BitSet(new long[]{0x0040010000000002L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary4006 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary4014 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary4022 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_unary4030 = new BitSet(new long[]{0x0080000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_FROM_in_lhs_unary4046 = new BitSet(new long[]{0x07C2FE77240A3FF0L});
    public static final BitSet FOLLOW_accumulate_statement_in_lhs_unary4101 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_collect_statement_in_lhs_unary4130 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_from_statement_in_lhs_unary4165 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary4204 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary4212 = new BitSet(new long[]{0x03000A2040000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_unary4216 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary4218 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_opt_semicolon_in_lhs_unary4228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_lhs_exist4252 = new BitSet(new long[]{0x0000002040000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist4272 = new BitSet(new long[]{0x03000A2040000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist4276 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist4308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_exist4358 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_lhs_not4412 = new BitSet(new long[]{0x0000002040000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not4425 = new BitSet(new long[]{0x03000A2040000000L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not4429 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not4462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_not4499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_lhs_eval4547 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval4558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_lhs_forall4587 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall4589 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall4593 = new BitSet(new long[]{0x0000002080000000L});
    public static final BitSet FOLLOW_COMMA_in_lhs_forall4607 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall4613 = new BitSet(new long[]{0x0000002880000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall4628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name4659 = new BitSet(new long[]{0x0010000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_71_in_dotted_name4671 = new BitSet(new long[]{0x07C2FE77240A3FF0L});
    public static final BitSet FOLLOW_identifier_in_dotted_name4675 = new BitSet(new long[]{0x0010000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dotted_name4697 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dotted_name4701 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_identifier_in_argument4740 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument4746 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument4748 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk4792 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_set_in_rhs_chunk4808 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_END_in_rhs_chunk4845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_name4889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_name4908 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_identifier4946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_identifier4959 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_identifier4966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GLOBAL_in_identifier4973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_identifier4980 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_identifier4989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_identifier4996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_identifier5017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_identifier5045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENABLED_in_identifier5071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_identifier5100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DURATION_in_identifier5122 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_identifier5144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_identifier5173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INIT_in_identifier5195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_identifier5224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RESULT_in_identifier5253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_identifier5282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_identifier5311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_in_identifier5340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONTAINS_in_identifier5369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXCLUDES_in_identifier5391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MEMBEROF_in_identifier5413 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MATCHES_in_identifier5433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_identifier5462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_identifier5491 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_identifier5520 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_identifier5549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_identifier5578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_identifier5616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THEN_in_identifier5648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_END_in_identifier5677 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_identifier5696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_synpred7156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_synpred8162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_synpred431908 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred451978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred462012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred532335 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_fact_in_synpred532353 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_field_constraint_in_synpred582643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred632789 = new BitSet(new long[]{0x0000FC0040000000L,0x000000000000FC00L});
    public static final BitSet FOLLOW_and_restr_connective_in_synpred632801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred642845 = new BitSet(new long[]{0x0000FC0040000000L,0x000000000000FC00L});
    public static final BitSet FOLLOW_constraint_expression_in_synpred642855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_synpred1054092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_synpred1064121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred1084151 = new BitSet(new long[]{0x0000000000000002L});

}