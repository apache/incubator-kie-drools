// $ANTLR 3.0.1 /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g 2008-04-28 17:25:36

	package org.drools.lang;
	import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.FailedPredicateException;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.MismatchedNotSetException;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.MismatchedTreeNodeException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.drools.lang.descr.AccessorDescr;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.CollectDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.DeclarativeInvokerDescr;
import org.drools.lang.descr.DescrFactory;
import org.drools.lang.descr.EntryPointDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FactTemplateDescr;
import org.drools.lang.descr.FieldAccessDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.FieldTemplateDescr;
import org.drools.lang.descr.ForallDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.FunctionCallDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.MethodAccessDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PatternSourceDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QualifiedIdentifierRestrictionDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RestrictionConnectiveDescr;
import org.drools.lang.descr.RestrictionDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.SlidingWindowDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.lang.descr.TypeFieldDescr;
import org.drools.lang.descr.VariableRestrictionDescr;
public class DRLParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PACKAGE", "IMPORT", "FUNCTION", "ID", "DOT", "GLOBAL", "LEFT_PAREN", "COMMA", "RIGHT_PAREN", "DECLARE", "END", "AT", "COLON", "EQUALS", "QUERY", "TEMPLATE", "RULE", "WHEN", "ATTRIBUTES", "DATE_EFFECTIVE", "STRING", "DATE_EXPIRES", "ENABLED", "BOOL", "SALIENCE", "INT", "NO_LOOP", "AUTO_FOCUS", "ACTIVATION_GROUP", "RULEFLOW_GROUP", "AGENDA_GROUP", "DURATION", "DIALECT", "LOCK_ON_ACTIVE", "OR", "DOUBLE_PIPE", "AND", "DOUBLE_AMPER", "WITH", "WINDOW", "FROM", "EXISTS", "NOT", "EVAL", "FORALL", "ACCUMULATE", "INIT", "ACTION", "REVERSE", "RESULT", "COLLECT", "ENTRY_POINT", "LEFT_SQUARE", "RIGHT_SQUARE", "CONTAINS", "EXCLUDES", "MATCHES", "SOUNDSLIKE", "MEMBEROF", "TILDE", "IN", "FLOAT", "NULL", "LEFT_CURLY", "RIGHT_CURLY", "THEN", "EVENT", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "MISC", "';'", "'.*'", "'->'", "'=='", "'>'", "'>='", "'<'", "'<='", "'!='"
    };
    public static final int EXISTS=45;
    public static final int COMMA=11;
    public static final int AUTO_FOCUS=31;
    public static final int END=14;
    public static final int HexDigit=74;
    public static final int FORALL=48;
    public static final int TEMPLATE=19;
    public static final int MISC=80;
    public static final int FLOAT=65;
    public static final int QUERY=18;
    public static final int THEN=69;
    public static final int RULE=20;
    public static final int INIT=50;
    public static final int TILDE=63;
    public static final int IMPORT=5;
    public static final int PACKAGE=4;
    public static final int DATE_EFFECTIVE=23;
    public static final int OR=38;
    public static final int DOT=8;
    public static final int DOUBLE_PIPE=39;
    public static final int AND=40;
    public static final int FUNCTION=6;
    public static final int GLOBAL=9;
    public static final int EscapeSequence=73;
    public static final int DIALECT=36;
    public static final int INT=29;
    public static final int LOCK_ON_ACTIVE=37;
    public static final int DATE_EXPIRES=25;
    public static final int LEFT_SQUARE=56;
    public static final int CONTAINS=58;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=77;
    public static final int ATTRIBUTES=22;
    public static final int EVENT=70;
    public static final int DECLARE=13;
    public static final int LEFT_CURLY=67;
    public static final int RESULT=53;
    public static final int AT=15;
    public static final int FROM=44;
    public static final int ID=7;
    public static final int LEFT_PAREN=10;
    public static final int ACTIVATION_GROUP=32;
    public static final int DOUBLE_AMPER=41;
    public static final int RIGHT_CURLY=68;
    public static final int SOUNDSLIKE=61;
    public static final int EXCLUDES=59;
    public static final int BOOL=27;
    public static final int MEMBEROF=62;
    public static final int WHEN=21;
    public static final int RULEFLOW_GROUP=33;
    public static final int WS=72;
    public static final int STRING=24;
    public static final int ACTION=51;
    public static final int WINDOW=43;
    public static final int COLLECT=54;
    public static final int WITH=42;
    public static final int IN=64;
    public static final int REVERSE=52;
    public static final int ACCUMULATE=49;
    public static final int EQUALS=17;
    public static final int NO_LOOP=30;
    public static final int UnicodeEscape=75;
    public static final int DURATION=35;
    public static final int EVAL=47;
    public static final int MATCHES=60;
    public static final int EOF=-1;
    public static final int EOL=71;
    public static final int NULL=66;
    public static final int AGENDA_GROUP=34;
    public static final int COLON=16;
    public static final int OctalEscape=76;
    public static final int SALIENCE=28;
    public static final int MULTI_LINE_COMMENT=79;
    public static final int NOT=46;
    public static final int RIGHT_PAREN=12;
    public static final int ENABLED=26;
    public static final int RIGHT_SQUARE=57;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=78;
    public static final int ENTRY_POINT=55;

        public DRLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[90+1];
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

            if ( (LA1_0==81) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:196:4: ';'
                    {
                    match(input,81,FOLLOW_81_in_opt_semicolon39); if (failed) return ;

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

                if ( ((LA2_0>=IMPORT && LA2_0<=FUNCTION)||LA2_0==GLOBAL||LA2_0==DECLARE||(LA2_0>=QUERY && LA2_0<=RULE)||LA2_0==DATE_EFFECTIVE||(LA2_0>=DATE_EXPIRES && LA2_0<=ENABLED)||LA2_0==SALIENCE||(LA2_0>=NO_LOOP && LA2_0<=LOCK_ON_ACTIVE)) ) {
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

            if ( (LA6_0==82) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:296:5: star= '.*'
                    {
                    star=(Token)input.LT(1);
                    match(input,82,FOLLOW_82_in_import_name402); if (failed) return name;
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

            if ( ((LA7_0>=PACKAGE && LA7_0<=ID)||LA7_0==GLOBAL||LA7_0==END||(LA7_0>=QUERY && LA7_0<=ATTRIBUTES)||LA7_0==ENABLED||LA7_0==SALIENCE||(LA7_0>=DURATION && LA7_0<=DIALECT)||LA7_0==FROM||(LA7_0>=INIT && LA7_0<=RESULT)||LA7_0==IN||(LA7_0>=THEN && LA7_0<=EVENT)) ) {
                int LA7_1 = input.LA(2);

                if ( ((LA7_1>=PACKAGE && LA7_1<=GLOBAL)||LA7_1==END||(LA7_1>=QUERY && LA7_1<=ATTRIBUTES)||LA7_1==ENABLED||LA7_1==SALIENCE||(LA7_1>=DURATION && LA7_1<=DIALECT)||LA7_1==FROM||(LA7_1>=INIT && LA7_1<=RESULT)||LA7_1==LEFT_SQUARE||LA7_1==IN||(LA7_1>=THEN && LA7_1<=EVENT)) ) {
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

            if ( ((LA11_0>=PACKAGE && LA11_0<=ID)||LA11_0==GLOBAL||LA11_0==END||(LA11_0>=QUERY && LA11_0<=ATTRIBUTES)||LA11_0==ENABLED||LA11_0==SALIENCE||(LA11_0>=DURATION && LA11_0<=DIALECT)||LA11_0==FROM||(LA11_0>=INIT && LA11_0<=RESULT)||LA11_0==IN||(LA11_0>=THEN && LA11_0<=EVENT)) ) {
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:374:1: type_declaration returns [TypeDeclarationDescr declaration] : DECLARE id= identifier ( decl_metadata[$declaration] )* ( decl_field[$declaration] )* END ;
    public final TypeDeclarationDescr type_declaration() throws RecognitionException {
        TypeDeclarationDescr declaration = null;

        identifier_return id = null;



                        declaration = factory.createTypeDeclaration();
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:378:9: ( DECLARE id= identifier ( decl_metadata[$declaration] )* ( decl_field[$declaration] )* END )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:378:11: DECLARE id= identifier ( decl_metadata[$declaration] )* ( decl_field[$declaration] )* END
            {
            match(input,DECLARE,FOLLOW_DECLARE_in_type_declaration645); if (failed) return declaration;
            pushFollow(FOLLOW_identifier_in_type_declaration649);
            id=identifier();
            _fsp--;
            if (failed) return declaration;
            if ( backtracking==0 ) {

                                          declaration.setTypeName( input.toString(id.start,id.stop) );
                                      
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:382:25: ( decl_metadata[$declaration] )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==AT) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:382:25: decl_metadata[$declaration]
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_type_declaration702);
            	    decl_metadata(declaration);
            	    _fsp--;
            	    if (failed) return declaration;

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:383:25: ( decl_field[$declaration] )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==END) ) {
                    int LA14_1 = input.LA(2);

                    if ( ((LA14_1>=COLON && LA14_1<=EQUALS)) ) {
                        alt14=1;
                    }


                }
                else if ( ((LA14_0>=PACKAGE && LA14_0<=ID)||LA14_0==GLOBAL||(LA14_0>=QUERY && LA14_0<=ATTRIBUTES)||LA14_0==ENABLED||LA14_0==SALIENCE||(LA14_0>=DURATION && LA14_0<=DIALECT)||LA14_0==FROM||(LA14_0>=INIT && LA14_0<=RESULT)||LA14_0==IN||(LA14_0>=THEN && LA14_0<=EVENT)) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:383:25: decl_field[$declaration]
            	    {
            	    pushFollow(FOLLOW_decl_field_in_type_declaration730);
            	    decl_field(declaration);
            	    _fsp--;
            	    if (failed) return declaration;

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);

            match(input,END,FOLLOW_END_in_type_declaration750); if (failed) return declaration;

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


    // $ANTLR start decl_metadata
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:387:1: decl_metadata[BaseDescr declaration] : AT att= identifier val= paren_chunk ;
    public final void decl_metadata(BaseDescr declaration) throws RecognitionException {
        identifier_return att = null;

        paren_chunk_return val = null;


        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:388:9: ( AT att= identifier val= paren_chunk )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:388:11: AT att= identifier val= paren_chunk
            {
            match(input,AT,FOLLOW_AT_in_decl_metadata784); if (failed) return ;
            pushFollow(FOLLOW_identifier_in_decl_metadata788);
            att=identifier();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_paren_chunk_in_decl_metadata792);
            val=paren_chunk();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

                                  if( declaration instanceof TypeDeclarationDescr )
                                      ((TypeDeclarationDescr)declaration).addMetaAttribute( input.toString(att.start,att.stop), getString( input.toString(val.start,val.stop) ).trim() );
                                  else
                                      ((TypeFieldDescr)declaration).addMetaAttribute( input.toString(att.start,att.stop), getString( input.toString(val.start,val.stop) ).trim() );
                              
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
    // $ANTLR end decl_metadata


    // $ANTLR start decl_field
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:397:1: decl_field[TypeDeclarationDescr declaration] : id= identifier (init= initialization )? COLON type= qualified_id ( decl_metadata[field] )* ;
    public final void decl_field(TypeDeclarationDescr declaration) throws RecognitionException {
        identifier_return id = null;

        String init = null;

        qualified_id_return type = null;



                TypeFieldDescr field = null;

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:401:2: (id= identifier (init= initialization )? COLON type= qualified_id ( decl_metadata[field] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:401:9: id= identifier (init= initialization )? COLON type= qualified_id ( decl_metadata[field] )*
            {
            pushFollow(FOLLOW_identifier_in_decl_field853);
            id=identifier();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:401:27: (init= initialization )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==EQUALS) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:401:27: init= initialization
                    {
                    pushFollow(FOLLOW_initialization_in_decl_field857);
                    init=initialization();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            match(input,COLON,FOLLOW_COLON_in_decl_field860); if (failed) return ;
            pushFollow(FOLLOW_qualified_id_in_decl_field864);
            type=qualified_id();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              		    field = new TypeFieldDescr( input.toString(id.start,id.stop) );
              		    if( init != null )
              		        field.setInitExpr( init );
              		    field.setPattern( new PatternDescr(type.text) );
              		    declaration.addField( field );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:409:3: ( decl_metadata[field] )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==AT) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:409:3: decl_metadata[field]
            	    {
            	    pushFollow(FOLLOW_decl_metadata_in_decl_field873);
            	    decl_metadata(field);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop16;
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
    // $ANTLR end decl_field


    // $ANTLR start initialization
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:412:1: initialization returns [String expr] : EQUALS val= paren_chunk ;
    public final String initialization() throws RecognitionException {
        String expr = null;

        paren_chunk_return val = null;


        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:413:2: ( EQUALS val= paren_chunk )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:413:9: EQUALS val= paren_chunk
            {
            match(input,EQUALS,FOLLOW_EQUALS_in_initialization900); if (failed) return expr;
            pushFollow(FOLLOW_paren_chunk_in_initialization904);
            val=paren_chunk();
            _fsp--;
            if (failed) return expr;
            if ( backtracking==0 ) {

              	           expr = getString( input.toString(val.start,val.stop) ).trim();
              	       
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return expr;
    }
    // $ANTLR end initialization


    // $ANTLR start query
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:419:1: query returns [QueryDescr query] : QUERY queryName= name ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )? normal_lhs_block[lhs] END opt_semicolon ;
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:427:2: ( QUERY queryName= name ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )? normal_lhs_block[lhs] END opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:428:3: QUERY queryName= name ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )? normal_lhs_block[lhs] END opt_semicolon
            {
            QUERY7=(Token)input.LT(1);
            match(input,QUERY,FOLLOW_QUERY_in_query938); if (failed) return query;
            pushFollow(FOLLOW_name_in_query942);
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:437:3: ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )?
            int alt21=2;
            alt21 = dfa21.predict(input);
            switch (alt21) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:437:5: LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_query952); if (failed) return query;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:438:11: ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )?
                    int alt20=2;
                    int LA20_0 = input.LA(1);

                    if ( (LA20_0==ID) ) {
                        alt20=1;
                    }
                    switch (alt20) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:438:13: ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )*
                            {
                            if ( backtracking==0 ) {
                               params = new ArrayList(); types = new ArrayList();
                            }
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:440:15: ( (paramType= qualified_id )? paramName= ID )
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:440:16: (paramType= qualified_id )? paramName= ID
                            {
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:440:25: (paramType= qualified_id )?
                            int alt17=2;
                            int LA17_0 = input.LA(1);

                            if ( (LA17_0==ID) ) {
                                int LA17_1 = input.LA(2);

                                if ( ((LA17_1>=ID && LA17_1<=DOT)||LA17_1==LEFT_SQUARE) ) {
                                    alt17=1;
                                }
                            }
                            switch (alt17) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:440:25: paramType= qualified_id
                                    {
                                    pushFollow(FOLLOW_qualified_id_in_query987);
                                    paramType=qualified_id();
                                    _fsp--;
                                    if (failed) return query;

                                    }
                                    break;

                            }

                            paramName=(Token)input.LT(1);
                            match(input,ID,FOLLOW_ID_in_query992); if (failed) return query;
                            if ( backtracking==0 ) {
                               params.add( paramName.getText() ); String type = (paramType != null) ? paramType.text : "Object"; types.add( type ); 
                            }

                            }

                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:441:15: ( COMMA (paramType= qualified_id )? paramName= ID )*
                            loop19:
                            do {
                                int alt19=2;
                                int LA19_0 = input.LA(1);

                                if ( (LA19_0==COMMA) ) {
                                    alt19=1;
                                }


                                switch (alt19) {
                            	case 1 :
                            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:441:16: COMMA (paramType= qualified_id )? paramName= ID
                            	    {
                            	    match(input,COMMA,FOLLOW_COMMA_in_query1013); if (failed) return query;
                            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:441:31: (paramType= qualified_id )?
                            	    int alt18=2;
                            	    int LA18_0 = input.LA(1);

                            	    if ( (LA18_0==ID) ) {
                            	        int LA18_1 = input.LA(2);

                            	        if ( ((LA18_1>=ID && LA18_1<=DOT)||LA18_1==LEFT_SQUARE) ) {
                            	            alt18=1;
                            	        }
                            	    }
                            	    switch (alt18) {
                            	        case 1 :
                            	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:441:31: paramType= qualified_id
                            	            {
                            	            pushFollow(FOLLOW_qualified_id_in_query1017);
                            	            paramType=qualified_id();
                            	            _fsp--;
                            	            if (failed) return query;

                            	            }
                            	            break;

                            	    }

                            	    paramName=(Token)input.LT(1);
                            	    match(input,ID,FOLLOW_ID_in_query1022); if (failed) return query;
                            	    if ( backtracking==0 ) {
                            	       params.add( paramName.getText() );  String type = (paramType != null) ? paramType.text : "Object"; types.add( type );  
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    break loop19;
                                }
                            } while (true);

                            if ( backtracking==0 ) {
                              	query.setParameters( (String[]) params.toArray( new String[params.size()] ) ); 
                              		            	query.setParameterTypes( (String[]) types.toArray( new String[types.size()] ) ); 
                              		            
                            }

                            }
                            break;

                    }

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_query1072); if (failed) return query;

                    }
                    break;

            }

            if ( backtracking==0 ) {

                                      location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
              	        
            }
            pushFollow(FOLLOW_normal_lhs_block_in_query1101);
            normal_lhs_block(lhs);
            _fsp--;
            if (failed) return query;
            END8=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_query1106); if (failed) return query;
            pushFollow(FOLLOW_opt_semicolon_in_query1108);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:459:1: template returns [FactTemplateDescr template] : TEMPLATE templateName= name opt_semicolon (slot= template_slot )+ END opt_semicolon ;
    public final FactTemplateDescr template() throws RecognitionException {
        FactTemplateDescr template = null;

        Token TEMPLATE9=null;
        Token END10=null;
        name_return templateName = null;

        FieldTemplateDescr slot = null;



        		template = null;		
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:463:2: ( TEMPLATE templateName= name opt_semicolon (slot= template_slot )+ END opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:464:3: TEMPLATE templateName= name opt_semicolon (slot= template_slot )+ END opt_semicolon
            {
            TEMPLATE9=(Token)input.LT(1);
            match(input,TEMPLATE,FOLLOW_TEMPLATE_in_template1136); if (failed) return template;
            pushFollow(FOLLOW_name_in_template1140);
            templateName=name();
            _fsp--;
            if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template1142);
            opt_semicolon();
            _fsp--;
            if (failed) return template;
            if ( backtracking==0 ) {

              			template = new FactTemplateDescr(templateName.name);
              			template.setLocation( offset(TEMPLATE9.getLine()), TEMPLATE9.getCharPositionInLine() );			
              			template.setStartCharacter( ((CommonToken)TEMPLATE9).getStartIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:470:3: (slot= template_slot )+
            int cnt22=0;
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==ID) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:471:4: slot= template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template1157);
            	    slot=template_slot();
            	    _fsp--;
            	    if (failed) return template;
            	    if ( backtracking==0 ) {

            	      				template.addFieldTemplate( slot );
            	      			
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt22 >= 1 ) break loop22;
            	    if (backtracking>0) {failed=true; return template;}
                        EarlyExitException eee =
                            new EarlyExitException(22, input);
                        throw eee;
                }
                cnt22++;
            } while (true);

            END10=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_template1172); if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template1174);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:482:1: template_slot returns [FieldTemplateDescr field] : fieldType= qualified_id id= identifier opt_semicolon ;
    public final FieldTemplateDescr template_slot() throws RecognitionException {
        FieldTemplateDescr field = null;

        qualified_id_return fieldType = null;

        identifier_return id = null;



        		field = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:486:2: (fieldType= qualified_id id= identifier opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:487:11: fieldType= qualified_id id= identifier opt_semicolon
            {
            if ( backtracking==0 ) {

              			field = factory.createFieldTemplate();
              	         
            }
            pushFollow(FOLLOW_qualified_id_in_template_slot1220);
            fieldType=qualified_id();
            _fsp--;
            if (failed) return field;
            if ( backtracking==0 ) {

              		        field.setClassType( fieldType.text );
              			field.setStartCharacter( ((CommonToken)((Token)fieldType.start)).getStartIndex() );
              			field.setEndCharacter( ((CommonToken)((Token)fieldType.stop)).getStopIndex() );
              		 
            }
            pushFollow(FOLLOW_identifier_in_template_slot1236);
            id=identifier();
            _fsp--;
            if (failed) return field;
            pushFollow(FOLLOW_opt_semicolon_in_template_slot1238);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:505:1: rule returns [RuleDescr rule] : RULE ruleName= name ( rule_attributes[$rule] )? ( WHEN ( COLON )? normal_lhs_block[lhs] )? rhs_chunk[$rule] ;
    public final RuleDescr rule() throws RecognitionException {
        RuleDescr rule = null;

        Token RULE11=null;
        Token WHEN12=null;
        name_return ruleName = null;



        		rule = null;
        		AndDescr lhs = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:510:2: ( RULE ruleName= name ( rule_attributes[$rule] )? ( WHEN ( COLON )? normal_lhs_block[lhs] )? rhs_chunk[$rule] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:511:3: RULE ruleName= name ( rule_attributes[$rule] )? ( WHEN ( COLON )? normal_lhs_block[lhs] )? rhs_chunk[$rule]
            {
            RULE11=(Token)input.LT(1);
            match(input,RULE,FOLLOW_RULE_in_rule1269); if (failed) return rule;
            pushFollow(FOLLOW_name_in_rule1273);
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:3: ( rule_attributes[$rule] )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( ((LA23_0>=ATTRIBUTES && LA23_0<=DATE_EFFECTIVE)||(LA23_0>=DATE_EXPIRES && LA23_0<=ENABLED)||LA23_0==SALIENCE||(LA23_0>=NO_LOOP && LA23_0<=LOCK_ON_ACTIVE)) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:3: rule_attributes[$rule]
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule1282);
                    rule_attributes(rule);
                    _fsp--;
                    if (failed) return rule;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:522:3: ( WHEN ( COLON )? normal_lhs_block[lhs] )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==WHEN) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:523:4: WHEN ( COLON )? normal_lhs_block[lhs]
                    {
                    WHEN12=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_rule1294); if (failed) return rule;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:523:9: ( COLON )?
                    int alt24=2;
                    int LA24_0 = input.LA(1);

                    if ( (LA24_0==COLON) ) {
                        alt24=1;
                    }
                    switch (alt24) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:523:9: COLON
                            {
                            match(input,COLON,FOLLOW_COLON_in_rule1296); if (failed) return rule;

                            }
                            break;

                    }

                    if ( backtracking==0 ) {
                       
                      				this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
                      				lhs.setLocation( offset(WHEN12.getLine()), WHEN12.getCharPositionInLine() );
                      				lhs.setStartCharacter( ((CommonToken)WHEN12).getStartIndex() );
                      			
                    }
                    pushFollow(FOLLOW_normal_lhs_block_in_rule1307);
                    normal_lhs_block(lhs);
                    _fsp--;
                    if (failed) return rule;

                    }
                    break;

            }

            pushFollow(FOLLOW_rhs_chunk_in_rule1317);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:536:1: rule_attributes[RuleDescr rule] : ( ATTRIBUTES COLON )? attr= rule_attribute ( ( ',' )? attr= rule_attribute )* ;
    public final void rule_attributes(RuleDescr rule) throws RecognitionException {
        AttributeDescr attr = null;


        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:537:2: ( ( ATTRIBUTES COLON )? attr= rule_attribute ( ( ',' )? attr= rule_attribute )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:538:2: ( ATTRIBUTES COLON )? attr= rule_attribute ( ( ',' )? attr= rule_attribute )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:538:2: ( ATTRIBUTES COLON )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==ATTRIBUTES) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:538:4: ATTRIBUTES COLON
                    {
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_rule_attributes1337); if (failed) return ;
                    match(input,COLON,FOLLOW_COLON_in_rule_attributes1339); if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_rule_attribute_in_rule_attributes1347);
            attr=rule_attribute();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               rule.addAttribute( attr ); 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:540:2: ( ( ',' )? attr= rule_attribute )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0==COMMA||LA28_0==DATE_EFFECTIVE||(LA28_0>=DATE_EXPIRES && LA28_0<=ENABLED)||LA28_0==SALIENCE||(LA28_0>=NO_LOOP && LA28_0<=LOCK_ON_ACTIVE)) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:540:4: ( ',' )? attr= rule_attribute
            	    {
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:540:4: ( ',' )?
            	    int alt27=2;
            	    int LA27_0 = input.LA(1);

            	    if ( (LA27_0==COMMA) ) {
            	        alt27=1;
            	    }
            	    switch (alt27) {
            	        case 1 :
            	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:540:4: ','
            	            {
            	            match(input,COMMA,FOLLOW_COMMA_in_rule_attributes1354); if (failed) return ;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes1359);
            	    attr=rule_attribute();
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       rule.addAttribute( attr ); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop28;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:545:1: rule_attribute returns [AttributeDescr attr] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect );
    public final AttributeDescr rule_attribute() throws RecognitionException {
        AttributeDescr attr = null;

        AttributeDescr a = null;



        		attr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:552:2: (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect )
            int alt29=12;
            switch ( input.LA(1) ) {
            case SALIENCE:
                {
                alt29=1;
                }
                break;
            case NO_LOOP:
                {
                alt29=2;
                }
                break;
            case AGENDA_GROUP:
                {
                alt29=3;
                }
                break;
            case DURATION:
                {
                alt29=4;
                }
                break;
            case ACTIVATION_GROUP:
                {
                alt29=5;
                }
                break;
            case AUTO_FOCUS:
                {
                alt29=6;
                }
                break;
            case DATE_EFFECTIVE:
                {
                alt29=7;
                }
                break;
            case DATE_EXPIRES:
                {
                alt29=8;
                }
                break;
            case ENABLED:
                {
                alt29=9;
                }
                break;
            case RULEFLOW_GROUP:
                {
                alt29=10;
                }
                break;
            case LOCK_ON_ACTIVE:
                {
                alt29=11;
                }
                break;
            case DIALECT:
                {
                alt29=12;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return attr;}
                NoViableAltException nvae =
                    new NoViableAltException("545:1: rule_attribute returns [AttributeDescr attr] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect );", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:552:4: a= salience
                    {
                    pushFollow(FOLLOW_salience_in_rule_attribute1396);
                    a=salience();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:553:4: a= no_loop
                    {
                    pushFollow(FOLLOW_no_loop_in_rule_attribute1404);
                    a=no_loop();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:554:4: a= agenda_group
                    {
                    pushFollow(FOLLOW_agenda_group_in_rule_attribute1413);
                    a=agenda_group();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:555:4: a= duration
                    {
                    pushFollow(FOLLOW_duration_in_rule_attribute1422);
                    a=duration();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:556:4: a= activation_group
                    {
                    pushFollow(FOLLOW_activation_group_in_rule_attribute1431);
                    a=activation_group();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:557:4: a= auto_focus
                    {
                    pushFollow(FOLLOW_auto_focus_in_rule_attribute1439);
                    a=auto_focus();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:558:4: a= date_effective
                    {
                    pushFollow(FOLLOW_date_effective_in_rule_attribute1447);
                    a=date_effective();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:559:4: a= date_expires
                    {
                    pushFollow(FOLLOW_date_expires_in_rule_attribute1455);
                    a=date_expires();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:560:4: a= enabled
                    {
                    pushFollow(FOLLOW_enabled_in_rule_attribute1463);
                    a=enabled();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:561:4: a= ruleflow_group
                    {
                    pushFollow(FOLLOW_ruleflow_group_in_rule_attribute1471);
                    a=ruleflow_group();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:562:4: a= lock_on_active
                    {
                    pushFollow(FOLLOW_lock_on_active_in_rule_attribute1479);
                    a=lock_on_active();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:563:4: a= dialect
                    {
                    pushFollow(FOLLOW_dialect_in_rule_attribute1486);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:566:1: date_effective returns [AttributeDescr descr] : DATE_EFFECTIVE STRING ;
    public final AttributeDescr date_effective() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING13=null;
        Token DATE_EFFECTIVE14=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:570:2: ( DATE_EFFECTIVE STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:571:3: DATE_EFFECTIVE STRING
            {
            DATE_EFFECTIVE14=(Token)input.LT(1);
            match(input,DATE_EFFECTIVE,FOLLOW_DATE_EFFECTIVE_in_date_effective1512); if (failed) return descr;
            STRING13=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_effective1514); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:581:1: date_expires returns [AttributeDescr descr] : DATE_EXPIRES STRING ;
    public final AttributeDescr date_expires() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING15=null;
        Token DATE_EXPIRES16=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:585:2: ( DATE_EXPIRES STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:585:4: DATE_EXPIRES STRING
            {
            DATE_EXPIRES16=(Token)input.LT(1);
            match(input,DATE_EXPIRES,FOLLOW_DATE_EXPIRES_in_date_expires1543); if (failed) return descr;
            STRING15=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_expires1545); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:594:1: enabled returns [AttributeDescr descr] : ENABLED BOOL ;
    public final AttributeDescr enabled() throws RecognitionException {
        AttributeDescr descr = null;

        Token BOOL17=null;
        Token ENABLED18=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:598:2: ( ENABLED BOOL )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:598:5: ENABLED BOOL
            {
            ENABLED18=(Token)input.LT(1);
            match(input,ENABLED,FOLLOW_ENABLED_in_enabled1574); if (failed) return descr;
            BOOL17=(Token)input.LT(1);
            match(input,BOOL,FOLLOW_BOOL_in_enabled1576); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:607:1: salience returns [AttributeDescr descr] : SALIENCE ( INT | txt= paren_chunk ) ;
    public final AttributeDescr salience() throws RecognitionException {
        AttributeDescr descr = null;

        Token SALIENCE19=null;
        Token INT20=null;
        paren_chunk_return txt = null;



        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:611:2: ( SALIENCE ( INT | txt= paren_chunk ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:612:3: SALIENCE ( INT | txt= paren_chunk )
            {
            SALIENCE19=(Token)input.LT(1);
            match(input,SALIENCE,FOLLOW_SALIENCE_in_salience1609); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "salience" );
              			descr.setLocation( offset(SALIENCE19.getLine()), SALIENCE19.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)SALIENCE19).getStartIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:618:3: ( INT | txt= paren_chunk )
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==INT) ) {
                alt30=1;
            }
            else if ( (LA30_0==LEFT_PAREN) ) {
                alt30=2;
            }
            else {
                if (backtracking>0) {failed=true; return descr;}
                NoViableAltException nvae =
                    new NoViableAltException("618:3: ( INT | txt= paren_chunk )", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:618:5: INT
                    {
                    INT20=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_salience1620); if (failed) return descr;
                    if ( backtracking==0 ) {

                      			descr.setValue( INT20.getText() );
                      			descr.setEndCharacter( ((CommonToken)INT20).getStopIndex() );
                      		
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:623:5: txt= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_salience1635);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:631:1: no_loop returns [AttributeDescr descr] : NO_LOOP ( BOOL )? ;
    public final AttributeDescr no_loop() throws RecognitionException {
        AttributeDescr descr = null;

        Token NO_LOOP21=null;
        Token BOOL22=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:635:2: ( NO_LOOP ( BOOL )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:635:4: NO_LOOP ( BOOL )?
            {
            NO_LOOP21=(Token)input.LT(1);
            match(input,NO_LOOP,FOLLOW_NO_LOOP_in_no_loop1665); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "no-loop", "true" );
              			descr.setLocation( offset(NO_LOOP21.getLine()), NO_LOOP21.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)NO_LOOP21).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)NO_LOOP21).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:642:3: ( BOOL )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==BOOL) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:642:5: BOOL
                    {
                    BOOL22=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1678); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:650:1: auto_focus returns [AttributeDescr descr] : AUTO_FOCUS ( BOOL )? ;
    public final AttributeDescr auto_focus() throws RecognitionException {
        AttributeDescr descr = null;

        Token AUTO_FOCUS23=null;
        Token BOOL24=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:654:2: ( AUTO_FOCUS ( BOOL )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:654:4: AUTO_FOCUS ( BOOL )?
            {
            AUTO_FOCUS23=(Token)input.LT(1);
            match(input,AUTO_FOCUS,FOLLOW_AUTO_FOCUS_in_auto_focus1713); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "auto-focus", "true" );
              			descr.setLocation( offset(AUTO_FOCUS23.getLine()), AUTO_FOCUS23.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)AUTO_FOCUS23).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)AUTO_FOCUS23).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:661:3: ( BOOL )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==BOOL) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:661:5: BOOL
                    {
                    BOOL24=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1726); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:669:1: activation_group returns [AttributeDescr descr] : ACTIVATION_GROUP STRING ;
    public final AttributeDescr activation_group() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING25=null;
        Token ACTIVATION_GROUP26=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:673:2: ( ACTIVATION_GROUP STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:673:4: ACTIVATION_GROUP STRING
            {
            ACTIVATION_GROUP26=(Token)input.LT(1);
            match(input,ACTIVATION_GROUP,FOLLOW_ACTIVATION_GROUP_in_activation_group1762); if (failed) return descr;
            STRING25=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1764); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:682:1: ruleflow_group returns [AttributeDescr descr] : RULEFLOW_GROUP STRING ;
    public final AttributeDescr ruleflow_group() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING27=null;
        Token RULEFLOW_GROUP28=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:686:2: ( RULEFLOW_GROUP STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:686:4: RULEFLOW_GROUP STRING
            {
            RULEFLOW_GROUP28=(Token)input.LT(1);
            match(input,RULEFLOW_GROUP,FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1792); if (failed) return descr;
            STRING27=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_ruleflow_group1794); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:695:1: agenda_group returns [AttributeDescr descr] : AGENDA_GROUP STRING ;
    public final AttributeDescr agenda_group() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING29=null;
        Token AGENDA_GROUP30=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:699:2: ( AGENDA_GROUP STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:699:4: AGENDA_GROUP STRING
            {
            AGENDA_GROUP30=(Token)input.LT(1);
            match(input,AGENDA_GROUP,FOLLOW_AGENDA_GROUP_in_agenda_group1822); if (failed) return descr;
            STRING29=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1824); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:708:1: duration returns [AttributeDescr descr] : DURATION INT ;
    public final AttributeDescr duration() throws RecognitionException {
        AttributeDescr descr = null;

        Token INT31=null;
        Token DURATION32=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:712:2: ( DURATION INT )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:712:4: DURATION INT
            {
            DURATION32=(Token)input.LT(1);
            match(input,DURATION,FOLLOW_DURATION_in_duration1852); if (failed) return descr;
            INT31=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1854); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:721:1: dialect returns [AttributeDescr descr] : DIALECT STRING ;
    public final AttributeDescr dialect() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING33=null;
        Token DIALECT34=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:725:2: ( DIALECT STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:725:4: DIALECT STRING
            {
            DIALECT34=(Token)input.LT(1);
            match(input,DIALECT,FOLLOW_DIALECT_in_dialect1882); if (failed) return descr;
            STRING33=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_dialect1884); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:734:1: lock_on_active returns [AttributeDescr descr] : LOCK_ON_ACTIVE ( BOOL )? ;
    public final AttributeDescr lock_on_active() throws RecognitionException {
        AttributeDescr descr = null;

        Token LOCK_ON_ACTIVE35=null;
        Token BOOL36=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:738:2: ( LOCK_ON_ACTIVE ( BOOL )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:738:4: LOCK_ON_ACTIVE ( BOOL )?
            {
            LOCK_ON_ACTIVE35=(Token)input.LT(1);
            match(input,LOCK_ON_ACTIVE,FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1916); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "lock-on-active", "true" );
              			descr.setLocation( offset(LOCK_ON_ACTIVE35.getLine()), LOCK_ON_ACTIVE35.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)LOCK_ON_ACTIVE35).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)LOCK_ON_ACTIVE35).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:3: ( BOOL )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==BOOL) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:5: BOOL
                    {
                    BOOL36=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_lock_on_active1929); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:753:1: normal_lhs_block[AndDescr descr] : (d= lhs[$descr] )* ;
    public final void normal_lhs_block(AndDescr descr) throws RecognitionException {
        BaseDescr d = null;



        		location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:757:2: ( (d= lhs[$descr] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:758:3: (d= lhs[$descr] )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:758:3: (d= lhs[$descr] )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==ID||LA34_0==LEFT_PAREN||(LA34_0>=EXISTS && LA34_0<=FORALL)) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:758:5: d= lhs[$descr]
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1968);
            	    d=lhs(descr);
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       if( d != null) descr.addDescr( d ); 
            	    }

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
        return ;
    }
    // $ANTLR end normal_lhs_block


    // $ANTLR start lhs
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:764:1: lhs[ConditionalElementDescr ce] returns [BaseDescr d] : l= lhs_or ;
    public final BaseDescr lhs(ConditionalElementDescr ce) throws RecognitionException {
        BaseDescr d = null;

        BaseDescr l = null;



        		d =null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:768:2: (l= lhs_or )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:768:4: l= lhs_or
            {
            pushFollow(FOLLOW_lhs_or_in_lhs2005);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:772:1: lhs_or returns [BaseDescr d] : ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* );
    public final BaseDescr lhs_or() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr lhsand = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d = null;
        		OrDescr or = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:777:2: ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* )
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==LEFT_PAREN) ) {
                int LA37_1 = input.LA(2);

                if ( (LA37_1==ID||LA37_1==LEFT_PAREN||LA37_1==AND||(LA37_1>=EXISTS && LA37_1<=FORALL)) ) {
                    alt37=2;
                }
                else if ( (LA37_1==OR) ) {
                    alt37=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("772:1: lhs_or returns [BaseDescr d] : ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* );", 37, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA37_0==ID||(LA37_0>=EXISTS && LA37_0<=FORALL)) ) {
                alt37=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("772:1: lhs_or returns [BaseDescr d] : ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* );", 37, 0, input);

                throw nvae;
            }
            switch (alt37) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:777:4: LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_or2030); if (failed) return d;
                    match(input,OR,FOLLOW_OR_in_lhs_or2032); if (failed) return d;
                    if ( backtracking==0 ) {

                      			or = new OrDescr();
                      			d = or;
                      			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                      		
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:783:3: (lhsand= lhs_and )+
                    int cnt35=0;
                    loop35:
                    do {
                        int alt35=2;
                        int LA35_0 = input.LA(1);

                        if ( (LA35_0==ID||LA35_0==LEFT_PAREN||(LA35_0>=EXISTS && LA35_0<=FORALL)) ) {
                            alt35=1;
                        }


                        switch (alt35) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:783:5: lhsand= lhs_and
                    	    {
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or2045);
                    	    lhsand=lhs_and();
                    	    _fsp--;
                    	    if (failed) return d;
                    	    if ( backtracking==0 ) {

                    	      			or.addDescr( lhsand );
                    	      		
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt35 >= 1 ) break loop35;
                    	    if (backtracking>0) {failed=true; return d;}
                                EarlyExitException eee =
                                    new EarlyExitException(35, input);
                                throw eee;
                        }
                        cnt35++;
                    } while (true);

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_or2056); if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:789:10: left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )*
                    {
                    pushFollow(FOLLOW_lhs_and_in_lhs_or2074);
                    left=lhs_and();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = left; 
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:790:3: ( ( OR | DOUBLE_PIPE ) right= lhs_and )*
                    loop36:
                    do {
                        int alt36=2;
                        int LA36_0 = input.LA(1);

                        if ( ((LA36_0>=OR && LA36_0<=DOUBLE_PIPE)) ) {
                            alt36=1;
                        }


                        switch (alt36) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:790:5: ( OR | DOUBLE_PIPE ) right= lhs_and
                    	    {
                    	    if ( (input.LA(1)>=OR && input.LA(1)<=DOUBLE_PIPE) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return d;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or2082);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {

                    	      				location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                    	      			
                    	    }
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or2098);
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
                    	    break loop36;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:807:1: lhs_and returns [BaseDescr d] : ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* );
    public final BaseDescr lhs_and() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr lhsunary = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d = null;
        		AndDescr and = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:812:2: ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* )
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==LEFT_PAREN) ) {
                int LA40_1 = input.LA(2);

                if ( (LA40_1==AND) ) {
                    alt40=1;
                }
                else if ( (LA40_1==ID||LA40_1==LEFT_PAREN||(LA40_1>=EXISTS && LA40_1<=FORALL)) ) {
                    alt40=2;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("807:1: lhs_and returns [BaseDescr d] : ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* );", 40, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA40_0==ID||(LA40_0>=EXISTS && LA40_0<=FORALL)) ) {
                alt40=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("807:1: lhs_and returns [BaseDescr d] : ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* );", 40, 0, input);

                throw nvae;
            }
            switch (alt40) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:812:4: LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_and2129); if (failed) return d;
                    match(input,AND,FOLLOW_AND_in_lhs_and2131); if (failed) return d;
                    if ( backtracking==0 ) {

                      			and = new AndDescr();
                      			d = and;
                      			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                      		
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:818:3: (lhsunary= lhs_unary )+
                    int cnt38=0;
                    loop38:
                    do {
                        int alt38=2;
                        int LA38_0 = input.LA(1);

                        if ( (LA38_0==ID||LA38_0==LEFT_PAREN||(LA38_0>=EXISTS && LA38_0<=FORALL)) ) {
                            alt38=1;
                        }


                        switch (alt38) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:818:4: lhsunary= lhs_unary
                    	    {
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and2143);
                    	    lhsunary=lhs_unary();
                    	    _fsp--;
                    	    if (failed) return d;
                    	    if ( backtracking==0 ) {

                    	      			and.addDescr( lhsunary );
                    	      		
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt38 >= 1 ) break loop38;
                    	    if (backtracking>0) {failed=true; return d;}
                                EarlyExitException eee =
                                    new EarlyExitException(38, input);
                                throw eee;
                        }
                        cnt38++;
                    } while (true);

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_and2153); if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:824:10: left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )*
                    {
                    pushFollow(FOLLOW_lhs_unary_in_lhs_and2171);
                    left=lhs_unary();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = left; 
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:825:3: ( ( AND | DOUBLE_AMPER ) right= lhs_unary )*
                    loop39:
                    do {
                        int alt39=2;
                        int LA39_0 = input.LA(1);

                        if ( ((LA39_0>=AND && LA39_0<=DOUBLE_AMPER)) ) {
                            alt39=1;
                        }


                        switch (alt39) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:825:5: ( AND | DOUBLE_AMPER ) right= lhs_unary
                    	    {
                    	    if ( (input.LA(1)>=AND && input.LA(1)<=DOUBLE_AMPER) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return d;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and2179);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {

                    	      				location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                    	      			
                    	    }
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and2195);
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
                    	    break loop39;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:842:1: lhs_unary returns [BaseDescr d] : ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source ) opt_semicolon ;
    public final BaseDescr lhs_unary() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr u = null;

        BaseDescr ps = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:846:2: ( ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source ) opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:846:4: ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source ) opt_semicolon
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:846:4: ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source )
            int alt41=6;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==EXISTS) && (synpred1())) {
                alt41=1;
            }
            else if ( (LA41_0==NOT) && (synpred2())) {
                alt41=2;
            }
            else if ( (LA41_0==EVAL) && (synpred3())) {
                alt41=3;
            }
            else if ( (LA41_0==FORALL) && (synpred4())) {
                alt41=4;
            }
            else if ( (LA41_0==LEFT_PAREN) && (synpred5())) {
                alt41=5;
            }
            else if ( (LA41_0==ID) ) {
                alt41=6;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("846:4: ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source )", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:846:6: ( EXISTS )=>u= lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary2240);
                    u=lhs_exist();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:847:5: ( NOT )=>u= lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary2258);
                    u=lhs_not();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:848:5: ( EVAL )=>u= lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary2277);
                    u=lhs_eval();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:849:5: ( FORALL )=>u= lhs_forall
                    {
                    pushFollow(FOLLOW_lhs_forall_in_lhs_unary2296);
                    u=lhs_forall();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:850:5: ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary2313); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_unary2317);
                    u=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary2319); if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:851:5: ps= pattern_source
                    {
                    pushFollow(FOLLOW_pattern_source_in_lhs_unary2330);
                    ps=pattern_source();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = (BaseDescr) ps; 
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_opt_semicolon_in_lhs_unary2342);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:856:1: pattern_source returns [BaseDescr d] : u= lhs_pattern ( WITH ( WINDOW COLON type= identifier param= paren_chunk )+ )? ( ( ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement ) | FROM ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )? ;
    public final BaseDescr pattern_source() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr u = null;

        identifier_return type = null;

        paren_chunk_return param = null;

        EntryPointDescr ep = null;

        AccumulateDescr ac = null;

        CollectDescr cs = null;

        FromDescr fm = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:860:2: (u= lhs_pattern ( WITH ( WINDOW COLON type= identifier param= paren_chunk )+ )? ( ( ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement ) | FROM ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:861:3: u= lhs_pattern ( WITH ( WINDOW COLON type= identifier param= paren_chunk )+ )? ( ( ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement ) | FROM ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )?
            {
            pushFollow(FOLLOW_lhs_pattern_in_pattern_source2369);
            u=lhs_pattern();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               d = u; 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:862:3: ( WITH ( WINDOW COLON type= identifier param= paren_chunk )+ )?
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==WITH) ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:862:11: WITH ( WINDOW COLON type= identifier param= paren_chunk )+
                    {
                    match(input,WITH,FOLLOW_WITH_in_pattern_source2384); if (failed) return d;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:863:3: ( WINDOW COLON type= identifier param= paren_chunk )+
                    int cnt42=0;
                    loop42:
                    do {
                        int alt42=2;
                        int LA42_0 = input.LA(1);

                        if ( (LA42_0==WINDOW) ) {
                            alt42=1;
                        }


                        switch (alt42) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:863:4: WINDOW COLON type= identifier param= paren_chunk
                    	    {
                    	    match(input,WINDOW,FOLLOW_WINDOW_in_pattern_source2390); if (failed) return d;
                    	    match(input,COLON,FOLLOW_COLON_in_pattern_source2392); if (failed) return d;
                    	    pushFollow(FOLLOW_identifier_in_pattern_source2396);
                    	    type=identifier();
                    	    _fsp--;
                    	    if (failed) return d;
                    	    pushFollow(FOLLOW_paren_chunk_in_pattern_source2400);
                    	    param=paren_chunk();
                    	    _fsp--;
                    	    if (failed) return d;
                    	    if ( backtracking==0 ) {

                    	      		                SlidingWindowDescr window = new SlidingWindowDescr( input.toString(type.start,type.stop), getString( input.toString(param.start,param.stop) ).trim() );
                    	      		                ((PatternDescr)d).addBehavior( window );
                    	      		        
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt42 >= 1 ) break loop42;
                    	    if (backtracking>0) {failed=true; return d;}
                                EarlyExitException eee =
                                    new EarlyExitException(42, input);
                                throw eee;
                        }
                        cnt42++;
                    } while (true);


                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:870:3: ( ( ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement ) | FROM ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )?
            int alt45=3;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==FROM) ) {
                int LA45_1 = input.LA(2);

                if ( (LA45_1==ENTRY_POINT) && (synpred6())) {
                    alt45=1;
                }
                else if ( ((LA45_1>=PACKAGE && LA45_1<=ID)||LA45_1==GLOBAL||LA45_1==END||(LA45_1>=QUERY && LA45_1<=ATTRIBUTES)||LA45_1==ENABLED||LA45_1==SALIENCE||(LA45_1>=DURATION && LA45_1<=DIALECT)||LA45_1==FROM||(LA45_1>=ACCUMULATE && LA45_1<=COLLECT)||LA45_1==IN||(LA45_1>=THEN && LA45_1<=EVENT)) ) {
                    alt45=2;
                }
            }
            switch (alt45) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:11: ( ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:11: ( ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:13: ( FROM ENTRY_POINT )=> FROM ep= entrypoint_statement
                    {
                    match(input,FROM,FOLLOW_FROM_in_pattern_source2471); if (failed) return d;
                    pushFollow(FOLLOW_entrypoint_statement_in_pattern_source2475);
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:875:4: FROM ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) )
                    {
                    match(input,FROM,FOLLOW_FROM_in_pattern_source2495); if (failed) return d;
                    if ( backtracking==0 ) {

                      				location.setType(Location.LOCATION_LHS_FROM);
                      				location.setProperty(Location.LOCATION_FROM_CONTENT, "");
                      		        
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:880:11: ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) )
                    int alt44=3;
                    switch ( input.LA(1) ) {
                    case ACCUMULATE:
                        {
                        alt44=1;
                        }
                        break;
                    case COLLECT:
                        {
                        alt44=2;
                        }
                        break;
                    case PACKAGE:
                    case IMPORT:
                    case FUNCTION:
                    case ID:
                    case GLOBAL:
                    case END:
                    case QUERY:
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
                        alt44=3;
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return d;}
                        NoViableAltException nvae =
                            new NoViableAltException("880:11: ( options {k=1; backtrack=true; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) )", 44, 0, input);

                        throw nvae;
                    }

                    switch (alt44) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:881:13: (ac= accumulate_statement )
                            {
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:881:13: (ac= accumulate_statement )
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:881:15: ac= accumulate_statement
                            {
                            pushFollow(FOLLOW_accumulate_statement_in_pattern_source2553);
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
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:882:15: (cs= collect_statement )
                            {
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:882:15: (cs= collect_statement )
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:882:17: cs= collect_statement
                            {
                            pushFollow(FOLLOW_collect_statement_in_pattern_source2576);
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
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:884:15: (fm= from_statement )
                            {
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:884:15: (fm= from_statement )
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:884:17: fm= from_statement
                            {
                            pushFollow(FOLLOW_from_statement_in_pattern_source2613);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:889:1: lhs_exist returns [BaseDescr d] : EXISTS ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) ;
    public final BaseDescr lhs_exist() throws RecognitionException {
        BaseDescr d = null;

        Token EXISTS37=null;
        Token RIGHT_PAREN38=null;
        BaseDescr or = null;

        BaseDescr pattern = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:893:2: ( EXISTS ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:893:4: EXISTS ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
            {
            EXISTS37=(Token)input.LT(1);
            match(input,EXISTS,FOLLOW_EXISTS_in_lhs_exist2656); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new ExistsDescr( ); 
              			d.setLocation( offset(EXISTS37.getLine()), EXISTS37.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)EXISTS37).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:900:10: ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==LEFT_PAREN) ) {
                alt46=1;
            }
            else if ( (LA46_0==ID) ) {
                alt46=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("900:10: ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )", 46, 0, input);

                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:900:12: ( LEFT_PAREN or= lhs_or RIGHT_PAREN )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:900:12: ( LEFT_PAREN or= lhs_or RIGHT_PAREN )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:900:14: LEFT_PAREN or= lhs_or RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist2676); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist2680);
                    or=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( or != null ) ((ExistsDescr)d).addDescr( or ); 
                    }
                    RIGHT_PAREN38=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist2710); if (failed) return d;
                    if ( backtracking==0 ) {
                       d.setEndCharacter( ((CommonToken)RIGHT_PAREN38).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:905:12: pattern= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_exist2760);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:915:1: lhs_not returns [NotDescr d] : NOT ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) ;
    public final NotDescr lhs_not() throws RecognitionException {
        NotDescr d = null;

        Token NOT39=null;
        Token RIGHT_PAREN40=null;
        BaseDescr or = null;

        BaseDescr pattern = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:919:2: ( NOT ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:919:4: NOT ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
            {
            NOT39=(Token)input.LT(1);
            match(input,NOT,FOLLOW_NOT_in_lhs_not2812); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new NotDescr( ); 
              			d.setLocation( offset(NOT39.getLine()), NOT39.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)NOT39).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_NOT );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:926:3: ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==LEFT_PAREN) ) {
                alt47=1;
            }
            else if ( (LA47_0==ID) ) {
                alt47=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("926:3: ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )", 47, 0, input);

                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:926:5: ( LEFT_PAREN or= lhs_or RIGHT_PAREN )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:926:5: ( LEFT_PAREN or= lhs_or RIGHT_PAREN )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:926:7: LEFT_PAREN or= lhs_or RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not2825); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_not2829);
                    or=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( or != null ) d.addDescr( or ); 
                    }
                    RIGHT_PAREN40=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not2860); if (failed) return d;
                    if ( backtracking==0 ) {
                       d.setEndCharacter( ((CommonToken)RIGHT_PAREN40).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:932:3: pattern= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_not2897);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:942:1: lhs_eval returns [BaseDescr d] : EVAL c= paren_chunk ;
    public final BaseDescr lhs_eval() throws RecognitionException {
        BaseDescr d = null;

        Token EVAL41=null;
        paren_chunk_return c = null;



        		d = new EvalDescr( );
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:946:2: ( EVAL c= paren_chunk )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:947:3: EVAL c= paren_chunk
            {
            EVAL41=(Token)input.LT(1);
            match(input,EVAL,FOLLOW_EVAL_in_lhs_eval2943); if (failed) return d;
            if ( backtracking==0 ) {

              			location.setType( Location.LOCATION_LHS_INSIDE_EVAL );
              		
            }
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval2954);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:967:1: lhs_forall returns [ForallDescr d] : FORALL LEFT_PAREN base= lhs_pattern (pattern= lhs_pattern )* RIGHT_PAREN ;
    public final ForallDescr lhs_forall() throws RecognitionException {
        ForallDescr d = null;

        Token FORALL42=null;
        Token RIGHT_PAREN43=null;
        BaseDescr base = null;

        BaseDescr pattern = null;



        		d = factory.createForall();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:971:2: ( FORALL LEFT_PAREN base= lhs_pattern (pattern= lhs_pattern )* RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:971:4: FORALL LEFT_PAREN base= lhs_pattern (pattern= lhs_pattern )* RIGHT_PAREN
            {
            FORALL42=(Token)input.LT(1);
            match(input,FORALL,FOLLOW_FORALL_in_lhs_forall2980); if (failed) return d;
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_forall2982); if (failed) return d;
            pushFollow(FOLLOW_lhs_pattern_in_lhs_forall2986);
            base=lhs_pattern();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {

              			d.setStartCharacter( ((CommonToken)FORALL42).getStartIndex() );
              		        // adding the base pattern
              		        d.addDescr( base );
              			d.setLocation( offset(FORALL42.getLine()), FORALL42.getCharPositionInLine() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:978:3: (pattern= lhs_pattern )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( (LA48_0==ID) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:978:5: pattern= lhs_pattern
            	    {
            	    pushFollow(FOLLOW_lhs_pattern_in_lhs_forall3001);
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
            	    break loop48;
                }
            } while (true);

            RIGHT_PAREN43=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_forall3017); if (failed) return d;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:990:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact[null] );
    public final BaseDescr lhs_pattern() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr f = null;



        		d =null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:997:2: (f= fact_binding | f= fact[null] )
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==ID) ) {
                int LA49_1 = input.LA(2);

                if ( (LA49_1==COLON) ) {
                    alt49=1;
                }
                else if ( (LA49_1==DOT||LA49_1==LEFT_PAREN||LA49_1==LEFT_SQUARE) ) {
                    alt49=2;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("990:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact[null] );", 49, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("990:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact[null] );", 49, 0, input);

                throw nvae;
            }
            switch (alt49) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:997:4: f= fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_pattern3050);
                    f=fact_binding();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:998:4: f= fact[null]
                    {
                    pushFollow(FOLLOW_fact_in_lhs_pattern3058);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1001:1: from_statement returns [FromDescr d] : ds= from_source[$d] ;
    public final FromDescr from_statement() throws RecognitionException {
        FromDescr d = null;

        DeclarativeInvokerDescr ds = null;



        		d =factory.createFrom();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1005:2: (ds= from_source[$d] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1006:2: ds= from_source[$d]
            {
            pushFollow(FOLLOW_from_source_in_from_statement3085);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1013:1: accumulate_statement returns [AccumulateDescr d] : ACCUMULATE LEFT_PAREN inputCE= lhs_or ( COMMA )? ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) ) RIGHT_PAREN ;
    public final AccumulateDescr accumulate_statement() throws RecognitionException {
        AccumulateDescr d = null;

        Token id=null;
        Token ACCUMULATE44=null;
        Token RIGHT_PAREN45=null;
        BaseDescr inputCE = null;

        paren_chunk_return text = null;



        		d = factory.createAccumulate();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1017:2: ( ACCUMULATE LEFT_PAREN inputCE= lhs_or ( COMMA )? ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) ) RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1018:10: ACCUMULATE LEFT_PAREN inputCE= lhs_or ( COMMA )? ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) ) RIGHT_PAREN
            {
            ACCUMULATE44=(Token)input.LT(1);
            match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_accumulate_statement3122); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(ACCUMULATE44.getLine()), ACCUMULATE44.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)ACCUMULATE44).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement3132); if (failed) return d;
            pushFollow(FOLLOW_lhs_or_in_accumulate_statement3136);
            inputCE=lhs_or();
            _fsp--;
            if (failed) return d;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1024:29: ( COMMA )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==COMMA) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1024:29: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement3138); if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {

              		        d.setInput( inputCE );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1028:3: ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) )
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==INIT) ) {
                alt55=1;
            }
            else if ( (LA55_0==ID) ) {
                alt55=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1028:3: ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) )", 55, 0, input);

                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1028:5: ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1028:5: ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1029:4: INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk
                    {
                    match(input,INIT,FOLLOW_INIT_in_accumulate_statement3156); if (failed) return d;
                    if ( backtracking==0 ) {

                      				location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE_INIT );
                      			
                    }
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_statement3169);
                    text=paren_chunk();
                    _fsp--;
                    if (failed) return d;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1033:21: ( COMMA )?
                    int alt51=2;
                    int LA51_0 = input.LA(1);

                    if ( (LA51_0==COMMA) ) {
                        alt51=1;
                    }
                    switch (alt51) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1033:21: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement3171); if (failed) return d;

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
                    match(input,ACTION,FOLLOW_ACTION_in_accumulate_statement3182); if (failed) return d;
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_statement3186);
                    text=paren_chunk();
                    _fsp--;
                    if (failed) return d;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1041:28: ( COMMA )?
                    int alt52=2;
                    int LA52_0 = input.LA(1);

                    if ( (LA52_0==COMMA) ) {
                        alt52=1;
                    }
                    switch (alt52) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1041:28: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement3188); if (failed) return d;

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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1049:4: ( REVERSE text= paren_chunk ( COMMA )? )?
                    int alt54=2;
                    int LA54_0 = input.LA(1);

                    if ( (LA54_0==REVERSE) ) {
                        alt54=1;
                    }
                    switch (alt54) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1049:6: REVERSE text= paren_chunk ( COMMA )?
                            {
                            match(input,REVERSE,FOLLOW_REVERSE_in_accumulate_statement3201); if (failed) return d;
                            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement3205);
                            text=paren_chunk();
                            _fsp--;
                            if (failed) return d;
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1049:31: ( COMMA )?
                            int alt53=2;
                            int LA53_0 = input.LA(1);

                            if ( (LA53_0==COMMA) ) {
                                alt53=1;
                            }
                            switch (alt53) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1049:31: COMMA
                                    {
                                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement3207); if (failed) return d;

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

                    match(input,RESULT,FOLLOW_RESULT_in_accumulate_statement3224); if (failed) return d;
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_statement3228);
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1067:3: (id= ID text= paren_chunk )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1067:3: (id= ID text= paren_chunk )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1068:4: id= ID text= paren_chunk
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_accumulate_statement3254); if (failed) return d;
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_statement3258);
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
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement3275); if (failed) return d;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1088:1: from_source[FromDescr from] returns [DeclarativeInvokerDescr ds] : ident= identifier ( options {k=1; } : args= paren_chunk )? ( expression_chain[$from, ad] )? ;
    public final DeclarativeInvokerDescr from_source(FromDescr from) throws RecognitionException {
        DeclarativeInvokerDescr ds = null;

        identifier_return ident = null;

        paren_chunk_return args = null;



        		ds = null;
        		AccessorDescr ad = null;
        		FunctionCallDescr fc = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1094:2: (ident= identifier ( options {k=1; } : args= paren_chunk )? ( expression_chain[$from, ad] )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1094:4: ident= identifier ( options {k=1; } : args= paren_chunk )? ( expression_chain[$from, ad] )?
            {
            pushFollow(FOLLOW_identifier_in_from_source3306);
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1103:3: ( options {k=1; } : args= paren_chunk )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==LEFT_PAREN) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1110:5: args= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_from_source3334);
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

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1124:3: ( expression_chain[$from, ad] )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==DOT) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1124:3: expression_chain[$from, ad]
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source3347);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1135:1: expression_chain[FromDescr from, AccessorDescr as] : ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )? ) ;
    public final void expression_chain(FromDescr from, AccessorDescr as) throws RecognitionException {
        identifier_return field = null;

        square_chunk_return sqarg = null;

        paren_chunk_return paarg = null;



          		FieldAccessDescr fa = null;
        	    	MethodAccessDescr ma = null;	
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1140:2: ( ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )? ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1141:2: ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )? )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1141:2: ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1141:4: DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )?
            {
            match(input,DOT,FOLLOW_DOT_in_expression_chain3378); if (failed) return ;
            pushFollow(FOLLOW_identifier_in_expression_chain3382);
            field=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              	        fa = new FieldAccessDescr(((Token)field.start).getText());	
              		fa.setLocation( offset(((Token)field.start).getLine()), ((Token)field.start).getCharPositionInLine() );
              		fa.setStartCharacter( ((CommonToken)((Token)field.start)).getStartIndex() );
              		fa.setEndCharacter( ((CommonToken)((Token)field.start)).getStopIndex() );
              	    
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1148:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )?
            int alt58=3;
            alt58 = dfa58.predict(input);
            switch (alt58) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1149:6: ( LEFT_SQUARE )=>sqarg= square_chunk
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain3413);
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1155:6: ( LEFT_PAREN )=>paarg= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain3446);
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

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1163:4: ( expression_chain[from, as] )?
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==DOT) ) {
                alt59=1;
            }
            switch (alt59) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1163:4: expression_chain[from, as]
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain3461);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1177:1: collect_statement returns [CollectDescr d] : COLLECT LEFT_PAREN pattern= pattern_source RIGHT_PAREN ;
    public final CollectDescr collect_statement() throws RecognitionException {
        CollectDescr d = null;

        Token COLLECT46=null;
        Token RIGHT_PAREN47=null;
        BaseDescr pattern = null;



        		d = factory.createCollect();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1181:2: ( COLLECT LEFT_PAREN pattern= pattern_source RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1182:10: COLLECT LEFT_PAREN pattern= pattern_source RIGHT_PAREN
            {
            COLLECT46=(Token)input.LT(1);
            match(input,COLLECT,FOLLOW_COLLECT_in_collect_statement3512); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(COLLECT46.getLine()), COLLECT46.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)COLLECT46).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_FROM_COLLECT );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement3522); if (failed) return d;
            pushFollow(FOLLOW_pattern_source_in_collect_statement3526);
            pattern=pattern_source();
            _fsp--;
            if (failed) return d;
            RIGHT_PAREN47=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement3528); if (failed) return d;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1196:1: entrypoint_statement returns [EntryPointDescr d] : ENTRY_POINT id= name ;
    public final EntryPointDescr entrypoint_statement() throws RecognitionException {
        EntryPointDescr d = null;

        Token ENTRY_POINT48=null;
        name_return id = null;



        		d = factory.createEntryPoint();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1200:2: ( ENTRY_POINT id= name )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1201:10: ENTRY_POINT id= name
            {
            ENTRY_POINT48=(Token)input.LT(1);
            match(input,ENTRY_POINT,FOLLOW_ENTRY_POINT_in_entrypoint_statement3565); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(ENTRY_POINT48.getLine()), ENTRY_POINT48.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)ENTRY_POINT48).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_FROM_ENTRY_POINT );
              		
            }
            pushFollow(FOLLOW_name_in_entrypoint_statement3577);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1215:1: fact_binding returns [BaseDescr d] : ID COLON (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN ) ;
    public final BaseDescr fact_binding() throws RecognitionException {
        BaseDescr d = null;

        Token ID49=null;
        BaseDescr fe = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d =null;
        		OrDescr or = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1220:3: ( ID COLON (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1221:4: ID COLON (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN )
            {
            ID49=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding3609); if (failed) return d;
            match(input,COLON,FOLLOW_COLON_in_fact_binding3611); if (failed) return d;
            if ( backtracking==0 ) {

               		        // handling incomplete parsing
               		        d = new PatternDescr( );
               		        ((PatternDescr) d).setIdentifier( ID49.getText() );
               		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1227:3: (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN )
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==ID) ) {
                alt61=1;
            }
            else if ( (LA61_0==LEFT_PAREN) ) {
                alt61=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1227:3: (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN )", 61, 0, input);

                throw nvae;
            }
            switch (alt61) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1227:5: fe= fact[$ID.text]
                    {
                    pushFollow(FOLLOW_fact_in_fact_binding3625);
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1236:4: LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_binding3641); if (failed) return d;
                    pushFollow(FOLLOW_fact_in_fact_binding3645);
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1244:4: ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )*
                    loop60:
                    do {
                        int alt60=2;
                        int LA60_0 = input.LA(1);

                        if ( ((LA60_0>=OR && LA60_0<=DOUBLE_PIPE)) ) {
                            alt60=1;
                        }


                        switch (alt60) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1244:6: ( OR | DOUBLE_PIPE ) right= fact[$ID.text]
                    	    {
                    	    if ( (input.LA(1)>=OR && input.LA(1)<=DOUBLE_PIPE) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return d;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fact_binding3658);    throw mse;
                    	    }

                    	    pushFollow(FOLLOW_fact_in_fact_binding3670);
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
                    	    break loop60;
                        }
                    } while (true);

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_binding3688); if (failed) return d;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1259:1: fact[String ident] returns [BaseDescr d] : id= qualified_id LEFT_PAREN ( constraints[pattern] )? RIGHT_PAREN ( EOF )? ;
    public final BaseDescr fact(String ident) throws RecognitionException {
        BaseDescr d = null;

        Token LEFT_PAREN50=null;
        Token RIGHT_PAREN51=null;
        qualified_id_return id = null;



        		d =null;
        		PatternDescr pattern = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1264:3: (id= qualified_id LEFT_PAREN ( constraints[pattern] )? RIGHT_PAREN ( EOF )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1265:11: id= qualified_id LEFT_PAREN ( constraints[pattern] )? RIGHT_PAREN ( EOF )?
            {
            if ( backtracking==0 ) {

               			pattern = new PatternDescr( );
               			if( ident != null ) {
               				pattern.setIdentifier( ident );
               			}
               			d = pattern; 
               	        
            }
            pushFollow(FOLLOW_qualified_id_in_fact3743);
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
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact3753); if (failed) return d;
            if ( backtracking==0 ) {

              		        location.setType( Location.LOCATION_LHS_INSIDE_CONDITION_START );
                          		location.setProperty( Location.LOCATION_PROPERTY_CLASS_NAME, id.text );
               				
               			pattern.setLocation( offset(LEFT_PAREN50.getLine()), LEFT_PAREN50.getCharPositionInLine() );
               			pattern.setLeftParentCharacter( ((CommonToken)LEFT_PAREN50).getStartIndex() );
               		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1288:4: ( constraints[pattern] )?
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( ((LA62_0>=PACKAGE && LA62_0<=ID)||(LA62_0>=GLOBAL && LA62_0<=LEFT_PAREN)||LA62_0==END||(LA62_0>=QUERY && LA62_0<=ATTRIBUTES)||LA62_0==ENABLED||LA62_0==SALIENCE||(LA62_0>=DURATION && LA62_0<=DIALECT)||LA62_0==FROM||LA62_0==EVAL||(LA62_0>=INIT && LA62_0<=RESULT)||LA62_0==LEFT_SQUARE||LA62_0==IN||(LA62_0>=THEN && LA62_0<=EVENT)) ) {
                alt62=1;
            }
            switch (alt62) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1288:4: constraints[pattern]
                    {
                    pushFollow(FOLLOW_constraints_in_fact3765);
                    constraints(pattern);
                    _fsp--;
                    if (failed) return d;

                    }
                    break;

            }

            RIGHT_PAREN51=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact3772); if (failed) return d;
            if ( backtracking==0 ) {

              		        if( ")".equals( RIGHT_PAREN51.getText() ) ) {
              				this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
              				pattern.setEndLocation( offset(RIGHT_PAREN51.getLine()), RIGHT_PAREN51.getCharPositionInLine() );	
              				pattern.setEndCharacter( ((CommonToken)RIGHT_PAREN51).getStopIndex() );
              		        	pattern.setRightParentCharacter( ((CommonToken)RIGHT_PAREN51).getStartIndex() );
              		        }
               		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1298:4: ( EOF )?
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( (LA63_0==EOF) ) {
                alt63=1;
            }
            switch (alt63) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1298:4: EOF
                    {
                    match(input,EOF,FOLLOW_EOF_in_fact3781); if (failed) return d;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1302:1: constraints[PatternDescr pattern] : ( constraint[$pattern] | behaviors ) ( COMMA ( constraint[$pattern] | behaviors ) )* ;
    public final void constraints(PatternDescr pattern) throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1303:2: ( ( constraint[$pattern] | behaviors ) ( COMMA ( constraint[$pattern] | behaviors ) )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1303:4: ( constraint[$pattern] | behaviors ) ( COMMA ( constraint[$pattern] | behaviors ) )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1303:4: ( constraint[$pattern] | behaviors )
            int alt64=2;
            int LA64_0 = input.LA(1);

            if ( ((LA64_0>=PACKAGE && LA64_0<=ID)||(LA64_0>=GLOBAL && LA64_0<=LEFT_PAREN)||LA64_0==END||(LA64_0>=QUERY && LA64_0<=ATTRIBUTES)||LA64_0==ENABLED||LA64_0==SALIENCE||(LA64_0>=DURATION && LA64_0<=DIALECT)||LA64_0==FROM||LA64_0==EVAL||(LA64_0>=INIT && LA64_0<=RESULT)||LA64_0==IN||(LA64_0>=THEN && LA64_0<=EVENT)) ) {
                alt64=1;
            }
            else if ( (LA64_0==LEFT_SQUARE) ) {
                alt64=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1303:4: ( constraint[$pattern] | behaviors )", 64, 0, input);

                throw nvae;
            }
            switch (alt64) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1303:5: constraint[$pattern]
                    {
                    pushFollow(FOLLOW_constraint_in_constraints3800);
                    constraint(pattern);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1303:26: behaviors
                    {
                    pushFollow(FOLLOW_behaviors_in_constraints3803);
                    behaviors();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1304:3: ( COMMA ( constraint[$pattern] | behaviors ) )*
            loop66:
            do {
                int alt66=2;
                int LA66_0 = input.LA(1);

                if ( (LA66_0==COMMA) ) {
                    alt66=1;
                }


                switch (alt66) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1304:5: COMMA ( constraint[$pattern] | behaviors )
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_constraints3810); if (failed) return ;
            	    if ( backtracking==0 ) {
            	       location.setType( Location.LOCATION_LHS_INSIDE_CONDITION_START ); 
            	    }
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1305:5: ( constraint[$pattern] | behaviors )
            	    int alt65=2;
            	    int LA65_0 = input.LA(1);

            	    if ( ((LA65_0>=PACKAGE && LA65_0<=ID)||(LA65_0>=GLOBAL && LA65_0<=LEFT_PAREN)||LA65_0==END||(LA65_0>=QUERY && LA65_0<=ATTRIBUTES)||LA65_0==ENABLED||LA65_0==SALIENCE||(LA65_0>=DURATION && LA65_0<=DIALECT)||LA65_0==FROM||LA65_0==EVAL||(LA65_0>=INIT && LA65_0<=RESULT)||LA65_0==IN||(LA65_0>=THEN && LA65_0<=EVENT)) ) {
            	        alt65=1;
            	    }
            	    else if ( (LA65_0==LEFT_SQUARE) ) {
            	        alt65=2;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("1305:5: ( constraint[$pattern] | behaviors )", 65, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt65) {
            	        case 1 :
            	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1305:6: constraint[$pattern]
            	            {
            	            pushFollow(FOLLOW_constraint_in_constraints3820);
            	            constraint(pattern);
            	            _fsp--;
            	            if (failed) return ;

            	            }
            	            break;
            	        case 2 :
            	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1305:27: behaviors
            	            {
            	            pushFollow(FOLLOW_behaviors_in_constraints3823);
            	            behaviors();
            	            _fsp--;
            	            if (failed) return ;

            	            }
            	            break;

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
        return ;
    }
    // $ANTLR end constraints


    // $ANTLR start constraint
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1309:1: constraint[PatternDescr pattern] : or_constr[top] ;
    public final void constraint(PatternDescr pattern) throws RecognitionException {

        		ConditionalElementDescr top = null;
        		location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_START);
        		location.setProperty(Location.LOCATION_PROPERTY_PROPERTY_NAME, input.LT(1).getText() );
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1315:2: ( or_constr[top] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1316:3: or_constr[top]
            {
            if ( backtracking==0 ) {

              			top = pattern.getConstraint();
              		
            }
            pushFollow(FOLLOW_or_constr_in_constraint3856);
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


    // $ANTLR start behaviors
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1322:1: behaviors : LEFT_SQUARE behavior ( COMMA behavior )* RIGHT_SQUARE ;
    public final void behaviors() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1323:2: ( LEFT_SQUARE behavior ( COMMA behavior )* RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1323:4: LEFT_SQUARE behavior ( COMMA behavior )* RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_behaviors3871); if (failed) return ;
            pushFollow(FOLLOW_behavior_in_behaviors3873);
            behavior();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1323:25: ( COMMA behavior )*
            loop67:
            do {
                int alt67=2;
                int LA67_0 = input.LA(1);

                if ( (LA67_0==COMMA) ) {
                    alt67=1;
                }


                switch (alt67) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1323:27: COMMA behavior
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_behaviors3877); if (failed) return ;
            	    pushFollow(FOLLOW_behavior_in_behaviors3879);
            	    behavior();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop67;
                }
            } while (true);

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_behaviors3884); if (failed) return ;

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
    // $ANTLR end behaviors


    // $ANTLR start behavior
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1326:1: behavior : ID ( COLON ID )? ( paren_chunk )? ;
    public final void behavior() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1327:2: ( ID ( COLON ID )? ( paren_chunk )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1327:4: ID ( COLON ID )? ( paren_chunk )?
            {
            match(input,ID,FOLLOW_ID_in_behavior3898); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1327:7: ( COLON ID )?
            int alt68=2;
            int LA68_0 = input.LA(1);

            if ( (LA68_0==COLON) ) {
                alt68=1;
            }
            switch (alt68) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1327:8: COLON ID
                    {
                    match(input,COLON,FOLLOW_COLON_in_behavior3901); if (failed) return ;
                    match(input,ID,FOLLOW_ID_in_behavior3903); if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1327:19: ( paren_chunk )?
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==LEFT_PAREN) ) {
                alt69=1;
            }
            switch (alt69) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1327:19: paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_behavior3907);
                    paren_chunk();
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
    // $ANTLR end behavior


    // $ANTLR start or_constr
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1330:1: or_constr[ConditionalElementDescr base] : and_constr[or] ( DOUBLE_PIPE and_constr[or] )* ;
    public final void or_constr(ConditionalElementDescr base) throws RecognitionException {

        		OrDescr or = new OrDescr();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1334:2: ( and_constr[or] ( DOUBLE_PIPE and_constr[or] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1335:3: and_constr[or] ( DOUBLE_PIPE and_constr[or] )*
            {
            pushFollow(FOLLOW_and_constr_in_or_constr3929);
            and_constr(or);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1336:3: ( DOUBLE_PIPE and_constr[or] )*
            loop70:
            do {
                int alt70=2;
                int LA70_0 = input.LA(1);

                if ( (LA70_0==DOUBLE_PIPE) ) {
                    alt70=1;
                }


                switch (alt70) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1336:5: DOUBLE_PIPE and_constr[or]
            	    {
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_constr3937); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_START);
            	      		
            	    }
            	    pushFollow(FOLLOW_and_constr_in_or_constr3946);
            	    and_constr(or);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop70;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1351:1: and_constr[ConditionalElementDescr base] : unary_constr[and] ( DOUBLE_AMPER unary_constr[and] )* ;
    public final void and_constr(ConditionalElementDescr base) throws RecognitionException {

        		AndDescr and = new AndDescr();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1355:2: ( unary_constr[and] ( DOUBLE_AMPER unary_constr[and] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1356:3: unary_constr[and] ( DOUBLE_AMPER unary_constr[and] )*
            {
            pushFollow(FOLLOW_unary_constr_in_and_constr3978);
            unary_constr(and);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1357:3: ( DOUBLE_AMPER unary_constr[and] )*
            loop71:
            do {
                int alt71=2;
                int LA71_0 = input.LA(1);

                if ( (LA71_0==DOUBLE_AMPER) ) {
                    alt71=1;
                }


                switch (alt71) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1357:5: DOUBLE_AMPER unary_constr[and]
            	    {
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_constr3986); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_START);
            	      		
            	    }
            	    pushFollow(FOLLOW_unary_constr_in_and_constr3995);
            	    unary_constr(and);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop71;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1372:1: unary_constr[ConditionalElementDescr base] : ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] ) ;
    public final void unary_constr(ConditionalElementDescr base) throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1373:2: ( ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1374:3: ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1374:3: ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] )
            int alt72=3;
            switch ( input.LA(1) ) {
            case PACKAGE:
            case IMPORT:
            case FUNCTION:
            case ID:
            case GLOBAL:
            case END:
            case QUERY:
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
                alt72=1;
                }
                break;
            case LEFT_PAREN:
                {
                alt72=2;
                }
                break;
            case EVAL:
                {
                alt72=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1374:3: ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] )", 72, 0, input);

                throw nvae;
            }

            switch (alt72) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1374:5: field_constraint[$base]
                    {
                    pushFollow(FOLLOW_field_constraint_in_unary_constr4023);
                    field_constraint(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1375:5: LEFT_PAREN or_constr[$base] RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_unary_constr4031); if (failed) return ;
                    pushFollow(FOLLOW_or_constr_in_unary_constr4033);
                    or_constr(base);
                    _fsp--;
                    if (failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_unary_constr4036); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1376:5: EVAL predicate[$base]
                    {
                    match(input,EVAL,FOLLOW_EVAL_in_unary_constr4042); if (failed) return ;
                    pushFollow(FOLLOW_predicate_in_unary_constr4044);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1380:1: field_constraint[ConditionalElementDescr base] : ( ( ID COLON f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )? ) | (f= accessor_path or_restr_connective[top] ) );
    public final void field_constraint(ConditionalElementDescr base) throws RecognitionException {
        Token ID52=null;
        accessor_path_return f = null;



        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        		RestrictionConnectiveDescr top = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1386:2: ( ( ID COLON f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )? ) | (f= accessor_path or_restr_connective[top] ) )
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==ID) ) {
                int LA74_1 = input.LA(2);

                if ( (LA74_1==COLON) ) {
                    alt74=1;
                }
                else if ( (LA74_1==DOT||LA74_1==LEFT_PAREN||LA74_1==NOT||LA74_1==LEFT_SQUARE||(LA74_1>=CONTAINS && LA74_1<=IN)||(LA74_1>=84 && LA74_1<=89)) ) {
                    alt74=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1380:1: field_constraint[ConditionalElementDescr base] : ( ( ID COLON f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )? ) | (f= accessor_path or_restr_connective[top] ) );", 74, 1, input);

                    throw nvae;
                }
            }
            else if ( ((LA74_0>=PACKAGE && LA74_0<=FUNCTION)||LA74_0==GLOBAL||LA74_0==END||(LA74_0>=QUERY && LA74_0<=ATTRIBUTES)||LA74_0==ENABLED||LA74_0==SALIENCE||(LA74_0>=DURATION && LA74_0<=DIALECT)||LA74_0==FROM||(LA74_0>=INIT && LA74_0<=RESULT)||LA74_0==IN||(LA74_0>=THEN && LA74_0<=EVENT)) ) {
                alt74=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1380:1: field_constraint[ConditionalElementDescr base] : ( ( ID COLON f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )? ) | (f= accessor_path or_restr_connective[top] ) );", 74, 0, input);

                throw nvae;
            }
            switch (alt74) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1387:10: ( ID COLON f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )? )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1387:10: ( ID COLON f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )? )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1388:3: ID COLON f= accessor_path ( or_restr_connective[top] | '->' predicate[$base] )?
                    {
                    ID52=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_field_constraint4083); if (failed) return ;
                    match(input,COLON,FOLLOW_COLON_in_field_constraint4085); if (failed) return ;
                    if ( backtracking==0 ) {
                       
                      			fbd = new FieldBindingDescr();
                      			fbd.setIdentifier( ID52.getText() );
                      			fbd.setLocation( offset(ID52.getLine()), ID52.getCharPositionInLine() );
                      			fbd.setStartCharacter( ((CommonToken)ID52).getStartIndex() );
                      			base.addDescr( fbd );

                      		    
                    }
                    pushFollow(FOLLOW_accessor_path_in_field_constraint4104);
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1422:3: ( or_restr_connective[top] | '->' predicate[$base] )?
                    int alt73=3;
                    int LA73_0 = input.LA(1);

                    if ( (LA73_0==LEFT_PAREN||LA73_0==NOT||(LA73_0>=CONTAINS && LA73_0<=IN)||(LA73_0>=84 && LA73_0<=89)) ) {
                        alt73=1;
                    }
                    else if ( (LA73_0==83) ) {
                        alt73=2;
                    }
                    switch (alt73) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1423:4: or_restr_connective[top]
                            {
                            pushFollow(FOLLOW_or_restr_connective_in_field_constraint4118);
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
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1431:4: '->' predicate[$base]
                            {
                            match(input,83,FOLLOW_83_in_field_constraint4133); if (failed) return ;
                            pushFollow(FOLLOW_predicate_in_field_constraint4135);
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1435:3: (f= accessor_path or_restr_connective[top] )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1435:3: (f= accessor_path or_restr_connective[top] )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1436:3: f= accessor_path or_restr_connective[top]
                    {
                    pushFollow(FOLLOW_accessor_path_in_field_constraint4161);
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
                    pushFollow(FOLLOW_or_restr_connective_in_field_constraint4170);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1468:1: or_restr_connective[ RestrictionConnectiveDescr base ] options {backtrack=true; } : and_restr_connective[or] ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )* ;
    public final void or_restr_connective(RestrictionConnectiveDescr base) throws RecognitionException {

        		RestrictionConnectiveDescr or = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR);
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1475:2: ( and_restr_connective[or] ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1476:3: and_restr_connective[or] ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )*
            {
            pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective4217);
            and_restr_connective(or);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1477:3: ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )*
            loop75:
            do {
                int alt75=2;
                int LA75_0 = input.LA(1);

                if ( (LA75_0==DOUBLE_PIPE) ) {
                    switch ( input.LA(2) ) {
                    case IN:
                        {
                        int LA75_3 = input.LA(3);

                        if ( (LA75_3==LEFT_PAREN) ) {
                            int LA75_6 = input.LA(4);

                            if ( (synpred11()) ) {
                                alt75=1;
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
                    case 84:
                    case 85:
                    case 86:
                    case 87:
                    case 88:
                    case 89:
                        {
                        alt75=1;
                        }
                        break;
                    case LEFT_PAREN:
                        {
                        int LA75_5 = input.LA(3);

                        if ( (synpred11()) ) {
                            alt75=1;
                        }


                        }
                        break;

                    }

                }


                switch (alt75) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1479:4: DOUBLE_PIPE and_restr_connective[or]
            	    {
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_restr_connective4241); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	      			
            	    }
            	    pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective4252);
            	    and_restr_connective(or);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop75;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1494:1: and_restr_connective[ RestrictionConnectiveDescr base ] : constraint_expression[and] ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )* ;
    public final void and_restr_connective(RestrictionConnectiveDescr base) throws RecognitionException {
        Token t=null;


        		RestrictionConnectiveDescr and = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND);
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1498:2: ( constraint_expression[and] ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1499:3: constraint_expression[and] ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )*
            {
            pushFollow(FOLLOW_constraint_expression_in_and_restr_connective4284);
            constraint_expression(and);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1500:3: ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )*
            loop76:
            do {
                int alt76=2;
                int LA76_0 = input.LA(1);

                if ( (LA76_0==DOUBLE_AMPER) ) {
                    switch ( input.LA(2) ) {
                    case IN:
                        {
                        int LA76_3 = input.LA(3);

                        if ( (LA76_3==LEFT_PAREN) ) {
                            switch ( input.LA(4) ) {
                            case IN:
                                {
                                int LA76_9 = input.LA(5);

                                if ( (LA76_9==DOT||(LA76_9>=COMMA && LA76_9<=RIGHT_PAREN)||LA76_9==LEFT_SQUARE) ) {
                                    alt76=1;
                                }


                                }
                                break;
                            case PACKAGE:
                            case IMPORT:
                            case FUNCTION:
                            case ID:
                            case GLOBAL:
                            case END:
                            case QUERY:
                            case TEMPLATE:
                            case RULE:
                            case WHEN:
                            case ATTRIBUTES:
                            case STRING:
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
                                alt76=1;
                                }
                                break;
                            case LEFT_PAREN:
                                {
                                int LA76_10 = input.LA(5);

                                if ( (synpred12()) ) {
                                    alt76=1;
                                }


                                }
                                break;

                            }

                        }


                        }
                        break;
                    case LEFT_PAREN:
                        {
                        switch ( input.LA(3) ) {
                        case IN:
                            {
                            int LA76_7 = input.LA(4);

                            if ( (LA76_7==LEFT_PAREN) ) {
                                int LA76_11 = input.LA(5);

                                if ( (synpred12()) ) {
                                    alt76=1;
                                }


                            }


                            }
                            break;
                        case LEFT_PAREN:
                            {
                            int LA76_8 = input.LA(4);

                            if ( (synpred12()) ) {
                                alt76=1;
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
                        case 84:
                        case 85:
                        case 86:
                        case 87:
                        case 88:
                        case 89:
                            {
                            alt76=1;
                            }
                            break;

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
                    case 84:
                    case 85:
                    case 86:
                    case 87:
                    case 88:
                    case 89:
                        {
                        alt76=1;
                        }
                        break;

                    }

                }


                switch (alt76) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1501:5: t= DOUBLE_AMPER constraint_expression[and]
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_restr_connective4304); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	      			
            	    }
            	    pushFollow(FOLLOW_constraint_expression_in_and_restr_connective4315);
            	    constraint_expression(and);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop76;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1516:1: constraint_expression[RestrictionConnectiveDescr base] : ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN ) ;
    public final void constraint_expression(RestrictionConnectiveDescr base) throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1517:9: ( ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1518:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1518:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )
            int alt77=3;
            switch ( input.LA(1) ) {
            case IN:
                {
                alt77=1;
                }
                break;
            case NOT:
                {
                int LA77_2 = input.LA(2);

                if ( (LA77_2==IN) ) {
                    alt77=1;
                }
                else if ( (LA77_2==CONTAINS||LA77_2==MATCHES||(LA77_2>=MEMBEROF && LA77_2<=TILDE)) ) {
                    alt77=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1518:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )", 77, 2, input);

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
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
                {
                alt77=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt77=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1518:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )", 77, 0, input);

                throw nvae;
            }

            switch (alt77) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1518:5: compound_operator[$base]
                    {
                    pushFollow(FOLLOW_compound_operator_in_constraint_expression4352);
                    compound_operator(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1519:5: simple_operator[$base]
                    {
                    pushFollow(FOLLOW_simple_operator_in_constraint_expression4359);
                    simple_operator(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1520:5: LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_constraint_expression4367); if (failed) return ;
                    if ( backtracking==0 ) {

                      			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
                      		
                    }
                    pushFollow(FOLLOW_or_restr_connective_in_constraint_expression4376);
                    or_restr_connective(base);
                    _fsp--;
                    if (failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_constraint_expression4381); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1529:1: simple_operator[RestrictionConnectiveDescr base] : (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | t= SOUNDSLIKE | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF | TILDE t= ID (param= square_chunk )? | n= NOT TILDE t= ID (param= square_chunk )? ) rd= expression_value[$base, op, isNegated, paramText] ;
    public final void simple_operator(RestrictionConnectiveDescr base) throws RecognitionException {
        Token t=null;
        Token n=null;
        square_chunk_return param = null;

        RestrictionDescr rd = null;



        		String op = null;
        		String paramText = null;
        		boolean isNegated = false;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1535:2: ( (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | t= SOUNDSLIKE | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF | TILDE t= ID (param= square_chunk )? | n= NOT TILDE t= ID (param= square_chunk )? ) rd= expression_value[$base, op, isNegated, paramText] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1536:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | t= SOUNDSLIKE | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF | TILDE t= ID (param= square_chunk )? | n= NOT TILDE t= ID (param= square_chunk )? ) rd= expression_value[$base, op, isNegated, paramText]
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1536:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | t= SOUNDSLIKE | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF | TILDE t= ID (param= square_chunk )? | n= NOT TILDE t= ID (param= square_chunk )? )
            int alt80=16;
            switch ( input.LA(1) ) {
            case 84:
                {
                alt80=1;
                }
                break;
            case 85:
                {
                alt80=2;
                }
                break;
            case 86:
                {
                alt80=3;
                }
                break;
            case 87:
                {
                alt80=4;
                }
                break;
            case 88:
                {
                alt80=5;
                }
                break;
            case 89:
                {
                alt80=6;
                }
                break;
            case CONTAINS:
                {
                alt80=7;
                }
                break;
            case NOT:
                {
                switch ( input.LA(2) ) {
                case MEMBEROF:
                    {
                    alt80=14;
                    }
                    break;
                case MATCHES:
                    {
                    alt80=12;
                    }
                    break;
                case TILDE:
                    {
                    alt80=16;
                    }
                    break;
                case CONTAINS:
                    {
                    alt80=8;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1536:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | t= SOUNDSLIKE | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF | TILDE t= ID (param= square_chunk )? | n= NOT TILDE t= ID (param= square_chunk )? )", 80, 8, input);

                    throw nvae;
                }

                }
                break;
            case EXCLUDES:
                {
                alt80=9;
                }
                break;
            case MATCHES:
                {
                alt80=10;
                }
                break;
            case SOUNDSLIKE:
                {
                alt80=11;
                }
                break;
            case MEMBEROF:
                {
                alt80=13;
                }
                break;
            case TILDE:
                {
                alt80=15;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1536:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | t= SOUNDSLIKE | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF | TILDE t= ID (param= square_chunk )? | n= NOT TILDE t= ID (param= square_chunk )? )", 80, 0, input);

                throw nvae;
            }

            switch (alt80) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1536:5: t= '=='
                    {
                    t=(Token)input.LT(1);
                    match(input,84,FOLLOW_84_in_simple_operator4412); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1537:5: t= '>'
                    {
                    t=(Token)input.LT(1);
                    match(input,85,FOLLOW_85_in_simple_operator4420); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1538:5: t= '>='
                    {
                    t=(Token)input.LT(1);
                    match(input,86,FOLLOW_86_in_simple_operator4428); if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1539:5: t= '<'
                    {
                    t=(Token)input.LT(1);
                    match(input,87,FOLLOW_87_in_simple_operator4436); if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1540:5: t= '<='
                    {
                    t=(Token)input.LT(1);
                    match(input,88,FOLLOW_88_in_simple_operator4444); if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1541:5: t= '!='
                    {
                    t=(Token)input.LT(1);
                    match(input,89,FOLLOW_89_in_simple_operator4452); if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1542:25: t= CONTAINS
                    {
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_simple_operator4480); if (failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1543:25: n= NOT t= CONTAINS
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator4508); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_simple_operator4512); if (failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1544:25: t= EXCLUDES
                    {
                    t=(Token)input.LT(1);
                    match(input,EXCLUDES,FOLLOW_EXCLUDES_in_simple_operator4540); if (failed) return ;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1545:25: t= MATCHES
                    {
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_simple_operator4568); if (failed) return ;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1546:25: t= SOUNDSLIKE
                    {
                    t=(Token)input.LT(1);
                    match(input,SOUNDSLIKE,FOLLOW_SOUNDSLIKE_in_simple_operator4596); if (failed) return ;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1547:25: n= NOT t= MATCHES
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator4624); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_simple_operator4628); if (failed) return ;

                    }
                    break;
                case 13 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1548:25: t= MEMBEROF
                    {
                    t=(Token)input.LT(1);
                    match(input,MEMBEROF,FOLLOW_MEMBEROF_in_simple_operator4656); if (failed) return ;

                    }
                    break;
                case 14 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1549:25: n= NOT t= MEMBEROF
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator4684); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,MEMBEROF,FOLLOW_MEMBEROF_in_simple_operator4688); if (failed) return ;

                    }
                    break;
                case 15 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1550:5: TILDE t= ID (param= square_chunk )?
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_simple_operator4694); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_simple_operator4698); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1550:21: (param= square_chunk )?
                    int alt78=2;
                    int LA78_0 = input.LA(1);

                    if ( (LA78_0==LEFT_SQUARE) ) {
                        alt78=1;
                    }
                    switch (alt78) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1550:21: param= square_chunk
                            {
                            pushFollow(FOLLOW_square_chunk_in_simple_operator4702);
                            param=square_chunk();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 16 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1551:5: n= NOT TILDE t= ID (param= square_chunk )?
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator4711); if (failed) return ;
                    match(input,TILDE,FOLLOW_TILDE_in_simple_operator4713); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_simple_operator4717); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1551:27: (param= square_chunk )?
                    int alt79=2;
                    int LA79_0 = input.LA(1);

                    if ( (LA79_0==LEFT_SQUARE) ) {
                        alt79=1;
                    }
                    switch (alt79) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1551:27: param= square_chunk
                            {
                            pushFollow(FOLLOW_square_chunk_in_simple_operator4721);
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
            pushFollow(FOLLOW_expression_value_in_simple_operator4736);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1570:1: compound_operator[RestrictionConnectiveDescr base] : ( IN | NOT IN ) LEFT_PAREN rd= expression_value[group, op, false, null] ( COMMA rd= expression_value[group, op, false, null] )* RIGHT_PAREN ;
    public final void compound_operator(RestrictionConnectiveDescr base) throws RecognitionException {
        RestrictionDescr rd = null;



        		String op = null;
        		RestrictionConnectiveDescr group = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1575:2: ( ( IN | NOT IN ) LEFT_PAREN rd= expression_value[group, op, false, null] ( COMMA rd= expression_value[group, op, false, null] )* RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1576:3: ( IN | NOT IN ) LEFT_PAREN rd= expression_value[group, op, false, null] ( COMMA rd= expression_value[group, op, false, null] )* RIGHT_PAREN
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1576:3: ( IN | NOT IN )
            int alt81=2;
            int LA81_0 = input.LA(1);

            if ( (LA81_0==IN) ) {
                alt81=1;
            }
            else if ( (LA81_0==NOT) ) {
                alt81=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1576:3: ( IN | NOT IN )", 81, 0, input);

                throw nvae;
            }
            switch (alt81) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1576:5: IN
                    {
                    match(input,IN,FOLLOW_IN_in_compound_operator4766); if (failed) return ;
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1584:5: NOT IN
                    {
                    match(input,NOT,FOLLOW_NOT_in_compound_operator4778); if (failed) return ;
                    match(input,IN,FOLLOW_IN_in_compound_operator4780); if (failed) return ;
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

            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_compound_operator4795); if (failed) return ;
            pushFollow(FOLLOW_expression_value_in_compound_operator4799);
            rd=expression_value(group,  op,  false,  null);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1594:3: ( COMMA rd= expression_value[group, op, false, null] )*
            loop82:
            do {
                int alt82=2;
                int LA82_0 = input.LA(1);

                if ( (LA82_0==COMMA) ) {
                    alt82=1;
                }


                switch (alt82) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1594:5: COMMA rd= expression_value[group, op, false, null]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_compound_operator4806); if (failed) return ;
            	    pushFollow(FOLLOW_expression_value_in_compound_operator4810);
            	    rd=expression_value(group,  op,  false,  null);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop82;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_compound_operator4819); if (failed) return ;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1601:1: expression_value[RestrictionConnectiveDescr base, String op, boolean isNegated, String paramText] returns [RestrictionDescr rd] : (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk ) ;
    public final RestrictionDescr expression_value(RestrictionConnectiveDescr base, String op, boolean isNegated, String paramText) throws RecognitionException {
        RestrictionDescr rd = null;

        accessor_path_return ap = null;

        literal_constraint_return lc = null;

        paren_chunk_return rvc = null;



        		rd = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1605:2: ( (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1606:3: (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1606:3: (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk )
            int alt83=3;
            switch ( input.LA(1) ) {
            case PACKAGE:
            case IMPORT:
            case FUNCTION:
            case ID:
            case GLOBAL:
            case END:
            case QUERY:
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
                alt83=1;
                }
                break;
            case STRING:
            case BOOL:
            case INT:
            case FLOAT:
            case NULL:
                {
                alt83=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt83=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return rd;}
                NoViableAltException nvae =
                    new NoViableAltException("1606:3: (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk )", 83, 0, input);

                throw nvae;
            }

            switch (alt83) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1606:5: ap= accessor_path
                    {
                    pushFollow(FOLLOW_accessor_path_in_expression_value4853);
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1614:5: lc= literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_expression_value4873);
                    lc=literal_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd = new LiteralRestrictionDescr(op, isNegated, paramText, lc.text, lc.type );
                      			
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1618:5: rvc= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_value4887);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1631:1: literal_constraint returns [String text, int type] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public final literal_constraint_return literal_constraint() throws RecognitionException {
        literal_constraint_return retval = new literal_constraint_return();
        retval.start = input.LT(1);

        Token t=null;


        		retval.text = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1635:2: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1635:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1635:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            int alt84=5;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt84=1;
                }
                break;
            case INT:
                {
                alt84=2;
                }
                break;
            case FLOAT:
                {
                alt84=3;
                }
                break;
            case BOOL:
                {
                alt84=4;
                }
                break;
            case NULL:
                {
                alt84=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1635:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )", 84, 0, input);

                throw nvae;
            }

            switch (alt84) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1635:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint4930); if (failed) return retval;
                    if ( backtracking==0 ) {
                       retval.text = getString( t.getText() ); retval.type = LiteralRestrictionDescr.TYPE_STRING; 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1636:5: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint4941); if (failed) return retval;
                    if ( backtracking==0 ) {
                       retval.text = t.getText(); retval.type = LiteralRestrictionDescr.TYPE_NUMBER; 
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1637:5: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint4954); if (failed) return retval;
                    if ( backtracking==0 ) {
                       retval.text = t.getText(); retval.type = LiteralRestrictionDescr.TYPE_NUMBER; 
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1638:5: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint4965); if (failed) return retval;
                    if ( backtracking==0 ) {
                       retval.text = t.getText(); retval.type = LiteralRestrictionDescr.TYPE_BOOLEAN; 
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1639:5: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal_constraint4977); if (failed) return retval;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1643:1: predicate[ConditionalElementDescr base] : text= paren_chunk ;
    public final void predicate(ConditionalElementDescr base) throws RecognitionException {
        paren_chunk_return text = null;



        		PredicateDescr d = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1647:2: (text= paren_chunk )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1648:3: text= paren_chunk
            {
            pushFollow(FOLLOW_paren_chunk_in_predicate5015);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1660:1: curly_chunk : LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk )* RIGHT_CURLY ;
    public final curly_chunk_return curly_chunk() throws RecognitionException {
        curly_chunk_return retval = new curly_chunk_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1661:2: ( LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk )* RIGHT_CURLY )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1662:3: LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk )* RIGHT_CURLY
            {
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk5033); if (failed) return retval;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1662:14: (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk )*
            loop85:
            do {
                int alt85=3;
                int LA85_0 = input.LA(1);

                if ( ((LA85_0>=PACKAGE && LA85_0<=NULL)||(LA85_0>=THEN && LA85_0<=89)) ) {
                    alt85=1;
                }
                else if ( (LA85_0==LEFT_CURLY) ) {
                    alt85=2;
                }


                switch (alt85) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1662:16: ~ ( LEFT_CURLY | RIGHT_CURLY )
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=NULL)||(input.LA(1)>=THEN && input.LA(1)<=89) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_curly_chunk5037);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1662:44: curly_chunk
            	    {
            	    pushFollow(FOLLOW_curly_chunk_in_curly_chunk5046);
            	    curly_chunk();
            	    _fsp--;
            	    if (failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop85;
                }
            } while (true);

            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk5051); if (failed) return retval;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1665:1: paren_chunk : LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk )* RIGHT_PAREN ;
    public final paren_chunk_return paren_chunk() throws RecognitionException {
        paren_chunk_return retval = new paren_chunk_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1666:2: ( LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk )* RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1667:3: LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk5065); if (failed) return retval;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1667:14: (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk )*
            loop86:
            do {
                int alt86=3;
                int LA86_0 = input.LA(1);

                if ( ((LA86_0>=PACKAGE && LA86_0<=GLOBAL)||LA86_0==COMMA||(LA86_0>=DECLARE && LA86_0<=89)) ) {
                    alt86=1;
                }
                else if ( (LA86_0==LEFT_PAREN) ) {
                    alt86=2;
                }


                switch (alt86) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1667:16: ~ ( LEFT_PAREN | RIGHT_PAREN )
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=GLOBAL)||input.LA(1)==COMMA||(input.LA(1)>=DECLARE && input.LA(1)<=89) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_paren_chunk5069);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1667:44: paren_chunk
            	    {
            	    pushFollow(FOLLOW_paren_chunk_in_paren_chunk5078);
            	    paren_chunk();
            	    _fsp--;
            	    if (failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop86;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk5083); if (failed) return retval;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1670:1: square_chunk : LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk )* RIGHT_SQUARE ;
    public final square_chunk_return square_chunk() throws RecognitionException {
        square_chunk_return retval = new square_chunk_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1671:2: ( LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk )* RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1672:3: LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk )* RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk5096); if (failed) return retval;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1672:15: (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk )*
            loop87:
            do {
                int alt87=3;
                int LA87_0 = input.LA(1);

                if ( ((LA87_0>=PACKAGE && LA87_0<=ENTRY_POINT)||(LA87_0>=CONTAINS && LA87_0<=89)) ) {
                    alt87=1;
                }
                else if ( (LA87_0==LEFT_SQUARE) ) {
                    alt87=2;
                }


                switch (alt87) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1672:17: ~ ( LEFT_SQUARE | RIGHT_SQUARE )
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=ENTRY_POINT)||(input.LA(1)>=CONTAINS && input.LA(1)<=89) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_square_chunk5100);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1672:47: square_chunk
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_square_chunk5109);
            	    square_chunk();
            	    _fsp--;
            	    if (failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop87;
                }
            } while (true);

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk5114); if (failed) return retval;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1675:1: qualified_id returns [ String text ] : ID ( DOT identifier )* ( LEFT_SQUARE RIGHT_SQUARE )* ;
    public final qualified_id_return qualified_id() throws RecognitionException {
        qualified_id_return retval = new qualified_id_return();
        retval.start = input.LT(1);

        Token ID53=null;
        identifier_return identifier54 = null;



        	        StringBuffer buf = new StringBuffer();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1682:2: ( ID ( DOT identifier )* ( LEFT_SQUARE RIGHT_SQUARE )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1682:5: ID ( DOT identifier )* ( LEFT_SQUARE RIGHT_SQUARE )*
            {
            ID53=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_qualified_id5143); if (failed) return retval;
            if ( backtracking==0 ) {
              buf.append(ID53.getText());
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1682:32: ( DOT identifier )*
            loop88:
            do {
                int alt88=2;
                int LA88_0 = input.LA(1);

                if ( (LA88_0==DOT) ) {
                    alt88=1;
                }


                switch (alt88) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1682:34: DOT identifier
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_qualified_id5149); if (failed) return retval;
            	    pushFollow(FOLLOW_identifier_in_qualified_id5151);
            	    identifier54=identifier();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) {
            	      buf.append("."+input.toString(identifier54.start,identifier54.stop));
            	    }

            	    }
            	    break;

            	default :
            	    break loop88;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1682:88: ( LEFT_SQUARE RIGHT_SQUARE )*
            loop89:
            do {
                int alt89=2;
                int LA89_0 = input.LA(1);

                if ( (LA89_0==LEFT_SQUARE) ) {
                    alt89=1;
                }


                switch (alt89) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1682:90: LEFT_SQUARE RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_qualified_id5160); if (failed) return retval;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_qualified_id5162); if (failed) return retval;
            	    if ( backtracking==0 ) {
            	      buf.append("[]");
            	    }

            	    }
            	    break;

            	default :
            	    break loop89;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1685:1: dotted_name returns [ String text ] : i= identifier ( DOT i= identifier )* ( LEFT_SQUARE RIGHT_SQUARE )* ;
    public final String dotted_name() throws RecognitionException {
        String text = null;

        identifier_return i = null;



        	        StringBuffer buf = new StringBuffer();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1692:2: (i= identifier ( DOT i= identifier )* ( LEFT_SQUARE RIGHT_SQUARE )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1692:4: i= identifier ( DOT i= identifier )* ( LEFT_SQUARE RIGHT_SQUARE )*
            {
            pushFollow(FOLLOW_identifier_in_dotted_name5196);
            i=identifier();
            _fsp--;
            if (failed) return text;
            if ( backtracking==0 ) {
              buf.append(input.toString(i.start,i.stop));
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1692:40: ( DOT i= identifier )*
            loop90:
            do {
                int alt90=2;
                int LA90_0 = input.LA(1);

                if ( (LA90_0==DOT) ) {
                    alt90=1;
                }


                switch (alt90) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1692:42: DOT i= identifier
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_dotted_name5202); if (failed) return text;
            	    pushFollow(FOLLOW_identifier_in_dotted_name5206);
            	    i=identifier();
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {
            	      buf.append("."+input.toString(i.start,i.stop));
            	    }

            	    }
            	    break;

            	default :
            	    break loop90;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1692:89: ( LEFT_SQUARE RIGHT_SQUARE )*
            loop91:
            do {
                int alt91=2;
                int LA91_0 = input.LA(1);

                if ( (LA91_0==LEFT_SQUARE) ) {
                    alt91=1;
                }


                switch (alt91) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1692:91: LEFT_SQUARE RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dotted_name5215); if (failed) return text;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dotted_name5217); if (failed) return text;
            	    if ( backtracking==0 ) {
            	      buf.append("[]");
            	    }

            	    }
            	    break;

            	default :
            	    break loop91;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1695:1: accessor_path returns [ String text ] : a= accessor_element ( DOT a= accessor_element )* ;
    public final accessor_path_return accessor_path() throws RecognitionException {
        accessor_path_return retval = new accessor_path_return();
        retval.start = input.LT(1);

        String a = null;



        	        StringBuffer buf = new StringBuffer();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1702:2: (a= accessor_element ( DOT a= accessor_element )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1702:4: a= accessor_element ( DOT a= accessor_element )*
            {
            pushFollow(FOLLOW_accessor_element_in_accessor_path5251);
            a=accessor_element();
            _fsp--;
            if (failed) return retval;
            if ( backtracking==0 ) {
              buf.append(a);
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1702:46: ( DOT a= accessor_element )*
            loop92:
            do {
                int alt92=2;
                int LA92_0 = input.LA(1);

                if ( (LA92_0==DOT) ) {
                    alt92=1;
                }


                switch (alt92) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1702:48: DOT a= accessor_element
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_accessor_path5257); if (failed) return retval;
            	    pushFollow(FOLLOW_accessor_element_in_accessor_path5261);
            	    a=accessor_element();
            	    _fsp--;
            	    if (failed) return retval;
            	    if ( backtracking==0 ) {
            	      buf.append("."+a);
            	    }

            	    }
            	    break;

            	default :
            	    break loop92;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1705:1: accessor_element returns [ String text ] : i= identifier (s= square_chunk )* ;
    public final String accessor_element() throws RecognitionException {
        String text = null;

        identifier_return i = null;

        square_chunk_return s = null;



        	        StringBuffer buf = new StringBuffer();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1712:2: (i= identifier (s= square_chunk )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1713:3: i= identifier (s= square_chunk )*
            {
            pushFollow(FOLLOW_identifier_in_accessor_element5299);
            i=identifier();
            _fsp--;
            if (failed) return text;
            if ( backtracking==0 ) {
              buf.append(input.toString(i.start,i.stop));
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1713:39: (s= square_chunk )*
            loop93:
            do {
                int alt93=2;
                int LA93_0 = input.LA(1);

                if ( (LA93_0==LEFT_SQUARE) ) {
                    alt93=1;
                }


                switch (alt93) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1713:40: s= square_chunk
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_accessor_element5306);
            	    s=square_chunk();
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {
            	      buf.append(input.toString(s.start,s.stop));
            	    }

            	    }
            	    break;

            	default :
            	    break loop93;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1716:1: rhs_chunk[RuleDescr rule] : THEN (~ END )* loc= END opt_semicolon ;
    public final void rhs_chunk(RuleDescr rule) throws RecognitionException {
        Token loc=null;
        Token THEN55=null;

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1717:2: ( THEN (~ END )* loc= END opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1718:3: THEN (~ END )* loc= END opt_semicolon
            {
            THEN55=(Token)input.LT(1);
            match(input,THEN,FOLLOW_THEN_in_rhs_chunk5327); if (failed) return ;
            if ( backtracking==0 ) {
               location.setType( Location.LOCATION_RHS ); 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1719:3: (~ END )*
            loop94:
            do {
                int alt94=2;
                int LA94_0 = input.LA(1);

                if ( ((LA94_0>=PACKAGE && LA94_0<=DECLARE)||(LA94_0>=AT && LA94_0<=89)) ) {
                    alt94=1;
                }


                switch (alt94) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1719:5: ~ END
            	    {
            	    if ( (input.LA(1)>=PACKAGE && input.LA(1)<=DECLARE)||(input.LA(1)>=AT && input.LA(1)<=89) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_rhs_chunk5335);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop94;
                }
            } while (true);

            loc=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_rhs_chunk5359); if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_rhs_chunk5361);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1739:1: name returns [String name] : ( ID | STRING );
    public final name_return name() throws RecognitionException {
        name_return retval = new name_return();
        retval.start = input.LT(1);

        Token ID56=null;
        Token STRING57=null;

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1740:2: ( ID | STRING )
            int alt95=2;
            int LA95_0 = input.LA(1);

            if ( (LA95_0==ID) ) {
                alt95=1;
            }
            else if ( (LA95_0==STRING) ) {
                alt95=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("1739:1: name returns [String name] : ( ID | STRING );", 95, 0, input);

                throw nvae;
            }
            switch (alt95) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1740:5: ID
                    {
                    ID56=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_name5395); if (failed) return retval;
                    if ( backtracking==0 ) {
                       retval.name = ID56.getText(); 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1741:5: STRING
                    {
                    STRING57=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_name5403); if (failed) return retval;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1744:1: identifier : ( ID | PACKAGE | FUNCTION | GLOBAL | IMPORT | EVENT | RULE | QUERY | TEMPLATE | ATTRIBUTES | ENABLED | SALIENCE | DURATION | DIALECT | FROM | INIT | ACTION | REVERSE | RESULT | WHEN | THEN | END | IN );
    public final identifier_return identifier() throws RecognitionException {
        identifier_return retval = new identifier_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1745:2: ( ID | PACKAGE | FUNCTION | GLOBAL | IMPORT | EVENT | RULE | QUERY | TEMPLATE | ATTRIBUTES | ENABLED | SALIENCE | DURATION | DIALECT | FROM | INIT | ACTION | REVERSE | RESULT | WHEN | THEN | END | IN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:
            {
            if ( (input.LA(1)>=PACKAGE && input.LA(1)<=ID)||input.LA(1)==GLOBAL||input.LA(1)==END||(input.LA(1)>=QUERY && input.LA(1)<=ATTRIBUTES)||input.LA(1)==ENABLED||input.LA(1)==SALIENCE||(input.LA(1)>=DURATION && input.LA(1)<=DIALECT)||input.LA(1)==FROM||(input.LA(1)>=INIT && input.LA(1)<=RESULT)||input.LA(1)==IN||(input.LA(1)>=THEN && input.LA(1)<=EVENT) ) {
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
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:846:6: ( EXISTS )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:846:8: EXISTS
        {
        match(input,EXISTS,FOLLOW_EXISTS_in_synpred12232); if (failed) return ;

        }
    }
    // $ANTLR end synpred1

    // $ANTLR start synpred2
    public final void synpred2_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:847:5: ( NOT )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:847:7: NOT
        {
        match(input,NOT,FOLLOW_NOT_in_synpred22250); if (failed) return ;

        }
    }
    // $ANTLR end synpred2

    // $ANTLR start synpred3
    public final void synpred3_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:848:5: ( EVAL )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:848:7: EVAL
        {
        match(input,EVAL,FOLLOW_EVAL_in_synpred32269); if (failed) return ;

        }
    }
    // $ANTLR end synpred3

    // $ANTLR start synpred4
    public final void synpred4_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:849:5: ( FORALL )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:849:7: FORALL
        {
        match(input,FORALL,FOLLOW_FORALL_in_synpred42288); if (failed) return ;

        }
    }
    // $ANTLR end synpred4

    // $ANTLR start synpred5
    public final void synpred5_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:850:5: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:850:7: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred52307); if (failed) return ;

        }
    }
    // $ANTLR end synpred5

    // $ANTLR start synpred6
    public final void synpred6_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:13: ( FROM ENTRY_POINT )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:873:14: FROM ENTRY_POINT
        {
        match(input,FROM,FOLLOW_FROM_in_synpred62464); if (failed) return ;
        match(input,ENTRY_POINT,FOLLOW_ENTRY_POINT_in_synpred62466); if (failed) return ;

        }
    }
    // $ANTLR end synpred6

    // $ANTLR start synpred9
    public final void synpred9_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1149:6: ( LEFT_SQUARE )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1149:8: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred93405); if (failed) return ;

        }
    }
    // $ANTLR end synpred9

    // $ANTLR start synpred10
    public final void synpred10_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1155:6: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1155:8: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred103438); if (failed) return ;

        }
    }
    // $ANTLR end synpred10

    // $ANTLR start synpred11
    public final void synpred11_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1479:4: ( DOUBLE_PIPE and_restr_connective[or] )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1479:4: DOUBLE_PIPE and_restr_connective[or]
        {
        match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred114241); if (failed) return ;
        pushFollow(FOLLOW_and_restr_connective_in_synpred114252);
        and_restr_connective(or);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred11

    // $ANTLR start synpred12
    public final void synpred12_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1501:5: ( DOUBLE_AMPER constraint_expression[and] )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1501:5: DOUBLE_AMPER constraint_expression[and]
        {
        match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred124304); if (failed) return ;
        pushFollow(FOLLOW_constraint_expression_in_synpred124315);
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
    protected DFA21 dfa21 = new DFA21(this);
    protected DFA58 dfa58 = new DFA58(this);
    static final String DFA8_eotS =
        "\6\uffff";
    static final String DFA8_eofS =
        "\6\uffff";
    static final String DFA8_minS =
        "\2\4\1\71\2\uffff\1\4";
    static final String DFA8_maxS =
        "\2\106\1\71\2\uffff\1\106";
    static final String DFA8_acceptS =
        "\3\uffff\1\2\1\1\1\uffff";
    static final String DFA8_specialS =
        "\6\uffff}>";
    static final String[] DFA8_transitionS = {
            "\4\1\1\uffff\1\1\4\uffff\1\1\3\uffff\5\1\3\uffff\1\1\1\uffff"+
            "\1\1\6\uffff\2\1\7\uffff\1\1\5\uffff\4\1\12\uffff\1\1\4\uffff"+
            "\2\1",
            "\6\4\1\uffff\2\3\1\uffff\1\4\3\uffff\5\4\3\uffff\1\4\1\uffff"+
            "\1\4\6\uffff\2\4\7\uffff\1\4\5\uffff\4\4\2\uffff\1\2\7\uffff"+
            "\1\4\4\uffff\2\4",
            "\1\5",
            "",
            "",
            "\4\4\1\uffff\1\4\1\uffff\2\3\1\uffff\1\4\3\uffff\5\4\3\uffff"+
            "\1\4\1\uffff\1\4\6\uffff\2\4\7\uffff\1\4\5\uffff\4\4\2\uffff"+
            "\1\2\7\uffff\1\4\4\uffff\2\4"
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
        "\2\4\1\71\2\uffff\1\4";
    static final String DFA9_maxS =
        "\2\106\1\71\2\uffff\1\106";
    static final String DFA9_acceptS =
        "\3\uffff\1\2\1\1\1\uffff";
    static final String DFA9_specialS =
        "\6\uffff}>";
    static final String[] DFA9_transitionS = {
            "\4\1\1\uffff\1\1\4\uffff\1\1\3\uffff\5\1\3\uffff\1\1\1\uffff"+
            "\1\1\6\uffff\2\1\7\uffff\1\1\5\uffff\4\1\12\uffff\1\1\4\uffff"+
            "\2\1",
            "\6\4\1\uffff\2\3\1\uffff\1\4\3\uffff\5\4\3\uffff\1\4\1\uffff"+
            "\1\4\6\uffff\2\4\7\uffff\1\4\5\uffff\4\4\2\uffff\1\2\7\uffff"+
            "\1\4\4\uffff\2\4",
            "\1\5",
            "",
            "",
            "\4\4\1\uffff\1\4\1\uffff\2\3\1\uffff\1\4\3\uffff\5\4\3\uffff"+
            "\1\4\1\uffff\1\4\6\uffff\2\4\7\uffff\1\4\5\uffff\4\4\2\uffff"+
            "\1\2\7\uffff\1\4\4\uffff\2\4"
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
    static final String DFA21_eotS =
        "\11\uffff";
    static final String DFA21_eofS =
        "\11\uffff";
    static final String DFA21_minS =
        "\2\7\1\uffff\1\7\1\uffff\1\4\1\71\2\7";
    static final String DFA21_maxS =
        "\2\60\1\uffff\1\70\1\uffff\1\106\1\71\2\70";
    static final String DFA21_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\4\uffff";
    static final String DFA21_specialS =
        "\11\uffff}>";
    static final String[] DFA21_transitionS = {
            "\1\2\2\uffff\1\1\3\uffff\1\2\36\uffff\4\2",
            "\1\3\2\uffff\1\2\1\uffff\1\4\31\uffff\1\2\1\uffff\1\2\4\uffff"+
            "\4\2",
            "",
            "\1\4\1\5\1\uffff\1\2\2\4\3\uffff\1\2\47\uffff\1\6",
            "",
            "\4\7\1\uffff\1\7\4\uffff\1\7\3\uffff\5\7\3\uffff\1\7\1\uffff"+
            "\1\7\6\uffff\2\7\7\uffff\1\7\5\uffff\4\7\12\uffff\1\7\4\uffff"+
            "\2\7",
            "\1\10",
            "\1\4\1\5\1\uffff\1\2\55\uffff\1\6",
            "\1\4\2\uffff\1\2\55\uffff\1\6"
    };

    static final short[] DFA21_eot = DFA.unpackEncodedString(DFA21_eotS);
    static final short[] DFA21_eof = DFA.unpackEncodedString(DFA21_eofS);
    static final char[] DFA21_min = DFA.unpackEncodedStringToUnsignedChars(DFA21_minS);
    static final char[] DFA21_max = DFA.unpackEncodedStringToUnsignedChars(DFA21_maxS);
    static final short[] DFA21_accept = DFA.unpackEncodedString(DFA21_acceptS);
    static final short[] DFA21_special = DFA.unpackEncodedString(DFA21_specialS);
    static final short[][] DFA21_transition;

    static {
        int numStates = DFA21_transitionS.length;
        DFA21_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA21_transition[i] = DFA.unpackEncodedString(DFA21_transitionS[i]);
        }
    }

    class DFA21 extends DFA {

        public DFA21(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 21;
            this.eot = DFA21_eot;
            this.eof = DFA21_eof;
            this.min = DFA21_min;
            this.max = DFA21_max;
            this.accept = DFA21_accept;
            this.special = DFA21_special;
            this.transition = DFA21_transition;
        }
        public String getDescription() {
            return "437:3: ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )?";
        }
    }
    static final String DFA58_eotS =
        "\150\uffff";
    static final String DFA58_eofS =
        "\150\uffff";
    static final String DFA58_minS =
        "\1\7\1\uffff\1\4\1\uffff\2\4\1\0\5\4\2\uffff\4\4\1\0\1\4\1\0\5\4"+
        "\1\0\1\4\1\0\1\4\2\0\3\4\2\0\1\4\1\0\1\4\2\0\3\4\2\0\1\4\1\0\1\4"+
        "\2\0\3\4\1\0\3\4\1\0\3\4\1\0\1\4\1\0\1\4\1\uffff\44\0";
    static final String DFA58_maxS =
        "\1\121\1\uffff\1\131\1\uffff\2\131\1\0\5\131\2\uffff\4\131\1\0\1"+
        "\131\1\0\5\131\1\0\1\131\1\0\1\131\2\0\3\131\2\0\1\131\1\0\1\131"+
        "\2\0\3\131\2\0\1\131\1\0\1\131\2\0\3\131\1\0\3\131\1\0\3\131\1\0"+
        "\1\131\1\0\1\131\1\uffff\44\0";
    static final String DFA58_acceptS =
        "\1\uffff\1\1\1\uffff\1\3\10\uffff\2\2\65\uffff\1\2\44\uffff";
    static final String DFA58_specialS =
        "\1\60\1\uffff\1\40\1\uffff\1\61\1\1\1\63\1\0\1\67\1\51\1\4\1\23"+
        "\2\uffff\1\5\1\16\1\27\1\24\1\66\1\56\1\17\1\3\1\2\1\7\1\76\1\36"+
        "\1\26\1\22\1\10\1\21\1\52\1\30\1\70\1\43\1\31\1\62\1\14\1\55\1\12"+
        "\1\54\1\20\1\65\1\50\1\53\1\57\1\47\1\11\1\35\1\41\1\37\1\25\1\42"+
        "\1\46\1\34\1\75\1\15\1\71\1\44\1\32\1\64\1\72\1\45\1\33\1\74\1\6"+
        "\1\13\1\73\45\uffff}>";
    static final String[] DFA58_transitionS = {
            "\2\3\1\uffff\1\2\2\3\1\uffff\1\3\27\uffff\4\3\3\uffff\4\3\1"+
            "\uffff\1\3\5\uffff\1\1\14\uffff\1\3\13\uffff\1\3",
            "",
            "\3\14\1\13\2\14\1\6\1\14\1\15\31\14\1\5\1\14\1\4\4\14\1\7\1"+
            "\10\1\11\1\12\51\14",
            "",
            "\3\14\1\23\2\14\1\22\1\14\1\15\40\14\1\16\1\17\1\20\1\21\51"+
            "\14",
            "\3\14\1\31\2\14\1\24\1\14\1\15\40\14\1\25\1\26\1\27\1\30\51"+
            "\14",
            "\1\uffff",
            "\3\14\1\33\2\14\1\32\1\14\1\15\115\14",
            "\3\14\1\35\2\14\1\34\1\14\1\15\115\14",
            "\6\14\1\36\1\14\1\15\115\14",
            "\6\14\1\37\1\14\1\15\115\14",
            "\4\14\1\41\1\14\1\43\1\14\1\15\3\14\1\40\47\14\1\42\41\14",
            "",
            "",
            "\3\14\1\45\2\14\1\44\1\14\1\15\115\14",
            "\3\14\1\47\2\14\1\46\1\14\1\15\115\14",
            "\6\14\1\50\1\14\1\15\115\14",
            "\6\14\1\51\1\14\1\15\115\14",
            "\1\uffff",
            "\4\14\1\53\1\14\1\55\1\14\1\15\3\14\1\52\47\14\1\54\41\14",
            "\1\uffff",
            "\3\14\1\57\2\14\1\56\1\14\1\15\115\14",
            "\3\14\1\61\2\14\1\60\1\14\1\15\115\14",
            "\6\14\1\62\1\14\1\15\115\14",
            "\6\14\1\63\1\14\1\15\115\14",
            "\4\14\1\65\1\14\1\67\1\14\1\15\3\14\1\64\47\14\1\66\41\14",
            "\1\uffff",
            "\4\14\1\71\1\14\1\73\1\14\1\15\3\14\1\70\47\14\1\72\41\14",
            "\1\uffff",
            "\4\14\1\75\1\14\1\77\1\14\1\15\3\14\1\74\47\14\1\76\41\14",
            "\1\uffff",
            "\1\uffff",
            "\3\14\1\100\2\14\1\101\1\14\1\15\115\14",
            "\4\102\1\14\1\102\1\103\1\14\1\15\1\14\1\102\3\14\5\102\3\14"+
            "\1\102\1\14\1\102\6\14\2\102\7\14\1\102\5\14\4\102\12\14\1\102"+
            "\4\14\2\102\23\14",
            "\6\14\1\103\1\14\1\15\54\14\1\104\40\14",
            "\1\uffff",
            "\1\uffff",
            "\4\14\1\106\1\14\1\110\1\14\1\15\3\14\1\105\47\14\1\107\41\14",
            "\1\uffff",
            "\4\14\1\112\1\14\1\114\1\14\1\15\3\14\1\111\47\14\1\113\41\14",
            "\1\uffff",
            "\1\uffff",
            "\3\14\1\115\2\14\1\116\1\14\1\15\115\14",
            "\4\117\1\14\1\117\1\103\1\14\1\15\1\14\1\117\3\14\5\117\3\14"+
            "\1\117\1\14\1\117\6\14\2\117\7\14\1\117\5\14\4\117\12\14\1\117"+
            "\4\14\2\117\23\14",
            "\6\14\1\103\1\14\1\15\54\14\1\120\40\14",
            "\1\uffff",
            "\1\uffff",
            "\4\14\1\122\1\14\1\124\1\14\1\15\3\14\1\121\47\14\1\123\41\14",
            "\1\uffff",
            "\4\14\1\126\1\14\1\130\1\14\1\15\3\14\1\125\47\14\1\127\41\14",
            "\1\uffff",
            "\1\uffff",
            "\3\14\1\131\2\14\1\132\1\14\1\15\115\14",
            "\4\133\1\14\1\133\1\103\1\14\1\15\1\14\1\133\3\14\5\133\3\14"+
            "\1\133\1\14\1\133\6\14\2\133\7\14\1\133\5\14\4\133\12\14\1\133"+
            "\4\14\2\133\23\14",
            "\6\14\1\103\1\14\1\15\54\14\1\134\40\14",
            "\1\uffff",
            "\3\14\1\135\2\14\1\136\1\14\1\15\115\14",
            "\4\137\1\14\1\137\1\103\1\14\1\15\1\14\1\137\3\14\5\137\3\14"+
            "\1\137\1\14\1\137\6\14\2\137\7\14\1\137\5\14\4\137\12\14\1\137"+
            "\4\14\2\137\23\14",
            "\6\14\1\103\1\14\1\15\54\14\1\140\40\14",
            "\1\uffff",
            "\3\14\1\141\2\14\1\142\1\14\1\15\115\14",
            "\4\143\1\14\1\143\1\103\1\14\1\15\1\14\1\143\3\14\5\143\3\14"+
            "\1\143\1\14\1\143\6\14\2\143\7\14\1\143\5\14\4\143\12\14\1\143"+
            "\4\14\2\143\23\14",
            "\6\14\1\103\1\14\1\15\54\14\1\144\40\14",
            "\1\uffff",
            "\4\14\1\145\1\14\1\147\1\14\1\15\53\14\1\146\41\14",
            "\1\uffff",
            "\4\14\1\41\1\14\1\43\1\14\1\15\53\14\1\42\41\14",
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

    static final short[] DFA58_eot = DFA.unpackEncodedString(DFA58_eotS);
    static final short[] DFA58_eof = DFA.unpackEncodedString(DFA58_eofS);
    static final char[] DFA58_min = DFA.unpackEncodedStringToUnsignedChars(DFA58_minS);
    static final char[] DFA58_max = DFA.unpackEncodedStringToUnsignedChars(DFA58_maxS);
    static final short[] DFA58_accept = DFA.unpackEncodedString(DFA58_acceptS);
    static final short[] DFA58_special = DFA.unpackEncodedString(DFA58_specialS);
    static final short[][] DFA58_transition;

    static {
        int numStates = DFA58_transitionS.length;
        DFA58_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA58_transition[i] = DFA.unpackEncodedString(DFA58_transitionS[i]);
        }
    }

    class DFA58 extends DFA {

        public DFA58(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 58;
            this.eot = DFA58_eot;
            this.eof = DFA58_eof;
            this.min = DFA58_min;
            this.max = DFA58_max;
            this.accept = DFA58_accept;
            this.special = DFA58_special;
            this.transition = DFA58_transition;
        }
        public String getDescription() {
            return "1148:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )?";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA58_7 = input.LA(1);

                         
                        int index58_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_7==LEFT_PAREN) ) {s = 26;}

                        else if ( (LA58_7==ID) ) {s = 27;}

                        else if ( (LA58_7==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_7>=PACKAGE && LA58_7<=FUNCTION)||(LA58_7>=DOT && LA58_7<=GLOBAL)||LA58_7==COMMA||(LA58_7>=DECLARE && LA58_7<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_7);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA58_5 = input.LA(1);

                         
                        int index58_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_5==LEFT_PAREN) ) {s = 20;}

                        else if ( (LA58_5==EXISTS) ) {s = 21;}

                        else if ( (LA58_5==NOT) ) {s = 22;}

                        else if ( (LA58_5==EVAL) ) {s = 23;}

                        else if ( (LA58_5==FORALL) ) {s = 24;}

                        else if ( (LA58_5==ID) ) {s = 25;}

                        else if ( (LA58_5==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_5>=PACKAGE && LA58_5<=FUNCTION)||(LA58_5>=DOT && LA58_5<=GLOBAL)||LA58_5==COMMA||(LA58_5>=DECLARE && LA58_5<=FROM)||(LA58_5>=ACCUMULATE && LA58_5<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_5);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA58_22 = input.LA(1);

                         
                        int index58_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_22==LEFT_PAREN) ) {s = 48;}

                        else if ( (LA58_22==ID) ) {s = 49;}

                        else if ( (LA58_22==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_22>=PACKAGE && LA58_22<=FUNCTION)||(LA58_22>=DOT && LA58_22<=GLOBAL)||LA58_22==COMMA||(LA58_22>=DECLARE && LA58_22<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_22);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA58_21 = input.LA(1);

                         
                        int index58_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_21==LEFT_PAREN) ) {s = 46;}

                        else if ( (LA58_21==ID) ) {s = 47;}

                        else if ( (LA58_21==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_21>=PACKAGE && LA58_21<=FUNCTION)||(LA58_21>=DOT && LA58_21<=GLOBAL)||LA58_21==COMMA||(LA58_21>=DECLARE && LA58_21<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_21);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA58_10 = input.LA(1);

                         
                        int index58_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_10==LEFT_PAREN) ) {s = 31;}

                        else if ( (LA58_10==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_10>=PACKAGE && LA58_10<=GLOBAL)||LA58_10==COMMA||(LA58_10>=DECLARE && LA58_10<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_10);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA58_14 = input.LA(1);

                         
                        int index58_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_14==LEFT_PAREN) ) {s = 36;}

                        else if ( (LA58_14==ID) ) {s = 37;}

                        else if ( (LA58_14==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_14>=PACKAGE && LA58_14<=FUNCTION)||(LA58_14>=DOT && LA58_14<=GLOBAL)||LA58_14==COMMA||(LA58_14>=DECLARE && LA58_14<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_14);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA58_64 = input.LA(1);

                         
                        int index58_64 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_64==DOT) ) {s = 101;}

                        else if ( (LA58_64==LEFT_SQUARE) ) {s = 102;}

                        else if ( (LA58_64==LEFT_PAREN) ) {s = 103;}

                        else if ( (LA58_64==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_64>=PACKAGE && LA58_64<=ID)||LA58_64==GLOBAL||LA58_64==COMMA||(LA58_64>=DECLARE && LA58_64<=ENTRY_POINT)||(LA58_64>=RIGHT_SQUARE && LA58_64<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_64);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA58_23 = input.LA(1);

                         
                        int index58_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_23==LEFT_PAREN) ) {s = 50;}

                        else if ( (LA58_23==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_23>=PACKAGE && LA58_23<=GLOBAL)||LA58_23==COMMA||(LA58_23>=DECLARE && LA58_23<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_23);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA58_28 = input.LA(1);

                         
                        int index58_28 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_28);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA58_46 = input.LA(1);

                         
                        int index58_46 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_46);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA58_38 = input.LA(1);

                         
                        int index58_38 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_38);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA58_65 = input.LA(1);

                         
                        int index58_65 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_65);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA58_36 = input.LA(1);

                         
                        int index58_36 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_36);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA58_55 = input.LA(1);

                         
                        int index58_55 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_55);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA58_15 = input.LA(1);

                         
                        int index58_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_15==LEFT_PAREN) ) {s = 38;}

                        else if ( (LA58_15==ID) ) {s = 39;}

                        else if ( (LA58_15==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_15>=PACKAGE && LA58_15<=FUNCTION)||(LA58_15>=DOT && LA58_15<=GLOBAL)||LA58_15==COMMA||(LA58_15>=DECLARE && LA58_15<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_15);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA58_20 = input.LA(1);

                         
                        int index58_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_20);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA58_40 = input.LA(1);

                         
                        int index58_40 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_40);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA58_29 = input.LA(1);

                         
                        int index58_29 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_29==COLON) ) {s = 60;}

                        else if ( (LA58_29==DOT) ) {s = 61;}

                        else if ( (LA58_29==LEFT_SQUARE) ) {s = 62;}

                        else if ( (LA58_29==LEFT_PAREN) ) {s = 63;}

                        else if ( (LA58_29==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_29>=PACKAGE && LA58_29<=ID)||LA58_29==GLOBAL||LA58_29==COMMA||(LA58_29>=DECLARE && LA58_29<=AT)||(LA58_29>=EQUALS && LA58_29<=ENTRY_POINT)||(LA58_29>=RIGHT_SQUARE && LA58_29<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_29);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA58_27 = input.LA(1);

                         
                        int index58_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_27==COLON) ) {s = 56;}

                        else if ( (LA58_27==DOT) ) {s = 57;}

                        else if ( (LA58_27==LEFT_SQUARE) ) {s = 58;}

                        else if ( (LA58_27==LEFT_PAREN) ) {s = 59;}

                        else if ( (LA58_27==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_27>=PACKAGE && LA58_27<=ID)||LA58_27==GLOBAL||LA58_27==COMMA||(LA58_27>=DECLARE && LA58_27<=AT)||(LA58_27>=EQUALS && LA58_27<=ENTRY_POINT)||(LA58_27>=RIGHT_SQUARE && LA58_27<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_27);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA58_11 = input.LA(1);

                         
                        int index58_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_11==COLON) ) {s = 32;}

                        else if ( (LA58_11==DOT) ) {s = 33;}

                        else if ( (LA58_11==LEFT_SQUARE) ) {s = 34;}

                        else if ( (LA58_11==LEFT_PAREN) ) {s = 35;}

                        else if ( (LA58_11==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_11>=PACKAGE && LA58_11<=ID)||LA58_11==GLOBAL||LA58_11==COMMA||(LA58_11>=DECLARE && LA58_11<=AT)||(LA58_11>=EQUALS && LA58_11<=ENTRY_POINT)||(LA58_11>=RIGHT_SQUARE && LA58_11<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_11);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA58_17 = input.LA(1);

                         
                        int index58_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_17==LEFT_PAREN) ) {s = 41;}

                        else if ( (LA58_17==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_17>=PACKAGE && LA58_17<=GLOBAL)||LA58_17==COMMA||(LA58_17>=DECLARE && LA58_17<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_17);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA58_50 = input.LA(1);

                         
                        int index58_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_50);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA58_26 = input.LA(1);

                         
                        int index58_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_26);
                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA58_16 = input.LA(1);

                         
                        int index58_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_16==LEFT_PAREN) ) {s = 40;}

                        else if ( (LA58_16==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_16>=PACKAGE && LA58_16<=GLOBAL)||LA58_16==COMMA||(LA58_16>=DECLARE && LA58_16<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_16);
                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA58_31 = input.LA(1);

                         
                        int index58_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_31);
                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA58_34 = input.LA(1);

                         
                        int index58_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_34==RIGHT_SQUARE) ) {s = 68;}

                        else if ( (LA58_34==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_34>=PACKAGE && LA58_34<=GLOBAL)||LA58_34==COMMA||(LA58_34>=DECLARE && LA58_34<=LEFT_SQUARE)||(LA58_34>=CONTAINS && LA58_34<=89)) && (synpred10())) {s = 12;}

                        else if ( (LA58_34==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index58_34);
                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA58_58 = input.LA(1);

                         
                        int index58_58 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_58==RIGHT_SQUARE) ) {s = 96;}

                        else if ( (LA58_58==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_58>=PACKAGE && LA58_58<=GLOBAL)||LA58_58==COMMA||(LA58_58>=DECLARE && LA58_58<=LEFT_SQUARE)||(LA58_58>=CONTAINS && LA58_58<=89)) && (synpred10())) {s = 12;}

                        else if ( (LA58_58==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index58_58);
                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA58_62 = input.LA(1);

                         
                        int index58_62 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_62==RIGHT_SQUARE) ) {s = 100;}

                        else if ( (LA58_62==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_62>=PACKAGE && LA58_62<=GLOBAL)||LA58_62==COMMA||(LA58_62>=DECLARE && LA58_62<=LEFT_SQUARE)||(LA58_62>=CONTAINS && LA58_62<=89)) && (synpred10())) {s = 12;}

                        else if ( (LA58_62==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index58_62);
                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA58_53 = input.LA(1);

                         
                        int index58_53 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA58_53>=PACKAGE && LA58_53<=ID)||LA58_53==GLOBAL||LA58_53==END||(LA58_53>=QUERY && LA58_53<=ATTRIBUTES)||LA58_53==ENABLED||LA58_53==SALIENCE||(LA58_53>=DURATION && LA58_53<=DIALECT)||LA58_53==FROM||(LA58_53>=INIT && LA58_53<=RESULT)||LA58_53==IN||(LA58_53>=THEN && LA58_53<=EVENT)) ) {s = 91;}

                        else if ( (LA58_53==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA58_53==DOT||LA58_53==COMMA||LA58_53==DECLARE||(LA58_53>=AT && LA58_53<=EQUALS)||(LA58_53>=DATE_EFFECTIVE && LA58_53<=DATE_EXPIRES)||LA58_53==BOOL||(LA58_53>=INT && LA58_53<=AGENDA_GROUP)||(LA58_53>=LOCK_ON_ACTIVE && LA58_53<=WINDOW)||(LA58_53>=EXISTS && LA58_53<=ACCUMULATE)||(LA58_53>=COLLECT && LA58_53<=TILDE)||(LA58_53>=FLOAT && LA58_53<=RIGHT_CURLY)||(LA58_53>=EOL && LA58_53<=89)) && (synpred10())) {s = 12;}

                        else if ( (LA58_53==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index58_53);
                        if ( s>=0 ) return s;
                        break;
                    case 29 : 
                        int LA58_47 = input.LA(1);

                         
                        int index58_47 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_47==COLON) ) {s = 81;}

                        else if ( (LA58_47==DOT) ) {s = 82;}

                        else if ( (LA58_47==LEFT_SQUARE) ) {s = 83;}

                        else if ( (LA58_47==LEFT_PAREN) ) {s = 84;}

                        else if ( (LA58_47==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_47>=PACKAGE && LA58_47<=ID)||LA58_47==GLOBAL||LA58_47==COMMA||(LA58_47>=DECLARE && LA58_47<=AT)||(LA58_47>=EQUALS && LA58_47<=ENTRY_POINT)||(LA58_47>=RIGHT_SQUARE && LA58_47<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_47);
                        if ( s>=0 ) return s;
                        break;
                    case 30 : 
                        int LA58_25 = input.LA(1);

                         
                        int index58_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_25==COLON) ) {s = 52;}

                        else if ( (LA58_25==DOT) ) {s = 53;}

                        else if ( (LA58_25==LEFT_SQUARE) ) {s = 54;}

                        else if ( (LA58_25==LEFT_PAREN) ) {s = 55;}

                        else if ( (LA58_25==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_25>=PACKAGE && LA58_25<=ID)||LA58_25==GLOBAL||LA58_25==COMMA||(LA58_25>=DECLARE && LA58_25<=AT)||(LA58_25>=EQUALS && LA58_25<=ENTRY_POINT)||(LA58_25>=RIGHT_SQUARE && LA58_25<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_25);
                        if ( s>=0 ) return s;
                        break;
                    case 31 : 
                        int LA58_49 = input.LA(1);

                         
                        int index58_49 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_49==COLON) ) {s = 85;}

                        else if ( (LA58_49==DOT) ) {s = 86;}

                        else if ( (LA58_49==LEFT_SQUARE) ) {s = 87;}

                        else if ( (LA58_49==LEFT_PAREN) ) {s = 88;}

                        else if ( (LA58_49==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_49>=PACKAGE && LA58_49<=ID)||LA58_49==GLOBAL||LA58_49==COMMA||(LA58_49>=DECLARE && LA58_49<=AT)||(LA58_49>=EQUALS && LA58_49<=ENTRY_POINT)||(LA58_49>=RIGHT_SQUARE && LA58_49<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_49);
                        if ( s>=0 ) return s;
                        break;
                    case 32 : 
                        int LA58_2 = input.LA(1);

                         
                        int index58_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_2==AND) ) {s = 4;}

                        else if ( (LA58_2==OR) ) {s = 5;}

                        else if ( (LA58_2==LEFT_PAREN) ) {s = 6;}

                        else if ( (LA58_2==EXISTS) ) {s = 7;}

                        else if ( (LA58_2==NOT) ) {s = 8;}

                        else if ( (LA58_2==EVAL) ) {s = 9;}

                        else if ( (LA58_2==FORALL) ) {s = 10;}

                        else if ( (LA58_2==ID) ) {s = 11;}

                        else if ( ((LA58_2>=PACKAGE && LA58_2<=FUNCTION)||(LA58_2>=DOT && LA58_2<=GLOBAL)||LA58_2==COMMA||(LA58_2>=DECLARE && LA58_2<=LOCK_ON_ACTIVE)||LA58_2==DOUBLE_PIPE||(LA58_2>=DOUBLE_AMPER && LA58_2<=FROM)||(LA58_2>=ACCUMULATE && LA58_2<=89)) && (synpred10())) {s = 12;}

                        else if ( (LA58_2==RIGHT_PAREN) && (synpred10())) {s = 13;}

                         
                        input.seek(index58_2);
                        if ( s>=0 ) return s;
                        break;
                    case 33 : 
                        int LA58_48 = input.LA(1);

                         
                        int index58_48 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_48);
                        if ( s>=0 ) return s;
                        break;
                    case 34 : 
                        int LA58_51 = input.LA(1);

                         
                        int index58_51 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_51);
                        if ( s>=0 ) return s;
                        break;
                    case 35 : 
                        int LA58_33 = input.LA(1);

                         
                        int index58_33 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA58_33>=PACKAGE && LA58_33<=ID)||LA58_33==GLOBAL||LA58_33==END||(LA58_33>=QUERY && LA58_33<=ATTRIBUTES)||LA58_33==ENABLED||LA58_33==SALIENCE||(LA58_33>=DURATION && LA58_33<=DIALECT)||LA58_33==FROM||(LA58_33>=INIT && LA58_33<=RESULT)||LA58_33==IN||(LA58_33>=THEN && LA58_33<=EVENT)) ) {s = 66;}

                        else if ( (LA58_33==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA58_33==DOT||LA58_33==COMMA||LA58_33==DECLARE||(LA58_33>=AT && LA58_33<=EQUALS)||(LA58_33>=DATE_EFFECTIVE && LA58_33<=DATE_EXPIRES)||LA58_33==BOOL||(LA58_33>=INT && LA58_33<=AGENDA_GROUP)||(LA58_33>=LOCK_ON_ACTIVE && LA58_33<=WINDOW)||(LA58_33>=EXISTS && LA58_33<=ACCUMULATE)||(LA58_33>=COLLECT && LA58_33<=TILDE)||(LA58_33>=FLOAT && LA58_33<=RIGHT_CURLY)||(LA58_33>=EOL && LA58_33<=89)) && (synpred10())) {s = 12;}

                        else if ( (LA58_33==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index58_33);
                        if ( s>=0 ) return s;
                        break;
                    case 36 : 
                        int LA58_57 = input.LA(1);

                         
                        int index58_57 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA58_57>=PACKAGE && LA58_57<=ID)||LA58_57==GLOBAL||LA58_57==END||(LA58_57>=QUERY && LA58_57<=ATTRIBUTES)||LA58_57==ENABLED||LA58_57==SALIENCE||(LA58_57>=DURATION && LA58_57<=DIALECT)||LA58_57==FROM||(LA58_57>=INIT && LA58_57<=RESULT)||LA58_57==IN||(LA58_57>=THEN && LA58_57<=EVENT)) ) {s = 95;}

                        else if ( (LA58_57==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA58_57==DOT||LA58_57==COMMA||LA58_57==DECLARE||(LA58_57>=AT && LA58_57<=EQUALS)||(LA58_57>=DATE_EFFECTIVE && LA58_57<=DATE_EXPIRES)||LA58_57==BOOL||(LA58_57>=INT && LA58_57<=AGENDA_GROUP)||(LA58_57>=LOCK_ON_ACTIVE && LA58_57<=WINDOW)||(LA58_57>=EXISTS && LA58_57<=ACCUMULATE)||(LA58_57>=COLLECT && LA58_57<=TILDE)||(LA58_57>=FLOAT && LA58_57<=RIGHT_CURLY)||(LA58_57>=EOL && LA58_57<=89)) && (synpred10())) {s = 12;}

                        else if ( (LA58_57==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index58_57);
                        if ( s>=0 ) return s;
                        break;
                    case 37 : 
                        int LA58_61 = input.LA(1);

                         
                        int index58_61 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA58_61>=PACKAGE && LA58_61<=ID)||LA58_61==GLOBAL||LA58_61==END||(LA58_61>=QUERY && LA58_61<=ATTRIBUTES)||LA58_61==ENABLED||LA58_61==SALIENCE||(LA58_61>=DURATION && LA58_61<=DIALECT)||LA58_61==FROM||(LA58_61>=INIT && LA58_61<=RESULT)||LA58_61==IN||(LA58_61>=THEN && LA58_61<=EVENT)) ) {s = 99;}

                        else if ( (LA58_61==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA58_61==DOT||LA58_61==COMMA||LA58_61==DECLARE||(LA58_61>=AT && LA58_61<=EQUALS)||(LA58_61>=DATE_EFFECTIVE && LA58_61<=DATE_EXPIRES)||LA58_61==BOOL||(LA58_61>=INT && LA58_61<=AGENDA_GROUP)||(LA58_61>=LOCK_ON_ACTIVE && LA58_61<=WINDOW)||(LA58_61>=EXISTS && LA58_61<=ACCUMULATE)||(LA58_61>=COLLECT && LA58_61<=TILDE)||(LA58_61>=FLOAT && LA58_61<=RIGHT_CURLY)||(LA58_61>=EOL && LA58_61<=89)) && (synpred10())) {s = 12;}

                        else if ( (LA58_61==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index58_61);
                        if ( s>=0 ) return s;
                        break;
                    case 38 : 
                        int LA58_52 = input.LA(1);

                         
                        int index58_52 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_52==ID) ) {s = 89;}

                        else if ( (LA58_52==LEFT_PAREN) ) {s = 90;}

                        else if ( (LA58_52==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_52>=PACKAGE && LA58_52<=FUNCTION)||(LA58_52>=DOT && LA58_52<=GLOBAL)||LA58_52==COMMA||(LA58_52>=DECLARE && LA58_52<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_52);
                        if ( s>=0 ) return s;
                        break;
                    case 39 : 
                        int LA58_45 = input.LA(1);

                         
                        int index58_45 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_45);
                        if ( s>=0 ) return s;
                        break;
                    case 40 : 
                        int LA58_42 = input.LA(1);

                         
                        int index58_42 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_42==ID) ) {s = 77;}

                        else if ( (LA58_42==LEFT_PAREN) ) {s = 78;}

                        else if ( (LA58_42==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_42>=PACKAGE && LA58_42<=FUNCTION)||(LA58_42>=DOT && LA58_42<=GLOBAL)||LA58_42==COMMA||(LA58_42>=DECLARE && LA58_42<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_42);
                        if ( s>=0 ) return s;
                        break;
                    case 41 : 
                        int LA58_9 = input.LA(1);

                         
                        int index58_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_9==LEFT_PAREN) ) {s = 30;}

                        else if ( (LA58_9==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_9>=PACKAGE && LA58_9<=GLOBAL)||LA58_9==COMMA||(LA58_9>=DECLARE && LA58_9<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_9);
                        if ( s>=0 ) return s;
                        break;
                    case 42 : 
                        int LA58_30 = input.LA(1);

                         
                        int index58_30 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_30);
                        if ( s>=0 ) return s;
                        break;
                    case 43 : 
                        int LA58_43 = input.LA(1);

                         
                        int index58_43 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA58_43>=PACKAGE && LA58_43<=ID)||LA58_43==GLOBAL||LA58_43==END||(LA58_43>=QUERY && LA58_43<=ATTRIBUTES)||LA58_43==ENABLED||LA58_43==SALIENCE||(LA58_43>=DURATION && LA58_43<=DIALECT)||LA58_43==FROM||(LA58_43>=INIT && LA58_43<=RESULT)||LA58_43==IN||(LA58_43>=THEN && LA58_43<=EVENT)) ) {s = 79;}

                        else if ( (LA58_43==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( (LA58_43==DOT||LA58_43==COMMA||LA58_43==DECLARE||(LA58_43>=AT && LA58_43<=EQUALS)||(LA58_43>=DATE_EFFECTIVE && LA58_43<=DATE_EXPIRES)||LA58_43==BOOL||(LA58_43>=INT && LA58_43<=AGENDA_GROUP)||(LA58_43>=LOCK_ON_ACTIVE && LA58_43<=WINDOW)||(LA58_43>=EXISTS && LA58_43<=ACCUMULATE)||(LA58_43>=COLLECT && LA58_43<=TILDE)||(LA58_43>=FLOAT && LA58_43<=RIGHT_CURLY)||(LA58_43>=EOL && LA58_43<=89)) && (synpred10())) {s = 12;}

                        else if ( (LA58_43==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index58_43);
                        if ( s>=0 ) return s;
                        break;
                    case 44 : 
                        int LA58_39 = input.LA(1);

                         
                        int index58_39 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_39==COLON) ) {s = 73;}

                        else if ( (LA58_39==DOT) ) {s = 74;}

                        else if ( (LA58_39==LEFT_SQUARE) ) {s = 75;}

                        else if ( (LA58_39==LEFT_PAREN) ) {s = 76;}

                        else if ( (LA58_39==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_39>=PACKAGE && LA58_39<=ID)||LA58_39==GLOBAL||LA58_39==COMMA||(LA58_39>=DECLARE && LA58_39<=AT)||(LA58_39>=EQUALS && LA58_39<=ENTRY_POINT)||(LA58_39>=RIGHT_SQUARE && LA58_39<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_39);
                        if ( s>=0 ) return s;
                        break;
                    case 45 : 
                        int LA58_37 = input.LA(1);

                         
                        int index58_37 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_37==COLON) ) {s = 69;}

                        else if ( (LA58_37==DOT) ) {s = 70;}

                        else if ( (LA58_37==LEFT_SQUARE) ) {s = 71;}

                        else if ( (LA58_37==LEFT_PAREN) ) {s = 72;}

                        else if ( (LA58_37==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_37>=PACKAGE && LA58_37<=ID)||LA58_37==GLOBAL||LA58_37==COMMA||(LA58_37>=DECLARE && LA58_37<=AT)||(LA58_37>=EQUALS && LA58_37<=ENTRY_POINT)||(LA58_37>=RIGHT_SQUARE && LA58_37<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_37);
                        if ( s>=0 ) return s;
                        break;
                    case 46 : 
                        int LA58_19 = input.LA(1);

                         
                        int index58_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_19==COLON) ) {s = 42;}

                        else if ( (LA58_19==DOT) ) {s = 43;}

                        else if ( (LA58_19==LEFT_SQUARE) ) {s = 44;}

                        else if ( (LA58_19==LEFT_PAREN) ) {s = 45;}

                        else if ( (LA58_19==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_19>=PACKAGE && LA58_19<=ID)||LA58_19==GLOBAL||LA58_19==COMMA||(LA58_19>=DECLARE && LA58_19<=AT)||(LA58_19>=EQUALS && LA58_19<=ENTRY_POINT)||(LA58_19>=RIGHT_SQUARE && LA58_19<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_19);
                        if ( s>=0 ) return s;
                        break;
                    case 47 : 
                        int LA58_44 = input.LA(1);

                         
                        int index58_44 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_44==RIGHT_SQUARE) ) {s = 80;}

                        else if ( (LA58_44==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_44>=PACKAGE && LA58_44<=GLOBAL)||LA58_44==COMMA||(LA58_44>=DECLARE && LA58_44<=LEFT_SQUARE)||(LA58_44>=CONTAINS && LA58_44<=89)) && (synpred10())) {s = 12;}

                        else if ( (LA58_44==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index58_44);
                        if ( s>=0 ) return s;
                        break;
                    case 48 : 
                        int LA58_0 = input.LA(1);

                         
                        int index58_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_0==LEFT_SQUARE) && (synpred9())) {s = 1;}

                        else if ( (LA58_0==LEFT_PAREN) ) {s = 2;}

                        else if ( ((LA58_0>=ID && LA58_0<=DOT)||(LA58_0>=COMMA && LA58_0<=RIGHT_PAREN)||LA58_0==END||(LA58_0>=OR && LA58_0<=DOUBLE_AMPER)||(LA58_0>=EXISTS && LA58_0<=FORALL)||LA58_0==INIT||LA58_0==THEN||LA58_0==81) ) {s = 3;}

                         
                        input.seek(index58_0);
                        if ( s>=0 ) return s;
                        break;
                    case 49 : 
                        int LA58_4 = input.LA(1);

                         
                        int index58_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_4==EXISTS) ) {s = 14;}

                        else if ( (LA58_4==NOT) ) {s = 15;}

                        else if ( (LA58_4==EVAL) ) {s = 16;}

                        else if ( (LA58_4==FORALL) ) {s = 17;}

                        else if ( (LA58_4==LEFT_PAREN) ) {s = 18;}

                        else if ( (LA58_4==ID) ) {s = 19;}

                        else if ( (LA58_4==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_4>=PACKAGE && LA58_4<=FUNCTION)||(LA58_4>=DOT && LA58_4<=GLOBAL)||LA58_4==COMMA||(LA58_4>=DECLARE && LA58_4<=FROM)||(LA58_4>=ACCUMULATE && LA58_4<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_4);
                        if ( s>=0 ) return s;
                        break;
                    case 50 : 
                        int LA58_35 = input.LA(1);

                         
                        int index58_35 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_35);
                        if ( s>=0 ) return s;
                        break;
                    case 51 : 
                        int LA58_6 = input.LA(1);

                         
                        int index58_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_6);
                        if ( s>=0 ) return s;
                        break;
                    case 52 : 
                        int LA58_59 = input.LA(1);

                         
                        int index58_59 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_59);
                        if ( s>=0 ) return s;
                        break;
                    case 53 : 
                        int LA58_41 = input.LA(1);

                         
                        int index58_41 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_41);
                        if ( s>=0 ) return s;
                        break;
                    case 54 : 
                        int LA58_18 = input.LA(1);

                         
                        int index58_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_18);
                        if ( s>=0 ) return s;
                        break;
                    case 55 : 
                        int LA58_8 = input.LA(1);

                         
                        int index58_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_8==LEFT_PAREN) ) {s = 28;}

                        else if ( (LA58_8==ID) ) {s = 29;}

                        else if ( (LA58_8==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_8>=PACKAGE && LA58_8<=FUNCTION)||(LA58_8>=DOT && LA58_8<=GLOBAL)||LA58_8==COMMA||(LA58_8>=DECLARE && LA58_8<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_8);
                        if ( s>=0 ) return s;
                        break;
                    case 56 : 
                        int LA58_32 = input.LA(1);

                         
                        int index58_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_32==ID) ) {s = 64;}

                        else if ( (LA58_32==LEFT_PAREN) ) {s = 65;}

                        else if ( (LA58_32==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_32>=PACKAGE && LA58_32<=FUNCTION)||(LA58_32>=DOT && LA58_32<=GLOBAL)||LA58_32==COMMA||(LA58_32>=DECLARE && LA58_32<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_32);
                        if ( s>=0 ) return s;
                        break;
                    case 57 : 
                        int LA58_56 = input.LA(1);

                         
                        int index58_56 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_56==ID) ) {s = 93;}

                        else if ( (LA58_56==LEFT_PAREN) ) {s = 94;}

                        else if ( (LA58_56==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_56>=PACKAGE && LA58_56<=FUNCTION)||(LA58_56>=DOT && LA58_56<=GLOBAL)||LA58_56==COMMA||(LA58_56>=DECLARE && LA58_56<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_56);
                        if ( s>=0 ) return s;
                        break;
                    case 58 : 
                        int LA58_60 = input.LA(1);

                         
                        int index58_60 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_60==ID) ) {s = 97;}

                        else if ( (LA58_60==LEFT_PAREN) ) {s = 98;}

                        else if ( (LA58_60==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_60>=PACKAGE && LA58_60<=FUNCTION)||(LA58_60>=DOT && LA58_60<=GLOBAL)||LA58_60==COMMA||(LA58_60>=DECLARE && LA58_60<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_60);
                        if ( s>=0 ) return s;
                        break;
                    case 59 : 
                        int LA58_66 = input.LA(1);

                         
                        int index58_66 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_66==LEFT_SQUARE) ) {s = 34;}

                        else if ( (LA58_66==LEFT_PAREN) ) {s = 35;}

                        else if ( (LA58_66==DOT) ) {s = 33;}

                        else if ( (LA58_66==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_66>=PACKAGE && LA58_66<=ID)||LA58_66==GLOBAL||LA58_66==COMMA||(LA58_66>=DECLARE && LA58_66<=ENTRY_POINT)||(LA58_66>=RIGHT_SQUARE && LA58_66<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_66);
                        if ( s>=0 ) return s;
                        break;
                    case 60 : 
                        int LA58_63 = input.LA(1);

                         
                        int index58_63 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_63);
                        if ( s>=0 ) return s;
                        break;
                    case 61 : 
                        int LA58_54 = input.LA(1);

                         
                        int index58_54 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_54==RIGHT_SQUARE) ) {s = 92;}

                        else if ( (LA58_54==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_54>=PACKAGE && LA58_54<=GLOBAL)||LA58_54==COMMA||(LA58_54>=DECLARE && LA58_54<=LEFT_SQUARE)||(LA58_54>=CONTAINS && LA58_54<=89)) && (synpred10())) {s = 12;}

                        else if ( (LA58_54==LEFT_PAREN) && (synpred10())) {s = 67;}

                         
                        input.seek(index58_54);
                        if ( s>=0 ) return s;
                        break;
                    case 62 : 
                        int LA58_24 = input.LA(1);

                         
                        int index58_24 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA58_24==LEFT_PAREN) ) {s = 51;}

                        else if ( (LA58_24==RIGHT_PAREN) && (synpred10())) {s = 13;}

                        else if ( ((LA58_24>=PACKAGE && LA58_24<=GLOBAL)||LA58_24==COMMA||(LA58_24>=DECLARE && LA58_24<=89)) && (synpred10())) {s = 12;}

                         
                        input.seek(index58_24);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 58, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_81_in_opt_semicolon39 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit57 = new BitSet(new long[]{0x0000003FD69C2260L});
    public static final BitSet FOLLOW_statement_in_compilation_unit62 = new BitSet(new long[]{0x0000003FD69C2260L});
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
    public static final BitSet FOLLOW_PACKAGE_in_package_statement222 = new BitSet(new long[]{0x003C1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement226 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_opt_semicolon_in_package_statement228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_statement259 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_import_name_in_import_statement282 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_opt_semicolon_in_import_statement285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_function_import_statement309 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_FUNCTION_in_function_import_statement311 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement334 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_opt_semicolon_in_function_import_statement337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_name362 = new BitSet(new long[]{0x0000000000000102L,0x0000000000040000L});
    public static final BitSet FOLLOW_DOT_in_import_name374 = new BitSet(new long[]{0x003C1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_identifier_in_import_name378 = new BitSet(new long[]{0x0000000000000102L,0x0000000000040000L});
    public static final BitSet FOLLOW_82_in_import_name402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GLOBAL_in_global436 = new BitSet(new long[]{0x003C1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_dotted_name_in_global447 = new BitSet(new long[]{0x003C1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_identifier_in_global458 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_opt_semicolon_in_global460 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function485 = new BitSet(new long[]{0x003C1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_dotted_name_in_function489 = new BitSet(new long[]{0x003C1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_identifier_in_function494 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function503 = new BitSet(new long[]{0x003C1018147C52F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_dotted_name_in_function512 = new BitSet(new long[]{0x003C1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_argument_in_function517 = new BitSet(new long[]{0x0000000000001800L});
    public static final BitSet FOLLOW_COMMA_in_function531 = new BitSet(new long[]{0x003C1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_dotted_name_in_function535 = new BitSet(new long[]{0x003C1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_argument_in_function540 = new BitSet(new long[]{0x0000000000001800L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function564 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_curly_chunk_in_function570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_argument597 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument603 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument605 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_DECLARE_in_type_declaration645 = new BitSet(new long[]{0x003C1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_identifier_in_type_declaration649 = new BitSet(new long[]{0x003C1018147CC2F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_decl_metadata_in_type_declaration702 = new BitSet(new long[]{0x003C1018147CC2F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_decl_field_in_type_declaration730 = new BitSet(new long[]{0x003C1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_END_in_type_declaration750 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_decl_metadata784 = new BitSet(new long[]{0x003C1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_identifier_in_decl_metadata788 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_decl_metadata792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_decl_field853 = new BitSet(new long[]{0x0000000000030000L});
    public static final BitSet FOLLOW_initialization_in_decl_field857 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_COLON_in_decl_field860 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_qualified_id_in_decl_field864 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_decl_metadata_in_decl_field873 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_EQUALS_in_initialization900 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_initialization904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_query938 = new BitSet(new long[]{0x0000000001000080L});
    public static final BitSet FOLLOW_name_in_query942 = new BitSet(new long[]{0x0001E00000004480L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_query952 = new BitSet(new long[]{0x0000000000001080L});
    public static final BitSet FOLLOW_qualified_id_in_query987 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ID_in_query992 = new BitSet(new long[]{0x0000000000001800L});
    public static final BitSet FOLLOW_COMMA_in_query1013 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_qualified_id_in_query1017 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ID_in_query1022 = new BitSet(new long[]{0x0000000000001800L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_query1072 = new BitSet(new long[]{0x0001E00000004480L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query1101 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_END_in_query1106 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_opt_semicolon_in_query1108 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_template1136 = new BitSet(new long[]{0x0000000001000080L});
    public static final BitSet FOLLOW_name_in_template1140 = new BitSet(new long[]{0x0000000000000080L,0x0000000000020000L});
    public static final BitSet FOLLOW_opt_semicolon_in_template1142 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_template_slot_in_template1157 = new BitSet(new long[]{0x0000000000004080L});
    public static final BitSet FOLLOW_END_in_template1172 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_opt_semicolon_in_template1174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualified_id_in_template_slot1220 = new BitSet(new long[]{0x003C1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_identifier_in_template_slot1236 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_opt_semicolon_in_template_slot1238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule1269 = new BitSet(new long[]{0x0000000001000080L});
    public static final BitSet FOLLOW_name_in_rule1273 = new BitSet(new long[]{0x0000003FD6E00000L,0x0000000000000020L});
    public static final BitSet FOLLOW_rule_attributes_in_rule1282 = new BitSet(new long[]{0x0000000000200000L,0x0000000000000020L});
    public static final BitSet FOLLOW_WHEN_in_rule1294 = new BitSet(new long[]{0x0001E00000010480L,0x0000000000000020L});
    public static final BitSet FOLLOW_COLON_in_rule1296 = new BitSet(new long[]{0x0001E00000000480L,0x0000000000000020L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule1307 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_rhs_chunk_in_rule1317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_rule_attributes1337 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_COLON_in_rule_attributes1339 = new BitSet(new long[]{0x0000003FD6800000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1347 = new BitSet(new long[]{0x0000003FD6800802L});
    public static final BitSet FOLLOW_COMMA_in_rule_attributes1354 = new BitSet(new long[]{0x0000003FD6800000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1359 = new BitSet(new long[]{0x0000003FD6800802L});
    public static final BitSet FOLLOW_salience_in_rule_attribute1396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute1404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute1413 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute1422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute1431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute1447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute1455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute1463 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_in_rule_attribute1471 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_in_rule_attribute1479 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_in_rule_attribute1486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EFFECTIVE_in_date_effective1512 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_STRING_in_date_effective1514 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EXPIRES_in_date_expires1543 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_STRING_in_date_expires1545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENABLED_in_enabled1574 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_BOOL_in_enabled1576 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_salience1609 = new BitSet(new long[]{0x0000000020000400L});
    public static final BitSet FOLLOW_INT_in_salience1620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_salience1635 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NO_LOOP_in_no_loop1665 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1678 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AUTO_FOCUS_in_auto_focus1713 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTIVATION_GROUP_in_activation_group1762 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_STRING_in_activation_group1764 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1792 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_STRING_in_ruleflow_group1794 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AGENDA_GROUP_in_agenda_group1822 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DURATION_in_duration1852 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_INT_in_duration1854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIALECT_in_dialect1882 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_STRING_in_dialect1884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1916 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_BOOL_in_lock_on_active1929 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1968 = new BitSet(new long[]{0x0001E00000000482L});
    public static final BitSet FOLLOW_lhs_or_in_lhs2005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_or2030 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_OR_in_lhs_or2032 = new BitSet(new long[]{0x0001E00000000480L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2045 = new BitSet(new long[]{0x0001E00000001480L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_or2056 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2074 = new BitSet(new long[]{0x000000C000000002L});
    public static final BitSet FOLLOW_set_in_lhs_or2082 = new BitSet(new long[]{0x0001E00000000480L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2098 = new BitSet(new long[]{0x000000C000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_and2129 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_AND_in_lhs_and2131 = new BitSet(new long[]{0x0001E00000000480L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2143 = new BitSet(new long[]{0x0001E00000001480L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_and2153 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2171 = new BitSet(new long[]{0x0000030000000002L});
    public static final BitSet FOLLOW_set_in_lhs_and2179 = new BitSet(new long[]{0x0001E00000000480L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2195 = new BitSet(new long[]{0x0000030000000002L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary2240 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary2258 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary2277 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary2296 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary2313 = new BitSet(new long[]{0x0001E00000000480L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_unary2317 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary2319 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_pattern_source_in_lhs_unary2330 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_opt_semicolon_in_lhs_unary2342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_pattern_source2369 = new BitSet(new long[]{0x0000140000000002L});
    public static final BitSet FOLLOW_WITH_in_pattern_source2384 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_WINDOW_in_pattern_source2390 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_COLON_in_pattern_source2392 = new BitSet(new long[]{0x003C1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_identifier_in_pattern_source2396 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_pattern_source2400 = new BitSet(new long[]{0x0000180000000002L});
    public static final BitSet FOLLOW_FROM_in_pattern_source2471 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_entrypoint_statement_in_pattern_source2475 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_pattern_source2495 = new BitSet(new long[]{0x007E1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_accumulate_statement_in_pattern_source2553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collect_statement_in_pattern_source2576 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_statement_in_pattern_source2613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_lhs_exist2656 = new BitSet(new long[]{0x0000000000000480L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist2676 = new BitSet(new long[]{0x0001E00000000480L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist2680 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist2710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_exist2760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_lhs_not2812 = new BitSet(new long[]{0x0000000000000480L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not2825 = new BitSet(new long[]{0x0001E00000000480L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not2829 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not2860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_not2897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_lhs_eval2943 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval2954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_lhs_forall2980 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall2982 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall2986 = new BitSet(new long[]{0x0000000000001080L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall3001 = new BitSet(new long[]{0x0000000000001080L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall3017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_pattern3050 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_pattern3058 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_source_in_from_statement3085 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_accumulate_statement3122 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement3132 = new BitSet(new long[]{0x0001E00000000480L});
    public static final BitSet FOLLOW_lhs_or_in_accumulate_statement3136 = new BitSet(new long[]{0x0004000000000880L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement3138 = new BitSet(new long[]{0x0004000000000080L});
    public static final BitSet FOLLOW_INIT_in_accumulate_statement3156 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement3169 = new BitSet(new long[]{0x0008000000000800L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement3171 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_ACTION_in_accumulate_statement3182 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement3186 = new BitSet(new long[]{0x0030000000000800L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement3188 = new BitSet(new long[]{0x0030000000000000L});
    public static final BitSet FOLLOW_REVERSE_in_accumulate_statement3201 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement3205 = new BitSet(new long[]{0x0020000000000800L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement3207 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RESULT_in_accumulate_statement3224 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement3228 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_accumulate_statement3254 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement3258 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement3275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_from_source3306 = new BitSet(new long[]{0x0000000000000502L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source3334 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_expression_chain_in_from_source3347 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_expression_chain3378 = new BitSet(new long[]{0x003C1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_identifier_in_expression_chain3382 = new BitSet(new long[]{0x0100000000000502L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain3413 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain3446 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain3461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_collect_statement3512 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement3522 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_pattern_source_in_collect_statement3526 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement3528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENTRY_POINT_in_entrypoint_statement3565 = new BitSet(new long[]{0x0000000001000080L});
    public static final BitSet FOLLOW_name_in_entrypoint_statement3577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding3609 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_COLON_in_fact_binding3611 = new BitSet(new long[]{0x0000000000000480L});
    public static final BitSet FOLLOW_fact_in_fact_binding3625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_binding3641 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_fact_in_fact_binding3645 = new BitSet(new long[]{0x000000C000001000L});
    public static final BitSet FOLLOW_set_in_fact_binding3658 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_fact_in_fact_binding3670 = new BitSet(new long[]{0x000000C000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_binding3688 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualified_id_in_fact3743 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact3753 = new BitSet(new long[]{0x013C9018147C56F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_constraints_in_fact3765 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact3772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EOF_in_fact3781 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints3800 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_behaviors_in_constraints3803 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_COMMA_in_constraints3810 = new BitSet(new long[]{0x013C9018147C46F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_constraint_in_constraints3820 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_behaviors_in_constraints3823 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_or_constr_in_constraint3856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_behaviors3871 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_behavior_in_behaviors3873 = new BitSet(new long[]{0x0200000000000800L});
    public static final BitSet FOLLOW_COMMA_in_behaviors3877 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_behavior_in_behaviors3879 = new BitSet(new long[]{0x0200000000000800L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_behaviors3884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_behavior3898 = new BitSet(new long[]{0x0000000000010402L});
    public static final BitSet FOLLOW_COLON_in_behavior3901 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ID_in_behavior3903 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_paren_chunk_in_behavior3907 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3929 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_constr3937 = new BitSet(new long[]{0x003C9018147C46F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3946 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3978 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_constr3986 = new BitSet(new long[]{0x003C9018147C46F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3995 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_field_constraint_in_unary_constr4023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_unary_constr4031 = new BitSet(new long[]{0x003C9018147C46F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_or_constr_in_unary_constr4033 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_unary_constr4036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_unary_constr4042 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_predicate_in_unary_constr4044 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_field_constraint4083 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_COLON_in_field_constraint4085 = new BitSet(new long[]{0x003C1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint4104 = new BitSet(new long[]{0xFC00400000000402L,0x0000000003F80001L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint4118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_field_constraint4133 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_predicate_in_field_constraint4135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint4161 = new BitSet(new long[]{0xFC00400000000400L,0x0000000003F00001L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint4170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective4217 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_restr_connective4241 = new BitSet(new long[]{0xFC00400000000400L,0x0000000003F00001L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective4252 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective4284 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_restr_connective4304 = new BitSet(new long[]{0xFC00400000000400L,0x0000000003F00001L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective4315 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_compound_operator_in_constraint_expression4352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_operator_in_constraint_expression4359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_constraint_expression4367 = new BitSet(new long[]{0xFC00400000000400L,0x0000000003F00001L});
    public static final BitSet FOLLOW_or_restr_connective_in_constraint_expression4376 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_constraint_expression4381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_84_in_simple_operator4412 = new BitSet(new long[]{0x003C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_85_in_simple_operator4420 = new BitSet(new long[]{0x003C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_86_in_simple_operator4428 = new BitSet(new long[]{0x003C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_87_in_simple_operator4436 = new BitSet(new long[]{0x003C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_88_in_simple_operator4444 = new BitSet(new long[]{0x003C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_89_in_simple_operator4452 = new BitSet(new long[]{0x003C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_CONTAINS_in_simple_operator4480 = new BitSet(new long[]{0x003C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_NOT_in_simple_operator4508 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_CONTAINS_in_simple_operator4512 = new BitSet(new long[]{0x003C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_EXCLUDES_in_simple_operator4540 = new BitSet(new long[]{0x003C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_MATCHES_in_simple_operator4568 = new BitSet(new long[]{0x003C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_SOUNDSLIKE_in_simple_operator4596 = new BitSet(new long[]{0x003C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_NOT_in_simple_operator4624 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_MATCHES_in_simple_operator4628 = new BitSet(new long[]{0x003C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_MEMBEROF_in_simple_operator4656 = new BitSet(new long[]{0x003C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_NOT_in_simple_operator4684 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_MEMBEROF_in_simple_operator4688 = new BitSet(new long[]{0x003C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_TILDE_in_simple_operator4694 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ID_in_simple_operator4698 = new BitSet(new long[]{0x013C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_square_chunk_in_simple_operator4702 = new BitSet(new long[]{0x003C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_NOT_in_simple_operator4711 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_TILDE_in_simple_operator4713 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ID_in_simple_operator4717 = new BitSet(new long[]{0x013C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_square_chunk_in_simple_operator4721 = new BitSet(new long[]{0x003C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_expression_value_in_simple_operator4736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_compound_operator4766 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_NOT_in_compound_operator4778 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_IN_in_compound_operator4780 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_compound_operator4795 = new BitSet(new long[]{0x003C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator4799 = new BitSet(new long[]{0x0000000000001800L});
    public static final BitSet FOLLOW_COMMA_in_compound_operator4806 = new BitSet(new long[]{0x003C10183D7C46F0L,0x0000000000000067L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator4810 = new BitSet(new long[]{0x0000000000001800L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_compound_operator4819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_expression_value4853 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_expression_value4873 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_value4887 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint4930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint4941 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint4954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint4965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal_constraint4977 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate5015 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk5033 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000003FFFFFFL});
    public static final BitSet FOLLOW_set_in_curly_chunk5037 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000003FFFFFFL});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk5046 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000003FFFFFFL});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk5051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk5065 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000003FFFFFFL});
    public static final BitSet FOLLOW_set_in_paren_chunk5069 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000003FFFFFFL});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk5078 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000003FFFFFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk5083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk5096 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000003FFFFFFL});
    public static final BitSet FOLLOW_set_in_square_chunk5100 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000003FFFFFFL});
    public static final BitSet FOLLOW_square_chunk_in_square_chunk5109 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000003FFFFFFL});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk5114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_qualified_id5143 = new BitSet(new long[]{0x0100000000000102L});
    public static final BitSet FOLLOW_DOT_in_qualified_id5149 = new BitSet(new long[]{0x003C1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_identifier_in_qualified_id5151 = new BitSet(new long[]{0x0100000000000102L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_qualified_id5160 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_qualified_id5162 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_identifier_in_dotted_name5196 = new BitSet(new long[]{0x0100000000000102L});
    public static final BitSet FOLLOW_DOT_in_dotted_name5202 = new BitSet(new long[]{0x003C1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_identifier_in_dotted_name5206 = new BitSet(new long[]{0x0100000000000102L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dotted_name5215 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dotted_name5217 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path5251 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_DOT_in_accessor_path5257 = new BitSet(new long[]{0x003C1018147C42F0L,0x0000000000000061L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path5261 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_identifier_in_accessor_element5299 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_square_chunk_in_accessor_element5306 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk5327 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000003FFFFFFL});
    public static final BitSet FOLLOW_set_in_rhs_chunk5335 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000003FFFFFFL});
    public static final BitSet FOLLOW_END_in_rhs_chunk5359 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_opt_semicolon_in_rhs_chunk5361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_name5395 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_name5403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_identifier0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_synpred12232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_synpred22250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_synpred32269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_synpred42288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred52307 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_synpred62464 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_ENTRY_POINT_in_synpred62466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred93405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred103438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred114241 = new BitSet(new long[]{0xFC00400000000400L,0x0000000003F00001L});
    public static final BitSet FOLLOW_and_restr_connective_in_synpred114252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred124304 = new BitSet(new long[]{0xFC00400000000400L,0x0000000003F00001L});
    public static final BitSet FOLLOW_constraint_expression_in_synpred124315 = new BitSet(new long[]{0x0000000000000002L});

}