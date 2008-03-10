// $ANTLR 3.0.1 /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g 2008-03-10 18:35:53

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PACKAGE", "IMPORT", "FUNCTION", "ID", "DOT", "GLOBAL", "LEFT_PAREN", "COMMA", "RIGHT_PAREN", "DECLARE", "LEFT_CURLY", "RIGHT_CURLY", "STRING", "QUERY", "END", "TEMPLATE", "RULE", "WHEN", "ATTRIBUTES", "DATE_EFFECTIVE", "DATE_EXPIRES", "ENABLED", "BOOL", "SALIENCE", "INT", "NO_LOOP", "AUTO_FOCUS", "ACTIVATION_GROUP", "RULEFLOW_GROUP", "AGENDA_GROUP", "DURATION", "DIALECT", "LOCK_ON_ACTIVE", "OR", "DOUBLE_PIPE", "AND", "DOUBLE_AMPER", "FROM", "EXISTS", "NOT", "EVAL", "FORALL", "ACCUMULATE", "INIT", "ACTION", "REVERSE", "RESULT", "COLLECT", "ENTRY_POINT", "CONTAINS", "EXCLUDES", "MATCHES", "SOUNDSLIKE", "MEMBEROF", "TILDE", "IN", "FLOAT", "NULL", "LEFT_SQUARE", "RIGHT_SQUARE", "THEN", "EVENT", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "MISC", "';'", "'.*'", "':'", "'->'", "'=='", "'>'", "'>='", "'<'", "'<='", "'!='"
    };
    public static final int ACCUMULATE=46;
    public static final int PACKAGE=4;
    public static final int FUNCTION=6;
    public static final int RIGHT_SQUARE=63;
    public static final int ACTIVATION_GROUP=31;
    public static final int RIGHT_CURLY=15;
    public static final int ATTRIBUTES=22;
    public static final int DIALECT=35;
    public static final int CONTAINS=53;
    public static final int NO_LOOP=29;
    public static final int MEMBEROF=57;
    public static final int LOCK_ON_ACTIVE=36;
    public static final int AGENDA_GROUP=33;
    public static final int FLOAT=60;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=72;
    public static final int NOT=43;
    public static final int ID=7;
    public static final int AND=39;
    public static final int EOF=-1;
    public static final int HexDigit=69;
    public static final int DATE_EFFECTIVE=23;
    public static final int ACTION=48;
    public static final int DOUBLE_PIPE=38;
    public static final int RIGHT_PAREN=12;
    public static final int IMPORT=5;
    public static final int EOL=66;
    public static final int DOUBLE_AMPER=40;
    public static final int THEN=64;
    public static final int IN=59;
    public static final int MATCHES=55;
    public static final int COMMA=11;
    public static final int TILDE=58;
    public static final int ENABLED=25;
    public static final int EXISTS=42;
    public static final int DOT=8;
    public static final int RULE=20;
    public static final int EXCLUDES=54;
    public static final int AUTO_FOCUS=30;
    public static final int NULL=61;
    public static final int SOUNDSLIKE=56;
    public static final int BOOL=26;
    public static final int FORALL=45;
    public static final int SALIENCE=27;
    public static final int RULEFLOW_GROUP=32;
    public static final int RESULT=50;
    public static final int INT=28;
    public static final int EVENT=65;
    public static final int MULTI_LINE_COMMENT=74;
    public static final int DURATION=34;
    public static final int WS=67;
    public static final int EVAL=44;
    public static final int TEMPLATE=19;
    public static final int WHEN=21;
    public static final int UnicodeEscape=70;
    public static final int ENTRY_POINT=52;
    public static final int LEFT_CURLY=14;
    public static final int OR=37;
    public static final int LEFT_PAREN=10;
    public static final int QUERY=17;
    public static final int DECLARE=13;
    public static final int MISC=75;
    public static final int REVERSE=49;
    public static final int END=18;
    public static final int GLOBAL=9;
    public static final int FROM=41;
    public static final int COLLECT=51;
    public static final int LEFT_SQUARE=62;
    public static final int INIT=47;
    public static final int OctalEscape=71;
    public static final int EscapeSequence=68;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=73;
    public static final int STRING=16;
    public static final int DATE_EXPIRES=24;

        public DRLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[86+1];
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

            if ( (LA1_0==76) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:196:4: ';'
                    {
                    match(input,76,FOLLOW_76_in_opt_semicolon39); if (failed) return ;

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

                if ( ((LA2_0>=IMPORT && LA2_0<=FUNCTION)||LA2_0==GLOBAL||LA2_0==DECLARE||LA2_0==QUERY||(LA2_0>=TEMPLATE && LA2_0<=RULE)||(LA2_0>=DATE_EFFECTIVE && LA2_0<=ENABLED)||LA2_0==SALIENCE||(LA2_0>=NO_LOOP && LA2_0<=LOCK_ON_ACTIVE)) ) {
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:223:1: statement : (a= rule_attribute | function_import_statement | import_statement | global | function | t= template | r= rule | q= query | d= type_declaration );
    public final void statement() throws RecognitionException {
        AttributeDescr a = null;

        FactTemplateDescr t = null;

        RuleDescr r = null;

        QueryDescr q = null;

        TypeDeclarationDescr d = null;


        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:224:2: (a= rule_attribute | function_import_statement | import_statement | global | function | t= template | r= rule | q= query | d= type_declaration )
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
                int LA4_2 = input.LA(2);

                if ( (LA4_2==FUNCTION) ) {
                    alt4=2;
                }
                else if ( (LA4_2==ID) ) {
                    alt4=3;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("223:1: statement : (a= rule_attribute | function_import_statement | import_statement | global | function | t= template | r= rule | q= query | d= type_declaration );", 4, 2, input);

                    throw nvae;
                }
                }
                break;
            case GLOBAL:
                {
                alt4=4;
                }
                break;
            case FUNCTION:
                {
                alt4=5;
                }
                break;
            case TEMPLATE:
                {
                alt4=6;
                }
                break;
            case RULE:
                {
                alt4=7;
                }
                break;
            case QUERY:
                {
                alt4=8;
                }
                break;
            case DECLARE:
                {
                alt4=9;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("223:1: statement : (a= rule_attribute | function_import_statement | import_statement | global | function | t= template | r= rule | q= query | d= type_declaration );", 4, 0, input);

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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:226:4: import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement134);
                    import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:227:4: global
                    {
                    pushFollow(FOLLOW_global_in_statement140);
                    global();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:228:4: function
                    {
                    pushFollow(FOLLOW_function_in_statement146);
                    function();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:229:10: t= template
                    {
                    pushFollow(FOLLOW_template_in_statement160);
                    t=template();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       this.packageDescr.addFactTemplate( t ); 
                    }

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:230:4: r= rule
                    {
                    pushFollow(FOLLOW_rule_in_statement169);
                    r=rule();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       this.packageDescr.addRule( r ); 
                    }

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:231:4: q= query
                    {
                    pushFollow(FOLLOW_query_in_statement181);
                    q=query();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       this.packageDescr.addRule( q ); 
                    }

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:232:10: d= type_declaration
                    {
                    pushFollow(FOLLOW_type_declaration_in_statement196);
                    d=type_declaration();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       this.packageDescr.addTypeDeclaration( d ); 
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
            match(input,PACKAGE,FOLLOW_PACKAGE_in_package_statement222); if (failed) return packageName;
            pushFollow(FOLLOW_dotted_name_in_package_statement226);
            n=dotted_name();
            _fsp--;
            if (failed) return packageName;
            pushFollow(FOLLOW_opt_semicolon_in_package_statement228);
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
            match(input,IMPORT,FOLLOW_IMPORT_in_import_statement259); if (failed) return ;
            if ( backtracking==0 ) {

              	            importDecl = factory.createImport( );
              	            importDecl.setStartCharacter( ((CommonToken)IMPORT1).getStartIndex() );
              		    if (packageDescr != null) {
              			packageDescr.addImport( importDecl );
              		    }
              	        
            }
            pushFollow(FOLLOW_import_name_in_import_statement282);
            import_name(importDecl);
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_import_statement285);
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
            match(input,IMPORT,FOLLOW_IMPORT_in_function_import_statement309); if (failed) return ;
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function_import_statement311); if (failed) return ;
            if ( backtracking==0 ) {

              	            importDecl = factory.createFunctionImport();
              	            importDecl.setStartCharacter( ((CommonToken)IMPORT2).getStartIndex() );
              		    if (packageDescr != null) {
              			packageDescr.addFunctionImport( importDecl );
              		    }
              	        
            }
            pushFollow(FOLLOW_import_name_in_function_import_statement334);
            import_name(importDecl);
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_function_import_statement337);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:278:1: import_name[ImportDescr importDecl] returns [String name] : ID ( DOT id= identifier )* (star= '.*' )? ;
    public final String import_name(ImportDescr importDecl) throws RecognitionException {
        String name = null;

        Token star=null;
        Token ID3=null;
        Token DOT4=null;
        identifier_return id = null;



        		name = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:282:2: ( ID ( DOT id= identifier )* (star= '.*' )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:283:3: ID ( DOT id= identifier )* (star= '.*' )?
            {
            ID3=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_name362); if (failed) return name;
            if ( backtracking==0 ) {
               
              		    name =ID3.getText(); 
              		    importDecl.setTarget( name );
              		    importDecl.setEndCharacter( ((CommonToken)ID3).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:289:3: ( DOT id= identifier )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==DOT) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:289:5: DOT id= identifier
            	    {
            	    DOT4=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_import_name374); if (failed) return name;
            	    pushFollow(FOLLOW_identifier_in_import_name378);
            	    id=identifier();
            	    _fsp--;
            	    if (failed) return name;
            	    if ( backtracking==0 ) {
            	       
            	      		        name = name + DOT4.getText() + input.toString(id.start,id.stop); 
            	      			importDecl.setTarget( name );
            	      		        importDecl.setEndCharacter( ((CommonToken)((Token)id.start)).getStopIndex() );
            	      		    
            	    }

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:296:3: (star= '.*' )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==77) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:296:5: star= '.*'
                    {
                    star=(Token)input.LT(1);
                    match(input,77,FOLLOW_77_in_import_name402); if (failed) return name;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:306:1: global : GLOBAL type= dotted_name id= identifier opt_semicolon ;
    public final void global() throws RecognitionException {
        Token GLOBAL5=null;
        String type = null;

        identifier_return id = null;



        	    GlobalDescr global = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:310:2: ( GLOBAL type= dotted_name id= identifier opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:311:3: GLOBAL type= dotted_name id= identifier opt_semicolon
            {
            GLOBAL5=(Token)input.LT(1);
            match(input,GLOBAL,FOLLOW_GLOBAL_in_global436); if (failed) return ;
            if ( backtracking==0 ) {

              		    global = factory.createGlobal();
              	            global.setStartCharacter( ((CommonToken)GLOBAL5).getStartIndex() );
              		    packageDescr.addGlobal( global );
              		
            }
            pushFollow(FOLLOW_dotted_name_in_global447);
            type=dotted_name();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              		    global.setType( type );
              		
            }
            pushFollow(FOLLOW_identifier_in_global458);
            id=identifier();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_global460);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:329:1: function : FUNCTION (retType= dotted_name )? id= identifier LEFT_PAREN ( (paramType= dotted_name )? paramName= argument ( COMMA (paramType= dotted_name )? paramName= argument )* )? RIGHT_PAREN body= curly_chunk ;
    public final void function() throws RecognitionException {
        Token FUNCTION6=null;
        String retType = null;

        identifier_return id = null;

        String paramType = null;

        String paramName = null;

        curly_chunk_return body = null;



        		FunctionDescr f = null;
        		String type = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:334:2: ( FUNCTION (retType= dotted_name )? id= identifier LEFT_PAREN ( (paramType= dotted_name )? paramName= argument ( COMMA (paramType= dotted_name )? paramName= argument )* )? RIGHT_PAREN body= curly_chunk )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:335:3: FUNCTION (retType= dotted_name )? id= identifier LEFT_PAREN ( (paramType= dotted_name )? paramName= argument ( COMMA (paramType= dotted_name )? paramName= argument )* )? RIGHT_PAREN body= curly_chunk
            {
            FUNCTION6=(Token)input.LT(1);
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function485); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:335:19: (retType= dotted_name )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( ((LA7_0>=PACKAGE && LA7_0<=ID)||LA7_0==GLOBAL||(LA7_0>=QUERY && LA7_0<=ATTRIBUTES)||LA7_0==ENABLED||LA7_0==SALIENCE||(LA7_0>=DURATION && LA7_0<=DIALECT)||LA7_0==FROM||(LA7_0>=INIT && LA7_0<=RESULT)||LA7_0==IN||(LA7_0>=THEN && LA7_0<=EVENT)) ) {
                int LA7_1 = input.LA(2);

                if ( ((LA7_1>=PACKAGE && LA7_1<=GLOBAL)||(LA7_1>=QUERY && LA7_1<=ATTRIBUTES)||LA7_1==ENABLED||LA7_1==SALIENCE||(LA7_1>=DURATION && LA7_1<=DIALECT)||LA7_1==FROM||(LA7_1>=INIT && LA7_1<=RESULT)||LA7_1==IN||LA7_1==LEFT_SQUARE||(LA7_1>=THEN && LA7_1<=EVENT)) ) {
                    alt7=1;
                }
            }
            switch (alt7) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:335:19: retType= dotted_name
                    {
                    pushFollow(FOLLOW_dotted_name_in_function489);
                    retType=dotted_name();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_identifier_in_function494);
            id=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              			//System.err.println( "function :: " + n.getText() );
              			type = retType != null ? retType : null;
              			f = factory.createFunction( input.toString(id.start,id.stop), type );
              			f.setLocation(offset(FUNCTION6.getLine()), FUNCTION6.getCharPositionInLine());
              	        	f.setStartCharacter( ((CommonToken)FUNCTION6).getStartIndex() );
              			packageDescr.addFunction( f );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_function503); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:345:4: ( (paramType= dotted_name )? paramName= argument ( COMMA (paramType= dotted_name )? paramName= argument )* )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( ((LA11_0>=PACKAGE && LA11_0<=ID)||LA11_0==GLOBAL||(LA11_0>=QUERY && LA11_0<=ATTRIBUTES)||LA11_0==ENABLED||LA11_0==SALIENCE||(LA11_0>=DURATION && LA11_0<=DIALECT)||LA11_0==FROM||(LA11_0>=INIT && LA11_0<=RESULT)||LA11_0==IN||(LA11_0>=THEN && LA11_0<=EVENT)) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:345:6: (paramType= dotted_name )? paramName= argument ( COMMA (paramType= dotted_name )? paramName= argument )*
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:345:15: (paramType= dotted_name )?
                    int alt8=2;
                    alt8 = dfa8.predict(input);
                    switch (alt8) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:345:15: paramType= dotted_name
                            {
                            pushFollow(FOLLOW_dotted_name_in_function512);
                            paramType=dotted_name();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_argument_in_function517);
                    paramName=argument();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      					type = paramType != null ? paramType : null;
                      					f.addParameter( type, paramName );
                      				
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:350:5: ( COMMA (paramType= dotted_name )? paramName= argument )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==COMMA) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:350:7: COMMA (paramType= dotted_name )? paramName= argument
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_function531); if (failed) return ;
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:350:22: (paramType= dotted_name )?
                    	    int alt9=2;
                    	    alt9 = dfa9.predict(input);
                    	    switch (alt9) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:350:22: paramType= dotted_name
                    	            {
                    	            pushFollow(FOLLOW_dotted_name_in_function535);
                    	            paramType=dotted_name();
                    	            _fsp--;
                    	            if (failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_argument_in_function540);
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

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_function564); if (failed) return ;
            pushFollow(FOLLOW_curly_chunk_in_function570);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:367:1: argument returns [String name] : id= identifier ( '[' ']' )* ;
    public final String argument() throws RecognitionException {
        String name = null;

        identifier_return id = null;



        		name = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:371:2: (id= identifier ( '[' ']' )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:371:4: id= identifier ( '[' ']' )*
            {
            pushFollow(FOLLOW_identifier_in_argument597);
            id=identifier();
            _fsp--;
            if (failed) return name;
            if ( backtracking==0 ) {
               name =input.toString(id.start,id.stop); 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:371:38: ( '[' ']' )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==LEFT_SQUARE) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:371:40: '[' ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_argument603); if (failed) return name;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_argument605); if (failed) return name;
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


    // $ANTLR start type_declaration
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:374:1: type_declaration returns [TypeDeclarationDescr declaration] : DECLARE id= identifier LEFT_CURLY type_decl_attribute[$declaration] ( COMMA type_decl_attribute[$declaration] )* RIGHT_CURLY ;
    public final TypeDeclarationDescr type_declaration() throws RecognitionException {
        TypeDeclarationDescr declaration = null;

        identifier_return id = null;



                        declaration = factory.createTypeDeclaration();
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:378:9: ( DECLARE id= identifier LEFT_CURLY type_decl_attribute[$declaration] ( COMMA type_decl_attribute[$declaration] )* RIGHT_CURLY )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:378:11: DECLARE id= identifier LEFT_CURLY type_decl_attribute[$declaration] ( COMMA type_decl_attribute[$declaration] )* RIGHT_CURLY
            {
            match(input,DECLARE,FOLLOW_DECLARE_in_type_declaration645); if (failed) return declaration;
            pushFollow(FOLLOW_identifier_in_type_declaration649);
            id=identifier();
            _fsp--;
            if (failed) return declaration;
            if ( backtracking==0 ) {

                                          declaration.setTypeName( input.toString(id.start,id.stop) );
                                      
            }
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_type_declaration694); if (failed) return declaration;
            pushFollow(FOLLOW_type_decl_attribute_in_type_declaration720);
            type_decl_attribute(declaration);
            _fsp--;
            if (failed) return declaration;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:383:59: ( COMMA type_decl_attribute[$declaration] )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==COMMA) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:383:61: COMMA type_decl_attribute[$declaration]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_type_declaration725); if (failed) return declaration;
            	    pushFollow(FOLLOW_type_decl_attribute_in_type_declaration727);
            	    type_decl_attribute(declaration);
            	    _fsp--;
            	    if (failed) return declaration;

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);

            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_type_declaration749); if (failed) return declaration;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return declaration;
    }
    // $ANTLR end type_declaration


    // $ANTLR start type_decl_attribute
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:387:1: type_decl_attribute[TypeDeclarationDescr declaration] : att= identifier (val= STRING | cl= dotted_name ) ;
    public final void type_decl_attribute(TypeDeclarationDescr declaration) throws RecognitionException {
        Token val=null;
        identifier_return att = null;

        String cl = null;


        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:388:9: (att= identifier (val= STRING | cl= dotted_name ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:388:11: att= identifier (val= STRING | cl= dotted_name )
            {
            pushFollow(FOLLOW_identifier_in_type_decl_attribute785);
            att=identifier();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:389:17: (val= STRING | cl= dotted_name )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==STRING) ) {
                alt14=1;
            }
            else if ( ((LA14_0>=PACKAGE && LA14_0<=ID)||LA14_0==GLOBAL||(LA14_0>=QUERY && LA14_0<=ATTRIBUTES)||LA14_0==ENABLED||LA14_0==SALIENCE||(LA14_0>=DURATION && LA14_0<=DIALECT)||LA14_0==FROM||(LA14_0>=INIT && LA14_0<=RESULT)||LA14_0==IN||(LA14_0>=THEN && LA14_0<=EVENT)) ) {
                alt14=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("389:17: (val= STRING | cl= dotted_name )", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:389:19: val= STRING
                    {
                    val=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_type_decl_attribute808); if (failed) return ;
                    if ( backtracking==0 ) {

                                          declaration.addAttribute( input.toString(att.start,att.stop), getString( val.getText() ) );
                                      
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:393:19: cl= dotted_name
                    {
                    pushFollow(FOLLOW_dotted_name_in_type_decl_attribute849);
                    cl=dotted_name();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                                          declaration.addAttribute( input.toString(att.start,att.stop), cl );
                                      
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
    // $ANTLR end type_decl_attribute


    // $ANTLR start query
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:400:1: query returns [QueryDescr query] : QUERY queryName= name ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )? normal_lhs_block[lhs] END opt_semicolon ;
    public final QueryDescr query() throws RecognitionException {
        QueryDescr query = null;

        Token paramName=null;
        Token QUERY7=null;
        Token END8=null;
        name_return queryName = null;

        qualified_id_return paramType = null;



        		query = null;
        		AndDescr lhs = null;
        		List params = null;
        		List types = null;		
         
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:408:2: ( QUERY queryName= name ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )? normal_lhs_block[lhs] END opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:409:3: QUERY queryName= name ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )? normal_lhs_block[lhs] END opt_semicolon
            {
            QUERY7=(Token)input.LT(1);
            match(input,QUERY,FOLLOW_QUERY_in_query922); if (failed) return query;
            pushFollow(FOLLOW_name_in_query926);
            queryName=name();
            _fsp--;
            if (failed) return query;
            if ( backtracking==0 ) {
               
              			query = factory.createQuery( queryName.name ); 
              			query.setLocation( offset(QUERY7.getLine()), QUERY7.getCharPositionInLine() );
              			query.setStartCharacter( ((CommonToken)QUERY7).getStartIndex() );
              			lhs = new AndDescr(); query.setLhs( lhs ); 
              			lhs.setLocation( offset(QUERY7.getLine()), QUERY7.getCharPositionInLine() );
                                      location.setType( Location.LOCATION_RULE_HEADER );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:418:3: ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )?
            int alt19=2;
            alt19 = dfa19.predict(input);
            switch (alt19) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:418:5: LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_query936); if (failed) return query;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:419:11: ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )?
                    int alt18=2;
                    int LA18_0 = input.LA(1);

                    if ( (LA18_0==ID) ) {
                        alt18=1;
                    }
                    switch (alt18) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:419:13: ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )*
                            {
                            if ( backtracking==0 ) {
                               params = new ArrayList(); types = new ArrayList();
                            }
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:421:15: ( (paramType= qualified_id )? paramName= ID )
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:421:16: (paramType= qualified_id )? paramName= ID
                            {
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:421:25: (paramType= qualified_id )?
                            int alt15=2;
                            int LA15_0 = input.LA(1);

                            if ( (LA15_0==ID) ) {
                                int LA15_1 = input.LA(2);

                                if ( ((LA15_1>=ID && LA15_1<=DOT)||LA15_1==LEFT_SQUARE) ) {
                                    alt15=1;
                                }
                            }
                            switch (alt15) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:421:25: paramType= qualified_id
                                    {
                                    pushFollow(FOLLOW_qualified_id_in_query971);
                                    paramType=qualified_id();
                                    _fsp--;
                                    if (failed) return query;

                                    }
                                    break;

                            }

                            paramName=(Token)input.LT(1);
                            match(input,ID,FOLLOW_ID_in_query976); if (failed) return query;
                            if ( backtracking==0 ) {
                               params.add( paramName.getText() ); String type = (paramType != null) ? paramType.text : "Object"; types.add( type ); 
                            }

                            }

                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:422:15: ( COMMA (paramType= qualified_id )? paramName= ID )*
                            loop17:
                            do {
                                int alt17=2;
                                int LA17_0 = input.LA(1);

                                if ( (LA17_0==COMMA) ) {
                                    alt17=1;
                                }


                                switch (alt17) {
                            	case 1 :
                            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:422:16: COMMA (paramType= qualified_id )? paramName= ID
                            	    {
                            	    match(input,COMMA,FOLLOW_COMMA_in_query997); if (failed) return query;
                            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:422:31: (paramType= qualified_id )?
                            	    int alt16=2;
                            	    int LA16_0 = input.LA(1);

                            	    if ( (LA16_0==ID) ) {
                            	        int LA16_1 = input.LA(2);

                            	        if ( ((LA16_1>=ID && LA16_1<=DOT)||LA16_1==LEFT_SQUARE) ) {
                            	            alt16=1;
                            	        }
                            	    }
                            	    switch (alt16) {
                            	        case 1 :
                            	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:422:31: paramType= qualified_id
                            	            {
                            	            pushFollow(FOLLOW_qualified_id_in_query1001);
                            	            paramType=qualified_id();
                            	            _fsp--;
                            	            if (failed) return query;

                            	            }
                            	            break;

                            	    }

                            	    paramName=(Token)input.LT(1);
                            	    match(input,ID,FOLLOW_ID_in_query1006); if (failed) return query;
                            	    if ( backtracking==0 ) {
                            	       params.add( paramName.getText() );  String type = (paramType != null) ? paramType.text : "Object"; types.add( type );  
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    break loop17;
                                }
                            } while (true);

                            if ( backtracking==0 ) {
                              	query.setParameters( (String[]) params.toArray( new String[params.size()] ) ); 
                              		            	query.setParameterTypes( (String[]) types.toArray( new String[types.size()] ) ); 
                              		            
                            }

                            }
                            break;

                    }

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_query1056); if (failed) return query;

                    }
                    break;

            }

            if ( backtracking==0 ) {

                                      location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
              	        
            }
            pushFollow(FOLLOW_normal_lhs_block_in_query1085);
            normal_lhs_block(lhs);
            _fsp--;
            if (failed) return query;
            END8=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_query1090); if (failed) return query;
            pushFollow(FOLLOW_opt_semicolon_in_query1092);
            opt_semicolon();
            _fsp--;
            if (failed) return query;
            if ( backtracking==0 ) {

              			query.setEndCharacter( ((CommonToken)END8).getStopIndex() );
              		
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:440:1: template returns [FactTemplateDescr template] : TEMPLATE templateName= name opt_semicolon (slot= template_slot )+ END opt_semicolon ;
    public final FactTemplateDescr template() throws RecognitionException {
        FactTemplateDescr template = null;

        Token TEMPLATE9=null;
        Token END10=null;
        name_return templateName = null;

        FieldTemplateDescr slot = null;



        		template = null;		
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:444:2: ( TEMPLATE templateName= name opt_semicolon (slot= template_slot )+ END opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:445:3: TEMPLATE templateName= name opt_semicolon (slot= template_slot )+ END opt_semicolon
            {
            TEMPLATE9=(Token)input.LT(1);
            match(input,TEMPLATE,FOLLOW_TEMPLATE_in_template1120); if (failed) return template;
            pushFollow(FOLLOW_name_in_template1124);
            templateName=name();
            _fsp--;
            if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template1126);
            opt_semicolon();
            _fsp--;
            if (failed) return template;
            if ( backtracking==0 ) {

              			template = new FactTemplateDescr(templateName.name);
              			template.setLocation( offset(TEMPLATE9.getLine()), TEMPLATE9.getCharPositionInLine() );			
              			template.setStartCharacter( ((CommonToken)TEMPLATE9).getStartIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:451:3: (slot= template_slot )+
            int cnt20=0;
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==ID) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:4: slot= template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template1141);
            	    slot=template_slot();
            	    _fsp--;
            	    if (failed) return template;
            	    if ( backtracking==0 ) {

            	      				template.addFieldTemplate( slot );
            	      			
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt20 >= 1 ) break loop20;
            	    if (backtracking>0) {failed=true; return template;}
                        EarlyExitException eee =
                            new EarlyExitException(20, input);
                        throw eee;
                }
                cnt20++;
            } while (true);

            END10=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_template1156); if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template1158);
            opt_semicolon();
            _fsp--;
            if (failed) return template;
            if ( backtracking==0 ) {

              			template.setEndCharacter( ((CommonToken)END10).getStopIndex() );
              		
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:463:1: template_slot returns [FieldTemplateDescr field] : fieldType= qualified_id id= identifier opt_semicolon ;
    public final FieldTemplateDescr template_slot() throws RecognitionException {
        FieldTemplateDescr field = null;

        qualified_id_return fieldType = null;

        identifier_return id = null;



        		field = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:467:2: (fieldType= qualified_id id= identifier opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:468:11: fieldType= qualified_id id= identifier opt_semicolon
            {
            if ( backtracking==0 ) {

              			field = factory.createFieldTemplate();
              	         
            }
            pushFollow(FOLLOW_qualified_id_in_template_slot1204);
            fieldType=qualified_id();
            _fsp--;
            if (failed) return field;
            if ( backtracking==0 ) {

              		        field.setClassType( fieldType.text );
              			field.setStartCharacter( ((CommonToken)((Token)fieldType.start)).getStartIndex() );
              			field.setEndCharacter( ((CommonToken)((Token)fieldType.stop)).getStopIndex() );
              		 
            }
            pushFollow(FOLLOW_identifier_in_template_slot1220);
            id=identifier();
            _fsp--;
            if (failed) return field;
            pushFollow(FOLLOW_opt_semicolon_in_template_slot1222);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:486:1: rule returns [RuleDescr rule] : RULE ruleName= name ( rule_attributes[$rule] )? ( WHEN ( ':' )? normal_lhs_block[lhs] )? rhs_chunk[$rule] ;
    public final RuleDescr rule() throws RecognitionException {
        RuleDescr rule = null;

        Token RULE11=null;
        Token WHEN12=null;
        name_return ruleName = null;



        		rule = null;
        		AndDescr lhs = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:491:2: ( RULE ruleName= name ( rule_attributes[$rule] )? ( WHEN ( ':' )? normal_lhs_block[lhs] )? rhs_chunk[$rule] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:492:3: RULE ruleName= name ( rule_attributes[$rule] )? ( WHEN ( ':' )? normal_lhs_block[lhs] )? rhs_chunk[$rule]
            {
            RULE11=(Token)input.LT(1);
            match(input,RULE,FOLLOW_RULE_in_rule1253); if (failed) return rule;
            pushFollow(FOLLOW_name_in_rule1257);
            ruleName=name();
            _fsp--;
            if (failed) return rule;
            if ( backtracking==0 ) {
               
              			location.setType( Location.LOCATION_RULE_HEADER );
              			debug( "start rule: " + ruleName.name );
              			rule = new RuleDescr( ruleName.name, null ); 
              			rule.setLocation( offset(RULE11.getLine()), RULE11.getCharPositionInLine() );
              			rule.setStartCharacter( ((CommonToken)RULE11).getStartIndex() );
              			lhs = new AndDescr(); 
              			rule.setLhs( lhs ); 
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:502:3: ( rule_attributes[$rule] )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( ((LA21_0>=ATTRIBUTES && LA21_0<=ENABLED)||LA21_0==SALIENCE||(LA21_0>=NO_LOOP && LA21_0<=LOCK_ON_ACTIVE)) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:502:3: rule_attributes[$rule]
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule1266);
                    rule_attributes(rule);
                    _fsp--;
                    if (failed) return rule;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:503:3: ( WHEN ( ':' )? normal_lhs_block[lhs] )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==WHEN) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:504:4: WHEN ( ':' )? normal_lhs_block[lhs]
                    {
                    WHEN12=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_rule1278); if (failed) return rule;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:504:9: ( ':' )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);

                    if ( (LA22_0==78) ) {
                        alt22=1;
                    }
                    switch (alt22) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:504:9: ':'
                            {
                            match(input,78,FOLLOW_78_in_rule1280); if (failed) return rule;

                            }
                            break;

                    }

                    if ( backtracking==0 ) {
                       
                      				this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
                      				lhs.setLocation( offset(WHEN12.getLine()), WHEN12.getCharPositionInLine() );
                      				lhs.setStartCharacter( ((CommonToken)WHEN12).getStartIndex() );
                      			
                    }
                    pushFollow(FOLLOW_normal_lhs_block_in_rule1291);
                    normal_lhs_block(lhs);
                    _fsp--;
                    if (failed) return rule;

                    }
                    break;

            }

            pushFollow(FOLLOW_rhs_chunk_in_rule1301);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:517:1: rule_attributes[RuleDescr rule] : ( ATTRIBUTES ':' )? attr= rule_attribute ( ( ',' )? attr= rule_attribute )* ;
    public final void rule_attributes(RuleDescr rule) throws RecognitionException {
        AttributeDescr attr = null;


        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:518:2: ( ( ATTRIBUTES ':' )? attr= rule_attribute ( ( ',' )? attr= rule_attribute )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:519:2: ( ATTRIBUTES ':' )? attr= rule_attribute ( ( ',' )? attr= rule_attribute )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:519:2: ( ATTRIBUTES ':' )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==ATTRIBUTES) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:519:4: ATTRIBUTES ':'
                    {
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_rule_attributes1321); if (failed) return ;
                    match(input,78,FOLLOW_78_in_rule_attributes1323); if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_rule_attribute_in_rule_attributes1331);
            attr=rule_attribute();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               rule.addAttribute( attr ); 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:2: ( ( ',' )? attr= rule_attribute )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==COMMA||(LA26_0>=DATE_EFFECTIVE && LA26_0<=ENABLED)||LA26_0==SALIENCE||(LA26_0>=NO_LOOP && LA26_0<=LOCK_ON_ACTIVE)) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:4: ( ',' )? attr= rule_attribute
            	    {
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:4: ( ',' )?
            	    int alt25=2;
            	    int LA25_0 = input.LA(1);

            	    if ( (LA25_0==COMMA) ) {
            	        alt25=1;
            	    }
            	    switch (alt25) {
            	        case 1 :
            	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:4: ','
            	            {
            	            match(input,COMMA,FOLLOW_COMMA_in_rule_attributes1338); if (failed) return ;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes1343);
            	    attr=rule_attribute();
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       rule.addAttribute( attr ); 
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
    // $ANTLR end rule_attributes


    // $ANTLR start rule_attribute
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:526:1: rule_attribute returns [AttributeDescr attr] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect );
    public final AttributeDescr rule_attribute() throws RecognitionException {
        AttributeDescr attr = null;

        AttributeDescr a = null;



        		attr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:533:2: (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect )
            int alt27=12;
            switch ( input.LA(1) ) {
            case SALIENCE:
                {
                alt27=1;
                }
                break;
            case NO_LOOP:
                {
                alt27=2;
                }
                break;
            case AGENDA_GROUP:
                {
                alt27=3;
                }
                break;
            case DURATION:
                {
                alt27=4;
                }
                break;
            case ACTIVATION_GROUP:
                {
                alt27=5;
                }
                break;
            case AUTO_FOCUS:
                {
                alt27=6;
                }
                break;
            case DATE_EFFECTIVE:
                {
                alt27=7;
                }
                break;
            case DATE_EXPIRES:
                {
                alt27=8;
                }
                break;
            case ENABLED:
                {
                alt27=9;
                }
                break;
            case RULEFLOW_GROUP:
                {
                alt27=10;
                }
                break;
            case LOCK_ON_ACTIVE:
                {
                alt27=11;
                }
                break;
            case DIALECT:
                {
                alt27=12;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return attr;}
                NoViableAltException nvae =
                    new NoViableAltException("526:1: rule_attribute returns [AttributeDescr attr] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect );", 27, 0, input);

                throw nvae;
            }

            switch (alt27) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:533:4: a= salience
                    {
                    pushFollow(FOLLOW_salience_in_rule_attribute1380);
                    a=salience();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:534:4: a= no_loop
                    {
                    pushFollow(FOLLOW_no_loop_in_rule_attribute1388);
                    a=no_loop();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:535:4: a= agenda_group
                    {
                    pushFollow(FOLLOW_agenda_group_in_rule_attribute1397);
                    a=agenda_group();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:536:4: a= duration
                    {
                    pushFollow(FOLLOW_duration_in_rule_attribute1406);
                    a=duration();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:537:4: a= activation_group
                    {
                    pushFollow(FOLLOW_activation_group_in_rule_attribute1415);
                    a=activation_group();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:538:4: a= auto_focus
                    {
                    pushFollow(FOLLOW_auto_focus_in_rule_attribute1423);
                    a=auto_focus();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:539:4: a= date_effective
                    {
                    pushFollow(FOLLOW_date_effective_in_rule_attribute1431);
                    a=date_effective();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:540:4: a= date_expires
                    {
                    pushFollow(FOLLOW_date_expires_in_rule_attribute1439);
                    a=date_expires();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:541:4: a= enabled
                    {
                    pushFollow(FOLLOW_enabled_in_rule_attribute1447);
                    a=enabled();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:542:4: a= ruleflow_group
                    {
                    pushFollow(FOLLOW_ruleflow_group_in_rule_attribute1455);
                    a=ruleflow_group();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:543:4: a= lock_on_active
                    {
                    pushFollow(FOLLOW_lock_on_active_in_rule_attribute1463);
                    a=lock_on_active();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:544:4: a= dialect
                    {
                    pushFollow(FOLLOW_dialect_in_rule_attribute1470);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:547:1: date_effective returns [AttributeDescr descr] : DATE_EFFECTIVE STRING ;
    public final AttributeDescr date_effective() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING13=null;
        Token DATE_EFFECTIVE14=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:551:2: ( DATE_EFFECTIVE STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:552:3: DATE_EFFECTIVE STRING
            {
            DATE_EFFECTIVE14=(Token)input.LT(1);
            match(input,DATE_EFFECTIVE,FOLLOW_DATE_EFFECTIVE_in_date_effective1496); if (failed) return descr;
            STRING13=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_effective1498); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "date-effective", getString( STRING13.getText() ) );
              			descr.setLocation( offset( DATE_EFFECTIVE14.getLine() ), DATE_EFFECTIVE14.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)DATE_EFFECTIVE14).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)STRING13).getStopIndex() );
              		
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:562:1: date_expires returns [AttributeDescr descr] : DATE_EXPIRES STRING ;
    public final AttributeDescr date_expires() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING15=null;
        Token DATE_EXPIRES16=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:566:2: ( DATE_EXPIRES STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:566:4: DATE_EXPIRES STRING
            {
            DATE_EXPIRES16=(Token)input.LT(1);
            match(input,DATE_EXPIRES,FOLLOW_DATE_EXPIRES_in_date_expires1527); if (failed) return descr;
            STRING15=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_expires1529); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "date-expires", getString( STRING15.getText() ) );
              			descr.setLocation( offset(DATE_EXPIRES16.getLine()), DATE_EXPIRES16.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)DATE_EXPIRES16).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)STRING15).getStopIndex() );
              		
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:575:1: enabled returns [AttributeDescr descr] : ENABLED BOOL ;
    public final AttributeDescr enabled() throws RecognitionException {
        AttributeDescr descr = null;

        Token BOOL17=null;
        Token ENABLED18=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:579:2: ( ENABLED BOOL )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:579:5: ENABLED BOOL
            {
            ENABLED18=(Token)input.LT(1);
            match(input,ENABLED,FOLLOW_ENABLED_in_enabled1558); if (failed) return descr;
            BOOL17=(Token)input.LT(1);
            match(input,BOOL,FOLLOW_BOOL_in_enabled1560); if (failed) return descr;
            if ( backtracking==0 ) {

              				descr = new AttributeDescr( "enabled", BOOL17.getText() );
              				descr.setLocation( offset(ENABLED18.getLine()), ENABLED18.getCharPositionInLine() );
              				descr.setStartCharacter( ((CommonToken)ENABLED18).getStartIndex() );
              				descr.setEndCharacter( ((CommonToken)BOOL17).getStopIndex() );
              			
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:588:1: salience returns [AttributeDescr descr] : SALIENCE ( INT | txt= paren_chunk ) ;
    public final AttributeDescr salience() throws RecognitionException {
        AttributeDescr descr = null;

        Token SALIENCE19=null;
        Token INT20=null;
        paren_chunk_return txt = null;



        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:592:2: ( SALIENCE ( INT | txt= paren_chunk ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:593:3: SALIENCE ( INT | txt= paren_chunk )
            {
            SALIENCE19=(Token)input.LT(1);
            match(input,SALIENCE,FOLLOW_SALIENCE_in_salience1593); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "salience" );
              			descr.setLocation( offset(SALIENCE19.getLine()), SALIENCE19.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)SALIENCE19).getStartIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:599:3: ( INT | txt= paren_chunk )
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==INT) ) {
                alt28=1;
            }
            else if ( (LA28_0==LEFT_PAREN) ) {
                alt28=2;
            }
            else {
                if (backtracking>0) {failed=true; return descr;}
                NoViableAltException nvae =
                    new NoViableAltException("599:3: ( INT | txt= paren_chunk )", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:599:5: INT
                    {
                    INT20=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_salience1604); if (failed) return descr;
                    if ( backtracking==0 ) {

                      			descr.setValue( INT20.getText() );
                      			descr.setEndCharacter( ((CommonToken)INT20).getStopIndex() );
                      		
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:604:5: txt= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_salience1619);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:612:1: no_loop returns [AttributeDescr descr] : NO_LOOP ( BOOL )? ;
    public final AttributeDescr no_loop() throws RecognitionException {
        AttributeDescr descr = null;

        Token NO_LOOP21=null;
        Token BOOL22=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:616:2: ( NO_LOOP ( BOOL )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:616:4: NO_LOOP ( BOOL )?
            {
            NO_LOOP21=(Token)input.LT(1);
            match(input,NO_LOOP,FOLLOW_NO_LOOP_in_no_loop1649); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "no-loop", "true" );
              			descr.setLocation( offset(NO_LOOP21.getLine()), NO_LOOP21.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)NO_LOOP21).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)NO_LOOP21).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:623:3: ( BOOL )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==BOOL) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:623:5: BOOL
                    {
                    BOOL22=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1662); if (failed) return descr;
                    if ( backtracking==0 ) {

                      				descr.setValue( BOOL22.getText() );
                      				descr.setEndCharacter( ((CommonToken)BOOL22).getStopIndex() );
                      			
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:631:1: auto_focus returns [AttributeDescr descr] : AUTO_FOCUS ( BOOL )? ;
    public final AttributeDescr auto_focus() throws RecognitionException {
        AttributeDescr descr = null;

        Token AUTO_FOCUS23=null;
        Token BOOL24=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:635:2: ( AUTO_FOCUS ( BOOL )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:635:4: AUTO_FOCUS ( BOOL )?
            {
            AUTO_FOCUS23=(Token)input.LT(1);
            match(input,AUTO_FOCUS,FOLLOW_AUTO_FOCUS_in_auto_focus1697); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "auto-focus", "true" );
              			descr.setLocation( offset(AUTO_FOCUS23.getLine()), AUTO_FOCUS23.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)AUTO_FOCUS23).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)AUTO_FOCUS23).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:642:3: ( BOOL )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==BOOL) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:642:5: BOOL
                    {
                    BOOL24=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1710); if (failed) return descr;
                    if ( backtracking==0 ) {

                      				descr.setValue( BOOL24.getText() );
                      				descr.setEndCharacter( ((CommonToken)BOOL24).getStopIndex() );
                      			
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:650:1: activation_group returns [AttributeDescr descr] : ACTIVATION_GROUP STRING ;
    public final AttributeDescr activation_group() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING25=null;
        Token ACTIVATION_GROUP26=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:654:2: ( ACTIVATION_GROUP STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:654:4: ACTIVATION_GROUP STRING
            {
            ACTIVATION_GROUP26=(Token)input.LT(1);
            match(input,ACTIVATION_GROUP,FOLLOW_ACTIVATION_GROUP_in_activation_group1746); if (failed) return descr;
            STRING25=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1748); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "activation-group", getString( STRING25.getText() ) );
              			descr.setLocation( offset(ACTIVATION_GROUP26.getLine()), ACTIVATION_GROUP26.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)ACTIVATION_GROUP26).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)STRING25).getStopIndex() );
              		
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:663:1: ruleflow_group returns [AttributeDescr descr] : RULEFLOW_GROUP STRING ;
    public final AttributeDescr ruleflow_group() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING27=null;
        Token RULEFLOW_GROUP28=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:667:2: ( RULEFLOW_GROUP STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:667:4: RULEFLOW_GROUP STRING
            {
            RULEFLOW_GROUP28=(Token)input.LT(1);
            match(input,RULEFLOW_GROUP,FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1776); if (failed) return descr;
            STRING27=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_ruleflow_group1778); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "ruleflow-group", getString( STRING27.getText() ) );
              			descr.setLocation( offset(RULEFLOW_GROUP28.getLine()), RULEFLOW_GROUP28.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)RULEFLOW_GROUP28).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)STRING27).getStopIndex() );
              		
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:676:1: agenda_group returns [AttributeDescr descr] : AGENDA_GROUP STRING ;
    public final AttributeDescr agenda_group() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING29=null;
        Token AGENDA_GROUP30=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:680:2: ( AGENDA_GROUP STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:680:4: AGENDA_GROUP STRING
            {
            AGENDA_GROUP30=(Token)input.LT(1);
            match(input,AGENDA_GROUP,FOLLOW_AGENDA_GROUP_in_agenda_group1806); if (failed) return descr;
            STRING29=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1808); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "agenda-group", getString( STRING29.getText() ) );
              			descr.setLocation( offset(AGENDA_GROUP30.getLine()), AGENDA_GROUP30.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)AGENDA_GROUP30).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)STRING29).getStopIndex() );
              		
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:689:1: duration returns [AttributeDescr descr] : DURATION INT ;
    public final AttributeDescr duration() throws RecognitionException {
        AttributeDescr descr = null;

        Token INT31=null;
        Token DURATION32=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:693:2: ( DURATION INT )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:693:4: DURATION INT
            {
            DURATION32=(Token)input.LT(1);
            match(input,DURATION,FOLLOW_DURATION_in_duration1836); if (failed) return descr;
            INT31=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1838); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "duration", INT31.getText() );
              			descr.setLocation( offset(DURATION32.getLine()), DURATION32.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)DURATION32).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)INT31).getStopIndex() );
              		
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:702:1: dialect returns [AttributeDescr descr] : DIALECT STRING ;
    public final AttributeDescr dialect() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING33=null;
        Token DIALECT34=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:706:2: ( DIALECT STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:706:4: DIALECT STRING
            {
            DIALECT34=(Token)input.LT(1);
            match(input,DIALECT,FOLLOW_DIALECT_in_dialect1866); if (failed) return descr;
            STRING33=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_dialect1868); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "dialect", getString( STRING33.getText() ) );
              			descr.setLocation( offset(DIALECT34.getLine()), DIALECT34.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)DIALECT34).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)STRING33).getStopIndex() );
              		
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:715:1: lock_on_active returns [AttributeDescr descr] : LOCK_ON_ACTIVE ( BOOL )? ;
    public final AttributeDescr lock_on_active() throws RecognitionException {
        AttributeDescr descr = null;

        Token LOCK_ON_ACTIVE35=null;
        Token BOOL36=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:719:2: ( LOCK_ON_ACTIVE ( BOOL )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:719:4: LOCK_ON_ACTIVE ( BOOL )?
            {
            LOCK_ON_ACTIVE35=(Token)input.LT(1);
            match(input,LOCK_ON_ACTIVE,FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1900); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "lock-on-active", "true" );
              			descr.setLocation( offset(LOCK_ON_ACTIVE35.getLine()), LOCK_ON_ACTIVE35.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)LOCK_ON_ACTIVE35).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)LOCK_ON_ACTIVE35).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:726:3: ( BOOL )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==BOOL) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:726:5: BOOL
                    {
                    BOOL36=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_lock_on_active1913); if (failed) return descr;
                    if ( backtracking==0 ) {

                      				descr.setValue( BOOL36.getText() );
                      				descr.setEndCharacter( ((CommonToken)BOOL36).getStopIndex() );
                      			
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:734:1: normal_lhs_block[AndDescr descr] : (d= lhs[$descr] )* ;
    public final void normal_lhs_block(AndDescr descr) throws RecognitionException {
        BaseDescr d = null;



        		location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:738:2: ( (d= lhs[$descr] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:739:3: (d= lhs[$descr] )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:739:3: (d= lhs[$descr] )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( (LA32_0==ID||LA32_0==LEFT_PAREN||(LA32_0>=EXISTS && LA32_0<=FORALL)) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:739:5: d= lhs[$descr]
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1952);
            	    d=lhs(descr);
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       if( d != null) descr.addDescr( d ); 
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
        return ;
    }
    // $ANTLR end normal_lhs_block


    // $ANTLR start lhs
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:1: lhs[ConditionalElementDescr ce] returns [BaseDescr d] : l= lhs_or ;
    public final BaseDescr lhs(ConditionalElementDescr ce) throws RecognitionException {
        BaseDescr d = null;

        BaseDescr l = null;



        		d =null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:749:2: (l= lhs_or )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:749:4: l= lhs_or
            {
            pushFollow(FOLLOW_lhs_or_in_lhs1989);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:753:1: lhs_or returns [BaseDescr d] : ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* );
    public final BaseDescr lhs_or() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr lhsand = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d = null;
        		OrDescr or = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:758:2: ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* )
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==LEFT_PAREN) ) {
                int LA35_1 = input.LA(2);

                if ( (LA35_1==ID||LA35_1==LEFT_PAREN||LA35_1==AND||(LA35_1>=EXISTS && LA35_1<=FORALL)) ) {
                    alt35=2;
                }
                else if ( (LA35_1==OR) ) {
                    alt35=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("753:1: lhs_or returns [BaseDescr d] : ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* );", 35, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA35_0==ID||(LA35_0>=EXISTS && LA35_0<=FORALL)) ) {
                alt35=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("753:1: lhs_or returns [BaseDescr d] : ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* );", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:758:4: LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_or2014); if (failed) return d;
                    match(input,OR,FOLLOW_OR_in_lhs_or2016); if (failed) return d;
                    if ( backtracking==0 ) {

                      			or = new OrDescr();
                      			d = or;
                      			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                      		
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:764:3: (lhsand= lhs_and )+
                    int cnt33=0;
                    loop33:
                    do {
                        int alt33=2;
                        int LA33_0 = input.LA(1);

                        if ( (LA33_0==ID||LA33_0==LEFT_PAREN||(LA33_0>=EXISTS && LA33_0<=FORALL)) ) {
                            alt33=1;
                        }


                        switch (alt33) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:764:5: lhsand= lhs_and
                    	    {
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or2029);
                    	    lhsand=lhs_and();
                    	    _fsp--;
                    	    if (failed) return d;
                    	    if ( backtracking==0 ) {

                    	      			or.addDescr( lhsand );
                    	      		
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt33 >= 1 ) break loop33;
                    	    if (backtracking>0) {failed=true; return d;}
                                EarlyExitException eee =
                                    new EarlyExitException(33, input);
                                throw eee;
                        }
                        cnt33++;
                    } while (true);

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_or2040); if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:770:10: left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )*
                    {
                    pushFollow(FOLLOW_lhs_and_in_lhs_or2058);
                    left=lhs_and();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = left; 
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:771:3: ( ( OR | DOUBLE_PIPE ) right= lhs_and )*
                    loop34:
                    do {
                        int alt34=2;
                        int LA34_0 = input.LA(1);

                        if ( ((LA34_0>=OR && LA34_0<=DOUBLE_PIPE)) ) {
                            alt34=1;
                        }


                        switch (alt34) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:771:5: ( OR | DOUBLE_PIPE ) right= lhs_and
                    	    {
                    	    if ( (input.LA(1)>=OR && input.LA(1)<=DOUBLE_PIPE) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return d;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or2066);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {

                    	      				location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                    	      			
                    	    }
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or2082);
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
                    	    break loop34;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:788:1: lhs_and returns [BaseDescr d] : ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* );
    public final BaseDescr lhs_and() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr lhsunary = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d = null;
        		AndDescr and = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:793:2: ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* )
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==LEFT_PAREN) ) {
                int LA38_1 = input.LA(2);

                if ( (LA38_1==AND) ) {
                    alt38=1;
                }
                else if ( (LA38_1==ID||LA38_1==LEFT_PAREN||(LA38_1>=EXISTS && LA38_1<=FORALL)) ) {
                    alt38=2;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("788:1: lhs_and returns [BaseDescr d] : ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* );", 38, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA38_0==ID||(LA38_0>=EXISTS && LA38_0<=FORALL)) ) {
                alt38=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("788:1: lhs_and returns [BaseDescr d] : ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* );", 38, 0, input);

                throw nvae;
            }
            switch (alt38) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:793:4: LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_and2113); if (failed) return d;
                    match(input,AND,FOLLOW_AND_in_lhs_and2115); if (failed) return d;
                    if ( backtracking==0 ) {

                      			and = new AndDescr();
                      			d = and;
                      			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                      		
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:799:3: (lhsunary= lhs_unary )+
                    int cnt36=0;
                    loop36:
                    do {
                        int alt36=2;
                        int LA36_0 = input.LA(1);

                        if ( (LA36_0==ID||LA36_0==LEFT_PAREN||(LA36_0>=EXISTS && LA36_0<=FORALL)) ) {
                            alt36=1;
                        }


                        switch (alt36) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:799:4: lhsunary= lhs_unary
                    	    {
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and2127);
                    	    lhsunary=lhs_unary();
                    	    _fsp--;
                    	    if (failed) return d;
                    	    if ( backtracking==0 ) {

                    	      			and.addDescr( lhsunary );
                    	      		
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt36 >= 1 ) break loop36;
                    	    if (backtracking>0) {failed=true; return d;}
                                EarlyExitException eee =
                                    new EarlyExitException(36, input);
                                throw eee;
                        }
                        cnt36++;
                    } while (true);

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_and2137); if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:805:10: left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )*
                    {
                    pushFollow(FOLLOW_lhs_unary_in_lhs_and2155);
                    left=lhs_unary();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = left; 
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:806:3: ( ( AND | DOUBLE_AMPER ) right= lhs_unary )*
                    loop37:
                    do {
                        int alt37=2;
                        int LA37_0 = input.LA(1);

                        if ( ((LA37_0>=AND && LA37_0<=DOUBLE_AMPER)) ) {
                            alt37=1;
                        }


                        switch (alt37) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:806:5: ( AND | DOUBLE_AMPER ) right= lhs_unary
                    	    {
                    	    if ( (input.LA(1)>=AND && input.LA(1)<=DOUBLE_AMPER) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return d;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and2163);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {

                    	      				location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                    	      			
                    	    }
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and2179);
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
                    	    break loop37;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:823:1: lhs_unary returns [BaseDescr d] : ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source ) opt_semicolon ;
    public final BaseDescr lhs_unary() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr u = null;

        BaseDescr ps = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:827:2: ( ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source ) opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:827:4: ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source ) opt_semicolon
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:827:4: ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source )
            int alt39=6;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==EXISTS) && (synpred1())) {
                alt39=1;
            }
            else if ( (LA39_0==NOT) && (synpred2())) {
                alt39=2;
            }
            else if ( (LA39_0==EVAL) && (synpred3())) {
                alt39=3;
            }
            else if ( (LA39_0==FORALL) && (synpred4())) {
                alt39=4;
            }
            else if ( (LA39_0==LEFT_PAREN) && (synpred5())) {
                alt39=5;
            }
            else if ( (LA39_0==ID) ) {
                alt39=6;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("827:4: ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source )", 39, 0, input);

                throw nvae;
            }
            switch (alt39) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:827:6: ( EXISTS )=>u= lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary2224);
                    u=lhs_exist();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:828:5: ( NOT )=>u= lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary2242);
                    u=lhs_not();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:829:5: ( EVAL )=>u= lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary2261);
                    u=lhs_eval();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:830:5: ( FORALL )=>u= lhs_forall
                    {
                    pushFollow(FOLLOW_lhs_forall_in_lhs_unary2280);
                    u=lhs_forall();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:831:5: ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary2297); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_unary2301);
                    u=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary2303); if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:832:5: ps= pattern_source
                    {
                    pushFollow(FOLLOW_pattern_source_in_lhs_unary2314);
                    ps=pattern_source();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = (BaseDescr) ps; 
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_opt_semicolon_in_lhs_unary2326);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:837:1: pattern_source returns [BaseDescr d] : u= lhs_pattern ( ( ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement ) | FROM ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )? ;
    public final BaseDescr pattern_source() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr u = null;

        EntryPointDescr ep = null;

        AccumulateDescr ac = null;

        CollectDescr cs = null;

        FromDescr fm = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:841:2: (u= lhs_pattern ( ( ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement ) | FROM ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:842:3: u= lhs_pattern ( ( ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement ) | FROM ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )?
            {
            pushFollow(FOLLOW_lhs_pattern_in_pattern_source2353);
            u=lhs_pattern();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               d = u; 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:843:3: ( ( ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement ) | FROM ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )?
            int alt41=3;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==FROM) ) {
                int LA41_1 = input.LA(2);

                if ( (LA41_1==ENTRY_POINT) && (synpred6())) {
                    alt41=1;
                }
                else if ( ((LA41_1>=PACKAGE && LA41_1<=ID)||LA41_1==GLOBAL||(LA41_1>=QUERY && LA41_1<=ATTRIBUTES)||LA41_1==ENABLED||LA41_1==SALIENCE||(LA41_1>=DURATION && LA41_1<=DIALECT)||LA41_1==FROM||(LA41_1>=ACCUMULATE && LA41_1<=COLLECT)||LA41_1==IN||(LA41_1>=THEN && LA41_1<=EVENT)) ) {
                    alt41=2;
                }
            }
            switch (alt41) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:846:11: ( ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:846:11: ( ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:846:13: ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement
                    {
                    match(input,FROM,FOLLOW_FROM_in_pattern_source2404); if (failed) return d;
                    pushFollow(FOLLOW_entrypoint_statement_in_pattern_source2408);
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:848:4: FROM ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) )
                    {
                    match(input,FROM,FOLLOW_FROM_in_pattern_source2428); if (failed) return d;
                    if ( backtracking==0 ) {

                      				location.setType(Location.LOCATION_LHS_FROM);
                      				location.setProperty(Location.LOCATION_FROM_CONTENT, "");
                      		        
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:853:11: ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) )
                    int alt40=3;
                    switch ( input.LA(1) ) {
                    case ACCUMULATE:
                        {
                        alt40=1;
                        }
                        break;
                    case COLLECT:
                        {
                        alt40=2;
                        }
                        break;
                    case PACKAGE:
                    case IMPORT:
                    case FUNCTION:
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
                    case IN:
                    case THEN:
                    case EVENT:
                        {
                        alt40=3;
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return d;}
                        NoViableAltException nvae =
                            new NoViableAltException("853:11: ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) )", 40, 0, input);

                        throw nvae;
                    }

                    switch (alt40) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:854:13: (ac= accumulate_statement )
                            {
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:854:13: (ac= accumulate_statement )
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:854:15: ac= accumulate_statement
                            {
                            pushFollow(FOLLOW_accumulate_statement_in_pattern_source2486);
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
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:855:15: (cs= collect_statement )
                            {
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:855:15: (cs= collect_statement )
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:855:17: cs= collect_statement
                            {
                            pushFollow(FOLLOW_collect_statement_in_pattern_source2509);
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
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:857:15: (fm= from_statement )
                            {
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:857:15: (fm= from_statement )
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:857:17: fm= from_statement
                            {
                            pushFollow(FOLLOW_from_statement_in_pattern_source2546);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:862:1: lhs_exist returns [BaseDescr d] : EXISTS ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) ;
    public final BaseDescr lhs_exist() throws RecognitionException {
        BaseDescr d = null;

        Token EXISTS37=null;
        Token RIGHT_PAREN38=null;
        BaseDescr or = null;

        BaseDescr pattern = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:866:2: ( EXISTS ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:866:4: EXISTS ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
            {
            EXISTS37=(Token)input.LT(1);
            match(input,EXISTS,FOLLOW_EXISTS_in_lhs_exist2589); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new ExistsDescr( ); 
              			d.setLocation( offset(EXISTS37.getLine()), EXISTS37.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)EXISTS37).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:10: ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==LEFT_PAREN) ) {
                alt42=1;
            }
            else if ( (LA42_0==ID) ) {
                alt42=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("873:10: ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:12: ( LEFT_PAREN or= lhs_or RIGHT_PAREN )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:12: ( LEFT_PAREN or= lhs_or RIGHT_PAREN )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:14: LEFT_PAREN or= lhs_or RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist2609); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist2613);
                    or=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( or != null ) ((ExistsDescr)d).addDescr( or ); 
                    }
                    RIGHT_PAREN38=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist2643); if (failed) return d;
                    if ( backtracking==0 ) {
                       d.setEndCharacter( ((CommonToken)RIGHT_PAREN38).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:878:12: pattern= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_exist2693);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:888:1: lhs_not returns [NotDescr d] : NOT ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) ;
    public final NotDescr lhs_not() throws RecognitionException {
        NotDescr d = null;

        Token NOT39=null;
        Token RIGHT_PAREN40=null;
        BaseDescr or = null;

        BaseDescr pattern = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:892:2: ( NOT ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:892:4: NOT ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
            {
            NOT39=(Token)input.LT(1);
            match(input,NOT,FOLLOW_NOT_in_lhs_not2745); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new NotDescr( ); 
              			d.setLocation( offset(NOT39.getLine()), NOT39.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)NOT39).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_NOT );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:899:3: ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==LEFT_PAREN) ) {
                alt43=1;
            }
            else if ( (LA43_0==ID) ) {
                alt43=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("899:3: ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )", 43, 0, input);

                throw nvae;
            }
            switch (alt43) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:899:5: ( LEFT_PAREN or= lhs_or RIGHT_PAREN )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:899:5: ( LEFT_PAREN or= lhs_or RIGHT_PAREN )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:899:7: LEFT_PAREN or= lhs_or RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not2758); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_not2762);
                    or=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( or != null ) d.addDescr( or ); 
                    }
                    RIGHT_PAREN40=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not2793); if (failed) return d;
                    if ( backtracking==0 ) {
                       d.setEndCharacter( ((CommonToken)RIGHT_PAREN40).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:905:3: pattern= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_not2830);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:915:1: lhs_eval returns [BaseDescr d] : EVAL c= paren_chunk ;
    public final BaseDescr lhs_eval() throws RecognitionException {
        BaseDescr d = null;

        Token EVAL41=null;
        paren_chunk_return c = null;



        		d = new EvalDescr( );
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:919:2: ( EVAL c= paren_chunk )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:920:3: EVAL c= paren_chunk
            {
            EVAL41=(Token)input.LT(1);
            match(input,EVAL,FOLLOW_EVAL_in_lhs_eval2876); if (failed) return d;
            if ( backtracking==0 ) {

              			location.setType( Location.LOCATION_LHS_INSIDE_EVAL );
              		
            }
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval2887);
            c=paren_chunk();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setStartCharacter( ((CommonToken)EVAL41).getStartIndex() );
              		        if( input.toString(c.start,c.stop) != null ) {
              	  		    this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
              		            String body = safeSubstring( input.toString(c.start,c.stop), 1, input.toString(c.start,c.stop).length()-1 );
              			    checkTrailingSemicolon( body, offset(EVAL41.getLine()) );
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:940:1: lhs_forall returns [ForallDescr d] : FORALL LEFT_PAREN base= lhs_pattern (pattern= lhs_pattern )* RIGHT_PAREN ;
    public final ForallDescr lhs_forall() throws RecognitionException {
        ForallDescr d = null;

        Token FORALL42=null;
        Token RIGHT_PAREN43=null;
        BaseDescr base = null;

        BaseDescr pattern = null;



        		d = factory.createForall();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:944:2: ( FORALL LEFT_PAREN base= lhs_pattern (pattern= lhs_pattern )* RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:944:4: FORALL LEFT_PAREN base= lhs_pattern (pattern= lhs_pattern )* RIGHT_PAREN
            {
            FORALL42=(Token)input.LT(1);
            match(input,FORALL,FOLLOW_FORALL_in_lhs_forall2913); if (failed) return d;
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_forall2915); if (failed) return d;
            pushFollow(FOLLOW_lhs_pattern_in_lhs_forall2919);
            base=lhs_pattern();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {

              			d.setStartCharacter( ((CommonToken)FORALL42).getStartIndex() );
              		        // adding the base pattern
              		        d.addDescr( base );
              			d.setLocation( offset(FORALL42.getLine()), FORALL42.getCharPositionInLine() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:951:3: (pattern= lhs_pattern )*
            loop44:
            do {
                int alt44=2;
                int LA44_0 = input.LA(1);

                if ( (LA44_0==ID) ) {
                    alt44=1;
                }


                switch (alt44) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:951:5: pattern= lhs_pattern
            	    {
            	    pushFollow(FOLLOW_lhs_pattern_in_lhs_forall2934);
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
            	    break loop44;
                }
            } while (true);

            RIGHT_PAREN43=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_forall2950); if (failed) return d;
            if ( backtracking==0 ) {

              		        d.setEndCharacter( ((CommonToken)RIGHT_PAREN43).getStopIndex() );
              		
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:963:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact[null] );
    public final BaseDescr lhs_pattern() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr f = null;



        		d =null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:970:2: (f= fact_binding | f= fact[null] )
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==ID) ) {
                int LA45_1 = input.LA(2);

                if ( (LA45_1==78) ) {
                    alt45=1;
                }
                else if ( (LA45_1==DOT||LA45_1==LEFT_PAREN||LA45_1==LEFT_SQUARE) ) {
                    alt45=2;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("963:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact[null] );", 45, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("963:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact[null] );", 45, 0, input);

                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:970:4: f= fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_pattern2983);
                    f=fact_binding();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:971:4: f= fact[null]
                    {
                    pushFollow(FOLLOW_fact_in_lhs_pattern2991);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:974:1: from_statement returns [FromDescr d] : ds= from_source[$d] ;
    public final FromDescr from_statement() throws RecognitionException {
        FromDescr d = null;

        DeclarativeInvokerDescr ds = null;



        		d =factory.createFrom();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:978:2: (ds= from_source[$d] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:979:2: ds= from_source[$d]
            {
            pushFollow(FOLLOW_from_source_in_from_statement3018);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:986:1: accumulate_statement returns [AccumulateDescr d] : ACCUMULATE LEFT_PAREN inputCE= lhs_or ( COMMA )? ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) ) RIGHT_PAREN ;
    public final AccumulateDescr accumulate_statement() throws RecognitionException {
        AccumulateDescr d = null;

        Token id=null;
        Token ACCUMULATE44=null;
        Token RIGHT_PAREN45=null;
        BaseDescr inputCE = null;

        paren_chunk_return text = null;



        		d = factory.createAccumulate();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:990:2: ( ACCUMULATE LEFT_PAREN inputCE= lhs_or ( COMMA )? ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) ) RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:991:10: ACCUMULATE LEFT_PAREN inputCE= lhs_or ( COMMA )? ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) ) RIGHT_PAREN
            {
            ACCUMULATE44=(Token)input.LT(1);
            match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_accumulate_statement3055); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(ACCUMULATE44.getLine()), ACCUMULATE44.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)ACCUMULATE44).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement3065); if (failed) return d;
            pushFollow(FOLLOW_lhs_or_in_accumulate_statement3069);
            inputCE=lhs_or();
            _fsp--;
            if (failed) return d;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:997:29: ( COMMA )?
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==COMMA) ) {
                alt46=1;
            }
            switch (alt46) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:997:29: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement3071); if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {

              		        d.setInput( inputCE );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1001:3: ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) )
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==INIT) ) {
                alt51=1;
            }
            else if ( (LA51_0==ID) ) {
                alt51=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1001:3: ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) )", 51, 0, input);

                throw nvae;
            }
            switch (alt51) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1001:5: ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1001:5: ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1002:4: INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk
                    {
                    match(input,INIT,FOLLOW_INIT_in_accumulate_statement3089); if (failed) return d;
                    if ( backtracking==0 ) {

                      				location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE_INIT );
                      			
                    }
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_statement3102);
                    text=paren_chunk();
                    _fsp--;
                    if (failed) return d;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1006:21: ( COMMA )?
                    int alt47=2;
                    int LA47_0 = input.LA(1);

                    if ( (LA47_0==COMMA) ) {
                        alt47=1;
                    }
                    switch (alt47) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1006:21: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement3104); if (failed) return d;

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
                    match(input,ACTION,FOLLOW_ACTION_in_accumulate_statement3115); if (failed) return d;
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_statement3119);
                    text=paren_chunk();
                    _fsp--;
                    if (failed) return d;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1014:28: ( COMMA )?
                    int alt48=2;
                    int LA48_0 = input.LA(1);

                    if ( (LA48_0==COMMA) ) {
                        alt48=1;
                    }
                    switch (alt48) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1014:28: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement3121); if (failed) return d;

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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1022:4: ( REVERSE text= paren_chunk ( COMMA )? )?
                    int alt50=2;
                    int LA50_0 = input.LA(1);

                    if ( (LA50_0==REVERSE) ) {
                        alt50=1;
                    }
                    switch (alt50) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1022:6: REVERSE text= paren_chunk ( COMMA )?
                            {
                            match(input,REVERSE,FOLLOW_REVERSE_in_accumulate_statement3134); if (failed) return d;
                            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement3138);
                            text=paren_chunk();
                            _fsp--;
                            if (failed) return d;
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1022:31: ( COMMA )?
                            int alt49=2;
                            int LA49_0 = input.LA(1);

                            if ( (LA49_0==COMMA) ) {
                                alt49=1;
                            }
                            switch (alt49) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1022:31: COMMA
                                    {
                                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement3140); if (failed) return d;

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

                    match(input,RESULT,FOLLOW_RESULT_in_accumulate_statement3157); if (failed) return d;
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_statement3161);
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1040:3: (id= ID text= paren_chunk )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1040:3: (id= ID text= paren_chunk )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1041:4: id= ID text= paren_chunk
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_accumulate_statement3187); if (failed) return d;
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_statement3191);
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

            RIGHT_PAREN45=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement3208); if (failed) return d;
            if ( backtracking==0 ) {

              			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
              			d.setEndCharacter( ((CommonToken)RIGHT_PAREN45).getStopIndex() );
              		
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1061:1: from_source[FromDescr from] returns [DeclarativeInvokerDescr ds] : ident= identifier ( options {k=1; } : args= paren_chunk )? ( expression_chain[$from, ad] )? ;
    public final DeclarativeInvokerDescr from_source(FromDescr from) throws RecognitionException {
        DeclarativeInvokerDescr ds = null;

        identifier_return ident = null;

        paren_chunk_return args = null;



        		ds = null;
        		AccessorDescr ad = null;
        		FunctionCallDescr fc = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1067:2: (ident= identifier ( options {k=1; } : args= paren_chunk )? ( expression_chain[$from, ad] )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1067:4: ident= identifier ( options {k=1; } : args= paren_chunk )? ( expression_chain[$from, ad] )?
            {
            pushFollow(FOLLOW_identifier_in_from_source3239);
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1076:3: ( options {k=1; } : args= paren_chunk )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==LEFT_PAREN) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1083:5: args= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_from_source3267);
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

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1097:3: ( expression_chain[$from, ad] )?
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==DOT) ) {
                alt53=1;
            }
            switch (alt53) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1097:3: expression_chain[$from, ad]
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source3280);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1108:1: expression_chain[FromDescr from, AccessorDescr as] : ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )? ) ;
    public final void expression_chain(FromDescr from, AccessorDescr as) throws RecognitionException {
        identifier_return field = null;

        square_chunk_return sqarg = null;

        paren_chunk_return paarg = null;



          		FieldAccessDescr fa = null;
        	    	MethodAccessDescr ma = null;	
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1113:2: ( ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )? ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1114:2: ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )? )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1114:2: ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1114:4: DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )?
            {
            match(input,DOT,FOLLOW_DOT_in_expression_chain3311); if (failed) return ;
            pushFollow(FOLLOW_identifier_in_expression_chain3315);
            field=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              	        fa = new FieldAccessDescr(((Token)field.start).getText());	
              		fa.setLocation( offset(((Token)field.start).getLine()), ((Token)field.start).getCharPositionInLine() );
              		fa.setStartCharacter( ((CommonToken)((Token)field.start)).getStartIndex() );
              		fa.setEndCharacter( ((CommonToken)((Token)field.start)).getStopIndex() );
              	    
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1121:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )?
            int alt54=3;
            alt54 = dfa54.predict(input);
            switch (alt54) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1122:6: ( LEFT_SQUARE )=>sqarg= square_chunk
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain3346);
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1128:6: ( LEFT_PAREN )=>paarg= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain3379);
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

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1136:4: ( expression_chain[from, as] )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==DOT) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1136:4: expression_chain[from, as]
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain3394);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1150:1: collect_statement returns [CollectDescr d] : COLLECT LEFT_PAREN pattern= pattern_source RIGHT_PAREN ;
    public final CollectDescr collect_statement() throws RecognitionException {
        CollectDescr d = null;

        Token COLLECT46=null;
        Token RIGHT_PAREN47=null;
        BaseDescr pattern = null;



        		d = factory.createCollect();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1154:2: ( COLLECT LEFT_PAREN pattern= pattern_source RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1155:10: COLLECT LEFT_PAREN pattern= pattern_source RIGHT_PAREN
            {
            COLLECT46=(Token)input.LT(1);
            match(input,COLLECT,FOLLOW_COLLECT_in_collect_statement3445); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(COLLECT46.getLine()), COLLECT46.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)COLLECT46).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_FROM_COLLECT );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement3455); if (failed) return d;
            pushFollow(FOLLOW_pattern_source_in_collect_statement3459);
            pattern=pattern_source();
            _fsp--;
            if (failed) return d;
            RIGHT_PAREN47=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement3461); if (failed) return d;
            if ( backtracking==0 ) {

              		        d.setInputPattern( (PatternDescr) pattern );
              			d.setEndCharacter( ((CommonToken)RIGHT_PAREN47).getStopIndex() );
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1169:1: entrypoint_statement returns [EntryPointDescr d] : ENTRY_POINT id= name ;
    public final EntryPointDescr entrypoint_statement() throws RecognitionException {
        EntryPointDescr d = null;

        Token ENTRY_POINT48=null;
        name_return id = null;



        		d = factory.createEntryPoint();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1173:2: ( ENTRY_POINT id= name )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1174:10: ENTRY_POINT id= name
            {
            ENTRY_POINT48=(Token)input.LT(1);
            match(input,ENTRY_POINT,FOLLOW_ENTRY_POINT_in_entrypoint_statement3498); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(ENTRY_POINT48.getLine()), ENTRY_POINT48.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)ENTRY_POINT48).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_FROM_ENTRY_POINT );
              		
            }
            pushFollow(FOLLOW_name_in_entrypoint_statement3510);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1188:1: fact_binding returns [BaseDescr d] : ID ':' (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN ) ;
    public final BaseDescr fact_binding() throws RecognitionException {
        BaseDescr d = null;

        Token ID49=null;
        BaseDescr fe = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d =null;
        		OrDescr or = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1193:3: ( ID ':' (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1194:4: ID ':' (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN )
            {
            ID49=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding3542); if (failed) return d;
            match(input,78,FOLLOW_78_in_fact_binding3544); if (failed) return d;
            if ( backtracking==0 ) {

               		        // handling incomplete parsing
               		        d = new PatternDescr( );
               		        ((PatternDescr) d).setIdentifier( ID49.getText() );
               		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1200:3: (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN )
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==ID) ) {
                alt57=1;
            }
            else if ( (LA57_0==LEFT_PAREN) ) {
                alt57=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1200:3: (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN )", 57, 0, input);

                throw nvae;
            }
            switch (alt57) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1200:5: fe= fact[$ID.text]
                    {
                    pushFollow(FOLLOW_fact_in_fact_binding3558);
                    fe=fact(ID49.getText());
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {

                       		        // override previously instantiated pattern
                       			d =fe;
                       			if( d != null ) {
                         			    d.setStartCharacter( ((CommonToken)ID49).getStartIndex() );
                         			}
                       		
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1209:4: LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_binding3574); if (failed) return d;
                    pushFollow(FOLLOW_fact_in_fact_binding3578);
                    left=fact(ID49.getText());
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {

                       		        // override previously instantiated pattern
                       			d =left;
                       			if( d != null ) {
                         			    d.setStartCharacter( ((CommonToken)ID49).getStartIndex() );
                         			}
                       		
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1217:4: ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )*
                    loop56:
                    do {
                        int alt56=2;
                        int LA56_0 = input.LA(1);

                        if ( ((LA56_0>=OR && LA56_0<=DOUBLE_PIPE)) ) {
                            alt56=1;
                        }


                        switch (alt56) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1217:6: ( OR | DOUBLE_PIPE ) right= fact[$ID.text]
                    	    {
                    	    if ( (input.LA(1)>=OR && input.LA(1)<=DOUBLE_PIPE) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return d;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fact_binding3591);    throw mse;
                    	    }

                    	    pushFollow(FOLLOW_fact_in_fact_binding3603);
                    	    right=fact(ID49.getText());
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

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_binding3621); if (failed) return d;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1232:1: fact[String ident] returns [BaseDescr d] : id= qualified_id LEFT_PAREN ( constraints[pattern] )? RIGHT_PAREN ( EOF )? ;
    public final BaseDescr fact(String ident) throws RecognitionException {
        BaseDescr d = null;

        Token LEFT_PAREN50=null;
        Token RIGHT_PAREN51=null;
        qualified_id_return id = null;



        		d =null;
        		PatternDescr pattern = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1237:3: (id= qualified_id LEFT_PAREN ( constraints[pattern] )? RIGHT_PAREN ( EOF )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1238:11: id= qualified_id LEFT_PAREN ( constraints[pattern] )? RIGHT_PAREN ( EOF )?
            {
            if ( backtracking==0 ) {

               			pattern = new PatternDescr( );
               			if( ident != null ) {
               				pattern.setIdentifier( ident );
               			}
               			d = pattern; 
               	        
            }
            pushFollow(FOLLOW_qualified_id_in_fact3676);
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
            LEFT_PAREN50=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact3686); if (failed) return d;
            if ( backtracking==0 ) {

              		        location.setType( Location.LOCATION_LHS_INSIDE_CONDITION_START );
                          		location.setProperty( Location.LOCATION_PROPERTY_CLASS_NAME, id.text );
               				
               			pattern.setLocation( offset(LEFT_PAREN50.getLine()), LEFT_PAREN50.getCharPositionInLine() );
               			pattern.setLeftParentCharacter( ((CommonToken)LEFT_PAREN50).getStartIndex() );
               		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1261:4: ( constraints[pattern] )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( ((LA58_0>=PACKAGE && LA58_0<=ID)||(LA58_0>=GLOBAL && LA58_0<=LEFT_PAREN)||(LA58_0>=QUERY && LA58_0<=ATTRIBUTES)||LA58_0==ENABLED||LA58_0==SALIENCE||(LA58_0>=DURATION && LA58_0<=DIALECT)||LA58_0==FROM||LA58_0==EVAL||(LA58_0>=INIT && LA58_0<=RESULT)||LA58_0==IN||(LA58_0>=THEN && LA58_0<=EVENT)) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1261:4: constraints[pattern]
                    {
                    pushFollow(FOLLOW_constraints_in_fact3698);
                    constraints(pattern);
                    _fsp--;
                    if (failed) return d;

                    }
                    break;

            }

            RIGHT_PAREN51=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact3705); if (failed) return d;
            if ( backtracking==0 ) {

              		        if( ")".equals( RIGHT_PAREN51.getText() ) ) {
              				this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
              				pattern.setEndLocation( offset(RIGHT_PAREN51.getLine()), RIGHT_PAREN51.getCharPositionInLine() );	
              				pattern.setEndCharacter( ((CommonToken)RIGHT_PAREN51).getStopIndex() );
              		        	pattern.setRightParentCharacter( ((CommonToken)RIGHT_PAREN51).getStartIndex() );
              		        }
               		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1271:4: ( EOF )?
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==EOF) ) {
                alt59=1;
            }
            switch (alt59) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1271:4: EOF
                    {
                    match(input,EOF,FOLLOW_EOF_in_fact3714); if (failed) return d;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1275:1: constraints[PatternDescr pattern] : constraint[$pattern] ( COMMA constraint[$pattern] )* ;
    public final void constraints(PatternDescr pattern) throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1276:2: ( constraint[$pattern] ( COMMA constraint[$pattern] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1276:4: constraint[$pattern] ( COMMA constraint[$pattern] )*
            {
            pushFollow(FOLLOW_constraint_in_constraints3732);
            constraint(pattern);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1277:3: ( COMMA constraint[$pattern] )*
            loop60:
            do {
                int alt60=2;
                int LA60_0 = input.LA(1);

                if ( (LA60_0==COMMA) ) {
                    alt60=1;
                }


                switch (alt60) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1277:5: COMMA constraint[$pattern]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_constraints3739); if (failed) return ;
            	    if ( backtracking==0 ) {
            	       location.setType( Location.LOCATION_LHS_INSIDE_CONDITION_START ); 
            	    }
            	    pushFollow(FOLLOW_constraint_in_constraints3748);
            	    constraint(pattern);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop60;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1282:1: constraint[PatternDescr pattern] : or_constr[top] ;
    public final void constraint(PatternDescr pattern) throws RecognitionException {

        		ConditionalElementDescr top = null;
        		location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_START);
        		location.setProperty(Location.LOCATION_PROPERTY_PROPERTY_NAME, input.LT(1).getText() );
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1288:2: ( or_constr[top] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1289:3: or_constr[top]
            {
            if ( backtracking==0 ) {

              			top = pattern.getConstraint();
              		
            }
            pushFollow(FOLLOW_or_constr_in_constraint3781);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1295:1: or_constr[ConditionalElementDescr base] : and_constr[or] ( DOUBLE_PIPE and_constr[or] )* ;
    public final void or_constr(ConditionalElementDescr base) throws RecognitionException {

        		OrDescr or = new OrDescr();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1299:2: ( and_constr[or] ( DOUBLE_PIPE and_constr[or] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1300:3: and_constr[or] ( DOUBLE_PIPE and_constr[or] )*
            {
            pushFollow(FOLLOW_and_constr_in_or_constr3804);
            and_constr(or);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1301:3: ( DOUBLE_PIPE and_constr[or] )*
            loop61:
            do {
                int alt61=2;
                int LA61_0 = input.LA(1);

                if ( (LA61_0==DOUBLE_PIPE) ) {
                    alt61=1;
                }


                switch (alt61) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1301:5: DOUBLE_PIPE and_constr[or]
            	    {
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_constr3812); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_START);
            	      		
            	    }
            	    pushFollow(FOLLOW_and_constr_in_or_constr3821);
            	    and_constr(or);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop61;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1316:1: and_constr[ConditionalElementDescr base] : unary_constr[and] ( DOUBLE_AMPER unary_constr[and] )* ;
    public final void and_constr(ConditionalElementDescr base) throws RecognitionException {

        		AndDescr and = new AndDescr();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1320:2: ( unary_constr[and] ( DOUBLE_AMPER unary_constr[and] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1321:3: unary_constr[and] ( DOUBLE_AMPER unary_constr[and] )*
            {
            pushFollow(FOLLOW_unary_constr_in_and_constr3853);
            unary_constr(and);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1322:3: ( DOUBLE_AMPER unary_constr[and] )*
            loop62:
            do {
                int alt62=2;
                int LA62_0 = input.LA(1);

                if ( (LA62_0==DOUBLE_AMPER) ) {
                    alt62=1;
                }


                switch (alt62) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1322:5: DOUBLE_AMPER unary_constr[and]
            	    {
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_constr3861); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_START);
            	      		
            	    }
            	    pushFollow(FOLLOW_unary_constr_in_and_constr3870);
            	    unary_constr(and);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop62;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1337:1: unary_constr[ConditionalElementDescr base] : ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] ) ;
    public final void unary_constr(ConditionalElementDescr base) throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1338:2: ( ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1339:3: ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1339:3: ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] )
            int alt63=3;
            switch ( input.LA(1) ) {
            case PACKAGE:
            case IMPORT:
            case FUNCTION:
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
            case IN:
            case THEN:
            case EVENT:
                {
                alt63=1;
                }
                break;
            case LEFT_PAREN:
                {
                alt63=2;
                }
                break;
            case EVAL:
                {
                alt63=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1339:3: ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] )", 63, 0, input);

                throw nvae;
            }

            switch (alt63) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1339:5: field_constraint[$base]
                    {
                    pushFollow(FOLLOW_field_constraint_in_unary_constr3898);
                    field_constraint(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1340:5: LEFT_PAREN or_constr[$base] RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_unary_constr3906); if (failed) return ;
                    pushFollow(FOLLOW_or_constr_in_unary_constr3908);
                    or_constr(base);
                    _fsp--;
                    if (failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_unary_constr3911); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1341:5: EVAL predicate[$base]
                    {
                    match(input,EVAL,FOLLOW_EVAL_in_unary_constr3917); if (failed) return ;
                    pushFollow(FOLLOW_predicate_in_unary_constr3919);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1345:1: field_constraint[ConditionalElementDescr base] : ( ( ID ':' f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )? ) | (f= accessor_path or_restr_connective[top] ) );
    public final void field_constraint(ConditionalElementDescr base) throws RecognitionException {
        Token ID52=null;
        accessor_path_return f = null;



        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        		RestrictionConnectiveDescr top = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1351:2: ( ( ID ':' f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )? ) | (f= accessor_path or_restr_connective[top] ) )
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==ID) ) {
                int LA65_1 = input.LA(2);

                if ( (LA65_1==78) ) {
                    alt65=1;
                }
                else if ( (LA65_1==DOT||LA65_1==LEFT_PAREN||LA65_1==NOT||(LA65_1>=CONTAINS && LA65_1<=IN)||LA65_1==LEFT_SQUARE||(LA65_1>=80 && LA65_1<=85)) ) {
                    alt65=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1345:1: field_constraint[ConditionalElementDescr base] : ( ( ID ':' f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )? ) | (f= accessor_path or_restr_connective[top] ) );", 65, 1, input);

                    throw nvae;
                }
            }
            else if ( ((LA65_0>=PACKAGE && LA65_0<=FUNCTION)||LA65_0==GLOBAL||(LA65_0>=QUERY && LA65_0<=ATTRIBUTES)||LA65_0==ENABLED||LA65_0==SALIENCE||(LA65_0>=DURATION && LA65_0<=DIALECT)||LA65_0==FROM||(LA65_0>=INIT && LA65_0<=RESULT)||LA65_0==IN||(LA65_0>=THEN && LA65_0<=EVENT)) ) {
                alt65=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1345:1: field_constraint[ConditionalElementDescr base] : ( ( ID ':' f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )? ) | (f= accessor_path or_restr_connective[top] ) );", 65, 0, input);

                throw nvae;
            }
            switch (alt65) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1352:10: ( ID ':' f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )? )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1352:10: ( ID ':' f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )? )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1353:3: ID ':' f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )?
                    {
                    ID52=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_field_constraint3958); if (failed) return ;
                    match(input,78,FOLLOW_78_in_field_constraint3960); if (failed) return ;
                    if ( backtracking==0 ) {
                       
                      			fbd = new FieldBindingDescr();
                      			fbd.setIdentifier( ID52.getText() );
                      			fbd.setLocation( offset(ID52.getLine()), ID52.getCharPositionInLine() );
                      			fbd.setStartCharacter( ((CommonToken)ID52).getStartIndex() );
                      			base.addDescr( fbd );

                      		    
                    }
                    pushFollow(FOLLOW_accessor_path_in_field_constraint3979);
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
                      			    fbd.setStartCharacter( ((CommonToken)ID52).getStartIndex() );
                      			} 
                      			fc = new FieldConstraintDescr(f.text);
                      			fc.setLocation( offset(((Token)f.start).getLine()), ((Token)f.start).getCharPositionInLine() );
                      			fc.setStartCharacter( ((CommonToken)((Token)f.start)).getStartIndex() );
                      			top = fc.getRestriction();
                      			
                      			// it must be a field constraint, as it is not a binding
                      			if( ID52 == null ) {
                      			    base.addDescr( fc );
                      			}
                      		    }
                      		
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1387:3: ( or_restr_connective[top] | '->' predicate[$base] )?
                    int alt64=3;
                    int LA64_0 = input.LA(1);

                    if ( (LA64_0==LEFT_PAREN||LA64_0==NOT||(LA64_0>=CONTAINS && LA64_0<=IN)||(LA64_0>=80 && LA64_0<=85)) ) {
                        alt64=1;
                    }
                    else if ( (LA64_0==79) ) {
                        alt64=2;
                    }
                    switch (alt64) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1388:4: or_restr_connective[top]
                            {
                            pushFollow(FOLLOW_or_restr_connective_in_field_constraint3993);
                            or_restr_connective(top);
                            _fsp--;
                            if (failed) return ;
                            if ( backtracking==0 ) {

                              				// we must add now as we didn't before
                              				if( ID52 != null) {
                              				    base.addDescr( fc );
                              				}
                              			
                            }

                            }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1396:4: '->' predicate[$base]
                            {
                            match(input,79,FOLLOW_79_in_field_constraint4008); if (failed) return ;
                            pushFollow(FOLLOW_predicate_in_field_constraint4010);
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1400:3: (f= accessor_path or_restr_connective[top] )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1400:3: (f= accessor_path or_restr_connective[top] )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1401:3: f= accessor_path or_restr_connective[top]
                    {
                    pushFollow(FOLLOW_accessor_path_in_field_constraint4036);
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
                    pushFollow(FOLLOW_or_restr_connective_in_field_constraint4045);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1433:1: or_restr_connective[ RestrictionConnectiveDescr base ] options {backtrack=true; } : and_restr_connective[or] ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )* ;
    public final void or_restr_connective(RestrictionConnectiveDescr base) throws RecognitionException {

        		RestrictionConnectiveDescr or = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR);
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1440:2: ( and_restr_connective[or] ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1441:3: and_restr_connective[or] ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )*
            {
            pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective4092);
            and_restr_connective(or);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1442:3: ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )*
            loop66:
            do {
                int alt66=2;
                int LA66_0 = input.LA(1);

                if ( (LA66_0==DOUBLE_PIPE) ) {
                    switch ( input.LA(2) ) {
                    case IN:
                        {
                        int LA66_3 = input.LA(3);

                        if ( (LA66_3==LEFT_PAREN) ) {
                            int LA66_6 = input.LA(4);

                            if ( (synpred11()) ) {
                                alt66=1;
                            }


                        }


                        }
                        break;
                    case LEFT_PAREN:
                        {
                        int LA66_4 = input.LA(3);

                        if ( (synpred11()) ) {
                            alt66=1;
                        }


                        }
                        break;
                    case NOT:
                    case CONTAINS:
                    case EXCLUDES:
                    case MATCHES:
                    case SOUNDSLIKE:
                    case MEMBEROF:
                    case TILDE:
                    case 80:
                    case 81:
                    case 82:
                    case 83:
                    case 84:
                    case 85:
                        {
                        alt66=1;
                        }
                        break;

                    }

                }


                switch (alt66) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1444:4: DOUBLE_PIPE and_restr_connective[or]
            	    {
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_restr_connective4116); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	      			
            	    }
            	    pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective4127);
            	    and_restr_connective(or);
            	    _fsp--;
            	    if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1459:1: and_restr_connective[ RestrictionConnectiveDescr base ] : constraint_expression[and] ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )* ;
    public final void and_restr_connective(RestrictionConnectiveDescr base) throws RecognitionException {
        Token t=null;


        		RestrictionConnectiveDescr and = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND);
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1463:2: ( constraint_expression[and] ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1464:3: constraint_expression[and] ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )*
            {
            pushFollow(FOLLOW_constraint_expression_in_and_restr_connective4159);
            constraint_expression(and);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1465:3: ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )*
            loop67:
            do {
                int alt67=2;
                int LA67_0 = input.LA(1);

                if ( (LA67_0==DOUBLE_AMPER) ) {
                    switch ( input.LA(2) ) {
                    case IN:
                        {
                        int LA67_3 = input.LA(3);

                        if ( (LA67_3==LEFT_PAREN) ) {
                            switch ( input.LA(4) ) {
                            case IN:
                                {
                                int LA67_9 = input.LA(5);

                                if ( (LA67_9==DOT||(LA67_9>=COMMA && LA67_9<=RIGHT_PAREN)||LA67_9==LEFT_SQUARE) ) {
                                    alt67=1;
                                }


                                }
                                break;
                            case PACKAGE:
                            case IMPORT:
                            case FUNCTION:
                            case ID:
                            case GLOBAL:
                            case STRING:
                            case QUERY:
                            case END:
                            case TEMPLATE:
                            case RULE:
                            case WHEN:
                            case ATTRIBUTES:
                            case ENABLED:
                            case BOOL:
                            case SALIENCE:
                            case INT:
                            case DURATION:
                            case DIALECT:
                            case FROM:
                            case INIT:
                            case ACTION:
                            case REVERSE:
                            case RESULT:
                            case FLOAT:
                            case NULL:
                            case THEN:
                            case EVENT:
                                {
                                alt67=1;
                                }
                                break;
                            case LEFT_PAREN:
                                {
                                int LA67_10 = input.LA(5);

                                if ( (synpred12()) ) {
                                    alt67=1;
                                }


                                }
                                break;

                            }

                        }


                        }
                        break;
                    case NOT:
                    case CONTAINS:
                    case EXCLUDES:
                    case MATCHES:
                    case SOUNDSLIKE:
                    case MEMBEROF:
                    case TILDE:
                    case 80:
                    case 81:
                    case 82:
                    case 83:
                    case 84:
                    case 85:
                        {
                        alt67=1;
                        }
                        break;
                    case LEFT_PAREN:
                        {
                        switch ( input.LA(3) ) {
                        case IN:
                            {
                            int LA67_7 = input.LA(4);

                            if ( (LA67_7==LEFT_PAREN) ) {
                                int LA67_11 = input.LA(5);

                                if ( (synpred12()) ) {
                                    alt67=1;
                                }


                            }


                            }
                            break;
                        case LEFT_PAREN:
                            {
                            int LA67_8 = input.LA(4);

                            if ( (synpred12()) ) {
                                alt67=1;
                            }


                            }
                            break;
                        case NOT:
                        case CONTAINS:
                        case EXCLUDES:
                        case MATCHES:
                        case SOUNDSLIKE:
                        case MEMBEROF:
                        case TILDE:
                        case 80:
                        case 81:
                        case 82:
                        case 83:
                        case 84:
                        case 85:
                            {
                            alt67=1;
                            }
                            break;

                        }

                        }
                        break;

                    }

                }


                switch (alt67) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1466:5: t= DOUBLE_AMPER constraint_expression[and]
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_restr_connective4179); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	      			
            	    }
            	    pushFollow(FOLLOW_constraint_expression_in_and_restr_connective4190);
            	    constraint_expression(and);
            	    _fsp--;
            	    if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1481:1: constraint_expression[RestrictionConnectiveDescr base] : ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN ) ;
    public final void constraint_expression(RestrictionConnectiveDescr base) throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1482:9: ( ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1483:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1483:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )
            int alt68=3;
            switch ( input.LA(1) ) {
            case IN:
                {
                alt68=1;
                }
                break;
            case NOT:
                {
                int LA68_2 = input.LA(2);

                if ( (LA68_2==CONTAINS||LA68_2==MATCHES||(LA68_2>=MEMBEROF && LA68_2<=TILDE)) ) {
                    alt68=2;
                }
                else if ( (LA68_2==IN) ) {
                    alt68=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1483:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )", 68, 2, input);

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
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
                {
                alt68=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt68=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1483:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )", 68, 0, input);

                throw nvae;
            }

            switch (alt68) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1483:5: compound_operator[$base]
                    {
                    pushFollow(FOLLOW_compound_operator_in_constraint_expression4227);
                    compound_operator(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1484:5: simple_operator[$base]
                    {
                    pushFollow(FOLLOW_simple_operator_in_constraint_expression4234);
                    simple_operator(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1485:5: LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_constraint_expression4242); if (failed) return ;
                    if ( backtracking==0 ) {

                      			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
                      		
                    }
                    pushFollow(FOLLOW_or_restr_connective_in_constraint_expression4251);
                    or_restr_connective(base);
                    _fsp--;
                    if (failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_constraint_expression4256); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1494:1: simple_operator[RestrictionConnectiveDescr base] : (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | t= SOUNDSLIKE | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF | TILDE t= ID (param= square_chunk )? | n= NOT TILDE t= ID (param= square_chunk )? ) rd= expression_value[$base, op, isNegated, paramText] ;
    public final void simple_operator(RestrictionConnectiveDescr base) throws RecognitionException {
        Token t=null;
        Token n=null;
        square_chunk_return param = null;

        RestrictionDescr rd = null;



        		String op = null;
        		String paramText = null;
        		boolean isNegated = false;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1500:2: ( (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | t= SOUNDSLIKE | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF | TILDE t= ID (param= square_chunk )? | n= NOT TILDE t= ID (param= square_chunk )? ) rd= expression_value[$base, op, isNegated, paramText] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1501:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | t= SOUNDSLIKE | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF | TILDE t= ID (param= square_chunk )? | n= NOT TILDE t= ID (param= square_chunk )? ) rd= expression_value[$base, op, isNegated, paramText]
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1501:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | t= SOUNDSLIKE | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF | TILDE t= ID (param= square_chunk )? | n= NOT TILDE t= ID (param= square_chunk )? )
            int alt71=16;
            switch ( input.LA(1) ) {
            case 80:
                {
                alt71=1;
                }
                break;
            case 81:
                {
                alt71=2;
                }
                break;
            case 82:
                {
                alt71=3;
                }
                break;
            case 83:
                {
                alt71=4;
                }
                break;
            case 84:
                {
                alt71=5;
                }
                break;
            case 85:
                {
                alt71=6;
                }
                break;
            case CONTAINS:
                {
                alt71=7;
                }
                break;
            case NOT:
                {
                switch ( input.LA(2) ) {
                case TILDE:
                    {
                    alt71=16;
                    }
                    break;
                case MATCHES:
                    {
                    alt71=12;
                    }
                    break;
                case CONTAINS:
                    {
                    alt71=8;
                    }
                    break;
                case MEMBEROF:
                    {
                    alt71=14;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1501:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | t= SOUNDSLIKE | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF | TILDE t= ID (param= square_chunk )? | n= NOT TILDE t= ID (param= square_chunk )? )", 71, 8, input);

                    throw nvae;
                }

                }
                break;
            case EXCLUDES:
                {
                alt71=9;
                }
                break;
            case MATCHES:
                {
                alt71=10;
                }
                break;
            case SOUNDSLIKE:
                {
                alt71=11;
                }
                break;
            case MEMBEROF:
                {
                alt71=13;
                }
                break;
            case TILDE:
                {
                alt71=15;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1501:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | t= SOUNDSLIKE | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF | TILDE t= ID (param= square_chunk )? | n= NOT TILDE t= ID (param= square_chunk )? )", 71, 0, input);

                throw nvae;
            }

            switch (alt71) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1501:5: t= '=='
                    {
                    t=(Token)input.LT(1);
                    match(input,80,FOLLOW_80_in_simple_operator4287); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1502:5: t= '>'
                    {
                    t=(Token)input.LT(1);
                    match(input,81,FOLLOW_81_in_simple_operator4295); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1503:5: t= '>='
                    {
                    t=(Token)input.LT(1);
                    match(input,82,FOLLOW_82_in_simple_operator4303); if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1504:5: t= '<'
                    {
                    t=(Token)input.LT(1);
                    match(input,83,FOLLOW_83_in_simple_operator4311); if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1505:5: t= '<='
                    {
                    t=(Token)input.LT(1);
                    match(input,84,FOLLOW_84_in_simple_operator4319); if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1506:5: t= '!='
                    {
                    t=(Token)input.LT(1);
                    match(input,85,FOLLOW_85_in_simple_operator4327); if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1507:25: t= CONTAINS
                    {
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_simple_operator4355); if (failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1508:25: n= NOT t= CONTAINS
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator4383); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_simple_operator4387); if (failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1509:25: t= EXCLUDES
                    {
                    t=(Token)input.LT(1);
                    match(input,EXCLUDES,FOLLOW_EXCLUDES_in_simple_operator4415); if (failed) return ;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1510:25: t= MATCHES
                    {
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_simple_operator4443); if (failed) return ;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1511:25: t= SOUNDSLIKE
                    {
                    t=(Token)input.LT(1);
                    match(input,SOUNDSLIKE,FOLLOW_SOUNDSLIKE_in_simple_operator4471); if (failed) return ;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1512:25: n= NOT t= MATCHES
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator4499); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_simple_operator4503); if (failed) return ;

                    }
                    break;
                case 13 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1513:25: t= MEMBEROF
                    {
                    t=(Token)input.LT(1);
                    match(input,MEMBEROF,FOLLOW_MEMBEROF_in_simple_operator4531); if (failed) return ;

                    }
                    break;
                case 14 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1514:25: n= NOT t= MEMBEROF
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator4559); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,MEMBEROF,FOLLOW_MEMBEROF_in_simple_operator4563); if (failed) return ;

                    }
                    break;
                case 15 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1515:5: TILDE t= ID (param= square_chunk )?
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_simple_operator4569); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_simple_operator4573); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1515:21: (param= square_chunk )?
                    int alt69=2;
                    int LA69_0 = input.LA(1);

                    if ( (LA69_0==LEFT_SQUARE) ) {
                        alt69=1;
                    }
                    switch (alt69) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1515:21: param= square_chunk
                            {
                            pushFollow(FOLLOW_square_chunk_in_simple_operator4577);
                            param=square_chunk();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 16 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1516:5: n= NOT TILDE t= ID (param= square_chunk )?
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator4586); if (failed) return ;
                    match(input,TILDE,FOLLOW_TILDE_in_simple_operator4588); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_simple_operator4592); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1516:27: (param= square_chunk )?
                    int alt70=2;
                    int LA70_0 = input.LA(1);

                    if ( (LA70_0==LEFT_SQUARE) ) {
                        alt70=1;
                    }
                    switch (alt70) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1516:27: param= square_chunk
                            {
                            pushFollow(FOLLOW_square_chunk_in_simple_operator4596);
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
            pushFollow(FOLLOW_expression_value_in_simple_operator4611);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1535:1: compound_operator[RestrictionConnectiveDescr base] : ( IN | NOT IN ) LEFT_PAREN rd= expression_value[group, op, false, null] ( COMMA rd= expression_value[group, op, false, null] )* RIGHT_PAREN ;
    public final void compound_operator(RestrictionConnectiveDescr base) throws RecognitionException {
        RestrictionDescr rd = null;



        		String op = null;
        		RestrictionConnectiveDescr group = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1540:2: ( ( IN | NOT IN ) LEFT_PAREN rd= expression_value[group, op, false, null] ( COMMA rd= expression_value[group, op, false, null] )* RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1541:3: ( IN | NOT IN ) LEFT_PAREN rd= expression_value[group, op, false, null] ( COMMA rd= expression_value[group, op, false, null] )* RIGHT_PAREN
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1541:3: ( IN | NOT IN )
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==IN) ) {
                alt72=1;
            }
            else if ( (LA72_0==NOT) ) {
                alt72=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1541:3: ( IN | NOT IN )", 72, 0, input);

                throw nvae;
            }
            switch (alt72) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1541:5: IN
                    {
                    match(input,IN,FOLLOW_IN_in_compound_operator4641); if (failed) return ;
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1549:5: NOT IN
                    {
                    match(input,NOT,FOLLOW_NOT_in_compound_operator4653); if (failed) return ;
                    match(input,IN,FOLLOW_IN_in_compound_operator4655); if (failed) return ;
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

            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_compound_operator4670); if (failed) return ;
            pushFollow(FOLLOW_expression_value_in_compound_operator4674);
            rd=expression_value(group,  op,  false,  null);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1559:3: ( COMMA rd= expression_value[group, op, false, null] )*
            loop73:
            do {
                int alt73=2;
                int LA73_0 = input.LA(1);

                if ( (LA73_0==COMMA) ) {
                    alt73=1;
                }


                switch (alt73) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1559:5: COMMA rd= expression_value[group, op, false, null]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_compound_operator4681); if (failed) return ;
            	    pushFollow(FOLLOW_expression_value_in_compound_operator4685);
            	    rd=expression_value(group,  op,  false,  null);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop73;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_compound_operator4694); if (failed) return ;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1566:1: expression_value[RestrictionConnectiveDescr base, String op, boolean isNegated, String paramText] returns [RestrictionDescr rd] : (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk ) ;
    public final RestrictionDescr expression_value(RestrictionConnectiveDescr base, String op, boolean isNegated, String paramText) throws RecognitionException {
        RestrictionDescr rd = null;

        accessor_path_return ap = null;

        literal_constraint_return lc = null;

        paren_chunk_return rvc = null;



        		rd = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1570:2: ( (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1571:3: (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1571:3: (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk )
            int alt74=3;
            switch ( input.LA(1) ) {
            case PACKAGE:
            case IMPORT:
            case FUNCTION:
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
            case IN:
            case THEN:
            case EVENT:
                {
                alt74=1;
                }
                break;
            case STRING:
            case BOOL:
            case INT:
            case FLOAT:
            case NULL:
                {
                alt74=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt74=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return rd;}
                NoViableAltException nvae =
                    new NoViableAltException("1571:3: (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk )", 74, 0, input);

                throw nvae;
            }

            switch (alt74) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1571:5: ap= accessor_path
                    {
                    pushFollow(FOLLOW_accessor_path_in_expression_value4728);
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1579:5: lc= literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_expression_value4748);
                    lc=literal_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd = new LiteralRestrictionDescr(op, isNegated, paramText, lc.text, lc.type );
                      			
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1583:5: rvc= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_value4762);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1596:1: literal_constraint returns [String text, int type] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public final literal_constraint_return literal_constraint() throws RecognitionException {
        literal_constraint_return retval = new literal_constraint_return();
        retval.start = input.LT(1);

        Token t=null;


        		retval.text = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1600:2: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1600:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1600:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            int alt75=5;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt75=1;
                }
                break;
            case INT:
                {
                alt75=2;
                }
                break;
            case FLOAT:
                {
                alt75=3;
                }
                break;
            case BOOL:
                {
                alt75=4;
                }
                break;
            case NULL:
                {
                alt75=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1600:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )", 75, 0, input);

                throw nvae;
            }

            switch (alt75) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1600:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint4805); if (failed) return retval;
                    if ( backtracking==0 ) {
                       retval.text = getString( t.getText() ); retval.type = LiteralRestrictionDescr.TYPE_STRING; 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1601:5: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint4816); if (failed) return retval;
                    if ( backtracking==0 ) {
                       retval.text = t.getText(); retval.type = LiteralRestrictionDescr.TYPE_NUMBER; 
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1602:5: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint4829); if (failed) return retval;
                    if ( backtracking==0 ) {
                       retval.text = t.getText(); retval.type = LiteralRestrictionDescr.TYPE_NUMBER; 
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1603:5: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint4840); if (failed) return retval;
                    if ( backtracking==0 ) {
                       retval.text = t.getText(); retval.type = LiteralRestrictionDescr.TYPE_BOOLEAN; 
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1604:5: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal_constraint4852); if (failed) return retval;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1608:1: predicate[ConditionalElementDescr base] : text= paren_chunk ;
    public final void predicate(ConditionalElementDescr base) throws RecognitionException {
        paren_chunk_return text = null;



        		PredicateDescr d = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1612:2: (text= paren_chunk )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1613:3: text= paren_chunk
            {
            pushFollow(FOLLOW_paren_chunk_in_predicate4890);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1625:1: curly_chunk : LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk )* RIGHT_CURLY ;
    public final curly_chunk_return curly_chunk() throws RecognitionException {
        curly_chunk_return retval = new curly_chunk_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1626:2: ( LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk )* RIGHT_CURLY )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1627:3: LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk )* RIGHT_CURLY
            {
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk4908); if (failed) return retval;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1627:14: (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk )*
            loop76:
            do {
                int alt76=3;
                int LA76_0 = input.LA(1);

                if ( ((LA76_0>=PACKAGE && LA76_0<=DECLARE)||(LA76_0>=STRING && LA76_0<=85)) ) {
                    alt76=1;
                }
                else if ( (LA76_0==LEFT_CURLY) ) {
                    alt76=2;
                }


                switch (alt76) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1627:16: ~ ( LEFT_CURLY | RIGHT_CURLY )
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=DECLARE)||(input.LA(1)>=STRING && input.LA(1)<=85) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_curly_chunk4912);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1627:44: curly_chunk
            	    {
            	    pushFollow(FOLLOW_curly_chunk_in_curly_chunk4921);
            	    curly_chunk();
            	    _fsp--;
            	    if (failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop76;
                }
            } while (true);

            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk4926); if (failed) return retval;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1630:1: paren_chunk : LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk )* RIGHT_PAREN ;
    public final paren_chunk_return paren_chunk() throws RecognitionException {
        paren_chunk_return retval = new paren_chunk_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1631:2: ( LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk )* RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1632:3: LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk4940); if (failed) return retval;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1632:14: (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk )*
            loop77:
            do {
                int alt77=3;
                int LA77_0 = input.LA(1);

                if ( ((LA77_0>=PACKAGE && LA77_0<=GLOBAL)||LA77_0==COMMA||(LA77_0>=DECLARE && LA77_0<=85)) ) {
                    alt77=1;
                }
                else if ( (LA77_0==LEFT_PAREN) ) {
                    alt77=2;
                }


                switch (alt77) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1632:16: ~ ( LEFT_PAREN | RIGHT_PAREN )
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=GLOBAL)||input.LA(1)==COMMA||(input.LA(1)>=DECLARE && input.LA(1)<=85) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_paren_chunk4944);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1632:44: paren_chunk
            	    {
            	    pushFollow(FOLLOW_paren_chunk_in_paren_chunk4953);
            	    paren_chunk();
            	    _fsp--;
            	    if (failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop77;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk4958); if (failed) return retval;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1635:1: square_chunk : LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk )* RIGHT_SQUARE ;
    public final square_chunk_return square_chunk() throws RecognitionException {
        square_chunk_return retval = new square_chunk_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1636:2: ( LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk )* RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1637:3: LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk )* RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk4971); if (failed) return retval;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1637:15: (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk )*
            loop78:
            do {
                int alt78=3;
                int LA78_0 = input.LA(1);

                if ( ((LA78_0>=PACKAGE && LA78_0<=NULL)||(LA78_0>=THEN && LA78_0<=85)) ) {
                    alt78=1;
                }
                else if ( (LA78_0==LEFT_SQUARE) ) {
                    alt78=2;
                }


                switch (alt78) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1637:17: ~ ( LEFT_SQUARE | RIGHT_SQUARE )
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=NULL)||(input.LA(1)>=THEN && input.LA(1)<=85) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_square_chunk4975);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1637:47: square_chunk
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_square_chunk4984);
            	    square_chunk();
            	    _fsp--;
            	    if (failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop78;
                }
            } while (true);

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk4989); if (failed) return retval;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1640:1: qualified_id returns [ String text ] : ID ( DOT identifier )* ( LEFT_SQUARE RIGHT_SQUARE )* ;
    public final qualified_id_return qualified_id() throws RecognitionException {
        qualified_id_return retval = new qualified_id_return();
        retval.start = input.LT(1);

        Token ID53=null;
        identifier_return identifier54 = null;



        	        StringBuffer buf = new StringBuffer();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1647:2: ( ID ( DOT identifier )* ( LEFT_SQUARE RIGHT_SQUARE )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1647:5: ID ( DOT identifier )* ( LEFT_SQUARE RIGHT_SQUARE )*
            {
            ID53=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_qualified_id5018); if (failed) return retval;
            if ( backtracking==0 ) {
              buf.append(ID53.getText());
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1647:32: ( DOT identifier )*
            loop79:
            do {
                int alt79=2;
                int LA79_0 = input.LA(1);

                if ( (LA79_0==DOT) ) {
                    alt79=1;
                }


                switch (alt79) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1647:34: DOT identifier
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_qualified_id5024); if (failed) return retval;
            	    pushFollow(FOLLOW_identifier_in_qualified_id5026);
            	    identifier54=identifier();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) {
            	      buf.append("."+input.toString(identifier54.start,identifier54.stop));
            	    }

            	    }
            	    break;

            	default :
            	    break loop79;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1647:88: ( LEFT_SQUARE RIGHT_SQUARE )*
            loop80:
            do {
                int alt80=2;
                int LA80_0 = input.LA(1);

                if ( (LA80_0==LEFT_SQUARE) ) {
                    alt80=1;
                }


                switch (alt80) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1647:90: LEFT_SQUARE RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_qualified_id5035); if (failed) return retval;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_qualified_id5037); if (failed) return retval;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1650:1: dotted_name returns [ String text ] : i= identifier ( DOT i= identifier )* ( LEFT_SQUARE RIGHT_SQUARE )* ;
    public final String dotted_name() throws RecognitionException {
        String text = null;

        identifier_return i = null;



        	        StringBuffer buf = new StringBuffer();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1657:2: (i= identifier ( DOT i= identifier )* ( LEFT_SQUARE RIGHT_SQUARE )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1657:4: i= identifier ( DOT i= identifier )* ( LEFT_SQUARE RIGHT_SQUARE )*
            {
            pushFollow(FOLLOW_identifier_in_dotted_name5071);
            i=identifier();
            _fsp--;
            if (failed) return text;
            if ( backtracking==0 ) {
              buf.append(input.toString(i.start,i.stop));
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1657:40: ( DOT i= identifier )*
            loop81:
            do {
                int alt81=2;
                int LA81_0 = input.LA(1);

                if ( (LA81_0==DOT) ) {
                    alt81=1;
                }


                switch (alt81) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1657:42: DOT i= identifier
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_dotted_name5077); if (failed) return text;
            	    pushFollow(FOLLOW_identifier_in_dotted_name5081);
            	    i=identifier();
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {
            	      buf.append("."+input.toString(i.start,i.stop));
            	    }

            	    }
            	    break;

            	default :
            	    break loop81;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1657:89: ( LEFT_SQUARE RIGHT_SQUARE )*
            loop82:
            do {
                int alt82=2;
                int LA82_0 = input.LA(1);

                if ( (LA82_0==LEFT_SQUARE) ) {
                    alt82=1;
                }


                switch (alt82) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1657:91: LEFT_SQUARE RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dotted_name5090); if (failed) return text;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dotted_name5092); if (failed) return text;
            	    if ( backtracking==0 ) {
            	      buf.append("[]");
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
    // $ANTLR end dotted_name

    public static class accessor_path_return extends ParserRuleReturnScope {
        public String text;
    };

    // $ANTLR start accessor_path
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1660:1: accessor_path returns [ String text ] : a= accessor_element ( DOT a= accessor_element )* ;
    public final accessor_path_return accessor_path() throws RecognitionException {
        accessor_path_return retval = new accessor_path_return();
        retval.start = input.LT(1);

        String a = null;



        	        StringBuffer buf = new StringBuffer();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1667:2: (a= accessor_element ( DOT a= accessor_element )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1667:4: a= accessor_element ( DOT a= accessor_element )*
            {
            pushFollow(FOLLOW_accessor_element_in_accessor_path5126);
            a=accessor_element();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) {
              buf.append(a);
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1667:46: ( DOT a= accessor_element )*
            loop83:
            do {
                int alt83=2;
                int LA83_0 = input.LA(1);

                if ( (LA83_0==DOT) ) {
                    alt83=1;
                }


                switch (alt83) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1667:48: DOT a= accessor_element
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_accessor_path5132); if (failed) return retval;
            	    pushFollow(FOLLOW_accessor_element_in_accessor_path5136);
            	    a=accessor_element();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) {
            	      buf.append("."+a);
            	    }

            	    }
            	    break;

            	default :
            	    break loop83;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1670:1: accessor_element returns [ String text ] : i= identifier (s= square_chunk )* ;
    public final String accessor_element() throws RecognitionException {
        String text = null;

        identifier_return i = null;

        square_chunk_return s = null;



        	        StringBuffer buf = new StringBuffer();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1677:2: (i= identifier (s= square_chunk )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1678:3: i= identifier (s= square_chunk )*
            {
            pushFollow(FOLLOW_identifier_in_accessor_element5174);
            i=identifier();
            _fsp--;
            if (failed) return text;
            if ( backtracking==0 ) {
              buf.append(input.toString(i.start,i.stop));
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1678:39: (s= square_chunk )*
            loop84:
            do {
                int alt84=2;
                int LA84_0 = input.LA(1);

                if ( (LA84_0==LEFT_SQUARE) ) {
                    alt84=1;
                }


                switch (alt84) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1678:40: s= square_chunk
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_accessor_element5181);
            	    s=square_chunk();
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {
            	      buf.append(input.toString(s.start,s.stop));
            	    }

            	    }
            	    break;

            	default :
            	    break loop84;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1681:1: rhs_chunk[RuleDescr rule] : THEN (~ END )* loc= END opt_semicolon ;
    public final void rhs_chunk(RuleDescr rule) throws RecognitionException {
        Token loc=null;
        Token THEN55=null;

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1682:2: ( THEN (~ END )* loc= END opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1683:3: THEN (~ END )* loc= END opt_semicolon
            {
            THEN55=(Token)input.LT(1);
            match(input,THEN,FOLLOW_THEN_in_rhs_chunk5202); if (failed) return ;
            if ( backtracking==0 ) {
               location.setType( Location.LOCATION_RHS ); 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1684:3: (~ END )*
            loop85:
            do {
                int alt85=2;
                int LA85_0 = input.LA(1);

                if ( ((LA85_0>=PACKAGE && LA85_0<=QUERY)||(LA85_0>=TEMPLATE && LA85_0<=85)) ) {
                    alt85=1;
                }


                switch (alt85) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1684:5: ~ END
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=QUERY)||(input.LA(1)>=TEMPLATE && input.LA(1)<=85) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_rhs_chunk5210);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop85;
                }
            } while (true);

            loc=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_rhs_chunk5234); if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_rhs_chunk5236);
            opt_semicolon();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

                                  // ignoring first line in the consequence
                                  String buf = input.toString( THEN55, loc );
                                  // removing final END keyword
                                  int idx=4;
                                  while( idx < buf.length()-3 && (buf.charAt(idx) == ' ' || buf.charAt(idx) == '\t') ) {
                                      idx++;
                                  }
                                  if( idx < buf.length()-3 && buf.charAt(idx) == '\r' ) idx++;
                                  if( idx < buf.length()-3 && buf.charAt(idx) == '\n' ) idx++;
                                  buf = safeSubstring( buf, idx, buf.length()-3 );
              		    rule.setConsequence( buf );
                   		    rule.setConsequenceLocation(offset(THEN55.getLine()), THEN55.getCharPositionInLine());
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1704:1: name returns [String name] : ( ID | STRING );
    public final name_return name() throws RecognitionException {
        name_return retval = new name_return();
        retval.start = input.LT(1);

        Token ID56=null;
        Token STRING57=null;

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1705:2: ( ID | STRING )
            int alt86=2;
            int LA86_0 = input.LA(1);

            if ( (LA86_0==ID) ) {
                alt86=1;
            }
            else if ( (LA86_0==STRING) ) {
                alt86=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1704:1: name returns [String name] : ( ID | STRING );", 86, 0, input);

                throw nvae;
            }
            switch (alt86) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1705:5: ID
                    {
                    ID56=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_name5270); if (failed) return retval;
                    if ( backtracking==0 ) {
                       retval.name = ID56.getText(); 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1706:5: STRING
                    {
                    STRING57=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_name5278); if (failed) return retval;
                    if ( backtracking==0 ) {
                       retval.name = getString( STRING57.getText() ); 
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1709:1: identifier : ( ID | PACKAGE | FUNCTION | GLOBAL | IMPORT | EVENT | RULE | QUERY | TEMPLATE | ATTRIBUTES | ENABLED | SALIENCE | DURATION | DIALECT | FROM | INIT | ACTION | REVERSE | RESULT | WHEN | THEN | END | IN );
    public final identifier_return identifier() throws RecognitionException {
        identifier_return retval = new identifier_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1710:2: ( ID | PACKAGE | FUNCTION | GLOBAL | IMPORT | EVENT | RULE | QUERY | TEMPLATE | ATTRIBUTES | ENABLED | SALIENCE | DURATION | DIALECT | FROM | INIT | ACTION | REVERSE | RESULT | WHEN | THEN | END | IN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:
            {
            if ( (input.LA(1)>=PACKAGE && input.LA(1)<=ID)||input.LA(1)==GLOBAL||(input.LA(1)>=QUERY && input.LA(1)<=ATTRIBUTES)||input.LA(1)==ENABLED||input.LA(1)==SALIENCE||(input.LA(1)>=DURATION && input.LA(1)<=DIALECT)||input.LA(1)==FROM||(input.LA(1)>=INIT && input.LA(1)<=RESULT)||input.LA(1)==IN||(input.LA(1)>=THEN && input.LA(1)<=EVENT) ) {
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
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:827:6: ( EXISTS )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:827:8: EXISTS
        {
        match(input,EXISTS,FOLLOW_EXISTS_in_synpred12216); if (failed) return ;

        }
    }
    // $ANTLR end synpred1

    // $ANTLR start synpred2
    public final void synpred2_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:828:5: ( NOT )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:828:7: NOT
        {
        match(input,NOT,FOLLOW_NOT_in_synpred22234); if (failed) return ;

        }
    }
    // $ANTLR end synpred2

    // $ANTLR start synpred3
    public final void synpred3_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:829:5: ( EVAL )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:829:7: EVAL
        {
        match(input,EVAL,FOLLOW_EVAL_in_synpred32253); if (failed) return ;

        }
    }
    // $ANTLR end synpred3

    // $ANTLR start synpred4
    public final void synpred4_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:830:5: ( FORALL )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:830:7: FORALL
        {
        match(input,FORALL,FOLLOW_FORALL_in_synpred42272); if (failed) return ;

        }
    }
    // $ANTLR end synpred4

    // $ANTLR start synpred5
    public final void synpred5_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:831:5: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:831:7: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred52291); if (failed) return ;

        }
    }
    // $ANTLR end synpred5

    // $ANTLR start synpred6
    public final void synpred6_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:846:13: ( FROM ENTRY_POINT )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:846:14: FROM ENTRY_POINT
        {
        match(input,FROM,FOLLOW_FROM_in_synpred62397); if (failed) return ;
        match(input,ENTRY_POINT,FOLLOW_ENTRY_POINT_in_synpred62399); if (failed) return ;

        }
    }
    // $ANTLR end synpred6

    // $ANTLR start synpred9
    public final void synpred9_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1122:6: ( LEFT_SQUARE )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1122:8: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred93338); if (failed) return ;

        }
    }
    // $ANTLR end synpred9

    // $ANTLR start synpred10
    public final void synpred10_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1128:6: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1128:8: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred103371); if (failed) return ;

        }
    }
    // $ANTLR end synpred10

    // $ANTLR start synpred11
    public final void synpred11_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1444:4: ( DOUBLE_PIPE and_restr_connective[or] )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1444:4: DOUBLE_PIPE and_restr_connective[or]
        {
        match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred114116); if (failed) return ;
        pushFollow(FOLLOW_and_restr_connective_in_synpred114127);
        and_restr_connective(or);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred11

    // $ANTLR start synpred12
    public final void synpred12_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1466:5: ( DOUBLE_AMPER constraint_expression[and] )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1466:5: DOUBLE_AMPER constraint_expression[and]
        {
        match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred124179); if (failed) return ;
        pushFollow(FOLLOW_constraint_expression_in_synpred124190);
        constraint_expression(and);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred12

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


    protected DFA8 dfa8 = new DFA8(this);
    protected DFA9 dfa9 = new DFA9(this);
    protected DFA19 dfa19 = new DFA19(this);
    protected DFA54 dfa54 = new DFA54(this);
    static final String DFA8_eotS =
        "\6\uffff";
    static final String DFA8_eofS =
        "\6\uffff";
    static final String DFA8_minS =
        "\2\4\1\77\2\uffff\1\4";
    static final String DFA8_maxS =
        "\2\101\1\77\2\uffff\1\101";
    static final String DFA8_acceptS =
        "\3\uffff\1\2\1\1\1\uffff";
    static final String DFA8_specialS =
        "\6\uffff}>";
    static final String[] DFA8_transitionS = {
            "\4\1\1\uffff\1\1\7\uffff\6\1\2\uffff\1\1\1\uffff\1\1\6\uffff"+
            "\2\1\5\uffff\1\1\5\uffff\4\1\10\uffff\1\1\4\uffff\2\1",
            "\6\4\1\uffff\2\3\4\uffff\6\4\2\uffff\1\4\1\uffff\1\4\6\uffff"+
            "\2\4\5\uffff\1\4\5\uffff\4\4\10\uffff\1\4\2\uffff\1\2\1\uffff"+
            "\2\4",
            "\1\5",
            "",
            "",
            "\4\4\1\uffff\1\4\1\uffff\2\3\4\uffff\6\4\2\uffff\1\4\1\uffff"+
            "\1\4\6\uffff\2\4\5\uffff\1\4\5\uffff\4\4\10\uffff\1\4\2\uffff"+
            "\1\2\1\uffff\2\4"
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
            return "345:15: (paramType= dotted_name )?";
        }
    }
    static final String DFA9_eotS =
        "\6\uffff";
    static final String DFA9_eofS =
        "\6\uffff";
    static final String DFA9_minS =
        "\2\4\1\uffff\1\77\1\uffff\1\4";
    static final String DFA9_maxS =
        "\2\101\1\uffff\1\77\1\uffff\1\101";
    static final String DFA9_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\1\uffff";
    static final String DFA9_specialS =
        "\6\uffff}>";
    static final String[] DFA9_transitionS = {
            "\4\1\1\uffff\1\1\7\uffff\6\1\2\uffff\1\1\1\uffff\1\1\6\uffff"+
            "\2\1\5\uffff\1\1\5\uffff\4\1\10\uffff\1\1\4\uffff\2\1",
            "\6\2\1\uffff\2\4\4\uffff\6\2\2\uffff\1\2\1\uffff\1\2\6\uffff"+
            "\2\2\5\uffff\1\2\5\uffff\4\2\10\uffff\1\2\2\uffff\1\3\1\uffff"+
            "\2\2",
            "",
            "\1\5",
            "",
            "\4\2\1\uffff\1\2\1\uffff\2\4\4\uffff\6\2\2\uffff\1\2\1\uffff"+
            "\1\2\6\uffff\2\2\5\uffff\1\2\5\uffff\4\2\10\uffff\1\2\2\uffff"+
            "\1\3\1\uffff\2\2"
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
            return "350:22: (paramType= dotted_name )?";
        }
    }
    static final String DFA19_eotS =
        "\11\uffff";
    static final String DFA19_eofS =
        "\11\uffff";
    static final String DFA19_minS =
        "\2\7\1\uffff\1\7\1\uffff\1\4\1\77\2\7";
    static final String DFA19_maxS =
        "\2\55\1\uffff\1\116\1\uffff\1\101\1\77\2\76";
    static final String DFA19_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\4\uffff";
    static final String DFA19_specialS =
        "\11\uffff}>";
    static final String[] DFA19_transitionS = {
            "\1\2\2\uffff\1\1\7\uffff\1\2\27\uffff\4\2",
            "\1\3\2\uffff\1\2\1\uffff\1\4\30\uffff\1\2\1\uffff\1\2\2\uffff"+
            "\4\2",
            "",
            "\1\4\1\5\1\uffff\1\2\2\4\61\uffff\1\6\17\uffff\1\2",
            "",
            "\4\7\1\uffff\1\7\7\uffff\6\7\2\uffff\1\7\1\uffff\1\7\6\uffff"+
            "\2\7\5\uffff\1\7\5\uffff\4\7\10\uffff\1\7\4\uffff\2\7",
            "\1\10",
            "\1\4\1\5\1\uffff\1\2\63\uffff\1\6",
            "\1\4\2\uffff\1\2\63\uffff\1\6"
    };

    static final short[] DFA19_eot = DFA.unpackEncodedString(DFA19_eotS);
    static final short[] DFA19_eof = DFA.unpackEncodedString(DFA19_eofS);
    static final char[] DFA19_min = DFA.unpackEncodedStringToUnsignedChars(DFA19_minS);
    static final char[] DFA19_max = DFA.unpackEncodedStringToUnsignedChars(DFA19_maxS);
    static final short[] DFA19_accept = DFA.unpackEncodedString(DFA19_acceptS);
    static final short[] DFA19_special = DFA.unpackEncodedString(DFA19_specialS);
    static final short[][] DFA19_transition;

    static {
        int numStates = DFA19_transitionS.length;
        DFA19_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA19_transition[i] = DFA.unpackEncodedString(DFA19_transitionS[i]);
        }
    }

    class DFA19 extends DFA {

        public DFA19(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 19;
            this.eot = DFA19_eot;
            this.eof = DFA19_eof;
            this.min = DFA19_min;
            this.max = DFA19_max;
            this.accept = DFA19_accept;
            this.special = DFA19_special;
            this.transition = DFA19_transition;
        }
        public String getDescription() {
            return "418:3: ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )?";
        }
    }
    static final String DFA54_eotS =
        "\150\uffff";
    static final String DFA54_eofS =
        "\150\uffff";
    static final String DFA54_minS =
        "\1\7\1\uffff\1\4\1\uffff\2\4\1\0\5\4\2\uffff\4\4\1\0\1\4\1\0\5\4"+
        "\1\0\1\4\1\0\1\4\2\0\3\4\1\0\1\4\2\0\1\4\2\0\3\4\1\0\1\4\2\0\1\4"+
        "\2\0\3\4\1\0\3\4\1\0\3\4\1\0\1\4\1\0\1\4\1\uffff\44\0";
    static final String DFA54_maxS =
        "\1\114\1\uffff\1\125\1\uffff\2\125\1\0\5\125\2\uffff\4\125\1\0\1"+
        "\125\1\0\5\125\1\0\1\125\1\0\1\125\2\0\3\125\1\0\1\125\2\0\1\125"+
        "\2\0\3\125\1\0\1\125\2\0\1\125\2\0\3\125\1\0\3\125\1\0\3\125\1\0"+
        "\1\125\1\0\1\125\1\uffff\44\0";
    static final String DFA54_acceptS =
        "\1\uffff\1\1\1\uffff\1\3\10\uffff\2\2\65\uffff\1\2\44\uffff";
    static final String DFA54_specialS =
        "\1\53\1\uffff\1\24\1\uffff\1\43\1\47\1\70\1\6\1\22\1\15\1\76\1\3"+
        "\2\uffff\1\50\1\66\1\41\1\37\1\10\1\21\1\26\1\54\1\35\1\62\1\33"+
        "\1\30\1\65\1\2\1\52\1\1\1\36\1\7\1\44\1\72\1\11\1\4\1\20\1\67\1"+
        "\61\1\17\1\0\1\60\1\63\1\14\1\71\1\75\1\27\1\56\1\51\1\31\1\16\1"+
        "\40\1\23\1\55\1\57\1\34\1\45\1\73\1\12\1\25\1\46\1\74\1\13\1\5\1"+
        "\32\1\42\1\64\45\uffff}>";
    static final String[] DFA54_transitionS = {
            "\2\3\1\uffff\1\2\2\3\5\uffff\1\3\22\uffff\4\3\1\uffff\4\3\1"+
            "\uffff\1\3\16\uffff\1\1\1\uffff\1\3\13\uffff\1\3",
            "",
            "\3\14\1\13\2\14\1\6\1\14\1\15\30\14\1\5\1\14\1\4\2\14\1\7\1"+
            "\10\1\11\1\12\50\14",
            "",
            "\3\14\1\23\2\14\1\22\1\14\1\15\35\14\1\16\1\17\1\20\1\21\50"+
            "\14",
            "\3\14\1\31\2\14\1\24\1\14\1\15\35\14\1\25\1\26\1\27\1\30\50"+
            "\14",
            "\1\uffff",
            "\3\14\1\33\2\14\1\32\1\14\1\15\111\14",
            "\3\14\1\35\2\14\1\34\1\14\1\15\111\14",
            "\6\14\1\36\1\14\1\15\111\14",
            "\6\14\1\37\1\14\1\15\111\14",
            "\4\14\1\41\1\14\1\43\1\14\1\15\61\14\1\42\17\14\1\40\7\14",
            "",
            "",
            "\3\14\1\44\2\14\1\45\1\14\1\15\111\14",
            "\3\14\1\47\2\14\1\46\1\14\1\15\111\14",
            "\6\14\1\50\1\14\1\15\111\14",
            "\6\14\1\51\1\14\1\15\111\14",
            "\1\uffff",
            "\4\14\1\53\1\14\1\55\1\14\1\15\61\14\1\54\17\14\1\52\7\14",
            "\1\uffff",
            "\3\14\1\56\2\14\1\57\1\14\1\15\111\14",
            "\3\14\1\61\2\14\1\60\1\14\1\15\111\14",
            "\6\14\1\62\1\14\1\15\111\14",
            "\6\14\1\63\1\14\1\15\111\14",
            "\4\14\1\65\1\14\1\67\1\14\1\15\61\14\1\66\17\14\1\64\7\14",
            "\1\uffff",
            "\4\14\1\71\1\14\1\73\1\14\1\15\61\14\1\72\17\14\1\70\7\14",
            "\1\uffff",
            "\4\14\1\75\1\14\1\77\1\14\1\15\61\14\1\76\17\14\1\74\7\14",
            "\1\uffff",
            "\1\uffff",
            "\3\14\1\100\2\14\1\101\1\14\1\15\111\14",
            "\4\102\1\14\1\102\1\103\1\14\1\15\4\14\6\102\2\14\1\102\1\14"+
            "\1\102\6\14\2\102\5\14\1\102\5\14\4\102\10\14\1\102\4\14\2\102"+
            "\24\14",
            "\6\14\1\103\1\14\1\15\62\14\1\104\26\14",
            "\1\uffff",
            "\4\14\1\106\1\14\1\110\1\14\1\15\61\14\1\107\17\14\1\105\7\14",
            "\1\uffff",
            "\1\uffff",
            "\4\14\1\112\1\14\1\114\1\14\1\15\61\14\1\113\17\14\1\111\7\14",
            "\1\uffff",
            "\1\uffff",
            "\3\14\1\115\2\14\1\116\1\14\1\15\111\14",
            "\4\117\1\14\1\117\1\103\1\14\1\15\4\14\6\117\2\14\1\117\1\14"+
            "\1\117\6\14\2\117\5\14\1\117\5\14\4\117\10\14\1\117\4\14\2\117"+
            "\24\14",
            "\6\14\1\103\1\14\1\15\62\14\1\120\26\14",
            "\1\uffff",
            "\4\14\1\122\1\14\1\124\1\14\1\15\61\14\1\123\17\14\1\121\7\14",
            "\1\uffff",
            "\1\uffff",
            "\4\14\1\126\1\14\1\130\1\14\1\15\61\14\1\127\17\14\1\125\7\14",
            "\1\uffff",
            "\1\uffff",
            "\3\14\1\131\2\14\1\132\1\14\1\15\111\14",
            "\4\133\1\14\1\133\1\103\1\14\1\15\4\14\6\133\2\14\1\133\1\14"+
            "\1\133\6\14\2\133\5\14\1\133\5\14\4\133\10\14\1\133\4\14\2\133"+
            "\24\14",
            "\6\14\1\103\1\14\1\15\62\14\1\134\26\14",
            "\1\uffff",
            "\3\14\1\135\2\14\1\136\1\14\1\15\111\14",
            "\4\137\1\14\1\137\1\103\1\14\1\15\4\14\6\137\2\14\1\137\1\14"+
            "\1\137\6\14\2\137\5\14\1\137\5\14\4\137\10\14\1\137\4\14\2\137"+
            "\24\14",
            "\6\14\1\103\1\14\1\15\62\14\1\140\26\14",
            "\1\uffff",
            "\3\14\1\141\2\14\1\142\1\14\1\15\111\14",
            "\4\143\1\14\1\143\1\103\1\14\1\15\4\14\6\143\2\14\1\143\1\14"+
            "\1\143\6\14\2\143\5\14\1\143\5\14\4\143\10\14\1\143\4\14\2\143"+
            "\24\14",
            "\6\14\1\103\1\14\1\15\62\14\1\144\26\14",
            "\1\uffff",
            "\4\14\1\145\1\14\1\147\1\14\1\15\61\14\1\146\27\14",
            "\1\uffff",
            "\4\14\1\41\1\14\1\43\1\14\1\15\61\14\1\42\27\14",
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

    static final short[] DFA54_eot = DFA.unpackEncodedString(DFA54_eotS);
    static final short[] DFA54_eof = DFA.unpackEncodedString(DFA54_eofS);
    static final char[] DFA54_min = DFA.unpackEncodedStringToUnsignedChars(DFA54_minS);
    static final char[] DFA54_max = DFA.unpackEncodedStringToUnsignedChars(DFA54_maxS);
    static final short[] DFA54_accept = DFA.unpackEncodedString(DFA54_acceptS);
    static final short[] DFA54_special = DFA.unpackEncodedString(DFA54_specialS);
    static final short[][] DFA54_transition;

    static {
        int numStates = DFA54_transitionS.length;
        DFA54_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA54_transition[i] = DFA.unpackEncodedString(DFA54_transitionS[i]);
        }
    }

    class DFA54 extends DFA {

        public DFA54(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 54;
            this.eot = DFA54_eot;
            this.eof = DFA54_eof;
            this.min = DFA54_min;
            this.max = DFA54_max;
            this.accept = DFA54_accept;
            this.special = DFA54_special;
            this.transition = DFA54_transition;
        }
        public String getDescription() {
            return "1121:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )?";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA54_40 = input.LA(1);

                         
                        int index54_40 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_40);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA54_29 = input.LA(1);

                         
                        int index54_29 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_29==78) ) {s = 60;}

                        else if ( (LA54_29==DOT) ) {s = 61;}

                        else if ( (LA54_29==LEFT_SQUARE) ) {s = 62;}

                        else if ( (LA54_29==LEFT_PAREN) ) {s = 63;}

                        else if ( (LA54_29==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_29>=PACKAGE && LA54_29<=ID)||LA54_29==GLOBAL||LA54_29==COMMA||(LA54_29>=DECLARE && LA54_29<=NULL)||(LA54_29>=RIGHT_SQUARE && LA54_29<=77)||(LA54_29>=79 && LA54_29<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_29);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA54_27 = input.LA(1);

                         
                        int index54_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_27==78) ) {s = 56;}

                        else if ( (LA54_27==DOT) ) {s = 57;}

                        else if ( (LA54_27==LEFT_SQUARE) ) {s = 58;}

                        else if ( (LA54_27==LEFT_PAREN) ) {s = 59;}

                        else if ( (LA54_27==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_27>=PACKAGE && LA54_27<=ID)||LA54_27==GLOBAL||LA54_27==COMMA||(LA54_27>=DECLARE && LA54_27<=NULL)||(LA54_27>=RIGHT_SQUARE && LA54_27<=77)||(LA54_27>=79 && LA54_27<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_27);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA54_11 = input.LA(1);

                         
                        int index54_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_11==78) ) {s = 32;}

                        else if ( (LA54_11==DOT) ) {s = 33;}

                        else if ( (LA54_11==LEFT_SQUARE) ) {s = 34;}

                        else if ( (LA54_11==LEFT_PAREN) ) {s = 35;}

                        else if ( (LA54_11==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_11>=PACKAGE && LA54_11<=ID)||LA54_11==GLOBAL||LA54_11==COMMA||(LA54_11>=DECLARE && LA54_11<=NULL)||(LA54_11>=RIGHT_SQUARE && LA54_11<=77)||(LA54_11>=79 && LA54_11<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_11);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA54_35 = input.LA(1);

                         
                        int index54_35 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_35);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA54_63 = input.LA(1);

                         
                        int index54_63 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_63);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA54_7 = input.LA(1);

                         
                        int index54_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_7==LEFT_PAREN) ) {s = 26;}

                        else if ( (LA54_7==ID) ) {s = 27;}

                        else if ( (LA54_7==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_7>=PACKAGE && LA54_7<=FUNCTION)||(LA54_7>=DOT && LA54_7<=GLOBAL)||LA54_7==COMMA||(LA54_7>=DECLARE && LA54_7<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_7);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA54_31 = input.LA(1);

                         
                        int index54_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_31);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA54_18 = input.LA(1);

                         
                        int index54_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_18);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA54_34 = input.LA(1);

                         
                        int index54_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_34==RIGHT_SQUARE) ) {s = 68;}

                        else if ( (LA54_34==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_34>=PACKAGE && LA54_34<=GLOBAL)||LA54_34==COMMA||(LA54_34>=DECLARE && LA54_34<=LEFT_SQUARE)||(LA54_34>=THEN && LA54_34<=85)) && (synpred10())) {s = 12;}

                        else if ( (LA54_34==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index54_34);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA54_58 = input.LA(1);

                         
                        int index54_58 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_58==RIGHT_SQUARE) ) {s = 96;}

                        else if ( (LA54_58==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_58>=PACKAGE && LA54_58<=GLOBAL)||LA54_58==COMMA||(LA54_58>=DECLARE && LA54_58<=LEFT_SQUARE)||(LA54_58>=THEN && LA54_58<=85)) && (synpred10())) {s = 12;}

                        else if ( (LA54_58==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index54_58);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA54_62 = input.LA(1);

                         
                        int index54_62 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_62==RIGHT_SQUARE) ) {s = 100;}

                        else if ( (LA54_62==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_62>=PACKAGE && LA54_62<=GLOBAL)||LA54_62==COMMA||(LA54_62>=DECLARE && LA54_62<=LEFT_SQUARE)||(LA54_62>=THEN && LA54_62<=85)) && (synpred10())) {s = 12;}

                        else if ( (LA54_62==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index54_62);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA54_43 = input.LA(1);

                         
                        int index54_43 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA54_43>=PACKAGE && LA54_43<=ID)||LA54_43==GLOBAL||(LA54_43>=QUERY && LA54_43<=ATTRIBUTES)||LA54_43==ENABLED||LA54_43==SALIENCE||(LA54_43>=DURATION && LA54_43<=DIALECT)||LA54_43==FROM||(LA54_43>=INIT && LA54_43<=RESULT)||LA54_43==IN||(LA54_43>=THEN && LA54_43<=EVENT)) ) {s = 79;}

                        else if ( (LA54_43==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA54_43==DOT||LA54_43==COMMA||(LA54_43>=DECLARE && LA54_43<=STRING)||(LA54_43>=DATE_EFFECTIVE && LA54_43<=DATE_EXPIRES)||LA54_43==BOOL||(LA54_43>=INT && LA54_43<=AGENDA_GROUP)||(LA54_43>=LOCK_ON_ACTIVE && LA54_43<=DOUBLE_AMPER)||(LA54_43>=EXISTS && LA54_43<=ACCUMULATE)||(LA54_43>=COLLECT && LA54_43<=TILDE)||(LA54_43>=FLOAT && LA54_43<=RIGHT_SQUARE)||(LA54_43>=EOL && LA54_43<=85)) && (synpred10())) {s = 12;}

                        else if ( (LA54_43==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index54_43);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA54_9 = input.LA(1);

                         
                        int index54_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_9==LEFT_PAREN) ) {s = 30;}

                        else if ( (LA54_9==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_9>=PACKAGE && LA54_9<=GLOBAL)||LA54_9==COMMA||(LA54_9>=DECLARE && LA54_9<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_9);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA54_50 = input.LA(1);

                         
                        int index54_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_50);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA54_39 = input.LA(1);

                         
                        int index54_39 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_39==78) ) {s = 73;}

                        else if ( (LA54_39==DOT) ) {s = 74;}

                        else if ( (LA54_39==LEFT_SQUARE) ) {s = 75;}

                        else if ( (LA54_39==LEFT_PAREN) ) {s = 76;}

                        else if ( (LA54_39==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_39>=PACKAGE && LA54_39<=ID)||LA54_39==GLOBAL||LA54_39==COMMA||(LA54_39>=DECLARE && LA54_39<=NULL)||(LA54_39>=RIGHT_SQUARE && LA54_39<=77)||(LA54_39>=79 && LA54_39<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_39);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA54_36 = input.LA(1);

                         
                        int index54_36 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_36==78) ) {s = 69;}

                        else if ( (LA54_36==DOT) ) {s = 70;}

                        else if ( (LA54_36==LEFT_SQUARE) ) {s = 71;}

                        else if ( (LA54_36==LEFT_PAREN) ) {s = 72;}

                        else if ( (LA54_36==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_36>=PACKAGE && LA54_36<=ID)||LA54_36==GLOBAL||LA54_36==COMMA||(LA54_36>=DECLARE && LA54_36<=NULL)||(LA54_36>=RIGHT_SQUARE && LA54_36<=77)||(LA54_36>=79 && LA54_36<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_36);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA54_19 = input.LA(1);

                         
                        int index54_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_19==78) ) {s = 42;}

                        else if ( (LA54_19==DOT) ) {s = 43;}

                        else if ( (LA54_19==LEFT_SQUARE) ) {s = 44;}

                        else if ( (LA54_19==LEFT_PAREN) ) {s = 45;}

                        else if ( (LA54_19==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_19>=PACKAGE && LA54_19<=ID)||LA54_19==GLOBAL||LA54_19==COMMA||(LA54_19>=DECLARE && LA54_19<=NULL)||(LA54_19>=RIGHT_SQUARE && LA54_19<=77)||(LA54_19>=79 && LA54_19<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_19);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA54_8 = input.LA(1);

                         
                        int index54_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_8==LEFT_PAREN) ) {s = 28;}

                        else if ( (LA54_8==ID) ) {s = 29;}

                        else if ( (LA54_8==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_8>=PACKAGE && LA54_8<=FUNCTION)||(LA54_8>=DOT && LA54_8<=GLOBAL)||LA54_8==COMMA||(LA54_8>=DECLARE && LA54_8<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_8);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA54_52 = input.LA(1);

                         
                        int index54_52 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_52==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA54_52==ID) ) {s = 89;}

                        else if ( (LA54_52==LEFT_PAREN) ) {s = 90;}

                        else if ( ((LA54_52>=PACKAGE && LA54_52<=FUNCTION)||(LA54_52>=DOT && LA54_52<=GLOBAL)||LA54_52==COMMA||(LA54_52>=DECLARE && LA54_52<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_52);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA54_2 = input.LA(1);

                         
                        int index54_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_2==AND) ) {s = 4;}

                        else if ( (LA54_2==OR) ) {s = 5;}

                        else if ( (LA54_2==LEFT_PAREN) ) {s = 6;}

                        else if ( (LA54_2==EXISTS) ) {s = 7;}

                        else if ( (LA54_2==NOT) ) {s = 8;}

                        else if ( (LA54_2==EVAL) ) {s = 9;}

                        else if ( (LA54_2==FORALL) ) {s = 10;}

                        else if ( (LA54_2==ID) ) {s = 11;}

                        else if ( ((LA54_2>=PACKAGE && LA54_2<=FUNCTION)||(LA54_2>=DOT && LA54_2<=GLOBAL)||LA54_2==COMMA||(LA54_2>=DECLARE && LA54_2<=LOCK_ON_ACTIVE)||LA54_2==DOUBLE_PIPE||(LA54_2>=DOUBLE_AMPER && LA54_2<=FROM)||(LA54_2>=ACCUMULATE && LA54_2<=85)) && (synpred10())) {s = 12;}

                        else if ( (LA54_2==RIGHT_PAREN) && (synpred10())) {s = 13;}

                         
                        input.seek(index54_2);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA54_59 = input.LA(1);

                         
                        int index54_59 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_59);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA54_20 = input.LA(1);

                         
                        int index54_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_20);
                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA54_46 = input.LA(1);

                         
                        int index54_46 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_46==78) ) {s = 81;}

                        else if ( (LA54_46==DOT) ) {s = 82;}

                        else if ( (LA54_46==LEFT_SQUARE) ) {s = 83;}

                        else if ( (LA54_46==LEFT_PAREN) ) {s = 84;}

                        else if ( (LA54_46==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_46>=PACKAGE && LA54_46<=ID)||LA54_46==GLOBAL||LA54_46==COMMA||(LA54_46>=DECLARE && LA54_46<=NULL)||(LA54_46>=RIGHT_SQUARE && LA54_46<=77)||(LA54_46>=79 && LA54_46<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_46);
                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA54_25 = input.LA(1);

                         
                        int index54_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_25==78) ) {s = 52;}

                        else if ( (LA54_25==DOT) ) {s = 53;}

                        else if ( (LA54_25==LEFT_SQUARE) ) {s = 54;}

                        else if ( (LA54_25==LEFT_PAREN) ) {s = 55;}

                        else if ( (LA54_25==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_25>=PACKAGE && LA54_25<=ID)||LA54_25==GLOBAL||LA54_25==COMMA||(LA54_25>=DECLARE && LA54_25<=NULL)||(LA54_25>=RIGHT_SQUARE && LA54_25<=77)||(LA54_25>=79 && LA54_25<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_25);
                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA54_49 = input.LA(1);

                         
                        int index54_49 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_49==78) ) {s = 85;}

                        else if ( (LA54_49==DOT) ) {s = 86;}

                        else if ( (LA54_49==LEFT_SQUARE) ) {s = 87;}

                        else if ( (LA54_49==LEFT_PAREN) ) {s = 88;}

                        else if ( (LA54_49==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_49>=PACKAGE && LA54_49<=ID)||LA54_49==GLOBAL||LA54_49==COMMA||(LA54_49>=DECLARE && LA54_49<=NULL)||(LA54_49>=RIGHT_SQUARE && LA54_49<=77)||(LA54_49>=79 && LA54_49<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_49);
                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA54_64 = input.LA(1);

                         
                        int index54_64 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_64==DOT) ) {s = 101;}

                        else if ( (LA54_64==LEFT_SQUARE) ) {s = 102;}

                        else if ( (LA54_64==LEFT_PAREN) ) {s = 103;}

                        else if ( (LA54_64==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_64>=PACKAGE && LA54_64<=ID)||LA54_64==GLOBAL||LA54_64==COMMA||(LA54_64>=DECLARE && LA54_64<=NULL)||(LA54_64>=RIGHT_SQUARE && LA54_64<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_64);
                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA54_24 = input.LA(1);

                         
                        int index54_24 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_24==LEFT_PAREN) ) {s = 51;}

                        else if ( (LA54_24==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_24>=PACKAGE && LA54_24<=GLOBAL)||LA54_24==COMMA||(LA54_24>=DECLARE && LA54_24<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_24);
                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA54_55 = input.LA(1);

                         
                        int index54_55 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_55);
                        if ( s>=0 ) return s;
                        break;
                    case 29 : 
                        int LA54_22 = input.LA(1);

                         
                        int index54_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_22==LEFT_PAREN) ) {s = 48;}

                        else if ( (LA54_22==ID) ) {s = 49;}

                        else if ( (LA54_22==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_22>=PACKAGE && LA54_22<=FUNCTION)||(LA54_22>=DOT && LA54_22<=GLOBAL)||LA54_22==COMMA||(LA54_22>=DECLARE && LA54_22<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_22);
                        if ( s>=0 ) return s;
                        break;
                    case 30 : 
                        int LA54_30 = input.LA(1);

                         
                        int index54_30 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_30);
                        if ( s>=0 ) return s;
                        break;
                    case 31 : 
                        int LA54_17 = input.LA(1);

                         
                        int index54_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_17==LEFT_PAREN) ) {s = 41;}

                        else if ( (LA54_17==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_17>=PACKAGE && LA54_17<=GLOBAL)||LA54_17==COMMA||(LA54_17>=DECLARE && LA54_17<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_17);
                        if ( s>=0 ) return s;
                        break;
                    case 32 : 
                        int LA54_51 = input.LA(1);

                         
                        int index54_51 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_51);
                        if ( s>=0 ) return s;
                        break;
                    case 33 : 
                        int LA54_16 = input.LA(1);

                         
                        int index54_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_16==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_16>=PACKAGE && LA54_16<=GLOBAL)||LA54_16==COMMA||(LA54_16>=DECLARE && LA54_16<=85)) && (synpred10())) {s = 12;}

                        else if ( (LA54_16==LEFT_PAREN) ) {s = 40;}

                         
                        input.seek(index54_16);
                        if ( s>=0 ) return s;
                        break;
                    case 34 : 
                        int LA54_65 = input.LA(1);

                         
                        int index54_65 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_65);
                        if ( s>=0 ) return s;
                        break;
                    case 35 : 
                        int LA54_4 = input.LA(1);

                         
                        int index54_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_4==EXISTS) ) {s = 14;}

                        else if ( (LA54_4==NOT) ) {s = 15;}

                        else if ( (LA54_4==EVAL) ) {s = 16;}

                        else if ( (LA54_4==FORALL) ) {s = 17;}

                        else if ( (LA54_4==LEFT_PAREN) ) {s = 18;}

                        else if ( (LA54_4==ID) ) {s = 19;}

                        else if ( (LA54_4==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_4>=PACKAGE && LA54_4<=FUNCTION)||(LA54_4>=DOT && LA54_4<=GLOBAL)||LA54_4==COMMA||(LA54_4>=DECLARE && LA54_4<=FROM)||(LA54_4>=ACCUMULATE && LA54_4<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_4);
                        if ( s>=0 ) return s;
                        break;
                    case 36 : 
                        int LA54_32 = input.LA(1);

                         
                        int index54_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_32==ID) ) {s = 64;}

                        else if ( (LA54_32==LEFT_PAREN) ) {s = 65;}

                        else if ( (LA54_32==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_32>=PACKAGE && LA54_32<=FUNCTION)||(LA54_32>=DOT && LA54_32<=GLOBAL)||LA54_32==COMMA||(LA54_32>=DECLARE && LA54_32<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_32);
                        if ( s>=0 ) return s;
                        break;
                    case 37 : 
                        int LA54_56 = input.LA(1);

                         
                        int index54_56 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_56==ID) ) {s = 93;}

                        else if ( (LA54_56==LEFT_PAREN) ) {s = 94;}

                        else if ( (LA54_56==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_56>=PACKAGE && LA54_56<=FUNCTION)||(LA54_56>=DOT && LA54_56<=GLOBAL)||LA54_56==COMMA||(LA54_56>=DECLARE && LA54_56<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_56);
                        if ( s>=0 ) return s;
                        break;
                    case 38 : 
                        int LA54_60 = input.LA(1);

                         
                        int index54_60 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_60==ID) ) {s = 97;}

                        else if ( (LA54_60==LEFT_PAREN) ) {s = 98;}

                        else if ( (LA54_60==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_60>=PACKAGE && LA54_60<=FUNCTION)||(LA54_60>=DOT && LA54_60<=GLOBAL)||LA54_60==COMMA||(LA54_60>=DECLARE && LA54_60<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_60);
                        if ( s>=0 ) return s;
                        break;
                    case 39 : 
                        int LA54_5 = input.LA(1);

                         
                        int index54_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_5==LEFT_PAREN) ) {s = 20;}

                        else if ( (LA54_5==EXISTS) ) {s = 21;}

                        else if ( (LA54_5==NOT) ) {s = 22;}

                        else if ( (LA54_5==EVAL) ) {s = 23;}

                        else if ( (LA54_5==FORALL) ) {s = 24;}

                        else if ( (LA54_5==ID) ) {s = 25;}

                        else if ( (LA54_5==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_5>=PACKAGE && LA54_5<=FUNCTION)||(LA54_5>=DOT && LA54_5<=GLOBAL)||LA54_5==COMMA||(LA54_5>=DECLARE && LA54_5<=FROM)||(LA54_5>=ACCUMULATE && LA54_5<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_5);
                        if ( s>=0 ) return s;
                        break;
                    case 40 : 
                        int LA54_14 = input.LA(1);

                         
                        int index54_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_14==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA54_14==ID) ) {s = 36;}

                        else if ( (LA54_14==LEFT_PAREN) ) {s = 37;}

                        else if ( ((LA54_14>=PACKAGE && LA54_14<=FUNCTION)||(LA54_14>=DOT && LA54_14<=GLOBAL)||LA54_14==COMMA||(LA54_14>=DECLARE && LA54_14<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_14);
                        if ( s>=0 ) return s;
                        break;
                    case 41 : 
                        int LA54_48 = input.LA(1);

                         
                        int index54_48 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_48);
                        if ( s>=0 ) return s;
                        break;
                    case 42 : 
                        int LA54_28 = input.LA(1);

                         
                        int index54_28 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_28);
                        if ( s>=0 ) return s;
                        break;
                    case 43 : 
                        int LA54_0 = input.LA(1);

                         
                        int index54_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_0==LEFT_SQUARE) && (synpred9())) {s = 1;}

                        else if ( (LA54_0==LEFT_PAREN) ) {s = 2;}

                        else if ( ((LA54_0>=ID && LA54_0<=DOT)||(LA54_0>=COMMA && LA54_0<=RIGHT_PAREN)||LA54_0==END||(LA54_0>=OR && LA54_0<=DOUBLE_AMPER)||(LA54_0>=EXISTS && LA54_0<=FORALL)||LA54_0==INIT||LA54_0==THEN||LA54_0==76) ) {s = 3;}

                         
                        input.seek(index54_0);
                        if ( s>=0 ) return s;
                        break;
                    case 44 : 
                        int LA54_21 = input.LA(1);

                         
                        int index54_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_21==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA54_21==ID) ) {s = 46;}

                        else if ( (LA54_21==LEFT_PAREN) ) {s = 47;}

                        else if ( ((LA54_21>=PACKAGE && LA54_21<=FUNCTION)||(LA54_21>=DOT && LA54_21<=GLOBAL)||LA54_21==COMMA||(LA54_21>=DECLARE && LA54_21<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_21);
                        if ( s>=0 ) return s;
                        break;
                    case 45 : 
                        int LA54_53 = input.LA(1);

                         
                        int index54_53 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA54_53>=PACKAGE && LA54_53<=ID)||LA54_53==GLOBAL||(LA54_53>=QUERY && LA54_53<=ATTRIBUTES)||LA54_53==ENABLED||LA54_53==SALIENCE||(LA54_53>=DURATION && LA54_53<=DIALECT)||LA54_53==FROM||(LA54_53>=INIT && LA54_53<=RESULT)||LA54_53==IN||(LA54_53>=THEN && LA54_53<=EVENT)) ) {s = 91;}

                        else if ( (LA54_53==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA54_53==DOT||LA54_53==COMMA||(LA54_53>=DECLARE && LA54_53<=STRING)||(LA54_53>=DATE_EFFECTIVE && LA54_53<=DATE_EXPIRES)||LA54_53==BOOL||(LA54_53>=INT && LA54_53<=AGENDA_GROUP)||(LA54_53>=LOCK_ON_ACTIVE && LA54_53<=DOUBLE_AMPER)||(LA54_53>=EXISTS && LA54_53<=ACCUMULATE)||(LA54_53>=COLLECT && LA54_53<=TILDE)||(LA54_53>=FLOAT && LA54_53<=RIGHT_SQUARE)||(LA54_53>=EOL && LA54_53<=85)) && (synpred10())) {s = 12;}

                        else if ( (LA54_53==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index54_53);
                        if ( s>=0 ) return s;
                        break;
                    case 46 : 
                        int LA54_47 = input.LA(1);

                         
                        int index54_47 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_47);
                        if ( s>=0 ) return s;
                        break;
                    case 47 : 
                        int LA54_54 = input.LA(1);

                         
                        int index54_54 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_54==RIGHT_SQUARE) ) {s = 92;}

                        else if ( (LA54_54==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_54>=PACKAGE && LA54_54<=GLOBAL)||LA54_54==COMMA||(LA54_54>=DECLARE && LA54_54<=LEFT_SQUARE)||(LA54_54>=THEN && LA54_54<=85)) && (synpred10())) {s = 12;}

                        else if ( (LA54_54==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index54_54);
                        if ( s>=0 ) return s;
                        break;
                    case 48 : 
                        int LA54_41 = input.LA(1);

                         
                        int index54_41 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_41);
                        if ( s>=0 ) return s;
                        break;
                    case 49 : 
                        int LA54_38 = input.LA(1);

                         
                        int index54_38 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_38);
                        if ( s>=0 ) return s;
                        break;
                    case 50 : 
                        int LA54_23 = input.LA(1);

                         
                        int index54_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_23==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_23>=PACKAGE && LA54_23<=GLOBAL)||LA54_23==COMMA||(LA54_23>=DECLARE && LA54_23<=85)) && (synpred10())) {s = 12;}

                        else if ( (LA54_23==LEFT_PAREN) ) {s = 50;}

                         
                        input.seek(index54_23);
                        if ( s>=0 ) return s;
                        break;
                    case 51 : 
                        int LA54_42 = input.LA(1);

                         
                        int index54_42 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_42==ID) ) {s = 77;}

                        else if ( (LA54_42==LEFT_PAREN) ) {s = 78;}

                        else if ( (LA54_42==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_42>=PACKAGE && LA54_42<=FUNCTION)||(LA54_42>=DOT && LA54_42<=GLOBAL)||LA54_42==COMMA||(LA54_42>=DECLARE && LA54_42<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_42);
                        if ( s>=0 ) return s;
                        break;
                    case 52 : 
                        int LA54_66 = input.LA(1);

                         
                        int index54_66 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_66==LEFT_SQUARE) ) {s = 34;}

                        else if ( (LA54_66==LEFT_PAREN) ) {s = 35;}

                        else if ( (LA54_66==DOT) ) {s = 33;}

                        else if ( (LA54_66==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_66>=PACKAGE && LA54_66<=ID)||LA54_66==GLOBAL||LA54_66==COMMA||(LA54_66>=DECLARE && LA54_66<=NULL)||(LA54_66>=RIGHT_SQUARE && LA54_66<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_66);
                        if ( s>=0 ) return s;
                        break;
                    case 53 : 
                        int LA54_26 = input.LA(1);

                         
                        int index54_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_26);
                        if ( s>=0 ) return s;
                        break;
                    case 54 : 
                        int LA54_15 = input.LA(1);

                         
                        int index54_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_15==LEFT_PAREN) ) {s = 38;}

                        else if ( (LA54_15==ID) ) {s = 39;}

                        else if ( (LA54_15==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_15>=PACKAGE && LA54_15<=FUNCTION)||(LA54_15>=DOT && LA54_15<=GLOBAL)||LA54_15==COMMA||(LA54_15>=DECLARE && LA54_15<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_15);
                        if ( s>=0 ) return s;
                        break;
                    case 55 : 
                        int LA54_37 = input.LA(1);

                         
                        int index54_37 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_37);
                        if ( s>=0 ) return s;
                        break;
                    case 56 : 
                        int LA54_6 = input.LA(1);

                         
                        int index54_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_6);
                        if ( s>=0 ) return s;
                        break;
                    case 57 : 
                        int LA54_44 = input.LA(1);

                         
                        int index54_44 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_44==RIGHT_SQUARE) ) {s = 80;}

                        else if ( (LA54_44==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_44>=PACKAGE && LA54_44<=GLOBAL)||LA54_44==COMMA||(LA54_44>=DECLARE && LA54_44<=LEFT_SQUARE)||(LA54_44>=THEN && LA54_44<=85)) && (synpred10())) {s = 12;}

                        else if ( (LA54_44==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index54_44);
                        if ( s>=0 ) return s;
                        break;
                    case 58 : 
                        int LA54_33 = input.LA(1);

                         
                        int index54_33 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA54_33>=PACKAGE && LA54_33<=ID)||LA54_33==GLOBAL||(LA54_33>=QUERY && LA54_33<=ATTRIBUTES)||LA54_33==ENABLED||LA54_33==SALIENCE||(LA54_33>=DURATION && LA54_33<=DIALECT)||LA54_33==FROM||(LA54_33>=INIT && LA54_33<=RESULT)||LA54_33==IN||(LA54_33>=THEN && LA54_33<=EVENT)) ) {s = 66;}

                        else if ( (LA54_33==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA54_33==DOT||LA54_33==COMMA||(LA54_33>=DECLARE && LA54_33<=STRING)||(LA54_33>=DATE_EFFECTIVE && LA54_33<=DATE_EXPIRES)||LA54_33==BOOL||(LA54_33>=INT && LA54_33<=AGENDA_GROUP)||(LA54_33>=LOCK_ON_ACTIVE && LA54_33<=DOUBLE_AMPER)||(LA54_33>=EXISTS && LA54_33<=ACCUMULATE)||(LA54_33>=COLLECT && LA54_33<=TILDE)||(LA54_33>=FLOAT && LA54_33<=RIGHT_SQUARE)||(LA54_33>=EOL && LA54_33<=85)) && (synpred10())) {s = 12;}

                        else if ( (LA54_33==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index54_33);
                        if ( s>=0 ) return s;
                        break;
                    case 59 : 
                        int LA54_57 = input.LA(1);

                         
                        int index54_57 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA54_57>=PACKAGE && LA54_57<=ID)||LA54_57==GLOBAL||(LA54_57>=QUERY && LA54_57<=ATTRIBUTES)||LA54_57==ENABLED||LA54_57==SALIENCE||(LA54_57>=DURATION && LA54_57<=DIALECT)||LA54_57==FROM||(LA54_57>=INIT && LA54_57<=RESULT)||LA54_57==IN||(LA54_57>=THEN && LA54_57<=EVENT)) ) {s = 95;}

                        else if ( (LA54_57==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA54_57==DOT||LA54_57==COMMA||(LA54_57>=DECLARE && LA54_57<=STRING)||(LA54_57>=DATE_EFFECTIVE && LA54_57<=DATE_EXPIRES)||LA54_57==BOOL||(LA54_57>=INT && LA54_57<=AGENDA_GROUP)||(LA54_57>=LOCK_ON_ACTIVE && LA54_57<=DOUBLE_AMPER)||(LA54_57>=EXISTS && LA54_57<=ACCUMULATE)||(LA54_57>=COLLECT && LA54_57<=TILDE)||(LA54_57>=FLOAT && LA54_57<=RIGHT_SQUARE)||(LA54_57>=EOL && LA54_57<=85)) && (synpred10())) {s = 12;}

                        else if ( (LA54_57==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index54_57);
                        if ( s>=0 ) return s;
                        break;
                    case 60 : 
                        int LA54_61 = input.LA(1);

                         
                        int index54_61 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_61==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_61>=PACKAGE && LA54_61<=ID)||LA54_61==GLOBAL||(LA54_61>=QUERY && LA54_61<=ATTRIBUTES)||LA54_61==ENABLED||LA54_61==SALIENCE||(LA54_61>=DURATION && LA54_61<=DIALECT)||LA54_61==FROM||(LA54_61>=INIT && LA54_61<=RESULT)||LA54_61==IN||(LA54_61>=THEN && LA54_61<=EVENT)) ) {s = 99;}

                        else if ( (LA54_61==LEFT_PAREN) && (synpred10())) {s = 67;}

                        else if ( (LA54_61==DOT||LA54_61==COMMA||(LA54_61>=DECLARE && LA54_61<=STRING)||(LA54_61>=DATE_EFFECTIVE && LA54_61<=DATE_EXPIRES)||LA54_61==BOOL||(LA54_61>=INT && LA54_61<=AGENDA_GROUP)||(LA54_61>=LOCK_ON_ACTIVE && LA54_61<=DOUBLE_AMPER)||(LA54_61>=EXISTS && LA54_61<=ACCUMULATE)||(LA54_61>=COLLECT && LA54_61<=TILDE)||(LA54_61>=FLOAT && LA54_61<=RIGHT_SQUARE)||(LA54_61>=EOL && LA54_61<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_61);
                        if ( s>=0 ) return s;
                        break;
                    case 61 : 
                        int LA54_45 = input.LA(1);

                         
                        int index54_45 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index54_45);
                        if ( s>=0 ) return s;
                        break;
                    case 62 : 
                        int LA54_10 = input.LA(1);

                         
                        int index54_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA54_10==LEFT_PAREN) ) {s = 31;}

                        else if ( (LA54_10==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA54_10>=PACKAGE && LA54_10<=GLOBAL)||LA54_10==COMMA||(LA54_10>=DECLARE && LA54_10<=85)) && (synpred10())) {s = 12;}

                         
                        input.seek(index54_10);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 54, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_76_in_opt_semicolon39 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit57 = new BitSet(new long[]{0x0000001FEB9A2260L});
    public static final BitSet FOLLOW_statement_in_compilation_unit62 = new BitSet(new long[]{0x0000001FEB9A2260L});
    public static final BitSet FOLLOW_EOF_in_compilation_unit67 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_package_statement_in_prolog96 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_attribute_in_statement121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_declaration_in_statement196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_package_statement222 = new BitSet(new long[]{0x0807820C0A7E02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement226 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_opt_semicolon_in_package_statement228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_statement259 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_import_name_in_import_statement282 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_opt_semicolon_in_import_statement285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_function_import_statement309 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_FUNCTION_in_function_import_statement311 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement334 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_opt_semicolon_in_function_import_statement337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_name362 = new BitSet(new long[]{0x0000000000000102L,0x0000000000002000L});
    public static final BitSet FOLLOW_DOT_in_import_name374 = new BitSet(new long[]{0x0807820C0A7E02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_identifier_in_import_name378 = new BitSet(new long[]{0x0000000000000102L,0x0000000000002000L});
    public static final BitSet FOLLOW_77_in_import_name402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GLOBAL_in_global436 = new BitSet(new long[]{0x0807820C0A7E02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_dotted_name_in_global447 = new BitSet(new long[]{0x0807820C0A7E02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_identifier_in_global458 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_opt_semicolon_in_global460 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function485 = new BitSet(new long[]{0x0807820C0A7E02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_dotted_name_in_function489 = new BitSet(new long[]{0x0807820C0A7E02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_identifier_in_function494 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function503 = new BitSet(new long[]{0x0807820C0A7E12F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_dotted_name_in_function512 = new BitSet(new long[]{0x0807820C0A7E02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_argument_in_function517 = new BitSet(new long[]{0x0000000000001800L});
    public static final BitSet FOLLOW_COMMA_in_function531 = new BitSet(new long[]{0x0807820C0A7E02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_dotted_name_in_function535 = new BitSet(new long[]{0x0807820C0A7E02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_argument_in_function540 = new BitSet(new long[]{0x0000000000001800L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function564 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_curly_chunk_in_function570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_argument597 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument603 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument605 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_DECLARE_in_type_declaration645 = new BitSet(new long[]{0x0807820C0A7E02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_identifier_in_type_declaration649 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_type_declaration694 = new BitSet(new long[]{0x0807820C0A7E02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_type_decl_attribute_in_type_declaration720 = new BitSet(new long[]{0x0000000000008800L});
    public static final BitSet FOLLOW_COMMA_in_type_declaration725 = new BitSet(new long[]{0x0807820C0A7E02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_type_decl_attribute_in_type_declaration727 = new BitSet(new long[]{0x0000000000008800L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_type_declaration749 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_type_decl_attribute785 = new BitSet(new long[]{0x0807820C0A7F02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_STRING_in_type_decl_attribute808 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_type_decl_attribute849 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_query922 = new BitSet(new long[]{0x0000000000010080L});
    public static final BitSet FOLLOW_name_in_query926 = new BitSet(new long[]{0x00003C0000040480L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_query936 = new BitSet(new long[]{0x0000000000001080L});
    public static final BitSet FOLLOW_qualified_id_in_query971 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ID_in_query976 = new BitSet(new long[]{0x0000000000001800L});
    public static final BitSet FOLLOW_COMMA_in_query997 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_qualified_id_in_query1001 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ID_in_query1006 = new BitSet(new long[]{0x0000000000001800L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_query1056 = new BitSet(new long[]{0x00003C0000040480L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query1085 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_END_in_query1090 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_opt_semicolon_in_query1092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_template1120 = new BitSet(new long[]{0x0000000000010080L});
    public static final BitSet FOLLOW_name_in_template1124 = new BitSet(new long[]{0x0000000000000080L,0x0000000000001000L});
    public static final BitSet FOLLOW_opt_semicolon_in_template1126 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_template_slot_in_template1141 = new BitSet(new long[]{0x0000000000040080L});
    public static final BitSet FOLLOW_END_in_template1156 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_opt_semicolon_in_template1158 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualified_id_in_template_slot1204 = new BitSet(new long[]{0x0807820C0A7E02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_identifier_in_template_slot1220 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_opt_semicolon_in_template_slot1222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule1253 = new BitSet(new long[]{0x0000000000010080L});
    public static final BitSet FOLLOW_name_in_rule1257 = new BitSet(new long[]{0x0000001FEBE00000L,0x0000000000000001L});
    public static final BitSet FOLLOW_rule_attributes_in_rule1266 = new BitSet(new long[]{0x0000000000200000L,0x0000000000000001L});
    public static final BitSet FOLLOW_WHEN_in_rule1278 = new BitSet(new long[]{0x00003C0000000480L,0x0000000000004001L});
    public static final BitSet FOLLOW_78_in_rule1280 = new BitSet(new long[]{0x00003C0000000480L,0x0000000000000001L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule1291 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_rhs_chunk_in_rule1301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_rule_attributes1321 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_78_in_rule_attributes1323 = new BitSet(new long[]{0x0000001FEB800000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1331 = new BitSet(new long[]{0x0000001FEB800802L});
    public static final BitSet FOLLOW_COMMA_in_rule_attributes1338 = new BitSet(new long[]{0x0000001FEB800000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1343 = new BitSet(new long[]{0x0000001FEB800802L});
    public static final BitSet FOLLOW_salience_in_rule_attribute1380 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute1388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute1397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute1406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute1415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute1431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute1439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute1447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_in_rule_attribute1455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_in_rule_attribute1463 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_in_rule_attribute1470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EFFECTIVE_in_date_effective1496 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_STRING_in_date_effective1498 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EXPIRES_in_date_expires1527 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_STRING_in_date_expires1529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENABLED_in_enabled1558 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_BOOL_in_enabled1560 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_salience1593 = new BitSet(new long[]{0x0000000010000400L});
    public static final BitSet FOLLOW_INT_in_salience1604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_salience1619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NO_LOOP_in_no_loop1649 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AUTO_FOCUS_in_auto_focus1697 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTIVATION_GROUP_in_activation_group1746 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_STRING_in_activation_group1748 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1776 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_STRING_in_ruleflow_group1778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AGENDA_GROUP_in_agenda_group1806 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1808 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DURATION_in_duration1836 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_INT_in_duration1838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIALECT_in_dialect1866 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_STRING_in_dialect1868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1900 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_BOOL_in_lock_on_active1913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1952 = new BitSet(new long[]{0x00003C0000000482L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_or2014 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_OR_in_lhs_or2016 = new BitSet(new long[]{0x00003C0000000480L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2029 = new BitSet(new long[]{0x00003C0000001480L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_or2040 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2058 = new BitSet(new long[]{0x0000006000000002L});
    public static final BitSet FOLLOW_set_in_lhs_or2066 = new BitSet(new long[]{0x00003C0000000480L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2082 = new BitSet(new long[]{0x0000006000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_and2113 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_AND_in_lhs_and2115 = new BitSet(new long[]{0x00003C0000000480L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2127 = new BitSet(new long[]{0x00003C0000001480L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_and2137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2155 = new BitSet(new long[]{0x0000018000000002L});
    public static final BitSet FOLLOW_set_in_lhs_and2163 = new BitSet(new long[]{0x00003C0000000480L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2179 = new BitSet(new long[]{0x0000018000000002L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary2224 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary2242 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary2261 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary2280 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary2297 = new BitSet(new long[]{0x00003C0000000480L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_unary2301 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary2303 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_pattern_source_in_lhs_unary2314 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_opt_semicolon_in_lhs_unary2326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_pattern_source2353 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_FROM_in_pattern_source2404 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_entrypoint_statement_in_pattern_source2408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_pattern_source2428 = new BitSet(new long[]{0x080FC20C0A7E02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_accumulate_statement_in_pattern_source2486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collect_statement_in_pattern_source2509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_statement_in_pattern_source2546 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_lhs_exist2589 = new BitSet(new long[]{0x0000000000000480L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist2609 = new BitSet(new long[]{0x00003C0000000480L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist2613 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist2643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_exist2693 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_lhs_not2745 = new BitSet(new long[]{0x0000000000000480L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not2758 = new BitSet(new long[]{0x00003C0000000480L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not2762 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not2793 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_not2830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_lhs_eval2876 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval2887 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_lhs_forall2913 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall2915 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall2919 = new BitSet(new long[]{0x0000000000001080L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall2934 = new BitSet(new long[]{0x0000000000001080L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall2950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_pattern2983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_pattern2991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_source_in_from_statement3018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_accumulate_statement3055 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement3065 = new BitSet(new long[]{0x00003C0000000480L});
    public static final BitSet FOLLOW_lhs_or_in_accumulate_statement3069 = new BitSet(new long[]{0x0000800000000880L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement3071 = new BitSet(new long[]{0x0000800000000080L});
    public static final BitSet FOLLOW_INIT_in_accumulate_statement3089 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement3102 = new BitSet(new long[]{0x0001000000000800L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement3104 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_ACTION_in_accumulate_statement3115 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement3119 = new BitSet(new long[]{0x0006000000000800L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement3121 = new BitSet(new long[]{0x0006000000000000L});
    public static final BitSet FOLLOW_REVERSE_in_accumulate_statement3134 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement3138 = new BitSet(new long[]{0x0004000000000800L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement3140 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_RESULT_in_accumulate_statement3157 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement3161 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_accumulate_statement3187 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement3191 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement3208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_from_source3239 = new BitSet(new long[]{0x0000000000000502L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source3267 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_expression_chain_in_from_source3280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_expression_chain3311 = new BitSet(new long[]{0x0807820C0A7E02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_identifier_in_expression_chain3315 = new BitSet(new long[]{0x4000000000000502L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain3346 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain3379 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain3394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_collect_statement3445 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement3455 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_pattern_source_in_collect_statement3459 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement3461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENTRY_POINT_in_entrypoint_statement3498 = new BitSet(new long[]{0x0000000000010080L});
    public static final BitSet FOLLOW_name_in_entrypoint_statement3510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding3542 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_78_in_fact_binding3544 = new BitSet(new long[]{0x0000000000000480L});
    public static final BitSet FOLLOW_fact_in_fact_binding3558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_binding3574 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_fact_in_fact_binding3578 = new BitSet(new long[]{0x0000006000001000L});
    public static final BitSet FOLLOW_set_in_fact_binding3591 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_fact_in_fact_binding3603 = new BitSet(new long[]{0x0000006000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_binding3621 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualified_id_in_fact3676 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact3686 = new BitSet(new long[]{0x0807920C0A7E16F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_constraints_in_fact3698 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact3705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EOF_in_fact3714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints3732 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_COMMA_in_constraints3739 = new BitSet(new long[]{0x0807920C0A7E06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_constraint_in_constraints3748 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_or_constr_in_constraint3781 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3804 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_constr3812 = new BitSet(new long[]{0x0807920C0A7E06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3821 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3853 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_constr3861 = new BitSet(new long[]{0x0807920C0A7E06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3870 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_field_constraint_in_unary_constr3898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_unary_constr3906 = new BitSet(new long[]{0x0807920C0A7E06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_or_constr_in_unary_constr3908 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_unary_constr3911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_unary_constr3917 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_predicate_in_unary_constr3919 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_field_constraint3958 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_78_in_field_constraint3960 = new BitSet(new long[]{0x0807820C0A7E02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint3979 = new BitSet(new long[]{0x0FE0080000000402L,0x00000000003F8000L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint3993 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_field_constraint4008 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_predicate_in_field_constraint4010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint4036 = new BitSet(new long[]{0x0FE0080000000400L,0x00000000003F0000L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint4045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective4092 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_restr_connective4116 = new BitSet(new long[]{0x0FE0080000000400L,0x00000000003F0000L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective4127 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective4159 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_restr_connective4179 = new BitSet(new long[]{0x0FE0080000000400L,0x00000000003F0000L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective4190 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_compound_operator_in_constraint_expression4227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_operator_in_constraint_expression4234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_constraint_expression4242 = new BitSet(new long[]{0x0FE0080000000400L,0x00000000003F0000L});
    public static final BitSet FOLLOW_or_restr_connective_in_constraint_expression4251 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_constraint_expression4256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_80_in_simple_operator4287 = new BitSet(new long[]{0x3807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_81_in_simple_operator4295 = new BitSet(new long[]{0x3807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_82_in_simple_operator4303 = new BitSet(new long[]{0x3807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_83_in_simple_operator4311 = new BitSet(new long[]{0x3807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_84_in_simple_operator4319 = new BitSet(new long[]{0x3807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_85_in_simple_operator4327 = new BitSet(new long[]{0x3807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_CONTAINS_in_simple_operator4355 = new BitSet(new long[]{0x3807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_NOT_in_simple_operator4383 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_CONTAINS_in_simple_operator4387 = new BitSet(new long[]{0x3807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_EXCLUDES_in_simple_operator4415 = new BitSet(new long[]{0x3807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_MATCHES_in_simple_operator4443 = new BitSet(new long[]{0x3807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_SOUNDSLIKE_in_simple_operator4471 = new BitSet(new long[]{0x3807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_NOT_in_simple_operator4499 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_MATCHES_in_simple_operator4503 = new BitSet(new long[]{0x3807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_MEMBEROF_in_simple_operator4531 = new BitSet(new long[]{0x3807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_NOT_in_simple_operator4559 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_MEMBEROF_in_simple_operator4563 = new BitSet(new long[]{0x3807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_TILDE_in_simple_operator4569 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ID_in_simple_operator4573 = new BitSet(new long[]{0x7807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_square_chunk_in_simple_operator4577 = new BitSet(new long[]{0x3807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_NOT_in_simple_operator4586 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_TILDE_in_simple_operator4588 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ID_in_simple_operator4592 = new BitSet(new long[]{0x7807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_square_chunk_in_simple_operator4596 = new BitSet(new long[]{0x3807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_expression_value_in_simple_operator4611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_compound_operator4641 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_NOT_in_compound_operator4653 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_IN_in_compound_operator4655 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_compound_operator4670 = new BitSet(new long[]{0x3807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator4674 = new BitSet(new long[]{0x0000000000001800L});
    public static final BitSet FOLLOW_COMMA_in_compound_operator4681 = new BitSet(new long[]{0x3807820C1E7F06F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator4685 = new BitSet(new long[]{0x0000000000001800L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_compound_operator4694 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_expression_value4728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_expression_value4748 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_value4762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint4805 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint4816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint4829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint4840 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal_constraint4852 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate4890 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk4908 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000003FFFFFL});
    public static final BitSet FOLLOW_set_in_curly_chunk4912 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000003FFFFFL});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk4921 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000003FFFFFL});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk4926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk4940 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000003FFFFFL});
    public static final BitSet FOLLOW_set_in_paren_chunk4944 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000003FFFFFL});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk4953 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000003FFFFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk4958 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk4971 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000003FFFFFL});
    public static final BitSet FOLLOW_set_in_square_chunk4975 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000003FFFFFL});
    public static final BitSet FOLLOW_square_chunk_in_square_chunk4984 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000003FFFFFL});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk4989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_qualified_id5018 = new BitSet(new long[]{0x4000000000000102L});
    public static final BitSet FOLLOW_DOT_in_qualified_id5024 = new BitSet(new long[]{0x0807820C0A7E02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_identifier_in_qualified_id5026 = new BitSet(new long[]{0x4000000000000102L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_qualified_id5035 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_qualified_id5037 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_identifier_in_dotted_name5071 = new BitSet(new long[]{0x4000000000000102L});
    public static final BitSet FOLLOW_DOT_in_dotted_name5077 = new BitSet(new long[]{0x0807820C0A7E02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_identifier_in_dotted_name5081 = new BitSet(new long[]{0x4000000000000102L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dotted_name5090 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dotted_name5092 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path5126 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_DOT_in_accessor_path5132 = new BitSet(new long[]{0x0807820C0A7E02F0L,0x0000000000000003L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path5136 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_identifier_in_accessor_element5174 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_square_chunk_in_accessor_element5181 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk5202 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000003FFFFFL});
    public static final BitSet FOLLOW_set_in_rhs_chunk5210 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000003FFFFFL});
    public static final BitSet FOLLOW_END_in_rhs_chunk5234 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_opt_semicolon_in_rhs_chunk5236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_name5270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_name5278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_identifier0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_synpred12216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_synpred22234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_synpred32253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_synpred42272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred52291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_synpred62397 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_ENTRY_POINT_in_synpred62399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred93338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred103371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred114116 = new BitSet(new long[]{0x0FE0080000000400L,0x00000000003F0000L});
    public static final BitSet FOLLOW_and_restr_connective_in_synpred114127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred124179 = new BitSet(new long[]{0x0FE0080000000400L,0x00000000003F0000L});
    public static final BitSet FOLLOW_constraint_expression_in_synpred124190 = new BitSet(new long[]{0x0000000000000002L});

}