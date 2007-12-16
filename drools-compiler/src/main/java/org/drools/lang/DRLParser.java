// $ANTLR 3.0.1 /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g 2007-12-16 09:53:56

	package org.drools.lang;
	import java.util.List;
	import java.util.ArrayList;
	import java.util.Iterator;
	import java.util.HashMap;	
	import java.util.StringTokenizer;
	import org.drools.lang.descr.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class DRLParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PACKAGE", "IMPORT", "FUNCTION", "EVENT", "ID", "DOT", "GLOBAL", "LEFT_PAREN", "COMMA", "RIGHT_PAREN", "QUERY", "END", "TEMPLATE", "RULE", "WHEN", "ATTRIBUTES", "DATE_EFFECTIVE", "STRING", "DATE_EXPIRES", "ENABLED", "BOOL", "SALIENCE", "INT", "NO_LOOP", "AUTO_FOCUS", "ACTIVATION_GROUP", "RULEFLOW_GROUP", "AGENDA_GROUP", "DURATION", "DIALECT", "LOCK_ON_ACTIVE", "OR", "DOUBLE_PIPE", "AND", "DOUBLE_AMPER", "FROM", "EXISTS", "NOT", "EVAL", "FORALL", "ACCUMULATE", "INIT", "ACTION", "REVERSE", "RESULT", "COLLECT", "ENTRY_POINT", "CONTAINS", "EXCLUDES", "MATCHES", "SOUNDSLIKE", "MEMBEROF", "TILDE", "IN", "FLOAT", "NULL", "LEFT_CURLY", "RIGHT_CURLY", "LEFT_SQUARE", "RIGHT_SQUARE", "THEN", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "MISC", "';'", "'.*'", "':'", "'->'", "'=='", "'>'", "'>='", "'<'", "'<='", "'!='"
    };
    public static final int COMMA=12;
    public static final int EXISTS=40;
    public static final int AUTO_FOCUS=28;
    public static final int END=15;
    public static final int HexDigit=68;
    public static final int FORALL=43;
    public static final int TEMPLATE=16;
    public static final int MISC=74;
    public static final int FLOAT=58;
    public static final int QUERY=14;
    public static final int THEN=64;
    public static final int RULE=17;
    public static final int INIT=45;
    public static final int TILDE=56;
    public static final int IMPORT=5;
    public static final int PACKAGE=4;
    public static final int DATE_EFFECTIVE=20;
    public static final int OR=35;
    public static final int DOT=9;
    public static final int DOUBLE_PIPE=36;
    public static final int AND=37;
    public static final int FUNCTION=6;
    public static final int GLOBAL=10;
    public static final int EscapeSequence=67;
    public static final int DIALECT=33;
    public static final int INT=26;
    public static final int LOCK_ON_ACTIVE=34;
    public static final int DATE_EXPIRES=22;
    public static final int LEFT_SQUARE=62;
    public static final int CONTAINS=51;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=71;
    public static final int ATTRIBUTES=19;
    public static final int EVENT=7;
    public static final int LEFT_CURLY=60;
    public static final int RESULT=48;
    public static final int ID=8;
    public static final int FROM=39;
    public static final int LEFT_PAREN=11;
    public static final int ACTIVATION_GROUP=29;
    public static final int DOUBLE_AMPER=38;
    public static final int RIGHT_CURLY=61;
    public static final int SOUNDSLIKE=54;
    public static final int EXCLUDES=52;
    public static final int BOOL=24;
    public static final int MEMBEROF=55;
    public static final int WHEN=18;
    public static final int RULEFLOW_GROUP=30;
    public static final int WS=66;
    public static final int STRING=21;
    public static final int ACTION=46;
    public static final int COLLECT=49;
    public static final int IN=57;
    public static final int REVERSE=47;
    public static final int ACCUMULATE=44;
    public static final int NO_LOOP=27;
    public static final int UnicodeEscape=69;
    public static final int DURATION=32;
    public static final int EVAL=42;
    public static final int MATCHES=53;
    public static final int EOF=-1;
    public static final int EOL=65;
    public static final int NULL=59;
    public static final int AGENDA_GROUP=31;
    public static final int OctalEscape=70;
    public static final int SALIENCE=25;
    public static final int MULTI_LINE_COMMENT=73;
    public static final int RIGHT_PAREN=13;
    public static final int NOT=41;
    public static final int ENABLED=23;
    public static final int RIGHT_SQUARE=63;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=72;
    public static final int ENTRY_POINT=50;

        public DRLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[85+1];
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
    	
    	private String getString(String token) {
    		return safeSubstring( token, 1, token.length() -1 );
    	}
    	
    	private String cleanupSpaces( String input ) {
                    return input.replaceAll( "\\s", "" );
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
                            if( mte.expecting >=0 && mte.expecting < tokenNames.length ) {
    	                        message.append("mismatched token: "+
                                                               e.token+
                                                               "; expecting type "+
                                                               tokenNames[mte.expecting]);
                            } else {
    	                        message.append("mismatched token: "+
                                                               e.token+
                                                               ";");
                            }
                    }
                    else if ( e instanceof MismatchedTreeNodeException ) {
                            MismatchedTreeNodeException mtne = (MismatchedTreeNodeException)e;
                            if( mtne.expecting >=0 && mtne.expecting < tokenNames.length ) {
    	                        message.append("mismatched tree node: "+
                                                               mtne.toString() +
                                                               "; expecting type "+
                                                               tokenNames[mtne.expecting]);
                            } else {
    	                        message.append("mismatched tree node: "+
                                                               mtne.toString() +
                                                               ";");
                            }
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
            
            private String safeSubstring( String text, int start, int end ) {
                	return text.substring( Math.min( start, text.length() ), Math.min( Math.max( start, end ), text.length() ) );
            }
          



    // $ANTLR start opt_semicolon
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:195:1: opt_semicolon : ( ';' )? ;
    public final void opt_semicolon() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:196:2: ( ( ';' )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:196:4: ( ';' )?
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:196:4: ( ';' )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==75) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:196:4: ';'
                    {
                    match(input,75,FOLLOW_75_in_opt_semicolon39); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:199:1: compilation_unit : prolog ( statement )* EOF ;
    public final void compilation_unit() throws RecognitionException {

        		// reset Location information
        		this.location = new Location( Location.LOCATION_UNKNOWN );
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:204:2: ( prolog ( statement )* EOF )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:204:4: prolog ( statement )* EOF
            {
            pushFollow(FOLLOW_prolog_in_compilation_unit57);
            prolog();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:205:3: ( statement )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>=IMPORT && LA2_0<=FUNCTION)||LA2_0==GLOBAL||LA2_0==QUERY||(LA2_0>=TEMPLATE && LA2_0<=RULE)||LA2_0==DATE_EFFECTIVE||(LA2_0>=DATE_EXPIRES && LA2_0<=ENABLED)||LA2_0==SALIENCE||(LA2_0>=NO_LOOP && LA2_0<=LOCK_ON_ACTIVE)) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:205:3: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_compilation_unit62);
            	    statement();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            match(input,EOF,FOLLOW_EOF_in_compilation_unit67); if (failed) return ;

            }

        }
        catch ( RecognitionException e ) {

            		reportError( e );
            	
        }
        finally {
        }
        return ;
    }
    // $ANTLR end compilation_unit


    // $ANTLR start prolog
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:212:1: prolog : (pkgstmt= package_statement )? ;
    public final void prolog() throws RecognitionException {
        String pkgstmt = null;



        		String packageName = "";
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:216:2: ( (pkgstmt= package_statement )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:216:4: (pkgstmt= package_statement )?
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:216:4: (pkgstmt= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==PACKAGE) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:216:6: pkgstmt= package_statement
                    {
                    pushFollow(FOLLOW_package_statement_in_prolog96);
                    pkgstmt=package_statement();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       packageName = pkgstmt; 
                    }

                    }
                    break;

            }

            if ( backtracking==0 ) {
               
              			this.packageDescr = factory.createPackage( packageName ); 
              		
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
    // $ANTLR end prolog


    // $ANTLR start statement
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:223:1: statement : (a= rule_attribute | function_import_statement | event_import_statement | import_statement | global | function | t= template | r= rule | q= query );
    public final void statement() throws RecognitionException {
        AttributeDescr a = null;

        FactTemplateDescr t = null;

        RuleDescr r = null;

        QueryDescr q = null;


        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:224:2: (a= rule_attribute | function_import_statement | event_import_statement | import_statement | global | function | t= template | r= rule | q= query )
            int alt4=9;
            switch ( input.LA(1) ) {
            case DATE_EFFECTIVE:
            case DATE_EXPIRES:
            case ENABLED:
            case SALIENCE:
            case NO_LOOP:
            case AUTO_FOCUS:
            case ACTIVATION_GROUP:
            case RULEFLOW_GROUP:
            case AGENDA_GROUP:
            case DURATION:
            case DIALECT:
            case LOCK_ON_ACTIVE:
                {
                alt4=1;
                }
                break;
            case IMPORT:
                {
                switch ( input.LA(2) ) {
                case EVENT:
                    {
                    alt4=3;
                    }
                    break;
                case FUNCTION:
                    {
                    alt4=2;
                    }
                    break;
                case ID:
                    {
                    alt4=4;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("223:1: statement : (a= rule_attribute | function_import_statement | event_import_statement | import_statement | global | function | t= template | r= rule | q= query );", 4, 2, input);

                    throw nvae;
                }

                }
                break;
            case GLOBAL:
                {
                alt4=5;
                }
                break;
            case FUNCTION:
                {
                alt4=6;
                }
                break;
            case TEMPLATE:
                {
                alt4=7;
                }
                break;
            case RULE:
                {
                alt4=8;
                }
                break;
            case QUERY:
                {
                alt4=9;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("223:1: statement : (a= rule_attribute | function_import_statement | event_import_statement | import_statement | global | function | t= template | r= rule | q= query );", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:224:4: a= rule_attribute
                    {
                    pushFollow(FOLLOW_rule_attribute_in_statement121);
                    a=rule_attribute();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       this.packageDescr.addAttribute( a ); 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:225:4: function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement128);
                    function_import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:226:4: event_import_statement
                    {
                    pushFollow(FOLLOW_event_import_statement_in_statement134);
                    event_import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:227:4: import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement139);
                    import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:228:4: global
                    {
                    pushFollow(FOLLOW_global_in_statement145);
                    global();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:229:4: function
                    {
                    pushFollow(FOLLOW_function_in_statement151);
                    function();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:230:10: t= template
                    {
                    pushFollow(FOLLOW_template_in_statement165);
                    t=template();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       this.packageDescr.addFactTemplate( t ); 
                    }

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:231:4: r= rule
                    {
                    pushFollow(FOLLOW_rule_in_statement174);
                    r=rule();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       this.packageDescr.addRule( r ); 
                    }

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:232:4: q= query
                    {
                    pushFollow(FOLLOW_query_in_statement186);
                    q=query();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       this.packageDescr.addRule( q ); 
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
        return ;
    }
    // $ANTLR end statement


    // $ANTLR start package_statement
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:235:1: package_statement returns [String packageName] : PACKAGE n= dotted_name opt_semicolon ;
    public final String package_statement() throws RecognitionException {
        String packageName = null;

        String n = null;



        		packageName = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:239:2: ( PACKAGE n= dotted_name opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:240:3: PACKAGE n= dotted_name opt_semicolon
            {
            match(input,PACKAGE,FOLLOW_PACKAGE_in_package_statement212); if (failed) return packageName;
            pushFollow(FOLLOW_dotted_name_in_package_statement216);
            n=dotted_name();
            _fsp--;
            if (failed) return packageName;
            pushFollow(FOLLOW_opt_semicolon_in_package_statement218);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:248:1: import_statement : IMPORT import_name[importDecl] opt_semicolon ;
    public final void import_statement() throws RecognitionException {
        Token IMPORT1=null;


                	ImportDescr importDecl = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:252:2: ( IMPORT import_name[importDecl] opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:252:4: IMPORT import_name[importDecl] opt_semicolon
            {
            IMPORT1=(Token)input.LT(1);
            match(input,IMPORT,FOLLOW_IMPORT_in_import_statement249); if (failed) return ;
            if ( backtracking==0 ) {

              	            importDecl = factory.createImport( );
              	            importDecl.setStartCharacter( ((CommonToken)IMPORT1).getStartIndex() );
              		    if (packageDescr != null) {
              			packageDescr.addImport( importDecl );
              		    }
              	        
            }
            pushFollow(FOLLOW_import_name_in_import_statement272);
            import_name(importDecl);
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_import_statement275);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:263:1: function_import_statement : IMPORT FUNCTION import_name[importDecl] opt_semicolon ;
    public final void function_import_statement() throws RecognitionException {
        Token IMPORT2=null;


                	FunctionImportDescr importDecl = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:267:2: ( IMPORT FUNCTION import_name[importDecl] opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:267:4: IMPORT FUNCTION import_name[importDecl] opt_semicolon
            {
            IMPORT2=(Token)input.LT(1);
            match(input,IMPORT,FOLLOW_IMPORT_in_function_import_statement299); if (failed) return ;
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function_import_statement301); if (failed) return ;
            if ( backtracking==0 ) {

              	            importDecl = factory.createFunctionImport();
              	            importDecl.setStartCharacter( ((CommonToken)IMPORT2).getStartIndex() );
              		    if (packageDescr != null) {
              			packageDescr.addFunctionImport( importDecl );
              		    }
              	        
            }
            pushFollow(FOLLOW_import_name_in_function_import_statement324);
            import_name(importDecl);
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_function_import_statement327);
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


    // $ANTLR start event_import_statement
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:278:1: event_import_statement : IMPORT EVENT import_name[importDecl] opt_semicolon ;
    public final void event_import_statement() throws RecognitionException {
        Token IMPORT3=null;


                	ImportDescr importDecl = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:282:2: ( IMPORT EVENT import_name[importDecl] opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:282:4: IMPORT EVENT import_name[importDecl] opt_semicolon
            {
            IMPORT3=(Token)input.LT(1);
            match(input,IMPORT,FOLLOW_IMPORT_in_event_import_statement351); if (failed) return ;
            match(input,EVENT,FOLLOW_EVENT_in_event_import_statement353); if (failed) return ;
            if ( backtracking==0 ) {

              	            importDecl = factory.createEventImport( );
              	            importDecl.setStartCharacter( ((CommonToken)IMPORT3).getStartIndex() );
              		    if (packageDescr != null) {
              			packageDescr.addImport( importDecl );
              		    }
              	        
            }
            pushFollow(FOLLOW_import_name_in_event_import_statement376);
            import_name(importDecl);
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_event_import_statement379);
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
    // $ANTLR end event_import_statement


    // $ANTLR start import_name
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:294:1: import_name[ImportDescr importDecl] returns [String name] : ID ( DOT id= identifier )* (star= '.*' )? ;
    public final String import_name(ImportDescr importDecl) throws RecognitionException {
        String name = null;

        Token star=null;
        Token ID4=null;
        Token DOT5=null;
        identifier_return id = null;



        		name = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:298:2: ( ID ( DOT id= identifier )* (star= '.*' )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:299:3: ID ( DOT id= identifier )* (star= '.*' )?
            {
            ID4=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_name405); if (failed) return name;
            if ( backtracking==0 ) {
               
              		    name =ID4.getText(); 
              		    importDecl.setTarget( name );
              		    importDecl.setEndCharacter( ((CommonToken)ID4).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:305:3: ( DOT id= identifier )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==DOT) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:305:5: DOT id= identifier
            	    {
            	    DOT5=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_import_name417); if (failed) return name;
            	    pushFollow(FOLLOW_identifier_in_import_name421);
            	    id=identifier();
            	    _fsp--;
            	    if (failed) return name;
            	    if ( backtracking==0 ) {
            	       
            	      		        name = name + DOT5.getText() + input.toString(id.start,id.stop); 
            	      			importDecl.setTarget( name );
            	      		        importDecl.setEndCharacter( ((CommonToken)((Token)id.start)).getStopIndex() );
            	      		    
            	    }

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:312:3: (star= '.*' )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==76) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:312:5: star= '.*'
                    {
                    star=(Token)input.LT(1);
                    match(input,76,FOLLOW_76_in_import_name445); if (failed) return name;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:322:1: global : GLOBAL type= dotted_name id= identifier opt_semicolon ;
    public final void global() throws RecognitionException {
        Token GLOBAL6=null;
        String type = null;

        identifier_return id = null;



        	    GlobalDescr global = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:326:2: ( GLOBAL type= dotted_name id= identifier opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:327:3: GLOBAL type= dotted_name id= identifier opt_semicolon
            {
            GLOBAL6=(Token)input.LT(1);
            match(input,GLOBAL,FOLLOW_GLOBAL_in_global479); if (failed) return ;
            if ( backtracking==0 ) {

              		    global = factory.createGlobal();
              	            global.setStartCharacter( ((CommonToken)GLOBAL6).getStartIndex() );
              		    packageDescr.addGlobal( global );
              		
            }
            pushFollow(FOLLOW_dotted_name_in_global490);
            type=dotted_name();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              		    global.setType( type );
              		
            }
            pushFollow(FOLLOW_identifier_in_global501);
            id=identifier();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_global503);
            opt_semicolon();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              		    global.setIdentifier( input.toString(id.start,id.stop) );
              		    global.setEndCharacter( ((CommonToken)((Token)id.start)).getStopIndex() );
              		
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:345:1: function : FUNCTION (retType= dotted_name )? id= identifier LEFT_PAREN ( (paramType= dotted_name )? paramName= argument ( COMMA (paramType= dotted_name )? paramName= argument )* )? RIGHT_PAREN body= curly_chunk ;
    public final void function() throws RecognitionException {
        Token FUNCTION7=null;
        String retType = null;

        identifier_return id = null;

        String paramType = null;

        String paramName = null;

        curly_chunk_return body = null;



        		FunctionDescr f = null;
        		String type = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:350:2: ( FUNCTION (retType= dotted_name )? id= identifier LEFT_PAREN ( (paramType= dotted_name )? paramName= argument ( COMMA (paramType= dotted_name )? paramName= argument )* )? RIGHT_PAREN body= curly_chunk )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:351:3: FUNCTION (retType= dotted_name )? id= identifier LEFT_PAREN ( (paramType= dotted_name )? paramName= argument ( COMMA (paramType= dotted_name )? paramName= argument )* )? RIGHT_PAREN body= curly_chunk
            {
            FUNCTION7=(Token)input.LT(1);
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function528); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:351:19: (retType= dotted_name )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( ((LA7_0>=PACKAGE && LA7_0<=ID)||LA7_0==GLOBAL||(LA7_0>=QUERY && LA7_0<=ATTRIBUTES)||LA7_0==ENABLED||LA7_0==SALIENCE||(LA7_0>=DURATION && LA7_0<=DIALECT)||LA7_0==FROM||(LA7_0>=INIT && LA7_0<=RESULT)||(LA7_0>=CONTAINS && LA7_0<=MEMBEROF)||LA7_0==IN||LA7_0==THEN) ) {
                int LA7_1 = input.LA(2);

                if ( ((LA7_1>=PACKAGE && LA7_1<=GLOBAL)||(LA7_1>=QUERY && LA7_1<=ATTRIBUTES)||LA7_1==ENABLED||LA7_1==SALIENCE||(LA7_1>=DURATION && LA7_1<=DIALECT)||LA7_1==FROM||(LA7_1>=INIT && LA7_1<=RESULT)||(LA7_1>=CONTAINS && LA7_1<=MEMBEROF)||LA7_1==IN||LA7_1==LEFT_SQUARE||LA7_1==THEN) ) {
                    alt7=1;
                }
            }
            switch (alt7) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:351:19: retType= dotted_name
                    {
                    pushFollow(FOLLOW_dotted_name_in_function532);
                    retType=dotted_name();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_identifier_in_function537);
            id=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              			//System.err.println( "function :: " + n.getText() );
              			type = retType != null ? retType : null;
              			f = factory.createFunction( input.toString(id.start,id.stop), type );
              			f.setLocation(offset(FUNCTION7.getLine()), FUNCTION7.getCharPositionInLine());
              	        	f.setStartCharacter( ((CommonToken)FUNCTION7).getStartIndex() );
              			packageDescr.addFunction( f );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_function546); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:361:4: ( (paramType= dotted_name )? paramName= argument ( COMMA (paramType= dotted_name )? paramName= argument )* )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( ((LA11_0>=PACKAGE && LA11_0<=ID)||LA11_0==GLOBAL||(LA11_0>=QUERY && LA11_0<=ATTRIBUTES)||LA11_0==ENABLED||LA11_0==SALIENCE||(LA11_0>=DURATION && LA11_0<=DIALECT)||LA11_0==FROM||(LA11_0>=INIT && LA11_0<=RESULT)||(LA11_0>=CONTAINS && LA11_0<=MEMBEROF)||LA11_0==IN||LA11_0==THEN) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:361:6: (paramType= dotted_name )? paramName= argument ( COMMA (paramType= dotted_name )? paramName= argument )*
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:361:15: (paramType= dotted_name )?
                    int alt8=2;
                    alt8 = dfa8.predict(input);
                    switch (alt8) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:361:15: paramType= dotted_name
                            {
                            pushFollow(FOLLOW_dotted_name_in_function555);
                            paramType=dotted_name();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_argument_in_function560);
                    paramName=argument();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      					type = paramType != null ? paramType : null;
                      					f.addParameter( type, paramName );
                      				
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:366:5: ( COMMA (paramType= dotted_name )? paramName= argument )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==COMMA) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:366:7: COMMA (paramType= dotted_name )? paramName= argument
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_function574); if (failed) return ;
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:366:22: (paramType= dotted_name )?
                    	    int alt9=2;
                    	    alt9 = dfa9.predict(input);
                    	    switch (alt9) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:366:22: paramType= dotted_name
                    	            {
                    	            pushFollow(FOLLOW_dotted_name_in_function578);
                    	            paramType=dotted_name();
                    	            _fsp--;
                    	            if (failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_argument_in_function583);
                    	    paramName=argument();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    if ( backtracking==0 ) {

                    	      						type = paramType != null ? paramType : null;
                    	      						f.addParameter( type, paramName );
                    	      					
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);


                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_function607); if (failed) return ;
            pushFollow(FOLLOW_curly_chunk_in_function613);
            body=curly_chunk();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              			//strip out '{','}'
              			f.setText( safeSubstring( input.toString(body.start,body.stop), 1, input.toString(body.start,body.stop).length()-1 ) );
              			f.setEndCharacter( ((CommonToken)((Token)body.stop)).getStopIndex() );
              			f.setEndLocation(offset(((Token)body.stop).getLine()), ((Token)body.stop).getCharPositionInLine());
              		
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


    // $ANTLR start argument
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:383:1: argument returns [String name] : id= identifier ( '[' ']' )* ;
    public final String argument() throws RecognitionException {
        String name = null;

        identifier_return id = null;



        		name = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:387:2: (id= identifier ( '[' ']' )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:387:4: id= identifier ( '[' ']' )*
            {
            pushFollow(FOLLOW_identifier_in_argument640);
            id=identifier();
            _fsp--;
            if (failed) return name;
            if ( backtracking==0 ) {
               name =input.toString(id.start,id.stop); 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:387:38: ( '[' ']' )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==LEFT_SQUARE) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:387:40: '[' ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_argument646); if (failed) return name;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_argument648); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       name += "[]";
            	    }

            	    }
            	    break;

            	default :
            	    break loop12;
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


    // $ANTLR start query
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:392:1: query returns [QueryDescr query] : QUERY queryName= name ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )? normal_lhs_block[lhs] END opt_semicolon ;
    public final QueryDescr query() throws RecognitionException {
        QueryDescr query = null;

        Token paramName=null;
        Token QUERY8=null;
        Token END9=null;
        name_return queryName = null;

        qualified_id_return paramType = null;



        		query = null;
        		AndDescr lhs = null;
        		List params = null;
        		List types = null;		
         
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:400:2: ( QUERY queryName= name ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )? normal_lhs_block[lhs] END opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:401:3: QUERY queryName= name ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )? normal_lhs_block[lhs] END opt_semicolon
            {
            QUERY8=(Token)input.LT(1);
            match(input,QUERY,FOLLOW_QUERY_in_query678); if (failed) return query;
            pushFollow(FOLLOW_name_in_query682);
            queryName=name();
            _fsp--;
            if (failed) return query;
            if ( backtracking==0 ) {
               
              			query = factory.createQuery( queryName.name ); 
              			query.setLocation( offset(QUERY8.getLine()), QUERY8.getCharPositionInLine() );
              			query.setStartCharacter( ((CommonToken)QUERY8).getStartIndex() );
              			lhs = new AndDescr(); query.setLhs( lhs ); 
              			lhs.setLocation( offset(QUERY8.getLine()), QUERY8.getCharPositionInLine() );
                                      location.setType( Location.LOCATION_RULE_HEADER );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:410:3: ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )?
            int alt17=2;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:410:5: LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_query692); if (failed) return query;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:411:11: ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==ID) ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:411:13: ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )*
                            {
                            if ( backtracking==0 ) {
                               params = new ArrayList(); types = new ArrayList();
                            }
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:413:15: ( (paramType= qualified_id )? paramName= ID )
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:413:16: (paramType= qualified_id )? paramName= ID
                            {
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:413:25: (paramType= qualified_id )?
                            int alt13=2;
                            int LA13_0 = input.LA(1);

                            if ( (LA13_0==ID) ) {
                                int LA13_1 = input.LA(2);

                                if ( ((LA13_1>=ID && LA13_1<=DOT)||LA13_1==LEFT_SQUARE) ) {
                                    alt13=1;
                                }
                            }
                            switch (alt13) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:413:25: paramType= qualified_id
                                    {
                                    pushFollow(FOLLOW_qualified_id_in_query727);
                                    paramType=qualified_id();
                                    _fsp--;
                                    if (failed) return query;

                                    }
                                    break;

                            }

                            paramName=(Token)input.LT(1);
                            match(input,ID,FOLLOW_ID_in_query732); if (failed) return query;
                            if ( backtracking==0 ) {
                               params.add( paramName.getText() ); String type = (paramType != null) ? paramType.text : "Object"; types.add( type ); 
                            }

                            }

                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:414:15: ( COMMA (paramType= qualified_id )? paramName= ID )*
                            loop15:
                            do {
                                int alt15=2;
                                int LA15_0 = input.LA(1);

                                if ( (LA15_0==COMMA) ) {
                                    alt15=1;
                                }


                                switch (alt15) {
                            	case 1 :
                            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:414:16: COMMA (paramType= qualified_id )? paramName= ID
                            	    {
                            	    match(input,COMMA,FOLLOW_COMMA_in_query753); if (failed) return query;
                            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:414:31: (paramType= qualified_id )?
                            	    int alt14=2;
                            	    int LA14_0 = input.LA(1);

                            	    if ( (LA14_0==ID) ) {
                            	        int LA14_1 = input.LA(2);

                            	        if ( ((LA14_1>=ID && LA14_1<=DOT)||LA14_1==LEFT_SQUARE) ) {
                            	            alt14=1;
                            	        }
                            	    }
                            	    switch (alt14) {
                            	        case 1 :
                            	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:414:31: paramType= qualified_id
                            	            {
                            	            pushFollow(FOLLOW_qualified_id_in_query757);
                            	            paramType=qualified_id();
                            	            _fsp--;
                            	            if (failed) return query;

                            	            }
                            	            break;

                            	    }

                            	    paramName=(Token)input.LT(1);
                            	    match(input,ID,FOLLOW_ID_in_query762); if (failed) return query;
                            	    if ( backtracking==0 ) {
                            	       params.add( paramName.getText() );  String type = (paramType != null) ? paramType.text : "Object"; types.add( type );  
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    break loop15;
                                }
                            } while (true);

                            if ( backtracking==0 ) {
                              	query.setParameters( (String[]) params.toArray( new String[params.size()] ) ); 
                              		            	query.setParameterTypes( (String[]) types.toArray( new String[types.size()] ) ); 
                              		            
                            }

                            }
                            break;

                    }

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_query812); if (failed) return query;

                    }
                    break;

            }

            if ( backtracking==0 ) {

                                      location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
              	        
            }
            pushFollow(FOLLOW_normal_lhs_block_in_query841);
            normal_lhs_block(lhs);
            _fsp--;
            if (failed) return query;
            END9=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_query846); if (failed) return query;
            pushFollow(FOLLOW_opt_semicolon_in_query848);
            opt_semicolon();
            _fsp--;
            if (failed) return query;
            if ( backtracking==0 ) {

              			query.setEndCharacter( ((CommonToken)END9).getStopIndex() );
              		
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:432:1: template returns [FactTemplateDescr template] : TEMPLATE templateName= name opt_semicolon (slot= template_slot )+ END opt_semicolon ;
    public final FactTemplateDescr template() throws RecognitionException {
        FactTemplateDescr template = null;

        Token TEMPLATE10=null;
        Token END11=null;
        name_return templateName = null;

        FieldTemplateDescr slot = null;



        		template = null;		
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:436:2: ( TEMPLATE templateName= name opt_semicolon (slot= template_slot )+ END opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:437:3: TEMPLATE templateName= name opt_semicolon (slot= template_slot )+ END opt_semicolon
            {
            TEMPLATE10=(Token)input.LT(1);
            match(input,TEMPLATE,FOLLOW_TEMPLATE_in_template876); if (failed) return template;
            pushFollow(FOLLOW_name_in_template880);
            templateName=name();
            _fsp--;
            if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template882);
            opt_semicolon();
            _fsp--;
            if (failed) return template;
            if ( backtracking==0 ) {

              			template = new FactTemplateDescr(templateName.name);
              			template.setLocation( offset(TEMPLATE10.getLine()), TEMPLATE10.getCharPositionInLine() );			
              			template.setStartCharacter( ((CommonToken)TEMPLATE10).getStartIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:443:3: (slot= template_slot )+
            int cnt18=0;
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==ID) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:444:4: slot= template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template897);
            	    slot=template_slot();
            	    _fsp--;
            	    if (failed) return template;
            	    if ( backtracking==0 ) {

            	      				template.addFieldTemplate( slot );
            	      			
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt18 >= 1 ) break loop18;
            	    if (backtracking>0) {failed=true; return template;}
                        EarlyExitException eee =
                            new EarlyExitException(18, input);
                        throw eee;
                }
                cnt18++;
            } while (true);

            END11=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_template912); if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template914);
            opt_semicolon();
            _fsp--;
            if (failed) return template;
            if ( backtracking==0 ) {

              			template.setEndCharacter( ((CommonToken)END11).getStopIndex() );
              		
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:455:1: template_slot returns [FieldTemplateDescr field] : fieldType= qualified_id id= identifier opt_semicolon ;
    public final FieldTemplateDescr template_slot() throws RecognitionException {
        FieldTemplateDescr field = null;

        qualified_id_return fieldType = null;

        identifier_return id = null;



        		field = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:459:2: (fieldType= qualified_id id= identifier opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:460:11: fieldType= qualified_id id= identifier opt_semicolon
            {
            if ( backtracking==0 ) {

              			field = factory.createFieldTemplate();
              	         
            }
            pushFollow(FOLLOW_qualified_id_in_template_slot960);
            fieldType=qualified_id();
            _fsp--;
            if (failed) return field;
            if ( backtracking==0 ) {

              		        field.setClassType( fieldType.text );
              			field.setStartCharacter( ((CommonToken)((Token)fieldType.start)).getStartIndex() );
              			field.setEndCharacter( ((CommonToken)((Token)fieldType.stop)).getStopIndex() );
              		 
            }
            pushFollow(FOLLOW_identifier_in_template_slot976);
            id=identifier();
            _fsp--;
            if (failed) return field;
            pushFollow(FOLLOW_opt_semicolon_in_template_slot978);
            opt_semicolon();
            _fsp--;
            if (failed) return field;
            if ( backtracking==0 ) {

              		        field.setName( input.toString(id.start,id.stop) );
              			field.setLocation( offset(((Token)id.start).getLine()), ((Token)id.start).getCharPositionInLine() );
              			field.setEndCharacter( ((CommonToken)((Token)id.start)).getStopIndex() );
              		 
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:478:1: rule returns [RuleDescr rule] : RULE ruleName= name ( rule_attributes[$rule] )? ( WHEN ( ':' )? normal_lhs_block[lhs] )? rhs_chunk[$rule] ;
    public final RuleDescr rule() throws RecognitionException {
        RuleDescr rule = null;

        Token RULE12=null;
        Token WHEN13=null;
        name_return ruleName = null;



        		rule = null;
        		AndDescr lhs = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:483:2: ( RULE ruleName= name ( rule_attributes[$rule] )? ( WHEN ( ':' )? normal_lhs_block[lhs] )? rhs_chunk[$rule] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:484:3: RULE ruleName= name ( rule_attributes[$rule] )? ( WHEN ( ':' )? normal_lhs_block[lhs] )? rhs_chunk[$rule]
            {
            RULE12=(Token)input.LT(1);
            match(input,RULE,FOLLOW_RULE_in_rule1009); if (failed) return rule;
            pushFollow(FOLLOW_name_in_rule1013);
            ruleName=name();
            _fsp--;
            if (failed) return rule;
            if ( backtracking==0 ) {
               
              			location.setType( Location.LOCATION_RULE_HEADER );
              			debug( "start rule: " + ruleName.name );
              			rule = new RuleDescr( ruleName.name, null ); 
              			rule.setLocation( offset(RULE12.getLine()), RULE12.getCharPositionInLine() );
              			rule.setStartCharacter( ((CommonToken)RULE12).getStartIndex() );
              			lhs = new AndDescr(); 
              			rule.setLhs( lhs ); 
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:494:3: ( rule_attributes[$rule] )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( ((LA19_0>=ATTRIBUTES && LA19_0<=DATE_EFFECTIVE)||(LA19_0>=DATE_EXPIRES && LA19_0<=ENABLED)||LA19_0==SALIENCE||(LA19_0>=NO_LOOP && LA19_0<=LOCK_ON_ACTIVE)) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:494:3: rule_attributes[$rule]
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule1022);
                    rule_attributes(rule);
                    _fsp--;
                    if (failed) return rule;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:495:3: ( WHEN ( ':' )? normal_lhs_block[lhs] )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==WHEN) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:496:4: WHEN ( ':' )? normal_lhs_block[lhs]
                    {
                    WHEN13=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_rule1034); if (failed) return rule;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:496:9: ( ':' )?
                    int alt20=2;
                    int LA20_0 = input.LA(1);

                    if ( (LA20_0==77) ) {
                        alt20=1;
                    }
                    switch (alt20) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:496:9: ':'
                            {
                            match(input,77,FOLLOW_77_in_rule1036); if (failed) return rule;

                            }
                            break;

                    }

                    if ( backtracking==0 ) {
                       
                      				this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
                      				lhs.setLocation( offset(WHEN13.getLine()), WHEN13.getCharPositionInLine() );
                      				lhs.setStartCharacter( ((CommonToken)WHEN13).getStartIndex() );
                      			
                    }
                    pushFollow(FOLLOW_normal_lhs_block_in_rule1047);
                    normal_lhs_block(lhs);
                    _fsp--;
                    if (failed) return rule;

                    }
                    break;

            }

            pushFollow(FOLLOW_rhs_chunk_in_rule1057);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:509:1: rule_attributes[RuleDescr rule] : ( ATTRIBUTES ':' )? attr= rule_attribute ( ( ',' )? attr= rule_attribute )* ;
    public final void rule_attributes(RuleDescr rule) throws RecognitionException {
        AttributeDescr attr = null;


        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:510:2: ( ( ATTRIBUTES ':' )? attr= rule_attribute ( ( ',' )? attr= rule_attribute )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:511:2: ( ATTRIBUTES ':' )? attr= rule_attribute ( ( ',' )? attr= rule_attribute )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:511:2: ( ATTRIBUTES ':' )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==ATTRIBUTES) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:511:4: ATTRIBUTES ':'
                    {
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_rule_attributes1077); if (failed) return ;
                    match(input,77,FOLLOW_77_in_rule_attributes1079); if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_rule_attribute_in_rule_attributes1087);
            attr=rule_attribute();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               rule.addAttribute( attr ); 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:513:2: ( ( ',' )? attr= rule_attribute )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==COMMA||LA24_0==DATE_EFFECTIVE||(LA24_0>=DATE_EXPIRES && LA24_0<=ENABLED)||LA24_0==SALIENCE||(LA24_0>=NO_LOOP && LA24_0<=LOCK_ON_ACTIVE)) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:513:4: ( ',' )? attr= rule_attribute
            	    {
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:513:4: ( ',' )?
            	    int alt23=2;
            	    int LA23_0 = input.LA(1);

            	    if ( (LA23_0==COMMA) ) {
            	        alt23=1;
            	    }
            	    switch (alt23) {
            	        case 1 :
            	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:513:4: ','
            	            {
            	            match(input,COMMA,FOLLOW_COMMA_in_rule_attributes1094); if (failed) return ;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes1099);
            	    attr=rule_attribute();
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       rule.addAttribute( attr ); 
            	    }

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
    // $ANTLR end rule_attributes


    // $ANTLR start rule_attribute
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:518:1: rule_attribute returns [AttributeDescr attr] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect );
    public final AttributeDescr rule_attribute() throws RecognitionException {
        AttributeDescr attr = null;

        AttributeDescr a = null;



        		attr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:525:2: (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect )
            int alt25=12;
            switch ( input.LA(1) ) {
            case SALIENCE:
                {
                alt25=1;
                }
                break;
            case NO_LOOP:
                {
                alt25=2;
                }
                break;
            case AGENDA_GROUP:
                {
                alt25=3;
                }
                break;
            case DURATION:
                {
                alt25=4;
                }
                break;
            case ACTIVATION_GROUP:
                {
                alt25=5;
                }
                break;
            case AUTO_FOCUS:
                {
                alt25=6;
                }
                break;
            case DATE_EFFECTIVE:
                {
                alt25=7;
                }
                break;
            case DATE_EXPIRES:
                {
                alt25=8;
                }
                break;
            case ENABLED:
                {
                alt25=9;
                }
                break;
            case RULEFLOW_GROUP:
                {
                alt25=10;
                }
                break;
            case LOCK_ON_ACTIVE:
                {
                alt25=11;
                }
                break;
            case DIALECT:
                {
                alt25=12;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return attr;}
                NoViableAltException nvae =
                    new NoViableAltException("518:1: rule_attribute returns [AttributeDescr attr] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect );", 25, 0, input);

                throw nvae;
            }

            switch (alt25) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:525:4: a= salience
                    {
                    pushFollow(FOLLOW_salience_in_rule_attribute1136);
                    a=salience();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:526:4: a= no_loop
                    {
                    pushFollow(FOLLOW_no_loop_in_rule_attribute1144);
                    a=no_loop();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:527:4: a= agenda_group
                    {
                    pushFollow(FOLLOW_agenda_group_in_rule_attribute1153);
                    a=agenda_group();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:528:4: a= duration
                    {
                    pushFollow(FOLLOW_duration_in_rule_attribute1162);
                    a=duration();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:529:4: a= activation_group
                    {
                    pushFollow(FOLLOW_activation_group_in_rule_attribute1171);
                    a=activation_group();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:530:4: a= auto_focus
                    {
                    pushFollow(FOLLOW_auto_focus_in_rule_attribute1179);
                    a=auto_focus();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:531:4: a= date_effective
                    {
                    pushFollow(FOLLOW_date_effective_in_rule_attribute1187);
                    a=date_effective();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:532:4: a= date_expires
                    {
                    pushFollow(FOLLOW_date_expires_in_rule_attribute1195);
                    a=date_expires();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:533:4: a= enabled
                    {
                    pushFollow(FOLLOW_enabled_in_rule_attribute1203);
                    a=enabled();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:534:4: a= ruleflow_group
                    {
                    pushFollow(FOLLOW_ruleflow_group_in_rule_attribute1211);
                    a=ruleflow_group();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:535:4: a= lock_on_active
                    {
                    pushFollow(FOLLOW_lock_on_active_in_rule_attribute1219);
                    a=lock_on_active();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:536:4: a= dialect
                    {
                    pushFollow(FOLLOW_dialect_in_rule_attribute1226);
                    a=dialect();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;

            }
            if ( backtracking==0 ) {

              		attr = a;
              	
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return attr;
    }
    // $ANTLR end rule_attribute


    // $ANTLR start date_effective
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:539:1: date_effective returns [AttributeDescr descr] : DATE_EFFECTIVE STRING ;
    public final AttributeDescr date_effective() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING14=null;
        Token DATE_EFFECTIVE15=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:543:2: ( DATE_EFFECTIVE STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:544:3: DATE_EFFECTIVE STRING
            {
            DATE_EFFECTIVE15=(Token)input.LT(1);
            match(input,DATE_EFFECTIVE,FOLLOW_DATE_EFFECTIVE_in_date_effective1252); if (failed) return descr;
            STRING14=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_effective1254); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "date-effective", getString( STRING14.getText() ) );
              			descr.setLocation( offset( DATE_EFFECTIVE15.getLine() ), DATE_EFFECTIVE15.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)DATE_EFFECTIVE15).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)STRING14).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return descr;
    }
    // $ANTLR end date_effective


    // $ANTLR start date_expires
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:554:1: date_expires returns [AttributeDescr descr] : DATE_EXPIRES STRING ;
    public final AttributeDescr date_expires() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING16=null;
        Token DATE_EXPIRES17=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:558:2: ( DATE_EXPIRES STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:558:4: DATE_EXPIRES STRING
            {
            DATE_EXPIRES17=(Token)input.LT(1);
            match(input,DATE_EXPIRES,FOLLOW_DATE_EXPIRES_in_date_expires1283); if (failed) return descr;
            STRING16=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_expires1285); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "date-expires", getString( STRING16.getText() ) );
              			descr.setLocation( offset(DATE_EXPIRES17.getLine()), DATE_EXPIRES17.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)DATE_EXPIRES17).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)STRING16).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return descr;
    }
    // $ANTLR end date_expires


    // $ANTLR start enabled
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:567:1: enabled returns [AttributeDescr descr] : ENABLED BOOL ;
    public final AttributeDescr enabled() throws RecognitionException {
        AttributeDescr descr = null;

        Token BOOL18=null;
        Token ENABLED19=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:571:2: ( ENABLED BOOL )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:571:5: ENABLED BOOL
            {
            ENABLED19=(Token)input.LT(1);
            match(input,ENABLED,FOLLOW_ENABLED_in_enabled1314); if (failed) return descr;
            BOOL18=(Token)input.LT(1);
            match(input,BOOL,FOLLOW_BOOL_in_enabled1316); if (failed) return descr;
            if ( backtracking==0 ) {

              				descr = new AttributeDescr( "enabled", BOOL18.getText() );
              				descr.setLocation( offset(ENABLED19.getLine()), ENABLED19.getCharPositionInLine() );
              				descr.setStartCharacter( ((CommonToken)ENABLED19).getStartIndex() );
              				descr.setEndCharacter( ((CommonToken)BOOL18).getStopIndex() );
              			
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return descr;
    }
    // $ANTLR end enabled


    // $ANTLR start salience
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:580:1: salience returns [AttributeDescr descr] : SALIENCE ( INT | txt= paren_chunk ) ;
    public final AttributeDescr salience() throws RecognitionException {
        AttributeDescr descr = null;

        Token SALIENCE20=null;
        Token INT21=null;
        paren_chunk_return txt = null;



        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:584:2: ( SALIENCE ( INT | txt= paren_chunk ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:585:3: SALIENCE ( INT | txt= paren_chunk )
            {
            SALIENCE20=(Token)input.LT(1);
            match(input,SALIENCE,FOLLOW_SALIENCE_in_salience1349); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "salience" );
              			descr.setLocation( offset(SALIENCE20.getLine()), SALIENCE20.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)SALIENCE20).getStartIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:591:3: ( INT | txt= paren_chunk )
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==INT) ) {
                alt26=1;
            }
            else if ( (LA26_0==LEFT_PAREN) ) {
                alt26=2;
            }
            else {
                if (backtracking>0) {failed=true; return descr;}
                NoViableAltException nvae =
                    new NoViableAltException("591:3: ( INT | txt= paren_chunk )", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:591:5: INT
                    {
                    INT21=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_salience1360); if (failed) return descr;
                    if ( backtracking==0 ) {

                      			descr.setValue( INT21.getText() );
                      			descr.setEndCharacter( ((CommonToken)INT21).getStopIndex() );
                      		
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:596:5: txt= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_salience1375);
                    txt=paren_chunk();
                    _fsp--;
                    if (failed) return descr;
                    if ( backtracking==0 ) {

                      			descr.setValue( input.toString(txt.start,txt.stop) );
                      			descr.setEndCharacter( ((CommonToken)((Token)txt.stop)).getStopIndex() );
                      		
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
        return descr;
    }
    // $ANTLR end salience


    // $ANTLR start no_loop
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:604:1: no_loop returns [AttributeDescr descr] : NO_LOOP ( BOOL )? ;
    public final AttributeDescr no_loop() throws RecognitionException {
        AttributeDescr descr = null;

        Token NO_LOOP22=null;
        Token BOOL23=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:608:2: ( NO_LOOP ( BOOL )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:608:4: NO_LOOP ( BOOL )?
            {
            NO_LOOP22=(Token)input.LT(1);
            match(input,NO_LOOP,FOLLOW_NO_LOOP_in_no_loop1405); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "no-loop", "true" );
              			descr.setLocation( offset(NO_LOOP22.getLine()), NO_LOOP22.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)NO_LOOP22).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)NO_LOOP22).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:615:3: ( BOOL )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==BOOL) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:615:5: BOOL
                    {
                    BOOL23=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1418); if (failed) return descr;
                    if ( backtracking==0 ) {

                      				descr.setValue( BOOL23.getText() );
                      				descr.setEndCharacter( ((CommonToken)BOOL23).getStopIndex() );
                      			
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
        return descr;
    }
    // $ANTLR end no_loop


    // $ANTLR start auto_focus
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:623:1: auto_focus returns [AttributeDescr descr] : AUTO_FOCUS ( BOOL )? ;
    public final AttributeDescr auto_focus() throws RecognitionException {
        AttributeDescr descr = null;

        Token AUTO_FOCUS24=null;
        Token BOOL25=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:627:2: ( AUTO_FOCUS ( BOOL )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:627:4: AUTO_FOCUS ( BOOL )?
            {
            AUTO_FOCUS24=(Token)input.LT(1);
            match(input,AUTO_FOCUS,FOLLOW_AUTO_FOCUS_in_auto_focus1453); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "auto-focus", "true" );
              			descr.setLocation( offset(AUTO_FOCUS24.getLine()), AUTO_FOCUS24.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)AUTO_FOCUS24).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)AUTO_FOCUS24).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:634:3: ( BOOL )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==BOOL) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:634:5: BOOL
                    {
                    BOOL25=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1466); if (failed) return descr;
                    if ( backtracking==0 ) {

                      				descr.setValue( BOOL25.getText() );
                      				descr.setEndCharacter( ((CommonToken)BOOL25).getStopIndex() );
                      			
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
        return descr;
    }
    // $ANTLR end auto_focus


    // $ANTLR start activation_group
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:642:1: activation_group returns [AttributeDescr descr] : ACTIVATION_GROUP STRING ;
    public final AttributeDescr activation_group() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING26=null;
        Token ACTIVATION_GROUP27=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:646:2: ( ACTIVATION_GROUP STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:646:4: ACTIVATION_GROUP STRING
            {
            ACTIVATION_GROUP27=(Token)input.LT(1);
            match(input,ACTIVATION_GROUP,FOLLOW_ACTIVATION_GROUP_in_activation_group1502); if (failed) return descr;
            STRING26=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1504); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "activation-group", getString( STRING26.getText() ) );
              			descr.setLocation( offset(ACTIVATION_GROUP27.getLine()), ACTIVATION_GROUP27.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)ACTIVATION_GROUP27).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)STRING26).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return descr;
    }
    // $ANTLR end activation_group


    // $ANTLR start ruleflow_group
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:655:1: ruleflow_group returns [AttributeDescr descr] : RULEFLOW_GROUP STRING ;
    public final AttributeDescr ruleflow_group() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING28=null;
        Token RULEFLOW_GROUP29=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:659:2: ( RULEFLOW_GROUP STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:659:4: RULEFLOW_GROUP STRING
            {
            RULEFLOW_GROUP29=(Token)input.LT(1);
            match(input,RULEFLOW_GROUP,FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1532); if (failed) return descr;
            STRING28=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_ruleflow_group1534); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "ruleflow-group", getString( STRING28.getText() ) );
              			descr.setLocation( offset(RULEFLOW_GROUP29.getLine()), RULEFLOW_GROUP29.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)RULEFLOW_GROUP29).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)STRING28).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return descr;
    }
    // $ANTLR end ruleflow_group


    // $ANTLR start agenda_group
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:668:1: agenda_group returns [AttributeDescr descr] : AGENDA_GROUP STRING ;
    public final AttributeDescr agenda_group() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING30=null;
        Token AGENDA_GROUP31=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:672:2: ( AGENDA_GROUP STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:672:4: AGENDA_GROUP STRING
            {
            AGENDA_GROUP31=(Token)input.LT(1);
            match(input,AGENDA_GROUP,FOLLOW_AGENDA_GROUP_in_agenda_group1562); if (failed) return descr;
            STRING30=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1564); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "agenda-group", getString( STRING30.getText() ) );
              			descr.setLocation( offset(AGENDA_GROUP31.getLine()), AGENDA_GROUP31.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)AGENDA_GROUP31).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)STRING30).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return descr;
    }
    // $ANTLR end agenda_group


    // $ANTLR start duration
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:681:1: duration returns [AttributeDescr descr] : DURATION INT ;
    public final AttributeDescr duration() throws RecognitionException {
        AttributeDescr descr = null;

        Token INT32=null;
        Token DURATION33=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:685:2: ( DURATION INT )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:685:4: DURATION INT
            {
            DURATION33=(Token)input.LT(1);
            match(input,DURATION,FOLLOW_DURATION_in_duration1592); if (failed) return descr;
            INT32=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1594); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "duration", INT32.getText() );
              			descr.setLocation( offset(DURATION33.getLine()), DURATION33.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)DURATION33).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)INT32).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return descr;
    }
    // $ANTLR end duration


    // $ANTLR start dialect
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:694:1: dialect returns [AttributeDescr descr] : DIALECT STRING ;
    public final AttributeDescr dialect() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING34=null;
        Token DIALECT35=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:698:2: ( DIALECT STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:698:4: DIALECT STRING
            {
            DIALECT35=(Token)input.LT(1);
            match(input,DIALECT,FOLLOW_DIALECT_in_dialect1622); if (failed) return descr;
            STRING34=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_dialect1624); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "dialect", getString( STRING34.getText() ) );
              			descr.setLocation( offset(DIALECT35.getLine()), DIALECT35.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)DIALECT35).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)STRING34).getStopIndex() );
              		
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return descr;
    }
    // $ANTLR end dialect


    // $ANTLR start lock_on_active
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:707:1: lock_on_active returns [AttributeDescr descr] : LOCK_ON_ACTIVE ( BOOL )? ;
    public final AttributeDescr lock_on_active() throws RecognitionException {
        AttributeDescr descr = null;

        Token LOCK_ON_ACTIVE36=null;
        Token BOOL37=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:711:2: ( LOCK_ON_ACTIVE ( BOOL )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:711:4: LOCK_ON_ACTIVE ( BOOL )?
            {
            LOCK_ON_ACTIVE36=(Token)input.LT(1);
            match(input,LOCK_ON_ACTIVE,FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1656); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "lock-on-active", "true" );
              			descr.setLocation( offset(LOCK_ON_ACTIVE36.getLine()), LOCK_ON_ACTIVE36.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)LOCK_ON_ACTIVE36).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)LOCK_ON_ACTIVE36).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:718:3: ( BOOL )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==BOOL) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:718:5: BOOL
                    {
                    BOOL37=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_lock_on_active1669); if (failed) return descr;
                    if ( backtracking==0 ) {

                      				descr.setValue( BOOL37.getText() );
                      				descr.setEndCharacter( ((CommonToken)BOOL37).getStopIndex() );
                      			
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
        return descr;
    }
    // $ANTLR end lock_on_active


    // $ANTLR start normal_lhs_block
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:726:1: normal_lhs_block[AndDescr descr] : (d= lhs[$descr] )* ;
    public final void normal_lhs_block(AndDescr descr) throws RecognitionException {
        BaseDescr d = null;



        		location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:730:2: ( (d= lhs[$descr] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:3: (d= lhs[$descr] )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:3: (d= lhs[$descr] )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==ID||LA30_0==LEFT_PAREN||(LA30_0>=EXISTS && LA30_0<=FORALL)) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:5: d= lhs[$descr]
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1708);
            	    d=lhs(descr);
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       if( d != null) descr.addDescr( d ); 
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
        return ;
    }
    // $ANTLR end normal_lhs_block


    // $ANTLR start lhs
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:737:1: lhs[ConditionalElementDescr ce] returns [BaseDescr d] : l= lhs_or ;
    public final BaseDescr lhs(ConditionalElementDescr ce) throws RecognitionException {
        BaseDescr d = null;

        BaseDescr l = null;



        		d =null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:741:2: (l= lhs_or )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:741:4: l= lhs_or
            {
            pushFollow(FOLLOW_lhs_or_in_lhs1745);
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


    // $ANTLR start lhs_or
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:1: lhs_or returns [BaseDescr d] : ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* );
    public final BaseDescr lhs_or() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr lhsand = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d = null;
        		OrDescr or = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:750:2: ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* )
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==LEFT_PAREN) ) {
                int LA33_1 = input.LA(2);

                if ( (LA33_1==ID||LA33_1==LEFT_PAREN||LA33_1==AND||(LA33_1>=EXISTS && LA33_1<=FORALL)) ) {
                    alt33=2;
                }
                else if ( (LA33_1==OR) ) {
                    alt33=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("745:1: lhs_or returns [BaseDescr d] : ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* );", 33, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA33_0==ID||(LA33_0>=EXISTS && LA33_0<=FORALL)) ) {
                alt33=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("745:1: lhs_or returns [BaseDescr d] : ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* );", 33, 0, input);

                throw nvae;
            }
            switch (alt33) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:750:4: LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_or1770); if (failed) return d;
                    match(input,OR,FOLLOW_OR_in_lhs_or1772); if (failed) return d;
                    if ( backtracking==0 ) {

                      			or = new OrDescr();
                      			d = or;
                      			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                      		
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:756:3: (lhsand= lhs_and )+
                    int cnt31=0;
                    loop31:
                    do {
                        int alt31=2;
                        int LA31_0 = input.LA(1);

                        if ( (LA31_0==ID||LA31_0==LEFT_PAREN||(LA31_0>=EXISTS && LA31_0<=FORALL)) ) {
                            alt31=1;
                        }


                        switch (alt31) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:756:5: lhsand= lhs_and
                    	    {
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or1785);
                    	    lhsand=lhs_and();
                    	    _fsp--;
                    	    if (failed) return d;
                    	    if ( backtracking==0 ) {

                    	      			or.addDescr( lhsand );
                    	      		
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt31 >= 1 ) break loop31;
                    	    if (backtracking>0) {failed=true; return d;}
                                EarlyExitException eee =
                                    new EarlyExitException(31, input);
                                throw eee;
                        }
                        cnt31++;
                    } while (true);

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_or1796); if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:762:10: left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )*
                    {
                    pushFollow(FOLLOW_lhs_and_in_lhs_or1814);
                    left=lhs_and();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = left; 
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:763:3: ( ( OR | DOUBLE_PIPE ) right= lhs_and )*
                    loop32:
                    do {
                        int alt32=2;
                        int LA32_0 = input.LA(1);

                        if ( ((LA32_0>=OR && LA32_0<=DOUBLE_PIPE)) ) {
                            alt32=1;
                        }


                        switch (alt32) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:763:5: ( OR | DOUBLE_PIPE ) right= lhs_and
                    	    {
                    	    if ( (input.LA(1)>=OR && input.LA(1)<=DOUBLE_PIPE) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return d;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or1822);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {

                    	      				location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                    	      			
                    	    }
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or1838);
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
                    	    break loop32;
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
        return d;
    }
    // $ANTLR end lhs_or


    // $ANTLR start lhs_and
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:780:1: lhs_and returns [BaseDescr d] : ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* );
    public final BaseDescr lhs_and() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr lhsunary = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d = null;
        		AndDescr and = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:785:2: ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* )
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==LEFT_PAREN) ) {
                int LA36_1 = input.LA(2);

                if ( (LA36_1==AND) ) {
                    alt36=1;
                }
                else if ( (LA36_1==ID||LA36_1==LEFT_PAREN||(LA36_1>=EXISTS && LA36_1<=FORALL)) ) {
                    alt36=2;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("780:1: lhs_and returns [BaseDescr d] : ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* );", 36, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA36_0==ID||(LA36_0>=EXISTS && LA36_0<=FORALL)) ) {
                alt36=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("780:1: lhs_and returns [BaseDescr d] : ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* );", 36, 0, input);

                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:785:4: LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_and1869); if (failed) return d;
                    match(input,AND,FOLLOW_AND_in_lhs_and1871); if (failed) return d;
                    if ( backtracking==0 ) {

                      			and = new AndDescr();
                      			d = and;
                      			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                      		
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:791:3: (lhsunary= lhs_unary )+
                    int cnt34=0;
                    loop34:
                    do {
                        int alt34=2;
                        int LA34_0 = input.LA(1);

                        if ( (LA34_0==ID||LA34_0==LEFT_PAREN||(LA34_0>=EXISTS && LA34_0<=FORALL)) ) {
                            alt34=1;
                        }


                        switch (alt34) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:791:4: lhsunary= lhs_unary
                    	    {
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and1883);
                    	    lhsunary=lhs_unary();
                    	    _fsp--;
                    	    if (failed) return d;
                    	    if ( backtracking==0 ) {

                    	      			and.addDescr( lhsunary );
                    	      		
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt34 >= 1 ) break loop34;
                    	    if (backtracking>0) {failed=true; return d;}
                                EarlyExitException eee =
                                    new EarlyExitException(34, input);
                                throw eee;
                        }
                        cnt34++;
                    } while (true);

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_and1893); if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:797:10: left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )*
                    {
                    pushFollow(FOLLOW_lhs_unary_in_lhs_and1911);
                    left=lhs_unary();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = left; 
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:798:3: ( ( AND | DOUBLE_AMPER ) right= lhs_unary )*
                    loop35:
                    do {
                        int alt35=2;
                        int LA35_0 = input.LA(1);

                        if ( ((LA35_0>=AND && LA35_0<=DOUBLE_AMPER)) ) {
                            alt35=1;
                        }


                        switch (alt35) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:798:5: ( AND | DOUBLE_AMPER ) right= lhs_unary
                    	    {
                    	    if ( (input.LA(1)>=AND && input.LA(1)<=DOUBLE_AMPER) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return d;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and1919);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {

                    	      				location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                    	      			
                    	    }
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and1935);
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
        return d;
    }
    // $ANTLR end lhs_and


    // $ANTLR start lhs_unary
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:815:1: lhs_unary returns [BaseDescr d] : ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source ) opt_semicolon ;
    public final BaseDescr lhs_unary() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr u = null;

        BaseDescr ps = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:819:2: ( ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source ) opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:819:4: ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source ) opt_semicolon
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:819:4: ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source )
            int alt37=6;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==EXISTS) && (synpred1())) {
                alt37=1;
            }
            else if ( (LA37_0==NOT) && (synpred2())) {
                alt37=2;
            }
            else if ( (LA37_0==EVAL) && (synpred3())) {
                alt37=3;
            }
            else if ( (LA37_0==FORALL) && (synpred4())) {
                alt37=4;
            }
            else if ( (LA37_0==LEFT_PAREN) && (synpred5())) {
                alt37=5;
            }
            else if ( (LA37_0==ID) ) {
                alt37=6;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("819:4: ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source )", 37, 0, input);

                throw nvae;
            }
            switch (alt37) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:819:6: ( EXISTS )=>u= lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary1980);
                    u=lhs_exist();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:820:5: ( NOT )=>u= lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary1998);
                    u=lhs_not();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:821:5: ( EVAL )=>u= lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary2017);
                    u=lhs_eval();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:822:5: ( FORALL )=>u= lhs_forall
                    {
                    pushFollow(FOLLOW_lhs_forall_in_lhs_unary2036);
                    u=lhs_forall();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:823:5: ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary2053); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_unary2057);
                    u=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary2059); if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:824:5: ps= pattern_source
                    {
                    pushFollow(FOLLOW_pattern_source_in_lhs_unary2070);
                    ps=pattern_source();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = (BaseDescr) ps; 
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_opt_semicolon_in_lhs_unary2082);
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


    // $ANTLR start pattern_source
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:829:1: pattern_source returns [BaseDescr d] : u= lhs_pattern ( ( ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement ) | FROM ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )? ;
    public final BaseDescr pattern_source() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr u = null;

        EntryPointDescr ep = null;

        AccumulateDescr ac = null;

        CollectDescr cs = null;

        FromDescr fm = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:833:2: (u= lhs_pattern ( ( ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement ) | FROM ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:834:3: u= lhs_pattern ( ( ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement ) | FROM ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )?
            {
            pushFollow(FOLLOW_lhs_pattern_in_pattern_source2109);
            u=lhs_pattern();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               d = u; 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:835:3: ( ( ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement ) | FROM ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )?
            int alt39=3;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==FROM) ) {
                int LA39_1 = input.LA(2);

                if ( ((LA39_1>=PACKAGE && LA39_1<=ID)||LA39_1==GLOBAL||(LA39_1>=QUERY && LA39_1<=ATTRIBUTES)||LA39_1==ENABLED||LA39_1==SALIENCE||(LA39_1>=DURATION && LA39_1<=DIALECT)||LA39_1==FROM||(LA39_1>=ACCUMULATE && LA39_1<=COLLECT)||(LA39_1>=CONTAINS && LA39_1<=MEMBEROF)||LA39_1==IN||LA39_1==THEN) ) {
                    alt39=2;
                }
                else if ( (LA39_1==ENTRY_POINT) && (synpred6())) {
                    alt39=1;
                }
            }
            switch (alt39) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:838:11: ( ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:838:11: ( ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:838:13: ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement
                    {
                    match(input,FROM,FOLLOW_FROM_in_pattern_source2160); if (failed) return d;
                    pushFollow(FOLLOW_entrypoint_statement_in_pattern_source2164);
                    ep=entrypoint_statement();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if( d != null ) ((PatternDescr)d).setSource((PatternSourceDescr) ep); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:840:4: FROM ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) )
                    {
                    match(input,FROM,FOLLOW_FROM_in_pattern_source2184); if (failed) return d;
                    if ( backtracking==0 ) {

                      				location.setType(Location.LOCATION_LHS_FROM);
                      				location.setProperty(Location.LOCATION_FROM_CONTENT, "");
                      		        
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:845:11: ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) )
                    int alt38=3;
                    switch ( input.LA(1) ) {
                    case ACCUMULATE:
                        {
                        alt38=1;
                        }
                        break;
                    case COLLECT:
                        {
                        alt38=2;
                        }
                        break;
                    case PACKAGE:
                    case IMPORT:
                    case FUNCTION:
                    case EVENT:
                    case ID:
                    case GLOBAL:
                    case QUERY:
                    case END:
                    case TEMPLATE:
                    case RULE:
                    case WHEN:
                    case ATTRIBUTES:
                    case ENABLED:
                    case SALIENCE:
                    case DURATION:
                    case DIALECT:
                    case FROM:
                    case INIT:
                    case ACTION:
                    case REVERSE:
                    case RESULT:
                    case CONTAINS:
                    case EXCLUDES:
                    case MATCHES:
                    case SOUNDSLIKE:
                    case MEMBEROF:
                    case IN:
                    case THEN:
                        {
                        alt38=3;
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return d;}
                        NoViableAltException nvae =
                            new NoViableAltException("845:11: ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) )", 38, 0, input);

                        throw nvae;
                    }

                    switch (alt38) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:846:13: (ac= accumulate_statement )
                            {
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:846:13: (ac= accumulate_statement )
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:846:15: ac= accumulate_statement
                            {
                            pushFollow(FOLLOW_accumulate_statement_in_pattern_source2242);
                            ac=accumulate_statement();
                            _fsp--;
                            if (failed) return d;
                            if ( backtracking==0 ) {
                               if( d != null ) ((PatternDescr)d).setSource((PatternSourceDescr) ac); 
                            }

                            }


                            }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:847:15: (cs= collect_statement )
                            {
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:847:15: (cs= collect_statement )
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:847:17: cs= collect_statement
                            {
                            pushFollow(FOLLOW_collect_statement_in_pattern_source2265);
                            cs=collect_statement();
                            _fsp--;
                            if (failed) return d;
                            if ( backtracking==0 ) {
                               if( d != null ) ((PatternDescr)d).setSource((PatternSourceDescr) cs); 
                            }

                            }


                            }
                            break;
                        case 3 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:849:15: (fm= from_statement )
                            {
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:849:15: (fm= from_statement )
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:849:17: fm= from_statement
                            {
                            pushFollow(FOLLOW_from_statement_in_pattern_source2302);
                            fm=from_statement();
                            _fsp--;
                            if (failed) return d;
                            if ( backtracking==0 ) {
                               if( d != null ) ((PatternDescr)d).setSource((PatternSourceDescr) fm); 
                            }

                            }


                            }
                            break;

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
    // $ANTLR end pattern_source


    // $ANTLR start lhs_exist
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:854:1: lhs_exist returns [BaseDescr d] : EXISTS ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) ;
    public final BaseDescr lhs_exist() throws RecognitionException {
        BaseDescr d = null;

        Token EXISTS38=null;
        Token RIGHT_PAREN39=null;
        BaseDescr or = null;

        BaseDescr pattern = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:858:2: ( EXISTS ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:858:4: EXISTS ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
            {
            EXISTS38=(Token)input.LT(1);
            match(input,EXISTS,FOLLOW_EXISTS_in_lhs_exist2345); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new ExistsDescr( ); 
              			d.setLocation( offset(EXISTS38.getLine()), EXISTS38.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)EXISTS38).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:865:10: ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==LEFT_PAREN) ) {
                alt40=1;
            }
            else if ( (LA40_0==ID) ) {
                alt40=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("865:10: ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )", 40, 0, input);

                throw nvae;
            }
            switch (alt40) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:865:12: ( LEFT_PAREN or= lhs_or RIGHT_PAREN )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:865:12: ( LEFT_PAREN or= lhs_or RIGHT_PAREN )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:865:14: LEFT_PAREN or= lhs_or RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist2365); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist2369);
                    or=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( or != null ) ((ExistsDescr)d).addDescr( or ); 
                    }
                    RIGHT_PAREN39=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist2399); if (failed) return d;
                    if ( backtracking==0 ) {
                       d.setEndCharacter( ((CommonToken)RIGHT_PAREN39).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:870:12: pattern= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_exist2449);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:880:1: lhs_not returns [NotDescr d] : NOT ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) ;
    public final NotDescr lhs_not() throws RecognitionException {
        NotDescr d = null;

        Token NOT40=null;
        Token RIGHT_PAREN41=null;
        BaseDescr or = null;

        BaseDescr pattern = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:884:2: ( NOT ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:884:4: NOT ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
            {
            NOT40=(Token)input.LT(1);
            match(input,NOT,FOLLOW_NOT_in_lhs_not2501); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new NotDescr( ); 
              			d.setLocation( offset(NOT40.getLine()), NOT40.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)NOT40).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_NOT );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:891:3: ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==LEFT_PAREN) ) {
                alt41=1;
            }
            else if ( (LA41_0==ID) ) {
                alt41=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("891:3: ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:891:5: ( LEFT_PAREN or= lhs_or RIGHT_PAREN )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:891:5: ( LEFT_PAREN or= lhs_or RIGHT_PAREN )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:891:7: LEFT_PAREN or= lhs_or RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not2514); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_not2518);
                    or=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( or != null ) d.addDescr( or ); 
                    }
                    RIGHT_PAREN41=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not2549); if (failed) return d;
                    if ( backtracking==0 ) {
                       d.setEndCharacter( ((CommonToken)RIGHT_PAREN41).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:897:3: pattern= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_not2586);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:907:1: lhs_eval returns [BaseDescr d] : EVAL c= paren_chunk ;
    public final BaseDescr lhs_eval() throws RecognitionException {
        BaseDescr d = null;

        Token EVAL42=null;
        paren_chunk_return c = null;



        		d = new EvalDescr( );
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:911:2: ( EVAL c= paren_chunk )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:912:3: EVAL c= paren_chunk
            {
            EVAL42=(Token)input.LT(1);
            match(input,EVAL,FOLLOW_EVAL_in_lhs_eval2632); if (failed) return d;
            if ( backtracking==0 ) {

              			location.setType( Location.LOCATION_LHS_INSIDE_EVAL );
              		
            }
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval2643);
            c=paren_chunk();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setStartCharacter( ((CommonToken)EVAL42).getStartIndex() );
              		        if( input.toString(c.start,c.stop) != null ) {
              	  		    this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
              		            String body = safeSubstring( input.toString(c.start,c.stop), 1, input.toString(c.start,c.stop).length()-1 );
              			    checkTrailingSemicolon( body, offset(EVAL42.getLine()) );
              			    ((EvalDescr) d).setContent( body );
              			    location.setProperty(Location.LOCATION_EVAL_CONTENT, body);
              			}
              			if( ((Token)c.stop) != null ) {
              			    d.setEndCharacter( ((CommonToken)((Token)c.stop)).getStopIndex() );
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:932:1: lhs_forall returns [ForallDescr d] : FORALL LEFT_PAREN base= lhs_pattern (pattern= lhs_pattern )* RIGHT_PAREN ;
    public final ForallDescr lhs_forall() throws RecognitionException {
        ForallDescr d = null;

        Token FORALL43=null;
        Token RIGHT_PAREN44=null;
        BaseDescr base = null;

        BaseDescr pattern = null;



        		d = factory.createForall();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:936:2: ( FORALL LEFT_PAREN base= lhs_pattern (pattern= lhs_pattern )* RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:936:4: FORALL LEFT_PAREN base= lhs_pattern (pattern= lhs_pattern )* RIGHT_PAREN
            {
            FORALL43=(Token)input.LT(1);
            match(input,FORALL,FOLLOW_FORALL_in_lhs_forall2669); if (failed) return d;
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_forall2671); if (failed) return d;
            pushFollow(FOLLOW_lhs_pattern_in_lhs_forall2675);
            base=lhs_pattern();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {

              			d.setStartCharacter( ((CommonToken)FORALL43).getStartIndex() );
              		        // adding the base pattern
              		        d.addDescr( base );
              			d.setLocation( offset(FORALL43.getLine()), FORALL43.getCharPositionInLine() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:943:3: (pattern= lhs_pattern )*
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);

                if ( (LA42_0==ID) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:943:5: pattern= lhs_pattern
            	    {
            	    pushFollow(FOLLOW_lhs_pattern_in_lhs_forall2690);
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
            	    break loop42;
                }
            } while (true);

            RIGHT_PAREN44=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_forall2706); if (failed) return d;
            if ( backtracking==0 ) {

              		        d.setEndCharacter( ((CommonToken)RIGHT_PAREN44).getStopIndex() );
              		
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


    // $ANTLR start lhs_pattern
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:955:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact[null] );
    public final BaseDescr lhs_pattern() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr f = null;



        		d =null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:962:2: (f= fact_binding | f= fact[null] )
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==ID) ) {
                int LA43_1 = input.LA(2);

                if ( (LA43_1==77) ) {
                    alt43=1;
                }
                else if ( (LA43_1==DOT||LA43_1==LEFT_PAREN||LA43_1==LEFT_SQUARE) ) {
                    alt43=2;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("955:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact[null] );", 43, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("955:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact[null] );", 43, 0, input);

                throw nvae;
            }
            switch (alt43) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:962:4: f= fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_pattern2739);
                    f=fact_binding();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:963:4: f= fact[null]
                    {
                    pushFollow(FOLLOW_fact_in_lhs_pattern2747);
                    f=fact(null);
                    _fsp--;
                    if (failed) return d;

                    }
                    break;

            }
            if ( backtracking==0 ) {

              		d =f;
              	
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:966:1: from_statement returns [FromDescr d] : ds= from_source[$d] ;
    public final FromDescr from_statement() throws RecognitionException {
        FromDescr d = null;

        DeclarativeInvokerDescr ds = null;



        		d =factory.createFrom();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:970:2: (ds= from_source[$d] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:971:2: ds= from_source[$d]
            {
            pushFollow(FOLLOW_from_source_in_from_statement2774);
            ds=from_source(d);
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {

              		d.setDataSource( ds );
              	
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


    // $ANTLR start accumulate_statement
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:978:1: accumulate_statement returns [AccumulateDescr d] : ACCUMULATE LEFT_PAREN inputCE= lhs_or ( COMMA )? ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) ) RIGHT_PAREN ;
    public final AccumulateDescr accumulate_statement() throws RecognitionException {
        AccumulateDescr d = null;

        Token id=null;
        Token ACCUMULATE45=null;
        Token RIGHT_PAREN46=null;
        BaseDescr inputCE = null;

        paren_chunk_return text = null;



        		d = factory.createAccumulate();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:982:2: ( ACCUMULATE LEFT_PAREN inputCE= lhs_or ( COMMA )? ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) ) RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:983:10: ACCUMULATE LEFT_PAREN inputCE= lhs_or ( COMMA )? ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) ) RIGHT_PAREN
            {
            ACCUMULATE45=(Token)input.LT(1);
            match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_accumulate_statement2811); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(ACCUMULATE45.getLine()), ACCUMULATE45.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)ACCUMULATE45).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement2821); if (failed) return d;
            pushFollow(FOLLOW_lhs_or_in_accumulate_statement2825);
            inputCE=lhs_or();
            _fsp--;
            if (failed) return d;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:989:29: ( COMMA )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==COMMA) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:989:29: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2827); if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {

              		        d.setInput( inputCE );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:993:3: ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) )
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==INIT) ) {
                alt49=1;
            }
            else if ( (LA49_0==ID) ) {
                alt49=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("993:3: ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) )", 49, 0, input);

                throw nvae;
            }
            switch (alt49) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:993:5: ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:993:5: ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:994:4: INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk
                    {
                    match(input,INIT,FOLLOW_INIT_in_accumulate_statement2845); if (failed) return d;
                    if ( backtracking==0 ) {

                      				location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE_INIT );
                      			
                    }
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2858);
                    text=paren_chunk();
                    _fsp--;
                    if (failed) return d;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:998:21: ( COMMA )?
                    int alt45=2;
                    int LA45_0 = input.LA(1);

                    if ( (LA45_0==COMMA) ) {
                        alt45=1;
                    }
                    switch (alt45) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:998:21: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2860); if (failed) return d;

                            }
                            break;

                    }

                    if ( backtracking==0 ) {

                      				if( input.toString(text.start,text.stop) != null ) {
                      				        d.setInitCode( safeSubstring( input.toString(text.start,text.stop), 1, input.toString(text.start,text.stop).length()-1 ) );
                      					location.setProperty(Location.LOCATION_PROPERTY_FROM_ACCUMULATE_INIT_CONTENT, d.getInitCode());
                      					location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION );
                      				}
                      			
                    }
                    match(input,ACTION,FOLLOW_ACTION_in_accumulate_statement2871); if (failed) return d;
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2875);
                    text=paren_chunk();
                    _fsp--;
                    if (failed) return d;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1006:28: ( COMMA )?
                    int alt46=2;
                    int LA46_0 = input.LA(1);

                    if ( (LA46_0==COMMA) ) {
                        alt46=1;
                    }
                    switch (alt46) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1006:28: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2877); if (failed) return d;

                            }
                            break;

                    }

                    if ( backtracking==0 ) {

                      				if( input.toString(text.start,text.stop) != null ) {
                      				        d.setActionCode( safeSubstring( input.toString(text.start,text.stop), 1, input.toString(text.start,text.stop).length()-1 ) );
                      	       				location.setProperty(Location.LOCATION_PROPERTY_FROM_ACCUMULATE_ACTION_CONTENT, d.getActionCode());
                      					location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE );
                      				}
                      			
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1014:4: ( REVERSE text= paren_chunk ( COMMA )? )?
                    int alt48=2;
                    int LA48_0 = input.LA(1);

                    if ( (LA48_0==REVERSE) ) {
                        alt48=1;
                    }
                    switch (alt48) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1014:6: REVERSE text= paren_chunk ( COMMA )?
                            {
                            match(input,REVERSE,FOLLOW_REVERSE_in_accumulate_statement2890); if (failed) return d;
                            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2894);
                            text=paren_chunk();
                            _fsp--;
                            if (failed) return d;
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1014:31: ( COMMA )?
                            int alt47=2;
                            int LA47_0 = input.LA(1);

                            if ( (LA47_0==COMMA) ) {
                                alt47=1;
                            }
                            switch (alt47) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1014:31: COMMA
                                    {
                                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2896); if (failed) return d;

                                    }
                                    break;

                            }

                            if ( backtracking==0 ) {

                              				if( input.toString(text.start,text.stop) != null ) {
                              				        d.setReverseCode( safeSubstring( input.toString(text.start,text.stop), 1, input.toString(text.start,text.stop).length()-1 ) );
                              	       				location.setProperty(Location.LOCATION_PROPERTY_FROM_ACCUMULATE_REVERSE_CONTENT, d.getReverseCode());
                              					location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT );
                              				}
                              			
                            }

                            }
                            break;

                    }

                    match(input,RESULT,FOLLOW_RESULT_in_accumulate_statement2913); if (failed) return d;
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2917);
                    text=paren_chunk();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {

                      				if( input.toString(text.start,text.stop) != null ) {
                      				        d.setResultCode( safeSubstring( input.toString(text.start,text.stop), 1, input.toString(text.start,text.stop).length()-1 ) );
                      					location.setProperty(Location.LOCATION_PROPERTY_FROM_ACCUMULATE_RESULT_CONTENT, d.getResultCode());
                      				}
                      			
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1032:3: (id= ID text= paren_chunk )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1032:3: (id= ID text= paren_chunk )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1033:4: id= ID text= paren_chunk
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_accumulate_statement2943); if (failed) return d;
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2947);
                    text=paren_chunk();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {

                      				if( id.getText() != null ) {
                      				        d.setExternalFunction( true );
                      					d.setFunctionIdentifier( id.getText() );
                      				}
                      				if( input.toString(text.start,text.stop) != null ) {
                      				        d.setExpression( safeSubstring( input.toString(text.start,text.stop), 1, input.toString(text.start,text.stop).length()-1 ) );
                      	       				location.setProperty(Location.LOCATION_PROPERTY_FROM_ACCUMULATE_EXPRESSION_CONTENT, d.getExpression());
                      				}
                      			
                    }

                    }


                    }
                    break;

            }

            RIGHT_PAREN46=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement2964); if (failed) return d;
            if ( backtracking==0 ) {

              			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
              			d.setEndCharacter( ((CommonToken)RIGHT_PAREN46).getStopIndex() );
              		
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


    // $ANTLR start from_source
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1053:1: from_source[FromDescr from] returns [DeclarativeInvokerDescr ds] : ident= identifier ( options {k=1; } : args= paren_chunk )? ( expression_chain[$from, ad] )? ;
    public final DeclarativeInvokerDescr from_source(FromDescr from) throws RecognitionException {
        DeclarativeInvokerDescr ds = null;

        identifier_return ident = null;

        paren_chunk_return args = null;



        		ds = null;
        		AccessorDescr ad = null;
        		FunctionCallDescr fc = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1059:2: (ident= identifier ( options {k=1; } : args= paren_chunk )? ( expression_chain[$from, ad] )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1059:4: ident= identifier ( options {k=1; } : args= paren_chunk )? ( expression_chain[$from, ad] )?
            {
            pushFollow(FOLLOW_identifier_in_from_source2995);
            ident=identifier();
            _fsp--;
            if (failed) return ds;
            if ( backtracking==0 ) {

              			ad = new AccessorDescr(ident.start.getText());	
              			ad.setLocation( offset(ident.start.getLine()), ident.start.getCharPositionInLine() );
              			ad.setStartCharacter( ((CommonToken)ident.start).getStartIndex() );
              			ad.setEndCharacter( ((CommonToken)ident.start).getStopIndex() );
              			ds = ad;
              			location.setProperty(Location.LOCATION_FROM_CONTENT, ident.start.getText());
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1068:3: ( options {k=1; } : args= paren_chunk )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==LEFT_PAREN) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1075:5: args= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_from_source3023);
                    args=paren_chunk();
                    _fsp--;
                    if (failed) return ds;
                    if ( backtracking==0 ) {

                      			if( input.toString(args.start,args.stop) != null ) {
                      				ad.setVariableName( null );
                      				fc = new FunctionCallDescr(((Token)ident.start).getText());
                      				fc.setLocation( offset(((Token)ident.start).getLine()), ((Token)ident.start).getCharPositionInLine() );			
                      				fc.setArguments(input.toString(args.start,args.stop));
                      				fc.setStartCharacter( ((CommonToken)((Token)ident.start)).getStartIndex() );
                      				fc.setEndCharacter( ((CommonToken)((Token)ident.start)).getStopIndex() );
                      				location.setProperty(Location.LOCATION_FROM_CONTENT, input.toString(args.start,args.stop));
                      				from.setEndCharacter( ((CommonToken)((Token)args.stop)).getStopIndex() );
                      			}
                      		
                    }

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1089:3: ( expression_chain[$from, ad] )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==DOT) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1089:3: expression_chain[$from, ad]
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source3036);
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

            		if( ad != null ) {
            			if( fc != null ) {
            				ad.addFirstInvoker( fc );
            			}
            			location.setProperty(Location.LOCATION_FROM_CONTENT, ad.toString() );
            		}
            	
        }
        return ds;
    }
    // $ANTLR end from_source


    // $ANTLR start expression_chain
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1100:1: expression_chain[FromDescr from, AccessorDescr as] : ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )? ) ;
    public final void expression_chain(FromDescr from, AccessorDescr as) throws RecognitionException {
        identifier_return field = null;

        square_chunk_return sqarg = null;

        paren_chunk_return paarg = null;



          		FieldAccessDescr fa = null;
        	    	MethodAccessDescr ma = null;	
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1105:2: ( ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )? ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1106:2: ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )? )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1106:2: ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1106:4: DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )?
            {
            match(input,DOT,FOLLOW_DOT_in_expression_chain3067); if (failed) return ;
            pushFollow(FOLLOW_identifier_in_expression_chain3071);
            field=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              	        fa = new FieldAccessDescr(((Token)field.start).getText());	
              		fa.setLocation( offset(((Token)field.start).getLine()), ((Token)field.start).getCharPositionInLine() );
              		fa.setStartCharacter( ((CommonToken)((Token)field.start)).getStartIndex() );
              		fa.setEndCharacter( ((CommonToken)((Token)field.start)).getStopIndex() );
              	    
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1113:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )?
            int alt52=3;
            alt52 = dfa52.predict(input);
            switch (alt52) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1114:6: ( LEFT_SQUARE )=>sqarg= square_chunk
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain3102);
                    sqarg=square_chunk();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      	          fa.setArgument( input.toString(sqarg.start,sqarg.stop) );	
                      		  from.setEndCharacter( ((CommonToken)((Token)sqarg.stop)).getStopIndex() );
                      	      
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1120:6: ( LEFT_PAREN )=>paarg= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain3135);
                    paarg=paren_chunk();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      	    	  ma = new MethodAccessDescr( ((Token)field.start).getText(), input.toString(paarg.start,paarg.stop) );	
                      		  ma.setLocation( offset(((Token)field.start).getLine()), ((Token)field.start).getCharPositionInLine() );
                      		  ma.setStartCharacter( ((CommonToken)((Token)field.start)).getStartIndex() );
                      		  from.setEndCharacter( ((CommonToken)((Token)paarg.stop)).getStopIndex() );
                      		
                    }

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1128:4: ( expression_chain[from, as] )?
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==DOT) ) {
                alt53=1;
            }
            switch (alt53) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1128:4: expression_chain[from, as]
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain3150);
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

            		// must be added to the start, since it is a recursive rule
            		if( ma != null ) {
            			as.addFirstInvoker( ma );
            		} else {
            			as.addFirstInvoker( fa );
            		}
            	
        }
        return ;
    }
    // $ANTLR end expression_chain


    // $ANTLR start collect_statement
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1142:1: collect_statement returns [CollectDescr d] : COLLECT LEFT_PAREN pattern= pattern_source RIGHT_PAREN ;
    public final CollectDescr collect_statement() throws RecognitionException {
        CollectDescr d = null;

        Token COLLECT47=null;
        Token RIGHT_PAREN48=null;
        BaseDescr pattern = null;



        		d = factory.createCollect();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1146:2: ( COLLECT LEFT_PAREN pattern= pattern_source RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1147:10: COLLECT LEFT_PAREN pattern= pattern_source RIGHT_PAREN
            {
            COLLECT47=(Token)input.LT(1);
            match(input,COLLECT,FOLLOW_COLLECT_in_collect_statement3201); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(COLLECT47.getLine()), COLLECT47.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)COLLECT47).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_FROM_COLLECT );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement3211); if (failed) return d;
            pushFollow(FOLLOW_pattern_source_in_collect_statement3215);
            pattern=pattern_source();
            _fsp--;
            if (failed) return d;
            RIGHT_PAREN48=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement3217); if (failed) return d;
            if ( backtracking==0 ) {

              		        d.setInputPattern( (PatternDescr) pattern );
              			d.setEndCharacter( ((CommonToken)RIGHT_PAREN48).getStopIndex() );
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


    // $ANTLR start entrypoint_statement
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1161:1: entrypoint_statement returns [EntryPointDescr d] : ENTRY_POINT id= name ;
    public final EntryPointDescr entrypoint_statement() throws RecognitionException {
        EntryPointDescr d = null;

        Token ENTRY_POINT49=null;
        name_return id = null;



        		d = factory.createEntryPoint();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1165:2: ( ENTRY_POINT id= name )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1166:10: ENTRY_POINT id= name
            {
            ENTRY_POINT49=(Token)input.LT(1);
            match(input,ENTRY_POINT,FOLLOW_ENTRY_POINT_in_entrypoint_statement3254); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(ENTRY_POINT49.getLine()), ENTRY_POINT49.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)ENTRY_POINT49).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_FROM_ENTRY_POINT );
              		
            }
            pushFollow(FOLLOW_name_in_entrypoint_statement3266);
            id=name();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {

              		        d.setEntryId( id.name );
              			d.setEndCharacter( ((CommonToken)((Token)id.stop)).getStopIndex() );
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
    // $ANTLR end entrypoint_statement


    // $ANTLR start fact_binding
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1180:1: fact_binding returns [BaseDescr d] : ID ':' (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN ) ;
    public final BaseDescr fact_binding() throws RecognitionException {
        BaseDescr d = null;

        Token ID50=null;
        BaseDescr fe = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d =null;
        		OrDescr or = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1185:3: ( ID ':' (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1186:4: ID ':' (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN )
            {
            ID50=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding3298); if (failed) return d;
            match(input,77,FOLLOW_77_in_fact_binding3300); if (failed) return d;
            if ( backtracking==0 ) {

               		        // handling incomplete parsing
               		        d = new PatternDescr( );
               		        ((PatternDescr) d).setIdentifier( ID50.getText() );
               		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1192:3: (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN )
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==ID) ) {
                alt55=1;
            }
            else if ( (LA55_0==LEFT_PAREN) ) {
                alt55=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1192:3: (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN )", 55, 0, input);

                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1192:5: fe= fact[$ID.text]
                    {
                    pushFollow(FOLLOW_fact_in_fact_binding3314);
                    fe=fact(ID50.getText());
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {

                       		        // override previously instantiated pattern
                       			d =fe;
                       			if( d != null ) {
                         			    d.setStartCharacter( ((CommonToken)ID50).getStartIndex() );
                         			}
                       		
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1201:4: LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_binding3330); if (failed) return d;
                    pushFollow(FOLLOW_fact_in_fact_binding3334);
                    left=fact(ID50.getText());
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {

                       		        // override previously instantiated pattern
                       			d =left;
                       			if( d != null ) {
                         			    d.setStartCharacter( ((CommonToken)ID50).getStartIndex() );
                         			}
                       		
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1209:4: ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )*
                    loop54:
                    do {
                        int alt54=2;
                        int LA54_0 = input.LA(1);

                        if ( ((LA54_0>=OR && LA54_0<=DOUBLE_PIPE)) ) {
                            alt54=1;
                        }


                        switch (alt54) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1209:6: ( OR | DOUBLE_PIPE ) right= fact[$ID.text]
                    	    {
                    	    if ( (input.LA(1)>=OR && input.LA(1)<=DOUBLE_PIPE) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return d;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fact_binding3347);    throw mse;
                    	    }

                    	    pushFollow(FOLLOW_fact_in_fact_binding3359);
                    	    right=fact(ID50.getText());
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
                    	    break loop54;
                        }
                    } while (true);

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_binding3377); if (failed) return d;

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
    // $ANTLR end fact_binding


    // $ANTLR start fact
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1224:1: fact[String ident] returns [BaseDescr d] : id= qualified_id LEFT_PAREN ( constraints[pattern] )? RIGHT_PAREN ( EOF )? ;
    public final BaseDescr fact(String ident) throws RecognitionException {
        BaseDescr d = null;

        Token LEFT_PAREN51=null;
        Token RIGHT_PAREN52=null;
        qualified_id_return id = null;



        		d =null;
        		PatternDescr pattern = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1229:3: (id= qualified_id LEFT_PAREN ( constraints[pattern] )? RIGHT_PAREN ( EOF )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1230:11: id= qualified_id LEFT_PAREN ( constraints[pattern] )? RIGHT_PAREN ( EOF )?
            {
            if ( backtracking==0 ) {

               			pattern = new PatternDescr( );
               			if( ident != null ) {
               				pattern.setIdentifier( ident );
               			}
               			d = pattern; 
               	        
            }
            pushFollow(FOLLOW_qualified_id_in_fact3432);
            id=qualified_id();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
               			if( id != null ) {
              	 		        pattern.setObjectType( id.text );
               			        pattern.setEndCharacter( -1 );
              				pattern.setStartCharacter( ((CommonToken)((Token)id.start)).getStartIndex() );
               			}
               		
            }
            LEFT_PAREN51=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact3442); if (failed) return d;
            if ( backtracking==0 ) {

              		        location.setType( Location.LOCATION_LHS_INSIDE_CONDITION_START );
                          		location.setProperty( Location.LOCATION_PROPERTY_CLASS_NAME, id.text );
               				
               			pattern.setLocation( offset(LEFT_PAREN51.getLine()), LEFT_PAREN51.getCharPositionInLine() );
               			pattern.setLeftParentCharacter( ((CommonToken)LEFT_PAREN51).getStartIndex() );
               		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1253:4: ( constraints[pattern] )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( ((LA56_0>=PACKAGE && LA56_0<=ID)||(LA56_0>=GLOBAL && LA56_0<=LEFT_PAREN)||(LA56_0>=QUERY && LA56_0<=ATTRIBUTES)||LA56_0==ENABLED||LA56_0==SALIENCE||(LA56_0>=DURATION && LA56_0<=DIALECT)||LA56_0==FROM||LA56_0==EVAL||(LA56_0>=INIT && LA56_0<=RESULT)||(LA56_0>=CONTAINS && LA56_0<=MEMBEROF)||LA56_0==IN||LA56_0==THEN) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1253:4: constraints[pattern]
                    {
                    pushFollow(FOLLOW_constraints_in_fact3454);
                    constraints(pattern);
                    _fsp--;
                    if (failed) return d;

                    }
                    break;

            }

            RIGHT_PAREN52=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact3461); if (failed) return d;
            if ( backtracking==0 ) {

              		        if( ")".equals( RIGHT_PAREN52.getText() ) ) {
              				this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
              				pattern.setEndLocation( offset(RIGHT_PAREN52.getLine()), RIGHT_PAREN52.getCharPositionInLine() );	
              				pattern.setEndCharacter( ((CommonToken)RIGHT_PAREN52).getStopIndex() );
              		        	pattern.setRightParentCharacter( ((CommonToken)RIGHT_PAREN52).getStartIndex() );
              		        }
               		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1263:4: ( EOF )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==EOF) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1263:4: EOF
                    {
                    match(input,EOF,FOLLOW_EOF_in_fact3470); if (failed) return d;

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
    // $ANTLR end fact


    // $ANTLR start constraints
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1267:1: constraints[PatternDescr pattern] : constraint[$pattern] ( COMMA constraint[$pattern] )* ;
    public final void constraints(PatternDescr pattern) throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1268:2: ( constraint[$pattern] ( COMMA constraint[$pattern] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1268:4: constraint[$pattern] ( COMMA constraint[$pattern] )*
            {
            pushFollow(FOLLOW_constraint_in_constraints3488);
            constraint(pattern);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1269:3: ( COMMA constraint[$pattern] )*
            loop58:
            do {
                int alt58=2;
                int LA58_0 = input.LA(1);

                if ( (LA58_0==COMMA) ) {
                    alt58=1;
                }


                switch (alt58) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1269:5: COMMA constraint[$pattern]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_constraints3495); if (failed) return ;
            	    if ( backtracking==0 ) {
            	       location.setType( Location.LOCATION_LHS_INSIDE_CONDITION_START ); 
            	    }
            	    pushFollow(FOLLOW_constraint_in_constraints3504);
            	    constraint(pattern);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop58;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1274:1: constraint[PatternDescr pattern] : or_constr[top] ;
    public final void constraint(PatternDescr pattern) throws RecognitionException {

        		ConditionalElementDescr top = null;
        		location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_START);
        		location.setProperty(Location.LOCATION_PROPERTY_PROPERTY_NAME, input.LT(1).getText() );
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1280:2: ( or_constr[top] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1281:3: or_constr[top]
            {
            if ( backtracking==0 ) {

              			top = pattern.getConstraint();
              		
            }
            pushFollow(FOLLOW_or_constr_in_constraint3537);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1287:1: or_constr[ConditionalElementDescr base] : and_constr[or] ( DOUBLE_PIPE and_constr[or] )* ;
    public final void or_constr(ConditionalElementDescr base) throws RecognitionException {

        		OrDescr or = new OrDescr();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1291:2: ( and_constr[or] ( DOUBLE_PIPE and_constr[or] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1292:3: and_constr[or] ( DOUBLE_PIPE and_constr[or] )*
            {
            pushFollow(FOLLOW_and_constr_in_or_constr3560);
            and_constr(or);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1293:3: ( DOUBLE_PIPE and_constr[or] )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( (LA59_0==DOUBLE_PIPE) ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1293:5: DOUBLE_PIPE and_constr[or]
            	    {
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_constr3568); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_START);
            	      		
            	    }
            	    pushFollow(FOLLOW_and_constr_in_or_constr3577);
            	    and_constr(or);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop59;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1308:1: and_constr[ConditionalElementDescr base] : unary_constr[and] ( DOUBLE_AMPER unary_constr[and] )* ;
    public final void and_constr(ConditionalElementDescr base) throws RecognitionException {

        		AndDescr and = new AndDescr();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1312:2: ( unary_constr[and] ( DOUBLE_AMPER unary_constr[and] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1313:3: unary_constr[and] ( DOUBLE_AMPER unary_constr[and] )*
            {
            pushFollow(FOLLOW_unary_constr_in_and_constr3609);
            unary_constr(and);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1314:3: ( DOUBLE_AMPER unary_constr[and] )*
            loop60:
            do {
                int alt60=2;
                int LA60_0 = input.LA(1);

                if ( (LA60_0==DOUBLE_AMPER) ) {
                    alt60=1;
                }


                switch (alt60) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1314:5: DOUBLE_AMPER unary_constr[and]
            	    {
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_constr3617); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_START);
            	      		
            	    }
            	    pushFollow(FOLLOW_unary_constr_in_and_constr3626);
            	    unary_constr(and);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop60;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1329:1: unary_constr[ConditionalElementDescr base] : ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] ) ;
    public final void unary_constr(ConditionalElementDescr base) throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1330:2: ( ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1331:3: ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1331:3: ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] )
            int alt61=3;
            switch ( input.LA(1) ) {
            case PACKAGE:
            case IMPORT:
            case FUNCTION:
            case EVENT:
            case ID:
            case GLOBAL:
            case QUERY:
            case END:
            case TEMPLATE:
            case RULE:
            case WHEN:
            case ATTRIBUTES:
            case ENABLED:
            case SALIENCE:
            case DURATION:
            case DIALECT:
            case FROM:
            case INIT:
            case ACTION:
            case REVERSE:
            case RESULT:
            case CONTAINS:
            case EXCLUDES:
            case MATCHES:
            case SOUNDSLIKE:
            case MEMBEROF:
            case IN:
            case THEN:
                {
                alt61=1;
                }
                break;
            case LEFT_PAREN:
                {
                alt61=2;
                }
                break;
            case EVAL:
                {
                alt61=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1331:3: ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] )", 61, 0, input);

                throw nvae;
            }

            switch (alt61) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1331:5: field_constraint[$base]
                    {
                    pushFollow(FOLLOW_field_constraint_in_unary_constr3654);
                    field_constraint(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1332:5: LEFT_PAREN or_constr[$base] RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_unary_constr3662); if (failed) return ;
                    pushFollow(FOLLOW_or_constr_in_unary_constr3664);
                    or_constr(base);
                    _fsp--;
                    if (failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_unary_constr3667); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1333:5: EVAL predicate[$base]
                    {
                    match(input,EVAL,FOLLOW_EVAL_in_unary_constr3673); if (failed) return ;
                    pushFollow(FOLLOW_predicate_in_unary_constr3675);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1337:1: field_constraint[ConditionalElementDescr base] : ( ( ID ':' f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )? ) | (f= accessor_path or_restr_connective[top] ) );
    public final void field_constraint(ConditionalElementDescr base) throws RecognitionException {
        Token ID53=null;
        accessor_path_return f = null;



        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        		RestrictionConnectiveDescr top = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1343:2: ( ( ID ':' f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )? ) | (f= accessor_path or_restr_connective[top] ) )
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( (LA63_0==ID) ) {
                int LA63_1 = input.LA(2);

                if ( (LA63_1==77) ) {
                    alt63=1;
                }
                else if ( (LA63_1==DOT||LA63_1==LEFT_PAREN||LA63_1==NOT||(LA63_1>=CONTAINS && LA63_1<=IN)||LA63_1==LEFT_SQUARE||(LA63_1>=79 && LA63_1<=84)) ) {
                    alt63=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1337:1: field_constraint[ConditionalElementDescr base] : ( ( ID ':' f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )? ) | (f= accessor_path or_restr_connective[top] ) );", 63, 1, input);

                    throw nvae;
                }
            }
            else if ( ((LA63_0>=PACKAGE && LA63_0<=EVENT)||LA63_0==GLOBAL||(LA63_0>=QUERY && LA63_0<=ATTRIBUTES)||LA63_0==ENABLED||LA63_0==SALIENCE||(LA63_0>=DURATION && LA63_0<=DIALECT)||LA63_0==FROM||(LA63_0>=INIT && LA63_0<=RESULT)||(LA63_0>=CONTAINS && LA63_0<=MEMBEROF)||LA63_0==IN||LA63_0==THEN) ) {
                alt63=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1337:1: field_constraint[ConditionalElementDescr base] : ( ( ID ':' f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )? ) | (f= accessor_path or_restr_connective[top] ) );", 63, 0, input);

                throw nvae;
            }
            switch (alt63) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1344:10: ( ID ':' f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )? )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1344:10: ( ID ':' f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )? )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1345:3: ID ':' f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )?
                    {
                    ID53=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_field_constraint3714); if (failed) return ;
                    match(input,77,FOLLOW_77_in_field_constraint3716); if (failed) return ;
                    if ( backtracking==0 ) {
                       
                      			fbd = new FieldBindingDescr();
                      			fbd.setIdentifier( ID53.getText() );
                      			fbd.setLocation( offset(ID53.getLine()), ID53.getCharPositionInLine() );
                      			fbd.setStartCharacter( ((CommonToken)ID53).getStartIndex() );
                      			base.addDescr( fbd );

                      		    
                    }
                    pushFollow(FOLLOW_accessor_path_in_field_constraint3735);
                    f=accessor_path();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      		    // use ((Token)f.start) to get token matched in identifier
                      		    // or use f.text to get text.
                      		    if( f.text != null ) {
                      			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
                      			location.setProperty(Location.LOCATION_PROPERTY_PROPERTY_NAME, f.text);
                      		    
                      			if ( fbd != null ) {
                      			    fbd.setFieldName( f.text );
                      			    // may have been overwritten
                      			    fbd.setStartCharacter( ((CommonToken)ID53).getStartIndex() );
                      			} 
                      			fc = new FieldConstraintDescr(f.text);
                      			fc.setLocation( offset(((Token)f.start).getLine()), ((Token)f.start).getCharPositionInLine() );
                      			fc.setStartCharacter( ((CommonToken)((Token)f.start)).getStartIndex() );
                      			top = fc.getRestriction();
                      			
                      			// it must be a field constraint, as it is not a binding
                      			if( ID53 == null ) {
                      			    base.addDescr( fc );
                      			}
                      		    }
                      		
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1379:3: ( or_restr_connective[top] | '->' predicate[$base] )?
                    int alt62=3;
                    int LA62_0 = input.LA(1);

                    if ( (LA62_0==LEFT_PAREN||LA62_0==NOT||(LA62_0>=CONTAINS && LA62_0<=IN)||(LA62_0>=79 && LA62_0<=84)) ) {
                        alt62=1;
                    }
                    else if ( (LA62_0==78) ) {
                        alt62=2;
                    }
                    switch (alt62) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1380:4: or_restr_connective[top]
                            {
                            pushFollow(FOLLOW_or_restr_connective_in_field_constraint3749);
                            or_restr_connective(top);
                            _fsp--;
                            if (failed) return ;
                            if ( backtracking==0 ) {

                              				// we must add now as we didn't before
                              				if( ID53 != null) {
                              				    base.addDescr( fc );
                              				}
                              			
                            }

                            }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1388:4: '->' predicate[$base]
                            {
                            match(input,78,FOLLOW_78_in_field_constraint3764); if (failed) return ;
                            pushFollow(FOLLOW_predicate_in_field_constraint3766);
                            predicate(base);
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1392:3: (f= accessor_path or_restr_connective[top] )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1392:3: (f= accessor_path or_restr_connective[top] )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1393:3: f= accessor_path or_restr_connective[top]
                    {
                    pushFollow(FOLLOW_accessor_path_in_field_constraint3792);
                    f=accessor_path();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      		    // use ((Token)f.start) to get token matched in identifier
                      		    // or use f.text to get text.
                      		    if( f.text != null ) {
                      			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
                      			location.setProperty(Location.LOCATION_PROPERTY_PROPERTY_NAME, f.text);
                      		    
                      			fc = new FieldConstraintDescr(f.text);
                      			fc.setLocation( offset(((Token)f.start).getLine()), ((Token)f.start).getCharPositionInLine() );
                      			fc.setStartCharacter( ((CommonToken)((Token)f.start)).getStartIndex() );
                      			top = fc.getRestriction();
                      			
                      			base.addDescr( fc );
                      		    }
                      		
                    }
                    pushFollow(FOLLOW_or_restr_connective_in_field_constraint3801);
                    or_restr_connective(top);
                    _fsp--;
                    if (failed) return ;

                    }


                    }
                    break;

            }
        }
        catch ( NoViableAltException nvae ) {

            	    if( input.LT(1) != null ) {
            	                // in case of incomplete parsing, build as much as possible of the AST
            	                // so that IDE requirements can be met
            			fc = new FieldConstraintDescr( input.LT(1).getText() );
            			fc.setLocation( offset(input.LT(1).getLine()), input.LT(1).getCharPositionInLine() );
            			fc.setStartCharacter( ((CommonToken)input.LT(1)).getStartIndex() );
            			base.addDescr( fc );
            	    }
            	    throw nvae;
            	
        }
        finally {
        }
        return ;
    }
    // $ANTLR end field_constraint


    // $ANTLR start or_restr_connective
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1425:1: or_restr_connective[ RestrictionConnectiveDescr base ] options {backtrack=true; } : and_restr_connective[or] ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )* ;
    public final void or_restr_connective(RestrictionConnectiveDescr base) throws RecognitionException {

        		RestrictionConnectiveDescr or = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR);
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1432:2: ( and_restr_connective[or] ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1433:3: and_restr_connective[or] ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )*
            {
            pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective3848);
            and_restr_connective(or);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1434:3: ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )*
            loop64:
            do {
                int alt64=2;
                alt64 = dfa64.predict(input);
                switch (alt64) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1436:4: DOUBLE_PIPE and_restr_connective[or]
            	    {
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_restr_connective3872); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	      			
            	    }
            	    pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective3883);
            	    and_restr_connective(or);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop64;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	        if( or.getRestrictions().size() == 1 ) {
            	                base.addOrMerge( (RestrictionDescr) or.getRestrictions().get( 0 ) );
            	        } else if ( or.getRestrictions().size() > 1 ) {
            	        	base.addRestriction( or );
            	        }
            	
        }
        return ;
    }
    // $ANTLR end or_restr_connective


    // $ANTLR start and_restr_connective
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1451:1: and_restr_connective[ RestrictionConnectiveDescr base ] : constraint_expression[and] ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )* ;
    public final void and_restr_connective(RestrictionConnectiveDescr base) throws RecognitionException {
        Token t=null;


        		RestrictionConnectiveDescr and = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND);
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1455:2: ( constraint_expression[and] ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1456:3: constraint_expression[and] ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )*
            {
            pushFollow(FOLLOW_constraint_expression_in_and_restr_connective3915);
            constraint_expression(and);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1457:3: ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )*
            loop65:
            do {
                int alt65=2;
                alt65 = dfa65.predict(input);
                switch (alt65) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1458:5: t= DOUBLE_AMPER constraint_expression[and]
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_restr_connective3935); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	      			
            	    }
            	    pushFollow(FOLLOW_constraint_expression_in_and_restr_connective3946);
            	    constraint_expression(and);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop65;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	        if( and.getRestrictions().size() == 1) {
            	                base.addOrMerge( (RestrictionDescr) and.getRestrictions().get( 0 ) );
            	        } else if ( and.getRestrictions().size() > 1 ) {
            	        	base.addRestriction( and );
            	        }
            	
        }
        return ;
    }
    // $ANTLR end and_restr_connective


    // $ANTLR start constraint_expression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1473:1: constraint_expression[RestrictionConnectiveDescr base] : ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN ) ;
    public final void constraint_expression(RestrictionConnectiveDescr base) throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1474:9: ( ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1475:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1475:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )
            int alt66=3;
            switch ( input.LA(1) ) {
            case IN:
                {
                alt66=1;
                }
                break;
            case NOT:
                {
                int LA66_2 = input.LA(2);

                if ( (LA66_2==CONTAINS||LA66_2==MATCHES||(LA66_2>=MEMBEROF && LA66_2<=TILDE)) ) {
                    alt66=2;
                }
                else if ( (LA66_2==IN) ) {
                    alt66=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1475:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )", 66, 2, input);

                    throw nvae;
                }
                }
                break;
            case CONTAINS:
            case EXCLUDES:
            case MATCHES:
            case SOUNDSLIKE:
            case MEMBEROF:
            case TILDE:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
                {
                alt66=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt66=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1475:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )", 66, 0, input);

                throw nvae;
            }

            switch (alt66) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1475:5: compound_operator[$base]
                    {
                    pushFollow(FOLLOW_compound_operator_in_constraint_expression3983);
                    compound_operator(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1476:5: simple_operator[$base]
                    {
                    pushFollow(FOLLOW_simple_operator_in_constraint_expression3990);
                    simple_operator(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1477:5: LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_constraint_expression3998); if (failed) return ;
                    if ( backtracking==0 ) {

                      			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
                      		
                    }
                    pushFollow(FOLLOW_or_restr_connective_in_constraint_expression4007);
                    or_restr_connective(base);
                    _fsp--;
                    if (failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_constraint_expression4012); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1486:1: simple_operator[RestrictionConnectiveDescr base] : (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | t= SOUNDSLIKE | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF | TILDE t= ID (param= square_chunk )? | n= NOT TILDE t= ID (param= square_chunk )? ) rd= expression_value[$base, op, isNegated, paramText] ;
    public final void simple_operator(RestrictionConnectiveDescr base) throws RecognitionException {
        Token t=null;
        Token n=null;
        square_chunk_return param = null;

        RestrictionDescr rd = null;



        		String op = null;
        		String paramText = null;
        		boolean isNegated = false;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1492:2: ( (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | t= SOUNDSLIKE | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF | TILDE t= ID (param= square_chunk )? | n= NOT TILDE t= ID (param= square_chunk )? ) rd= expression_value[$base, op, isNegated, paramText] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1493:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | t= SOUNDSLIKE | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF | TILDE t= ID (param= square_chunk )? | n= NOT TILDE t= ID (param= square_chunk )? ) rd= expression_value[$base, op, isNegated, paramText]
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1493:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | t= SOUNDSLIKE | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF | TILDE t= ID (param= square_chunk )? | n= NOT TILDE t= ID (param= square_chunk )? )
            int alt69=16;
            switch ( input.LA(1) ) {
            case 79:
                {
                alt69=1;
                }
                break;
            case 80:
                {
                alt69=2;
                }
                break;
            case 81:
                {
                alt69=3;
                }
                break;
            case 82:
                {
                alt69=4;
                }
                break;
            case 83:
                {
                alt69=5;
                }
                break;
            case 84:
                {
                alt69=6;
                }
                break;
            case CONTAINS:
                {
                alt69=7;
                }
                break;
            case NOT:
                {
                switch ( input.LA(2) ) {
                case TILDE:
                    {
                    alt69=16;
                    }
                    break;
                case MEMBEROF:
                    {
                    alt69=14;
                    }
                    break;
                case CONTAINS:
                    {
                    alt69=8;
                    }
                    break;
                case MATCHES:
                    {
                    alt69=12;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1493:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | t= SOUNDSLIKE | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF | TILDE t= ID (param= square_chunk )? | n= NOT TILDE t= ID (param= square_chunk )? )", 69, 8, input);

                    throw nvae;
                }

                }
                break;
            case EXCLUDES:
                {
                alt69=9;
                }
                break;
            case MATCHES:
                {
                alt69=10;
                }
                break;
            case SOUNDSLIKE:
                {
                alt69=11;
                }
                break;
            case MEMBEROF:
                {
                alt69=13;
                }
                break;
            case TILDE:
                {
                alt69=15;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1493:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | t= SOUNDSLIKE | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF | TILDE t= ID (param= square_chunk )? | n= NOT TILDE t= ID (param= square_chunk )? )", 69, 0, input);

                throw nvae;
            }

            switch (alt69) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1493:5: t= '=='
                    {
                    t=(Token)input.LT(1);
                    match(input,79,FOLLOW_79_in_simple_operator4043); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1494:5: t= '>'
                    {
                    t=(Token)input.LT(1);
                    match(input,80,FOLLOW_80_in_simple_operator4051); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1495:5: t= '>='
                    {
                    t=(Token)input.LT(1);
                    match(input,81,FOLLOW_81_in_simple_operator4059); if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1496:5: t= '<'
                    {
                    t=(Token)input.LT(1);
                    match(input,82,FOLLOW_82_in_simple_operator4067); if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1497:5: t= '<='
                    {
                    t=(Token)input.LT(1);
                    match(input,83,FOLLOW_83_in_simple_operator4075); if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1498:5: t= '!='
                    {
                    t=(Token)input.LT(1);
                    match(input,84,FOLLOW_84_in_simple_operator4083); if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1499:25: t= CONTAINS
                    {
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_simple_operator4111); if (failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1500:25: n= NOT t= CONTAINS
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator4139); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_simple_operator4143); if (failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1501:25: t= EXCLUDES
                    {
                    t=(Token)input.LT(1);
                    match(input,EXCLUDES,FOLLOW_EXCLUDES_in_simple_operator4171); if (failed) return ;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1502:25: t= MATCHES
                    {
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_simple_operator4199); if (failed) return ;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1503:25: t= SOUNDSLIKE
                    {
                    t=(Token)input.LT(1);
                    match(input,SOUNDSLIKE,FOLLOW_SOUNDSLIKE_in_simple_operator4227); if (failed) return ;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1504:25: n= NOT t= MATCHES
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator4255); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_simple_operator4259); if (failed) return ;

                    }
                    break;
                case 13 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1505:25: t= MEMBEROF
                    {
                    t=(Token)input.LT(1);
                    match(input,MEMBEROF,FOLLOW_MEMBEROF_in_simple_operator4287); if (failed) return ;

                    }
                    break;
                case 14 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1506:25: n= NOT t= MEMBEROF
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator4315); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,MEMBEROF,FOLLOW_MEMBEROF_in_simple_operator4319); if (failed) return ;

                    }
                    break;
                case 15 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1507:5: TILDE t= ID (param= square_chunk )?
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_simple_operator4325); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_simple_operator4329); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1507:21: (param= square_chunk )?
                    int alt67=2;
                    int LA67_0 = input.LA(1);

                    if ( (LA67_0==LEFT_SQUARE) ) {
                        alt67=1;
                    }
                    switch (alt67) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1507:21: param= square_chunk
                            {
                            pushFollow(FOLLOW_square_chunk_in_simple_operator4333);
                            param=square_chunk();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 16 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1508:5: n= NOT TILDE t= ID (param= square_chunk )?
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator4342); if (failed) return ;
                    match(input,TILDE,FOLLOW_TILDE_in_simple_operator4344); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_simple_operator4348); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1508:27: (param= square_chunk )?
                    int alt68=2;
                    int LA68_0 = input.LA(1);

                    if ( (LA68_0==LEFT_SQUARE) ) {
                        alt68=1;
                    }
                    switch (alt68) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1508:27: param= square_chunk
                            {
                            pushFollow(FOLLOW_square_chunk_in_simple_operator4352);
                            param=square_chunk();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }

            if ( backtracking==0 ) {

                		    location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);
                                  location.setProperty(Location.LOCATION_PROPERTY_OPERATOR, t.getText());
               	            op = t.getText();
               	            isNegated = n != null;
               	            if( param != null ) {
                                      paramText = safeSubstring( input.toString(param.start,param.stop), 1, input.toString(param.start,param.stop).length()-1 );
               	            } 
              		
            }
            pushFollow(FOLLOW_expression_value_in_simple_operator4367);
            rd=expression_value(base,  op,  isNegated,  paramText);
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            		if ( rd == null && op != null ) {
            		        base.addRestriction( new LiteralRestrictionDescr(op, false, null) );
            		}
            	
        }
        return ;
    }
    // $ANTLR end simple_operator


    // $ANTLR start compound_operator
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1527:1: compound_operator[RestrictionConnectiveDescr base] : ( IN | NOT IN ) LEFT_PAREN rd= expression_value[group, op, false, null] ( COMMA rd= expression_value[group, op, false, null] )* RIGHT_PAREN ;
    public final void compound_operator(RestrictionConnectiveDescr base) throws RecognitionException {
        RestrictionDescr rd = null;



        		String op = null;
        		RestrictionConnectiveDescr group = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1532:2: ( ( IN | NOT IN ) LEFT_PAREN rd= expression_value[group, op, false, null] ( COMMA rd= expression_value[group, op, false, null] )* RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1533:3: ( IN | NOT IN ) LEFT_PAREN rd= expression_value[group, op, false, null] ( COMMA rd= expression_value[group, op, false, null] )* RIGHT_PAREN
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1533:3: ( IN | NOT IN )
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==IN) ) {
                alt70=1;
            }
            else if ( (LA70_0==NOT) ) {
                alt70=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1533:3: ( IN | NOT IN )", 70, 0, input);

                throw nvae;
            }
            switch (alt70) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1533:5: IN
                    {
                    match(input,IN,FOLLOW_IN_in_compound_operator4397); if (failed) return ;
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1541:5: NOT IN
                    {
                    match(input,NOT,FOLLOW_NOT_in_compound_operator4409); if (failed) return ;
                    match(input,IN,FOLLOW_IN_in_compound_operator4411); if (failed) return ;
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

            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_compound_operator4426); if (failed) return ;
            pushFollow(FOLLOW_expression_value_in_compound_operator4430);
            rd=expression_value(group,  op,  false,  null);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1551:3: ( COMMA rd= expression_value[group, op, false, null] )*
            loop71:
            do {
                int alt71=2;
                int LA71_0 = input.LA(1);

                if ( (LA71_0==COMMA) ) {
                    alt71=1;
                }


                switch (alt71) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1551:5: COMMA rd= expression_value[group, op, false, null]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_compound_operator4437); if (failed) return ;
            	    pushFollow(FOLLOW_expression_value_in_compound_operator4441);
            	    rd=expression_value(group,  op,  false,  null);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop71;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_compound_operator4450); if (failed) return ;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1558:1: expression_value[RestrictionConnectiveDescr base, String op, boolean isNegated, String paramText] returns [RestrictionDescr rd] : (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk ) ;
    public final RestrictionDescr expression_value(RestrictionConnectiveDescr base, String op, boolean isNegated, String paramText) throws RecognitionException {
        RestrictionDescr rd = null;

        accessor_path_return ap = null;

        literal_constraint_return lc = null;

        paren_chunk_return rvc = null;



        		rd = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1562:2: ( (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1563:3: (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1563:3: (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk )
            int alt72=3;
            switch ( input.LA(1) ) {
            case PACKAGE:
            case IMPORT:
            case FUNCTION:
            case EVENT:
            case ID:
            case GLOBAL:
            case QUERY:
            case END:
            case TEMPLATE:
            case RULE:
            case WHEN:
            case ATTRIBUTES:
            case ENABLED:
            case SALIENCE:
            case DURATION:
            case DIALECT:
            case FROM:
            case INIT:
            case ACTION:
            case REVERSE:
            case RESULT:
            case CONTAINS:
            case EXCLUDES:
            case MATCHES:
            case SOUNDSLIKE:
            case MEMBEROF:
            case IN:
            case THEN:
                {
                alt72=1;
                }
                break;
            case STRING:
            case BOOL:
            case INT:
            case FLOAT:
            case NULL:
                {
                alt72=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt72=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return rd;}
                NoViableAltException nvae =
                    new NoViableAltException("1563:3: (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk )", 72, 0, input);

                throw nvae;
            }

            switch (alt72) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1563:5: ap= accessor_path
                    {
                    pushFollow(FOLLOW_accessor_path_in_expression_value4484);
                    ap=accessor_path();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      			        if( ap.text.indexOf( '.' ) > -1 || ap.text.indexOf( '[' ) > -1) {
                      					rd = new QualifiedIdentifierRestrictionDescr(op, isNegated, paramText, ap.text);
                      				} else {
                      					rd = new VariableRestrictionDescr(op, isNegated, paramText, ap.text);
                      				}
                      			
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1571:5: lc= literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_expression_value4504);
                    lc=literal_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd = new LiteralRestrictionDescr(op, isNegated, paramText, lc.text, lc.type );
                      			
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1575:5: rvc= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_value4518);
                    rvc=paren_chunk();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd = new ReturnValueRestrictionDescr(op, isNegated, paramText, safeSubstring( input.toString(rvc.start,rvc.stop), 1, input.toString(rvc.start,rvc.stop).length()-1) );							
                      			
                    }

                    }
                    break;

            }

            if ( backtracking==0 ) {

              			if( rd != null ) {
              				base.addRestriction( rd );
              			}
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

    public static class literal_constraint_return extends ParserRuleReturnScope {
        public String text;
        public int type;
    };

    // $ANTLR start literal_constraint
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1588:1: literal_constraint returns [String text, int type] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public final literal_constraint_return literal_constraint() throws RecognitionException {
        literal_constraint_return retval = new literal_constraint_return();
        retval.start = input.LT(1);

        Token t=null;


        		retval.text = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1592:2: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1592:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1592:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            int alt73=5;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt73=1;
                }
                break;
            case INT:
                {
                alt73=2;
                }
                break;
            case FLOAT:
                {
                alt73=3;
                }
                break;
            case BOOL:
                {
                alt73=4;
                }
                break;
            case NULL:
                {
                alt73=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1592:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )", 73, 0, input);

                throw nvae;
            }

            switch (alt73) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1592:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint4561); if (failed) return retval;
                    if ( backtracking==0 ) {
                       retval.text = getString( t.getText() ); retval.type = LiteralRestrictionDescr.TYPE_STRING; 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1593:5: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint4572); if (failed) return retval;
                    if ( backtracking==0 ) {
                       retval.text = t.getText(); retval.type = LiteralRestrictionDescr.TYPE_NUMBER; 
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1594:5: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint4585); if (failed) return retval;
                    if ( backtracking==0 ) {
                       retval.text = t.getText(); retval.type = LiteralRestrictionDescr.TYPE_NUMBER; 
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1595:5: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint4596); if (failed) return retval;
                    if ( backtracking==0 ) {
                       retval.text = t.getText(); retval.type = LiteralRestrictionDescr.TYPE_BOOLEAN; 
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1596:5: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal_constraint4608); if (failed) return retval;
                    if ( backtracking==0 ) {
                       retval.text = null; retval.type = LiteralRestrictionDescr.TYPE_NULL; 
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end literal_constraint


    // $ANTLR start predicate
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1600:1: predicate[ConditionalElementDescr base] : text= paren_chunk ;
    public final void predicate(ConditionalElementDescr base) throws RecognitionException {
        paren_chunk_return text = null;



        		PredicateDescr d = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1604:2: (text= paren_chunk )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1605:3: text= paren_chunk
            {
            pushFollow(FOLLOW_paren_chunk_in_predicate4646);
            text=paren_chunk();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              		        if( input.toString(text.start,text.stop) != null ) {
              				d = new PredicateDescr( );
              			        d.setContent( safeSubstring( input.toString(text.start,text.stop), 1, input.toString(text.start,text.stop).length()-1 ) );
              				d.setEndCharacter( ((CommonToken)((Token)text.stop)).getStopIndex() );
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

    public static class curly_chunk_return extends ParserRuleReturnScope {
    };

    // $ANTLR start curly_chunk
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1617:1: curly_chunk : LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk )* RIGHT_CURLY ;
    public final curly_chunk_return curly_chunk() throws RecognitionException {
        curly_chunk_return retval = new curly_chunk_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1618:2: ( LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk )* RIGHT_CURLY )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1619:3: LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk )* RIGHT_CURLY
            {
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk4664); if (failed) return retval;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1619:14: (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk )*
            loop74:
            do {
                int alt74=3;
                int LA74_0 = input.LA(1);

                if ( ((LA74_0>=PACKAGE && LA74_0<=NULL)||(LA74_0>=LEFT_SQUARE && LA74_0<=84)) ) {
                    alt74=1;
                }
                else if ( (LA74_0==LEFT_CURLY) ) {
                    alt74=2;
                }


                switch (alt74) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1619:16: ~ ( LEFT_CURLY | RIGHT_CURLY )
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=NULL)||(input.LA(1)>=LEFT_SQUARE && input.LA(1)<=84) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_curly_chunk4668);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1619:44: curly_chunk
            	    {
            	    pushFollow(FOLLOW_curly_chunk_in_curly_chunk4677);
            	    curly_chunk();
            	    _fsp--;
            	    if (failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop74;
                }
            } while (true);

            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk4682); if (failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end curly_chunk

    public static class paren_chunk_return extends ParserRuleReturnScope {
    };

    // $ANTLR start paren_chunk
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1622:1: paren_chunk : LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk )* RIGHT_PAREN ;
    public final paren_chunk_return paren_chunk() throws RecognitionException {
        paren_chunk_return retval = new paren_chunk_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1623:2: ( LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk )* RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1624:3: LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk4696); if (failed) return retval;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1624:14: (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk )*
            loop75:
            do {
                int alt75=3;
                int LA75_0 = input.LA(1);

                if ( ((LA75_0>=PACKAGE && LA75_0<=GLOBAL)||LA75_0==COMMA||(LA75_0>=QUERY && LA75_0<=84)) ) {
                    alt75=1;
                }
                else if ( (LA75_0==LEFT_PAREN) ) {
                    alt75=2;
                }


                switch (alt75) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1624:16: ~ ( LEFT_PAREN | RIGHT_PAREN )
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=GLOBAL)||input.LA(1)==COMMA||(input.LA(1)>=QUERY && input.LA(1)<=84) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_paren_chunk4700);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1624:44: paren_chunk
            	    {
            	    pushFollow(FOLLOW_paren_chunk_in_paren_chunk4709);
            	    paren_chunk();
            	    _fsp--;
            	    if (failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop75;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk4714); if (failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end paren_chunk

    public static class square_chunk_return extends ParserRuleReturnScope {
    };

    // $ANTLR start square_chunk
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1627:1: square_chunk : LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk )* RIGHT_SQUARE ;
    public final square_chunk_return square_chunk() throws RecognitionException {
        square_chunk_return retval = new square_chunk_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1628:2: ( LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk )* RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1629:3: LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk )* RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk4727); if (failed) return retval;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1629:15: (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk )*
            loop76:
            do {
                int alt76=3;
                int LA76_0 = input.LA(1);

                if ( ((LA76_0>=PACKAGE && LA76_0<=RIGHT_CURLY)||(LA76_0>=THEN && LA76_0<=84)) ) {
                    alt76=1;
                }
                else if ( (LA76_0==LEFT_SQUARE) ) {
                    alt76=2;
                }


                switch (alt76) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1629:17: ~ ( LEFT_SQUARE | RIGHT_SQUARE )
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=RIGHT_CURLY)||(input.LA(1)>=THEN && input.LA(1)<=84) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_square_chunk4731);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1629:47: square_chunk
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_square_chunk4740);
            	    square_chunk();
            	    _fsp--;
            	    if (failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop76;
                }
            } while (true);

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk4745); if (failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end square_chunk

    public static class qualified_id_return extends ParserRuleReturnScope {
        public String text;
    };

    // $ANTLR start qualified_id
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1632:1: qualified_id returns [ String text ] : ID ( DOT identifier )* ( LEFT_SQUARE RIGHT_SQUARE )* ;
    public final qualified_id_return qualified_id() throws RecognitionException {
        qualified_id_return retval = new qualified_id_return();
        retval.start = input.LT(1);

        Token ID54=null;
        identifier_return identifier55 = null;



        	        StringBuffer buf = new StringBuffer();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1639:2: ( ID ( DOT identifier )* ( LEFT_SQUARE RIGHT_SQUARE )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1639:5: ID ( DOT identifier )* ( LEFT_SQUARE RIGHT_SQUARE )*
            {
            ID54=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_qualified_id4774); if (failed) return retval;
            if ( backtracking==0 ) {
              buf.append(ID54.getText());
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1639:32: ( DOT identifier )*
            loop77:
            do {
                int alt77=2;
                int LA77_0 = input.LA(1);

                if ( (LA77_0==DOT) ) {
                    alt77=1;
                }


                switch (alt77) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1639:34: DOT identifier
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_qualified_id4780); if (failed) return retval;
            	    pushFollow(FOLLOW_identifier_in_qualified_id4782);
            	    identifier55=identifier();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) {
            	      buf.append("."+input.toString(identifier55.start,identifier55.stop));
            	    }

            	    }
            	    break;

            	default :
            	    break loop77;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1639:88: ( LEFT_SQUARE RIGHT_SQUARE )*
            loop78:
            do {
                int alt78=2;
                int LA78_0 = input.LA(1);

                if ( (LA78_0==LEFT_SQUARE) ) {
                    alt78=1;
                }


                switch (alt78) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1639:90: LEFT_SQUARE RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_qualified_id4791); if (failed) return retval;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_qualified_id4793); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	      buf.append("[]");
            	    }

            	    }
            	    break;

            	default :
            	    break loop78;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {

              	        retval.text = buf != null ? buf.toString() : "";
              	
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end qualified_id


    // $ANTLR start dotted_name
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1642:1: dotted_name returns [ String text ] : i= identifier ( DOT i= identifier )* ( LEFT_SQUARE RIGHT_SQUARE )* ;
    public final String dotted_name() throws RecognitionException {
        String text = null;

        identifier_return i = null;



        	        StringBuffer buf = new StringBuffer();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1649:2: (i= identifier ( DOT i= identifier )* ( LEFT_SQUARE RIGHT_SQUARE )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1649:4: i= identifier ( DOT i= identifier )* ( LEFT_SQUARE RIGHT_SQUARE )*
            {
            pushFollow(FOLLOW_identifier_in_dotted_name4827);
            i=identifier();
            _fsp--;
            if (failed) return text;
            if ( backtracking==0 ) {
              buf.append(input.toString(i.start,i.stop));
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1649:40: ( DOT i= identifier )*
            loop79:
            do {
                int alt79=2;
                int LA79_0 = input.LA(1);

                if ( (LA79_0==DOT) ) {
                    alt79=1;
                }


                switch (alt79) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1649:42: DOT i= identifier
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_dotted_name4833); if (failed) return text;
            	    pushFollow(FOLLOW_identifier_in_dotted_name4837);
            	    i=identifier();
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {
            	      buf.append("."+input.toString(i.start,i.stop));
            	    }

            	    }
            	    break;

            	default :
            	    break loop79;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1649:89: ( LEFT_SQUARE RIGHT_SQUARE )*
            loop80:
            do {
                int alt80=2;
                int LA80_0 = input.LA(1);

                if ( (LA80_0==LEFT_SQUARE) ) {
                    alt80=1;
                }


                switch (alt80) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1649:91: LEFT_SQUARE RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dotted_name4846); if (failed) return text;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dotted_name4848); if (failed) return text;
            	    if ( backtracking==0 ) {
            	      buf.append("[]");
            	    }

            	    }
            	    break;

            	default :
            	    break loop80;
                }
            } while (true);


            }

            if ( backtracking==0 ) {

              	        text = buf != null ? buf.toString() : "";
              	
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
    // $ANTLR end dotted_name

    public static class accessor_path_return extends ParserRuleReturnScope {
        public String text;
    };

    // $ANTLR start accessor_path
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1652:1: accessor_path returns [ String text ] : a= accessor_element ( DOT a= accessor_element )* ;
    public final accessor_path_return accessor_path() throws RecognitionException {
        accessor_path_return retval = new accessor_path_return();
        retval.start = input.LT(1);

        String a = null;



        	        StringBuffer buf = new StringBuffer();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1659:2: (a= accessor_element ( DOT a= accessor_element )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1659:4: a= accessor_element ( DOT a= accessor_element )*
            {
            pushFollow(FOLLOW_accessor_element_in_accessor_path4882);
            a=accessor_element();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) {
              buf.append(a);
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1659:46: ( DOT a= accessor_element )*
            loop81:
            do {
                int alt81=2;
                int LA81_0 = input.LA(1);

                if ( (LA81_0==DOT) ) {
                    alt81=1;
                }


                switch (alt81) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1659:48: DOT a= accessor_element
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_accessor_path4888); if (failed) return retval;
            	    pushFollow(FOLLOW_accessor_element_in_accessor_path4892);
            	    a=accessor_element();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) {
            	      buf.append("."+a);
            	    }

            	    }
            	    break;

            	default :
            	    break loop81;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( backtracking==0 ) {

              	        retval.text = buf != null ? buf.toString() : "";
              	
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end accessor_path


    // $ANTLR start accessor_element
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1662:1: accessor_element returns [ String text ] : i= identifier (s= square_chunk )* ;
    public final String accessor_element() throws RecognitionException {
        String text = null;

        identifier_return i = null;

        square_chunk_return s = null;



        	        StringBuffer buf = new StringBuffer();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1669:2: (i= identifier (s= square_chunk )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1670:3: i= identifier (s= square_chunk )*
            {
            pushFollow(FOLLOW_identifier_in_accessor_element4930);
            i=identifier();
            _fsp--;
            if (failed) return text;
            if ( backtracking==0 ) {
              buf.append(input.toString(i.start,i.stop));
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1670:39: (s= square_chunk )*
            loop82:
            do {
                int alt82=2;
                int LA82_0 = input.LA(1);

                if ( (LA82_0==LEFT_SQUARE) ) {
                    alt82=1;
                }


                switch (alt82) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1670:40: s= square_chunk
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_accessor_element4937);
            	    s=square_chunk();
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {
            	      buf.append(input.toString(s.start,s.stop));
            	    }

            	    }
            	    break;

            	default :
            	    break loop82;
                }
            } while (true);


            }

            if ( backtracking==0 ) {

              	        text = buf != null ? buf.toString() : "";
              	
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
    // $ANTLR end accessor_element


    // $ANTLR start rhs_chunk
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1673:1: rhs_chunk[RuleDescr rule] : THEN (~ END )* loc= END opt_semicolon ;
    public final void rhs_chunk(RuleDescr rule) throws RecognitionException {
        Token loc=null;
        Token THEN56=null;

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1674:2: ( THEN (~ END )* loc= END opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1675:3: THEN (~ END )* loc= END opt_semicolon
            {
            THEN56=(Token)input.LT(1);
            match(input,THEN,FOLLOW_THEN_in_rhs_chunk4958); if (failed) return ;
            if ( backtracking==0 ) {
               location.setType( Location.LOCATION_RHS ); 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1676:3: (~ END )*
            loop83:
            do {
                int alt83=2;
                int LA83_0 = input.LA(1);

                if ( ((LA83_0>=PACKAGE && LA83_0<=QUERY)||(LA83_0>=TEMPLATE && LA83_0<=84)) ) {
                    alt83=1;
                }


                switch (alt83) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1676:5: ~ END
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=QUERY)||(input.LA(1)>=TEMPLATE && input.LA(1)<=84) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_rhs_chunk4966);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop83;
                }
            } while (true);

            loc=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_rhs_chunk4990); if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_rhs_chunk4992);
            opt_semicolon();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

                                  // ignoring first line in the consequence
                                  String buf = input.toString( THEN56, loc );
                                  // removing final END keyword
                                  int idx=4;
                                  while( idx < buf.length()-3 && (buf.charAt(idx) == ' ' || buf.charAt(idx) == '\t') ) {
                                      idx++;
                                  }
                                  if( idx < buf.length()-3 && buf.charAt(idx) == '\r' ) idx++;
                                  if( idx < buf.length()-3 && buf.charAt(idx) == '\n' ) idx++;
                                  buf = safeSubstring( buf, idx, buf.length()-3 );
              		    rule.setConsequence( buf );
                   		    rule.setConsequenceLocation(offset(THEN56.getLine()), THEN56.getCharPositionInLine());
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

    public static class name_return extends ParserRuleReturnScope {
        public String name;
    };

    // $ANTLR start name
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1696:1: name returns [String name] : ( ID | STRING );
    public final name_return name() throws RecognitionException {
        name_return retval = new name_return();
        retval.start = input.LT(1);

        Token ID57=null;
        Token STRING58=null;

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1697:2: ( ID | STRING )
            int alt84=2;
            int LA84_0 = input.LA(1);

            if ( (LA84_0==ID) ) {
                alt84=1;
            }
            else if ( (LA84_0==STRING) ) {
                alt84=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1696:1: name returns [String name] : ( ID | STRING );", 84, 0, input);

                throw nvae;
            }
            switch (alt84) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1697:5: ID
                    {
                    ID57=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_name5026); if (failed) return retval;
                    if ( backtracking==0 ) {
                       retval.name = ID57.getText(); 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1698:5: STRING
                    {
                    STRING58=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_name5034); if (failed) return retval;
                    if ( backtracking==0 ) {
                       retval.name = getString( STRING58.getText() ); 
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end name

    public static class identifier_return extends ParserRuleReturnScope {
    };

    // $ANTLR start identifier
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1701:1: identifier : ( ID | PACKAGE | FUNCTION | GLOBAL | IMPORT | EVENT | RULE | QUERY | TEMPLATE | ATTRIBUTES | ENABLED | SALIENCE | DURATION | DIALECT | FROM | INIT | ACTION | REVERSE | RESULT | CONTAINS | EXCLUDES | MEMBEROF | MATCHES | SOUNDSLIKE | WHEN | THEN | END | IN );
    public final identifier_return identifier() throws RecognitionException {
        identifier_return retval = new identifier_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1702:2: ( ID | PACKAGE | FUNCTION | GLOBAL | IMPORT | EVENT | RULE | QUERY | TEMPLATE | ATTRIBUTES | ENABLED | SALIENCE | DURATION | DIALECT | FROM | INIT | ACTION | REVERSE | RESULT | CONTAINS | EXCLUDES | MEMBEROF | MATCHES | SOUNDSLIKE | WHEN | THEN | END | IN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:
            {
            if ( (input.LA(1)>=PACKAGE && input.LA(1)<=ID)||input.LA(1)==GLOBAL||(input.LA(1)>=QUERY && input.LA(1)<=ATTRIBUTES)||input.LA(1)==ENABLED||input.LA(1)==SALIENCE||(input.LA(1)>=DURATION && input.LA(1)<=DIALECT)||input.LA(1)==FROM||(input.LA(1)>=INIT && input.LA(1)<=RESULT)||(input.LA(1)>=CONTAINS && input.LA(1)<=MEMBEROF)||input.LA(1)==IN||input.LA(1)==THEN ) {
                input.consume();
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_identifier0);    throw mse;
            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end identifier

    // $ANTLR start synpred1
    public final void synpred1_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:819:6: ( EXISTS )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:819:8: EXISTS
        {
        match(input,EXISTS,FOLLOW_EXISTS_in_synpred11972); if (failed) return ;

        }
    }
    // $ANTLR end synpred1

    // $ANTLR start synpred2
    public final void synpred2_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:820:5: ( NOT )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:820:7: NOT
        {
        match(input,NOT,FOLLOW_NOT_in_synpred21990); if (failed) return ;

        }
    }
    // $ANTLR end synpred2

    // $ANTLR start synpred3
    public final void synpred3_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:821:5: ( EVAL )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:821:7: EVAL
        {
        match(input,EVAL,FOLLOW_EVAL_in_synpred32009); if (failed) return ;

        }
    }
    // $ANTLR end synpred3

    // $ANTLR start synpred4
    public final void synpred4_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:822:5: ( FORALL )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:822:7: FORALL
        {
        match(input,FORALL,FOLLOW_FORALL_in_synpred42028); if (failed) return ;

        }
    }
    // $ANTLR end synpred4

    // $ANTLR start synpred5
    public final void synpred5_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:823:5: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:823:7: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred52047); if (failed) return ;

        }
    }
    // $ANTLR end synpred5

    // $ANTLR start synpred6
    public final void synpred6_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:838:13: ( FROM ENTRY_POINT )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:838:14: FROM ENTRY_POINT
        {
        match(input,FROM,FOLLOW_FROM_in_synpred62153); if (failed) return ;
        match(input,ENTRY_POINT,FOLLOW_ENTRY_POINT_in_synpred62155); if (failed) return ;

        }
    }
    // $ANTLR end synpred6

    // $ANTLR start synpred9
    public final void synpred9_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1114:6: ( LEFT_SQUARE )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1114:8: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred93094); if (failed) return ;

        }
    }
    // $ANTLR end synpred9

    // $ANTLR start synpred10
    public final void synpred10_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1120:6: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1120:8: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred103127); if (failed) return ;

        }
    }
    // $ANTLR end synpred10

    // $ANTLR start synpred11
    public final void synpred11_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1436:4: ( DOUBLE_PIPE and_restr_connective[or] )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1436:4: DOUBLE_PIPE and_restr_connective[or]
        {
        match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred113872); if (failed) return ;
        pushFollow(FOLLOW_and_restr_connective_in_synpred113883);
        and_restr_connective(or);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred11

    // $ANTLR start synpred12
    public final void synpred12_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1458:5: ( DOUBLE_AMPER constraint_expression[and] )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1458:5: DOUBLE_AMPER constraint_expression[and]
        {
        match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred123935); if (failed) return ;
        pushFollow(FOLLOW_constraint_expression_in_synpred123946);
        constraint_expression(and);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred12

    public final boolean synpred12() {
        backtracking++;
        int start = input.mark();
        try {
            synpred12_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred4() {
        backtracking++;
        int start = input.mark();
        try {
            synpred4_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred9() {
        backtracking++;
        int start = input.mark();
        try {
            synpred9_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred2() {
        backtracking++;
        int start = input.mark();
        try {
            synpred2_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred3() {
        backtracking++;
        int start = input.mark();
        try {
            synpred3_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred1() {
        backtracking++;
        int start = input.mark();
        try {
            synpred1_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred11() {
        backtracking++;
        int start = input.mark();
        try {
            synpred11_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred5() {
        backtracking++;
        int start = input.mark();
        try {
            synpred5_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred6() {
        backtracking++;
        int start = input.mark();
        try {
            synpred6_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred10() {
        backtracking++;
        int start = input.mark();
        try {
            synpred10_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }


    protected DFA8 dfa8 = new DFA8(this);
    protected DFA9 dfa9 = new DFA9(this);
    protected DFA17 dfa17 = new DFA17(this);
    protected DFA52 dfa52 = new DFA52(this);
    protected DFA64 dfa64 = new DFA64(this);
    protected DFA65 dfa65 = new DFA65(this);
    static final String DFA8_eotS =
        "\6\uffff";
    static final String DFA8_eofS =
        "\6\uffff";
    static final String DFA8_minS =
        "\2\4\1\uffff\1\77\1\uffff\1\4";
    static final String DFA8_maxS =
        "\2\100\1\uffff\1\77\1\uffff\1\100";
    static final String DFA8_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\1\uffff";
    static final String DFA8_specialS =
        "\6\uffff}>";
    static final String[] DFA8_transitionS = {
            "\5\1\1\uffff\1\1\3\uffff\6\1\3\uffff\1\1\1\uffff\1\1\6\uffff"+
            "\2\1\5\uffff\1\1\5\uffff\4\1\2\uffff\5\1\1\uffff\1\1\6\uffff"+
            "\1\1",
            "\7\2\1\uffff\2\4\6\2\3\uffff\1\2\1\uffff\1\2\6\uffff\2\2\5\uffff"+
            "\1\2\5\uffff\4\2\2\uffff\5\2\1\uffff\1\2\4\uffff\1\3\1\uffff"+
            "\1\2",
            "",
            "\1\5",
            "",
            "\5\2\1\uffff\1\2\1\uffff\2\4\6\2\3\uffff\1\2\1\uffff\1\2\6\uffff"+
            "\2\2\5\uffff\1\2\5\uffff\4\2\2\uffff\5\2\1\uffff\1\2\4\uffff"+
            "\1\3\1\uffff\1\2"
    };

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        public String getDescription() {
            return "361:15: (paramType= dotted_name )?";
        }
    }
    static final String DFA9_eotS =
        "\6\uffff";
    static final String DFA9_eofS =
        "\6\uffff";
    static final String DFA9_minS =
        "\2\4\1\77\2\uffff\1\4";
    static final String DFA9_maxS =
        "\2\100\1\77\2\uffff\1\100";
    static final String DFA9_acceptS =
        "\3\uffff\1\2\1\1\1\uffff";
    static final String DFA9_specialS =
        "\6\uffff}>";
    static final String[] DFA9_transitionS = {
            "\5\1\1\uffff\1\1\3\uffff\6\1\3\uffff\1\1\1\uffff\1\1\6\uffff"+
            "\2\1\5\uffff\1\1\5\uffff\4\1\2\uffff\5\1\1\uffff\1\1\6\uffff"+
            "\1\1",
            "\7\4\1\uffff\2\3\6\4\3\uffff\1\4\1\uffff\1\4\6\uffff\2\4\5\uffff"+
            "\1\4\5\uffff\4\4\2\uffff\5\4\1\uffff\1\4\4\uffff\1\2\1\uffff"+
            "\1\4",
            "\1\5",
            "",
            "",
            "\5\4\1\uffff\1\4\1\uffff\2\3\6\4\3\uffff\1\4\1\uffff\1\4\6\uffff"+
            "\2\4\5\uffff\1\4\5\uffff\4\4\2\uffff\5\4\1\uffff\1\4\4\uffff"+
            "\1\2\1\uffff\1\4"
    };

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        public String getDescription() {
            return "366:22: (paramType= dotted_name )?";
        }
    }
    static final String DFA17_eotS =
        "\11\uffff";
    static final String DFA17_eofS =
        "\11\uffff";
    static final String DFA17_minS =
        "\2\10\1\uffff\1\10\1\uffff\1\4\1\77\2\10";
    static final String DFA17_maxS =
        "\2\53\1\uffff\1\115\1\uffff\1\100\1\77\2\76";
    static final String DFA17_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\4\uffff";
    static final String DFA17_specialS =
        "\11\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\2\2\uffff\1\1\3\uffff\1\2\30\uffff\4\2",
            "\1\3\2\uffff\1\2\1\uffff\1\4\25\uffff\1\2\1\uffff\1\2\2\uffff"+
            "\4\2",
            "",
            "\1\4\1\5\1\uffff\1\2\2\4\60\uffff\1\6\16\uffff\1\2",
            "",
            "\5\7\1\uffff\1\7\3\uffff\6\7\3\uffff\1\7\1\uffff\1\7\6\uffff"+
            "\2\7\5\uffff\1\7\5\uffff\4\7\2\uffff\5\7\1\uffff\1\7\6\uffff"+
            "\1\7",
            "\1\10",
            "\1\4\1\5\1\uffff\1\2\62\uffff\1\6",
            "\1\4\2\uffff\1\2\62\uffff\1\6"
    };

    static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
    static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
    static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
    static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
    static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
    static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
    static final short[][] DFA17_transition;

    static {
        int numStates = DFA17_transitionS.length;
        DFA17_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
        }
    }

    class DFA17 extends DFA {

        public DFA17(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 17;
            this.eot = DFA17_eot;
            this.eof = DFA17_eof;
            this.min = DFA17_min;
            this.max = DFA17_max;
            this.accept = DFA17_accept;
            this.special = DFA17_special;
            this.transition = DFA17_transition;
        }
        public String getDescription() {
            return "410:3: ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )?";
        }
    }
    static final String DFA52_eotS =
        "\150\uffff";
    static final String DFA52_eofS =
        "\150\uffff";
    static final String DFA52_minS =
        "\1\10\1\uffff\1\4\1\uffff\2\4\1\0\5\4\2\uffff\4\4\1\0\2\4\1\0\4"+
        "\4\1\0\1\4\1\0\1\4\2\0\3\4\2\0\2\4\3\0\2\4\1\0\1\4\1\0\2\4\3\0\2"+
        "\4\1\0\4\4\1\0\3\4\1\0\1\4\1\0\1\4\1\uffff\44\0";
    static final String DFA52_maxS =
        "\1\113\1\uffff\1\124\1\uffff\2\124\1\0\5\124\2\uffff\4\124\1\0\2"+
        "\124\1\0\4\124\1\0\1\124\1\0\1\124\2\0\3\124\2\0\2\124\3\0\2\124"+
        "\1\0\1\124\1\0\2\124\3\0\2\124\1\0\4\124\1\0\3\124\1\0\1\124\1\0"+
        "\1\124\1\uffff\44\0";
    static final String DFA52_acceptS =
        "\1\uffff\1\1\1\uffff\1\3\10\uffff\2\2\65\uffff\1\2\44\uffff";
    static final String DFA52_specialS =
        "\1\43\1\uffff\1\62\1\uffff\1\15\1\0\1\55\1\66\1\54\1\2\1\22\1\51"+
        "\2\uffff\1\6\1\73\1\4\1\36\1\10\1\52\1\71\1\35\1\3\1\53\1\30\1\61"+
        "\1\56\1\50\1\76\1\47\1\67\1\64\1\31\1\12\1\23\1\17\1\37\1\46\1\45"+
        "\1\41\1\65\1\16\1\72\1\21\1\40\1\74\1\63\1\60\1\57\1\5\1\1\1\11"+
        "\1\75\1\70\1\24\1\7\1\32\1\13\1\25\1\20\1\33\1\14\1\26\1\34\1\27"+
        "\1\44\1\42\45\uffff}>";
    static final String[] DFA52_transitionS = {
            "\2\3\1\uffff\1\2\2\3\1\uffff\1\3\23\uffff\4\3\1\uffff\4\3\1"+
            "\uffff\1\3\20\uffff\1\1\1\uffff\1\3\12\uffff\1\3",
            "",
            "\4\14\1\13\2\14\1\6\1\14\1\15\25\14\1\5\1\14\1\4\2\14\1\7\1"+
            "\10\1\11\1\12\51\14",
            "",
            "\4\14\1\23\2\14\1\22\1\14\1\15\32\14\1\16\1\17\1\20\1\21\51"+
            "\14",
            "\4\14\1\31\2\14\1\25\1\14\1\15\32\14\1\24\1\26\1\27\1\30\51"+
            "\14",
            "\1\uffff",
            "\4\14\1\33\2\14\1\32\1\14\1\15\107\14",
            "\4\14\1\35\2\14\1\34\1\14\1\15\107\14",
            "\7\14\1\36\1\14\1\15\107\14",
            "\7\14\1\37\1\14\1\15\107\14",
            "\5\14\1\41\1\14\1\43\1\14\1\15\60\14\1\42\16\14\1\40\7\14",
            "",
            "",
            "\4\14\1\45\2\14\1\44\1\14\1\15\107\14",
            "\4\14\1\46\2\14\1\47\1\14\1\15\107\14",
            "\7\14\1\50\1\14\1\15\107\14",
            "\7\14\1\51\1\14\1\15\107\14",
            "\1\uffff",
            "\5\14\1\53\1\14\1\54\1\14\1\15\60\14\1\55\16\14\1\52\7\14",
            "\4\14\1\57\2\14\1\56\1\14\1\15\107\14",
            "\1\uffff",
            "\4\14\1\60\2\14\1\61\1\14\1\15\107\14",
            "\7\14\1\62\1\14\1\15\107\14",
            "\7\14\1\63\1\14\1\15\107\14",
            "\5\14\1\65\1\14\1\66\1\14\1\15\60\14\1\67\16\14\1\64\7\14",
            "\1\uffff",
            "\5\14\1\71\1\14\1\73\1\14\1\15\60\14\1\72\16\14\1\70\7\14",
            "\1\uffff",
            "\5\14\1\75\1\14\1\77\1\14\1\15\60\14\1\76\16\14\1\74\7\14",
            "\1\uffff",
            "\1\uffff",
            "\4\14\1\100\2\14\1\101\1\14\1\15\107\14",
            "\5\102\1\14\1\102\1\103\1\14\1\15\6\102\3\14\1\102\1\14\1\102"+
            "\6\14\2\102\5\14\1\102\5\14\4\102\2\14\5\102\1\14\1\102\6\14"+
            "\1\102\24\14",
            "\7\14\1\103\1\14\1\15\61\14\1\104\25\14",
            "\1\uffff",
            "\1\uffff",
            "\5\14\1\106\1\14\1\110\1\14\1\15\60\14\1\107\16\14\1\105\7\14",
            "\5\14\1\112\1\14\1\114\1\14\1\15\60\14\1\113\16\14\1\111\7\14",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\4\14\1\115\2\14\1\116\1\14\1\15\107\14",
            "\5\117\1\14\1\117\1\103\1\14\1\15\6\117\3\14\1\117\1\14\1\117"+
            "\6\14\2\117\5\14\1\117\5\14\4\117\2\14\5\117\1\14\1\117\6\14"+
            "\1\117\24\14",
            "\1\uffff",
            "\7\14\1\103\1\14\1\15\61\14\1\120\25\14",
            "\1\uffff",
            "\5\14\1\122\1\14\1\123\1\14\1\15\60\14\1\124\16\14\1\121\7\14",
            "\5\14\1\126\1\14\1\130\1\14\1\15\60\14\1\127\16\14\1\125\7\14",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\4\14\1\131\2\14\1\132\1\14\1\15\107\14",
            "\5\133\1\14\1\133\1\103\1\14\1\15\6\133\3\14\1\133\1\14\1\133"+
            "\6\14\2\133\5\14\1\133\5\14\4\133\2\14\5\133\1\14\1\133\6\14"+
            "\1\133\24\14",
            "\1\uffff",
            "\7\14\1\103\1\14\1\15\61\14\1\134\25\14",
            "\4\14\1\135\2\14\1\136\1\14\1\15\107\14",
            "\5\137\1\14\1\137\1\103\1\14\1\15\6\137\3\14\1\137\1\14\1\137"+
            "\6\14\2\137\5\14\1\137\5\14\4\137\2\14\5\137\1\14\1\137\6\14"+
            "\1\137\24\14",
            "\7\14\1\103\1\14\1\15\61\14\1\140\25\14",
            "\1\uffff",
            "\4\14\1\141\2\14\1\142\1\14\1\15\107\14",
            "\5\143\1\14\1\143\1\103\1\14\1\15\6\143\3\14\1\143\1\14\1\143"+
            "\6\14\2\143\5\14\1\143\5\14\4\143\2\14\5\143\1\14\1\143\6\14"+
            "\1\143\24\14",
            "\7\14\1\103\1\14\1\15\61\14\1\144\25\14",
            "\1\uffff",
            "\5\14\1\145\1\14\1\146\1\14\1\15\60\14\1\147\26\14",
            "\1\uffff",
            "\5\14\1\41\1\14\1\43\1\14\1\15\60\14\1\42\26\14",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA52_eot = DFA.unpackEncodedString(DFA52_eotS);
    static final short[] DFA52_eof = DFA.unpackEncodedString(DFA52_eofS);
    static final char[] DFA52_min = DFA.unpackEncodedStringToUnsignedChars(DFA52_minS);
    static final char[] DFA52_max = DFA.unpackEncodedStringToUnsignedChars(DFA52_maxS);
    static final short[] DFA52_accept = DFA.unpackEncodedString(DFA52_acceptS);
    static final short[] DFA52_special = DFA.unpackEncodedString(DFA52_specialS);
    static final short[][] DFA52_transition;

    static {
        int numStates = DFA52_transitionS.length;
        DFA52_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA52_transition[i] = DFA.unpackEncodedString(DFA52_transitionS[i]);
        }
    }

    class DFA52 extends DFA {

        public DFA52(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 52;
            this.eot = DFA52_eot;
            this.eof = DFA52_eof;
            this.min = DFA52_min;
            this.max = DFA52_max;
            this.accept = DFA52_accept;
            this.special = DFA52_special;
            this.transition = DFA52_transition;
        }
        public String getDescription() {
            return "1113:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )?";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA52_5 = input.LA(1);

                         
                        int index52_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_5==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA52_5==EXISTS) ) {s = 20;}

                        else if ( (LA52_5==LEFT_PAREN) ) {s = 21;}

                        else if ( (LA52_5==NOT) ) {s = 22;}

                        else if ( (LA52_5==EVAL) ) {s = 23;}

                        else if ( (LA52_5==FORALL) ) {s = 24;}

                        else if ( (LA52_5==ID) ) {s = 25;}

                        else if ( ((LA52_5>=PACKAGE && LA52_5<=EVENT)||(LA52_5>=DOT && LA52_5<=GLOBAL)||LA52_5==COMMA||(LA52_5>=QUERY && LA52_5<=FROM)||(LA52_5>=ACCUMULATE && LA52_5<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_5);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA52_50 = input.LA(1);

                         
                        int index52_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_50);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA52_9 = input.LA(1);

                         
                        int index52_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_9==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_9>=PACKAGE && LA52_9<=GLOBAL)||LA52_9==COMMA||(LA52_9>=QUERY && LA52_9<=84)) && (synpred10())) {s = 12;}

                        else if ( (LA52_9==LEFT_PAREN) ) {s = 30;}

                         
                        input.seek(index52_9);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA52_22 = input.LA(1);

                         
                        int index52_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_22==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA52_22==ID) ) {s = 48;}

                        else if ( (LA52_22==LEFT_PAREN) ) {s = 49;}

                        else if ( ((LA52_22>=PACKAGE && LA52_22<=EVENT)||(LA52_22>=DOT && LA52_22<=GLOBAL)||LA52_22==COMMA||(LA52_22>=QUERY && LA52_22<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_22);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA52_16 = input.LA(1);

                         
                        int index52_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_16==LEFT_PAREN) ) {s = 40;}

                        else if ( (LA52_16==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_16>=PACKAGE && LA52_16<=GLOBAL)||LA52_16==COMMA||(LA52_16>=QUERY && LA52_16<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_16);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA52_49 = input.LA(1);

                         
                        int index52_49 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_49);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA52_14 = input.LA(1);

                         
                        int index52_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_14==LEFT_PAREN) ) {s = 36;}

                        else if ( (LA52_14==ID) ) {s = 37;}

                        else if ( (LA52_14==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_14>=PACKAGE && LA52_14<=EVENT)||(LA52_14>=DOT && LA52_14<=GLOBAL)||LA52_14==COMMA||(LA52_14>=QUERY && LA52_14<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_14);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA52_55 = input.LA(1);

                         
                        int index52_55 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_55==RIGHT_SQUARE) ) {s = 92;}

                        else if ( (LA52_55==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_55>=PACKAGE && LA52_55<=GLOBAL)||LA52_55==COMMA||(LA52_55>=QUERY && LA52_55<=LEFT_SQUARE)||(LA52_55>=THEN && LA52_55<=84)) && (synpred10())) {s = 12;}

                        else if ( (LA52_55==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index52_55);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA52_18 = input.LA(1);

                         
                        int index52_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_18);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA52_51 = input.LA(1);

                         
                        int index52_51 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_51);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA52_33 = input.LA(1);

                         
                        int index52_33 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA52_33>=PACKAGE && LA52_33<=ID)||LA52_33==GLOBAL||(LA52_33>=QUERY && LA52_33<=ATTRIBUTES)||LA52_33==ENABLED||LA52_33==SALIENCE||(LA52_33>=DURATION && LA52_33<=DIALECT)||LA52_33==FROM||(LA52_33>=INIT && LA52_33<=RESULT)||(LA52_33>=CONTAINS && LA52_33<=MEMBEROF)||LA52_33==IN||LA52_33==THEN) ) {s = 66;}

                        else if ( (LA52_33==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA52_33==DOT||LA52_33==COMMA||(LA52_33>=DATE_EFFECTIVE && LA52_33<=DATE_EXPIRES)||LA52_33==BOOL||(LA52_33>=INT && LA52_33<=AGENDA_GROUP)||(LA52_33>=LOCK_ON_ACTIVE && LA52_33<=DOUBLE_AMPER)||(LA52_33>=EXISTS && LA52_33<=ACCUMULATE)||(LA52_33>=COLLECT && LA52_33<=ENTRY_POINT)||LA52_33==TILDE||(LA52_33>=FLOAT && LA52_33<=RIGHT_SQUARE)||(LA52_33>=EOL && LA52_33<=84)) && (synpred10())) {s = 12;}

                        else if ( (LA52_33==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index52_33);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA52_57 = input.LA(1);

                         
                        int index52_57 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA52_57>=PACKAGE && LA52_57<=ID)||LA52_57==GLOBAL||(LA52_57>=QUERY && LA52_57<=ATTRIBUTES)||LA52_57==ENABLED||LA52_57==SALIENCE||(LA52_57>=DURATION && LA52_57<=DIALECT)||LA52_57==FROM||(LA52_57>=INIT && LA52_57<=RESULT)||(LA52_57>=CONTAINS && LA52_57<=MEMBEROF)||LA52_57==IN||LA52_57==THEN) ) {s = 95;}

                        else if ( (LA52_57==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA52_57==DOT||LA52_57==COMMA||(LA52_57>=DATE_EFFECTIVE && LA52_57<=DATE_EXPIRES)||LA52_57==BOOL||(LA52_57>=INT && LA52_57<=AGENDA_GROUP)||(LA52_57>=LOCK_ON_ACTIVE && LA52_57<=DOUBLE_AMPER)||(LA52_57>=EXISTS && LA52_57<=ACCUMULATE)||(LA52_57>=COLLECT && LA52_57<=ENTRY_POINT)||LA52_57==TILDE||(LA52_57>=FLOAT && LA52_57<=RIGHT_SQUARE)||(LA52_57>=EOL && LA52_57<=84)) && (synpred10())) {s = 12;}

                        else if ( (LA52_57==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index52_57);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA52_61 = input.LA(1);

                         
                        int index52_61 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA52_61>=PACKAGE && LA52_61<=ID)||LA52_61==GLOBAL||(LA52_61>=QUERY && LA52_61<=ATTRIBUTES)||LA52_61==ENABLED||LA52_61==SALIENCE||(LA52_61>=DURATION && LA52_61<=DIALECT)||LA52_61==FROM||(LA52_61>=INIT && LA52_61<=RESULT)||(LA52_61>=CONTAINS && LA52_61<=MEMBEROF)||LA52_61==IN||LA52_61==THEN) ) {s = 99;}

                        else if ( (LA52_61==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA52_61==DOT||LA52_61==COMMA||(LA52_61>=DATE_EFFECTIVE && LA52_61<=DATE_EXPIRES)||LA52_61==BOOL||(LA52_61>=INT && LA52_61<=AGENDA_GROUP)||(LA52_61>=LOCK_ON_ACTIVE && LA52_61<=DOUBLE_AMPER)||(LA52_61>=EXISTS && LA52_61<=ACCUMULATE)||(LA52_61>=COLLECT && LA52_61<=ENTRY_POINT)||LA52_61==TILDE||(LA52_61>=FLOAT && LA52_61<=RIGHT_SQUARE)||(LA52_61>=EOL && LA52_61<=84)) && (synpred10())) {s = 12;}

                        else if ( (LA52_61==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index52_61);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA52_4 = input.LA(1);

                         
                        int index52_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_4==EXISTS) ) {s = 14;}

                        else if ( (LA52_4==NOT) ) {s = 15;}

                        else if ( (LA52_4==EVAL) ) {s = 16;}

                        else if ( (LA52_4==FORALL) ) {s = 17;}

                        else if ( (LA52_4==LEFT_PAREN) ) {s = 18;}

                        else if ( (LA52_4==ID) ) {s = 19;}

                        else if ( (LA52_4==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_4>=PACKAGE && LA52_4<=EVENT)||(LA52_4>=DOT && LA52_4<=GLOBAL)||LA52_4==COMMA||(LA52_4>=QUERY && LA52_4<=FROM)||(LA52_4>=ACCUMULATE && LA52_4<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_4);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA52_41 = input.LA(1);

                         
                        int index52_41 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_41);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA52_35 = input.LA(1);

                         
                        int index52_35 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_35);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA52_59 = input.LA(1);

                         
                        int index52_59 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_59);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA52_43 = input.LA(1);

                         
                        int index52_43 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_43==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_43>=PACKAGE && LA52_43<=ID)||LA52_43==GLOBAL||(LA52_43>=QUERY && LA52_43<=ATTRIBUTES)||LA52_43==ENABLED||LA52_43==SALIENCE||(LA52_43>=DURATION && LA52_43<=DIALECT)||LA52_43==FROM||(LA52_43>=INIT && LA52_43<=RESULT)||(LA52_43>=CONTAINS && LA52_43<=MEMBEROF)||LA52_43==IN||LA52_43==THEN) ) {s = 79;}

                        else if ( (LA52_43==LEFT_PAREN) && (synpred10())) {s = 67;}

                        else if ( (LA52_43==DOT||LA52_43==COMMA||(LA52_43>=DATE_EFFECTIVE && LA52_43<=DATE_EXPIRES)||LA52_43==BOOL||(LA52_43>=INT && LA52_43<=AGENDA_GROUP)||(LA52_43>=LOCK_ON_ACTIVE && LA52_43<=DOUBLE_AMPER)||(LA52_43>=EXISTS && LA52_43<=ACCUMULATE)||(LA52_43>=COLLECT && LA52_43<=ENTRY_POINT)||LA52_43==TILDE||(LA52_43>=FLOAT && LA52_43<=RIGHT_SQUARE)||(LA52_43>=EOL && LA52_43<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_43);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA52_10 = input.LA(1);

                         
                        int index52_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_10==LEFT_PAREN) ) {s = 31;}

                        else if ( (LA52_10==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_10>=PACKAGE && LA52_10<=GLOBAL)||LA52_10==COMMA||(LA52_10>=QUERY && LA52_10<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_10);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA52_34 = input.LA(1);

                         
                        int index52_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_34==RIGHT_SQUARE) ) {s = 68;}

                        else if ( (LA52_34==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_34>=PACKAGE && LA52_34<=GLOBAL)||LA52_34==COMMA||(LA52_34>=QUERY && LA52_34<=LEFT_SQUARE)||(LA52_34>=THEN && LA52_34<=84)) && (synpred10())) {s = 12;}

                        else if ( (LA52_34==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index52_34);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA52_54 = input.LA(1);

                         
                        int index52_54 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_54);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA52_58 = input.LA(1);

                         
                        int index52_58 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_58==RIGHT_SQUARE) ) {s = 96;}

                        else if ( (LA52_58==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_58>=PACKAGE && LA52_58<=GLOBAL)||LA52_58==COMMA||(LA52_58>=QUERY && LA52_58<=LEFT_SQUARE)||(LA52_58>=THEN && LA52_58<=84)) && (synpred10())) {s = 12;}

                        else if ( (LA52_58==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index52_58);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA52_62 = input.LA(1);

                         
                        int index52_62 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_62==RIGHT_SQUARE) ) {s = 100;}

                        else if ( (LA52_62==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_62>=PACKAGE && LA52_62<=GLOBAL)||LA52_62==COMMA||(LA52_62>=QUERY && LA52_62<=LEFT_SQUARE)||(LA52_62>=THEN && LA52_62<=84)) && (synpred10())) {s = 12;}

                        else if ( (LA52_62==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index52_62);
                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA52_64 = input.LA(1);

                         
                        int index52_64 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_64==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA52_64==DOT) ) {s = 101;}

                        else if ( (LA52_64==LEFT_PAREN) ) {s = 102;}

                        else if ( (LA52_64==LEFT_SQUARE) ) {s = 103;}

                        else if ( ((LA52_64>=PACKAGE && LA52_64<=ID)||LA52_64==GLOBAL||LA52_64==COMMA||(LA52_64>=QUERY && LA52_64<=RIGHT_CURLY)||(LA52_64>=RIGHT_SQUARE && LA52_64<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_64);
                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA52_24 = input.LA(1);

                         
                        int index52_24 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_24==LEFT_PAREN) ) {s = 51;}

                        else if ( (LA52_24==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_24>=PACKAGE && LA52_24<=GLOBAL)||LA52_24==COMMA||(LA52_24>=QUERY && LA52_24<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_24);
                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA52_32 = input.LA(1);

                         
                        int index52_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_32==ID) ) {s = 64;}

                        else if ( (LA52_32==LEFT_PAREN) ) {s = 65;}

                        else if ( (LA52_32==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_32>=PACKAGE && LA52_32<=EVENT)||(LA52_32>=DOT && LA52_32<=GLOBAL)||LA52_32==COMMA||(LA52_32>=QUERY && LA52_32<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_32);
                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA52_56 = input.LA(1);

                         
                        int index52_56 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_56==ID) ) {s = 93;}

                        else if ( (LA52_56==LEFT_PAREN) ) {s = 94;}

                        else if ( (LA52_56==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_56>=PACKAGE && LA52_56<=EVENT)||(LA52_56>=DOT && LA52_56<=GLOBAL)||LA52_56==COMMA||(LA52_56>=QUERY && LA52_56<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_56);
                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA52_60 = input.LA(1);

                         
                        int index52_60 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_60==ID) ) {s = 97;}

                        else if ( (LA52_60==LEFT_PAREN) ) {s = 98;}

                        else if ( (LA52_60==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_60>=PACKAGE && LA52_60<=EVENT)||(LA52_60>=DOT && LA52_60<=GLOBAL)||LA52_60==COMMA||(LA52_60>=QUERY && LA52_60<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_60);
                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA52_63 = input.LA(1);

                         
                        int index52_63 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_63);
                        if ( s>=0 ) return s;
                        break;
                    case 29 : 
                        int LA52_21 = input.LA(1);

                         
                        int index52_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_21);
                        if ( s>=0 ) return s;
                        break;
                    case 30 : 
                        int LA52_17 = input.LA(1);

                         
                        int index52_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_17==LEFT_PAREN) ) {s = 41;}

                        else if ( (LA52_17==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_17>=PACKAGE && LA52_17<=GLOBAL)||LA52_17==COMMA||(LA52_17>=QUERY && LA52_17<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_17);
                        if ( s>=0 ) return s;
                        break;
                    case 31 : 
                        int LA52_36 = input.LA(1);

                         
                        int index52_36 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_36);
                        if ( s>=0 ) return s;
                        break;
                    case 32 : 
                        int LA52_44 = input.LA(1);

                         
                        int index52_44 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_44);
                        if ( s>=0 ) return s;
                        break;
                    case 33 : 
                        int LA52_39 = input.LA(1);

                         
                        int index52_39 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_39);
                        if ( s>=0 ) return s;
                        break;
                    case 34 : 
                        int LA52_66 = input.LA(1);

                         
                        int index52_66 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_66==LEFT_SQUARE) ) {s = 34;}

                        else if ( (LA52_66==LEFT_PAREN) ) {s = 35;}

                        else if ( (LA52_66==DOT) ) {s = 33;}

                        else if ( (LA52_66==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_66>=PACKAGE && LA52_66<=ID)||LA52_66==GLOBAL||LA52_66==COMMA||(LA52_66>=QUERY && LA52_66<=RIGHT_CURLY)||(LA52_66>=RIGHT_SQUARE && LA52_66<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_66);
                        if ( s>=0 ) return s;
                        break;
                    case 35 : 
                        int LA52_0 = input.LA(1);

                         
                        int index52_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_0==LEFT_SQUARE) && (synpred9())) {s = 1;}

                        else if ( (LA52_0==LEFT_PAREN) ) {s = 2;}

                        else if ( ((LA52_0>=ID && LA52_0<=DOT)||(LA52_0>=COMMA && LA52_0<=RIGHT_PAREN)||LA52_0==END||(LA52_0>=OR && LA52_0<=DOUBLE_AMPER)||(LA52_0>=EXISTS && LA52_0<=FORALL)||LA52_0==INIT||LA52_0==THEN||LA52_0==75) ) {s = 3;}

                         
                        input.seek(index52_0);
                        if ( s>=0 ) return s;
                        break;
                    case 36 : 
                        int LA52_65 = input.LA(1);

                         
                        int index52_65 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_65);
                        if ( s>=0 ) return s;
                        break;
                    case 37 : 
                        int LA52_38 = input.LA(1);

                         
                        int index52_38 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_38==77) ) {s = 73;}

                        else if ( (LA52_38==DOT) ) {s = 74;}

                        else if ( (LA52_38==LEFT_SQUARE) ) {s = 75;}

                        else if ( (LA52_38==LEFT_PAREN) ) {s = 76;}

                        else if ( (LA52_38==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_38>=PACKAGE && LA52_38<=ID)||LA52_38==GLOBAL||LA52_38==COMMA||(LA52_38>=QUERY && LA52_38<=RIGHT_CURLY)||(LA52_38>=RIGHT_SQUARE && LA52_38<=76)||(LA52_38>=78 && LA52_38<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_38);
                        if ( s>=0 ) return s;
                        break;
                    case 38 : 
                        int LA52_37 = input.LA(1);

                         
                        int index52_37 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_37==77) ) {s = 69;}

                        else if ( (LA52_37==DOT) ) {s = 70;}

                        else if ( (LA52_37==LEFT_SQUARE) ) {s = 71;}

                        else if ( (LA52_37==LEFT_PAREN) ) {s = 72;}

                        else if ( (LA52_37==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_37>=PACKAGE && LA52_37<=ID)||LA52_37==GLOBAL||LA52_37==COMMA||(LA52_37>=QUERY && LA52_37<=RIGHT_CURLY)||(LA52_37>=RIGHT_SQUARE && LA52_37<=76)||(LA52_37>=78 && LA52_37<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_37);
                        if ( s>=0 ) return s;
                        break;
                    case 39 : 
                        int LA52_29 = input.LA(1);

                         
                        int index52_29 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_29==77) ) {s = 60;}

                        else if ( (LA52_29==DOT) ) {s = 61;}

                        else if ( (LA52_29==LEFT_SQUARE) ) {s = 62;}

                        else if ( (LA52_29==LEFT_PAREN) ) {s = 63;}

                        else if ( (LA52_29==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_29>=PACKAGE && LA52_29<=ID)||LA52_29==GLOBAL||LA52_29==COMMA||(LA52_29>=QUERY && LA52_29<=RIGHT_CURLY)||(LA52_29>=RIGHT_SQUARE && LA52_29<=76)||(LA52_29>=78 && LA52_29<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_29);
                        if ( s>=0 ) return s;
                        break;
                    case 40 : 
                        int LA52_27 = input.LA(1);

                         
                        int index52_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_27==77) ) {s = 56;}

                        else if ( (LA52_27==DOT) ) {s = 57;}

                        else if ( (LA52_27==LEFT_SQUARE) ) {s = 58;}

                        else if ( (LA52_27==LEFT_PAREN) ) {s = 59;}

                        else if ( (LA52_27==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_27>=PACKAGE && LA52_27<=ID)||LA52_27==GLOBAL||LA52_27==COMMA||(LA52_27>=QUERY && LA52_27<=RIGHT_CURLY)||(LA52_27>=RIGHT_SQUARE && LA52_27<=76)||(LA52_27>=78 && LA52_27<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_27);
                        if ( s>=0 ) return s;
                        break;
                    case 41 : 
                        int LA52_11 = input.LA(1);

                         
                        int index52_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_11==77) ) {s = 32;}

                        else if ( (LA52_11==DOT) ) {s = 33;}

                        else if ( (LA52_11==LEFT_SQUARE) ) {s = 34;}

                        else if ( (LA52_11==LEFT_PAREN) ) {s = 35;}

                        else if ( (LA52_11==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_11>=PACKAGE && LA52_11<=ID)||LA52_11==GLOBAL||LA52_11==COMMA||(LA52_11>=QUERY && LA52_11<=RIGHT_CURLY)||(LA52_11>=RIGHT_SQUARE && LA52_11<=76)||(LA52_11>=78 && LA52_11<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_11);
                        if ( s>=0 ) return s;
                        break;
                    case 42 : 
                        int LA52_19 = input.LA(1);

                         
                        int index52_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_19==77) ) {s = 42;}

                        else if ( (LA52_19==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA52_19==DOT) ) {s = 43;}

                        else if ( (LA52_19==LEFT_PAREN) ) {s = 44;}

                        else if ( (LA52_19==LEFT_SQUARE) ) {s = 45;}

                        else if ( ((LA52_19>=PACKAGE && LA52_19<=ID)||LA52_19==GLOBAL||LA52_19==COMMA||(LA52_19>=QUERY && LA52_19<=RIGHT_CURLY)||(LA52_19>=RIGHT_SQUARE && LA52_19<=76)||(LA52_19>=78 && LA52_19<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_19);
                        if ( s>=0 ) return s;
                        break;
                    case 43 : 
                        int LA52_23 = input.LA(1);

                         
                        int index52_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_23==LEFT_PAREN) ) {s = 50;}

                        else if ( (LA52_23==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_23>=PACKAGE && LA52_23<=GLOBAL)||LA52_23==COMMA||(LA52_23>=QUERY && LA52_23<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_23);
                        if ( s>=0 ) return s;
                        break;
                    case 44 : 
                        int LA52_8 = input.LA(1);

                         
                        int index52_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_8==LEFT_PAREN) ) {s = 28;}

                        else if ( (LA52_8==ID) ) {s = 29;}

                        else if ( (LA52_8==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_8>=PACKAGE && LA52_8<=EVENT)||(LA52_8>=DOT && LA52_8<=GLOBAL)||LA52_8==COMMA||(LA52_8>=QUERY && LA52_8<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_8);
                        if ( s>=0 ) return s;
                        break;
                    case 45 : 
                        int LA52_6 = input.LA(1);

                         
                        int index52_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_6);
                        if ( s>=0 ) return s;
                        break;
                    case 46 : 
                        int LA52_26 = input.LA(1);

                         
                        int index52_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_26);
                        if ( s>=0 ) return s;
                        break;
                    case 47 : 
                        int LA52_48 = input.LA(1);

                         
                        int index52_48 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_48==77) ) {s = 85;}

                        else if ( (LA52_48==DOT) ) {s = 86;}

                        else if ( (LA52_48==LEFT_SQUARE) ) {s = 87;}

                        else if ( (LA52_48==LEFT_PAREN) ) {s = 88;}

                        else if ( (LA52_48==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_48>=PACKAGE && LA52_48<=ID)||LA52_48==GLOBAL||LA52_48==COMMA||(LA52_48>=QUERY && LA52_48<=RIGHT_CURLY)||(LA52_48>=RIGHT_SQUARE && LA52_48<=76)||(LA52_48>=78 && LA52_48<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_48);
                        if ( s>=0 ) return s;
                        break;
                    case 48 : 
                        int LA52_47 = input.LA(1);

                         
                        int index52_47 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_47==77) ) {s = 81;}

                        else if ( (LA52_47==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA52_47==DOT) ) {s = 82;}

                        else if ( (LA52_47==LEFT_PAREN) ) {s = 83;}

                        else if ( (LA52_47==LEFT_SQUARE) ) {s = 84;}

                        else if ( ((LA52_47>=PACKAGE && LA52_47<=ID)||LA52_47==GLOBAL||LA52_47==COMMA||(LA52_47>=QUERY && LA52_47<=RIGHT_CURLY)||(LA52_47>=RIGHT_SQUARE && LA52_47<=76)||(LA52_47>=78 && LA52_47<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_47);
                        if ( s>=0 ) return s;
                        break;
                    case 49 : 
                        int LA52_25 = input.LA(1);

                         
                        int index52_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_25==77) ) {s = 52;}

                        else if ( (LA52_25==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA52_25==DOT) ) {s = 53;}

                        else if ( (LA52_25==LEFT_PAREN) ) {s = 54;}

                        else if ( (LA52_25==LEFT_SQUARE) ) {s = 55;}

                        else if ( ((LA52_25>=PACKAGE && LA52_25<=ID)||LA52_25==GLOBAL||LA52_25==COMMA||(LA52_25>=QUERY && LA52_25<=RIGHT_CURLY)||(LA52_25>=RIGHT_SQUARE && LA52_25<=76)||(LA52_25>=78 && LA52_25<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_25);
                        if ( s>=0 ) return s;
                        break;
                    case 50 : 
                        int LA52_2 = input.LA(1);

                         
                        int index52_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_2==AND) ) {s = 4;}

                        else if ( (LA52_2==OR) ) {s = 5;}

                        else if ( (LA52_2==LEFT_PAREN) ) {s = 6;}

                        else if ( (LA52_2==EXISTS) ) {s = 7;}

                        else if ( (LA52_2==NOT) ) {s = 8;}

                        else if ( (LA52_2==EVAL) ) {s = 9;}

                        else if ( (LA52_2==FORALL) ) {s = 10;}

                        else if ( (LA52_2==ID) ) {s = 11;}

                        else if ( ((LA52_2>=PACKAGE && LA52_2<=EVENT)||(LA52_2>=DOT && LA52_2<=GLOBAL)||LA52_2==COMMA||(LA52_2>=QUERY && LA52_2<=LOCK_ON_ACTIVE)||LA52_2==DOUBLE_PIPE||(LA52_2>=DOUBLE_AMPER && LA52_2<=FROM)||(LA52_2>=ACCUMULATE && LA52_2<=84)) && (synpred10())) {s = 12;}

                        else if ( (LA52_2==RIGHT_PAREN) && (synpred10())) {s = 13;}

                         
                        input.seek(index52_2);
                        if ( s>=0 ) return s;
                        break;
                    case 51 : 
                        int LA52_46 = input.LA(1);

                         
                        int index52_46 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_46);
                        if ( s>=0 ) return s;
                        break;
                    case 52 : 
                        int LA52_31 = input.LA(1);

                         
                        int index52_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_31);
                        if ( s>=0 ) return s;
                        break;
                    case 53 : 
                        int LA52_40 = input.LA(1);

                         
                        int index52_40 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_40);
                        if ( s>=0 ) return s;
                        break;
                    case 54 : 
                        int LA52_7 = input.LA(1);

                         
                        int index52_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_7==LEFT_PAREN) ) {s = 26;}

                        else if ( (LA52_7==ID) ) {s = 27;}

                        else if ( (LA52_7==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_7>=PACKAGE && LA52_7<=EVENT)||(LA52_7>=DOT && LA52_7<=GLOBAL)||LA52_7==COMMA||(LA52_7>=QUERY && LA52_7<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_7);
                        if ( s>=0 ) return s;
                        break;
                    case 55 : 
                        int LA52_30 = input.LA(1);

                         
                        int index52_30 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_30);
                        if ( s>=0 ) return s;
                        break;
                    case 56 : 
                        int LA52_53 = input.LA(1);

                         
                        int index52_53 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_53==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_53>=PACKAGE && LA52_53<=ID)||LA52_53==GLOBAL||(LA52_53>=QUERY && LA52_53<=ATTRIBUTES)||LA52_53==ENABLED||LA52_53==SALIENCE||(LA52_53>=DURATION && LA52_53<=DIALECT)||LA52_53==FROM||(LA52_53>=INIT && LA52_53<=RESULT)||(LA52_53>=CONTAINS && LA52_53<=MEMBEROF)||LA52_53==IN||LA52_53==THEN) ) {s = 91;}

                        else if ( (LA52_53==LEFT_PAREN) && (synpred10())) {s = 67;}

                        else if ( (LA52_53==DOT||LA52_53==COMMA||(LA52_53>=DATE_EFFECTIVE && LA52_53<=DATE_EXPIRES)||LA52_53==BOOL||(LA52_53>=INT && LA52_53<=AGENDA_GROUP)||(LA52_53>=LOCK_ON_ACTIVE && LA52_53<=DOUBLE_AMPER)||(LA52_53>=EXISTS && LA52_53<=ACCUMULATE)||(LA52_53>=COLLECT && LA52_53<=ENTRY_POINT)||LA52_53==TILDE||(LA52_53>=FLOAT && LA52_53<=RIGHT_SQUARE)||(LA52_53>=EOL && LA52_53<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_53);
                        if ( s>=0 ) return s;
                        break;
                    case 57 : 
                        int LA52_20 = input.LA(1);

                         
                        int index52_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_20==LEFT_PAREN) ) {s = 46;}

                        else if ( (LA52_20==ID) ) {s = 47;}

                        else if ( (LA52_20==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_20>=PACKAGE && LA52_20<=EVENT)||(LA52_20>=DOT && LA52_20<=GLOBAL)||LA52_20==COMMA||(LA52_20>=QUERY && LA52_20<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_20);
                        if ( s>=0 ) return s;
                        break;
                    case 58 : 
                        int LA52_42 = input.LA(1);

                         
                        int index52_42 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_42==ID) ) {s = 77;}

                        else if ( (LA52_42==LEFT_PAREN) ) {s = 78;}

                        else if ( (LA52_42==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_42>=PACKAGE && LA52_42<=EVENT)||(LA52_42>=DOT && LA52_42<=GLOBAL)||LA52_42==COMMA||(LA52_42>=QUERY && LA52_42<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_42);
                        if ( s>=0 ) return s;
                        break;
                    case 59 : 
                        int LA52_15 = input.LA(1);

                         
                        int index52_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_15==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA52_15==ID) ) {s = 38;}

                        else if ( (LA52_15==LEFT_PAREN) ) {s = 39;}

                        else if ( ((LA52_15>=PACKAGE && LA52_15<=EVENT)||(LA52_15>=DOT && LA52_15<=GLOBAL)||LA52_15==COMMA||(LA52_15>=QUERY && LA52_15<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_15);
                        if ( s>=0 ) return s;
                        break;
                    case 60 : 
                        int LA52_45 = input.LA(1);

                         
                        int index52_45 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_45==RIGHT_SQUARE) ) {s = 80;}

                        else if ( (LA52_45==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_45>=PACKAGE && LA52_45<=GLOBAL)||LA52_45==COMMA||(LA52_45>=QUERY && LA52_45<=LEFT_SQUARE)||(LA52_45>=THEN && LA52_45<=84)) && (synpred10())) {s = 12;}

                        else if ( (LA52_45==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index52_45);
                        if ( s>=0 ) return s;
                        break;
                    case 61 : 
                        int LA52_52 = input.LA(1);

                         
                        int index52_52 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_52==ID) ) {s = 89;}

                        else if ( (LA52_52==LEFT_PAREN) ) {s = 90;}

                        else if ( (LA52_52==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA52_52>=PACKAGE && LA52_52<=EVENT)||(LA52_52>=DOT && LA52_52<=GLOBAL)||LA52_52==COMMA||(LA52_52>=QUERY && LA52_52<=84)) && (synpred10())) {s = 12;}

                         
                        input.seek(index52_52);
                        if ( s>=0 ) return s;
                        break;
                    case 62 : 
                        int LA52_28 = input.LA(1);

                         
                        int index52_28 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index52_28);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 52, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA64_eotS =
        "\23\uffff";
    static final String DFA64_eofS =
        "\23\uffff";
    static final String DFA64_minS =
        "\1\14\1\uffff\1\4\1\11\1\0\1\4\1\uffff\1\4\13\0";
    static final String DFA64_maxS =
        "\1\46\1\uffff\2\124\1\0\1\124\1\uffff\1\124\13\0";
    static final String DFA64_acceptS =
        "\1\uffff\1\2\4\uffff\1\1\14\uffff";
    static final String DFA64_specialS =
        "\4\uffff\1\0\16\uffff}>";
    static final String[] DFA64_transitionS = {
            "\2\1\26\uffff\1\2\1\uffff\1\1",
            "",
            "\5\1\1\uffff\1\1\1\4\2\uffff\6\1\3\uffff\1\1\1\uffff\1\1\6\uffff"+
            "\2\1\5\uffff\1\1\1\uffff\1\6\1\1\2\uffff\4\1\2\uffff\1\5\1\7"+
            "\1\10\1\11\1\12\1\6\1\3\6\uffff\1\1\16\uffff\6\6",
            "\1\1\1\uffff\1\13\35\uffff\1\1\11\uffff\7\1\4\uffff\1\1\20\uffff"+
            "\6\1",
            "\1\uffff",
            "\5\6\1\1\1\6\1\15\2\uffff\6\6\1\uffff\1\6\1\uffff\4\6\5\uffff"+
            "\2\6\5\uffff\1\6\1\uffff\1\1\3\uffff\4\6\2\uffff\1\16\1\17\1"+
            "\20\1\21\1\22\1\1\1\14\2\6\2\uffff\1\1\1\uffff\1\6\16\uffff"+
            "\6\1",
            "",
            "\5\6\1\1\1\6\1\15\2\uffff\6\6\1\uffff\1\6\1\uffff\4\6\5\uffff"+
            "\2\6\5\uffff\1\6\1\uffff\1\1\3\uffff\4\6\2\uffff\1\16\1\17\1"+
            "\20\1\21\1\22\1\1\1\14\2\6\2\uffff\1\1\1\uffff\1\6\16\uffff"+
            "\6\1",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA64_eot = DFA.unpackEncodedString(DFA64_eotS);
    static final short[] DFA64_eof = DFA.unpackEncodedString(DFA64_eofS);
    static final char[] DFA64_min = DFA.unpackEncodedStringToUnsignedChars(DFA64_minS);
    static final char[] DFA64_max = DFA.unpackEncodedStringToUnsignedChars(DFA64_maxS);
    static final short[] DFA64_accept = DFA.unpackEncodedString(DFA64_acceptS);
    static final short[] DFA64_special = DFA.unpackEncodedString(DFA64_specialS);
    static final short[][] DFA64_transition;

    static {
        int numStates = DFA64_transitionS.length;
        DFA64_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA64_transition[i] = DFA.unpackEncodedString(DFA64_transitionS[i]);
        }
    }

    class DFA64 extends DFA {

        public DFA64(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 64;
            this.eot = DFA64_eot;
            this.eof = DFA64_eof;
            this.min = DFA64_min;
            this.max = DFA64_max;
            this.accept = DFA64_accept;
            this.special = DFA64_special;
            this.transition = DFA64_transition;
        }
        public String getDescription() {
            return "()* loopback of 1434:3: ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )*";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA64_4 = input.LA(1);

                         
                        int index64_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11()) ) {s = 6;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index64_4);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 64, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA65_eotS =
        "\32\uffff";
    static final String DFA65_eofS =
        "\1\1\31\uffff";
    static final String DFA65_minS =
        "\1\14\1\uffff\1\4\1\11\2\4\1\uffff\1\4\22\0";
    static final String DFA65_maxS =
        "\1\46\1\uffff\4\124\1\uffff\1\124\22\0";
    static final String DFA65_acceptS =
        "\1\uffff\1\2\4\uffff\1\1\23\uffff";
    static final String DFA65_specialS =
        "\32\uffff}>";
    static final String[] DFA65_transitionS = {
            "\2\1\26\uffff\1\1\1\uffff\1\2",
            "",
            "\5\1\1\uffff\1\1\1\4\2\uffff\6\1\3\uffff\1\1\1\uffff\1\1\6\uffff"+
            "\2\1\5\uffff\1\1\1\uffff\1\6\1\1\2\uffff\4\1\2\uffff\1\5\1\7"+
            "\1\10\1\11\1\12\1\6\1\3\6\uffff\1\1\16\uffff\6\6",
            "\1\1\1\uffff\1\13\35\uffff\1\1\11\uffff\7\1\4\uffff\1\1\20\uffff"+
            "\6\1",
            "\5\1\1\uffff\1\1\1\22\2\uffff\6\1\3\uffff\1\1\1\uffff\1\1\6"+
            "\uffff\2\1\5\uffff\1\1\1\uffff\1\6\1\1\2\uffff\4\1\2\uffff\1"+
            "\15\1\16\1\17\1\20\1\21\1\6\1\14\6\uffff\1\1\16\uffff\6\6",
            "\5\6\1\1\1\6\1\31\2\uffff\6\6\1\uffff\1\6\1\uffff\4\6\5\uffff"+
            "\2\6\5\uffff\1\6\1\uffff\1\1\3\uffff\4\6\2\uffff\1\24\1\25\1"+
            "\26\1\27\1\30\1\1\1\23\2\6\2\uffff\1\1\1\uffff\1\6\16\uffff"+
            "\6\1",
            "",
            "\5\6\1\1\1\6\1\31\2\uffff\6\6\1\uffff\1\6\1\uffff\4\6\5\uffff"+
            "\2\6\5\uffff\1\6\1\uffff\1\1\3\uffff\4\6\2\uffff\1\24\1\25\1"+
            "\26\1\27\1\30\1\1\1\23\2\6\2\uffff\1\1\1\uffff\1\6\16\uffff"+
            "\6\1",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA65_eot = DFA.unpackEncodedString(DFA65_eotS);
    static final short[] DFA65_eof = DFA.unpackEncodedString(DFA65_eofS);
    static final char[] DFA65_min = DFA.unpackEncodedStringToUnsignedChars(DFA65_minS);
    static final char[] DFA65_max = DFA.unpackEncodedStringToUnsignedChars(DFA65_maxS);
    static final short[] DFA65_accept = DFA.unpackEncodedString(DFA65_acceptS);
    static final short[] DFA65_special = DFA.unpackEncodedString(DFA65_specialS);
    static final short[][] DFA65_transition;

    static {
        int numStates = DFA65_transitionS.length;
        DFA65_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA65_transition[i] = DFA.unpackEncodedString(DFA65_transitionS[i]);
        }
    }

    class DFA65 extends DFA {

        public DFA65(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 65;
            this.eot = DFA65_eot;
            this.eof = DFA65_eof;
            this.min = DFA65_min;
            this.max = DFA65_max;
            this.accept = DFA65_accept;
            this.special = DFA65_special;
            this.transition = DFA65_transition;
        }
        public String getDescription() {
            return "()* loopback of 1457:3: ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )*";
        }
    }
 

    public static final BitSet FOLLOW_75_in_opt_semicolon39 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit57 = new BitSet(new long[]{0x00000007FAD34460L});
    public static final BitSet FOLLOW_statement_in_compilation_unit62 = new BitSet(new long[]{0x00000007FAD34460L});
    public static final BitSet FOLLOW_EOF_in_compilation_unit67 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_package_statement_in_prolog96 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_attribute_in_statement121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_event_import_statement_in_statement134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement139 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement151 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_package_statement212 = new BitSet(new long[]{0x02F9E083028FC5F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement216 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_opt_semicolon_in_package_statement218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_statement249 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_import_name_in_import_statement272 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_opt_semicolon_in_import_statement275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_function_import_statement299 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_FUNCTION_in_function_import_statement301 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement324 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_opt_semicolon_in_function_import_statement327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_event_import_statement351 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_EVENT_in_event_import_statement353 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_import_name_in_event_import_statement376 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_opt_semicolon_in_event_import_statement379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_name405 = new BitSet(new long[]{0x0000000000000202L,0x0000000000001000L});
    public static final BitSet FOLLOW_DOT_in_import_name417 = new BitSet(new long[]{0x02F9E083028FC5F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_identifier_in_import_name421 = new BitSet(new long[]{0x0000000000000202L,0x0000000000001000L});
    public static final BitSet FOLLOW_76_in_import_name445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GLOBAL_in_global479 = new BitSet(new long[]{0x02F9E083028FC5F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_dotted_name_in_global490 = new BitSet(new long[]{0x02F9E083028FC5F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_identifier_in_global501 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_opt_semicolon_in_global503 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function528 = new BitSet(new long[]{0x02F9E083028FC5F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_dotted_name_in_function532 = new BitSet(new long[]{0x02F9E083028FC5F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_identifier_in_function537 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function546 = new BitSet(new long[]{0x02F9E083028FE5F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_dotted_name_in_function555 = new BitSet(new long[]{0x02F9E083028FC5F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_argument_in_function560 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_COMMA_in_function574 = new BitSet(new long[]{0x02F9E083028FC5F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_dotted_name_in_function578 = new BitSet(new long[]{0x02F9E083028FC5F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_argument_in_function583 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function607 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_curly_chunk_in_function613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_argument640 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument646 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument648 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_query678 = new BitSet(new long[]{0x0000000000200100L});
    public static final BitSet FOLLOW_name_in_query682 = new BitSet(new long[]{0x00000F0000008900L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_query692 = new BitSet(new long[]{0x0000000000002100L});
    public static final BitSet FOLLOW_qualified_id_in_query727 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_query732 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_COMMA_in_query753 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_qualified_id_in_query757 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_query762 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_query812 = new BitSet(new long[]{0x00000F0000008900L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query841 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_END_in_query846 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_opt_semicolon_in_query848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_template876 = new BitSet(new long[]{0x0000000000200100L});
    public static final BitSet FOLLOW_name_in_template880 = new BitSet(new long[]{0x0000000000000100L,0x0000000000000800L});
    public static final BitSet FOLLOW_opt_semicolon_in_template882 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_template_slot_in_template897 = new BitSet(new long[]{0x0000000000008100L});
    public static final BitSet FOLLOW_END_in_template912 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_opt_semicolon_in_template914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualified_id_in_template_slot960 = new BitSet(new long[]{0x02F9E083028FC5F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_identifier_in_template_slot976 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_opt_semicolon_in_template_slot978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule1009 = new BitSet(new long[]{0x0000000000200100L});
    public static final BitSet FOLLOW_name_in_rule1013 = new BitSet(new long[]{0x00000007FADC0000L,0x0000000000000001L});
    public static final BitSet FOLLOW_rule_attributes_in_rule1022 = new BitSet(new long[]{0x0000000000040000L,0x0000000000000001L});
    public static final BitSet FOLLOW_WHEN_in_rule1034 = new BitSet(new long[]{0x00000F0000000900L,0x0000000000002001L});
    public static final BitSet FOLLOW_77_in_rule1036 = new BitSet(new long[]{0x00000F0000000900L,0x0000000000000001L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule1047 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_rhs_chunk_in_rule1057 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_rule_attributes1077 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_77_in_rule_attributes1079 = new BitSet(new long[]{0x00000007FAD00000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1087 = new BitSet(new long[]{0x00000007FAD01002L});
    public static final BitSet FOLLOW_COMMA_in_rule_attributes1094 = new BitSet(new long[]{0x00000007FAD00000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1099 = new BitSet(new long[]{0x00000007FAD01002L});
    public static final BitSet FOLLOW_salience_in_rule_attribute1136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute1144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute1153 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute1162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute1171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute1187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute1195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute1203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_in_rule_attribute1211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_in_rule_attribute1219 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_in_rule_attribute1226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EFFECTIVE_in_date_effective1252 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_STRING_in_date_effective1254 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EXPIRES_in_date_expires1283 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_STRING_in_date_expires1285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENABLED_in_enabled1314 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_BOOL_in_enabled1316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_salience1349 = new BitSet(new long[]{0x0000000004000800L});
    public static final BitSet FOLLOW_INT_in_salience1360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_salience1375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NO_LOOP_in_no_loop1405 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AUTO_FOCUS_in_auto_focus1453 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTIVATION_GROUP_in_activation_group1502 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_STRING_in_activation_group1504 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1532 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_STRING_in_ruleflow_group1534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AGENDA_GROUP_in_agenda_group1562 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DURATION_in_duration1592 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_INT_in_duration1594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIALECT_in_dialect1622 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_STRING_in_dialect1624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1656 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_BOOL_in_lock_on_active1669 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1708 = new BitSet(new long[]{0x00000F0000000902L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1745 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_or1770 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_OR_in_lhs_or1772 = new BitSet(new long[]{0x00000F0000000900L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1785 = new BitSet(new long[]{0x00000F0000002900L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_or1796 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1814 = new BitSet(new long[]{0x0000001800000002L});
    public static final BitSet FOLLOW_set_in_lhs_or1822 = new BitSet(new long[]{0x00000F0000000900L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1838 = new BitSet(new long[]{0x0000001800000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_and1869 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_AND_in_lhs_and1871 = new BitSet(new long[]{0x00000F0000000900L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1883 = new BitSet(new long[]{0x00000F0000002900L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_and1893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1911 = new BitSet(new long[]{0x0000006000000002L});
    public static final BitSet FOLLOW_set_in_lhs_and1919 = new BitSet(new long[]{0x00000F0000000900L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1935 = new BitSet(new long[]{0x0000006000000002L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary1980 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary1998 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary2017 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary2036 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary2053 = new BitSet(new long[]{0x00000F0000000900L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_unary2057 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary2059 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_pattern_source_in_lhs_unary2070 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_opt_semicolon_in_lhs_unary2082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_pattern_source2109 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_FROM_in_pattern_source2160 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_entrypoint_statement_in_pattern_source2164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_pattern_source2184 = new BitSet(new long[]{0x02FBF083028FC5F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_accumulate_statement_in_pattern_source2242 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collect_statement_in_pattern_source2265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_statement_in_pattern_source2302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_lhs_exist2345 = new BitSet(new long[]{0x0000000000000900L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist2365 = new BitSet(new long[]{0x00000F0000000900L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist2369 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist2399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_exist2449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_lhs_not2501 = new BitSet(new long[]{0x0000000000000900L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not2514 = new BitSet(new long[]{0x00000F0000000900L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not2518 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not2549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_not2586 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_lhs_eval2632 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval2643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_lhs_forall2669 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall2671 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall2675 = new BitSet(new long[]{0x0000000000002100L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall2690 = new BitSet(new long[]{0x0000000000002100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall2706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_pattern2739 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_pattern2747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_source_in_from_statement2774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_accumulate_statement2811 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement2821 = new BitSet(new long[]{0x00000F0000000900L});
    public static final BitSet FOLLOW_lhs_or_in_accumulate_statement2825 = new BitSet(new long[]{0x0000200000001100L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2827 = new BitSet(new long[]{0x0000200000000100L});
    public static final BitSet FOLLOW_INIT_in_accumulate_statement2845 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2858 = new BitSet(new long[]{0x0000400000001000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2860 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_ACTION_in_accumulate_statement2871 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2875 = new BitSet(new long[]{0x0001800000001000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2877 = new BitSet(new long[]{0x0001800000000000L});
    public static final BitSet FOLLOW_REVERSE_in_accumulate_statement2890 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2894 = new BitSet(new long[]{0x0001000000001000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2896 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_RESULT_in_accumulate_statement2913 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2917 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_ID_in_accumulate_statement2943 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2947 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement2964 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_from_source2995 = new BitSet(new long[]{0x0000000000000A02L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source3023 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_expression_chain_in_from_source3036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_expression_chain3067 = new BitSet(new long[]{0x02F9E083028FC5F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_identifier_in_expression_chain3071 = new BitSet(new long[]{0x4000000000000A02L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain3102 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain3135 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain3150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_collect_statement3201 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement3211 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_pattern_source_in_collect_statement3215 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement3217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENTRY_POINT_in_entrypoint_statement3254 = new BitSet(new long[]{0x0000000000200100L});
    public static final BitSet FOLLOW_name_in_entrypoint_statement3266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding3298 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_77_in_fact_binding3300 = new BitSet(new long[]{0x0000000000000900L});
    public static final BitSet FOLLOW_fact_in_fact_binding3314 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_binding3330 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_fact_in_fact_binding3334 = new BitSet(new long[]{0x0000001800002000L});
    public static final BitSet FOLLOW_set_in_fact_binding3347 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_fact_in_fact_binding3359 = new BitSet(new long[]{0x0000001800002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_binding3377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualified_id_in_fact3432 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact3442 = new BitSet(new long[]{0x02F9E483028FEDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_constraints_in_fact3454 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact3461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EOF_in_fact3470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints3488 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_COMMA_in_constraints3495 = new BitSet(new long[]{0x02F9E483028FCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_constraint_in_constraints3504 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_or_constr_in_constraint3537 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3560 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_constr3568 = new BitSet(new long[]{0x02F9E483028FCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3577 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3609 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_constr3617 = new BitSet(new long[]{0x02F9E483028FCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3626 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_field_constraint_in_unary_constr3654 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_unary_constr3662 = new BitSet(new long[]{0x02F9E483028FCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_or_constr_in_unary_constr3664 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_unary_constr3667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_unary_constr3673 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_predicate_in_unary_constr3675 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_field_constraint3714 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_77_in_field_constraint3716 = new BitSet(new long[]{0x02F9E083028FC5F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint3735 = new BitSet(new long[]{0x03F8020000000802L,0x00000000001FC000L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint3749 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_field_constraint3764 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_predicate_in_field_constraint3766 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint3792 = new BitSet(new long[]{0x03F8020000000800L,0x00000000001F8000L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint3801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective3848 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_restr_connective3872 = new BitSet(new long[]{0x03F8020000000800L,0x00000000001F8000L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective3883 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective3915 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_restr_connective3935 = new BitSet(new long[]{0x03F8020000000800L,0x00000000001F8000L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective3946 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_compound_operator_in_constraint_expression3983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_operator_in_constraint_expression3990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_constraint_expression3998 = new BitSet(new long[]{0x03F8020000000800L,0x00000000001F8000L});
    public static final BitSet FOLLOW_or_restr_connective_in_constraint_expression4007 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_constraint_expression4012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_simple_operator4043 = new BitSet(new long[]{0x0EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_80_in_simple_operator4051 = new BitSet(new long[]{0x0EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_81_in_simple_operator4059 = new BitSet(new long[]{0x0EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_82_in_simple_operator4067 = new BitSet(new long[]{0x0EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_83_in_simple_operator4075 = new BitSet(new long[]{0x0EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_84_in_simple_operator4083 = new BitSet(new long[]{0x0EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_CONTAINS_in_simple_operator4111 = new BitSet(new long[]{0x0EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_NOT_in_simple_operator4139 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_CONTAINS_in_simple_operator4143 = new BitSet(new long[]{0x0EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_EXCLUDES_in_simple_operator4171 = new BitSet(new long[]{0x0EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_MATCHES_in_simple_operator4199 = new BitSet(new long[]{0x0EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_SOUNDSLIKE_in_simple_operator4227 = new BitSet(new long[]{0x0EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_NOT_in_simple_operator4255 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_MATCHES_in_simple_operator4259 = new BitSet(new long[]{0x0EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_MEMBEROF_in_simple_operator4287 = new BitSet(new long[]{0x0EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_NOT_in_simple_operator4315 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_MEMBEROF_in_simple_operator4319 = new BitSet(new long[]{0x0EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_TILDE_in_simple_operator4325 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_simple_operator4329 = new BitSet(new long[]{0x4EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_square_chunk_in_simple_operator4333 = new BitSet(new long[]{0x0EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_NOT_in_simple_operator4342 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_TILDE_in_simple_operator4344 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_simple_operator4348 = new BitSet(new long[]{0x4EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_square_chunk_in_simple_operator4352 = new BitSet(new long[]{0x0EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_value_in_simple_operator4367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_compound_operator4397 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_NOT_in_compound_operator4409 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_IN_in_compound_operator4411 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_compound_operator4426 = new BitSet(new long[]{0x0EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator4430 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_COMMA_in_compound_operator4437 = new BitSet(new long[]{0x0EF9E08307AFCDF0L,0x0000000000000001L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator4441 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_compound_operator4450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_expression_value4484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_expression_value4504 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_value4518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint4561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint4572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint4585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint4596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal_constraint4608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate4646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk4664 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000001FFFFFL});
    public static final BitSet FOLLOW_set_in_curly_chunk4668 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000001FFFFFL});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk4677 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000001FFFFFL});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk4682 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk4696 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000001FFFFFL});
    public static final BitSet FOLLOW_set_in_paren_chunk4700 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000001FFFFFL});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk4709 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000001FFFFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk4714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk4727 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000001FFFFFL});
    public static final BitSet FOLLOW_set_in_square_chunk4731 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000001FFFFFL});
    public static final BitSet FOLLOW_square_chunk_in_square_chunk4740 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000001FFFFFL});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk4745 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_qualified_id4774 = new BitSet(new long[]{0x4000000000000202L});
    public static final BitSet FOLLOW_DOT_in_qualified_id4780 = new BitSet(new long[]{0x02F9E083028FC5F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_identifier_in_qualified_id4782 = new BitSet(new long[]{0x4000000000000202L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_qualified_id4791 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_qualified_id4793 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_identifier_in_dotted_name4827 = new BitSet(new long[]{0x4000000000000202L});
    public static final BitSet FOLLOW_DOT_in_dotted_name4833 = new BitSet(new long[]{0x02F9E083028FC5F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_identifier_in_dotted_name4837 = new BitSet(new long[]{0x4000000000000202L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dotted_name4846 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dotted_name4848 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path4882 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_DOT_in_accessor_path4888 = new BitSet(new long[]{0x02F9E083028FC5F0L,0x0000000000000001L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path4892 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_identifier_in_accessor_element4930 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_square_chunk_in_accessor_element4937 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk4958 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000001FFFFFL});
    public static final BitSet FOLLOW_set_in_rhs_chunk4966 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000001FFFFFL});
    public static final BitSet FOLLOW_END_in_rhs_chunk4990 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_opt_semicolon_in_rhs_chunk4992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_name5026 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_name5034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_identifier0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_synpred11972 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_synpred21990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_synpred32009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_synpred42028 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred52047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_synpred62153 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_ENTRY_POINT_in_synpred62155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred93094 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred103127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred113872 = new BitSet(new long[]{0x03F8020000000800L,0x00000000001F8000L});
    public static final BitSet FOLLOW_and_restr_connective_in_synpred113883 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred123935 = new BitSet(new long[]{0x03F8020000000800L,0x00000000001F8000L});
    public static final BitSet FOLLOW_constraint_expression_in_synpred123946 = new BitSet(new long[]{0x0000000000000002L});

}