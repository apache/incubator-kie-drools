// $ANTLR 3.0 /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g 2007-07-02 17:20:05

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ATTRIBUTES", "PACKAGE", "IMPORT", "FUNCTION", "ID", "DOT", "GLOBAL", "LEFT_PAREN", "COMMA", "RIGHT_PAREN", "QUERY", "END", "TEMPLATE", "RULE", "WHEN", "DATE_EFFECTIVE", "STRING", "DATE_EXPIRES", "ENABLED", "BOOL", "SALIENCE", "INT", "NO_LOOP", "AUTO_FOCUS", "ACTIVATION_GROUP", "RULEFLOW_GROUP", "AGENDA_GROUP", "DURATION", "DIALECT", "LOCK_ON_ACTIVE", "OR", "DOUBLE_PIPE", "AND", "DOUBLE_AMPER", "FROM", "EXISTS", "NOT", "EVAL", "FORALL", "ACCUMULATE", "INIT", "ACTION", "REVERSE", "RESULT", "COLLECT", "CONTAINS", "EXCLUDES", "MATCHES", "MEMBEROF", "IN", "FLOAT", "NULL", "LEFT_CURLY", "RIGHT_CURLY", "LEFT_SQUARE", "RIGHT_SQUARE", "THEN", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "MISC", "';'", "':'", "'.*'", "'->'", "'=='", "'>'", "'>='", "'<'", "'<='", "'!='"
    };
    public static final int COMMA=12;
    public static final int EXISTS=39;
    public static final int AUTO_FOCUS=27;
    public static final int END=15;
    public static final int HexDigit=64;
    public static final int FORALL=42;
    public static final int TEMPLATE=16;
    public static final int MISC=70;
    public static final int FLOAT=54;
    public static final int QUERY=14;
    public static final int THEN=60;
    public static final int RULE=17;
    public static final int INIT=44;
    public static final int IMPORT=6;
    public static final int DATE_EFFECTIVE=19;
    public static final int PACKAGE=5;
    public static final int OR=34;
    public static final int DOT=9;
    public static final int DOUBLE_PIPE=35;
    public static final int AND=36;
    public static final int FUNCTION=7;
    public static final int GLOBAL=10;
    public static final int EscapeSequence=63;
    public static final int DIALECT=32;
    public static final int INT=25;
    public static final int LOCK_ON_ACTIVE=33;
    public static final int DATE_EXPIRES=21;
    public static final int LEFT_SQUARE=58;
    public static final int CONTAINS=49;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=67;
    public static final int ATTRIBUTES=4;
    public static final int LEFT_CURLY=56;
    public static final int RESULT=47;
    public static final int ID=8;
    public static final int FROM=38;
    public static final int LEFT_PAREN=11;
    public static final int ACTIVATION_GROUP=28;
    public static final int DOUBLE_AMPER=37;
    public static final int RIGHT_CURLY=57;
    public static final int EXCLUDES=50;
    public static final int BOOL=23;
    public static final int MEMBEROF=52;
    public static final int WHEN=18;
    public static final int RULEFLOW_GROUP=29;
    public static final int WS=62;
    public static final int STRING=20;
    public static final int ACTION=45;
    public static final int COLLECT=48;
    public static final int IN=53;
    public static final int REVERSE=46;
    public static final int NO_LOOP=26;
    public static final int ACCUMULATE=43;
    public static final int UnicodeEscape=65;
    public static final int DURATION=31;
    public static final int EVAL=41;
    public static final int MATCHES=51;
    public static final int EOF=-1;
    public static final int EOL=61;
    public static final int NULL=55;
    public static final int AGENDA_GROUP=30;
    public static final int OctalEscape=66;
    public static final int SALIENCE=24;
    public static final int MULTI_LINE_COMMENT=69;
    public static final int RIGHT_PAREN=13;
    public static final int NOT=40;
    public static final int ENABLED=22;
    public static final int RIGHT_SQUARE=59;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=68;

        public DRLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[80+1];
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
    		return token.substring( 1, token.length() -1 );
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:174:1: opt_semicolon : ( ';' )? ;
    public final void opt_semicolon() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:175:4: ( ( ';' )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:175:4: ( ';' )?
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:175:4: ( ';' )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==71) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:175:4: ';'
                    {
                    match(input,71,FOLLOW_71_in_opt_semicolon39); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:178:1: compilation_unit : prolog ( statement )+ EOF ;
    public final void compilation_unit() throws RecognitionException {

        		// reset Location information
        		this.location = new Location( Location.LOCATION_UNKNOWN );
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:183:4: ( prolog ( statement )+ EOF )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:183:4: prolog ( statement )+ EOF
            {
            pushFollow(FOLLOW_prolog_in_compilation_unit57);
            prolog();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:184:3: ( statement )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>=IMPORT && LA2_0<=FUNCTION)||LA2_0==GLOBAL||LA2_0==QUERY||(LA2_0>=TEMPLATE && LA2_0<=RULE)) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:184:3: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_compilation_unit62);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:191:1: prolog : (pkgstmt= package_statement )? ( ATTRIBUTES ':' )? (a= rule_attribute ( ( ',' )? a= rule_attribute )* )? ;
    public final void prolog() throws RecognitionException {
        String pkgstmt = null;

        AttributeDescr a = null;



        		String packageName = "";
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:195:4: ( (pkgstmt= package_statement )? ( ATTRIBUTES ':' )? (a= rule_attribute ( ( ',' )? a= rule_attribute )* )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:195:4: (pkgstmt= package_statement )? ( ATTRIBUTES ':' )? (a= rule_attribute ( ( ',' )? a= rule_attribute )* )?
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:195:4: (pkgstmt= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==PACKAGE) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:195:6: pkgstmt= package_statement
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:199:3: ( ATTRIBUTES ':' )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==ATTRIBUTES) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:199:4: ATTRIBUTES ':'
                    {
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_prolog110); if (failed) return ;
                    match(input,72,FOLLOW_72_in_prolog112); if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:200:3: (a= rule_attribute ( ( ',' )? a= rule_attribute )* )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==DATE_EFFECTIVE||(LA7_0>=DATE_EXPIRES && LA7_0<=ENABLED)||LA7_0==SALIENCE||(LA7_0>=NO_LOOP && LA7_0<=LOCK_ON_ACTIVE)) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:200:5: a= rule_attribute ( ( ',' )? a= rule_attribute )*
                    {
                    pushFollow(FOLLOW_rule_attribute_in_prolog122);
                    a=rule_attribute();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                       	  	        	this.packageDescr.addAttribute( a );
                      	                
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:204:6: ( ( ',' )? a= rule_attribute )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==COMMA||LA6_0==DATE_EFFECTIVE||(LA6_0>=DATE_EXPIRES && LA6_0<=ENABLED)||LA6_0==SALIENCE||(LA6_0>=NO_LOOP && LA6_0<=LOCK_ON_ACTIVE)) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:204:14: ( ',' )? a= rule_attribute
                    	    {
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:204:14: ( ',' )?
                    	    int alt5=2;
                    	    int LA5_0 = input.LA(1);

                    	    if ( (LA5_0==COMMA) ) {
                    	        alt5=1;
                    	    }
                    	    switch (alt5) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:204:14: ','
                    	            {
                    	            match(input,COMMA,FOLLOW_COMMA_in_prolog145); if (failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_rule_attribute_in_prolog150);
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
    // $ANTLR end prolog


    // $ANTLR start package_statement
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:211:1: package_statement returns [String packageName] : PACKAGE n= dotted_name opt_semicolon ;
    public final String package_statement() throws RecognitionException {
        String packageName = null;

        dotted_name_return n = null;



        		packageName = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:216:3: ( PACKAGE n= dotted_name opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:216:3: PACKAGE n= dotted_name opt_semicolon
            {
            match(input,PACKAGE,FOLLOW_PACKAGE_in_package_statement194); if (failed) return packageName;
            pushFollow(FOLLOW_dotted_name_in_package_statement198);
            n=dotted_name();
            _fsp--;
            if (failed) return packageName;
            pushFollow(FOLLOW_opt_semicolon_in_package_statement200);
            opt_semicolon();
            _fsp--;
            if (failed) return packageName;
            if ( backtracking==0 ) {

              			packageName = input.toString(n.start,n.stop);
              		
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


    // $ANTLR start statement
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:221:1: statement : ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query );
    public final void statement() throws RecognitionException {
        FactTemplateDescr t = null;

        RuleDescr r = null;

        QueryDescr q = null;


        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:222:4: ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query )
            int alt8=7;
            switch ( input.LA(1) ) {
            case IMPORT:
                {
                int LA8_1 = input.LA(2);

                if ( (LA8_1==FUNCTION) ) {
                    alt8=1;
                }
                else if ( (LA8_1==ID) ) {
                    alt8=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("221:1: statement : ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query );", 8, 1, input);

                    throw nvae;
                }
                }
                break;
            case GLOBAL:
                {
                alt8=3;
                }
                break;
            case FUNCTION:
                {
                alt8=4;
                }
                break;
            case TEMPLATE:
                {
                alt8=5;
                }
                break;
            case RULE:
                {
                alt8=6;
                }
                break;
            case QUERY:
                {
                alt8=7;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("221:1: statement : ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query );", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:222:4: function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement214);
                    function_import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:223:4: import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement220);
                    import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:224:4: global
                    {
                    pushFollow(FOLLOW_global_in_statement226);
                    global();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:225:4: function
                    {
                    pushFollow(FOLLOW_function_in_statement232);
                    function();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:226:10: t= template
                    {
                    pushFollow(FOLLOW_template_in_statement246);
                    t=template();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       this.packageDescr.addFactTemplate( t ); 
                    }

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:227:4: r= rule
                    {
                    pushFollow(FOLLOW_rule_in_statement255);
                    r=rule();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       this.packageDescr.addRule( r ); 
                    }

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:228:4: q= query
                    {
                    pushFollow(FOLLOW_query_in_statement267);
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


    // $ANTLR start import_statement
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:233:1: import_statement : IMPORT import_name[importDecl] opt_semicolon ;
    public final void import_statement() throws RecognitionException {
        Token IMPORT1=null;


                	ImportDescr importDecl = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:237:4: ( IMPORT import_name[importDecl] opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:237:4: IMPORT import_name[importDecl] opt_semicolon
            {
            IMPORT1=(Token)input.LT(1);
            match(input,IMPORT,FOLLOW_IMPORT_in_import_statement296); if (failed) return ;
            if ( backtracking==0 ) {

              	            importDecl = factory.createImport( );
              	            importDecl.setStartCharacter( ((CommonToken)IMPORT1).getStartIndex() );
              		    if (packageDescr != null) {
              			packageDescr.addImport( importDecl );
              		    }
              	        
            }
            pushFollow(FOLLOW_import_name_in_import_statement319);
            import_name(importDecl);
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_import_statement322);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:248:1: function_import_statement : IMPORT FUNCTION import_name[importDecl] opt_semicolon ;
    public final void function_import_statement() throws RecognitionException {
        Token IMPORT2=null;


                	FunctionImportDescr importDecl = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:252:4: ( IMPORT FUNCTION import_name[importDecl] opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:252:4: IMPORT FUNCTION import_name[importDecl] opt_semicolon
            {
            IMPORT2=(Token)input.LT(1);
            match(input,IMPORT,FOLLOW_IMPORT_in_function_import_statement346); if (failed) return ;
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function_import_statement348); if (failed) return ;
            if ( backtracking==0 ) {

              	            importDecl = factory.createFunctionImport();
              	            importDecl.setStartCharacter( ((CommonToken)IMPORT2).getStartIndex() );
              		    if (packageDescr != null) {
              			packageDescr.addFunctionImport( importDecl );
              		    }
              	        
            }
            pushFollow(FOLLOW_import_name_in_function_import_statement371);
            import_name(importDecl);
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_function_import_statement374);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:264:1: import_name[ImportDescr importDecl] returns [String name] : ID ( DOT id= identifier )* (star= '.*' )? ;
    public final String import_name(ImportDescr importDecl) throws RecognitionException {
        String name = null;

        Token star=null;
        Token ID3=null;
        Token DOT4=null;
        identifier_return id = null;



        		name = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:269:3: ( ID ( DOT id= identifier )* (star= '.*' )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:269:3: ID ( DOT id= identifier )* (star= '.*' )?
            {
            ID3=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_name400); if (failed) return name;
            if ( backtracking==0 ) {
               
              		    name =ID3.getText(); 
              		    importDecl.setTarget( name );
              		    importDecl.setEndCharacter( ((CommonToken)ID3).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:275:3: ( DOT id= identifier )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==DOT) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:275:5: DOT id= identifier
            	    {
            	    DOT4=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_import_name412); if (failed) return name;
            	    pushFollow(FOLLOW_identifier_in_import_name416);
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
            	    break loop9;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:282:3: (star= '.*' )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==73) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:282:5: star= '.*'
                    {
                    star=(Token)input.LT(1);
                    match(input,73,FOLLOW_73_in_import_name440); if (failed) return name;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:292:1: global : GLOBAL type= dotted_name id= identifier opt_semicolon ;
    public final void global() throws RecognitionException {
        Token GLOBAL5=null;
        dotted_name_return type = null;

        identifier_return id = null;



        	    GlobalDescr global = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:297:3: ( GLOBAL type= dotted_name id= identifier opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:297:3: GLOBAL type= dotted_name id= identifier opt_semicolon
            {
            GLOBAL5=(Token)input.LT(1);
            match(input,GLOBAL,FOLLOW_GLOBAL_in_global474); if (failed) return ;
            if ( backtracking==0 ) {

              		    global = factory.createGlobal();
              	            global.setStartCharacter( ((CommonToken)GLOBAL5).getStartIndex() );
              		    packageDescr.addGlobal( global );
              		
            }
            pushFollow(FOLLOW_dotted_name_in_global485);
            type=dotted_name();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              		    global.setType( input.toString(type.start,type.stop) );
              		
            }
            pushFollow(FOLLOW_identifier_in_global496);
            id=identifier();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_global498);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:315:1: function : FUNCTION (retType= dotted_name )? id= identifier LEFT_PAREN ( (paramType= dotted_name )? paramName= argument ( COMMA (paramType= dotted_name )? paramName= argument )* )? RIGHT_PAREN body= curly_chunk ;
    public final void function() throws RecognitionException {
        Token FUNCTION6=null;
        dotted_name_return retType = null;

        identifier_return id = null;

        dotted_name_return paramType = null;

        String paramName = null;

        curly_chunk_return body = null;



        		FunctionDescr f = null;
        		String type = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:321:3: ( FUNCTION (retType= dotted_name )? id= identifier LEFT_PAREN ( (paramType= dotted_name )? paramName= argument ( COMMA (paramType= dotted_name )? paramName= argument )* )? RIGHT_PAREN body= curly_chunk )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:321:3: FUNCTION (retType= dotted_name )? id= identifier LEFT_PAREN ( (paramType= dotted_name )? paramName= argument ( COMMA (paramType= dotted_name )? paramName= argument )* )? RIGHT_PAREN body= curly_chunk
            {
            FUNCTION6=(Token)input.LT(1);
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function523); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:321:19: (retType= dotted_name )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( ((LA11_0>=ATTRIBUTES && LA11_0<=ID)||LA11_0==GLOBAL||(LA11_0>=QUERY && LA11_0<=WHEN)||LA11_0==ENABLED||LA11_0==SALIENCE||LA11_0==DURATION||LA11_0==FROM||(LA11_0>=INIT && LA11_0<=RESULT)||(LA11_0>=CONTAINS && LA11_0<=IN)||LA11_0==THEN) ) {
                int LA11_1 = input.LA(2);

                if ( ((LA11_1>=ATTRIBUTES && LA11_1<=GLOBAL)||(LA11_1>=QUERY && LA11_1<=WHEN)||LA11_1==ENABLED||LA11_1==SALIENCE||LA11_1==DURATION||LA11_1==FROM||(LA11_1>=INIT && LA11_1<=RESULT)||(LA11_1>=CONTAINS && LA11_1<=IN)||LA11_1==LEFT_SQUARE||LA11_1==THEN) ) {
                    alt11=1;
                }
            }
            switch (alt11) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:321:19: retType= dotted_name
                    {
                    pushFollow(FOLLOW_dotted_name_in_function527);
                    retType=dotted_name();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_identifier_in_function532);
            id=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              			//System.err.println( "function :: " + n.getText() );
              			type = retType != null ? input.toString(retType.start,retType.stop) : null;
              			f = factory.createFunction( input.toString(id.start,id.stop), type );
              			f.setLocation(offset(FUNCTION6.getLine()), FUNCTION6.getCharPositionInLine());
              	        	f.setStartCharacter( ((CommonToken)FUNCTION6).getStartIndex() );
              			packageDescr.addFunction( f );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_function541); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:331:4: ( (paramType= dotted_name )? paramName= argument ( COMMA (paramType= dotted_name )? paramName= argument )* )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( ((LA15_0>=ATTRIBUTES && LA15_0<=ID)||LA15_0==GLOBAL||(LA15_0>=QUERY && LA15_0<=WHEN)||LA15_0==ENABLED||LA15_0==SALIENCE||LA15_0==DURATION||LA15_0==FROM||(LA15_0>=INIT && LA15_0<=RESULT)||(LA15_0>=CONTAINS && LA15_0<=IN)||LA15_0==THEN) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:331:6: (paramType= dotted_name )? paramName= argument ( COMMA (paramType= dotted_name )? paramName= argument )*
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:331:15: (paramType= dotted_name )?
                    int alt12=2;
                    alt12 = dfa12.predict(input);
                    switch (alt12) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:331:15: paramType= dotted_name
                            {
                            pushFollow(FOLLOW_dotted_name_in_function550);
                            paramType=dotted_name();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_argument_in_function555);
                    paramName=argument();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      					type = paramType != null ? input.toString(paramType.start,paramType.stop) : null;
                      					f.addParameter( type, paramName );
                      				
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:336:5: ( COMMA (paramType= dotted_name )? paramName= argument )*
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==COMMA) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:336:7: COMMA (paramType= dotted_name )? paramName= argument
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_function569); if (failed) return ;
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:336:22: (paramType= dotted_name )?
                    	    int alt13=2;
                    	    alt13 = dfa13.predict(input);
                    	    switch (alt13) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:336:22: paramType= dotted_name
                    	            {
                    	            pushFollow(FOLLOW_dotted_name_in_function573);
                    	            paramType=dotted_name();
                    	            _fsp--;
                    	            if (failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_argument_in_function578);
                    	    paramName=argument();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    if ( backtracking==0 ) {

                    	      						type = paramType != null ? input.toString(paramType.start,paramType.stop) : null;
                    	      						f.addParameter( type, paramName );
                    	      					
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop14;
                        }
                    } while (true);


                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_function602); if (failed) return ;
            pushFollow(FOLLOW_curly_chunk_in_function608);
            body=curly_chunk();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              			//strip out '{','}'
              			f.setText( input.toString(body.start,body.stop).substring( 1, input.toString(body.start,body.stop).length()-1 ) );
              		
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:351:1: argument returns [String name] : id= identifier ( '[' ']' )* ;
    public final String argument() throws RecognitionException {
        String name = null;

        identifier_return id = null;



        		name = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:355:4: (id= identifier ( '[' ']' )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:355:4: id= identifier ( '[' ']' )*
            {
            pushFollow(FOLLOW_identifier_in_argument635);
            id=identifier();
            _fsp--;
            if (failed) return name;
            if ( backtracking==0 ) {
               name =input.toString(id.start,id.stop); 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:355:38: ( '[' ']' )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==LEFT_SQUARE) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:355:40: '[' ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_argument641); if (failed) return name;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_argument643); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       name += "[]";
            	    }

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
        return name;
    }
    // $ANTLR end argument


    // $ANTLR start query
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:360:1: query returns [QueryDescr query] : QUERY queryName= name ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )? normal_lhs_block[lhs] END opt_semicolon ;
    public final QueryDescr query() throws RecognitionException {
        QueryDescr query = null;

        Token paramName=null;
        Token QUERY7=null;
        Token END8=null;
        String queryName = null;

        qualified_id_return paramType = null;



        		query = null;
        		AndDescr lhs = null;
        		List params = null;
        		List types = null;		
         
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:369:3: ( QUERY queryName= name ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )? normal_lhs_block[lhs] END opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:369:3: QUERY queryName= name ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )? normal_lhs_block[lhs] END opt_semicolon
            {
            QUERY7=(Token)input.LT(1);
            match(input,QUERY,FOLLOW_QUERY_in_query673); if (failed) return query;
            pushFollow(FOLLOW_name_in_query677);
            queryName=name();
            _fsp--;
            if (failed) return query;
            if ( backtracking==0 ) {
               
              			query = factory.createQuery( queryName ); 
              			query.setLocation( offset(QUERY7.getLine()), QUERY7.getCharPositionInLine() );
              			query.setStartCharacter( ((CommonToken)QUERY7).getStartIndex() );
              			lhs = new AndDescr(); query.setLhs( lhs ); 
              			lhs.setLocation( offset(QUERY7.getLine()), QUERY7.getCharPositionInLine() );
                                      location.setType( Location.LOCATION_RULE_HEADER );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:378:3: ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )?
            int alt21=2;
            alt21 = dfa21.predict(input);
            switch (alt21) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:378:5: LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_query687); if (failed) return query;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:379:11: ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )?
                    int alt20=2;
                    int LA20_0 = input.LA(1);

                    if ( (LA20_0==ID) ) {
                        alt20=1;
                    }
                    switch (alt20) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:379:13: ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )*
                            {
                            if ( backtracking==0 ) {
                               params = new ArrayList(); types = new ArrayList();
                            }
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:381:15: ( (paramType= qualified_id )? paramName= ID )
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:381:16: (paramType= qualified_id )? paramName= ID
                            {
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:381:25: (paramType= qualified_id )?
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
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:381:25: paramType= qualified_id
                                    {
                                    pushFollow(FOLLOW_qualified_id_in_query722);
                                    paramType=qualified_id();
                                    _fsp--;
                                    if (failed) return query;

                                    }
                                    break;

                            }

                            paramName=(Token)input.LT(1);
                            match(input,ID,FOLLOW_ID_in_query727); if (failed) return query;
                            if ( backtracking==0 ) {
                               params.add( paramName.getText() ); String type = (paramType != null) ? input.toString(paramType.start,paramType.stop) : "Object"; types.add( type ); 
                            }

                            }

                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:382:15: ( COMMA (paramType= qualified_id )? paramName= ID )*
                            loop19:
                            do {
                                int alt19=2;
                                int LA19_0 = input.LA(1);

                                if ( (LA19_0==COMMA) ) {
                                    alt19=1;
                                }


                                switch (alt19) {
                            	case 1 :
                            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:382:16: COMMA (paramType= qualified_id )? paramName= ID
                            	    {
                            	    match(input,COMMA,FOLLOW_COMMA_in_query748); if (failed) return query;
                            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:382:31: (paramType= qualified_id )?
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
                            	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:382:31: paramType= qualified_id
                            	            {
                            	            pushFollow(FOLLOW_qualified_id_in_query752);
                            	            paramType=qualified_id();
                            	            _fsp--;
                            	            if (failed) return query;

                            	            }
                            	            break;

                            	    }

                            	    paramName=(Token)input.LT(1);
                            	    match(input,ID,FOLLOW_ID_in_query757); if (failed) return query;
                            	    if ( backtracking==0 ) {
                            	       params.add( paramName.getText() );  String type = (paramType != null) ? input.toString(paramType.start,paramType.stop) : "Object"; types.add( type );  
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

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_query807); if (failed) return query;

                    }
                    break;

            }

            if ( backtracking==0 ) {

                                      location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
              	        
            }
            pushFollow(FOLLOW_normal_lhs_block_in_query836);
            normal_lhs_block(lhs);
            _fsp--;
            if (failed) return query;
            END8=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_query841); if (failed) return query;
            pushFollow(FOLLOW_opt_semicolon_in_query843);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:400:1: template returns [FactTemplateDescr template] : TEMPLATE templateName= name opt_semicolon (slot= template_slot )+ END opt_semicolon ;
    public final FactTemplateDescr template() throws RecognitionException {
        FactTemplateDescr template = null;

        Token TEMPLATE9=null;
        Token END10=null;
        String templateName = null;

        FieldTemplateDescr slot = null;



        		template = null;		
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:405:3: ( TEMPLATE templateName= name opt_semicolon (slot= template_slot )+ END opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:405:3: TEMPLATE templateName= name opt_semicolon (slot= template_slot )+ END opt_semicolon
            {
            TEMPLATE9=(Token)input.LT(1);
            match(input,TEMPLATE,FOLLOW_TEMPLATE_in_template871); if (failed) return template;
            pushFollow(FOLLOW_name_in_template875);
            templateName=name();
            _fsp--;
            if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template877);
            opt_semicolon();
            _fsp--;
            if (failed) return template;
            if ( backtracking==0 ) {

              			template = new FactTemplateDescr(templateName);
              			template.setLocation( offset(TEMPLATE9.getLine()), TEMPLATE9.getCharPositionInLine() );			
              			template.setStartCharacter( ((CommonToken)TEMPLATE9).getStartIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:411:3: (slot= template_slot )+
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:412:4: slot= template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template892);
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
            match(input,END,FOLLOW_END_in_template907); if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template909);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:423:1: template_slot returns [FieldTemplateDescr field] : fieldType= qualified_id id= identifier opt_semicolon ;
    public final FieldTemplateDescr template_slot() throws RecognitionException {
        FieldTemplateDescr field = null;

        qualified_id_return fieldType = null;

        identifier_return id = null;



        		field = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:428:11: (fieldType= qualified_id id= identifier opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:428:11: fieldType= qualified_id id= identifier opt_semicolon
            {
            if ( backtracking==0 ) {

              			field = factory.createFieldTemplate();
              	         
            }
            pushFollow(FOLLOW_qualified_id_in_template_slot955);
            fieldType=qualified_id();
            _fsp--;
            if (failed) return field;
            if ( backtracking==0 ) {

              		        field.setClassType( input.toString(fieldType.start,fieldType.stop) );
              			field.setStartCharacter( ((CommonToken)((Token)fieldType.start)).getStartIndex() );
              			field.setEndCharacter( ((CommonToken)((Token)fieldType.stop)).getStopIndex() );
              		 
            }
            pushFollow(FOLLOW_identifier_in_template_slot971);
            id=identifier();
            _fsp--;
            if (failed) return field;
            pushFollow(FOLLOW_opt_semicolon_in_template_slot973);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:446:1: rule returns [RuleDescr rule] : RULE ruleName= name ( rule_attributes[$rule] )? ( WHEN ( ':' )? normal_lhs_block[lhs] )? rhs_chunk[$rule] ;
    public final RuleDescr rule() throws RecognitionException {
        RuleDescr rule = null;

        Token RULE11=null;
        Token WHEN12=null;
        String ruleName = null;



        		rule = null;
        		AndDescr lhs = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:3: ( RULE ruleName= name ( rule_attributes[$rule] )? ( WHEN ( ':' )? normal_lhs_block[lhs] )? rhs_chunk[$rule] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:3: RULE ruleName= name ( rule_attributes[$rule] )? ( WHEN ( ':' )? normal_lhs_block[lhs] )? rhs_chunk[$rule]
            {
            RULE11=(Token)input.LT(1);
            match(input,RULE,FOLLOW_RULE_in_rule1004); if (failed) return rule;
            pushFollow(FOLLOW_name_in_rule1008);
            ruleName=name();
            _fsp--;
            if (failed) return rule;
            if ( backtracking==0 ) {
               
              			location.setType( Location.LOCATION_RULE_HEADER );
              			debug( "start rule: " + ruleName );
              			rule = new RuleDescr( ruleName, null ); 
              			rule.setLocation( offset(RULE11.getLine()), RULE11.getCharPositionInLine() );
              			rule.setStartCharacter( ((CommonToken)RULE11).getStartIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:460:3: ( rule_attributes[$rule] )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==ATTRIBUTES||LA23_0==DATE_EFFECTIVE||(LA23_0>=DATE_EXPIRES && LA23_0<=ENABLED)||LA23_0==SALIENCE||(LA23_0>=NO_LOOP && LA23_0<=LOCK_ON_ACTIVE)) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:460:3: rule_attributes[$rule]
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule1017);
                    rule_attributes(rule);
                    _fsp--;
                    if (failed) return rule;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:461:3: ( WHEN ( ':' )? normal_lhs_block[lhs] )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==WHEN) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:462:4: WHEN ( ':' )? normal_lhs_block[lhs]
                    {
                    WHEN12=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_rule1029); if (failed) return rule;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:462:9: ( ':' )?
                    int alt24=2;
                    int LA24_0 = input.LA(1);

                    if ( (LA24_0==72) ) {
                        alt24=1;
                    }
                    switch (alt24) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:462:9: ':'
                            {
                            match(input,72,FOLLOW_72_in_rule1031); if (failed) return rule;

                            }
                            break;

                    }

                    if ( backtracking==0 ) {
                       
                      				this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
                      				lhs = new AndDescr(); rule.setLhs( lhs ); 
                      				lhs.setLocation( offset(WHEN12.getLine()), WHEN12.getCharPositionInLine() );
                      				lhs.setStartCharacter( ((CommonToken)WHEN12).getStartIndex() );
                      			
                    }
                    pushFollow(FOLLOW_normal_lhs_block_in_rule1042);
                    normal_lhs_block(lhs);
                    _fsp--;
                    if (failed) return rule;

                    }
                    break;

            }

            pushFollow(FOLLOW_rhs_chunk_in_rule1052);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:476:1: rule_attributes[RuleDescr rule] : ( ATTRIBUTES ':' )? attr= rule_attribute ( ( ',' )? attr= rule_attribute )* ;
    public final void rule_attributes(RuleDescr rule) throws RecognitionException {
        AttributeDescr attr = null;


        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:478:2: ( ( ATTRIBUTES ':' )? attr= rule_attribute ( ( ',' )? attr= rule_attribute )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:478:2: ( ATTRIBUTES ':' )? attr= rule_attribute ( ( ',' )? attr= rule_attribute )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:478:2: ( ATTRIBUTES ':' )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==ATTRIBUTES) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:478:4: ATTRIBUTES ':'
                    {
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_rule_attributes1072); if (failed) return ;
                    match(input,72,FOLLOW_72_in_rule_attributes1074); if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_rule_attribute_in_rule_attributes1082);
            attr=rule_attribute();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               rule.addAttribute( attr ); 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:480:2: ( ( ',' )? attr= rule_attribute )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0==COMMA||LA28_0==DATE_EFFECTIVE||(LA28_0>=DATE_EXPIRES && LA28_0<=ENABLED)||LA28_0==SALIENCE||(LA28_0>=NO_LOOP && LA28_0<=LOCK_ON_ACTIVE)) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:480:4: ( ',' )? attr= rule_attribute
            	    {
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:480:4: ( ',' )?
            	    int alt27=2;
            	    int LA27_0 = input.LA(1);

            	    if ( (LA27_0==COMMA) ) {
            	        alt27=1;
            	    }
            	    switch (alt27) {
            	        case 1 :
            	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:480:4: ','
            	            {
            	            match(input,COMMA,FOLLOW_COMMA_in_rule_attributes1089); if (failed) return ;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes1094);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:485:1: rule_attribute returns [AttributeDescr attr] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect );
    public final AttributeDescr rule_attribute() throws RecognitionException {
        AttributeDescr attr = null;

        AttributeDescr a = null;



        		attr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:492:4: (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect )
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
                    new NoViableAltException("485:1: rule_attribute returns [AttributeDescr attr] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect );", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:492:4: a= salience
                    {
                    pushFollow(FOLLOW_salience_in_rule_attribute1131);
                    a=salience();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:493:4: a= no_loop
                    {
                    pushFollow(FOLLOW_no_loop_in_rule_attribute1139);
                    a=no_loop();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:494:4: a= agenda_group
                    {
                    pushFollow(FOLLOW_agenda_group_in_rule_attribute1148);
                    a=agenda_group();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:495:4: a= duration
                    {
                    pushFollow(FOLLOW_duration_in_rule_attribute1157);
                    a=duration();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:496:4: a= activation_group
                    {
                    pushFollow(FOLLOW_activation_group_in_rule_attribute1166);
                    a=activation_group();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:497:4: a= auto_focus
                    {
                    pushFollow(FOLLOW_auto_focus_in_rule_attribute1174);
                    a=auto_focus();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:498:4: a= date_effective
                    {
                    pushFollow(FOLLOW_date_effective_in_rule_attribute1182);
                    a=date_effective();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:499:4: a= date_expires
                    {
                    pushFollow(FOLLOW_date_expires_in_rule_attribute1190);
                    a=date_expires();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:500:4: a= enabled
                    {
                    pushFollow(FOLLOW_enabled_in_rule_attribute1198);
                    a=enabled();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:501:4: a= ruleflow_group
                    {
                    pushFollow(FOLLOW_ruleflow_group_in_rule_attribute1206);
                    a=ruleflow_group();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:502:4: a= lock_on_active
                    {
                    pushFollow(FOLLOW_lock_on_active_in_rule_attribute1214);
                    a=lock_on_active();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:503:4: a= dialect
                    {
                    pushFollow(FOLLOW_dialect_in_rule_attribute1221);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:506:1: date_effective returns [AttributeDescr descr] : DATE_EFFECTIVE STRING ;
    public final AttributeDescr date_effective() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING13=null;
        Token DATE_EFFECTIVE14=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:511:3: ( DATE_EFFECTIVE STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:511:3: DATE_EFFECTIVE STRING
            {
            DATE_EFFECTIVE14=(Token)input.LT(1);
            match(input,DATE_EFFECTIVE,FOLLOW_DATE_EFFECTIVE_in_date_effective1247); if (failed) return descr;
            STRING13=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_effective1249); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:1: date_expires returns [AttributeDescr descr] : DATE_EXPIRES STRING ;
    public final AttributeDescr date_expires() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING15=null;
        Token DATE_EXPIRES16=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:525:4: ( DATE_EXPIRES STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:525:4: DATE_EXPIRES STRING
            {
            DATE_EXPIRES16=(Token)input.LT(1);
            match(input,DATE_EXPIRES,FOLLOW_DATE_EXPIRES_in_date_expires1278); if (failed) return descr;
            STRING15=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_expires1280); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:534:1: enabled returns [AttributeDescr descr] : ENABLED BOOL ;
    public final AttributeDescr enabled() throws RecognitionException {
        AttributeDescr descr = null;

        Token BOOL17=null;
        Token ENABLED18=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:538:5: ( ENABLED BOOL )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:538:5: ENABLED BOOL
            {
            ENABLED18=(Token)input.LT(1);
            match(input,ENABLED,FOLLOW_ENABLED_in_enabled1309); if (failed) return descr;
            BOOL17=(Token)input.LT(1);
            match(input,BOOL,FOLLOW_BOOL_in_enabled1311); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:547:1: salience returns [AttributeDescr descr] : SALIENCE ( INT | txt= paren_chunk ) ;
    public final AttributeDescr salience() throws RecognitionException {
        AttributeDescr descr = null;

        Token SALIENCE19=null;
        Token INT20=null;
        paren_chunk_return txt = null;



        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:552:3: ( SALIENCE ( INT | txt= paren_chunk ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:552:3: SALIENCE ( INT | txt= paren_chunk )
            {
            SALIENCE19=(Token)input.LT(1);
            match(input,SALIENCE,FOLLOW_SALIENCE_in_salience1344); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "salience" );
              			descr.setLocation( offset(SALIENCE19.getLine()), SALIENCE19.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)SALIENCE19).getStartIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:558:3: ( INT | txt= paren_chunk )
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
                    new NoViableAltException("558:3: ( INT | txt= paren_chunk )", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:558:5: INT
                    {
                    INT20=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_salience1355); if (failed) return descr;
                    if ( backtracking==0 ) {

                      			descr.setValue( INT20.getText() );
                      			descr.setEndCharacter( ((CommonToken)INT20).getStopIndex() );
                      		
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:563:5: txt= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_salience1370);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:571:1: no_loop returns [AttributeDescr descr] : NO_LOOP ( BOOL )? ;
    public final AttributeDescr no_loop() throws RecognitionException {
        AttributeDescr descr = null;

        Token NO_LOOP21=null;
        Token BOOL22=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:575:4: ( NO_LOOP ( BOOL )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:575:4: NO_LOOP ( BOOL )?
            {
            NO_LOOP21=(Token)input.LT(1);
            match(input,NO_LOOP,FOLLOW_NO_LOOP_in_no_loop1400); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "no-loop", "true" );
              			descr.setLocation( offset(NO_LOOP21.getLine()), NO_LOOP21.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)NO_LOOP21).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)NO_LOOP21).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:582:3: ( BOOL )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==BOOL) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:582:5: BOOL
                    {
                    BOOL22=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1413); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:590:1: auto_focus returns [AttributeDescr descr] : AUTO_FOCUS ( BOOL )? ;
    public final AttributeDescr auto_focus() throws RecognitionException {
        AttributeDescr descr = null;

        Token AUTO_FOCUS23=null;
        Token BOOL24=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:594:4: ( AUTO_FOCUS ( BOOL )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:594:4: AUTO_FOCUS ( BOOL )?
            {
            AUTO_FOCUS23=(Token)input.LT(1);
            match(input,AUTO_FOCUS,FOLLOW_AUTO_FOCUS_in_auto_focus1448); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "auto-focus", "true" );
              			descr.setLocation( offset(AUTO_FOCUS23.getLine()), AUTO_FOCUS23.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)AUTO_FOCUS23).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)AUTO_FOCUS23).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:601:3: ( BOOL )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==BOOL) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:601:5: BOOL
                    {
                    BOOL24=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1461); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:609:1: activation_group returns [AttributeDescr descr] : ACTIVATION_GROUP STRING ;
    public final AttributeDescr activation_group() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING25=null;
        Token ACTIVATION_GROUP26=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:613:4: ( ACTIVATION_GROUP STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:613:4: ACTIVATION_GROUP STRING
            {
            ACTIVATION_GROUP26=(Token)input.LT(1);
            match(input,ACTIVATION_GROUP,FOLLOW_ACTIVATION_GROUP_in_activation_group1497); if (failed) return descr;
            STRING25=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1499); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:622:1: ruleflow_group returns [AttributeDescr descr] : RULEFLOW_GROUP STRING ;
    public final AttributeDescr ruleflow_group() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING27=null;
        Token RULEFLOW_GROUP28=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:626:4: ( RULEFLOW_GROUP STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:626:4: RULEFLOW_GROUP STRING
            {
            RULEFLOW_GROUP28=(Token)input.LT(1);
            match(input,RULEFLOW_GROUP,FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1527); if (failed) return descr;
            STRING27=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_ruleflow_group1529); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:635:1: agenda_group returns [AttributeDescr descr] : AGENDA_GROUP STRING ;
    public final AttributeDescr agenda_group() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING29=null;
        Token AGENDA_GROUP30=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:639:4: ( AGENDA_GROUP STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:639:4: AGENDA_GROUP STRING
            {
            AGENDA_GROUP30=(Token)input.LT(1);
            match(input,AGENDA_GROUP,FOLLOW_AGENDA_GROUP_in_agenda_group1557); if (failed) return descr;
            STRING29=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1559); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:648:1: duration returns [AttributeDescr descr] : DURATION INT ;
    public final AttributeDescr duration() throws RecognitionException {
        AttributeDescr descr = null;

        Token INT31=null;
        Token DURATION32=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:652:4: ( DURATION INT )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:652:4: DURATION INT
            {
            DURATION32=(Token)input.LT(1);
            match(input,DURATION,FOLLOW_DURATION_in_duration1587); if (failed) return descr;
            INT31=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1589); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:661:1: dialect returns [AttributeDescr descr] : DIALECT STRING ;
    public final AttributeDescr dialect() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING33=null;
        Token DIALECT34=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:665:4: ( DIALECT STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:665:4: DIALECT STRING
            {
            DIALECT34=(Token)input.LT(1);
            match(input,DIALECT,FOLLOW_DIALECT_in_dialect1617); if (failed) return descr;
            STRING33=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_dialect1619); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:674:1: lock_on_active returns [AttributeDescr descr] : LOCK_ON_ACTIVE ( BOOL )? ;
    public final AttributeDescr lock_on_active() throws RecognitionException {
        AttributeDescr descr = null;

        Token LOCK_ON_ACTIVE35=null;
        Token BOOL36=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:678:4: ( LOCK_ON_ACTIVE ( BOOL )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:678:4: LOCK_ON_ACTIVE ( BOOL )?
            {
            LOCK_ON_ACTIVE35=(Token)input.LT(1);
            match(input,LOCK_ON_ACTIVE,FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1651); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "lock-on-active", "true" );
              			descr.setLocation( offset(LOCK_ON_ACTIVE35.getLine()), LOCK_ON_ACTIVE35.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)LOCK_ON_ACTIVE35).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)LOCK_ON_ACTIVE35).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:685:3: ( BOOL )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==BOOL) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:685:5: BOOL
                    {
                    BOOL36=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_lock_on_active1664); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:693:1: normal_lhs_block[AndDescr descr] : (d= lhs[$descr] )* ;
    public final void normal_lhs_block(AndDescr descr) throws RecognitionException {
        BaseDescr d = null;



        		location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:698:3: ( (d= lhs[$descr] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:698:3: (d= lhs[$descr] )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:698:3: (d= lhs[$descr] )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==ID||LA34_0==LEFT_PAREN||(LA34_0>=EXISTS && LA34_0<=FORALL)) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:698:5: d= lhs[$descr]
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1703);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:704:1: lhs[ConditionalElementDescr ce] returns [BaseDescr d] : l= lhs_or ;
    public final BaseDescr lhs(ConditionalElementDescr ce) throws RecognitionException {
        BaseDescr d = null;

        BaseDescr l = null;



        		d =null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:708:4: (l= lhs_or )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:708:4: l= lhs_or
            {
            pushFollow(FOLLOW_lhs_or_in_lhs1740);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:712:1: lhs_or returns [BaseDescr d] : ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* );
    public final BaseDescr lhs_or() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr lhsand = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d = null;
        		OrDescr or = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:717:4: ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* )
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==LEFT_PAREN) ) {
                int LA37_1 = input.LA(2);

                if ( (LA37_1==OR) ) {
                    alt37=1;
                }
                else if ( (LA37_1==ID||LA37_1==LEFT_PAREN||LA37_1==AND||(LA37_1>=EXISTS && LA37_1<=FORALL)) ) {
                    alt37=2;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("712:1: lhs_or returns [BaseDescr d] : ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* );", 37, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA37_0==ID||(LA37_0>=EXISTS && LA37_0<=FORALL)) ) {
                alt37=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("712:1: lhs_or returns [BaseDescr d] : ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* );", 37, 0, input);

                throw nvae;
            }
            switch (alt37) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:717:4: LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_or1765); if (failed) return d;
                    match(input,OR,FOLLOW_OR_in_lhs_or1767); if (failed) return d;
                    if ( backtracking==0 ) {

                      			or = new OrDescr();
                      			d = or;
                      			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                      		
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:723:9: (lhsand= lhs_and )+
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
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:723:9: lhsand= lhs_and
                    	    {
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or1778);
                    	    lhsand=lhs_and();
                    	    _fsp--;
                    	    if (failed) return d;

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

                    if ( backtracking==0 ) {

                      			or.addDescr( lhsand );
                      		
                    }
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_or1788); if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:729:10: left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )*
                    {
                    pushFollow(FOLLOW_lhs_and_in_lhs_or1806);
                    left=lhs_and();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = left; 
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:730:3: ( ( OR | DOUBLE_PIPE ) right= lhs_and )*
                    loop36:
                    do {
                        int alt36=2;
                        int LA36_0 = input.LA(1);

                        if ( ((LA36_0>=OR && LA36_0<=DOUBLE_PIPE)) ) {
                            alt36=1;
                        }


                        switch (alt36) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:730:5: ( OR | DOUBLE_PIPE ) right= lhs_and
                    	    {
                    	    if ( (input.LA(1)>=OR && input.LA(1)<=DOUBLE_PIPE) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return d;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or1814);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {

                    	      				location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                    	      			
                    	    }
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or1830);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:747:1: lhs_and returns [BaseDescr d] : ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* );
    public final BaseDescr lhs_and() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr lhsunary = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d = null;
        		AndDescr and = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:752:4: ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* )
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
                        new NoViableAltException("747:1: lhs_and returns [BaseDescr d] : ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* );", 40, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA40_0==ID||(LA40_0>=EXISTS && LA40_0<=FORALL)) ) {
                alt40=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("747:1: lhs_and returns [BaseDescr d] : ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* );", 40, 0, input);

                throw nvae;
            }
            switch (alt40) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:752:4: LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_and1861); if (failed) return d;
                    match(input,AND,FOLLOW_AND_in_lhs_and1863); if (failed) return d;
                    if ( backtracking==0 ) {

                      			and = new AndDescr();
                      			d = and;
                      			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                      		
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:758:11: (lhsunary= lhs_unary )+
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
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:758:11: lhsunary= lhs_unary
                    	    {
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and1874);
                    	    lhsunary=lhs_unary();
                    	    _fsp--;
                    	    if (failed) return d;

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

                    if ( backtracking==0 ) {

                      			and.addDescr( lhsunary );
                      		
                    }
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_and1884); if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:764:10: left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )*
                    {
                    pushFollow(FOLLOW_lhs_unary_in_lhs_and1902);
                    left=lhs_unary();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = left; 
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:765:3: ( ( AND | DOUBLE_AMPER ) right= lhs_unary )*
                    loop39:
                    do {
                        int alt39=2;
                        int LA39_0 = input.LA(1);

                        if ( ((LA39_0>=AND && LA39_0<=DOUBLE_AMPER)) ) {
                            alt39=1;
                        }


                        switch (alt39) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:765:5: ( AND | DOUBLE_AMPER ) right= lhs_unary
                    	    {
                    	    if ( (input.LA(1)>=AND && input.LA(1)<=DOUBLE_AMPER) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return d;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and1910);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {

                    	      				location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                    	      			
                    	    }
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and1926);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:782:1: lhs_unary returns [BaseDescr d] : ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source ) opt_semicolon ;
    public final BaseDescr lhs_unary() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr u = null;

        BaseDescr ps = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:786:4: ( ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source ) opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:786:4: ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source ) opt_semicolon
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:786:4: ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source )
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
                    new NoViableAltException("786:4: ( ( EXISTS )=>u= lhs_exist | ( NOT )=>u= lhs_not | ( EVAL )=>u= lhs_eval | ( FORALL )=>u= lhs_forall | ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN | ps= pattern_source )", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:786:6: ( EXISTS )=>u= lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary1971);
                    u=lhs_exist();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:787:5: ( NOT )=>u= lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary1989);
                    u=lhs_not();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:788:5: ( EVAL )=>u= lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary2008);
                    u=lhs_eval();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:789:5: ( FORALL )=>u= lhs_forall
                    {
                    pushFollow(FOLLOW_lhs_forall_in_lhs_unary2027);
                    u=lhs_forall();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:790:5: ( LEFT_PAREN )=> LEFT_PAREN u= lhs_or RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary2044); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_unary2048);
                    u=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary2050); if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:791:5: ps= pattern_source
                    {
                    pushFollow(FOLLOW_pattern_source_in_lhs_unary2061);
                    ps=pattern_source();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = (BaseDescr) ps; 
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_opt_semicolon_in_lhs_unary2073);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:796:1: pattern_source returns [BaseDescr d] : u= lhs_pattern ( FROM ( options {k=1; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )? ;
    public final BaseDescr pattern_source() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr u = null;

        AccumulateDescr ac = null;

        CollectDescr cs = null;

        FromDescr fm = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:801:3: (u= lhs_pattern ( FROM ( options {k=1; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:801:3: u= lhs_pattern ( FROM ( options {k=1; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )?
            {
            pushFollow(FOLLOW_lhs_pattern_in_pattern_source2100);
            u=lhs_pattern();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               d = u; 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:802:3: ( FROM ( options {k=1; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )?
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==FROM) ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:803:4: FROM ( options {k=1; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) )
                    {
                    match(input,FROM,FOLLOW_FROM_in_pattern_source2112); if (failed) return d;
                    if ( backtracking==0 ) {

                      				location.setType(Location.LOCATION_LHS_FROM);
                      				location.setProperty(Location.LOCATION_FROM_CONTENT, "");
                      		        
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:808:11: ( options {k=1; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) )
                    int alt42=3;
                    switch ( input.LA(1) ) {
                    case ACCUMULATE:
                        {
                        alt42=1;
                        }
                        break;
                    case COLLECT:
                        {
                        alt42=2;
                        }
                        break;
                    case ATTRIBUTES:
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
                    case ENABLED:
                    case SALIENCE:
                    case DURATION:
                    case FROM:
                    case INIT:
                    case ACTION:
                    case REVERSE:
                    case RESULT:
                    case CONTAINS:
                    case EXCLUDES:
                    case MATCHES:
                    case MEMBEROF:
                    case IN:
                    case THEN:
                        {
                        alt42=3;
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return d;}
                        NoViableAltException nvae =
                            new NoViableAltException("808:11: ( options {k=1; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) )", 42, 0, input);

                        throw nvae;
                    }

                    switch (alt42) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:809:15: (ac= accumulate_statement )
                            {
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:809:15: (ac= accumulate_statement )
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:809:17: ac= accumulate_statement
                            {
                            pushFollow(FOLLOW_accumulate_statement_in_pattern_source2168);
                            ac=accumulate_statement();
                            _fsp--;
                            if (failed) return d;
                            if ( backtracking==0 ) {
                               ((PatternDescr)d).setSource((PatternSourceDescr) ac); 
                            }

                            }


                            }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:810:15: (cs= collect_statement )
                            {
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:810:15: (cs= collect_statement )
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:810:17: cs= collect_statement
                            {
                            pushFollow(FOLLOW_collect_statement_in_pattern_source2191);
                            cs=collect_statement();
                            _fsp--;
                            if (failed) return d;
                            if ( backtracking==0 ) {
                               ((PatternDescr)d).setSource((PatternSourceDescr) cs); 
                            }

                            }


                            }
                            break;
                        case 3 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:811:15: (fm= from_statement )
                            {
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:811:15: (fm= from_statement )
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:811:17: fm= from_statement
                            {
                            pushFollow(FOLLOW_from_statement_in_pattern_source2215);
                            fm=from_statement();
                            _fsp--;
                            if (failed) return d;
                            if ( backtracking==0 ) {
                               ((PatternDescr)d).setSource((PatternSourceDescr) fm); 
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:816:1: lhs_exist returns [BaseDescr d] : EXISTS ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) ;
    public final BaseDescr lhs_exist() throws RecognitionException {
        BaseDescr d = null;

        Token EXISTS37=null;
        Token RIGHT_PAREN38=null;
        BaseDescr or = null;

        BaseDescr pattern = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:820:4: ( EXISTS ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:820:4: EXISTS ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
            {
            EXISTS37=(Token)input.LT(1);
            match(input,EXISTS,FOLLOW_EXISTS_in_lhs_exist2258); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new ExistsDescr( ); 
              			d.setLocation( offset(EXISTS37.getLine()), EXISTS37.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)EXISTS37).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:827:10: ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==LEFT_PAREN) ) {
                alt44=1;
            }
            else if ( (LA44_0==ID) ) {
                alt44=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("827:10: ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )", 44, 0, input);

                throw nvae;
            }
            switch (alt44) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:827:12: ( LEFT_PAREN or= lhs_or RIGHT_PAREN )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:827:12: ( LEFT_PAREN or= lhs_or RIGHT_PAREN )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:827:14: LEFT_PAREN or= lhs_or RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist2278); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist2282);
                    or=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( or != null ) ((ExistsDescr)d).addDescr( or ); 
                    }
                    RIGHT_PAREN38=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist2312); if (failed) return d;
                    if ( backtracking==0 ) {
                       d.setEndCharacter( ((CommonToken)RIGHT_PAREN38).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:832:12: pattern= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_exist2362);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:842:1: lhs_not returns [NotDescr d] : NOT ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) ;
    public final NotDescr lhs_not() throws RecognitionException {
        NotDescr d = null;

        Token NOT39=null;
        Token RIGHT_PAREN40=null;
        BaseDescr or = null;

        BaseDescr pattern = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:846:4: ( NOT ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:846:4: NOT ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
            {
            NOT39=(Token)input.LT(1);
            match(input,NOT,FOLLOW_NOT_in_lhs_not2414); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new NotDescr( ); 
              			d.setLocation( offset(NOT39.getLine()), NOT39.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)NOT39).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_NOT );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:853:3: ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==LEFT_PAREN) ) {
                alt45=1;
            }
            else if ( (LA45_0==ID) ) {
                alt45=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("853:3: ( ( LEFT_PAREN or= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )", 45, 0, input);

                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:853:5: ( LEFT_PAREN or= lhs_or RIGHT_PAREN )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:853:5: ( LEFT_PAREN or= lhs_or RIGHT_PAREN )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:853:7: LEFT_PAREN or= lhs_or RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not2427); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_not2431);
                    or=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( or != null ) d.addDescr( or ); 
                    }
                    RIGHT_PAREN40=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not2462); if (failed) return d;
                    if ( backtracking==0 ) {
                       d.setEndCharacter( ((CommonToken)RIGHT_PAREN40).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:859:3: pattern= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_not2499);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:869:1: lhs_eval returns [BaseDescr d] : EVAL c= paren_chunk ;
    public final BaseDescr lhs_eval() throws RecognitionException {
        BaseDescr d = null;

        Token EVAL41=null;
        paren_chunk_return c = null;



        		d = new EvalDescr( );
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:874:3: ( EVAL c= paren_chunk )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:874:3: EVAL c= paren_chunk
            {
            EVAL41=(Token)input.LT(1);
            match(input,EVAL,FOLLOW_EVAL_in_lhs_eval2545); if (failed) return d;
            if ( backtracking==0 ) {

              			location.setType( Location.LOCATION_LHS_INSIDE_EVAL );
              		
            }
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval2556);
            c=paren_chunk();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setStartCharacter( ((CommonToken)EVAL41).getStartIndex() );
              		        if( input.toString(c.start,c.stop) != null ) {
              	  		    this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
              		            String body = input.toString(c.start,c.stop).length() > 1 ? input.toString(c.start,c.stop).substring(1, input.toString(c.start,c.stop).length()-1) : "";
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:894:1: lhs_forall returns [ForallDescr d] : FORALL LEFT_PAREN base= lhs_pattern ( ( COMMA )? pattern= lhs_pattern )+ RIGHT_PAREN ;
    public final ForallDescr lhs_forall() throws RecognitionException {
        ForallDescr d = null;

        Token FORALL42=null;
        Token RIGHT_PAREN43=null;
        BaseDescr base = null;

        BaseDescr pattern = null;



        		d = factory.createForall();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:898:4: ( FORALL LEFT_PAREN base= lhs_pattern ( ( COMMA )? pattern= lhs_pattern )+ RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:898:4: FORALL LEFT_PAREN base= lhs_pattern ( ( COMMA )? pattern= lhs_pattern )+ RIGHT_PAREN
            {
            FORALL42=(Token)input.LT(1);
            match(input,FORALL,FOLLOW_FORALL_in_lhs_forall2582); if (failed) return d;
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_forall2584); if (failed) return d;
            pushFollow(FOLLOW_lhs_pattern_in_lhs_forall2588);
            base=lhs_pattern();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {

              			d.setStartCharacter( ((CommonToken)FORALL42).getStartIndex() );
              		        // adding the base pattern
              		        d.addDescr( base );
              			d.setLocation( offset(FORALL42.getLine()), FORALL42.getCharPositionInLine() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:905:3: ( ( COMMA )? pattern= lhs_pattern )+
            int cnt47=0;
            loop47:
            do {
                int alt47=2;
                int LA47_0 = input.LA(1);

                if ( (LA47_0==ID||LA47_0==COMMA) ) {
                    alt47=1;
                }


                switch (alt47) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:905:5: ( COMMA )? pattern= lhs_pattern
            	    {
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:905:5: ( COMMA )?
            	    int alt46=2;
            	    int LA46_0 = input.LA(1);

            	    if ( (LA46_0==COMMA) ) {
            	        alt46=1;
            	    }
            	    switch (alt46) {
            	        case 1 :
            	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:905:5: COMMA
            	            {
            	            match(input,COMMA,FOLLOW_COMMA_in_lhs_forall2601); if (failed) return d;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_lhs_pattern_in_lhs_forall2606);
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
            	    if ( cnt47 >= 1 ) break loop47;
            	    if (backtracking>0) {failed=true; return d;}
                        EarlyExitException eee =
                            new EarlyExitException(47, input);
                        throw eee;
                }
                cnt47++;
            } while (true);

            RIGHT_PAREN43=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_forall2619); if (failed) return d;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:917:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact[null] );
    public final BaseDescr lhs_pattern() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr f = null;



        		d =null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:924:4: (f= fact_binding | f= fact[null] )
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==ID) ) {
                int LA48_1 = input.LA(2);

                if ( (LA48_1==72) ) {
                    alt48=1;
                }
                else if ( (LA48_1==DOT||LA48_1==LEFT_PAREN||LA48_1==LEFT_SQUARE) ) {
                    alt48=2;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("917:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact[null] );", 48, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("917:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact[null] );", 48, 0, input);

                throw nvae;
            }
            switch (alt48) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:924:4: f= fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_pattern2652);
                    f=fact_binding();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:925:4: f= fact[null]
                    {
                    pushFollow(FOLLOW_fact_in_lhs_pattern2660);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:928:1: from_statement returns [FromDescr d] : ds= from_source[$d] ;
    public final FromDescr from_statement() throws RecognitionException {
        FromDescr d = null;

        DeclarativeInvokerDescr ds = null;



        		d =factory.createFrom();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:933:2: (ds= from_source[$d] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:933:2: ds= from_source[$d]
            {
            pushFollow(FOLLOW_from_source_in_from_statement2687);
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


    // $ANTLR start from_source
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:939:1: from_source[FromDescr from] returns [DeclarativeInvokerDescr ds] : ident= identifier ( options {k=1; } : args= paren_chunk )? ( expression_chain[$from, ad] )? ;
    public final DeclarativeInvokerDescr from_source(FromDescr from) throws RecognitionException {
        DeclarativeInvokerDescr ds = null;

        identifier_return ident = null;

        paren_chunk_return args = null;



        		ds = null;
        		AccessorDescr ad = null;
        		FunctionCallDescr fc = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:945:4: (ident= identifier ( options {k=1; } : args= paren_chunk )? ( expression_chain[$from, ad] )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:945:4: ident= identifier ( options {k=1; } : args= paren_chunk )? ( expression_chain[$from, ad] )?
            {
            pushFollow(FOLLOW_identifier_in_from_source2716);
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:954:3: ( options {k=1; } : args= paren_chunk )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==LEFT_PAREN) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:961:5: args= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_from_source2744);
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

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:975:3: ( expression_chain[$from, ad] )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==DOT) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:975:3: expression_chain[$from, ad]
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source2757);
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


    // $ANTLR start accumulate_statement
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:987:1: accumulate_statement returns [AccumulateDescr d] : ACCUMULATE LEFT_PAREN pattern= pattern_source ( COMMA )? ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) ) RIGHT_PAREN ;
    public final AccumulateDescr accumulate_statement() throws RecognitionException {
        AccumulateDescr d = null;

        Token id=null;
        Token ACCUMULATE44=null;
        Token RIGHT_PAREN45=null;
        BaseDescr pattern = null;

        paren_chunk_return text = null;



        		d = factory.createAccumulate();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:992:10: ( ACCUMULATE LEFT_PAREN pattern= pattern_source ( COMMA )? ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) ) RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:992:10: ACCUMULATE LEFT_PAREN pattern= pattern_source ( COMMA )? ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) ) RIGHT_PAREN
            {
            ACCUMULATE44=(Token)input.LT(1);
            match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_accumulate_statement2798); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(ACCUMULATE44.getLine()), ACCUMULATE44.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)ACCUMULATE44).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement2808); if (failed) return d;
            pushFollow(FOLLOW_pattern_source_in_accumulate_statement2812);
            pattern=pattern_source();
            _fsp--;
            if (failed) return d;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:998:37: ( COMMA )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==COMMA) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:998:37: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2814); if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {

              		        d.setInputPattern( (PatternDescr) pattern );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1002:3: ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) )
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==INIT) ) {
                alt56=1;
            }
            else if ( (LA56_0==ID) ) {
                alt56=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1002:3: ( ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk ) | (id= ID text= paren_chunk ) )", 56, 0, input);

                throw nvae;
            }
            switch (alt56) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1002:5: ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1002:5: ( INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1003:4: INIT text= paren_chunk ( COMMA )? ACTION text= paren_chunk ( COMMA )? ( REVERSE text= paren_chunk ( COMMA )? )? RESULT text= paren_chunk
                    {
                    match(input,INIT,FOLLOW_INIT_in_accumulate_statement2832); if (failed) return d;
                    if ( backtracking==0 ) {

                      				location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE_INIT );
                      			
                    }
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2845);
                    text=paren_chunk();
                    _fsp--;
                    if (failed) return d;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1007:21: ( COMMA )?
                    int alt52=2;
                    int LA52_0 = input.LA(1);

                    if ( (LA52_0==COMMA) ) {
                        alt52=1;
                    }
                    switch (alt52) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1007:21: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2847); if (failed) return d;

                            }
                            break;

                    }

                    if ( backtracking==0 ) {

                      				if( input.toString(text.start,text.stop) != null ) {
                      				        d.setInitCode( input.toString(text.start,text.stop).substring(1, input.toString(text.start,text.stop).length()-1) );
                      					location.setProperty(Location.LOCATION_PROPERTY_FROM_ACCUMULATE_INIT_CONTENT, d.getInitCode());
                      					location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION );
                      				}
                      			
                    }
                    match(input,ACTION,FOLLOW_ACTION_in_accumulate_statement2858); if (failed) return d;
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2862);
                    text=paren_chunk();
                    _fsp--;
                    if (failed) return d;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1015:28: ( COMMA )?
                    int alt53=2;
                    int LA53_0 = input.LA(1);

                    if ( (LA53_0==COMMA) ) {
                        alt53=1;
                    }
                    switch (alt53) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1015:28: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2864); if (failed) return d;

                            }
                            break;

                    }

                    if ( backtracking==0 ) {

                      				if( input.toString(text.start,text.stop) != null ) {
                      				        d.setActionCode( input.toString(text.start,text.stop).substring(1, input.toString(text.start,text.stop).length()-1) );
                      	       				location.setProperty(Location.LOCATION_PROPERTY_FROM_ACCUMULATE_ACTION_CONTENT, d.getActionCode());
                      					location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE );
                      				}
                      			
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1023:4: ( REVERSE text= paren_chunk ( COMMA )? )?
                    int alt55=2;
                    int LA55_0 = input.LA(1);

                    if ( (LA55_0==REVERSE) ) {
                        alt55=1;
                    }
                    switch (alt55) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1023:6: REVERSE text= paren_chunk ( COMMA )?
                            {
                            match(input,REVERSE,FOLLOW_REVERSE_in_accumulate_statement2877); if (failed) return d;
                            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2881);
                            text=paren_chunk();
                            _fsp--;
                            if (failed) return d;
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1023:31: ( COMMA )?
                            int alt54=2;
                            int LA54_0 = input.LA(1);

                            if ( (LA54_0==COMMA) ) {
                                alt54=1;
                            }
                            switch (alt54) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1023:31: COMMA
                                    {
                                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2883); if (failed) return d;

                                    }
                                    break;

                            }

                            if ( backtracking==0 ) {

                              				if( input.toString(text.start,text.stop) != null ) {
                              				        d.setReverseCode( input.toString(text.start,text.stop).substring(1, input.toString(text.start,text.stop).length()-1) );
                              	       				location.setProperty(Location.LOCATION_PROPERTY_FROM_ACCUMULATE_REVERSE_CONTENT, d.getReverseCode());
                              					location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT );
                              				}
                              			
                            }

                            }
                            break;

                    }

                    match(input,RESULT,FOLLOW_RESULT_in_accumulate_statement2900); if (failed) return d;
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2904);
                    text=paren_chunk();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {

                      				if( input.toString(text.start,text.stop) != null ) {
                      				        d.setResultCode( input.toString(text.start,text.stop).substring(1, input.toString(text.start,text.stop).length()-1) );
                      					location.setProperty(Location.LOCATION_PROPERTY_FROM_ACCUMULATE_RESULT_CONTENT, d.getResultCode());
                      				}
                      			
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1041:3: (id= ID text= paren_chunk )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1041:3: (id= ID text= paren_chunk )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1042:4: id= ID text= paren_chunk
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_accumulate_statement2930); if (failed) return d;
                    pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2934);
                    text=paren_chunk();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {

                      				if( id.getText() != null ) {
                      				        d.setExternalFunction( true );
                      					d.setFunctionIdentifier( id.getText() );
                      				        d.setExpression( input.toString(text.start,text.stop).substring(1, input.toString(text.start,text.stop).length()-1) );
                      	       				location.setProperty(Location.LOCATION_PROPERTY_FROM_ACCUMULATE_EXPRESSION_CONTENT, d.getExpression());
                      				}
                      			
                    }

                    }


                    }
                    break;

            }

            RIGHT_PAREN45=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement2951); if (failed) return d;
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


    // $ANTLR start expression_chain
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1060:1: expression_chain[FromDescr from, AccessorDescr as] : ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )? ) ;
    public final void expression_chain(FromDescr from, AccessorDescr as) throws RecognitionException {
        identifier_return field = null;

        square_chunk_return sqarg = null;

        paren_chunk_return paarg = null;



          		FieldAccessDescr fa = null;
        	    	MethodAccessDescr ma = null;	
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1066:2: ( ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )? ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1066:2: ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )? )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1066:2: ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1066:4: DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( expression_chain[from, as] )?
            {
            match(input,DOT,FOLLOW_DOT_in_expression_chain2980); if (failed) return ;
            pushFollow(FOLLOW_identifier_in_expression_chain2984);
            field=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              	        fa = new FieldAccessDescr(((Token)field.start).getText());	
              		fa.setLocation( offset(((Token)field.start).getLine()), ((Token)field.start).getCharPositionInLine() );
              		fa.setStartCharacter( ((CommonToken)((Token)field.start)).getStartIndex() );
              		fa.setEndCharacter( ((CommonToken)((Token)field.start)).getStopIndex() );
              	    
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1073:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )?
            int alt57=3;
            alt57 = dfa57.predict(input);
            switch (alt57) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1074:6: ( LEFT_SQUARE )=>sqarg= square_chunk
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain3015);
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1080:6: ( LEFT_PAREN )=>paarg= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain3048);
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

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1088:4: ( expression_chain[from, as] )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==DOT) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1088:4: expression_chain[from, as]
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain3063);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1102:1: collect_statement returns [CollectDescr d] : COLLECT LEFT_PAREN pattern= pattern_source RIGHT_PAREN ;
    public final CollectDescr collect_statement() throws RecognitionException {
        CollectDescr d = null;

        Token COLLECT46=null;
        Token RIGHT_PAREN47=null;
        BaseDescr pattern = null;



        		d = factory.createCollect();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1107:10: ( COLLECT LEFT_PAREN pattern= pattern_source RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1107:10: COLLECT LEFT_PAREN pattern= pattern_source RIGHT_PAREN
            {
            COLLECT46=(Token)input.LT(1);
            match(input,COLLECT,FOLLOW_COLLECT_in_collect_statement3114); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(COLLECT46.getLine()), COLLECT46.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)COLLECT46).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_FROM_COLLECT );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement3124); if (failed) return d;
            pushFollow(FOLLOW_pattern_source_in_collect_statement3128);
            pattern=pattern_source();
            _fsp--;
            if (failed) return d;
            RIGHT_PAREN47=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement3130); if (failed) return d;
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


    // $ANTLR start fact_binding
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1121:1: fact_binding returns [BaseDescr d] : ID ':' (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN ) ;
    public final BaseDescr fact_binding() throws RecognitionException {
        BaseDescr d = null;

        Token ID48=null;
        BaseDescr fe = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d =null;
        		OrDescr or = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1127:4: ( ID ':' (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1127:4: ID ':' (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN )
            {
            ID48=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding3162); if (failed) return d;
            match(input,72,FOLLOW_72_in_fact_binding3164); if (failed) return d;
            if ( backtracking==0 ) {

               		        // handling incomplete parsing
               		        d = new PatternDescr( );
               		        ((PatternDescr) d).setIdentifier( ID48.getText() );
               		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1133:3: (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN )
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==ID) ) {
                alt60=1;
            }
            else if ( (LA60_0==LEFT_PAREN) ) {
                alt60=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1133:3: (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN )", 60, 0, input);

                throw nvae;
            }
            switch (alt60) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1133:5: fe= fact[$ID.text]
                    {
                    pushFollow(FOLLOW_fact_in_fact_binding3178);
                    fe=fact(ID48.getText());
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {

                       		        // override previously instantiated pattern
                       			d =fe;
                       			if( d != null ) {
                         			    d.setStartCharacter( ((CommonToken)ID48).getStartIndex() );
                         			}
                       		
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1142:4: LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_binding3194); if (failed) return d;
                    pushFollow(FOLLOW_fact_in_fact_binding3198);
                    left=fact(ID48.getText());
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {

                       		        // override previously instantiated pattern
                       			d =left;
                       			if( d != null ) {
                         			    d.setStartCharacter( ((CommonToken)ID48).getStartIndex() );
                         			}
                       		
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1150:4: ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )*
                    loop59:
                    do {
                        int alt59=2;
                        int LA59_0 = input.LA(1);

                        if ( ((LA59_0>=OR && LA59_0<=DOUBLE_PIPE)) ) {
                            alt59=1;
                        }


                        switch (alt59) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1150:6: ( OR | DOUBLE_PIPE ) right= fact[$ID.text]
                    	    {
                    	    if ( (input.LA(1)>=OR && input.LA(1)<=DOUBLE_PIPE) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return d;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fact_binding3211);    throw mse;
                    	    }

                    	    pushFollow(FOLLOW_fact_in_fact_binding3223);
                    	    right=fact(ID48.getText());
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
                    	    break loop59;
                        }
                    } while (true);

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_binding3241); if (failed) return d;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1165:1: fact[String ident] returns [BaseDescr d] : id= qualified_id LEFT_PAREN ( constraints[pattern] )? RIGHT_PAREN ( EOF )? ;
    public final BaseDescr fact(String ident) throws RecognitionException {
        BaseDescr d = null;

        Token LEFT_PAREN49=null;
        Token RIGHT_PAREN50=null;
        qualified_id_return id = null;



        		d =null;
        		PatternDescr pattern = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1171:11: (id= qualified_id LEFT_PAREN ( constraints[pattern] )? RIGHT_PAREN ( EOF )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1171:11: id= qualified_id LEFT_PAREN ( constraints[pattern] )? RIGHT_PAREN ( EOF )?
            {
            if ( backtracking==0 ) {

               			pattern = new PatternDescr( );
               			if( ident != null ) {
               				pattern.setIdentifier( ident );
               			}
               			d = pattern; 
               	        
            }
            pushFollow(FOLLOW_qualified_id_in_fact3296);
            id=qualified_id();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
               			if( id != null ) {
              	 		        pattern.setObjectType( input.toString(id.start,id.stop) );
               			        pattern.setEndCharacter( -1 );
              				pattern.setStartCharacter( ((CommonToken)((Token)id.start)).getStartIndex() );
               			}
               		
            }
            LEFT_PAREN49=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact3306); if (failed) return d;
            if ( backtracking==0 ) {

              		        location.setType( Location.LOCATION_LHS_INSIDE_CONDITION_START );
                          		location.setProperty( Location.LOCATION_PROPERTY_CLASS_NAME, input.toString(id.start,id.stop) );
               				
               			pattern.setLocation( offset(LEFT_PAREN49.getLine()), LEFT_PAREN49.getCharPositionInLine() );
               			pattern.setLeftParentCharacter( ((CommonToken)LEFT_PAREN49).getStartIndex() );
               		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1194:4: ( constraints[pattern] )?
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( ((LA61_0>=ATTRIBUTES && LA61_0<=ID)||(LA61_0>=GLOBAL && LA61_0<=LEFT_PAREN)||(LA61_0>=QUERY && LA61_0<=WHEN)||LA61_0==ENABLED||LA61_0==SALIENCE||LA61_0==DURATION||LA61_0==FROM||LA61_0==EVAL||(LA61_0>=INIT && LA61_0<=RESULT)||(LA61_0>=CONTAINS && LA61_0<=IN)||LA61_0==THEN) ) {
                alt61=1;
            }
            switch (alt61) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1194:6: constraints[pattern]
                    {
                    pushFollow(FOLLOW_constraints_in_fact3320);
                    constraints(pattern);
                    _fsp--;
                    if (failed) return d;

                    }
                    break;

            }

            RIGHT_PAREN50=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact3331); if (failed) return d;
            if ( backtracking==0 ) {

              			this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
              			pattern.setEndLocation( offset(RIGHT_PAREN50.getLine()), RIGHT_PAREN50.getCharPositionInLine() );	
              			pattern.setEndCharacter( ((CommonToken)RIGHT_PAREN50).getStopIndex() );
              		        pattern.setRightParentCharacter( ((CommonToken)RIGHT_PAREN50).getStartIndex() );
               		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1202:4: ( EOF )?
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==EOF) ) {
                alt62=1;
            }
            switch (alt62) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1202:4: EOF
                    {
                    match(input,EOF,FOLLOW_EOF_in_fact3340); if (failed) return d;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1206:1: constraints[PatternDescr pattern] : constraint[$pattern] ( COMMA constraint[$pattern] )* ;
    public final void constraints(PatternDescr pattern) throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1207:4: ( constraint[$pattern] ( COMMA constraint[$pattern] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1207:4: constraint[$pattern] ( COMMA constraint[$pattern] )*
            {
            pushFollow(FOLLOW_constraint_in_constraints3358);
            constraint(pattern);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1208:3: ( COMMA constraint[$pattern] )*
            loop63:
            do {
                int alt63=2;
                int LA63_0 = input.LA(1);

                if ( (LA63_0==COMMA) ) {
                    alt63=1;
                }


                switch (alt63) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1208:5: COMMA constraint[$pattern]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_constraints3365); if (failed) return ;
            	    if ( backtracking==0 ) {
            	       location.setType( Location.LOCATION_LHS_INSIDE_CONDITION_START ); 
            	    }
            	    pushFollow(FOLLOW_constraint_in_constraints3374);
            	    constraint(pattern);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop63;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1213:1: constraint[PatternDescr pattern] : or_constr[top] ;
    public final void constraint(PatternDescr pattern) throws RecognitionException {

        		ConditionalElementDescr top = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1218:3: ( or_constr[top] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1218:3: or_constr[top]
            {
            if ( backtracking==0 ) {

              			top = pattern.getConstraint();
              		
            }
            pushFollow(FOLLOW_or_constr_in_constraint3407);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1224:1: or_constr[ConditionalElementDescr base] : and_constr[or] ( DOUBLE_PIPE and_constr[or] )* ;
    public final void or_constr(ConditionalElementDescr base) throws RecognitionException {

        		OrDescr or = new OrDescr();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1229:3: ( and_constr[or] ( DOUBLE_PIPE and_constr[or] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1229:3: and_constr[or] ( DOUBLE_PIPE and_constr[or] )*
            {
            pushFollow(FOLLOW_and_constr_in_or_constr3430);
            and_constr(or);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1230:3: ( DOUBLE_PIPE and_constr[or] )*
            loop64:
            do {
                int alt64=2;
                int LA64_0 = input.LA(1);

                if ( (LA64_0==DOUBLE_PIPE) ) {
                    alt64=1;
                }


                switch (alt64) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1230:5: DOUBLE_PIPE and_constr[or]
            	    {
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_constr3438); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_START);
            	      		
            	    }
            	    pushFollow(FOLLOW_and_constr_in_or_constr3447);
            	    and_constr(or);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop64;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1245:1: and_constr[ConditionalElementDescr base] : unary_constr[and] ( DOUBLE_AMPER unary_constr[and] )* ;
    public final void and_constr(ConditionalElementDescr base) throws RecognitionException {

        		AndDescr and = new AndDescr();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1250:3: ( unary_constr[and] ( DOUBLE_AMPER unary_constr[and] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1250:3: unary_constr[and] ( DOUBLE_AMPER unary_constr[and] )*
            {
            pushFollow(FOLLOW_unary_constr_in_and_constr3479);
            unary_constr(and);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1251:3: ( DOUBLE_AMPER unary_constr[and] )*
            loop65:
            do {
                int alt65=2;
                int LA65_0 = input.LA(1);

                if ( (LA65_0==DOUBLE_AMPER) ) {
                    alt65=1;
                }


                switch (alt65) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1251:5: DOUBLE_AMPER unary_constr[and]
            	    {
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_constr3487); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_START);
            	      		
            	    }
            	    pushFollow(FOLLOW_unary_constr_in_and_constr3496);
            	    unary_constr(and);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop65;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1266:1: unary_constr[ConditionalElementDescr base] : ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] ) ;
    public final void unary_constr(ConditionalElementDescr base) throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1268:3: ( ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1268:3: ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1268:3: ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] )
            int alt66=3;
            switch ( input.LA(1) ) {
            case ATTRIBUTES:
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
            case ENABLED:
            case SALIENCE:
            case DURATION:
            case FROM:
            case INIT:
            case ACTION:
            case REVERSE:
            case RESULT:
            case CONTAINS:
            case EXCLUDES:
            case MATCHES:
            case MEMBEROF:
            case IN:
            case THEN:
                {
                alt66=1;
                }
                break;
            case LEFT_PAREN:
                {
                alt66=2;
                }
                break;
            case EVAL:
                {
                alt66=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1268:3: ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] )", 66, 0, input);

                throw nvae;
            }

            switch (alt66) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1268:5: field_constraint[$base]
                    {
                    pushFollow(FOLLOW_field_constraint_in_unary_constr3524);
                    field_constraint(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1269:5: LEFT_PAREN or_constr[$base] RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_unary_constr3532); if (failed) return ;
                    pushFollow(FOLLOW_or_constr_in_unary_constr3534);
                    or_constr(base);
                    _fsp--;
                    if (failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_unary_constr3537); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1270:5: EVAL predicate[$base]
                    {
                    match(input,EVAL,FOLLOW_EVAL_in_unary_constr3543); if (failed) return ;
                    pushFollow(FOLLOW_predicate_in_unary_constr3545);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1274:1: field_constraint[ConditionalElementDescr base] : ( ID ':' )? f= accessor_path ( ( options {backtrack=true; } : or_restr_connective[top] ) | '->' predicate[$base] )? ;
    public final void field_constraint(ConditionalElementDescr base) throws RecognitionException {
        Token ID51=null;
        accessor_path_return f = null;



        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        		RestrictionConnectiveDescr top = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1281:3: ( ( ID ':' )? f= accessor_path ( ( options {backtrack=true; } : or_restr_connective[top] ) | '->' predicate[$base] )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1281:3: ( ID ':' )? f= accessor_path ( ( options {backtrack=true; } : or_restr_connective[top] ) | '->' predicate[$base] )?
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1281:3: ( ID ':' )?
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==ID) ) {
                int LA67_1 = input.LA(2);

                if ( (LA67_1==72) ) {
                    alt67=1;
                }
            }
            switch (alt67) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1281:5: ID ':'
                    {
                    ID51=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_field_constraint3575); if (failed) return ;
                    match(input,72,FOLLOW_72_in_field_constraint3577); if (failed) return ;
                    if ( backtracking==0 ) {
                       
                      			fbd = new FieldBindingDescr();
                      			fbd.setIdentifier( ID51.getText() );
                      			fbd.setLocation( offset(ID51.getLine()), ID51.getCharPositionInLine() );
                      			fbd.setStartCharacter( ((CommonToken)ID51).getStartIndex() );
                      			base.addDescr( fbd );

                      		    
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_accessor_path_in_field_constraint3598);
            f=accessor_path();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              		    // use ((Token)f.start) to get token matched in identifier
              		    // or use input.toString(f.start,f.stop) to get text.
              		    if( input.toString(f.start,f.stop) != null ) {
              			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
              			location.setProperty(Location.LOCATION_PROPERTY_PROPERTY_NAME, input.toString(f.start,f.stop));
              		    
              			if ( fbd != null ) {
              			    fbd.setFieldName( input.toString(f.start,f.stop) );
              			    // may have been overwritten
              			    fbd.setStartCharacter( ((CommonToken)ID51).getStartIndex() );
              			} 
              			fc = new FieldConstraintDescr(input.toString(f.start,f.stop));
              			fc.setLocation( offset(((Token)f.start).getLine()), ((Token)f.start).getCharPositionInLine() );
              			fc.setStartCharacter( ((CommonToken)((Token)f.start)).getStartIndex() );
              			top = fc.getRestriction();
              			
              			// it must be a field constraint, as it is not a binding
              			if( ID51 == null ) {
              			    base.addDescr( fc );
              			}
              		    }
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1315:3: ( ( options {backtrack=true; } : or_restr_connective[top] ) | '->' predicate[$base] )?
            int alt68=3;
            int LA68_0 = input.LA(1);

            if ( (LA68_0==LEFT_PAREN||LA68_0==NOT||(LA68_0>=CONTAINS && LA68_0<=IN)||(LA68_0>=75 && LA68_0<=80)) ) {
                alt68=1;
            }
            else if ( (LA68_0==74) ) {
                alt68=2;
            }
            switch (alt68) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1316:4: ( options {backtrack=true; } : or_restr_connective[top] )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1316:4: ( options {backtrack=true; } : or_restr_connective[top] )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1317:6: or_restr_connective[top]
                    {
                    pushFollow(FOLLOW_or_restr_connective_in_field_constraint3626);
                    or_restr_connective(top);
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      				// we must add now as we didn't before
                      				if( ID51 != null) {
                      				    base.addDescr( fc );
                      				}
                      			
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1326:4: '->' predicate[$base]
                    {
                    match(input,74,FOLLOW_74_in_field_constraint3646); if (failed) return ;
                    pushFollow(FOLLOW_predicate_in_field_constraint3648);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1331:1: or_restr_connective[ RestrictionConnectiveDescr base ] options {backtrack=true; } : and_restr_connective[or] ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )* ;
    public final void or_restr_connective(RestrictionConnectiveDescr base) throws RecognitionException {

        		RestrictionConnectiveDescr or = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR);
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1339:3: ( and_restr_connective[or] ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1339:3: and_restr_connective[or] ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )*
            {
            pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective3691);
            and_restr_connective(or);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1340:3: ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )*
            loop69:
            do {
                int alt69=2;
                alt69 = dfa69.predict(input);
                switch (alt69) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1342:6: DOUBLE_PIPE and_restr_connective[or]
            	    {
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_restr_connective3715); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	      			
            	    }
            	    pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective3726);
            	    and_restr_connective(or);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop69;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1357:1: and_restr_connective[ RestrictionConnectiveDescr base ] : constraint_expression[and] ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )* ;
    public final void and_restr_connective(RestrictionConnectiveDescr base) throws RecognitionException {
        Token t=null;


        		RestrictionConnectiveDescr and = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND);
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1362:3: ( constraint_expression[and] ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1362:3: constraint_expression[and] ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )*
            {
            pushFollow(FOLLOW_constraint_expression_in_and_restr_connective3758);
            constraint_expression(and);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1363:3: ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )*
            loop70:
            do {
                int alt70=2;
                alt70 = dfa70.predict(input);
                switch (alt70) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1364:5: t= DOUBLE_AMPER constraint_expression[and]
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_restr_connective3779); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	      			
            	    }
            	    pushFollow(FOLLOW_constraint_expression_in_and_restr_connective3790);
            	    constraint_expression(and);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop70;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1379:1: constraint_expression[RestrictionConnectiveDescr base] : ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN ) ;
    public final void constraint_expression(RestrictionConnectiveDescr base) throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1381:3: ( ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1381:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1381:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )
            int alt71=3;
            switch ( input.LA(1) ) {
            case IN:
                {
                alt71=1;
                }
                break;
            case NOT:
                {
                int LA71_2 = input.LA(2);

                if ( (LA71_2==CONTAINS||(LA71_2>=MATCHES && LA71_2<=MEMBEROF)) ) {
                    alt71=2;
                }
                else if ( (LA71_2==IN) ) {
                    alt71=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1381:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )", 71, 2, input);

                    throw nvae;
                }
                }
                break;
            case CONTAINS:
            case EXCLUDES:
            case MATCHES:
            case MEMBEROF:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
                {
                alt71=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt71=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1381:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )", 71, 0, input);

                throw nvae;
            }

            switch (alt71) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1381:5: compound_operator[$base]
                    {
                    pushFollow(FOLLOW_compound_operator_in_constraint_expression3827);
                    compound_operator(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1382:5: simple_operator[$base]
                    {
                    pushFollow(FOLLOW_simple_operator_in_constraint_expression3834);
                    simple_operator(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1383:5: LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_constraint_expression3841); if (failed) return ;
                    if ( backtracking==0 ) {

                      			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
                      		
                    }
                    pushFollow(FOLLOW_or_restr_connective_in_constraint_expression3850);
                    or_restr_connective(base);
                    _fsp--;
                    if (failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_constraint_expression3856); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1392:1: simple_operator[RestrictionConnectiveDescr base] : (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF ) rd= expression_value[$base, op] ;
    public final void simple_operator(RestrictionConnectiveDescr base) throws RecognitionException {
        Token t=null;
        Token n=null;
        RestrictionDescr rd = null;



        		String op = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1397:3: ( (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF ) rd= expression_value[$base, op] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1397:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF ) rd= expression_value[$base, op]
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1397:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF )
            int alt72=13;
            switch ( input.LA(1) ) {
            case 75:
                {
                alt72=1;
                }
                break;
            case 76:
                {
                alt72=2;
                }
                break;
            case 77:
                {
                alt72=3;
                }
                break;
            case 78:
                {
                alt72=4;
                }
                break;
            case 79:
                {
                alt72=5;
                }
                break;
            case 80:
                {
                alt72=6;
                }
                break;
            case CONTAINS:
                {
                alt72=7;
                }
                break;
            case NOT:
                {
                switch ( input.LA(2) ) {
                case MEMBEROF:
                    {
                    alt72=13;
                    }
                    break;
                case CONTAINS:
                    {
                    alt72=8;
                    }
                    break;
                case MATCHES:
                    {
                    alt72=11;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1397:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF )", 72, 8, input);

                    throw nvae;
                }

                }
                break;
            case EXCLUDES:
                {
                alt72=9;
                }
                break;
            case MATCHES:
                {
                alt72=10;
                }
                break;
            case MEMBEROF:
                {
                alt72=12;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1397:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF )", 72, 0, input);

                throw nvae;
            }

            switch (alt72) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1397:5: t= '=='
                    {
                    t=(Token)input.LT(1);
                    match(input,75,FOLLOW_75_in_simple_operator3887); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1398:5: t= '>'
                    {
                    t=(Token)input.LT(1);
                    match(input,76,FOLLOW_76_in_simple_operator3895); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1399:5: t= '>='
                    {
                    t=(Token)input.LT(1);
                    match(input,77,FOLLOW_77_in_simple_operator3903); if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1400:5: t= '<'
                    {
                    t=(Token)input.LT(1);
                    match(input,78,FOLLOW_78_in_simple_operator3911); if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1401:5: t= '<='
                    {
                    t=(Token)input.LT(1);
                    match(input,79,FOLLOW_79_in_simple_operator3919); if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1402:5: t= '!='
                    {
                    t=(Token)input.LT(1);
                    match(input,80,FOLLOW_80_in_simple_operator3927); if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1403:5: t= CONTAINS
                    {
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_simple_operator3935); if (failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1404:5: n= NOT t= CONTAINS
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator3943); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_simple_operator3947); if (failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1405:5: t= EXCLUDES
                    {
                    t=(Token)input.LT(1);
                    match(input,EXCLUDES,FOLLOW_EXCLUDES_in_simple_operator3955); if (failed) return ;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1406:5: t= MATCHES
                    {
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_simple_operator3963); if (failed) return ;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1407:5: n= NOT t= MATCHES
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator3971); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_simple_operator3975); if (failed) return ;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1408:5: t= MEMBEROF
                    {
                    t=(Token)input.LT(1);
                    match(input,MEMBEROF,FOLLOW_MEMBEROF_in_simple_operator3983); if (failed) return ;

                    }
                    break;
                case 13 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1409:5: n= NOT t= MEMBEROF
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator3991); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,MEMBEROF,FOLLOW_MEMBEROF_in_simple_operator3995); if (failed) return ;

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
            pushFollow(FOLLOW_expression_value_in_simple_operator4009);
            rd=expression_value(base,  op);
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
            		        base.addRestriction( new LiteralRestrictionDescr(op, null) );
            		}
            	
        }
        return ;
    }
    // $ANTLR end simple_operator


    // $ANTLR start compound_operator
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1428:1: compound_operator[RestrictionConnectiveDescr base] : ( IN | NOT IN ) LEFT_PAREN rd= expression_value[group, op] ( COMMA rd= expression_value[group, op] )* RIGHT_PAREN ;
    public final void compound_operator(RestrictionConnectiveDescr base) throws RecognitionException {
        RestrictionDescr rd = null;



        		String op = null;
        		RestrictionConnectiveDescr group = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1434:3: ( ( IN | NOT IN ) LEFT_PAREN rd= expression_value[group, op] ( COMMA rd= expression_value[group, op] )* RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1434:3: ( IN | NOT IN ) LEFT_PAREN rd= expression_value[group, op] ( COMMA rd= expression_value[group, op] )* RIGHT_PAREN
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1434:3: ( IN | NOT IN )
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==IN) ) {
                alt73=1;
            }
            else if ( (LA73_0==NOT) ) {
                alt73=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1434:3: ( IN | NOT IN )", 73, 0, input);

                throw nvae;
            }
            switch (alt73) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1434:5: IN
                    {
                    match(input,IN,FOLLOW_IN_in_compound_operator4039); if (failed) return ;
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1442:5: NOT IN
                    {
                    match(input,NOT,FOLLOW_NOT_in_compound_operator4051); if (failed) return ;
                    match(input,IN,FOLLOW_IN_in_compound_operator4053); if (failed) return ;
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

            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_compound_operator4068); if (failed) return ;
            pushFollow(FOLLOW_expression_value_in_compound_operator4072);
            rd=expression_value(group,  op);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1452:3: ( COMMA rd= expression_value[group, op] )*
            loop74:
            do {
                int alt74=2;
                int LA74_0 = input.LA(1);

                if ( (LA74_0==COMMA) ) {
                    alt74=1;
                }


                switch (alt74) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1452:5: COMMA rd= expression_value[group, op]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_compound_operator4079); if (failed) return ;
            	    pushFollow(FOLLOW_expression_value_in_compound_operator4083);
            	    rd=expression_value(group,  op);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop74;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_compound_operator4092); if (failed) return ;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1459:1: expression_value[RestrictionConnectiveDescr base, String op] returns [RestrictionDescr rd] : (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk ) ;
    public final RestrictionDescr expression_value(RestrictionConnectiveDescr base, String op) throws RecognitionException {
        RestrictionDescr rd = null;

        accessor_path_return ap = null;

        String lc = null;

        paren_chunk_return rvc = null;



        		rd = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1464:3: ( (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1464:3: (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1464:3: (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk )
            int alt75=3;
            switch ( input.LA(1) ) {
            case ATTRIBUTES:
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
            case ENABLED:
            case SALIENCE:
            case DURATION:
            case FROM:
            case INIT:
            case ACTION:
            case REVERSE:
            case RESULT:
            case CONTAINS:
            case EXCLUDES:
            case MATCHES:
            case MEMBEROF:
            case IN:
            case THEN:
                {
                alt75=1;
                }
                break;
            case STRING:
            case BOOL:
            case INT:
            case FLOAT:
            case NULL:
                {
                alt75=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt75=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return rd;}
                NoViableAltException nvae =
                    new NoViableAltException("1464:3: (ap= accessor_path | lc= literal_constraint | rvc= paren_chunk )", 75, 0, input);

                throw nvae;
            }

            switch (alt75) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1464:5: ap= accessor_path
                    {
                    pushFollow(FOLLOW_accessor_path_in_expression_value4126);
                    ap=accessor_path();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      			        if( input.toString(ap.start,ap.stop).indexOf( '.' ) > -1 || input.toString(ap.start,ap.stop).indexOf( '[' ) > -1) {
                      					rd = new QualifiedIdentifierRestrictionDescr(op, input.toString(ap.start,ap.stop));
                      				} else {
                      					rd = new VariableRestrictionDescr(op, input.toString(ap.start,ap.stop));
                      				}
                      			
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1472:5: lc= literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_expression_value4146);
                    lc=literal_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd = new LiteralRestrictionDescr(op, lc);
                      			
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1476:5: rvc= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_value4160);
                    rvc=paren_chunk();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd = new ReturnValueRestrictionDescr(op, input.toString(rvc.start,rvc.stop).substring(1, input.toString(rvc.start,rvc.stop).length()-1) );							
                      			
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


    // $ANTLR start literal_constraint
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1489:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public final String literal_constraint() throws RecognitionException {
        String text = null;

        Token t=null;


        		text = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1493:4: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1493:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1493:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            int alt76=5;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt76=1;
                }
                break;
            case INT:
                {
                alt76=2;
                }
                break;
            case FLOAT:
                {
                alt76=3;
                }
                break;
            case BOOL:
                {
                alt76=4;
                }
                break;
            case NULL:
                {
                alt76=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return text;}
                NoViableAltException nvae =
                    new NoViableAltException("1493:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )", 76, 0, input);

                throw nvae;
            }

            switch (alt76) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1493:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint4203); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = getString( t.getText() ); 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1494:5: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint4214); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1495:5: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint4227); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1496:5: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint4238); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1497:5: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal_constraint4250); if (failed) return text;
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


    // $ANTLR start predicate
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1501:1: predicate[ConditionalElementDescr base] : text= paren_chunk ;
    public final void predicate(ConditionalElementDescr base) throws RecognitionException {
        paren_chunk_return text = null;



        		PredicateDescr d = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1506:3: (text= paren_chunk )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1506:3: text= paren_chunk
            {
            pushFollow(FOLLOW_paren_chunk_in_predicate4288);
            text=paren_chunk();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              		        if( input.toString(text.start,text.stop) != null ) {
              				d = new PredicateDescr( );
              			        d.setContent( input.toString(text.start,text.stop).substring(1, input.toString(text.start,text.stop).length()-1) );
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1518:1: curly_chunk : LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk )* RIGHT_CURLY ;
    public final curly_chunk_return curly_chunk() throws RecognitionException {
        curly_chunk_return retval = new curly_chunk_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1520:3: ( LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk )* RIGHT_CURLY )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1520:3: LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk )* RIGHT_CURLY
            {
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk4306); if (failed) return retval;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1520:14: (~ ( LEFT_CURLY | RIGHT_CURLY ) | curly_chunk )*
            loop77:
            do {
                int alt77=3;
                int LA77_0 = input.LA(1);

                if ( ((LA77_0>=ATTRIBUTES && LA77_0<=NULL)||(LA77_0>=LEFT_SQUARE && LA77_0<=80)) ) {
                    alt77=1;
                }
                else if ( (LA77_0==LEFT_CURLY) ) {
                    alt77=2;
                }


                switch (alt77) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1520:16: ~ ( LEFT_CURLY | RIGHT_CURLY )
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=NULL)||(input.LA(1)>=LEFT_SQUARE && input.LA(1)<=80) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_curly_chunk4310);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1520:44: curly_chunk
            	    {
            	    pushFollow(FOLLOW_curly_chunk_in_curly_chunk4319);
            	    curly_chunk();
            	    _fsp--;
            	    if (failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop77;
                }
            } while (true);

            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk4324); if (failed) return retval;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1523:1: paren_chunk : LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk )* RIGHT_PAREN ;
    public final paren_chunk_return paren_chunk() throws RecognitionException {
        paren_chunk_return retval = new paren_chunk_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1525:3: ( LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk )* RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1525:3: LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk4338); if (failed) return retval;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1525:14: (~ ( LEFT_PAREN | RIGHT_PAREN ) | paren_chunk )*
            loop78:
            do {
                int alt78=3;
                int LA78_0 = input.LA(1);

                if ( ((LA78_0>=ATTRIBUTES && LA78_0<=GLOBAL)||LA78_0==COMMA||(LA78_0>=QUERY && LA78_0<=80)) ) {
                    alt78=1;
                }
                else if ( (LA78_0==LEFT_PAREN) ) {
                    alt78=2;
                }


                switch (alt78) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1525:16: ~ ( LEFT_PAREN | RIGHT_PAREN )
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=GLOBAL)||input.LA(1)==COMMA||(input.LA(1)>=QUERY && input.LA(1)<=80) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_paren_chunk4342);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1525:44: paren_chunk
            	    {
            	    pushFollow(FOLLOW_paren_chunk_in_paren_chunk4351);
            	    paren_chunk();
            	    _fsp--;
            	    if (failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop78;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk4356); if (failed) return retval;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1528:1: square_chunk : LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk )* RIGHT_SQUARE ;
    public final square_chunk_return square_chunk() throws RecognitionException {
        square_chunk_return retval = new square_chunk_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1530:3: ( LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk )* RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1530:3: LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk )* RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk4369); if (failed) return retval;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1530:15: (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | square_chunk )*
            loop79:
            do {
                int alt79=3;
                int LA79_0 = input.LA(1);

                if ( ((LA79_0>=ATTRIBUTES && LA79_0<=RIGHT_CURLY)||(LA79_0>=THEN && LA79_0<=80)) ) {
                    alt79=1;
                }
                else if ( (LA79_0==LEFT_SQUARE) ) {
                    alt79=2;
                }


                switch (alt79) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1530:17: ~ ( LEFT_SQUARE | RIGHT_SQUARE )
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=RIGHT_CURLY)||(input.LA(1)>=THEN && input.LA(1)<=80) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return retval;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_square_chunk4373);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1530:47: square_chunk
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_square_chunk4382);
            	    square_chunk();
            	    _fsp--;
            	    if (failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop79;
                }
            } while (true);

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk4387); if (failed) return retval;

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
    };

    // $ANTLR start qualified_id
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1533:1: qualified_id : ID ( DOT identifier )* ( LEFT_SQUARE RIGHT_SQUARE )* ;
    public final qualified_id_return qualified_id() throws RecognitionException {
        qualified_id_return retval = new qualified_id_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1534:5: ( ID ( DOT identifier )* ( LEFT_SQUARE RIGHT_SQUARE )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1534:5: ID ( DOT identifier )* ( LEFT_SQUARE RIGHT_SQUARE )*
            {
            match(input,ID,FOLLOW_ID_in_qualified_id4400); if (failed) return retval;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1534:8: ( DOT identifier )*
            loop80:
            do {
                int alt80=2;
                int LA80_0 = input.LA(1);

                if ( (LA80_0==DOT) ) {
                    alt80=1;
                }


                switch (alt80) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1534:10: DOT identifier
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_qualified_id4404); if (failed) return retval;
            	    pushFollow(FOLLOW_identifier_in_qualified_id4406);
            	    identifier();
            	    _fsp--;
            	    if (failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop80;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1534:28: ( LEFT_SQUARE RIGHT_SQUARE )*
            loop81:
            do {
                int alt81=2;
                int LA81_0 = input.LA(1);

                if ( (LA81_0==LEFT_SQUARE) ) {
                    alt81=1;
                }


                switch (alt81) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1534:30: LEFT_SQUARE RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_qualified_id4413); if (failed) return retval;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_qualified_id4415); if (failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop81;
                }
            } while (true);


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
    // $ANTLR end qualified_id

    public static class dotted_name_return extends ParserRuleReturnScope {
    };

    // $ANTLR start dotted_name
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1537:1: dotted_name : identifier ( DOT identifier )* ( LEFT_SQUARE RIGHT_SQUARE )* ;
    public final dotted_name_return dotted_name() throws RecognitionException {
        dotted_name_return retval = new dotted_name_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1538:4: ( identifier ( DOT identifier )* ( LEFT_SQUARE RIGHT_SQUARE )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1538:4: identifier ( DOT identifier )* ( LEFT_SQUARE RIGHT_SQUARE )*
            {
            pushFollow(FOLLOW_identifier_in_dotted_name4430);
            identifier();
            _fsp--;
            if (failed) return retval;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1538:15: ( DOT identifier )*
            loop82:
            do {
                int alt82=2;
                int LA82_0 = input.LA(1);

                if ( (LA82_0==DOT) ) {
                    alt82=1;
                }


                switch (alt82) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1538:17: DOT identifier
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_dotted_name4434); if (failed) return retval;
            	    pushFollow(FOLLOW_identifier_in_dotted_name4436);
            	    identifier();
            	    _fsp--;
            	    if (failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop82;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1538:35: ( LEFT_SQUARE RIGHT_SQUARE )*
            loop83:
            do {
                int alt83=2;
                int LA83_0 = input.LA(1);

                if ( (LA83_0==LEFT_SQUARE) ) {
                    alt83=1;
                }


                switch (alt83) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1538:37: LEFT_SQUARE RIGHT_SQUARE
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dotted_name4443); if (failed) return retval;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dotted_name4445); if (failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop83;
                }
            } while (true);


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
    // $ANTLR end dotted_name

    public static class accessor_path_return extends ParserRuleReturnScope {
    };

    // $ANTLR start accessor_path
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1541:1: accessor_path : accessor_element ( DOT accessor_element )* ;
    public final accessor_path_return accessor_path() throws RecognitionException {
        accessor_path_return retval = new accessor_path_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1542:4: ( accessor_element ( DOT accessor_element )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1542:4: accessor_element ( DOT accessor_element )*
            {
            pushFollow(FOLLOW_accessor_element_in_accessor_path4461);
            accessor_element();
            _fsp--;
            if (failed) return retval;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1542:21: ( DOT accessor_element )*
            loop84:
            do {
                int alt84=2;
                int LA84_0 = input.LA(1);

                if ( (LA84_0==DOT) ) {
                    alt84=1;
                }


                switch (alt84) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1542:23: DOT accessor_element
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_accessor_path4465); if (failed) return retval;
            	    pushFollow(FOLLOW_accessor_element_in_accessor_path4467);
            	    accessor_element();
            	    _fsp--;
            	    if (failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop84;
                }
            } while (true);


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
    // $ANTLR end accessor_path


    // $ANTLR start accessor_element
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1545:1: accessor_element : identifier ( square_chunk )* ;
    public final void accessor_element() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1547:3: ( identifier ( square_chunk )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1547:3: identifier ( square_chunk )*
            {
            pushFollow(FOLLOW_identifier_in_accessor_element4485);
            identifier();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1547:14: ( square_chunk )*
            loop85:
            do {
                int alt85=2;
                int LA85_0 = input.LA(1);

                if ( (LA85_0==LEFT_SQUARE) ) {
                    alt85=1;
                }


                switch (alt85) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1547:14: square_chunk
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_accessor_element4487);
            	    square_chunk();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop85;
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
    // $ANTLR end accessor_element


    // $ANTLR start rhs_chunk
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1550:1: rhs_chunk[RuleDescr rule] : THEN (~ END )* loc= END opt_semicolon ;
    public final void rhs_chunk(RuleDescr rule) throws RecognitionException {
        Token loc=null;
        Token THEN52=null;

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1552:3: ( THEN (~ END )* loc= END opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1552:3: THEN (~ END )* loc= END opt_semicolon
            {
            THEN52=(Token)input.LT(1);
            match(input,THEN,FOLLOW_THEN_in_rhs_chunk4504); if (failed) return ;
            if ( backtracking==0 ) {
               location.setType( Location.LOCATION_RHS ); 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1553:3: (~ END )*
            loop86:
            do {
                int alt86=2;
                int LA86_0 = input.LA(1);

                if ( ((LA86_0>=ATTRIBUTES && LA86_0<=QUERY)||(LA86_0>=TEMPLATE && LA86_0<=80)) ) {
                    alt86=1;
                }


                switch (alt86) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1553:5: ~ END
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=QUERY)||(input.LA(1)>=TEMPLATE && input.LA(1)<=80) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_rhs_chunk4512);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop86;
                }
            } while (true);

            loc=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_rhs_chunk4536); if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_rhs_chunk4538);
            opt_semicolon();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

                                  // ignoring first line in the consequence
                                  String buf = input.toString( THEN52, loc );
                                  // removing final END keyword
                                  buf = buf.substring( 0, buf.length()-3 );
                                  if( buf.indexOf( '\n' ) > -1 ) {
                                      buf = buf.substring( buf.indexOf( '\n' ) + 1 );
                                  } else if ( buf.indexOf( '\r' ) > -1 ) {
                                      buf = buf.substring( buf.indexOf( '\r' ) + 1 );
                                  }
              		    rule.setConsequence( buf );
                   		    rule.setConsequenceLocation(offset(THEN52.getLine()), THEN52.getCharPositionInLine());
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1572:1: name returns [String name] : ( ID | STRING );
    public final String name() throws RecognitionException {
        String name = null;

        Token ID53=null;
        Token STRING54=null;

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1573:5: ( ID | STRING )
            int alt87=2;
            int LA87_0 = input.LA(1);

            if ( (LA87_0==ID) ) {
                alt87=1;
            }
            else if ( (LA87_0==STRING) ) {
                alt87=2;
            }
            else {
                if (backtracking>0) {failed=true; return name;}
                NoViableAltException nvae =
                    new NoViableAltException("1572:1: name returns [String name] : ( ID | STRING );", 87, 0, input);

                throw nvae;
            }
            switch (alt87) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1573:5: ID
                    {
                    ID53=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_name4572); if (failed) return name;
                    if ( backtracking==0 ) {
                       name = ID53.getText(); 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1574:5: STRING
                    {
                    STRING54=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_name4580); if (failed) return name;
                    if ( backtracking==0 ) {
                       name = getString( STRING54.getText() ); 
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
        return name;
    }
    // $ANTLR end name

    public static class identifier_return extends ParserRuleReturnScope {
    };

    // $ANTLR start identifier
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1577:1: identifier : ( ID | PACKAGE | FUNCTION | GLOBAL | IMPORT | RULE | QUERY | TEMPLATE | ATTRIBUTES | ENABLED | SALIENCE | DURATION | FROM | INIT | ACTION | REVERSE | RESULT | CONTAINS | EXCLUDES | MEMBEROF | MATCHES | WHEN | THEN | END | IN );
    public final identifier_return identifier() throws RecognitionException {
        identifier_return retval = new identifier_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1578:10: ( ID | PACKAGE | FUNCTION | GLOBAL | IMPORT | RULE | QUERY | TEMPLATE | ATTRIBUTES | ENABLED | SALIENCE | DURATION | FROM | INIT | ACTION | REVERSE | RESULT | CONTAINS | EXCLUDES | MEMBEROF | MATCHES | WHEN | THEN | END | IN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:
            {
            if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=ID)||input.LA(1)==GLOBAL||(input.LA(1)>=QUERY && input.LA(1)<=WHEN)||input.LA(1)==ENABLED||input.LA(1)==SALIENCE||input.LA(1)==DURATION||input.LA(1)==FROM||(input.LA(1)>=INIT && input.LA(1)<=RESULT)||(input.LA(1)>=CONTAINS && input.LA(1)<=IN)||input.LA(1)==THEN ) {
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
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:786:6: ( EXISTS )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:786:8: EXISTS
        {
        match(input,EXISTS,FOLLOW_EXISTS_in_synpred11963); if (failed) return ;

        }
    }
    // $ANTLR end synpred1

    // $ANTLR start synpred2
    public final void synpred2_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:787:5: ( NOT )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:787:7: NOT
        {
        match(input,NOT,FOLLOW_NOT_in_synpred21981); if (failed) return ;

        }
    }
    // $ANTLR end synpred2

    // $ANTLR start synpred3
    public final void synpred3_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:788:5: ( EVAL )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:788:7: EVAL
        {
        match(input,EVAL,FOLLOW_EVAL_in_synpred32000); if (failed) return ;

        }
    }
    // $ANTLR end synpred3

    // $ANTLR start synpred4
    public final void synpred4_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:789:5: ( FORALL )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:789:7: FORALL
        {
        match(input,FORALL,FOLLOW_FORALL_in_synpred42019); if (failed) return ;

        }
    }
    // $ANTLR end synpred4

    // $ANTLR start synpred5
    public final void synpred5_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:790:5: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:790:7: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred52038); if (failed) return ;

        }
    }
    // $ANTLR end synpred5

    // $ANTLR start synpred6
    public final void synpred6_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1074:6: ( LEFT_SQUARE )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1074:8: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred63007); if (failed) return ;

        }
    }
    // $ANTLR end synpred6

    // $ANTLR start synpred7
    public final void synpred7_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1080:6: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1080:8: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred73040); if (failed) return ;

        }
    }
    // $ANTLR end synpred7

    // $ANTLR start synpred8
    public final void synpred8_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1342:6: ( DOUBLE_PIPE and_restr_connective[or] )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1342:6: DOUBLE_PIPE and_restr_connective[or]
        {
        match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred83715); if (failed) return ;
        pushFollow(FOLLOW_and_restr_connective_in_synpred83726);
        and_restr_connective(or);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred8

    // $ANTLR start synpred9
    public final void synpred9_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1364:5: ( DOUBLE_AMPER constraint_expression[and] )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1364:5: DOUBLE_AMPER constraint_expression[and]
        {
        match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred93779); if (failed) return ;
        pushFollow(FOLLOW_constraint_expression_in_synpred93790);
        constraint_expression(and);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred9

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


    protected DFA12 dfa12 = new DFA12(this);
    protected DFA13 dfa13 = new DFA13(this);
    protected DFA21 dfa21 = new DFA21(this);
    protected DFA57 dfa57 = new DFA57(this);
    protected DFA69 dfa69 = new DFA69(this);
    protected DFA70 dfa70 = new DFA70(this);
    static final String DFA12_eotS =
        "\6\uffff";
    static final String DFA12_eofS =
        "\6\uffff";
    static final String DFA12_minS =
        "\2\4\1\uffff\1\73\1\uffff\1\4";
    static final String DFA12_maxS =
        "\2\74\1\uffff\1\73\1\uffff\1\74";
    static final String DFA12_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\1\uffff";
    static final String DFA12_specialS =
        "\6\uffff}>";
    static final String[] DFA12_transitionS = {
            "\5\1\1\uffff\1\1\3\uffff\5\1\3\uffff\1\1\1\uffff\1\1\6\uffff"+
            "\1\1\6\uffff\1\1\5\uffff\4\1\1\uffff\5\1\6\uffff\1\1",
            "\7\2\1\uffff\2\4\5\2\3\uffff\1\2\1\uffff\1\2\6\uffff\1\2\6\uffff"+
            "\1\2\5\uffff\4\2\1\uffff\5\2\4\uffff\1\3\1\uffff\1\2",
            "",
            "\1\5",
            "",
            "\5\2\1\uffff\1\2\1\uffff\2\4\5\2\3\uffff\1\2\1\uffff\1\2\6\uffff"+
            "\1\2\6\uffff\1\2\5\uffff\4\2\1\uffff\5\2\4\uffff\1\3\1\uffff"+
            "\1\2"
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
            return "331:15: (paramType= dotted_name )?";
        }
    }
    static final String DFA13_eotS =
        "\6\uffff";
    static final String DFA13_eofS =
        "\6\uffff";
    static final String DFA13_minS =
        "\2\4\1\73\2\uffff\1\4";
    static final String DFA13_maxS =
        "\2\74\1\73\2\uffff\1\74";
    static final String DFA13_acceptS =
        "\3\uffff\1\2\1\1\1\uffff";
    static final String DFA13_specialS =
        "\6\uffff}>";
    static final String[] DFA13_transitionS = {
            "\5\1\1\uffff\1\1\3\uffff\5\1\3\uffff\1\1\1\uffff\1\1\6\uffff"+
            "\1\1\6\uffff\1\1\5\uffff\4\1\1\uffff\5\1\6\uffff\1\1",
            "\7\4\1\uffff\2\3\5\4\3\uffff\1\4\1\uffff\1\4\6\uffff\1\4\6\uffff"+
            "\1\4\5\uffff\4\4\1\uffff\5\4\4\uffff\1\2\1\uffff\1\4",
            "\1\5",
            "",
            "",
            "\5\4\1\uffff\1\4\1\uffff\2\3\5\4\3\uffff\1\4\1\uffff\1\4\6\uffff"+
            "\1\4\6\uffff\1\4\5\uffff\4\4\1\uffff\5\4\4\uffff\1\2\1\uffff"+
            "\1\4"
    };

    static final short[] DFA13_eot = DFA.unpackEncodedString(DFA13_eotS);
    static final short[] DFA13_eof = DFA.unpackEncodedString(DFA13_eofS);
    static final char[] DFA13_min = DFA.unpackEncodedStringToUnsignedChars(DFA13_minS);
    static final char[] DFA13_max = DFA.unpackEncodedStringToUnsignedChars(DFA13_maxS);
    static final short[] DFA13_accept = DFA.unpackEncodedString(DFA13_acceptS);
    static final short[] DFA13_special = DFA.unpackEncodedString(DFA13_specialS);
    static final short[][] DFA13_transition;

    static {
        int numStates = DFA13_transitionS.length;
        DFA13_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA13_transition[i] = DFA.unpackEncodedString(DFA13_transitionS[i]);
        }
    }

    class DFA13 extends DFA {

        public DFA13(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 13;
            this.eot = DFA13_eot;
            this.eof = DFA13_eof;
            this.min = DFA13_min;
            this.max = DFA13_max;
            this.accept = DFA13_accept;
            this.special = DFA13_special;
            this.transition = DFA13_transition;
        }
        public String getDescription() {
            return "336:22: (paramType= dotted_name )?";
        }
    }
    static final String DFA21_eotS =
        "\11\uffff";
    static final String DFA21_eofS =
        "\11\uffff";
    static final String DFA21_minS =
        "\2\10\1\uffff\1\10\1\uffff\1\4\1\73\2\10";
    static final String DFA21_maxS =
        "\2\52\1\uffff\1\110\1\uffff\1\74\1\73\2\72";
    static final String DFA21_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\4\uffff";
    static final String DFA21_specialS =
        "\11\uffff}>";
    static final String[] DFA21_transitionS = {
            "\1\2\2\uffff\1\1\3\uffff\1\2\27\uffff\4\2",
            "\1\3\2\uffff\1\2\1\uffff\1\4\24\uffff\1\2\1\uffff\1\2\2\uffff"+
            "\4\2",
            "",
            "\1\4\1\5\1\uffff\1\2\2\4\54\uffff\1\6\15\uffff\1\2",
            "",
            "\5\7\1\uffff\1\7\3\uffff\5\7\3\uffff\1\7\1\uffff\1\7\6\uffff"+
            "\1\7\6\uffff\1\7\5\uffff\4\7\1\uffff\5\7\6\uffff\1\7",
            "\1\10",
            "\1\4\1\5\1\uffff\1\2\56\uffff\1\6",
            "\1\4\2\uffff\1\2\56\uffff\1\6"
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
            return "378:3: ( LEFT_PAREN ( ( (paramType= qualified_id )? paramName= ID ) ( COMMA (paramType= qualified_id )? paramName= ID )* )? RIGHT_PAREN )?";
        }
    }
    static final String DFA57_eotS =
        "\150\uffff";
    static final String DFA57_eofS =
        "\150\uffff";
    static final String DFA57_minS =
        "\1\10\1\uffff\1\4\1\uffff\2\4\1\0\5\4\2\uffff\1\4\1\0\5\4\1\0\4"+
        "\4\1\0\1\4\1\0\1\4\2\0\2\4\1\0\1\4\1\0\1\4\1\0\1\4\2\0\2\4\1\0\1"+
        "\4\1\0\1\4\1\0\1\4\2\0\3\4\1\0\2\4\1\0\4\4\1\0\1\4\1\0\1\4\1\uffff"+
        "\44\0";
    static final String DFA57_maxS =
        "\1\107\1\uffff\1\120\1\uffff\2\120\1\0\5\120\2\uffff\1\120\1\0\5"+
        "\120\1\0\4\120\1\0\1\120\1\0\1\120\2\0\2\120\1\0\1\120\1\0\1\120"+
        "\1\0\1\120\2\0\2\120\1\0\1\120\1\0\1\120\1\0\1\120\2\0\3\120\1\0"+
        "\2\120\1\0\4\120\1\0\1\120\1\0\1\120\1\uffff\44\0";
    static final String DFA57_acceptS =
        "\1\uffff\1\1\1\uffff\1\3\10\uffff\2\2\65\uffff\1\2\44\uffff";
    static final String DFA57_specialS =
        "\1\43\1\uffff\1\75\1\uffff\1\26\1\47\1\66\1\64\1\44\1\10\1\1\1\31"+
        "\2\uffff\1\15\1\34\1\24\1\76\1\2\1\13\1\5\1\0\1\73\1\4\1\16\1\55"+
        "\1\61\1\30\1\6\1\27\1\67\1\21\1\33\1\70\1\17\1\40\1\63\1\12\1\14"+
        "\1\11\1\25\1\46\1\7\1\65\1\62\1\51\1\22\1\54\1\32\1\56\1\23\1\57"+
        "\1\52\1\53\1\60\1\74\1\35\1\71\1\37\1\41\1\36\1\72\1\42\1\20\1\3"+
        "\1\45\1\50\45\uffff}>";
    static final String[] DFA57_transitionS = {
            "\2\3\1\uffff\1\2\2\3\1\uffff\1\3\22\uffff\4\3\1\uffff\4\3\1"+
            "\uffff\1\3\15\uffff\1\1\1\uffff\1\3\12\uffff\1\3",
            "",
            "\4\14\1\13\2\14\1\6\1\14\1\15\24\14\1\4\1\14\1\5\2\14\1\7\1"+
            "\10\1\11\1\12\46\14",
            "",
            "\4\14\1\23\2\14\1\17\1\14\1\15\31\14\1\16\1\20\1\21\1\22\46"+
            "\14",
            "\4\14\1\31\2\14\1\25\1\14\1\15\31\14\1\24\1\26\1\27\1\30\46"+
            "\14",
            "\1\uffff",
            "\4\14\1\33\2\14\1\32\1\14\1\15\103\14",
            "\4\14\1\35\2\14\1\34\1\14\1\15\103\14",
            "\7\14\1\36\1\14\1\15\103\14",
            "\7\14\1\37\1\14\1\15\103\14",
            "\5\14\1\41\1\14\1\42\1\14\1\15\54\14\1\43\15\14\1\40\10\14",
            "",
            "",
            "\4\14\1\45\2\14\1\44\1\14\1\15\103\14",
            "\1\uffff",
            "\4\14\1\47\2\14\1\46\1\14\1\15\103\14",
            "\7\14\1\50\1\14\1\15\103\14",
            "\7\14\1\51\1\14\1\15\103\14",
            "\5\14\1\53\1\14\1\54\1\14\1\15\54\14\1\55\15\14\1\52\10\14",
            "\4\14\1\57\2\14\1\56\1\14\1\15\103\14",
            "\1\uffff",
            "\4\14\1\61\2\14\1\60\1\14\1\15\103\14",
            "\7\14\1\62\1\14\1\15\103\14",
            "\7\14\1\63\1\14\1\15\103\14",
            "\5\14\1\65\1\14\1\67\1\14\1\15\54\14\1\66\15\14\1\64\10\14",
            "\1\uffff",
            "\5\14\1\71\1\14\1\72\1\14\1\15\54\14\1\73\15\14\1\70\10\14",
            "\1\uffff",
            "\5\14\1\75\1\14\1\77\1\14\1\15\54\14\1\76\15\14\1\74\10\14",
            "\1\uffff",
            "\1\uffff",
            "\4\14\1\100\2\14\1\101\1\14\1\15\103\14",
            "\5\102\1\14\1\102\1\103\1\14\1\15\5\102\3\14\1\102\1\14\1\102"+
            "\6\14\1\102\6\14\1\102\5\14\4\102\1\14\5\102\6\14\1\102\24\14",
            "\1\uffff",
            "\7\14\1\103\1\14\1\15\55\14\1\104\25\14",
            "\1\uffff",
            "\5\14\1\106\1\14\1\107\1\14\1\15\54\14\1\110\15\14\1\105\10"+
            "\14",
            "\1\uffff",
            "\5\14\1\112\1\14\1\114\1\14\1\15\54\14\1\113\15\14\1\111\10"+
            "\14",
            "\1\uffff",
            "\1\uffff",
            "\4\14\1\115\2\14\1\116\1\14\1\15\103\14",
            "\5\117\1\14\1\117\1\103\1\14\1\15\5\117\3\14\1\117\1\14\1\117"+
            "\6\14\1\117\6\14\1\117\5\14\4\117\1\14\5\117\6\14\1\117\24\14",
            "\1\uffff",
            "\7\14\1\103\1\14\1\15\55\14\1\120\25\14",
            "\1\uffff",
            "\5\14\1\122\1\14\1\123\1\14\1\15\54\14\1\124\15\14\1\121\10"+
            "\14",
            "\1\uffff",
            "\5\14\1\126\1\14\1\130\1\14\1\15\54\14\1\127\15\14\1\125\10"+
            "\14",
            "\1\uffff",
            "\1\uffff",
            "\4\14\1\131\2\14\1\132\1\14\1\15\103\14",
            "\5\133\1\14\1\133\1\103\1\14\1\15\5\133\3\14\1\133\1\14\1\133"+
            "\6\14\1\133\6\14\1\133\5\14\4\133\1\14\5\133\6\14\1\133\24\14",
            "\7\14\1\103\1\14\1\15\55\14\1\134\25\14",
            "\1\uffff",
            "\4\14\1\135\2\14\1\136\1\14\1\15\103\14",
            "\5\137\1\14\1\137\1\103\1\14\1\15\5\137\3\14\1\137\1\14\1\137"+
            "\6\14\1\137\6\14\1\137\5\14\4\137\1\14\5\137\6\14\1\137\24\14",
            "\1\uffff",
            "\7\14\1\103\1\14\1\15\55\14\1\140\25\14",
            "\4\14\1\141\2\14\1\142\1\14\1\15\103\14",
            "\5\143\1\14\1\143\1\103\1\14\1\15\5\143\3\14\1\143\1\14\1\143"+
            "\6\14\1\143\6\14\1\143\5\14\4\143\1\14\5\143\6\14\1\143\24\14",
            "\7\14\1\103\1\14\1\15\55\14\1\144\25\14",
            "\1\uffff",
            "\5\14\1\145\1\14\1\147\1\14\1\15\54\14\1\146\26\14",
            "\1\uffff",
            "\5\14\1\41\1\14\1\42\1\14\1\15\54\14\1\43\26\14",
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

    static final short[] DFA57_eot = DFA.unpackEncodedString(DFA57_eotS);
    static final short[] DFA57_eof = DFA.unpackEncodedString(DFA57_eofS);
    static final char[] DFA57_min = DFA.unpackEncodedStringToUnsignedChars(DFA57_minS);
    static final char[] DFA57_max = DFA.unpackEncodedStringToUnsignedChars(DFA57_maxS);
    static final short[] DFA57_accept = DFA.unpackEncodedString(DFA57_acceptS);
    static final short[] DFA57_special = DFA.unpackEncodedString(DFA57_specialS);
    static final short[][] DFA57_transition;

    static {
        int numStates = DFA57_transitionS.length;
        DFA57_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA57_transition[i] = DFA.unpackEncodedString(DFA57_transitionS[i]);
        }
    }

    class DFA57 extends DFA {

        public DFA57(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 57;
            this.eot = DFA57_eot;
            this.eof = DFA57_eof;
            this.min = DFA57_min;
            this.max = DFA57_max;
            this.accept = DFA57_accept;
            this.special = DFA57_special;
            this.transition = DFA57_transition;
        }
        public String getDescription() {
            return "1073:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )?";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA57_21 = input.LA(1);

                         
                        int index57_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_21);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA57_10 = input.LA(1);

                         
                        int index57_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_10==LEFT_PAREN) ) {s = 31;}

                        else if ( (LA57_10==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_10>=ATTRIBUTES && LA57_10<=GLOBAL)||LA57_10==COMMA||(LA57_10>=QUERY && LA57_10<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_10);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA57_18 = input.LA(1);

                         
                        int index57_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_18==LEFT_PAREN) ) {s = 41;}

                        else if ( (LA57_18==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_18>=ATTRIBUTES && LA57_18<=GLOBAL)||LA57_18==COMMA||(LA57_18>=QUERY && LA57_18<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_18);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA57_64 = input.LA(1);

                         
                        int index57_64 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_64==DOT) ) {s = 101;}

                        else if ( (LA57_64==LEFT_SQUARE) ) {s = 102;}

                        else if ( (LA57_64==LEFT_PAREN) ) {s = 103;}

                        else if ( (LA57_64==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_64>=ATTRIBUTES && LA57_64<=ID)||LA57_64==GLOBAL||LA57_64==COMMA||(LA57_64>=QUERY && LA57_64<=RIGHT_CURLY)||(LA57_64>=RIGHT_SQUARE && LA57_64<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_64);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA57_23 = input.LA(1);

                         
                        int index57_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_23==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_23>=ATTRIBUTES && LA57_23<=GLOBAL)||LA57_23==COMMA||(LA57_23>=QUERY && LA57_23<=80)) && (synpred7())) {s = 12;}

                        else if ( (LA57_23==LEFT_PAREN) ) {s = 50;}

                         
                        input.seek(index57_23);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA57_20 = input.LA(1);

                         
                        int index57_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_20==LEFT_PAREN) ) {s = 46;}

                        else if ( (LA57_20==ID) ) {s = 47;}

                        else if ( (LA57_20==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_20>=ATTRIBUTES && LA57_20<=FUNCTION)||(LA57_20>=DOT && LA57_20<=GLOBAL)||LA57_20==COMMA||(LA57_20>=QUERY && LA57_20<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_20);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA57_28 = input.LA(1);

                         
                        int index57_28 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_28);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA57_42 = input.LA(1);

                         
                        int index57_42 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_42==ID) ) {s = 77;}

                        else if ( (LA57_42==LEFT_PAREN) ) {s = 78;}

                        else if ( (LA57_42==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_42>=ATTRIBUTES && LA57_42<=FUNCTION)||(LA57_42>=DOT && LA57_42<=GLOBAL)||LA57_42==COMMA||(LA57_42>=QUERY && LA57_42<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_42);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA57_9 = input.LA(1);

                         
                        int index57_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_9==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_9>=ATTRIBUTES && LA57_9<=GLOBAL)||LA57_9==COMMA||(LA57_9>=QUERY && LA57_9<=80)) && (synpred7())) {s = 12;}

                        else if ( (LA57_9==LEFT_PAREN) ) {s = 30;}

                         
                        input.seek(index57_9);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA57_39 = input.LA(1);

                         
                        int index57_39 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_39==72) ) {s = 73;}

                        else if ( (LA57_39==DOT) ) {s = 74;}

                        else if ( (LA57_39==LEFT_SQUARE) ) {s = 75;}

                        else if ( (LA57_39==LEFT_PAREN) ) {s = 76;}

                        else if ( (LA57_39==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_39>=ATTRIBUTES && LA57_39<=ID)||LA57_39==GLOBAL||LA57_39==COMMA||(LA57_39>=QUERY && LA57_39<=RIGHT_CURLY)||(LA57_39>=RIGHT_SQUARE && LA57_39<=71)||(LA57_39>=73 && LA57_39<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_39);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA57_37 = input.LA(1);

                         
                        int index57_37 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_37==72) ) {s = 69;}

                        else if ( (LA57_37==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( (LA57_37==DOT) ) {s = 70;}

                        else if ( (LA57_37==LEFT_PAREN) ) {s = 71;}

                        else if ( (LA57_37==LEFT_SQUARE) ) {s = 72;}

                        else if ( ((LA57_37>=ATTRIBUTES && LA57_37<=ID)||LA57_37==GLOBAL||LA57_37==COMMA||(LA57_37>=QUERY && LA57_37<=RIGHT_CURLY)||(LA57_37>=RIGHT_SQUARE && LA57_37<=71)||(LA57_37>=73 && LA57_37<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_37);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA57_19 = input.LA(1);

                         
                        int index57_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_19==72) ) {s = 42;}

                        else if ( (LA57_19==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( (LA57_19==DOT) ) {s = 43;}

                        else if ( (LA57_19==LEFT_PAREN) ) {s = 44;}

                        else if ( (LA57_19==LEFT_SQUARE) ) {s = 45;}

                        else if ( ((LA57_19>=ATTRIBUTES && LA57_19<=ID)||LA57_19==GLOBAL||LA57_19==COMMA||(LA57_19>=QUERY && LA57_19<=RIGHT_CURLY)||(LA57_19>=RIGHT_SQUARE && LA57_19<=71)||(LA57_19>=73 && LA57_19<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_19);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA57_38 = input.LA(1);

                         
                        int index57_38 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_38);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA57_14 = input.LA(1);

                         
                        int index57_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_14==LEFT_PAREN) ) {s = 36;}

                        else if ( (LA57_14==ID) ) {s = 37;}

                        else if ( (LA57_14==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_14>=ATTRIBUTES && LA57_14<=FUNCTION)||(LA57_14>=DOT && LA57_14<=GLOBAL)||LA57_14==COMMA||(LA57_14>=QUERY && LA57_14<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_14);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA57_24 = input.LA(1);

                         
                        int index57_24 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_24==LEFT_PAREN) ) {s = 51;}

                        else if ( (LA57_24==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_24>=ATTRIBUTES && LA57_24<=GLOBAL)||LA57_24==COMMA||(LA57_24>=QUERY && LA57_24<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_24);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA57_34 = input.LA(1);

                         
                        int index57_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_34);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA57_63 = input.LA(1);

                         
                        int index57_63 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_63);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA57_31 = input.LA(1);

                         
                        int index57_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_31);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA57_46 = input.LA(1);

                         
                        int index57_46 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_46);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA57_50 = input.LA(1);

                         
                        int index57_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_50);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA57_16 = input.LA(1);

                         
                        int index57_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_16==LEFT_PAREN) ) {s = 38;}

                        else if ( (LA57_16==ID) ) {s = 39;}

                        else if ( (LA57_16==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_16>=ATTRIBUTES && LA57_16<=FUNCTION)||(LA57_16>=DOT && LA57_16<=GLOBAL)||LA57_16==COMMA||(LA57_16>=QUERY && LA57_16<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_16);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA57_40 = input.LA(1);

                         
                        int index57_40 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_40);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA57_4 = input.LA(1);

                         
                        int index57_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_4==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( (LA57_4==EXISTS) ) {s = 14;}

                        else if ( (LA57_4==LEFT_PAREN) ) {s = 15;}

                        else if ( (LA57_4==NOT) ) {s = 16;}

                        else if ( (LA57_4==EVAL) ) {s = 17;}

                        else if ( (LA57_4==FORALL) ) {s = 18;}

                        else if ( (LA57_4==ID) ) {s = 19;}

                        else if ( ((LA57_4>=ATTRIBUTES && LA57_4<=FUNCTION)||(LA57_4>=DOT && LA57_4<=GLOBAL)||LA57_4==COMMA||(LA57_4>=QUERY && LA57_4<=FROM)||(LA57_4>=ACCUMULATE && LA57_4<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_4);
                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA57_29 = input.LA(1);

                         
                        int index57_29 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_29==72) ) {s = 60;}

                        else if ( (LA57_29==DOT) ) {s = 61;}

                        else if ( (LA57_29==LEFT_SQUARE) ) {s = 62;}

                        else if ( (LA57_29==LEFT_PAREN) ) {s = 63;}

                        else if ( (LA57_29==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_29>=ATTRIBUTES && LA57_29<=ID)||LA57_29==GLOBAL||LA57_29==COMMA||(LA57_29>=QUERY && LA57_29<=RIGHT_CURLY)||(LA57_29>=RIGHT_SQUARE && LA57_29<=71)||(LA57_29>=73 && LA57_29<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_29);
                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA57_27 = input.LA(1);

                         
                        int index57_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_27==72) ) {s = 56;}

                        else if ( (LA57_27==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( (LA57_27==DOT) ) {s = 57;}

                        else if ( (LA57_27==LEFT_PAREN) ) {s = 58;}

                        else if ( (LA57_27==LEFT_SQUARE) ) {s = 59;}

                        else if ( ((LA57_27>=ATTRIBUTES && LA57_27<=ID)||LA57_27==GLOBAL||LA57_27==COMMA||(LA57_27>=QUERY && LA57_27<=RIGHT_CURLY)||(LA57_27>=RIGHT_SQUARE && LA57_27<=71)||(LA57_27>=73 && LA57_27<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_27);
                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA57_11 = input.LA(1);

                         
                        int index57_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_11==72) ) {s = 32;}

                        else if ( (LA57_11==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( (LA57_11==DOT) ) {s = 33;}

                        else if ( (LA57_11==LEFT_PAREN) ) {s = 34;}

                        else if ( (LA57_11==LEFT_SQUARE) ) {s = 35;}

                        else if ( ((LA57_11>=ATTRIBUTES && LA57_11<=ID)||LA57_11==GLOBAL||LA57_11==COMMA||(LA57_11>=QUERY && LA57_11<=RIGHT_CURLY)||(LA57_11>=RIGHT_SQUARE && LA57_11<=71)||(LA57_11>=73 && LA57_11<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_11);
                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA57_48 = input.LA(1);

                         
                        int index57_48 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_48);
                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA57_32 = input.LA(1);

                         
                        int index57_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_32==ID) ) {s = 64;}

                        else if ( (LA57_32==LEFT_PAREN) ) {s = 65;}

                        else if ( (LA57_32==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_32>=ATTRIBUTES && LA57_32<=FUNCTION)||(LA57_32>=DOT && LA57_32<=GLOBAL)||LA57_32==COMMA||(LA57_32>=QUERY && LA57_32<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_32);
                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA57_15 = input.LA(1);

                         
                        int index57_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_15);
                        if ( s>=0 ) return s;
                        break;
                    case 29 : 
                        int LA57_56 = input.LA(1);

                         
                        int index57_56 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_56==ID) ) {s = 93;}

                        else if ( (LA57_56==LEFT_PAREN) ) {s = 94;}

                        else if ( (LA57_56==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_56>=ATTRIBUTES && LA57_56<=FUNCTION)||(LA57_56>=DOT && LA57_56<=GLOBAL)||LA57_56==COMMA||(LA57_56>=QUERY && LA57_56<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_56);
                        if ( s>=0 ) return s;
                        break;
                    case 30 : 
                        int LA57_60 = input.LA(1);

                         
                        int index57_60 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_60==ID) ) {s = 97;}

                        else if ( (LA57_60==LEFT_PAREN) ) {s = 98;}

                        else if ( (LA57_60==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_60>=ATTRIBUTES && LA57_60<=FUNCTION)||(LA57_60>=DOT && LA57_60<=GLOBAL)||LA57_60==COMMA||(LA57_60>=QUERY && LA57_60<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_60);
                        if ( s>=0 ) return s;
                        break;
                    case 31 : 
                        int LA57_58 = input.LA(1);

                         
                        int index57_58 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_58);
                        if ( s>=0 ) return s;
                        break;
                    case 32 : 
                        int LA57_35 = input.LA(1);

                         
                        int index57_35 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_35==RIGHT_SQUARE) ) {s = 68;}

                        else if ( (LA57_35==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_35>=ATTRIBUTES && LA57_35<=GLOBAL)||LA57_35==COMMA||(LA57_35>=QUERY && LA57_35<=LEFT_SQUARE)||(LA57_35>=THEN && LA57_35<=80)) && (synpred7())) {s = 12;}

                        else if ( (LA57_35==LEFT_PAREN) && (synpred7())) {s = 67;}

                         
                        input.seek(index57_35);
                        if ( s>=0 ) return s;
                        break;
                    case 33 : 
                        int LA57_59 = input.LA(1);

                         
                        int index57_59 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_59==RIGHT_SQUARE) ) {s = 96;}

                        else if ( (LA57_59==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_59>=ATTRIBUTES && LA57_59<=GLOBAL)||LA57_59==COMMA||(LA57_59>=QUERY && LA57_59<=LEFT_SQUARE)||(LA57_59>=THEN && LA57_59<=80)) && (synpred7())) {s = 12;}

                        else if ( (LA57_59==LEFT_PAREN) && (synpred7())) {s = 67;}

                         
                        input.seek(index57_59);
                        if ( s>=0 ) return s;
                        break;
                    case 34 : 
                        int LA57_62 = input.LA(1);

                         
                        int index57_62 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_62==RIGHT_SQUARE) ) {s = 100;}

                        else if ( (LA57_62==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_62>=ATTRIBUTES && LA57_62<=GLOBAL)||LA57_62==COMMA||(LA57_62>=QUERY && LA57_62<=LEFT_SQUARE)||(LA57_62>=THEN && LA57_62<=80)) && (synpred7())) {s = 12;}

                        else if ( (LA57_62==LEFT_PAREN) && (synpred7())) {s = 67;}

                         
                        input.seek(index57_62);
                        if ( s>=0 ) return s;
                        break;
                    case 35 : 
                        int LA57_0 = input.LA(1);

                         
                        int index57_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_0==LEFT_SQUARE) && (synpred6())) {s = 1;}

                        else if ( (LA57_0==LEFT_PAREN) ) {s = 2;}

                        else if ( ((LA57_0>=ID && LA57_0<=DOT)||(LA57_0>=COMMA && LA57_0<=RIGHT_PAREN)||LA57_0==END||(LA57_0>=OR && LA57_0<=DOUBLE_AMPER)||(LA57_0>=EXISTS && LA57_0<=FORALL)||LA57_0==INIT||LA57_0==THEN||LA57_0==71) ) {s = 3;}

                         
                        input.seek(index57_0);
                        if ( s>=0 ) return s;
                        break;
                    case 36 : 
                        int LA57_8 = input.LA(1);

                         
                        int index57_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_8==LEFT_PAREN) ) {s = 28;}

                        else if ( (LA57_8==ID) ) {s = 29;}

                        else if ( (LA57_8==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_8>=ATTRIBUTES && LA57_8<=FUNCTION)||(LA57_8>=DOT && LA57_8<=GLOBAL)||LA57_8==COMMA||(LA57_8>=QUERY && LA57_8<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_8);
                        if ( s>=0 ) return s;
                        break;
                    case 37 : 
                        int LA57_65 = input.LA(1);

                         
                        int index57_65 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_65);
                        if ( s>=0 ) return s;
                        break;
                    case 38 : 
                        int LA57_41 = input.LA(1);

                         
                        int index57_41 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_41);
                        if ( s>=0 ) return s;
                        break;
                    case 39 : 
                        int LA57_5 = input.LA(1);

                         
                        int index57_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_5==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( (LA57_5==EXISTS) ) {s = 20;}

                        else if ( (LA57_5==LEFT_PAREN) ) {s = 21;}

                        else if ( (LA57_5==NOT) ) {s = 22;}

                        else if ( (LA57_5==EVAL) ) {s = 23;}

                        else if ( (LA57_5==FORALL) ) {s = 24;}

                        else if ( (LA57_5==ID) ) {s = 25;}

                        else if ( ((LA57_5>=ATTRIBUTES && LA57_5<=FUNCTION)||(LA57_5>=DOT && LA57_5<=GLOBAL)||LA57_5==COMMA||(LA57_5>=QUERY && LA57_5<=FROM)||(LA57_5>=ACCUMULATE && LA57_5<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_5);
                        if ( s>=0 ) return s;
                        break;
                    case 40 : 
                        int LA57_66 = input.LA(1);

                         
                        int index57_66 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_66==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( (LA57_66==LEFT_SQUARE) ) {s = 35;}

                        else if ( (LA57_66==LEFT_PAREN) ) {s = 34;}

                        else if ( (LA57_66==DOT) ) {s = 33;}

                        else if ( ((LA57_66>=ATTRIBUTES && LA57_66<=ID)||LA57_66==GLOBAL||LA57_66==COMMA||(LA57_66>=QUERY && LA57_66<=RIGHT_CURLY)||(LA57_66>=RIGHT_SQUARE && LA57_66<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_66);
                        if ( s>=0 ) return s;
                        break;
                    case 41 : 
                        int LA57_45 = input.LA(1);

                         
                        int index57_45 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_45==RIGHT_SQUARE) ) {s = 80;}

                        else if ( (LA57_45==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_45>=ATTRIBUTES && LA57_45<=GLOBAL)||LA57_45==COMMA||(LA57_45>=QUERY && LA57_45<=LEFT_SQUARE)||(LA57_45>=THEN && LA57_45<=80)) && (synpred7())) {s = 12;}

                        else if ( (LA57_45==LEFT_PAREN) && (synpred7())) {s = 67;}

                         
                        input.seek(index57_45);
                        if ( s>=0 ) return s;
                        break;
                    case 42 : 
                        int LA57_52 = input.LA(1);

                         
                        int index57_52 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_52==ID) ) {s = 89;}

                        else if ( (LA57_52==LEFT_PAREN) ) {s = 90;}

                        else if ( (LA57_52==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_52>=ATTRIBUTES && LA57_52<=FUNCTION)||(LA57_52>=DOT && LA57_52<=GLOBAL)||LA57_52==COMMA||(LA57_52>=QUERY && LA57_52<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_52);
                        if ( s>=0 ) return s;
                        break;
                    case 43 : 
                        int LA57_53 = input.LA(1);

                         
                        int index57_53 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA57_53>=ATTRIBUTES && LA57_53<=ID)||LA57_53==GLOBAL||(LA57_53>=QUERY && LA57_53<=WHEN)||LA57_53==ENABLED||LA57_53==SALIENCE||LA57_53==DURATION||LA57_53==FROM||(LA57_53>=INIT && LA57_53<=RESULT)||(LA57_53>=CONTAINS && LA57_53<=IN)||LA57_53==THEN) ) {s = 91;}

                        else if ( (LA57_53==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( (LA57_53==DOT||LA57_53==COMMA||(LA57_53>=DATE_EFFECTIVE && LA57_53<=DATE_EXPIRES)||LA57_53==BOOL||(LA57_53>=INT && LA57_53<=AGENDA_GROUP)||(LA57_53>=DIALECT && LA57_53<=DOUBLE_AMPER)||(LA57_53>=EXISTS && LA57_53<=ACCUMULATE)||LA57_53==COLLECT||(LA57_53>=FLOAT && LA57_53<=RIGHT_SQUARE)||(LA57_53>=EOL && LA57_53<=80)) && (synpred7())) {s = 12;}

                        else if ( (LA57_53==LEFT_PAREN) && (synpred7())) {s = 67;}

                         
                        input.seek(index57_53);
                        if ( s>=0 ) return s;
                        break;
                    case 44 : 
                        int LA57_47 = input.LA(1);

                         
                        int index57_47 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_47==72) ) {s = 81;}

                        else if ( (LA57_47==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( (LA57_47==DOT) ) {s = 82;}

                        else if ( (LA57_47==LEFT_PAREN) ) {s = 83;}

                        else if ( (LA57_47==LEFT_SQUARE) ) {s = 84;}

                        else if ( ((LA57_47>=ATTRIBUTES && LA57_47<=ID)||LA57_47==GLOBAL||LA57_47==COMMA||(LA57_47>=QUERY && LA57_47<=RIGHT_CURLY)||(LA57_47>=RIGHT_SQUARE && LA57_47<=71)||(LA57_47>=73 && LA57_47<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_47);
                        if ( s>=0 ) return s;
                        break;
                    case 45 : 
                        int LA57_25 = input.LA(1);

                         
                        int index57_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_25==72) ) {s = 52;}

                        else if ( (LA57_25==DOT) ) {s = 53;}

                        else if ( (LA57_25==LEFT_SQUARE) ) {s = 54;}

                        else if ( (LA57_25==LEFT_PAREN) ) {s = 55;}

                        else if ( (LA57_25==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_25>=ATTRIBUTES && LA57_25<=ID)||LA57_25==GLOBAL||LA57_25==COMMA||(LA57_25>=QUERY && LA57_25<=RIGHT_CURLY)||(LA57_25>=RIGHT_SQUARE && LA57_25<=71)||(LA57_25>=73 && LA57_25<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_25);
                        if ( s>=0 ) return s;
                        break;
                    case 46 : 
                        int LA57_49 = input.LA(1);

                         
                        int index57_49 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_49==72) ) {s = 85;}

                        else if ( (LA57_49==DOT) ) {s = 86;}

                        else if ( (LA57_49==LEFT_SQUARE) ) {s = 87;}

                        else if ( (LA57_49==LEFT_PAREN) ) {s = 88;}

                        else if ( (LA57_49==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_49>=ATTRIBUTES && LA57_49<=ID)||LA57_49==GLOBAL||LA57_49==COMMA||(LA57_49>=QUERY && LA57_49<=RIGHT_CURLY)||(LA57_49>=RIGHT_SQUARE && LA57_49<=71)||(LA57_49>=73 && LA57_49<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_49);
                        if ( s>=0 ) return s;
                        break;
                    case 47 : 
                        int LA57_51 = input.LA(1);

                         
                        int index57_51 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_51);
                        if ( s>=0 ) return s;
                        break;
                    case 48 : 
                        int LA57_54 = input.LA(1);

                         
                        int index57_54 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_54==RIGHT_SQUARE) ) {s = 92;}

                        else if ( (LA57_54==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_54>=ATTRIBUTES && LA57_54<=GLOBAL)||LA57_54==COMMA||(LA57_54>=QUERY && LA57_54<=LEFT_SQUARE)||(LA57_54>=THEN && LA57_54<=80)) && (synpred7())) {s = 12;}

                        else if ( (LA57_54==LEFT_PAREN) && (synpred7())) {s = 67;}

                         
                        input.seek(index57_54);
                        if ( s>=0 ) return s;
                        break;
                    case 49 : 
                        int LA57_26 = input.LA(1);

                         
                        int index57_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_26);
                        if ( s>=0 ) return s;
                        break;
                    case 50 : 
                        int LA57_44 = input.LA(1);

                         
                        int index57_44 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_44);
                        if ( s>=0 ) return s;
                        break;
                    case 51 : 
                        int LA57_36 = input.LA(1);

                         
                        int index57_36 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_36);
                        if ( s>=0 ) return s;
                        break;
                    case 52 : 
                        int LA57_7 = input.LA(1);

                         
                        int index57_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_7==LEFT_PAREN) ) {s = 26;}

                        else if ( (LA57_7==ID) ) {s = 27;}

                        else if ( (LA57_7==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_7>=ATTRIBUTES && LA57_7<=FUNCTION)||(LA57_7>=DOT && LA57_7<=GLOBAL)||LA57_7==COMMA||(LA57_7>=QUERY && LA57_7<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_7);
                        if ( s>=0 ) return s;
                        break;
                    case 53 : 
                        int LA57_43 = input.LA(1);

                         
                        int index57_43 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA57_43>=ATTRIBUTES && LA57_43<=ID)||LA57_43==GLOBAL||(LA57_43>=QUERY && LA57_43<=WHEN)||LA57_43==ENABLED||LA57_43==SALIENCE||LA57_43==DURATION||LA57_43==FROM||(LA57_43>=INIT && LA57_43<=RESULT)||(LA57_43>=CONTAINS && LA57_43<=IN)||LA57_43==THEN) ) {s = 79;}

                        else if ( (LA57_43==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( (LA57_43==DOT||LA57_43==COMMA||(LA57_43>=DATE_EFFECTIVE && LA57_43<=DATE_EXPIRES)||LA57_43==BOOL||(LA57_43>=INT && LA57_43<=AGENDA_GROUP)||(LA57_43>=DIALECT && LA57_43<=DOUBLE_AMPER)||(LA57_43>=EXISTS && LA57_43<=ACCUMULATE)||LA57_43==COLLECT||(LA57_43>=FLOAT && LA57_43<=RIGHT_SQUARE)||(LA57_43>=EOL && LA57_43<=80)) && (synpred7())) {s = 12;}

                        else if ( (LA57_43==LEFT_PAREN) && (synpred7())) {s = 67;}

                         
                        input.seek(index57_43);
                        if ( s>=0 ) return s;
                        break;
                    case 54 : 
                        int LA57_6 = input.LA(1);

                         
                        int index57_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_6);
                        if ( s>=0 ) return s;
                        break;
                    case 55 : 
                        int LA57_30 = input.LA(1);

                         
                        int index57_30 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_30);
                        if ( s>=0 ) return s;
                        break;
                    case 56 : 
                        int LA57_33 = input.LA(1);

                         
                        int index57_33 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA57_33>=ATTRIBUTES && LA57_33<=ID)||LA57_33==GLOBAL||(LA57_33>=QUERY && LA57_33<=WHEN)||LA57_33==ENABLED||LA57_33==SALIENCE||LA57_33==DURATION||LA57_33==FROM||(LA57_33>=INIT && LA57_33<=RESULT)||(LA57_33>=CONTAINS && LA57_33<=IN)||LA57_33==THEN) ) {s = 66;}

                        else if ( (LA57_33==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( (LA57_33==DOT||LA57_33==COMMA||(LA57_33>=DATE_EFFECTIVE && LA57_33<=DATE_EXPIRES)||LA57_33==BOOL||(LA57_33>=INT && LA57_33<=AGENDA_GROUP)||(LA57_33>=DIALECT && LA57_33<=DOUBLE_AMPER)||(LA57_33>=EXISTS && LA57_33<=ACCUMULATE)||LA57_33==COLLECT||(LA57_33>=FLOAT && LA57_33<=RIGHT_SQUARE)||(LA57_33>=EOL && LA57_33<=80)) && (synpred7())) {s = 12;}

                        else if ( (LA57_33==LEFT_PAREN) && (synpred7())) {s = 67;}

                         
                        input.seek(index57_33);
                        if ( s>=0 ) return s;
                        break;
                    case 57 : 
                        int LA57_57 = input.LA(1);

                         
                        int index57_57 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA57_57>=ATTRIBUTES && LA57_57<=ID)||LA57_57==GLOBAL||(LA57_57>=QUERY && LA57_57<=WHEN)||LA57_57==ENABLED||LA57_57==SALIENCE||LA57_57==DURATION||LA57_57==FROM||(LA57_57>=INIT && LA57_57<=RESULT)||(LA57_57>=CONTAINS && LA57_57<=IN)||LA57_57==THEN) ) {s = 95;}

                        else if ( (LA57_57==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( (LA57_57==DOT||LA57_57==COMMA||(LA57_57>=DATE_EFFECTIVE && LA57_57<=DATE_EXPIRES)||LA57_57==BOOL||(LA57_57>=INT && LA57_57<=AGENDA_GROUP)||(LA57_57>=DIALECT && LA57_57<=DOUBLE_AMPER)||(LA57_57>=EXISTS && LA57_57<=ACCUMULATE)||LA57_57==COLLECT||(LA57_57>=FLOAT && LA57_57<=RIGHT_SQUARE)||(LA57_57>=EOL && LA57_57<=80)) && (synpred7())) {s = 12;}

                        else if ( (LA57_57==LEFT_PAREN) && (synpred7())) {s = 67;}

                         
                        input.seek(index57_57);
                        if ( s>=0 ) return s;
                        break;
                    case 58 : 
                        int LA57_61 = input.LA(1);

                         
                        int index57_61 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA57_61>=ATTRIBUTES && LA57_61<=ID)||LA57_61==GLOBAL||(LA57_61>=QUERY && LA57_61<=WHEN)||LA57_61==ENABLED||LA57_61==SALIENCE||LA57_61==DURATION||LA57_61==FROM||(LA57_61>=INIT && LA57_61<=RESULT)||(LA57_61>=CONTAINS && LA57_61<=IN)||LA57_61==THEN) ) {s = 99;}

                        else if ( (LA57_61==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( (LA57_61==DOT||LA57_61==COMMA||(LA57_61>=DATE_EFFECTIVE && LA57_61<=DATE_EXPIRES)||LA57_61==BOOL||(LA57_61>=INT && LA57_61<=AGENDA_GROUP)||(LA57_61>=DIALECT && LA57_61<=DOUBLE_AMPER)||(LA57_61>=EXISTS && LA57_61<=ACCUMULATE)||LA57_61==COLLECT||(LA57_61>=FLOAT && LA57_61<=RIGHT_SQUARE)||(LA57_61>=EOL && LA57_61<=80)) && (synpred7())) {s = 12;}

                        else if ( (LA57_61==LEFT_PAREN) && (synpred7())) {s = 67;}

                         
                        input.seek(index57_61);
                        if ( s>=0 ) return s;
                        break;
                    case 59 : 
                        int LA57_22 = input.LA(1);

                         
                        int index57_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_22==LEFT_PAREN) ) {s = 48;}

                        else if ( (LA57_22==ID) ) {s = 49;}

                        else if ( (LA57_22==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_22>=ATTRIBUTES && LA57_22<=FUNCTION)||(LA57_22>=DOT && LA57_22<=GLOBAL)||LA57_22==COMMA||(LA57_22>=QUERY && LA57_22<=80)) && (synpred7())) {s = 12;}

                         
                        input.seek(index57_22);
                        if ( s>=0 ) return s;
                        break;
                    case 60 : 
                        int LA57_55 = input.LA(1);

                         
                        int index57_55 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index57_55);
                        if ( s>=0 ) return s;
                        break;
                    case 61 : 
                        int LA57_2 = input.LA(1);

                         
                        int index57_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_2==OR) ) {s = 4;}

                        else if ( (LA57_2==AND) ) {s = 5;}

                        else if ( (LA57_2==LEFT_PAREN) ) {s = 6;}

                        else if ( (LA57_2==EXISTS) ) {s = 7;}

                        else if ( (LA57_2==NOT) ) {s = 8;}

                        else if ( (LA57_2==EVAL) ) {s = 9;}

                        else if ( (LA57_2==FORALL) ) {s = 10;}

                        else if ( (LA57_2==ID) ) {s = 11;}

                        else if ( ((LA57_2>=ATTRIBUTES && LA57_2<=FUNCTION)||(LA57_2>=DOT && LA57_2<=GLOBAL)||LA57_2==COMMA||(LA57_2>=QUERY && LA57_2<=LOCK_ON_ACTIVE)||LA57_2==DOUBLE_PIPE||(LA57_2>=DOUBLE_AMPER && LA57_2<=FROM)||(LA57_2>=ACCUMULATE && LA57_2<=80)) && (synpred7())) {s = 12;}

                        else if ( (LA57_2==RIGHT_PAREN) && (synpred7())) {s = 13;}

                         
                        input.seek(index57_2);
                        if ( s>=0 ) return s;
                        break;
                    case 62 : 
                        int LA57_17 = input.LA(1);

                         
                        int index57_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA57_17==RIGHT_PAREN) && (synpred7())) {s = 13;}

                        else if ( ((LA57_17>=ATTRIBUTES && LA57_17<=GLOBAL)||LA57_17==COMMA||(LA57_17>=QUERY && LA57_17<=80)) && (synpred7())) {s = 12;}

                        else if ( (LA57_17==LEFT_PAREN) ) {s = 40;}

                         
                        input.seek(index57_17);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 57, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA69_eotS =
        "\21\uffff";
    static final String DFA69_eofS =
        "\21\uffff";
    static final String DFA69_minS =
        "\1\14\1\uffff\1\4\1\11\1\0\1\4\1\uffff\1\4\11\0";
    static final String DFA69_maxS =
        "\1\45\1\uffff\2\120\1\0\1\120\1\uffff\1\120\11\0";
    static final String DFA69_acceptS =
        "\1\uffff\1\2\4\uffff\1\1\12\uffff";
    static final String DFA69_specialS =
        "\4\uffff\1\0\14\uffff}>";
    static final String[] DFA69_transitionS = {
            "\2\1\25\uffff\1\2\1\uffff\1\1",
            "",
            "\5\1\1\uffff\1\1\1\4\2\uffff\5\1\3\uffff\1\1\1\uffff\1\1\6\uffff"+
            "\1\1\6\uffff\1\1\1\uffff\1\6\1\1\2\uffff\4\1\1\uffff\1\5\1\7"+
            "\1\10\1\11\1\3\6\uffff\1\1\16\uffff\6\6",
            "\1\1\1\uffff\1\12\2\1\25\uffff\1\1\1\uffff\1\1\2\uffff\1\1\10"+
            "\uffff\5\1\4\uffff\1\1\17\uffff\7\1",
            "\1\uffff",
            "\5\6\1\1\1\6\1\14\2\1\5\6\1\uffff\1\6\1\uffff\4\6\5\uffff\1"+
            "\6\3\uffff\1\1\1\uffff\1\1\1\6\1\uffff\1\1\3\uffff\4\6\1\uffff"+
            "\1\15\1\16\1\17\1\20\1\13\2\6\2\uffff\1\1\1\uffff\1\6\15\uffff"+
            "\7\1",
            "",
            "\5\6\1\1\1\6\1\14\2\1\5\6\1\uffff\1\6\1\uffff\4\6\5\uffff\1"+
            "\6\3\uffff\1\1\1\uffff\1\1\1\6\1\uffff\1\1\3\uffff\4\6\1\uffff"+
            "\1\15\1\16\1\17\1\20\1\13\2\6\2\uffff\1\1\1\uffff\1\6\15\uffff"+
            "\7\1",
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

    static final short[] DFA69_eot = DFA.unpackEncodedString(DFA69_eotS);
    static final short[] DFA69_eof = DFA.unpackEncodedString(DFA69_eofS);
    static final char[] DFA69_min = DFA.unpackEncodedStringToUnsignedChars(DFA69_minS);
    static final char[] DFA69_max = DFA.unpackEncodedStringToUnsignedChars(DFA69_maxS);
    static final short[] DFA69_accept = DFA.unpackEncodedString(DFA69_acceptS);
    static final short[] DFA69_special = DFA.unpackEncodedString(DFA69_specialS);
    static final short[][] DFA69_transition;

    static {
        int numStates = DFA69_transitionS.length;
        DFA69_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA69_transition[i] = DFA.unpackEncodedString(DFA69_transitionS[i]);
        }
    }

    class DFA69 extends DFA {

        public DFA69(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 69;
            this.eot = DFA69_eot;
            this.eof = DFA69_eof;
            this.min = DFA69_min;
            this.max = DFA69_max;
            this.accept = DFA69_accept;
            this.special = DFA69_special;
            this.transition = DFA69_transition;
        }
        public String getDescription() {
            return "()* loopback of 1340:3: ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )*";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA69_4 = input.LA(1);

                         
                        int index69_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8()) ) {s = 6;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index69_4);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 69, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA70_eotS =
        "\61\uffff";
    static final String DFA70_eofS =
        "\1\1\12\uffff\1\4\1\uffff\4\4\40\uffff";
    static final String DFA70_minS =
        "\1\14\1\uffff\1\4\1\11\1\uffff\6\4\1\11\5\4\1\11\1\0\2\4\34\0";
    static final String DFA70_maxS =
        "\1\45\1\uffff\2\120\1\uffff\6\120\1\72\1\120\4\74\1\120\1\0\2\120"+
        "\34\0";
    static final String DFA70_acceptS =
        "\1\uffff\1\2\2\uffff\1\1\54\uffff";
    static final String DFA70_specialS =
        "\22\uffff\1\0\36\uffff}>";
    static final String[] DFA70_transitionS = {
            "\2\1\25\uffff\1\1\1\uffff\1\2",
            "",
            "\5\1\1\uffff\1\1\1\11\2\uffff\5\1\3\uffff\1\1\1\uffff\1\1\6"+
            "\uffff\1\1\6\uffff\1\1\1\uffff\1\4\1\1\2\uffff\4\1\1\uffff\1"+
            "\5\1\6\1\7\1\10\1\3\6\uffff\1\1\16\uffff\6\4",
            "\1\1\1\uffff\1\12\2\1\25\uffff\1\1\1\uffff\1\1\2\uffff\1\1\10"+
            "\uffff\5\1\4\uffff\1\1\17\uffff\7\1",
            "",
            "\5\4\1\1\1\4\1\14\2\1\5\4\1\uffff\1\4\1\uffff\4\4\5\uffff\1"+
            "\4\3\uffff\1\1\1\uffff\1\1\1\4\1\uffff\1\1\3\uffff\4\4\1\uffff"+
            "\1\15\1\16\1\17\1\20\1\13\2\4\2\uffff\1\1\1\uffff\1\4\15\uffff"+
            "\7\1",
            "\5\4\1\1\1\4\1\14\2\1\5\4\1\uffff\1\4\1\uffff\4\4\5\uffff\1"+
            "\4\3\uffff\1\1\1\uffff\1\1\1\4\1\uffff\1\1\3\uffff\4\4\1\uffff"+
            "\1\15\1\16\1\17\1\20\1\13\2\4\2\uffff\1\1\1\uffff\1\4\15\uffff"+
            "\7\1",
            "\5\4\1\1\1\4\1\14\2\1\5\4\1\uffff\1\4\1\uffff\4\4\5\uffff\1"+
            "\4\3\uffff\1\1\1\uffff\1\1\1\4\1\uffff\1\1\3\uffff\4\4\1\uffff"+
            "\1\15\1\16\1\17\1\20\1\13\2\4\2\uffff\1\1\1\uffff\1\4\15\uffff"+
            "\7\1",
            "\5\4\1\1\1\4\1\14\2\1\5\4\1\uffff\1\4\1\uffff\4\4\5\uffff\1"+
            "\4\3\uffff\1\1\1\uffff\1\1\1\4\1\uffff\1\1\3\uffff\4\4\1\uffff"+
            "\1\15\1\16\1\17\1\20\1\13\2\4\2\uffff\1\1\1\uffff\1\4\15\uffff"+
            "\7\1",
            "\5\1\1\uffff\1\1\1\22\2\uffff\5\1\3\uffff\1\1\1\uffff\1\1\6"+
            "\uffff\1\1\6\uffff\1\1\1\uffff\1\4\1\1\2\uffff\4\1\1\uffff\1"+
            "\23\1\24\1\25\1\26\1\21\6\uffff\1\1\16\uffff\6\4",
            "\5\4\1\uffff\1\4\1\30\2\uffff\5\4\1\uffff\1\4\1\uffff\4\4\5"+
            "\uffff\1\4\6\uffff\1\4\1\uffff\1\1\3\uffff\4\4\1\uffff\1\31"+
            "\1\32\1\33\1\34\1\27\2\4\4\uffff\1\4\16\uffff\6\1",
            "\1\4\1\uffff\1\1\2\4\25\uffff\1\4\1\uffff\1\4\24\uffff\1\4",
            "\7\4\1\51\34\4\1\36\10\4\1\45\1\46\1\47\1\50\1\35\25\4\1\37"+
            "\1\40\1\41\1\42\1\43\1\44",
            "\5\1\1\4\2\1\2\4\5\1\1\uffff\1\1\1\uffff\4\1\5\uffff\1\1\3\uffff"+
            "\1\4\1\uffff\1\4\1\1\5\uffff\4\1\1\uffff\7\1\2\uffff\1\4\1\uffff"+
            "\1\1",
            "\5\1\1\4\2\1\2\4\5\1\1\uffff\1\1\1\uffff\4\1\5\uffff\1\1\3\uffff"+
            "\1\4\1\uffff\1\4\1\1\5\uffff\4\1\1\uffff\7\1\2\uffff\1\4\1\uffff"+
            "\1\1",
            "\5\1\1\4\2\1\2\4\5\1\1\uffff\1\1\1\uffff\4\1\5\uffff\1\1\3\uffff"+
            "\1\4\1\uffff\1\4\1\1\5\uffff\4\1\1\uffff\7\1\2\uffff\1\4\1\uffff"+
            "\1\1",
            "\5\1\1\4\2\1\2\4\5\1\1\uffff\1\1\1\uffff\4\1\5\uffff\1\1\3\uffff"+
            "\1\4\1\uffff\1\4\1\1\5\uffff\4\1\1\uffff\7\1\2\uffff\1\4\1\uffff"+
            "\1\1",
            "\1\1\1\uffff\1\52\1\uffff\1\1\25\uffff\1\1\1\uffff\1\1\2\uffff"+
            "\1\1\10\uffff\5\1\4\uffff\1\1\17\uffff\7\1",
            "\1\uffff",
            "\5\4\1\1\1\4\1\54\1\uffff\1\1\5\4\1\uffff\1\4\1\uffff\4\4\5"+
            "\uffff\1\4\3\uffff\1\1\1\uffff\1\1\1\4\1\uffff\1\1\3\uffff\4"+
            "\4\1\uffff\1\55\1\56\1\57\1\60\1\53\2\4\2\uffff\1\1\1\uffff"+
            "\1\4\15\uffff\7\1",
            "\5\4\1\1\1\4\1\54\1\uffff\1\1\5\4\1\uffff\1\4\1\uffff\4\4\5"+
            "\uffff\1\4\3\uffff\1\1\1\uffff\1\1\1\4\1\uffff\1\1\3\uffff\4"+
            "\4\1\uffff\1\55\1\56\1\57\1\60\1\53\2\4\2\uffff\1\1\1\uffff"+
            "\1\4\15\uffff\7\1",
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

    static final short[] DFA70_eot = DFA.unpackEncodedString(DFA70_eotS);
    static final short[] DFA70_eof = DFA.unpackEncodedString(DFA70_eofS);
    static final char[] DFA70_min = DFA.unpackEncodedStringToUnsignedChars(DFA70_minS);
    static final char[] DFA70_max = DFA.unpackEncodedStringToUnsignedChars(DFA70_maxS);
    static final short[] DFA70_accept = DFA.unpackEncodedString(DFA70_acceptS);
    static final short[] DFA70_special = DFA.unpackEncodedString(DFA70_specialS);
    static final short[][] DFA70_transition;

    static {
        int numStates = DFA70_transitionS.length;
        DFA70_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA70_transition[i] = DFA.unpackEncodedString(DFA70_transitionS[i]);
        }
    }

    class DFA70 extends DFA {

        public DFA70(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 70;
            this.eot = DFA70_eot;
            this.eof = DFA70_eof;
            this.min = DFA70_min;
            this.max = DFA70_max;
            this.accept = DFA70_accept;
            this.special = DFA70_special;
            this.transition = DFA70_transition;
        }
        public String getDescription() {
            return "()* loopback of 1363:3: ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )*";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA70_18 = input.LA(1);

                         
                        int index70_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred9()) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index70_18);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 70, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_71_in_opt_semicolon39 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit57 = new BitSet(new long[]{0x00000000000344C0L});
    public static final BitSet FOLLOW_statement_in_compilation_unit62 = new BitSet(new long[]{0x00000000000344C0L});
    public static final BitSet FOLLOW_EOF_in_compilation_unit67 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_package_statement_in_prolog96 = new BitSet(new long[]{0x00000003FD680012L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_prolog110 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_72_in_prolog112 = new BitSet(new long[]{0x00000003FD680002L});
    public static final BitSet FOLLOW_rule_attribute_in_prolog122 = new BitSet(new long[]{0x00000003FD681002L});
    public static final BitSet FOLLOW_COMMA_in_prolog145 = new BitSet(new long[]{0x00000003FD680000L});
    public static final BitSet FOLLOW_rule_attribute_in_prolog150 = new BitSet(new long[]{0x00000003FD681002L});
    public static final BitSet FOLLOW_PACKAGE_in_package_statement194 = new BitSet(new long[]{0x103EF0408147C5F0L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement198 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_opt_semicolon_in_package_statement200 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_statement296 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_import_name_in_import_statement319 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_opt_semicolon_in_import_statement322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_function_import_statement346 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FUNCTION_in_function_import_statement348 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement371 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_opt_semicolon_in_function_import_statement374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_name400 = new BitSet(new long[]{0x0000000000000202L,0x0000000000000200L});
    public static final BitSet FOLLOW_DOT_in_import_name412 = new BitSet(new long[]{0x103EF0408147C5F0L});
    public static final BitSet FOLLOW_identifier_in_import_name416 = new BitSet(new long[]{0x0000000000000202L,0x0000000000000200L});
    public static final BitSet FOLLOW_73_in_import_name440 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GLOBAL_in_global474 = new BitSet(new long[]{0x103EF0408147C5F0L});
    public static final BitSet FOLLOW_dotted_name_in_global485 = new BitSet(new long[]{0x103EF0408147C5F0L});
    public static final BitSet FOLLOW_identifier_in_global496 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_opt_semicolon_in_global498 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function523 = new BitSet(new long[]{0x103EF0408147C5F0L});
    public static final BitSet FOLLOW_dotted_name_in_function527 = new BitSet(new long[]{0x103EF0408147C5F0L});
    public static final BitSet FOLLOW_identifier_in_function532 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function541 = new BitSet(new long[]{0x103EF0408147E5F0L});
    public static final BitSet FOLLOW_dotted_name_in_function550 = new BitSet(new long[]{0x103EF0408147C5F0L});
    public static final BitSet FOLLOW_argument_in_function555 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_COMMA_in_function569 = new BitSet(new long[]{0x103EF0408147C5F0L});
    public static final BitSet FOLLOW_dotted_name_in_function573 = new BitSet(new long[]{0x103EF0408147C5F0L});
    public static final BitSet FOLLOW_argument_in_function578 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function602 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_curly_chunk_in_function608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_argument635 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument641 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument643 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_QUERY_in_query673 = new BitSet(new long[]{0x0000000000100100L});
    public static final BitSet FOLLOW_name_in_query677 = new BitSet(new long[]{0x0000078000008900L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_query687 = new BitSet(new long[]{0x0000000000002100L});
    public static final BitSet FOLLOW_qualified_id_in_query722 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_query727 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_COMMA_in_query748 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_qualified_id_in_query752 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_query757 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_query807 = new BitSet(new long[]{0x0000078000008900L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query836 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_END_in_query841 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_opt_semicolon_in_query843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_template871 = new BitSet(new long[]{0x0000000000100100L});
    public static final BitSet FOLLOW_name_in_template875 = new BitSet(new long[]{0x0000000000000100L,0x0000000000000080L});
    public static final BitSet FOLLOW_opt_semicolon_in_template877 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_template_slot_in_template892 = new BitSet(new long[]{0x0000000000008100L});
    public static final BitSet FOLLOW_END_in_template907 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_opt_semicolon_in_template909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualified_id_in_template_slot955 = new BitSet(new long[]{0x103EF0408147C5F0L});
    public static final BitSet FOLLOW_identifier_in_template_slot971 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_opt_semicolon_in_template_slot973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule1004 = new BitSet(new long[]{0x0000000000100100L});
    public static final BitSet FOLLOW_name_in_rule1008 = new BitSet(new long[]{0x10000003FD6C0010L});
    public static final BitSet FOLLOW_rule_attributes_in_rule1017 = new BitSet(new long[]{0x1000000000040000L});
    public static final BitSet FOLLOW_WHEN_in_rule1029 = new BitSet(new long[]{0x1000078000000900L,0x0000000000000100L});
    public static final BitSet FOLLOW_72_in_rule1031 = new BitSet(new long[]{0x1000078000000900L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule1042 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_rhs_chunk_in_rule1052 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_rule_attributes1072 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_72_in_rule_attributes1074 = new BitSet(new long[]{0x00000003FD680000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1082 = new BitSet(new long[]{0x00000003FD681002L});
    public static final BitSet FOLLOW_COMMA_in_rule_attributes1089 = new BitSet(new long[]{0x00000003FD680000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1094 = new BitSet(new long[]{0x00000003FD681002L});
    public static final BitSet FOLLOW_salience_in_rule_attribute1131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute1139 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute1148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute1157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute1166 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute1182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute1190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute1198 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_in_rule_attribute1206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_in_rule_attribute1214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_in_rule_attribute1221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EFFECTIVE_in_date_effective1247 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_STRING_in_date_effective1249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EXPIRES_in_date_expires1278 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_STRING_in_date_expires1280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENABLED_in_enabled1309 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_BOOL_in_enabled1311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_salience1344 = new BitSet(new long[]{0x0000000002000800L});
    public static final BitSet FOLLOW_INT_in_salience1355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_salience1370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NO_LOOP_in_no_loop1400 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1413 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AUTO_FOCUS_in_auto_focus1448 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTIVATION_GROUP_in_activation_group1497 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_STRING_in_activation_group1499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1527 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_STRING_in_ruleflow_group1529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AGENDA_GROUP_in_agenda_group1557 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DURATION_in_duration1587 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_INT_in_duration1589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIALECT_in_dialect1617 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_STRING_in_dialect1619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1651 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_BOOL_in_lock_on_active1664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1703 = new BitSet(new long[]{0x0000078000000902L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1740 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_or1765 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_OR_in_lhs_or1767 = new BitSet(new long[]{0x0000078000000900L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1778 = new BitSet(new long[]{0x0000078000002900L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_or1788 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1806 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_set_in_lhs_or1814 = new BitSet(new long[]{0x0000078000000900L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1830 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_and1861 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_AND_in_lhs_and1863 = new BitSet(new long[]{0x0000078000000900L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1874 = new BitSet(new long[]{0x0000078000002900L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_and1884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1902 = new BitSet(new long[]{0x0000003000000002L});
    public static final BitSet FOLLOW_set_in_lhs_and1910 = new BitSet(new long[]{0x0000078000000900L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1926 = new BitSet(new long[]{0x0000003000000002L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary1971 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary1989 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary2008 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary2027 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary2044 = new BitSet(new long[]{0x0000078000000900L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_unary2048 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary2050 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_pattern_source_in_lhs_unary2061 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_opt_semicolon_in_lhs_unary2073 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_pattern_source2100 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_FROM_in_pattern_source2112 = new BitSet(new long[]{0x103FF8408147C5F0L});
    public static final BitSet FOLLOW_accumulate_statement_in_pattern_source2168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collect_statement_in_pattern_source2191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_statement_in_pattern_source2215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_lhs_exist2258 = new BitSet(new long[]{0x0000000000000900L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist2278 = new BitSet(new long[]{0x0000078000000900L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist2282 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist2312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_exist2362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_lhs_not2414 = new BitSet(new long[]{0x0000000000000900L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not2427 = new BitSet(new long[]{0x0000078000000900L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not2431 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not2462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_not2499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_lhs_eval2545 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval2556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_lhs_forall2582 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall2584 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall2588 = new BitSet(new long[]{0x0000000000001100L});
    public static final BitSet FOLLOW_COMMA_in_lhs_forall2601 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall2606 = new BitSet(new long[]{0x0000000000003100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall2619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_pattern2652 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_pattern2660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_source_in_from_statement2687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_from_source2716 = new BitSet(new long[]{0x0000000000000A02L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source2744 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_expression_chain_in_from_source2757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_accumulate_statement2798 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement2808 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_pattern_source_in_accumulate_statement2812 = new BitSet(new long[]{0x0000100000001100L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2814 = new BitSet(new long[]{0x0000100000000100L});
    public static final BitSet FOLLOW_INIT_in_accumulate_statement2832 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2845 = new BitSet(new long[]{0x0000200000001000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2847 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ACTION_in_accumulate_statement2858 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2862 = new BitSet(new long[]{0x0000C00000001000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2864 = new BitSet(new long[]{0x0000C00000000000L});
    public static final BitSet FOLLOW_REVERSE_in_accumulate_statement2877 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2881 = new BitSet(new long[]{0x0000800000001000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2883 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RESULT_in_accumulate_statement2900 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2904 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_ID_in_accumulate_statement2930 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2934 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement2951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_expression_chain2980 = new BitSet(new long[]{0x103EF0408147C5F0L});
    public static final BitSet FOLLOW_identifier_in_expression_chain2984 = new BitSet(new long[]{0x0400000000000A02L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain3015 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain3048 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain3063 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_collect_statement3114 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement3124 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_pattern_source_in_collect_statement3128 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement3130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding3162 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_72_in_fact_binding3164 = new BitSet(new long[]{0x0000000000000900L});
    public static final BitSet FOLLOW_fact_in_fact_binding3178 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_binding3194 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_fact_in_fact_binding3198 = new BitSet(new long[]{0x0000000C00002000L});
    public static final BitSet FOLLOW_set_in_fact_binding3211 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_fact_in_fact_binding3223 = new BitSet(new long[]{0x0000000C00002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_binding3241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualified_id_in_fact3296 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact3306 = new BitSet(new long[]{0x103EF2408147EDF0L});
    public static final BitSet FOLLOW_constraints_in_fact3320 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact3331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EOF_in_fact3340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints3358 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_COMMA_in_constraints3365 = new BitSet(new long[]{0x103EF2408147CDF0L});
    public static final BitSet FOLLOW_constraint_in_constraints3374 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_or_constr_in_constraint3407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3430 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_constr3438 = new BitSet(new long[]{0x103EF2408147CDF0L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3447 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3479 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_constr3487 = new BitSet(new long[]{0x103EF2408147CDF0L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3496 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_field_constraint_in_unary_constr3524 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_unary_constr3532 = new BitSet(new long[]{0x103EF2408147CDF0L});
    public static final BitSet FOLLOW_or_constr_in_unary_constr3534 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_unary_constr3537 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_unary_constr3543 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_predicate_in_unary_constr3545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_field_constraint3575 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_72_in_field_constraint3577 = new BitSet(new long[]{0x103EF0408147C5F0L});
    public static final BitSet FOLLOW_accessor_path_in_field_constraint3598 = new BitSet(new long[]{0x003E010000000802L,0x000000000001FC00L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint3626 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_field_constraint3646 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_predicate_in_field_constraint3648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective3691 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_restr_connective3715 = new BitSet(new long[]{0x003E010000000800L,0x000000000001F800L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective3726 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective3758 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_restr_connective3779 = new BitSet(new long[]{0x003E010000000800L,0x000000000001F800L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective3790 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_compound_operator_in_constraint_expression3827 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_operator_in_constraint_expression3834 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_constraint_expression3841 = new BitSet(new long[]{0x003E010000000800L,0x000000000001F800L});
    public static final BitSet FOLLOW_or_restr_connective_in_constraint_expression3850 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_constraint_expression3856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_simple_operator3887 = new BitSet(new long[]{0x10FEF04083D7CDF0L});
    public static final BitSet FOLLOW_76_in_simple_operator3895 = new BitSet(new long[]{0x10FEF04083D7CDF0L});
    public static final BitSet FOLLOW_77_in_simple_operator3903 = new BitSet(new long[]{0x10FEF04083D7CDF0L});
    public static final BitSet FOLLOW_78_in_simple_operator3911 = new BitSet(new long[]{0x10FEF04083D7CDF0L});
    public static final BitSet FOLLOW_79_in_simple_operator3919 = new BitSet(new long[]{0x10FEF04083D7CDF0L});
    public static final BitSet FOLLOW_80_in_simple_operator3927 = new BitSet(new long[]{0x10FEF04083D7CDF0L});
    public static final BitSet FOLLOW_CONTAINS_in_simple_operator3935 = new BitSet(new long[]{0x10FEF04083D7CDF0L});
    public static final BitSet FOLLOW_NOT_in_simple_operator3943 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_CONTAINS_in_simple_operator3947 = new BitSet(new long[]{0x10FEF04083D7CDF0L});
    public static final BitSet FOLLOW_EXCLUDES_in_simple_operator3955 = new BitSet(new long[]{0x10FEF04083D7CDF0L});
    public static final BitSet FOLLOW_MATCHES_in_simple_operator3963 = new BitSet(new long[]{0x10FEF04083D7CDF0L});
    public static final BitSet FOLLOW_NOT_in_simple_operator3971 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_MATCHES_in_simple_operator3975 = new BitSet(new long[]{0x10FEF04083D7CDF0L});
    public static final BitSet FOLLOW_MEMBEROF_in_simple_operator3983 = new BitSet(new long[]{0x10FEF04083D7CDF0L});
    public static final BitSet FOLLOW_NOT_in_simple_operator3991 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_MEMBEROF_in_simple_operator3995 = new BitSet(new long[]{0x10FEF04083D7CDF0L});
    public static final BitSet FOLLOW_expression_value_in_simple_operator4009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_compound_operator4039 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_NOT_in_compound_operator4051 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_IN_in_compound_operator4053 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_compound_operator4068 = new BitSet(new long[]{0x10FEF04083D7CDF0L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator4072 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_COMMA_in_compound_operator4079 = new BitSet(new long[]{0x10FEF04083D7CDF0L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator4083 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_compound_operator4092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accessor_path_in_expression_value4126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_expression_value4146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_value4160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint4203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint4214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint4227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint4238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal_constraint4250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate4288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk4306 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000001FFFFL});
    public static final BitSet FOLLOW_set_in_curly_chunk4310 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000001FFFFL});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk4319 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000001FFFFL});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk4324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk4338 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000001FFFFL});
    public static final BitSet FOLLOW_set_in_paren_chunk4342 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000001FFFFL});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk4351 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000001FFFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk4356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk4369 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000001FFFFL});
    public static final BitSet FOLLOW_set_in_square_chunk4373 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000001FFFFL});
    public static final BitSet FOLLOW_square_chunk_in_square_chunk4382 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000001FFFFL});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk4387 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_qualified_id4400 = new BitSet(new long[]{0x0400000000000202L});
    public static final BitSet FOLLOW_DOT_in_qualified_id4404 = new BitSet(new long[]{0x103EF0408147C5F0L});
    public static final BitSet FOLLOW_identifier_in_qualified_id4406 = new BitSet(new long[]{0x0400000000000202L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_qualified_id4413 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_qualified_id4415 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_identifier_in_dotted_name4430 = new BitSet(new long[]{0x0400000000000202L});
    public static final BitSet FOLLOW_DOT_in_dotted_name4434 = new BitSet(new long[]{0x103EF0408147C5F0L});
    public static final BitSet FOLLOW_identifier_in_dotted_name4436 = new BitSet(new long[]{0x0400000000000202L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dotted_name4443 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dotted_name4445 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path4461 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_DOT_in_accessor_path4465 = new BitSet(new long[]{0x103EF0408147C5F0L});
    public static final BitSet FOLLOW_accessor_element_in_accessor_path4467 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_identifier_in_accessor_element4485 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_square_chunk_in_accessor_element4487 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk4504 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000001FFFFL});
    public static final BitSet FOLLOW_set_in_rhs_chunk4512 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000001FFFFL});
    public static final BitSet FOLLOW_END_in_rhs_chunk4536 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_opt_semicolon_in_rhs_chunk4538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_name4572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_name4580 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_identifier0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_synpred11963 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_synpred21981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_synpred32000 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_synpred42019 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred52038 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred63007 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred73040 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred83715 = new BitSet(new long[]{0x003E010000000800L,0x000000000001F800L});
    public static final BitSet FOLLOW_and_restr_connective_in_synpred83726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred93779 = new BitSet(new long[]{0x003E010000000800L,0x000000000001F800L});
    public static final BitSet FOLLOW_constraint_expression_in_synpred93790 = new BitSet(new long[]{0x0000000000000002L});

}